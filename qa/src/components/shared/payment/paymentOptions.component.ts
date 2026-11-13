import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';

/**
 * The payment options section (Pay now / Pay on arrival / Reserve without card) on the Payment
 * page containing the UI elements, custom actions and validations. Mirrors qa/reference
 * `components/common/payment/paymentOptions.js`.
 */
export class PaymentOptionsComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly cardDetailsContainer: Locator = this.page.locator('div[data-testid="CardDetails-Container"]');
  readonly cardDetailsLabel: Locator = this.page.locator('p[data-testid="CardDetails-Title"]');
  readonly cardDetailsDescriptionLabel: Locator = this.page.locator('p[data-testid="CardDetails-Disclaimer1"]');
  readonly payNowRadioButton: Locator = this.page.locator('[data-testid="radio-box-inside_PAY_NOW"]');
  readonly payNowLabel: Locator = this.page.locator('p[data-testid="CardDetails-payment-option-PAY_NOW"]');
  readonly payNowInput: Locator = this.page.locator('span[data-testid="radio-box-inside_PAY_NOW"]').locator('xpath preceding-sibling::input');
  readonly payNowDescriptionLabel: Locator = this.page.locator('p[data-testid="CardDetails-payment-option-PAY_NOW-desc"]');
  readonly payOnArrivalRadioButton: Locator = this.page.locator('[data-testid="radio-box-inside_PAY_ON_ARRIVAL"]');
  readonly payOnArrivalLabel: Locator = this.page.locator('p[data-testid="CardDetails-payment-option-PAY_ON_ARRIVAL"]');
  readonly payOnArrivalInput: Locator = this.page.locator('span[data-testid="radio-box-inside_PAY_ON_ARRIVAL"]').locator('xpath preceding-sibling::input');
  readonly payOnArrivalDescriptionLabel: Locator = this.page.locator('p[data-testid="CardDetails-payment-option-PAY_ON_ARRIVAL-desc"]');
  readonly reserveWithoutCardRadioButton: Locator = this.page.locator('[data-testid="radio-box-inside_RESERVE_WITHOUT_CARD"]');
  readonly reserveWithoutCardLabel: Locator = this.page.locator('p[data-testid="CardDetails-payment-option-RESERVE_WITHOUT_CARD"]');
  readonly reserveWithoutCardInput: Locator = this.page.locator('span[data-testid="radio-box-inside_RESERVE_WITHOUT_CARD"]').locator('xpath preceding-sibling::input');
  readonly reserveWithoutCardDescriptionLabel: Locator = this.page.locator('p[data-testid="CardDetails-payment-option-RESERVE_WITHOUT_CARD-desc"]');
  readonly paymentOptionsList: Locator = this.page.locator('div[data-testid="CardDetails-RadioContainer"] div[data-testid*="radio-box-wrapper"]');
  readonly paymentTypeInfoMessage: Locator = this.page.locator('div[data-testid="PaymentType-InfoMessages"] div[data-testid="AlertDescription"]');
  readonly paymentErrorAlertCardDetailsContainer: Locator = this.page.locator('div[data-testid="CardDetails-Container"] div[data-testid="Payment-Error-Alert"]');
  readonly paymentErrorAlertCardDetailsDescriptionLabel: Locator = this.page.locator('div[data-testid="CardDetails-Container"] div[data-testid="Payment-Error-AlertDescription"]');

  // ######## UI actions/navigation ########

  /** Select the 'Pay now' payment option. */
  async selectPayNow(): Promise<void> {
    console.log('Click on Pay now option');
    await this.payNowRadioButton.click();
  }

  /** Select the 'Pay on arrival' payment option. */
  async selectPayOnArrival(): Promise<void> {
    console.log('Click on Pay on arrival option');
    await this.payOnArrivalRadioButton.click();
  }

  /** Select the 'Reserve without card' payment option. */
  async selectReserveWithoutCard(): Promise<void> {
    console.log('Click on Reserve without credit card option');
    await this.reserveWithoutCardRadioButton.click();
  }

  /** Select Pay on Arrival using the legacy method name. */
  async selectPayOnArrivalRadioButton(): Promise<void> { await this.selectPayOnArrival(); }
  /** Select Pay Now using the legacy method name. */
  async selectPayNowRadioButton(): Promise<void> { await this.selectPayNow(); }
  /** Select Reserve Without Card using the legacy method name. */
  async selectReserveWithoutCardButton(): Promise<void> { await this.selectReserveWithoutCard(); }

  /** Select a payment option by its displayed label, or the first enabled default. */
  async selectPaymentOptionByLabel(paymentOption?: string): Promise<void> {
    console.log(`Select payment option: ${paymentOption ?? 'default'}`);
    if (paymentOption === await Strings.PAY_NOW.name) return this.selectPayNow();
    if (paymentOption === await Strings.PAY_ON_ARRIVAL.name) return this.selectPayOnArrival();
    if (paymentOption === await Strings.RESERVE_WITHOUT_CREDIT_CARD.name) return this.selectReserveWithoutCard();
    if (await this.payOnArrivalRadioButton.isEnabled()) return this.selectPayOnArrival();
    if (await this.payNowRadioButton.isEnabled()) return this.selectPayNow();
    return this.selectReserveWithoutCard();
  }

  /** Select Pay Now or Pay on Arrival. */
  async selectRandomPaymentOption(): Promise<void> {
    console.log('Select either Pay now or Pay on arrival');
    return Math.random() < 0.5 ? this.selectPayOnArrival() : this.selectPayNow();
  }

  // ######## UI validations ########

  /** Validate the payment error alert is displayed with the expected message. */
  async validatePaymentErrorAlert(expectedMessage: string): Promise<void> {
    console.log('Validate payment error alert');
    await expect(this.paymentErrorAlertCardDetailsContainer, 'Payment error alert').toBeVisible();
    await expect(this.paymentErrorAlertCardDetailsDescriptionLabel, 'Payment error alert description').toContainText(expectedMessage);
  }

  /** Validate the card details section title. */
  async validateCardDetailsLabel(): Promise<void> {
    console.log('Validate Card details label');
    await expect(this.cardDetailsLabel, 'Card details title').toHaveText(await Strings.CARD_DETAILS.name);
  }

  /** Validate the card-details section visibility and payment option count. */
  async validatePaymentDetailsSection({ numberOfOptions = 2, isDisplayed = true }: { numberOfOptions?: number; isDisplayed?: boolean } = {}): Promise<void> {
    console.log(`Validate Card details section displayed=${isDisplayed}`);
    if (isDisplayed) {
      await this.validateCardDetailsLabel();
      await expect(this.paymentOptionsList, 'Payment options list').toHaveCount(numberOfOptions);
    } else {
      await expect(this.cardDetailsLabel, 'Card details title').toBeHidden();
      await expect(this.paymentOptionsList, 'Payment options list').toBeHidden();
    }
  }

  /** Validate Pay Now option text, enabled state, and selection state. */
  async validatePayNowOption(buttonState: boolean, optionAvailable: boolean): Promise<void> {
    console.log('Validate Pay now radio button');
    await expect(this.payNowLabel, 'Pay now label').toContainText(await Strings.PAY_NOW.name);
    await expect(this.payNowDescriptionLabel, 'Pay now description').toContainText(await Strings.YOU_LL_PAY_SECURELY.name);
    if (optionAvailable) await expect(this.payNowRadioButton, 'Pay now option enabled').toBeEnabled();
    else await expect(this.payNowRadioButton, 'Pay now option disabled').toBeDisabled();
    if (optionAvailable) await expect(this.payNowInput, 'Pay now selected state').toBeChecked({ checked: buttonState });
  }

  /** Validate Pay On Arrival option text, enabled state, and selection state. */
  async validatePayOnArrivalOption(buttonState: boolean, optionAvailable: boolean): Promise<void> {
    console.log('Validate Pay on arrival radio button');
    await expect(this.payOnArrivalLabel, 'Pay on arrival label').toContainText(await Strings.PAY_ON_ARRIVAL.name);
    await expect(this.payOnArrivalDescriptionLabel, 'Pay on arrival description').toContainText(await Strings.WE_LL_NEED_YOUR_CARD_DETAILS.name);
    if (optionAvailable) await expect(this.payOnArrivalRadioButton, 'Pay on arrival option enabled').toBeEnabled();
    else await expect(this.payOnArrivalRadioButton, 'Pay on arrival option disabled').toBeDisabled();
    if (optionAvailable) await expect(this.payOnArrivalInput, 'Pay on arrival selected state').toBeChecked({ checked: buttonState });
  }

  /** Validate Reserve Without Card option state. */
  async validateReserveWithoutCardOption(buttonState: boolean, optionAvailable: boolean): Promise<void> {
    console.log('Validate Reserve without credit card radio button');
    await expect(this.reserveWithoutCardLabel, 'Reserve without card label').toContainText(await Strings.RESERVE_WITHOUT_CREDIT_CARD.name);
    await expect(this.reserveWithoutCardDescriptionLabel, 'Reserve without card description').toContainText(await Strings.NO_PAYMENT_WILL_BE_TAKEN_NOW.name);
    if (optionAvailable) await expect(this.reserveWithoutCardRadioButton, 'Reserve without card option enabled').toBeEnabled();
    else await expect(this.reserveWithoutCardRadioButton, 'Reserve without card option disabled').toBeDisabled();
    if (optionAvailable) await expect(this.reserveWithoutCardInput, 'Reserve without card selected state').toBeChecked({ checked: buttonState });
  }

  /** Validate each expected payment option. */
  async validatePaymentOptions(options: Array<{ name: string; buttonState: boolean; optionAvailable: boolean }>): Promise<void> {
    console.log('Validate payment options');
    await expect(this.paymentOptionsList, 'Payment options list').toHaveCount(options.length);
    for (const option of options) {
      if (option.name === await Strings.PAY_NOW.name) await this.validatePayNowOption(option.buttonState, option.optionAvailable);
      else if (option.name === await Strings.PAY_ON_ARRIVAL.name) await this.validatePayOnArrivalOption(option.buttonState, option.optionAvailable);
      else if (option.name === await Strings.RESERVE_WITHOUT_CREDIT_CARD.name) await this.validateReserveWithoutCardOption(option.buttonState, option.optionAvailable);
      else throw new Error(`Unknown Payment Option: ${option.name}`);
    }
  }

  /** Validate payment type info messages. */
  async validatePaymentTypeInfoMessages(paymentInfoMessages: Array<{ message: string }>): Promise<void> {
    console.log('Validate payment type info messages');
    if (paymentInfoMessages.length === 0) return expect(this.paymentTypeInfoMessage, 'Payment type info messages').toBeHidden();
    for (const [index, message] of paymentInfoMessages.entries()) {
      const expected = message.message.replace(/<[^>]+>/g, '').replace(/&nbsp;/g, ' ').trim();
      await expect(this.paymentTypeInfoMessage.nth(index), `Payment info message ${index}`).toHaveText(expected);
    }
  }

  /** Validate the payment section using its default contract. */
  async validateData(): Promise<void> { console.log('Validate payment options section'); await expect(this.paymentOptionsList, 'Payment options list').toBeVisible(); }
}
