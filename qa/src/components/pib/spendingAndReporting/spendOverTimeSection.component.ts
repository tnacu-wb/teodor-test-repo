import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../../test-data/pib/ibStrings";
/** Spend over time chart */
export class SpendOverTimeSectionComponent {
  private readonly page: Page = global.page;
  // ######## UI elements/properties ########
  readonly spendOverTimeSection: Locator = this.page.locator( 'div[data-testid*="Spend-Over-Time"]', );
  readonly spendOverTimeTitleWrapper: Locator = this.page .getByTestId("CompanyTab-Spend-Over-Time-Title") .locator("xpath=parent::div");
  readonly spendOverTimeTitleLabel: Locator = this.page.locator( 'h1[data-testid*="Spend-Over-Time-Title"]', );
  readonly spendOverTimeChartContainer: Locator = this.page.locator( 'div[data-testid="CompanyTab-recharts-container"], div[data-testid="InnBusinessPayTab-recharts-container"]', );
  readonly spendOverTimeTooltip: Locator = this.page.getByTestId( "CompanyTab-title-icon", );
  readonly spendOverTimeTooltipLabel: Locator = this.spendOverTimeTooltip.locator("xpath=following-sibling::div[1]//span");
  readonly spendOverTimeDownloadButton: Locator = this.page.getByTestId( "InnBusinessPayTab-DownloadButton", );
  readonly spendOverTimeChartXAxisLabels: Locator = this.page.locator( "g.recharts-xAxis text", );
  readonly spendOverTimeChartBars: Locator = this.page.locator( "g.recharts-bar-rectangle path", );
  readonly spendOverTimeChartBarLabel: Locator = this.page.locator( "div.recharts-tooltip-label", );
  readonly spendOverTimeChartBarCostLabel: Locator = this.page.locator( "div.recharts-tooltip-cost > div", );
  readonly spendOverTimeChartYAxisLabels: Locator = this.page.locator( "g.recharts-yAxis text", );
  readonly viewListByMonthButton: Locator = this.page.locator( 'button[data-testid*="Accordion-Trigger"]', );
  readonly viewListByMonthTitleLabel: Locator = this.page.locator( 'h1[data-testid*="Accordion-Title"]', );
  readonly viewListByMonthEntries: Locator = this.page.locator( 'div[data-testid*="Spend-Over-Time-Per-Months"] ul', );
  /** Get month list item. */
  getViewListByMonthItemBasedOnIndex(index: number): Locator {
    return this.viewListByMonthEntries.nth(index);
  }
  /** Get chart bar. */
  getChartBarElementBasedOnIndex(index: number): Locator {
    return this.spendOverTimeChartBars.nth(index);
  }
  // ######## UI actions/navigation ########
  /** Hover chart bar. */
  async hoverOverBarChartBasedOnIndex(index: number): Promise<void> {
    console.log("Hover chart bar");
    await this.getChartBarElementBasedOnIndex(index).hover();
  }
  /** Hover tooltip. */
  async hoverOverTooltip(): Promise<void> {
    console.log("Hover tooltip");
    await this.spendOverTimeTooltip.hover();
  }
  /** Click view list by month. */
  async clickOnViewListByMonth(): Promise<void> {
    console.log("Click view list by month");
    await this.viewListByMonthButton.click();
  }
  /** Download CSV. */
  async clickOnDownloadCSVButton(): Promise<void> {
    console.log("Click spend over time download");
    await this.spendOverTimeDownloadButton.click();
  }
  // ######## UI validations ########
  /** Validate spend over time section. */
  async validateSpendOverTimeSection(
    _data: {
      isSuspendedAccount?: boolean;
      isEurSelected?: boolean;
      isUkCompany?: boolean;
    } = {},
  ): Promise<void> {
    console.log("Validate spend over time section");
    await expect( this.spendOverTimeSection, "Spend over time section", ).toBeVisible();
    await expect( this.spendOverTimeTitleLabel, "Spend over time title", ).toHaveText(await IbStrings.SPENDING_AND_REPORTING_CHART_TITLE.name);
  }
  /** Validate section is hidden. */
  async validateSpendOverTimeSectionIsNotDisplayed(): Promise<void> {
    console.log("Validate spend over time hidden");
    await expect( this.spendOverTimeSection, "Spend over time section", ).not.toBeVisible();
  }
  /** Validate chart bars. */
  async validateChartBars({
    isDisplayed = true,
    spendingAmounts,
  }: {
    isDisplayed?: boolean;
    spendingMonths?: string[];
    spendingAmounts?: string[];
    isEurTheBiggerCurrency?: boolean;
    isAccountSpending?: boolean;
  }): Promise<void> {
    console.log("Validate chart bars");
    if (isDisplayed)
      await expect( this.spendOverTimeChartBars, "Spend over time chart bars", ).toHaveCount(spendingAmounts?.length ?? 0);
    else
      await expect( this.spendOverTimeChartBars, "Spend over time chart bars", ).toHaveCount(0);
  }
  /** Validate view list by month. */
  async validateViewListByMonth({
    isDisplayed = true,
  }: {
    isDisplayed?: boolean;
    spendingMonths?: string[];
    spendingAmounts?: string[];
    isEurTheBiggerCurrency?: boolean;
    isAccountSpending?: boolean;
  }): Promise<void> {
    console.log("Validate view list by month");
    if (isDisplayed)
      await expect( this.viewListByMonthButton, "View list by month button", ).toBeVisible();
    else
      await expect( this.viewListByMonthButton, "View list by month button", ).not.toBeVisible();
  }
  /** Validate tooltip. */
  async validateTooltip(): Promise<void> {
    console.log("Validate spend over time tooltip");
    await expect( this.spendOverTimeTooltip, "Spend over time tooltip", ).toBeVisible();
    await this.hoverOverTooltip();
    await expect( this.spendOverTimeTooltipLabel, "Spend over time tooltip label", ).toHaveText(await IbStrings.SPENDING_AND_REPORTING_TOOLTIP.name);
  }
}
