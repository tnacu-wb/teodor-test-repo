import { expect, type Locator } from "@playwright/test";
import {
  PersonalDetailsSectionComponent,
  RegistrationQuestionsFromComponent,
} from "../../components/pib";
import { type AnsweredQuestions } from "../../api/response/answeredQuestions";
import { type NewBookerDetails } from "../../test-data/booker";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { Constants } from "../../test-data/constants";
import { Locales } from "../../test-data/locales";
import { EncryptionUtils } from "../../utils/encryptionUtils";
import { BasePibPage } from "./basePib.page";

/**
 * Activate Your Account page
 */
export class ActivateYourAccountPage extends BasePibPage {
  readonly url = "account/activate?key=";
  // ######## UI elements/properties ########
  readonly headerLabel: Locator = this.page.getByTestId("wizard-title");
  readonly subtitleLabel: Locator = this.page.getByTestId( "EmployeeActivationPage-EmployeeActivation-Title", );
  readonly nameLabel: Locator = this.page.locator("h3.font-bold").nth(0);
  readonly contactDetailsLabel: Locator = this.page .locator("h3.font-bold") .nth(1);
  readonly companyEmailAddressInput: Locator = this.page.getByTestId( "CompanyEmailAddress-Form-Input", );
  readonly addressLabel: Locator = this.page.locator("h3.font-bold").nth(2);
  readonly postcodeInput: Locator = this.page.getByTestId( "postCode-Form-Input", );
  readonly findAddressButton: Locator = this.page.getByTestId( "CompanyAddressForm-findAddressButton", );
  readonly enterAddressManuallyButton: Locator = this.page.getByTestId( "IB-ManualAddress-Button", );
  readonly addressLine1Input: Locator = this.page.getByTestId( "Address-Line-1-Form-Input", );
  readonly addressLine2Input: Locator = this.page.getByTestId( "Address-Line-2-Form-Input", );
  readonly addressLine3Input: Locator = this.page.getByTestId( "Address-Line-3-Form-Input", );
  readonly addressLine4Input: Locator = this.page.getByTestId( "Address-Line-4-Form-Input", );
  readonly addressLine5Input: Locator = this.page.getByTestId( "Address-Line-5-Form-Input", );
  readonly manualPostcodeInput: Locator = this.page.getByTestId( "Manual-Postcode-Form-Input", );
  readonly countryLabel: Locator = this.page.getByTestId( "Manual-Countries-IB-Form-Select" ).locator("span");
  readonly countryImage: Locator = this.page.getByTestId( "Manual-Countries-IB-Form-Select" ).locator("img");
  readonly createPasswordLabel: Locator = this.page .locator("h3.font-bold") .nth(3);
  readonly passwordInput: Locator = this.page.getByTestId( "CreatePassword-Form-Input", );
  readonly showHidePasswordButton: Locator = this.passwordInput.locator("~ button");
  readonly passwordRulesTitleLabel: Locator = this.page.getByTestId( "EmployeeActivationPage-list-title", );
  readonly passwordRulesLabel: Locator = this.page.getByTestId( "EmployeeActivationPage-list-content", );
  readonly contactInfoLabel: Locator = this.page.getByTestId( "EmployeeActivationPage-Contact-Info", );
  readonly createAccountButton: Locator = this.page.getByTestId("footer-button");
  readonly registrationQuestions = new RegistrationQuestionsFromComponent();
  readonly personalDetails = new PersonalDetailsSectionComponent();
  // ######## UI actions/navigation ########
  /** Open IB Activate your account page. */
  async open(
    keyValue: string,
  ): Promise<void> {
    console.log("Open IB Activate your account page");
    await this.page.context().clearCookies();
    await this.openPath(`${this.url}${keyValue}`, false);
    await this.validatePage();
  }
  /** Set Postcode input. */
  async setPostcodeInput({
    postcode,
  }: {
    postcode: string;
  }): Promise<void> {
    console.log(`Postcode Input: ${postcode}`);
    await this.postcodeInput.fill(postcode);
  }
  /** Click on Find address button. */
  async clickFindAddressButton(): Promise<void> {
    console.log("Click Find address button");
    await this.findAddressButton.click();
  }
  /** Set Address line 1 input. */
  async setAddressLine1Input({
    addressLine1,
  }: {
    addressLine1: string;
  }): Promise<void> {
    console.log(`Set Address line 1: ${addressLine1}`);
    await this.addressLine1Input.fill(addressLine1);
  }
  /** Set Manual postcode input. */
  async setManualPostcodeInput({
    postcode,
  }: {
    postcode: string;
  }): Promise<void> {
    console.log(`Manual postcode input: ${postcode}`);
    await this.manualPostcodeInput.fill(postcode);
  }
  /** Set password input. */
  async setPasswordInput({
    password,
  }: {
    password: string;
  }): Promise<void> {
    console.log("Set activation password");
    await this.passwordInput.fill(password);
  }
  /** Click activate/create account button. */
  async clickCreateAccountButton({
    isManagerUser = false,
  }: { isManagerUser?: boolean } = {}): Promise<void> {
    console.log("Click create account button");
    await this.createAccountButton.scrollIntoViewIfNeeded();
    await this.createAccountButton.click();
    await expect(this.page, "Activated account redirect").toHaveURL( isManagerUser ? /welcome/ : /homepage/, );
  }
  /** Set User data and create account. */
  async setUserDataAndCreateAccount({
    userData,
    shouldSetAddress = true,
    isManagerUser = false,
  }: {
    userData: NewBookerDetails;
    shouldSetAddress?: boolean;
    isManagerUser?: boolean;
  }): Promise<void> {
    console.log("Set user data and create account");
    await this.personalDetails.setUserTitle(userData.title ?? "");
    await this.personalDetails.setEmployeeFirstName(userData.firstName);
    await this.personalDetails.setEmployeeLastName(userData.lastName);
    await this.personalDetails.setMobilePhoneInput(userData.mobilePhone);
    if (shouldSetAddress && (await this.postcodeInput.inputValue()) === "") {
      if (Locales.isEnglishWebsite()) {
        await this.setPostcodeInput({
          postcode: userData.address.postalCode ?? "",
        });
        await this.clickFindAddressButton();
      } else {
        await this.setAddressLine1Input({ addressLine1: userData.address.addressLine1 ?? "" });
        await this.setManualPostcodeInput({ postcode: Constants.VALID_DE_POSTCODE });
      }
    }
    await this.setPasswordInput({
      password: EncryptionUtils.decode(userData.password),
    });
    await this.clickCreateAccountButton({ isManagerUser });
  }
  /** Set password and click create your account button. */
  async setPasswordAndActivateAccount(
    password: string,
    isManagerUser = false,
  ): Promise<void> {
    console.log("Set password and click Create account button");
    await this.setPasswordInput({ password: EncryptionUtils.decode(password) });
    await this.clickCreateAccountButton({ isManagerUser });
  }
  // ######## UI validations ########
  /** Check we reached the current page by checking a specific element from the page. */
  async validatePage(): Promise<void> {
    console.log("Validate Activate your account page");
    await this.validatePageMarker(this.subtitleLabel, "Activate your account");
  }
  /** Validate the company email address input. */
  async validateCompanyEmailAddressInputValueAndPlaceholder({
    placeholder,
    value = "",
  }: { placeholder?: string; value?: string } = {}): Promise<void> {
    console.log("Validate company email address input");
    await expect( this.companyEmailAddressInput, "Company email address placeholder", ).toHaveAttribute( "placeholder", placeholder ?? (await IbStrings.ACTIVATE_YOUR_ACCOUNT_COMPANY_EMAIL_ADDRESS.name), );
    await expect( this.companyEmailAddressInput, "Company email address value", ).toHaveValue(value);
  }
  /** Validate the password input. */
  async validatePasswordInputValueAndPlaceholder({
    value = "",
  }: { value?: string } = {}): Promise<void> {
    console.log("Validate activation password input");
    await expect( this.passwordInput, "Activation password placeholder", ).toHaveAttribute( "placeholder", await IbStrings.ACTIVATE_YOUR_ACCOUNT_PASSWORD.name, );
    await expect(this.passwordInput, "Activation password value").toHaveValue( value, );
  }
  /** Validate a manual address input. */
  async validateAddressInput(input: Locator, value: string, placeholder: string): Promise<void> {
    console.log(`Validate manual address field: ${placeholder}`);
    await expect(input, `${placeholder} placeholder`).toHaveAttribute("placeholder", placeholder);
    await expect(input, `${placeholder} value`).toHaveValue(value);
  }
  /** Validate the selected country in the manual address form. */
  async validateSelectedCountry(countryName?: string): Promise<void> {
    console.log(`Validate selected country: ${countryName ?? "default"}`);
    await expect(this.countryLabel, "Country field").toBeVisible();
    await expect(this.countryImage, "Selected country flag").toBeVisible();
    if (countryName) await expect(this.countryLabel, "Selected country name").toContainText(countryName);
  }
  /** Validate Title dropdown values. */
  async validateTitleDropdownValues(): Promise<void> {
    console.log("Validate Title dropdown values");
    const actual = (await this.personalDetails.getTitleDropdownValues()).sort();
    const expected = (await IbStrings.WELCOME_TO_INNBUSINESS_TITLE_VALUES.name)
      .replace(/\\/g, "")
      .replace(/'/g, "")
      .split(",")
      .sort();
    expect(actual, "The Title list is not the same").toEqual(expected);
  }
  /** Validate Activate your account page elements. */
  async validateActivateYourAccountElements({
    companyName,
    inviteEmailAddress,
    password,
    companyRegistrationQuestionsAndAnswersResponse,
  }: {
    companyName: string;
    inviteEmailAddress: string;
    password: string;
    companyRegistrationQuestionsAndAnswersResponse: AnsweredQuestions;
  }): Promise<void> {
    console.log("Validate Activate your account elements");
    await expect(this.headerLabel, "Activation header").toHaveText( await IbStrings.ACTIVATE_YOUR_ACCOUNT.name, );
    await expect(this.subtitleLabel, "Activation subtitle").toHaveText( (await IbStrings.ACTIVATE_YOUR_ACCOUNT_SUBTITLE.name).replace( "{name}", companyName, ), );
    await this.validateCompanyEmailAddressInputValueAndPlaceholder({
      value: inviteEmailAddress,
    });
    await this.validatePasswordInputValueAndPlaceholder({ value: password });
    await this.registrationQuestions.validateRegistrationQuestionsAndAnswersForm(
      companyRegistrationQuestionsAndAnswersResponse,
      false,
      false,
    );
    await expect(this.createAccountButton, "Create account button").toHaveText( await IbStrings.ACTIVATE_YOUR_ACCOUNT_CREATE_ACCOUNT.name, );
  }
}
