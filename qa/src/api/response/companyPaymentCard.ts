import { CompanyBillingAddress } from './companyBillingAddress';

/**
 * response example:
 *{
     {
        "cardId": "COPC_ef0e30d7-5a8d-4190-9ffd-d8e9100cb92d",
        "cardLabel": "Amex",
        "cardType": "AX",
        "nameOnCard": "Amex",
        "cardNumber": "************6665",
        "startDate": null,
        "expiryDate": "01/26",
        "issueNumber": null,
        "cardToken": "3492404973517136665",
        "billingAddress": {
            "addressLine1": "1428 Stratford Road",
            "addressLine2": "Hall Green",
            "addressLine3": "",
            "addressLine4": "BIRMINGHAM",
            "addressLine5": "",
            "postCode": "B28 9ES",
            "country": "GB"
        },
        "cardNotPresentRequired": false
    },
 *}
 */
export class CompanyPaymentCard {
  [key: string]: unknown;
  billingAddress?: CompanyBillingAddress;
  cardId?: string;
  cardLabel?: string;
  cardNotPresentRequired?: boolean;
  cardNumber?: string;
  cardToken?: string;
  cardType?: string;
  expiryDate?: string;
  issueNumber?: string | null;
  nameOnCard?: string;
  startDate?: string | null;

  /**
   * Company Payment Card Constructor
   * @param data object data
   * @param data.companyPaymentCard payment card data
   */
  constructor(data: { companyPaymentCard?: Record<string, unknown> } = {}) {
    const companyPaymentCard = data.companyPaymentCard ?? {};
    this.cardId = (companyPaymentCard.cardId as string) || '';
    this.cardLabel = (companyPaymentCard.cardLabel as string) || '';
    this.cardType = (companyPaymentCard.cardType as string) || '';
    this.nameOnCard = (companyPaymentCard.nameOnCard as string) || '';
    this.cardNumber = (companyPaymentCard.cardNumber as string) || '';
    this.startDate = (companyPaymentCard.startDate as string) || null;
    this.expiryDate = (companyPaymentCard.expiryDate as string) || '';
    this.issueNumber = (companyPaymentCard.issueNumber as string) || null;
    this.cardToken = (companyPaymentCard.cardToken as string) || '';
    this.billingAddress = new CompanyBillingAddress((companyPaymentCard.billingAddress as Record<string, unknown>) || {});
    this.cardNotPresentRequired = (companyPaymentCard.cardNotPresentRequired as boolean) || false;
  }

  static fromResponse(data: { companyPaymentCard?: Record<string, unknown> }): CompanyPaymentCard {
    return new CompanyPaymentCard(data);
  }

  /**
   * Validate Card Not Present Required value
   * @param isCardNotPresent true if card not present required, false otherwise
   */
  async validateCardNotPresentRequired(isCardNotPresent = false): Promise<void> {
    console.log('Validate Card Not Present Required from company payment card');
    if (this.cardNotPresentRequired !== isCardNotPresent) {
      throw new Error(`Card Not Present Required=${this.cardNotPresentRequired} should be=${isCardNotPresent}`);
    }
  }

}
