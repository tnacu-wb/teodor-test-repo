import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';

/**
 * PI Hotel information section present to the left of the hotel gallery on the hotel details
 * page, containing the UI elements, custom actions and validations. Mirrors qa/reference
 * `components/opera/hotelDetails/piHotelInformationSection.js` (trivial PI override, no
 * additions) folded together with `components/common/hotelDetails/hotelInformationSectionBase.js`
 * (simplified: badge de-duplication loop and full API cross-checks are reduced to count/visibility
 * assertions - the underlying `ApiContentCalls`/`ApiSlugsCalls` distance/strapline endpoints are
 * already exercised elsewhere in the target suite).
 */
export class PiHotelInformationSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly hotelInformationContainer: Locator = this.page.locator('div[data-testid="hdp_hotelInformationTopSection"]');
  readonly hotelNameLabel: Locator = this.page.locator('h1[data-testid="hdp_hotelTitle"]');
  readonly searchLocationDistanceValue: Locator = this.page.locator('p[data-testid="hdp_hotelDistanceFromSearch"]');
  readonly boldDistanceValueLabel: Locator = this.searchLocationDistanceValue.locator('strong');
  readonly hotelBadgesList: Locator = this.page.locator('div[data-testid="hdp_badgesList"] span');
  readonly hotelDescriptionLabel: Locator = this.page.locator('p[data-testid="hdp_hotelHeadline"]').first();

  // ######## UI actions/navigation ########

  // ######## UI validations ########

  /** Validate the hotel title text. */
  async validateHotelTitle({ apiHotelTitle }: { apiHotelTitle: string }): Promise<void> {
    await expect(this.hotelNameLabel, 'Hotel name').toHaveText(apiHotelTitle);
  }

  /** Validate the distance-from-search label has a numeric/bold value. */
  async validateDistanceFormat(): Promise<void> {
    await expect(this.boldDistanceValueLabel, 'Hotel distance bold value').not.toBeEmpty();
  }

  /** Validate the distance label contains the given expected distance text. */
  async validateDistanceAgainstAPI({ distance }: { distance: string }): Promise<void> {
    await expect(this.searchLocationDistanceValue, 'Hotel distance from search').toContainText(distance);
  }

  /** Validate the hotel headline/strapline text. */
  async validateStrapline({ apiHotelHeadline }: { apiHotelHeadline: string }): Promise<void> {
    await expect(this.hotelDescriptionLabel, 'Hotel headline/strapline').toContainText(apiHotelHeadline);
  }

  /** Validate the hotel badge list has at least one badge with no duplicate labels. */
  async validateHotelBadges(): Promise<void> {
    const count = await this.hotelBadgesList.count();
    expect(count, 'Hotel badge labels list length').toBeGreaterThan(0);
    const labels = new Set<string>();
    for (let index = 0; index < count; index++) {
      const label = (await this.hotelBadgesList.nth(index).innerText()).trim();
      expect(labels.has(label), `Duplicated label: ${label}`).toBe(false);
      labels.add(label);
    }
  }
}
