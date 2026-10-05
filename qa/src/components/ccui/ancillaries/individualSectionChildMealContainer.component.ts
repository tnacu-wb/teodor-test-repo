import { expect, type Locator } from '@playwright/test';
import { Strings } from '@test-data/strings';
import { CcuiComponent } from '../baseCcui.component';

/**
 * Child meal container in the CCUI individual meals section.
 * Mirrors qa/reference/test/pages/components/ccui/ancillaries/individualSectionChildMealContainer.js.
 */
export class IndividualSectionChildMealContainerComponent extends CcuiComponent {
  /**
   * Create a child-meal container scoped to the supplied meal item.
   * @param container Meal-item locator used as the component root.
   */
  constructor(private readonly container: Locator = global.page.locator('div[data-testid="AncillariesPage-IndividualSelection-Meals-Children-MealItem-Wrapper"]').last()) { super(); }

  // ######## UI elements/properties ########

  /** Return the root container for this meal item. */
  get sectionContainer(): Locator { return this.container; }
  /** Return the child-meal title locator. */
  get mealTitleLabel(): Locator { return this.container.locator('h4[data-testid="AncillariesPage-IndividualSelection-Meals-Children-MealItem-Title"]'); }
  /** Return the child-meal image wrapper locator. */
  get mealThumbnailImage(): Locator { return this.container.locator('div[data-testid="AncillariesPage-IndividualSelection-Meals-Children-MealItem-Image-Wrapper"]'); }
  /** Return the child-meal image element locator. */
  get mealImgPropImage(): Locator { return this.mealThumbnailImage.locator('img'); }
  /** Return the child-meal description locator. */
  get mealDescriptionLabel(): Locator { return this.container.locator('div[data-testid="AncillariesPage-IndividualSelection-Meals-Children-MealItem-Description"]'); }
  /** Return the add-meal button locator. */
  get addMealButton(): Locator { return this.container.locator('button[data-testid="AncillariesPage-IndividualSelection-Meals-Children-MealItem-AddSubtractControls-AddButton"]'); }
  /** Return the remove-meal button locator. */
  get removeMealButton(): Locator { return this.container.locator('button[data-testid="AncillariesPage-IndividualSelection-Meals-Children-MealItem-AddSubtractControls-SubtractButton"]'); }
  /** Return the add-meal button icon locator. */
  get addMealButtonImage(): Locator { return this.addMealButton.locator('svg'); }
  /** Return the remove-meal button icon locator. */
  get removeMealButtonImage(): Locator { return this.removeMealButton.locator('svg'); }
  /** Return the meal quantity/value locator. */
  get mealValueLabel(): Locator { return this.container.locator('p[data-testid="AncillariesPage-IndividualSelection-Meals-Children-MealItem-AddSubtractControls-Value"]'); }
  /** Return the children label locator. */
  get childrenLabel(): Locator { return this.container.locator('p[data-testid="AncillariesPage-IndividualSelection-Meals-Children-MealItem-AddSubtractControls-Label"]'); }
  /** Return the list of child-meal title locators. */
  get mealsIndexLabelsList(): Locator { return this.page.locator('h4[data-testid="AncillariesPage-IndividualSelection-Meals-Children-MealItem-Title"]'); }
  /** Read the displayed child meal title. */
  async getChildMealTitle(): Promise<string> { console.log('Get child meal title'); return (await this.mealTitleLabel.textContent())?.trim() ?? ''; }

  // ######## UI actions/navigation ########

  /** Click Remove meal button. */
  async clickRemoveMealButton(): Promise<void> { console.log('Click Remove meal button'); await this.removeMealButton.click(); }
  /** Click Add meal button. */
  async clickAddMealButton(): Promise<void> { console.log('Click Add meal button'); await this.addMealButton.scrollIntoViewIfNeeded(); await this.addMealButton.click(); }
  /** Add the requested number of child meals. */
  async addChildMeals(numberToAdd: number): Promise<void> { console.log(`Add child meals count=${numberToAdd}`); for (let index = 0; index < numberToAdd; index += 1) await this.clickAddMealButton(); }
  /** Remove the requested number of child meals. */
  async removeChildMeals(numberToRemove: number): Promise<void> { console.log(`Remove child meals count=${numberToRemove}`); for (let index = 0; index < numberToRemove; index += 1) await this.clickRemoveMealButton(); }

  // ######## UI validations ########

