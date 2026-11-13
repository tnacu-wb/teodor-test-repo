import { expect, type Locator } from "@playwright/test";
import {
  ReviewChangesModalComponent,
  ToastNotificationSectionComponent,
} from "../../components/pib";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { BasePibPage } from "./basePib.page";

interface CreateNewCustomQuestionOptions {
  count?: number;
  expectedQuestionLabel: string[];
  isMandatory?: boolean;
}
interface SetValueToBusinessQuestionOptions {
  questionTitle: string;
}
interface EditBusinessQuestionOptions {
  expectedQuestionTitle: string;
  isMandatory?: boolean;
  isActivated?: boolean;
}
interface ValidateCustomQuestionOptions {
  label: string;
  mandatory: boolean;
}
interface ValidateBusinessQuestionStatusOptions {
  expectedStatus: string;
}

/** InnBusiness application > Manage > Employee Questions */
export class EmployeeQuestionsPage extends BasePibPage {
  readonly url = "manage/questions";

  // ######## UI elements/properties ########
  readonly employeeQuestionsTitleLabel: Locator = this.page.locator( 'div[data-testid="EmployeeQuestions-container"] h1', );
  readonly customQuestionsTitleLabel: Locator = this.page.locator( '//div[@data-testid="CustomQuestions-container"]//p[1]', );
  readonly customQuestionsDescriptionLabel: Locator = this.page.locator( '//div[@data-testid="CustomQuestions-container"]//p[2]', );
  readonly createCustomQuestionButton: Locator = this.page.getByTestId( "CustomQuestions-button-add", );
  readonly customQuestionDetailsTitleLabel: Locator = this.page.getByTestId( "CustomQuestionDetailsForm--title", );
  readonly customQuestionTitleInput: Locator = this.page.locator( 'input[placeholder="Question title"], input[placeholder="Fragentitel"]', );
  readonly customQuestionNewQuestionImage: Locator = this.page.locator( 'tbody[data-testid="CustomQuestions-table-body"] tr:last-child img[data-testid="Expand-Collapse-Icon"]', );
  readonly customQuestionTitleDescriptionLabel: Locator = this.page.getByTestId( "CustomQuestionDetailsForm-questionTitle--description", );
  readonly customQuestionLabelInput: Locator = this.page.locator( 'input[placeholder="Question label"], input[placeholder="Fragenbezeichnung"]', );
  readonly customQuestionLabelDescriptionLabel: Locator = this.page.getByTestId( "CustomQuestionDetailsForm-questionLabel--description", );
  readonly CustomQuestionNotificationMessageLabel: Locator = this.page.locator( ".bg-notificationAlertBg .flex-col > span", );
  readonly mandatoryQuestionCheckbox: Locator = this.page.locator( 'button[data-testid*="CustomQuestionDetailsForm"][data-testid*="mandatoryQuestion-Form-Checkbox"]', );
  readonly customQuestionMandatoryQuestionLabel: Locator = this.page.getByTestId("CustomQuestionDetailsForm--mandatoryQuestion-Label");
  readonly customQuestionWhenToAskTitleLabel: Locator = this.page.getByTestId( "WhenShouldWeAskForm--title", );
  readonly customQuestionWhenRegisteringLabel: Locator = this.page.locator( ".text-sm.font-medium.leading-none.flex.items-center.space-x-2 .font-semibold.text-base#WhenShouldWeAskForm--1", );
  readonly customQuestionWhenRegisteringRadioButton: Locator = this.page.locator( 'label[for="WhenShouldWeAskForm--1"] > button.aspect-square', );
  readonly customQuestionWhenBookingLabel: Locator = this.page.locator( ".text-sm.font-medium.leading-none.flex.items-center.space-x-2 .font-semibold.text-base#WhenShouldWeAskForm--2", );
  readonly customQuestionHowToAnswerLabel: Locator = this.page.getByTestId( "HowToAnswerForm--title", );
  readonly customQuestionSelectPresetAnswersLabel: Locator = this.page.locator( ".text-sm.font-medium.leading-none.flex.items-center.space-x-2 .font-semibold.text-base#HowToAnswerForm--1", );
  readonly customQuestionAddOwnAnswersLabel: Locator = this.page.locator( ".text-sm.font-medium.leading-none.flex.items-center.space-x-2 .font-semibold.text-base#HowToAnswerForm--2", );
  readonly customQuestionSelectPresetAnswersRadioButton: Locator = this.page.locator('button#HowToAnswerForm--1[role="radio"]');
  readonly customQuestionAddOwnAnswersRadioButton: Locator = this.page.locator( 'button#HowToAnswerForm--2[role="radio"]', );
  readonly customQuestionPresetAnswerInput: Locator = this.page.locator( 'input[placeholder="Pre-set answer"], input[placeholder="Vordefinierte Antwort"]', );
  readonly createCustomQuestionAddAnswerButton: Locator = this.page.getByTestId( "HowToAnswerForm--add-answer-button", );
  readonly customQuestionSaveUpdatesButton: Locator = this.page.getByTestId( "CustomEmployeeQuestions-undefined-save-changes-button", );
  readonly customQuestionEditSaveUpdatesButton: Locator = this.page.locator( '[data-testid^="CustomEmployeeQuestions-"][data-testid$="-save-changes-button"]', );
  readonly customQuestionDeleteQuestionButton: Locator = this.page.locator( 'button[data-testid^="CustomEmployeeQuestions-Delete-question-"]', );
  readonly customQuestionConfirmDeleteQuestionButton: Locator = this.page.getByTestId("DeleteModal-Question-DeleteButton");
  readonly customQuestionExpandIcon: Locator = this.page.locator( 'div[data-testid="CustomQuestions-container"] img[data-testid="Expand-Collapse-Icon"]', );
  readonly customQuestionExpandedQuestionSection: Locator = this.page.getByTestId("CustomQuestions-row-custom-0-expanded");
  readonly anyCustomQuestionExpandedSection: Locator = this.page.locator( '[data-testid^="CustomQuestions-row-custom-"][data-testid$="-expanded"]', );
  readonly customQuestionContainers: Locator = this.page.locator( '[data-testid^="CustomEmployeeQuestions-"][data-testid$="-container"]', );
  readonly customQuestionsExpandButtons: Locator = this.page.locator( 'tbody[data-testid="CustomQuestions-table-body"] img[data-testid="Expand-Collapse-Icon"]', );
  readonly customQuestionTitleErrorTooltip: Locator = this.page.locator( '[data-testid="CustomQuestionDetailsForm--container"] > div:nth-of-type(1) [data-testid="CustomQuestionDetailsForm-typeOfQuestion--Error-Tooltip"]', );
  readonly customQuestionLabelErrorTooltip: Locator = this.page.locator( '[data-testid="CustomQuestionDetailsForm--container"] > div:nth-of-type(2) [data-testid="CustomQuestionDetailsForm-typeOfQuestion--Error-Tooltip"]', );
  readonly customQuestionRowsLabel: Locator = this.page.locator( '//tr[contains(@data-testid,"CustomQuestions-row-custom")]//div/div', );
  readonly customQuestionNameLabel: Locator = this.page.locator( '[data-testid="CustomQuestions-table-body"] tr td div div', );
  readonly businessAccountQuestionsExpandQuestionIcon: Locator = this.page.locator( 'tr[data-testid="BusinessQuestions-row-purchase-order"] img[data-testid="Expand-Collapse-Icon"]', );
  readonly businessAccountQuestionsDetailsSection: Locator = this.page.getByTestId( "BusinessAccountQuestionsForm-purchase_order_number-container", );
  readonly businessAccountQuestionsActivateQuestionButton: Locator = this.page.getByTestId( "BusinessAccountQuestionsForm-purchase_order_number-activeQuestion-switcher", );
  readonly businessAccountQuestionsSaveUpdatesButton: Locator = this.page.getByTestId( "BusinessEmployeeQuestions-purchase_order_number-save-changes-button", );
  readonly businessAccountQuestionTitleLabel: Locator = this.page.getByTestId( "BusinessAccountQuestionsForm-typeOfQuestion-purchase_order_number-Form-Input", );
  readonly businessAccountQuestionMandatoryCheckbox: Locator = this.page.getByTestId( "BusinessAccountQuestionsForm-purchase_order_number-mandatoryQuestion-Form-Checkbox", );
  readonly businessQuestionStatusLabel: Locator = this.page.locator( 'tr[data-testid="BusinessQuestions-row-purchase-order"] td:nth-child(2) .flex > div', );
  readonly purchaseOrderNumberWhenRegisteringRadioButton: Locator = this.page.locator( 'label[for="WhenShouldWeAskForm-purchase_order_number-1"] button', );

