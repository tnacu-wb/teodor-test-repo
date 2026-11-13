import { expect, type Locator, type Page } from '@playwright/test';

/** Shared helper base for migrated CCUI components. */
export abstract class CcuiComponent {
  /** Page instance shared by migrated CCUI components. */
  protected readonly page: Page = global.page;

  /**
   * Validate a locator's display state.
   * @param locator Locator whose display state should be checked.
   * @param description Assertion description used in diagnostics.
   * @param isDisplayed Whether the locator should be displayed.
   */
  protected async validateDisplayState(locator: Locator, description: string, isDisplayed = true): Promise<void> {
    if (isDisplayed) {
      await expect(locator, description).toBeVisible();
    } else {
      await expect(locator, description).toBeHidden();
    }
  }

  /**
   * Validate a locator's enabled state.
   * @param locator Locator whose enabled state should be checked.
   * @param description Assertion description used in diagnostics.
   * @param isEnabled Whether the locator should be enabled.
   */
  protected async validateEnabledState(locator: Locator, description: string, isEnabled = true): Promise<void> {
    if (isEnabled) {
      await expect(locator, description).toBeEnabled();
    } else {
      await expect(locator, description).toBeDisabled();
    }
  }

  /**
   * Fill an input and optionally move focus away, matching the reference pressTab option.
   * @param locator Input locator to fill.
   * @param value Input value, or null to clear the input.
   * @param pressTab Whether to move focus away after filling.
   */
  protected async fillInput(locator: Locator, value: string | number | null, pressTab = true): Promise<void> {
    await locator.fill(value === null ? '' : String(value));
    if (pressTab) {
      await this.page.keyboard.press('Tab');
    }
  }
}