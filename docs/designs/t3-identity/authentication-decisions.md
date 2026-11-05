# T3 authentication intake decisions

* **Author:** igrgin
* **Date:** 2026-11-05
* **Issue:** [#3](https://github.com/igrgin/ai-knowledge-base/issues/3)
* **Status:** Research and EGM brainstorm intake; formal design and review pending.
* **Artifact directory:** `docs/designs/t3-identity/`
* **Workflow:** EGM with small fixes 0.2.1, `egm-brainstorm`.

## Owner decisions

The owner selected Google as the login provider in this chat: “Let's just do Google as the provider.” The latest clarification defines skipping sign-in as a developer pressing Run in IntelliJ and starting supporting services locally. This is a development-only shortcut, not a separate unauthenticated product mode. This clarification supersedes the earlier interpretation recorded in commit `da08ee3`.

Google is the sole application login provider. No application password login, separate admin login or admin portal is included. The repository and open issue audit found no existing application-admin requirement. Existing infrastructure-administrator references do not define one.

The owner requested Google research and an audit of plans, files and current issues before continuing brainstorming. This authorizes the research and planning reconciliation, not feature implementation or completion of issue #3. [The roadmap](../../roadmap.md) owns task authority; [the application design](../application.md) owns amended R1/R18 and C7.

## Research and audit

The [source-backed Google investigation](../../research/google-sign-in-investigation.md) covers existing-version fit, Google project/client setup, identity scopes, callback origins, persistent sessions, logout, offline verification and recipient identity. The [plan and issue audit](../../reviews/t3-identity/google-auth-plan-audit.md) records changed files, unchanged boundaries and exact proposed remote issue edits.

The recommended signed-in integration uses server-side OpenID Connect through Spring Security and PostgreSQL-backed application sessions. Keep the application's UUID identity independent of Google's email/profile. These mechanisms and their exact routes/schema still require the bounded T3 design; the provider decision alone does not select every research recommendation.

## Questions still requiring design or owner input

- The exact developer launch/profile configuration and safeguard that keeps the shortcut unavailable outside development.
- The server-established development account/principal and repeatable fixtures needed to exercise real ownership paths without Google login.
- Google configuration failure behavior; missing configuration must not silently enable the development shortcut. No local-to-Google product migration or account switcher is needed by this request.
- Whether a new Google identity creates an account immediately or needs an onboarding step; returning identity and changed-email behavior.
- Session lifetime, browser restart behavior, concurrent sessions, application logout and provider-side revocation/recovery limits.
- The identity and proof needed for named project invitations/restricted snapshots. Google sign-in must not silently change accepted sharing behavior; development fixtures do not authorize access to real users' private resources.
- Exact API/CSRF/error/principal/module interfaces, persistence comparison, file boundaries, implementation phases and meaningful checks required by the roadmap.
- Actual Google project/client availability and a real-provider acceptance gate, separately from controlled offline tests and development-shortcut checks.

The development shortcut is a local workflow convenience; it does not require a separate product ownership/network-access design. The concrete development principal and activation safeguards belong in the bounded T3 design. No readiness confirmation or Goldfish brainstorm review has occurred. Continue the open-ended clarification before requesting that gate.
