import { expect, type Locator } from "@playwright/test";
import {
  CompanyAddressSectionComponent,
  HeaderSectionComponent,
  MenuContainerComponent,
  ReviewChangesModalComponent,
  ToastNotificationSectionComponent,
} from "../../components/pib";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { BasePibPage } from "./basePib.page";

interface CompanyAddress {
  addressLine1?: string;
  addressLine2?: string;
  addressLine3?: string;
  addressLine4?: string;
  addressLine5?: string;
  line1?: string;
  line2?: string;
  line3?: string;
  line4?: string;
  line5?: string;
  postCode?: string;
  postalCode?: string;
  postcode?: string;
  country?: string;
  countryCode?: string;
}

interface CompanyInfo {
  companyName: string;
  companyAddress: CompanyAddress;
}

interface MainContact {
  title?: string;
  contactName: string;
  emailAddress: string;
  contactNumber: string;
  jobTitle?: string;
}

interface SetCompanyNameInputOptions {
  companyName: string;
  pressTab?: boolean;
}

interface SearchMainContactOptions {
  userNameOrEmail: string;
  pressTab?: boolean;
}

interface SetJobTitleOptions {
  jobTitle: string;
  pressTab?: boolean;
}

interface ValidateMainContactLabelsOptions {
  mainContact: MainContact;
  isMainContactNameEmailDisplayed?: boolean;
  isMainContactIncompleteBadgeDisplayed?: boolean;
  isMainContactJobTitleDisplayed?: boolean;
}

interface ValidateAdditionalDetailsLabelsOptions {
  companySector?: string;
  averageMonthlyBookings?: string;
  numberOfEmployees?: string;
}

interface ValidateMainContactErrorTooltipOptions {
  searchedText?: string;
  isDisplayed?: boolean;
}

interface ValidateEditMainContactSectionOptions {
  mainContact: MainContact;
  hasEmail?: boolean;
  hasContactNumber?: boolean;
}

/**
 * InnBusiness application > Manage > Company details
 */
export class CompanyDetailsPage extends BasePibPage {
  readonly url = "manage/company";

