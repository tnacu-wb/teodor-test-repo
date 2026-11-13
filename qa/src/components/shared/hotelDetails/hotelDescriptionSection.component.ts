import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';
import { Helpers } from '@utils/helpers';

/**
 * The Hotel Description section within the hotel details page containing the UI elements,
 * custom actions and validations. Mirrors qa/reference
 * `components/common/hotelDetails/hotelDescriptionSection.js`.
 */
export class HotelDescriptionSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly container: Locator = this.page.locator('section[data-testid="hdp_description-Section"]');
  readonly hotelDescriptionTitleLabel: Locator = this.container.locator('h3');
  readonly hotelDescriptionContainer: Locator = this.container.locator('div[class*="formatLinks"]');

  // ######## UI actions/navigation ########

  /** Scroll to the hotel description title. */
  async scrollToHotelDescriptionTitle(): Promise<void> {
    console.log('Scroll to hotel description title');
    await this.hotelDescriptionTitleLabel.scrollIntoViewIfNeeded();
  }

  // ######## UI validations ########

  /** Validate the 'Hotel Description' section title. */
  async validateHotelDescriptionTitle(): Promise<void> {
    console.log("Validate 'Hotel description' title");
    await expect(this.hotelDescriptionTitleLabel, 'Hotel description title').toBeVisible();
    await expect(this.hotelDescriptionTitleLabel, '"Hotel description" title').toHaveText(await Strings.HOTEL_DESCRIPTION.name);
  }

  /** Validate the hotel description content after removing markup and unreadable characters. */
  async validateHotelDescriptionContent(expectedDescription: string): Promise<void> {
    console.log("Validate 'Hotel description' description text");
    await expect(this.hotelDescriptionContainer, 'Hotel description container').toBeVisible();
    const actualDescription = Helpers.removeNonReadableCharsFromString(await this.hotelDescriptionContainer.innerText());
    const normalizedExpectedDescription = Helpers.removeNonReadableCharsFromString(expectedDescription);
    expect(actualDescription, `Hotel description should equal "${normalizedExpectedDescription}"`).toBe(normalizedExpectedDescription);
  }

  /** Validate the hotel description text contains the expected description. */
  async validateHotelDescriptionText(expectedDescription: string): Promise<void> {
    console.log('Validate hotel description text');
    await expect(this.hotelDescriptionContainer, 'Hotel description text').toContainText(expectedDescription);
  }
}
