/**
 * Employees from getEmployees
{
    "data": {
        "getAccountList": {
            "accounts": [
                {
                    "accountName": "whibtread Digital",
                    "tetheredGuid": "70ffaa91-c655-4e53-ad34-5381895ac9de",
                    "accountNumber": "3089503200100352",
                    "schemeCustomerId": 782226,
                    "registrationRoles": [
                        "ACCOUNT_HOLDER",
                        "CARD_HOLDER"
                    ]
                }
            ]
        }
    }
}
 
 */
export class Accounts {
  [key: string]: unknown;
  accountName?: string;
  accountNumber?: string;
  registrationRoles?: string[];
  scheme?: string;
  schemeCustomerId?: number;
  tetheredGuid?: string;

  /**
   * Employee constructor
   * @param data data object
   * @param data.account account object
   */
  constructor(data: { account?: Record<string, unknown> } = {}) {
    const account = data.account ?? {};
    this.accountName = account.accountName as string | undefined;
    this.accountNumber = account.accountNumber as string | undefined;
    this.registrationRoles = account.registrationRoles as string[] | undefined;
    this.tetheredGuid = account.tetheredGuid as string | undefined;
    this.scheme = account.scheme as string | undefined;
    this.schemeCustomerId = account.schemeCustomerId as number | undefined;
  }

  static fromResponse(data: { account?: Record<string, unknown> }): Accounts {
    return new Accounts(data);
  }
}
