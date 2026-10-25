# GitHub workflow

The project owner accepted the workflow in D14 and amended commit formatting in D15–D16 in [the change index](https://github.com/igrgin/ai-knowledge-base/blob/7252f046b96f9a33e94f095f2b8b7790b76785ad/docs/changes/20261023-ai-knowledge-base/change.json). The repository and 20 task issues exist; the Tests requirement and repository protections apply. Original setup-only wording below describes the preserved setup baseline. The [current plan](plan.md#revised-task-list) remains the task source.

## Repository and initial baseline

The selected repository is `igrgin/ai-knowledge-base`, public, with the MIT license and default branch `main`. Confirm the copyright holder and actual Git author/noreply identity during authorized setup before creating the license/commit. Prefer the GitHub-provided noreply identity for public commits.

D17 authorizes the initial GitHub setup. D18 confirms `igrgin` as copyright holder/Git author and `10382095+igrgin@users.noreply.github.com` as the repository-local public commit email. See the [setup baseline](https://github.com/igrgin/ai-knowledge-base/blob/7252f046b96f9a33e94f095f2b8b7790b76785ad/docs/github-setup.md); implementation remains separately gated.

The authorized initial setup commit contains current project instructions, Lighthouse configuration, consolidated planning records, agreed policies, README, license, ignore rules, issue/PR templates and bootstrap CI. Use the consolidated documents as the baseline; future Git commits provide ordinary history. Do not recreate obsolete draft references or a history folder. Application code belongs to the later approved implementation slices.

The repository, all task issues, their mapping and workflow setup must be ready for the owner's implementation approval. Selection of these rules does not itself authorize creating commits/resources or beginning implementation.

## Issues and branches

The 20 task issues already exist. Their real issue links, established branch names and creation states are in current plan rows; preserve them instead of recreating issues or a mapping JSON. New issues use actual returned numbers.

Each issue includes task/requirement IDs and current section links, demonstrable behavior, start gates, separate completion prerequisites, meaningful tests, owning role/files, shared-interface/migration coordination and required reuse. Distinguish an agent's role in the issue body from an actual GitHub account assignee. Contract-based starts may precede completion of other slices; do not turn the plan into a chain of issues blocked until every predecessor closes.

Each issue uses its own branch named `<issue-number>-<issue-title-in-kebab-case>`, for example `42-upload-and-process-pdfs`. Use no namespaces or slashes. Keep the issue title stable after creating its branch and record the exact issue/branch association. Link the branch through GitHub's Development association where supported. Create the implementation branch from the appropriate agreed baseline when the slice is authorized, preserving its start gates and completion prerequisites.

## Commit messages

Use plain-language commit messages with no type prefixes. The first commit's subject is exactly:

```text
initial commit
```

For an issue-related commit, put `#<issue-number>` and the issue title together on the first line. After a blank line, briefly describe the work actually included in that commit. For example:

```text
#42 Upload and process PDFs

Add private PDF uploads and durable processing status.
```

Use the actual issue number/title and describe the particular commit, even when it completes only part of the issue. For a commit unrelated to an issue, use a brief descriptive subject such as `Update setup instructions`.

Use squash merges so the completed slice produces one coherent main-branch commit. The squash message follows the same format. Do not include a proposed squash-commit message in the PR body or template (owner decision D21). Original development commits remain associated with the PR discussion. Issue closure remains in the completed PR's `Closes` reference rather than the commit's issue-number header.

## PRs and owner approval

After implementing the slice and self-checking, the implementation agent pushes its issue branch and opens a PR linking its issue and Lighthouse task. It may remain draft while independent review/corrections run. Use `Refs` for incomplete work; use `Closes #42` when the PR delivers the issue's completed slice. Reuse the existing PR for corrections.

Follow the [review policy](project.md#review-policy). Present the final current candidate to the owner with a behavior/change summary, actual test commands/results and CI links, independent review coverage/findings, limitations, and base/head identities. Planned tests are not results.

The owner merges manually after approval. Auto-merge stays disabled. Agents do not merge or push directly to protected `main`. Implementation, technical review, integration and human approval are distinct responsibilities.

## Main protections and CI

Configure `main` to require PRs, passing required CI, resolved review conversations and an up-to-date branch. Block force pushes/deletion, apply protections to administrators, and use squash merges with linear main history.

For this solo repository using the owner's account, require **zero GitHub approving reviews**. Do not require the owner's CODEOWNER approval or another approval of the last push: agents using that account are not independent GitHub identities, and the owner cannot approve their own PR through that mechanism. The fresh technical review and human decision remain required by policy. Stronger identity-based review gates need genuinely distinct identities.

Under D21, CI sets up the required environment/dependencies, then runs all frontend and backend tests through native npm/Maven commands. It does not validate Lighthouse records, documentation or PR metadata and does not call custom verification/package orchestration. Keep meaningful checks in the application test suites and use their standard fixtures/lifecycle. Require only application test checks that exist and have run successfully; replace obsolete bootstrap contexts after the replacement passes. CI performs no live AI calls. Technical-review quality and independence remain separate policy obligations.

## Tracking and worktrees

Track repository instructions, current `.Lighthouse/` records/policies and deliberately retained research. Keep credentials, private PDFs/chats, databases, backups/journals, generated private evaluation data, dependency/build output, local logs and `.DS_Store` ignored. Track only deliberately redistributable fixtures and sanitized results.

Worktree agents start from a committed baseline containing these records and read the existing task contracts/ownership. Current implementation concurrency is 1; increasing it to 2 remains a separate proposal. [Global Lighthouse installation](project.md#tooling-and-native-commands) supplies the skills without a project Codex enable flag or vendored package.

## Issue-intent labels and interactive spikes

Use exactly one primary label: `bug` for correcting incorrect/undesirable existing behavior, `feature` for a bounded new behavior, or `spike` for interactive research/documentation/prototyping to reach a decision. Preserve other applicable labels. The existing `bug` label is reused; `feature` uses the existing enhancement color `a2eeef`, and `spike` uses `fbca04`. No supplementary labels are mandatory. Additional taxonomy changes need an actual project request.

Tasks T1–T13 and T15–T20 are feature delivery/integrated-verification tasks. T14 is the interactive AI-evaluation spike described in the plan; it retains its evidence thresholds and separate paid-call prerequisites. Labeling an issue starts no work. For spikes, the assigned developer and agent discuss evidence and agree a conclusion; there is no implementation-agent handoff, mandatory issue branch/worktree, commit, PR or formal implementation-review cycle. Temporary research/prototypes are disposable. A verified final issue comment records the question, evidence, human-agreed decision, limitations and follow-ups before completion; optional lasting documentation is deliberate. Production changes require separately authorized bug/feature scope.

## Worktree cleanup and record maintenance

Retain checkouts until merging or user-directed disposition. Within authorized cleanup, use a recoverable native archive, preserve unique/dirty work, and never force-remove a dirty checkout. Serialize shared record writes. Owner-directed amendments and records-only reconciliation consume no finding-correction round, retain unaffected approvals, and never erase outstanding findings or reset their remaining allowance. The historical T1 count of 2 is preserved.
