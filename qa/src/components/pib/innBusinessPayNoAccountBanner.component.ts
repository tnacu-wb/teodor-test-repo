import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../test-data/pib/ibStrings";

/**
 * Inn Business Pay no tethered account banner
 */
export class InnBusinessPayNoAccountBannerComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########
  readonly noAccountBannerTitleLabel: Locator = this.page.getByTestId( "NoAccountBanner-Title", );
  readonly noAccountBannerSubTitleLabel: Locator = this.page.getByTestId( "NoAccountBanner-SubTitle", );
  readonly applyNowButton: Locator = this.page.getByTestId( "NoAccountBanner-ApplyNowButton", );
  readonly linkAccountButton: Locator = this.page.getByTestId( "NoAccountBanner-LinkAccountButton", );

  // ######## UI actions/navigation ########
  /** Click on apply now button. */
  async clickApplyNowButton(): Promise<void> {
    console.log("Click on apply now button");
    await this.applyNowButton.scrollIntoViewIfNeeded();
    await this.applyNowButton.click();
  }

  /** Click on link account button. */
  async clickLinkAccountButton(): Promise<void> {
    console.log("Click on link account button");
    await this.linkAccountButton.scrollIntoViewIfNeeded();
    await this.linkAccountButton.click();
  }

  // ######## UI validations ########
  /** Validate Inn Business Pay no account banner. */
  async validateInnBusinessPayNoAccountBanner({
    isTravelManager = true,
  }: { isTravelManager?: boolean } = {}): Promise<void> {
    console.log("Validate Inn Business Pay no account banner");
    await expect(this.noAccountBannerTitleLabel, "No account banner title label").toHaveText(await IbStrings.WORK_TRAVEL_JUST_GOT_EASIER.name);
    await expect(this.noAccountBannerSubTitleLabel, "No account banner subtitle label").toHaveText(await IbStrings.THE_NEW_HASSLE_FREE_PAYMENT.name);
    await expect(this.linkAccountButton, "Link account button").toHaveText(await IbStrings.LINK_AN_EXISTING_ACCOUNT.name);
    if (isTravelManager) {
      await expect(this.applyNowButton, "Apply now button").toHaveText(await IbStrings.APPLY_NOW.name);
    } else {
      await expect(this.applyNowButton, "Apply now button").not.toBeVisible();
    }
  }
}
