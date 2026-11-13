/**
 * The total cost information from  data.bookingInformation.totalCost reservation of API response
 *{
 *    "data": {
 *        "bookingInformation": {
 *           "totalCost": 2352.0
 *        }
 *    }
 *}
 */
export class TotalCost {
  [key: string]: unknown;
  totalCost?: number;

  /**
   * Total Cost constructor
   * @param data object data
   * @param data.bookingInfo bookingInfo object
   */
  constructor(data: { bookingInfo?: { totalCost?: number } } = {}) {
    this.totalCost = data.bookingInfo?.totalCost;
  }

  static fromResponse(data: { bookingInfo?: { totalCost?: number } }): TotalCost {
    return new TotalCost(data);
  }
}
