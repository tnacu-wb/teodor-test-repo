import { expect, type Locator } from '@playwright/test';
import { Strings } from '../../../test-data/strings';
import { CcuiComponent } from '../baseCcui.component';
import { TotalCostSectionComponent } from './totalCostSection.component';

/** Discount section from the CCUI payment page. Mirrors the CCUI reference component. */
export class DiscountSectionComponent extends CcuiComponent {
  // ######## properties ########

  // ######## UI elements/properties ########
  readonly discountContainer: Locator = this.page.locator('div[data-testid="discountSection"]');
  readonly discountElementsList: Locator = this.discountContainer.locator(':scope > *');
  readonly discountLabel: Locator = this.discountContainer.locator('div > h3');
  readonly discountDescription: Locator = this.discountContainer.locator('div > h6');
  readonly discountInputCurrency: Locator = this.page.locator('div[data-testid*="discountInput"]');
  readonly discountAmountInput: Locator = this.page.locator('input[data-testid="input-discount"]');
  readonly discountSectionErrorLabel: Locator = this.discountContainer.locator('div[data-testid="input-discount-FormErrorMessage"]');
  readonly discountAmountInputPlaceholder: Locator = this.discountAmountInput;
  readonly discountAmountInputAriaInvalidAttribute: Locator = this.discountAmountInput;
  readonly bookingSummaryPreviousTotalLabel: Locator = this.page.locator('s[data-testid="BookingSummary-DesktopVariant-TotalCost-PreviousTotalCostName"]');
  readonly totalCostAmountLabel: Locator = this.page.locator('h2[data-testid="BookingSummary-DesktopVariant-TotalCost-TotalCostValue"]');
  readonly totalCost = new TotalCostSectionComponent();

  /**
   * Get a random discount amount below half of the current total.
   * @returns A discount amount formatted to two decimals.
   */
  async getRandomDiscountAmount(): Promise<string> {
    console.log('Get random discount amount');
    const total = Number((await this.totalCost.getTotalCostAmount()).replace(/[^0-9.,-]/g, '').replace(',', '.'));
    const maximum = Math.max(total / 2, 1);
    return (1 + Math.random() * Math.max(maximum - 1, 0)).toFixed(2);
  }
  /**
   * Get discount smaller than total.
   * @param totalAmount Total amount used to calculate the discount.
   * @returns A discount amount one unit below the total, formatted to two decimals.
   */
  getDiscountSmallerThanTotal(totalAmount: string | number): string { return (Number(totalAmount) - 1).toFixed(2); }

