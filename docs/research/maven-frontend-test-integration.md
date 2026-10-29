# Frontend tests through Maven

Research date: 2026-10-26, Europe/Zagreb. Baseline: `68042f28a112de23c22e230a48a61e77f4bc2a6b` on `2-add-minimal-persistence-and-contract-conventions`. **Accepted for T2 implementation in PR #23; implementation/review evidence is tracked in that PR.** On 2026-10-26 the owner said in the current conversation: “Adopt this approach and implement it in T2’s existing PR. Use the agreed agents and review process, keep concurrency at 1, and show me the updated PR before merging.” This note records the adopted design and original research; it does not itself prove implementation or verification. PR #23 remains unmerged.

## Recommendation

Use `org.codehaus.mojo:exec-maven-plugin:3.6.4` in **`backend/pom.xml` only** to invoke the existing npm commands from `../frontend`. Install dependencies at `initialize`, build assets at `generate-resources`, and run the existing Playwright suite at `test`. Then `./mvnw test` runs Java basic tests and frontend tests, and `./mvnw -Pintegration-tests verify` runs both plus Java integration tests. Keep Node/browser provisioning as environment setup, as it is today.

This is the smallest fit for this repository: the backend already packages frontend assets; running both their producer and consumer in that module makes the lifecycle ordering explicit. npm scripts remain the frontend behavior authority. There are no new shell orchestration scripts, Java test wrappers, test categories or Maven modules. The tradeoff is that selecting `backend` includes the frontend, and Maven requires the project's Node/npm on PATH. Frontend-only development/tests retain their native npm commands.

