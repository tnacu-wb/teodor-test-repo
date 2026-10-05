import { expect, type Locator, type Page } from "@playwright/test";
import { Strings } from "@test-data/strings";

interface EmployeeDetails {
  title: string;
  firstName: string;
  lastName: string;
  email: string;
}

/**
 * The accompanying guest details section from Ancillaries BB page containing the UI elements, custom actions and validations for BB
 */
export class AccompanyingGuestDetailsSectionComponent {
  private readonly page: Page = global.page;

  // ######## properties ########
  static readonly ADULT_MEAL_CONTAINER_TESTID =
    "GuestDetailsPageBB-Meals-Adults-MealItem-Wrapper";
  static readonly TITLE_DROPDOWN_TESTID =
    "DropdownComp-AccompanyingGuestDetailsBBContainer-Form-TitleDropdown-entireList";

  // ######## UI elements/properties ########
  readonly accompanyingGuestDetailsTitleLabel: Locator = this.page.getByTestId( "AccompanyingGuestDetailsBBContainer-AccompanyingGDTitle", );
  readonly dynamicEmployeeSuggestionDropdown: Locator = this.page.getByTestId( "AccompanyingGuestDetailsBBContainer-Form-SuggestionCard", );
  readonly successfulSearchIcon: Locator = this.page.getByTestId("inputIconSuccess");
  readonly unsuccessfulSearchIcon: Locator = this.page.getByTestId("inputIconError");
  readonly titleDropdownLabelsList: Locator = this.page.locator( `[data-testid="${AccompanyingGuestDetailsSectionComponent.TITLE_DROPDOWN_TESTID}"]`, );
  readonly accompanyingGuestDetailsInput: Locator = this.page.getByTestId( "input-searchCriteria", );
  readonly firstNameErrorMessageLabel: Locator = this.page.getByTestId( "input-bbAccompanyingGuestDetails[0][firstName]-FormErrorMessage", );
  readonly leadGuestDetailsLabel: Locator = this.page.getByTestId( "GuestDetailsBBContainer-Form-LeadGuestDetails", );
  readonly firstNameInput: Locator = this.page.getByTestId( "input-bbAccompanyingGuestDetails[0][firstName]", );
  readonly lastNameInput: Locator = this.page.getByTestId( "input-bbAccompanyingGuestDetails[0][lastName]", );
  readonly emailInput: Locator = this.page.getByTestId( "input-bbAccompanyingGuestDetails[0][emailAddress]", );

  /** Get an employee suggestion containing the supplied email. */
  getDynamicEmployeeLabelByEmail(employeeEmail: string): Locator {
    return this.dynamicEmployeeSuggestionDropdown.locator("strong", {
      hasText: employeeEmail,
    });
  }
  /** Get the complete employee suggestion row containing the supplied email. */
  getDynamicSuggestionEmployeeLabelByEmail(employeeEmail: string): Locator {
    return this.getDynamicEmployeeLabelByEmail(employeeEmail).locator("..");
  }
  /** Get a dynamic accompanying guest container by index. */
  getDynamicAccompanyingGuestDetailsContainerByIndex(index: number): Locator {
    return this.page.getByTestId(
      `AccompanyingGuestDetailsBBContainer-Form-DynamicGuestLead-${index}`,
    );
  }
  /** Get the dynamic accompanying guest input by index. */
  getDynamicAccompanyingGuestDetailsInputFieldByIndex(index: number): Locator {
    return this.getDynamicAccompanyingGuestDetailsContainerByIndex(
      index,
    ).locator("input");
  }
  /** Get the enter-details-manually link by index. */
  getEnterAccompanyingGuestDetailsManuallyLinkByIndex(index: number): Locator {
    return this.getDynamicAccompanyingGuestDetailsContainerByIndex(
      index,
    ).locator("p");
  }
  /** Get the manual accompanying guest container by index. */
  getAccompanyingGuestDetailContainerByIndex(index: number): Locator {
    return this.page.getByTestId(
      `AccompanyingGuestDetailsBBContainer-Form-ManualGuestLead-${index}`,
    );
  }
  /** Get the title dropdown by container index. */
  getTitleDropdownByContainerIndex(index: number): Locator {
    return this.getAccompanyingGuestDetailContainerByIndex(index).getByTestId(
      "DropdownComp-AccompanyingGuestDetailsBBContainer-Form-TitleDropdown-menuButton",
    );
  }
  /** Get the title success icon by container index. */
  getTitleSuccessIconByContainerIndex(index: number): Locator {
    return this.getAccompanyingGuestDetailContainerByIndex(index).getByTestId(
      "inputIconSuccess",
    );
  }
  /** Get a title option from the open dropdown. */
  getTitleDropdownValue(titleValue: string): Locator {
    return this.page.locator(
      `[data-testid="${AccompanyingGuestDetailsSectionComponent.TITLE_DROPDOWN_TESTID}"] button[value="${titleValue}"]`,
    );
  }
  /** Get the title validation error by container index. */
  getTitleDropdownErrorByContainerIndex(index: number): Locator {
    return this.getTitleDropdownByContainerIndex(index).locator(
      '[data-testid="AccompanyingGuestDetailsBBContainer-Form-ErrorText"]',
    );
  }
  /** Get the first name field by container index. */
  getFirstNameFieldByContainerIndex(index: number): Locator {
    return this.getAccompanyingGuestDetailContainerByIndex(index).getByTestId(
      `input-bbAccompanyingGuestDetails[${index - 1}][firstName]`,
    );
  }
  /** Get the last name field by container index. */
  getLastNameFieldByContainerIndex(index: number): Locator {
    return this.getAccompanyingGuestDetailContainerByIndex(index).getByTestId(
      `input-bbAccompanyingGuestDetails[${index - 1}][lastName]`,
    );
  }
  /** Get the email field by container index. */
  getEmailFieldByContainerIndex(index: number): Locator {
    return this.getAccompanyingGuestDetailContainerByIndex(index).getByTestId(
      `input-bbAccompanyingGuestDetails[${index - 1}][emailAddress]`,
    );
  }
  /** Get the control that switches a manual guest back to dynamic search. */
  getEnterAccompanyingGuestDetailsDynamicLinkByIndex(index: number): Locator {
    return this.getAccompanyingGuestDetailContainerByIndex(index).getByTestId(
      "AccompanyingGuestDetailsBBContainer-Form-SwitchToDynamic",
    );
  }