  // ######## UI actions/navigation ########
  /**
   * Set discount amount value.
   * @param value Discount amount to enter.
   * @param isFocusChangedAfterSet Whether to move focus away after filling.
   */
  async setDiscountAmountValueTo(value: string | number, isFocusChangedAfterSet = true): Promise<void> { console.log(`Set discount amount value to ${value}`); await this.fillInput(this.discountAmountInput, value, isFocusChangedAfterSet); }
  /** Clear discount amount. */
  async clearDiscountAmount(): Promise<void> {
    console.log('Clear discount amount');
    await this.discountAmountInput.scrollIntoViewIfNeeded();
    await this.discountAmountInput.click();
    for (let retry = 0; retry < 10 && (await this.discountAmountInput.inputValue()); retry += 1) {
      await this.page.keyboard.press('Backspace');
    }
  }
  // ######## UI validations ########
  /**
   * Validate discount error display.
   * @param isDisplayed Whether the discount error should be displayed.
   * @param errorMessage Optional expected error message.
   */
  async validateDiscountErrorDisplay({ isDisplayed, errorMessage }: { isDisplayed: boolean; errorMessage?: string }): Promise<void> {
    console.log('Validate discount error display');
    await this.validateDisplayState(this.discountSectionErrorLabel, 'Discount error', isDisplayed);
    await expect(await this.discountAmountInput.getAttribute('aria-invalid'), 'Discount input error state').toBe(isDisplayed ? 'true' : null);
    if (isDisplayed && errorMessage) {
      const expectedMessage = errorMessage === 'Amount error'
        ? await Strings.DISCOUNT_AMOUNT_ERROR.name
        : errorMessage === 'Character error'
          ? await Strings.DISCOUNT_CHARACTER_ERROR.name
          : errorMessage;
      await expect(this.discountSectionErrorLabel, 'Discount error message').toContainText(expectedMessage);
    }
  }
  /**
   * Validate discount container exists.
   * @param isDisplayed Whether the discount container should be displayed.
   */
  async validateContainerExists({ isDisplayed }: { isDisplayed: boolean }): Promise<void> { console.log('Validate discount container exists'); await this.validateDisplayState(this.discountContainer, 'Discount container', isDisplayed); }
  /**
   * Validate currency is correct.
   * @param currency Expected discount currency.
   */
  async validateCurrencyIsCorrect(currency: string): Promise<void> {
    console.log('Validate discount currency is correct');
    await expect(this.discountInputCurrency, 'Discount input has value').toHaveAttribute('hasValue', 'true');
    await expect(this.discountInputCurrency, 'Discount input currency').toHaveAttribute('currency', currency);
  }
  /**
   * Validate discount exists and is enabled.
   * @param isDisplayed Whether the discount container should be displayed.
   * @param isEnabled Whether the discount amount input should be enabled.
   */
  async validateDiscountExistsAndIsEnabled({ isDisplayed = true, isEnabled = true }: { isDisplayed?: boolean; isEnabled?: boolean } = {}): Promise<void> {
    console.log('Validate discount exists and is enabled');
    await this.validateDisplayState(this.discountContainer, 'Discount container', isDisplayed);
    if (isDisplayed) await this.validateEnabledState(this.discountAmountInput, 'Discount amount input enabled state', isEnabled);
  }
  /** Validate discount applied on total cost. */
  async validateDiscountAppliedOnTotalCost({ discountAmount, initialTotal }: { discountAmount?: string | number; initialTotal?: string | number } = {}): Promise<void> {
    console.log('Validate discount applied on total cost');
    if (discountAmount !== undefined && initialTotal !== undefined) {
      const displayedTotal = (await this.totalCostAmountLabel.textContent() ?? '').replace(/[^0-9.,-]/g, '').replace(',', '.');
      const expectedTotal = Number(initialTotal) - Number(discountAmount || 0);
      await expect(Number(displayedTotal), 'Discounted total amount').toBeCloseTo(expectedTotal, 2);
    } else {
      await expect(this.bookingSummaryPreviousTotalLabel, 'Previous total label after discount').toBeVisible();
    }
  }
  /** Validate discount section data. */
  async validateData(): Promise<void> {
    console.log('Validate discount section data');
    await expect(this.discountLabel, 'Discount label').toContainText(await Strings.DISCOUNT.name);
    await expect(this.discountDescription, 'Discount description').toContainText(await Strings.DISCOUNT_AMOUNT_DESCRIPTION.name);
    await expect(this.discountAmountInput, 'Discount amount placeholder').toHaveAttribute('placeholder', await Strings.DISCOUNT_AMOUNT_INPUT.name);
    await expect(this.discountAmountInput, 'Discount amount input enabled').toBeEnabled();
  }
  /** Validate section elements alignment. */
  async validateSectionElementsAlignment(): Promise<void> {
    console.log('Validate discount section elements alignment');
    await expect(this.discountLabel, 'Discount label in section').toBeVisible();
    await expect(this.discountDescription, 'Discount description in section').toBeVisible();
    await expect(this.discountAmountInput, 'Discount amount input in section').toBeVisible();
  }
  /** Wait for discount to be applied. */
  async waitForDiscountToBeApplied({ initialTotal, discountValue }: { initialTotal?: string | number; discountValue?: string | number } = {}): Promise<void> {
    console.log('Wait for discount to be applied');
    await expect(this.bookingSummaryPreviousTotalLabel, 'Previous total label after discount').toBeVisible();
    if (initialTotal !== undefined && discountValue !== undefined) {
      await expect(this.totalCostAmountLabel, 'Discounted total amount').toContainText((Number(initialTotal) - Number(discountValue)).toFixed(2));
    }
  }
}