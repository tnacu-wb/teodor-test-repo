import { expect, type Locator } from "@playwright/test";
import {
  AccountSettingsSectionComponent,
  CompanyAddressSectionComponent,
  PersonalDetailsSectionComponent,
  RegistrationQuestionsFromComponent,
  ReviewChangesModalComponent,
  ToastNotificationSectionComponent,
} from "../../components/pib";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { BasePibPage } from "./basePib.page";

interface EmployeePersonalDetails {
  title: string;
  firstName?: string;
  lastName?: string;
  emailAddress?: string;
  mobilePhone?: string;
  alternatePhone?: string;
}

interface EmployeeDetails extends EmployeePersonalDetails {
  employeeStatus: string;
  addressLine1?: string;
  addressLine2?: string;
  addressLine3?: string;
  addressLine4?: string;
  addressLine5?: string;
  postCode?: string;
  postalCode?: string;
}

interface SetEmployeeDetailsOptions {
  userDetails: EmployeePersonalDetails;
  userRole: string | Promise<string>;
  cardLabel?: string | Promise<string>;
}

interface ValidateAddEditEmployeeDataOptions {
  employeeDetails: EmployeeDetails;
  isEdit?: boolean;
}

interface ValidateCompanyAddressSectionOptions {
  employeeDetails: EmployeeDetails;
  isEdit?: boolean;
  isManualAddress?: boolean;
}

/**
 * InnBusiness application > Manage > Employees > Add/Edit Employee
 */
export class AddEditEmployeePage extends BasePibPage {
  readonly url = "manage/employees/add";

  // ######## UI elements/properties ########
  readonly addEditEmployeeBackButton: Locator = this.page.getByTestId( "AddEditEmployee-back-icon", );
  readonly addEditEmployeePageTitleLabel: Locator = this.page.getByTestId( "AddEditEmployee-title", );
  readonly addEmployeePageDescriptionLabel: Locator = this.addEditEmployeePageTitleLabel.locator( "xpath=parent::node()/following-sibling::p", );
  readonly addMultipleEmployeesLink: Locator = this.addEditEmployeePageTitleLabel.locator( "xpath=parent::node()/following-sibling::a[1]", );
  readonly inviteEmployeesLink: Locator = this.addEditEmployeePageTitleLabel.locator( "xpath=parent::node()/following-sibling::a[2]", );
  readonly companyAddressForm: Locator = this.page.getByTestId("CompanyAddressForm");
  readonly companyAddressTitleLabel: Locator = this.page.getByTestId( "CompanyAddressForm-heading", );
  readonly companySearchNewAddressButton: Locator = this.page.locator( 'button[data-testid="CompanyAddressForm-search-for-new-address"] span', );
  readonly submitAddEditEmployeeButton: Locator = this.page.getByTestId( "Submit-Employee-Details", );
  readonly cancelAddEditEmployeeButton: Locator = this.page.getByTestId( "IB-Add-Edit-Cancel-Button", );
  readonly weSendActivationEmailTooltipLabel: Locator = this.page.locator( '//div[contains(@class,"bg-tooltipInfo")]/div', );

  // UI components
  readonly personalDetails = new PersonalDetailsSectionComponent();
  readonly accountSettings = new AccountSettingsSectionComponent();
  readonly registrationQuestions = new RegistrationQuestionsFromComponent();
  readonly reviewChanges = new ReviewChangesModalComponent();
  readonly companyAddress = new CompanyAddressSectionComponent();
  readonly toastNotificationSection = new ToastNotificationSectionComponent();

  // ######## UI actions/navigation ########
  /** Open IB Add Employee page. */
  async open(): Promise<void> {
    console.log("Open IB Add Employee page");
    await this.openPath(this.url);
    await this.validatePage();
  }

