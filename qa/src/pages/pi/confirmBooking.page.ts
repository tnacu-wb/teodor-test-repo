import { type Locator, expect } from '@playwright/test';
import { formatDateForUI, UiUtils } from '../../utils';
import { Locales } from '../../test-data/locales';
import { Strings } from '../../test-data/strings';
import { ConfirmBookingPageBase } from '../shared/confirmBookingBase.page';

export interface BookingDetailsIntroExpected {
  guestName: string;
  guestTitle?: string;
  bookingReference: string;
  hotelName: string;
  hotelType: string;
}

export interface RoomDetailsExpected {
  roomType: string;
  checkIn: string;
  checkOut: string;
  adults?: number;
  meals?: string;
}

export interface TotalCostExpected {
  amount: string;
  currencyCode: string;
  paymentLabel: string;
}

/**
 * Confirm booking page from Opera environment containing the UI elements, custom actions and validations.
 */
export class ConfirmBookingPage extends ConfirmBookingPageBase {
  // ######## UI elements/properties ########

  readonly pageLoadedIndicator: Locator = this.page.locator('[data-testid="ThanksForBooking-Container"]');

  readonly guestNameHeading: Locator = this.page.locator('[data-testid="ThanksForBooking-Title-Name"]');
  readonly bookingReferenceLabel: Locator = this.page.locator('[data-testid="BookingReferenceDetails-Label"]');
  readonly bookingReference: Locator = this.page.locator('[data-testid="BookingReferenceDetails-Id"]');
  readonly bookingConfirmationSection: Locator = this.bookingReferenceLabel.locator('..');
  readonly hotelName: Locator = this.page.locator('[data-testid="BookingReferenceDetails-Hotel"]');
  readonly hotelAddress: Locator = this.page.locator('[data-testid="BookingReferenceDetails-HotelAdress"]');
  readonly hotelPhone: Locator = this.page.locator('[data-testid="BookingReferenceDetails-HotelPhone"]');

  readonly hotelDirectionsLabel: Locator = this.page.locator('[data-testid="hotelDirections-label"]');
  readonly hotelDirectionsText: Locator = this.page.locator('[data-testid="hotelDirections-directions"]');
  readonly hotelDirectionsSectionContainer: Locator = this.hotelDirectionsLabel.locator('..');
  readonly cityTaxNotification: Locator = this.page.locator('div[data-testid="AlertDescription"]');
  readonly cityTaxNotificationLink: Locator = this.cityTaxNotification.locator('a');

  readonly totalCostContainer: Locator = this.page.locator('[data-testid="TotalCostConfirm-container"]');
  readonly totalCostAmount: Locator = this.page.locator('[data-testid="TotalCostConfirm-amount"]');
  readonly totalCostLabel: Locator = this.page.locator('[data-testid="TotalCostConfirm-label"]');
  readonly totalCostPaymentMessage: Locator = this.page.locator('[data-testid="TotalCostConfirm-paymentMessage"]');
  readonly onlineCharitablePledgeLabel: Locator = this.page.locator('b[data-testid="TotalCostConfirm-donationLabel"]');
  readonly donationsAmountLabel: Locator = this.page.locator('b[data-testid="TotalCostConfirm-donationAmount"]');

  readonly continueButton: Locator = this.page.locator('button[data-testid*="BackToDashboardButton"], button[data-testid="continueBtn"]');

  /**
   * Get Booking reference id displayed on booking confirmation page.
   * @returns booking reference ID text
   */
  async getBookingReference(): Promise<string> {
    console.log('Get booking reference from Confirm booking page');

    await this.bookingReference.waitFor({ state: 'visible' });
    const text = await this.bookingReference.textContent();
    return text?.trim() ?? '';
  }

  /**
   * Get total cost amount from confirm booking page.
   * @returns total cost amount text
   */
  async getTotalCostAmount(): Promise<string> {
    console.log('Get total cost amount from Confirm booking page');

    await this.totalCostAmount.waitFor({ state: 'visible' });
    const text = await this.totalCostAmount.textContent();
    return text?.trim() ?? '';
  }

