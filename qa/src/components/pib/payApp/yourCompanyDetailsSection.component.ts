import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../../test-data/pib/ibStrings";
import { AdditionalCompanyDetailsSectionComponent } from "./additionalCompanyDetailsSection.component";
import { BusinessTypeSectionComponent } from "./businessTypeSection.component";
/** InnBusiness application > Pay App > Your Company details section */
export class YourCompanyDetailsSectionComponent {
  private readonly page: Page = global.page;
  // ######## UI elements/properties ########
  readonly yourCompanyDetailsTitleLabel: Locator = this.page.getByTestId("wizard-title");
  readonly yourCompanyDetailsDescriptionLabel: Locator = this.page .locator('h1[data-testid="wizard-title"]') .locator("xpath=parent::*/following-sibling::div[1]");
  readonly additionalCompanyDetailsSection =
    new AdditionalCompanyDetailsSectionComponent();
  readonly businessTypeSection = new BusinessTypeSectionComponent();
  // ######## UI actions/navigation ########
  // ######## UI validations ########
  /** Validate your company details section. */
  async validateYourCompanyDetailsSection(): Promise<void> {
    console.log("Validate your company details section");
    await expect( this.yourCompanyDetailsTitleLabel, "Your company details title", ).toHaveText(await IbStrings.YOUR_COMPANY_DETAILS.name);
    await expect( this.yourCompanyDetailsDescriptionLabel, "Your company details description", ).toHaveText(await IbStrings.YOUR_COMPANY_DETAILS_DESCRIPTION.name);
  }
}
