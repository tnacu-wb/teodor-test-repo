import { type Locator, expect } from '@playwright/test';
import { Strings } from '../../test-data/strings';
import { UiUtils } from '../../utils/uiUtils';
import { PaymentOptionsComponent } from '../../components/shared/payment/paymentOptions.component';
import { PaymentVerticalStripSectionComponent } from '../../components/shared/payment/paymentVerticalStripSection.component';
import { BasePage } from '../shared/base.page';

/**
 * PI Payment Page - where users select payment type (New Business Account Card),
 * payment option (Pay on Arrival), donation, and proceed to payment details.
 */
export class PaymentPage extends BasePage {
  // ######## UI elements/properties ########

  readonly url = 'payment';
  readonly paymentOption: PaymentOptionsComponent = new PaymentOptionsComponent();
  readonly verticalStripSection: PaymentVerticalStripSectionComponent = new PaymentVerticalStripSectionComponent();

  // Page loaded indicator
  readonly pageLoadedIndicator: Locator = this.page.locator('div[data-testid="CardDetails-Container"]');

  // Payment option radios
  readonly payOnArrivalOption: Locator = this.page.locator('*[data-testid="radio-box-inside_PAY_ON_ARRIVAL"]');

  // Payment type radios
  readonly newBusinessAccountCardRadio: Locator = this.page.getByRole('radio', { name: /Business.*Card|Premier Inn Business Pay/i });

  // Donation section
  readonly donationContainer: Locator = this.page.locator('div[data-testid="Donation-Container"]');

  // Total cost section
  readonly totalCostValueLabel: Locator = this.page.locator('p[data-testid="TotalCost-Currency"]');

  // Navigation
  readonly continueToPaymentDetailsButton: Locator = this.page.locator('div[data-testid="TotalCost-Container"] button[data-testid="submitButton"]');
  readonly backToYourDetailsLabel: Locator = this.page.locator('div[data-testid="backToPageContainer"] p');
  readonly backToYourDetailsImage: Locator = this.page.locator('div[data-testid="backToPageContainer"] svg');

  /** Get the current total cost string from the payment page. */
  async getTotalCost(): Promise<string> {
    console.log('Getting total cost from payment page');
    await this.totalCostValueLabel.waitFor({ state: 'visible', timeout: 30000 });
    await this.totalCostValueLabel.scrollIntoViewIfNeeded();

    const text = (await this.totalCostValueLabel.textContent())?.trim();
    if (!text) {
      throw new Error('PaymentPage: Total cost value was empty or not readable.');
    }

    return text;
  }

  // ######## UI actions/navigation ########

  /** Click the Back to Your Details link. */
  async clickOnBackToYourDetails(): Promise<void> {
    console.log('Click on back to your details label');
    await this.backToYourDetailsLabel.click();
  }

  /**
   * Select a donation amount by matching the label text (e.g. "£3", "£5", "£1").
   * The donation radio buttons use data-testid="radio-box-wrapper_DonationRadio-{index}"
   * with indices: 0 = No donation, 1 = £5, 2 = £3, 3 = £1.
   * This method finds the radio label containing the specified amount text and clicks it.
   *
   * @param amount - The donation amount string to match (e.g. "£3", "£5", "£1")
   */
  async selectDonation(amount: string): Promise<void> {
    console.log(`Selecting donation amount: ${amount}`);
    // Find the donation radio label that contains the amount text
    const donationLabel = this.page
      .locator(`div[data-testid*="radio-box-wrapper_DonationRadio-"] label`)
      .filter({ hasText: amount })
      .first();

    await donationLabel.waitFor({ state: 'visible', timeout: 30000 });
    await donationLabel.scrollIntoViewIfNeeded();
    await donationLabel.click();
  }

  /**
   * Select the "Pay on Arrival" payment option radio button.
   * Waits up to 30s for the option to become visible before clicking.
   */
  async selectPayOnArrival(): Promise<void> {
    console.log('Selecting pay on arrival payment option');
    await this.payOnArrivalOption.waitFor({ state: 'visible', timeout: 30000 });
    await this.payOnArrivalOption.scrollIntoViewIfNeeded();
    await this.payOnArrivalOption.click();
    await expect(this.payOnArrivalOption, 'Pay on arrival payment option should be selected').toHaveAttribute('data-checked', '');
  }

  /**
   * Select the "New Business Account Card" (PIBA) payment type radio button.
   * Waits up to 30s for the option to become clickable before clicking.
   */
  async selectPIBACard(): Promise<void> {
    console.log('Selecting PIBA card payment type');
    const wrapper = this.page.locator('div[data-testid="radio-box-wrapper_payment-type-radio-1"] label');
    await wrapper.waitFor({ state: 'visible', timeout: 30000 });
    await wrapper.scrollIntoViewIfNeeded();
    await wrapper.click();
    await expect(this.newBusinessAccountCardRadio, 'PIBA card payment type should be selected').toBeChecked();
  }

