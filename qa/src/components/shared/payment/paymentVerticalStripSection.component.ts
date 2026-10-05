import { type Page, type Locator, expect } from '@playwright/test';
import { Constants } from '@test-data/constants';
import { Strings } from '@test-data/strings';
import { type BookingInformation } from '@api/response/bookingInformation';
import { PaymentBookingSummaryHotelInformationSectionComponent } from './bookingSummaryHotelInformationSection.component';
import { PaymentBookingSummaryRateInformationSectionComponent } from './bookingSummaryRateInformationSection.component';
import { PaymentBookingSummaryRoomInformationSectionComponent } from './bookingSummaryRoomInformationSection.component';
import { PaymentBookingSummaryStayDatesSectionComponent } from './bookingSummaryStayDatesInformationSection.component';
import { PaymentBookingSummaryTotalCostSectionComponent } from './bookingSummaryTotalCostSection.component';

/**
 * The vertical strip on the Payment page containing the UI elements, custom actions and
 * validations. Mirrors qa/reference `components/common/payment/paymentVerticalStripSection.js`.
 */
export class PaymentVerticalStripSectionComponent {
  private readonly page: Page = global.page;

  private get resolutionId(): string {
    return Constants.RESOLUTION_IDENTIFIER_FOR_DATATESTID_ATTRIBUTE;
  }

  // ######## UI elements/properties ########

  get bookingSummaryWrapper(): Locator {
    return this.page.locator(`div[data-testid="BookingSummary-${this.resolutionId}-Wrapper"]`);
  }

  get mobileVariantExpandButton(): Locator {
    return this.page.locator('[data-testid="BookingSummary-MobileVariant-ExpandButton"]');
  }

  get notificationInfoIconList(): Locator { return this.page.locator('[data-testid="BookingSummary-InfoMessages"] [data-testid="Alert"], [data-testid="BookingSummary-MobileVariant-HotelInformation-InfoMessages"] [data-testid="Alert"]'); }
  get notificationInfoTitleLabelList(): Locator { return this.page.locator('[data-testid="BookingSummary-InfoMessages"] [data-testid="AlertTitle"], [data-testid="BookingSummary-MobileVariant-HotelInformation-InfoMessages"] [data-testid="AlertTitle"]'); }
  get notificationInfoDescriptionLabelList(): Locator { return this.page.locator('[data-testid="BookingSummary-InfoMessages"] [data-testid="AlertDescription"], [data-testid="BookingSummary-MobileVariant-HotelInformation-InfoMessages"] [data-testid="AlertDescription"]'); }

  readonly termsAndConditionsPaymentLabel: Locator = this.page.locator('div[data-testid="termsAndConditionsPayment"] > p');
  readonly termsAndConditionsPaymentLink: Locator = this.page.locator('div[data-testid="termsAndConditionsPayment"] a');
  readonly continueToPaymentDetailsButton: Locator = this.page.locator('div[data-testid*="BookingSummary"][data-testid*="Wrapper"] ~ * button[data-testid="submitButton"]');

  // UI components

  readonly bookingSummaryHotelInformationSection: PaymentBookingSummaryHotelInformationSectionComponent = new PaymentBookingSummaryHotelInformationSectionComponent();
  readonly bookingSummaryRateInformationSection: PaymentBookingSummaryRateInformationSectionComponent = new PaymentBookingSummaryRateInformationSectionComponent();
  readonly bookingSummaryRoomInformationSection: PaymentBookingSummaryRoomInformationSectionComponent = new PaymentBookingSummaryRoomInformationSectionComponent();
  readonly bookingSummaryStayDatesSection: PaymentBookingSummaryStayDatesSectionComponent = new PaymentBookingSummaryStayDatesSectionComponent();
  readonly bookingSummaryTotalCostSection: PaymentBookingSummaryTotalCostSectionComponent = new PaymentBookingSummaryTotalCostSectionComponent();

  // ######## UI actions/navigation ########

  /** Expand the booking summary on mobile if the expand button is displayed. */
  async expandOnMobile(): Promise<void> {
    console.log('Expand Booking Summary Section on Mobile Version');
    const isVisible = await this.mobileVariantExpandButton.isVisible().catch(() => false);
    if (isVisible) {
      await this.mobileVariantExpandButton.click();
    }
  }

