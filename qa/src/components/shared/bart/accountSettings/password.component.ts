import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';

/**
 * 'Password' section on the Account Settings page, legacy ("bart") Premier Inn web application.
 * Mirrors qa/reference `pages/bart/components/accountSettings/password.js`.
 *
 */
export class PasswordSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly changePasswordTitleLabel: Locator = this.page.locator('form[name="password-form"] ~ h4');
  readonly currentPasswordInput: Locator = this.page.locator('form[name="password-form"] input#currentPassword');
  readonly newPasswordInput: Locator = this.page.locator('form[name="password-form"] input#newPassword');
  readonly confirmPasswordInput: Locator = this.page.locator('form[name="password-form"] input#confirmPassword');
  readonly updatePasswordButton: Locator = this.page.locator('form[name="password-form"] button').first();
  readonly cancelUpdatePasswordButton: Locator = this.page.locator('form[name="password-form"] button').nth(1);
  readonly successPasswordUpdateMessage: Locator = this.page.locator('div[data-test="success-message"] div.content');
  readonly passwordErrorMessageLabel: Locator = this.page.locator('div[data-test="error-notification"] div[class*="content"]');
  readonly errorMessageContainer: Locator = this.page.locator('form[name="password-form"] span[class*="form-item-msg--error"]');

  // ######## UI actions/navigation ########

  /** Fill current/new/confirm password fields and submit. */
  async updatePassword({
    currentPassword,
    newPassword,
  }: { currentPassword: string; newPassword: string }): Promise<void> {
    console.log('Update password');
    await this.currentPasswordInput.fill(currentPassword);
    await this.newPasswordInput.fill(newPassword);
    await this.confirmPasswordInput.fill(newPassword);
    await this.updatePasswordButton.click();
  }

  // ######## UI validations ########

  /** Validate the password update success message is displayed. */
  async validatePasswordUpdateSuccess(): Promise<void> {
    console.log('Validate password update success message');
    await expect(this.successPasswordUpdateMessage, 'Password update success message').toBeVisible();
  }

  /** Set the current password field. */
  async setCurrentPassword({ password }: { password: string }): Promise<void> {
    console.log('Set current password');
    await this.currentPasswordInput.fill(password);
  }

  /** Set the new password field. */
  async setNewPassword({ password }: { password: string }): Promise<void> {
    console.log('Set new password');
    await this.newPasswordInput.fill(password);
  }

  /** Set the confirmation password field. */
  async setConfirmPassword({ password }: { password: string }): Promise<void> {
    console.log('Set confirm password');
    await this.confirmPasswordInput.fill(password);
  }

  /** Validate the password success message display state. */
  async validatePasswordSuccessUpdateMessage({ shouldBeDisplayed = false }: { shouldBeDisplayed?: boolean } = {}): Promise<void> {
    console.log(`Validate password success message displayed=${shouldBeDisplayed}`);
    if (shouldBeDisplayed) await expect(this.successPasswordUpdateMessage, 'Success message').toBeVisible();
    else await expect(this.successPasswordUpdateMessage, 'Success message').toBeHidden();
  }

  /** Set both new and confirmation password fields. */
  async setNewAndConfirmPassword({ password }: { password: string }): Promise<void> {
    console.log('Set new and confirm password');
    await this.newPasswordInput.fill(password);
    await this.confirmPasswordInput.fill(password);
  }

  /** Click update password and preserve the reference success/error retry behavior. */
  async clickUpdatePasswordButton({ retry = 3 }: { retry?: number } = {}): Promise<void> {
    console.log(`Click update password button, retries remaining=${retry}`);
    await this.updatePasswordButton.scrollIntoViewIfNeeded();
    await this.updatePasswordButton.click();
    if (retry > 0) {
      const successOrError = await this.successPasswordUpdateMessage.isVisible() || await this.passwordErrorMessageLabel.isVisible();
      if (!successOrError || !(await this.successPasswordUpdateMessage.isVisible())) {
        console.log(`Retrying click on update password button. Retries left: ${retry - 1}`);
        await this.clickUpdatePasswordButton({ retry: retry - 1 });
      }
    }
  }

  /** Validate a password notification message. */
  async validatePasswordMessage({ passwordMessage }: { passwordMessage: string | Promise<string> }): Promise<void> {
    console.log('Validate password error notification');
    await expect(this.passwordErrorMessageLabel, 'Password error message label').toContainText(await passwordMessage);
  }

  /** Validate an inline password error message. */
  async validatePasswordErrorMessage({ errorMessage }: { errorMessage: string | Promise<string> }): Promise<void> {
    console.log('Validate password inline error message');
    await expect(this.errorMessageContainer, 'Password inline error message').toContainText(await errorMessage);
  }

  /** Validate the password requirement labels. */
  async validatePasswordRequirementLabels(): Promise<void> {
    console.log('Validate password requirement labels');
    for (const requirement of [Strings.CHARACTERS_MINIMUM, Strings.NUMBER_REQUIRED, Strings.CAPITAL_LETTER, Strings.NO_SPECIAL_CHARACTERS]) {
      await expect(this.page.locator('form[name="password-form"] *').filter({ hasText: await requirement.name }).first(), `Password requirement ${await requirement.name}`).toBeVisible();
    }
  }

  /** Validate that the password fields and update action are visible. */
  async validatePasswordFieldsPresent(): Promise<void> {
    console.log('Validate password fields are present');
    await expect(this.currentPasswordInput, 'Current password input').toBeVisible();
    await expect(this.newPasswordInput, 'New password input').toBeVisible();
    await expect(this.confirmPasswordInput, 'Confirm password input').toBeVisible();
    await expect(this.updatePasswordButton, 'Update password button').toBeVisible();
  }
}
