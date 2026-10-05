import { expect, type Locator } from '@playwright/test';
import { CcuiComponent } from '../baseCcui.component';
import { Constants } from '@test-data/constants';
import { Strings } from '@test-data/strings';

/** The card holder name and security check section from payment page, containing the UI elements, custom actions and validations. */
export class CardHolderNameSectionComponent extends CcuiComponent {
  // ######## properties ########

  // ######## UI elements/properties ########

  readonly cardholderNameSection: Locator = this.page.locator('[data-testid="cardHolderNameSection"]');
  readonly cardHolderNameTitleLabel: Locator = this.cardholderNameSection.locator('h3');
  readonly cardHolderFirstNameInput: Locator = this.page.locator('input[data-testid="input-cardHolderFirstName"]');
  readonly cardHolderLastNameInput: Locator = this.page.locator('input[data-testid="input-cardHolderLastName"]');
  readonly cardHolderFirstNameErrorMessageLabel: Locator = this.page.locator('[data-testid="input-cardHolderFirstName-FormErrorMessage"]');
  readonly cardHolderLastNameErrorMessageLabel: Locator = this.page.locator('[data-testid="input-cardHolderLastName-FormErrorMessage"]');
  readonly cardSecurityCheckButton: Locator = this.page.locator('button[data-testid="cardSecurityCheck_launchButton"]');

  // ######## UI actions/navigation ########
  /**
   * Set cardholder first name.
   * @param firstName First name to enter.
   */
  async setCardholderFirstName(firstName = Constants.RANDOM_GENERATED_FIRST_NAME): Promise<void> { console.log('Set cardholder first name'); await this.fillInput(this.cardHolderFirstNameInput, firstName); }
  /**
   * Set cardholder last name.
   * @param lastName Last name to enter.
   */
  async setCardholderLastName(lastName = Constants.RANDOM_GENERATED_LAST_NAME): Promise<void> { console.log('Set cardholder last name'); await this.fillInput(this.cardHolderLastNameInput, lastName); }
  /**
   * Set cardholder first and last name.
   * @param firstName First name to enter.
   * @param lastName Last name to enter.
   */
  async setCardholderFirstNameLastName(firstName = Constants.RANDOM_GENERATED_FIRST_NAME, lastName = Constants.RANDOM_GENERATED_LAST_NAME): Promise<void> { console.log('Set cardholder first and last name'); await this.clearFirstNameLastNameInput(); await this.setCardholderFirstName(firstName); await this.setCardholderLastName(lastName); }
  /** Clear first and last name input. */
  async clearFirstNameLastNameInput(): Promise<void> { console.log('Clear first and last name input'); await this.cardHolderFirstNameInput.fill(''); await this.cardHolderLastNameInput.fill(''); }
  // ######## UI validations ########
  /** Validate card holder name section is displayed. */
  async validateCardHolderNameSectionIsDisplayed(): Promise<void> { console.log('Validate card holder name section is displayed'); await expect(this.cardholderNameSection, 'Card holder name section').toBeVisible(); }
  /** Validate card holder name elements are displayed. */
  async validateCardHolderNameElementsAreDisplayed(): Promise<void> { console.log('Validate card holder name elements are displayed'); await expect(this.cardHolderNameTitleLabel, 'Card holder name title').toBeVisible(); await expect(this.cardHolderNameTitleLabel, 'Card holder name label').toContainText(await Strings.CARD_HOLDER_NAME.name); await expect(this.cardHolderFirstNameInput, 'Card holder first name').toBeVisible(); await expect(this.cardHolderFirstNameInput, 'First name placeholder').toHaveAttribute('placeholder', expect.stringContaining(await Strings.ENTER_FIRST_NAME.name)); await expect(this.cardHolderLastNameInput, 'Card holder last name').toBeVisible(); await expect(this.cardHolderLastNameInput, 'Last name placeholder').toHaveAttribute('placeholder', expect.stringContaining(await Strings.ENTER_LAST_NAME.name)); }
  /** Validate first and last name are empty. */
  async validateFirstNameLastNameAreEmpty(): Promise<void> { console.log('Validate first and last name are empty'); await expect(this.cardHolderFirstNameInput, 'Card holder first name should be empty').toHaveValue(''); await expect(this.cardHolderLastNameInput, 'Card holder last name should be empty').toHaveValue(''); }
  /**
   * Validate first name input field.
   * @param firstName Expected first name value.
   * @param isValid Whether the first-name field should be valid.
   */
  async validateFirstNameInputField(firstName: string, isValid: boolean): Promise<void> { console.log('Validate first name input field'); const errorFeedback = firstName.length === 0 ? await Strings.FIRST_NAME_REQUIRED.name : firstName.length > Constants.MAX_FIRST_NAME_CHARACTERS ? await Strings.PLEASE_ENTER_YOUR_FIRST_NAME_REQUIRED.name : !isValid ? await Strings.FIRST_NAME_INVALID_CHARACTERS.name : ''; await expect(this.cardHolderFirstNameInput, 'First name input value').toHaveValue(firstName); await expect(this.cardHolderFirstNameInput, 'First name input validity').toHaveAttribute('aria-invalid', String(!isValid)); await this.validateDisplayState(this.cardHolderFirstNameErrorMessageLabel, 'First name error feedback', !isValid); if (!isValid) await expect(this.cardHolderFirstNameErrorMessageLabel, 'First name error message').toContainText(errorFeedback); }
  /**
   * Validate last name input field.
   * @param lastName Expected last name value.
   * @param isValid Whether the last-name field should be valid.
   */
  async validateLastNameInputField(lastName: string, isValid: boolean): Promise<void> { console.log('Validate last name input field'); const errorFeedback = lastName.length === 0 ? await Strings.LAST_NAME_REQUIRED.name : lastName.length > Constants.MAX_LAST_NAME_CHARACTERS ? await Strings.PLEASE_ENTER_YOUR_LAST_NAME_REQUIRED.name : !isValid ? await Strings.LAST_NAME_INVALID_CHARACTERS.name : ''; await expect(this.cardHolderLastNameInput, 'Last name input value').toHaveValue(lastName); await expect(this.cardHolderLastNameInput, 'Last name input validity').toHaveAttribute('aria-invalid', String(!isValid)); await this.validateDisplayState(this.cardHolderLastNameErrorMessageLabel, 'Last name error feedback', !isValid); if (!isValid) await expect(this.cardHolderLastNameErrorMessageLabel, 'Last name error message').toContainText(errorFeedback); }
  /**
   * Validate first and last name input fields.
   * @param firstName Expected first name value.
   * @param lastName Expected last name value.
   * @param isValid Whether both fields should be valid.
   */
  async validateFirstNameLastNameInputFields(firstName: string, lastName: string, isValid: boolean): Promise<void> { console.log('Validate first and last name input fields'); await this.validateFirstNameInputField(firstName, isValid); await this.validateLastNameInputField(lastName, isValid); }
  /**
   * Validate card security check button is displayed.
   * @param isDisplayed Whether the button should be displayed.
   */
  async validateCardSecurityCheckButtonIsDisplayed(isDisplayed = true): Promise<void> { console.log('Validate card security check button is displayed'); await this.validateDisplayState(this.cardSecurityCheckButton, 'Card security check button', isDisplayed); }
}