  // UI components
  readonly toastNotificationSection = new ToastNotificationSectionComponent();
  readonly reviewChanges = new ReviewChangesModalComponent();

  // ######## UI actions/navigation ########
  /** Open IB Employees Questions page. */
  async open(): Promise<void> {
    console.log("Open IB Employees Questions page");
    await this.openPath(this.url);
    await this.validatePage();
  }
  /** Delete custom question. */
  async deleteCustomQuestion(): Promise<void> {
    console.log("Delete custom question");
    await this.clickExpandQuestionIcon();
    await this.clickDeleteQuestionButton();
    await this.clickConfirmDeleteQuestionButton();
    await this.validateToastMessage(
      IbStrings.YOUR_CHANGES_HAVE_BEEN_SAVED.name,
    );
  }
  /** Delete all custom questions. */
  async deleteAllCustomQuestions(): Promise<void> {
    console.log("Delete all custom questions");
    while (await this.customQuestionRowsLabel.count())
      await this.deleteCustomQuestion();
    console.log("All custom questions deleted");
  }
  /** Click create custom question button. */
  async clickCreateCustomQuestionButton(): Promise<void> {
    console.log("Click create custom question button");
    await this.createCustomQuestionButton.click();
  }
  /** Click mandatory question checkbox. */
  async clickMandatoryQuestionCheckbox(): Promise<void> {
    console.log("Click mandatory question checkbox");
    await this.mandatoryQuestionCheckbox.scrollIntoViewIfNeeded();
    await this.mandatoryQuestionCheckbox.click();
  }
  /** Click new question button. */
  async clickNewQuestionButton(): Promise<void> {
    console.log("Click new question button");
    await this.customQuestionNewQuestionImage.click();
  }
  /** Wait until the newly creatd custom question is being processed. */
  async waitForCustomQuestionToBeProcessed(): Promise<void> {
    console.log("Wait for custom question to be processed");
    await expect( this.customQuestionNameLabel, "Custom question should be processed", ).not.toHaveText(await IbStrings.CUSTOM_QUESTION_DEFAULT_NAME.name);
  }
  /** Click select preset answers button. */
  async clickSelectPresetAnswers(): Promise<void> {
    console.log("Click select preset answers button");
    await this.customQuestionSelectPresetAnswersRadioButton.click();
  }
  /** Click When Registering radio button. */
  async clickWhenRegisteringRadioButton(): Promise<void> {
    console.log("Click When Registering radio button");
    await this.customQuestionWhenRegisteringRadioButton.click();
  }
  /** Click Add Own Answers radio button. */
  async clickAddOwnAnswersRadioButton(): Promise<void> {
    console.log("Click Add Own Answers radio button");
    await this.customQuestionAddOwnAnswersRadioButton.click();
  }
  /** Click Save Updates button. */
  async clickSaveUpdatesButton(): Promise<void> {
    console.log("Click Save Updates button");
    await this.customQuestionSaveUpdatesButton.click();
  }
  /** Click Edit Save Updates button. */
  async clickEditSaveUpdatesButton(): Promise<void> {
    console.log("Click Edit Save Updates button");
    await this.customQuestionEditSaveUpdatesButton.click();
  }
  /** Click expand question icon. */
  async clickExpandQuestionIcon(): Promise<void> {
    console.log("Click expand question icon");
    await this.customQuestionExpandIcon.scrollIntoViewIfNeeded();
    await this.customQuestionExpandIcon.click();
  }
  /** Click Delete question button. */
  async clickDeleteQuestionButton(): Promise<void> {
    console.log("Click Delete question button");
    await this.customQuestionDeleteQuestionButton.click();
  }
  /** Click Confirm Delete question button. */
  async clickConfirmDeleteQuestionButton(): Promise<void> {
    console.log("Click Confirm Delete question button");
    await this.customQuestionConfirmDeleteQuestionButton.click();
  }
  /** Click Business Account Questions expand icon. */
  async clickBusinessQuestionsExpandIcon(): Promise<void> {
    console.log("Click Business Account Questions expand icon");
    await this.businessAccountQuestionsExpandQuestionIcon.click();
  }
  /** Click Business question mandatory checkbox button. */
  async clickBusinessQuestionMandatoryCheckboxButton(): Promise<void> {
    console.log("Click Business question mandatory checkbox button");
    await this.businessAccountQuestionMandatoryCheckbox.click();
  }
  /** Click Business Account Questions activate question button. */
  async clickBusinessActivateQuestionButton(): Promise<void> {
    console.log("Click Business Account Questions activate question button");
    await this.businessAccountQuestionsActivateQuestionButton.click();
  }
  /** Click Business Account Questions save button. */
  async clickBusinessAccountQuestionsSaveButton(): Promise<void> {
    console.log("Click Business Account Questions save button");
    await this.businessAccountQuestionsSaveUpdatesButton.click();
  }
  /** Click Purchase Order Number When Registering radio button. */
  async clickPurchaseOrderNumberWhenRegisteringButton(): Promise<void> {
    console.log("Click Purchase Order Number When Registering radio button");
    await this.purchaseOrderNumberWhenRegisteringRadioButton.click();
  }
  /** Create new custom questions based on count. */
  async createNewCustomQuestion({
    count = 1,
    expectedQuestionLabel,
    isMandatory = false,
  }: CreateNewCustomQuestionOptions): Promise<void> {
    console.log("Create new custom question");
    for (let index = 0; index < count; index += 1) {
      await this.clickCreateCustomQuestionButton();
      await this.clickNewQuestionButton();
      await this.customQuestionTitleInput.fill(
        expectedQuestionLabel[index] ?? "",
      );
      await this.customQuestionTitleInput.press("Tab");
      await this.customQuestionLabelInput.fill(
        expectedQuestionLabel[index] ?? "",
      );
      await this.customQuestionLabelInput.press("Tab");
      if (isMandatory) await this.clickMandatoryQuestionCheckbox();
      await this.clickWhenRegisteringRadioButton();
      await this.clickAddOwnAnswersRadioButton();
      await this.clickSaveUpdatesButton();
      await this.validateToastMessage(
        IbStrings.YOUR_CHANGES_HAVE_BEEN_SAVED.name,
      );
      await this.page.reload();
    }
  }
  /** Set value to business question. */
  async setValueToBusinessQuestion({
    questionTitle,
  }: SetValueToBusinessQuestionOptions): Promise<void> {
    console.log(`Set value to business question=${questionTitle}`);
    await this.businessAccountQuestionTitleLabel.fill(questionTitle);
    await this.businessAccountQuestionTitleLabel.press("Tab");
  }
  /** Edit business question. */
  async editBusinessQuestion({
    expectedQuestionTitle,
    isMandatory = false,
    isActivated = false,
  }: EditBusinessQuestionOptions): Promise<void> {
    console.log("Edit business question");
    await this.clickBusinessQuestionsExpandIcon();
    await this.validateBusinessQuestDetailsSectionDisplayed();
    await this.setValueToBusinessQuestion({
      questionTitle: expectedQuestionTitle,
    });
    if (
      ((await this.businessAccountQuestionMandatoryCheckbox.getAttribute(
        "aria-checked",
      )) ===
        "true") !==
      isMandatory
    )
      await this.clickBusinessQuestionMandatoryCheckboxButton();
    if (
      ((await this.businessAccountQuestionsActivateQuestionButton.getAttribute(
        "aria-checked",
      )) ===
        "true") !==
      isActivated
    )
      await this.clickBusinessActivateQuestionButton();
    await this.clickPurchaseOrderNumberWhenRegisteringButton();
    await this.clickBusinessAccountQuestionsSaveButton();
    await this.validateToastMessage(
      IbStrings.YOUR_CHANGES_HAVE_BEEN_SAVED.name,
    );
  }
  /** Expands or collapse custom questions. */
  async expandOrCollapseAllCustomQuestions({
    isExpanded = true,
  }: { isExpanded?: boolean } = {}): Promise<void> {
    console.log("Expand or collapse all custom questions");
    const count = await this.customQuestionsExpandButtons.count();
    for (let index = 0; index < count; index += 1) {
      const icon = this.customQuestionsExpandButtons.nth(index);
      await icon.scrollIntoViewIfNeeded();
      await icon.click();
    }
    await this.validateExpandedQuestionIsDisplayed(isExpanded);
  }

