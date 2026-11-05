# Substantive architecture and task reassessment — issue #24

## Result and evidence boundary

Date: **2026-11-05**. Review input: `452cd66b9a93932603c0ad660309ce13332d36c5`.

**Keep the principal architecture; change the decision records, feature-design gates and several future feasibility checkpoints.** There are **11 consolidated Major actions and 3 Nit actions**, all documentation/backlog actions or requirements for later bounded feature designs. No reviewer found a current T1/T2 implementation Blocker. This is not an implementation-ready T3 design and does not authorize T3 or later implementation.

Nine required fresh Sol/high contexts completed sequentially through all five EGM stages. An additional context-assisted comprehension report is preserved as supplemental evidence after the installed package changed mid-run. Actual IDs, reads, version transition and statuses are in [the execution ledger](reassessment-execution.md). Complete stage reports, the full comprehension paraphrases and source intake remain in the task-owned OS artifact directory recorded there. This paragraph describes the read-only review pipeline; the integrator dispositions below record subsequent documentation repairs.

The original review input was the pre-repair filesystem at baseline `452cd66b9a93932603c0ad660309ce13332d36c5`. Its semantic boundary was `application.md:326`: FeatureRange 1–325 and DetailedRange 326–567. The hashes below identify that original input, not the repaired candidate:

| File | SHA-256 |
| --- | --- |
| docs/designs/application.md | 789e55063802eccd9a0dcc6e736d8247dd5814a34ffc8efa4c01102c0ca96834 |
| docs/roadmap.md | ee5b7350244fe0926d584b50ea58b5e593977c35475deb79a0bd517e7399be8b |
| docs/contracts/README.md | c2264c7ffc6a54a8d37eeca80bf9c7aa436edb1486e3537fe9caff92dd3c8b8c |
| docs/egm-workflow.md | bdc16eb379b2ca293c0a1e9f3267c2227d5dd19f6626caa05e89de4e5234ead6 |

## Consolidated actions

### Documentation corrections authorized in this reassessment

**C01 — Major — Make feature design a universal implementation prerequisite.** `docs/roadmap.md:11,21,232`; `docs/egm-workflow.md:24–30`; `docs/designs/application.md:326,390–406,428–468`. State that task authority, accepted/reviewed bounded feature design, available provider gates and integrated completion prerequisites answer different questions. Label the implementation-area table as an architectural ownership inventory, not a write allowlist. The selected feature's handoff must resolve consequential choices and include exact repository-relative Create/Modify/Delete paths, migrations/interfaces, allowed shared changes, incremental phases and meaningful checks. Do not create speculative designs or complete future schemas now. The broad table is useful at application altitude; its open-ended language needs the later boundary, not replacement with invented files.

**C02 — Major — Repair decision status and local provenance.** `application.md:5,33,92,102,119,125,145–155,207,388,428,436`. Replace blanket acceptance of D-A–D-L with scoped status. Provide a compact local mapping of accepted product constraint, accepted architecture direction, current implemented convention, proposed mechanism and deferred choice, with source/date/supersession where known. D1–D12 and D19–D21 references and ordinal quotations cannot be the only way to interpret operative obligations. Do not invent owner acceptance. In particular, JDBC across all tables, Spring AI, exact principal naming and speculative future message/citation extension shapes need local status/deciding checkpoints. K0 deliberately lets T3 choose its concrete principal. Keep KH minimal until a real answer consumer needs extension.

**C03 — Major — Supply the missing persistence and model-adapter alternatives.** `application.md:143,153–155,207–211,320–324,388`; `k0-foundation.md:42–48`. Retain JDBC as a reasoned current recommendation: explicit ownership/vector queries, locks, claims and money transactions dominate difficult work; existing foundation supports it and no domain repositories require migration. Compare JPA CRUD plus native SQL honestly: it reduces some CRUD mapping but introduces entity lifecycle and mixed-write complexity while leaving hard SQL protocols. Do not claim JPA is incompatible. Retain owned embedding/generation ports and SQL retrieval independently of adapter choice. Spring AI is a candidate; compare a direct provider client and, where a concrete capability warrants it, LangChain4j. T5 must check structured response, storage/reasoning options, billed usage, token counts, timeout and hidden retry behavior. Every billable attempt must remain visible to financial admission. No new framework/dependency is needed merely to populate an alternatives table.