  /**
   * Get payment message text from confirm booking page.
   * @returns payment message text
   */
  async getPaymentMessage(): Promise<string> {
    console.log('Get payment message from Confirm booking page');

    await this.totalCostPaymentMessage.waitFor({ state: 'visible' });
    const text = await this.totalCostPaymentMessage.textContent();
    return text?.trim() ?? '';
  }

  /**
   * Get hotel name text from confirm booking page.
   * @returns hotel name text
   */
  async getHotelName(): Promise<string> {
    console.log('Get hotel name from Confirm booking page');

    await this.hotelName.waitFor({ state: 'visible' });
    const text = await this.hotelName.textContent();
    return text?.trim() ?? '';
  }

  /**
   * Get hotel directions text from confirm booking page.
   * @returns hotel directions text
   */
  async getHotelDirectionsText(): Promise<string> {
    console.log('Get hotel directions text from Confirm booking page');

    await this.hotelDirectionsText.waitFor({ state: 'visible' });
    const text = await this.hotelDirectionsText.textContent();
    return text?.trim() ?? '';
  }

  // ######## UI actions/navigation ########

  /** Click the continue button below the total-cost section. */
  async clickGoToHomePageButton(): Promise<void> {
    console.log('Click on Continue to homepage button');
    await this.continueButton.scrollIntoViewIfNeeded();
    await this.continueButton.click();
  }

  /** Click the City Tax notification link. */
  async clickCityTaxNotificationLink(): Promise<void> {
    console.log('Click on City Tax Notification Link');
    await this.cityTaxNotificationLink.scrollIntoViewIfNeeded();
    await this.cityTaxNotificationLink.click();
  }

  // ######## UI validations ########

  /**
   * Check we reached the current page by checking a specific element from the page.
   */
  async validatePage(): Promise<void> {
    console.log('Validate Booking confirmation page was reached');

    try {
      await this.pageLoadedIndicator.waitFor({ state: 'visible', timeout: 120000 });
    } catch {
      throw new Error('Confirm booking page did not load within 120s');
    }
  }

  /**
   * Validate booking details intro section displayed on confirm booking page.
   * @param expected expected booking details intro data
   */
  async validateBookingDetailsIntro(expected: BookingDetailsIntroExpected): Promise<void> {
    console.log('Validate booking details intro section on Confirm booking page');

    const [firstName, ...lastNameParts] = expected.guestName.trim().split(' ');
    const lastName = lastNameParts.join(' ');
    let expectedThanksForBookingMessage = await Strings.THANKS_FOR_BOOKING_MESSAGE.name;
    if (global.browser?.options?.locale === Locales.GB_EN.name) {
      expectedThanksForBookingMessage = expectedThanksForBookingMessage.replace('**[title]** ', '').replace('**[lastName]**', firstName);
    } else {
      expectedThanksForBookingMessage = expectedThanksForBookingMessage.replace('**[title]**', expected.guestTitle ?? '').replace('**[lastName]**', lastName);
    }

    await expect(this.guestNameHeading, 'Guest name should be displayed in the confirmation greeting').toContainText(expectedThanksForBookingMessage, { timeout: 10000 });
    await expect(this.bookingReference, 'Basket reference ID should match expected booking reference').toContainText(expected.bookingReference, { timeout: 10000 });
    await expect(this.hotelName, 'Hotel name should match booking confirmation details').toContainText(expected.hotelName, { timeout: 10000 });
  }

  /** Validate the booking reference shown on the confirmation page. */
  async validateBookingReference(bookingReference: string): Promise<void> {
    console.log(`Validate booking reference: ${bookingReference}`);
    await expect(this.bookingReference, 'Basket reference ID should be visible').toBeVisible();
    await expect(this.bookingReference, 'Basket reference ID should match expected booking reference').toContainText(bookingReference);
  }

