/**
 * Response example
 * {
   "countries":[
      {
         "countryCode":"A",
         "countryCodeISO":"AT",
         "countryLegend":"Austria",
         "passportRequired":true,
         "dialingCode":"+43",
         "flagImg":"/content/dam/global/flags/Austria.png"
      },
      {
         "countryCode":"AFG",
         "countryCodeISO":"AF",
         "countryLegend":"Afghanistan",
         "passportRequired":true,
         "dialingCode":"+93",
         "flagImg":"/content/dam/global/flags/Afghanistan.png"
      }
   ]
}
 */
export class Country {
  [key: string]: unknown;
  countryCode?: string;
  countryCodeISO?: string;
  countryLegend?: string;
  dialingCode?: string;
  flagImg?: string;
  passportRequired?: boolean;

  /**
   * Country constructor
   * @param data object data
   * @param data.countryApiResponse response from API
   */
  constructor(data: { countryApiResponse?: Record<string, unknown> } = {}) {
    const countryApiResponse = data.countryApiResponse ?? {};
    this.countryCode = countryApiResponse.countryCode as string | undefined;
    this.countryCodeISO = countryApiResponse.countryCodeISO as string | undefined;
    this.countryLegend = countryApiResponse.countryLegend as string | undefined;
    this.passportRequired = countryApiResponse.passportRequired as boolean | undefined;
    this.dialingCode = countryApiResponse.dialingCode as string | undefined;
    this.flagImg = countryApiResponse.flagImg as string | undefined;
  }

  static fromResponse(data: { countryApiResponse?: Record<string, unknown> }): Country {
    return new Country(data);
  }
}