  /** Click Submit (Add employee / Save updates) button. */
  async clickSubmitAddEditEmployeeButton(): Promise<void> {
    console.log("Click Submit Add/Edit Employee button");
    await this.submitAddEditEmployeeButton.scrollIntoViewIfNeeded();
    await this.submitAddEditEmployeeButton.click();
  }

  /** Click Cancel (Add employee / Save updates) button. */
  async clickCancelAddEditEmployeeButton(): Promise<void> {
    console.log("Click Cancel Add/Edit Employee button");
    await this.cancelAddEditEmployeeButton.click();
  }

  /** Click Add/Edit employee back button. */
  async clickAddEditEmployeeBackButton(): Promise<void> {
    console.log("Click Add/Edit employee back button");
    await this.addEditEmployeeBackButton.click();
  }

  /** Click search for a new address. */
  async clickSearchNewAddressButton(): Promise<void> {
    console.log("Click search for a new address button");
    await this.companySearchNewAddressButton.click();
  }

  /** Click add multiple employees via bulk upload link. */
  async clickAddMultipleEmployeesLink(): Promise<void> {
    console.log("Click add multiple employees via bulk upload link");
    await this.addMultipleEmployeesLink.scrollIntoViewIfNeeded();
    await this.addMultipleEmployeesLink.click();
  }

  /** Click invite employees to add themselves link. */
  async clickInviteEmployeesLink(): Promise<void> {
    console.log("Click add employees to add themselves link");
    await this.inviteEmployeesLink.scrollIntoViewIfNeeded();
    await this.inviteEmployeesLink.click();
  }

  /** Set employee details to add employee. */
  async setEmployeeDetails({
    userDetails,
    userRole,
    cardLabel,
  }: SetEmployeeDetailsOptions): Promise<void> {
    console.log("Set employee details to add employee");
    await this.personalDetails.setEmployeeDetails(userDetails);
    await this.accountSettings.clickUserRoleRadioButton({ userRole });
    if (cardLabel) await this.accountSettings.selectEmployeeCard(cardLabel);
  }

  // ######## UI validations ########
  /** Validate Add and Edit employee page data. */
  async validateAddEditEmployeeData({
    employeeDetails,
    isEdit = true,
  }: ValidateAddEditEmployeeDataOptions): Promise<void> {
    console.log("Validate Add and Edit employee page data");
    if (isEdit) await this.validateEditEmployeesPage();
    else await this.validateAddEmployeesPage();
    if (!isEdit) await this.validateAddEmployeePageTopSectionElements();
    await this.personalDetails.validatePersonalDetailsSection({
      employeeDetails,
      isEdit,
    });
    await this.accountSettings.validateAccountSettingsData({
      employeeDetails,
      isEdit,
    });
    await this.validateCompanyAddressSection({ employeeDetails, isEdit });
    await expect( this.submitAddEditEmployeeButton, "Add employee button", ).toBeVisible();
    await expect( this.cancelAddEditEmployeeButton, "Cancel button", ).toBeVisible();
  }

  /** Validate Add employee page top section elements (description and links). */
  async validateAddEmployeePageTopSectionElements(): Promise<void> {
    console.log("Validate Add employee page top section elements");
    await expect( this.addEmployeePageDescriptionLabel, "Add employee page description label", ).toHaveText(await IbStrings.ADD_MULTIPLE_EMPLOYEES_DESCRIPTION.name);
    await expect( this.addMultipleEmployeesLink, "Add multiple employees via bulk upload label", ).toHaveText(await IbStrings.ADD_MULTIPLE_EMPLOYEES_BULK.name);
    await expect( this.inviteEmployeesLink, "Invite employees to add themselves label", ).toHaveText(await IbStrings.INVITE_EMPLOYEES.name);
  }

  /** Validate send activation email info tooltip message. */
  async validateSendActivationEmailTooltip(): Promise<void> {
    console.log("Validate send activation email tooltip");
    const invitationText = await IbStrings.WE_LL_SEND_AN_INVITATION.name;
    const employeesText = await IbStrings.EMPLOYEES_YOU_WILL_ADD.name;
    const expectedText = `${invitationText.replace(/'/g, "'")}\n${employeesText.replace(/\.\s/, ".\n")}`;
    await expect( this.weSendActivationEmailTooltipLabel, "Send activation email tooltip message", ).toHaveText(expectedText);
  }

