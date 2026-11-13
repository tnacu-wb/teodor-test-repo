import { type Page, type Locator, expect } from '@playwright/test';

/**
 * 'Manage permissions' (marketing preferences) section on the Account Settings page, legacy
 * ("bart") Premier Inn web application. Mirrors qa/reference
 * `pages/bart/components/accountSettings/managePermissions.js`.
 */
export class ManagePermissionsSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly backToYourProfileButton: Locator = this.page.locator('div#pi-marketing-preferences button[class*="preferences--back-button-container"]');
  readonly editContactButton: Locator = this.page.locator('div#pi-marketing-preferences button[class*="options-preferences-button"]');
  readonly premierInnEmailPreferencesTitleLabel: Locator = this.page.locator('div[data-testid="Premier Inn Hotels"] > div > h4');
  readonly premierInnEmailPreferencesTextLabel: Locator = this.page.locator('div[data-testid="Premier Inn Hotels"] > div > div.email-preferences-text');
  readonly premierInnEmailPreferencesCheckbox: Locator = this.page.locator('div[data-testid="Premier Inn Hotels"] > div > div.email-preferences-checkbox-tick');
  readonly premierInnEmailPreferencesIconsList: Locator = this.page.locator('div[data-testid="Premier Inn Hotels"] div[data-testid="table-desktop-view"]');
  readonly whitbreadEmailPreferencesTitleLabel: Locator = this.page.locator('div[data-testid="Whitbread restaurants"] > div > h4');
  readonly whitbreadEmailPreferencesTextLabel: Locator = this.page.locator('div[data-testid="Whitbread restaurants"] > div > div.email-preferences-text');
  readonly whitbreadEmailPreferencesCheckbox: Locator = this.page.locator('div[data-testid="Whitbread restaurants"] > div > div.email-preferences-checkbox-tick');
  readonly whitbreadEmailPreferencesIconsList: Locator = this.page.locator('div[data-testid="Whitbread restaurants"] div[data-testid="table-desktop-view"]');
  readonly thirdPartyEmailPreferencesTitleLabel: Locator = this.page.locator('div[data-testid="Third party vendors"] > div > h4');
  readonly thirdPartyEmailPreferencesTextLabel: Locator = this.page.locator('div[data-testid="Third party vendors"] > div > div.email-preferences-text');
  readonly thirdPartyEmailPreferencesCheckbox: Locator = this.page.locator('div[data-testid="Third party vendors"] > div > div.email-preferences-checkbox-tick');
  readonly saveChangesButton: Locator = this.page.locator('button[data-testid="save-button"]');
  readonly cancelChangesButton: Locator = this.page.locator('button[data-testid="cancel-button"]');

  // ######## UI actions/navigation ########

  /** Toggle the Premier Inn Hotels email preferences checkbox. */
  async togglePremierInnEmailPreferences(): Promise<void> {
    console.log('Toggle Premier Inn Hotels email preferences');
    await this.premierInnEmailPreferencesCheckbox.click();
  }

  /** Click 'Back to your profile'. */
  async clickBackToYourProfile(): Promise<void> {
    console.log('Click Back to your profile');
    await this.backToYourProfileButton.click();
  }

  // ######## UI validations ########

  /** Validate the Premier Inn Hotels email preferences title is displayed. */
  async validatePremierInnEmailPreferencesTitleIsDisplayed(): Promise<void> {
    console.log('Validate Premier Inn Hotels email preferences title');
    await expect(this.premierInnEmailPreferencesTitleLabel, 'Premier Inn Hotels email preferences title').toBeVisible();
  }
}
