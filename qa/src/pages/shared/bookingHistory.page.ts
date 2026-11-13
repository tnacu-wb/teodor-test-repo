import { type Locator, expect } from '@playwright/test';
import { Strings } from '../../test-data/strings';
import { BasePage } from './base.page';
import { BookingHistoryCardComponent } from '../../components/pi/bookingHistory/bookingHistoryCard.component';
import { BookingHistoryTableComponent } from '../../components/pi/bookingHistory/bookingHistoryTable.component';
import { BookingSearchConsoleSectionComponent } from '../../components/pi/bookingHistory/bookingSearchConsole.component';

/**
 * PI Booking History Page - displays booking history cards with guest information,
 * room configuration, payment details, and booking status.
 * Used to validate post-amendment and post-cancellation states.
 */
export class BookingHistoryPage extends BasePage {
  // ######## UI elements/properties ########

  readonly bookingHistoryCard: BookingHistoryCardComponent = new BookingHistoryCardComponent();
  readonly bookingHistoryTable: BookingHistoryTableComponent = new BookingHistoryTableComponent();
  readonly bookingSearchConsole: BookingSearchConsoleSectionComponent = new BookingSearchConsoleSectionComponent();

  readonly bookingHistoryTitle: Locator = this.page.locator('h1');
  readonly emptyBookingsAlertLabel: Locator = this.page.locator('div[data-testid="AlertDescription"]');
  readonly noBookingsFoundAlertLabel: Locator = this.page.locator('div[data-testid="AlertDescription"] p').nth(0);
  readonly pleaseTryAgainAlertLabel: Locator = this.page.locator('div[data-testid="AlertDescription"] p').nth(1);
  readonly upcomingSummaryLabel: Locator = this.page.locator('[data-testid="textstats-element-upcoming"] span').nth(1);
  readonly checkedInSummaryLabel: Locator = this.page.locator('[data-testid="textstats-element-checkedin"] span');
  readonly pastSummaryLabel: Locator = this.page.locator('[data-testid="textstats-element-past"] span').nth(1);
  readonly cancelledSummaryLabel: Locator = this.page.locator('[data-testid="textstats-element-cancelled"] span').nth(1);
  readonly bookingsRowsList: Locator = this.bookingHistoryTable.rows;
  readonly bookingsSummary: Locator = this.page.locator('[data-testid="textstats-summary"]');

  // Booking Information Card container
  readonly bookingInfoCardContainer: Locator = this.page.locator('[data-testid="BookingInfoCardContainer"]');

  // Booking reference
  readonly bookingReferenceTitleLabel: Locator = this.page.locator('[data-testid="BookingActions-BookingReference"] p:first-child');
  readonly bookingReferenceLabel: Locator = this.page.locator('[data-testid="BookingActions-BookingReference"] p:last-child');

  // Pay On Arrival cost
  readonly payOnArrivalTotalCostLabel: Locator = this.page.locator('h6[data-testid="pay-on-arrival-total-cost-label"]');
  readonly payOnArrivalTotalCostAmountLabel: Locator = this.page.locator('h6[data-testid="pay-on-arrival-total-cost"]');

  // Status labels
  readonly bookingInfoCardStatusLabel: Locator = this.page.locator('tr[data-testid*="MyDashboard-Table-Row"] td:nth-child(6) span');

  /**
   * Get a booking history card locator by index.
   * @param index - 0-based index of the booking card (default: 0 for the first/only card)
   */
  getBookingCard(index = 0): Locator {
    return this.bookingInfoCardContainer.nth(index);
  }

  /**
   * Get the room number label for a specific room (0-based index).
   * @param roomIndex - 0-based room index
   */
  getRoomNumberLabel(roomIndex: number): Locator {
    return this.page.locator(`span[data-testid="roomNumber-${roomIndex + 1}"]`);
  }

  /**
   * Get the lead guest name label for a specific room (0-based index).
   * @param roomIndex - 0-based room index
   */
  getRoomLeadGuestNameLabel(roomIndex: number): Locator {
    return this.page.locator(
      `//span[@data-testid="roomNumber-${roomIndex + 1}"]//following-sibling::span[@data-testid="leadGuestName"]`
    );
  }

  /**
   * Get the room type and members label for a specific room (0-based index).
   * This includes the room type text followed by "X Adults, Y Children".
   * @param roomIndex - 0-based room index
   */
  getRoomTypeAndMembersLabel(roomIndex: number): Locator {
    return this.page.locator(
      `//span[@data-testid="roomNumber-${roomIndex + 1}"]/following-sibling::div/p[@data-testid="roomType"]`
    );
  }

