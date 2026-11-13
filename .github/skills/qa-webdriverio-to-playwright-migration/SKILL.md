---
name: qa-webdriverio-to-playwright-migration
description: "Use when: migrating qa/reference WebdriverIO JavaScript Mocha tests, page objects, API helpers, test data, utilities, globals, hooks, locale handling, function/method documentation, or flaky regression fixes into the qa Playwright TypeScript framework. Trigger on webdriverio, wdio, qa/reference, Playwright, TypeScript, e2e migration, baseline test migration, page object migration, method docs, function docs, or compare with reference."
argument-hint: "Source qa/reference path, target qa path, failing test, or migration goal"
---

# QA WebdriverIO to Playwright Migration Skill

Use this skill to migrate or repair Premier Inn QA automation from the legacy `qa/reference` WebdriverIO/JavaScript framework into the `qa` Playwright/TypeScript framework.

The core rule is behavioral fidelity: the migrated Playwright test should preserve the `qa/reference` journey, data, validations, and helper semantics unless the difference is required by Playwright, TypeScript, or the current `qa` framework design.

## When to Use

Use this skill for:
- converting `qa/reference/test/specs/**/*.spec.js` tests into `qa/tests/**/*.ts`
- converting `qa/reference/test/pages/**/*.js` page objects into `qa/src/pages/**/*.ts`
- converting `qa/reference/test/api`, `constants`, `data`, `fixtures`, or `utilities` code into `qa/src/api`, `qa/src/test-data`, `qa/src/utils`, or fixtures
- fixing migrated Playwright regressions where `qa/reference` already works
- aligning locale, strings, feature toggles, hooks, waits, selectors, page objects, or validation behavior with the reference implementation

## Required Starting Point

Always start from the most concrete anchor available:
- source file under `qa/reference`
- target file under `qa`
- failing Playwright test output
- failing method, selector, page object, API helper, or validation symbol
- visible Zephyr/test-case title in a baseline journey

If the user gives only a target Playwright file or failing error, find the corresponding `qa/reference` implementation before editing. For baseline failures, search the exact test-case ID first and confirm the reference title and file before drawing conclusions; then use nearby names, page-object methods, console step text, selectors, constants, and data class names to resolve any remaining ambiguity.

## Bulk/Exhaustive Sweep Requests

When the user asks to fix, migrate, or type an entire directory or class of files (e.g. "all the response/request DTOs", "until completing all the files", "always continue"), treat it as a persistence directive, not a one-shot batch:
- Enumerate the full file list up front (e.g. via grep for a marker like `any` fields or a stub method pattern) so progress can be measured against a concrete count.
- Track completed/remaining files in session memory so the sweep can resume across turns without re-scanning from scratch or redoing finished files.
- Work in small batches (roughly 5-10 files), re-running the enumeration query and a full `tsc --noEmit` (or equivalent) after each batch, not just at the end.
- Do not stop early to ask whether to continue once the user has said to continue/complete all files - keep going batch by batch until the enumeration query returns empty or a genuine blocker is hit.
- After every multi-file edit, re-read the tail of each changed file (or re-run the type check) to catch partial replacements that leave duplicate/dead trailing code - this is a common failure mode when only part of a class body was matched.

## Recent Chat-History Rules

These rules come from repeated recent migration prompts and corrections:

### Fidelity And Runtime

- Read the closest `qa/reference` implementation before changing target tests, pages, API helpers, data, or utilities. Preserve its journey, data, validations, console steps, and behavior; investigate any target-only branch, retry, fallback, assertion, or timeout before keeping it.
- Use `global.browser.options.app` exclusively in target application code. Never add or retain `browser.options.project`/`options.project` compatibility paths; `testInfo.project` and `info.project` remain valid Playwright metadata.
- When porting a reference branch on `browser.options.project`, translate it to `global.browser.options.app`; do not copy the legacy property name. Confirm the target runtime options in `playwright.config.ts` or focused test output before changing an existing `app` condition.
- Keep locale behavior explicit through `global.browser.options.locale`, including `gb-en` and `de-de`. Use the exact `Strings` entry and existing constants from `qa/src/test-data` rather than hard-coded UI text or domain values.
- When the reference has a suite-level `before` locale gate, migrate it as a Playwright `test.beforeEach` hook using `test.skip(!Locales.isEnglishWebsite(), reason)`. Do not compare `global.browser.options.locale` directly in the spec when `Locales` owns the check, and never leave `test.only` in a migrated regression.
- Treat a visible but disabled amendment control as a failed journey precondition, not a clickability problem. Do not force-click it, remove its `disabled` attribute, add a conditional skip, or substitute a different UI action unless the reference has that branch. Trace the policy and seeded dates; for policies expressed as “three full days before arrival,” create a deterministic booking beyond that boundary from the confirmed availability interval.

