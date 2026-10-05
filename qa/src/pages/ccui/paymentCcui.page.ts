import { expect, type Locator } from '@playwright/test';
import { CardHolderNameSectionComponent } from '../../components/ccui/payment/cardHolderNameSection.component';
import { DiscountSectionComponent } from '../../components/ccui/payment/discountSection.component';
import { BookingSummarySectionComponent } from '../../components/ccui/payment/bookingSummarySection.component';
import { TotalCostSectionComponent } from '../../components/ccui/payment/totalCostSection.component';
import { PaymentTypeSectionComponent } from '../../components/ccui/payment/paymentTypeSection.component';
import { ApiBasketCalls, ApiCalls, EntityApiCalls } from '../../api';
import { Basket } from '../../api/response';
import { BillingAddressSectionComponent } from '../../components/ccui/payment/billingAddressSection.component';
import { CardPresentStatusSectionComponent } from '../../components/ccui/payment/cardPresentStatusSection.component';
import { RoomRatePoliciesModalComponent } from '../../components/ccui/payment/roomRatePoliciesModal.component';
import { AccountToCompanyDetailsSectionComponent } from '../../components/ccui/payment/accountToCompanyDetailsSection.component';
import { TypeOfCallerSectionComponent } from '../../components/ccui/payment/typeOfCallerSection.component';
import { BookersReferenceSectionComponent } from '../../components/ccui/payment/bookersReferenceSection.component';
import { BusinessAllowancesSectionComponent } from '../../components/ccui/payment/businessAllowancesSection.component';
import { PaymentBookingSummaryRoomInformationSectionComponent } from '../../components/shared/payment/bookingSummaryRoomInformationSection.component';
import type { CardDetails } from '../../test-data/cards';
import { Strings } from '../../test-data/strings';
import { BasePage } from '../shared/base.page';

/**
 * Payment page from Opera CCUI environment containing the UI elements, custom actions and validations.
 */
export class PaymentPageCcui extends BasePage {
  // ######## properties ########

  readonly url = 'payment';
  readonly title = Strings.PREMIER_INN.data.default;

  // ######## UI elements/properties ########

  readonly paymentTitle: Locator = this.page.locator('//h3[@data-testid="paymentPageSection_title"]');
  readonly cardDetailsContainer: Locator = this.page.locator('//div[@data-testid="CardDetails-RadioContainer"]');
  readonly paymentDescription: Locator = this.page.locator('//h6[@data-testid="paymentPageSection_titleDescription"]');
  readonly payNowRadioButton: Locator = this.page.locator('//span[@data-testid="radio-box-inside_PAY_NOW"]');
  readonly payNowLabel: Locator = this.page.locator('//div[@data-testid="radio-box-wrapper_PAY_NOW"]/label');
  readonly payOnArrivalRadioButton: Locator = this.page.locator('//span[@data-testid="radio-box-inside_PAY_ON_ARRIVAL"]');
  readonly payOnArrivalLabel: Locator = this.page.locator('//div[@data-testid="radio-box-wrapper_PAY_ON_ARRIVAL"]/label');
  readonly launchEckohButton: Locator = this.page.locator('//button[@data-testid="launchEckoh_launchButton"]');
  readonly launchEchoModal: Locator = this.page.locator('//section[@data-testid="launchEckoh-ModalContent"]');
  readonly launchEckohModalCloseButton: Locator = this.page.locator('//button[@data-testid="launchEckoh-ModalCloseButton"]');
  readonly cancelBookingOverlay: Locator = this.page.locator('//section[@data-testid="launchEckoh_cancel_booking-ModalContent"]');
  readonly cancelBookingOverlayTitleLabel: Locator = this.page.locator('//p[@data-testid="launchEckoh_cancel_booking-ModalTitle"]');
  readonly cancelBookingOverlayDescription: Locator = this.page.locator('//div[@data-testid="launchEckoh_cancel_booking-ModalBody"]//div[@class="css-1quiqw3"]');
  readonly cancelBookingOverlayReTryButton: Locator = this.page.locator('//button[@data-testid="launchEckoh_retryButton"]');
  readonly cancelBookingOverlayCancelButton: Locator = this.page.locator('//button[@data-testid="launchEckoh_cancelButton"]');
  readonly cancelBookingConfirmationModal: Locator = this.page.locator('//section[@data-testid="launchEckoh_confirm-ModalContent"]');
  readonly cancelBookingConfirmationModalDescriptionLabel: Locator = this.page.locator('//p[@data-testid="launchEckoh_confirm-ModalTitle"]');
  readonly cancelBookingConfirmationModalRetryButton: Locator = this.page.locator('//button[@data-testid="launchEckoh_confirm_retryButton"]');
  readonly cancelBookingConfirmationModalCancelButton: Locator = this.page.locator('//button[@data-testid="launchEckoh_confirm_cancelButton"]');
  readonly backToDetailsContainer: Locator = this.page.locator('//div[@data-testid="paymentPageSection-backToDetails_back-entire"]');
  readonly backToDetailsArrowContainer: Locator = this.page.locator('//div[@data-testid="paymentPageSection-backToDetails_back-arrow"]');
  readonly backToDetailsHyperlinkLabel: Locator = this.page.locator('//p[@data-testid="paymentPageSection-backToDetails_back-text"]');
  readonly discountAmountInputRegex = /^[0-9]*(\.[0-9]{0,2})?$/;

