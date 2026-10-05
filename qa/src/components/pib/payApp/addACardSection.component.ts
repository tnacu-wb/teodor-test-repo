import { type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../../test-data/pib/ibStrings";
import { CardHolderSectionComponent } from "../cardHolderSection.component";
import { ConsentSectionComponent } from "../consentSection.component";
import { ReviewChangesModalComponent } from "../reviewChangesModal.component";
import { SetCreditLimitSectionComponent } from "../setCreditLimitSection.component";
import { WhoWillUseThisCardSectionComponent } from "../whoWillUseThisCardSection.component";
/** InnBusiness application > Home > Apply now > Start application > Your details section -> Company details section -> Card details section -> Add a card section */
export class AddACardSectionComponent {
  private readonly page: Page = global.page;
  // ######## properties ########
  // ######## UI elements/properties ########
  readonly addCardButton: Locator = this.page .locator('button[data-testid="footer-button"]') .filter({ hasText: IbStrings.ADD_CARD.data.default });
  readonly whoWillUseThisCardSection = new WhoWillUseThisCardSectionComponent();
  readonly cardHolderSection = new CardHolderSectionComponent();
  readonly creditLimitSection = new SetCreditLimitSectionComponent();
  readonly consentSection = new ConsentSectionComponent();
  readonly reviewChanges = new ReviewChangesModalComponent();
  // ######## UI actions/navigation ########
  /** Click Add Card button. */
  async clickAddCardButton(): Promise<void> {
    console.log("Click Add Card button");
    await this.addCardButton.click();
  }
  // ######## UI validations ########
}
