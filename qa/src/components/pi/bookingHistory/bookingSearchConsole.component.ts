import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';

/**
 * The booking search console section on the PI Booking History page containing the UI elements,
 * custom actions and validations. Mirrors qa/reference
 * `components/opera/bookingHistory/bookingSearchConsole.js` (simplified: reference wraps a
 * `SingleDatePicker` helper class not yet ported to the target - date entry uses direct
 * `fill()`/click on the date input instead).
 */
export class BookingSearchConsoleSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly datePickerInput: Locator = this.page.locator('input[aria-label="datepicker-input"][data-testid="SingleDatePicker"]');
  readonly calendarModal: Locator = this.page.locator('div.react-datepicker');
  readonly bookingReferenceOrSurnameInput: Locator = this.page.locator('input[data-testid="input-bookingFilter"]');
  readonly findButton: Locator = this.page.locator('button[data-testid="MyDashboard-findButton"]');
  readonly clearSearchButton: Locator = this.page.locator('span[data-testid="MyDashboard-clearButton"]');
  readonly searchDescriptionLabel: Locator = this.page.locator('h6[data-testid="filters-description"]');
  readonly loadMoreButton: Locator = this.page.locator('button[data-testid="MyDashboard-Table-LoadMore"]');
  readonly errorMessageLabel: Locator = this.page.locator('div[data-testid="input-bookingFilter-FormErrorMessage"]');

  // ######## UI actions/navigation ########

  /** Set the booking reference or surname filter. */
  async setInputFilter(value: string): Promise<void> {
    console.log(`Set booking history filter=${value}`);
    await this.bookingReferenceOrSurnameInput.waitFor({ state: 'visible', timeout: browser.options.actionTimeout });
    await this.bookingReferenceOrSurnameInput.fill(value);
  }

  /** Click the booking-history Find button and wait for the result list to settle. */
  async clickFindButton(): Promise<void> {
    console.log('Click booking history Find button');
    await this.findButton.scrollIntoViewIfNeeded();
    await this.findButton.click();
    await this.page.waitForTimeout(4000);
  }

  /** Clear the booking-history filter. */
  async clickClearSearchButton({ waitForDisplayed = true }: { waitForDisplayed?: boolean } = {}): Promise<void> {
    console.log('Clear booking history search');
    await this.clearSearchButton.click();
    if (waitForDisplayed) await this.loadMoreButton.waitFor({ state: 'visible' });
  }

  /** Click Load More until all booking rows are displayed. */
  async clickLoadMoreUntilAllBookingsDisplayed(): Promise<void> {
    console.log('Load all booking history rows');
    while (await this.loadMoreButton.isVisible() && await this.loadMoreButton.isEnabled()) {
      await this.loadMoreButton.click();
    }
  }

  /** Click Load More once. */
  async clickLoadMoreButton(): Promise<void> {
    console.log('Click booking history Load More button');
    await this.loadMoreButton.click();
  }

  /** Click the date picker input field. */
  async clickDatePickerInputField(): Promise<void> {
    await this.datePickerInput.click();
  }

  /** Select a date for filtering (ISO format, e.g. "2026-09-02"). */
  async selectDate(date: string): Promise<void> {
    await this.clickDatePickerInputField();
    await this.datePickerInput.fill(date);
  }

  // ######## UI validations ########

  /** Validate the date picker input placeholder text. */
  async validateDatePickerInputPlaceholder(): Promise<void> {
    await expect(this.datePickerInput, 'Booking reference or surname input placeholder').toHaveAttribute(
      'placeholder',
      await Strings.DATE_PICKER_INPUT_PLACEHOLDER.name
    );
  }

  /** Validate the calendar modal visibility. */
  async validateCalendarModalIsDisplayed(isDisplayed: boolean): Promise<void> {
    if (isDisplayed) {
      await expect(this.calendarModal, 'Calendar Modal').toBeVisible();
    } else {
      await expect(this.calendarModal, 'Calendar Modal').not.toBeVisible();
    }
  }

  /** Validate the date filter input is enabled/disabled. */
  async validateDateFilterIsEnabled(isEnabled: boolean): Promise<void> {
    if (isEnabled) {
      await expect(this.datePickerInput, 'Date picker element should be enabled').toBeEnabled();
    } else {
      await expect(this.datePickerInput, 'Date picker element should be disabled').toBeDisabled();
    }
  }

  /** Validate the booking reference or surname input placeholder. */
  async validateBookingReferenceOrSurnameInputPlaceholder(): Promise<void> {
    console.log('Validate booking reference or surname input placeholder');
    await expect(this.bookingReferenceOrSurnameInput, 'Booking reference or surname input placeholder').toHaveAttribute('placeholder', await Strings.BOOKING_REFERENCE_INPUT_PLACEHOLDER.name);
  }

  /** Validate the Find button and its label. */
  async validateFindButton(): Promise<void> {
    console.log('Validate booking history Find button');
    await expect(this.findButton, 'Find button').toBeVisible();
    await expect(this.findButton, 'Find button label').toHaveText(await Strings.FIND_BUTTON.name);
  }

  /** Validate the Clear search button and its label. */
  async validateClearSearchButton(): Promise<void> {
    console.log('Validate booking history Clear search button');
    await expect(this.clearSearchButton, 'Clear search button').toBeVisible();
    await expect(this.clearSearchButton, 'Clear search button label').toHaveText(await Strings.CLEAR_SEARCH_BUTTON.name);
  }

  /** Validate the booking-history filter input state. */
  async validateBookingReferenceOrSurnameInputIsEnabled(isEnabled: boolean): Promise<void> {
    console.log(`Validate booking history filter enabled=${isEnabled}`);
    if (isEnabled) await expect(this.bookingReferenceOrSurnameInput, 'Booking reference or surname input').toBeEnabled();
    else await expect(this.bookingReferenceOrSurnameInput, 'Booking reference or surname input').toBeDisabled();
  }

  /** Validate the filter error message. */
  async validateErrorMessage(expectedErrorMessage: string): Promise<void> {
    console.log(`Validate booking history filter error=${expectedErrorMessage}`);
    await expect(this.errorMessageLabel, 'Error message label').toContainText(expectedErrorMessage);
  }

  /** Validate the filter error message display state. */
  async validateErrorMessageIsDisplayed(isDisplayed: boolean): Promise<void> {
    console.log(`Validate booking history filter error displayed=${isDisplayed}`);
    if (isDisplayed) await expect(this.errorMessageLabel, 'Error message label').toBeVisible();
    else await expect(this.errorMessageLabel, 'Error message label').toBeHidden();
  }

  /** Validate the booking-history search description. */
  async validateSearchDescriptionText(): Promise<void> {
    console.log('Validate booking history search description');
    await expect(this.searchDescriptionLabel, 'Search console info label').toBeVisible();
    await expect(this.searchDescriptionLabel, 'Search console info label text').toContainText(await Strings.BOOKING_SEARCH_DESCRIPTION.name);
  }

  /** Validate the booking-history filtering controls display state. */
  async validateFilteringFieldIsDisplayed(isDisplayed: boolean): Promise<void> {
    console.log(`Validate booking history filtering fields displayed=${isDisplayed}`);
    const controls = [this.bookingReferenceOrSurnameInput, this.datePickerInput, this.findButton, this.clearSearchButton];
    for (const control of controls) {
      if (isDisplayed) await expect(control, 'Booking history filtering control').toBeVisible();
      else await expect(control, 'Booking history filtering control').toBeHidden();
    }
  }
}
