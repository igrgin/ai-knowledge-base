# Lighthouse adoption

Adopted on 2026-10-23 for `ai-knowledge-base` through the user's explicit `lighthouse-init` invocation. This record describes adoption and pending project decisions; it is not an architecture, security, review, or operations policy.

## Existing sources and preservation

The target directory was empty, with no Git metadata, root or scoped instructions, application code, task definitions, project policies, ownership records, provider workflows, or project Codex settings. No `AGENTS.md` was found in its ancestor directories or the Codex home. Local Git metadata was initialized for Lighthouse's Git prerequisite. No commit, remote, branch policy, or application toolchain was configured.

The user's instruction to preserve existing instructions, policies, and tools remains the adoption constraint. Existing Codex configuration, model and effort settings, and provider tools were preserved.

## Ownership

The project owner is the human user in the adoption conversation. Their answer to the ownership question was: “I do. I'm not sure I understand this question, but I am the one that will define these.” The question named intent, design, plan, code review, and release roles. One person therefore holds all five roles under the `project-owner` person ID in `lighthouse.json`. No separate approvers are required by this mapping. It establishes role responsibility, rather than accepting any future plan, policy, code candidate, or release.

Original ownership source (recover this conversation before relying on the decision after resume):

```json
{
  "providerRef": "codex://threads/01a1699d-af41-71f0-a5e7-c75152955b39",
  "selector": "2026-10-23: reply to the question beginning 'Who holds the project’s intent, design, plan, code review, and release roles?'; reply begins 'I do.' and ends 'I am the one that will define these.'"
}
```

No message-level locator or immutable provider revision was supplied. The user has not supplied a personal name or provider account identity; the configuration identifies them through this conversation rather than inferring identity from a filesystem username. The project owner defines the policy choices below and identifies any additional policy maintainers or platform administrators when needed. Actual platform control ownership remains unresolved until a platform exists and its native authority is known.

## Configuration and source map

| Mapping | Source and current treatment |
| --- | --- |
| Project decision roles | Original user reply above; all five roles map to `project-owner` |
| Project instructions | Root `AGENTS.md`; no prior project instructions existed |
| Record layout | Default `docs/changes` and `docs/incidents`; default filenames and sections apply |
| Architecture, security, operations, UX | No separate policy sources configured; product invariants are in the current specification |
| Review | Owner adopted `docs/policies/review.md` through D14; it is mapped in `lighthouse.json` |
| Risk and route rules | No project overrides recorded; packaged defaults apply. Joint acceptance remains a proposal until the owner accepts it; uncertain eligibility selects the fuller route |
| Commands | No task definitions found; map actual definitions once the application toolchain exists |
| Protected actions | No project-required controls or enforcing platforms identified; no control mappings authored |
| Optional capabilities | Review provider unconfigured; concurrency uses the default of one task; operational jobs disabled |

Empty policy, command, and control objects mean no configured entries. They do not establish that future work needs no policy or enforcement. Read the relevant stage's input contract as that work begins.

## Unresolved choices

All project choices below belong to `project-owner`, who may name additional maintainers. They limit the indicated work, rather than preventing unrelated intent capture or adoption authoring.

| Choice | Affected work and next resolution |
| --- | --- |
| Product scope and architecture | Define intended users, behavior, system boundaries, technology choices, and authoritative design sources when intent/design work begins |
| Security policy and maintainer | Establish data sensitivity, access rules, secrets handling, and applicable security constraints before dependent design, implementation, or operations |
| Review policy and maintainer | Resolved by D14: project owner adopted one fresh reviewer for bugs/security/requirements, blocking/advisory rules, two correction rounds and review refresh in `docs/policies/review.md` |
| Route choices | Decide whether to accept bounded routing and joint planning acceptance. Preserve packaged fuller triggers for authentication, authorization, persistent data, infrastructure, and public interfaces unless an authorized exception is recorded |
| UX guidance, if applicable | Identify interaction rules and their source before affected UX decisions |
| Operations and platform authority | Identify environments, release/recovery route, incident authority, actual platform/control owners, approval records, and native enforcement before dependent operational actions |
| Task definitions and capacity limits | Choose application commands from the eventual toolchain and identify affected system limits when load or concurrency matters |
| Git workflow | D14 adopts `docs/policies/github-workflow.md`; actual repository/resources/commits remain pending separate setup authorization |

Optional review providers and operational automation require no selection for this adoption. If requested later, map their actual sources, owners, and limits through the package contracts. No scheduler, credentials, provider permissions, or protected-action controls were configured or activated.

## Current installation and agent assignments

Under D14, Lighthouse remains globally installed and the repository's `.codex/config.toml` enable flag is removed. There is no repository-local plugin package. See `docs/development-tooling.md` for host/worktree availability and `docs/policies/development-agents.md` for the accepted model/effort assignments. Global Codex settings are unchanged. Development-agent billing and usage budgets are outside this repository workflow.

## Authorized GitHub setup follow-up

On 2026-10-25, D17 separately authorizes the public repository, initial commit, 20 task issues/mapping and workflow protections. D18 confirms `igrgin` and the GitHub noreply address as the repository-local identity and `igrgin` as MIT copyright holder. The authenticated `igrgin` account has repository administration authority. This resolves the setup identity input without retroactively changing the original adoption provenance above. See [the setup baseline](github-setup.md) for settings and boundaries. The earlier table describes adoption-time inputs; application/platform operations and implementation remain deferred. The empty Lighthouse `commands`/`controls` mappings do not claim a native platform check is absent or replace its actual GitHub configuration.
