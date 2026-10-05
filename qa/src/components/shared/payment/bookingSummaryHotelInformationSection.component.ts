import { type Page, type Locator, expect } from '@playwright/test';
import { Constants } from '@test-data/constants';
import { type HotelInformation } from '@api/response/hotelInformation';
import { type HotelAddressByHotelInformation } from '@api/response/hotelAddressByHotelInformation';

/**
 * The booking summary hotel information section present on the Payment page vertical strip
 * containing the UI elements, custom actions and validations. Mirrors qa/reference
 * `components/common/payment/bookingSummaryHotelInformationSection.js`.
 */
export class PaymentBookingSummaryHotelInformationSectionComponent {
  private readonly page: Page = global.page;

  private get resolutionId(): string {
    return Constants.RESOLUTION_IDENTIFIER_FOR_DATATESTID_ATTRIBUTE;
  }

  // ######## UI elements/properties ########

  get sectionContainer(): Locator {
    return this.page.locator(`div[data-testid="BookingSummary-${this.resolutionId}-HotelInformation-Wrapper"]`);
  }

  get hotelNameLabel(): Locator {
    return this.page.locator(`h5[data-testid="BookingSummary-${this.resolutionId}-HotelInformation-HotelName"]`);
  }

  get hotelAddressLabelsList(): Locator {
    return this.sectionContainer.locator(`div[data-testid="BookingSummary-${this.resolutionId}-HotelInformation-HotelAddress"] > div h6`);
  }

  // ######## UI actions/navigation ########

  // ######## UI validations ########

  /** Validate the hotel name matches the expected value. */
  async validateHotelName(expectedHotelName: string): Promise<void> {
    console.log('Validate hotel name against hotel information');
    await expect(this.hotelNameLabel, 'Hotel name').toBeVisible();
    await expect(this.hotelNameLabel, 'Payment booking summary hotel name').toHaveText(expectedHotelName);
  }

  /** Validate hotel name and address from hotel-information data. */
  async validateHotelNameAndAddress(hotelInformation: HotelInformation): Promise<void> {
    console.log('Validate hotel information on Booking summary component');
    await this.validateHotelName(hotelInformation.name ?? '');
    if (hotelInformation.address) await this.validateHotelAddress(hotelInformation.address);
  }

  /** Validate the displayed hotel address against the API address model. */
  async validateHotelAddress(expectedHotelAddress: HotelAddressByHotelInformation): Promise<void> {
    console.log('Validate hotel address against hotel information');
    await expect(this.hotelAddressLabelsList.first(), 'Hotel address').toBeVisible();
    const actualAddress = (await this.hotelAddressLabelsList.allInnerTexts()).join(', ');
    const expectedAddress = await expectedHotelAddress.getFullAddress(expectedHotelAddress.country);
    expect(actualAddress, 'Hotel address').toBe(expectedAddress);
  }

  /** Validate hotel information against the booking summary. */
  async validateData(hotelInformation: HotelInformation): Promise<void> {
    console.log(`Validate booking summary hotel information: ${JSON.stringify(hotelInformation)}`);
    await this.validateHotelNameAndAddress(hotelInformation);
  }
}