  /** Check we reached the add employee page by checking page title. */
  async validateAddEmployeesPage(): Promise<void> {
    console.log("Validate Add Employees page");
    await expect( this.addEditEmployeePageTitleLabel, "Add employee page title label", ).toHaveText(await IbStrings.ADD_AN_EMPLOYEE.name);
  }

  /** Check we reached the employees details page by checking page title. */
  async validateEditEmployeesPage(): Promise<void> {
    console.log("Validate Edit Employees page");
    await expect( this.addEditEmployeePageTitleLabel, "Edit employee page title label", ).toHaveText(await IbStrings.EDIT_EMPLOYEE_DETAILS.name);
  }

  /** Validate Company address section. */
  async validateCompanyAddressSection({
    employeeDetails,
    isEdit = false,
    isManualAddress = false,
  }: ValidateCompanyAddressSectionOptions): Promise<void> {
    console.log("Validate Company address section");
    await expect( this.companyAddressForm, "Company address section", ).toBeVisible();
    await expect( this.companyAddressTitleLabel, "Company Address title label", ).toHaveText(await IbStrings.COMPANY_ADDRESS_IB.name);
    await this.validateCompanyAddressLabels(
      employeeDetails,
      isEdit,
      isManualAddress,
    );
    await expect( this.companySearchNewAddressButton, "Search for a new address button", ).toHaveText(await IbStrings.SEARCH_FOR_A_NEW_ADDRESS.name);
  }

  /** Check we reached the current page by checking a specific element from the page. */
  async validatePage(): Promise<void> {
    console.log("Validate Add/Edit Employee page");
    await this.validatePageMarker(
      this.addEditEmployeePageTitleLabel,
      "Add/Edit Employee",
    );
  }

  /** Validate the address values displayed in the Company address section. */
  private async validateCompanyAddressLabels(
    employeeDetails: EmployeeDetails,
    isEdit: boolean,
    isManualAddress: boolean,
  ): Promise<void> {
    console.log("Validate Company address labels");
    const addressFields = [
      [
        employeeDetails.addressLine1,
        this.companyAddress.companyAddress1Label,
        this.companyAddress.companyAddress1Input,
        "Company address line 1",
      ],
      [
        employeeDetails.addressLine2,
        this.companyAddress.companyAddress2Label,
        this.companyAddress.companyAddress2Input,
        "Company address line 2",
      ],
      [
        employeeDetails.addressLine3,
        this.companyAddress.companyAddress3Label,
        this.companyAddress.companyAddress3Input,
        "Company address line 3",
      ],
      [
        employeeDetails.addressLine4,
        this.companyAddress.companyAddress4Label,
        this.companyAddress.companyAddress4Input,
        "Company address line 4",
      ],
      [
        employeeDetails.addressLine5,
        this.companyAddress.companyAddress5Label,
        this.companyAddress.companyAddress5Input,
        "Company address line 5",
      ],
    ] as const;
    for (const [value, label, input, description] of addressFields) {
      if (value === undefined) continue;
      if (isEdit && !isManualAddress)
        await expect(label, description).toHaveText(value);
      else await expect(input, description).toHaveValue(value);
    }
    const postCode = employeeDetails.postCode ?? employeeDetails.postalCode;
    if (postCode === undefined) return;
    if (isEdit && !isManualAddress)
      await expect( this.companyAddress.companyPostCodeLabel, "Company postcode", ).toHaveText(postCode);
    else
      await expect( isManualAddress ? this.companyAddress.companyManualPostCodeInput : this.companyAddress.companyPostCodeInput, "Company postcode", ).toHaveValue(postCode);
  }
}
