# URL Platform

A production-style URL shortening application built as a DevOps portfolio project.

## Architecture

```
                    Next.js
                   Frontend
                  (port 3000)
                       |
                       v
                API Gateway
                (port 8080)
                       |
              +--------+--------+
              |                 |
              v                 v
        User Service       URL Service
         (port 8081)       (port 8082)
              |                 |
              +--------+--------+
                       |
                       v
                  PostgreSQL
                  (port 5432)
```

## Technology Stack

| Layer       | Technology                                      |
|-------------|-------------------------------------------------|
| Frontend    | Next.js 15, React 19, TypeScript, Bootstrap 5, Axios |
| API Gateway | Spring Cloud Gateway (Reactive)                 |
| Backend     | Java 21, Spring Boot 3.3, Spring Data JPA       |
| Validation  | Jakarta Bean Validation, Hibernate Validator    |
| Database    | PostgreSQL, Flyway migrations                   |
| Testing     | JUnit 5, Mockito, Spring Boot Test              |
| API Docs    | Springdoc OpenAPI 3 (Swagger UI)                |
| Observability | Spring Boot Actuator                          |

## Project Structure

```
url-platform/
├── frontend/                  # Next.js frontend
│   ├── src/
│   │   ├── app/
│   │   │   ├── page.tsx       # Dashboard
│   │   │   ├── users/         # Users page
│   │   │   ├── urls/          # URLs page
│   │   │   ├── layout.tsx
│   │   │   └── globals.css
│   │   ├── components/
│   │   │   └── NavBar.tsx
│   │   └── lib/
│   │       └── api.ts         # Axios API client
│   └── .env.local
│
├── api-gateway/               # Spring Cloud Gateway
│   └── src/
│       ├── main/resources/application.yml
│       └── test/
│
├── user-service/              # User management microservice
│   └── src/
│       ├── main/java/com/urlplatform/userservice/
│       │   ├── controller/    UserController.java
│       │   ├── service/       UserService.java
│       │   ├── repository/    UserRepository.java
│       │   ├── entity/        User.java
│       │   ├── dto/           CreateUserRequest, UpdateUserRequest, UserResponse
│       │   └── exception/     GlobalExceptionHandler, ...
│       └── test/
│
├── url-service/               # URL shortening microservice
│   └── src/
│       ├── main/java/com/urlplatform/urlservice/
│       │   ├── controller/    UrlController.java, RedirectController.java
│       │   ├── service/       UrlService.java, ShortCodeGenerator.java
│       │   ├── repository/    UrlRepository.java
│       │   ├── entity/        Url.java
│       │   ├── dto/           CreateUrlRequest, UrlResponse
│       │   └── exception/     GlobalExceptionHandler, ...
│       └── test/
│
├── database/
│   └── migrations/
│       ├── V1__create_users_table.sql
│       └── V2__create_urls_table.sql
│
└── README.md
```

## Database Setup

### Start PostgreSQL locally

Using `psql` / CLI:

```bash
# macOS (Homebrew)
brew install postgresql@16
brew services start postgresql@16

# Create database and user
psql postgres
CREATE DATABASE urlplatform;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE urlplatform TO postgres;
\q
```

> **Flyway migrations run automatically** on service startup. No manual SQL execution needed.

## Environment Variables

### User Service & URL Service

| Variable      | Default       | Description             |
|---------------|---------------|-------------------------|
| `DB_HOST`     | `localhost`   | PostgreSQL host         |
| `DB_PORT`     | `5432`        | PostgreSQL port         |
| `DB_NAME`     | `urlplatform` | Database name           |
| `DB_USERNAME` | `postgres`    | Database username       |
| `DB_PASSWORD` | `postgres`    | Database password       |

### API Gateway

| Variable           | Default                 | Description           |
|--------------------|-------------------------|-----------------------|
| `USER_SERVICE_URL` | `http://localhost:8081` | User Service base URL |
| `URL_SERVICE_URL`  | `http://localhost:8082` | URL Service base URL  |

### Frontend

| Variable               | Default                | Description           |
|------------------------|------------------------|-----------------------|
| `NEXT_PUBLIC_API_URL`  | `http://localhost:8080`| API Gateway base URL  |

## How to Run Locally

Start services in this order:

### 1. Start PostgreSQL

```bash
brew services start postgresql@16
```

### 2. Start User Service (port 8081)

```bash
cd user-service
mvn spring-boot:run

# Override DB credentials if needed:
DB_PASSWORD=secret mvn spring-boot:run
```

