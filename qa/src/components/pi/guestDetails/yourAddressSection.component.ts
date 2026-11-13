import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';

/**
 * The 'Your Address' section (Guest Details) containing the UI elements, custom actions and
 * validations. Mirrors qa/reference `components/opera/guestDetails/yourAddressSection.js`
 * (simplified: postcode-lookup auto-suggestion and the chance/lodash-based random address
 * generation utilities are not ported - no target equivalent exists for those test-data helpers,
 * so this component focuses on the manual-entry surface already used by `GuestDetailsPage`).
 */
export class YourAddressSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly manualAddressLink: Locator = this.page.locator('div[data-testid="GuestDetails-ManualAddressToggle"]');
  readonly manualAddressSection: Locator = this.page.locator('div[data-testid="GuestDetails-AddressSelection"]');
  readonly homeAddressRadioButton: Locator = this.page.locator('span[data-testid="GuestDetails-AddressSelection-PersonalAddress"]');
  readonly homeAddressLabel: Locator = this.homeAddressRadioButton.locator('xpath=following-sibling::span/p');
  readonly yourAddressRequiredErrorLabel: Locator = this.manualAddressSection.locator('div.chakra-radio-group + div p');
  readonly openDropDownCountryMenuButton: Locator = this.manualAddressSection.locator('button[data-testid$="menuButton"]');
  readonly selectedCountrySelectorLabel: Locator = this.page.locator(
    '[data-testid="DropdownComp-Country-menuButton"], [data-testid="GuestDetails-CountrySelector-countrySelector"]'
  );
  readonly searchCountryInput: Locator = this.page.locator(
    'div[data-testid="GuestDetails-CountrySelector"] input[autocomplete="off"]'
  );
  readonly findAddressButton: Locator = this.page.locator('button[data-testid="PostcodeAddress-FindAddressBtn"]');
  readonly enterYourAddressManuallyLink: Locator = this.manualAddressLink.locator('a');

  // UI components

  readonly postCodeInput: Locator = this.page.locator('input[data-testid="input-postcodeAddress"]');
  readonly addressLineOneInput: Locator = this.page.locator('input[data-testid="input-addressLine1"]');
  readonly addressLineTwoInput: Locator = this.page.locator('input[data-testid="input-addressLine2"]');
  readonly addressLineThreeInput: Locator = this.page.locator('input[data-testid="input-addressLine3"]');
  readonly manualPostCodeInput: Locator = this.page.locator('input[data-testid="input-postalCode"]');
  readonly locationInput: Locator = this.page.locator('input[data-testid="input-cityName"]');

  // ######## UI actions/navigation ########

  /** Click 'Enter your address manually' to reveal the manual address fields. */
  async clickManualAddressLink(): Promise<void> {
    console.log('Click manual address link');
    const manualLink = (await this.enterYourAddressManuallyLink.isVisible().catch(() => false))
      ? this.enterYourAddressManuallyLink
      : this.manualAddressLink;
    await manualLink.scrollIntoViewIfNeeded();
    await manualLink.click();
    await this.addressLineOneInput.waitFor({ state: 'visible', timeout: global.browser.options.actionTimeout });
  }

  /** Look up addresses for the given postcode via the postcode search input. */
  async searchByPostcode(postCode: string): Promise<void> {
    console.log(`Search address by postcode: ${postCode}`);
    await this.postCodeInput.fill(postCode);
    await this.findAddressButton.click();
  }

  /** Get the localized country name for a country code (GB/DE). */
  getCountryName(countryCode: string): Promise<string> {
    const normalised = countryCode.toUpperCase();
    return normalised === 'DE' ? Strings.GERMANY.name : Strings.UNITED_KINGDOM_THE.name;
  }

  /** Select a country from the manual-address country dropdown. */
  async selectCountry(countryCode: string): Promise<void> {
    const countryName = await this.getCountryName(countryCode);
    await this.openDropDownCountryMenuButton.click();
    const option = this.page.getByRole('option', { name: countryName }).first();
    if (!(await option.isVisible({ timeout: 3000 }).catch(() => false))) {
      await this.searchCountryInput.fill(countryName);
    }
    await this.page.getByRole('option', { name: countryName }).first().click();
  }

  // ######## UI validations ########

  /** Validate the 'Your address' required error is displayed. */
  async validateAddressRequiredError(): Promise<void> {
    await expect(this.yourAddressRequiredErrorLabel, "Your address's required error").toBeVisible();
  }

  /** Validate the Home Address radio button is displayed with a non-empty label. */
  async validateHomeAddressLabel(): Promise<void> {
    await expect(this.homeAddressRadioButton, 'Home address radio button').toBeVisible();
    await expect(this.homeAddressLabel, 'Home address label').not.toBeEmpty();
  }
}
