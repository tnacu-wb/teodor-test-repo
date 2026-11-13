import { expect, type Locator, type Page } from "@playwright/test";

/**
 * Existing account modal
 */
export class ExistingAccountSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########
  readonly existingAccountModal: Locator = this.page.getByTestId( "ExistingAccount-Dialog-Content", );
  readonly existingAccountTitleLabel: Locator = this.existingAccountModal.locator("h2");
  readonly existingAccountDescriptionLabel: Locator = this.page.getByTestId( "ExistingAccount-Box", );
  readonly linkExistingAccountButton: Locator = this.page.getByTestId( "ExistingAccount-Link-Button", );
  readonly startNewApplicationButton: Locator = this.page.getByTestId( "ExistingAccount-Submit-Button", );

  // ######## UI actions/navigation ########
  /** Click on Link an existing account. */
  async clickOnLinkExistingAccount(): Promise<void> {
    console.log("Click on link and existing account");
    await this.linkExistingAccountButton.click();
  }

  /** Click on start a new application button. */
  async clickStartANewApplicationButton(): Promise<void> {
    console.log("Click on start a new application button");
    await this.startNewApplicationButton.scrollIntoViewIfNeeded();
    await this.startNewApplicationButton.click();
  }

  // ######## UI validations ########
  /** Validate existing account modal is displayed for un-tethered users. */
  async validateExistingAccountModalIsDisplayed(
    isDisplayed = true,
  ): Promise<void> {
    console.log("Validate existing account modal");
    if (!isDisplayed) {
      await expect( this.existingAccountModal, "Existing account modal", ).not.toBeVisible();
      return;
    }
    await expect( this.existingAccountModal, "Existing account modal", ).toBeVisible();
    await expect( this.existingAccountTitleLabel, "EAD modal title", ).toBeVisible();
    await expect( this.existingAccountDescriptionLabel, "EAD modal description", ).toBeVisible();
    await expect( this.linkExistingAccountButton, "Link existing account button", ).toBeVisible();
    await expect( this.startNewApplicationButton, "Start a new application button", ).toBeVisible();
  }
}
