# T1 runtime shell review

## Comparison

| Field | Value |
| --- | --- |
| `mode` | committed |
| Original base | `origin/main` |
| Original candidate | PR #21 issue branch, D20 revision |
| `base` | `{"kind":"gitCommit","identity":"b203ffd17063f65f06e0e4bea3cb49e9e68cdc44"}` |
| `candidate` | `{"kind":"gitCommit","identity":"a043cf1522be32250d9b9533370cd979e53d2d59"}` |
| Merge-base | `b203ffd17063f65f06e0e4bea3cb49e9e68cdc44` |
| Previous reviewed head | `386b652f9e3156f4ecf63d764b3c6fc231eb02fc` |
| PR | [#21](https://github.com/igrgin/ai-knowledge-base/pull/21) |

Scope is T1 only, including the owner's D20 README/configuration/testing revision. Later tasks and deployment are not part of this completion claim. The coordinator persists this report after the implementation comparison; final metadata-only descendant freshness is recorded in the [PR assessment](https://github.com/igrgin/ai-knowledge-base/pull/21#issuecomment-6390736664).

## Material inputs

| Input | Source and revision | Scope |
| --- | --- | --- |
| Requirements/design | `spec.md` at `b203ffd17063f65f06e0e4bea3cb49e9e68cdc44` | R18 local runtime portion, R19 optional hosting, D-A/D-G and applicable invariants |
| Accepted plan | `plan.md` at `b203ffd17063f65f06e0e4bea3cb49e9e68cdc44` | T1, pins, command contracts and ownership |
| Initial authority | D19 in `change.json` at `386b652f9e3156f4ecf63d764b3c6fc231eb02fc` | User-authorized T1 implementation and sequential review, no merge |
| Owner revision | D20 and plan revision section at `a043cf1522be32250d9b9533370cd979e53d2d59` | Product/startup README, shared/dev/prod/test/lvl2test configuration and test classification |
| Review policy | `docs/policies/review.md` at `b203ffd17063f65f06e0e4bea3cb49e9e68cdc44` | One fresh reviewer, three passes, finite correction limit |
| Workflow and assignments | `docs/policies/github-workflow.md`, `development-agents.md` at `b203ffd17063f65f06e0e4bea3cb49e9e68cdc44` | Issue branch, Sol implementation/review and human merge |
| README reference | [Voice Draft README](https://github.com/igrgin/voice-draft/blob/main/README.md) | Owner-selected product/startup structure; its supplied file SHA was a blob identity, not a commit |

Separate architecture/security/operations policy sources remain unconfigured as identified in adoption. Applicable scoped specification, plan and direct owner constraints supplied T1 expectations; no unresolved T1 policy choice prevented coverage. This assessment does not establish hosted-production or later-feature readiness.

## Reviewer context

One fresh GPT-6.1 Sol high reviewer performed the original three passes and subsequent refreshes report-only, without the implementation conversation. The Sol medium implementer yielded before each review. No separate pass agents or concurrent writers were used. The reviewer changed no source, records or repository state.

## Pass coverage

| Pass | Coverage | Assessed scope and limits |
| --- | --- | --- |
| Bugs | covered | Full revision delta and affected base-to-current profile loading, launcher aliases, production validation, Maven selection, packaged smoke configuration and README commands. Original frontend/database/extractor/build coverage retained after equivalence checks. |
| Security | covered | Production missing/blank-value failure, no local credentials in base/prod, test/runtime resource and dependency separation, bindings and owned-resource cleanup. No missing required T1 coverage. |
| Requirements compliance | covered | D20 README removals/scaffolding, common static base, dev/prod overrides, required production inputs and functional basic versus full-Spring/Testcontainers split. T2 schemas/general fixtures, hosting, AI and later features remain excluded. |

## Verification

Implementation evidence, distinct from independent review:

- `DOCKER_HOST=unix:///nonexistent-basic-test-docker.sock ./mvnw -B test`: four backend configuration checks and extractor child launch passed without Docker or full application startup.
- `./mvnw -B -Pintegration-tests verify`: real full Spring startup and pinned-pgvector `ReadinessIT` under `lvl2test` passed, including vector SQL, HTTP 200 and database-loss HTTP 503.
- `./scripts/verify`: locked frontend build, basic/integration tests, packaged browser, outage/recovery, persisted restart and backend restart passed.
- Direct local launcher selected `dev` and served actual readiness on `APP_PORT=18083`. JAR inspection found only base/dev/prod YAML.
- [Repository checks](https://github.com/igrgin/ai-knowledge-base/actions/runs/40143650017) and [Runtime checks](https://github.com/igrgin/ai-knowledge-base/actions/runs/40143649974) passed at `a043cf1522be32250d9b9533370cd979e53d2d59`. Runtime CI explicitly checked basic tests without Docker access before the integration/browser suite.

Independent reviewer checks:

- Retrieved successful candidate CI and verified HEAD, origin/main, merge-base and PR identities; inspected local basic/integration/browser/persistence/restart evidence.
- Inspected the runtime JAR: test profiles and Testcontainers absent.
- Launched packaged `prod` with blank `DATABASE_PASSWORD`, including a canonical datasource-password override: validation rejected startup and the selected port did not serve.
- Launched with complete required production inputs and an explicitly unreachable database: supplied free loopback port bound, packaged UI served, readiness returned HTTP 503 and exactly `{"status":"DOWN"}`.
- Terminated only reviewer-owned processes; development Docker data and unrelated listeners were untouched. Repository and fixed-range whitespace checks passed. Focused logs are ignored `.local/review-round1-prod-empty.log` and `.local/review-round1-prod-complete.log`.

Broad suites were not repeated by the reviewer without a new concern; current CI and existing evidence covered unchanged behavior. Basic YAML is selected by Surefire's `test` profile. Explicit Maven `integration-tests` selects Failsafe and the Spring `lvl2test` profile. Local launchers select `dev`; production configuration is selected in a direct Java launch, not a hosted rollout.

## Findings

No findings identified. The original empty finding set is preserved; no advisory or blocking disposition is required.

## Comparison and correction history

`roundCount`: 2

| Round | Before / after | Outcome |
| --- | --- | --- |
| 0, initial review | Base `b203ffd17063f65f06e0e4bea3cb49e9e68cdc44` to `67fd957c3686bd8b0385a843a0cfc97482c9692e` | Fresh bugs/security/requirements coverage clear, no findings; independent real UP/DOWN readiness, static assets, unavailable actuator/env and process cleanup checks. Original CI40141270696/40141270670 passed. |
| 0, metadata refresh | `67fd957c3686bd8b0385a843a0cfc97482c9692e` to `386b652f9e3156f4ecf63d764b3c6fc231eb02fc` | Eight records/configuration files inspected; implementation objects identical. No findings, count unchanged. CI40141843989/40141843881 passed. |
| 1, D20 owner revision | Reserved before edits at `b684bf81bab5d9f78abc9908a5f7c30a33aeb60c`; before `386b652f9e3156f4ecf63d764b3c6fc231eb02fc`, after `a043cf1522be32250d9b9533370cd979e53d2d59` | Owner-requested README/profile/testing batch, not a finding from the earlier empty set. Implemented and independently reviewed clear. No open findings; batch complete. |

One correction round remains under the accepted limit; no reset or extra allowance is implied. The previous clear results apply to their named historical comparisons. Persisting the final report and implementation evidence is metadata reconciliation, not another correction batch.

## Assessment

`assessment`: clear

All three required passes are covered for the fixed revised T1 candidate, current candidate CI passed and no blocking findings remain. This is technical readiness, not code approval, release authority or permission to merge.

## Metadata reconciliation and remaining work

The coordinator updates review/progress and records the exact D20 profile semantics in the plan/design after this comparison. These records introduce no new behavior, authority, policy, concurrency or runtime source. The same independent reviewer checks their equivalence and records the current metadata-only head in the linked PR assessment before final presentation. Any material implementation change requires affected tests/review refresh.

D19/D20 cover T1 only. The project owner retains approval and manual merge authority. T2–T20, hosting, paid AI and a concurrency increase remain unauthorized.

## D21 owner-requested simplification — round 2 reservation

Reserved before edits, with before candidate `490fa7ee1eb1cb1e32a8834fa762703a6c0c0fd7`. D21 requests standard frontend/backend test CI, removal of redundant orchestration and no proposed squash-commit body. This is an owner-directed revision, not a finding in the earlier empty finding set. Revised candidate/evidence/review are pending. Current assessment is `incomplete` for the new revision; previous clear statements apply only to their named comparisons. This is the final ordinary correction batch under the existing two-round policy; no new allowance or reset is implied.
