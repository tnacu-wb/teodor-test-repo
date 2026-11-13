# Whitbread Digital Monorepo Copilot Instructions

## Repository Scope

This repository is a multi-stack Whitbread Digital monorepo. Treat each top-level area as an independently owned stack:

- `backend/`: Java and Kotlin Spring services and shared Maven libraries.
- `graphql/`: TypeScript Apollo GraphQL services and subgraphs.
- `qa/`: Playwright Test and TypeScript end-to-end automation.
- `frontend/pi-front-end-applications/`: customer-facing Next.js applications and shared UI packages. Its nested `.github/copilot-instructions.md` is authoritative for frontend-specific conventions.
- `mobile/`: Android and iOS applications.
- `integration-tests-kotlin/`: Kotlin integration journeys and test support.
- `infrastructure/`, `infra/`, `temporal/`, and `integration-env/`: deployment, workflow, and local integration infrastructure.

Before editing, identify the owning stack and read its nearest README, package/build file, configuration, tests, and local instructions. Do not assume versions, scripts, APIs, or environment values. Prefer existing abstractions and patterns in the same module.

## General Engineering Rules

- Make the smallest focused change that fixes the root cause.
- Preserve public APIs and behavior unless the task explicitly changes the contract.
- Do not revert unrelated user changes or reformat unrelated files.
- Use descriptive names, explicit control flow, early returns, and typed contracts.
- Avoid speculative abstractions, retries, fallbacks, sleeps, and compatibility aliases. Preserve an existing compatibility alias when it protects current callers or mirrors a reference API; do not add aliases only to avoid choosing the owning name.
- Keep secrets, tokens, credentials, and personal data out of source, logs, tests, and commits. Read them from environment configuration.
- Add or update focused tests for changed behavior. Keep test data deterministic.
- Do not commit changes unless explicitly requested.
- Use ASCII for new documentation and source unless the existing file clearly requires another character set.

## Validation

Run the narrowest meaningful check after each focused edit, then the relevant broader check before finishing:

- Backend: run the Maven Wrapper from `backend/`, targeting the changed module where possible, for example `./mvnw -pl <module> -am test`.
- GraphQL: inspect `graphql/package.json`; run its existing typecheck, lint, and test scripts from `graphql/`.
- QA: run `cd qa && npx playwright test <path>` for behavior changes and `cd qa && npx tsc --noEmit` for API, type, fixture, or shared utility changes.
- Integration tests: use `integration-tests-kotlin/gradlew` and the narrowest relevant test task.
- Frontend: follow `frontend/pi-front-end-applications/.github/copilot-instructions.md` and its package scripts.

Report environment, credential, service, or infrastructure blockers precisely. A blocked external dependency is not evidence that an unrelated code path is correct.

## QA WebdriverIO to Playwright Migration

For any work involving `qa/reference`, WebdriverIO, Wdio, Playwright tests, page objects, API helpers, test data, fixtures, utilities, locales, or migration regressions, first read:

`.github/skills/qa-webdriverio-to-playwright-migration/SKILL.md`

The skill is the detailed source of truth. The core rule is behavioral fidelity: preserve the closest `qa/reference` journey, data, validations, logs, comments, sequencing, and helper semantics. Adapt only what Playwright, TypeScript, or the current target framework requires.

### Source and Target Mapping

- Reference tests: `qa/reference/test/specs/**/*.spec.js`
- Reference pages/components: `qa/reference/test/pages/**/*.js`
- Target tests: `qa/tests/**/*.ts`
- Target pages/components: `qa/src/pages/**/*.ts` and `qa/src/components/**/*.ts`
- Target APIs: `qa/src/api`
- Target data and constants: `qa/src/test-data`
- Target utilities and fixtures: `qa/src/utils` and `qa/src/fixtures`

Always inspect the closest reference implementation before changing its target. If only a failing target file or error is provided, map it to the reference by test title, selector, method, data class, console step, or test-case ID before editing.

### Migration Rules

