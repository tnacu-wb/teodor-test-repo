import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../../test-data/pib/ibStrings";
import { Strings } from "../../../test-data/strings";
/** InnBusiness application > Manage > Cards > Centrally Stored section */
export class CentrallyStoredSectionComponent {
  private readonly page: Page = global.page;
  // ######## properties ########
  // ######## UI elements/properties ########
  readonly addNewCardButton: Locator = this.page.getByTestId( "CentrallyStoredTab-add-card-button", );
  readonly cardLabelColumnLabel: Locator = this.page.getByTestId( "InnBusiness-DataTable-head-cardLabel", );
  readonly cardHolderColumnLabel: Locator = this.page.getByTestId( "InnBusiness-DataTable-head-cardHolderName", );
  readonly cardNoColumnLabel: Locator = this.page.getByTestId( "InnBusiness-DataTable-head-cardNumber", );
  readonly expiryColumnLabel: Locator = this.page.getByTestId( "InnBusiness-DataTable-head-expiryDate", );
  readonly cardStatusColumnLabel: Locator = this.page.getByTestId( "InnBusiness-DataTable-head-cardStatus", );
  readonly editCardButtons: Locator = this.page.locator( 'td[data-testid^="DataTablePage-row-actions-"] a', );
  readonly noCardLabel: Locator = this.page.locator( 'div[data-testid="CardNoResults"] div', );
  readonly cardPaginationNextButton: Locator = this.page.locator( '//nav[@data-testid="DataTablePagination"][1]//ul//button/img[@alt="pagination-next"]', );
  /** Returns edit link for a card by text. */
  getEditLinkByCardLabel(cardLabel: string): Locator {
    return this.page.locator(`tr[data-rowdata*="${cardLabel}"] td a`);
  }
  // ######## UI actions/navigation ########
  /** Click Add new card button. */
  async clickAddNewCardButton(): Promise<void> {
    console.log("Click add new card button");
    await this.addNewCardButton.click();
    await expect(this.page.getByTestId('Centrally-Stored-Add-Card-title'), 'Add Centrally Stored Card page loaded').toBeVisible();
  }
  /** Click Next button from cards table. */
  async clickNextButton(): Promise<void> {
    console.log("Click next button");
    await this.cardPaginationNextButton.click();
  }
  /** Click Edit card button. */
  async clickEditCardButton(index = 0): Promise<void> {
    console.log("Click edit card button");
    await this.editCardButtons.nth(index).click();
    await expect(this.page.getByTestId('Centrally-Stored-Add-Card-title'), 'Edit Centrally Stored Card page loaded').toBeVisible();
  }
  /** Click Edit card button using card label. */
  async clickEditCardLinkByCardLabel(cardLabel: string): Promise<void> {
    console.log(`Click edit card button for ${cardLabel} card`);
    await this.getEditLinkByCardLabel(cardLabel).click();
  }
  // ######## UI validations ########
  /** Validate Add New Card button is displayed. */
  async validateAddNewCardButton(): Promise<void> {
    console.log("Validate Add New Card button");
    await expect( this.addNewCardButton, "Add New Card button should include hover", ).toHaveAttribute("class", /hover/);
    await expect(this.addNewCardButton, "Add New Card button text").toHaveText( await IbStrings.ADD_NEW_CARD.name, );
    await expect( this.addNewCardButton, "Add New Card button enabled", ).toBeEnabled();
  }
  /** Validate Centrally stored cards table header. */
  async validateCentrallyStoredCardsHeaderLabels(): Promise<void> {
    console.log("Validate centrally stored cards table header labels");
    await Promise.all(
      [
        [this.cardLabelColumnLabel, Strings.CARD_LABEL, "Card label"],
        [
          this.cardHolderColumnLabel,
          IbStrings.CARD_HOLDER,
          "Card holder label",
        ],
        [this.cardNoColumnLabel, IbStrings.CARD_NO, "Card number label"],
        [this.expiryColumnLabel, IbStrings.EXPIRY, "Card expiry label"],
        [
          this.cardStatusColumnLabel,
          IbStrings.CARD_STATUS,
          "Card status label",
        ],
      ].map(async ([locator, value, label]) =>
        expect(locator as Locator, label as string).toHaveText( await (value as { name: Promise<string> }).name, ), ), );
  }
  /** Validate at least one Edit card button is displayed. */
  async validateEditCardButtons(): Promise<void> {
    console.log("Validate Edit card buttons are displayed");
    await expect( this.editCardButtons.first(), "At least one Edit button should be displayed for the displayed cards", ).toBeVisible();
  }
  /** Validate No cards added label for a company with no cards. */
  async validateNoCardLabel(): Promise<void> {
    console.log("Validate no card added label in centrally stored table");
    await expect(this.noCardLabel, "No card label").toHaveText( await IbStrings.NO_CARDS_ADDED.name, );
  }
  /** Validate pagination Next button is not displayed. */
  async validatePaginationNextButtonNotDisplayed(): Promise<void> {
    console.log("Validate pagination Next button is not displayed");
    await expect( this.cardPaginationNextButton, "Pagination Next button", ).not.toBeVisible();
  }
}
