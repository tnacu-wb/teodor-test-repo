/**
 * Payment methods from API response
 * {
 *   "data": {
 *      "paymentMethods": [
 *          {
 *              "name": "CARD",
 *              "type": "NEW_CARD",
 *              "order": 1,
 *              "enabled": true,
 *              "cnpPreSelected": false,
 *              "cnpOptionAvailable": false,
 *              "acceptedCardTypes": [
 *                   {
 *                      "type": "MC",
 *                      "name": "Mastercard Credit",
 *                      "logoUrl": "/content/dam/global/booking/Mastercard.jpg"
 *                  }, {
 *                      "type": "AX",
 *                      "name": "American Express",
 *                      "logoUrl": "/content/dam/global/booking/AX.jpg"
 *                  }, {
 *                      "type": "DN",
 *                      "name": "Diners Club",
 *                       "logoUrl": "/content/dam/global/booking/DClub.jpg"
 *                  }, {
 *                      "type": "VS",
 *                      "name": "Visa Debit",
 *                      "logoUrl": "/content/dam/global/booking/Visa_Debit.jpg"
 *                  }, {
 *                      "type": "VS",
 *                      "name": "Electron",
 *                      "logoUrl": "/content/dam/global/booking/Electron_white_v.jpg"
 *                  }, {
 *                      "type": "MA",
 *                      "name": "Maestro",
 *                      "logoUrl": "/content/dam/global/booking/maestro.jpg"
 *                  }, {
 *                      "type": "MC",
 *                      "name": "Mastercard Debit",
 *                      "logoUrl": "/content/dam/global/booking/MD.jpg"
 *                  }, {
 *                      "type": "VS",
 *                      "name": "Visa Credit",
 *                      "logoUrl": "/content/dam/global/booking/VC.jpg"
 *                  }
 *              ],
 *              "card": null,
 *              "paymentOptions": [
 *                  {
 *                      "type": "PAY_NOW",
 *                      "order": 1,
 *                      "enabled": true
 *                  }, {
 *                      "type": "PAY_ON_ARRIVAL",
 *                      "order": 2,
 *                      "enabled": true
 *                  }
 *              ],
 *              "reasons": []
 *          }
 *      ]
 *  }
 * }
 */
export class PaymentMethod {
  [key: string]: unknown;
  acceptedCardTypes?: Array<{ type?: string; name?: string; logoUrl?: string }>;
  card?: unknown;
  cnpOptionAvailable?: boolean;
  cnpPreSelected?: boolean;
  enabled?: boolean;
  name?: string;
  order?: number;
  paymentOptions?: Array<{ type?: string; order?: number; enabled?: boolean }>;
  reasons?: unknown[];
  type?: string;

  /**
   * PaymentMethod constructor
   * @param data object data
   * @param data.paymentMethod paymentMethod
   */
  constructor(data: { paymentMethod?: Record<string, unknown> } = {}) {
    const paymentMethod = data.paymentMethod ?? {};
    this.name = paymentMethod.name as string | undefined;
    this.type = paymentMethod.type as string | undefined;
    this.order = paymentMethod.order as number | undefined;
    this.enabled = paymentMethod.enabled as boolean | undefined;
    this.cnpPreSelected = paymentMethod.cnpPreSelected as boolean | undefined;
    this.cnpOptionAvailable = paymentMethod.cnpOptionAvailable as boolean | undefined;
    this.acceptedCardTypes = paymentMethod.acceptedCardTypes as PaymentMethod['acceptedCardTypes'];
    this.card = paymentMethod.card;
    this.paymentOptions = paymentMethod.paymentOptions as PaymentMethod['paymentOptions'];
    this.reasons = paymentMethod.reasons as unknown[] | undefined;
  }

  static fromResponse(data: { paymentMethod?: Record<string, unknown> }): PaymentMethod {
    return new PaymentMethod(data);
  }
}
