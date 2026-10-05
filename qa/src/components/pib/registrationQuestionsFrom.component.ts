import { expect, type Locator, type Page } from "@playwright/test";
import { type AnsweredQuestions } from "../../api/response/answeredQuestions";
import { IbStrings } from "../../test-data/pib/ibStrings";

/**
 * InnBusiness registration Questions Form used on Add/Edit employee and My Profile pages
 */
export class RegistrationQuestionsFromComponent {
  private readonly page: Page = global.page;

  // ######## properties ########

  // ######## UI elements/properties ########
  readonly cardRegistrationQuestionsTitleLabel: Locator = this.page.locator( 'h4[data-testid="Registration-Questions-Heading"]', );
  readonly purchaseOrderNumberQuestionLabel: Locator = this.page.locator( 'span[data-testid="Registration-Question-Label-0"]', );
  readonly purchaseOrderNumberInput: Locator = this.page.locator( 'input[data-testid="purchaseOrderAnswer-Form-Input"]', );
  readonly customerReferenceQuestionLabel: Locator = this.page.locator( 'span[data-testid="Registration-Question-Label-1"]', );
  readonly customerReferenceInput: Locator = this.page.locator( 'input[data-testid="customerReferenceAnswer-Form-Input"]', );
  readonly answerOptionsDropdownButtons: Locator = this.page.locator( '//button[contains(@data-testid,"-Option")]', );
  readonly errorToolTip: Locator = this.page.locator( '//div[contains(@data-testid,"Error-Tooltip")]', );
  /** Get Registration Questions Sections. */
  getRegistrationQuestionsSections(): Locator {
    return this.page.url().includes("account/activate")
      ? this.page.locator(
          '//form/div[contains(@data-testid,"Registration-Question")]/div',
        )
      : this.page.locator(
          '//form/div[contains(@data-testid,"Registration-Question")]',
        );
  }
  /** Get element by text. */
  getElementByText(text: string): Locator {
    return this.page.locator(`//*[text()="${text}"]`);
  }
  /** Returns registration answer input element based on user defined question Id. */
  getUserDefinedAnswerInputByUserDefinedQuestionId(
    questionId: string,
  ): Locator {
    return this.page.locator(`//input[contains(@data-testid,'${questionId}')]`);
  }
  /** Returns registration answer button element based on user defined question Id. */
  getUserDefinedAnswerButtonByUserDefinedQuestionId(
    questionId: string,
  ): Locator {
    return this.page.locator(
      `//button[contains(@data-testid,'${questionId}')]`,
    );
  }
  /** Returns registration answer input element based on question section index. */
  getRegistrationAnswerInputElementBasedOnIndex(index: number): Locator {
    return this.getRegistrationQuestionsSections().nth(index).locator("input");
  }
  /** Returns registration answer button element based on question section index. */
  getRegistrationAnswerButtonElementBasedOnIndex(index: number): Locator {
    return this.getRegistrationQuestionsSections().nth(index).locator("button");
  }
  /** Returns registration answer dropdown button element based on index. */
  getAnswerOptionsDropdownButtonElementByTextAndQuestionId(
    text: string,
    questionId: string,
  ): Locator {
    return this.page.locator(
      `//span[contains(text(),'${text}')]/parent::button[contains(@data-testid,'${questionId}') and contains(@data-testid,'Option')]`,
    );
  }

  // ######## UI actions/navigation ########
  /** Set purchase order number input. */
  async setPurchaseOrderNumber(purchaseOrderNumber: string): Promise<void> {
    console.log(`Set purchase order number: ${purchaseOrderNumber}`);
    await this.purchaseOrderNumberInput.fill(purchaseOrderNumber);
    await this.purchaseOrderNumberInput.press("Tab");
  }
  /** Set customer reference input. */
  async setCustomerReference(customerReference: string): Promise<void> {
    console.log(`Set customer reference: ${customerReference}`);
    await this.customerReferenceInput.fill(customerReference);
    await this.customerReferenceInput.press("Tab");
  }
  /** Set user defined question answer. */
  async setUserDefinedAnswer({
    questionLabel,
    userDefinedAnswer,
    answeredQuestions,
  }: {
    questionLabel: string;
    userDefinedAnswer: string;
    answeredQuestions: AnsweredQuestions;
  }): Promise<void> {
    console.log(
      `Set answer: ${userDefinedAnswer} for question ${questionLabel}`,
    );
    const questionId =
      await answeredQuestions.getUserDefinedQuestionIdByQuestionLabel(
        questionLabel,
      );
    const answerType =
      await answeredQuestions.getUserDefinedAnswerTypeByQuestionId(questionId);
    if (!questionId || !answerType || answerType === "F")
      await this.getUserDefinedAnswerInputByUserDefinedQuestionId(
        questionId ?? "",
      ).fill(userDefinedAnswer);
    else if (answerType === "U") {
      await this.getUserDefinedAnswerButtonByUserDefinedQuestionId(
        questionId,
      ).click();
      await this.getAnswerOptionsDropdownButtonElementByTextAndQuestionId(
        userDefinedAnswer,
        questionId,
      ).click();
    }
  }

