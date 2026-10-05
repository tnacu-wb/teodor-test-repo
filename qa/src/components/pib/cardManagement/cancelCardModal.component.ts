import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../../test-data/pib/ibStrings";

/**
 * InnBusiness application > Manage Cards > InnBusiness Pay section > Edit Card > Cancel card modal
 */
export class CancelCardModalComponent {
  private readonly page: Page = global.page;

  // ######## properties ########

  // ######## UI elements/properties ########
  readonly cancelCardModal: Locator = this.page.locator( '//div[@data-testid="Cancel-Card-Dialog"]', );
  readonly cancelCardTitleLabel: Locator = this.cancelCardModal.locator("div h2");
  readonly actionCannotBeUndoneDescriptionLabel: Locator = this.cancelCardModal .locator("div") .nth(1);
  readonly closeButton: Locator = this.page.locator( 'button[data-testid="Dialog-X-Close-Button"]', );
  readonly cancelButton: Locator = this.page.locator( 'button[data-testid="CancelCardDialog-Cancel-Button"]', );
  readonly cancelCardButton: Locator = this.page.locator( 'button[data-testid="CancelCardDialog-Cancel-Card-Button"]', );

  // ######## UI actions/navigation ########
  /** Click close button. */
  async clickCloseButton(): Promise<void> {
    console.log("Click on close button from Cancel Card modal");
    await this.closeButton.click();
  }

  /** Click cancel button. */
  async clickCancelButton(): Promise<void> {
    console.log("Click on Cancel button from Cancel Card modal");
    await this.cancelButton.click();
  }

  /** Click cancel card button. */
  async clickCancelCardButton(): Promise<void> {
    console.log("Click on Cancel Card button from Cancel Card modal");
    await this.cancelCardButton.click();
  }

  // ######## UI validations ########
  /** Validate cancel card modal. */
  async validateCancelCardModal({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log(
      `Validate that Cancel Card modal is ${isDisplayed ? "" : "not "}displayed`,
    );
    if (!isDisplayed) {
      await expect(this.cancelCardModal, "Cancel Card modal").not.toBeVisible();
      return;
    }

    await expect(this.cancelCardModal, "Cancel Card modal").toBeVisible();
    await expect( this.cancelCardTitleLabel, "Cancel card title label", ).toHaveText(await IbStrings.CANCEL_CARD.name);
    const expectedCancelCardNotificationText = `${await IbStrings.CANCEL_CARD_ACTION_CANNOT_BE_UNDONE.name}\n${await IbStrings.REPLACE_CARD_LOST_ACTION_CANNOT_BE_UNDONE_2.name}`;
    const formattedExpectedText = expectedCancelCardNotificationText.replace(
      /([.,!?;:])(?=\S)/g,
      "$1 ",
    );
    await expect( this.actionCannotBeUndoneDescriptionLabel, "Action cannot be undone description label", ).toHaveText(formattedExpectedText);
    await expect(this.closeButton, "Close button").toBeVisible();
    await expect(this.cancelButton, "Cancel button").toHaveText( await IbStrings.CANCEL.name, );
    await expect(this.cancelCardButton, "Cancel card button").toBeEnabled();
    await expect(this.cancelCardButton, "Cancel card button label").toHaveText( await IbStrings.CANCEL_CARD.name, );
  }
}
