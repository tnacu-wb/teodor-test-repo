import { type Locator, expect } from '@playwright/test';

/**
 * One radio option within a room section representing twin rooms.
 * Mirrors qa/reference `components/opera/chooseYourRoomType/twinRoomRadioOption.js`.
 */
export class TwinRoomRadioOptionComponent {
  private readonly container: Locator;

  constructor(container: Locator) {
    this.container = container;
  }

  // ######## UI elements/properties ########

  get radioButton(): Locator {
    return this.container.locator('label:has(input.chakra-radio__input)');
  }

  get roomTypeLabel(): Locator {
    return this.container.locator('span > div > p');
  }

  get roomTypeDescription(): Locator {
    return this.container.locator('span > div > div');
  }

  async isOptionSelected(): Promise<boolean> {
    return (await this.radioButton.getAttribute('data-checked')) !== null;
  }

  // ######## UI actions/navigation ########

  // ######## UI validations ########

  /** Validate data from title and description. */
  async validateData({ title, description, isSelected }: { title: string; description: string; isSelected: boolean }): Promise<void> {
    console.log('Validate title and description');

    await expect(this.radioButton, 'Radio button visibility').toBeVisible();
    await expect(this.roomTypeLabel, 'Room type label').toContainText(title);
    await expect(this.roomTypeDescription, 'Room type description').toContainText(description);
    if (isSelected) {
      await expect(this.radioButton, `${title} radio option`).toHaveAttribute('data-checked', '');
    } else {
      await expect(this.radioButton, `${title} radio option`).not.toHaveAttribute('data-checked', '');
    }
  }
}
