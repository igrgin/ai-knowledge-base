# K0 foundation contract

Version 1. Provider: T2 foundation. Initial consumers: T3 identity and T5 usage/model. This contract provides small shared conventions; feature tasks own identity/session persistence, authorization, durable operation deduplication and domain tables.

## Identifiers and trusted identity

Use `java.util.UUID` for account/actor, resource and durable operation request IDs; store them in PostgreSQL `uuid` columns and represent them as UUID strings in JSON. Generate resource IDs server-side with `UUID.randomUUID()`. UUID possession is never authorization. Resource reads/mutations must apply the authenticated account's ownership predicate; client-supplied owner/account fields cannot establish identity.

K0 defines the identity convention, without a concrete Java principal: trusted account identity comes from successful server-side authentication/session resolution, never a client header/body/query parameter. T3 owns `AuthenticatedActor` or the concrete principal representation required by its real authentication and persistent-session implementation. T2 provides no principal implementation, login mechanism or implicit authenticated user. Identity alone does not authorize access; each resource owner must enforce ownership.

A durable operation's request UUID identifies a logical submission and is retained on retries, including retries through another web instance. The owning feature persists its request identity, defines the account/operation uniqueness scope and rejects incompatible payload reuse atomically. T2 supplies no deduplication table or claim protocol.

Each HTTP attempt also gets a new server-generated correlation UUID from `RequestIdFilter`, returned in `X-Request-ID`. Incoming header values are ignored. `RequestIdFilter.requestId(request)` gives that UUID to error handling. This correlation ID differs from a durable operation request ID: retries have a new correlation ID while keeping their logical operation identity.

## Safe MVC errors

Handled Spring MVC failures return `application/json` with exactly `code`, `message` and `requestId`, including when the request Accept header excludes JSON; success-response negotiation is unaffected. The server-generated `requestId` matches `X-Request-ID`. `com.aiknowledgebase.shared.ErrorCode` defines the stable codes and safe messages without importing HTTP or Spring types. Application consumers throw `ApplicationException(ErrorCode)`; `shared.web.ApiExceptionHandler` maps each code to an explicit HTTP status. The web adapter selects framework codes by exception kind, never by status. `INVALID_REQUEST` and `REQUEST_BODY_UNREADABLE` deliberately share HTTP 400 while identifying different corrections.

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

Ordinary missing/invalid parameters, binding and input-validation failures use `INVALID_REQUEST`; malformed/unreadable bodies use `REQUEST_BODY_UNREADABLE`. Missing MVC routes/static resources use `RESOURCE_NOT_FOUND`. Later resource owners must throw the same `RESOURCE_NOT_FOUND` for missing and cross-owner lookups. Actual ownership enforcement and two-account denial tests belong to the resource-owning feature; T2 retains the shared public error mapping without artificial cross-owner endpoints.

Known method, media negotiation, upload-size and async-timeout failures use their named mappings above. Server-side conversion, missing path-variable configuration, response serialization and return-value validation failures use `INTERNAL_ERROR`; a path value missing after conversion remains `INVALID_REQUEST`. Unknown Spring `ErrorResponse`/`ErrorResponseException` failures, including `ResponseStatusException`, use `FRAMEWORK_REQUEST_REJECTED` while retaining their framework-provided status and protocol headers (for example `Allow`, `Accept`, `Retry-After`). Features use typed application errors rather than status exceptions to obtain a deliberate application code. Unhandled exceptions return safe HTTP 500 `INTERNAL_ERROR`.

Messages never include submitted values, exception details, private data or credentials. Unexpected-failure logging includes only correlation ID and exception class. Committed responses are not rewritten; Spring's unusable async-response handling remains in place. Protocol headers are preserved. This MVC advice does not claim coverage of servlet-container failures or future security-filter responses.

Clients use codes for logic and may display messages, but must not parse message text. They tolerate unknown codes with a safe fallback and use HTTP status for general protocol behavior. Feature owners extend this small catalogue only when different client behavior is needed; no actual authentication, project or AI features are supplied by these conventions.

Authentication/security-filter responses remain T3-owned and must reuse this public shape when introduced. Actuator health retains its existing response contract.

## JDBC transactions

Use Boot's managed `JdbcClient`/datasource and Spring's transaction manager. Put each atomic database state change in a short `@Transactional` service method or `TransactionTemplate` callback. Bind UUIDs as SQL parameters; do not interpolate user values. Keep ownership predicates, relevant locks and uniqueness constraints inside the transaction. Use the default PostgreSQL READ COMMITTED isolation unless a slice's actual race requires a more specific lock/isolation protocol. Checked failures need explicit rollback semantics when used with `@Transactional`.

