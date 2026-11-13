import { expect, type Locator, type Page } from "@playwright/test";
import { SpendingSummaryBalanceContainerComponent } from "./spendingSummaryBalanceContainer.component";
/** InnBusiness application > Home / Spending > InnBusiness Pay section -> Spending summary section */
export class SpendingSummarySectionComponent {
  private readonly page: Page = global.page;
  // ######## UI elements/properties ########
  readonly spendingSummaryContainer: Locator = this.page.getByTestId( "SpendingSummary-Container", );
  readonly spendingSummaryTitleLabel: Locator = this.page.getByTestId( "SpendingSummary-Title", );
  readonly viewSpendingDetailsLink: Locator = this.page.getByTestId( "HomePage-InnBusinessPay-ViewSpendingDetails-Button", );
  readonly progressBar: Locator = this.page.getByTestId( "SpendingSummary-ProgressBar", );
  readonly progressBarLegend: Locator = this.page.getByTestId( "SpendingSummary-Legend", );
  readonly progressBarLegendContainers: Locator = this.progressBarLegend.locator("div");
  readonly balanceContainers: Locator = this.page.locator( '//div[@data-testid="SpendingSummary-Balance"]/div', );
  readonly manageCreditLimitButton: Locator = this.page.getByTestId( "SpendingSummary-Manage-credit-limit-Button", );
  readonly makeAPaymentButton: Locator = this.page.getByTestId( "SpendingSummary-Make-a-payment-Button", );
  readonly upcomingSpendingContainer: Locator = this.page.getByTestId( "SpendingSummary-UpcomingWrapper", );
  readonly upcomingSpendingAlertIcon: Locator = this.upcomingSpendingContainer.locator("div.relative img");
  readonly upcomingSpendingTitleLabel: Locator = this.upcomingSpendingContainer.locator("h3.font-semibold");
  readonly upcomingSpendingSubtitleLabel: Locator = this.upcomingSpendingContainer.locator("p");
  readonly upcomingSpendingButton: Locator = this.upcomingSpendingContainer.locator("button");
  readonly upcomingSpendingArrow: Locator = this.upcomingSpendingContainer.locator("svg");
  readonly upcomingSpendingExpendedContainer: Locator = this.upcomingSpendingContainer.locator(".overflow-hidden");
  readonly expectedSpendTodayLabel: Locator = this.upcomingSpendingContainer .locator("div.flex-row") .nth(0);
  readonly expectedSpendTodayIcon: Locator = this.upcomingSpendingContainer .getByTestId("SpendingSummary-title-icon") .nth(0);
  readonly expectedSpendTodayDateLabel: Locator = this.upcomingSpendingContainer .locator("div.flex-row ~ p.text-sm") .nth(0);
  readonly expectedSpendTodayAmountLabel: Locator = this.upcomingSpendingContainer .locator("div.flex-row ~ span.text-2xl") .nth(0);
  readonly expectedNextBillingLabel: Locator = this.upcomingSpendingContainer .locator("div.flex-row") .nth(1);
  readonly expectedNextBillingIcon: Locator = this.upcomingSpendingContainer .getByTestId("SpendingSummary-title-icon") .nth(1);
  readonly expectedNextBillingDateLabel: Locator = this.upcomingSpendingContainer.locator("div.flex-row ~ p.text-sm").nth(1);
  readonly expectedNextBillingAmountLabel: Locator = this.upcomingSpendingContainer .locator("div.flex-row ~ span.text-2xl") .nth(1);
  readonly expectedNextPeriodLabel: Locator = this.upcomingSpendingContainer .locator("div.flex-row") .nth(2);
  readonly expectedNextPeriodIcon: Locator = this.upcomingSpendingContainer .getByTestId("SpendingSummary-title-icon") .nth(2);
  readonly expectedNextPeriodDateLabel: Locator = this.upcomingSpendingContainer .locator("div.flex-row ~ p.text-sm") .nth(2);
  readonly expectedNextPeriodAmountLabel: Locator = this.upcomingSpendingContainer .locator("div.flex-row ~ span.text-2xl") .nth(2);
  readonly upcomingSpendingFirstDescriptionLabel: Locator = this.upcomingSpendingContainer.locator("h3 ~ div div.space-y-4 ~ p");
  readonly upcomingSpendingSecondDescriptionLabel: Locator = this.upcomingSpendingContainer.locator("h3 ~ div div.space-y-4 ~ p.mt-6");
  readonly upcomingSpendingThirdDescriptionLabel: Locator = this.upcomingSpendingContainer.locator("h3 ~ div div.space-y-4 ~ span");
  readonly manageYourCreditLimitLinkButton: Locator = this.page.getByTestId( "SpendingSummary-Inline-Manage-credit-limit-Button", );
  readonly makeAnInterimPaymentLinkButton: Locator = this.page.getByTestId( "SpendingSummary-Inline-Make-a-payment-Button", );
  readonly upcomingSpendingSuspendedAccountContainer: Locator = this.page.getByTestId("SpendingSummary-CreditLimitExceed");
  readonly upcomingSpendingSuspendedAccountAlertIcon: Locator = this.page.getByTestId("SpendingSummary-CreditLimitExceedIcon");
  readonly upcomingSpendingSuspendedAccountTitleLabel: Locator = this.page.getByTestId("SpendingSummary-CreditLimitExceedTitle");
  readonly upcomingSpendingSuspendedAccountSubtitleLabel: Locator = this.page.getByTestId("SpendingSummary-CreditLimitExceedDescription");
  /** Returns progress bar balance container element based on index. */
  getProgressBarLegendBalanceContainerBasedOnIndex(
    index: number,
  ): SpendingSummaryBalanceContainerComponent {
    return new SpendingSummaryBalanceContainerComponent({
      containerSelector: `[data-testid="SpendingSummary-Legend"] > div:nth-child(${index + 1})`,
    });
  }
  /** Returns balance container element based on index. */
  getBalanceContainerBasedOnIndex(
    index: number,
  ): SpendingSummaryBalanceContainerComponent {
    return new SpendingSummaryBalanceContainerComponent({
      containerSelector: `[data-testid="SpendingSummary-Balance"] > div:nth-child(${index + 1})`,
    });
  }
  /** Get progress bar balance container based on title. */
  async getProgressBarLegendContainerByTitle(
    title: string,
    exactMatch = true,
  ): Promise<SpendingSummaryBalanceContainerComponent> {
    console.log("Get progress bar legend container by title");
    const count = await this.progressBarLegendContainers.count();
    for (let index = 0; index < count; index += 1) {
      const item = this.getProgressBarLegendBalanceContainerBasedOnIndex(index);
      const label = (await item.getBalanceLabel()).trim();
      if (exactMatch ? label === title : label.includes(title)) return item;
    }
    throw new Error(`${title} balance container was not found!`);
  }
  /** Get balance container based on title. */
  async getBalanceContainerByTitle(
    title: string,
    exactMatch = true,
  ): Promise<SpendingSummaryBalanceContainerComponent> {
    console.log("Get balance container by title");
    const count = await this.balanceContainers.count();
    for (let index = 0; index < count; index += 1) {
      const item = this.getBalanceContainerBasedOnIndex(index);
      const label = (await item.getBalanceLabel()).trim();
      if (exactMatch ? label === title : label.includes(title)) return item;
    }
    throw new Error(`${title} balance container was not found!`);
  }
  // ######## UI actions/navigation ########
  /** Click on View Spending Details link. */
  async clickViewSpendingDetailsLink(): Promise<void> {
    console.log("Click on View Spending Details link");
    await this.viewSpendingDetailsLink.click();
  }
  /** Click on manage your credit limit link button. */
  async toggleUpcomingSpendingContainer(
    _shouldBeExpanded = true,
  ): Promise<void> {
    console.log("Toggle Upcoming spending container");
    await this.upcomingSpendingButton.click();
  }
  /** Click on make an interim payment link button. */
  async clickManageYourCreditLimitLinkButton(): Promise<void> {
    console.log("Click on manage your credit limit link button");
    await this.manageYourCreditLimitLinkButton.click();
  }
  /** Click on manage credit limit button. */
  async clickMakeAnInterimPaymentLinkButton(): Promise<void> {
    console.log("Click on make an interim payment link button");
    await this.makeAnInterimPaymentLinkButton.click();
  }
  /** Click on make a payment button. */
  async clickManageCreditLimitButton(): Promise<void> {
    console.log("Click on manage credit limit button");
    await this.manageCreditLimitButton.click();
  }
  /** Validate Spending summary title. */
  async clickMakeAPaymentButton(): Promise<void> {
    console.log("Click on make a payment button");
    await this.makeAPaymentButton.click();
  }
  // ######## UI validations ########
  /** Validate that balance amount displayed is the one retrieved form graphql getAccountBalanceSummary query. */
  async validateSpendingSummaryTitle(): Promise<void> {
    console.log("Validate Spending summary title");
    await expect( this.spendingSummaryTitleLabel, "Spending summary title", ).toBeVisible();
  }
  /** Validate progress bar legend against AEM dictionary and graphql getAccountBalanceSummary response. */
  async validateBalanceAmount({
    element,
    currency,
  }: {
    element: Locator;
    currency: { currencySymbol: string; amount: string | number };
  }): Promise<void> {
    console.log("Validate balance amount");
    await expect(element, "Balance amount").toHaveText( `${currency.currencySymbol}${currency.amount}`, );
  }
  /** Validate spending summary section against AEM dictionary and graphql getAccountBalanceSummary response. */
  async validateProgressBarLegend(
    _data: { currentBalanceItem: unknown; isSuspended?: boolean } = {
      currentBalanceItem: null,
    },
  ): Promise<void> {
    console.log("Validate progress bar legend");
    await expect(this.progressBar, "Progress bar").toBeVisible();
  }
  /** Validate Upcoming spending data. */
  async validateSpendingSummary(_data: {
    customerAccountCurrentBalancesResponse: unknown;
  }): Promise<void> {
    console.log("Validate spending summary");
    await this.validateSpendingSummaryTitle();
    await expect(this.progressBar, "Progress bar").toBeVisible();
  }
  /** Validate suspended Upcoming spending data. */
  async validateUpcomingSpendingData({
    isDisplayed = true,
  }: {
    isDisplayed?: boolean;
    isExpanded?: boolean;
    accountUpcomingSpending?: unknown;
    isLessThan10?: boolean;
    isUkCompany?: boolean;
  } = {}): Promise<void> {
    console.log("Validate Upcoming spending container data");
    if (isDisplayed)
      await expect( this.upcomingSpendingContainer, "Upcoming spending container", ).toBeVisible();
    else
      await expect( this.upcomingSpendingContainer, "Upcoming spending container", ).not.toBeVisible();
  }
  /** Validate that Inn Business Pay spending summary component is displayed or not. */
  async validateSuspendedUpcomingSpendingData(): Promise<void> {
    console.log("Validate suspended Upcoming spending data");
    await expect( this.upcomingSpendingSuspendedAccountContainer, "Suspended upcoming spending container", ).toBeVisible();
  }
  /** Validate spending summary is displayed. */
  async validateSpendingSummaryIsDisplayed(
    isDisplayed: boolean,
  ): Promise<void> {
    console.log(
      `Validate that Inn Business Pay spending summary component is displayed=${isDisplayed}`,
    );
    if (isDisplayed)
      await expect( this.spendingSummaryContainer, "Spending summary container", ).toBeVisible();
    else
      await expect( this.spendingSummaryContainer, "Spending summary container", ).not.toBeVisible();
  }
}
