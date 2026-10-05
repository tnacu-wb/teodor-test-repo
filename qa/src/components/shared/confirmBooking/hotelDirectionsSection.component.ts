import { type Locator, type Page } from '@playwright/test';

/** Hotel directions section on the confirmation page. */
export class HotelDirectionsSectionComponent {
  private readonly page: Page = global.page;
  // ######## UI elements/properties ########
  readonly hotelDirectionsSectionContainer: Locator = this.page.locator('[data-testid="hotelDirections-label"]').locator('..');
  readonly hotelDirectionsTitleLabel: Locator = this.page.locator('[data-testid="hotelDirections-label"]');
  readonly hotelDirectionsMapContainer: Locator = this.page.locator('[data-testid="hotelDirections-map"]');
  readonly hotelDirectionsLabel: Locator = this.page.locator('[data-testid="hotelDirections-directions"]');

  // ######## UI actions/navigation ########

  // ######## UI validations ########
}