### API Contracts And Reuse

- For a target API helper, audit its entire public facade: request inputs, GraphQL/REST responses, return types, JSDoc, and changed callers. Use classes from `qa/src/api/requests` and `qa/src/api/response` where the reference does, preserving constructor keys and nesting such as `new Basket({ basket })`.
- Preserve backwards compatibility for the existing Playwright target surface while migrating reference behavior. Do not remove or silently rename public page/component/API methods, constructors, exports, fixture properties, or accepted data shapes used by current `qa/tests` callers. When a reference-aligned name differs from an established target name, keep a thin deprecated delegate or compatibility alias with the old signature, update callers incrementally, and typecheck/search all usages before removing it. This rule applies to target APIs and facades; it does not justify retaining obsolete framework globals such as `browser.options.project` or `global.pages`.
- When the reference returns a primitive, collection, or native-service payload, preserve that wire shape using a small named owner-local interface. Do not substitute an incompatible GraphQL DTO. Public API methods must not expose `Promise<any>`, `Promise<unknown>`, or generic `Promise<Object>`; type unions, collection elements, and `Promise<void>` explicitly.
- Keep JSDoc aligned with implementation: name request and response types, distinguish criteria objects from string codes, and correct copied endpoint descriptions. Preserve reference JSDoc and class-level response examples unless TypeScript or Playwright makes wording inaccurate.
- Remove proven duplication only within the current slice. Keep one canonical element, method, or orchestration path for a purpose; make stack-specific callers delegate to it instead of creating parallel wrappers with the same behavior. Reuse existing DTOs, constants, and helpers first; extract repeated pure plumbing to the smallest established shared owner (`qa/src/api` for API guards, `qa/src/utils` for general utilities). Do not create a new helper merely to move a small duplicated block when an existing owning abstraction can be extended. Keep routing, retries, and diagnostics local unless they are exactly identical. Update every proven duplicate, retain thin local aliases only when current callers require compatibility, search for the removed implementation, then typecheck.
- Keep authentication token responses and bearer strings distinct, including separate caches for different audiences. Name non-GraphQL external payloads after their owning service (for example, `Ohip...Response`).
- Translate high-level API helper inputs into the exact GraphQL/REST wire payload inside the owning API facade; never forward convenience arguments directly to a GraphQL document with a different variable contract. Preserve source payment sequencing, including payment initiation, any required `PAY_PENDING` wait, payment webhook, and final `COMPLETED` wait. Keep card shapes distinct at their boundary: map a legacy card fixture to GraphQL `CardRequest` fields for payment initiation, while retaining the full card data required by tokenization/webhook calls.
- Preserve custom reservation availability inputs end to end. When the underlying reservation API treats `hotelId` and `hotelAvailabilityInput` as mutually exclusive, omit `hotelId` for custom inputs; do not silently fall back to the default one-adult room. Configure REST/entity and webhook endpoints from environment-aware runtime options, and obtain credentials or API keys only from environment configuration rather than hard-coding or logging them.

### Targeted Migration Rules

