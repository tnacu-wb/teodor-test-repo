import { Constants } from '../../test-data/constants';
import { Locales } from '../../test-data/locales';
import type { SearchCriteria } from '../../test-data/searchCriteria';
import { Place } from './place';
import { Room } from './room';

/**
 Example of input object for Hotel Availabilities request (executed when the user is searching available hotels for a specific location)
{
    "oldWorldChannel": "WEB",
    "country": "de",
    "endDate": "2022-11-09",
    "initialPageSize": "40",
    "language": "de",
    "lazyLoadPageSize": "10",
    "page": 2,
    "place": {
        "location": "ChIJdd4hrwug2EcRmSrV3Vo6llI",
        "locationFormat": "PLACEID",
        "radius": 40,
        "radiusUnit": "KILOMETERS"
    },
    "ratePlanCodes": [],
    "rooms": [
        {
        "adultsNumber": 1,
        "childrenNumber": 0,
        "type": "SB"
        }
    ],
    "sort": "DISTANCE",
    "startDate": "2022-11-08",
    "filters": ["LFT","CPP"],
    "channel": "PI",
    "subChannel": "WEB"
}
 */
export class HotelAvailabilitiesInput {
  [key: string]: unknown;
  channel?: string;
  country?: string;
  endDate?: string;
  filters?: string[];
  initialPageSize?: number;
  language?: string;
  lazyLoadPageSize?: number;
  oldWorldChannel?: string;
  page?: number;
  place?: Place;
  ratePlanCodes?: string[];
  rooms: Room[] = [];
  sort?: string;
  sortOption?: { rcPriceModifier: number; rcDistanceModifier: number; rcHubModifier: number };
  startDate?: string;
  subChannel?: string;

  /**
   * HotelAvailabilitiesInput constructor
   * @param data object data
   * @param data.startDate Start date
   * @param data.endDate End date
   * @param data.country Country
   * @param data.language Language
   * @param data.oldWorldChannel Booking channel
   * @param data.channel Booking channel
   * @param data.subChannel Booking channel
   * @param data.page Page number
   * @param data.initialPageSize Initial page size (number of results displayed on page 1)
   * @param data.lazyLoadPageSize Number of results displayed from page 2 to the last page
   * @param data.sort Sorting field
   * @param data.place place object
   * @param data.rooms array with rooms details
   * @param data.filters array with used filters
   * @param data.ratePlanCodes array with ratePlanCodes
   */
  constructor(
    data: {
      startDate?: string;
      endDate?: string;
      country?: string;
      language?: string;
      oldWorldChannel?: string;
      channel?: string;
      subChannel?: string;
      page?: number;
      initialPageSize?: number;
      lazyLoadPageSize?: number;
      sort?: string;
      sortOption?: { rcPriceModifier: number; rcDistanceModifier: number; rcHubModifier: number };
      place?: Place;
      rooms?: Array<{ adultsNumber?: number; childrenNumber?: number; roomType: { id: string } }>;
      filters?: string[];
      ratePlanCodes?: string[];
    } = {},
  ) {
    this.startDate = data.startDate;
    this.endDate = data.endDate;
    this.country = data.country;
    this.language = data.language;
    this.oldWorldChannel = data.oldWorldChannel;
    this.channel = data.channel;
    this.subChannel = data.subChannel;
    this.page = data.page;
    this.initialPageSize = data.initialPageSize;
    this.lazyLoadPageSize = data.lazyLoadPageSize;
    this.sort = data.sort;
    this.sortOption = data.sortOption;
    this.place = data.place;
    this.filters = data.filters;
    this.ratePlanCodes = data.ratePlanCodes;

    const rooms = data.rooms ?? [];
    this.rooms = rooms.map((room) => new Room({ adultsNumber: room.adultsNumber, childrenNumber: room.childrenNumber, type: room.roomType.id }));
  }

  static fromRequest(data: {
    startDate?: string;
    endDate?: string;
    country?: string;
    language?: string;
    oldWorldChannel?: string;
    channel?: string;
    subChannel?: string;
    page?: number;
    initialPageSize?: number;
    lazyLoadPageSize?: number;
    sort?: string;
    sortOption?: { rcPriceModifier: number; rcDistanceModifier: number; rcHubModifier: number };
    place?: Place;
    rooms?: Array<{ adultsNumber?: number; childrenNumber?: number; roomType: { id: string } }>;
    filters?: string[];
    ratePlanCodes?: string[];
  }): HotelAvailabilitiesInput {
    return new HotelAvailabilitiesInput(data);
  }

