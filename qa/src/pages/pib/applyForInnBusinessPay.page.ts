import { type Locator } from "@playwright/test";
import { BasePibPage } from "./basePib.page";
/** Page object for Apply for InnBusiness Pay */
export class ApplyForInnBusinessPayPage extends BasePibPage {
  readonly url = "business-pay/apply";
  // ######## UI elements/properties ########
  readonly applyForInnBusinessContainer: Locator = this.page.getByTestId("wizard-page");
  // ######## UI actions/navigation ########
  // ######## UI validations ########
  /** Check we reached the current page by checking a specific element from the page. */
  async validatePage(): Promise<void> {
    console.log("Validate Apply for InnBusiness Pay page");
    await this.validatePageMarker(
      this.applyForInnBusinessContainer,
      "Apply for InnBusiness Pay",
    );
  }
}
