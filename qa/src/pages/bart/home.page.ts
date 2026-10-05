import { type Locator, expect } from '@playwright/test';
import { Constants } from '@test-data/constants';
import { type HotelData, Hotels } from '@test-data/hotels';
import { BartSearchConsolePage } from './searchConsole.page';
import { BartUserDropdownMenuComponent } from '../../components/shared/bart/header/userDropdownMenu.component';
import { HeaderComponent } from '../../components/pi/header/header.component';
import { BasePage } from '../shared/base.page';
import { StorageUtils } from '../../utils/storageUtils';

/**
 * Home page of the legacy ("bart") Premier Inn web application. Mirrors qa/reference
 * `pages/bart/home.page.js` (which itself imports from `pages/pi` and describes itself as
 * "Home page of the Premier Inn web application" - this is a legacy/decommissioned build of the
 * PI site, kept standalone here). Inert/unwired - not part of any fixture or `App` type; see
 * session memory for why `bart` has no live environment config in this target framework.
 */
export class BartHomePage extends BasePage {
  // ######## UI elements/properties ########

  readonly loadMoreBtn: Locator = this.page.locator('#load-more');
  readonly editorialCollectionContainer: Locator = this.page.locator('div[class*="editorial__collection"]');
  readonly loginButton: Locator = Constants.BROWSER_RESOLUTIONS.isDesktop()
    ? this.page.locator('button#log-in')
    : this.page.locator('div#mobile-login-tab');
  readonly mobileMenuContainer: Locator = this.page.locator('div.mobile-menu');
  readonly mobileBurgerMenuButton: Locator = this.page.locator('div#pi-menu-button');
  readonly loginModal: Locator = this.page.locator('modal-dialog.pi-modal__dialog').nth(1);
  readonly emailAddressInput: Locator = this.page.locator('input#email-input');
  readonly passwordInput: Locator = this.page.locator('input#password-input');
  readonly forgotPasswordLink: Locator = this.page.locator('a#forgotten-password');
  readonly incorrectCredentialsLabel: Locator = this.page.locator('form.login-form form-errors div wb-notification-content div');
  readonly signUpLink: Locator = this.page.locator('a#sign-up');
  readonly submitButton: Locator = this.page.locator('button#submit-button');
  readonly acceptAllCookiesButton: Locator = this.page.locator('button#accept-all-cookies-button');
  readonly notificationPermissionPopupAllowButton: Locator = this.page.locator('button[data-testid="pi-notification-permission-popup-allow-btn"]');
  readonly notificationPermissionPopupDenyButton: Locator = this.page.locator('button[data-testid="pi-notification-permission-popup-deny-btn"]');

  // UI components

  readonly searchConsole: BartSearchConsolePage = new BartSearchConsolePage();
  readonly userDropdownMenu: BartUserDropdownMenuComponent = new BartUserDropdownMenuComponent();
  readonly header: HeaderComponent = new HeaderComponent();

  // ######## UI actions/navigation ########

  /** Open the Bart home page before using its search or account actions. */
  async open(): Promise<void> {
    await this.openLocalizedPath('home.html');
  }

  /** Open the account login modal, including the mobile navigation path when required. */
  async openLoginModal(): Promise<void> {
    await this.header.openLoginModal();
  }

  /** Click the mobile burger menu, if the mobile menu isn't already open. */
  async clickOnMobileBurger(): Promise<void> {
    console.log('Click on mobile burger');
    if (!(await this.mobileMenuContainer.isVisible().catch(() => false))) {
      await this.mobileBurgerMenuButton.click();
    }
  }

  /** Set the email address input in the login modal. */
  async setEmailAddress(emailAddressValue: string): Promise<void> {
    console.log(`Sign In email address = ${emailAddressValue}`);
    await this.emailAddressInput.fill(emailAddressValue);
  }

  /** Set the password input in the login modal. */
  async setPassword(passwordValue: string): Promise<void> {
    console.log('Sign In password = ****');
    await this.passwordInput.fill(passwordValue);
  }

  /** Log in using the given credentials after opening the login modal. */
  async loginIntoAccount({ emailAddress, password }: { emailAddress: string; password: string }): Promise<void> {
    await this.header.openLoginModal();
    await this.header.loginIntoAccount({ emailAddress, password });
  }

  /** Log in as the default leisure customer using the reference credential contract. */
  async loginForLeisureCustomer({
    emailAddress = Constants.PI_LEISURE_EMAIL,
    password = Constants.PI_LEISURE_PASSWORD,
  }: { emailAddress?: string; password?: string } = {}): Promise<void> {
    await this.header.loginForLeisureCustomer({
      emailAddress,
      password,
    });
  }

  /**
   * Navigate to the PI hotel-details page and authenticate a leisure customer.
   * Mirrors the reference BART-to-Opera handoff used by amendment journeys.
   *
   * @param options.emailAddress - Leisure customer email address.
   * @param options.password - Encoded leisure customer password.
   * @param options.hotel - Hotel whose details page establishes the login context.
   */
  async navigateToHotelDetailsPageAndLoginIntoAccount({
    emailAddress = Constants.PI_LEISURE_EMAIL,
    password = Constants.PI_LEISURE_PASSWORD,
    hotel = Hotels.DEFAULT_HOTEL,
  }: {
    emailAddress?: string;
    password?: string;
    hotel?: HotelData;
  } = {}): Promise<void> {
    if (!hotel.slug) {
      throw new Error(`Hotel ${hotel.id} does not have a hotel-details slug.`);
    }
    await global.piPages.hotelDetailsPage.openHotelDetailsBySlug(hotel.slug);
    await StorageUtils.deleteSignInCookies(this.page);
    await this.header.loginForLeisureCustomer({ emailAddress, password });
    await global.piPages.hotelDetailsPage.validatePage();
  }

  // ######## UI validations ########

  /** Validate the incorrect-credentials alert is displayed. */
  async validateIncorrectCredentialsAlertIsDisplayed(): Promise<void> {
    await expect(this.incorrectCredentialsLabel, 'Incorrect credentials alert').toBeVisible();
  }
}