  /**
   * Validate Remove meal button enabled state.
   * @param isEnabled Whether the button should be enabled.
   */
  async validateRemoveMealButton(isEnabled: boolean): Promise<void> { console.log('Validate Remove meal button enabled state'); await this.validateEnabledState(this.removeMealButton, 'Remove meal button enabled state', isEnabled); if (isEnabled) await expect(this.removeMealButton, 'Remove meal button disabled attribute').not.toHaveAttribute('disabled'); else await expect(this.removeMealButton, 'Remove meal button disabled attribute').toHaveAttribute('disabled', 'true'); await expect(this.removeMealButtonImage, 'Remove meal button icon color').toHaveAttribute('color', expect.stringContaining(isEnabled ? await Strings.COLORS_PRIMARY.name : await Strings.COLORS_DARK_GREY_2.name)); }
  /**
   * Validate Add meal button enabled state.
   * @param isEnabled Whether the button should be enabled.
   */
  async validateAddMealButton(isEnabled: boolean): Promise<void> { console.log('Validate Add meal button enabled state'); await this.validateEnabledState(this.addMealButton, 'Add meal button enabled state', isEnabled); if (isEnabled) await expect(this.addMealButton, 'Add meal button disabled attribute').not.toHaveAttribute('disabled'); else await expect(this.addMealButton, 'Add meal button disabled attribute').toHaveAttribute('disabled', 'true'); await expect(this.addMealButtonImage, 'Add meal button icon color').toHaveAttribute('color', expect.stringContaining(isEnabled ? await Strings.COLORS_PRIMARY.name : await Strings.COLORS_DARK_GREY_2.name)); }
  /**
   * Validate meal value.
   * @param mealNumber Expected meal quantity.
   */
  async validateMealValue(mealNumber: number): Promise<void> { console.log('Validate meal value'); await expect(this.mealValueLabel, 'Child meal value').toHaveText(String(mealNumber)); }
  /**
   * Validate child meal container state.
   * @param addButtonEnabled Whether the add button should be enabled.
   * @param removeButtonEnabled Whether the remove button should be enabled.
   * @param mealValue Expected meal quantity.
   */
  async validateContainer({ addButtonEnabled, removeButtonEnabled, mealValue }: { addButtonEnabled: boolean; removeButtonEnabled: boolean; mealValue: number }): Promise<void> { console.log('Validate child meal container state'); await this.validateAddMealButton(addButtonEnabled); await this.validateRemoveMealButton(removeButtonEnabled); await this.validateMealValue(mealValue); }
  /** Validate that both child-meal quantity buttons are displayed. */
  async validateAddRemoveMealButtonsVisibility(): Promise<void> { console.log('Validate child meal buttons visibility'); await expect(this.addMealButton, 'Add child meal button').toBeVisible(); await expect(this.removeMealButton, 'Remove child meal button').toBeVisible(); }
  /** Validate the child-meal image. */
  async validateChildMealImage(): Promise<void> { console.log('Validate child meal image'); await expect(this.mealThumbnailImage, 'Child meal image').toBeVisible(); }
  /** Validate the child-meal title. */
  async validateChildMealTitle(title: string): Promise<void> { console.log(`Validate child meal title=${title}`); await expect(this.mealTitleLabel, 'Child meal title').toContainText(title.trim()); }
  /** Validate the child-meal description after removing markup. */
  async validateChildMealDescription(description: string): Promise<void> { console.log('Validate child meal description'); const expectedDescription = description.replace(/<([^>]+)>/g, '').replace('&nbsp;', '').trim(); await expect(this.mealDescriptionLabel, 'Child meal description').toContainText(expectedDescription); }
  /** Validate the child/children label for the displayed quantity. */
  async validateChildtrenLabelFromAddSubstractArea(): Promise<void> { console.log('Validate child meal quantity label'); await expect(this.childrenLabel, 'Child meal quantity label').toBeVisible(); const mealValue = Number.parseInt((await this.mealValueLabel.textContent())?.trim() ?? '0', 10); const expectedLabel = mealValue === 1 ? await Strings.CHILD_LOWER_CASE.name : await Strings.CHILDREN_LOWER_CASE.name; await expect(this.childrenLabel, 'Child meal quantity label text').toContainText(expectedLabel); }
  /** Validate that the meal quantity is numeric. */
  async validateNumberOfMealsLabelIsNumeric(): Promise<void> { console.log('Validate child meal quantity is numeric'); const value = (await this.mealValueLabel.textContent())?.trim() ?? ''; expect(Number.isFinite(Number.parseInt(value, 10)), 'Child meal quantity should be numeric').toBe(true); }
  /** Validate the complete child-meal quantity area. */
  async validateAddSubstractMealsArea(): Promise<void> { console.log('Validate child meal quantity area'); await this.validateAddRemoveMealButtonsVisibility(); await this.validateChildtrenLabelFromAddSubstractArea(); await this.validateNumberOfMealsLabelIsNumeric(); }
}