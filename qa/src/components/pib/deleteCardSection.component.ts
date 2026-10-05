import { expect, type Locator, type Page } from "@playwright/test";

/**
 * InnBusiness delete card modal
 */
export class DeleteCardSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########
  readonly deleteCardModal: Locator = this.page.getByTestId("DeleteModal-Card");
  readonly deleteCardCloseButton: Locator = this.page.getByTestId( "Dialog-X-Close-Button", );
  readonly deleteCardTitleLabel: Locator = this.deleteCardCloseButton.locator( "xpath=following-sibling::div[1]", );
  readonly deleteCardDescriptionLabel: Locator = this.deleteCardCloseButton.locator("xpath=following-sibling::div[2]");
  readonly deleteCardCancelButton: Locator = this.page.getByTestId( "DeleteModal-Card-CancelButton", );
  readonly deleteCardDeleteButton: Locator = this.page.getByTestId( "DeleteModal-Card-DeleteButton", );

  // ######## UI actions/navigation ########
  /** Click delete card close button. */
  async clickDeleteCardCloseButton(): Promise<void> {
    console.log("Click delete card close button");
    await this.deleteCardCloseButton.scrollIntoViewIfNeeded();
    await this.deleteCardCloseButton.click();
  }

  /** Click cancel delete card button. */
  async clickDeleteCardCancelButton(): Promise<void> {
    console.log("Click cancel delete card button");
    await this.deleteCardCancelButton.scrollIntoViewIfNeeded();
    await this.deleteCardCancelButton.click();
  }

  /** Click delete card button. */
  async clickDeleteCardButton(): Promise<void> {
    console.log("Click delete card button");
    await this.deleteCardDeleteButton.scrollIntoViewIfNeeded();
    await this.deleteCardDeleteButton.click();
  }

  // ######## UI validations ########
  /** Validate delete card modal. */
  async validateDeleteCardModal(isDisplayed = false): Promise<void> {
    console.log("Validate delete card modal");
    if (!isDisplayed) {
      await expect(this.deleteCardModal, "Delete card modal").not.toBeVisible();
      return;
    }
    await expect(this.deleteCardModal, "Delete card modal").toBeVisible();
    await expect(this.deleteCardTitleLabel, "Delete card title").toBeVisible();
    await expect( this.deleteCardDescriptionLabel, "Delete card description", ).toBeVisible();
    await expect( this.deleteCardCloseButton, "Delete card close button", ).toBeVisible();
    await expect( this.deleteCardCancelButton, "Delete card cancel button", ).toBeVisible();
    await expect( this.deleteCardDeleteButton, "Delete card delete button", ).toBeVisible();
  }
}
