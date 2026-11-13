import { type Locator, expect } from '@playwright/test';
import { BasePage } from '../shared/base.page';

/**
 * Buy Our Bed page of the legacy ("bart") Premier Inn web application (EN only). Mirrors
 * qa/reference `pages/bart/buyOurBed.page.js`. Inert/unwired - not part of any fixture or `App`
 * type; `bart` has no environment config in the target framework (see session memory).
 */
export class BuyOurBedPage extends BasePage {
  static readonly BUY_OUR_BED_WEB_LINK = 'https://www.premierinn.com/gb/en/sleep/buy-our-bed.html';

  // ######## UI elements/properties ########

  readonly pageNameTitleLabel: Locator = this.page.locator('div[data-pagename="nd: Buy our bed"] h1');

  // ######## UI actions/navigation ########

  // ######## UI validations ########

  /** Validate the page URL was redirected to the Buy Our Bed URL. */
  async validatePageUrl(): Promise<void> {
    console.log('Validate page url was redirected to Buy our bed url');
    await expect(this.page, 'Page was not redirected to the expected link').toHaveURL(BuyOurBedPage.BUY_OUR_BED_WEB_LINK);
  }

  /** Validate the Buy Our Bed page was reached. */
  async validatePage(): Promise<void> {
    console.log('Validate Buy our bed page was reached');
    await expect(this.pageNameTitleLabel, 'Buy our bed page title').toBeVisible();
  }
}
