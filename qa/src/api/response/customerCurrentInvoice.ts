import { Currency } from './currency';

/**
 * {
        "statementDate": "2025-07-28",
        "invoiceNo": "414969",
        "broughtForward": {
            "amount": -131.01,
            "currencyCode": "GBP",
            "currencySymbol": "\u00a3"
        },
        "paymentsReceived": {
            "amount": 34,
            "currencyCode": "GBP",
            "currencySymbol": "\u00a3"
        },
        "overdueBalance": {
            "amount": -165.01,
            "currencyCode": "GBP",
            "currencySymbol": "\u00a3"
        },
        "invoiceValue": {
            "amount": 0,
            "currencyCode": "GBP",
            "currencySymbol": "\u00a3"
        },
        "statementBalance": {
            "amount": -165.01,
            "currencyCode": "GBP",
            "currencySymbol": "\u00a3"
        },
        "fileAutoID": 36682
    }
 */
export class CustomerCurrentInvoice {
  [key: string]: unknown;
  broughtForward?: Currency;
  fileAutoID?: number;
  invoiceNo?: string;
  invoiceValue?: Currency;
  overdueBalance?: Currency;
  paymentsReceived?: Currency;
  statementBalance?: Currency;
  statementDate?: string;

  /**
   * Customer current invoice constructor
   * @param data data object
   * @param data.invoice invoice object
   */
  constructor(data: { invoice?: Record<string, unknown> } = {}) {
    const invoice = data.invoice ?? {};
    this.statementDate = invoice.statementDate as string | undefined;
    this.invoiceNo = invoice.invoiceNo as string | undefined;
    this.broughtForward = new Currency({ currency: invoice.broughtForward });
    this.paymentsReceived = new Currency({ currency: invoice.paymentsReceived });
    this.overdueBalance = new Currency({ currency: invoice.overdueBalance });
    this.invoiceValue = new Currency({ currency: invoice.invoiceValue });
    this.statementBalance = new Currency({ currency: invoice.statementBalance });
    this.fileAutoID = invoice.fileAutoID as number | undefined;
  }

  static fromResponse(data: { invoice?: Record<string, unknown> }): CustomerCurrentInvoice {
    return new CustomerCurrentInvoice(data);
  }
}
