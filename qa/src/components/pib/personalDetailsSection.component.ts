import { expect, type Locator, type Page } from "@playwright/test";
import { Strings } from "../../test-data/strings";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { Locales } from "../../test-data/locales";
/** InnBusiness Personal Details Section from Add/Edit employee */
export class PersonalDetailsSectionComponent {
  private readonly page: Page = global.page;
  // ######## UI elements/properties ########
  readonly personalDetailTitleLabel: Locator = this.page.getByTestId( "Personal-Details-Heading", );
  readonly titleDropdownButton: Locator = this.page.locator( '//button[@data-testid="Title-IB-Form-Select-Button" or @data-testid="title-IB-Form-Select-Button" or @data-testid="titleEmployee-IB-Form-Select-Button"]', );
  readonly titleDropdownLabelsList: Locator = this.page.locator( 'button[data-testid*="-Option"]', );
  readonly titleErrorTooltip: Locator = this.page.locator( '//div[@data-testid="Title-Error-Tooltip" or @data-testid="title-Error-Tooltip"]', );
  readonly firstNameLabel: Locator = this.page.getByTestId("First-Name-Label");
  readonly firstNameInput: Locator = this.page.locator( '//input[@data-testid="First-Name-Form-Input" or @data-testid="foreName-Form-Input" or @data-testid="FirstName-Form-Input" or @data-testid="firstNameEmployee-Form-Input"]', );
  readonly firstNameErrorTooltip: Locator = this.page.locator( '//div[@data-testid="First-Name-Error-Tooltip" or @data-testid="foreName-Error-Tooltip" or @data-testid="FirstName-Error-Tooltip" or @data-testid="firstNameEmployee-Error-Tooltip"]', );
  readonly lastNameLabel: Locator = this.page.getByTestId("Last-Name-Label");
  readonly lastNameInput: Locator = this.page.locator( '//input[@data-testid="Last-Name-Form-Input" or @data-testid="lastName-Form-Input" or @data-testid="LastName-Form-Input" or @data-testid="lastNameEmployee-Form-Input"]', );
  readonly lastNameErrorTooltip: Locator = this.page.locator( '//div[@data-testid="First-Name-Error-Tooltip" or @data-testid="foreName-Error-Tooltip" or @data-testid="FirstName-Error-Tooltip" or @data-testid="lastNameEmployee-Error-Tooltip"]', );
  readonly emailLabel: Locator = this.page.getByTestId("Email-Label");
  readonly emailInput: Locator = this.page.getByTestId("Email-Form-Input");
  readonly emailErrorTooltip: Locator = this.page.getByTestId( "Email-Error-Tooltip", );
  readonly contactNumberLabel: Locator = this.page.getByTestId("Phone-Number-Label");
  readonly contactNumberPrefix: Locator = this.page.getByTestId( "Phone-Number-IB-Form-Select-Button", );
  readonly contactNumberInput: Locator = this.page.locator( '//input[@data-testid="PhoneNumber-Form-Input" or @data-testid="Phone-Number-Form-Input"]', );
  readonly contactNumberErrorTooltip: Locator = this.page.locator( '//div[@data-testid="PhoneNumber-Error-Tooltip" or @data-testid="Phone-Number-Error-Tooltip"]', );
  readonly alternatePhoneLabel: Locator = this.page.getByTestId( "Alternate-Phone-Number-Label", );
  readonly alternatePhonePrefix: Locator = this.page.getByTestId( "Alternate-Phone-Number-IB-Form-Select-Button", );
  readonly alternatePhoneInput: Locator = this.page.getByTestId( "Alternate-Phone-Number-Form-Input", );
  readonly alternatePhoneErrorTooltip: Locator = this.page.locator( '//div[@data-testid="AlternatePhone-Number-Error-Tooltip" or @data-testid="Alternate-Phone-Number-Error-Tooltip"]', );
  /** Get title dropdown option based on title value. */
  getTitleDropdownOptionByTitle(titleValue: string): Locator {
    return this.page.getByTestId(`Title-${titleValue}-Option`);
  }
  /** Return title dropdown values. */
  async getTitleDropdownValues(): Promise<string[]> {
    console.log("Get title dropdown values");
    return this.titleDropdownLabelsList.allTextContents();
  }
  // ######## UI actions/navigation ########
  /** Click the title button. */
  async clickTitleButton(): Promise<void> {
    console.log("Click on Title dropdown");
    await this.titleDropdownButton.click();
  }
  /** Select a title value. */
  async selectTitleValueButton(titleValue: string): Promise<void> {
    console.log(`Set Employee Title: ${titleValue}`);
    const selectedTitle = titleValue || (Locales.isEnglishWebsite() ? "Mr" : "Herr");
    await this.page
      .locator(
        `//button[@data-testid="Title-${selectedTitle}-Option" or @data-testid="title-${selectedTitle}-Option" or @data-testid="titleEmployee-${selectedTitle}-Option"]`,
      )
      .click();
  }
  /** Set the user title. */
  async setUserTitle(titleValue: string): Promise<void> {
    console.log(`Set user title: ${titleValue}`);
    await this.clickTitleButton();
    await this.selectTitleValueButton(titleValue);
  }
  /** Set the employee first name. */
  async setEmployeeFirstName(
    firstName: string,
    pressTab = false,
  ): Promise<void> {
    console.log(`Set Employee First Name: ${firstName}`);
    await this.firstNameInput.fill(firstName);
    if (pressTab) await this.firstNameInput.press("Tab");
  }
  /** Set the employee last name. */
  async setEmployeeLastName(lastName: string, pressTab = false): Promise<void> {
    console.log(`Set Employee Last Name: ${lastName}`);
    await this.lastNameInput.fill(lastName);
    if (pressTab) await this.lastNameInput.press("Tab");
  }
  /** Set the employee email address. */
  async setEmployeeEmail(
    emailAddress: string,
    pressTab = false,
  ): Promise<void> {
    console.log(`Set Employee Email Address: ${emailAddress}`);
    await this.emailInput.fill(emailAddress);
    if (pressTab) await this.emailInput.press("Tab");
  }
  /** Set the mobile phone input. */
  async setMobilePhoneInput(
    mobilePhone: string,
    pressTab = false,
  ): Promise<void> {
    console.log(`Set Mobile Phone: ${mobilePhone}`);
    await this.contactNumberInput.fill(mobilePhone);
    if (pressTab) await this.contactNumberInput.press("Tab");
  }
  /** Set the alternate phone input. */
  async setAlternatePhoneInput(
    alternatePhone: string,
    pressTab = false,
  ): Promise<void> {
    console.log(`Set Alternate Mobile Phone: ${alternatePhone}`);
    await this.alternatePhoneInput.fill(alternatePhone);
    if (pressTab) await this.alternatePhoneInput.press("Tab");
  }
  /** Set employee personal details. */
  async setEmployeeDetails(details: {
    title: string;
    firstName?: string;
    lastName?: string;
    emailAddress?: string;
    mobilePhone?: string;
    alternatePhone?: string;
  }): Promise<void> {
    console.log("Fill employee personal details");
    await this.setUserTitle(details.title);
    if (details.firstName) await this.setEmployeeFirstName(details.firstName);
    if (details.lastName) await this.setEmployeeLastName(details.lastName);
    if (details.emailAddress) await this.setEmployeeEmail(details.emailAddress);
    if (details.mobilePhone)
      await this.setMobilePhoneInput(details.mobilePhone);
    if (details.alternatePhone)
      await this.setAlternatePhoneInput(details.alternatePhone);
  }
  // ######## UI validations ########
  /** Validate the title. */
  async validateTitle({ value = "" }: { value?: string } = {}): Promise<void> {
    console.log("Validate title");
    await expect(this.titleDropdownButton, "Title button").toHaveText(value || await Strings.TITLE_LEAD_GUEST.name);
  }
  /** Validate the contact number error tooltip. */
  async validateContactNumberErrorTooltip(): Promise<void> {
    console.log("Validate contact number tooltip");
    await expect( this.contactNumberErrorTooltip, "Contact number tooltip", ).toBeVisible();
    await expect(this.contactNumberErrorTooltip, "Contact number tooltip text").toContainText(await IbStrings.INVALID_CONTACT_NUMBER_TOOLTIP.name);
  }
  /** Validate the alternate phone error tooltip. */
  async validateAternatePhoneErrorTooltip(): Promise<void> {
    console.log("Validate alternate phone tooltip");
    await expect( this.alternatePhoneErrorTooltip, "Alternate phone tooltip", ).toBeVisible();
    await expect(this.alternatePhoneErrorTooltip, "Alternate phone tooltip text").toContainText(await IbStrings.INVALID_ALTERNATE_PHONE_TOOLTIP.name);
  }
  /** Validate the selected title. */
  async validateTitleIsSelected(selectedValue = ""): Promise<void> {
    console.log(`Validate that ${selectedValue} is selected`);
    if (selectedValue)
      await expect(this.titleDropdownButton, "Title selection").toContainText( selectedValue, );
    else await expect(this.titleErrorTooltip, "Title error").toBeVisible();
  }
  /** Validate the first name. */
  async validateFirstName({
    value = "",
    shouldBeValid = true,
  }: { value?: string; shouldBeValid?: boolean } = {}): Promise<void> {
    console.log("Validate first name");
    if (!shouldBeValid) {
      await expect(this.firstNameErrorTooltip, "First name error tooltip").toContainText(await IbStrings.INCORRECT_FIRST_NAME_FORMAT.name);
    }
    await expect(this.firstNameInput, "First name placeholder").toHaveAttribute("placeholder", await Strings.FIRST_NAME.name);
    await expect(this.firstNameInput, "First name").toHaveValue(value);
  }
  /** Validate the last name. */
  async validateLastName({
    value = "",
    shouldBeValid = true,
  }: { value?: string; shouldBeValid?: boolean } = {}): Promise<void> {
    console.log("Validate last name");
    if (!shouldBeValid) {
      await expect(this.lastNameErrorTooltip, "Last name error tooltip").toContainText(await IbStrings.INCORRECT_LAST_NAME_FORMAT.name);
    }
    await expect(this.lastNameInput, "Last name placeholder").toHaveAttribute("placeholder", await Strings.LAST_NAME.name);
    await expect(this.lastNameInput, "Last name").toHaveValue(value);
  }
  /** Validate the email address. */
  async validateEmailAddress({
    value = "",
    shouldBeValid = true,
  }: { value?: string; shouldBeValid?: boolean } = {}): Promise<void> {
    console.log("Validate email address");
    if (!shouldBeValid) {
      await expect(this.emailErrorTooltip, "Email error tooltip").toContainText(await IbStrings.INCORRECT_EMAIL_FORMAT.name);
    }
    await expect(this.emailInput, "Email placeholder").toHaveAttribute("placeholder", await Strings.EMAIL_STAR_IB.name);
    await expect(this.emailInput, "Email address").toHaveValue(value);
  }
  /** Validate the mobile phone number. */
  async validateMobilePhoneNumber({
    value = "",
    shouldBeValid = true,
  }: { value?: string; shouldBeValid?: boolean } = {}): Promise<void> {
    console.log("Validate mobile phone number");
    if (!shouldBeValid) {
      const errorMessage = value ? IbStrings.INCORRECT_PHONE_NUMBER_FORMAT : IbStrings.PLEASE_FILL_PHONE_NUMBER;
      await expect(this.contactNumberErrorTooltip, "Contact number error tooltip").toContainText(await errorMessage.name);
    }
    await expect(this.contactNumberInput, "Contact number placeholder").toHaveAttribute("placeholder", await IbStrings.CONTACT_NUMBER.name);
    await expect(this.contactNumberInput, "Contact number").toHaveValue(value);
  }
  /** Validate the alternate phone number. */
  async validateAlternatePhoneNumber({
    value = "",
  }: { value?: string } = {}): Promise<void> {
    console.log("Validate alternate phone number");
    await expect(this.alternatePhoneInput, "Alternate phone placeholder").toHaveAttribute("placeholder", await IbStrings.ALTERNATE_PHONE.name);
    await expect(this.alternatePhoneInput, "Alternate phone").toHaveValue( value, );
  }
  /** Validate the personal details section. */
  async validatePersonalDetailsSection(data: {
    employeeDetails?: {
      title?: string | null;
      firstName?: string;
      lastName?: string;
      emailAddress?: string;
      phoneNumber?: string;
      mobileNumber?: string;
    };
    isEdit?: boolean;
  }): Promise<void> {
    console.log("Validate Personal Details Section");
    await expect( this.personalDetailTitleLabel, "Personal details title", ).toBeVisible();
    const employeeDetails = data.employeeDetails ?? {};
    const isEdit = data.isEdit ?? false;
    await expect(this.titleDropdownButton, "Title button").toHaveText(
      isEdit && employeeDetails.title ? employeeDetails.title : await Strings.TITLE_LEAD_GUEST.name,
    );
    await this.validateFirstName({ value: isEdit ? employeeDetails.firstName ?? "" : "" });
    await this.validateLastName({ value: isEdit ? employeeDetails.lastName ?? "" : "" });
    await this.validateEmailAddress({ value: isEdit ? employeeDetails.emailAddress ?? "" : "" });
    const contactNumberPrefix = await this.contactNumberPrefix.textContent() ?? "";
    const alternatePhonePrefix = await this.alternatePhonePrefix.textContent() ?? "";
    await this.validateMobilePhoneNumber({
      value: isEdit && employeeDetails.phoneNumber ? employeeDetails.phoneNumber.replace(contactNumberPrefix, "") : "",
    });
    await this.validateAlternatePhoneNumber({
      value: isEdit && employeeDetails.mobileNumber ? employeeDetails.mobileNumber.replace(alternatePhonePrefix, "") : "",
    });
  }
}
