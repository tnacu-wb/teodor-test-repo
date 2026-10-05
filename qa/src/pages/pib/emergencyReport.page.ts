import { expect, type Locator } from "@playwright/test";
import { FooterSectionComponent } from "../../components/pib";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { BasePibPage } from "./basePib.page";
/** InnBusiness application > Spending > Emergency Report page */
export class EmergencyReportPage extends BasePibPage {
  readonly url = "spending/emergency-report";
  // ######## UI elements/properties ########
  readonly titleLabel: Locator = this.page.locator( 'div[data-testid="EmergencyReportPage-title"] h1', );
  readonly descriptionLabel: Locator = this.page.locator( 'div[data-testid="EmergencyReportPage-container"] p', );
  readonly downloadReportButton: Locator = this.page.getByTestId( "IB-Generate-Emergency-Report", );
  readonly downloadReportErrorLabel: Locator = this.page.locator( "div.bg-tooltipError", );
  readonly footer = new FooterSectionComponent();
  // ######## UI actions/navigation ########
  /** Navigate to Emergency report page. */
  async navigateToEmergencyReportPage(): Promise<void> {
    console.log("Navigate to Emergency report page");
    await this.openPath(this.url);
  }
  /** Click on download emergency report button. */
  async clickDownloadReportButton(): Promise<void> {
    console.log("Click download emergency report button");
    await this.downloadReportButton.scrollIntoViewIfNeeded();
    await this.downloadReportButton.click();
  }
  // ######## UI validations ########
  /** Check we reached the current page by checking a specific element from the page. */
  async validatePage(): Promise<void> {
    console.log("Validate Emergency report page");
    await this.validatePageMarker(this.titleLabel, "Emergency report");
  }
  /** Validate Emergency report page elements. */
  async validateEmergencyReport(): Promise<void> {
    console.log("Validate Emergency report page elements");
    await expect(this.titleLabel, "Emergency report title").toHaveText( await IbStrings.EMERGENCY_REPORT_TITLE.name, );
    await expect( this.descriptionLabel, "Emergency report description", ).toHaveText(await IbStrings.EMERGENCY_REPORT_DESCRIPTION.name);
    await expect( this.downloadReportButton, "Emergency report download button", ).toHaveText(await IbStrings.EMERGENCY_REPORT_DOWNLOAD_BUTTON.name);
    await this.footer.validateFooterIsDisplayed();
  }
  /** Validate download report error. */
  async validateDownloadReportErrorIsDisplayed({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log("Validate download report error");
    if (isDisplayed)
      await expect( this.downloadReportErrorLabel, "Download report error", ).toHaveText(await IbStrings.EMERGENCY_REPORT_ERROR.name);
    else
      await expect( this.downloadReportErrorLabel, "Download report error", ).not.toBeVisible();
  }
}
