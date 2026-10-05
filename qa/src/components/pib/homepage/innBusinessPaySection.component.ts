import { expect, type Locator, type Page } from "@playwright/test";
import { AccountHolderSectionComponent } from "../accountHolderSection.component";
import { InnBusinessPayNoAccountBannerComponent } from "../innBusinessPayNoAccountBanner.component";
import { SpendingSummarySectionComponent } from "../spendingSummarySection.component";
import { IbStrings } from "../../../test-data/pib/ibStrings";

/** Inn Business Pay section from Homepage. */
export class HomepageInnBusinessPaySectionComponent {
  private readonly page: Page = global.page;
  // ######## properties ########
  // ######## UI elements/properties ########
  readonly welcomeTitleLabel: Locator = this.page.getByTestId( "HomePage-Welcome-Title", );
  readonly companySpendingLabel: Locator = this.page.getByTestId( "HomePage-Welcome-SubTitle", );
  readonly companySpendingLink: Locator = this.page.locator( 'a[data-testid*="HomePage-Welcome-SpendingLink"]', );
  readonly noAccountBannerContainer: Locator = this.page.getByTestId( "NoAccountBanner-Container", );
  // ######## UI components ########
  readonly accountHolderSection = new AccountHolderSectionComponent();
  readonly innBusinessPayNoAccountBanner =
    new InnBusinessPayNoAccountBannerComponent();
  readonly spendingSummarySection = new SpendingSummarySectionComponent();
  // ######## UI actions/navigation ########
  /** Click the company spending link. */
  async clickCompanySpendingLink(): Promise<void> {
    console.log("Click company spending link");
    await this.companySpendingLink.click();
  }
  // ######## UI validations ########
  /** Validate the company name in the home page subtitle. */
  async validateCompanyNameIsReflectedOnHomePage(
    companyName: string,
  ): Promise<void> {
    console.log("Validate company name on Home Page");
    await expect( this.companySpendingLabel, "Company name in welcome subtitle", ).toContainText(companyName);
  }
  /** Validate IB banner visibility. */
  async validateIbBanner({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log("Validate IB banner");
    if (isDisplayed)
      await expect(this.noAccountBannerContainer, "IB banner").toBeVisible();
    else
      await expect( this.noAccountBannerContainer, "IB banner", ).not.toBeVisible();
  }
  /** Validate the Inn Business Pay section for user role and account state. */
  async validateInnBusinessPaySection({
    isTravelManager,
    tetheredAccount,
  }: {
    isTravelManager: boolean;
    tetheredAccount: boolean;
    customerAccountCurrentBalancesResponse?: unknown;
  }): Promise<void> {
    console.log("Validate Inn Business Pay section");
    if (isTravelManager && !tetheredAccount) {
      await this.validateIbBanner();
      await expect( this.spendingSummarySection.spendingSummaryContainer, "Spending summary container", ).not.toBeVisible();
    } else if (!isTravelManager) {
      await this.validateIbBanner({ isDisplayed: false });
      await expect( this.spendingSummarySection.spendingSummaryContainer, "Spending summary container", ).not.toBeVisible();
    }
  }
  /** Validate the company name displayed in the welcome subtitle. */
  async validateWelcomeSubtitleCompanyName(companyName: string): Promise<void> {
    console.log("Validate welcome subtitle company name");
    await expect( this.companySpendingLabel, "Welcome subtitle company name", ).toContainText(companyName);
  }
  /** Validate the home page heading. */
  async validatePageHeading({
    firstName,
    companyName,
    isTravelManager,
    companySpendingCurrentMonth,
  }: {
    firstName: string;
    companyName: string;
    isTravelManager: boolean;
    companySpendingCurrentMonth?: {
      bookingValue: string | number;
      bookingCurrency: string;
    };
  }): Promise<void> {
    console.log("Validate Home Page heading");
    await expect(this.welcomeTitleLabel, "Welcome title").toHaveText( `${await IbStrings.HOME_WELCOME.name} ${firstName}`, );
    if (isTravelManager && companySpendingCurrentMonth)
      await expect( this.companySpendingLabel, "Company spending subtitle", ).toContainText(companyName);
    else
      await expect( this.companySpendingLabel, "Company welcome subtitle", ).toContainText(companyName);
  }
}
