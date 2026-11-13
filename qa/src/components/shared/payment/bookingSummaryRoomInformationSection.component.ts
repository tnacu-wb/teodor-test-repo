import { type Page, type Locator, expect } from '@playwright/test';
import { Constants } from '@test-data/constants';
import { Strings } from '@test-data/strings';
import { ApiHelpers } from '@api/apiHelpers';
import { type BookingInformation } from '@api/response/bookingInformation';

/**
 * The booking summary room information section present on the Payment page vertical strip
 * containing the UI elements, custom actions and validations. Mirrors qa/reference
 * `components/common/payment/bookingSummaryRoomInformationSection.js`.
 */
export class PaymentBookingSummaryRoomInformationSectionComponent {
  private readonly page: Page = global.page;

  private get resolutionId(): string {
    return Constants.RESOLUTION_IDENTIFIER_FOR_DATATESTID_ATTRIBUTE;
  }

  // ######## UI elements/properties ########

  get roomPanelsList(): Locator {
    return this.page.locator(`div[data-testid="BookingSummary-${this.resolutionId}-RoomInformation-Wrapper"] > div`);
  }

  /** Room type/name label for the room panel at the given 1-based index. */
  getRoomTypeByIndex(index: number): Locator {
    return this.roomPanelsList.nth(index - 1).locator(`span[data-testid="BookingSummary-${this.resolutionId}-RoomInformation-RoomName"]`);
  }

  /** Children count label for the room panel at the given 1-based index. */
  getRoomChildrenByIndex(index: number): Locator {
    return this.roomPanelsList.nth(index - 1).locator(`p[data-testid="BookingSummary-${this.resolutionId}-RoomInformation-ChildrenNumber"]`);
  }

  /** Adults count label for the room panel at the given 1-based index. */
  getRoomAdultsByIndex(index: number): Locator {
    return this.roomPanelsList.nth(index - 1).locator(`p[data-testid="BookingSummary-${this.resolutionId}-RoomInformation-AdultsNumber"]`);
  }

  getNoMealsSelectedLabelByIndex(index: number): Locator { return this.roomPanelsList.nth(index - 1).locator(`p[data-testid="BookingSummary-${this.resolutionId}-RoomInformation-NoMealsSelected"]`); }
  getFoodOptionsListByIndex(index: number): Locator { return this.roomPanelsList.nth(index - 1).locator('p[data-testid*="Meal"]:not([data-testid*="NoMealsSelected"])'); }
  getNoExtrasSelectedLabelByIndex(index: number): Locator { return this.roomPanelsList.nth(index - 1).locator(`p[data-testid="BookingSummary-${this.resolutionId}-RoomInformation-NoExtrasSelected"]`); }
  getRoomEciLabelsByIndex(index: number): Locator { return this.roomPanelsList.nth(index - 1).locator(`p[data-testid="BookingSummary-${this.resolutionId}-RoomInformation-EARLY_CHECK_IN"]`); }
  getRoomLcoLabelsByIndex(index: number): Locator { return this.roomPanelsList.nth(index - 1).locator(`p[data-testid="BookingSummary-${this.resolutionId}-RoomInformation-LATE_CHECK_OUT"]`); }
  getRoomWifiLabelsByIndex(index: number): Locator { return this.roomPanelsList.nth(index - 1).locator(`p[data-testid="BookingSummary-${this.resolutionId}-RoomInformation-WIFI"]`); }

  // ######## UI actions/navigation ########

  // ######## UI validations ########

  /** Validate the number of room panels matches the expected room count. */
  async validateRoomCount(expectedRoomsCount: number): Promise<void> {
    console.log(`Validate payment booking summary room count: ${expectedRoomsCount}`);
    await expect(this.roomPanelsList, 'Payment booking summary room panels count').toHaveCount(expectedRoomsCount);
  }

  /** Retrieve room details currently displayed in the booking summary. */
  async getRoomPanelDetailsBasedOnUi(): Promise<Array<{ type: string; adultsNumber: string; childrenNumber: string; meals: string[] }>> {
    console.log('Retrieve room details from Booking summary panel');
    const details: Array<{ type: string; adultsNumber: string; childrenNumber: string; meals: string[] }> = [];
    for (let index = 1; index <= await this.roomPanelsList.count(); index++) {
      const children = this.getRoomChildrenByIndex(index);
      const noMeals = this.getNoMealsSelectedLabelByIndex(index);
      const meals = await noMeals.isVisible() ? [await noMeals.innerText()] : (await this.getFoodOptionsListByIndex(index).allInnerTexts()).sort();
      details.push({ type: (await this.getRoomTypeByIndex(index).innerText()).replace(/[()]/g, ''), adultsNumber: await this.getRoomAdultsByIndex(index).innerText(), childrenNumber: await children.isVisible() ? await children.innerText() : '', meals });
    }
    return details;
  }

  /** Validate room panels against booking-information data. */
  async validateRoomPanelList(bookingInformation: BookingInformation, basketReferenceId: string): Promise<void> {
    console.log('Validate room panel list from Booking summary panel');
    const expected = await ApiHelpers.getRoomPanelDetailsBasedOnApi(bookingInformation, basketReferenceId);
    await this.validateRoomCount(expected.length);
    const actual = await this.getRoomPanelDetailsBasedOnUi();
    expect(actual.length, 'Room detail list size').toBeGreaterThan(0);
    for (let index = 0; index < expected.length; index++) {
      expect(actual[index].adultsNumber, `Room ${index + 1} adults`).toBe(expected[index].adultsNumber);
      expect(actual[index].childrenNumber, `Room ${index + 1} children`).toBe(expected[index].childrenNumber);
      expect(actual[index].meals, `Room ${index + 1} meals`).toEqual(expected[index].meals);
    }
  }

  /** Validate room information against booking-information data. */
  async validateData(bookingInformation: BookingInformation, basketReferenceId: string): Promise<void> {
    console.log(`Validate booking summary room information: ${JSON.stringify(bookingInformation)}`);
    await this.validateRoomPanelList(bookingInformation, basketReferenceId);
  }

  /** Validate early check-in and late check-out selections for one room. */
  async validateAddedRemovedEciLcoLabelPerRoom({ roomNumber, isEciSelected, isLcoSelected }: { roomNumber: number; isEciSelected: boolean; isLcoSelected: boolean }): Promise<void> {
    console.log(`Validate ECI/LCO selection for room ${roomNumber + 1}`);
    if (isEciSelected) await expect(this.getRoomEciLabelsByIndex(roomNumber + 1), 'Early check-in label').toHaveCount(1);
    if (isLcoSelected) await expect(this.getRoomLcoLabelsByIndex(roomNumber + 1), 'Late check-out label').toHaveCount(1);
    if (!isEciSelected && !isLcoSelected) await expect(this.getNoExtrasSelectedLabelByIndex(roomNumber + 1), 'No extras selected label').toContainText(await Strings.NO_EXTRAS_SELECTED.name);
  }

  /** Validate Wi-Fi selection for one room. */
  async validateAddedRemovedWifiLabelPerRoom({ roomNumber, isWifiSelected }: { roomNumber: number; isWifiSelected: boolean }): Promise<void> {
    console.log(`Validate Wi-Fi selection for room ${roomNumber + 1}`);
    if (isWifiSelected) await expect(this.getRoomWifiLabelsByIndex(roomNumber + 1), 'Wi-Fi label').toHaveCount(1);
    else await expect(this.getNoExtrasSelectedLabelByIndex(roomNumber + 1), 'No extras selected label').toContainText(await Strings.NO_EXTRAS_SELECTED.name);
  }
}
