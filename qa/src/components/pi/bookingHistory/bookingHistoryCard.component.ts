import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';
import { BookingInformationCardComponent } from '../../shared/manageBooking/bookingInformationCard.component';

/**
 * Booking card section on the PI Booking History page containing the UI elements, custom actions
 * and validations. Mirrors qa/reference `components/opera/bookingHistory/bookingHistoryCard.js`
 * (extends `components/common/bookingInformationCardSectionBase.js`, whose PI-relevant surface is
 * already covered by the shared `BookingInformationCardComponent` used by `ManageBookingPage` -
 * composed here rather than duplicated).
 */
export class BookingHistoryCardComponent {
  private readonly page: Page = global.page;

  readonly bookingInformationCard: BookingInformationCardComponent = new BookingInformationCardComponent();

  // ######## UI elements/properties ########

  readonly bookingInfoCardContainer: Locator = this.page.locator('div[data-testid="BookingInfoCardContainer"]');
  readonly resendConfirmationLink: Locator = this.page.locator('div[data-testid="BookingActions-Links"] div p');
  readonly resendInvoiceButton: Locator = this.page.locator('div[data-testid="BookingActions-Links"] div p');

  // ######## UI actions/navigation ########

  // ######## UI validations ########

  /** Validate the booking actions section (reference label, resend confirmation/invoice links). */
  async validateBookingActionsFromBIC({
    resendConfirmation,
    resendInvoice,
  }: { resendConfirmation?: boolean; resendInvoice?: boolean } = {}): Promise<void> {
    await expect(this.bookingInformationCard.bookingReferenceLabel, 'Booking reference value').toBeVisible();
    if (resendConfirmation) {
      await expect(this.resendConfirmationLink, 'Resend confirmation link').toContainText(await Strings.BOOKING_RESEND_CONFIRMATION_LABEL.name);
    }
    if (resendInvoice) {
      await expect(this.resendInvoiceButton, 'Resend invoice link').toBeVisible();
    }
  }

  /** Validate the Booking Information Card is displayed. */
  async validateBicIsDisplayed(): Promise<void> {
    await expect(this.bookingInfoCardContainer, 'Booking information card').toBeVisible();
  }
}