  readonly discountSection: DiscountSectionComponent = new DiscountSectionComponent();
  readonly totalCostSection: TotalCostSectionComponent = new TotalCostSectionComponent();
  readonly paymentTypeSection: PaymentTypeSectionComponent = new PaymentTypeSectionComponent();
  readonly bookingSummarySection: BookingSummarySectionComponent = new BookingSummarySectionComponent();
  readonly cardHolderName: CardHolderNameSectionComponent = new CardHolderNameSectionComponent();
  readonly cardPresentStatus: CardPresentStatusSectionComponent = new CardPresentStatusSectionComponent();
  readonly billingAddressSection: BillingAddressSectionComponent = new BillingAddressSectionComponent();
  readonly roomRatePoliciesModal: RoomRatePoliciesModalComponent = new RoomRatePoliciesModalComponent();
  readonly accountToCompanyDetailsSection: AccountToCompanyDetailsSectionComponent = new AccountToCompanyDetailsSectionComponent();
  readonly typeOfCaller: TypeOfCallerSectionComponent = new TypeOfCallerSectionComponent();
  readonly bookersReference: BookersReferenceSectionComponent = new BookersReferenceSectionComponent();
  readonly businessAllowances: BusinessAllowancesSectionComponent = new BusinessAllowancesSectionComponent();
  readonly bookingSummaryRoomInformationSection: PaymentBookingSummaryRoomInformationSectionComponent = new PaymentBookingSummaryRoomInformationSectionComponent();

  // ######## UI actions/navigation ########

  /** Click on Pay on arrival option. */
  async clickOnPayOnArrivalOption(): Promise<void> {
    console.log('Click on Pay on arrival option');
    await this.payOnArrivalRadioButton.waitFor({ state: 'visible' });
    await this.payOnArrivalRadioButton.scrollIntoViewIfNeeded();
    await this.payOnArrivalRadioButton.click();
    await expect(this.payOnArrivalRadioButton, 'Pay on arrival method option is selected').toHaveAttribute('data-checked', '');
  }

  /** Click on Pay now option. */
  async clickOnPayNowOption(): Promise<void> {
    console.log('Click on Pay now option');
    await this.payNowRadioButton.scrollIntoViewIfNeeded();
    await this.payNowRadioButton.click();
    await expect(this.payNowRadioButton, 'Pay now method option is selected').toHaveAttribute('data-checked', '');
  }

  /**
   * Loads the page handling cookies and the needed authorization.
   * @param reservationInfo Contains details to open the payment page.
   */
  async open(reservationInfo: unknown): Promise<void> {
    console.log('Open CCUI payment page');
    const reservationId = this.getReservationId(reservationInfo);
    await this.openLocalizedPath(`${this.url}?reservationId=${reservationId}`);
  }

  /**
   * Click on the payment option indicated by paymentOption parameter.
   * @param paymentOption Payment option to click on.
   */
  async clickOnSelectedPaymentOption(paymentOption: string | Promise<string>): Promise<void> {
    const resolvedPaymentOption = await paymentOption;
    console.log(`Select payment option: ${resolvedPaymentOption}`);
    if (resolvedPaymentOption === await Strings.PAY_NOW_CCUI.name) {
      await this.clickOnPayNowOption();
    } else if (resolvedPaymentOption === await Strings.PAY_ON_ARRIVAL_CCUI.name) {
      await this.clickOnPayOnArrivalOption();
    } else {
      throw new Error(`Payment option with value ${resolvedPaymentOption} is not available`);
    }
  }

