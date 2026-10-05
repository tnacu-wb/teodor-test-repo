/**
The create reservation from API response example: 
{
    "data": {
        "createReservation": {
            "basketReference": "MANOLD3426715",
            "reservations": [
                {
                    "reservationId": "36402",
                    "createDateTime": "2022-07-05T13:28:34Z",
                    "roomStay": {
                        "adultsNumber": 1,
                        "arrivalDate": "2022-07-06",
                        "childrenNumber": 0,
                        "departureDate": "2022-07-08",
                        "ratePlanCode": "FLEXRATE",
                        "roomType": "DOUBLE",
                        "rateExtraInfo": null
                    }
                }
            ],
            "bookingFlowId": "booking-a1",
            "hotelId": "MANOLD",
            "policyCode": "OA",
            "previousTotal": null,
            "totalCost": 400.0,
            "balanceOutstanding": null,
            "currencyCode": "GBP",
            "newTotal": null
        }
    }
}
 */
export class CreateReservation {
  [key: string]: unknown;
  basketReference?: string;
  bookingFlowId?: string;
  currencyCode?: string;
  hotelId?: string;
  reservations?: Array<{ reservationId?: string; createDateTime?: string; roomStay?: Record<string, unknown> }>;
  totalCost?: number;

  /**
   * CreateReservation constructor
   * @param data object data
   * @param data.createReservation createReservation
   */
  constructor(data: { createReservation?: Record<string, unknown> } = {}) {
    const createReservation = data.createReservation ?? {};
    this.basketReference = createReservation.basketReference as string | undefined;
    this.reservations = createReservation.reservations as CreateReservation['reservations'];
    this.hotelId = createReservation.hotelId as string | undefined;
    this.totalCost = createReservation.totalCost as number | undefined;
    this.currencyCode = createReservation.currencyCode as string | undefined;
    this.bookingFlowId = createReservation.bookingFlowId as string | undefined;
  }

  static fromResponse(data: { createReservation?: Record<string, unknown> }): CreateReservation {
    return new CreateReservation(data);
  }
}
