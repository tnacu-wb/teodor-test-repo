import { expect, type Locator } from '@playwright/test';
import { GuestData } from '../../test-data/guestData';
import { Strings } from '../../test-data/strings';
import { BasePage } from '../shared/base.page';

/**
 * Confirmation-Error page from Opera environment containing the UI elements, custom actions and validations.
 */
export class ConfirmationErrorPage extends BasePage {
  // ######## properties ########

  readonly url = 'confirmation';

  // ######## UI elements/properties ########

  readonly failConfirmationTitle: Locator = this.page.locator('p[data-testid="FailConfirmation-Title-Name"]');
  readonly failConfirmationEmailLabel: Locator = this.page.locator('p[data-testid="FailConfirmation-Email"]');
  readonly failConfirmationBookingErrorMessageLabel: Locator = this.page.locator('p[data-testid="FailConfirmation-BookingErrorMsgPOA"]');
  readonly referenceLabel: Locator = this.page.locator('p[data-testid="FailConfirmation-Label"]');

  // UI components

  // ######## UI actions/navigation ########

  /**
   * Loads the page handling cookies and the needed authorization.
   * @param reservationInfo Contains details to open the confirmation page.
   */
  async open(reservationInfo: unknown): Promise<void> {
    console.log('Open CCUI confirmation error page');
    const reservationId = this.getReservationId(reservationInfo);
    await this.openLocalizedPath(`${this.url}?reservationId=${reservationId}`);
  }

  // ######## UI validations ########

  /** Validate the fail confirmation error title. */
  async validateFailConfirmationTitle(): Promise<void> {
    console.log('Validate the fail confirmation title');
    await this.failConfirmationTitle.scrollIntoViewIfNeeded();
    await expect(this.failConfirmationTitle, 'The fail confirmation title').toBeVisible();
    await expect(this.failConfirmationTitle, 'The fail confirmation title text').toHaveText(await Strings.BOOKING_CONFIRMATION_SORRY.name);
  }

  /**
   * Validate the fail confirmation email label.
   * @param paymentOption The selected payment option.
   * @param emailAddress The email address.
   */
  async validateFailConfirmationEmailLabel(
    paymentOption: string,
    emailAddress = GuestData.DEFAULT_GUEST.emailAddress,
  ): Promise<void> {
    console.log('Validate the fail confirmation email label');
    const expectedConfirmationEmailLabel = `${await Strings.BOOKING_CONFIRMATION_EMAIL.name}`
      .replace('[emailAddress]', emailAddress.concat(' '))
      .replace('withinthe', 'within the');
    const expectedErrorMessagePayNowLabel = await Strings.CONFIRMATION_BOOKING_ERROR_MESSAGE_PAY_NOW_CCUI.name;
    const expectedLabel = expectedConfirmationEmailLabel.concat(' ', expectedErrorMessagePayNowLabel);
    await expect(this.failConfirmationEmailLabel, 'The fail confirmation email label').toBeVisible();
    await expect(this.failConfirmationEmailLabel, 'The fail confirmation email label text').toHaveText(
      paymentOption === await Strings.PAY_NOW_CCUI.name ? expectedLabel : expectedConfirmationEmailLabel,
    );
  }

  /** Validate the fail confirmation message for pay on arrival. */
  async validateFailConfirmationPOAMessage(): Promise<void> {
    console.log('Validate fail confirmation message for pay on arrival');
    await expect(this.failConfirmationBookingErrorMessageLabel, 'The fail confirmation error message for pay on arrival').toBeVisible();
    await expect(this.failConfirmationBookingErrorMessageLabel, 'The fail confirmation POA message text').toHaveText(await Strings.CONFIRMATION_BOOKING_ERROR_MESSAGE_RESERVED_WITH_CARD_CCUI.name);
  }

  /**
   * Validate the fail confirmation label containing an email address.
   * @param data Validation data.
   */
  async validateFailConfirmationLabel({
    isEmailOptionSelected = false,
    paymentOption,
    emailAddress = GuestData.DEFAULT_GUEST.emailAddress,
  }: { isEmailOptionSelected?: boolean; paymentOption?: string; emailAddress?: string } = {}): Promise<void> {
    console.log('Validate the fail confirmation email label');
    const resolvedPaymentOption = paymentOption ?? await Strings.PAY_ON_ARRIVAL_CCUI.name;
    if (isEmailOptionSelected) {
      await this.validateFailConfirmationEmailLabel(resolvedPaymentOption, emailAddress);
    } else {
      await this.validateFailConfirmationPOAMessage();
    }
  }

  /** Check we reached the current page by checking a specific element from the page. */
  async validatePage(): Promise<void> {
    console.log('Validate Confirmation-Error page was reached');
    await expect(this.referenceLabel, 'Confirmation error reference label').toBeVisible();
  }

  private getReservationId(reservationInfo: unknown): string {
    const candidate = reservationInfo as { reservationDetails?: { basketReference?: string }; basketReference?: string };
    return candidate.reservationDetails?.basketReference ?? candidate.basketReference ?? '';
  }
}