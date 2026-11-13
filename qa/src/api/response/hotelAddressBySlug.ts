/**
 * The hotel address from API response
 * response example: 
 * {"data": 
 *  {"hotelInformation":
 *   {"address":
 *    {"addressLine1":"27-29 Red Lion Street",
 *     "addressLine2":"Holborn",
 *     "addressLine3":"London","postalCode":"WC1R 4PS"},
 *    "satNavDirections":"Europa-Allee 44, 60327 Frankfurt."
 *    }
 *   }
 * }
 */
export class HotelAddressBySlug {
  [key: string]: unknown;
  city?: string;
  fullAddress?: string;
  postcode?: string;
  satNavDirections?: string;

  /**
   * HotelAddressBySlug constructor
   * @param data object data
   * @param data.hotelAddressApiResponse response from API
   */
  constructor(data: { hotelAddressApiResponse?: { address: Record<string, unknown>; satNavDirections?: string } } = {}) {
    const hotelAddressApiResponse = data.hotelAddressApiResponse;
    const address = hotelAddressApiResponse?.address ?? {};
    this.fullAddress = `${address.addressLine1}, ${address.addressLine2}`;
    this.city = address.addressLine3 as string | undefined;
    this.postcode = address.postalCode as string | undefined;
    this.satNavDirections = hotelAddressApiResponse?.satNavDirections;
  }

  static fromResponse(data: { hotelAddressApiResponse?: { address: Record<string, unknown>; satNavDirections?: string } }): HotelAddressBySlug {
    return new HotelAddressBySlug(data);
  }
}
