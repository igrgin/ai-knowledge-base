# Development-agent assignments

The project owner accepted these assignments in decision D14 in [the change index](../changes/20261023-ai-knowledge-base/change.json). This policy covers development-agent models and reasoning effort. The application's RAG model remains defined in the [specification](../changes/20261023-ai-knowledge-base/spec.md#d-h-measured-ai-choice-and-bounded-use).

| Role | Assignment | Workload adjustment |
| --- | --- | --- |
| Orchestrator/integrator | GPT-6.1 Sol, high | Medium for routine status and coordination |
| Implementation | GPT-6.1 Sol, medium | High for authorization, private files, financial admission, leases, migrations, purge/recovery and sharing boundaries |
| Independent technical reviewer | GPT-6.1 Sol, high, in a fresh context | Higher effort for difficult security or concurrency analysis |
| Difficult escalation | GPT-6 Astra, high | Xhigh for a bounded unresolved problem |
| Narrow mechanical work | GPT-6 Luna, low or medium | Use Sol when interpretation or judgment is required |

Use model IDs `gpt-6.1-sol`, `gpt-6-astra` and `gpt-6-luna`. These assignments do not change global Codex settings or the active chat's model. Apply them to the appropriate roles when work is authorized. Current task concurrency remains 1; independent review runs after implementation work yields its slot. The proposed increase to 2 remains unadopted.

These are role assignments only. Agent billing and development usage budgets are outside repository scope. Application spending controls remain unchanged.