  // ######## UI elements/properties ########
  readonly headerLabel: Locator = this.page.locator( 'div[data-testid="CompanyDetailsPage-container"] h1', );
  readonly companyInfoContainer: Locator = this.page.getByTestId( "CompanyInformation-display-company-info-widget", );
  readonly companyInfoEditButton: Locator = this.companyInfoContainer.locator("button");
  readonly companyInfoTitleLabel: Locator = this.companyInfoContainer.getByTestId( "CompanyContainer-display-company-info-widget-title", );
  readonly companyNameLabel: Locator = this.page.getByTestId( "AddressDetails-company-name", );
  readonly companyNameInput: Locator = this.page.getByTestId( "Company-name-Form-Input", );
  readonly companyNameInputErrorLabel: Locator = this.page.getByTestId( "Company-name-Error-Tooltip", );
  readonly mainContactContainer: Locator = this.page.getByTestId( "CompanyInformation-display-main-contact-widget", );
  readonly mainContactTitleLabel: Locator = this.mainContactContainer.getByTestId( "CompanyContainer-display-company-info-widget-title", );
  readonly mainContactIncompleteBadgeLabel: Locator = this.page.locator( '(//div[@data-testid="CompanyContainer"])[2]/div/span[2]', );
  readonly mainContactEditButton: Locator = this.mainContactContainer.locator("button");
  readonly mainContactTextContainer: Locator = this.page.getByTestId( "CompanyMainContact-container", );
  readonly mainContactNameLabel: Locator = this.page.getByTestId("UserDetails-name");
  readonly mainContactEmailAddressLabel: Locator = this.page.getByTestId( "UserDetails-emailAddress", );
  readonly mainContactJobTitleLabel: Locator = this.page.getByTestId( "UserDetails-jobTitle", );
  readonly additionalDetailsContainer: Locator = this.page.getByTestId( "CompanyInformation-display-company-additional-details-widget", );
  readonly additionalDetailsEditButton: Locator = this.additionalDetailsContainer.locator("button");
  readonly additionalDetailsTitleLabel: Locator = this.additionalDetailsContainer.getByTestId( "CompanyContainer-display-company-info-widget-title", );
  readonly additionalDetailsBadgeLabel: Locator = this.additionalDetailsContainer.locator( '[data-testid="CompanyContainer-display-company-info-widget-title"] ~ span', );
  readonly additionalDetailsLabels: Locator = this.page.locator( 'div[data-testid="CompanyAdditionalDetails-container"] span', );
  readonly saveButton: Locator = this.page.getByTestId( "CompanyInformation-Save-updates-Company", );
  readonly discardChangesButton: Locator = this.additionalDetailsContainer.locator("~ div button.underline");
  readonly companyDeleteContainer: Locator = this.page.getByTestId( "CompanyDeleteInfo-container", );
  readonly companyDeleteIcon: Locator = this.companyDeleteContainer.locator("img");
  readonly companyDeleteLabel: Locator = this.companyDeleteContainer.locator("p");
  readonly companyDeleteLink: Locator = this.companyDeleteContainer.locator("a");
  readonly companySelectAddressIBButton: Locator = this.page.getByTestId( "selectAddress-IB-Form-Select-Button", );
  readonly editMainContactSearchInput: Locator = this.page.getByTestId( "PeoplePicker-Form-Input", );
  readonly firstMainContactOption: Locator = this.page .locator( '//div[@data-radix-popper-content-wrapper]//div[@data-side="bottom"]//button[starts-with(@data-testid, "PeoplePicker-IB-Form-People-Picker-Option-EMPL")]', ) .first();
  readonly mainContactErrorTooltipLabel: Locator = this.page .getByTestId("PeoplePicker-Error-Tooltip") .locator("span");
  readonly editMainContactSearchByNameOrEmailLabel: Locator = this.page.getByTestId("PeoplePicker-Label");
  readonly editMainContactEmailLabel: Locator = this.page.locator( '//div[@data-testid="CompanyMainContact-company-details-container"]//div[@class="flex flex-col py-4"]//span[@class="font-bold"]', );
  readonly editMainContactEmailAddressLabel: Locator = this.page.locator( '(//div[@data-testid="CompanyMainContact-company-details-container"]//div[@class="flex flex-col py-4"]//span)[2]', );
  readonly editMainContactContactNumberLabel: Locator = this.page.locator( '//div[@data-testid="CompanyMainContact-company-details-container"]//div[@class="flex flex-col pb-4"]//span[@class="font-bold"]', );
  readonly editMainContactActualContactNumberLabel: Locator = this.page.locator( '(//div[@data-testid="CompanyMainContact-company-details-container"]//div[@class="flex flex-col pb-4"]//span)[2]', );
  readonly editMainContactJobTitleLabel: Locator = this.page.getByTestId( "CompanyMainContact-job-title-Label", );
  readonly editMainContactJobTitleInput: Locator = this.page.getByTestId( "CompanyMainContact-job-title-Form-Input", );
  readonly editCompanyDetailsDiscardChangesButton: Locator = this.page.getByTestId("CompanyInformation-Discard-changes-Company");
  readonly selectCompanySectorButton: Locator = this.page.getByTestId( "companySector-IB-Form-Select-Button", );
  readonly selectAverageBookingsButton: Locator = this.page.getByTestId( "averageMonthlyBooking-IB-Form-Select-Button", );
  readonly selectNumberOfEmployeesButton: Locator = this.page.getByTestId( "numberOfEmployee-IB-Form-Select-Button", );
  readonly companySectorDropdownItemsLabels: Locator = this.page.locator( 'div[data-testid="companySector-IB-Form-Select-Dropdown"] button[data-testid^="companySector-"]', );
  readonly averageMonthlyBookingsDropdownItemsLabels: Locator = this.page.locator( 'div[data-testid="averageMonthlyBooking-IB-Form-Select-Dropdown"] button[data-testid^="averageMonthlyBooking-"]', );
  readonly numberOfEmployeesDropdownItemsLabels: Locator = this.page.locator( 'div[data-testid="numberOfEmployee-IB-Form-Select-Dropdown"] button[data-testid^="numberOfEmployee-"]', );

