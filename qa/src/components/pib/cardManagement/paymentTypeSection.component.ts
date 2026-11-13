import { type Locator, type Page } from "@playwright/test";
/** InnBusiness application > Manage > Cards > Centrally Stored section > Add/Edit Card */
export class PaymentTypeSectionComponent {
  private readonly page: Page = global.page;
  // ######## properties ########
  // ######## UI elements/properties ########
  readonly newCreditDebitCardButton: Locator = this.page.locator( '//div[@data-testid="Centrally-Stored-Card-Type"]//button[@value="NEW_CARD"]', );
  readonly newInnBusinessPayCardButton: Locator = this.page.locator( '//div[@data-testid="Centrally-Stored-Card-Type"]//button[@value="NEW_PIBA"]', );
  readonly keepExistingCardButton: Locator = this.page.locator( '//div[@data-testid="Centrally-Stored-Card-Type"]//button[starts-with(@value, "KEEP_")]', );
  // ######## UI actions/navigation ########
  /** Select New Credit card option. */
  async selectNewCreditCardOption(): Promise<void> {
    console.log("Click on New Credit card option");
    await this.newCreditDebitCardButton.scrollIntoViewIfNeeded();
    await this.newCreditDebitCardButton.click();
  }
  /** Select New InnBusiness Pay card option. */
  async selectNewInnBusinessPayCardOption(): Promise<void> {
    console.log("Click on New InnBusiness Pay card option");
    await this.newInnBusinessPayCardButton.scrollIntoViewIfNeeded();
    await this.newInnBusinessPayCardButton.click();
  }
  /** Select Keep Existing card option. */
  async selectKeepExistingCard(): Promise<void> {
    console.log("Click on Keep Existing card option");
    await this.keepExistingCardButton.scrollIntoViewIfNeeded();
    await this.keepExistingCardButton.click();
  }
  // ######## UI validations ########
}
