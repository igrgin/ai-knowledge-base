# T3 authentication intake decisions

* **Author:** igrgin
* **Date:** 2026-11-05
* **Issue:** [#3](https://github.com/igrgin/ai-knowledge-base/issues/3)
* **Status:** Research and EGM brainstorm intake; formal design and review pending.
* **Artifact directory:** `docs/designs/t3-identity/`
* **Workflow:** EGM with small fixes 0.2.1, `egm-brainstorm`.

## Owner decisions

The owner selected Google as the login provider in this chat: “Let's just do Google as the provider.” Normal local startup/use of the AI Knowledge Base must also work without sign-in. The later clarification means running the application locally, not merely running automated tests or a developer-only shortcut.

Google is the sole provider wherever sign-in is required. No application password login, separate admin login or admin portal is included. The repository and open issue audit found no existing application-admin requirement. Existing infrastructure-administrator references do not define one.

The owner requested Google research and an audit of plans, files and current issues before continuing brainstorming. This authorizes the research and planning reconciliation, not feature implementation or completion of issue #3. [The roadmap](../../roadmap.md) owns task authority; [the application design](../application.md) owns amended R1/R2/R18 and C7.

## Research and audit

The [source-backed Google investigation](../../research/google-sign-in-investigation.md) covers existing-version fit, Google project/client setup, identity scopes, callback origins, persistent sessions, logout, offline verification and recipient identity. The [plan and issue audit](../../reviews/t3-identity/google-auth-plan-audit.md) records changed files, unchanged boundaries and exact proposed remote issue edits.

The recommended signed-in integration uses server-side OpenID Connect through Spring Security and PostgreSQL-backed application sessions. Keep the application's UUID identity independent of Google's email/profile. These mechanisms and their exact routes/schema still require the bounded T3 design; the provider decision alone does not select every research recommendation.

## Questions still requiring design or owner input

- Whether local access is limited to the same computer or intentionally available to other devices on a local network.
- Which persisted application identity owns local content, whether multiple local identities exist, and what privacy boundary the local mode promises.
- How operators explicitly select access modes, how configuration errors fail, and whether/how local data can later be associated with a Google account. No automatic transfer or account merge is selected.
- Whether a new Google identity creates an account immediately or needs an onboarding step; returning identity and changed-email behavior.
- Session lifetime, browser restart behavior, concurrent sessions, application logout and provider-side revocation/recovery limits.
- The identity and proof needed for named project invitations/restricted snapshots, and whether those features are available in local mode. Google sign-in must not silently change accepted sharing behavior.
- Exact API/CSRF/error/principal/module interfaces, persistence comparison, file boundaries, implementation phases and meaningful checks required by the roadmap.
- Actual Google project/client availability and a real-provider acceptance gate, separately from controlled offline tests and local-mode demonstration.

The local-mode ownership and network answers come first: otherwise “private account” has no defined meaning for that mode. No readiness confirmation or Goldfish brainstorm review has occurred. Continue the open-ended clarification before requesting that gate.
