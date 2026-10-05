import { expect, type Locator } from '@playwright/test';
import { HeaderSectionComponent } from '../../components/ccui/homePage/headerSection.component';
import { SearchConsoleComponent } from '../../components/ccui/searchConsole/searchConsole.component';
import { BasePage } from '../shared/base.page';

/**
 * Home page from CCUI environment.
 */
export class HomePage extends BasePage {
  // ######## properties ########

  readonly url = '';

  // ######## UI elements/properties ########

  readonly headerSection: HeaderSectionComponent = new HeaderSectionComponent();
  readonly searchConsole: SearchConsoleComponent = new SearchConsoleComponent();
  readonly headerContainer: Locator = this.headerSection.headerContainer;

  // ######## methods ########

  // ######## UI actions/navigation ########

  /**
   * Loads the home page handling cookies and the needed authorization.
   * @param params Navigation options.
    * @param params.isAuthorized True if HTTP authentication is required.
   */
  async open({ isAuthorized = false }: { isAuthorized?: boolean } = {}): Promise<void> {
    console.log('Open CCUI home page');
    await this.restoreHttpCredentials();
    await this.authorizeSecureUrls(isAuthorized);
    if (isAuthorized) {
      const baseUrl = global.browser.options.baseUrl;
      if (!baseUrl) {
        throw new Error('CCUI baseUrl is not configured in browser options.');
      }
      await this.cookieConsent.preSetConsentCookies();
      await this.setHttpCredentialsForUrl(baseUrl);
      await this.page.goto(baseUrl, { waitUntil: 'domcontentloaded', timeout: 60000 });
      await this.notificationPopup.dismissIfPresent();
      return;
    }

    await this.openLocalizedPath(this.url);
  }

  /** Restore HTTP credentials before navigating through protected CCUI domains. */
  private async restoreHttpCredentials(): Promise<void> {
    const httpAuthUsername = global.browser?.options?.httpAuthUsername;
    if (!httpAuthUsername) {
      return;
    }

    await this.page.context().setHTTPCredentials({
      username: httpAuthUsername,
      password: global.browser?.options?.httpAuthPassword ?? '',
    });
  }

  /** Authorize the secure CCUI and Premier Inn domains before loading the home page. */
  private async authorizeSecureUrls(isAuthorized: boolean): Promise<void> {
    const secureUrls = [global.browser.options.secureUrl2, global.browser.options.secureUrl].filter((url): url is string => Boolean(url));
    for (const secureUrl of secureUrls) {
      console.log(`Authorize secure URL: ${new URL(secureUrl).origin}`);
      if (isAuthorized) {
        await this.setHttpCredentialsForUrl(secureUrl);
      }
      await this.page.goto(secureUrl, { waitUntil: 'domcontentloaded', timeout: 60000 });
    }
  }

  private async setHttpCredentialsForUrl(url: string): Promise<void> {
    const username = global.browser?.options?.httpAuthUsername;
    const password = this.getAuthPasswordForUrl(url);
    if (username && password) {
      await this.page.context().setHTTPCredentials({ username, password });
    }
  }

  private getAuthPasswordForUrl(url: string): string | undefined {
    const secureUrl = global.browser?.options?.secureUrl?.replace(':443', '');
    const oldAuthPassword = global.browser?.options?.oldAuthPassword;
    if (global.browser?.options?.app === 'ccui' && secureUrl && url.includes(secureUrl) && oldAuthPassword) {
      return oldAuthPassword;
    }

    return global.browser?.options?.httpAuthPassword;
  }

  // ######## UI validations ########

  /**
   * Validate that the header is displayed on the home page.
   * @param isDisplayed true -> element is displayed / false -> element not displayed.
   */
  async validateHeaderContainerIsDisplayed(isDisplayed: boolean): Promise<void> {
    console.log('Validate header container display state');
    if (isDisplayed) {
      await expect(this.headerContainer, 'Header').toBeVisible();
    } else {
      await expect(this.headerContainer, 'Header').toBeHidden();
    }
  }

  /** Check we reached the current page by checking a specific element from the page. */
  async validatePage(): Promise<void> {
    console.log('Validate CCUI home page');
    await expect(this.headerSection.logoImage, 'Header logo image').toBeVisible();
  }
}