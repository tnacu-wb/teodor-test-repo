import { expect, type Locator } from '@playwright/test';
import { Constants } from '../../../test-data/constants';
import { Strings } from '../../../test-data/strings';
import { CcuiComponent } from '../baseCcui.component';
import { PaymentTypeSectionComponent } from './paymentTypeSection.component';

/**
 * Billing address section from the CCUI payment page.
 * Mirrors qa/reference/test/pages/components/ccui/payment/billingAddressSection.js.
 */
export class BillingAddressSectionComponent extends CcuiComponent {
	// ######## properties ########

	// ######## UI elements/properties ########

	readonly billingAddressContainer: Locator = this.page.locator('form[data-testid="Payment-BillingAdress-MainContainer"]');
	readonly billingAddressTitleLabel: Locator = this.page.locator('p[data-testid="Payment-BillingAddress-Title"]');
	readonly useCurrentAddressRadioButton: Locator = this.page.locator('span[data-testid="Payment-BillingAddressSelection-SameAddress"]');
	readonly useCurrentAddressLabel: Locator = this.page.locator('p[data-testid="Payment-BillingAddress-UseCurrentAdress-Title"]');
	readonly useCurrentAddressCurrentAddressLabel: Locator = this.page.locator('p[data-testid="Payment-BillingAddress-UseCurrentAdress-Label"]');
	readonly useCurrentAddressInput: Locator = this.page.locator('input[name="CurrentAddress"]');
	readonly useDiffAddressRadioButton: Locator = this.page.locator('span[data-testid="Payment-BillingAddressSelection-DifferentAddress"]');
	readonly useDiffAddressLabel: Locator = this.page.locator('div[data-testid="Payment-BillingAddressSelection"] div[data-testid="radio-box-wrapper"]:nth-of-type(2) p');
	readonly useDiffAddressInput: Locator = this.page.locator('input[name="DifferentAddress"]');
	readonly yourAddressTitleLabel: Locator = this.page.locator('p[data-testid="Payment-BillingAddress-YourDifferentAdress-Title"]');
	readonly yourAddressPostcodeInput: Locator = this.page.locator('input[data-testid="input-postcodeAddress"]');
	readonly postcodeAriaDescribedByInput: Locator = this.yourAddressPostcodeInput.locator('..');
	readonly findAddressButton: Locator = this.page.locator('button[data-testid="PostcodeAddress-FindAddressBtn"]');
	readonly enterAddressManuallyLink: Locator = this.page.locator('div[data-testid="Payment-ManualAddressToggle"] a');
	readonly homeAddressRadioButton: Locator = this.page.locator('span[data-testid="Payment-addressSelection-PersonalAddress"]');
	readonly homeAddressLabel: Locator = this.page.locator('div[data-testid="Payment-addressSelection"] div[data-testid="radio-box-wrapper"]:nth-of-type(1) p');
	readonly businessAddressRadioButton: Locator = this.page.locator('span[data-testid="Payment-addressSelection-CompanyAddress"]');
	readonly businessAddressLabel: Locator = this.page.locator('div[data-testid="Payment-addressSelection"] div[data-testid="radio-box-wrapper"]:nth-of-type(2) p');
	readonly companyNameInput: Locator = this.page.locator('input[data-testid="input-companyName"]');
	readonly addressLineOneInput: Locator = this.page.locator('input[data-testid="input-addressLine1"]');
	readonly addressLineTwoInput: Locator = this.page.locator('input[data-testid="input-addressLine2"]');
	readonly addressLineThreeInput: Locator = this.page.locator('input[data-testid="input-addressLine3"]');
	readonly addressLineFourInput: Locator = this.page.locator('input[data-testid="input-addressLine4"]');
	readonly addressPostcodeInput: Locator = this.page.locator('input[data-testid="input-postalCode"]');
	readonly countryDropDown: Locator = this.page.locator('[data-testid="DropdownComp-Country-menuButton"], [data-testid="GuestDetails-CountrySelector-countrySelector"], [data-testid*="Payment-CountrySelector"]');
	readonly countryInput: Locator = this.page.locator('[data-testid="GuestDetails-CountrySelector"], [data-testid*="Payment-CountrySelector"] input[autocomplete="off"]');
	readonly countryOptionsList: Locator = this.page.locator('[data-testid="GuestDetails-CountrySelector"], [data-testid*="Payment-CountrySelector"] [role="list"], [data-testid*="Payment-CountrySelector"] [role="option"]');
	readonly bookingLocationInput: Locator = this.page.locator('input[data-testid="input-cityName"]');
	readonly addressSuggestionsList: Locator = this.page.locator('[data-testid="DropdownComp-PostcodeAddress-DropdownComp-entireList"] > *');
	readonly addressByPostCodeEntireList: Locator = this.page.locator('[data-testid="DropdownComp-PostcodeAddress-DropdownComp-entireList"]');
	readonly successInputIcon: Locator = this.page.locator('[data-testid="inputIconSuccess"]');
	readonly errorInputIcon: Locator = this.page.locator('[data-testid="inputIconError"]');
	readonly postcodeTooltipError: Locator = this.page.locator('[data-testid="input-postcodeAddress-FormErrorMessage"]');
	readonly paymentType = new PaymentTypeSectionComponent();

