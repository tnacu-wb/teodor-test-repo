import { expect, type Locator } from '@playwright/test';
import { Strings } from '../../test-data/strings';
import { SearchConsoleComponent } from '../../components/ccui/searchConsole/searchConsole.component';
import { BookingInformationCardSectionComponent } from '../../components/ccui/manageBooking/bookingInformationCardSection.component';
import { SearchBookingResultsSectionComponent } from '../../components/ccui/manageBooking/searchBookingResultsSection.component';
import { SearchBookingTableSectionComponent } from '../../components/ccui/manageBooking/searchBookingTableSection.component';
import { BasePage } from '../shared/base.page';

/** Manage Booking CCUI Page. */
export class ManageBookingPage extends BasePage {
  // ######## properties ########
  // ######## UI elements/properties ########
  readonly notificationContainer: Locator = this.page.locator('div[data-testid="SearchBookingsPage-Notification"]');
  readonly notificationLabel: Locator = this.page.locator('div[data-testid="AlertDescription"]');
  readonly searchRetryNotificationContainers: Locator = this.page.locator('div[data-testid="SearchBookingsPage-Notification"], div[data-testid="SearchBookingsPage-NoBookingNotification"], div[data-testid="AlertTitle"]');
  readonly searchConsole: SearchConsoleComponent = new SearchConsoleComponent();
  readonly searchBookingResults: SearchBookingResultsSectionComponent = new SearchBookingResultsSectionComponent();
  readonly searchBookingTable: SearchBookingTableSectionComponent = new SearchBookingTableSectionComponent();
  readonly bookingInformationCard: BookingInformationCardSectionComponent = new BookingInformationCardSectionComponent();
  readonly changePaymentMethodButton: Locator = this.bookingInformationCard.bookingActionsContainer.locator('p').last();

  // ######## UI actions/navigation ########
  /** Check the opened booking information card or loaded search result rows for the expected booking reference. */
  async bookingReferenceExistsInLoadedSearchResults(bookingReference: string): Promise<boolean> {
    console.log(`Check loaded search results for booking reference ${bookingReference}`);
    const rows = await this.searchBookingTable.getSearchBookingTableRowsArray();

    for (const row of rows) {
      await row.bookingRow.scrollIntoViewIfNeeded();
      if ((await row.bookingRow.innerText()).includes(bookingReference)) return true;
    }

    if (await this.bookingInformationCard.bookingInfoCardContainer.isVisible()) {
      const displayedBookingReference = await this.bookingInformationCard.bookingReferenceLabel.innerText();
      if (displayedBookingReference.includes(bookingReference)) {
        await this.bookingInformationCard.validateBookingReferenceTitle(bookingReference);
        return true;
      }
    }

    return this.page.getByText(bookingReference, { exact: false }).first().isVisible();
  }
  /** Check if a search-level retryable notification is displayed. */
  async isSearchRetryNotificationDisplayed(): Promise<boolean> {
    console.log('Check if a search-level retryable notification is displayed');
    const notificationCount = await this.searchRetryNotificationContainers.count();
    for (let notificationIndex = 0; notificationIndex < notificationCount; notificationIndex += 1) {
      if (await this.searchRetryNotificationContainers.nth(notificationIndex).isVisible()) return true;
    }
    return false;
  }
  /** Click on change payment method button. */
  async changePaymentMethod(): Promise<void> { console.log('Click on change payment method button'); await this.changePaymentMethodButton.click(); }
  /** Validated if booking info card has cancelled status. */
  async checkIfBookingIsCancelled(): Promise<boolean> { console.log('Check if booking is cancelled'); const rows = await this.searchBookingTable.getSearchBookingTableRowsArray(); return rows.length > 0 && (await rows[0].statusLabel.innerText()).includes(await Strings.BOOKING_CANCELLED_LABEL.name); }
  /** Find the booking, refreshing while the booking is still propagating to the search service. */
  async findBooking(bookingReferenceID: string, { checkIsCancelled = false, fail = true }: { checkIsCancelled?: boolean; fail?: boolean } = {}): Promise<void> {
    console.log('Wait for 10 minutes for the booking to appear.');
    const maxWaitTime = Date.now() + 600000;

    while (Date.now() <= maxWaitTime) {
      await this.validatePage();
      await this.searchBookingResults.clearBookingReferenceInput();
      await this.searchBookingResults.setBookingReferenceInput({ value: bookingReferenceID });
      await this.searchBookingResults.clickSearchForBooking();

      const bookingIsFound = await this.bookingInformationCard.checkIfBookingInfoCardIsDisplayed();
      const bookingIsCancelled = bookingIsFound && checkIsCancelled ? await this.checkIfBookingIsCancelled() : false;
      if (bookingIsFound && (!checkIsCancelled || bookingIsCancelled)) return;

      console.log('Retry finding the booking...');
      await this.page.waitForTimeout(10000);
      await this.page.reload({ waitUntil: 'domcontentloaded' });
    }

    if (fail) throw new Error('Wait timeout finding the booking!');
    console.log('Wait timeout finding the booking!');
  }
  /** Navigate to Amend booking page from Manage booking page. */
  async navigateToAmendBookingPage(): Promise<void> { console.log('Navigate to Amend booking page from Manage booking page'); await this.bookingInformationCard.clickAmendBookingButton(); await global.ccuiPages.amendBookingPage.validatePage(); }
  /** Search bookings by Hotel Name, Arrival Date and Email Address. */
  async searchBookingByHotelArrivalDateAndEmail({ hotelName, arrivalDate, emailAddress }: { hotelName: string; arrivalDate: Date; emailAddress: string }): Promise<void> { console.log('Search bookings by Hotel Name, Arrival Date and Email Address'); await this.validatePage(); if (!(await this.searchBookingResults.emailAddressInput.isVisible().catch(() => false))) await this.searchBookingResults.clickToggleSearchCriteriaLink(); await this.searchBookingResults.setHotelNameInputForExtendedSearch(hotelName); await this.searchBookingResults.selectHotelNameFromDropdownForExtendedSearch(hotelName); await this.searchBookingResults.setArrivalDateInputForExtendedSearch({ value: arrivalDate, pressTab: true }); await this.searchBookingResults.setEmailAddressInputForExtendedSearch({ value: emailAddress, pressTab: true }); await this.searchBookingResults.validateHotelArrivalDateAndEmailSearchValues({ hotelName, emailAddress }); await this.searchBookingResults.validateSearchForBookingButtonIsEnabled(); await this.searchBookingResults.clickSearchForBooking(); }
  /** Find booking by Hotel Name, Arrival Date and Email Address, then match the booking reference in the results. */
  async findBookingByHotelArrivalDateAndEmail(data: { hotelName: string; arrivalDate: Date; emailAddress: string; bookingReference: string }): Promise<void> {
    console.log('Find booking by Hotel Name, Arrival Date and Email Address');
    await this.searchBookingByHotelArrivalDateAndEmail(data);
    let retryCount = 0;

    await expect.poll(async () => {
      if (await this.bookingReferenceExistsInLoadedSearchResults(data.bookingReference)) {
        console.log(`Booking reference ${data.bookingReference} found in search results.`);
        return true;
      }

      if (await this.isSearchRetryNotificationDisplayed()) {
        retryCount += 1;
        console.log(`Booking reference ${data.bookingReference} not visible because search notification is displayed. Clicking Search again. Retry ${retryCount}`);
        await this.searchBookingResults.clickSearchForBooking();
        return this.bookingReferenceExistsInLoadedSearchResults(data.bookingReference);
      }

      console.log(`Booking reference ${data.bookingReference} not readable yet. Waiting for loaded booking details.`);
      return false;
    }, { timeout: 60000, intervals: [5000], message: `Booking reference ${data.bookingReference} was not found in search results` }).toBe(true);
  }

