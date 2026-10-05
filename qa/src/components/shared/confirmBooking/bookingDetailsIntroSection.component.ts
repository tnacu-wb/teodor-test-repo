import { type Locator, type Page } from '@playwright/test';

/** Booking details intro section on the confirmation page. */
export class BookingDetailsIntroSectionComponent {
  private readonly page: Page = global.page;
  // ######## UI elements/properties ########
  readonly thanksForBookingEmailLabel: Locator = this.page.locator('[data-testid="ThanksForBooking-Email"]');
  readonly thanksForBookingTitleLabel: Locator = this.page.locator('[data-testid="ThanksForBooking-Title-Name"]');
  readonly bookingReferenceDetailsLabel: Locator = this.page.locator('[data-testid="BookingReferenceDetails-Label"]');
  readonly bookingReferenceDetailsId: Locator = this.page.locator('[data-testid="BookingReferenceDetails-Id"]');
  readonly bookingReferenceDetailsHotelNameLabel: Locator = this.page.locator('[data-testid="BookingReferenceDetails-Hotel"]');
  readonly bookingReferenceDetailsHotelAddressLabel: Locator = this.page.locator('[data-testid="BookingReferenceDetails-HotelAdress"]');
  readonly bookingReferenceDetailsHotelTelephoneLabel: Locator = this.page.locator('[data-testid="BookingReferenceDetails-HotelPhone"]');

  // ######## UI actions/navigation ########

  // ######## UI validations ########
}