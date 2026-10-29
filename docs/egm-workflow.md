# EGM development workflow

## Start from the governing documents

Read the [application design](designs/application.md) for requirements and accepted/proposed distinctions, the [roadmap](roadmap.md) for task authority, dependencies and ownership, and the [contract index](contracts/README.md) for available gates. [EGM.md](../EGM.md) and package maps describe implemented behavior. A design, ready gate or clean review does not authorize another task.

The selected package is **EGM with small fixes**, experimental local derivative **0.2.0**, based on the [Elephant-Goldfish Model](https://github.com/google/elephant-goldfish-model). It includes `egm-fix`; an upstream-only installation does not establish that capability. Resolve the installed package and skill paths through the host's skill catalog, then read the selected `SKILL.md` and its referenced integration guide/personas/standards. Keep those procedures in the package rather than copying them here or relying on a machine's cache path.

## Choose the skill for the work

| Need | Route and inputs |
| --- | --- |
| Unclear outcome or consequential unresolved direction | `egm-brainstorm`, with existing decisions and relevant contracts |
| Agreed outcome needing a concrete implementable design | `egm-draft`, reusing the application design and real provider boundaries |
| Design comprehension, critique and implementation readiness | `egm-review`, with explicit target, source reading list and selected mode |
| Bounded document audit | `egm-review-loop`, with explicit scope, reviewers and standards |
| Resolve document-review findings | `egm-resolve`, with target/range/findings; preserve owner decisions and unresolved items |
| Established localized software defect | `egm-fix`; consequential behavior changes return to design |
| Missing documentation of an implemented boundary | `egm-bootstrap`, bounded `path`, **`target_filename="EGM.md"`**, and task artifact directory; preserve product READMEs |
| Suspected design/code mismatch | `egm-drift-report`; verify each mismatch and decide whether code or documentation should change |

Name the task artifact directory for every invocation. Feature briefs/designs belong under `docs/designs/<task-slug>/`; selected review/drift evidence belongs under `docs/reviews/<task-slug>/`. Keep one-off checks and bulky raw evidence in a task-owned OS temporary directory. Retain evidence deliberately when it supports a decision or recovery. Register real design documents in [SITEMAP](designs/SITEMAP.md); avoid speculative per-task designs and shared scratch filenames.

## Implement an accepted scope

EGM leaves implementation tooling to the project. Identify the authorized issue/scope and baseline, confirm start gates and integrated completion prerequisites, implement the design's owned files, and use native verification from [local development](local-development.md). Keep controlled providers and fixtures out of application-mode success paths. Feature migrations are introduced with their actual data, not speculatively in the foundation.

Update changed package maps and canonical contracts with the implementation. Proposed product changes belong in the application/feature design until accepted; reconcile accepted amendments at their owning source before consumers rely on them. The roadmap owns current task state, and the contract index owns gate versions/availability.

Obtain a fresh, report-only implementation review of the actual diff for bugs, security and design compliance. Give it the candidate identity, actual checks and governing requirements; wait for completion and preserve findings, dispositions, failed checks and limits. A design review does not review implementation. A targeted specialist is useful for a concrete additional concern. Use the installed EGM procedure for document reviews; do not substitute an invented stage/iteration policy.

For issue work, branches, commits, pushes, PRs, comments, CI operations and merge/closure, read [remote work](remote-work.md). It is the single authority for those operations. The existing `Tests` check and native protections remain in force. No paid call or deployment follows from review or an application budget target.

## Concurrency and shared ownership

The current limit is **one active writer/task**. Run required fresh scouts/reviewers sequentially while the parent waits; this project rule overrides parallel reviewer defaults in skills. A fresh reviewer uses a non-inherited conversation context and reads its actual persona, standards, target and supplied sources. A failed launch, missing required read or incomplete result is not a clean review.

The integrator alone edits shared contracts, the roadmap, build/dependency locks, common configuration, shell/routes and API types. Providers and affected consumers agree on cross-slice changes before use. The [ownership matrix and migration rules](roadmap.md#ownership-and-coordination) govern future file/table assignments and immutable integrated Flyway migrations. Dependency opportunities do not authorize concurrency increases or dispatch.

## Agent assignments

These are the owner's selected development-agent assignments from the [planning conversation](codex://threads/01a1699d-af41-71f0-a5e7-c75152955b39), accepted on 2026-10-24 for assignments only. The application's RAG models are a separate design concern.

| Role | Assignment | Workload adjustment |
| --- | --- | --- |
| Orchestrator/integrator | GPT-6.1 Sol, high | Medium for routine status and coordination |
| Implementation | GPT-6.1 Sol, medium | High for authorization, private files, financial admission, leases, migrations, purge/recovery and sharing boundaries |
| Independent technical reviewer | GPT-6.1 Sol, high, in a fresh context | Higher effort for difficult security or concurrency analysis |
| Difficult escalation | GPT-6 Astra, high | Xhigh for a bounded unresolved problem |
| Narrow mechanical work | GPT-6 Luna, low or medium | Use Sol when interpretation or judgment is required |

Model IDs are `gpt-6.1-sol`, `gpt-6-astra` and `gpt-6-luna`. These assignments grant no native account, implementation, merge or spending authority.
