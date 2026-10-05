import { Strings } from '../../test-data/strings';

/**
 * response example:
 *{
 *    "data": {
 *        "hotelInformation": {
 *            "address": {
 *                "addressLine1": "1 Lochside Court",
 *                "addressLine2": "Edinburgh Park",
 *                "addressLine3": "Edinburgh",
 *                "addressLine4": null,
 *                "postalCode": "EH12 9FX",
 *                "country":"United Kingdom (the)"
 *            },
 *        }
 *    }
 *}
 */
export class HotelAddressByHotelInformation {
  [key: string]: unknown;
  addressLine1?: string;
  addressLine2?: string;
  addressLine3?: string;
  addressLine4?: string;
  country?: string;
  postalCode?: string;

  /**
   * HotelAddressByHotelInformation constructor
   * @param data object data
   * @param data.hotelAddress hotelAddress
   */
  constructor(data: { hotelAddress?: Record<string, unknown> } = {}) {
    const hotelAddress = data.hotelAddress ?? {};
    this.addressLine1 = hotelAddress.addressLine1 as string | undefined;
    this.addressLine2 = hotelAddress.addressLine2 as string | undefined;
    this.addressLine3 = hotelAddress.addressLine3 as string | undefined;
    this.addressLine4 = hotelAddress.addressLine4 as string | undefined;
    this.postalCode = hotelAddress.postalCode as string | undefined;
    this.country = hotelAddress.country as string | undefined;
  }

  static fromResponse(data: { hotelAddress?: Record<string, unknown> }): HotelAddressByHotelInformation {
    return new HotelAddressByHotelInformation(data);
  }

  /**
   * Build the full address as a string for the current address object
   * @param hotelCountry expected hotelCountry
   * @returns fullAddress
   */
  async getFullAddress(hotelCountry?: string): Promise<string> {
    let fullAddress = '';
    fullAddress += `${this.addressLine1}, `;
    // TODO : workaround until bug https://whitbreadis.atlassian.net/browse/DNRQ-64265 is fixed
    // remove if and keep only code from else condition
    if (hotelCountry === (await Strings.GERMANY.name)) {
      if (this.addressLine3) {
        fullAddress += `, ${this.addressLine3}`;
      }
      if (this.addressLine4) {
        fullAddress += `, ${this.addressLine4}`;
      }
      if (this.postalCode) {
        fullAddress += this.postalCode;
      }
      fullAddress += ` ${this.addressLine2}`;
    } else {
      fullAddress += this.addressLine2;
      if (this.addressLine3) {
        fullAddress += `, ${this.addressLine3}`;
      }
      if (this.addressLine4) {
        fullAddress += `, ${this.addressLine4}`;
      }
      if (this.postalCode) {
        fullAddress += `, ${this.postalCode}`;
      }
    }
    return fullAddress;
  }

  /**
   * Build the address depending on the country that the current address object has
   * @param hotelCountry expected hotelCountry
   * @returns partialAddress
   */
  async getPartialAddressDependingOnTheCountry(hotelCountry?: string): Promise<string> {
    let partialAddress = this.addressLine1 ?? '';
    if (hotelCountry === (await Strings.GERMANY.name)) {
      if (this.postalCode) {
        partialAddress += `, ${this.postalCode}`;
      }
    }
    if (this.addressLine2) {
      partialAddress += `, ${this.addressLine2}`;
    }
    if (this.addressLine3) {
      partialAddress += `, ${this.addressLine3}`;
    }
    if (hotelCountry !== (await Strings.GERMANY.name)) {
      if (this.postalCode) {
        partialAddress += `, ${this.postalCode}`;
      }
    }
    return partialAddress;
  }

}
