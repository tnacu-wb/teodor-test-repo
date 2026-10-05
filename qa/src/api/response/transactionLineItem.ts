import { Currency } from './currency';

/**
 * {
    "grossAmount": {
        "amount": 74.4,
        "currencyCode": "GBP",
        "currencySymbol": "\u00a3"
    },
    "description": "Accommodation",
    "guestName": "Opera Testing",
    "invoiceLineItem": 1,
    "quantity": 1
    }
 */
export class TransactionLineItem {
  [key: string]: unknown;
  description?: string;
  grossAmount?: Currency;
  guestName?: string;
  invoiceLineItem?: number;
  quantity?: number;

  /**
   * Transaction Line item constructor from transactions details
   * @param data object data
   * @param data.transactionLineItem transactions line item details object
   */
  constructor(data: { transactionLineItem?: Record<string, unknown> } = {}) {
    const transactionLineItem = data.transactionLineItem ?? {};
    this.description = transactionLineItem.description as string | undefined;
    this.grossAmount = new Currency({ currency: transactionLineItem.grossAmount });
    this.guestName = transactionLineItem.guestName as string | undefined;
    this.invoiceLineItem = transactionLineItem.invoiceLineItem as number | undefined;
    this.quantity = transactionLineItem.quantity as number | undefined;
  }

  static fromResponse(data: { transactionLineItem?: Record<string, unknown> }): TransactionLineItem {
    return new TransactionLineItem(data);
  }
}
