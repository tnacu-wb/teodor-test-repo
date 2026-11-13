# CTECH-CORE-0013: QA Automation testing

- [Document Control](#document-control)
- [Context](#context)
- [Problem statement](#problem-statement)
- [Requirements/Decision Drivers](#requirementsdecision-drivers)
  - [Functional Requirements](#functional-requirements)
  - [Quality & Performance Requirements](#quality--performance-requirements)
  - [Operational Requirements](#operational-requirements)
- [AS-IS](#as-is)
- [TO-BE](#to-be)
- [Proposed approach](#proposed-approach)
  - [Option 1 - Optimise the standalone WebdriverIO suite in place](#option-1---optimise-the-standalone-webdriverio-suite-in-place)
  - [Option 2 - Migrate FE automation into the monorepo on a modern Playwright + TypeScript framework](#option-2---migrate-fe-automation-into-the-monorepo-on-a-modern-playwright--typescript-framework)
  - [Decision making - Option 1 vs Option 2](#decision-making---option-1-vs-option-2)
- [Framework Considerations](#framework-considerations)
  - [WebdriverIO](#webdriverio)
  - [Cypress](#cypress)
  - [Playwright](#playwright)
  - [Table comparison](#table-comparison)
  - [Overall Decision](#overall-decision)
- [Test documentation & traceability](#test-documentation--traceability)
- [Project structure & PoC](#project-structure--poc)
- [Next Steps](#next-steps)

## Document Control

| | |
|---|---|
| **Author** | TBD |
| **Contributors** | TBD |
| **Stakeholders** | TBD |
| **Status** | DRAFT |

## Context

Whitbread Digital is aiming to reduce the time between code completion and production release, with a long-term ambition of enabling **daily deployments to production**. Reaching that goal requires a significantly higher level of confidence in the quality of every change promoted through the delivery pipeline — and that confidence has to extend to the customer-facing frontend, not only to the services behind it.

Today, the three frontend applications — **PremierInn.com (PI)**, **Premier Inn Business (PIB)**, and **CCUI** — are validated by a standalone UI automation suite (`ui-tests-automation`) that lives in **its own repository**, outside the digital monorepo that now hosts the backend services and the frontend applications themselves. That suite is built on **WebdriverIO v8 + Mocha + Selenium-standalone**, with Babel/ES6 plumbing, multiple external runners (BrowserStack, LambdaTest, Docker-Selenium), and a reporting stack stitched together from Allure, JUnit and JSON reporters.

This separation, combined with the age of the framework, has turned frontend automation into a drag on delivery rather than an enabler:

- The framework is **old and clunky** — heavy configuration, a Selenium dependency, and a Mocha BDD style that no longer reflects how the rest of the estate is built.
- **Reporting is weak and fragmented** — there is no single, first-class view of pass/fail, failure root cause, and requirement coverage.
- Tests are **maintenance-heavy** — selector drift, brittle waits, and a large body of page objects and specs that are expensive to keep green.
- **Test cases and traceability are poorly documented** — TCs live partly in Zephyr and partly in spec descriptions, with no reliable, enforced link between a requirement, its test case, and the automated spec that proves it.

Meanwhile, the backend services and the frontend applications already live **together in the monorepo** with shared tooling, change-detection CI, and a squad-based organisation (`discover-search-squad`, `book-pay-squad`, `company-profile-management-squad`, etc.). A modern **Playwright + TypeScript** suite has already been scaffolded under `qa/`, and a companion ADR — [`ADR-001: QA Test Folder Structure`](./001-qa-test-folder-structure.md) — defines a squad/feature-aligned test layout intended to keep tests traceable to the features they cover.

The objective of this ADR is to decide **whether — and how — frontend test automation should live inside the monorepo** alongside backend, frontend and the other areas, and **what framework and conventions should replace the legacy WebdriverIO suite**, so that QA automation becomes a reliable release-confidence mechanism rather than a maintenance burden.

## Problem statement

We currently lack a modern, well-integrated, and trustworthy frontend automation capability that can act as a **release-confidence gate** for the three FE applications (PI, PIB, CCUI).

The legacy `ui-tests-automation` suite creates several structural problems:

1. **The framework is old and clunky.** WebdriverIO v8 + Mocha + Selenium-standalone, wired together with Babel and a large set of WebdriverIO services, is heavy to run and slow to evolve. The Selenium dependency adds moving parts (driver/server lifecycle) that modern frameworks no longer require.
2. **Reporting is weak.** Pass/fail signal is spread across Allure, JUnit and JSON outputs with no consistent, first-class report that engineers actually trust or read. Diagnosing a failure usually means re-running locally rather than reading the report.
3. **Tests are maintenance-heavy.** Brittle selectors, manual waits, and duplicated page-object logic mean the suite needs constant attention. A high cost of keeping tests green discourages teams from extending coverage.
4. **Test cases and traceability are poorly documented.** There is no enforced, discoverable link from a requirement → a documented test case → the automated spec that verifies it. Coverage is hard to reason about, and regressions are hard to attribute.

On top of these, the suite lives **outside the monorepo**, so a frontend change in one repository and the test that validates it evolve in a separate repository. This requires cross-repo coordination, makes "what was tested vs what was shipped" harder to guarantee, and prevents QA from benefiting from the monorepo's shared tooling and change-detection CI.

The problem is therefore **not the absence of tests** — a large WebdriverIO suite already exists — but the absence of a **modern, maintainable, well-reported, and traceable automation strategy that lives where the code lives**.

## Requirements/Decision Drivers

### Functional Requirements

1. **Confidence in release quality**
   The main business reason for this initiative is to raise confidence in each frontend release. The strategy must validate meaningful business journeys (search → hotel details → booking → payment → confirmation) across PI, PIB and CCUI, and reduce the probability that broken functionality reaches higher environments or production.

2. **Lives in the monorepo, shared across FE projects**
   Frontend automation must live inside the digital monorepo alongside `backend/` and `frontend/`, under `qa/`. The three frontend applications (PI, PIB, CCUI) should use a **common automation approach** — shared page objects, fixtures, utilities and conventions — rather than three divergent setups.

3. **Documented test cases and traceability**
   Every automated test must be traceable: from the feature/spec it covers, to a documented test case, to the automated spec file. Test-case identifiers should be visible in the test itself, and the folder layout should map predictably to features.

4. **Local execution compatibility**
   Engineers must be able to run the full suite, or a targeted subset, locally with a single command (e.g. `npm test`) and a consistent, reproducible environment matching CI.

### Quality & Performance Requirements

5. **Speed of feedback**
   A suite that is too slow will not be adopted and will become a bottleneck rather than an enabler. The target is for the relevant suite to execute in **under 30 minutes**, with most feedback arriving earlier where possible (fail fast on first failure for PR runs).

6. **Strong, first-class reporting**
   Reporting must be a built-in, first-class capability: a single HTML report with steps, screenshots, video and traces for failures, usable directly by engineers and attachable to CI runs — not a stack of bolted-on reporters.

7. **Reliability & determinism**
   Tests must be stable and deterministic. The framework should provide auto-waiting and robust selector strategies to reduce the flakiness that plagues the legacy suite, and support repeatable executions.

### Operational Requirements

8. **PR-level enforcement and CI integration**
   The suite must integrate into the existing CI model (path-based change detection + parallel matrix) and run as part of the pull-request workflow, becoming an enforceable quality gate before merge to `develop`.

9. **Developer/QA experience and maintainability**
   The framework must be usable by frontend engineers and QA engineers across multiple projects. Adding a test or updating test data must not require deep infrastructure knowledge. Good local debugging (trace viewer, UI mode, codegen) is a strong driver of adoption.

## AS-IS

```
whitbread-eos/ui-tests-automation              ← separate repository (outside the monorepo)
├── WebdriverIO v8 + Mocha (BDD) + Selenium-standalone
├── Babel / ES6 runtime plumbing
├── Runners: local / BrowserStack / LambdaTest / Docker-Selenium
├── Reporters: Allure + JUnit + JSON (stitched together)
├── Page Object pattern (*.page.js) + components
├── Test cases authored/managed in Zephyr
└── AI helpers (Copilot PR review, Zephyr→test generation, page-from-HTML, failed-test analysis)
```

- Frontend automation is **decoupled** from the application code it validates.
- Reporting and traceability are **fragmented** across tools.
- Maintenance cost is **high**, which suppresses coverage growth.

## TO-BE

```
digital-monorepo/
├── backend/                         ← Maven, squad-based services
├── frontend/                        ← pi-front-end-applications (PI, PIB, CCUI)
└── qa/                              ← Playwright + TypeScript automation (this ADR)
    ├── playwright.config.ts
    ├── src/                         ← shared page objects, fixtures, constants, utils
    └── tests/                       ← squad/feature-aligned specs (see ADR-001)
```

- Frontend automation lives **next to the code it validates**, in the monorepo.
- A **single, modern framework** (Playwright + TypeScript) replaces WebdriverIO + Mocha + Selenium.
- Reporting and traceability are **first-class and consistent**.

## Proposed approach

The central question is twofold: **where should frontend automation live**, and **on what framework**. Two main approaches were identified for the "where" decision. They are materially different because they optimise for different goals — the first optimises for minimal disruption, the second for long-term maintainability, traceability and alignment with the rest of the estate.

### Option 1 - Optimise the standalone WebdriverIO suite in place

Under this approach, we keep the existing `ui-tests-automation` repository and the WebdriverIO + Mocha + Selenium stack, and invest in cleaning it up: standardising reporters, tidying page objects, reducing flakiness, and tightening the Zephyr linkage.

#### Why this option is attractive

The primary reason this option is attractive is that it is the **least disruptive**. The suite already exists, engineers know it, and a large body of tests is already written. It avoids a migration and lets the team make incremental improvements without standing up anything new.

#### Advantages

- **No migration cost** — the repository, tests and CI hooks already exist; improvements are incremental.
- **Familiarity** — the QA team already knows WebdriverIO, Mocha and the existing page-object patterns.
- **Existing AI tooling** — the Copilot skills (Zephyr→test generation, page-from-HTML, failed-test analysis) are already wired into this repository.

#### Disadvantages

- **Does not fix the root causes.** The framework remains old and clunky; the Selenium dependency, Babel plumbing and Mocha style stay. We would be polishing the very thing identified as the problem.
- **Reporting stays fragmented.** Allure + JUnit + JSON remain bolted on; a genuinely first-class report would still have to be built.
- **Stays outside the monorepo.** Cross-repo coordination between FE code and FE tests continues, and QA cannot benefit from monorepo change-detection CI and shared tooling.
- **Maintenance burden persists.** Brittle selectors and manual waits are inherent to the current approach; cleanup buys time but not a step change.

#### Overall recap

This option is best understood as **deferring the decision** rather than making one. It reduces short-term effort but leaves every structural problem in place — old framework, weak reporting, high maintenance, weak traceability, and separation from the codebase. It does not move us toward the strategic goal of confident, frequent releases.

### Option 2 - Migrate FE automation into the monorepo on a modern Playwright + TypeScript framework

Under this approach, frontend automation becomes a **first-class `qa/` workspace inside the monorepo**, built on **Playwright + TypeScript**, shared across PI, PIB and CCUI, and organised by squad/feature in line with [ADR-001](./001-qa-test-folder-structure.md). The legacy WebdriverIO suite is retired (kept temporarily under `qa/reference/` for migration reference only).

This is the direction already started: a Playwright suite, page objects (`src/pages/pi/*`), fixtures and constants, and TC-ID-named specs (e.g. `tc-468889-guest-piba-poa.spec.ts`) already exist under `qa/`.

#### Why this option is attractive

The primary reason this option is attractive is that it **addresses the root causes directly** rather than working around them. Playwright is a modern, batteries-included framework (auto-waiting, built-in HTML report, trace viewer, parallel execution, codegen) that removes the Selenium/Babel plumbing and the bolted-on reporting. Living in the monorepo means tests sit next to the code they validate, benefit from the existing change-detection CI, and can be enforced as a PR quality gate.

It also matters because adoption is not just a tooling problem — it is an ownership problem. With tests in the same repo as the application, a frontend change and the test that proves it move together in a single PR, which is exactly what keeps a suite healthy over time.

#### Advantages

- **Modern, low-maintenance framework** — auto-waiting and resilient locators sharply reduce the flakiness and manual-wait maintenance that dominate the legacy suite.
- **First-class reporting** — Playwright's HTML report (steps, screenshots, video, and the trace viewer) is built in, giving a single, trustworthy failure-analysis view without stitching reporters together.
- **Lives where the code lives** — FE code and its tests evolve in the same repo and PR; "what was tested" matches "what was shipped". QA inherits monorepo tooling and CI.
- **Shared approach across FE apps** — common page objects, fixtures and constants serve PI, PIB and CCUI, with path aliases (`@pages`, `@fixtures`, `@constants`, …) already in place.
- **Traceability by design** — the squad/feature folder layout from ADR-001 plus TC-ID-named specs give a discoverable requirement → test-case → spec chain.
- **Strong local DX** — UI mode, trace viewer and codegen make tests easier to write, debug and maintain, which drives adoption.
- **Fast, parallel execution** — Playwright runs fully parallel by default, supporting the sub-30-minute feedback target.

#### Disadvantages

- **Migration effort.** Existing WebdriverIO tests must be re-implemented (or re-prioritised) in Playwright; not every legacy test will carry over one-to-one.
- **Re-skilling.** Engineers comfortable with WebdriverIO/Mocha need to ramp up on Playwright and TypeScript patterns.
- **AI tooling re-wire.** The Copilot skills built around WebdriverIO/Zephyr need to be re-pointed at the Playwright structure.
- **WAF friction.** UAT sits behind an Akamai WAF that blocks headless browsers; the suite currently runs headed/with detection-avoidance flags, which needs a durable solution for CI.

#### Overall recap

This is the stronger solution and the one aligned with the strategic direction. It costs an up-front migration, but it is the only option that actually resolves the four stated problems — old framework, weak reporting, high maintenance, and weak traceability — while bringing FE automation into the monorepo next to backend and frontend.

### Decision making - Option 1 vs Option 2

**Option 2 is the recommended direction.** Option 1 lowers short-term effort but preserves every problem this ADR set out to solve. Option 2 carries a migration cost but is the only approach that delivers a modern framework, first-class reporting, lower maintenance, documented traceability, and co-location with the application code — the prerequisites for confident, frequent frontend releases.

## Framework Considerations

Once we accept that automation belongs in the monorepo (Option 2), we still need to confirm the framework. Three candidates were considered: the incumbent **WebdriverIO**, **Cypress**, and **Playwright**.

### WebdriverIO

The incumbent. Highly flexible, with a large service ecosystem (BrowserStack, LambdaTest, Appium for mobile) and WebDriver/DevTools protocols. In our setup, however, it is coupled to Selenium-standalone, Babel and a Mocha BDD style, with reporting assembled from separate Allure/JUnit/JSON reporters. It is powerful but configuration-heavy and, as used today, is the source of the maintenance and reporting pain.

### Cypress

A developer-friendly framework with an excellent time-travel debugger and mature component testing. It is well suited to many UI testing use cases. Historically it has had constraints around multi-tab/multi-origin flows and runs effectively one browser per execution, which is relevant given our cross-app (PI/PIB/CCUI) and payment-iframe/3DS journeys.

### Playwright

A modern browser-automation framework with auto-waiting, first-class TypeScript support, parallel execution via projects, cross-browser coverage (Chromium, Firefox, WebKit), a built-in HTML report, and a powerful trace viewer. It is well suited to complex user flows and large-scale browser automation, and is already the basis of the scaffolded `qa/` suite.

### Table comparison

| Area | WebdriverIO (incumbent) | Cypress | Playwright |
|---|---|---|---|
| **Architecture** | WebDriver/DevTools; Selenium-standalone in our setup | In-browser execution engine | Browser-automation via CDP/WebKit protocols, out-of-process |
| **Language / typing** | JS + Babel/ES6 (Mocha BDD today) | JS/TS | TypeScript-first |
| **Browser support** | Broad via drivers/services | Chromium-family, Firefox, Electron, WebKit (experimental) | Chromium, Firefox, WebKit |
| **Auto-waiting / flakiness** | Manual waits common; flakiness-prone as used | Good built-in retry-ability | Strong auto-waiting and web-first assertions |
| **Parallelism** | Supported, config-heavy | Per-machine/orchestration | Fully parallel by default (workers/projects) |
| **Reporting (out of the box)** | Allure/JUnit/JSON bolted on | Good runner UI; reporters available | Built-in HTML report + trace viewer |
| **Debugging / local DX** | Logs + Allure | Excellent time-travel debugger | UI mode, trace viewer, codegen |
| **Mobile / cross-device** | Appium support | Limited | Device emulation; no native mobile |
| **Monorepo / tooling fit** | Separate repo today | Good | Excellent; TS + npm fit the `qa/` workspace |
| **Maintenance burden (our context)** | High | Medium | Low–Medium |

### Overall Decision

Given the need for a **common framework across PI, PIB and CCUI**, strong CI behaviour, first-class reporting and long-term maintainability, **Playwright + TypeScript is the recommended strategic choice**, hosted inside the monorepo under `qa/`. It is already the basis of the scaffolded suite, and it directly targets the framework, reporting, maintenance and traceability problems that motivated this ADR.

## Test documentation & traceability

To resolve the "poorly documented TCs and traceability" problem, the strategy combines three mechanisms:

- **Folder-level traceability** — tests are organised by `squad → feature → {FunctionalTests, E2E}`, with feature folder names matching `.kiro/specs/<squad>/<feature>/`, per [ADR-001](./001-qa-test-folder-structure.md). Finding the tests for a feature is then trivial.
- **Test-case identifiers in specs** — specs carry their TC ID in the file name and test title (e.g. `tc-468889-guest-piba-poa.spec.ts` / `TC-468889: …`), giving a direct link from a documented test case to its automated spec.
- **First-class reporting** — the Playwright HTML report (with traces, screenshots and video on failure) provides the run-level evidence and root-cause view that the legacy reporting stack lacked.

The link to a test-management tool (e.g. Zephyr) can be preserved by keeping the TC ID as the stable key between the documented case and the automated spec. Whether to automate that synchronisation is called out as an open question below.

## Project structure & PoC

The PoC is already underway in the monorepo and follows the structure below (aligned with ADR-001):

```
qa/
├── playwright.config.ts            ← envs (UAT/DIT), chromium project, html+list reporters, trace on retry
├── package.json                    ← Playwright + TypeScript, scripts: test / test:headed / test:ui / report
├── tsconfig.json                   ← path aliases: @pages, @components, @fixtures, @api, @constants, @utils
├── src/
│   ├── pages/pi/                   ← page objects (home, hotelDetails, ancillaries, guestDetails,
│   │                                  payment, paymentDetails, threeDSecure, bookingConfirmation)
│   ├── fixtures/                   ← base fixture(s)
│   └── constants/                  ← hotels, cards, guestData
├── tests/
│   ├── general_regression/         ← migrated legacy regression tests
│   └── <squad>/<feature>/          ← FunctionalTests/ and E2E/ (see ADR-001)
└── reference/                      ← legacy WebdriverIO suite, kept for migration reference only (git-ignored)
```

Existing PoC evidence:

- A working Playwright + TypeScript suite with page objects and fixtures.
- An end-to-end booking journey spec (`tc-468889-guest-piba-poa.spec.ts`) exercising search → hotel details → ancillaries → guest details → payment (PIBA Pay on Arrival) → 3D Secure → confirmation.
- Environment switching (UAT/DIT) and CI-aware retries/workers in `playwright.config.ts`.

## Next Steps

1. **Confirm the decision** — ratify Option 2 (monorepo) + Playwright with the QA, FE and platform stakeholders, and record the agreed Author/Contributors/Stakeholders above.
2. **Define the migration plan** — prioritise which legacy WebdriverIO journeys to re-implement first (highest-value business flows), and agree what is retired vs. migrated.
3. **Stand up CI integration** — add a QA job to the existing change-detection + matrix pipeline, run the relevant suite on PRs to `develop`, and enforce it as a quality gate. Resolve the Akamai-WAF/headless constraint for CI.
4. **Lock in traceability conventions** — standardise TC-ID naming in spec files/titles and decide whether to automate Zephyr synchronisation.
5. **Migrate AI tooling** — re-point the Copilot skills (test generation, page-from-HTML, failed-test analysis) at the Playwright structure.
6. **Document conventions in steering** — capture the agreed patterns in `.kiro/steering` (qa conventions) so Kiro and engineers apply them consistently.

### Open questions (to resolve before accepting)

1. Do we automate synchronisation between Zephyr test cases and Playwright specs, or keep the TC ID as a manual-but-stable link?
2. What is the durable solution for running against the Akamai-WAF-protected UAT environment in headless CI?
3. Which browsers/projects do we commit to (Chromium-only initially, or add WebKit/Firefox for cross-browser coverage)?
4. How are shared page objects and fixtures scoped — globally under `qa/src`, or per squad — as the suite grows across PI, PIB and CCUI?
