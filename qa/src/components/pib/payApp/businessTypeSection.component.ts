import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../../test-data/pib/ibStrings";
import { Strings } from "../../../test-data/strings";
/** InnBusiness application > Pay App > Your Company details > Business Type section */
export class BusinessTypeSectionComponent {
  private readonly page: Page = global.page;
  // ######## UI elements/properties ########
  readonly companyNameInput: Locator = this.page.getByTestId( "Company-name-Form-Input", );
  readonly companyNameErrorToolTipLabel: Locator = this.page.getByTestId( "Company-name-Error-Tooltip", );
  readonly businessTypeErrorTooltipLabel: Locator = this.page.getByTestId( "businessType-Error-Tooltip", );
  readonly businessTypeOptionsContainer: Locator = this.page.locator( 'div[data-testid="CompanyDetailsBusinessType-company-details"] ~ div', );
  readonly businessTypeLabel: Locator = this.page .locator( 'div[data-testid="CompanyDetailsBusinessType-company-details"] ~ div div', ) .first();
  readonly companyTypeOptionsList: Locator = this.page.locator( 'label[for^="BusinessTypeForm"]', );
  /** Get radio button by button label. */
  getRadioButtonByLabel(label: string): Locator {
    return this.page.locator(`//label[normalize-space()="${label}"]//button`);
  }
  // ######## UI actions/navigation ########
  /** Set Company Name input. */
  async setCompanyName({
    value,
    pressTab = false,
  }: {
    value: string;
    pressTab?: boolean;
  }): Promise<void> {
    console.log(`Set Company Name - ${value}`);
    await this.companyNameInput.fill(value);
    if (pressTab) await this.companyNameInput.press("Tab");
  }
  /** Click Company type radio button by label. */
  async clickCompanyTypeRadioButtonByLabel({
    companyTypeLabel,
  }: { companyTypeLabel?: string } = {}): Promise<void> {
    const label = companyTypeLabel ?? (await Strings.CHARITY.name);
    console.log(`Click company type ${label}`);
    await this.getRadioButtonByLabel(label).click();
  }
  // ######## UI validations ########
  /** Validate Company Name input. */
  async validateCompanyNameInput({
    companyName,
  }: { companyName?: string } = {}): Promise<void> {
    console.log("Validate Company Name input");
    await expect(this.companyNameInput, "Company name input").toHaveValue( companyName ?? "", );
  }
  /** Validate Business type error tooltip. */
  async validateBusinessTypeErrorTooltip(isDisplayed: boolean): Promise<void> {
    console.log("Validate business type error tooltip");
    if (isDisplayed)
      await expect( this.businessTypeErrorTooltipLabel, "Business type error tooltip", ).toBeVisible();
    else
      await expect( this.businessTypeErrorTooltipLabel, "Business type error tooltip", ).not.toBeVisible();
  }
  /** Validate Company radio button by label. */
  async validateCompanyTypeOptionButton({
    label,
    isDisplayed = true,
    isChecked = false,
  }: {
    label: string;
    isDisplayed?: boolean;
    isChecked?: boolean;
  }): Promise<void> {
    console.log(`Validate company type option ${label}`);
    const option = this.getRadioButtonByLabel(label);
    if (isDisplayed) {
      await expect(option, `Company type ${label}`).toBeVisible();
      await expect(option, `Company type ${label} state`).toHaveAttribute( "data-state", isChecked ? "checked" : "unchecked", );
    } else await expect(option, `Company type ${label}`).not.toBeVisible();
  }
  /** Validate Company Type section. */
  async validateCompanyTypeSection({
    companyName,
  }: { companyName?: string } = {}): Promise<void> {
    console.log("Validate Company Type section");
    await this.validateCompanyNameInput({ companyName });
    await expect( this.businessTypeOptionsContainer, "Business type options container", ).toBeVisible();
    await expect(this.businessTypeLabel, "Business type label").toHaveText( await IbStrings.BUSINESS_TYPE.name, );
  }
}
