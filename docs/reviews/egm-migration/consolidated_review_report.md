# EGM migration plan review

Date: 2026-10-29. Preset: full consolidated `egm-review`. This document combines two reviews of the original plan and a fresh review of the revised plan.

**Current status: all recorded review findings are addressed.** The fresh full five-stage review found no Blocker or Major issue and two minor wording suggestions, N8 and N9. Both wording fixes have now been applied at the owner's request. The earlier ten findings and baseline follow-up remain resolved. The migration has not started.

The [migration plan](../../designs/egm-migration-plan.md) was unchanged during the first two review runs, then revised using `egm-resolve`. The [resolution report](goldfish_review_report.md) records those fixes. The latest review assessed that revised 417-line version, which stayed unchanged throughout the review. The subsequent N8/N9 wording changes are recorded below; they have not received another full review. Earlier findings and line numbers remain as history of the preserved 334-line version; the latest result below supersedes the pending-review status recorded at resolution time.

## Required corrections

### A1 — Major: preserve the current remote-work authority explicitly

Affected plan sections: [authority and file dispositions](../../designs/egm-migration-plan.md#target-structure-and-authority), lines 91–141; workflow rules, lines 220–230; implementation steps 2–5, lines 246–261.

The reviewed snapshot contains **15** `.Lighthouse/` files, including the untracked `.Lighthouse/settings/github.md`. The plan inventories 14 and omits both this bridge and `docs/remote-work.md` from its disposition table. The bridge makes the remote-work guide authoritative for named operations while retaining other Lighthouse defaults. The plan's catch-all inspection protects against blind deletion, but leaves the implementer to decide which of those remaining rules survive.

The existing [remote-work guide](../../remote-work.md) already covers issue authority, branch naming, commits, PR edits, comments, CI, approval and merge. Retain it as the canonical remote policy, preserve its entry-point link, and have the new EGM workflow guide reference it. Add the settings bridge to the retirement inventory. Explicitly identify which still-applicable inherited rules are preserved and which are intentionally retired before deleting it. Examples requiring disposition include the primary intent-label convention and native issue/branch associations; this review does not choose those policies for the owner.

**Done when:** the refreshed inventory covers every current file; the authority table gives remote work one home; the preservation map accounts for the bridge and remaining imported rules; no agent must load the retired plugin to determine the resulting policy.

### A2 — Major: route bootstrap to `EGM.md`

Affected plan sections: [workflow routing](../../designs/egm-migration-plan.md#egm-workflow-and-agent-entry-point), line 209 and lines 232–236.

The plan chooses `EGM.md` as the architecture-contract filename but invokes `egm-bootstrap` without selecting it. The installed [bootstrap input documentation](/Users/igrgin/.codex/plugins/cache/egm-local/egm/0.2.0/skills/egm-bootstrap/SKILL.md:44) defaults to `README.md` and explicitly supports `target_filename="EGM.md"`. Following the current route can generate or update README contracts instead of extending the chosen architecture map.

Require that input alongside the bounded subtree and artifact location in the planned workflow guidance. No wrapper or custom bootstrap implementation is needed.

**Done when:** an agent following the documented route selects `EGM.md` explicitly and preserves the product/startup purpose of human-maintained READMEs.

Comprehension rated this Major; design and zoom-out reviewers rated it Nit. The consolidated rating is Major because the default changes the intended output destination. The small size of the correction does not remove that consequence.

### A3 — Major: include issue entry points in rollback

Affected plan sections: [implementation step 8](../../designs/egm-migration-plan.md#implementation-sequence), lines 274–279; [recovery](../../designs/egm-migration-plan.md#alternatives-and-recovery), lines 324–328.

After T3–T20 issue bodies are updated, reverting only the repository would remove the new destinations and restore the previous governance while those issues still direct agents to EGM and deleted paths. The plan separates repository cutover from remote follow-up, but its rollback covers only the repository.

Define recovery before and after issue reconciliation. Before remote changes, repository rollback suffices. Before authorized issue edits, capture each affected issue ID and original body, then record each verified update. Use those inputs to coordinate repository rollback with authorized restoration or repointing of the entry points. Reconcile against current issue contents rather than overwriting intervening scope changes. Verify both links and workflow instructions, and leave the remote outcome visibly pending if the required write is not authorized. The second run reinforces this existing correction; it does not add a separate rollback finding.

**Done when:** both rollback states have a defined completion check and neither can silently leave active issues pointing to the wrong workflow or missing files.

## Minor clarity suggestions

| ID | Plan location | Suggested change |
| --- | --- | --- |
| N1 | 8, 157–159, 213–225 | Expand Elephant-Goldfish Model at first use and identify/link the selected distribution. The installed package is the local 0.2.0 derivative supplying `egm-fix`; the plan currently relies on an unspecified “installed guide.” Keep plugin procedures at their source. |
| N2 | 172–174 | Say “Keep the accepted starting numeric limits and English-first evaluation criteria,” then preserve the separate proposed status of unfinished-upload, retention and operating details. |
| N3 | 210 | Replace “deliberate code-versus-document disposition” with “verify each mismatch and decide whether to correct the code or the documentation.” |
| N4 | 305–308 | Define static command validation as checking documented commands against native manifests, alongside link and documentation-diff checks. |
| N5 | 277 | Name the associations to preserve and verify: task-to-issue mapping, existing dependency relationships, and applicable native branch/PR links. Do not let a wording simplification discard relationships retained under A1. |
| N6 — new in rerun | 265–267 | Replace “do not bless changed requirements as editorial cleanup” with “do not classify requirement changes as editorial cleanup.” This makes the instruction direct and objective. |
| N7 — new in rerun | 324–325 | Replace “before removal/integration” with explicit source-deletion and candidate-integration wording. Preserve the sequence: extraction/coverage checks before deleting source documents; repairs from later candidate review before integration. |

These suggestions were unapplied at the end of the review runs and are now addressed in the revised plan. N5 preserves the broader native-association obligations. N7 names source deletion and candidate integration without moving candidate review ahead of the working-tree deletion specified in steps 5 and 6.

## First-run comprehension result

The fresh reader's complete paraphrase is retained below. It reflects what the plan says, including its historical description of T2, rather than asserting that the historical state is still current.

### What this proposes

Replace the repository’s Lighthouse workflow with EGM after T2 is finalized and before T3 begins. Consolidate product and architecture records into an application design, retain task authority and progress in a roadmap, and use package contracts to document implemented behavior. Preserve requirements, decisions, dependencies, ownership and verification obligations while removing the retired workflow’s files and active references. Repository cutover, GitHub issue updates and later feature implementation retain separate authorization boundaries.

### How the existing system works, per this document

Lighthouse currently supplies three scope records, project settings and supporting research; `AGENTS.md` and the draft `EGM.md` route agents into that workflow. T2 establishes the migration baseline, while K0 and the existing package contracts supply foundation conventions and implemented error-boundary documentation. An uncommitted EGM bootstrap must be preserved and reconciled with final T2 changes. The document describes this governance arrangement sufficiently to explain the proposed migration, without establishing a complete account of current application runtime behavior.

## First-run scope and evidence

- Reviewed target: 334 lines, SHA-256 `62663e5fa333511057ae9315db976aed1f98789c11187f88c9f3147d39e4eada`.
- Semantic boundary: line 238, “Implementation sequence.” FeatureRange 1–237; DetailedRange 238–334. Reviewers could read the whole document to resolve definitions and avoid false missing-section findings.
- Local baseline: `b4b3087696e9f58d81bcf3a723431745e6d3901e`, branch `2-add-minimal-persistence-and-contract-conventions`, plus the captured working changes and untracked documentation. A 67-file snapshot and hashes preserve those exact inputs.
- Read-only GitHub metadata confirmed [PR #23](https://github.com/igrgin/ai-knowledge-base/pull/23) merged on 2026-10-29, with merge commit `d5b2efceda70db39bcf6e19fcdef69c9f236f297`. The plan's inspection baseline is historical; refresh it before execution as step 1 already requires. This review did not switch branches or fetch/modify Git state.
- Every reviewer used `fork_turns="none"`, GPT-6.1 Sol/high, and ran sequentially while the parent waited. Each completed with its required persona, target and assigned standard read. Stage 8 alone received prior stage reports as required by the review flow. No same-context simulated reviewer or launch-only success was counted.
- Reviewers used the supplied source reading list and source searches appropriate to their role. Some supporting research/specification reads were truncated in individual reports; these limitations remain recorded. This is a review of the migration plan, not certification that a future rewrite preserves every source line. The implementation preservation matrix is still required.
- No application tests ran. No runtime, test, build or existing documentation file changed. The source-detail check supports documentation-specific validation and does not require restoring the synthetic tests retired by final T2.

The first-run [input manifest](/var/folders/sj/4nd1nyqd51s69mn57s_4bc_h0000gn/T/egm-migration-review-nq34e2gb/manifest.json), [review instructions](/var/folders/sj/4nd1nyqd51s69mn57s_4bc_h0000gn/T/egm-migration-review-nq34e2gb/review-inputs.md), [captured PR metadata](/var/folders/sj/4nd1nyqd51s69mn57s_4bc_h0000gn/T/egm-migration-review-nq34e2gb/pr23.json) and stage reports are retained in task-owned OS temporary storage for resolution and comparison. They are local evidence, not portable CI artifacts. The first run created this consolidated report; the second run updates the same file.

| Stage | Completed reviewer(s) | Assigned standard | Report |
| --- | --- | --- | --- |
| 5 — Initial Zoom Out | `egm_zoom_initial` / `zoom_out.md` | Zoom-out | [Initial report](/var/folders/sj/4nd1nyqd51s69mn57s_4bc_h0000gn/T/egm-migration-review-nq34e2gb/reports/zoom_out_report.md) |
| 6 — Comprehension | `egm_comprehension` / `comprehension.md` | No additional standard | [Complete paraphrase and findings](/var/folders/sj/4nd1nyqd51s69mn57s_4bc_h0000gn/T/egm-migration-review-nq34e2gb/reports/comprehension_report.md) |
| 7 — Critique | `egm_design_critic`, `egm_structure`, `egm_style_design` | Feature-spec for design/structure; style persona | [Stage report and individual reports](/var/folders/sj/4nd1nyqd51s69mn57s_4bc_h0000gn/T/egm-migration-review-nq34e2gb/reports/critic_report.md) |
| 8 — Intermediate Zoom Out | `egm_zoom_intermediate` / `zoom_out.md` | Zoom-out | [Intermediate report](/var/folders/sj/4nd1nyqd51s69mn57s_4bc_h0000gn/T/egm-migration-review-nq34e2gb/reports/intermediate_zoom_out_report.md) |
| 9 — Implementation Readiness | `egm_readiness`, `egm_codebase_details`, `egm_style_execution` | Implementation-readiness for readiness/source details; style persona | [Stage report and individual reports](/var/folders/sj/4nd1nyqd51s69mn57s_4bc_h0000gn/T/egm-migration-review-nq34e2gb/reports/implementation_readiness_report.md) |

## First-run finding traceability

All 13 first-run observations map to the eight consolidated actions below. At that run's conclusion, none had been fixed or accepted as a trade-off; the current resolution status is recorded separately.

| Raw finding(s) | Consolidated action | Disposition |
| --- | --- | --- |
| Z1 (Nit), D1 (Major), IZ1 (Major), CD1 (Major) | A1 | Merge duplicates. Major reflects policy preservation, beyond inventory/navigation alone. |
| C1 (Major), D2 (Nit), IZ2 (Nit) | A2 | Merge duplicates. Retain Major for the wrong default output destination; severity disagreement remains recorded. |
| IR1 (Major) | A3 | Retain as Major. |
| C2 (Nit) | N1 | Retain as Nit. |
| SD1 (Nit) | N2 | Retain as Nit. |
| SD2 (Nit) | N3 | Retain as Nit. |
| SE1 (Nit) | N4 | Retain as Nit. |
| SE2 (Nit) | N5 | Retain clarity request; preserve the broader relationship obligations. |
| Structural reviewer: no findings | No action | Heading hierarchy, formatting and required feature-spec substance passed. Alternatives after the semantic boundary were considered, not falsely reported missing. |

## Second-run evidence and comparison

The owner requested another review and a combined report. The second run used the same full five-stage preset and the same 334-line plan, with the same SHA-256 recorded above. Local HEAD remained `b4b3087696e9f58d81bcf3a723431745e6d3901e`. The only source change since the first snapshot was the addition of section headings in `AGENTS.md`; its substantive routing remained the same. A fresh GitHub metadata read again confirmed the T2 merge.

The [second input manifest](/var/folders/sj/4nd1nyqd51s69mn57s_4bc_h0000gn/T/egm-migration-rereview-_p4vkm0g/manifest.json) records all 68 starting repository files. Reviewers received a 67-file source snapshot excluding the existing consolidated report. The [prior report copy](/var/folders/sj/4nd1nyqd51s69mn57s_4bc_h0000gn/T/egm-migration-rereview-_p4vkm0g/history/prior_consolidated_review_report.md) was preserved separately for parent synthesis and was not supplied to reviewers. The [second-run instructions](/var/folders/sj/4nd1nyqd51s69mn57s_4bc_h0000gn/T/egm-migration-rereview-_p4vkm0g/review-inputs.md) required fresh `fork_turns="none"` contexts, Sol/high, and sequential execution. Stage 8 received only this run's earlier reports. All nine reviewers completed; none failed or was replaced by an in-context review.

| Stage | Completed reviewer(s) | Result and report |
| --- | --- | --- |
| 5 — Initial Zoom Out | `r2_zoom_initial` | [One Major, overlapping A1](/var/folders/sj/4nd1nyqd51s69mn57s_4bc_h0000gn/T/egm-migration-rereview-_p4vkm0g/reports/zoom_out_report.md) |
| 6 — Comprehension | `r2_comprehension` | [No findings; complete paraphrase and reviewer clarification](/var/folders/sj/4nd1nyqd51s69mn57s_4bc_h0000gn/T/egm-migration-rereview-_p4vkm0g/reports/comprehension_report.md) |
| 7 — Critique | `r2_design_critic`, `r2_structure`, `r2_style_design` | [A1 repeated, stale baseline noted, no additional style finding](/var/folders/sj/4nd1nyqd51s69mn57s_4bc_h0000gn/T/egm-migration-rereview-_p4vkm0g/reports/critic_report.md) |
| 8 — Intermediate Zoom Out | `r2_zoom_intermediate` | [A1 and baseline note confirmed; no further high-level finding](/var/folders/sj/4nd1nyqd51s69mn57s_4bc_h0000gn/T/egm-migration-rereview-_p4vkm0g/reports/intermediate_zoom_out_report.md) |
| 9 — Implementation Readiness | `r2_readiness`, `r2_codebase_details`, `r2_style_execution` | [A1/A3 confirmed, two new wording suggestions](/var/folders/sj/4nd1nyqd51s69mn57s_4bc_h0000gn/T/egm-migration-rereview-_p4vkm0g/reports/implementation_readiness_report.md) |

The same personas and standards applied as in the first run. The comprehension reviewer corrected its own compressed paraphrase after checking the explicit sequence: source preservation and coverage checks, candidate deletion, then candidate review/drift before integration. It confirmed that the document itself was clear on that ordering; no extra finding was manufactured from the reporting correction. Its complete corrected response and the clarification are retained in the linked report.

The second run produced ten raw observations. They map to two existing Major actions, the already-recorded baseline refresh, and two new Nit suggestions:

| Second-run raw finding(s) | Combined destination | Disposition |
| --- | --- | --- |
| R2-Z1, R2-S1, R2-IZ1, R2-IR1, R2-CD1 — Major | A1 | Independent confirmation; one preservation/authority correction, not five additional defects. |
| R2-IR2 — Major | A3 | Independent confirmation; original-body capture makes the existing rollback remedy more concrete. |
| R2-D1, R2-IZ2 — Nit | Baseline refresh in first-run scope/evidence | Already noted in the first report and required by plan step 1. Update the plan's “still open” statement to the completed T2 merge. This is not a new migration risk or another unresolved architectural choice. |
| R2-SE1 — Nit | N6 | New wording suggestion. |
| R2-SE2 — Nit | N7 | New wording suggestion; preserve the actual phase ordering. |

At the end of the second run, A2 and N1–N5 remained unresolved. Their absence from that run's finding lists did not fix or withdraw them; no reviewer supplied a substantive rebuttal. All original findings were retained for the subsequent resolution, with their severities unchanged.

Each report lists actual supporting reads and limits. Some supporting-source outputs were partial; required personas, target text and assigned standards were read completely. This second run is not an exhaustive preservation audit of every research note or a review of rewritten files that do not yet exist. No application tests, Git mutations or remote writes were performed. Before combining reports, hashes confirmed that no starting repository file had changed during the review. Only this report is edited by the second run.

## Resolution applied

| Item | Category | Revised plan location |
| --- | --- | --- |
| A1 | PROPOSE FIX — applied | [Remote-policy disposition](../../designs/egm-migration-plan.md#remote-policy-disposition), authority table, full inventory and cutover checks |
| A2 | PROPOSE FIX — applied | [EGM workflow](../../designs/egm-migration-plan.md#egm-workflow-and-agent-entry-point), explicit `target_filename="EGM.md"` |
| A3 | PROPOSE FIX — applied | [Issue cutover](../../designs/egm-migration-plan.md#implementation-sequence) and [recovery after updates](../../designs/egm-migration-plan.md#after-issue-updates) |
| N1 | PROPOSE FIX — applied | [Overview](../../designs/egm-migration-plan.md#overview), selected local distribution and documentation |
| N2 | PROPOSE FIX — applied | [Application design](../../designs/egm-migration-plan.md#application-design), unchanged acceptance/proposal distinctions |
| N3 | PROPOSE FIX — applied | [EGM workflow](../../designs/egm-migration-plan.md#egm-workflow-and-agent-entry-point), direct drift-resolution action |
| N4 | PROPOSE FIX — applied | [Verification](../../designs/egm-migration-plan.md#verification-and-completion-criteria), native-manifest comparison |
| N5 | PROPOSE FIX — applied | [Issue cutover](../../designs/egm-migration-plan.md#implementation-sequence), named mapping/dependency/branch/PR checks |
| N6 | PROPOSE FIX — applied | [Candidate review](../../designs/egm-migration-plan.md#implementation-sequence), requirement-change wording |
| N7 | PROPOSE FIX — applied | [Recovery](../../designs/egm-migration-plan.md#alternatives-and-recovery), explicit deletion and integration phases |
| B1 — existing baseline follow-up | PROPOSE FIX — applied | [Starting point](../../designs/egm-migration-plan.md#starting-point), final T2 merge and separate local bootstrap state |

No finding was contested, escalated or declined. The residual-policy disposition retains current remote rules and applicable inherited conventions, while retiring the old fallback and superseded plugin machinery in the proposed future migration. It does not change active policy in this turn.

## Fresh review of the revised plan

The owner explicitly selected full consolidated mode and requested only an update to this report. All five stages completed against the 417-line plan, SHA-256 `38c0c32fd84b813004bd1b414954a23606cc79b6274bb4f87cbdb1b21747d44e`. The semantic boundary is line 282: FeatureRange 1–281, DetailedRange 282–417. The [starting manifest](/var/folders/sj/4nd1nyqd51s69mn57s_4bc_h0000gn/T/egm-revised-review-wyv50zkz/manifest.json) records the working inputs at HEAD `b4b3087696e9f58d81bcf3a723431745e6d3901e`.

Reviewers used a frozen source snapshot. Previous review and resolution documents were preserved separately under `history/` and excluded from that snapshot. All nine reviewers used `fork_turns="none"`, GPT-6.1 Sol/high and sequential execution at concurrency 1. Only Stage 8 received this run's earlier stage reports. No failed or incomplete reviewer was counted as clean. The [reading instructions](/var/folders/sj/4nd1nyqd51s69mn57s_4bc_h0000gn/T/egm-revised-review-wyv50zkz/inputs.md) and reports retain each role's actual reads and limits.

| Stage | Completed reviewer(s) | Assigned standard | Result and evidence |
| --- | --- | --- | --- |
| 5 — Initial Zoom Out | `r3_zoom_initial` | Zoom-out | [No findings](/var/folders/sj/4nd1nyqd51s69mn57s_4bc_h0000gn/T/egm-revised-review-wyv50zkz/reports/zoom_out_report.md) |
| 6 — Comprehension | `r3_comprehension` | Persona; no extra standard | [No findings; complete paraphrase](/var/folders/sj/4nd1nyqd51s69mn57s_4bc_h0000gn/T/egm-revised-review-wyv50zkz/reports/comprehension_report.md) |
| 7 — Critique | `r3_design`, `r3_structure`, `r3_style_design` | Feature-spec for design/structure; style persona | [Design/structure clear; two style Nits](/var/folders/sj/4nd1nyqd51s69mn57s_4bc_h0000gn/T/egm-revised-review-wyv50zkz/reports/critic_report.md) |
| 8 — Intermediate Zoom Out | `r3_zoom_intermediate` | Zoom-out | [No architectural findings](/var/folders/sj/4nd1nyqd51s69mn57s_4bc_h0000gn/T/egm-revised-review-wyv50zkz/reports/intermediate_zoom_out_report.md) |
| 9 — Implementation Readiness | `r3_readiness`, `r3_codebase`, `r3_style_execution` | Implementation-readiness for readiness/source details; style persona | [All three report no findings](/var/folders/sj/4nd1nyqd51s69mn57s_4bc_h0000gn/T/egm-revised-review-wyv50zkz/reports/implementation_readiness_report.md) |

### Wording findings from the revised-plan review — addressed

| ID | Severity | Location | Finding and suggested wording |
| --- | --- | --- | --- |
| N8 | Nit | Revised plan line 112, authority table | “An imitation of a missing EGM build/release skill” uses judgmental wording for an authority boundary. Suggested replacement: “Copied remote policy or implementation/release tooling.” The tooling boundary is already explained at lines 230–236. |
| N9 | Nit | Revised plan lines 192–194, quality gate | “18/20 grounded supported answers” compresses the distinction between supported questions and answers judged correct and grounded. Suggested wording: “Preserve the quality gate: at least 18 of 20 supported-question answers judged correct and grounded, appropriate abstentions for all 10 unsupported questions, and citations resolving to retrieved passages and the correct physical pages.” This clarifies the existing specification's gate without changing its values. |

Traceability: raw R3-SD1 maps to N8, and R3-SD2 maps to N9. No duplicates or additional findings came from the other eight reviewers. Both were initially left open and subsequently applied on the owner's instruction. N8 now uses “Copied remote policy or implementation/release tooling.” N9 now explicitly requires at least 18 of 20 supported-question answers judged correct and grounded, appropriate abstentions for all 10 unsupported questions, and citations resolving to retrieved passages and correct physical pages. These are the source specification's existing thresholds and citation requirements; no product criterion changed. Both findings retain Nit severity and are addressed without an accepted trade-off.

### Disposition of earlier findings

The earlier A1–A3 and N1–N7 retain their original severities and applied status. The fresh review found no corresponding residual issue: remote authority and all 15 source files are mapped; the bootstrap route explicitly selects `EGM.md`; issue bodies and associations are captured for coordinated recovery; the selected distribution and direct workflow/check wording are documented. The final T2 baseline is identified. The new wording Nits are additional suggestions, not severity changes to earlier findings.

The comprehension reviewer reconstructed the proposed migration, current authority, separate repository/remote outcomes and recovery without a comprehension finding. The readiness reviewer found no consequential migration decision implicitly delegated to the executor. These conclusions concern the plan, not a demonstration that migration or rollback has been executed.

Limits: this is static review of the frozen plan and referenced sources. Required personas, target and assigned standards were read fully; supporting-source depth varies by role and is recorded. Some broad outputs required bounded rereads. Exploratory unreferenced standard/template paths were missing for two reviewers, who located/read the actual required resources; no required input was missing. Several technical reviewers read all nine research notes and the real native manifests/infrastructure, but the run is not a line-by-line proof of a future rewrite's preservation. Live PR/issue/protection state and branch contents were not independently refreshed; the plan's execution-time checks remain necessary. No application tests, builds, Git mutations, remote writes or migration operations ran.

Only this combined report is updated by this review. The revised plan and existing resolution record remain unchanged. Local report links/anchors, whitespace and the final file diff were checked; source hashes confirmed that the review inputs stayed unchanged.

## Next step

No unresolved review findings remain recorded. The full fresh review found no Blocker or Major issue, and its two wording suggestions are now applied. Local checks cover the two edits and report synchronization; no additional full review was run for these wording-only changes. Migration implementation still requires a separate instruction.

At cutover, preserve this pre-migration review with the task/PR evidence rather than presenting it as a review of the migrated application design or copying its retired-workflow references into active policy.
