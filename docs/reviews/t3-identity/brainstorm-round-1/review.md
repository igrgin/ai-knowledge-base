# T3 Goldfish brainstorm review, round 1

**Mode:** BRAINSTORM; report-only concept feedback. **Date:** 2026-11-05, Europe/Zagreb. **Plugin:** EGM with small fixes 0.2.1.

**Target:** `docs/designs/t3-identity/authentication-decisions.md` (82 lines). **Fixed baseline:** `bb26f2571b6d6787d86fa29ed1f4f8a55a5dfda4`, branch `3-register-log-in-and-retain-authenticated-sessions`. **Issue:** [#3](https://github.com/igrgin/ai-knowledge-base/issues/3).

The owner confirmed “Review with Goldfishes” before dispatch, as supplied in the root handoff. The reviewed intake contained a pre-review status sentence; its current status has since been updated to link this completed round. Findings and line references below concern the fixed baseline above. The [concepts brief](../../../designs/t3-identity/concepts_brief.md) is a synthesis awaiting owner alignment. Neither artifact accepts recommendations, completes formal design review, authorizes implementation, publishes a contract gate or reviews an implementation diff.

## Reviewer status and read evidence

Six general-purpose native children were dispatched with `fork_turns="none"`, `model="gpt-6.1-sol"`, `reasoning_effort="high"`, their exact installed persona and BRAINSTORM lens. The tool returned the canonical native task identities below; it did not expose separate UUIDs. Each actual final report was received before the next dispatch. No failed run, retry or incomplete required-read report occurred.

| Order / persona | Native child identity | Status / raw final response |
| --- | --- | --- |
| 1, `egm-design` / `design_critic.md` | `/root/t3_brainstorm_synthesizer/design_brainstorm` | completed; [raw-design.md](raw-design.md) |
| 2, `egm-codebase-details` / `codebase_details.md` | `/root/t3_brainstorm_synthesizer/operations_brainstorm` | completed; [raw-codebase-details.md](raw-codebase-details.md) |
| 3, `egm-zoom-out` / `zoom_out.md` | `/root/t3_brainstorm_synthesizer/simplicity_brainstorm` | completed; [raw-zoom-out.md](raw-zoom-out.md) |
| 4, `egm-comprehension` / `comprehension.md` | `/root/t3_brainstorm_synthesizer/ux_brainstorm` | completed; [raw-comprehension.md](raw-comprehension.md) |
| 5, `egm-implementation-readiness` / `implementation_readiness.md` | `/root/t3_brainstorm_synthesizer/rollout_brainstorm` | completed; [raw-implementation-readiness.md](raw-implementation-readiness.md) |
| 6, `egm-structural` / `structural.md` | `/root/t3_brainstorm_synthesizer/maintenance_brainstorm` | completed; [raw-structural.md](raw-structural.md) |

Every child attested full reads of its persona, target, all five supporting sources and both standards; none reported a missing read. Required absolute repository prefix was `/Users/igrgin/Documents/code/apps/ai-knowledge-base/`; files were target above, `docs/designs/application.md` (618 lines), `docs/roadmap.md` (253), `docs/contracts/k0-foundation.md` (70), `EGM.md` (70), and `docs/research/google-sign-in-investigation.md` (133). Persona prefix was `/Users/igrgin/.codex/plugins/cache/egm-local/egm/0.2.1/agents/`; exact filenames/frontmatter were verified before dispatch. Full standards were `resources/document_standards/zoom-out-standard.md` and `heilmeier-standard.md` under that installed plugin root.

Raw reports preserve reviewer wording, read commands and bounded recovery reads after truncated output; the integrator normalized trailing Markdown line-break spaces without changing words. Typical evidence was `cat`, numbered `nl -ba` reads and complete `sed -n` chunks, plus bounded `rg` searches of actual code/configuration. Each child independently verified baseline/branch. These are child attestations backed by native completion messages, not a transcript-level audit of every read. All were instructed to exclude conversation/memory/prior reports, recovery dumps and the unrelated untracked LangChain assessment, remain read-only, and run no builds/servers/subagents.

Repository single-task sequencing overrode plugin parallel defaults. No earlier findings were supplied to later children. `fork_turns="none"` establishes native parent-turn isolation; instruction-file exclusion was not separately probed and remains unverified. Shared-filesystem isolation is not claimed. Standards served as concept heuristics, not a final-design template checklist. Sequential fresh reviewers preserve independent prompts, though this is not a claim of experimentally measured bias elimination.

## Normalized findings and dispositions

There were **19 raw observations: 16 Major, 3 Nit, 0 Blocker**. Deduplication yields **9 Major and 2 Nit** concept items below. Severity marks drafting importance, not an implementation readiness verdict. The catalog item combines a Zoom Out Nit with two independently reported Majors; its consequential upgrade behavior warrants Major. All recommendations are **carried into the brief, pending owner/design disposition**; no software mitigation has been implemented or tested.

### M1 — Define qualifying session activity

**Sources:** design 1, codebase-details 1, zoom-out 1, comprehension 1, structural 2. **Evidence:** intake 22–24; application 322; current shell `frontend/src/main.tsx:7–15` has no established polling policy.

**Risk:** automatic polling may continually extend idle age until the eight-hour absolute maximum. One raw report says “indefinitely”; normalization retains the accepted absolute limit. Passive reading and multiple tabs create the opposing UX tradeoff. **Recommendation:** choose request inactivity or qualifying user activity, centralize server enforcement, specify polling/automatic refresh and test the chosen consequences. Do not assume a new heartbeat service. **Owner:** define inactivity; durations remain accepted.

### M2 — Separate revocation boundaries and accepted work

**Sources:** design 2, zoom-out 2, structural 3. **Evidence:** intake 24–26, 77; application 165–169, 189, 468; K0 50.

**Risk:** session-long permission snapshots preserve removed authority; treating logout as distributed job cancellation expands scope and conflicts with accepted durable work. **Recommendation:** propose current shared authority at subsequent authorization checks; define concurrent requests/logins and all-session coverage; agree admission/dispatch/publication effects with future owners. Workers use stable account identity, independently of browser sessions. Dispatched/uncertain paid calls retain settlement. **Owner:** effect on queued/running work and later login; proposal is not accepted.

### M3 — Bound authentication resource use

**Source:** design 3. **Evidence:** intake 20–24; application 319; `backend/src/main/resources/application.yml:7`; primary [Spring JDBC-session documentation](https://docs.spring.io/spring-session/reference/configuration/jdbc.html).

**Risk:** login state, session persistence/listing and administration can consume the small shared pool before PDF/AI quotas apply. **Recommendation:** finite login bursts/state lifetimes, bounded list work, small attributes, indexed lookup, cleanup and measured database work with competing instances. No Redis/throughput claim follows. **Owner:** any simultaneous-session cap or forced replacement policy; technical bounds are drafting work.

### M4 — Define basic-permission evolution

**Sources:** zoom-out 3 (Nit), implementation-readiness 1, structural 1. **Evidence:** intake 26–41, 57; application 165.

**Risk:** existing users miss later basic actions, or repeat seeding restores deliberate restrictions. **Recommendation:** finite stable action catalog, feature ownership and an explicit existing-account grant/migration policy that preserves intentional revocations. Avoid a generic policy engine or speculative feature administration. **Owner:** do new basics extend to existing users, and do changed defaults affect assignments? Private-sharing defaults remain unaccepted.

### M5 — Audit later administrative mutations

**Source:** codebase-details 2. **Evidence:** intake 16, 24, 26, 52–63.

**Risk:** bootstrap evidence alone cannot explain later grants/revocations or committed outcomes. **Recommendation:** minimal durable actor/target/action/delta/time/outcome/correlation evidence for committed privilege mutations and admin session revocations, with safe bounded evidence for sensitive failures; no credentials/private content. **Owner:** retention, inspection access and audit-persistence failure policy. No general dashboard implied.

### M6 — Diagnose login and session failures safely

**Source:** codebase-details 3. **Evidence:** intake 75, 79–80; research 107–110; `RequestIdFilter.java:22–34`, `ApiExceptionHandler.java:70–72`; K0 38–42.

**Risk:** provider cancellation, callback validation, provisioning and session-store failures look alike; a per-request ID does not join a cross-instance login journey. **Recommendation:** reuse K0 errors/correlation, add safe opaque journey correlation and bounded stage/reason/outcome events; exclude callback secrets. Distinguish new-login availability from established-session readiness. **Owner:** startup failure versus unavailable-login state; engineering defines diagnostics.

### M7 — Distinct user recovery for session and permission denial

**Source:** comprehension 2. **Evidence:** intake 22, 26; K0 40–42.

**Risk:** re-login loops cannot repair removed grants; automatic replay may duplicate a prior submission. **Recommendation:** sign-in for expired/revoked sessions, explicit permission-denial recovery for authenticated users, non-disclosing ownership errors, same-account restoration and submitted-operation reconciliation. **Owner:** hide versus explain disabled actions. Exact routes/codes belong in drafting.

### M8 — Authentication-mode session transitions

**Source:** implementation-readiness 2. **Evidence:** intake 12–14, 22–26.

**Risk:** changed settings or inconsistent instances can interpret an old cookie as the wrong actor/mode. **Recommendation:** define actor selection and incompatible-state rejection/invalidation; require consistent mode across instances and test both transitions. This adds no account switcher/content migration and does not prohibit production overrides. **Owner:** whether Google sessions survive a temporary disabled-mode run.

### M9 — Restore must reconcile identity authority

**Source:** implementation-readiness 3. **Evidence:** intake 24–26, 52–63; application 533–535.

**Risk:** old backups restore revoked sessions/grants or unconsume bootstrap. **Recommendation:** pass T11/T15 a bounded recovery contract; one option is non-serving restore, session invalidation and reconciliation of grants/bootstrap against trusted evidence. A separate auth journal is optional; clearing sessions alone is insufficient. **Owner:** acceptable recovery/evidence policy. This differs from deferred Google-account-loss recovery.

### N1 — Show effective mode to operators

**Source:** codebase-details 4. **Evidence:** intake 12–14; current application profile YAML.

**Recommendation:** sanitized startup diagnostics for instance/profiles/effective mode/config validation, without secrets. No new owner choice or restriction on accepted overrides needed.

### N2 — Supported bootstrap self-identification

**Source:** comprehension 3. **Evidence:** intake 52–59; `frontend/src/main.tsx:16` readiness-only UI.

**Recommendation:** let a signed-in operator obtain their own UUID through a supported narrow path; command output clearly identifies target and success/consumed/invalid outcomes. Avoid SQL/email guessing or a public account directory. Exact UX is drafting work.

## Remaining inputs and limits

Owner questions M1/M2/M4 lead the next clarification; M5/M8/M9 and existing missing-config/recipient-identity choices also need disposition. Google research already warns that email text is not universal current-mailbox proof; the round does not approve a recipient mechanism. Existing basic/private-sharing recommendations remain pending, and public snapshots remain excluded. None of these findings reverses accepted explicit permissions, bootstrap, timeout values or YAML overrides.

Current code is foundation only: no legacy password-user conversion, existing public-snapshot cleanup or proven auth cache exists. Reviewers identified reuse of K0/`ApiError`/`RequestIdFilter`, `PostgresIntegrationTest`, Spring security mechanisms and JDBC sessions; these are proposed design inputs, not implemented integration. No builds, tests, performance runs, runtime changes, Google project/client checks or real sign-in occurred. External framework assertions in the raw reports link primary checks; they do not establish this application's behavior or pricing. Later formal design, gate agreement, implementation authority and report-only implementation review remain separate.

## Integrator artifact verification

The integrator read the brief, normalized report and six raw results, checked available native child completion state and reconciled the later review status in the intake/sitemap/roadmap. `git diff --check` passed. An ephemeral checker validated 12 changed/new Markdown files, 96 local file/anchor references (including source links with line suffixes), all required brief sections, six nonempty raw reports, 16 Major plus 3 Nit raw observations, and the normalized 9 Major plus 2 Nit count. The initial synthesized brief was 1,950 words. These are artifact checks, not application tests or a new review of an implementation. All recommendations remain pending owner/design disposition.

## Post-review discussion

M1: the owner requested an inactivity recommendation. The integrator added a proposed user-activity policy to the brief, excluding automatic polling and using bounded activity reporting for local interactions. It remains pending owner acceptance and implementation design; M1 is not closed and no repeat Goldfish review is claimed. Original raw reports and reviewed baseline remain unchanged.

The owner then proposed near-expiry renewal and asked about uploads/project creation. The revised proposal recommends normal user-action request renewal, throttled local interaction reporting, no separate status check per action, and separate admission/completion semantics. It documents the shortened-idle tradeoff of near-expiry-only renewal and retains the absolute limit. M1 and the affected M2 operation boundary remain pending owner acceptance; no new Goldfish round is claimed.
