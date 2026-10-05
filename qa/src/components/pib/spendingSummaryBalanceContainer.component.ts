import { expect, type Locator, type Page } from "@playwright/test";

/** Spending summary balance container. */
export class SpendingSummaryBalanceContainerComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########
  readonly sectionContainer: Locator;
  readonly balanceLabel: Locator;
  readonly balanceAmountLabel: Locator;

  /** Balance Container constructor. */
  constructor({ containerSelector }: { containerSelector: string }) {
    this.sectionContainer = this.page.locator(containerSelector);
    this.balanceLabel = this.sectionContainer.locator("div span");
    this.balanceAmountLabel = this.sectionContainer.locator(":scope > span");
  }

  // ######## UI actions/navigation ########
  /** Get balance label from actual container. */
  async getBalanceLabel(): Promise<string> {
    console.log("Get spending summary balance label");
    return (await this.balanceLabel.textContent()) ?? "";
  }

  // ######## UI validations ########
  /** Validate spending summary balance container based on account balance response. */
  async validateBalanceContainer({
    currency,
    isSuspended = false,
  }: {
    currency: { currencySymbol: string; amount: string | number };
    isSuspended?: boolean;
  }): Promise<void> {
    console.log("Validate spending summary balance container");
    await expect( this.sectionContainer, `Spending summary ${await this.getBalanceLabel()} container`, ).toBeVisible();
    await expect(this.balanceAmountLabel, "Balance amount").toHaveText( isSuspended ? "N/A" : `${currency.currencySymbol}${currency.amount}`, );
  }
}