Commit/release connections and row locks before provider calls, filesystem work or child-process waits. Claim/reserve and finalize in separate short transactions under the owning feature's durable state protocol; request UUIDs alone cannot provide idempotency or fence stale results. No generic transaction/job framework or long-lived connection is added by K0.

These are transaction conventions, not a requirement for a generic T2 transaction demonstration. The first real feature implementation must verify its owned atomic state changes and external-call boundaries. T3/T4 own identity/project transactions; T5/T6/T9 own their reservations/jobs/answer boundaries. Real ownership, races, idempotency and recovery require those feature tests; T13 adds integrated evidence rather than replacing them.

## Migrations and integration fixtures

Use Boot-managed Flyway with its PostgreSQL support module. Production migrations live under `backend/src/main/resources/db/migration/`. V1 enables `vector` with `CREATE EXTENSION IF NOT EXISTS`; it creates no domain tables. Flyway owns schema initialization, including against an existing local volume that already has the extension. Startup requires a reachable database and successful migrations. Never silently baseline, repair/clean, recreate or erase a database on failure. The database role needs extension-creation permission for initial migration.

Feature owners introduce their own additive migrations with integrator-assigned version ordering. Applied migrations stay immutable. Publish later contracts under `docs/contracts/` when the first real provider/consumer pair agrees them; describe changes with affected consumers before dependent work continues.

Integration-test classes end in `ITests`. Failsafe explicitly includes `**/*ITests.java` and selects the Spring profile `itest`; Surefire excludes those classes so basic `test` checks remain Docker-free. Basic tests use Surefire and Spring profile `test`. The Maven `integration-tests` build profile remains separate from Spring runtime profiles. Ordinary application integration tests use `@SpringBootTest` with real PostgreSQL; HTTP tests select `RANDOM_PORT`. Explicit manual startup is reserved for future feature tests that actually require restart or startup-failure control, not a requirement to maintain generic lifecycle checks. `PostgresIntegrationTest` supplies only PostgreSQL plumbing using the Compose image, `pgvector/pgvector:0.8.7-pg17@sha256:ac08538c6f8b9904c33c8224c5e5706dbe760aca29db1d096972b4052c22a75d`, on a random host port. JUnit owns the disposable container per class. Each Boot class supplies an independent database through its own `@DynamicPropertySource` and closes/evicts its context with `@DirtiesContext(AFTER_CLASS)`, so cached contexts cannot outlive their database/container. Missing Docker fails integration verification. H2 cannot substitute for owned PostgreSQL/vector/locking behavior.

T2 does not maintain standalone tests of pre-enabled-vector adoption, synthetic-row restart retention or intentionally broken migrations. These checks were useful setup evidence, but feature owners must test their actual persisted state, migrations and recovery instead. The special pre-Flyway existing-volume adoption path loses dedicated automated coverage; normal fresh-database feature tests do not automatically replace it. Runtime migration/data-preservation rules above remain unchanged. T3/T4 own persisted identity/project behavior, T6 owns durable upload/restart behavior, and T11 owns purge/restore recovery; T13 adds integrated evidence. No generic sentinel, broken-migration fixture or transaction demonstration is required.

Focused MVC tests own our error classification, safe bodies, protocol headers, request correlation and committed-response guard. Consolidate the fallback status examples, remove the redundant missing-parameter demonstration and use one representative committed-response case for the shared guard. One real servlet HTTP fixture retains F1's restrictive-Accept JSON 406 regression, protocol header and filter/advice correlation behavior. Standalone missing-route wiring and dummy successful JSON/plain-text checks are removed; real feature responses own their success formats when introduced. UUID echo and fake ownership demonstrations remain absent; no actual authentication, deduplication or resource-ownership coverage is claimed.

The packaged extractor launch is retained as the runtime/manifest/exit contract; its overlapping classpath-launch test is removed. Extractor execution therefore occurs during integration verification, not basic `test`. T6 owns real parser behavior when introduced. The three frontend shell browser checks retain their distinct UI success/retry, non-2xx and network-failure coverage until that UI is replaced.

Backend Maven invokes npm dependency installation/build/browser checks in order at initialize/generate-resources/test. `skipFrontendTests` defaults to `skipTests` and controls browser execution only; Java `skipUnitTests`/`skipITs` retain their separate meanings. Assets still build when test execution is skipped. Native npm remains supported; Node/browser preparation is external, and Maven Clean removes only generated frontend outputs.

Run the native commands in [local development](../local-development.md#native-tests). Dependencies resolve through the shared BOM; Boot 4.1.2 currently manages Flyway 12.4.0, Testcontainers 2.0.5 and JUnit 6.0.3 on Java 26.
