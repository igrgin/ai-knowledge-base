Required reads complete: full `zoom_out.md` persona; both standards; target `authentication-decisions.md` (82 lines); `application.md` (618); `roadmap.md` (253); K0 (70); root EGM (70); Google investigation (133). Truncated combined output was recovered through smaller reads. No required source missing. Standards used as concept heuristics.

Baseline verified with `git rev-parse HEAD`: `bb26f2571b6d6787d86fa29ed1f4f8a55a5dfda4`. `git status --short` showed only the excluded untracked LangChain assessment, which I did not read. Read-only throughout; no builds, servers, writes or subagents.

**1. Major — Decide what “inactivity” means before adopting the framework’s idle clock.**

Dimension: Contrarian / YAGNI & Code Minimization.

Scope: intake lines 22–24; application lines 319–322.

Risk: the selected 30-minute inactivity promise coexists with status polling approximately every two seconds. Spring Session documents updating last access when saving sessions. If ordinary polling counts as activity, an unattended processing/chat screen can remain signed in until the eight-hour maximum. Independent per-device timers alone do not settle this.

Recommendation: explicitly choose between HTTP-request inactivity and meaningful user activity. The simplest implementation is request inactivity, with the polling consequence accepted and documented. If the owner expects unattended screens to expire after 30 minutes, identify which requests extend the clock and keep background polling from extending it; avoid introducing a client heartbeat or separate activity service merely to measure inactivity.

Owner decision: does passive polling keep a session active?

Primary evidence: [Spring Session API](https://docs.spring.io/spring-session/reference/api.html), particularly expiration/last-access behavior. This is a product tradeoff, not a claim that the intake already chose human-interaction inactivity.

**2. Major — Bound revocation to an observable authorization point; avoid turning it into distributed cancellation.**

Dimension: Contrarian / YAGNI & Code Minimization.

Scope: intake lines 24, 26, 77; application lines 165–169.

Risk: cross-instance session revocation and permission-change propagation are accepted, but their effective boundary is open. “Immediate” can accidentally expand T3 into cancelling running requests, interrupting future paid work, or preventing every concurrent login. Conversely, a grant list copied into an eight-hour session can leave revoked permissions effective on another instance.

Recommendation: propose that committed changes govern subsequent server authorization checks on every instance, using shared authoritative permission/session state. Treat already-admitted requests separately and leave future worker admission/publication checks with their owning features. Explicitly settle whether “revoke all sessions” covers sessions established concurrently and whether a fresh Google sign-in after revocation remains allowed. Revoking sessions should not silently become account suspension.

Owner decision: accept next-authorization-check effect, or require stronger cancellation/fencing for particular operations? Also confirm whether all-session revocation allows subsequent login.

Actual reuse/current evidence: repository searches found no existing security chain, principal, permission repository or session implementation. `backend/pom.xml:21–63` currently supplies MVC/JDBC/Flyway, so no existing app authorization cache needs preserving. K0 lines 48–50 supplies short shared-database transaction conventions without a generic distributed framework.

**3. Nit — Keep the permission catalog finite and introduced by real feature slices; decide how later defaults affect existing accounts.**

Dimension: Contrarian / YAGNI & Code Minimization.

Scope: intake lines 26–46; application line 165; roadmap lines 15–17, 133.

Risk: the proposal spans almost the entire future product. Building a configurable policy language, editable permission definitions, role hierarchy or permission UI for unimplemented operations would substantially expand identity delivery. There is also a consequential lifecycle choice: automatically granting new “basic” permissions to existing accounts must not accidentally restore an administrator’s deliberate restriction.

Recommendation: retain the accepted individual-grant model, with a fixed application-defined action catalog and a small grant-management interface. T3 establishes its real session/permission-management actions and the extension boundary; later slices introduce their own actual action definitions and enforcement. Decide explicitly how new defaults reach existing accounts and ensure ordinary startup/login does not reapply revoked grants. Optional role grouping has no demonstrated need yet.

Owner decision: when a later basic action is introduced, should existing accounts receive it automatically, and what preserves deliberate restrictions? Acceptance of sharing defaults remains a separate decision; nothing here reverses the owner’s required explicit permissions.

[Spring method security](https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html) already supports action authorities and bean-based checks; this is a reuse option, not a mandate to choose annotations or a claim that session-carried authorities solve propagation.

Actual reuse investigation:

- Commands: `rg --files backend/src frontend/src frontend/e2e | sort`; `rg -n 'Security|Session|Principal|Permission|Authentication|ApiError|RequestIdFilter|JdbcClient|Transactional|PostgresIntegrationTest|fetch\(' backend/src frontend/src frontend/e2e backend/pom.xml frontend/vite.config.ts`; full numbered reads of matched implementation and configuration files.
- `ApiError.java:6`, `RequestIdFilter.java:17–34` and `ApiExceptionHandler.java:76–86` supply the existing public envelope/correlation behavior. Reuse those conventions for T3 responses; the MVC advice’s private response helper is not an existing security-filter adapter.
- `PostgresIntegrationTest.java:19–45` already supplies disposable real-PostgreSQL plumbing; reuse it for owned identity/permission races instead of another database fixture framework.
- `frontend/src/main.tsx:5–27` is only the readiness screen; `frontend/vite.config.ts:4` proxies only Actuator. There is no existing auth client/router to preserve or extend by assumption.
- Spring Session’s [JDBC reference](https://docs.spring.io/spring-session/reference/configuration/jdbc.html) provides indexed durable sessions, and its [API reference](https://docs.spring.io/spring-session/reference/api.html) provides principal-based session lookup. Start session-management design from that store, indexed by stable application identity, before adding a parallel application session registry. Absolute lifetime, safe display metadata and revocation races still need explicit design.

No blocker identified under this concept lens. These are bounded drafting choices and reuse safeguards, not findings that the brainstorm intake must already contain final files, schemas or implementation phases.