- Compare the complete public surface of every selected reference page or component before implementing its target. Migrate all source-used elements, composed components, actions, validations, and setup prerequisites, including inherited/base-page behavior; do not create a narrow replacement that only satisfies the first failing test. When a reference page composes shared components, preserve that composition and make the target page use the same owning components rather than duplicating their locators inline.
- Keep component ownership explicit during refactors: move a locator and its UI action/validation into the nearest reference-aligned component, then update tests to call that component directly. Keep a single canonical component property/method name and retain only a thin typed deprecated alias when existing target callers require compatibility; do not leave page-level wrapper methods that merely forward to a component.
- Use semantic renames for renamed TypeScript symbols. For page/component migrations, preserve the reference's full reusable surface, composition, section-comment structure, and page-context folder nesting. Keep the section comments for UI elements, UI actions, and UI validations as they appear in `qa/reference`.
- Keep exactly one instance of each section marker per class. Locator-returning factories and non-mutating UI getters belong under `UI elements/properties`; clicks, navigation, mutation, and displayed-value reads belong under `UI actions/navigation`; assertions belong under `UI validations`.
- Migrate source comments along with source behavior. Preserve class-level descriptions, journey-step comments, `Given`/`When`/`Then` intent, setup rationale, feature-flag workarounds, and API/UI validation context from `qa/reference`; translate only framework-specific wording that is no longer accurate. Do not replace meaningful source comments with generic summaries.
- Migrate source journey logs along with source behavior. Preserve the Zephyr/test-case URL and meaningful `console.log` messages for `Given`/`When`/`Then` steps, setup, API/UI validation, amendment, cancellation, and cleanup. Update framework-specific wording only when needed for the Playwright implementation, and keep logs concise enough to remain useful in test output.
- For search-driven journeys, compare the complete reference search orchestration before editing: whether the flow searches from the home page, selects a hotel suggestion/result, or opens HDP directly. Keep availability-derived arrival/departure dates and room occupancy as the single source for both the UI search and expected-rate capture; do not initialize HDP with default or stale query dates and then overwrite them through a second search path.
- Preserve the complete source journey when a migrated step fails. Do not remove room, guest, meal, payment, API, or validation functionality to make a test pass; trace the failure to its owning page object, component, API helper, data, selector, wait, or environment contract and repair that support instead. Keep the source step in the test and rerun the focused check.
- Do not add retries, repeat loops, fallback attempts, or extended polling to migrated code unless the corresponding behavior exists in `qa/reference` or is required by an explicit target-framework/API contract. Repair the failing action or wait at its owning abstraction instead of hiding a mismatch with retry logic.
- Avoid adding `try/catch` around UI actions, validations, navigation, selectors, or migrated helper flows as a general fallback mechanism. Prefer explicit Playwright locators, web assertions, precondition checks, and owner-level waits that preserve the reference behavior. Use `try/catch` only when the closest `qa/reference` implementation has equivalent error handling, or when a narrow Playwright/platform boundary has a documented non-behavioral failure mode; keep that handling local, log the reason, and never use it to continue after a failed required journey step.
- Add an entry log to every migrated UI action and UI validation method, before its first locator interaction or assertion. Use concise source-aligned wording that identifies the action or validation and its key input, so failed Playwright output remains diagnostically useful.
- Preserve initial reservation state from the reference, including pre-confirmation package or meal selections. When the target confirmation facade cannot safely express the source sequence, add or repair a reusable API owner rather than silently creating a meal-free or otherwise incomplete reservation.
- Document every migrated or newly added function and method with concise JSDoc. Include parameter and return details when they clarify the contract, and preserve source caveats, examples, and behavior notes. This applies to page objects, components, API helpers, utilities, fixtures, and test-local helpers.
- Place exactly one method JSDoc block immediately above its method, getter, setter, or constructor signature. Never leave documentation inline with the declaration, inside a multiline parameter list, or inside the method body. When repairing migrated files, verify that comments remain attached to the correct method rather than merely moving all comments in source order; preserve the reference wording and remove orphaned, duplicated, or shifted blocks.
- Keep migrated method formatting structurally readable: use the surrounding two-space class-member/four-space body indentation, put the method signature below its documentation, and keep locator declarations and Playwright assertions on one line when they fit the existing PIB formatting convention. Do not use formatting-only rewrites to alter behavior.
- Remove constructors that only call `super()` when the base class constructor already provides the required initialization. Keep a constructor only when it adds configuration, dependency injection, state, or other subclass-specific behavior; do not retain a redundant constructor solely to attach documentation.
- Keep migrated page and component classes in separate target files that mirror the closest `qa/reference` file and folder structure. Do not create catch-all files that define multiple page/component classes together. If several reference components were temporarily grouped in one target file, split them into one class per file before finishing and update imports/barrel exports accordingly.
- Keep UI interaction encapsulated in page objects and components. Migrated tests must call reusable page-level or composed-component actions rather than accessing or clicking locator properties directly (for example, use `loginForLeisureCustomer()` instead of `loginButton.click()`). Do not use the raw Playwright `page`/`global.page` to locate, click, fill, select, read, or assert UI elements from a test when an owning page or component can provide the method; add that reusable method to the owning class first. The `page` fixture remains appropriate in tests for browser/runtime concerns such as reset helpers, context/cookie setup, navigation required by the framework, and API-response synchronization.
- Declare stable, reusable UI elements in the UI elements/properties section as fixed locators whenever the reference element has a stable selector. Do not parameterize those locator properties or factories with visible text merely to reuse a text-based locator; use the reference selector, a stable test ID, or a scoped structural locator. Parameterize only when the reference itself represents a genuinely variable repeated element, and keep localized text resolution in the action or validation that needs it.
- Use fixture-provided global page objects for shared runtime helpers that need page-level navigation. In the PI suite, use `global.piPages.<pageName>` rather than constructing an ad hoc page object or introducing the legacy `global.pages` alias. A reset helper that loads an application page must delegate to the appropriate page object's `open()` method, never call `page.goto()` directly; for a journey whose `qa/reference` source uses BART, use `global.piPages.bartHomePage.open()`.
- Use the injected `pages.<pageName>` fixture property for every page participating in a journey, including optional pages such as `chooseYourBathroomPage`. If a stack fixture does not expose a reference-used page, add it to that fixture's `Pages` type and setup rather than constructing the page directly inside a test.
- Preserve page-action prerequisites when migrating higher-level methods. A method such as login must not assume the browser is already on the correct page when the reference action opened or navigated there; either include the navigation in the owning page action or make the test explicitly open the page before calling it. Validate this with a focused runtime check, since an action on `about:blank` can pass TypeScript and test discovery while failing at runtime.
- When the reference uses a named orchestration action (for example, BART-to-HDP navigation followed by sign-in), migrate that action into its owning page object and call it from the test. Do not reduce it to a generic login call. Preserve all source preconditions, including sign-in-cookie clearing and post-navigation page validation. For legacy headers, retain every proven source selector fallback, including nonstandard attributes such as `date-testid`, and recognize the authenticated-header state before retrying a login.
- Keep UI validations encapsulated in page objects and components. Migrated tests should orchestrate named validation methods such as `validatePage()`, `validateBookingDetails()`, or `validateTotalCost()` rather than asserting directly against UI locators, `page`/`global.page` text, or DOM state. Add the missing reusable validation to the owning page/component; API, data-contract, and cross-service assertions may remain in the test or their API helper.
- Mirror `qa/reference`'s page-name subfolders under the matching `qa/src/components/<app>/` directory (e.g. `qa/reference/test/pages/components/opera/searchConsole/` -> `qa/src/components/pi/searchConsole/`). Move an existing flat `*.component.ts` file into its page-name folder when one exists in the reference structure, and create the folder (with a `.gitkeep` placeholder) even before any component for that page has been migrated, so future files land in the reference-aligned location. Components with no page-name folder in the reference structure (e.g. `cookiesConsentModal.js`) stay flat.
- Match the reference sequencing for credential handling and browser-context changes. Configure HTTP authentication with `page.context().setHTTPCredentials(...)` and navigate to plain URLs; never embed usernames or passwords in URLs, logs, traces, reports, or browser history. When secure domains use different passwords, update the context credentials per domain before navigation. In date pickers, navigate months and use formatted accessible names rather than raw ISO dates.
- Preserve the reference Manage Booking authentication flow. Do not clear HTTP credentials before submitting the modal search when the target environment still needs them; submit the search, wait for the search control/modal to disappear, then reload the current URL with credentials removed using the target `BrowserUtils` equivalent. Keep the URL sanitization step in the owning page/component action rather than moving it into an unrelated test hook.
- Preserve the reference secure-host warm-up for CCUI. When the environment exposes multiple secure hosts, authorize `secureUrl2` (the `www` host) before `secureUrl`, then open the CCUI base URL. Keep `secureUrl2` in the target runtime options and use the authorized CCUI home-page open path from application reset; otherwise authentication can fail before the login flow begins.
- Preserve payment-field behavior, not only the card number. Compare the reference payment component and ensure expiry month/year controls, hidden/native selects, combined visible inputs, iframe boundaries, and their post-entry validation are handled when the provider exposes more than one representation. A card fixture containing an expiry date is not sufficient evidence that the expiry was entered into the live payment control.
- Preserve state re-entry after page refreshes and navigation. If the reference reintroduces a booking reference, search criteria, or other journey state after refreshing a page, keep that step in the migrated flow and place it in the owning page action or test orchestration. Do not assume a value remains in a form after reload merely because it was entered earlier in the journey.
- Scope component locators to their owning container whenever the page can render repeated controls, overlays, or loading spinners. For dropdowns, inspect the reference selector and live ARIA role before choosing a Playwright locator; do not assume native `option` semantics when the UI exposes `menuitem` controls. Migrate date validations as the source expresses them: calculate the displayed checkout date from the displayed arrival date and selected nights, rather than comparing it to an unrelated ISO input value.
- When desktop and mobile variants are both present in the DOM, select the configured responsive variant through `Constants.BROWSER_RESOLUTIONS` (or the owning component's equivalent) instead of unioning both `:visible` selectors. This avoids transient duplicate counts during responsive rerenders and keeps row/control assertions tied to the active UI.
- Run the narrowest meaningful validation: `npx playwright test <path>` for migrated behavior, plus `cd qa && npx tsc --noEmit` after API/type/shared-utility changes. Report environment or credential blockers precisely.

## Framework Map

Legacy source:
- root: `qa/reference`
- tests: `qa/reference/test/specs/**/*.spec.js`
- page objects: `qa/reference/test/pages/**/*.js`
- test data/constants: `qa/reference/test/data`, `qa/reference/test/constants`
- APIs: `qa/reference/test/api`
- utilities: `qa/reference/utilities`
- framework: WebdriverIO v8, Mocha, Chai, JavaScript, global `browser`, global `$`/`$$`

Playwright target:
- root: `qa`
- tests: `qa/tests/**/*.ts`
- page objects: `qa/src/pages/**/*.ts`
- test data/constants: `qa/src/test-data`
- APIs: `qa/src/api`
- utilities: `qa/src/utils`
- fixtures: `qa/src/fixtures`
- framework: Playwright Test, TypeScript strict mode, `@playwright/test`, `global.browser.options`, `global.page`

Use existing path aliases from `qa/tsconfig.json`, for example `@fixtures/*`, `@pages/*`, `@api/*`, `@test-data/*`, and `@utils/*`, when the surrounding code already does.

## Migration Procedure

1. Map the source and target: read the closest `qa/reference` behavior and JSDoc, then choose the owning target layer (`qa/tests`, pages/components, `src/api`, test data, or fixtures).
2. Translate behavior, not syntax: preserve source data, locale branches, validations, and sequencing while adapting only the framework mechanics needed for Playwright and TypeScript. Any new branch, retry, fallback, assertion, timeout, or selector must be justified by a target-framework constraint or current API behavior.
3. Reproduce the terminal failure before changing behavior, then validate the smallest affected surface: run a focused Playwright spec for behavior changes and `cd qa && npx tsc --noEmit` for API, type, or shared-utility changes. Distinguish an external API failure, such as a payment-webhook `400`, from the UI issue under repair; state the blocker precisely instead of claiming an unrelated fix passed.
4. Remove temporary diagnostic logging or probes after the focused check identifies the cause. Keep only concise, source-aligned diagnostics that are useful for the final migrated journey.
5. When the editor reports a syntax or type error, confirm it with the current compiler/typecheck before editing; refresh or disregard stale Problems diagnostics when the executable check is clean.

## Common Translation Patterns

### Test Structure

WebdriverIO/Mocha:

```js
describe('Journey title', function () {
  it('Test case title', async function () {
    await pages.homePage.open();
  });
});
```

Playwright:

```ts
import { test, expect } from '@fixtures/pi.fixture';

test.describe('Journey title', () => {
  test('Test case title', async ({ pages }) => {
    await pages.homePage.open();
  });
});
```

Keep source `describe` and test titles recognizable. Preserve Zephyr links and console step logs when they document the migrated baseline journey.

### Page Objects

WebdriverIO getters:

```js
get submitButton() { return $('[data-testid="submit"]'); }
```

Playwright locators:

```ts
readonly submitButton: Locator = this.page.getByTestId('submit');
```

Prefer stable `data-testid` locators already used in nearby Playwright page objects. Do not parameterize a locator with visible text when a stable selector or scoped structural locator exists. When no stable selector exists, a reference-backed visible-text, label, or accessible-name locator is acceptable; resolve localized text through `Strings`/`Locales` and scope it to the owning component. Use XPath only when the target suite already needs it for the same UI surface or no stable structural locator exists.

Preserve the reference section comments and their order when grouping the migrated page-object members:

```ts
// ######## UI elements/properties ########
readonly submitButton: Locator = this.page.getByTestId('submit');

// ######## UI actions/navigation ########
async submit(): Promise<void> {
  await this.submitButton.click();
}

// ######## UI validations ########
async expectConfirmation(): Promise<void> {
  await expect(this.page.getByRole('status')).toBeVisible();
}
```

Use the exact section comment text from the corresponding `qa/reference` file when it differs from this example.

This section grouping applies to existing, already-migrated pages and components too, not just new migrations. Place every new member in the section that describes its responsibility:
- `// ######## UI elements/properties ########`: locator properties, composed child page/component instances, locator factory/getter methods that return a `Locator` without interacting with the page, and non-mutating helpers whose sole purpose is to resolve or read a UI element's state (for example, a notification color). For example, `getRegularGuestEmailLabel(guestName): Locator` belongs here even though it is declared as a method.
- `// ######## UI actions/navigation ########`: interactions, navigation, state-changing methods, and read-only helpers that retrieve broader displayed values or page state without asserting it. Methods that only resolve/read a UI element belong in UI elements/properties; methods that perform an interaction, navigation, mutation, or workflow step belong here.
- `// ######## UI validations ########`: methods whose purpose is to assert visible UI state or content; keep all `expect(...)` calls inside these validation methods unless the assertion is an internal guard required by an action/getter's documented contract.

When editing a page/component file that lacks the `// ######## UI elements/properties ########` / `// ######## UI actions/navigation ########` / `// ######## UI validations ########` grouping, retrofit those section comments around its existing members in the same pass, ordering members to match the corresponding `qa/reference` file's grouping. Before finishing, review every newly added member against this placement rule and move it to the correct section rather than appending it to the end of the class.

### Browser And Page Globals

The target suite intentionally emulates some WebdriverIO globals:
- `global.browser.options` is set by `qa/src/fixtures/base.fixture.ts` from `qa/playwright.config.ts` runtime options; use the `app` option as specified in Fidelity And Runtime.
- `global.page` is set by the base and app fixtures.
- PI page objects are exposed through the `pages` fixture and as `global.piPages` in `qa/src/fixtures/pi.fixture.ts`.
- Runtime configuration supports both the legacy environment names (`APP`, `ENV`, `BROWSER`, `VIEWPORT`) and the Playwright-style names (`PROJECT`, `ENVIRONMENT`, `BROWSER_NAME`, `BROWSER_RESOLUTION`). Helpers used by specs importing plain `@playwright/test` must not assume `global.browser.options` is initialized; use the established environment fallback for that helper's specific setting when required.

Do not introduce a separate options system or a legacy `global.pages` alias for migrated code. Use the existing fixture, `global.piPages`, and runtime options.

### Actions And Waits

Map common WebdriverIO calls to Playwright:
- `browser.url(path)` -> `page.goto(path)` or an existing page object's `open()` method
- `browser.getUrl()` -> `page.url()`
- `browser.pause(ms)` -> prefer locator/web assertion waits; use `waitForTimeout` only as a last resort matching unavoidable app behavior
- `element.click()` / `element.jsClick()` -> `locator.click()` unless the target page object already has a helper for that interaction
- `element.setValue(value)` -> `locator.fill(value)`
- `element.addValue(value)` -> `locator.pressSequentially(value)` or `locator.fill(existing + value)` depending on source behavior
- `element.isDisplayed()` -> `await locator.isVisible()` or `await expect(locator).toBeVisible()`
- `waitForDisplayed` / `waitForExist` -> `await expect(locator).toBeVisible()` or `locator.waitFor()`
- `browser.switchToFrame(...)` -> `frameLocator(...)` or page/frame helper methods
- `browser.switchWindow(...)` -> `context.waitForEvent('page')` plus assertions on the new `Page`

Use Playwright auto-waiting and the default expect/locator timeout from config. For `waitFor` methods, explicitly use `browser.options.actionTimeout`, for example `locator.waitFor({ state: 'visible', timeout: browser.options.actionTimeout })`. Do not add explicit timeout values to assertions when the config default is intended; omit the `timeout` option from patterns like `expect(locator).toBeVisible()`.

Avoid copying arbitrary sleeps from WebdriverIO unless the target app genuinely requires them and nearby Playwright code already uses the same pattern.

### Assertions

Map Chai and soft assertions to Playwright expectations:
- `expect(value).to.equal(expected)` -> `expect(value).toBe(expected)`
- `expect(value).to.deep.equal(expected)` -> `expect(value).toEqual(expected)`
- `expect(text).to.contain(fragment)` -> `expect(text).toContain(fragment)`
- element visibility checks -> `await expect(locator).toBeVisible()`

Keep assertion messages where they explain business behavior. Do not delete reference validations just because they are inconvenient to migrate.

Preserve the reference's chai/`UiUtils` message string (e.g. `chai.expect(x).to.equal(y, 'List container')`, `UiUtils.validateElementIsDisplayed({ elementDescription: 'Alert label' })`) as the Playwright assertion message: `expect(locator, 'List container').toBeVisible()` / `expect(actual, 'Alert label').toContainText(...)`. Also keep any `console.log(...)` debug line that precedes the reference validation, even when the migrated assertion itself is one line.

Every `expect(...)` call must carry a debug message describing what is being checked, even when the reference had none: `expect(locator, 'Continue button label').toHaveText(...)` rather than a bare `expect(locator).toHaveText(...)`. Before finishing any edit that touches assertions, re-scan every `expect(` call in the changed file(s) and add a message to any that are missing one.

### XPath Text Locators

When migrating XPath selectors that match UI text, use `contains(text(), "...")` or `normalize-space()="..."` when the target markup may contain leading or trailing whitespace. Avoid exact `text()="..."` matching unless whitespace is part of the contract; it can fail when the visible label appears unchanged but the DOM text includes whitespace.

### Strings And Locale

Reference code often uses `String.*.name`, `Locale.getLocaleByString(browser.options.locale)`, and locale-specific branches. Target code should use `Strings`, `Locales`, and `global.browser.options.locale` equivalents from `qa/src/test-data`.

Rules:
- Use `Strings` objects for UI text expectations when a matching value exists.
- Preserve German behavior for `de-de`; do not replace localized expectations with English literals.
- Use locale helpers such as `Locales.getLocaleByString`, `Locales.isEnglishWebsite`, `getCurrentLocale`, or `withLocaleDefaults` when they fit the nearby target code.
- If a source string is missing in target `Strings`, migrate the string object instead of hard-coding it in a spec.
Migrate source constants and data classes into the target data/API layers:
- `Hotel` -> `Hotels`
- `HotelRate` -> `HotelRates`
- `Room` -> `Rooms` or typed room data
- `Card` -> `Cards`
- `GuestDetails` -> `GuestData` or target guest-detail helpers
- `SearchCriteria` -> `SearchCriteriaData`
- `ApiCalls`, `ApiContentCalls`, `ApiSlugsCalls`, `ApiBasketCalls`, `ApiDictionary`, `ApiHelpers` -> target equivalents in `qa/src/api`

When a reference helper includes important defaulting or locale behavior, migrate that behavior into the target helper. Do not duplicate one-off API payload construction in the test unless the target suite already does so nearby.

### Feature Toggles, Cookies, And Hooks

Use target utilities and fixtures:
- feature toggles: `FeaturesToggles` in `qa/src/utils/featuresToggles.ts`
- application reset: `resetApplicationState()`; it uses fixture-provided `global.page`
- cookies and notifications: existing page/component helpers such as cookie consent and notification popup components
- global setup/teardown: `qa/src/fixtures/global-setup.ts`, `qa/src/fixtures/global-teardown.ts`, wired through `qa/playwright.config.ts`

Keep default feature-toggle values aligned with the reference behavior unless the migrated framework already centralized those defaults.

## Review Checklist

Before finishing a migration or fix, verify:
- The closest `qa/reference` source was checked.
- The complete source public surface and page/component composition were compared, including inherited/base-page behavior and all components used by the journey.
- The target preserves source data, locale behavior, feature toggles, API parameters, and validations.
- Existing target callers remain source-compatible: public methods, constructors, exports, fixture properties, and accepted input shapes are preserved or supported by typed deprecated delegates during incremental migration.
- High-level API helpers transform their inputs to the exact downstream GraphQL/REST contract, including the source payment lifecycle and distinct card representations where applicable.
- Custom reservation availability input, room/guest shape, rate, and date interval match the reference; no mutually exclusive creation parameters are passed together.
- Search-driven journeys use the same availability-backed dates and occupancy for UI search, expected-rate capture, and booking; no stale direct-navigation bootstrap is left in front of a second search.
- Keep constants grouped where the test establishes its scenario; declare phase-local variables at their first use and use `const` unless reassignment is required across phases.
- Any behavior that differs from the source is explicitly justified by Playwright/TypeScript/framework constraints.
- No source validation was silently removed.
- No new conditional skip/fallback was added only to make a failure disappear.
- No broad `try/catch` was added to hide selector, action, validation, navigation, or required journey-step failures; any remaining `try/catch` is reference-backed or narrowly justified by a documented Playwright/platform boundary.
- `Strings`/`Locales` are used for localized UI text.
- Stable UI elements are declared as fixed locators; text is not used as an unnecessary parameter to locator properties or factories.
- Every `expect(...)` assertion in the changed files carries a debug message (no bare `expect(locator).toX()` calls).
- Page-object, API-helper, utility, fixture, and shared-helper methods/functions remain reusable and documented where the source was documented.
- All migrated and newly added functions/methods have concise JSDoc, including parameter/return contracts where useful; redundant `super()`-only constructors are removed.
- Tests do not directly invoke page/component locator properties for UI actions; equivalent reusable actions belong on the owning page object or component and are called from the test.
- Page-level actions preserve their source prerequisites, including navigation and required page state; focused runtime execution must be used to catch failures hidden by clean TypeScript compilation or Playwright test discovery.
- Refresh and re-entry steps restore the booking/search state required by the source journey.
- Named source orchestration actions, header selector fallbacks, authenticated-state checks, and post-action validations were retained rather than replaced with generic calls.
- Tests do not contain direct UI validation assertions against locators, page text, or DOM state; those checks belong in reusable page-object/component validation methods with descriptive assertion messages.
- Component locators are scoped where repeated page controls exist, and dropdown/date assertions reflect the source control semantics and displayed values.
- Payment fields preserve the source expiry interaction and validate the live provider control, including iframe or hidden/native input boundaries where applicable.
- Section comments for UI elements, UI actions, and UI validations match the corresponding `qa/reference` implementation.
- Each migrated page/component class lives in its own target file matching the reference page/component folder structure; no catch-all page/component file contains multiple migrated classes.
- Meaningful source journey comments, `Given`/`When`/`Then` steps, setup rationale, workaround notes, and validation context were migrated or intentionally updated for target-framework accuracy.
- Source Zephyr links and meaningful journey `console.log` diagnostics were migrated or intentionally updated, including logs that precede reusable page-object/component validations.
- Every migrated UI action and UI validation method begins with a concise diagnostic log before interacting with locators or asserting state.
- The migrated journey retains all source functionality and initial state, including room/guest changes, meal/package selections, payment sequencing, API validations, and cleanup; failures are repaired at the owning abstraction rather than resolved by deleting steps.
- No retry or fallback behavior was added unless it is present in the reference or required by the target contract.
- Temporary diagnostic logging or probes were removed after the failure cause was confirmed; only concise source-aligned diagnostics remain.
- Source method/function JSDoc, parameter notes, return notes, examples, and behavior caveats were preserved or intentionally updated for accurate target names/types.
- Class-level JSDoc/response-example comments from `qa/reference` were preserved, not replaced with a generic placeholder.
- The narrowest available validation was run or the blocker was reported.
