import { expect, type Locator } from "@playwright/test";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { EncryptionUtils } from "../../utils/encryptionUtils";
import { FeaturesToggles } from "../../utils/featuresToggles";
import { BasePibPage } from "./basePib.page";

/**
 * Login page of the Inn Business web application
 */
export class LoginIbPage extends BasePibPage {
  readonly url = "account/login";

  // ######## UI elements/properties ########
  readonly pageTitleLabel: Locator = this.page.getByTestId("wizard-title");
  readonly pageDescriptionLabel: Locator = this.page.locator( 'h1[data-testid="wizard-title"] + p', );
  readonly emailAddressInput: Locator = this.page.getByTestId("email-Form-Input");
  readonly emailErrorTooltipLabel: Locator = this.page.getByTestId( "email-Error-Tooltip", );
  readonly passwordInput: Locator = this.page.getByTestId( "password-Form-Input", );
  readonly passwordErrorTooltipLabel: Locator = this.page.getByTestId( "password-Error-Tooltip", );
  readonly forgottenPasswordLink: Locator = this.page.getByTestId( "LoginPage-ForgotPassword", );
  readonly loginButton: Locator = this.page.getByTestId("LoginPage-Button");
  readonly loginErrorLabel: Locator = this.page.getByTestId( "LoginPage-error-alert", );
  readonly loginErrorLink: Locator = this.loginErrorLabel.locator("a");
  readonly newToIBLabel: Locator = this.page.locator( 'h1[data-testid="wizard-title"] + p + p', );
  readonly createAnAccountLink: Locator = this.page.getByTestId( "LoginPage-CreateAccount", );

  // ######## UI actions/navigation ########
  /** Open the Login IB page. */
  async open({
    acceptCookies = true,
  }: { acceptCookies?: boolean } = {}): Promise<void> {
    console.log("Open Login IB page");
    const httpAuthUsername = global.browser?.options?.httpAuthUsername;
    if (httpAuthUsername) {
      await this.page.context().setHTTPCredentials({
        username: httpAuthUsername,
        password: global.browser.options.httpAuthPassword ?? "",
      });
    }
    const secureUrl = global.browser?.options?.secureUrl;
    if (secureUrl) await this.page.goto(secureUrl, { waitUntil: "domcontentloaded" });
    await FeaturesToggles.applyDefaultFeaturesTogglesOverrides();
    await this.openPath(this.url, acceptCookies);
    await this.validatePage();
    if (acceptCookies) await this.cookiesSection.clickAcceptAllCookiesButton();
  }
  /** Set the sign-in email address. */
  async setEmailAddress(
    emailAddressValue: string,
    pressTab = false,
  ): Promise<void> {
    console.log(`Sign In email address = ${emailAddressValue}`);
    await this.emailAddressInput.fill(emailAddressValue);
    if (pressTab) await this.emailAddressInput.press("Tab");
  }
  /** Set the sign-in password. */
  async setPassword(passwordValue: string, pressTab = false): Promise<void> {
    console.log("Set Sign In password");
    await this.passwordInput.fill(passwordValue);
    if (pressTab) await this.passwordInput.press("Tab");
  }
  /** Click Forgotten password link. */
  async clickForgottenPasswordLink(): Promise<void> {
    console.log("Click Forgotten password link");
    await this.forgottenPasswordLink.click();
  }
  /** Click Login button. */
  async clickLoginButton(): Promise<void> {
    console.log("Click Login button");
    await this.loginButton.click();
  }
  /** Submit supplied credentials without retrying a failed login. */
  async loginIntoAccount(
    emailAddress: string,
    password: string,
    loginSuccess = true,
  ): Promise<void> {
    console.log(`Sign in with InnBusiness email ${emailAddress}`);
    await this.setEmailAddress(emailAddress);
    await this.setPassword(EncryptionUtils.decode(password));
    await this.clickLoginButton();
    if (!loginSuccess)
      await expect( this.loginButton, "Login button after unsuccessful login", ).toBeVisible();
  }
  /** Open and submit supplied account credentials once. */
  async performLogin(
    emailAddress: string,
    password: string,
    loginSuccess = true,
    retries = 5,
  ): Promise<void> {
    console.log(`Perform InnBusiness login for ${emailAddress}`);
    await this.open();
    await this.loginIntoAccount(emailAddress, password, loginSuccess);
    if (!loginSuccess) return;

    if (this.page.url().includes('homepage')) {
      console.log('Already on homepage, login successful');
      await this.cookiesSection.clickAcceptAllCookiesButton();
      await global.pibPages.homePage.validatePage();
      return;
    }

    let loginSucceeded = true;
    try {
      await this.loginButton.waitFor({ state: 'hidden', timeout: 80000 });
    } catch {
      loginSucceeded = false;
    }

    if (!loginSucceeded && retries > 0) {
      console.log(`Login failed, retrying... Remaining attempts: ${retries - 1}`);
      await this.performLogin(emailAddress, password, loginSuccess, retries - 1);
      return;
    }

    await expect(this.page, 'Successful login should redirect to homepage').toHaveURL(/homepage/, { timeout: 120000 });
    await this.cookiesSection.clickAcceptAllCookiesButton();
    await global.pibPages.homePage.validatePage();
  }

