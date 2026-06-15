Trial Bank Application
Overview
Trial Bank Application is a secure digital banking platform built with Spring Boot that provides account management, authentication, fund transfers, deposits, withdrawals, transaction history, and role-based access control.
The system follows modern software engineering principles including layered architecture, SOLID design principles, centralized exception handling, JWT-based authentication, transactional consistency, and ownership validation.
The application is designed as an educational banking system while incorporating many architectural patterns used in real-world banking software.
________________________________________
Features
User Management
•	User registration and onboarding
•	Secure authentication using JWT
•	User profile management
•	Role-based authorization
•	Ownership validation for account access
Account Management
•	Automatic account creation
•	Unique account number generation
•	Multiple accounts per user support
•	Account status management:
o	ACTIVE
o	INACTIVE
o	SUSPENDED (future enhancement)
o	DORMANT (future enhancement)
o	FROZEN (future enhancement)
Transaction Processing
Deposits
•	Deposit funds into owned accounts
•	Transaction recording
•	Balance updates within database transactions
Withdrawals
•	Secure balance validation
•	Insufficient fund checks
•	Transaction recording
Transfers
•	Account-to-account transfers
•	Ownership validation
•	Atomic balance updates
•	Transaction reference generation
•	Debit and credit notifications
Transaction History
•	Transaction storage
•	Transaction lookup by reference
•	Account transaction statements
•	Audit-ready transaction records
Security
•	JWT Authentication
•	Spring Security integration
•	Password encryption using BCrypt
•	Ownership validation
•	Unauthorized access prevention
•	Role-based endpoint protection
Exception Handling
Centralized exception handling through:
•	AccountNotFoundException
•	InsufficientFundsException
•	InactiveAccountException
•	UnauthorizedAccountAccessException
•	InvalidAmountException
•	TransactionNotFoundException
All exceptions are processed through a Global Exception Handler to provide consistent API responses.
________________________________________
Technology Stack
Backend
•	Java 17
•	Spring Boot 3.x
•	Spring Security
•	Spring Data JPA
•	Hibernate
•	Maven
Database
•	MySQL
Authentication
•	JWT (JSON Web Tokens)
•	BCrypt Password Encoder
Documentation
•	Swagger / OpenAPI
Utilities
•	Lombok
________________________________________
Project Architecture
The project follows a layered architecture:
com.edwin.trial_bank_app
├── controller
├── dto
├── entity
├── enums
├── exception
├── repository
├── security
├── service
│ └── impl
├── utils
└── config
Layer Responsibilities
Controller Layer
Handles HTTP requests and responses.
Service Layer
Contains business logic and transaction processing.
Repository Layer
Handles database interactions.
DTO Layer
Transfers data between layers.
Exception Layer
Handles application-specific errors.
Security Layer
Manages authentication and authorization.
________________________________________
Setup Instructions
Prerequisites
•	Java 17+
•	Maven 3.8+
•	MySQL 8+
________________________________________
Database Setup
Create database:
CREATE DATABASE trial_bank_db;
Update application.properties:
spring.datasource.url=jdbc:mysql://localhost:3306/trial_bank_db
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update

spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
________________________________________
Run Application
Using Maven:
mvn spring-boot:run
Or run:
TrialBankAppApplication.java
The application starts on:
http://localhost:8080
________________________________________
Authentication Flow
Register User
POST /api/v1/auth/register
Login
POST /api/v1/auth/login
Response returns:
{
  "accessToken": "jwt-token"
}
Use token:
Authorization: Bearer <jwt-token>
for all protected endpoints.
________________________________________
Core Banking Endpoints
Account Creation
POST /api/v1/accounts
________________________________________
Deposit Funds
POST /api/v1/accounts/deposit
________________________________________
Withdraw Funds
POST /api/v1/accounts/withdraw
________________________________________
Transfer Funds
POST /api/v1/accounts/transfer
________________________________________
Transaction History
GET /api/v1/transactions
________________________________________
Account Details
GET /api/v1/accounts/{accountNumber}
________________________________________
Security Features
Ownership Validation
The system ensures users can only access accounts they own.
Before any balance-changing operation:
1.	User authentication is verified.
2.	Account ownership is validated.
3.	Account status is checked.
4.	Balance validation is performed.
5.	Transaction executes.
This prevents account spoofing attacks where a user attempts to operate on another customer’s account.
________________________________________
Transaction Processing Design
All monetary operations execute inside database transactions.
Benefits:
•	Atomic operations
•	Consistent balances
•	Automatic rollback on failure
•	Data integrity protection
________________________________________
Exception Handling
The application uses custom exceptions and centralized handling.
Example error response:
{
  "code": "404",
  "message": "Account not found: 1234567890",
  "timestamp": "2026-06-15T10:00:00",
  "path": "/api/v1/accounts/transfer"
}
________________________________________
Banking Best Practices Implemented
•	Layered Architecture
•	SOLID Principles
•	Constructor Dependency Injection
•	JWT Authentication
•	Centralized Exception Handling
•	Ownership Validation
•	Transactional Consistency
•	Password Encryption
•	Separation of Concerns
•	DTO Pattern
•	Repository Pattern
•	Audit-Ready Transaction Records
________________________________________
Future Enhancements
Audit Logging
•	User activity tracking
•	Security event monitoring
Account Freezing
•	Freeze suspicious accounts
•	Fraud investigation support
Transaction Reversal
•	Controlled reversal workflow
•	Approval process
Email Notifications
•	Deposit alerts
•	Withdrawal alerts
•	Transfer alerts
Statement Generation
•	PDF statements
•	Monthly reports
Fraud Detection
•	Suspicious activity monitoring
•	Transaction velocity checks
Two-Factor Authentication
•	OTP verification
•	Enhanced login security
Production Deployment
•	PostgreSQL or MySQL clustering
•	Redis caching
•	Docker containerization
•	CI/CD pipelines
•	Monitoring and observability
________________________________________
Author
Edwin Ezue
Computer Science Final Year Project
Trial Bank Application – Secure Digital Banking Platform
