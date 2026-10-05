import { type Locator, expect } from '@playwright/test';

/**
 * Search Results Page hotel card component. Mirrors qa/reference
 * `components/common/searchResults/hotelCard.js` (PI-relevant subset).
 */
export class HotelCardComponent {
  // ######## UI elements/properties ########

  readonly hotelCard: Locator;
  readonly nameLabel: Locator;
  readonly distanceLabel: Locator;
  readonly thumbnailImage: Locator;
  readonly priceLabel: Locator;
  readonly soldOutLabel: Locator;
  readonly openingOnLabel: Locator;
  readonly viewDetailsButton: Locator;

  constructor(container: Locator) {
    this.hotelCard = container;
    this.nameLabel = container.locator('a p').first();
    this.distanceLabel = container.locator('p[data-testid="SRP-hotel-distance"]');
    this.thumbnailImage = container.locator('[data-testid="SRP-hotel-thumbnail"] img');
    this.priceLabel = container.locator('[data-testid="SRP-lowest-rate"] p').nth(1);
    this.soldOutLabel = container.locator('p[data-testid="SRP-hotel-soldout"]');
    this.openingOnLabel = container.locator('[data-testid="SRP-hotel-opening-information"] p').first();
    this.viewDetailsButton = container.locator('[data-testid="SRP-hotel-button"] button');
  }

  // ######## UI actions/navigation ########

  /** Click the "View details" button on this hotel card. */
  async clickViewDetails(): Promise<void> {
    await this.viewDetailsButton.click();
  }

  /** Check whether the hotel card currently displays a non-empty price label. */
  async hasDisplayedPrice(): Promise<boolean> {
    if (!(await this.priceLabel.isVisible())) {
      return false;
    }
    return ((await this.priceLabel.textContent()) ?? '').trim().length > 0;
  }

  // ######## UI validations ########

  /** Validate the hotel name label matches the given text (exact or truncated-prefix match). */
  async expectNameToContain(hotelName: string): Promise<void> {
    await expect(this.nameLabel).toContainText(hotelName.slice(0, 39));
  }
}
