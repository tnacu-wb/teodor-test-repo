import { type Page, type Locator, expect } from '@playwright/test';
import { Strings } from '@test-data/strings';

/**
 * The billing address section on the payment page containing the UI elements, custom actions
 * and validations. Mirrors qa/reference `components/common/payment/billingAddressSection.js`
 * (simplified: the reference's `chance`-based random-address generation and `InputElement`
 * wrapper are dropped - callers pass address values directly and use `Locator.fill()`).
 */
export class BillingAddressSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########

  readonly billingAddressContainer: Locator = this.page.locator('form#billingAddressForm');
  readonly billingAddressLabel: Locator = this.billingAddressContainer.locator('p');
  readonly useCurrentAddressRadioButton: Locator = this.page.locator('div[data-testid="Payment-BillingAddressSelection"] div[data-testid="radio-box-wrapper"]').nth(0).locator('label');
  readonly useDifferentAddressRadioButton: Locator = this.page.locator('div[data-testid="Payment-BillingAddressSelection"] div[data-testid="radio-box-wrapper"]').nth(1).locator('label');
  readonly differentAddressContainer: Locator = this.page.locator('p[data-testid="DifferentAddress-DifferentInput"]');
  readonly yourAddressLabel: Locator = this.page.locator('p[data-testid="Payment-BillingAddress-YourDifferentAdress-Title"]');
  readonly findAddressButton: Locator = this.page.locator('button[data-testid="PostcodeAddress-FindAddressBtn"]');
  readonly enterAddressManuallyLink: Locator = this.page.locator('div[data-testid="Payment-ManualAddressToggle"] a');
  readonly billingAddressOptionsList: Locator = this.page.locator('div[data-testid="Payment-BillingAddressSelection"] div[data-testid="radio-box-wrapper"]');
  readonly useCurrentAddressLabel: Locator = this.billingAddressOptionsList.nth(0).locator('p').first();
  readonly useCurrentProvidedAddressLabel: Locator = this.billingAddressOptionsList.nth(0).locator('p').nth(1);
  readonly useDifferentAddressLabel: Locator = this.billingAddressOptionsList.nth(1).locator('p');
  readonly homeAddressLabel: Locator = this.page.locator('span[data-testid="Payment-addressSelection-PersonalAddress"]').locator('xpath following-sibling::span//p');
  readonly homeAddressRadioButton: Locator = this.page.locator('span[data-testid="Payment-addressSelection-PersonalAddress"]');
  readonly companyAddressLabel: Locator = this.page.locator('span[data-testid="Payment-addressSelection-CompanyAddress"]').locator('xpath following-sibling::span//p');
  readonly companyAddressRadioButton: Locator = this.page.locator('span[data-testid="Payment-addressSelection-CompanyAddress"]');
  readonly selectedCountryFlagIcon: Locator = this.page.locator('button[data-testid="DropdownComp-Country-menuButton"] img');
  readonly selectedCountryNameLabel: Locator = this.page.locator('[data-testid="DropdownComp-Country-menuButtonText"]');
  readonly addressCountryButton: Locator = this.page.locator('button[data-testid="DropdownComp-Country-menuButton"]');
  readonly addressCountryDropDownList: Locator = this.page.locator('div[data-testid="DropdownComp-Country-entireList"] div[data-testid*="DropdownComp-Country-"]');
  readonly addressCompanyNameInput: Locator = this.page.locator('input[data-testid="input-companyName"]');
  readonly addressLine1Input: Locator = this.page.locator('input[data-testid="input-addressLine1"]');
  readonly addressLine2Input: Locator = this.page.locator('input[data-testid="input-addressLine2"]');
  readonly addressLine3Input: Locator = this.page.locator('input[data-testid="input-addressLine3"]');
  readonly addressLine4Input: Locator = this.page.locator('input[data-testid="input-addressLine4"]');
  readonly cityNameInput: Locator = this.page.locator('input[data-testid="input-cityName"]');
  readonly addressPostcodeInput: Locator = this.page.locator('input[data-testid="input-postalCode"]');
  readonly useDifferentAddressPostcodeInput: Locator = this.page.locator('input[data-testid="input-postcodeAddress"]');
  readonly companyNameErrorMessageInput: Locator = this.page.locator('[data-testid="input-companyName-FormErrorMessage"]');
  readonly submitButton: Locator = this.page.locator('[data-testid="submitButton"]');
  readonly billingHomeAddressLabel: Locator = this.page.locator('[data-testid="GuestDetails-BillingAddress-PersonalAddress"]');
  readonly billingAddressLineOneInput: Locator = this.page.locator('input[data-testid="input-billing_addressLine1"]');
  readonly billingAddressLineTwoInput: Locator = this.page.locator('input[data-testid="input-billing_addressLine2"]');
  readonly billingAddressLineThreeInput: Locator = this.page.locator('input[data-testid="input-billing_addressLine3"]');
  readonly billingAddressPostalCodeInput: Locator = this.page.locator('input[data-testid="input-billing_postalCode"]');
  readonly billingAddressCityNameInput: Locator = this.page.locator('input[data-testid="input-billing_cityName"]');

  // ######## UI actions/navigation ########

  /** Select 'Use current address' for billing. */
  async selectUseCurrentAddress(): Promise<void> {
    console.log('Click Use current address radio button');
    await this.useCurrentAddressRadioButton.click();
  }

  /** Select 'Use a different address' for billing. */
  async selectUseDifferentAddress(): Promise<void> {
    console.log('Click Use different address radio button');
    await this.useDifferentAddressRadioButton.click();
  }

  /** Return a country option locator by visible country name. */
  getCountryDropDownOptionByName(countryName: string): Locator {
    return this.addressCountryButton.locator('xpath following-sibling::div//button').filter({ hasText: countryName }).first();
  }

  /** Select a country in the manual address dropdown. */
  async selectCountry(country: string): Promise<void> {
    console.log(`Select country: ${country}`);
    await this.addressCountryButton.click();
    await this.getCountryDropDownOptionByName(country).click();
  }

  /** Open the manual address form after selecting a different billing address. */
  async openEnterAddressManuallyLink(): Promise<void> {
    console.log('Navigate and click Enter address manually link');
    await this.selectUseDifferentAddress();
    await this.enterAddressManuallyLink.scrollIntoViewIfNeeded();
    await this.enterAddressManuallyLink.click();
  }

  /** Click the country dropdown. */
  async clickCountryDropdown(): Promise<void> {
    console.log('Click Country dropdown');
    await this.addressCountryButton.click();
  }

  /** Click the billing home address label. */
  async clickBillingHomeAddressLabel(): Promise<void> {
    console.log('Click billing Home Address label');
    await this.billingHomeAddressLabel.click();
  }

  /** Click Find address. */
  async clickFindAddressButton(): Promise<void> {
    console.log('Click find address button');
    await this.findAddressButton.click();
  }

  /** Return an address suggestion by partial text. */
  getFindAddressSuggestionByText(text: string): Locator {
    return this.page.locator('[data-testid="DropdownComp-PostcodeAddress-DropdownComp-entireList"]').getByText(text, { exact: false }).first();
  }

  /** Select an address suggestion by partial text. */
  async selectAddressBasedOnText(text: string): Promise<void> {
    console.log(`Select address based on partial text: ${text}`);
    await this.getFindAddressSuggestionByText(text).click();
  }

  /** Fill the postcode used to find an address. */
  async setPostcode(value: string, pressTab = false): Promise<void> {
    console.log(`Set postcode ${value}`);
    await this.useDifferentAddressPostcodeInput.fill(value);
    if (pressTab) await this.useDifferentAddressPostcodeInput.press('Tab');
  }

  /** Fill the company name. */
  async setCompanyName(companyName: string, pressTab = false): Promise<void> {
    console.log(`Set company name value: ${companyName}`);
    await this.addressCompanyNameInput.fill(companyName);
    if (pressTab) await this.addressCompanyNameInput.press('Tab');
  }

  /** Fill a manual address line. */
  async setAddressLine1(value: string, pressTab = false): Promise<void> { await this.fillAddressField(this.addressLine1Input, 'Address line 1', value, pressTab); }
  async setAddressLine2(value: string, pressTab = false): Promise<void> { await this.fillAddressField(this.addressLine2Input, 'Address line 2', value, pressTab); }
  async setAddressLine3(value: string, pressTab = false): Promise<void> { await this.fillAddressField(this.addressLine3Input, 'Address line 3', value, pressTab); }
  async setAddressLine4(value: string, pressTab = false): Promise<void> { await this.fillAddressField(this.addressLine4Input, 'Address line 4', value, pressTab); }
  async setManuallyEnteredAddressPostcode(value: string, pressTab = false): Promise<void> { await this.fillAddressField(this.addressPostcodeInput, 'Manual address postcode', value, pressTab); }

  /** Select the business address option. */
  async selectBusinessAddressRadioButton(): Promise<void> { console.log('Select Business Address radio button'); await this.companyAddressRadioButton.click(); }

  /** Fill the billing address fields. */
  async setBillingAddressFields(address: { addressLine1?: string; addressLine2?: string; addressLine3?: string; addressLine4?: string; postalCode?: string; cityName?: string }): Promise<void> {
    console.log('Set billing address fields');
    await this.billingAddressLineOneInput.fill(address.addressLine1 ?? '');
    await this.billingAddressLineTwoInput.fill(address.addressLine2 ?? '');
    await this.billingAddressLineThreeInput.fill(address.addressLine3 ?? '');
    await this.billingAddressPostalCodeInput.fill(address.postalCode ?? '');
    await this.billingAddressCityNameInput.fill(address.cityName ?? '');
  }

  /** Click Use current address. */
  async clickUseCurrentAddressRadioButton(): Promise<void> { await this.selectUseCurrentAddress(); }

  /** Click submit. */
  async clickSubmitButton(): Promise<void> { console.log('Click submit button'); await this.submitButton.click(); }

  // ######## UI validations ########

  /** Validate the billing address section is displayed. */
  async validateBillingAddressSectionIsDisplayed(): Promise<void> {
    console.log('Validate billing address section');
    await expect(this.billingAddressContainer, 'Billing address container').toBeVisible();
  }

  /** Validate the available billing address choices. */
  async validateData(defaultSuppliedAddress: { postalCode: string; addressLine1: string }): Promise<void> {
    console.log('Validate billing address section data');
    await expect(this.billingAddressLabel, 'Billing address label').toContainText(await Strings.BILLING_ADDRESS.name);
    await expect(this.billingAddressOptionsList, 'Billing address options').toHaveCount(2);
    await expect(this.useCurrentAddressLabel, 'Use current address label').toContainText(await Strings.USE_CURRENT_ADDRESS.name);
    await expect(this.useCurrentProvidedAddressLabel, 'Current provided address').toContainText(`${defaultSuppliedAddress.postalCode} ${defaultSuppliedAddress.addressLine1}`);
    await expect(this.useDifferentAddressLabel, 'Use different address label').toContainText(await Strings.USE_DIFFERENT_ADDRESS.name);
  }

  /** Validate that use-current is selected and use-different is not. */
  async validateUseCurrentAddressIsCheckedAndUseDifferentAddressNot(): Promise<void> {
    console.log('Validate current address selection');
    await expect(this.useCurrentAddressRadioButton, 'Use current address selection').toHaveAttribute('data-checked', /(?:^$|true)/);
    await expect(this.useDifferentAddressRadioButton, 'Use different address selection').not.toHaveAttribute('data-checked');
  }

  /** Validate that use-different is selected and use-current is not. */
  async validateUseDifferentAddressIsCheckedAndUseCurrentAddressNot(): Promise<void> {
    console.log('Validate different address selection');
    await expect(this.useDifferentAddressRadioButton, 'Use different address selection').toHaveAttribute('data-checked', /(?:^$|true)/);
    await expect(this.useCurrentAddressRadioButton, 'Use current address selection').not.toHaveAttribute('data-checked');
  }

  /** Validate the default different-address form. */
  async validateYourDifferentAddressDefaultSectionIsDisplayed(): Promise<void> {
    console.log('Validate different address section');
    await expect(this.yourAddressLabel, 'Your address label').toBeVisible();
    await expect(this.useDifferentAddressPostcodeInput, 'Different address postcode').toBeVisible();
    await expect(this.findAddressButton, 'Find address button').toBeVisible();
    await expect(this.enterAddressManuallyLink, 'Enter address manually link').toBeVisible();
  }

  private async fillAddressField(field: Locator, description: string, value: string, pressTab: boolean): Promise<void> {
    console.log(`Set ${description}: ${value}`);
    await field.fill(value);
    if (pressTab) await field.press('Tab');
  }
}
