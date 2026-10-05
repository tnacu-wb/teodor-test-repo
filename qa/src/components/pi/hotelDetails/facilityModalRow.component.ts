import { type Locator, expect } from '@playwright/test';
import { type HotelFacility } from '@api/response/hotelFacility';

/**
 * One facility row from the facilities modal containing the UI elements, custom actions and validations.
 * Mirrors qa/reference `components/opera/hotelDetails/facilityModalRow.js`.
 */
export class FacilityModalRowComponent {
  private readonly container: Locator;

  constructor(container: Locator) {
    this.container = container;
  }

  // ######## UI elements/properties ########

  get facilityIcon(): Locator {
    return this.container.locator('img');
  }

  get facilityNameLabel(): Locator {
    return this.container.locator('p[data-testid="facility-title"]');
  }

  get facilityDescriptionLabel(): Locator {
    return this.container.locator('p[data-testid="facility-description"]');
  }

  // ######## UI actions/navigation ########

  // ######## UI validations ########

  /** Validate this facility row's icon, title, and (optional) description. */
  async validateData({ title, description, iconPath }: { title: string; description: string; iconPath: string }): Promise<void> {
    await expect(this.facilityIcon, 'Facility icon').toBeVisible();
    await expect(this.facilityIcon, 'Facility icon path').toHaveAttribute('src', new RegExp(iconPath));
    await expect(this.facilityNameLabel, 'Facility title').toHaveText(title);
    if (description !== '') {
      await expect(this.facilityDescriptionLabel, 'Facility description').toHaveText(description);
    } else {
      await expect(this.facilityDescriptionLabel, 'Empty description label').not.toBeVisible();
    }
  }

  /** Validate this row's data directly from a HotelFacility API record. */
  async validateFromApi(facility: HotelFacility): Promise<void> {
    await this.validateData({
      title: facility.name ?? '',
      description: facility.description ?? '',
      iconPath: facility.icon ?? '',
    });
  }
}
