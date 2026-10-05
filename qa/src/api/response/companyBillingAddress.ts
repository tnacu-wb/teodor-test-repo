/**
 * Example JSON response:
 * {
 *   "billingAddress": {
 *     "addressLine1": "1428 Stratford Road",
 *     "addressLine2": "Hall Green",
 *     "addressLine3": "",
 *     "addressLine4": "BIRMINGHAM",
 *     "addressLine5": "",
 *     "postCode": "B28 9ES",
 *     "country": "GB"
 *   },
 *}
 */
export class CompanyBillingAddress {
  [key: string]: unknown;
  address1?: string;
  address2?: string;
  address3?: string;
  address4?: string;
  address5?: string;
  addressLine1?: string;
  addressLine2?: string;
  addressLine3?: string;
  addressLine4?: string;
  addressLine5?: string;
  country?: string;
  postCode?: string;

  /**
   * Billing Address Constructor
   * @param data object data
   * @param data.billingAddress billing address data
   */
  constructor(data: { billingAddress?: Record<string, unknown> } = {}) {
    const billingAddress = data.billingAddress ?? {};
    this.addressLine1 = (billingAddress.addressLine1 as string) || '';
    this.addressLine2 = (billingAddress.addressLine2 as string) || '';
    this.addressLine3 = (billingAddress.addressLine3 as string) || '';
    this.addressLine4 = (billingAddress.addressLine4 as string) || '';
    this.addressLine5 = (billingAddress.addressLine5 as string) || '';
    this.postCode = (billingAddress.postCode as string) || '';
    this.country = (billingAddress.country as string) || '';
  }

  static fromResponse(data: { billingAddress?: Record<string, unknown> }): CompanyBillingAddress {
    return new CompanyBillingAddress(data);
  }

  /**
   * Validate Billing Address
   * @param data object data
   * @param data.billingAddress object data
   */
  async validateBillingAddress(data: { billingAddress: Record<string, unknown> }): Promise<void> {
    console.log('Validate Billing Address from company payment card');
    const { billingAddress } = data;
    const checks: Array<[unknown, unknown, string]> = [
      [this.address1, billingAddress.addressLine1, 'Address Line 1'],
      [this.address2, billingAddress.addressLine2, 'Address Line 2'],
      [this.address3, billingAddress.addressLine3, 'Address Line 3'],
      [this.address4, billingAddress.addressLine4, 'Address Line 4'],
      [this.address5, billingAddress.addressLine5, 'Address Line 5'],
      [this.postCode, billingAddress.postCode, 'Postcode'],
      [this.country, billingAddress.country, 'Country'],
    ];
    for (const [actual, expected, label] of checks) {
      if (actual !== expected) {
        throw new Error(`${label}=${actual} should be=${expected}`);
      }
    }
  }

}
