import { expect, type Locator } from "@playwright/test";
import {
  CancelCardModalComponent,
  CardDeliveryOptionsSectionComponent,
  ReplaceCardModalComponent,
  ResendCodeModalComponent,
  RestrictUsageSectionComponent,
  ReviewChangesModalComponent,
  SetCreditLimitSectionComponent,
} from "../../components/pib";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { BasePibPage } from "./basePib.page";

/**
 * InnBusiness application > Manage > Card management > InnBusinessPay > Edit InnBusiness Pay card
 */
export class EditInnBusinessPayPage extends BasePibPage {
  // ######## UI elements/properties ########
  readonly editCardPageTitleLabel: Locator = this.page.getByTestId( "Inn-Business-Pay-Edit-Card-title", );
  readonly backButton: Locator = this.page.getByTestId( "Inn-Business-Pay-Edit-Card-back-icon", );
  readonly editCardSubmitButton: Locator = this.page.getByTestId("Edit-Card-Submit");
  readonly cardNumberContainer: Locator = this.page.getByTestId( "Card-Number-Container", );
  readonly cardExpiryDateContainer: Locator = this.page.getByTestId( "Card-Expiry-Date-Container", );
  readonly cardStatusContainer: Locator = this.page.getByTestId( "Card-Status-Container", );
  readonly activateCardButton: Locator = this.page.getByTestId( "Activate-Card-Popup-Button", );
  readonly cardHolderContainer: Locator = this.cardStatusContainer.locator( "xpath=following-sibling::div[1]", );
  readonly cardHolderStatusLabel: Locator = this.cardHolderContainer .locator("div") .first();
  readonly cardHolderDetailsLabel: Locator = this.cardHolderContainer .locator("div") .nth(1);
  readonly cardHolderRegisteredButton: Locator = this.page.locator( 'span[data-testid="CardHolderRegistered"] a', );
  readonly somethingWentWrongTitleLabel: Locator = this.page.locator( '(//div[contains(@class, "bg-tooltipError")]//span)[1]', );
  readonly somethingWentWrongTextLabel: Locator = this.page.locator( '(//div[contains(@class, "bg-tooltipError")]//span)[2]', );
  readonly replaceCardIcon: Locator = this.page.getByTestId("Replace-Card-Icon");
  readonly replaceCardButton: Locator = this.replaceCardIcon.locator( "xpath=following-sibling::span", );
  readonly replaceCardDescriptionLabel: Locator = this.replaceCardIcon.locator( "xpath=parent::button/following-sibling::span", );
  readonly cancelCardIcon: Locator = this.page.getByTestId("Cancel-Card-Icon");
  readonly cancelCardButton: Locator = this.cancelCardIcon.locator( "xpath=following-sibling::span", );
  readonly cancelCardDescriptionLabel: Locator = this.cancelCardIcon.locator( "xpath=parent::div/following-sibling::span", );
  // UI components
  readonly cardDeliveryOptionSection =
    new CardDeliveryOptionsSectionComponent();
  readonly creditLimitSection = new SetCreditLimitSectionComponent();
  readonly restrictUsageSection = new RestrictUsageSectionComponent();
  readonly reviewChangesSection = new ReviewChangesModalComponent();
  readonly replaceCardModal = new ReplaceCardModalComponent();
  readonly resendCodeModal = new ResendCodeModalComponent();
  readonly cancelCardModal = new CancelCardModalComponent();
  // ######## UI actions/navigation ########
  /** Click on Back button from edit card page. */
  async clickEditCardBackButton(): Promise<void> {
    console.log("Click on Back button from edit card page");
    await this.backButton.click();
  }
  /** Click on Replace button from edit card page. */
  async clickReplaceCardButton(): Promise<void> {
    console.log("Click on Replace button from edit card page");
    await this.replaceCardButton.click();
  }
  /** Click on Cancel button from edit card page. */
  async clickCancelCardButton(): Promise<void> {
    console.log("Click on Cancel button from edit card page");
    await this.cancelCardButton.click();
  }
  /** Click on Submit button from edit card page. */
  async clickEditCardSubmitButton(): Promise<void> {
    console.log("Click on Submit button from edit card page");
    await this.editCardSubmitButton.click();
  }
  /** Click on Resend Code link from edit card page. */
  async clickResendCodeButton(): Promise<void> {
    console.log("Click on Resend Code link from edit card page");
    await this.cardHolderRegisteredButton.click();
  }
  // ######## UI validations ########
  /** Validate Edit InnBusiness Pay Card page. */
  async validateEditInnBusinessPayPage(): Promise<void> {
    console.log("Validate Edit InnBusiness Pay Card page");
    await expect( this.editCardPageTitleLabel, "Create InnBusiness Pay page title", ).toHaveText(await IbStrings.CARD_DETAILS_IB.name);
    await expect(this.backButton, "Edit card back button").toBeVisible();
  }
  /** Validate Submit button is displayed or not. */
  async validateSubmitButtonIsDisplayed(
    isDisplayed: boolean,
  ): Promise<void> {
    console.log("Validate Edit card submit button");
    if (isDisplayed) {
      await expect( this.editCardSubmitButton, "Edit card submit button", ).toBeVisible();
      await expect( this.editCardSubmitButton, "Edit card submit button label", ).toHaveText(await IbStrings.SAVE_UPDATES_CARD.name);
    } else
      await expect( this.editCardSubmitButton, "Edit card submit button", ).not.toBeVisible();
  }
  /** Validate Something went wrong message. */
  async validateSomethingWentWrongMessage(
    isDisplayed: boolean,
  ): Promise<void> {
    console.log("Validate Something went wrong message");
    if (isDisplayed) {
      await expect( this.somethingWentWrongTitleLabel, "Something went wrong title label", ).toHaveText(await IbStrings.OOPS_SOMETHING_WENT_WRONG_CARD.name);
      await expect( this.somethingWentWrongTextLabel, "Something went wrong text label", ).toHaveText(await IbStrings.OOPS_SOMETHING_WENT_WRONG_CARD_TEXT.name);
    } else {
      await expect( this.somethingWentWrongTitleLabel, "Something went wrong title label", ).not.toBeVisible();
      await expect( this.somethingWentWrongTextLabel, "Something went wrong text label", ).not.toBeVisible();
    }
  }
  /** Validate Replace Card option. */
  async validateReplaceCardOption(): Promise<void> {
    console.log("Validate Replace Card option");
    await expect(this.replaceCardIcon, "Replace Card icon").toBeVisible();
    await expect(this.replaceCardButton, "Replace Card button").toHaveText( await IbStrings.REPLACE_CARD.name, );
    await expect( this.replaceCardDescriptionLabel, "Replace Card description label", ).toHaveText(await IbStrings.REPLACE_CARD_DESCRIPTION.name);
  }
  /** Validate Cancel Card option. */
  async validateCancelCardOption(): Promise<void> {
    console.log("Validate Cancel Card option");
    await expect(this.cancelCardIcon, "Cancel Card icon").toBeVisible();
    await expect(this.cancelCardButton, "Cancel Card button").toHaveText( await IbStrings.CANCEL_CARD.name, );
    await expect( this.cancelCardDescriptionLabel, "Cancel Card description label", ).toHaveText(await IbStrings.CANCEL_CARD_DESCRIPTION.name);
  }
}
