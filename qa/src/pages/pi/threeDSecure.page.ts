import { expect, type Locator } from '@playwright/test';
import { UiUtils } from '../../utils/uiUtils';
import { PaymentVerticalStripSectionComponent } from '../../components/shared/payment/paymentVerticalStripSection.component';
import { BasePage } from '../shared/base.page';

/**
 * 3D Secure Challenge Page - handles the third-party 3DS verification page
 * (Worldline/SIX hosted payment page) that may appear after card submission
 * in the payment flow.
 *
 * This page is optional — it may not appear in all environments or card configurations.
 * The confirmPayment() method handles both scenarios gracefully.
 *
 * Flow:
 * 1. PaymentDetailsPage submits payment via the Worldline iframe
 * 2. The 3DS challenge page may appear (Worldline/SIX hosted page)
 * 3. confirmPayment() handles the challenge (or returns immediately if not shown)
 * 4. waitForConfirmationPage() waits for the redirect to booking confirmation
 */
export class ThreeDSecurePage extends BasePage {
  // ######## UI elements/properties ########

  readonly verticalStripSection: PaymentVerticalStripSectionComponent = new PaymentVerticalStripSectionComponent();

  // 3DS challenge elements (third-party page, using generic selectors)
  readonly paymentDetailsContainer: Locator = this.page.locator('div[data-testid="paymentContainer"]');
  readonly confirmPaymentRadio: Locator = this.page.locator('input[name="radioButtonList"][value="Confirm payment"], input#confirm-radio');
  readonly declinePaymentRadio: Locator = this.page.locator('[id="radioButtonList"] [id="radioButtonFail"]');
  readonly submitButton: Locator = this.page.locator('input#buttonSubmit, input#submit-btn');
  readonly redirectLink: Locator = this.page.locator('td#clickIfNotRedirected a, a#redirect-link');

  // ######## UI actions/navigation ########

  /** Select the Confirm payment option on the 3D Secure host. */
  async selectConfirmPaymentOption(): Promise<void> {
    console.log('Select Confirm payment option');
    await this.confirmPaymentRadio.click();
  }

  /** Select the Decline payment option on the 3D Secure host. */
  async selectDeclinePaymentOption(): Promise<void> {
    console.log('Select Decline payment option');
    await this.declinePaymentRadio.click();
  }

  /** Submit the selected 3D Secure outcome. */
  async clickOnSubmitButton(): Promise<void> {
    console.log('Click 3D Secure submit button');
    await this.submitButton.click();
  }

  /** Follow the manual redirect link when 3D Secure does not redirect automatically. */
  async clickOnRedirectButton(): Promise<void> {
    console.log('Click 3D Secure redirect button');
    await this.redirectLink.first().scrollIntoViewIfNeeded();
    await this.redirectLink.first().click();
  }

  /**
   * Handle the 3D Secure challenge flow on the Worldline/SIX hosted page.
   *
   * - If the 3DS page does not appear (submit button not visible within 10s), returns early.
   * - If displayed: selects "Confirm payment" radio and clicks Submit.
   * - After Submit: waits 3s for auto-redirect (submit button disappearing).
   * - If no auto-redirect: waits up to 10s for redirect link and clicks it.
   * - If redirect link not found: throws a clear error.
   */
  async confirmPayment(): Promise<void> {
    console.log('Confirming payment via 3D Secure challenge');
    // Check if 3DS page is displayed — submit button visible within 10s
    const isThreeDSVisible = await this.submitButton
      .isVisible()
      .catch(() => false);

    if (!isThreeDSVisible) {
      // Wait up to 10s for submit button to appear
      try {
        await this.submitButton.waitFor({ state: 'visible' });
      } catch {
        // 3DS page not shown — proceed directly to confirmation page
        return;
      }
    }

    // 3DS page is displayed — select "Confirm payment" and click Submit
    await this.selectConfirmPaymentOption();
    await this.clickOnSubmitButton();

    // Wait 3s to check if auto-redirect occurred (submit button disappears)
    try {
      await this.submitButton.waitFor({ state: 'hidden', timeout: 3000 });
      // Auto-redirect occurred — done
      return;
    } catch {
      // No auto-redirect — need to find and click redirect link
    }

    // Wait up to 10s for redirect link to appear and click it
    try {
      await this.redirectLink.first().waitFor({ state: 'visible' });
      await this.clickOnRedirectButton();
    } catch {
      throw new Error(
        '3D Secure page did not complete the redirect. ' +
          'The manual redirect link was not found within 10 seconds after the auto-redirect failed.'
      );
    }
  }

  /**
   * Wait for navigation to the booking confirmation page after 3DS challenge.
   * Uses an extended timeout (120s) because payment processing and redirect
   * back to the merchant can take significant time.
   *
   * Waits for the booking confirmation page indicator element to be visible,
   * which signals that the payment was processed and the redirect completed.
   * @throws Error if the booking confirmation page does not load within 120s
   */
  async waitForConfirmationPage(): Promise<void> {
    console.log('Waiting for booking confirmation page after 3D Secure');
    try {
      // Wait for the booking confirmation container to appear on the page.
      // This element is the primary indicator that payment succeeded and the
      // Worldline/SIX redirect back to the merchant site completed.
      await this.page
        .locator('[data-testid="ThanksForBooking-Container"]')
        .waitFor({ state: 'visible', timeout: 120000 });
    } catch {
      throw new Error(
        'Booking confirmation page did not load within 120s after 3D Secure challenge. ' +
          'Payment processing or redirect may have timed out.'
      );
    }
  }

  /** Decline the 3D Secure challenge when it is displayed. */
  async declinePayment(): Promise<void> {
    console.log('Declining payment via 3D Secure challenge');
    if (!(await this.submitButton.isVisible().catch(() => false))) {
      try {
        await this.submitButton.waitFor({ state: 'visible', timeout: 10000 });
      } catch {
        console.log('3D secure page was not displayed');
        return;
      }
    }

    await this.selectDeclinePaymentOption();
    await this.clickOnSubmitButton();
    if (await this.submitButton.isVisible().catch(() => false)) {
      await this.redirectLink.first().waitFor({ state: 'visible', timeout: 10000 });
      await this.clickOnRedirectButton();
    }
  }

  // ######## UI validations ########

  /** Validate the 3D Secure page is displayed. */
  async validatePage(): Promise<void> {
    console.log('Validate 3D Secure page');
    await expect(this.submitButton, '3D Secure submit button should be visible').toBeVisible();
    await expect(this.confirmPaymentRadio, '3D Secure confirm-payment option should be visible').toBeVisible();
  }

  /** Validate the booking summary appears to the right of the 3D Secure form. */
  async validateBookingSummaryComponentPosition(): Promise<void> {
    console.log('Validate 3D Secure booking summary component position');
    await UiUtils.validateIsLeftOf({
      leftElement: this.paymentDetailsContainer,
      rightElement: this.verticalStripSection.bookingSummaryWrapper,
      maxDistanceBetween: 1100,
      elementDescription: 'Vertical strip section position on page',
    });
  }
}
