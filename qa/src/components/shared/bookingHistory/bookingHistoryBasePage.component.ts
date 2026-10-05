import { type Page, type Locator, expect } from '@playwright/test';
import { Constants } from '@test-data/constants';
import { Strings } from '@test-data/strings';
import { BookingHistoryCardComponent } from '../../pi/bookingHistory/bookingHistoryCard.component';
import { BookingHistoryTableComponent } from '../../pi/bookingHistory/bookingHistoryTable.component';
import { BookingSearchConsoleSectionComponent } from '../../pi/bookingHistory/bookingSearchConsole.component';

/**
 * Booking History Base Page containing the shared UI, actions, and validations.
 */
export class BookingHistoryBasePageComponent {
  private readonly page: Page = global.page;

  readonly bookingHistoryCard: BookingHistoryCardComponent = new BookingHistoryCardComponent();
  readonly bookingHistoryTable: BookingHistoryTableComponent = new BookingHistoryTableComponent();
  readonly bookingSearchConsole: BookingSearchConsoleSectionComponent = new BookingSearchConsoleSectionComponent();

  // ######## UI elements/properties ########

  readonly bookingHistoryTitle: Locator = this.page.locator('[data-testid="BookingHistory-Title"], h1').last();
  readonly emptyBookingsAlertLabel: Locator = this.page.locator('div[data-testid="AlertDescription"]');
  readonly noBookingsFoundAlertLabel: Locator = this.page.locator('div[data-testid="AlertDescription"] p').nth(0);
  readonly pleaseTryAgainAlertLabel: Locator = this.page.locator('div[data-testid="AlertDescription"] p').nth(1);
  readonly upcomingSummaryLabel: Locator = this.page.locator('[data-testid="textstats-element-upcoming"] span').nth(1);
  readonly checkedInSummaryLabel: Locator = this.page.locator('[data-testid="textstats-element-checkedin"] span');
  readonly pastSummaryLabel: Locator = this.page.locator('[data-testid="textstats-element-past"] span').nth(1);
  readonly cancelledSummaryLabel: Locator = this.page.locator('[data-testid="textstats-element-cancelled"] span').nth(1);
  readonly bookingInfoCardStatusLabel: Locator = this.page.locator('tr[data-testid*="MyDashboard-Table-Row"] td:nth-child(6) span');
  readonly bookingsRowsList: Locator = this.page.locator('tr[data-testid*="MyDashboard-Table-Row"]:not([data-testid*="-expanded"])');
  readonly bookingsSummary: Locator = this.page.locator('[data-testid="textstats-summary"]');

  // ######## UI actions/navigation ########

  /** Check whether a booking information card is displayed. */
  async checkIfBookingInfoCardIsDisplayed(): Promise<boolean> {
    console.log('Check if booking information card is displayed');
    return this.bookingHistoryCard.bookingInfoCardContainer.isVisible();
  }

  /** Check whether the displayed booking has cancelled status. */
  async checkIfBookingIsCancelled(): Promise<boolean> {
    console.log('Check if booking is cancelled');
    if (Constants.BROWSER_RESOLUTIONS.isMobilePhone() && !(await this.bookingInfoCardStatusLabel.isVisible())) {
      await this.page.setViewportSize({ width: 768, height: 1024 });
      return false;
    }

    await this.page.setViewportSize({
      width: Constants.BROWSER_RESOLUTIONS.getCurrentResolutionWidth(),
      height: Constants.BROWSER_RESOLUTIONS.getCurrentResolutionHeight(),
    });
    return (await this.bookingInfoCardStatusLabel.textContent() ?? '').includes(await Strings.BOOKING_CANCELLED_LABEL.name);
  }

  /** Search for a booking by reference or surname. */
  async searchBooking({ bookingReference, waitForBooking = true }: { bookingReference: string; waitForBooking?: boolean }): Promise<void> {
    console.log(`Search booking history for ${bookingReference}`);
    await this.bookingSearchConsole.setInputFilter(bookingReference);
    if (waitForBooking) await this.page.waitForTimeout(180000);
    await this.bookingSearchConsole.clickFindButton();
  }

