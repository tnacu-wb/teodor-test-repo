import { CustomerCurrentInvoice } from './customerCurrentInvoice';

/**
 * {
    "data": {
        "viewCustomerInvoicesV2": {
            "response": {
                "invoices": [
                    {
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
                ]
            },
            "pagingResult": {
                "toRecord": 1,
                "totalRecordCount": 1,
                "fromRecord": 1,
                "lastPage": 1
            }
        }
    }
}
 */
export class CustomerInvoices {
  [key: string]: unknown;
  fromRecord?: number;
  invoices: CustomerCurrentInvoice[] = [];
  lastPage?: number;
  toRecord?: number;
  totalRecordCount?: number;

  /**
   * Invoices constructor
   * @param data data object
   * @param data.customerCurrentInvoice invoice object
   */
  constructor(data: { customerCurrentInvoice?: Record<string, unknown> } = {}) {
    const customerCurrentInvoice = data.customerCurrentInvoice ?? {};
    const response = (customerCurrentInvoice.response ?? {}) as Record<string, unknown>;
    const pagingResult = (customerCurrentInvoice.pagingResult ?? {}) as Record<string, unknown>;
    const invoices = Array.isArray(response.invoices) ? (response.invoices as Array<Record<string, unknown>>) : [];
    this.invoices = invoices.map((currentInvoice) => new CustomerCurrentInvoice({ invoice: currentInvoice }));
    this.toRecord = pagingResult.toRecord as number | undefined;
    this.fromRecord = pagingResult.fromRecord as number | undefined;
    this.totalRecordCount = pagingResult.totalRecordCount as number | undefined;
    this.lastPage = pagingResult.lastPage as number | undefined;
  }

  static fromResponse(data: { customerCurrentInvoice?: Record<string, unknown> }): CustomerInvoices {
    return new CustomerInvoices(data);
  }

}
