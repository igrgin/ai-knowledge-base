# Migration to EGM project governance

Status: proposed implementation plan; migration is not executed.
Date: 2026-10-29.

## Overview

Replace Lighthouse as this repository's active development workflow with the
Elephant-Goldfish Model (EGM).
Rewrite the product/design records into an EGM design, retain a small roadmap
for delivery state, and make package contracts the entry points for implemented
behavior. Remove `.Lighthouse/` and every active instruction or reference that
requires it. This supersedes the earlier proposal to use EGM alongside Lighthouse.

The selected distribution is **EGM with small fixes**, the installed experimental
local derivative, version **0.2.0**, which supplies `egm-fix` alongside the design
skills. Its [package documentation](/Users/igrgin/.codex/plugins/cache/egm-local/egm/0.2.0/README.md)
and [Codex integration guide](/Users/igrgin/.codex/plugins/cache/egm-local/egm/0.2.0/docs/codex.md)
identify the applicable procedures. The future workflow guide should name this
distribution and resolve installed skill/documentation paths through the host's
skill catalog; machine-specific cache paths are inspection references in this
provisional plan, not permanent project configuration.

The owner requested a plan to remove the old workflow, reorganize its files and
rewrite the core documents. This document proposes that migration; it authorizes
neither deletion nor execution of later application tasks.

## Starting point

