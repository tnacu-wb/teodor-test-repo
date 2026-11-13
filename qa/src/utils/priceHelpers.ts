import { expect, type Locator } from '@playwright/test';
import { Constants } from '../test-data/constants';
import { Strings } from '../test-data/strings';

/**
 * Price helper methods migrated from the reference Helpers utility.
 */
export class PriceHelpers {
  private constructor() {}

  /**
   * Get numeric value of price UI label.
   * @param elementLabel label from ui element
   * @returns numeric amount
   */
  static getPriceAmountFromUiLabel(elementLabel: string): number {
    let amount = elementLabel.replace(/[^\d,.]/g, '').replace(/\s+/g, '');

    if (amount.slice(-3, -2) === ',') {
      amount = amount.replace(/\./g, '');
      amount = amount.replace(',', '.');
    } else if (amount.slice(-3, -2) === '.') {
      amount = amount.replace(/,/g, '');
    } else {
      amount = amount.replace(/[,.]/g, '');
    }

    return Number.parseFloat(amount);
  }

  /**
   * Get currency symbol used for price label.
   * @param elementLabel label of ui element
   * @returns total cost currency
   */
  static getPriceCurrencySymbolFromUiLabel(elementLabel: string): string {
    return elementLabel.replace(/[\d,.]/g, '').replace(/\s+/g, '').replace('-', '');
  }

  /**
   * Validate currency symbol used in UI matches expected currency.
   * @param actualCurrency actual currency symbol displayed in UI
   * @param expectedCurrency expected currency
   * @param elementDescription element description
   */
  static async validateCurrencySymbol(actualCurrency: string, expectedCurrency: string, elementDescription = ''): Promise<void> {
    let expectedSymbol = expectedCurrency;

    if (expectedCurrency === Constants.UK_CURRENCY_CODE) {
      expectedSymbol = await Strings.POUND_CURRENCY_SIGN.name;
    } else if (expectedCurrency === Constants.EURO_CURRENCY_CODE) {
      expectedSymbol = await Strings.EURO_CURRENCY_SIGN.name;
    }

    expect(actualCurrency, `${elementDescription} - Currency symbol is not as expected`).toBe(expectedSymbol);
  }

  /**
   * Validate price amount and currency are correctly displayed.
   * @param data data object
   * @param data.element UI element to be validated
   * @param data.expectedAmount expected amount
   * @param data.expectedCurrency expected currency
   * @param data.elementDescription element description
   */
  static async validatePriceAmountAndCurrencyLabel(data: {
    element: Locator;
    expectedAmount: string | number;
    expectedCurrency: string;
    elementDescription?: string;
  }): Promise<void> {
    const { element, expectedAmount, expectedCurrency, elementDescription = '' } = data;

    console.log(`${elementDescription} - Validate that the correct amount and currency symbol are displayed`);
    await expect(element, `${elementDescription} label should be displayed`).toBeVisible({ });
    await element.scrollIntoViewIfNeeded();
    const uiPriceLabel = await element.textContent() ?? '';

    const actualAmount = PriceHelpers.getPriceAmountFromUiLabel(uiPriceLabel);
    const expectedFormattedAmount = Number(Number.parseFloat(String(expectedAmount)).toFixed(2));
    expect(actualAmount, `${elementDescription} - Amount is not as expected`).toBe(expectedFormattedAmount);

    const actualCurrency = PriceHelpers.getPriceCurrencySymbolFromUiLabel(uiPriceLabel);
    await PriceHelpers.validateCurrencySymbol(actualCurrency, expectedCurrency, elementDescription);
  }
}