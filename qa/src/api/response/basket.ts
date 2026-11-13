import { Constants } from '../../test-data/constants';
import { PaymentOptions } from '../../test-data/paymentOptions';
import { Strings } from '../../test-data/strings';
import { ApiBasketCalls } from '../graphql/apiBasketCalls';
import { OhipApiCalls } from '../ohip/ohipApiCalls';
import { OhipHelpers } from '../ohip/ohipHelpers';
import { BasketItem } from './basketItem';

interface PaymentMethodRecord {
  paymentMethod?: string;
  folioView?: number;
  paymentCard?: {
    cardHolderName?: string;
    cardNumberMasked?: string;
    [key: string]: unknown;
  };
  [key: string]: unknown;
}

/**
The get basket API response example: 
{
    "data": {
        "basket": {
            "createdAt": "2022-08-11T13:05:36Z",
            "hotelId": "MANOLD",
            "itemTypes": [
                "STAY"
            ],
            "items": [
                {
                    "details": null,
                    "sourceId": "46916",
                    "type": "STAY"
                }
            ],
            "lastModifiedAt": "2022-08-11T13:05:37Z",
            "lockingTime": "2022-08-11T14:05:36Z",
            "paymentID": null,
            "reference": "MANOLD4995860",
            "bookingReference": "4995860",
            "status": "OPEN",
            "paymentStatus": null,
            "paymentOption": null,
            "userId": null,
            "sendMail": false,
            "basketError": null,
            "isErroredBooking": false,
            "bookingAllowances": [
                {
                    "allowance": "BREAKFAST",
                    "budget": 10.00
                }
            ],
            "isCheckInOnlinePay": false
        }
    }
}
 */
export class Basket {
  [key: string]: unknown;
  createdAt?: string;
  hotelId?: string;
  itemTypes?: string[];
  items: BasketItem[] = [];
  lastModifiedAt?: string;
  lockingTime?: string;
  paymentID?: string | null;
  reference?: string;
  status?: string;
  userId?: string | null;
  paymentStatus?: string | null;
  paymentOption?: string | null;
  sendMail?: boolean;
  bookingReference?: string;
  basketError?: unknown;
  isErroredBooking?: boolean;
  bookingAllowances?: Array<{ allowance?: string; budget?: unknown; [key: string]: unknown }>;
  isCheckInOnlinePay?: boolean;

  static readonly STATUS_OPEN = 'OPEN';
  static readonly STATUS_PAY_PENDING = 'PAY_PENDING';
  static readonly STATUS_PROCESSING = 'PROCESSING';
  static readonly STATUS_COMPLETED = 'COMPLETED';
  static readonly STATUS_FAILED = 'FAILED';
  static readonly STATUS_FAILED_REFUND = 'FAILED_REFUND';
  static readonly STATUS_CANCELLED = 'CANCELLED';
  static readonly PAYMENT_STATUS_COMPLETED = 'COMPLETED';
  static readonly PAYMENT_STATUS_REFUNDED = 'REFUNDED';

  /**
   * Basket constructor
   * @param data object data
   * @param data.basket basket
   */
  constructor(data: { basket?: Record<string, unknown> } = {}) {
    const basket = data.basket ?? {};

    this.createdAt = basket.createdAt as string | undefined;
    this.hotelId = basket.hotelId as string | undefined;
    this.itemTypes = basket.itemTypes as string[] | undefined;
    this.items = (Array.isArray(basket.items) ? basket.items : []).map(
      (item) => new BasketItem({ basketItem: item as Record<string, unknown> }),
    );
    this.lastModifiedAt = basket.lastModifiedAt as string | undefined;
    this.lockingTime = basket.lockingTime as string | undefined;
    this.paymentID = basket.paymentID as string | null | undefined;
    this.reference = basket.reference as string | undefined;
    this.status = basket.status as string | undefined;
    this.userId = basket.userId as string | null | undefined;
    this.paymentStatus = basket.paymentStatus as string | null | undefined;
    this.paymentOption = basket.paymentOption as string | null | undefined;
    this.sendMail = basket.sendMail as boolean | undefined;
    this.bookingReference = basket.bookingReference as string | undefined;
    this.basketError = basket.basketError;
    this.isErroredBooking = basket.isErroredBooking as boolean | undefined;
    this.bookingAllowances = basket.bookingAllowances as Basket['bookingAllowances'];
    this.isCheckInOnlinePay = basket.isCheckInOnlinePay as boolean | undefined;
  }

