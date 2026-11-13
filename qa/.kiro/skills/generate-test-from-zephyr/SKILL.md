---
name: generate-test-from-zephyr
description: Generate a Playwright TypeScript test from Zephyr test-case steps and logs. Researches the current QA suite before implementing a complete, maintainable journey.
keywords:
  - test automation
  - zephyr
  - test case
  - playwright
  - typescript
  - spec file
  - generate test
  - automated test
triggers:
  - generate test from zephyr
  - create test from test case
  - automate zephyr test case
  - write test from logs
---

# Generate Playwright Test from Zephyr Test Case

Generate Playwright Test TypeScript tests from Zephyr cases by researching verified current-suite patterns before writing code. Preserve every actionable Zephyr step, its data, ordering, assertions, and meaningful diagnostic logs.

## Scope And Preconditions

- Target root: `qa/`
- Tests: `qa/tests/**/*.ts`
- Page objects: `qa/src/pages/**/*.ts`
- Components: `qa/src/components/**/*.ts`
- APIs: `qa/src/api/**/*.ts`
- Test data: `qa/src/test-data/**/*.ts`
- Fixtures: `qa/src/fixtures/**/*.ts`
- Framework: Playwright Test, TypeScript strict mode, `@playwright/test`

Use the closest current-suite spec, page objects, APIs, test data, and utility methods as the implementation guide. Preserve the Zephyr journey while following established Playwright and TypeScript conventions.

## Quick Start

```bash
cd qa

# Find 2-3 similar target tests.
rg -l -i "keyword" tests

# Verify test-data values before importing them.
rg "CONSTANT_NAME" src/test-data

# Read owning page and component implementations in full.
sed -n '1,260p' src/pages/pi/payment.page.ts
sed -n '1,260p' src/components/pi/basket/basket.component.ts

# Find method definitions and existing use sites.
rg -n -C 8 "setAllGuestDetailsFields|clickBookNowButton" src tests

# Generate the spec, then type-check it.
npx tsc --noEmit

# Run only the new or changed journey locally.
EXECUTION_ENV=local npx playwright test tests/path/to/my-test.spec.ts
```

## Non-Negotiable Requirements

1. Research before implementation. Read 2-3 comparable Playwright specs, the owning page objects/components, and the relevant test-data/API surfaces.
2. Implement every Zephyr action and verification in executable code. Logs alone are not an implementation.
3. Use only constants, strings, APIs, fixture names, and page/component methods confirmed to exist.
4. Keep UI interaction and UI assertions in reusable owning page objects or components. Specs orchestrate named actions and validations.
5. Use fixture-provided page objects: import `test` and `expect` from the application fixture and receive `pages` from the test callback. For PI journeys, `global.piPages` is also available to shared runtime helpers.
6. Use `global.browser.options.app` and `global.browser.options.locale`; do not introduce legacy `browser.options.project` or `global.pages`.
7. Use `Strings`, `Locales`, and typed values from `@test-data` for visible/localized data. Do not hard-code localized UI text in a spec.
8. Preserve the source's BDD or plain-log format. Keep its Zephyr URL and meaningful `Given`/`When`/`Then` logs.
9. Every `expect(...)` in a changed QA file must include a descriptive debug message.
10. Do not invent methods, use raw locators in a spec, add arbitrary waits/retries/force-clicks, or silently remove a failed step.

## Workflow

### 1. Create The Target Spec

Choose a descriptive location under `qa/tests/`, matching nearby ownership and naming patterns. Use `.spec.ts` for the new test.

```ts
import { test, expect } from '@fixtures/pi.fixture';

test.describe('Booking journey', () => {
  test('Test TC-12345 - guest can complete the booking', async ({ pages }) => {
    console.log('Zephyr: https://...');

    console.log('-> Given I open the home page');
    await pages.homePage.open();
  });
});
```

Do not leave `test.only` or `test.skip` in a generated journey. Use a locale gate only when the baseline/source behavior requires it:

```ts
import { Locales } from '@test-data';

test.beforeEach(() => {
  test.skip(!Locales.isEnglishWebsite(), 'This journey is available only on the English website.');
});
```

### 2. Research The Existing Suite

Before using a name, validate that it exists and read its implementation, not just its declaration.

```bash
cd qa
rg -l -i "booking.*payment" tests
rg -n "HOTEL_NAME|RATE_NAME|CARD_NAME" src/test-data
rg -n -C 10 "async setPaymentDetails|async confirmCurrentBooking" src/pages src/components
rg -n "new .*Component|readonly .*Component" src/pages/pi
```

Verify:

- The target test's fixture import and test structure.
- The page object/component that owns every UI action or validation.
- Method signatures, required arguments, preconditions, and whether an action already validates a result.
- Component composition. Basket, header, footer, payment-option, and search-engine behavior may be exposed by a nested component rather than a page method.
- Typed API facades and their expected request/response shapes.
- Existing test-data constants and localized `Strings` entries.

If the needed behavior does not exist, add a focused reusable method to its owning page, component, API facade, or test-data owner before calling it from the spec. Add a concise JSDoc and an entry diagnostic log to each new UI action or validation method.

### 3. Translate Zephyr Steps Precisely

Match the original log style:

