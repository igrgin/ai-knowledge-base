# AI Knowledge Base repository guidance

## Shared workflow

Use the shared skills in `.agents/skills/`, linked to
`~/Documents/home-repo/.agents/skills/`. For implementation, diagnosis, review,
coordination, and skill setup, read `~/Documents/home-repo/docs/agent-workflow.md`.
For GitHub work, use `~/Documents/home-repo/docs/github-workflow.md` as the
authority, including when project documents reference older remote conventions.

## Delivery work

For delivery work, use the [EGM workflow](docs/egm-workflow.md), [application design](docs/designs/application.md) and [roadmap](docs/roadmap.md). Before substantial feature implementation, satisfy the roadmap's bounded EGM design/review prerequisite. The roadmap owns task authority, dependencies, file/schema ownership and progress; the [contract index](docs/contracts/README.md) owns gate availability. T3 and later features require separate authorization.

## Agent assignments

For skill routing, review handoff, sequential execution and role/model assignment, read the [workflow](docs/egm-workflow.md). Use the installed skill's own procedure. For architecture bootstrap, explicitly select `target_filename="EGM.md"` and a bounded subtree.

## Architecture

For architecture discovery or changes to shared errors, read the [EGM architecture map](EGM.md) and the affected package contract; K0 remains the canonical foundation contract linked there.

## Build and local development

The Java backend and extractor use the [root Maven reactor](pom.xml) and module POMs; frontend tasks are in [frontend/package.json](frontend/package.json). For local startup, Spring profiles, package/build and meaningful test commands, read [local development](docs/local-development.md).
