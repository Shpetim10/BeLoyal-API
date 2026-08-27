# BesaHub BeLoyal API — Comprehensive Project Documentation

> Thesis-grade architectural and functional documentation of the BeLoyal loyalty-program backend.
> Generated from a full source audit of `src/main/java`, Liquibase changelogs, and configuration.
> Date of analysis: 2026-05-19. Branch: `main`.

---

## 1. Executive Summary

BeLoyal is the backend for a multi-tenant **loyalty-program platform**. Each *business* runs its
own loyalty program; *customers* earn points on purchases (bill transactions), points accumulate
into FIFO **points buckets** that expire over time, and customers spend points to redeem
**coupons** (free products or discounts). Staff members operate the program on behalf of a
business; platform administrators (SUPER_ADMIN/ADMIN) approve businesses and oversee the platform.

- **Language / Framework:** Java 21, Spring Boot 3.5.6
- **Persistence:** MySQL + Spring Data JPA, schema owned by **Liquibase** (`ddl-auto=validate`)
- **Infrastructure:** Redis (idempotency + JWT revocation), SMTP email, local filesystem storage
- **Security:** Spring Security, stateless JWT access tokens, opaque refresh tokens, ownership tokens
- **Scale of codebase:** ~580 Java classes across ~30 feature modules, 22 JPA entities, 22 enums,
  5 Liquibase changelog files, 8 test classes.

---

## 2. Technology Stack

| Concern | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.6 (Web, Data JPA, Security, Validation, Mail) |
| Database | MySQL, Spring Data JPA / Hibernate |
| Schema migration | Liquibase (YAML master + XML changelogs), `spring.jpa.hibernate.ddl-auto=validate` |
| Cache / coordination | Redis (Spring Data Redis) |
| Auth | Spring Security 6, jjwt (HS256), BCrypt password hashing |
| Email | Spring Mail (SMTP), HTML templates, after-commit dispatch |
| File storage | Local filesystem (`LocalStorageService`), served at `/uploads/**` |
| Build | Maven (`mvnw`), `pom.xml` |
| Boilerplate | Lombok, Bean Validation (Jakarta) |
| Testing | Spring Boot Test, JUnit |

---

## 3. Architectural Overview

### 3.1 Layering

The application follows a strict **`controller → service → repository`** layering:

- **Controllers** (`features/<feature>/controller`): REST endpoints, request binding, validation,
  authorization annotations. No business logic.
- **Services** (`features/<feature>/service` + `/impl`): business logic and orchestration; almost
  every service is defined as an interface plus an `Impl`, enabling clean dependency inversion.
- **Repositories** (`features/<feature>/repository`): Spring Data JPA interfaces; persistence and
  query scoping.
- **DTOs** (`features/<feature>/dto`): request/response models. Entities are (mostly) not exposed
  directly — see Known Issue H1 for the exception.
- **Shared concerns** (`common/`): configuration, exception handling, validation framework,
  storage, email, Redis, helpers.
- **Domain model** (`model/Entity`, `model/Enums`): JPA entities and enums centralized rather
  than per-feature.

### 3.2 Feature-sliced packaging

Code is organized by **business capability**, not by technical layer. Each feature is a
self-contained vertical slice. Cross-feature interaction happens through public service APIs,
not by reaching into another feature's internals.

### 3.3 Module map

| Module | Responsibility |
|---|---|
| `auth` | Login, JWT issuance, authentication attempts/lockout, login policy |
| `Security` | Spring Security config, JWT filter/service, ownership tokens, business/transaction security expressions |
| `token` | Refresh, reset-password, staff-invite, email-verification token lifecycles + cleanup scheduler |
| `registration` | Business registration & admin approval/rejection, staff invitation/acceptance, customer registration |
| `passwordChanger` | Authenticated password change + forgot/reset-password flow |
| `user`, `userProfiles/*` | User CRUD (admin), user/business/customer/staff profiles |
| `business`, `businessMember` | Business entities & lifecycle, staff/membership relationships |
| `loyaltyAccount`, `loyaltyCard` | Per-business customer balance accounts; QR/manual loyalty cards |
| `pointsTransaction` | Immutable points ledger (earn / redeem / expire / adjust) |
| `pointsBucket`, `pointsBucketConsumption` | FIFO expiry buckets and their consumption bridge |
| `registerLoyaltyPoints`, `billTransaction` | Earn-points transaction flow, preview, bill records |
| `earningSettings`, `loyaltySettings` | Per-business earning rate & loyalty rules |
| `coupon` | Business-owned coupon *templates* (CRUD, lifecycle, expiry scheduler) |
| `customerCoupon` | Issued coupons: redemption, availability, staff scan |
| `couponLookup`, `customerLookup` | Lookup helpers (catalog targets, customer by email/QR/code) |
| `catalogCategories`, `catalogItems`, `catalogItemVariants` | Product catalog hierarchy |
| `customerApis` | Customer-facing aggregate views (home, business detail, transactions, promotions, account deletion) |
| `dashboard`, `superadmin` | Business-admin/staff dashboards, platform summaries, superadmin platform ops |
| `redirect`, `seeding`, `common/image_upload` | Deep-link redirect, demo data seeder, media upload |

