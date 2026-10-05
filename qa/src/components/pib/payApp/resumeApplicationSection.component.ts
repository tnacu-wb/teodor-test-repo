import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../../test-data/pib/ibStrings";
import { ShareWithColleagueSectionComponent } from "./shareWithColleagueSection.component";
/** InnBusiness application > Home > Applications > Resume */
export class ResumeApplicationSectionComponent {
  private readonly page: Page = global.page;
  // ######## properties ########
  // ######## UI elements/properties ########
  readonly resumeApplicationTitleLabel: Locator = this.page.getByTestId( "ResumeApplication-title", );
  readonly resumeApplicationBackButton: Locator = this.page.getByTestId( "ResumeApplication-back-arrow", );
  readonly resumeApplicationCompanyNameLabel: Locator = this.page.getByTestId( "ResumeApplication-application-details-company-name", );
  readonly resumeApplicationReferenceLabel: Locator = this.page.getByTestId( "ResumeApplication-application-details-reference", );
  readonly resumeApplicationStartedByLabel: Locator = this.page.getByTestId( "ResumeApplication-application-details-started-by", );
  readonly resumeApplicationContactTextLabel: Locator = this.page.locator("div.pt-12.pb-2");
  readonly resumeApplicationResumeApplicationButton: Locator = this.page.getByTestId("ResumeApplication-resume-application");
  readonly resumeApplicationSharedEmail: Locator = this.page.locator( '[data-testid^="ResumeApplication-participant-"][data-testid$="-mail"]', );
  readonly shareWithColleagueSection = new ShareWithColleagueSectionComponent();
  // ######## UI actions/navigation ########
  /** Click Resume application. */
  async clickResumeApplicationButton(): Promise<void> {
    console.log("Click Resume application button");
    await this.resumeApplicationResumeApplicationButton.click();
  }
  /** Click Resume application back button. */
  async clickResumeApplicationBackButton(): Promise<void> {
    console.log("Click Resume application back button");
    await this.resumeApplicationBackButton.click();
  }
  // ######## UI validations ########
  /** Validate resume title. */
  async validateResumeTitle(): Promise<void> {
    console.log("Validate resume title");
    await expect( this.resumeApplicationTitleLabel, "Resume application title", ).toHaveText(await IbStrings.RESUME_APPLICATION_FOR_INNBUSINESS_PAY.name);
  }
  /** Validate shared email. */
  async validateSharedEmail(sharedEmail: string): Promise<void> {
    console.log("Validate shared email");
    await expect(this.resumeApplicationSharedEmail, "Shared email").toHaveText( sharedEmail, );
  }
  /** Validate Resume application section. */
  async validateResumeApplicationSection({
    applicationDetails,
    isShared = false,
    isMaxShares = false,
    isManagerUser = true,
  }: {
    applicationDetails: {
      applicationId: string;
      contactDetails: { email: string };
      participants?: { initiator: boolean; email: string }[];
    };
    isShared?: boolean;
    isMaxShares?: boolean;
    isManagerUser?: boolean;
  }): Promise<void> {
    console.log("Validate Resume application section");
    await this.validateResumeTitle();
    await expect( this.resumeApplicationReferenceLabel, "Application reference", ).toHaveText( `${await IbStrings.APPLICATION_REFERENCE.name} ${applicationDetails.applicationId}`, );
    if (isShared)
      await this.validateSharedEmail(
        applicationDetails.participants?.find(
          (participant) => !participant.initiator,
        )?.email ?? "",
      );
    await this.shareWithColleagueSection.validateShareWithColleagueSection({
      isManagerUser,
      isMaxShares,
    });
  }
}
