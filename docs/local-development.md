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

Build the frontend before packaging the Maven reactor:

```sh
npm run build --prefix frontend
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
npm test --prefix frontend
./mvnw -Pintegration-tests verify
```

`npm test` uses npm's normal `pretest` build hook, then Playwright starts/stops its own Vite preview on loopback 15175. Three isolated browser tests cover successful readiness and retry, HTTP503-to-success recovery, and connection failure. These mocks exist only in Playwright; application code always calls the real readiness endpoint.

The explicit Maven `integration-tests` build profile activates Failsafe `*ITests` in both Java modules with Spring profile `itest`. Surefire excludes `*ITests` so basic tests remain Docker-free. The Maven profile is distinct from the Spring runtime profile. Run the frontend build/test first so the backend package includes the current browser assets.

- Backend Surefire runs `ApiContractTest`, checking UUID resource/operation consumption, safe errors and server-generated request IDs with test-only MVC endpoints. Extractor Surefire runs `ExtractorLaunchTest`, checking the Java child contract. `./mvnw test` needs no Docker/full startup.
- Backend Failsafe runs `PersistenceITests` and `ExistingVectorITests` with nonweb `@SpringBootTest` contexts, checking clean migration/vector support, UUID persistence, transaction commit/rollback/release, and adoption of a vector extension installed before application startup. `PostgresIntegrationTest` supplies the same pinned pgvector/PostgreSQL image as Compose on a random port. Each Boot test class creates its own database through `@DynamicPropertySource` and closes/removes its cached context with `@DirtiesContext` after the class, so a context cannot be reused against a stopped container. `PersistenceLifecycleITests` creates a fresh database per test and controls application startup explicitly to check restart/data retention and failed-migration startup/DDL rollback/data preservation. Its restart path also checks the production missing-route error envelope over HTTP on random loopback ports. The suite fails rather than silently skipping when Docker is unavailable.
- Backend Failsafe also runs `ErrorRepresentationITests` with `@SpringBootTest(RANDOM_PORT)`, checking JSON error envelopes for incompatible Accept, missing-resource and method failures over real servlet HTTP, preserving protocol headers and successful JSON/plain-text responses. Its consumer endpoints exist only in test sources.
- Extractor Failsafe runs `ExtractorPackageITests`, launching the actual built JAR with the same Java runtime and bounded heap to check its probe and unsupported-argument exit.

Select modules and test levels independently from the repository root. Omit `-pl` to run the whole reactor, use `-pl backend` or `-pl pdf-extractor` for one Java module, or `-pl backend,pdf-extractor` for both Java modules. Dependency versions remain shared through the BOM and plugin versions through the root parent.

| Test level | Native Maven command (add the desired `-pl` selection) |
| --- | --- |
| Basic tests only (Surefire) | `./mvnw test` |
| Basic and integration tests (Surefire + Failsafe) | `./mvnw -Pintegration-tests verify` |
| Integration tests only (Failsafe) | `./mvnw -Pintegration-tests -DskipUnitTests=true verify` |

For example, `./mvnw -pl pdf-extractor -Pintegration-tests -DskipUnitTests=true verify` builds the extractor JAR and runs only `ExtractorPackageITests`. `verify` still performs the earlier compilation/package phases; the flag skips Surefire test execution. `-DskipITs=true` skips Failsafe execution, and `-DskipTests=true` skips both test plugins with the pinned Surefire/Failsafe 3.5.6. Leave `skipUnitTests` unset when using the global skip flag; explicitly setting it overrides Surefire's inherited default from `skipTests`.

The database fixture owns only its disposable test container and databases; it does not connect to the development volume or bind a fixed host port. Consumer tables and the intentionally broken V2 migration live only in test sources. Extractor tests terminate only their own children. Tests do not call AI providers or alter user development data/listeners.

CI has one `Tests` job: environment/dependency setup, `npm test --prefix frontend`, then `./mvnw -B -Pintegration-tests verify`. No repository/document/PR metadata checks or custom build/test wrappers run there.

## Compatibility

Core pins remain Boot 4.1.2, Java 26, Maven 3.9.16, Node 24.22.1, React 19.3.1, TypeScript 7.0.2, Vite 8.3.5/plugin 6.1.2 and Playwright 1.64.1. Compose pins pgvector 0.8.7/PostgreSQL 17 by image manifest digest.

The reactor includes a standalone `bom` module (`ai-knowledge-base-bom`) that imports Spring Boot's dependency BOM 4.1.2. Backend and PDF extractor explicitly import the shared BOM; their root parent retains Spring Boot's starter parent for Java/build/plugin defaults, including Failsafe 3.5.6. The BOM has no project parent, avoiding an import/parent cycle, and adds no runtime dependencies. Keep the Boot dependency BOM and starter-parent versions aligned when upgrading. Backend Flyway and test dependencies use the same Boot BOM: Flyway 12.4.0, Testcontainers 2.0.5 and JUnit 6.0.3. The extractor retains JUnit. `./mvnw dependency:tree` shows resolved Java dependencies.

## Persistence and shared contracts

Flyway is the only schema migration mechanism. Add feature-owned SQL under `backend/src/main/resources/db/migration/` with an integrator-assigned `V<number>__description.sql`; keep applied migrations unchanged and introduce a new additive version for changes. Do not enable automatic baselining or clean an existing database to fix a failure. Inspect and correct the failing migration/configuration while preserving stored data. The database user must be allowed to enable the installed `vector` extension on first startup.

[K0 foundation conventions](contracts/k0-foundation.md) define UUID identity/resource/operation IDs, trusted server-established account identity, safe MVC errors, and short JDBC transactions. Feature owners add their own tables and publish consumer-reviewed contracts when a real interface is needed. No identity/session, resource ownership, operation deduplication or job processing feature is supplied by this foundation.
