import { BookingInfoBillingCCUI } from './bookingInfoBillingCCUI';
import { RoomStay } from './roomStay';

/**
 * response example
 * {
    "data": {
        "bookingInformation": {
            "hotelId": "NEWDRO",
            "totalCost": 304.6,
            "currencyCode": "GBP",
            "bookingFlowId": "booking-a1",
            "reservationByIdList": [
                {
                    "roomStay": {
                        "adultsNumber": 1,
                        "childrenNumber": 0,
                        "arrivalDate": "2022-08-09",
                        "departureDate": "2022-08-11",
                        "ratePlanCode": "FLEXRATE",
                        "rateExtraInfo": {
                            "rateName": "Flex"
                        },
                        "ratesPerNight": [
                            {
                                "startDate": "2023-02-01",
                                "pricePerNight": 999.0
                            }
                        ],
                        "roomExtraInfo": {
                            "roomType": "Double"
                        }
                    },
                    "billing": {
                        "address": {
                            "addressLine1": "",
                            "addressLine2": "",
                            "addressLine3": "",
                            "addressLine4": "",
                            "country": "",
                            "postalCode": ""
                        },
                        "email": "",
                        "firstName": "Tester",
                        "lastName": "Testerson",
                        "telephone": "",
                        "title": "Mrs"
                    }
                }
            ]
        }
    }
}
 */
export class ReservationById {
  [key: string]: unknown;
  billing?: BookingInfoBillingCCUI;
  roomStay?: RoomStay;

  /**
   * Reservation By Id constructor
   * @param data object data
   * @param data.reservationByIdApiResponse response from API
   */
  constructor(data: { reservationByIdApiResponse?: Record<string, unknown> } = {}) {
    const reservationByIdApiResponse = data.reservationByIdApiResponse ?? {};
    this.roomStay = new RoomStay({ bookingInfo: reservationByIdApiResponse.roomStay as Record<string, unknown> });
    this.billing = new BookingInfoBillingCCUI({ bookingBillingInfo: reservationByIdApiResponse.billing as Record<string, unknown> });
  }

  static fromResponse(data: { reservationByIdApiResponse?: Record<string, unknown> }): ReservationById {
    return new ReservationById(data);
  }
}
