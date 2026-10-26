# AI knowledge base

A learning and portfolio project for exploring text PDFs through grounded AI conversations. The project brings together Java, React and PostgreSQL to make answers traceable to their sources.

<!-- Add a screenshot of the actual PDF workspace when that interface exists. -->

## What you can do

- Open a local workspace in your browser.
- Check whether its PostgreSQL database is connected, and retry after a connection failure.
- Stop and restart the local database while preserving its stored data.
- Run the same browser shell from a single packaged Java application.

<!-- Extend this section with demonstrated PDF, project, chat and source-navigation features. -->

## How it works

The React shell asks the Spring Boot backend for readiness. The backend checks a real PostgreSQL connection and reports whether the workspace runtime is available. In the package, the backend serves both the frontend and its readiness endpoint from the same origin.

| Layer | Stack |
| --- | --- |
| Browser | React 19, TypeScript, Vite 8, shadcn/ui |
| Backend | Java 26, Spring Boot 4.1, Maven Wrapper |
| Database | PostgreSQL 17, pgvector, Docker Compose |
| Tests | JUnit, Playwright |

## Run locally

You need JDK 26, a running Docker engine with Compose, and Node 24.22.1. Maven is provided by the wrapper. To install Node inside this project without changing your global runtime:

```sh
./scripts/setup-node
export PATH="$PWD/.local/tools/node-v24.22.1-darwin-arm64/bin:$PATH"
```

Use your platform's directory (`darwin-arm64`, `darwin-x64`, `linux-arm64` or `linux-x64`). The optional installer also needs Python 3, curl and tar.

Start the database, then run the backend and frontend in separate terminals:

```sh
docker compose up -d --wait db
./mvnw -pl backend spring-boot:run -Dspring-boot.run.profiles=dev
```

```sh
npm ci --prefix frontend
npm run dev --prefix frontend
```

Open [localhost:5173](http://127.0.0.1:5173). Stop each app with Ctrl-C; `docker compose stop db` stops its database while preserving the named volume.

To build and run the single-origin package:

```sh
npm run build --prefix frontend
./mvnw package
java -jar backend/target/backend-0.1.0-SNAPSHOT.jar --spring.profiles.active=dev
```

Open [localhost:8080](http://127.0.0.1:8080). For occupied ports or custom database settings, see [local development](docs/local-development.md).

## Test and build

```sh
npx --prefix frontend playwright install chromium  # Once per browser version
npm test --prefix frontend                        # Builds UI; browser tests start their own preview
./mvnw -Pintegration-tests verify                 # Basic + extractor package integration tests
```

Run the frontend command first: its production build is included in the backend package. Linux may need Playwright's `--with-deps` option when installing Chromium. `./mvnw test` runs only the basic Java tests, without Docker or a full application startup.

Frontend browser tests isolate readiness success, failure and retry with test-only network mocks. Java tests cover the extractor child process and its actual built package. Backend readiness and database lifecycle currently have no automated integration tests. Tests do not call AI providers.

## Scope

The app runs on your own machine, with local services bound to loopback. Keep database credentials, private documents and generated data outside version control. The extractor package exposes a version probe; it does not yet parse PDFs.

<!-- Add supported document limits and grounded-answer behavior alongside those features. -->

[MIT](LICENSE)