  /**
   * Select the "New Business Account Card" payment type radio button.
   * Alias for selectPIBACard() for backward compatibility.
   */
  async selectNewBusinessAccountCard(): Promise<void> {
    console.log('Selecting new business account card (deprecated, use selectPIBACard)');
    await this.selectPIBACard();
  }

  /**
   * Click "Continue to Payment Details" to proceed to the card entry iframe.
   * Retries the click while the button remains displayed, matching the reference 3CP/payment-initiation handling.
   */
  async clickContinueToPaymentDetails(): Promise<void> {
    console.log('Click on Continue to next step button in order to reach Payment details page');
    const maxRetries = 5;

    await this.continueToPaymentDetailsButton.waitFor({ state: 'visible', timeout: 30000 });
    await expect(this.continueToPaymentDetailsButton, 'Continue to payment details button label should match AEM text')
      .toContainText(await Strings.CONTINUE_TO_PAYMENT_DETAILS.name);
    await this.continueToPaymentDetailsButton.scrollIntoViewIfNeeded();
    await this.continueToPaymentDetailsButton.click();

    for (let retry = 0; retry < maxRetries; retry++) {
      try {
        await expect(this.continueToPaymentDetailsButton, 'Continue to payment details button should disappear after payment initiation')
          .toBeHidden({ timeout: 30000 });
        return;
      } catch (error) {
        if (retry === maxRetries - 1) {
          throw error;
        }
        console.log(`Retry [${retry + 1}/${maxRetries}] clicking Continue button.`);
        await this.continueToPaymentDetailsButton.scrollIntoViewIfNeeded();
        await this.continueToPaymentDetailsButton.click();
      }
    }
  }

  // ######## UI validations ########

  /**
   * Validate that the payment page has loaded by waiting
   * for the page loaded indicator to be visible.
   * Throws a descriptive error if the indicator is not found within 30s.
   */
  async validatePage(): Promise<void> {
    console.log('Validating payment page loaded');
    try {
      await this.pageLoadedIndicator.waitFor({ state: 'visible', timeout: 30000 });
    } catch {
      throw new Error(
        `PaymentPage did not load: page loaded indicator [data-testid="CardDetails-Container"] was not visible within 30s`
      );
    }
  }

  /** Validate the Back to Your Details controls and their relative position. */
  async validateBackToYourDetailsElement(): Promise<void> {
    console.log('Validate Back to Your Details controls');
    await expect(this.backToYourDetailsLabel, 'Payment page: Back to your details label').toBeVisible();
    await expect(this.backToYourDetailsImage, 'Payment page: Back to your details image').toBeVisible();
    await UiUtils.validateIsLeftOf({
      leftElement: this.backToYourDetailsImage,
      rightElement: this.backToYourDetailsLabel,
      elementDescription: 'Back to your details',
    });
    await expect(this.backToYourDetailsLabel, 'Back to your details label text').toHaveText(await Strings.BACK_TO_YOUR_DETAILS.name);
  }

  /** Validate that the total cost display has updated to include the donation. */
  async validateTotalCostWithDonation(expectedTotal: string): Promise<void> {
    console.log(`Validating total cost with donation: ${expectedTotal}`);
    await this.totalCostValueLabel.scrollIntoViewIfNeeded();
    await expect(this.totalCostValueLabel, `Total cost should contain ${expectedTotal}`).toContainText(expectedTotal, { timeout: 30000 });
  }

  /** Validate whether the total cost changed after an action. */
  async validateTotalCostIsUpdated(oldValue: string, isUpdated: boolean = true): Promise<void> {
    console.log(`Validating total cost is ${isUpdated ? 'updated' : 'unchanged'}`);
    if (isUpdated) {
      await expect(async () => {
        const currentTotal = await this.getTotalCost();
        expect(currentTotal, `Total cost should change from ${oldValue}`).not.toBe(oldValue);
      }, `Total cost should eventually change from ${oldValue}`).toPass({ timeout: 30000 });
      return;
    }
    const currentTotal = await this.getTotalCost();
    expect(currentTotal, `Total cost should remain ${oldValue}`).toBe(oldValue);
  }

  /** Validate total cost amount and currency code. */
  async validateTotalCostAmountAndCurrency(expectedTotal: string, currencyCode: string): Promise<void> {
    console.log(`Validating total cost: ${expectedTotal} (${currencyCode})`);
    const totalCostText = await this.getTotalCost();
    const expectedSymbol = currencyCode === 'GBP' ? '£' : '€';
    expect(totalCostText, `Total cost should contain currency symbol: ${expectedSymbol}`).toContain(expectedSymbol);
    expect(totalCostText, `Total cost should contain amount: ${expectedTotal}`).toContain(expectedTotal.replace(/[£€]/g, ''));
  }

  /** Validate the booking summary sits to the right of payment options. */
  async validateBookingSummaryComponentPosition(): Promise<void> {
    console.log('Validate booking summary component position');
    await UiUtils.validateIsLeftOf({
      leftElement: this.paymentOption.cardDetailsContainer,
      rightElement: this.verticalStripSection.bookingSummaryWrapper,
      maxDistanceBetween: 1100,
      elementDescription: 'Vertical strip section position on page',
    });
  }
}