---

## 4. Domain Data Model

### 4.1 Identity & tenancy

- **`User`** — global account. Holds platform roles via a `user_roles` `@ElementCollection`
  (EAGER), plus status/lock/email-verification lifecycle. Email & username are normalized to
  lowercase via JPA lifecycle callbacks.
- **`Business`** — a tenant. Status lifecycle: `PENDING_APPROVAL → ACTIVE / REJECTED`, plus
  `INACTIVE` and `BANNED` (see `BUSINESS_STATUS_LIFECYCLE.md`).
- **`BusinessMember`** — the User↔Business join carrying the *business-scoped* role
  (`BUSINESS_ADMIN` or `STAFF`). Unique on `(user_id, business_id)`. **Business roles live here,
  not in the JWT.**
- **`CustomerProfile`** — 1:1 with `User`, unique `referral_code`.
- **`LoyaltyCard`** — 1:1 with `CustomerProfile`; carries a unique QR token and a unique manual
  code used by staff to look the customer up.

### 4.2 Loyalty ledger

```
LoyaltyAccount (1 per customer+business, @Version optimistic lock, integer balances)
   ├── PointsTransaction (immutable ledger row, typed by PointsType: EARN_BILL / COUPON_REDEMPTION / EXPIRE / ...)
   │       └── optional link to BillTransaction and performing BusinessMember
   ├── PointsBucket (one per EARN transaction; pointsEarned, pointsRemaining, expiresAt, status)
   └── PointsBucketConsumption (bridge: which transaction consumed how much of which bucket)
```

- `LoyaltyAccount.availablePoints` is a **denormalized cache** of the sum of `pointsRemaining`
  over the account's `ACTIVE` buckets. Intended invariant:
  `availablePoints == Σ active bucket pointsRemaining`, and
  `lifetimeEarned − lifetimeRedeemed − lifetimeExpired == availablePoints`.
- Buckets are consumed **FIFO by expiry** (soonest-to-expire first, nulls/never-expire last).

### 4.3 Coupons

- **`LoyaltyCoupon`** — business-owned *template*: `CouponType`
  (`FREE_PRODUCT`, `PERCENTAGE_DISCOUNT`, `FIXED_AMOUNT_DISCOUNT`), points cost, validity window,
  total & per-customer redemption limits, `CouponStatus`
  (`DRAFT → ACTIVE → {PAUSED, EXPIRED, ARCHIVED}`), `CouponVisibility` (`PUBLIC`/`HIDDEN`),
  optional 1:1 `CouponDiscountDetails` or `CouponFreeProductDetails`. `@Version` optimistic lock,
  soft-delete via `deletedAt`/`archivedAt`.
- **`CustomerCoupon`** — an *issued instance* a customer owns after spending points. Carries a
  unique QR code and **snapshot** columns (title, description, image, type, discount values,
  free-product names) captured at redemption time so later template edits do not retroactively
  change purchased coupons. Lifecycle `CustomerCouponStatus`: in practice only `REDEEMED → USED`
  are persisted; `EXPIRED` is derived at read time (see Known Issue H4).

### 4.4 Catalog

`CatalogCategory → CatalogItem → CatalogItemVariant`, all soft-deletable, business-scoped
uniqueness on name/order index. Coupons of type `FREE_PRODUCT` target a category/item/variant.

### 4.5 Tokens (separate entities & lifecycles)

