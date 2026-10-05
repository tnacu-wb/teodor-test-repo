import { type Page, type Locator } from '@playwright/test';
import { Constants } from '../../../test-data/constants';
import { Locales, getCurrentLocale } from '../../../test-data/locales';
import { EncryptionUtils } from '../../../utils/encryptionUtils';

/**
 * PI Header Component - the global site header for premierinn.com.
 * Mirrors the PI-relevant subset of qa/reference `header.page.js` +
 * `components/opera/header/{loginSection,userDropdownMenu}.js`
 * (business-booker/reporting/IB multi-window flows are BB/IB-only and out of scope for PI).
 */
export class HeaderComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly loginLink: Locator = this.page.locator(
    '[data-testid="Global-SignUp-Desktop"] ~ *, div[data-testid="listId"] button:first-child'
  ).or(this.page.getByRole('button', { name: /^(Log in|Anmelden)$/i }));
  readonly usernameLink: Locator = this.page.locator(
    '[data-testid="Global-UserName-Desktop"], [date-testid="Global-UserName-Desktop"], [data-testid="Global-SideNav-UserName-NavItem-Mobile"], div.btn-user-details'
  );
  readonly mobileBurgerMenuButton: Locator = this.page.locator('[data-testid="mobile-navigation-menu"]');
  readonly sidebarContainer: Locator = this.page.locator('section[data-testid="modalSideBar"]');
  readonly mobileCloseButton: Locator = this.page.locator('[data-testid="closeModal"]');
  readonly manageBookingsLabel: Locator = this.page.locator(
    '[data-testid="navigation-link-findBooking"], [id="find-a-booking"], [data-testid="manageBookingPage"], [data-testid="ManageBookingButton"]'
  );
  readonly languageSelectorButton: Locator = this.page.locator('[data-testid="pi-languageSelectorContainer"] div');
  readonly mobileLanguageSelectorButton: Locator = this.page.locator(
    '[data-testid="defaultSideNav"] [data-testid="navigationItem"] p'
  );
  readonly englishLanguageSelectorFlagIcon: Locator = this.page.locator(
    '[id*="popover-body"] div div:nth-child(1) div img'
  );
  readonly germanLanguageSelectorFlagIcon: Locator = this.page.locator(
    '[id*="popover-body"] div div:nth-child(2) div img'
  );

  // Login modal (Header-Auth)
  readonly loginModal: Locator = this.page.locator('div[data-testid="Header-Auth-ModalBody"]');
  readonly loginModalEmailInput: Locator = this.loginModal.locator(
    'label[data-testid="input-email-label"] + div input'
  );
  readonly loginModalPasswordInput: Locator = this.loginModal.locator(
    'div[data-testid="Login-Password"] input'
  );
  readonly loginModalLoginButton: Locator = this.page.locator('button[data-testid="Login-ButtonLogin"]');
  readonly loginModalCloseButton: Locator = this.page.locator('button[data-testid="Header-Auth-ModalCloseButton"]');
  readonly incorrectCredentialsAlert: Locator = this.page.locator('div[data-testid="Alert"]');

  // User dropdown menu (shown after clicking usernameLink)
  readonly logOutButton: Locator = this.page.locator(
    '[data-testid="Global-Logout-Desktop"], [data-testid="Global-Logout-Mobile"]'
  );

  // ######## UI actions/navigation ########

  /** Check if user is currently logged in (login link still visible). */
  async checkIfUserIsLoggedIn(): Promise<boolean> {
    return this.loginLink.isVisible();
  }

  /** Click the mobile burger menu to open the sidebar, if not already open. */
  async clickOnMobileBurger(): Promise<void> {
    console.log('Click on mobile burger');
    await this.mobileBurgerMenuButton.click();
  }

  /** Open the sidebar on mobile if it is not already displayed. */
  private async ensureMobileSidebarOpen(): Promise<void> {
    if (!Constants.BROWSER_RESOLUTIONS.isDesktop() && !(await this.sidebarContainer.isVisible())) {
      await this.clickOnMobileBurger();
    }
  }

  /** Open the login modal. */
  async openLoginModal(): Promise<void> {
    console.log('Open Login modal');
    await this.ensureMobileSidebarOpen();
    await this.loginLink.click();
    await this.loginModal.waitFor({ state: 'visible' });
  }

  /**
   * Log in to the account using the credentials provided in the login modal.
   * @param emailAddress the email address
   * @param password decoded password
   */
  async loginIntoAccount({ emailAddress, password }: { emailAddress: string; password: string }): Promise<void> {
    await this.loginModalEmailInput.fill(emailAddress);
    await this.loginModalPasswordInput.fill(password);
    await this.loginModalLoginButton.click();
  }

  /**
    * Perform login with the leisure customer credentials, retrying until the authenticated header changes.
   * Mirrors reference `header.page.js` `loginForLeisureCustomer`.
   */
  async loginForLeisureCustomer({
    emailAddress = Constants.PI_LEISURE_EMAIL,
    password = Constants.PI_LEISURE_PASSWORD,
    retry = 5,
  }: { emailAddress?: string; password?: string; retry?: number } = {}): Promise<void> {
    console.log('Log in as leisure customer');
    if (await this.usernameLink.isVisible()) {
      return;
    }
    if (!(await this.loginLink.isVisible())) {
      try {
        await this.page.reload({ waitUntil: 'domcontentloaded' });
      } catch {
        // Reload can timeout, continue to retry logic
      }
    }
    await this.openLoginModal();
    await this.loginIntoAccount({ emailAddress, password: EncryptionUtils.decode(password) });

    if (retry > 0) {
      const isLoggedIn = await this.usernameLink
        .waitFor({ state: 'visible', timeout: 200000 })
        .then(() => true)
        .catch(() => false);
      if (isLoggedIn) {
        return;
      }

      if (await this.loginModalCloseButton.isVisible()) {
        await this.loginModalCloseButton.click();
      }
      if (!(await this.usernameLink.isVisible())) {
        try {
          await this.page.reload({ waitUntil: 'domcontentloaded' });
        } catch {
          // Reload can timeout, continue to retry logic
        }
      }
      await this.loginForLeisureCustomer({ emailAddress, password, retry: retry - 1 });
    }
  }

  /** Log out the current user if logged in. */
  async logOutUserIfLoggedIn(): Promise<void> {
    if (await this.loginLink.isVisible()) {
      return;
    }
    await this.ensureMobileSidebarOpen();
    if (Constants.BROWSER_RESOLUTIONS.isDesktop()) {
      await this.usernameLink.click();
    }
    await this.logOutButton.click();
  }

  /** Click on the language selector button (desktop or mobile). */
  async clickOnLanguageSelector(): Promise<void> {
    console.log('Click on language selector button');
    if (Constants.BROWSER_RESOLUTIONS.isDesktop()) {
      await this.languageSelectorButton.click();
    } else {
      await this.ensureMobileSidebarOpen();
      await this.mobileLanguageSelectorButton.click();
    }
  }

  /** Click on the language option matching the given locale-string language ("gb-en"/"de-de"). */
  async clickOnLanguageOptionBasedOnLocale(): Promise<void> {
    const currentLocale = getCurrentLocale();
    const flagIcon =
      currentLocale.language === Locales.GB_EN.language
        ? this.englishLanguageSelectorFlagIcon
        : this.germanLanguageSelectorFlagIcon;
    await flagIcon.click();
    await this.languageSelectorButton.waitFor({ state: 'attached' });
  }

  /** Click the Manage booking link/menu, opening the modal or navigating to the page. */
  async openManageBookingModal(): Promise<void> {
    console.log('Open Manage booking modal');
    await this.ensureMobileSidebarOpen();
    await this.manageBookingsLabel.click();
  }

  // ######## UI validations ########

  /** Validate an alert is displayed when login credentials are incorrect. */
  async validateIncorrectCredentialsAlertIsDisplayed(): Promise<void> {
    await this.incorrectCredentialsAlert.waitFor({ state: 'visible' });
  }
}