**C04 — Major — Separate provider checkpoint evidence from full-task completion.** `roadmap.md:11,21,133–138`; `contracts/README.md:5–12`; `application.md:430`. KP explicitly precedes UI polish, but roadmap rule 6 ties checkpoint updates to UI/API/database/test completion without distinguishing the checkpoint's own evidence. Define agreed version/schema assumptions, usable provider development interface, first-consumer checks and recorded availability as checkpoint criteria. Full UI and real integrated demonstrations still govern task completion. An unagreed draft or app-mode stub cannot make a gate available.

**C05 — Major — Give T7's ready knowledge an owned consumer checkpoint.** `application.md:440,446,450`; `roadmap.md:33,36,38,117,122–124`; `contracts/README.md:13–20`. KD describes extraction/page/job handoff and KU financial/model ports; neither publishes T7's complete generation, chunk/page identity, vector compatibility and visibility boundary consumed by T9 and T16. Add a bounded T7-owned checkpoint (for example KE), or explicitly extend an existing gate with T7 ownership and agreement, and update affected starts/ownership. Do not add a task or speculative vector schema. T7 remains an integrated completion prerequisite. The preservation-era gate count is not a reason to omit a necessary boundary.

**C06 — Major — Make archive-cancelled uploads explicitly retryable through the normal bounded path.** `application.md:167,346,442`. Archive produces a distinct cancelled state and unarchive promises ordinary retry, while retry eligibility only names failed ingestion. Align the journey/interface wording: an active incomplete upload cancelled by archive can use explicit user-requested retry under current versions, admission and reservations. Unarchive alone must not initiate paid work. T6/T20 will decide/test the exact shared transition. This corrects a stranded-state ambiguity without changing archive-only retention.

### Bounded future feature-design checkpoints to record now

The next five actions require adding explicit design obligations and tests to the backlog/gate descriptions now. They do **not** call for designing or implementing all their protocols in this application document.

**C07 — Major — Jointly design dispatch, recovery and deadline clocks.** `application.md:165,211,217–219,289–293,432–446,468,484–486`; `contracts/README.md:13–14,19`. T5 financial authority, T6 jobs and T11 lifecycle remain distinct owners, but must agree one claim/reserve/dispatch/unknown/settle/finalize protocol, lock order, fencing, cancellation and available reconciliation evidence before charged consumers. Include worker loss before/after dispatch, alternate-instance duplicate submission, delayed stale completion and unresolvable provider outcomes. Distinguish conservative funds from evidence that remote execution ended; lease expiry proves neither. A sole embedding slot cannot depend on undefined reconciliation forever. Specify an owner-operated disposition when automated evidence cannot resolve a call. Define whether queue wait, retry, restart downtime and admission wait consume whole-job/answer deadlines. Preserve read-only library and permitted deletion operations during exhausted or uncertain paid capacity.

**C08 — Major — Prove independently retained lifecycle-journal crash consistency before KL.** `application.md:193–195,263,444,480–486`; `contracts/README.md:19`. PostgreSQL commit and filesystem append are not atomic together. T11 design must choose durability/ordering/acknowledgement, interrupted-operation and journal-unavailability behavior, and restore replay before traffic. Compare journal-first durable intent with a genuinely independent durable-log/reconciliation alternative. Keep missing/inconsistent journal restores non-serving. No speculative journal schema is required now.

**C09 — Major — Design a directional module graph and exposed shared-web interface.** `application.md:123–125,163–175,384,393,428–434`; `roadmap.md:113–126`; `shared/web/ApiError.java:6`; `shared/web/RequestIdFilter.java:28`; `spring-modulith-investigation.md:9–13`. Task ownership areas are not automatically Modulith modules. Document/upload, ingestion, archive, chat sources, jobs and lifecycle have a prospective cycle risk; no existing illegal dependency was found. T3/T4 must expose the intended shared error/correlation API through an allowed named interface or facade before boundary enforcement. Later owners must choose orchestration/public ports and dependency direction before their gates. Keep structural verification; do not introduce the Modulith event registry as a second scheduler.

