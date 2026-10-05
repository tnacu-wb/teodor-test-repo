import { type Locator, expect } from '@playwright/test';
import { BasePage } from '../shared/base.page';

/**
 * Presse (press/news) page of the legacy ("bart") Premier Inn web application (DE only).
 * Mirrors qa/reference `pages/bart/presse.page.js`. Inert/unwired - see session memory.
 */
export class PressePage extends BasePage {
  static readonly NEWSLETTER_SIGNUP_WEB_LINK = 'https://www.premierinn.com/de/de/presse.html';

  // ######## UI elements/properties ########

  readonly pageNameTitleLabel: Locator = this.page.locator('div[data-pagename="nde: Premier Inn: News"] h1');

  // ######## UI actions/navigation ########

  // ######## UI validations ########

  /** Validate the page URL was redirected to the Presse URL. */
  async validatePageUrl(): Promise<void> {
    console.log('Validate page url was redirected to presse url');
    await expect(this.page, 'Page was not redirected to the expected link').toHaveURL(PressePage.NEWSLETTER_SIGNUP_WEB_LINK);
  }

  /** Validate the Presse DE page was reached. */
  async validatePage(): Promise<void> {
    console.log('Validate Presse DE page was reached');
    await expect(this.pageNameTitleLabel, 'Presse page title').toBeVisible();
  }
}
