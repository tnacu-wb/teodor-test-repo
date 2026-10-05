import { expect, type Locator } from '@playwright/test';
import { ExtrasSectionComponent } from '@components/shared/amendBooking/extrasSection.component';
import { AccompanyingGuestDetailsSectionComponent } from '@components/pib/ancillaries/accompanyingGuestDetailsSection.component';
import { GuestDetailsSectionComponent } from '@components/pib/ancillaries/guestDetailsSection.component';
import { AncillariesBasePage } from '../shared/ancillariesBase.page';

/** Ancillaries page from the Opera BB environment. */
export class AncillariesBBPage extends AncillariesBasePage {
  protected get ancillariesProjectIdentifier(): string { return 'GuestDetailsPageBB'; }
  readonly url = 'business-booker/booking-business/guest-details';
  readonly extrasSection = new ExtrasSectionComponent();
  readonly guestDetailsSection = new GuestDetailsSectionComponent();
  readonly accompanyingGuestDetailsSection = new AccompanyingGuestDetailsSectionComponent();

  // ######## UI elements/properties ########
  /** The BB meals heading used by the free-breakfast layout validation. */
  get bbMealHeading(): Locator { return this.page.locator('h1[data-testid="GuestDetailsPageBB-MealsHeading"]'); }
  /** The entire-stay meals heading used by the free-breakfast layout validation. */
  get entireStayMealsHeading(): Locator { return this.page.locator('[data-testid="AncillariesPage-EntireStay-Meals-Heading-Title"]'); }

  // ######## UI actions/navigation ########
  /** Wait for the BB meals heading to be loaded. */
  async waitForMealHeading(): Promise<void> { console.log('Wait for BB meal heading'); await this.bbMealHeading.waitFor({ state: 'visible', timeout: browser.options.actionTimeout }); }

  // ######## UI validations ########
  /** Validate the free breakfast block location. */
  async validateFreeBreakFastBlockLocation(): Promise<void> {
    console.log('Validate Free breakfast block location');
    await expect(this.bbMealHeading, 'BB meals heading').toBeVisible();
    const notification = this.mealSection.childMealsNotificationContainer;
    await expect(notification, 'Free breakfast notification').toBeVisible();
    const [headingBox, notificationBox, entireStayBox] = await Promise.all([this.bbMealHeading.boundingBox(), notification.boundingBox(), this.entireStayMealsHeading.boundingBox()]);
    expect(headingBox && notificationBox && notificationBox.y > headingBox.y, 'Free breakfast notification should be below meals heading').toBeTruthy();
    expect(entireStayBox && notificationBox && entireStayBox.y > notificationBox.y && entireStayBox.y - notificationBox.y <= 300, 'Entire-stay meals heading should be below free breakfast notification').toBeTruthy();
  }
}