**C10 — Major — Bound history-dependent work and prove fixed-copy/snapshot semantics.** `application.md:173–175,249–265,285–292,450–454,466,525`; `roadmap.md:120,124–126`; `contracts/README.md:20`. Ten PDFs and six prompt messages do not bound retained chats, transcript bytes, manifests, snapshots or archive-grant fanout under a project lock. T20 should compare materialized grants with a serialized chat-order/archive boundary; retain the simpler correct treatment of cycles/imports. T16/T17/T19 must choose bounded capture/copy, source-side retention/storage accounting, pinning/materialization, cleanup and import-versus-revocation commit rules. A share must retain its creation-time history, source eligibility and processing state; later completion cannot be substituted. Immutable references plus captured boundaries may suffice; copying every payload immediately is not automatically required. Define the fixed visible treatment of saved in-flight turns without copying executable work or replaying paid calls. Test interrupted capture/copy/publication, later source changes/completion, destination-only IDs/citations and independence after source deletion. Do not silently truncate “all chats.” New user-facing quotas require owner agreement.

**C11 — Major — Design the actual parser distribution, not just its dependency.** `application.md:382,396,434`; `pdf-extractor/pom.xml:21–40`. The current dependency-free executable probe JAR does not prove a parser with PDFBox can run under `java -jar`. T6 design must select dependency-inclusive packaging or explicit distribution/classpath, backend artifact discovery, bounded versioned output, stderr/exit handling, termination and co-distribution. Require exact POM and packaged-launch-test changes then. Reuse the existing child boundary and package test; absence of parsing now is expected.

### Documentation Nits

**C12 — Nit — Remove duplication while preserving concrete limits.** `application.md:125,145–149,265,330–338,422–468,513,519–540`. Keep architecture/invariants and concise resulting obligations here. Keep task execution/status in roadmap, implementation conventions in K0/local-development, approval narrative in local provenance evidence, source-selection rule in D-D and detailed experiment definition in one checkpoint table. Drop the rhetorical provider-marketing sentence at `:207`. Preserve coverage loss for existing-volume adoption, provider retention, uncertainty, restore non-serving behavior, excluded-source context and offline/real-provider distinctions: each changes a reachable decision. No forced heading/template rewrite.

**C13 — Nit — Calibrate stale metadata and repair retained-source pointers.** `application.md:416,490`; `frontend/package.json:7`; `docs/research/maven-frontend-test-integration.md:3`; `docs/research/caching-evaluation.md:5`; `docs/research/scalability-evaluation.md:5`. Date the historical Node 22.12 observation or remove it in favor of the selected 24.22.1 runtime. Caption the capacity table as accepted bounds, proposals and experiment settings rather than all accepted limits. The Maven/frontend note's present-tense “PR #23 remains unmerged” conflicts with roadmap's merged T2 record; preserve original baseline as dated context and point current status to roadmap. The coordinator also alleged that the cache/scalability notes target a nonexistent `#design-decisions` anchor. The integrator contests that portion: `### Design decisions` exists in the reviewed input and retained target, so these links need no repair. This observation is not attributed to a Goldfish.

**C14 — Nit — Reconcile recorded installed EGM version.** `docs/egm-workflow.md:7` records 0.2.0; cache changed to 0.2.1 during this review. Update only after the integrator verifies actual package/capabilities. Do not copy procedures into repository policy. Changes include document-only Stage 6, local/external source intake, style caveat calibration and exact-file handoff standard; ledger records actual versions used. This is an environment observation, not a request to install or update a plugin.

## Architecture and task assessment