  // UI components
  readonly companyAddress = new CompanyAddressSectionComponent();
  readonly reviewChanges = new ReviewChangesModalComponent();
  readonly menuContainer = new MenuContainerComponent();
  readonly headerSection = new HeaderSectionComponent();

  // ######## UI actions/navigation ########
  /** Open IB Company Details page. */
  async open(): Promise<void> {
    console.log("Open IB Company Details page");
    await this.openPath(this.url);
    await this.validatePage();
  }

  /** Click Company Info Edit button. */
  async clickCompanyInfoEditButton(): Promise<void> {
    console.log("Click Company Info Edit button");
    await this.companyInfoEditButton.click();
  }

  /** Set Company name input. */
  async setCompanyNameInput({
    companyName,
    pressTab = false,
  }: SetCompanyNameInputOptions): Promise<void> {
    console.log(`Company Name Input = ${companyName}`);
    await this.companyNameInput.fill(companyName);
    if (pressTab) await this.companyNameInput.press("Tab");
  }

  /** Click outside Company name input. */
  async clickOutsideCompanyNameInput(): Promise<void> {
    console.log("Click outside Company name input");
    await this.companyNameInput.blur();
  }

  /** Click edit main contact search input. */
  async clickEditMainContactSearchInput(): Promise<void> {
    console.log("Click edit main contact search input");
    await this.editMainContactSearchInput.click();
  }

  /** Click Main Contact Edit button. */
  async clickMainContactEditButton(): Promise<void> {
    console.log("Click Main Contact Edit button");
    await this.mainContactEditButton.click();
  }

  /** Clear job title field. */
  async clearJobTitleField(): Promise<void> {
    console.log("Clear job title field");
    await this.clickMainContactEditButton();
    await this.setJobTitle({ jobTitle: "" });
    await this.clickSaveUpdatesButton();
  }

  /** Click Additional Details Edit button. */
  async clickAdditionalDetailsEditButton(): Promise<void> {
    console.log("Click Additional Details Edit button");
    await this.additionalDetailsEditButton.click();
  }

  /** Click Save updates button. */
  async clickSaveUpdatesButton(): Promise<void> {
    console.log("Click Save updates button");
    await this.saveButton.click();
  }

  /** Click Discard changes button. */
  async clickDiscardChangesButton(): Promise<void> {
    console.log("Click Discard changes button");
    await this.editCompanyDetailsDiscardChangesButton.click();
  }

  /** Click Contact us link. */
  async clickContactUsLink(): Promise<void> {
    console.log("Click Contact us link");
    await this.companyDeleteLink.click();
  }

  /** Click first Main Contact option. */
  async clickFirstMainContactOption(): Promise<void> {
    console.log("Click first Main Contact option");
    await this.firstMainContactOption.click();
  }

  /** Search by name or email input. */
  async searchMainContactByNameOrEmail({
    userNameOrEmail,
    pressTab = false,
  }: SearchMainContactOptions): Promise<void> {
    console.log(`Main Contact Search Input = ${userNameOrEmail}`);
    await this.editMainContactSearchInput.fill(userNameOrEmail);
    if (pressTab) await this.editMainContactSearchInput.press("Tab");
  }

  /** Set job title input. */
  async setJobTitle({
    jobTitle,
    pressTab = false,
  }: SetJobTitleOptions): Promise<void> {
    console.log(`Job Title Input = ${jobTitle}`);
    await expect( this.editMainContactJobTitleInput, "Job title input", ).toBeVisible();
    await expect( this.editMainContactSearchInput, "Main contact search input has a non-empty value", ).not.toHaveValue("");
    await this.editMainContactJobTitleInput.fill(jobTitle);
    if (pressTab) await this.editMainContactJobTitleInput.press("Tab");
  }

  /** Click Select Company sector dropdown button. */
  async clickSelectCompanySectorDropdownButton(): Promise<void> {
    console.log("Click Select Company sector dropdown button");
    await this.selectCompanySectorButton.click();
  }

  /** Click Select Average monthly bookings dropdown button. */
  async clickSelectAverageBookingsDropdownButton(): Promise<void> {
    console.log("Click Select Average monthly bookings dropdown button");
    await this.selectAverageBookingsButton.click();
  }

