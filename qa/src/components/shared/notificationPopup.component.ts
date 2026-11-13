import { type Page } from '@playwright/test';

/**
 * Shared Notification Popup Component — handles the browser push-notification
 * permission popup that appears across all channels.
 */
export class NotificationPopupComponent {
  private readonly page: Page = global.page;

  /**
   * Dismiss the notification permission popup if displayed by clicking "Deny".
   * No-op if the popup is not present.
   */
  async dismissIfPresent(): Promise<void> {
    console.log('Dismissing notification popup if present');
    const denyButton = this.page.locator(
      '[data-testid="pi-notification-permission-popup-deny-btn"]'
    );
    try {
      await denyButton.waitFor({ state: 'visible', timeout: 3000 });
      await denyButton.click();
    } catch {
      // Notification popup not displayed — continue
    }
  }
}