| Token | Storage | TTL | Single-use |
|---|---|---|---|
| Access JWT | stateless (HS256) | 15 min | revoked via Redis version bump |
| Refresh token | opaque 32-byte random, **SHA-256 hashed at rest** | 30 days | rotated on use |
| Email-verification | UUIDv4, cleartext | 24 h | enforced |
| Reset-password | UUIDv4, cleartext | 60 min | **NOT enforced — see C2** |
| Staff-invite | UUIDv4, cleartext | 72 h | enforced (new-user path only) |
| Ownership token | HS256 JWT, separate secret, `purpose`/`token_type` checked | 15 min | n/a |

### 4.6 Persistence strategy

- Liquibase is authoritative. `db.changelog-master.yaml` includes 5 XML changelogs:
  `001-baseline` (full schema + FKs/indexes/unique constraints), `002-coupons`,
  `003-currency-rename` (data-only `EURO→EUR`), `004-coupon-qr-redemption`,
  `005-customer-coupon-snapshot-names`.
- All FKs are `ON DELETE/UPDATE RESTRICT`; cascading deletes are handled explicitly in the
  service layer (e.g. business/customer account deletion), not by the database.
- Enums are intended to be persisted as `EnumType.STRING` (one unsafe exception — Known Issue C-P1).
- Money is `BigDecimal`/`DECIMAL`; points are `int`/`INT`.

---

## 5. Key Functional Flows

### 5.1 Earn points (purchase → points)

Endpoint: `POST /api/besahub/business/{businessId}/transactions/earn`
(staff/admin, requires an `Idempotency-Key` header).

1. **Idempotency gate (Redis):** `EarnPointsIdempotencyService.beginOrReplay` atomically
   `SETNX`s a `PROCESSING` record (TTL 2 min) keyed by
   `idem:earn:{businessId}:{idempotencyKey}`. `COMPLETED` → return cached response;
   `PROCESSING` → 409; payload-hash mismatch → conflict. The request hash is SHA-256 over
   `businessId | normalized billAmount | sorted guestIds`.
2. **Transactional core** loads business, performing `BusinessMember`, `LoyaltySettings`,
   `EarningSettings`.
3. **Optional coupon discount:** if a coupon QR is supplied, it is loaded with a pessimistic
   write lock, validated to belong to the business, and an `effectiveBillAmount` is computed.
4. **Points computation:**
   `points = floor(effectiveBillAmount / earningSettings.amountPer) * pointsPer`, capped by
   `loyaltySettings.maxPointsPerTransactions`.
5. **Multi-guest split:** `equalShare = points / guestCount`; the integer remainder is added
   entirely to the primary (first) guest.
6. **Persistence per request:** save `BillTransaction` (unique on
   `(business_id, idempotency_key)`) → mark coupon `USED` → for each guest: find-or-create
   `LoyaltyAccount`, add points, save an `EARN_BILL` `PointsTransaction`, create one `ACTIVE`
   `PointsBucket` (`expiresAt = now + monthsToExpire` if configured, else never).
7. **Idempotency completion:** an `AFTER_COMMIT` transactional event listener writes the
   `COMPLETED` record (TTL 3 days). The DB unique constraint on
   `(business_id, idempotency_key)` is the durable backstop; on
   `DataIntegrityViolationException` the original response is reconstructed (`REPLAYED`).

### 5.2 Coupon redemption

- **Issuance / customer redeem** (`CouponRedemptionServiceImpl.redeem`, role `CUSTOMER`): locks
  the template, validates ACTIVE + PUBLIC + within window + not sold out + per-customer limit +
  sufficient points; spends loyalty points (writes a `COUPON_REDEMPTION` `PointsTransaction` and
  bucket consumption), increments `totalRedemptions`, and creates a `CustomerCoupon` with a UUID
  QR code and value snapshot.
- **Customers cannot self-consume:** the legacy `/use` route deliberately throws.
- **Free-product redemption (staff scan)** (`StaffCouponRedemptionServiceImpl.scanAndRedeem`,
  staff/admin): locks the `CustomerCoupon` by QR, enforces same-business, type=FREE_PRODUCT,
  status=REDEEMED, within validity, then **atomically** flips to `USED` recording staff,
  location, channel.
- **Discount redemption (earn-points flow):** the coupon is resolved & validated under lock in
  `EarnPointsTransactionServiceImpl`, the discount is applied to the bill, and the coupon is
  marked `USED` (see Known Issue C-C1 — the mark uses a second, unlocked lookup).
