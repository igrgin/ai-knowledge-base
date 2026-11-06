Design-Critic-Brainstormer report — BRAINSTORM, egm-design lens

Read attestation

Reviewed EGM with small fixes 0.2.1:
- `/Users/igrgin/.codex/plugins/cache/egm-local/egm/0.2.1/agents/design_critic.md`
- `/Users/igrgin/.codex/plugins/cache/egm-local/egm/0.2.1/resources/document_standards/zoom-out-standard.md`
- `/Users/igrgin/.codex/plugins/cache/egm-local/egm/0.2.1/resources/document_standards/heilmeier-standard.md`

Reviewed required repository sources:
- `AGENTS.md`
- `docs/egm-workflow.md`
- `docs/remote-work.md`
- `docs/designs/t3-identity/concepts_brief.md`
- `docs/designs/t3-identity/authentication-decisions.md`
- `docs/roadmap.md`
- `docs/reviews/t3-identity/brainstorm-round-2/inputs.md`
- `docs/reviews/t3-identity/issue-drafts/3.md`
- `docs/contracts/README.md`
- `docs/contracts/k0-foundation.md`
- `EGM.md`
- `docs/designs/application.md`: overview, R1/R2/R16/R18/R23, D-A, feature design checkpoints, and T3/T12/T16/T17/T19 scope paragraphs.

Verified actual implementation/reuse using `rg`, source inventory and direct reads of the backend POM/configuration, `ApiError`, `RequestIdFilter`, PostgreSQL test fixture, V1 migration and React readiness screen.

Actual HEAD, checked twice: `77e774661f350ec5a95dd3d9f25e25bf03333d7e`, matching dispatch. No required read failed; truncated combined output was followed by narrower reads. No edits, tests, external research, parent-history retrieval or additional agents. The excluded research document was not read.

Verdict

The architectural direction remains sound. Google authentication, durable application identity, persistent sessions and application-owned permissions fit the existing modular Spring/PostgreSQL direction. Reusing the framework session store and K0 is preferable to introducing another identity service, permission engine or duplicate session registry.

There is meaningful conceptual progress, but no T3 implementation progress. The supplied incoming snapshot records 36 commits and 40 Markdown files. At the dispatched HEAD, I independently verified that the diff from the recorded main baseline contains 41 changed files, all Markdown. Source still contains the readiness shell and foundation only; no identity/session/security implementation or domain schema exists.

The original issue described registration, login/logout and session persistence. The accepted amendments substantially enlarge that into an identity and authorization administration slice: automatic Google account creation, development admin, individual permissions, session management, bootstrap safeguards and configurable audit history. These are explicit owner choices, not unexplained speculative additions. Their acceptance does not authorize implementation; the local issue replacement remains unpublished, the bounded feature design/review remains incomplete, and the roadmap grants research/brainstorm authority only.

We have gone far enough in adjacent product exploration. The latest sharing-mode confirmation clarifies future consumers, but further recipient-selection, copy-history or sharing-UI exploration should no longer delay the T3 design.

Consequential architectural risks

1. **Severity:** Major  
   **Dimension:** Cost, Quota & Token Economics  
   **Risk Description:** Session persistence, current permission checks, qualifying-activity updates and enabled audit writes all compete for the same database. Actual common configuration allows four connections per instance with a two-second acquisition timeout. The brief correctly avoids claiming that the one-minute local activity interval limits all session writes, but no workload evidence establishes the combined cost. Audit retention duration alone does not bound event volume. This is a known drafting risk, not evidence that a larger database or new service is required.  
   **Mitigation Requirement:** Carry the existing bounded-state, pagination, cleanup and indexed-lookup recommendations into the design. Keep transactions short, check audit flags before constructing optional events, and measure representative protected requests plus enabled auditing. Do not introduce caching, an event bus or a separate audit service without measured need.

2. **Severity:** Major  
   **Dimension:** Cost, Quota & Token Economics  
   **Risk Description:** Explicit production disablement is accepted, while development auto-login is expressly prohibited whenever `prod` is active. The intake leaves the actor for a request without a valid Google session in non-development disabled mode unresolved. That choice affects whether the configured mode actually serves requests, which account bears quota/audit attribution, and whether the administrator banner has an authenticated audience. It is a consequential identity boundary, not a missing schema detail.  
   **Mitigation Requirement:** Resolve this one mode/actor behavior before the design is accepted. Preserve production override permission, development-fixture exclusion, stored Google-session continuity and normal authorization. Do not silently reuse the development administrator outside development or infer an account from browser input. No broader local-user edition is needed.

T3 versus later work

T3 should provide the account/principal boundary, Google flow, development fixture and mode behavior, persistent sessions, permission assignment/evaluation, initial administrator command, last-manager protection, session controls, audit storage/settings/inspection boundary, and trusted permission/configuration metadata. Its concrete design must settle the identified races and required persistence/module choices.

Resource slices should introduce their actual permissions and ownership enforcement with their features. T6 owns durable jobs and unfinished-upload staging; T11/T15 own operational restore integration; T12 and feature owners integrate real draft/view preservation and app-wide UI. T16/T19 own recipient selection/proof, sharing links, fixed capture and complete project-copy implementation. T17 remains withdrawn. These later implementations are neither T3 prerequisites nor authorized by this review.

Smallest concrete next step

Use this checkpoint to close broad brainstorming: present one bounded brief-alignment disposition preserving accepted choices, deciding the proposed permission mechanism and resolving non-development disabled-mode identity. Then seek the scoped drafting authority required by the roadmap and produce the T3 design through `egm-draft`. Do not start another round of sharing-product questions or begin implementation before the accepted design, formal review and separate implementation authorization.
