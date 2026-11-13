import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';

/**
 * Hotel parking section on the hotel details page containing the UI elements, custom actions and
 * validations. Mirrors qa/reference `components/opera/hotelDetails/hotelParkingSection.js`.
 */
export class HotelParkingSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly hotelParkingContainer: Locator = this.page.locator('section[data-testid="hdp_parking-Section"]');
  readonly titleLabel: Locator = this.page.locator('div[data-testid="hotel-details-parking"] h3');
  readonly hotelParkingDescriptionLabel: Locator = this.page.locator('div[data-testid="hotel-details-parking"] div');

  // ######## UI actions/navigation ########

  // ######## UI validations ########

  /** Validate the hotel parking section, or its absence. */
  async validateData({
    parkingDescription,
    isHotelParkingSectionDisplayed = true,
  }: { parkingDescription?: string; isHotelParkingSectionDisplayed?: boolean } = {}): Promise<void> {
    console.log('Validate the hotel parking');
    if (isHotelParkingSectionDisplayed) {
      await this.titleLabel.scrollIntoViewIfNeeded();
      await expect(this.hotelParkingContainer, 'Hotel Parking section').toBeVisible();
      await this.validateHotelParkingTitle();
      if (parkingDescription) {
        await this.validateHotelParkingDescription(parkingDescription);
      }
    } else {
      await expect(this.hotelParkingContainer, 'Hotel Parking section').not.toBeVisible();
    }
  }

  /** Validate the hotel parking section title. */
  async validateHotelParkingTitle(): Promise<void> {
    console.log('Validate hotel parking title');
    await expect(this.titleLabel, 'Hotel parking title').toHaveText(await Strings.HOTEL_PARKING.name);
  }

  /** Validate the hotel parking description text. */
  async validateHotelParkingDescription(parkingDescription: string): Promise<void> {
    console.log('Validate hotel parking description');
    await expect(this.hotelParkingDescriptionLabel, 'Hotel parking description').toContainText(parkingDescription);
  }
}
