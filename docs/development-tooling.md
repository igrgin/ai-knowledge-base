# Development tooling

Lighthouse is installed globally on the development host. The currently installed package is `lighthouse` version `0.1.0` from the `lighthouse-local` marketplace. Its skills are an agent-workflow dependency, not an application runtime dependency.

The project owner removed `.codex/config.toml` under decision D14 in [the change index](changes/20261023-ai-knowledge-base/change.json). That file contained only a repository-level `enabled = true` override for the globally installed Lighthouse plugin. The repository contains no local plugin package or marketplace and requires no project Codex configuration for this arrangement. Global Codex preferences and marketplace registration remain host-managed.

Agents in another worktree read the committed `AGENTS.md`, `lighthouse.json`, current records and policies. On another machine, make the compatible Lighthouse skills available through the host's normal global installation and verify they load before using the workflow. A Git clone alone does not install the plugin. The package's version 0.1 commitment targets Codex on macOS desktop; other clients need compatibility verification rather than an assumed support claim.

Use the [development-agent assignments](policies/development-agents.md) when authorized work requires an agent role. Read [GitHub workflow](policies/github-workflow.md) before branches/PRs and [review policy](policies/review.md) before candidate assessment. Model assignments are repository instructions, not changes to global Codex model settings.
