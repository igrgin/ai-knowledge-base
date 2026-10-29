# Goldfish Annotated Review Report

Status: resolved

Resolution covers the authorized textual repairs. Independent re-review of the revised plan is pending; migration implementation has not begun.

**Target:** [migration plan](../../designs/egm-migration-plan.md) (`full file`).
**FindingsList:** A1–A3 and N1–N7 from the [combined review](consolidated_review_report.md), plus its existing T2-baseline follow-up, B1.
**Authorization:** The owner requested `egm-resolve` to address the findings and update the plan, explicitly excluding migration implementation. The proposed textual fixes below fall within that instruction. Existing project instructions, application files, remote records and Git state remain outside this edit.

Affected line numbers below identify the reviewed 334-line document, SHA-256 `62663e5fa333511057ae9315db976aed1f98789c11187f88c9f3147d39e4eada`. The [preserved input](/var/folders/sj/4nd1nyqd51s69mn57s_4bc_h0000gn/T/egm-migration-resolve-slo4rp9v/before/docs/designs/egm-migration-plan.md) and [exact applied patch](/var/folders/sj/4nd1nyqd51s69mn57s_4bc_h0000gn/T/egm-migration-resolve-slo4rp9v/plan.patch) make the replacements checkable. The [resolution table](consolidated_review_report.md#resolution-applied) links every item to its revised owning section.

## Prioritized Findings & AI Suggestions

### A1 [Zoom Out / Design / Structural / Codebase Details] — Severity: Major

- **Affected Scope:** Original lines 91–141, 220–230 and 246–261; authority, file disposition and cutover steps.
- **Discrepancy Description:** “The reviewed snapshot contains **15** `.Lighthouse/` files, including the untracked `.Lighthouse/settings/github.md`. The plan inventories 14 and omits both this bridge and `docs/remote-work.md` from its disposition table.”
- **Rationale:** “The bridge makes the remote-work guide authoritative for named operations while retaining other Lighthouse defaults. The plan's catch-all inspection protects against blind deletion, but leaves the implementer to decide which of those remaining rules survive.”
- ***AI Suggestion:*** PROPOSE FIX.
- ***Justification:*** The current settings bridge, `AGENTS.md` remote pointer and remote guide establish the existing authority. Preserve that authority and the applicable residual conventions; remove only the fallback dependency and superseded plugin machinery in the future migration. The installed GitHub guide's sections on records, intent labels, worktrees, PRs and native controls provide the residual inventory. The current remote guide already overrides commit and merge-operation defaults.
- ***Proposed Edit:***
  - Add the remote guide to the target structure, authority table and file disposition; add the settings bridge to the 15-file retirement inventory.
  - Add **Remote-policy disposition** with explicit retention of current remote rules, one primary `bug`/`feature`/`spike` label, verified native associations, safe checkout handling, partial/completed issue references, review evidence and existing native protections.
  - Retire the implicit old-default fallback, plugin-only routing/templates and completed setup recipes. Preserve the current remote guide's commit/merge rules instead of reintroducing superseded defaults.
  - Retain the conditional `AGENTS.md` pointer; link the EGM workflow guide to the remote guide. Extend preservation checks to this disposition.
  - Preserve pre-migration reports with task/PR history before their eventual retirement, so they cannot defeat the final reference-removal check.

### A2 [Comprehension / Design / Zoom Out] — Severity: Major

- **Affected Scope:** Original line 209 and lines 232–236; bootstrap route and invocation guidance.
- **Discrepancy Description:** “The plan chooses `EGM.md` as the architecture-contract filename but invokes `egm-bootstrap` without selecting it.”
- **Rationale:** “Following the current route can generate or update README contracts instead of extending the chosen architecture map.”
- ***AI Suggestion:*** PROPOSE FIX.
- ***Justification:*** The installed bootstrap input definition defaults to `README.md` and supports the chosen alternate filename. This repair uses the existing skill input and keeps the combined report's Major severity.
- ***Proposed Edit:*** Replace the route with “`egm-bootstrap` with the bounded subtree as `path`, `target_filename="EGM.md"`, and the task artifact directory.” Require the same filename in the planned workflow and `AGENTS.md` route, and link the skill's input documentation.

### A3 [Implementation Readiness] — Severity: Major

- **Affected Scope:** Original lines 274–279 and 324–328; issue cutover and recovery.
- **Discrepancy Description:** “After T3–T20 issue bodies are updated, reverting only the repository would remove the new destinations and restore the previous governance while those issues still direct agents to EGM and deleted paths.”
- **Rationale:** “The plan separates repository cutover from remote follow-up, but its rollback covers only the repository.”
- ***AI Suggestion:*** PROPOSE FIX.
- ***Justification:*** Git rollback cannot restore remote bodies. Capture issue IDs, original bodies and revisions before authorized updates, then reconcile against current content during recovery to preserve later human edits.
- ***Proposed Edit:*** Expand step 8 to capture originals and actual associations, reread before each write, record verified updates and retain recovery evidence. Add **Before issue updates** and **After issue updates** recovery states. Require repository and issue verification before declaring recovery complete; report remote recovery as pending when authority or access is absent.

### N1 [Comprehension] — Severity: Nit

- **Affected Scope:** Original lines 8, 157–159 and 213–225.
- **Discrepancy Description:** “Expand Elephant-Goldfish Model at first use and identify/link the selected distribution.”
- **Rationale:** “The installed package is the local 0.2.0 derivative supplying `egm-fix`; the plan currently relies on an unspecified ‘installed guide.’”
- ***AI Suggestion:*** PROPOSE FIX.
- ***Justification:*** The distribution matters because the plan routes bug fixes to its extension. This context helps a human execute the plan without assuming the upstream package has identical capabilities.
- ***Proposed Edit:*** Name **EGM with small fixes**, experimental local derivative **0.2.0**; link its installed README, Codex integration and getting-started guide. State that the future workflow resolves installed paths through the host skill catalog rather than copying this machine's cache paths into permanent configuration.

### N2 [Style] — Severity: Nit

- **Affected Scope:** Original lines 172–174.
- **Discrepancy Description:** “Say ‘Keep the accepted starting numeric limits and English-first evaluation criteria,’ then preserve the separate proposed status of unfinished-upload, retention and operating details.”
- **Rationale:** The original wording compresses distinct subjects and repeats acceptance terminology.
- ***AI Suggestion:*** PROPOSE FIX.
- ***Justification:*** This is a small clarity improvement that preserves every acceptance/proposal distinction and numeric quality gate.
- ***Proposed Edit:*** “Keep the accepted starting numeric limits and English-first evaluation criteria. Preserve the proposed status of C4's unfinished-upload handling and the separately proposed retention and operating details.”

### N3 [Style] — Severity: Nit

- **Affected Scope:** Original line 210.
- **Discrepancy Description:** “Replace ‘deliberate code-versus-document disposition’ with ‘verify each mismatch and decide whether to correct the code or the documentation.’”
- **Rationale:** The abstract phrase obscures the action following drift detection.
- ***AI Suggestion:*** PROPOSE FIX.
- ***Justification:*** The direct action preserves the original workflow without adding a procedure.
- ***Proposed Edit:*** Use the quoted replacement in the `egm-drift-report` route.

### N4 [Style] — Severity: Nit

- **Affected Scope:** Original lines 305–308.
- **Discrepancy Description:** “Define static command validation as checking documented commands against native manifests, alongside link and documentation-diff checks.”
- **Rationale:** The prior phrase could mean syntax checking, manifest comparison or running commands.
- ***AI Suggestion:*** PROPOSE FIX.
- ***Justification:*** Explicit validation avoids unnecessary application test execution for a documentation-only migration.
- ***Proposed Edit:*** “Check documented commands against native manifests, validate links and review the documentation diff.” Retain the unchanged-runtime boundary and existing required-CI policy.

### N5 [Style] — Severity: Nit

- **Affected Scope:** Original line 277.
- **Discrepancy Description:** “Name the associations to preserve and verify: task-to-issue mapping, existing dependency relationships, and applicable native branch/PR links.”
- **Rationale:** “Native associations” does not identify the relationships that must survive.
- ***AI Suggestion:*** PROPOSE FIX.
- ***Justification:*** Naming the existing relationships makes step 8 verifiable without narrowing A1's preservation obligations or inventing new associations.
- ***Proposed Edit:*** Capture and verify task-to-issue mapping, dependencies, supported existing branch/PR links and replacement document links in step 8 and recovery verification.

### N6 [Style] — Severity: Nit

- **Affected Scope:** Original lines 265–267.
- **Discrepancy Description:** “Replace ‘do not bless changed requirements as editorial cleanup’ with ‘do not classify requirement changes as editorial cleanup.’”
- **Rationale:** The approval metaphor is colloquial and less precise than the direct action.
- ***AI Suggestion:*** PROPOSE FIX.
- ***Justification:*** The change preserves the requirement-change boundary.
- ***Proposed Edit:*** Apply the quoted replacement in candidate review step 6.

### N7 [Style] — Severity: Nit

- **Affected Scope:** Original lines 324–325.
- **Discrepancy Description:** “Replace ‘before removal/integration’ with explicit source-deletion and candidate-integration wording.”
- **Rationale:** The compressed wording omits the objects and phases of the two operations.
- ***AI Suggestion:*** PROPOSE FIX.
- ***Justification:*** Candidate review occurs after candidate deletion in the existing sequence. The repair must clarify that sequence rather than move review ahead of deletion.
- ***Proposed Edit:*** “Complete extraction and coverage checks, repairing lost meaning before deleting old source documents. The later candidate review follows that deletion; repair any loss it uncovers from the preserved originals before integrating the candidate.”

### B1 [Design / Intermediate Zoom Out] — Severity: Nit; supplemental baseline follow-up

- **Affected Scope:** Original lines 20–42, implementation step 1 and Next decision.
- **Discrepancy Description:** The plan still says PR #23 is open, although both review runs captured its completed merge.
- **Rationale:** The existing baseline recheck mitigates execution risk, but the starting description should identify final T2.
- ***AI Suggestion:*** PROPOSE FIX.
- ***Justification:*** Captured PR metadata identifies merge commit `d5b2efceda70db39bcf6e19fcdef69c9f236f297`; local HEAD remains `b4b3087696e9f58d81bcf3a723431745e6d3901e`. Refreshing the plan does not switch branches or apply the migration.
- ***Proposed Edit:*** Identify the completed 2026-10-29 T2 merge, the separate local checkout/bootstrap state, and the need to verify the merged baseline before authorized implementation. Replace waiting-for-T2 language with preservation and baseline verification.

## Post-resolution synchronization

Applied all ten consolidated finding fixes (three Major, seven Nit), plus the existing baseline refresh B1. Counts: 10 finding fixes and 1 supplemental baseline fix applied; 0 contested, 0 escalated, 0 declined, 0 unaddressed findings. Original review severities are unchanged.

The revised plan has 417 lines, SHA-256 `38c0c32fd84b813004bd1b414954a23606cc79b6274bb4f87cbdb1b21747d44e`. Its semantic boundary is now line 282: design scope 1–281, implementation/recovery scope 282–417. In particular, remote-policy disposition starts at 262, verification at 343, recovery before issue updates at 389, and recovery after issue updates at 396.

Verification covered local paths/anchors, Markdown structure, whitespace, the complete 15-file retirement inventory, preserved requirement/task/contract identifiers, numeric quality gates, concurrency and the final diff. A hash comparison against the [starting-state manifest](/var/folders/sj/4nd1nyqd51s69mn57s_4bc_h0000gn/T/egm-migration-resolve-slo4rp9v/manifest.json) confirms that the only existing files changed are the plan and combined report. This resolution report is the only new repository file; the other 66 starting files are unchanged. Git HEAD is unchanged. Application tests were not run for these prose edits.

A fresh independent review of the edited plan has not run. Migration implementation remains unrequested and unexecuted: no workflow file was relocated/deleted, no active policy was edited, and no branch, remote record or application file changed.
