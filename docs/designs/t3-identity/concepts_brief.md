# T3 Google identity, persistent sessions and explicit permissions

* **Author:** igrgin
* **Date:** 2026-11-06, Europe/Zagreb
* **Issue:** [#3](https://github.com/igrgin/ai-knowledge-base/issues/3)
* **Status:** Predecessor brief for owner-authorized bounded drafting. Accepted decisions are retained; open mechanisms remain pending. Formal design/review and implementation are incomplete.

## Overview

T3 lets people sign in through Google, automatically receive an application account and return to that same account with a valid persistent session. New accounts need no invitation, registration form, approval or profile-completion step and receive permission to create projects immediately. Projects themselves remain T4 work.

The owner expanded the original login/logout/restart milestone with development automatic sign-in, individual permissions, session administration, initial-administrator safeguards and configurable audit history. These are selected outcomes. Administrators use the ordinary website under the same ownership and quota rules; administrative grants do not grant access to another account's private content.

The [round-2 scope/progress review](../../reviews/t3-identity/brainstorm-round-2/review.md) completed six fresh-context lenses sequentially at `77e774661f350ec5a95dd3d9f25e25bf03333d7e`. The direction remains coherent, but progress is conceptual: 37 branch commits and 41 changed files are all Markdown, and runtime code still supplies only the foundation/readiness shell. No T3 behavior is implemented.

The recommended path is to stop broad product exploration, align this amended boundary and proceed to one bounded T3 design under scoped drafting authority. Further sharing, job or recovery exploration should not delay it. This recommendation neither removes accepted controls nor authorizes code. [Owner decisions](authentication-decisions.md), the [application design](../application.md), [roadmap](../../roadmap.md), [contract index](../../contracts/README.md) and [K0](../../contracts/k0-foundation.md) retain their respective authority. The amended issue body remains an unpublished local draft.

## Analysis

The main remaining behavior gap is requests without a valid session when sign-in is disabled outside development. Production disablement is permitted, while the development administrator is forbidden whenever `prod` is active. The design must state who, if anyone, becomes the effective account and how an authorized administrator reaches the banner. Do not silently forbid the override or select a production development-admin fallback.

Keep stored Google authentication, effective development identity, development provisioning and normal administrator bootstrap separate. Define one mode-to-effective-account contract across instances; scattered profile checks and overwritten security contexts would make expiry, revocation and private-state restoration brittle.

Resource slices should consume trusted identity and current-permission checks, retain their ownership/admission rules and emit bounded audit metadata. Identity must not import project/chat/job/sharing internals or construct their engines early. T3 agrees narrow accepted-work and recovery obligations; later owners implement their real consumers.

Reuse Spring Security, Spring Session JDBC, Flyway, K0 errors/correlation and real PostgreSQL fixtures. Verify framework session listing/revocation before adding another registry. JDBC session storage does not settle the separately required JDBC/JPA/Spring Data JDBC domain-persistence comparison or directional module/interface design.

Session persistence, permissions, activity and enabled auditing share the current four-connection pool per instance with a two-second acquisition timeout. Bound login state, stored attributes, lists and cleanup; use indexed lookups and short transactions. Measure representative combined work before adding caches, write coalescing, an event bus or an audit service. One-minute local activity reporting does not cap all database writes. No traffic target, capacity estimate or elapsed delivery estimate is established; the bounded design supplies buildable milestones.

Operational acceptance must show that sanitized evidence can explain a failed login and a protected-request rejection: provider/protocol failure, idle/maximum expiry, pre-write sign-in, revocation, permission denial or database failure. Reuse K0 request correlation and public error shape, including new security-filter handlers. Optional action history must not be the only diagnostic source or be secretly recreated in logs when disabled.

## Goals vs. Non-Goals

### Accepted identity and mode behavior

Google is the sole login provider. Keep a trusted application UUID separate from email/profile data; resolve returning identities and atomically avoid duplicate first-login accounts. The researched server-side OpenID Connect authorization-code integration still needs concrete design and verification. Rotate sessions at login, protect mutations against CSRF, use loopback-compatible development cookies and secure HttpOnly cookies for non-local HTTPS, and keep credentials/tokens out of browser storage.

Planned `app.auth.enabled` defaults true in common configuration, false in `dev` and true in `prod`; explicit overrides are allowed. Enabled mode requires Google and fails startup for missing/blank required settings with a sanitized operator error. Disabled mode needs no Google settings. Missing configuration never changes the boolean.

Disabled development creates/reuses a persistent database administrator and automatically acts as that account. Initial creation supplies basic and administrative grants; restart preserves its UUID, data and explicit permission revocations. Normal permission, ownership and quota checks remain. Enabled development uses Google. Exclude development provisioning/automatic identity whenever `prod` is active, including mixed profiles. Keep development provisioning separate from normal bootstrap and production-manager availability.

Mode changes preserve valid Google sessions under ordinary expiry/revocation without resetting/freezing deadlines. Development activity must neither overwrite nor renew dormant Google sessions. Prevent private draft transfer or automatic write replay across account changes. All serving instances must agree on mode. Non-development disabled-mode identity remains unresolved.

When `prod` is active and effective auth is false, trusted backend metadata drives a prominent red administrator banner: **“Sign-in is disabled in the production profile.”** T12 integrates text and color; exact administrative audience remains design work.

A transiently failed login ends safely and offers a user-triggered fresh sign-in with new state/code; never automatically replay callbacks/code exchange. Without a browser return, an in-app error cannot be guaranteed. Ordinary valid-session requests should avoid Google calls. Verify selected discovery/token/UserInfo/JWK dependencies, bounded transport behavior and failed same-browser reauthentication before claiming continuity.

### Activity and operation admission

Each signed-in session has independent server-enforced timers. Planned settings are overridable in `dev`, `prod`, `test` and `itest`:

| Property | Default |
| --- | --- |
| `app.auth.session.idle-timeout` | `30m` |
| `app.auth.session.max-lifetime` | `8h` |
| `app.auth.session.reauthenticate-before-write` | `5m` |
| `app.auth.session.activity-report-interval` | `1m` |

Valid sessions survive browser reopening/backend restart without resetting age. Authorized user actions renew idle age through their existing requests: navigation, project/PDF/chat actions and explicit permission/session controls. Visible-tab typing, scrolling and PDF page changes use bounded local signals, initially at most once per minute across tabs sharing a session. Polling, prefetch, workers, passive tabs and another session's activity never renew it. Delayed reports cannot revive expired/revoked sessions. Do not add a session-status call before every button.

With five minutes or less remaining before the configured absolute maximum, require fresh sign-in before uploads or other mutations; reads continue while valid. The default cutoff is 7h55m, derived from lifetime minus lead time. Never extend the maximum. Validate positive durations, activity interval below idle duration and lead time below maximum; short test overrides must remain consistent.

Ordinary expiry does not cancel already-authorized requests or durably accepted jobs. New requests need valid authentication. T6 owns unfinished-transfer staging/interruption and durable acceptance; do not label an unaccepted upload safe. Preserve the view/unsent question on expiry and restore private state only for the same account as owning features arrive.

Users list/revoke one or all own sessions; ordinary sign-out ends only the current session, while all-session revocation includes it. Separately authorized administrators revoke selected/all user sessions across instances. Old cookies cannot resume revoked sessions; a later Google sign-in remains allowed under current permissions. This neither suspends accounts nor guarantees Google logout/password prompts. Concurrent login/revoke-all and admitted-work interactions need design. No sticky sessions may be required; T13 extends integrated multi-instance evidence.

### Other review findings

Use explicit individual application permissions alongside ownership; roles alone are insufficient. New accounts receive own-project/document/chat/session/status and full-project share/import basics. Administrators receive those basics plus administrative grants, with individual revocations still authoritative. Quotas, recipient rules and private-resource ownership always apply. No PDF editing or individual PDF deletion is added.

Administrators assign/revoke individual permissions in the app. New basics reach existing accounts without restoring explicit revocations. Check current authority on every new protected request without session refresh, forced sign-out or Google re-login. Hide unauthorized admin areas; ordinary actions may be disabled with explanations. Reject stale/forged submissions and never automatically replay denied actions. Grants and UI permission refresh work within a valid session; exact refresh strategy is drafting work.

Already-authorized requests and specific durably accepted queued/running work may finish within original scope, deadlines and bounded automatic attempts. Atomically order admission against permission revocation: admission first preserves that operation; revocation first denies it. New submissions/user retries and later status/result/file reads require current authority. Lifecycle/sharing fences, quotas, spending admission and settlement still apply. No global job scan/cancellation follows from a grant change; accepted work can still spend after submit permission is removed.

The proposed finite permission catalog with shared basic defaults and explicit account grants/revocations remains pending acceptance. Revocation wins. Stable identifiers/replacement migrations must preserve restrictions; activate permissions with their real features. No generic policy language, wildcard category grant or login-time permission snapshot is selected.

A one-time trusted-host command targets an existing account UUID and atomically grants initial permission-management, administrator session-management and audit-inspection authority with durable completion evidence. Ordinary startup never replays grants. Draft supported own-UUID discovery and actionable command results. After setup, atomically prevent removal of the final permission manager, including concurrent self-revocation; count account authority, not live sessions. Development provisioning must not consume normal bootstrap. Restore obligations must prevent revived sessions/grants or replayed bootstrap; T11/T15 implement operational recovery. Audit retention is not bootstrap state.

Accepted audit settings are independent and overridable across runtime/test profiles:

| Property | Default |
| --- | --- |
| `app.audit.admins.enabled` | `true` |
| `app.audit.admins.retention` | `90d` |
| `app.audit.users.enabled` | `false` |
| `app.audit.users.retention` | `30d` |

Development administrators follow admin settings; disabled admin capture must not fall through to user capture. Classify authority at action admission and persist category for retention. Enabled administrative privilege/session changes and their audit rows commit together; audit failure rejects the change. Disabled admin capture omits that dependency. Other event/failure semantics need design.

Provide permission-gated, read-only metadata history, excluding tokens, session credentials, private bodies and unnecessary profile data. Capture safe actor/target/action/time/outcome/correlation. Do not record failed actions as committed changes. Check flags before event enrichment; bound pagination/records/cleanup. Cleanup follows captured category/retention even when capture is off. Disabling capture does not erase existing history, reconstruct missing events or disable minimal failure diagnostics/functional state. Verify all four flag combinations and measure overhead. Feature owners emit actual hooks; T3 owns storage/settings/inspection contracts and T12 cross-feature presentation.

**Non-goals:** application passwords/separate admin login; private-content administrator override; account suspension/deletion/relinking or Google-account-loss recovery; email service; local-user edition/account switcher; universal policy/event infrastructure; future resource/job/sharing implementations; deployment, paid calls or capacity guarantees.

## Alternatives Weighed

The owner selected Google over earlier application-password proposals and chose the one-time existing-account bootstrap over manual assignment. First registrant privilege, email-string matching and startup grant replay are excluded by that workflow.

The accepted configurable boolean supersedes the earlier hard development-only restriction. Mode-change invalidation was rejected; valid Google sessions remain under ordinary deadlines. A production-disablement ban would require a new owner decision.

All standalone chat sharing/snapshots are dropped, including restricted/public views and administrator exceptions; R27/T17 remain withdrawn. Whole-project copies include every saved chat/message/citation and source setting; T19 is mandatory, with no content selector/files-only product mode. The owner selects a designated signed-in recipient or a general link usable by any signed-in possessor with import permission/quotas. Copies are independent; revocation stops future imports without erasing accepted copies. T16/T19 own selection/proof, capture and copying. These accepted choices are context for T3, not T3 implementation work.

Framework OIDC/JDBC sessions are the recommended starting point; duplicate session tracking, a policy engine and asynchronous audit infrastructure need demonstrated justification. Exact domain persistence, default/exception storage and restore mechanisms remain design alternatives, not owner rejections.

## Key Decisions & Staging for Next Steps

Preserve accepted controls and stop adding adjacent product questions. Align this brief and obtain scoped drafting authority. Resolve non-development disabled-mode identity and disposition the permission mechanism; settle module direction, persistence, protocol/CSRF/errors, transaction races, safe session metadata, command sequence and consumer interfaces in one bounded design.

A proposed drafting sequence is account/security/session continuity and development modes, then permissions/bootstrap safeguards, then session administration/audit inspection and consumer contracts. These are recommendations, not accepted phases or authority to omit controls. Any actual T3 scope reduction/reassignment needs owner choice.

The design must list exact Create/Modify/Delete files, migrations, permitted shared edits, buildable phases and meaningful checks. Midpoint evidence should establish two-account identity/session continuity; final T3 evidence must cover all accepted controls, first-login/bootstrap/revocation races, timing/activity, permission evolution, mode isolation and audit combinations using real PostgreSQL. Keep developer-bypass checks, offline test-only OIDC protocol/fault checks and real Google acceptance separate. No Google project/client/sign-in, application tests or load measurements were verified in this checkpoint. Formal design review, separate implementation authority and later fresh implementation review still follow.

On 2026-11-06 the owner instructed proceeding to bounded `egm-draft`, keeping all accepted decisions and resolving disabled-sign-in behavior as part of that design. The [working draft](design_draft.md) is the active artifact. This authorizes drafting, not automatic acceptance of proposed mechanisms or runtime implementation.
