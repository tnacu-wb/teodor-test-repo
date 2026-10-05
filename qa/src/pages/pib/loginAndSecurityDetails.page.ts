import { expect, type Locator } from "@playwright/test";
import { MemorableWordSectionComponent } from "../../components/pib";
import { Locales } from "../../test-data/locales";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { BasePibPage } from "./basePib.page";

/**
 * Login and security details page class
 */
export class LoginAndSecurityDetailsPage extends BasePibPage {
  // ######## UI elements/properties ########
  readonly pageTitleLabel: Locator = this.page.getByTestId("wizard-title");
  readonly firstParagraphDescriptionLabel: Locator = this.page.locator( '//h1[@data-testid="wizard-title"]/parent::div/following-sibling::p[1]', );
  readonly secondParagraphDescriptionLabel: Locator = this.page.locator( '//h1[@data-testid="wizard-title"]/parent::div/following-sibling::p[2]', );
  readonly fullNameLabel: Locator = this.page.getByTestId( "RegisterIbPage-FullName-Label", );
  readonly fullNameValueLabel: Locator = this.page.getByTestId( "RegisterIbPage-FullName-Value", );
  readonly emailLabel: Locator = this.page.getByTestId( "RegisterIbPage-Email-Label", );
  readonly emailValueLabel: Locator = this.page.getByTestId( "RegisterIbPage-Email-Value", );
  readonly landlineNumberLabel: Locator = this.page.getByTestId( "RegisterIbPage-LandlineNumber-Label", );
  readonly mobileNumberLabel: Locator = this.page.getByTestId( "RegisterIbPage-MobileNumber-Label", );
  readonly createMemorableWordLabel: Locator = this.page.locator( '//div[@data-testid="MemorableWordInput-form"]/preceding-sibling::h2', );
  readonly memorableWordDescriptionLabel: Locator = this.page.locator( '//div[@data-testid="MemorableWordInput-form"]/preceding-sibling::p', );
  readonly submitRegistrationButton: Locator = this.page.getByTestId( "RegisterIbPage-Button", );
  readonly needHelpLabel: Locator = this.page.locator( '//form[@id="RegisterIbPage-Form"]/following-sibling::div/p', );
  readonly faqsLabel: Locator = this.page.locator( '//form[@id="RegisterIbPage-Form"]/following-sibling::div//a/span', );
  readonly faqsLink: Locator = this.page.locator( '//form[@id="RegisterIbPage-Form"]/following-sibling::div//a', );
  readonly faqsIcon: Locator = this.page.locator( '//form[@id="RegisterIbPage-Form"]/following-sibling::div//a/img', );

  // UI components
  readonly memorableWordSection = new MemorableWordSectionComponent();

  // ######## UI actions/navigation ########
  /**
   * Click on submit registration button
   */
  async clickOnSubmitRegistrationButton(): Promise<void> {
    console.log("Click on submit registration button");
    await this.submitRegistrationButton.click();
    await expect( this.submitRegistrationButton, "Submit registration button after click", ).not.toBeVisible();
  }

  // ######## UI validations ########
  /**
   * Check we reached the current page by checking a specific element from the page
   */
  async validatePage(): Promise<void> {
    console.log("Validate Login and security details page");
    await this.validatePageMarker(
      this.pageTitleLabel,
      "Login and security details",
    );
  }

  /**
   * Validate page elements
   * @param data data object
   * @param data.userData user data
   */
  async validateLoginAndSecurityDetailsPageElements({
    userData,
  }: {
    userData: {
      title: string;
      firstName: string;
      lastName: string;
      emailAddress: string;
    };
  }): Promise<void> {
    console.log("Validate page elements");
    await expect( this.pageTitleLabel, "Login and security details page title", ).toHaveText(await IbStrings.PAY_APP_LOGIN_SECURITY_TITLE.name);
    await expect( this.firstParagraphDescriptionLabel, "First paragraph description", ).toHaveText(await IbStrings.PAY_APP_LOGIN_SECURITY_FIRST_PARAGRAPH.name);
    await expect( this.secondParagraphDescriptionLabel, "Second paragraph description", ).toHaveText(await IbStrings.PAY_APP_LOGIN_SECURITY_SECOND_PARAGRAPH.name);
    await expect(this.fullNameLabel, "Full name").toHaveText( await IbStrings.PAY_APP_LOGIN_SECURITY_FULL_NAME.name, );
    await expect(this.fullNameValueLabel, "Full name value").toHaveText( `${userData.title} ${userData.firstName} ${userData.lastName}`, );
    await expect(this.emailLabel, "Email").toHaveText( await IbStrings.PAY_APP_LOGIN_SECURITY_EMAIL.name, );
    await expect(this.emailValueLabel, "Email value").toHaveText( userData.emailAddress, );
    await expect(this.landlineNumberLabel, "Landline number").toHaveText( await IbStrings.PAY_APP_LOGIN_SECURITY_LANDLINE_NUMBER.name, );
    await expect(this.mobileNumberLabel, "Mobile number").toHaveText( await IbStrings.PAY_APP_LOGIN_SECURITY_MOBILE_NUMBER.name, );
    await expect( this.createMemorableWordLabel, "Create memorable word", ).toHaveText(await IbStrings.CREATE_A_MEMORABLE_WORD.name);
    await expect( this.memorableWordDescriptionLabel, "Memorable word description", ).toHaveText(await IbStrings.THIS_CAN_AUTHORISE_CARD_PAYMENTS.name);
    await expect( this.memorableWordSection.memorableWordListTitleLabel, "Memorable word instructions title", ).toBeVisible();
    await this.memorableWordSection.validateMemorableWordElements();
    await this.memorableWordSection.validateMemorableWordInputValueAndPlaceholder(
      { placeholder: await IbStrings.MEMORABLE_WORD.name, value: "" },
    );
    await expect( this.submitRegistrationButton, "Submit registration button", ).toHaveText( await IbStrings.PAY_APP_LOGIN_SECURITY_SUBMIT_REGISTRATION.name, );
    await expect(this.needHelpLabel, "Need help").toHaveText( await IbStrings.PAY_APP_LOGIN_SECURITY_NEED_HELP.name, );
    await expect(this.faqsLabel, "FAQs").toHaveText( await IbStrings.PAY_APP_LOGIN_SECURITY_FAQ.name, );
    await expect( this.faqsLink, "FAQs link should point to the correct URL", ).toHaveAttribute("href", `/${Locales.getIbUrlName()}/faqs`);
    await expect(this.faqsIcon, "FAQs icon is not as expected").toHaveAttribute( "src", expect.stringContaining(await IbStrings.ICON_FILE_PURPLE.name), );
  }
}
