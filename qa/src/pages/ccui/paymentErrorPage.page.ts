import { expect, type Locator } from '@playwright/test';
import { BasePage } from '../shared/base.page';

/**
 * Payment error page from Opera CCUI environment containing the UI elements, custom actions and validations.
 */
export class PaymentErrorPage extends BasePage {
  // ######## properties ########

  // ######## UI elements/properties ########

  readonly paymentErrorTitle: Locator = this.page.locator('h3[data-testid="paymentErrorHandling_title"]');
  readonly paymentErrorNotification: Locator = this.page.locator('div[data-testid="Alert"]');
  readonly paymentErrorNotificationTitle: Locator = this.page.locator('div[data-testid="AlertTitle"]');
  readonly paymentErrorNotificationMessage: Locator = this.page.locator('//div[@data-testid="AlertDescription"]');
  readonly paymentErrorNotificationIcon: Locator = this.page.locator('//div[@data-testid="Alert"]/div[1]');
  readonly paymentErrorBackToPaymentDetailsButton: Locator = this.page.locator('//div[@data-testid="paymentErrorHandling-backToDetails_back-arrow"]');

  // ######## UI actions/navigation ########

  /** Click on back to payment details button. */
  async clickOnBackToPaymentDetailsButton(): Promise<void> {
    console.log('Click on back to payment details button');
    await this.paymentErrorBackToPaymentDetailsButton.click();
  }

  // ######## UI validations ########

  /** Validate payment error page title is displayed. */
  async validatePaymentErrorPageTitleIsDisplayed(): Promise<void> {
    console.log('Validate payment error page title is displayed');
    await expect(this.paymentErrorTitle, 'Payment error handling page title').toBeVisible();
  }

  /** Validate payment error notification icon is displayed. */
  async validatePaymentErrorNotificationIconIsDisplayed(): Promise<void> {
    console.log('Validate payment error notification icon is displayed');
    await expect(this.paymentErrorNotificationIcon, 'Payment error notification handling notification icon').toBeVisible();
  }

  /** Validate payment error notification title is displayed. */
  async validatePaymentErrorNotificationTitleIsDisplayed(): Promise<void> {
    console.log('Validate payment error notification title is displayed');
    await expect(this.paymentErrorNotificationTitle, 'Payment error notification handling notification title').toBeVisible();
  }

  /** Validate payment error notification message is displayed. */
  async validatePaymentErrorNotificationMessageIsDisplayed(): Promise<void> {
    console.log('Validate payment error notification message is displayed');
    await expect(this.paymentErrorNotificationMessage, 'Payment error notification handling notification message').toBeVisible();
  }

  /** Validate payment error page elements are displayed. */
  async validateErrorPage(): Promise<void> {
    console.log('Validating payment error page');
    await this.validatePaymentErrorPageTitleIsDisplayed();
    await this.validatePaymentErrorNotificationIconIsDisplayed();
    await this.validatePaymentErrorNotificationTitleIsDisplayed();
    await this.validatePaymentErrorNotificationMessageIsDisplayed();
  }
}