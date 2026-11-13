import { type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';
import { BasePage } from '../shared/base.page';

/**
 * Reset Password ('My account') page of the legacy ("bart") Premier Inn web application. Mirrors
 * qa/reference `pages/bart/resetMyAccount.page.js` (simplified: reference `InputElement` wrapper
 * replaced with direct `Locator` fill). Inert/unwired - see session memory.
 */
export class ResetMyAccountPage extends BasePage {
  // ######## UI elements/properties ########

  readonly submitButton: Locator = this.page.locator('.reset-password-wrapper button[type="submit"]');
  readonly resetEmailInput: Locator = this.page.locator('.reset-password-wrapper #reset-email');
  readonly resetPasswordInput: Locator = this.page.locator('.reset-password-wrapper #reset-password');
  readonly resetConfirmPasswordInput: Locator = this.page.locator('.reset-password-wrapper #reset-confirm-password');
  readonly resetPasswordSuccessLabel: Locator = this.page.locator('.reset-password-wrapper .wb-notification-new-text');

  // ######## UI actions/navigation ########

  /** Set the reset email address input. */
  async setEmailAddress(emailAddress: string): Promise<void> {
    await this.resetEmailInput.fill(emailAddress);
  }

  /** Fill the new password and confirmation, then submit. */
  async resetPassword({ password, confirmPassword }: { password: string; confirmPassword: string }): Promise<void> {
    await this.resetPasswordInput.fill(password);
    await this.resetConfirmPasswordInput.fill(confirmPassword);
    await this.submitButton.click();
  }

  // ######## UI validations ########

  /** Validate the reset-password success message is displayed. */
  async validateResetPasswordSuccess(): Promise<void> {
    await expect(this.resetPasswordSuccessLabel, 'Reset password success message').toBeVisible();
  }
}
