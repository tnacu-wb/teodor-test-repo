import { type Page, type Locator, expect } from '@playwright/test';
import { getCurrentLocale, Locales } from '../../test-data/locales';

/**
 * Calendar sub-component (react-datepicker, PI / non-IB variant) shared by the search console
 * and amend-booking stay-dates flows. Mirrors qa/reference
 * `components/common/searchConsole/datePickerBase.js` (simplified: the reference's single
 * `DatePicker` class owns both the triggering input and the popup calendar - here the popup-only
 * elements/actions live in `CalendarComponent`, and each caller's own input locator - e.g.
 * `SearchConsoleComponent.datesButton`, `AmendStayDatesSectionComponent.arrivalDateCalendarInput` -
 * is clicked by the caller to open it).
 */
export class CalendarComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly monthContainer: Locator = this.page.locator('[data-testid="IB-Date-Picker-Calendar"], .react-datepicker__month-container').first();
  readonly nextMonthButton: Locator = this.page.locator('button[aria-label="Go to next month"]:visible, button[aria-label="Next Month"]:visible').first();
  readonly previousMonthButton: Locator = this.page.locator('button[aria-label="Go to previous month"]:visible, button[aria-label="Previous Month"]:visible').first();
  readonly currentMonthLabel: Locator = this.page.locator('.react-datepicker__current-month:visible, [data-testid="date-picker-day-date-picker-header"]:visible, [id="react-day-picker-1"]:visible').first();
  readonly weekDayLabels: Locator = this.page.locator('[data-testid="IB-Date-Picker-Calendar"] tr > th, .react-datepicker__day-names > div');
  readonly resetButton: Locator = this.page.locator('[data-testid="IB-Date-Picker-Calendar-buttons-Reset"]');
  readonly doneButton: Locator = this.page.locator('[data-testid="IB-Date-Picker-Calendar-buttons-Done"]');

  // ######## UI actions/navigation ########

  /**
   * Locator for an enabled day option using the localized month name and
   * react-datepicker's day class, matching the legacy DatePickerBase selector.
   */
  dayOption(date: Date): Locator {
    const localeTag = getCurrentLocale().name === Locales.DE_DE.name ? 'de-DE' : 'en-GB';
    const monthName = date.toLocaleString(localeTag, { month: 'long' });
    const day = String(date.getDate()).padStart(2, '0');
    const year = date.getFullYear();
    const dayClass = `react-datepicker__day--0${day}`;
    const datePickerId = `date-picker-day-${day}${date.getMonth() + 1}${year}`;

    return this.monthContainer.locator(
      `[aria-label*="${monthName}"][class*="${dayClass}"]:not(.react-datepicker__day--outside-month):not([aria-disabled="true"]):not([disabled]), button#${datePickerId}:not([aria-disabled="true"]):not([disabled])`
    );
  }

  /** Return an IB calendar day cell matching the displayed day number. */
  private ibDayOption(date: Date): Locator {
    return this.page.locator('[data-testid="IB-Date-Picker-Calendar"]:visible tr td')
      .filter({ hasText: new RegExp(`^${date.getDate()}$`) })
      .first();
  }

  /** Click the next-month button and wait for the displayed month to change. */
  async clickNextMonth(): Promise<void> {
    console.log('Click calendar next month button');
    const currentMonth = await this.currentMonthLabel.textContent().catch(() => null);
    await this.nextMonthButton.click();
    if (currentMonth) {
      await expect(
        this.currentMonthLabel,
        `Calendar month should change from "${currentMonth}" after clicking next month`
      ).not.toHaveText(currentMonth);
    }
  }

  /** Click the previous-month button and wait for the displayed month to change. */
  async clickPreviousMonth(): Promise<void> {
    console.log('Click calendar previous month button');
    const currentMonth = await this.currentMonthLabel.textContent().catch(() => null);
    await this.previousMonthButton.click();
    if (currentMonth) {
      await expect(
        this.currentMonthLabel,
        `Calendar month should change from "${currentMonth}" after clicking previous month`
      ).not.toHaveText(currentMonth);
    }
  }

  /** Click the calendar Done button and wait for it to close. */
  async clickDone(): Promise<void> {
    console.log('Click calendar Done button');
    await this.doneButton.click();
    await this.doneButton.waitFor({ state: 'hidden' }).catch(() => {});
  }

  /** Click the calendar Reset button and wait for it to close. */
  async clickReset(): Promise<void> {
    console.log('Click calendar Reset button');
    await this.resetButton.click();
    await this.resetButton.waitFor({ state: 'hidden' }).catch(() => {});
  }

  /**
   * Navigate the calendar from `currentDate`'s displayed month to `targetDate`'s month.
   * Mirrors `qa/reference` `datePickerBase.js` `goToDate`.
   */
  async goToDate(targetDate: Date, currentDate?: Date): Promise<void> {
    const resolvedCurrentDate = currentDate ?? await this.getDisplayedMonthDate() ?? new Date();
    console.log(`Go to date=${targetDate.toISOString().slice(0, 10)} from current=${resolvedCurrentDate.toISOString().slice(0, 10)}`);
    const monthsDiff = (targetDate.getFullYear() - resolvedCurrentDate.getFullYear()) * 12
      + (targetDate.getMonth() - resolvedCurrentDate.getMonth());

    for (let i = 0; i < Math.abs(monthsDiff); i++) {
      if (monthsDiff > 0) {
        await this.clickNextMonth();
      } else {
        await this.clickPreviousMonth();
      }
    }
  }

  /**
   * Navigate to and select the given date. Uses `goToDate` as the primary jump (mirrors
   * `qa/reference` `datePickerBase.js` `selectArrivalDate`), then retries with next-month clicks
   * if the day isn't found - hardens callers that don't reliably know the calendar's current
   * displayed month (e.g. selecting a second date after the first shifted the visible month).
   */
  async selectDate(targetDate: Date, currentDate?: Date): Promise<void> {
    console.log(`Select date=${targetDate.toISOString().slice(0, 10)}`);
    await this.goToDate(targetDate, currentDate);
    const ibCalendar = this.page.locator('[data-testid="IB-Date-Picker-Calendar"]:visible');
    if (await ibCalendar.isVisible().catch(() => false)) {
      await expect(this.ibDayOption(targetDate), `IB calendar day ${targetDate.getDate()} should be selectable`).toBeVisible();
      await this.ibDayOption(targetDate).click();
      return;
    }
    const dayOption = this.dayOption(targetDate);

    for (let attempt = 0; attempt < 12; attempt++) {
      if (await dayOption.isVisible().catch(() => false)) {
        await dayOption.click();
        return;
      }
      await this.clickNextMonth();
    }

    throw new Error(`Could not find date ${targetDate.toISOString().slice(0, 10)} in calendar after 12 month advances`);
  }

  private async getDisplayedMonthDate(): Promise<Date | null> {
    const label = (await this.currentMonthLabel.textContent().catch(() => null))?.trim();
    if (!label) {
      return null;
    }

    const year = Number(label.match(/\d{4}/)?.[0]);
    if (!year) {
      return null;
    }

    const localeTag = getCurrentLocale().name === Locales.DE_DE.name ? 'de-DE' : 'en-GB';
    const lowerLabel = label.toLocaleLowerCase(localeTag);
    for (let monthIndex = 0; monthIndex < 12; monthIndex++) {
      const monthName = new Date(year, monthIndex, 1).toLocaleString(localeTag, { month: 'long' }).toLocaleLowerCase(localeTag);
      if (lowerLabel.includes(monthName)) {
        return new Date(year, monthIndex, 1);
      }
    }

    return null;
  }

  /** Whether the next-month button is currently displayed. */
  async isNextMonthButtonDisplayed(): Promise<boolean> {
    console.log('Check if calendar next month button is displayed');
    return this.nextMonthButton.isVisible();
  }

  // ######## UI validations ########

  /** Validate the calendar header shows the current month and year. */
  async validateCurrentMonthAndYear(): Promise<void> {
    console.log('Validate calendar current month and year');
    const localeTag = getCurrentLocale().name === Locales.DE_DE.name ? 'de-DE' : 'en-GB';
    const expectedMonthYear = new Date().toLocaleDateString(localeTag, { month: 'long', year: 'numeric' });
    await expect(this.currentMonthLabel, `Calendar should display current month and year "${expectedMonthYear}"`).toHaveText(expectedMonthYear);
  }

  /** Validate whether the given date is disabled (not selectable) in the calendar. */
  async validateDayDisabled(date: Date, disabled = true): Promise<void> {
    console.log(`Validate date=${date.toISOString().slice(0, 10)} disabled=${disabled}`);
    const description = `Date ${date.toISOString().slice(0, 10)} disabled state`;
    if (disabled) {
      await expect(this.dayOption(date), description).toHaveCount(0);
    } else {
      await expect(this.dayOption(date), description).toHaveCount(1);
    }
  }

  /** Validate whether the next-month button is displayed. */
  async validateNextMonthButtonDisplayed(isDisplayed = true): Promise<void> {
    console.log(`Validate calendar next month button isDisplayed=${isDisplayed}`);
    if (isDisplayed) {
      await expect(this.nextMonthButton, 'Calendar next month button should be displayed').toBeVisible();
    } else {
      await expect(this.nextMonthButton, 'Calendar next month button should not be displayed').not.toBeVisible();
    }
  }

  /** Validate whether the previous-month button is displayed. */
  async validatePreviousMonthButtonDisplayed(isDisplayed = true): Promise<void> {
    console.log(`Validate calendar previous month button isDisplayed=${isDisplayed}`);
    if (isDisplayed) {
      await expect(this.previousMonthButton, 'Calendar previous month button should be displayed').toBeVisible();
    } else {
      await expect(this.previousMonthButton, 'Calendar previous month button should not be displayed').not.toBeVisible();
    }
  }
}
