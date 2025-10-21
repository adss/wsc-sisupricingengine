# wsc-sisupricingengine

A minimal Spring Boot service that exposes a REST consultation endpoint to retrieve the applicable price for a given brand, product, and application date.

## Build and Run

Prerequisites: Java 21 and Maven.

- Build: `mvn -q -DskipTests package`
- Run: `mvn spring-boot:run`

The service starts on port 8080 by default.

## API Documentation

- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- OpenAPI YAML: http://localhost:8080/v3/api-docs.yaml

## Structured logging

- SLF4J + Logback configured with a structured key=value console format and MDC.
- Each HTTP request is assigned a correlation ID (header: X-Correlation-Id). If the header is absent, a UUID is generated.
- The correlation ID is injected into the MDC and appears in every log line as corrId=...
- Sample log line:
  2025-01-01T12:00:00.123+00:00 level=INFO logger=com.inditex.sisuprice.api.controller.PriceController thread=http-nio-8080-exec-1 corrId=0b4d... traceId= spanId= msg="request getPrice brandId=1 productId=35455 date=2020-06-14T16:00:00"

### Tuning log levels
- Default root level is INFO. Package noise reduced (Spring/Hibernate at WARN).
- To change level at runtime, set logging.level.com.inditex.sisuprice=DEBUG in application.yml or as an env var.

## Observability (Spring Boot Actuator)

Actuator endpoints are enabled to help monitor and inspect the application.

- Health: http://localhost:8080/actuator/health (details enabled)
- Info: http://localhost:8080/actuator/info
- Metrics: http://localhost:8080/actuator/metrics

Notes:
- Endpoints are exposed via management configuration in application.yml.
- You can query a specific metric, e.g.: http://localhost:8080/actuator/metrics/jvm.memory.used

## Endpoint

GET /api/v1/prices

Query parameters:
- brandId (int, required, >=1)
- productId (long, required, >=1)
- date (ISO-8601 date-time, required) e.g. 2020-06-14T16:00:00

Example:

```
curl "http://localhost:8080/api/v1/prices?brandId=1&productId=35455&date=2020-06-14T16:00:00"
```

Successful response (200):

```
{
  "productId": 35455,
  "brandId": 1,
  "priceList": 2,
  "startDate": "2020-06-14T15:00:00",
  "endDate": "2020-06-14T18:30:00",
  "price": 25.45,
  "curr": "EUR"
}
```

If no price is applicable for the provided inputs, the service returns 404 Not Found.

Validation errors return 400 Bad Request with a JSON body providing a messages array and metadata (timestamp, status, path).

## Architecture & Design

- Hexagonal architecture (Ports & Adapters):
  - Domain model: `PriceRecord`
  - Port: `PriceRepository` (domain-driven interface)
  - Adapters:
    - Primary (driving): REST controller (`PriceController`) and service (`PriceQueryService`)
    - Secondary (driven): `PriceDbRepository` (H2/JPA)
- SOLID principles:
  - SRP: Controller handles HTTP, Service orchestrates use-case, Repository handles data access
  - OCP/LSP: Alternate repository implementations can be swapped via Spring profiles without changing core logic
  - DIP: Service depends on `PriceRepository` abstraction
- REST best practices: GET with query params, validation, 404 for not found, 400 for bad input, stable DTO (`PriceResponse`).

## Technologies

- Java 21: Modern, LTS JVM runtime used to build and run the service.
- Spring Boot (Web, Validation): Provides the REST API layer and request validation annotations.
- Spring Data JPA: Data access abstraction over JPA to interact with the database via repositories.
- H2 Database (runtime): In‑memory relational database used for local development, tests, and examples.
- Liquibase: Database change management to version and apply schema and seed data on startup.
- MapStruct: Compile‑time mapper to transform persistence/domain objects into API DTOs efficiently.
- Lombok: Reduces boilerplate (getters, constructors, logging) via annotations.
- springdoc-openapi (Swagger UI): Generates OpenAPI docs and interactive Swagger UI.
- Spring Boot Actuator: Operational endpoints for health, info, and metrics.
- Caffeine + Spring Cache: In‑memory caching of use‑case results to improve response times.
- SLF4J + Logback: Structured logging with MDC; correlation ID propagated per request.
- JUnit 5 + Mockito: Unit and integration testing framework and mocking library.

## Performance

- Efficient data extraction when using DB:
  - `PriceDbRepository#findApplicable` delegates to a JPQL query (`PriceJpaRepository.findTopApplicable`) that applies date and id filters and orders by priority in the database (uses index on BRAND_ID, PRODUCT_ID, START_DATE, END_DATE, PRIORITY). This avoids loading all rows into memory.
- Time window evaluation uses an efficient [start, end) check.

## Testing

- Integration tests cover all five required scenarios and additional cases (404 unknown product, 400 invalid input).
- Run tests: `mvn test`

## Configuration

- Default profile uses H2 in-memory DB seeded at startup via Liquibase (see `src/main/resources/db/changelog/db.changelog-master.yaml`).
- The previous Java-based memory seeding was removed; all profiles now use the H2 database initialized by Liquibase.
- H2 console enabled for convenience at `/h2-console`.

## Notes
- Selection logic prioritizes higher priority when multiple records match the time window.
- Input validation is applied to query parameters; invalid inputs will result in 400 Bad Request.


## Project layout (Hexagonal)

```
src/main/java/com/inditex/price/
├── PriceApplication
│
├── api/
│   ├── controller/
│   │   └── PriceController
│   ├── dto/
│   │   └── PriceResponse
│   ├── mapper/
│   │   └── PriceRecordMapper
│   └── GlobalExceptionHandler
│
├── domain/
│   ├── PriceRecord
│   ├── repository/
│   │   └── PriceRepository
│   └── usecase/
│       └── PriceQueryUseCase
│
├── application/
│   └── PriceQueryUseCaseImpl
│
└── infrastructure/
    ├── persistence/
    │   ├── PriceDbRepository
    │   └── jpa/
    │       ├── PriceEntity
    │       └── PriceJpaRepository
    │
    └── mapper/
        └── PriceEntityMapper (MapStruct: PriceEntity → PriceRecord)
```

src/main/resources/
- db/changelog/db.changelog-master.yaml (Liquibase schema and seed data)
- schema.sql, data.sql (legacy scripts, not used when Liquibase is enabled)