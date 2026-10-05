import { expect, type Locator } from '@playwright/test';
import { CcuiComponent } from '../baseCcui.component';
import { IndividualSectionAdultMealContainerComponent } from './individualSectionAdultMealContainer.component';
import { IndividualSectionChildMealContainerComponent } from './individualSectionChildMealContainer.component';

/**
 * Individual meals section on the CCUI ancillaries page.
 * Mirrors qa/reference/test/pages/components/ccui/ancillaries/individualMealsSection.js.
 */
export class IndividualMealsSectionComponent extends CcuiComponent {
  // ######## UI elements/properties ########

  readonly adultMealItems: Locator = this.page.locator('div[data-testid="AncillariesPage-IndividualSelection-Meals-Adults-MealItem-Wrapper"]');
  readonly adultMealsLabel: Locator = this.page.locator('[data-testid="AncillariesPage-IndividualSelection-Meals-Adults-Heading-Title"]');
  readonly adultMealItemsTitleLabels: Locator = this.page.locator('div[data-testid="AncillariesPage-IndividualSelection-Meals-Adults-MealItem-Wrapper"] h4');
  readonly addMealsLabel: Locator = this.page.locator('[data-testid="AncillariesPage-IndividualSelection-Meals-Heading-Title"]');
  readonly adultsNumberCountAndNightsLabel: Locator = this.page.locator('p[data-testid="AncillariesPage-IndividualSelection-Meals-Adults-Heading-Values"]');
  readonly restaurantLogoIcon: Locator = this.page.locator('img[data-testid="AncillariesPage-IndividualSelection-Meals-Heading-RestaurantLogo"]');
  readonly seeMenusLabel: Locator = this.page.locator('[data-testid="AncillariesPage-IndividualSelection-Meals-Heading-ScrollToMenus"]');
  readonly menusWrapper: Locator = this.page.locator('[data-testid="AncillariesPage-Menus-Wrapper"]');
  readonly menusLabelsList: Locator = this.page.locator('a[data-testid="AncillariesPage-Menus-Item"]');
  readonly sectionTitleLabel: Locator = this.page.locator('[data-testid="AncillariesPage-Individual-Room-Title"]');
  readonly childMealsSectionTitleLabel: Locator = this.page.locator('[data-testid="AncillariesPage-IndividualSelection-Meals-Children-Heading-Title"]');
  readonly kidsMealsContainersList: Locator = this.page.locator('div[data-testid="AncillariesPage-IndividualSelection-Meals-Children-MealItem-Wrapper"]');

  /**
   * Get the child meal container component.
   * @returns The child meal container component.
   */
  getChildMealContainer(): IndividualSectionChildMealContainerComponent { return new IndividualSectionChildMealContainerComponent(); }
  /**
   * Get an adult meal container by zero-based index.
   * @param mealPosition Zero-based adult meal position.
   * @returns The adult meal container component at the requested position.
   */
  getAdultMealContainerByIndex(mealPosition: number): IndividualSectionAdultMealContainerComponent { return new IndividualSectionAdultMealContainerComponent(this.adultMealItems.nth(mealPosition)); }

  // ######## UI actions/navigation ########

  // ######## UI validations ########

  /**
   * Validate the individual-meals section title.
   * @param title Expected individual-meals section title.
   */
  async validateTitle(title: string): Promise<void> { console.log('Validate individual meals title'); await expect(this.sectionTitleLabel, 'Individual meals section title').toContainText(title); }
  /**
   * Validate the individual-meals section subtitle.
   * @param subTitle Expected individual-meals section subtitle.
   */
  async validateSubTitle(subTitle: string): Promise<void> { console.log('Validate individual meals subtitle'); await expect(this.addMealsLabel, 'Individual meals section subtitle').toContainText(subTitle); }
}