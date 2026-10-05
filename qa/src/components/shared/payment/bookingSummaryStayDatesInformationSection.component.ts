import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';
import { Constants } from '@test-data/constants';
import { ApiHelpers } from '@api/apiHelpers';
import { type BookingInformation } from '@api/response/bookingInformation';

/**
 * The booking summary stay-dates information section present on the Payment page vertical strip
 * containing the UI elements, custom actions and validations. Mirrors qa/reference
 * `components/common/payment/bookingSummaryStayDatesInformationSection.js`.
 */
export class PaymentBookingSummaryStayDatesSectionComponent {
  private readonly page: Page = global.page;

  private get resolutionId(): string {
    return Constants.RESOLUTION_IDENTIFIER_FOR_DATATESTID_ATTRIBUTE;
  }

  // ######## UI elements/properties ########

  get sectionContainer(): Locator {
    return this.page.locator(`div[data-testid="BookingSummary-${this.resolutionId}-StayDatesInformation-Wrapper"]`);
  }

  get arrivingDateLabel(): Locator {
    return this.sectionContainer.locator(`p[data-testid="BookingSummary-${this.resolutionId}-StayDatesInformation-ArrivalDate"]`);
  }

  get departureDateLabel(): Locator {
    return this.sectionContainer.locator(`p[data-testid="BookingSummary-${this.resolutionId}-StayDatesInformation-DepartureDate"]`);
  }

  get numberOfNightsLabel(): Locator {
    return this.sectionContainer.locator(`p[data-testid="BookingSummary-${this.resolutionId}-StayDatesInformation-NightsNumber"]`);
  }

  get stayLabel(): Locator {
    return this.sectionContainer.locator('> div').nth(2).locator('p');
  }

  get checkingInLabel(): Locator { return this.sectionContainer.locator('> div').nth(0).locator('p').first(); }
  get checkingOutLabel(): Locator { return this.sectionContainer.locator('> div').nth(1).locator('p').first(); }

  // ######## UI actions/navigation ########

  // ######## UI validations ########

  /** Validate the 'Stay' section label and arrival/departure dates. */
  async validateStaySection({ arrivalDate, departureDate }: { arrivalDate?: string; departureDate?: string } = {}): Promise<void> {
    console.log('Validate Stay section from Booking summary panel');
    await this.stayLabel.scrollIntoViewIfNeeded();
    await expect(this.stayLabel, 'Stay label text').toContainText(await Strings.STAY.name);
    if (arrivalDate) {
      await expect(this.arrivingDateLabel, 'Arrival date').toContainText(arrivalDate);
    }
    if (departureDate) {
      await expect(this.departureDateLabel, 'Departure date').toContainText(departureDate);
    }
  }

  /** Validate the checking-in label and displayed arrival date. */
  async validateCheckingInSection(checkInDate: string): Promise<void> {
    console.log(`Validate CheckingIn section using arrival date: ${checkInDate}`);
    await this.checkingInLabel.scrollIntoViewIfNeeded();
    await expect(this.checkingInLabel, 'Checking in label').toContainText(await Strings.CHECKING_IN.name);
    await expect(this.arrivingDateLabel, 'Arrival date').toContainText(this.formatDate(checkInDate));
  }

  /** Validate the checking-out label and displayed departure date. */
  async validateCheckingOutSection(checkOutDate: string): Promise<void> {
    console.log(`Validate CheckingOut section using departure date: ${checkOutDate}`);
    await this.checkingOutLabel.scrollIntoViewIfNeeded();
    await expect(this.checkingOutLabel, 'Checking out label').toContainText(await Strings.CHECKING_OUT.name);
    await expect(this.departureDateLabel, 'Departure date').toContainText(this.formatDate(checkOutDate));
  }

  /** Validate stay dates using the booking-information API response. */
  async validateData(bookingInformation: BookingInformation): Promise<void> {
    console.log(`Validate booking summary stay dates: ${JSON.stringify(bookingInformation)}`);
    const reservation = (bookingInformation.reservationByIdList?.[0] ?? {}) as { roomStay?: { arrivalDate?: string; departureDate?: string } };
    const roomStay = reservation.roomStay ?? {};
    await this.validateCheckingInSection(String(roomStay.arrivalDate ?? ''));
    await this.validateCheckingOutSection(String(roomStay.departureDate ?? ''));
    const nights = await ApiHelpers.getNumberOfStayNightsBasedOnBookingInformation(bookingInformation);
    await this.validateStaySection();
    await expect(this.numberOfNightsLabel, 'Number of nights explanation label').toContainText(`${nights} ${nights === 1 ? await Strings.NIGHT.name : await Strings.NIGHTS.name}`);
  }

  private formatDate(date: string): string {
    const locale = String((global.browser?.options as { locale?: string } | undefined)?.locale ?? '').toLowerCase();
     const localeTag = locale.startsWith('de') ? 'de-DE' : 'en-GB';
     return new Intl.DateTimeFormat(localeTag, { weekday: 'long', day: 'numeric', month: 'long' }).format(new Date(`${date}T12:00:00`));
  }
}
