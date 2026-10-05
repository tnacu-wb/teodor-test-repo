import { expect, type Locator, type FrameLocator } from '@playwright/test';
import { type CardDetails } from '../../test-data/cards';
import { Strings } from '../../test-data/strings';
import { PaymentVerticalStripSectionComponent } from '../../components/shared/payment/paymentVerticalStripSection.component';
import { BasePage } from '../shared/base.page';

/**
 * PI Payment Details Page - handles the third-party Worldline/3CP payment iframe
 * where card details (number, holder name, expiry) are entered.
 * Elements inside the iframe cannot use data-testid as they are controlled by the 3rd party.
 *
 * Frame context handling:
 * - The payment flow enters the Worldline iframe via `switchToPaymentDetailsIFrame()`
 * - Card details are entered within the iframe scope
 * - After payment submission, 3DS challenge occurs on the Worldline/SIX hosted page
 * - In Playwright, FrameLocator-scoped interactions are automatically isolated —
 *   after leaving the iframe, page-level locators work again without an explicit
 *   "switch back to default content" call.
 */
export class PaymentDetailsPage extends BasePage {
  // ######## UI elements/properties ########

  readonly verticalStripSection: PaymentVerticalStripSectionComponent = new PaymentVerticalStripSectionComponent();

  // Payment iframe (Worldline/3CP)
  readonly paymentIframe: FrameLocator = this.page.frameLocator('iframe#paymentFrame');
  readonly paymentIframeElement: Locator = this.page.locator('iframe#paymentFrame');

  // Card input locators (within iframe context)
  readonly cardNumberInput: Locator = this.paymentIframe.locator('input#card_pan, div#input_card_number input');
  readonly cardHolderNameInput: Locator = this.paymentIframe.locator('input#card_holder_first_name');
  readonly cardExpiryInput: Locator = this.paymentIframe.locator('input#card_expiry_date');
  readonly confirmBookingButton: Locator = this.paymentIframe.locator('input#paybutton');
  readonly paymentDetailsContainer: Locator = this.paymentIframe.locator('div[data-testid="paymentContainer"]');
  readonly paymentDetailsHeaderTitleLabel: Locator = this.paymentIframe.locator('h2.wb-heading');
  readonly backToPaymentPageLabel: Locator = this.page.locator('div[data-testid="backToPageContainer"] p');
  readonly backPaymentPageArrowImage: Locator = this.page.locator('div[data-testid="backToPageContainer"] svg');
  readonly backToPaymentPageTextElement: Locator = this.page.getByText(
    Strings.BACK_TO_PAYMENT_PAGE.data.default ?? 'Back to payment page',
    { exact: false }
  );

  /** Return the payment iframe frame locator for callers that need direct iframe access. */
  switchToIframe(): FrameLocator {
    return this.paymentIframe;
  }

  // ######## UI actions/navigation ########

  /**
   * Switch to the payment iframe context.
   * In Playwright, this returns the FrameLocator which automatically scopes
   * all subsequent locator interactions to the iframe content.
   * Waits up to 30s for the iframe element to be visible on the parent page.
   * @returns The FrameLocator for the payment iframe
   * @throws Error if the iframe is not visible within 30s
   */
  async switchToPaymentDetailsIFrame(): Promise<FrameLocator> {
    console.log('Switching to payment details iframe');
    try {
      await this.paymentIframeElement.waitFor({ state: 'visible', timeout: 30000 });
    } catch {
      throw new Error(
        'PaymentDetailsPage: Payment iframe (iframe#paymentFrame) was not found within 30s timeout'
      );
    }
    return this.paymentIframe;
  }

  /** Fill the card number and optionally move focus to the next field. */
  async setCardNumber(card: CardDetails, pressTab = true): Promise<void> {
    console.log(`Set card number for: ${card.name}`);
    await this.cardNumberInput.fill(card.number.replace(/\s/g, ''));
    if (pressTab) {
      await this.cardNumberInput.press('Tab');
    }
  }

  /** Fill the cardholder name and optionally move focus to the next field. */
  async setCardholderName(card: CardDetails, pressTab = true): Promise<void> {
    console.log(`Set cardholder name for: ${card.name}`);
    await this.cardHolderNameInput.waitFor({ state: 'visible' });
    await this.cardHolderNameInput.fill(card.name);
    if (pressTab) {
      await this.cardHolderNameInput.press('Tab');
    }
    await expect(this.cardHolderNameInput, 'Cardholder name should be populated').toHaveValue(card.name);
  }

