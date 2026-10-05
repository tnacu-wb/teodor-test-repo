import { expect, type Locator } from '@playwright/test';
import { CcuiComponent } from '../baseCcui.component';
import { EntireStayAdultMealContainerComponent } from './entireStayAdultMealContainer.component';
import { EntireStayChildMealContainerComponent } from './entireStayChildMealContainer.component';

/**
 * Entire-stay meals section on the CCUI ancillaries page.
 * Mirrors qa/reference/test/pages/components/ccui/ancillaries/entireStayMealsSection.js.
 */
export class EntireStayMealsSectionComponent extends CcuiComponent {
  // ######## UI elements/properties ########

  readonly adultMealsLabel: Locator = this.page.locator('[data-testid="AncillariesPage-EntireStay-Meals-Adults-Heading-Title"]');
  readonly adultMealItems: Locator = this.page.locator('div[data-testid="AncillariesPage-EntireStay-Meals-Adults-MealItem-Wrapper"]');
  readonly addMealsLabel: Locator = this.page.locator('[data-testid="AncillariesPage-EntireStay-Meals-Heading-Title"]');
  readonly adultsNumberCountAndNightsLabel: Locator = this.page.locator('p[data-testid="AncillariesPage-EntireStay-Meals-Adults-Heading-Values"]');
  readonly restaurantLogoIcon: Locator = this.page.locator('img[data-testid="AncillariesPage-EntireStay-Meals-Heading-RestaurantLogo"]');
  readonly seeMenusLabel: Locator = this.page.locator('[data-testid="AncillariesPage-EntireStay-Meals-Heading-ScrollToMenus"]');
  readonly addMealsButton: Locator = this.page.locator('button[data-testid="AncillariesPage-EntireStay-Meals-Adults-MealItem-Button"]');
  readonly menusWrapper: Locator = this.page.locator('[data-testid="AncillariesPage-Menus-Wrapper"]');
  readonly menusLabelsList: Locator = this.page.locator('a[data-testid="AncillariesPage-Menus-Item"]');

  /**
   * Get an adult meal container by title.
   * @param title Adult meal title used to identify the container.
   * @returns The matching adult meal container component.
   */
  getAdultMealContainerByTitle(title: string): EntireStayAdultMealContainerComponent { return new EntireStayAdultMealContainerComponent(this.adultMealItems.filter({ hasText: title }).first()); }
  /**
   * Get an adult meal container by zero-based index.
   * @param mealPosition Zero-based adult meal position.
   * @returns The adult meal container component at the requested position.
   */
  getAdultMealContainerByIndex(mealPosition: number): EntireStayAdultMealContainerComponent { return new EntireStayAdultMealContainerComponent(this.adultMealItems.nth(mealPosition)); }
  /**
   * Get the child meal container component.
   * @returns The child meal container component.
   */
  getChildMealContainer(): EntireStayChildMealContainerComponent { return new EntireStayChildMealContainerComponent(); }

  // ######## UI actions/navigation ########

  // ######## UI validations ########

  /**
   * Validate entire-stay section title.
   * @param title Expected entire-stay section title.
   */
  async validateEntireStaySectionTitle(title: string): Promise<void> { console.log('Validate entire-stay section title'); await expect(this.addMealsLabel, 'Entire stay section title').toContainText(title); }
}