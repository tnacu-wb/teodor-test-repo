import { BookingSpinnerConfigItem } from './bookingSpinnerConfigItem';
import { ReservationByIdItem } from './reservationByIdItem';
import { UpgradeToFlex } from './upgradeToFlex';

/**
 * response example
 * {
    "data": {
        "bookingConfirmation": {
            "hotelId": "LONEUS",
            "hotelName": "London Euston",
            "previousTotal": 0,
            "balanceOutstanding": 0,
            "totalCostWoDiscount": 0,
            "newTotal": 0,
            "totalCost": 999.0,
            "currencyCode": "GBP",
            "bookingFlowId": "booking-a1",
            "policyCode": "DAX",
            "reservationByIdList": [],
            "infoMessages": [],
            "bookingSpinnerConfig": [
                {
                    "order": "3",
                    "seconds": "30",
                    "text": "Sorry for the delay - please bear with us"
                }
            ],
            "upgradeToFlex": {
                "amount": null,
                "currency": null,
                "flexRateCode": null
            },
            "rateMessage": "<p>Amend or cancel up to 6pm on arrival day</p>\n"
        }
    }
}
 */
export class BookingConfirmation {
  [key: string]: unknown;
  balanceOutstanding?: number;
  bookingFlowId?: string;
  bookingSpinnerConfig: BookingSpinnerConfigItem[] = [];
  currencyCode?: string;
  hotelId?: string;
  hotelName?: string;
  infoMessages?: unknown;
  newTotal?: number;
  policyCode?: string;
  previousTotal?: number;
  rateMessage?: string;
  reservationByIdList: ReservationByIdItem[] = [];
  totalCost?: number;
  totalCostWoDiscount?: number;
  upgradeToFlex?: UpgradeToFlex;

  /**
   * BookingConfirmation constructor
   * @param data object data
   * @param data.bookingConfirmation bookingConfirmation info
   */
  constructor(data: { bookingConfirmation?: Record<string, unknown> } = {}) {
    const bookingConfirmation = data.bookingConfirmation ?? {};
    this.hotelId = bookingConfirmation.hotelId as string | undefined;
    this.hotelName = bookingConfirmation.hotelName as string | undefined;
    this.previousTotal = bookingConfirmation.previousTotal as number | undefined;
    this.balanceOutstanding = bookingConfirmation.balanceOutstanding as number | undefined;
    this.totalCostWoDiscount = bookingConfirmation.totalCostWoDiscount as number | undefined;
    this.newTotal = bookingConfirmation.newTotal as number | undefined;
    this.totalCost = bookingConfirmation.totalCost as number | undefined;
    this.currencyCode = bookingConfirmation.currencyCode as string | undefined;
    this.bookingFlowId = bookingConfirmation.bookingFlowId as string | undefined;
    this.policyCode = bookingConfirmation.policyCode as string | undefined;
    this.rateMessage = bookingConfirmation.rateMessage as string | undefined;
    this.infoMessages = bookingConfirmation.infoMessages;

    const reservationByIdList = Array.isArray(bookingConfirmation.reservationByIdList)
      ? (bookingConfirmation.reservationByIdList as Array<Record<string, unknown>>)
      : [];
    this.reservationByIdList = reservationByIdList.map((item) => new ReservationByIdItem({ reservationByIdItem: item }));

    const bookingSpinnerConfig = Array.isArray(bookingConfirmation.bookingSpinnerConfig)
      ? (bookingConfirmation.bookingSpinnerConfig as Array<Record<string, unknown>>)
      : [];
    this.bookingSpinnerConfig = bookingSpinnerConfig.map((item) => new BookingSpinnerConfigItem({ bookingSpinnerConfigItem: item }));

    this.upgradeToFlex = new UpgradeToFlex({ upgradeToFlex: bookingConfirmation.upgradeToFlex });
  }

  static fromResponse(data: { bookingConfirmation?: Record<string, unknown> }): BookingConfirmation {
    return new BookingConfirmation(data);
  }

}
