Implementation-Readiness-Brainstormer report — BRAINSTORM, EGM with small fixes 0.2.1.

Read attestation: actual HEAD was verified before and after inspection as `77e774661f350ec5a95dd3d9f25e25bf03333d7e`, matching dispatch. Read:

- Persona: `/Users/igrgin/.codex/plugins/cache/egm-local/egm/0.2.1/agents/implementation_readiness.md`.
- Standards: the same package’s `resources/document_standards/zoom-out-standard.md` and `heilmeier-standard.md`.
- Repository guidance: `AGENTS.md`, `docs/egm-workflow.md`, `docs/remote-work.md`.
- Target and decisions: `docs/designs/t3-identity/concepts_brief.md`, `docs/designs/t3-identity/authentication-decisions.md`.
- Scope/evidence: `docs/roadmap.md`, `docs/reviews/t3-identity/brainstorm-round-2/inputs.md`, `docs/reviews/t3-identity/issue-drafts/3.md`.
- Boundaries: `docs/contracts/README.md`, `docs/contracts/k0-foundation.md`, `EGM.md`.
- Application design: R1/R2/R16/R18/R23, D-A, decision status, feature-design checkpoints, and T3/T12/T16/T17/T19 scope paragraphs in `docs/designs/application.md`.

Verified current implementation/reuse through `rg`, source inventory, backend POM/configuration/V1, React entry point, shared package maps, `ApiError`, `RequestIdFilter`, and `PostgresIntegrationTest`. No required source read failed. An optional search initially named nonexistent `dependency-bom/pom.xml`; the correct module was identified as `bom/pom.xml` and checked. No edits, tests, web research, parent history retrieval or excluded research-file inspection occurred. Fresh conversation context was used; shared-filesystem/instruction exclusion remains unverified.

The direction is sound, but progress is conceptual rather than implementation progress. The original issue snapshot sought registration/login/logout and persistent sessions, with security, migrations and two-account verification. Google-only login replaces its older authentication proposal. Development auto-login, permission administration, session controls and configurable auditing are explicit owner amendments; they should not be dismissed as accidental scope creep.

The work has nevertheless travelled beyond the original login milestone into wider product clarification. The newly settled sharing modes are useful consumer constraints, but further sharing capture, invitation or history-copy design is unnecessary to advance T3. The committed brief correctly places those implementations later.

I independently checked the diff from the supplied merged baseline `e85f9de…` to current HEAD: every changed path is Markdown. Current code remains the readiness shell, shared errors/correlation, database plumbing and V1 extension migration. There is no implemented account, permission, security/session integration, audit storage or authentication configuration. K0 remains the only available gate. The local issue replacement is unpublished, the brief is not an accepted bounded design, and the roadmap grants research/brainstorming authority rather than implementation authority.

There is no legacy application-account or password schema to convert. T3’s initial persistence work should therefore be additive over the foundation, retaining V1 and existing data. The consequential compatibility risks begin once T3 persists identity and authority:

1. **Severity: Major. Dimension: Migration & Backward Compatibility.**  
   **Compatibility Hazard:** changing authentication mode must preserve dormant Google sessions while automatically selecting a different development actor. Reusing or overwriting one stored security context could resume the development identity as Google authentication, renew the wrong session, or transfer private client state. Non-development disabled-mode identity also remains explicitly unresolved. Evidence: intake “Accepted authentication-mode behavior”; brief “Other review findings.”  
   **Migration Requirement:** the bounded T3 design must specify the reversible actor/session transition and agreement across serving instances, including mixed `dev`/`prod` exclusion, unchanged Google deadlines, same-account private-state restoration and no automatic write replay. Settle the non-development disabled-mode identity without reversing the accepted production override. These are already identified design inputs, not evidence of an existing runtime defect.

2. **Severity: Major. Dimension: Migration & Backward Compatibility.**  
   **Compatibility Hazard:** permission-default evolution, create-once administrator provisioning and database restore can unintentionally restore removed authority. A newly basic or replacement permission can bypass an earlier revocation; restart or an older restored database can replay initial grants or revive revoked sessions. Evidence: intake “Basic-permission evolution” and initial setup/mode sections; brief’s recovery paragraph.  
   **Migration Requirement:** preserve explicit revocations and stable permission identity through catalog changes; define safe activation against deployed feature support and any supported version overlap. Bootstrap/development grants need durable create-once evidence independent of retained audit history. T3 must also state the identity-specific non-serving restore/reconciliation obligation for T11/T15; implementing their complete recovery machinery is later work. The shared-default/exception mechanism and restore safeguards remain proposals requiring disposition, not accepted implementations.

T3 should contain trusted account identity, atomic Google account creation, persistent sessions and their controls, configuration/development identity, permission administration/enforcement, initial-manager safeguards, the selected audit storage/inspection boundary, and narrow consumer interfaces. Reuse the existing safe error envelope, correlation filter, PostgreSQL fixture and migration conventions. Evaluate framework session storage before adding a parallel registry; JDBC sessions do not settle the separate required domain-persistence comparison.

Later slices retain project/PDF/chat functionality, actual durable-job admission implementations and audit hooks, project invitations/link acceptance and complete-copy mapping, view/draft integration, the app-wide banner/history composition, and integrated restore/multi-instance verification. T17 remains withdrawn. T3 may agree those interfaces without constructing their schemas or engines early.

The smallest concrete next step is to conclude this checkpoint, align and freeze the amended T3 brief, and move into one bounded T3 design under scoped design authority. Resolve the identity/mode and authority-evolution risks there alongside the existing persistence/module checkpoints, then complete formal design review before separately authorized implementation. Another broad product-question round or an exact-file-list audit of this concepts brief would not advance the immediate milestone.
