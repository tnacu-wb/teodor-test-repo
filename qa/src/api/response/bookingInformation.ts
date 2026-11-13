/**
 * response example
 * {
    "data": {
        "bookingInformation": {
            "hotelId": "MANOLD",
            "totalCost": 999.0,
            "totalCostWoDiscount": 999.0,
            "discount": 0.0,
            "newTotal": 999.0,
            "previousTotal": 0.0,
            "currencyCode": "GBP",
            "reservationByIdList": [
                {
                    "roomStay": {
                        "adultsNumber": 1,
                        "childrenNumber": 0,
                        "arrivalDate": "2022-10-10",
                        "departureDate": "2022-10-11",
                        "ratePlanCode": "FLEXRATE",
                        "rateExtraInfo": {
                            "rateName": "Flex",
                            "rateDescription": "Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival",
                            "rateClassification": "FLEXRATE"
                        },
                        "roomExtraInfo": {
                            "roomName": "Premier Plus Room",
                            "roomType": "PPLDBL"
                        }
                    }
                }
            ]
            "bookingFlowId": "booking-a1"
        }
    }
}
 */
export class BookingInformation {
  [key: string]: unknown;
  bookingFlowId?: string;
  currencyCode?: string;
  discount?: number;
  hotelId?: string;
  newTotal?: number;
  policyCode?: string;
  previousTotal?: number;
  reservationByIdList?: unknown[];
  totalCost?: number;
  totalCostWoDiscount?: number;

  /**
   * BookingInformation constructor
   * @param data object data
   * @param data.bookingInformation bookingInformation
   */
  constructor(data: { bookingInformation?: Record<string, unknown> } = {}) {
    const bookingInformation = data.bookingInformation ?? {};
    this.hotelId = bookingInformation.hotelId as string | undefined;
    this.totalCost = bookingInformation.totalCost as number | undefined;
    this.totalCostWoDiscount = bookingInformation.totalCostWoDiscount as number | undefined;
    this.discount = bookingInformation.discount as number | undefined;
    this.newTotal = bookingInformation.newTotal as number | undefined;
    this.previousTotal = bookingInformation.previousTotal as number | undefined;
    this.currencyCode = bookingInformation.currencyCode as string | undefined;
    this.reservationByIdList = bookingInformation.reservationByIdList as unknown[] | undefined;
    this.bookingFlowId = bookingInformation.bookingFlowId as string | undefined;
    this.policyCode = bookingInformation.policyCode as string | undefined;
  }

  static fromResponse(data: { bookingInformation?: Record<string, unknown> }): BookingInformation {
    return new BookingInformation(data);
  }
}
