import { expect, type Locator, type Page } from "@playwright/test";
import { BillingAddressSectionComponent } from "../../shared/payment/billingAddressSection.component";
import { type CardDetails, Cards } from "../../../test-data/cards";
import { Constants } from "../../../test-data/constants";
import { IbStrings } from "../../../test-data/pib/ibStrings";
import { Strings } from "../../../test-data/strings";

type Address = {
  line1?: string;
  line2?: string;
  line3?: string;
  line4?: string;
  line5?: string;
  postCode?: string;
  countryCode?: string;
};
type SavedCard = {
  cardType: string;
  cardNumber: string;
  cardHolderName: string;
  expiryDate: string;
};

/**
 * InnBusiness Payment Type Section from My Profile
 */
export class PaymentTypeSectionComponent {
  private readonly page: Page = global.page;

  // ######## properties ########

  // ######## UI elements/properties ########
  readonly paymentTypeTitleLabel: Locator = this.page.locator( 'h4[data-testid="PaymentTypeCta-Title"]', );
  readonly paymentTypeDescriptionLabel: Locator = this.page.locator( 'p[data-testid="PaymentTypeCta-Description"]', );
  readonly addPaymentCardButton: Locator = this.page.locator( 'button[data-testid="PaymentTypeCta-Add-New-Button"]', );
  readonly paymentFormContainer: Locator = this.page.locator( 'section[data-testid="payment-form-container"]', );
  readonly paymentFormTitleLabel: Locator = this.page.locator( 'h1[data-testid="payment-form-title"]', );
  readonly cardAuthorisedMessageLabel: Locator = this.page.locator( '//section[@data-testid="PaymentCard-Container"]/div[1]//span', );
  readonly billingAddressSectionTitleLabel: Locator = this.page.locator( 'div[data-testid="billing-section"] p', );
  readonly usePersonalAddressLabel: Locator = this.page.locator( 'label[data-testid="PaymentForm-PersonalAddress"] div span', );
  readonly usePersonalAddressButton: Locator = this.page.locator( 'label[data-testid="PaymentForm-PersonalAddress"]', );
  readonly useDifferentAddressLabel: Locator = this.page.locator( 'label[data-testid="PaymentForm-DifferentAddress"] div span', );
  readonly useDifferentAddressButton: Locator = this.page.locator( 'label[data-testid="PaymentForm-DifferentAddress"]', );
  readonly yourAddressTitleLabel: Locator = this.page.locator( 'h4[data-testid="different-address-title"]', );
  readonly homeAddressRadioButton: Locator = this.page.locator( '//div[@data-testid="AddressType-form"]/div/label[1]', );
  readonly businessAddressRadioButton: Locator = this.page.locator( '//div[@data-testid="AddressType-form"]/div/label[2]', );
  readonly paymentTypeSectionTitleLabel: Locator = this.page.locator( 'div[data-testid="payment-type-header"] h2', );
  readonly paymentTypeSectionDescriptionLabel: Locator = this.page.locator( 'div[data-testid="payment-type-header"] p', );
  readonly newCreditDebitCardRadioButton: Locator = this.page.locator( 'label[data-testid="PaymentForm-CreditDebit"]', );
  readonly newCreditDebitCardLabel: Locator = this.page.locator( 'label[data-testid="PaymentForm-CreditDebit"] div span', );
  readonly newInnBusinessPayCardRadioButton: Locator = this.page.locator( 'label[data-testid="PaymentForm-InnBusinessPay"]', );
  readonly newInnBusinessPayCardLabel: Locator = this.page.locator( 'label[data-testid="PaymentForm-InnBusinessPay"] div span', );
  readonly cardNotPresentCheckbox: Locator = this.page.locator( '//*[@id="prepayCheckbox"]', );
  readonly cardNotPresentDescriptionLabel: Locator = this.page.locator( '//*[@id="prepayCheckbox"]/following-sibling::label', );
  readonly memorableWordTitleLabel: Locator = this.page.locator( 'div [data-testid="memorable-word-container"] p', );
  readonly memorableWordInput: Locator = this.page.locator( 'input[data-testid="memorableWord-Form-Input"]', );
  readonly memorableWordErrorTooltipLabel: Locator = this.page.locator( 'div [data-testid="memorableWord-Error-Tooltip"]', );
  readonly addCardDetailsButton: Locator = this.page.locator( 'button[data-testid="submit-button"]', );
  readonly cancelChangesLink: Locator = this.page.locator( 'button[data-testid="cancel-button"]', );
  readonly paymentSavedCardTitleLabel: Locator = this.page.locator( 'h4[data-testid="PaymentCard-Title"]', );
  readonly paymentSavedCardForm: Locator = this.page.locator( '//section[@data-testid="PaymentCard-Card"]', );
  readonly paymentSavedCardLabel: Locator = this.paymentSavedCardForm.locator("div[1] > p[1]");
  readonly paymentSavedCardNumberLabel: Locator = this.paymentSavedCardForm.locator("div[1] > p[2]");
  readonly paymentSavedCardHolderNameLabel: Locator = this.paymentSavedCardForm.locator("div[2] > p[1]");
  readonly paymentSavedCardExpiryDateLabel: Locator = this.paymentSavedCardForm.locator("div[2] > p[2]");
  readonly paymentSavedCardTooltipLabel: Locator = this.paymentSavedCardForm.locator("xpath=following-sibling::div//span");
  readonly deleteSavedCardButton: Locator = this.page.locator( 'button[data-testid="PaymentCard-Delete-Button"]', );
  readonly addNewCardButton: Locator = this.page.locator( 'button[data-testid="PaymentCard-Add-New-Button"]', );
  readonly billingAddress = new BillingAddressSectionComponent();

