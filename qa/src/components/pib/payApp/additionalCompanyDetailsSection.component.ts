import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../../test-data/pib/ibStrings";
/** InnBusiness application > Pay App > Your Company details section step 3 (additional company details) */
export class AdditionalCompanyDetailsSectionComponent {
  private readonly page: Page = global.page;
  // ######## UI elements/properties ########
  readonly estimatedMonthlySpendDropdownButton: Locator = this.page.getByTestId( "CompanyDetailsAdditionalDetails-estMonthlySpend-IB-Form-Select-Button", );
  readonly estimatedMonthlySpendDropdownList: Locator = this.page.getByTestId( "CompanyDetailsAdditionalDetails-estMonthlySpend-IB-Form-Select-Dropdown", );
  readonly estimatedMonthlySpendOptions: Locator = this.estimatedMonthlySpendDropdownList.locator("button");
  readonly estimatedMonthlySpendErrorTooltip: Locator = this.page.getByTestId( "CompanyDetailsAdditionalDetails-estMonthlySpend-Error-Tooltip", );
  readonly companyHotelPolicyDropdownButton: Locator = this.page.locator( 'div[data-testid="CompanyDetailsAdditionalDetails-hotelBrandPolicy-IB-Form-Select"] button', );
  readonly hotelPolicyDropdownList: Locator = this.page.getByTestId( "CompanyDetailsAdditionalDetails-hotelBrandPolicy-IB-Form-Select-Dropdown", );
  readonly hotelPolicyOptions: Locator = this.hotelPolicyDropdownList.locator("button");
  readonly additionalDetailsBackButton: Locator = this.page.getByTestId("wizard-back-icon");
  /** Get estimated monthly spend option by amount label. */
  getEstimatedMonthlySpendOptionByAmount(amountLabel: string): Locator {
    return this.page.getByTestId(
      `CompanyDetailsAdditionalDetails-estMonthlySpend-${amountLabel}-Option`,
    );
  }
  /** Get hotel policy option by label. */
  getHotelPolicyOptionByLabel(hotelPolicyLabel: string): Locator {
    return this.page.getByTestId(
      `CompanyDetailsAdditionalDetails-hotelBrandPolicy-${hotelPolicyLabel}-Option`,
    );
  }
  // ######## UI actions/navigation ########
  /** Return hotel policy values. */
  async getHotelPolicyDropdownValues(): Promise<string[]> {
    console.log("Get hotel policy dropdown values");
    return this.hotelPolicyOptions.allTextContents();
  }
  /** Get estimated monthly spend options. */
  async getEstimatedMonthlySpendArray(): Promise<string[]> {
    console.log("Get estimated monthly spend array");
    return this.estimatedMonthlySpendOptions.allTextContents();
  }
  /** Return estimated monthly spend dropdown values. */
  async getEstimatedMonthlySpendDropdownValues(): Promise<string[]> {
    console.log("Get estimated monthly spend dropdown values");
    return this.estimatedMonthlySpendOptions.allTextContents();
  }
  /** Click estimated monthly spend dropdown button. */
  async clickEstimatedMonthlySpendDropdownButton(): Promise<void> {
    console.log("Click estimated monthly spend dropdown button");
    await this.estimatedMonthlySpendDropdownButton.click();
  }
  /** Click company hotel policy dropdown button. */
  async clickCompanyHotelPolicyDropdownButton(): Promise<void> {
    console.log("Click company hotel policy dropdown button");
    await this.companyHotelPolicyDropdownButton.click();
  }
  /** Set company estimated monthly spend. */
  async setCompanyEstimatedMonthlySpend({
    amountLabel,
  }: {
    amountLabel: string;
  }): Promise<void> {
    console.log(`Set Company Estimated Monthly Spend - ${amountLabel}`);
    await this.clickEstimatedMonthlySpendDropdownButton();
    await this.getEstimatedMonthlySpendOptionByAmount(amountLabel).click();
  }
  /** Set company hotel policy. */
  async setCompanyHotelPolicy({
    hotelPolicyLabel,
  }: {
    hotelPolicyLabel: string;
  }): Promise<void> {
    console.log(`Set Company Hotel Policy - ${hotelPolicyLabel}`);
    await this.clickCompanyHotelPolicyDropdownButton();
    await this.getHotelPolicyOptionByLabel(hotelPolicyLabel).click();
  }
  /** Click Additional details back button. */
  async clickAdditionalDetailsBackButton(): Promise<void> {
    console.log("Click Additional details back button");
    await this.additionalDetailsBackButton.click();
  }
  // ######## UI validations ########
  /** Validate estimated monthly spend dropdown options. */
  async validateEstimateMonthlySpendDropdownOptions(): Promise<void> {
    console.log("Validate estimated monthly spend dropdown options");
    await expect( await this.getEstimatedMonthlySpendDropdownValues(), "Estimated monthly spend value list", ).toEqual(await this.getEstimatedMonthlySpendArray());
  }
  /** Validate company hotel policy dropdown options. */
  async validateCompanyHotelPolicyDropdownOptions(): Promise<void> {
    console.log("Validate company hotel policy dropdown options");
    await expect( this.hotelPolicyOptions, "Company hotel policy options", ).toHaveCount(3);
  }
  /** Validate estimated monthly spend error tooltip. */
  async validateEstimatedMonthlySpendErrorTooltip(
    isDisplayed: boolean,
  ): Promise<void> {
    console.log("Validate estimated monthly spend error tooltip");
    if (isDisplayed)
      await expect( this.estimatedMonthlySpendErrorTooltip, "Estimated monthly spend error tooltip", ).toHaveText(await IbStrings.YOUR_COMPANY_DETAILS_VALUE_REQUIRED.name);
    else
      await expect( this.estimatedMonthlySpendErrorTooltip, "Estimated monthly spend error tooltip", ).not.toBeVisible();
  }
  /** Validate additional company details section step. */
  async validateAdditionalCompanyDetailsSection({
    estimatedMonthlySpend,
    hotelPolicy,
  }: {
    estimatedMonthlySpend?: string;
    hotelPolicy?: string;
  } = {}): Promise<void> {
    console.log("Validate Additional Company Details section");
    await expect( this.estimatedMonthlySpendDropdownButton, "Estimated monthly spend label", ).toHaveText( estimatedMonthlySpend ?? (await IbStrings.ESTIMATED_MONTHLY_SPEND.name), );
    if (hotelPolicy)
      await expect( this.companyHotelPolicyDropdownButton, "Hotel policy label", ).toHaveText(hotelPolicy);
  }
}
