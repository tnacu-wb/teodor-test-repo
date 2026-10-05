import { HotelAddressCCUI } from './hotelAddressCCUI';

/**
 * response example
 * "billing":{
    "address":{
      "addressLine1":"",
      "addressLine2":"",
      "addressLine3":"",
      "addressLine4":"",
      "country":"",
      "postalCode":""
    },
   "email":"",
   "firstName":"Tester",
   "lastName":"Testerson",
   "telephone":"",
   "title":"Mrs"
}
 */
export class BookingInfoBillingCCUI {
  [key: string]: unknown;
  address?: HotelAddressCCUI;
  email?: string;
  firstName?: string;
  lastName?: string;
  telephone?: string;
  title?: string;

  /**
   * BookingInfoBillingCCUI constructor
   * @param data object data
   * @param data.bookingBillingInfo bookingBillingInfo
   */
  constructor(data: { bookingBillingInfo?: Record<string, unknown> } = {}) {
    const bookingBillingInfo = data.bookingBillingInfo ?? {};
    this.address = new HotelAddressCCUI({ hotelAddress: bookingBillingInfo.address as Record<string, unknown> });
    this.email = bookingBillingInfo.email as string | undefined;
    this.firstName = bookingBillingInfo.firstName as string | undefined;
    this.lastName = bookingBillingInfo.lastName as string | undefined;
    this.telephone = bookingBillingInfo.telephone as string | undefined;
    this.title = bookingBillingInfo.title as string | undefined;
  }

  static fromResponse(data: { bookingBillingInfo?: Record<string, unknown> }): BookingInfoBillingCCUI {
    return new BookingInfoBillingCCUI(data);
  }
}
