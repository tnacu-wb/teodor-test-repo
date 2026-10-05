import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';

const ID = 'AncillariesPage';

/**
 * The add-meal section on the Ancillaries page containing the UI elements, custom actions and
 * validations. Mirrors qa/reference `components/opera/ancillaries/mealSection.js` (simplified:
 * `AncillariesPage` already implements the adult-meal add/find/validate flow directly for its
 * baseline test needs - this component adds the remaining section-level elements: restaurant
 * header/menu links and the child-meals/restaurant-unavailable notifications).
 */
export class MealSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly addMealsLabel: Locator = this.page.locator(`[data-testid="${ID}-Meals-Heading-Title"]`);
  readonly adultMealsLabel: Locator = this.page.locator(`[data-testid="${ID}-Meals-Adults-Heading-Title"]`);
  readonly adultsNumberCountAndNightsLabel: Locator = this.page.locator(`p[data-testid="${ID}-Meals-Adults-Heading-Values"]`);
  readonly restaurantLogoIcon: Locator = this.page.locator(`img[data-testid="${ID}-Meals-Heading-RestaurantLogo"]`);
  readonly restaurantMessageTitleLabel: Locator = this.page.locator(`[data-testid="${ID}-RestaurantMessage-Title"]`);
  readonly restaurantMessageDescriptionLabel: Locator = this.page.locator(`[data-testid="${ID}-RestaurantMessage-Description"]`);
  readonly seeMenusLabel: Locator = this.page.locator(`[data-testid="${ID}-Meals-Heading-ScrollToMenus"]`);
  readonly menusWrapper: Locator = this.page.locator(`[data-testid="${ID}-Menus-Wrapper"]`);
  readonly menusLabelsList: Locator = this.page.locator(`a[data-testid="${ID}-Menus-Item"]`);
  readonly restaurantUnavailableNotificationContainer: Locator = this.page.locator(`[data-testid="${ID}-RestaurantUnavailableNotification-Wrapper"]`);
  readonly childMealsSectionTitleLabel: Locator = this.page.locator(`[data-testid="${ID}-Meals-Children-Heading-Title"]`);
  readonly kidsMealsContainersList: Locator = this.page.locator(`div[data-testid="${ID}-Meals-Children-MealItem-Wrapper"]`);
  readonly childMealsNotificationContainer: Locator = this.page.locator(`div[data-testid="${ID}-FreeFoodKidsNotification-Wrapper"]`);

  // ######## UI actions/navigation ########

  /** Click 'See menus' to scroll to the restaurant menus section. */
  async clickSeeMenus(): Promise<void> {
    await this.seeMenusLabel.click();
  }

  // ######## UI validations ########

  /** Validate the 'Add meals' heading and adult meals sub-heading are displayed. */
  async validateHeadingsDisplayed(): Promise<void> {
    await expect(this.addMealsLabel, 'Add meals heading').toBeVisible();
    await expect(this.adultMealsLabel, 'Adult meals heading').toBeVisible();
  }

  /** Validate the restaurant-unavailable notification is displayed, or not. */
  async validateRestaurantUnavailableNotification(isDisplayed = true): Promise<void> {
    if (isDisplayed) {
      await expect(this.restaurantUnavailableNotificationContainer, 'Restaurant unavailable notification').toBeVisible();
    } else {
      await expect(this.restaurantUnavailableNotificationContainer, 'Restaurant unavailable notification').not.toBeVisible();
    }
  }
}
