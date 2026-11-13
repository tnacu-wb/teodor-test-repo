import { ReservationInfo } from './reservationInfo';

/**
 * response example
 * {
    "reservations": {
        "reservationInfo": [
            {
                "hotelId": "LONEUS",
                "hotelName": "London Euston ",
                "roomStayReservation": true
            }
        ],
        "totalPages": 1,
        "offset": 20,
        "limit": 20,
        "hasMore": false,
        "totalResults": 3
    }
}
 */
export class Reservations {
  [key: string]: unknown;
  hasMore?: boolean;
  limit?: number;
  offset?: number;
  reservations: ReservationInfo[] = [];
  totalPages?: number;
  totalResults?: number;

  /**
   * Reservations constructor
   * @param data object data
   * @param data.reservations reservations list api response
   */
  constructor(data: { reservations?: unknown } = {}) {
    const reservations = (data.reservations ?? {}) as Record<string, unknown>;
    const reservationInfoList = Array.isArray(reservations.reservationInfo)
      ? (reservations.reservationInfo as Array<Record<string, unknown>>)
      : [];
    this.reservations = reservationInfoList.map((reservation) => new ReservationInfo({ reservationInfo: reservation }));
    this.totalPages = reservations.totalPages as number | undefined;
    this.offset = reservations.offset as number | undefined;
    this.limit = reservations.limit as number | undefined;
    this.hasMore = reservations.hasMore as boolean | undefined;
    this.totalResults = reservations.totalResults as number | undefined;
  }

  static fromResponse(data: { reservations?: unknown }): Reservations {
    return new Reservations(data);
  }

  /**
   * Validate Staying Guests
   * @param data object data
   * @param data.stayingGuests array of staying guests
   */
  async validateStayingGuests(data: { stayingGuests: Array<{ title?: string; firstName?: string; lastName?: string }> }): Promise<void> {
    console.log('Validate staying guests');
    const { stayingGuests } = data;
    let noOfMatches = 0;

    for (const item of stayingGuests) {
      console.log(`-> expected: title=${item.title}, firstName=${item.firstName}, lastName=${item.lastName}`);
    }

    for (const reservation of this.reservations) {
      const stayingGuest = reservation.stayingGuest as Record<string, unknown> | undefined;
      console.log(`-> actual: ${stayingGuest?.title}, ${stayingGuest?.firstName}, ${stayingGuest?.lastName}`);
      const matchedGuest = stayingGuests.find(
        (guest) => guest.title === stayingGuest?.title && guest.firstName === stayingGuest?.firstName && guest.lastName === stayingGuest?.lastName,
      );

      if (matchedGuest) {
        noOfMatches += 1;
      }
    }
    if (this.totalResults !== noOfMatches) {
      throw new Error(`Matching staying guests=${this.totalResults} should be=${noOfMatches}`);
    }
  }

}
