Required full reads are complete: the `egm-design` persona, target intake, application design, roadmap, K0, root EGM map, Google investigation, Zoom Out standard and Heilmeier standard. No required source was missing.

Baseline verified with `git rev-parse HEAD`: `bb26f2571b6d6787d86fa29ed1f4f8a55a5dfda4`. `git status --short --branch` showed the specified branch and only the excluded untracked research file; I did not read that file. Evidence reads used `nl -ba <path>`, with bounded `sed -n` ranges for long documents, plus `rg --files backend/src/main frontend/src backend/pom.xml frontend/vite.config.ts`. No writes, builds, servers or mutations occurred.

These are concept safeguards, not a formal design-readiness verdict.

1. **Major — Session semantics and database load: define what counts as inactivity.**

   Evidence: [intake:22](/Users/igrgin/Documents/code/apps/ai-knowledge-base/docs/designs/t3-identity/authentication-decisions.md:22) selects 30-minute inactivity expiry; [intake:24](/Users/igrgin/Documents/code/apps/ai-knowledge-base/docs/designs/t3-identity/authentication-decisions.md:24) makes timers independent per session. [Application design:322](/Users/igrgin/Documents/code/apps/ai-knowledge-base/docs/designs/application.md:322) introduces visible-job polling approximately every two seconds.

   **Risk:** “Inactivity” could mean no user interaction or no authenticated HTTP requests. These diverge once a browser polls automatically. If polling refreshes activity, an unattended page may remain authenticated until the eight-hour maximum. A custom client heartbeat would add traffic without establishing that a person is present. Polling also contributes database work to the selected JDBC session approach. Spring documents last-access updates when sessions are saved; actual query/write frequency needs verification in the chosen integration. [Spring Session API](https://docs.spring.io/spring-session/reference/api.html#api-session)

   **Recommendation:** Carry an explicit activity definition into the Concepts Brief. State whether polling, `/me`, session-list refreshes and background tabs extend idle lifetime. Test an unattended polling page and multiple tabs sharing one session. Keep expiry server-enforced and avoid an extra heartbeat architecture unless the chosen semantics require it.

   **Owner decision:** Select request inactivity or user-interaction inactivity, with its observable consequences. This does not revisit the accepted durations.

2. **Major — Authorization boundary: permission revocation needs a separate effect contract from session revocation.**

   Evidence: [intake:24–26](/Users/igrgin/Documents/code/apps/ai-knowledge-base/docs/designs/t3-identity/authentication-decisions.md:24) requests cross-instance session revocation and individual permission changes, while leaving propagation open. [Application design:189](/Users/igrgin/Documents/code/apps/ai-knowledge-base/docs/designs/application.md:189) and [application design:423](/Users/igrgin/Documents/code/apps/ai-knowledge-base/docs/designs/application.md:423) establish durable work that outlives its receiving web request. [K0:50](/Users/igrgin/Documents/code/apps/ai-knowledge-base/docs/contracts/k0-foundation.md:50) requires releasing database connections before external work.

   **Risk:** A permission snapshot retained for the session could preserve revoked authority for hours. Conversely, equating logout/session revocation with revoking all previously accepted operations could unexpectedly cancel durable work. “Check the permission” does not establish the boundary when a job was admitted before revocation but dispatches or publishes afterward. This becomes financially relevant for future uploads and answers.

   **Recommendation:** Define two separate contracts: freshness of permission checks on new protected actions, and treatment of already accepted work. Prefer a small principal plus authoritative shared permission resolution over a session-long grant snapshot. Have T3 agree with later resource owners where permission changes matter—admission, dispatch or publication—and how denied future work releases unused reservations. Preserve existing settlement of already dispatched calls.

   **Owner decision:** Choose the intended effect of removing an action permission on queued and running work. Session termination should have its own explicitly stated behavior.

3. **Major — Quota economics: authentication itself needs bounded resource admission.**

   Evidence: [intake:20](/Users/igrgin/Documents/code/apps/ai-knowledge-base/docs/designs/t3-identity/authentication-decisions.md:20) permits automatic account creation; [intake:22–24](/Users/igrgin/Documents/code/apps/ai-knowledge-base/docs/designs/t3-identity/authentication-decisions.md:22) adds persistent, independently managed sessions. [Application design:319](/Users/igrgin/Documents/code/apps/ai-knowledge-base/docs/designs/application.md:319) already requires bounded registration/login bursts. The existing datasource has four connections and a two-second acquisition timeout in [application.yml:7](/Users/igrgin/Documents/code/apps/ai-knowledge-base/backend/src/main/resources/application.yml:7). T5/T6 select other admission settings later; that does not cover T3’s first database-backed HTTP security boundary.

   **Risk:** Per-account PDF/chat quotas do not bound repeated login attempts, pending login sessions, session records, session-list responses or permission-management listings. Adding these paths without admission and cleanup leaves authentication able to consume the same database pool needed for ordinary access. Time-limited records still require controlled deletion. Spring Session JDBC documents persisted session/attribute tables, a cleanup job and transactional JDBC operations. [Spring Session JDBC](https://docs.spring.io/spring-session/reference/configuration/jdbc.html)

   **Recommendation:** Add a bounded T3 resource envelope: finite login/initiation bursts, bounded pending-login lifetime/state, small persisted attributes, indexed account-to-session lookup, bounded listing responses and verified expired-session cleanup. Measure authentication/session query counts with competing instances and the current small pool. Start with PostgreSQL and existing framework mechanisms; no extra cache/service is justified by this concept.

   **Owner decision:** Any user-visible simultaneous-session cap or forced replacement policy needs acceptance. Technical bounds on pending login state and listing work can be designed without silently imposing that policy.

No numerical provider fee or throughput claim is established here. The strongest cost concern is shared PostgreSQL capacity and retained authentication state; this feature introduces no necessary LLM token expenditure.