  /**
   * Create hotel availabilities input for a specific search criteria
   * @param searchCriteria search criteria object
   * @param filters filters array of filter codes used to retrieve available hotels when filtered by
   * @param ratePlanCodes ratePlanCodes array of ratePlanCodes used to retrieve available hotels
   * @returns HotelAvailabilitiesInput object that keeps the data for the request
   */
  static async createInputForSearchCriteria(
    searchCriteria: SearchCriteria,
    filters: string[] = [],
    ratePlanCodes: string[] = [],
  ): Promise<HotelAvailabilitiesInput> {
    const locationId = searchCriteria.location.id;
    const localeString = String((global.browser?.options as Record<string, unknown> | undefined)?.locale ?? 'gb-en');
    const locale = Locales.getLocaleByString(localeString);
    // Radius unit for English is 'MILES' and for German is 'KILOMETERS'; reference has no radius value constant, so radius stays undefined
    const radiusUnit = localeString === Locales.GB_EN.name ? Constants.SEARCH_RESULTS_RADIUS_UNIT_EN : Constants.SEARCH_RESULTS_RADIUS_UNIT_DE;
    const runtimeOptions = (global.browser?.options as Record<string, unknown> | undefined) ?? {};
    const channel = String(runtimeOptions.app ?? '').toUpperCase();

    return new HotelAvailabilitiesInput({
      startDate: searchCriteria.arrivalDate.toISOString().slice(0, 10),
      endDate: searchCriteria.departureDate.toISOString().slice(0, 10),
      country: locale.country,
      language: locale.language,
      oldWorldChannel: 'WEB',
      channel,
      subChannel: 'WEB',
      initialPageSize: 40,
      lazyLoadPageSize: 10,
      rooms: searchCriteria.rooms,
      place: new Place({ location: locationId, locationFormat: 'PLACEID', radiusUnit }),
      page: 1,
      sort: 'DISTANCE',
      filters,
      ratePlanCodes,
    });
  }

  /**
   * Create hotel availabilities input for a specific search criteria with RECOMMENDATION sort and sortOption
   * @param searchCriteria search criteria object
   * @param filters filters array of filter codes used to retrieve available hotels when filtered by
   * @param ratePlanCodes ratePlanCodes array of ratePlanCodes used to retrieve available hotels
   * @param sortOption sorting option object with RECOMMENDATION modifiers (rcPriceModifier, rcDistanceModifier, rcHubModifier) used to sort available hotels by recommendation score
   * @returns HotelAvailabilitiesInput object with RECOMMENDATION sort and sortOption (rcPriceModifier, rcDistanceModifier, rcHubModifier)
   */
  static async createInputForSearchCriteriaForSortOptions(
    searchCriteria: SearchCriteria,
    filters: string[] = [],
    ratePlanCodes: string[] = [],
    sortOption: { rcPriceModifier: number; rcDistanceModifier: number; rcHubModifier: number } = {
      rcPriceModifier: 1,
      rcDistanceModifier: 1,
      rcHubModifier: 0.85,
    },
  ): Promise<HotelAvailabilitiesInput> {
    const locationId = searchCriteria.location.id;
    const localeString = String((global.browser?.options as Record<string, unknown> | undefined)?.locale ?? 'gb-en');
    const locale = Locales.getLocaleByString(localeString);
    const radiusUnit = localeString === Locales.GB_EN.name ? Constants.SEARCH_RESULTS_RADIUS_UNIT_EN : Constants.SEARCH_RESULTS_RADIUS_UNIT_DE;
    const runtimeOptions = (global.browser?.options as Record<string, unknown> | undefined) ?? {};
    const channel = String(runtimeOptions.app ?? '').toUpperCase();

    return new HotelAvailabilitiesInput({
      startDate: searchCriteria.arrivalDate.toISOString().slice(0, 10),
      endDate: searchCriteria.departureDate.toISOString().slice(0, 10),
      country: locale.country,
      language: locale.language,
      oldWorldChannel: 'WEB',
      channel,
      subChannel: 'WEB',
      initialPageSize: 40,
      lazyLoadPageSize: 10,
      rooms: searchCriteria.rooms,
      place: new Place({ location: locationId, locationFormat: 'PLACEID', radiusUnit }),
      page: 1,
      sort: 'RECOMMENDATION',
      sortOption,
      filters,
      ratePlanCodes,
    });
  }

}
