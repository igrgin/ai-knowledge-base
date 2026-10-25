# Project workflow

## Current scope and sources

Original human decisions and their covered scopes are in the [decision source](https://github.com/igrgin/ai-knowledge-base/blob/7252f046b96f9a33e94f095f2b8b7790b76785ad/docs/changes/20261023-ai-knowledge-base/change.json). Use the current [plan](plan.md#revised-task-list) for task authority and progress.

## Development-agent assignments

The project owner accepted these assignments in decision D14 in [the change index](https://github.com/igrgin/ai-knowledge-base/blob/7252f046b96f9a33e94f095f2b8b7790b76785ad/docs/changes/20261023-ai-knowledge-base/change.json). This policy covers development-agent models and reasoning effort. The application's RAG model remains defined in the [specification](spec.md#d-h-measured-ai-choice-and-bounded-use).

| Role | Assignment | Workload adjustment |
| --- | --- | --- |
| Orchestrator/integrator | GPT-6.1 Sol, high | Medium for routine status and coordination |
| Implementation | GPT-6.1 Sol, medium | High for authorization, private files, financial admission, leases, migrations, purge/recovery and sharing boundaries |
| Independent technical reviewer | GPT-6.1 Sol, high, in a fresh context | Higher effort for difficult security or concurrency analysis |
| Difficult escalation | GPT-6 Astra, high | Xhigh for a bounded unresolved problem |
| Narrow mechanical work | GPT-6 Luna, low or medium | Use Sol when interpretation or judgment is required |

Use model IDs `gpt-6.1-sol`, `gpt-6-astra` and `gpt-6-luna`. These assignments do not change global Codex settings or the active chat's model. Apply them to the appropriate roles when work is authorized. Current task concurrency remains 1; independent review runs after implementation work yields its slot. The proposed increase to 2 remains unadopted.

These are role assignments only. Agent billing and development usage budgets are outside repository scope. Application spending controls remain unchanged.

## Review policy

The project owner accepted this policy in decision D14 in [the change index](https://github.com/igrgin/ai-knowledge-base/blob/7252f046b96f9a33e94f095f2b8b7790b76785ad/docs/changes/20261023-ai-knowledge-base/change.json). Use `lighthouse-review` for a candidate assessment. [Agent assignments](project.md#development-agent-assignments) define reviewer models; [GitHub workflow](github.md) defines PR presentation and merge authority.

### Responsibilities and independence

The implementation agent implements the slice, runs meaningful focused checks and inspects its own diff. Self-checks are evidence, not independent review.

One fresh reviewer context reviews each slice PR for bugs, security and requirements compliance. Give it the fixed base/head commits, issue, current requirements/plan, policy and test evidence rather than the implementation conversation. The same reviewer covers all three areas and reports coverage separately. It reports findings without editing the implementation or merging.

The orchestrator/integrator checks task gates, shared interfaces, migrations, integration, test evidence and current review state. It assigns corrections to the implementing owner and coordinates shared-record updates. It does not substitute self-review for the fresh review or accept code on the owner's behalf.

The human project owner decides scope, exceptions and final approval. A clear technical assessment is not permission to merge. An agent using the owner's GitHub credentials is not a separate GitHub reviewer identity.

Additional specialist review is warranted for a specific unresolved question, such as unfamiliar authentication/security behavior, parser isolation, financial races, migration or recovery risk. It is targeted additional coverage, not three default reviewers per PR. Specialist context and any remaining limitation are recorded.

### Findings and readiness

Each finding records a stable ID, code location, review area, severity, evidence or credible failure path, consequence, classification as violation or judgment, smallest adequate correction, disposition and whether it blocks readiness. A requirements violation names its requirement or policy source. Preserve findings and reasons when addressed, dismissed or deferred.

Blocking findings identify a documented violation or credible consequential failure: unauthorized access, data loss, overspending, unsafe migrations, broken required behavior or failing required checks. Critical/high-severity failures block; a lower-severity finding also blocks when it demonstrates a required behavior is broken. Missing meaningful verification blocks when it leaves required changed behavior unassessed.

Preferences, optional refactoring and speculative optimizations are advisory. Record them without silently expanding the slice. Unsupported hardening suggestions do not become blockers merely by assigning a severe label.

Assess readiness as `clear` only when all required review areas have adequate coverage, required independence is satisfied, required checks pass and no blocking finding remains. Use `changes_needed` for corrections and `incomplete` for missing coverage, identity or evidence. A blocking deferral remains blocking unless an explicit owner decision changes the applicable requirement/scope or permits the exception; retain its source.

### Correction limit

The limit is **two correction rounds after the initial review**, shared by all participating agents and reviewers for the PR.

1. Perform the initial independent review at round 0.
2. Reserve round 1 before the first correction batch, then review the revised candidate.
3. If needed, reserve round 2 before the next batch, then review that candidate.
4. If blocking findings remain after round 2, stop ordinary correction work and present the owner with remaining findings and a proposed escalation, scope change or other decision. Further correction work requires that decision.

A correction round is a batch fixing review findings followed by review refresh. Owner-directed amendments and records-only reconciliation do not consume a round; they do not erase unresolved findings or reset the allowance for the same finding set. Preserve the historical T1 count of 2; D20/D21 history is not retroactively renumbered. Changing models, rebasing, resuming or changing reviewer contexts does not reset the count. A content-equivalent rebase is not itself a correction round, but still requires candidate/evidence refresh. A later integration correction uses the same count rather than creating an automatic new allowance.

### Review refresh

Bind each assessment to actual base/head commit identities and material policy/requirements inputs. New fixes, conflict resolution, integration changes, dependency changes or changes to scope/policy refresh affected tests and review areas before the candidate is presented as current.

For a content-equivalent rebase, verify equivalence and the new integration base, update the candidate identity/evidence and retain unaffected coverage with its reason. Broader behavioral, schema, authorization or interface changes can require a broader review. The integrator must not change reviewed code and retain an unchanged clear assessment.

An earlier human approval does not automatically cover materially changed code. The final PR presentation identifies the current head, relevant base, behavior summary, actual test results, review coverage, findings and limitations. After repository setup, ordinary Git commits provide revision history; use current document sections and actual candidate commits rather than restoring obsolete draft references.

### GitHub enforcement

Required CI and conversation resolution are native checks. GitHub cannot establish fresh agent context, correct review conclusions or genuine human approval when all actors use the same account. Use the PR review/comment history for the current technical assessment; do not submit a same-account approving review as an independent approval. The owner merges manually under the GitHub workflow policy.

## Tooling and native commands

Use globally installed Lighthouse 0.2.0; this project has no vendored plugin or repository enable override. On another host verify compatible skill availability through its normal installation route. Commands are authoritative in [Maven](../pom.xml), [backend Maven](../backend/pom.xml) and [frontend npm](../frontend/package.json); read [local development](../docs/local-development.md) for startup/test profile details. Preserve global runtimes/settings. Native CI remains environment/dependency setup followed by frontend/backend tests; no Lighthouse/document/PR-metadata validators or orchestration wrappers are introduced.

Accepted concurrency is one, with sequential implementation then independent review. Two writers remains an unaccepted proposal. One writer owns shared planning/policy files. Operational jobs remain disabled. Hosting, paid AI evaluation and native provider controls retain separate authority; missing security/operations sources limit only dependent work and are not silently filled with an invented policy. The user is the default decision-maker.

## Repository and review evidence

The repository is public `igrgin/ai-knowledge-base`, main, MIT, with repository-local author identity `igrgin` / `10382095+igrgin@users.noreply.github.com` accepted during setup. Preserve global Git identity. There are 20 task issues; no branches are created for pending tasks. Main requires the successful real `Tests` context, strict updates, PRs and resolved conversations; administrator/linear-history/force-push/deletion controls remain as inspected, with zero native approving reviews for the same-account solo arrangement. Squash-only/manual merging remains the user action.

T1's [review assessment](https://github.com/igrgin/ai-knowledge-base/pull/21#issuecomment-6390736664) is clear at head `7252f046b96f9a33e94f095f2b8b7790b76785ad`, with [Tests CI](https://github.com/igrgin/ai-knowledge-base/actions/runs/40156815793) passing at that head. Original base is `b203ffd17063f65f06e0e4bea3cb49e9e68cdc44`; implementation comparison was `2227a489bb01da13732cf829902f9692ca6bc6a3` plus independently checked metadata descendants. Findings/security/requirements coverage, F1 addressed disposition and count 2 remain historical facts, not merge approval.

The [complete old review](https://github.com/igrgin/ai-knowledge-base/blob/7252f046b96f9a33e94f095f2b8b7790b76785ad/docs/changes/20261023-ai-knowledge-base/review.md), [setup evidence](https://github.com/igrgin/ai-knowledge-base/blob/7252f046b96f9a33e94f095f2b8b7790b76785ad/docs/github-setup.md) and [adoption source](https://github.com/igrgin/ai-knowledge-base/blob/7252f046b96f9a33e94f095f2b8b7790b76785ad/docs/lighthouse-adoption.md) remain in Git history.