  // ######## UI actions/navigation ########
  /** Click the enter-details-manually link. */
  async clickEnterDetailsManuallyLink(roomIndex = 0): Promise<void> {
    console.log('\tClick on "Enter details manually" link');
    const link =
      this.getEnterAccompanyingGuestDetailsManuallyLinkByIndex(roomIndex);
    await link.scrollIntoViewIfNeeded();
    await link.click();
  }
  /** Open the title dropdown. */
  async clickTitleButton(roomIndex = 0): Promise<void> {
    console.log('\tClick on "Title" button');
    const dropdown = this.getTitleDropdownByContainerIndex(roomIndex);
    await dropdown.scrollIntoViewIfNeeded();
    await dropdown.click();
  }
  /** Select a title from the open dropdown. */
  async clickTitleValueBtn(titleValue: string): Promise<void> {
    console.log(`\tSelect "${titleValue}" element from the "Title" field`);
    await this.getTitleDropdownValue(titleValue).click();
  }
  /** Choose a title. */
  async setTitleButton(title: string, roomIndex = 0): Promise<void> {
    await this.clickTitleButton(roomIndex);
    await this.clickTitleValueBtn(title);
  }
  /** Set the first name input. */
  async setFirstNameInput({
    firstName,
    roomIndex = 0,
    pressTab = false,
  }: {
    firstName: string;
    roomIndex?: number;
    pressTab?: boolean;
  }): Promise<void> {
    console.log(`\t Set First Name Input = ${firstName}`);
    const field = this.getFirstNameFieldByContainerIndex(roomIndex);
    await field.fill(firstName);
    if (pressTab) await field.press("Tab");
  }
  /** Set the last name input. */
  async setLastNameInput({
    lastName,
    roomIndex = 0,
    pressTab = false,
  }: {
    lastName: string;
    roomIndex?: number;
    pressTab?: boolean;
  }): Promise<void> {
    console.log(`\t Set Last Name Input = ${lastName}`);
    const field = this.getLastNameFieldByContainerIndex(roomIndex);
    await field.fill(lastName);
    if (pressTab) await field.press("Tab");
  }
  /** Set the email address input. */
  async setEmailAddressInput({
    emailAddress = "",
    roomIndex = 0,
    pressTab = true,
  }: {
    emailAddress?: string;
    roomIndex?: number;
    pressTab?: boolean;
  } = {}): Promise<void> {
    console.log(`\t Set Email Address Input = ${emailAddress} `);
    const field = this.getEmailFieldByContainerIndex(roomIndex);
    await field.fill(emailAddress);
    if (pressTab) await field.press("Tab");
  }
  /** Fill accompanying guest details with source defaults. */
  async fillYourDetails({
    title,
    firstName,
    lastName,
    emailAddress,
    roomIndex = 1,
    selectTitle = true,
  }: {
    title?: string | Promise<string>;
    firstName?: string;
    lastName?: string;
    emailAddress?: string;
    roomIndex?: number;
    selectTitle?: boolean;
  }): Promise<void> {
    const resolvedTitle = await title;
    console.log(
      `\tFill "Accompanying Guest details" Section for Room ${roomIndex}`,
    );
    if (selectTitle) {
      if (resolvedTitle == null) {
        await this.clickTitleButton(roomIndex);
        await this.titleDropdownLabelsList
          .nth((await this.titleDropdownLabelsList.count()) > 1 ? 1 : 0)
          .click();
      } else if (resolvedTitle)
        await this.setTitleButton(resolvedTitle, roomIndex);
    }
    if (firstName) await this.setFirstNameInput({ firstName, roomIndex });
    if (lastName) await this.setLastNameInput({ lastName, roomIndex });
    if (emailAddress)
      await this.setEmailAddressInput({ emailAddress, roomIndex });
  }
  /** Select an employee using full or partial email behavior. */
  async setEmployeeInfoInDynamicField(
    employee: EmployeeDetails,
    roomIndex = 0,
  ): Promise<void> {
    const isFullEmailInput = employee.email.length <= 30;
    const email = isFullEmailInput
      ? employee.email
      : employee.email.substring(0, 30);
    const inputField =
      this.getDynamicAccompanyingGuestDetailsInputFieldByIndex(roomIndex);
    await inputField.fill(email);
    await expect( this.dynamicEmployeeSuggestionDropdown, "Employee suggestion dropdown", ).toBeVisible();
    await this.validateDropdownDynamicallySuggestedEmployeeLabelFormat(
      employee,
      isFullEmailInput,
    );
    if (await this.unsuccessfulSearchIcon.isVisible()) {
      await inputField.fill(email);
      await this.validateDropdownDynamicallySuggestedEmployeeLabelFormat(
        employee,
        isFullEmailInput,
      );
    }
  }
  /** Search for and select an employee by email. */
  async searchEmployeeDynamicallyByEmail(
    employee: EmployeeDetails,
    roomIndex = 0,
    isFullEmailInput = true,
  ): Promise<void> {
    const email = isFullEmailInput
      ? employee.email
      : employee.email.slice(0, employee.email.indexOf("@"));
    const inputField =
      this.getDynamicAccompanyingGuestDetailsInputFieldByIndex(roomIndex);
    await inputField.fill(email);
    await expect( this.dynamicEmployeeSuggestionDropdown, "Employee suggestion dropdown", ).toBeVisible();
    await this.validateDropdownDynamicallySuggestedEmployeeLabelFormat(
      employee,
      isFullEmailInput,
    );
    let employeeLabel = this.getDynamicEmployeeLabelByEmail(email);
    await employeeLabel.click();
    if (isFullEmailInput && (await this.unsuccessfulSearchIcon.isVisible())) {
      await inputField.fill(email);
      await this.validateDropdownDynamicallySuggestedEmployeeLabelFormat(
        employee,
      );
      employeeLabel = this.getDynamicEmployeeLabelByEmail(email);
      await employeeLabel.click();
    }
    await expect( this.successfulSearchIcon, "Successful employee search icon", ).toBeVisible();
  }

