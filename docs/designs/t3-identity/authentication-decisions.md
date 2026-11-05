# T3 authentication intake decisions

* **Author:** igrgin
* **Date:** 2026-11-05
* **Issue:** [#3](https://github.com/igrgin/ai-knowledge-base/issues/3)
* **Status:** Research and EGM brainstorm intake; formal design and review pending.
* **Artifact directory:** `docs/designs/t3-identity/`
* **Workflow:** EGM with small fixes 0.2.1, `egm-brainstorm`.

## Owner decisions

The owner selected Google as the login provider. For developer startup from IntelliJ with supporting services running locally, sign-in is controlled by an application YAML boolean. It defaults to **off in `dev`** and **on in `prod`**. These are overridable defaults, not a rule forbidding explicit disablement in production. This supersedes the earlier hard development-only restriction in commit `4f9a737` and the separate local-product interpretation in `da08ee3`.

Use `app.auth.enabled` as the planned property name. The common default is true; `application-dev.yml` sets false and `application-prod.yml` sets true. When false, the server supplies a stable application account/principal without a Google login or Google configuration. Existing ownership predicates and quotas still apply. When true, Google sign-in is required; missing Google configuration must not silently change the boolean. The concrete no-sign-in account/fixtures and property binding are T3 design work. This is the accepted planned configuration, not an implemented setting yet.

Google is the sole application login provider; no separate password-based admin login is selected. The initial plans had no application-admin capability. The owner now requests session controls for users and administrators: administrators can revoke a selected user session or all of that user's sessions. Administrators must also be able to assign/revoke individual permissions in the app. Initial privileged-account setup, the exact permission catalog and admin UI design remain open; private-content access is not implied.

The owner requested Google research and an audit of plans, files and current issues before continuing brainstorming. This authorizes the research and planning reconciliation, not feature implementation or completion of issue #3. [The roadmap](../../roadmap.md) owns task authority; [the application design](../application.md) owns amended R1/R18 and C7.

On the first successful Google sign-in, create the application account automatically and let the user start creating projects immediately. No separate registration form, profile-completion step, invitation or approval is required. Returning sign-ins resolve the existing account; concurrent first sign-ins must not create duplicate accounts.

Session defaults are **30 minutes of inactivity** and **8 hours maximum from sign-in**, both configurable in YAML and enforced by the server. A valid session survives closing/reopening the browser and backend restarts within both limits; restarts do not reset its age. Expiry preserves the current view and unsent question while requesting sign-in again. Restore private view/draft state only for the same application account; these UI obligations apply as their owning features are introduced.

Each signed-in session has its own inactivity countdown and maximum lifetime; activity on another device does not extend it. Users can view/manage their sessions from settings or profile and revoke an individual session or all sessions. Ordinary sign-out affects the current session; the explicit all-sessions action includes the current session. Expiry happens independently per session. Administrator revocation must use an explicit permission and take effect across web instances; it does not grant access to the user's private documents/chats. Device/session representation, safe display metadata and concurrent-request revocation behavior need bounded design.

The owner requires an explicit permission system; a role-only authorization model is insufficient. Backend actions must check the needed permission together with resource ownership. Google establishes identity; application permissions and their assignments are managed by this application. Administrators assign/revoke individual user permissions through the app, and new accounts receive basic permissions automatically. Optional role grouping is not a substitute for those controls. The basic set must retain immediate project creation; its remaining actions need clarification. Disabling sign-in does not disable permission checks: the server-established no-sign-in account uses the same authorization paths. Permission catalog, basic defaults, initial privileged-account setup, exact UI and cross-instance propagation need the bounded design; no general policy language or deny/override hierarchy is selected.

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
| Publish restricted chat snapshots | Basic by default: explicitly publish/update/revoke snapshots for authorized recipients | T17; transcript only, no PDF/project/AI access |
| Public/anonymous chat snapshots | Excluded entirely by owner decision; not a grantable permission | T17 must omit the option/route; administrators cannot enable it |

Administrative session revocation and permission assignment/revocation are separate from the basic set. No default permission grants access to another account's private resources. Permission checks remain combined with resource ownership; resource IDs and browser-supplied permission lists do not authorize access. The exact permission matrix must be tested on the backend, consistent with [OWASP authorization guidance](https://cheatsheetseries.owasp.org/cheatsheets/Authorization_Cheat_Sheet.html#enforce-least-privileges) (checked 2026-11-05).

The remaining private-sharing defaults above are recommendations pending owner acceptance. The owner explicitly rejected public/anonymous chat publishing, including an administrator-granted exception: no such permission or public-view route is supported. This supersedes the earlier proposal in `2996c7e` and amends R27/T17. No anonymous account/login workflow is introduced. Authenticated restricted snapshots remain in scope; project import remains an independently owned copy, and revoking a share stops future imports without erasing accepted copies. Every share/publish action remains explicit.

Alternative rejected by the owner: public snapshots enabled through an extra administrator-granted permission. The owner removed anonymous/public viewing entirely. Public URLs would not technically require anonymous accounts; this exclusion is a product decision, separate from Spring's anonymous security-context support. [Spring anonymous authentication](https://docs.spring.io/spring-security/reference/servlet/authentication/anonymous.html) Keep ordinary public login/static/health endpoints and the configured developer no-sign-in principal separate from that product decision; the development principal still satisfies ownership/recipient checks. Definitions of `read`/`write` do not add PDF editing, individual file deletion or unlimited AI use.

## Proposed initial administrator setup

The owner suggested manual internal assignment of the first administrator and asked for an alternative. Recommendation, **pending owner acceptance**: provide a one-time operator-run setup command that grants the initial permission-management and administrator session-management permissions to an explicitly selected existing application account UUID.

1. The operator signs in through Google normally, creating an ordinary account with basic permissions.
2. From the trusted application host/environment, the operator invokes the internal setup command with that account UUID. Exact command syntax is a design output; no such command exists yet.
3. The application verifies the account, grants the defined initial permissions transactionally, and records the bootstrap completion and grant evidence without tokens/private content. First successful bootstrap consumes the setup facility; concurrent/duplicate attempts cannot grant a different account.
4. Subsequent assignments/revocations happen in the permission-management UI under the accepted permissions. Ordinary startup must never replay the initial grant or undo a later revocation.

This is an operator action rather than a public setup endpoint. Normal users cannot invoke it through the application UI/API, and being the first registrant must not confer administrator permissions. Do not create a separate local-password admin account or trust a matching email string as proof of identity. The concrete trusted-host invocation, transaction/concurrency rules and failure recovery need bounded design. A later last-administrator recovery procedure is a separate unresolved question; it must not be an always-on bootstrap backdoor.

This recommendation replaces neither the owner's Google login decision nor in-app permission management. It is a proposed way to establish the first permission manager without requiring ad hoc SQL edits, automatic privilege assignment on every startup or a generic role-only seed.

## Research and audit

The [source-backed Google investigation](../../research/google-sign-in-investigation.md) covers existing-version fit, Google project/client setup, identity scopes, callback origins, persistent sessions, logout, offline verification and recipient identity. The [plan and issue audit](../../reviews/t3-identity/google-auth-plan-audit.md) records changed files, unchanged boundaries and exact proposed remote issue edits.

The recommended signed-in integration uses server-side OpenID Connect through Spring Security and PostgreSQL-backed application sessions. Keep the application's UUID identity independent of Google's email/profile. These mechanisms and their exact routes/schema still require the bounded T3 design; the provider decision alone does not select every research recommendation.

## Questions still requiring design or owner input

- Property binding, configuration validation and checks for the accepted YAML/profile defaults and explicit overrides.
- The server-established development account/principal and repeatable fixtures needed to exercise real ownership paths without Google login.
- Google configuration failure behavior; missing configuration must not silently enable the development shortcut. No local-to-Google product migration or account switcher is needed by this request.
- Atomic automatic account creation, returning-identity lookup and changed-email behavior; the no-extra-onboarding outcome is settled.
- The basic permission set, initial administrator/bootstrap procedure, exact permission-management UI, any role grouping, session/device representation and concurrent revocation behavior. Independent per-session timers, user individual/all-session controls and administrator revocation are requested; provider-side revocation/recovery remains separate.
- The identity and proof needed for named project invitations/restricted snapshots. Google sign-in must not silently change accepted sharing behavior; development fixtures do not authorize access to real users' private resources.
- Exact API/CSRF/error/principal/module interfaces, persistence comparison, file boundaries, implementation phases and meaningful checks required by the roadmap.
- Actual Google project/client availability and a real-provider acceptance gate, separately from controlled offline tests and development-shortcut checks.

The development shortcut is a local workflow convenience; it does not require a separate product ownership/network-access design. The concrete development principal and configuration behavior belong in the bounded T3 design. No readiness confirmation or Goldfish brainstorm review has occurred. Continue the open-ended clarification before requesting that gate.
