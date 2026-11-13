import { type Locator, expect } from '@playwright/test';
import { BasePage } from '../shared/base.page';

/**
 * Terms and Conditions page of the legacy ("bart") Premier Inn web application. Mirrors
 * qa/reference `pages/bart/termsAndConditions.page.js`. Inert/unwired - see session memory.
 */
export class TermsAndConditionsPage extends BasePage {
  // ######## UI elements/properties ########

  readonly acceptAllCookiesButton: Locator = this.page.locator('button#accept-all-cookies-button');
  readonly termsAndConditionsContainer: Locator = this.page.locator('div[data-pagename*="Premier Inn: Terms & Conditions"]');
  readonly termsAndConditionsMenuList: Locator = this.page.locator('div[data-pagename*="Premier Inn: Terms & Conditions"] ul');

  // ######## UI actions/navigation ########

  /** Click 'Accept all cookies'. */
  async clickOnAcceptAllCookiesButton(): Promise<void> {
    await this.acceptAllCookiesButton.click();
  }

  // ######## UI validations ########

  /** Validate the current URL contains the expected terms-and-conditions path. */
  async validateTermsAndConditionPageUrl(expectedPartialUrl: string): Promise<void> {
    console.log('Validate Terms and conditions page contains correct path in url');
    const partialUrl = expectedPartialUrl.slice(expectedPartialUrl.indexOf('desktop/') + 8);
    await expect(this.page, 'Terms and conditions link is not correct').toHaveURL(new RegExp(partialUrl));
  }

  /** Validate the terms and conditions page container is displayed. */
  async validatePage(): Promise<void> {
    await expect(this.termsAndConditionsContainer, 'Terms and conditions container').toBeVisible();
  }
}
