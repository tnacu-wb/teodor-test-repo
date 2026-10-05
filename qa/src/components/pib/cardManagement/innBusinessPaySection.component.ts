import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../../test-data/pib/ibStrings";
import { Strings } from "../../../test-data/strings";

interface CardDetails {
  cardHolderName: string;
  cardNumber: string;
}
interface ApiCardDetails {
  cardHolderName: string;
  cardNumber: string;
  myCard: boolean;
  isActivated: boolean;
}
/** InnBusiness application > Manage > Cards > InnBusiness Pay section */
export class InnBusinessPaySectionComponent {
  private readonly page: Page = global.page;
  // ######## properties ########
  readonly filterUrl = "manage/cards?tab=innbusiness-pay&pageIndex=";
  static readonly ACCOUNT_HOLDER_CSS = 'div[data-testid="AccountHolder"]';
  // ######## UI elements/properties ########
  readonly addNewCardButton: Locator = this.page.getByTestId( "InnBusinessPayTab-add-card-button", );
  readonly editCardButtons: Locator = this.page.getByTestId( "Edit-WL-Card-Button", );
  readonly editCardsButton: Locator = this.page.getByTestId( "InnBusinessPayTab-edit-cards-button", );
  readonly showLabel: Locator = this.page .locator('div[data-testid="InnBusinessPayFilters"] div') .first();
  readonly cancelledCardsFilterButton: Locator = this.page.locator( 'a[data-testid="InnBusinessPayFilters-cancelledCards"] button', );
  readonly cancelledCardsFilterLabel: Locator = this.page.locator( 'a[data-testid="InnBusinessPayFilters-cancelledCards"] label', );
  readonly onlyMyCardsFilterButton: Locator = this.page.locator( 'a[data-testid="InnBusinessPayFilters-onlyMyCards"] button', );
  readonly onlyMyCardsFilterLabel: Locator = this.page.locator( 'a[data-testid="InnBusinessPayFilters-onlyMyCards"] label', );
  readonly yourCardColumnLabel: Locator = this.page.getByTestId( "InnBusiness-DataTable-head-myCard", );
  readonly cardHolderColumnLabel: Locator = this.page.getByTestId( "InnBusiness-DataTable-head-cardHolderName", );
  readonly cardHolderRegColumnLabel: Locator = this.page.getByTestId( "InnBusiness-DataTable-head-cardRegistrationCount", );
  readonly cardNoColumnLabel: Locator = this.page.getByTestId( "InnBusiness-DataTable-head-cardNumber", );
  readonly cardStatusColumnLabel: Locator = this.page.getByTestId( "InnBusiness-DataTable-head-cardStatus", );
  readonly cardRows: Locator = this.page.locator( 'tr[data-testid^="DataTablePage-row-"]', );
  readonly cardHolderNameLabels: Locator = this.page.locator( '[data-testid^="DataTablePage-row-cardHolderName"]', );
  readonly cardPaginationNextButton: Locator = this.page.locator( '//nav[@data-testid="DataTablePagination"][1]//ul//button/img[@alt="pagination-next"]', );
  readonly cardPaginationLastButton: Locator = this.page.locator( '(//nav[@data-testid="DataTablePagination"][1]//li[@data-testid="DataTablePagination-link"]//button)[not(descendant::img)][last()]', );
  readonly cardStatusActivateButton: Locator = this.page.getByTestId( "Activate-Card-Popup-Button", );
  readonly activateCardModal: Locator = this.page.getByTestId( "Activate-Card-Modal", );
  readonly closeButton: Locator = this.page.getByTestId( "Dialog-X-Close-Button", );
  readonly activateCardTitleLabel: Locator = this.activateCardModal.locator("div:first-child h2");
  readonly activateCardNotificationLabel: Locator = this.activateCardModal.locator("div:first-child div");
  readonly activateCardCardholderNameLabel: Locator = this.activateCardModal.locator( "div:nth-child(2) div:first-child div:first-child span", );
  readonly activateCardCardholderMaskedCardLabel: Locator = this.activateCardModal.locator( "div:nth-child(2) div:first-child div:nth-child(2)", );
  readonly activateCardContactUsLabel: Locator = this.activateCardModal.locator( "div:nth-child(2) div:nth-child(2)", );
  readonly cardReceivedCheckbox: Locator = this.page.getByTestId( "Card-Received-Checkbox", );
  readonly cancelButton: Locator = this.page.getByTestId( "Activate-Card-Cancel", );
  readonly activateCardButton: Locator = this.page.getByTestId( "Activate-Card-Confirm", );
  readonly cardTableRows: Locator = this.page.locator( 'tbody[data-testid="InnBusiness-DataTable-body"] tr[data-testid^="DataTablePage-row-"]', );
  /** Get card status by index. */
  getCardStatusLabelByIndex(cardIndex: number): Locator {
    return this.page.locator(
      `tr[data-testid="DataTablePage-row-${cardIndex}"] span[data-testid="CardStatus"]`,
    );
  }
  /** Get Your card by index. */
  getYourCardImgByIndex(cardIndex: number): Locator {
    return this.page.locator(
      `td[data-testid="DataTablePage-row-myCard-${cardIndex}"] svg`,
    );
  }
  /** Returns edit link for a card by text. */
  getEditLinkByCardHolderLabel(cardHolderLabel: string): Locator {
    return this.page.locator(
      `tr[data-rowdata*="${cardHolderLabel}"] td button`,
    );
  }
  /** Returns Resend code link for a card by text. */
  getResendCodeLinkByCardHolderLabel(cardHolderLabel: string): Locator {
    return this.page.locator(
      `tr[data-rowdata*="${cardHolderLabel}"] span[data-testid="CardHolderRegistered"] a`,
    );
  }
  /** Returns Card status label for a card by text. */
  getCardStatusLabelByCardHolderLabel(cardHolderLabel: string): Locator {
    return this.page.locator(
      `tr[data-rowdata*="${cardHolderLabel}"] span[data-testid="CardStatus"]`,
    );
  }
  // ######## UI actions/navigation ########
  /** Click Cancelled cards filter. */
  async clickCancelledCardsFilter({
    shouldBeChecked = true,
  }: { shouldBeChecked?: boolean } = {}): Promise<void> {
    console.log("Click Cancelled cards filter");
    if (
      ((await this.cancelledCardsFilterButton.getAttribute("data-state")) ===
        "unchecked") ===
      Boolean(shouldBeChecked)
    )
      await this.cancelledCardsFilterButton.click();
    await expect(this.cancelledCardsFilterButton, 'Cancelled cards filter checked state').toHaveAttribute('data-state', shouldBeChecked ? 'checked' : 'unchecked');
  }
  /** Boolean. */
  async clickOnlyMyCardsFilter({
    shouldBeChecked = true,
  }: { shouldBeChecked?: boolean } = {}): Promise<void> {
    console.log("Click Only my cards filter");
    if (
      ((await this.onlyMyCardsFilterButton.getAttribute("data-state")) ===
        "unchecked") ===
      Boolean(shouldBeChecked)
    )
    await this.onlyMyCardsFilterButton.click();
    await expect(this.onlyMyCardsFilterButton, 'Only my cards filter checked state').toHaveAttribute('data-state', shouldBeChecked ? 'checked' : 'unchecked');
  }
  /** Click Only my cards filter. */
  async clickAddNewCardButton(): Promise<void> {
    console.log("Click add new card button");
    await this.addNewCardButton.click();
    await expect(this.page.getByTestId('Inn-Business-Pay-Add-Card-title'), 'Add InnBusiness Pay Card page loaded').toBeVisible();
  }
  /** Boolean. */
  async clickCardStatusActivateButton(): Promise<void> {
    console.log("Click card status Activate button");
    await this.cardStatusActivateButton.click();
  }
  /** Click Add new card button. */
  async clickCardReceivedCheckbox(): Promise<void> {
    console.log("Click card received checkbox");
    await this.cardReceivedCheckbox.click();
  }
  /** Click card status Activate button. */
  async clickCancelButton(): Promise<void> {
    console.log("Click Cancel button");
    await this.cancelButton.click();
  }
  /** Click received checkbox. */
  async clickActivateCardButton(): Promise<void> {
    console.log("Click Activate card button");
    await this.activateCardButton.click();
  }
  /** Click Cancel button. */
  async clickEditCardButton(index = 0): Promise<void> {
    console.log("Click edit card button");
    await this.editCardButtons.nth(index).click();
    await expect(this.page.getByTestId('Inn-Business-Pay-Edit-Card-title'), 'Edit InnBusiness Pay Card page loaded').toBeVisible();
  }
  /** Click Activate Card button. */
  async clickEditCardLinkByCardHolderName(
    cardHolderName: string,
  ): Promise<void> {
    console.log(`Click edit card button for ${cardHolderName}`);
    await this.getEditLinkByCardHolderLabel(cardHolderName).click();
  }
  /** Click Edit card button. */
  async clickResendCodeLinkByCardHolderName(
    cardHolderName: string,
  ): Promise<void> {
    console.log(`Click resend code link for ${cardHolderName}`);
    await this.getResendCodeLinkByCardHolderLabel(cardHolderName).click();
  }
  /** Click Edit card button using card holder name. */
  async goToPageNumber(pageNumber: number): Promise<void> {
    console.log(`Navigate to InnBusiness Pay page number ${pageNumber}`);
    await this.page.goto(
      `${global.browser.options.baseUrl}/${this.filterUrl}${pageNumber}`,
    );
  }
  // ######## UI validations ########
  /** Click Resend code link using card holder name. */
  async validateCardFilters({
    isCancelledCardsChecked = false,
    isOnlyMyCardsChecked = false,
  }: {
    isCancelledCardsChecked?: boolean;
    isOnlyMyCardsChecked?: boolean;
  } = {}): Promise<void> {
    console.log(
      `Validate Card filters where isCancelledCardsChecked=${isCancelledCardsChecked} and isOnlyMyCardsChecked=${isOnlyMyCardsChecked}`,
    );
    await expect(this.showLabel, "Show label").toHaveText( `${await IbStrings.SHOW.name}:`, );
    await expect( this.cancelledCardsFilterButton, "Cancelled cards button", ).toHaveAttribute( "data-state", isCancelledCardsChecked ? "checked" : "unchecked", );
    await expect( this.cancelledCardsFilterLabel, "Cancelled cards label", ).toHaveText(await IbStrings.CANCELLED_CARDS.name);
    await expect( this.onlyMyCardsFilterButton, "Only my cards button", ).toHaveAttribute( "data-state", isOnlyMyCardsChecked ? "checked" : "unchecked", );
    await expect(this.onlyMyCardsFilterLabel, "Only my cards label").toHaveText( await IbStrings.ONLY_MY_CARDS.name, );
  }
  /** Go to InnBusiness Pay page number. */
  async validateAddNewCardButton({
    isDisplayed = true,
    isClickable = true,
  }: { isDisplayed?: boolean; isClickable?: boolean } = {}): Promise<void> {
    console.log("Validate Add New Card button");
    if (isDisplayed)
      await expect(this.addNewCardButton, "Add New Card button").toBeVisible();
    else {
      await expect( this.addNewCardButton, "Add New Card button", ).not.toBeVisible();
      return;
    }
    await expect(this.addNewCardButton, "Add New Card button label").toHaveText( await IbStrings.ADD_NEW_CARD.name, );
    if (isClickable)
      await expect( this.addNewCardButton, "Add New Card button enabled", ).toBeEnabled();
    else
      await expect( this.addNewCardButton, "Add New Card button disabled", ).toBeDisabled();
  }
  /** Validate Card filters. */
  async validateOnlyMyCardsFilterIsDisplayed(
    isDisplayed = true,
  ): Promise<void> {
    console.log("Validate Only my cards filter is visible");
    if (isDisplayed)
      await expect( this.onlyMyCardsFilterButton, "Only my cards button", ).toBeVisible();
    else
      await expect( this.onlyMyCardsFilterButton, "Only my cards button", ).not.toBeVisible();
  }
  /** Validate Add New Card button is displayed. */
  async validateEditCardButtons({
    areButtonsDisplayed = true,
  }: { areButtonsDisplayed?: boolean } = {}): Promise<void> {
    console.log("Validate Edit Cards button");
    const count = await this.editCardButtons.count();
    if (areButtonsDisplayed) {
      await expect( this.editCardButtons.first(), "Edit Cards button should be displayed", ).toBeVisible();
      for (let index = 0; index < count; index += 1)
        await expect( this.editCardButtons.nth(index), "Edit Cards button label", ).toHaveText(await IbStrings.EDIT_IB.name);
    } else
      await expect(count, "Edit Cards button should not be displayed").toBe(0);
  }
  /** Validate only my cards filter. */
  async validateInnBusinessPayHeaderLabels(): Promise<void> {
    console.log("Validate table header labels");
    await Promise.all(
      [
        [this.yourCardColumnLabel, IbStrings.YOUR_CARD, "Your card label"],
        [
          this.cardHolderColumnLabel,
          IbStrings.CARD_HOLDER,
          "Card holder label",
        ],
        [
          this.cardHolderRegColumnLabel,
          IbStrings.CARD_HOLDER_REGISTERED,
          "Card holder reg label",
        ],
        [this.cardNoColumnLabel, IbStrings.CARD_NO, "Card no label"],
        [
          this.cardStatusColumnLabel,
          IbStrings.CARD_STATUS,
          "Card status label",
        ],
      ].map(async ([locator, text, label]) =>
        expect(locator as Locator, label as string).toHaveText( await (text as { name: Promise<string> }).name, ), ), );
  }
  /** Validate Edit cards button. */
  async validateFilteredCardsAndPageLink({
    isCancelledCardsChecked = false,
    isOnlyMyCardsChecked = false,
    pageUrl,
    areAllCancelledCardsMyOwn = false,
  }: {
    isCancelledCardsChecked?: boolean;
    isOnlyMyCardsChecked?: boolean;
    pageUrl: string;
    areAllCancelledCardsMyOwn?: boolean;
  }): Promise<void> {
    console.log("Validate cards according to filters");
    let cancelledCards = 0;
    let otherCards = 0;
    const cardCount = await this.cardRows.count();
    for (let index = 0; index < cardCount; index += 1) {
      const yourCard = this.getYourCardImgByIndex(index);
      if (isOnlyMyCardsChecked)
        await expect(yourCard, "Your card image").toBeVisible();
      else if (!(await yourCard.isVisible())) otherCards += 1;
      if (
        isCancelledCardsChecked &&
        (await this.getCardStatusLabelByIndex(index).textContent()) ===
          (await IbStrings.CANCELLED_IB.name)
      )
        cancelledCards += 1;
    }
    await expect(this.page, "InnBusiness Pay page URL").toHaveURL( new RegExp(pageUrl.replace(/[.*+?^${}()|[\]\\]/g, "\\$&")), );
    if (
      isCancelledCardsChecked &&
      (areAllCancelledCardsMyOwn || !isOnlyMyCardsChecked)
    )
      await expect( cancelledCards, "Cards with Cancelled status", ).toBeGreaterThan(0);
    else await expect(cancelledCards, "Cards with Cancelled status").toBe(0);
    if (
      !isOnlyMyCardsChecked &&
      (areAllCancelledCardsMyOwn || isCancelledCardsChecked)
    )
      await expect(otherCards, "Cards with different owners").toBeGreaterThan( 0, );
    else await expect(otherCards, "Cards with different owners").toBe(0);
  }
  /** Validate InnBusiness Pay cards table header. */
  async validateNoTetheredAccountPageElements(): Promise<void> {
    console.log("Validate no tethered account page elements");
    const cards = this.page.locator(
      '[data-testid="BenefitsBoxes-Container"] > div',
    );
    await expect(cards, "InnBusiness Pay benefit cards").toHaveCount(3);
    const titles = [
      IbStrings.INTEREST_FREE_CREDIT,
      IbStrings.EXPENSE_MANAGEMENT,
      IbStrings.CONSOLIDATED_INVOICES,
    ];
    const descriptions = [
      IbStrings.UTILISE_INTEREST_FREE_CREDIT,
      IbStrings.PRE_AUTHORISE_EMPLOYEE_STAYS,
      IbStrings.RECEIVE_A_SINGLE_CONSOLIDATED_VAT,
    ];
    for (let index = 0; index < titles.length; index += 1) {
      await expect( cards.nth(index).locator("span").first(), "Content title label", ).toHaveText(await titles[index].name);
      await expect( cards.nth(index).locator("span").nth(1), "Content description label", ).toHaveText(await descriptions[index].name);
    }
  }
  /** Expect. */
  async validateAllCardholderNames(expectedName: string): Promise<void> {
    console.log(`Validate all cardholder names are ${expectedName}`);
    const count = await this.cardHolderNameLabels.count();
    if (count === 0) throw new Error("No cards were found for the user");
    for (let index = 0; index < count; index += 1)
      await expect( this.cardHolderNameLabels.nth(index), `Cardholder name at row ${index}`, ).toHaveText(expectedName);
  }
  /**
   * Validate cards displayed according to filter selection.
   * - No filters: only active cards, both mine and others.
   * - Cancelled cards filter: cancelled and active cards, both mine and others.
   * - Only my cards filter: only my cards (with check mark), active or cancelled depending on cancelled filter.
   */
  async validateActivateCardButton({
    isButtonChecked = false,
  }: { isButtonChecked?: boolean } = {}): Promise<void> {
    console.log("Validate Activate Card button");
    await expect(this.activateCardButton, "Activate Card button").toHaveText( await IbStrings.ACTIVATE_CARD.name, );
    if (isButtonChecked)
      await expect( this.activateCardButton, "Activate Card button enabled", ).toBeEnabled();
    else
      await expect( this.activateCardButton, "Activate Card button disabled", ).toBeDisabled();
  }
  /** Validate no tethered account page elements. */
  async validateActivateCardSection({
    isButtonChecked = false,
    cardDetails,
  }: {
    isButtonChecked?: boolean;
    cardDetails: CardDetails;
  }): Promise<void> {
    console.log("Validate Activate Card section");
    await expect(this.activateCardModal, "Activate Card Modal").toBeVisible();
    await expect( this.activateCardTitleLabel, "Activate card title label", ).toHaveText(await IbStrings.ACTIVATE_CARD.name);
    await expect( this.activateCardNotificationLabel, "Activate card notification label", ).toHaveText(await IbStrings.ACTIVATING_THIS_CARD_BEFORE.name);
    await expect( this.activateCardCardholderNameLabel, "Activate card cardholder name label", ).toHaveText(cardDetails.cardHolderName);
    await expect( this.activateCardCardholderMaskedCardLabel, "Card number with masked card label", ).toHaveText( `${await Strings.CARD_NUMBER.name}:${cardDetails.cardNumber.slice(-8)}`, );
    await expect( this.cardReceivedCheckbox, "Card received checkbox", ).toHaveAttribute("data-state", isButtonChecked ? "checked" : "unchecked");
    await expect( this.activateCardContactUsLabel, "Contact us label", ).toHaveText(await IbStrings.IF_YOU_HAVENT_RECEIVED_YOUR_IBPAY_CARD.name);
    await expect(this.cancelButton, "Cancel button").toHaveText( await Strings.CANCEL_RESEND.name, );
    await this.validateActivateCardButton({ isButtonChecked });
  }
  /** Validate all cardholder name cells have the same value. */
  async validateCardsTable(apiCardDetails: ApiCardDetails[]): Promise<void> {
    console.log("Validate cards table");
    const rows = this.cardRows;
    for (const apiCard of apiCardDetails) {
      const row = rows
        .filter({ hasText: apiCard.cardHolderName })
        .filter({ hasText: apiCard.cardNumber.slice(-4) });
      await expect(row, `Card row for ${apiCard.cardHolderName}`).toBeVisible();
      await expect( row.locator('td[data-testid^="DataTablePage-row-myCard-"] svg'), `Your card icon for ${apiCard.cardHolderName}`, ).toHaveCount(apiCard.myCard ? 1 : 0);
      await expect( row.locator('span[data-testid="CardStatus"]'), `Card status for ${apiCard.cardHolderName}`, ).toHaveText( apiCard.isActivated ? await IbStrings.ACTIVE.name : await IbStrings.NOT_ACTIVATED_STATUS.name, );
    }
  }
  /** Validate Card status by card holder name. */
  async validateCardRowRegistrationAndStatus({
    cardHolderName,
  }: {
    cardHolderName: string;
    noOfCards?: number;
  }): Promise<void> {
    console.log(`Validate registration and status for ${cardHolderName}`);
    const row = this.cardTableRows.filter({ hasText: cardHolderName });
    await expect(row, `Card row for ${cardHolderName}`).toBeVisible();
    await expect( row.locator('[data-testid="CardHolderRegistered"]'), "Card holder registration status", ).toHaveText(await IbStrings.REGISTERED.name);
    await expect( row.locator('[data-testid="CardStatus"]'), "Card status", ).toHaveText(await IbStrings.DISPATCHING.name);
  }
  /** Validate Resend Code link by card holder name. */
  async validateResendCodeLink(cardHolderName: string): Promise<void> {
    console.log(`Validate Resend Code link for card holder: ${cardHolderName}`);
    await expect( this.getResendCodeLinkByCardHolderLabel(cardHolderName), "Resend Code link", ).toHaveText(await IbStrings.RESEND_CODE.name);
  }
  /** Validate Card status by card holder name. */
  async validateCardStatus(
    cardHolderName: string,
    expectedStatus?: string,
  ): Promise<void> {
    console.log(`Validate Card status for card holder: ${cardHolderName}`);
    await expect( this.getCardStatusLabelByCardHolderLabel(cardHolderName), "Card status label", ).toHaveText(expectedStatus ?? (await IbStrings.NOT_ACTIVATED_STATUS.name));
  }
  /** Validate card row by cardHolderName for registration and status. */
  async validateEditCardsButton({
    isDisplayed = false,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log("Validate Edit cards button");
    if (isDisplayed)
      await expect(this.editCardsButton, "Edit cards button").toBeVisible();
    else
      await expect(this.editCardsButton, "Edit cards button").not.toBeVisible();
  }
}
