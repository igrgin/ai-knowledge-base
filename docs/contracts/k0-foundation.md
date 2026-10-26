# K0 foundation contract

Version 1. Provider: T2 foundation. Initial consumers: T3 identity and T5 usage/model. This contract provides small shared conventions; feature tasks own identity/session persistence, authorization, durable operation deduplication and domain tables.

## Identifiers and authenticated identity

Use `java.util.UUID` for account/actor, resource and durable operation request IDs; store them in PostgreSQL `uuid` columns and represent them as UUID strings in JSON. Generate resource IDs server-side with `UUID.randomUUID()`. UUID possession is never authorization. Resource reads/mutations must apply the authenticated account's ownership predicate; client-supplied owner/account fields cannot establish identity.

`com.aiknowledgebase.shared.AuthenticatedActor(UUID accountId)` is the immutable, serializable `java.security.Principal` shape. The constructor rejects null; `getName()` returns the account UUID string. T3 must establish it from successful server-side authentication/session resolution before private API consumers use it. No client header/body/query parameter may construct the trusted principal. T2 adds no login mechanism or implicit authenticated user, and the record itself performs no authorization.

A durable operation's request UUID identifies a logical submission and is retained on retries, including retries through another web instance. The owning feature persists its request identity, defines the account/operation uniqueness scope and rejects incompatible payload reuse atomically. T2 supplies no deduplication table or claim protocol.

Each HTTP attempt also gets a new server-generated correlation UUID from `RequestIdFilter`, returned in `X-Request-ID`. Incoming header values are ignored. `RequestIdFilter.requestId(request)` gives that UUID to error handling. This correlation ID differs from a durable operation request ID: retries have a new correlation ID while keeping their logical operation identity.

## Safe MVC errors

The shared `ApiExceptionHandler` returns JSON with exactly `code`, `message` and `requestId` for handled Spring MVC exceptions. `requestId` matches `X-Request-ID`. The HTTP status remains authoritative; supported MVC protocol headers such as `Allow` are retained.

| Status | Code | Safe default message |
| --- | --- | --- |
| 400 | `BAD_REQUEST` | `Bad Request` |
| 401 | `UNAUTHENTICATED` | `Unauthorized` |
| 403 | `FORBIDDEN` | `Forbidden` |
| 404 | `NOT_FOUND` | `Not Found` |
| 500 | `INTERNAL_ERROR` | `Internal Server Error` |
| Other MVC status | `HTTP_<status>` | Standard HTTP reason phrase, or `Request failed` |

Malformed UUIDs are 400. Known status exceptions retain their status but do not return their exception reason. Unexpected exceptions return 500. Never expose SQL, exception details, stack traces, submitted values, file paths, credentials, document/chat content or another owner's resource existence in the body. Cross-owner lookups use the same safe 404 as missing resources. Unexpected-failure logs contain only correlation UUID and exception class, not exception text or stack traces. Authentication/security filters are feature-owned; when T3 adds them, their rejection handlers must reuse this public shape because MVC advice does not handle filter-level failures. Actuator health retains its own existing response contract. Servlet/container rejections outside MVC are not covered by this handler.

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
