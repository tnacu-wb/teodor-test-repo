import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../../test-data/pib/ibStrings";
import { ReportCardsSectionComponent } from "./reportCardsSection.component";
/** InnBusiness application > Spending > Company spending section */
export class CompanySpendingSectionComponent {
  private readonly page: Page = global.page;
  static readonly COMPANY_SPENDING_TAB_URL = "/spending?tab=company";
  static readonly REPORT_CARDS_CONTAINER_XPATH =
    '//a[@data-testid="ManagementInformationReportCard"]/parent::div';
  static readonly REPORT_CARD_CONTAINERS_XPATH =
    '//a[@data-testid="ManagementInformationReportCard"]/parent::div/a';
  // ######## UI elements/properties ########
  readonly companySpendingContainer: Locator = this.page.getByTestId( "CompanyTab-container", );
  readonly subtitleLabel: Locator = this.page.getByTestId( "CompanyTab-Spent-This-Month-Subheading", );
  readonly spendingDateIntervalLabel: Locator = this.page.getByTestId( "CompanyTab-Spent-This-Month-Date", );
  readonly spentThisMonthEurButton: Locator = this.page.getByTestId( "CompanyTab-Spent-This-Month-Card-EUR", );
  readonly spentThisMonthGbpButton: Locator = this.page.getByTestId( "CompanyTab-Spent-This-Month-Card-GBP", );
  readonly reportCards = new ReportCardsSectionComponent({
    reportCardsContainerSelector:
      CompanySpendingSectionComponent.REPORT_CARDS_CONTAINER_XPATH,
    reportCardContainersSelector:
      CompanySpendingSectionComponent.REPORT_CARD_CONTAINERS_XPATH,
  });
  /** Determine whether EUR has the greater value. */
  async isEurTheBiggerCurrency(
    items: { bookingCurrency: string; bookingValue: number }[],
  ): Promise<boolean> {
    console.log("Compare EUR and GBP spending");
    return (
      (items.find((item) => item.bookingCurrency === "EUR")?.bookingValue ??
        0) >
      (items.find((item) => item.bookingCurrency === "GBP")?.bookingValue ?? 0)
    );
  }
  // ######## UI actions/navigation ########
  /** Click EUR spending card. */
  async clickSpentThisMonthEurButton(): Promise<void> {
    console.log("Click spent this month EUR");
    await this.spentThisMonthEurButton.click();
  }
  /** Click GBP spending card. */
  async clickSpentThisMonthGbpButton(): Promise<void> {
    console.log("Click spent this month GBP");
    await this.spentThisMonthGbpButton.click();
  }
  // ######## UI validations ########
  /** Validate subtitle. */
  async validateSubtitle(): Promise<void> {
    console.log("Validate company spending subtitle");
    await expect(this.subtitleLabel, "Company spending subtitle").toHaveText( await IbStrings.SPENDING_AND_REPORTING_SUBHEADING.name, );
  }
  /** Validate spent this month section. */
  async validateSpentThisMonth({
    companySpendingCurrentMonthArray,
  }: {
    companySpendingCurrentMonthArray: {
      bookingCurrency: string;
      bookingValue: number;
    }[];
    isUkCompany?: boolean;
  }): Promise<void> {
    console.log("Validate company spent this month");
    await this.validateSubtitle();
    for (const item of companySpendingCurrentMonthArray)
      await expect( item.bookingCurrency === "EUR" ? this.spentThisMonthEurButton : this.spentThisMonthGbpButton, `${item.bookingCurrency} spending card`, ).toBeVisible();
  }
}
