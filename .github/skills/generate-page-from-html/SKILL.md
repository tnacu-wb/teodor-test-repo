---
name: generate-page-from-html
description: "Use when: generating or extending Playwright TypeScript page objects and components from HTML, selecting stable locators, mapping UI elements, or extracting page sections into components. Trigger on page object from HTML, generate page, create page class, extract selectors, or HTML components."
argument-hint: "HTML source, target page/component path, or UI surface to model"
---

# Generate Playwright Page Object from HTML

Generate Playwright TypeScript page objects and components from HTML. Analyze the structure first, map every testable element, then create reusable component and page methods that follow the current QA suite.

## Scope And Target Conventions

- Pages: `qa/src/pages/<app>/`
- Components: `qa/src/components/<app>/` or `qa/src/components/shared/`
- Page base class: `BasePage` from `@pages/shared/base.page`
- Test fixture: `@fixtures/pi.fixture`
- Test data: `@test-data`
- Browser access: `global.page` and `global.browser.options`
- Framework: Playwright Test with TypeScript strict mode

Use existing page objects and components as the implementation guide. Do not paste HTML into the committed TypeScript file; HTML is analysis input only.

## Component Rule (Mandatory)

**Every HTML section with two or more related testable elements must be a component.** This applies to both newly generated page objects and extensions to existing ones.

Before generating code:

1. Analyze the HTML and count every testable element in each semantic section.
2. List every section with two or more elements as a component to create, extend, or reuse.
3. Create or complete those components before adding the page class.
4. Keep only standalone sections with fewer than two testable elements on the page class.
5. Verify every mapped HTML element is represented in the page or its components.

Examples: a header with a logo and menu button, a gallery with an image and control, and a booking summary with multiple prices/actions are components. A standalone page title can remain on the page object.

## Quick Start

```bash
cd qa

# Locate similar target page objects and components.
find src/pages src/components -name '*.ts' | sort

grep -R -n -E 'getByTestId|data-testid|locator\(' src/pages src/components

# Read the complete owners before selecting locator or method patterns.
sed -n '1,280p' src/pages/pi/searchResults.page.ts
sed -n '1,280p' src/components/pi/searchResults/searchResultsListView.component.ts

# Verify the generated page and component files.
npx tsc --noEmit
```

## Non-Negotiable Requirements

1. List every testable HTML element by semantic section before generating code.
2. Every section with two or more related testable elements must be extracted into a reusable component. Reuse an existing component only after verifying it covers the needed controls.
3. Map every testable HTML element in either the page or an owning component. Document any intentional omission and its reason.
4. Use stable selectors in this order: existing `data-testid`, accessible role/name, label, semantic attribute, then a scoped structural locator. Never use generated CSS framework classes such as `.chakra-*`, `.css-*`, `.mui-*`, or `.ant-*`.
5. Use typed `Locator` fields. Name them by element type, such as `submitButton`, `emailInput`, `pageTitleLabel`, `hotelCardsContainer`, and `footerLinks`.
6. Keep UI interaction and UI assertions inside page objects/components. Specs call named actions and validation methods, never raw locators.
7. Maintain `// ######## UI elements/properties ########`, `// ######## UI actions/navigation ########`, and `// ######## UI validations ########` sections in that order.
8. Every action and validation method starts with a concise `console.log(...)` before locator interaction or assertion. Every new class and method has concise JSDoc.
9. Every `expect(...)` includes a debug message.
10. Use Playwright auto-waiting and assertion waits. Do not introduce fixed waits, retries, conditional fallbacks, force-clicks, or legacy helper abstractions unless the application contract and existing target patterns require them.

## Workflow

### 1. Analyze HTML And Plan Ownership

Create an element mapping before editing code. Count controls by page region, including labels, containers needed for assertions, and collections.

```text
Element Mapping: Hotel Details

Header (3 elements) -> reuse or extend HeaderComponent
- logoImage
- menuButton
- signInLink

Booking summary (8 elements) -> BookingSummaryComponent
- summaryContainer
- totalCostLabel
- bookNowButton
- priceBreakdownButton
- roomItems

Gallery (2 elements) -> GalleryComponent
- heroImage
- viewAllPhotosButton

Standalone (1 element) -> page object
- pageTitleLabel

Coverage: 14 / 14 testable elements
```

