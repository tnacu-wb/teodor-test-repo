import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';
import { Constants } from '@test-data/constants';
import { ApiSlugsCalls } from '@api/graphql/apiSlugsCalls';
import { type HotelFacility } from '@api/response/hotelFacility';
import { FacilitiesModalComponent } from './facilitiesModal.component';

/**
 * Hotel facilities section on the hotel details page containing the UI elements, custom actions
 * and validations. Mirrors qa/reference `components/opera/hotelDetails/hotelFacilitiesSection.js`.
 */
export class HotelFacilitiesSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly titleLabel: Locator = this.page.locator('p[data-testid="hdp_hotelFacilitiesText"] strong');
  readonly seeAllLink: Locator = Constants.BROWSER_RESOLUTIONS.isMobilePhone()
    ? this.page.locator('a[data-testid="hdp_mobileSeeAllHotelFacilities"]')
    : this.page.locator('p[data-testid="hdp_hotelFacilitiesText"] a');
  readonly hotelFacilitiesIconsList: Locator = Constants.BROWSER_RESOLUTIONS.isMobilePhone()
    ? this.page.locator('[data-testid*="-HotelFacilitiesHStack"] img')
    : this.page.locator('[data-testid*="-HotelFacilitiesVStack"] > img[data-testid="svg-container"]');
  readonly hotelFacilitiesDescriptionsList: Locator = Constants.BROWSER_RESOLUTIONS.isMobilePhone()
    ? this.page.locator('[data-testid*="-HotelFacilitiesHStack"] p')
    : this.page.locator('[data-testid*="-HotelFacilitiesVStack"] > p');

  // UI components

  readonly facilitiesModal: FacilitiesModalComponent = new FacilitiesModalComponent();

  // ######## UI actions/navigation ########

  /** Open the modal listing all hotel facilities. */
  async openFacilityModal(): Promise<void> {
    await this.seeAllLink.click();
    await expect(this.facilitiesModal.modal).toBeVisible();
  }

  // ######## UI validations ########

  /** Validate the hotel facilities section against the API facility list (falls back to fetching it). */
  async validateData(options: { apiHotelFacilities?: HotelFacility[] } = {}): Promise<void> {
    const apiHotelFacilities = options.apiHotelFacilities ?? (await ApiSlugsCalls.graphqlGetHotelFacilities());
    console.log('Validate hotel facilities section');

    await this.validateTitle();
    await expect(this.hotelFacilitiesIconsList, 'Hotel facilities icons count').toHaveCount(apiHotelFacilities.length);
    await expect(this.hotelFacilitiesDescriptionsList, 'Hotel facilities descriptions count').toHaveCount(apiHotelFacilities.length);
  }

  /** Validate the hotel facilities section title (default English label, or a caller-provided override for other locales). */
  async validateTitle(expectedLabel?: string): Promise<void> {
    console.log('Validate hotel facilities title');
    const label = expectedLabel ?? (await Strings.HOTEL_FACILITIES.name);
    await expect(this.titleLabel, `${label} label`).toContainText(label);
  }
}