| Decision/boundary | KEEP / CHANGE / DEFER | Independently reasoned rationale and alternative |
| --- | --- | --- |
| Java/Spring + React/shadcn, PostgreSQL/pgvector | KEEP accepted stack | Meets owner learning/local requirements. One SQL authority keeps privacy/source/lifecycle/accounting decisions transactional. Provider document/vector stores add synchronization and deletion duties. |
| Modular application; combined/web/worker roles | KEEP | Required multi-process durability excludes process-local queues/limits. Business microservices would distribute transactions without established independent operation/release need. |
| Current bom/backend/pdf-extractor reactor | KEEP | Real artifact boundaries exist. Feature Maven modules add build/dependency surfaces without a concrete consumer. |
| Bounded extractor child | KEEP; C11 later | Killable parser isolation avoids private-file transfer and remote-service operation. Remote extraction is a measured/authorized expansion, not required now. |
| Modulith | KEEP bounded; C09 | Useful structural enforcement once modules/interfaces exist. Domain task names are not the module graph; no need for event registry or speculative module rollout. |
| JDBC versus JPA | KEEP JDBC recommendation; CHANGE status/rationale | Explicit SQL suits the hard invariants; ordinary CRUD could use JPA. No existing domain repository migration determines the decision. Record choice before affected implementation rather than treating inheritance as proof. |
| Model adapter | KEEP owned narrow ports; DEFER final implementation to T5 | Spring AI, direct client and another framework are implementation alternatives. Required options/accounting/retry visibility decide; retrieval ownership does not force one framework. |
| T3 identity / T4 projects | KEEP distinct | Durable login/session behavior is independently useful; ownership/project rules build on it. Avoid a speculative broad domain foundation. |
| T5 financial admission / T6 execution | KEEP owners; C07 joint seam | Money/charged-attempt authority differs from runnable claims and leases. One agreed dispatch/retry engine avoids competing state machines. Merging tasks/services is not necessary to solve their interaction. |
| T6 extraction / T7 indexing | KEEP; C05/C11 | Accepted originals/page text and restartable indexing are distinct durable stages with different failure/cost boundaries. One typed job mechanism is sufficient. |
| T8 history / T9 answering | KEEP | History has no embedding dependency and is independently demonstrable. T9 extends it without a second store/provider engine. |
| T18 reader / T10 citations | KEEP reader first | Original viewing is useful without AI. Citation navigation should extend that reader rather than create another source subsystem. |
| T9 and T10 delivery | KEEP task IDs; CHANGE milestone clarity if needed | One readiness reviewer proposes folding T10 into T9; others find the small extension legitimate. R10 is spanning and roadmap already says all mapped slices must pass. Recommend one grounded-answer/page-navigation delivery milestone and explicit no-full-R10-completion until both; retain existing issue IDs unless deliberate consolidation improves selected work. No renumbering solely for churn. |
| T20 source eligibility | KEEP owner; C06/C10 | One active-or-granted-minus-exclusions rule avoids divergent access predicates. Reconsider concrete grant representation at design; accepted archive semantics remain. |
| T11 before completed answers/sharing | KEEP; C08 | Real delete commands are needed to test stale completion races. Later features extend actual purge/replay; early T11 cannot prove deletion of nonexistent future tables. |
| T16 files copy / T19 copied chat extension | KEEP one pipeline, staged features | Files-only import is useful independently. Extend immutable capture/mapping without a generic plugin framework. Full R26 chat option remains incomplete until T19. |
| T17 publication versus copying | KEEP separate | A revocable source-bound transcript and an independently owned accepted project have different lifetime/access semantics. One generic sharing entity complicates both. |
| T12 integration / T13 offline proof / T14 real AI / T15 handoff | KEEP | Features own their UI/tests; integration adds combined evidence. Offline correctness/scale needs no key; actual quality/paid evaluation requires separate authority; handoff claims must match evidence. |
| Exact filtered retrieval, persisted embedding reuse, no cache | KEEP | Meets source/ownership requirements with minimal runtime services; compare exact recall before an approximate index. Batching/reuse need measurement, not Redis. |
| Hosting, multi-host storage/journal, caches, higher concurrency | DEFER | None is required for the same-machine flow. Adopt for measured needs and authorized operational/budget scope. |

## Unresolved owner product/operating decisions

The corrections above can be recorded now without asking the owner to design future protocols. Preserve accepted R1–R29. Any later proposal to auto-restart paid work on unarchive, truncate copied “all chats,” impose new visible chat/share quotas, alter privacy/lifecycle semantics, or remove accepted sharing/scaling requirements requires owner disposition. Backup expiry/retention, proposed AI/storage allocations, hosted/email scope, increased concurrency and paid calls retain their existing owner/operation checkpoints. Technical adapter/schema/protocol decisions belong in the selected bounded feature design; they are not all owner product questions.

## Retained-file purpose inventory