- **Availability** (`CouponAvailabilityServiceImpl`): lists redeemable templates annotated with
  `canRedeem` and a `CouponCannotRedeemCode`.
- **Template expiry:** `CouponExpiryScheduler` runs hourly and flips ACTIVE-but-past-`endDate`
  templates to `EXPIRED`. It does **not** touch owned `CustomerCoupon` rows.

### 5.3 Points expiry

`ExpirePointsBucketScheduler` runs daily at 02:00. `expireBuckets()` streams all `ACTIVE`
buckets past `expiresAt` (using `SKIP LOCKED`), marks each `EXPIRED`, decrements the account,
and writes an `EXPIRE` `PointsTransaction` — all inside a single transaction.

### 5.4 Authentication & authorization

- **Login** issues an access JWT (15 min; claims `sub`=email, `uid`, `ver`) + an opaque refresh
  token. Failed logins are recorded and lock the account per `LoginPolicy`.
- **Filter chain** (`JwtAuthenticationFilter`): extract bearer → parse/verify signature & expiry
  → compare token `ver` against Redis `auth:ver:<uid>` → load `UserPrincipal` →
  set `SecurityContext`.
- **URL authorization** (`SecurityConfig`): `permitAll` for `/auth/**`, `/activate`,
  `/accept-invitation`, `/reset-password`, swagger, uploads, assetlinks;
  `/api/besahub/admin/**` → `SUPER_ADMIN`; `/api/besahub/customer/**` → `CUSTOMER`;
  everything else authenticated. CSRF disabled (bearer API); CORS allows localhost +
  `*.trycloudflare.com` with credentials.
- **Business isolation** is enforced at the method level via
  `@PreAuthorize("@businessSecurity.hasAccess(#businessId, authentication, ROLES…)")`, which
  verifies a matching `BusinessMember` row **and** `UserStatus.ENABLED` **and**
  `BusinessStatus.ACTIVE`. Customer endpoints derive identity from the authenticated principal
  (no client-supplied customer id), which prevents IDOR on the customer surface.
- **Revocation:** password change/reset bump the Redis token-version (invalidates access JWTs)
  and revoke all refresh tokens; `/logout` revokes the presented refresh token; `/logout-all`
  revokes all.

---

## 6. Known Issues, Bugs & Flaws

> Severity scale: **Critical** (data corruption / auth bypass / boot failure),
> **High** (security weakness or correctness bug under normal use),
> **Medium** (correctness/robustness gap), **Low** (hygiene / minor).
> Each item cites a file; line numbers were accurate at analysis time.

### 6.1 Security

