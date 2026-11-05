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

The most repeated objection concerns the meaning of inactivity. The owner chose 30 minutes idle and eight hours maximum from sign-in, but planned job polling runs approximately every two seconds. Counting every authenticated request as activity lets an unattended polling page remain signed in until the absolute deadline. Counting deliberate interaction instead requires a clear treatment of passive PDF reading, background tabs and multiple tabs sharing one session. Neither interpretation was selected. The brief therefore carries an owner choice, not a new timeout or heartbeat mechanism. Enforcement must remain server-side and activity in another session must not extend this session.

Revocation also needs distinct meanings. Ending a session, removing an action permission and revoking a resource are separate changes. Reviewers recommend that committed session/permission changes govern subsequent authorization checks across instances, rather than leaving grants frozen in an eight-hour session. This is a proposed effect boundary. The design must settle concurrent requests, login racing with “revoke all,” and whether later fresh Google login remains allowed. Revoking sessions must not silently introduce account suspension.

Later durable jobs complicate the boundary further: a job can be accepted before a permission is removed and dispatch or publish afterward. The owner must choose whether permission removal blocks new submissions only, also blocks undispatched work, or additionally fences publication. T3 should define the account-based consumer contract with future owners, without implementing their jobs. Logout need not cancel accepted work; any such rule requires an explicit choice. Already dispatched or uncertain paid calls still require the owning feature's accounting settlement. Workers must not depend on a live browser session or Google token.

The basic catalog needs an upgrade policy. New accounts receive basic grants, while each feature introduces its real action permissions. Granting only at registration disadvantages older accounts; reapplying a current default set on every login can undo deliberate administrator revocations. Reviewers recommend stable application-defined permission identifiers and an explicit existing-account grant policy, with intentional restrictions preserved across upgrades. This calls for a small shared rule, not roles, editable policy definitions or a deny hierarchy. The detailed basic catalog and private-sharing defaults remain unaccepted.

The Google research recommends server-side OpenID Connect through Spring Security and PostgreSQL-backed Spring Session. Application identity should remain a UUID separate from email/profile data, with verified issuer/subject uniqueness and atomic first-account creation. Those mechanisms still need bounded selection and verification. The existing repository supplies safe error envelopes, request correlation, short transaction conventions and real-PostgreSQL test plumbing, but no identity implementation. Start from those conventions and the framework session store before proposing a duplicate session registry. JDBC sessions do not resolve the separate domain-persistence comparison required before domain implementation.

Authentication itself needs finite resource use. Account project/PDF quotas do not bound login bursts, pending login state, session listings or administrative account searches. Reviewers recommend bounded state lifetimes and responses, small persisted attributes, indexed account/session lookup and verified cleanup. Measure session database work against the existing small pool and competing instances. No traffic target, throughput guarantee, provider fee or necessary AI token expenditure is established. Any simultaneous-session cap that signs people out is a separate owner-facing policy choice.

Operator evidence should explain failures without exposing credentials. Review recommendations include minimal durable evidence for committed permission changes and administrator revocations, supplementing the already accepted bootstrap evidence; safe correlation across login initiation/callback requests; a small stage/reason vocabulary; and sanitized effective-mode diagnostics. Audit retention, inspection authority and failure handling remain open. Raw callback queries, tokens, cookies and private content must stay out of telemetry. No logging service, audit dashboard or tracing platform is proposed as a prerequisite.

User recovery must distinguish an expired/revoked session from a valid session that lacks permission. Reauthentication can repair the former; it cannot restore an administrator-removed grant. Ownership denial must keep K0's non-disclosing response. Preserve unsent work where permitted, restore private state only after confirming the same account, and reconcile submitted operations before retrying. T3 owns the auth response contract; T12 and feature owners integrate actual views and question drafts when those screens exist. Bootstrap also needs a supported way to discover one's own account UUID without SQL or email guessing.

Two lifecycle safeguards remain proposals. Changing `app.auth.enabled` must define how existing cookies and authentication records are interpreted, and instances must agree on mode. Rejecting incompatible state is a simple option; retaining resumable Google sessions after a temporary disabled-mode run is an open choice. Separately, database restore can resurrect revoked sessions/grants or lose bootstrap's consumed marker. T3 should supply T11/T15 an identity recovery contract. One bounded option keeps restore non-serving, invalidates restored sessions and reconciles grants/bootstrap against trusted evidence. A separate auth journal is an alternative, not an accepted new subsystem. Neither concern adds a local-to-Google content migration or Google-account-loss recovery feature.

## Goals vs. Non-Goals

**In-scope goals already selected by the owner:**

- Google-only sign-in, automatic first-account creation, returning-account continuity and immediate project-creation permission.
- Planned `app.auth.enabled`: common true, `dev` false and `prod` true, with explicit overrides allowed. Disabled mode supplies a stable server-established account without Google configuration; permissions, ownership and quotas still apply. Enabled mode must never fall back because Google configuration is missing.
- Server-enforced, configurable 30-minute idle and eight-hour absolute limits; valid sessions survive browser reopening and restarts without resetting age. Each session has independent timers.
- Users list/revoke one or all own sessions; ordinary sign-out ends the current session, and the explicit all-sessions action includes it. Administrators can revoke selected/all user sessions through a separate permission across instances.
- In-app assignment/revocation of individual action permissions plus resource ownership. A role-only model is insufficient. New accounts get basic grants; the no-sign-in account follows the same authorization paths.
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

Carry owner decisions above unchanged into the next draft. Review recommendations in this brief are pending dispositions, and no reviewer selected policy on the owner's behalf. The immediate owner choices are:

1. Define inactivity against polling and passive reading, retaining the selected time limits.
2. Choose session/permission revocation effect for concurrent requests and already accepted work, including the boundary of “all sessions.”
3. Decide the detailed basic grants, including private-sharing defaults, and how newly introduced defaults affect existing users without restoring intentional restrictions.
4. Resolve missing-Google-configuration behavior, authentication-mode transitions and identity restore safeguards; retain the explicit override and no-silent-fallback requirements.
5. Set a proportionate administrative audit policy and usable permission-denial/session-management presentation. Agree recipient identity with T16/T17 without treating email text as universal proof.

After refinement and explicit brief alignment, the EGM handoff can offer formal drafting or retaining the brief for later. A bounded `egm-draft` must settle the real principal/permission API, protocol and CSRF/error behavior, persistence comparison, transaction races, command invocation, safe session metadata and consumer interfaces. It must name exact Create/Modify/Delete files, migrations, permitted shared edits, buildable phases and meaningful checks. This brief does not supply that execution allowlist or publish a gate.

Proposed verification should separate three forms of evidence: no-sign-in startup/default/override behavior; offline protocol, persistence, ownership, permission and cross-instance tests; and actual configured Google acceptance. Exercise first-login races, changed email without account merging, browser/restart continuity, both deadlines, unattended polling, permission propagation, revoke-all races, atomic last-manager/bootstrap rules, catalog upgrades and mode changes. Reuse real PostgreSQL fixtures and verify security-filter responses against K0. T3 tests should remain offline-capable; later feature integrations carry their own draft/view and durable-work scenarios.

No Google project/client or actual sign-in has been verified, and this round ran no application tests or performance measurements. A controlled issuer or developer shortcut cannot establish Google readiness. Formal design review, explicit implementation authority and later report-only implementation review remain separate steps under the roadmap and workflow.
