import { type Page, type Locator, expect } from '@playwright/test';
import { ApiContentCalls } from '../../../api';
import type { PmsRoomType } from '../../../test-data/pmsRoomTypes';
import { Strings } from '../../../test-data/strings';
import { HotelRates } from '../../../test-data/hotelRates';
import { Locales } from '../../../test-data/locales';

/**
 * Booking Information card section that is part of the Manage Booking and Booking History page.
 * Mirrors qa/reference test/pages/pi/manageBooking.page.js (BIC-specific getters/validations)
 * plus the PI-relevant subset of qa/reference test/pages/components/common/bookingInformationCardSectionBase.js.
 */
export class BookingInformationCardComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly wrapper: Locator = this.page.locator('[data-testid="BookingInfoCardHeader-Wrapper"]');
  readonly title: Locator = this.page.locator('[data-testid="BookingInfoCardHeader-Title"]');
  readonly hotelName: Locator = this.page.locator('[data-testid="BookingInfoCardHeader-HotelName"]');
  readonly checkInLabel: Locator = this.wrapper.locator('div').first().locator('h6').first();
  readonly checkInDate: Locator = this.page.locator('[data-testid="BookingInfoCardHeader-CheckInDate"]');
  readonly checkOutLabel: Locator = this.wrapper.locator('div').nth(1).locator('h6').first();
  readonly checkOutDate: Locator = this.page.locator('[data-testid="BookingInfoCardHeader-CheckOutDate"]');
  readonly bookingReferenceLabel: Locator = this.page.locator('[data-testid="BookingActions-BookingReference"] p:last-child');
  readonly bookingUpcomingStatusLabel: Locator = this.page.locator('[data-testid="BookingActions-Links"] > p');
  readonly hotelDetailsImage: Locator = this.page.locator('img[data-testid="hotel-thumbnail"]');
  readonly hotelDetailsAddressLabel: Locator = this.page.locator('[data-testid="HotelDetails-Address"]');
  readonly hotelDetailsParkingInfoLabel: Locator = this.page.locator('[data-testid="HotelDetails-ParkingInformation"]');
  readonly charitablePledgeLabel: Locator = this.page.locator('[data-testid="charityLabel"]');
  readonly amendCancelPoliciesLabel: Locator = this.page.locator('[data-testid="BookingDetailsControllerReasonLabel"]');
  readonly amendButton: Locator = this.page.locator('[data-testid="BookingDetailsController-AmendButton"]');
  readonly cancelButton: Locator = this.page.locator('[data-testid="BookingDetailsController-CancelButton"]');

  /**
   * Returns the ECI/LCO element for a given room.
   * @param eciLcoName - extras name
   * @param roomNumber - room number (0-based)
   */
  getEciLcoLabel(eciLcoName: string, roomNumber: number): Locator {
    return this.page.locator(`//span[@data-testid="roomNumber-${roomNumber + 1}"]//following-sibling::div//h6[@data-testid="extras-name" and contains(text(), '${eciLcoName}')]`);
  }

  /**
   * Returns the ECI/LCO price element for a given room.
   * @param roomNumber - room number (0-based)
   */
  getEciLcoPrice(roomNumber: number): Locator {
    return this.page.locator(`//span[@data-testid="roomNumber-${roomNumber + 1}"]//following-sibling::div//div[@data-testid="extras-price"]//h6`);
  }

  // ######## UI actions/navigation ########

  /**
   * Click the "Amend Booking" button to initiate the amendment flow.
   */
  async clickAmendButton(): Promise<void> {
    console.log('Clicking Amend Booking button');
    // Try data-testid first, fall back to button text
    const amendByTestId = this.amendButton;
    const amendByText = this.page.locator('button', { hasText: await Strings.AMEND.name });

    const testIdVisible = await amendByTestId.isVisible().catch(() => false);
    const target = testIdVisible ? amendByTestId : amendByText;

    await target.waitFor({ state: 'visible' });
    await expect(target, 'Amend Booking button should be enabled').toBeEnabled();
    await target.scrollIntoViewIfNeeded();
    await target.click();
  }

  /**
   * Click the "Cancel Booking" button to initiate the cancellation flow.
   */
  async clickCancelButton(): Promise<void> {
    console.log('Clicking Cancel Booking button');
    // Try data-testid first, fall back to button text
    const cancelByTestId = this.cancelButton;
    const cancelByText = this.page.getByRole('button', { name: await Strings.CANCEL_BOOKING.name });

    const testIdVisible = await cancelByTestId.isVisible().catch(() => false);
    const target = testIdVisible ? cancelByTestId : cancelByText;

    await target.waitFor({ state: 'visible' });
    await target.scrollIntoViewIfNeeded();
    await target.click();
  }

  // ######## UI validations ########

  /**
   * Validate that the booking information card is displayed.
   */
  async validateVisible(): Promise<void> {
    console.log('Validate booking information card is visible');
    await expect(this.wrapper, 'Booking information card wrapper should be visible').toBeVisible({ timeout: 30000 });
  }

  /**
   * Validate the booking reference displayed on the card.
   * Uses partial text match (toContainText) for flexible validation.
   * @param expectedReference - the expected booking reference string
   */
  async validateBookingReference(expectedReference: string): Promise<void> {
    console.log(`Validating booking reference contains: ${expectedReference}`);
    await this.bookingReferenceLabel.waitFor({ state: 'visible' });
    await expect(this.bookingReferenceLabel, `Booking reference should contain "${expectedReference}"`).toContainText(expectedReference);
  }

  /**
   * Validate the booking reference ID displayed on the card (exact match).
   * Used after re-searching a booking (e.g. post-cancellation) to confirm the correct booking is shown.
   * @param expectedReference - the exact expected booking reference string
   */
  async validateBookingReferenceId(expectedReference: string): Promise<void> {
    console.log(`Validating booking reference ID is: ${expectedReference}`);
    await this.bookingReferenceLabel.waitFor({ state: 'visible' });
    await expect(this.bookingReferenceLabel, `Booking reference ID should be exactly "${expectedReference}"`).toHaveText(expectedReference);
  }

  /**
   * Validate the booking status displayed in the booking-actions section.
   * @param expectedLabel - expected booking status text
   */
  async validateBookingInformationCardStatus(expectedLabel: string): Promise<void> {
    console.log(`Validate the booking status card label is: ${expectedLabel}`);
    await expect(this.bookingUpcomingStatusLabel, 'Booking status label should be visible').toBeVisible();
    await expect(this.bookingUpcomingStatusLabel, `Booking status label should contain "${expectedLabel}"`).toContainText(expectedLabel);
  }

  /**
   * Validate the card hotel image source and visibility.
   * @param options - expected image state
   * @param options.hotelImgUrl - image URL fragment expected in the image source
   * @param options.isDisplayed - whether the hotel image should be visible
   */
  async validateHotelImageURL({ hotelImgUrl, isDisplayed = true }: { hotelImgUrl: string; isDisplayed?: boolean }): Promise<void> {
    console.log(`Validate hotel image isDisplayed=${isDisplayed}`);
    if (!isDisplayed) {
      await expect(this.hotelDetailsImage, 'Hotel image should not be visible').not.toBeVisible();
      return;
    }

    await expect(this.hotelDetailsImage, 'Hotel image should be visible').toBeVisible();
    const source = await this.hotelDetailsImage.getAttribute('src');
    expect(source, 'Hotel image source should be present').not.toBeNull();
    expect(decodeURIComponent(source ?? ''), `Hotel image source should include ${hotelImgUrl}`).toContain(hotelImgUrl);
  }

  /**
   * Validate the hotel address rendered in the booking card.
   * @param expectedAddress - expected address text
   * @param isDisplayed - whether the address should be visible
   */
  async validateHotelAddress(expectedAddress: string, isDisplayed = true): Promise<void> {
    console.log(`Validate hotel address isDisplayed=${isDisplayed}`);
    if (!isDisplayed) {
      await expect(this.hotelDetailsAddressLabel, 'Hotel address should not be visible').not.toBeVisible();
      return;
    }

    await expect(this.hotelDetailsAddressLabel, 'Hotel address should be visible').toBeVisible();
    await expect(this.hotelDetailsAddressLabel, `Hotel address should contain "${expectedAddress}"`).toContainText(expectedAddress);
  }

  /**
   * Validate the hotel parking text rendered in the booking card.
   * @param hotelParking - parking HTML/text returned by the hotel API
   * @param isDisplayed - whether the parking text should be visible
   */
  async validateHotelParkingInfo(hotelParking: string, isDisplayed = true): Promise<void> {
    console.log(`Validate hotel parking isDisplayed=${isDisplayed}`);
    if (!isDisplayed) {
      await expect(this.hotelDetailsParkingInfoLabel, 'Hotel parking information should not be visible').not.toBeVisible();
      return;
    }

    const parkingDescription = hotelParking.trim().replace(/<([^>]+)>/g, '').replace(/&nbsp;/g, ' ').replace(/\u00a0/g, ' ');
    await expect(this.hotelDetailsParkingInfoLabel, 'Hotel parking information should be visible').toBeVisible();
    await expect(this.hotelDetailsParkingInfoLabel, `Hotel parking information should contain "${parkingDescription}"`).toContainText(parkingDescription);
  }

  /**
   * Validate the hotel detail portion of the booking card.
   * @param options - expected hotel details
   */
  async validateBookingHotelInfo({ isDisplayed = true, hotelThumbnail, hotelAddress, hotelParking }: {
    isDisplayed?: boolean;
    hotelThumbnail: string;
    hotelAddress: string;
    hotelParking: string;
  }): Promise<void> {
    console.log(`Validate booking hotel information isDisplayed=${isDisplayed}`);
    await this.validateHotelImageURL({ hotelImgUrl: hotelThumbnail, isDisplayed });
    await this.validateHotelAddress(hotelAddress, isDisplayed);
    await this.validateHotelParkingInfo(hotelParking, isDisplayed);
  }

  /**
   * Validate the charitable pledge label using the value supplied by AEM.
   * @param expectedLabel - expected charitable pledge text
   */
  async validateCharitablePledgeLabel(expectedLabel: string): Promise<void> {
    console.log(`Validate charitable pledge label=${expectedLabel}`);
    await expect(this.charitablePledgeLabel, 'Charitable pledge label should be visible').toBeVisible();
    await expect(this.charitablePledgeLabel, `Charitable pledge label should contain "${expectedLabel}"`).toContainText(expectedLabel);
  }

  /**
   * Validate the amend and cancel policy text shown for the booking.
   * @param cancelPolicies - Policy text returned by the dashboard dictionary.
   * @param isDisplayed - Whether the policy label should be displayed.
   */
  async validateAmendCancelPoliciesText(cancelPolicies: string, isDisplayed = true): Promise<void> {
    console.log(`Validate Amend and Cancel Policies text for cancel policies=${cancelPolicies}`);
    await this.validateVisible();

    if (!isDisplayed) {
      await expect(this.amendCancelPoliciesLabel, 'Amend and cancel policy label should not be visible').not.toBeVisible();
      return;
    }

    const policyText = cancelPolicies
      .replace(/<([^>]+)>/g, '')
      .replace(/&nbsp;/g, ' ')
      .replace(/\u00a0/g, '')
      .trim();
    await expect(this.amendCancelPoliciesLabel, 'Amend and cancel policy text should be displayed').toContainText(policyText);
  }

  /**
   * Validate the card displays the expected details.
   * @param expected - the expected values to validate against
   * @param expected.hotelName - expected hotel name
   * @param expected.dates - expected date text (e.g. check-in or check-out)
   * @param expected.guests - expected guest information text
   */
  async validateDetails(expected: { hotelName?: string; dates?: string; guests?: string }): Promise<void> {
    console.log(`Validate booking details=${JSON.stringify(expected)}`);
    await this.wrapper.waitFor({ state: 'visible' });

    if (expected.hotelName) {
      await expect(this.hotelName, `Hotel name should contain "${expected.hotelName}"`).toContainText(expected.hotelName);
    }

    if (expected.dates) {
      await expect(this.wrapper, `Booking information card should contain "${expected.dates}"`).toContainText(expected.dates);
    }

    if (expected.guests) {
      // Guest info is typically within the room information section
      const roomInfo = this.page.locator('[data-testid="roomType"]');
      await expect(roomInfo.first(), `Room information should contain "${expected.guests}"`).toContainText(expected.guests);
    }
  }

  /**
   * Validate that the card shows a cancelled status: the card remains visible but the
   * amend/cancel action buttons are absent.
   */
  async validateCancelledStatus(): Promise<void> {
    console.log('Validating booking is in cancelled status');
    await this.wrapper.waitFor({ state: 'visible' });
    await expect(this.amendButton, 'Amend button should not be visible for cancelled booking').not.toBeVisible();
    await expect(this.cancelButton, 'Cancel button should not be visible for cancelled booking').not.toBeVisible();
  }

  /**
   * Validate the booking information title.
   */
  async validateBookingInformationTitle(): Promise<void> {
    console.log('Validate booking information title');
    const bookingInformationLabelAem = await Strings.BOOKING_INFORMATION_TITLE.name;
    console.log(`Validate the Booking information title using: ${bookingInformationLabelAem}`);
    await expect(this.title, 'Booking information title should be visible').toBeVisible();
    await expect(this.title, 'Booking information title').toHaveText(bookingInformationLabelAem);
  }

  /**
   * Validate the hotel name label.
   * @param expectedText - expected hotel name
   */
  async validateHotelNameLabel(expectedText: string): Promise<void> {
    console.log(`Validate hotel name=${expectedText}`);
    await expect(this.hotelName, 'Hotel name label should be visible').toBeVisible();
    await expect(this.hotelName, `Hotel name label should contain "${expectedText}"`).toContainText(expectedText);
  }

  /**
   * Validate the room type label using an explicit AEM label or PMS room type information.
   * @param options.roomIndex - zero-based room index
   * @param options.roomTypeLabelAem - expected room type label from AEM
   * @param options.pmsRoomType - PMS room type object used to fetch room type information
   * @param options.pmsRoomTypeCode - exact PMS room type code used to select the API room type label
   */
  async validateRoomTypeLabel({ roomIndex = 0, roomTypeLabelAem, pmsRoomType, pmsRoomTypeCode }: {
    roomIndex?: number;
    roomTypeLabelAem?: string;
    pmsRoomType?: PmsRoomType;
    pmsRoomTypeCode?: string;
  } = {}): Promise<void> {
    let expectedRoomTypeLabel = roomTypeLabelAem;

    if (!expectedRoomTypeLabel && pmsRoomType) {
      const roomTypeInformation = await ApiContentCalls.graphqlGetManageBookingRoomTypeInformation({ pmsRoomType });
      const roomTypes = Array.isArray(roomTypeInformation.roomTypes) ? roomTypeInformation.roomTypes : [];
      const roomType = pmsRoomTypeCode
        ? roomTypes.find((roomTypeItem) => String(roomTypeItem.roomTypeCode ?? '').includes(pmsRoomTypeCode))
        : roomTypes[0];
      if (!roomType) {
        throw new Error(`Room type information should contain PMS room type ${pmsRoomTypeCode}`);
      }
      expectedRoomTypeLabel = String(roomType.roomLabel ?? '');
    }

    expectedRoomTypeLabel ||= await Strings.DOUBLE_ROOM.name;
    console.log(`Validate the Room type label using: ${expectedRoomTypeLabel}`);
    const roomTypeLabel = this.page.locator(`span[data-testid="roomNumber-${roomIndex + 1}"] + div p[data-testid="roomType"]`);
    await expect(roomTypeLabel, 'Room type label should be displayed').toBeVisible();
    await expect(roomTypeLabel, 'Room type label does not match expected value').toContainText(expectedRoomTypeLabel, { ignoreCase: true });
  }

  /**
   * Validate the Check in label.
   */
  async validateCheckInLabel(): Promise<void> {
    console.log('Validate check-in label');
    const checkInLabelAem = await Strings.CHECK_IN_LABEL.name;
    console.log(`Validate the Check in label using: ${checkInLabelAem}`);
    await expect(this.checkInLabel, 'Check in label should be visible').toBeVisible();
    await expect(this.checkInLabel, 'Check in label').toHaveText(checkInLabelAem);
  }

  /**
   * Validate the Check out label.
   */
  async validateCheckOutLabel(): Promise<void> {
    console.log('Validate check-out label');
    const checkOutLabelAem = await Strings.CHECK_OUT_LABEL.name;
    console.log(`Validate the Check out label using: ${checkOutLabelAem}`);
    await expect(this.checkOutLabel, 'Check out label should be visible').toBeVisible();
    await expect(this.checkOutLabel, 'Check out label').toHaveText(checkOutLabelAem);
  }

  /**
   * Validate Check in date & time.
   * @param arrivalDate - expected arrival date/time text
   */
  async validateCheckInDate(arrivalDate: string): Promise<void> {
    console.log(`Validate check-in date=${arrivalDate}`);
    await expect(this.checkInDate, 'Check in date and time label should be visible').toBeVisible();
    await expect(this.checkInDate, `Check in date and time should contain "${arrivalDate}"`).toContainText(arrivalDate);
  }

  /**
   * Validate Check out date & time.
   * @param departureDate - expected departure date/time text
   */
  async validateCheckOutDate(departureDate: string): Promise<void> {
    console.log(`Validate check-out date=${departureDate}`);
    await expect(this.checkOutDate, 'Check out date and time label should be visible').toBeVisible();
    await expect(this.checkOutDate, `Check out date and time should contain "${departureDate}"`).toContainText(departureDate);
  }

  /**
   * Validate Early Check-in and Late check-out is displayed with their prices.
   * @param options - the ECI/LCO details to validate
   * @param options.eciLcoName - ECI/LCO naming
   * @param options.extrasPrice - ECI/LCO price
   * @param options.currency - ECI/LCO currency
   * @param options.roomIndex - room number (0-based)
   */
  async validateEciLcoWithPrices(options: { eciLcoName: string; extrasPrice: number; currency: string; roomIndex: number }): Promise<void> {
    console.log('Validate Early Check-in and Late check-out is displayed with their prices');
    const label = this.getEciLcoLabel(options.eciLcoName, options.roomIndex);
    const price = this.getEciLcoPrice(options.roomIndex);
    await expect(label, 'ECI/LCO name label should be visible').toBeVisible();
    await expect(price, 'ECI/LCO price label should be visible').toBeVisible();
    await expect(price, 'ECI/LCO price and currency').toHaveText(await Locales.formatPriceCurrency(options.extrasPrice, options.currency));
  }

  /**
   * Validate the amend and cancel buttons. Cancel is only shown for cancellable rate plans
   * (Flex, Semi-Flex, Advance); Standard/Non-Flex rates never show the cancel button.
   * @param isDisplayed - whether the action buttons are expected to be displayed at all
   * @param ratePlanCode - the booking's rate plan code, used to decide the cancel button visibility
   */
  async validateAmendCancelButtonAreDisplayed(isDisplayed: boolean, ratePlanCode: string): Promise<void> {
    console.log(`Validate amend/cancel buttons isDisplayed=${isDisplayed} ratePlanCode=${ratePlanCode}`);
    if (isDisplayed) {
      const isCancellableRate = [HotelRates.PI_FLEX.ratePlanCode, HotelRates.PI_SEMI_FLEX.ratePlanCode, HotelRates.PI_ADVANCE.ratePlanCode].includes(ratePlanCode);

      if (isCancellableRate) {
        await expect(this.cancelButton, 'Cancel booking button should be displayed').toBeVisible();
        await expect(this.cancelButton, 'Cancel booking button label').toContainText(await Strings.CONFIRM_CANCEL_BOOKING.name);
      } else {
        await expect(this.cancelButton, `Cancel booking button should not be displayed for ${ratePlanCode} rate`).not.toBeVisible();
      }

      await expect(this.amendButton, 'Amend booking button should be visible').toBeVisible();
      await expect(this.amendButton, 'Amend booking button label').toContainText(await Strings.AMEND.name);
    } else {
      await expect(this.amendButton, 'Amend button should not be visible').not.toBeVisible();
      await expect(this.cancelButton, 'Cancel button should not be visible').not.toBeVisible();
    }
  }
}