An element is testable when it participates in a user journey, state verification, or an accessible page landmark. Decorative presentation-only markup does not need a locator; document why it was excluded from the mapping.

### 2. Inspect Existing Owners Before Creating Anything

- Find comparable pages/components and read their full implementation.
- Check whether a shared or page-specific component already owns the HTML section.
- Compare every required element with that component's locators, actions, and validations.
- Extend the existing component when the element is a general responsibility of that section; otherwise create a focused page-specific component.
- Mirror established source layout, file naming, imports, and export style. Do not introduce singleton page/component exports.

### 3. Create Components First

Create every multi-element section component under the nearest existing app or shared component folder before creating or extending the page object. Components receive the current Playwright page through `global.page`, unless their local pattern injects it differently.

```ts
import { type Locator, expect } from '@playwright/test';

/** Booking summary controls and details displayed alongside a hotel selection. */
export class BookingSummaryComponent {
  private readonly page = global.page;

  // ######## UI elements/properties ########

  readonly summaryContainer: Locator = this.page.getByTestId('booking-summary');
  readonly totalCostLabel: Locator = this.page.getByTestId('total-cost');
  readonly bookNowButton: Locator = this.summaryContainer.getByRole('button', { name: 'Book now' });

  // ######## UI actions/navigation ########

  /** Select the current hotel by clicking the Book now button. */
  async clickBookNowButton(): Promise<void> {
    console.log('Click Book now button');
    await this.bookNowButton.scrollIntoViewIfNeeded();
    await this.bookNowButton.click();
  }

  // ######## UI validations ########

  /** Validate that the booking summary is visible. */
  async validateSummaryDisplayed(): Promise<void> {
    console.log('Validate booking summary is displayed');
    await expect(this.summaryContainer, 'Booking summary container is visible').toBeVisible();
  }
}
```

Keep a component's locators scoped to its container where repeated controls, overlays, and loading states could otherwise create ambiguity.

### 4. Create Or Extend The Page Object

Pages extend `BasePage`; use its localized navigation support rather than direct navigation from tests. The page object owns standalone page elements and composes its section components.

```ts
import { type Locator, expect } from '@playwright/test';
import { BookingSummaryComponent } from '@components/pi/hotelDetails/bookingSummary.component';
import { BasePage } from '@pages/shared/base.page';

/** Hotel details page containing standalone title content and booking sections. */
export class HotelDetailsPage extends BasePage {
  // ######## UI elements/properties ########

  readonly url = 'hotels';
  readonly pageTitleLabel: Locator = this.page.getByRole('heading', { level: 1 });
  readonly bookingSummary: BookingSummaryComponent = new BookingSummaryComponent();

  // ######## UI actions/navigation ########

  /** Open a hotel-details route. */
  async open(): Promise<void> {
    console.log('Open hotel details page');
    await this.openLocalizedPath(this.url);
  }

  // ######## UI validations ########

  /** Validate that the hotel details page title is visible. */
  async validatePage(): Promise<void> {
    console.log('Validate hotel details page is displayed');
    await expect(this.pageTitleLabel, 'Hotel details page title is visible').toBeVisible();
  }
}
```

Do not add a redundant constructor only to call `super()`. Export the class from its nearest barrel when that is the surrounding convention, then add it to the relevant fixture only when tests need fixture injection.

## Locator Strategy

Use the narrowest stable locator appropriate to the live HTML, preserving locale safety and component scope.

```ts
// Preferred when present and stable.
readonly submitButton: Locator = this.page.getByTestId('submit-button');

// Prefer accessible semantics over text-only CSS locators.
readonly emailInput: Locator = this.page.getByLabel('Email address');
readonly closeButton: Locator = this.modalContainer.getByRole('button', { name: /close/i });

// Semantic attribute when no test id or accessible contract exists.
readonly searchInput: Locator = this.page.locator('input[name="search"]');

// Dynamic selector, scoped and typed.
hotelCardByName(hotelName: string): Locator {
  return this.hotelCardsContainer.getByTestId('hotel-card').filter({ hasText: hotelName });
}
```

