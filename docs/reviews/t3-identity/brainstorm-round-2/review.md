# T3 scope and progress checkpoint

**Date:** 2026-11-06, Europe/Zagreb. **Mode:** BRAINSTORM. **Package:** EGM with small fixes 0.2.1; root verified its manifests, and the synthesizer read the installed brainstorm skill, Codex integration and synthesizer persona. **Target:** [concepts brief](../../../designs/t3-identity/concepts_brief.md), as committed at `77e774661f350ec5a95dd3d9f25e25bf03333d7e`.

This report answers the [owner's scope/progress question](inputs.md). It is neither formal design review nor implementation review, and grants no drafting, coding, gate-publication or remote-write authority.

## Answer to the owner

**Right track:** yes, the identity direction fits the modular Spring/PostgreSQL application. Google proves identity; the application owns accounts, permissions and sessions; resource owners retain ownership and business admission. Existing framework and K0 boundaries offer a bounded path.

**Implementation progress:** none yet for T3 runtime behavior. There is meaningful research and decision progress. At this checkpoint, `git rev-list --count e85f9dea6a58b4b10df8e5c67a75b10bc0573729..HEAD` returns **37 commits**; `git diff --stat e85f9de...HEAD` reports **41 changed files, all Markdown**. Source inventory and `rg` confirm foundation-only behavior: readiness UI, shared safe MVC errors/request correlation, JDBC/Flyway wiring and PostgreSQL test plumbing. There is no concrete account principal, Google/security/session integration, permission engine, audit storage or authentication configuration. The incoming **36 commits/40 files** in inputs.md describe the earlier `ded655e…` HEAD; the added checkpoint commit explains the difference.

**Too far:** the original login/logout/restart milestone has expanded substantially. Development automatic identity, individual permissions, user/admin session controls, bootstrap safeguards and configurable auditing are explicit owner amendments, so reviewers do not delete them as accidental scope creep. Adjacent sharing exploration has now supplied enough identity context. Continuing into recipient UX, copy capture, history mapping, upload staging or recovery machinery would delay T3 without advancing its immediate boundary. Stop broad product exploration and converge on one bounded T3 design.

**Recommended next action:** align/freeze this amended brief and establish scoped drafting authority, then use `egm-draft` for T3. Settle the remaining mode/actor behavior and disposition the permission-storage proposal within that bounded handoff. Formal design review and separate implementation authority still precede code. No additional general product questionnaire is recommended.

## Reviewer completion evidence

Native `collaboration.spawn_agent` dispatches used **`model="gpt-6.1-sol"`, `reasoning_effort="high"`, `fork_turns="none"`**, with role titles ending `-Brainstormer`. The six native final reports were received by the synthesizer in the order below; each child actually completed before the next dispatch. No child wrote files. The synthesizer waited, then alone preserved reports and edited the brief. The repository's one-active-task rule overrides the package's parallel default.

| Lens | Native child task | Native final result | Raw findings | Evidence |
| --- | --- | --- | --- | --- |
| Design | `/root/t3_scope_goldfish_synth/design_brainstormer` | completed | 2 Major | [Verbatim report](raw-design.md) |
| Codebase details | `/root/t3_scope_goldfish_synth/codebase_details_brainstormer` | completed | 1 Major | [Verbatim report](raw-codebase-details.md) |
| Zoom out | `/root/t3_scope_goldfish_synth/zoom_out_brainstormer` | completed | 2 Major, 1 Nit | [Verbatim report](raw-zoom-out.md) |
| Comprehension | `/root/t3_scope_goldfish_synth/comprehension_brainstormer` | completed | 1 Major, 1 Nit | [Verbatim report](raw-comprehension.md) |
| Implementation readiness | `/root/t3_scope_goldfish_synth/implementation_readiness_brainstormer` | completed | 2 Major | [Verbatim report](raw-implementation-readiness.md) |
| Structural | `/root/t3_scope_goldfish_synth/structural_brainstormer` | completed | 2 Major | [Verbatim report](raw-structural.md) |

**Counts:** 6 completed; 0 not run; 0 retries. **12 raw findings: 0 Blocker, 10 Major, 2 Nit.** Normalized below into **6 findings: 0 Blocker, 5 Major, 1 Nit**. Major means consequential drafting obligation here, not a discovered runtime defect. Zero Blockers does not certify a formal design or implementation as ready.

Completion provenance is the synthesizer's actual native `FINAL_ANSWER` receipts and the verbatim reports, not launch acknowledgements. The root's later live inventory exposed only the last two completed child cards plus the synthesizer; the earlier four were no longer returned. Root therefore did not directly observe all six retained native cards. Live-inventory retention limits independent reinspection, while the synthesizer observed all six completions before writing these artifacts.

Every child attested reading its exact native persona, both supplied standards (`zoom-out-standard.md` and `heilmeier-standard.md`), all common sources and the selected application-design passages, and verified the exact dispatched HEAD. Each verified current implementation/reuse with `rg` and relevant source reads. No required-source failure remains. Design recovered truncated output through narrower reads. Implementation-readiness and structural initially searched a nonexistent optional `dependency-bom` path, then located/read `bom/pom.xml`; these recovered attempts remain in their raw reports.

## Normalized findings and proposed dispositions

### F1 — Define one mode-to-effective-account contract — Major

**Lenses/raw mapping:** design 2; zoom out 2; comprehension 1; implementation readiness 1; structural 2. **Affected scope:** intake, “Accepted authentication-mode behavior”; brief mode/session policies.

Production disablement is permitted, but the development administrator must never be provisioned/selected when `prod` is active. Requests without a valid session in non-development disabled mode still have no selected actor. That leaves entry behavior, quota/audit attribution and the banner audience unresolved. Keeping Google authentication, effective development identity, development provisioning and normal bootstrap separate also needs one explicit transition contract.

**Proposed disposition:** settle existing-valid-session and no-valid-session outcomes for each supported mode/profile before design acceptance. Preserve dormant Google sessions and their running deadlines without overwriting/renewing them through development activity. Keep mixed-profile exclusion, no private draft transfer or automatic write replay, ordinary authorization and agreement across serving instances. Do not silently ban production disablement or invent a production development-admin fallback. This is the clearest remaining consequential behavior choice, not grounds for a new local-user product.

### F2 — Freeze T3's boundary and keep dependencies directional — Major

**Lenses/raw mapping:** zoom out 1; structural 1. **Affected scope:** brief analysis/staging; application feature checkpoints and T3/T6/T11/T12/T16/T19 scope.

Identity must not become a dependency hub importing project/chat/job/sharing internals. Requiring complete future feature designs now can make T3 brainstorming endless.

**Proposed disposition:** T3 exposes trusted account identity, current action-permission evaluation, session/configuration metadata and bounded audit hooks. Resource owners retain ownership, operation admission and lifecycle rules. Agree narrow consumer obligations without building their tables, jobs, sharing pipeline or universal event system. Stop additional sharing-product exploration. This respects existing task ownership; any actual removal or reassignment of an accepted T3 deliverable remains an owner proposal, not this report's decision.

### F3 — Bound combined database work and reuse the framework — Major

**Lenses/raw mapping:** design 1; zoom out 3 (Nit combined with the resource finding). **Affected scope:** brief reuse/resource discussion; `backend/src/main/resources/application.yml`; K0 transaction conventions.

Session persistence, current permissions, activity and enabled audit writes share a pool currently configured to **4 connections per instance**, with **2000 ms** acquisition timeout. One-minute local interaction reporting does not cap all session/database writes; retention days do not bound event volume. No workload measurement supports either a large or negligible overhead claim.

**Proposed disposition:** use short transactions, bounded login state/attributes/listings, indexed lookups, pagination and bounded cleanup; check audit flags before enrichment. Measure representative protected requests with enabled auditing. Start with Spring Security/Spring Session JDBC and K0/Flyway/PostgreSQL fixtures; verify session-list/revoke support before adding another registry. No caching, event bus, audit service or pool increase follows from this finding. Framework JDBC sessions do not settle the separately required domain JDBC/JPA/Spring Data JDBC comparison.

### F4 — Add one finite diagnostic acceptance example — Major

**Lens/raw mapping:** codebase details 1. **Affected scope:** brief verification staging; intake Google failure/audit policies; K0 security-filter boundary.

Current request correlation identifies individual HTTP attempts; MVC advice does not yet handle security-filter failures. Operators need sanitized evidence distinguishing provider/protocol failure, idle/maximum expiry, pre-write sign-in, revocation, permission denial and database failure even with action history disabled.

**Proposed disposition:** demonstrate in the eventual T3 checks that an operator can identify login stage/category, correlate relevant requests for a failed attempt and explain a protected-request rejection. Reuse K0 shape/correlation and preserve non-disclosing ownership responses. Never log OAuth state/codes/tokens/session credentials or reconstruct disabled action history in logs. No external tracing system or separate research round is required.

### F5 — Preserve removed authority through evolution and restore — Major

**Lens/raw mapping:** implementation readiness 2. **Affected scope:** intake basic-permission evolution/bootstrap; brief recovery obligation.

Permission replacement/default activation, administrator provisioning or old backups could restore removed permissions or sessions. Retained audit history cannot be the sole bootstrap-completion record.

**Proposed disposition:** retain explicit revocations through stable permission identities and deliberate replacement migrations; bound supported deployment overlap. Store create-once bootstrap/development evidence independently of optional history. State the identity-specific non-serving restore/reconciliation obligation for T11/T15. The shared-default/per-account-exception mechanism and recovery mechanism remain proposals; full recovery machinery stays with its later owners.

### F6 — Complete the initial-manager operator sequence — Nit

**Lens/raw mapping:** comprehension 2. **Affected scope:** intake accepted initial administrator setup.

The selected one-time command takes an existing application account UUID, so the supported workflow must expose that account's UUID without ad hoc database inspection.

**Proposed disposition:** draft one sequence covering normal sign-in, supported own-UUID discovery, trusted-host command invocation and actionable success/already-completed/failure output. Keep normal bootstrap separate from development provisioning. Exact command syntax is drafting work.

## Recommended bounded handoff

| Keep in T3's accepted scope | Agree now; implement with later owners |
| --- | --- |
| Google account mapping/automatic creation, concrete principal and security flow | Actual project/PDF/chat features and their resource ownership checks |
| Durable sessions, accepted activity/deadline rules, user/admin revocation | T6 unfinished-transfer staging and durable-job engine; T5 spending integration |
| Development database admin, mode isolation and trusted configuration metadata | T12/feature view/draft preservation, banner/history composition and permission presentation |
| Current individual permissions, bootstrap command and final-manager invariant | Actual feature permissions/audit hooks as their slices arrive |
| Audit settings/storage/inspection boundary and safe operational diagnostics | T11/T15 operational restore machinery; T13 integrated multi-instance/fault evidence |
| Narrow principal/permission/admission/audit consumer contracts | T16/T19 recipient selection/proof, invitation/general-link acceptance, fixed capture and complete-copy mapping |

Both accepted project-sharing access modes and mandatory full-project contents remain selected. T17 stays withdrawn. No new issue or feature removal is recommended. The framework/session/module/persistence choices, races, exact files, migrations, shared edits and checks belong in the bounded design, rather than another brainstorm or a premature file-list audit of this brief.

A useful proposed sequence inside that design is: (1) real account/security/session continuity and development-mode behavior; (2) current permissions/bootstrap/manager safeguards; (3) session administration and audit inspection with consumer interfaces. These are **drafting recommendations, not accepted implementation phases or partial-completion criteria**. Accepted T3 scope remains incomplete until all its selected deliverables and actual checks pass. Splitting/removing/deferring accepted T3 controls requires a separate owner choice.

## Authority, evidence and limits

The [original issue snapshot](inputs.md#remote-issue-3-snapshot) describes the earlier login milestone. [Owner intake](../../../designs/t3-identity/authentication-decisions.md) records accepted amendments and proposals. The [local replacement issue body](../issue-drafts/3.md) remains unpublished; this review did not refresh or change remote issue state. The [roadmap](../../../roadmap.md) owns current authority/dependencies/ownership. Only K0 is available in the [contract index](../../../contracts/README.md); no gate is published here.

The synthesizer read all common sources, selected requirements/D-A/checkpoints/task passages, repository guidance, installed brainstorm/Codex/synthesizer instructions and both standards. It independently verified HEAD, the 37-commit/41-Markdown comparison, source inventory and the configured database pool. No application tests, fault/load measurements, Google setup/sign-in, browser flow, external research or formal design/implementation audit ran. No runtime correctness/performance/readiness claim follows.

Native tool dispatch establishes fresh conversation context. It does **not** establish filesystem, configured-memory-service or host instruction-file isolation; no separate child-context exclusion probe ran. Reviewers were instructed not to retrieve parent history/memory, and each attested compliance. Shared files remained accessible, although reviewers were read-only and all saw the committed target before synthesis. This report does not claim stronger isolation than the available evidence supports.

The excluded untracked `docs/research/langchain-architecture-assessment.md` was not inspected or changed. Historical round-1/raw evidence and round-2 inputs were preserved. The synthesizer edited only the six new raw reports, this report and the concepts brief. Shared roadmap/intake/index reconciliation remains with the root integrator. No commits, pushes, issue/comments, deployment or implementation were performed.
