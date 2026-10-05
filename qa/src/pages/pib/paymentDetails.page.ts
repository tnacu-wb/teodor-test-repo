import { expect, type FrameLocator, type Locator } from "@playwright/test";
import { type CardDetails, Cards } from "../../test-data/cards";
import { Strings } from "../../test-data/strings";
import { PaymentDetailsBasePage } from "../shared/paymentDetailsBase.page";

/**
 * Payment Details page from Opera environment containing the UI elements, custom actions and validations
 * Payment Details page uses 3CP/Planet iFrame which cannot be changed and may behave different in some areas
 * Elements in this iFrame cannot be changed so data-testid cannot be added
 */
export class PaymentDetailsPage extends PaymentDetailsBasePage {
  static readonly ADD_IB_CARD_IFRAME = "iframe#payment-iframe";
  // ######## UI elements/properties ########
  readonly addPaymentCardIframe: Locator = this.page.locator( `div[data-testid="add-card-iframe-element"] ${PaymentDetailsPage.ADD_IB_CARD_IFRAME}`, );
  readonly addPaymentCardFrame: FrameLocator = this.page.frameLocator( PaymentDetailsPage.ADD_IB_CARD_IFRAME, );
  readonly addPaymentCardTitleLabel: Locator = this.page.getByTestId( "add-card-iframe-title", );
  readonly addPaymentCardIframeCloseButton: Locator = this.page.getByTestId( "add-card-iframe-close-button", );
  readonly savedCardNumberLabel: Locator = this.paymentIframe.locator("#card_token > option");
  readonly savedCardholderNameLabel: Locator = this.paymentIframe.locator("div.maskedCardDetails > div").nth(0);
  readonly savedCardExpiryDateLabel: Locator = this.paymentIframe.locator("div.maskedCardDetails > div").nth(1);
  // ######## UI actions/navigation ########
  /** Click Add card details iframe close button. */
  async clickAddCardIframeCloseButton(): Promise<void> {
    console.log("Click Add card details iframe close button");
    await this.addPaymentCardIframeCloseButton.scrollIntoViewIfNeeded();
    await this.addPaymentCardIframeCloseButton.click();
  }
  /** Switch to Payment Details Opera iFrame. */
  async switchToPaymentDetailsIFrame(): Promise<FrameLocator> {
    console.log("Switch to Payment Details Opera iFrame");
    if (await this.addPaymentCardIframe.isVisible()) {
      await expect(this.addPaymentCardIframe, "Add payment card iframe").toBeVisible();
      return this.addPaymentCardFrame;
    }
    await expect(this.paymentIframeElement, "Payment details iframe").toBeVisible();
    return this.paymentIframe;
  }
  // ######## UI validations ########
  /** Validate add payment card iframe is displayed. */
  async validateAddPaymentCardIframeIsDisplayed(
    isDisplayed = false,
  ): Promise<void> {
    console.log("Validate add payment card iframe");
    if (isDisplayed)
      await expect( this.addPaymentCardIframe, "Add payment card iframe", ).toBeVisible();
    else
      await expect( this.addPaymentCardIframe, "Add payment card iframe", ).not.toBeVisible();
  }
  /** Validate the stored card number displayed by Worldline. */
  async validateStoredCardNumber(paymentDetails: CardDetails = Cards.VISA_CARD): Promise<void> {
    console.log("Validate saved card number");
    await expect(this.savedCardNumberLabel, "Saved card number").toContainText( `****${paymentDetails.number.replace(/\s/g, "").slice(-4)}`, );
  }
  /** Validate the stored cardholder name. */
  async validateStoredCardholderName(expectedName: string): Promise<void> {
    console.log("Validate saved cardholder name");
    await expect(this.savedCardholderNameLabel, "Saved cardholder name").toContainText(expectedName);
  }
  /** Validate the stored card expiry date. */
  async validateStoredExpiryYear(paymentDetails: CardDetails = Cards.VISA_CARD): Promise<void> {
    console.log("Validate saved card expiry");
    await expect(this.savedCardExpiryDateLabel, "Saved card expiry date").toContainText( `${await Strings.EXPIRES_IN_DATE.name} ${paymentDetails.expiryMonth}/${paymentDetails.expiryYear.slice(-2)}`, );
  }
}
