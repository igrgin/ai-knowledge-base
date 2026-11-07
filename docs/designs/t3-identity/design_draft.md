# T3 identity, sessions, permissions and administration

* **Author:** igrgin
* **Status:** Draft — Overview & Goals approved 2026-11-07; Technical Plan in progress, starting with disabled-sign-in behavior. No mode option selected.
* **Date:** 2026-11-07, Europe/Zagreb
* **Issue:** [#3](https://github.com/igrgin/ai-knowledge-base/issues/3)
* **Workflow:** EGM with small fixes 0.2.1, `egm-draft`.
* **Predecessor:** [Concepts brief](concepts_brief.md), with [accepted decisions](authentication-decisions.md) and [round-2 findings](../../reviews/t3-identity/brainstorm-round-2/review.md).
* **Authority:** The owner authorized bounded T3 drafting and resolution of disabled-sign-in behavior on 2026-11-06. This is a working design, not implementation authority or completed design review.

The owner approved Overview & Goals on 2026-11-07 and instructed proceeding to the Technical Plan, starting with the remaining disabled-sign-in behavior. The approval preserves the existing scope; it does not select an unresolved technical mechanism.

## Overview

T3 establishes how people enter the AI knowledge base, keep an application session and exercise their assigned permissions. Google is the sole normal login provider. A first successful sign-in automatically creates an ordinary application account; returning sign-ins resolve that same account. No invitation, separate registration, approval or profile-completion step is required. The account receives basic capabilities immediately, including permission to create projects as T4 introduces that feature.

The application owns accounts, sessions and authorization. Google proves identity; it does not decide access to projects or administrative actions. Administrators use the same website and account model, with ordinary capabilities plus explicit administrative permissions. Those permissions do not confer access to other people's private projects, PDFs or chats.

Developer startup from IntelliJ must also work without Google sign-in or credentials. An explicit YAML setting controls this behavior. Disabled development uses a persistent database administrator account through the same authorization and ownership paths. Production defaults to requiring sign-in but permits an explicit override. This design must resolve disabled sign-in outside development without inventing a production development-admin fallback or silently reversing that override.

The scope includes the owner's accepted session controls, individual permissions, initial-administrator safeguards and configurable auditing. It supplies narrow contracts to later features rather than implementing their projects, uploads, jobs or sharing workflows.

## The Problem

The running application currently provides a foundation/readiness shell rather than an account or security boundary. It cannot yet identify a project owner, preserve a user's login, enforce individual grants or let administrators manage those grants and sessions. Later resource features need one trusted account identity and one consistent enforcement contract before they can safely store private content.

Development must remain easy to start while using realistic persisted accounts and permissions. That convenience must not overwrite a saved Google session, renew its deadlines, regrant deliberately removed authority or leak private state between identities. Production disablement is an accepted operator choice whose no-session behavior remains unspecified; leaving it implicit would make entry behavior and attribution unpredictable.

The brainstorm also reached future sharing, upload and recovery details. They establish consumer obligations, but requiring those complete implementations now would keep T3 from becoming buildable. This design bounds their interfaces and keeps their implementation with their existing owners.

## Goals

The detailed accepted decisions remain constraints throughout drafting. A technical proposal is not selected merely because it appeared in research or the brief.

| Outcome | Required behavior |
| --- | --- |
| Automatic Google accounts | Create exactly one application account on first successful sign-in; resolve returning identities without merging accounts by email. Keep a stable application UUID distinct from Google profile data. No application passwords or separate admin login. |
| Configurable developer entry | Plan `app.auth.enabled=true` commonly and in `prod`, with `dev` overriding to false. Allow explicit overrides and tests of both values. Enabled mode fails startup with sanitized feedback if required Google settings are missing; disabled mode needs no Google configuration. Never silently change the boolean. |
| Persistent development identity | Disabled development creates/reuses one database admin, preserving its UUID, data and explicit revocations across restarts. Provision initial grants once. Enabled development uses Google. Never provision/select this fixture when `prod` is active, including mixed profiles; keep normal initial-admin setup separate. |
| Explicit disabled-mode behavior | Resolve requests with and without valid sessions outside development in the technical plan. Preserve permitted production disablement and the required red administrator banner driven by backend effective configuration: “Sign-in is disabled in the production profile.” Never treat disabled sign-in as disabled authorization. |
| Session continuity and isolation | Preserve valid Google application sessions across browser reopening, backend restart and mode changes without resetting/freezing their deadlines. Keep dormant Google sessions separate from development identity; development activity never renews them. Resume only still-valid sessions. Keep private view/draft restoration within the same account and never replay a write automatically. |
| Independent session timing | Each application session has its own server-enforced defaults: 30 minutes idle and 8 hours maximum. Meaningful authorized actions renew inactivity; local visible-tab interaction reports are limited to once per minute across tabs sharing a session. Polling, prefetch, workers, passive tabs and other sessions do not renew it. At five minutes or less before the maximum, new uploads/mutations need fresh sign-in; reads remain available while valid. All four timing defaults are configurable in development, production and test profiles. |
| Session management | Users list/revoke selected or all own sessions; ordinary logout ends the current session, while revoke-all includes it. Explicitly authorized admins manage user sessions across instances. Revoked cookies cannot resume sessions; later Google sign-in remains possible with current permissions. Define concurrent-login/revoke-all behavior without adding account suspension. |
| Current individual permissions | Admins assign/revoke individual permissions in the app. New and existing accounts receive new basic permissions without restoring explicit revocations. Check current permissions and resource ownership for each new protected request; no forced logout or Google re-login for grant changes. Hide unauthorized admin areas and explain disabled ordinary actions; reject stale/forged submissions. |
| Initial administration | A one-time trusted-host command targets an existing account UUID and atomically establishes initial permission-management, admin session-management and audit-inspection grants. Supply supported own-UUID discovery and useful command outcomes. Do not replay grants on ordinary startup. After setup, atomically prevent removal of the final permission manager, including concurrent changes. Count account authority rather than live sessions. |
| Configurable audit foundation | Independently default admin capture to true with 90-day retention and ordinary-user capture to false with 30-day retention; all four settings are overridable across profiles. Admin capture may be disabled, including for the development admin, without falling through to user capture. Preserve captured category for cleanup and keep functional permissions/bootstrap state separate from history. |
| Safe audit inspection and diagnostics | Provide permission-gated, read-only metadata history, without private content, tokens or session credentials. When admin capture is enabled, its privilege/session changes and audit rows commit together; audit failure rejects the change. Disabled capture omits that write dependency. Define remaining event/failure semantics. Minimal sanitized failure diagnostics remain available independently and must not recreate disabled action history. |
| Narrow consumer contracts | Later slices consume trusted identity/current permissions and retain their own resource ownership, admission and lifecycle rules. Atomically order admission against revocation. Already-authorized requests and specific durably accepted work may finish within original bounds; new submissions/retries and result/status/file reads require current authority. Preserve spending, quota, deletion and sharing fences without implementing their engines here. |

Basic capabilities retain the [accepted action table](authentication-decisions.md#accepted-basic-permission-set): own projects, eligible document/chat/source actions, own sessions/status, and full-project share/import. Administrators receive those basic capabilities plus administrative grants, with explicit revocations still authoritative. Future feature permissions become active with their actual slices; this design does not pre-build their functionality.

The accepted Google recovery policy also remains: safely end a failed attempt and offer user-triggered fresh sign-in, without replaying a callback or one-use code. Do not invalidate unrelated sessions or extend authority during a provider failure. Concrete dependencies, transport bounds and failed same-browser reauthentication require design and verification.

T3 completion must eventually demonstrate its accepted behavior through its own UI/API/persistence and meaningful checks, rather than documentation alone. Developer-bypass evidence, offline protocol/fault tests and real Google acceptance are distinct. No live Google setup, runtime tests or performance measurements are established by this initial section.

## Non-Goals

* Implementing projects, PDFs, saved chats, AI processing, durable jobs or upload staging. T4 and the existing feature owners retain those implementations and their ownership/admission checks.
* Implementing invitations, general sharing links, capture manifests or full-project copying. T16/T19 retain both accepted access modes and mandatory complete independent copies, including all saved chats/citations/source settings. No content selector or files-only product mode is added.
* Restoring standalone chat snapshots, anonymous/public content access or an administrator exception for them. R27/T17 remain withdrawn.
* Giving administrators access to another account's private content; bypassing quotas, recipient checks, lifecycle fences or provider spending rules.
* Adding account suspension/deletion/relinking, external Google-account-loss recovery, an application email service, a local-user product edition or an account switcher.
* Building later operational restore machinery or cross-feature UI integration in T3. T3 states its identity/authority recovery obligations and supplies metadata/interfaces; T11/T15 and T12 retain their integration work. T13 extends integrated fault/multi-instance evidence without replacing T3's own checks.
* Introducing a generic policy language, universal domain-event system, second job engine or new audit service without demonstrated need.
* Authorizing implementation, publishing gates, changing remote issues, deploying or making paid calls through this draft.

## Technical Plan (Architecture)

### 1. Authentication mode and effective account

This subsection starts the technical plan with one pending author decision. The options below are application-policy proposals, not framework defaults or selected behavior. The remainder of the technical plan and its section approval follow after the consequential choices are settled.

#### Rules already fixed

| Configuration | Account behavior already accepted |
| --- | --- |
| Auth enabled, any profile | Use normal Google authentication/application sessions. Missing required Google settings stops startup with sanitized feedback. Do not select a development account. |
| Auth disabled, `dev` active and `prod` absent | Create/reuse and automatically select the persistent development database admin. Preserve stored Google sessions separately under running deadlines; do not renew them through development activity. |
| Auth disabled outside that development case, including mixed `dev`/`prod` | Startup does not require Google configuration. Do not provision/select the development admin. Existing-session and no-session request behavior must be explicitly selected below. |

In every case, current application permissions and resource ownership remain enforced. Mode changes do not invalidate saved Google sessions, reset/freeze their deadlines or revive expired/revoked sessions. When `prod` is active and auth is false, an identifiable administrator must receive the selected red banner. A browser-supplied UUID/email cannot establish an account.

#### Pending decision: entry outside development when auth is false

Both alternatives below retain valid Google sessions as the effective account for requests outside development. Neither supplies an anonymous content path, shared fallback account, new login provider or production development admin. They differ in whether fresh Google entry remains available.

| Alternative | Mechanism | Trade-off and failure condition |
| --- | --- | --- |
| **A — Existing sessions only** | Keep using existing valid Google sessions with normal activity, permission and deadline rules. Disable new Google entry/callback completion while the flag is false. A request without a valid session cannot access protected resources; show a neutral entry-unavailable message without starting a login loop. | Gives “sign-in disabled” a literal meaning outside development. An existing admin can reach the banner while their session remains valid. A new browser, revoked/expired session or session needing pre-write reauthentication cannot regain access until the operator re-enables auth with valid Google settings. This mode is not a replacement login path or indefinite outage workaround. |
| **B — Existing sessions plus configured Google entry** | Keep using valid Google sessions. Also offer fresh Google sign-in/reauthentication when the operator supplies complete required Google settings. Without those settings, startup still succeeds and protected entry requires an already-valid session. | Allows new browsers and pre-write reauthentication while Google is configured and available. Outside development, the boolean would disable the requirement for Google configuration rather than disable Google entry itself; the property/banner wording is consequently less literal and needs clear operator documentation. Without configuration or during provider failure, fresh entry remains unavailable and existing-session limits still apply. |

Neither alternative permits extending deadlines because reauthentication is unavailable. Ordinary reads require a valid session; new writes at or beyond the configured pre-write threshold require fresh authentication. Logout/session revocation continues to end the selected sessions without suspending the account. Already-admitted work retains its previously accepted boundary; no new job behavior is introduced here.

The selected production banner condition and wording remain unchanged under either proposal. Its audience is an existing authenticated account with the administrative authority to be defined later in this plan. Neither option guarantees a reachable in-app banner after all administrator sessions have expired. Deployment documentation must explain the selected entry behavior; an additional startup warning remains a separate recommendation, not an accepted replacement for the banner.

No alternative is selected or ranked. Account/context storage, exact routes, response fields and security-filter implementation remain later technical-plan work. After selection, record it in the intake and governing design before treating it as a consumer contract.

## Open Questions

These are bounded inputs for the technical plan, not requests to reopen accepted product choices.

1. **Disabled sign-in outside development:** choose Technical Plan alternative A or B, or revise the proposed behavior. No option or fallback account outside development has been selected.
2. **Authority representation and evolution:** choose the finite permission/default/revocation mechanism, stable identifiers and replacement rules. The shared-default/per-account-exception model remains a proposal. Preserve create-once setup evidence independently of retained audit history.
3. **Security/session and persistence boundaries:** settle module direction, domain persistence comparison, session/context isolation, filter/CSRF/error behavior, transaction ordering and cross-instance consistency. Preserve the accepted deadlines and revocation semantics.
4. **Audit and operational bounds:** settle action/failure behavior, safe metadata, retention cleanup and finite diagnostic acceptance. Bound and measure combined session/permission/audit work against the existing datasource rather than assuming its cost.
5. **Executable handoff:** later approved sections must identify exact file actions, migrations, permitted shared changes, buildable phases and meaningful checks, carrying every accepted requirement and review obligation forward.

## Additional Resources

* [Application requirements and target design](../application.md)
* [Roadmap authority, ownership and prerequisites](../../roadmap.md)
* [Available contracts](../../contracts/README.md) and [K0 foundation](../../contracts/k0-foundation.md)
* [Google sign-in investigation](../../research/google-sign-in-investigation.md) and [failure-handling investigation](../../research/google-sign-in-failure-handling.md)

The Technical Plan is in progress. Critical User Journeys, Alternatives Considered, Non-Functional Requirements and Detailed Implementation Plan will follow through the EGM section gates. This working draft does not yet supply an implementation file allowlist or resolve the open architecture choices.
