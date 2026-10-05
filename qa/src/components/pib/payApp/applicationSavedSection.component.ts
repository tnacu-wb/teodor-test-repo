import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../../test-data/pib/ibStrings";
import { ShareWithColleagueSectionComponent } from "./shareWithColleagueSection.component";
/** InnBusiness application > Home > Apply now > Start application > Save application section */
export class ApplicationSavedSectionComponent {
  private readonly page: Page = global.page;
  // ######## UI elements/properties ########
  readonly applicationSavedTitleLabel: Locator = this.page.locator( 'div[data-testid="SavedApplication-title"] span', );
  readonly applicationBackToHomeButton: Locator = this.page.getByTestId( "SavedApplication-back-to-home", );
  readonly applicationCompanyNameLabel: Locator = this.page.getByTestId( "SavedApplication-application-details-company-name", );
  readonly applicationReferenceLabel: Locator = this.page.getByTestId( "SavedApplication-application-details-reference", );
  readonly applicationStartedByLabel: Locator = this.page.getByTestId( "SavedApplication-application-details-started-by", );
  readonly applicationContactTextLabel: Locator = this.page.locator("div.pt-12.pb-2");
  readonly shareWithColleagueSection = new ShareWithColleagueSectionComponent();
  /** Employee email address from dropdown suggestions. */
  getDynamicEmployeeLabelByEmail(employeeEmail: string): Locator {
    return this.page.locator(
      `div[data-testid="PeoplePicker-IB-Form-People-Picker-Dropdown"] span`,
      { hasText: employeeEmail },
    );
  }
  // ######## UI actions/navigation ########
  /** Click Back to home button from application saved section. */
  async clickBackToHomeButton(): Promise<void> {
    console.log("Click Back button from application saved section");
    await this.applicationBackToHomeButton.click();
  }
  // ######## UI validations ########
  /** Validate page title. */
  async validateApplicationSavedTitle(): Promise<void> {
    console.log("Validate application saved title");
    await expect( this.applicationSavedTitleLabel, "Application saved title", ).toHaveText(await IbStrings.APPLICATION_SAVED.name);
  }
  /** Validate Application saved section. */
  async validateApplicationSavedSection({
    applicationDetails,
    isMaxShares = false,
    isManagerUser = true,
  }: {
    applicationDetails: {
      applicationId: string;
      contactDetails: { email: string };
    };
    isMaxShares?: boolean;
    isManagerUser?: boolean;
  }): Promise<void> {
    console.log("Validate Application saved section");
    await this.validateApplicationSavedTitle();
    await expect( this.applicationReferenceLabel, "Application saved reference", ).toHaveText( `${await IbStrings.APPLICATION_REFERENCE.name} ${applicationDetails.applicationId}`, );
    await this.shareWithColleagueSection.validateShareWithColleagueSection({
      isManagerUser,
      isMaxShares,
    });
  }
}
