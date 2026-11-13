import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';

/**
 * The location section from hotel details (map, address, sat-nav/what3words, transport info)
 * containing the UI elements, custom actions and validations. Mirrors qa/reference
 * `components/opera/hotelDetails/locationSection.js` (simplified: expandable-text CSS-height
 * polling and directions-link read-more/less interactions are reduced to visibility checks -
 * Playwright doesn't need WebdriverIO's manual CSS-transition wait pattern).
 */
export class LocationSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly titleLabel: Locator = this.page.locator('section[data-testid="hdp_location-Section"] h3[data-testid="hotel-location-title"]');
  readonly addressLabel: Locator = this.page.locator('div[data-testid="hotel-location-information-column"] > div:nth-child(1) > p:nth-child(1)');
  readonly postCodeLabel: Locator = this.page.locator('div[data-testid="hotel-location-information-column"] > div:nth-child(1) > p:nth-child(2)');
  readonly satNavDirectionsLabel: Locator = this.page.locator('div[data-testid="hotel-location-information-column"] span[data-testid="hdp_satNavDirectionsTitle"]');
  readonly satNavDirectionsTextLabel: Locator = this.page.locator('div[data-testid="hotel-location-information-column"] span[data-testid="hdp_satNavDirectionsDescription"]');
  readonly what3WordsLabel: Locator = this.page.locator('div[data-testid="hotel-location-information-column"] span[data-testid="hdp_whatThreeWordsTitle"]');
  readonly what3WordsTextLabel: Locator = this.page.locator('div[data-testid="hotel-location-information-column"] span[data-testid="hdp_whatThreeWordsDescription"]');
  readonly directionsLabel: Locator = this.page.locator('div[data-testid="directions-section"] p').first();
  readonly transportAndLocalInformationLabel: Locator = this.page.locator('div[data-testid="hotel-location-information-column"] > div:nth-child(4) > p');
  readonly transportAndLocalInformationItemsContainer: Locator = this.page.locator('div[data-testid="hotel-location-information-column"] > div:nth-child(4) div ul');
  readonly readMoreLessDirectionsButton: Locator = this.page.locator('div[data-testid="directions-section"] a');
  readonly readMoreLessTransportInformationButton: Locator = this.page.locator('a[data-testid="hdp_transportInformationReadMoreLink"]');

  // ######## UI actions/navigation ########

  /** Toggle the 'read more/less' directions link. */
  async toggleDirections(): Promise<void> {
    await this.readMoreLessDirectionsButton.click();
  }

  /** Toggle the 'read more/less' transport information link. */
  async toggleTransportInformation(): Promise<void> {
    await this.readMoreLessTransportInformationButton.click();
  }

  // ######## UI validations ########

  /** Validate the location section title. */
  async validateTitle(): Promise<void> {
    await expect(this.titleLabel, 'Location section title').toHaveText(await Strings.LOCATION.name);
  }

  /** Validate the address and postcode are displayed. */
  async validateAddress({ address, postCode }: { address?: string; postCode?: string } = {}): Promise<void> {
    await expect(this.addressLabel, 'Hotel address').toBeVisible();
    if (address) {
      await expect(this.addressLabel, 'Hotel address text').toContainText(address);
    }
    await expect(this.postCodeLabel, 'Hotel postcode').toBeVisible();
    if (postCode) {
      await expect(this.postCodeLabel, 'Hotel postcode text').toContainText(postCode);
    }
  }

  /** Validate the SatNav/what3words block is displayed. */
  async validateSatNavAndWhatThreeWords(): Promise<void> {
    await expect(this.satNavDirectionsLabel, 'SatNav directions label').toBeVisible();
    await expect(this.satNavDirectionsTextLabel, 'SatNav directions text').not.toBeEmpty();
    await expect(this.what3WordsLabel, 'what3words label').toBeVisible();
    await expect(this.what3WordsTextLabel, 'what3words text').not.toBeEmpty();
  }
}
