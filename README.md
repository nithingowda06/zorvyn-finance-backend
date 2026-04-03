# Zorvyn Finance Backend

## 🚀 Overview
The **Zorvyn Finance Backend** is a high-performance financial data processing system built with Spring Boot and MongoDB. Architected for scalability and security, this backend showcases advanced data modeling, analytical thinking, and role-based access control.

---

## 🔐 Role-Based Access Control (RBAC)
The system enforces a strict permission matrix using Spring Security Method-Level validation:

| Capability | Viewer | Analyst | Admin |
| :--- | :---: | :---: | :---: |
| **View Dashboard Summary** | ✅ | ✅ | ✅ |
| **View Trends & Breakdown** | ❌ | ✅ | ✅ |
| **View Financial Records** | ✅ | ✅ | ✅ |
| **Create/Update Records** | ❌ | ✅ | ✅ |
| **Delete Records (Soft)** | ❌ | ❌ | ✅ |
| **Manage Users & Roles** | ❌ | ❌ | ✅ |

---

## 📊 Analytical Capabilities
Unlike basic CRUD systems, this backend provides deep insights into financial health:
1.  **Dynamic Trending**: `/api/dashboard/trends` supports both `monthly` and `weekly` aggregation logic.
2.  **Category Intelligence**: `/api/dashboard/category-breakdown` provides a detailed Income vs. Expense analysis per category.
3.  **Real-time Activity**: `/api/dashboard/recent-activity` tracks the latest 10 transactions with audit-ready details (`createdBy`).
4.  **Industry Filtering**: Advanced filtering on `/api/records` using `type`, `category`, and precise `from/to` date ranges.

---

## 🧠 Design Choices & Assumptions

### 1. Account Deactivation vs. Physical Delete
For financial auditability, the system uses a **Soft Deactivation** approach for users (`toggle-status`). This ensures history is preserved while blocking access. If a user is explicitly "Deleted", we prefix their username (`DELETED_`) to free up the original namespace for future registration.

### 2. Audit Trial (`createdBy`)
The `createdBy` field for financial entries is extracted directly from the **JWT SecurityContext** at the service layer, preventing any client-side tampering or spoofing of data ownership.

### 3. Data Seeding Assumption
- The app automatically seeds default users: `admin`, `analyst`, and `viewer` on the first run.
- **Specific User**: A production-ready account `nithin` (password: `nithin`) is also seeded for immediate testing.

---

## 📈 API Specification Highlights

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

## 🧪 Quick Run & Test
1. Ensure **MongoDB** is running on `:27017`.
2. Run `mvn clean install` then `mvn spring-boot:run`.
3. Test via **[Swagger UI](http://localhost:8080/swagger-ui.html)**.
4. Or import the included **`zorvyn-finance-backend.postman_collection.json`** into Postman.

---
**Developed by Nithin M**
*Finance Backend Developer Assignment - Zorvyn FinTech*
