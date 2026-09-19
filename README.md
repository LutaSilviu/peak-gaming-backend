# Peak Gaming API

Booking, availability and admin backend for the Peak Gaming Suceava room, built with Spring Boot (Java 21) and Postgres. Pairs with [peak-gaming-frontend](https://github.com/peak-gaming/peak-gaming-frontend) — see that repo for the site itself.

## Architecture

Layered per domain module (`station`, `zone`, `pricing`, `reservation`), each split into:

- **controller** — HTTP layer (`@RestController`), request validation, OpenAPI annotations.
- **service** — business logic (pricing, availability, station assignment), `@Transactional` boundaries.
- **repository** — Spring Data JPA interfaces.
- **entity / DTO** — JPA entities never leave the service layer; controllers only see DTOs (records).

Cross-cutting concerns live in `config` (CORS, admin-key auth interceptor, OpenAPI setup) and `exception` (a `@RestControllerAdvice` mapping domain exceptions to HTTP status codes).

## Running locally

```bash
docker compose up -d
```

Starts Postgres and the API together. The API applies Flyway migrations on boot and is available at `http://localhost:8080`.

To run just Postgres and the app from your IDE/Gradle instead:

```bash
docker compose up -d db
./gradlew bootRun
```

## API documentation

Swagger UI: `http://localhost:8080/swagger-ui.html`
OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Admin authentication

Admin-only endpoints (day stats, confirm/cancel a reservation) require an `X-Admin-Key` header matching `app.admin.api-key` (default `peak2026`, override via `APP_ADMIN_API_KEY`). There's no login flow — this mirrors the frontend's original single shared PIN, just enforced server-side instead of client-side.

## Database

- `V1__create_schema.sql` — tables: `station_types`, `stations`, `price_tiers`, `zones`, `reservations`, `reservation_stations`.
- `V2__seed_data.sql` — the fixed catalog (9 PCs, 5 PS5 pods, 1 racing rig), tariffs, and the four room zones — mirrors the frontend's constants exactly (`booking-pricing.ts`, `room-zones.ts`).

Pricing and availability are always recomputed server-side (`PricingService`, `ReservationService`); a client-submitted total is never trusted. Booking creation is serialized per `(date, stationType)` via a Postgres advisory lock, so two concurrent requests for the same slot can't both see the same free stations and overbook them.

## Tests

```bash
./gradlew test
```

Unit tests (`PricingServiceTest`, `ReservationServiceTest`, `ConfirmationCodeGeneratorTest`) mock repositories with Mockito. Integration tests (`*IntegrationTest`) spin up a real Postgres via Testcontainers and exercise the full HTTP stack — no mocking, since Flyway migrations use Postgres-specific SQL that H2 wouldn't validate correctly.

Coverage report: `build/reports/jacoco/test/html/index.html`. `./gradlew check` fails the build if instruction coverage drops below 80%.

### Windows + Docker Desktop note

If Testcontainers fails to find a Docker environment, it's usually because it probed the wrong named pipe. Either add `DOCKER_HOST=npipe:////./pipe/dockerDesktopLinuxEngine` to your environment (the `test` task in `build.gradle` forwards it to the test JVM automatically when set), or check `docker context ls` for the pipe your active context actually uses.

## Deploying

Any host that can build the `Dockerfile` and reach a Postgres instance works (Railway, Render, Fly.io, a plain VPS). Environment variables to set:

| Variable | Purpose |
|---|---|
| `SPRING_DATASOURCE_URL` | e.g. `jdbc:postgresql://<host>:5432/<db>` |
| `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` | Postgres credentials |
| `APP_ADMIN_API_KEY` | admin panel access key — change this from the `peak2026` default in production |
| `APP_CORS_ALLOWED_ORIGINS` | the deployed frontend's origin, e.g. `https://peak-gaming.vercel.app` |

On Railway specifically: add a Postgres service to the project, then reference its variables on this service (`${{Postgres.PGHOST}}`, etc.) instead of hardcoding them.

## Frontend integration

The frontend talks to this API when `NEXT_PUBLIC_API_URL` is set (see its `.env.local`); otherwise it falls back to browser-only `localStorage`. Endpoint contract:

| Method | Path | Auth | Used by |
|---|---|---|---|
| `GET` | `/api/rezervari?data=YYYY-MM-DD` | none | booking wizard (availability), admin panel |
| `POST` | `/api/rezervari` | none | booking wizard (create) |
| `GET` | `/api/rezervari/stats?data=YYYY-MM-DD` | `X-Admin-Key` | admin panel |
| `PATCH` | `/api/rezervari/{id}/stare` | `X-Admin-Key` | admin panel (confirm/cancel) |
| `GET` | `/api/stations`, `/api/stations/types` | none | (available; not yet consumed by the frontend) |
| `GET` | `/api/zones` | none | (available; not yet consumed by the frontend) |
