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

Google is the sole application login provider. No application password login, separate admin login or admin portal is included. The repository and open issue audit found no existing application-admin requirement. Existing infrastructure-administrator references do not define one.

The owner requested Google research and an audit of plans, files and current issues before continuing brainstorming. This authorizes the research and planning reconciliation, not feature implementation or completion of issue #3. [The roadmap](../../roadmap.md) owns task authority; [the application design](../application.md) owns amended R1/R18 and C7.

On the first successful Google sign-in, create the application account automatically and let the user start creating projects immediately. No separate registration form, profile-completion step, invitation or approval is required. Returning sign-ins resolve the existing account; concurrent first sign-ins must not create duplicate accounts.

Session defaults are **30 minutes of inactivity** and **8 hours maximum from sign-in**, both configurable in YAML and enforced by the server. A valid session survives closing/reopening the browser and backend restarts within both limits; restarts do not reset its age. Expiry preserves the current view and unsent question while requesting sign-in again. Restore private view/draft state only for the same application account; these UI obligations apply as their owning features are introduced.

## Research and audit

The [source-backed Google investigation](../../research/google-sign-in-investigation.md) covers existing-version fit, Google project/client setup, identity scopes, callback origins, persistent sessions, logout, offline verification and recipient identity. The [plan and issue audit](../../reviews/t3-identity/google-auth-plan-audit.md) records changed files, unchanged boundaries and exact proposed remote issue edits.

The recommended signed-in integration uses server-side OpenID Connect through Spring Security and PostgreSQL-backed application sessions. Keep the application's UUID identity independent of Google's email/profile. These mechanisms and their exact routes/schema still require the bounded T3 design; the provider decision alone does not select every research recommendation.

## Questions still requiring design or owner input

- Property binding, configuration validation and checks for the accepted YAML/profile defaults and explicit overrides.
- The server-established development account/principal and repeatable fixtures needed to exercise real ownership paths without Google login.
- Google configuration failure behavior; missing configuration must not silently enable the development shortcut. No local-to-Google product migration or account switcher is needed by this request.
- Atomic automatic account creation, returning-identity lookup and changed-email behavior; the no-extra-onboarding outcome is settled.
- Concurrent sessions, the effect of logout on other browsers/devices, and provider-side revocation/recovery limits. The 30-minute idle/8-hour maximum defaults and restart/browser continuity are settled.
- The identity and proof needed for named project invitations/restricted snapshots. Google sign-in must not silently change accepted sharing behavior; development fixtures do not authorize access to real users' private resources.
- Exact API/CSRF/error/principal/module interfaces, persistence comparison, file boundaries, implementation phases and meaningful checks required by the roadmap.
- Actual Google project/client availability and a real-provider acceptance gate, separately from controlled offline tests and development-shortcut checks.

The development shortcut is a local workflow convenience; it does not require a separate product ownership/network-access design. The concrete development principal and configuration behavior belong in the bounded T3 design. No readiness confirmation or Goldfish brainstorm review has occurred. Continue the open-ended clarification before requesting that gate.
