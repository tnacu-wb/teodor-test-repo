import { expect, type Locator } from '@playwright/test';
import { Strings } from '../../../test-data/strings';
import { PriceHelpers } from '../../../utils/priceHelpers';
import { CcuiComponent } from '../baseCcui.component';

/**
 * Total-cost section from the CCUI confirmation page.
 * Mirrors qa/reference/test/pages/components/ccui/confirmBooking/totalCostSection.js
 * and its common total-cost section base component.
 */
export class TotalCostSectionComponent extends CcuiComponent {
  // ######## UI elements/properties ########

  /** Return the total-cost section container. */
  readonly totalCostSectionContainer: Locator = this.page.locator('[data-testid="TotalCostConfirm-container"]');
  /** Return the total-cost heading label. */
  readonly totalCostConfirmLabel: Locator = this.page.locator('b[data-testid="TotalCostConfirm-label"]');
  /** Return the payment message label. */
  readonly paymentMessageLabel: Locator = this.page.locator('p[data-testid="TotalCostConfirm-paymentMessage"]');
  /** Return the total-cost amount label. */
  readonly totalCostLabel: Locator = this.page.locator('p[data-testid="TotalCostConfirm-amount"]');
  /** Return the city-tax message label. */
  readonly totalCostCityTaxMessage: Locator = this.page.locator('p[data-testid="TotalCostConfirm-TaxesMessage"]');
  /** Read the numeric total-cost amount from the confirmation page. */
  async getTotalCostAmount(): Promise<number> {
    console.log('Get numeric total cost amount');
    await this.totalCostLabel.scrollIntoViewIfNeeded();
    const amount = PriceHelpers.getPriceAmountFromUiLabel(await this.totalCostLabel.innerText());
    if (!amount) throw new Error('Unexpected total cost amount. Total cost should not be 0 or unparsable');
    return amount;
  }

  // ######## UI actions/navigation ########

  // ######## UI validations ########

  /** Validate the Pay Now payment message. */
  async validateTotalCostPayNowPaymentMessage(): Promise<void> { console.log('Validate total cost Pay Now payment message'); await expect(this.paymentMessageLabel, 'Total cost Pay Now payment message').toContainText(await Strings.THANK_YOU_YOUR_PREPAYMENT_HAS_BEEN_TAKEN_CCUI.name); }
  /** Validate the Pay On Arrival payment message. */
  async validateTotalCostPayOnArrivalPaymentMessage(): Promise<void> { console.log('Validate total cost Pay On Arrival payment message'); await expect(this.paymentMessageLabel, 'Total cost Pay On Arrival payment message').toContainText(await Strings.THANK_YOU_YOUR_PAYMENT_WILL_BE_TAKEN_CCUI.name); }
  /** Validate the non-guaranteed payment message. */
  async validateTotalCostNonGuaranteedPaymentMessage(): Promise<void> { console.log('Validate total cost non-guaranteed payment message'); await expect(this.paymentMessageLabel, 'Total cost non-guaranteed payment message').toContainText(await Strings.THANK_YOU_FOR_YOUR_BOOKING_CCUI.name); }
  /** Validate the account-to-company payment message. */
  async validateTotalCostA2cPaymentMessage(): Promise<void> { console.log('Validate total cost A2C payment message'); await expect(this.paymentMessageLabel, 'Total cost A2C payment message').toContainText(await Strings.THANK_YOU_THE_PAYMENT_WILL_BE_INVOICED.name); }

  /**
   * Validate the payment message for a selected payment option.
   * @param paymentOption Localized payment option label.
   */
  async validateTotalCostPaymentMessage(paymentOption: string): Promise<void> {
    console.log('Validate total cost payment message');
    await expect(this.paymentMessageLabel, 'Total cost payment message').toBeVisible();
    if (paymentOption === await Strings.PAY_NOW.name) return this.validateTotalCostPayNowPaymentMessage();
    if (paymentOption === await Strings.PAY_ON_ARRIVAL.name || paymentOption === await Strings.PAY_ON_ARRIVAL_CCUI.name) return this.validateTotalCostPayOnArrivalPaymentMessage();
    if (paymentOption === await Strings.RESERVE_WITHOUT_CREDIT_CARD.name || paymentOption === await Strings.NON_GUARANTEED_BOOKING.name) return this.validateTotalCostNonGuaranteedPaymentMessage();
    if (paymentOption === await Strings.ACCOUNT_TO_COMPANY.name) return this.validateTotalCostA2cPaymentMessage();
    throw new Error(`Unknown payment option: ${paymentOption}`);
  }

  /** Validate the total-cost label and amount using the expected currency. */
  async validateTotalCostAmountAndCurrency(expectedAmount: string | number, currencyCode: string): Promise<void> {
    console.log('Validate total cost amount and currency');
    await PriceHelpers.validatePriceAmountAndCurrencyLabel({ element: this.totalCostLabel, expectedAmount, expectedCurrency: currencyCode, elementDescription: 'Total cost amount' });
  }

  /** Validate the localized total-cost heading. */
  async validateTotalCostConfirmLabel(): Promise<void> {
    console.log('Validate total cost confirm label');
    await expect(this.totalCostConfirmLabel, 'Total cost label').toBeVisible();
    await expect(this.totalCostConfirmLabel, 'Total cost label text').toContainText(`${await Strings.TOTAL_COST_CCUI.name}:`);
  }

  /**
   * Validate the complete total-cost section.
   * @param expectedAmount Expected total amount.
   * @param currencyCode Expected currency code.
   * @param paymentOption Selected payment option label.
   */
  async validateTotalCostSection(expectedAmount: string | number, currencyCode: string, paymentOption = Strings.PAY_ON_ARRIVAL.data.default ?? ''): Promise<void> {
    console.log('Validate total cost section');
    await this.validateTotalCostPaymentMessage(paymentOption);
    await this.validateTotalCostAmountAndCurrency(expectedAmount, currencyCode);
    await this.validateTotalCostConfirmLabel();
  }

  /** Validate whether the total-cost city-tax message is displayed. */
  async validateTotalCostCityTaxMessage(isCityTaxDisplayed = true): Promise<void> {
    console.log('Validate total cost city tax message');
    if (isCityTaxDisplayed) await expect(this.totalCostCityTaxMessage, 'Total cost city tax message').toContainText(await Strings.CITY_TAX_MESSAGE.name);
    else await expect(this.totalCostCityTaxMessage, 'Total cost city tax message').toBeHidden();
  }
}