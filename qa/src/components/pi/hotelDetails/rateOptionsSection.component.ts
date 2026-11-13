import { type Locator, expect } from '@playwright/test';
import { RateOptionComponent } from './rateOption.component';

/**
 * Rate options section within a hotel rate card containing the UI elements, custom actions and
 * validations. Mirrors qa/reference `components/opera/hotelDetails/rateOptionsSection.js`
 * (simplified: rate-plan-code-sorting cross-reference against `HotelAvailability`/
 * `RatesInformation`/`HotelRoomTypeInformation` API responses is dropped - it's a data-layer
 * concern already covered by the target's GraphQL-response helpers, not a UI validation).
 */
export class RateOptionsSectionComponent {
  private readonly container: Locator;

  constructor(container: Locator) {
    this.container = container;
  }

  // ######## UI elements/properties ########

  get optionContainersList(): Locator {
    return this.container.locator('div[data-testid="hdp_rateItemCard"]');
  }

  /** Rate option components for each rate item card in this section. */
  async rateOptionsList(): Promise<RateOptionComponent[]> {
    const options = this.optionContainersList;
    await options.first().waitFor({ state: 'visible' }).catch(() => {});
    const count = await options.count();
    return Array.from({ length: count }, (_, index) => new RateOptionComponent(options.nth(index)));
  }

  /** Find the rate option component whose title matches the given rate name. */
  async getRateOptionByName(rateName: string): Promise<RateOptionComponent | null> {
    for (const rateOption of await this.rateOptionsList()) {
      if ((await rateOption.optionTitleLabel.innerText()) === rateName) {
        return rateOption;
      }
    }
    return null;
  }
}
