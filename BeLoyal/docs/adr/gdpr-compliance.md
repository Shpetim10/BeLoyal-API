# Section 3.3 — Right to Erasure vs the Append-Only Ledger (Rev 3)

**Status:** Resolved (supersedes Open Question #1; supersedes Rev 1 and Rev 2)
**Relates to:** P4 (immutable events, derived state), P10 (privacy is structural), REQ‑45, REQ‑39
**Applies from:** v0.9 — this is a schema-defining decision and blocks the UUID migration

*Rev 3 assumes approval to change the database structure and to migrate identity IDs to UUIDv7. It replaces Rev 2's anonymize-in-place approach with a design in which the identity row is genuinely **deleted**.*

---

## 3.3.0 Summary of the change

Rev 2 kept `User` forever as an anonymized stub because business memberships, audit trails and the ledger all pointed at it. That works, but it leaves a permanent row per erased person, needs tombstoned email/username values to survive unique constraints, and depends on developers remembering to null every PII field — a checklist that rots.

Rev 3 removes the constraint that forced it. **Nothing in the system points at the identity row.** Everything points at a permanent, PII-free `subject`. The identity row then becomes a leaf that can be hard-deleted, and `ON DELETE CASCADE` removes every dependent PII table automatically.

The design goal is that **compliance is enforced by referential integrity rather than by process**. A developer who adds a table containing an email address either attaches it to the identity row, in which case erasure already covers it, or attaches it to the subject, in which case a schema test fails the build.

---

## 3.3.1 Is the requirement real?

Yes, and the current implementation is inverted.

**Legal footing.** Albania's Law No. 124/2024 "On Personal Data Protection" passed 19 December 2024 and entered into force 17 January 2025, repealing Law No. 9887/2008 and aligning Albanian law with GDPR and Directive (EU) 2016/680. Erasure is an express right under Art. 15(2); where data was collected in the context of online provision of goods or services, erasure must complete **within 30 days of receipt**. Fines reach 2,000,000,000 ALL or 4% of worldwide annual turnover, whichever is higher.

**Internal commitment.** REQ‑45 is scoped to **v1.0**: erasure removes PII while ledger aggregates survive k‑anonymized; export completes under 24h.

**Current behaviour.** `CustomerAccountDeletionServiceImpl` deletes the `CustomerProfile` and the loyalty history while retaining the `User` row — name, username, email, password hash, phone, profile-image reference, login/security history. It does not revoke authentication tokens and does not delete the stored profile-image object.

| | Today | Required |
|---|---|---|
| Points transactions, buckets, consumptions | **hard deleted** | never deleted |
| `User` name, email, phone, password hash | **retained** | deleted |
| Refresh / verify / reset / invite tokens | **retained** | deleted |
| Profile-image object in storage | **orphaned** | deleted |
| `CustomerProfile` | deleted | correct |

The system destroys the one class of data with a lawful retention basis and keeps the one class with none. This is a reversal, not a refactor.

**Retention basis.** Art. 17(3) exceptions for legal obligations and legal claims cover the numeric ledger. Two binding cautions: immutability is not a retention justification (P4 governs *updates*, not *duration* — storage limitation still applies); and the period is a legal input recorded as `RETENTION_LEDGER_YEARS`, to be confirmed by Albanian counsel. No part of this design depends on its value, only the timer does.

---

## 3.3.2 Decision

> **Every entity is split into a permanent, PII-free `subject` and a deletable identity row.** All foreign keys in the system — ledger, memberships, coupons, audit, analytics — reference the subject. No table outside the identity cluster may reference an identity row, and no table outside the identity cluster may contain personal data.
>
> **Erasure is a hard `DELETE` of the identity row.** `ON DELETE CASCADE` removes every dependent PII table in one transaction. The subject remains as an anonymous anchor so that ledger history, business records and audit trails stay referentially valid.
>
> **Three erasure paths are defined:** customer, staff/platform user, and business. They differ in scope and in which retention exceptions apply, and must not share one endpoint.
>
> On erasure Ritema (a) appends a forfeiture event so no residual Beats liability survives, (b) deletes the identity row and everything cascading from it, (c) deletes all stored objects, (d) fans out to every derived and downstream copy under a tracked completion watermark, and (e) records a tombstone replayed on any restore.
>
> **Beat events are never updated and never deleted.** The legacy hard-delete of points transactions is forbidden from the first release of the new ledger.
>
> **Erase the person, not the accounting fact — and do not assume the accounting fact may be kept forever.**

---

## 3.3.3 Core schema

```sql
-- ============================================================
-- TIER 1: PERMANENT, PII-FREE. Never deleted. FK target for
-- everything in the system.
-- ============================================================
CREATE TABLE subject (
    subject_id    UUID PRIMARY KEY,              -- UUIDv7
    subject_type  TEXT NOT NULL
                  CHECK (subject_type IN ('PERSON','ORGANISATION')),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    erased_at     TIMESTAMPTZ,                   -- set when identity deleted
    CONSTRAINT subject_no_pii CHECK (true)       -- documented invariant; see 3.3.8
);

-- ============================================================
-- TIER 2: IDENTITY. All PII and credentials. Hard-deletable leaf.
-- Nothing outside this cluster may reference it.
-- ============================================================
CREATE TABLE person_identity (
    subject_id         UUID PRIMARY KEY
                       REFERENCES subject(subject_id) ON DELETE RESTRICT,
    email              CITEXT NOT NULL UNIQUE,
    phone              TEXT UNIQUE,
    full_name          TEXT,
    display_name       TEXT,
    password_hash      TEXT,
    profile_image_key  TEXT,          -- object storage key, random UUID
    birth_date         DATE,
    gender             TEXT,
    locale             TEXT,
    city               TEXT,
    legacy_user_id     BIGINT UNIQUE, -- migration only; DROPPED at cutover (3.3.9)
    created_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Every PII child table hangs off the identity row and cascades.
CREATE TABLE refresh_token (
    token_id     UUID PRIMARY KEY,
    subject_id   UUID NOT NULL
                 REFERENCES person_identity(subject_id) ON DELETE CASCADE,
    token_hash   BYTEA NOT NULL,
    device_id    TEXT,
    user_agent   TEXT,
    ip_address   INET,
    issued_at    TIMESTAMPTZ NOT NULL,
    expires_at   TIMESTAMPTZ NOT NULL
);

CREATE TABLE verification_token (   -- email verify / password reset
    token_id     UUID PRIMARY KEY,
    subject_id   UUID NOT NULL
                 REFERENCES person_identity(subject_id) ON DELETE CASCADE,
    purpose      TEXT NOT NULL,
    token_hash   BYTEA NOT NULL,
    expires_at   TIMESTAMPTZ NOT NULL
);

CREATE TABLE notification_channel (   -- FCM/APNs tokens, email opt-ins
    channel_id   UUID PRIMARY KEY,
    subject_id   UUID NOT NULL
                 REFERENCES person_identity(subject_id) ON DELETE CASCADE,
    kind         TEXT NOT NULL,
    address      TEXT NOT NULL,
    consent_at   TIMESTAMPTZ
);

CREATE TABLE login_event (            -- IP/device bearing; no retention basis
    id           UUID PRIMARY KEY,
    subject_id   UUID NOT NULL
                 REFERENCES person_identity(subject_id) ON DELETE CASCADE,
    ip_address   INET,
    user_agent   TEXT,
    occurred_at  TIMESTAMPTZ NOT NULL
);

-- ============================================================
-- TIER 3: OPERATIONAL. References subject only. Survives erasure.
-- ============================================================
CREATE TABLE beat_events (
    event_id         UUID PRIMARY KEY,           -- UUIDv7
    subject_id       UUID NOT NULL REFERENCES subject(subject_id),
    business_id      UUID NOT NULL REFERENCES business(business_id),
    event_type       TEXT NOT NULL,
    available_delta  BIGINT NOT NULL,
    status_delta     BIGINT NOT NULL CHECK (status_delta >= 0),
    issuer           TEXT NOT NULL,              -- BUSINESS | PLATFORM
    lot_id           UUID,
    source_ref       JSONB,
    idempotency_key  TEXT NOT NULL UNIQUE,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE business_membership (
    membership_id  UUID PRIMARY KEY,
    subject_id     UUID NOT NULL REFERENCES subject(subject_id),
    business_id    UUID NOT NULL REFERENCES business(business_id),
    role           TEXT NOT NULL,
    status         TEXT NOT NULL,
    joined_at      TIMESTAMPTZ NOT NULL,
    ended_at       TIMESTAMPTZ
);

CREATE TABLE terms_acceptance (      -- legal-claims basis; no PII
    id             UUID PRIMARY KEY,
    subject_id     UUID NOT NULL REFERENCES subject(subject_id),
    document       TEXT NOT NULL,
    version        TEXT NOT NULL,
    accepted_at    TIMESTAMPTZ NOT NULL
);
```

The rule that makes this work is stated once and enforced mechanically:

> **A table either references `person_identity`/`business_identity` and may contain PII, or it references `subject` and may not. Never both.**

### Why the identity row can now be deleted

- `email` and `phone` uniqueness live on the identity row, so deletion frees them naturally. No tombstone values, and the person can re-register with the same address — which they are entitled to do. This removes the most fragile part of Rev 2.
- Every PII table cascades. Adding a new one and forgetting to update the erasure service is not possible, because the cascade is declared at creation.
- `subject.erased_at` is set in the same transaction, giving downstream systems a queryable signal.

---

## 3.3.4 Business and staff

### Business

A business is usually a legal person, and data protection law protects natural persons — but the exceptions matter and are common in your market. A sole trader's business name may *be* their name; the contact phone is often a personal mobile; the owner's tax ID is personal data. Treat business identity as PII by default rather than reasoning case by case.

```sql
CREATE TABLE business (                -- permanent operational anchor
    business_id   UUID PRIMARY KEY,    -- UUIDv7
    subject_id    UUID NOT NULL UNIQUE REFERENCES subject(subject_id),
    status        TEXT NOT NULL,       -- ACTIVE | SUSPENDED | CLOSED
    closed_at     TIMESTAMPTZ,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE business_identity (       -- deletable, retention-gated
    business_id     UUID PRIMARY KEY
                    REFERENCES business(business_id) ON DELETE RESTRICT,
    legal_name      TEXT NOT NULL,
    trading_name    TEXT,
    owner_name      TEXT,
    contact_email   CITEXT,
    contact_phone   TEXT,
    address         TEXT,
    tax_id          TEXT,
    iban            TEXT,
    is_sole_trader  BOOLEAN NOT NULL DEFAULT false
);
```

**Business closure is not business erasure.** Two separate steps, and conflating them is what the legacy code does:

1. **Closure** — `status = CLOSED`. The ledger, liability records and every customer's history at that business survive untouched. Trading name may be retained for customer-facing history ("closed business") only where necessary.
2. **Identity deletion** — gated on `RETENTION_BILLING_YEARS`, because Ritema has its **own** legal obligation to retain merchant billing identity for its invoices and tax filings. That obligation is Ritema's, not the merchant's, and it survives the merchant's erasure request under Art. 17(3)(b). Where the merchant is a sole trader, only elements not appearing on issued invoices are erasable on request before the period expires.

**Closure has a customer-facing consequence the ADR must not leave silent:** outstanding Beats at a closing business are a live liability owed to real people. Closure therefore requires an explicit settlement event before `status = CLOSED` — one of expiry after a notice window, conversion to Platform Beats at Ritema's cost, or cash settlement. The policy choice is a product decision (follow-up #7), but the *event* is mandatory, so that no customer's balance silently becomes unredeemable and no liability is orphaned.

### Staff

`business_membership` references `subject`, so it carries no PII and survives staff erasure. A business retains "membership `m-8f3…` performed this adjustment on 4 March" without retaining who that was.

Where the business has a genuine need to re-identify a staff member during a dispute — a contested manual adjustment, a suspected internal fraud — that need is bounded and should not block erasure. Handle it with **crypto-shredded archival** rather than by retaining PII:

```sql
CREATE TABLE identity_archive (
    subject_id     UUID PRIMARY KEY REFERENCES subject(subject_id),
    ciphertext     BYTEA NOT NULL,    -- AEAD-encrypted minimal identity snapshot
    key_ref        TEXT NOT NULL,     -- per-subject KMS key
    sealed_at      TIMESTAMPTZ NOT NULL,
    shred_after    TIMESTAMPTZ NOT NULL
);
```

Written only where a live legal basis exists, containing the minimum needed for the claim, opened only under two-person authorization with an audit entry, and the key destroyed at `shred_after`. After shredding, the ciphertext is unreadable forever and the subject is anonymous. Do not create this record by default; an archive written for every erasure is just retention with extra steps.

**Staff erasure is scoped.** A staff-only user who erases has their platform identity deleted; the employment relationship itself sits between them and their employer, and the business may have its own obligations Ritema is not party to. Where a person holds both roles, the customer-side data is erased and any staff-side archival applies separately — with the scoping explained in the confirmation, not left implicit.

---

## 3.3.5 Supporting tables

```sql
-- Erasure request tracking; drives the 30-day clock and the alert
CREATE TABLE erasure_request (
    request_id    UUID PRIMARY KEY,
    subject_id    UUID NOT NULL REFERENCES subject(subject_id),
    scope         TEXT NOT NULL,   -- CUSTOMER | PLATFORM_USER | BUSINESS
    received_at   TIMESTAMPTZ NOT NULL,
    due_at        TIMESTAMPTZ NOT NULL,   -- received_at + 30 days
    state         TEXT NOT NULL,   -- RECEIVED|VERIFIED|HELD|RUNNING|COMPLETE|REFUSED
    completed_at  TIMESTAMPTZ,
    refusal_basis TEXT
);

-- Erasure must be deferrable under Art. 17(3)(e); a hold must be
-- justified, time-boxed, and disclosed to the data subject.
CREATE TABLE legal_hold (
    hold_id      UUID PRIMARY KEY,
    subject_id   UUID NOT NULL REFERENCES subject(subject_id),
    reason       TEXT NOT NULL,
    opened_by    UUID NOT NULL REFERENCES subject(subject_id),
    opened_at    TIMESTAMPTZ NOT NULL,
    review_at    TIMESTAMPTZ NOT NULL,
    released_at  TIMESTAMPTZ
);

-- Fraud prevention survives deletion without retaining PII.
-- Keyed HMAC with a pepper held in KMS: checkable, not reversible.
CREATE TABLE identity_block (
    block_id     UUID PRIMARY KEY,
    email_hmac   BYTEA,
    phone_hmac   BYTEA,
    reason       TEXT NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL,
    expires_at   TIMESTAMPTZ NOT NULL   -- mandatory; no indefinite blocks
);

-- Replayed on every restore, before the restored system serves traffic
CREATE TABLE erasure_tombstone (
    subject_id     UUID PRIMARY KEY,
    completed_at   TIMESTAMPTZ NOT NULL,
    scope          TEXT NOT NULL,
    legacy_user_id BIGINT        -- needed to scrub pre-migration backups
);

CREATE TABLE erasure_target (
    subject_id    UUID NOT NULL,
    target        TEXT NOT NULL,   -- CDN|FCM|SES|OLAP|LOGS|SEARCH|SUPPORT|...
    completed_at  TIMESTAMPTZ,
    PRIMARY KEY (subject_id, target)
);
```

`identity_block` deserves a note, because full deletion creates a real gap: without it, a fraudster earns, erases, and re-registers on the same email in a loop, and you have destroyed your own evidence. An HMAC of the identifier under a server-side pepper lets you check a new signup against past abuse without storing anything reversible. It needs its own legal basis (legitimate interest, fraud prevention), a mandatory expiry, and a documented appeal route — an indefinite block list is itself a compliance problem.

---

## 3.3.6 The three erasure paths

```
CUSTOMER
  1. VERIFY      authenticate; write erasure_request; 30-day clock starts
  2. HOLD CHECK  open legal_hold? → state=HELD, inform subject of the
                 restriction and the reason; do not silently stall
  3. EXPORT      offer the DSAR bundle before destruction (REQ-45)
  4. FORFEIT     ADJUSTMENT_MINUS(ACCOUNT_CLOSED_ERASURE) per business
                 → Available Beats = 0 ; Status Beats untouched
  5. REVOKE      DELETE FROM refresh_token / verification_token
                 (explicit and first — before any cascade races a login)
  6. OBJECTS     delete profile image, CDN Signature/Rewind renders
  7. DELETE      DELETE FROM person_identity WHERE subject_id = ?
                 → cascades: tokens, channels, login_event, prefs
                 → sets subject.erased_at in the same transaction
  8. FAN OUT     outbox → per-target jobs, watermark each (3.3.7)
  9. TOMBSTONE   erasure_tombstone on full completion
 10. CONFIRM     notify within 30 days via the channel captured at step 1

PLATFORM USER / STAFF
  as above, plus:
  2b. SCOPE      active business_membership? → record scope and the basis
  6b. ARCHIVE    seal identity_archive only where a live claim requires it
  7b.            membership rows survive (subject-referencing, no PII)

BUSINESS
  1. SETTLE      mandatory settlement event for outstanding Beats
                 (expiry after notice | convert to Platform | cash out)
  2. CLOSE       business.status = CLOSED ; ledger untouched
  3. STAFF       each staff member follows the path above individually
  4. RETAIN      business_identity held for RETENTION_BILLING_YEARS
                 (Ritema's own invoicing/tax obligation, Art. 17(3)(b))
  5. DELETE      after the period: DELETE FROM business_identity
```

Two sequencing traps, both silent when got wrong: token revocation must precede identity deletion, or a live refresh token mints an access token against a half-erased account; and the confirmation cannot be sent through a contact detail step 7 has already deleted, so capture a one-time delivery channel at step 1.

---

## 3.3.7 Rules that the schema alone does not enforce

**D1 — UUIDv7 everywhere, never derived.** All identifiers are UUIDv7: time-ordered for index locality on `beat_events`, non-enumerable, non-correlating. No identifier may be derived from an email hash, phone number or any other re-linkable value.

**D2 — `idempotency_key` must be opaque.** A scanner building `phone + timestamp` writes PII into a unique index that survives deletion. UUIDs or opaque hashes only, validated at the API boundary.

**D3 — `source_ref` carries references only.** Whitelisted keys (`scan_id`, `order_id`, `refund_id`, `campaign_id`, `gift_id`, `reason_code`), UUID- or enum-shaped values, enforced by `CHECK` plus a CI lint at every write site. Legacy transaction rows carry human-readable descriptions; the migration must not carry them into `beat_events`.

**D4 — Object storage keys are random UUIDs.** Never `email.jpg`, never `userId_photo.png`. The key is stored on the identity row and deleted with it, and the object deletion is an outbox job that must acknowledge.

**D5 — Logs and traces carry `subject_id` only.** No email, phone or name in structured logs, OTel attributes or error reports. Enforced by a logging lint and a Sentry scrubber. This is the most commonly missed surface, because logs are outside the schema and therefore outside the cascade.

**D6 — Forfeit before delete.** Deleting identity while Available Beats remain creates a permanent liability with no claimant and corrupts business-liability reporting. Status Beats are untouched; `CHECK (status_delta >= 0)` holds through erasure as through every other path.

**D7 — Erasure fans out through the outbox** with a per-target watermark: `balances`, read replicas, reporting/ETL partitions, OLAP (v5+), Glow cadence models, CDN renders, FCM/APNs, SES/SendGrid contacts and suppression lists, search index, structured logs and traces, support tickets, admin console history. Erasure is not complete until every target acknowledges.

**D8 — Tombstones make restores safe.** Encrypted backups cannot be rewritten. Tombstone replay is a mandatory step of any restore, before the restored system serves traffic. Backup retention capped at `BACKUP_RETENTION_DAYS` (target 35). `erasure_tombstone.legacy_user_id` exists specifically so that pre-migration backups can be scrubbed on restore.

**D9 — Post-erasure ledger rows are reachable only in aggregate.** Deleting identity does not anonymize if the content re-identifies: a stream of timestamped scans at one café in Tirana is a behavioural fingerprint, and the Signature is precisely a rendering of that pattern. Anonymized history is queryable only through the policy-enforcing gateway above the k‑anonymity floor (REQ‑39). No row-level read path to erased subjects exists for any dashboard, export or analyst.

**D10 — Legacy hard-delete of value history is forbidden** for the new ledger, on both customer and business deletion.

---

## 3.3.8 Tests that enforce the invariant

The design's whole value is that violations are mechanically detectable. These belong in the brand-law suite and must fail the build, not a review.

| Law | Assertion |
|---|---|
| Tier separation | Schema test walks `information_schema`: every table FK-ing `subject` has no column matching the PII name/type patterns; every table with such a column FKs an identity row with `ON DELETE CASCADE`; no table references both |
| Cascade completeness | Property test: create a user, exercise every write path in the app, `DELETE FROM person_identity`, assert zero rows remain in every table under the identity cluster |
| Ledger survives | Same test asserts every `beat_events` row for the subject still exists and balances reconcile |
| No orphaned objects | Storage and CDN audit finds nothing for an erased subject |
| Credentials dead | Captured refresh token returns 401 immediately after erasure |
| Email reusable | The erased address registers successfully as a new subject with a new `subject_id` |
| No liability orphaned | Available Beats = 0; business-liability report unchanged by the erasure |
| Status preserved | Status Beats byte-identical before and after |
| Business closure settles | Closing a business with outstanding Beats without a settlement event is rejected |
| Logs clean | Log/trace scan for email, phone and name patterns over a full test run returns nothing |
| Restores stay erased | Quarterly restore drill: tombstones replayed, erased subject absent |
| Aggregate-only | Every query route to anonymized history rejected below the k floor |
| No indefinite blocks | `identity_block` rows all have `expires_at` |
| Deadline | Alert on any `erasure_request` open past 20 days |

---

## 3.3.9 Migration

Order matters; steps 5 and 7 are the irreversible ones.

1. **Flyway baseline.** Convert Liquibase changelogs to a Flyway baseline before any structural work, so the new schema is versioned in the target tool from its first migration.
2. **UUIDv7 generation.** Postgres 18 provides `uuidv7()` natively; on 16/17 generate application-side in Java and pass explicitly. Do not use v4 for `beat_events` — the index locality of v7 matters at scan volume.
3. **Create tiers.** Add `subject`, `person_identity`, `business`, `business_identity` alongside the existing tables. Backfill one `subject` row per existing `User`, with `legacy_user_id` recorded **on `person_identity`, never on `subject`** — putting it on the subject would preserve a link to old backups that survives erasure.
4. **Add UUID columns** to every operational table; backfill; dual-write.
5. **Switch foreign keys** to `subject_id` / `business_id`. This is the point at which the identity row becomes a leaf.
6. **Cut over reads**, verify, run the cascade property test against production-shaped data.
7. **Drop legacy integer columns**, including `person_identity.legacy_user_id` once no pre-migration backup remains within `BACKUP_RETENTION_DAYS`. Until then it is required for tombstone scrubbing on restore.
8. **Rewrite `CustomerAccountDeletionServiceImpl`** as the three paths in 3.3.6, and stop hard-deleting transactions.

Keep the new ledger out of the legacy tables entirely; `beat_events` is created directly in target shape and never carries a sequential id.

---

## 3.3.10 Follow-ups

| # | Item | Owner | Needed by |
|---|---|---|---|
| 1 | Confirm `RETENTION_LEDGER_YEARS` and `RETENTION_BILLING_YEARS` under Albanian tax and accounting law | Legal | before v1.0 |
| 2 | Controller/processor mapping for staff data — Ritema vs the employing business | Legal | v1.0 |
| 3 | DSAR export (<24h) spanning identity, ledger, coupons, memberships | Backend | v1.0 |
| 4 | Restore runbook with mandatory tombstone replay | Platform | v1.0 |
| 5 | `identity_block` legal basis, expiry policy and appeal route | Legal + Backend | v1.0 |
| 6 | Two-person authorization flow for `identity_archive` unsealing | Backend | v1.0 |
| 7 | Settlement policy for Beats at a closing business (expire / convert / cash) | Product | v1.0 |
| 8 | Gift escrow and v3.5 rev-share: erasing one party must not corrupt the counterparty's record | Backend | v3.5 |
| 9 | Records of processing and retention schedule document (Art. 30 equivalent) | Legal | v1.0 |

---

*Resolves ADR Open Question #1. Questions #2 (Status Beats on `PLATFORM_GRANT` and manual adjustments), #3 (is account merge near-term) and #4 (merged-Beats expiry) remain open — product decisions that do not block schema freeze. This one did, because it determines the primary key of every table in the system.*

*Not legal advice. Items 1, 2, 5 and 9 require confirmation by qualified Albanian counsel.*