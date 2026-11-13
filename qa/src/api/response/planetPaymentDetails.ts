import { PaymentOptions } from '../../test-data/paymentOptions';
import { EncryptionUtils } from '../../utils/encryptionUtils';

/** API model for the current TypeScript test framework. */
export class PlanetPaymentDetails {
  [key: string]: unknown;
  booking?: { type?: string; [key: string]: unknown };
  bookingReference?: string;
  createdOn?: string;
  payment?: {
    amount?: { minorUnits?: number; currency?: string };
    billing?: { firstName?: string; lastName?: string; [key: string]: unknown };
    [key: string]: unknown;
  };
  paymentId?: string;
  paymentStatus?: string;
  providerResponse?: { threecResponse?: { providerStatus?: string; fraudCheckDecision?: string; [key: string]: unknown }; [key: string]: unknown };
  refunded?: boolean;
  revisedSolution?: boolean;

  static readonly STATUS_SUCCESS = 'SUCCESS';

  static readonly SUCCESSFUL_PROVIDER_STATUSES = ['CS', 'CQ', 'AA'];
  static readonly FAILURE_PROVIDER_STATUSES = ['AD', 'RE', 'AR', '3E', '00', 'AE', 'AX', '3D', '3P', '3U'];

  /**
   * Payment constructor
   * @param data object data
   * @param data.paymentDetails paymentDetails
   */
  constructor(data: { paymentDetails?: Record<string, unknown> } = {}) {
    const paymentDetails = data.paymentDetails ?? {};
    this.paymentId = paymentDetails.paymentId as string | undefined;
    this.providerResponse = paymentDetails.providerResponse as PlanetPaymentDetails['providerResponse'];
    this.booking = paymentDetails.booking as PlanetPaymentDetails['booking'];
    this.payment = paymentDetails.payment as PlanetPaymentDetails['payment'];
    this.createdOn = paymentDetails.createdOn as string | undefined;
    this.refunded = paymentDetails.refunded as boolean | undefined;
    this.bookingReference = paymentDetails.bookingReference as string | undefined;
    this.paymentStatus = paymentDetails.paymentStatus as string | undefined;
    this.revisedSolution = paymentDetails.revisedSolution as boolean | undefined;
  }

  static fromResponse(data: { paymentDetails?: Record<string, unknown> }): PlanetPaymentDetails {
    return new PlanetPaymentDetails(data);
  }

  /**
   * Validate paymentStatus
   * @param paymentStatus expected payment status
   */
  async validatePaymentStatus(paymentStatus?: string): Promise<void> {
    console.log(`Validate paymentStatus is ${paymentStatus}`);

    if (this.paymentStatus !== paymentStatus) {
      throw new Error('paymentStatus is not as expected');
    }
  }

  /**
   * Validate refunded status
   * @param isRefunded refunded status
   */
  async validateRefundStatus(isRefunded?: boolean): Promise<void> {
    console.log(`Validate refunded is ${isRefunded}`);

    if (this.refunded !== isRefunded) {
      throw new Error('refunded status is not as expected');
    }
  }

  /**
   * Validate payment option
   * @param paymentOption expected payment option
   */
  async validatePaymentOption(paymentOption?: string): Promise<void> {
    console.log(`Validate paymentOption is ${paymentOption}`);

    const expectedApiPaymentOption = paymentOption ? PaymentOptions.getPaymentOptionApiMapping(paymentOption) : undefined;
    if (this.booking?.type !== expectedApiPaymentOption) {
      throw new Error('paymentOption not as expected');
    }
  }

  /**
   * Validate amount
   * @param expectedAmount expected amount as it is displayed on Opera UI pages
   */
  async validateAmount(expectedAmount: number): Promise<void> {
    console.log(`Validate amount is ${expectedAmount}`);

    const expectedMinorUnits = expectedAmount * 100;
    if (this.payment?.amount?.minorUnits !== Number.parseInt(expectedMinorUnits.toFixed(), 10)) {
      throw new Error('amount is not as expected');
    }
  }

  /**
   * Validate currency
   * @param expectedCurrency expected currency
   */
  async validateCurrency(expectedCurrency?: string): Promise<void> {
    console.log(`Validate currency is ${expectedCurrency}`);

    if (this.payment?.amount?.currency !== expectedCurrency) {
      throw new Error('currency is not as expected');
    }
  }

  /**
   * Validate providerStatus is successful
   */
  async validateProviderStatusIsSuccessful(): Promise<void> {
    console.log('Validate provider status');

    const providerStatus = this.providerResponse?.threecResponse?.providerStatus;
    if (!providerStatus || !PlanetPaymentDetails.SUCCESSFUL_PROVIDER_STATUSES.includes(providerStatus)) {
      throw new Error(`Provider status '${providerStatus}' is not in successful statuses`);
    }
  }

  /**
   * Validate Fraud Check against planet payment response
   * @param fraudCheckResponse expected response
   */
  async validateFraudCheck(fraudCheckResponse?: string): Promise<void> {
    console.log('Validate fraud check status');

    const fraudCheckDecision = this.providerResponse?.threecResponse?.fraudCheckDecision;
    if (fraudCheckResponse !== fraudCheckDecision) {
      throw new Error(`Fraud Check ${fraudCheckResponse} is not as expected ${fraudCheckDecision}`);
    }
  }

  /**
   * Validate card holder name against planet payment response
   * @param expectedFirstName cardholder expected FirstName
   * @param expectedLastName cardholder expected LastName
   */
  async validateCardholderName(expectedFirstName?: string, expectedLastName?: string): Promise<void> {
    console.log('Validate cardholder first name and last name');

    const firstName = EncryptionUtils.decode(this.payment?.billing?.firstName ?? '');
    const lastName = EncryptionUtils.decode(this.payment?.billing?.lastName ?? '');

    if (expectedFirstName !== firstName) {
      throw new Error(`Fraud Check expected first name ${expectedFirstName} is not as expected ${firstName}`);
    }
    if (expectedLastName !== lastName) {
      throw new Error(`Fraud Check expected last name ${expectedLastName} is not as expected ${lastName}`);
    }
  }

}