  /** Click Select Number of employees dropdown button. */
  async clickSelectNumberOfEmployeesDropdownButton(): Promise<void> {
    console.log("Click Select Number of employees dropdown button");
    await this.selectNumberOfEmployeesButton.click();
  }

  /** Click Company sector dropdown item by index. */
  async clickCompanySectorDropdownItem(index = 0): Promise<void> {
    console.log(`Click Company sector dropdown item ${index}`);
    const count = await this.companySectorDropdownItemsLabels.count();
    if (index < 0 || index >= count)
      throw new Error(
        `Invalid index ${index} for Company sector dropdown items`,
      );
    await this.companySectorDropdownItemsLabels.nth(index).click();
  }

  /** Click average monthly bookings dropdown item by index. */
  async clickAverageMonthlyBookingsDropdownItem(index = 0): Promise<void> {
    console.log(`Click Average monthly bookings dropdown item ${index}`);
    const count = await this.averageMonthlyBookingsDropdownItemsLabels.count();
    if (index < 0 || index >= count)
      throw new Error(
        `Invalid index ${index} for Average monthly bookings dropdown items`,
      );
    await this.averageMonthlyBookingsDropdownItemsLabels.nth(index).click();
  }

  /** Click number of employees dropdown item by index. */
  async clickNumberOfEmployeesDropdownItem(index = 0): Promise<void> {
    console.log(`Click Number of employees dropdown item ${index}`);
    const count = await this.numberOfEmployeesDropdownItemsLabels.count();
    if (index < 0 || index >= count)
      throw new Error(
        `Invalid index ${index} for Number of employees dropdown items`,
      );
    await this.numberOfEmployeesDropdownItemsLabels.nth(index).click();
  }

  // ######## UI validations ########
  /** Check we reached the current page by checking a specific element from the page. */
  async validatePage(): Promise<void> {
    console.log("Validate Company Details page");
    await this.validatePageMarker(this.headerLabel, "Company Details");
  }

  /** Validate Company info title. */
  async validateCompanyInfoTitle(): Promise<void> {
    console.log("Validate Company info title");
    await expect(this.companyInfoTitleLabel, "Company info title").toHaveText( await IbStrings.COMPANY_INFORMATION.name, );
  }

  /** Validate Company Info labels. */
  async validateCompanyInfoLabels(companyInfo: CompanyInfo): Promise<void> {
    console.log("Validate Company Info labels");
    await this.validateCompanyInfoTitle();
    await expect(this.companyNameLabel, "Company name").toHaveText( companyInfo.companyName, );
    const address = this.normaliseAddress(companyInfo.companyAddress);
    if (address.addressLine1)
      await expect( this.companyAddress.companyAddress1Label, "Company address line 1", ).toHaveText(address.addressLine1);
    if (address.addressLine2)
      await expect( this.companyAddress.companyAddress2Label, "Company address line 2", ).toHaveText(address.addressLine2);
    if (address.addressLine3)
      await expect( this.companyAddress.companyAddress3Label, "Company address line 3", ).toHaveText(address.addressLine3);
    if (address.addressLine4)
      await expect( this.companyAddress.companyAddress4Label, "Company address line 4", ).toHaveText(address.addressLine4);
    if (address.country === "GB" && address.addressLine5)
      await expect( this.companyAddress.companyAddress5Label, "Company address line 5", ).toHaveText(address.addressLine5);
    await expect( this.companyAddress.companyPostCodeLabel, "Company postcode", ).toHaveText(address.postCode);
  }

  /** Validate Company name. */
  async validateCompanyName(companyName: string): Promise<void> {
    console.log("Validate Company name");
    await expect(this.companyNameLabel, "Company name label").toHaveText( companyName, );
  }