Avoid generated CSS classes, broad tag selectors, raw positional selectors, untranslated visible strings, and unscoped page-wide locators for repeated UI. Use `Strings` and `Locales` for localized expectations and accessible names when target data already defines them.

## Actions, Validations, And Browser State

- An action method logs first, then uses `scrollIntoViewIfNeeded()` only when needed and the normal `locator.click()`/`fill()`/`selectOption()` action.
- A validation method logs first and uses `expect(locator, message)` for visible state, text, value, enabled/disabled state, or count.
- Use `UiUtils` only when an existing target utility expresses a complex shared validation, such as layout comparison. Keep ordinary locator assertions direct and clear.
- Use page-object navigation methods that delegate to `openLocalizedPath()`. Do not call `page.goto()` directly from a test.
- When a control is disabled, repair its journey precondition or test data. Do not force-click it.
- For menus, date pickers, and overlays, inspect the rendered accessibility roles and scope locators to the owning container. Prefer the displayed accessible date/name over raw implementation values.

```ts
/** Fill the booking email address. */
async fillEmailAddress(email: string): Promise<void> {
  console.log(`Fill email address: ${email}`);
  await this.emailInput.fill(email);
}

/** Validate that the selected room count is displayed. */
async validateRoomCount(expectedRoomCount: string): Promise<void> {
  console.log(`Validate room count: ${expectedRoomCount}`);
  await expect(this.roomCountLabel, 'Displayed room count matches the selected room count').toHaveText(expectedRoomCount);
}
```

## Element Coverage Review

Before completing the generated code, compare the mapping against the HTML section by section:

- Every button is represented by a `Button` locator or an existing component action.
- Every input is represented by an `Input` locator or an existing component action.
- Every link is represented by a `Link` locator when needed for a journey.
- Every validated text value has a `Label` or `Text` locator and owning validation method.
- Every collection is exposed as a scoped locator and queried by meaningful filters rather than hard-coded indexes.
- Every reused component was verified against the source HTML, including gaps.
- Every excluded element is decorative or otherwise explicitly documented as non-testable.

## Troubleshooting

| Symptom | Correct response |
| --- | --- |
| Generated CSS class is the only selector | Request/add a `data-testid` where possible; otherwise use stable role, label, semantic attributes, or scoped structure. |
| Selector matches multiple elements | Scope it to the component container and distinguish candidates by accessible name or stable attributes. |
| Reused component is missing elements | Extend it when the control belongs to the shared section, or create a focused component. Do not scatter its locators across an unrelated page. |
| Element is not clickable | Verify page state, overlay, or disabled preconditions; use a normal scoped locator action after the cause is addressed. |
| Test needs a UI assertion | Add a reusable page/component validation method with `expect(..., message)` rather than asserting from the spec. |
| The file compiles but fails at runtime | Run the owning focused test, inspect the live accessible markup, and correct the owner locator/action without adding retry loops or fixed waits. |

## Validation Checklist

- [ ] HTML was removed from source after analysis.
- [ ] A complete element mapping exists, including intentionally excluded decorative elements.
- [ ] Existing page/component ownership was researched before creating new classes.
- [ ] Every section with two or more related testable elements is a reusable component; only standalone sections with fewer than two elements remain on the page.
- [ ] Every locator is typed as `Locator`, scoped where appropriate, and uses a stable strategy.
- [ ] No generated framework classes, raw indexes, broad unscoped selectors, or hard-coded localized text were introduced.
- [ ] Pages extend `BasePage`; constructors and singleton exports were not added unnecessarily.
- [ ] UI elements, actions/navigation, and validations are in the correct section.
- [ ] New class and methods have JSDoc; actions and validations log before interaction/assertion.
- [ ] Every changed `expect(...)` includes a descriptive debug message.
- [ ] `cd qa && npx tsc --noEmit` passes.
- [ ] The focused Playwright test was run when a consuming journey exists, or its environmental blocker is precisely documented.