  /** Click Continue to Payment Details. */
  async clickContinueToPaymentDetailsButton(): Promise<void> {
    console.log('Click on the continue to payment details button');
    await this.continueToPaymentDetailsButton.scrollIntoViewIfNeeded();
    await this.continueToPaymentDetailsButton.click();
  }

  /** Expand the booking summary on mobile using the source-compatible name. */
  async expandBookingSummarySectionOnMobile(): Promise<void> { await this.expandOnMobile(); }

  // ######## UI validations ########

  /** Validate the booking summary wrapper is displayed. */
  async validateBookingSummaryIsDisplayed(): Promise<void> {
    console.log('Validate if the Booking Summary component exists');
    await expect(this.bookingSummaryWrapper, 'Payment booking summary wrapper').toBeVisible();
  }

  /** Validate the continue button label and display state. */
  async validateContinueToPaymentDetailsButton(expectedFlag: boolean): Promise<void> {
    console.log(`Validate continue to payment button displayed=${expectedFlag}`);
    if (expectedFlag) {
      await expect(this.continueToPaymentDetailsButton, 'Continue to payment details button').toBeVisible();
      await expect(this.continueToPaymentDetailsButton, 'Continue button label').toContainText(await Strings.CONTINUE_TO_PAYMENT_DETAILS.name);
    } else {
      await expect(this.continueToPaymentDetailsButton, 'Continue to payment details button').toBeHidden();
    }
  }

  /** Validate the terms and conditions text and link. */
  async validateTermsAndConditionDetails(termsAndConditions: { text: string }): Promise<void> {
    console.log('Validate terms and conditions details');
    const expectedText = termsAndConditions.text.replace(/<[^>]+>/g, '').replace(/&nbsp;/g, ' ').trim();
    await expect(this.termsAndConditionsPaymentLabel, 'Terms and conditions text').toContainText(expectedText);
    const expectedLink = termsAndConditions.text.split('"')[1];
    if (expectedLink) await expect(this.termsAndConditionsPaymentLink, 'Terms and conditions link').toHaveAttribute('href', expectedLink);
  }

  /** Validate the number and visibility of booking-summary notifications. */
  async validateInfoMessages(infoMessages: Array<unknown>): Promise<void> {
    console.log(`Validate ${infoMessages.length} booking summary info messages`);
    if (infoMessages.length > 0) await this.validateNotificationItems();
    else await expect(this.notificationInfoIconList, 'Booking summary info messages').toBeHidden();
  }

  /** Validate notification items displayed under the booking summary. */
  async validateNotificationItems(hasVerticalStripNotificationMessages = true): Promise<void> {
    console.log(`Validate booking summary notification items displayed=${hasVerticalStripNotificationMessages}`);
    if (!hasVerticalStripNotificationMessages) {
      await expect(this.notificationInfoIconList, 'Notification icons').toBeHidden();
      await expect(this.notificationInfoDescriptionLabelList, 'Notification descriptions').toBeHidden();
      return;
    }
    await expect(this.notificationInfoIconList, 'Notification icon count').toHaveCount(await this.notificationInfoDescriptionLabelList.count());
    await expect(this.notificationInfoDescriptionLabelList, 'Notification descriptions').not.toHaveCount(0);
    for (let index = 0; index < await this.notificationInfoDescriptionLabelList.count(); index++) {
      await expect(this.notificationInfoDescriptionLabelList.nth(index), `Notification info description ${index}`).toBeVisible();
    }
  }

  /** Validate the complete vertical strip against booking information. */
  async validateData(bookingInformation: BookingInformation, basketReferenceId: string): Promise<void> {
    console.log(`Validate vertical strip against ${JSON.stringify(bookingInformation)}`);
    await this.validateBookingSummaryIsDisplayed();
    await this.bookingSummaryTotalCostSection.validateData(bookingInformation);
    await this.bookingSummaryRateInformationSection.validateData(bookingInformation);
    await this.bookingSummaryStayDatesSection.validateData(bookingInformation);
    await this.bookingSummaryRoomInformationSection.validateData(bookingInformation, basketReferenceId);
  }
}
