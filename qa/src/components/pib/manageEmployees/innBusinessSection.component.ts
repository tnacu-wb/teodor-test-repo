import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../../test-data/pib/ibStrings";
import { Strings } from "../../../test-data/strings";

/**
 * InnBusiness tab section from manage employees page
 */
export class InnBusinessSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########
  readonly ibEmployeeManagementTitleLabel: Locator = this.page.locator( 'div[data-testid="InnBusinessTab-title-section"] span', );
  readonly manageEmployeeTooltipImage: Locator = this.page.getByTestId( "InnBusinessTab-title-icon", );
  readonly manageEmployeesTooltipLabel: Locator = this.page .locator('div[data-testid="InnBusinessTab-title-section"] div span') .first();
  readonly employeeDownloadButton: Locator = this.page.getByTestId( "InnBusinessTab-DownloadButton-label", );
  readonly employeeAddButton: Locator = this.page.getByTestId( "InnBusinessTab-add-employee-button", );
  readonly searchEmployeesInput: Locator = this.page.getByTestId( "SearchEmployeeInput-search-employees-Input", );
  readonly searchEmployeesClearInputButton: Locator = this.page.getByTestId( "SearchEmployeeInput-search-input-icon", );
  readonly ibEmployeesTableHeader: Locator = this.page.locator( 'tr[data-testid="InnBusiness-DataTable-row"] th', );
  readonly ibEmployeeTableLoadMoreButton: Locator = this.page.getByTestId( "DataTablePage-LoadMoreButton", );
  readonly companyEmailsLabelsList: Locator = this.page.getByTestId( "UserInitials-name-and-email", );
  readonly employeesTableRows: Locator = this.page.locator( 'tbody[data-testid="InnBusiness-DataTable-body"] > tr', );
  readonly employeeTableNoResultLabel: Locator = this.page.getByTestId("UserNoResults");
  readonly resendActivationEmailLink: Locator = this.page.getByTestId( "Resend-Activation-Row-Button", );
  readonly resendActivationModal: Locator = this.page.getByTestId( "Resend-Activation-content", );
  readonly resendActivationCloseButton: Locator = this.page.getByTestId( "Dialog-X-Close-Button", );
  readonly resendActivationModalTitle: Locator = this.resendActivationCloseButton.locator( "xpath=following-sibling::div[1]//h2", );
  readonly resendActivationEmployeeProfileTitle: Locator = this.page.getByTestId("Resend-Activation-Box-title");
  readonly resendActivationEmployeeName: Locator = this.page.getByTestId( "Resend-Activation-Full-Name", );
  readonly resendActivationEmployeeEmail: Locator = this.page.getByTestId( "Resend-Activation-Email", );
  readonly resendActivationCancelButton: Locator = this.page.getByTestId( "Resend-Activation-Cancel-Button", );
  readonly resendActivationResendButton: Locator = this.page.getByTestId( "Resend-Activation-Submit-Button", );

  /** Get IB employee row by index. */
  getIbEmployeeTableRowByIndex(index: number): Locator {
    return this.page
      .locator(
        `tr[data-testid*="DataTablePage-row-"], tr[data-testId*="InnBusinessTab-row-loadMore-"]`,
      )
      .nth(index);
  }
  /** Get IB employee initials by index. */
  getIbEmployeeTableInitials(index: number): Locator {
    return this.getIbEmployeeTableRowByIndex(index).getByTestId(
      "UserInitials-userInitials",
    );
  }
  /** Get IB employee name by index. */
  getIbEmployeeTableName(index: number): Locator {
    return this.getIbEmployeeTableRowByIndex(index).getByTestId(
      "UserInitials-name",
    );
  }
  /** Get IB employee email by index. */
  getIbEmployeeTableEmail(index: number): Locator {
    return this.getIbEmployeeTableRowByIndex(index).getByTestId(
      "UserInitials-email",
    );
  }
  /** Get IB employee role by index. */
  getIbEmployeeTableUserRoleLabel(index: number): Locator {
    return this.getIbEmployeeTableRowByIndex(index).getByTestId(
      "UserRoleLabel",
    );
  }
  /** Get IB employee status by index. */
  getIbEmployeeTableUserStatusLabel(index: number): Locator {
    return this.getIbEmployeeTableRowByIndex(index).getByTestId("UserStatus");
  }
  /** Get IB employee edit button by index. */
  getIbEmployeeTableEditButton(index: number): Locator {
    return this.getIbEmployeeTableRowByIndex(index).getByTestId("UserActions");
  }

  // ######## UI actions/navigation ########
  /** Click Add employee button. */
  async clickAddEmployeeButton(): Promise<void> {
    console.log("Click add employee button");
    await this.employeeAddButton.click();
  }
  /** Click Load more button. */
  async clickLoadMoreButton(): Promise<void> {
    console.log("Click Load more button");
    await this.ibEmployeeTableLoadMoreButton.click();
  }
  /** Click X button from resend activation email modal. */
  async clickResendCloseButton(): Promise<void> {
    console.log("Click Close (X) button");
    await this.resendActivationCloseButton.click();
  }
  /** Click Cancel button from resend activation email modal. */
  async clickResendCancelButton(): Promise<void> {
    console.log("Click Cancel button");
    await this.resendActivationCancelButton.click();
  }
  /** Click resend activation code button from resend activation email modal. */
  async clickResendCodeButton(): Promise<void> {
    console.log("Click Resend code button");
    await this.resendActivationResendButton.click();
  }
  /** Find employee by name or email. */
  async searchEmployeeByNameOrEmail(
    searchedTerm: string,
    shouldBeValid = true,
  ): Promise<void> {
    console.log(`Find employee by email: ${searchedTerm}`);
    await this.searchEmployeesInput.fill(searchedTerm);
    if (shouldBeValid)
      await expect( this.employeesTableRows, "Employee table search results", ).not.toHaveCount(0);
    else
      await expect( this.employeeTableNoResultLabel, "No employee result label", ).toBeVisible();
  }
  /** Click Resend Activation email link. */
  async clickResendActivationEmailLink(): Promise<void> {
    console.log('Click "Resend Activation email" link');
    await this.resendActivationEmailLink.click();
    await expect( this.resendActivationModal, "Resend activation modal", ).toBeVisible();
  }
  /** Search employee by email and click Resend Activation Email link. */
  async searchEmployeeAncClickResendActivationEmailLink(
    searchedTerm: string,
  ): Promise<void> {
    console.log(`Search employee and resend activation email: ${searchedTerm}`);
    await this.searchEmployeeByNameOrEmail(searchedTerm);
    await this.clickResendActivationEmailLink();
  }
  /** Get employee row index by employee email or name. */
  async getEmployeeIndexByEmployeeEmail(
    searchedTerm: string,
  ): Promise<number | undefined> {
    console.log(`Get employee index by email or name: ${searchedTerm}`);
    const count = await this.companyEmailsLabelsList.count();
    for (let index = 0; index < count; index += 1)
      if (
        (await this.companyEmailsLabelsList.nth(index).textContent())?.includes(
          searchedTerm,
        )
      )
        return index;
    return undefined;
  }
  /** Edit Employee by employee email or name. */
  async clickEditEmployeeByEmployeeEmail(searchedTerm: string): Promise<void> {
    console.log(
      `Click edit button for employee with email or name: ${searchedTerm}`,
    );
    const employeeIndex =
      await this.getEmployeeIndexByEmployeeEmail(searchedTerm);
    if (employeeIndex === undefined)
      throw new Error(
        `Employee with email or name ${searchedTerm} was not found`,
      );
    await this.getIbEmployeeTableEditButton(employeeIndex).click();
  }
  /** Search employee by employee email or name and click Edit button for the employee. */
  async searchAndEditEmployeeByEmployeeEmail(
    searchedTerm: string,
  ): Promise<void> {
    console.log(`Search and edit employee: ${searchedTerm}`);
    await this.searchEmployeeByNameOrEmail(searchedTerm);
    await this.clickEditEmployeeByEmployeeEmail(searchedTerm);
  }
  /** Click X (Clear) button from search employee input. */
  async clickClearSearchEmployeeButton(): Promise<void> {
    console.log("Click dismiss search button");
    await this.searchEmployeesClearInputButton.click();
  }

  // ######## UI validations ########
  /** Validate employee status by email. */
  async validateEmployeeStatusByEmail({
    employeeEmail,
    expectedStatus,
  }: {
    employeeEmail: string;
    expectedStatus: { name: Promise<string> };
  }): Promise<void> {
    console.log(`Validate employee status for email: ${employeeEmail}`);
    const index = await this.getEmployeeIndexByEmployeeEmail(employeeEmail);
    if (index === undefined)
      throw new Error(`Employee ${employeeEmail} was not found`);
    await expect( this.getIbEmployeeTableUserStatusLabel(index), `Employee status for ${employeeEmail}`, ).toHaveText(await expectedStatus.name);
  }
  /** Validate employees table header. */
  async validateEmployeesTableHeader(): Promise<void> {
    console.log("Validate employees table header");
    await expect( this.ibEmployeesTableHeader, "Employees table header labels", ).toHaveText([ await IbStrings.NAME.name, await IbStrings.ACCOUNT_ROLE.name, await Strings.STATUS.name, "", ]);
  }
  /** Validate employees table content against getEmployees graphql. */
  async validateEmployeesTableContent(
    employees: Array<{
      firstName: string;
      lastName: string;
      emailAddress: string;
      accessLevel: string;
      employeeStatus: string;
    }>,
  ): Promise<void> {
    console.log("Validate employees table content");
    for (let index = 0; index < employees.length; index += 1) {
      const employee = employees[index];
      await expect( this.getIbEmployeeTableInitials(index), "Employee initials", ).toHaveText(`${employee.firstName[0]}${employee.lastName[0]}`);
      await expect( this.getIbEmployeeTableName(index), "Employee name", ).toHaveText(`${employee.firstName} ${employee.lastName}`);
      await expect( this.getIbEmployeeTableEmail(index), "Employee email", ).toHaveText(employee.emailAddress);
      await expect( this.getIbEmployeeTableUserRoleLabel(index), "Employee access role", ).toHaveText(employee.accessLevel);
      await expect( this.getIbEmployeeTableUserStatusLabel(index), "Employee status", ).toHaveText(employee.employeeStatus);
    }
  }
  /** Validate manage employees tooltip. */
  async validateManageEmployeesTooltip(): Promise<void> {
    console.log("Validate manage employees tooltip");
    await expect( this.manageEmployeeTooltipImage, "Manage employees tooltip icon", ).toBeVisible();
    await this.manageEmployeeTooltipImage.hover();
    await expect( this.manageEmployeeTooltipImage, "Manage employees tooltip open state", ).toHaveAttribute("data-state", "delayed-open");
    await expect( this.manageEmployeesTooltipLabel, "Manage employees tooltip", ).toHaveText( (await IbStrings.ACCOUNT_ROLE_TOOLTIP.name) .replace(":", ":\n\n") .replace(/\.([A-Z])/g, ".\n$1"), );
    await this.manageEmployeeTooltipImage.click();
  }
  /** Validate Manage employee page elements from InnBusiness tab. */
  async validateManageEmployeesIbPageElements(): Promise<void> {
    console.log("Validate Manage employee page elements - InnBusiness tab");
    await expect( this.ibEmployeeManagementTitleLabel, "Employee management title label", ).toHaveText(await IbStrings.EMPLOYEE_MANAGEMENT.name);
    await this.validateManageEmployeesTooltip();
    await expect(this.employeeDownloadButton, "Download button").toHaveText( await IbStrings.DOWNLOAD.name, );
    await expect(this.employeeAddButton, "Add employee button").toHaveText( await IbStrings.ADD_EMPLOYEE.name, );
    await expect( this.searchEmployeesInput, "Search employees input", ).toBeVisible();
  }
  /** Validate Resend activation email modal is displayed. */
  async validateResendActivationModalIsDisplayed(
    isDisplayed = true,
  ): Promise<void> {
    console.log("Validate Resend activation email modal is displayed");
    if (isDisplayed)
      await expect( this.resendActivationModal, "Resend activation email modal", ).toBeVisible();
    else
      await expect( this.resendActivationModal, "Resend activation email modal", ).not.toBeVisible();
  }
  /** Validate Load more button. */
  async validateLoadMoreButton(isDisplayed = true): Promise<void> {
    console.log("Validate Load more button");
    if (isDisplayed)
      await expect( this.ibEmployeeTableLoadMoreButton, "Load more button", ).toBeVisible();
    else
      await expect( this.ibEmployeeTableLoadMoreButton, "Load more button", ).not.toBeVisible();
  }
  /** Validate Resend activation email modal. */
  async validateResendActivationEmailModal(): Promise<void> {
    console.log("Validate Resend activation email modal");
    await expect( this.resendActivationCloseButton, "Close button", ).toBeVisible();
    await expect( this.resendActivationModalTitle, "Resend activation title", ).toHaveText(await IbStrings.RESEND_ACTIVATION_CODE.name);
    await expect( this.resendActivationEmployeeProfileTitle, "Employee profile title", ).toHaveText(await IbStrings.EMPLOYEE_PROFILE.name);
    await expect(this.resendActivationCancelButton, "Cancel button").toHaveText( await Strings.CANCEL_RESEND.name, );
    await expect(this.resendActivationResendButton, "Resend button").toHaveText( await IbStrings.RESEND_ACTIVATION_CODE.name, );
  }
  /** Validate no results found label. */
  async validateEmployeesTableNoResults(searchedTerm: string): Promise<void> {
    console.log("Validate employees table no results found label");
    await expect( this.employeeTableNoResultLabel, "No results found label", ).toContainText( (await IbStrings.NO_EMPLOYEE_FOUND.name) .replace("searchTerm", searchedTerm) .replace(/%/g, "'"), );
    await expect( this.employeeTableNoResultLabel.locator("a"), "Add employee link", ).toBeVisible();
  }
  /** Validate search employee input field with placeholder and search icon. */
  async validateEmployeeSearchInputField(searchedTerm = ""): Promise<void> {
    console.log("Validate search employee input field");
    await expect( this.searchEmployeesInput, "Search employees input value", ).toHaveValue(searchedTerm);
    await expect( this.searchEmployeesInput, "Search employees placeholder", ).toHaveAttribute( "placeholder", await IbStrings.SEARCH_BY_NAME_OR_EMAIL_IB.name, );
    await expect( this.searchEmployeesClearInputButton, "Search icon", ).toHaveAttribute( "src", new RegExp( searchedTerm.length > 0 ? "dismiss-dark.svg" : "search-icon.svg", ), );
  }
  /** Verify number of rows after email search. */
  async validateNumberOfRowsAfterEmailSearch(
    searchedEmail: string,
    shouldBeValid = true,
  ): Promise<void> {
    console.log(`Verify number of rows after email search: ${searchedEmail}`);
    await this.searchEmployeesInput.fill(searchedEmail);
    if (shouldBeValid)
      await expect( this.employeesTableRows, "Employee table result count", ).toHaveCount(1);
    else
      await expect( this.employeeTableNoResultLabel, "No result label", ).toBeVisible();
  }
  /** Validate employee was added by searching and verifying presence in table. */
  async validateEmployeeWasAdded(employeeEmail: string): Promise<void> {
    console.log(
      `Validate employee was added - searching for: ${employeeEmail}`,
    );
    await this.validateNumberOfRowsAfterEmailSearch(employeeEmail);
    await expect( this.companyEmailsLabelsList, `Newly added employee ${employeeEmail}`, ).toContainText(employeeEmail);
  }
}
