import { expect, type Locator, type Page } from "@playwright/test";
import { Strings } from "@test-data/strings";

interface EmployeeDetails {
  title: string;
  firstName: string;
  lastName: string;
  email: string;
}

/**
 * The guest details section from Ancillaries BB page containing the UI elements, custom actions and validations for BB
 */
export class GuestDetailsSectionComponent {
  private readonly page: Page = global.page;

  // ######## properties ########
  static readonly ADULT_MEAL_CONTAINER_TESTID =
    "GuestDetailsPageBB-Meals-Adults-MealItem-Wrapper";
  static readonly TITLE_DROPDOWN_TESTID =
    "DropdownComp-GuestDetailsBBContainer-Form-TitleDropdown-entireList";

  // ######## UI elements/properties ########
  readonly guestDetailsTitleLabel: Locator = this.page.getByTestId( "GuestDetailsPageBB-GuestDetailsTitle", );
  readonly dynamicEmployeeSuggestionDropdown: Locator = this.page.getByTestId( "GuestDetailsBBContainer-Form-SuggestionCard", );
  readonly successfulSearchIcon: Locator = this.page.getByTestId("inputIconSuccess");
  readonly unsuccessfulSearchIcon: Locator = this.page.getByTestId("inputIconError");
  readonly titleDropdownLabelsList: Locator = this.page.locator( `[data-testid="${GuestDetailsSectionComponent.TITLE_DROPDOWN_TESTID}"]`, );
  readonly guestDetailsInput: Locator = this.page.getByTestId( "input-searchCriteria", );

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
  /** Get a dynamic guest details container by its room index. */
  getDynamicGuestDetailsContainerByIndex(index: number): Locator {
    return this.page.getByTestId(
      `GuestDetailsBBContainer-Form-DynamicGuestLead-${index}`,
    );
  }
  /** Get the dynamic guest details input by its room index. */
  getDynamicGuestDetailsInputFieldByIndex(index: number): Locator {
    return this.getDynamicGuestDetailsContainerByIndex(index).locator("input");
  }
  /** Get the enter-details-manually link by its room index. */
  getEnterGuestDetailsManuallyLinkByIndex(index: number): Locator {
    return this.getDynamicGuestDetailsContainerByIndex(index).locator("p");
  }
  /** Get the manual guest details container by its 1-based room index. */
  getGuestDetailContainerByIndex(index: number): Locator {
    return this.page.getByTestId(
      `GuestDetailsBBContainer-Form-ManualGuestLead-${index}`,
    );
  }
  /** Get the title dropdown by its manual guest container index. */
  getTitleDropdownByContainerIndex(index: number): Locator {
    return this.getGuestDetailContainerByIndex(index).getByTestId(
      "DropdownComp-GuestDetailsBBContainer-Form-TitleDropdown-menuButton",
    );
  }
  /** Get the title success icon associated with a field. */
  getTitleSuccessIconForElement(element: Locator): Locator {
    return element.locator("xpath=../../..").getByTestId("inputIconSuccess");
  }
  /** Get a title option from the open dropdown. */
  getTitleDropdownValue(titleValue: string): Locator {
    return this.page.locator(
      `[data-testid="${GuestDetailsSectionComponent.TITLE_DROPDOWN_TESTID}"] button[value="${titleValue}"]`,
    );
  }
  /** Get the title validation error by manual guest container index. */
  getTitleDropdownErrorByContainerIndex(index: number): Locator {
    return this.getTitleDropdownByContainerIndex(index).locator(
      '[data-testid="GuestDetailsBBContainer-Form-ErrorText"]',
    );
  }
  /** Get the first name input by manual guest container index. */
  getFirstNameFieldByContainerIndex(index: number): Locator {
    return this.getGuestDetailContainerByIndex(index).getByTestId(
      `input-bbGuestDetails[${index - 1}][firstName]`,
    );
  }
  /** Get the last name input by manual guest container index. */
  getLastNameFieldByContainerIndex(index: number): Locator {
    return this.getGuestDetailContainerByIndex(index).getByTestId(
      `input-bbGuestDetails[${index - 1}][lastName]`,
    );
  }
  /** Get the email input by manual guest container index. */
  getEmailFieldByContainerIndex(index: number): Locator {
    return this.getGuestDetailContainerByIndex(index).getByTestId(
      `input-bbGuestDetails[${index - 1}][emailAddress]`,
    );
  }
  /** Get the control that switches a manual guest back to dynamic search. */
  getEnterGuestDetailsDynamicLinkByIndex(index: number): Locator {
    return this.getGuestDetailContainerByIndex(index).getByTestId(
      "GuestDetailsBBContainer-Form-SwitchToDynamic",
    );
  }

