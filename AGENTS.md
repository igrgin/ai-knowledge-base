# AI Knowledge Base repository guidance

## Shared workflow

Use the shared skills in `.agents/skills/`, linked to
`~/Documents/home-repo/.agents/skills/`. For implementation, diagnosis, review,
coordination, and skill setup, read `~/Documents/home-repo/docs/agent-workflow.md`.
For GitHub work, follow `~/Documents/home-repo/docs/github-workflow.md` and
see [project GitHub requirements](docs/remote-work.md) for the repository checks.

## Delivery work

Use the [EGM workflow](docs/egm-workflow.md) for skill selection, design and
review handoffs, and agent assignments. Before substantial feature
implementation, satisfy the roadmap's design and review prerequisite.

Use the [application design](docs/designs/application.md) for requirements, the
[roadmap](docs/roadmap.md) for task authority, dependencies, file and schema
ownership, and progress, and the [contract index](docs/contracts/README.md) for
gate availability. T3 and later features require separate authorization.

## Architecture

For architecture discovery or changes to shared errors, read the [EGM architecture map](EGM.md) and the affected package contract; K0 remains the canonical foundation contract linked there.

## Build and local development

The Java backend and extractor use the [root Maven reactor](pom.xml) and module POMs; frontend tasks are in [frontend/package.json](frontend/package.json). For local startup, Spring profiles, package/build and meaningful test commands, read [local development](docs/local-development.md).