### 3. Start URL Service (port 8082)

```bash
cd url-service
mvn spring-boot:run
```

### 4. Start API Gateway (port 8080)

```bash
cd api-gateway
mvn spring-boot:run

# Override service URLs if needed:
USER_SERVICE_URL=http://localhost:8081 \
URL_SERVICE_URL=http://localhost:8082 \
mvn spring-boot:run
```

### 5. Start Frontend (port 3000)

```bash
cd frontend
npm install
npm run dev
```

Open http://localhost:3000

## API Endpoints

### User Service (direct: port 8081, via gateway: port 8080)

| Method   | Path              | Description          |
|----------|-------------------|----------------------|
| `POST`   | `/api/users`      | Create user          |
| `GET`    | `/api/users`      | List all users       |
| `GET`    | `/api/users/{id}` | Get user by ID       |
| `PUT`    | `/api/users/{id}` | Update user          |
| `DELETE` | `/api/users/{id}` | Delete user          |

**Create User Request:**
```json
{
  "name": "Vikash Singh",
  "email": "vikash@example.com"
}
```

### URL Service (direct: port 8082, via gateway: port 8080)

| Method   | Path              | Description                |
|----------|-------------------|----------------------------|
| `POST`   | `/api/urls`       | Create shortened URL        |
| `GET`    | `/api/urls`       | List all shortened URLs    |
| `GET`    | `/api/urls/{id}`  | Get URL by ID              |
| `DELETE` | `/api/urls/{id}`  | Delete shortened URL       |
| `GET`    | `/{shortCode}`    | Redirect to original URL   |

**Create URL Request:**
```json
{
  "originalUrl": "https://www.example.com/some/long/url",
  "userId": 1
}
```

**Error Response Format:**
```json
{
  "timestamp": "2026-09-28T11:00:00+05:30",
  "status": 400,
  "message": "originalUrl: Original URL must be a valid URL"
}
```

## Swagger UI

| Service       | Swagger UI                          | OpenAPI JSON             |
|---------------|-------------------------------------|--------------------------|
| User Service  | http://localhost:8081/swagger-ui.html | http://localhost:8081/v3/api-docs |
| URL Service   | http://localhost:8082/swagger-ui.html | http://localhost:8082/v3/api-docs |

## Actuator Health

| Service       | Health URL                               |
|---------------|------------------------------------------|
| User Service  | http://localhost:8081/actuator/health    |
| URL Service   | http://localhost:8082/actuator/health    |
| API Gateway   | http://localhost:8080/actuator/health    |

## Testing

### Run All Backend Tests

```bash
# User Service (17 tests)
cd user-service && mvn test

# URL Service (40 tests)
cd url-service && mvn test

# API Gateway (2 tests)
cd api-gateway && mvn test
```

> **Note:** Running on Java 26 requires `-Dnet.bytebuddy.experimental=true`. This is pre-configured in each service's `pom.xml`.

### Test Coverage

**User Service:**
- `UserServiceTest` — 10 unit tests (create, get, list, update, delete, duplicate email, not found)
- `UserControllerTest` — 7 web-layer tests (HTTP status codes, validation, error responses)

**URL Service:**
- `UrlServiceTest` — 9 unit tests (create, collision handling, get, redirect, delete, not found)
- `UrlControllerTest` — 9 web-layer tests (create, invalid URL, missing userId, get, redirect, delete)
- `ShortCodeGeneratorTest` — 22 tests (length, character set, repeated generation)

**API Gateway:**
- `GatewayRoutingTest` — 2 integration tests (context loads, routes configured)

## Gateway Routing

Routes (in priority order):

1. `/api/users/**` → User Service
2. `/api/urls/**` → URL Service
3. `/{shortCode}` → URL Service (redirect)

The more specific `/api/**` routes take priority over the catch-all `/{shortCode}` route because they are listed first in the configuration.

## Short Code Generation

- 6-character alphanumeric codes (A-Z, a-z, 0-9)
- ~56 billion possible combinations
- Generated using `SecureRandom`
- Up to 10 collision retries before failing

## Key Design Decisions

- **Flyway**: Each service has its own copy of migrations so it can start independently (both services share the same PostgreSQL database and schema).
- **No JPA FK join**: `Url.userId` is stored as a plain `Long` because users and URLs are logically separate microservices.
- **Gateway CORS**: Enabled globally for local development (`allowedOrigins: "*"`).
- **ddl-auto=validate**: Tables are never auto-created/modified by Hibernate; Flyway owns the schema.