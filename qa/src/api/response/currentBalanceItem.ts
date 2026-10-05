import { Currency } from './currency';

/**
 * {
    "schemeCustomerId": 784245,
    "tetheredGuid": "70ffaa91-c655-4e53-ad34-5381895ac9de",
    "accountName": "IB Automation Ltd",
    "accountNumber": "3089503200100391",
    "registrationRoles": [
        "ACCOUNT_HOLDER",
        "CARD_HOLDER"
    ],
    "errorCode": null,
    "scheme": "GB",
    "outstanding": {
        "currencySymbol": "\u00a3",
        "currencyCode": "GBP",
        "amount": 0.0
    },
    "newTransactions": {
        "currencySymbol": "\u00a3",
        "currencyCode": "GBP",
        "amount": 0.0
    },
    "available": {
        "currencySymbol": "\u00a3",
        "currencyCode": "GBP",
        "amount": 25000.0
    },
    "creditLimit": {
        "currencySymbol": "\u00a3",
        "currencyCode": "GBP",
        "amount": 25000.0
    },
    "currentBalance": {
        "currencySymbol": "\u00a3",
        "currencyCode": "GBP",
        "amount": 0.0
    },
    "interimPayments": {
        "currencySymbol": "\u00a3",
        "currencyCode": "GBP",
        "amount": 0.0
    }
}
 */
export class CurrentBalanceItem {
  [key: string]: unknown;
  accountName?: string;
  accountNumber?: string;
  available?: Currency;
  creditLimit?: Currency;
  currentBalance?: Currency;
  errorCode?: unknown;
  interimPayments?: Currency;
  newTransactions?: Currency;
  outstanding?: Currency;
  registrationRoles?: string[];
  scheme?: string;
  schemeCustomerId?: number;
  tetheredGuid?: string;

  /**
   * CurrentBalanceItem constructor
   * @param data data object
   * @param data.currentBalanceItem CurrentBalanceItem object
   */
  constructor(data: { currentBalanceItem?: Record<string, unknown> } = {}) {
    const currentBalanceItem = data.currentBalanceItem ?? {};
    this.schemeCustomerId = currentBalanceItem.schemeCustomerId as number | undefined;
    this.tetheredGuid = currentBalanceItem.tetheredGuid as string | undefined;
    this.accountName = currentBalanceItem.accountName as string | undefined;
    this.accountNumber = currentBalanceItem.accountNumber as string | undefined;
    this.registrationRoles = currentBalanceItem.registrationRoles as string[] | undefined;
    this.errorCode = currentBalanceItem.errorCode;
    this.scheme = currentBalanceItem.scheme as string | undefined;
    this.outstanding = new Currency({ currency: currentBalanceItem.outstanding });
    this.newTransactions = new Currency({ currency: currentBalanceItem.newTransactions });
    this.available = new Currency({ currency: currentBalanceItem.available });
    this.creditLimit = new Currency({ currency: currentBalanceItem.creditLimit });
    this.currentBalance = new Currency({ currency: currentBalanceItem.currentBalance });
    this.interimPayments = new Currency({ currency: currentBalanceItem.interimPayments });
  }

  static fromResponse(data: { currentBalanceItem?: Record<string, unknown> }): CurrentBalanceItem {
    return new CurrentBalanceItem(data);
  }
}
