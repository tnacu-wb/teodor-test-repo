import { expect, type Locator } from '@playwright/test';
import { Locales } from '@test-data/locales';
import { Strings } from '@test-data/strings';
import { CcuiComponent } from '../baseCcui.component';

/**
 * Early check-in all-rooms section on the CCUI ancillaries page.
 * Mirrors qa/reference/test/pages/components/ccui/ancillaries/eciAllRoomsSection.js.
 */
export class EciAllRoomsSectionComponent extends CcuiComponent {
  // ######## UI elements/properties ########

  readonly eciAllRoomsButton: Locator = this.page.locator('button[data-testid^="Extras-Item-allRooms-ButtonAdd-"]').first();
  readonly eciAllRoomsWrapperContainer: Locator = this.page.locator('div[data-testid^="Extras-Item-allRooms-Wrapper-"]').first();
  readonly eciAllRoomsTitleLabel: Locator = this.page.locator('p[data-testid^="Extras-Item-allRooms-Title-"]').first();
  readonly eciAllRoomsThumbnailImage: Locator = this.page.locator('div[data-testid^="Extras-Item-allRooms-Image-"] img').first();
  readonly eciAllRoomsNotificationMessageLowInventoryContainer: Locator = this.eciAllRoomsWrapperContainer.locator(`xpath=${Locales.isEnglishWebsite() ? 'preceding-sibling::div[1]' : 'preceding::div[2][not(contains(@data-testid, "Extras-Item-allRooms-Wrapper-"))]'}`);
  readonly eciAllRoomsNotificationMessageNoInventoryContainer: Locator = this.eciAllRoomsWrapperContainer.locator('xpath=preceding-sibling::div[1][not(contains(@data-testid, "Extras-Item-allRooms-Wrapper-"))]');

  // ######## UI actions/navigation ########

  /** Click Add/Remove All Rooms ECI button. */
  async clickAddRemoveAllRoomsEciButton(): Promise<void> {
    console.log('Click Add/Remove All Rooms ECI button');
    await this.eciAllRoomsButton.scrollIntoViewIfNeeded();
    await this.eciAllRoomsButton.click();
  }

  // ######## UI validations ########

  /**
   * Validate ECI section is displayed for all rooms.
   * @param isDisplayed Whether the ECI section should be displayed.
   */
  async validateEciSectionIsDisplayedAllRooms(isDisplayed = true): Promise<void> {
    console.log('Validate ECI section is displayed for all rooms');
    await this.validateDisplayState(this.eciAllRoomsButton, 'Add/Remove Early check-in button for all rooms', isDisplayed);
    await this.validateDisplayState(this.eciAllRoomsTitleLabel, 'Early check-in title for all rooms', isDisplayed);
    await this.validateDisplayState(this.eciAllRoomsThumbnailImage, 'Early check-in thumbnail image for all rooms', isDisplayed);
  }
  /**
   * Validate All Rooms Add/Remove ECI button state.
   * @param isPressed Whether the ECI button should have its pressed state set.
   */
  async validateAllRoomsAddRemoveEciButton({ isPressed = true }: { isPressed?: boolean } = {}): Promise<void> {
    console.log('Validate Add/Remove Early check-in button all rooms');
    const expectedLabel = isPressed ? await Strings.REMOVE_ANCILLARIES_BUTTON.name : await Strings.ADD_ANCILLARIES_BUTTON.name;
    await expect(this.eciAllRoomsButton, 'All rooms ECI add/remove button label').toContainText(expectedLabel);
  }
  /**
   * Validate ECI all rooms notification is displayed.
   * @param isDisplayed Whether an ECI inventory notification should be displayed.
   */
  async validateEciAllRoomsNotificationIsDisplayed(isDisplayed = true): Promise<void> {
    console.log('Validate ECI all rooms notifications are displayed');
    await this.validateDisplayState(this.eciAllRoomsNotificationMessageNoInventoryContainer, 'ECI no-inventory notification', isDisplayed);
    await this.validateDisplayState(this.eciAllRoomsNotificationMessageLowInventoryContainer.filter({ hasText: /\S/ }), 'ECI low-inventory notification', isDisplayed);
  }
}