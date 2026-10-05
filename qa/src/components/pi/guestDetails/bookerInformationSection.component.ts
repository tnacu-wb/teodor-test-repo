import { type Page, type Locator } from '@playwright/test';

/**
 * The Booker Information section (Guest Details) containing the UI elements, custom actions and validations.
 * Mirrors qa/reference `components/opera/guestDetails/bookerInformationSection.js`.
 * Simplified for Playwright: country-prefix dropdown mechanics are handled via the parent
 * `GuestDetailsPage.fillBookerInformation()`; this component exposes the raw field locators
 * and phone-prefix actions that aren't already covered there.
 */
export class BookerInformationSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly titleButton: Locator = this.page.locator('button[data-testid="DropdownComp-GuestDetails-Title-InnerDropdown-menuButton"]');
  readonly titleValueLabel: Locator = this.page.locator('div[data-testid="DropdownComp-GuestDetails-Title-InnerDropdown-menuButtonText"]');
  readonly firstNameInput: Locator = this.page.locator('input[data-testid="input-firstName"]');
  readonly lastNameInput: Locator = this.page.locator('input[data-testid="input-lastName"]');
  readonly emailAddressInput: Locator = this.page.locator('input[data-testid="input-email"]');
  readonly mobilePhoneInput: Locator = this.page.locator('input[data-testid="GuestDetails-Mobile-phoneNumber"]');
  readonly landlineInput: Locator = this.page.locator('input[data-testid="GuestDetails-Landline-phoneNumber"]');
  readonly landlineCountrySelectorButton: Locator = this.page.locator('div[data-testid="GuestDetails-Landline-countrySelector"]');
  readonly mobileCountrySelectorButton: Locator = this.page.locator('div[data-testid="GuestDetails-Mobile-countrySelector"]');
  readonly landlineCountrySelectorPrefixLabel: Locator = this.landlineCountrySelectorButton.locator('span');
  readonly mobileCountrySelectorPrefixLabel: Locator = this.mobileCountrySelectorButton.locator('span');
  readonly errorTitleLabel: Locator = this.page.locator('div[data-testid="GuestDetails-Title"] p');
  readonly errorTitleIcon: Locator = this.page.locator('div[data-testid="GuestDetails-Title"] div[data-testid="inputIconError"]');

  // ######## UI actions/navigation ########

  /** Click the country-prefix selector button for the mobile field. */
  async clickMobileCountrySelectorButton(): Promise<void> {
    console.log('Click on MobileSelector Button');
    await this.mobileCountrySelectorButton.scrollIntoViewIfNeeded();
    await this.mobileCountrySelectorButton.click();
  }

  /** Click the country-prefix selector button for the landline field. */
  async clickLandlineCountrySelectorButton(): Promise<void> {
    console.log('Click on LandlineSelector Button');
    await this.landlineCountrySelectorButton.scrollIntoViewIfNeeded();
    await this.landlineCountrySelectorButton.click();
  }

  /** Set the phone-prefix search box (opened via the country selector) and select the matching result. */
  private async setPrefix(countrySelectorButton: Locator, countryName: string): Promise<void> {
    await countrySelectorButton.click();
    const prefixInput = countrySelectorButton.locator('xpath=following-sibling::div//input');
    await prefixInput.fill(countryName);
    await this.page.locator('ul').getByText(countryName, { exact: false }).first().click();
  }

  /** Set the mobile phone prefix based on the given country name. */
  async setPrefixMobile(countryName: string): Promise<void> {
    console.log('Set MobilePhonePrefix');
    await this.setPrefix(this.mobileCountrySelectorButton, countryName);
  }

  /** Set the landline phone prefix based on the given country name. */
  async setPrefixLandline(countryName: string): Promise<void> {
    console.log('Set LandLinePrefix');
    await this.setPrefix(this.landlineCountrySelectorButton, countryName);
  }
}
