import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';

/**
 * The Sign In section (Guest Details) containing the UI elements, custom actions and validations.
 * Mirrors qa/reference `components/opera/guestDetails/signInSection.js`.
 */
export class SignInSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly signInContainer: Locator = this.page.locator('div[data-testid="GuestDetails-OptionalAuth-Container"]');
  readonly signInTitleLabel: Locator = this.page.locator('p[data-testid="GuestDetails-OptionalAuth-Header"]');
  readonly signInDescriptionLabel: Locator = this.page.locator('p[data-testid="GuestDetails-OptionalAuth-Description"]');
  readonly signInButton: Locator = this.page.locator('button[data-testid="GuestDetails-OptionalAuth-SignIn-Option"]');
  readonly signInButtonLabel: Locator = this.signInButton.locator('p');
  readonly logInContainer: Locator = this.page.locator('div[data-testid="GuestDetails-OptionalAuth-Login-Container"]');
  readonly logInButton: Locator = this.page.locator('button[data-testid="GuestDetails-OptionalAuth-ButtonLogin"]');
  readonly forgottenPasswordLink: Locator = this.page.locator('form a[data-testid="GuestDetails-OptionalAuth-ResetPassLink"]');
  readonly registerButton: Locator = this.page.locator('button[data-testid="GuestDetails-OptionalAuth-Register-Option"]');
  readonly registerButtonLabel: Locator = this.registerButton.locator('p');
  readonly incorrectCredentialsAlert: Locator = this.page.locator('div[data-testid="Alert"]');
  readonly incorrectCredentialsLabel: Locator = this.page.locator('div[data-testid="AlertTitle"]');
  readonly incorrectCredentialsLink: Locator = this.incorrectCredentialsAlert.locator('a[data-testid="GuestDetails-OptionalAuth-ResetPassLink"]');

  // UI components

  readonly emailAddressInput: Locator = this.page.locator('div[data-testid="GuestDetails-OptionalAuth-Email"] input[data-testid="input-email"]');
  readonly passwordInput: Locator = this.page.locator('input[data-testid="input-password"]');

  // ######## UI actions/navigation ########

  /** Toggle (open/close) the Sign In section. */
  async toggleSignInSection(): Promise<void> {
    await this.signInButton.scrollIntoViewIfNeeded();
    await this.signInButton.click();
  }

  /** Toggle (open/close) the Register section. */
  async toggleRegisterSection(): Promise<void> {
    await this.registerButton.scrollIntoViewIfNeeded();
    await this.registerButton.click();
  }

  /** Set the email address input in the Sign In section. */
  async setEmailAddress(emailAddressValue: string): Promise<void> {
    console.log(`Sign In email address = ${emailAddressValue}`);
    if (emailAddressValue) {
      await this.emailAddressInput.fill(emailAddressValue);
    }
  }

  /** Set the password input in the Sign In section. */
  async setPassword(passwordValue: string): Promise<void> {
    console.log('Sign In password');
    if (passwordValue) {
      await this.passwordInput.fill(passwordValue);
    }
  }

  /** Click the Login button and wait for the sign-in container to hide (or stay if login fails). */
  async clickLoginButton({ successfulLogin = true }: { successfulLogin?: boolean } = {}): Promise<void> {
    console.log('Click Login button');
    await this.logInButton.scrollIntoViewIfNeeded();
    await this.logInButton.click();
    if (successfulLogin) {
      await this.signInContainer.waitFor({ state: 'hidden', timeout: 10000 }).catch(() => {});
    }
  }

  /** Fill the email/password and click Login. */
  async loginIntoAccount({
    emailAddress,
    password,
    successfulLogin = true,
  }: { emailAddress: string; password: string; successfulLogin?: boolean }): Promise<void> {
    await this.setEmailAddress(emailAddress);
    await this.setPassword(password);
    await this.clickLoginButton({ successfulLogin });
  }

  /** Click the Forgotten Password link. */
  async clickForgottenPassword(): Promise<void> {
    console.log('Click Forgotten password button');
    await this.forgottenPasswordLink.click();
  }

  // ######## UI validations ########

  /** Validate the Sign In section title. */
  async validateSignInSectionTitle(): Promise<void> {
    console.log('Validate the title of the Sign In Section');
    await expect(this.signInTitleLabel, 'Sign In title label').toContainText(await Strings.SIGN_IN.name);
  }

  /** Validate the Sign In section description. */
  async validateSignInSectionDescription(): Promise<void> {
    console.log('Validate the description of the Sign In Section');
    await expect(this.signInDescriptionLabel, `${await Strings.SAVE_TIME_AND_MAKE_BOOKING_EVEN_EASIER.name} description`).toHaveText(
      await Strings.SAVE_TIME_AND_MAKE_BOOKING_EVEN_EASIER.name
    );
  }

  /** Validate the Sign In button label. */
  async validateSignInButton(): Promise<void> {
    console.log('Validate the Sign In button');
    await expect(this.signInButtonLabel, `${await Strings.SIGN_IN.name} button`).toHaveText(await Strings.SIGN_IN.name);
  }

  /** Validate the Register button label. */
  async validateRegisterButton(): Promise<void> {
    console.log('Validate the Register button');
    await expect(this.registerButtonLabel, `${await Strings.REGISTER.name} button`).toHaveText(await Strings.REGISTER.name);
  }
}
