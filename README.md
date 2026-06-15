# 🏦 Trial Bank Application

A secure, full-featured digital banking platform built with **Spring Boot 3.x** and **Java 17**. Designed as a Computer Science final year project, it incorporates real-world banking architecture patterns including JWT authentication, role-based access control, transactional consistency, and layered design.

---

## 📋 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Architecture](#project-architecture)
- [Getting Started](#getting-started)
- [Authentication Flow](#authentication-flow)
- [API Endpoints](#api-endpoints)
- [Security Design](#security-design)
- [Exception Handling](#exception-handling)
- [Future Enhancements](#future-enhancements)

---

## ✨ Features

### User Management
- User registration and onboarding
- JWT-based secure authentication
- User profile management
- Role-based authorization and ownership validation

### Account Management
- Automatic account creation with unique account number generation
- Multiple accounts per user
- Account status support: `ACTIVE`, `INACTIVE` *(+ `SUSPENDED`, `DORMANT`, `FROZEN` planned)*

### Transaction Processing
| Operation | Description |
|-----------|-------------|
| **Deposit** | Fund owned accounts with balance updates inside DB transactions |
| **Withdraw** | Secure balance validation with insufficient fund checks |
| **Transfer** | Atomic account-to-account transfers with reference generation |

### Transaction History
- Full transaction storage and lookup by reference
- Account statements and audit-ready records

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Security | Spring Security + JWT + BCrypt |
| ORM | Spring Data JPA / Hibernate |
| Database | MySQL 8+ |
| Build Tool | Maven 3.8+ |
| Docs | Swagger / OpenAPI |
| Utilities | Lombok |

---

## 🗂️ Project Architecture

The project follows a clean **layered architecture**:

```
com.edwin.trial_bank_app
├── controller      # HTTP request/response handling
├── dto             # Data transfer between layers
├── entity          # JPA entities
├── enums           # Application enumerations
├── exception       # Custom exceptions + global handler
├── repository      # Database interactions
├── security        # Authentication and authorization
├── service
│   └── impl        # Business logic and transaction processing
├── utils
└── config
```

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8+

### Database Setup

```sql
CREATE DATABASE trial_bank_db;
```

### Configuration

Update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/trial_bank_db
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### Run the Application

```bash
mvn spring-boot:run
```

Or run `TrialBankAppApplication.java` directly from your IDE.

The app starts at: **http://localhost:8080**

---

## 🔐 Authentication Flow

**1. Register**
```
POST /api/v1/auth/register
```

**2. Login**
```
POST /api/v1/auth/login
```

Response:
```json
{
  "accessToken": "jwt-token"
}
```

**3. Use the token on all protected endpoints:**
```
Authorization: Bearer <jwt-token>
```

---

## 📡 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/auth/register` | Register a new user |
| `POST` | `/api/v1/auth/login` | Login and receive JWT |
| `POST` | `/api/v1/accounts` | Create a new account |
| `POST` | `/api/v1/accounts/deposit` | Deposit funds |
| `POST` | `/api/v1/accounts/withdraw` | Withdraw funds |
| `POST` | `/api/v1/accounts/transfer` | Transfer between accounts |
| `GET`  | `/api/v1/accounts/{accountNumber}` | Get account details |
| `GET`  | `/api/v1/transactions` | Get transaction history |

Full interactive docs available via **Swagger UI** at `/swagger-ui.html` when the app is running.

---

## 🛡️ Security Design

### Ownership Validation

Every balance-changing operation follows this strict pipeline:

```
1. Verify user authentication (JWT)
2. Validate account ownership
3. Check account status
4. Validate balance/amount
5. Execute transaction
```

This prevents account spoofing — users can never operate on another customer's account.

### Transaction Integrity

All monetary operations are wrapped in **database transactions**, ensuring:
- Atomic operations (all-or-nothing)
- Consistent balances at all times
- Automatic rollback on failure
- Full data integrity protection

---

## ⚠️ Exception Handling

All errors are handled centrally via a **Global Exception Handler**, which produces consistent API error responses.

**Custom exceptions:**
- `AccountNotFoundException`
- `InsufficientFundsException`
- `InactiveAccountException`
- `UnauthorizedAccountAccessException`
- `InvalidAmountException`
- `TransactionNotFoundException`

**Example error response:**
```json
{
  "code": "404",
  "message": "Account not found: 1234567890",
  "timestamp": "2026-06-15T10:00:00",
  "path": "/api/v1/accounts/transfer"
}
```

---

## 🏗️ Banking Best Practices Implemented

- ✅ Layered Architecture
- ✅ SOLID Principles
- ✅ Constructor Dependency Injection
- ✅ JWT Authentication
- ✅ Centralized Exception Handling
- ✅ Ownership Validation
- ✅ Transactional Consistency
- ✅ BCrypt Password Encryption
- ✅ Separation of Concerns
- ✅ DTO Pattern
- ✅ Repository Pattern
- ✅ Audit-Ready Transaction Records

---

## 🔮 Future Enhancements

| Feature | Description |
|---------|-------------|
| Audit Logging | Track user activity and security events |
| Account Freezing | Freeze suspicious accounts for fraud investigation |
| Transaction Reversal | Controlled reversal workflow with approval process |
| Email Notifications | Alerts for deposits, withdrawals, and transfers |
| Statement Generation | PDF statements and monthly reports |
| Fraud Detection | Suspicious activity monitoring and velocity checks |
| Two-Factor Authentication | OTP verification for enhanced login security |
| Production Deployment | PostgreSQL clustering, Redis caching, Docker, CI/CD |

---

## 👨‍💻 Author

**Edwin Ezue**
*Trial Bank Application – Secure Digital Banking Platform*
