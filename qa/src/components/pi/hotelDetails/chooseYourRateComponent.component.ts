import { type Page, type Locator, expect } from '@playwright/test';
import { HotelRates } from '@test-data/hotelRates';
import { PmsRoomTypes } from '@test-data/pmsRoomTypes';
import { RateSectionComponent } from './rateSection.component';

/**
 * The 'Choose your rate' section from the hotel details page containing the UI elements, custom
 * actions and validations. Mirrors qa/reference
 * `components/opera/hotelDetails/chooseYourRateComponent.js` (heavily simplified: the reference's
 * extensive premium/standard room-class cross-referencing against `HotelAvailability`/
 * `RoomClassConfig`/`PmsRoomType` API responses is dropped in favour of the simple DOM-scoped
 * rate/room lookups already used by `HotelDetailsPage` for its own flex-rate selection - this
 * component adds the remaining named-rate-button and per-room rate lookups that page doesn't
 * expose yet).
 */
export class ChooseYourRateComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly ratesContainer: Locator = this.page.locator('section[data-testid="hdp_rateSelector-Section"]');
  readonly chooseYourRateTitleLabel: Locator = this.page.locator('h3[data-testid="hdp_rateSelectorTitle"]');
  readonly descriptionLabel: Locator = this.page.locator('p[data-testid="hdp_rateSelectorDescription"]');
  readonly rateItemCardList: Locator = this.page.locator('div[data-testid="hdp_rateItemCard"]');
  readonly ratesTitleList: Locator = this.page.locator(
    'div[data-testid="hdp_rateItemCard"] b[data-testid="hdp_ratePlanName"], div[data-testid="hdp_rateItemCard"] span[data-testid="hdp_ratePlanName"]'
  );
  readonly ratesPriceList: Locator = this.page.locator('div[data-testid="hdp_rateItemCard"] p[data-testid="hdp_rateItemTotalPrice"]');
  readonly accessibleRoomNotification: Locator = this.page.locator('div[data-testid="hdp_accessibleRoomNotification"]');
  readonly ratesContainersList: Locator = this.page.locator('div[data-testid="hdp_rateCard"]');
  readonly showMoreRatesLabel: Locator = this.page.locator('p[data-testid="hdp_roomTypeShowMoreRatesLink"]');
  readonly employeeOfferLabel: Locator = this.page.locator('p[data-testid="hdp_employeeOffer"]');
  readonly selectedRateLabel: Locator = this.page
    .locator('div[data-testid="hdp_rateItemCard"] label[data-checked]')
    .locator('xpath=.//*[self::b or self::span][@data-testid="hdp_ratePlanName"]')
    .first();
  readonly selectedRoomLabel: Locator = this.page
    .locator('div[data-testid="hdp_rateCard"]:has(label[data-checked])')
    .locator('xpath=.//*[self::b or self::p][@data-testid="hdp_roomTypeTitle"]')
    .first();

  /** The rate-plan-name button for a given rate (e.g. `HotelRates.PI_FLEX`). */
  rateButtonByName(rateName: string): Locator {
    return this.page
      .locator('b[data-testid="hdp_ratePlanName"], span[data-testid="hdp_ratePlanName"]', { hasText: rateName })
      .first();
  }

  /** The room-type title label for a given room name. */
  roomNameLabel(roomName: string): Locator {
    const escapedRoomName = roomName.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
    return this.page.locator('b, p').filter({ hasText: new RegExp(`^\\s*${escapedRoomName}\\s*$`) }).first();
  }

  /** The rate-plan-name button for a specific rate within a specific room's rate card. */
  async rateButtonForSpecificRoom({
    roomName,
    rateName,
  }: { roomName?: string; rateName?: string } = {}): Promise<Locator> {
    const selectedRoomName = roomName ?? await PmsRoomTypes.PI_STANDARD_ROOM.name.name;
    const selectedRateName = rateName ?? HotelRates.PI_FLEX.name;
    const roomCard = this.roomNameLabel(selectedRoomName).locator('xpath=ancestor::div[@data-testid="hdp_rateCard"]');
    const escapedRateName = selectedRateName.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
    await roomCard.waitFor({ state: 'visible', timeout: 60000 });
    return roomCard
      .locator('b[data-testid="hdp_ratePlanName"], span[data-testid="hdp_ratePlanName"]')
      .filter({ hasText: new RegExp(`^\\s*${escapedRateName}\\s*$`) })
      .first();
  }

  /** Rate section components for each rate card on the page. */
  async rateSectionsList(): Promise<RateSectionComponent[]> {
    const count = await this.ratesContainersList.count();
    return Array.from({ length: count }, (_, index) => new RateSectionComponent(this.ratesContainersList.nth(index)));
  }

  /** Return the selected rate-plan name from the selected rate card. */
  async getSelectedRateName(): Promise<string> {
    console.log('Getting selected rate name');
    await this.selectedRateLabel.waitFor({ state: 'visible', timeout: 40000 });
    return (await this.selectedRateLabel.innerText()).trim();
  }

  /** Return the selected room name from the selected rate card. */
  async getSelectedRoomName(): Promise<string> {
    console.log('Getting selected room name');
    await this.selectedRoomLabel.waitFor({ state: 'visible', timeout: 40000 });
    return (await this.selectedRoomLabel.innerText()).trim();
  }

  // ######## UI actions/navigation ########

  /** Click a named rate (e.g. Flex/Standard/Advance) irrespective of room. */
  async clickRateByName(rateName: string): Promise<void> {
    console.log(`Selecting rate: ${rateName}`);
    const button = this.rateButtonByName(rateName);
    await button.scrollIntoViewIfNeeded();
    await button.click();
  }

  /** Click a specific rate for a specific room (e.g. Flex on the Double room). */
  async clickSpecificRateForSpecificRoom({
    roomName,
    rateName,
  }: { roomName?: string; rateName?: string } = {}): Promise<void> {
    const selectedRoomName = roomName ?? await PmsRoomTypes.PI_STANDARD_ROOM.name.name;
    const selectedRateName = rateName ?? HotelRates.PI_FLEX.name;
    console.log(`Selecting rate ${selectedRateName} for room ${selectedRoomName}`);
    const button = await this.rateButtonForSpecificRoom({ roomName: selectedRoomName, rateName: selectedRateName });
    await button.scrollIntoViewIfNeeded();
    await button.click();
  }

  /** Select the PI Flex rate option. */
  async clickFlexRate(): Promise<void> {
    await this.clickRateByName(await HotelRates.PI_FLEX.name);
  }

  // ######## UI validations ########

  /** Validate the rate item cards are displayed. */
  async validatePage(): Promise<void> {
    console.log('Validate choose your rate section');
    await expect(this.rateItemCardList.first(), 'Rate item cards').toBeVisible();
  }

  /** Validate at least one Flex-family rate (Flex/Semi-Flex/Advance) rate button is present. */
  async validateFlexFamilyRatesPresent(): Promise<void> {
    const flexRateNames = [HotelRates.PI_FLEX.name, HotelRates.PI_SEMI_FLEX.name, HotelRates.PI_ADVANCE.name];
    let found = false;
    for (const rateName of flexRateNames) {
      if (await this.rateButtonByName(await rateName).isVisible().catch(() => false)) {
        found = true;
        break;
      }
    }
    expect(found, 'At least one PI Flex-family rate should be visible').toBe(true);
  }
}
