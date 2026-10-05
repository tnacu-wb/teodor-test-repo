import { expect, type Locator, type Page } from "@playwright/test";
import { type DeliveryOption } from "../../../test-data/pib/deliveryOptions";
import { IbStrings } from "../../../test-data/pib/ibStrings";
import { Strings } from "../../../test-data/strings";
import { CardDeliveryOptionsSectionComponent } from "./cardDeliveryOptionsSection.component";

interface EmployeeDetails {
  title?: string;
  firstName?: string;
  lastName?: string;
}
/** InnBusiness application > Manage Cards > InnBusiness Pay section > Edit Card > Replace card modal */
export class ReplaceCardModalComponent {
  private readonly page: Page = global.page;
  // ######## properties ########
  // ######## UI elements/properties ########
  readonly replaceCardModal: Locator = this.page.getByTestId( "Replace-Card-Dialog", );
  readonly replaceCardTitleLabel: Locator = this.replaceCardModal.locator("div h2");
  readonly whatHappenedToCardLabel: Locator = this.replaceCardModal.locator("div > span");
  readonly selectionDescriptionLabel: Locator = this.replaceCardModal.locator( "div div:nth-child(3)", );
  readonly closeButton: Locator = this.page.getByTestId( "Dialog-X-Close-Button", );
  readonly cardLostOrStolenRadioButton: Locator = this.page.locator( `//button[@id="${IbStrings.REPLACE_CARD_LOST.data.default}"]`, );
  readonly cardDamagedOrNotWorkingRadioButton: Locator = this.page.locator( `//button[@id="${IbStrings.REPLACE_CARD_DAMAGED.data.default}"]`, );
  readonly cancelButton: Locator = this.page.getByTestId( "Replace-Card-Cancel-Button", );
  readonly continueButton: Locator = this.page.getByTestId( "Replace-Card-Continue-Button", );
  readonly replaceCardDeliveryAddressLabel: Locator = this.page.locator( 'span[data-testid="Delivery-Title"]', );
  readonly alternativeAddressNotificationLabel: Locator = this.page.locator( "div.bg-notificationAlertBg.border-notificationAlertBorder.mt-12 > div", );
  readonly replaceCardSuccessModal: Locator = this.page.getByTestId( "Replace-Card-Success-Dialog", );
  readonly replaceCardSuccessTitleLabel: Locator = this.replaceCardSuccessModal.locator("div h2");
  readonly replaceCardSuccessDescriptionLabel: Locator = this.replaceCardSuccessModal.locator("span");
  readonly backToCardDetailsButton: Locator = this.page.getByTestId( "Replace-Success-Back-Button", );
  readonly personalDetailsTitleInput: Locator = this.page.getByTestId( "Title-IB-Form-Select-Button", );
  readonly personalDetailsFirstNameInput: Locator = this.page.getByTestId( "First-Name-Form-Input", );
  readonly personalDetailsLastNameInput: Locator = this.page.getByTestId( "Last-Name-Form-Input", );
  readonly companyPostcodeInput: Locator = this.page.locator( 'input[data-testid*="Postcode"]', );
  readonly cardDeliveryOptionsSection: CardDeliveryOptionsSectionComponent =
    new CardDeliveryOptionsSectionComponent();
  // ######## UI actions/navigation ########
  /** Click on close button from replace card modal. */
  async clickCloseButton(): Promise<void> {
    console.log("Click on close button from Replace Card modal");
    await this.closeButton.click();
  }
  /** Click on card lost radio button. */
  async clickCardLostOrStolenButton(): Promise<void> {
    console.log("Click on card lost or stolen radio button");
    await this.cardLostOrStolenRadioButton.click();
  }
  /** Click on card damaged radio button. */
  async clickCardDamagedOrNotWorkingButton(): Promise<void> {
    console.log("Click on card damaged or not working radio button");
    await this.cardDamagedOrNotWorkingRadioButton.click();
  }
  /** Click on Cancel button. */
  async clickCancelButton(): Promise<void> {
    console.log("Click on Cancel button from Replace Card modal");
    await this.cancelButton.click();
  }
  /** Click on Continue button. */
  async clickContinueButton(): Promise<void> {
    console.log("Click on Continue button from Replace Card modal");
    await this.continueButton.click();
  }
  // ######## UI validations ########
  /** Validate replace card modal. */
  async validateReplaceCardModal({
    lostOrStolen = false,
    damagedOrNotWorking = false,
  }: {
    lostOrStolen?: boolean;
    damagedOrNotWorking?: boolean;
  } = {}): Promise<void> {
    console.log("Validate Replace Card modal");
    await expect( this.replaceCardTitleLabel, "Replace card title label", ).toHaveText(await IbStrings.REPLACE_CARD.name);
    await expect(this.closeButton, "Close button").toBeVisible();
    await expect( this.whatHappenedToCardLabel, "What's happened to this card label", ).toHaveText(await IbStrings.WHAT_S_HAPPENED.name);
    await this.validateRadio(
      this.cardLostOrStolenRadioButton,
      await IbStrings.REPLACE_CARD_LOST.name,
      lostOrStolen,
      "Lost or stolen card radio button",
    );
    if (lostOrStolen)
      await expect( this.selectionDescriptionLabel, "Lost card notification label", ).toHaveText( this.formatText( `${await IbStrings.REPLACE_CARD_LOST_ACTION_CANNOT_BE_UNDONE_1.name}\n${await IbStrings.REPLACE_CARD_LOST_ACTION_CANNOT_BE_UNDONE_2.name}`, ), );
    await this.validateRadio(
      this.cardDamagedOrNotWorkingRadioButton,
      await IbStrings.REPLACE_CARD_DAMAGED.name,
      damagedOrNotWorking,
      "Damaged card radio button",
    );
    if (damagedOrNotWorking)
      await expect( this.selectionDescriptionLabel, "Damaged card notification label", ).toHaveText( this.formatText( await IbStrings.REPLACE_CARD_DAMAGED_ACTION_CANNOT_BE_UNDONE.name, ), );
    await expect(this.cancelButton, "Cancel button").toHaveText( await IbStrings.CANCEL.name, );
    await expect(this.continueButton, "Continue button").toHaveText( await Strings.CONTINUE.name, );
    if (lostOrStolen || damagedOrNotWorking)
      await expect( this.continueButton, "Continue button enabled", ).toBeEnabled();
    else
      await expect( this.continueButton, "Continue button disabled", ).toBeDisabled();
  }
  /** Validate the Replace Card modal address elements. */
  async validateReplaceCardAddressModal({
    deliveryOptions,
    whoWillUseCardOption,
  }: {
    deliveryOptions: DeliveryOption[];
    whoWillUseCardOption?: string;
  }): Promise<void> {
    console.log("Validate Replace Card address modal");
    await expect(this.replaceCardModal, "Replace card modal").toBeVisible();
    await expect( this.replaceCardTitleLabel, "Replace card title label", ).toHaveText(await IbStrings.REPLACE_CARD.name);
    await expect(this.closeButton, "Close button").toBeVisible();
    await expect( this.replaceCardDeliveryAddressLabel, "Card delivery address label", ).toHaveText(await IbStrings.REPLACE_NEW_CARD_DELIVERY_ADDRESS.name);
    await this.cardDeliveryOptionsSection.validateCardDeliveryOptions({
      deliveryOptions,
      whoWillUseCardOption,
    });
    await expect(this.cancelButton, "Cancel button").toHaveText( await IbStrings.CANCEL.name, );
    await this.validateReplaceCardButtonIsClickable({
      isClickable: deliveryOptions[0]?.buttonSelected ?? false,
    });
  }
  /** Validate cardholder alternative address section elements. */
  async validateCardholderAddressDetails({
    employeeDetails = {},
    isManagerOrBooker = true,
  }: {
    employeeDetails?: EmployeeDetails;
    isManagerOrBooker?: boolean;
  } = {}): Promise<void> {
    console.log("Validate cardholder address details");
    await expect( this.alternativeAddressNotificationLabel, "Alternative address notification label", ).toHaveText( await IbStrings.CARD_HOLDER_ALTERNATIVE_ADDRESS_NOTIFICATION.name, );
    await expect(this.personalDetailsTitleInput, "Cardholder title").toHaveText( isManagerOrBooker ? (employeeDetails.title ?? "") : "", );
    await expect( this.personalDetailsFirstNameInput, "Cardholder first name", ).toHaveValue(isManagerOrBooker ? (employeeDetails.firstName ?? "") : "");
    await expect( this.personalDetailsLastNameInput, "Cardholder last name", ).toHaveValue(isManagerOrBooker ? (employeeDetails.lastName ?? "") : "");
    await expect( this.companyPostcodeInput, "Company postcode input", ).toHaveValue("");
  }
  /** Validate the Replace Card success modal elements. */
  async validateReplaceCardSuccessModal(): Promise<void> {
    console.log("Validate Replace Card success modal");
    await expect( this.replaceCardSuccessModal, "Replace card success modal", ).toBeVisible();
    await expect(this.closeButton, "Close button").toBeVisible();
    await expect( this.replaceCardSuccessTitleLabel, "Replace card success title label", ).toHaveText(await IbStrings.REPLACE_NEW_CARD_SUBMITTED.name);
    await expect( this.replaceCardSuccessDescriptionLabel, "Replace card success description label", ).toHaveText(await IbStrings.REPLACE_NEW_CARD_APPLICATION_PROCESSED.name);
    await expect( this.backToCardDetailsButton, "Back to card details button", ).toHaveText(await IbStrings.BACK_TO_CARD_DETAILS.name);
  }
  /** Validate that the Replace Card modal is displayed or not. */
  async validateReplaceCardModalIsDisplayed({
    isDisplayed = true,
  }: {
    isDisplayed?: boolean;
  }): Promise<void> {
    console.log(
      `Validate that Replace Card modal is ${isDisplayed ? "" : "not "}displayed`,
    );
    if (isDisplayed)
      await expect(this.replaceCardModal, "Replace card modal").toBeVisible();
    else
      await expect( this.replaceCardModal, "Replace card modal", ).not.toBeVisible();
  }
  /** Validate Replace Card button is clickable. */
  async validateReplaceCardButtonIsClickable({
    isClickable = true,
  }: {
    isClickable?: boolean;
  }): Promise<void> {
    console.log(
      `Validate that Replace Card button is ${isClickable ? "" : "not "}clickable`,
    );
    await expect(this.continueButton, "Replace card button").toHaveText( await IbStrings.REPLACE_CARD.name, );
    if (isClickable)
      await expect( this.continueButton, "Replace card button enabled", ).toBeEnabled();
    else
      await expect( this.continueButton, "Replace card button disabled", ).toBeDisabled();
  }
  /** Validate Radio. */
  private async validateRadio(
    button: Locator,
    label: string,
    isChecked: boolean,
    description: string,
  ): Promise<void> {
    await expect(button, description).toHaveText(label);
    await expect(button, `${description} checked state`).toHaveAttribute( "data-state", isChecked ? "checked" : "unchecked", );
  }
  /** Format Text. */
  private formatText(value: string): string {
    return value.replace(/([.,!?;:])(?=\S)/g, "$1 ");
  }
}
