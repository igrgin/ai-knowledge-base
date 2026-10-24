# Hosting and AI cost proposal

Researched on 2026-10-24 for the v1 learning/portfolio demo. Amounts below distinguish published rates from calculated scenarios. No account, resource, subscription, or API workload was created. Provider availability, account eligibility, and checkout pricing must be checked again before deployment.

## Owner choices and delivery phases

During design the owner changed hosting from a required v1 finish line to an optional later phase, accepted a managed platform, and selected a **$15–20/month target before tax and optional domain costs**. The latest PDF lifecycle instruction is **archive only within a project, with no individual PDF deletion option**; archived PDFs remain sources for old chats and citations but are excluded from new chats. Whole-project deletion still removes everything in it. The current [intent](intent.md#constraints) and [requirements](spec.md#requirements) incorporate these choices; no older draft is needed to interpret this cost note. Sources are the 2026-10-24 replies in `codex://threads/01a1699d-af41-71f0-a5e7-c75152955b39`, including “lets leave hosting as optional part of a later phase”, “Keep it around $15–20/month”, and the final message beginning “you know what lets remove pdf deletion as an option”.

Phase 1 is the complete application running locally with real external AI calls when the owner supplies credentials. Hosting costs are zero in that phase; local hardware/electricity are not estimated here. External AI still incurs usage charges. Phase 2 may publish a managed live demo if measured costs fit the target and the owner separately authorizes it.

## Proposed optional hosting

Propose Railway Hobby for one application service, a PostgreSQL service with pgvector, and a private PDF bucket. Serve the built React frontend from Spring Boot so that the browser uses one origin. This keeps the deployment to two compute services and avoids a separate paid frontend or ingestion worker. Railway hosts the infrastructure; database versioning, extensions, backup configuration, and restore verification still need a project owner. Its standard PostgreSQL template does not include arbitrary extensions, so the pgvector image/template must be verified during implementation planning. [Railway PostgreSQL](https://docs.railway.com/databases/postgresql), [private storage pattern](https://docs.railway.com/guides/storage-buckets-guide)

Provisional infrastructure envelope: **$15–25/month** for a lightly used demo. Because the owner selected a lower $15–20 combined target, hosting is conditional: propose at most **$17/month for infrastructure and $3/month for application-enforced AI allowance**, with no purchased domain or email service in the initial quote. The low-use example below can fit near $20, but the upper infrastructure estimate cannot. Benchmark before hosting; if costs exceed the target, keep the local release available and return the cost/size tradeoff to the owner. These are proposals and operating allocations, not a fixed bill or authorization to spend.

## Published hosting rates and calculated example

Railway Hobby has a $5 minimum that includes $5 of resource usage. Usage above it is charged; the subscription is not added a second time to the total. The rates read were $0.00000386/GB-second of RAM, $0.00000772/vCPU-second, $0.00000006/GB-second of volume storage, $0.05/GB service egress, and $0.015/GB-month object storage. [Railway pricing](https://railway.com/pricing)

A 30-day month has 2,592,000 seconds. This illustrative workload uses aggregate resource consumption across the app and database, rather than treating a configured maximum as actual average usage:

| Assumption | Calculated monthly cost, USD |
| --- | ---: |
| Average 1.25 GB RAM total: 0.75 GB app + 0.50 GB database | 12.51 |
| Average 0.10 vCPU total | 2.00 |
| 2 GB PostgreSQL volume | 0.31 |
| 5 GB PDF object storage | 0.08 |
| 5 GB application egress | 0.25 |
| Base infrastructure total, rounded after summing unrounded values | **15.14** |

The example excludes actual backup storage, build/deploy overhead, taxes, custom domains, email, and excess usage. The $15–25 envelope provides some headroom but does not measure those components or guarantee they fit. At 2 GB average aggregate RAM and 0.20 average vCPU, the same storage/egress assumptions produce about **$24.65/month before backups and AI**. A launch benchmark must measure idle RAM, PDF-processing peaks, database size, and daily cost, then reassess the budget rather than silently increasing it. Railway bucket storage and database volume size are separate limits; the Hobby PostgreSQL volume limit must accommodate vectors, extracted text, indexes, and ordinary data, not just PDFs. [Rates and limits](https://railway.com/pricing)

The database runs continuously. This proposal does not count on service sleeping or free-tier credits to remain within budget. A provider domain can serve the initial demo; choosing a purchased domain is separate.

## Hosting alternatives

| Option | Published or calculated baseline | Assessment |
| --- | --- | --- |
| Railway | Calculated $15.14/month example above; budget $15–25 infrastructure | Proposed default to focus on application learning; usage billing requires monitoring and measured sizing |
| Hetzner CX33 | Published €8.49/month server + €0.50 IPv4 + 20% server backup = calculated **€10.69/month excluding VAT** | Conditional alternative only: public listing currently marks CX33 unavailable; server administration and application-consistent backups belong to the project |
| Hetzner CPX22 | Published €19.49/month server + €0.50 IPv4 + 20% server backup = calculated **€23.89/month excluding VAT** | Also marked unavailable on the public page at research time; do not claim an orderable server or include it as the launch baseline |
| Render | Paid web service and database are separate compute charges; no reliable complete current total established from the dynamic pricing page | Consider if the owner already prefers it; no numeric budget recommendation based on an incomplete quote |

Hetzner rates: [price adjustment notice](https://docs.hetzner.com/general/infrastructure-and-availability/price-adjustment/), [IPv4 charge](https://docs.hetzner.com/cloud/servers/overview/), [backup pricing](https://docs.hetzner.com/cloud/billing/faq/). Availability: [cost-optimized plans](https://www.hetzner.com/cloud/cost-optimized/), [regular-performance plans](https://www.hetzner.com/cloud/regular-performance/). These server totals exclude application-consistent/off-host backup costs, domain, AI, tax, and maintenance time. No EUR/USD conversion is assumed. [Render pricing](https://render.com/pricing)

## AI baseline and rates

Propose OpenAI `gpt-6-luna`, with reasoning effort set to `none`, for an initial cost/quality evaluation; `text-embedding-3-small` at its normal 1536 dimensions for embeddings. GPT-6 Luna supports text output, structured output, and both Responses and Chat Completions. API account availability and Spring AI option compatibility remain implementation checks, not established by documentation alone. [Model capabilities](https://developers.openai.com/api/docs/models/gpt-6-luna), [embeddings guide](https://developers.openai.com/api/docs/guides/embeddings)

Published standard prices per million tokens:

| Model | Input, USD | Output, USD | Role |
| --- | ---: | ---: | --- |
| gpt-6-luna | 0.10 | 0.50 | Proposed first evaluation candidate |
| gpt-4.1-mini | 0.40 | 1.60 | Alternative evaluation baseline if instruction/citation quality is better |
| text-embedding-3-small | 0.02 | n/a | PDF passages and search queries |

[Official OpenAI pricing](https://developers.openai.com/api/docs/pricing). No quality equivalence between the models is assumed. The alternative supports structured output and operates without a reasoning step. [GPT-4.1 Mini capabilities](https://developers.openai.com/api/docs/models/gpt-4.1-mini)

### Answer cost example

Assume one generation per answer, 6,000 input tokens including instructions, selected source passages, recent chat context, and the question; and 700 total billed output tokens, including any reasoning. Do not send the whole project or unbounded chat history every time.

`cost per answer = (input tokens × input rate + billed output tokens × output rate) / 1,000,000`

| Answers/month | GPT-6 Luna, USD | GPT-4.1 Mini, USD |
| --- | ---: | ---: |
| 1,000 | 0.95 | 3.52 |
| 5,000 | 4.75 | 17.60 |
| 10,000 | 9.50 | 35.20 |

This is a calculated scenario, not a measured average. Longer replies, hidden reasoning, retries, query rewriting, extra validation calls, and larger history/context increase usage. No discount for caching, batch, or regional processing is assumed. For models with cache-write charges, reserve using the applicable maximum input/cache-write rate until measured usage establishes the actual billing behavior. No paid hosted file-search, web-search, or provider vector-storage tools are part of the proposal.

### PDF embedding example

Assume 200 pages × 800 text tokens/page × 1.20 for additional overlapping text = 192,000 embedded tokens. At $0.02/million tokens, one such PDF costs approximately **$0.00384** to embed. A hundred such PDFs cost **$0.384**. Actual text density, chunk boundaries, repeated uploads/retries, and tokenization vary. This estimate excludes compute, stored files, and stored vectors. An 80-token question embedding adds about $0.0016 per 1,000 questions. [Embedding rates](https://developers.openai.com/api/docs/pricing)

For 1,000 illustrative answers plus 100 illustrative PDFs and their question embeddings, GPT-6 Luna AI usage is approximately **$1.34/month**. With the $15.14 infrastructure example, the combined calculated base is about **$16.48 before backup costs, overhead, taxes, and domain/email**. This leaves only about $3.52 within the owner's $20 upper target for the excluded costs. Public registration means per-user quotas alone cannot bound total spend. If measured baseline/backup costs do not fit, optional hosting waits for an owner decision rather than assuming free services or raising the budget.

## Enforceable cost behavior

Propose separate owner-selected infrastructure and AI allocations. Within the application, reserve a conservative upper bound before each AI request using the selected model's current rates, actual input tokens and a bounded maximum output. Reservations and settlements are atomic across workers; unknown outcomes retain their reservation until reconciled. Ingestion reserves its entire planned embedding cost before starting provider work. A global monthly AI ceiling stops new paid generation and ingestion; browsing and deletion remain available. Per-user and per-IP limits reduce bursts but do not replace that global ceiling. The chosen monthly ceiling and reset timezone still need owner acceptance.

Railway's compute hard limit takes workloads offline; it is distinct from an alert and from Railway Agent limits, which do not control this app's external OpenAI calls. Provider billing updates can lag. The proposed application ledger bounds planned API work, not unrelated credentials or an absolute tax-inclusive invoice. A native compute limit must be set by the actual workspace administrator with an understood outage consequence. [Railway cost controls](https://docs.railway.com/pricing/cost-control)

## Privacy and deletion boundary

The app retains its own files, embeddings, and histories, sends text directly to embeddings/chat endpoints, and disables optional provider response storage. It does not upload original PDFs to provider file/vector-store endpoints. OpenAI's endpoint documentation says these API inputs are not used for training by default; standard abuse-monitoring content may be retained up to 30 days, with documented exceptions. `store: false` does not establish zero retention. [OpenAI data controls](https://developers.openai.com/api/docs/guides/your-data)

A PDF removed from active project use is archived rather than individually deleted. Original files, chunks, embeddings, and backups remain stored until the project itself is deleted, even when the PDF has no chat references. Existing chats and their citations retain access; new chats exclude archived PDFs. Archive operations therefore do not free storage or embedding quotas. Whole-project deletion removes all project contents from active application storage through a tracked purge. Backup and external-provider retention are disclosed separately; they do not permit ordinary application access after project deletion.

The proposed deployment region is Europe for app/database/files. This does not assert EU-only processing at the AI provider: that requires eligible accounts, model/endpoint choices, and pricing checks. The demo privacy notice and deletion UX must describe the actual provider and backup retention. Immediate deletion from every provider log or backup is not promised.

## Owner choices

`project-owner` has selected optional later managed hosting, the $15–20 monthly target, and archive-only individual PDF handling. Proposed quotas, backup retention, AI model quality gates, and any account-email workflow remain identified in `spec.md`. Budget allocations are design choices, not authorization to create resources or incur charges.
