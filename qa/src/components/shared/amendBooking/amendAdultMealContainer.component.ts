import { type Locator, expect } from '@playwright/test';

/**
 * One adult meal container within the Amend Booking meal-selection flow containing the UI
 * elements, custom actions and validations. Mirrors qa/reference
 * `components/common/amendBooking/amendAdultMealContainer.js` (extends
 * `components/opera/ancillaries/adultMealContainer.js` with the `RoomsMealSelection-*` testid
 * prefix used specifically on the Amend Booking page, instead of `AncillariesPage-*`).
 */
export class AmendAdultMealContainerComponent {
  private readonly container: Locator;

  /** Create an adult meal container scoped to its meal-item wrapper. */
  constructor(container: Locator) {
    this.container = container;
  }

  // ######## UI elements/properties ########

  /** Return the adult meal title locator. */
  get mealTitleLabel(): Locator {
    return this.container.locator('h4[data-testid="RoomsMealSelection-Meals-Adults-MealItem-Title"]');
  }

  /** Return the adult meal thumbnail locator. */
  get mealThumbnailImage(): Locator {
    return this.container.locator('div[data-testid="RoomsMealSelection-Meals-Adults-MealItem-Image-Wrapper"] img');
  }

  /** Return the adult meal price locator. */
  get mealPriceLabel(): Locator {
    return this.container.locator('p[data-testid="RoomsMealSelection-Meals-Adults-MealItem-Price"]');
  }

  /** Return the adult meal description locator. */
  get mealDescriptionLabel(): Locator {
    return this.container.locator('div[data-testid="RoomsMealSelection-Meals-Adults-MealItem-Description"] p');
  }

  /** Return the adult meal allergy label locator. */
  get mealAllergyLabel(): Locator {
    return this.container.locator('p[data-testid="RoomsMealSelection-Meals-Adults-MealItem-Allergy"]');
  }

  /** Return the adult meal allergy link locator. */
  get mealAllergyLink(): Locator {
    return this.container.locator('p[data-testid="RoomsMealSelection-Meals-Adults-MealItem-Allergy"]');
  }

  /** Return the free-meal-for-children label locator. */
  get freeMealForKidsLabel(): Locator {
    return this.container.locator('p[data-testid="RoomsMealSelection-Meals-Adults-MealItem-FreeFoodKids"]');
  }

  /** Return the add-meal button locator. */
  get addMealButton(): Locator {
    return this.container.locator('button[data-testid="RoomsMealSelection-Meals-Adults-MealItem-AddSubtractControls-AddButton"]');
  }

  /** Return the remove-meal button locator. */
  get removeMealButton(): Locator {
    return this.container.locator('button[data-testid="RoomsMealSelection-Meals-Adults-MealItem-AddSubtractControls-SubtractButton"]');
  }

  /** Return the selected adult meal count locator. */
  get mealValueLabel(): Locator {
    return this.container.locator('p[data-testid="RoomsMealSelection-Meals-Adults-MealItem-AddSubtractControls-Value"]');
  }

  /** Get the current adult meal count value for this container. */
  async getAddedAdultMealValue(): Promise<number> {
    console.log('Get adult meal value');
    await expect(this.mealValueLabel, 'Adult meal value label should be visible').toBeVisible();
    return Number.parseInt((await this.mealValueLabel.innerText()).trim(), 10);
  }

  // ######## UI actions/navigation ########

  /** Click the add-meal button. */
  async clickAddMealButton(): Promise<void> {
    console.log('Click add adult meal button');
    await this.addMealButton.scrollIntoViewIfNeeded();
    await this.addMealButton.click();
  }

  /** Click the remove-meal button. */
  async clickRemoveMealButton(): Promise<void> {
    console.log('Click remove adult meal button');
    await this.removeMealButton.click();
  }

  // ######## UI validations ########

  /** Validate this meal container's title and price are displayed. */
  async validateData(): Promise<void> {
    console.log('Validate adult meal container');
    await expect(this.mealTitleLabel, 'Amend adult meal title').toBeVisible();
    await expect(this.mealPriceLabel, 'Amend adult meal price').toBeVisible();
  }
}
