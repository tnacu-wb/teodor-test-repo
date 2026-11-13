import { Bookings } from './bookings';

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
    "hasMore": true
    "limit": 10
    "offset": 10
    "totalPages": 2
    "totalResults": 20
}
 */
export class SearchBookings {
  [key: string]: unknown;
  bookings: Bookings[] = [];
  hasMore?: boolean;
  limit?: number;
  offset?: number;
  totalPages?: number;
  totalResults?: number;

  /**
   * SearchBookings constructor
   * @param data object data
   * @param data.searchBookingsResults searchBookingsResults info
   */
  constructor(data: { searchBookingsResults?: Record<string, unknown> } = {}) {
    const searchBookingsResults = data.searchBookingsResults ?? {};
    const bookings = Array.isArray(searchBookingsResults.bookings) ? (searchBookingsResults.bookings as Array<Record<string, unknown>>) : [];
    this.bookings = bookings.map((booking) => new Bookings({ booking }));
    this.hasMore = searchBookingsResults.hasMore as boolean | undefined;
    this.limit = searchBookingsResults.limit as number | undefined;
    this.offset = searchBookingsResults.offset as number | undefined;
    this.totalPages = searchBookingsResults.totalPages as number | undefined;
    this.totalResults = searchBookingsResults.totalResults as number | undefined;
  }

  static fromResponse(data: { searchBookingsResults?: Record<string, unknown> }): SearchBookings {
    return new SearchBookings(data);
  }

  /**
   * Validate bookings pagination
   * @param data object data
   * @param data.expectedNumber expected number of bookings
   */
  async validateBookingsPagination(data: { expectedNumber: number }): Promise<void> {
    const { expectedNumber } = data;
    console.log(`Validate bookings pagination and total number of bookings=${expectedNumber}`);

    if (this.totalResults !== expectedNumber) {
      throw new Error(`Number of bookings should be=${expectedNumber} instead of=${this.totalResults}`);
    }
    const expectedHasMore = (this.offset ?? 0) < (this.totalResults ?? 0);
    if (this.hasMore !== expectedHasMore) {
      throw new Error(`hasMore should be=${expectedHasMore} instead of=${this.hasMore}`);
    }

    const integerPart = Math.trunc(expectedNumber / 10);
    const noOfPages = integerPart === expectedNumber / (this.limit ?? 1) ? integerPart : integerPart + 1;
    if (this.totalPages !== noOfPages) {
      throw new Error(`Number of pages should be=${noOfPages} instead of=${this.totalPages}`);
    }
  }

}
