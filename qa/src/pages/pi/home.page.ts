import { expect, type Locator } from '@playwright/test';
import { SearchConsoleComponent } from '../../components/pi/searchConsole/searchConsole.component';
import { Locales } from '../../test-data/locales';
import { BasePage } from '../shared/base.page';

/**
 * PI Homepage - the main landing page for premierinn.com.
 * Contains the search console for hotel search.
 *
 * Composes shared components:
 * - CookieConsentComponent (cookie banner handling)
 * - NotificationPopupComponent (notification permission popup)
 * - SearchConsoleComponent (location, dates, rooms, submit)
 */
export class HomePage extends BasePage {
  // ######## UI elements/properties ########

  readonly url = 'home.html';
  readonly searchConsole: SearchConsoleComponent = new SearchConsoleComponent();

  readonly searchSubmitButton: Locator = this.searchConsole.submitButton;
  readonly searchLocationInput: Locator = this.searchConsole.locationInput;
  readonly displayQAPanelButton: Locator = this.page.getByRole('button', { name: 'Display QA Panel' });
  readonly dloneu2363516Button: Locator = this.page.locator('button[data-testid="undefined-menuButton"] + div button').nth(1);
  readonly duncro6988257Button: Locator = this.page.locator('button[data-testid="undefined-menuButton"] + div button').first();
  readonly goToPaymentsButton: Locator = this.page.locator('button.chakra-button.css-1b74zjw');

  // ######## UI actions/navigation ########

  /** Navigate to the homepage using the legacy configurable open contract. */
  async open({
    url = this.url,
    acceptCookies = true,
    closeNotificationPopup = true,
  }: {
    url?: string;
    acceptCookies?: boolean;
    closeNotificationPopup?: boolean;
  } = {}): Promise<void> {
    // Restore HTTP auth credentials if were removed
    const httpAuthUsername = global.browser?.options?.httpAuthUsername;
    if (httpAuthUsername) {
      await this.page.context().setHTTPCredentials({
        username: httpAuthUsername,
        password: global.browser?.options?.httpAuthPassword ?? '',
      });
    }
    if (acceptCookies) {
      await this.cookieConsent.preSetConsentCookies();
    }
    const locale = Locales.getLocaleByString(global.browser?.options?.locale ?? Locales.GB_EN.name);
    const cleanPath = url.startsWith('/') ? url.slice(1) : url;
    await this.page.goto(`/${locale.country}/${locale.language}/${cleanPath}`, { waitUntil: 'domcontentloaded', timeout: 30000 });
    if (closeNotificationPopup) {
      await this.notificationPopup.dismissIfPresent();
    }
  }

  /**
   * Accept all cookies if the banner is displayed.
   * @deprecated Use `this.cookieConsent.dismissIfPresent()` instead.
   */
  async dismissCookieConsent(): Promise<void> {
    console.log('Dismiss cookie consent if present');
    await this.cookieConsent.dismissIfPresent();
  }

  /**
   * Dismiss the notification permission popup if displayed.
   * @deprecated Use `this.notificationPopup.dismissIfPresent()` instead.
   */
  async dismissNotificationPopup(): Promise<void> {
    console.log('Dismiss notification popup if present');
    await this.notificationPopup.dismissIfPresent();
  }

  /**
   * Search for a hotel by name using the search console.
   * @param hotelName - The hotel name or location to search for
   */
  async searchForHotel(hotelName: string): Promise<void> {
    console.log(`Search for hotel: ${hotelName}`);
    await this.searchConsole.searchForHotel(hotelName);
  }

  /**
   * Click the search/submit button to execute the search.
   */
  async submitSearch(): Promise<void> {
    console.log('Submit hotel search');
    await this.searchConsole.submit();
  }

  /** Open a hotel link whose visible text includes the hotel name. */
  async openHotelByHotelName(hotelName: string): Promise<void> {
    console.log(`Open hotel by name: ${hotelName}`);
    await this.page.getByRole('link', { name: hotelName, exact: false }).first().click();
  }

  /** Open a hotel link whose URL includes the hotel slug. */
  async openHotelByHotelSlug(hotelSlug: string): Promise<void> {
    console.log(`Open hotel by slug: ${hotelSlug}`);
    await this.page.locator(`a[href*="${hotelSlug}"]`).first().click();
  }

  // ######## UI validations ########

  /** Validate the homepage URL and search console are displayed. */
  async validateHomePageIsDisplayed(expectedUrl?: string): Promise<void> {
    console.log('Validate that url matches home page');
    if (expectedUrl) {
      expect(this.page.url(), `Home page URL should be ${expectedUrl}`).toBe(expectedUrl);
    } else {
      const locale = Locales.getLocaleByString(global.browser?.options?.locale ?? Locales.GB_EN.name);
      expect(this.page.url(), 'Home page URL should match the active locale').toContain(`/${locale.country}/${locale.language}/${this.url}`);
    }
    await expect(this.searchSubmitButton, 'Search button from Search console').toBeVisible();
  }
}
