import { expect, type Locator, type Page } from "@playwright/test";

/**
 * Company Address section used on Add employee, Edit employee, Company details and Card management pages
 */
export class CompanyAddressSectionComponent {
  private readonly page: Page = global.page;
  // ######## UI elements/properties ########
  readonly companyAddress1Label: Locator = this.page.getByTestId( "AddressDetails-addressLine1", );
  readonly companyAddress2Label: Locator = this.page.getByTestId( "AddressDetails-addressLine2", );
  readonly companyAddress3Label: Locator = this.page.getByTestId( "AddressDetails-addressLine3", );
  readonly companyAddress4Label: Locator = this.page.getByTestId( "AddressDetails-addressLine4", );
  readonly companyAddress5Label: Locator = this.page.getByTestId( "AddressDetails-addressLine5", );
  readonly companyPostCodeLabel: Locator = this.page.getByTestId( "AddressDetails-postCode", );
  readonly companyCountryNameLabel: Locator = this.page.getByTestId( "AddressDetails-countryName", );
  readonly companyPostCodeInput: Locator = this.page.getByTestId( "postCode-Form-Input", );
  readonly companyPostCodeInputErrorLabel: Locator = this.page.getByTestId( "postCode-Error-Tooltip", );
  readonly findAddressButton: Locator = this.page.getByTestId( "CompanyAddressForm-findAddressButton", );
  readonly selectedAddressButton: Locator = this.page.getByTestId( "selectAddress-IB-Form-Select-Button", );
  readonly selectedAddressLabel: Locator = this.selectedAddressButton.locator("span");
  readonly addressDropDownItems: Locator = this.page.locator( 'div[data-side="bottom"] button[data-testid*="selectAddress-"] span', );
  readonly addressByPostCodeEntireList: Locator = this.page.getByTestId( "selectAddress-IB-Form-Select-Dropdown", );
  readonly enterAddressManuallyButton: Locator = this.page.getByTestId( "IB-ManualAddress-Button", );
  readonly addressByPostCode = '//div[@data-side="bottom"]/button[{{index}}]';
  readonly companyAddress1Input: Locator = this.page.locator( 'input[data-testid*="Address-Line-1-Form-Input"]', );
  readonly companyAddress1InputErrorLabel: Locator = this.page.locator( 'div[data-testid*="Address-Line-1-Error-Tooltip"]', );
  readonly companyAddress2Input: Locator = this.page.locator( 'input[data-testid*="Address-Line-2-Form-Input"]', );
  readonly companyAddress3Input: Locator = this.page.locator( 'input[data-testid*="Address-Line-3-Form-Input"]', );
  readonly companyAddress4Input: Locator = this.page.locator( 'input[data-testid*="Address-Line-4-Form-Input"]', );
  readonly companyAddress5Input: Locator = this.page.locator( 'input[data-testid*="Address-Line-5-Form-Input"]', );
  readonly companyManualPostCodeInput: Locator = this.page.locator( 'input[data-testid*="Manual-Postcode-Form-Input"]', );
  readonly companyManualPostCodeInputErrorLabel: Locator = this.page.locator( 'div[data-testid*="Manual-Postcode-Error-Tooltip"]', );
  readonly selectedCountryButton: Locator = this.page.locator( 'button[data-testid*="Manual-Countries-IB-Form-Select-Button"]', );
  readonly selectedCountryNameLabel: Locator = this.selectedCountryButton.locator("span");
  readonly selectedCountryFlagIcon: Locator = this.page.locator( 'div[data-testid*="Manual-Countries-IB-Form-Select"] img[alt="Select Icon"]', );
  readonly countryItemsList: Locator = this.page.locator( 'div[data-testid*="Manual-Countries-IB-Form-Select-Dropdown"]', );
  readonly countryDropDownItems: Locator = this.page.locator( 'button[data-testid*="Manual-Countries-"]', );
  readonly countryDropDownItemsFlagIcon: Locator = this.countryDropDownItems.locator("img");
  // ######## UI actions/navigation ########
  /** Fill. */
  private async fill(
    locator: Locator,
    value: string,
    pressTab = false,
  ): Promise<void> {
    await locator.fill(value);
    if (pressTab) await locator.press("Tab");
  }
  /** Click Find address button. */
  async clickFindAddressButton(): Promise<void> {
    console.log("Click Find address button");
    await this.findAddressButton.click();
  }
  /** Click select suggested addresses button. */
  async clickSelectAddressButton(): Promise<void> {
    console.log("Click Select address button");
    await this.selectedAddressButton.click();
  }
  /** Click Enter address manually button. */
  async clickEnterAddressManuallyButton(): Promise<void> {
    console.log("Click Enter address manually button");
    await this.enterAddressManuallyButton.click();
  }
  /** Set Company post code input. */
  async setPostCodeInput({
    postCode,
    pressTab = false,
  }: {
    postCode: string;
    pressTab?: boolean;
  }): Promise<void> {
    console.log(`Post Code Input = ${postCode}`);
    await this.fill(this.companyPostCodeInput, postCode, pressTab);
  }
  /** Select Address Drop Down Item By Index. */
  async selectAddressDropDownItemByIndex(index = 0): Promise<void> {
    console.log(`Select address item by index=${index}`);
    await this.addressDropDownItems.nth(index).click();
  }
  /** Set Company address 1 input. */
  async setAddress1Input({
    address1,
    pressTab = false,
  }: {
    address1: string;
    pressTab?: boolean;
  }): Promise<void> {
    console.log(`Address 1 Input = ${address1}`);
    await this.fill(this.companyAddress1Input, address1, pressTab);
  }
  /** Set Company address 2 input. */
  async setAddress2Input({
    address2,
    pressTab = false,
  }: {
    address2: string;
    pressTab?: boolean;
  }): Promise<void> {
    console.log(`Address 2 Input = ${address2}`);
    await this.fill(this.companyAddress2Input, address2, pressTab);
  }
  /** Set Company address 3 input. */
  async setAddress3Input({
    address3,
    pressTab = false,
  }: {
    address3: string;
    pressTab?: boolean;
  }): Promise<void> {
    console.log(`Address 3 Input = ${address3}`);
    await this.fill(this.companyAddress3Input, address3, pressTab);
  }
  /** Set Company address 4 input. */
  async setAddress4Input({
    address4,
    pressTab = false,
  }: {
    address4: string;
    pressTab?: boolean;
  }): Promise<void> {
    console.log(`Address 4 Input = ${address4}`);
    await this.fill(this.companyAddress4Input, address4, pressTab);
  }
  /** Set Company address 5 input. */
  async setAddress5Input({
    address5,
    pressTab = false,
  }: {
    address5: string;
    pressTab?: boolean;
  }): Promise<void> {
    console.log(`Address 5 Input = ${address5}`);
    await this.fill(this.companyAddress5Input, address5, pressTab);
  }
  /** Toggle Country drop down. */
  async setManualPostCodeInput({
    manualPostCode,
    pressTab = false,
  }: {
    manualPostCode: string;
    pressTab?: boolean;
  }): Promise<void> {
    console.log(`Manual Post Code Input = ${manualPostCode}`);
    await this.fill(this.companyManualPostCodeInput, manualPostCode, pressTab);
  }
  /** Select Country Drop Down Item By Index. */
  async toggleCountryDropDown({
    shouldBeOpened = true,
  }: { shouldBeOpened?: boolean } = {}): Promise<void> {
    console.log(`Toggle country drop down: shouldBeOpened=${shouldBeOpened}`);
    await this.selectedCountryButton.click();
  }
  /** Select Country Drop Down by value. */
  async selectCountryDropDownItemByIndex(index = 0): Promise<void> {
    console.log(`Select country item by index=${index}`);
    await this.countryDropDownItems.nth(index).click();
  }
  /** Set Company address input. */
  async selectCountryDropDownLabelItemByCountryCode(
    countryCode = "GB",
  ): Promise<void> {
    console.log(`Chosen country label by value=${countryCode}`);
    await this.countryDropDownItems
      .filter({ has: this.page.locator(`img[alt="${countryCode}"]`) })
      .click();
  }
  /** Return all Countries from dropdown trimmed. */
  async setCompanyAddressInput({
    inputAddress,
    isEnCompany = true,
  }: {
    inputAddress: Record<string, string>;
    isEnCompany?: boolean;
  }): Promise<void> {
    console.log("Set Company address input");
    if (isEnCompany)
      await this.setPostCodeInput({ postCode: inputAddress.postCode });
    await this.setAddress1Input({ address1: inputAddress.addressLine1 });
    await this.setAddress2Input({ address2: inputAddress.addressLine2 });
    await this.setAddress3Input({ address3: inputAddress.addressLine3 });
    await this.setAddress4Input({ address4: inputAddress.addressLine4 });
    if (isEnCompany)
      await this.setAddress5Input({ address5: inputAddress.addressLine5 });
    await this.setManualPostCodeInput({
      manualPostCode: inputAddress.postalCode,
    });
  }
  /** Validate Postcode input. */
  async trimCountryDropdownItems(): Promise<string[]> {
    console.log("Return all countries from dropdown trimmed");
    return Promise.all(await this.countryDropDownItems.allTextContents()).then(
      (items) => items.map((item) => item.trim()),
    );
  }
  // ######## UI validations ########
  /** Validate Select address input. */
  async validatePostCodeInput(postCode: string, isValid = true): Promise<void> {
    console.log("Validate Postcode input");
    await expect(this.companyPostCodeInput, "Postcode input").toHaveValue( postCode, );
    if (!isValid)
      await expect( this.companyPostCodeInputErrorLabel, "Postcode error", ).toBeVisible();
  }
  /** Validate Address 1 input. */
  async validateSelectAddressInput(searchedAddress: string): Promise<void> {
    console.log(`Validate Select address input ${searchedAddress}`);
    await expect(this.selectedAddressLabel, "Selected address").toContainText( searchedAddress, );
  }
  /** Validate Address 2 input. */
  async validateAddress1Input(address1: string): Promise<void> {
    console.log("Validate Address 1 input");
    await expect(this.companyAddress1Input, "Address 1 input").toHaveValue( address1, );
  }
  /** Validate Address 3 input. */
  async validateAddress2Input(address2: string): Promise<void> {
    console.log("Validate Address 2 input");
    await expect(this.companyAddress2Input, "Address 2 input").toHaveValue( address2, );
  }
  /** Validate Address 4 input. */
  async validateAddress3Input(address3: string): Promise<void> {
    console.log("Validate Address 3 input");
    await expect(this.companyAddress3Input, "Address 3 input").toHaveValue( address3, );
  }
  /** Validate Address 5 input. */
  async validateAddress4Input(
    address4: string,
    _isEnCompany = true,
  ): Promise<void> {
    console.log("Validate Address 4 input");
    await expect(this.companyAddress4Input, "Address 4 input").toHaveValue( address4, );
  }
  /** Validate manual Post code input. */
  async validateAddress5Input(address5: string): Promise<void> {
    console.log("Validate Address 5 input");
    await expect(this.companyAddress5Input, "Address 5 input").toHaveValue( address5, );
  }
  /** Validate the list of suggested addresses based on postcode. */
  async validateManualPostCodeInput(manualPostCode: string): Promise<void> {
    console.log("Validate manual Post code input");
    await expect( this.companyManualPostCodeInput, "Manual postcode", ).toHaveValue(manualPostCode);
  }
  /** Validate country suggestion list. */
  async validateListOfSuggestedAddresses(
    _addresses: { addressText: string }[],
  ): Promise<void> {
    console.log("Validate suggested addresses");
    await expect( this.addressByPostCodeEntireList, "Suggested addresses", ).toBeVisible();
  }
  /** Validate the selected country. */
  async validateCountrySuggestionList(): Promise<void> {
    console.log("Validate country suggestion list");
    await expect( this.countryItemsList, "Country suggestion list", ).toBeVisible();
  }
  /** Validate selected country. */
  async validateSelectedCountry({
    countryName,
  }: {
    countryName?: string;
    countryNameFromSrc?: string;
    isEnCompany?: boolean;
  }): Promise<void> {
    console.log(`Validate selected country: countryName=${countryName}`);
    await expect( this.selectedCountryFlagIcon, "Selected country flag", ).toBeVisible();
    if (countryName)
      await expect( this.selectedCountryNameLabel, "Selected country name", ).toHaveText(countryName);
  }
}