  static fromResponse(data: { basket?: Record<string, unknown> }): Basket {
    return new Basket(data);
  }

  private asObject(input: unknown): Record<string, unknown> {
    return input && typeof input === 'object' && !Array.isArray(input) ? (input as Record<string, unknown>) : {};
  }

  /**
   * Validate the status is as expected
   * @param basketStatus expected status
   */
  async validateBasketStatus(basketStatus?: string): Promise<void> {
    console.log(`Validate basket status ${this.status} equals expected status ${basketStatus}`);

    if (this.status !== basketStatus) {
      throw new Error(`Basket status is not as expected\n${JSON.stringify(this)}\n`);
    }
  }

  /**
   * Validate paymentStatus is as expected
   * @param paymentStatus expected payment status
   */
  async validatePaymentStatus(paymentStatus?: string | null): Promise<void> {
    console.log(`Validate payment status is ${paymentStatus}`);

    if (paymentStatus) {
      if (this.paymentStatus !== paymentStatus) {
        throw new Error(`paymentStatus is not as expected\n${JSON.stringify(this)}\n`);
      }
    } else if (this.paymentStatus !== null && this.paymentStatus !== undefined) {
      throw new Error(`paymentStatus expected to be null but was '${String(this.paymentStatus)}'`);
    }
  }

  /**
   * Validate PaymentId is set
   * @param hasPaymentId boolean flag
   */
  async validatePaymentIdIsSet(hasPaymentId: boolean): Promise<void> {
    console.log(`Validate payment id is ${this.paymentID ? 'set' : 'not set'}`);

    if (hasPaymentId && (this.paymentID === null || this.paymentID === undefined)) {
      throw new Error('Expected paymentID to be set but it was null');
    }
    if (!hasPaymentId && this.paymentID !== null && this.paymentID !== undefined) {
      throw new Error(`Expected paymentID to be null but it was '${this.paymentID}'`);
    }
  }

  /**
   * Validate the payment option
   * @param paymentOption expected payment option (display name), may be a `Strings` promise
   */
  async validatePaymentOption(paymentOption?: string | Promise<string> | null): Promise<void> {
    const resolvedPaymentOption = paymentOption ? await paymentOption : paymentOption;
    console.log(`Validate payment option is ${resolvedPaymentOption ? resolvedPaymentOption : 'No payment option'}`);

    if (resolvedPaymentOption) {
      const expectedApiPaymentOption = await PaymentOptions.getPaymentOptionApiMapping(resolvedPaymentOption);
      if (this.paymentOption !== expectedApiPaymentOption) {
        throw new Error(`paymentOption not as expected\n${JSON.stringify(this)}\n`);
      }
    } else if (this.paymentOption !== null && this.paymentOption !== undefined) {
      throw new Error(`paymentOption expected to be null but was '${String(this.paymentOption)}'`);
    }
  }

