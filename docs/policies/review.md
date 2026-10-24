# Code review

The project owner accepted this policy in decision D14 in [the change index](../changes/20261023-ai-knowledge-base/change.json). Use `lighthouse-review` for a candidate assessment. [Agent assignments](development-agents.md) define reviewer models; [GitHub workflow](github-workflow.md) defines PR presentation and merge authority.

## Responsibilities and independence

The implementation agent implements the slice, runs meaningful focused checks and inspects its own diff. Self-checks are evidence, not independent review.

One fresh reviewer context reviews each slice PR for bugs, security and requirements compliance. Give it the fixed base/head commits, issue, current requirements/plan, policy and test evidence rather than the implementation conversation. The same reviewer covers all three areas and reports coverage separately. It reports findings without editing the implementation or merging.

The orchestrator/integrator checks task gates, shared interfaces, migrations, integration, test evidence and current review state. It assigns corrections to the implementing owner and coordinates shared-record updates. It does not substitute self-review for the fresh review or accept code on the owner's behalf.

The human project owner decides scope, exceptions and final approval. A clear technical assessment is not permission to merge. An agent using the owner's GitHub credentials is not a separate GitHub reviewer identity.

Additional specialist review is warranted for a specific unresolved question, such as unfamiliar authentication/security behavior, parser isolation, financial races, migration or recovery risk. It is targeted additional coverage, not three default reviewers per PR. Specialist context and any remaining limitation are recorded.

## Findings and readiness

Each finding records a stable ID, code location, review area, severity, evidence or credible failure path, consequence, classification as violation or judgment, smallest adequate correction, disposition and whether it blocks readiness. A requirements violation names its requirement or policy source. Preserve findings and reasons when addressed, dismissed or deferred.

Blocking findings identify a documented violation or credible consequential failure: unauthorized access, data loss, overspending, unsafe migrations, broken required behavior or failing required checks. Critical/high-severity failures block; a lower-severity finding also blocks when it demonstrates a required behavior is broken. Missing meaningful verification blocks when it leaves required changed behavior unassessed.

Preferences, optional refactoring and speculative optimizations are advisory. Record them without silently expanding the slice. Unsupported hardening suggestions do not become blockers merely by assigning a severe label.

Assess readiness as `clear` only when all required review areas have adequate coverage, required independence is satisfied, required checks pass and no blocking finding remains. Use `changes_needed` for corrections and `incomplete` for missing coverage, identity or evidence. A blocking deferral remains blocking unless an explicit owner decision changes the applicable requirement/scope or permits the exception; retain its source.

## Correction limit

The limit is **two correction rounds after the initial review**, shared by all participating agents and reviewers for the PR.

1. Perform the initial independent review at round 0.
2. Reserve round 1 before the first correction batch, then review the revised candidate.
3. If needed, reserve round 2 before the next batch, then review that candidate.
4. If blocking findings remain after round 2, stop ordinary correction work and present the owner with remaining findings and a proposed escalation, scope change or other decision. Further correction work requires that decision.

A correction round is a substantive correction batch followed by review refresh. Changing models, rebasing, resuming or changing reviewer contexts does not reset the count. A content-equivalent rebase is not itself a correction round, but still requires candidate/evidence refresh. A later integration correction uses the same count rather than creating an automatic new allowance.

## Review refresh

Bind each assessment to actual base/head commit identities and material policy/requirements inputs. New fixes, conflict resolution, integration changes, dependency changes or changes to scope/policy refresh affected tests and review areas before the candidate is presented as current.

For a content-equivalent rebase, verify equivalence and the new integration base, update the candidate identity/evidence and retain unaffected coverage with its reason. Broader behavioral, schema, authorization or interface changes can require a broader review. The integrator must not change reviewed code and retain an unchanged clear assessment.

An earlier human approval does not automatically cover materially changed code. The final PR presentation identifies the current head, relevant base, behavior summary, actual test results, review coverage, findings and limitations. After repository setup, ordinary Git commits provide revision history; use current document sections and actual candidate commits rather than restoring obsolete draft references.

## GitHub enforcement

Required CI and conversation resolution are native checks. GitHub cannot establish fresh agent context, correct review conclusions or genuine human approval when all actors use the same account. Use review comments/findings and Lighthouse records for the technical assessment; do not submit a same-account approving review as an independent approval. The owner merges manually under the GitHub workflow policy.
