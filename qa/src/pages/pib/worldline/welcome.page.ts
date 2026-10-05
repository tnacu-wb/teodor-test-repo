import { type Locator } from "@playwright/test";
import { Constants } from "../../../test-data/constants";
import { WorldlinePage } from "./worldline.page";

/** Welcome page for Worldline. */
export class WelcomePage extends WorldlinePage {
  readonly url = Constants.WORLDLINE_PAGE_LINK_CODE;

  // ######## UI elements/properties ########
  readonly registerButton: Locator = this.page.locator( "#ctl00_ContentPlaceHolder1_btnRegister", );
  readonly emailAddressInput: Locator = this.page.locator( "#ctl00_ContentPlaceHolder1_txtAuthorisation", );
  readonly passwordInput: Locator = this.page.locator( "#ctl00_ContentPlaceHolder1_txtPwd", );
  readonly loginButton: Locator = this.page.locator( "#ctl00_ContentPlaceHolder1_btnProceed1", );
  readonly oldPasswordInput: Locator = this.page.locator( "#ctl00_ContentPlaceHolder1_ucPwd1_txtOldPassword", );
  readonly newPasswordInput: Locator = this.page.locator( "#ctl00_ContentPlaceHolder1_ucPwd1_txtNewPassword1", );
  readonly confirmPasswordInput: Locator = this.page.locator( "#ctl00_ContentPlaceHolder1_ucPwd1_txtNewPassword2", );
  readonly updatePasswordButton: Locator = this.page.locator( "#ctl00_ContentPlaceHolder1_btnCP_Update1", );
  readonly recaptchaFrame: Locator = this.page.locator( 'iframe[title="reCAPTCHA"]', );

  // ######## UI actions/navigation ########
  /** Open the Worldline welcome page. */
  async open(): Promise<void> {
    console.log("Open Worldline welcome page");
    await this.openExternal(this.url);
  }
  /** Click the Register button. */
  async clickRegisterButton(): Promise<void> {
    console.log("Click Register button");
    await this.registerButton.scrollIntoViewIfNeeded();
    await this.registerButton.click();
  }
  /** Set the email address input. */
  async setEmailAddressInput(emailAddress: string): Promise<void> {
    console.log(`Set email address input: ${emailAddress}`);
    await this.emailAddressInput.fill(emailAddress);
  }
  /** Set the password input. */
  async setPasswordInput(password: string): Promise<void> {
    console.log("Set password input");
    await this.passwordInput.fill(password);
  }
  /** Click the Login button. */
  async clickLoginButton(): Promise<void> {
    console.log("Click Login button");
    await this.loginButton.scrollIntoViewIfNeeded();
    await this.loginButton.click();
  }
  /** Log in to Worldline, including the source reCAPTCHA interaction. */
  async loginWL(emailAddress: string, password: string): Promise<void> {
    console.log(`Login to WL with email: ${emailAddress}`);
    await this.setEmailAddressInput(emailAddress);
    await this.recaptchaFrame.click({ position: { x: 30, y: 30 } });
    await this.setPasswordInput(password);
    await this.clickLoginButton();
  }
  /** Set the old password input. */
  async setOldPasswordInput(password: string): Promise<void> {
    console.log("Set old password input");
    await this.oldPasswordInput.fill(password);
  }
  /** Set the new password input. */
  async setNewPasswordInput(password: string): Promise<void> {
    console.log("Set new password input");
    await this.newPasswordInput.fill(password);
  }
  /** Set the confirm password input. */
  async setConfirmPasswordInput(password: string): Promise<void> {
    console.log("Set confirm password input");
    await this.confirmPasswordInput.fill(password);
  }
  /** Click the Update password button. */
  async clickUpdatePasswordButton(): Promise<void> {
    console.log("Click Update password button");
    await this.updatePasswordButton.scrollIntoViewIfNeeded();
    await this.updatePasswordButton.click();
  }
  /** Update the Worldline password. */
  async updatePassword(data: {
    oldPassword: string;
    newPassword: string;
  }): Promise<void> {
    console.log("Update password");
    await this.oldPasswordInput.waitFor({ state: "visible", timeout: 20000 });
    await this.setOldPasswordInput(data.oldPassword);
    await this.setNewPasswordInput(data.newPassword);
    await this.setConfirmPasswordInput(data.newPassword);
    await this.clickUpdatePasswordButton();
  }
}
