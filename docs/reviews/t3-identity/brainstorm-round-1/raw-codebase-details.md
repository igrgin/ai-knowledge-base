Required reads complete; none missing. Read-only brainstorm at verified HEAD `bb26f2571b6d6787d86fa29ed1f4f8a55a5dfda4`, branch `3-register-log-in-and-retain-authenticated-sessions`. No writes, builds, servers or subagents.

Full reads:

- Persona: `/Users/igrgin/.codex/plugins/cache/egm-local/egm/0.2.1/agents/codebase_details.md`.
- Both standards under the same plugin’s `resources/document_standards/`: `zoom-out-standard.md`, `heilmeier-standard.md`.
- Repository target and all six sources: `docs/designs/t3-identity/authentication-decisions.md` (82 lines), `docs/designs/application.md` (618), `docs/roadmap.md` (253), `docs/contracts/k0-foundation.md` (70), `EGM.md` (70), `docs/research/google-sign-in-investigation.md` (133). Repository root: `/Users/igrgin/Documents/code/apps/ai-knowledge-base`.

Commands/evidence: `cat` for persona; `nl -ba` for target, standards, K0 and EGM; application read with `nl -ba … | sed -n '1,160p'`, `'161,320p'`, `'321,480p'`, `'481,640p'`, and `'531,618p'`; roadmap with `'1,130p'` and `'131,253p'`; Google investigation with `'1,79p'` and `'80,133p'`. Initial aggregate output was truncated; bounded follow-up reads completed it. Baseline checked using `git rev-parse HEAD` and `git branch --show-current`. Bounded implementation inspection used `rg --files backend/src/main`, targeted `rg -n`, and numbered reads of the request filter, MVC handler, web package contract, three application YAML files and backend POM.

No Blocker found. Four useful concept decisions follow.

1. **Major — Define what session “activity” means before promising meaningful inactivity expiry.**

   **Evidence:** Intake lines 22–24 select independent inactivity timers; application design line 322 proposes visible-job polling approximately every two seconds.

   **Operational risk:** If every authenticated request refreshes idle age, background polling can keep an unattended browser session alive indefinitely. Operators then cannot distinguish incorrect expiry from intended renewal using “last request” timestamps.

   **Recommendation / mitigation requirement:** Define whether background polling, session-list refresh and other automatic reads count as activity. Record the chosen last-activity basis and a bounded expiry reason such as idle, absolute or revoked; verify it with an unattended polling tab and two independent sessions. Preserve the selected 30-minute/8-hour values.

   **Owner decision:** Whether “inactivity” means absence of qualifying human interaction or absence of authenticated requests. Engineering then specifies the trusted, server-enforced renewal mechanism.

2. **Major — Extend bootstrap evidence into an explicit audit policy for subsequent administrative changes.**

   **Evidence:** Intake lines 52–57 require transactional bootstrap completion/grant evidence. Lines 16, 24 and 26 authorize later permission changes and administrator session revocations, but provide no corresponding audit commitment. Lines 59–63 add duplicate-bootstrap and last-manager rejection cases.

   **Operational risk:** After an accidental or disputed grant/revocation, there may be no durable way to establish who changed whose authority, which change committed, or why a last-manager/bootstrap attempt failed. Ordinary application logs alone would not establish transaction outcome.

   **Recommendation / mitigation requirement:** The concepts brief should require minimal durable audit evidence for successful privilege mutations and administrative session revocations, tied to their committed database change. Include actor/target account UUIDs, action, permission delta or noncredential session reference, timestamp, outcome and request/command correlation. Record rejected sensitive attempts through a bounded safe channel. Exclude tokens, cookies and private content.

   **Owner decision:** Audit retention, who can inspect it, and whether successful administrative changes require audit persistence. This needs no general audit dashboard or hosted logging system.

3. **Major — Require a small, safe failure taxonomy and login-journey correlation for operational triage.**

   **Evidence:** Intake lines 75, 79–80 leave configuration failures, security errors and real-provider acceptance open. Research lines 107–110 identify protocol, session-store and configuration failure cases. Existing `RequestIdFilter.java:22–34` creates an ID for each HTTP attempt; `ApiExceptionHandler.java:70–72` logs only request ID and exception class. K0 lines 38–42 explicitly exclude future security-filter coverage. Current `application.yml:23–25` readiness checks application readiness and the database.

   **Operational risk:** A report of “Google login stopped working” can arise from user cancellation, callback validation, provider connectivity, account provisioning or session persistence. An initiation and callback are separate requests, potentially on separate instances; the existing request ID alone does not connect that journey. Broad exception logging would conflict with the established safe-logging boundary.

   **Recommendation / mitigation requirement:** Carry existing request IDs through security responses and add a separate opaque login-attempt correlation value for server-side initiation/callback events. Define bounded stage/reason/outcome events and low-cardinality counts/timings. Keep raw callback query strings, authorization codes, OAuth state/nonce, tokens and session credentials out of telemetry. Provide a short diagnostic path that distinguishes configuration, Google connectivity and session-store failures. Decide readiness behavior separately from new-login availability so an external outage does not automatically imply every established application session is unusable. A tracing backend is unnecessary for this local slice.

   **Owner decision:** Startup failure versus explicit unavailable-login state remains the existing product choice. Engineering owns the safe reason vocabulary and event fields; no new external dependency or availability promise is implied.

4. **Nit — Make the effective authentication mode visible to operators without restricting accepted overrides.**

   **Evidence:** Intake lines 12–14 expressly allow `app.auth.enabled=false` even as an explicit production override. Current profile YAML files contain no implemented authentication setting; the document correctly marks it planned.

   **Operational risk:** Two instances with different effective mode/profile settings can produce confusing account/session behavior, and an intentional override may be mistaken for broken Google configuration.

   **Recommendation / mitigation requirement:** Emit one sanitized startup diagnostic identifying instance, active profiles and effective authentication mode, plus whether required Google configuration passed validation. Do not emit secrets or the development principal’s credentials. Include this diagnostic in the future triage instructions.

   **Owner decision:** None needed to implement a diagnostic. Any proposal to forbid production disablement would reverse an accepted owner choice and must return explicitly as a tradeoff.

These are inputs for `egm-draft`, not findings that the brainstorm document lacks implementation-ready files, schemas or headers.