  // ######## UI actions/navigation ########
  /** Click the enter-details-manually link. */
  async clickEnterDetailsManuallyLink(roomIndex = 1): Promise<void> {
    console.log('\tClick on "Enter details manually" link');
    const link = this.getEnterGuestDetailsManuallyLinkByIndex(roomIndex);
    await link.scrollIntoViewIfNeeded();
    await link.click();
  }
  /** Open the title dropdown for a zero-based room index. */
  async clickTitleButton(roomIndex = 0): Promise<void> {
    console.log('\tClick on "Title" button');
    const dropdown = this.getTitleDropdownByContainerIndex(roomIndex + 1);
    await dropdown.scrollIntoViewIfNeeded();
    await dropdown.click();
  }
  /** Select a title from the open dropdown. */
  async clickTitleValueBtn(titleValue: string): Promise<void> {
    console.log(`\tSelect "${titleValue}" element from the "Title" field`);
    await this.getTitleDropdownValue(titleValue).click();
  }
  /** Choose a title for a zero-based room index. */
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
    console.log(`\tFirst Name Input = ${firstName}`);
    const field = this.getFirstNameFieldByContainerIndex(roomIndex + 1);
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
    console.log(`\tLast Name Input = ${lastName}`);
    const field = this.getLastNameFieldByContainerIndex(roomIndex + 1);
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
    console.log(`\tEmail Address Input = ${emailAddress} `);
    const field = this.getEmailFieldByContainerIndex(roomIndex + 1);
    await field.fill(emailAddress);
    if (pressTab) await field.press("Tab");
  }
  /** Fill guest details while preserving optional-field semantics. */
  async fillYourDetails({
    title,
    firstName,
    lastName,
    emailAddress,
    roomIndex = 0,
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
    console.log(`\tFill "Guest details" Section for Room ${roomIndex}`);
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
  /** Select an employee from dynamic suggestions using full or partial email behavior. */
  async setEmployeeInfoInDynamicField(
    employee: EmployeeDetails,
    roomIndex = 0,
  ): Promise<void> {
    const isFullEmailInput = employee.email.length <= 30;
    const email = isFullEmailInput
      ? employee.email
      : employee.email.substring(0, 30);
    const inputField = this.getDynamicGuestDetailsInputFieldByIndex(roomIndex);
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
    roomIndex = 1,
    isFullEmailInput = true,
  ): Promise<void> {
    const email = isFullEmailInput
      ? employee.email
      : employee.email.slice(0, employee.email.indexOf("@"));
    const inputField = this.getDynamicGuestDetailsInputFieldByIndex(roomIndex);
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
  /** Select the first employee suggestion using keyboard navigation. */
  async selectEmployeeUsingArrowKeys(): Promise<void> {
    await this.guestDetailsInput.press("ArrowDown");
    await this.guestDetailsInput.press("Enter");
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
  /** Validate the selected employee label in the search input. */
  async validateEmployeeInputFieldLabelFormat(
    employee: EmployeeDetails,
  ): Promise<void> {
    console.log("Validate selected employee details format");
    await expect( this.guestDetailsInput, "Guest details input field format", ).toHaveValue( `${employee.title} ${employee.firstName} ${employee.lastName}`, );
  }
  /** Validate the employee input is empty. */
  async validateEmployeeInputFieldLabelIsEmpty(): Promise<void> {
    console.log("Validate employee details input is empty");
    await expect( this.guestDetailsInput, "Guest details input field is empty", ).toHaveValue("");
  }
  /** Validate title selection or its required-field error. */
  async validateTitleIsSelected(
    selectedValue: string | Promise<string>,
    roomIndex = 1,
  ): Promise<void> {
    const value = await selectedValue;
    console.log(`Validate that ${value} is selected`);
    const dropdown = this.getTitleDropdownByContainerIndex(roomIndex);
    if (value) {
      await expect(dropdown, "Title label text").toContainText(value);
      await expect( this.getTitleSuccessIconForElement(dropdown), "Title success icon", ).toBeVisible();
    } else {
      const error = this.getTitleDropdownErrorByContainerIndex(roomIndex);
      await expect(error, "Error title label").toBeVisible();
      await expect(error, "Error title label text").toHaveText( await Strings.PLEASE_SELECT_YOUR_TITLE.name, );
    }
  }
  /** Validate the first name input. */
  async validateFirstName({
    value = "",
    shouldBeValid = false,
    errorFeedback = Strings.PLEASE_ENTER_YOUR_FIRST_NAME_REQUIRED.name,
    roomIndex = 1,
  }: {
    value?: string;
    shouldBeValid?: boolean;
    errorFeedback?: string | Promise<string>;
    roomIndex?: number;
  } = {}): Promise<void> {
    console.log("Validate first name input");
    await this.validateTextInput(
      this.getFirstNameFieldByContainerIndex(roomIndex),
      "First name",
      Strings.FIRST_NAME.name,
      value,
      shouldBeValid,
      errorFeedback,
    );
  }
  /** Validate the last name input. */
  async validateLastName({
    value = "",
    shouldBeValid = false,
    errorFeedback = Strings.PLEASE_ENTER_YOUR_LAST_NAME_REQUIRED.name,
    roomIndex = 1,
  }: {
    value?: string;
    shouldBeValid?: boolean;
    errorFeedback?: string | Promise<string>;
    roomIndex?: number;
  } = {}): Promise<void> {
    console.log("Validate last name input");
    await this.validateTextInput(
      this.getLastNameFieldByContainerIndex(roomIndex),
      "Last name",
      Strings.LAST_NAME.name,
      value,
      shouldBeValid,
      errorFeedback,
    );
  }
  /** Validate the email address input. */
  async validateEmailAddress({
    value = "",
    shouldBeValid = false,
    errorFeedback = Strings.PLEASE_ENTER_YOUR_EMAIL_ADDRESS.name,
    roomIndex = 1,
  }: {
    value?: string;
    shouldBeValid?: boolean;
    errorFeedback?: string | Promise<string>;
    roomIndex?: number;
  } = {}): Promise<void> {
    console.log("Validate email address input");
    await this.validateTextInput(
      this.getEmailFieldByContainerIndex(roomIndex),
      "Email address",
      Strings.EMAIL_STAR.name,
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
