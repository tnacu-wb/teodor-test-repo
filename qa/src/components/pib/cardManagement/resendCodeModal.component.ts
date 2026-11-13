import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../../test-data/pib/ibStrings";
/** InnBusiness application > Manage Cards > InnBusiness Pay section > Edit Card > Resend code modal */
export class ResendCodeModalComponent {
  private readonly page: Page = global.page;
  // ######## properties ########
  // ######## UI elements/properties ########
  readonly resendCodeModal: Locator = this.page.getByTestId("Resend-Code-Dialog");
  readonly resendCodeTitleLabel: Locator = this.resendCodeModal.locator("div h2");
  readonly resendCodeDescriptionLabel: Locator = this.resendCodeModal.locator("div span");
  readonly cardHolderLabel: Locator = this.page.locator( "div.form-details-box.mt-12 span.font-bold", );
  readonly cardHolderNameLabel: Locator = this.page .locator("div.form-details-box.mt-12 div.flex-col span") .first();
  readonly cardHolderEmailLabel: Locator = this.page .locator("div.form-details-box.mt-12 div.flex-col span") .nth(1);
  readonly sendEmailCopyCheckboxButton: Locator = this.page.locator( '//button[@id="Send-Email-Copy"]', );
  readonly sendEmailCopyCheckboxLabel: Locator = this.page.locator( 'label[data-testid="Send-Email-Copy-Label"] span', );
  readonly closeButton: Locator = this.page.getByTestId( "Dialog-X-Close-Button", );
  readonly cancelButton: Locator = this.page.getByTestId( "ResendCodeDialog-Cancel-Button", );
  readonly resendRegistrationCodeButton: Locator = this.page.getByTestId( "ResendCodeDialog-Resend-Code-Button", );
  // ######## UI actions/navigation ########
  /** Click on close button. */
  async clickCloseButton(): Promise<void> {
    console.log("Click on close button");
    await this.closeButton.click();
  }
  /** Click on Cancel button. */
  async clickCancelButton(): Promise<void> {
    console.log("Click on Cancel button");
    await this.cancelButton.click();
  }
  /** Click on Send email copy checkbox. */
  async clickSendEmailCopyCheckbox(): Promise<void> {
    console.log("Click on Send email copy checkbox");
    await this.sendEmailCopyCheckboxButton.click();
  }
  /** Click on Resend Registration Code button. */
  async clickResendRegistrationCodeButton(): Promise<void> {
    console.log("Click on Resend Registration Code button");
    await this.resendRegistrationCodeButton.click();
  }
  // ######## UI validations ########
  /** Validate resend code modal. */
  async validateResendCodeModal(
    cardHolderName: string,
    cardHolderEmail: string,
  ): Promise<void> {
    console.log("Validate Resend Code modal");
    await expect( this.resendCodeTitleLabel, "Resend code title label", ).toHaveText(await IbStrings.RESEND_CODE_TITLE.name);
    await expect( this.resendCodeDescriptionLabel, "Resend code description label", ).toHaveText(await IbStrings.RESEND_CODE_DESCRIPTION.name);
    await expect( this.cardHolderLabel, "Resend code card holder label", ).toHaveText(await IbStrings.RESEND_CODE_CARD_HOLDER.name);
    await expect( this.cardHolderNameLabel, "Resend code card holder name label", ).toHaveText(cardHolderName);
    await expect( this.cardHolderEmailLabel, "Resend code card holder email label", ).toHaveText(cardHolderEmail);
    await this.validateSendEmailCopyCheckboxIsSelected({ isSelected: false });
    await expect( this.resendRegistrationCodeButton, "Resend registration code button", ).toHaveText(await IbStrings.RESEND_CODE_BUTTON.name);
    await expect(this.cancelButton, "Cancel button").toHaveText( await IbStrings.CANCEL.name, );
    await expect(this.closeButton, "Close button").toBeVisible();
  }
  /** Validate Send email copy checkbox is selected or not. */
  async validateSendEmailCopyCheckboxIsSelected({
    isSelected = true,
  }: { isSelected?: boolean } = {}): Promise<void> {
    console.log(
      `Validate that Send email copy checkbox is ${isSelected ? "" : "not "} selected`,
    );
    await expect( this.sendEmailCopyCheckboxButton, "Resend code copy email checkbox", ).toHaveAttribute("data-state", isSelected ? "checked" : "unchecked");
    await expect( this.sendEmailCopyCheckboxLabel, "Resend code copy email checkbox label", ).toHaveText(await IbStrings.RESEND_CODE_COPY_EMAIL.name);
  }
  /** Validate that the Resend Code modal is displayed or not. */
  async validateResendCodeModalIsDisplayed({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log(
      `Validate that Resend Code modal is ${isDisplayed ? "" : "not "} displayed`,
    );
    if (isDisplayed)
      await expect(this.resendCodeModal, "Resend Code modal").toBeVisible();
    else
      await expect(this.resendCodeModal, "Resend Code modal").not.toBeVisible();
  }
}
