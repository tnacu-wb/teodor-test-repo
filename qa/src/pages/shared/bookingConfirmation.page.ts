import { type Locator, expect } from '@playwright/test';
import moment from 'moment';
import { Constants } from '../../test-data/constants';
import { Locales, getCurrentLocale } from '../../test-data/locales';
import { Strings } from '../../test-data/strings';
import { PriceHelpers } from '../../utils';
import { BasePage } from './base.page';

/**
 * Booking confirmation page from Opera environment containing the UI elements, custom actions and validations.
 */
export class BookingConfirmationPage extends BasePage {
  // ######## UI elements/properties ########

  readonly backToDashboardButton: Locator = this.page.locator('button[data-testid="BookingInfoCardHeader-BackToDashboardButton"], button[data-testid="BookingInfoCardHeader-CCUIBackToDashboardButton"]');
  readonly bookingReferenceLabel: Locator = this.page.locator('[data-testid="BookingActions-BookingReference"]');
  readonly amendSuccessNotification: Locator = this.page.locator('[data-testid="amend-booking-confirmation-success-notification-Alert"]');
  readonly amendErrorNotification: Locator = this.page.locator('[data-testid="amend-booking-confirmation-error-notification-Alert"]');
  readonly bookingDetailsContainer: Locator = this.page.locator('[data-testid="BookingDetails-Container"]');
  readonly totalCostAmount: Locator = this.bookingDetailsContainer.locator('h6[data-testid$="total-cost"]');
  readonly newTotalCostAmount: Locator = this.page.locator('h6[data-testid="newTotalSumLabel"]');
  readonly balanceOutstandingAmount: Locator = this.page.locator('h6[data-testid="balanceOutstandingSumLabel"]');
  readonly previousTotalAmount: Locator = this.page.locator('h6[data-testid="previousTotalSumLabel"]');
  readonly checkinDate: Locator = this.page.locator('[data-testid="BookingInfoCardHeader-CheckInDate"]');
  readonly checkoutDate: Locator = this.page.locator('[data-testid="BookingInfoCardHeader-CheckOutDate"]');

  // ######## UI actions/navigation ########

  /**
   * Click on Back To Dashboard button.
   */
  async clickOnBackToDashboardButton(): Promise<void> {
    console.log('Click on Back To Dashboard button');

    await expect(this.backToDashboardButton, 'Back To Dashboard button should be clickable').toBeEnabled({ timeout: 10000 });
    await this.backToDashboardButton.scrollIntoViewIfNeeded();
    await this.backToDashboardButton.click();
  }

  // ######## UI validations ########

  /**
   * Check we reached the current page by checking a specific element from the page.
   */
  async validatePage(): Promise<void> {
    console.log('Validate Booking confirmation page was reached');

    await expect(
      this.backToDashboardButton.or(this.bookingReferenceLabel).first(),
      'Booking confirmation marker should be displayed (within 120s)',
    ).toBeVisible({ timeout: 120000 });
  }

  /** Validate displayed stay dates using optional booking check-in and check-out times. */
  async validateStayingDates(expectedArrivalDate: string, expectedDepartureDate: string, { checkInTime = '15:00', checkOutTime = '12:00' }: { checkInTime?: string; checkOutTime?: string } = {}): Promise<void> {
    console.log(`Validate staying dates. Expected arrival date: ${expectedArrivalDate}; Expected departure date: ${expectedDepartureDate}`);
    const locale = getCurrentLocale();
    const formatDate = (date: string, time: string): string => {
      const parsedTime = moment(time, 'HH:mm');
      const dateTime = moment(date).locale(locale.language).set({ hour: parsedTime.hour(), minute: parsedTime.minute() });
      if (locale.name === Locales.GB_EN.name) {
        return dateTime.format(Constants.HOUR_DAYNAME_DAY_DATE_FORMAT_GB).replace(/^\d+\w+/, parsedTime.format('ha'));
      }
      const formattedDate = dateTime.format(Constants.HOUR_DAYNAME_DAY_DATE_FORMAT_DE);
      const monthDotIndex = formattedDate.length - 6;
      return ['.', 'z'].includes(formattedDate.charAt(monthDotIndex))
        ? formattedDate.slice(0, monthDotIndex) + formattedDate.slice(monthDotIndex + 1)
        : formattedDate;
    };
    const arrival = formatDate(expectedArrivalDate, checkInTime);
    const departure = formatDate(expectedDepartureDate, checkOutTime);
    if (locale.name === Locales.DE_DE.name && (arrival.includes('Juni') || departure.includes('Juni') || arrival.includes('Juli') || departure.includes('Juli'))) {
      if (arrival.includes('Juni')) await expect(this.checkinDate, 'Check in date in June').toContainText('Jun');
      else if (departure.includes('Juni')) await expect(this.checkoutDate, 'Check out date in June').toContainText('Jun');
      else if (arrival.includes('Juli')) await expect(this.checkinDate, 'Check in date in July').toContainText('Jul');
      else await expect(this.checkoutDate, 'Check out date in July').toContainText('Jul');
      return;
    }
    await expect(this.checkinDate, 'Check in date').toHaveText(arrival);
    await expect(this.checkoutDate, 'Check out date').toHaveText(departure);
  }

