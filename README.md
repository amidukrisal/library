# Library Management API

REST Spring Boot API for managing a library system.

---

## Table of Contents
1. [Quick Start](#quick-start)
2. [API Endpoints](#api-endpoints)
3. [Data Models](#data-models)
4. [Environment Profiles](#environment-profiles)
5. [Running with Docker](#running-with-docker)
6. [Kubernetes Deployment](#kubernetes-deployment)
7. [Running Tests](#running-tests)
8. [Design Decisions & Assumptions](#design-decisions--assumptions)
9. [12-Factor Compliance](#12-factor-compliance)

---

## Quick Start

### Prerequisites
- Java 17+
- Maven 4.0+
- (Optional) Docker & Docker Compose

### Run locally (dev mode with H2 in-memory DB)

```bash
# Clone repository
git clone <repo-url>
cd library

# Run with dev profile (H2 in-memory, no external DB needed)
mvn spring-boot:run -Pdev

# API is available at:
#   http://localhost:8080/api/v1
#   http://localhost:8080/swagger-ui.html  (interactive API docs)
```

---

## API Endpoints

### Base URL
```
http://localhost:8080/api/v1
```

### Borrowers

| Method | URL | Description |
|--------|-----|-------------|
| `POST` | `/borrowers` | Register a new borrower |
| `POST` | `/borrowers/{borrowerId}/borrow/{bookId}` | Borrow a book |
| `POST` | `/borrowers/{borrowerId}/return/{bookId}` | Return a borrowed book |

### Books

| Method | URL | Description |
|--------|-----|-------------|
| `POST` | `/books` | Register a new book |
| `GET`  | `/books` | List all books |

---

### Example Requests

#### 1. Register a Borrower
```bash
curl -X POST http://localhost:8080/api/v1/borrowers \
  -H "Content-Type: application/json" \
  -d '{"name": "Jane Doe", "email": "jane.doe@example.com"}'
```
**Response (201 Created)**
```json
{
  "id": 1,
  "name": "Jane Doe",
  "email": "jane.doe@example.com",
  "createdAt": "2024-03-15T10:00:00"
}
```

#### 2. Register a Book
```bash
curl -X POST http://localhost:8080/api/v1/books \
  -H "Content-Type: application/json" \
  -d '{
    "isbn": "123456789",
    "title": "Shirlock Holmes",
    "author": "Arthur Conan Doile"
  }'
```
**Response (201 Created)**
```json
{
  "id": 1,
  "isbn": "123456789",
  "title": "Shirlock Holmes",
  "author": "Arthur Conan Doile",
  "available": true,
  "createdAt": "2024-03-15T10:00:00"
}
```

#### 3. Register a second copy of the same book (same ISBN, new id)
```bash
curl -X POST http://localhost:8080/api/v1/books \
  -H "Content-Type: application/json" \
  -d '{
    "isbn": "123456789",
    "title": "Shirlock Holmes",
    "author": "Arthur Conan Doile"
  }'
```
**Response (201 Created)** — same ISBN, different `id`
```json
{ "id": 2, "isbn": "123456789", "title": "Shirlock Holmes", ... }
```

#### 4. Get All Books
```bash
curl http://localhost:8080/api/v1/books
```
**Response (200 OK)**
```json
[
  { "id": 1, "isbn": "123456789", "title": "Shirlock Holmes", "author": "Arthur Conan Doile", "borrowed": false },
  { "id": 2, "isbn": "123456789", "title": "Shirlock Holmes", "author": "Arthur Conan Doile", "borrowed": true }
]
```

#### 5. Borrow a Book
```bash
curl -X POST http://localhost:8080/api/v1/borrowers/1/borrow/1
```
**Response (200 OK)**
```json
{
  "bookId": 1,
  "bookIsbn": "123456789",
  "bookTitle": "Shirlock Holmes",
  "borrowed": true,
  "borrowedAt": "2026-06-03T15:14:28.7687432",
  "borrowerId": 1,
  "borrowerName": "Amidu",
  "recordId": 1,
  "returnedAt": null
}
```

#### 6. Return a Book
```bash
curl -X PUT http://localhost:8080/api/v1/borrowers/1/return/1
```
**Response (200 OK)**
```json
{
  "bookId": 1,
  "bookIsbn": "123456789",
  "bookTitle": "Shirlock Holmes",
  "borrowed": false,
  "borrowedAt": "2026-06-03T15:14:28.768743",
  "borrowerId": 1,
  "borrowerName": "Amidu",
  "recordId": 1,
  "returnedAt": "2026-06-03T15:16:33.1067295"
}
```

---

### Error Responses

All errors follow a consistent format:

Book id=1 ('Shirlock Holmes') is already borrowed by borrower id=1 and has not been returned.

| HTTP Status | When                                       |
|-------------|--------------------------------------------|
| `400` | Validation failed (missing/invalid fields) |
| `404` | Borrower or book not found                 |
| `409` | Book already borrowed / ISBN data conflict |
| `422` | Illegal state                              |

---

## Data Models

### Borrower
| Field | Type | Constraints |
|-------|------|-------------|
| `id` | Long | Auto-generated, unique |
| `name` | String | Required, non-blank |
| `email` | String | Required, valid email, unique across all borrowers |
| `createdAt` | LocalDateTime | Auto-set on creation |

### Book
| Field        | Type | Constraints |
|--------------|------|-------------|
| `id`         | Long | Auto-generated, unique per copy |
| `isbn`       | String | Required; 10 or 13 digit ISBN (no hyphens) |
| `title`      | String | Required; must match existing title for same ISBN |
| `author`     | String | Required; must match existing author for same ISBN |
| `isBorrowed` | boolean | True unless currently borrowed |
| `createdAt`  | LocalDateTime | Auto-set on creation |

### BorrowRecord (internal, returned in borrow/return responses)
| Field | Type | Meaning |
|-------|------|---------|
| `recordId` | Long | Auto-generated |
| `borrowerId` | Long | Who borrowed |
| `bookId` | Long | Which specific copy |
| `borrowedAt` | LocalDateTime | When borrowed |
| `returnedAt` | LocalDateTime | When returned (`null` = still borrowed) |
| `isBorrowed` | boolean | True if not yet returned |

---

## Environment Profiles

| Profile | Database | Activate with |
|---------|----------|---------------|
| `dev` (default) | H2 in-memory | `mvn spring-boot:run -Pdev` |
| `prod` | PostgreSQL | `SPRING_PROFILES_ACTIVE=prod` |

### Environment Variables (prod profile)

| Variable | Default | Description |
|----------|---------|-------------|
| `DATABASE_URL` | `jdbc:postgresql://localhost:5432/librarydb` | JDBC URL |
| `DATABASE_USER` | `library` | DB username |
| `DATABASE_PASSWORD` | `changeme` | DB password |
| `PORT` | `8080` | HTTP port |
| `SPRING_PROFILES_ACTIVE` | `prod` | Active profile |

---

## Running with Docker

```bash
# Start PostgreSQL + API
docker compose up -d

# View logs
docker compose logs -f library

# Stop everything
docker compose down
```

The API will be available at `http://localhost:8080`.


---

## Running Tests

```bash
# Run all tests + coverage report
mvn clean verify

# Tests only (skip coverage gate)
mvn test

```

### Test Coverage
- **Unit tests**: Service layer tested with Mockito (BookService, BorrowerService, BorrowRecordService)
- **Integration tests**: Full HTTP stack via MockMvc with H2 in-memory DB (BorrowerController, BookController, borrow/return flow)


---

