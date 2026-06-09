# CloudSessionManager

Spring Boot microservice for managing user sessions with Redis-backed storage and JWT-based authentication.

## Tech Stack

- **Java 17** / **Spring Boot 3.2**
- **Redis** for session persistence
- **JWT (jjwt 0.11.5)** for stateless authentication
- **Spring Security** for access control
- **Spring Actuator** for health and metrics

## Prerequisites

- JDK 17+
- Maven 3.8+
- Redis running on `localhost:6379`

## Quick Start

1. **Clone the repo**
   ```bash
   git clone <repo-url>
   cd CloudSessionManager
   ```

2. **Start Redis** (if not already running)
   ```bash
   redis-server
   ```

3. **Set environment variables** (optional — defaults work for local dev)
   ```bash
   cp .env.example .env
   source .env
   ```

4. **Run the app**
   ```bash
   mvn spring-boot:run
   ```

5. **Verify**
   ```bash
   curl http://localhost:8080/actuator/health
   ```

## Configuration

All settings live in `src/main/resources/application.yml`. Override via environment variables:

| Variable       | Default                      | Description           |
|----------------|------------------------------|-----------------------|
| `JWT_SECRET`   | `change-me-to-a-real-secret` | JWT signing key       |
| `REDIS_HOST`   | `localhost`                  | Redis hostname        |
| `REDIS_PORT`   | `6379`                       | Redis port            |
| `SERVER_PORT`  | `8080`                       | HTTP server port      |
