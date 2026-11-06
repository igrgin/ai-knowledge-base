# T3 Goldfish scope/progress review inputs

Prepared 2026-11-06, Europe/Zagreb. Mode: **BRAINSTORM**, EGM with small fixes **0.2.1**. This is a scope/progress checkpoint, not formal design or implementation acceptance.

## Owner review request

> Both options sound good. The owner can choose a specific recipient or share a link that any signed-in user with import permission can use. Instead of continuing on, I want you to do a goldfish pass, see if we are still on the right track. Are we actually progressing with implementing task 3, or did we go too far?

## Repository evidence before this checkpoint

- Repository: `igrgin/ai-knowledge-base`; issue branch: `3-register-log-in-and-retain-authenticated-sessions`.
- Incoming branch HEAD: `ded655e387aeb6e5a44c15fd58260303721a703f`.
- Freshly fetched `origin/main` and merge base: `e85f9dea6a58b4b10df8e5c67a75b10bc0573729`.
- `git log origin/main..HEAD` at the incoming HEAD contains 36 commits; `git diff --name-only origin/main...HEAD` lists 40 files, all Markdown. No tracked Java, React/TypeScript, migration, YAML, dependency/build or test implementation changed on this branch at that point.
- Read-only evidence commands: `git fetch origin main`, `git rev-parse origin/main`, `git merge-base origin/main HEAD`, `git diff --name-only origin/main...HEAD`, `git log --oneline --reverse origin/main..HEAD`.
- Runtime implementation evidence is in [EGM.md](../../../../EGM.md), [K0](../../../contracts/k0-foundation.md), source and module/configuration files. Reviewers may verify those directly.
- The checkpoint commit also records the latest accepted sharing modes. Its exact SHA is supplied at dispatch and must be recorded by the review. No code implementation is part of that amendment.
- Unrelated untracked `docs/research/langchain-architecture-assessment.md` is excluded: do not read, modify or stage it.

## Governing reading list

