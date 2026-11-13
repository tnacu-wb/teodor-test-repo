import { expect, type Locator } from "@playwright/test";
import { PersonalDetailsSectionComponent } from "../../components/pib";
import { Constants } from "../../test-data/constants";
import { Locales } from "../../test-data/locales";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { Strings } from "../../test-data/strings";
import { BasePibPage } from "./basePib.page";

/**
 * InnBusiness application > Create your InnBusiness account page > Confirm your email page > Welcome to InnBusiness Page
 */
export class WelcomeToInnBusinessPage extends BasePibPage {
  // ######## properties ########
  // ######## UI elements/properties ########
  readonly headerLabel: Locator = this.page.getByTestId("wizard-title");
  readonly subtitleLabel: Locator = this.page.getByTestId( "RegisterStep2-PersonalInfoLanding-Title", );
  readonly countryFlagImg: Locator = this.page.locator( 'div[data-testid="PhoneNumber-IB-Form-Select"] div img', );
  readonly countryPrefixLabel: Locator = this.page.locator( 'div[data-testid="PhoneNumber-IB-Form-Select"] div span', );
  readonly contactNumberDropdownButton: Locator = this.page.locator( 'div[data-testid="PhoneNumber-IB-Form-Select"] div div', );
  readonly continueButton: Locator = this.page.getByTestId("footer-button");

