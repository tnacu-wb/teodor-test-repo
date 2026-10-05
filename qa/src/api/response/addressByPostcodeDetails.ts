/**
 * Response example
 * {
   "postcode":"SW1W 0NY",
   "label":"Flat 1, Belgravia Court, 33 Ebury Street, LONDON, SW1W 0NY",
   "line1":"Flat 1, Belgravia Court",
   "line2":"33 Ebury Street",
   "line3": "",
   "line4":"LONDON",
   "country":"GB",
   "companyName": "",
}
 
 * Details of address returned by api
 */
export class AddressByPostcodeDetails {
  [key: string]: unknown;
  companyName?: string;
  country?: string;
  label?: string;
  line1?: string;
  line2?: string;
  line3?: string;
  line4?: string;
  postcode?: string;

  /**
   * AddressByPostcodeDetails constructor
   * @param data object data
   * @param data.addressDetailsApiResponse response from API
   */
  constructor(data: { addressDetailsApiResponse?: Record<string, unknown> } = {}) {
    const addressDetailsApiResponse = data.addressDetailsApiResponse ?? {};
    this.postcode = addressDetailsApiResponse.postcode as string | undefined;
    this.label = addressDetailsApiResponse.label as string | undefined;
    this.line1 = addressDetailsApiResponse.line1 as string | undefined;
    this.line2 = addressDetailsApiResponse.line2 as string | undefined;
    this.line3 = addressDetailsApiResponse.line3 as string | undefined;
    this.line4 = addressDetailsApiResponse.line4 as string | undefined;
    this.country = addressDetailsApiResponse.country as string | undefined;
    this.companyName = addressDetailsApiResponse.companyName as string | undefined;
  }

  static fromResponse(data: { addressDetailsApiResponse?: Record<string, unknown> }): AddressByPostcodeDetails {
    return new AddressByPostcodeDetails(data);
  }
}
