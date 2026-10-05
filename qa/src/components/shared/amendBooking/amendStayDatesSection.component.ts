import { type Page, type Locator, expect } from '@playwright/test';
import { CalendarComponent } from '../calendar.component';
import { Strings } from '@test-data/strings';

/**
 * The amend section where stay dates can be updated, containing the UI elements, custom actions
 * and validations. Mirrors qa/reference `components/common/amendBooking/amendStayDatesSection.js`
 * (simplified: the reference's `DatePicker`/`InputElement` wrapper classes are replaced with
 * direct `Locator` fill/click - Playwright's `Locator` already provides that behavior natively).
 */
export class AmendStayDatesSectionComponent {
  private readonly page: Page = global.page;
  private readonly calendar = new CalendarComponent();

  // ######## UI elements/properties ########

  readonly sectionContainer: Locator = this.page.locator('div[data-testid="amend-stay-dates-section"]');
  readonly stayDatesHotelLabel: Locator = this.sectionContainer.locator('> div:nth-child(1) > p:nth-child(1)');
  readonly arrivalDateLabel: Locator = this.sectionContainer.locator('> div:nth-child(2) > div:nth-child(1) > p');
  readonly arrivalDateCalendarInput: Locator = this.sectionContainer.locator('div.react-datepicker__input-container input');
  readonly nightsLabel: Locator = this.sectionContainer.locator('> div:nth-child(2) > div:nth-child(2) > p');
  readonly nightsInput: Locator = this.sectionContainer.locator('input[inputmode="decimal"]');
  readonly nightsDropdown: Locator = this.sectionContainer.locator('button[data-testid="DropdownComp-amend-stay-dates-nights-menuButton"]');
  readonly nightsDropdownValue: Locator = this.nightsDropdown;
  readonly nightsInputField: Locator = this.page.locator('p[data-testid="amend-stay-dates-nights-label"] ~ input');
  readonly checkoutLabel: Locator = this.sectionContainer.locator('> div:nth-child(2) > div:nth-child(3)');
  readonly checkoutDate: Locator = this.page.locator('p[data-testid="amend-stay-dates-checkOutDate"]');
  readonly changedDatesAlertNotification: Locator = this.sectionContainer.locator('xpath=following-sibling::div[@data-testid="Alert"][2]//div[@data-testid="AlertDescription"]');
  readonly loadingSpinner: Locator = this.page.locator('[data-testid="loading-spinner"]');

  /** Return the locator for a one-based nights option. */
  getNumberOfNights(nights: number): Locator {
    return this.page.locator(`button[data-testid="DropdownComp-amend-stay-dates-nights-${nights - 1}"]`);
  }

  /** Read the displayed checkout date. */
  async getCheckoutDateValue(): Promise<string> {
    console.log('Read selected checkout date');
    await this.checkoutDate.waitFor({ state: 'visible' });
    return (await this.checkoutDate.textContent() ?? '').trim();
  }

  // ######## UI actions/navigation ########

  /** Get the current arrival date input value. */
  async getArrivalDate(): Promise<string> {
    console.log('Read selected arrival date');
    return this.arrivalDateCalendarInput.inputValue();
  }

  /** Set the number of nights via the nights input. */
  async setNights(nights: number): Promise<void> {
    console.log(`Set nights value=${nights}`);
    await this.nightsInput.fill(`${nights}`);
  }

