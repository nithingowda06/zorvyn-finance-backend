# 💰 Zorvyn Finance Backend

[![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-Latest-green?style=for-the-badge&logo=mongodb)](https://www.mongodb.com/)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](https://opensource.org/licenses/MIT)

## 🚀 Overview
The **Zorvyn Finance Backend** is a financial data processing and access control system built with Java 17 and Spring Boot. It provides a secure, role-based REST API for managing financial records, user access, and dashboard analytics.

This project was built as part of the Zorvyn FinTech Backend Developer Internship assessment.

---

## 🛠️ Tech Stack

| Layer | Technology |
| :--- | :--- |
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Security | Spring Security + JWT |
| Database | MongoDB |
| Data Access | Spring Data MongoDB |
| Documentation | Swagger / OpenAPI 3.0 |
| Build Tool | Maven |

---

## ✨ Features

- 🔐 **JWT Authentication** — Stateless token-based auth for all secured endpoints
- 🛡️ **Role-Based Access Control (RBAC)** — Method-level enforcement for ADMIN, ANALYST, VIEWER
- 📊 **Dashboard Analytics** — Summary totals, trends, category breakdown, recent activity
- 📑 **Pagination & Filtering** — Query params for type, category, date range, page, size
- 💾 **Caching** — Dashboard endpoints cached with Spring Cache for performance
- 🗑️ **Soft Delete** — Records and users are deactivated, not permanently removed
- ⚠️ **Global Exception Handling** — Consistent `{ status, message, timestamp }` error shape
- ✅ **Request Validation** — JSR-380 `@Valid` annotations on all incoming request bodies

---

## 🏗️ Project Structure
```text
src/main/java/com/zorvyn/finance/
├── config        # Security, JWT, Swagger, Cache configuration
├── controller    # REST API controllers
├── dto           # Request and Response DTOs
├── entity        # MongoDB document models
├── exception     # GlobalExceptionHandler + custom exceptions
├── repository    # Spring Data MongoDB repositories
├── security      # JWT filter, entry point, UserDetailsService, Role enum
├── service       # Business logic layer
└── util          # Constants and shared helpers
```

---

## 🔐 Permission Matrix (RBAC)

| Capability | Viewer | Analyst | Admin |
| :--- | :---: | :---: | :---: |
| View Dashboard Summary | ✅ | ✅ | ✅ |
| View Trends & Category Breakdown | ❌ | ✅ | ✅ |
| View Financial Records | ❌ | ✅ | ✅ |
| Create / Update Records | ❌ | ✅ | ✅ |
| Delete Records (Soft) | ❌ | ❌ | ✅ |
| Manage Users & Roles | ❌ | ❌ | ✅ |

> **Assumption:** VIEWER role is restricted to dashboard summary only. This reflects a read-only stakeholder who needs high-level visibility without access to raw transaction data.

---

## 🧠 Design Choices & Assumptions

### 1. Soft Delete for Users and Records
All deletes are non-destructive. For records, a `deletedAt` timestamp is set. For users, the username is prefixed with `DELETED_` to free the namespace while preserving history. This is important for financial auditability.

### 2. `createdBy` Extracted from JWT
The `createdBy` field on financial records is extracted from the JWT `SecurityContext` at the service layer — never from the request body. This prevents client-side spoofing of data ownership.

### 3. Inactive User Blocking
If a user's status is set to inactive via `toggle-status`, their JWT is rejected at the filter level even if the token is still valid. This ensures immediate access revocation.

### 4. Data Seeding on Startup
The application automatically seeds three default users on first run for immediate testing:

| Username | Password | Role |
| :--- | :--- | :--- |
| `admin` | `admin123` | ADMIN |
| `analyst` | `analyst123` | ANALYST |
| `viewer` | `viewer123` | VIEWER |

---

## ⚖️ Tradeoffs

| Decision | Reason | Production Alternative |
| :--- | :--- | :--- |
| MongoDB over relational DB | Flexible schema for evolving financial record fields | PostgreSQL for complex joins and strict ACID guarantees |
| In-memory cache (ConcurrentMapCache) | Simple setup, no external dependency | Redis for distributed caching across instances |
| No rate limiting | Out of scope for this assessment | Spring Rate Limiter or API Gateway throttling |
| Soft delete over hard delete | Preserves audit trail for financial data | Configurable purge policy after retention period |
| Embedded seeded users | Easier reviewer setup | Admin-only user creation endpoint in production |

---

## 💻 How to Run Locally

### Prerequisites
- Java 17
- Maven
- MongoDB running on `localhost:27017`

### Steps

1. **Clone the repository**
```bash
   git clone https://github.com/nithingowda06/zorvyn-finance-backend.git
   cd zorvyn-finance-backend
```

2. **Build the project**
```bash
   mvn clean install
```

3. **Run the application**
```bash
   mvn spring-boot:run
```

4. **Open Swagger UI**
http://localhost:8080/swagger-ui.html

5. **Authenticate in Swagger**
   - Call `POST /api/auth/login` with any seeded credentials above
   - Copy the returned token
   - Click **Authorize** (top right) and paste: `<token>` or `Bearer <token>`

---

## 🧪 Testing with Postman

A pre-configured Postman collection is included in the project root:

1. Locate `zorvyn-finance-backend.postman_collection.json`
2. Import into Postman via **File → Import**
3. Use the seeded credentials to authenticate and explore all endpoints

---

## 📈 API Reference

### 🔑 Auth (`/api/auth`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| POST | `/api/auth/register` | Public | Register a new user |
| POST | `/api/auth/login` | Public | Login and receive JWT token |

### 👤 User Management (`/api/users`) — Admin only
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| GET | `/api/users` | ADMIN | List all users |
| GET | `/api/users/{id}` | ADMIN | Get user by ID |
| POST | `/api/users` | ADMIN | Create a new user |
| PATCH | `/api/users/{id}/role` | ADMIN | Update user role |
| PATCH | `/api/users/{id}/toggle-status` | ADMIN | Activate or deactivate user |
| DELETE | `/api/users/{id}` | ADMIN | Soft delete a user |

### 🧾 Financial Records (`/api/records`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| GET | `/api/records` | ANALYST, ADMIN | List records with pagination and filters |
| GET | `/api/records/{id}` | ANALYST, ADMIN | Get a single record by ID |
| POST | `/api/records` | ANALYST, ADMIN | Create a new financial record |
| PUT | `/api/records/{id}` | ANALYST, ADMIN | Update an existing record |
| DELETE | `/api/records/{id}` | ADMIN | Soft delete a record |

**Supported query parameters for `GET /api/records`:**
- `type` — `INCOME` or `EXPENSE`
- `category` — e.g. `food`, `rent`, `salary`
- `from` — start date (`yyyy-MM-dd`)
- `to` — end date (`yyyy-MM-dd`)
- `page` — page number (default: 0)
- `size` — page size (default: 10)

### 📊 Dashboard (`/api/dashboard`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| GET | `/api/dashboard/summary` | VIEWER, ANALYST, ADMIN | Total income, expenses, net balance |
| GET | `/api/dashboard/trends` | ANALYST, ADMIN | Monthly or weekly time-series data |
| GET | `/api/dashboard/category-breakdown` | ANALYST, ADMIN | Totals grouped by category |
| GET | `/api/dashboard/recent-activity` | ANALYST, ADMIN | Latest 10 transactions |

---

## 🧪 Unit Tests

Service layer unit tests are written using **JUnit 5 + Mockito**, covering:
- `AuthService` — register, login, duplicate user, bad credentials
- `RecordService` — create, update, soft delete, filter logic
- `DashboardService` — summary calculations, empty data edge cases
- `UserService` — role assignment, inactive user handling

---

**Developed by [Nithin M](https://github.com/nithingowda06)**  
