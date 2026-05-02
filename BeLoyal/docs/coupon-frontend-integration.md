# Coupon Backend Integration Guide (Frontend)

This document is based on the current backend implementation under:
- `features/coupon`
- `features/couponLookup`
- `common/image_upload`

## 1) Base Path and Auth

- Base path prefix: `/api/besahub`
- Business coupon routes use singular `business`, not `businesses`:
  - `/api/besahub/business/{businessId}/...`
- All business coupon endpoints require authenticated user with `BUSINESS_ADMIN` access to that business.

## 2) Enums (Source of Truth)

### `CouponType`
- `FREE_PRODUCT`
- `PERCENTAGE_DISCOUNT`
- `FIXED_AMOUNT_DISCOUNT`

### `CouponStatus`
- `DRAFT`
- `ACTIVE`
- `PAUSED`
- `EXPIRED`
- `ARCHIVED`

### `CouponVisibility`
- `PUBLIC`
- `HIDDEN`

### `CatalogStatus` (lookup payloads)
- `ACTIVE`
- `INACTIVE`

### `CurrencyCode` (coupon response)
- `LEK`
- `DOLLAR`
- `EURO`

Note:
- Coupon `currency` in coupon APIs is enum value (`LEK`, `DOLLAR`, `EURO`).
- Variant lookup `currency` is a string symbol/code from product currency (`ALL`, `$`, `EUR symbol`).

Frontend display mapping recommendation:
- `LEK` -> `ALL`
- `DOLLAR` -> `$`
- `EURO` -> `EUR symbol`

## 3) Enum Color Mapping (Must use `colors.dart`)

Use centralized color tokens from `colors.dart` (do not hardcode colors in widgets/components).

Recommended mapping:

### Coupon status colors
- `DRAFT` -> neutral gray (`couponStatusDraft`)
- `ACTIVE` -> success green (`couponStatusActive`)
- `PAUSED` -> warning amber (`couponStatusPaused`)
- `EXPIRED` -> danger red (`couponStatusExpired`)
- `ARCHIVED` -> slate/muted (`couponStatusArchived`)

### Coupon type colors
- `FREE_PRODUCT` -> brand blue (`couponTypeFreeProduct`)
- `PERCENTAGE_DISCOUNT` -> violet/indigo (`couponTypePercentage`)
- `FIXED_AMOUNT_DISCOUNT` -> teal (`couponTypeFixedAmount`)

### Visibility colors
- `PUBLIC` -> green (`couponVisibilityPublic`)
- `HIDDEN` -> gray (`couponVisibilityHidden`)

Example Dart pattern:

```dart
// Keep all UI state colors centralized in colors.dart
Color couponStatusColor(CouponStatus status) {
  switch (status) {
    case CouponStatus.DRAFT:
      return AppColors.couponStatusDraft;
    case CouponStatus.ACTIVE:
      return AppColors.couponStatusActive;
    case CouponStatus.PAUSED:
      return AppColors.couponStatusPaused;
    case CouponStatus.EXPIRED:
      return AppColors.couponStatusExpired;
    case CouponStatus.ARCHIVED:
      return AppColors.couponStatusArchived;
  }
}
```

## 4) API Endpoints (Business Dashboard)

## 4.1 Upload Coupon Image

- `POST /api/besahub/business/{businessId}/media/coupon-images`
- Content-Type: `multipart/form-data`
- Part name: `file`
- Allowed file types: `image/jpeg`, `image/png`, `image/webp`
- Max size for coupon image: `5MB`

Success response `200`:

```json
{
  "url": "https://...",
  "key": "businesses/1/coupons/uuid.jpg",
  "contentType": "image/jpeg",
  "sizeBytes": 123456
}
```

Frontend should send returned `url` in coupon create/update as `imageUrl`.

## 4.2 Lookup Active Categories

- `GET /api/besahub/business/{businessId}/lookups/categories`

Success `200`:

```json
{
  "data": [
    {
      "id": 10,
      "name": "Coffee",
      "status": "ACTIVE"
    }
  ]
}
```

## 4.3 Lookup Active Products by Category

- `GET /api/besahub/business/{businessId}/lookups/products?categoryId={categoryId}`

Success `200`:

```json
{
  "data": [
    {
      "id": 100,
      "name": "Cappuccino",
      "categoryId": 10,
      "imageUrl": "https://...",
      "status": "ACTIVE"
    }
  ]
}
```

## 4.4 Lookup Active Variants by Product

- `GET /api/besahub/business/{businessId}/lookups/products/{productId}/variants`

Success `200`:

