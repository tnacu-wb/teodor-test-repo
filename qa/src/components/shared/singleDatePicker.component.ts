import { type Page, type Locator } from '@playwright/test';

/**
 * Generic react-datepicker (https://reactdatepicker.com/) helper for selecting a single date,
 * containing the UI elements, custom actions and validations. Mirrors qa/reference
 * `components/common/singleDatePicker.js` (PI/generic branch only - the IB-specific
 * `IB-Date-Picker-Calendar` branch is out of scope for this PI-only target; simplified: the
 * reference's `SelectElement`-wrapped month/year `<select>` dropdowns are exposed as plain
 * `Locator`s since Playwright's `selectOption()` already covers that natively).
 */
export class SingleDatePickerComponent {
  private readonly page: Page = global.page;
  readonly element: Locator;

  constructor(selector: string) {
    this.element = this.page.locator(selector);
  }

  // ######## UI elements/properties ########

  readonly calendarMonthContainer: Locator = this.page.locator(
    'div.react-datepicker__month-container, div[class*="datepicker__header"]'
  );
  readonly calendarNextMonthButton: Locator = this.page.locator(
    'button[aria-label="Next Month"], button[class*="navigation-btn-right"] span'
  );
  readonly calendarPreviousMonthButton: Locator = this.page.locator(
    'button[aria-label="Previous Month"], button[class*="navigation-btn-left"] span'
  );
  readonly calendarCurrentMonthLabel: Locator = this.page.locator(
    '.react-datepicker__current-month, div[class*="datepicker__header"]'
  );
  readonly monthDropdown: Locator = this.page.locator('select').first();
  readonly yearDropdown: Locator = this.page.locator('select').nth(1);
  readonly calendarDoneButton: Locator = this.page.locator('div.react-datepicker div button[type="button"]').nth(1);

  // ######## UI actions/navigation ########

  /** Click the date picker input to open the calendar. */
  async click(): Promise<void> {
    await this.element.click();
  }

  /** Navigate to the next month. */
  async clickNextMonth(): Promise<void> {
    await this.calendarNextMonthButton.click();
  }

  /** Navigate to the previous month. */
  async clickPreviousMonth(): Promise<void> {
    await this.calendarPreviousMonthButton.click();
  }

  /** Select a specific day by its accessible name (e.g. "2 September 2026"). */
  async selectDate({ selectedDate }: { selectedDate: string }): Promise<void> {
    await this.page.getByRole('option', { name: selectedDate }).click();
  }

  // ######## UI validations ########
}
