import { expect, type Locator, type Page } from "@playwright/test";
import { Strings } from "../../../test-data/strings";
import { IbStrings } from "../../../test-data/pib/ibStrings";

/**
 * InnBusiness Change Password Section from My Profile page
 */
export class ChangePasswordSectionComponent {
  private readonly page: Page = global.page;

  // ######## properties ########

  // ######## UI elements/properties ########
  readonly changePasswordSectionContainer: Locator = this.page.locator( '[data-testid="ChangePassword-form"]', );
  readonly changePasswordSectionTitleLabel: Locator = this.page.locator( 'h4[data-testid="ProfilePage-Change-Password-Title"]', );
  readonly changePasswordSectionCurrentPasswordInput: Locator = this.page.locator('input[data-testid="currentPassword-Form-Input"]');
  readonly changePasswordSectionCurrentPasswordErrorTooltip: Locator = this.page.locator('[data-testid="currentPassword-Error-Tooltip"]');
  readonly changePasswordSectionNewPasswordInput: Locator = this.page.locator( 'input[data-testid="newPassword-Form-Input"]', );
  readonly changePasswordSectionConfirmPasswordInput: Locator = this.page.locator('input[data-testid="confirmPassword-Form-Input"]');
  readonly changePasswordSectionUpdateButton: Locator = this.page.locator( 'button[data-testid="ProfilePage-Change-Password-Update-Password"]', );
  readonly changePasswordSectionCancelUpdateButton: Locator = this.page.locator( 'input[data-testid="ProfilePage-Change-Password-Cancel-update"]', );
  readonly changePasswordSectionNewPasswordErrorTooltip: Locator = this.page.locator('[data-testid="newPassword-Error-Tooltip"]');
  readonly changePasswordSectionConfirmPasswordErrorTooltip: Locator = this.page.locator('div[data-testid="confirmPassword-Error-Tooltip"]');

  // ######## UI actions/navigation ########
  /** Set password fields. */
  async setPasswordInput({
    currentPassword,
    newPassword,
    confirmPassword,
  }: {
    currentPassword?: string;
    newPassword?: string;
    confirmPassword?: string;
  }): Promise<void> {
    console.log("Set password fields");
    if (currentPassword)
      await this.changePasswordSectionCurrentPasswordInput.fill(
        currentPassword,
      );
    if (newPassword) {
      await this.changePasswordSectionNewPasswordInput.fill(newPassword);
      await this.changePasswordSectionNewPasswordInput.press("Tab");
    }
    if (confirmPassword) {
      await this.changePasswordSectionConfirmPasswordInput.fill(
        confirmPassword,
      );
      await this.changePasswordSectionConfirmPasswordInput.press("Tab");
    }
  }

  /** Click Current password field. */
  async clickCurrentPasswordFiled(): Promise<void> {
    console.log("Click current password field");
    await this.changePasswordSectionCurrentPasswordInput.scrollIntoViewIfNeeded();
    await this.changePasswordSectionCurrentPasswordInput.click();
  }

  /** Click Update password button, retrying if the success toast is not displayed. */
  async clickUpdatePasswordButton({ retry = 2 }: { retry?: number } = {}): Promise<void> {
    console.log("Click update password button");
    await this.changePasswordSectionUpdateButton.scrollIntoViewIfNeeded();
    await this.changePasswordSectionUpdateButton.click();

    if (retry > 0) {
      const toastLabel = this.page.locator('[data-testid="Toast"]');
      let isSuccessful = true;
      try {
        await toastLabel.waitFor({ state: "visible", timeout: 5000 });
        isSuccessful = (await toastLabel.textContent()) === (await IbStrings.YOUR_CHANGES_HAVE_BEEN_UPDATED.name);
      } catch {
        isSuccessful = false;
      }
      if (!isSuccessful) {
        console.log(`Retrying click on update password button. Retries left: ${retry - 1}`);
        await this.clickUpdatePasswordButton({ retry: retry - 1 });
      }
    }
  }

  /** Click outside current password field. */
  async clickOutsideOfCurrentPasswordField(): Promise<void> {
    console.log("Click outside of Current password field");
    await this.changePasswordSectionCurrentPasswordInput.press("Tab");
  }

  // ######## UI validations ########
  /** Validate Change Password container display state. */
  async validatePasswordContainerIsDisplayed(
    isDisplayed = true,
  ): Promise<void> {
    console.log("Validate Change Password container");
    if (isDisplayed)
      await expect( this.changePasswordSectionContainer, "Password Container", ).toBeVisible();
    else
      await expect( this.changePasswordSectionContainer, "Password Container", ).not.toBeVisible();
  }

  /** Validate New Password tooltip and message. */
  async validateNewPasswordErrorTooltipAndMessage({
    isDisplayed = false,
    expectedMessage,
  }: {
    isDisplayed?: boolean;
    expectedMessage?: string | Promise<string>;
  }): Promise<void> {
    console.log("Validate New Password error message");
    if (isDisplayed) {
      await expect( this.changePasswordSectionNewPasswordErrorTooltip, "Error message Container", ).toBeVisible();
      await expect( this.changePasswordSectionNewPasswordErrorTooltip, "Password error message", ).toHaveText((await expectedMessage) ?? "");
    } else
      await expect( this.changePasswordSectionNewPasswordErrorTooltip, "Error message Container", ).not.toBeVisible();
  }

  /** Validate Current password error tooltip and label display state. */
  async validateCurrentPasswordErrorTooltipAndLabel(
    isDisplayed = false,
  ): Promise<void> {
    console.log("Validate Current Password error message");
    if (isDisplayed) {
      await expect( this.changePasswordSectionCurrentPasswordErrorTooltip, "Error message Container", ).toBeVisible();
      await expect( this.changePasswordSectionCurrentPasswordErrorTooltip, "Password error message", ).toHaveText(await Strings.YOU_NEED_TO_ENTER_YOUR_PASSWORD.name);
    } else
      await expect( this.changePasswordSectionCurrentPasswordErrorTooltip, "Error message Container", ).not.toBeVisible();
  }

  /** Validate Confirm Password error tooltip and label display state. */
  async validateConfirmPasswordErrorTooltipAndLabelAreDisplayed({
    isDisplayed = false,
    expectedMessage,
  }: {
    isDisplayed?: boolean;
    expectedMessage?: string | Promise<string>;
  }): Promise<void> {
    console.log("Validate Confirm Password error message");
    if (isDisplayed) {
      await expect( this.changePasswordSectionConfirmPasswordErrorTooltip, "Confirm password error tooltip", ).toBeVisible();
      await expect( this.changePasswordSectionConfirmPasswordErrorTooltip, "Password error message", ).toHaveText((await expectedMessage) ?? "");
    } else
      await expect( this.changePasswordSectionConfirmPasswordErrorTooltip, "Confirm password error tooltip", ).not.toBeVisible();
  }
}
