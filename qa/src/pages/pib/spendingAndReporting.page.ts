import { expect, type Locator } from "@playwright/test";
import {
  CompanySpendingSectionComponent,
  ExistingAccountSectionComponent,
  LinkExistingAccountSectionComponent,
  SpendingInnBusinessPaySectionComponent,
  ToastNotificationSectionComponent,
  YourSpendingSectionComponent,
} from "../../components/pib";
import { Constants } from "../../test-data/constants";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { BasePibPage } from "./basePib.page";

/** InnBusiness application > Spending > Spending and reporting */
export class SpendingAndReportingPage extends BasePibPage {
  readonly url = "spending";
  // ######## UI elements/properties ########
  readonly spendingTitleLabel: Locator = this.page.getByTestId("Spending-Title");
  readonly companySpendingTabLabel: Locator = Constants.BROWSER_RESOLUTIONS.isDesktop() ? this.page.getByTestId("SpendingTabs-InnBusinessTab") : this.page.locator('//a[@data-testid="-Sidebar-Link"][1]');
  readonly innBusinessPayTabLabel: Locator = Constants.BROWSER_RESOLUTIONS.isDesktop() ? this.page.getByTestId("SpendingTabs-innbusinessPayTab") : this.page.locator('//a[@data-testid="-Sidebar-Link"][2]');
  readonly yourSpendingTabLabel: Locator = Constants.BROWSER_RESOLUTIONS.isDesktop() ? this.page.getByTestId("SpendingTabs-YourSpendingTab") : this.page.getByTestId("Spending-Your-Spending-Sidebar-Link");
  readonly companySpendingSection = new CompanySpendingSectionComponent();
  readonly innBusinessPaySection = new SpendingInnBusinessPaySectionComponent();
  readonly linkExistingAccountSection =
    new LinkExistingAccountSectionComponent();
  readonly existingAccountModal = new ExistingAccountSectionComponent();
  readonly toastNotification = new ToastNotificationSectionComponent();
  readonly yourSpendingSection = new YourSpendingSectionComponent();
  // ######## UI actions/navigation ########
  /** Navigate to Spending and reporting. */
  async navigateToPage(): Promise<void> {
    console.log("Navigate to Spending and reporting");
    await this.openPath(this.url);
  }
  /** Open IB Spending and reporting page. */
  async open(): Promise<void> {
    console.log("Open IB Spending and reporting page");
    await this.openPath(this.url);
    await this.validatePage();
  }
  /** Click Company spending tab. */
  async clickCompanySpendingTab(): Promise<void> {
    console.log("Click Company Spending tab");
    await this.companySpendingTabLabel.click();
  }
  /** Click InnBusiness Pay tab. */
  async clickInnBusinessPayTab(): Promise<void> {
    console.log("Click InnBusiness Pay tab");
    await this.innBusinessPayTabLabel.click();
    await expect( this.innBusinessPaySection.innBusinessPaySection, "InnBusiness Pay tab content", ).toBeVisible();
  }
  /** Click Your Spending tab. */
  async clickYourSpendingTab(): Promise<void> {
    console.log("Click Your Spending tab");
    await this.yourSpendingTabLabel.scrollIntoViewIfNeeded();
    await this.yourSpendingTabLabel.click();
  }
  // ######## UI validations ########
  /** Check we reached the current page by checking a specific element from the page. */
  async validatePage(): Promise<void> {
    console.log("Validate Spending and reporting page");
    await this.validatePageMarker(
      this.spendingTitleLabel,
      "Spending and reporting",
    );
  }
  /** Validate Spending title label. */
  async validateSpendingTitleLabel(): Promise<void> {
    console.log("Validate Spending title label");
    await expect(this.spendingTitleLabel, "Spending title label").toHaveText( await IbStrings.SPENDING_AND_REPORTING.name, );
  }
  /** Validate Spending tabs for a role. */
  async validateSpendingTabs({
    userRole = "Travel Manager",
  }: { userRole?: string } = {}): Promise<void> {
    console.log("Validate Spending tabs");
    if (userRole === "Travel Manager")
      await expect( this.companySpendingTabLabel, "Company spending tab label", ).toHaveText(await IbStrings.COMPANY_SPENDING.name);
    else
      await expect( this.companySpendingTabLabel, "Company spending tab label", ).not.toBeVisible();
    if (userRole === "Guest") {
      await expect( this.yourSpendingTabLabel, "Your spending tab label", ).not.toBeVisible();
      await expect( this.innBusinessPayTabLabel, "InnBusiness pay tab label", ).not.toBeVisible();
      return;
    }
    await expect( this.innBusinessPayTabLabel, "InnBusiness pay tab label", ).toHaveText(await IbStrings.INN_BUSINESS_PAY_SPENDING.name);
    await expect( this.yourSpendingTabLabel, "Your spending tab label", ).toHaveText(await IbStrings.YOUR_SPENDING.name);
  }
}
