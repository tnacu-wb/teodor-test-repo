import { expect, type Locator } from "@playwright/test";
import { Locales } from "../../test-data/locales";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { BasePibPage } from "./basePib.page";

/**
 * InnBusiness application > Create your InnBusiness account page > This Email Already Has An Account Page
 */
export class ThisEmailAlreadyHasAnAccountPage extends BasePibPage {
  // ######## properties ########
  // ######## UI elements/properties ########
  readonly headerLabel: Locator = this.page.getByTestId("wizard-title");
  readonly backImg: Locator = this.page.getByTestId("wizard-back-icon");
  readonly subtitleLabel: Locator = this.page.getByTestId( "AccountExistsManager-description", );
  readonly loginButton: Locator = this.page.getByTestId( "AccountExistsManager-Button", );
  readonly forgotPasswordButton: Locator = this.page.getByTestId( "AccountExistsManager-forgot-password", );

  // UI components

  // ######## UI actions/navigation ########
  /**
   * Click Back img
   */
  async clickBackImg(): Promise<void> {
    console.log("Click Back img");
    await this.backImg.click();
    await expect(this.backImg, "Back image after click").not.toBeVisible();
  }

  /**
   * Click Login button
   */
  async clickLoginButton(): Promise<void> {
    console.log("Click Login button");
    await this.loginButton.click();
    await expect( this.loginButton, "Login button after click", ).not.toBeVisible();
  }

  /**
   * Click Forgot password button
   */
  async clickForgotPasswordButton(): Promise<void> {
    console.log("Click Forgot password button");
    await this.forgotPasswordButton.click();
    await expect( this.forgotPasswordButton, "Forgot password button after click", ).not.toBeVisible();
  }

  // ######## UI validations ########
  /**
   * Check we reached the current page by checking a specific element from the page
   */
  async validatePage(): Promise<void> {
    console.log("Validate This Email Already Has An Account page");
    await this.validatePageMarker(
      this.forgotPasswordButton,
      "This Email Already Has An Account",
    );
  }

  /**
   * Validate This Email Already Has An Account elements
   */
  async validateThisEmailAlreadyHasAnAccountElements(): Promise<void> {
    console.log("Validate This Email Already Has An Account elements");
    await expect(this.headerLabel, "Header label").toHaveText( await IbStrings.THIS_EMAIL_ALREADY.name, );
    await expect(this.backImg, "Back img").toBeVisible();
    await expect(this.subtitleLabel, "Subtitle label").toHaveText( await IbStrings.THIS_EMAIL_ALREADY_DESCRIPTION.name, );
    await expect(this.loginButton, "Login button").toHaveText( await IbStrings.THIS_EMAIL_ALREADY_LOG_IN.name, );
    await expect( this.forgotPasswordButton, "Forgot password button", ).toHaveText(await IbStrings.THIS_EMAIL_ALREADY_FORGOT_PASSWORD.name);
    await expect(this.forgotPasswordButton, "Forgot password").toHaveAttribute( "href", `/${Locales.getIbUrlName()}/account/forgot`, );
  }
}
