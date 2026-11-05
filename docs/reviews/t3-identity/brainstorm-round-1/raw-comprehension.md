Completed all required full reads. No missing sources, writes, builds, servers, mutations or subagents. Findings concern concept choices and `egm-draft` safeguards; this is not a formal readiness audit.

Baseline verified: `HEAD=bb26f2571b6d6787d86fa29ed1f4f8a55a5dfda4`, branch `3-register-log-in-and-retain-authenticated-sessions`. Required tracked sources have no diff from that baseline. The excluded untracked research file was not read.

1. **Major — Define inactivity against automatic polling.**
   **Dimension:** UX & Developer Ergonomics.
   [Intake lines 22–24](/Users/igrgin/Documents/code/apps/ai-knowledge-base/docs/designs/t3-identity/authentication-decisions.md:22) select independent 30-minute inactivity limits, while [application line 322](/Users/igrgin/Documents/code/apps/ai-knowledge-base/docs/designs/application.md:322) requires visible-job polling approximately every two seconds. The concept never defines what counts as activity. If every authenticated request refreshes inactivity, an unattended polling tab remains active; if only explicit interaction counts, reading a long PDF may expire unexpectedly.
   **Risk:** The delivered behavior can satisfy a technical timeout while contradicting what users understand as inactivity.
   **Ergonomic requirement/recommendation:** Before drafting, define whether activity means application requests or deliberate user interaction, and state how polling, passive reading and multiple tabs in one session behave. Verify the chosen policy with an unattended polling tab and a long reading session.
   **Owner decision:** Choose the intended meaning of inactivity. Keep the accepted durations and independent-session rule.

2. **Major — Permission removal needs a different recovery from session expiry.**
   **Dimension:** UX & Developer Ergonomics.
   [Intake line 22](/Users/igrgin/Documents/code/apps/ai-knowledge-base/docs/designs/t3-identity/authentication-decisions.md:22) specifies same-account reauthentication with preserved view/draft; [line 26](/Users/igrgin/Documents/code/apps/ai-knowledge-base/docs/designs/t3-identity/authentication-decisions.md:26) permits live administrative removal of individual permissions. Those states require different user actions, but the concept leaves their interaction entirely under “exact API/CSRF/error” design work. Signing in again cannot repair a removed permission. Likewise, a rejected write cannot safely be assumed either completed or eligible for automatic replay.
   **Risk:** A common “authentication failed” recovery can create login loops, misleading retry buttons or duplicate submissions when later features integrate.
   **Ergonomic requirement/recommendation:** Carry an explicit behavior distinction into the brief: expired/revoked session requests sign-in; authenticated permission denial explains the unavailable action without requesting another login; ownership denial retains K0’s non-disclosing response. Preserve the unsent draft where permitted, reconcile an already-submitted operation before resubmission, and restore private state only after confirming the same account. Exact routes/codes belong in `egm-draft`. [K0 already requires codes for client logic and T3-owned filter responses](/Users/igrgin/Documents/code/apps/ai-knowledge-base/docs/contracts/k0-foundation.md:40).
   **Owner decision:** Whether a revoked permission leaves the affected action visibly disabled with an explanation or removes it from navigation. The distinct recovery paths are a design safeguard, not a reason to reverse explicit permissions.

3. **Nit — Make the first administrator’s UUID discoverable without database inspection.**
   **Dimension:** UX & Developer Ergonomics.
   [Intake lines 52–59](/Users/igrgin/Documents/code/apps/ai-knowledge-base/docs/designs/t3-identity/authentication-decisions.md:52) select a one-time command targeting an existing account UUID, but the operator journey jumps from Google sign-in to possessing that UUID. The current shell exposes only readiness information; [its complete UI](/Users/igrgin/Documents/code/apps/ai-knowledge-base/frontend/src/main.tsx:16) has no account information.
   **Risk:** Setup instructions may quietly require ad hoc SQL or email-based account guessing, undermining the stated setup ergonomics.
   **Ergonomic requirement/recommendation:** Include a supported way for the signed-in operator to copy their application account UUID, then make command output identify the resolved target and unambiguously distinguish successful bootstrap, already consumed and invalid account. This needs no general account directory or public lookup.
   **Owner decision:** No new product choice is essential; settle the narrow self-identification workflow during drafting.

Read evidence:

- Full persona: `/Users/igrgin/.codex/plugins/cache/egm-local/egm/0.2.1/agents/comprehension.md` via `cat`.
- Full target: `nl -ba docs/designs/t3-identity/authentication-decisions.md`.
- Full required sources: `docs/designs/application.md` (bounded reads covering lines 1–618), `docs/roadmap.md` (1–253), `docs/contracts/k0-foundation.md`, `EGM.md`, and `docs/research/google-sign-in-investigation.md` (1–133).
- Both full standards read via `cat`: `resources/document_standards/zoom-out-standard.md` and `heilmeier-standard.md` under the installed EGM 0.2.1 package.
- Also read the full repository EGM workflow, installed brainstorm skill and Codex integration guide.
- Bounded implementation evidence: `rg --files frontend/src backend/src/main/java backend/src/main/resources`; numbered reads of `frontend/src/main.tsx`, `frontend/vite.config.ts`, `shared/ErrorCode.java` and all three current application YAML files.
- Baseline commands: `git status --short`, `git rev-parse HEAD`, `git branch --show-current`, and `git diff --name-only bb26f2571b6d6787d86fa29ed1f4f8a55a5dfda4 -- <six required tracked documents>`.

No new external factual claims were needed.
