import { type Page, type Locator, expect } from '@playwright/test';
import { AmendAdultMealContainerComponent } from './amendAdultMealContainer.component';
import { AmendChildMealContainerComponent } from './amendChildMealContainer.component';

/**
 * The 'entire stay' meals section within the Amend Booking meal-selection flow (applies the same
 * meal choice to every night of the stay) containing the UI elements, custom actions and
 * validations. Mirrors qa/reference `components/common/amendBooking/amendEntireStayMealsSection.js`
 * (simplified: extends the ancillaries `MealSection` reference class - only the entire-stay-scoped
 * elements distinct from the base are exposed here).
 */
export class AmendEntireStayMealsSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly sectionContainer: Locator = this.page.locator('div[data-testid="RoomsMealSelection-Meals"] > div').nth(0);
  readonly adultMealsLabel: Locator = this.sectionContainer.locator('h6[data-testid="RoomsMealSelection-Meals-Adults-Heading-Title"]');
  readonly addMealsLabel: Locator = this.page.getByTestId('RoomsMealSelection-Meals-Heading-Title');
  readonly adultMealItemsList: Locator = this.sectionContainer.locator('div[data-testid="RoomsMealSelection-Meals-Adults-MealItem-Wrapper"]');
  readonly childMealItemsList: Locator = this.sectionContainer.locator('div[data-testid="RoomsMealSelection-Meals-Children-MealItem-Wrapper"]');
  readonly roomTabsList: Locator = this.page.locator('[role="tablist"] button');
  readonly mealSelectionNotification: Locator = this.page.locator('[data-testid="RoomsMealSelection-notification-Alert"]');
  readonly updateMealSectionButton: Locator = this.page.getByTestId('RoomsMealSelection-Meals-Update-Meal-Selection');

  /** Adult meal container component for the item at the given 0-based index. */
  adultMealContainer(index: number): AmendAdultMealContainerComponent {
    return new AmendAdultMealContainerComponent(this.adultMealItemsList.nth(index));
  }

  /** Child (breakfast) meal container component for the item at the given 0-based index. */
  childMealContainer(index: number): AmendChildMealContainerComponent {
    return new AmendChildMealContainerComponent(this.childMealItemsList.nth(index));
  }

  /** Open a room tab in the per-room meal selection view. */
  async clickRoomTabButton(roomIndex: number): Promise<void> {
    const roomTab = this.page.locator(`button[data-testid="Room ${roomIndex + 1}-TabButton"]`);
    await roomTab.waitFor({ state: 'visible' });
    console.log(`Open Room ${roomIndex + 1} tab`);
    await roomTab.click();
  }

  /** Return the adult meal container matching the displayed meal title. */
  adultMealContainerByTitle(title: string): AmendAdultMealContainerComponent {
    console.log(`Find adult meal container with title=${title}`);
    return new AmendAdultMealContainerComponent(this.adultMealItemsList.filter({ hasText: title }).first());
  }

  /** Return the child breakfast meal container. */
  childBreakfastMealContainer(): AmendChildMealContainerComponent {
    console.log('Find child breakfast meal container');
    return this.childMealContainer(0);
  }

  // ######## UI actions/navigation ########

  /** Apply the selected meal changes for the amendment. */
  async clickUpdateMealSectionButton(): Promise<void> {
    console.log('Click Update Meal Section');
    await this.updateMealSectionButton.waitFor({ state: 'visible' });
    await this.updateMealSectionButton.click();
  }

  // ######## UI validations ########

  /** Validate the entire-stay meals section and its adult meals heading are displayed. */
  async validateData(): Promise<void> {
    console.log('Validate entire stay meals section');
    await expect(this.sectionContainer, 'Entire stay meals section').toBeVisible();
    await expect(this.adultMealsLabel, 'Adult meals heading').toBeVisible();
  }

  /** Validate the entire-stay meal section title. */
  async validateEntireStaySectionTitle(title: string): Promise<void> {
    console.log(`Validate entire-stay meals title=${title}`);
    await expect(this.addMealsLabel, 'Entire-stay meals title').toHaveText(title);
  }

  /** Validate the meal-selection notification after an amendment. */
  async validateMealSelectionNotification(isDisplayed = true): Promise<void> {
    console.log(`Validate meal selection notification isDisplayed=${isDisplayed}`);
    if (isDisplayed) {
      await expect(this.mealSelectionNotification, 'Meal selection success notification').toBeVisible();
    } else {
      await expect(this.mealSelectionNotification, 'Meal selection success notification should be hidden').not.toBeVisible();
    }
  }
}
