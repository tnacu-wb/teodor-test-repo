import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../../test-data/pib/ibStrings";
/** InnBusiness application > Home > Apply now > Start application > Your details section */
export class YourDetailsSectionComponent {
  private readonly page: Page = global.page;
  // ######## properties ########
  // ######## UI elements/properties ########
  readonly yourDetailsTitleLabel: Locator = this.page.getByTestId("wizard-title");
  readonly yourDetailsDescriptionLabel: Locator = this.page .locator("form#YourDetails-Form > div") .first();
  readonly landLineInput: Locator = this.page.getByTestId( "landLineNumber-Form-Input", );
  readonly mobileNumberInput: Locator = this.page.getByTestId( "mobileNumber-Form-Input", );
  readonly positionInput: Locator = this.page.getByTestId( "position-Form-Input", );
  readonly landLineErrorLabel: Locator = this.page.getByTestId( "landLineNumber-Error-Tooltip", );
  readonly mobileNumberErrorLabel: Locator = this.page.getByTestId( "mobileNumber-Error-Tooltip", );
  readonly positionErrorLabel: Locator = this.page.getByTestId( "position-Error-Tooltip", );
  readonly titleDropdown: Locator = this.page.getByTestId( "title-IB-Form-Select-Button", );
  readonly titleDropdownLabelsList: Locator = this.page.locator( 'button[data-testid*="-Option"]', );
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
  /** Set landline. */
  async setLandLineInput({
    value,
    pressTab = false,
  }: {
    value: string;
    pressTab?: boolean;
  }): Promise<void> {
    console.log("Set landline");
    await this.fill(this.landLineInput, value, pressTab);
  }
  /** Set mobile number. */
  async setMobileNumberInput({
    value,
    pressTab = false,
  }: {
    value: string;
    pressTab?: boolean;
  }): Promise<void> {
    console.log("Set mobile number");
    await this.fill(this.mobileNumberInput, value, pressTab);
  }
  /** Set position. */
  async setPositionInput({
    value,
    pressTab = false,
  }: {
    value: string;
    pressTab?: boolean;
  }): Promise<void> {
    console.log("Set position");
    await this.fill(this.positionInput, value, pressTab);
  }
  /** Blur position input. */
  async clickOutsidePositionInput(): Promise<void> {
    console.log("Blur position input");
    await this.positionInput.press("Tab");
  }
  /** Blur landline input. */
  async clickOutsideLandLineInput(): Promise<void> {
    console.log("Blur landline input");
    await this.landLineInput.press("Tab");
  }
  /** Blur mobile input. */
  async clickOutsideMobileNumberInput(): Promise<void> {
    console.log("Blur mobile input");
    await this.mobileNumberInput.press("Tab");
  }
  /** Select first title. */
  async clickFirstTitleFromDropDown(): Promise<void> {
    console.log("Select first title");
    await this.titleDropdown.click();
    await this.titleDropdownLabelsList.first().click();
  }
  // ######## UI validations ########
  /** Validate Your details page title. */
  async validateYourDetailsPageTitle(): Promise<void> {
    console.log("Validate Your details page title");
    await expect(this.yourDetailsTitleLabel, "Your details title").toHaveText( await IbStrings.YOUR_DETAILS_PAY_APP_TITLE.name, );
  }
  /** Validate position label. */
  async validatePositionWithinTheCompanyLabel(): Promise<void> {
    console.log("Validate position label");
    await expect(this.positionInput, "Position input").toBeVisible();
  }
  /** Validate contact number labels. */
  async validateContactNumberLabels(): Promise<void> {
    console.log("Validate contact number labels");
    await expect(this.landLineInput, "Landline input").toBeVisible();
    await expect(this.mobileNumberInput, "Mobile number input").toBeVisible();
  }
  /** Validate position input. */
  async validatePositionInput(position: string, isValid = true): Promise<void> {
    console.log("Validate position input");
    await expect(this.positionInput, "Position input").toHaveValue(position);
    if (!isValid)
      await expect(this.positionErrorLabel, "Position error").toBeVisible();
  }
  /** Validate landline input. */
  async validateLandLineInput({
    landLine,
    isValid = true,
  }: {
    landLine: string;
    isValid?: boolean;
  }): Promise<void> {
    console.log("Validate landline input");
    await expect(this.landLineInput, "Landline input").toHaveValue(landLine);
    if (!isValid)
      await expect(this.landLineErrorLabel, "Landline error").toBeVisible();
  }
  /** Validate mobile number input. */
  async validateMobileNumberInput({
    mobileNumber,
    isValid = true,
  }: {
    mobileNumber: string;
    isValid?: boolean;
  }): Promise<void> {
    console.log("Validate mobile number input");
    await expect(this.mobileNumberInput, "Mobile number input").toHaveValue( mobileNumber, );
    if (!isValid)
      await expect(this.mobileNumberErrorLabel, "Mobile error").toBeVisible();
  }
  /** Validate Your details language state. */
  async validateYourDetailsSectionAfterLanguageChange(): Promise<void> {
    console.log("Validate Your details after language change");
    await expect( this.yourDetailsTitleLabel, "Your details title", ).toBeVisible();
    await expect( this.yourDetailsDescriptionLabel, "Your details description", ).toBeVisible();
  }
}