  /** Validate Company Name input. */
  async validateCompanyNameInput(
    companyName: string,
    isValid = true,
  ): Promise<void> {
    console.log("Validate Company Name input");
    const inputValue = await this.companyNameInput.inputValue();
    console.log(`Company Name input value = ${inputValue}`);
    const expectedValue =
      companyName.length >= 100 && companyName !== inputValue
        ? `${companyName.substring(0, 52)}${companyName.substring(53)}`
        : companyName;
    await expect( this.companyNameInput, "Company name input placeholder", ).toHaveAttribute( "placeholder", `${await IbStrings.COMPANY_NAME_IB.name} *`, );
    await expect(this.companyNameInput, "Company name input value").toHaveValue( expectedValue, );
    if (!companyName || !isValid) {
      await expect( this.companyNameInputErrorLabel, "Company name error label", ).toHaveText(await IbStrings.PLEASE_ENTER_A_VALID_COMPANY_NAME.name);
      return;
    }
    await expect( this.companyNameInputErrorLabel, "Company name error label", ).not.toBeVisible();
  }

  /** Validate Company Info inputs. */
  async validateCompanyInfoInputs(
    companyInfo: CompanyInfo,
    isEnCompany = true,
  ): Promise<void> {
    console.log("Validate Company inputs");
    await this.validateCompanyInfoTitle();
    await this.validateCompanyNameInput(companyInfo.companyName);
    const address = this.normaliseAddress(companyInfo.companyAddress);
    const isEnglishAddress = isEnCompany && address.country === "GB";
    if (isEnglishAddress)
      await this.companyAddress.validatePostCodeInput(
        address.postCode,
        Boolean(address.postCode),
      );
    await this.companyAddress.validateAddress1Input(address.addressLine1);
    await this.companyAddress.validateAddress2Input(address.addressLine2);
    await this.companyAddress.validateAddress3Input(address.addressLine3);
    await this.companyAddress.validateAddress4Input(
      address.addressLine4,
      isEnglishAddress,
    );
    if (isEnglishAddress)
      await this.companyAddress.validateAddress5Input(address.addressLine5);
  }

  /** Validate Main contact title. */
  async validateMainContactTitle(): Promise<void> {
    console.log("Validate Main contact title");
    await expect(this.mainContactTitleLabel, "Main contact title").toHaveText( await IbStrings.MAIN_CONTACT.name, );
  }

  /** Validate Main Contact labels. */
  async validateMainContactLabels({
    mainContact,
    isMainContactNameEmailDisplayed = true,
    isMainContactIncompleteBadgeDisplayed = false,
    isMainContactJobTitleDisplayed = true,
  }: ValidateMainContactLabelsOptions): Promise<void> {
    console.log("Validate Main Contact labels");
    await this.validateMainContactTitle();
    if (isMainContactIncompleteBadgeDisplayed)
      await expect( this.mainContactIncompleteBadgeLabel, "Main contact badge label", ).toHaveText(await IbStrings.INCOMPLETE.name);
    else
      await expect( this.mainContactIncompleteBadgeLabel, "Main contact badge label", ).not.toBeVisible();
    if (isMainContactNameEmailDisplayed) {
      await expect( this.mainContactNameLabel, "Main contact name label", ).toHaveText( `${mainContact.title ? `${mainContact.title} ` : ""}${mainContact.contactName}`, );
      await expect( this.mainContactEmailAddressLabel, "Main contact email address label", ).toHaveText(mainContact.emailAddress);
    } else {
      await expect( this.mainContactNameLabel, "Main contact name label", ).not.toBeVisible();
      await expect( this.mainContactEmailAddressLabel, "Main contact email address label", ).not.toBeVisible();
    }
    if (isMainContactJobTitleDisplayed)
      await expect( this.mainContactJobTitleLabel, "Main contact job title label", ).toHaveText(mainContact.jobTitle ?? "");
    else
      await expect( this.mainContactJobTitleLabel, "Main contact job title label", ).not.toBeVisible();
  }

  /** Validate Additional details title. */
  async validateAdditionalDetailsTitle(): Promise<void> {
    console.log("Validate Additional details title");
    await expect( this.additionalDetailsTitleLabel, "Additional details title", ).toHaveText(await IbStrings.ADDITIONAL_DETAILS.name);
  }

