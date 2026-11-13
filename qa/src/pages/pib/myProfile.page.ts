import { expect, type Locator } from "@playwright/test";
import {
  ChangePasswordSectionComponent,
  CompanyAddressSectionComponent,
  DeleteCardSectionComponent,
  EditRegistrationQuestionsSectionComponent,
  HeaderSectionComponent,
  MealsAndExtrasPreferencesSectionComponent,
  MyProfilePaymentTypeSectionComponent,
  PersonalDetailsSectionComponent,
  RegistrationQuestionsFromComponent,
  ReviewChangesModalComponent,
  RoomPreferencesSectionComponent,
  ToastNotificationSectionComponent,
} from "../../components/pib";
import { type AnsweredQuestions } from "../../api/response/answeredQuestions";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { BasePibPage } from "./basePib.page";

interface CompanyAddress {
  addressLine1?: string;
  addressLine2?: string;
  addressLine3?: string;
  addressLine4?: string;
  addressLine5?: string;
  postCode?: string;
}
interface EmployeeDetails {
  title: string;
  firstName: string;
  lastName: string;
  email: string;
  telephone: string;
  address: CompanyAddress;
  mobilePhone?: string;
  alternatePhone?: string;
}

/** Page object for My Profile page */
export class MyProfilePage extends BasePibPage {
  readonly url = "profile";
  // ######## UI elements/properties ########
  readonly myProfileContainer: Locator = this.page.getByTestId( "ProfilePage-container", );
  readonly companyRegistrationQuestionsContainerTitleLabel: Locator = this.page.getByTestId("ProfilePage-Company-Registration-Questions-Title");
  readonly companyRegistrationQuestionsContent: Locator = this.page.getByTestId( "ProfilePage-Company-Registration-Questions-Content", );
  readonly companyRegistrationQuestionsLabels: Locator = this.page.getByTestId( "ProfilePage-Company-Registration-Question-Label", );
  readonly editRegistrationQuestionsButton: Locator = this.page.getByTestId( "ProfilePage-Company-Registration-Questions-Button", );
  readonly changePasswordButton: Locator = this.page.getByTestId( "ProfilePage-Change-Password-Button", );
  readonly mealsAndExtrasButton: Locator = this.page.getByTestId( "ProfilePage-Meals-And-Extras-Edit-Button", );
  readonly contactPreferencesSectionTitleLabel: Locator = this.page.locator( 'div[data-testid="EditContactPreferencesSection"] h4', );
  readonly editContactPreferencesButton: Locator = this.page.getByTestId( "EditContactPreferencesSection-EditButton", );
  readonly editProfileButton: Locator = this.page.getByTestId( "ProfilePage-Your-Profile-Edit-Profile-Button", );
  readonly saveChangesButton: Locator = this.page.getByTestId( "ProfilePage-Your-Profile-save-changes", );
  readonly yourProfileDiv: Locator = this.page.getByTestId( "UserDetails-container", );
  readonly yourProfileTitleLabel: Locator = this.page.getByTestId( "ProfilePage-Your-Profile-Title", );
  readonly myProfileEmailAddressLabel: Locator = this.page.getByTestId( "UserDetails-emailAddress", );
  readonly editRegistrationQuestionsSection =
    new EditRegistrationQuestionsSectionComponent();
  readonly registrationQuestionsForm = new RegistrationQuestionsFromComponent();
  readonly roomPreferences = new RoomPreferencesSectionComponent();
  readonly paymentTypeSection = new MyProfilePaymentTypeSectionComponent();
  readonly deleteCardSection = new DeleteCardSectionComponent();
  readonly changePasswordSection = new ChangePasswordSectionComponent();
  readonly mealsAndExtras = new MealsAndExtrasPreferencesSectionComponent();
  readonly personalDetails = new PersonalDetailsSectionComponent();
  readonly companyAddress = new CompanyAddressSectionComponent();
  readonly reviewChangesModal = new ReviewChangesModalComponent();
  readonly headerSection = new HeaderSectionComponent();
  getRegistrationQuestionBasedOnIndex(index: number): Locator {
    return this.companyRegistrationQuestionsContent.locator(
      `div:nth-child(${index + 1}) p:nth-child(1)`,
    );
  }
  getRegistrationAnswerBasedOnIndex(index: number): Locator {
    return this.companyRegistrationQuestionsContent.locator(
      `div:nth-child(${index + 1}) p:nth-child(2)`,
    );
  }
  // ######## UI actions/navigation ########
  /** Open IB homepage. */
  async open(): Promise<void> {
    console.log("Open IB homepage");
    await this.openPath(this.url);
    await this.validatePage();
  }
  /** Click Edit Registration Questions button. */
  async clickEditRegistrationQuestionsButton(): Promise<void> {
    console.log("Click Edit Registration Questions button");
    await this.editRegistrationQuestionsButton.scrollIntoViewIfNeeded();
    await this.editRegistrationQuestionsButton.click();
    await expect( this.registrationQuestionsForm.cardRegistrationQuestionsTitleLabel, "Registration questions title label", ).toBeVisible();
  }
  /** Click Change Password button. */
  async clickChangePasswordButton(): Promise<void> {
    console.log("Click Change Password button");
    await this.changePasswordButton.scrollIntoViewIfNeeded();
    await this.changePasswordButton.click();
  }
  /** Click Edit Profile button. */
  async clickEditProfileButton(): Promise<void> {
    console.log("Click Edit Profile button");
    await this.editProfileButton.scrollIntoViewIfNeeded();
    await this.editProfileButton.click();
  }
  /** Click Meals and Extras button. */
  async clickMealAndExtrasButton(): Promise<void> {
    console.log("Click Meals and Extras button");
    await this.mealsAndExtras.mealsAndExtrasContainerTitleLabel.scrollIntoViewIfNeeded();
    await this.mealsAndExtrasButton.click();
    await expect( this.mealsAndExtras.mealsHeadingLabel, "Meals heading", ).toBeVisible();
  }
  /** Click edit contact preferences button. */
  async clickEditContactPreferencesButton(): Promise<void> {
    console.log("Click Edit Contact preferences button");
    await this.editContactPreferencesButton.scrollIntoViewIfNeeded();
    await this.editContactPreferencesButton.click();
  }
  /** Click edit payment type button. */
  async clickSaveChangesButton(): Promise<void> {
    console.log("Click Save changes button");
    await this.saveChangesButton.scrollIntoViewIfNeeded();
    await this.saveChangesButton.click();
  }
  /** Edit employee details. */
  async editEmployeeDetails({
    userDetails,
  }: {
    userDetails: EmployeeDetails;
  }): Promise<void> {
    console.log("Edit employee details");
    await this.personalDetails.setEmployeeDetails({
      title: userDetails.title,
      firstName: userDetails.firstName,
      lastName: userDetails.lastName,
      mobilePhone: userDetails.mobilePhone,
      alternatePhone: userDetails.alternatePhone,
    });
  }
  // ######## UI validations ########
  /** Check we reached the current page by checking a specific element from the page. */
  async validatePage(): Promise<void> {
    console.log("Validate My Profile page");
    await this.validatePageMarker(this.myProfileContainer, "My Profile");
  }
  /** Validate registration questions and answers are displayed based on graphqlGetCompanyRegistrationQuestionsAndAnswers response. */
  async validateRegistrationQuestionsAndAnswersSection(
    registrationQuestionsAndAnswers: AnsweredQuestions,
  ): Promise<void> {
    console.log("Validate registration questions and answers");
    await this.companyRegistrationQuestionsContainerTitleLabel.scrollIntoViewIfNeeded();
    await expect( this.companyRegistrationQuestionsContainerTitleLabel, "Registration questions section title", ).toHaveText(await IbStrings.REGISTRATION_QUESTIONS_MY_PROFILE_IB.name);
    await expect( this.companyRegistrationQuestionsContent, "Registration questions content", ).toBeVisible();
    const expected =
      await registrationQuestionsAndAnswers.getRegistrationQuestionsAndAnswers();
    await expect( this.companyRegistrationQuestionsLabels, "Number of registration questions", ).toHaveCount(expected.size);
    for (const [index, [question, answer]] of Array.from(
      expected.entries(),
    ).entries()) {
      await expect( this.getRegistrationQuestionBasedOnIndex(index), "Registration question label", ).toHaveText(question);
      if (answer)
        await expect( this.getRegistrationAnswerBasedOnIndex(index), "Registration answer label", ).toHaveText(answer);
      else
        await expect( this.getRegistrationAnswerBasedOnIndex(index), "Registration answer label", ).not.toBeVisible();
    }
    await expect( this.editRegistrationQuestionsButton, "Edit registration questions button", ).toHaveText(await IbStrings.EDIT_REGISTRATION_QUESTIONS_MY_PROFILE.name);
  }
  /** Validate registration questions answers are saved successfully. */
  async validateSavedRegistrationQuestionsAnswers(
    userAnswers: string[],
  ): Promise<void> {
    console.log(
      "Validate registration questions answers are saved successfully",
    );
    for (const [index, answer] of userAnswers.entries()) {
      await this.companyRegistrationQuestionsContainerTitleLabel.scrollIntoViewIfNeeded();
      await expect( this.getRegistrationAnswerBasedOnIndex(index), "Registration answer label", ).toHaveText(answer);
    }
  }
  /** Validate Meals and Extras Preferences section is displayed. */
  async validateMealsAndExtrasSection(): Promise<void> {
    console.log("Validate Meals and Extras Preferences section");
    await this.mealsAndExtras.mealsAndExtrasContainerTitleLabel.scrollIntoViewIfNeeded();
    await expect( this.mealsAndExtras.mealsAndExtrasContainerTitleLabel, "Meals and extras preferences section heading", ).toHaveText(await IbStrings.MEALS_AND_EXTRAS_MY_PROFILE_IB.name);
    await expect( this.mealsAndExtras.mealsAndExtrasDescriptionLabel, "Meals and extras preferences section description", ).toHaveText( await IbStrings.MEALS_AND_EXTRAS_DESCRIPTION_MY_PROFILE_IB.name, );
    await expect( this.mealsAndExtrasButton, "Meals and extras edit button", ).toHaveText( await IbStrings.MEALS_AND_EXTRAS_EDIT_BUTTON_MY_PROFILE_IB.name, );
  }
  /** Validate success toast. */
  async validateSuccessToast({
    successMessage,
  }: {
    successMessage: string;
  }): Promise<void> {
    console.log("Validate success toast");
    await this.toastNotificationSection.validateToastNotification({
      message: successMessage,
    });
    await expect( this.toastNotificationSection.toastLabel, "Success toast should close", ).not.toBeVisible();
  }
  /** Validate contact centre section from my profile page. */
  async validateContactCentreSection(): Promise<void> {
    console.log("Validate contact centre section");
    await expect( this.contactPreferencesSectionTitleLabel, "Contact and permissions centre section title", ).toHaveText(await IbStrings.CONTACT_AND_PERMISSIONS_CENTRE.name);
    await expect( this.editContactPreferencesButton, "Edit contact preferences button", ).toHaveText(await IbStrings.EDIT_CONTACT_PREFERENCES.name);
  }
  /** Validate your profile section. */
  async validateYourProfileSection({
    employeeDetails,
  }: {
    employeeDetails: EmployeeDetails;
  }): Promise<void> {
    console.log("Validate your profile section");
    await expect(this.yourProfileTitleLabel, "Your profile title").toHaveText( await IbStrings.YOUR_PROFILE_TITLE.name, );
    await expect(this.yourProfileDiv, "Profile labels").toHaveText( `${employeeDetails.title} ${employeeDetails.firstName} ${employeeDetails.lastName}\n${employeeDetails.email}\n${employeeDetails.telephone}`.trim(), );
    if (employeeDetails.address.addressLine1)
      await expect( this.companyAddress.companyAddress1Label, "Company address line 1", ).toHaveText(employeeDetails.address.addressLine1);
    if (employeeDetails.address.addressLine2)
      await expect( this.companyAddress.companyAddress2Label, "Company address line 2", ).toHaveText(employeeDetails.address.addressLine2);
    if (employeeDetails.address.addressLine3)
      await expect( this.companyAddress.companyAddress3Label, "Company address line 3", ).toHaveText(employeeDetails.address.addressLine3);
    if (employeeDetails.address.addressLine4)
      await expect( this.companyAddress.companyAddress4Label, "Company address line 4", ).toHaveText(employeeDetails.address.addressLine4);
    if (employeeDetails.address.addressLine5)
      await expect( this.companyAddress.companyAddress5Label, "Company address line 5", ).toHaveText(employeeDetails.address.addressLine5);
    if (employeeDetails.address.postCode)
      await expect( this.companyAddress.companyPostCodeLabel, "Company postcode", ).toHaveText(employeeDetails.address.postCode);
  }
  /** Validate email address is displayed. */
  async validateEmailAddress(emailAddress: string): Promise<void> {
    console.log("Validate email address is displayed");
    await this.myProfileEmailAddressLabel.scrollIntoViewIfNeeded();
    await expect( this.myProfileEmailAddressLabel, "My profile email address", ).toHaveText(emailAddress);
  }
}
