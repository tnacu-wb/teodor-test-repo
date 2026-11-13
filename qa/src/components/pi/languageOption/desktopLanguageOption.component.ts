import { type Locator, expect } from '@playwright/test';

/**
 * One desktop language option from the language selector containing the UI elements, custom actions and validations.
 * Mirrors qa/reference `components/opera/languageOption/desktopLanguageOption.js`.
 */
export class DesktopLanguageOptionComponent {
  private readonly container: Locator;

  constructor(container: Locator) {
    this.container = container;
  }

  // ######## UI elements/properties ########

  get languageLabel(): Locator {
    return this.container.locator('> div > p');
  }

  get flagIcon(): Locator {
    return this.container.locator('img[data-testid="svg-container"]');
  }

  get selectedLanguageTickIcon(): Locator {
    return this.container.locator('> img');
  }

  // ######## UI actions/navigation ########

  // ######## UI validations ########

  /** Validate the expected data for one language option. */
  async validateData({
    expectedLabel,
    expectedFlagIcon,
    isSelectedLanguage,
  }: {
    expectedLabel: string;
    expectedFlagIcon: string;
    isSelectedLanguage: boolean;
  }): Promise<void> {
    console.log(`Validate ${expectedLabel} language option`);

    await expect(this.languageLabel, `Desktop ${expectedLabel} language option label`).toContainText(expectedLabel);
    await expect(this.flagIcon, `Desktop ${expectedLabel} language option flag`).toHaveAttribute(
      'src',
      new RegExp(expectedFlagIcon.toLowerCase())
    );

    if (isSelectedLanguage) {
      await expect(this.selectedLanguageTickIcon, 'Desktop selected language tick icon').toBeVisible();
    } else {
      await expect(this.selectedLanguageTickIcon, 'Desktop language tick icon for not selected option').toHaveCount(0);
    }
  }
}
