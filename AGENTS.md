# AI Knowledge Base repository guidance

## Delivery work

For delivery work, use the installed Lighthouse skills with this repository's [intent](.Lighthouse/scopes/initial-application/intent.md), [specification](.Lighthouse/scopes/initial-application/spec.md) and [plan](.Lighthouse/scopes/initial-application/plan.md). The plan owns task authority, file ownership, contract gates and progress.

## Agent assignments

For agent role/model assignment, read [project settings](.Lighthouse/settings/project.md#development-agent-assignments). Review and delivery lifecycle procedure come from Lighthouse.

## Remote work

Before issue work, branch or commit operations, pushes, PR changes, comments, CI work or merges, read and follow [remote-work.md](docs/remote-work.md).

## Architecture

For architecture discovery or changes to shared errors, read the [EGM architecture map](EGM.md) and the affected package contract; K0 remains the canonical foundation contract linked there.

## Build and local development

The Java backend and extractor use the [root Maven reactor](pom.xml) and module POMs; frontend tasks are in [frontend/package.json](frontend/package.json). For local startup, Spring profiles, package/build and meaningful test commands, read [local development](docs/local-development.md).
