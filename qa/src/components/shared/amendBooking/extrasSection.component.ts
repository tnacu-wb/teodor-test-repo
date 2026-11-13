import { type Page, type Locator, expect } from '@playwright/test';

/**
 * The Extras section of the Amend Booking flow containing the UI elements, custom actions and
 * validations. Mirrors qa/reference `components/common/amendBooking/extrasSection.js`.
 */
export class ExtrasSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly wifiContainer: Locator = this.page.locator('div[data-testid*="Extras-Item-Wrapper-Ultimate-"]');

  // ######## UI actions/navigation ########

  // ######## UI validations ########

  /** Validate the WiFi extra is displayed. */
  async validateWifiExtraIsDisplayed(): Promise<void> {
    console.log('Validate WiFi extra is displayed');
    await expect(this.wifiContainer, 'WiFi extra container').toBeVisible();
  }
}
