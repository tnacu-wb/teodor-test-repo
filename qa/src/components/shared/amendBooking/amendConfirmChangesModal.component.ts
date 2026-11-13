import { type Page, type Locator, expect } from '@playwright/test';

/**
 * The 'Confirm changes' modal shown when amending a booking's email address containing the UI
 * elements, custom actions and validations. Mirrors qa/reference
 * `components/common/amendBooking/amendConfirmChangesModal.js`.
 */
export class AmendConfirmChangesModalComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly container: Locator = this.page.locator('div[data-testid="AmendEmailAddress-ModalContainer"]');
  readonly titleLabel: Locator = this.container.locator('p').nth(0);
  readonly additionalInformationLabel: Locator = this.container.locator('p').nth(1);
  readonly sendConfirmationLabel: Locator = this.container.locator('p').nth(2);
  readonly emailInputField: Locator = this.page.locator('input[data-testid="input-emailAddress"]');
  readonly confirmChangesButton: Locator = this.page.locator('button[data-testid="AmendEmailAddress-ModalButton"]');
  readonly modalCloseButton: Locator = this.page.locator('button[data-testid="ModalCloseButton"]');

  // ######## UI actions/navigation ########

  /** Click the confirm-changes button. */
  async clickConfirmChanges(): Promise<void> {
    console.log('Click confirm changes button in amend email modal');
    await this.confirmChangesButton.click();
  }

  // ######## UI validations ########

  /** Validate the modal's title/additional-information/send-confirmation labels are displayed. */
  async validateElementsAreDisplayed(): Promise<void> {
    console.log('Validate confirm changes modal elements are displayed');
    await expect(this.titleLabel, 'Title label').toBeVisible();
    await expect(this.additionalInformationLabel, 'Additional information label').toBeVisible();
    await expect(this.sendConfirmationLabel, 'Send confirmation by email label').toBeVisible();
    await expect(this.emailInputField, 'Confirmation email input').toBeVisible();
    await expect(this.confirmChangesButton, 'Confirm changes modal button').toBeVisible();
  }
}
