import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../../test-data/pib/ibStrings";
/** InnBusiness application > Spending > Your spending section */
export class YourSpendingSectionComponent {
  private readonly page: Page = global.page;
  static readonly YOUR_SPENDING_TAB_URL = "/spending?tab=your-spending";
  // ######## UI elements/properties ########
  readonly yourSpendingContainer: Locator = this.page.getByTestId( "YourSpendingTab-container", );
  readonly subtitleLabel: Locator = this.page.getByTestId( "YourSpendingTab-Spent-This-Month-Subheading", );
  readonly spendingDateIntervalLabel: Locator = this.page.getByTestId( "YourSpendingTab-Spent-This-Month-Date", );
  readonly spentThisMonthEurButton: Locator = this.page.getByTestId( "YourSpendingTab-Spent-This-Month-Card-EUR", );
  readonly spentThisMonthGbpButton: Locator = this.page.getByTestId( "YourSpendingTab-Spent-This-Month-Card-GBP", );
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
  /** Validate Your Spending section. */
  async validateYourSpendingSection(): Promise<void> {
    console.log("Validate Your Spending section");
    await expect( this.yourSpendingContainer, "Your spending container", ).toBeVisible();
    await expect(this.subtitleLabel, "Your spending subtitle").toHaveText( await IbStrings.SPENDING_AND_REPORTING_SUBHEADING.name, );
  }
  /** Validate spent this month section. */
  async validateSpentThisMonth({
    employeeSpendCurrentMonthArray,
  }: {
    employeeSpendCurrentMonthArray: {
      bookingCurrency: string;
      bookingValue: number;
    }[];
    isUkCompany?: boolean;
  }): Promise<void> {
    console.log("Validate Your Spending this month");
    await this.validateYourSpendingSection();
    for (const item of employeeSpendCurrentMonthArray)
      await expect( item.bookingCurrency === "EUR" ? this.spentThisMonthEurButton : this.spentThisMonthGbpButton, `${item.bookingCurrency} spending card`, ).toBeVisible();
  }
}
