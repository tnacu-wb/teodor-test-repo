import { expect, type Locator, type Page } from '@playwright/test';
import { Strings } from '@test-data/strings';

/** The Early check-in section on the Ancillaries page. */
export class EciSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly mealHeading: Locator = this.page.locator('h1[data-testid="AncillariesPage-MealsHeading"]');
  readonly roomTabsList: Locator = this.page.locator('[role="tablist"] button');
  readonly eciButton: Locator = this.page.locator('button[data-testid*="Extras-Item-ButtonAdd-Early-"], button[data-testid*="Extras-Item-ButtonAdd-Früher-Check-in"]');
  readonly eciTitleLabel: Locator = this.page.locator('p[data-testid="Extras-Item-Title-Early-check-in"], p[data-testid="Extras-Item-Title-Früher-Check-in"]');
  readonly eciThumbnailImage: Locator = this.page.locator('div[data-testid="Extras-Item-Image-Early-check-in"] img, div[data-testid="Extras-Item-Image-Früher-Check-in"] img');

  // ######## UI actions/navigation ########

  /**
   * Select the tab for a room in a multi-room ancillaries booking.
   * @param roomIndex - Zero-based room index.
   */
  async clickRoomTabButton({ roomIndex }: { roomIndex: number }): Promise<void> {
    const roomTab = this.roomTabsList.nth(roomIndex);
    console.log(`Open room ${roomIndex + 1} tab`);
    await roomTab.scrollIntoViewIfNeeded();
    await roomTab.click();
    await this.validateRoomIsIntoFocus({ roomNumber: roomIndex });
  }

  /** Click the Early check-in add/remove button. */
  async clickAddRemoveEciButton(): Promise<void> {
    console.log('Click early check-in add/remove button');
    await this.eciButton.scrollIntoViewIfNeeded();
    await this.eciButton.click();
  }

  // ######## UI validations ########

  /**
   * Validate that one room tab is selected and all other room tabs are unselected.
   * @param roomNumber - Zero-based selected room index.
   */
  async validateRoomIsIntoFocus({ roomNumber }: { roomNumber: number }): Promise<void> {
    await expect(this.mealHeading, 'Meals heading before checking selected room').toBeVisible();
    await expect(this.roomTabsList.nth(roomNumber), `Room ${roomNumber + 1} tab should be selected`).toHaveAttribute('aria-selected', 'true');
    const roomCount = await this.roomTabsList.count();
    for (let index = 0; index < roomCount; index++) {
      if (index !== roomNumber) await expect(this.roomTabsList.nth(index), `Room ${index + 1} tab should not be selected`).toHaveAttribute('aria-selected', 'false');
    }
  }

  /**
   * Validate the Early check-in button displays its expected add or remove label.
   * @param isPressed - Whether Early check-in is currently selected.
   */
  async validateAddRemoveEciButton(isPressed: boolean): Promise<void> {
    const expectedLabel = isPressed ? await Strings.REMOVE_ANCILLARIES_BUTTON.name : await Strings.ADD_ANCILLARIES_BUTTON.name;
    await expect(this.eciButton, 'Early check-in add/remove button label').toContainText(expectedLabel);
  }

  /**
   * Validate the visibility of the Early check-in control, title, and image.
   * @param isDisplayed - Whether the section is expected to be displayed.
   */
  async validateEciSectionIsDisplayed(isDisplayed = true): Promise<void> {
    if (isDisplayed) {
      await expect(this.eciButton, 'Early check-in add/remove button').toBeVisible();
      await expect(this.eciTitleLabel, 'Early check-in title').toBeVisible();
      await expect(this.eciThumbnailImage, 'Early check-in thumbnail image').toBeVisible();
    } else {
      await expect(this.eciButton, 'Early check-in add/remove button').not.toBeVisible();
      await expect(this.eciTitleLabel, 'Early check-in title').not.toBeVisible();
      await expect(this.eciThumbnailImage, 'Early check-in thumbnail image').not.toBeVisible();
    }
  }
}