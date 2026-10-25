# T1 runtime shell review

## Comparison

| Field | Value |
| --- | --- |
| `mode` | committed |
| Original base | `origin/main` |
| Original candidate | PR #21, D21 simplification |
| `base` | `{"kind":"gitCommit","identity":"b203ffd17063f65f06e0e4bea3cb49e9e68cdc44"}` |
| `candidate` | `{"kind":"gitCommit","identity":"2227a489bb01da13732cf829902f9692ca6bc6a3"}` |
| Actual merge-base | `b203ffd17063f65f06e0e4bea3cb49e9e68cdc44` |
| Previously reviewed candidate | `490fa7ee1eb1cb1e32a8834fa762703a6c0c0fd7` |
| PR | [#21](https://github.com/igrgin/ai-knowledge-base/pull/21) |

Scope is T1 including D20 profile/README and D21 test-tooling revisions. Later features and hosting are excluded. The coordinator persists this report after the implementation comparison; the [PR assessment](https://github.com/igrgin/ai-knowledge-base/pull/21#issuecomment-6390736664) records the exact final metadata-only descendant after independent freshness checks and its CI success.

## Material inputs

- Original scoped R18/R19, D-A/D-G, invariants, T1 task/pins/ownership at `b203ffd17063f65f06e0e4bea3cb49e9e68cdc44`.
- D19 T1 authority, D20 README/profile/testing direction, D21 CI/command simplification and corresponding current plan sections at `2227a489bb01da13732cf829902f9692ca6bc6a3`.
- Review/development-agent policies unchanged from the base. D21-amended GitHub workflow policy at the candidate supersedes repository-governance CI and script orchestration, while retaining commit conventions, issue/branch association, squash-only merging and owner merge authority.
- Architecture/security/operations sources remain unconfigured as identified in adoption; scoped specification and direct owner constraints provide T1 expectations. No missing required T1 input prevented coverage.

## Reviewer context

One independent fresh-context GPT-6.1 Sol high reviewer covered all three passes and their refreshes report-only. The Sol medium implementer yielded before each review; concurrency remained one. No implementation conversation, separate pass agents, source edits, approvals or merges were used by the reviewer.

## Pass coverage

| Pass | Coverage | Scope and limits |
| --- | --- | --- |
| Bugs | covered | Native workflow, npm/Playwright lifecycle, Maven resources/Failsafe inheritance, actual database/process lifecycle, installer and native startup docs. Unchanged app/profile coverage retained after equivalence checks. |
| Security | covered | Read-only CI/action pins, test-only mocks, runtime/test separation, loopback/port handling, owned resource cleanup and installer integrity. Production configuration remains unchanged from reviewed D20. |
| Requirements compliance | covered | D21 environment/setup then direct frontend/backend tests, removal of redundant orchestration and proposed squash text, preserved D20 profiles/core pins/T1-only scope. F1 is the nonblocking pointer reconciliation below. |

## Verification

- [Tests CI](https://github.com/igrgin/ai-knowledge-base/actions/runs/40156201638) passed at `2227a489bb01da13732cf829902f9692ca6bc6a3`, including `npm test --prefix frontend` and `./mvnw -B -Pintegration-tests verify`.
- Seven Java and three browser tests passed locally and on Linux without skipped critical lifecycle coverage. Basic tests additionally passed with Docker unreachable.
- Frontend tests use explicit test-only response mocks with Playwright-owned production preview. Native backend tests exercise the real packaged app, pinned pgvector, vector SQL, same-origin HTML/JS, outage/recovery, persistent sentinel/container restart and application restart. Extractor Failsafe launches the real JAR with Java 26/heap/version/unsupported-argument checks. There is no combined live-database browser harness; this coverage boundary is documented honestly.
- Reviewer independently inspected the native backend JAR: actual frontend assets and base/dev/prod YAML present, test profiles and Testcontainers absent. A direct packaged dev launch on a free loopback port returned exactly `{"status":"UP"}`, served HTML and both referenced assets; the packaged extractor probe reported Java 26 and extraction unavailable. Only reviewer-owned processes were terminated. Development services/data were untouched; whitespace checks passed.
- Broad suites were not repeated by the reviewer without a new concern. Local implementation evidence is in ignored `.local/round2-frontend.log`, `round2-backend-final.log`, `round2-basic.log`, `round2-native-startup.log`, `round2-native-package.log`, `round2-ci-success.log`.

## Findings

### F1 — obsolete local-development heading pointer

- Pass: requirements compliance; severity: low; classification: violation.
- Location in implementation candidate: `lighthouse.json:50`, `commands.localDevelopment.section`.
- Evidence: the pointer named `Start and stop`, while the document now names the section `Development and package`.
- Expectation: D21 includes updating affected command references at `2227a489bb01da13732cf829902f9692ca6bc6a3`.
- Blocks readiness: false; runtime/tests are unaffected and the mismatch was already identified for coordinator reconciliation.
- Smallest correction: point to `Development and package`.
- Disposition: addressed in the final metadata reconciliation, with the same reviewer verifying the corrected pointer/current descendant before presentation.
- Reason: the value now matches the real heading. This is the already-planned D21 record reconciliation, not a new implementation batch or a reset of the count.

The earlier rounds had empty finding sets. Preserve F1 and its disposition; do not rewrite it as no finding ever having existed.

## Comparison and correction history

`roundCount`: 2

| Round | Comparison/reservation | Outcome |
| --- | --- | --- |
| 0 | Base to `67fd957c3686bd8b0385a843a0cfc97482c9692e` | Initial three-pass fresh review clear, no findings; actual readiness/assets/process checks and CI40141270696/40141270670. |
| 0 refresh | To `386b652f9e3156f4ecf63d764b3c6fc231eb02fc` | Metadata-only equivalence, no findings; CI40141843989/40141843881. |
| 1, D20 | Reserved at `b684bf81bab5d9f78abc9908a5f7c30a33aeb60c`; implemented `a043cf1522be32250d9b9533370cd979e53d2d59` | Owner-requested README/profile/test split; all passes clear, no findings; prod failure/binding and JAR isolation checked; CI40143650017/40143649974. |
| 1 refresh | To `490fa7ee1eb1cb1e32a8834fa762703a6c0c0fd7` | Four records-only files checked, no findings; CI40144171270/40144171287. |
| 2, D21 | Reserved at `2fb2420a639963724286efb7b8956d3fea471ea0`; implemented `2227a489bb01da13732cf829902f9692ca6bc6a3` | Native tests/CI simplification reviewed clear; F1 low/nonblocking reconciled by coordinator. Replacement Tests CI40156201638 passed. |

Round 2's implementation assessment is complete. The finite count is not reset; further substantive corrections require the owner decision specified by policy. Final report/progress/pointer reconciliation and its equivalence refresh do not create a new implementation batch.

## Assessment

`assessment`: clear

All required passes are covered and implementation CI passed, with no blocking findings. F1's planned metadata correction is recorded above; final current-head equivalence and test CI are recorded in the linked PR assessment before presentation. This grants no code approval, release or merge authority.

## Final metadata reconciliation and remaining work

The coordinator updates plan/current command references, setup documentation and this report, preserving historical evidence at its named candidates. The current test policy and native required check are `Tests`; other protections are unchanged. The same reviewer confirms this small record/configuration delta and F1's actual correction; current-head CI must pass before ready presentation.

D19–D21 authorize T1 and these revisions only. Owner approval/manual merge remains pending. T2–T20, hosting, paid AI and concurrency increases remain unauthorized.