| ID | Sev | Finding |
|---|---|---|
| **C1** | Critical | **Hardcoded secrets committed to source.** `application.properties` contains the HS256 JWT signing key (`app.jwt.secret`), the ownership-token secret, the MySQL password (`root`), and a live Gmail app password. Anyone with repo access can forge access tokens for any user/role and bypass authentication entirely. **Rotate and externalize all secrets immediately** (env vars / secret manager). |
| **C2** | Critical | **Reset-password token is replayable.** `PasswordResetServiceImpl.validateTokenAndResetPassword` (line 60) checks only `getExpiryDate()`; it never checks `isUsed()` before resetting. The token remains usable for its full 60-minute TTL even after the password has been changed — a captured link can be replayed to re-set the victim's password. (Email-verification correctly enforces single-use; reset does not.) |
| **H1** | High | **Admin user management exposes raw JPA entities with no method-level guard.** `UserController.createUser` binds `@RequestBody User` (mass-assignment of `roles`, `status`, `passwordHash`, `emailVerified`); list/get endpoints return full `User` entities (leaking `passwordHash`). Protection relies solely on the `/admin/**` URL rule; add `@PreAuthorize` + DTOs and never bind/return the entity. |
| **H2** | High | **JWT validation does not bind token type/issuer.** Access tokens carry no `token_type`/issuer claim; any HS256 token signed with the (committed) secret is accepted as an access token. Signature and expiry *are* enforced. |
| **H3** | High | **Token-version revocation is fail-open.** The revocation counter lives only in Redis and is *initialized to "1" on a cache miss*. If Redis is flushed/recreated, previously revoked tokens (logout-all, password change) carrying `ver=1` become valid again. Also, `/logout` does not bump the version, while password change/reset do — inconsistent. |
| **H4-S** | High | **Permissive CORS with credentials.** `SecurityConfig` trusts `https://*.trycloudflare.com` with `setAllowCredentials(true)`. Anyone can create a free Cloudflare quick-tunnel subdomain, making attacker-controlled origins "trusted" for credentialed cross-site requests. |
| **M1** | Medium | **No refresh-token reuse detection.** Rotation revokes the old token but a replayed already-revoked token only errors — there is no token-family/session revocation, so stolen-refresh-token theft is not contained. |
| **M2** | Medium | **User enumeration / message leakage.** `resendVerification` distinguishes "user not found" vs "already verified"; `login` returns raw exception messages. `forget-password` is correctly silent. |
| **M3** | Medium | **Activation/reset tokens stored in cleartext.** Unlike refresh tokens (hashed), verification/reset tokens are stored unhashed; a DB read discloses all live tokens. Activation also auto-issues a full session. |
| **C3** | Medium (corrected) | **Malformed `@PreAuthorize("isAuthenticated")`** in `BusinessMemberRegistrationController:33`, `UserProfileController:25,31`, `AuthenticatedPasswordChangerController:24`. The correct SpEL is `isAuthenticated()`. Without parentheses Spring evaluates it as a property reference, which **throws and fails closed (denies access)** — so the practical effect is broken/erroring endpoints, *not* the privilege escalation an earlier draft suggested. These endpoints are still covered by `anyRequest().authenticated()`. Fix the annotation regardless. |
| **L** | Low | `UserPrincipal.isEnabled()` treats `PENDING_VERIFICATION` as enabled (email verification not enforced for API access); demo seeder creates a known-password SUPER_ADMIN gated only by a flag (no profile guard); login catches all exceptions and returns 400; actuator health `show-details=always`. |

### 6.2 Loyalty balance correctness & concurrency

