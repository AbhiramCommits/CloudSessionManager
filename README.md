# CloudSessionManager

Spring Boot microservice for managing user sessions with Redis-backed storage and JWT-based authentication.

## Architecture

```
┌──────────┐     ┌─────────────────────────────────────┐     ┌─────────┐
│  Client  │────▶│       CloudSessionManager            │────▶│  Redis  │
│          │     │                                      │     │         │
│ (JWT)    │     │  ┌──────────────┐  ┌──────────────┐  │     │ 7-alpine│
│          │     │  │ JwtAuthFilter│─▶│SessionControl│──│────▶│         │
└──────────┘     │  └──────────────┘  └──────┬───────┘  │     └─────────┘
                 │                           │          │
                 │  ┌───────────────────────▼───────┐   │
                 │  │       SessionService           │   │
                 │  │  ┌─────────────────────────┐   │   │
                 │  │  │  SessionRoutingService   │   │   │
                 │  │  │  (us-west|us-east|eu-    │   │   │
                 │  │  │   central)               │   │   │
                 │  │  └─────────────────────────┘   │   │
                 │  └───────────────────────────────┘   │
                 └─────────────────────────────────────┘
```

## Tech Stack

- **Java 17** / **Spring Boot 3.2**
- **Redis** for session persistence (TTL = 30 min)
- **JWT (jjwt 0.11.5)** for stateless authentication (HS256)
- **Spring Security** for access control
- **Spring Actuator** for health and metrics

## API Endpoints

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `POST` | `/api/auth/login` | None | Authenticate with `{userId, password}` (demo: `demo123`), returns JWT |
| `POST` | `/api/sessions` | Bearer | Create a new game session |
| `GET` | `/api/sessions/{id}` | Bearer | Retrieve session details |
| `PATCH` | `/api/sessions/{id}/activity` | Bearer | Refresh `lastActiveAt` timestamp |
| `DELETE` | `/api/sessions/{id}` | Bearer | Terminate and delete session |

### Usage

```bash
# Login
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"userId":"player1","password":"demo123"}' | jq -r '.token')

# Create session
curl -X POST http://localhost:8080/api/sessions \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"gameTitle":"Fortnite"}'

# Get session
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/sessions/{sessionId}

# Refresh activity
curl -X PATCH -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/sessions/{sessionId}/activity

# Terminate
curl -X DELETE -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/sessions/{sessionId}

# Health
curl http://localhost:8080/actuator/health
```

## Quick Start (Docker Compose)

```bash
# Build and start both Redis and the app
docker compose up --build -d

# Verify
curl http://localhost:8080/actuator/health

# View logs
docker compose logs -f app

# Stop
docker compose down
```

## Quick Start (local)

### Prerequisites

- JDK 17+
- Maven 3.8+
- Redis running on `localhost:6379`

### Steps

```bash
# Start Redis
redis-server

# Set env vars (optional — defaults work for local dev)
cp .env.example .env
source .env

# Run
mvn spring-boot:run

# Verify
curl http://localhost:8080/actuator/health
```

## Running Tests

```bash
# Unit tests
mvn test

# Integration tests (requires Redis)
mvn verify
```

## Configuration

| Variable | Default | Description |
|----------|---------|-------------|
| `JWT_SECRET` | `change-me-to-a-real-secret` | JWT HS256 signing key |
| `REDIS_HOST` | `localhost` | Redis hostname |
| `REDIS_PORT` | `6379` | Redis port |
| `SERVER_PORT` | `8080` | HTTP server port |
