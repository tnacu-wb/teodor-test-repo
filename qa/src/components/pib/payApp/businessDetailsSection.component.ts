import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../../test-data/pib/ibStrings";
import { CompanyAddressSectionComponent } from "../companyAddressSection.component";
import { PersonalDetailsSectionComponent } from "../personalDetailsSection.component";
/** InnBusiness application > Pay App > Your Company details section */
export class BusinessDetailsSectionComponent {
  private readonly page: Page = global.page;
  // ######## properties ########
  // ######## UI elements/properties ########
  readonly companyNameContainer: Locator = this.page.getByTestId( "CompanyDetailsBusinessInfo-company-details", );
  readonly companyNameTitleLabel: Locator = this.page .getByTestId("CompanyContainer-display-company-info-widget-title") .first();
  readonly savedCompanyNameLabel: Locator = this.page.getByTestId( "CompanyDetailsBusinessInfo-company-name", );
  readonly companyNameEditButton: Locator = this.companyNameContainer .locator("button") .first();
  readonly businessTypeTitleLabel: Locator = this.companyNameContainer.locator( "xpath=following-sibling::div/div[1]", );
  readonly selectedBusinessTypeLabel: Locator = this.companyNameContainer.locator("xpath=following-sibling::div/div[2]");
  readonly companyAddressEditButton: Locator = this.companyNameContainer .locator("button") .nth(1);
  readonly addCorrespondenceAddressButton: Locator = this.page.getByTestId( "CompanyDetailsBusinessInfo-button-add-correspondence", );
  readonly registeredCharityNumberInput: Locator = this.page.getByTestId( "RegisteredCharityNumberForm-Form-Input", );
  readonly registeredCharityNumberErrorTooltip: Locator = this.page.getByTestId( "RegisteredCharityNumberForm-Error-Tooltip", );
  readonly companyRegistrationNumberInput: Locator = this.page.getByTestId( "CompanyRegistrationNumber-Form-Input", );
  readonly companyRegistrationNumberErrorTooltip: Locator = this.page.getByTestId("CompanyRegistrationNumber-Error-Tooltip");
  readonly findCompanyDetailsButton: Locator = this.page .locator('h1[data-testid="wizard-title"]') .locator("xpath=parent::*/following-sibling::div[4]//button");
  readonly companyRegistrationSuccessTooltip: Locator = this.page.locator( "div.border-successTint2.bg-successTint.mt-12 > div", );
  readonly timeTradingDropdownButton: Locator = this.page.getByTestId( "TimeTradingForm-timeTradingId-IB-Form-Select-Button", );
  readonly timeTradingDropdownList: Locator = this.page.getByTestId( "TimeTradingForm-timeTradingId-IB-Form-Select-Dropdown", );
  readonly timeTradingErrorTooltip: Locator = this.page.getByTestId( "TimeTradingForm-timeTradingId-Error-Tooltip", );
  readonly parentCompanyNameInput: Locator = this.page.getByTestId( "ParentCompanyForm-Form-Input", );
  readonly numberOfPartnersInput: Locator = this.page.getByTestId( "numberOfPartners-Form-Input", );
  readonly numberOfPartnersErrorTooltip: Locator = this.page.getByTestId( "numberOfPartners-Error-Tooltip", );
  readonly customerNameSection: Locator = this.page.getByTestId( "CompanyDetailsBusinessInfo-userName", );
  readonly customerNameEditButton: Locator = this.customerNameSection.getByRole("button");
  readonly birthdayPickerContainer: Locator = this.page.getByTestId( "CompanyDetailsBusinessInfo-birthDayPicker", );
  readonly dayOfBirthDropdownButton: Locator = this.page.getByTestId( "DateOfBirthForm-day-IB-Form-Select-Button", );
  readonly monthOfBirthDropdownButton: Locator = this.page.getByTestId( "DateOfBirthForm-month-IB-Form-Select-Button", );
  readonly yearOfBirthDropdownButton: Locator = this.page.getByTestId( "DateOfBirthForm-year-IB-Form-Select-Button", );
  readonly dateOfBirthErrorTooltip: Locator = this.page.getByTestId( "DateOfBirthForm-Error-Tooltip", );
  readonly companyAddressSection = new CompanyAddressSectionComponent();
  readonly personalDetailsSection = new PersonalDetailsSectionComponent();
  /** Get time trading option by label. */
  getTimeTradingOptionByLabel(label: string): Locator {
    return this.page.getByTestId(
      `TimeTradingForm-timeTradingId-${label}-Option`,
    );
  }
  /** Get day of birth. */
  getDayOfBirthOptionLabel(day: string | number): Locator {
    return this.page.getByTestId(`DateOfBirthForm-day-${day}-Option`);
  }
  /** get month of birth. */
  getMonthOfBirthOptionLabel(month: string | number): Locator {
    return this.page.getByTestId(`DateOfBirthForm-month-${month}-Option`);
  }
  /** Get year of birth. */
  getYearOfBirthOptionLabel(year: string | number): Locator {
    return this.page.getByTestId(`DateOfBirthForm-year-${year}-Option`);
  }
  // ######## UI actions/navigation ########
  /** Fill. */
  private async fill(
    input: Locator,
    value: string,
    pressTab = false,
  ): Promise<void> {
    await input.fill(value);
    if (pressTab) await input.press("Tab");
  }
  /** Set registered charity number. */
  async setRegisteredCharityNumber({
    value,
    pressTab = false,
  }: {
    value: string;
    pressTab?: boolean;
  }): Promise<void> {
    console.log("Set Registered Charity Number");
    await this.fill(this.registeredCharityNumberInput, value, pressTab);
  }
  /** Set company registration number. */
  async setCompanyRegistrationNumber({
    value,
    pressTab = false,
  }: {
    value: string;
    pressTab?: boolean;
  }): Promise<void> {
    console.log("Set Company Registration Number");
    await this.fill(this.companyRegistrationNumberInput, value, pressTab);
  }
  /** Set parent company name. */
  async setParentCompanyName({
    value,
    pressTab = false,
  }: {
    value: string;
    pressTab?: boolean;
  }): Promise<void> {
    console.log("Set Parent Company Name");
    await this.fill(this.parentCompanyNameInput, value, pressTab);
  }
  /** Set number of partners. */
  async setNumberOfPartners({
    value,
    pressTab = false,
  }: {
    value: string;
    pressTab?: boolean;
  }): Promise<void> {
    console.log("Set Number of Partners");
    await this.fill(this.numberOfPartnersInput, value, pressTab);
  }
  /** Click Find Company details. */
  async clickFindCompanyDetailsButton(): Promise<void> {
    console.log("Click Find Company details");
    await this.findCompanyDetailsButton.click();
  }
  /** Click add correspondence address. */
  async clickAddCompanyCorrespondenceButton(): Promise<void> {
    console.log("Click add correspondence address");
    await this.addCorrespondenceAddressButton.click();
  }
  /** Click customer name edit. */
  async clickCustomerNameEditButton(): Promise<void> {
    console.log("Click customer name edit");
    await this.customerNameEditButton.click();
  }
  /** Set time trading option. */
  async setTimeTradingOption({
    timeTrading,
  }: {
    timeTrading: string;
  }): Promise<void> {
    console.log("Set Time Trading Option");
    await this.timeTradingDropdownButton.click();
    await this.getTimeTradingOptionByLabel(timeTrading).click();
  }
  /** Set partner details. */
  async setPartnerDetails({
    numberOfPartners,
    title,
    firstName,
    lastName,
  }: {
    numberOfPartners: string;
    title: string;
    firstName: string;
    lastName: string;
  }): Promise<void> {
    console.log("Set Partner Details");
    await this.setNumberOfPartners({ value: numberOfPartners });
    await this.personalDetailsSection.setUserTitle(title);
    await this.personalDetailsSection.setEmployeeFirstName(firstName);
    await this.personalDetailsSection.setEmployeeLastName(lastName);
  }
  /** Set day of birth. */
  async setDayOfBirthOption(day: string | number): Promise<void> {
    console.log("Set day of birth");
    await this.dayOfBirthDropdownButton.click();
    await this.getDayOfBirthOptionLabel(day).click();
  }
  /** Set month of birth. */
  async setMonthOfBirthOption(month: string | number): Promise<void> {
    console.log("Set month of birth");
    await this.monthOfBirthDropdownButton.click();
    await this.getMonthOfBirthOptionLabel(month).click();
  }
  /** Set year of birth. */
  async setYearOfBirthOption(year: string | number): Promise<void> {
    console.log("Set year of birth");
    await this.yearOfBirthDropdownButton.click();
    await this.getYearOfBirthOptionLabel(year).click();
  }
  /** Set date of birth. */
  async setDateOfBirth({
    day,
    month,
    year,
  }: {
    day: string | number;
    month: string | number;
    year: string | number;
  }): Promise<void> {
    console.log("Set Date of Birth");
    await this.setDayOfBirthOption(day);
    await this.setMonthOfBirthOption(month);
    await this.setYearOfBirthOption(year);
  }
  // ######## UI validations ########
  /** Validate company name section. */
  async validateCompanyNameSection(expectedCompanyName: string): Promise<void> {
    console.log("Validate company name section");
    await expect( this.companyNameContainer, "Company name container", ).toBeVisible();
    await expect(this.savedCompanyNameLabel, "Company name").toHaveText( expectedCompanyName, );
  }
  /** Validate registered charity input. */
  async validateRegisteredCharityNumberInput({
    registeredCharityNumber = "",
  }: { registeredCharityNumber?: string } = {}): Promise<void> {
    console.log("Validate Registered Charity Number");
    await expect( this.registeredCharityNumberInput, "Registered charity number", ).toHaveValue(registeredCharityNumber);
  }
  /** Validate company registration input. */
  async validateCompanyRegistrationNumberInput({
    companyRegistrationNumber = "",
  }: { companyRegistrationNumber?: string } = {}): Promise<void> {
    console.log("Validate Company Registration Number");
    await expect( this.companyRegistrationNumberInput, "Company registration number", ).toHaveValue(companyRegistrationNumber);
  }
  /** Validate parent company input. */
  async validateParentCompanyNameInput({
    parentCompanyName = "",
  }: { parentCompanyName?: string } = {}): Promise<void> {
    console.log("Validate Parent Company Name");
    await expect( this.parentCompanyNameInput, "Parent company name", ).toHaveValue(parentCompanyName);
  }
  /** Validate number of partners input. */
  async validateNumberOfPartnersInput({
    numberOfPartners = "",
    isValid = true,
  }: { numberOfPartners?: string; isValid?: boolean } = {}): Promise<void> {
    console.log("Validate Number of Partners");
    await expect(this.numberOfPartnersInput, "Number of partners").toHaveValue( numberOfPartners, );
    if (!isValid)
      await expect( this.numberOfPartnersErrorTooltip, "Number of partners error", ).toBeVisible();
  }
  /** Validate partner first name. */
  async validatePartnerFirstNameInput(
    _data: {
      partnerFirstName?: string;
      isValid?: boolean;
      companyType?: string;
    } = {},
  ): Promise<void> {
    console.log("Validate Partner First Name");
    await expect( this.personalDetailsSection.firstNameInput, "Partner first name", ).toBeVisible();
  }
  /** Validate partner last name. */
  async validatePartnerLastNameInput(
    _data: {
      partnerLastName?: string;
      isValid?: boolean;
      companyType?: string;
    } = {},
  ): Promise<void> {
    console.log("Validate Partner Last Name");
    await expect( this.personalDetailsSection.lastNameInput, "Partner last name", ).toBeVisible();
  }
  /** Validate day button. */
  async validateDayButton({ day }: { day?: string } = {}): Promise<void> {
    console.log("Validate day button");
    await expect(this.dayOfBirthDropdownButton, "Day of birth").toHaveText( day ?? (await IbStrings.DAY.name), );
  }
  /** Validate month button. */
  async validateMonthButton({ month }: { month?: string } = {}): Promise<void> {
    console.log("Validate month button");
    await expect(this.monthOfBirthDropdownButton, "Month of birth").toHaveText( month ?? (await IbStrings.MONTH.name), );
  }
  /** Validate year button. */
  async validateYearButton({ year }: { year?: string } = {}): Promise<void> {
    console.log("Validate year button");
    await expect(this.yearOfBirthDropdownButton, "Year of birth").toHaveText( year ?? (await IbStrings.YEAR.name), );
  }
  /** Validate date of birth values. */
  async validateDateOfBirthValues({
    day,
    month,
    year,
  }: { day?: string; month?: string; year?: string } = {}): Promise<void> {
    console.log("Validate date of birth values");
    await this.validateDayButton({ day });
    await this.validateMonthButton({ month });
    await this.validateYearButton({ year });
  }
  /** Validate registered charity error. */
  async validateRegisteredCharityErrorTooltip(
    isDisplayed: boolean,
  ): Promise<void> {
    console.log("Validate registered charity error");
    if (isDisplayed)
      await expect( this.registeredCharityNumberErrorTooltip, "Registered charity error", ).toBeVisible();
    else
      await expect( this.registeredCharityNumberErrorTooltip, "Registered charity error", ).not.toBeVisible();
  }
  /** Validate time trading error. */
  async validateTimeTradingErrorTooltip(isDisplayed: boolean): Promise<void> {
    console.log("Validate time trading error");
    if (isDisplayed)
      await expect( this.timeTradingErrorTooltip, "Time trading error", ).toBeVisible();
    else
      await expect( this.timeTradingErrorTooltip, "Time trading error", ).not.toBeVisible();
  }
  /** Validate date of birth error. */
  async validateDateOfBirthErrorTooltip(isDisplayed: boolean): Promise<void> {
    console.log("Validate date of birth error");
    if (isDisplayed)
      await expect( this.dateOfBirthErrorTooltip, "Date of birth error", ).toBeVisible();
    else
      await expect( this.dateOfBirthErrorTooltip, "Date of birth error", ).not.toBeVisible();
  }
  /** Validate company registration error. */
  async validateCompanyRegistrationNumberErrorTooltip(
    isDisplayed: boolean,
  ): Promise<void> {
    console.log("Validate company registration error");
    if (isDisplayed)
      await expect( this.companyRegistrationNumberErrorTooltip, "Company registration error", ).toBeVisible();
    else
      await expect( this.companyRegistrationNumberErrorTooltip, "Company registration error", ).not.toBeVisible();
  }
  /** Validate company registration success. */
  async validateCompanyRegistrationSuccessTooltip(
    isDisplayed: boolean,
  ): Promise<void> {
    console.log("Validate company registration success");
    if (isDisplayed)
      await expect( this.companyRegistrationSuccessTooltip, "Company registration success", ).toBeVisible();
    else
      await expect( this.companyRegistrationSuccessTooltip, "Company registration success", ).not.toBeVisible();
  }
  /** Validate company details section. */
  async validateCompanyDetailsSection({
    companyDetails,
  }: {
    companyType?: string;
    companyDetails: { companyName: string };
  }): Promise<void> {
    console.log("Validate Company details section");
    await this.validateCompanyNameSection(companyDetails.companyName);
  }
}