  /** Click outside the payment options section. */
  async clickOutsidePaymentOptionSection(): Promise<void> {
    console.log('Click outside the payment options section');
    await this.page.mouse.click(0, 0);
  }

  /** Open Eckoh iframe and wait for it to respond successfully. */
  async openEckohIframe(): Promise<void> {
    console.log('Click to open Eckoh iFrame');
    await this.launchEckohButton.waitFor({ state: 'visible' });
    await this.launchEckohButton.scrollIntoViewIfNeeded();
    await this.launchEckohButton.click();
  }

  /** Close Eckoh iframe and wait for it to respond successfully. */
  async closeEckohIframe(): Promise<void> {
    console.log('Close Eckoh iFrame');
    await this.launchEckohModalCloseButton.waitFor({ state: 'visible' });
    await this.launchEckohModalCloseButton.click();
  }

  /**
   * Open cancel booking overlay by selecting New credit/debit card as payment type and a payment option.
   * @param paymentOption Value coming from spec file. Could be pay now/ pay on arrival.
   */
  async openCancelBookingOverlay(paymentOption: string): Promise<void> {
    console.log(`Choose new credit/ debit card as payment type and ${paymentOption} as payment option in order for eckoh to respond with error`);
    await this.clickOnSelectedPaymentOption(paymentOption);
    console.log('Launch eckoh to validate the card and close it to trigger the cancel booking overlay');
    await this.openEckohIframe();
    await this.closeEckohIframe();
  }

  /**
   * Open eckoh overlay, send the success status and close the overlay.
   * @param data Payment webhook bypass data.
   */
  async openEckohIframeAndBypassApiWebhook({ basketReference, card, isPaymentMethodChange = false }: { basketReference: string; card: CardDetails; isPaymentMethodChange?: boolean }): Promise<void> {
    console.log('Launch eckoh and send card details in order for eckoh to respond with success and trigger the cardholder section');
    const basketInitial = await ApiBasketCalls.graphqlGetBasketByBasketReference(basketReference);
    if (isPaymentMethodChange) {
      await basketInitial.validateChangePaymentMethodBeforePaymentInitiation();
    } else {
      await basketInitial.validateStatusAndPaymentDetailsBeforePaymentInitiation();
    }

    if (await this.cardHolderName.cardHolderNameTitleLabel.isVisible().catch(() => false)) {
      return;
    }

    await this.openEckohIframe();
    const paymentID = await this.getEckohPaymentId(basketReference);
    await this.validateEckohStatus(basketReference);
    await EntityApiCalls.postEckohWebhookCallback({ paymentID, card });
    await this.cardHolderName.cardHolderNameTitleLabel.waitFor({ state: 'attached', timeout: 30000 });
    await this.closeEckohIframe();
    const basketAfterPayment = await ApiBasketCalls.graphqlGetBasketByBasketReference(basketReference);
    if (isPaymentMethodChange) {
      await basketAfterPayment.validateChangePaymentMethodAfterPaymentInitiation(Basket.STATUS_PAY_PENDING);
    } else {
      await basketAfterPayment.validateStatusAndPaymentDetailsAfterPaymentInitiation(Basket.STATUS_PAY_PENDING);
    }
    await this.validateEckohStatus(basketReference, false);
  }

  /** Change payment method through Eckoh while preserving the legacy facade. */
  async changePaymentMethodOpenEckohIframeAndBypassApiWebhook(data: { basketReference: string; card: CardDetails }): Promise<void> {
    console.log('Change payment method through Eckoh');
    await this.openEckohIframeAndBypassApiWebhook({ ...data, isPaymentMethodChange: true });
  }

  /**
   * Poll basket data until Eckoh paymentID is created by payment initiation.
   * @param basketReference Basket reference from API.
   * @returns Eckoh payment ID.
   */
  private async getEckohPaymentId(basketReference: string): Promise<string> {
    const retries = 7;
    for (let retry = 0; retry < retries; retry++) {
      const basket = await ApiBasketCalls.graphqlGetBasketByBasketReference(basketReference);
      const paymentID = String((basket as { paymentID?: string | null; paymentId?: string | null }).paymentID ?? (basket as { paymentID?: string | null; paymentId?: string | null }).paymentId ?? '');
      if (paymentID) {
        return paymentID;
      }
      console.log(`Retrying call for paymentId: [${retry + 1}/${retries}]`);
      await this.page.waitForTimeout(2000 * (retry + 1));
    }

    throw new Error('Eckoh paymentID is null');
  }

