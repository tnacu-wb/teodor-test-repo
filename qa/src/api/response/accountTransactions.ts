import { Currency } from './currency';
import { TransactionLineItem } from './transactionLineItem';

/**
 * {
    "data": {
        "viewAccountTransactions": {
            "response": {
                "transactions": [
                    {
                        "transactionDate": "2025-10-03T13:42:00",
                        "grossAmount": {
                            "amount": 160.6,
                            "currencyCode": "GBP",
                            "currencySymbol": "\u00a3"
                        },
                        "location": "Glasgow (Paisley)",
                        "pan": "30895001*******6551",
                        "cardName": "Opera Testing",
                        "purchaseOrderReference": "HR",
                        "customerOwnRef": "782226",
                        "lineItems": [
                            {
                                "grossAmount": {
                                    "amount": 5,
                                    "currencyCode": "GBP",
                                    "currencySymbol": "\u00a3"
                                },
                                "description": "Daily Record - Saturday",
                                "guestName": "Opera Testing",
                                "invoiceLineItem": 4,
                                "quantity": 1
                            }
                        ]
                    }
                ]
            }
        }
    }
}
 */
export class AccountTransactions {
  [key: string]: unknown;
  cardName?: string;
  customerOwnRef?: string;
  grossAmount?: Currency;
  lineItems: TransactionLineItem[] = [];
  location?: string;
  pan?: string;
  purchaseOrderReference?: string;
  transactionDate?: string;

  /**
   * Transactions constructor from spending and reporting
   * @param data object data
   * @param data.accountTransactions transactions object
   */
  constructor(data: { accountTransactions?: Record<string, unknown> } = {}) {
    const accountTransactions = data.accountTransactions ?? {};
    this.transactionDate = accountTransactions.transactionDate as string | undefined;
    this.grossAmount = new Currency({ currency: accountTransactions.grossAmount });
    this.location = accountTransactions.location as string | undefined;
    this.pan = accountTransactions.pan as string | undefined;
    this.cardName = accountTransactions.cardName as string | undefined;
    this.purchaseOrderReference = accountTransactions.purchaseOrderReference as string | undefined;
    this.customerOwnRef = accountTransactions.customerOwnRef as string | undefined;
    const lineItems = Array.isArray(accountTransactions.lineItems) ? (accountTransactions.lineItems as Array<Record<string, unknown>>) : [];
    this.lineItems = lineItems.map((lineItem) => new TransactionLineItem({ transactionLineItem: lineItem }));
  }

  static fromResponse(data: { accountTransactions?: Record<string, unknown> }): AccountTransactions {
    return new AccountTransactions(data);
  }

}