```json
{
  "data": [
    {
      "id": 1000,
      "name": "Large",
      "status": "ACTIVE",
      "price": 4.5,
      "currency": "ALL"
    }
  ]
}
```

If no variants, backend returns empty `data` array.

## 4.5 Create Coupon

- `POST /api/besahub/business/{businessId}/coupons`
- Body: `CouponCreateRequest`

Common fields:
- `type` (required)
- `title` (required, max 200)
- `description` (optional, max 1000)
- `imageUrl` (optional, max 500)
- `pointsCost` (required, min 1)
- `startDate` (required)
- `endDate` (required)
- `totalRedemptionLimit` (optional, min 1)
- `perCustomerRedemptionLimit` (optional, min 1)
- `status` (required)
- `visibility` (required)
- `termsAndConditions` (optional, max 2000)
- `isFeatured` (optional)
- `sortOrder` (optional)

Type-specific fields:
- `FREE_PRODUCT`: `categoryId` required, `productId` required, `variantId` optional, `quantity` optional (defaults to `1`, min 1)
- `PERCENTAGE_DISCOUNT`: `discountPercentage` required (0 < x <= 100)
- `FIXED_AMOUNT_DISCOUNT`: `discountAmount` required (> 0)
- Discount types can also include `minimumOrderAmount` (>= 0), `maximumDiscountAmount` (> 0)

FREE_PRODUCT example:

```json
{
  "type": "FREE_PRODUCT",
  "title": "Free Cappuccino",
  "description": "",
  "imageUrl": null,
  "pointsCost": 250,
  "categoryId": 10,
  "productId": 100,
  "variantId": 1000,
  "quantity": 1,
  "startDate": "2026-05-01T00:00:00",
  "endDate": "2026-06-01T00:00:00",
  "totalRedemptionLimit": 500,
  "perCustomerRedemptionLimit": 1,
  "status": "DRAFT",
  "visibility": "PUBLIC",
  "termsAndConditions": "Valid once per customer."
}
```

Success `201`: returns `CouponDetailResponse` (section 5.2).

Backend behavior:
- Currency is always resolved from business and stored server-side.
- If free product `imageUrl` is empty/null, backend falls back to selected product image.
- If `description` is empty/null, backend auto-generates description.

## 4.6 List Coupons

- `GET /api/besahub/business/{businessId}/coupons`
- Query params:
  - `status` optional (`CouponStatus`)
  - `type` optional (`CouponType`)
  - `search` optional (matches title)
  - `page` default `0`
  - `limit` default `20`
  - `sortBy` default `createdAt`; allowed: `title`, `pointsCost`, `status`, `startDate`, `endDate`, `createdAt`
  - `sortDirection` default `DESC`

Success `200`: Spring `Page<CouponSummaryResponse>`.

Typical shape:

```json
{
  "content": [
    {
      "id": 1,
      "type": "FREE_PRODUCT",
      "title": "Free Cappuccino",
      "imageUrl": "https://...",
      "pointsCost": 250,
      "currency": "EURO",
      "status": "ACTIVE",
      "visibility": "PUBLIC",
      "startDate": "2026-05-01T00:00:00",
      "endDate": "2026-06-01T00:00:00",
      "totalRedemptionLimit": 500,
      "totalRedemptions": 30,
      "isFeatured": true,
      "createdAt": "2026-05-01T10:00:00"
    }
  ],
  "number": 0,
  "size": 20,
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true,
  "empty": false
}
```

## 4.7 Get Coupon Detail

- `GET /api/besahub/business/{businessId}/coupons/{couponId}`
- Success `200`: `CouponDetailResponse`

## 4.8 Update Coupon (Partial)

- `PATCH /api/besahub/business/{businessId}/coupons/{couponId}`
- Body: `CouponUpdateRequest` (all fields optional patch semantics)

Important update constraints:
- If coupon already has redemptions:
  - cannot change `pointsCost`
  - for free product coupon cannot change `variantId`
  - `totalRedemptionLimit` cannot be lower than current `totalRedemptions`
- For free product coupons, only `variantId` and `quantity` are updateable in details.
- For discount coupons, discount fields are updateable.

## 4.9 Change Coupon Status

- `PATCH /api/besahub/business/{businessId}/coupons/{couponId}/status`

Request:

```json
{
  "status": "ACTIVE"
}
```

Allowed transitions:
- `DRAFT` -> `ACTIVE`, `ARCHIVED`
- `ACTIVE` -> `PAUSED`, `EXPIRED`, `ARCHIVED`
- `PAUSED` -> `ACTIVE`, `ARCHIVED`
- `EXPIRED` -> `ARCHIVED`
- `ARCHIVED` -> (none)

