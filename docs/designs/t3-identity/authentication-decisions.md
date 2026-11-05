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
| Sharing/publication | Create/revoke owned project-copy shares, accept authorized imports, publish/update/revoke owned chat snapshots | T16/T17/T19; whether these are automatic basic grants remains an owner question |

Administrative session revocation and permission assignment/revocation are separate from the basic set. No default permission grants access to another account's private resources. Permission checks remain combined with resource ownership; resource IDs and browser-supplied permission lists do not authorize access. The exact permission matrix must be tested on the backend, consistent with [OWASP authorization guidance](https://cheatsheetseries.owasp.org/cheatsheets/Authorization_Cheat_Sheet.html#enforce-least-privileges) (checked 2026-11-05).

The sharing row preserves existing requirements; leaving its default grants unresolved does not remove those features or authorize early implementation. Clarify that row before finalizing the basic catalog. Definitions of `read`/`write` must not add PDF editing, individual file deletion or unrestricted AI use to the accepted application scope.

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
