import { expect, type Locator } from '@playwright/test';
import { Locales } from '@test-data/locales';
import { Strings } from '@test-data/strings';
import { CcuiComponent } from '../baseCcui.component';

/**
 * Late check-out all-rooms section on the CCUI ancillaries page.
 * Mirrors qa/reference/test/pages/components/ccui/ancillaries/lcoAllRoomsSection.js.
 */
export class LcoAllRoomsSectionComponent extends CcuiComponent {
  // ######## UI elements/properties ########

  readonly lcoAllRoomsButton: Locator = this.page.locator('button[data-testid^="Extras-Item-allRooms-ButtonAdd-"]').first();
  readonly lcoAllRoomsWrapperContainer: Locator = this.page.locator('div[data-testid^="Extras-Item-allRooms-Wrapper-"]').first();
  readonly lcoAllRoomsTitleLabel: Locator = this.page.locator('p[data-testid^="Extras-Item-allRooms-Title-"]').first();
  readonly lcoAllRoomsThumbnailImage: Locator = this.page.locator('div[data-testid^="Extras-Item-allRooms-Image-"] img').first();
  readonly lcoAllRoomsNotificationMessageLowInventoryContainer: Locator = this.lcoAllRoomsWrapperContainer.locator(`xpath=${Locales.isEnglishWebsite() ? 'preceding-sibling::div[3]' : 'preceding::div[1]'}`);
  readonly lcoAllRoomsNotificationMessageNoInventoryContainer: Locator = this.lcoAllRoomsWrapperContainer.locator('xpath=preceding-sibling::div[3]');
  readonly lcoAllRoomsDescriptionLabel: Locator = this.page.locator('div[data-testid^="Extras-Item-allRooms-Description-"]').first();
  readonly lcoAllRoomsPrice: Locator = this.page.locator('p[data-testid^="Extras-Item-allRooms-Price-"]').first();
  readonly addExtrasSectionTitle: Locator = this.page.locator('p[data-testid="ExtrasSection-allRooms-Heading-Title"]');

  // ######## UI actions/navigation ########

  /** Click Add/Remove All Rooms LCO button. */
  async clickAddRemoveAllRoomsLCOButton(): Promise<void> {
    console.log('Click Add/Remove All Rooms LCO button');
    await this.lcoAllRoomsButton.scrollIntoViewIfNeeded();
    await this.lcoAllRoomsButton.click();
  }

  // ######## UI validations ########

  /**
   * Validate LCO section is displayed for all rooms.
   * @param isDisplayed Whether the LCO section should be displayed.
   */
  async validateLcoSectionIsDisplayedAllRooms(isDisplayed = true): Promise<void> {
    console.log('Validate Late check-out section is displayed for all rooms');
    await this.validateDisplayState(this.lcoAllRoomsButton, 'Add/Remove Late check-out button for all rooms', isDisplayed);
    await this.validateDisplayState(this.lcoAllRoomsTitleLabel, 'Late check-out title for all rooms', isDisplayed);
    await this.validateDisplayState(this.lcoAllRoomsThumbnailImage, 'Late check-out thumbnail image for all rooms', isDisplayed);
  }
  /**
   * Validate All Rooms Add/Remove LCO button state.
   * @param isPressed Whether the LCO button should have its pressed state set.
   */
  async validateAllRoomsAddRemoveLCOButton({ isPressed = true }: { isPressed?: boolean } = {}): Promise<void> {
    console.log('Validate Add/Remove Late check-out button all rooms');
    const expectedLabel = isPressed ? await Strings.REMOVE_ANCILLARIES_BUTTON.name : await Strings.ADD_ANCILLARIES_BUTTON.name;
    await expect(this.lcoAllRoomsButton, 'All rooms LCO add/remove button label').toContainText(expectedLabel);
  }
  /**
   * Validate LCO all rooms notification is displayed.
   * @param isDisplayed Whether an LCO inventory notification should be displayed.
   */
  async validateLcoAllRoomsNotificationIsDisplayed(isDisplayed = true): Promise<void> {
    console.log('Validate LCO all rooms notifications are displayed');
    await this.validateDisplayState(this.lcoAllRoomsNotificationMessageNoInventoryContainer, 'LCO no-inventory notification', isDisplayed);
    await this.validateDisplayState(this.lcoAllRoomsNotificationMessageLowInventoryContainer.filter({ hasText: /\S/ }), 'LCO low-inventory notification', isDisplayed);
  }
}