  /**
   * Validate amend success notification.
   * @param isDisplayed true if notification is displayed
   */
  async validateAmendSuccessNotification(isDisplayed = true): Promise<void> {
    console.log(`Validate amend success notification that is displayed=${isDisplayed}`);

    if (!isDisplayed) {
      await expect(this.amendSuccessNotification, 'Amend success notification should not be displayed').not.toBeVisible({ timeout: 5000 });
      return;
    }

    await expect(this.amendSuccessNotification, 'Amend success notification should be displayed').toBeVisible({ timeout: 30000 });
    await expect(this.amendSuccessNotification, 'Amend success notification text should match expected text').toContainText(await Strings.YOUR_BOOKING_HAS_BEEN_UPDATED.name, { timeout: 10000 });
  }

  /**
   * Validate room adults and children.
   * @param roomIndexes array of room indexes (0 based)
   * @param expectedGuestsByRoom array of expected labels of adults and children number
   */
  async validateRoomAdultsAndChildren(roomIndexes: number[], expectedGuestsByRoom: string[]): Promise<void> {
    console.log('Validate adults and children number');

    const actualGuestsByRoom: string[] = [];

    for (const roomIndex of roomIndexes) {
      const roomLabel = this.getRoomTypeAdultsAndChildrenLabel(roomIndex);
      await expect(roomLabel, `Room type and guests label should be displayed for room ${roomIndex}`).toBeVisible({ timeout: 10000 });
      const text = await roomLabel.textContent() ?? '';
      actualGuestsByRoom.push(text.match('[^-]*$')?.[0]?.trim() ?? text.trim());
    }

    expect(actualGuestsByRoom.sort(), 'Rooms should have expected guests').toEqual([...expectedGuestsByRoom].sort());
  }

  /**
   * Validate Room type label.
   * @param options data object
   * @param options.roomIndex room index (0 based)
   * @param options.roomNumber room number (1 based)
   * @param options.roomTypeLabelAem room type label
   */
  async validateRoomTypeLabel(options: { roomTypeLabelAem: string; roomIndex?: number; roomNumber?: number }): Promise<void> {
    console.log(`Validate the Room type label using: ${options.roomTypeLabelAem}`);

    const roomIndex = options.roomIndex ?? (options.roomNumber !== undefined ? options.roomNumber - 1 : 0);
    const roomLabel = this.getRoomTypeAdultsAndChildrenLabel(roomIndex);
    await expect(roomLabel, 'Room type label should be displayed').toBeVisible({ timeout: 10000 });
    await expect(roomLabel, 'Room type label does not match expected value').toContainText(options.roomTypeLabelAem, { ignoreCase: true });
  }

  /**
   * Validate total cost amount and currency displayed under booking summary are correct.
   * @param expectedAmount expected total cost amount
   * @param expectedCurrency expected total cost currency
   */
  async validateTotalCostAmountAndCurrency(expectedAmount: string, expectedCurrency: string): Promise<void> {
    console.log('Validate total cost amount and currency');

    await PriceHelpers.validatePriceAmountAndCurrencyLabel({
      element: this.totalCostAmount,
      expectedAmount,
      expectedCurrency,
      elementDescription: 'Total cost',
    });
  }

  /**
   * Validate the amended booking's new total and currency.
   * @param expectedAmount expected new total amount
   * @param expectedCurrency expected total currency
   */
  async validateNewTotalCostAmountAndCurrency(expectedAmount: string | number, expectedCurrency: string): Promise<void> {
    console.log('Validate amended new total cost amount and currency');

    await PriceHelpers.validatePriceAmountAndCurrencyLabel({
      element: this.newTotalCostAmount,
      expectedAmount,
      expectedCurrency,
      elementDescription: 'New total cost',
    });
  }

  // ######## Private helpers ########

  /**
   * Returns type, adults and children number of specific room from booking card.
   * @param roomIndex room index (0 based)
   */
  private getRoomTypeAdultsAndChildrenLabel(roomIndex: number): Locator {
    return this.page.locator(`span[data-testid="roomNumber-${roomIndex + 1}"] + div p[data-testid="roomType"]`);
  }
}