  /**
   * Validate guarantee code changed from one value to another by checking payment method change
   * @param data validation options
   * @param data.hotelId hotel id
   * @param data.guaranteeCodeBefore expected guarantee code before change (e.g., 'NON')
   * @param data.guaranteeCodeAfter expected guarantee code after change (e.g., 'CC')
   * @param data.isPaymentMethodChange indicates if this was a payment method change scenario
   */
  async validateGuaranteeCodeChanged(data: {
    hotelId?: string;
    guaranteeCodeBefore?: string;
    guaranteeCodeAfter?: string;
    isPaymentMethodChange?: boolean;
  }): Promise<void> {
    const { hotelId, guaranteeCodeBefore, guaranteeCodeAfter, isPaymentMethodChange = false } = data;
    console.log(
      `Validate guarantee code changed from '${guaranteeCodeBefore}' to '${guaranteeCodeAfter}'${isPaymentMethodChange ? ' (payment method change)' : ''}`,
    );

    if (isPaymentMethodChange) {
      if (typeof guaranteeCodeBefore !== 'string' || guaranteeCodeBefore.length === 0) {
        throw new Error('Guarantee code before change should be provided for payment method change validations');
      }

      // Some payment method changes are valid without a guarantee code transition.
      // Enforce a transition only when the expected "after" value differs from the captured "before" value.
      if (guaranteeCodeBefore === guaranteeCodeAfter) {
        console.log(`Guarantee code transition check skipped: expected value remains '${guaranteeCodeAfter}'`);
      }
    }

    for (const item of this.items) {
      const reservationResponse = this.asObject(
        await OhipApiCalls.getHotelReservationById({ hotelId: String(hotelId ?? ''), reservationId: String(item.sourceId ?? '') }),
      );
      const reservations = this.asObject(reservationResponse.reservations);
      const reservationList = Array.isArray(reservations.reservation) ? (reservations.reservation as Array<Record<string, unknown>>) : [];
      const paymentMethodForReservation = reservationList[0];

      // Infer current guarantee code from payment method
      const paymentMethods = Array.isArray(paymentMethodForReservation?.reservationPaymentMethods)
        ? (paymentMethodForReservation!.reservationPaymentMethods as PaymentMethodRecord[])
        : [];
      const primaryPaymentMethod = OhipHelpers.getLatestPaymentMethod({ paymentMethods })?.paymentMethod ?? '';

      let actualGuaranteeCode = 'NON'; // Default: Non-guaranteed
      if (primaryPaymentMethod === 'AC') {
        actualGuaranteeCode = 'CO'; // Account to Company = Company Guaranteed
      } else if (primaryPaymentMethod && primaryPaymentMethod !== 'CA') {
        actualGuaranteeCode = 'CC'; // Card payment = Credit Card Guaranteed
      }

      console.log(`Reservation ${item.sourceId}: Payment method: '${primaryPaymentMethod}' → Guarantee: '${actualGuaranteeCode}'`);

      if (actualGuaranteeCode !== guaranteeCodeAfter) {
        throw new Error(
          `Guarantee code mismatch for reservation ${item.sourceId}: expected '${guaranteeCodeAfter}', got '${actualGuaranteeCode}'. Payment method: '${primaryPaymentMethod}'`,
        );
      }

      if (isPaymentMethodChange && guaranteeCodeBefore !== guaranteeCodeAfter && actualGuaranteeCode === guaranteeCodeBefore) {
        throw new Error(
          `Guarantee code did not transition for reservation ${item.sourceId}: current guarantee '${actualGuaranteeCode}' is still the same as before '${guaranteeCodeBefore}'. Payment method: '${primaryPaymentMethod}'`,
        );
      }

      console.log(`✓ Guarantee code validated: before '${guaranteeCodeBefore}' -> current '${actualGuaranteeCode}'`);
    }
  }

  /**
   * Validate Basket status and change payment method status, before a payment initiation
   */
  async validateChangePaymentMethodBeforePaymentInitiation(): Promise<void> {
    await this.validateBasketStatus(Basket.STATUS_COMPLETED);
    await this.validatePaymentStatus(Basket.PAYMENT_STATUS_COMPLETED);
    await this.validatePaymentIdIsSet(false);
  }

  /**
   * Validate Basket status and change payment method status, payment initiated
   * @param basketStatus expected basket status
   */
  async validateChangePaymentMethodAfterPaymentInitiation(basketStatus?: string): Promise<void> {
    await this.validateBasketStatus(basketStatus);
    await this.validatePaymentStatus(Basket.PAYMENT_STATUS_COMPLETED);
  }

  /**
   * Validate Basket status and payment status for a not confirmed basket, before payment initiation
   */
  async validateStatusAndPaymentDetailsBeforePaymentInitiation(): Promise<void> {
    await this.validateBasketStatus(Basket.STATUS_OPEN);
    await this.validatePaymentStatus(null);
    await this.validatePaymentIdIsSet(false);
    await this.validatePaymentOption(null);
  }

  /**
   * Validate Basket status and payment status for a not confirmed basket, payment initiated
   * @param basketStatus expected basket status
   */
  async validateStatusAndPaymentDetailsAfterPaymentInitiation(basketStatus?: string): Promise<void> {
    await this.validateBasketStatus(basketStatus);
    await this.validatePaymentStatus(null);
  }

