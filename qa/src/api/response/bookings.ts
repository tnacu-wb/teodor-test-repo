import { StayingGuest } from './stayingGuest';

/**
 * response example
 * {
    "bookings": [
        {
            "arrivalDate": '2023-02-06'
            "booker": {
                "firstName": 'Test Auto'
                "lastName": 'VannozzißÜÖÄüöä'
                "title": 'Mr'
            }
            "bookingReference": 'GBM6367787'
            "currencyCode": 'GBP'
            "departureDate": '2023-02-07'
            "hotelId": 'BERALX'
            "hotelName": 'Berlin Alexanderplatz'
            "sourcePms": 'OPERA'
            "status": 'CANCELLED'
            "stayingGuests": [
                {
                    "firstName": 'Test Auto'
                    "lastName": 'VannozzißÜÖÄüöä'
                    "title": 'Mr'
                }
            ]
            "totalCost": 0
        }
    ]
}
 */
export class Bookings {
  [key: string]: unknown;
  arrivalDate?: string;
  bookerFirstName?: string;
  bookerLastName?: string;
  bookerTitle?: string;
  bookingReference?: string;
  currencyCode?: string;
  departureDate?: string;
  hotelId?: string;
  hotelName?: string;
  sourcePms?: string;
  status?: string;
  stayingGuests: StayingGuest[] = [];
  totalCost?: number;

  /**
   * Bookings constructor
   * @param data object data
   * @param data.booking bookings info
   */
  constructor(data: { booking?: Record<string, unknown> } = {}) {
    const booking = data.booking ?? {};
    const booker = (booking.booker ?? {}) as Record<string, unknown>;
    this.arrivalDate = booking.arrivalDate as string | undefined;
    this.bookerFirstName = booker.firstName as string | undefined;
    this.bookerLastName = booker.lastName as string | undefined;
    this.bookerTitle = booker.title as string | undefined;
    this.bookingReference = booking.bookingReference as string | undefined;
    this.currencyCode = booking.currencyCode as string | undefined;
    this.departureDate = booking.departureDate as string | undefined;
    this.hotelId = booking.hotelId as string | undefined;
    this.hotelName = booking.hotelName as string | undefined;
    this.sourcePms = booking.sourcePms as string | undefined;
    this.status = booking.status as string | undefined;

    const stayingGuests = Array.isArray(booking.stayingGuests) ? (booking.stayingGuests as Array<Record<string, unknown>>) : [];
    this.stayingGuests = stayingGuests.map((guest) => new StayingGuest({ stayingGuest: guest }));

    this.totalCost = booking.totalCost as number | undefined;
  }

  static fromResponse(data: { booking?: Record<string, unknown> }): Bookings {
    return new Bookings(data);
  }

}
