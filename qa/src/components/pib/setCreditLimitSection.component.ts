import { expect, type Locator, type Page } from "@playwright/test";

/** InnBusiness application > Set credit limit section. */
export class SetCreditLimitSectionComponent {
  private readonly page: Page = global.page;
  // ######## UI elements/properties ########
  readonly setCreditLimitCheckbox: Locator = this.page.getByTestId( "Credit-Limit-Form-Checkbox", );
  readonly setCreditLimitLabel: Locator = this.setCreditLimitCheckbox.locator( "xpath=following-sibling::label", );
  readonly setCreditLimitDescriptionLabel: Locator = this.setCreditLimitCheckbox.locator( "xpath=ancestor::div[2]/following-sibling::span[1]", );
  readonly creditLimitInput: Locator = this.page.getByTestId( "Credit-Limit-Number-Form-Input", );
  readonly creditLimitInputErrorTooltip: Locator = this.page.getByTestId( "Credit-Limit-Number-Error-Tooltip", );
  // ######## UI actions/navigation ########
  /** Set the credit limit checkbox to the requested checked state. */
  async clickSetCreditLimitCheckbox({ shouldBeChecked = true }: { shouldBeChecked?: boolean } = {}): Promise<void> {
    console.log("Click Set credit limit checkbox");
    await this.setCreditLimitCheckbox.scrollIntoViewIfNeeded();
    if (await this.setCreditLimitCheckbox.getAttribute('aria-checked') !== String(shouldBeChecked)) {
      await this.setCreditLimitCheckbox.click();
    }
  }
  /** Set value to credit limit input. */
  async setCreditLimit(creditLimit: string): Promise<void> {
    console.log(`Set credit limit - ${creditLimit}`);
    await this.creditLimitInput.fill(creditLimit);
  }
  // ######## UI validations ########
  /** Validate credit limit section. */
  async validateCreditLimitSection({
    isDisplayed = true,
    isSetCreditLimitChecked = false,
  }: {
    isDisplayed?: boolean;
    isSetCreditLimitChecked?: boolean;
  } = {}): Promise<void> {
    console.log("Validate credit limit section");
    if (!isDisplayed) {
      await expect( this.setCreditLimitCheckbox, "Credit limit checkbox", ).not.toBeVisible();
      return;
    }
    await expect( this.setCreditLimitCheckbox, "Credit limit checkbox", ).toBeVisible();
    await expect( this.setCreditLimitCheckbox, "Set credit limit checked state", ).toHaveAttribute( "aria-checked", isSetCreditLimitChecked ? "true" : "false", );
  }
  /** Validate credit limit input. */
  async validateCreditLimitInput({
    value,
    shouldBeValid = true,
  }: {
    value: string;
    shouldBeValid?: boolean;
  }): Promise<void> {
    console.log("Validate credit limit input");
    await expect(this.creditLimitInput, "Credit limit input value").toHaveValue( value, );
    if (!shouldBeValid)
      await expect( this.creditLimitInputErrorTooltip, "Credit Limit Error Tooltip", ).toBeVisible();
  }
}