	/** Return a valid random suggestion index. */
	async getRandomAddressIndex(): Promise<number> { console.log('Get random address index'); return Math.floor(Math.random() * await this.addressSuggestionsList.count()); }

	// ######## UI actions/navigation ########

	/** Fill the available business-address fields. */
	async setManualBusinessAddressSection(data: Record<string, string | null> = {}): Promise<void> { console.log('Set manual business address section'); for (const [key, locator] of [['companyName', this.companyNameInput], ['addressLine1', this.addressLineOneInput], ['addressLine2', this.addressLineTwoInput], ['addressLine3', this.addressLineThreeInput], ['postCode', this.addressPostcodeInput]] as const) if (data[key]) await this.fillInput(locator, data[key]); if (data.countryCode) await this.setCountry(data.countryCode); if (data.countryCode?.toUpperCase() === Constants.GERMANY_COUNTRY_CODE) { if (data.city) await this.fillInput(this.bookingLocationInput, data.city); } else if (data.addressLine4) await this.fillInput(this.addressLineFourInput, data.addressLine4); }
	/** Fill the available home-address fields. */
	async setManualHomeAddressSection(data: Record<string, string | null> = {}): Promise<void> { console.log('Set manual home address section'); for (const [key, locator] of [['addressLine1', this.addressLineOneInput], ['addressLine2', this.addressLineTwoInput], ['addressLine3', this.addressLineThreeInput], ['postCode', this.addressPostcodeInput]] as const) if (data[key]) await this.fillInput(locator, data[key]); if (global.browser.options.locale === 'gb-en') { if (data.addressLine4) await this.fillInput(this.addressLineFourInput, data.addressLine4); } else if (data.city) await this.fillInput(this.bookingLocationInput, data.city); if (data.countryCode) await this.setCountry(data.countryCode); }
	/** Select a country from the billing-address country control. */
	async setCountry(countryCode: string): Promise<void> { console.log(`Set country ${countryCode}`); await this.countryDropDown.click(); await this.page.getByText(countryCode, { exact: false }).first().click(); }
	/** Select the different billing-address option. */
	async selectToUseDifferentAddress(): Promise<void> { console.log('Select to use different address'); await this.useDiffAddressRadioButton.click(); }
	/** Search billing addresses by postcode. */
	async searchAddressByPostCode(postcode: string): Promise<void> { console.log(`Search address by postcode ${postcode}`); await this.fillInput(this.yourAddressPostcodeInput, postcode); await this.findAddressButton.click(); }
	/** Open the manual billing-address form. */
	async clickToEnterTheAddressManually(): Promise<void> { console.log('Click to enter address manually'); await this.enterAddressManuallyLink.click(); }
	/** Select home billing address. */
	async clickHomeAddressRadioButton(): Promise<void> { console.log('Click home address radio button'); await this.homeAddressRadioButton.click(); }
	/** Select business billing address. */
	async clickBusinessAddressRadioButton(): Promise<void> { console.log('Click business address radio button'); await this.businessAddressRadioButton.click(); }
	/** Select a billing address suggestion by index. */
	async selectAddressFromSuggestionsList(addressIndex: number): Promise<void> { console.log('Select address from suggestions list'); await this.billingAddressContainer.locator('[role="option"], li, button').nth(addressIndex).click(); }
	/** Validate whether postcode suggestions are displayed. */
	async validateAddressSuggestions(isDisplayed = true): Promise<void> { console.log('Validate address suggestions'); await this.validateDisplayState(this.addressSuggestionsList.first(), 'Address suggestions', isDisplayed); }
	/** Validate the postcode field and placeholder. */
	async validateYourAddressPostcodeInput(): Promise<void> { console.log('Validate postcode input'); await expect(this.yourAddressPostcodeInput, 'Postcode input').toBeVisible(); await expect(this.yourAddressPostcodeInput, 'Postcode placeholder').toHaveAttribute('placeholder', await Strings.POST_CODE_NO_SPACE.name); }
	/** Validate the Find address button. */
	async validateFindAddressButton(): Promise<void> { console.log('Validate Find address button'); await expect(this.findAddressButton, 'Find address button').toBeEnabled(); await expect(this.findAddressButton, 'Find address button label').toContainText(await Strings.FIND_ADDRESS.name); }
	/** Validate the manual-address link. */
	async validateEnterAddressManuallyHyperlink(): Promise<void> { console.log('Validate enter-address-manually link'); await expect(this.enterAddressManuallyLink, 'Enter address manually link').toBeVisible(); await expect(this.enterAddressManuallyLink, 'Enter address manually label').toContainText(await Strings.OR_ENTER_YOUR_ADDRESS_MANUALLY.name); }
	/** Validate the Your address title. */
	async validateYourAddressTitleLabel(): Promise<void> { console.log('Validate Your address title'); await expect(this.yourAddressTitleLabel, 'Your address title').toContainText(await Strings.YOUR_ADDRESS.name); }
	/** Validate all controls in the different-address section. */
	async validateUseDiffAddressSectionElements(): Promise<void> { console.log('Validate different-address section'); await this.validateYourAddressTitleLabel(); await this.validateYourAddressPostcodeInput(); await this.validateFindAddressButton(); await this.validateEnterAddressManuallyHyperlink(); }
	/** Validate postcode success or error state. */
	async validateErrorTooltipForInvalidPostCode(isErrorState: boolean): Promise<void> { console.log(`Validate postcode error state=${isErrorState}`); await expect(await this.yourAddressPostcodeInput.getAttribute('aria-invalid'), 'Postcode aria-invalid state').toBe(isErrorState ? 'true' : null); if (isErrorState) { await expect(this.postcodeTooltipError, 'Postcode error message').toContainText(await Strings.PLEASE_ENTER_A_VALID_POSTCODE_INVALID.name); await expect(this.errorInputIcon, 'Postcode error icon').toBeVisible(); await expect(this.addressSuggestionsList, 'Invalid postcode suggestions').toHaveCount(0); } else await expect(this.successInputIcon, 'Postcode success icon').toBeVisible(); }
	/** Validate the home-address option and default selection. */
	async validateHomeAddressOption(): Promise<void> { console.log('Validate home address option'); await expect(this.homeAddressRadioButton, 'Home address radio button').toBeVisible(); await expect(this.homeAddressLabel, 'Home address label').toContainText(await Strings.HOME_ADDRESS.name); await expect(this.homeAddressRadioButton, 'Home address default selection').toBeChecked(); }
	/** Validate the business-address option. */
	async validateBusinessAddressOption(): Promise<void> { console.log('Validate business address option'); await expect(this.businessAddressRadioButton, 'Business address radio button').toBeVisible(); await expect(this.businessAddressLabel, 'Business address label').toContainText(await Strings.BUSINESS_ADDRESS.name); }
	/** Validate both manual address options. */
	async validateEnterYourAddressManuallyFields(): Promise<void> { console.log('Validate manual address options'); await this.validateHomeAddressOption(); await this.validateBusinessAddressOption(); }
	/** Validate current-address radio selection. */
	async validateUseCurrentAddressIsSelected(isSelected: boolean): Promise<void> { console.log(`Validate current address selected=${isSelected}`); await expect(this.useCurrentAddressInput, 'Use current address selection').toBeChecked({ checked: isSelected }); }
	/** Validate different-address radio selection. */
	async validateUseDifferentAddressIsSelected(isSelected: boolean): Promise<void> { console.log(`Validate different address selected=${isSelected}`); await expect(this.useDiffAddressInput, 'Use different address selection').toBeChecked({ checked: isSelected }); }
	/** Validate billing address radio controls. */
	async validateBillingAddressRadioButtons(): Promise<void> { console.log('Validate billing address radio buttons'); await expect(this.useCurrentAddressInput, 'Current address radio type').toHaveAttribute('type', 'radio'); await expect(this.useDiffAddressInput, 'Different address radio type').toHaveAttribute('type', 'radio'); }
	/** Validate all home-address form fields are visible. */
	async validateHomeAddressFormElements(): Promise<void> { console.log('Validate home address form elements'); for (const field of [this.addressLineOneInput, this.addressLineTwoInput, this.addressLineThreeInput, this.addressPostcodeInput, this.countryDropDown]) await expect(field, 'Home address form field').toBeVisible(); }
	/** Validate all business-address form fields are visible. */
	async validateBusinessAddressFormElements(): Promise<void> { console.log('Validate business address form elements'); for (const field of [this.companyNameInput, this.addressLineOneInput, this.addressLineTwoInput, this.addressLineThreeInput, this.addressPostcodeInput, this.countryDropDown]) await expect(field, 'Business address form field').toBeVisible(); }
	/** Validate supplied address values against visible input values. */
	async validateAddressFormBasedOnPostcode(suggestedAddressDetails: Record<string, string> = {}): Promise<void> { console.log('Validate address form based on postcode'); const fields: Record<string, Locator> = { addressLine1: this.addressLineOneInput, addressLine2: this.addressLineTwoInput, addressLine3: this.addressLineThreeInput, addressLine4: this.addressLineFourInput, postCode: this.addressPostcodeInput, postalCode: this.addressPostcodeInput, city: this.bookingLocationInput, cityName: this.bookingLocationInput, companyName: this.companyNameInput }; for (const [key, locator] of Object.entries(fields)) if (suggestedAddressDetails[key] !== undefined) await expect(locator, `${key} address value`).toHaveValue(suggestedAddressDetails[key]); }
	/** Validate a list of suggested addresses. */
	async validateListOfSuggestedAddresses(listOfSuggestedAddresses: string[]): Promise<void> { console.log('Validate suggested addresses list'); await expect(this.addressSuggestionsList, 'Suggested address count').toHaveCount(listOfSuggestedAddresses.length); for (const [index, address] of listOfSuggestedAddresses.entries()) await expect(this.addressSuggestionsList.nth(index), `Suggested address ${index + 1}`).toContainText(address); }
	/** Validate whether the selected address type matches supplied API details. */
	async validateAddressOptionSelectedBasedOnPostcode(suggestedAddressDetails: { companyName?: string }): Promise<void> { console.log('Validate address option selected from postcode'); if (suggestedAddressDetails.companyName) await expect(this.businessAddressRadioButton, 'Business address selected').toBeChecked(); else await expect(this.homeAddressRadioButton, 'Home address selected').toBeChecked(); }
	/** Validate one address input against an API field. */
	async validateInputFieldBasedOnApi({ placeholder, inputField, apiField }: { placeholder: string; inputField: Locator; apiField: string }): Promise<void> { console.log(`Validate address input ${placeholder}`); await expect(inputField, `${placeholder} value`).toHaveValue(apiField); await expect(inputField, `${placeholder} placeholder`).toHaveAttribute('placeholder', placeholder); }
	/** Validate the selected country value against API data. */
	async validateCountryBasedOnApi(suggestedAddressDetails: { country?: string; countryCode?: string }): Promise<void> { console.log('Validate billing address country'); const expectedCountry = suggestedAddressDetails.country ?? suggestedAddressDetails.countryCode; if (expectedCountry) await expect(this.countryDropDown, 'Billing address country').toContainText(expectedCountry); }

	// ######## UI validations ########

	/**
	 * Validate whether the billing-address section is displayed.
	 * @param isDisplayed Whether the section should be displayed.
	 */
	async validateBillingAddressSectionIsDisplayed(isDisplayed = true): Promise<void> { console.log('Validate billing address section is displayed'); await this.validateDisplayState(this.billingAddressContainer, 'Billing address section', isDisplayed); }
	/**
	 * Validate the billing-address section and any supplied address details.
	 * @param address Optional billing-address values expected in the section.
	 */
	async validateBillingAddressElements(address: { postalCode?: string; addressLine1?: string; cityName?: string } = {}): Promise<void> { console.log('Validate billing address elements'); await expect(this.billingAddressContainer, 'Billing address section').toBeVisible(); if (address.postalCode) await expect(this.billingAddressContainer, 'Billing address postcode').toContainText(address.postalCode); }
}