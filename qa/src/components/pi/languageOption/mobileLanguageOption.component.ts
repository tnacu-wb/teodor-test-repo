import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';

/**
 * One mobile language option from the language selector containing the UI elements, custom actions and validations.
 * Mirrors qa/reference `components/opera/languageOption/mobileLanguageOption.js`.
 */
export class MobileLanguageOptionComponent {
  private readonly page: Page = global.page;
  private readonly container: Locator;

  constructor(container: Locator) {
    this.container = container;
  }

  // ######## UI elements/properties ########

  readonly backToLanguageMenuButton: Locator = this.page.locator('div.css-bc00vf');

  get languageLabel(): Locator {
    return this.container.locator('p');
  }

  get flagIcon(): Locator {
    return this.container.locator('div[data-testid="svg-container"] [data-testid*="Flag"]');
  }

  get selectedLanguageTickIcon(): Locator {
    return this.container.locator('img[data-testid="svg-container"]');
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

    await expect(this.backToLanguageMenuButton, 'Mobile back to language menu button').toHaveText(await Strings.LANGUAGE.name);
    await expect(this.languageLabel, `Mobile ${expectedLabel} language option label`).toHaveText(expectedLabel);
    await expect(this.flagIcon, `Mobile ${expectedLabel} language option flag`).toHaveAttribute(
      'data-testid',
      new RegExp(expectedFlagIcon.toLowerCase())
    );

    if (isSelectedLanguage) {
      await expect(this.selectedLanguageTickIcon, `Mobile ${expectedLabel} selected language tick icon`).toBeVisible();
    } else {
      await expect(this.selectedLanguageTickIcon, 'Mobile language tick icon for not selected option').toHaveCount(0);
    }
  }
}
