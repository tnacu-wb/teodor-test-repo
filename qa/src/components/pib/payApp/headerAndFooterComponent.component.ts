import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../../test-data/pib/ibStrings";
/** Footer and header items from Pay Application page components */
export class HeaderAndFooterComponentComponent {
  private readonly page: Page = global.page;
  // ######## UI elements/properties ########
  readonly headerBackButton: Locator = this.page.getByTestId("wizard-back-icon");
  readonly footerButton: Locator = this.page.getByTestId("footer-button");
  readonly footerLink: Locator = this.page.getByTestId("footer-link");
  // ######## UI actions/navigation ########
  /** Click pay app back arrow button. */
  async clickOnBackIconButton(): Promise<void> {
    console.log("Click pay app back arrow button");
    await this.headerBackButton.click();
  }
  /** Click pay app footer button. */
  async clickOnFooterContinueButton(): Promise<void> {
    console.log("Click Continue / Finish button");
    await this.footerButton.click();
  }
  /** Click pay app footer link. */
  async clickSaveAndCloseLink(): Promise<void> {
    console.log("Click Save and close / Close application link");
    await this.footerLink.click();
  }
  // ######## UI validations ########
  /** Validate Footer link and button. */
  async validateFooterLinkAndButton({
    expectedLinkLabel,
    expectedButtonLabel,
    isBackButtonDisplayed = true,
  }: {
    expectedLinkLabel: string;
    expectedButtonLabel: string;
    isBackButtonDisplayed?: boolean;
  }): Promise<void> {
    console.log("Validate Footer link and button");
    if (isBackButtonDisplayed)
      await expect(this.headerBackButton, "Header back button").toBeVisible();
    else
      await expect( this.headerBackButton, "Header back button", ).not.toBeVisible();
    await expect(this.footerLink, "Footer link").toHaveText(expectedLinkLabel);
    await expect(this.footerButton, "Footer button").toHaveText( expectedButtonLabel, );
  }
  /** Validate footer Continue button is enabled. */
  async validateContinueButtonIsEnabled(isEnabled: boolean): Promise<void> {
    console.log(`Validate Continue button enabled: ${isEnabled}`);
    if (isEnabled)
      await expect(this.footerButton, "Continue button").toBeEnabled();
    else await expect(this.footerButton, "Continue button").toBeDisabled();
  }
}
