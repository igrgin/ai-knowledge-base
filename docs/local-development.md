# Local development

## Tools

Use Java 26 (`JAVA_HOME` must point to it when configured), Maven Wrapper 3.9.16 and Node 24.22.1/npm 11.19.1. Docker with Compose is required for the local database; a running Docker engine is also required for backend integration tests. Basic Java and frontend tests run without Docker. No global tool installation is changed by the repository.

The only optional helper is `./scripts/setup-node`: a self-contained installer that verifies the official Node archive SHA-256 and extracts under ignored `.local/tools`. It needs Python 3, curl and tar. Add the resulting platform directory to PATH; for example:

```sh
./scripts/setup-node
export PATH="$PWD/.local/tools/node-v24.22.1-darwin-arm64/bin:$PATH"
```

Supported directories end in `darwin-arm64`, `darwin-x64`, `linux-arm64` or `linux-x64`. CI uses setup-node directly and never invokes this installer.

## Development and package

From the repository root, start PostgreSQL and the Java backend:

```sh
docker compose up -d --wait db
./mvnw -pl backend spring-boot:run -Dspring-boot.run.profiles=dev
```

In another terminal with project Node on PATH:

```sh
npm ci --prefix frontend
npm run dev --prefix frontend
```

The frontend listens on 127.0.0.1:5173 and proxies readiness to 127.0.0.1:8080. Neither process silently chooses another frontend port. If defaults are occupied, select matching backend/proxy ports explicitly:

```sh
SERVER_PORT=18081 ./mvnw -pl backend spring-boot:run -Dspring-boot.run.profiles=dev
APP_PORT=18081 FRONTEND_PORT=15173 npm run dev --prefix frontend
```

Stop the processes with Ctrl-C. They run directly through their standard tools; there is no combined launcher. Backend Java edits require a restart; Vite reloads frontend changes.

