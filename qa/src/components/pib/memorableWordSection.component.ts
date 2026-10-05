import { expect, type Locator, type Page } from "@playwright/test";

/**
 * InnBusiness application > Memorable word section
 */
export class MemorableWordSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########
  readonly memorableWordListTitleLabel: Locator = this.page.getByTestId( "MemorableWordInput-list-title", );
  readonly memorableWordListLabel: Locator = this.page.getByTestId( "MemorableWordInput-list-content", );
  readonly memorableWordInput: Locator = this.page.getByTestId( "memorableWord-Form-Input", );
  readonly hideShowButton: Locator = this.memorableWordInput.locator( "xpath=following-sibling::button", );
  readonly memorableWordInputErrorLabel: Locator = this.page.getByTestId( "memorableWord-Error-Tooltip", );

  // ######## UI actions/navigation ########
  /** Toggle hide/show button. */
  async toggleHideShowButton(): Promise<void> {
    console.log("Toggle hide/show button");
    await this.memorableWordListTitleLabel.scrollIntoViewIfNeeded();
    await this.hideShowButton.click();
  }

  /** Set value to memorable word input. */
  async setMemorableWordValue({
    value = "",
    pressTab = false,
  }: { value?: string; pressTab?: boolean } = {}): Promise<void> {
    console.log(`Set value - ${value} to memorable word`);
    await this.memorableWordInput.fill(value);
    if (pressTab) await this.memorableWordInput.press("Tab");
  }

  // ######## UI validations ########
  /** Validate the placeholder and the value of the memorable word input. */
  async validateMemorableWordInputValueAndPlaceholder({
    placeholder,
    value = "",
  }: { placeholder?: string; value?: string } = {}): Promise<void> {
    console.log("Validate memorable word input value and placeholder");
    if (placeholder)
      await expect( this.memorableWordInput, "Memorable word input placeholder", ).toHaveAttribute("placeholder", placeholder);
    await expect( this.memorableWordInput, "Memorable word input value", ).toHaveValue(value);
  }

  /** Validate memorable word input type. */
  async validateMemorableWordInputType({
    isHidden = true,
  }: { isHidden?: boolean } = {}): Promise<void> {
    console.log("Validate memorable word input type");
    await expect( this.memorableWordInput, "Memorable word input type", ).toHaveAttribute("type", isHidden ? "password" : "text");
  }

  /** Validate hide/show button. */
  async validateHideShowButton({
    isHidden = true,
  }: { isHidden?: boolean } = {}): Promise<void> {
    console.log("Validate hide/show button");
    await expect( this.hideShowButton, `Memorable word ${isHidden ? "show" : "hide"} button`, ).toBeVisible();
  }

  /** Validate memorable word input error. */
  async validateMemorableWordInputError({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log("Validate memorable word input error");
    if (isDisplayed)
      await expect( this.memorableWordInputErrorLabel, "Memorable word input error label", ).toBeVisible();
    else
      await expect( this.memorableWordInputErrorLabel, "Memorable word input error label", ).not.toBeVisible();
  }

  /** Validate Memorable word elements. */
  async validateMemorableWordElements(): Promise<void> {
    console.log("Validate memorable word elements");
    await expect( this.memorableWordListTitleLabel, "Memorable word list title label", ).toBeVisible();
    await expect( this.memorableWordListLabel, "Memorable word list content", ).toBeVisible();
    await expect(this.memorableWordInput, "Memorable word input").toBeVisible();
  }
}
