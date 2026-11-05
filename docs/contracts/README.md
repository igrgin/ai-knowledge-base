# Contract index

## Contract availability

K0 is available from merged T2 in the [foundation contract](k0-foundation.md). KP, KU, KD, KE, KH, KS, KV, KC, KL and KI are planned boundaries, not published interfaces or available schemas. Each future provider must publish its real interface/schema assumptions with the first consumer and integrator before its gate is available. Record agreement, version, consumers and availability here as actual work lands. The [roadmap](../roadmap.md) owns task state and start/completion dependencies.

These are checkpoints inside existing tasks, not additional tasks or claims that implementations already exist. The owning writer proposes changes; the integrator is the sole editor of shared contract records and records versions, consumers and agreement in this index during authorized implementation.

A checkpoint needs an agreed version and schema assumptions, a usable provider interface and passing first-consumer checks. It may become available before all producer UI work is finished. A draft, application-mode stub or a future design obligation does not meet that bar. The common [feature-design prerequisite](../roadmap.md#feature-design-prerequisite) applies separately; task completion still requires its real integrated demonstrations.

| Gate | Producing task and owner | What must be agreed before a consumer starts | Consumers |
| --- | --- | --- | --- |
| K0 | T2, foundation owner | UUID actor/resource/request IDs, trusted server-established identity convention (concrete principal owned by T3), error envelope, migration/test conventions; no domain schema | T3, T5 |
| KP | T4, project owner | Owned-project key/read boundary, lifecycle revision and SQL project-lock convention; project migration/API available before UI polish | T6, T8, T11 |
| KU | T5, usage/model owner | Embedding/chat ports, token/price input and atomic reservation/admission; agree the dispatch/unknown/settlement seam jointly with T6's KD protocol and lifecycle obligations before charged consumers; no requirement to implement future jobs in T5 | T7, T9 |
| KD | T6, document owner | Private original-file lookup/storage interface, immutable version/page-extraction format, durable extraction/indexing stage handoff and bounded persisted pages, processing states and job claim/fence/parser-admission contract | T7, T18, T20, T11, T16 |
| KE | T7, indexing owner | Complete-ready generation visibility, immutable chunk/page/vector identities, model/dimension/version compatibility and read/copy interface; agreement with T9 retrieval and T16 imports, including stale-generation/lifecycle checks | T9, T16 |
| KH | T8, saved-chat owner | Ordered transcript/read-write API, owner/project association, message IDs and delete command boundary; no embeddings required | T20, T9, T19 |
| KS | T20, source-control owner | Active-or-granted minus chat-exclusions query, archive-cycle/source revision semantics | T9, T16, T19 |
| KV | T18, viewer owner | One reusable viewer route/component accepting an authorized original-file reference and physical page | T10, T16 |
| KC | T9, answer owner | Persisted validated citation/turn DTO, passage/version/page references and bounded transcript representation | T10, T19 |
| KL | T11, lifecycle owner | Parent revocation/SQL lock boundary, crash-consistent independent journal acknowledgement/replay and deletion fences; failure evidence for interrupted SQL/log writes and non-serving restores; extension mechanism for later link kinds | T9, T16 |
| KI | T16, distribution owner | Fixed-version manifest and destination-ID map/import transaction extension point | T19 |


T17 is withdrawn and consumes no gates. T19 references in the table are conditional on the unresolved saved-chat contents of full-project imports; they authorize no work or interface publication.

K0 supplies typed application/MVC errors and UUID/trusted-identity, short-transaction, migration and meaningful-test conventions. It does not implement a principal or domain schema. Its [shared](../../backend/src/main/java/com/aiknowledgebase/shared/EGM.md) and [web](../../backend/src/main/java/com/aiknowledgebase/shared/web/EGM.md) package maps distinguish current behavior from future consumer obligations. A cross-slice contract change needs provider/consumer review and versioning before consumers rely on it; one integrator edits shared records.