| ID | Sev | Finding |
|---|---|---|
| **C-L1** | Critical | **Unlocked read-modify-write on `LoyaltyAccount` balances.** The earn path loads the account via `findByCustomerProfileAndBusiness` (no lock) and mutates `availablePoints`, even though a `findWithLock…` query and an `@Version` field both exist but are unused on this path. Concurrent earns/expiry for the same account can lose updates; the `OptimisticLockException` surfaces as a generic failure with no retry, permanently failing legitimate concurrent earns. |
| **C-L2** | Critical | **Idempotency completion depends on a single DB constraint.** `markCompleted` runs only in an `AFTER_COMMIT` listener with no failure handling. If Redis write fails or the JVM dies post-commit, the `PROCESSING` key expires (2 min) with no `COMPLETED` record; correctness then rests entirely on the `BillTransaction (business_id, idempotency_key)` unique constraint. The Redis idempotency tier is effectively advisory. |
| **H-L1** | High | **Expiry batch is one giant transaction with no isolation or distributed lock.** `expireBuckets()` streams *all* expired buckets in a single `@Transactional`; one bad row (e.g. NPE on a non-`EARN_BILL` source transaction's null bill) rolls back the entire night's expiry, and the next run reprocesses everything. On multi-instance deployments there is no ShedLock/leader election, so account mutations during expiry can also lose updates. |
| **H-L2** | High | **Stale `pointsRemaining` during expiry.** The scheduler reads `pointsRemaining` and calls `account.expire(...)` without re-reading the bucket under lock, racing committed redemptions on the same account → possible negative/inconsistent `availablePoints`. |
| **M-L1** | Medium | **Lossy & internally inconsistent points math.** Floor division then integer guest-split compounds rounding loss; the `PointsTransaction.moneyAmount` stores the *pre-discount* bill while points were computed on the *post-discount* amount, so coupon-discounted earn rows don't reconcile against their own points. |
| **M-L2** | Medium | **Unsolicited account creation.** `guestIds` are not validated to be enrolled at the business before find-or-create, letting staff create loyalty accounts / inject points for arbitrary customer ids (no cross-tenant *read* leak). |
| **L-L** | Low | Mixed clock sources (`LocalDateTime.now()` vs injected `Clock` vs DB `CURRENT_TIMESTAMP`) make expiry boundaries timezone-skewed and untestable; replay response is recomputed rather than read back (not byte-faithful if split logic changes). |

### 6.3 Coupon correctness & concurrency

| ID | Sev | Finding |
|---|---|---|
| **C-C1** | Critical | **Discount coupon can be double-redeemed.** In the earn flow the coupon is validated under a pessimistic lock, but `applyAndMarkCouponUsed` re-loads it via a *second, unlocked* `findById` to set `USED`. `CustomerCoupon` has no `@Version` and there is no atomic conditional update (`UPDATE … SET status=USED WHERE status=REDEEMED`), so two concurrent bills can both apply the same discount coupon. (The free-product staff-scan path is safe — it locks and flips status in one transaction.) |
| **H-C1** | High | **Per-customer redemption limit not serialized.** `countByCouponIdAndCustomerProfileId` is read outside any lock, so two concurrent redeems by the same customer can both pass and exceed `perCustomerRedemptionLimit`. (Off-by-one is correct; the *serialization* is missing. Total-limit/oversell is adequately guarded by the template lock + `@Version`.) |
| **H-C2** | High | **Owned coupons are never persisted as `EXPIRED`.** `CouponExpiryScheduler` only expires *templates*. `CustomerCouponStatus.EXPIRED` is set nowhere in `src/main`; expiry is re-derived at read time everywhere. The `EXPIRED` enum value is effectively dead and the `getCustomerCoupons` filter that excludes `EXPIRED` is a no-op (expired coupons always appear in the wallet). Either persist the status via a scheduler or formally document the derived-expiry design. |
| **M-C1** | Medium | **`applyCoupon` lost update.** Customer `applyCoupon` writes `orderId` on a `CustomerCoupon` with no lock, racing the staff-scan locked path (dirty write of `orderId`/`status`). Ownership is correctly checked, so no cross-customer leak. |
| **M-C2** | Medium | **Misconfigured FREE_PRODUCT coupon degrades silently.** If a FREE_PRODUCT template has no `freeProductDetails`, redemption snapshots null product info; staff scan returns null `snapshotProductId`. Creation should reject FREE_PRODUCT without details. |
| **L-C** | Low | Public template metadata (`validateRedemption`, coupon detail) is readable by any authenticated customer regardless of business relationship — likely intentional (public catalog) but ungated; multiple independent `LocalDateTime.now()` calls within one response can show boundary inconsistencies. |

### 6.4 Persistence & schema alignment

| ID | Sev | Finding |
|---|---|---|
| **C-P1** | High | **`LoyaltyCard.status` has no `@Enumerated` → persisted as ORDINAL** into a `TINYINT` column (`LoyaltyCard.java:33-34`; baseline changelog `status TINYINT(3)`). Every other enum in the codebase is `STRING`. Reordering `LoyaltyCardStatus` silently corrupts the status of every card (a security-relevant field: REVOKED/SUSPENDED). Change to `@Enumerated(EnumType.STRING)` + `VARCHAR` via a new migration. |
| **H-P1** | High | **Repository ID type mismatch.** `PointsTransactionRepository extends JpaRepository<PointsTransaction, Integer>` but the entity `@Id` is `Long` (`PointsTransaction.java:28`). `findById`/`getReferenceById` with a `Long` or large id throws at runtime. Change the generic to `Long`. |
| **H-P2** | High | **`LoyaltyCard` → `CustomerProfile` uses `cascade = CascadeType.ALL` on the wrong side.** Removing/detaching a card cascades `REMOVE` into the entire `CustomerProfile`. The card is the child; remove the cascade. |
| **M-P1** | Medium | **Inner `JOIN FETCH` drops valid rows.** `PointsTransactionRepository.getPointsTransactionById` inner-joins `billTransaction`/`businessMember`, both nullable (e.g. EXPIRE rows have no bill) → returns `null` for adjustment/expiry transaction detail. Use `LEFT JOIN FETCH` (siblings already do). |
| **M-P2** | Medium | **`BusinessMember` status default disagreement.** Entity no-arg default is `MemberStatus.INVITE` while changelog default is `'ACTIVE'`; JPA-created vs SQL-created rows get different defaults. |
| **M-P3** | Medium | **Int overflow on lifetime points.** `lifetimeEarned` etc. are `int`/`INT` and accumulate forever (~2.1B ceiling); money columns are inconsistently sized (`DECIMAL(38,2)` vs `DECIMAL(12,2)`). |
| **M-P4** | Medium | **Storage/static handler fragility.** `LocalStorageService` resolves `uploads` against the JVM working directory and `UploadsStaticConfig` serves `file:uploads/`; in a container/systemd CWD these diverge. `UploadsStaticConfig` is not annotated `@Configuration` — verify static `/uploads/**` serving is actually registered. `app.base-url` is a Cloudflare tunnel baked into config — all emailed activation/reset/invite links and public asset URLs break when the tunnel rotates. |
| **L-P** | Low | `GlobalExceptionHandler` maps several constraint names that don't match the Liquibase-generated names (`uk_refresh_token_token_hash`, `uk_bill_transaction_business_invoice`, `uk_item_variant`) → those friendly-message branches are dead code; `RefreshToken.isExpired(now)` ignores its argument and calls `LocalDateTime.now()`; EAGER `User.roles` is a global N+1 on the hot auth path. |

### 6.5 Documentation hallucinations / drift

The repository's existing `.md` files contain several **unverified or contradicted claims** —
relevant because they would otherwise be cited in the thesis:

- `IMPLEMENTATION_SUMMARY.md` asserts "Project compiles successfully", "All unit tests pass",
  "No compilation errors or warnings." These are aspirational and partly contradicted by H-P1
  (the `Integer`/`Long` repository mismatch fails at runtime, not compile time). **Do not cite
  these as evidence of correctness.**
- `IMPLEMENTATION_SUMMARY.md`'s hard-deletion description is broadly accurate (the
  `deleteByBusinessId`/`deleteByCustomerProfileId` repository methods exist) but lists some
  deletions ("associated users", `CouponRepository`) not verified in service code — treat as
  unconfirmed.