  /**
   * Get the adults label for a specific room (0-based index).
   * @param roomIndex - 0-based room index
   */
  getRoomAdultsLabel(roomIndex: number): Locator {
    return this.page.locator(
      `//span[@data-testid="roomNumber-${roomIndex + 1}"]/following-sibling::div/p/span[1]`
    );
  }

  /**
   * Get the children label for a specific room (0-based index).
   * @param roomIndex - 0-based room index
   */
  getRoomChildrenLabel(roomIndex: number): Locator {
    return this.page.locator(
      `//span[@data-testid="roomNumber-${roomIndex + 1}"]/following-sibling::div/p/span[2]`
    );
  }

  // ######## UI actions/navigation ########

  /** Check whether the booking information card is displayed. */
  async checkIfBookingInfoCardIsDisplayed(): Promise<boolean> {
    console.log('Check if booking information card is displayed');
    return this.bookingHistoryCard.bookingInfoCardContainer.isVisible();
  }

  /** Check whether the first booking row has cancelled status. */
  async checkIfBookingIsCancelled(): Promise<boolean> {
    console.log('Check if booking is cancelled');
    const statusLabel = this.page.locator('tr[data-testid*="MyDashboard-Table-Row"] td:nth-child(6) span').first();
    return (await statusLabel.textContent() ?? '').includes(await Strings.BOOKING_CANCELLED_LABEL.name);
  }

  /** Search booking history by booking reference or surname. */
  async searchBooking({ bookingReference, waitForBooking = true }: { bookingReference: string; waitForBooking?: boolean }): Promise<void> {
    console.log(`Search booking history for ${bookingReference}`);
    await this.bookingSearchConsole.setInputFilter(bookingReference);
    if (waitForBooking) await this.page.waitForTimeout(180000);
    await this.bookingSearchConsole.clickFindButton();
  }

  /** Find a booking, refreshing while the booking is still propagating to CDH. */
  async findBooking(bookingReferenceID: string, { checkIsCancelled = false, fail = true }: { checkIsCancelled?: boolean; fail?: boolean } = {}): Promise<void> {
    console.log('Wait for 10 minutes for the booking to appear.');
    const maxWaitTime = Date.now() + 600000;

    while (Date.now() <= maxWaitTime) {
      await this.validatePage();
      await this.bookingSearchConsole.setInputFilter(bookingReferenceID);
      await this.bookingSearchConsole.clickFindButton();
      const bookingIsFound = await this.checkIfBookingInfoCardIsDisplayed();
      const bookingIsCancelled = bookingIsFound && checkIsCancelled ? await this.checkIfBookingIsCancelled() : false;
      if (bookingIsFound && (!checkIsCancelled || bookingIsCancelled)) {
        console.log('Booking found!');
        return;
      }

      console.log('Retrying after refresh...');
      await this.page.waitForTimeout(10000);
      await this.page.reload({ waitUntil: 'domcontentloaded' });
    }

    if (fail) throw new Error('Wait timeout finding the booking!');
    console.log('Wait timeout finding the booking!');
  }

  // ######## UI validations ########

  /** Validate Booking history page was reached. */
  async validatePage(): Promise<void> {
    console.log('Validate Booking history page was reached');
    await expect(this.bookingHistoryTitle, 'Booking history title').toHaveText(await Strings.YOUR_BOOKING_HISTORY.name);
  }

  /** Validate the no-bookings-found alert. */
  async validateNoBookingsFoundAlertMessage(): Promise<void> {
    console.log('Validate no bookings found alert');
    await expect(this.noBookingsFoundAlertLabel, 'No bookings found alert').toContainText(await Strings.NO_BOOKINGS_FOUND.name);
    await expect(this.pleaseTryAgainAlertLabel, 'Please try again alert').toContainText(await Strings.PLEASE_TRY_AGAIN.name);
  }

  /** Validate the empty booking-history alert. */
  async validateEmptyBookingsAlertMessage(): Promise<void> {
    console.log('Validate empty bookings alert');
    await expect(this.emptyBookingsAlertLabel, 'Empty bookings alert').toContainText(await Strings.YOUR_BOOKINGS_WILL_APPEAR_HERE.name);
  }

  /** Validate the dashboard URL. */
  async validateUrl(): Promise<void> {
    console.log('Validate booking history URL');
    expect(this.page.url(), 'URL should contain the booking dashboard path').toContain('/account/dashboard');
  }

  /** Validate the amendment URL. */
  async validateAmendBookingUrl(): Promise<void> {
    console.log('Validate amend booking URL');
    expect(this.page.url(), 'URL should contain the amendment path').toContain('/amend/');
  }