  /** Click on re-try button from cancel booking overlay. */
  async clickOnReTryButtonFromCancelOverlay(): Promise<void> {
    console.log('Click on re-try button from cancel booking overlay');
    await this.cancelBookingOverlayReTryButton.click();
  }

  /** Click on cancel button from cancel booking overlay. */
  async clickOnCancelButtonFromCancelOverlay(): Promise<void> {
    console.log('Click on cancel button from cancel booking overlay');
    await this.cancelBookingOverlayCancelButton.click();
  }

  /** Click on re-try button from cancel booking confirmation modal. */
  async clickOnRetryButtonFromCancelConfirmationModal(): Promise<void> {
    console.log('Click on re-try button from cancel booking confirmation modal');
    await this.cancelBookingConfirmationModalRetryButton.click();
  }

  /** Click on cancel button from cancel booking confirmation modal. */
  async clickOnCancelButtonFromCancelConfirmationModal(): Promise<void> {
    console.log('Click on cancel button from cancel booking confirmation modal');
    await this.cancelBookingConfirmationModalCancelButton.click();
  }

  /** Click on "back to your details" arrow. */
  async clickOnBackToDetailsArrow(): Promise<void> {
    console.log('Click on back to your details arrow');
    await this.backToDetailsArrowContainer.click();
  }

  /** Click on "back to your details" hyperlink. */
  async clickOnBackToDetailsHyperlink(): Promise<void> {
    console.log('Click on back to your details hyperlink');
    await this.backToDetailsHyperlinkLabel.click();
  }

  /** Click outside of "back to your details" container. */
  async clickOutsideBackToDetailsSection(): Promise<void> {
    console.log('Click outside of back to your details container');
    await this.page.mouse.click(0, 0);
  }

  /** Wait to be redirected from payment page to another one. */
  async waitToBeRedirectedFromPaymentPage(): Promise<void> {
    console.log('Wait to be redirected from payment page');
    await expect(this.page, 'User is not redirected from payment page').not.toHaveURL(/\/payment/);
  }

  /**
   * Validate eckoh status before and after payment process.
   * @param basketReference Basket reference from API.
   * @param isInitial true if status is before processing the payment.
   */
  async validateEckohStatus(basketReference: string, isInitial = true): Promise<void> {
    const eckohStatus = await ApiCalls.getEckohStatus(basketReference);
    console.log(`Validate Eckoh status: ${eckohStatus}`);
    expect(eckohStatus, isInitial ? `Wrong initial Eckoh status: ${eckohStatus}` : `Wrong status after Eckoh process: ${eckohStatus}`).toBe(isInitial ? 'PENDING' : 'SUCCESS');
  }

  // ######## UI validations ########

  /** Validate the elements' visibility and their data. */
  async validateData(): Promise<void> {
    console.log('Validate payment page elements visibility and data');
    await this.validatePaymentDetails();
  }

  /** Validate Payment Details. */
  async validatePaymentDetails(): Promise<void> {
    console.log('Validate payment details elements visibility');
    await expect(this.paymentTitle, 'Payment title label check').toBeVisible();
    await expect(this.paymentTitle, 'Payment title label').toHaveText(await Strings.PAYMENT.name);
    await expect(this.paymentDescription, 'Payment description check').toBeVisible();
    await expect(this.paymentDescription, 'Payment page description').toHaveText(await Strings.PLEASE_CHOOSE_HOW_TO_PAY_FOR_BOOKING.name);
    await this.validatePaymentOptionsLabelElements();
    await this.validateEckohButtonIsDisplayed();
  }

  /**
   * Validate that eckoh button is displayed.
   * @param isDisplayed true -> element is displayed / false -> element not displayed.
   */
  async validateEckohButtonIsDisplayed(isDisplayed = true): Promise<void> {
    console.log('Validate that eckoh button is displayed');
    if (isDisplayed) {
      await expect(this.launchEckohButton, 'Launch Eckoh button').toBeVisible();
      await expect(this.launchEckohButton, 'Launch eckoh button text').toHaveText(await Strings.LAUNCH_ECKOH.name);
    } else {
      await expect(this.launchEckohButton, 'Launch Eckoh button').toBeHidden();
    }
  }

