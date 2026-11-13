import { AccessibleRoom } from './accessibleRoom';
import { RateClassification } from './rateClassification';
import { RatesPerNight } from './ratesPerNight';
import { RoomExtraInfo } from './roomExtraInfo';

/**
 * The room stay information from  data.bookingInformation.reservationByIdList[0].roomStay reservation of API response
 *{
 *    "data": {
 *        "bookingInformation": {
 *            "reservationByIdList": [
 *                {
 *                    "roomStay": {
 *                        "roomType": 'DOUBLE'    
 *                        "adultsNumber": 2,
 *                        "childrenNumber": 2,
 *                        "arrivalDate": "2022-05-10",
 *                        "departureDate": "2022-05-15",
 *                        "ratePlanCode": "FLEXRATE",
                         "accessibleRoom": {
                            "isAccessible": false,
                            "phoneNumber:
                                    }
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
                            "roomName": "Standard double",
                            "roomType": "Double"
                         }
 *                    }
 *                }
 *            ]
 *        }
 *    }
 *}
 */
export class RoomStay {
  [key: string]: unknown;
  accessibleRoom?: AccessibleRoom;
  adultsNumber?: number;
  arrivalDate?: string;
  checkInTime?: string;
  checkOutTime?: string;
  childrenNumber?: number;
  cot?: boolean;
  departureDate?: string;
  rateExtraInfo?: RateClassification;
  ratePlanCode?: string;
  ratesPerNight: RatesPerNight[] = [];
  roomExtraInfo?: RoomExtraInfo;
  roomPrice?: number;
  roomType?: string;

  /**
   * RoomStay constructor
   * @param data object data
   * @param data.bookingInfo bookingInfo
   */
  constructor(data: { bookingInfo?: Record<string, unknown> } = {}) {
    const bookingInfo = data.bookingInfo ?? {};
    this.roomType = bookingInfo.roomType as string | undefined;
    this.adultsNumber = bookingInfo.adultsNumber as number | undefined;
    this.childrenNumber = bookingInfo.childrenNumber as number | undefined;
    this.cot = bookingInfo.cot as boolean | undefined;
    this.arrivalDate = bookingInfo.arrivalDate as string | undefined;
    this.departureDate = bookingInfo.departureDate as string | undefined;
    this.ratePlanCode = bookingInfo.ratePlanCode as string | undefined;
    this.checkInTime = bookingInfo.checkInTime as string | undefined;
    this.checkOutTime = bookingInfo.checkOutTime as string | undefined;
    this.roomPrice = bookingInfo.roomPrice as number | undefined;
    if (bookingInfo.accessibleRoom) {
      this.accessibleRoom = new AccessibleRoom({ bookingInfo: bookingInfo.accessibleRoom as Record<string, unknown> });
    }
    const ratesPerNight = Array.isArray(bookingInfo.ratesPerNight) ? (bookingInfo.ratesPerNight as Array<Record<string, unknown>>) : [];
    this.ratesPerNight = ratesPerNight.map((item) => new RatesPerNight({ ratePerNight: item }));
    if (bookingInfo.rateExtraInfo) {
      this.rateExtraInfo = new RateClassification({ rateClassificationObject: bookingInfo.rateExtraInfo as Record<string, unknown> });
    }
    if (bookingInfo.roomExtraInfo) {
      this.roomExtraInfo = new RoomExtraInfo({ roomExtraInfo: bookingInfo.roomExtraInfo as Record<string, unknown> });
    }
  }

  static fromResponse(data: { bookingInfo?: Record<string, unknown> }): RoomStay {
    return new RoomStay(data);
  }

}
