import { type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';

/**
 * One hotel card item (search results page -> hotel cards list) on the legacy ("bart") Premier
 * Inn web application. Mirrors qa/reference `pages/bart/components/searchResults/hotelCard.js`.
 */
export class BartHotelCardComponent {
  private readonly container: Locator;

  constructor(container: Locator) {
    this.container = container;
  }

  // ######## UI elements/properties ########

  get hotelNameLabel(): Locator {
    return this.container.locator('hotel-name name');
  }

  get hotelDistanceMilesLabel(): Locator {
    return this.container.locator('hotel-core-info distance div');
  }

  get hotelDistanceTextLabel(): Locator {
    return this.container.locator('hotel-core-info distance span');
  }

  get hotelPhotoContainer(): Locator {
    return this.container.locator('hotel-photo');
  }

  get hotelFacilitiesList(): Locator {
    return this.container.locator('facilities-wrapper img');
  }

  get hotelStyleLabelList(): Locator {
    return this.container.locator('message-wrapper > *');
  }

  get hotelViewDetailsBtn(): Locator {
    return this.container.locator('hotel-rate cta-button');
  }

  get hotelRoomStartingPriceLabel(): Locator {
    return this.container.locator('hotel-rate hotel-rate hotel-price price-integer');
  }

  get hotelPriceCurrencySignLabel(): Locator {
    return this.container.locator('hotel-rate hotel-rate hotel-price currency-sign');
  }

  get hotelFromLabel(): Locator {
    return this.container.locator('hotel-rate hotel-rate > div');
  }

  get hotelSoldOutLabel(): Locator {
    return this.container.locator('sold-out fullybookedstyle');
  }

  // ######## UI actions/navigation ########

  /** Click 'View details' for this hotel card. */
  async clickViewDetails(): Promise<void> {
    console.log('Click View Details button');
    await this.hotelViewDetailsBtn.click();
  }

  /** Click the source-compatible View Details action. */
  async clickViewDetailsButton(): Promise<void> {
    console.log('Click source-compatible View Details button');
    await this.clickViewDetails();
  }

  // ######## UI validations ########

  /** Validate the hotel card data and available hotel state. */
  async validateData(): Promise<void> {
    const hotelName = (await this.hotelNameLabel.textContent() ?? '').trim();
    console.log(`Validate data for ${hotelName} hotel`);

    const distanceText = (await this.hotelDistanceMilesLabel.textContent() ?? '').trim();
    expect(distanceText, 'Numerical distance value').toMatch(/^([0-9]*\.?[0-9]+|\.[0-9]+) miles/);

    const environment = (global.browser?.options as { environment?: string } | undefined)?.environment;
    const expectedDistanceText = environment === 'uat'
      ? Strings.FROM_YOUR_SEARCH_SEARCH_RESULTS
      : environment === 'beta'
        ? Strings.FROM_SEARCH_LOCATION
        : undefined;
    if (expectedDistanceText) {
      await expect(this.hotelDistanceTextLabel, 'Distance text').toContainText(await expectedDistanceText.name);
    }

    expect(hotelName.length, 'Hotel name length').toBeGreaterThan(0);
    await expect(this.hotelViewDetailsBtn, 'View details button').toContainText(await Strings.VIEW_DETAILS.name);

    if (await this.hotelFromLabel.isVisible()) {
      await expect(this.hotelFromLabel, 'From price label').toContainText(await Strings.FROM_SEARCH_RESULTS.name);
      await expect(this.hotelPriceCurrencySignLabel, 'Pound currency sign').toContainText(await Strings.POUND_CURRENCY_SIGN.name);
      const priceText = (await this.hotelRoomStartingPriceLabel.textContent() ?? '').trim();
      expect(priceText, 'Numerical price value').toMatch(/^([0-9]*\.?[0-9]+|\.[0-9]+)/);
    } else {
      await expect(this.hotelSoldOutLabel, 'Sold out label').toBeVisible();
    }

    const expectedFacilities = [await Strings.AIR_CONDITIONING.name, await Strings.RESTAURANT.name, await Strings.PARKING.name];
    for (let index = 0; index < await this.hotelFacilitiesList.count(); index++) {
      const facility = this.hotelFacilitiesList.nth(index);
      const altAttribute = await facility.getAttribute('alt');
      expect(expectedFacilities, 'Facility check').toContain(altAttribute);
      await facility.scrollIntoViewIfNeeded();
    }

    const expectedStyles = [await Strings.NEW_ROOMS.name, await Strings.PREMIER_PLUS.name];
    for (let index = 0; index < await this.hotelStyleLabelList.count(); index++) {
      const styleText = (await this.hotelStyleLabelList.nth(index).textContent() ?? '').trim();
      expect(expectedStyles, 'Style label check').toContain(styleText);
    }

    await expect(this.hotelPhotoContainer, 'Hotel image').toBeVisible();
  }
}
