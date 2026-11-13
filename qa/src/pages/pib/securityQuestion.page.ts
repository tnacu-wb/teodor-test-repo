import { expect, type Locator } from "@playwright/test";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { Strings } from "../../test-data/strings";
import { BasePibPage } from "./basePib.page";

/**
 * Security question page class
 */
export class SecurityQuestionPage extends BasePibPage {
  // ######## properties ########
  readonly url = "business-pay/register/";

  // ######## UI elements/properties ########
  readonly pageTitleLabel: Locator = this.page.getByTestId("wizard-title");
  readonly backImg: Locator = this.page.getByTestId("wizard-back-icon");
  readonly pageDescriptionLabels: Locator = this.page.locator("p.text-neutral-900");
  readonly questionTitleLabel: Locator = this.page.getByTestId( "RegisterIbPage-Question-Label-1", );
  readonly questionDescriptionLabel: Locator = this.page.locator("p.mb-6");
  readonly questionInput: Locator = this.page.getByTestId( "question1-Form-Input", );
  readonly questionErrorLabel: Locator = this.page.locator("div.text-sm");
  readonly continueButton: Locator = this.page.getByTestId( "RegisterIbPage-Button", );
  readonly needHelpLabel: Locator = this.page.locator("p.mt-6");
  readonly iconImgs: Locator = this.page.locator("div.flex a.flex img");
  readonly iconLabels: Locator = this.page.locator("div.flex a.flex span");

  // ######## UI actions/navigation ########
  /**
   * Open IB Security question page
   * @param data data object
   * @param data.registrationCode registration code to be set
   */
  async open({
    registrationCode = "",
  }: { registrationCode?: string } = {}): Promise<void> {
    console.log("Open IB Security question page");
    await this.openPath(`${this.url}${registrationCode}`);
    await this.validatePage();
  }

  /**
   * Set question pin
   * @param data data object
   * @param data.questionPin question pin to be set
   */
  async setQuestionPinInput({
    questionPin = "",
  }: { questionPin?: string } = {}): Promise<void> {
    console.log(`Setting question pin: ${questionPin}`);
    await this.questionInput.fill(questionPin);
  }

  /**
   * Click on Continue button
   */
  async clickOnContinueButton(): Promise<void> {
    console.log("Click on continue button");
    await this.continueButton.click();
  }

  // ######## UI validations ########
  /**
   * Check we reached the current page by checking a specific element from the page
   */
  async validatePage(): Promise<void> {
    console.log("Validate Security question page");
    await this.validatePageMarker(this.pageTitleLabel, "Security question");
    await expect( this.pageTitleLabel, "Security question page title", ).toHaveText(await IbStrings.SECURITY_QUESTION_TITLE.name);
  }

  /**
   * Validate Security question page elements
   */
  async validateSecurityQuestionPageElements(): Promise<void> {
    console.log("Validate Security Question page elements");
    await expect(this.pageTitleLabel, "Security Question").toHaveText( await IbStrings.SECURITY_QUESTION_TITLE.name, );
    await expect(this.backImg, "Security Question back image").toBeVisible();
    const descriptionLabels = [
      await IbStrings.SECURITY_QUESTION_DESCRIPTION_1.name,
      await IbStrings.SECURITY_QUESTION_DESCRIPTION_2.name,
    ];
    for (
      let index = 0;
      index < (await this.pageDescriptionLabels.count());
      index += 1
    )
      await expect( this.pageDescriptionLabels.nth(index), `Security Question Description ${index + 1}`, ).toHaveText(descriptionLabels[index]);
    await expect(this.questionTitleLabel, "Security Question title").toHaveText( await Strings.SECURITY_QUESTION_QUESTION_TITLE.name, );
    await expect( this.questionDescriptionLabel, "Security Question description", ).toHaveText(await IbStrings.SECURITY_QUESTION_QUESTION_DESCRIPTION.name);
    await expect( this.questionInput, "Security Question input placeholder", ).toHaveAttribute( "placeholder", await IbStrings.SECURITY_QUESTION_INPUT.name, );
    await expect( this.questionInput, "Security Question input value", ).toHaveValue("");
    await expect( this.continueButton, "Security Question Continue button", ).toHaveText(await IbStrings.SECURITY_QUESTION_CONTINUE.name);
    await expect(this.needHelpLabel, "Need Help title").toHaveText( await IbStrings.SECURITY_QUESTION_NEED_HELP.name, );
    const iconLabels = [
      await IbStrings.SECURITY_QUESTION_CONTACT.name,
      await IbStrings.SECURITY_QUESTION_FAQS.name,
    ];
    for (let index = 0; index < (await this.iconImgs.count()); index += 1) {
      await expect( this.iconImgs.nth(index), `Security Question icon image ${index + 1}`, ).toBeVisible();
      await expect( this.iconLabels.nth(index), `Security Question icon label ${index + 1}`, ).toHaveText(iconLabels[index]);
    }
  }

  /**
   * Validate security question pin error
   * @param data data object
   * @param data.isDisplayed whether the error message should be displayed
   */
  async validateSecurityQuestionPinError({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log("Validate security question pin error");
    if (isDisplayed)
      await expect( this.questionErrorLabel, "Security Question pin error label", ).toHaveText(await IbStrings.SECURITY_QUESTION_ERROR.name);
    else
      await expect( this.questionErrorLabel, "Security Question pin error label", ).not.toBeVisible();
  }
}
