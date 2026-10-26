# Maven multi-module investigation

## Scope and recommendation

Research date: 2026-10-26, Europe/Zagreb. Repository baseline: `776396722e499ff9108b87d1eba3ff091011cf62`. This note evaluates build structure and native test selection. The owner clarified that the immediate aim is to group tests, run either test level separately or together through Maven plugins, select one or both Java modules, and keep versions together. The accepted [specification](../scopes/initial-application/spec.md) and [plan](../scopes/initial-application/plan.md) own the resulting bounded amendment; this note supplies its investigation and verification evidence.

Keep the existing Maven reactor with `bom`, `backend` and `pdf-extractor`. It already provides useful separation between dependency management and two executable artifacts. Do not add a Maven module for every future business feature yet; begin with feature packages inside `backend`, then extract build modules when a concrete dependency boundary or reuse requirement justifies them. This is a project-specific recommendation, not a Maven requirement.

## What exists today

```text
pom.xml                    root aggregator and Java-module parent
├── bom/pom.xml             dependency-management POM
├── backend/pom.xml         Spring Boot application artifact
└── pdf-extractor/pom.xml   standalone executable JAR
```

The [root POM](../../pom.xml) aggregates all three submodules and inherits Spring Boot's starter parent 4.1.2. Backend and extractor inherit the root's build configuration and explicitly import the shared [BOM](../../bom/pom.xml), which imports Spring Boot's dependency BOM 4.1.2. The BOM has no project parent, avoiding an import/parent cycle; it introduces no runtime library by itself. The [development guide](../../docs/local-development.md#compatibility) describes the current arrangement.

The backend currently contains the application shell. The [extractor entry point](../../pdf-extractor/src/main/java/com/aiknowledgebase/extractor/ExtractorMain.java) implements only `--version`; real extraction remains planned in T6. Its separate executable packaging is an existing reason for a build-module boundary, without claiming that PDF processing is implemented.

## What Maven modules provide

Aggregation selects projects to build together; inheritance shares configuration. They are distinct mechanisms, although the root uses both. A multi-module build does not require separate Git repositories. Maven's reactor collects projects and orders their builds using declared dependencies and other build relationships. [Maven POM introduction](https://maven.apache.org/guides/introduction/introduction-to-the-pom.html#project-inheritance-vs-project-aggregation), [reactor guide](https://maven.apache.org/guides/mini/guide-multiple-modules.html#the-reactor)

A separate Java module produces its own artifact and declares its dependencies. This makes dependency direction explicit and restricts its compilation classpath through the dependency graph, including transitive dependencies. It does not automatically hide another module's public implementation classes; API discipline still matters. A BOM centralizes managed dependency versions, while parent configuration can also share build/plugin settings. Dependency management does not itself add a dependency. [Maven dependency mechanism](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#dependency-management)

Maven's `-T` option schedules eligible modules concurrently during the build. That concerns compilation, tests and packaging; it does not add runtime extractor processes or increase upload throughput. Parallel builds also require compatible plugins and isolated test resources. No parallel-build speedup has been measured here. [Apache Maven parallel-build guidance](https://cwiki.apache.org/confluence/spaces/MAVEN/pages/18153538/Parallel%2Bbuilds%2Bin%2BMaven%2B3)

## Native test grouping

Keep the current modules: module selection and test-level selection are independent. The root parent shares Surefire/Failsafe 3.5.6 and plugin configuration; both Java modules import the shared dependency BOM. Additional test modules are unnecessary for the owner's requested grouping.

| Module selection | Maven option from the repository root |
| --- | --- |
| Backend | `-pl backend` |
| Extractor | `-pl pdf-extractor` |
| Both Java modules | `-pl backend,pdf-extractor` |
| Whole reactor, including parent/BOM | Omit `-pl` |

| Test level | Native command (combine with the desired module selection) |
| --- | --- |
| Basic, Surefire | `./mvnw test` |
| Basic and integration, Surefire/Failsafe | `./mvnw -Pintegration-tests verify` |
| Integration only, Failsafe | `./mvnw -Pintegration-tests -DskipUnitTests=true verify` |

`skipUnitTests` is mapped only to Surefire. Its default follows `skipTests`, preserving the pinned plugins' global `-DskipTests=true` behavior when no explicit `skipUnitTests` override is given. `-DskipITs=true` skips Failsafe execution. Integration-only `verify` still compiles and packages before launching packaged-JAR tests. Maven's `integration-tests` build profile is distinct from the Spring `test` and `lvl2test` configuration profiles.

The backend currently has zero Java tests; selecting it does not invent coverage. Extractor has one Surefire test (`ExtractorLaunchTest`) and one Failsafe test (`ExtractorPackageIT`). Browser tests remain in the native npm suite. The [development guide](../../docs/local-development.md#native-tests) owns ongoing command instructions.

## Whether to add feature modules

| Choice | Project-specific benefit | Cost or limitation |
| --- | --- | --- |
| Feature packages within `backend` | Simple refactoring while identity, projects and chats are still unimplemented | Dependency boundaries need deliberate enforcement |
| Separate feature Maven modules | Explicit artifact dependencies; useful when a feature has a stable API, distinct dependencies or another consumer | Additional POMs, assembly wiring and dependency-graph maintenance; poorly chosen splits can create cycles |
| Existing extractor module | Own executable entry point and packaging; parser dependencies can remain separate from the web artifact | Parent/child protocol and artifact distribution still need implementation |

Revisit extra modules after feature interfaces settle, when an unwanted dependency repeatedly crosses a boundary, a library needs independent reuse, or measured build/test costs warrant isolation. More POMs alone are not evidence of better architecture. Spring Modulith versus microservices and runtime extraction scaling are separate investigations.

## Evidence limits

Native test-selection verification accompanies the bounded POM change; command logs and exact invocations are retained under ignored `.local/test-selection-evidence`. Each matrix command uses `clean`, and results are read from that invocation's log rather than stale reports. No runtime scaling or parallel-build performance benchmark was run. No backend Java tests exist, so backend selection verifies Maven configuration/build selection without claiming backend behavior coverage.


Verified matrix (JDK 26, Maven Wrapper 3.9.16; every command exited 0):

| Selection | Test level/flag | Actual Java executions |
| --- | --- | --- |
| Backend | Basic | 0; module has no tests |
| Extractor | Basic | 1 Surefire |
| Both Java modules | Basic | 1 Surefire |
| Whole reactor | Basic | 1 Surefire |
| Extractor | Both levels | 1 Surefire + 1 Failsafe |
| Whole reactor | Both levels | 1 Surefire + 1 Failsafe |
| Whole reactor | `-DskipUnitTests=true`, integration profile | 0 Surefire + 1 Failsafe |
| Whole reactor | `-DskipTests=true`, integration profile | 0; both plugins skipped |
| Whole reactor | `-DskipITs=true`, integration profile | 1 Surefire; Failsafe skipped |

The effective POM also confirms Surefire/Failsafe 3.5.6 and preserves the backend Surefire Spring `test` property merged with the new skip setting, plus Failsafe's Spring `lvl2test` property. No YAML, dependency/plugin version or CI command changed. No frontend rerun, Docker or paid calls were needed for this POM-only test-selection change.
