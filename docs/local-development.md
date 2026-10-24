# Local development

T1 supplies a Java/React runtime and a real PostgreSQL readiness check. PDF extraction, accounts, projects and AI are later slices. Ordinary checks make no AI calls.

## Prerequisites

Use Java **26** (initially tested with Temurin **26.0.1**) and set `JAVA_HOME` to that JDK when present. The scripts use its `bin/java` for both the backend and extractor. Maven Wrapper **3.9.16** is checked in with the distribution SHA-256; a global Maven installation is unnecessary. Python 3.12+, curl, tar and a running Docker engine with Compose are required.

Install the project Node runtime without changing global Node:

```sh
./scripts/setup-node
./scripts/check-tools
```

`setup-node` installs **24.22.1** under the ignored `.local/tools` directory after checking the official release's SHA-256. It supports macOS/Linux on arm64/x64. Scripts also accept Node 24.22.1 already on PATH, as in CI. The release bundles npm **11.19.1**. Frontend dependencies use exact direct versions and a committed npm lockfile. `npm ci` checks the lock before every package build.

## Start and stop

```sh
docker compose up -d --wait db
./scripts/dev
```

Open [the development UI](http://127.0.0.1:5173). Vite forwards `/actuator` to the backend on `127.0.0.1:${APP_PORT:-8080}`; packaged requests use that same relative URL. Stop development with Ctrl-C. Backend Java edits require restarting the command; frontend edits reload through Vite.

For the single-origin package:

```sh
./scripts/package
./scripts/run-local
```

Open [the packaged UI](http://127.0.0.1:8080). `.local/package/backend.jar` contains the built frontend; `pdf-extractor.jar` is alongside it. Both launch with the same Java 26 executable. The standalone extractor currently supports only the following packaging probe, exits 2 for extraction arguments, and does not parse PDFs:

```sh
java -Xmx256m -jar .local/package/pdf-extractor.jar --version
```

Expected probe: `{"contractVersion":1,"javaFeature":26,"extractionAvailable":false}`. T6 will add the bounded extraction protocol.

`docker compose stop db` stops only this project's database. `docker compose up -d --wait db` starts it again. The named `ai-knowledge-base_database` volume retains data through restarts and `docker compose down`. Do not remove that volume to fix startup. Database initialization enables pgvector only when a fresh volume is first initialized; there are no application tables or Flyway migrations in T1. Changing database user/password/name variables does not rewrite an initialized volume's credentials.

## Configuration

All services bind to loopback. Default PostgreSQL port is **54329**, backend port **8080**, and frontend development port **5173**. Backend readiness is [the readiness endpoint](http://127.0.0.1:8080/actuator/health/readiness): HTTP 200/`UP` requires a real database connection; HTTP 503/`DOWN` means unavailable. The UI checks on load and on **Check again**. It can still load while the database is down.

### Spring profiles

There is no automatic `dev` profile. `application.yml` contains common static settings: application name, loopback/8080 server defaults, pool limits and readiness exposure. It contains no database URL or credentials.

| Spring profile | Configuration | Selection and purpose |
| --- | --- | --- |
| `dev` | Main resources `application-dev.yml` | Local PostgreSQL URL/credentials; selected explicitly by `scripts/dev` and `scripts/run-local` |
| `prod` | Main resources `application-prod.yml` | Requires `SERVER_ADDRESS`, `SERVER_PORT`, `DATABASE_URL`, `DATABASE_USER`, `DATABASE_PASSWORD`; no fallback values |
| `test` | Test resources `application-test.yml` | Basic configuration/binding checks, without a web application or Docker |
| `lvl2test` | Test resources `application-lvl2test.yml` | Full Spring startup with an ephemeral port and small test pool; real Testcontainers/readiness and packaged browser scenarios |

`prod` overrides the base bind address/port with required deployment values. Its validator rejects missing or blank deployment variables before serving. It cannot inherit the development datasource. A directly launched application without a selected profile has no datasource configuration and cannot silently choose local credentials. No production launcher supplies defaults, and these files do not constitute a hosted deployment.

Local defaults live in `dev`; ordinary Spring environment overrides remain available. For a custom local database, export matching Compose and Java settings (scripts never source/evaluate `.env`):

```sh
export DB_PORT=54331 DB_NAME=custom_local DB_USER=custom_user
export DB_PASSWORD='choose-a-local-password'
export SPRING_DATASOURCE_URL='jdbc:postgresql://127.0.0.1:54331/custom_local?connectTimeout=2&socketTimeout=3'
export SPRING_DATASOURCE_USERNAME="$DB_USER"
export SPRING_DATASOURCE_PASSWORD="$DB_PASSWORD"
```

Compose reads the `DB_*` values; Spring reads `SPRING_DATASOURCE_*`. `.env.example` lists both explicitly. Compose itself also reads `.env`, so export matching Java values if using that feature. Changing values does not rewrite credentials in an existing volume. `APP_PORT` remains the local launcher alias for Spring's `SERVER_PORT`; `FRONTEND_PORT` sets the Vite port. For occupied defaults, use `APP_PORT=18081 FRONTEND_PORT=15173 ./scripts/dev`. These local commands always select `dev`; direct Java launches may select `prod` with complete environment values. Backend credentials are never Vite variables or frontend assets.

## Verification

The Maven profile `integration-tests` is a build switch, distinct from Spring's `lvl2test` runtime profile:

```sh
./mvnw test                         # Surefire *Test; Spring profile test; no full startup/Docker
./mvnw -Pintegration-tests verify   # Also Failsafe *IT; Spring profile lvl2test; requires Docker
./scripts/package                  # Basic tests and package build, no Docker required
```

Basic tests load/bind configuration and use a small configuration-only context to verify prod's missing/complete environment handling. They do not launch `KnowledgeBaseApplication`, a web server or Testcontainers. The extractor basic test retains its separate Java26 child process check. The narrow readiness integration test launches actual Spring against the exact pinned pgvector Testcontainer, verifies HTTP200 and vector SQL, then stops its owned container and requires HTTP503. There are no application tables/migrations or shared fixture framework.

Install the locked Chromium once with project Node on PATH (`npx --prefix frontend playwright install chromium`; Linux may require `--with-deps`), then:

```sh
./scripts/verify
```

`verify` checks tools/records, builds the frontend and JARs, runs the basic and explicit integration suites, then creates an isolated Compose project for the packaged browser smoke. The packaged JVM explicitly selects `lvl2test` and loads `backend/src/test/resources/` as an additional configuration directory. These test YAML files are never inside the distributable JAR. The browser talks to that JVM's real readiness endpoint; no stub is used.

The smoke checks pgvector/version, a sentinel preserved across database restart, the browser success/failure messages, HTTP503 after database stop, recovery and backend restart. It removes only its disposable smoke volume. Development data stays intact. Ports **54330** and **18080** must be free.

The `Runtime checks` workflow demonstrates basic tests with an unreachable Docker socket, then runs the complete profile-aware package/database/browser checks on Linux. Local logs/screenshots are under `.local`; test output is ignored. `./mvnw dependency:tree` shows the managed versions. No parser, model, auth or migration libraries and no AI calls are involved.

## Compatibility checkpoint

The accepted core pins resolved and ran: Boot **4.1.2**, React/React DOM **19.3.1**, Vite **8.3.5**, TypeScript **7.0.2**, React plugin **6.1.2**, and Playwright **1.64.1**. PostgreSQL **17.11** with pgvector **0.8.7** ran from the plan's exact multi-platform image digest on arm64. CI verifies the same digest on x64.

The local shadcn/ui Button is project source using the [documented Radix button pattern](https://ui.shadcn.com/docs/components/radix/button), with its Slot/CVA/utility dependencies locked. No initializer runs during builds. Tailwind and its Vite plugin use **4.3.3**: an initial 4.1.18 candidate rejected Vite 8 in its peer range, while [4.3.3's registry metadata](https://registry.npmjs.org/@tailwindcss/vite/4.3.3) accepts it. No accepted plan pin was changed. `./mvnw dependency:tree` resolved Spring JDBC **7.0.10**, PostgreSQL JDBC **42.7.14**, Jackson Databind **3.1.7**, and JUnit **6.0.3** from the Boot BOM. Testcontainers PostgreSQL **2.0.5** is now supplied by the Boot BOM for the narrow readiness integration test. Failsafe uses Boot-managed **3.5.6**. Feature libraries are added only by their owning slices.
