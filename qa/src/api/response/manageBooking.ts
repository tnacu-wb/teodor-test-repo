/**
The manage booking API response example: 
{
    "data": {
        "manageBooking": {
            "isCancellable": false,
            "isAmendable": true,
            "isRuleCompliant": true
        }
    }
}
 */
export class ManageBooking {
  [key: string]: unknown;
  isAmendable?: boolean;
  isCancellable?: boolean;
  isRuleCompliant?: boolean;

  /**
   * Manage booking response constructor
   * @param data object data
   * @param data.manageBooking manageBooking response
   */
  constructor(data: { manageBooking?: Record<string, unknown> } = {}) {
    const manageBooking = data.manageBooking ?? {};
    this.isCancellable = manageBooking.isCancellable as boolean | undefined;
    this.isAmendable = manageBooking.isAmendable as boolean | undefined;
    this.isRuleCompliant = manageBooking.isRuleCompliant as boolean | undefined;
  }

  static fromResponse(data: { manageBooking?: Record<string, unknown> }): ManageBooking {
    return new ManageBooking(data);
  }
}
