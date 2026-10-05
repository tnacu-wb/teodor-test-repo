import { expect, type Locator } from '@playwright/test';
import { Strings } from '@test-data/strings';
import { CcuiComponent } from '../baseCcui.component';

/**
 * Child meal container in the CCUI entire-stay meals section.
 * Mirrors qa/reference/test/pages/components/ccui/ancillaries/entireStayChildMealContainer.js.
 */
export class EntireStayChildMealContainerComponent extends CcuiComponent {
  /**
   * Create a child-meal container scoped to the supplied meal item.
   * @param container Meal-item locator used as the component root.
   */
  constructor(private readonly container: Locator = global.page.locator('div[data-testid="AncillariesPage-EntireStay-Meals-Children-MealItem-Wrapper"]').last()) { super(); }

  // ######## UI elements/properties ########

  /** Return the entire-stay child-meal add/remove button locator. */
  get addRemoveMealButton(): Locator { return this.container.locator('button[data-testid="AncillariesPage-EntireStay-Meals-Children-MealItem-Button"]'); }
  /** Return the child-meal title locator. */
  get mealTitleLabel(): Locator { return this.container.locator('h4[data-testid="AncillariesPage-IndividualSelection-Meals-Children-MealItem-Title"]'); }
  /** Return the child-meal image wrapper locator. */
  get mealThumbnailImage(): Locator { return this.container.locator('div[data-testid="AncillariesPage-IndividualSelection-Meals-Children-MealItem-Image-Wrapper"]'); }
  /** Return the child-meal image element locator. */
  get mealImgPropImage(): Locator { return this.mealThumbnailImage.locator('img'); }
  /** Return the child-meal description locator. */
  get mealDescriptionLabel(): Locator { return this.container.locator('div[data-testid="AncillariesPage-IndividualSelection-Meals-Children-MealItem-Description"]'); }
  /** Return the children label locator from the add/subtract controls. */
  get childrenLabel(): Locator { return this.container.locator('p[data-testid="AncillariesPage-IndividualSelection-Meals-Children-MealItem-AddSubtractControls-Label"]'); }
  /** Return the list of child-meal title locators. */
  get mealsIndexLabelsList(): Locator { return this.page.locator('h4[data-testid="AncillariesPage-IndividualSelection-Meals-Children-MealItem-Title"]'); }
  /** Read the displayed child meal title. */
  async getChildMealTitle(): Promise<string> { console.log('Get child meal title'); return (await this.mealTitleLabel.textContent())?.trim() ?? ''; }

  // ######## UI actions/navigation ########

  /** Click the entire-stay child Add/Remove meal button. */
  async clickAddRemoveMealButton(): Promise<void> {
    console.log('Click Add/Remove child meal button');
    await this.addRemoveMealButton.click();
  }
  /** Click the entire-stay child Add meal button. */
  async clickAddMealButton(): Promise<void> {
    console.log('Click Add child meal button');
    await this.addRemoveMealButton.click();
  }
  /** Add the requested number of child meals. */
  async addChildMeals(numberToAdd: number): Promise<void> { console.log(`Add child meals count=${numberToAdd}`); for (let index = 0; index < numberToAdd; index += 1) await this.clickAddMealButton(); }
  /** Remove the requested number of child meals. */
  async removeChildMeals(numberToRemove: number): Promise<void> { console.log(`Remove child meals count=${numberToRemove}`); for (let index = 0; index < numberToRemove; index += 1) await this.clickAddRemoveMealButton(); }

  // ######## UI validations ########

  /**
   * Validate Add/Remove meal button state.
   * @param isPressed Whether the button should have its pressed state set.
   */
  async validateAddRemoveMealButton(isPressed: boolean): Promise<void> {
    console.log('Validate Add/Remove child meal button label');
    const expectedLabel = isPressed ? await Strings.ADD_ANCILLARIES_BUTTON.name : await Strings.REMOVE_ANCILLARIES_BUTTON.name;
    await expect(this.addRemoveMealButton, 'Add/Remove child meal button label').toContainText(expectedLabel);
  }
  /**
   * Validate Add/Remove meal button enabled state.
   * @param isEnabled Whether the button should be enabled.
   */
  async validateAddRemoveMealButtonIsEnabled(isEnabled: boolean): Promise<void> {
    console.log('Validate Add/Remove child meal button enabled state');
    await this.validateEnabledState(this.addRemoveMealButton, 'Add/Remove child meal button enabled state', isEnabled);
  }
}