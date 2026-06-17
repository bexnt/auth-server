# Auth Server

Auth Server is a Spring Boot REST API for user registration, login, JWT-based authentication, and role-based access control.

The project demonstrates a basic authentication flow with BCrypt password hashing, JWT access tokens, protected endpoints, custom security error handling, and PostgreSQL persistence.

## Features

* User registration
* User login
* BCrypt password hashing
* JWT access token generation
* JWT access token validation
* Protected current user endpoint
* Role-based access control
* Custom JSON error responses
* PostgreSQL persistence
* Environment-based configuration

## Tech Stack

* Java 17
* Spring Boot
* Spring Web MVC
* Spring Security
* Spring Data JPA
* Hibernate
* PostgreSQL
* JWT
* Gradle

## Environment Variables

The application supports environment variables with local default values.

| Variable            | Description                               | Default                                                           |
| ------------------- | ----------------------------------------- | ----------------------------------------------------------------- |
| `DB_URL`            | PostgreSQL JDBC URL                       | `jdbc:postgresql://localhost:5432/auth-server`                    |
| `DB_USERNAME`       | Database username                         | `postgres`                                                        |
| `DB_PASSWORD`       | Database password                         | `postgres`                                                        |
| `JWT_SECRET`        | Secret key used to sign JWT access tokens | `temporary-development-secret-key-which-is-long-enough-for-hs256` |
| `JWT_EXPIRATION_MS` | JWT expiration time in milliseconds       | `3600000`                                                         |

Example `.env.example`:

```env
DB_URL=jdbc:postgresql://localhost:5432/auth-server
DB_USERNAME=postgres
DB_PASSWORD=postgres

JWT_SECRET=change-me-to-a-long-random-secret-key-at-least-32-bytes
JWT_EXPIRATION_MS=3600000
```

> Do not commit real secrets to GitHub. Use environment variables for production-like environments.

## API Endpoints

| Method | Endpoint             | Access        | Description                            |
| ------ | -------------------- | ------------- | -------------------------------------- |
| `POST` | `/api/auth/register` | Public        | Register a new user                    |
| `POST` | `/api/auth/login`    | Public        | Login and receive a JWT access token   |
| `GET`  | `/api/users/me`      | Authenticated | Get current authenticated user profile |
| `GET`  | `/api/admin/test`    | ADMIN         | Test admin-only access                 |

## Request Examples

### Register

```http
POST /api/auth/register
Content-Type: application/json
```

```json
{
  "email": "user@mail.com",
  "password": "123456"
}
```

Successful response:

```json
{
  "accessToken": "eyJhbGciOiJIUz...",
  "tokenType": "Bearer"
}
```

### Login

```http
POST /api/auth/login
Content-Type: application/json
```

```json
{
  "email": "user@mail.com",
  "password": "123456"
}
```

Successful response:

```json
{
  "accessToken": "eyJhbGciOiJIUz...",
  "tokenType": "Bearer"
}
```

### Get Current User

```http
GET /api/users/me
Authorization: Bearer <accessToken>
```

Successful response:

```json
{
  "id": 1,
  "email": "user@mail.com",
  "role": "USER",
  "createdAt": "2026-06-13T23:18:11.075511"
}
```

## Error Response Format

The API returns errors in a unified JSON format.

Example:

```json
{
  "timestamp": "2026-06-13T23:18:11.075511",
  "status": 401,
  "message": "Unauthorized",
  "errors": [
    "Authentication is required"
  ]
}
```

Common responses:

| Status             | Meaning                                                    |
| ------------------ | ---------------------------------------------------------- |
| `400 Bad Request`  | Validation failed                                          |
| `401 Unauthorized` | Authentication is missing or invalid                       |
| `403 Forbidden`    | User is authenticated but does not have enough permissions |
| `409 Conflict`     | Email already exists                                       |

## Security Flow

### Registration and Login

1. The client sends email and password.
2. The server validates the request.
3. During registration, the password is hashed with BCrypt before saving.
4. During login, the raw password is compared with the stored BCrypt hash.
5. If authentication succeeds, the server generates a JWT access token.
6. The client receives the token and sends it in future requests using the `Authorization` header.

```http
Authorization: Bearer <accessToken>
```

### Protected Requests

1. The client sends a request with a JWT access token.
2. `JwtAuthenticationFilter` reads the `Authorization` header.
3. `JwtService` validates the token signature and expiration date.
4. The email is extracted from the token subject.
5. `CustomUserDetailsService` loads the user from the database by email.
6. `CustomUserDetails` adapts `UserEntity` to Spring Security's `UserDetails`.
7. The filter creates an `Authentication` object and stores it in the `SecurityContext`.
8. Spring Security allows access to protected endpoints if the request is authenticated.

## Role-Based Access

User roles are stored in the database.

The JWT currently contains only the user's email. The actual role is loaded from the database during authentication.

Role mapping:

| Database Role | Spring Security Authority |
| ------------- | ------------------------- |
| `USER`        | `ROLE_USER`               |
| `ADMIN`       | `ROLE_ADMIN`              |

Admin endpoints require:

```java
hasRole("ADMIN")
```

This means the authenticated user must have the `ROLE_ADMIN` authority.

## How to Run

### 1. Create PostgreSQL Database

```sql
CREATE DATABASE "auth-server";
```

### 2. Configure Environment Variables

You can use the default values from `application.properties` for local development.

Or provide custom environment variables:

```env
DB_URL=jdbc:postgresql://localhost:5432/auth-server
DB_USERNAME=postgres
DB_PASSWORD=postgres
JWT_SECRET=change-me-to-a-long-random-secret-key-at-least-32-bytes
JWT_EXPIRATION_MS=3600000
```

### 3. Run the Application

Linux/macOS:

```bash
./gradlew bootRun
```

Windows:

```bash
gradlew bootRun
```

The application will start on:

```text
http://localhost:8080
```

## Project Structure

```text
src/main/java/com/bexnt/authserver
├── auth
│   ├── controller
│   ├── dto
│   └── service
├── user
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── mapper
│   ├── repository
│   └── service
├── security
│   ├── config
│   ├── handler
│   ├── jwt
│   └── userdetails
└── exception
```

## Current Limitations

* Refresh tokens are not implemented yet.
* Logout/token revocation is not implemented yet.
* JWT secret is provided through configuration and should be replaced with a strong environment secret outside local development.
* Admin role assignment is currently manual through the database.

## Planned Improvements

* Refresh token flow
* Logout and token revocation
* Integration tests
* Docker Compose for PostgreSQL
* Flyway or Liquibase database migrations
* OpenAPI/Swagger documentation
