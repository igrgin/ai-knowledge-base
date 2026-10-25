# Project settings

## Development-agent assignments

The owner selected these project settings under D14 in the [original decision source](https://github.com/igrgin/ai-knowledge-base/blob/7252f046b96f9a33e94f095f2b8b7790b76785ad/docs/changes/20261023-ai-knowledge-base/change.json).

| Role | Assignment | Workload adjustment |
| --- | --- | --- |
| Orchestrator/integrator | GPT-6.1 Sol, high | Medium for routine status and coordination |
| Implementation | GPT-6.1 Sol, medium | High for authorization, private files, financial admission, leases, migrations, purge/recovery and sharing boundaries |
| Independent technical reviewer | GPT-6.1 Sol, high, in a fresh context | Higher effort for difficult security or concurrency analysis |
| Difficult escalation | GPT-6 Astra, high | Xhigh for a bounded unresolved problem |
| Narrow mechanical work | GPT-6 Luna, low or medium | Use Sol when interpretation or judgment is required |


Use model IDs `gpt-6.1-sol`, `gpt-6-astra` and `gpt-6-luna`. The application's RAG model is defined separately in the [specification](spec.md#d-h-measured-ai-choice-and-bounded-use).
