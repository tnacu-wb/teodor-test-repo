import { expect, type Locator } from '@playwright/test';
import { Constants } from '../../test-data/constants';
import { Locales } from '../../test-data/locales';
import { EncryptionUtils } from '../../utils/encryptionUtils';
import { FeaturesToggles } from '../../utils/featuresToggles';
import { StorageUtils } from '../../utils/storageUtils';
import { BasePage } from '../shared/base.page';

type UserRoleMap = Record<string, boolean>;

/**
 * Login page from Opera CCUI environment. This class also contains any elements from microsoft page.
 */
export class LoginPage extends BasePage {
  // ######## UI elements/properties ########

  readonly premierInnLogoImage: Locator = this.page.locator('//img[@id="prompt-logo-center"]');
  readonly welcomeTitleLabel: Locator = this.page.locator('//section[contains(@class, "_prompt-box-outer")]//div//div//h1');
  readonly loginDescriptionLabel: Locator = this.page.locator('//section[contains(@class, "_prompt-box-outer")]//div//div//div//p');
  readonly emailAddressInput: Locator = this.page.locator('//input[@id="username"]');
  readonly continueButton: Locator = this.page.locator('//div[not(@aria-hidden="true")]/button[@type="submit"]');
  readonly passwordInput: Locator = this.page.locator('//input[@id="password"]');
  readonly logoutButton: Locator = this.page.locator('//p[@data-testid="navigation-link-logout"]');
  readonly emailLoginsError: Locator = this.page.locator('//*[@id="error-element-username"]');
  readonly emailLoginPlaceholder: Locator = this.page.locator('//*[@id="username-label"]');
  readonly editUsernameButton: Locator = this.page.locator('//a[@data-link-name="edit-username"]');
  readonly wrongEmailOrPasswordError: Locator = this.page.locator('//span[@id="error-element-password"]');

  /**
   * Set username based on user role.
   * @param role User role could be 'standard' or 'manager'.
   * @returns Email username.
   */
  getUsernameBasedOnRole(role = Constants.ROLE_MANAGER): string {
    return role === Constants.ROLE_MANAGER
      ? Constants.AUTOMATION_MANAGER_USERNAME_CCUI
      : Constants.AUTOMATION_AGENT_USERNAME_CCUI;
  }

  /**
   * Set password based on user role.
   * @param role User role could be 'standard' or 'manager'.
   * @returns Encoded user password.
   */
  getUserPasswordBasedOnRole(role = Constants.ROLE_MANAGER): string {
    return role === Constants.ROLE_MANAGER
      ? Constants.AUTOMATION_MANAGER_PASSWORD_CCUI
      : Constants.AUTOMATION_AGENT_PASSWORD_CCUI;
  }

  // ######## UI actions/navigation ########

  /**
   * Fill in username field.
   * @param emailUsername Account username.
   */
  async setEmailUsername(emailUsername: string): Promise<void> {
    console.log(`Fill in email field with ${emailUsername}`);
    await this.emailAddressInput.waitFor({ state: 'visible' });
    await this.emailAddressInput.fill(emailUsername);
  }

  /**
   * Fill in password field.
   * @param userPassword Account password.
   */
  async setUserPassword(userPassword: string): Promise<void> {
    console.log('Fill in password field');
    await this.passwordInput.waitFor({ state: 'visible' });
    await this.passwordInput.fill(userPassword);
  }

  /** Click on edit button from login page. */
  async clickOnEditButton(): Promise<void> {
    console.log('Click on edit button from login page');
    await this.editUsernameButton.click();
  }

  /**
   * Login with Ccui user and open payment page from ccui based on user role and validate it.
   * @param data Login and navigation data.
   */
  async loginAndOpenPaymentPage(data: { role: string; reservationInfo: unknown }): Promise<void> {
    await this.loginCcuiUser(data.role);
    await this.openPaymentPage(data.reservationInfo);
  }

  /**
   * Open payment page from ccui based on user role and validate it.
   * @param reservationInfo API reservation info.
   */
  async openPaymentPage(reservationInfo: unknown): Promise<void> {
    console.log('-> And the CCUI user is navigating on the payment page');
    this.clearBookingFlowId(reservationInfo);
    await global.ccuiPages.paymentCcuiPage.open(reservationInfo);
    await global.ccuiPages.paymentCcuiPage.validatePage();
  }

  /**
   * Login with Ccui user and open ancillaries page from ccui based on user role and validate it.
   * @param data Login and navigation data.
   */
  async loginAndOpenAncillariesPage(data: { role: string; reservationInfo: unknown }): Promise<void> {
    await this.loginCcuiUser(data.role);
    await this.openAncillariesPage(data.reservationInfo);
  }

  /**
   * Open ancillaries page from ccui based on user role and validate it.
   * @param reservationInfo API reservation info.
   */
  async openAncillariesPage(reservationInfo: unknown): Promise<void> {
    this.clearBookingFlowId(reservationInfo);
    await global.ccuiPages.ancillariesCcuiPage.open(reservationInfo);
    await global.ccuiPages.ancillariesCcuiPage.validatePage();
  }