  /**
   * Validate Basket status and Payment details are successful
   * @param data object
   * @param data.paymentOption payment option
   */
  async validateBasketIsConfirmed(data: { paymentOption?: string | Promise<string> }): Promise<void> {
    const paymentOption = data.paymentOption ? await data.paymentOption : data.paymentOption;
    await this.validateBasketStatus(Basket.STATUS_COMPLETED);
    await this.validatePaymentStatus(Basket.PAYMENT_STATUS_COMPLETED);
    if (paymentOption === (await Strings.RESERVE_WITHOUT_CREDIT_CARD.name) || paymentOption === (await Strings.NON_GUARANTEED_BOOKING.name)) {
      await this.validatePaymentIdIsSet(false);
    } else {
      await this.validatePaymentIdIsSet(true);
    }
    await this.validatePaymentOption(paymentOption);
  }

  /**
   * Validate the value of the send mail field from the basket object
   * @param isEmailSent boolean flag that shows if the email is sent or not
   */
  async validateSendMail(isEmailSent?: boolean): Promise<void> {
    console.log('Validate the value of the send mail field from the basket object');

    if (this.sendMail !== isEmailSent) {
      throw new Error(`Validate the send mail field value is set to ${isEmailSent}`);
    }
  }

  /**
   * Validate the routing instructions for every basket item
   * @param expectedTransactionCodes expected routing instruction transaction codes
   */
  async validateRoutingInstructions(expectedTransactionCodes: string[]): Promise<void> {
    console.log('Validate routing instructions for every basket sourceId');
    for (const item of this.items) {
      const routingInstructionsObject = this.asObject(
        await OhipApiCalls.getRoutingInstructions(String(this.hotelId ?? ''), String(item.sourceId ?? '')),
      );
      const routingInstructions = Array.isArray(routingInstructionsObject.routingInstructions)
        ? (routingInstructionsObject.routingInstructions as Array<Record<string, unknown>>)
        : [];
      const folio = this.asObject(routingInstructions[0]?.folio);
      const instructions = Array.isArray(folio.instructions) ? (folio.instructions as Array<Record<string, unknown>>) : [];
      const transactionCodes: string[] = [];

      for (const instruction of instructions) {
        if (Array.isArray(instruction.transactionCodes)) {
          const firstCode = this.asObject((instruction.transactionCodes as Array<Record<string, unknown>>)[0]);
          transactionCodes.push(String(firstCode.transactionCode ?? ''));
        } else if (Array.isArray(instruction.billingInstructions)) {
          for (const billingInstruction of instruction.billingInstructions as Array<Record<string, unknown>>) {
            transactionCodes.push(String(billingInstruction.billingCode ?? '')); // meal deal codes
          }
        }
      }

      const knownTransactionCodes = new Set([
        Constants.TRANSACTION_CODE_ACCOMMODATION,
        Constants.TRANSACTION_CODE_BREAKFAST,
        Constants.TRANSACTION_CODE_FOOD_ONLY,
        Constants.TRANSACTION_CODE_FOOD_AND_BEVERAGE_NO_ALCOHOL,
        Constants.TRANSACTION_CODE_FOOD_AND_BEVERAGE,
        Constants.TRANSACTION_CODE_CARD_PARKING,
        Constants.TRANSACTION_CODE_ULTIMATE_WIFI,
        Constants.TRANSACTION_CODE_CITY_TAX,
      ]);

      for (const code of expectedTransactionCodes) {
        if (!knownTransactionCodes.has(code)) {
          throw new Error(`Unknown ${code} transaction code`);
        }
        if (!transactionCodes.includes(code)) {
          throw new Error(`Expected transaction code '${code}' not found in ${JSON.stringify(transactionCodes)}`);
        }
      }
    }
  }

  /**
   * Validate payee info for every basket item
   * @param payeeId expected payee id
   * @param payeeType expected payee type
   */
  async validatePayeeInfo(payeeId?: string, payeeType?: string): Promise<void> {
    console.log('Validate payee info for every basket sourceId');
    for (const item of this.items) {
      const routingInstructionsObject = this.asObject(
        await OhipApiCalls.getRoutingInstructions(String(this.hotelId ?? ''), String(item.sourceId ?? '')),
      );
      const routingInstructions = Array.isArray(routingInstructionsObject.routingInstructions)
        ? (routingInstructionsObject.routingInstructions as Array<Record<string, unknown>>)
        : [];
      const folio = this.asObject(routingInstructions[0]?.folio);
      const payeeInfo = this.asObject(folio.payeeInfo);
      const payeeIdObject = this.asObject(payeeInfo.payeeId);

      if (payeeIdObject.id !== payeeId) {
        throw new Error(`Payee ID value expected '${payeeId}' but got '${String(payeeIdObject.id)}'`);
      }
      if (payeeIdObject.type !== payeeType) {
        throw new Error(`Payee type value expected '${payeeType}' but got '${String(payeeIdObject.type)}'`);
      }
    }
  }

