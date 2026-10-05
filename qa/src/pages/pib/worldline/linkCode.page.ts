import { expect, type Locator } from "@playwright/test";
import { Constants } from "../../../test-data/constants";
import { WorldlinePage } from "./worldline.page";

/** Worldline Link code page. */
export class LinkCodePage extends WorldlinePage {
  readonly url = Constants.WORLDLINE_BB_LINK_CODE;

  // ######## UI elements/properties ########
  readonly linkAccountsButton: Locator = this.page.locator( "#ctl00_ContentPlaceHolder1_btnLinkCode", );

  // ######## UI actions/navigation ########
  /** Open the Worldline Link code page. */
  async open(): Promise<void> {
    console.log("Open Worldline Link code page");
    await this.openExternal(this.url);
  }

  /** Click the Link accounts button. */
  async clickLinkAccountsButton(): Promise<void> {
    console.log("Click on Link accounts button");
    await this.linkAccountsButton.scrollIntoViewIfNeeded();
    await this.linkAccountsButton.click();
  }

  // ######## UI validations ########
  /** Check that the Worldline Link code page is displayed. */
  async validatePage(): Promise<void> {
    console.log("Validate Worldline Link code page");
    await expect( this.linkAccountsButton, "Worldline Link accounts button", ).toBeVisible();
  }
}
