import { type Locator, expect } from '@playwright/test';
import { BasePage } from '../shared/base.page';

/**
 * New Hotels page of the legacy ("bart") Premier Inn web application. Mirrors qa/reference
 * `pages/bart/newHotels.page.js`. Inert/unwired - see session memory.
 */
export class NewHotelsPage extends BasePage {
  static readonly URL = 'https://www.premierinn.com/gb/en/why/locations/new-hotels.html';

  // ######## UI elements/properties ########

  readonly pageNameTitleLabel: Locator = this.page.locator('div[data-pagename="nd: Premier Inn: Why: Locations: New hotels"] h1');

  // ######## UI actions/navigation ########

  // ######## UI validations ########

  /** Validate the page URL was redirected to the New Hotels URL. */
  async validatePageUrl(): Promise<void> {
    console.log('Validate page url was redirected to New hotels url');
    await expect(this.page, 'Page was not redirected to the expected link').toHaveURL(new RegExp(NewHotelsPage.URL));
  }

  /** Validate the New Hotels page was reached. */
  async validatePage(): Promise<void> {
    console.log('Validate New hotels page was reached');
    await expect(this.pageNameTitleLabel, 'New hotels page title').toBeVisible();
  }
}
