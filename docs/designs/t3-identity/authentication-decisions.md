# T3 authentication intake decisions

* **Author:** igrgin
* **Date:** 2026-11-05
* **Issue:** [#3](https://github.com/igrgin/ai-knowledge-base/issues/3)
* **Status:** Goldfish brainstorm round 1 completed; owner dispositions/brief alignment pending. Formal design/review and implementation remain pending.
* **Artifact directory:** `docs/designs/t3-identity/`
* **Workflow:** EGM with small fixes 0.2.1, `egm-brainstorm`.

## Owner decisions

The owner selected Google as the login provider. For developer startup from IntelliJ with supporting services running locally, sign-in is controlled by an application YAML boolean. It defaults to **off in `dev`** and **on in `prod`**. These are overridable defaults, not a rule forbidding explicit disablement in production. This supersedes the earlier hard development-only restriction in commit `4f9a737` and the separate local-product interpretation in `da08ee3`.

Use `app.auth.enabled` as the planned property name. The common default is true; `application-dev.yml` sets false and `application-prod.yml` sets true. When false, the server supplies a stable application account/principal without a Google login or Google configuration. Existing ownership predicates and quotas still apply. When true, Google sign-in is required; missing Google configuration must not silently change the boolean. The concrete no-sign-in account/fixtures and property binding are T3 design work. This is the accepted planned configuration, not an implemented setting yet.

Google is the sole application login provider; no separate password-based admin login is selected. The initial plans had no application-admin capability. The owner now requests session controls for users and administrators: administrators can revoke a selected user session or all of that user's sessions. Administrators must also be able to assign/revoke individual permissions in the app. The owner selected a one-time internal setup command for the first permission manager; the exact permission catalog and admin UI design remain open; private-content access is not implied.

The owner requested Google research and an audit of plans, files and current issues before continuing brainstorming. This authorizes the research and planning reconciliation, not feature implementation or completion of issue #3. [The roadmap](../../roadmap.md) owns task authority; [the application design](../application.md) owns amended R1/R18 and C7.

On the first successful Google sign-in, create the application account automatically and let the user start creating projects immediately. No separate registration form, profile-completion step, invitation or approval is required. Returning sign-ins resolve the existing account; concurrent first sign-ins must not create duplicate accounts.

Session defaults are **30 minutes of inactivity** and **8 hours maximum from sign-in**, both configurable in YAML and enforced by the server. A valid session survives closing/reopening the browser and backend restarts within both limits; restarts do not reset its age. Expiry preserves the current view and unsent question while requesting sign-in again. Restore private view/draft state only for the same application account; these UI obligations apply as their owning features are introduced.

Each signed-in session has its own inactivity countdown and maximum lifetime; activity on another device does not extend it. Users can view/manage their sessions from settings or profile and revoke an individual session or all sessions. Ordinary sign-out affects the current session; the explicit all-sessions action includes the current session. Expiry happens independently per session. Administrator revocation must use an explicit permission and take effect across web instances; it does not grant access to the user's private documents/chats. Device/session representation, safe display metadata and concurrent-request revocation behavior need bounded design.

The owner requires an explicit permission system; a role-only authorization model is insufficient. Backend actions must check the needed permission together with resource ownership. Google establishes identity; application permissions and their assignments are managed by this application. Administrators assign/revoke individual user permissions through the app, and new accounts receive basic permissions automatically. Optional role grouping is not a substitute for those controls. The basic set must retain immediate project creation; its remaining actions need clarification. Disabling sign-in does not disable permission checks: the server-established no-sign-in account uses the same authorization paths. Permission catalog, basic defaults, setup-command implementation, exact UI and cross-instance propagation need the bounded design; no general policy language is selected. The new permission-evolution requirement below needs explicit revocations to be remembered separately; its proposed override model is not yet accepted.

## Proposed basic permission set

The owner described ordinary users as able to read, write and access their own documents and access their own chats, and requested suggestions for any missing everyday actions. Immediate project creation and own-session controls are already selected. The following expands those needs into a **proposal**, not an accepted final permission catalog or implementation allowlist. Exact permission identifiers and feature migrations belong in the bounded design/owning slices.

| Area | Proposed ordinary-user actions | Existing scope |
| --- | --- | --- |
| Projects | Create, list/open, rename and delete owned projects | T4/T11; preserve confirmation and tracked purge |
| PDFs | Upload, open originals/citations, archive/unarchive, view processing status and retry eligible failed work | T6/T18/T20/T7; no PDF editing or individual PDF deletion |
| Chats | Create/reopen/read, ask questions/follow-ups, delete owned chats and change eligible source selections | T8/T9/T20; keep document ownership and archive rules |
| Own sessions | List and revoke individual sessions or all own sessions | T3; independent idle/absolute expiry |
| AI/operation status | See the account's AI availability/limits and its own operation status | T5 and producing slices; permission never bypasses spending/quota admission |
| Share own project copies | Basic by default: create/revoke fixed copies for intended recipients or registered-link acceptance | T16/T19; explicit share action and existing independent-copy rules |
| Import an authorized project copy | Basic by default: accept/import subject to account/storage quotas | T16/T19; permission alone grants no access to an unshared source |
| Standalone chat sharing/snapshots | Dropped entirely, restricted and public alike | R27/T17 withdrawn; no publication permission, route or admin override |

Administrative session revocation and permission assignment/revocation are separate from the basic set. No default permission grants access to another account's private resources. Permission checks remain combined with resource ownership; resource IDs and browser-supplied permission lists do not authorize access. The exact permission matrix must be tested on the backend, consistent with [OWASP authorization guidance](https://cheatsheetseries.owasp.org/cheatsheets/Authorization_Cheat_Sheet.html#enforce-least-privileges) (checked 2026-11-05).

The project-share/import basic defaults above remain recommendations pending owner acceptance. Sharing is limited to complete project copies and importing those copies. Standalone chat sharing and all chat snapshots are dropped. The owner clarified that everything is copied, including all saved chats/messages/citations and source settings. T19 is required within the same import pipeline; no include-chats selector or files-only mode exists. The owner explicitly rejected public/anonymous chat publishing, including an administrator-granted exception: no such permission or public-view route is supported. This supersedes the earlier proposal in `2996c7e` and amends R27/T17. No anonymous account/login workflow is introduced. This earlier public-only removal is superseded by withdrawal of all standalone chat sharing. Project imports remain independent in both directions: edits/deletions in either project never change the other. Share revocation stops future imports without erasing accepted copies. Every project-share/import action remains explicit.

Alternative rejected by the owner: public snapshots enabled through an extra administrator-granted permission. The owner removed anonymous/public viewing entirely. Public URLs would not technically require anonymous accounts; this exclusion is a product decision, separate from Spring's anonymous security-context support. [Spring anonymous authentication](https://docs.spring.io/spring-security/reference/servlet/authentication/anonymous.html) Keep ordinary public login/static/health endpoints and the configured developer no-sign-in principal separate from that product decision; the development principal still satisfies ownership/recipient checks. Definitions of `read`/`write` do not add PDF editing, individual file deletion or unlimited AI use.

## Basic-permission evolution

**Accepted outcome:** new basic permissions apply to existing accounts too, without restoring anything an administrator explicitly revoked. The owner selected server-side permission changes with current checks on each new protected request. Permission changes require no user/session refresh, forced sign-out or fresh Google login. Already-admitted work retains the previously accepted boundary; UI permission displays may remain stale until their next normal reload, but cannot authorize an action. The default/exception storage model below remains proposed.

**Recommendation, pending owner acceptance:** keep a finite application-defined permission catalog, a shared set of basic defaults, and one explicit grant or revocation per account/permission when an administrator makes an exception. No exception means inherit the default. An explicit revocation wins over a basic default; an explicit grant allows a non-basic action. Regranting a revoked permission replaces that revocation through an authorized administrator action. Record revocation even when the permission is currently absent, so making it basic later does not silently grant it.

The effective action set is `(basic defaults + explicit grants) minus explicit revocations`, still subject to ownership, sharing, quotas and resource lifecycle. Permission-management and administrator session-management stay outside basic defaults. Bootstrap establishes explicit grants once and retains the last-manager invariant. This is a fixed precedence rule for individual permissions, not an editable policy language or role hierarchy.

“Automatic” means a reviewed feature migration activates its new basic permission in the shared catalog once. On the next protected request, each existing or new account inherits it unless explicitly revoked. No job walks all accounts, no login re-seeds assignments, and no session is invalidated just to refresh permissions. A wholly new feature category uses the same mechanism; categories organize named actions in the UI and do not confer wildcard access to future actions. Adding unrelated capabilities does not imply restoring a different, previously revoked action.

Use current shared database state in the normal protected business request, through bounded indexed catalog/account-override lookups. Keep identity in the session rather than treating login-time permission lists as lasting authority. Start without a cross-request permission cache, event bus or permission polling endpoint. Reuse the current-request result when appropriate; do not add a browser permission-check request before every action. UI visibility can update when it next loads permissions; stale UI is never authority. This avoids forced-login or account-update bursts, but ordinary authorization database cost still requires measurement against K0's connection/resource limits. It does not promise unlimited throughput.

Stable identifiers matter: renaming a display label keeps the permission ID. A split/replacement must explicitly migrate relevant revocations as well as grants, so changing the catalog cannot bypass the original restriction. New permissions start non-basic until deliberately approved as basic. Activate defaults consistently with deployed feature support; exact migrations, schema/indexes and mixed-version rollout belong in the bounded design. Default changes affect many accounts, so review their privilege impact rather than quietly adding all permissions to the basic set.

Preserve the accepted atomic order between permission changes and new-operation admission. Already-admitted work retains only its original scope; fresh requests see current authority. Verify old/new accounts, an explicit revocation surviving default activation and restart/login, deliberate regrant, non-basic/admin exclusion, catalog replacement and cross-instance visibility. No implementation or load test is claimed.

[OWASP recommends permission checks on every request](https://cheatsheetseries.owasp.org/cheatsheets/Authorization_Cheat_Sheet.html#validate-the-permissions-on-every-request); [Spring AuthorizationManager](https://docs.spring.io/spring-security/reference/servlet/authorization/architecture.html#authz-authorization-manager) supports application-specific authorization. Checked 2026-11-05. These sources support the enforcement boundary, not this proposed default/exception storage model, which is a project design recommendation.

## Accepted initial administrator setup

The owner accepted a one-time operator-run setup command that grants the initial permission-management and administrator session-management permissions to an explicitly selected existing application account UUID. This replaces manual initial assignment as the selected setup workflow; the command is planned, not implemented.

1. The operator signs in through Google normally, creating an ordinary account with basic permissions.
2. From the trusted application host/environment, the operator invokes the internal setup command with that account UUID. Exact command syntax is a design output; no such command exists yet.
3. The application verifies the account, grants the defined initial permissions transactionally, and records the bootstrap completion and grant evidence without tokens/private content. First successful bootstrap consumes the setup facility; concurrent/duplicate attempts cannot grant a different account.
4. Subsequent assignments/revocations happen in the permission-management UI under the accepted permissions. Ordinary startup must never replay the initial grant or undo a later revocation.

This is an operator action rather than a public setup endpoint. Normal users cannot invoke it through the application UI/API, and being the first registrant must not confer administrator permissions. Do not create a separate local-password admin account or trust a matching email string as proof of identity. The concrete trusted-host invocation, transaction/concurrency rules and failure recovery need bounded design. Removing the last permission manager is forbidden. A later external-account-loss recovery procedure is a separate unresolved question; it must not be an always-on bootstrap backdoor.

This recommendation replaces neither the owner's Google login decision nor in-app permission management. It is the selected way to establish the first permission manager without requiring ad hoc SQL edits, automatic privilege assignment on every startup or a generic role-only seed.

After initial setup, the application must retain at least one account with permission-management access. Reject a permission change that would remove the last such account, including self-revocation and concurrent changes by multiple managers. Enforce this against shared database state atomically; UI disabling alone is insufficient. Count account permissions, not currently signed-in sessions, so ordinary logout/expiry remains valid. Any future account-disabling/deletion operation must preserve the invariant when introduced; this does not add those features now. External Google account loss and operator recovery remain a separate design/operations concern.

## Research and audit

The [source-backed Google investigation](../../research/google-sign-in-investigation.md) covers existing-version fit, Google project/client setup, identity scopes, callback origins, persistent sessions, logout, offline verification and recipient identity. The [plan and issue audit](../../reviews/t3-identity/google-auth-plan-audit.md) records changed files, unchanged boundaries and exact proposed remote issue edits.

The recommended signed-in integration uses server-side OpenID Connect through Spring Security and PostgreSQL-backed application sessions. Keep the application's UUID identity independent of Google's email/profile. These mechanisms and their exact routes/schema still require the bounded T3 design; the provider decision alone does not select every research recommendation.

The owner accepted the [activity/admission policy](concepts_brief.md#activity-and-operation-admission): existing user-action requests renew idle age, typing/scrolling notifications are limited to once per minute, automatic traffic does not renew, and ordinary expiry does not cancel an already-authorized request or durably accepted job. New requests still require a valid session; permission/resource/admin revocation is separate. The owner accepted the exact absolute-limit preflight: five minutes or less remaining requires fresh sign-in before new uploads or other state-changing actions (7h55m onward with the default maximum); the hard limit remains 8h. All four timing values are configurable defaults in common application YAML and every runtime/test profile, including `dev`, `prod`, `test` and `itest`. Unfinished transfer/staging rules remain T6 design work.

Planned timing properties (not implemented yet):

| Property | Default in every profile |
| --- | --- |
| `app.auth.session.idle-timeout` | `30m` |
| `app.auth.session.max-lifetime` | `8h` |
| `app.auth.session.reauthenticate-before-write` | `5m` |
| `app.auth.session.activity-report-interval` | `1m` |

Each profile may explicitly override these values. The 7h55m example is derived from maximum lifetime minus the pre-write lead time, not a separately hardcoded cutoff. Tests may use consistent shorter values. Validate positive durations, activity interval below idle duration and pre-write lead time below maximum lifetime. Exact binding to framework expiration and frontend timing belongs in the bounded design; retain one authoritative setting per behavior.

The owner accepted the [permission-revocation boundary](concepts_brief.md#other-review-findings): check current permissions/ownership on each new protected request, while preserving already-authorized requests and specific durably accepted queued/running operations within their original bounds. Order acceptance against revocation atomically. Result/status/file reads and new user-triggered retries require current authority; resource lifecycle, sharing revocation, quotas/provider admission and settlement remain enforced. A permission change does not scan/cancel every open job or create reusable authority for unrelated actions. Session revoke-all/concurrent-login behavior remains separate.

## Accepted session-revocation meaning

**Accepted by the owner:** user/admin selected-session revocation ends the selected application session; revoke-all ends the target account's existing application sessions, including the current one when applicable. A later new Google sign-in is allowed and uses current application permissions. Revocation must not implicitly disable the account or restore removed grants; account suspension remains outside this scope.

Invalidate revoked sessions on the server so an old cookie cannot resume them. The [OWASP session-management guidance](https://cheatsheetseries.owasp.org/cheatsheets/Session_Management_Cheat_Sheet.html#session-expiration), checked 2026-11-05, supports server-side invalidation; it does not choose this application's future-login policy. This policy makes no promise to end Google's own session or require Google to prompt for a password. The tradeoff is that session revocation alone cannot keep someone out if they can authenticate again. Exact ordering of concurrent login/revoke-all, and already-admitted work interactions, remain bounded-design inputs; no concurrency protocol or new account-blocking feature is selected here.

## Proposed Google-configuration failure policy

**Next owner decision, pending acceptance:** when `app.auth.enabled=true`, missing/blank required Google configuration should stop startup with a clear operator error naming the missing setting without displaying secrets. When false, Google configuration is not required; the selected development workflow continues to start without it. Never change the configured sign-in mode automatically.

Distinguish incomplete local setup from a temporary Google outage after startup: the latter should give new sign-in attempts a useful retryable error while existing valid application sessions continue within their normal limits. Locally present settings do not prove Google accepts the secret or callback; those failures need useful login diagnostics and actual provider verification. This is a recommended application policy, not a claim about existing runtime behavior or an automatic guarantee from Spring.

[Spring Boot configuration validation](https://docs.spring.io/spring-boot/reference/features/external-config.html#features.external-config.typesafe-configuration-properties.validation), checked 2026-11-05, supports validating bound settings. Conditional binding, exact required keys, startup diagnostics and offline/provider checks belong in the bounded design. No configuration or code is changed by this proposal.

## Questions still requiring design or owner input

- Property binding, configuration validation and checks for the accepted YAML/profile defaults and explicit overrides.
- The server-established development account/principal and repeatable fixtures needed to exercise real ownership paths without Google login.
- Google configuration failure behavior; missing configuration must not silently enable the development shortcut. No local-to-Google product migration or account switcher is needed by this request.
- Atomic automatic account creation, returning-identity lookup and changed-email behavior; the no-extra-onboarding outcome is settled.
- The basic permission set, setup-command implementation/recovery policy, exact permission-management UI, any role grouping, session/device representation and concurrent revocation behavior. Independent per-session timers, user individual/all-session controls and administrator revocation are requested; provider-side revocation/recovery remains separate.
- The identity and proof needed for named project invitations. Google sign-in must not silently change accepted sharing behavior; development fixtures do not authorize access to real users' private resources.
- Exact API/CSRF/error/principal/module interfaces, persistence comparison, file boundaries, implementation phases and meaningful checks required by the roadmap.
- Actual Google project/client availability and a real-provider acceptance gate, separately from controlled offline tests and development-shortcut checks.

The development shortcut is a local workflow convenience; it does not require a separate product ownership/network-access design. After the owner explicitly confirmed readiness, six fresh Goldfish lenses completed sequentially at baseline `bb26f25`. The [concepts brief](concepts_brief.md) and [round-1 report](../../reviews/t3-identity/brainstorm-round-1/review.md) record the synthesis and unresolved choices. Findings are pending owner/design disposition; this is not formal design acceptance or implementation authority.
