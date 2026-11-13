import { expect, type Locator } from "@playwright/test";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { BasePibPage } from "./basePib.page";

/**
 * InnBusiness application > Create your InnBusiness account page
 */
export class CreateYourInnBusinessAccountPage extends BasePibPage {
  readonly url = "account/register";
  // ######## UI elements/properties ########
  readonly headerLabel: Locator = this.page.getByTestId("wizard-title");
  readonly firstSubtitleLabel: Locator = this.page.locator("p.text-neutral-900");
  readonly secondSubtitleLabel: Locator = this.firstSubtitleLabel.locator("~ p");
  readonly logInLink: Locator = this.firstSubtitleLabel.locator("~ p a");
  readonly emailAddressInput: Locator = this.page.getByTestId( "emailAddress-Form-Input", );
  readonly emailAddressErrorLabel: Locator = this.page.getByTestId( "emailAddress-Error-Tooltip", );
  readonly companyNameInput: Locator = this.page.getByTestId( "companyName-Form-Input", );
  readonly companyNameErrorLabel: Locator = this.page.getByTestId( "companyName-Error-Tooltip", );
  readonly postcodeInput: Locator = this.page.getByTestId( "postCode-Form-Input", );
  readonly postcodeErrorLabel: Locator = this.page.getByTestId( "postCode-Error-Tooltip", );
  readonly findAddressButton: Locator = this.page.getByTestId( "RegisterForm-findAddressButton", );
  readonly selectAddressButton: Locator = this.page.getByTestId( "selectAddress-IB-Form-Select-Button", );
  readonly selectAddressLabel: Locator = this.page.getByTestId( "selectAddress-Label", );
  readonly addressByPostCodeEntireList: Locator = this.page.getByTestId( "selectAddress-IB-Form-Select-Dropdown", );
  readonly addressByPostCodeList: Locator = this.page.locator( 'button[data-testid^="selectAddress-"]', );
  readonly enterAddressManuallyLabel: Locator = this.page.locator( '[data-testid="RegisterForm-findAddress"] ~ p', );
  readonly enterAddressManuallyButton: Locator = this.enterAddressManuallyLabel.locator( 'button[data-testid="RegisterPage-Button"]', );
  readonly enterAddressManuallyHeaderLabel: Locator = this.page.locator("h3.font-bold");
  readonly enterAddressManuallySubtitleLabel: Locator = this.enterAddressManuallyHeaderLabel.locator("~ p");
  readonly returnToPostcodeLookupButton: Locator = this.page.locator( 'div button[data-testid="RegisterPage-Button"]', );
  readonly addressLine1Input: Locator = this.page.getByTestId( "Address-Line-1-Form-Input", );
  readonly addressLine1ErrorLabel: Locator = this.page.getByTestId( "Address-Line-1-Error-Tooltip", );
  readonly addressLine2Input: Locator = this.page.getByTestId( "Address-Line-2-Form-Input", );
  readonly addressLine3Input: Locator = this.page.getByTestId( "Address-Line-3-Form-Input", );
  readonly addressLine4Input: Locator = this.page.getByTestId( "Address-Line-4-Form-Input", );
  readonly addressLine5Input: Locator = this.page.getByTestId( "Address-Line-5-Form-Input", );
  readonly manualPostcodeInput: Locator = this.page.getByTestId( "Manual-Postcode-Form-Input", );
  readonly manualPostcodeErrorLabel: Locator = this.page.getByTestId( "Manual-Postcode-Error-Tooltip", );
  readonly countryButton: Locator = this.page.getByTestId( "Manual-Countries-IB-Form-Select-Button", );
  readonly selectedCountryFlagIcon: Locator = this.page.locator( '[data-testid="Manual-Countries-IB-Form-Select"] img', );
  readonly selectedCountryNameLabel: Locator = this.countryButton.locator("span");
  readonly countryDropDownItems: Locator = this.page.locator( 'button[data-testid*="Manual-Countries-"]', );
  readonly uniqueTaxpayerReferenceInput: Locator = this.page.getByTestId( "uniqueTaxpayerReference-Form-Input", );
  readonly uniqueTaxpayerReferenceErrorLabel: Locator = this.page.getByTestId( "uniqueTaxpayerReference-Error-Tooltip", );
  readonly optInCheckbox: Locator = this.page.locator("button#optIn");
  readonly optInLabel: Locator = this.optInCheckbox.locator("~ label");
  readonly continueButton: Locator = this.page.locator( 'div ~ button[data-testid="RegisterPage-Button"]', );
  /** Get dropdown option for address selection by company name. */
  getAddressByCompanyNameDropdownOption(
    companyName: string,
  ): Locator {
    return this.page.locator(
      `//span[contains(text(),${JSON.stringify(companyName)})]/parent::button`,
    );
  }
  // ######## UI actions/navigation ########
  /** Open IB Create your InnBusiness account page. */
  async open(): Promise<void> {
    console.log("Open Create your InnBusiness account page");
    await this.openPath(this.url);
    await this.validatePage();
  }
  /** Click Find address button. */
  async clickFindAddressButton(): Promise<void> {
    console.log("Click Find address button");
    await this.findAddressButton.click();
    await expect( this.selectAddressButton, "Select address button after find address", ).toBeVisible();
  }
  /** Click Select address button. */
  async clickSelectAddressButton(): Promise<void> {
    console.log("Click Select address button");
    await this.selectAddressButton.click();
  }
  /** Click address from address suggested list. */
  async clickAddressFromAddressSuggestedList(
    index: number,
  ): Promise<void> {
    console.log(`Click address from address suggested list=${index}`);
    await this.addressByPostCodeList.nth(index).click();
  }
  /** Click Enter address manually button. */
  async clickEnterAddressManuallyButton(): Promise<void> {
    console.log("Click Enter address manually button");
    await this.enterAddressManuallyButton.click();
    await expect( this.addressLine1Input, "Manual address line 1 input", ).toBeVisible();
  }
  /** Click Return to postcode lookup button. */
  async clickReturnToPostcodeLookupButton(): Promise<void> {
    console.log("Click Return to postcode lookup button");
    await this.returnToPostcodeLookupButton.click();
  }
  /** Click Continue button. */
  async clickContinueButton({
    shouldBeRedirected = true,
  }: { shouldBeRedirected?: boolean } = {}): Promise<void> {
    console.log("Click Continue button");
    await this.continueButton.click();
    if (shouldBeRedirected)
      await expect( this.continueButton, "Continue button after successful submission", ).not.toBeVisible();
  }
  /** Click outside Continue button. */
  async clickOutsideOfContinueButton(): Promise<void> {
    console.log("Click outside Continue button");
    await this.continueButton.blur();
  }
  /** Click outside Manual postcode input. */
  async clickOutsideOfManualPostcodeInput(): Promise<void> {
    console.log("Click outside Manual postcode input");
    await this.manualPostcodeInput.blur();
  }
  /** Set a form input value and optionally leave the field. */
  private async setInput(
    input: Locator,
    value: string,
    pressTab: boolean,
  ): Promise<void> {
    await input.fill(value);
    if (pressTab) await input.press("Tab");
  }
  /** Set value to email address input. */
  async setEmailAddressInput({
    value,
    pressTab = true,
  }: {
    value: string;
    pressTab?: boolean;
  }): Promise<void> {
    console.log(`Set value - ${value} to email address`);
    await this.setInput(this.emailAddressInput, value, pressTab);
  }
  /** Set value to company name input. */
  async setCompanyNameInput({
    value,
    pressTab = true,
  }: {
    value: string;
    pressTab?: boolean;
  }): Promise<void> {
    console.log(`Set value - ${value} to company name`);
    await this.setInput(this.companyNameInput, value, pressTab);
  }
  /** Set value to postcode input. */
  async setPostcodeInput({
    value,
    pressTab = true,
  }: {
    value: string;
    pressTab?: boolean;
  }): Promise<void> {
    console.log(`Set value - ${value} to postcode`);
    await this.setInput(this.postcodeInput, value, pressTab);
  }
  /** Set value to address 1 input. */
  async setAddress1Input({
    value,
    pressTab = true,
  }: {
    value: string;
    pressTab?: boolean;
  }): Promise<void> {
    console.log(`Set value - ${value} to address 1`);
    await this.setInput(this.addressLine1Input, value, pressTab);
  }
  /** Set value to address 2 input. */
  async setAddress2Input({
    value,
    pressTab = true,
  }: {
    value: string;
    pressTab?: boolean;
  }): Promise<void> {
    console.log(`Set value - ${value} to address 2`);
    await this.setInput(this.addressLine2Input, value, pressTab);
  }
  /** Set value to address 3 input. */
  async setAddress3Input({
    value,
    pressTab = true,
  }: {
    value: string;
    pressTab?: boolean;
  }): Promise<void> {
    console.log(`Set value - ${value} to address 3`);
    await this.setInput(this.addressLine3Input, value, pressTab);
  }
  /** Set value to address 4 input. */
  async setAddress4Input({
    value,
    pressTab = true,
  }: {
    value: string;
    pressTab?: boolean;
  }): Promise<void> {
    console.log(`Set value - ${value} to address 4`);
    await this.setInput(this.addressLine4Input, value, pressTab);
  }
  /** Set value to manual postcode input. */
  async setManualPostcodeInput({
    value,
    pressTab = true,
  }: {
    value: string;
    pressTab?: boolean;
  }): Promise<void> {
    console.log(`Set value - ${value} to manual postcode`);
    await this.setInput(this.manualPostcodeInput, value, pressTab);
  }
  /** Set value to unique taxpayer reference input. */
  async setUniqueTaxpayerReferenceInput({
    value,
    pressTab = true,
  }: {
    value: string;
    pressTab?: boolean;
  }): Promise<void> {
    console.log(`Set value - ${value} to unique taxpayer reference`);
    await this.setInput(this.uniqueTaxpayerReferenceInput, value, pressTab);
  }
  /** Opt-in for marketing promotions by clicking the checkbox. */
  async clickOptInCheckbox(): Promise<void> {
    console.log("Click Opt-in checkbox");
    await this.optInCheckbox.click();
    await expect( this.optInCheckbox, "Opt in checkbox is checked", ).toHaveAttribute("data-state", "checked");
  }
  /** Select Country Drop Down by value. */
  async selectCountryDropDownLabelItemByCountryCode(
    countryCode = "DE",
  ): Promise<void> {
    console.log(`Select country code=${countryCode}`);
    await this.countryButton.click();
    await this.page
      .locator(
        `button[data-testid*="Manual-Countries-"] img[alt*="${countryCode}"]`,
      )
      .click();
  }
  /** Select address by company name from the dropdown. */
  async selectAddressByCompanyName(
    companyName: string,
  ): Promise<void> {
    console.log(`Select address by company name=${companyName}`);
    if ((await this.companyNameInput.inputValue()) !== companyName) {
      await this.clickSelectAddressButton();
      await this.getAddressByCompanyNameDropdownOption(companyName).click();
      await expect( this.companyNameInput, `Company name input value is ${companyName}`, ).toHaveValue(companyName);
    }
  }
  // ######## UI validations ########
  /** Check we reached the current page by checking a specific element from the page. */
  async validatePage(): Promise<void> {
    console.log("Validate Create your InnBusiness account page");
    await this.validatePageMarker(
      this.continueButton,
      "Create InnBusiness account",
    );
  }
  /** Validate a registration input's placeholder and value. */
  private async validateInput(
    input: Locator,
    description: string,
    placeholder: string,
    value = "",
  ): Promise<void> {
    await expect(input, `${description} placeholder`).toHaveAttribute( "placeholder", placeholder, );
    await expect(input, `${description} value`).toHaveValue(value);
  }
  /** Validate email address input. */
  async validateEmailAddressInputValueAndPlaceholder({
    placeholder,
    value = "",
  }: { placeholder?: string; value?: string } = {}): Promise<void> {
    console.log("Validate email address input");
    await this.validateInput(
      this.emailAddressInput,
      "Email address",
      placeholder ??
        (await IbStrings.CREATE_YOUR_IB_ACCOUNT_EMAIL_ADDRESS.name),
      value,
    );
  }
  /** Validate company name input. */
  async validateCompanyNameInputValueAndPlaceholder({
    placeholder,
    value = "",
  }: { placeholder?: string; value?: string } = {}): Promise<void> {
    console.log("Validate company name input");
    await this.validateInput(
      this.companyNameInput,
      "Company name",
      placeholder ?? (await IbStrings.CREATE_YOUR_IB_ACCOUNT_COMPANY_NAME.name),
      value,
    );
  }
  /** Validate postcode input. */
  async validatePostcodeInputValueAndPlaceholder({
    value = "",
  }: { value?: string } = {}): Promise<void> {
    console.log("Validate postcode input");
    await this.validateInput(
      this.postcodeInput,
      "Postcode",
      await IbStrings.CREATE_YOUR_IB_ACCOUNT_POSTCODE.name,
      value,
    );
  }
  /** Validate field error visibility. */
  private async validateError(
    error: Locator,
    description: string,
    expectedText: string,
    isDisplayed: boolean,
  ): Promise<void> {
    if (isDisplayed) await expect(error, description).toHaveText(expectedText);
    else await expect(error, description).not.toBeVisible();
  }
  /** Validate email address input error. */
  async validateEmailAddressInputError({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log("Validate email address input error");
    await this.validateError(
      this.emailAddressErrorLabel,
      "Email address input error",
      await IbStrings.SIGN_IN_EMAIL_ERROR.name,
      isDisplayed,
    );
  }
  /** Validate company name input error. */
  async validateCompanyNameInputError({
    isDisplayed = true,
    errorText,
  }: { isDisplayed?: boolean; errorText?: string } = {}): Promise<void> {
    console.log("Validate company name input error");
    await this.validateError(
      this.companyNameErrorLabel,
      "Company name input error",
      errorText ?? (await IbStrings.INVALID_INPUT_VALUE.name),
      isDisplayed,
    );
  }
  /** Validate postcode input error. */
  async validatePostcodeInputError({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log("Validate postcode input error");
    await this.validateError(
      this.postcodeErrorLabel,
      "Postcode input error",
      await IbStrings.INVALID_POSTCODE.name,
      isDisplayed,
    );
  }
  /** Validate Create your InnBusiness account elements. */
  async validateCreateYourInnBusinessAccountElements({
    accountData,
  }: {
    accountData: {
      emailAddress: string;
      companyName: string;
      postcode: string;
      optIn: boolean;
    };
  }): Promise<void> {
    console.log("Validate Create your InnBusiness account elements");
    await expect(this.headerLabel, "Registration header").toHaveText( await IbStrings.CREATE_YOUR_IB_ACCOUNT.name, );
    await this.validateEmailAddressInputValueAndPlaceholder({
      value: accountData.emailAddress,
    });
    await this.validateCompanyNameInputValueAndPlaceholder({
      value: accountData.companyName,
    });
    await this.validatePostcodeInputValueAndPlaceholder({
      value: accountData.postcode,
    });
    await expect(this.optInCheckbox, "Opt in checkbox").toHaveAttribute( "aria-checked", String(accountData.optIn), );
    await expect(this.continueButton, "Continue button").toHaveText( await IbStrings.CREATE_YOUR_IB_ACCOUNT_CONTINUE.name, );
  }
}
