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
| Tests | JUnit, Testcontainers, Playwright |

## Run locally

You need JDK 26, Python 3.12+, curl, tar and a running Docker engine with Compose. Set `JAVA_HOME` to your JDK if it is configured. Maven is provided by the wrapper; Node 24.22.1 installs inside this project without changing your global Node.

```sh
./scripts/setup-node
./scripts/check-tools
docker compose up -d --wait db
./scripts/package
./scripts/run-local
```

Open [localhost:8080](http://127.0.0.1:8080). The local launcher explicitly selects Spring's `dev` profile. Stop the app with Ctrl-C; use `docker compose stop db` to stop its database. Ordinary stops preserve the database volume.

For frontend development with live reload:

```sh
./scripts/dev
```

Open [localhost:5173](http://127.0.0.1:5173). If those ports are occupied, use `APP_PORT=18081 FRONTEND_PORT=15173 ./scripts/dev`. See [local development](docs/local-development.md) for configuration, profiles and database commands.

## Test and build

```sh
./mvnw test                         # Basic tests; no Docker or full application startup
./mvnw -Pintegration-tests verify   # Full Spring startup and a real PostgreSQL Testcontainer
./scripts/package                  # Frontend build and both Java packages
```

For browser checks, install Chromium once with project Node on PATH, then run the complete suite:

```sh
PATH="$PWD/.local/tools/node-v24.22.1-darwin-arm64/bin:$PATH" npx --prefix frontend playwright install chromium
./scripts/verify
```

Use your platform's project Node directory in that command; the development guide lists supported platforms. Linux may need Playwright's `--with-deps` option.

The full suite checks the packaged shell against real PostgreSQL, database outage and recovery, retained data across restart, and the extractor's Java launch contract. Tests do not call AI providers.

## Scope

The app runs on your own machine, with local services bound to loopback. Keep database credentials, private documents and generated data outside version control. The extractor package exposes a version probe; it does not yet parse PDFs.

<!-- Add supported document limits and grounded-answer behavior alongside those features. -->

[MIT](LICENSE)