  /** Poll for a booking while it propagates to CDH. */
  async findBooking(bookingReferenceID: string, { checkIsCancelled = false, fail = true }: { checkIsCancelled?: boolean; fail?: boolean } = {}): Promise<void> {
    console.log('Wait for 10 minutes for the booking to appear.');
    const waitTimeout = 600000;
    const stepTime = 10000;
    const maxWaitTime = Date.now() + waitTimeout;

    while (true) {
      await this.validatePage();
      let bookingIsFound = false;
      if (!await this.bookingSearchConsole.bookingReferenceOrSurnameInput.isVisible()) {
        await this.page.reload({ waitUntil: 'domcontentloaded' });
        await this.validatePage();
        await this.bookingSearchConsole.setInputFilter(bookingReferenceID);
        await this.bookingSearchConsole.clickFindButton();
        bookingIsFound = await this.checkIfBookingInfoCardIsDisplayed();
      } else {
        await this.bookingSearchConsole.setInputFilter(bookingReferenceID);
        await this.bookingSearchConsole.clickFindButton();
        bookingIsFound = await this.checkIfBookingInfoCardIsDisplayed();
      }

      const bookingIsCancelled = bookingIsFound && checkIsCancelled ? await this.checkIfBookingIsCancelled() : false;
      if (bookingIsFound && (!checkIsCancelled || bookingIsCancelled === checkIsCancelled)) {
        console.log('Booking found!');
        return;
      }

      if (Date.now() > maxWaitTime) {
        if (fail) throw new Error('Wait timeout finding the booking!');
        console.log('Wait timeout finding the booking!');
        return;
      }

      console.log('Retrying after refresh...');
      await this.page.waitForTimeout(stepTime);
      await this.page.reload({ waitUntil: 'domcontentloaded' });
    }
  }

  // ######## UI validations ########

  /** Validate that the booking history page was reached. */
  async validatePage(): Promise<void> {
    console.log('Validate Booking history page was reached');
    await expect(this.bookingHistoryTitle, 'Booking history title').toHaveText(await Strings.YOUR_BOOKING_HISTORY.name);
  }

  /** Validate the booking history page title. */
  async validateBookingHistoryTitle(): Promise<void> {
    await this.validatePage();
  }

  /** Validate the no-bookings-found alert. */
  async validateNoBookingsFoundAlertMessage(): Promise<void> {
    console.log('Validate no bookings found alert');
    await expect(this.noBookingsFoundAlertLabel, 'No bookings found alert').toContainText(await Strings.NO_BOOKINGS_FOUND.name);
    await expect(this.pleaseTryAgainAlertLabel, 'Please try again alert').toContainText(await Strings.PLEASE_TRY_AGAIN.name);
  }

  /** Validate the empty-bookings alert. */
  async validateEmptyBookingsAlertMessage(): Promise<void> {
    console.log('Validate empty bookings alert');
    await expect(this.emptyBookingsAlertLabel, 'Empty bookings alert').toContainText(await Strings.YOUR_BOOKINGS_WILL_APPEAR_HERE.name);
  }

  /** Validate the empty-bookings notification is displayed. */
  async validateEmptyBookingsAlert(): Promise<void> {
    await this.validateEmptyBookingsAlertMessage();
  }

  /** Validate the booking dashboard URL. */
  async validateUrl(): Promise<void> {
    console.log('Validate booking history URL');
    expect(this.page.url(), 'URL should contain the booking dashboard path').toContain('/account/dashboard');
  }

  /** Validate the amendment URL. */
  async validateAmendBookingUrl(): Promise<void> {
    console.log('Validate amend booking URL');
    expect(this.page.url(), 'URL should contain the amendment path').toContain('/amend/');
  }

  /** Validate that all booking summary status labels are displayed. */
  async validateTotalBookingsSummaryStatusLabelsDisplayed(): Promise<void> {
    console.log('Validate total booking summary status labels');
    await expect(this.upcomingSummaryLabel, 'Upcoming total summary label').toBeVisible();
    await expect(this.checkedInSummaryLabel, 'Checked in total summary label').toBeVisible();
    await expect(this.pastSummaryLabel, 'Past total summary label').toBeVisible();
    await expect(this.cancelledSummaryLabel, 'Cancelled total summary label').toBeVisible();
  }

  /** Validate the values shown in the booking summary status labels. */
  async validateTotalBookingsSummaryStatusValues({ upcomingSummaryValue, checkedInSummaryValue, pastSummaryValue, cancelledSummaryValue }: {
    upcomingSummaryValue: number;
    checkedInSummaryValue: number;
    pastSummaryValue: number;
    cancelledSummaryValue: number;
  }): Promise<void> {
    console.log('Validate total booking summary status values');
    await expect(this.upcomingSummaryLabel, 'Upcoming total summary value').toContainText(`${upcomingSummaryValue} ${await Strings.BOOKING_UPCOMING_LABEL.name}`);
    await expect(this.checkedInSummaryLabel, 'Checked in total summary value').toContainText(`${checkedInSummaryValue} ${await Strings.BOOKING_CHECKED_IN_LABEL.name}`);
    await expect(this.pastSummaryLabel, 'Past total summary value').toContainText(`${pastSummaryValue} ${await Strings.BOOKING_PAST_LABEL.name}`);
    await expect(this.cancelledSummaryLabel, 'Cancelled total summary value').toContainText(`${cancelledSummaryValue} ${await Strings.BOOKING_CANCELLED_LABEL.name}`);
  }

  /** Validate the number of displayed booking rows. */
  async validateBookingsAreDisplayed(bookings: number): Promise<void> {
    console.log(`Validate ${bookings} bookings are displayed`);
    await expect(this.bookingsRowsList, 'Booking rows count').toHaveCount(bookings);
  }
}
