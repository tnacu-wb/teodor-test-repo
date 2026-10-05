import { type Locator } from "@playwright/test";
import { Constants } from "../../../test-data/constants";
import { WorldlinePage } from "./worldline.page";

/** Register page for Worldline. */
export class RegisterPage extends WorldlinePage {
  readonly url = Constants.WORDLINE_REGISTER_PAGE;

  // ######## UI elements/properties ########
  readonly registrationCodeInput: Locator = this.page.locator( "#ctl00_ContentPlaceHolder1_txtRegistrationCode", );
  readonly continueButton: Locator = this.page.locator( "#ctl00_ContentPlaceHolder1_btnRegCodeContinue", );
  readonly mobileNumberInput: Locator = this.page.locator( "#ctl00_ContentPlaceHolder1_regInfo1_txtMobileNumber", );
  readonly aboutYouDropdownButton: Locator = this.page.locator( "#ctl00_ContentPlaceHolder1_regInfo1_ddlHotelBookingRole", );
  readonly submitRegistrationButton: Locator = this.page.locator( "#ctl00_ContentPlaceHolder1_btnLoginInfoContinue", );
  readonly registrationCompleteLabel: Locator = this.page.locator( "#ctl00_ContentPlaceHolder1_Label9", );

  // ######## UI actions/navigation ########
  /** Open the Worldline registration page. */
  async open(): Promise<void> {
    console.log("Open Worldline registration page");
    await this.openExternal(this.url);
  }
  /** Set the registration code input. */
  async setRegistrationCodeInput(registrationCode: string): Promise<void> {
    console.log(`Set registration code: ${registrationCode}`);
    await this.registrationCodeInput.fill(registrationCode);
  }
  /** Click the Continue button. */
  async clickContinueButton(): Promise<void> {
    console.log("Click Continue button");
    await this.continueButton.scrollIntoViewIfNeeded();
    await this.continueButton.click();
  }
  /** Set the mobile number input. */
  async setMobileNumberInput(mobileNumber: string): Promise<void> {
    console.log(`Set mobile number: ${mobileNumber}`);
    await this.mobileNumberInput.fill(mobileNumber);
  }
  /** Select an About you dropdown option. */
  async selectAboutYouDropdownOption(option: string): Promise<void> {
    console.log(`Select "About you" dropdown option: ${option}`);
    await this.aboutYouDropdownButton.selectOption({ label: option });
  }
  /** Submit the registration form and wait for completion. */
  async clickSubmitRegistrationButton(): Promise<void> {
    console.log("Click Submit Registration button");
    await this.submitRegistrationButton.scrollIntoViewIfNeeded();
    await this.submitRegistrationButton.click();
    await this.registrationCompleteLabel.waitFor({
      state: "visible",
      timeout: 20000,
    });
  }
}
