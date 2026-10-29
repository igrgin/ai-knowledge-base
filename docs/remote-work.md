# Remote work

Apply these rules to working branches, commits, pushes, PRs, decision records on issues, comments and closure of resolved issues. “PR” includes pull requests and merge requests.

## Issues and authority

Every piece of work must have an issue defining its scope and intended outcome. Identify that issue before starting implementation or decision work.

Use the supplied issue or search for a matching existing issue. If none can be found, present the destination and an issue draft for the user's approval. After approval, create the issue and verify its identifier and content before proceeding with the requested work.

A request to implement an issue authorizes its scoped changes, commits, pushes to the issue's working branch and creation of a PR, including its initial title and description. Use that standing permission without asking for confirmation again. Follow any limits the user gives for the issue.

When an issue calls for decisions rather than an implementation to integrate, follow [decision issues](#decision-issues). Apply the separate [comment approval](#issue-and-pr-comments) and [merge approval](#approval-and-merge) requirements before those actions.

## Repository requirements

Apply repository requirements where this file already allows repository-specific conventions. For a genuine conflict, pause the conflicting action, explain the conflicting requirements in chat and ask the user to decide. Continue authorized work that is unaffected by the conflict.

## Decision issues

For decision issues, use code, prototypes and working branches as evidence for the decision. Their existence does not require a PR or authorize merging into the main repository. Apply the PR workflow only to implementation intended for integration.

The user finalizes decisions unless they explicitly delegate that authority to you.

Once all requested decisions are finalized, post one concise issue comment recording every decision, its brief reasoning and any necessary links to material supplied by the user. Obtain any missing required references from the user before publishing the final record.

Treat this complete decision record as the equivalent of a merge. After the comment is published and [verified](#comment-verification), close the issue without a separate permission request and verify its closed state. Completion requires both the complete decision comment and confirmed issue closure. If closing fails, retain the posted record and retry only the unfinished closure.

## Branch naming

Name new working branches `<issue-id>-<issue-title-slug>`: the issue identifier followed by a lowercase, hyphen-separated slug of its title.

Use a separate branch for the issue rather than pushing to a branch other people are using. A reopened issue always gets a new branch. When the base name has already been used, append the next incrementing numeric suffix, such as `42-fix-empty-input-handling-1`, then `42-fix-empty-input-handling-2`. Continue the sequence across earlier attempts, including branches that have been deleted.

Check available branch and historical PR records for prior names. If they cannot establish the next suffix for a previously used name, explain the missing history in chat and ask the user for the suffix before creating the branch.

Add a namespace prefix to a branch name only when explicitly instructed.

Use the number without `#` for numeric issues. Preserve tracker keys such as `TPS-163`.

- `42-fix-empty-input-handling`
- `TPS-163-handle-expired-sessions`

## Working commits

Commit and push after each coherent, verified unit of work. Run the relevant checks and inspect the staged diff before committing; include only changes belonging to that unit.

Leave final history consolidation until approval to merge.

When the repository does not explicitly require another format, use plain imperative commit subjects, such as `fix empty input handling`.

## PR creation and state

Before drafting a PR title or description, inspect existing PRs for repository conventions. Then load and use the `pr` skill by default for the body, adapting its output to those conventions.

Open the PR as a draft. Mark it ready only on the user's instruction or when required to carry out an approved merge.

Link the PR to each issue it resolves, using the correct repository or project reference. Configure automatic issue closure on merge when the service supports it.

After creating the PR, read it back and verify its repository, source and target branches, draft state, title and description, and issue links. If creation has an uncertain result, look for the PR before retrying.

Changes to a published PR title or description require the user's approval. Present the proposed edit together with a draft comment briefly explaining what changed and why. Obtain approval for both. Apply and verify the edit, then publish the approved comment under the [comment rules](#issue-and-pr-comments).

Close a PR without merging, or reopen a closed issue or PR, only when the user explicitly instructs you.

## Checks and CI

At the start of implementation intended for integration, determine whether CI is already present. If it is, modify its configuration or workflows only when the issue covers CI.

If CI is absent, create minimal CI that runs the repository's tests using its existing test setup. Completing and correcting that initial CI is part of its creation. If no test setup exists, report the missing prerequisite in chat and ask the user how to proceed before adding CI or a test setup.

When relevant local checks fail or you encounter CI failures, diagnose and fix them to prepare the PR for the user's review, including necessary repairs to failures not introduced by the issue. Keep those repairs focused on making the PR verifiable. If a fix requires an out-of-scope change to CI that was present at the start, report that blocker in chat for the user to decide.

Retry failures with evidence of a transient cause. If a rerun reproduces the same failure and the investigation identifies no supported next remediation, stop retrying and report the failing check, evidence, attempts and remaining blocker in chat. The user decides how unresolved failures are handled.

At PR creation, handle any failures already known under these rules, then report the verified PR's link and known CI status in chat. Wait for a pending CI run to finish only if the user asks.

## Issue and PR comments

Draft ordinary issue comments, PR conversation comments, inline review comments and replies to humans or bots for the user's approval before posting. Finalized decision records follow the [decision issue workflow](#decision-issues).

Write for easy human reading: include enough detail to follow the explanation, keep it concise and focused, and link evidence when required or useful.

Present the destination and exact proposed text together. Publish the approved text to that destination. Obtain approval for material changes to the draft.

Resolve review threads, including bot threads, only when the user explicitly asks.

## Comment verification

After posting an authorized comment, read it back to verify its destination and text. If the posting result is uncertain, inspect existing comments before retrying.

Leave published comments unchanged. Report needed corrections or additions in chat with a link to the comment and a brief explanation. The user handles the remote correction.

## Attribution

Add AI signatures, model or harness footers, or AI commit trailers only when explicitly required by the user or repository.

## Approval and merge

1. **Establish approval.** Accept the user's statement in the conversation that the PR is approved, or an approval submitted through their verified remote account. Only the user's approval authorizes starting a merge operation, including auto-merge or merge-queue entry.
2. **Check approval still applies.** Compare any changes since approval with the approved implementation. Existing approval remains valid when both the original scope and implementation behavior are unchanged. Obtain renewed approval before merging if either changes.
3. **Check readiness.** Read the current PR state and required checks. Proceed through the repository's normal protected merge path when its required conditions pass. Report an unresolved blocker without asking again for permission the user already granted.
4. **Squash and merge.** Perform the merge without another permission request, preferably using the hosting service's squash-merge operation. Use a brief final commit message that describes the issue solved and includes its issue identifier.
5. **Confirm the merge.** Verify that the remote PR is merged, then complete [issue closure](#issue-closure-after-merge).

## Issue closure after merge

After a confirmed merge, inspect each issue the PR resolves. If it closed automatically, leave it closed. Otherwise, close it without a separate permission request and verify its closed state in the tracker.

Keep related or partially addressed issues open. A PR closed without merging does not trigger issue closure.

Report the merge and issue-closure outcomes together. This PR workflow is complete after a confirmed merge and confirmed closure of its resolved issues. If closure fails or cannot be verified, report the completed work and remaining action.

