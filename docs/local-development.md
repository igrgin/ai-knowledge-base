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

The defaults are local development credentials. `.env.example` lists supported variables. Export custom values in the invoking shell so Compose and Java receive the same settings; scripts never source or evaluate `.env`:

```sh
export DB_PORT=54329
export DB_NAME=knowledge_base
export DB_USER=knowledge_base
export DB_PASSWORD='choose-a-local-password'
```

Compose itself also reads `.env`. If you use that feature, export matching values for the Java scripts. Avoid a `.env` containing values that differ from your shell. `DB_HOST` defaults to `127.0.0.1`. `APP_PORT` changes the backend port in both modes; `FRONTEND_PORT` changes the Vite port. Development refuses occupied ports instead of connecting to another application. For example, use `APP_PORT=18081 FRONTEND_PORT=15173 ./scripts/dev` when defaults are occupied. Backend credentials are never Vite variables or frontend assets. Keep secrets and local output untracked.

## Verification

```sh
# Once per Playwright browser version; Linux may need --with-deps.
.local/tools/node-v24.22.1-darwin-arm64/bin/npm exec --prefix frontend -- playwright install chromium
./scripts/verify
```

Use your platform's installed project Node directory in the first command, or `npx --prefix frontend playwright install chromium` with project Node on PATH. CI installs the locked Chromium and Linux browser dependencies before verification.

`verify` checks tools and records, performs `npm ci`, TypeScript checking and Vite production build, runs Maven backend/extractor tests, and builds both JARs. Its smoke test then creates an isolated Compose project, checks pgvector distance and version, persists a sentinel across an ordinary database restart, and runs Chromium against the packaged same-origin shell. It stops only its own test database, checks HTTP 503 and the browser failure message, restarts the database, checks recovery, and restarts the backend. It removes only its own disposable smoke volume. Development data stays intact. Ports **54330** and **18080** must be free.

Use `./mvnw verify` for the focused Java suite without Docker. It includes a real HTTP check that an unavailable database cannot report readiness and a separate Java26 extractor child launch. `./mvnw dependency:tree` shows the Boot-managed dependencies; no parser, model, authentication or migration libraries are included yet.

The `Runtime checks` workflow runs these same package/database/browser checks on Linux. Local logs and screenshots are under `.local`; build/browser results are ignored. No synthetic or real AI provider is wired into the runtime.

## Compatibility checkpoint

The accepted core pins resolved and ran: Boot **4.1.2**, React/React DOM **19.3.1**, Vite **8.3.5**, TypeScript **7.0.2**, React plugin **6.1.2**, and Playwright **1.64.1**. PostgreSQL **17.11** with pgvector **0.8.7** ran from the plan's exact multi-platform image digest on arm64. CI verifies the same digest on x64.

The local shadcn/ui Button is project source using the [documented Radix button pattern](https://ui.shadcn.com/docs/components/radix/button), with its Slot/CVA/utility dependencies locked. No initializer runs during builds. Tailwind and its Vite plugin use **4.3.3**: an initial 4.1.18 candidate rejected Vite 8 in its peer range, while [4.3.3's registry metadata](https://registry.npmjs.org/@tailwindcss/vite/4.3.3) accepts it. No accepted plan pin was changed. `./mvnw dependency:tree` resolved Spring JDBC **7.0.10**, PostgreSQL JDBC **42.7.14**, Jackson Databind **3.1.7**, and JUnit **6.0.3** from the Boot BOM. Feature libraries are added only by their owning slices.
