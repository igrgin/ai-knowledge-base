# Google authentication plan and issue audit

Date: **2026-11-05, Europe/Zagreb**. Scope: existing [issue #3](https://github.com/igrgin/ai-knowledge-base/issues/3), owner-requested research and planning audit before resuming EGM brainstorming. This is the integrator's reconciliation record, not a Goldfish review or completed feature design.

## Decisions and evidence

The owner selected Google as the sole provider where sign-in is required and normal local AI Knowledge Base startup/use without sign-in. The local flow is ordinary application use, not an automated-test-only or developer-only shortcut. No admin portal or application password login is included. [Intake decisions](../../designs/t3-identity/authentication-decisions.md) separate accepted choices from open questions; the [Google investigation](../../research/google-sign-in-investigation.md) cites primary sources checked on this date.

The repository was initially on `egm-foundation-bootstrap` at `e095a9161f3382aa9ee1a83db6198f3f93f94f76`. Fetching the remote showed merged main at `e85f9de` with the same tracked tree. Issue #24 is closed/completed. The issue-specific local branch is `3-register-log-in-and-retain-authenticated-sessions`, created from that main baseline after checking current branches and historical PR search for that name. The existing issue/title association is retained. The pre-existing untracked `docs/research/langchain-architecture-assessment.md` was not edited or included in this work.

## Research conclusions that affect design

- Spring Security's Google OIDC login fits the existing server/session direction. Google credentials, callback origins and consent configuration remain unverified; no dependencies or configuration were installed.
- Keep the application account UUID separate from Google identity/profile. Google's stable subject is suitable for a provider mapping; email text must not merge accounts or transfer existing private data. The exact mapping and provisioning mechanism still need T3 design.
- Basic Google identity scopes have a documented exception to common Testing-mode restrictions. Do not carry generic seven-day/100-listed-tester assumptions into setup without checking the actual scopes and console state.
- Google's assurance of a third-party email claim is insufficient to silently define every named-recipient access rule. T16/T17 must agree recipient identity/proof without dropping named invitations or changing registered-link/public-transcript semantics.
- Distinguish local no-sign-in use, controlled offline tests of signed-in mode, and actual Google acceptance. None proves the other. Local use must not need Google credentials; Google-mode configuration errors must not silently enable local access.

These are conclusions and recommendations from the linked research, not a blanket acceptance of every implementation mechanism. Local identity/isolation, mode activation and network reach remain unresolved.

## Repository audit and reconciliation

Searched repository documentation, production/test sources, configuration and issue bodies for account, registration, login, password, email, session, Google/OIDC, admin, privacy and offline assumptions. Read the governing design/workflow/roadmap/contracts, local startup instructions and affected runtime/proxy configuration. Reviewed all **18 open issues (#3–#20)**. The six substantively affected issues have no existing comments as of this audit. Historical research/review records were treated as dated evidence rather than rewritten as current requirements.

| File or boundary | Result |
| --- | --- |
| [Application design](../../designs/application.md) | Replaced the email/password proposal in R1/C7, session direction, API/data inventory and T3. Amended R2/R18 and access-mode wording; added unresolved local/recipient checkpoints and both-mode verification/handoff obligations. Removed implied password-hash storage and an assumed password-register endpoint. |
| [Roadmap](../../roadmap.md) | Recorded active T3 research/brainstorm authority and pending implementation, the existing local issue branch, Google/local scope, identity ownership and R1/R18/C7 coverage. Reconciled the closed/merged #24 status. Dependencies, single-writer limit and feature completion gates remain in force. |
| [K0](../../contracts/k0-foundation.md) | Added a future-target clarification: available K0 v1 has no implemented local actor or auth mode. T3 must reconcile its concrete local identity/access contract before consumers rely on it. No gate/version/schema was published. |
| [Local development](../../local-development.md) | Added a clearly pending access-mode note. Existing commands and Spring profiles are current foundation behavior; there is no new startup flag or implemented Google/local switch. |
| [Root architecture map](../../../EGM.md) | Updated T3 planning status and linked its intake decisions; continues to describe only implemented T1/T2 behavior. |
| [Design sitemap](../../designs/SITEMAP.md) and [research index](../../research/README.md) | Registered the real intake record and source-backed research, with their incomplete design/implementation status. |
| [Intake decisions](../../designs/t3-identity/authentication-decisions.md) | Captured owner choices, superseded assumptions and the next clarification questions without generating an unreviewed final concepts brief. |
| [README](../../../README.md), [contract index](../../contracts/README.md), package EGM maps | Existing descriptions of implemented foundation behavior remain accurate. No new provider gate, admin role or login implementation is present; no change needed now. |
| Java/React source, migrations, POMs, lockfiles, `.env.example`, `application*.yml`, Compose, Vite proxy, tests and CI | No auth implementation to replace. Later T3 design must name exact changes, including API/auth proxy paths beyond today's `/actuator` proxy and Flyway-owned account/session schema. No speculative credentials, startup flags, migrations or runtime behavior were added by the audit. |
| Historical reviews and earlier research | Preserved their dates and past decisions. Current documents and the new research mark supersession. Database passwords and password-encrypted PDF handling are unrelated to user login and remain unchanged. |

## Current issue changes

No remote issue edits or comments were published. Each linked draft contains its destination, proposed title and **complete replacement body**; existing native metadata, relationships and comments are to be preserved. Publish only after the governing amendments are available remotely. The latest owner authority belongs in T3; the other issues retain their pending implementation state.

All 18 issue bodies still have a cutover-candidate banner saying #24 awaits review/merge. Each draft replaces just that stale banner unless substantive changes are identified below. Older pinned source links are retained as historical context; this is not a reason to rewrite unrelated scope or authorizations.

| Issue | Needed change | Exact draft |
| --- | --- | --- |
| [#3](https://github.com/igrgin/ai-knowledge-base/issues/3) | Retitle for Google/local scope; replace password authentication inventory/tests; record research-only authority and unresolved local/identity choices. | [#3 draft](issue-drafts/3.md) |
| [#4](https://github.com/igrgin/ai-knowledge-base/issues/4) | Stale banner only. Ownership consumes T3's account contract; no project redesign needed now. | [#4 draft](issue-drafts/4.md) |
| [#5](https://github.com/igrgin/ai-knowledge-base/issues/5) | Stale banner only. Spending/account limits stay attached to T3 account identity; no Google-specific ledger needed. | [#5 draft](issue-drafts/5.md) |
| [#6](https://github.com/igrgin/ai-knowledge-base/issues/6) | Stale banner only. Keep owner-authorized upload/storage and parser scope. | [#6 draft](issue-drafts/6.md) |
| [#7](https://github.com/igrgin/ai-knowledge-base/issues/7) | Stale banner only. Indexing consumes existing account/resource authority. | [#7 draft](issue-drafts/7.md) |
| [#8](https://github.com/igrgin/ai-knowledge-base/issues/8) | Stale banner only. Saved chats keep application account ownership. | [#8 draft](issue-drafts/8.md) |
| [#9](https://github.com/igrgin/ai-knowledge-base/issues/9) | Stale banner only. Answer ownership and model accounting stay provider-independent. | [#9 draft](issue-drafts/9.md) |
| [#10](https://github.com/igrgin/ai-knowledge-base/issues/10) | Stale banner only. Citation/file access keeps T3/T4 account ownership. | [#10 draft](issue-drafts/10.md) |
| [#11](https://github.com/igrgin/ai-knowledge-base/issues/11) | Stale banner only. Project deletion/recovery is not Google account deletion. | [#11 draft](issue-drafts/11.md) |
| [#12](https://github.com/igrgin/ai-knowledge-base/issues/12) | Integrate both access modes and Google return/cancellation/errors; keep T3 as auth owner. | [#12 draft](issue-drafts/12.md) |
| [#13](https://github.com/igrgin/ai-knowledge-base/issues/13) | Separate local-mode checks from signed-in isolation/session/protocol checks; keep live Google outside ordinary offline CI. | [#13 draft](issue-drafts/13.md) |
| [#14](https://github.com/igrgin/ai-knowledge-base/issues/14) | Stale banner only. Google identity access and real paid AI authority are separate. | [#14 draft](issue-drafts/14.md) |
| [#15](https://github.com/igrgin/ai-knowledge-base/issues/15) | Document local no-sign-in startup separately from Google setup, account-access limits and real-login evidence. | [#15 draft](issue-drafts/15.md) |
| [#16](https://github.com/igrgin/ai-knowledge-base/issues/16) | Add recipient identity/proof and local-mode import/distribution design checkpoint without changing copy semantics. | [#16 draft](issue-drafts/16.md) |
| [#17](https://github.com/igrgin/ai-knowledge-base/issues/17) | Add restricted-recipient/local-mode publication checkpoint; preserve intentionally anonymous public transcripts. | [#17 draft](issue-drafts/17.md) |
| [#18](https://github.com/igrgin/ai-knowledge-base/issues/18) | Stale banner only. Keep account-authorized originals separate from retrieval eligibility. | [#18 draft](issue-drafts/18.md) |
| [#19](https://github.com/igrgin/ai-knowledge-base/issues/19) | Stale banner only. Copied data uses independent destination account/resource IDs. | [#19 draft](issue-drafts/19.md) |
| [#20](https://github.com/igrgin/ai-knowledge-base/issues/20) | Stale banner only. Source eligibility consumes application account authority. | [#20 draft](issue-drafts/20.md) |

No new admin issue or separate duplicate authentication issue is needed. No issue is complete or eligible for closure from this research.

## Verification and remaining work

Validation passed: `git diff --check`; 28 changed/new Markdown files with 150 local links/anchors and terminal newlines; all 18 issue drafts checked for retained ownership/scheduling sections, with exact banner-only comparison for the 12 otherwise unchanged bodies. The stale-auth-assumption search found no current email/password proposal outside dated historical records. There are no runtime changes for Java/browser suites to validate. No Google account/project configuration, real login, credentials, network binding, new migration or paid call has been tested.

Continue brainstorming with the local access boundary and persisted actor first. Then settle Google provisioning/session behavior and the recipient identity contract. Request the installed EGM readiness gate before Goldfish concept review; no review or design acceptance is inferred from the research scout.