- [Concepts brief](../../../designs/t3-identity/concepts_brief.md): review target, not an accepted feature design.
- [Owner intake](../../../designs/t3-identity/authentication-decisions.md): accepted choices versus proposals.
- [Roadmap](../../../roadmap.md): authority, dependencies, ownership and progress.
- [Application design](../../../designs/application.md): selected requirements and future-feature scopes; focus R1/R2/R16/R18/R23, D-A, feature design checkpoints and T3/T12/T16/T17/T19 paragraphs.
- [Contract index](../../../contracts/README.md), [K0](../../../contracts/k0-foundation.md) and [architecture map](../../../../EGM.md): published versus planned behavior.
- [Proposed local issue #3 body](../issue-drafts/3.md): unpublished amendment, separate from the remote snapshot below.
- [Workflow](../../../egm-workflow.md): one active writer/task; fresh reviewers sequential while parent waits; Sol/high independent reviewer; later features require separate authority.

Original round-1 reports remain historical evidence and must not be rewritten. Do not retrieve parent chats or memory. This checkpoint gives the owner's review question and committed documents, not the parent agent's conclusions.

## Remote issue #3 snapshot

Read back through the GitHub connector on 2026-11-06. [Issue #3](https://github.com/igrgin/ai-knowledge-base/issues/3): **Register log in and retain authenticated sessions**. State: **open**. Assignee: **igrgin**. Primary label: **feature**. Comments: **0**. No remote edit/comment/closure was performed. The dated body below includes old provider/cutover wording; use the current accepted amendments when judging product behavior and this snapshot when comparing original task scope.

```markdown
> Documentation cutover candidate: [#24](https://github.com/igrgin/ai-knowledge-base/issues/24). The linked EGM documents are published on the migration branch and await owner review/merge. This navigation update does not authorize this task or start implementation.

## EGM source

**Task: T3. Requirements: R1, R2, R16, R18, R23.** State: pending.

[Current plan](https://github.com/igrgin/ai-knowledge-base/blob/e095a9161f3382aa9ee1a83db6198f3f93f94f76/docs/roadmap.md#tasks) · [Requirements](https://github.com/igrgin/ai-knowledge-base/blob/e095a9161f3382aa9ee1a83db6198f3f93f94f76/docs/designs/application.md#requirements) · [Dependency graph](https://github.com/igrgin/ai-knowledge-base/blob/e095a9161f3382aa9ee1a83db6198f3f93f94f76/docs/roadmap.md#completion-dependency-graph) · [Task issue links](https://github.com/igrgin/ai-knowledge-base/blob/e095a9161f3382aa9ee1a83db6198f3f93f94f76/docs/roadmap.md#tasks)

## Demonstrable behavior

Register, log in/out and retain an authenticated session after restart.

## Start gates

Before implementation, satisfy the reviewed bounded [EGM feature-design prerequisite](https://github.com/igrgin/ai-knowledge-base/blob/e095a9161f3382aa9ee1a83db6198f3f93f94f76/docs/roadmap.md#feature-design-prerequisite), including exact file actions. This task remains pending and unauthorized. The revised [decision register](https://github.com/igrgin/ai-knowledge-base/blob/e095a9161f3382aa9ee1a83db6198f3f93f94f76/docs/designs/application.md#design-decisions) distinguishes inherited proposals from accepted constraints.

T2.

A task ID in this field means its completion. A K gate is a narrower recorded agreement after its producer has begun and published the named interface/schema assumptions; a draft alone is insufficient. Start gates allow bounded development before all completion prerequisites exist.

No additional K gate beyond the start conditions above.

## Completion prerequisites

T2.

These implementations must be integrated and the behavior demonstrated before this task is complete. Test adapters can support development but cannot substitute for production wiring. Numeric task/issue order is not a prerequisite.

## Implementation and required reuse

Design and own the concrete authenticated principal (name and representation resolved here) alongside real authentication/session integration, identity/session migrations, Spring Security hashing/login rotation/logout/CSRF, JDBC sessions, `/me` and registration/login/expired-session screens. Demonstrate two accounts, persistent sessions and rejected unauthorized/CSRF writes through the UI/API and actual database. Local cookies are explicitly loopback-compatible and secure for non-local HTTPS. Include cross-instance session tests when T13's harness exists; current shared-session design must not rely on sticky sessions. Email verification/recovery stays deferred.

## Meaningful tests and evidence

- Register two accounts; prove login/logout, session rotation, session persistence after restart, and expired-session UI.
- Reject unauthorized/CSRF writes and cross-account access; extend session continuity/invalidation checks to the T13 multi-instance harness.

Record actual commands/results, fixture conditions and limitations in the PR. Ordinary CI/offline verification must not call paid AI providers.

## Ownership and coordination

- Accountable owner / GitHub assignee: **@igrgin**. Future implementing role: **Identity slice owner**; no agent is dispatched by this issue.
- Exclusive paths: `identity/**`, `features/auth/**`. Backend package paths are relative to `backend/src/main/java/com/aiknowledgebase/`; frontend paths are relative to `frontend/src/`. Matching feature tests/fixtures belong to the slice.
- Schema ownership: Users/JDBC sessions.
- Shared touchpoints: K0 actor/principal and route/auth integration.
- The integrator is sole writer of shared build/dependency locks, common configuration, shell/routes/API types and plan and contract records. Ask the owning writer for changes outside this slice's paths.
- Introduce feature migrations with this slice, not in T2. Use isolated disposable databases; the integrator assigns final unused Flyway versions at integration and reruns clean/upgrade checks. Applied migrations remain immutable.

[Full ownership, contracts and migration rules](https://github.com/igrgin/ai-knowledge-base/blob/e095a9161f3382aa9ee1a83db6198f3f93f94f76/docs/roadmap.md#ownership-and-coordination)

## Scheduling and delivery

Potential overlap with T5 after T2/K0; different identity versus usage/model paths.

Current implementation concurrency remains **1**. Any concurrency increase requires separate owner authorization. No branch or checkout is created until this slice is authorized. Planned branch: `<actual-issue-number>-register-log-in-and-retain-authenticated-sessions`; the current plan row records the actual association.

**Repository setup is authorized; implementation is not yet authorized.** Deliver authorized work through its issue branch and draft PR with actual checks and a fresh implementation review for bugs, security and design compliance. Follow the [EGM workflow](https://github.com/igrgin/ai-knowledge-base/blob/e095a9161f3382aa9ee1a83db6198f3f93f94f76/docs/egm-workflow.md), [remote-work policy](https://github.com/igrgin/ai-knowledge-base/blob/e095a9161f3382aa9ee1a83db6198f3f93f94f76/docs/remote-work.md) and [agent assignments](https://github.com/igrgin/ai-knowledge-base/blob/e095a9161f3382aa9ee1a83db6198f3f93f94f76/docs/egm-workflow.md#agent-assignments). Review completion does not authorize merge.

```