- `README.md` seeder accounts are real but inert (`app.seed.demo.enabled=false`).
- `BUSINESS_STATUS_LIFECYCLE.md` and `SUPERADMIN_ENDPOINTS.md` are consistent with the
  `BusinessStatus`/`Role` enums and lifecycle code as far as sampled.

---

## 7. Prioritized Remediation Roadmap

1. **C1** — Rotate every committed secret; move to environment variables / a secret manager;
   purge from git history.
2. **C2** — Enforce `resetPasswordToken.isUsed()` (and ideally invalidate on first use) before
   resetting the password.
3. **C-L1 / C-C1** — Use the existing pessimistic-lock / `@Version` mechanisms on the *write*
   paths for `LoyaltyAccount` and `CustomerCoupon`; mark coupons `USED` inside the same locked
   transaction (or via atomic conditional `UPDATE`).
4. **C-L2 / H-L1** — Make idempotency completion durable; chunk the expiry batch with per-bucket
   commits, error isolation, and a distributed lock (ShedLock) for multi-instance safety.
5. **H1** — Replace raw-entity binding/return in admin user management with DTOs + explicit
   `@PreAuthorize`.
6. **C-P1 / H-P1 / H-P2 / M-P1** — Fix the enum-ordinal mapping (new migration), the repository
   generic type, the cascade, and the inner-join-fetch correctness bug.
7. **H2/H3/H4-S/M1** — Add JWT token-type/issuer binding, a durable revocation store, tighten
   CORS, add refresh-token reuse detection.
8. **H-C2** — Persist `CustomerCouponStatus.EXPIRED` via a scheduler, or document the
   derived-expiry design explicitly.

---

## 8. Testing Gaps

Only 8 test classes exist, concentrated on coupon redemption/availability and the discount
calculator. **No automated tests cover** the highest-risk areas: concurrent earn (lost-update),
idempotency replay/failure, points-bucket expiry batch behaviour, cross-business authorization,
token single-use enforcement, or schema/entity alignment. A thesis discussion of reliability
should note this as the primary engineering risk: the most safety-critical logic
(balance movement under concurrency) is the least tested.

---

## 9. Conclusion

BeLoyal is a well-structured, feature-sliced Spring Boot application with a clear layering
discipline, an explicit Liquibase-owned schema, and a thoughtfully designed loyalty ledger
(immutable transactions + FIFO expiry buckets + denormalized balance cache) and snapshot-based
coupon model. Its principal weaknesses are **not architectural but operational**: committed
secrets, replayable reset tokens, and a recurring pattern of *correct locking primitives existing
but not being applied on the write paths*, leaving balance and coupon flows vulnerable to lost
updates and double-spend under concurrency. Addressing Section 7 in order would bring the system
to production-grade safety without structural rework.
