# ApexGym Project Standards & Instructions

## Project Overview
ApexGym is a comprehensive gym management platform designed to handle member authentication, class/resource bookings, fitness tracking, and administrative tasks. The application also integrates AI capabilities for personalized fitness insights.

## Tech Stack
- **Language:** Java 21
- **Framework:** Spring Boot 4.0.2
- **Security:** Spring Security with JSON Web Tokens (JJWT 0.12.3)
- **Database:** PostgreSQL (Production), H2 (Development/Testing)
- **ORM/Data:** Spring Data JPA / Hibernate
- **Mapping:** MapStruct 1.6.3
- **Utilities:** Lombok 1.18.44
- **Build Tool:** Maven
- **API Style:** RESTful with Spring Web

## Architecture
- **Pattern:** Layered Architecture (Web -> Service -> Persistence).
- **Organization:** Package-by-feature (e.g., `com.apexgym.auth`, `com.apexgym.booking`, `com.apexgym.tracking`).
- **Data Access:** Repository Pattern using Spring Data JPA.
- **Data Transfer:** DTOs (Data Transfer Objects) are used for all API requests and responses. Prefer Java **Records** for DTOs.
- **Dependency Injection:** Use constructor-based injection. Leverage Lombok's `@RequiredArgsConstructor` for brevity.

## Coding Standards
### General Rules
- **Modern Java:** Use Java 21 features (Records, Pattern Matching, Switch Expressions, Virtual Threads) where applicable.
- **Immutability:** Favor immutable objects and final fields.
- **Clean Code:** Keep methods short and focused on a single responsibility.

### Naming Conventions
- **Classes:** PascalCase (e.g., `BookingService`, `UserRepository`).
- **Methods/Variables:** camelCase (e.g., `processPayment`, `userEmail`).
- **Constants:** SCREAMING_SNAKE_CASE (e.g., `MAX_RETRY_ATTEMPTS`).
- **Packages:** lowercase, dot-separated (e.g., `com.apexgym.shared.exception`).

### REST API Standards
- **Endpoints:** Use lowercase and hyphens for URIs (e.g., `/api/v1/gym-members`).
- **HTTP Methods:** 
  - `GET` for retrieval.
  - `POST` for creation.
  - `PUT` for full updates.
  - `PATCH` for partial updates.
  - `DELETE` for removal.
- **Responses:** Always return `ResponseEntity<T>` from Controllers. Use appropriate HTTP status codes (200 OK, 201 Created, 400 Bad Request, 401 Unauthorized, 404 Not Found, 500 Internal Server Error).

### Error Handling
- Use a global exception handler (`@RestControllerAdvice`).
- Define custom runtime exceptions for business logic failures.
- Avoid returning raw error messages; use a structured `ErrorResponse` DTO.

### Testing
- **Unit Tests:** Use JUnit 5 and Mockito.
- **Integration Tests:** Use `@SpringBootTest` with `@ActiveProfiles("test")` and H2 database.

## Domain Vocabulary
- **User:** An authenticated entity in the system (can be a Member, Staff, or Admin).
- **Role:** Defines access levels (`USER`, `STAFF`, `ADMIN`).
- **Booking:** A scheduled reservation for a gym class or equipment.
- **Tracking:** The module responsible for logging and analyzing workout data.
- **Profile:** User-specific settings and fitness metadata.
