# Microservices investigation

Date: 2026-10-26. Research recommendation only; this does not amend the accepted architecture or authorize implementation, hosting or spending.

## Starting point

This is a learning/portfolio application with private projects, three projects per account and ten retained text PDFs per project. Hosting is optional, with a $15–20/month small-demo target. The accepted Java 26/Spring Boot 4.1 application serves React and uses PostgreSQL/pgvector. It supports combined, web-only and worker-only roles from one codebase, with shared durable jobs, leases/fences and admission. These are design requirements: actual job execution is still planned in T6; the existing extractor only implements a `--version` packaging probe. [Intent](../scopes/initial-application/intent.md), [limits](../scopes/initial-application/spec.md#limits-and-user-experience), [architecture](../scopes/initial-application/spec.md#d-a-one-application-with-clear-internal-responsibilities), [T6](../scopes/initial-application/plan.md#slice-implementation-and-reuse), [extractor source](../../pdf-extractor/src/main/java/com/aiknowledgebase/extractor/ExtractorMain.java).

## What separation would buy

Microservices can release and scale independently, isolate failures and choose different technologies. They also introduce network latency and partial failures, versioned contracts, distributed consistency, dependency testing and operational coordination. These advantages matter when boundaries have genuinely different change, ownership or resource needs. [Microsoft architecture guidance](https://learn.microsoft.com/en-us/azure/architecture/guide/architecture-styles/microservices).

Independent worker scaling does not require splitting identity, projects and chats into services. Microsoft's web-queue-worker style explicitly supports separate scaling of the web and background components. This supports the accepted role separation concept; its Azure products and cache examples are not requirements for this repository. Multiple copies of the same worker using one application job table remain replicas of that application. [Web-queue-worker guidance](https://learn.microsoft.com/en-us/azure/architecture/guide/architecture-styles/web-queue-worker), [shared coordination](../scopes/initial-application/spec.md#d-i-shared-coordination-and-staged-growth).

Autonomous services own their data and expose contracts; arbitrary access to each other's tables undermines independent evolution. Sharing a PostgreSQL server is distinct from sharing ownership of its tables: autonomy does not inherently require one physical database server per service. The proposed application deliberately shares an authoritative schema across its processes instead. [Data ownership guidance](https://learn.microsoft.com/en-us/dotnet/architecture/microservices/architect-microservice-container-applications/data-sovereignty-per-microservice), [accepted storage design](../scopes/initial-application/spec.md#d-b-own-the-retrieval-data-in-postgresql).

## Project-specific comparison

The following assessments are inferences from the requirements and architecture guidance above.

| Option | Project benefit | Project cost or limitation | Reassess signal |
| --- | --- | --- | --- |
| Plain modular Spring Boot plus workers | Preserves common authorization/lifecycle transactions; allows separate web/worker resource allocation | Coordinated application/schema releases; shared database remains a dependency | Measured contention, release coupling or materially different resource requirements |
| Broad business microservices | Independent domain releases, ownership and resource allocation; substantial distributed-systems learning | More deployments, contracts, failure testing and distributed lifecycle/accounting work | Stable autonomous domains, separate maintainers or a specific release/scaling bottleneck |
| Narrow extraction service | Independent parser release, runtime, hardware placement or security boundary without distributing all business domains | Adds authenticated private-file transfer, remote failures, result compatibility and revocation coordination | Parser-specific operational needs beyond what a bounded child JVM provides |

The difficult boundaries here concern correctness, not simply CPU. Private file reads depend on current ownership; deletion must revoke access immediately and remain revoked after journal replay during restore. Archive eligibility depends on chat creation, while accepted copies survive source deletion and published snapshots must be revoked. Global AI caps and spending authority must remain global. Splitting these responsibilities requires explicit cross-service enforcement; eventual consistency cannot silently weaken these guarantees. [Requirements R2, R7, R15, R17, R20, R23, R26–R28](../scopes/initial-application/spec.md#requirements).

## Assessment and limits

My recommendation is to retain the accepted application/worker shape initially. More extraction capacity alone does not establish a reason to split the business domains. A narrow extraction service is a credible later alternative when measured isolation or independent-release needs justify it; the planned child JVM already supplies process separation. [Extraction design](../scopes/initial-application/spec.md#d-c-page-preserving-restartable-ingestion).

For learning, broad microservices are defensible if distributed operations become an explicit project objective, with corresponding scope and budget review. They add operational effort relevant to the small-demo target, but this investigation establishes no hosting price, throughput or capacity claim. No benchmark or deployment was performed; future multi-host operation still requires shared private storage and an independently retained shared deletion log. [Growth requirements](../scopes/initial-application/spec.md#d-i-shared-coordination-and-staged-growth).