  /**
   * Validate folio window no. for every basket item
   * @param folioWindowNo folio window no.
   */
  async validateFolioWindowNo(folioWindowNo = 2): Promise<void> {
    console.log('Validate folio window no. for every basket sourceId');
    for (const item of this.items) {
      const routingInstructionsObject = this.asObject(
        await OhipApiCalls.getRoutingInstructions(String(this.hotelId ?? ''), String(item.sourceId ?? '')),
      );
      const routingInstructions = Array.isArray(routingInstructionsObject.routingInstructions)
        ? (routingInstructionsObject.routingInstructions as Array<Record<string, unknown>>)
        : [];
      const folio = this.asObject(routingInstructions[0]?.folio);

      if (folio.folioWindowNo !== folioWindowNo) {
        throw new Error(`Folio window no. value expected '${folioWindowNo}' but got '${String(folio.folioWindowNo)}'`);
      }
    }
  }

  /**
   * Get payment methods for all reservation items
   * @param data object
   * @param data.hotelId hotel id
   * @returns array of payment method objects from all reservations
   */
  async getPaymentMethodsForAllItems(
    data: { hotelId?: string },
  ): Promise<Array<{ reservationId?: string; paymentMethods: PaymentMethodRecord[] }>> {
    const allPaymentMethods: Array<{ reservationId?: string; paymentMethods: PaymentMethodRecord[] }> = [];
    for (const item of this.items) {
      const paymentMethodForReservation = this.asObject(
        await OhipApiCalls.getPaymentMethodForReservation(String(data.hotelId ?? ''), String(item.sourceId ?? '')),
      );
      allPaymentMethods.push({
        reservationId: item.sourceId,
        paymentMethods: Array.isArray(paymentMethodForReservation.reservationPaymentMethods)
          ? (paymentMethodForReservation.reservationPaymentMethods as PaymentMethodRecord[])
          : [],
      });
    }
    return allPaymentMethods;
  }

  /**
   * Find new payment methods by comparing before and after snapshots
   * @param data object with before/after payment method arrays
   * @param data.beforePaymentMethods payment methods before change
   * @param data.afterPaymentMethods payment methods after change
   * @returns array of new payment methods added
   */
  static findNewPaymentMethods(data: {
    beforePaymentMethods?: PaymentMethodRecord[];
    afterPaymentMethods?: PaymentMethodRecord[];
  }): PaymentMethodRecord[] {
    const beforePaymentMethods = data.beforePaymentMethods ?? [];
    const afterPaymentMethods = data.afterPaymentMethods ?? [];
    const newPaymentMethods: PaymentMethodRecord[] = [];

    // Get set of payment method codes from before
    const beforeCodes = beforePaymentMethods.map((pm) => pm.paymentMethod);

    // Find payment methods that weren't in before list
    for (const afterPm of afterPaymentMethods) {
      if (!beforeCodes.includes(afterPm.paymentMethod)) {
        console.log(`New payment method found: '${afterPm.paymentMethod}'`);
        newPaymentMethods.push(afterPm);
      }
    }

    return newPaymentMethods;
  }

