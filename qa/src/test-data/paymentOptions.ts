import { Constants } from './constants';
import { type StringBase } from './stringBase';
import { Strings } from './strings';

/** Payment option used in Payment page */
export interface PaymentOption {
  name: StringBase;
  buttonState: boolean;
  optionAvailable: boolean;
}

/** Button state and availability combinations for supported payment options. */
export class PaymentOptions {
  private constructor() {}

  static readonly PAY_NOW_OPTION_SELECTED_AND_AVAILABLE: PaymentOption = { name: Strings.PAY_NOW, buttonState: true, optionAvailable: true };
  static readonly PAY_NOW_OPTION_NOT_SELECTED_AND_AVAILABLE: PaymentOption = { name: Strings.PAY_NOW, buttonState: false, optionAvailable: true };
  static readonly PAY_NOW_OPTION_NOT_SELECTED_AND_UNAVAILABLE: PaymentOption = { name: Strings.PAY_NOW, buttonState: false, optionAvailable: false };
  static readonly PAY_ON_ARRIVAL_OPTION_SELECTED_AND_AVAILABLE: PaymentOption = { name: Strings.PAY_ON_ARRIVAL, buttonState: true, optionAvailable: true };
  static readonly PAY_ON_ARRIVAL_OPTION_NOT_SELECTED_AND_AVAILABLE: PaymentOption = { name: Strings.PAY_ON_ARRIVAL, buttonState: false, optionAvailable: true };
  static readonly PAY_ON_ARRIVAL_OPTION_NOT_SELECTED_AND_UNAVAILABLE: PaymentOption = { name: Strings.PAY_ON_ARRIVAL, buttonState: false, optionAvailable: false };
  static readonly RESERVE_WITHOUT_CREDIT_CARD_OPTION_SELECTED_AND_AVAILABLE: PaymentOption = { name: Strings.RESERVE_WITHOUT_CREDIT_CARD, buttonState: true, optionAvailable: true };
  static readonly RESERVE_WITHOUT_CREDIT_CARD_OPTION_NOT_SELECTED_AND_AVAILABLE: PaymentOption = { name: Strings.RESERVE_WITHOUT_CREDIT_CARD, buttonState: false, optionAvailable: true };
  static readonly RESERVE_WITHOUT_CREDIT_CARD_OPTION_NOT_SELECTED_AND_UNAVAILABLE: PaymentOption = { name: Strings.RESERVE_WITHOUT_CREDIT_CARD, buttonState: false, optionAvailable: false };

  /** Payment option mapping for backend/API payload values. */
  static async getPaymentOptionApiMapping(paymentOption: string | Promise<string>): Promise<string | undefined> {
    const resolvedPaymentOption = await paymentOption;
    const paymentOptionsMapping = new Map([
      [await Strings.PAY_NOW.name, Constants.PAYMENT_OPTION.payNow],
      [await Strings.PAY_NOW_CCUI.name, Constants.PAYMENT_OPTION.payNow],
      [await Strings.PAY_ON_ARRIVAL.name, Constants.PAYMENT_OPTION.payOnArrival],
      [await Strings.PAY_ON_ARRIVAL_CCUI.name, Constants.PAYMENT_OPTION.payOnArrival],
      [await Strings.RESERVE_WITHOUT_CREDIT_CARD.name, Constants.PAYMENT_OPTION.reserveWithoutCreditCard],
      [await Strings.NON_GUARANTEED_BOOKING.name, Constants.PAYMENT_OPTION.reserveWithoutCreditCard],
    ]);
    return paymentOptionsMapping.get(resolvedPaymentOption);
  }
}
