import { type Page, type Locator, expect } from '@playwright/test';
import { BrowserUtils } from '../../../utils/browserUtils';
import { Strings } from '../../../test-data/strings';
import { getCurrentLocale, Locales } from '../../../test-data/locales';

/**
 * Manage booking menu modal, opened clicking Manage booking menu.
 * Mirrors qa/reference test/pages/components/opera/header/manageBookingModal.js.
 */
export class ManageBookingModalComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly modalContent: Locator = this.page.locator('[data-testid="ManageBookingModal-ModalContent"]');
  readonly bookingTitle: Locator = this.page.locator('[data-testid="ManageBookingModal_Title"]');
  readonly bookingDescription: Locator = this.page.locator('[data-testid="ManageBookingModal_Description"]');
  readonly closeButton: Locator = this.page.locator('[data-testid="ManageBookingModal-ModalCloseButton"]');
  readonly manageBookingModalBody: Locator = this.page.locator('[data-testid="ManageBookingModal-ModalBody"]');
  readonly bookingReferenceInput: Locator = this.page.locator('input[data-testid="input-bookingReference"], #booking-reference-input');
  readonly bookingSurnameInput: Locator = this.page.locator('input[data-testid="input-bookingSurname"], #booking-surname-input');
  readonly arrivalDateTopText: Locator = this.page.locator('[data-testid="arrivalDate-label"]');
  readonly arrivalDatePicker: Locator = this.page.locator('input[data-testid="SingleDatePicker"], [class^="find-booking-date-picker"]');
  readonly calendarNextMonthButton: Locator = this.page.locator('button[aria-label="Go to next month"], button[aria-label="Next Month"]');
  readonly searchButton: Locator = this.page.locator('[data-testid="ManageBookingModal-Search"], #find-booking-form-button');
  readonly invalidBookingAlertDescriptionLabel: Locator = this.page.locator('[data-testid="AlertDescription"]');

  /**
   * Locator for the invalid booking reference validation message.
   */
  async invalidReference(): Promise<Locator> {
    return this.page.locator(`[description="${await Strings.INVALID_REFERENCE.name}"]`);
  }

  /**
   * Locator for the invalid booking surname validation message.
   */
  async invalidSurname(): Promise<Locator> {
    return this.page.locator(`[description="${await Strings.MUST_BE_AT_LEAST_2_CHARACTERS_LONG.name}"]`);
  }

  // ######## UI actions/navigation ########

  /**
   * Search for a booking using the booking reference, surname and arrival date.
   * @param data - the booking information needed to search a booking
   * @param data.bookingReference - the basket reference
   * @param data.bookingSurname - the booking surname
   * @param data.arrivalDate - the booking arrival date (ISO format YYYY-MM-DD)
   */
  async searchBooking({ bookingReference, bookingSurname, arrivalDate }: {
    bookingReference: string;
    bookingSurname: string;
    arrivalDate: string;
  }): Promise<void> {
    console.log(`Search booking with information: bookingReference = ${bookingReference}, bookingSurname = ${bookingSurname}, arrivalDate = ${arrivalDate}`);
    await this.bookingReferenceInput.waitFor({ state: 'visible' });
    await this.bookingReferenceInput.fill(bookingReference);
    await this.bookingSurnameInput.fill(bookingSurname);
    await this.arrivalDatePicker.click();
    await this.selectArrivalDate(arrivalDate);
    await this.closeDatePickerByClickingOutside();

    const httpAuthUsername = global.browser?.options?.httpAuthUsername;
    if (httpAuthUsername) {
      await this.page.context().setHTTPCredentials({
        username: httpAuthUsername,
        password: global.browser?.options?.httpAuthPassword ?? '',
      });
    }

    await this.searchButton.click();
    await this.searchButton.waitFor({ state: 'detached', timeout: 60000 });
    await this.page.goto(BrowserUtils.getCurrentPageURLWithoutUsernameAndPassword(this.page), {
      waitUntil: 'domcontentloaded',
    });
  }

  /**
   * Click X button from Manage Booking Modal.
   */
  async clickCloseManageBookingModal(): Promise<void> {
    console.log('Close Manage Booking modal');
    await this.closeButton.scrollIntoViewIfNeeded();
    await this.closeButton.click();
  }

  /**
   * Click arrival date field.
   */
  async clickArrivalDateField(): Promise<void> {
    console.log('Open Manage Booking arrival date picker');
    await this.arrivalDatePicker.click();
  }

  /**
   * Close the date picker by clicking outside of it.
   */
  private async closeDatePickerByClickingOutside(): Promise<void> {
    console.log('Close date picker by clicking outside of it');
    await this.manageBookingModalBody.click({ position: { x: 140, y: 0 } });
  }

  /**
   * Select the arrival date from the calendar, advancing to the required month first.
   * Mirrors qa/reference datePickerBase.js goToDate + selectArrivalDate (react-datepicker).
   * @param arrivalDate - the arrival date to select (ISO format YYYY-MM-DD)
   */
  private async selectArrivalDate(arrivalDate: string): Promise<void> {
    console.log(`Select arrival date=${arrivalDate}`);
    const date = new Date(arrivalDate);
    const localeTag = getCurrentLocale().name === Locales.DE_DE.name ? 'de-DE' : 'en-GB';
    const monthName = date.toLocaleString(localeTag, { month: 'long' });
    const day = date.getDate();
    const year = date.getFullYear();
    const pattern = new RegExp(`(${monthName} ${day}(st|nd|rd|th)?,? ${year}|${day}\\.? ${monthName} ${year})`, 'i');
    const dayOption = this.page.getByRole('gridcell', { name: pattern });

    for (let attempt = 0; attempt < 12; attempt++) {
      try {
        await dayOption.waitFor({ state: 'visible', timeout: 1000 });
        await dayOption.click();
        return;
      } catch {
        await this.calendarNextMonthButton.click();
      }
    }

    // Fall back to filling the input directly if the day option could not be found in the calendar
    await this.arrivalDatePicker.fill(arrivalDate);
  }

  /**
   * Select the arrival date of a booking from the Search Booking Modal, re-opening the picker afterwards
   * to match the reference's click-select-click flow.
   * @param arrivalDate - the booking arrival date (ISO format YYYY-MM-DD)
   */
  async selectBookingArrivalDate(arrivalDate: string): Promise<void> {
    console.log(`Select Manage Booking arrival date=${arrivalDate}`);
    await this.arrivalDatePicker.click();
    await this.selectArrivalDate(arrivalDate);
    await this.arrivalDatePicker.click();
  }

  // ######## UI validations ########

  /**
   * Validate the modal is displayed (or not displayed).
   */
  async validateVisible(isDisplayed = true): Promise<void> {
    console.log(`Validate Manage Booking modal is ${isDisplayed ? 'visible' : 'not visible'}`);
    if (isDisplayed) {
      await expect(this.modalContent, 'Manage Booking modal content should be visible').toBeVisible({ timeout: 10000 });
      await expect(this.bookingTitle, 'Manage Booking modal title should be visible').toBeVisible();
    } else {
      await expect(this.modalContent, 'Manage Booking modal content should not be visible').not.toBeVisible({ timeout: 5000 });
    }
  }

  /**
   * Validate Manage Booking Modal fields comparing expected text with the FE fields.
   */
  async validateManageBookingModalFields(): Promise<void> {
    console.log('Validate Manage Booking modal fields');
    await expect(this.bookingTitle, 'Manage Booking Modal Title should be visible').toBeVisible();
    await expect(this.bookingDescription, 'Manage Booking Modal Description should be visible').toBeVisible();
    await expect(this.bookingReferenceInput, 'Manage Booking Modal Booking Reference should be visible').toBeVisible();
    await expect(this.bookingSurnameInput, 'Manage Booking Modal Booking Surname should be visible').toBeVisible();
    await expect(this.arrivalDatePicker, 'Manage Booking Modal Arrival Date Picker should be visible').toBeVisible();
    await expect(this.searchButton, 'Manage Booking Modal Search Button should be visible').toBeVisible();

    await expect(this.bookingTitle, 'Manage Booking Modal Title').toHaveText(await Strings.MANAGE_BOOKING_MODAL_TITLE.name);
    await expect(this.bookingDescription, 'Booking Description').toHaveText(await Strings.BOOKING_DESCRIPTION.name);
    await expect(this.arrivalDateTopText, 'Booking Arrival Date Title').toHaveText(await Strings.ARRIVAL_DATE_GENERAL.name);
    await expect(this.searchButton, 'Booking search button').toHaveText(await Strings.SEARCH_GENERIC.name);
    await expect(this.bookingReferenceInput, 'Booking Reference watermark doesn\'t match').toHaveAttribute('placeholder', await Strings.BOOKING_REFERENCE_LABEL.name);
    await expect(this.bookingSurnameInput, 'Booking Surname watermark doesn\'t match').toHaveAttribute('placeholder', await Strings.BOOKING_SURNAME_LABEL.name);
    await expect(this.arrivalDatePicker, 'Booking Arrival Date is not correct').toHaveValue(await Strings.TODAY_GENERIC.name);

    await this.bookingReferenceInput.click();
    await this.bookingSurnameInput.click();
    await this.bookingReferenceInput.click();
    await expect(await this.invalidReference(), 'Booking invalid reference').toHaveText(await Strings.INVALID_REFERENCE.name);
    await expect(await this.invalidSurname(), 'Booking Surname').toHaveText(await Strings.MUST_BE_AT_LEAST_2_CHARACTERS_LONG.name);
  }

  /**
   * Validate if the arrival date field contains the correct labels.
   * @param expectedTitle - expected arrival date title text
   * @param expectedValue - expected arrival date field value
   */
  async validateArrivalDateFieldLabels(expectedTitle: string, expectedValue: string): Promise<void> {
    console.log(`Validate arrival date labels title=${expectedTitle} value=${expectedValue}`);
    await expect(this.arrivalDateTopText, 'Booking Arrival Date Title').toHaveText(expectedTitle);
    await expect(this.arrivalDatePicker, 'Booking Arrival Date is not correct').toHaveValue(expectedValue);
  }

  /**
   * Validate the correct error message for invalid booking info.
   */
  async validateInvalidBookingErrorMessage(): Promise<void> {
    console.log('Validate invalid booking error message');
    await expect(this.invalidBookingAlertDescriptionLabel, 'Invalid booking error message').toHaveText(await Strings.INVALID_BOOKING.name);
    await expect(this.searchButton, 'Booking search button').toHaveText(await Strings.SEARCH_GENERIC.name);
  }
}
