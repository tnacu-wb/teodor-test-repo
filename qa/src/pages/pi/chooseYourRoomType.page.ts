import { type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';
import { Constants } from '@test-data/constants';
import { type Room } from '@test-data/room';
import { BasePage } from '../shared/base.page';
import { TwinRoomSectionComponent } from '../../components/pi/chooseYourRoomType/twinRoomSection.component';
import { NonTwinRoomSectionComponent } from '../../components/pi/chooseYourRoomType/nonTwinRoomSection.component';

/**
 * 'Choose room type' page of the Premier Inn web application containing the UI elements,
 * custom actions and validations. Shown for twin ('TWIN') rooms so the guest can pick
 * a two-single-beds or double-bed-and-sofa-bed configuration.
 * Mirrors qa/reference `chooseYourRoomType.page.js`.
 */
export class ChooseYourRoomTypePage extends BasePage {
  // ######## properties ########

  private static containerListSelector(): string {
    return `[data-testid="ChooseTwinroomPage-PageContent"] > div:nth-child(${Constants.BROWSER_RESOLUTIONS.isDesktop() ? 1 : 2}) > div`;
  }

  // ######## UI elements/properties ########

  readonly chooseYourRoomTypeTitleLabel: Locator = this.page.locator(
    'h1[data-testid="twin-title"], h1[data-testid="choose-roomtype-title"]'
  );
  readonly continueButton: Locator = this.page.locator(
    'button[data-testid="ChooseTwinroomPage-ContinueButton"], button[data-testid="hdp_basketBookNowButton"]'
  );
  readonly basketContinueButton: Locator = Constants.BROWSER_RESOLUTIONS.isDesktop()
    ? this.page.locator('button[data-testid="hdp_basketBookNowButton"]')
    : this.page.locator('button[data-testid="hdp_mobileBasketBookNowButton"]');
  readonly twinTwoSingleBedsRadioButton: Locator = this.page.locator('div[data-testid="radio-box-wrapper_improved-twin"] label');
  readonly twinTwoSingleBedsPriceLabel: Locator = this.page.locator('p[data-testid="improvedTwin-price"]');
  readonly twinDoubleBedAndSofaBedRadioButton: Locator = this.page.locator('div[data-testid="radio-box-wrapper_sofa-double-twin"] label');
  readonly twinDoubleBedAndSofaBedPriceLabel: Locator = this.page.locator('p[data-testid="double-sofa-twin-price"]');
  readonly backToHotelDetailsButton: Locator = this.page.locator('[data-testid="ChooseTwinroomPage-BackToHDPButton"]');

  /** Get the list of twin/non-twin room section components. */
  async getRoomContainerList(rooms: Room[]): Promise<Array<TwinRoomSectionComponent | NonTwinRoomSectionComponent>> {
    console.log(`Get room containers for ${rooms.length} room(s)`);
    const containers = this.page.locator(ChooseYourRoomTypePage.containerListSelector());
    const roomElementsCount = await containers.count();
    expect(roomElementsCount, 'Room container count should match search criteria rooms').toBeGreaterThanOrEqual(rooms.length);

    return rooms.map((room, index) =>
      room.roomType.id === 'TWIN'
        ? new TwinRoomSectionComponent(containers.nth(index))
        : new NonTwinRoomSectionComponent(containers.nth(index))
    );
  }

  // ######## UI actions/navigation ########

  /** Click the 'Twin - two single beds' radio button. */
  async clickTwinTwoSingleBedsRadioButton(): Promise<void> {
    console.log("Click on the 'Twin - Two single beds' button");
    await this.twinTwoSingleBedsRadioButton.scrollIntoViewIfNeeded();
    await this.twinTwoSingleBedsRadioButton.click();
  }

  /** Click the 'Twin - double bed and sofa' radio button. */
  async clickTwinDoubleBedAndSofaBedRadioButton(): Promise<void> {
    console.log("Click on the 'Twin - Double bed and sofa bed' button");
    await this.twinDoubleBedAndSofaBedRadioButton.scrollIntoViewIfNeeded();
    await this.twinDoubleBedAndSofaBedRadioButton.click();
  }

  /** Click the specific twin room radio button matching the given label. */
  async clickSpecificTwinRoomByLabel(twinRoomLabel: string): Promise<void> {
    console.log(`Select twin room configuration: ${twinRoomLabel}`);
    if (twinRoomLabel === (await Strings.TWIN_TWO_SINGLE_BEDS.name)) {
      await this.clickTwinTwoSingleBedsRadioButton();
    } else {
      await this.clickTwinDoubleBedAndSofaBedRadioButton();
    }
  }

  /** Check if the Choose Twin Room page is displayed and click continue. */
  async clickContinueIfChooseYourRoomTypePageIsDisplayed(): Promise<void> {
    console.log('Continue if Choose Your Room Type page is displayed');
    const isShown = await this.chooseYourRoomTypeTitleLabel
      .waitFor({ state: 'visible', timeout: 3000 })
      .then(() => true)
      .catch(() => false);
    if (isShown) {
      await this.clickContinueButton();
    }
  }

  /** Click the 'Continue' button from the bottom left of the 'Choose Twin Room' page. */
  async clickContinueButton(): Promise<void> {
    console.log("Click on the 'Continue' button (from bottom left of the 'Choose Twin Room' page)");
    await this.continueButton.scrollIntoViewIfNeeded();
    await this.continueButton.click();
  }

  /** Click the 'Continue' button from the basket. */
  async clickBasketContinueButton(): Promise<void> {
    console.log("Click on the 'Continue' button (from the basket)");
    await this.basketContinueButton.scrollIntoViewIfNeeded();
    await this.basketContinueButton.click();
  }

  /** Click the 'Back to hotel details' button. */
  async clickBackToHotelDetailsButton(): Promise<void> {
    console.log("Click on the 'Back to hotel details' button");
    await this.backToHotelDetailsButton.scrollIntoViewIfNeeded();
    await this.backToHotelDetailsButton.click();
  }

  // ######## UI validations ########

  /** Validate the Choose Room Type page has loaded. */
  async validatePage(): Promise<void> {
    console.log('Validating Choose Room Type page loaded');
    await expect(this.chooseYourRoomTypeTitleLabel, 'Choose Room Type page title').toBeVisible({ timeout: 30000 });
  }

  /** Validate the room containers against the search criteria rooms. */
  async validateRooms(searchCriteriaRooms: Room[]): Promise<void> {
    console.log(`Validate ${searchCriteriaRooms.length} choose-room-type room(s)`);
    const roomContainers = await this.getRoomContainerList(searchCriteriaRooms);
    expect(roomContainers.length, 'Room count must be greater than 0').toBeGreaterThan(0);

    for (const [index, item] of roomContainers.entries()) {
      await item.validateData(searchCriteriaRooms[index]);
    }
  }
}
