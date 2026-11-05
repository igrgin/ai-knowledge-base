# T3 Google identity, persistent sessions and explicit permissions

* **Author:** igrgin
* **Date:** 2026-11-05, Europe/Zagreb
* **Issue:** [#3](https://github.com/igrgin/ai-knowledge-base/issues/3)
* **Status:** Brainstorm synthesis for owner review; not an accepted feature design or implementation handoff.

## Overview

T3 must let people enter the private PDF knowledge base through Google, return to the same application account, and keep a valid application session across browser reopening and backend restarts. The first successful sign-in automatically creates an account with basic permissions and allows immediate project creation, without a registration form, invitation, approval or profile-completion step. Project functionality remains T4 work.

The owner also selected a configurable developer launch without Google sign-in, independent session expiry and revocation controls, and application-managed individual permissions. Administrators can manage grants and revoke user sessions through explicitly authorized actions. Those powers do not grant access to private documents or chats. This expands identity beyond login alone, so the bounded design must establish one authorization boundary that later features can consume.

The [intake](authentication-decisions.md) records owner decisions and remaining proposals. [Application requirements](../application.md), the [roadmap](../../roadmap.md), [K0](../../contracts/k0-foundation.md) and [implemented architecture](../../../EGM.md) remain authoritative at their respective boundaries. Only the foundation exists today. This brief summarizes six completed fresh-context brainstorm reviews at baseline `bb26f2571b6d6787d86fa29ed1f4f8a55a5dfda4`; [review evidence](../../reviews/t3-identity/brainstorm-round-1/review.md) retains findings and limits. Completion of this round does not mean owner acceptance, completed formal design review or implementation authority.

## Analysis

The repeated inactivity objection now has an owner-selected policy: meaningful user-action requests renew idle age; local typing/scrolling reports are limited to once per minute; automatic polling does not count. The 30-minute idle/eight-hour absolute limits remain fixed. The owner accepted the operation/admission behavior and a configurable five-minute pre-write threshold. Server enforcement and concurrency still require drafting and tests.

### Activity and operation admission

The owner accepted ordinary user-action renewal, bounded local interaction reports, excluded automatic traffic and admission-based protection of accepted work. The owner also accepted the exact pre-action threshold and configurable timing defaults in every development, production and test profile; this is separate from idle renewal.

| Activity | Accepted treatment |
| --- | --- |
| Refresh/navigation; create/open/rename/delete an owned project | Qualifying activity; validate and renew on the existing server request |
| Upload/open/archive/unarchive PDFs; retry eligible processing | Qualifying activity; no new individual PDF delete/edit capability |
| Create/open/delete chats, send questions, change source selection | Qualifying activity on the existing authorized request |
| Explicit own-session/permission-management actions | Qualifying activity; ordinary permission checks still apply |
| Typing or PDF scrolling/page changes without an API request | Bounded activity notification from a visible tab, initially no more than once per minute across tabs sharing that session |
| Automatic status polling, prefetch, worker completion, passive open tabs or another session's activity | Do not renew the meaningful-activity clock |

Conditional sliding renewal has framework precedents; Spring's access updates still need application integration to exclude polling. [Conditional renewal](https://learn.microsoft.com/en-us/dotnet/api/microsoft.aspnetcore.authentication.cookies.cookieauthenticationoptions.slidingexpiration), [Spring Session filter 4.1.1](https://raw.githubusercontent.com/spring-projects/spring-session/4.1.1/spring-session-core/src/main/java/org/springframework/session/web/http/SessionRepositoryFilter.java) (checked 2026-11-05).

Do not add a session-status round trip before every button click or an extra renewal call for actions already reaching the backend. Server admission checks the live session, current permission and ownership, then records qualifying activity. Idle renewal concerns this application's session, not another Google login. No claim of one database write per minute is made: ordinary session persistence still has its own work. Throttle only noisy client-local interaction signals initially; verify database costs before adding more write-coalescing machinery.

Preserve the configured idle window on user-action API requests; local event recording has the configured report interval's granularity to specify/test in drafting. The server uses its own clock and cannot revive expired/revoked sessions from delayed reports.

Treat operation admission separately from session lifetime. Recommend renewing idle age before admitting project creation/upload and letting an already-authorized request or durably accepted job finish despite ordinary idle expiry; new requests still need a valid session. The owner accepted ordinary-expiry protection and the accepted-work permission boundary below. Session-revocation races remain separate. An upload that has not reached durable acceptance must not be reported as safely accepted; T6 must define bounded transfer/staging, interruption and any reauthentication/finalization behavior. Permission/resource/admin revocation rules remain separate.

The configured maximum is never extended. Accepted rule: with **five minutes or less remaining** (at or after **7h55m** from sign-in), require fresh sign-in before a new PDF upload or other state-changing action. Reads continue with a valid session until the maximum; new requests then require sign-in. The server decides at admission, not via a separate per-click status call. Already-authorized requests/accepted jobs finish despite ordinary expiry; unfinished transfers still need T6 staging/interruption rules. The cutoff is `max-lifetime` minus `reauthenticate-before-write`, not a guarantee of transfer duration. Passive reading can reach idle expiry; preserve position/draft. No implementation or repeat review is claimed.

Accepted timing defaults are YAML-overridable in `dev`, `prod`, `test` and `itest`: idle `30m`, maximum lifetime `8h`, pre-write sign-in lead time `5m`, local activity-report interval `1m`. The [intake property table](authentication-decisions.md) names the planned settings; production and development use the same timer defaults. Tests may override them consistently. No runtime configuration has been added.

### Other review findings

Owner-accepted permission policy: check current authority on new protected requests; let already-authorized requests and durably accepted operations finish, including queued work. [OWASP request checks](https://cheatsheetseries.owasp.org/cheatsheets/Authorization_Cheat_Sheet.html#validate-the-permissions-on-every-request) and [Spring method authorization](https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html) support action-boundary checks; they do not choose this application's queued-job policy.

Order operation acceptance and permission revocation atomically: admission committed first preserves that specific operation; revocation committed first rejects admission. Record the accepting account, approved action/target, request identity and immutable scope. Retain original bounded deadlines/automatic attempts; this is not a reusable permission snapshot. New submissions and user-triggered retries require current permission. Every later status/result/file request checks current read authority and ownership; an accepted job does not preserve revoked read access.

No full-user job scan is required just because an action grant changes. Workers still honor current project deletion, sharing/lifecycle fences, quotas, spending/provider admission and settlement. Already dispatched calls are settled; deleted content cannot be republished. Explicit job cancellation is a separate control if later requested, not a required new UI. Tradeoff: a bounded, accepted queued operation can still spend after its submit permission is removed.

Session revocation separately blocks subsequent use of that session; concurrent login/revoke-all and later fresh-login behavior still need disposition. T3 defines the authorization contract with future job owners, not their job implementation. The permission boundary is accepted; implementation and verification remain pending.

The owner requires new basics to reach existing users while preserving explicit administrator revocations. The [proposed mechanism](authentication-decisions.md#basic-permission-evolution), pending acceptance, combines shared basic defaults with explicit per-account grants/revocations; revocation wins. The owner accepted current server-side checks on the next protected request without user/session refresh or forced login. The proposal also avoids per-user backfills and permission polling. Stable action identifiers and explicit replacement migrations preserve restrictions. Categories are display groups, not wildcard grants. The detailed catalog and private-sharing defaults remain unaccepted.

The Google research recommends server-side OpenID Connect through Spring Security and PostgreSQL-backed Spring Session. Application identity should remain a UUID separate from email/profile data, with verified issuer/subject uniqueness and atomic first-account creation. Those mechanisms still need bounded selection and verification. The existing repository supplies safe error envelopes, request correlation, short transaction conventions and real-PostgreSQL test plumbing, but no identity implementation. Start from those conventions and the framework session store before proposing a duplicate session registry. JDBC sessions do not resolve the separate domain-persistence comparison required before domain implementation.

Authentication itself needs finite resource use. Account project/PDF quotas do not bound login bursts, pending login state, session listings or administrative account searches. Reviewers recommend bounded state lifetimes and responses, small persisted attributes, indexed account/session lookup and verified cleanup. Measure session database work against the existing small pool and competing instances.

Operator evidence should explain failures without exposing credentials. Review recommendations include minimal durable evidence for committed permission changes and administrator revocations, supplementing the already accepted bootstrap evidence; safe correlation across login initiation/callback requests; a small stage/reason vocabulary; and sanitized effective-mode diagnostics. Audit retention, inspection authority and failure handling remain open. Raw callback queries, tokens, cookies and private content must stay out of telemetry. No logging service, audit dashboard or tracing platform is proposed as a prerequisite.

User recovery must distinguish an expired/revoked session from a valid session that lacks permission. Reauthentication can repair the former; it cannot restore an administrator-removed grant. Ownership denial must keep K0's non-disclosing response. Preserve unsent work where permitted, restore private state only after confirming the same account, and reconcile submitted operations before retrying. T3 owns the auth response contract; T12 and feature owners integrate actual views and question drafts when those screens exist. Bootstrap also needs a supported way to discover one's own account UUID without SQL or email guessing.

Two lifecycle safeguards remain proposals. Changing `app.auth.enabled` must define how existing cookies and authentication records are interpreted, and instances must agree on mode. Rejecting incompatible state is a simple option; retaining resumable Google sessions after a temporary disabled-mode run is an open choice. Separately, database restore can resurrect revoked sessions/grants or lose bootstrap's consumed marker. T3 should supply T11/T15 an identity recovery contract. One bounded option keeps restore non-serving, invalidates restored sessions and reconciles grants/bootstrap against trusted evidence. A separate auth journal is an alternative, not an accepted new subsystem. Neither concern adds a local-to-Google content migration or Google-account-loss recovery feature.

## Goals vs. Non-Goals

**In-scope goals already selected by the owner:**

- Google-only sign-in, automatic first-account creation, returning-account continuity and immediate project-creation permission.
- Planned `app.auth.enabled`: common true, `dev` false and `prod` true, with explicit overrides allowed. Disabled mode supplies a stable server-established account without Google configuration; permissions, ownership and quotas still apply. Enabled mode must never fall back because Google configuration is missing.
- Server-enforced, configurable 30-minute idle and eight-hour absolute limits; valid sessions survive browser reopening and restarts without resetting age. Each session has independent timers.
- Users list/revoke one or all own sessions; ordinary sign-out ends the current session, and the explicit all-sessions action includes it. Administrators can revoke selected/all user sessions through a separate permission across instances.
- In-app assignment/revocation of individual action permissions plus resource ownership. A role-only model is insufficient. New and existing accounts inherit new basics without restoring explicit revocations; the no-sign-in account follows the same authorization paths.
- One-time trusted operator command targeting an existing account UUID, with atomic initial grants/completion evidence. Ordinary startup never reapplies grants. After setup, atomically prevent removal of the final permission manager, including concurrent changes; count account grants rather than signed-in sessions.

**Scope to resolve rather than treat as accepted defaults:** the exact permission catalog and UI, basic grants beyond the selected ordinary needs, private-sharing defaults, no-sign-in fixture, activity semantics, revocation boundaries, configuration failures, mode transitions, audit and recovery policy. The intake's proposed ordinary actions cover owned projects, retained PDFs, chats, sessions and operation/AI status within existing limits; they add neither PDF editing nor individual PDF deletion.

**Out-of-scope non-goals:** application passwords or a separate admin login; a general policy language; broader private-content administration; new account disable/delete/relinking or Google-account-loss recovery; Google-side logout/revocation guarantees; an application email service; a local-user product edition or account switcher; future PDF/chat/sharing implementations; deployment, paid calls and capacity claims. Public/anonymous transcript viewing is excluded entirely, including administrator overrides. Ordinary public login/static/health endpoints remain distinct from public content publication.

## Alternatives Weighed

The intake records the owner's explicit rejection of public chat snapshots enabled by an additional administrator-granted permission. The replacement is authenticated, explicitly authorized recipient access, with no public-view permission or route. This is a product choice; it does not depend on whether anonymous accounts would technically be necessary.

The owner selected Google as the sole login provider, replacing earlier application-password proposals. A separate password-based administrator login is not selected. Google proves identity; this application manages authority. No provider-based claim is being used to replace individual application grants.

The accepted YAML boolean supersedes both the earlier hard development-only restriction and the separate local-product interpretation. Defaults support IntelliJ startup with local services, and explicit overrides remain permitted in either profile. A proposal to forbid production disablement would require a new owner decision.

The one-time internal setup command replaces manual initial assignment as the chosen workflow. First registrant privilege, email-string matching and automatic grant replay on normal startup are excluded by that choice. Later operator recovery remains unresolved; the bootstrap facility must not become an always-on recovery backdoor.

Research recommends server-side OIDC and JDBC sessions over introducing browser-held login tokens or another identity service. These are recommendations to evaluate in drafting, not newly attributed owner rejections. Likewise, framework session lookup versus a parallel registry, a finite action catalog versus optional role grouping, and controlled identity recovery versus a separate journal remain technical alternatives. No implementation or performance comparison has selected their exact form.

Private project copying and restricted transcript publication retain their different accepted lifetimes: accepted project copies are independently owned; revoking a share stops future imports without erasing accepted copies; restricted snapshots expose only a fixed transcript and revoke with their source. Whether create/import/private-publish permissions belong to every user's basic grants is still a proposal. Public publication cannot be added through that discussion.

## Key Decisions & Staging for Next Steps

Carry owner decisions above unchanged into the next draft. Review recommendations in this brief are pending dispositions, and no reviewer selected policy on the owner's behalf. The next decisions and drafting inputs are:

1. Carry the selected activity policy and configurable timers into the bounded design and later verification; no further duration choice is pending.
2. Carry the accepted-work permission boundary into consumer contracts; settle session revocation/fresh-login and concurrent “all sessions” behavior.
3. Decide detailed basic/private-sharing grants and the proposed shared-default/per-account-exception mechanism; preserve the accepted existing-account/revocation outcome.
4. Resolve missing-Google-configuration behavior, authentication-mode transitions and identity restore safeguards; retain the explicit override and no-silent-fallback requirements.
5. Set a proportionate administrative audit policy and usable permission-denial/session-management presentation. Agree recipient identity with T16/T17 without treating email text as universal proof.

After refinement and explicit brief alignment, the EGM handoff can offer formal drafting or retaining the brief for later. A bounded `egm-draft` must settle the real principal/permission API, protocol and CSRF/error behavior, persistence comparison, transaction races, command invocation, safe session metadata and consumer interfaces. It must name exact Create/Modify/Delete files, migrations, permitted shared edits, buildable phases and meaningful checks. This brief does not supply that execution allowlist or publish a gate.

Proposed verification should separate three forms of evidence: no-sign-in startup/default/override behavior; offline protocol, persistence, ownership, permission and cross-instance tests; and actual configured Google acceptance. Exercise first-login races, changed email without account merging, browser/restart continuity, both deadlines, unattended polling, permission propagation, revoke-all races, atomic last-manager/bootstrap rules, catalog upgrades and mode changes. Reuse real PostgreSQL fixtures and verify security-filter responses against K0. T3 tests should remain offline-capable; later feature integrations carry their own draft/view and durable-work scenarios.

No Google project/client or actual sign-in has been verified, and this round ran no application tests or performance measurements. A controlled issuer or developer shortcut cannot establish Google readiness. Formal design review, explicit implementation authority and later report-only implementation review remain separate steps under the roadmap and workflow.
