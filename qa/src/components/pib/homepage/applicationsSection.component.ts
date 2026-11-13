import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../../test-data/pib/ibStrings";

/** Applications section on IB. */
export class ApplicationsSectionComponent {
  private readonly page: Page = global.page;
  // ######## properties ########
  // ######## UI elements/properties ########
  readonly applicationsContainer: Locator = this.page.getByTestId( "Applications-Container", );
  readonly applicationItems: Locator = this.page.locator( 'div[data-testid^="Applications-ListItem-"]', );
  readonly applicationResumeItems: Locator = this.page.locator( 'a[data-testid^="Applications-ListItemResume-"]', );
  readonly applyForANewAccountButton: Locator = this.page.locator( 'a[data-testid="Applications-AddApplication"] button', );
  readonly applicationRegisterItems: Locator = this.page.locator( 'a[data-testid^="Applications-ListItemRegister-"]', );
  readonly deleteApplicationButton: Locator = this.page.getByTestId( "DeleteApplicationModal-DeleteButton", );
  readonly deleteApplicationLink: Locator = this.page.locator( 'span[data-testid^="Applications-ListItemDelete"]', );
  /** Get application resume url by index. */
  getApplicationResumeUrlByIndex(index = 0): Locator {
    return this.applicationResumeItems.nth(index);
  }
  /** Get application register url by index. */
  getApplicationRegisterUrlByIndex(index = 0): Locator {
    return this.applicationRegisterItems.nth(index);
  }
  /** Get application delete url by index. */
  getApplicationDeleteUrlByIndex(index = 0): Locator {
    return this.deleteApplicationLink.nth(index);
  }
  /** Get application resume url by application guid. */
  getApplicationResumeUrlByGuid(applicationGuid: string): Locator {
    return this.page.getByTestId(
      `Applications-ListItemResume-${applicationGuid}`,
    );
  }
  /** Get application delete url by application guid. */
  getApplicationDeleteUrlByGuid(applicationGuid: string): Locator {
    return this.page.getByTestId(
      `Applications-ListItemDelete-${applicationGuid}`,
    );
  }
  // ######## UI actions/navigation ########
  /** Return the number of resumable applications. */
  async getNoOfApplications(): Promise<number> {
    console.log("Get number of applications");
    return this.applicationResumeItems.count();
  }
  /** Click Delete application by index. */
  async clickDeleteApplication(index = 0): Promise<void> {
    console.log("Click Delete application button");
    await this.getApplicationDeleteUrlByIndex(index).click();
  }
  /** Click delete application button. */
  async clickDeleteApplicationButton(): Promise<void> {
    console.log("Click delete application confirmation button");
    await this.deleteApplicationButton.click();
  }
  /** Delete all applications currently displayed. */
  async deleteAllApplications(): Promise<void> {
    console.log("Delete all applications");
    while (await this.deleteApplicationLink.count()) {
      await this.clickDeleteApplication();
      await this.clickDeleteApplicationButton();
    }
  }
  /** Click Resume application by index. */
  async clickResumeApplicationUrlByIndex(index = 0): Promise<void> {
    console.log("Click Resume application link");
    await this.getApplicationResumeUrlByIndex(index).click();
  }
  /** Click Register application by index. */
  async clickRegisterApplicationUrlByIndex(index = 0): Promise<void> {
    console.log("Click Register application link");
    await this.getApplicationRegisterUrlByIndex(index).click();
  }
  /** Click Apply for a new account button. */
  async clickApplyForANewAccountButton(): Promise<void> {
    console.log("Click Apply for a new account button");
    await this.applyForANewAccountButton.click();
  }
  /** Click Resume application by GUID. */
  async clickResumeApplicationUrlByGuid(
    applicationGuid: string,
  ): Promise<void> {
    console.log("Click Resume application link by GUID");
    await this.getApplicationResumeUrlByGuid(applicationGuid).click();
  }
  /** Click Delete application by GUID. */
  async clickDeleteApplicationUrlByGuid(
    applicationGuid: string,
  ): Promise<void> {
    console.log("Click Delete application link by GUID");
    await this.getApplicationDeleteUrlByGuid(applicationGuid).click();
  }
  // ######## UI validations ########
  /** Validate the last added application name, status, and action. */
  async validateLastAddedApplicationStatus({
    applicationName,
    applicationStatus,
    applicationAction,
  }: {
    applicationName: string;
    applicationStatus: string;
    applicationAction: string;
  }): Promise<void> {
    console.log("Validate last added application status");
    await expect( this.applicationItems.last(), "Last added application name and statuses", ).toHaveText( `${applicationName}\n${applicationStatus}\n${applicationAction}`, );
  }
  /** Validate Apply for a new account button visibility. */
  async validateApplyForANewAccountButton(isDisplayed = true): Promise<void> {
    console.log("Validate Apply for a new account button");
    if (isDisplayed)
      await expect( this.applyForANewAccountButton, "Apply for a new account button", ).toHaveText(await IbStrings.APPLY_FOR_A_NEW_ACCOUNT.name);
    else
      await expect( this.applyForANewAccountButton, "Apply for a new account button", ).not.toBeVisible();
  }
  /** Validate application names, statuses, and actions. */
  async validateApplications(
    applicationsArray: {
      name: string;
      status: "approved" | "submitted" | "started";
    }[],
  ): Promise<void> {
    console.log("Validate applications");
    await expect( this.applicationsContainer, "Applications container", ).toBeVisible();
    await expect(this.applicationItems, "Application item count").toHaveCount( applicationsArray.length, );
    for (const [index, application] of applicationsArray.entries()) {
      const text =
        application.status === "approved"
          ? [
              application.name,
              await IbStrings.APPLICATION_APPROVED.name,
              await IbStrings.REGISTER_NOW.name,
            ]
          : application.status === "submitted"
            ? [
                application.name,
                await IbStrings.APPLICATION_SUBMITTED.name,
                await IbStrings.IN_REVIEW.name,
              ]
            : [
                application.name,
                await IbStrings.APPLICATION_STARTED.name,
                await IbStrings.RESUME.name,
                await IbStrings.DELETE.name,
              ];
      await expect( this.applicationItems.nth(index), `Application item ${index}`, ).toHaveText(text.join("\n"));
    }
    await expect( this.applyForANewAccountButton, "Apply for a new account button", ).toHaveText(await IbStrings.APPLY_FOR_A_NEW_ACCOUNT.name);
  }
}
