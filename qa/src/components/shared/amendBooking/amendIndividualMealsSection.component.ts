import { type Page, type Locator, expect } from '@playwright/test';
import { AmendAdultMealContainerComponent } from './amendAdultMealContainer.component';
import { AmendChildMealContainerComponent } from './amendChildMealContainer.component';

/**
 * The 'individual' (per-room) meals section within the Amend Booking meal-selection flow
 * containing the UI elements, custom actions and validations. Mirrors qa/reference
 * `components/common/amendBooking/amendIndividualMealsSection.js` (simplified: extends the
 * ancillaries `MealSection` reference class - only the per-room-scoped elements distinct from
 * the base are exposed here).
 */
export class AmendIndividualMealsSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly sectionContainer: Locator = this.page.locator('div[data-testid="RoomsMealSelection-Meals"] > div').nth(1);
  readonly adultMealsLabel: Locator = this.sectionContainer.locator('h6[data-testid="RoomsMealSelection-Meals-Adults-Heading-Title"]');
  readonly sectionTitleLabel: Locator = this.page.getByTestId('RoomsMealSelection-Meals-Heading-Title');
  readonly roomTabsList: Locator = this.page.locator('[role="tablist"] button');
  readonly adultMealItemsList: Locator = this.sectionContainer.locator('div[data-testid="RoomsMealSelection-Meals-Adults-MealItem-Wrapper"]');
  readonly childMealItemsList: Locator = this.sectionContainer.locator('div[data-testid="RoomsMealSelection-Meals-Children-MealItem-Wrapper"]');

  /** Adult meal container component for the item at the given 0-based index. */
  adultMealContainer(index: number): AmendAdultMealContainerComponent {
    return new AmendAdultMealContainerComponent(this.adultMealItemsList.nth(index));
  }

  /** Child (breakfast) meal container component for the item at the given 0-based index. */
  childMealContainer(index: number): AmendChildMealContainerComponent {
    return new AmendChildMealContainerComponent(this.childMealItemsList.nth(index));
  }

  // ######## UI actions/navigation ########

  /** Select a room tab for individual meal changes. */
  async selectRoom(roomIndex: number): Promise<void> {
    console.log(`Select individual meals room ${roomIndex}`);
    const roomTab = this.roomTabsList.nth(roomIndex);
    await roomTab.waitFor({ state: 'visible' });
    await roomTab.click();
  }

  // ######## UI validations ########

  /** Validate the individual meals section and its adult meals heading are displayed. */
  async validateData(): Promise<void> {
    console.log('Validate individual meals section');
    await expect(this.sectionContainer, 'Individual meals section').toBeVisible();
    await expect(this.adultMealsLabel, 'Adult meals heading').toBeVisible();
  }

  /** Validate the individual-meals section title. */
  async validateSectionTitle(title: string): Promise<void> {
    console.log(`Validate individual meals title=${title}`);
    await expect(this.sectionTitleLabel, 'Individual meals title').toHaveText(title);
  }
}
