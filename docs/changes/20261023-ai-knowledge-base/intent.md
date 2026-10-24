# Intent for an AI knowledge base with PDF chat

This is the current consolidated intent. It carries the owner's continuing decisions and later changes together in one document. Detailed behavior is in [Requirements](spec.md#requirements); implementation work is in the single [Revised task list](plan.md#revised-task-list). Original owner statements remain identified in `change.json`; they are decision provenance, not alternative plans to implement.

## Problem

Build a learning and portfolio application that lets people organize text PDFs into projects and explore their contents through saved AI conversations. Answers should be grounded in the project's eligible documents and include page citations that users can inspect. The application must say when those documents cannot support an answer.

## Affected users and systems

- Anyone can register and log in. Each project belongs to one account; its original files, chats and private APIs remain owner-scoped.
- Project distribution creates independent copies for registered recipients through named invitations or registered-user links. This is not collaboration in the original project.
- Separately published chat snapshots are fixed, read-only views, optionally public without login; they grant no original PDF, project API or AI access.
- Java 26 and Spring Boot provide the backend; React and shadcn/ui provide the frontend. PostgreSQL/pgvector, private original-file storage and an external AI provider support the selected design.

## Desired outcome

1. Register, log in and create a private project containing at most 10 retained PDFs, including archives.
2. Upload text PDFs and observe durable processing, ready or failed states. Preserve physical-page provenance when extracting text, creating passages and storing embeddings.
3. Open retained originals from the project library in a simple PDF viewer, including through citations at the actual cited page. File viewing is distinct from AI-source eligibility.
4. Create separate saved chats per project, reopen their history, and receive grounded answers with usable citations or an explicit insufficient-evidence response.
5. New chats start with ready active files enabled. New active uploads automatically become available to existing chats when ready.
6. Archive/unarchive individual files without deleting their originals, text, embeddings or citations. Archives retain quota usage. Chats already present at archive time retain source eligibility; new chats exclude a file while it is archived.
7. Exclude/re-enable otherwise eligible files within one chat. Exclusions stop new retrieval only; earlier answers may remain in bounded conversation context and visible history/citations remain intact. A retrieval toggle does not guarantee that later discussion is uninfluenced by earlier answers.
8. Share a fixed project version captured when creating an invitation/link, either with files alone or with all saved chats. Create a new share for updates. Accepted imports have their own owner, identifiers, quotas and files; later source changes, source deletion or link revocation do not erase those copies.
9. Explicitly publish, update or revoke a fixed read-only chat snapshot. Later private messages appear only after explicit republication. Revocation or deletion of its source chat/project disables the published view; it is not imported as a loose chat into someone else's account.
10. Delete saved chats without deleting project PDFs. Delete a whole project through immediate revocation and tracked purge of its own active/archived files and derived data. Revoke its distribution links and published snapshots; independently accepted project copies remain intact.
11. Run the full core flow locally with repeatable commands and persistent state. Demonstrate two-user isolation, archive/source rules, independent copies, snapshots, restart recovery and deletion/revocation behavior. Hosting is optional later work.

## Constraints

- Preserve project guidance, policies and tools. Use Java 26 as the project compilation, test and runtime default, including the PDF-extraction child process.
- Support text PDFs; OCR and non-PDF ingestion are outside v1. Keep separate conversations scoped to their owned project.
- Send only bounded necessary content to an external AI provider; keep credentials server-side. Explain external processing and retention. Treat PDF text as untrusted evidence.
- Store extracted text, passage embeddings, saved answers and operation identities durably. Reopening, source selection and ready-file unarchive reuse existing data. Compatible authorized copies can reuse completed embeddings rather than requesting them again.
- Start without Redis or Caffeine. Measure embedding batching/reuse and actual bottlenecks; no cache prototype is required to complete local v1.
- Support a growth path with separately runnable web/worker roles and shared durable coordination. Keep access, global quotas, provider concurrency and spending authority correct across processes. Verify locally without claiming an unmeasured hosted capacity.
- Keep the $15–20/month target before tax/domain costs for an optional small hosted demo. Larger capacity and hosting need separate assessment/authorization. Credentials or a budget target do not authorize spending.
- Retain files and citations through archive and chat deletion. Do not provide individual PDF deletion or an automatic orphan purge. Whole-project removal, backup expiry and independent provider retention are distinct; recovery must not revive deleted projects or revoked shares.
- Existing file/page/token, account, AI and quality bounds remain in [Limits and user experience](spec.md#limits-and-user-experience) and [Verification and quality gate](spec.md#verification-and-quality-gate).
- The owner adopted the [review policy](../../policies/review.md), [GitHub workflow](../../policies/github-workflow.md) and [agent assignments](../../policies/development-agents.md) through D14. Lighthouse is globally installed. Operations/platform controls remain to be configured before their dependent actions; D19 subsequently authorizes T1 only; later task implementation remains unauthorized.

## Exclusions

No OCR, non-PDF ingestion, individual file delete, live project collaboration, web-search answers, provider-hosted PDF/vector storage or answer streaming is added. Dedicated quiz/summary/comparison features are not required; ordinary grounded questions asking for summaries or comparisons are supported. Hosted infrastructure, email delivery/verification/recovery and production release operations remain optional later work.

## Open questions and dispositions

Product choices are settled through the owner statements referenced in `change.json`. Remaining inputs are implementation evidence or later operational choices, not competing product plans.

| Input | Current disposition | Owner and affected work |
| --- | --- | --- |
| Representative PDFs/questions | Use redistributable/generated fixtures; preferred owner material remains optional | project-owner; T6/T13/T14 fixtures |
| Languages and limits | English-first acceptance and the existing configured bounds; expand only after evidence | project-owner; current specification and T6/T7/T14 |
| Sharing/archive/context semantics | Fixed independent project copies; revocable transcript snapshots; old-chat archive eligibility and retrieval-only per-chat exclusions, as stated above | Resolved product choices; T16/T19/T17/T20/T9 |
| Traffic/corpus capacity | No user target supplied; perform reproducible local experiments and report measured limits | project-owner; T13/T14, future hosting |
| Provider access and paid scope | Test adapters stay test-only; actual provider evaluation needs credentials and bounded authority | project-owner; T14 only |
| GitHub/review/operations setup | D14 adopts GitHub/review/model rules and global Lighthouse use; D17 setup is complete; D19 authorizes T1 only; later operations and releases remain separately gated | project-owner; setup/review/release work |

## Sources

| Source | Identifying information | Current content |
| --- | --- | --- |
| `codex://threads/01a1699d-af41-71f0-a5e7-c75152955b39` | Owner messages and replies recorded as D1–D14 in `change.json` | Product purpose/stack, optional hosting/budget, final PDF lifecycle, Java 26, scalability, sharing, source context and vertical-slice planning directions |
| [Requirements and design](spec.md#requirements) | Current R1–R29 and design sections | Detailed behavior, invariants, interfaces, constraints and verification |
| [Implementation plan](plan.md#revised-task-list) | Current T1–T20 with start/completion gates | One implementation plan, reuse, ownership and conditional parallel-work proposal |
| `docs/lighthouse-adoption.md`, `lighthouse.json` | Project ownership and configured policy/control sources | `project-owner` owns the decision roles; actual platform identities are not inferred |

Implementation, publication, deployment and paid calls remain separate actions requiring their applicable owner authority. This cleanup does not record a new approval of those actions.
