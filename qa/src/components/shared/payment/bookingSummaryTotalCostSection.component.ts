import { type Page, type Locator, expect } from '@playwright/test';
import { Constants } from '@test-data/constants';
import { Strings } from '@test-data/strings';
import { type BookingInformation } from '@api/response/bookingInformation';
import { PriceHelpers } from '../../../utils';

/**
 * The booking summary total cost section present on the Payment page vertical strip containing
 * the UI elements, custom actions and validations. Mirrors qa/reference
 * `components/common/payment/bookingSummaryTotalCostSectionBase.js`.
 */
export class PaymentBookingSummaryTotalCostSectionComponent {
  private readonly page: Page = global.page;

  private get resolutionId(): string {
    return Constants.RESOLUTION_IDENTIFIER_FOR_DATATESTID_ATTRIBUTE;
  }

  // ######## UI elements/properties ########

  get totalPriceContainer(): Locator {
    return this.page.locator(`div[data-testid="BookingSummary-${this.resolutionId}-TotalCost-Wrapper"]`);
  }

  get totalPriceLabel(): Locator {
    return this.totalPriceContainer.locator(`h2[data-testid="BookingSummary-${this.resolutionId}-TotalCost-TotalCostPrice"]`);
  }

  get totalPriceValueLabel(): Locator {
    return this.totalPriceContainer.locator(`h2[data-testid="BookingSummary-${this.resolutionId}-TotalCost-CostAmount"]`);
  }

  get vatLabel(): Locator {
    return this.totalPriceContainer.locator(`p[data-testid="BookingSummary-${this.resolutionId}-TotalCost-VATMessage"]`);
  }

  // ######## UI actions/navigation ########

  /** Get the numeric total cost amount displayed in the booking summary. */
  async getTotalCostValue(): Promise<number> {
    console.log('Get numeric total cost amount from booking summary');
    await this.totalPriceValueLabel.waitFor({ state: 'visible' });
    return PriceHelpers.getPriceAmountFromUiLabel(await this.totalPriceValueLabel.innerText());
  }

  /** Get the displayed total cost text. */
  async getTotalCostLabel(): Promise<string> {
    console.log('Get displayed total cost label');
    await this.totalPriceValueLabel.waitFor({ state: 'visible' });
    return this.totalPriceValueLabel.innerText();
  }

  /** Validate the total price amount and currency. */
  async validateTotalPrice(amount: number, currency: string): Promise<void> {
    console.log(`Validate total price ${amount} ${currency}`);
    await expect(this.totalPriceContainer, 'Total price container').toBeVisible();
    await expect(this.totalPriceLabel, 'Total label text').toContainText(await Strings.TOTAL_COST.name);
    expect(await this.getTotalCostValue(), 'Total cost amount').toBe(amount);
  }

  /** Validate the numeric total cost amount. */
  async validateTotalCostAmount(amount: number, message = ''): Promise<void> {
    console.log(`Validate total cost amount ${amount}`);
    expect(await this.getTotalCostValue(), `Total cost amount not as expected. ${message}`).toBe(amount);
  }

  /** Validate the VAT and fees label. */
  async validateVatLabel(): Promise<void> {
    console.log('Validate VAT label is correct');
    await expect(this.vatLabel, 'VAT label text').toContainText(await Strings.INCLUDED_VAT_AND_FEES.name);
  }

  /** Validate total cost information against booking data. */
  async validateData(bookingInformation: BookingInformation): Promise<void> {
    console.log(`Validate booking summary total cost: ${JSON.stringify(bookingInformation)}`);
    await this.validateTotalPrice(Number(bookingInformation.totalCost ?? 0), String(bookingInformation.currencyCode ?? ''));
    await this.validateVatLabel();
  }

  // ######## UI validations ########
}
