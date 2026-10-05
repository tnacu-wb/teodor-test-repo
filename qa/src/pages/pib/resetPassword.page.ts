import { expect, type Locator } from "@playwright/test";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { BasePibPage } from "./basePib.page";

/**
 * InnBusiness application > Create your InnBusiness account page > Forgotten Password page > Reset Password page
 */
export class ResetPasswordPage extends BasePibPage {
  readonly url = "account/reset?key=";
  // ######## UI elements/properties ########
  readonly headerLabel: Locator = this.page.getByTestId( "ResetPasswordForm-title", );
  readonly subtitleLabel: Locator = this.page.getByTestId( "ResetPasswordForm-description", );
  readonly invalidKeyErrorLabel: Locator = this.page.getByTestId( "ResetPasswordForm-invalidKeyError", );
  readonly passwordInput: Locator = this.page.getByTestId( "ResetPasswordForm-password-Form-Input", );
  readonly passwordErrorLabel: Locator = this.page.getByTestId( "ResetPasswordForm-password-Error-Tooltip", );
  readonly resetPasswordRulesLabel: Locator = this.page.getByTestId( "ResetPasswordForm-rules", );
  readonly confirmPasswordInput: Locator = this.page.getByTestId( "ResetPasswordForm-confirmPassword-Form-Input", );
  readonly confirmPasswordErrorLabel: Locator = this.page.getByTestId( "ResetPasswordForm-confirmPassword-Error-Tooltip", );
  readonly saveNewPasswordButton: Locator = this.page.getByTestId( "ResetPasswordForm-savePassword", );
  // ######## UI actions/navigation ########
  /** Open IB Reset password page. */
  async open(
    keyValue: string,
  ): Promise<void> {
    console.log("Open IB Reset password page");
    await this.openPath(`${this.url}${keyValue}`);
    await this.validatePage();
  }
  /** Click Save new password button. */
  async clickSaveNewPasswordButton(): Promise<void> {
    console.log("Click Save new password button");
    await this.saveNewPasswordButton.click();
  }
  /** Set value to password input. */
  async setPasswordInput({
    value,
    pressTab = true,
  }: {
    value: string;
    pressTab?: boolean;
  }): Promise<void> {
    console.log("Set password");
    await this.passwordInput.fill(value);
    if (pressTab) await this.passwordInput.press("Tab");
  }
  /** Set value to confirm password input. */
  async setConfirmPasswordInput({
    value,
    pressTab = true,
  }: {
    value: string;
    pressTab?: boolean;
  }): Promise<void> {
    console.log("Set confirm password");
    await this.confirmPasswordInput.fill(value);
    if (pressTab) await this.confirmPasswordInput.press("Tab");
  }
  // ######## UI validations ########
  /** Check we reached the current page by checking a specific element from the page. */
  async validatePage(): Promise<void> {
    console.log("Validate Reset password page");
    await this.validatePageMarker(this.headerLabel, "Reset password");
  }
  /** Validate password input placeholder and value. */
  async validatePasswordInputValueAndPlaceholder({
    placeholder,
    value = "",
  }: { placeholder?: string; value?: string } = {}): Promise<void> {
    console.log("Validate password input");
    await expect( this.passwordInput, "Password input placeholder", ).toHaveAttribute( "placeholder", placeholder ?? (await IbStrings.RESET_PASSWORD_PASSWORD_PLACEHOLDER.name), );
    await expect(this.passwordInput, "Password input value").toHaveValue(value);
  }
  /** Validate confirm-password input placeholder and value. */
  async validateConfirmPasswordInputValueAndPlaceholder({
    placeholder,
    value = "",
  }: { placeholder?: string; value?: string } = {}): Promise<void> {
    console.log("Validate confirm password input");
    await expect( this.confirmPasswordInput, "Confirm password input placeholder", ).toHaveAttribute( "placeholder", placeholder ?? (await IbStrings.RESET_PASSWORD_CONFIRM_PASSWORD_PLACEHOLDER.name), );
    await expect( this.confirmPasswordInput, "Confirm password input value", ).toHaveValue(value);
  }
  /** Validate password input error. */
  async validatePasswordInputError({
    isDisplayed = true,
    errorLabel,
  }: { isDisplayed?: boolean; errorLabel?: string } = {}): Promise<void> {
    console.log("Validate password input error");
    if (isDisplayed)
      await expect(this.passwordErrorLabel, "Password input error").toHaveText( errorLabel ?? (await IbStrings.RESET_PASSWORD_REQUIRED.name), );
    else
      await expect( this.passwordErrorLabel, "Password input error", ).not.toBeVisible();
  }
  /** Validate confirm password input error. */
  async validateConfirmPasswordInputError({
    isDisplayed = true,
    errorLabel,
  }: { isDisplayed?: boolean; errorLabel?: string } = {}): Promise<void> {
    console.log("Validate confirm password input error");
    if (isDisplayed)
      await expect( this.confirmPasswordErrorLabel, "Confirm password input error", ).toHaveText( errorLabel ?? (await IbStrings.RESET_PASSWORD_REQUIRED.name), );
    else
      await expect( this.confirmPasswordErrorLabel, "Confirm password input error", ).not.toBeVisible();
  }
  /** Validate Reset password elements. */
  async validateResetPasswordElements({
    isInvalidKey = false,
  }: { isInvalidKey?: boolean } = {}): Promise<void> {
    console.log("Validate Reset password elements");
    await expect(this.headerLabel, "Reset password header").toHaveText( await IbStrings.RESET_PASSWORD.name, );
    await expect(this.subtitleLabel, "Reset password subtitle").toHaveText( await IbStrings.RESET_PASSWORD_SUBTITLE.name, );
    await expect( this.invalidKeyErrorLabel, "Invalid reset key error", ).toBeVisible({ visible: isInvalidKey });
    await this.validatePasswordInputValueAndPlaceholder();
    await this.validateConfirmPasswordInputValueAndPlaceholder();
    await expect( this.saveNewPasswordButton, "Save new password button", ).toHaveText(await IbStrings.RESET_PASSWORD_SAVE_NEW_PASSWORD.name);
  }
}
