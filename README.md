# Let's Play

RESTful CRUD API for a small e-commerce–style platform: **users** and **products**, with JWT auth and role-based access (Admin / User). Built with Spring Boot and MongoDB.

## Quick start

**Prerequisites:** Java 17+, Docker (for MongoDB).

From the project root:

```bash
./scripts/run-dev.sh
```

This starts MongoDB via Docker Compose, waits for it, ensures an HTTPS keystore exists, then runs the app. API base: **https://localhost:8443**.

**Manual run:** Start MongoDB (see [docs/mongoDB/README.md](docs/mongoDB/README.md)), then:

```bash
./mvnw spring-boot:run
```

## Configuration

Key settings (env or `application.properties`):

| Purpose        | Property / Env              | Default (dev)        |
|----------------|-----------------------------|------------------------|
| MongoDB        | `spring.mongodb.uri`        | `mongodb://localhost:27017/letsplay` |
| Default admin  | `ADMIN_EMAIL`, `ADMIN_PASSWORD` | `admin@letsplay.local` / `admin123` |
| JWT            | `JWT_SECRET`, `JWT_EXPIRATION_MS` | (see application.properties) |
| HTTPS keystore | `SSL_KEY_STORE_PASSWORD`    | `changeit`             |

Default admin is created on first startup if no user exists with that email. **Change the default password in non-dev environments.**

## Documentation

| Topic | Location |
|-------|----------|
| **API contract** (OpenAPI) | [docs/lets_play.yaml](docs/lets_play.yaml) |
| **Project overview, goals, constraints** | [docs/lets-play.md](docs/lets-play.md) |
| **MongoDB setup and schema** | [docs/mongoDB/README.md](docs/mongoDB/README.md) |

For endpoint details, request/response schemas, and auth (Bearer JWT), use the OpenAPI spec or import `docs/lets_play.yaml` into Swagger UI / Postman.
