import { type Locator, expect } from '@playwright/test';

/**
 * Notification card saying that one specific accessible room type is not available.
 * Mirrors qa/reference `components/opera/chooseYourBathroom/noRoomTypeNotification.js`.
 */
export class NoRoomTypeNotificationComponent {
  private readonly container: Locator;

  constructor(container: Locator) {
    this.container = container;
  }

  // ######## UI elements/properties ########

  get notificationIcon(): Locator {
    return this.container.locator('[data-testid*="-Alert"] [data-testid="svg-container"] svg');
  }

  get roomTypeLabel(): Locator {
    return this.container.locator('[data-testid*="-AlertTitle"]');
  }

  get notificationDescription(): Locator {
    return this.container.locator('[data-testid*="-AlertDescription"] p');
  }

  // ######## UI actions/navigation ########

  // ######## UI validations ########

  /** Validate notification label and description. */
  async validateData({
    roomTypeLabel,
    notificationDescription,
  }: { roomTypeLabel?: string; notificationDescription?: string } = {}): Promise<void> {
    console.log('Validate roomType and description');

    await expect(this.notificationIcon, 'Notification icon visibility').toBeVisible();

    if (roomTypeLabel) {
      await expect(this.roomTypeLabel, 'Room type label').toHaveText(roomTypeLabel);
    } else {
      expect((await this.roomTypeLabel.innerText()).length, 'Empty room type label').toBeGreaterThan(0);
    }

    if (notificationDescription) {
      await expect(this.notificationDescription, 'Notification description').toHaveText(notificationDescription);
    } else {
      expect((await this.notificationDescription.innerText()).length, 'Empty notification description label.').toBeGreaterThan(0);
    }
  }
}