- T2 is complete: [PR #23](https://github.com/igrgin/ai-knowledge-base/pull/23)
  merged on 2026-10-29 as `d5b2efceda70db39bcf6e19fcdef69c9f236f297`.
  The local checkout remains on `2-add-minimal-persistence-and-contract-conventions`
  at its final head, `b4b3087696e9f58d81bcf3a723431745e6d3901e`. Use the merged T2
  baseline when preparing the migration candidate, preserving the separate local work.
- `egm-foundation-bootstrap` points to the older `3f0d084` baseline. The working
  checkout currently contains the uncommitted EGM map/contracts and additions to
  `AGENTS.md` and the existing plan. Preserve these exact contents before moving
  between branches; uncommitted files are not isolated by a branch name.
- T2's latest reconciliation establishes the starting limits and English-first
  quality gate as accepted. Preserve this result; the earlier adoption assessment
  no longer represents those items as unresolved.
- `.Lighthouse/` currently contains **15 files**: three scope records, two settings
  documents, nine research notes and one dated drift report. Its GitHub settings
  bridge routes to the existing [remote-work policy](../remote-work.md), which
  must remain authoritative. `AGENTS.md` and the draft `EGM.md` still route to the
  old workflow; no application dependency or CI invocation of Lighthouse was found.
- The unused local `codex/lighthouse-0.2-migration` branch still exists. Its
  disposition is separate from the already-deleted test migration branch.
- Final T2 includes the test removals and contract/documentation reconciliation
  made during planning. Carry forward its final obligations; do not restore
  superseded fixtures or test requirements from the earlier bootstrap baseline.

With T2 merged, the next step is to finalize the EGM migration before starting T3.
Recheck the merged baseline and local changes before any migration implementation.

## Goals and boundaries

The finished repository must be understandable and operable without loading any
Lighthouse skill, document, configuration or historical workflow instruction.
All active EGM routing and project-specific delivery rules must be reachable from
`AGENTS.md` and `EGM.md`.

Preserve product meaning, accepted architecture, requirements R1–R29, task IDs
T1–T20, contract IDs, task prerequisites, ownership, verification obligations,
completed work and unresolved proposals. The migration changes documentation and
workflow, not application behavior, dependencies, runtime limits or task approval.

Remove branding and obsolete process dependencies from the final tracked tree,
including the migration plan itself after its work is recorded. Retain Git and
PR history as historical evidence. Do not rewrite history, uninstall globally
installed plugins, erase closed PR discussions, alter native protections, or
change global agent settings. Those are outside repository migration.

## Target structure and authority

```text
AGENTS.md                              # EGM routing and essential project pointers
EGM.md                                 # Blueprint of implemented system boundaries
README.md                              # Demonstrated product behavior and startup
docs/
  egm-workflow.md                       # Skill routing, verification and handoff
  remote-work.md                        # Existing issue, branch, PR and merge authority
  roadmap.md                           # Tasks, dependencies, ownership and progress
  local-development.md                 # Existing native run/build/test guide
  contracts/
    README.md                          # Contract index, providers and consumers
    k0-foundation.md                    # Existing foundation contract
  designs/
    SITEMAP.md                         # Index of real design documents
    application.md                     # Consolidated product and architecture design
  research/                            # Nine retained technical investigations
  reviews/
    egm-migration/                     # New review/drift evidence for this migration
backend/src/main/java/com/aiknowledgebase/shared/
  EGM.md
  web/EGM.md
```

Do not create empty directories or speculative feature designs. When T3 is
separately authorized, its design can live at
`docs/designs/t3-identity-sessions/design.md`; that directory will also hold its
working brief/draft and design-review artifacts when needed.

| Document | Owns | Does not own |
| --- | --- | --- |
| `AGENTS.md` | When to load EGM skills and which project sources to consult | Detailed product requirements or copied plugin procedures |
| `EGM.md` and package `EGM.md` files | Actual interfaces, invariants, effects and verification pointers | Unimplemented features presented as existing behavior |
| `docs/designs/application.md` | Product purpose, R1–R29, journeys, accepted architecture, target behavior, limits, quality gate and remaining proposals | Live delivery status or permission to implement every task |
| `docs/roadmap.md` | T1–T20 outcomes, start/completion dependencies, path/schema ownership, requirement coverage and current progress | A second copy of feature designs or the skill manuals |
| `docs/contracts/README.md` | Contract availability and provider/consumer map | Speculative contract files for unstarted features |
| `docs/contracts/k0-foundation.md` | Existing cross-feature foundation conventions and consumer obligations | Concrete identity or future domain implementation |
| `docs/egm-workflow.md` | Skill routing, agent assignments, concurrency, verification and implementation-review handoff | Copied remote policy or implementation/release tooling |
| `docs/remote-work.md` | Issue and decision-record workflow, labels/associations, branches, commits, PRs, comments, CI operations, merge and closure authority | Product requirements, EGM personas or duplicate task progress |
| GitHub issues/PRs | Requested task scope, candidate changes, evidence, review and merge state | Old instructions that override current project workflow |

Use one authoritative home for each rule. A feature design may propose an
amendment to the application design; reconcile the accepted change at its owning
source before consumers rely on it. Document status must distinguish accepted
requirements, proposed mechanisms, implementation state and actual review results.

## Exact file disposition

Paths below are repository-relative. Each deletion follows successful extraction
and preservation verification, not a directory-wide delete performed first.

| Current file | Destination/action | Required treatment |
| --- | --- | --- |
| `.Lighthouse/scopes/initial-application/intent.md` | Rewrite into `docs/designs/application.md`; delete source | Preserve problem, audience, goals, constraints, exclusions and owner-selected behavior. |
| `.Lighthouse/scopes/initial-application/spec.md` | Rewrite into `docs/designs/application.md`; delete source | Preserve R1–R29, D-A–D-L rationale, interfaces, data/lifecycle rules, accepted limits, quality gates and C1–C12 dispositions. Replace workflow-specific C6 text with current review/operations ownership. |
| `.Lighthouse/scopes/initial-application/plan.md` | Split into application design, `docs/roadmap.md`, contract index and workflow guide; delete source | Preserve detailed slice reuse obligations, dependencies, migration ownership, verification, capacity and recovery prerequisites. Retain a compact T1/T2 result with PR evidence. Retire chronological process recaps and the unapplied two-writer proposal. |
| `.Lighthouse/settings/project.md` | Move substantive choices to `docs/egm-workflow.md#agent-assignments`; delete source | Preserve selected role/model/effort assignments and their provenance; separate development agents from the application's AI model. |
| `.Lighthouse/settings/github.md` | Delete after preserving its routing and residual rules | Retain `docs/remote-work.md` as canonical; apply the explicit [remote-policy disposition](#remote-policy-disposition), preserve the `AGENTS.md` pointer, then remove the old fallback clause and bridge. |
| `.Lighthouse/research/caching-evaluation.md` | `docs/research/caching-evaluation.md` | Retain no-cache rationale, measurement limits and links to the revised design/roadmap. |
| `.Lighthouse/research/costs.md` | `docs/research/costs.md` | Retain dated cost scenarios, assumptions and distinction from spending authority. Do not refresh prices as part of this migration. |
| `.Lighthouse/research/extraction-scaling-investigation.md` | `docs/research/extraction-scaling-investigation.md` | Retain stage separation, resource limits and hypothetical-versus-measured distinctions. |
| `.Lighthouse/research/java-spring-test-scope-review.md` | `docs/research/java-spring-test-scope-review.md` | Preserve adopted test ownership/reduction rationale and historical evidence limits. |
| `.Lighthouse/research/maven-frontend-test-integration.md` | `docs/research/maven-frontend-test-integration.md` | Preserve Maven/native npm lifecycle and skip semantics. |
| `.Lighthouse/research/maven-multi-module-investigation.md` | `docs/research/maven-multi-module-investigation.md` | Preserve build/module rationale; no new Maven feature modules. |
| `.Lighthouse/research/microservices-investigation.md` | `docs/research/microservices-investigation.md` | Preserve architecture alternatives and evidence limits. |
| `.Lighthouse/research/scalability-evaluation.md` | `docs/research/scalability-evaluation.md` | Preserve experiments, process boundaries and absence of hosted capacity claims. |
| `.Lighthouse/research/spring-modulith-investigation.md` | `docs/research/spring-modulith-investigation.md` | Preserve accepted direction, compatibility evidence and deferred implementation. |
| `.Lighthouse/research/2026-10-29_DRIFT_REPORT.md` | Retire from active tree; retain through its T2 commit/PR history | Do not relabel an old report as verification of rewritten files. Produce a new report against the migrated documents. |
| `AGENTS.md` | Rewrite | Route to EGM and the new authoritative sources; remove all Lighthouse dependencies. |
| `EGM.md` | Rewrite navigation and authority sections | Replace coexistence wording; keep implemented boundaries grounded in code. |
| `backend/src/main/java/com/aiknowledgebase/shared/EGM.md` | Inspect/update affected links and wording | Retain the reviewed application-error contract; keep K0 canonical. |
| `backend/src/main/java/com/aiknowledgebase/shared/web/EGM.md` | Inspect/update affected links and wording | Retain explicit MVC/security-filter limits, logging scope and verification ownership. |
| `docs/contracts/k0-foundation.md` | Retain and reconcile references only | Preserve typed errors, identity/transaction conventions, migrations, test boundaries and consumer handoffs. Keep the stable K0 identifier and path. |
| `docs/local-development.md` | Retain; repair moved research/design references if needed | Native manifests remain the command authority. |
| `docs/remote-work.md` | Retain; incorporate the bounded residual conventions listed below | Preserve current authority and operations; make remaining inherited rules self-contained here, without copying the retired plugin manual. |
| `README.md` | Small navigation edit | Preserve product/startup focus; link the architecture/design/development entry points. |
| `docs/designs/SITEMAP.md` | Create | Index the application design and later real feature designs, with status/coverage links. |
| `docs/contracts/README.md` | Create | Preserve the ten contract definitions and availability map described below. |
| `docs/egm-workflow.md` | Create | Make EGM routing and project-specific delivery self-contained. |
| `docs/roadmap.md` | Create | Preserve the task graph and delivery obligations without a Lighthouse record hierarchy. |
| `docs/reviews/egm-migration/review.md` | Create during actual review | Record exact candidate, completed reviewers, findings/dispositions and remaining limits. |
| `docs/reviews/egm-migration/2026-10-29_DRIFT_REPORT.md` | Create only if the actual drift run is on that date | Use the actual run date if later; inspect the rewritten documents rather than copying the previous report. |
| `docs/designs/egm-migration-plan.md` | This provisional plan; retire after migration completion | Preserve its approved content in Git/PR history before removing it from the final tree. |
| `docs/reviews/egm-migration/consolidated_review_report.md`, `docs/reviews/egm-migration/goldfish_review_report.md` | Preserve as pre-migration task/PR evidence; retire from the final active tree | These review and resolve this plan, not the migrated design. Preserve their dispositions before removing their retired-workflow references. |

Research relocation includes repairing links in both directions, removing old
workflow instructions and preserving exact technical meaning. Original quotes
that name the retired workflow remain in recoverable history; current documents
state the resulting accepted rule and link ordinary commit/PR evidence instead.

Application source, SQL migrations, POMs, package manifests/locks, configuration,
scripts and `.github/workflows/tests.yml` are unchanged by this plan. Inspect all
tracked and relevant untracked text at execution time for any newly introduced
references; report a substantive scope expansion before modifying new areas.

## Core rewrites

### Application design

Use EGM's design-document structure: Overview, Problem, Goals/Non-goals, Open
Questions, Critical User Journeys, Technical Plan, Alternatives, Non-functional
Requirements and Detailed Implementation/Verification.

Integrate the current intent and specification into coherent prose rather than
concatenating the files. Keep R1–R29 and existing D/C identifiers where consumers
use them. Describe the major journeys: registration/private projects, upload and
viewing, source selection and grounded chat, independent imports, transcript
publication, and purge/recovery. Carry each journey's failure cases and tests.

Move the existing per-slice implementation/reuse detail here, grouped by feature
boundary and linked to roadmap task IDs. Future detailed feature designs can
replace their own subsection by reference when they become useful. Do not write
18 speculative feature specifications during the migration.

Keep the accepted starting numeric limits and English-first evaluation criteria.
Preserve the proposed status of C4's unfinished-upload handling and the separately
proposed retention and operating details. Experimental throughput targets remain
experiments. Preserve the quality gate: at least 18 of 20 supported-question
answers judged correct and grounded, appropriate abstentions for all 10 unsupported
questions, and citations resolving to retrieved passages and the correct physical
pages. Existing model/access, hosted operations and paid-call decisions remain
separate from this documentation change.

### Roadmap and contract index

Keep all 20 task IDs and their actual issue associations. For each, preserve the
outcome, requirement coverage, start gate, integrated completion prerequisites,
owner/path boundaries, schema ownership, verification responsibility and current
state. Preserve independent starts rather than treating numeric order as a chain.
Move the requirement-to-task coverage table and dependency graph here.

The contract index lists K0, KP, KU, KD, KH, KS, KV, KC, KL and KI with producer,
consumers and availability. Link only K0 as an implemented published contract;
later entries are planned boundaries until their actual provider/consumer agreement.
The roadmap references this index rather than duplicating its definitions.

Retain completed T1/T2 evidence through concise entries and PR links, including
the final T2 documentation reconciliation. Historic review counts/findings remain
in their original evidence; do not make those historical counters the EGM review
policy. T3–T20 do not become authorized because their documents were moved.

### EGM workflow and agent entry point

The workflow guide will route by need:

| Need | Route |
| --- | --- |
| Unclear outcome or consequential unresolved direction | `egm-brainstorm` with existing decisions as context |
| Agreed outcome needing an implementable design | `egm-draft`, reusing the application design and relevant contracts |
| Design comprehension, critique and implementation readiness | `egm-review`; use `egm-review-loop` only for an explicitly selected bounded document review |
| Resolve design-review findings | `egm-resolve`, preserving user decisions and unresolved findings |
| Established localized software defect | `egm-fix`; consequential behavior changes return to design |
| Missing documentation of implemented boundaries | `egm-bootstrap` with the bounded subtree as `path`, `target_filename="EGM.md"`, and the task artifact directory |
| Suspected document/code mismatch | `egm-drift-report`, then verify each mismatch and decide whether to correct the code or the documentation |
| Implement an accepted feature | Normal coding against the reviewed design, native verification and a fresh implementation review |

The selected distribution's [getting-started guide](/Users/igrgin/.codex/plugins/cache/egm-local/egm/0.2.0/docs/getting_started.md)
leaves implementation tooling to the project.
There is no `egm-build`, deployment operator or substitute for application tests.
Specify the project handoff: identify the accepted scope and baseline, implement
the design's file/check list, verify owned behavior, update affected contracts,
and obtain a fresh report-only review of the diff for bugs, security and design
compliance before presenting the PR. A design review alone does not review code.

Retain the selected Sol/Astra/Luna assignments, current native `Tests` check and
concurrency 1. Owner approval and squash-merge execution follow `docs/remote-work.md`.
Dispatch required fresh reviewers sequentially, with the parent waiting;
explicitly record this project override
of parallel reviewer defaults. Do not copy a second version of EGM's stage gates,
templates, iteration budgets or personas into the repository. Use the installed
skill's own review procedure and report failed reviewers as incomplete.

Link `docs/remote-work.md` for branch naming and remote operations, retaining the
owner-selected unnamespaced migration branch and existing issue associations.
Keep meaningful test ownership and the one-writer rule for shared contracts and
migration ordering. A clean review does not itself authorize merge, deployment
or paid calls.

For architecture/package bootstrap work, the workflow guide and `AGENTS.md` route
must require `target_filename="EGM.md"`; the [skill input documentation](/Users/igrgin/.codex/plugins/cache/egm-local/egm/0.2.0/skills/egm-bootstrap/SKILL.md#inputs)
otherwise defaults to `README.md`. Keep human-maintained READMEs product-focused.

For each EGM invocation, name the task's artifact directory explicitly. Feature
working artifacts belong in `docs/designs/<task-slug>/`; selected drift/technical
review records belong in `docs/reviews/<task-slug>/`. Avoid a global scratch
`design_draft.md` reused across unrelated tasks. Do not create a custom context
skill or copy the installed plugin into this repository; `AGENTS.md` is sufficient.

### Remote-policy disposition

Retain `docs/remote-work.md` as the single remote-operation authority and preserve
its conditional pointer in `AGENTS.md`. The new EGM workflow guide links to it.
During migration, make only the residual conventions below explicit in that
existing document; this plan does not amend the active policy now.

| Existing source/rule | Planned disposition and owning location |
| --- | --- |
| Current `docs/remote-work.md` and the settings bridge's precedence rule | Preserve all current issue/decision, branch, commit/push, PR-state/edit, comment, CI, approval, merge/closure and attribution rules. Replace the bridge with direct entry-point links. Its current overrides continue to govern. |
| Record writing, issue-intent labels and native associations | Carry forward exactly one primary `bug` (repair), `feature` (addition) or `spike` (decision/research) label; preserve existing definitions/colors and separate priority/status. Inspect and reuse existing remote records, preserve human content and history, and verify actual supported issue/dependency/branch/PR associations rather than inferring them from names or body text. Keep the real T-to-issue mapping in the roadmap; task IDs need not equal issue numbers. Add these conventions to the remote guide. |
| Checkout and branch safety not already stated by the remote guide | Retain suitable checkouts and established issue/title associations; prepare agents from a committed baseline containing needed instructions; preserve unique/dirty work before any authorized recoverable archive. Keep global Git configuration unchanged. Remote branch naming remains with the remote guide, and concurrency/shared-writer ownership remains with the EGM workflow guide. |
| Completion and evidence | Keep decision work under the remote guide's decision-issue workflow, without inventing an implementation PR requirement. Preserve partial `Refs` versus completed `Closes` issue references. Keep behavior changes, actual checks, independent review coverage/limits and candidate identities in task/PR evidence, using the installed `pr` skill for bodies. The EGM workflow guide owns the fresh implementation-review handoff; the remote guide owns publication and merge authority. |
| Native protections and CI | Preserve existing provider controls and actual required checks, including `Tests`; verify supported native settings rather than inventing guarantees. Keep independent technical review distinct from approval by a separate GitHub identity. This migration changes no protection or CI setting and adds no metadata-only CI. |
| Superseded defaults and plugin-only machinery | Retire the implicit “other Lighthouse defaults still apply” fallback, old scope-record routing, plugin-specific templates and completed setup recipes. Do not reimport old commit-message or human-only merge defaults over the current remote guide. Keep EGM procedures in the installed skills, with current project conventions in their owning documents. |

This disposition preserves current policy and assigns the remaining applicable
rules a home. It introduces no remote resource creation, label changes, new
protection settings or policy dependence on the retired plugin.

## Implementation sequence

1. **Freeze and preserve inputs.** Verify the completed T2 merge and record the
   exact baseline used. Preserve all uncommitted bootstrap work, then update or
   recreate the owner-selected unnamespaced migration branch from that baseline.
   Do not carry the discarded test migration back in. Preserve the original document bytes
   and candidate identity for reviewers, plus the source inventory and an
   old-section-to-new-section preservation map in external review scratch.
2. **Build the new authoritative documents.** Write the application design,
   roadmap, contract index and workflow guide, then the design sitemap. Incorporate
   the listed residual remote conventions in `docs/remote-work.md`. Verify every
   R/T/contract identifier, proposal distinction, consumer obligation, policy rule
   and retained rationale against preserved sources before deleting any source.
3. **Relocate supporting research.** Move all nine named notes; repair relative
   links and current-source pointers, preserving original dates, claims and
   evidence limitations. Keep old reports recoverable in Git rather than
   recycling them as current evidence.
4. **Switch repository entry points.** Rewrite `AGENTS.md` and `EGM.md`, update
   README navigation and reconcile K0/package/development pointers. Retain the
   mandatory remote-work pointer and explicit EGM bootstrap filename. A fresh
   reader must reach all required inputs through these current documents.
5. **Remove the retired structure.** Delete the 15 inventoried `.Lighthouse/`
   files only after coverage and remote-policy routing checks pass; remove the
   empty directory. Inspect any additional files that appeared since this plan
   instead of using an
   unqualified recursive removal. No compatibility stubs should continue routing
   agents into the retired workflow.
6. **Review the migration as a candidate.** Run EGM design/readiness review on
   the rewritten application design and targeted document review on the workflow,
   remote-work guide, roadmap and contract index, with preserved original sources
   as a deliberate reading list. Keep fresh reviewer contexts and run them sequentially. Resolve
   consequential differences with the owner; do not classify requirement changes
   as editorial cleanup. Run drift detection against the implemented map and
   package contracts, distinguishing future design work from implementation gaps.
7. **Complete delivery and remove temporary planning text.** Preserve the accepted
   migration plan, pre-migration review/resolution reports and detailed mapping in
   Git/PR history, then retire those temporary files from the final tracked tree.
   Retain concise neutral evidence of the migrated candidate's actual review.
   Present the documentation migration for owner review and follow the remote
   guide's merge authority; rerun affected checks after any final edits.
8. **Reconcile active GitHub task entry points.** Once new paths are published
   and this remote-write step is authorized, capture each affected T3–T20 issue's
   identifier, original body and revision/update time in task/PR evidence. Record
   the task-to-issue mapping, dependencies and existing supported native branch/PR
   links. Re-read before each write, preserve intervening human changes, and change
   only the governing-document links and workflow instructions. Preserve scope,
   ownership, dependencies and tests. Read back each update and record its result;
   verify the mapping, dependency relationships, branch/PR associations and links.
   Retain this recovery record with task/PR history for subsequent rollback,
   outside the active policy documents. Follow [recovery after issue updates](#after-issue-updates)
   if rollback is needed. Repository and issue recovery are separately visible
   outcomes; remote writes require applicable authority and are not authorized by
   this plan-only revision. Preserve closed T1/T2 history.
9. **Retire the remaining migration branch.** Inspect the unique commits and any
   remote counterpart of `codex/lighthouse-0.2-migration`. Once useful content is
   preserved and the execution scope covers branch cleanup, remove that obsolete branch/ref. Preserve
   ordinary task branches and Git history; do not uninstall the shared plugin.
10. **Begin the first new EGM feature only when requested.** Use T3's requirements
    and K0 to draft its concrete identity/session/security interfaces, then review
    the design before implementation. T2 needs no retrospective feature draft.

## Verification and completion criteria

- The final tracked tree has no `.Lighthouse/` paths, case-insensitive Lighthouse
  references, old skill invocations or live links to its former documents. Search
  paths as well as Markdown, prose/code spans and native configuration. Include
  relevant untracked files; exclude `.git`, dependencies and generated outputs.
  This provisional plan is deliberately removed before that final check.
- Every local Markdown path and anchor resolves. Research cross-links, the design
  sitemap, package navigation and pending-task entry points lead to current sources.
- A preservation matrix accounts for all 29 requirements, all 20 tasks, all ten
  contract boundaries, design rationale, concern dispositions, feature verification,
  ownership/migration rules, all 15 `.Lighthouse/` files, the remote-policy disposition
  and the latest accepted T2 changes. No item is lost, silently weakened, newly
  accepted or presented as already implemented.
- A fresh agent can identify the governing requirement, current implementation,
  next task's prerequisites, native tests and correct EGM route without reading a
  retired document. It can distinguish design approval from implementation and
  merge authorization, and design review from code review. It reaches the retained
  remote-work authority and uses `EGM.md` explicitly for package bootstrap.
- The diff changes documentation only. Runtime/build/test files and numeric
  product limits remain unchanged. Check documented commands against native
  manifests, validate links and review the documentation diff. Do not add metadata
  CI or run the application suite solely for this migration. Existing required CI
  still follows repository policy.
- EGM review/drift evidence identifies the actual candidate and reading list.
  Unrun reviewers and unresolved consequential findings prevent a completed review
  claim. Existing source behavior is not changed to make documentation pass.
- Repository cutover and GitHub follow-up have separate visible outcomes. Do not
  claim all future task entry points are EGM-controlled while open issues still
  instruct agents to use the retired workflow.

## Alternatives and recovery

The prior coexistence approach is superseded by the owner's full-replacement
request. Renaming `.Lighthouse` to `.EGM` would preserve its record hierarchy and
hidden workflow dependencies, so the proposal instead reorganizes by reader need.
Regenerating every package contract or rewriting every feature design immediately
would add speculative material; expand documentation when real boundaries appear.

Perform the migration in an isolated, preserved candidate. Complete extraction
and coverage checks, repairing lost meaning before deleting old source documents.
The later candidate review follows that deletion; repair any loss it uncovers
from the preserved originals before integrating the candidate. If the candidate
is abandoned, retain the preserved T2/bootstrap contents and discard only this
migration's changes.

### Before issue updates

If recovery is needed after repository merge but before any issue-body update,
use a targeted revert of the migration commits and verify the restored document
links and workflow. The issues still use their previous entry points, so no remote
body restoration is needed. Preserve unrelated work; use no hard reset.

### After issue updates

If any issue body was updated, repository rollback alone is incomplete. Use the
step 8 recovery record to identify the affected issues, including a partially
completed cutover. Coordinate the repository revert with authorized restoration
or repointing of those issue entries to the recovered documents and workflow.
Read current bodies and revisions first; reconcile only the migration's link and
instruction changes, preserving subsequent human edits, scope changes and history.
Do not blindly replace a current body with its saved original.

Read back the repository and affected issues, verifying document paths, governing
workflow, task-to-issue mapping, dependencies and supported native branch/PR links.
Recovery is complete only when both states agree. If remote-write authority or
access is missing, report the repository result and the pending issue recovery
separately; do not claim recovery complete.

## Next decision

Review the revised structure, file disposition and workflow, including the resolved
review findings. Migration implementation requires a separate instruction and a
verified merged-T2 baseline. This plan revision changes neither the active project
instructions nor the repository layout; it authorizes no remote edits or later tasks.
