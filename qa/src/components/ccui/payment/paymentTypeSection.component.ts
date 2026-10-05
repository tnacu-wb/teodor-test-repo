import { expect, type Locator } from '@playwright/test';
import { Strings } from '../../../test-data/strings';
import { Constants } from '../../../test-data/constants';
import { CcuiComponent } from '../baseCcui.component';

/** Payment type section from the CCUI payment page. Mirrors the CCUI reference component. */
export class PaymentTypeSectionComponent extends CcuiComponent {
  // ######## properties ########

  // ######## UI elements/properties ########
  readonly paymentTypeContainer: Locator = this.page.locator('div[data-testid="paymentTypeContainer_withData"]');
  readonly paymentTypeTitleLabel: Locator = this.page.locator('p[data-testid="payment-type-method_title"]');
  readonly paymentTypeDescriptionLabel: Locator = this.page.locator('p[data-testid="payment-type-method_description"]');
  readonly paymentNewCreditDebitCardLabel: Locator = this.page.locator('p[data-testid="payment-type-method_option-text-CARD"]');
  readonly paymentPibaUkLabel: Locator = this.page.locator('p[data-testid="payment-type-method_option-text-PIBA UK"]');
  readonly paymentPibaEuLabel: Locator = this.page.locator('p[data-testid="payment-type-method_option-text-PIBA EU"]');
  readonly paymentAccountToCompanyLabel: Locator = this.page.locator('p[data-testid="payment-type-method_option-text-Account to company"]');
  readonly paymentNonGuaranteedBookingLabel: Locator = this.page.locator('p[data-testid="payment-type-method_option-text-Non-guaranteed booking"]');
  readonly paymentNewCreditDebitCardRadioButton: Locator = this.page.locator('div[data-testid="payment-type-method_option-CARD"]');
  readonly paymentVisaDebitCardRadioButton: Locator = this.page.locator('p[data-testid="payment-type-method_option-text-Visa Debit"]').locator('..');
  readonly paymentPibaUkRadioButton: Locator = this.page.locator('div[data-testid="payment-type-method_option-PIBA_UK"]');
  readonly paymentPibaEuRadioButton: Locator = this.page.locator('div[data-testid="payment-type-method_option-PIBA_EU"]');
  readonly paymentAccountToCompanyRadioButton: Locator = this.page.locator('div[data-testid="payment-type-method_option-Account_to_company"]');
  readonly paymentNonGuaranteedBookingRadioButton: Locator = this.page.locator('div[data-testid="payment-type-method_option-Non-guaranteed_booking"]');
  readonly paymentTypeContainerList: Locator = this.page.locator('div[data-testid="payment-type-method_option-wrapper"] label');
  readonly paymentTypeRadioButton: Locator = this.paymentTypeContainerList.locator('input[type="radio"]');
  readonly paymentTypeLabel: Locator = this.page.locator('label');
  readonly payOnArrivalRadioButton: Locator = this.page.locator('//span[@data-testid="radio-box-inside_PAY_ON_ARRIVAL"]');
  readonly payNowRadioButton: Locator = this.page.locator('//span[@data-testid="radio-box-inside_PAY_NOW"]');
  // ######## UI actions/navigation ########
  /** Select New Credit/Debit Card radio button. */
  async selectNewCreditDebitCardRadioButton(): Promise<void> { console.log('Select New Credit/Debit Card radio button'); await this.paymentNewCreditDebitCardLabel.click(); }
  /** Select PIBA UK radio button. */
  async selectPibaUkRadioButton(): Promise<void> { console.log('Select PIBA UK radio button'); await this.paymentPibaUkLabel.click(); }
  /** Select PIBA EU radio button. */
  async selectPibaEuRadioButton(): Promise<void> { console.log('Select PIBA EU radio button'); await this.paymentPibaEuLabel.click(); }
  /** Select Account to Company radio button. */
  async selectAccountToCompanyRadioButton(): Promise<void> { console.log('Select Account to Company radio button'); await this.paymentAccountToCompanyLabel.click(); }
  /** Select Non Guaranteed Booking radio button. */
  async selectNonGuaranteedBookingRadioButton(): Promise<void> { console.log('Select Non Guaranteed Booking radio button'); await this.paymentNonGuaranteedBookingLabel.click(); }
  /**
   * Click to select payment type by label.
   * @param paymentType Payment type label to select.
   */
  async clickToSelectPaymentTypeByLabel(paymentType: string): Promise<void> { console.log(`Click to select payment type by label: ${paymentType}`); const actions: Record<string, () => Promise<void>> = { [await Strings.NEW_CREDIT_DEBIT_CARD.name]: () => this.selectNewCreditDebitCardRadioButton(), [await Strings.NEW_PIBA_UK.name]: () => this.selectPibaUkRadioButton(), [await Strings.NEW_PIBA_EU.name]: () => this.selectPibaEuRadioButton(), [await Strings.ACCOUNT_TO_COMPANY.name]: () => this.selectAccountToCompanyRadioButton(), [await Strings.NON_GUARANTEED_BOOKING.name]: () => this.selectNonGuaranteedBookingRadioButton() }; const action = actions[paymentType]; if (!action) throw new Error(`Payment type with value ${paymentType} is not available`); await action(); }
  /**
   * Click to select a payment option by radio button.
   * @param payment Payment option to select; defaults to pay on arrival.
   */
  async clickToSelectPaymentByRadioButton(payment = Strings.PAY_ON_ARRIVAL_CCUI.name): Promise<void> { const resolvedPayment = await payment; console.log(`Click to select payment by radio button: ${resolvedPayment}`); await (resolvedPayment === await Strings.PAY_NOW_CCUI.name ? this.payNowRadioButton : this.payOnArrivalRadioButton).click(); }
  // ######## UI validations ########
  /** Validate payment type elements in container are clickable. */
  async validatePaymentTypeElementsInContainerAreClickable(): Promise<void> { console.log('Validate payment type elements in container are clickable'); await expect(this.paymentTypeContainerList.first(), 'Payment type element').toBeVisible(); }
  /**
   * Validate payment type elements in container are enabled.
   * @param isEnabled Whether the payment type radio should be enabled.
   */
  async validatePaymentTypeElementsInContainerAreEnabled(isEnabled = true): Promise<void> { console.log('Validate payment type elements in container are enabled'); await this.validateEnabledState(this.paymentTypeRadioButton.first(), 'Payment type radio enabled state', isEnabled); }
  /**
   * Validate New Credit/Debit Card is selected.
   * @param isSelected Whether the option should be selected.
   */
  async validateNewCreditDebitCardIsSelected(isSelected: boolean): Promise<void> { await this.validateRadioChecked(this.paymentNewCreditDebitCardRadioButton, 'New Credit/Debit Card', isSelected); }
  /**
   * Validate PIBA UK is selected.
   * @param isSelected Whether the option should be selected.
   */
  async validatePibaUkIsSelected(isSelected: boolean): Promise<void> { await this.validateRadioChecked(this.paymentPibaUkRadioButton, 'PIBA UK', isSelected); }
  /**
   * Validate PIBA EU is selected.
   * @param isSelected Whether the option should be selected.
   */
  async validatePibaEuIsSelected(isSelected: boolean): Promise<void> { await this.validateRadioChecked(this.paymentPibaEuRadioButton, 'PIBA EU', isSelected); }
  /**
   * Validate Account to Company is selected.
   * @param isSelected Whether the option should be selected.
   */
  async validateAccountToCompanyIsSelected(isSelected: boolean): Promise<void> { await this.validateRadioChecked(this.paymentAccountToCompanyRadioButton, 'Account to Company', isSelected); }
  /**
   * Validate Non Guaranteed Booking is selected.
   * @param isSelected Whether the option should be selected.
   */
  async validateNonGuaranteedBookingIsSelected(isSelected: boolean): Promise<void> { await this.validateRadioChecked(this.paymentNonGuaranteedBookingRadioButton, 'Non Guaranteed Booking', isSelected); }
  /**
   * Validate Visa Debit Card is enabled.
   * @param isEnabled Whether the option should be enabled.
   */
  async validateVisaDebitCardIsEnabled(isEnabled: boolean): Promise<void> { await this.validateEnabledState(this.paymentVisaDebitCardRadioButton, 'Visa Debit Card enabled state', isEnabled); }
  /**
   * Validate New Credit/Debit Card is enabled.
   * @param isEnabled Whether the option should be enabled.
   */
  async validateNewCreditDebitCardIsEnabled(isEnabled: boolean): Promise<void> { await this.validateEnabledState(this.paymentNewCreditDebitCardRadioButton, 'New Credit/Debit Card enabled state', isEnabled); }
  /**
   * Validate PIBA UK is enabled.
   * @param isEnabled Whether the option should be enabled.
   */
  async validatePibaUkIsEnabled(isEnabled: boolean): Promise<void> { await this.validateEnabledState(this.paymentPibaUkRadioButton, 'PIBA UK enabled state', isEnabled); }
  /**
   * Validate PIBA EU is enabled.
   * @param isEnabled Whether the option should be enabled.
   */
  async validatePibaEuIsEnabled(isEnabled: boolean): Promise<void> { await this.validateEnabledState(this.paymentPibaEuRadioButton, 'PIBA EU enabled state', isEnabled); }
  /**
   * Validate Account to Company is enabled.
   * @param isEnabled Whether the option should be enabled.
   */
  async validateAccountToCompanyIsEnabled(isEnabled: boolean): Promise<void> { await this.validateEnabledState(this.paymentAccountToCompanyRadioButton, 'Account to Company enabled state', isEnabled); }
  /**
   * Validate Non Guaranteed Booking is enabled.
   * @param isEnabled Whether the option should be enabled.
   */
  async validateNonGuaranteedBookingIsEnabled(isEnabled: boolean): Promise<void> { await this.validateEnabledState(this.paymentNonGuaranteedBookingRadioButton, 'Non Guaranteed Booking enabled state', isEnabled); }
  /**
   * Validate that a payment option is selected.
   * @param paymentOption Payment option label to validate.
   * @param isSelected Whether the option should be selected.
   */
  async validatePaymentOptionIsSelected(paymentOption: string | Promise<string>, isSelected = true): Promise<void> { const resolvedPayment = await paymentOption; await this.validateRadioChecked(resolvedPayment === await Strings.PAY_NOW_CCUI.name ? this.payNowRadioButton : this.payOnArrivalRadioButton, resolvedPayment, isSelected); }
  /**
   * Validate that a payment option is enabled.
   * @param paymentOption Payment option label to validate.
   * @param isEnabled Whether the option should be enabled.
   */
  async validatePaymentOptionIsEnabled(paymentOption: string | Promise<string>, isEnabled = true): Promise<void> { const resolvedPayment = await paymentOption; await this.validateEnabledState(resolvedPayment === await Strings.PAY_NOW_CCUI.name ? this.payNowRadioButton : this.payOnArrivalRadioButton, `${resolvedPayment} enabled state`, isEnabled); }
  /** Validate a payment type option's enabled state by localized label. */
  async validatePaymentTypeIsEnabled(paymentType: string | Promise<string>, isEnabled = true): Promise<void> { const resolvedPaymentType = await paymentType; console.log(`Validate payment type enabled=${isEnabled}: ${resolvedPaymentType}`); const options: Record<string, Locator> = { [await Strings.NEW_CREDIT_DEBIT_CARD.name]: this.paymentNewCreditDebitCardRadioButton, [await Strings.NEW_PIBA_UK.name]: this.paymentPibaUkRadioButton, [await Strings.NEW_PIBA_EU.name]: this.paymentPibaEuRadioButton, [await Strings.ACCOUNT_TO_COMPANY.name]: this.paymentAccountToCompanyRadioButton, [await Strings.NON_GUARANTEED_BOOKING.name]: this.paymentNonGuaranteedBookingRadioButton }; const option = options[resolvedPaymentType]; if (!option) throw new Error(`Payment type with value ${resolvedPaymentType} is not enabled`); await this.validateEnabledState(option, `${resolvedPaymentType} enabled state`, isEnabled); }
  /** Validate whether a payment type label is displayed. */
  async validatePibaUkIsDisplayed(isDisplayed: boolean): Promise<void> { console.log(`Validate PIBA UK displayed=${isDisplayed}`); await this.validateDisplayState(this.paymentPibaUkLabel, 'PIBA UK display state', isDisplayed); }
  /** Validate whether PIBA EU is displayed. */
  async validatePibaEuIsDisplayed(isDisplayed: boolean): Promise<void> { console.log(`Validate PIBA EU displayed=${isDisplayed}`); await this.validateDisplayState(this.paymentPibaEuLabel, 'PIBA EU display state', isDisplayed); }
  /** Validate whether Account to Company is displayed. */
  async validateAccountToCompanyIsDisplayed(isDisplayed: boolean): Promise<void> { console.log(`Validate Account to Company displayed=${isDisplayed}`); await this.validateDisplayState(this.paymentAccountToCompanyLabel, 'Account to Company display state', isDisplayed); }
  /** Validate whether Non-guaranteed booking is displayed. */
  async validateNonGuaranteedBookingIsDisplayed(isDisplayed: boolean): Promise<void> { console.log(`Validate non-guaranteed booking displayed=${isDisplayed}`); await this.validateDisplayState(this.paymentNonGuaranteedBookingLabel, 'Non-guaranteed booking display state', isDisplayed); }
  /** Validate whether the payment type container is displayed. */
  async validatePaymentTypeContainerIsDisplayed(isDisplayed = true): Promise<void> { console.log(`Validate payment type container displayed=${isDisplayed}`); await this.validateDisplayState(this.paymentTypeContainer, 'Payment type container', isDisplayed); }
  /** Validate the payment type labels for the hotel country. */
  async validatePaymentTypeElements(hotelCountryCode: string): Promise<void> { console.log(`Validate payment type elements for ${hotelCountryCode}`); await expect(this.paymentTypeTitleLabel, 'Payment type title').toContainText(await Strings.PAYMENT_TYPE.name); await expect(this.paymentTypeDescriptionLabel, 'Payment type description').toContainText(await Strings.PAYMENT_WILL_BE_HANDLED_BY.name); await expect(this.paymentNewCreditDebitCardLabel, 'New card label').toContainText(await Strings.NEW_CREDIT_DEBIT_CARD.name); await this.validatePibaUkIsDisplayed(true); await this.validatePibaEuIsDisplayed(false); await this.validateAccountToCompanyIsDisplayed(false); if (hotelCountryCode === Constants.GERMANY_COUNTRY_CODE) await expect(this.paymentNonGuaranteedBookingLabel, 'Non-guaranteed booking label').toContainText(await Strings.NON_GUARANTEED_BOOKING.name); }
  /** Validate payment elements are enabled or disabled. */
  async validatePaymentElementsAreEnabled(isEnabled: boolean): Promise<void> { console.log(`Validate payment elements enabled=${isEnabled}`); await this.validatePibaUkIsEnabled(isEnabled); await this.validatePibaEuIsEnabled(isEnabled); await this.validateAccountToCompanyIsEnabled(isEnabled); }
  /** Validate the selected state of a radio button. */
  private async validateRadioChecked(locator: Locator, description: string, isSelected: boolean): Promise<void> { console.log(`Validate ${description} selected=${isSelected}`); if (isSelected) await expect(locator, `${description} selected state`).toBeChecked(); else await expect(locator, `${description} selected state`).not.toBeChecked(); }
}