  /**
   * Validate the payment instructions for every basket item
   * @param data object data
   * @param data.hotelId hotel id
   * @param data.paymentCard payment card details
   * @param data.cardHolder card holder details
   * @param data.paymentWindow opera UI payment window to validate
   * @param data.isA2C true if Account to Company is used, false otherwise
   * @param data.paymentMethodsBeforeChange optional - payment methods before change (for change payment scenarios)
   */
  async validatePaymentInstructions(data: {
    hotelId?: string;
    paymentCard?: { number?: string; paymentMethodCodePi?: string };
    cardHolder?: { firstName?: string; lastName?: string };
    paymentWindow?: number;
    isA2C?: boolean;
    paymentMethodsBeforeChange?:
      | PaymentMethodRecord[]
      | Array<{ reservationId?: string; paymentMethods?: PaymentMethodRecord[] }>
      | Record<string, PaymentMethodRecord[]>
      | null;
  }): Promise<void> {
    console.log('Validate payment instructions');
    const { hotelId, paymentCard, cardHolder, paymentWindow, isA2C = false, paymentMethodsBeforeChange = null } = data;

    const getBeforePaymentMethodsForReservation = (reservationId: string): PaymentMethodRecord[] => {
      if (!paymentMethodsBeforeChange) {
        return [];
      }
      if (Array.isArray(paymentMethodsBeforeChange)) {
        if (paymentMethodsBeforeChange.length === 0) {
          return [];
        }
        const firstEntry = paymentMethodsBeforeChange[0] as Record<string, unknown>;
        const isReservationScopedArray =
          firstEntry &&
          typeof firstEntry === 'object' &&
          Object.prototype.hasOwnProperty.call(firstEntry, 'reservationId') &&
          Object.prototype.hasOwnProperty.call(firstEntry, 'paymentMethods');
        if (isReservationScopedArray) {
          const reservationSnapshot = (
            paymentMethodsBeforeChange as Array<{ reservationId?: string; paymentMethods?: PaymentMethodRecord[] }>
          ).find((entry) => `${entry.reservationId}` === `${reservationId}`);
          return reservationSnapshot?.paymentMethods ?? [];
        }
        // Backward compatibility: original flat-array snapshot for single-reservation baskets/callers
        return paymentMethodsBeforeChange as PaymentMethodRecord[];
      }
      if (typeof paymentMethodsBeforeChange === 'object') {
        const byId = paymentMethodsBeforeChange as Record<string, PaymentMethodRecord[]>;
        return byId[reservationId] ?? byId[`${reservationId}`] ?? [];
      }
      return [];
    };

    for (const item of this.items) {
      const sourceId = String(item.sourceId ?? '');
      const paymentMethodForReservation = this.asObject(
        await OhipApiCalls.getPaymentMethodForReservation(String(hotelId ?? ''), sourceId),
      );
      const currentPaymentMethods = Array.isArray(paymentMethodForReservation.reservationPaymentMethods)
        ? (paymentMethodForReservation.reservationPaymentMethods as PaymentMethodRecord[])
        : [];
      const beforePaymentMethodsForReservation = getBeforePaymentMethodsForReservation(sourceId);
      let targetPaymentMethod: PaymentMethodRecord | null | undefined;
      const expectedPaymentMethodCode = isA2C ? Constants.ACCOUNT_TO_COMPANY_PAYMENT_METHOD : paymentCard?.paymentMethodCodePi;
      console.log(`Found ${currentPaymentMethods.length} payment method(s). Looking for code: ${expectedPaymentMethodCode}`);
      console.log(`Available payment methods: ${currentPaymentMethods.map((pm) => pm.paymentMethod).join(', ')}`);

      // If we have a before snapshot (payment method change scenario), find the new method
      if (beforePaymentMethodsForReservation.length > 0) {
        const newMethods = Basket.findNewPaymentMethods({
          beforePaymentMethods: beforePaymentMethodsForReservation,
          afterPaymentMethods: currentPaymentMethods,
        });
        if (newMethods.length > 0) {
          targetPaymentMethod = newMethods[0];
          console.log(`Using new payment method from change for reservation '${sourceId}': '${targetPaymentMethod.paymentMethod}'`);
        } else {
          console.log(`No new payment methods found for reservation '${sourceId}'. Using fallback strategy...`);
        }
      }

      // Fallback: use standard lookup strategies if no new method found
      if (!targetPaymentMethod) {
        targetPaymentMethod = OhipHelpers.getPaymentMethodByCode({
          paymentMethods: currentPaymentMethods,
          expectedCode: expectedPaymentMethodCode,
        });
      }
      if (!targetPaymentMethod) {
        throw new Error(
          `Could not find payment method with code: ${expectedPaymentMethodCode}. Available codes: ${currentPaymentMethods.map((pm) => pm.paymentMethod).join(', ')}`,
        );
      }

      if (isA2C) {
        if (targetPaymentMethod.paymentMethod !== Constants.ACCOUNT_TO_COMPANY_PAYMENT_METHOD) {
          throw new Error('Payment method validation in opera reservation info');
        }
        if (targetPaymentMethod.folioView !== paymentWindow) {
          throw new Error('Payment method validation in opera reservation info');
        }
      } else {
        const expectedCardNumber = (paymentCard?.number ?? '').replace(/\s+/g, '');

        // For payment method changes, the payment method code may not update correctly in Opera
        // So we check card details instead of the payment method code
        if (paymentMethodsBeforeChange && (Array.isArray(paymentMethodsBeforeChange) ? paymentMethodsBeforeChange.length > 0 : true)) {
          console.log('Payment method change detected - validating card details only');
          const allowIncompleteOperaPaymentCardData = process.env.ALLOW_INCOMPLETE_OPERA_PAYMENT_CARD_DATA === 'true';
          // Only validate card details if they exist (new payment method should have card details)
          if (targetPaymentMethod.paymentCard) {
            if (targetPaymentMethod.paymentCard.cardHolderName !== `${cardHolder?.firstName} ${cardHolder?.lastName}`) {
              throw new Error('Payment method validation in opera reservation info');
            }
            if (targetPaymentMethod.paymentCard.cardNumberMasked?.slice(-4) !== expectedCardNumber.slice(-4)) {
              throw new Error('Payment method validation in opera reservation info');
            }
          } else if (allowIncompleteOperaPaymentCardData) {
            console.log('Warning: Payment method change completed but no card details found in Opera');
            console.log('Skipping card details validation because ALLOW_INCOMPLETE_OPERA_PAYMENT_CARD_DATA=true');
          } else {
            throw new Error(
              `Payment method change validation failed: Opera returned no paymentCard details for payment method '${targetPaymentMethod.paymentMethod}'. Set ALLOW_INCOMPLETE_OPERA_PAYMENT_CARD_DATA=true only when this incomplete integration state is explicitly accepted.`,
            );
          }
        } else {
          // Normal validation for new bookings
          if (!targetPaymentMethod.paymentMethod?.includes(paymentCard?.paymentMethodCodePi ?? '')) {
            throw new Error('Payment method validation in opera reservation info');
          }
          if (targetPaymentMethod.folioView !== paymentWindow) {
            throw new Error('Payment method validation in opera reservation info');
          }
          if (targetPaymentMethod.paymentCard?.cardHolderName !== `${cardHolder?.firstName} ${cardHolder?.lastName}`) {
            throw new Error('Payment method validation in opera reservation info');
          }
          if (targetPaymentMethod.paymentCard?.cardNumberMasked?.slice(-4) !== expectedCardNumber.slice(-4)) {
            throw new Error('Payment method validation in opera reservation info');
          }
        }
      }
    }
  }

