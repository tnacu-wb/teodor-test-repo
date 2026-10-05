import { type Page, type Locator, expect } from '@playwright/test';

// Matches AncillariesPage.PAGE_IDENTIFIER ('AncillariesPage') - inlined to avoid a circular
// import between this component and the page that composes it.
const ID = 'AncillariesPage';

/**
 * The individual meal container suggested for children on the Ancillaries page containing the UI
 * elements, custom actions and validations. Mirrors qa/reference
 * `components/opera/ancillaries/childMealContainer.js` (simplified: reference selects the *last*
 * matching container via XPath `[last()]` to always target the most-recently-added child meal
 * row - here callers pass the specific row `Locator` instead, which is the idiomatic Playwright
 * equivalent).
 */
export class ChildMealContainerComponent {
  private readonly page: Page = global.page;
  private readonly container: Locator;

  constructor(container?: Locator) {
    this.container = container ?? this.page.locator(`div[data-testid="${ID}-Meals-Children-MealItem-Wrapper"]`).last();
  }

  // ######## UI elements/properties ########

  get mealTitleLabel(): Locator {
    return this.container.locator(`h4[data-testid="${ID}-Meals-Children-MealItem-Title"]`);
  }

  get mealThumbnailImage(): Locator {
    return this.container.locator(`div[data-testid="${ID}-Meals-Children-MealItem-Image-Wrapper"] img`);
  }

  get mealDescriptionLabel(): Locator {
    return this.container.locator(`div[data-testid="${ID}-Meals-Children-MealItem-Description"]`);
  }

  get addMealButton(): Locator {
    return this.container.locator(`button[data-testid="${ID}-Meals-Children-MealItem-AddSubtractControls-AddButton"]`);
  }

  get removeMealButton(): Locator {
    return this.container.locator(`button[data-testid="${ID}-Meals-Children-MealItem-AddSubtractControls-SubtractButton"]`);
  }

  get mealValueLabel(): Locator {
    return this.container.locator(`p[data-testid="${ID}-Meals-Children-MealItem-AddSubtractControls-Value"]`);
  }

  get childrenLabel(): Locator {
    return this.container.locator(`p[data-testid="${ID}-Meals-Children-MealItem-AddSubtractControls-Label"]`);
  }

  // ######## UI actions/navigation ########

  /** Get the child meal title text. */
  async getChildMealTitle(): Promise<string> {
    return (await this.mealTitleLabel.innerText()).trim();
  }

  /** Click the remove-meal button. */
  async clickRemoveMealButton(): Promise<void> {
    console.log('Click remove meal button');
    await this.removeMealButton.click();
  }

  /** Click the add-meal button. */
  async clickAddMealButton(): Promise<void> {
    console.log('Click add meal button');
    await this.addMealButton.scrollIntoViewIfNeeded();
    await this.addMealButton.click();
  }

  // ######## UI validations ########

  /** Validate this child meal container's title is displayed. */
  async validateData(): Promise<void> {
    await expect(this.mealTitleLabel, 'Child meal title').toBeVisible();
  }
}
