# K0 foundation contract

Version 1. Provider: T2 foundation. Initial consumers: T3 identity and T5 usage/model. This contract provides small shared conventions; feature tasks own identity/session persistence, authorization, durable operation deduplication and domain tables.

## Identifiers and authenticated identity

Use `java.util.UUID` for account/actor, resource and durable operation request IDs; store them in PostgreSQL `uuid` columns and represent them as UUID strings in JSON. Generate resource IDs server-side with `UUID.randomUUID()`. UUID possession is never authorization. Resource reads/mutations must apply the authenticated account's ownership predicate; client-supplied owner/account fields cannot establish identity.

`com.aiknowledgebase.shared.AuthenticatedActor(UUID accountId)` is the immutable, serializable `java.security.Principal` shape. The constructor rejects null; `getName()` returns the account UUID string. T3 must establish it from successful server-side authentication/session resolution before private API consumers use it. No client header/body/query parameter may construct the trusted principal. T2 adds no login mechanism or implicit authenticated user, and the record itself performs no authorization.

A durable operation's request UUID identifies a logical submission and is retained on retries, including retries through another web instance. The owning feature persists its request identity, defines the account/operation uniqueness scope and rejects incompatible payload reuse atomically. T2 supplies no deduplication table or claim protocol.

Each HTTP attempt also gets a new server-generated correlation UUID from `RequestIdFilter`, returned in `X-Request-ID`. Incoming header values are ignored. `RequestIdFilter.requestId(request)` gives that UUID to error handling. This correlation ID differs from a durable operation request ID: retries have a new correlation ID while keeping their logical operation identity.

## Safe MVC errors

Handled Spring MVC failures return JSON with exactly `code`, `message` and `requestId`. The server-generated `requestId` matches `X-Request-ID`. `com.aiknowledgebase.shared.ErrorCode` defines the stable codes and safe messages without importing HTTP or Spring types. Application consumers throw `ApplicationException(ErrorCode)`; `shared.web.ApiExceptionHandler` maps each code to an explicit HTTP status. The web adapter selects framework codes by exception kind, never by status. `INVALID_REQUEST` and `REQUEST_BODY_UNREADABLE` deliberately share HTTP 400 while identifying different corrections.

| Code | HTTP status | Safe public message |
| --- | --- | --- |
| `INVALID_REQUEST` | 400 | The request contains missing or invalid values. Check the required fields and value formats. |
| `REQUEST_BODY_UNREADABLE` | 400 | The request body could not be read. Send a valid body in the documented format. |
| `RESOURCE_NOT_FOUND` | 404 | The requested resource is unavailable. Check the address or return to your resource list. |
| `METHOD_NOT_ALLOWED` | 405 | This request method is not supported. Use a method listed in the Allow header. |
| `UNSUPPORTED_MEDIA_TYPE` | 415 | The request content type is not supported. Use a documented Content-Type. |
| `RESPONSE_TYPE_NOT_ACCEPTABLE` | 406 | The requested response format is not supported. Use a documented Accept type. |
| `REQUEST_TOO_LARGE` | 413 | The request exceeds the allowed size. Reduce its size and try again. |
| `REQUEST_TIMED_OUT` | 503 | The server could not finish the request in time. Check its outcome before retrying. |
| `FRAMEWORK_REQUEST_REJECTED` | Framework-provided; 400 for direct application use | The server could not complete this request. Check the API requirements and response status before retrying. |
| `INTERNAL_ERROR` | 500 | The server could not complete the request. Try again later; if it persists, contact support with the request ID. |

Ordinary missing/invalid parameters, binding and input-validation failures use `INVALID_REQUEST`; malformed/unreadable bodies use `REQUEST_BODY_UNREADABLE`. Missing MVC routes/static resources use `RESOURCE_NOT_FOUND`. Later resource owners must throw the same `RESOURCE_NOT_FOUND` for missing and cross-owner lookups. The consumer fixtures demonstrate identical status/code/message behavior; actual ownership enforcement remains feature-owned.