Retention is based on purpose, not the authoring workflow. No reviewed file warrants deletion merely because it came from the previous process. The following inventory distinguishes current authority from evidence and avoids making research a second plan.

| File/group | Assessment and purpose |
| --- | --- |
| docs/designs/application.md | KEEP, correct C01–C13: accepted behavior, target architecture, alternatives and open concerns; not current task state or feature write allowlist. |
| docs/roadmap.md | KEEP, correct C01/C04/C05: task authority, ownership, starts/completion, progress and integration rules. |
| docs/contracts/README.md | KEEP, correct C04/C05 and required checkpoint content: availability/owner/consumer index, not speculative full contracts. |
| docs/contracts/k0-foundation.md | KEEP: actual foundation conventions and consumer obligations, linked to implemented maps. Do not replace it with a future-domain schema. |
| EGM.md and shared/shared.web EGM.md | KEEP: current implemented architecture and canonical links; change with actual implementation, not proposed future features. |
| docs/egm-workflow.md | KEEP, C01/C14: skill routing, actual protocol selection, sequential fresh review, roles/assignments and handoff. It complements installed skill procedure rather than duplicating it. |
| docs/remote-work.md | KEEP: native issue/branch/PR/CI/merge authority and operations; EGM does not replace repository remote policy. |
| docs/local-development.md | KEEP: executable startup, profiles, packaging and meaningful native test commands. Runtime/build sources remain the implementation authority. |
| docs/designs/SITEMAP.md | KEEP: discover real design records; register selected feature designs, avoid empty speculative entries. |
| docs/research/microservices-investigation.md | KEEP as dated alternatives evidence for one app versus autonomous services; no measured scale claim. |
| docs/research/extraction-scaling-investigation.md | KEEP: distinguishes replica capacity from accepted one-parser concurrency and child process versus service. |
| docs/research/spring-modulith-investigation.md | KEEP: domain module/interface semantics and event-registry alternatives; useful for C09. |
| docs/research/maven-multi-module-investigation.md | KEEP: build-boundary alternatives and test selection evidence, not requirement for feature modules. |
| docs/research/maven-frontend-test-integration.md | KEEP, C13: why/how native frontend tests attach to Maven, with dated original verification; current merge status belongs in roadmap. |
| docs/research/java-spring-test-scope-review.md | KEEP as explicitly superseded baseline analysis; current test obligations are K0/roadmap. Do not revive removed synthetic tests by citing old recommendations. |
| docs/research/costs.md | KEEP: dated rates/scenarios and owner budget/local-vs-hosted direction; not price refresh or spending authority. |
| docs/research/scalability-evaluation.md | KEEP, pointer verified; no repair needed: larger coordination/corpus experiment alternatives and measured-evidence boundary. |
| docs/research/caching-evaluation.md | KEEP, pointer verified; no repair needed: specific cache/reuse risks and no-cache rationale. Some overlap with scale note is useful at different scope; no new cache dependency follows. |
| docs/reviews/egm-migration/review.md and linked drift report | KEEP historical evidence: earlier preservation audit scope/candidate/check limits; not architecture proof. Add new reassessment evidence separately rather than rewrite historical clean result to imply broader work. |
| README.md / AGENTS.md | KEEP user entry point and repository routing; link current authority without mirroring full plans. |
| docs/research/langchain-architecture-assessment.md | EXCLUDED: separate untracked work item; neither read nor assessed, no retention/deletion judgment. |

## What the earlier review did not establish

`docs/reviews/egm-migration/review.md:31` lists paths/anchors, requirement/task/gate coverage, source inventories, obsolete-reference search and whitespace, with runtime/build/test sources unchanged from merged T2 and application tests not rerun. At `:33` it expressly says: “Review assessed preservation and current implemented claims, not implementation readiness of every future feature.” Its cited nine contexts and clean disposition therefore do not independently prove inherited architecture/task choices optimal, future KU/KD/KL feasibility, model compatibility, real-provider quality, parser packaging, future feature schemas or hosted capacity. This new review reopens architectural merit while keeping accepted requirements and actual foundation evidence as inputs.

The coordinator left the historical file-count observation unverified. The integrator subsequently checked the merged T2 tree and confirmed fourteen retired governance paths, all absent; see File removal and retained content below.

## Raw-to-consolidated traceability

