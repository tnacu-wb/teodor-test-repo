import { expect, type Locator, type Page } from '@playwright/test';
import { Strings } from '@test-data/strings';

/** The Late check-out section on the Ancillaries page. */
export class LcoSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly mealHeading: Locator = this.page.locator('h1[data-testid="AncillariesPage-MealsHeading"]');
  readonly roomTabsList: Locator = this.page.locator('[role="tablist"] button');
  readonly addExtrasLabel: Locator = this.page.locator('p[data-testid="ExtrasSection-Heading-Title"]');
  readonly lcoThumbnailImage: Locator = this.page.locator('div[data-testid="Extras-Item-Image-Late-check-out"] img, div[data-testid="Extras-Item-Image-Late-checkout"] img, div[data-testid="Extras-Item-Image-Später-Check-out"] img');
  readonly lcoTitleLabel: Locator = this.page.locator('p[data-testid="Extras-Item-Title-Late-check-out"], p[data-testid="Extras-Item-allRooms-Title-Late-checkout"], p[data-testid="Extras-Item-Title-Später-Check-out"]');
  readonly addRemoveLCOButton: Locator = this.page.locator('button[data-testid="Extras-Item-ButtonAdd-Late-check-out"], button[data-testid*="Extras-Item-ButtonAdd-Late-"], button[data-testid="Extras-Item-ButtonAdd-Später-Check-out"]');

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

  /** Click the Late check-out add/remove button. */
  async clickAddRemoveLCOButton(): Promise<void> {
    console.log('Click late check-out add/remove button');
    await this.addRemoveLCOButton.scrollIntoViewIfNeeded();
    await this.addRemoveLCOButton.click();
  }

  /** @returns The Late check-out title currently displayed. */
  async getLcoTitleLabel(): Promise<string> { return this.lcoTitleLabel.innerText(); }

  // ######## UI validations ########

  /**
   * Validate that the requested room tab is selected.
   * @param roomNumber - Zero-based selected room index.
   */
  async validateRoomIsIntoFocus({ roomNumber }: { roomNumber: number }): Promise<void> {
    await expect(this.mealHeading, 'Meals heading before checking selected room').toBeVisible();
    await expect(this.roomTabsList.nth(roomNumber), `Room ${roomNumber + 1} tab should be selected`).toHaveAttribute('aria-selected', 'true');
  }

  /**
   * Validate the Late check-out button displays its expected add or remove label.
   * @param isPressed - Whether Late check-out is currently selected.
   */
  async validateAddRemoveLCOButton(isPressed: boolean): Promise<void> {
    const expectedLabel = isPressed ? await Strings.REMOVE_ANCILLARIES_BUTTON.name : await Strings.ADD_ANCILLARIES_BUTTON.name;
    await expect(this.addRemoveLCOButton, 'Late check-out add/remove button label').toContainText(expectedLabel);
  }

  /**
   * Validate the visibility of the Late check-out control, title, and image.
   * @param isDisplayed - Whether the section is expected to be displayed.
   */
  async validateLcoSectionIsDisplayed(isDisplayed = true): Promise<void> {
    if (isDisplayed) {
      await expect(this.addRemoveLCOButton, 'Late check-out add/remove button').toBeVisible();
      await expect(this.lcoTitleLabel, 'Late check-out title').toBeVisible();
      await expect(this.lcoThumbnailImage, 'Late check-out thumbnail image').toBeVisible();
    } else {
      await expect(this.addRemoveLCOButton, 'Late check-out add/remove button').not.toBeVisible();
      await expect(this.lcoTitleLabel, 'Late check-out title').not.toBeVisible();
      await expect(this.lcoThumbnailImage, 'Late check-out thumbnail image').not.toBeVisible();
    }
  }
}