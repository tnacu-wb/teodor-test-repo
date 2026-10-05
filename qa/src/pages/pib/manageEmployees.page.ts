import { expect, type Locator } from "@playwright/test";
import {
  InnBusinessSectionComponent,
  ManageEmployeesInnBusinessPaySectionComponent,
  ToastNotificationSectionComponent,
} from "../../components/pib";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { BasePibPage } from "./basePib.page";

interface PendingEmployeeRequest {
  emailAddress: string;
  userRole?: string | Promise<string>;
}

interface PendingEmployeeRequestLabelOptions {
  noOfPendingRequests?: number;
}

/**
 * Page object for the Manage Employees page
 */
export class ManageEmployeesPage extends BasePibPage {
  readonly url = "manage/employees";

  // ######## UI elements/properties ########
  readonly manageEmployeesContainer: Locator = this.page.getByTestId( "ManageEmployeesPage-container", );
  readonly manageEmployeesContainerTitleLabel: Locator = this.manageEmployeesContainer.locator("h1");
  readonly manageEmployeesInnBusinessTab: Locator = this.page.locator( 'a[href="?tab=innbusiness"] > span', );
  readonly manageEmployeesInnBusinessPayTab: Locator = this.page.locator( 'a[href="?tab=innbusiness-pay"] > span', );
  readonly manageEmployeesAccountSection: Locator = this.page.getByTestId( "AccountHolder-Account-1", );
  readonly manageEmployeesAccountBadgeLabel: Locator = this.page.getByTestId( "AccountHolder-Account-1-badge", );
  readonly managePendingEmployeesDropdownButton: Locator = this.page.getByTestId("AccountHolder-Account-1-badge");
  readonly manageEmployeesTitleLabel: Locator = this.page.getByTestId( "ManagePendingEmployees-title", );
  readonly managePendingEmployeesRejectButton: Locator = this.page.locator( 'button[data-testid*="-reject"]', );

  /** Get manage pending employees dropdown button based on employee email address. */
  getManagePendingEmployeesDropdownButton({
    emailAddress,
  }: PendingEmployeeRequest): Locator {
    return this.page.locator(
      `//span[contains(text(),'${emailAddress}')]/parent::li//button[contains(@data-testid,'IB-Form-Select-Button')]`,
    );
  }

  /** Get manage pending employees dropdown option button based on the text. */
  getManagePendingEmployeesDropdownOptionButton(text: string): Locator {
    return this.page.locator(`//span[text()='${text}']/parent::button`);
  }

  /** Get manage pending employees accept request button based on employee email address. */
  getManagePendingEmployeesAcceptRequestButton({
    emailAddress,
  }: PendingEmployeeRequest): Locator {
    return this.page.locator(
      `//span[contains(text(),'${emailAddress}')]/parent::li//button[contains(@data-testid,'approve')]`,
    );
  }

  /** Get manage pending employees reject request button based on employee email address. */
  getManagePendingEmployeesRejectRequestButton({
    emailAddress,
  }: PendingEmployeeRequest): Locator {
    return this.page.locator(
      `//span[contains(text(),'${emailAddress}')]/parent::li//button[contains(@data-testid,'reject')]`,
    );
  }

  // UI components
  readonly innBusinessTab = new InnBusinessSectionComponent();
  readonly innBusinessPayTab =
    new ManageEmployeesInnBusinessPaySectionComponent();
  readonly toastNotificationSection = new ToastNotificationSectionComponent();

  // ######## UI actions/navigation ########
  /** Open IB Employee Management page. */
  async open(): Promise<void> {
    console.log("Open IB Employee Management page");
    await this.openPath(this.url);
    await this.validatePage();
  }

  /** Click InnBusiness tab. */
  async clickInnBusinessTab(): Promise<void> {
    console.log("Click InnBusiness tab");
    await this.manageEmployeesInnBusinessTab.click();
  }

  /** Click InnBusiness Pay tab. */
  async clickInnBusinessPayTab(): Promise<void> {
    console.log("Click InnBusiness Pay tab");
    await this.manageEmployeesInnBusinessPayTab.click();
    await expect( this.manageEmployeesInnBusinessPayTab, "InnBusiness Pay tab should be selected", ).toHaveAttribute("data-state", "active");
  }

  /** Accept pending employee request. */
  async acceptPendingEmployeeRequest({
    emailAddress,
    userRole,
  }: PendingEmployeeRequest): Promise<void> {
    console.log(
      `Accept pending employee request for ${emailAddress} with role ${await userRole}`,
    );
    if (!userRole)
      throw new Error(
        "A user role is required to accept a pending employee request",
      );
    await this.getManagePendingEmployeesDropdownButton({
      emailAddress,
    }).click();
    await this.getManagePendingEmployeesDropdownOptionButton(
      await userRole,
    ).click();
    await this.getManagePendingEmployeesAcceptRequestButton({
      emailAddress,
    }).click();
  }