  /** Validate Additional details labels. */
  async validateAdditionalDetailsLabels({
    companySector,
    averageMonthlyBookings,
    numberOfEmployees,
  }: ValidateAdditionalDetailsLabelsOptions = {}): Promise<void> {
    console.log("Validate Additional details labels");
    await this.validateAdditionalDetailsTitle();
    const notSelected = await IbStrings.NOT_SELECTED.name;
    const resolvedCompanySector = companySector ?? notSelected;
    const resolvedAverageMonthlyBookings =
      averageMonthlyBookings ?? notSelected;
    const resolvedNumberOfEmployees = numberOfEmployees ?? notSelected;
    if (
      resolvedCompanySector === notSelected ||
      resolvedAverageMonthlyBookings === notSelected ||
      resolvedNumberOfEmployees === notSelected
    )
      await expect( this.additionalDetailsBadgeLabel, "Additional details badge", ).toHaveText(await IbStrings.INCOMPLETE.name);
    else
      await expect( this.additionalDetailsBadgeLabel, "Additional details badge", ).not.toBeVisible();
    const additionalDetailsStrings = [
      await IbStrings.COMPANY_SECTOR.name,
      resolvedCompanySector,
      await IbStrings.AVERAGE_MONTHLY_BOOKINGS.name,
      resolvedAverageMonthlyBookings,
      await IbStrings.NUMBER_OF_EMPLOYEES.name,
      resolvedNumberOfEmployees,
    ];
    for (const [index, text] of additionalDetailsStrings.entries())
      await expect( this.additionalDetailsLabels.nth(index), `Additional details label ${index}`, ).toHaveText(text);
  }

  /** Validate Select Address button. */
  async validateSelectAddressButton(): Promise<void> {
    console.log("Validate Select Address button");
    await expect( this.companySelectAddressIBButton, "Select address button", ).toBeVisible();
  }

  /** Validate Save button. */
  async validateSaveButton(isSaveButtonDisplayed = true): Promise<void> {
    console.log("Validate Save button");
    if (isSaveButtonDisplayed)
      await expect(this.saveButton, "Save button").toBeVisible();
    else await expect(this.saveButton, "Save button").not.toBeVisible();
  }

  /** Validate Main contact error tooltip. */
  async validateMainContactErrorTooltip({
    searchedText = "",
    isDisplayed = true,
  }: ValidateMainContactErrorTooltipOptions = {}): Promise<void> {
    console.log("Validate Main contact error tooltip");
    if (!isDisplayed) {
      await expect( this.mainContactErrorTooltipLabel, "Main contact error tooltip", ).not.toBeVisible();
      return;
    }
    await expect( this.mainContactErrorTooltipLabel, "Main contact error tooltip", ).toHaveText( (await IbStrings.NO_EMPLOYEE_FOUND.name) .replace("searchTerm", searchedText) .replace(/%/g, "'"), );
  }

  /** Validate Main contact Incomplete label. */
  async validateMainContactIncompleteLabel(isDisplayed = true): Promise<void> {
    console.log("Validate Main contact Incomplete label");
    if (isDisplayed)
      await expect( this.mainContactIncompleteBadgeLabel, "Main contact label", ).toHaveText(await IbStrings.INCOMPLETE.name);
    else
      await expect( this.mainContactIncompleteBadgeLabel, "Main contact label", ).not.toBeVisible();
  }

  /** Validate Edit Main Contact section. */
  async validateEditMainContactSection({
    mainContact,
    hasEmail = true,
    hasContactNumber = true,
  }: ValidateEditMainContactSectionOptions): Promise<void> {
    console.log("Validate Edit Main Contact section");
    await this.validateMainContactTitle();
    const searchLabel = await IbStrings.SEARCH_BY_NAME_OR_EMAIL_IB.name;
    const mainContactDisplayName = mainContact.title
      ? `${mainContact.title} ${mainContact.contactName}`
      : mainContact.contactName;
    const mainContactValue = hasEmail
      ? `${mainContactDisplayName} (${mainContact.emailAddress})`
      : mainContactDisplayName;
    await expect( this.editMainContactSearchByNameOrEmailLabel, "Edit Main contact search label", ).toHaveText(`${searchLabel} *`);
    await expect( this.editMainContactSearchInput, "Edit Main contact search input placeholder", ).toHaveAttribute("placeholder", `${searchLabel} *`);
    await expect( this.editMainContactSearchInput, "Edit Main contact search input value", ).toHaveValue(mainContactValue);
    await expect( this.editMainContactEmailLabel, "Edit Main contact email label", ).toHaveText(await IbStrings.FORGOTTEN_PASSWORD_EMAIL.name);
    if (hasEmail)
      await expect( this.editMainContactEmailAddressLabel, "Edit Main contact email address", ).toHaveText(mainContact.emailAddress);
    else
      await expect( this.editMainContactEmailAddressLabel, "Edit Main contact email address", ).not.toBeVisible();
    await expect( this.editMainContactContactNumberLabel, "Edit Main contact contact number label", ).toHaveText(await IbStrings.CONTACT_NUMBER_LABEL.name);
    if (hasContactNumber)
      await expect( this.editMainContactActualContactNumberLabel, "Edit Main contact contact number", ).toHaveText(mainContact.contactNumber);
    else
      await expect( this.editMainContactActualContactNumberLabel, "Edit Main contact contact number", ).not.toBeVisible();
    await expect( this.editMainContactJobTitleInput, "Edit Main contact job title placeholder", ).toHaveAttribute("placeholder", await IbStrings.JOB_TITLE.name);
    await expect( this.editMainContactJobTitleInput, "Edit Main contact job title value", ).toHaveValue(mainContact.jobTitle ?? "");
  }

