# Delivery roadmap

## Current authority

[Issue #24](https://github.com/igrgin/ai-knowledge-base/issues/24) is closed after the EGM documentation cutover and architecture/task reassessment merged at `e85f9de`; [results and dispositions](reviews/egm-migration/reassessment.md) record that work. T1 is merged in [PR #21](https://github.com/igrgin/ai-knowledge-base/pull/21); T2 is merged in [PR #23](https://github.com/igrgin/ai-knowledge-base/pull/23), merge `d5b2efceda70db39bcf6e19fcdef69c9f236f297`. On 2026-11-05 the owner authorized T3 [issue #3](https://github.com/igrgin/ai-knowledge-base/issues/3) research, plan/issue audit and EGM brainstorming, selecting Google as the login provider plus a configurable no-sign-in launch from IntelliJ with local supporting services. [Intake decisions](designs/t3-identity/authentication-decisions.md) record the bounded scope. T3–T20 implementation remains pending and requires separate authority. No paid calls, deployment or merge follows from this record. Concurrency remains one.

[Application design](designs/application.md) owns product requirements and proposed mechanisms. [Contract availability](contracts/README.md) owns published interface gates. [Workflow](egm-workflow.md) owns EGM routing and review handoff; [remote work](remote-work.md) owns native task/branch/PR operations. Current progress belongs here; detailed execution evidence belongs on the task/PR.

On 2026-11-06 the owner authorized bounded T3 `egm-draft` from the existing brief, preserving accepted decisions and resolving non-development disabled-sign-in behavior within that design. On 2026-11-07 the owner approved Overview & Goals and then confirmed A for disabled entry outside development: preserve valid Google sessions under ordinary limits and block fresh entry until re-enabled. The [working draft](designs/t3-identity/design_draft.md#technical-plan-architecture) uses that direction and the selected individual-grant/template model; full Technical Plan approval remains pending. Formal design review and implementation are not authorized by this drafting instruction.

The owner further selected a YAML sign-in boolean (planned name `app.auth.enabled`): false by default in `dev`, true in `prod`, explicitly overridable. This supersedes a hard development-only bypass restriction. T3 must design its no-sign-in principal and property behavior; no runtime setting has been implemented. The first successful Google sign-in must also create the account automatically with immediate project access and no extra onboarding.

The owner accepted session defaults of 30 minutes idle and 8 hours maximum, YAML-configurable. Browser reopening and backend restart preserve valid sessions within both limits; expiry preserves view/unsent-question state for same-account reauthentication. T3 owns session behavior; T12/feature owners integrate the UI obligations without adding future chat UI to T3.

The owner additionally requests independent session timers and settings/profile controls to revoke one or all sessions, plus administrator session-revocation controls. These are T3 brainstorm amendments; administrator designation/permission, UI boundary and exact file/schema changes must be resolved before implementation. No separate password/admin login or broader private-data access is authorized.

The owner additionally selected explicit permissions rather than a role-only authorization model. T3 must design permission assignments/enforcement and agree the consumer boundary; admins assign/revoke individual permissions in the app and new users receive basic permissions automatically. The first permission manager is established by an owner-accepted one-time internal setup command targeting an existing account UUID; concrete basic permission IDs/granularity, command/recovery design, optional role grouping and propagation remain open. Resource slices still own ownership predicates and their actual action permissions. Permission checks remain active when sign-in is disabled.

The owner described basic access as reading/writing owned documents and accessing owned chats. The T3 [accepted capability table](designs/t3-identity/authentication-decisions.md#accepted-basic-permission-set) maps these to actions. The owner now limits sharing to whole projects and importing shared projects; all standalone chat snapshots are dropped (R27/T17). Full-project share/import are now basic permissions, individually revocable. Administrators receive all basic capabilities plus administrative grants and use the ordinary website under the same ownership/quota and explicit-revocation rules. Concrete permission IDs/granularity still need design. The owner clarified that everything is copied, including all saved chats/messages/citations and source settings. T19 is required to complete the same project-copy flow; no content selector or files-only product mode remains.

The owner requires at least one permission manager after initial setup. T3 must reject removal of the final permission-management grant, including concurrent changes, against shared database state. Logged-out/expired sessions do not remove the account's authority. No new account deletion or recovery feature is authorized by this rule.

Post-review, the owner accepted meaningful user-action renewal, at-most-once-per-minute local interaction notifications and exclusion of polling from idle renewal. Ordinary expiry preserves already-authorized requests/durably accepted jobs; future requests need valid authentication. The five-minute pre-write threshold is accepted; no absolute-lifetime extension is allowed. The 30m idle, 8h maximum, 5m pre-write lead time and 1m activity-report interval are overridable YAML defaults for every profile, including development, production and tests.

The owner accepted the permission-removal boundary: current checks for every new protected request, with previously authorized requests/durably accepted queued or running work allowed to finish within original bounds. Atomically order admission/revocation; keep result-access, lifecycle and spending checks. No global permission-change job scan or implicit cancellation is required. T3 must agree this contract with future job owners before consumers implement it.

On 2026-11-07 the owner selected [individual grants and independent convenience templates](designs/t3-identity/authentication-decisions.md#basic-permission-evolution). Accounts have no saved template membership or permanent authorization deny state. Removing a grant removes access. New basic permissions reach existing accounts through reviewed grant rollouts, and an intentional all-user grant may restore a previously removed permission. This explicitly supersedes the earlier blanket preservation rule for such global updates. Login/restart does not reapply templates or replay completed initial grants. Current protected requests use stored grants without session refresh or Google sign-in; admitted work keeps its accepted boundary. The owner selected additive template application: keep other grants, with individual removals separate. Persistence and rollout transaction details remain design work; no schema/gate is published.

The owner accepted session revocation without account suspension: invalidate targeted application sessions on the server, allow a later new Google sign-in using current permissions, and never restore removed grants. Concurrent login/revoke-all ordering and admitted-work interactions still need design; no Google-side logout guarantee or account-blocking feature is added.

The owner accepted enabled-mode startup failure for missing required Google configuration and disabled-mode startup without Google settings (2026-11-06). Following the [source-backed investigation](research/google-sign-in-failure-handling.md), the owner accepted fresh user-triggered sign-in after a safe transient failure, without callback/code replay. Existing-session continuity remains a goal to verify under current limits, especially failed same-browser reauthentication; no browser return means no guaranteed in-app error. T3 must explicitly choose/test discovery, token/UserInfo/JWK dependencies, bounded transport/retry and failure handlers; no outage availability or automatic SDK recovery is verified.

The owner rejected mode-change session invalidation (2026-11-06): preserve existing Google sessions under normal expiry/revocation without timer reset/freeze. `prod` is the production Spring profile, defaults to sign-in enabled and permits explicit disablement; production-profile + effective disabled mode requires a red admin banner. T3 owns trusted mode/principal boundaries, T12 banner integration and T13/T15 verification/documentation. Development/tests must exercise both boolean values; the selected development-admin fixture implementation, non-development disabled-mode identity and isolation from stored Google sessions require bounded design. See the [accepted mode behavior](designs/t3-identity/authentication-decisions.md#accepted-authentication-mode-behavior).

The owner selected a persistent development administrator in the database (2026-11-06): common auth defaults true, development YAML overrides false, and disabled development creates/reuses that account and establishes its identity automatically. Permissions are stored/enforced normally; restarts preserve the account and explicit revocations. Enabling auth in development uses Google. Development provisioning/auto-login must be excluded when `prod` is active; saved Google sessions stay isolated with ordinary deadlines and no development-activity renewal. This does not replace normal Google-admin bootstrap or choose a user for non-development disabled mode. T3 owns exact fixture schema/identifiers, tests and bootstrap/manager isolation; implementation remains unauthorized.

The owner amended audit policy (2026-11-06): administrators default to capture enabled and 90-day retention; ordinary users default to capture disabled and 30-day retention. All four values are independently YAML-configurable; administrator auditing may be disabled, including for the development admin. Keep audit-view permission/read-only metadata history and atomic administrative change/audit commits only when that category is enabled. T3 owns audit settings/storage/inspection and hook agreement; features emit real events, T12 integrates the history view and T13 verifies all flag combinations and measured overhead. Category-aware cleanup, exact event/failure semantics and M9 recovery remain bounded-design inputs. [Current policy](designs/t3-identity/authentication-decisions.md#accepted-audit-policy); no implementation/gate is published.

The owner accepted permission feedback (2026-11-06): hide unauthorized administrative areas; grey/disable ordinary actions with explanation/notification. Current backend permission/ownership checks reject stale or forged submissions regardless of UI state. Permission changes need no Google re-login within a valid session; grants can appear after a UI permission refresh. Keep normal expiry/pre-write and non-disclosing/privacy rules. T3 owns response/permission metadata; T12 and feature owners integrate their real views. Exact refresh strategy/components remain design inputs, not a new push/notification subsystem.

The owner confirmed both full-project sharing access modes (2026-11-06): designated-recipient invitations and general links usable by any signed-in possessor with import permission/quotas. Detailed recipient binding/selection and acceptance remain T16/T19 work; T3 provides identity/permission boundaries. The requested [round-2 Goldfish scope/progress checkpoint](reviews/t3-identity/brainstorm-round-2/review.md) completed six lenses: five Major findings and one Nit after deduplication. Direction is coherent; the reviewed branch contains documentation only and no T3 runtime implementation. The recommendation is to stop broad exploration and align a bounded T3 brief before drafting, retaining accepted identity/session/permission/admin/audit requirements. Later slices retain their resource workflows and actual hooks; no dependency, deliverable or authority changes follow from this recommendation.

## Feature design prerequisite

Every unstarted substantial feature requires a bounded, accepted EGM design and completed design review before implementation, following the [workflow](egm-workflow.md#implement-an-accepted-scope). Resolve its technical choices, exact Create/Modify/Delete file boundary, schemas/interfaces, shared edits, incremental phases and tests there. The application design and tables below are direction and ownership, not implementation-ready task plans. Design work itself needs its scoped issue/authority. Both T3 brainstorm rounds are complete; the owner authorized bounded drafting from the [concepts brief](designs/t3-identity/concepts_brief.md), preserving decisions and resolving remaining choices within that design. Its formal feature design and design review are not complete.

The [feature design checkpoints](designs/application.md#feature-design-checkpoints) name known cross-feature decisions. Provider/consumer agreement must precede publication of an affected gate; an unresolved interface cannot become available merely because its producing task can start.

## Dependency semantics

Start gates permit bounded development against a published and agreed provider/consumer contract. A task ID means its completion; a K gate means the narrower checkpoint in the contract index. A draft alone is insufficient. Completion prerequisites must be integrated real implementations; controlled adapters support development but cannot substitute for production wiring. Numeric order and preferred demonstration order create no dependency or implementation permission.

Every feature owns its UI, API, persistence, schema and meaningful tests. Shared changes remain with their owner/integrator. A spanning requirement is complete only after all mapped slices and integration checks pass.

## Shared foundation

T1 provides the runnable shell and toolchain. T2 provides migration/test infrastructure and a few contract conventions. Neither creates future users/projects/documents/chat/vector/sharing tables, a general job engine, speculative adapters, or every feature's UI. Domain tables are introduced by their owning feature. The first real durable queue/storage implementation belongs to T6, the financial ledger to T5, and lifecycle recovery to T11.

## Tasks

A task in the start column denotes its completion; a K gate denotes the narrower checkpoint in the contract index. Completion columns list the real dependencies directly, including some transitive ones for clarity. A feature can begin before all its completion dependencies exist, but cannot claim its integrated demonstration early. Writer roles are bounded assignments for a future build, not people or agents appointed by this record.

| Task | Demonstrable result | Requirement IDs | Can start after | Must exist before completion | Assigned writer | State | Intent label | Issue / branch |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| T1 | Run the Java 26/React shell and local database reproducibly | R18, R19 | Planning/build authority | None | Integrator | completed | feature | [#1](https://github.com/igrgin/ai-knowledge-base/issues/1); `1-run-java-26-react-shell-and-local-database` (created) |
| T2 | Provide Flyway/pgvector wiring and safe API error/K0 conventions | R18, R23 | T1 | T1 | Foundation owner | completed | feature | [#2](https://github.com/igrgin/ai-knowledge-base/issues/2); `2-add-minimal-persistence-and-contract-conventions`; [PR #23](https://github.com/igrgin/ai-knowledge-base/pull/23) (merged) |
| T3 | Register/log in through Google, log out and retain sessions after restart; skip sign-in during local development | R1, R2, R16, R18, R23 | T2 | T2 | Identity slice owner | Overview & Goals approved; Technical Plan active; implementation pending | feature | [#3](https://github.com/igrgin/ai-knowledge-base/issues/3); `3-register-log-in-and-retain-authenticated-sessions` (created for research/brainstorm; title retained) |
| T4 | Create/list/open/rename private projects through the UI | R2, R3, R6, R17 | T3 | T3 | Project slice owner | pending | feature | [#4](https://github.com/igrgin/ai-knowledge-base/issues/4); `4-create-and-manage-private-projects` (planned; not created) |
| T5 | See AI availability/budget status and enforce bounded, idempotent provider spending | R12, R16, R17, R23 | T2 and K0 | T3 | Usage/model slice owner | pending | feature | [#5](https://github.com/igrgin/ai-knowledge-base/issues/5); `5-show-ai-availability-and-enforce-provider-spending-limits` (planned; not created) |
| T6 | Upload a private PDF, observe durable extraction/failure status and recover after restart | R2, R4, R5, R6, R16, R17, R22, R23 | KP | T4 | Document slice owner | pending | feature | [#6](https://github.com/igrgin/ai-knowledge-base/issues/6); `6-upload-and-extract-private-pdfs-with-durable-status` (planned; not created) |
| T8 | Create/reopen/delete separate saved chats in a project | R2, R8, R14 | KP | T4 | Saved-chat slice owner | pending | feature | [#8](https://github.com/igrgin/ai-knowledge-base/issues/8); `8-create-reopen-and-delete-saved-project-chats` (planned; not created) |
| T18 | Open an uploaded original PDF from the project library | R2, R3, R13, R29 | KD | T6 | Viewer slice owner | pending | feature | [#18](https://github.com/igrgin/ai-knowledge-base/issues/18); `18-read-original-pdfs-from-the-project-library` (planned; not created) |
| T7 | Finish indexing a text PDF and show it ready for knowledge queries | R4, R5, R6, R16, R17, R22, R23, R25 | KD and KU | T6, T5 | Indexing slice owner | pending | feature | [#7](https://github.com/igrgin/ai-knowledge-base/issues/7); `7-index-pdfs-and-recover-interrupted-embedding-work` (planned; not created) |
| T20 | Archive/unarchive files and control each chat's fresh-retrieval sources | R2, R7, R13, R28 | KD and KH | T6, T8 | Source-control slice owner | pending | feature | [#20](https://github.com/igrgin/ai-knowledge-base/issues/20); `20-archive-pdfs-and-control-chat-retrieval-sources` (planned; not created) |
| T11 | Delete a project and recover safely from purge/restart/restore failures | R2, R5, R14, R15, R20, R23, R24 | KP, KD and KH | T6, T8 | Lifecycle slice owner | pending | feature | [#11](https://github.com/igrgin/ai-knowledge-base/issues/11); `11-purge-projects-and-recover-safely-after-restore` (planned; not created) |
| T9 | Ask a grounded question and persist an idempotent, validated answer | R2, R7, R9, R10, R11, R12, R16, R17, R22, R23, R28 | KU, KD, KE, KH, KS and KL | T5, T7, T8, T20, T11 | Answer slice owner | pending | feature | [#9](https://github.com/igrgin/ai-knowledge-base/issues/9); `9-answer-grounded-questions-with-saved-provenance` (planned; not created) |
| T10 | Open a validated answer citation at its original physical PDF page | R2, R10, R13, R29 | KV and KC | T18, T9 | Citation slice owner | pending | feature | [#10](https://github.com/igrgin/ai-knowledge-base/issues/10); `10-open-answer-citations-at-the-correct-pdf-page` (planned; not created) |
| T16 | Build the fixed full-project share/import foundation; final product behavior requires T19 | R2, R3, R7, R13, R15, R17, R20, R25, R26, R29 | KP, KD, KE, KS, KV and KL | T7, T18, T20, T11 | Distribution slice owner | pending | feature | [#16](https://github.com/igrgin/ai-knowledge-base/issues/16); `16-share-and-import-independent-fixed-project-copies` (planned; not created) |
| T17 | Dropped: standalone chat snapshots | R27 withdrawn | None | None; excluded from completion | No active slice owner | dropped by owner | feature (historical issue label) | [#17](https://github.com/igrgin/ai-knowledge-base/issues/17); remote issue remains open pending reconciliation; no branch planned |
| T19 | Complete full-project imports with all saved chats, mapped citations and source settings | R2, R7, R8, R10, R14, R20, R26, R28 | KI, KH and KC | T16, T10 | Distribution extension owner | pending; required product scope | feature | [#19](https://github.com/igrgin/ai-knowledge-base/issues/19); `19-include-saved-chats-and-citations-in-project-copies` (planned; not created) |
| T12 | Complete cross-feature navigation, accessibility and packaged UI consistency | R1, R3, R6, R8, R10, R11, R12, R13, R14, R15, R18, R26, R28, R29 | T4 | All retained feature demos integrated with passing evidence; T19 required for complete project copying | Integrator/UI integration owner | pending | feature | [#12](https://github.com/igrgin/ai-knowledge-base/issues/12); `12-integrate-navigation-accessibility-and-packaged-ui` (planned; not created) |
| T13 | Prove integrated privacy/lifecycle/scalability behavior and measure no-cache performance | R1, R2, R4, R5, R7, R10, R12, R13, R14, R15, R17, R20, R21, R22, R23, R24, R25, R26, R28, R29 | T6 | T12 and its feature prerequisites | Verification owner | pending | feature | [#13](https://github.com/igrgin/ai-knowledge-base/issues/13); `13-verify-privacy-lifecycle-scalability-and-embedding-performance` (planned; not created) |
| T14 | Evaluate actual model grounding, citation quality, latency and cost within a bounded allowance | R4, R9, R10, R11, R16, R17, R21, R24, R25 | T5, T7 and passing focused offline dispatch/accounting checks; separate credential/paid-call authority | T9, T10 and passing focused offline answer/citation checks; paid-call authority | AI evaluation owner | pending | spike | [#14](https://github.com/igrgin/ai-knowledge-base/issues/14); `14-evaluate-real-ai-grounding-quality-latency-and-cost` (legacy planned name; no branch required for spike) |
| T15 | Hand over the reproducible local package and truthful readiness evidence | R18, R19, R20, R21, R22, R24, R25, R26, R28, R29 | T1 for living documentation | T12, T13; T14 additionally required for a real-AI-ready claim | Integrator/handoff owner | pending | feature | [#15](https://github.com/igrgin/ai-knowledge-base/issues/15); `15-document-and-hand-over-the-reproducible-local-package` (planned; not created) |

T5 can be scheduled independently of T4's project UI, subject to the common prerequisite and single-task limit. T8 has no T7 dependency; T18 has no embedding/answer dependency. T17 is dropped and has no gate or ownership. T19 is a mandatory stage of T16's same full-project copy flow; the former include-chats selector is removed. T16 alone cannot expose a files-only product mode or complete R26. T12 does not gate any feature slice.

Keep T9 and T10 as separate owned increments but demonstrate them as one usable grounded-answer/citation milestone, reusing T18's reader. T9 alone does not complete R10: correct physical-page navigation requires T10 as well. Keeping the existing issue IDs preserves review history without creating a second reader or answer pipeline.

## Completion dependency graph

Solid arrows mean integrated completion prerequisites, not permission to start. Transitive edges are reduced for readability; the table above supplies the exact full prerequisites. T14 also has the explicit credential/paid-call gate. T15's T14 edge is conditional on claiming real-AI readiness.

```mermaid
flowchart TD
  T1["T1 Runnable shell"] --> T2["T2 Minimal contracts/persistence"]
  T2 --> T3["T3 Accounts"]
  T3 --> T4["T4 Projects"]
  T3 --> T5["T5 AI limits/status"]
  T4 --> T6["T6 Upload/extract"]
  T4 --> T8["T8 Saved chats"]
  T6 --> T7["T7 Index PDF"]
  T5 --> T7
  T6 --> T18["T18 Library PDF viewer"]
  T6 --> T20["T20 Archive/chat sources"]
  T8 --> T20
  T6 --> T11["T11 Purge/recovery"]
  T8 --> T11
  T7 --> T9["T9 Grounded answers"]
  T20 --> T9
  T11 --> T9
  T18 --> T10["T10 Citation navigation"]
  T9 --> T10
  T7 --> T16["T16 Full-project copy"]
  T18 --> T16
  T20 --> T16
  T11 --> T16
  T16 --> T19["T19 Complete project history copy"]
  T10 --> T19
  T19 --> T12["T12 Integration/polish"]
  T12 --> T13["T13 Integrated offline/performance proof"]
  T10 --> T14["T14 Bounded real-AI evaluation"]
  T13 --> T15["T15 Local handoff"]
  T14 -. "real-AI-ready claim" .-> T15
```

For example, T5 starts after K0 while T3 is unfinished, although its signed-in UI/ownership demonstration finishes after T3. T9 demonstrates a grounded answer once T5/T7/T8/T20 are wired; T11 is needed for completed deletion-race checks. T10 starts after KV/KC agreement while real answer integration proceeds. T17 is withdrawn; T19 is required for full project copying and starts only after its agreed gates and bounded design prerequisite. No app-mode fake provider counts as final behavior.

## Demonstration order

| Demonstration | Relevant slices | Scheduling intent |
| --- | --- | --- |
| Local accounts/projects | T1–T4; T5 can overlap | First useful authenticated shell; no broad all-domain foundation |
| Upload/read and saved chats | T6, T18 and T8 | Show these independently before embeddings/answers |
| Ready knowledge and source controls | T5, T7, T20; T11 alongside | Finish AI inputs and lifecycle foundations without serializing viewer work |
| Grounded chat/page citation | T9, T10 | Extend history/reader; T14 may begin bounded evidence after focused safety checks |
| Distribute a full project | T16/T19 | One independent project-copy pipeline; no standalone chat sharing or collaboration |
| Complete local evidence/handoff | T12, T13, T14, T15 | Integration, actual AI evidence and package claims remain separate |

A valid single-writer sequence remains possible: T1, T2, T3, T4, T6, T8, T18, T5, T7, T20, T11, T9, T10, T16, T19, T12, T13, T14, T15. This is a preference, not authority; T5 can start earlier and T18 can follow T6. T19 completes mandatory history copying within the same full-project product flow.

## Ownership and coordination

The following paths assign future slice ownership, not current authorization or deployed schema. Assign an actual writer and committed baseline before dispatch. The integrator is the sole editor of this roadmap and shared contracts. Feature owners deliver UI/backend/schema/tests together; there is no separate frontend/backend ownership split for one slice.

### File and schema ownership

Paths are proposed because implementation files do not exist yet. Backend paths below are under `backend/src/main/java/com/aiknowledgebase/`; frontend paths are under `frontend/src/`. Matching feature tests/e2e fixtures belong to the same slice. A writer cannot modify another owner's base files merely because their task can start; request a small contract/implementation change from that owner/integrator instead.

| Slice owner | Exclusive application files/directories | Feature schema/migration responsibility | Shared touchpoints to coordinate |
| --- | --- | --- | --- |
| Integrator, T1/T2/T12/T15 | Root build/config/scripts, application shell/routes, `components/ui/`, `lib/api/`, contract records, plan and shared test bootstrap | Only minimal migration wiring in T2; reviews/numbers feature migrations at integration | Sole editor of poms, npm manifest/lock, common config, Compose, shell/routes, shared API types and plan; feature authors submit narrow changes |
| Identity, T3 | `identity/**`, `features/auth/**` | Application accounts, Google identity mappings/JDBC sessions; development account fixture; permission assignments and audit history/settings design pending | K0 UUID/trusted-identity conventions; concrete principal, Google login, configured sign-in bypass and route/auth integration |
| Projects, T4 | `project/**`, `features/projects/**` | Projects/ownership/count limits and lifecycle revision | KP ownership/lock API; library mounts feature components without taking their files |
| Usage/model, T5 | `usage/**`, `model/**`, `features/ai-status/**` | Reservations/usage/periods/shared provider admission | KU, actor FK to T3, model dependencies/config through integrator |
| Documents, T6 | `document/upload/**`, `document/storage/**`, `document/api/**`, `ingestion/extraction/**`, `jobs/**`, `pdf-extractor/src/**`, `features/documents/upload/**`, `features/documents/status/**` | Documents/versions/pages/extraction, storage admission and first durable operations/jobs | KD document state, file service and job API; other slices extend through these boundaries |
| Indexing, T7 | `ingestion/indexing/**`, `features/documents/indexing/**` | Chunks/vectors/generations and ready-publication data | KU/KD; publishes KE; document state fields and status mounting through T6/integrator |
| Saved chats, T8 | `chat/history/**`, `features/chats/history/**` | Chats/ordered messages | KH; project lock, later answer metadata and deletion integration through agreed boundaries |
| Viewer, T18 | `source/viewer/**`, `features/pdf-viewer/**`, `features/documents/open/**` | Normally none; reuse T6 document identities | KV and document file route; library action mounted by integrator |
| Source controls, T20 | `document/archive/**`, `chat/sources/**`, `features/documents/archive/**`, `features/chats/sources/**` | Archive grants/cycles, chat exclusions and source revisions | KP/KD/KH; adds its own migration to existing domain tables with table-owner agreement |
| Lifecycle, T11 | `lifecycle/**`, `features/lifecycle/**`; backup/restore script contents integrated by sole script owner | Revocation journal/purge/tombstone data | KL; T4/T8 commands and T6 job/file API changes are serialized with those owners |
| Answers, T9 | `chat/answering/**`, `retrieval/**`, `features/chats/answering/**` | Answer operations and provenance/citation extensions | KU/KD/KE/KH/KS/KL; message-schema extension approved with T8; no second history/provider/job implementation |
| Citations, T10 | `source/citations/**`, `features/chats/citations/**` | Normally none; extend T9 provenance only when warranted | KC/KV; use existing viewer props and coordinate API changes with T18 |
| Distribution, T16 | `distribution/projectcopy/**`, `features/distribution/projectcopy/**` | Fixed manifests, token/invitation access and import acceptance/state | KD/KE/KS/KV/KL and existing quota/job APIs; KI for chat-copy extension |
| Full-project history mapping, T19 | `distribution/chatcopy/**`, `features/distribution/chatcopy/**` | Pinned chat-manifest/mapping extensions | KI/KH/KC; T16 owner/integrator controls base copy engine; all project chats/citations/source settings are mandatory; no content selector |
| Verification/evaluation, T13/T14 | `evaluation/**`, dedicated `frontend/e2e/integration/**` and `backend/src/test/**/integration/**` scenarios | No production feature schema | Root launcher/Compose changes through integrator; failures go to the feature owner |

This matrix restricts ownership rather than granting blanket access to shared parent directories. When two slices need the same test helper, DTO, table definition or route entry, its existing owner/integrator is the sole writer and consumer changes are integrated together.

### Feature migrations and integration coordination

1. Each slice lists the tables/columns/indexes it owns when its contract gate is agreed. T2 owns only migration infrastructure. Examples: users/sessions in T3, projects in T4, money/provider slots in T5, originals/jobs in T6, saved history in T8, vectors in T7, grants/exclusions in T20, answer provenance in T9, lifecycle journal in T11, project imports in T16, required copied-history mappings in T19.
2. Slice writers use isolated disposable test databases and task-specific SQL filenames. The integrator assigns final unique Flyway version numbers in actual integration order after prerequisite migrations are available. Tentative branch migration numbers must never be applied to user/shared persistent data.
3. Before merging, rebase on the integration baseline, assign the final unused version/name, and rerun clean-schema plus upgrade-from-baseline checks. An unapplied/disposable-branch migration may be renumbered; an applied integrated/non-disposable migration is immutable. Never enable out-of-order execution or rewrite applied history to repair a collision. One integration writer serializes this step, so merging a higher migration cannot silently strand a lower version.
4. A foreign key to another slice's table is a real migration prerequisite. For example T5 can develop after K0, but its user ownership migration cannot complete before T3's identity migration; project UI completion is irrelevant. T20 needs T6 document and T8 chat schemas. T9 needs vector/history/source schema. This is separate from preferred UI order.
5. Cross-owner table/interface changes require the provider owner and integrator to agree on the extension and impacted consumers. Keep the API contract and consumer checks in the same integration batch; no concurrent edits to common DTO/build/config files.
6. Publish a provider checkpoint when its agreed version/schema assumptions, usable provider interface and first-consumer checks pass, and record the agreement/availability in the contract index. This may precede the producing feature's remaining UI work. Mark a task complete only after its own full UI/API/database/test criteria and real completion prerequisites pass. Tests against controlled adapters alone cannot complete an integrated task. The integrator alone updates `docs/roadmap.md` and shared contract records; other writers report evidence and proposed edits.

### Parallel opportunities and actual limits

| Candidate pair | Earliest useful overlap | Independent ownership and completion caveat |
| --- | --- | --- |
| T3 accounts + T5 usage/model | T2/K0 agreed | Identity/auth files vs usage/model files; T5 signed-in UI/FK tests finish after T3, not T4 |
| T6 upload/extraction + T8 saved chats | KP agreed | Documents/jobs/parser UI vs saved-history UI; both finish against real T4 projects; no embeddings required |
| T7 indexing + T18 library reader | KD/KU and KD respectively | Indexing schema/worker vs PDF viewer; common document/status API changes go through T6/integrator |
| T18 reader + T20 source controls | KD and KH available | Viewer files vs archive/source-picker files; both use document state without editing its owner's base files |
| T7 indexing + T11 recovery | KD/KU and KP/KD/KH available | Indexing vs lifecycle/journal; shared lifecycle/job contract agreed first; T13 verifies combined races |

These are alternatives, not permission to start every ready pair at once. The integrator selects work whose start gates and exclusive paths are available. T12 integration and T13 harness work can begin early, but shared routing/configuration edits stay serialized and do not add a third writer. T14 functional provider checks can overlap later UI work only with explicit paid-call authority and independently passing focused safety checks; they share the existing global provider allowance, not a new budget. T13/T14 timed resource benchmarks run in a quiet reference environment, not alongside load tests or heavy builds that would contaminate their measurements.

## Requirement and constraint coverage

Each requirement maps to its owning vertical implementation and later integration/evidence. T2 supplies conventions, not every domain implementation. T12 verifies composition, not duplicate feature UI. Partial slice demonstrations do not claim the entire mapped requirement complete.

| Requirement | Owning slice and evidence |
| --- | --- |
| R1 | T3 Google registration/login/session UI/API/schema; T12 composition; T13 cross-account/session checks |
| R2 | T3 identity, T4 projects; each T6–T11/T16–T20 owns its resource authorization; T13 integrated owner/recipient/unauthenticated attack paths |
| R3 | T4 projects/counts; T6 document library/status; T18 retained PDF reader; T20 active/archive controls |
| R4 | T6 real parser/negative fixtures; T7 page-local indexing; T14 source-quality evidence |
| R5 | T6 first durable upload/extraction jobs; T7 indexing/retry; T11 purge fences; T13 restart/race integration |
| R6 | T4 empty project/library; T6 upload/status UI; T7 ready outcome; T8 saved chats; T12 consistency |
| R7 | T8 project-serialized chat creation; T20 grants/exclusions/automatic uploads; T7 readiness; T9 KS retrieval/revision checks; T19 copied grants; T13 races |
| R8 | T8 saved-history UI/API/schema; T9 answer persistence; T19 copied histories; T13 independent conversations |
| R9 | T9 evidence-only retrieval/answering through real history/data and KU; T14 semantic grounding/follow-ups |
| R10 | T9 validated citation provenance; T10 navigation extending T18 reader; T19 copied mapping; T14 semantic/page gate |
| R11 | T9 refusal/structural validation/UI states; T14 unsupported-question gate |
| R12 | T5 charged idempotency/admission; T6 reusable operation mechanics; T9 answer operation/UI; T13 duplicate/unknown-outcome tests |
| R13 | T20 archive/unarchive/grants and quota retention; T18 retained viewer; T11 no orphan purge; T13 lifecycle cases |
| R14 | T8 saved-chat deletion without PDF removal; T11 journal-aware parent deletion; T16/T19 distribution-manifest revocation for included chats; T13 replay |
| R15 | T4 parent revision/ownership; T11 real delete/purge/recovery UI; T6/T7/T9 consumer fences; T16/T19 original-link revocation and copy independence; T13 combined races |
| R16 | T3 session boundary; T5 server-only adapters; T6 parser isolation; T7/T9 minimal model inputs; T14/T15 retention evidence/docs |
| R17 | T4 account project limits; T6 storage/pages/files admission; T5 financial/global provider authority; T7/T9 bounded calls; T16 import admission reuse; T13 races/T14 actual usage |
| R18 | T1 local shell/toolchain; T2 persistence wiring; T3 IntelliJ/developer startup without sign-in/Google setup; each slice runnable locally; T12 single-origin integration; T15 repeatable distribution |
| R19 | T1/T15 no hosting prerequisite; hosted provider/controls/costs remain separately requested later work |
| R20 | T11 independent journal and restore replay; T16/T19 extend actual revocation integration; T13 old-backup tests; T15 documentation |
| R21 | Every slice owns meaningful tests; T13 integrated offline/performance evidence; T14 real-model gate; T15 truthful handoff |
| R22 | T6 first combined/web/worker durable execution; T7/T9/T11/T16 extend it; T13 two-web/two-worker proof; T15 role instructions |
| R23 | T3 JDBC sessions; T4 SQL ownership/lock boundary; T5 shared admission/accounting; T6 claims/fences; T20 grants/revisions; T11 recovery; consumers reuse those boundaries; T13 competing processes |
| R24 | T6/T11 shared local file/journal contracts; T13 load/corpus/failure report; T14 bounded provider-stage evidence; T15 clearly deferred multi-host storage/operations |
| R25 | T7 bounded embedding batches and completed-vector persistence; T20 ready unarchive reuse; T16 compatible ready-vector copying; T13 no-cache performance/embedding report; T14 actual endpoint data; no mandatory cache prototype |
| R26 | T16 fixed full-project invitation/link import foundation; T19 mandatory all-chat/history/citation/source mapping in the same pipeline; T11 lifecycle; T13 complete-contents/quota/independence/version/race checks |
| R27 | Withdrawn: no standalone chat snapshot feature; T17 dropped and excluded from completion |
| R28 | T20 fresh-retrieval exclusions; T9 keeps bounded prior answers/context and fences stale retrieval; T19 maps exclusions into copied chats; T13 no hidden history reset |
| R29 | T18 library/original PDF reader; T10 citation navigation; T16 destination files; T19 destination citations |

| Invariant or concern | Work/disposition |
| --- | --- |
| Per-user confidentiality and explicit sharing | Resource owners enforce ownership; T16 accepts explicit full-project imports; T13 verifies isolation; no standalone transcript access |
| Old-chat archives, new-chat exclusion, retained originals | T20 is the only eligibility owner; T9 consumes KS and T18 viewer remains separate; T19 remaps archive grants |
| Physical-page provenance and grounded/refused answers | T6 extraction, T7 immutable indexing, T9 validation/refusal, T10 viewer navigation, T14 actual grounding |
| No resurrection after delete/revoke | T11 parent/journal base; later T16/T19 install their own actual consumer integration before completion; T13 restore/worker races |
| Independent full-project copies | T16 creates fresh destination ownership/IDs; T19 maps all history; edits/deletions in either project never affect the other; no synchronization or shared mutable content |
| C1 hosted cost fit | Deferred hosting; T5 financial bounds, T13 resources and T14 actual usage inform a future cost decision |
| C2 archive/source UX | T20 owns controls; T18 owns library reading; T9 consumes retrieval-only choices; T12 checks navigation/copy consistency |
| C3 limits/languages/evaluation | Existing quotas/English-first criteria unchanged; feature tests and T13/T14 evidence before expansion |
| C4 unfinished/failed archives | T6 owns processing state; T20 visibility/cancellation; T7 uses the same fences and T13 verifies combined cases |
| C5 backup/provider retention | T11 local journal/restore; T16 replay integration; T15 transparent retention; hosted backup terms remain later |
| C6 review/operations/platform policy | Use the EGM workflow, fresh implementation review and selected project agent assignments. The remote-work guide governs native GitHub operations; hosted controls remain deferred and require actual authority |
| C7 Google identity/developer launch | T3 Google-only sign-in plus an explicit configured sign-in bypass for IntelliJ/local services. T3/T16 agree recipient identity; no application email/recovery service added |
| C8 dependency/model compatibility/access | Core checks T1, feature-library checks in its slice; T5 test adapters remain test-only; paid/account-quality limitations affect T14 only |
| C9 unknown total workload/capacity | T13 reproducible experiments, T14 bounded provider evidence; no invented throughput promise or budget increase |
| C10 no-cache performance | T7/T16 reuse ready embeddings; T13 report mandatory, cache prototype optional only with evidence |
| C11 retrieval-only exclusion | R28 settled behavior; T20 filter/T9 bounded prior context, T13 checks; no transitive history-filter graph |
| C12 project-import operating limits | T16 reuses quota/admission/jobs; T19 extends one pipeline; T13 measures before hosting |
| Preserve instructions/settings/tools | Preserve current records and native definitions; T1/T2 and EGM cutover are merged, T3 research/brainstorm is active, later implementation remains pending, and the concurrency proposal is unapplied |

## Open delivery inputs

T1/T2 and issue #24's EGM cutover are merged. Issue #3 has research/brainstorm and planning-audit authority only; T3–T20 implementation remains pending and unauthorized. The full task definitions, gates and ownership above govern later implementation requests. The specification owns target behavior and unresolved application-design concerns.

| Input | Current disposition | Affected work |
| --- | --- | --- |
| Google setup and developer launch | Google selected for normal application login; the IntelliJ/developer launch must work without sign-in/Google credentials. Settle development principal/configuration safeguards, atomic automatic account creation, administrator session-management designation/permission, recipient identity and real-provider acceptance; no Google project/client has been verified | T3/T12/T13/T15; T16 recipients |
| Representative PDFs/questions | Use redistributable/generated fixtures; preferred owner material remains optional | T6/T13/T14 |
| Languages and limits | Accepted baseline starting limits and English-first quality criteria remain unchanged; verify them in their owning features and expand only after evidence. Later explicit resource proposals and experimental targets retain their separate status | T6/T7/T13/T14; specification limits/quality gate |
| Capacity/concurrency | No traffic/corpus target was supplied; measure defined local experiments without claiming hosted throughput. Current concurrency is one; any increase requires a separate owner decision | T13/T14 and later sizing/dispatch decisions |
| Real provider access and paid-call scope | Credentials/account access and bounded call authority remain unresolved; ordinary offline work is independent | T14 and real-AI readiness only |
| Model quality | Execute recorded fixtures and numeric grounding/citation criteria; structural citation validation alone is insufficient | T14; T15 real-AI-ready claim |
| Hosted operations/email/provider controls | Optional later phase; establish actual permissions, security/operating controls and recovery before dependent release actions | Hosted operations, not local-v1 readiness |



## Completed foundation

T1 provides the Java 26 Maven reactor, shared BOM, React readiness shell, loopback persistent PostgreSQL/pgvector, profiles and same-origin package. T2 provides Flyway V1, typed safe MVC errors, correlation and K0, plus Maven-owned npm install/build/test. Both are merged; historical review rounds remain in their PR evidence, not a new EGM policy.

Final T2 deliberately removed synthetic persistence/adoption/restart/broken-migration and general HTTP smoke demonstrations. Its retained verification was 7 MVC cases, 1 real restrictive-Accept HTTP regression, 1 packaged extractor check and 3 browser checks. Dedicated existing-volume adoption coverage was retired; later fresh feature tests do not implicitly cover that special upgrade case. Concrete identity belongs to T3; real persisted data, migrations, ownership, spending and recovery checks belong to their actual feature owners and T13. See [K0](contracts/k0-foundation.md) and [test-scope research](research/java-spring-test-scope-review.md). Earlier larger suite counts are historical.

T3 identity/session/security research and two brainstorm rounds supplied the amended brief; Overview & Goals is approved and Technical Plan drafting is active, preserving accepted controls and later task ownership. Resolve disabled-mode identity, session/CSRF/error interfaces, permission evolution, audit semantics and module/persistence choices in its reviewed bounded design before implementation. Section approval is not completed feature-design acceptance. There is no retrospective T2 feature draft.
