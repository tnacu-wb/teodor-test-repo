import { type Page, type Locator, expect } from '@playwright/test';
import { Constants } from '@test-data/constants';
import { Strings } from '@test-data/strings';
import { type BookingInformation } from '@api/response/bookingInformation';

/**
 * The booking summary rate information section present on the Payment page vertical strip
 * containing the UI elements, custom actions and validations. Mirrors qa/reference
 * `components/common/payment/bookingSummaryRateInformationSection.js`.
 */
export class PaymentBookingSummaryRateInformationSectionComponent {
  private readonly page: Page = global.page;

  private get resolutionId(): string {
    return Constants.RESOLUTION_IDENTIFIER_FOR_DATATESTID_ATTRIBUTE;
  }

  // ######## UI elements/properties ########

  get rateTitleLabel(): Locator {
    return this.page.locator(`div[data-testid="BookingSummary-${this.resolutionId}-RateInformation-Wrapper"] > p[data-testid="BookingSummary-${this.resolutionId}-RateInformation-Label"]`);
  }

  get rateExplanationLabel(): Locator {
    return this.page.locator(`div[data-testid="BookingSummary-${this.resolutionId}-RateInformation-Wrapper"] > p[data-testid="BookingSummary-${this.resolutionId}-RateInformation-StayInfo"]`);
  }

  // ######## UI actions/navigation ########

  // ######## UI validations ########

  /** Validate the rate title label matches the expected rate name (e.g. "Flex"). */
  async validateRateTitleLabel(rateName: string): Promise<void> {
    console.log('Validate rate title label is correct');
    await this.rateTitleLabel.scrollIntoViewIfNeeded();
    await expect(this.rateTitleLabel, 'Payment booking summary rate title').toContainText(rateName);
  }

  /** Validate the rate explanation workaround state preserved from the reference. */
  async validateRateExplanationText({ numberOfRooms, numberOfNights }: { numberOfRooms: number; numberOfNights: number }): Promise<void> {
    console.log(`Validate rate explanation text for ${numberOfRooms} rooms and ${numberOfNights} nights`);
    await expect(this.rateExplanationLabel, 'Rate explanation label workaround').toBeHidden();
  }

  /** Validate rate information against booking-information data. */
  async validateData(bookingInformation: BookingInformation): Promise<void> {
    console.log(`Validate booking summary rate information: ${JSON.stringify(bookingInformation)}`);
    const reservations = (bookingInformation.reservationByIdList ?? []) as Array<{ roomStay?: { rateExtraInfo?: { rateName?: string }; arrivalDate?: string; departureDate?: string } }>;
    const isHubHotel = String(bookingInformation.bookingFlowId ?? '').toLowerCase().includes('hub');
    for (const reservation of reservations) {
      await this.validateRateTitleLabel(`${await Strings.RATE.name}: ${isHubHotel ? 'Hub ' : ''}${reservation.roomStay?.rateExtraInfo?.rateName ?? ''}`);
    }
    const stay = reservations[0]?.roomStay;
    const nights = stay?.arrivalDate && stay.departureDate
      ? Math.round((new Date(`${stay.departureDate}T12:00:00`).getTime() - new Date(`${stay.arrivalDate}T12:00:00`).getTime()) / 86400000)
      : 0;
    await this.validateRateExplanationText({ numberOfRooms: reservations.length, numberOfNights: nights });
  }
}