  /** Validate toast message. */
  async validateToastMessage(
    expectedMessage: string | Promise<string>,
  ): Promise<void> {
    console.log("Validate toast message");
    await this.toastNotificationSection.validateToastNotification({
      message: expectedMessage,
    });
  }

  /** Validate Company Sector dropdown button. */
  async validateCompanySectorDropdownButton(value?: string): Promise<void> {
    console.log("Validate Company Sector dropdown button");
    await expect( this.selectCompanySectorButton, "Company sector dropdown button", ).toHaveText( value ?? (await IbStrings.ADDITIONAL_DETAILS_SELECT_PLACEHOLDER.name), );
  }

  /** Validate average monthly bookings dropdown button. */
  async validateAverageMonthlyBookingsDropdownButton(
    value?: string,
  ): Promise<void> {
    console.log("Validate Average monthly bookings dropdown button");
    await expect( this.selectAverageBookingsButton, "Average monthly bookings dropdown button", ).toHaveText( value ?? (await IbStrings.ADDITIONAL_DETAILS_SELECT_PLACEHOLDER.name), );
  }

  /** Validate number of employees dropdown button. */
  async validateNumberOfEmployeesDropdownButton(value?: string): Promise<void> {
    console.log("Validate Number of Employees dropdown button");
    await expect( this.selectNumberOfEmployeesButton, "Number of employees dropdown button", ).toHaveText( value ?? (await IbStrings.ADDITIONAL_DETAILS_SELECT_PLACEHOLDER.name), );
  }

  /** Validate Company Sector dropdown. */
  async validateCompanySectorDropdown(): Promise<void> {
    console.log("Validate Company Sector dropdown");
    const dropdownList = await this.companySectorValues();
    await expect( this.companySectorDropdownItemsLabels, "Company sector dropdown item count", ).toHaveCount(dropdownList.length);
    for (const [index, text] of dropdownList.entries())
      await expect( this.companySectorDropdownItemsLabels.nth(index), `Company sector dropdown item ${index}`, ).toHaveText(text);
  }

  /** Validate Average monthly bookings dropdown. */
  async validateAverageMonthlyBookingsDropdown(): Promise<void> {
    console.log("Validate Average monthly bookings dropdown");
    const dropdownList = await this.averageMonthlyBookingValues();
    await expect( this.averageMonthlyBookingsDropdownItemsLabels, "Average monthly bookings dropdown item count", ).toHaveCount(dropdownList.length);
    for (const [index, text] of dropdownList.entries())
      await expect( this.averageMonthlyBookingsDropdownItemsLabels.nth(index), `Average monthly bookings dropdown item ${index}`, ).toHaveText(text);
  }

  /** Validate Number of employees dropdown. */
  async validateNumberOfEmployeesDropdown(): Promise<void> {
    console.log("Validate Number of employees dropdown");
    const dropdownList = await this.numberOfEmployeeValues();
    await expect( this.numberOfEmployeesDropdownItemsLabels, "Number of employees dropdown item count", ).toHaveCount(dropdownList.length);
    for (const [index, text] of dropdownList.entries())
      await expect( this.numberOfEmployeesDropdownItemsLabels.nth(index), `Number of employees dropdown item ${index}`, ).toHaveText(text);
  }