  // ######## UI validations ########
  /** Check we reached the current page by checking a specific element from the page. */
  async validatePage(): Promise<void> {
    console.log("Validate Employee Questions page");
    await this.validatePageMarker(
      this.employeeQuestionsTitleLabel,
      "Employee Questions",
    );
  }
  /** Validate Questions Details section. */
  async validateQuestionDetailsSection(): Promise<void> {
    console.log("Validate Question Details section");
    await expect( this.customQuestionTitleInput, "Question title input placeholder", ).toHaveAttribute("placeholder", /Question title|Fragentitel/);
    await expect( this.customQuestionTitleInput, "Question title input value", ).toHaveValue("");
    await expect( this.customQuestionTitleDescriptionLabel, "Question title description label", ).toHaveText(await IbStrings.CUSTOM_QUESTION_TITLE_DESCRIPTION.name);
    await expect( this.customQuestionDetailsTitleLabel, "Question details title label", ).toHaveText(await IbStrings.CUSTOM_QUESTION_DETAILS_TITLE.name);
    await expect( this.customQuestionLabelInput, "Question label input value", ).toHaveValue("");
    await expect( this.customQuestionLabelDescriptionLabel, "Question label description label", ).toHaveText(await IbStrings.CUSTOM_QUESTION_LABEL_DESCRIPTION.name);
    await expect( this.CustomQuestionNotificationMessageLabel, "Notification message label", ).toHaveText(await IbStrings.CUSTOM_QUESTION_NOTIFICATION_MESSAGE.name);
    await expect( this.customQuestionMandatoryQuestionLabel, "Mandatory question label", ).toHaveText(await IbStrings.CUSTOM_QUESTION_MANDATORY_LABEL.name);
    await expect( this.customQuestionWhenToAskTitleLabel, "When to ask title label", ).toHaveText( await IbStrings.CUSTOM_QUESTION_DETAILS_WHEN_TO_ASK_TITLE.name, );
    await expect( this.customQuestionWhenRegisteringLabel, "When registering label", ).toHaveText( await IbStrings.CUSTOM_QUESTION_DETAILS_WHEN_REGISTERING_LABEL.name, );
    await expect( this.customQuestionWhenBookingLabel, "When booking label", ).toHaveText( await IbStrings.CUSTOM_QUESTION_DETAILS_WHEN_BOOKING_LABEL.name, );
    await expect( this.customQuestionHowToAnswerLabel, "How to answer label", ).toHaveText( await IbStrings.CUSTOM_QUESTION_DETAILS_HOW_TO_ANSWER_TITLE.name, );
    await expect( this.customQuestionSelectPresetAnswersLabel, "Select pre-set answers label", ).toHaveText( await IbStrings.CUSTOM_QUESTION_DETAILS_SELECT_PRESET_ANSWERS.name, );
    await expect( this.customQuestionAddOwnAnswersLabel, "Add own answers label", ).toHaveText( await IbStrings.CUSTOM_QUESTION_DETAILS_SELECT_OWN_ANSWERS.name, );
  }
  /** Validate Preset answers section. */
  async validatePresetAnswersSection(): Promise<void> {
    console.log("Validate Preset answers section");
    await expect( this.customQuestionPresetAnswerInput, "Preset answer input placeholder", ).toHaveAttribute( "placeholder", await IbStrings.CUSTOM_QUESTION_DETAILS_PRESET_ANSWER_PLACEHOLDER.name, );
    await expect( this.customQuestionPresetAnswerInput, "Preset answer input value", ).toHaveValue("");
    await expect( this.createCustomQuestionAddAnswerButton, "Add answer button", ).toBeVisible();
  }
  /** Validate expanded question section is not displayed. */
  async validateExpandedQuestionIsDisplayed(isDisplayed = true): Promise<void> {
    console.log("Validate expanded question display");
    if (isDisplayed)
      await expect( this.anyCustomQuestionExpandedSection, "Expanded question section", ).toBeVisible();
    else
      await expect( this.anyCustomQuestionExpandedSection, "Expanded question section", ).not.toBeVisible();
  }
  /** Validate business question details section is displayed. */
  async validateBusinessQuestDetailsSectionDisplayed(): Promise<void> {
    console.log("Validate business question details section is displayed");
    await expect( this.businessAccountQuestionsDetailsSection, "Expanded business question section", ).toBeVisible();
  }
  /** Validate toast message. */
  async validateToastMessage(
    expectedMessage: string | Promise<string>,
  ): Promise<void> {
    console.log("Validate toast message");
    await this.toastNotificationSection.validateToastNotification({
      message: expectedMessage,
    });
  }
  /** Validate expanded questions are displayed. */
  async validateAllExpandedQuestionsDisplayed(
    questionsNumber: number,
  ): Promise<void> {
    console.log("Validate all expanded questions are displayed");
    await expect( this.customQuestionContainers, "Number of expanded questions", ).toHaveCount(questionsNumber);
  }
  /** Validate Question title input. */
  async validateQuestionTitleInput(
    questionTitle: string,
    isValid = true,
  ): Promise<void> {
    console.log("Validate Question title input");
    await expect( this.customQuestionTitleInput, "Question title input value", ).toHaveValue(questionTitle);
    if (!questionTitle || !isValid) {
      await expect( this.customQuestionTitleErrorTooltip, "Question title error tooltip", ).toHaveText( !isValid ? await IbStrings.CUSTOM_QUESTION_TITLE_ERROR_TOOLTIP.name : await IbStrings.CUSTOM_QUESTION_NO_TITLE_ERROR_TOOLTIP.name, );
    } else
      await expect( this.customQuestionTitleErrorTooltip, "Question title error tooltip", ).not.toBeVisible();
  }
  /** Validate Question label input. */
  async validateCustomQuestion({
    label,
    mandatory,
  }: ValidateCustomQuestionOptions): Promise<void> {
    console.log("Validate custom question");
    await expect( this.mandatoryQuestionCheckbox, "Mandatory status", ).toHaveAttribute("aria-checked", String(mandatory));
    await expect(this.customQuestionLabelInput, "Question label").toHaveValue( label, );
  }
  /** Validates Employee Questions page URL. */
  async validateEmployeeQuestionsPageUrl(): Promise<void> {
    console.log("Validate Employee Questions page URL");
    this.validateUrl(this.url);
  }
  /** Validate the status of a business question. */
  async validateBusinessQuestionStatus({
    expectedStatus,
  }: ValidateBusinessQuestionStatusOptions): Promise<void> {
    console.log("Validate business question status");
    await expect( this.businessQuestionStatusLabel, "Business question status", ).toHaveText(expectedStatus);
  }
  /** Valdate that all custom questions are deleted. */
  async validateAllCustomQuestionsDeleted(): Promise<void> {
    console.log("Validate all custom questions are deleted");
    await expect( this.customQuestionRowsLabel, "Custom question row count", ).toHaveCount(0);
  }
}
