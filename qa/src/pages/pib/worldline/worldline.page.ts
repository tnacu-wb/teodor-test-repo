import { type Locator } from "@playwright/test";
import { BasePibPage } from "../basePib.page";

/** Shared Worldline page actions for the external Worldline application. */
export class WorldlinePage extends BasePibPage {
  // ######## UI elements/properties ########
  readonly backToInnBusinessButton: Locator = this.page.locator( "a#ctl00_hypILReturn", );
  readonly backToInnBusinessDeButton: Locator = this.page.locator( 'a[href="IntegratedLoginReturn.aspx"]', );

  // ######## UI actions/navigation ########
  /** Navigate to an external Worldline page. */
  protected async openExternal(url: string): Promise<void> {
    console.log(`Open Worldline page: ${url}`);
    await this.page.goto(url, { waitUntil: "domcontentloaded" });
  }

  /** Click the Back to Inn Business button. */
  async clickBackToInnBusiness(isDeCompany = false): Promise<void> {
    console.log(
      `Click Back to Inn Business button for German company: ${isDeCompany}`,
    );
    const backButton = isDeCompany
      ? this.backToInnBusinessDeButton
      : this.backToInnBusinessButton;
    await backButton.scrollIntoViewIfNeeded();
    await backButton.click();
  }
}
