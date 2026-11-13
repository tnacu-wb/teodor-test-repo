/**
 * The upgrade to cost information from  data.bookingInformation.upgradeToFlex reservation of API response
 *{
 *    "data": {
 *        "bookingInformation": {
 *           "upgradeToFlex": {
                "flexRateCode": "FLEXRATE",
                "amount": 134.0,
                "currency": "GBP"
            }
            "totalCost": 999.0
 *        }
 *    }
 *}
 */
export class AncillariesUpgradeToFlex {
  [key: string]: unknown;
  amount?: number;
  currency?: string;
  flexRateCode?: string;
  totalCost?: number;

  /**
  * AncillariesUpgradeToFlex constructor
   * @param data object data
   * @param data.bookingInfo bookingInfo
   */
  constructor(data: { bookingInfo?: { upgradeToFlex?: Record<string, unknown>; totalCost?: number } } = {}) {
    const bookingInfo = data.bookingInfo ?? {};
    const upgradeToFlex = bookingInfo.upgradeToFlex ?? {};
    this.flexRateCode = upgradeToFlex.flexRateCode as string | undefined;
    this.amount = upgradeToFlex.amount as number | undefined;
    this.currency = upgradeToFlex.currency as string | undefined;
    this.totalCost = bookingInfo.totalCost;
  }

  static fromResponse(data: { bookingInfo?: { upgradeToFlex?: Record<string, unknown>; totalCost?: number } }): AncillariesUpgradeToFlex {
    return new AncillariesUpgradeToFlex(data);
  }
}
