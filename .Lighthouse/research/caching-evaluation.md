# Caching evaluation for local v1

## Current planning references

This is supporting research, not another implementation plan. Use the current [requirements](../spec.md#requirements), [design decisions](../spec.md#design-decisions) and single [task list](../plan.md#revised-task-list). The [performance checkpoint](../plan.md#offline-scalability-and-performanceembedding-checkpoint-in-t13) measures a no-cache baseline and embedding batching/reuse; no Caffeine or Redis prototype is required. Hosting remains optional later. No implementation or capacity result is claimed by this note.

## Recommendation

Scalability is an explicit owner requirement. Keep Redis optional in local v1, while putting shared correctness state in PostgreSQL and making web and worker execution separable. Start without a backend cache of application data. Use existing persistence, durable request IDs, and frontend asset caching. Measure repeated work during T13 and T14. Caffeine can cache recomputable values per process; any shared-cache requirement must be evaluated across multiple instances. See `scalability-evaluation.md` for the required growth path and proposed multi-instance verification.

This is an evaluation for the project owner. It does not change the accepted requirements or plan, add a dependency, or configure a cache. There is no application implementation or workload measurement yet, so no cache hit rate or latency saving has been established.

## What the choices mean

Spring's cache abstraction provides an interface such as `CacheManager`; an actual store implements it. Caffeine keeps entries in the application process. Redis is a separate backing store accessed over a connection. Spring Boot integrates with both. A common interface helps integration, but it does not solve source eligibility, invalidation, key design, or cross-process consistency. [Spring cache abstraction](https://docs.spring.io/spring-framework/reference/integration/cache/strategies.html), [Spring Boot providers](https://docs.spring.io/spring-boot/reference/io/caching.html), [Spring Data Redis](https://docs.spring.io/spring-data/redis/reference/redis/redis-cache.html)

Caffeine supports limits on entry count/weight and expiration. Those controls are preferable to an unbounded application map if a cache is introduced. Its entries disappear with the process, which is acceptable only when the original data remains available or the result can be recomputed. [Caffeine eviction](https://github.com/ben-manes/caffeine/wiki/Eviction)

## Evaluate each part of this app

| Area | Local v1 treatment | Reason |
| --- | --- | --- |
| Extracted PDF text and passage embeddings | Persist the complete ready ingestion version in PostgreSQL | They are reusable project data. Restarting the app should not re-extract or re-embed every document. This is persistence rather than a temporary cache |
| Ingestion retries and repeated chat submissions | Keep durable job/version IDs and unique request keys in PostgreSQL | A retry must reuse the existing operation across restart. An in-memory cache cannot provide that guarantee |
| Saved answers and chat history | Read the persisted messages | Reopening a chat does not call the AI again. Saving an answer is different from using it for a new question |
| Identical query embeddings | First candidate for Caffeine only if repeated inputs occur often enough | It can avoid repeated embedding calls, but each prepared query includes its bounded context and model settings. The benefit remains unmeasured |
| Retrieval results | No cache initially | Active uploads, archiving, old-chat grants and project deletion change which passages are eligible |
| Generated answers for new questions | No cache initially | The same question text can need a different answer for another user, project, chat history or eligible source set |
| Project/library summaries | Read indexed database queries; keep the current view in frontend state | The queries are bounded and the UI must reflect processing, archive and deletion changes. Profile before duplicating this state in a backend cache |
| PDF source pages | Keep original files and use the viewer's current in-memory document/page state | Source requests still require current owner/chat authorization. A shared public PDF cache would violate the private-source design |
| Built frontend JavaScript/CSS | Use fingerprinted assets with browser caching | These static assets contain no user PDF/chat data. This does not need Redis or a Java object cache |
| Sessions, spending limits, archive grants, deletion state | Keep the accepted PostgreSQL-backed implementation authoritative | Losing or serving stale state here can affect access, spending or recovery. They are not disposable cache entries |

## Why an answer cache is a poor starting point

Consider two chats asking the same question after a PDF is archived. An older chat may still use that PDF; a new chat may not. A cache keyed only by question or project could return an answer based on a forbidden source. A cached permission check could also preserve access after project deletion.

Correct answer reuse would need the owner, project, chat context, exact eligible document versions, model, prompt version and generation options. It would also need current authorization and source-lifecycle checks before returning a hit. Similar-question or semantic answer reuse would add a further correctness decision. The current plan's saved-turn/idempotency behavior already handles browser retries without this complexity.

## Rules for a future Caffeine cache

Keep caching inside the existing owning module. Do not introduce a generic cache module or an unused Redis adapter. Callers should not need to manage invalidation themselves.

For an initial query-embedding cache, key by owner, project, embedding model/version, dimensions, preprocessing version and a hash of the exact prepared query text. Avoid cross-user reuse. Recheck current project/chat access before use. Cache a successful numeric embedding only; do not cache authorization decisions, failed calls, unknown outcomes or budget balances.

Set an explicit memory/count bound and a short expiry, and record hit/miss/load/eviction metrics without logging question text. Scope the implementation so project purge removes its cached entries and an in-flight load cannot republish them after purge. Expiration alone is not an authorization or deletion mechanism. A cache miss or restart may increase recomputation; it must not change which answer or source access is permitted. A hit schedules no provider call and incurs no new provider reservation; user-request quotas still apply under the configured rules.

Test equivalent results with the cache enabled/disabled, owner separation, model/context changes, expiry/eviction, simultaneous loads and project deletion during loading. If retrieval/answer caching is later proposed, include ready-upload publication, archive/grant changes, chat deletion and corpus revisions in the invalidation analysis before accepting it.

## When Redis would deserve another evaluation

Revisit it when multiple application instances need a shared cache, when measured repeated reads overload PostgreSQL despite query/index work, or when an explicitly selected shared rate-limiting/session design calls for it. Multiple instances alone do not require Redis: the current PostgreSQL session and budget design already shares durable state.

A Redis proposal must identify a measured benefit, memory/retention limits, deployment cost, failure behavior and consistency rules. Moving from Caffeine to Redis changes process scope, network failures and serialization. A common Spring interface does not make those differences disappear. Redis would not automatically improve the AI model's generation time on a cache miss.

## Measurement and follow-up

Use existing T13/T14 measurements to separate PDF extraction, embedding calls, filtered vector retrieval, answer generation and file viewing. Measure repeated prepared-query frequency, database query duration, application memory and actual provider usage. Add a cache only when those observations justify its memory and invalidation work. No arbitrary latency or hit-rate threshold is claimed as a requirement here.

The owner subsequently required a scalable design and supplied no fixed traffic/corpus target. The companion evaluation supports the scalable design and experiments allocated in the current task list. Redis selection remains a workload decision; no plan acceptance is replaced by this evaluation. Java 26 and optional later hosting remain unchanged.

## Project sources

- `spec.md`, especially R2, R5, R7, R10, R12, R13, R15, R17 and designs D-B, D-D, D-F and D-H.
- [Current task list](../plan.md#revised-task-list), especially T5/T7 model work, T20 source rules, T18/T10 file/citation viewing, and T13/T14 measurement.
- Current conversation request to evaluate Redis versus caching inside Spring Boot, 2026-10-24, `codex://threads/01a1699d-af41-71f0-a5e7-c75152955b39`.
