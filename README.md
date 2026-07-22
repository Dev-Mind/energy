# Energy

Spring Boot (Kotlin) service that proxies Enedis Data Connect APIs for token retrieval, customer contracts, and metering curves.

## Stack

- Java 21
- Kotlin 2.4
- Spring Boot 4.1 (Web MVC)
- `RestClient` for Enedis outbound calls
- springdoc OpenAPI UI + REST Docs / epages OpenAPI generation

## Configuration

Defined in `src/main/resources/application.properties` through `energy.*` properties:

| Property | Env fallback | Default |
| --- | --- | --- |
| `energy.base-url` | `HOME_AUTOMATION_ENEDIS_BASE_URL` | `https://gw.ext.prod-sandbox.api.enedis.fr` |
| `energy.client-id` | `HOME_AUTOMATION_ENEDIS_CLIENT_ID` | `replace-with-client-id` |
| `energy.secret` | `HOME_AUTOMATION_ENEDIS_SECRET` | `replace-with-secret` |
| `energy.oauth-path` | `HOME_AUTOMATION_ENEDIS_OAUTH_PATH` | `/oauth2/v3/token` |
| `server.port` | — | `8085` |

## Application flow

1. `TokenService` requests an OAuth token from Enedis (`grant_type=client_credentials`) and caches it until a computed refresh time.
2. `DataConnectService` calls Enedis customer/metering endpoints using `Authorization: Bearer <token>`.
3. `ApiExceptionHandler` forwards Enedis HTTP status and body to API clients when upstream calls fail.

## API endpoints

Base path: `https://localhost:8085`

| Method | Path | Query params | Description |
| --- | --- | --- | --- |
| `GET` | `/api/enedis/token` | — | Returns current Enedis OAuth token (`access_token`, `scope`, `token_type`, `expires_in`). |
| `GET` | `/api/enedis/customers/contracts` | `usagePointId` | Returns contracts for a usage point (mapped from Enedis customer contract payload). |
| `GET` | `/api/enedis/metering/consumption-load-curve` | `start`, `end`, `usagePointId` | Returns consumption load curve for the period. |
| `GET` | `/api/enedis/metering/production-load-curve` | `start`, `end`, `usagePointId` | Returns production load curve for the period. |

`start` and `end` are `LocalDate` values (ISO-8601, e.g. `2026-07-01`).

## Run locally

```bash
./gradlew bootRun
```

Home page (static): `https://localhost:8085/`  
Swagger UI: `https://localhost:8085/swagger-ui/index.html` (uses `/openapi3.yaml`).

## Build, test, and OpenAPI generation

```bash
# Dev (http — default)
./gradlew test

# Prod (https)
./gradlew test -PserverScheme=https
```

During `test`, task `openapi3` runs and generates `build/api-spec/openapi3.yaml` using the configured scheme, then copies it to `build/resources/main/static/openapi3.yaml`.

