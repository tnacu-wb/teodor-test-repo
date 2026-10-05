import { type Page, type Locator, expect } from '@playwright/test';

/**
 * The individual (breakfast) child meal container within the Amend Booking meal-selection flow
 * containing the UI elements, custom actions and validations. Mirrors qa/reference
 * `components/common/amendBooking/amendChildMealContainer.js` (extends
 * `components/opera/ancillaries/childMealContainer.js`; simplified: reference scopes to the
 * breakfast-specific container via a text-content XPath search for "breakfast"/"Frühstück" -
 * callers should pass that scoped `Locator` in directly rather than relying on hardcoded text).
 */
export class AmendChildMealContainerComponent {
  private readonly container: Locator;

  /** Create a child meal container scoped to its meal-item wrapper. */
  constructor(container: Locator) {
    this.container = container;
  }

  // ######## UI elements/properties ########

  /** Return the child meal title locator. */
  get mealTitleLabel(): Locator {
    return this.container.locator('h4[data-testid="RoomsMealSelection-Meals-Children-MealItem-Title"]');
  }

  /** Return the child meal thumbnail locator. */
  get mealThumbnailImage(): Locator {
    return this.container.locator('div[data-testid="RoomsMealSelection-Meals-Children-MealItem-Image-Wrapper"] img');
  }

  /** Return the child meal description locator. */
  get mealDescriptionLabel(): Locator {
    return this.container.locator('div[data-testid="RoomsMealSelection-Meals-Children-MealItem-Description"]');
  }

  /** Return the add-meal button locator. */
  get addMealButton(): Locator {
    return this.container.locator('button[data-testid="RoomsMealSelection-Meals-Children-MealItem-AddSubtractControls-AddButton"]');
  }

  /** Return the remove-meal button locator. */
  get removeMealButton(): Locator {
    return this.container.locator('button[data-testid="RoomsMealSelection-Meals-Children-MealItem-AddSubtractControls-SubtractButton"]');
  }

  /** Return the selected child meal count locator. */
  get mealValueLabel(): Locator {
    return this.container.locator('p[data-testid="RoomsMealSelection-Meals-Children-MealItem-AddSubtractControls-Value"]');
  }

  // ######## UI actions/navigation ########

  /** Click the add-meal button. */
  async clickAddMealButton(): Promise<void> {
    console.log('Click add child meal button');
    await this.addMealButton.scrollIntoViewIfNeeded();
    await this.addMealButton.click();
  }

  /** Click the remove-meal button. */
  async clickRemoveMealButton(): Promise<void> {
    console.log('Click remove child meal button');
    await this.removeMealButton.click();
  }

  // ######## UI validations ########

  /** Validate this child meal container's title is displayed. */
  async validateData(): Promise<void> {
    console.log('Validate child meal container');
    await expect(this.mealTitleLabel, 'Amend child meal title').toBeVisible();
  }

  /** Validate the selected child meal count. */
  async validateMealValue(expectedValue: number): Promise<void> {
    console.log(`Validate child meal value=${expectedValue}`);
    await expect(this.mealValueLabel, 'Child meal value').toHaveText(String(expectedValue));
  }

  /** Validate add and remove child meal button state. */
  async validateButtons(addEnabled: boolean, removeEnabled: boolean): Promise<void> {
    console.log(`Validate child meal buttons: add=${addEnabled}, remove=${removeEnabled}`);
    if (addEnabled) await expect(this.addMealButton, 'Add child meal button').toBeEnabled();
    else await expect(this.addMealButton, 'Add child meal button').toBeDisabled();
    if (removeEnabled) await expect(this.removeMealButton, 'Remove child meal button').toBeEnabled();
    else await expect(this.removeMealButton, 'Remove child meal button').toBeDisabled();
  }
}