If activating, `endDate` must not be in the past.

## 4.10 Archive Coupon

- `PATCH /api/besahub/business/{businessId}/coupons/{couponId}/archive`
- Success `204`

## 4.11 Delete Coupon (Soft Delete)

- `DELETE /api/besahub/business/{businessId}/coupons/{couponId}`
- Success `204`

## 5) Response Models

## 5.1 `CouponSummaryResponse`

```json
{
  "id": 1,
  "type": "FREE_PRODUCT",
  "title": "Free Cappuccino",
  "imageUrl": "https://...",
  "pointsCost": 250,
  "currency": "EURO",
  "status": "ACTIVE",
  "visibility": "PUBLIC",
  "startDate": "2026-05-01T00:00:00",
  "endDate": "2026-06-01T00:00:00",
  "totalRedemptionLimit": 500,
  "totalRedemptions": 30,
  "isFeatured": true,
  "createdAt": "2026-05-01T10:00:00"
}
```

## 5.2 `CouponDetailResponse`

```json
{
  "id": 1,
  "businessId": 55,
  "type": "FREE_PRODUCT",
  "title": "Free Cappuccino",
  "description": "Redeem this coupon for one free Large Cappuccino using 250 loyalty points.",
  "imageUrl": "https://...",
  "pointsCost": 250,
  "currency": "EURO",
  "status": "ACTIVE",
  "visibility": "PUBLIC",
  "startDate": "2026-05-01T00:00:00",
  "endDate": "2026-06-01T00:00:00",
  "totalRedemptionLimit": 500,
  "totalRedemptions": 30,
  "perCustomerRedemptionLimit": 1,
  "termsAndConditions": "Valid once per customer.",
  "isFeatured": true,
  "sortOrder": 1,
  "createdAt": "2026-05-01T10:00:00",
  "updatedAt": "2026-05-03T09:00:00",
  "freeProductDetails": {
    "categoryId": 10,
    "categoryName": "Coffee",
    "productId": 100,
    "productName": "Cappuccino",
    "variantId": 1000,
    "variantName": "Large",
    "quantity": 1
  },
  "discountDetails": null
}
```

For discount coupons, `freeProductDetails` is `null` and `discountDetails` contains:
- `discountPercentage`
- `discountAmount`
- `minimumOrderAmount`
- `maximumDiscountAmount`

## 6) Date/Time Format

Backend DTOs use `LocalDateTime`.

Use ISO local datetime strings in requests:
- `YYYY-MM-DDTHH:mm:ss`
- Example: `2026-05-01T00:00:00`

## 7) Frontend Validation Checklist

Mirror backend validation client-side:
- `type`, `title`, `pointsCost`, `startDate`, `endDate`, `status`, `visibility` required
- `pointsCost >= 1`
- `startDate < endDate`
- `quantity >= 1` when provided
- `discountPercentage > 0 && <= 100`
- `discountAmount > 0`
- `minimumOrderAmount >= 0`
- `maximumDiscountAmount > 0` when provided

FREE_PRODUCT flow:
- Require category before enabling product dropdown
- Require product before enabling variant dropdown
- Reset product when category changes
- Reset variant when product changes

## 8) Error Response Shapes

There are two error body shapes in current backend:

### Shape A (most `ApiException` and generic errors)

```json
{
  "timestamp": "2026-05-02T12:00:00Z",
  "status": 404,
  "message": "Coupon not found: 123",
  "path": "/api/besahub/business/55/coupons/123"
}
```

### Shape B (`BadRequestException` handler)

```json
{
  "message": "Selected product is not active"
}
```

Frontend should parse `message` defensively from either shape.

## 9) Backend Behaviors to Reflect in UI

- No separate currency field in create/update request.
- Display money labels based on business currency from coupon response (`currency`) and business context.
- Free-product variant is optional in current backend (no explicit "variant required" contract yet).
- Coupon `EXPIRED` can be set automatically by scheduler (hourly) when active coupon passes `endDate`.

## 10) Integration Differences vs Draft Prompt

Important differences from the draft integration prompt:
- Route is `/business/{businessId}` (singular), not `/businesses/{businessId}`.
- IDs are numeric (`Long`), not string IDs like `cat_123`.
- Lookup responses are wrapped as `{ "data": [...] }`.
- Date fields are `LocalDateTime` (use `YYYY-MM-DDTHH:mm:ss`).
- Error payload currently does not expose stable machine error codes like `CATEGORY_NOT_ACTIVE`; it exposes message text.
