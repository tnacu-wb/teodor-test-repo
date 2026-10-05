import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '../../../test-data/strings';

/**
 * Cancel booking modal section on Manage Booking / Booking History page (PI).
 * Mirrors qa/reference test/pages/components/common/bookingHistory/cancelBookingModal.js
 * plus the cancel-related getters from bookingInformationCardSectionBase.js.
 */
export class CancelBookingModalComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly container: Locator = this.page.locator('[data-testid="CancelBookingModalContainer"]');
  readonly titleLabel: Locator = this.container.locator('p:nth-child(1)');
  readonly additionalInformationLabel: Locator = this.container.locator('p:nth-child(2)');
  readonly bookedForLabel: Locator = this.container.locator('p:nth-child(3)');
  readonly hotelLabel: Locator = this.container.locator('p:nth-child(4)');
  readonly dateLabel: Locator = this.container.locator('p:nth-child(5)');
  readonly confirmButton: Locator = this.container.locator('button').nth(0);
  readonly keepButton: Locator = this.container.locator('button').nth(1);
  readonly closeButton: Locator = this.page.locator('[data-testid="ModalCloseButton"]');
  readonly backButton: Locator = this.page.locator('[data-testid="CancelBookingModalBackButton"]');
  readonly alertContainer: Locator = this.container.locator('[data-testid="Alert"]');
  readonly alertDescriptionLabel: Locator = this.container.locator('[data-testid="AlertDescription"]');
  readonly checkMark: Locator = this.container.locator('[data-testid="svg-container"]');

  // ######## UI actions/navigation ########

  /**
   * Click the 'Keep booking' button, dismissing the cancellation.
   */
  async clickKeepBooking(): Promise<void> {
    console.log('Click on \'Keep booking\' button');
    await this.keepButton.scrollIntoViewIfNeeded();
    await this.keepButton.click();
  }

  /**
   * Confirm the cancellation in the cancel booking modal.
   * Clicks the "Cancel booking" confirmation button within the modal.
   * Waits for the cancellation success alert to appear (indicating processing is complete).
   */
  async confirmCancellation(): Promise<void> {
    console.log('Confirming cancellation in modal');
    await this.confirmButton.waitFor({ state: 'visible' });
    await this.confirmButton.click();

    // Wait for the success alert to appear — this confirms cancellation processed
    await this.alertContainer.waitFor({ state: 'visible', timeout: 30000 });
  }

  /**
   * Close the cancel modal after the cancellation success message is displayed.
   * Tries the modal close button first (ModalCloseButton), falls back to the back button.
   */
  async close(): Promise<void> {
    console.log('Close cancel booking modal');
    if (await this.closeButton.isVisible()) {
      await this.closeButton.click();
    } else {
      await this.backButton.waitFor({ state: 'visible' });
      await this.backButton.click();
    }
  }

  // ######## UI validations ########

  /**
   * Validate modal elements are displayed.
   */
  async validateElementsAreDisplayed(): Promise<void> {
    console.log('Validate cancel booking modal elements are displayed');
    await expect(this.titleLabel, 'Title label should be visible').toBeVisible();
    await expect(this.additionalInformationLabel, 'Additional information label should be visible').toBeVisible();
    await expect(this.bookedForLabel, 'Booked for label should be visible').toBeVisible();
    await expect(this.hotelLabel, 'Hotel label should be visible').toBeVisible();
    await expect(this.dateLabel, 'Date label should be visible').toBeVisible();
    await expect(this.confirmButton, 'Cancel booking button should be visible').toBeVisible();
    await expect(this.keepButton, 'Keep booking button should be visible').toBeVisible();
  }

  /**
   * Validate the label displayed for the Cancel booking confirm button is as expected.
   */
  async validateConfirmButtonLabel(): Promise<void> {
    console.log('Validate cancel booking confirmation button label');
    await expect(this.confirmButton, `Cancel booking label should be "${await Strings.CONFIRM_CANCEL_BOOKING.name}"`).toContainText(await Strings.CONFIRM_CANCEL_BOOKING.name);
  }

  /**
   * Validate the label displayed for the Keep booking button is as expected.
   */
  async validateKeepButtonLabel(): Promise<void> {
    console.log('Validate keep booking button label');
    await expect(this.keepButton, `Keep booking label should be "${await Strings.KEEP_BOOKING_FOR_MANAGE_BOOKING.name}"`).toContainText(await Strings.KEEP_BOOKING_FOR_MANAGE_BOOKING.name);
  }

  /**
   * Validate the cancellation success message is displayed with the booking reference.
   * Verifies the alert container, check mark icon, and description text that includes
   * the booking reference.
   * @param bookingReference - the booking reference that should appear in the success message
   */
  async validateCancellationSuccessMessage(bookingReference: string): Promise<void> {
    console.log(`Validating cancellation success message for booking: ${bookingReference}`);
    await expect(this.alertContainer, 'Cancellation alert container should be visible').toBeVisible({ timeout: 10000 });
    await expect(this.checkMark, 'Cancellation success check mark should be visible').toBeVisible();
    await expect(this.alertDescriptionLabel, 'Cancellation alert description should be visible').toBeVisible();
    await expect(this.alertDescriptionLabel, `Cancellation message should contain booking reference "${bookingReference}"`).toContainText(bookingReference);
  }
}