  // ######## UI actions/navigation ########
  /** Click add payment card button. */
  async clickAddPaymentCardButton(): Promise<void> {
    console.log("Click Add payment card button");
    await this.addPaymentCardButton.scrollIntoViewIfNeeded();
    await this.addPaymentCardButton.click();
  }
  /** Click add new card button. */
  async clickAddNewCardButton(): Promise<void> {
    console.log("Click Add new card button");
    await this.addNewCardButton.scrollIntoViewIfNeeded();
    await this.addNewCardButton.click();
  }
  /** Click delete card button. */
  async clickDeleteCardButton(): Promise<void> {
    console.log("Click Delete card button");
    await this.deleteSavedCardButton.scrollIntoViewIfNeeded();
    await this.deleteSavedCardButton.click();
  }
  /** Click Use personal address button. */
  async clickUsePersonalAddressButton(): Promise<void> {
    console.log("Click Use personal address button");
    await this.usePersonalAddressButton.scrollIntoViewIfNeeded();
    await this.usePersonalAddressButton.click();
  }
  /** Click Use different address button. */
  async clickUseDifferentAddressButton(): Promise<void> {
    console.log("Click Use different address button");
    await this.useDifferentAddressButton.scrollIntoViewIfNeeded();
    await this.useDifferentAddressButton.click();
  }
  /** Click new credit debit card button. */
  async clickNewCreditDebitCardButton(): Promise<void> {
    console.log("Click New Credit Debit card button");
    await this.newCreditDebitCardRadioButton.scrollIntoViewIfNeeded();
    await this.newCreditDebitCardRadioButton.click();
  }
  /** Click new InnBusiness Pay card button. */
  async clickNewInnbusinessPayCardButton(): Promise<void> {
    console.log("Click New InnBusiness Pay card button");
    await this.newInnBusinessPayCardRadioButton.scrollIntoViewIfNeeded();
    await this.newInnBusinessPayCardRadioButton.click();
  }
  /** Click card not present checkbox. */
  async clickCardNotPresentCheckbox(): Promise<void> {
    console.log("Click card not present checkbox");
    await this.cardNotPresentCheckbox.scrollIntoViewIfNeeded();
    await this.cardNotPresentCheckbox.click();
  }
  /** Set memorable word input. */
  async setMemorableWordInput({
    memorableWord,
  }: {
    memorableWord: string;
  }): Promise<void> {
    console.log(`Set Memorable Word: ${memorableWord}`);
    await this.memorableWordInput.fill(memorableWord);
  }
  /** Click Add card details button. */
  async clickAddCardDetailsButton(): Promise<void> {
    console.log("Click Add card details button");
    await this.addCardDetailsButton.scrollIntoViewIfNeeded();
    await this.addCardDetailsButton.click();
  }
  /** Click Cancel changes button. */
  async clickCancelChangesButton(): Promise<void> {
    console.log("Click Add card details button");
    await this.cancelChangesLink.scrollIntoViewIfNeeded();
    await this.cancelChangesLink.click();
  }
  /** Add payment card details through the Worldline iframe. */
  async setPaymentCard(card: CardDetails = Cards.VISA_CARD): Promise<void> {
    console.log(`Set payment card: ${card.name}`);
    await this.clickAddCardDetailsButton();
    await global.piPages.paymentDetailsPage.switchToPaymentDetailsIFrame();
    await global.piPages.paymentDetailsPage.enterCardDetails(card);
    await global.piPages.paymentDetailsPage.clickConfirmBooking();
  }

