import { expect, type Locator } from "@playwright/test";
import {
  CentrallyStoredSectionComponent,
  CompanyAddressSectionComponent,
  MemorableWordSectionComponent,
  PaymentTypeSectionComponent,
  ReviewChangesModalComponent,
  ToastNotificationSectionComponent,
} from "../../components/pib";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { BasePibPage } from "./basePib.page";
import { PaymentDetailsPage } from "./paymentDetails.page";
import { PaymentSixCardSol3dSecureHostPage } from "./paymentSixCardSol3dSecureHost.page";

interface CardDetails {
  name: string;
  number: string;
  expiryMonth: string;
  expiryYear: string;
}
interface CreateOrEditCreditCardOptions {
  cardLabel?: string;
  card?: CardDetails;
  isBACCard?: boolean;
  isConfirmed?: boolean;
  isCardNotPresent?: boolean;
  isKeepExisting?: boolean;
  memorableWord?: string;
}
interface CompanyPaymentCard {
  cardLabel: string;
  nameOnCard: string;
  cardNumber: string;
  expiryDate: string;
  cardType: string;
}

/**
 * InnBusiness application > Card management > Centrally stored tab > Add/Edit Card
 */
export class AddEditCentrallyStoredPage extends BasePibPage {
  // ######## UI elements/properties ########
  readonly addEditCentrallyStoredCardBackButton: Locator = this.page.getByTestId("Centrally-Stored-Add-Card-back-icon");
  readonly addEditCentrallyStoredCardPageTitleLabel: Locator = this.page.getByTestId("Centrally-Stored-Add-Card-title");
  readonly cardDetailsTitleLabel: Locator = this.page.locator( "div.form-details-box span", );
  readonly cardInput: Locator = this.page.getByTestId("cardLabel-Form-Input");
  readonly cardErrorTooltip: Locator = this.page.getByTestId( "cardLabel-Error-Tooltip", );
  readonly billingAddressContainer: Locator = this.page.getByTestId("AddressDetails");
  readonly billingTitleLabel: Locator = this.billingAddressContainer.locator( "xpath=preceding-sibling::div[1]/span", );
  readonly companyNameLabel: Locator = this.page.getByTestId( "AddressDetails-company-name", );
  readonly editAddressButton: Locator = this.page.getByTestId( "Centrally-Card-Address-Edit", );
  readonly cardNotPresentButton: Locator = this.page.getByTestId( "Card-Carry-Form-Checkbox", );
  readonly cardNotificationLabel: Locator = this.page.locator( 'img[alt="notification icon"] + div span', );
  readonly submitAddCard: Locator = this.page.getByTestId("Submit-Add-Card");
  readonly deleteButton: Locator = this.page.locator( 'img[data-testid="delete-card-icon"] + button', );
  readonly submitDeleteCardButton: Locator = this.page.getByTestId( "Submit-Delete-Card-Button", );
  readonly deleteCardModalTitleLabel: Locator = this.page.getByTestId("Delete-Card-Title");
  readonly cancelCardModalButton: Locator = this.page.getByTestId( "Delete-Card-Cancel-Button", );
  readonly closeCardModalButton: Locator = this.page.getByTestId( "Dialog-X-Close-Button", );
  readonly cardTableRows: Locator = this.page.locator( 'tbody[data-testid="InnBusiness-DataTable-body"] tr[data-testid^="DataTablePage-row-"]', );
  readonly cardNameLabelList: Locator = this.page.locator( 'td[data-testid^="DataTablePage-row-cardLabel-"]', );
  readonly cardHolderNameLabelList: Locator = this.page.locator( 'td[data-testid^="DataTablePage-row-cardHolderName-"]', );
  readonly cardNumberLabelList: Locator = this.page.locator( 'td[data-testid^="DataTablePage-row-cardNumber-"]', );
  readonly cardExpiryDateLabelList: Locator = this.page.locator( 'td[data-testid^="DataTablePage-row-expiryDate-"]', );
  readonly cardStatusLabelList: Locator = this.page.locator( 'td[data-testid^="DataTablePage-row-cardStatus-"]', );
  readonly cardIconList: Locator = this.page.getByTestId("CardIcon");
  // UI components
  readonly reviewChanges = new ReviewChangesModalComponent();
  readonly companyAddress = new CompanyAddressSectionComponent();
  readonly paymentTypeSection = new PaymentTypeSectionComponent();
  readonly memorableWordSection = new MemorableWordSectionComponent();
  readonly toastNotificationSection = new ToastNotificationSectionComponent();
  readonly centrallyStoredSection = new CentrallyStoredSectionComponent();
  readonly paymentDetailsPage = new PaymentDetailsPage();
  readonly paymentSixCardSol3dSecureHostPage =
    new PaymentSixCardSol3dSecureHostPage();
  // ######## UI actions/navigation ########
  /** Click Add/Edit Centrally Stored Card back button. */
  async clickAddEditCentrallyStoredCardBackButton(): Promise<void> {
    console.log("Click Add/Edit Centrally Stored Card back button");
    await this.addEditCentrallyStoredCardBackButton.click();
  }
  /** Click Edit button. */
  async clickEditButton(): Promise<void> {
    console.log("Click edit button");
    await this.editAddressButton.click();
  }
  /** Click Modal Close button. */
  async clickModalCloseButton(): Promise<void> {
    console.log("Click Modal close button");
    await this.closeCardModalButton.click();
  }
  /** Click modal Cancel button. */
  async clickModalCancelButton(): Promise<void> {
    console.log("Click modal cancel button");
    await this.cancelCardModalButton.click();
  }
  /** Click delete card button. */
  async clickDeleteCardButton(): Promise<void> {
    console.log("Click delete card button");
    await this.deleteButton.click();
  }
  /** Click submit delete card button. */
  async clickSubmitDeleteCardButton(): Promise<void> {
    console.log("Click delete card button");
    await this.submitDeleteCardButton.click();
  }
  /** Delete all cards in the Centrally Stored section. */
  async deleteAllCards(): Promise<void> {
    console.log("Delete all cards");
    while (await this.centrallyStoredSection.editCardButtons.count()) {
      await this.centrallyStoredSection.clickEditCardButton(0);
      await this.clickDeleteCardButton();
      await this.clickSubmitDeleteCardButton();
      await expect( this.page.getByTestId("ManageCardsPage-container"), "Card Management page after card deletion", ).toBeVisible();
    }
  }
  /** Click to CNP check Box. */
  async clickCnpCheckBox(): Promise<void> {
    console.log("Click CNP check box");
    await this.cardNotPresentButton.click();
  }
  /** Click Add card details button. */
  async clickAddCardDetailsButton(): Promise<void> {
    console.log("Click Add card details button");
    await this.submitAddCard.click();
  }
  /** Set value to card input. */
  async setCardNameInput({
    value,
    pressTab = true,
  }: {
    value: string;
    pressTab?: boolean;
  }): Promise<void> {
    console.log(`Set value - ${value} to card name`);
    await this.cardInput.fill(value);
    if (pressTab) await this.cardInput.press("Tab");
  }
  /** Create or edit credit card. */
  async createOrEditCreditCard({
    cardLabel = "",
    card,
    isBACCard = false,
    isConfirmed = true,
    isCardNotPresent = false,
    isKeepExisting = false,
    memorableWord,
  }: CreateOrEditCreditCardOptions = {}): Promise<void> {
    console.log(`Edit or create new credit card - ${cardLabel}`);
    await this.setCardNameInput({ value: cardLabel });
    if (isBACCard)
      await this.paymentTypeSection.selectNewInnBusinessPayCardOption();
    else await this.paymentTypeSection.selectNewCreditCardOption();
    if (isKeepExisting) await this.paymentTypeSection.selectKeepExistingCard();
    if (isCardNotPresent) {
      await this.clickCnpCheckBox();
      if (memorableWord)
        await this.memorableWordSection.setMemorableWordValue({
          value: memorableWord,
        });
    }
    await this.clickAddCardDetailsButton();
    if (!isKeepExisting && card) {
      const frame =
        await this.paymentDetailsPage.switchToPaymentDetailsIFrame();
      await frame
        .locator("input#card_pan, div#input_card_number input")
        .fill(card.number.replace(/\s/g, ""));
      await frame.locator("input#card_holder_first_name").fill(card.name);
      await frame
        .locator("input#card_expiry_date")
        .fill(`${card.expiryMonth}/${card.expiryYear.slice(-2)}`);
      await frame.locator("input#paybutton").click();
      if (!isBACCard) {
        if (isConfirmed)
          await this.paymentSixCardSol3dSecureHostPage.confirmPayment();
        else await this.paymentSixCardSol3dSecureHostPage.declinePayment();
      }
    }
  }
  // ######## UI validations ########
  /** Validate add centrally stored card page title. */
  async validateAddCentrallyStoredCardPageTitle(): Promise<void> {
    console.log("Validate add centrally stored card page title");
    await expect( this.addEditCentrallyStoredCardPageTitleLabel, "Add centrally stored card title label", ).toHaveText(await IbStrings.ADD_A_CENTRALLY_STORED_CARD.name);
  }
  /** Validate edit centrally stored card page title. */
  async validateEditCentrallyStoredPageTitle(): Promise<void> {
    console.log("Validate edit centrally stored card page title");
    await expect( this.addEditCentrallyStoredCardPageTitleLabel, "Edit card title label", ).toHaveText(await IbStrings.EDIT_CARD_DETAILS.name);
  }
  /** Validate card details title. */
  async validateCardDetailsTitle(): Promise<void> {
    console.log("Validate card details title");
    await expect( this.cardDetailsTitleLabel, "Card details title label", ).toHaveText(await IbStrings.CARD_DETAILS_IB.name);
  }
  /** Validate toast notification. */
  async validateToastNotification(): Promise<void> {
    console.log("Validate toast notification");
    await this.toastNotificationSection.validateToastNotification({
      message: await IbStrings.YOUR_CARD_HAS_BEEN_SAVED_IN_YOUR_ACCOUNT.name,
    });
  }
  /** Validate delete toast notification. */
  async validateDeleteToastNotification(): Promise<void> {
    console.log("Validate delete toast notification");
    await this.toastNotificationSection.validateToastNotification({
      message: await IbStrings.THE_CARD_HAS_BEEN_DELETED.name,
    });
  }
  /** Validate success toast notification after editing a card. */
  async validateEditCardSuccessNotification(): Promise<void> {
    console.log("Validate edit card success notification");
    await this.toastNotificationSection.validateToastNotification({
      message: await IbStrings.THE_CHANGES_ON_YOUR_CARD_HAVE_BEEN_SAVED.name,
    });
  }
  /** Validate fail toast notification. */
  async validateFailToastNotification(): Promise<void> {
    console.log("Validate fail toast notification");
    await this.toastNotificationSection.validateGenericNotificationTooltipLabel(
      { message: await IbStrings.YOUR_CARD_CANNOT_BE_SAVED.name },
    );
  }
  /** Validate Card Error Tooltip. */
  async validateCardErrorTooltip(
    isTooltipDisplayed = true,
  ): Promise<void> {
    console.log("Validate card error tooltip");
    if (isTooltipDisplayed) {
      await expect(this.cardErrorTooltip, "Card Error Tooltip").toBeVisible();
      await expect(this.cardErrorTooltip, "Card error tooltip").toHaveText( await IbStrings.CARD_ERROR_TOOLTIP.name, );
    } else
      await expect( this.cardErrorTooltip, "Card Error Tooltip", ).not.toBeVisible();
  }
  /** Validate Submit Add card. */
  async validateSubmitAddCardButton(
    isEditCardDisplayed = true,
  ): Promise<void> {
    console.log("Validate submit add card button");
    await expect(this.submitAddCard, "Submit Add card button").toHaveText( await ( isEditCardDisplayed ? IbStrings.SAVE_UPDATES_CARD : IbStrings.ADD_CARD_DETAILS ).name, );
  }
  /** Validate Back button is displayed. */
  async validateBackButton(): Promise<void> {
    console.log("Validate back button is displayed");
    await expect( this.addEditCentrallyStoredCardBackButton, "Back button", ).toBeVisible();
  }
  /** Validate Delete card modal. */
  async validateDeleteCardModal(): Promise<void> {
    console.log("Validate delete card modal");
    await expect( this.deleteCardModalTitleLabel, "Delete Card modal title", ).toHaveText(await IbStrings.DELETE_CARD_CARD_MANAGEMENT.name);
    await expect(this.cancelCardModalButton, "Cancel button").toHaveText( await IbStrings.CANCEL.name, );
    await expect(this.closeCardModalButton, "Close button").toBeVisible();
    await expect(this.submitDeleteCardButton, "Delete Card button").toHaveText( await IbStrings.DELETE_CARD.name, );
  }
  /** Check we reached the current page by checking a specific element from the page. */
  async validatePage(): Promise<void> {
    console.log("Validate Add/Edit Centrally Stored Card page");
    await this.validatePageMarker(
      this.addEditCentrallyStoredCardPageTitleLabel,
      "Add/Edit Centrally Stored Card",
    );
  }
  /** Verify if a card row is deleted. */
  async verifyCardRowIsDeleted(
    cardLabel: string,
  ): Promise<boolean> {
    console.log(`Verify card row is deleted: ${cardLabel}`);
    return !(await this.cardNameLabelList.allTextContents()).includes(
      cardLabel,
    );
  }
  /** Validate card row details by index. */
  async validateCardRowByIndex(
    apiResponse: CompanyPaymentCard,
    rowIndex = 0,
  ): Promise<void> {
    console.log(`Validate card row with index ${rowIndex}`);
    const expiryDate =
      apiResponse.expiryDate[2] === "/"
        ? apiResponse.expiryDate
        : `${apiResponse.expiryDate.slice(0, 2)}/${apiResponse.expiryDate.slice(2)}`;
    await expect(this.cardNameLabelList.nth(rowIndex), "Card label").toHaveText( apiResponse.cardLabel, );
    await expect( this.cardHolderNameLabelList.nth(rowIndex), "Card holder name", ).toHaveText(apiResponse.nameOnCard);
    await expect( this.cardNumberLabelList.nth(rowIndex), "Card number", ).toHaveText(apiResponse.cardNumber.slice(-8));
    await expect( this.cardExpiryDateLabelList.nth(rowIndex), "Card expiry date", ).toHaveText(expiryDate);
    await this.validateCardIconByIndex(apiResponse.cardType, rowIndex);
  }
  /** Validate card icon by index. */
  async validateCardIconByIndex(
    cardType: string,
    rowIndex = 0,
  ): Promise<void> {
    console.log(`Validate card icon with type ${cardType}`);
    const icons: Record<string, string> = {
      AC: "mastercard",
      MA: "mastercard",
      MC: "mastercard",
      AM: "amex",
      AX: "amex",
      DL: "visa",
      EL: "visa",
      VI: "visa",
      VS: "visa",
      DI: "dinners",
      DN: "dinners",
      AT: "piba",
      PI: "piba",
      BD: "piba",
      PE: "piba",
    };
    const expectedIcon = icons[cardType];
    if (!expectedIcon) throw new Error(`Unknown card type: ${cardType}`);
    await expect( this.cardIconList.nth(rowIndex), `Card icon for ${cardType}`, ).toHaveAttribute("src", new RegExp(expectedIcon, "i"));
  }
}
