import { expect, type Locator } from "@playwright/test";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { BasePibPage } from "./basePib.page";

/**
 *  Set a Password page class
 */
export class SetAPasswordPage extends BasePibPage {
  // ######## UI elements/properties ########
  readonly titleLabel: Locator = this.page.getByTestId("wizard-title");
  readonly backButton: Locator = this.page.locator( '//*[@data-testid="wizard-back-icon"]/parent::a', );
  readonly passwordForm: Locator = this.page.getByTestId( "RegisterStep2-PersonalInfoPassword-Form", );
  readonly passwordInput: Locator = this.page.getByTestId( "SetPassword-Form-Input", );
  readonly showHidePasswordButton: Locator = this.passwordInput.locator( "xpath=following-sibling::button", );
  readonly errorTooltipLabel: Locator = this.page.getByTestId( "SetPassword-Error-Tooltip", );
  readonly passwordPolicyTitle: Locator = this.page.getByTestId( "RegisterStep2-list-title", );
  readonly passwordPolicyDescriptionListItemLabels: Locator = this.page.locator( 'ul[data-testid="RegisterStep2-list-content"] li', );
  readonly privacyPolicyLabel: Locator = this.page.locator( '//h4[@data-testid="RegisterStep2-list-title"]/parent::div/following-sibling::div', );
  readonly createAccountButton: Locator = this.page.getByTestId("footer-button");

  // ######## UI actions/navigation ########
  /**
   * Click on Back button
   */
  async clickBackButton(): Promise<void> {
    console.log("Click Back button");
    await this.backButton.scrollIntoViewIfNeeded();
    await this.backButton.click();
  }

  /**
   * Click on show/hide password button
   */
  async clickShowHidePasswordButton(): Promise<void> {
    console.log("Click Show/Hide Password button");
    await this.showHidePasswordButton.scrollIntoViewIfNeeded();
    await this.showHidePasswordButton.click();
  }

  /**
   * Set password input
   * @param password password input
   */
  async setPasswordInput(password: string): Promise<void> {
    console.log("Set password");
    await this.passwordInput.fill(password);
  }

  /**
   *  Click on Create Account button
   */
  async clickCreateAccountButton(): Promise<void> {
    console.log("Click Create Account button");
    await this.createAccountButton.scrollIntoViewIfNeeded();
    await this.createAccountButton.click();
  }

  // ######## UI validations ########
  /**
   * Check we reached the current page by checking a specific element from the page
   */
  async validatePage(): Promise<void> {
    console.log("Validate Set a Password page");
    await this.validatePageMarker(this.passwordForm, "Set a password");
  }

  /**
   * Validate the Set a Password page look and feel
   */
  async validateSetAPasswordPageElements(): Promise<void> {
    console.log("Validate Set a Password page elements");
    await expect(this.backButton, "Back button").toBeVisible();
    await expect(this.titleLabel, "Set a password title").toHaveText( await IbStrings.AUTH_PASSWORD_TITLE.name, );
    await expect( this.passwordInput, "Password input placeholder", ).toHaveAttribute( "placeholder", await IbStrings.AUTH_PASSWORD_PLACEHOLDER.name, );
    await expect(this.passwordInput, "Password input value").toHaveValue("");
    await expect(this.passwordPolicyTitle, "Password policy title").toHaveText( await IbStrings.AUTH_PASSWORD_INSTRUCTIONS.name, );
    const passwordRules = [
      IbStrings.AUTH_PASSWORD_RULE_1,
      IbStrings.AUTH_PASSWORD_RULE_2,
      IbStrings.AUTH_PASSWORD_RULE_3,
      IbStrings.AUTH_PASSWORD_RULE_4,
    ];
    for (
      let index = 0;
      index < (await this.passwordPolicyDescriptionListItemLabels.count());
      index += 1
    )
      await expect( this.passwordPolicyDescriptionListItemLabels.nth(index), `Password policy description list item ${index + 1}`, ).toHaveText(await passwordRules[index].name);
    await expect(this.privacyPolicyLabel, "Privacy policy label").toHaveText( await IbStrings.AUTH_PASSWORD_TERMS.name, );
    await expect(this.createAccountButton, "Create account button").toHaveText( await IbStrings.AUTH_PASSWORD_CREATE_ACCOUNT_BUTTON.name, );
  }

  /**
   * Validate hide/show button
   * @param data data object
   * @param data.isHidden true if text is hidden
   */
  async validateShowHide({
    isHidden = true,
  }: { isHidden?: boolean } = {}): Promise<void> {
    console.log("Validate hide/show button");
    await expect( this.showHidePasswordButton, "Show/hide password button", ).toHaveText(await (isHidden ? IbStrings.SHOW_IB : IbStrings.HIDE).name);
    if (isHidden) {
      console.log("Validate input text is hidden");
      await expect( this.passwordInput, "Input text is not hidden", ).toHaveAttribute("type", "password");
    } else {
      console.log("Validate input text is displayed");
      await expect( this.passwordInput, "Input text is not displayed", ).toHaveAttribute("type", "text");
    }
  }

  /**
   * Validate error tooltip message
   * @param data data object
   * @param data.isDisplayed true if the error tooltip should be displayed, false otherwise
   */
  async validateErrorTooltipMessage({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log(
      `Validate error tooltip message ${isDisplayed ? "is displayed" : "is not displayed"}`,
    );
    if (isDisplayed)
      await expect(this.errorTooltipLabel, "Error tooltip").toHaveText( await IbStrings.AUTH_PASSWORD_VALIDATION.name, );
    else
      await expect( this.errorTooltipLabel, "Error tooltip label", ).not.toBeVisible();
  }
}
