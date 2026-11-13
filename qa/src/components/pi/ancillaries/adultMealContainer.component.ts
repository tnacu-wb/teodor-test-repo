import { type Locator, expect } from '@playwright/test';

// Matches AncillariesPage.PAGE_IDENTIFIER ('AncillariesPage') - inlined to avoid a circular
// import between this component and the page that composes it.
const ID = 'AncillariesPage';

/**
 * One adult meal container suggested on the Ancillaries page containing the UI elements, custom
 * actions and validations. Mirrors qa/reference `components/opera/ancillaries/adultMealContainer.js`
 * (simplified: this is a lower-level per-item accessor - `AncillariesPage` already implements the
 * primary add/find-meal-by-title flow directly for its own baseline test needs; use this component
 * when a specific meal container instance needs finer-grained inspection).
 */
export class AdultMealContainerComponent {
  private readonly container: Locator;

  constructor(container: Locator) {
    this.container = container;
  }

  // ######## UI elements/properties ########

  get mealTitleLabel(): Locator {
    return this.container.locator(`h4[data-testid="${ID}-Meals-Adults-MealItem-Title"]`);
  }

  get mealThumbnailImage(): Locator {
    return this.container.locator(`div[data-testid="${ID}-Meals-Adults-MealItem-Image-Wrapper"] img`);
  }

  get mealPriceLabel(): Locator {
    return this.container.locator(`p[data-testid="${ID}-Meals-Adults-MealItem-Price"]`);
  }

  get mealDescriptionLabel(): Locator {
    return this.container.locator(`div[data-testid="${ID}-Meals-Adults-MealItem-Description"] p`);
  }

  get freeMealForKidsLabel(): Locator {
    return this.container.locator(`p[data-testid="${ID}-Meals-Adults-MealItem-FreeFoodKids"]`);
  }

  get addMealButton(): Locator {
    return this.container.locator(`button[data-testid="${ID}-Meals-Adults-MealItem-AddSubtractControls-AddButton"]`);
  }

  get removeMealButton(): Locator {
    return this.container.locator(`button[data-testid="${ID}-Meals-Adults-MealItem-AddSubtractControls-SubtractButton"]`);
  }

  get mealValueLabel(): Locator {
    return this.container.locator(`p[data-testid="${ID}-Meals-Adults-MealItem-AddSubtractControls-Value"]`);
  }

  // ######## UI actions/navigation ########

  /** Get the current adult meal count value for this container. */
  async getAdultMealValue(): Promise<number> {
    return Number((await this.mealValueLabel.innerText()).trim());
  }

  /** Click the add-meal button. */
  async clickAddMealButton(): Promise<void> {
    console.log('Click add meal button');
    await this.addMealButton.scrollIntoViewIfNeeded();
    await this.addMealButton.click();
  }

  /** Click the remove-meal button. */
  async clickRemoveMealButton(): Promise<void> {
    console.log('Click remove meal button');
    await this.removeMealButton.click();
  }

  // ######## UI validations ########

  /** Validate this meal container's title and price are displayed. */
  async validateData(): Promise<void> {
    await expect(this.mealTitleLabel, 'Adult meal title').toBeVisible();
    await expect(this.mealPriceLabel, 'Adult meal price').toBeVisible();
  }
}
