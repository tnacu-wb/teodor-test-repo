import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';
import { Helpers } from '@utils/helpers';

/**
 * The Hotel Contact Information section within the hotel details page containing the UI
 * elements, custom actions and validations. Mirrors qa/reference
 * `components/common/hotelDetails/hotelContactInformationSection.js`.
 */
export class HotelContactInformationSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly container: Locator = this.page.locator('div[data-testid="hotel-details-contact"]');
  readonly hotelContactInformationTitleLabel: Locator = this.container.locator('h3');
  readonly hotelContactInformationPhoneNumberLabel: Locator = this.container.locator('p[class*="text"]');
  readonly hotelContactInformationPhoneRatesNoteContainer: Locator = this.container.locator('div[class*="formatLinks"]');

  // ######## UI actions/navigation ########

  // ######## UI validations ########

  /** Validate the 'Hotel Contact Information' section title. */
  async validateHotelContactInformationTitle(): Promise<void> {
    console.log("Validate 'Hotel Contact Information' title");
    await expect(this.hotelContactInformationTitleLabel, 'Hotel Contact Information title').toBeVisible();
    await expect(this.hotelContactInformationTitleLabel, '"Hotel Contact Information" title').toHaveText(
      await Strings.HOTEL_CONTACT_INFORMATION.name
    );
  }

  /** Validate the 'Hotel Contact Information' section phone number. */
  async validateHotelContactInformationPhoneNumber(expectedPhoneNumber: string): Promise<void> {
    console.log("Validate 'Hotel Contact Information' contact phone number");
    await expect(this.hotelContactInformationPhoneNumberLabel, 'Hotel contact phone number').toBeVisible();
    const actualPhoneNumber = Helpers.removeNonReadableCharsFromString(await this.hotelContactInformationPhoneNumberLabel.innerText());
    const normalizedExpectedPhoneNumber = Helpers.removeNonReadableCharsFromString(expectedPhoneNumber);
    expect(actualPhoneNumber, `Phone number should equal "${normalizedExpectedPhoneNumber}"`).toBe(normalizedExpectedPhoneNumber);
  }

  /** Validate the phone rates note. */
  async validateHotelContactInformationPhoneRatesNote(): Promise<void> {
    console.log("Validate 'Hotel Contact Information' phone rates note");
    await expect(this.hotelContactInformationPhoneRatesNoteContainer, 'Hotel contact phone rates note').toBeVisible();
    const actualRateNote = Helpers.removeNonReadableCharsFromString(await this.hotelContactInformationPhoneRatesNoteContainer.innerText());
    const expectedRateNote = Helpers.removeNonReadableCharsFromString(await Strings.CALLS_TO.name);
    expect(actualRateNote, `Phone rates note should equal "${expectedRateNote}"`).toBe(expectedRateNote);
  }
}