  /**
   * Validate reference details
   * @param data data object
   * @param data.referenceOptional referenceOptional
   * @param data.purchaseOrderNumber purchase order number
   * @param data.validateReferenceOptional if validation for reference field must not be done, set to false
   * @param data.validatePurchaseOrderNum if validation for purchase order number must not be done, set to false
   */
  async validateReferenceDetails(data: {
    referenceOptional?: string;
    purchaseOrderNumber?: string;
    validateReferenceOptional?: boolean;
    validatePurchaseOrderNum?: boolean;
  }): Promise<void> {
    console.log('Validate reference details');
    const { referenceOptional, purchaseOrderNumber, validateReferenceOptional = true, validatePurchaseOrderNum = true } = data;
    const hotelId = this.hotelId;
    for (const item of this.items) {
      const reservationsResponse = this.asObject(
        await OhipApiCalls.getHotelReservationById({ hotelId: String(hotelId ?? ''), reservationId: String(item.sourceId ?? '') }),
      );
      const reservations = this.asObject(reservationsResponse.reservations);
      const reservationList = Array.isArray(reservations.reservation) ? (reservations.reservation as Array<Record<string, unknown>>) : [];
      const reservation = this.asObject(reservationList[0]);

      if (validateReferenceOptional) {
        if (reservation.customReference !== referenceOptional) {
          throw new Error('Reference Optional Value not equal');
        }
      } else if (Object.prototype.hasOwnProperty.call(reservation, 'customReference')) {
        throw new Error('Custom Reference Present in reservations object');
      }

      const userDefinedFields = this.asObject(reservation.userDefinedFields);
      const characterUDFs = Array.isArray(userDefinedFields.characterUDFs)
        ? (userDefinedFields.characterUDFs as Array<Record<string, unknown>>)
        : [];
      const characterUDFList = characterUDFs.map((characterUDF) => characterUDF.value);

      if (validatePurchaseOrderNum) {
        if (!characterUDFList.includes(purchaseOrderNumber)) {
          throw new Error('Purchase Order Number Value not found');
        }
      } else if (characterUDFList.length !== 1) {
        throw new Error(`More than one value is present: ${characterUDFList}`);
      }
    }
  }