  /**
   * Login ccui user based on user role.
   * @param role User role could be 'standard' or 'manager'.
   * @param forceLogin True if the user should be logged out, false otherwise.
   */
  async loginCcuiUser(role: string, forceLogin = false): Promise<void> {
    const alreadyLoggedIn = await this.logoutButton.isVisible().catch(() => false);
     if (alreadyLoggedIn && !forceLogin) {
       return;
     }
     console.log(`-> Given a CCUI user logged in with ${JSON.stringify(role, undefined, 2)} role`);
     const emailUsername = this.getUsernameBasedOnRole(role);
     const password = EncryptionUtils.decode(this.getUserPasswordBasedOnRole(role));
     await this.loginIntoAccount({ emailUsername, userPassword: password });
  }

  /**
   * Enter an email.
   * @param emailUsername Account username.
   */
  async loginUser(emailUsername: string): Promise<void> {
    if (await this.logoutButton.isVisible().catch(() => false)) {
      await this.logoutFromAccount();
    }
    await this.validatePage();
    await this.setEmailUsername(emailUsername);
    await this.continueButton.click();
  }

  /**
   * Enter a password.
   * @param password Account password.
   */
  async loginUserPassword(password: string): Promise<void> {
    await this.setUserPassword(password);
    await this.continueButton.click();
  }

  /**
   * Login into ccui account without MFA steps.
   * @param data Login account data.
   */
  async loginIntoAccount({ emailUsername, userPassword }: { emailUsername: string; userPassword: string }): Promise<void> {
    console.log(`Logging in with ${emailUsername}`);
    await this.loginUser(emailUsername);
    await this.loginUserPassword(userPassword);
    console.log('Wait for user to be redirected to CCUI homepage');
    await expect(this.page, 'User is redirected to ccui page').toHaveURL(/ccui/, { timeout: 30000 });
    await global.ccuiPages.homePage.validatePage();

    if (global.browser.options.locale === Locales.DE_DE.name) {
      await global.ccuiPages.homePage.headerSection.changeLanguage(Locales.DE_DE.language);
    }
    const localeCookie = await StorageUtils.getCookieValue(this.page, 'CCUI_LOCALE');
    if (global.browser.options.locale === Locales.GB_EN.name && localeCookie === 'de') {
      await StorageUtils.deleteCcuiLocaleCookie(this.page);
      await this.page.reload({ waitUntil: 'domcontentloaded' });
    }
    await global.ccuiPages.homePage.validatePage();
    await FeaturesToggles.applyDefaultFeaturesTogglesOverrides();
    await this.page.reload({ waitUntil: 'domcontentloaded' });
  }

  /** Logout out of the account. */
  async logoutFromAccount(): Promise<void> {
    console.log('Logging out from ccui account');
    await this.logoutButton.click();
    if (!(await this.continueButton.isVisible().catch(() => false))) {
      await this.logoutButton.click();
    }
  }

  // ######## UI validations ########

  /** Check we reached the current page by checking a specific element from the page. */
  async validatePage(): Promise<void> {
    console.log('Validate CCUI Login page was reached');
    await expect(this.premierInnLogoImage, 'Premier Inn logo').toBeVisible();
  }

  /** Validate that the login page is displayed. */
  async validateLogInPageIsDisplayed(): Promise<void> {
    console.log('Validate that the login page is displayed');
    await this.logoutFromAccount();
    await expect(this.premierInnLogoImage, 'Premier Inn logo').toBeVisible();
  }

  /** Validate that the elements on login page are displayed. */
  async validateLogInElementsAreDisplayed(): Promise<void> {
    console.log('Validate that the elements on login page are displayed');
    await expect(this.premierInnLogoImage, 'Premier Inn logo').toBeVisible();
    await expect(this.welcomeTitleLabel, 'Welcome title').toBeVisible();
    await expect(this.loginDescriptionLabel, 'Login description').toBeVisible();
    await expect(this.emailLoginPlaceholder, 'Email placeholder').toBeVisible();
    await expect(this.emailAddressInput, 'Email address').toBeVisible();
    await expect(this.continueButton, 'Continue').toBeVisible();
  }

  /**
   * Validate the email error message is displayed.
   * @param isDisplayed true -> element is displayed / false -> element not displayed.
   */
  async validateEmail(isDisplayed: boolean): Promise<void> {
    console.log('Validate email error display state');
    if (isDisplayed) {
      await expect(this.emailLoginsError, 'Email error').toBeVisible();
    } else {
      await expect(this.emailLoginsError, 'Email error').toBeHidden();
    }
  }

  /** Validate that the email and the password are invalid. */
  async validateUserEmailAndPassword(): Promise<void> {
    console.log('Validate that the email and the password are invalid');
    await expect(this.wrongEmailOrPasswordError, 'Wrong email or password').toBeVisible();
  }

  /** Validate password field is displayed. */
  async validatePasswordFieldIsDisplayed(): Promise<void> {
    console.log('Validate password field is displayed');
    await expect(this.passwordInput, 'Password').toBeVisible();
  }

  /** Validate logout button is displayed. */
  async validateLogoutIsDisplayed(): Promise<void> {
    console.log('Validate logout button is displayed');
    await expect(this.logoutButton, 'Logout button').toBeVisible();
  }

  /** Clear the booking-flow identifier before opening a direct CCUI page. */
  private clearBookingFlowId(reservationInfo: unknown): void {
    if (typeof reservationInfo === 'object' && reservationInfo !== null && 'bookingFlowId' in reservationInfo) {
      (reservationInfo as { bookingFlowId?: string }).bookingFlowId = '';
    }
  }
}