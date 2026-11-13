import { expect, type Locator } from '@playwright/test';
import { AncillariesBasePage } from '../shared/ancillariesBase.page';

/**
 * PI Ancillaries page specialization.
 *
 * Shared booking, meals, extras, navigation, and validation behavior is
 * implemented by AncillariesBasePage, mirroring the reference base page.
 */
export class AncillariesPage extends AncillariesBasePage {
  // ######## UI elements/properties ########

  /** PI-specific meals-for-entire-stay title used by free-breakfast layout checks. */
  get mealsForEntireStayTitle(): Locator {
    return this.mealsHeading;
  }

  // ######## UI actions/navigation ########

  // ######## UI validations ########

  /** Validate the PI free-breakfast notice appears between the meals and entire-stay headings. */
  async validateFreeBreakFastBlockLocation(): Promise<void> {
    console.log('Validate free breakfast notification location');
    const notification = this.mealSection.childMealsNotificationContainer;
    await expect(this.mealHeading, 'Meals heading before free breakfast location validation').toBeVisible();
    await expect(notification, 'Free breakfast notification').toBeVisible();

    const [headingBox, notificationBox, entireStayBox] = await Promise.all([
      this.mealHeading.boundingBox(),
      notification.boundingBox(),
      this.mealsForEntireStayTitle.boundingBox(),
    ]);
    expect(
      headingBox && notificationBox && notificationBox.y > headingBox.y,
      'Free breakfast notification should be below meals heading'
    ).toBeTruthy();
    expect(
      notificationBox && entireStayBox && entireStayBox.y > notificationBox.y && entireStayBox.y - notificationBox.y <= 300,
      'Meals for entire stay heading should be below free breakfast notification within 300px'
    ).toBeTruthy();
  }
}
