import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../../test-data/pib/ibStrings";
import { AddACardSectionComponent } from "./addACardSection.component";
/** InnBusiness application > Home > Apply now > Start application > Your details section -> Company details section -> Card details section */
export class CardDetailsSectionComponent {
  private readonly page: Page = global.page;
  // ######## properties ########
  // ######## UI elements/properties ########
  readonly cardDetailsTitleLabel: Locator = this.page.getByTestId("wizard-title");
  readonly cardDetailsDescriptionLabel: Locator = this.page .getByTestId("App-Card-Details-Container") .locator("span");
  readonly addAnotherCardButton: Locator = this.page .locator('img[alt="pay app card icon"]') .locator("xpath=parent::button");
  readonly deleteButtons: Locator = this.page.getByTestId( "Delete-PayAppCard-Button", );
  readonly cardList: Locator = this.page.locator( 'div[data-testid="App-Card-List"] div[data-testid^="App-Card-Number-"]', );
  readonly cardIconList: Locator = this.page.getByTestId("CardIcon");
  readonly cardNameList: Locator = this.page .locator("span.font-bold") .locator("xpath=parent::div");
  readonly noCardsNotificationLabel: Locator = this.page.locator( "div.notificationAlertBorder", );
  readonly deleteCardButton: Locator = this.page.getByTestId( "Delete-PayAppCard-Button", );
  readonly removeCardButton: Locator = this.page.getByTestId( "Delete-PayAppCard-Submit-Button", );
  readonly deleteCardModalTitleLabel: Locator = this.page.getByTestId( "Delete-PayAppCard-Dialog-Title", );
  readonly deleteModal: Locator = this.page.getByTestId( "Delete-PayAppCard-Dialog-Content", );
  readonly removeCardConfirmationLabel: Locator = this.page.getByTestId( "Delete-PayAppCard-Box", );
  readonly cancelCardModalButton: Locator = this.page.getByTestId( "Delete-PayAppCard-Cancel-Button", );
  readonly closeCardModalButton: Locator = this.page.getByTestId( "Dialog-X-Close-Button", );
  readonly addCardSection = new AddACardSectionComponent();
  // ######## UI actions/navigation ########
  /** Click Add another card button. */
  async clickAddAnotherCardButton(): Promise<void> {
    console.log("Click Add another card button");
    await this.addAnotherCardButton.click();
  }
  /** Click remove card button. */
  async clickRemoveCardButton(): Promise<void> {
    console.log("Click remove card button");
    await this.removeCardButton.click();
  }
  /** Click cancel from remove card modal. */
  async clickCancelRemoveCardButton(): Promise<void> {
    console.log("Click cancel remove card button");
    await this.cancelCardModalButton.click();
  }
  /** Click Delete card button. */
  async clickDeleteCardButton(index = 1): Promise<void> {
    console.log("Click delete card button");
    await this.deleteButtons.nth(index).click();
  }
  /** Remove selected card. */
  async clickToRemoveCard({
    index = 1,
  }: { index?: number } = {}): Promise<void> {
    console.log("Remove selected card");
    await this.clickDeleteCardButton(index);
    await this.clickRemoveCardButton();
  }
  // ######## UI validations ########
  /** Validate Card details page title. */
  async validateCardDetailsTitle(): Promise<void> {
    console.log("Validate Card details title");
    await expect(this.cardDetailsTitleLabel, "Card details title").toHaveText( await IbStrings.CARD_DETAILS_PAY_APP.name, );
  }
  /** Validate Card details description. */
  async validateCardDetailsDescription({
    isTMorBooker = true,
  }: { isTMorBooker?: boolean } = {}): Promise<void> {
    console.log("Validate Card details description");
    await expect( this.cardDetailsDescriptionLabel, "Card details description", ).toHaveText( await ( isTMorBooker ? IbStrings.CARD_DETAILS_DESCRIPTION_PAY_APP : IbStrings.CARD_DETAILS_PERMISSION ).name, );
  }
  /** Validate Add Another Card button. */
  async validateAddAnotherCardButton({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log("Validate Add Another Card button");
    if (isDisplayed)
      await expect( this.addAnotherCardButton, "Add another card button", ).toBeVisible();
    else
      await expect( this.addAnotherCardButton, "Add another card button", ).not.toBeVisible();
  }
  /** Validate Delete Card button. */
  async validateDeleteCardButton({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log("Validate Delete Card button");
    if (isDisplayed)
      await expect(this.deleteCardButton, "Delete card button").toBeVisible();
    else
      await expect( this.deleteCardButton, "Delete card button", ).not.toBeVisible();
  }
  /** Validate Card delete notification requirement. */
  async validateCardDeleteNotificationRequirement(): Promise<void> {
    console.log("Validate card delete notification requirement");
    await expect( this.noCardsNotificationLabel, "Card delete notification", ).toHaveText(await IbStrings.CARD_DELETE_REQUIREMENT.name);
  }
  /** Validate number of cards. */
  async validateNumberOfCards(expectedCount = 1): Promise<void> {
    console.log("Validate number of cards");
    await expect(this.cardList, "Number of cards").toHaveCount(expectedCount);
  }
  /** Validate remove card modal. */
  async validateRemoveCardModal(): Promise<void> {
    console.log("Validate remove card modal");
    await expect(this.deleteModal, "Delete card modal").toBeVisible();
    await expect( this.deleteCardModalTitleLabel, "Delete card modal title", ).toHaveText(await IbStrings.REMOVE_CARD.name);
  }
  /** Validate card component. */
  async validateCardComponent({
    employeeDetails,
    indexCard = 0,
  }: {
    employeeDetails: { title: string; foreName: string; lastName: string };
    indexCard?: number;
  }): Promise<void> {
    console.log("Validate card component");
    await expect( this.cardNameList.nth(indexCard), "Card holder name", ).toContainText( `${employeeDetails.title} ${employeeDetails.foreName} ${employeeDetails.lastName}`, );
  }
  /** Validate card name by option. */
  async validateCardNameByCardOption({
    optionName,
    cardDetails,
    employeeDetails,
    customCardName = "",
  }: {
    optionName?: string;
    cardDetails: { cardName: string };
    employeeDetails: {
      firstName: string;
      foreName?: string;
      lastName: string;
      title: string;
    };
    customCardName?: string;
  }): Promise<void> {
    console.log("Validate card name by option");
    const expected =
      optionName === (await IbStrings.CARD_NAME_CUSTOM.name)
        ? customCardName
        : `${employeeDetails.firstName || employeeDetails.foreName} ${employeeDetails.lastName}`;
    await expect(cardDetails.cardName, "Card name").toBe(expected);
  }
  /** Validate card details. */
  async validateCardDetails({
    optionName,
    cardDetails,
    employeeDetails,
    customCardName = "",
    indexCard = 0,
  }: {
    optionName?: string;
    cardDetails: { cardName: string };
    employeeDetails: {
      firstName: string;
      foreName?: string;
      lastName: string;
      title: string;
    };
    customCardName?: string;
    creditLimit?: string;
    cardUser?: string;
    indexCard?: number;
    isInitiatorEmail?: boolean;
  }): Promise<void> {
    console.log("Validate card details");
    await this.validateCardNameByCardOption({
      optionName,
      cardDetails,
      employeeDetails,
      customCardName,
    });
    await this.validateCardComponent({
      employeeDetails: {
        title: employeeDetails.title,
        foreName: employeeDetails.firstName || employeeDetails.foreName || "",
        lastName: employeeDetails.lastName,
      },
      indexCard,
    });
  }
}
