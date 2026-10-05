import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../../test-data/pib/ibStrings";
/** Card Management > InnBusiness Pay section > Create Card > Confirmation section */
export class IbPayAddCardConfirmationSectionComponent {
  private readonly page: Page = global.page;
  // ######## properties ########
  // ######## UI elements/properties ########
  readonly addCardConfirmationTitleLabel: Locator = this.page.getByTestId( "Add-Card-Success-Title", );
  readonly addCardConfirmationDescriptionLabel: Locator = this.addCardConfirmationTitleLabel.locator( "xpath=parent::node()/following-sibling::div", );
  readonly backToCardManagementButton: Locator = this.page.getByTestId("Back-Button");
  // ######## UI actions/navigation ########
  /** Click Back to card management button. */
  async clickBackToCardManagementButton(): Promise<void> {
    console.log("Click Back to card management button");
    await this.backToCardManagementButton.click();
  }
  // ######## UI validations ########
  /** Validate Add IB Pay card message. */
  async validateAddCardConfirmationSection(): Promise<void> {
    console.log("Validate Add IB Pay Card confirmation section");
    await expect( this.addCardConfirmationTitleLabel, "Add card confirmation success title", ).toHaveText(await IbStrings.YOUR_CARD_SUBMISSION_HAS_BEEN_SENT.name);
    await expect( this.addCardConfirmationDescriptionLabel, "Add card confirmation success description", ).toHaveText( `${await IbStrings.THE_CARD_HAS_BEEN_ORDERED.name}\n${await IbStrings.IF_YOU_HAVE_ANY_QUERIES.name}`, );
    await expect( this.backToCardManagementButton, "Back to card management button", ).toHaveText(await IbStrings.BACK_TO_CARD_MANAGEMENT.name);
  }
}
