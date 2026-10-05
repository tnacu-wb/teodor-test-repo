import { expect, type FrameLocator, type Locator } from '@playwright/test';
import { type CardDetails, Cards } from '../../test-data/cards';
import { Strings } from '../../test-data/strings';
import { BasePage } from './base.page';

/** Shared Worldline/3CP payment-details page used by Opera booking flows. */
export abstract class PaymentDetailsBasePage extends BasePage {
  static readonly PAYMENT_DETAILS_IFRAME_SELECTOR = 'iframe#paymentFrame';

  // ######## UI elements/properties ########
  readonly paymentIframeElement: Locator = this.page.locator(PaymentDetailsBasePage.PAYMENT_DETAILS_IFRAME_SELECTOR);
  readonly paymentIframe: FrameLocator = this.page.frameLocator(PaymentDetailsBasePage.PAYMENT_DETAILS_IFRAME_SELECTOR);
  readonly paymentDetailsContainer: Locator = this.paymentIframe.locator('div[data-testid="paymentContainer"]');
  readonly paymentDetailsHeaderTitleLabel: Locator = this.paymentIframe.locator('h2.wb-heading');
  readonly cardNumberInput: Locator = this.paymentIframe.locator('div#input_card_number input, input#card_pan');
  readonly cardHolderNameInput: Locator = this.paymentIframe.locator('input#card_holder_first_name');
  readonly cardExpiryMonthDropdown: Locator = this.paymentIframe.locator('select[name="card_date_expiry_month"]');
  readonly cardExpiryYearDropdown: Locator = this.paymentIframe.locator('select[name="card_date_expiry_year"]');
  readonly cardExpiryInput: Locator = this.paymentIframe.locator('input#card_expiry_date');
  readonly cvvInput: Locator = this.paymentIframe.locator('input#card_card_security_cvx_2');
  readonly confirmBookingButton: Locator = this.paymentIframe.locator('input#paybutton');
  readonly backToPaymentPageLabel: Locator = this.page.locator('div[data-testid="backToPageContainer"] p');
  readonly backPaymentPageArrowImage: Locator = this.page.locator('div[data-testid="backToPageContainer"] svg');
  readonly backToPaymentPageTextElement: Locator = this.page.locator('div[data-testid="backToPageContainer"] p');

  // ######## UI actions/navigation ########
  /** Switch to the third-party payment iframe. */
  async switchToPaymentDetailsIFrame(): Promise<FrameLocator> {
    console.log('Switch to Payment Details Opera iFrame');
    await expect(this.paymentIframeElement, 'Payment details iframe').toBeVisible();
    return this.paymentIframe;
  }

  /** Set the card expiry date using the payment provider's available field variant. */
  async setCardExpiryDate(paymentDetails: CardDetails): Promise<void> {
    const expiryMonth = paymentDetails.expiryMonth.padStart(2, '0');
    const expiryYear = paymentDetails.expiryYear;
    const expiryDate = `${expiryMonth}/${expiryYear.slice(-2)}`;

    if (await this.cardExpiryMonthDropdown.count()) {
      await this.cardExpiryMonthDropdown.click();
      await this.cardExpiryMonthDropdown.selectOption({ label: expiryMonth });
      await this.cardExpiryYearDropdown.click();
      await this.cardExpiryYearDropdown.selectOption({ label: expiryYear });
      await expect(this.cardExpiryMonthDropdown, 'Expiry month should be selected').toHaveValue(expiryMonth);
      await expect(this.cardExpiryYearDropdown, 'Expiry year should be selected').toHaveValue(expiryYear);
      return;
    }

    await this.cardExpiryInput.fill('');
    await this.cardExpiryInput.pressSequentially(expiryDate);
    await this.cardExpiryInput.press('Tab');
    await expect(this.cardExpiryInput, 'Expiry date should be populated').toHaveValue(expiryDate);
  }

  /** Enter card details in the third-party payment iframe. */
  async setPaymentDetails(paymentDetails: CardDetails = Cards.VISA_CARD, isBACCard = false): Promise<void> {
    console.log(`Set all payment details data for ${paymentDetails.name}`);
    await this.switchToPaymentDetailsIFrame();
    await this.cardNumberInput.waitFor({ state: 'visible' });
    await this.cardNumberInput.fill(paymentDetails.number.replace(/\s/g, ''));
    await this.cardHolderNameInput.fill(paymentDetails.name);
    await this.setCardExpiryDate(paymentDetails);
    if (!isBACCard && paymentDetails.code) {
      await this.cvvInput.fill(paymentDetails.code);
    }
  }

  /** Click the payment confirmation control in the iframe. */
  async clickOnConfirmBookingButton(): Promise<void> {
    console.log('Click on Confirm booking button in order to confirm the booking');
    await expect(this.confirmBookingButton, 'Confirm booking button should be enabled').toBeEnabled();
    await this.confirmBookingButton.evaluate((button) => (button as HTMLInputElement).click());
  }

  // ######## UI validations ########
  /** Validate the payment iframe and its confirmation control. */
  async validatePage(): Promise<void> {
    console.log('Validate Payment details page was reached');
    await expect(this.paymentIframeElement, 'Payment details iframe').toBeVisible();
    await expect(this.confirmBookingButton, 'Confirm booking button').toBeVisible();
  }

  /** Validate the payment controls are not displayed outside the iframe flow. */
  async validateBackToPaymentPageElementsAreNotDisplayed(): Promise<void> {
    console.log('Validate Back to Payment controls are not displayed');
    await expect(this.backPaymentPageArrowImage, 'Back to payment page arrow').not.toBeVisible();
    await expect(this.backToPaymentPageLabel, 'Back to payment page label').not.toBeVisible();
    await expect(this.backToPaymentPageTextElement, 'Back to payment page text').not.toBeVisible();
  }
}