  /** Reject pending employee request. */
  async rejectPendingEmployeeRequest({
    emailAddress,
  }: PendingEmployeeRequest): Promise<void> {
    console.log(`Reject pending employee request for ${emailAddress}`);
    await this.getManagePendingEmployeesRejectRequestButton({
      emailAddress,
    }).click();
  }

  /** Reject all pending employee requests. */
  async rejectAllPendingEmployeeRequests(): Promise<void> {
    console.log("Reject all pending employee requests");
    if (!(await this.manageEmployeesTitleLabel.isVisible())) return;
    let noOfPendingRequests = await this.getNoOfPendingEmployeeRequests();
    for (let index = 0; index <= noOfPendingRequests; index += 1) {
      console.log(
        `Rejecting pending employee request ${index + 1} of ${noOfPendingRequests}`,
      );
      await this.managePendingEmployeesRejectButton
        .nth(index)
        .scrollIntoViewIfNeeded();
      await this.managePendingEmployeesRejectButton.nth(index).click();
      noOfPendingRequests = await this.getNoOfPendingEmployeeRequests();
    }
  }

  /** Get the number of pending employee requests. */
  async getNoOfPendingEmployeeRequests(): Promise<number> {
    console.log("Get number of pending employee requests");
    const pendingRequestsText =
      await this.manageEmployeesTitleLabel.textContent();
    const match = pendingRequestsText?.match(/\((\d+)\)/);
    if (!match) throw new Error("Pending employee request count was not found");
    return Number.parseInt(match[1], 10);
  }

  // ######## UI validations ########
  /** Validate InnBusiness tab from manage employees. */
  async validateEmployeeInnBusinessTabUrl(): Promise<void> {
    console.log("Validate InnBusiness tab URL");
    this.validateUrl(`${this.url}?tab=innbusiness`);
    await expect( this.manageEmployeesInnBusinessTab, "InnBusiness tab is not selected", ).toHaveAttribute("data-state", "active");
  }

  /** Validate InnBusiness Pay tab from manage employees. */
  async validateEmployeeInnBusinessPayTabUrl(): Promise<void> {
    console.log("Validate InnBusiness Pay tab URL");
    this.validateUrl(`${this.url}?tab=innbusiness-pay`);
    await expect( this.manageEmployeesInnBusinessPayTab, "InnBusiness Pay tab is not selected", ).toHaveAttribute("data-state", "active");
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

  /** Check we reached the current page by checking a specific element from the page. */
  async validatePage(): Promise<void> {
    console.log("Validate Manage Employees page");
    await this.validatePageMarker(
      this.manageEmployeesContainer,
      "Manage Employees",
    );
    await expect( this.manageEmployeesContainerTitleLabel, "Manage employees container title label", ).toHaveText(await IbStrings.MANAGE_EMPLOYEES.name);
  }

  /** Validate Accounts Section. */
  async validateAccountsSectionIsDisplayed(): Promise<void> {
    console.log("Validate Accounts section");
    await expect( this.manageEmployeesAccountSection, "Accounts Section", ).toBeVisible();
  }

  /** Validate Accounts badge. */
  async validateAccountsBadgeIsDisplayed(): Promise<void> {
    console.log("Validate Accounts badge");
    await expect( this.manageEmployeesAccountBadgeLabel, "Accounts badge", ).toHaveText(await IbStrings.ACCOUNT_HOLDER.name);
  }

  /** Validate InnBusiness Pay tab display state. */
  async validateInnBusinessPayTabIsDisplayed(
    isDisplayed = true,
  ): Promise<void> {
    console.log("Validate InnBusiness Pay tab display state");
    if (isDisplayed)
      await expect( this.manageEmployeesInnBusinessPayTab, "InnBusiness Pay tab", ).toBeVisible();
    else
      await expect( this.manageEmployeesInnBusinessPayTab, "InnBusiness Pay tab", ).not.toBeVisible();
  }

  /** Validate Pending employee requests label. */
  async validatePendingEmployeeRequestsLabel({
    noOfPendingRequests,
  }: PendingEmployeeRequestLabelOptions = {}): Promise<void> {
    console.log("Validate Pending employee requests label");
    await expect( this.manageEmployeesTitleLabel, "Pending employee requests label", ).toHaveText( `${await IbStrings.PENDING_REQUESTS.name} (${noOfPendingRequests})`, );
  }
}