  // ######## UI validations ########
  /** Validate the employee suggestion format. */
  async validateDropdownDynamicallySuggestedEmployeeLabelFormat(
    employee: EmployeeDetails,
    isFullEmailInput = true,
  ): Promise<void> {
    console.log("Validate dynamically suggested employee details format");
    const emailSuggestion = isFullEmailInput
      ? employee.email
      : employee.email.slice(0, employee.email.indexOf("@"));
    await expect( this.getDynamicSuggestionEmployeeLabelByEmail(emailSuggestion), "Suggested employee details", ).toContainText( `${employee.title} ${employee.firstName} ${employee.lastName} (${employee.email})`, );
  }
  /** Validate the lead guest details label. */
  async validateLeadGuestDetailsLabelLocation({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log(`Validate Lead Guest Details is Displayed = ${isDisplayed}`);
    if (isDisplayed)
      await expect( this.leadGuestDetailsLabel, "Lead Guest Details Label", ).toBeVisible();
    else
      await expect( this.leadGuestDetailsLabel, "Lead Guest Details Label", ).not.toBeVisible();
  }
  /** Validate the first name input visibility. */
  async validateFirstNameInputIsDisplayed({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log(`Validate first name input is Displayed = ${isDisplayed}`);
    if (isDisplayed)
      await expect(this.firstNameInput, "First Name Input").toBeVisible();
    else
      await expect(this.firstNameInput, "First Name Input").not.toBeVisible();
  }
  /** Validate the last name input visibility. */
  async validateLastNameInputIsDisplayed({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log(`Validate last name input is Displayed = ${isDisplayed}`);
    if (isDisplayed)
      await expect(this.lastNameInput, "Last Name Input").toBeVisible();
    else await expect(this.lastNameInput, "Last Name Input").not.toBeVisible();
  }
  /** Validate the email input visibility. */
  async validateEmailInputIsDisplayed({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log(`Validate email input is Displayed = ${isDisplayed}`);
    if (isDisplayed) await expect(this.emailInput, "Email Input").toBeVisible();
    else await expect(this.emailInput, "Email Input").not.toBeVisible();
  }
  /** Validate the selected employee label in the search input. */
  async validateEmployeeInputFieldLabelValue(
    employee: EmployeeDetails,
  ): Promise<void> {
    console.log("Validate selected accompanying employee details format");
    await expect( this.accompanyingGuestDetailsInput, "Accompanying guest details input field format", ).toHaveValue( `${employee.title} ${employee.firstName} ${employee.lastName}`, );
  }
  /** Validate the accompanying guest input is empty. */
  async validateEmployeeInputFieldLabelIsEmpty(): Promise<void> {
    console.log("Validate accompanying employee details input is empty");
    await expect( this.accompanyingGuestDetailsInput, "Accompanying guest details input field is empty", ).toHaveValue("");
  }
  /** Validate title selection or its required-field error. */
  async validateTitleIsSelected(
    selectedValue: string | Promise<string>,
    roomIndex = 0,
  ): Promise<void> {
    const value = await selectedValue;
    console.log(`Validate that ${value} is selected`);
    const dropdown = this.getTitleDropdownByContainerIndex(roomIndex);
    if (value) await expect(dropdown, "Title label text").toContainText(value);
    else {
      const error = this.getTitleDropdownErrorByContainerIndex(roomIndex);
      await expect(error, "Error title label").toBeVisible();
      await expect(error, "Error title label text").toHaveText( await Strings.PLEASE_SELECT_YOUR_TITLE.name, );
    }
  }
  /** Validate the first name input. */
  async validateFirstName({
    value = "",
    shouldBeValid = false,
    errorFeedback = Strings.PLEASE_ENTER_YOUR_FIRST_NAME_MIN.name,
    roomIndex = 0,
  }: {
    value?: string;
    shouldBeValid?: boolean;
    errorFeedback?: string | Promise<string>;
    roomIndex?: number;
  } = {}): Promise<void> {
    console.log("Validate accompanying first name input");
    await this.validateTextInput(
      this.getFirstNameFieldByContainerIndex(roomIndex),
      "First name",
      Strings.FIRST_NAME_ACCOMPANYING_GUEST_DETAILS_SECTION.name,
      value,
      shouldBeValid,
      errorFeedback,
    );
  }
  /** Validate the last name input. */
  async validateLastName({
    value = "",
    shouldBeValid = false,
    errorFeedback = Strings.PLEASE_ENTER_YOUR_FIRST_NAME_MAX.name,
    roomIndex = 0,
  }: {
    value?: string;
    shouldBeValid?: boolean;
    errorFeedback?: string | Promise<string>;
    roomIndex?: number;
  } = {}): Promise<void> {
    console.log("Validate accompanying last name input");
    await this.validateTextInput(
      this.getLastNameFieldByContainerIndex(roomIndex),
      "Last name",
      Strings.LAST_NAME_ACCOMPANYING_GUEST_DETAILS_SECTION.name,
      value,
      shouldBeValid,
      errorFeedback,
    );
  }
  /** Validate the email address input. */
  async validateEmailAddress({
    value = "",
    shouldBeValid = false,
    errorFeedback = Strings.PLEASE_ENTER_A_VALID_EMAIL_ADDRESS_INVALID.name,
    roomIndex = 0,
  }: {
    value?: string;
    shouldBeValid?: boolean;
    errorFeedback?: string | Promise<string>;
    roomIndex?: number;
  } = {}): Promise<void> {
    console.log("Validate accompanying email address input");
    await this.validateTextInput(
      this.getEmailFieldByContainerIndex(roomIndex),
      "Email address",
      Strings.EMAIL.name,
      value,
      shouldBeValid,
      errorFeedback,
    );
  }
  private async validateTextInput(
    input: Locator,
    description: string,
    placeholder: string | Promise<string>,
    value: string,
    shouldBeValid: boolean,
    errorFeedback: string | Promise<string>,
  ): Promise<void> {
    const resolvedPlaceholder = await placeholder;
    const resolvedErrorFeedback = await errorFeedback;
    await expect(input, `${description} placeholder`).toHaveAttribute( "placeholder", resolvedPlaceholder, );
    await expect(input, `${description} value`).toHaveValue(value);
    const error = input
      .locator("xpath=ancestor::*[1]")
      .getByTestId(/FormErrorMessage|ErrorText/);
    if (shouldBeValid)
      await expect(error, `${description} error hidden`).not.toBeVisible();
    else
      await expect(error, `${description} validation error`).toContainText( resolvedErrorFeedback, );
  }
}
