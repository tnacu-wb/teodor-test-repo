import { RoomStay } from './roomStay';
import { StayingGuest } from './stayingGuest';

/**
 * response example
 *  {
                "reservationIdList": [
                    {
                        "id": "840656",
                        "type": "Reservation"
                    },
                    {
                        "id": "10383710",
                        "type": "Confirmation"
                    }
                ],
                "roomStay": {
                    "arrivalDate": "2023-02-21",
                    "departureDate": "2023-02-22",
                    "adultCount": 1,
                    "childCount": 2,
                    "roomClass": "ST",
                    "roomType": "FMQUAD",
                    "numberOfRooms": 1,
                    "ratePlanCode": "FLEXRATE"
                },
                "reservationGuest": {
                    "givenName": "Calvin",
                    "surname": "Corsi",
                    "nameTitle": "Sir"
                },
                "hotelId": "LONEUS",
                "hotelName": "London Euston ",
                "roomStayReservation": true
    }
 */
export class ReservationInfo {
  [key: string]: unknown;
  hotelId?: string;
  hotelName?: string;
  roomStay?: RoomStay;
  roomStayReservation?: boolean;
  stayingGuest?: StayingGuest;

  /**
   * Reservation info constructor
   * @param data object data
   * @param data.reservationInfo reservations info
   */
  constructor(data: { reservationInfo?: Record<string, unknown> } = {}) {
    const reservationInfo = data.reservationInfo ?? {};
    const roomStay = (reservationInfo.roomStay ?? {}) as Record<string, unknown>;
    const reservationGuest = (reservationInfo.reservationGuest ?? {}) as Record<string, unknown>;
    this.roomStay = new RoomStay({
      bookingInfo: {
        roomType: roomStay.roomType,
        adultsNumber: roomStay.adultCount,
        childrenNumber: roomStay.childCount,
        arrivalDate: roomStay.arrivalDate,
        departureDate: roomStay.departureDate,
        ratePlanCode: roomStay.ratePlanCode,
      },
    });
    this.stayingGuest = new StayingGuest({
      stayingGuest: {
        firstName: reservationGuest.givenName,
        lastName: reservationGuest.surname,
        title: reservationGuest.nameTitle,
      },
    });
    this.hotelId = reservationInfo.hotelId as string | undefined;
    this.hotelName = reservationInfo.hotelName as string | undefined;
    this.roomStayReservation = reservationInfo.roomStayReservation as boolean | undefined;
  }

  static fromResponse(data: { reservationInfo?: Record<string, unknown> }): ReservationInfo {
    return new ReservationInfo(data);
  }
}
