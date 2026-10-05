import { CurrentBalanceItem } from './currentBalanceItem';

/**
 * {
    "data": {
        "getAccountBalanceSummary": {
            "currentBalances": [
                {
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
            ],
            "totalRecordCount": 1
        }
    }
}
 */
export class CustomerAccountCurrentBalancesResponse {
  [key: string]: unknown;
  currentBalances: CurrentBalanceItem[] = [];
  totalRecordCount?: number;

  /**
   * CustomerAccountCurrentBalancesResponse constructor
   * @param data data object
   * @param data.customerAccountCurrentBalancesResponse object returned by graphql getAccountBalanceSummary query
   */
  constructor(data: { customerAccountCurrentBalancesResponse?: Record<string, unknown> } = {}) {
    const customerAccountCurrentBalancesResponse = data.customerAccountCurrentBalancesResponse ?? {};
    const currentBalances = Array.isArray(customerAccountCurrentBalancesResponse.currentBalances)
      ? (customerAccountCurrentBalancesResponse.currentBalances as Array<Record<string, unknown>>)
      : [];
    this.currentBalances = currentBalances.map((currentBalance) => new CurrentBalanceItem({ currentBalanceItem: currentBalance }));
    this.totalRecordCount = customerAccountCurrentBalancesResponse.totalRecordCount as number | undefined;
  }

  static fromResponse(data: { customerAccountCurrentBalancesResponse?: Record<string, unknown> }): CustomerAccountCurrentBalancesResponse {
    return new CustomerAccountCurrentBalancesResponse(data);
  }

}
