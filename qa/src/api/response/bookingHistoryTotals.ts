/**
 * Response Example:
 * {
    "data": {
        "bookingHistory": {
            "totals": {
                "upcoming": 0,
                "cancelled": 1,
                "checkedIn": 0,
                "past": 1
            }
        }
    }
}
 */
export class BookingHistoryTotals {
  [key: string]: unknown;
  cancelled?: number;
  checkedIn?: number;
  past?: number;
  upcoming?: number;

  /**
   * Booking History Constructor
   * @param data object data
   * @param data.bookingTotals booking history totals
   */
  constructor(data: { bookingTotals?: Record<string, unknown> } = {}) {
    const bookingTotals = data.bookingTotals ?? {};
    this.upcoming = bookingTotals.upcoming as number | undefined;
    this.cancelled = bookingTotals.cancelled as number | undefined;
    this.checkedIn = bookingTotals.checkedIn as number | undefined;
    this.past = bookingTotals.past as number | undefined;
  }

  static fromResponse(data: { bookingTotals?: Record<string, unknown> }): BookingHistoryTotals {
    return new BookingHistoryTotals(data);
  }
}
