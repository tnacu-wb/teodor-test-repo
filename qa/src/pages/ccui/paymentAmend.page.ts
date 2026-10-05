import { expect, type Locator } from '@playwright/test';
import { Strings } from '../../test-data/strings';
import { BasePage } from '../shared/base.page';

/**
 * Payment amend page from Opera CCUI environment containing the UI elements, custom actions and validations.
 */
export class PaymentAmendPage extends BasePage {
  // ######## properties ########

  readonly url = 'payment';
  readonly title = Strings.PREMIER_INN.data.default;

  // ######## UI elements/properties ########

  readonly paymentTitleLabel: Locator = this.page.locator('h3[data-testid="amend-payment_title"]');
  readonly backToAmendPageButton: Locator = this.page.locator('div[data-testid="backToPageContainer"]');

  // UI components

  // ######## UI actions/navigation ########

  /**
   * Loads the page handling cookies and the needed authorization.
   * @param reservationInfo Contains details to open the payment page.
   */
  async open(reservationInfo: unknown): Promise<void> {
    console.log('Open CCUI payment amend page');
    const reservationId = this.getReservationId(reservationInfo);
    await this.openLocalizedPath(`${this.url}?reservationId=${reservationId}`);
  }

  /** Click Back to Amend page button. */
  async clickBackToAmendPageButton(): Promise<void> {
    console.log('Click on Back to Amend page button');
    await this.backToAmendPageButton.scrollIntoViewIfNeeded();
    await this.backToAmendPageButton.click();
  }

  // ######## UI validations ########

  /** Validate Back to Amend page button. */
  async validateBackToAmendPageButton(): Promise<void> {
    console.log('Validate Back to Amend page button');
    await expect(this.backToAmendPageButton, 'Back to Amend page button').toBeVisible();
    await expect(this.backToAmendPageButton, 'Back to Amend page button label').toContainText(await Strings.BACK.name);
  }

  /** Check we reached the current page by checking a specific element from the page. */
  async validatePage(): Promise<void> {
    console.log('Validate CCUI payment amend page');
    await expect(this.paymentTitleLabel, 'Payment amend title label').toBeVisible();
  }

  private getReservationId(reservationInfo: unknown): string {
    const candidate = reservationInfo as { reservationDetails?: { basketReference?: string }; basketReference?: string };
    return candidate.reservationDetails?.basketReference ?? candidate.basketReference ?? '';
  }
}