  /** Return all company sector dropdown labels. */
  private async companySectorValues(): Promise<string[]> {
    return Promise.all([
      IbStrings.ADDITIONAL_DETAILS_SECTOR_1.name,
      IbStrings.ADDITIONAL_DETAILS_SECTOR_2.name,
      IbStrings.ADDITIONAL_DETAILS_SECTOR_3.name,
      IbStrings.ADDITIONAL_DETAILS_SECTOR_4.name,
      IbStrings.ADDITIONAL_DETAILS_SECTOR_5.name,
      IbStrings.ADDITIONAL_DETAILS_SECTOR_6.name,
      IbStrings.ADDITIONAL_DETAILS_SECTOR_7.name,
      IbStrings.ADDITIONAL_DETAILS_SECTOR_8.name,
      IbStrings.ADDITIONAL_DETAILS_SECTOR_9.name,
      IbStrings.ADDITIONAL_DETAILS_SECTOR_10.name,
      IbStrings.ADDITIONAL_DETAILS_SECTOR_11.name,
      IbStrings.ADDITIONAL_DETAILS_SECTOR_12.name,
      IbStrings.ADDITIONAL_DETAILS_SECTOR_13.name,
      IbStrings.ADDITIONAL_DETAILS_SECTOR_14.name,
      IbStrings.ADDITIONAL_DETAILS_SECTOR_15.name,
      IbStrings.ADDITIONAL_DETAILS_SECTOR_16.name,
      IbStrings.ADDITIONAL_DETAILS_SECTOR_17.name,
      IbStrings.ADDITIONAL_DETAILS_SECTOR_18.name,
      IbStrings.ADDITIONAL_DETAILS_SECTOR_19.name,
      IbStrings.ADDITIONAL_DETAILS_SECTOR_20.name,
      IbStrings.ADDITIONAL_DETAILS_SECTOR_21.name,
    ]);
  }

  /** Return all average monthly booking dropdown labels. */
  private async averageMonthlyBookingValues(): Promise<string[]> {
    return Promise.all([
      IbStrings.ADDITIONAL_DETAILS_BOOKINGS_1.name,
      IbStrings.ADDITIONAL_DETAILS_BOOKINGS_2.name,
      IbStrings.ADDITIONAL_DETAILS_BOOKINGS_3.name,
      IbStrings.ADDITIONAL_DETAILS_BOOKINGS_4.name,
      IbStrings.ADDITIONAL_DETAILS_BOOKINGS_5.name,
    ]);
  }

  /** Return all number of employee dropdown labels. */
  private async numberOfEmployeeValues(): Promise<string[]> {
    return Promise.all([
      IbStrings.ADDITIONAL_DETAILS_EMPLOYEES_1.name,
      IbStrings.ADDITIONAL_DETAILS_EMPLOYEES_2.name,
      IbStrings.ADDITIONAL_DETAILS_EMPLOYEES_3.name,
      IbStrings.ADDITIONAL_DETAILS_EMPLOYEES_4.name,
      IbStrings.ADDITIONAL_DETAILS_EMPLOYEES_5.name,
    ]);
  }

  /** Normalise company address field aliases returned by Company Details APIs. */
  private normaliseAddress(
    address: CompanyAddress,
  ): Required<
    Pick<
      CompanyAddress,
      | "addressLine1"
      | "addressLine2"
      | "addressLine3"
      | "addressLine4"
      | "addressLine5"
      | "postCode"
      | "country"
    >
  > {
    return {
      addressLine1: address.addressLine1 ?? address.line1 ?? "",
      addressLine2: address.addressLine2 ?? address.line2 ?? "",
      addressLine3: address.addressLine3 ?? address.line3 ?? "",
      addressLine4: address.addressLine4 ?? address.line4 ?? "",
      addressLine5: address.addressLine5 ?? address.line5 ?? "",
      postCode:
        address.postCode ?? address.postalCode ?? address.postcode ?? "",
      country: address.country ?? address.countryCode ?? "",
    };
  }
}
