import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../../test-data/pib/ibStrings";
import { Strings } from "../../../test-data/strings";
/** InnBusiness application > Pay Application > Summary section */
export class SummarySectionComponent {
  private readonly page: Page = global.page;
  // ######## properties ########
  // ######## UI elements/properties ########
  readonly summaryOneLastStepTitleLabel: Locator = this.page.getByTestId("wizard-title");
  readonly summaryYourDetailsLabel: Locator = this.page.getByText(/Your details|Ihre Angaben/i);
  readonly summaryYourDetailsEditButton: Locator = this.summaryYourDetailsLabel.locator("xpath=parent::*").getByRole("button");
  readonly summaryCompanyDetailsLabel: Locator = this.page.getByText(/Company details|Unternehmensdetails/i);
  readonly summaryCompanyDetailsEditButton: Locator = this.summaryCompanyDetailsLabel.locator("xpath=parent::*").getByRole("button");
  readonly summaryCardDetailsLabel: Locator = this.page.getByText(/Card details|Kartendetails/i);
  readonly summaryCardDetailsEditButton: Locator = this.summaryCardDetailsLabel.locator("xpath=parent::*").getByRole("button");
  readonly summaryPaymentDetailsLabel: Locator = this.page.getByText(/Payment details|Zahlungsdetails|Zahlung/i);
  readonly summaryPaymentDetailsEditButton: Locator = this.summaryPaymentDetailsLabel.locator("xpath=parent::*").getByRole("button");
  readonly summaryTermsCheckbox: Locator = this.page.getByTestId( "Terms-Form-Checkbox", );
  readonly summaryTermsLabel: Locator = this.page.getByTestId("Terms-Label");
  readonly summaryMemorableQuestionTitleLabel: Locator = this.page.locator( "span.font-bold.mb-4", );
  readonly summaryMemorableQuestionDescriptionLabel: Locator = this.page.locator("span.mb-4");
  readonly summaryMemorableQuestionInfoLabel: Locator = this.page.locator("span.mb-6");
  readonly summaryMemorableAnswerInput: Locator = this.page.getByTestId( "Memorable-Answer-Form-Input", );
  readonly summarySelectMemorableQuestionButton: Locator = this.page.getByTestId("selectMemorableQuestion-IB-Form-Select-Button");
  readonly summaryMemorableQuestionsOptions: Locator = this.page.locator( '[data-testid^="selectMemorableQuestion-"][data-testid$="-Option"]', );
  readonly summaryMemorableWordErrorLabel: Locator = this.page.getByTestId( "Memorable-Answer-Error-Tooltip", );
  // ######## UI actions/navigation ########
  /** Click memorable question select. */
  async clickSummarySelectMemorableQuestionButton(): Promise<void> {
    console.log("Click memorable question select");
    await this.summarySelectMemorableQuestionButton.click();
  }
  /** Click Terms checkbox. */
  async clickTermsAndConditionsCheckbox(): Promise<void> {
    console.log("Click Terms checkbox");
    await this.summaryTermsCheckbox.click();
  }
  /** Click Your details edit. */
  async clickSummaryYourDetailsEditButton(): Promise<void> {
    console.log("Click Your details edit");
    await this.summaryYourDetailsEditButton.click();
  }
  /** Click Company details edit. */
  async clickSummaryCompanyDetailsEditButton(): Promise<void> {
    console.log("Click Company details edit");
    await this.summaryCompanyDetailsEditButton.click();
  }
  /** Click Card details edit. */
  async clickSummaryCardDetailsEditButton(): Promise<void> {
    console.log("Click Card details edit");
    await this.summaryCardDetailsEditButton.click();
  }
  /** Click Payment details edit. */
  async clickSummaryPaymentDetailsEditButton(): Promise<void> {
    console.log("Click Payment details edit");
    await this.summaryPaymentDetailsEditButton.click();
  }
  /** Set memorable answer. */
  async setMemorableWordAnswerInput({
    memorableAnswer,
  }: {
    memorableAnswer: string;
  }): Promise<void> {
    console.log("Set memorable word answer");
    await this.summaryMemorableAnswerInput.fill(memorableAnswer);
  }
  /** Select memorable question by index. */
  async selectMemorableQuestionByIndex(index: number): Promise<void> {
    console.log("Select memorable question");
    await this.clickSummarySelectMemorableQuestionButton();
    await this.summaryMemorableQuestionsOptions.nth(index).click();
  }
  // ######## UI validations ########
  /** Validate Summary section title. */
  async validateSummarySectionTitle(): Promise<void> {
    console.log("Validate Summary section title");
    await expect( this.summaryOneLastStepTitleLabel, "Summary section title", ).toHaveText(await IbStrings.ONE_LAST_STEP.name);
  }
  /** Validate memorable word input. */
  async validateMemorableWordInput({
    memorableWord,
  }: {
    memorableWord: string;
  }): Promise<void> {
    console.log("Validate memorable word input");
    await expect( this.summaryMemorableAnswerInput, "Memorable word input", ).toHaveValue(memorableWord);
    if (!memorableWord)
      await expect( this.summaryMemorableWordErrorLabel, "Memorable word error", ).toBeVisible();
  }
  /** Validate Summary labels. */
  async validateSummaryLabels(): Promise<void> {
    console.log("Validate Summary labels");
    await expect(this.summaryYourDetailsLabel, "Your details label").toHaveText( await Strings.YOUR_DETAILS.name, );
    await expect( this.summaryCompanyDetailsLabel, "Company details label", ).toHaveText(await IbStrings.COMPANY_DETAILS.name);
    await expect(this.summaryCardDetailsLabel, "Card details label").toHaveText( await IbStrings.CARD_DETAILS_PAY_APP.name, );
  }
}
