import { expect, type Locator, type Page } from "@playwright/test";

/**
 * Cookies Section class
 */
export class CookiesSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########
  readonly cookiesSectionContainer: Locator = this.page.locator( '[data-testid="CookieConsentDialog-Container"], .ot-sdk-container', );
  readonly cookiesTitleLabel: Locator = this.cookiesSectionContainer.locator("h2");
  readonly cookiesDescriptionLabel: Locator = this.page.getByTestId( "CookieConsentDialog-Description", );
  readonly manageCookiesButton: Locator = this.page.getByTestId( "CookieConsentDialog-ManageButton", );
  readonly necessaryOnlyCookiesButton: Locator = this.page.getByTestId( "CookieConsentDialog-NecessaryOnlyButton", );
  readonly acceptAllCookiesButton: Locator = this.page.locator( '[data-testid="CookieConsentDialog-AcceptAllButton"], #onetrust-accept-btn-handler', );
  readonly manageCookiesContainer: Locator = this.page.getByTestId( "ManageCookiesDialog-Container", );
  readonly manageCookiesCloseButton: Locator = this.page.getByTestId( "Dialog-X-Close-Button", );
  readonly oneTrustAcceptCookiesButton: Locator = this.page.locator( "#onetrust-accept-btn-handler", );

  // ######## UI actions/navigation ########
  /** Click Accept All Cookies button. */
  async clickAcceptAllCookiesButton(): Promise<void> {
    console.log("Click Accept All Cookies button");
    let isDisplayed = true;
    try {
      await this.cookiesSectionContainer.waitFor({ state: "visible", timeout: 5000 });
    } catch {
      isDisplayed = false;
    }
    if (!isDisplayed) return;
    await this.acceptAllCookiesButton.scrollIntoViewIfNeeded();
    await this.acceptAllCookiesButton.click();
    await expect(this.cookiesSectionContainer, "Cookies section after accepting all cookies").not.toBeVisible({ timeout: 5000 });
  }

  /** Click Manage Cookies button. */
  async clickManageCookiesButton(): Promise<void> {
    console.log("Click Manage Cookies button");
    await this.manageCookiesButton.scrollIntoViewIfNeeded();
    await this.manageCookiesButton.click();
    await expect( this.manageCookiesContainer, "Manage Cookies container after clicking Manage Cookies button", ).toBeVisible();
  }

  /** Click Accept one trust cookies button. */
  async clickAcceptCookiesButton(): Promise<void> {
    console.log("Click Accept one trust cookies button");
    let isDisplayed = true;
    try {
      await this.oneTrustAcceptCookiesButton.waitFor({ state: "visible", timeout: 5000 });
    } catch {
      isDisplayed = false;
    }
    if (!isDisplayed) return;
    await this.oneTrustAcceptCookiesButton.scrollIntoViewIfNeeded();
    await this.oneTrustAcceptCookiesButton.click();
  }

  /** Click Manage Cookies Close button. */
  async clickManageCookiesCloseButton(): Promise<void> {
    console.log("Click Manage Cookies Close button");
    await this.manageCookiesCloseButton.scrollIntoViewIfNeeded();
    await this.manageCookiesCloseButton.click();
  }

  /** Click Necessary Only Cookies button. */
  async clickNecessaryOnlyCookiesButton(): Promise<void> {
    console.log("Click Necessary Only Cookies button");
    await this.necessaryOnlyCookiesButton.scrollIntoViewIfNeeded();
    await this.necessaryOnlyCookiesButton.click();
  }

  // ######## UI validations ########
  /** Validate cookies container is displayed. */
  async validateCookiesContainerIsDisplayed(): Promise<void> {
    console.log("Validate Cookies container is displayed");
    await expect( this.cookiesSectionContainer, "Cookies section container", ).toBeVisible();
  }

  /** Validate Cookies section against the cookie-consent dictionary. */
  async validateCookiesSection({
    cookiePolicies,
  }: {
    cookiePolicies: { introView: Record<string, string> };
  }): Promise<void> {
    console.log("Validate Cookies section");
    await expect( this.cookiesSectionContainer, "Cookies container", ).toBeVisible();
    await expect(this.cookiesTitleLabel, "Cookies title").toHaveText( cookiePolicies.introView.title, );
    await expect( this.cookiesDescriptionLabel, "Cookies description", ).toHaveText( cookiePolicies.introView.description.replace(/<\/?p>/g, "").trimEnd(), );
    await expect(this.manageCookiesButton, "Manage Cookies button").toHaveText( cookiePolicies.introView.manageButtonText, );
    await expect( this.necessaryOnlyCookiesButton, "Necessary Only Cookies button", ).toHaveText(cookiePolicies.introView.necessaryOnlyButtonText);
    await expect( this.acceptAllCookiesButton, "Accept All Cookies button", ).toHaveText(cookiePolicies.introView.acceptAllButtonText);
  }
}
