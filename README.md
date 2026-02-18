# Gym Membership Management System (Java/Spring Boot)

A robust backend REST API developed with **Spring Boot** to manage gym memberships, member profiles, and subscription lifecycles. This project demonstrates clean architecture, JPA persistence, and automated API documentation.

---

## 🚀 Key Features

* **Member Management:** Full CRUD operations (Create, Read, Update, Delete) for gym members.
* **Subscription Tiers:** Logic to handle different membership levels (e.g., Monthly, Quarterly, Annual).
* **Status Tracking:** Real-time tracking of active, expired, and pending memberships.
* **Validation:** Server-side data validation using Spring Boot Starter Validation.
* **API Documentation:** Interactive API testing via Swagger UI/OpenAPI.

---

## 🛠️ Tech Stack

* **Language:** Java 17+
* **Framework:** Spring Boot 3.x
* **Database:** H2 (In-memory for development) / PostgreSQL support
* **ORM:** Spring Data JPA (Hibernate)
* **Documentation:** Springdoc-OpenAPI
* **Build Tool:** Maven

---

## 🏗️ Project Architecture

The project follows a standard N-tier architecture to ensure separation of concerns:



1.  **Controller Layer:** Handles HTTP requests and maps them to specific endpoints.
2.  **Service Layer:** Contains business logic and membership rules.
3.  **Repository Layer:** Interacts with the database using Spring Data JPA.
4.  **Model/Entity Layer:** Represents the database schema (Members, Plans, etc.).

---

## ⚙️ Getting Started

### Prerequisites
* **JDK 17** or higher
* **Maven 3.8+**

### Installation
1. **Clone the repository:**
   ```bash
   git clone [https://github.com/aklenekar/gym-membership-java-demo.git](https://github.com/aklenekar/gym-membership-java-demo.git)
   cd gym-membership-java-demo
