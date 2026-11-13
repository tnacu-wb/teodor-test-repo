/**
 * response example:
 *{
 *    "data": { 
 *        "accountCompany": { 
 *            "name": "Softvision", 
 *            "number": "1234567", 
 *            "address": "London", 
 *            "postCode": "BS1" 
 *        } 
 *    } 
 *}
 */
export class AccountCompanyDetails {
  [key: string]: unknown;
  address?: string;
  name?: string;
  number?: string;
  postCode?: string;

  /**
   * AccountCompanyDetails constructor
   * @param data object data
   * @param data.accountCompany accountCompany
   */
  constructor(data: { accountCompany?: Record<string, unknown> } = {}) {
    const accountCompany = data.accountCompany ?? {};
    this.name = accountCompany.name as string | undefined;
    this.number = accountCompany.number as string | undefined;
    this.address = accountCompany.address as string | undefined;
    this.postCode = accountCompany.postCode as string | undefined;
  }

  static fromResponse(data: { accountCompany?: Record<string, unknown> }): AccountCompanyDetails {
    return new AccountCompanyDetails(data);
  }
}
