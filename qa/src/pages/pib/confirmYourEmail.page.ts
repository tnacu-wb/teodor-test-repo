import { expect, type Locator } from "@playwright/test";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { BasePibPage } from "./basePib.page";

/**
 * InnBusiness application > Create your InnBusiness account page > Confirm your email page
 */
export class ConfirmYourEmailPage extends BasePibPage {
  // ######## UI elements/properties ########
  readonly headerLabel: Locator = this.page.getByTestId("wizard-title");
  readonly subtitleLabel: Locator = this.page.getByTestId( "ConfirmationEmail-description", );
  readonly resentEmailConfirmationLabel: Locator = this.page.getByTestId( "ConfirmationEmail-success-alert", );
  readonly resendNowButton: Locator = this.page.getByTestId( "ConfirmationEmail-resend", );
  readonly backToHomeButton: Locator = this.page.getByTestId( "ConfirmationEmail-back-to-home", );
  // ######## UI actions/navigation ########
  /** Click on Resend now button. */
  async clickResendNowButton(): Promise<void> {
    console.log("Click Resend now button");
    await this.resendNowButton.click();
  }
  /** Click on Back to home button. */
  async clickBackToHomeButton(): Promise<void> {
    console.log("Click on Back to home button");
    await this.backToHomeButton.click();
    await expect( this.backToHomeButton, "Back to home button after navigation", ).not.toBeVisible();
  }
  // ######## UI validations ########
  /** Check we reached the current page by checking a specific element from the page. */
  async validatePage(): Promise<void> {
    console.log("Validate confirm your email page");
    await this.validatePageMarker(this.backToHomeButton, "Confirm your email");
  }
  /** Validate Confirm your email elements. */
  async validateConfirmYourEmailElements({
    emailAddress,
  }: {
    emailAddress: string;
  }): Promise<void> {
    console.log("Validate Confirm your email elements");
    await expect(this.headerLabel, "Confirm email header").toHaveText( await IbStrings.CONFIRM_YOUR_EMAIL.name, );
    await expect(this.subtitleLabel, "Confirm email subtitle").toHaveText( (await IbStrings.CONFIRM_YOUR_EMAIL_DESCRIPTION.name).replace( "[address]", emailAddress, ), );
    await expect(this.resendNowButton, "Resend now button").toHaveText( await IbStrings.CONFIRM_YOUR_EMAIL_DIDNT_GET_AN_EMAIL.name, );
    await expect(this.backToHomeButton, "Back to home button").toHaveText( await IbStrings.CONFIRM_YOUR_EMAIL_BACK_TO_HOME.name, );
  }
  /** Validate resent email confirmation label is displayed. */
  async validateResentEmailConfirmationLabel(): Promise<void> {
    console.log("Validate resent email confirmation label");
    await expect( this.resentEmailConfirmationLabel, "Resent email confirmation", ).toHaveText(await IbStrings.AUTH_RESEND_EMAIL.name);
    await expect( this.resentEmailConfirmationLabel, "Resent email confirmation background", ).toHaveCSS("background-color", "rgb(232, 243, 237)");
  }
}
