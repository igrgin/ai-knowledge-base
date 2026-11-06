**Codebase-Details-Brainstormer — BRAINSTORM, EGM 0.2.1**

**Read attestation**

Actual HEAD verified twice: `77e774661f350ec5a95dd3d9f25e25bf03333d7e`, matching dispatch.

Read:
- Persona: `/Users/igrgin/.codex/plugins/cache/egm-local/egm/0.2.1/agents/codebase_details.md`.
- Standards: the same package’s `resources/document_standards/zoom-out-standard.md` and `heilmeier-standard.md`.
- Repository guidance: `AGENTS.md`, `docs/egm-workflow.md`, `docs/remote-work.md`.
- Target and accepted decisions: `docs/designs/t3-identity/concepts_brief.md`, `docs/designs/t3-identity/authentication-decisions.md`.
- Authority and evidence: `docs/roadmap.md`, `docs/reviews/t3-identity/brainstorm-round-2/inputs.md`, `docs/reviews/t3-identity/issue-drafts/3.md`.
- Contracts and architecture: `docs/contracts/README.md`, `docs/contracts/k0-foundation.md`, `EGM.md`.
- Selected application-design material: R1/R2/R16/R18/R23, D-A, feature design checkpoints, and T3/T12/T16/T17/T19 scope paragraphs.

Verified implementation/reuse through `rg`, source inventory, and direct reads of backend dependencies/configuration, `RequestIdFilter`, `ApiExceptionHandler`, the shared-web package contract, `PostgresIntegrationTest`, and the React readiness shell. No required-source read failure remains. No excluded research, parent chats or memory accessed; no edits, tests or web research performed.

**Verdict: direction is sound; progress is conceptual, with a clear need to stop expanding intake.**

The original issue covered registration, login/logout, persistent sessions, CSRF and trusted identity. Accepted amendments materially enlarge T3 to development automatic identity, individual permissions, session administration, bootstrap safeguards and configurable audit history. These additions have owner support; their size alone does not establish unauthorized scope drift.

There is no T3 runtime implementation. Comparing the fixed HEAD with the recorded main baseline shows 41 changed files, all Markdown; a filtered diff finds no Java, frontend implementation, SQL, YAML or build/dependency changes. Source inspection confirms only foundation behavior: a readiness UI, shared errors/correlation, database wiring and PostgreSQL test plumbing. Spring Security/OIDC/session integration and identity schemas are absent.

The brief accurately acknowledges this. Research and decision reconciliation are useful progress toward implementation, but cannot be reported as implemented authentication. The unpublished issue replacement is also neither published task scope nor implementation authority. Only K0 is available; future gates remain planned.

The conversation has gone far enough into future features to establish identity/permission seams. Continuing to resolve sharing mechanics, future action-event catalogs or worker internals during T3 brainstorming would now have diminishing value.

**Consequential finding — one carried design requirement**

- **Severity:** Major — bounded-design obligation, not a current implementation defect.
- **Dimension:** Observability & Operational SRE Debuggability.
- **Affected scope:** Concepts brief, “Other review findings” and verification staging; intake, Google failure handling and audit policy.
- **Operational Risk:** The brief recognizes safe login correlation and reason categories, but does not yet state an operational acceptance example covering a multi-request login attempt or the different reasons access can end. Existing `RequestIdFilter` correlation identifies an individual HTTP request; existing MVC advice supplies no authentication-filter handling. During an incident, an operator must distinguish provider/protocol failure, idle expiry, maximum-age expiry, pre-write reauthentication, session revocation, permission denial and database failure. Configurable audit capture cannot be the sole diagnostic source because either category may be disabled.
- **Mitigation Requirement:** Carry one finite diagnostic acceptance criterion into drafting: demonstrate that an operator can identify the stage/category and correlate relevant application requests for a failed sign-in, and explain a protected-request rejection using sanitized evidence. Reuse K0 correlation and error shape; define any login-attempt linkage without logging OAuth state, codes, tokens or session credentials. Preserve non-disclosing ownership responses. Keep minimal failure diagnostics separate from optional action history, without recreating disabled audit history in logs. This requires no external tracing platform or additional brainstorming round.

**T3 versus later**

T3 owns identity/session/permission boundaries, its own administrative controls, audit storage/configuration/inspection API, safe diagnostics and consumer-hook agreements. Reuse the foundation rather than creating another error/correlation system or speculative infrastructure.

T4 and later slices own their resources, permission activation and actual audit hooks. T6 owns durable jobs and unfinished-upload behavior. T16/T19 own invitations, link acceptance, capture and complete-copy mechanics. T12 owns cross-feature presentation; T13 extends integrated and multi-process evidence. T17 is withdrawn.

**Smallest concrete next step**

Present the existing brief for explicit alignment with a firm T3 boundary, then request authority for the bounded drafting step. Draft buildable T3 phases and finite operational acceptance alongside the required persistence/module/security decisions. Do not reopen accepted policies or design future features to completion. Formal design review and separate implementation authorization remain required before coding.