```ts
console.log('-> When I click Continue');
await pages.ancillariesPage.clickContinueButton();

console.log('-> Then the guest details page is displayed');
await pages.guestDetailsPage.validatePage();
```

For plain Zephyr steps:

```ts
console.log('-> Click Continue button');
await pages.ancillariesPage.clickContinueButton();

console.log('-> Guest details page is displayed');
await pages.guestDetailsPage.validatePage();
```

Logs are diagnostic context; each action requires a state-changing call and every verification requires an assertion or named validation.

### 4. Use Target Framework Boundaries

**Page and component ownership**

```ts
// Correct: page-level action.
await pages.searchResultsPage.searchHotels({ criteria });

// Correct: composed component action, when exposed by the page object.
await pages.searchResultsPage.basket.clickBookNowButton();

// Incorrect: invented page method.
await pages.searchResultsPage.clickBookNowButton();

// Incorrect: direct locator interaction in a spec.
// await global.page.getByRole('button', { name: 'Book now' }).click();
```

**Assertions**

Put UI assertions in page/component validation methods and attach a diagnostic message:

```ts
await expect(this.pageTitle, 'Guest details page title is visible').toBeVisible();
await expect(total, 'Displayed total matches the selected rate').toBe(expectedTotal);
```

**Data, locale, and APIs**

```ts
import { Cards, Hotels, HotelRates, Locales, Strings } from '@test-data';

const locale = Locales.getLocaleByString(global.browser.options.locale);
const hotel = Hotels.DEFAULT_GERMAN_HOTEL;
await pages.paymentPage.setPaymentDetails(Cards.VISA_CARD);
```

Keep convenience-to-wire transformations in the owning typed API facade. Preserve required payment lifecycle states and all source API parameters.

**Waits and navigation**

Use page-object `open()` methods and Playwright locator/expect auto-waiting. When a reusable method genuinely needs an explicit visibility wait, use `global.browser.options.actionTimeout`. Do not copy `browser.pause`, add arbitrary `waitForTimeout`, or force click controls to conceal a failed precondition.

### 5. Resolve Missing Support Correctly

Do not use an unverified fallback value or direct UI locator workaround in a test merely to make it compile. Prefer this sequence:

1. Search the relevant target owner for the actual constant, method, component, or API facade.
2. Inspect comparable current-suite journeys for intended behavior and data.
3. Add missing reusable support in the proper target owner, preserving the Zephyr journey.
4. Use a TODO only when a genuine external dependency or unimplementable requirement remains. State what was searched and why it blocks the exact Zephyr step.

Example:

```ts
// TODO: Add selectPayOnArrival() to PaymentOptionComponent after confirming
// the target control's accessible name and reference behavior.
```

A TODO is not permission to omit or fake a Zephyr action or validation.

## Validation Checklist

Before considering the journey complete, confirm:

- [ ] Two or more comparable Playwright tests were reviewed.
- [ ] Every Zephyr action and verification has executable code.
- [ ] Test data, APIs, methods, fixtures, and component ownership were verified.
- [ ] The test starts with its Zephyr URL and preserves meaningful step logs and BDD/plain format.
- [ ] Test name begins with `Test ` and includes the Zephyr test-case ID.
- [ ] UI actions/assertions are encapsulated in pages/components; changed UI methods have JSDoc and entry logs.
- [ ] Changed assertions have descriptive debug messages.
- [ ] No unused imports, variables, `test.only`, arbitrary wait, retry, fallback, conditional skip, or force-click was introduced.
- [ ] Locale behavior uses `Locales`, `Strings`, and `global.browser.options.locale`.
- [ ] `npx tsc --noEmit` passes.
- [ ] The focused test was run with `EXECUTION_ENV=local npx playwright test <path>` or its precise environment blocker is documented.

## Troubleshooting

| Symptom | Correct response |
| --- | --- |
| A name is missing | Search target owners and comparable journeys; add focused typed support to the correct owner. |
| A page method is missing | Inspect composed components and call or extend the proper component. |
| A control is visible but disabled | Repair the test's source-equivalent precondition, seeded data, or policy boundary. Never force-click. |
| A test has only logs | Implement the corresponding action/validation; do not retain placeholder logs. |
| TypeScript fails | Remove unused values, correct imports/signatures, then rerun `npx tsc --noEmit`. |
| Runtime fails after type-checking | Run only the spec, trace the failed step to its page/component/API owner, and repair that owner without dropping the journey step. |

## Playwright Translation Reference

| WebdriverIO | Playwright target |
| --- | --- |
| `browser.url(path)` | Owning page object's `open()` method |
| `browser.getUrl()` | `global.page.url()` in framework/runtime code |
| `browser.pause(ms)` | Locator/expect auto-waiting; no arbitrary sleep |
| `element.click()` | Owning page/component action that calls `locator.click()` |
| `element.setValue(value)` | Owning action that calls `locator.fill(value)` |
| `waitForDisplayed()` | `expect(locator, message).toBeVisible()` |
| Chai assertion | `expect(actual, message).toBe(...)` |
| `browser.options.locale` | `global.browser.options.locale` |
| `pages` / `global.pages` | Fixture `pages` / `global.piPages` |