  // ######## UI validations ########
  /** Validate Notification Container is displayed. */
  async validateNotificationContainer(): Promise<void> { console.log('Validate Notification container'); await expect(this.notificationContainer, 'Notification container').toBeVisible(); await expect(this.notificationLabel, 'Notification message is not present').toContainText(await Strings.NOTIFICATION_MESSAGE.name); }
  /** Validate Change payment method CTA is displayed and disabled. */
  async validateChangePaymentMethodButtonIsDisabled(): Promise<void> { console.log('Validate Change payment method CTA is displayed and disabled'); await expect(this.changePaymentMethodButton, 'Change payment method CTA').toBeVisible(); await expect(this.changePaymentMethodButton, 'Change payment method CTA should be disabled').toBeDisabled(); }
  /** Validate page. */
  async validatePage(): Promise<void> { console.log('Validate CCUI Manage Booking page'); await expect(this.searchBookingResults.searchForBookingButton, 'Search for booking button').toBeVisible(); }
  /** Validate Notification Container is displayed after language change. */
  async validateNotificationContainerExpectedLanguage(language: string): Promise<void> { console.log(`Validate Notification container expected language '${language}'`); await expect(this.notificationContainer, 'Notification container after language change').toBeVisible(); if (language === 'en') await expect(this.notificationLabel, 'Notification message in english').toContainText(Strings.NOTIFICATION_MESSAGE.data.default ?? ''); else if (language === 'de') await expect(this.notificationLabel, 'Notification message in german').toContainText(Strings.NOTIFICATION_MESSAGE.data.de ?? ''); else console.log('Value entered in parameter is not supported for notification validation after language change'); }
  /** Validate the booking card position relative to the surrounding result rows. */
  async validateBookingCardPosition({ isBetweenRows = true, aboveElement, belowElement }: { isBetweenRows?: boolean; aboveElement: { bookingRow: Locator }; belowElement?: { bookingRow: Locator } }): Promise<void> {
    console.log('Validate booking card position');
    await expect(this.bookingInformationCard.bookingInfoCardContainer, 'Booking card is displayed').toBeVisible();
    const bookingCardBox = await this.bookingInformationCard.bookingInfoCardContainer.boundingBox();
    const aboveBox = await aboveElement.bookingRow.boundingBox();
    expect(bookingCardBox, 'Booking card should have a visible position').not.toBeNull();
    expect(aboveBox, 'Row above booking card should have a visible position').not.toBeNull();
    expect(bookingCardBox!.y, 'Booking card should be below the row above it').toBeGreaterThan(aboveBox!.y);
    expect(bookingCardBox!.y - (aboveBox!.y + aboveBox!.height), 'Booking card should be within 100px of the row above it').toBeLessThanOrEqual(100);

    if (isBetweenRows) {
      if (!belowElement) throw new Error('A row below the booking card is required when isBetweenRows is true');
      const belowBox = await belowElement.bookingRow.boundingBox();
      expect(belowBox, 'Row below booking card should have a visible position').not.toBeNull();
      expect(bookingCardBox!.y, 'Booking card should be above the row below it').toBeLessThan(belowBox!.y);
      expect(belowBox!.y - (bookingCardBox!.y + bookingCardBox!.height), 'Booking card should be within 600px of the row below it').toBeLessThanOrEqual(600);
    }
  }
}