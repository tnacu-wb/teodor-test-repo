import { AddressByPostcode } from './addressByPostcode';

/**
 * The addresses suggestions list from api response based on postcode
 */
export class AddressesSuggestionsByPostcode {
  [key: string]: unknown;
  addresses: AddressByPostcode[] = [];

  /**
   * AddressesSuggestionsByPostcode constructor
   * @param data object data
   * @param data.addressesSuggestionsApiResponse response from API
   */
  constructor(data: { addressesSuggestionsApiResponse?: { partialAddress?: Array<Record<string, unknown>> } } = {}) {
    const partialAddress = data.addressesSuggestionsApiResponse?.partialAddress ?? [];
    this.addresses = partialAddress.map((address) => new AddressByPostcode({ addressApiResponse: address }));
  }

  static fromResponse(data: { addressesSuggestionsApiResponse?: { partialAddress?: Array<Record<string, unknown>> } }): AddressesSuggestionsByPostcode {
    return new AddressesSuggestionsByPostcode(data);
  }

}