  /**
   * Open the nights dropdown and select the given value.
   *
   * @param nights - Number of nights to select.
   */
  async selectNightsFromDropdown(nights: number): Promise<void> {
    console.log(`Select nights value=${nights}`);

    const usesDropdown = global.browser?.options?.app !== 'ccui';

    if (!usesDropdown) {
      await this.nightsInputField.waitFor({ state: 'visible', timeout: 15000 });
      await this.nightsInputField.fill(String(nights));
      await this.nightsInputField.press('Tab');
    } else {
      await this.nightsDropdown.waitFor({ state: 'visible', timeout: 15000 });
      console.log(`[AmendStayDates] Opening dropdown to select ${nights} nights`);
      await this.nightsDropdown.click();

      // Wait for menu to appear
      const menuList = this.page.locator(`[data-testid="DropdownComp-amend-stay-dates-nights-entireList"]`);
      await menuList.waitFor({ state: 'visible', timeout: 5000 });

      const nightsOption = this.page.locator(
        `button[data-testid="DropdownComp-amend-stay-dates-nights-${nights - 1}"]`
      );

      console.log(`[AmendStayDates] Looking for option with testid: DropdownComp-amend-stay-dates-nights-${nights - 1}`);
      await expect(nightsOption, `[AmendStayDates] Nights option ${nights} should be visible in dropdown menu`).toBeVisible({ timeout: 15000 });
      console.log(`[AmendStayDates] Selecting nights option ${nights}`);
      await nightsOption.focus();
      await nightsOption.press('Enter');
    }

    await this.loadingSpinner.waitFor({ state: 'visible', timeout: 4000 }).catch(() => {});
    await this.loadingSpinner.waitFor({ state: 'hidden', timeout: 30000 }).catch(() => {});

    if (usesDropdown) {
      const nightLabel = nights === 1 ? await Strings.AMEND_NIGHT.name : await Strings.AMEND_NIGHTS.name;
      await expect(
        this.nightsDropdownValue,
        `[AmendStayDates] Nights dropdown should display ${nights} ${nightLabel}`
      ).toContainText(`${nights} ${nightLabel}`);
    }
  }

  /**
   * Open the calendar and select a new arrival date, advancing months as needed.
   * Mirrors `qa/reference` `amendStayDatesSection.js` `editArrivalDate`.
   *
   * @param arrivalDate - The new arrival date to select.
   */
  async editArrivalDate(data: Date | { arrivalDate: Date; currentDate?: Date }): Promise<void> {
    const arrivalDate = data instanceof Date ? data : data.arrivalDate;
    console.log(`Edit arrival date to ${arrivalDate.toISOString().slice(0, 10)}`);
    const currentArrivalDate = data instanceof Date ? new Date(await this.getArrivalDate()) : data.currentDate ?? new Date();
    await this.arrivalDateCalendarInput.click();
    await this.calendar.selectDate(arrivalDate, currentArrivalDate);

    await this.loadingSpinner.waitFor({ state: 'visible', timeout: 4000 }).catch(() => {});
    await this.loadingSpinner.waitFor({ state: 'hidden', timeout: 30000 }).catch(() => {});
  }

  /** Calculate the checkout date using the displayed arrival date and night count. */
  async calculateExpectedCheckoutDate(numberOfNights: number): Promise<string> {
    console.log(`Calculate checkout date for ${numberOfNights} night(s)`);
    const checkoutDate = new Date(await this.getArrivalDate());
    checkoutDate.setDate(checkoutDate.getDate() + numberOfNights);
    const locale = (global.browser?.options?.locale ?? 'gb-en').toLowerCase() === 'de-de' ? 'de-DE' : 'en-GB';
    return new Intl.DateTimeFormat(locale, {
      weekday: 'short', day: '2-digit', month: 'short', year: 'numeric',
    }).format(checkoutDate).replace(',', '').replace('Sept', 'Sep').replace('Sep.', 'Sep').replace('Okt', 'Oct').replace('Dez', 'Dec');
  }

  /** Set the editable nights input and optionally move focus away from it. */
  async setInputNightsValue(numberOfNights: number, pressTab = true): Promise<void> {
    console.log(`Select nights value=${numberOfNights}`);
    await this.nightsInput.fill(String(numberOfNights));
    if (pressTab) await this.nightsInput.press('Tab');
    await this.waitForLoadingSpinner();
  }

  // ######## UI validations ########

  /** Validate the checkout date is displayed and contains the expected value. */
  async validateCheckoutDate(expectedCheckoutDate: string): Promise<void> {
    console.log(`Validate checkout date=${expectedCheckoutDate}`);
    await expect(this.checkoutDate, `[AmendStayDates] Checkout date should contain "${expectedCheckoutDate}"`).toContainText(expectedCheckoutDate);
  }

