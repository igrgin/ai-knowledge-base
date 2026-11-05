Required full reads complete; none missing. I read the structural persona in BRAINSTORM mode, the full target, application design, roadmap, K0, root EGM map, Google investigation, and both standards. I also read remote-work guidance before inspecting Git. Confirmed HEAD `bb26f2571b6d6787d86fa29ed1f4f8a55a5dfda4`, branch `3-register-log-in-and-retain-authenticated-sessions`. No writes, builds, servers, mutations, or subagents.

These are concept risks to settle through brainstorming and bounded drafting, not failures to supply a final-design template.

1. **Major — Long-Term Tech Debt & Maintainability: define how basic permissions evolve as features arrive.**

   **Evidence:** `authentication-decisions.md:26` grants basic permissions automatically to new accounts; `:30–41` distributes the proposed catalog across future slices. `application.md:165` explicitly makes those slices introduce their own action permissions. `authentication-decisions.md:57` requires later revocations to survive ordinary startup.

   **Risk:** Granting defaults only during registration makes existing users lose access to newly delivered ordinary features. Repeatedly synchronizing every account with a current “basic” list risks restoring permissions an administrator deliberately removed. Leaving each slice to invent its own backfill and catalog behavior creates recurring migration and authorization debt.

   **Maintainability requirement / egm-draft safeguard:** Establish one small permission-catalog evolution contract: stable identifiers, feature ownership, treatment of existing accounts when a basic action is introduced, and preservation of deliberate assignments/revocations across upgrades. Verify a revoked grant remains revoked after restart and catalog reconciliation. This does not require roles, a policy language, or a deny hierarchy.

   **Owner decision:** Should new basic actions automatically become available to existing accounts, or require an administrator grant? Keep the selected explicit-permission model; clarify this upgrade behavior.

2. **Major — Long-Term Tech Debt & Maintainability: “inactivity” needs a product definition before polling features inherit it.**

   **Evidence:** `authentication-decisions.md:22–24` selects per-session inactivity expiry, while `application.md:322` proposes visible-library status polling approximately every two seconds. Current `frontend/src/main.tsx:7–15` has only a one-shot readiness request, so no established activity policy exists to reuse.

   **Risk:** If every authenticated request counts as activity, unattended polling can keep an idle session alive until its absolute deadline. If only explicit interaction counts, that decision needs a shared mechanism; otherwise each future feature can accidentally extend or prematurely expire sessions. Multiple tabs sharing one session also need consistent behavior.

   **Maintainability requirement / egm-draft safeguard:** Define qualifying activity once, distinguish automatic refresh/status traffic from qualifying requests if necessary, and centralize enforcement. Carry a focused future integration scenario: an unattended polling page reaches idle expiry; qualifying activity in that same session extends it; activity in a separate session does not. Avoid independent feature timers becoming security authority.

   **Owner decision:** Does “30 minutes of inactivity” mean no authenticated HTTP requests, or no meaningful user activity? Both are implementable tradeoffs; the accepted duration need not change.

3. **Major — Long-Term Tech Debt & Maintainability: separate permission revocation from the lifecycle of already accepted durable work.**

   **Evidence:** `authentication-decisions.md:24–26` requests cross-instance session revocation and backend permission checks. `application.md:60` requires durable PDF, answer, and purge work independent of the receiving web process; `:189` requires lifecycle checks before calls/publication; `:468` requires conservative handling of dispatched or unknown paid calls. K0 `:50` already separates claim/reserve and finalize transactions.

   **Risk:** A literal “check permission for every backend action” can couple workers to an expired HTTP session or a serialized permission snapshot. Conversely, checking only at initial request admission may let queued work begin after the relevant permission is removed. Different feature owners could resolve this differently, especially for charged AI work and deletion/purge.

   **Maintainability requirement / egm-draft safeguard:** Publish a narrow authorization boundary based on the stable account UUID, independent of Google tokens and servlet sessions. Define when current permissions are evaluated for ordinary requests and durable work, and distinguish session logout from account-permission changes and resource lifecycle revocation. Let T5/T6 and later consumers extend that policy without building another authorization system. Do not require T3 to implement their jobs.

   **Owner decision:** Does permission removal prevent only new submissions, also stop not-yet-dispatched work, or additionally fence publication? Already dispatched paid calls still require settlement; do not silently equate revocation with reversal of external effects.

Evidence commands used:

- `cat` for the persona, target, required sources, both standards, remote-work guidance and EGM map.
- `wc -l docs/designs/application.md docs/roadmap.md docs/contracts/k0-foundation.md EGM.md docs/research/google-sign-in-investigation.md docs/designs/t3-identity/authentication-decisions.md docs/remote-work.md`
- Complete application reads with `sed -n '1,120p'`, `'121,240p'`, `'241,360p'`, `'361,480p'`, and `'481,618p' docs/designs/application.md`.
- Complete roadmap reads with `sed -n '1,130p'` and `'131,253p' docs/roadmap.md`.
- `git rev-parse HEAD && git branch --show-current`
- `nl -ba docs/designs/t3-identity/authentication-decisions.md`
- `rg --files backend/src/main/java backend/src/main/resources frontend/src frontend | rg '(\.java$|application.*\.yml$|vite\.config\.ts$)'`
- Numbered reads of `backend/pom.xml`, `frontend/src/main.tsx`, all three current application YAML files, and relevant K0 lines.
- Bounded `rg -n` searches of application design for permission ownership, polling, durable work, dispatch and publication obligations.

Current implementation confirms there is no existing identity/permission implementation to reuse: `backend/pom.xml:21–63` has MVC/JDBC/Flyway dependencies without security/session dependencies; the discovered production Java files contain the entry point and shared errors/web adapter only. Reuse K0’s UUID, transaction and error conventions. The findings above are repository-derived design inferences; they do not rely on unverified external framework claims.
