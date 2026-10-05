import { expect, type FrameLocator, type Locator } from '@playwright/test';
import { ApiBasketCalls } from '../../api/graphql/apiBasketCalls';
import { Basket } from '../../api/response/basket';
import { type CardDetails, Cards } from '../../test-data/cards';
import { Constants } from '../../test-data/constants';
import { Strings } from '../../test-data/strings';
import { PaymentOptionsComponent } from '../../components/shared/payment/paymentOptions.component';
import { PaymentVerticalStripSectionComponent } from '../../components/shared/payment/paymentVerticalStripSection.component';
import { UiUtils } from '../../utils/uiUtils';
import { BasePage } from './base.page';

export interface PaymentDetailsFlow {
  switchToPaymentDetailsIFrame(): Promise<FrameLocator>;
  validatePage(): Promise<void>;
  setPaymentDetails(card: CardDetails, isBACCard?: boolean): Promise<void>;
  clickOnConfirmBookingButton(): Promise<void>;
}

export interface ThreeDSecureFlow {
  confirmPayment(): Promise<void>;
  declinePayment(): Promise<void>;
}

export interface PaymentHotel {
  countryCode?: string;
  type?: { name: string };
}

/** Shared payment page lifecycle for Opera booking variants. */
export abstract class PaymentPageBase extends BasePage {
  // ######## UI elements/properties ########

  readonly url = 'payment';
  readonly paymentOptions = new PaymentOptionsComponent();
  readonly verticalStripSection = new PaymentVerticalStripSectionComponent();
  readonly pageLoadedIndicator: Locator = this.page.locator('div[data-testid="CardDetails-Container"]');
  readonly backToYourDetailsLabel: Locator = this.page.locator('div[data-testid="backToPageContainer"] p');
  readonly backToYourDetailsImage: Locator = this.page.locator('div[data-testid="backToPageContainer"] svg');
  readonly continueToPaymentDetailsButton: Locator = this.page.locator('div[data-testid="TotalCost-Container"] button[data-testid="submitButton"]');

  /** Select the variant-specific card type before payment initiation. */
  protected async selectBusinessAccountCard(): Promise<void> {}

  /** Select the variant-specific new credit/debit card before payment initiation. */
  protected async selectNewCreditDebitCard(): Promise<void> {}

  // ######## UI actions/navigation ########

  /** Open the localized payment path and validate its loaded state. */
  async open(path = this.url): Promise<void> {
    await this.openLocalizedPath(path);
    await this.validatePage();
  }

  /** Click the link back to guest details. */
  async clickOnBackToYourDetails(): Promise<void> {
    console.log('Click on back to your details label');
    await this.backToYourDetailsLabel.click();
  }

  /** Confirm a booking while preserving basket, prod, iframe, and 3DS guards. */
  async confirmCurrentBooking(data: {
    paymentOptionLabel?: string | null;
    card?: CardDetails;
    hotel?: PaymentHotel;
    basketReferenceId?: string;
    isPaymentOptionDisplayed?: boolean;
    paymentDetailsPage: PaymentDetailsFlow;
    paymentSixCardSolution3dSecureHostPage: ThreeDSecureFlow;
    isBAC?: boolean;
  }): Promise<void> {
    console.log('Confirm current booking');
    let paymentOptionLabel = data.paymentOptionLabel === undefined ? await Strings.PAY_ON_ARRIVAL.name : data.paymentOptionLabel;
    const environment = (global.browser.options as unknown as { environment?: string }).environment;
    if (environment === 'prod') throw new Error("The prod tests shouldn't confirm booking!");
    if (!data.paymentDetailsPage || !data.paymentSixCardSolution3dSecureHostPage) {
      throw new Error('paymentDetails page object instance and paymentSixCardSolution3dSecureHost page object instances for current project are required');
    }
    if (data.hotel?.countryCode === Constants.IRELAND_COUNTRY_CODE) {
      paymentOptionLabel = await Strings.PAY_ON_ARRIVAL.name;
      console.log(`Payment option available for Ireland hotels is ${paymentOptionLabel}`);
    }
    if (data.isBAC) {
      await this.paymentOptions.selectPaymentOptionByLabel(paymentOptionLabel ?? undefined);
      await this.selectBusinessAccountCard();
    } else if ((data.hotel?.type?.name !== 'HUB') || (data.isPaymentOptionDisplayed ?? true)) {
      await this.selectNewCreditDebitCard();
      await this.paymentOptions.selectPaymentOptionByLabel(paymentOptionLabel ?? undefined);
    }

    if (paymentOptionLabel === await Strings.RESERVE_WITHOUT_CREDIT_CARD.name) {
      if (data.basketReferenceId) {
        const basket = await ApiBasketCalls.graphqlGetBasketByBasketReference(data.basketReferenceId);
        await basket.validateStatusAndPaymentDetailsBeforePaymentInitiation();
      }
      await this.continueToPaymentDetailsButton.click();
      return;
    }

    await this.continueToPaymentDetailsButton.click();
    await data.paymentDetailsPage.switchToPaymentDetailsIFrame();
    await data.paymentDetailsPage.validatePage();
    if (data.basketReferenceId) {
      const basket = await ApiBasketCalls.graphqlGetBasketByBasketReference(data.basketReferenceId);
      await basket.validateStatusAndPaymentDetailsAfterPaymentInitiation(Basket.STATUS_PAY_PENDING);
    }
    await data.paymentDetailsPage.setPaymentDetails(data.card ?? Cards.VISA_CARD, data.isBAC);
    await data.paymentDetailsPage.clickOnConfirmBookingButton();
    await data.paymentSixCardSolution3dSecureHostPage.confirmPayment();
  }

