# ADR 0003: Mapping the legacy points model onto the four-balance Beats ledger

- Status: DRAFT — all sections complete, all open questions resolved, pending your approval
- Date: 2026-08-27
- Scope: `docs/audit/legacy-audit.md` section 8 (findings 60-67)
- Related: `docs/adr/gdpr-compliance.md` (resolves Open Question #1 below; its schema corroborates Sections 4 and 6)

## 1. What the legacy model actually represents

### Account shape

`LoyaltyAccount` (`src/main/java/com/shabanaj/beloyal/model/Entity/LoyaltyAccount.java:23-84`) is unique per
`(customer_profile_id, business_id)` pair
(`src/main/resources/db/changelog/changes/001-baseline.xml:173-194`, unique constraint
`uk_loyalty_account_unique_profile_and_business`). There is no platform-wide or cross-business account. It carries
four numeric fields plus an unused optimistic-lock version column:

| Field | Meaning today | Ever decremented? |
|---|---|---|
| `available_points` | Spendable balance | Yes — by `spend()` and `expire()` |
| `lifetime_earned` | Cumulative points ever credited by `add()` | No — never decremented anywhere in the codebase |
| `lifetime_redeemed` | Cumulative points ever debited by `spend()` | No — never decremented anywhere in the codebase |
| `lifetime_expired` | Cumulative points ever debited by `expire()` | No — never decremented anywhere in the codebase |

"Lifetime points" is therefore not one concept — it is **three independent, one-directional gross counters**, each fed
by exactly one operation. Their monotonicity is not protected by any invariant: there is no `CHECK` constraint and no
business rule stating any of them must not decrease. It holds only because no code path ever attempts to decrease
them, which in turn is because **no refund, reversal, chargeback, or account-merge feature exists anywhere in this
codebase** (confirmed by exhaustive grep across `src/main/java` — zero matches for refund/chargeback logic, zero
matches for account-merge logic). This is a fact about what was never built, not a fact about what the system
guarantees.

The three mutator methods on `LoyaltyAccount` (`LoyaltyAccount.java:68-83`) are each called from exactly one place in
the entire codebase (confirmed exhaustively by grep for `loyaltyAccount.(add|spend|expire)(`):

- `add(points)` — `available_points += points; lifetime_earned += points`. Called only from
  `EarnPointsGuestsCalculatorServiceImpl.java:110`, inside
  `distributeAndPersistPointsTransactionsAndPointsBuckets`, under a pessimistic write lock obtained via
  `LoyaltyAccountServiceImpl.findWithLockOrCreate`.
- `spend(points)` — `available_points -= points; lifetime_redeemed += points`. Called only from
  `CouponRedemptionServiceImpl.java:87`, under a pessimistic lock via
  `LoyaltyAccountRepository.findWithLockByCustomerProfileIdAndBusinessId`.
- `expire(points)` — `available_points -= points; lifetime_expired += points`. Called only from
  `PointsBucketServiceImpl.java:73`, inside `expireSingleBucket`, with **no lock at all** — the bucket is loaded via
  a plain `pointsBucketRepository.findById(bucketId)` with no `@Lock` annotation
  (`PointsBucketServiceImpl.java:64-96`). This is audit findings #2 and #66: two scheduler instances racing this
  method can both read the same `ACTIVE` bucket before either write is visible, and double-debit
  `available_points`.

### Bucket layer (FIFO earning lots)

Each earn creates exactly one `PointsBucket` (`model/Entity/PointsBucket.java:23-61`), 1:1 with the `PointsTransaction`
that created it via a unique constraint on `(loyalty_account_id, source_transaction_id)`. `expires_at` is computed
once at earn time from `LoyaltySettings.expiryType`/`monthsToExpire`
(`EarnPointsGuestsCalculatorServiceImpl.java:131-146`) and then frozen — it is never recomputed. If
`ExpiryType.NO_EXPIRY` is configured, `expires_at` stays `null` and the bucket never expires.

Spend consumes buckets **soonest-expiring-first**, not oldest-first — `ORDER BY ... expiresAt ASC` with nulls sorted
last (`PointsBucketRepository.java:35-41`), taken under `PESSIMISTIC_WRITE`. Each unit consumed is journaled as a
`PointsBucketConsumption` row (`model/Entity/PointsBucketConsumption.java:19-39`) linking the consuming
`PointsTransaction` to the specific bucket and the exact amount drawn from it
(`PointsBucketServiceImpl.spend`, lines 100-130). This consumption record is the only place in the legacy schema
where a value movement is traceable back to which specific earn-lot it drew down — it is the closest thing to a
real ledger relationship that exists today.

Expiry does not append a new state — it **mutates the bucket in place**: status flips `ACTIVE → EXPIRED`
(`PointsBucketServiceImpl.java:75`), and a single `PointsTransaction` of type `EXPIRE` is written with
`pointsDelta = -pointsRemaining` (`PointsBucketServiceImpl.java:79-94`). There is no separate append-only "bucket
closed" event distinct from the bucket row's own mutation; the audit trail for *why* the bucket closed is the prose
`description`/`reason` string on that one `PointsTransaction` row, not structured data.

### Transaction layer (closest thing to an event log)

`PointsTransaction` (`model/Entity/PointsTransaction.java:17-81`) is one row per value movement, with a signed
`points_delta` and a `type` from an 8-value enum (`PointType.java`). Only **4 of the 8** declared types are ever
constructed by a real service path (confirmed by exhaustive grep for `.type(PointsType.`):

| Type | Constructed by |
|---|---|
| `EARN_BILL` | `EarnPointsGuestsCalculatorServiceImpl.java:118` |
| `COUPON_REDEMPTION` | `CouponRedemptionServiceImpl.java:95` |
| `EXPIRE` | `PointsBucketServiceImpl.java:83` |
| `REDEEM_DISCOUNT` | Only `DemoDataSeeder.java:951` — never a live service |

`REDEEM_OFFER`, `ADJUSTMENT_PLUS`, `ADJUSTMENT_MINUS`, and `REVERSAL` exist in the enum and appear in *display*
switch statements (`CustomerTransactionViewServiceImpl.java:72`, `CustomerBusinessDetailServiceImpl.java:620`) but
are never constructed anywhere. They are unimplemented, forward-declared UI affordances — not confirmed behavior.
Treat them as aspirational, not as evidence of an intended adjustment/reversal design.

This table is also not append-only in the sense P4 requires: `PointsTransactionRepository.deleteByBusinessId` and
`.deleteByCustomerProfileId` (`PointsTransactionRepository.java:132-138`) are called directly by
`CustomerAccountDeletionServiceImpl.java:55` and `BusinessDeletionServiceImpl.java:93` on account/business
deletion — a hard, irreversible bulk delete of value history (finding #25). The same two services also hard-delete
`points_buckets` and `points_bucket_consumption` rows. Neither `points_transactions`, `points_buckets`, nor
`points_bucket_consumption` carries its own `business_id` column — business is only reachable by joining through
`loyalty_account_id → loyalty_accounts.business_id` (finding #21, verified against
`001-baseline.xml:285-313` for the transactions table shape).

### What "lifetime" does and does not mean, precisely

`lifetime_earned` is the closest legacy analogue to Status Beats, but the resemblance is partial and must not be
overstated:

- **It matches**: it is monotonic today, in the same direction Status Beats requires (never decreases).
- **It does not match**: nothing in the legacy system *enforces* that monotonicity as an invariant — it is an
  absence of a decrementing feature, not a guarantee. It also only accrues from one earning path (`EARN_BILL`); if
  Ritema ever wants Status to accrue from something other than bill-based earning, legacy provides no precedent
  either way.
- **No refund/reversal/chargeback/merge path exists to test the boundary against.** This is the central fact that
  shapes Section 2: the "hard part" of this mapping is not resolving a conflict in existing behavior — there is
  none to resolve — it is that the four-balance model must introduce protections (a real `CHECK`, immutable-event
  enforcement) that legacy never needed because it never had a code path that would have violated them.

## 2. The mapping

### Available Beats → `loyalty_accounts.available_points`, directly

Clean 1:1 legacy analogue. Spendable, decreases on spend/expire, increases on earn. Only the accounting mechanism
changes — from mutable read-modify-write on a single row to a value recomputed by replaying `beat_events`. The
existing `@Min(0)` is Bean Validation only, not a database constraint (finding #23, already tracked in the audit;
not re-litigated here).

### Status Beats → `loyalty_accounts.lifetime_earned`

Per the direction confirmed with you: `lifetime_earned`'s existing monotonicity is treated as the Status Beats
precedent, not as a coincidence to be redesigned around. It already tracks "how much value has this customer, at
this business, ever been credited with," which is the substance of a recognition-tier signal, and no legacy feature
contradicts treating it that way.

The caveat from Section 1 carries forward directly: this mapping means the four-balance model must **add** a
protection legacy never had — no event type may ever carry a negative `status_delta` — rather than **preserve** one
that already existed. That distinction matters for how Section 3 frames "illegal behaviours": we are not finding
legacy code that violates the new rule (nothing does, because nothing was ever built to try), we are noting that
legacy's absence of an adjustment/reversal feature is not evidence the rule is already satisfied by design.

### Business Beats → all of legacy's `available_points`, retroactively relabeled

Genuinely new *label*, not a new *concept*. Every legacy `loyalty_account` is already scoped 1:1 to
`(customer_profile_id, business_id)`, and every coupon/reward is already owned by exactly one business
(`CouponRedemptionServiceImpl.java:78-88` loads the account by `customerProfile.id + coupon.getBusiness().getId()`).
There is no existing multi-issuer concept to disentangle — legacy has never had to distinguish "whose beats these
are" because there was only ever one possible answer. The mapping is additive: everything legacy calls
`available_points` today **is** Business Beats, until Platform Beats is introduced as a second issuer sharing the
same spendable surface.

### Platform Beats → genuinely new, no legacy precedent

Confirmed by grep: every occurrence of "platform" in the codebase refers to super-admin dashboards
(`PlatformSummaryService`, etc.), never to a currency, ledger, or cross-business reward concept. No platform-issued
coupon or reward exists anywhere.

Per the direction confirmed with you, this ADR defines Platform Beats minimally rather than designing it in full:

- **Issuer**: Ritema (the platform), not any business.
- **Liability**: Ritema bears the liability for Platform Beats outstanding, not the redeeming business.
- **Fungibility on spend**: Platform Beats must be able to fund `available_points` alongside Business Beats without
  the spend/redemption logic needing to distinguish them — Available is one fungible pool at spend time.
- **Distinguishability in the ledger**: despite being fungible on spend, `beat_events` must still record which
  issuer each `available_delta` came from, so liability accounting and reconciliation can separate "who owes what"
  after the fact.

Full design of *how* Platform Beats gets funded, which businesses can participate in redeeming them, and what
promotional rules govern issuance is explicitly out of scope here and is flagged as a follow-up ADR once a concrete
product requirement exists (see Open Questions).

### What happens to `lifetime_redeemed` and `lifetime_expired`

Neither maps to one of the four balances — they are not balances, they are historical audit counters. Under the
event-sourced model they become **derived read-models**: `lifetime_redeemed` is `SUM(available_delta)` over
redemption-type events (negative, so summed as absolute value), `lifetime_expired` is the same over expiry-type
events. Nothing is lost — replaying `beat_events` reproduces both exactly — but neither needs a dedicated mutable
column any longer. This closes the loop on all four legacy `LoyaltyAccount` counters, not just the one that maps to
Status.

### Naming note: "points" → "Beats"

CLAUDE.md already names the target schema and vocabulary in Beats terms (`beat_events`, `available_beats`,
`status_beats`, `business_beats`, `platform_beats`, "Beats"). Every new-model name used in this ADR follows that
convention. Separately, you asked for the existing legacy vocabulary — Java packages/classes/fields
(`PointsTransaction`, `PointsBucket`, `PointsBucketConsumption`, `LoyaltyAccount.availablePoints`, the `points`
feature packages, `PointsType`, etc.), the legacy MySQL/Liquibase column and table names
(`points_transactions`, `points_buckets`, `available_points`, `lifetime_earned`, …), and project documentation — to
be renamed from "points" to "Beats" throughout.

That is recorded here as a decision, not executed here:

- It is explicitly **out of scope for this ADR** — the mission that produced this file is documentation-only ("All
  code and all DDL" is out of scope; this file is one markdown document).
- It is a **large, cross-cutting refactor of live code** (roughly 20 Java files across 4 feature packages, plus the
  Liquibase changelog, plus this repo's own documentation), which CLAUDE.md's workflow requires a proposed plan and
  your explicit approval for, the same as any other ledger-adjacent change.
- It may be **redundant with, rather than a precursor to, the ledger rewrite**: per CLAUDE.md's module table, `ledger`
  (Beat events, balances, idempotency, reconciliation) is a distinct target module, not a renamed version of today's
  `pointsBucket`/`pointsTransaction`/`loyaltyAccount` feature packages — Section 5 below shows the target shape is
  not just a renamed `PointsBucket`, it is a structurally different table (append-only events, derived snapshots,
  `FOR UPDATE SKIP LOCKED`, no optimistic-lock version column). Renaming the legacy classes in place, only to then
  replace their bodies wholesale when `ledger` is built, would be double-work if the legacy feature packages are
  going to be retired rather than evolved in place.
- The legacy MySQL/Liquibase schema itself is slated for wholesale replacement (a new immutable Flyway baseline),
  not incremental migration — see "Must fix before the Postgres migration," item 9, in the audit. Renaming its
  columns has no runtime benefit if that schema is dropped rather than carried forward.

**Recommendation:** track the rename as a follow-up decision, scoped as: (a) this repository's documentation and
this ADR's own vocabulary — already done, above, and going forward; (b) the *new* `ledger` module's names, which
will be written in Beats vocabulary from day one, so no renaming is needed there; (c) the *legacy* `points*`
packages — leave as-is until it is decided whether they are renamed in place, refactored into the new `ledger`
module, or deleted outright once `ledger` replaces them, since that decision depends on choices this ADR does not
make (see Open Questions).

## 3. Legacy behaviours that become illegal under the new laws

Each item below is a behaviour Section 1 confirmed exists today, the law it breaks, and the replacement behaviour
the four-balance model requires. None of these are legacy code *violating* an existing rule — legacy never had
these rules. They are new protections the target model must add.

**3.1 In-place bucket mutation on expiry — illegal under P4.**
`PointsBucketServiceImpl.java:75` (`pointsBucket.setStatus(PointsBucketStatus.EXPIRED)`) followed by
`pointsBucketRepository.save(pointsBucket)` at line 95 mutates a row that represents a still-open earn lot, in
place, with no separate record of the closing event. P4 requires the ledger to be append-only and state to be a
snapshot recomputable by replay. *Replacement:* expiry becomes an appended `EXPIRE` beat event; the lot's
remaining-value figure becomes a derived snapshot maintained alongside the event (Section 5), never an UPDATE
issued independently of writing that event.

**3.2 Unlocked read-then-write on expiry — illegal under P3 and P4.**
`expireSingleBucket` (`PointsBucketServiceImpl.java:64-96`) loads the bucket via a plain `findById` with no `@Lock`,
computes the amount to debit from that unlocked read, and writes two rows afterward with no outbox entry
guaranteeing the write survives a crash between the two saves. This is audit findings #2 and #66, and is the
specific case Section 5 exists to close.

**3.3 Hard deletion of value history — illegal under P4, unconditionally.**
`PointsTransactionRepository.deleteByBusinessId`/`deleteByCustomerProfileId`
(`PointsTransactionRepository.java:132-138`), and the equivalent methods on `PointsBucketRepository.java:44-50` and
`PointsBucketConsumptionRepository`, are called directly by `CustomerAccountDeletionServiceImpl.java:47-56` and
`BusinessDeletionServiceImpl.java:86-94` on account and business deletion. P4 states plainly: "Never `UPDATE` or
`DELETE` a beat event." *Replacement:* account/business deletion must never issue a `DELETE` against `beat_events`
or its derived tables. The customer- or business-facing effect of "deletion" (the account no longer appears, no
further activity is possible) is represented by new state/events layered on top of an intact history, not by
erasing the history. **This creates a real tension with a plausible legal requirement** (GDPR-style right to
erasure) that this ADR does not resolve — see Open Questions.

**3.4 Value written without a guaranteed durable event — illegal under P1/P4 (context for Section 4, not itself in
this ADR's scope to fix).**
The earn path (`EarnPointsTransactionServiceImpl.java:137-175`) writes the bill, points transaction, and bucket in
one JPA transaction but with no outbox row — a committed database write is not the same as a guaranteed downstream
event (finding #61). This is a code-level fix (transactional outbox), correctly deferred per the coverage table;
it is listed here only because Section 4's event taxonomy must be shaped so implementing the outbox later is a
mechanical addition, not a redesign.

**3.5 No idempotency key on value-moving operations — illegal under P3.**
Coupon redemption (`CouponRedemptionServiceImpl.java:45-105`, finding #62), staff coupon consumption (finding
#63), and bucket expiry (finding #66) each move value with no caller- or system-supplied idempotency key enforced
by a unique constraint. *Replacement:* every event-emitting operation carries a deterministic idempotency key;
Section 5 specifies expiry's key concretely since it is the one with no natural caller-supplied key (there is no
"caller" for a scheduled sweep).

**3.6 Tenant scoping reachable only by join, not structural — illegal under P6.**
`points_transactions`, `points_buckets`, and `points_bucket_consumption` carry no `business_id` column of their
own; business is only reachable via `loyalty_account_id → loyalty_accounts.business_id` (finding #21, verified
against `001-baseline.xml:285-313`). P6 requires every tenant-scoped table to carry `business_id` directly, filtered
in the repository layer *and* enforced by row-level security. *Replacement:* `beat_events` and any derived
lot/consumption tables carry `business_id` as a leading column, independent of any join.

**3.7 Balance concurrency control via optimistic version + scattered pessimistic locks — retired, not fixed.**
`LoyaltyAccount.java:55-57` carries a `@Version` column that JPA's optimistic locking would use on a
conflicting concurrent write, but every actual balance mutation in the codebase instead takes an explicit
pessimistic lock (`findWithLockOrCreate`, `findWithLockByCustomerProfileIdAndBusinessId`,
`findSpendableBuckets`'s `PESSIMISTIC_WRITE`) — the version column does not appear to be the thing preventing lost
updates in practice. This is not illegal under any law by itself, but it disappears under the target model rather
than being carried forward: P5/ledger rules specify concurrent writers use `INSERT ... ON CONFLICT DO UPDATE`
against a balance snapshot, not optimistic-lock retries and not application-level pessimistic locks on a mutable
account row. There is no "Available Beats" row to lock, in the legacy sense — it is upserted per event.

## 5. Expiry design under an append-only ledger

This is the concrete mechanism for closing 3.1, 3.2, and 3.5 (findings #2 and #66) simultaneously. It has four
parts: what expiry becomes, who emits it, how it stays idempotent and single-writer, and how FIFO ordering
survives without a mutable bucket row.

**What expiry becomes.** Expiry is an appended beat event, `EXPIRE` (name carried over from `PointsType.EXPIRE`,
since nothing about the label needs to change): `available_delta = -N`, `status_delta = 0`. The `status_delta = 0`
is not incidental — it is the mechanism that satisfies the stated invariant "Status Beats never decrease... under
any operation including... expiry": expiry is structurally forbidden from ever emitting a nonzero `status_delta`,
so there is no code path left that could accidentally decrement Status on expiry, because expiry events do not
carry that field's ability to move at all.

**Lots survive as a derived cache, not as authoritative state.** The legacy `points_buckets` table plays two roles
today that the ledger model must separate: (1) it records *that* an earn lot exists with an expiry date — this is
genuinely new information not derivable from a flat event log unless earn events themselves carry an
`expires_at`, so lot identity is intrinsic to the `EARN` event, not a separate fact; (2) it tracks *how much of
that lot remains* — this is a derived quantity, exactly like the four top-level balances, and must be treated the
same way: a snapshot that is always recomputable by replaying every event that touched that lot (its own `EARN`,
every `SPEND` that consumed from it, and at most one `EXPIRE`). Concretely, a lot-level snapshot row is kept — it
is not eliminated, because recomputing "remaining in this lot" by folding the full event history on every spend
would be too slow for the scan path — but it is maintained *by* the event write, in the same transaction, the same
way the four account-level balances are maintained by event writes. It is never the target of an independent
`UPDATE` issued without a corresponding event row justifying the change. This is the resolution to the "bucket
expiry against an immutable event log" tension: the bucket doesn't disappear, its authority does — the event log is
what makes a lot's remaining balance true; the lot row is a cache of that truth, invalidated and rebuilt by
replay if it and the log ever disagree (the same reconciliation posture CLAUDE.md already specifies for the
top-level balances).

**Who emits it, and single-writer discipline.** The emitter is a scheduled sweep, structurally identical in shape
to today's `ExpirePointsBucketScheduler`/`findExpiredBucketIds` (enumerate lots where `expires_at` has passed and
remaining is greater than zero). Two protections apply together, not as alternatives:

- *Coarse-grained*: the sweep itself is guarded by a distributed lock (ShedLock, per the Async Rules — "Every
  `@Scheduled` method carries a distributed lock"), so only one instance runs a given sweep pass at a time. This
  alone closes finding #1 (every instance running the scheduler concurrently) but is not sufficient by itself,
  because it does not protect a sweep from racing a *concurrent spend* against the same lot, and it does not
  survive a sweep instance crashing mid-pass and a second pass starting before ShedLock's lease expires.
- *Fine-grained, database-enforced*: for each lot, the expiry write is a single transaction that reads the lot's
  current remaining value, writes the `EXPIRE` event, and updates the lot snapshot together — with the lot row
  locked for that transaction (`FOR UPDATE`, or `FOR UPDATE SKIP LOCKED` if the sweep processes lots in a loop and
  should skip past ones already being touched by a concurrent spend rather than block on them). This is what
  actually closes finding #2/#66's read-then-write race: the amount debited is read and consumed atomically, not
  read once and reused after the lock is released.

**Idempotency key.** A scheduled sweep has no caller to supply an idempotency key, so the key must be deterministic
and derived from the lot itself: the combination of the lot's identity and the fact that it is being expired (at
most one `EXPIRE` event can ever exist per lot) is enforced as a database uniqueness constraint on the event table
— a duplicate attempt (a retried sweep pass, or two passes that both selected the same lot before either lock was
taken) fails or no-ops on that constraint rather than double-writing, which is exactly P3's contract: "a duplicate
returns the original result, silently." This is stronger than the ShedLock alone, and is what makes the mechanism
correct even if the distributed lock is ever misconfigured or expires early — the database, not the scheduler, is
the final arbiter of "has this lot already expired."

**How FIFO ordering survives.** The query that selects spendable lots today — soonest-`expires_at`-first, nulls
(no-expiry lots) sorted last, `remaining > 0`, not already past its own expiry (`PointsBucketRepository.java:35-41`)
— does not need to change in shape. It continues to run against the lot-snapshot table described above; the only
difference is that table's `remaining` column is now guaranteed consistent with the event log because every writer
that touches it also writes the event that justifies the change, in the same transaction, under a row lock. Spend
already takes `PESSIMISTIC_WRITE` on the lots it selects (`PointsBucketRepository.java:35`) — that discipline
carries forward unchanged. What changes is that expiry now takes the equivalent lock too, which it does not do
today, closing the asymmetry between a correctly-locked spend path and an unlocked expiry path that findings #2 and
#66 describe.

## 4. Event taxonomy

Two groups of evidence anchor this taxonomy: the event shapes legacy already proves out by construction (EARN,
REDEEM, EXPIRE — Section 1's exhaustive call-site check), and the operations the invariants list requires Status to
survive without decreasing (refund, reversal, chargeback, expiry, account merge, business deletion). Expiry is
covered above. Of the remaining five, four are active design targets below; **account merge will not be offered as
a feature** (your decision) and is addressed as a considered-and-rejected note rather than a live type.

| Type | `available_delta` | `status_delta` | Reversible | Source |
|---|---|---|---|---|
| `EARN` | `+N` | `+N` | via `REVERSAL` | Legacy `EARN_BILL` — `LoyaltyAccount.add()` moves both fields by the same amount, in lockstep, every time (`LoyaltyAccount.java:68-72`) |
| `REDEEM` | `-N` | `0` | via `REVERSAL` | Legacy `COUPON_REDEMPTION` (live) — `spend()` never touches `lifetime_earned` (`LoyaltyAccount.java:74-78`). `REDEEM_DISCOUNT` (seeder-only) and `REDEEM_OFFER` (unimplemented) are the same shape and collapse into this one type, distinguished by a reference field, not by three separate point-moving types |
| `EXPIRE` | `-N` | `0` | in principle, via a corrective `EARN` referencing the mistake (never needed by legacy) | Legacy `EXPIRE` — `expire()` never touches `lifetime_earned` (`LoyaltyAccount.java:80-83`) |
| `REVERSAL` | `∓N` (sign matches what is being undone) | `0`, always | not itself reversible by another `REVERSAL` of the same target; a wrong `REVERSAL` is corrected by a new ordinary event | New. Collapses **refund + reversal + chargeback** into one type — they are the same ledger shape (Available moves, Status never does), differing only in *why*, which belongs in a reason field, not in three point-moving types |
| — | — | — | — | "Business deletion" deliberately produces **no event type at all** — see below |
| `PLATFORM_GRANT` | `+N` | `0` or `+N`, issuer's choice — see below | via `REVERSAL` | New — no legacy precedent (Section 2); Platform Beats issuance |
| `ADJUSTMENT_PLUS` | `+N` | `0` (**nonincreasing**) or `+N` (**increasing**, equal to `available_delta`) — a deliberate choice made at issuance, not a fixed default | via `REVERSAL` | Legacy enum value exists (`PointsType.java`) but is never constructed anywhere |
| `ADJUSTMENT_MINUS` | `-N` | `0`, forced — no increasing mode | via `REVERSAL` | Same category as `REVERSAL`/`EXPIRE`/`REDEEM`: everything that reduces Available never touches Status. There is no sensible reading of a debit-only correction that should also raise Status, so this type gets one mode, not a choice |

**Resolved: `ADJUSTMENT_PLUS` (and `PLATFORM_GRANT`) get both modes, as a deliberate choice, not a fixed default.**
Open Question #2 asked whether these types ever move `status_delta`. Per your decision, the answer is: both
possibilities are legitimate, sanctioned outcomes — the issuer chooses which one applies each time, rather than the
taxonomy forcing a single answer for every use. Concretely, `ADJUSTMENT_PLUS` has two modes:

- **Increasing** (`status_delta = available_delta`): used when the adjustment corrects an under-count of value the
  customer should be treated as having genuinely earned — e.g., staff forgot to award points for a large group
  booking. The correction counts toward Status exactly as if `EARN` had fired correctly the first time.
- **Nonincreasing** (`status_delta = 0`): used when the adjustment is discretionary or corrective without implying
  the customer earned anything — a goodwill credit, a promotional grant, or the erasure forfeiture below.

`ADJUSTMENT_MINUS` keeps its single forced mode from the table above — reducing Available never has a sensible
"increasing Status" counterpart, so no choice is offered there. This ADR extends the same two-mode design to
`PLATFORM_GRANT` for consistency, since it is structurally identical to `ADJUSTMENT_PLUS` (a discretionary positive
Available credit from a non-`EARN` source) — flag this if you intended the decision to be scoped to
`ADJUSTMENT_PLUS` only and not carried over to `PLATFORM_GRANT`.

**This is what resolves the residual doubt in the erasure ADR, not something separate from it.** Before this
decision, Open Question #2 noted that `gdpr-compliance.md`'s `FORFEIT` step
(`ADJUSTMENT_MINUS(ACCOUNT_CLOSED_ERASURE)`, "Status Beats untouched") only *corroborated* `status_delta = 0` for
that one use — it was one example doing something, not a declared rule authorizing it. `ADJUSTMENT_MINUS`'s forced
single mode now makes that usage a direct instance of a general rule stated here, not a special case invented on
the spot in a different document. Nothing about the erasure flow's mechanics changes — it already worked — what
changes is that it is now grounded in this ADR's taxonomy instead of standing on its own.

**Considered and rejected: a `MERGE` event type for account merge.** You've decided account merge will not be
offered as a feature, so no `MERGE` type is part of this taxonomy. It's worth recording *why* the design was
attempted and abandoned, not just that it was: my first draft used a paired `MERGE_OUT` (on the source account,
`status_delta = -S`) and `MERGE_IN` (on the destination, `status_delta = +S`), modeled like a transfer. That design
was wrong on its own terms — it made the source account's Status decrease by exactly `S` at the moment of merge,
which is the literal operation the invariant list names as one Status must never decrease under. The corrected
shape would have credited the surviving account only, in a single event carrying the source's balances as a
one-time snapshot, with the source account retired outside the ledger rather than zeroed by an offsetting event.
That corrected version is not being built. If account merge is ever reconsidered, this note — not the abandoned
symmetric-pair design — is the starting point, since the symmetric pair is a trap that looks natural (transfers
usually are symmetric) and isn't, specifically because of the never-decreases invariant.

**Why "business deletion" produces no event type.** Per Section 3.3, deletion must never touch the ledger at all —
a deleted business's history simply persists, unread by anyone, exactly as immutable as any other account's. Status
"never decreases under business deletion" holds trivially because nothing happens to any event when a business is
deleted. Inventing an event type for this would be building a mechanism nothing requires.

## 6. Business Beats and Platform Beats: issuance, liability, and interaction on redemption

**Business Beats.** Every legacy `LoyaltyCoupon` is owned by exactly one business (`business_id` is `NOT NULL`,
`LoyaltyCoupon.java:31-33`) and its `points_cost` (`@Min(1)`, line 50-52) is spent out of the `LoyaltyAccount` scoped
to that same `(customer, business)` pair (`CouponRedemptionServiceImpl.java:78-88`) — there is only ever one pool to
draw from, so legacy already enforces, by construction, that a business's coupons are paid for out of that
business's own issued value. The four-balance model must keep this as an explicit rule rather than an accident of
schema shape: a `REDEEM` against a business's coupon may be funded by Business Beats issued by that business, or by
Platform Beats (if enabled — see below), but never by another business's Business Beats. Liability for Business
Beats sits with the issuing business.

**Platform Beats.** Per Section 2's minimal definition: issuer and liability are Ritema, not the redeeming
business. On redemption, Platform Beats and Business Beats fund the same fungible `available_beats` figure for a
given `(customer, business)` pair — the customer does not see two separate spendable numbers — but the ledger must
still record, per unit spent, which issuer's liability it discharged, or reconciling what Ritema owes a business
(or vice versa) becomes impossible after the fact. This reuses Section 5's lot mechanism directly: lots are tagged
with an issuer (`BUSINESS` or `PLATFORM`) at creation (`EARN` for Business Beats, `PLATFORM_GRANT` for Platform
Beats), a `REDEEM` still consumes lots soonest-expiring-first regardless of issuer (the customer-facing spend order
does not change), but the consumption record — the equivalent of today's `points_bucket_consumption` — preserves
which issuer each consumed unit came from. "How many Platform Beats did this business's customers redeem, that
Ritema owes this business for" is then a derivable query over consumption records, not a redesign.

What is explicitly **not** decided here, per your direction to define Platform Beats minimally: whether a business
must opt in to accepting Platform Beats redemptions, whether Platform Beats can be earned at every business or only
specific ones, and what funds Platform Beats issuance (a marketing budget, a subscription tier, etc.). These are
product questions with zero legacy precedent to ground them, and belong in a separate ADR once a concrete
requirement exists.

## 7. Options where the mapping is genuinely ambiguous

**Option set A — What `lifetime_earned` becomes.** *(Decided; recorded for the record.)*
- **A1 (chosen): treat as Status Beats precedent.** Cost: the monotonicity being carried forward is incidental
  (no refund/reversal path ever tested it), so the four-balance model must add real enforcement (a `CHECK`, or
  reliance on `REVERSAL`/`ADJUSTMENT` never carrying a `status_delta`) that legacy never needed. Benefit: simplest
  migration story, zero behavioral surprise for a business reading their existing `lifetime_earned` numbers as
  "this is now called Status."
- **A2 (rejected): design Status fresh, independent of `lifetime_earned`.** Cost: no legacy grounding at all for
  what should drive Status instead; invents scope (which actions count) this ADR has no mandate to invent. Benefit:
  avoids inheriting a field whose monotonicity was never actually guaranteed by anything.

**Option set B — Platform Beats scope.** *(Decided; recorded for the record.)*
- **B1 (chosen): define minimally, flag full design as a future ADR.** Cost: Section 6 above leaves real product
  questions open. Benefit: does not invent funding/opt-in/eligibility rules with zero legacy precedent and no
  stated product requirement — consistent with not designing for hypothetical futures.
- **B2 (rejected): design Platform Beats in full here.** Cost: every rule invented here would be pure speculation,
  not grounded in code, product requirement, or a decision made in this conversation. Benefit: would give
  implementers a complete answer immediately, if the speculation happened to be right.

**Option set C — Granularity of the refund/reversal/chargeback taxonomy.**
- **C1 (chosen, used in Section 4): one `REVERSAL` type, distinguished by a reason/reference field.** Cost:
  dashboards or automated workflows that need to react differently to a chargeback than to a voluntary refund (e.g.,
  freezing an account only on a chargeback, not a refund) must branch on the reason field rather than the event
  type. Benefit: smaller taxonomy, and the three operations are genuinely the same ledger shape — Available moves,
  Status never does — so a type-per-operation split would be duplicating logic for no ledger-level reason.
- **C2 (viable alternative): three separate types, `REFUND` / `REVERSAL` / `CHARGEBACK`.** Cost: larger enum,
  three code paths to keep in sync instead of one. Benefit: if chargebacks specifically need a different downstream
  workflow trigger (dispute handling, account risk flags) later, having a distinct type makes that trivial to filter
  on without parsing a reason string. Worth revisiting if/when a chargeback-specific workflow becomes a real
  requirement — not before.

## 8. Open questions

1. ~~GDPR-style erasure vs. append-only ledger.~~ **RESOLVED** by `docs/adr/gdpr-compliance.md` (Rev 3): identity
   splits into a permanent, PII-free `subject` (the ledger's FK target) and a deletable `person_identity`/
   `business_identity` leaf; erasure is a hard `DELETE` of the identity row with `ON DELETE CASCADE`, never a delete
   of `beat_events`. That schema's `beat_events.status_delta BIGINT NOT NULL CHECK (status_delta >= 0)` is worth
   noting here: it enforces "Status never decreases" as a single, blanket database constraint over every event
   regardless of type, which is a cleaner mechanism than this ADR's type-by-type "defaults to 0" reasoning in
   Section 4 — that CHECK is the actual enforcement; Section 4's per-type deltas are what satisfy it. That schema
   also corroborates Section 6's design directly: its `beat_events.issuer` column (`BUSINESS | PLATFORM`) is exactly
   the per-event issuer tag Section 6 requires for Business/Platform Beats liability accounting.
2. ~~Do `PLATFORM_GRANT` and `ADJUSTMENT_PLUS`/`ADJUSTMENT_MINUS` ever move `status_delta`?~~ **RESOLVED.** Both
   modes are sanctioned for `ADJUSTMENT_PLUS` and `PLATFORM_GRANT` — increasing (`status_delta = available_delta`)
   or nonincreasing (`status_delta = 0`) — chosen deliberately at issuance, not fixed by type. `ADJUSTMENT_MINUS`
   keeps a single forced nonincreasing mode, same category as `REVERSAL`/`EXPIRE`/`REDEEM`. See Section 4 for the
   full design and how this grounds the erasure ADR's `FORFEIT` step in a declared rule rather than a one-off.
3. ~~Is account merge a real near-term feature?~~ **RESOLVED — cleared.** You will not offer account merge. No
   `MERGE` event type is part of this taxonomy (Section 4); the design work that was attempted and rejected is kept
   as a note there for the record, not as a pending decision.
4. ~~What expiry does a `MERGE`-credited lot get?~~ **RESOLVED — moot**, as a direct consequence of #3: there is no
   merge, so there is no merged-in lot to assign an expiry to.

---

*This ADR is complete, with all four open questions resolved, pending your final review and approval per the
mission's Definition of Done.*