  /** Fill the combined card expiry date using the Worldline MM/YY format. */
  async setCardExpiryDate(card: CardDetails): Promise<void> {
    console.log(`Set card expiry date for: ${card.name}`);
    const expiryDate = `${card.expiryMonth}/${card.expiryYear.slice(-2)}`;
    await this.cardExpiryInput.waitFor({ state: 'visible' });
    await this.cardExpiryInput.click();
    await this.cardExpiryInput.pressSequentially(expiryDate, { delay: 50 });
    await expect(this.cardExpiryInput, 'Card expiry date should be populated').toHaveValue(expiryDate);
  }

  /**
   * Enter PIBA card details within the Worldline payment iframe.
   * Fills card number, cardholder name, and expiry date (MM/YY combined).
   * Does NOT fill CVV for PIBA cards (code is optional/undefined).
   * @param card - The card details to enter (from `qa/src/constants/cards.ts`)
   */
  async enterCardDetails(card: CardDetails): Promise<void> {
    console.log(`Entering card details for: ${card.name}`);
    // Ensure iframe is ready
    await this.switchToPaymentDetailsIFrame();

    // Fill card number (remove spaces, use pressSequentially for iframe inputs)
    await this.cardNumberInput.waitFor({ state: 'visible' });
    await this.setCardNumber(card, false);
    await this.setCardholderName(card, false);
    await this.setCardExpiryDate(card);

    // PIBA cards do NOT require CVV — intentionally skip code/CVV field
  }

  /**
   * @deprecated Use `enterCardDetails()` instead — same behaviour, clearer naming.
   */
  async fillCardDetails(card: CardDetails): Promise<void> {
    console.log(`Filling card details (deprecated): ${card.name}`);
    return this.enterCardDetails(card);
  }

  /**
   * Submit payment by clicking the "Confirm Booking" button within the payment iframe.
   * Waits for the button to be enabled before clicking.
   * After clicking, the Worldline/SIX 3D Secure challenge page may appear.
   */
  async submitPayment(): Promise<void> {
    console.log('Submitting payment');
    // Wait for Confirm Booking button to be enabled
    await this.confirmBookingButton.waitFor({ state: 'visible', timeout: 30000 });
    await this.confirmBookingButton.isEnabled();

    // Click the confirm booking button
    await this.confirmBookingButton.click();
  }

  /**
   * @deprecated Use `submitPayment()` instead — same behaviour, clearer naming.
   */
  async clickConfirmBooking(): Promise<void> {
    console.log('Clicking confirm booking (deprecated)');
    return this.submitPayment();
  }

  // ######## UI validations ########

  /** Validate that the Worldline payment iframe and confirmation control are available. */
  async validatePage(): Promise<void> {
    console.log('Validate payment details page');
    await expect(this.paymentIframeElement, 'Payment Details iframe should be visible').toBeVisible();
    await expect(this.confirmBookingButton, 'Confirm booking button should be visible in the payment iframe').toBeVisible();
  }

  /** Validate card number and cardholder values currently shown in the iframe. */
  async validatePaymentDetailsValues(card: CardDetails): Promise<void> {
    console.log(`Validate payment details values for: ${card.name}`);
    await expect(this.cardNumberInput, 'Card number should match the provided payment card').toHaveValue(card.number.replace(/\s/g, ''));
    await expect(this.cardHolderNameInput, 'Cardholder name should match the provided payment card').toHaveValue(card.name);
    await expect(this.cardExpiryInput, 'Card expiry date should match the provided payment card').toHaveValue(
      `${card.expiryMonth}/${card.expiryYear.slice(-2)}`
    );
  }

  /** Validate the Back to Payment controls are not displayed during the 3CP card-entry step. */
  async validateBackToPaymentPageElementsAreNotDisplayed(): Promise<void> {
    console.log('Validate Back to Payment controls are not displayed');
    await expect(this.backPaymentPageArrowImage, 'Back to payment page arrow should not be visible').not.toBeVisible();
    await expect(this.backToPaymentPageLabel, 'Back to payment page hyperlink should not be visible').not.toBeVisible();
    await expect(this.backToPaymentPageTextElement, 'Back to payment page text should not be visible').not.toBeVisible();
  }
}
