import { expect, type Locator, type Page } from "@playwright/test";
import {
  CookiesSectionComponent,
  ToastNotificationSectionComponent,
} from "../../components/pib";

/** Shared base page for non-localized InnBusiness routes. */
export abstract class BasePibPage {
  protected readonly page: Page = global.page;
  readonly cookiesSection = new CookiesSectionComponent();
  readonly toastNotificationSection = new ToastNotificationSectionComponent();

  /** Navigate to a PIB path relative to the configured PIB base URL. */
  protected async openPath(path: string, acceptCookies = true): Promise<void> {
    console.log(`Open PIB path: ${path}`);
    if (acceptCookies) await this.cookiesSection.clickAcceptCookiesButton();
    const configuredBaseUrl = global.browser.options.baseUrl;
    if (!configuredBaseUrl) throw new Error("PIB baseUrl is required for navigation");
    const configuredUrl = new URL(configuredBaseUrl);
    const localePrefix = configuredUrl.pathname.split('/').filter(Boolean)[0] ?? '';
    const navigationBase = `${configuredUrl.origin}/${localePrefix}/`;
    await this.page.goto(
      new URL(path.replace(/^\/+/, ""), navigationBase).toString(),
      { waitUntil: "domcontentloaded" },
    );
  }

  /** Validate the current URL contains the expected PIB relative path. */
  protected validateUrl(path: string): void {
    expect(this.page.url(), `PIB URL should contain ${path}`).toContain(path);
  }

  /** Validate a PIB page marker is visible. */
  protected async validatePageMarker(
    marker: Locator,
    description: string,
  ): Promise<void> {
    await expect(marker, `${description} page marker`).toBeVisible({ timeout: 60000 });
  }
}