  // ######## UI validations ########
  /** Check we reached the current page by checking a specific element from the page. */
  async validatePage(): Promise<void> {
    console.log("Validate Login IB page");
    await this.validatePageMarker(this.emailAddressInput, "Login IB");
  }
  /** Validate page elements. */
  async validatePageElements(): Promise<void> {
    console.log("Validate Login IB page elements");
    await expect(this.pageTitleLabel, "Page title").toHaveText( await IbStrings.SIGN_IN_PAGE_TITLE.name, );
    await expect(this.pageDescriptionLabel, "Page description").toHaveText( await IbStrings.SIGN_IN_PAGE_SUBTITLE.name, );
    await expect(this.emailAddressInput, "Email placeholder").toHaveAttribute( "placeholder", await IbStrings.SIGN_IN_EMAIL_PLACEHOLDER.name, );
    await expect(this.passwordInput, "Password placeholder").toHaveAttribute( "placeholder", await IbStrings.SIGN_IN_PASSWORD_PLACEHOLDER.name, );
    await expect( this.forgottenPasswordLink, "Forgotten password link", ).toHaveText(await IbStrings.SIGN_IN_FORGOTTEN_PASSWORD_LINK.name);
    await expect( this.forgottenPasswordLink, "Forgotten password href", ).toHaveAttribute("href", expect.stringContaining("account/forgot"));
    await expect(this.loginButton, "Login button").toHaveText( await IbStrings.SIGN_IN_BUTTON.name, );
    await expect(this.newToIBLabel, "New to InnBusiness prompt").toContainText( await IbStrings.SIGN_IN_CREATE_LABEL.name, );
    await expect( this.createAnAccountLink, "Create account href", ).toHaveAttribute("href", expect.stringContaining("account/register"));
  }
  /** Validate invalid email error is displayed. */
  async validateInvalidEmailError(): Promise<void> {
    console.log("Validate invalid email error");
    await expect(this.emailErrorTooltipLabel, "Invalid email error").toHaveText( await IbStrings.SIGN_IN_EMAIL_ERROR.name, );
  }
  /** Validate invalid password error is displayed. */
  async validateInvalidPasswordError(): Promise<void> {
    console.log("Validate invalid password error");
    await expect( this.passwordErrorTooltipLabel, "Invalid password error", ).toHaveText(await IbStrings.SIGN_IN_PASSWORD_ERROR.name);
  }
  /** Validate incorrect email/password error is displayed. */
  async validateIncorrectCredentialsError(): Promise<void> {
    console.log("Validate incorrect credentials error");
    await expect( this.loginErrorLabel, "Incorrect credentials error", ).toContainText(await IbStrings.SIGN_IN_SUBMIT_ERROR_LABEL.name);
    await expect( this.loginErrorLink, "Incorrect credentials forgotten-password href", ).toHaveAttribute("href", expect.stringContaining("account/forgot"));
  }
}