  // ######## UI validations ########

  /** Decline a booking through the payment iframe and 3DS host. */
  async declineCurrentBooking(data: {
    paymentOptionLabel?: string | null;
    card?: CardDetails;
    basketReferenceId?: string;
    paymentDetailsPage: PaymentDetailsFlow;
    paymentSixCardSolution3dSecureHostPage: ThreeDSecureFlow;
    isBAC?: boolean;
  }): Promise<void> {
    console.log('Decline current booking');
    const paymentOptionLabel = data.paymentOptionLabel === undefined ? await Strings.PAY_ON_ARRIVAL.name : data.paymentOptionLabel;
    const environment = (global.browser.options as unknown as { environment?: string }).environment;
    if (environment === 'prod') throw new Error('The prod is not configured for payment declines!');
    if (!data.paymentDetailsPage || !data.paymentSixCardSolution3dSecureHostPage) {
      throw new Error('paymentDetails page object instance and paymentSixCardSolution3dSecureHost page object instances for current project are required');
    }
    if (data.isBAC) throw new Error('BAC should not be selected for payment declines testing!');
    if (paymentOptionLabel === await Strings.RESERVE_WITHOUT_CREDIT_CARD.name) {
      throw new Error('Reserve without card should not be selected for payment declines testing!');
    }
    await this.continueToPaymentDetailsButton.click();
    await data.paymentDetailsPage.switchToPaymentDetailsIFrame();
    await data.paymentDetailsPage.validatePage();
    if (data.basketReferenceId) {
      const basket = await ApiBasketCalls.graphqlGetBasketByBasketReference(data.basketReferenceId);
      await basket.validateStatusAndPaymentDetailsAfterPaymentInitiation(Basket.STATUS_PAY_PENDING);
    }
    await data.paymentDetailsPage.setPaymentDetails(data.card ?? Cards.VISA_CARD, false);
    await data.paymentDetailsPage.clickOnConfirmBookingButton();
    await data.paymentSixCardSolution3dSecureHostPage.declinePayment();
  }

  /** Validate the payment page loaded indicator. */
  async validatePage(): Promise<void> {
    console.log('Validate Payment page was reached');
    await expect(this.pageLoadedIndicator, 'Payment page loaded indicator').toBeVisible({ timeout: 120000 });
  }

  /** Validate the Back to your details controls. */
  async validateBackToYourDetailsElement(): Promise<void> {
    console.log('Validate Back to your details is displayed correctly');
    await expect(this.backToYourDetailsLabel, 'Back to your details label').toBeVisible();
    await expect(this.backToYourDetailsImage, 'Back to your details image').toBeVisible();
    await UiUtils.validateIsLeftOf({
      leftElement: this.backToYourDetailsImage,
      rightElement: this.backToYourDetailsLabel,
      elementDescription: 'Back to your details',
    });
    await expect(this.backToYourDetailsLabel, 'Back to your details label text').toHaveText(await Strings.BACK_TO_YOUR_DETAILS.name);
  }
}