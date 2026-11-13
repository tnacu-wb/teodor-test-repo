/**
 * {
    "data": {
        "bookingAlerts": {
            "rateCaps": {
                "uKWide": {
                    "amount": 100,
                    "currency": "GBP"
                },
                "greaterLondon": {
                    "amount": 0,
                    "currency": "GBP"
                },
                "ireland": {
                    "amount": 0,
                    "currency": "GBP"
                }
            },
            "bookingAlertHotels": [],
            "recipientEmailAddresses": [],
            "dayOfArrival": true,
            "weekendArrival": false,
            "passThroughWeekend": false,
            "frequency": "D"
        }
    } 
}
 */
export class CompanyBookingAlerts {
  [key: string]: unknown;
  bookingAlertHotels?: string[];
  dayOfArrival?: boolean;
  frequency?: string;
  passThroughWeekend?: boolean;
  rateCaps?: {
    uKWide: { amount: number; currency: string };
    greaterLondon: { amount: number; currency: string };
    ireland: { amount: number; currency: string };
  };
  recipientEmailAddresses?: string[];
  weekendArrival?: boolean;

  /**
   * Company Address Info
   * @param data data
   * @param data.bookingAlerts company details address and name
   */
  constructor(data: { bookingAlerts?: Record<string, unknown> } = {}) {
    const bookingAlerts = data.bookingAlerts ?? {};
    this.rateCaps = bookingAlerts.rateCaps as CompanyBookingAlerts['rateCaps'];
    this.bookingAlertHotels = bookingAlerts.bookingAlertHotels as string[] | undefined;
    this.recipientEmailAddresses = bookingAlerts.recipientEmailAddresses as string[] | undefined;
    this.dayOfArrival = bookingAlerts.dayOfArrival as boolean | undefined;
    this.weekendArrival = bookingAlerts.weekendArrival as boolean | undefined;
    this.passThroughWeekend = bookingAlerts.passThroughWeekend as boolean | undefined;
    this.frequency = bookingAlerts.frequency as string | undefined;
  }

  static fromResponse(data: { bookingAlerts?: Record<string, unknown> }): CompanyBookingAlerts {
    return new CompanyBookingAlerts(data);
  }

  /**
   * Set alerts to default settings
   */
  setAlertsDefaultSettings(): void {
    if (this.rateCaps) {
      this.rateCaps.uKWide.amount = 0;
      this.rateCaps.greaterLondon.amount = 0;
      this.rateCaps.ireland.amount = 0;
    }
    this.bookingAlertHotels = [];
    this.recipientEmailAddresses = [];
    this.dayOfArrival = false;
    this.weekendArrival = false;
    this.passThroughWeekend = false;
    this.frequency = 'N';
  }

}
