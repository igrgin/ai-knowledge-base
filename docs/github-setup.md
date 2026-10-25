# GitHub setup baseline

Owner decision D17 in the [change index](changes/20261023-ai-knowledge-base/change.json) authorizes repository setup only. The repository is [igrgin/ai-knowledge-base](https://github.com/igrgin/ai-knowledge-base), public, with default branch `main` and the MIT license. D18 selects `igrgin` and `10382095+igrgin@users.noreply.github.com` for repository-local Git identity and `igrgin` as copyright holder. Global Git identity remains unchanged.

## Task tracking and authorization

The [actual issue mapping](changes/20261023-ai-knowledge-base/github-issues.json) records all 20 task IDs, issue numbers, URLs and planned branch names. Issues reproduce the current plan's behavior, start/completion distinction, meaningful tests, exclusive ownership and required reuse. GitHub assignment to `igrgin` means human accountability; it does not appoint or dispatch an implementation agent.

**At the setup baseline, implementation was unauthorized and all tasks were pending.** Setup created no feature branch, worktree or agent. Subsequent D19 authorizes T1 only; the current plan/index record its execution. Create each mapped branch when its slice is separately authorized; the branch is based on the appropriate agreed integration baseline. Preserve the [dependency graph and parallel opportunities](changes/20261023-ai-knowledge-base/plan.md#potential-parallel-execution-and-ownership). Task numbering is not execution order. `maxParallelTasks` stays 1; two remains a separate proposal.

## Workflow configuration

The setup applies the following settings after the initial baseline is pushed and the named check has succeeded. Verify actual GitHub state when relying on enforcement; this document specifies the baseline rather than replacing platform evidence.

| Setting | Selected value |
| --- | --- |
| Merge methods | Squash only; merge commits, rebase merging and auto-merge disabled |
| Main pull requests | Required, including administrators |
| Native approving-review count | 0; no CODEOWNER or last-push approval requirement |
| Required check | `Tests`, pinned to GitHub Actions after its successful D21 replacement run |
| Update requirement | Branch must be up to date with `main` |
| Review conversations | Must be resolved |
| Main history | Linear; force pushes and branch deletion blocked |
| Human merge | Owner manually approves and merges under the review policy |

The one-time authorized initial commit seeds the empty `main` branch before protection can be applied. Its subject is exactly `initial commit`. Further work follows the issue/PR workflow; setup does not create an ongoing direct-push exception.

Under D21, the [application test workflow](../.github/workflows/tests.yml) sets up Java, Node and browser dependencies, then directly runs `npm test --prefix frontend` and `./mvnw -B -Pintegration-tests verify`. It has a read-only token and pinned official actions. There is no repository/document/PR-metadata check or custom verification/package runner in CI. The former `Repository checks` and `Runtime checks` contexts were replaced only after `Tests` actually passed; strict update/PR/admin/conversation and merge controls were retained. Native application tests extend as later features are authorized. No CI step calls AI providers.

## Review and platform limits

Follow the [review policy](policies/review.md): implementation self-checks, one fresh technical reviewer covering bugs/security/requirements, integrator coordination, and final human approval are distinct responsibilities. Two correction rounds follow the initial review; changes refresh affected evidence against current commits. No reviewer is launched during setup.

GitHub enforces the configured checks, conversation resolution and branch rules. With one shared account and zero native approvals it cannot establish fresh agent context or distinguish an agent's click from the human owner's decision. Technical review records and manual owner merging remain explicit policy obligations. Do not submit a self-approval or treat passing tests as an independent application review.

## Tracked baseline and worktree access

Track `AGENTS.md`, `lighthouse.json`, the current consolidated change and research records, agreed policies, documentation, MIT license, issue/PR templates and native application tests. Keep local/generated/private data under the ignored paths in [.gitignore](../.gitignore). Redistributable fixtures and sanitized research/results are deliberate tracked inputs, not blanket permission to commit PDF uploads or transcripts.

There is no repository Codex configuration or vendored Lighthouse package. The globally installed Lighthouse 0.1.0 dependency is described in [development tooling](development-tooling.md). Worktree agents receive committed project instructions and records. Another host must separately install compatible skills through its normal global mechanism.

Hosted environments, production controls, application credentials and paid AI evaluation are outside this setup. The current product requirements and task definitions are unchanged. Ordinary future Git commits provide revision history; no history folder or obsolete Git-blob reference is introduced.


## Local setup verification

Before the initial commit, the offline check passed for all 20 task mappings and current local document/JSON references. Temporary-copy checks accepted a correctly associated task PR and rejected malformed JSON, a nonexistent section link, duplicate/mismatched issue numbers and an incorrect branch. The 20 remote issue bodies and accountable-owner assignments were compared with the current plan-derived drafts. Credential-pattern scanning found no matching credential material in the staged baseline. These are setup checks, not application tests or an independent technical review. GitHub run and protection state are verified after the initial push through their native APIs.

D21 retires the initial setup checker and its workflow; the setup verification above is historical evidence, not a current command or required context.
