import { expect, type Locator } from '@playwright/test';
import { Strings } from '@test-data/strings';
import { CcuiComponent } from '../baseCcui.component';

/**
 * Adult meal container in the CCUI entire-stay meals section.
 * Mirrors qa/reference/test/pages/components/ccui/ancillaries/entireStayAdultMealContainer.js.
 */
export class EntireStayAdultMealContainerComponent extends CcuiComponent {
  /**
   * Create an adult-meal container scoped to the supplied meal item.
   * @param container Meal-item locator used as the component root.
   */
  constructor(private readonly container: Locator = global.page.locator('div[data-testid="AncillariesPage-EntireStay-Meals-Adults-MealItem-Wrapper"]').first()) { super(); }

  // ######## UI elements/properties ########

  /** Return the root container for this meal item. */
  get sectionContainer(): Locator { return this.container; }
  /** Return the meal title locator. */
  get mealTitleLabel(): Locator { return this.container.locator('h4[data-testid="AncillariesPage-EntireStay-Meals-Adults-MealItem-Title"]'); }
  /** Return the meal thumbnail image locator. */
  get mealThumbnailImage(): Locator { return this.container.locator('div[data-testid="AncillariesPage-EntireStay-Meals-Adults-MealItem-Image-Wrapper"]'); }
  /** Return the meal price locator. */
  get mealPriceLabel(): Locator { return this.container.locator('p[data-testid="AncillariesPage-EntireStay-Meals-Adults-MealItem-Price"]'); }
  /** Return the meal description locator. */
  get mealDescriptionLabel(): Locator { return this.container.locator('div[data-testid="AncillariesPage-EntireStay-Meals-Adults-MealItem-Description"] p'); }
  /** Return the meal allergy-information label locator. */
  get mealAllergyLabel(): Locator { return this.container.locator('p[data-testid="AncillariesPage-EntireStay-Meals-Adults-MealItem-Allergy"]'); }
  /** Return the meal allergy-information link locator. */
  get mealAllergyLink(): Locator { return this.mealAllergyLabel.locator('..'); }
  /** Return the free-meal-for-children label locator. */
  get freeMealForKidsLabel(): Locator { return this.container.locator('p[data-testid="AncillariesPage-EntireStay-Meals-Adults-MealItem-FreeFoodKids"]'); }
  /** Return the add/remove meals button locator. */
  get addRemoveMealsButton(): Locator { return this.container.locator('button[data-testid="AncillariesPage-EntireStay-Meals-Adults-MealItem-Button"]'); }
  /** Return the parent container of the add/remove meals button. */
  get addRemoveMealsButtonChildContainer(): Locator { return this.container.locator('button[data-testid="AncillariesPage-EntireStay-Meals-Children-MealItem-Button"]'); }
  /** Return the displayed meal quantity locator. */
  get mealValueLabel(): Locator { return this.container.locator('p[data-testid="AncillariesPage-EntireStay-Meals-Adults-MealItem-AddSubtractControls-Value"]'); }
  /** Return the list of adult-meal title locators. */
  get mealsTitlesLabelsList(): Locator { return this.page.locator('h4[data-testid="AncillariesPage-EntireStay-Meals-Adults-MealItem-Title"]'); }
  /** Read the displayed adult meal quantity. */
  async getAdultMealValue(): Promise<number> { console.log('Get adult meal value'); const value = Number.parseInt((await this.mealValueLabel.textContent())?.trim() ?? '0', 10); return Number.isNaN(value) ? 0 : value; }
  /** Read the displayed adult meal title. */
  async getAdultMealTitle(): Promise<string> { console.log('Get adult meal title'); return (await this.mealTitleLabel.textContent())?.trim() ?? ''; }
  /** Read the displayed adult meal price. */
  async getAdultMealPrice(): Promise<string> { console.log('Get adult meal price'); return (await this.mealPriceLabel.textContent())?.trim() ?? ''; }

  // ######## UI actions/navigation ########

  /** Click Add/Remove meal button. */
  async clickAddRemoveMealButton(): Promise<void> {
    console.log('Click Add/Remove adult meal button');
    await this.addRemoveMealsButton.click();
  }
  /** Click the add-meal button when the entire-stay control exposes it. */
  async clickAddMealButton(): Promise<void> { console.log('Click add adult meal button'); await this.addRemoveMealsButton.click(); }
  /** Click the remove-meal button when the entire-stay control exposes it. */
  async clickRemoveMealButton(): Promise<void> { console.log('Click remove adult meal button'); await this.addRemoveMealsButton.click(); }
  /** Click the allergy and nutrition link. */
  async clickAllergyAndNutritionLink(): Promise<void> { console.log('Click allergy and nutrition link'); await this.mealAllergyLabel.click(); }

  // ######## UI validations ########

  /**
   * Validate Add/Remove meal button state.
   * @param isPressed Whether the button should have its pressed state set.
   */
  async validateAddRemoveMealButton(isPressed: boolean): Promise<void> {
    console.log('Validate Add/Remove adult meal button label');
    const expectedLabel = isPressed ? await Strings.ADD_ANCILLARIES_BUTTON.name : await Strings.REMOVE_ANCILLARIES_BUTTON.name;
    await expect(this.addRemoveMealsButton, 'Add/Remove adult meal button label').toContainText(expectedLabel);
  }
  /** Validate the displayed adult meal quantity. */
  async validateMealValue(mealNumber: number): Promise<void> { console.log('Validate adult meal value'); await expect(this.mealValueLabel, 'Adult meal value').toHaveText(String(mealNumber)); }
}