  /**
   * Validate that checkout is recalculated from the displayed arrival date and selected nights.
   *
   * @param nights - Number of nights selected in the amendment dropdown.
   */
  async validateCheckoutDateUpdate(nights: number): Promise<void> {
    console.log('Validate check out date update');
    const arrivalDate = new Date(await this.getArrivalDate());
    arrivalDate.setDate(arrivalDate.getDate() + nights);

    // Get current locale from browser options to format dates correctly
    const locale = (global.browser?.options?.locale ?? 'gb-en').toLowerCase();
    const localeFormatString = locale === 'de-de' ? 'de-DE' : 'en-GB';

    const expectedCheckoutDate = arrivalDate
      .toLocaleDateString(localeFormatString, {
        weekday: 'short',
        day: '2-digit',
        month: 'short',
        year: 'numeric',
      })
      .replace(',', '')
      .replace('Sept', 'Sep')
      .replace('Sep.', 'Sep')
      .replace('Okt', 'Oct')
      .replace('Okt.', 'Oct')
      .replace('Dez', 'Dec')
      .replace('Dez.', 'Dec');

    await this.checkoutDate.waitFor({ state: 'visible', timeout: 15000 });
    await expect
      .poll(
        async () => {
          const checkoutText = (await this.checkoutDate.textContent())?.trim() ?? '';
          console.log(`[AmendStayDates] Polling checkout date text: "${checkoutText}" (expecting to contain "${expectedCheckoutDate}")`);
          return checkoutText;
        },
        {
          timeout: 15000,
          intervals: [250, 500, 1000, 2000],
          message: `[AmendStayDates] Checkout date should update to "${expectedCheckoutDate}" for ${nights} night(s)`,
        }
      )
      .toContain(expectedCheckoutDate);
  }

  /** Validate the selected nights value in the dropdown. */
  async validateNumberOfNights(data: { expectedValue: number }): Promise<void> {
    console.log('Validate number of nights');
    const actualValue = Number.parseInt((await this.nightsDropdownValue.textContent() ?? '').split(' ')[0], 10);
    expect(actualValue, 'Nights input value').toBe(data.expectedValue);
  }

  /** Validate the editable CCUI nights value. */
  async validateInputNumberOfNights(data: { expectedValue: number }): Promise<void> {
    console.log('Validate input number of nights');
    const actualValue = Number.parseInt(await this.nightsInput.inputValue(), 10);
    expect(actualValue, 'Nights input value').toBe(data.expectedValue);
  }

  /** Validate the changed-dates alert notification is displayed with the expected text. */
  async validateChangedDatesNotification(expectedText: string): Promise<void> {
    console.log(`Validate changed dates notification contains=${expectedText}`);
    await expect(this.changedDatesAlertNotification, `[AmendStayDates] Changed dates notification should contain "${expectedText}"`).toContainText(expectedText);
  }

  /** Validate the reference amendment notification and its formatted booking values. */
  async validateChangedArrivalDateNotification(data: {
    oldArrivalDate: string | Date;
    oldNights: number;
    expectedArrivalDate: string | Date;
    expectedNights: number;
    totalCost: string;
    isDisplayed?: boolean;
  }): Promise<void> {
    console.log('Validate amend staying dated notification');
    const isDisplayed = data.isDisplayed ?? true;
    if (!isDisplayed) {
      await expect(this.changedDatesAlertNotification, 'Stay Dates amend notification alert').toBeHidden();
      return;
    }
    const oldNightsLabel = data.oldNights > 1 ? await Strings.AMEND_NIGHTS.name : await Strings.AMEND_NIGHT.name;
    const newNightsLabel = data.expectedNights > 1 ? await Strings.AMEND_NIGHTS.name : await Strings.AMEND_NIGHT.name;
    const formatDate = (value: string | Date): string => {
      const date = value instanceof Date ? value : new Date(value);
      return date.toISOString().slice(0, 10);
    };
    const notificationMessage = (await Strings.YOU_HAVE_CHANGED_YOUR_BOOKING.name)
      .replace('{arrivalDateOld}', formatDate(data.oldArrivalDate))
      .replace('{numNightOld}', `${data.oldNights} ${oldNightsLabel}`)
      .replace('{arrivalDateNew}', formatDate(data.expectedArrivalDate))
      .replace('{numNightNew}', `${data.expectedNights} ${newNightsLabel}`)
      .replace('{newTotal}', data.totalCost);
    await expect(this.changedDatesAlertNotification, 'Stay Dates amend notification alert').toContainText(notificationMessage);
  }

  private async waitForLoadingSpinner(): Promise<void> {
    await this.loadingSpinner.waitFor({ state: 'visible', timeout: 4000 }).catch(() => {});
    await this.loadingSpinner.waitFor({ state: 'hidden', timeout: 30000 }).catch(() => {});
  }
}
