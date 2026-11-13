import { expect, type Locator } from "@playwright/test";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { BasePibPage } from "./basePib.page";

/**
 * InnBusiness application > Manage > Employees > Add Employee > Add new employees via bulk upload page
 */
export class AddNewEmployeesViaBulkUploadPage extends BasePibPage {
  readonly url = "manage/employees/add-bulk";

  // ######## UI elements/properties ########
  readonly backArrowLink: Locator = this.page.locator( 'a img[data-testid="Add-Employees-Bulk-back-icon"]', );
  readonly addNewEmployeesViaBulkUploadLabel: Locator = this.page.getByTestId( "Add-Employees-Bulk-title", );
  readonly instructionsLabel: Locator = this.page.getByTestId("Bulk-Add-Container");
  readonly uploadFileButton: Locator = this.page.getByTestId("Bulk-Upload-File");

  // UI components

  // ######## UI actions/navigation ########
  /** Open IB Add new employees via bulk upload page. */
  async open(): Promise<void> {
    console.log("Open IB Add new employees via bulk upload page");
    await this.openPath(this.url);
    await this.validatePage();
  }

  /** Click back arrow link. */
  async clickBackArrowLink(): Promise<void> {
    console.log("Click back arrow link");
    await this.backArrowLink.click();
  }

  // ######## UI validations ########
  /** Validate Add new employees via bulk upload page elements. */
  async validateAddNewEmployeesViaBulkUploadPageElements(): Promise<void> {
    console.log("Validate Add new employees via bulk upload page elements");
    await expect( this.addNewEmployeesViaBulkUploadLabel, "Add new employees via bulk upload title", ).toHaveText(await IbStrings.ADD_NEW_EMPLOYEES_TITLE.name);
    await expect(this.instructionsLabel, "Instructions label").toBeVisible();
    const instructionsText = (
      (await this.instructionsLabel.textContent()) ?? ""
    )
      .replace("1.", "")
      .replace("2.", "")
      .replace("3.", "")
      .replace(/\n/g, "")
      .replace(/\./g, "")
      .replace("an])dere", "");
    const expectedText =
      `${await IbStrings.ADD_NEW_EMPLOYEES_INSTRUCTIONS.name}${await IbStrings.ADD_NEW_EMPLOYEES_BUTTON.name}`
        .replace(/\./g, "")
        .replace("andere", "");
    await expect(instructionsText, "Instructions text is not correct").toBe( expectedText, );
    await expect(this.uploadFileButton, "Upload File button").toHaveText( await IbStrings.ADD_NEW_EMPLOYEES_BUTTON.name, );
  }

  /** Check we reached the current page by checking a specific element from the page. */
  async validatePage(): Promise<void> {
    console.log("Validate Add new employees via bulk upload page");
    await this.validatePageMarker(
      this.addNewEmployeesViaBulkUploadLabel,
      "Add new employees via bulk upload",
    );
  }
}