  // ######## UI validations ########
  /** Validate my profile payment type section with no saved card. */
  async validatePaymentTypeSection(): Promise<void> {
    console.log("Validate payment type section with no saved card");
    await expect( this.paymentTypeTitleLabel, "Payment type title label", ).toHaveText(await Strings.PAYMENT_TYPE.name);
    await expect( this.paymentTypeDescriptionLabel, "Payment type details label", ).toHaveText(await Strings.SAVE_CARD_DETAILS.name);
    await expect( this.addPaymentCardButton, "Add payment card button", ).toHaveText(await Strings.ADD_PAYMENT_CARD.name);
  }
  /** Validate use personal address. */
  async validateUsePersonalAddressRadioButton({
    expectedAddress,
  }: {
    expectedAddress: Address;
  }): Promise<void> {
    console.log("Validate use personal address");
    const address = [
      expectedAddress.line1,
      expectedAddress.line2,
      expectedAddress.line3,
      expectedAddress.line4,
      expectedAddress.line5,
      expectedAddress.postCode,
    ]
      .filter(Boolean)
      .join(", ");
    const country =
      expectedAddress.countryCode === "GB"
        ? await Strings.UNITED_KINGDOM_THE.name
        : await Strings.GERMANY.name;
    await expect( this.usePersonalAddressButton, "Use personal address button", ).toHaveText( `${await IbStrings.USE_PERSONAL_ADDRESS.name}\n${address}${address ? ", " : ""}${country}`, );
  }
  /** Validate payment form container. */
  async validatePaymentFormContainer(): Promise<void> {
    console.log("Validate payment form container");
    await expect( this.paymentFormContainer, "Payment form container", ).toBeVisible();
    await expect(this.paymentFormTitleLabel, "Payment form title").toHaveText( await Strings.ADD_PAYMENT_CARD.name, );
    await expect( this.billingAddressSectionTitleLabel, "Billing address section title label", ).toHaveText(await Strings.BILLING_ADDRESS.name);
    await expect( this.usePersonalAddressButton, "Use personal address button", ).toBeVisible();
    await expect( this.usePersonalAddressLabel, "Use personal address label", ).toHaveText(await IbStrings.USE_PERSONAL_ADDRESS.name);
    await expect( this.useDifferentAddressButton, "Use different address button", ).toHaveText(await Strings.USE_DIFFERENT_ADDRESS.name);
    await expect( this.paymentTypeSectionTitleLabel, "Payment type section title label", ).toHaveText(await Strings.PAYMENT_TYPE.name);
    await expect( this.paymentTypeSectionDescriptionLabel, "Payment type section description label", ).toHaveText(await Strings.PAYMENT_WILL_BE_HANDLED_BY.name);
    await expect( this.newCreditDebitCardRadioButton, "New credit debit card button", ).toHaveText(await Strings.NEW_CREDIT_DEBIT_CARD_IB.name);
    await expect( this.newInnBusinessPayCardRadioButton, "New InnBusiness Pay card button", ).toHaveText(await IbStrings.NEW_INN_BUSINESS_PAY_CARD.name);
  }
  /** Validate memorable word input. */
  async validateMemorableWordInput({
    memorableWord,
  }: {
    memorableWord: string;
  }): Promise<void> {
    console.log("Validate Memorable word input");
    await expect( this.memorableWordInput, "Memorable word input placeholder", ).toHaveAttribute("placeholder", await IbStrings.YOUR_MEMORABLE_WORD.name);
    await expect( this.memorableWordInput, "Memorable word input value", ).toHaveValue(memorableWord);
  }
  /** Validate card not present section. */
  async validateCardNotPresentSection({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log("Validate Card not present section");
    if (isDisplayed) {
      await expect( this.cardNotPresentCheckbox, "Card not present checkbox", ).toBeVisible();
      await expect( this.cardNotPresentDescriptionLabel, "Card not present description", ).toHaveText(await IbStrings.I_WANT_TO_PREPAY.name);
    } else
      await expect( this.cardNotPresentCheckbox, "Card not present checkbox", ).not.toBeVisible();
  }
  /** Validate memorable word container. */
  async validateMemorableWordContainer(): Promise<void> {
    console.log("Validate Memorable word container");
    await expect( this.memorableWordTitleLabel, "Memorable word title", ).toHaveText(await IbStrings.AUTHORISE_PAYMENT_WHEN_CARD_NOT_PRESENT.name);
    await this.validateMemorableWordInput({ memorableWord: "" });
  }
  /** Validate memorable word error tooltip. */
  async validateMemorableWordErrorTooltip(isDisplayed = false): Promise<void> {
    console.log("Validate memorable word error tooltip");
    if (isDisplayed) {
      await expect( this.memorableWordErrorTooltipLabel, "Memorable word error tooltip", ).toBeVisible();
      await expect( this.memorableWordErrorTooltipLabel, "Memorable word error tooltip label", ).toHaveText(await IbStrings.THIS_FIELD_IS_REQUIRED_IB.name);
    } else
      await expect( this.memorableWordErrorTooltipLabel, "Memorable word error tooltip", ).not.toBeVisible();
  }
  /** Validate card authorised message label. */
  async validateCardAuthorisedMessageLabel(): Promise<void> {
    console.log("Validate card authorised message label");
    await expect( this.cardAuthorisedMessageLabel, "Add payment card iframe", ).toHaveText( (await IbStrings.YOUR_CARD_HAS_BEEN_AUTHORISED.name).replace( /([a-z])([A-Z])/g, "$1\n$2", ), );
  }
  /** Validate payment type section for users with saved cards. */
  async validateSavedCardPaymentTypeSection({
    profileSavedCard,
  }: {
    profileSavedCard: SavedCard;
  }): Promise<void> {
    console.log("Validate payment type section with saved card");
    const cardLabel = Constants.IB_CARD_LABEL_MAP[profileSavedCard.cardType];
    await expect( this.paymentSavedCardTitleLabel, "Payment type title", ).toHaveText(await Strings.PAYMENT_TYPE.name);
    await expect(this.paymentSavedCardForm, "Profile saved card").toBeVisible();
    await expect( this.paymentSavedCardLabel, "Payment card type label", ).toHaveText(cardLabel);
    await expect( this.paymentSavedCardNumberLabel, "Payment card masked number", ).toHaveText(`•••• •••• •••• ${profileSavedCard.cardNumber.slice(-4)}`);
    await expect( this.paymentSavedCardHolderNameLabel, "Payment card holder name", ).toHaveText(profileSavedCard.cardHolderName);
    await expect( this.paymentSavedCardExpiryDateLabel, "Payment card expiration date", ).toHaveText( `${await IbStrings.EXPIRES.name} ${profileSavedCard.expiryDate}`, );
    if (cardLabel === IbStrings.INN_BUSINESS_PAY.data.default)
      await expect( this.paymentSavedCardTooltipLabel, "PIBA Card tooltip", ).toHaveText(await IbStrings.CARD_CAN_BE_USED_FOR_STAYS_UK.name);
    await expect(this.deleteSavedCardButton, "Delete card button").toHaveText( await IbStrings.DELETE_CARD.name, );
    await expect(this.addNewCardButton, "Add new card button").toHaveText( await Strings.ADD_PAYMENT_NEW_CARD.name, );
  }
  /** Validate saved card input against profile saved card. */
  async validateSavedCard({ card }: { card: CardDetails }): Promise<void> {
    console.log("Validate saved card");
    const cardLabel = Constants.IB_CARD_LABEL_MAP[card.cardSchemeId ?? ""];
    await expect(this.paymentSavedCardForm, "Profile saved card").toBeVisible();
    await expect( this.paymentSavedCardLabel, "Payment card type label", ).toHaveText(cardLabel);
    await expect( this.paymentSavedCardNumberLabel, "Payment card masked number", ).toHaveText(`•••• •••• •••• ${card.last4Digits ?? ""}`);
    await expect( this.paymentSavedCardHolderNameLabel, "Payment card holder name", ).toHaveText(card.name);
    await expect( this.paymentSavedCardExpiryDateLabel, "Payment card expiration date", ).toHaveText( `${await IbStrings.EXPIRES.name} ${card.expiryMonth}/${card.expiryYear.slice(-2)}`, );
  }
  /** Validate use different address section. */
  async validateUseDifferentAddressSection({
    expectedAddress,
  }: {
    expectedAddress: { address: Address };
  }): Promise<void> {
    console.log("Validate Use different address section");
    await expect( this.yourAddressTitleLabel, "Your address title label", ).toHaveText(await IbStrings.YOUR_ADDRESS_IB.name);
    await expect( this.homeAddressRadioButton, "Home address radio button", ).toHaveText(await Strings.HOME_ADDRESS.name);
    await expect( this.businessAddressRadioButton, "Business address radio button", ).toHaveText(await Strings.BUSINESS_ADDRESS_IB.name);
    if (expectedAddress.address.countryCode === "GB")
      await expect( this.billingAddress.companyAddressRadioButton, "Company address radio button", ).toBeVisible();
    else
      await expect( this.billingAddress.addressLine1Input, "Company address input", ).toBeVisible();
  }
}
