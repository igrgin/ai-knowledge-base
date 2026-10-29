# AI knowledge base architecture map

## System Overview

This is the entry point for Elephant-Goldfish (EGM) architecture and package contracts.
The initial contract coverage is the backend's shared error boundary. The
[README](README.md) describes demonstrated product behavior and startup.

The current application is a React readiness screen served by a Spring Boot
backend, with PostgreSQL/pgvector and Flyway. The standalone extractor implements
a version probe. Authentication, project/PDF/chat features, durable workers and
AI calls are planned behavior, not implemented boundaries in this map.

## Subdirectories & System Boundaries

| Boundary | Current interface and responsibility |
| --- | --- |
| [Shared application errors](backend/src/main/java/com/aiknowledgebase/shared/EGM.md) | `ApplicationException` and `ErrorCode`; application failures independent of HTTP. |
| [Shared web adapter](backend/src/main/java/com/aiknowledgebase/shared/web/EGM.md) | `ApiError`, `ApiExceptionHandler` and `RequestIdFilter`; safe MVC responses and request correlation. |
| [Backend entry point](backend/src/main/java/com/aiknowledgebase/KnowledgeBaseApplication.java) and [configuration](backend/src/main/resources/application.yml) | Spring Boot startup, static UI serving and database-backed Actuator readiness. |
| [Database migration](backend/src/main/resources/db/migration/V1__enable_vector.sql) | V1 enables `vector`; feature-owned domain schemas are introduced by later tasks. |
| [React shell](frontend/src/main.tsx) | Calls `/actuator/health/readiness` and shows checking, ready or unavailable state with retry. |
| [Extractor entry point](pdf-extractor/src/main/java/com/aiknowledgebase/extractor/ExtractorMain.java) | `--version` reports `contractVersion`, `javaFeature` and `extractionAvailable:false`; unsupported arguments exit 2. |

Only the two shared packages have EGM contracts. Other rows link directly to
implementation evidence. Add package contracts as real feature boundaries appear.

## Global Invariants

- [K0](docs/contracts/k0-foundation.md) owns shared identity, error, transaction
  and migration conventions. Its consumer obligations remain distinct from the
  behavior already implemented in the linked package contracts.
- [Runtime profiles](docs/local-development.md#configuration-and-profiles)
  separate local/deployment settings from test resources. The
  [backend POM](backend/pom.xml) packages built frontend assets into the backend;
  the [extractor POM](pdf-extractor/pom.xml) packages a separate executable JAR.
- [Product invariants](docs/designs/application.md#invariants)
  describe the required target. The [roadmap](docs/roadmap.md) owns task completion;
  the [contract index](docs/contracts/README.md#contract-availability) owns gate availability.

## Global Side Effects

- [The browser shell](frontend/src/main.tsx) makes same-origin readiness requests.
  [Database configuration](backend/src/main/resources/application.yml) enables
  the readiness database check; Flyway applies the [versioned SQL migration](backend/src/main/resources/db/migration/V1__enable_vector.sql)
  at startup, including creation of `vector` when absent.
- [The web adapter](backend/src/main/java/com/aiknowledgebase/shared/web/EGM.md#side-effects)
  sets request/response correlation metadata and logs a restricted description
  of unexpected MVC failures.
- [The extractor probe](pdf-extractor/src/main/java/com/aiknowledgebase/extractor/ExtractorMain.java)
  writes to standard output/error and can terminate its process with exit 2.
  Private PDF storage and provider-call side effects belong to future features.

## System Verification

Run from the repository root after the prerequisites in
[local development](docs/local-development.md#native-tests):

- `./mvnw test` — Java basic and browser checks; no Docker requirement.
- `./mvnw -Pintegration-tests verify` — also runs real PostgreSQL, HTTP and
  packaged-extractor integration checks; Docker is required.

Package contracts point to the tests that own their behavior. Commands describe
verification entry points, not evidence that a particular candidate has passed.

## Design and Contract Maintenance

[Application design](docs/designs/application.md) owns product requirements and proposed mechanisms; the [roadmap](docs/roadmap.md) owns task authority and progress, and the [contract index](docs/contracts/README.md) owns gate availability. Follow the [EGM workflow](docs/egm-workflow.md) for design, review, implementation handoff and agent assignments, and [remote work](docs/remote-work.md) for issue/branch/PR operations.

K0 stays canonical. Update affected package contracts alongside implementation and reconcile accepted design amendments at their owning source. Future feature proposals remain in design records until accepted and implemented; package maps describe actual code and explicit consumer obligations. T3 is the proposed next feature-design task and has not started.