  /** Validate that eckoh modal display and elements. */
  async validateEckohModal(): Promise<void> {
    console.log('Validate that eckoh modal display and elements');
    await expect(this.launchEchoModal, 'Launch Eckoh modal').toBeVisible();
    await expect(this.launchEckohModalCloseButton, 'Launch Eckoh close button').toBeVisible();
  }

  /**
   * Validate that cancel booking overlay was displayed.
   * @param isDisplayed true -> element is displayed / false -> element not displayed.
   */
  async validateCancelBookingOverlayIsOpen(isDisplayed = true): Promise<void> {
    console.log('Validate that cancel booking overlay was displayed');
    if (isDisplayed) {
      await expect(this.cancelBookingOverlay, 'Cancel booking overlay').toBeVisible();
    } else {
      await expect(this.cancelBookingOverlay, 'Cancel booking overlay').toBeHidden();
    }
  }

  /** Validate the title element from cancel booking overlay. */
  async validateCancelBookingOverlayTitle(): Promise<void> {
    console.log('Validate the title element from cancel booking overlay');
    await expect(this.cancelBookingOverlayTitleLabel, 'Overlay title').toHaveText(await Strings.CANCEL_BOOKING_OVERLAY_TITLE.name);
  }

  /** Validate the description element from cancel booking overlay. */
  async validateCancelBookingOverlayDescription(): Promise<void> {
    console.log('Validate the description element from cancel booking overlay');
    await expect(this.cancelBookingOverlayDescription, 'Overlay description').toHaveText(await Strings.CANCEL_BOOKING_OVERLAY_DESCRIPTION.name);
  }

  /** Validate re-try button from cancel booking overlay. */
  async validateRetryButtonFromCancelBookingOverlay(): Promise<void> {
    console.log('Validate re-try button from cancel booking overlay');
    await expect(this.cancelBookingOverlayReTryButton, 'Cancel booking overlay retry button').toHaveText(await Strings.CANCEL_BOOKING_OVERLAY_RETRY_BUTTON.name);
  }

  /** Validate cancel button from cancel booking overlay. */
  async validateCancelButtonFromCancelBookingOverlay(): Promise<void> {
    console.log('Validate cancel button from cancel booking overlay');
    await expect(this.cancelBookingOverlayCancelButton, 'Cancel booking overlay cancel button').toHaveText(await Strings.CANCEL_BOOKING_OVERLAY_CANCEL_BUTTON.name);
  }

  /** Validate that cancel booking overlay displayed when eckoh responds with error. */
  async validateCancelBookingOverlay(): Promise<void> {
    console.log('Validate elements from cancel booking overlay');
    await this.validateCancelBookingOverlayIsOpen();
    await this.validateCancelBookingOverlayTitle();
    await this.validateCancelBookingOverlayDescription();
    await this.validateRetryButtonFromCancelBookingOverlay();
    await this.validateCancelButtonFromCancelBookingOverlay();
  }

  /** Validate payment options elements and labels. */
  async validatePaymentOptionsLabelElements(): Promise<void> {
    console.log('Validate payment options elements and labels');
    await expect(this.payNowRadioButton, 'Pay now radio button').toBeVisible();
    await expect(this.payNowLabel, 'Pay now label').toHaveText(await Strings.PAY_NOW_CCUI.name);
    await expect(this.payOnArrivalRadioButton, 'Pay on arrival radio button').toBeVisible();
    await expect(this.payOnArrivalLabel, 'Pay on arrival label').toHaveText(await Strings.PAY_ON_ARRIVAL_CCUI.name);
  }

  /** Validate pay on arrival is displayed. */
  async validatePayOnArrivalDisplayed(): Promise<void> {
    console.log('Validate pay on arrival is displayed');
    await expect(this.payOnArrivalLabel, 'Pay on arrival').toBeVisible();
  }

  /** Check we reached the current page by checking a specific element from the page. */
  async validatePage(): Promise<void> {
    console.log('Validate CCUI payment page');
    await expect(this.paymentDescription, 'Payment description').toBeVisible();
  }

  private getReservationId(reservationInfo: unknown): string {
    const candidate = reservationInfo as { reservationDetails?: { basketReference?: string }; basketReference?: string };
    return candidate.reservationDetails?.basketReference ?? candidate.basketReference ?? '';
  }
}