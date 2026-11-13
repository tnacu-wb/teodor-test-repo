import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../../test-data/pib/ibStrings";
/** InnBusiness application > Pay App > Payment details section */
export class PaymentDetailsSectionComponent {
  private readonly page: Page = global.page;
  // ######## properties ########
  // ######## UI elements/properties ########
  readonly paymentDetailsTitleLabel: Locator = this.page.getByTestId("wizard-title");
  readonly directDebitButton: Locator = this.page.getByTestId("radio-direct-debit");
  readonly directDebitByPostButton: Locator = this.page.getByTestId( "radio-direct-debit-post", );
  readonly setUpDirectDebitErrorLabel: Locator = this.page.getByTestId( "payment-method-error-tooltip", );
  readonly wishSetupDirectDebitCheckbox: Locator = this.page.getByTestId( "checkbox-wish-setup-direct-debit", );
  readonly onlyAuthorizedPersonCheckbox: Locator = this.page.getByTestId( "checkbox-only-person-authorized", );
  readonly accountHolderAndPayerCheckbox: Locator = this.page.getByTestId( "checkbox-account-holder-payer", );
  readonly onlyAuthorizedErrorTooltipLabel: Locator = this.page .getByTestId("checkbox-error-tooltip") .first();
  readonly accountHolderAndPayerErrorTooltipLabel: Locator = this.page .getByTestId("checkbox-error-tooltip") .nth(1);
  readonly mandateSection: Locator = this.page.getByTestId("mandate-section");
  readonly mandateAlertLabel: Locator = this.page.getByTestId("mandate-alert");
  readonly downloadMandateButton: Locator = this.page.getByTestId( "download-mandate-button", );
  // ######## UI actions/navigation ########
  /** Click Direct debit. */
  async clickDirectDebitButton(): Promise<void> {
    console.log("Click Direct debit button");
    await this.directDebitButton.click();
  }
  /** Click Direct debit by post. */
  async clickDirectDebitByPostButton(): Promise<void> {
    console.log("Click Direct debit by post button");
    await this.directDebitByPostButton.click();
  }
  /** Click setup Direct Debit checkbox. */
  async clickSetUpDirectDebitCheckbox(): Promise<void> {
    console.log("Click setup Direct Debit checkbox");
    await this.wishSetupDirectDebitCheckbox.click();
  }
  /** Click only authorized person checkbox. */
  async clickOnlyAuthorizedPersonCheckbox(): Promise<void> {
    console.log("Click only authorized person checkbox");
    await this.onlyAuthorizedPersonCheckbox.click();
  }
  /** Click account holder checkbox. */
  async clickAccountHolderAndPayerCheckbox(): Promise<void> {
    console.log("Click account holder and payer checkbox");
    await this.accountHolderAndPayerCheckbox.click();
  }
  /** Click Download mandate. */
  async clickDownloadMandateButton(): Promise<void> {
    console.log("Click Download mandate button");
    await this.downloadMandateButton.click();
  }
  // ######## UI validations ########
  /** Validate authorized person error. */
  async validateOnlyAuthorizedPersonErrorTooltip(
    isDisplayed = false,
  ): Promise<void> {
    console.log("Validate authorized person error");
    if (isDisplayed)
      await expect( this.onlyAuthorizedErrorTooltipLabel, "Authorized person error", ).toBeVisible();
    else
      await expect( this.onlyAuthorizedErrorTooltipLabel, "Authorized person error", ).not.toBeVisible();
  }
  /** Validate mandate download. */
  async validateMandateDownloadLabel(isDisplayed = false): Promise<void> {
    console.log("Validate mandate download label");
    if (isDisplayed)
      await expect(this.mandateAlertLabel, "Mandate download label").toHaveText( await IbStrings.MANDATE_DOWNLOAD.name, );
    else
      await expect( this.mandateAlertLabel, "Mandate download label", ).not.toBeVisible();
  }
  /** Validate account holder error. */
  async validateAccountHolderAndPayerErrorTooltip(
    isDisplayed = false,
  ): Promise<void> {
    console.log("Validate account holder error");
    if (isDisplayed)
      await expect( this.accountHolderAndPayerErrorTooltipLabel, "Account holder error", ).toBeVisible();
    else
      await expect( this.accountHolderAndPayerErrorTooltipLabel, "Account holder error", ).not.toBeVisible();
  }
  /** Validate direct debit selection error. */
  async validateSetUpDirectDebitErrorLabel({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log("Validate direct debit selection error");
    if (isDisplayed)
      await expect( this.setUpDirectDebitErrorLabel, "Direct debit selection error", ).toBeVisible();
    else
      await expect( this.setUpDirectDebitErrorLabel, "Direct debit selection error", ).not.toBeVisible();
  }
  /** Validate direct debit by post. */
  async validateDirectDebitByPostOption(): Promise<void> {
    console.log("Validate direct debit by post");
    await expect(this.mandateSection, "Mandate section").toBeVisible();
  }
  /** Validate direct debit option. */
  async validateDirectDebitOption(): Promise<void> {
    console.log("Validate direct debit option");
    await expect( this.onlyAuthorizedPersonCheckbox, "Authorized person checkbox", ).toBeVisible();
  }
  /** Validate payment details section. */
  async validatePaymentDetailsSection({
    isDDOnlineSelected = false,
    isDDByPostSelected = false,
  }: {
    isDDOnlineSelected?: boolean;
    isDDByPostSelected?: boolean;
  } = {}): Promise<void> {
    console.log("Validate payment details section");
    await expect( this.paymentDetailsTitleLabel, "Payment details title", ).toHaveText(await IbStrings.PAYMENT_DETAILS_PAYAPP.name);
    if (isDDOnlineSelected) await this.validateDirectDebitOption();
    if (isDDByPostSelected) await this.validateDirectDebitByPostOption();
  }
}
