import { type Locator, type Page } from '@playwright/test';

/** Shared total-cost section on the confirmation page. */
export class TotalCostSectionBaseComponent {
  private readonly page: Page = global.page;
  // ######## UI elements/properties ########
  readonly totalCostSectionContainer: Locator = this.page.locator('[data-testid="TotalCostConfirm-container"]');
  readonly totalCostConfirmLabel: Locator = this.page.locator('[data-testid="TotalCostConfirm-label"]');
  readonly totalCostPaymentMessageLabel: Locator = this.page.locator('[data-testid="TotalCostConfirm-paymentMessage"]');
  readonly totalCostLabel: Locator = this.page.locator('[data-testid="TotalCostConfirm-amount"]');
  readonly totalCostCityTaxMessage: Locator = this.page.locator('[data-testid="TotalCostConfirm-TaxesMessage"]');

  // ######## UI actions/navigation ########

  // ######## UI validations ########
}