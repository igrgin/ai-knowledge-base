# AI Knowledge Base repository guidance

## Delivery work

For delivery work, use the [EGM workflow](docs/egm-workflow.md), [application design](docs/designs/application.md) and [roadmap](docs/roadmap.md). The roadmap owns task authority, dependencies, file/schema ownership and progress; the [contract index](docs/contracts/README.md) owns gate availability. T3 and later features require separate authorization.

## Agent assignments

For skill routing, review handoff, sequential execution and role/model assignment, read the [workflow](docs/egm-workflow.md). Use the installed skill's own procedure. For architecture bootstrap, explicitly select `target_filename="EGM.md"` and a bounded subtree.

## Remote work

Before issue work, branch or commit operations, pushes, PR changes, comments, CI work or merges, read and follow [remote-work.md](docs/remote-work.md).

## Architecture

For architecture discovery or changes to shared errors, read the [EGM architecture map](EGM.md) and the affected package contract; K0 remains the canonical foundation contract linked there.

## Build and local development

The Java backend and extractor use the [root Maven reactor](pom.xml) and module POMs; frontend tasks are in [frontend/package.json](frontend/package.json). For local startup, Spring profiles, package/build and meaningful test commands, read [local development](docs/local-development.md).
