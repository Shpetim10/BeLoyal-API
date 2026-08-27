# Business Status Lifecycle & Display

This document describes the business status lifecycle, what each status means, and how it is displayed to business admins and staff during login and profile refresh.

## Business Statuses

### 1. **ACTIVE**
- **Display Name:** "Active"
- **Description:** "Business can be seen and can operate transactions!"
- **Meaning:** The business is fully operational. All features are available and transactions can be processed.
- **User Permissions:** Admins and staff can access all business features, manage customers, process loyalty transactions, and view reports.
- **Visibility:** Always shown to authorized users.

### 2. **INACTIVE**
- **Display Name:** "Inactive"
- **Description:** "Business cannot be seen and cannot operate transactions!"
- **Meaning:** The business is temporarily disabled. This could be due to suspension, closure, or administrative action.
- **User Permissions:** The business is hidden from customer-facing features. Admins and staff cannot process transactions but can still view historical data.
- **Visibility:** Shown to admins and staff in their profile list but operations are restricted.

### 3. **PENDING_APPROVAL**
- **Display Name:** "Approval is pending"
- **Description:** "Your application is being verified by the support. This process may take a while!"
- **Meaning:** The business registration is under review by the support team. The business cannot operate until approved.
- **User Permissions:** No customer-facing access. Admins can view and complete setup but cannot process transactions until approval.
- **Visibility:** Shown to admins and staff; they should expect review delays.

### 4. **REJECTED**
- **Display Name:** "Rejected"
- **Description:** "Your application was rejected. Please contact support if you think there was something wrong."
- **Meaning:** The business application was rejected by the support team, typically due to compliance, documentation, or eligibility issues.
- **User Permissions:** No access to business features. Admins can contact support to appeal or resubmit.
- **Rejection Reason:** Only visible to business admins (not staff), includes details from the support team explaining the rejection.
- **Visibility:** Shown to all authorized users; admins see the rejection reason.

### 5. **BANNED**
- **Display Name:** "Banned"
- **Description:** "Your business was banned!"
- **Meaning:** The business has been permanently banned from the platform due to severe compliance violations or abuse.
- **User Permissions:** Complete access revocation. No staff or admin access to any features.
- **Visibility:** Shown to authenticated users; admins should seek support explanation.

---

## Status Display Rules

### During Login (`POST /api/besahub/auth/login`)

When a user logs in, each business they are associated with appears in the `businessProfiles` array with the following information:

- `businessStatus` - Enum name (e.g., "ACTIVE", "PENDING_APPROVAL")
- `statusDisplayName` - User-friendly name (e.g., "Active", "Approval is pending")
- `statusDescription` - Clear explanation of what the status means
- `rejectionReason` - **Only included if:**
  - The status is "REJECTED"
  - The user's role is "ADMIN"

### During Refresh (`POST /api/besahub/auth/refresh`)

The refresh endpoint returns the same business profile information as login, allowing clients to update their cached view of business status.

### Visibility Rules

| Status | Shown to Staff | Shown to Admins | Rejection Reason Visible |
|--------|---|---|---|
| ACTIVE | ✓ | ✓ | N/A |
| INACTIVE | ✓ | ✓ | N/A |
| PENDING_APPROVAL | ✓ | ✓ | N/A |
| REJECTED | ✓ | ✓ | ✓ (Admins only) |
| BANNED | ✓ | ✓ | N/A |

---

## Updated API Contracts

### POST /api/besahub/auth/login

**Response Body (LoginResponse):**
```json
{
  "userId": 123,
  "accessToken": "eyJhbGc...",
  "refreshToken": "refresh_token_value",
  "accessTokenExpiresInSeconds": 900,
  "roles": ["ADMIN"],
  "emailVerified": true,
  "customerProfileComplete": false,
  "businessProfiles": [
    {
      "businessId": 456,
      "businessName": "My Restaurant",
      "role": "ADMIN",
      "active": true,
      "businessStatus": "ACTIVE",
      "statusDisplayName": "Active",
      "statusDescription": "Business can be seen and can operate transactions!",
      "rejectionReason": null,
      "memberStatus": "ACTIVE",
      "invitationAccepted": true,
      "earningSettingsEnabled": true,
      "earningSettingsConfigured": true,
      "loyaltySettingsEnabled": true,
      "loyaltySettingsConfigured": true,
      "currency": "EUR"
    },
    {
      "businessId": 789,
      "businessName": "New Cafe",
      "role": "ADMIN",
      "active": false,
      "businessStatus": "REJECTED",
      "statusDisplayName": "Rejected",
      "statusDescription": "Your application was rejected. Please contact support if you think there was something wrong.",
      "rejectionReason": "Missing required documentation for VAT registration",
      "memberStatus": "ACTIVE",
      "invitationAccepted": true,
      "earningSettingsEnabled": false,
      "earningSettingsConfigured": false,
      "loyaltySettingsEnabled": false,
      "loyaltySettingsConfigured": false,
      "currency": "EUR"
    }
  ]
}
```

**New Fields Added:**
- `statusDisplayName` (string) - User-friendly status name
- `statusDescription` (string) - Explanation of the status

---

### POST /api/besahub/auth/refresh

**Request Body (RefreshRequest):**
```json
{
  "refreshToken": "refresh_token_value"
}
```

**Response Body (LoginResponse):**
Same structure as login endpoint (see above).

---

## Implementation Notes

- **Admin Check:** The rejection reason is conditionally included based on the user's role (`ADMIN`). Staff members with other roles will not see this field.
- **Backward Compatibility:** Existing clients can ignore the new fields (`statusDisplayName`, `statusDescription`) without breaking.
- **Metadata Source:** Status display names and descriptions are sourced from the `BusinessStatus` enum, ensuring consistency across the application.
- **No Action Hints:** Status fields provide information only; UI clients are responsible for interpreting status and determining available actions.

---

## Related Files

- **DTO:** `src/main/java/com/shabanaj/beloyal/features/auth/dto/BusinessProfileInfo.java`
- **Policy:** `src/main/java/com/shabanaj/beloyal/features/auth/policy/BusinessAccessPolicy.java`
- **Enum:** `src/main/java/com/shabanaj/beloyal/model/Enums/BusinessStatus.java`
- **Controller:** `src/main/java/com/shabanaj/beloyal/features/auth/controller/AuthController.java`
- **Services:** `src/main/java/com/shabanaj/beloyal/features/auth/service/impl/LoginServiceImpl.java`, `AuthenticationServiceImpl.java`