  /** Validate the City Tax notification visibility and content. */
  async validateCityTaxNotification(isDisplayed: boolean): Promise<void> {
    console.log(`Validate City Tax notification displayed=${isDisplayed}`);
    if (!isDisplayed) {
      await expect(this.cityTaxNotification, 'City Tax Notification should not be visible').not.toBeVisible();
      return;
    }

    await expect(this.cityTaxNotification, 'City Tax Notification should be visible').toBeVisible();
    await expect(this.cityTaxNotification, 'City Tax Notification should have the expected text').toContainText(
      await Strings.CITY_TAX_NOTIFICATION_PI.name
    );
    await expect(this.cityTaxNotificationLink, 'City Tax Notification link should be visible').toBeVisible();
  }

  /**
   * Validate room details section displayed on confirm booking page.
   * @param expected expected room details data
   * @param roomNumber room number (1 based)
   */
  async validateRoomDetails(expected: RoomDetailsExpected, roomNumber = 1): Promise<void> {
    console.log(`Validate room details section for room ${roomNumber} on Confirm booking page`);

    await expect(this.roomDetailsSection.roomDetailsSectionContainer, 'Room details section should be displayed').toBeVisible({ timeout: 10000 });
    await expect(this.getRoomCardHeader(roomNumber), `Room ${roomNumber} card header should be displayed`).toBeVisible({ timeout: 10000 });

    const roomDates = this.getRoomCardHeaderDates(roomNumber);
    await expect(roomDates, `Room ${roomNumber} check-in date should match expected date`).toContainText(formatDateForUI(expected.checkIn), { timeout: 10000 });
    await expect(roomDates, `Room ${roomNumber} check-out date should match expected date`).toContainText(formatDateForUI(expected.checkOut), { timeout: 10000 });

    if (expected.meals) {
      const mealsSection = this.page.locator(`[data-testid="RoomCardInfo-room${roomNumber}-RightColumn-Meals-Label"]`);
      const mealsVisible = await mealsSection.isVisible().catch(() => false);
      if (mealsVisible) {
        const adultsLabel = expected.adults === 1 ? await Strings.ADULT.name : await Strings.ADULTS.name;
        await expect(mealsSection.locator('..'), `Room ${roomNumber} meals section should contain adult meal details`).toContainText(`${expected.adults ?? ''} ${adultsLabel}`.trim(), { timeout: 10000 });
        await expect(mealsSection.locator('..'), `Room ${roomNumber} meals section should contain selected meal details`).toContainText(expected.meals, { timeout: 10000 });
      } else {
        console.warn(`[Confirmation] Meals section not found for room ${roomNumber} - skipping UI meals validation`);
      }
    }
  }

  /**
   * Validate hotel directions displayed on confirm booking page.
   * @param expectedDirections expected hotel directions text
   */
  async validateHotelDirections(expectedDirections: string): Promise<void> {
    console.log('Validate hotel directions section on Confirm booking page');

    const directionsVisible = await this.hotelDirectionsLabel.isVisible().catch(() => false);
    if (!directionsVisible) {
      console.warn('[Confirmation] Hotel directions section not visible - skipping');
      return;
    }

    const actualText = await this.hotelDirectionsText.textContent() ?? '';
    const actualDirections = this.normaliseText(actualText);
    const expectedDirectionsPreview = this.normaliseText(expectedDirections).slice(0, 30);
    expect(actualDirections, `Hotel directions text should contain expected directions preview: '${expectedDirectionsPreview}'`).toContain(expectedDirectionsPreview);
  }

  /**
   * Validate total cost section displayed on confirm booking page.
   * @param expected expected total cost data
   */
  async validateTotalCost(expected: TotalCostExpected): Promise<void> {
    console.log('Validate total cost amount and payment message on Confirm booking page');

    await expect(this.totalCostContainer, 'Total cost section should be displayed').toBeVisible({ timeout: 10000 });
    await expect(this.totalCostAmount, `Total cost amount should be=${expected.amount}`).toContainText(expected.amount, { timeout: 10000 });

    await expect(this.totalCostPaymentMessage, 'Total cost payment message should match pay on arrival text').toContainText(await Strings.THANK_YOU_YOUR_PAYMENT_WILL_BE_TAKEN.name, { timeout: 10000 });
  }

