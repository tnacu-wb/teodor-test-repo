/**
 * Response Example:
 * {
    "data": {
        "bookingAllowances": {
            "bookingAllowances": [
                {
                    "allowance": "alcohol",
                    "budget": "0"
                },
                {
                    "allowance": "carParking",
                    "budget": null
                },
                {
                    "allowance": "ultimateWifi",
                    "budget": null
                },
                {
                    "allowance": "dinner",
                    "budget": "33.0"
                }
            ]
        }
    }
}
 */
export class BookingAllowances {
  [key: string]: unknown;
  allowance?: string;
  budget?: string | null;

  /**
   * Booking Allowance Constructor
   * @param data object data
   * @param data.bookingAllowance booking allowance
   */
  constructor(data: { bookingAllowance?: Record<string, unknown> } = {}) {
    const bookingAllowance = data.bookingAllowance ?? {};
    this.allowance = bookingAllowance.allowance as string | undefined;
    this.budget = bookingAllowance.budget as string | null | undefined;
  }

  static fromResponse(data: { bookingAllowance?: Record<string, unknown> }): BookingAllowances {
    return new BookingAllowances(data);
  }
}
