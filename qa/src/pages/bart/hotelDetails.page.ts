import { type Locator, expect } from '@playwright/test';
import { BartSearchConsolePage } from './searchConsole.page';
import { BasePage } from '../shared/base.page';

/**
 * Hotel Details page of the legacy ("bart") Premier Inn web application. Mirrors qa/reference
 * `pages/bart/hotelDetails.page.js`. Inert/unwired - see session memory for why `bart` has no
 * live App/fixture wiring.
 */
export class BartHotelDetailsPage extends BasePage {
  // ######## UI elements/properties ########

  readonly hotelNameLabel: Locator = this.page.locator('h1.hdp-topsection__hotel-title');
  readonly hotelBadge: Locator = this.page.locator('div.hdp-topsection__badges > div');
  readonly hotelShortDescriptionLabel: Locator = this.page.locator('div.hdp-topsection__hotel-short-description');
  readonly hotelFacilitiesDescriptionsList: Locator = this.page.locator('p.hdp-topsection__facilities__facility-label');

  // UI components

  readonly searchConsole: BartSearchConsolePage = new BartSearchConsolePage();

  // ######## UI actions/navigation ########

  // ######## UI validations ########

  /** Validate the Bart HDP was reached. */
  async validatePage(): Promise<void> {
    console.log('Validate hotel details page for Bart hotel');
    await expect(this.hotelNameLabel, 'Bart hotel name').toBeVisible();
  }
}
