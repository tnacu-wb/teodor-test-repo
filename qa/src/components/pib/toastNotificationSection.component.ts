import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../test-data/pib/ibStrings";

/**
 * InnBusiness application > Toast notification
 */
export class ToastNotificationSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########
  readonly toastLabel: Locator = this.page.locator('li[data-testid="Toast"]');
  readonly genericNotificationTooltipLabel: Locator = this.page.locator( "div.bg-tooltipError", );

  // ######## UI validations ########
  /** Validate toast notification. */
  async validateToastNotification({
    message,
  }: {
    message: string | Promise<string>;
  }): Promise<void> {
    const resolvedMessage = await message;
    console.log(`Validate toast notification=${resolvedMessage}`);
    await expect(this.toastLabel, "Toast notification label").toBeVisible();
    await expect( this.toastLabel, `Toast notification label text should be=${resolvedMessage}`, ).toHaveText(resolvedMessage);
  }

  /** Validate Generic notification error. */
  async validateGenericNotificationTooltipLabel({
    message,
  }: {
    message: string | Promise<string>;
  }): Promise<void> {
    const resolvedMessage = await message;
    console.log(`Validate generic notification=${resolvedMessage}`);
    await expect( this.genericNotificationTooltipLabel, "Generic error message", ).toHaveText(resolvedMessage.replace(/([a-z])([A-Z])/g, "$1\n$2"));
  }

  /** Validate account registered notification. */
  async validateAccountRegisteredSuccessfullyNotification(): Promise<void> {
    console.log("Validate account registered notification");
    const successMessage = await IbStrings.PAY_APP_LOGIN_SUCCESS.name;
    await expect( this.toastLabel, "Account registration success notification pattern", ).toHaveText( new RegExp(successMessage.replace("{accountNumber}", "\\d{16}")), );
  }
}
