/**
 * response example:
 * {
   "data":{
      "hotelInformation":{
         "address":{
            "addressLine1":"4-12 Whimbrel Place",
            "addressLine2":"Fife Leisure Park",
            "addressLine3":"Dunfermline",
            "postalCode":"KY11 8EX",
            "country":"United Kingdom (the)"
         },
         "name":"Dunfermline"
      }
   }
}
 */
export class HotelAddressCCUI {
  [key: string]: unknown;
  addressLine1?: string;
  addressLine2?: string;
  addressLine3?: string;
  addressLine4?: string;
  country?: string;
  postalCode?: string;

  /**
   * HotelAddressCCUI constructor
   * @param data object data
   * @param data.hotelAddress hotelAddress
   */
  constructor(data: { hotelAddress?: Record<string, unknown> } = {}) {
    const hotelAddress = data.hotelAddress ?? {};
    this.addressLine1 = hotelAddress.addressLine1 as string | undefined;
    this.addressLine2 = hotelAddress.addressLine2 as string | undefined;
    this.addressLine3 = hotelAddress.addressLine3 as string | undefined;
    this.postalCode = hotelAddress.postalCode as string | undefined;
    this.country = hotelAddress.country as string | undefined;
  }

  static fromResponse(data: { hotelAddress?: Record<string, unknown> }): HotelAddressCCUI {
    return new HotelAddressCCUI(data);
  }

  /**
   * Build the full address as a string for the current address object
   * @returns fullAddress
   */
  async getFullAddress(): Promise<string> {
    let fullAddress = '';
    fullAddress += `${this.addressLine1}, `;
    fullAddress += `${this.addressLine2}, `;
    fullAddress += `${this.addressLine3}, `;
    if (this.addressLine4) {
      fullAddress += `${this.addressLine4}, `;
    }
    fullAddress += this.postalCode;
    return fullAddress;
  }

}
