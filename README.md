# 💰 Zorvyn Finance Backend

[![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-Latest-green?style=for-the-badge&logo=mongodb)](https://www.mongodb.com/)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](https://opensource.org/licenses/MIT)

## 🚀 Overview
The **Zorvyn Finance Backend** is a high-performance, production-grade financial data processing system. Built with modern Java standards and Spring Boot, it delivers a secure and scalable foundation for financial management applications. 

This project demonstrates expertise in **Domain-Driven Design (DDD)**, **Restful API architecture**, and **Enterprise Security patterns**.

---

## 🛠️ Tech Stack
*   **Language:** Java 17
*   **Framework:** Spring Boot 3.x
*   **Security:** Spring Security + JWT (JSON Web Token)
*   **Database:** MongoDB
*   **Data Access:** Spring Data MongoDB
*   **Documentation:** Swagger / OpenAPI 3.0
*   **Build Tool:** Maven

---

## ✨ Features
-   🔐 **JWT-Based Authentication**: Secure stateless authentication using industry-standard tokens.
-   🛡️ **Role-Based Access Control (RBAC)**: Fine-grained permissions for Admins, Analysts, and Viewers.
-   📊 **Advanced Analytics**: Real-time trending, category breakdowns, and summary insights.
-   📑 **Pagination & Filtering**: Efficient data retrieval using industry-standard query parameters.
-   💾 **Caching Strategy**: High-performance dashboard analytics with intelligent cache eviction.
-   🗑️ **Soft Delete Strategy**: Data integrity preserved through non-destructive record deletion.
-   ⚠️ **Global Exception Handling**: Standardized error responses across all API endpoints.
-   ✅ **Request Validation**: Strict JSR-303/JSR-380 validation for all incoming data.

---

## 🏗️ Project Structure
```text
src/main/java/com/zorvyn/finance/
├── config        # Configuration classes (Swagger, Caching)
├── controller    # REST API Endpoints
├── dto           # Request/Response Data Transfer Objects
├── entity        # MongoDB Collections & Data Models
├── exception     # Global Exception Handling Logic
├── repository    # Spring Data MongoDB Repositories
├── security      # JWT Implementation & Security Filters
├── service       # Business Logic & Service Layer
└── util          # Shared Constants & Utility Helpers
```

---

## 🔐 Permission Matrix (RBAC)
The system enforces a strict permission matrix using **Spring Security Method-Level validation**:

| Capability | Viewer | Analyst | Admin |
| :--- | :---: | :---: | :---: |
| **View Dashboard Summary** | ✅ | ✅ | ✅ |
| **View Trends & Breakdown** | ❌ | ✅ | ✅ |
| **View Financial Records** | ✅ | ✅ | ✅ |
| **Create/Update Records** | ❌ | ✅ | ✅ |
| **Delete Records (Soft)** | ❌ | ❌ | ✅ |
| **Manage Users & Roles** | ❌ | ❌ | ✅ |

---

## 🧠 Design Choices & Assumptions

### 1. Account Deactivation vs. Physical Delete
For financial auditability, the system uses a **Soft Deactivation** approach for users (`toggle-status`). This ensures history is preserved while blocking access. If a user is explicitly "Deleted", we prefix their username (`DELETED_`) to free up the original namespace for future registration.

### 2. Audit Trail (`createdBy`)
The `createdBy` field for financial entries is extracted directly from the **JWT SecurityContext** at the service layer, preventing any client-side tampering or spoofing of data ownership.

### 3. Data Seeding Strategy
- The app automatically seeds default users: `admin`, `analyst`, and `viewer` on the first run.
- **Test Credentials**: A production-ready account `nithin` (password: `nithin`) is seeded for immediate evaluation.

---

## 💻 How to Run Locally

### Prerequisites
- **Java 17** installed.
- **Maven** installed.
- **MongoDB** running on `localhost:27017`.

### Steps to Run
1.  **Clone the repository**:
    ```bash
    git clone https://github.com/nithingowda06/zorvyn-finance-backend.git
    cd zorvyn-finance-backend
    ```
2.  **Build the project**:
    ```bash
    mvn clean install
    ```
3.  **Run the application**:
    ```bash
    mvn spring-boot:run
    ```
4.  **Access Swagger Documentation**:
    Open [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) to explore the API.

---

## 🧪 Testing with Postman
A pre-configured Postman collection is included in the project root:
1.  Locate `zorvyn-finance-backend.postman_collection.json`.
2.  Import it into Postman (**File -> Import**).
3.  The collection includes pre-configured environment variables for easy authentication and testing.

---

## 🌟 Why This Project Stands Out (Recruiter Insights)
-   **Production Readiness**: Implements real-world patterns like soft deletes, caching, and custom exception handling.
-   **Security First**: RBAC is handled at the method level using standard Spring Security annotations.
-   **High Observability**: Fully documented with Swagger, making it easy for frontend developers to integrate.
-   **Scalable Architecture**: Decoupled layers (Controller -> Service -> Repository) ensure maintainability and testability.

---

## 📈 API Specification Summary

### 🧾 Financial Records (`/api/records`)
- `GET /`: Retrieve records with **Industry-Standard Pagination** and **Dynamic Filtering**.
- `GET /{id}`: Retrieve full details for a specific record.
- `POST /`: Create a new entry (automatically assigns `createdBy`).
- `PUT /{id}`: Update an existing entry.
- `DELETE /{id}`: Perform a **Soft Delete**.

### 📊 Dashboard Analytics (`/api/dashboard`)
- `GET /summary`: High-level totals (Cached for performance).
- `GET /trends?period=monthly|weekly`: Time-series data for growth analysis.
- `GET /category-breakdown`: Distribution of funds across various categories.
- `GET /recent-activity`: Snapshot of the latest system interactions.

---

**Developed by [Nithin M](https://github.com/nithingowda06)**
*Finance Backend Developer Assignment - Zorvyn FinTech*