| Raw finding(s) | Consolidated action |
| --- | --- |
| ZO-01; S6-C2 supplemental; S7D-07; S7S-02; S8Z-01; S9R-01; S9C-01 | C01 |
| ZO-02 status; S6-C3 supplemental status; S7S-01; S7Y-01, S7Y-02; S8Z-02 status; S9R-04 | C02 |
| ZO-02 alternatives; S6-C3 supplemental alternatives; S7D-01, S7D-02; S8Z-02 alternatives | C03 |
| S9R-02 | C04 |
| S9R-03 | C05 |
| ZO-03; S6-C1 supplemental; S6D-01; S8Z-03 | C06 |
| S7D-04; S8Z-04; S9R-05; S6-C5 supplemental | C07 |
| S7D-06; S8Z-06 | C08 |
| S7D-03; S9C-02 | C09 |
| S7D-05; S8Z-05; S9R-06; S6-C4 supplemental | C10 |
| S9C-03 | C11 |
| ZO-04; S7Y-03, S7Y-04, S7Y-05; S8Z-07; S9Y-01, S9Y-03 | C12 |
| S9Y-02, S9Y-04; coordinator verified research status/anchor observations | C13 |
| Coordinator verified installed-package transition versus workflow:7 | C14 |
| S9R task-reassessment proposal to fold T10; other reviewers' contrary keep assessments | Task table: joint milestone, keep IDs unless deliberately consolidated; not promoted to a defect |
| S9Y assessment that planned area table needs no exact future files now | Retained as calibration of C01: exact files at selected feature design gate, not application rewrite |

## Completion and limits

All requested roles completed actual fresh native contexts with `fork_turns="none"`; no concurrent reviewers or writers ran. Conversation isolation does not prove host instruction-file exclusion, and none is claimed. Stage 5 used the installed 0.2.0 intake; later stages used re-resolved 0.2.1. Official Stage 6 was rerun target-only under the new protocol; contextual output is supplemental, not a substitute. The initial nonexistent `extractor/pom.xml` was a coordinator path typo corrected to actual `pdf-extractor/pom.xml`; Stage 5 had already read the actual POM and BOM. Required local inputs were read; planned nonexistent outputs are not missing current evidence. External/history sources were explicitly omitted, no claims depend on newly verified external content, and the separate untracked assessment was not read.

This is static documentation/architecture review. It establishes neither a fresh build/test pass nor provider compatibility, paid quality, measured throughput or hosted readiness. The read-only review pipeline applied no repairs. Subsequent authorized integrator changes and their verification are recorded below; future feature protocols remain pending design, not implemented by this migration.


## Integrator dispositions

The owner instructed this PR to re-evaluate inherited architecture/task choices and push warranted changes, while preserving existing work and leaving T3 unstarted. That standing instruction authorizes these documentation repairs; it accepts no implementation of later features. `egm-resolve` was applied with full-file targets and C01–C14 as input. The precise edit plan is retained as `goldfish_review_report.md` in the task artifact directory.