  /** Validate that the continue button appears below the total-cost section. */
  async validateTotalCostContinueToHomePageButtonLocation(): Promise<void> {
    console.log('Validate that Continue to homepage button is below total cost section');
    await UiUtils.validateIsBelow({
      belowElement: this.continueButton,
      aboveElement: this.totalCostContainer,
      elementDescription: 'Continue to homepage button below Total cost section',
    });
  }

  /** Validate that room details appear below the booking-confirmation section. */
  async validateRoomDetailsSectionPosition(): Promise<void> {
    console.log('Validate the position of Room details section on Confirm booking page');
    await UiUtils.validateIsBelow({
      belowElement: this.roomDetailsSection.roomDetailsSectionContainer,
      aboveElement: this.bookingConfirmationSection,
      maxDistanceBetween: 1000,
      elementDescription: 'Room details and Booking confirmation section',
    });
    await UiUtils.validateIsBelow({
      belowElement: this.hotelDirectionsLabel,
      aboveElement: this.roomDetailsSection.roomDetailsSectionContainer,
      maxDistanceBetween: 1000,
      elementDescription: 'Room details section and Hotel directions section',
    });
  }

  /** Validate that hotel directions appear below room details and above total cost. */
  async validateHotelDirectionsSectionPosition(): Promise<void> {
    console.log('Validate the position of Hotel directions section on Confirm booking page');
    await UiUtils.validateIsBelow({
      belowElement: this.hotelDirectionsSectionContainer,
      aboveElement: this.roomDetailsSection.roomDetailsSectionContainer,
      maxDistanceBetween: 1000,
      elementDescription: 'Hotel directions section below Room details section',
    });
    await UiUtils.validateIsBelow({
      belowElement: this.totalCostContainer,
      aboveElement: this.hotelDirectionsSectionContainer,
      maxDistanceBetween: 1000,
      elementDescription: 'Total cost section below Hotel directions section',
    });
  }

  /** Validate that the charitable pledge sits between directions and total cost. */
  async validatePositionOfCharitablePledge(): Promise<void> {
    console.log('Validate position of On-line charitable pledge between hotel directions and total cost');
    await UiUtils.validateIsBelow({
      belowElement: this.onlineCharitablePledgeLabel,
      aboveElement: this.hotelDirectionsSectionContainer,
      maxDistanceBetween: 1000,
      elementDescription: 'Donations label below Hotel directions',
    });
    await UiUtils.validateIsBelow({
      belowElement: this.totalCostLabel,
      aboveElement: this.onlineCharitablePledgeLabel,
      maxDistanceBetween: 700,
      elementDescription: 'Total cost label below Donations label',
    });
    await UiUtils.validateIsBelow({
      belowElement: this.donationsAmountLabel,
      aboveElement: this.hotelDirectionsSectionContainer,
      maxDistanceBetween: 1000,
      elementDescription: 'Donations amount below Hotel directions',
    });
    await UiUtils.validateIsBelow({
      belowElement: this.totalCostAmount,
      aboveElement: this.donationsAmountLabel,
      maxDistanceBetween: 700,
      elementDescription: 'Total cost amount below Donations amount',
    });
  }

  // ######## Private helpers ########

  /**
   * Returns room card header of specific room from confirm booking page.
   * @param roomNumber room number (1 based)
   */
  private getRoomCardHeader(roomNumber: number): Locator {
    return this.page.locator(`[data-testid="RoomCardHeader-room${roomNumber}"]`);
  }

  /**
   * Returns room card header dates of specific room from confirm booking page.
   * @param roomNumber room number (1 based)
   */
  private getRoomCardHeaderDates(roomNumber: number): Locator {
    return this.page.locator(`[data-testid="RoomCardHeader-room${roomNumber}-Dates"]`);
  }

  private normaliseText(value: string): string {
    return value
      .replace(/<[^>]*>/g, ' ')
      .replace(/&nbsp;/gi, ' ')
      .replace(/&pound;/gi, 'gbp')
      .replace(/&amp;/gi, '&')
      .replace(/\s+/g, ' ')
      .trim()
      .toLowerCase();
  }
}