import { type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';
import { Constants } from '@test-data/constants';
import { type Room } from '@test-data/room';
import { BasePage } from './base.page';
import { AccessibleRoomBathroomSectionComponent } from '../../components/pi/chooseYourBathroom/accessibleRoomBathroomSection.component';
import { NonAccessibleRoomBathroomSectionComponent } from '../../components/pi/chooseYourBathroom/nonAccessibleRoomBathroomSection.component';
import { AccessibleGallerySectionComponent } from '../../components/pi/chooseYourBathroom/accessibleGallerySection.component';

/**
 * 'Choose your bathroom' page of the Premier Inn web application containing the UI elements,
 * custom actions and validations. Shown for accessible ('DIS') rooms so the guest can pick
 * a lowered-bath or wet-room bathroom configuration.
 * Mirrors qa/reference `chooseYourBathroom.page.js`.
 */
export class ChooseYourBathroomPage extends BasePage {
  // ######## properties ########

  private static containerListSelector(): string {
    return `[data-testid="ChooseBathroomPage-PageContent"] > div:nth-child(${Constants.BROWSER_RESOLUTIONS.isDesktop() ? 1 : 2}) > div`;
  }

  // ######## UI elements/properties ########

  readonly bathroomOptionsContainer: Locator = this.page.locator(
    `${ChooseYourBathroomPage.containerListSelector()} > div[data-testid="accessible-bathroom-dropdown"]`
  );
  readonly chooseYourBathroomTitleLabel: Locator = this.page.locator('h1[data-testid="accessible-title"]');
  readonly continueButton: Locator = this.page.locator('button[data-testid="ChooseBathroomPage-ContinueButton"]');
  readonly basketContinueButton: Locator = Constants.BROWSER_RESOLUTIONS.isDesktop()
    ? this.page.locator('button[data-testid="hdp_basketBookNowButton"]')
    : this.page.locator('button[data-testid="hdp_mobileBasketBookNowButton"]');
  readonly backToHotelDetailsButton: Locator = this.page.locator('[data-testid="ChooseBathroomPage-BackToHDPButton"]');
  readonly backToHotelDetailsLabel: Locator = this.backToHotelDetailsButton.locator('p');
  readonly backToHotelDetailsIcon: Locator = this.backToHotelDetailsButton.locator('div');
  readonly loweredBathRoomRadioButton: Locator = this.page.locator('div[data-testid="radio-box-wrapper_low-bath"] label');
  readonly wetRoomRadioButton: Locator = this.page.locator('div[data-testid="radio-box-wrapper_wet-room"] label');

  /** Get the list of accessible/non-accessible room bathroom section components. */
  async getRoomContainerList(
    rooms: Room[]
  ): Promise<Array<AccessibleRoomBathroomSectionComponent | NonAccessibleRoomBathroomSectionComponent>> {
    console.log(`Get bathroom room containers for ${rooms.length} room(s)`);
    const containers = this.page.locator(ChooseYourBathroomPage.containerListSelector());
    const roomElementsCount = await containers.count();
    expect(roomElementsCount, 'Room container count should match search criteria rooms').toBeGreaterThanOrEqual(rooms.length);

    return rooms.map((room, index) =>
      room.roomType.id === 'DIS'
        ? new AccessibleRoomBathroomSectionComponent(containers.nth(index))
        : new NonAccessibleRoomBathroomSectionComponent(containers.nth(index))
    );
  }

  // UI components

  readonly accessibleGallerySection: AccessibleGallerySectionComponent = new AccessibleGallerySectionComponent();

  // ######## UI actions/navigation ########

  /** Bring the gallery section into focus. */
  async bringGallerySectionIntoFocus(): Promise<void> {
    console.log('Bring bathroom gallery section into focus');
    await this.accessibleGallerySection.sectionContainer.scrollIntoViewIfNeeded();
  }

  /** Click the 'Lower bath room' radio button. */
  async clickLoweredBathRoomRadioButton(): Promise<void> {
    console.log("Click on the 'Lower bath room' button");
    await this.loweredBathRoomRadioButton.scrollIntoViewIfNeeded();
    await this.loweredBathRoomRadioButton.click();
  }

  /** Click the 'Wet room' radio button. */
  async clickWetRoomRadioButton(): Promise<void> {
    console.log("Click on the 'Wet room' button");
    await this.wetRoomRadioButton.scrollIntoViewIfNeeded();
    await this.wetRoomRadioButton.click();
  }

  /** Select the lowered-bath accessible bathroom option when the bathroom page is shown. */
  async selectLoweredBathAccessibleBathroomIfDisplayed({ rooms }: { rooms: Room[] }): Promise<void> {
    console.log('Select lowered bath accessible bathroom if displayed');
    const isShown = await this.chooseYourBathroomTitleLabel
      .waitFor({ state: 'visible', timeout: 5000 })
      .then(() => true)
      .catch(() => false);
    if (isShown) {
      await this.validatePage();
      await this.clickLoweredBathRoomRadioButton();
      await this.validateRooms(rooms);
      await this.clickContinueButton();
    }
  }

  /** Check if the Choose Bathroom page is displayed and click continue. */
  async clickContinueIfChooseBathroomPageIsDisplayed(): Promise<void> {
    console.log('Continue if Choose Your Bathroom page is displayed');
    const isShown = await this.chooseYourBathroomTitleLabel
      .waitFor({ state: 'visible', timeout: 4000 })
      .then(() => true)
      .catch(() => false);
    if (isShown) {
      await this.clickContinueButton();
    }
  }

  /** Click the 'Continue' button from the bottom left of the 'Choose Bathroom' page. */
  async clickContinueButton(): Promise<void> {
    console.log("Click on the 'Continue' button (from bottom left of the 'Choose Bathroom' page)");
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

  /** Validate the Choose Your Bathroom page has loaded. */
  async validatePage(): Promise<void> {
    console.log('Validating Choose Your Bathroom page loaded');
    await expect(this.chooseYourBathroomTitleLabel, 'Choose Your Bathroom page title').toBeVisible({ timeout: 30000 });
  }

  /** Validate the room containers against the search criteria rooms. */
  async validateRooms(searchCriteriaRooms: Room[]): Promise<void> {
    console.log(`Validate ${searchCriteriaRooms.length} choose-bathroom room(s)`);
    const roomContainers = await this.getRoomContainerList(searchCriteriaRooms);
    expect(roomContainers.length, 'Room count must be greater than 0').toBeGreaterThan(0);

    for (const [index, item] of roomContainers.entries()) {
      await item.validateData(searchCriteriaRooms[index]);
    }
  }

  /** Validate the basket Continue button. */
  async validateBasketContinueButton(): Promise<void> {
    console.log('Validate the basket Continue button');
    await expect(this.basketContinueButton, 'Basket Continue button').toBeEnabled();
    await expect(this.basketContinueButton, 'Basket Continue button label').toHaveText(await Strings.CONTINUE.name);
  }

  /** Validate the Continue button under the bathroom options. */
  async validateContinueButton(): Promise<void> {
    console.log('Validate the Continue button under the bathroom options');
    await expect(this.continueButton, 'Continue button').toBeEnabled();
    await expect(this.continueButton, 'Continue button label').toHaveText(await Strings.CONTINUE.name);
  }
}