The selected plugin version is a released Maven Central version, verified against [Central metadata](https://repo.maven.apache.org/maven2/org/codehaus/mojo/exec-maven-plugin/maven-metadata.xml) and its [published POM](https://repo.maven.apache.org/maven2/org/codehaus/mojo/exec-maven-plugin/3.6.4/exec-maven-plugin-3.6.4.pom). Official documentation identifies the same version and minimum Maven 3.6.3/JDK 8 requirements; the repository's Maven 3.9.16/Java 26 exceed those minimums. This establishes declared prerequisites, not tested Java 26 compatibility. [Plugin requirements](https://www.mojohaus.org/exec-maven-plugin/plugin-info.html)

## Repository evidence

- [Root POM](../../pom.xml) currently aggregates `bom`, `backend` and `pdf-extractor`. Surefire uses `skipUnitTests`, defaulting to `skipTests`; Failsafe runs `*ITests` only under Maven profile `integration-tests`, with Spring profile `itest`.
- [Backend POM](../../backend/pom.xml) copies `../frontend/dist` into `static` during resource processing. Today Maven does not generate it, so packaging depends on a prior separate frontend build.
- [Frontend package](../../frontend/package.json) pins Node 24.22.1/npm 11.19.1. `build` owns TypeScript/Vite compilation, `pretest` runs `build`, and `test` runs `playwright test`. The committed lockfile owns dependency resolution.
- [Playwright configuration](../../frontend/playwright.config.ts) owns Vite preview startup/shutdown on loopback 15175, with `reuseExistingServer: false` and one worker. [Three tests](../../frontend/e2e/shell.spec.ts) mock readiness success/retry, HTTP 503 recovery, and connection failure. They need Chromium, not a running Java backend or PostgreSQL.
- [CI](../../.github/workflows/tests.yml) has one `Tests` job: setup Java/Node, `npm ci`, Playwright browser/system installation, `npm test`, then Maven verification. [Local development](../local-development.md) documents native tools and a project-local optional Node installer. The [plan](../roadmap.md) preserves native commands and three current Maven modules; this proposal does not amend that authority.

## Lifecycle and illustrative configuration

Maven executes phases in sequence through the requested phase. `generate-resources` precedes `process-resources`, so placing the asset build there guarantees fresh output before backend resource copying, including `package -DskipTests=true`. Binding only to `test` would be too late for resource copying. [Maven lifecycle](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html)

| Backend phase | Command, with working directory `frontend` | Purpose |
| --- | --- | --- |
| `initialize` | `npm ci --engine-strict --include=dev` | Install the locked toolchain dependencies and reject incompatible engines |
| `generate-resources` | `npm run build` | Generate assets before backend resource copying |
| `test` | `npm --ignore-scripts test` | Run the package's test script once, without its redundant pretest build |

`npm ci` fails on package/lockfile disagreement, removes existing `node_modules`, and leaves manifests/locks unchanged. Explicit `--include=dev` retains build/test tooling even in an environment that omits development dependencies. Engine checking should remain strict without `--force`; npm's pinned source checks both `engines.node` and `engines.npm`, including the root package, and makes an incompatibility fatal with engineStrict. [npm ci](https://docs.npmjs.com/cli/v11/commands/npm-ci/), [npm engine config](https://docs.npmjs.com/cli/v11/using-npm/config/#engine-strict), [npm 11.19.1 engine checking](https://github.com/npm/cli/blob/v11.19.1/node_modules/npm-install-checks/lib/index.js), [Arborist enforcement](https://github.com/npm/cli/blob/v11.19.1/workspaces/arborist/lib/arborist/build-ideal-tree.js)

For an explicit `npm test` invocation, `--ignore-scripts` still runs the requested test script but suppresses pre/post hooks. Scope the option to this Maven test execution; do not set it globally or apply it to dependency installation. Native `npm test --prefix frontend` keeps its normal pretest build. Future essential test setup must live in the main test script or Playwright configuration, rather than a suppressed hook. [npm script options](https://docs.npmjs.com/cli/v11/commands/npm-run/#ignore-scripts)

The adopted configuration puts the property default in the root POM, the plugin version in root `pluginManagement`, and the actual executions in the backend. The POMs own the implementation; T2's plan/PR own actual verification evidence. Do not put executions in the shared root plugin list, where they would be inherited by the extractor.

```xml
<!-- root properties; the existing skipTests default remains -->
<skipFrontendTests>${skipTests}</skipFrontendTests>

<!-- root build/pluginManagement/plugins -->
<plugin>
  <groupId>org.codehaus.mojo</groupId>
  <artifactId>exec-maven-plugin</artifactId>
  <version>3.6.4</version>
</plugin>

<!-- backend build/plugins -->
<plugin>
  <groupId>org.codehaus.mojo</groupId>
  <artifactId>exec-maven-plugin</artifactId>
  <configuration>
    <workingDirectory>${project.basedir}/../frontend</workingDirectory>
    <executable>npm</executable>
  </configuration>
  <executions>
    <execution>
      <id>frontend-dependencies</id>
      <phase>initialize</phase>
      <goals><goal>exec</goal></goals>
      <configuration>
        <arguments>
          <argument>ci</argument>
          <argument>--engine-strict</argument>
          <argument>--include=dev</argument>
        </arguments>
      </configuration>
    </execution>
    <execution>
      <id>frontend-build</id>
      <phase>generate-resources</phase>
      <goals><goal>exec</goal></goals>
      <configuration>
        <arguments><argument>run</argument><argument>build</argument></arguments>
      </configuration>
    </execution>
    <execution>
      <id>frontend-tests</id>
      <phase>test</phase>
      <goals><goal>exec</goal></goals>
      <configuration>
        <skip>${skipFrontendTests}</skip>
        <arguments><argument>--ignore-scripts</argument><argument>test</argument></arguments>
      </configuration>
    </execution>
  </executions>
</plugin>
```

`exec:exec` supports `workingDirectory`, executable/argument lists and an execution-local `skip` parameter; synchronous execution is the default. Its source treats nonzero process exit as failure by default. Leave `async`, `successCodes` and failure suppression unset, so install/build/browser failures fail Maven. No Maven process should launch or manage a second Vite server; Playwright already owns that lifecycle. [Exec goal parameters](https://www.mojohaus.org/exec-maven-plugin/exec-mojo.html), [exit-code implementation](https://github.com/mojohaus/exec-maven-plugin/blob/master/src/main/java/org/codehaus/mojo/exec/ExecMojo.java)

## Environment, browser setup and CI

Keep exact project Node/npm on PATH through the existing optional local installer or CI `setup-node`. Maven does not change a global installation. The strict install step detects drift against the package engines; effective-POM and wrong-version validation remain required.

Browser installation is separate environment setup, just as Docker is a prerequisite for Java integration tests. A new machine still needs `npm ci --prefix frontend` followed by `npx --prefix frontend playwright install chromium` before its first browser test. CI retains `playwright install --with-deps chromium`; OS package installation belongs in CI provisioning, not an ordinary Maven lifecycle that could request elevated host changes. Playwright browser revisions follow the Playwright package version, so update/reinstall together. [Playwright browser and system dependency installation](https://playwright.dev/docs/browsers#install-browsers)

For the first implementation, retain CI's existing Node and browser preparation, remove the standalone `Frontend tests` step, and replace the verification step with a clearly named single Maven verification step:

```sh
./mvnw -B -Pintegration-tests verify
```

Keep workflow/job name `Tests`, trigger behavior and permissions, preserving its check context. Its preparation `npm ci` and Maven's `npm ci` would both run; this is a known small inefficiency, not a second build/test run. npm's download cache is still useful, while `node_modules` is deliberately recreated. Prefer this simple initial behavior over another skip/install-state protocol. Optimize dependency preparation only if measurements justify it; do not bypass lockfile validation based solely on directory existence.

## Selection and skip contract

The following is accepted behavior for implementation, with required environment/browser preparation completed:

| Request | Command | Expected behavior |
| --- | --- | --- |
| All basic checks, no Docker | `./mvnw test` | Java basic + three current browser tests |
| All checks | `./mvnw -Pintegration-tests verify` | Java basic + browser + Java integration; requires Docker |
| Backend and frontend | `./mvnw -pl backend test` | Backend basic + browser suite |
| Extractor only | `./mvnw -pl pdf-extractor test` | Extractor basic only; no npm |
| Both Java modules and frontend | `./mvnw -pl backend,pdf-extractor test` | Both Java basic suites + browser suite once |
| Java basic only | `./mvnw -DskipFrontendTests=true test` | Java basic only; frontend dependencies/assets still built |
| Browser suite via Maven, no Java test execution | `./mvnw -pl backend -DskipUnitTests=true test` | Browser suite; backend compilation/resource phases still run |
| Java integration only | `./mvnw -Pintegration-tests -DskipUnitTests=true -DskipFrontendTests=true verify` | Java integration only; compilation/package/assets still built |
| Basic/browser checks, skip Java integration | `./mvnw -Pintegration-tests -DskipITs=true verify` | Basic/browser checks + packages, no Failsafe execution |
| Package without test execution | `./mvnw -DskipTests=true package` | No Java/browser tests; fresh assets still built and packaged |

`skipUnitTests` remains Java-Surefire-specific; `skipITs` remains Failsafe-specific; neither silently suppresses frontend tests. `skipFrontendTests` controls only browser execution and defaults to `skipTests`. As today, explicit narrower overrides win: leave `skipUnitTests` and `skipFrontendTests` unset when using `skipTests` as the global skip. Do not add an all-frontend skip that could package missing/stale assets. `maven.test.skip` is not the proposed browser skip contract.

Backend-only Java test execution consequently still requires Node/build tooling. This is a deliberate consequence of making backend packaging own its frontend assets. Native npm remains the leanest frontend-only path; the reactor cannot select `-pl frontend` without a frontend Maven module. Maven's module selection and `-am` operate on actual reactor relationships, not arbitrary filesystem references. [Reactor selection](https://maven.apache.org/guides/mini/guide-multiple-modules.html#command-line-options)

## Alternatives considered

| Approach | Assessment |
| --- | --- |
| Backend-bound `exec-maven-plugin` | Recommended: reuses existing Node provision and npm behavior; least structural change |
| Backend-bound `frontend-maven-plugin` | Good alternative if Maven must provision Node/npm; adds a second project-local runtime to maintain alongside the existing installer/CI runtime |
| New frontend Maven module | Useful if `-pl frontend` or an independently consumed frontend artifact becomes a requirement; unnecessary for today's packaged backend |

`frontend-maven-plugin:2.0.2` is released in [Central metadata](https://repo.maven.apache.org/maven2/com/github/eirslett/frontend-maven-plugin/maven-metadata.xml). Its tagged README declares Maven 3.6/Java 17 minimums, supports local `install-node-and-npm` with explicit `nodeVersion`/`npmVersion`, and `npm` goals with argument strings. If selected instead, use `v24.22.1` and `11.19.1`, backend working directory `../frontend`, and `installDirectory` under backend `target`; then the same install/build/test phases and per-execution test skip apply. It explicitly does **not** use an already installed Node/npm. Do not claim that it reuses `setup-node` or the optional installer. [Tagged plugin documentation](https://github.com/eirslett/frontend-maven-plugin/blob/frontend-plugins-2.0.2/README.md)

A separate frontend module listed before backend gives sequential ordering but does not establish a dependency edge for parallel scheduling or `-pl backend -am`. A sound module design would produce a real frontend artifact consumed by backend and replace the sibling-directory resource coupling; this is a larger packaging change. Do not introduce a POM dependency merely to force ordering. Maven honors instantiated dependencies and related graph edges, not dependencyManagement entries alone. The recommended backend-local binding avoids this issue. Do not run two Maven invocations against the same frontend directory concurrently: both could replace `node_modules`/`dist` and contend for preview port 15175. [Reactor sorting](https://maven.apache.org/guides/mini/guide-multiple-modules.html#reactor-sorting)

Suite counts in this research refer to its baseline; the adopted [test-scope reduction](java-spring-test-scope-review.md) and current development guide own the later inventory.

## Implementation and validation

Implement within the accepted T2 amendment and governing records:

1. Add root version management/test-skip default and backend-local executions as above. Keep the existing static resource mapping and Java test discovery/profile configuration.
2. Extend backend's Maven Clean Plugin configuration with narrow filesets for generated `../frontend/dist`, Playwright report/results directories, and generated TypeScript build-info files. Explicit extra filesets are the supported way to clean output outside Maven `target`. Preserve source, lockfiles, npm download cache, Playwright browser cache and optional local runtimes; no blanket deletion of `frontend` or user caches. `node_modules` can remain because `npm ci` recreates it on the next build. [Maven Clean extra files](https://maven.apache.org/plugins/maven-clean-plugin/examples/delete_additional_files.html)
3. Retain list console reporting. If machine-readable results are wanted, configure Playwright with list plus JUnit output to `frontend/test-results/junit.xml`, keeping browser reports distinct from Surefire/Failsafe reports. The XML comes directly from Playwright; no JUnit wrapper executes the browser suite. Existing ignore patterns cover these paths. [Playwright JUnit reporter](https://playwright.dev/docs/test-reporters#junit-reporter)
4. Update `.github/workflows/tests.yml` and `docs/local-development.md` with the Maven entry point, environment prerequisites, selection table and single-build behavior. `frontend/package.json` need not change. Keep native npm commands documented.
5. Implement/review using the project's agreed assignments and concurrency; present the PR before merge.

Required validation, **not performed in this research**:

| Check | Evidence required |
| --- | --- |
| Effective POM/normal and selected reactor runs | Executions appear only in backend, correct inherited skip values, frontend runs exactly once |
| `clean test` and clean full integration `verify` | Current 16 Java basic + 3 browser checks; full run additionally discovers all 6 Java integration tests, no duplicate or silently skipped tests |
| Skip combinations in the table | Browser/Java selection agrees with the stated contract, including integration-only and all-tests skip |
| Clean `package -DskipTests=true` | Build runs once; backend JAR contains current `static/index.html` and referenced assets, without tests/reports/node_modules |
| Native `npm test` | Still builds once and runs all three tests; Playwright owns and releases preview listener |
| Failure injection in a disposable checkout | TypeScript/build error, failing browser assertion, missing browser, wrong Node/npm and lockfile mismatch each fail Maven; no ignored exit code |
| Local macOS and CI Linux | PATH resolution, exact runtimes, browser dependencies and cleanup work; Java 26/plugin compatibility demonstrated |
| CI change | One `Tests` context remains green; standalone frontend verification removed and Maven logs show one frontend build and suite |

No dependencies were installed, no tests/builds or application processes were run, and no paid calls were made for this note. Research read repository files, upstream documentation/source and Maven Central metadata. Implementation execution evidence for plugin/runtime integration, cleanup and selection belongs in T2's current plan/PR; this research note alone does not establish readiness. The owner adopted reuse of the existing Node setup. Provisioning a second runtime or adding a frontend Maven module remains an unselected alternative.
