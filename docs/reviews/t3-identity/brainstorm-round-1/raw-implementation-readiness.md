Required full reads completed; none missing. Read the complete implementation-readiness persona, both standards, target, application design, roadmap, K0, EGM map and Google investigation. Standards were used as concept heuristics. Also read workflow/remote guidance and inspected current configuration/migration boundaries. No writes, builds, servers, remote mutations or subagents.

Baseline verified with `git rev-parse HEAD && git branch --show-current`: `bb26f2571b6d6787d86fa29ed1f4f8a55a5dfda4`, branch `3-register-log-in-and-retain-authenticated-sessions`.

No Blocker found. Three Major concept decisions merit carrying into `egm-draft`:

1. **Major — Evolving basic permissions needs an existing-account policy.**
   **Dimension:** Migration & Backward Compatibility.
   **Evidence:** [Intake lines 20–26](/Users/igrgin/Documents/code/apps/ai-knowledge-base/docs/designs/t3-identity/authentication-decisions.md:20) gives new accounts automatic basic permissions; [application line 165](/Users/igrgin/Documents/code/apps/ai-knowledge-base/docs/designs/application.md:165) says feature owners introduce actual action permissions as their slices arrive. The proposal spans T4 through T19.
   **Risk:** A T3-created account could lack ordinary actions introduced later, while accounts created after that feature receive them. Conversely, reseeding defaults at startup/login could silently restore permissions an administrator deliberately revoked.
   **Recommendation / migration requirement:** Choose a stable permission-evolution rule before publishing the T3 consumer boundary: either declare the accepted catalog ahead of its feature implementations, or introduce permissions with explicit existing-account grant migrations that preserve intentional revocations. Keep changing “basic defaults” separate from individual assignments; test an older account, a new account and an explicitly revoked grant across an upgrade. Do not preaccept the currently unaccepted sharing defaults.
   **Owner decision:** Whether later basic permissions automatically extend to existing users, and whether changing a default affects existing assignments. T3/integrator then own the migration contract; feature owners introduce their approved actions.

2. **Major — The authentication boolean needs a session transition rule.**
   **Dimension:** Migration & Backward Compatibility.
   **Evidence:** [Intake lines 12–14](/Users/igrgin/Documents/code/apps/ai-knowledge-base/docs/designs/t3-identity/authentication-decisions.md:12) permits explicit overrides in either profile and selects a stable no-sign-in principal; [lines 22–26](/Users/igrgin/Documents/code/apps/ai-knowledge-base/docs/designs/t3-identity/authentication-decisions.md:22) require durable sessions and identical authorization paths.
   **Risk:** Configuration changes are a real rollout transition even without a local-to-Google product migration. An implementation could retain an old no-sign-in authentication after enabling Google, retain a Google account identity when disabled mode promises the configured account, or let differently configured instances interpret the same browser session differently.
   **Recommendation / migration requirement:** Specify the actor used on every request in each mode and what happens to cookies/session records created under the other mode. A simple supported policy is to invalidate/reject incompatible authentication state on a mode change and require consistent mode configuration across web instances. Prove both configuration transitions with an existing browser cookie; do not add an account switcher, content migration or a prohibition on the accepted production override.
   **Owner decision:** Whether existing Google sessions may remain resumable after a temporary disabled-mode run, or mode changes explicitly end them. T3 owns enforcement; integrator owns consistent configuration.

3. **Major — Restoring a database can restore revoked authority and unconsume bootstrap.**
   **Dimension:** Migration & Backward Compatibility.
   **Evidence:** [Intake lines 24–26](/Users/igrgin/Documents/code/apps/ai-knowledge-base/docs/designs/t3-identity/authentication-decisions.md:24) requires session/permission revocation; [lines 52–63](/Users/igrgin/Documents/code/apps/ai-knowledge-base/docs/designs/t3-identity/authentication-decisions.md:52) makes bootstrap one-time and subsequent grants application-managed. [Application recovery lines 533–535](/Users/igrgin/Documents/code/apps/ai-knowledge-base/docs/designs/application.md:533) restores compatible snapshots and replays project/share/snapshot revocations, but does not establish the corresponding identity policy.
   **Risk:** Restoring a snapshot taken before revocation can reintroduce a session or administrator grant. A pre-bootstrap snapshot can also lose the consumed marker. The last-manager invariant does not establish that restored grants are still authorized.
   **Recommendation / migration requirement:** Record an explicit identity recovery contract for T11/T15 to consume. A bounded option is to keep restored environments non-serving, invalidate all restored sessions, reconcile privileged assignments/bootstrap completion against trusted retained evidence, then reopen. An independent auth-change journal is an alternative, not a mandatory new subsystem. Clearing sessions alone does not correct restored permission grants.
   **Owner decision:** The acceptable recovery behavior and retained evidence needed before serving a restored database. This concerns rollback of application state, not the separately deferred Google-account-loss recovery feature.

Current-behavior evidence prevents inventing a legacy-user migration: [V1 lines 1–2](/Users/igrgin/Documents/code/apps/ai-knowledge-base/backend/src/main/resources/db/migration/V1__enable_vector.sql:1) only enables vector; [EGM lines 9–12](/Users/igrgin/Documents/code/apps/ai-knowledge-base/EGM.md:9) identifies authentication as planned. No password-account conversion or public-snapshot cleanup is currently required.

Read/evidence commands used, from repository root:

- `cat` on the persona, target, both standards, `EGM.md`, `docs/contracts/k0-foundation.md`, `docs/egm-workflow.md` and `docs/remote-work.md`.
- `sed -n` on `docs/designs/application.md`: `1,140p`, `141,280p`, `281,425p`, `426,618p`, with `485,545p` reread to recover truncated output.
- `sed -n` on `docs/roadmap.md`: `1,130p`, `131,253p`; on `docs/research/google-sign-in-investigation.md`: `1,70p`, `71,133p`.
- `nl -ba docs/designs/t3-identity/authentication-decisions.md`; numbered reads of `backend/src/main/resources/application{,-dev,-prod}.yml`, `V1__enable_vector.sql`, and `frontend/vite.config.ts`.
- `rg --files backend/src/main/resources backend/src/main/java frontend/src frontend/e2e`; bounded `rg -n` searches for permission, migration, rollout and restore passages.

These findings are repository-derived risk inferences; no new external capability claim is asserted.
