# Energy

Spring Boot (Kotlin) service that proxies Enedis Data Connect APIs for token retrieval, customer contracts, and metering curves.

## Stack

- Java 21
- Kotlin 2.4
- Spring Boot 4.1 (Web MVC)
- Spring Data JPA + H2 (file database)
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
| `energy.authorize-url` | `HOME_AUTOMATION_ENEDIS_AUTHORIZE_URL` | `https://mon-compte-particulier.enedis.fr/dataconnect/v2/oauth2/authorize` |
| `energy.subscribed-services-path` | `HOME_AUTOMATION_ENEDIS_SUBSCRIBED_SERVICES_PATH` | `/subscribed_services/v1` |
| `energy.metering-path` | `HOME_AUTOMATION_ENEDIS_METERING_PATH` | `/mesure_synchrone_auto/v2` |
| `energy.application-name` | `HOME_AUTOMATION_ENEDIS_APPLICATION_NAME` | `DEV-MIND ENERGY` |
| `server.port` | — | `8085` |
| `spring.datasource.url` | `ENERGY_DB_URL` | `jdbc:h2:file:./data/energy-users;AUTO_SERVER=TRUE` |
| `spring.datasource.username` | `ENERGY_DB_USERNAME` | `sa` |
| `spring.datasource.password` | `ENERGY_DB_PASSWORD` | *(empty)* |

## Application flow

1. `TokenService` requests an OAuth token from Enedis (`grant_type=client_credentials`) and caches it until a computed refresh time.
2. `DataConnectService` calls Enedis customer/metering endpoints using `Authorization: Bearer <token>`.
3. `ApiExceptionHandler` forwards Enedis HTTP status and body to API clients when upstream calls fail.

## API endpoints

Base path: `https://localhost:8085`

| Method | Path | Query params | Description |
| --- | --- | --- | --- |
| `GET` | `/api/enedis/token` | — | Returns current Enedis OAuth token (`access_token`, `scope`, `token_type`, `expires_in`). |
| `GET` | `/api/enedis/account` | — | Redirects the client to the Enedis DataConnect v2 consent page. |
| `GET` | `/api/enedis/redirect` | `state`, `autorisation_id` | Resolves the single PRM associated with the Enedis authorization and returns the computed consent validity window. |
| `POST` | `/api/enedis/consents` | — | Creates or updates user consent data (`userAccountId`, consent flag, validity range, associated PRMs). |
| `GET` | `/api/enedis/consents/{userAccountId}` | — | Returns consent data persisted for a user account. |
| `GET` | `/api/enedis/customers/contracts` | `usagePointId` | Returns contracts for a usage point (mapped from Enedis customer contract payload). |
| `GET` | `/api/enedis/metering/consumption-load-curve` | `start`, `end`, `usagePointId` | Returns consumption load curve for the period. |
| `GET` | `/api/enedis/metering/production-load-curve` | `start`, `end`, `usagePointId` | Returns production load curve for the period. |
| `GET` | `/api/enedis/metering/data` | `prm`, `dataType`, `startDate`, `endDate` | Returns consumption or production data for a PRM after retrieving the Enedis token server-side. |

`start` and `end` are `LocalDate` values (ISO-8601, e.g. `2026-07-01`).
`startDate` and `endDate` use the UI format `YYYY-DD-MM` (e.g. `2026-24-07`), and `dataType` must be `consumption` or `production`.

The callback no longer accepts `usage_point_id` or `code`. Enedis now returns `autorisation_id`; the application retrieves the PRM server-side through `/subscribed_services/v1`.

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
