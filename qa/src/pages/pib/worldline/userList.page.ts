import { expect, type Locator } from "@playwright/test";
import { Constants } from "../../../test-data/constants";
import { WorldlinePage } from "./worldline.page";

/** Worldline User List page. */
export class UserListPage extends WorldlinePage {
  readonly url = Constants.WORLDLINE_USER_LIST_PAGE;

  // ######## UI elements/properties ########
  readonly addNewUserButton: Locator = this.page.locator( "#ctl00_ContentPlaceHolder1_btnAddUser", );
  readonly addNewUserModal: Locator = this.page.locator( "#ctl00_ContentPlaceHolder1_pnlNewUser", );
  readonly addNewUserTitleDropdownButton: Locator = this.page.locator( "#ctl00_ContentPlaceHolder1_ddlNewUser_UserTitle", );
  readonly addNewUserRoleDropdownButton: Locator = this.page.locator( "#ctl00_ContentPlaceHolder1_ddlNewUserRoles", );
  readonly addNewUserFirstNameInput: Locator = this.page.locator( "#ctl00_ContentPlaceHolder1_txtNewUser_Forename", );
  readonly addNewUserLastNameInput: Locator = this.page.locator( "#ctl00_ContentPlaceHolder1_txtNewUser_Surname", );
  readonly addNewUserEmailAddressInput: Locator = this.page.locator( "#ctl00_ContentPlaceHolder1_txtNewUser_Email", );
  readonly addNewUserConfirmEmailAddressInput: Locator = this.page.locator( "#ctl00_ContentPlaceHolder1_txtNewUser_EmailConfirm", );
  readonly addNewUserSaveButton: Locator = this.page.locator( "#ctl00_ContentPlaceHolder1_btnNewUser_Save", );
  readonly successToast: Locator = this.page.locator( "#ctl00_ContentPlaceHolder1_notificationControl_divNotification", );
  readonly closeButton: Locator = this.page.locator( "#ctl00_ContentPlaceHolder1_btnNewUser_Cancel", );

  // ######## UI actions/navigation ########
  /** Open the Worldline User List page. */
  async open(): Promise<void> {
    console.log("Open Worldline User List page");
    await this.openExternal(this.url);
  }
  /** Open the Add new user modal. */
  async clickAddNewUserButton(): Promise<void> {
    console.log("Click Add New User button");
    await this.addNewUserButton.scrollIntoViewIfNeeded();
    await this.addNewUserButton.waitFor({ state: "visible", timeout: 30000 });
    await this.addNewUserButton.click();
    await this.addNewUserModal.waitFor({ state: "visible", timeout: 30000 });
  }
  /** Close the Add new user modal. */
  async clickCloseButton(): Promise<void> {
    console.log("Click Close button");
    await this.closeButton.scrollIntoViewIfNeeded();
    await this.closeButton.click();
    await expect( this.addNewUserModal, "Add new user modal should be closed", ).not.toBeVisible({ timeout: 30000 });
  }
  /** Add a new Worldline user. */
  async addNewUser(data: {
    title: string;
    role: string;
    firstName: string;
    lastName: string;
    emailAddress: string;
    confirmEmailAddress: string;
  }): Promise<void> {
    console.log("Add new user");
    await this.addNewUserTitleDropdownButton.selectOption({
      label: data.title,
    });
    await this.addNewUserRoleDropdownButton.selectOption({ label: data.role });
    await this.addNewUserFirstNameInput.fill(data.firstName);
    await this.addNewUserLastNameInput.fill(data.lastName);
    await this.addNewUserEmailAddressInput.fill(data.emailAddress);
    await this.addNewUserConfirmEmailAddressInput.fill(
      data.confirmEmailAddress,
    );
    await this.addNewUserSaveButton.scrollIntoViewIfNeeded();
    await this.addNewUserSaveButton.click();
    await this.successToast.waitFor({ state: "visible", timeout: 20000 });
  }
}