  /**
   * Get titles array
   */
  async getTitlesArray(): Promise<string[]> {
    console.log("Get titles array");
    return (await IbStrings.WELCOME_TO_INNBUSINESS_TITLE_VALUES.name)
      .replace(/\\/g, "")
      .replace(/'/g, "")
      .split(",");
  }

  /**
   * Return an list of titles
   * @returns getTitleDropdownValues
   */
  async getTitleDropdownValues(): Promise<string[]> {
    console.log("Get title dropdown values");
    return this.personalDetails.getTitleDropdownValues();
  }

  // UI components
  readonly personalDetails = new PersonalDetailsSectionComponent();

  // ######## UI actions/navigation ########
  /**
   * Click on Continue button
   */
  async clickContinueButton(): Promise<void> {
    console.log("Click on Continue button");
    await this.continueButton.click();
  }

  /**
   * Set registration information
   * @param data data object
   * @param data.title title value
   * @param data.firstName first name value
   * @param data.lastName last name value
   * @param data.contactNumber contact number value
   */
  async setRegistrationInformation({
    title = "",
    firstName = "",
    lastName = "",
    contactNumber = "",
  }: {
    title?: string;
    firstName?: string;
    lastName?: string;
    contactNumber?: string;
  } = {}): Promise<void> {
    console.log("Set registration information");
    await this.personalDetails.setUserTitle(title);
    await this.personalDetails.setEmployeeFirstName(firstName);
    await this.personalDetails.setEmployeeLastName(lastName);
    await this.personalDetails.setMobilePhoneInput(contactNumber);
    await this.clickContinueButton();
  }

  // ######## UI validations ########
  /**
   * Check we reached the current page by checking a specific element from the page
   */
  async validatePage(): Promise<void> {
    console.log("Validate Welcome to InnBusiness page");
    await this.validatePageMarker(this.subtitleLabel, "Welcome to InnBusiness");
  }

  /**
   * Validate Title dropdown values
   */
  async validateTitleDropdownValues(): Promise<void> {
    console.log("Validate title dropdown values");
    const titleDropdownValues = await this.getTitleDropdownValues();
    titleDropdownValues.sort((firstTitle, secondTitle) =>
      firstTitle.localeCompare(secondTitle),
    );
    const titleDropdownsListExpected = await this.getTitlesArray();
    titleDropdownsListExpected.sort((firstTitle, secondTitle) =>
      firstTitle.localeCompare(secondTitle),
    );
    expect(titleDropdownValues, 'The "Title" list is not the same').toEqual( titleDropdownsListExpected, );
  }

  /**
   * Validate the placeholder and the value of the first name input
   * @param data data object
   * @param data.placeholder placeholder of input
   * @param data.value value of input
   */
  async validateFirstNameInputValueAndPlaceholder({
    placeholder,
    value = "",
  }: { placeholder?: string; value?: string } = {}): Promise<void> {
    console.log("Validate first name input value and placeholder");
    await expect( this.personalDetails.firstNameInput, "First name input placeholder", ).toHaveAttribute( "placeholder", placeholder ?? (await IbStrings.WELCOME_TO_INNBUSINESS_FIRST_NAME.name), );
    await expect( this.personalDetails.firstNameInput, "First name input value", ).toHaveValue(value);
  }

  /**
   * Validate the placeholder and the value of the last name input
   * @param data data object
   * @param data.placeholder placeholder of input
   * @param data.value value of input
   */
  async validateLastNameInputValueAndPlaceholder({
    placeholder,
    value = "",
  }: { placeholder?: string; value?: string } = {}): Promise<void> {
    console.log("Validate last name input value and placeholder");
    await expect( this.personalDetails.lastNameInput, "Last name input placeholder", ).toHaveAttribute( "placeholder", placeholder ?? (await IbStrings.WELCOME_TO_INNBUSINESS_LAST_NAME.name), );
    await expect( this.personalDetails.lastNameInput, "Last name input value", ).toHaveValue(value);
  }

  /**
   * Validate the placeholder and the value of the contact number input
   * @param data data object
   * @param data.placeholder placeholder of input
   * @param data.value value of input
   */
  async validateContactNumberInputValueAndPlaceholder({
    placeholder,
    value = "",
  }: { placeholder?: string; value?: string } = {}): Promise<void> {
    console.log("Validate contact number input value and placeholder");
    await expect( this.personalDetails.contactNumberInput, "Contact number input placeholder", ).toHaveAttribute( "placeholder", placeholder ?? (await IbStrings.WELCOME_TO_INNBUSINESS_CONTACT_NUMBER.name), );
    await expect( this.personalDetails.contactNumberInput, "Contact number input value", ).toHaveValue(value);
  }

  /**
   * Validate title button error
   * @param data data object
   * @param data.isDisplayed true if error is displayed
   */
  async validateTitleButtonError({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log("Validate title button error");
    if (isDisplayed)
      await expect( this.personalDetails.titleErrorTooltip, "Title button error label", ).toHaveText(await Strings.WELCOME_TO_INNBUSINESS_TITLE_ERROR.name);
    else
      await expect( this.personalDetails.titleErrorTooltip, "Title button error label", ).not.toBeVisible();
  }

  /**
   * Validate first name input error
   * @param data data object
   * @param data.isDisplayed true if error is displayed
   */
  async validateFirstNameInputError({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log("Validate first name input error");
    if (isDisplayed)
      await expect( this.personalDetails.firstNameErrorTooltip, "First name input error label text", ).toHaveText(await Strings.WELCOME_TO_INNBUSINESS_NAME_ERROR.name);
    else
      await expect( this.personalDetails.firstNameErrorTooltip, "First name input error label", ).not.toBeVisible();
  }

  /**
   * Validate last name input error
   * @param data data object
   * @param data.isDisplayed true if error is displayed
   */
  async validateLastNameInputError({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log("Validate last name input error");
    if (isDisplayed)
      await expect( this.personalDetails.lastNameErrorTooltip, "Last name input error label text", ).toHaveText(await Strings.WELCOME_TO_INNBUSINESS_NAME_ERROR.name);
    else
      await expect( this.personalDetails.lastNameErrorTooltip, "Last name input error label", ).not.toBeVisible();
  }

  /**
   * Validate contact number input error
   * @param data data object
   * @param data.isDisplayed true if error is displayed
   */
  async validateContactNumberInputError({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log("Validate contact number input error");
    if (isDisplayed)
      await expect( this.personalDetails.contactNumberErrorTooltip, "Contact number input error label text", ).toHaveText( await Strings.WELCOME_TO_INNBUSINESS_CONTACT_NUMBER_ERROR.name, );
    else
      await expect( this.personalDetails.contactNumberErrorTooltip, "Contact number input error label", ).not.toBeVisible();
  }

  /**
   * Validate the selected country flag and prefix
   * @param data data object
   * @param data.countryName name of selected country
   * @param data.countryNameFromSrc name of selected country from Src
   * @param data.prefix prefix of selected country
   */
  async validateSelectedCountryFlagAndPrefix({
    countryName,
    countryNameFromSrc,
    prefix,
  }: {
    countryName?: string;
    countryNameFromSrc?: string;
    prefix?: string;
  } = {}): Promise<void> {
    countryName = countryName || (await Locales.getDefaultCountryLabel());
    prefix =
      prefix || Locales.isEnglishWebsite()
        ? Constants.GB_MOBILE_PREFIX
        : Constants.DE_MOBILE_PREFIX;
    console.log(
      `Validate the selected country and prefix: countryName=${countryName}`,
    );
    const countryNameSrc = Locales.isEnglishWebsite()
      ? countryName === (await Strings.UNITED_KINGDOM_THE.name)
        ? Constants.UNITED_KINGDOM
        : countryNameFromSrc
      : countryName === (await Strings.GERMANY.name)
        ? String(Strings.GERMANY.data.default)
        : countryNameFromSrc;
    await expect(this.countryFlagImg, "Selected country flag").toBeVisible();
    await expect(this.countryPrefixLabel, "Selected country prefix").toHaveText( prefix, );
    await expect( this.countryFlagImg, `Country flag should be: ${countryNameSrc}`, ).toHaveAttribute("src", expect.stringContaining(countryNameSrc ?? ""));
  }

  /**
   * Validate Continue button
   */
  async validateContinueButton(): Promise<void> {
    console.log("Validate Continue button");
    await expect(this.continueButton, "Continue button").toHaveText( await IbStrings.WELCOME_TO_INNBUSINESS_CONTINUE.name, );
  }

  /**
   * Validate Welcome to InnBusiness elements
   * @param data data object
   * @param data.accountData account detail
   */
  async validateWelcomeToInnBusinessElements({
    accountData = {},
  }: {
    accountData?: {
      title?: string;
      firstName?: string;
      lastName?: string;
      contactNumber?: string;
    };
  } = {}): Promise<void> {
    console.log("Validate Welcome to InnBusiness elements");
    await expect(this.headerLabel, "Header label").toHaveText( await IbStrings.WELCOME_TO_INNBUSINESS.name, );
    await expect(this.subtitleLabel, "Subtitle label").toHaveText( await IbStrings.WELCOME_TO_INNBUSINESS_SUBTITLE.name, );
    await this.personalDetails.validateTitle({ value: accountData.title });
    await this.validateFirstNameInputValueAndPlaceholder({
      value: accountData.firstName,
    });
    await this.validateLastNameInputValueAndPlaceholder({
      value: accountData.lastName,
    });
    await this.validateContactNumberInputValueAndPlaceholder({
      value: accountData.contactNumber,
    });
    await this.validateSelectedCountryFlagAndPrefix();
    await this.validateContinueButton();
  }
}
