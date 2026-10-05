import { expect, type Locator, type Page } from "@playwright/test";
import { type AnsweredQuestions } from "../../../api/response/answeredQuestions";
import { IbStrings } from "../../../test-data/pib/ibStrings";
import { RegistrationQuestionsFromComponent } from "../registrationQuestionsFrom.component";
import { ReviewChangesModalComponent } from "../reviewChangesModal.component";

/**
 * InnBusiness registration Questions Section from Add/Edit employee and My Profile
 */
export class EditRegistrationQuestionsSectionComponent {
  private readonly page: Page = global.page;
  // ######## properties ########
  // ######## UI elements/properties ########
  readonly editRegistrationQuestionsSectionTitleLabel: Locator = this.page.locator('h4[data-testid="Registration-Questions-Heading"]');
  readonly saveButton: Locator = this.page.locator( '//button[@data-testid="ProfilePage-Company-Registration-Questions-Save-Button"]', );
  readonly cancelChangesButton: Locator = this.page.locator( '//button[@data-testid="ProfilePage-Company-Registration-Questions-Cancel-Button"]', );
  readonly registrationQuestionsForm = new RegistrationQuestionsFromComponent();
  readonly reviewChangesModal = new ReviewChangesModalComponent();
  // ######## UI actions/navigation ########
  /** Click cancel changes button. */
  async clickCancelChangesButton(): Promise<void> {
    console.log("Click Cancel Changes button");
    await this.cancelChangesButton.scrollIntoViewIfNeeded();
    await this.cancelChangesButton.click();
  }
  /** Click save button. */
  async clickSaveButton(): Promise<void> {
    console.log("Click Save button");
    await this.saveButton.scrollIntoViewIfNeeded();
    await this.saveButton.click();
  }
  // ######## UI validations ########
  /** Validate edit registration questions section. */
  async validateEditRegistrationQuestionsSection(
    registrationQuestionsAndAnswers: AnsweredQuestions,
  ): Promise<void> {
    console.log("Validate edit registration questions section");
    await expect( this.registrationQuestionsForm.cardRegistrationQuestionsTitleLabel, "Edit Registration Questions title", ).toHaveText(await IbStrings.EDIT_REGISTRATION_QUESTIONS_MY_PROFILE.name);
    await this.registrationQuestionsForm.validateRegistrationQuestionsAndAnswersForm(
      registrationQuestionsAndAnswers,
    );
    await expect(this.cancelChangesButton, "Cancel Changes button").toHaveText( await IbStrings.REGISTRATION_QUESTIONS_CANCEL_CHANGES_MY_PROFILE_IB.name, );
    await expect(this.saveButton, "Save Changes button").toHaveText( await IbStrings.REGISTRATION_QUESTIONS_SAVE_MY_PROFILE_IB.name, );
  }
}
