# Spring Modulith investigation

Research date: 2026-10-26, Europe/Zagreb. Repository baseline: `776396722e499ff9108b87d1eba3ff091011cf62`. This is supporting research; the current [specification](../scopes/initial-application/spec.md#d-a-one-application-with-clear-internal-responsibilities) and [plan](../scopes/initial-application/plan.md) own the resulting decisions and implementation authority.

## What Modulith adds

A modular monolith is an application design; Spring Modulith is an optional library/toolset supporting that design within Spring Boot. It models domain modules, verifies boundaries, supports focused integration tests and generates documentation. Boot remains the application runtime. [Spring Modulith overview](https://spring.io/projects/spring-modulith/)

By default, direct subpackages of the application package become logical modules. Their public base-package types form the exposed interface; nested packages are internal, even when Java visibility requires their classes to be public. `@NamedInterface` can deliberately expose an additional package. This works inside one backend artifact: no Maven module per feature is required. [Module fundamentals](https://docs.spring.io/spring-modulith/reference/fundamentals.html)

For illustration, `identity`, `projects`, `documents`, `chats` and `usage` could expose narrow operations while keeping repositories and implementation classes internal. These are candidate boundaries, not a final package design; accepted lifecycle, source-viewing and ingestion responsibilities still need deliberate placement. Modulith can verify a chosen arrangement, but cannot decide the domain boundaries for us. [Module model](https://docs.spring.io/spring-modulith/reference/fundamentals.html#modules)

`ApplicationModules.of(KnowledgeBaseApplication.class).verify()` checks module dependency cycles and references into internals. Optional `@ApplicationModule(allowedDependencies = …)` declarations restrict which other modules or named interfaces a module may use. These are executable architectural checks, rather than additional process isolation. [Verification rules](https://docs.spring.io/spring-modulith/reference/verification.html)

`@ApplicationModuleTest` boots one module, its direct dependencies, or its complete dependency tree; unavailable cross-module beans require suitable mocks or included dependencies. `Documenter` produces component diagrams and module canvases from the discovered structure. [Module tests](https://docs.spring.io/spring-modulith/reference/testing.html), [documentation](https://docs.spring.io/spring-modulith/reference/documentation.html)

## Fit for this repository

The [root reactor](../../pom.xml) already contains `bom`, `backend` and `pdf-extractor`. The [backend entry point](../../backend/src/main/java/com/aiknowledgebase/KnowledgeBaseApplication.java) is still a shell; the [extractor](../../pdf-extractor/src/main/java/com/aiknowledgebase/extractor/ExtractorMain.java) implements only a version probe. T6 plans actual parsing in a child process with bounded heap/deadline. Modulith would organize future backend features; it supplies neither extractor isolation nor automatic worker/job scaling. That separation follows its package-based model and the existing [runtime plan](../scopes/initial-application/plan.md#approach-and-affected-files). [Modulith fundamentals](https://docs.spring.io/spring-modulith/reference/fundamentals.html)

Proposal: introduce structural verification and generated documentation when real feature packages begin interacting. A test-scoped `spring-modulith-starter-test` includes its testing and documentation support; focused module tests can follow meaningful feature behavior. Benefits grow with the dependency graph: earlier boundary failures and inspectable dependencies. Costs include another dependency family, package conventions and maintaining test collaborators; today’s shell offers little immediate payoff. [Test-starter source](https://github.com/spring-projects/spring-modulith/blob/2.1.2/spring-modulith-starters/spring-modulith-starter-test/pom.xml), [testing constraints](https://docs.spring.io/spring-modulith/reference/testing.html)

Runtime events are optional for this proposal. Spring bean event listeners communicate within an application context, not a distributed job queue. Modulith’s publication registry records transactional listener deliveries and completion/retry state, but should not become a second ingestion/answer/purge scheduler competing with the accepted PostgreSQL leases, fencing and admission authority. Retain those [coordination contracts](../scopes/initial-application/spec.md#d-i-shared-coordination-and-staged-growth). [Spring event scope](https://docs.spring.io/spring-framework/reference/core/beans/context-introduction.html#context-functionality-events), [Modulith registry](https://docs.spring.io/spring-modulith/reference/events.html#publication-registry)

## Compatibility and evidence limits

Spring’s generations mapping pairs Boot **4.1.x** with Modulith **2.1.x**. Released **2.1.2** source uses Boot **4.1.2**, matching this repository; its release notes record that upgrade. [Spring generations](https://spring.io/projects/generations/), [released POM](https://github.com/spring-projects/spring-modulith/blob/2.1.2/pom.xml), [release notes](https://github.com/spring-projects/spring-modulith/releases/tag/2.1.2)

Sources disagree in freshness: the reference labels itself 2.1.1, while its compatibility appendix still lists 2.0 snapshot-era information; the project/release pages show 2.1.2. Prefer the generations mapping and tagged release evidence for this assessment. No dependency upgrade, Java 26 compatibility test, implementation or performance measurement was performed. [Reference appendix](https://docs.spring.io/spring-modulith/reference/appendix.html#compatibility-matrix)

Maven artifact boundaries, a possible microservices design and additional extractor processes remain separate investigations. This note only proposes optional enforcement inside the accepted modular backend; it does not replace the scale plan.
