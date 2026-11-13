/**
The find booking API response example: 
{
    "data": {
        "findBooking": {
            "cookieName": "pi.single-booking",
            "redirectBase": "/gb/en/account/dashboard",
            "ref": "AKU6894369",
            "sourcePms": "Opera",
            "token": "7L6xY8g9jMwro1UzUusSncyGNozlUIDslsdJ55ymHsViPMa7acTRqEVv31SfgA4i",
            "minutesTillExpiry": "30"
        }
    }
}
 */
export class FindBooking {
  [key: string]: unknown;
  cookieName?: string;
  minutesTillExpiry?: string;
  redirectBase?: string;
  ref?: string;
  sourcePms?: string;
  token?: string;

  /**
   * Find booking response constructor
   * @param data object data
   * @param data.findBooking findBooking response
   */
  constructor(data: { findBooking?: Record<string, unknown> } = {}) {
    const findBooking = data.findBooking ?? {};
    this.cookieName = findBooking.cookieName as string | undefined;
    this.redirectBase = findBooking.redirectBase as string | undefined;
    this.ref = findBooking.ref as string | undefined;
    this.sourcePms = findBooking.sourcePms as string | undefined;
    this.token = findBooking.token as string | undefined;
    this.minutesTillExpiry = findBooking.minutesTillExpiry as string | undefined;
  }

  static fromResponse(data: { findBooking?: Record<string, unknown> }): FindBooking {
    return new FindBooking(data);
  }
}
