import { expect, type Locator } from "@playwright/test";
import { IbStrings } from "../../test-data/pib/ibStrings";
import { BasePibPage } from "./basePib.page";

/**
 * IB access restricted page
 */
export class AccessRestrictedPage extends BasePibPage {
  readonly url = "pay-application-access-restricted";
  // ######## UI elements/properties ########
  readonly accessRestrictedTitleLabel: Locator = this.page.getByTestId( "PayApplicationAccessRestricted-title", );
  readonly accessRestrictedDescriptionLabel: Locator = this.page.getByTestId( "PayApplicationAccessRestricted-description", );
  readonly accessRestrictedContactSupportLabel: Locator = this.page.getByTestId( "PayApplicationAccessRestricted-contact", );
  readonly goToInnBusinessButton: Locator = this.page.getByTestId( "PayApplicationAccessRestricted-homepage", );
  readonly genericErrorPageLabel: Locator = this.page.locator( "main > div:nth-child(2)", );
  // ######## UI actions/navigation ########
  /** Click Go to InnBusiness button. */
  async clickGoToInnBusinessButton(): Promise<void> {
    console.log("Click Go to InnBusiness button");
    await this.goToInnBusinessButton.click();
  }
  // ######## UI validations ########
  /** Validate access restricted page content. */
  async validatePage({
    isDisplayed = true,
  }: { isDisplayed?: boolean } = {}): Promise<void> {
    console.log("Validate access restricted page");
    if (!isDisplayed) {
      await expect( this.genericErrorPageLabel, "Generic error message label", ).toBeVisible();
      return;
    }
    await this.validatePageMarker(
      this.accessRestrictedTitleLabel,
      "Access restricted",
    );
    this.validateUrl(this.url);
    await expect( this.accessRestrictedTitleLabel, "Access restricted title", ).toHaveText(await IbStrings.RESET_PASSWORD_INVALID_KEY_ERROR_TITLE.name);
    await expect( this.accessRestrictedDescriptionLabel, "Access restricted description", ).toHaveText(await IbStrings.RESET_PASSWORD_INVALID_KEY_ERROR_MESSAGE.name);
    await expect( this.accessRestrictedContactSupportLabel, "Access restricted contact support", ).toContainText(await IbStrings.CONTACT_COMPANY_ACCOUNT_OWNER.name);
    await expect( this.goToInnBusinessButton, "Go to InnBusiness button", ).toHaveText(await IbStrings.GO_TO_INNBUSINESS.name);
  }
}