  // ######## UI validations ########
  /** Validate registration questions and answers form based on graphqlGetCompanyRegistrationQuestionsAndAnswers response. */
  async validateRegistrationQuestionsAndAnswersForm(
    registrationQuestionsAndAnswers: AnsweredQuestions,
    shouldHaveAsterix = true,
    checkAnswers = true,
  ): Promise<void> {
    console.log(
      "Validate registration questions and answers are displayed based on graphqlGetCompanyRegistrationQuestionsAndAnswers response",
    );
    if (!this.page.url().includes("account/activate"))
      await this.cardRegistrationQuestionsTitleLabel.scrollIntoViewIfNeeded();
    const expectedRegistrationQuestions =
      await registrationQuestionsAndAnswers.getRegistrationQuestions();
    const expectedAnswerTypes =
      await registrationQuestionsAndAnswers.getAnswerTypes();
    const expectedAnswers = await registrationQuestionsAndAnswers.getAnswers();
    const userDefinedAnswers =
      await registrationQuestionsAndAnswers.getUserDefinedAnswers();
    await expect( this.getRegistrationQuestionsSections(), "Number of registration questions does not match!", ).toHaveCount(expectedRegistrationQuestions.size);
    for (const [question, type] of expectedRegistrationQuestions)
      await expect( this.getElementByText( type === "mandatory" ? `${question}${shouldHaveAsterix ? " *" : " "}` : `${question} (optional)`, ), "Registration question label", ).toBeVisible();
    if (!checkAnswers) return;
    for (const [index, answerType] of expectedAnswerTypes.entries()) {
      if (answerType === "U") {
        const button =
          this.getRegistrationAnswerButtonElementBasedOnIndex(index);
        await expect(button, "Registration answer button").toBeVisible();
        if (expectedAnswers[index] && userDefinedAnswers[index])
          await expect(button, "Answer button text").toHaveText( expectedAnswers[index]?.[Number(userDefinedAnswers[index]) - 1] ?? "", );
        else await expect(button, "Answer button text").toBeEmpty();
      } else if (
        answerType === null ||
        answerType === "F" ||
        answerType === undefined
      ) {
        const input = this.getRegistrationAnswerInputElementBasedOnIndex(index);
        await expect(input, "Registration answer input").toBeVisible();
        if (userDefinedAnswers[index])
          await expect( input, `Value of input should be: ${userDefinedAnswers[index]}`, ).toHaveValue(userDefinedAnswers[index] ?? "");
        else
          await expect( input, "Registration answer input should be empty", ).toHaveValue("");
      }
    }
  }
  /** Validate error tooltip is displayed if mandatory answer is missing. */
  async validateMandatoryRegistrationAnswerInput(
    registrationQuestionsAndAnswers: AnsweredQuestions,
  ): Promise<void> {
    console.log(
      "Validate error tooltip is displayed if mandatory answer is missing",
    );
    await this.cardRegistrationQuestionsTitleLabel.scrollIntoViewIfNeeded();
    for (const [index, answerType] of (
      await registrationQuestionsAndAnswers.getMandatoryAnswerTypes()
    ).entries())
      if (!answerType || answerType === "F") {
        const input = this.getRegistrationAnswerInputElementBasedOnIndex(index);
        await input.fill("");
        await input.press("Tab");
        await expect(this.errorToolTip, "Error tooltip").toHaveText( await IbStrings.THIS_FIELD_IS_REQUIRED_IB.name, );
      }
  }
}
