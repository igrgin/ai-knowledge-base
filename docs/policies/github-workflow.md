# GitHub workflow

The project owner accepted the workflow in D14 and amended commit formatting in D15–D16 in [the change index](../changes/20261023-ai-knowledge-base/change.json). These rules define the intended setup; they do not claim GitHub resources or protections already exist. The [current plan](../changes/20261023-ai-knowledge-base/plan.md#revised-task-list) remains the task source.

## Repository and initial baseline

The selected repository is `igrgin/ai-knowledge-base`, public, with the MIT license and default branch `main`. Confirm the copyright holder and actual Git author/noreply identity during authorized setup before creating the license/commit. Prefer the GitHub-provided noreply identity for public commits.

D17 authorizes the initial GitHub setup. D18 confirms `igrgin` as copyright holder/Git author and `10382095+igrgin@users.noreply.github.com` as the repository-local public commit email. See the [setup baseline](../github-setup.md); implementation remains separately gated.

The authorized initial setup commit contains current project instructions, Lighthouse configuration, consolidated planning records, agreed policies, README, license, ignore rules, issue/PR templates and bootstrap CI. Use the consolidated documents as the baseline; future Git commits provide ordinary history. Do not recreate obsolete draft references or a history folder. Application code belongs to the later approved implementation slices.

The repository, all task issues, their mapping and workflow setup must be ready for the owner's implementation approval. Selection of these rules does not itself authorize creating commits/resources or beginning implementation.

## Issues and branches

Create an issue for each of the 20 current Lighthouse tasks during authorized setup. Preserve the real task-to-issue mapping in `docs/changes/20261023-ai-knowledge-base/github-issues.json`; capture actual issue numbers rather than assuming they equal task IDs.

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

Use squash merges so the completed slice produces one coherent main-branch commit. The squash message follows the same format; include its proposed message in the final PR presentation. Original development commits remain associated with the PR discussion. Issue closure remains in the completed PR's `Closes` reference rather than the commit's issue-number header.

## PRs and owner approval

After implementing the slice and self-checking, the implementation agent pushes its issue branch and opens a PR linking its issue and Lighthouse task. It may remain draft while independent review/corrections run. Use `Refs` for incomplete work; use `Closes #42` when the PR delivers the issue's completed slice. Reuse the existing PR for corrections.

Follow the [review policy](review.md). Present the final current candidate to the owner with a behavior/change summary, actual test commands/results and CI links, independent review coverage/findings, limitations, and base/head identities. Planned tests are not results.

The owner merges manually after approval. Auto-merge stays disabled. Agents do not merge or push directly to protected `main`. Implementation, technical review, integration and human approval are distinct responsibilities.

## Main protections and CI

Configure `main` to require PRs, passing required CI, resolved review conversations and an up-to-date branch. Block force pushes/deletion, apply protections to administrators, and use squash merges with linear main history.

For this solo repository using the owner's account, require **zero GitHub approving reviews**. Do not require the owner's CODEOWNER approval or another approval of the last push: agents using that account are not independent GitHub identities, and the owner cannot approve their own PR through that mechanism. The fresh technical review and human decision remain required by policy. Stronger identity-based review gates need genuinely distinct identities.

Bootstrap CI validates repository configuration, current planning references and workflow metadata. Require only checks that actually exist and have run successfully. T1 and later slices introduce real application build/test checks as their code appears; make those required as part of the agreed workflow setup progression. CI performs no live AI calls. Native checks enforce mechanical conditions, not the truth or independence of an agent review.

## Tracking and worktrees

Track instructions/configuration, policies, current planning records and dated cost/caching/scalability research. Keep credentials, private PDFs/chats, databases, backups/journals, generated private evaluation data, dependency/build output, local logs and `.DS_Store` ignored. Track only deliberately redistributable fixtures and sanitized results.

Worktree agents start from a committed baseline containing these records and read the existing task contracts/ownership. Current implementation concurrency is 1; increasing it to 2 remains a separate proposal. [Global Lighthouse installation](../development-tooling.md) supplies the skills without a project Codex enable flag or vendored package.
