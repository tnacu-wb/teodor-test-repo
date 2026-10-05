/**
 The privacy policy information for a hotel from API response
 response example:
{
  "data": {
    "packages": {
        "hotelHasCityTaxForBusiness" : "true",
        "hotelHasCityTaxForLeisure" : "false"
    }
  }
}
 */
export class HotelCityTax {
  [key: string]: unknown;
  hotelHasCityTaxForBusiness?: boolean;
  hotelHasCityTaxForLeisure?: boolean;

  /**
   * Hotel City Tax Constructor
   * @param data object data
   * @param data.paymentPackagesApiResponse response from Api
   */
  constructor(data: { paymentPackagesApiResponse?: Record<string, unknown> } = {}) {
    const paymentPackagesApiResponse = data.paymentPackagesApiResponse ?? {};
    this.hotelHasCityTaxForBusiness = paymentPackagesApiResponse.hotelHasCityTaxForBusiness as boolean | undefined;
    this.hotelHasCityTaxForLeisure = paymentPackagesApiResponse.hotelHasCityTaxForLeisure as boolean | undefined;
  }

  static fromResponse(data: { paymentPackagesApiResponse?: Record<string, unknown> }): HotelCityTax {
    return new HotelCityTax(data);
  }
}
