# AI knowledge base

A learning and portfolio project for exploring text PDFs through grounded AI conversations. The planned stack is Java 26, Spring Boot, React with shadcn/ui, and PostgreSQL with pgvector.

**Status: T1 provides the runnable local shell; independent technical review is pending.** The remaining 19 tasks are pending and unauthorized. Hosting is optional for a later phase.

Users will register, create private projects, upload up to ten PDFs per project, read their files, and reopen saved chats with answers linked to the original PDF pages. Unsupported questions should receive an explicit abstention. Project and per-chat source controls, independent project copies, and revocable read-only chat snapshots are included in the current requirements. Individual PDFs can be archived; deletion applies to chats and whole projects.

## Project records

- [Intent](docs/changes/20261023-ai-knowledge-base/intent.md)
- [Requirements and design](docs/changes/20261023-ai-knowledge-base/spec.md)
- [Current implementation plan and dependency graph](docs/changes/20261023-ai-knowledge-base/plan.md#revised-task-list)
- [Task-to-issue mapping](docs/changes/20261023-ai-knowledge-base/github-issues.json)
- [Cost research](docs/changes/20261023-ai-knowledge-base/costs.md), [scalability research](docs/changes/20261023-ai-knowledge-base/scalability-evaluation.md), and [caching research](docs/changes/20261023-ai-knowledge-base/caching-evaluation.md)

These are the consolidated current documents. Decision provenance remains in the [change index](docs/changes/20261023-ai-knowledge-base/change.json); ordinary Git commits provide future revision history.

## Development workflow

Read [AGENTS.md](AGENTS.md), the [GitHub workflow](docs/policies/github-workflow.md), [review policy](docs/policies/review.md), and [development-agent assignments](docs/policies/development-agents.md). Lighthouse is a [globally installed development dependency](docs/development-tooling.md), not a vendored application dependency.

Each implementation issue gets its own branch named `<issue-number>-<issue-title-in-kebab-case>`. Tasks have separate start gates and completion prerequisites; numeric order is not a sequential dependency chain. Current implementation concurrency is one. A fresh technical reviewer examines each slice PR; the owner approves and merges manually.

Issue-related commit messages use this format:

```text
#42 Upload and process PDFs

Add private PDF uploads and durable processing status.
```

## Repository checks

Python 3.12 or newer is sufficient for the setup-only checks:

```sh
python3 scripts/check_repository.py
```

The `Repository checks` GitHub Actions job validates JSON configuration, current local document references, task/issue mapping, workflow metadata, and issue-related PR branch association. It does not build an application or call an AI provider. `Runtime checks` additionally builds and tests the T1 application; later slices extend its checks with their feature evidence.

## Run the local shell

Use Java 26 and Docker Compose, then:

```sh
./scripts/setup-node
./scripts/check-tools
docker compose up -d --wait db
./scripts/package
./scripts/run-local
```

Open [the local app](http://127.0.0.1:8080). The shell checks the real PostgreSQL connection and reports when it is unavailable. The database persists in a named Docker volume. See [local development](docs/local-development.md) for development mode, environment settings, verification and safe shutdown.

`./scripts/verify` builds/tests both Java modules and the frontend, then verifies the packaged UI in Chromium against real PostgreSQL/pgvector, including outage/recovery and restart persistence. Install the locked Playwright Chromium first as described in the development guide. `Runtime checks` runs this suite in CI. T1 adds no domain features or AI calls.

Do not infer working later features or measured capacity from planning records. Keep credentials, personal PDFs/chats, databases, backups, journals and generated private evaluation data outside version control. Deliberately redistributable fixtures and sanitized reports may be tracked in their designated directories.

## License

[MIT](LICENSE), copyright 2026 igrgin.
