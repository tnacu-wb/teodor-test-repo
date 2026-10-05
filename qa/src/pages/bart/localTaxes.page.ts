import { type Locator, expect } from '@playwright/test';
import { BasePage } from '../shared/base.page';

/**
 * Local Taxes page of the legacy ("bart") Premier Inn web application. Mirrors qa/reference
 * `pages/bart/localTaxes.page.js`. Inert/unwired - see session memory.
 */
export class LocalTaxesPage extends BasePage {
  // ######## UI elements/properties ########

  readonly acceptAllCookiesButton: Locator = this.page.locator('button#accept-all-cookies-button');
  readonly localTaxesTextLabel: Locator = this.page.locator('h1.divider-container__heading.divider-container__heading__md-top');

  // ######## UI actions/navigation ########

  /** Click 'Accept all cookies'. */
  async clickOnAcceptAllCookiesButton(): Promise<void> {
    await this.acceptAllCookiesButton.click();
  }

  // ######## UI validations ########

  /** Validate the Local Taxes page was reached, dismissing the cookie banner if present. */
  async validatePage(): Promise<void> {
    if (await this.acceptAllCookiesButton.isVisible().catch(() => false)) {
      await this.clickOnAcceptAllCookiesButton();
    }
    await expect(this.localTaxesTextLabel, 'Local taxes page title').toBeVisible();
  }
}