| Action | Resolver category | Disposition in the revised documents |
| --- | --- | --- |
| C01 | PROPOSE FIX | Workflow, AGENTS, root map and roadmap require bounded accepted/reviewed feature design, exact Create/Modify/Delete paths and meaningful checks before implementation. Application areas are ownership inventory. |
| C02 | PROPOSE FIX | Added local decision register and provenance below; corrected blanket acceptance, principal naming and premature KH message/citation extensions. |
| C03 | PROPOSE FIX | Added concrete architecture alternatives. JDBC is provisional; the separately requested comparison precedes first domain implementation. Model adapter selection belongs to T5. No benchmark superiority or JPA incompatibility is claimed. |
| C04 | PROPOSE FIX | Contract index and roadmap distinguish agreed usable checkpoint/consumer evidence from full task UI/API/database completion. |
| C05 | PROPOSE FIX | Added planned T7-owned KE, updated T9/T16 starts and ownership/consumer text. There are now eleven gate entries; K0 alone is available. No KE implementation/schema is invented. |
| C06 | PROPOSE FIX | D-C, retry interface and T20 specify explicit retry for active incomplete archive-cancelled uploads; unarchive alone performs no paid work. Exact transition/tests remain a T6/T20 design obligation. |
| C07 | PROPOSE FIX | Added joint T5/T6 design obligation covering lifecycle fences, uncertainty, reconciliation/operator disposition and deadline clocks; reflected at KU. Tasks retain separate ownership. |
| C08 | PROPOSE FIX | Added T11 crash-consistent SQL/independent-journal design and interruption evidence before KL. Restore remains non-serving on missing/inconsistent journal. |
| C09 | PROPOSE FIX | Added directional module/public-interface checkpoint including shared web, and retained structural verification without another job runtime. |
| C10 | PROPOSE FIX | Added T20 grant representation/fanout comparison and T16/T17/T19 bounded fixed-capture, storage/retention, in-flight-turn and commit-race design obligations. Product limits/history are not silently changed. |
| C11 | PROPOSE FIX | Added real dependency-inclusive parser-distribution and packaged-child verification prerequisite to T6 design. |
| C12 | PROPOSE FIX | Moved amendment history to provenance, removed duplicated benchmark/execution prose, retained concrete privacy/coverage/failure caveats and distinct source/sharing verification cases. |
| C13 | PROPOSE FIX in part; CONTEST in part | Dated the old Node observation, calibrated capacity caption and corrected the Maven note's present-tense unmerged claim. Kept valid `#design-decisions` links, with checkable heading evidence. |
| C14 | PROPOSE FIX | Verified installed 0.2.1 and egm-fix; workflow records the observation and requires version/capability resolution per invocation. No plugin installation performed. |

The T9/T10 merge suggestion is an alternative, not a defect: retain stable issue/task IDs and one delivery milestone because T18 already owns the reusable reader and R10 spans T9/T10. No task is renumbered, dispatched or marked complete. The extra KE checkpoint is the warranted dependency change. Future protocol choices listed above are explicitly pending at their owning design gates; recording them is not proof they work.

## Decision provenance

