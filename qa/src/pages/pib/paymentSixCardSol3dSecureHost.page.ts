import { expect, type Locator } from "@playwright/test";
import { BasePibPage } from "./basePib.page";

/**
 * Six Card Solutions Three D Secure V2 Test Host page from Opera environment containing the UI elements, custom actions and validations
 * Elements in this iFrame cannot be changed so data-testid cannot be added
 */
export class PaymentSixCardSol3dSecureHostPage extends BasePibPage {
  // ######## UI elements/properties ########
  readonly confirmPaymentRadio: Locator = this.page.locator( 'input[name="radioButtonList"][value="Confirm payment"], input#confirm-radio', );
  readonly declinePaymentRadio: Locator = this.page.locator( '[id="radioButtonList"] [id="radioButtonFail"]', );
  readonly submitButton: Locator = this.page.locator( "input#buttonSubmit, input#submit-btn", );
  // ######## UI actions/navigation ########
  /** Confirm payment through the 3D Secure host. */
  async confirmPayment(): Promise<void> {
    console.log("Confirm payment through 3D Secure host");
    const hostDisplayed = await this.submitButton
      .waitFor({ state: "visible", timeout: 10000 })
      .then(() => true)
      .catch(() => false);
    if (!hostDisplayed) {
      console.log("3D Secure page was not displayed");
      return;
    }
    await this.confirmPaymentRadio.click();
    await this.submitButton.click();
  }
  /** Decline payment through the 3D Secure host. */
  async declinePayment(): Promise<void> {
    console.log("Decline payment through 3D Secure host");
    await this.declinePaymentRadio.click();
    await this.submitButton.click();
  }
  // ######## UI validations ########
  /** Validate the 3D Secure host page. */
  async validatePage(): Promise<void> {
    console.log("Validate 3D Secure host page");
    await expect(this.submitButton, "3D Secure submit button").toBeVisible();
  }
}