Known method, media negotiation, upload-size and async-timeout failures use their named mappings above. Server-side conversion, missing path-variable configuration, response serialization and return-value validation failures use `INTERNAL_ERROR`; a path value missing after conversion remains `INVALID_REQUEST`. Unknown Spring `ErrorResponse`/`ErrorResponseException` failures, including `ResponseStatusException`, use `FRAMEWORK_REQUEST_REJECTED` while retaining their framework-provided status and protocol headers (for example `Allow`, `Accept`, `Retry-After`). Features use typed application errors rather than status exceptions to obtain a deliberate application code. Unhandled exceptions return safe HTTP 500 `INTERNAL_ERROR`.

Messages never include submitted values, exception details, private data or credentials. Unexpected-failure logging includes only correlation ID and exception class. Committed responses are not rewritten; Spring's unusable async-response handling remains in place. Protocol headers are preserved. This MVC advice does not claim coverage of servlet-container failures or future security-filter responses.

Clients use codes for logic and may display messages, but must not parse message text. They tolerate unknown codes with a safe fallback and use HTTP status for general protocol behavior. Feature owners extend this small catalogue only when different client behavior is needed; no actual authentication, project or AI features are supplied by these conventions.

Authentication/security-filter responses remain T3-owned and must reuse this public shape when introduced. Actuator health retains its existing response contract.

## JDBC transactions

Use Boot's managed `JdbcClient`/datasource and Spring's transaction manager. Put each atomic database state change in a short `@Transactional` service method or `TransactionTemplate` callback. Bind UUIDs as SQL parameters; do not interpolate user values. Keep ownership predicates, relevant locks and uniqueness constraints inside the transaction. Use the default PostgreSQL READ COMMITTED isolation unless a slice's actual race requires a more specific lock/isolation protocol. Checked failures need explicit rollback semantics when used with `@Transactional`.

Commit/release connections and row locks before provider calls, filesystem work or child-process waits. Claim/reserve and finalize in separate short transactions under the owning feature's durable state protocol; request UUIDs alone cannot provide idempotency or fence stale results. No generic transaction/job framework or long-lived connection is added by K0.

The real-PostgreSQL consumer test writes UUID actor/resource/request values through JDBC, proves rollback and commit, and verifies no transaction-bound resources remain before an external phase. This establishes the convention and native mechanism; feature-specific ownership, races and external-call recovery need their own tests.

## Migrations and integration fixtures

Use Boot-managed Flyway with its PostgreSQL support module. Production migrations live under `backend/src/main/resources/db/migration/`. V1 enables `vector` with `CREATE EXTENSION IF NOT EXISTS`; it creates no domain tables. Flyway owns schema initialization, including against an existing local volume that already has the extension. Startup requires a reachable database and successful migrations. Never silently baseline, repair/clean, recreate or erase a database on failure. The database role needs extension-creation permission for initial migration.

Feature owners introduce their own additive migrations with integrator-assigned version ordering. Applied migrations stay immutable. Publish later contracts under `docs/contracts/` when the first real provider/consumer pair agrees them; describe changes with affected consumers before dependent work continues.

Backend `*IT` tests use Failsafe and Spring profile `lvl2test`; `*Test` uses Surefire and `test`. Reuse `PostgresIntegrationTest` for real isolated PostgreSQL tests. Its image matches Compose: `pgvector/pgvector:0.8.7-pg17@sha256:ac08538c6f8b9904c33c8224c5e5706dbe760aca29db1d096972b4052c22a75d`. It owns an ephemeral container/random host port and creates an independent database per test. Missing Docker fails integration verification rather than skipping it. H2 cannot substitute for PostgreSQL/vector/locking behavior.

`PersistenceIT` checks clean migration, adoption of an existing vector extension, vector use, restart of the application on the same database, committed UUID data retention, transaction rollback/release, and migration failure preventing startup while rolling back partial DDL and preserving data. The restart path starts the real embedded web application on random loopback ports and verifies the production missing-route error envelope over HTTP. Consumer tables and broken migrations exist only in test sources. `ApiContractTest` uses test-only MVC consumers to check principal/UUID consumption, safe errors, protocol headers and correlation IDs; no feature endpoint or fake authenticated user is exposed in the application.

Run the native commands in [local development](../local-development.md#native-tests). Dependencies resolve through the shared BOM; Boot 4.1.2 currently manages Flyway 12.4.0, Testcontainers 2.0.5 and JUnit 6.0.3 on Java 26.