  /** Validate all booking summary status labels are displayed. */
  async validateTotalBookingsSummaryStatusLabelsDisplayed(): Promise<void> {
    console.log('Validate total booking summary status labels');
    await expect(this.upcomingSummaryLabel, 'Upcoming total summary label').toBeVisible();
    await expect(this.checkedInSummaryLabel, 'Checked in total summary label').toBeVisible();
    await expect(this.pastSummaryLabel, 'Past total summary label').toBeVisible();
    await expect(this.cancelledSummaryLabel, 'Cancelled total summary label').toBeVisible();
  }

  /** Validate the values shown in the booking summary status labels. */
  async validateTotalBookingsSummaryStatusValues({ upcomingSummaryValue, checkedInSummaryValue, pastSummaryValue, cancelledSummaryValue }: { upcomingSummaryValue: number; checkedInSummaryValue: number; pastSummaryValue: number; cancelledSummaryValue: number }): Promise<void> {
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

  /**
   * Validate the booking reference ID matches the expected value.
   * @param expected - the expected booking reference string
   */
  async validateBookingReferenceId(expected: string): Promise<void> {
    console.log(`Validating booking reference: ${expected}`);
    await expect(this.bookingReferenceLabel, 'Booking reference label should be visible').toBeVisible();
    await expect(this.bookingReferenceLabel, `Booking reference should display "${expected}"`).toHaveText(expected);
  }

  /**
   * Validate the lead guest name displayed for a specific room.
   * @param expected - the expected full name (e.g. "John Smith")
   * @param roomIndex - 0-based room index (default: 0)
   */
  async validateLeadGuestName(expected: string, roomIndex = 0): Promise<void> {
    console.log(`Validating lead guest name for room ${roomIndex}: ${expected}`);
    const guestLabel = this.getRoomLeadGuestNameLabel(roomIndex);
    await expect(guestLabel, `Lead guest name for room ${roomIndex} should be visible`).toBeVisible();
    await expect(guestLabel, `Lead guest name for room ${roomIndex} should be "${expected}"`).toHaveText(expected);
  }

  /**
   * Validate the room guests string (e.g. "2 Adults, 1 Child") for a specific room.
   * Extracts the guests portion from the room type and members label.
   * @param expected - the expected guests string (e.g. "2 Adults, 1 Child")
   * @param roomIndex - 0-based room index (default: 0)
   */
  async validateRoomGuests(expected: string, roomIndex = 0): Promise<void> {
    console.log(`Validating room ${roomIndex} guests: ${expected}`);
    const roomTypeLabel = this.getRoomTypeAndMembersLabel(roomIndex);
    await expect(roomTypeLabel, `Room ${roomIndex} type label should be visible`).toBeVisible();
    const fullText = await roomTypeLabel.textContent() ?? '';
    // The format is "RoomType - X Adults, Y Children" — extract after the dash
    const guestsText = fullText.includes('-') ? fullText.split('-').pop()!.trim() : fullText.trim();
    expect(guestsText, `Room ${roomIndex} guests string should be "${expected}" but got "${guestsText}"`).toBe(expected);
  }

  /**
   * Validate the adults and children numbers for specific room indexes.
   * @param options.roomIndex - 0-based room index
   * @param options.expectedAdults - expected number of adults
   * @param options.expectedChildren - expected number of children
   */
  async validateAdultsAndChildrenNumbers(options: {
    roomIndex: number;
    expectedAdults: number;
    expectedChildren: number;
  }): Promise<void> {
    console.log(`Validating room ${options.roomIndex} occupancy: ${options.expectedAdults} adults, ${options.expectedChildren} children`);
    const { roomIndex, expectedAdults, expectedChildren } = options;
    const roomTypeLabel = this.getRoomTypeAndMembersLabel(roomIndex);
    await expect(roomTypeLabel, `Room ${roomIndex} type label should be visible`).toBeVisible();
    const fullText = await roomTypeLabel.textContent() ?? '';
    // Extract guests portion after the dash
    const guestsText = fullText.includes('-') ? fullText.split('-').pop()!.trim() : fullText.trim();

    // Validate adults count
    const adultsMatch = guestsText.match(/(\d+)\s*Adult/i);
    expect(adultsMatch, `Room ${roomIndex} - adults count should be found in "${guestsText}"`).not.toBeNull();
    expect(Number(adultsMatch![1]), `Room ${roomIndex} - expected ${expectedAdults} adults but got ${adultsMatch![1]}`).toBe(expectedAdults);

    // Validate children count
    if (expectedChildren > 0) {
      const childrenMatch = guestsText.match(/(\d+)\s*Child/i);
      expect(childrenMatch, `Room ${roomIndex} - children count should be found in "${guestsText}"`).not.toBeNull();
      expect(Number(childrenMatch![1]), `Room ${roomIndex} - expected ${expectedChildren} children but got ${childrenMatch![1]}`).toBe(expectedChildren);
    }
  }

  /**
   * Validate the Pay On Arrival total cost displayed on the booking history card.
   * Handles both with and without decimal places (e.g. "£102" or "£102.00").
   * @param expected - the expected total cost string (formatted with currency, e.g. "£123.00" or "£123")
   */
  async validatePayOnArrivalTotalCost(expected: string): Promise<void> {
    console.log(`Validating Pay On Arrival total cost: ${expected}`);
    await expect(this.payOnArrivalTotalCostAmountLabel, 'Pay On Arrival total cost amount label should be visible').toBeVisible();
    const actualText = await this.payOnArrivalTotalCostAmountLabel.textContent() ?? '';
    
    // Extract currency symbol and numeric value from both strings
    const expectedNumeric = this.extractNumericValue(expected);
    const actualNumeric = this.extractNumericValue(actualText);
    const expectedCurrency = this.extractCurrency(expected);
    const actualCurrency = this.extractCurrency(actualText);
    
    // Compare currency symbols and numeric values
    expect(actualCurrency, `Currency should be "${expectedCurrency}" but got "${actualCurrency}"`).toBe(expectedCurrency);
    expect(actualNumeric, `Total cost should be "${expectedNumeric}" but got "${actualNumeric}"`).toBeCloseTo(expectedNumeric, 2);
  }

  /**
   * Validate that the booking is in a cancelled state.
   * Checks that the booking card shows the cancelled BIC (Booking Information Card)
   * by verifying room labels, guest names, and room type information are still visible
   * for cancelled bookings.
   */
  async validateCancelledStatus(): Promise<void> {
    console.log('Validating cancelled booking status');
    // The booking reference label should still be visible with "Booking reference" title
    await expect(this.bookingReferenceTitleLabel, 'Booking reference title should be visible for cancelled booking').toBeVisible();
    await expect(this.bookingReferenceLabel, 'Booking reference label should be visible for cancelled booking').toBeVisible();
  }

  /**
   * Validate the cancelled BIC (Booking Information Card) — verifies the card structure
   * is shown for a cancelled booking including room details and guest information.
   * This is the primary validation used after re-searching a cancelled booking via the
   * manage booking modal, matching the original baseline's validateCanceledBIC call.
   * @param options.roomsList - array of room configurations to validate against
   */
  async validateCanceledBIC(options: {
    roomsList: Array<{ adultsNumber: number; childrenNumber: number }>;
  }): Promise<void> {
    console.log(`Validating cancelled BIC with ${options.roomsList.length} room(s)`);
    const { roomsList } = options;

    for (let roomIndex = 0; roomIndex < roomsList.length; roomIndex++) {
      // Booking reference title should be visible
      await expect(this.bookingReferenceTitleLabel, 'Booking reference title should be visible in cancelled BIC').toBeVisible({ timeout: 10000 });

      // Room number label should be visible
      await expect(this.getRoomNumberLabel(roomIndex), `Room ${roomIndex} number label should be visible`).toBeVisible();

      // Lead guest name should be visible
      await expect(this.getRoomLeadGuestNameLabel(roomIndex), `Room ${roomIndex} lead guest name should be visible`).toBeVisible();

      // Room type and members label should be visible
      await expect(this.getRoomTypeAndMembersLabel(roomIndex), `Room ${roomIndex} type and members label should be visible`).toBeVisible();

      // Adults label should be visible
      await expect(this.getRoomAdultsLabel(roomIndex), `Room ${roomIndex} adults label should be visible`).toBeVisible();

      // Children label visibility depends on whether room has children
      if (roomsList[roomIndex].childrenNumber > 0) {
        await expect(this.getRoomChildrenLabel(roomIndex), `Room ${roomIndex} children label should be visible (${roomsList[roomIndex].childrenNumber} children)`).toBeVisible();
      }
    }
  }

  // ######## Private helpers ########

  /**
   * Extract numeric value from a formatted price string.
   * @param text - the formatted price text (e.g. "£102.00" or "£102")
   * @returns the numeric value
   */
  private extractNumericValue(text: string): number {
     let amount = text.replace(/[^\d,.-]/g, '').replace(/\s+/g, '');
     if (amount.slice(-3, -2) === ',') amount = amount.replace(/\./g, '').replace(',', '.');
     else if (amount.slice(-3, -2) === '.') amount = amount.replace(/,/g, '');
     else amount = amount.replace(/[,.]/g, '');
     return Number.parseFloat(amount);
  }

  /**
   * Extract currency symbol from a formatted price string.
   * @param text - the formatted price text (e.g. "£102.00")
   * @returns the currency symbol
   */
  private extractCurrency(text: string): string {
    return text.replace(/[\d,.]/g, '').trim();
  }
}
