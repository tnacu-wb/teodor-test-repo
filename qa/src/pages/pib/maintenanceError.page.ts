import { expect, type Locator } from "@playwright/test";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { BasePibPage } from "./basePib.page";

/**
 * IB maintenance/error page
 */
export class MaintenanceErrorPage extends BasePibPage {
  readonly url = "error";
  // ######## UI elements/properties ########
  readonly maintenanceErrorLabel: Locator = this.page .locator('header[data-testid="Maintenance-header"] ~ div div span') .nth(0);
  readonly maintenanceErrorDescriptionLabel: Locator = this.page .locator('header[data-testid="Maintenance-header"] ~ div div span') .nth(1);
  readonly backToHomeButton: Locator = this.page.getByTestId( "Maintenance-back-to-home-button", );
  // ######## UI actions/navigation ########
  /** Click Back to Home button. */
  async clickBackToHomeButton(): Promise<void> {
    console.log("Click Back to Home button");
    await this.backToHomeButton.click();
  }
  // ######## UI validations ########
  /** Validate maintenance error page content. */
  async validatePage(): Promise<void> {
    console.log("Validate maintenance error page");
    await this.validatePageMarker(
      this.maintenanceErrorLabel,
      "Maintenance error",
    );
    this.validateUrl(this.url);
    await expect( this.maintenanceErrorLabel, "Maintenance page error title", ).toHaveText(await IbStrings.OOPS_SOMETHING_WENT_WRONG_UPPER.name);
    await expect( this.maintenanceErrorDescriptionLabel, "Maintenance page error description", ).toHaveText(await IbStrings.WE_WERE_UNABLE_TO_PROCESS_REQUEST_PAYAPP.name);
    await expect(this.backToHomeButton, "Back to home button").toHaveText( await IbStrings.BACK_TO_HOME.name, );
  }
}