Package the Maven reactor; backend packaging installs locked frontend dependencies and builds fresh assets. The default command also runs browser tests, so complete [browser setup](#native-tests) first. Use `./mvnw -DskipTests=true package` when test execution is intentionally skipped:

```sh
./mvnw package
SERVER_PORT=18081 java -jar backend/target/backend-0.1.0-SNAPSHOT.jar --spring.profiles.active=dev
```

The backend JAR includes `frontend/dist` as static resources. The separate `pdf-extractor/target/pdf-extractor-0.1.0-SNAPSHOT.jar` is alongside its own module's build output. Both use Java 26; the extractor exposes only `--version` and exits 2 for unsupported extraction arguments. No parser feature is implemented.

```sh
java -Xmx256m -jar pdf-extractor/target/pdf-extractor-0.1.0-SNAPSHOT.jar --version
```

`docker compose stop db` and `docker compose down` preserve the named `ai-knowledge-base_database` volume. Do not remove user volumes to fix startup. Flyway runs the versioned migrations during backend startup. Flyway creates its schema history and V1 enables pgvector; there are no domain tables. An existing local volume with pgvector already enabled is supported. Startup fails if the database is unavailable or a migration fails; a running application still reports later database outages through readiness. Changing initialization credentials does not rewrite an existing database.

## Configuration and profiles

`application.yml` contains common static settings: loopback/8080, application name, bounded pool and readiness exposure. It has no datasource URL/credentials and never defaults to `dev`.

| Spring profile | Location and use |
| --- | --- |
| `dev` | Main resources; local datasource literals; explicitly select in native run commands |
| `prod` | Main resources; requires SERVER_ADDRESS, SERVER_PORT, DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD; no fallback values |
| `test` | Test resources; basic-test profile, selected by backend Surefire |
| `itest` | Test resources; application integration profile, selected by Maven Failsafe |

Prod resolves deployment values through Spring configuration placeholders without fallback values; it overrides base bind settings and uses no dev credentials. Test YAMLs are absent from the distributed runtime. Backend basic tests use standalone MVC consumers, while integration tests start the application against isolated real PostgreSQL databases. There is no custom deployment validator.

`.env.example` lists local Compose `DB_*` and Java `SPRING_DATASOURCE_*` variables. Compose reads `.env`; Java reads exported environment values. Nothing sources/evaluates `.env`. For custom local database settings, export matching values for both:

```sh
export DB_PORT=54331 DB_NAME=custom_local DB_USER=custom_user DB_PASSWORD='local-password'
export SPRING_DATASOURCE_URL='jdbc:postgresql://127.0.0.1:54331/custom_local?connectTimeout=2&socketTimeout=3'
export SPRING_DATASOURCE_USERNAME="$DB_USER" SPRING_DATASOURCE_PASSWORD="$DB_PASSWORD"
```

`SERVER_PORT` controls Java directly; `APP_PORT` controls the Vite backend proxy. Set both to the same value for custom development ports. `FRONTEND_PORT` changes the Vite listener. Credentials never use `VITE_` variables or enter browser assets.

## Native tests

```sh
npm ci --prefix frontend
npx --prefix frontend playwright install chromium
./mvnw -Pintegration-tests verify
```

Prepare Chromium once per Playwright version using the first two commands; Linux may require `playwright install --with-deps chromium`. Maven does not install Node, browsers or OS packages.

Its backend module runs `npm ci --engine-strict --include=dev` at `initialize`, `npm run build` at `generate-resources`, and `npm --ignore-scripts test` at `test`. The existing package test script runs once; suppressing pre/post hooks here avoids a second build.

Native `npm test --prefix frontend` remains the frontend-only command: its normal `pretest` hook builds once. Future essential test setup must live in the main test script or Playwright configuration.

Playwright starts/stops its own Vite preview on loopback 15175. Three isolated browser tests cover successful readiness and retry, HTTP503-to-success recovery, and connection failure. These mocks exist only in Playwright; application code always calls the real readiness endpoint.

The explicit Maven `integration-tests` build profile activates Failsafe `*ITests` in both Java modules with Spring profile `itest`. Surefire excludes `*ITests` so basic tests remain Docker-free. The Maven profile is distinct from the Spring runtime profile. The backend builds frontend assets before resource copying, including when tests are skipped. Its npm executions are absent from the extractor and BOM. Backend selection therefore requires the project Node/npm on PATH even for Java-only tests; strict engine and lockfile checks fail the Maven build on drift.

- Backend Surefire runs `ApiContractTest`, checking safe typed/framework/unexpected errors, protocol headers, server-generated correlation IDs and committed-response guards through test-only MVC endpoints. Extractor basic selection currently has no tests: removing the overlapping classpath probe means extractor execution is verified only after packaging. T6 will add meaningful parser/timeout/error tests. `./mvnw test` needs no Docker/full startup.
- Backend Failsafe runs only `ErrorRepresentationITests` with `@SpringBootTest(RANDOM_PORT)`, owning the real HTTP incompatible-Accept JSON 406 regression, protocol header and server-generated filter/advice correlation. `PostgresIntegrationTest` supplies the same pinned pgvector/PostgreSQL image as Compose on a random port. The Boot test creates its own database through `@DynamicPropertySource` and closes/removes its cached context with `@DirtiesContext` after the class. The suite fails rather than silently skipping when Docker is unavailable. Its consumer endpoint exists only in test sources outside production component scanning.
- Dedicated migration, existing-vector adoption, sentinel-restart and failed-migration checks have been removed, along with general missing-route and dummy successful-response representation smoke checks. Real persisted identity/project state, durable uploads and lifecycle recovery belong to T3/T4/T6/T11; feature migrations must verify their real effects, and T13 adds integration evidence. New feature tests do not automatically replace special pre-Flyway existing-volume adoption coverage. Typed application 404 and method 405 remain owned by MVC tests; real feature responses own their success formats when introduced.
- Extractor Failsafe runs `ExtractorPackageITests`, launching the actual built JAR with the same Java runtime and bounded heap to check its probe and unsupported-argument exit.

Select modules and test levels independently from the repository root. Omit `-pl` to run the whole reactor, use `-pl backend` or `-pl pdf-extractor` for one Java module, or `-pl backend,pdf-extractor` for both Java modules. Dependency versions remain shared through the BOM and plugin versions through the root parent.

| Selection | Native command |
| --- | --- |
| Java basic + browser tests, no Docker | `./mvnw test` |
| Java basic + browser + Java integration tests | `./mvnw -Pintegration-tests verify` |
| Backend basic + browser tests | `./mvnw -pl backend test` |
| Extractor basic only, no Node required | `./mvnw -pl pdf-extractor test` |
| Java basic only; frontend assets still built | `./mvnw -DskipFrontendTests=true test` |
| Browser tests via Maven, no Java test execution | `./mvnw -pl backend -DskipUnitTests=true test` |
| Java integration only; frontend assets still built | `./mvnw -Pintegration-tests -DskipUnitTests=true -DskipFrontendTests=true verify` |
| Basic/browser checks and packages, no Java integration | `./mvnw -Pintegration-tests -DskipITs=true verify` |
| Package without Java/browser test execution | `./mvnw -DskipTests=true package` |

`verify` still performs earlier compilation/package phases. `skipUnitTests` controls Java Surefire only, `skipITs` controls Failsafe only, and `skipFrontendTests` controls browser execution only. Both `skipUnitTests` and `skipFrontendTests` default to `skipTests`; leave narrower flags unset for a global skip, since explicit overrides win. `maven.test.skip` is not the browser skip contract. For example, `./mvnw -pl pdf-extractor -Pintegration-tests -DskipUnitTests=true verify` builds the extractor JAR and runs only `ExtractorPackageITests`.

`./mvnw clean` removes Maven targets plus generated frontend `dist`, `test-results`, `playwright-report` and root `*.tsbuildinfo`. It preserves source, manifests/lockfiles, `node_modules`, npm/browser caches, local runtimes and private application data. The next `npm ci` recreates `node_modules`. Do not run two Maven invocations against this checkout's frontend concurrently: they share generated files and preview port 15175.

The database fixture owns only its disposable test container and databases; it does not connect to the development volume or bind a fixed host port. Extractor tests terminate only their own children. Tests do not call AI providers or alter user development data/listeners.

CI has one `Tests` job: Java/Node setup, `npm ci` and Playwright browser/OS dependency preparation, then the single `./mvnw -B -Pintegration-tests verify` entry point. There is one frontend build and browser run. The preparation install and Maven install both run deliberately; npm download caching helps, while each install validates the lockfile. No repository/document/PR metadata checks or custom build/test wrappers run there.

## Compatibility

Core pins remain Boot 4.1.2, Java 26, Maven 3.9.16, Node 24.22.1, React 19.3.1, TypeScript 7.0.2, Vite 8.3.5/plugin 6.1.2 and Playwright 1.64.1. Compose pins pgvector 0.8.7/PostgreSQL 17 by image manifest digest.

The reactor includes a standalone `bom` module (`ai-knowledge-base-bom`) that imports Spring Boot's dependency BOM 4.1.2. Backend and PDF extractor explicitly import the shared BOM; their root parent retains Spring Boot's starter parent for Java/build/plugin defaults, including Failsafe 3.5.6. The BOM has no project parent, avoiding an import/parent cycle, and adds no runtime dependencies. Keep the Boot dependency BOM and starter-parent versions aligned when upgrading. Backend Flyway and test dependencies use the same Boot BOM: Flyway 12.4.0, Testcontainers 2.0.5 and JUnit 6.0.3. The extractor retains JUnit. `./mvnw dependency:tree` shows resolved Java dependencies.

## Persistence and shared contracts

Flyway is the only schema migration mechanism. Add feature-owned SQL under `backend/src/main/resources/db/migration/` with an integrator-assigned `V<number>__description.sql`; keep applied migrations unchanged and introduce a new additive version for changes. Do not enable automatic baselining or clean an existing database to fix a failure. Inspect and correct the failing migration/configuration while preserving stored data. The database user must be allowed to enable the installed `vector` extension on first startup.

[K0 foundation conventions](contracts/k0-foundation.md) define UUID identity/resource/operation IDs, trusted server-established account identity, safe MVC errors, and short JDBC transactions. Feature owners add their own tables and publish consumer-reviewed contracts when a real interface is needed. No identity/session, resource ownership, operation deduplication or job processing feature is supplied by this foundation.