This local record makes scoped decisions interpretable without retrieving a private conversation. It summarizes evidence already present in the preserved candidate and dated research; it does not infer full-document owner approval. The original [planning conversation](codex://threads/01a1699d-af41-71f0-a5e7-c75152955b39), [preserved inputs](https://github.com/igrgin/ai-knowledge-base/commit/deb7838) and merged [T1](https://github.com/igrgin/ai-knowledge-base/pull/21)/[T2](https://github.com/igrgin/ai-knowledge-base/pull/23) remain historical evidence.

| Source/date | Scoped meaning and supersession |
| --- | --- |
| Baseline owner requirements before T1; carried into R1–R29 | Java/React learning project, private text PDFs and grounded cited chats; local readiness first, optional hosting and $15–20 target; accepted starting quotas/English-first quality gate. Later explicit product amendments below supersede conflicting early proposals. |
| Recorded archive/copy/publication replies in the preserved design, before merged T2 | Archive-only individual PDFs supersedes earlier individual-delete proposals. Existing-chat archive eligibility, retrieval-only exclusions with retained bounded prior context, retained-original viewing, fixed independent project copies with optional all chats, and revocable transcript-only snapshots are product constraints. They do not accept every proposed table/DTO/capture protocol. Historical question numbers are no longer needed to interpret them. |
| 2026-10-24 agent-assignment direction | Development-agent roles/effort and single-writer operation; unrelated to application model selection. Workflow owns the current assignments. |
| 2026-10-26 architecture assessment acceptance | Owner: “I agree with number four. I agree with your assessment for number three and number two.” The preserved design identifies these as Modulith verification, modular application/independent workers and deferred dedicated extraction service. Retain the three Maven modules; selected-module and test-level execution was the stated Maven purpose. Exact module graph/interfaces and parser packaging were not accepted by that sentence. |
| T1 configuration/test amendments and merged PR #21 | Base/dev/prod configuration and separate test/itest resources; native build/test tooling. Owner requested removal of ProductionConfig, DeploymentConfigurationTest and ReadinessIT and a shared Java BOM. Backend configuration/readiness/lifecycle coverage was removed rather than replaced equivalently; runtime readiness/profile behavior remains. Current commands live in local development. |
| 2026-10-26 T2 build, identity/test and error amendments; merged PR #23 | Owner accepted backend-local Maven frontend lifecycle integration; deferred concrete principal to T3; ordinary integration uses SpringBootTest; typed application errors with explicit safe messages/status mapping replace status-derived codes. K0 owns current details. This was bounded T2 authority, not T3 authority. |
| 2026-10-27 test-scope research; 2026-10-29 feature-focused reduction | Later owner-approved reduction supersedes earlier synthetic migration/adoption/restart/failure and general HTTP smoke keep recommendations. Final T2 retains custom errors/correlation, restrictive-Accept real HTTP regression, packaged extractor and browser checks. Special pre-Flyway volume adoption loses dedicated automation; future fresh-schema tests do not implicitly cover it. |
| Original issue #24 and 2026-10-29 cutover review | Preserved requirements, decisions/task/gate inventory and current-code claims. The earlier review expressly excluded readiness of every future feature; it is not substantive proof that inherited architecture choices are optimal. |
| 2026-11-05 current owner instruction | Extends this same draft PR to substantive EGM architecture/task reassessment and warranted documentation changes. Core architecture is retained with explicit alternatives, decision status and design checkpoints. T3, behavior changes, merge and paid calls remain outside authority. |

JDBC across all domain tables, Spring AI as the final client, exact future principal naming and every proposed grant/capture/journal mechanism have no separate owner acceptance established by those records. They remain recommendations or decisions for bounded design. The requested persistence research has not been completed by this review; the capability citations added to Alternatives are limited primary-source checks, not that independent comparison.

## File removal and retained content

The merged T2 baseline contains fourteen files under the retired governance directory: nine research notes, one old drift report, three scope records and one settings record. All fourteen old paths are absent from the active tree. Git history and preservation tags intentionally retain recovery evidence; this is not history erasure. Earlier narrative counts of fifteen mixed a different inventory with this baseline and should not be used as a deletion check.

| Original purpose | EGM disposition |
| --- | --- |
| Intent and product specification | Consolidated into application requirements, current decision status/alternatives and named future design gates; old scope paths removed. |
| Task plan/progress and coordination | Rewritten as roadmap, contract index and EGM workflow; old plan/settings paths removed. Reassessment changes supersede preservation-only assumptions. |
| Nine research notes | Retained as dated evidence under docs/research, indexed by purpose/adoption status. Historical Maven status corrected. They grant neither task nor architecture authority. |
| Old drift/review evidence | Current EGM reviews live here; original evidence remains in preserved history. Earlier scoped drift covers actual maps/K0, not future architecture readiness. |
| K0 and existing product README/local development | Retained because they describe implemented conventions, demonstrated behavior and real commands. K0 now exposes the persistence decision boundary; these documents are governed by EGM, not treated as a second workflow. |
| Application/build/test files | Preserved by behavior and usefulness, irrespective of authoring tool. Deleting working Java/React/build files merely because a prior tool helped author them would violate preservation. No runtime/dependency/test change is part of this amendment. |

## Revised candidate verification

Fresh report-only technical reviewer `/root/reassessment_candidate_review` read all thirteen amendment candidate files and actual diff against `452cd66`, verified their SHA-256 identities in the task artifact manifest, and checked each C01–C14 disposition. No Blocker or Major defect. Three Nits were corrected: T16’s secondary scheduling summary now includes KP/KE; original-review hashes/ranges are explicitly pre-repair; valid research anchors are recorded as verified rather than repaired. The integrator checked those exact edits afterward. This focused check is not another full five-stage review or an implementation-ready feature review.

Static verification: all 29 R rows byte-for-byte unchanged; 20 task IDs, 18 pending; eleven gates with K0 alone available; all fourteen old governance paths absent; only Markdown changes against merged T2. All application/build/test sources remain unchanged. The separate untracked assessment retains its initial SHA-256 and is excluded from the commit. Local Markdown path/anchor checks and whitespace pass; the final count is recorded in task evidence. No application build, runtime test, benchmark, provider compatibility or real-model evaluation was rerun for this documentation amendment. Earlier native Tests CI is evidence for its earlier head; a new push triggers the existing suite.

This amendment corrects the governance/backlog findings and assigns the identified future design work. It does not establish the pending protocols’ correctness. T3–T20 remain unstarted; no merge, provider call, deployment, new schema or dependency is authorized.