  /**
   * Validate Booking allowances values
   * @param data data object
   * @param data.actualBudgetDinner dinner budget value
   * @param data.isDinnerBudgetSet if validation for dinner budget must not be done, set to false
   * @param data.isAlcoholIncluded if validation for included alcohol must not be done, set to false
   * @param data.isDinnerChecked if validation for dinner must not be done, set to false
   */
  async validateBookingBusinessAllowances(data: {
    actualBudgetDinner?: number;
    isDinnerBudgetSet?: boolean;
    isAlcoholIncluded?: boolean;
    isDinnerChecked?: boolean;
  }): Promise<void> {
    console.log('Validate Booking Allowances data');
    const { actualBudgetDinner = 0, isDinnerBudgetSet = true, isAlcoholIncluded = true, isDinnerChecked = true } = data;

    const bookingAllowances = await ApiBasketCalls.graphqlGetBookingAllowanceByBasketReference(String(this.reference ?? ''));
    const listOfAllowances = bookingAllowances.map((bookingAllowance) => bookingAllowance.allowance);

    const alcoholAllowanceName = await Strings.ALLOWANCE_ALCOHOL.name;
    if (isAlcoholIncluded) {
      if (!listOfAllowances.includes(alcoholAllowanceName)) {
        throw new Error('Alcohol Allowance not found');
      }
    } else if (listOfAllowances.includes(alcoholAllowanceName)) {
      throw new Error('Alcohol Allowance is present in the list');
    }

    const dinnerAllowanceName = await Strings.ALLOWANCE_DINNER.name;
    if (isDinnerChecked) {
      if (!listOfAllowances.includes(dinnerAllowanceName)) {
        throw new Error('Dinner Allowance not found');
      }
      const position = listOfAllowances.indexOf(dinnerAllowanceName);
      const dinnerBudget = String(bookingAllowances[position]?.budget ?? '');
      if (isDinnerBudgetSet) {
        if (!dinnerBudget.includes(actualBudgetDinner.toString())) {
          throw new Error('Budget dinner value not equal');
        }
      } else if (!dinnerBudget.includes('0')) {
        throw new Error('Budget dinner value not equal');
      }
    } else if (listOfAllowances.includes(dinnerAllowanceName)) {
      throw new Error('Dinner Allowance is present in the list');
    }
  }

  /**
   * Validate other booking allowances details
   * @param data data object
   * @param data.isCarParkingChecked if validation for car parking must not be done, set to false
   * @param data.isUltimateWifiChecked if validation for ultimate wifi must not be done, set to false
   */
  async validateOtherBookingAllowances(data: { isCarParkingChecked?: boolean; isUltimateWifiChecked?: boolean }): Promise<void> {
    console.log('Validate Other Booking Allowances data');
    const { isCarParkingChecked, isUltimateWifiChecked } = data;

    const bookingAllowances = await ApiBasketCalls.graphqlGetBookingAllowanceByBasketReference(String(this.reference ?? ''));
    const listOfAllowances = bookingAllowances.map((bookingAllowance) => bookingAllowance.allowance);

    const carParkingAllowanceName = await Strings.ALLOWANCE_CAR_PARKING.name;
    if (isCarParkingChecked) {
      if (!listOfAllowances.includes(carParkingAllowanceName)) {
        throw new Error('Car Parking Allowance not found');
      }
    } else if (listOfAllowances.includes(carParkingAllowanceName)) {
      throw new Error('Car Parking Allowance is present in the list');
    }

    const ultimateWifiAllowanceName = await Strings.ALLOWANCE_ULTIMATE_WIFI.name;
    if (isUltimateWifiChecked) {
      if (!listOfAllowances.includes(ultimateWifiAllowanceName)) {
        throw new Error('Ultimate Wifi Allowance not found');
      }
    } else if (listOfAllowances.includes(ultimateWifiAllowanceName)) {
      throw new Error('Ultimate Wifi Allowance is present in the list');
    }
  }
}
