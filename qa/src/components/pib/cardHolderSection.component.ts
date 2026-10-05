import { expect, type Locator, type Page } from "@playwright/test";

/**
 * InnBusiness application > Card holder section
 */
export class CardHolderSectionComponent {
  private readonly page: Page = global.page;
  // ######## UI elements/properties ########
  readonly restrictedAccessMessageLabelForTmUser: Locator = this.page.locator( '(//div[contains(@class, "bg-tooltipInfo")]//span)[2]', );
  readonly restrictedAccessMessageTitleLabel: Locator = this.page.locator( "div.bg-tooltipInfo span.font-semibold", );
  readonly cardHolderNameInput: Locator = this.page.getByTestId( "PeoplePicker-Form-Input", );
  readonly restrictedConfigurationAccessNotificationLabel: Locator = this.page.locator('(//div[contains(@class,"bg-tooltipInfo")])[1]/div');
  readonly cardHolderNameTitleLabel: Locator = this.page.locator( '//div[@data-testid="Card-Holder-Section"]/span[1]', );
  readonly cardHolderNameEmployeeLabel: Locator = this.page.locator( '//div[@data-testid="Card-Holder-Section"]/span[2]', );
  readonly cardHolderSuggestionsDropdown: Locator = this.page.getByTestId( "PeoplePicker-IB-Form-People-Picker-Dropdown", );
  readonly employeeSuggestionsList: Locator = this.page.locator( '[data-testid^="PeoplePicker-IB-Form-People-Picker-Option-EMPL_"]', );
  readonly employeeSuggestionButton: Locator = this.cardHolderSuggestionsDropdown.locator("button");
  readonly cardHolderHowShouldTheNameAppearLabel: Locator = this.page.locator( '//div[@data-testid="cardDisplayNameOption-IB-Form-Select"]/parent::div/span', );
  readonly nameLabelOnCardDropdownButton: Locator = this.page.getByTestId( "cardDisplayNameOption-IB-Form-Select-Button", );
  readonly cardDefaultNameLabel: Locator = this.nameLabelOnCardDropdownButton.locator("span");
  readonly fullNameOptionLabel: Locator = this.page.getByTestId( "cardDisplayNameOption-fullName-Option", );
  readonly titleFullNameOptionLabel: Locator = this.page.getByTestId( "cardDisplayNameOption-titleFullName-Option", );
  readonly initialLastNameOptionLabel: Locator = this.page.getByTestId( "cardDisplayNameOption-initialLastName-Option", );
  readonly titleInitialLastNameOptionLabel: Locator = this.page.getByTestId( "cardDisplayNameOption-titleInitialLastName-Option", );
  readonly customFormatOptionLabel: Locator = this.page.getByTestId( "cardDisplayNameOption-custom-Option", );
  readonly nameLabelOptions: Locator = this.page.locator( '//div[@data-testid="cardDisplayNameOption-IB-Form-Select-Dropdown"]/button/span', );
  readonly customNameLabelInput: Locator = this.page.getByTestId( "cardDisplayName-Form-Input", );
  readonly customNameInputErrorLabel: Locator = this.page.getByTestId( "cardDisplayName-Error-Tooltip", );
  readonly restrictedAccessMessageForBookerLabel: Locator = this.page.locator( '(//div[contains(@class, "bg-tooltipInfo")]//span)[4]', );
  /** Employee email address from dropdown suggestions. */
  getDynamicEmployeeLabelByEmail(employeeEmail: string): Locator {
    return this.cardHolderSuggestionsDropdown.locator(
      `span:has-text("${employeeEmail}")`,
    );
  }
  // ######## UI actions/navigation ########
  /** Search employee by email. */
  async searchEmployeeByEmail(employeeEmail: string): Promise<void> {
    console.log(`Search employee by email ${employeeEmail}`);
    await this.cardHolderNameInput.fill(employeeEmail);
    await this.validateCardHolderSuggestion(employeeEmail);
    await this.getDynamicEmployeeLabelByEmail(employeeEmail).click();
  }
  /** Click name on card button button. */
  async clickNameOnCardDropdownButton(): Promise<void> {
    console.log("Click to change how name should be displayed on card button");
    await this.nameLabelOnCardDropdownButton.scrollIntoViewIfNeeded();
    await this.nameLabelOnCardDropdownButton.click();
  }
  /** Click on a specific index from nameLabelOptions based on string. */
  async clickNameLabelOption(optionName: { name: string }): Promise<void> {
    console.log(`Click on ${optionName.name} from Name Label Options`);
    await this.nameLabelOptions.filter({ hasText: optionName.name }).click();
  }
  /** Set value to custom name label input. */
  async setCardCustomName(customName: string): Promise<void> {
    console.log(`Set custom name label - ${customName}`);
    await this.customNameLabelInput.fill(customName);
  }
  /** Search by name or email. */
  async searchEmployeeByNameOrEmail({
    searchedTerm,
    shouldBeValid = true,
  }: {
    searchedTerm: string;
    shouldBeValid?: boolean;
  }): Promise<void> {
    console.log(`Find employee by email: ${searchedTerm}`);
    await this.cardHolderNameInput.fill(searchedTerm);
    if (shouldBeValid)
      await expect( this.employeeSuggestionsList.filter({ hasText: searchedTerm }).first(), "Expected employee suggestion", ).toBeVisible();
    else
      await expect( this.employeeSuggestionsList, "Expected no employee suggestion", ).toHaveCount(0);
  }
  /** Get card default name selected from dropdown. */
  async getCardDefaultName(): Promise<string> {
    console.log("Get card default name selected from dropdown");
    return this.cardDefaultNameLabel.innerText();
  }
  // ######## UI validations ########
  /** Validate card info notification message when adding card for existing employee when the user is travel manager. */
  async validateRestrictedConfigurationNotification(): Promise<void> {
    console.log("Validate restricted configuration notification");
    await expect( this.restrictedConfigurationAccessNotificationLabel, "Restricted configuration access notification", ).toBeVisible();
  }
  /** Validate card info notification message when adding card for existing employee when the user is booker. */
  async validateCardInfoNotificationForTravelManager(): Promise<void> {
    console.log("Validate card info notification for travel manager");
    await expect( this.restrictedAccessMessageLabelForTmUser, "Card info notification", ).toBeVisible();
  }
  /** Validate that card holder suggestion contains the expected text. */
  async validateCardInfoNotificationForBooker(): Promise<void> {
    console.log("Validate card info notification for booker");
    await expect( this.restrictedAccessMessageForBookerLabel, "Card info notification", ).toBeVisible();
  }
  /** Validate card holder name section. */
  async validateCardHolderSuggestion(expectedText: string): Promise<void> {
    console.log("Validate card holder suggestion");
    await expect( this.employeeSuggestionButton, "Employee suggestion not found", ).toContainText(expectedText);
  }
  /** Validate card custom name input. */
  async validateCardHolderNameSection(contactDetails: {
    firstName: string;
    lastName: string;
  }): Promise<void> {
    console.log("Validate card holder name section");
    await expect( this.cardHolderNameTitleLabel, "Card holder name title", ).toBeVisible();
    await expect( this.cardHolderNameEmployeeLabel, "Card holder employee label", ).toHaveText(`${contactDetails.firstName} ${contactDetails.lastName}`);
  }
  /** Validate Card holder name input is displayed. */
  async validateNamOnCardDropdown(): Promise<void> {
    console.log("Validate name on card dropdown");
    await expect(this.nameLabelOptions, "Name on card options").toHaveCount(5);
  }
  /** Validate restricted access message. */
  async validateCardCustomNameInput({
    nameInput,
    isValid = true,
  }: {
    nameInput: string;
    isValid?: boolean;
  }): Promise<void> {
    console.log("Validate name on card - custom name input");
    await expect( this.customNameLabelInput, "Card custom name input", ).toHaveValue(nameInput);
    if (!isValid)
      await expect( this.customNameInputErrorLabel, "Card custom name error label", ).toBeVisible();
  }
  /** Validate card holder name section when adding card for existing employee is selected. */
  async validateCardHolderNameInput(): Promise<void> {
    console.log("Validate card holder name input");
    await expect( this.cardHolderNameInput, "Card holder name input", ).toBeVisible();
  }
  /** Validate restricted access message. */
  async validateRestrictedAccessMessage(): Promise<void> {
    console.log("Validate restricted access message");
    await expect( this.restrictedAccessMessageTitleLabel, "Restricted access title", ).toBeVisible();
  }
  /** Validate card holder name section for adding card existing employee. */
  async validateCardHolderNameSectionForAddingCardExistingEmployee(): Promise<void> {
    console.log(
      "Validate card holder name section for adding card existing employee",
    );
    await expect( this.cardHolderNameTitleLabel, "Card holder name title", ).toBeVisible();
    await expect( this.cardDefaultNameLabel, "Card default name label", ).toBeVisible();
  }
}
