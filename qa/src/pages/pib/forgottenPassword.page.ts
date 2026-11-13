import { expect, type Locator } from "@playwright/test";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { BasePibPage } from "./basePib.page";

/**
 * InnBusiness application > Create your InnBusiness account page > Forgotten Password page
 */
export class ForgottenPasswordPage extends BasePibPage {
  readonly url = "account/forgot";
  // ######## UI elements/properties ########
  readonly headerLabel: Locator = this.page.getByTestId("ForgotForm-title");
  readonly subtitleLabel: Locator = this.page.getByTestId( "ForgotForm-description", );
  readonly emailAddressInput: Locator = this.page.getByTestId( "ForgotForm-email-Form-Input", );
  readonly emailAddressErrorLabel: Locator = this.page.getByTestId( "ForgotForm-email-Error-Tooltip", );
  readonly emailPasswordResetButton: Locator = this.page.getByTestId( "ForgotForm-sendEmail", );
  readonly backToLoginButton: Locator = this.page.getByTestId( "ForgotForm-backToLogin", );
  readonly emailSentHeaderLabel: Locator = this.page.getByTestId( "EmailSentConfirmation-title", );
  readonly emailSentNotificationLabel: Locator = this.page.getByTestId( "EmailSentConfirmation-defaultMessage", );
  readonly emailSentFirstSubtitleLabel: Locator = this.page .locator("div.gap-4 p") .nth(0);
  readonly emailSentSecondSubtitleLabel: Locator = this.page .locator("div.gap-4 p") .nth(1);
  readonly emailResentNotificationLabel: Locator = this.page.getByTestId( "EmailSentConfirmation-successMessage", );
  readonly resendEmailButton: Locator = this.page.getByTestId( "EmailSentConfirmation-resendEmail", );
  readonly emailSentBackToLoginButton: Locator = this.page.getByTestId( "EmailSentConfirmation-backToLogin", );
  // ######## UI actions/navigation ########
  /** Open IB Create your InnBusiness account page. */
  async open(): Promise<void> {
    console.log("Open forgotten password page");
    await this.openPath(this.url);
    await this.validatePage();
  }
  /** Click Email password reset button. */
  async clickEmailPasswordResetButton(): Promise<void> {
    console.log("Click Email password reset button");
    await this.emailPasswordResetButton.click();
  }
  /** Click Back to login button. */
  async clickBackToLoginButton(): Promise<void> {
    console.log("Click Back to login button");
    await this.backToLoginButton.click();
  }
  /** Click Resend email button. */
  async clickResendEmailButton(): Promise<void> {
    console.log("Click Resend email button");
    await this.resendEmailButton.click();
  }
  /** Set value to email address input. */
  async setEmailAddressInput({
    value,
    pressTab = true,
  }: {
    value: string;
    pressTab?: boolean;
  }): Promise<void> {
    console.log(`Set value - ${value} to email address`);
    await this.emailAddressInput.fill(value);
    if (pressTab) await this.emailAddressInput.press("Tab");
  }
  // ######## UI validations ########
  /** Check we reached the current page by checking a specific element from the page. */
  async validatePage(): Promise<void> {
    console.log("Validate forgotten password page");
    await this.validatePageMarker(this.headerLabel, "Forgotten password");
  }
  /** Validate the placeholder and the value of the email address input. */
  async validateEmailAddressInputValueAndPlaceholder({
    placeholder,
    value = "",
  }: { placeholder?: string; value?: string } = {}): Promise<void> {
    console.log("Validate forgotten password email input");
    await expect( this.emailAddressInput, "Forgotten password email placeholder", ).toHaveAttribute( "placeholder", placeholder ?? (await IbStrings.FORGOTTEN_PASSWORD_EMAIL.name), );
    await expect( this.emailAddressInput, "Forgotten password email value", ).toHaveValue(value);
  }
  /** Validate email address input error. */
  async validateEmailAddressInputError({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log("Validate email address input error");
    if (isDisplayed)
      await expect( this.emailAddressErrorLabel, "Email address input error", ).toHaveText(await IbStrings.FORGOTTEN_PASSWORD_EMAIL_ERROR.name);
    else
      await expect( this.emailAddressErrorLabel, "Email address input error", ).not.toBeVisible();
  }
  /** Validate Forgotten password elements. */
  async validateForgottenPasswordElements({
    emailAddress = "",
  }: { emailAddress?: string } = {}): Promise<void> {
    console.log("Validate Forgotten password elements");
    await expect(this.headerLabel, "Header label").toHaveText( await IbStrings.FORGOTTEN_PASSWORD.name, );
    await expect(this.subtitleLabel, "Subtitle label").toHaveText( await IbStrings.FORGOTTEN_PASSWORD_SUBTITLE.name, );
    await this.validateEmailAddressInputValueAndPlaceholder({
      value: emailAddress,
    });
    await expect( this.emailPasswordResetButton, "Email password reset button", ).toHaveText(await IbStrings.FORGOTTEN_PASSWORD_EMAIL_PASSWORD_RESET.name);
    await expect(this.backToLoginButton, "Back to login button").toHaveText( await IbStrings.FORGOTTEN_PASSWORD_BACK_TO_LOGIN.name, );
  }
  /** Validate Email sent confirmation elements. */
  async validateEmailSentConfirmationElements({
    emailAddress,
    isResendClicked = false,
  }: {
    emailAddress: string;
    isResendClicked?: boolean;
  }): Promise<void> {
    console.log("Validate Email sent confirmation elements");
    await expect(this.emailSentHeaderLabel, "Email sent header").toHaveText( await IbStrings.FORGOTTEN_PASSWORD.name, );
    await expect( this.emailSentFirstSubtitleLabel, "Email sent first subtitle", ).toHaveText( (await IbStrings.FORGOTTEN_PASSWORD_RESEND_SUBTITLE_1.name).replace( "{email}", emailAddress, ), );
    await expect( this.emailSentSecondSubtitleLabel, "Email sent second subtitle", ).toHaveText(await IbStrings.FORGOTTEN_PASSWORD_RESEND_SUBTITLE_2.name);
    await expect(this.resendEmailButton, "Resend email button").toHaveText( await IbStrings.FORGOTTEN_PASSWORD_RESEND.name, );
    await expect( isResendClicked ? this.emailResentNotificationLabel : this.emailSentNotificationLabel, "Email sent confirmation notification", ).toBeVisible();
  }
}
