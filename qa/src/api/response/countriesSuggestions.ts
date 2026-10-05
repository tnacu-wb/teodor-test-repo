import { Country } from './country';

/**
 * The countries suggestions list from api response
 */
export class CountriesSuggestions {
  [key: string]: unknown;
  countries: Country[] = [];

  /**
   * CountriesSuggestions constructor
   * @param data object data
   * @param data.countriesSuggestionsApiResponse response from API
   */
  constructor(data: { countriesSuggestionsApiResponse?: { countries?: Array<Record<string, unknown>> } } = {}) {
    const countries = data.countriesSuggestionsApiResponse?.countries ?? [];
    this.countries = countries.map((country) => new Country({ countryApiResponse: country }));
  }

  static fromResponse(data: { countriesSuggestionsApiResponse?: { countries?: Array<Record<string, unknown>> } }): CountriesSuggestions {
    return new CountriesSuggestions(data);
  }

  /**
   * Filter list of suggested countries from AEM to find the country name given the country code
   * @param countryCode country code
   */
  async getCountryNameByCountryCodeFromAem(countryCode?: string): Promise<string | undefined> {
    const filteredArray = this.countries.filter((country) => country.countryCodeISO === countryCode);
    return filteredArray[0]?.countryLegend as string | undefined;
  }

  /**
   * Filter list of suggested countries from AEM to find the country code given the country name
   * @param countryName country name
   */
  async getCountryCodeByCountryNameFromAem(countryName?: string): Promise<string | undefined> {
    const matchedCountries = this.countries.filter((country) => country.countryLegend === countryName);
    if (matchedCountries.length === 0) {
      throw new Error(`Country not found: ${countryName}`);
    }
    return matchedCountries[0].countryCodeISO as string | undefined;
  }

}
