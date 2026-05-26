<div align="center">

<img src="https://img.shields.io/badge/BeLoyal-API-4F46E5?style=for-the-badge&logoColor=white" alt="BeLoyal API" />

# BeLoyal — Loyalty Program Management API

**A production-grade REST API for managing multi-business loyalty programs.**  
Built with Spring Boot 3 · Java 21 · MySQL · Redis · JWT

<br/>

[![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.6-6DB33F?style=flat-square&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.x-4479A1?style=flat-square&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-latest-DC382D?style=flat-square&logo=redis&logoColor=white)](https://redis.io/)
[![Liquibase](https://img.shields.io/badge/Liquibase-4.29-2962FF?style=flat-square&logo=liquibase&logoColor=white)](https://www.liquibase.org/)
[![License](https://img.shields.io/badge/License-See_Repo-gray?style=flat-square)](./LICENSE)

</div>

---

## Table of Contents

- [About](#about)
- [Architecture](#architecture)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Environment Variables](#environment-variables)
- [Running Locally](#running-locally)
- [Database Migrations](#database-migrations)
- [API Overview](#api-overview)
- [Project Structure](#project-structure)
- [Author](#author)

---

## About

**BeLoyal API** is the backend service for the BesaHub loyalty platform. It allows businesses to set up and manage loyalty programs — including points earning rules, rewards, coupons, and customer management — through a secure REST API consumed by the BeLoyal mobile application.

The system supports multiple businesses operating independently within a single platform, with strict data isolation between each business's customers, loyalty accounts, and transactions.

---

## Architecture

The backend follows a standard layered architecture:

```
HTTP Request
     │
     ▼
┌─────────────┐
│  Controller │  ◄── Exposes REST endpoints, handles request/response mapping
└──────┬──────┘
       │
       ▼
┌─────────────┐
│   Service   │  ◄── Business logic, orchestration, validation
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ Repository  │  ◄── JPA data access via Spring Data
└──────┬──────┘
       │
       ▼
┌─────────────┐
│   MySQL DB  │  ◄── Schema managed by Liquibase migrations
└─────────────┘

         ┌────────┐
         │  Redis │  ◄── Caching and token management
         └────────┘
```

---

## Features

| Area | Capabilities |
|---|---|
| **Authentication** | JWT access tokens, refresh tokens, ownership tokens, password reset flows |
| **Business Management** | Business registration, onboarding workflow, status lifecycle |
| **Staff Management** | Email invitation flow, staff roles, business membership |
| **Loyalty Accounts** | Per-customer loyalty accounts linked to individual businesses |
| **Loyalty Cards** | Digital card management with status tracking |
| **Points Engine** | Earning rules, bill-based point registration, point buckets with expiry |
| **Rewards & Coupons** | Coupon creation, QR-code redemption, customer coupon tracking |
| **Catalog** | Product categories, catalog items, and item variants |
| **Customer Portal** | Customer-facing APIs for loyalty data, coupon lookup, and QR flows |
| **Super Admin** | Platform-level administration and business oversight |
| **Email Notifications** | SMTP transactional emails for invitations, activations, and resets |
| **File Uploads** | Local file storage for business profile assets |

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.6 |
| Security | Spring Security + JJWT 0.11.5 |
| Database | MySQL 8.x |
| ORM | Spring Data JPA / Hibernate |
| Migrations | Liquibase 4.29 |
| Caching | Redis (Spring Data Redis) |
| Email | Spring Mail (SMTP / Gmail) |
| Validation | Jakarta Bean Validation |
| Utilities | Lombok 1.18.38 |
| Build Tool | Maven (Maven Wrapper included) |
| Testing | Spring Boot Test |

---

## Prerequisites

Make sure the following are installed before running the project:

| Requirement | Minimum Version |
|---|---|
| Java JDK | 21 |
| Maven | 3.9+ (or use the included `./mvnw`) |
| MySQL | 8.x |
| Redis | 7.x (or run via Docker using the included Compose file) |

---

## Getting Started

### 1. Clone the repository

```bash
git clone <repository-url>
cd BeLoyal-API/BeLoyal
```

### 2. Create the MySQL database

Log into MySQL and run:

```sql
CREATE DATABASE besahub_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Start Redis

A Docker Compose file is included for convenience:

```bash
docker compose -f src/main/resources/docker-redis-compose.yml up -d
```

Alternatively, start your local Redis instance manually on port `6379`.

### 4. Set your environment variables

See the [Environment Variables](#environment-variables) section and configure them for your local environment.

### 5. Run the application

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

Check it is running:

```
GET http://localhost:8080/actuator/health
```

---

## Environment Variables

The application reads the following environment variables and falls back to local defaults when they are not set. In production, all secrets must be explicitly overridden.

| Variable | Local Default | Description |
|---|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/besahub_db` | MySQL connection URL |
| `DB_USERNAME` | `root` | Database username |
| `DB_PASSWORD` | `root` | Database password |
| `JWT_SECRET` | *(insecure default)* | Signing key for JWT access tokens — **rotate in production** |
| `OWNERSHIP_JWT_SECRET` | *(insecure default)* | Signing key for ownership tokens — **rotate in production** |
| `MAIL_USERNAME` | `digimenu10@gmail.com` | SMTP sender address |
| `MAIL_PASSWORD` | *(app password)* | SMTP password or Gmail app password |
| `APP_BASE_URL` | `http://localhost:8080` | Base URL used in email links and file URLs |
| `CORS_EXTRA_ORIGINS` | *(empty)* | Comma-separated list of additional allowed CORS origins |

You can provide variables inline when starting the app:

```bash
DB_USERNAME=myuser \
DB_PASSWORD=mypassword \
JWT_SECRET=my-secure-secret-at-least-32-chars \
./mvnw spring-boot:run
```

Or export them in your shell session / `.env` file before running.

---

## Running Locally

### Start the API

```bash
./mvnw spring-boot:run
```

### Run all tests

```bash
./mvnw test
```

### Run a specific test class or method

```bash
./mvnw -Dtest=ClassName#methodName test
```

### Package as a JAR

```bash
./mvnw package -DskipTests
java -jar target/BesaHub-0.0.1-SNAPSHOT.jar
```

### Enable demo seed data (optional)

To populate the database with sample businesses and customers for frontend development, set in `application.properties`:

```properties
app.seed.demo.enabled=true
```

---

## Database Migrations

Schema changes are managed by **Liquibase** and run automatically on startup. The `ddl-auto` is set to `validate`, so Hibernate will not modify the schema — all changes must go through a migration file.

```
src/main/resources/db/changelog/
├── db.changelog-master.yaml      ← master index of all changelogs
└── changes/
    ├── 001-baseline.xml
    ├── 002-coupons.xml
    ├── 003-currency-rename.xml
    ├── 004-coupon-qr-redemption.xml
    ├── 005-customer-coupon-snapshot-names.xml
    ├── 006-loyalty-card-status-string.xml
    ├── 007-user-token-version.xml
    └── ...
```

> **Rule:** Never edit an already-applied migration. Add a new numbered changelog file instead.

To generate a diff between JPA entities and the current database schema:

```bash
./mvnw liquibase:diff
```

---

## API Overview

All endpoints are prefixed with `/api`. Protected routes require a valid JWT Bearer token:

```
Authorization: Bearer <access_token>
```

| Prefix | Functional Area |
|---|---|
| `/api/auth/**` | Login, registration, token refresh, password reset, activation |
| `/api/businesses/**` | Business management and onboarding lifecycle |
| `/api/business-members/**` | Staff roles and invitation management |
| `/api/loyalty-accounts/**` | Customer loyalty account access |
| `/api/loyalty-cards/**` | Loyalty card issuance and status |
| `/api/points/**` | Bill registration, point transactions, bucket management |
| `/api/earning-settings/**` | Earning rule configuration per business |
| `/api/loyalty-settings/**` | Loyalty program settings per business |
| `/api/coupons/**` | Coupon creation, listing, and QR redemption |
| `/api/catalog/**` | Categories, products, and variants |
| `/api/customer/**` | Customer-facing loyalty portal APIs |
| `/api/dashboard/**` | Dashboard summary data for business users |
| `/api/admin/**` | Super admin platform management |

---

## Project Structure

```
src/main/java/com/shabanaj/beloyal/
├── features/
│   ├── auth/                     # Login, JWT, registration, activation
│   ├── business/                 # Business entity and lifecycle management
│   ├── businessMember/           # Staff membership and invitation flows
│   ├── loyaltyAccount/           # Per-customer loyalty balances
│   ├── loyaltyCard/              # Loyalty card management
│   ├── pointsTransaction/        # Point earning and redemption records
│   ├── pointsBucket/             # Point expiry buckets
│   ├── pointsBucketConsumption/  # Bucket consumption tracking
│   ├── coupon/                   # Coupon management
│   ├── couponLookup/             # QR and code-based coupon lookup
│   ├── customerCoupon/           # Customer-owned coupons
│   ├── earningSettings/          # Points earning rules per business
│   ├── loyaltySettings/          # Loyalty program configuration
│   ├── catalogCategories/        # Product categories
│   ├── catalogItems/             # Products and menu items
│   ├── catalogItemVariants/      # Item variants and options
│   ├── registerLoyaltyPoints/    # Bill-to-points registration flow
│   ├── billTransaction/          # Bill transaction records
│   ├── customerApis/             # Customer-facing portal endpoints
│   ├── customerLookup/           # Customer lookup by code or QR
│   ├── dashboard/                # Dashboard summary endpoints
│   ├── superadmin/               # Platform-wide admin
│   ├── seeding/                  # Demo data seeding
│   ├── token/                    # Token lifecycle management
│   ├── passwordChanger/          # Password change and reset
│   ├── registration/             # User onboarding
│   └── user/ / userProfiles/     # User entity and profile management
│
├── common/
│   ├── config/                   # Spring configuration beans
│   ├── security/                 # JWT filters and Spring Security config
│   ├── exception/                # Global exception handling
│   ├── storage/                  # Local file storage service
│   ├── email/                    # Email delivery service
│   └── redis/                   # Redis configuration and access
│
└── model/                        # Shared JPA entities and enums

src/main/resources/
├── application.properties        # Application configuration
├── docker-redis-compose.yml      # Redis Docker Compose
└── db/changelog/                 # Liquibase migration files
```

---

## Author

<div align="center">

**Shpëtim Shabanaj**

*BSc Software Engineering — BesaHub Loyalty Platform*

</div>
