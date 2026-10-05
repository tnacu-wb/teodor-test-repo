import { HotelAddressByHotelInformation } from './hotelAddressByHotelInformation';

/**
 * response example:
 *{
 *    "data": {
 *        "hotelInformation": {
 *            "hotelOpeningDate": "2023-05-01T00:00:00.000+01:00",
 *            "coordinates": {
 *                "latitude": 55.928445,
 *                "longitude": -3.307357
 *            },
 *            "address": {
 *                "addressLine1": "1 Lochside Court",
 *                "addressLine2": "Edinburgh Park",
 *                "addressLine3": "Edinburgh",
 *                "addressLine4": null,
 *                "postalCode": "EH12 9FX",
 *                "country": "United Kingdom (the)"
 *            },
 *            "contactDetails": {
 *              "email": "hotelName@gmail.com",
 *              "phone": "0333 321 1315",
 *              "hotelNationalPhone": ""
 *            },
 *            "name": "Edinburgh Park (Airport)",
 *            "directions": "Located on A14(J52). At roundabout exit onto Paper Mill Lane. Hotel is the first left.",
 *            "brand": "PI"
 *        }
 *    }
 *}
 */
export class HotelInformation {
  [key: string]: unknown;
  address?: HotelAddressByHotelInformation;
  ancillaryCloseout?: unknown;
  announcement?: unknown;
  bookingFlow?: unknown;
  brand?: string;
  contactDetails?: unknown;
  coordinates?: unknown;
  directions?: string;
  hotelOpeningDate?: string;
  messagingFlag?: unknown;
  name?: string;

  /**
   * HotelInformation constructor
   * @param data object data
   * @param data.hotelInformation hotelInformation
   */
  constructor(data: { hotelInformation?: Record<string, unknown> } = {}) {
    const hotelInformation = data.hotelInformation ?? {};
    this.address = new HotelAddressByHotelInformation({ hotelAddress: hotelInformation.address as Record<string, unknown> });
    this.name = hotelInformation.name as string | undefined;
    this.brand = hotelInformation.brand as string | undefined;
    this.coordinates = hotelInformation.coordinates;
    this.bookingFlow = hotelInformation.bookingFlow;
    this.contactDetails = hotelInformation.contactDetails;
    this.directions = hotelInformation.directions as string | undefined;
    this.hotelOpeningDate = hotelInformation.hotelOpeningDate as string | undefined;
    this.messagingFlag = hotelInformation.messagingFlag;
    this.ancillaryCloseout = hotelInformation.ancillaryCloseout;
    this.announcement = hotelInformation.announcement;
  }

  static fromResponse(data: { hotelInformation?: Record<string, unknown> }): HotelInformation {
    return new HotelInformation(data);
  }
}
