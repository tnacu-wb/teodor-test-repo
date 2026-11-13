import { type Page } from '@playwright/test';
import { CookieConsentComponent } from '../../components/shared/cookieConsent.component';
import { NotificationPopupComponent } from '../../components/shared/notificationPopup.component';
import { Locales } from '../../test-data/locales';

/**
 * Shared base for Premier Inn page objects.
 *
 * Provides access to the Playwright page plus cross-page components that need to
 * run around direct navigation, such as consent cookie setup and notification
 * prompt dismissal.
 */
export abstract class BasePage {
  /** Playwright page instance shared by the concrete page object. */
  readonly page: Page = global.page;
  /** Cookie consent helper used before navigation to avoid banner interference. */
  readonly cookieConsent: CookieConsentComponent = new CookieConsentComponent();
  /** Notification permission helper used after navigation when the prompt appears. */
  readonly notificationPopup: NotificationPopupComponent = new NotificationPopupComponent();

  /**
   * Navigate to a Premier Inn localized path.
   *
   * Accepts paths with or without a leading slash and prefixes them with the
   * active locale route segment, such as /gb/en or /de/de.
   */
  protected async openLocalizedPath(path: string): Promise<void> {
    console.log(`Navigating to localized path: ${path}`);
    await this.cookieConsent.preSetConsentCookies();

    const cleanPath = path.replace(/^\/+/, '');
    const targetPath = cleanPath ? `${this.localizedRoutePrefix}/${cleanPath}` : this.localizedRoutePrefix;
    const navigationTimeout = global.browser?.options?.navigationTimeout;
    await this.page.goto(targetPath, { waitUntil: 'domcontentloaded', timeout: navigationTimeout });
    await this.notificationPopup.dismissIfPresent();
  }

  private get localizedRoutePrefix(): string {
    const localeString = global.browser?.options?.locale ?? Locales.GB_EN.name;
    const locale = Locales.getLocaleByString(localeString);
    return `/${locale.country}/${locale.language}`;
  }
}