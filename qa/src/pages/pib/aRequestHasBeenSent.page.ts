import { expect, type Locator } from "@playwright/test";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { BasePibPage } from "./basePib.page";

/**
 * InnBusiness application > Create your InnBusiness account page > A Request Has Been Sent page
 */
export class ARequestHasBeenSentPage extends BasePibPage {
  // ######## properties ########
  // ######## UI elements/properties ########
  readonly headerLabel: Locator = this.page.getByTestId("wizard-title");
  readonly subtitleLabel: Locator = this.page.getByTestId( "AccountExistsNoManager-description", );
  readonly backToHomeButton: Locator = this.page.getByTestId( "AccountExistsNoManager-Button", );

  // UI components

  // ######## UI actions/navigation ########
  /**
   * Click on Back to login button
   */
  async clickBackToLoginButton(): Promise<void> {
    console.log("Click Back to Home button");
    await this.backToHomeButton.scrollIntoViewIfNeeded();
    await this.backToHomeButton.click();
  }

  // ######## UI validations ########
  /**
   * Check we reached the current page by checking a specific element from the page
   */
  async validatePage(): Promise<void> {
    console.log("Validate A Request Has Been Sent page");
    await this.validatePageMarker(
      this.backToHomeButton,
      "A Request Has Been Sent",
    );
  }

  /**
   * Validate A Request Has Been Sent  elements
   */
  async validateARequestHasBeenSentElements(): Promise<void> {
    console.log("Validate A Request Has Been Sent elements");
    await expect(this.headerLabel, "Header label").toHaveText( await IbStrings.A_REQUEST_HAS_BEEN_SENT.name, );
    await expect(this.subtitleLabel, "Subtitle label").toHaveText( await IbStrings.A_REQUEST_HAS_BEEN_SENT_DESCRIPTION.name, );
    await expect(this.backToHomeButton, "Back to Home button").toHaveText( await IbStrings.A_REQUEST_HAS_BEEN_SENT_BACK_TO_HOME.name, );
  }
}
