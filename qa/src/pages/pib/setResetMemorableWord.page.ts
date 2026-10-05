import { expect, type Locator } from "@playwright/test";
import {
  AccountHolderSectionComponent,
  MemorableWordSectionComponent,
} from "../../components/pib";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { BasePibPage } from "./basePib.page";

/**
 * InnBusiness application > Spending > Set/Reset your memorable word
 */
export class SetResetMemorableWordPage extends BasePibPage {
  // ######## properties ########
  readonly url = "spending/memorable-word";

  // ######## UI elements/properties ########
  readonly headerLabel: Locator = this.page.getByTestId( "MemorableWordPage-title", );
  readonly backIconImg: Locator = this.page.getByTestId( "MemorableWordPage-back-icon", );
  readonly descriptionLabel: Locator = this.page.getByTestId( "MemorableWordPage-description", );
  readonly submitRegistrationButton: Locator = this.page.getByTestId( "MemorableWordPage-Submit-Button", );

  // UI components
  readonly accountHolderSection = new AccountHolderSectionComponent();
  readonly memorableWordSection = new MemorableWordSectionComponent();

  // ######## UI actions/navigation ########
  /**
   * Open IB Set/Reset memorable word page
   */
  async open(): Promise<void> {
    console.log("Open IB Set/Reset memorable word page");
    await this.openPath(this.url);
    await this.validatePage();
  }

  /**
   * Click Back icon
   */
  async clickBackIcon(): Promise<void> {
    console.log("Click Back icon");
    await this.backIconImg.click();
    await expect(this.backIconImg, "Back icon after click").not.toBeVisible();
  }

  /**
   * Click Submit registration button
   */
  async clickSubmitRegistration(): Promise<void> {
    console.log("Click Submit registration button");
    await this.submitRegistrationButton.click();
    await expect( this.submitRegistrationButton, "Submit registration button after click", ).not.toBeVisible();
  }

  // ######## UI validations ########
  /**
   * Check we reached the current page by checking a specific element from the page
   */
  async validatePage(): Promise<void> {
    console.log("Validate Set/Reset memorable word page");
    await this.validatePageMarker(this.headerLabel, "Set/Reset memorable word");
  }

  /**
   * Validate Set/Reset your memorable word page elements
   */
  async validateSetResetMemorableWordElements(): Promise<void> {
    console.log("Validate Set/Reset your memorable word page elements");
    await expect(this.headerLabel, "Header label").toHaveText( await IbStrings.SET_RESET_YOUR_MEMORABLE_WORD.name, );
    await expect(this.backIconImg, "Back icon").toBeVisible();
    await expect(this.descriptionLabel, "Description label").toHaveText( await IbStrings.YOUR_MEMORABLE_WORD_WILL_BE_USED.name, );
    await this.memorableWordSection.validateMemorableWordElements();
    await expect( this.submitRegistrationButton, "Submit registration button", ).toHaveText(await IbStrings.SUBMIT_REGISTRATION.name);
  }

  /**
   * Validate toast message
   * @param expectedMessage expected message
   */
  async validateToastMessage(
    expectedMessage: string | Promise<string>,
  ): Promise<void> {
    console.log("Validate toast message");
    await this.toastNotificationSection.validateToastNotification({
      message: expectedMessage,
    });
  }
}
