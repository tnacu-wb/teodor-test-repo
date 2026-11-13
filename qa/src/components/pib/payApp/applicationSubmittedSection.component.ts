import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../../test-data/pib/ibStrings";
/** InnBusiness application > Pay App > Application submitted */
export class ApplicationSubmittedSectionComponent {
  private readonly page: Page = global.page;
  // ######## UI elements/properties ########
  readonly applicationSubmittedTitleLabel: Locator = this.page.getByTestId( "AppSuccessfullySubmitted-title", );
  readonly applicationSubmittedDescriptionLabel: Locator = this.applicationSubmittedTitleLabel.locator("~ p");
  readonly applicationNameLabel: Locator = this.page.getByTestId( "AppSuccessfullySubmitted-application-details-company-name", );
  readonly applicationReferenceLabel: Locator = this.page.getByTestId( "AppSuccessfullySubmitted-application-details-reference", );
  readonly applicationStartedByLabel: Locator = this.page.getByTestId( "AppSuccessfullySubmitted-application-details-started-by", );
  readonly backToHomeButton: Locator = this.page.getByTestId( "AppSuccessfullySubmitted-back-to-home-button", );
  readonly emailLink: Locator = this.page .locator('div[data-testid="AppSuccessfullySubmitted-container"] p a') .first();
  // ######## UI actions/navigation ########
  /** Click back to home button. */
  async clickBackToHomeButton(): Promise<void> {
    console.log("Click Back to home button");
    await this.backToHomeButton.click();
  }
  // ######## UI validations ########
  /** Validate email link. */
  async validateEmailLink(): Promise<void> {
    console.log("Validate email link");
    await expect(this.emailLink, "Contact email").toHaveText( await IbStrings.CONTACT_EMAIL.name, );
    await expect(this.emailLink, "Contact email href").toHaveAttribute( "href", `mailto:${await IbStrings.CONTACT_EMAIL.name}`, );
  }
  /** Validate page section. */
  async validateApplicationSubmittedSection({
    applicationDetails,
  }: {
    applicationDetails: {
      accountName: string;
      applicationId: string;
      contactDetails: { email: string };
    };
  }): Promise<void> {
    console.log("Validate application submitted section");
    await expect( this.applicationSubmittedTitleLabel, "Application submitted title", ).toHaveText(await IbStrings.YOUR_APPLICATION_SUBMITTED.name);
    await expect(this.applicationNameLabel, "Application name").toHaveText( applicationDetails.accountName, );
    await expect(this.backToHomeButton, "Back to home button").toHaveText( await IbStrings.BACK_TO_HOME.name, );
  }
}
