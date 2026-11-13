import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../test-data/pib/ibStrings";

/**
 * Review Changes modal used on Add employee, Edit employee, My profile and Card management pages
 */
export class ReviewChangesModalComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########
  readonly reviewChangesModalContainer: Locator = this.page.getByTestId("ReviewChanges");
  readonly reviewChangesModalTitleLabel: Locator = this.reviewChangesModalContainer.locator("div h2");
  readonly reviewChangesModalDescriptionLabel: Locator = this.reviewChangesModalContainer.locator("div").nth(1);
  readonly reviewChangesModalDiscardButton: Locator = this.page.getByTestId( "ReviewChanges-Discard-Button", );
  readonly reviewChangesModalContinueButton: Locator = this.page.getByTestId( "ReviewChanges-Continue-Button", );
  readonly reviewChangesModalCloseButton: Locator = this.page.getByTestId( "Dialog-X-Close-Button", );
  readonly mainContactReviewChangesModal: Locator = this.page.getByTestId( "MainContactReviewChanges", );
  readonly mainContactReviewChangesModalTitleLabel: Locator = this.mainContactReviewChangesModal.locator("div h2");
  readonly mainContactReviewChangesModalDescriptionLabel: Locator = this.mainContactReviewChangesModal.locator("div").nth(1);

  // ######## UI actions/navigation ########
  /** Click Discard changes on review changes modal. */
  async clickDiscardChangesButton(): Promise<void> {
    console.log("Click Discard Changes button");
    await this.reviewChangesModalDiscardButton.click();
  }

  /** Click Continue editing on review changes modal. */
  async clickContinueEditingButton(): Promise<void> {
    console.log("Click Continue editing button");
    await this.reviewChangesModalContinueButton.click();
  }

  /** Click close button from review changes modal. */
  async clickCloseButton(): Promise<void> {
    console.log("Click close button from review changes modal");
    await this.reviewChangesModalCloseButton.click();
  }

  // ######## UI validations ########
  /** Validate Review changes modal. */
  async validateReviewChangesModal(isDisplayed = true): Promise<void> {
    console.log("Validate review changes modal");
    if (!isDisplayed) {
      await expect( this.reviewChangesModalContainer, "Review changes modal", ).not.toBeVisible();
      return;
    }
    await expect( this.reviewChangesModalContainer, "Review changes modal", ).toBeVisible();
    await expect( this.reviewChangesModalTitleLabel, "Review changes title label", ).toHaveText(await IbStrings.REVIEW_CHANGES.name);
    await expect( this.reviewChangesModalDescriptionLabel, "Review changes description label", ).toHaveText(await IbStrings.REVIEW_CHANGES_DESCRIPTION.name);
    await expect( this.reviewChangesModalDiscardButton, "Discard changes button", ).toHaveText(await IbStrings.DISCARD_CHANGES.name);
    await expect( this.reviewChangesModalContinueButton, "Continue editing button", ).toHaveText(await IbStrings.CONTINUE_EDITING.name);
    await expect( this.reviewChangesModalCloseButton, "Close button", ).toBeVisible();
  }

  /** Validate Main contact Review changes modal. */
  async validateMainContactReviewChangesModal(
    isDisplayed = true,
  ): Promise<void> {
    console.log("Validate main contact review changes modal");
    if (!isDisplayed) {
      await expect( this.mainContactReviewChangesModal, "Main contact review changes modal", ).not.toBeVisible();
      return;
    }
    await expect( this.mainContactReviewChangesModal, "Main contact review changes modal", ).toBeVisible();
    await expect( this.mainContactReviewChangesModalTitleLabel, "Main contact review changes title label", ).toHaveText(await IbStrings.MAIN_CONTACT_REVIEW_CHANGES_TITLE.name);
    await expect( this.mainContactReviewChangesModalDescriptionLabel, "Main contact review changes description label", ).toHaveText(await IbStrings.MAIN_CONTACT_REVIEW_CHANGES_DESCRIPTION.name);
    await expect( this.reviewChangesModalDiscardButton, "Discard changes button", ).toHaveText(await IbStrings.DISCARD_CHANGES.name);
    await expect( this.reviewChangesModalContinueButton, "Continue editing button", ).toHaveText(await IbStrings.CONTINUE_EDITING.name);
    await expect( this.reviewChangesModalCloseButton, "Close button", ).toBeVisible();
  }
}
