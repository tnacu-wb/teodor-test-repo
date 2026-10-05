import { expect, type Locator, type Page } from "@playwright/test";
import { IbStrings } from "../../test-data/pib/ibStrings";

/**
 * Link and existing account section - Card management and Spending and reporting
 */
export class LinkExistingAccountSectionComponent {
  private readonly page: Page = global.page;

  // ######## UI elements/properties ########
  readonly linkExistingAccountContainer: Locator = this.page.getByTestId( "BenefitsBoxes-LinkAccountContainer", );
  readonly contentTitleLabel: Locator = this.linkExistingAccountContainer.locator("> div > span:nth-child(1)");
  readonly contentDescriptionLabel: Locator = this.linkExistingAccountContainer.locator("> div > span:nth-child(2)");
  readonly linkExistingAccountButton: Locator = this.page.getByTestId( "link-account-button", );

  // ######## UI actions/navigation ########
  /** Click on Link an existing account. */
  async clickOnLinkExistingAccount(): Promise<void> {
    console.log("Click on link and existing account");
    await this.linkExistingAccountButton.click();
  }

  // ######## UI validations ########
  /** Validate link and existing account section. */
  async validateLinkAnExistingAccountSection(): Promise<void> {
    console.log("Validate link and existing account section");
    await expect( this.contentTitleLabel, "Link existing account content title", ).toBeVisible();
    await expect(this.contentTitleLabel, "Link existing account title").toHaveText(await IbStrings.ALREADY_HAVE_AN_INNBUSINESS_PAY_ACCOUNT.name);
    await expect(this.contentDescriptionLabel, "Link existing account description").toHaveText(await IbStrings.ALREADY_HAVE_AN_INNBUSINESS_PAY_ACCOUNT_DESCRIPTION.name);
    await expect(this.linkExistingAccountButton, "Link existing account button").toHaveText(await IbStrings.LINK_AN_EXISTING_ACCOUNT.name);
  }
}