- Preserve source data, locale branches, feature toggles, API parameters, validations, setup, cleanup, and payment sequencing. Do not delete a journey step to make a test pass.
- Use `global.browser.options.app` for the application and `global.browser.options.locale` for locale. Do not introduce `browser.options.project`, `options.project`, or a legacy `global.pages` alias.
- Use fixture-provided page objects, especially `global.piPages`, and delegate navigation to page `open()` methods. Preserve source orchestration actions and their prerequisites.
- Configure Playwright HTTP authentication with `page.context().setHTTPCredentials(...)`; never embed usernames or passwords in navigation URLs, logs, traces, reports, or browser history. When secure domains require different passwords, update the context credentials before navigating to each plain URL.
- Preserve backwards compatibility for the existing Playwright target surface while migrating reference behavior. Do not remove or silently rename public page/component/API methods, constructors, exports, fixture properties, or accepted data shapes used by current `qa/tests` callers. When a reference-aligned name differs from an established target name, keep a thin typed deprecated delegate or compatibility alias with the old signature, update callers incrementally, and search/typecheck all usages before removing it. This does not justify retaining obsolete framework globals such as `browser.options.project` or `global.pages`.
- Preserve the reference Manage Booking authentication flow: do not clear HTTP credentials before submitting the modal search when the environment still needs them; submit the search, wait for the search control/modal to disappear, then reload the current URL with credentials removed through the owning page/component action.
- For CCUI environments with multiple secure hosts, authorize `secureUrl2` before `secureUrl`, then open the CCUI base URL. Keep that runtime option and use the authorized CCUI home-page open path during application reset.
- Use `Strings`, `Locales`, and existing constants from `qa/src/test-data`; do not hard-code localized UI text or domain values. Preserve `gb-en` and `de-de` behavior.
- Migrate suite-level English gates as `test.beforeEach` with `test.skip(!Locales.isEnglishWebsite(), reason)` when that matches the reference. Never leave `test.only` in migrated code.
- Keep UI interaction and UI assertions inside owning page objects/components. Tests should call named actions and validations, not click locators or assert directly against page text or DOM state.
- Scope component locators to their owning container when repeated controls, overlays, or spinners can occur. Prefer stable test IDs and structural or ARIA semantics already used by the target suite; avoid localized visible-text locators when a stable selector is available.
- Preserve reference section comments and grouping: UI elements/properties, UI actions/navigation, and UI validations. Put new members in the owning section. Locator properties, composed child components, and locator factory/getter methods that return a `Locator` without interacting with the page belong in `UI elements/properties`, even when declared as methods (for example, `getRegularGuestEmailLabel(guestName): Locator`). Methods that click, fill, select, navigate, mutate state, or retrieve displayed values belong in `UI actions/navigation`.
- Add a concise diagnostic log at the start of every migrated UI action and UI validation method. Preserve meaningful source `Given`/`When`/`Then` logs, Zephyr links, comments, and setup rationale.
- Document migrated and new page, component, API, utility, fixture, and test-local methods with concise JSDoc when it clarifies their contract. Preserve meaningful source JSDoc, including class-level response or JSON examples. Remove constructors that only call `super()` when they add no behavior.
- Do not wrap required UI selectors, actions, or validations in `try/catch` to hide failures or continue; use explicit locators and waits, and add error handling only when the reference implementation or a documented Playwright boundary requires it.
- Keep migrated page and component classes in separate files that mirror the reference structure; do not add unrelated page or component classes to a catch-all file.
- Every `expect(...)` in changed QA files must include a debug message describing what is checked.
- Keep API facades typed end to end. Do not expose `Promise<any>`, `Promise<unknown>`, or generic `Promise<Object>` when a named interface, union, collection type, or `Promise<void>` is appropriate.
- Translate convenience inputs into the exact GraphQL or REST wire payload inside the owning API facade. Preserve constructor nesting, response shapes, custom availability inputs, authentication audience separation, and environment-aware endpoints.
- Do not add a retry, fallback, conditional skip, force-click, extended poll, or arbitrary timeout unless it exists in the reference or is required by a documented target contract. Repair the owning selector, wait, data, API helper, or environment contract instead.
- Treat visible but disabled amendment controls as failed journey preconditions. Do not force-click or remove `disabled`; trace the policy and seeded booking dates.
- For date pickers, use displayed accessible names and month navigation rather than raw ISO values. Match dropdown locators to the live ARIA semantics instead of assuming native `option` elements.
- Keep card representations distinct at their boundaries and preserve the reference payment lifecycle, including required pending, webhook, and completed states.
- Use `.spec.ts` for QA tests. New test names should describe the feature or journey and include the Zephyr test-case ID when one exists. For migrated cases, the ID in the test title must match the ID in the Zephyr URL's `searchText` query, not a different `testcaseId` path value.
- Preserve Given/When/Then diagnostic logs and prefix migrated step logs with `-> `. Every meaningful source step or expectation must have executable coverage; a console log alone is not an implementation.
- Keep imports grouped consistently with the target suite: utilities and APIs, fixture/page objects, then test data and models. Verify every imported symbol, called method, constant, and fixture member exists before finalizing a migration.
- Prefer deterministic, reusable test data and reset application state before each independent journey through the existing fixture or owning utility. Clean up created bookings and other test data through the established API helpers.
- Use environment-aware configuration and existing constants for URLs, credentials, locales, hotels, rates, and guest data. Do not hard-code environment-specific values or copy legacy `browser.options.environment`/`browser.options.project` access into Playwright tests.
- Use Playwright auto-waiting and web-first assertions. Add an explicit wait only when the owning page or component contract requires it; do not replace a missing selector, state transition, or precondition with a fixed pause.

### QA Review Checklist

Before finishing QA work, confirm:

- The nearest `qa/reference` source was read.
- No source validation, journey step, initial state, cleanup, or meaningful diagnostic was silently removed.
- Locale, Strings, feature toggles, API shapes, dates, and selectors match the source or have an explicit framework-based justification.
- UI actions and validations remain reusable and encapsulated.
- No unjustified retry, skip, fallback, force-click, or timeout was added.
- All changed assertions have debug messages and all changed methods have appropriate documentation.
- A focused Playwright test or TypeScript check was run, or the exact blocker was reported.
- Test titles, Zephyr references, and diagnostic logs are traceable to the migrated source, and every source step is implemented rather than merely logged.
- New data, API calls, page methods, component methods, and fixture members were checked against their definitions and existing callers.

## Backend, GraphQL, and Integration Boundaries

- Keep service, library, adapter, and infrastructure responsibilities aligned with their existing module boundaries.
- For endpoint or integration-test work, trace the runtime call chain and existing contracts before editing. Reuse default stubs and test support where available; do not mask an unreachable state or service defect with test-only behavior.
- Keep generated code generated: update its source schema/configuration and use the repository's generation command rather than hand-editing generated output.
- Preserve API compatibility and error semantics. Update contract tests, fixtures, and documentation when a contract intentionally changes.
- Prefer module-scoped builds and tests over full-monorepo commands during iteration.
