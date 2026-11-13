import { ApiFixtures } from '../apiFixtures';
import { request as playwrightRequest, type APIRequestContext } from '@playwright/test';
import { GraphQLClient } from './graphqlClient';
import { getEnvironmentConfig, GetEnvironmentConfigOptions } from '../../../config/environments';
import { Constants } from '../../test-data/constants';
import { Hotels } from '../../test-data/hotels';
import { HotelBrands } from '../../test-data/hotelBrands';
import { HotelRates } from '../../test-data/hotelRates';
import { getCurrentLocale, withLocaleDefaults } from '../../test-data/locales';
import { AuthZeroApiCalls } from '../authZeroApiCalls';
import { asObject, getBrowserOptions, isApiRequestContext } from '../apiValueUtils';
import { BookingChannel, HotelAvailabilitiesInput } from '../requests';
import {
  AccountCompanyDetails,
  AncillariesAdultMeal,
  AncillariesChildMeal,
  AncillariesExtraItems,
  AncillariesPrivacyPolicies,
  AnonymousNewsletterPreferences,
  AddressByPostcodeDetails,
  AddressesSuggestionsByPostcode,
  HotelAvailabilityCard,
  HotelAvailabilities,
  HotelCityTax,
  HotelInventory,
  HotelMapViewDLP,
  HotelTripAdvisorReviews,
  LocationSuggestions,
  MealPackages,
  BookingHistoryTotals,
  NewsletterSignup,
  PaymentPrivacyPolicies,
  RestaurantDetails,
  RoomSelection,
  RequestedCompany,
  FindBooking,
  ManageBooking,
  SearchBookings,
  AccountUpcomingSpending,
  UpcomingBooking,
  CancelledCardDetails,
  UserProfileDetails,
} from '../response';

export interface GraphQLResponse<T = unknown> {
  body: { data: T };
  errors?: unknown[];
  text: string;
  status: number;
  statusCode: number;
}

interface AncillariesInputData {
  hotelId?: string;
  nightsNumber?: number;
  startDate?: Date | string;
  endDate?: Date | string;
  adultsNumber?: number;
  childrenNumber?: number;
  bookingFlowId?: string | null;
  basketReferenceId?: string | null;
  isManageBookingPage?: boolean;
  bookingChannel?: BookingChannel;
  channel?: string;
  country?: string;
  language?: string;
  rooms?: Array<{ adultsNumber?: number; childrenNumber?: number }>;
}

interface HotelAvailabilitiesParams {
  hotelAvailabilitiesInput: HotelAvailabilitiesInput;
  loggedUser?: boolean;
  failIfError?: boolean;
}

interface RoomSubstitutionLimitations {
  substitutionList?: Array<{ type?: string }>;
  [key: string]: unknown;
}

interface CancellationPolicy {
  [key: string]: unknown;
}

type PaymentApiResponse = Omit<GraphQLResponse<unknown>, 'body'> & {
  body: { data: Record<string, unknown> };
};

interface CancellationReason {
  [key: string]: unknown;
}

/** Methods for accessing Premier Inn API backend resources. */
export class ApiCalls {
  [key: string]: unknown;

  constructor(data: Record<string, unknown> = {}) {
    const values = Object.values(data);
    const payload = values.length === 1 && values[0] && typeof values[0] === 'object' && !Array.isArray(values[0])
      ? values[0] as Record<string, unknown>
      : data;

    Object.assign(this, payload);
  }

  static fromApiData<T extends Record<string, unknown>>(data: T): ApiCalls {
    return new ApiCalls(data);
  }

  private static getLocaleData() {
    return getCurrentLocale();
  }

  private static toIsoDayDate(value: unknown): string {
    const date = value instanceof Date ? value : new Date(String(value ?? ''));
    if (Number.isNaN(date.getTime())) {
      return '';
    }
    const year = date.getUTCFullYear();
    const month = `${date.getUTCMonth() + 1}`.padStart(2, '0');
    const day = `${date.getUTCDate()}`.padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  private static readonly asObject = asObject;

  private static normalizeAncillariesInput(input: unknown = {}): Record<string, unknown> {
    const data = ApiCalls.asObject(input);
    const locale = ApiCalls.getLocaleData();
    const nightsNumber = Number(data.nightsNumber ?? 1);
    const startDate = data.startDate ?? new Date();
    const endDate = data.endDate ?? new Date(Date.now() + nightsNumber * 24 * 60 * 60 * 1000);
    const bookingChannel = ApiCalls.asObject(data.bookingChannel);

    return {
      hotelId: String(data.hotelId ?? Hotels.DEFAULT_HOTEL.id),
      startDate: ApiCalls.toIsoDayDate(startDate),
      endDate: ApiCalls.toIsoDayDate(endDate),
      adultsNumber: Number(data.adultsNumber ?? 1),
      childrenNumber: Number(data.childrenNumber ?? 0),
      country: String(data.country ?? locale.country),
      language: String(data.language ?? locale.language),
      nightsNumber,
      bookingFlowId: String(data.bookingFlowId ?? 'booking-a1'),
      basketReferenceId: data.basketReferenceId ?? 'null',
      channel: String(bookingChannel.channel ?? data.channel ?? 'PI'),
    };
  }

  private static getNested<T = unknown>(value: unknown, path: string): T | undefined {
    const keys = path.split('.');
    let current: unknown = value;
    for (const key of keys) {
      if (!current || typeof current !== 'object') {
        return undefined;
      }
      current = (current as Record<string, unknown>)[key];
    }
    return current as T;
  }

  private static readonly isApiRequestContext = isApiRequestContext;

  static getRequestContext(request?: APIRequestContext): APIRequestContext {
    if (request) {
      return request;
    }

    if (!global.page) {
      throw new Error('global.page is not available. Make sure the Playwright fixture has initialized the page before using ApiCalls.');
    }

    return global.page.context().request;
  }

  private static readonly getBrowserOptions = getBrowserOptions;

  private static async getCookieByName(name: string): Promise<string> {
    if (!global.page) {
      return '';
    }

    const cookies = await global.page.context().cookies();
    const cookie = cookies.find((item) => item.name === name);
    return cookie?.value ?? '';
  }

  /** 
   * Make the GraphQL Experience API call and return the response 
   * @param {String} queryFile the file from where to get the query or mutation for GraphQL call
   * @param {Object} variables the variables data used for GraphQL call
   * @param {Boolean} failIfError true if it should fail the run if it is a response error.
   * @param {Boolean} loggedUser if user is logged in
   * @param {String} username email address of the user in case user is not logged in
   * @param {String} password user password in case user is not logged in
   * @returns the response
   */
  static async makeGraphqlCall<T = unknown>(queryFile: string, variables?: Record<string, unknown>, failIfError?: boolean, loggedUser?: boolean, username?: string, password?: string): Promise<GraphQLResponse<T>>;
  static async makeGraphqlCall<T = unknown>(request: APIRequestContext, queryFile: string, variables?: Record<string, unknown>, failIfError?: boolean, loggedUser?: boolean, username?: string, password?: string): Promise<GraphQLResponse<T>>;
  static async makeGraphqlCall<T = unknown>(...args: unknown[]): Promise<GraphQLResponse<T>> {
    let request: APIRequestContext | undefined;
    let queryFile: string;
    let graphQLVariables: Record<string, unknown> = {};
    let shouldFailIfError = true;
    let loggedUser = false;
    let username: string | undefined;
    let password: string | undefined;

    if (ApiCalls.isApiRequestContext(args[0])) {
      request = args[0];
      queryFile = args[1] as string;
      graphQLVariables = (args[2] as Record<string, unknown> | undefined) ?? {};
      shouldFailIfError = (args[3] as boolean | undefined) ?? true;
      loggedUser = (args[4] as boolean | undefined) ?? false;
      username = args[5] as string | undefined;
      password = args[6] as string | undefined;
    } else {
      queryFile = args[0] as string;
      graphQLVariables = (args[1] as Record<string, unknown> | undefined) ?? {};
      shouldFailIfError = (args[2] as boolean | undefined) ?? true;
      loggedUser = (args[3] as boolean | undefined) ?? false;
      username = args[4] as string | undefined;
      password = args[5] as string | undefined;
    }

    if (typeof queryFile !== 'string') {
      throw new Error('queryFile must be provided when calling ApiCalls.makeGraphqlCall with APIRequestContext.');
    }

    const query = await ApiFixtures.readGraphqlFixture(queryFile);
    const requestContext = ApiCalls.getRequestContext(request);
    const options = ApiCalls.getBrowserOptions();
    const app = String(options.app ?? '').toLowerCase();
    const headers: Record<string, string> = {
      Accept: '*/*',
      'X-WHIT-API-KEY': String(options.graphQlXWhitApiKey ?? ''),
    };

    if (app === 'ccui' && loggedUser) {
      headers.Authorization = `Bearer ${String(await AuthZeroApiCalls.getCcuiAuthZeroToken(requestContext))}`;
    } else if (app === 'pib' && !loggedUser && username && password) {
      headers.Authorization = `Bearer ${String(await AuthZeroApiCalls.getIBAuthZeroToken(requestContext, username, password))}`;
    } else {
      const idToken = loggedUser ? await ApiCalls.getCookieByName('id_token_cookie') : '';
      if (idToken) {
        headers.Authorization = `Bearer ${idToken}`;
      }
    }

    const apiBaseURL = getEnvironmentConfig(browser.options as GetEnvironmentConfigOptions).apiBaseUrl;
    const client = new GraphQLClient(requestContext, apiBaseURL, { headers });
    try {
      const data = await client.query<T>(query, graphQLVariables as Record<string, unknown>);

      return {
        body: { data },
        text: JSON.stringify({ data }),
        status: 200,
        statusCode: 200,
      };
    } catch (error) {
      if (shouldFailIfError) {
        throw error;
      }

      const errorMessage = error instanceof Error ? error.message : String(error);
      return {
        body: { data: {} as T },
        errors: [{ message: errorMessage }],
        text: JSON.stringify({ errors: [{ message: errorMessage }] }),
        status: 500,
        statusCode: 500,
      };
    }
  }

  /**
   * Get Autocomplete Location Suggestions List
   * @param {String} searchedText is the text that will be used to get the suggestions list
   * @returns {LocationSuggestions} LocationSuggestions object containing suggestions for places and properties(hotels)
   */
  static async getAutocompleteLocationSuggestionsList(...args: [inputData?: { searchedText?: string }]): Promise<LocationSuggestions> {
    const inputData = args.length === 1 && args[0] && typeof args[0] === 'object' && !Array.isArray(args[0])
      ? args[0] as { searchedText?: string }
      : {};
    const searchedText = inputData.searchedText ?? '';

    const configuredEndpoint = (global.browser?.options as Record<string, unknown> | undefined)?.autocompleteEndpoint;
    const endpoint = typeof configuredEndpoint === 'string' && configuredEndpoint.length > 0
      ? configuredEndpoint
      : `${getEnvironmentConfig(browser.options as GetEnvironmentConfigOptions).baseUrl}/autocomplete`;
    const url = `${endpoint}?input=/${encodeURIComponent(searchedText)}&gplaces[components]=country:uk|country:de`;

    const response = await ApiCalls.getRequestContext().get(url, { headers: { Accept: '*/*' } });
    if (!response.ok()) {
      throw new Error(`Autocomplete request failed with status ${response.status()} for URL ${url}`);
    }

    return new LocationSuggestions({ locationSuggestionsApiResponse: await response.json() as Record<string, unknown> });
  }

  /**
   * GraphQL query for GetAncillariesAdultMeals
   * @param {Object} inputData used to set the GraphQL variables for the get hotel packages experience call.
   * @param {String} inputData.hotelId hotelId
   * @param {Number} inputData.nightsNumber nightsNumber
   * @param {Date} inputData.startDate startDate
   * @param {Date} inputData.endDate endDate
   * @param {Number} inputData.adultsNumber adultsNumber
   * @param {Number} inputData.childrenNumber childrenNumber
   * @param {String} inputData.bookingFlowId bookingFlowId
   * @param {String} inputData.basketReferenceId basketReferenceId
   * @param {BookingChannel} inputData.bookingChannel the booking channel data
   * @param {String} inputData.country country
   * @param {String} inputData.language language
   * @returns {Array.<AncillariesAdultMeal>}  returns an array with adult meal items
   */
  static async graphqlGetAncillariesAdultMeals(...args: [inputData?: AncillariesInputData]): Promise<AncillariesAdultMeal[]> {
    const variables = ApiCalls.normalizeAncillariesInput(args[0]);
    const response = await ApiCalls.makeGraphqlCall('getAncillariesPackages.graphql', variables);
    const adultMeals = ApiCalls.getNested<Array<Record<string, unknown>>>(response.body.data, 'packages.packages.meals') ?? [];
    return adultMeals.map((adultMeal) => new AncillariesAdultMeal({ adultMeal }));
  }

  /**
   * GraphQL query for GetAncillariesEciExtras
   * @param {Object} inputData used to set the GraphQL variables for the get hotel packages experience call.
   * @param {String} inputData.hotelId hotelId
   * @param {Number} inputData.nightsNumber nightsNumber
   * @param {Date} inputData.startDate startDate
   * @param {Date} inputData.endDate endDate
   * @param {Number} inputData.adultsNumber adultsNumber
   * @param {Number} inputData.childrenNumber childrenNumber
   * @param {String} inputData.bookingFlowId bookingFlowId
   * @param {String} inputData.basketReferenceId basketReferenceId
   * @param {String} inputData.country country
   * @param {String} inputData.language language
   * @param {BookingChannel} inputData.bookingChannel the booking channel data
   * @returns {Array.<AncillariesExtraItems>}  returns an array with early check-in extra items
   */
  static async graphqlGetAncillariesEciExtras(...args: [inputData?: AncillariesInputData]): Promise<AncillariesExtraItems[]> {
    const variables = {
      ...ApiCalls.normalizeAncillariesInput(args[0]),
      isManageBookingPage: null,
    };
    const response = await ApiCalls.makeGraphqlCall('getAncillariesPackages.graphql', variables);
    const extrasItems = ApiCalls.getNested<Array<Record<string, unknown>>>(response.body.data, 'packages.packages.extrasItems') ?? [];
    return extrasItems
      .filter((item) => String(item.id ?? '') === Constants.EARLY_CHECK_IN_CODE)
      .map((extraItem) => new AncillariesExtraItems({ extraItem }));
  }

  /**
   * GraphQL query for GetAncillariesLcoExtras
   * @param {Object} inputData used to set the GraphQL variables for the get hotel packages experience call
   * @param {String} inputData.hotelId hotelId
   * @param {Number} inputData.nightsNumber nightsNumber
   * @param {Date} inputData.startDate startDate
   * @param {Date} inputData.endDate endDate
   * @param {Number} inputData.adultsNumber adultsNumber
   * @param {Number} inputData.childrenNumber childrenNumber
   * @param {String} inputData.bookingFlowId bookingFlowId
   * @param {String} inputData.basketReferenceId basketReferenceId
   * @param {String} inputData.country country
   * @param {String} inputData.language language
   * @param {BookingChannel} inputData.bookingChannel the booking channel data
   * @returns {Array.<AncillariesExtraItems>}  returns an array with Late check-out extra items
   */
  static async graphqlGetAncillariesLcoExtras(...args: [inputData?: AncillariesInputData]): Promise<AncillariesExtraItems[]> {
    const variables = ApiCalls.normalizeAncillariesInput(args[0]);
    const response = await ApiCalls.makeGraphqlCall('getAncillariesPackages.graphql', variables);
    const extrasItems = ApiCalls.getNested<Array<Record<string, unknown>>>(response.body.data, 'packages.packages.extrasItems') ?? [];
    return extrasItems
      .filter((item) => String(item.id ?? '') === Constants.LATE_CHECK_OUT_CODE)
      .map((extraItem) => new AncillariesExtraItems({ extraItem }));
  }

  /**
   * GraphQL query for GetAncillariesWiFiExtras
   * @param {Object} inputData used to set the GraphQL variables for the get hotel packages experience call.
   * @param {String} inputData.hotelId hotelId
   * @param {Number} inputData.nightsNumber nightsNumber
   * @param {Date} inputData.startDate startDate
   * @param {Date} inputData.endDate endDate
   * @param {Number} inputData.adultsNumber adultsNumber
   * @param {Number} inputData.childrenNumber childrenNumber
   * @param {String} inputData.bookingFlowId bookingFlowId
   * @param {String} inputData.basketReferenceId basketReferenceId
   * @param {String} inputData.country country
   * @param {String} inputData.language language
   * @param {BookingChannel} inputData.bookingChannel the booking channel data
   * @returns {Array.<AncillariesExtraItems>}  returns an array with early check-in extra items
   */
  static async graphqlGetAncillariesWiFiExtras(...args: [inputData?: AncillariesInputData]): Promise<AncillariesExtraItems[]> {
    const variables = {
      ...ApiCalls.normalizeAncillariesInput(args[0]),
      isManageBookingPage: null,
    };
    const response = await ApiCalls.makeGraphqlCall('getAncillariesPackages.graphql', variables);
    const extrasItems = ApiCalls.getNested<Array<Record<string, unknown>>>(response.body.data, 'packages.packages.extrasItems') ?? [];
    return extrasItems
      .filter((item) => String(item.id ?? '') === Constants.WIFI_CODE)
      .map((extraItem) => new AncillariesExtraItems({ extraItem }));
  }

  /**
   * GraphQL for GetAncillariesChildMeals
   * @param {Object} inputData used to set the GraphQL variables for the get hotel packages experience call.
   * @param {String} inputData.hotelId hotelId
   * @param {Number} inputData.nightsNumber nightsNumber
   * @param {Date} inputData.startDate startDate
   * @param {Date} inputData.endDate endDate
   * @param {Number} inputData.adultsNumber adultsNumber
   * @param {Number} inputData.childrenNumber childrenNumber
   * @param {String} inputData.bookingFlowId bookingFlowId
   * @param {String} inputData.country country
   * @param {String} inputData.language language
   * @param {String} inputData.basketReferenceId basketReferenceId
   * @returns {Array.<AncillariesAdultMeal>}  returns an array with child meal items
   */
  static async graphqlGetAncillariesChildMeals(...args: [inputData?: AncillariesInputData]): Promise<AncillariesChildMeal[]> {
    const variables = ApiCalls.normalizeAncillariesInput(args[0]);
    const response = await ApiCalls.makeGraphqlCall('getAncillariesPackages.graphql', variables);
    const childMeals = ApiCalls.getNested<Array<Record<string, unknown>>>(response.body.data, 'packages.packages.mealsKids') ?? [];
    return childMeals.map((kidsMeal) => new AncillariesChildMeal({ kidsMeal }));
  }

  /**
   * GraphQL query for GetAncillariesRestaurantDetails
   * @param {Object} inputData used to set the GraphQL variables for the get hotel packages experience call.
   * @param {String} inputData.hotelId hotelId
   * @param {Number} inputData.nightsNumber nightsNumber
   * @param {Date} inputData.startDate startDate
   * @param {Date} inputData.endDate endDate
   * @param {Number} inputData.adultsNumber adultsNumber
   * @param {Number} inputData.childrenNumber childrenNumber
   * @param {String} inputData.bookingFlowId bookingFlowId
   * @param {String} inputData.basketReferenceId basketReferenceId
   * @param {String} inputData.country country
   * @param {String} inputData.language language
   * @returns {RestaurantDetails}  returns an array with adult meal items
   */
  static async graphqlGetAncillariesRestaurantDetails(...args: [inputData?: AncillariesInputData]): Promise<RestaurantDetails> {
    const variables = ApiCalls.normalizeAncillariesInput(args[0]);
    const response = await ApiCalls.makeGraphqlCall('getAncillariesPackages.graphql', variables);
    const restaurantDetails = ApiCalls.getNested<Record<string, unknown>>(response.body.data, 'packages.restaurant') ?? {};
    return new RestaurantDetails({ restaurantDetails });
  }

  /** 
   * Eckoh payment Id generated every time the overlay is opened
   * @param {Object} inputData used to set the GraphQL variables for EckohPaymentId call.
   * @param {String} inputData.basketReference basketReference
   * @param {String} inputData.paymentOption payment option
   * @param {String} inputData.hotel request id
   * @returns {String} paymentId used for eckoh webhook call
   */
  static async graphqlGetEckohPaymentId(...args: [inputData?: Record<string, unknown>]): Promise<string> {
    const variables = args.length === 1 && args[0] && typeof args[0] === 'object' && !Array.isArray(args[0]) ? args[0] as Record<string, unknown> : {};
    const response = await ApiCalls.makeGraphqlCall('getEckohPaymentId.graphql', variables);
    return String(ApiCalls.getNested(response.body.data, 'initiateEckohPayment.paymentId') ?? '');
  }

  /** 
   * Get Eckoh status
   * @param {String} basketReference basketReference
   * @returns {String} eckohStatus 
   */
  static async getEckohStatus(...args: [basketReference: string] | [inputData: { basketReference?: string }]): Promise<string> {
    const basketReference = typeof args[0] === 'string' ? args[0] : String(ApiCalls.asObject(args[0]).basketReference ?? '');
    const response = await ApiCalls.makeGraphqlCall('getEckohStatus.graphql', { basketReference }, true, true);
    return String(ApiCalls.getNested(response.body.data, 'eckohRecordingStatus.status') ?? '');
  }

  /**
   * Retrieves booking flow id for a specific hotel based on rate plan code
   * @param {Object} data object data
   * @param {String} data.hotelId  hotelId
   * @param {String} data.ratePlanCode ratePlanCode
   * @param {Boolean} data.isForUI if the booking flow id is used for page url, then true. If it is for passing it to another API call, then it should be false.
   * @returns {Promise<string>} bookingFlowId
   */
  static async getBookingFlowIdBasedOnHotelAndRate(...args: unknown[]): Promise<string> {
    const data = ApiCalls.asObject(args[0]);
    const locale = ApiCalls.getLocaleData();
    const hotelId = String(data.hotelId ?? Hotels.DEFAULT_HOTEL.id);
    const ratePlanCode = String(data.ratePlanCode ?? HotelRates.PI_FLEX.classification);
    const isForUI = Boolean(data.isForUI ?? false);
    const response = await ApiCalls.makeGraphqlCall('getHotelInformation.graphql', {
      hotelId,
      country: String(data.country ?? locale.country),
      language: String(data.language ?? locale.language),
    });
    const app = String(ApiCalls.getBrowserOptions().app ?? '').toLowerCase();

    if (app === 'bb' || app === 'pib') {
      const brand = String(ApiCalls.getNested(response.body.data, 'hotelInformation.brand') ?? '');
      if (!isForUI && Hotels.getHotelById(hotelId).countryCode === 'de') {
        return 'booking-business-ct';
      }
      return brand === HotelBrands.HUB.name ? `booking-business-${brand.toLowerCase()}` : 'booking-business';
    }

    const bookingFlowItems = ApiCalls.getNested<Array<{ bookingId: string; rateCode: string }>>(
      response.body.data,
      'hotelInformation.bookingFlow.bookingFlowItems',
    ) ?? [];
    const bookingFlowItem = bookingFlowItems.find(item => item.rateCode === ratePlanCode);

    if (!bookingFlowItem) {
      throw new Error(
        `No booking flow item found for hotel '${hotelId}' with rate plan code '${ratePlanCode}'. ` +
        `Available rate codes: [${bookingFlowItems.map(item => item.rateCode).join(', ')}]`,
      );
    }

    return bookingFlowItem.bookingId;
  }

  /**
   * GraphQL query for GetHotelInventory
   * @param {Object} data object data
   * @param {String} data.hotelId hotelId
   * @param {String} data.dateRangeStart dateRangeStart
   * @param {String} data.dateRangeEnd dateRangeEnd
   * @returns {HotelInventory} HotelInventory object that holds hotel inventory response
   */
  static async graphqlGetHotelInventory(...args: [inputData?: { hotelId?: string; dateRangeStart?: string; dateRangeEnd?: string }]): Promise<HotelInventory> {
    const variables = args.length === 1 && args[0] && typeof args[0] === 'object' && !Array.isArray(args[0]) ? args[0] as Record<string, unknown> : {};
    const response = await ApiCalls.makeGraphqlCall('getHotelInventory.graphql', variables);
    const hotelInventory = ApiCalls.getNested<Record<string, unknown>>(response.body.data, 'hotelInventory') ?? {};
    return new HotelInventory({ hotelInventory });
  }

  /**
   * GraphQL query for room substitution limitations.
   * @param {Object} data object data
   * @param {Number} data.adults adults number
   * @param {Number} data.children children number
   * @param {String} data.channel booking channel enum value
   * @param {String} data.pms pms source
   * @param {String} data.roomType room type code
  * @returns {RoomSubstitutionLimitations} room substitution limitations response
   */
  static async graphqlGetRoomSubstitutions(...args: [inputData?: { adults?: number; children?: number; channel?: string; pms?: string; roomType?: string }]): Promise<RoomSubstitutionLimitations> {
    const data = ApiCalls.asObject(args[0]);
    const app = String((global.browser?.options as Record<string, unknown> | undefined)?.app ?? '').toLowerCase();
    const variables = {
      adults: Number(data.adults ?? 2),
      children: Number(data.children ?? 0),
      channel: String(data.channel ?? (app === 'pib' ? 'BB' : app.toUpperCase() || 'PI')),
      pms: String(data.pms ?? 'OP'),
      roomType: String(data.roomType ?? 'DB'),
    };
    const response = await ApiCalls.makeGraphqlCall('getRoomSubstitutionLimitations.graphql', variables);
    return ApiCalls.getNested<RoomSubstitutionLimitations>(response.body.data, 'roomSubstitutionLimitations') ?? {};
  }

  /**
   * GraphQL query for cancellation policies.
   * @param {Object} data object data
   * @param {String} data.hotelId hotel id
   * @param {String} data.arrivalDate arrival date (YYYY-MM-DD format)
   * @param {String} data.ratePlanCode rate plan code
   * @param {String} data.basketRef basket reference id
  * @returns {Array.<CancellationPolicy>} cancellation policies response entries
   */
  static async graphqlGetCancellationPolicies(...args: [inputData?: { hotelId?: string; arrivalDate?: string; ratePlanCode?: string; basketRef?: string }]): Promise<CancellationPolicy[]> {
    const variables = args.length === 1 && args[0] && typeof args[0] === 'object' && !Array.isArray(args[0]) ? args[0] as Record<string, unknown> : {};
    const response = await ApiCalls.makeGraphqlCall('getCancellationPolicies.graphql', variables);
    return ApiCalls.getNested<CancellationPolicy[]>(response.body.data, 'cancellationPolicies') ?? [];
  }

  /**
   * GraphQL query for GetAncillariesPrivacyPolicies
   * @param {Object} inputData used to set the GraphQL variables for the get hotel privacy policies experience call.
   * @param {String} inputData.hotelId hotelId
   * @param {Number} inputData.nightsNumber nightsNumber
   * @param {Date} inputData.startDate startDate
   * @param {Date} inputData.endDate endDate
   * @param {Number} inputData.adultsNumber adultsNumber
   * @param {Number} inputData.childrenNumber childrenNumber
   * @param {String} inputData.bookingFlowId bookingFlowId
   * @param {String} inputData.basketReferenceId basketReferenceId
   * @param {String} inputData.country country
   * @param {String} inputData.language language
   * @returns {AncillariesPrivacyPolicies}  AncillariesPrivacyPolicies object that keeps privacy policies from the response
   */
  static async graphqlGetAncillariesPrivacyPolicies(...args: [inputData?: AncillariesInputData]): Promise<AncillariesPrivacyPolicies> {
    const variables = ApiCalls.normalizeAncillariesInput(args[0]);
    const response = await ApiCalls.makeGraphqlCall('getAncillariesPackages.graphql', variables);
    const policy = ApiCalls.getNested<Record<string, unknown>>(response.body.data, 'packages.privacyPolicy') ?? {};
    return new AncillariesPrivacyPolicies({ policy });
  }

  /**
   * Get details for a specific suggested address by addressId
   * @param {Object} data object data
   * @param {String} data.postcode - postal code used to bring the list of suggested addresses
   * @param {String} data.addressId - id of a specific address returned by getAddressesSugestionsByPostcode(postcode)
   * @returns {AddressByPostcodeDetails} address by postcode details
   */
  static async getAddressByPostcodeDetails(...args: [inputData?: { postcode?: string; addressId?: string }]): Promise<AddressByPostcodeDetails> {
    const data = ApiCalls.asObject(args[0]);
    const postcode = encodeURIComponent(String(data.postcode ?? ''));
    const addressId = String(data.addressId ?? '');
    const endpointTemplate = String(ApiCalls.getBrowserOptions().addressesSuggestionsEndpoint ?? `${getEnvironmentConfig(browser.options as GetEnvironmentConfigOptions).baseUrl}/addresses/{{postcode}}`);
    const base = endpointTemplate.replace('{{postcode}}', postcode).replace(/\/$/, '');
    const url = `${base}/${addressId}`;

    const response = await ApiCalls.getRequestContext().get(url, { headers: { Accept: 'application/json' } });
    if (!response.ok()) {
      throw new Error(`getAddressByPostcodeDetails failed with status ${response.status()} for URL ${url}`);
    }

    return new AddressByPostcodeDetails({ addressDetailsApiResponse: await response.json() as Record<string, unknown> });
  }

  /**
   * Get user profile details
   * @param {Object} data object data
   * @param {String} data.emailAddress email address of user
   * @param {Boolean} data.business true if user is a company
   * @returns {UserProfileDetails} UserProfileDetails object that map the data from getUserProfileDetails response 
   */
  static async getUserProfileDetails(...args: [inputData?: { emailAddress?: string; business?: boolean }]): Promise<UserProfileDetails> {
    const data = ApiCalls.asObject(args[0]);
    const emailAddress = String(data.emailAddress ?? '');
    const business = String(Boolean(data.business ?? false));
    const options = ApiCalls.getBrowserOptions();
    const entityApiBaseUrl = String(options.entityApiBaseUrl ?? getEnvironmentConfig(browser.options as GetEnvironmentConfigOptions).apiBaseUrl.replace('/graphql', ''));
    const url = `${entityApiBaseUrl}:443/customers/hotels/${emailAddress}?business=${business}`;
    const token = await ApiCalls.getCookieByName('id_token_cookie');
    console.log(`Get user profile details for: ${emailAddress} and business: ${business}; bearer token present: ${Boolean(token)}`);
    const headers: Record<string, string> = { Accept: 'application/json' };
    headers.Authorization = `Bearer ${token}`;
    console.log(`Profile request headers: bearer=${Boolean(token)}`);

    const maxRetries = 5;
    const requestContext = await playwrightRequest.newContext();
    try {
      for (let attempt = 1; attempt <= maxRetries; attempt += 1) {
        const response = await requestContext.get(url, { headers });
        if (response.ok()) {
          return new UserProfileDetails({ userProfileDetailsApiResponse: await response.json() as Record<string, unknown> });
        }

        if (attempt === maxRetries) {
          const responseBody = (await response.text()).slice(0, 500);
          throw new Error(`getUserProfileDetails failed with status ${response.status()} for URL ${url}. Response: ${responseBody}`);
        }
      }
    } finally {
      await requestContext.dispose();
    }

    return new UserProfileDetails();
  }

  /**
   * Get requested company details
   * @param {Object} data object data
   * @param {String} data.companyId company id
   * @returns {RequestedCompany} RequestedCompany object that map the data from getRequestedCompanyDetails response 
   */
  static async getRequestedCompanyDetails(...args: [inputData?: { companyId?: string }]): Promise<RequestedCompany> {
    const data = ApiCalls.asObject(args[0]);
    const companyId = String(data.companyId ?? '');
    const options = ApiCalls.getBrowserOptions();
    const entityApiBaseUrl = String(options.entityApiBaseUrl ?? getEnvironmentConfig(browser.options as GetEnvironmentConfigOptions).apiBaseUrl.replace('/graphql', ''));
    const url = `${entityApiBaseUrl}/company/${companyId}`;
    const token = await ApiCalls.getCookieByName('id_token_cookie');
    const headers: Record<string, string> = { Accept: 'application/json' };
    if (token) {
      headers.Authorization = `Bearer ${token}`;
    }
    const response = await ApiCalls.getRequestContext().get(url, { headers });
    if (!response.ok()) {
      throw new Error(`getRequestedCompanyDetails failed with status ${response.status()} for URL ${url}`);
    }

    return new RequestedCompany({ company: await response.json() as Record<string, unknown> });
  }

  /**
   * GraphQL query for GetPaymentPrivacyPolicies
   * @param {Object} inputData used to set the GraphQL variables for the get hotel privacy policies experience call.
   * @param {String} inputData.hotelId hotelId
   * @param {Number} inputData.nightsNumber nightsNumber
   * @param {Date} inputData.startDate startDate
   * @param {Date} inputData.endDate endDate
   * @param {Number} inputData.adultsNumber adultsNumber
   * @param {Number} inputData.childrenNumber childrenNumber
   * @param {String} inputData.bookingFlowId bookingFlowId
   * @param {String} inputData.country country
   * @param {String} inputData.language language
   * @param {String} inputData.basketReferenceId basketReferenceId
   * @returns {PaymentPrivacyPolicies}  PaymentPrivacyPolicies object that keeps privacy policies from the response
   */
  static async graphqlGetPaymentPrivacyPolicies(...args: [inputData?: AncillariesInputData]): Promise<PaymentPrivacyPolicies> {
    const variables = ApiCalls.normalizeAncillariesInput(args[0]);
    const response = await ApiCalls.makeGraphqlCall('getPaymentPackages.graphql', variables);
    const policy = ApiCalls.getNested<Record<string, unknown>>(response.body.data, 'paymentPackages.privacyPolicy')
      ?? ApiCalls.getNested<Record<string, unknown>>(response.body.data, 'packages.privacyPolicy')
      ?? {};
    return new PaymentPrivacyPolicies({ policy });
  }

  /**
   * Get one single page of hotels available for a specific location
   * @param {Object} data object data
   * @param {HotelAvailabilitiesInput} data.hotelAvailabilitiesInput used to set the GraphQL variables for the hotel availabilities experience call.
   * @param {Boolean} data.loggedUser if user is logged in
   * @param {Boolean} data.failIfError true if it should fail the run if it is a response error.
   * @returns {HotelAvailabilities} HotelAvailabilities object that keeps the data from the response
   */
  static async graphqlGetHotelAvailabilitiesOnePage(...args: [inputData: HotelAvailabilitiesParams]): Promise<HotelAvailabilities> {
    const data = ApiCalls.asObject(args[0]);
    const input = ApiCalls.asObject(data.hotelAvailabilitiesInput);
    const queryFile = String((global.browser?.options as Record<string, unknown> | undefined)?.app ?? '').toLowerCase() === 'pi'
      ? 'hotelAvailabilitiesV2.graphql'
      : 'hotelAvailabilities.graphql';

    const variables = {
      startDate: input.startDate,
      endDate: input.endDate,
      location: ApiCalls.asObject(input.place).location,
      place: input.place,
      rooms: input.rooms,
      country: input.country,
      language: input.language,
      oldWorldChannel: input.oldWorldChannel,
      channel: input.channel,
      subChannel: input.subChannel,
      page: input.page,
      initialPageSize: input.initialPageSize,
      lazyLoadPageSize: input.lazyLoadPageSize,
      sort: input.sort,
      sortOption: input.sortOption,
      filters: input.filters,
      ratePlanCodes: input.ratePlanCodes,
    };

    const failIfError = data.failIfError === undefined ? true : Boolean(data.failIfError);
    const response = await ApiCalls.makeGraphqlCall(queryFile, variables, failIfError);
    const onePage = ApiCalls.getNested(response.body.data, 'hotelAvailabilitiesV2') ?? ApiCalls.getNested(response.body.data, 'hotelAvailabilities');
    if (!onePage) {
      throw new Error('GraphQL query returned null hotelAvailabilities. Check backend response for errors.');
    }
    return new HotelAvailabilities({ hotelAvailabilitiesApiResponse: onePage as {
      multiHotelAvailabilities?: Array<Record<string, unknown>>;
      page?: number;
      pageSize?: number;
      total?: number;
    } });
  }

  /**
   * Get all pages of available hotels for a specific location
   * @param {Object} data object data
   * @param {HotelAvailabilitiesInput} data.hotelAvailabilitiesInput used to set the GraphQL variables for the hotel availabilities experience call.
   * @param {Boolean} data.loggedUser - true if user is logged in
   * @param {Boolean} data.failIfError true if it should fail the run if it is a response error.
   * @returns {Array.<HotelAvailabilities>} hotelAvailabilitiesAllPages list that keeps details for all available hotels
   */
  static async getHotelAvailabilitiesAllPages(...args: [inputData: HotelAvailabilitiesParams]): Promise<HotelAvailabilityCard[]> {
    const data = ApiCalls.asObject(args[0]);
    const input = data.hotelAvailabilitiesInput as HotelAvailabilitiesInput;
    const failIfError = data.failIfError === undefined ? true : Boolean(data.failIfError);
    const loggedUser = Boolean(data.loggedUser ?? false);
    const collected: HotelAvailabilityCard[] = [];

    const firstPage = await ApiCalls.graphqlGetHotelAvailabilitiesOnePage({
      hotelAvailabilitiesInput: input,
      failIfError,
      loggedUser,
    });

    collected.push(...firstPage.multiHotelAvailabilities);

    const total = Number(firstPage.total ?? 0);
    const firstPageSize = Number(input.initialPageSize ?? Constants.SEARCH_RESULTS_FIRST_PAGE_SIZE);
    const followingPageSize = Number(input.lazyLoadPageSize ?? Constants.SEARCH_RESULTS_FOLLOWING_PAGES_SIZE);
    const remaining = Math.max(total - firstPageSize, 0);
    const extraPages = followingPageSize > 0 ? Math.ceil(remaining / followingPageSize) : 0;

    for (let page = 2; page <= 1 + extraPages; page += 1) {
      input.page = page;
      const nextPage = await ApiCalls.graphqlGetHotelAvailabilitiesOnePage({
        hotelAvailabilitiesInput: input,
        failIfError,
        loggedUser,
      });
      collected.push(...nextPage.multiHotelAvailabilities);
    }

    return collected;
  }

  /**
   * GraphQL query for GetAccountCompanyDetails
   * @param {Object} data object data
   * @param {String} data.id companyId
   * @returns {AccountCompanyDetails} AccountCompanyDetails object
   */
  static async graphqlGetAccountCompanyDetails(...args: [inputData?: { id?: string }]): Promise<AccountCompanyDetails> {
    const data = ApiCalls.asObject(args[0]);
    const response = await ApiCalls.makeGraphqlCall('getAccountCompanyDetails.graphql', { id: data.id });
    const accountCompany = ApiCalls.getNested<Record<string, unknown>>(response.body.data, 'accountCompany')
      ?? ApiCalls.getNested<Record<string, unknown>>(response.body.data, 'accountCompanyDetails')
      ?? {};
    return new AccountCompanyDetails({ accountCompany });
  }

  /**
   * GraphQL query for GetAddressesSuggestionsByPostcode
   * @param {Object} data object data
   * @param {String} data.postcode postcode
   * @returns {AddressesSuggestionsByPostcode} AddressesSuggestionsByPostcode object
   */
  static async graphqlGetAddressesSuggestionsByPostcode(...args: [inputData?: { postcode?: string }]): Promise<AddressesSuggestionsByPostcode> {
    const data = ApiCalls.asObject(args[0]);
    const response = await ApiCalls.makeGraphqlCall('getAddressesSuggestionsByPostcode.graphql', { searchTerm: data.postcode });
    return new AddressesSuggestionsByPostcode({ addressesSuggestionsApiResponse: response.body.data as { partialAddress?: Array<Record<string, unknown>> } });
  }

  /**
   * GraphQL query for GetMealsPackages
   * @param {Object} inputData used to set the GraphQL variables for the get hotel packages experience call.
   * @param {String} inputData.hotelId hotelId
   * @param {Number} inputData.nightsNumber nightsNumber
   * @param {Date} inputData.startDate startDate
   * @param {Date} inputData.endDate endDate
   * @param {Number} inputData.adultsNumber adultsNumber
   * @param {Number} inputData.childrenNumber childrenNumber
   * @param {String} inputData.bookingFlowId bookingFlowId
   * @param {String} inputData.basketReferenceId basketReferenceId
   * @param {String} inputData.country country
   * @param {String} inputData.language language
   * @returns {MealPackages}  returns an object with detailed meal items
   */
  static async graphqlGetMealsPackages(...args: [inputData?: AncillariesInputData]): Promise<MealPackages> {
    const variables = { ...ApiCalls.normalizeAncillariesInput(args[0]), isManageBookingPage: args[0]?.isManageBookingPage ?? false };
    const response = await ApiCalls.makeGraphqlCall('getAncillariesPackages.graphql', variables);
    const packagesResponse = ApiCalls.getNested<Record<string, unknown>>(response.body.data, 'packages') ?? {};
    const packages = ApiCalls.getNested<Record<string, unknown>>(response.body.data, 'packages.packages') ?? {};
    const adultMeals = Array.isArray(packages.meals) ? packages.meals as Array<Record<string, unknown>> : [];
    const childMeals = Array.isArray(packages.mealsKids) ? packages.mealsKids as Array<Record<string, unknown>> : [];
    const roomSelection = Array.isArray(packages.roomSelection) ? packages.roomSelection as Array<Record<string, unknown>> : [];
    return new MealPackages({
      adultMeals: adultMeals.map((adultMeal) => new AncillariesAdultMeal({ adultMeal })),
      childMeals: childMeals.map((kidsMeal) => new AncillariesChildMeal({ kidsMeal })),
      roomSelection: roomSelection.map((selection) => new RoomSelection({ roomSelection: selection })),
      hotelHasCityTaxForBusiness: packagesResponse.hotelHasCityTaxForBusiness as boolean | undefined,
      hotelHasCityTaxForLeisure: packagesResponse.hotelHasCityTaxForLeisure as boolean | undefined,
    });
  }

  /**
   * GraphQL query for GetHotelCityTax
   * @param {Object} inputData used to set the GraphQL variables for the GetPaymentPackages call.
   * @param {String} inputData.hotelId hotelId
   * @param {Number} inputData.nightsNumber nightsNumber
   * @param {Date} inputData.startDate startDate
   * @param {Date} inputData.endDate endDate
   * @param {Number} inputData.adultsNumber adultsNumber
   * @param {Number} inputData.childrenNumber childrenNumber
   * @param {String} inputData.bookingFlowId bookingFlowId
   * @param {String} inputData.basketReferenceId basketReferenceId
   * @param {String} inputData.country country
   * @param {String} inputData.language language
   * @returns {HotelCityTax} hotel city-tax configuration
   */
  static async graphQlGetHotelCityTax(...args: [inputData?: AncillariesInputData]): Promise<HotelCityTax> {
    const variables = ApiCalls.normalizeAncillariesInput(args[0]);
    const response = await ApiCalls.makeGraphqlCall('getPaymentPackages.graphql', variables);
    const paymentPackages = ApiCalls.getNested<Record<string, unknown>>(response.body.data, 'paymentPackages')
      ?? ApiCalls.getNested<Record<string, unknown>>(response.body.data, 'packages')
      ?? {};
    return new HotelCityTax({ paymentPackagesApiResponse: paymentPackages });
  }

  /**
   * GraphQL query for GetNewsletterSignup
   * @param {Object} inputData used to set the GraphQL variables for the get newsletter signup experience call.
   * @param {String} inputData.language language
   * @param {String} inputData.country country
   * @param {String} inputData.site site
   * @returns {NewsletterSignup}  returns an object with detailed newsletter signup items
   */
  static async graphqlGetNewsletterSignup(...args: [inputData?: { language?: string; country?: string; site?: string }]): Promise<NewsletterSignup> {
    const data = ApiCalls.asObject(args[0]);
    const locale = ApiCalls.getLocaleData();
    const variables = {
      language: String(data.language ?? locale.language),
      country: String(data.country ?? locale.country),
      site: String(data.site ?? 'leisure'),
    };
    const response = await ApiCalls.makeGraphqlCall('getNewsletterSignup.graphql', variables);
    const newsletterSignupApiResponse = ApiCalls.getNested<Record<string, unknown>>(response.body.data, 'footer.newsletterSignup')
      ?? ApiCalls.getNested<Record<string, unknown>>(response.body.data, 'newsletterSignup')
      ?? {};
    return new NewsletterSignup({ newsletterSignupApiResponse });
  }

  /**
   * Find booking call
   * @param {Object} inputData used to set the GraphQL variables
   * @param {String} inputData.basketReference basket reference
   * @param {String} inputData.lastName the booker last name
   * @param {String} inputData.arrivalDate the arrival date in the format: "YYYY-MM-DD"
   * @param {String} inputData.country the country code
   * @param {String} inputData.language the language code
   * @returns {FindBooking} returns the response
   */
  static async graphqlFindBooking(...args: [inputData: { basketReference?: string; lastName?: string; arrivalDate?: string; country?: string; language?: string }]): Promise<FindBooking> {
    const data = ApiCalls.asObject(args[0]);
    const locale = ApiCalls.getLocaleData();
    const variables = {
      resNo: data.basketReference,
      lastName: data.lastName,
      arrivalDate: data.arrivalDate,
      country: data.country ?? locale.country,
      language: data.language ?? locale.language,
    };
    const response = await ApiCalls.makeGraphqlCall('findBooking.graphql', variables);
    return new FindBooking({ findBooking: ApiCalls.getNested<Record<string, unknown>>(response.body.data, 'findBooking') ?? {} });
  }

  /**
   * Manage booking call
   * @param {Object} inputData used to set the GraphQL variables for the get newsletter signup experience call.
   * @param {String} inputData.basketReference basket reference
   * @param {String} inputData.hotelId the hotel id
   * @param {Date} inputData.userDateTime the user timestamp
   * @param {String} inputData.token the token used to authorize the request
   * @param {BookingChannel} inputData.bookingChannel the booking channel data
   * @returns {ManageBooking} returns the response
   */
  static async graphqlManageBooking(...args: [inputData: { basketReference?: string; hotelId?: string; userDateTime?: string; token?: string; bookingChannel?: BookingChannel }]): Promise<ManageBooking> {
    const data = ApiCalls.asObject(args[0]);
    const variables = {
      basketReference: data.basketReference,
      hotelId: data.hotelId,
      userDateTime: data.userDateTime ?? new Date().toISOString(),
      token: encodeURIComponent(String(data.token ?? '')),
      bookingChannel: data.bookingChannel ?? {},
    };
    const response = await ApiCalls.makeGraphqlCall('manageBooking.graphql', variables);
    return new ManageBooking({ manageBooking: ApiCalls.getNested<Record<string, unknown>>(response.body.data, 'manageBooking') ?? {} });
  }

  /**
   * Create Payment Mutation
   * @param inputData - Reservation, hotel, payment, and optional business-payment inputs.
   * @returns Payment mutation response.
   */
  static async graphqlCreatePaymentMutation(...args: [inputData?: Record<string, unknown>]): Promise<PaymentApiResponse> {
    const data = args.length === 1 ? ApiCalls.asObject(args[0]) : {};
    const reservationInfo = ApiCalls.asObject(data.reservationInfo);
    const reservationDetails = ApiCalls.asObject(reservationInfo.reservationDetails);
    const hotel = ApiCalls.asObject(data.hotel);
    const guestDetails = ApiCalls.asObject(reservationInfo.guestDetails);
    const booker = ApiCalls.asObject(guestDetails.booker);
    const address = ApiCalls.asObject(booker.address);
    const card = ApiCalls.asObject(data.card);
    const reservations = Array.isArray(reservationDetails.reservations)
      ? reservationDetails.reservations.map(ApiCalls.asObject)
      : [];
    const firstRoomStay = ApiCalls.asObject(reservations[0]?.roomStay);
    const hotelId = String(hotel.id ?? '');
    const locale = ApiCalls.getLocaleData();
    const options = getBrowserOptions();
    const channel = String(data.channel ?? options.app ?? 'pi').toUpperCase();
    const businessItems = ApiCalls.asObject(data.businessItems);
    const isPibaCardPresent = Boolean(data.isPibaCardPresent ?? true);
    const paymentOption = String(
      data.paymentOption ?? Constants.PAYMENT_OPTION.reserveWithoutCreditCard
    );
    const rooms = reservations.map((reservation) => {
      const roomStay = ApiCalls.asObject(reservation.roomStay);
      return {
        adultsNumber: roomStay.adultsNumber,
        rate: roomStay.ratePlanCode,
        type: roomStay.pmsRoomType ?? roomStay.roomType,
      };
    });
    const payment: Record<string, unknown> = {
      billing: {
        email: booker.emailAddress,
        firstName: booker.firstName,
        lastName: booker.lastName,
        title: booker.title ?? (locale.language === 'de' ? 'Herr' : 'Mr'),
        telephone: booker.mobile,
        address: {
          addressLine1: address.addressLine1 ?? '',
          addressLine2: address.addressLine2 ?? '',
          addressLine3: address.addressLine3 ?? '',
          addressLine4: address.addressLine4 ?? '',
          country: address.countryCode,
          postalCode: address.postalCode,
        },
      },
      environment: String(
        options.aemBaseUrl
        ?? getEnvironmentConfig(options as unknown as GetEnvironmentConfigOptions).aemBaseUrl
      ),
      card: data.card ? {
        cardholderName: card.cardholderName ?? card.name ?? '',
        cardType: card.cardType ?? card.cardSchemeId ?? '',
        expiryMonth: card.expiryMonth ?? '',
        expiryYear: card.expiryYear ?? '',
        token: card.token ?? '',
        cnpRequired: card.cnpRequired,
        logoUrl: card.logoUrl,
        type: card.type,
        last4Digits: card.last4Digits,
      } : undefined,
      subType: String(data.subType ?? 'ECOMM'),
      type: data.isPiba ? 'PIBA' : 'CARD',
      pibaCardPresent: isPibaCardPresent,
    };
    const createPaymentCriteria: Record<string, unknown> = {
      booking: {
        businessSite: {
          identifier: hotelId,
          name: hotel.name,
          type: 'HOTEL',
          location: hotelId,
        },
        channel: String(options.app).toLowerCase() === 'pib' ? 'BB' : channel,
        journey: 'BOOKING',
        language: locale.language,
        rooms,
        arrivalDate: firstRoomStay.arrivalDate,
        departureDate: firstRoomStay.departureDate,
        type: paymentOption,
      },
      payment,
      charityPackageCode: String(data.charityPackageCode ?? ''),
      hotelId,
      requestId: `${Date.now()}-${Math.random().toString(36).slice(2, 10)}`,
    };

    if (data.businessItems) {
      payment.businessItems = businessItems;
      const allowances = Array.isArray(businessItems.businessAllowances)
        ? businessItems.businessAllowances.map(ApiCalls.asObject)
        : [];
      const allowance = (name: string) => allowances.find(
        (item) => item.allowance === name
      );
      const dinnerAllowance = allowance('dinner');
      const alcoholAllowance = allowance('alcohol');
      const carParkingAllowance = allowance('carParking');
      const wifiAllowance = allowance('ultimateWifi');
      createPaymentCriteria.businessAccount = {
        purchaseOrder: businessItems.purchaseOrderNumber ?? '',
        customerReference: businessItems.customReferenceNumber ?? '',
        cardNotPresentAuth: isPibaCardPresent ? 'No' : 'Yes',
        dinnerAllowance: dinnerAllowance?.isAuthorised ? dinnerAllowance.budget : null,
        alcoholAllowed: alcoholAllowance?.isAuthorised ? 'Yes' : 'No',
        carParkingAllowed: carParkingAllowance?.isAuthorised ? 'Yes' : 'No',
        wifiAllowed: wifiAllowance?.isAuthorised ? 'Yes' : 'No',
      };
    }

    const variables = {
      basketReference: String(reservationDetails.basketReference ?? ''),
      createPaymentCriteria,
    };
    const response = await ApiCalls.makeGraphqlCall('createPaymentMutation.graphql', variables);
    return {
      ...response,
      body: { data: ApiCalls.getNested<Record<string, unknown>>(response.body.data, 'createPaymentMutation') ?? ApiCalls.asObject(response.body.data) },
      text: JSON.stringify({ data: ApiCalls.getNested<Record<string, unknown>>(response.body.data, 'createPaymentMutation') ?? ApiCalls.asObject(response.body.data) }),
    };
  }

  /**
   * Create Payment Process
   * @param {Object} data - Object of a reservationInfo 
   * @param {ReservationInfo} data.reservationInfo reservationInfo response from APIcalls.createReservationViaAPi
   * @param {Hotel} data.hotel - new Hotel instance
   * @param {String} data.paymentOption payment option
   * @param {Boolean} data.isPiba if set to true, Piba payment flow will be initialized
   * @param {BusinessItemsInput} data.businessItems business items
   * @param {String} data.subType subType
  * @returns {GraphQLResponse<Object>} payment process response
   */
  static async graphqlCreatePaymentProcess(...args: [inputData?: Record<string, unknown>]): Promise<PaymentApiResponse> {
    const variables = args.length === 1 && args[0] && typeof args[0] === 'object' && !Array.isArray(args[0]) ? args[0] as Record<string, unknown> : {};
    const response = await ApiCalls.makeGraphqlCall('createPaymentProcess.graphql', variables);
    return {
      ...response,
      body: { data: ApiCalls.getNested<Record<string, unknown>>(response.body.data, 'createPaymentProcess') ?? ApiCalls.asObject(response.body.data) },
      text: JSON.stringify({ data: ApiCalls.getNested<Record<string, unknown>>(response.body.data, 'createPaymentProcess') ?? ApiCalls.asObject(response.body.data) }),
    };
  }

  /**
   * Get search bookings results
   * @param {Object} searchResultsFilters used to set the GraphQL variables
   * @param {String} searchResultsFilters.arrivalDate the arrival date in the format "23 Feb 2023"
   * @param {String} searchResultsFilters.bookerEmail booker email
   * @param {String} searchResultsFilters.bookerLastName booker last name
   * @param {String} searchResultsFilters.bookerPhone booker phone
   * @param {String} searchResultsFilters.hotelId hotel ID
   * @param {String} searchResultsFilters.bookerPostcode booker postcode
   * @param {String} searchResultsFilters.bookingReference booking reference
   * @param {String} searchResultsFilters.cancellationDate cancellation date
   * @param {String} searchResultsFilters.companyName company name
   * @param {String} searchResultsFilters.guestLastName guest last name
   * @param {Number} searchResultsFilters.limit number of bookings per page (10 by default)
   * @param {Number} searchResultsFilters.offset number of displayed bookings
   * @param {String} searchResultsFilters.thirdPartyBookingReferenceNumber third party booking reference number
   * @returns {SearchBookings} returns the response
   */
  static async graphqlGetSearchBookingsResults(...args: [searchResultsFilters?: Record<string, unknown>]): Promise<SearchBookings> {
    const data = ApiCalls.asObject(args[0]);
    const asString = (value: unknown) => String(value ?? '');
    const asNumber = (value: unknown) => Number(value ?? 0);
    const variables = {
      bookingReference: asString(data.bookingReference),
      bookerLastName: asString(data.bookerLastName),
      guestLastName: asString(data.guestLastName),
      bookerPostcode: asString(data.bookerPostcode),
      bookerEmail: asString(data.bookerEmail),
      bookerPhone: asString(data.bookerPhone),
      hotelId: asString(data.hotelId),
      arrivalDate: asString(data.arrivalDate),
      cancellationDate: asString(data.cancellationDate),
      companyName: asString(data.companyName),
      thirdPartyBookingReferenceNumber: asString(data.thirdPartyBookingReferenceNumber),
      limit: asNumber(data.limit),
      offset: asNumber(data.offset),
    };
    const response = await ApiCalls.makeGraphqlCall('getSearchBookingsResults.graphql', variables);
    return new SearchBookings({ searchBookingsResults: ApiCalls.getNested<Record<string, unknown>>(response.body.data, 'searchBookings') ?? {} });
  }

  /**
   * Get cancellation reasons
   * @param {String} hotelId hotelId
   * @returns {Array.<CancellationReason>} cancellation reasons for the hotel
   */
  static async getCancellationReasons(...args: [hotelId: string] | [inputData: { hotelId?: string }]): Promise<CancellationReason[]> {
    const hotelId = typeof args[0] === 'string' ? args[0] : String(ApiCalls.asObject(args[0]).hotelId ?? '');
    const response = await ApiCalls.makeGraphqlCall('getCancellationReasons.graphql', { hotelId });
    return ApiCalls.getNested<CancellationReason[]>(response.body.data, 'cancellationReasons.cancellationReasons') ?? [];
  }

  /**
   * GraphQL query to get account upcoming spending
   * @param {Object} data object data
   * @param {String} data.accountId account ID
   * @returns {AccountUpcomingSpending} AccountUpcomingSpending object
   */
  static async graphqlGetAccountUpcomingSpending(...args: [inputData?: { accountId?: string }]): Promise<AccountUpcomingSpending> {
    const data = ApiCalls.asObject(args[0]);
    const response = await ApiCalls.makeGraphqlCall('getAccountUpcomingSpending.graphql', { accountId: data.accountId });
    const accountUpcomingSpending = ApiCalls.getNested<Record<string, unknown>>(response.body.data, 'accountUpcomingSpending')
      ?? ApiCalls.getNested<Record<string, unknown>>(response.body.data, 'getAccountUpcomingSpending')
      ?? {};
    return new AccountUpcomingSpending({ accountUpcomingSpending });
  }

  /**
   * Returns upcoming booking
   * @param {Object} inputData used to set the GraphQL variables for the get upcoming booking experience call.
   * @param {String} inputData.language language
   * @param {String} inputData.country country
   * @param {String} inputData.channel channel
   * @param {String} inputData.subchannel channel
  * @returns {UpcomingBooking} upcoming booking details
   */
  static async graphqlGetUpcomingBooking(...args: [inputData?: { language?: string; country?: string; channel?: string; subchannel?: string }]): Promise<UpcomingBooking> {
    const data = ApiCalls.asObject(args[0]);
    const locale = ApiCalls.getLocaleData();
    const variables = {
      language: data.language ?? locale.language,
      country: data.country ?? locale.country,
      channel: data.channel ?? 'BB',
      subchannel: data.subchannel ?? 'WEB',
    };
    const response = await ApiCalls.makeGraphqlCall('getUpcomingBookings.graphql', variables);
    const upcomingBooking = ApiCalls.getNested<Record<string, unknown>>(response.body.data, 'upcomingBookings')
      ?? ApiCalls.getNested<Record<string, unknown>>(response.body.data, 'getUpcomingBookings')
      ?? {};
    return new UpcomingBooking({ upcomingBooking });
  }

  /**
   * Returns bookings totals
   * @param {Object} data used to set the GraphQL variables for the get bookings history totals
   * @param {String} data.bookingChannel booking channel
   * @param {String} data.business business
   * @param {String} data.filterType filter type
   * @param {String} data.filterValue filter value
   * @param {String} data.pageIndex page index
   * @param {String} data.pageSize page size
   * @param {String} data.sortOrder sorted order
   * @returns {BookingHistoryTotals}  returns an object with booking history totals
   */
  static async graphqlGetBookingTotals(...args: [inputData?: Record<string, unknown>]): Promise<BookingHistoryTotals> {
    const data = ApiCalls.asObject(args[0]);
    const variables = {
      bookingChannel: data.bookingChannel ?? {},
      business: data.business ?? true,
      filterType: data.filterType ?? '',
      filterValue: data.filterValue ?? '',
      pageIndex: data.pageIndex ?? 1,
      pageSize: data.pageSize ?? 40,
      sortOrder: data.sortOrder ?? 'DEFAULT',
    };
    const response = await ApiCalls.makeGraphqlCall('getBookingHistoryTotals.graphql', variables);
    return new BookingHistoryTotals({ bookingTotals: ApiCalls.getNested<Record<string, unknown>>(response.body.data, 'bookingHistory.totals') ?? {} });
  }

  /**
   * GraphQL query for graphqlGetHotelTripAdvisorReview
   * @param {Object} queryParams object data
   * @param {String} queryParams.country country
   * @param {String} queryParams.language language
   * @param {Array} queryParams.hotelIds hotelId
   * @param {String} queryParams.longitudeRef longitude
   * @param {String} queryParams.latitudeRef latitude
   * @param {number} count number of reviews to return
   * @returns {Array<HotelTripAdvisorReviews>} Array of hotels and their tripadvisor reviews
   */
  static async graphqlGetHotelTripAdvisorReview(...args: [queryParams?: Record<string, unknown>, count?: number]): Promise<HotelTripAdvisorReviews[]> {
    const queryParams = {
      ...withLocaleDefaults(ApiCalls.asObject(args[0])),
      tripAdvisorDataRequired: true,
    };
    const count = typeof args[1] === 'number' ? args[1] : 12;
    const response = await ApiCalls.makeGraphqlCall('getHotelsTripAdvisorReviews.graphql', queryParams);
    const hotelsInformation = ApiCalls.getNested<Array<Record<string, unknown>>>(response.body.data, 'getHotelsInformation') ?? [];
    return hotelsInformation
      .slice(0, count)
      .map((information) => new HotelTripAdvisorReviews({
        tripAdvisorReviews: ApiCalls.asObject(information.tripAdvisorReviews),
        links: ApiCalls.asObject(information.links),
      }))
      .filter((review) => review.rating !== undefined && review.numberOfReviews !== undefined);
  }

  /**
   * GraphQL query for graphqlGetHotelFacilities
   * @param {Object} queryParams object data
   * @param {String} queryParams.country country
   * @param {String} queryParams.language language
   * @param {Array} queryParams.hotelIds hotelId
   * @param {String} queryParams.longitudeRef longitude
   * @param {String} queryParams.latitudeRef latitude
  * @returns {Array.<Array.<String>>} facility codes grouped by hotel
   */
  static async graphqlGetHotelFacilities(...args: [queryParams?: Record<string, unknown>]): Promise<string[][]> {
    const queryParams = withLocaleDefaults(ApiCalls.asObject(args[0]));
    const response = await ApiCalls.makeGraphqlCall('getHotelsDLPFacilities.graphql', queryParams);
    const hotelsInformation = ApiCalls.getNested<Array<Record<string, unknown>>>(response.body.data, 'getHotelsInformation') ?? [];
    return hotelsInformation
      .filter((information) => Array.isArray(information.hotelFacilities))
      .map((information) => {
        const facilities = information.hotelFacilities as Array<Record<string, unknown>>;
        return facilities.map((facility) => String(facility.code ?? ''));
      });
  }

  /**
   * GraphQL query for graphqlGetHotelsInformationForDLPMapView
   * @param {Object} queryParams object data
   * @param {String} queryParams.country country
   * @param {String} queryParams.language language
   * @param {Array} queryParams.hotelIds hotelId
   * @param {String} queryParams.longitudeRef longitude
   * @param {String} queryParams.latitudeRef latitude
   * @returns {Array<HotelMapViewDLP>} Array of hotels in dlp map
   */
  static async graphqlGetHotelsInformationForDLPMapView(...args: [queryParams?: Record<string, unknown>]): Promise<HotelMapViewDLP[]> {
    const queryParams = withLocaleDefaults(ApiCalls.asObject(args[0]));
    const response = await ApiCalls.makeGraphqlCall('getHotelsForDLPMapView.graphql', queryParams);
    const hotelsInformation = ApiCalls.getNested<Array<Record<string, unknown>>>(response.body.data, 'getHotelsInformation') ?? [];
    return hotelsInformation.map((hotel) => new HotelMapViewDLP({
      name: hotel.name as string | undefined,
      brand: hotel.brand as string | undefined,
      coordinates: ApiCalls.asObject(hotel.coordinates),
      distanceFromReference: hotel.distanceFromReference as number | undefined,
      hotelFacilities: hotel.hotelFacilities as unknown[] | undefined,
      tripAdvisorReviews: ApiCalls.asObject(hotel.tripAdvisorReviews),
      links: ApiCalls.asObject(hotel.links),
      topSectionImages: Array.isArray(hotel.topSectionImages) ? hotel.topSectionImages as Array<{ thumbnailSrc?: string }> : [],
    }));
  }

  /**
   * Returns anonymous newsletter preferences
   * @param {Object} inputData used to set the GraphQL variables for the anonymous newsletter preferences call.
   * @param {String} inputData.brandCode
   * @param {String} inputData.email
   * @param {String} inputData.countryOfResidence
   * @param {String} inputData.language
   * @returns {AnonymousNewsletterPreferences} returns an object with newsletter preferences
   */
  static async graphqlGetAnonymousNewsletterPreferences(...args: [inputData?: Record<string, unknown>]): Promise<AnonymousNewsletterPreferences> {
    const variables = args.length === 1 && args[0] && typeof args[0] === 'object' && !Array.isArray(args[0]) ? args[0] as Record<string, unknown> : {};
    const response = await ApiCalls.makeGraphqlCall('getAnonymousNewsletterPreferences.graphql', variables);
    return new AnonymousNewsletterPreferences({
      anonymousNewsletterPreferences: ApiCalls.getNested<Record<string, unknown>>(response.body.data, 'anonymousNewsletterPreferences') ?? {},
    });
  }

  /**
   * Cancel IB piba card
   * @param {Object} data object data
   * @param {String} data.tetheredUserId tethered guid
   * @param {String} data.cardId piba card id
   * @param {String} data.scheme scheme
   * @returns {CancelledCardDetails} cancelled card details object
   */
  static async cancelIbPibaCard(...args: [inputData?: { tetheredUserId?: string; cardId?: number; scheme?: string }]): Promise<CancelledCardDetails> {
    const data = ApiCalls.asObject(args[0]);
    const variables = {
      tetheredUserId: data.tetheredUserId,
      cardId: data.cardId,
      cancelAndReplaceInnBCardRequest: {
        issueReplacement: false,
        scheme: data.scheme,
      },
    };
    const response = await ApiCalls.makeGraphqlCall('cancelAndReplaceIbPibaCard.graphql', variables);
    return new CancelledCardDetails({
      cancelledCardDetails: ApiCalls.getNested<Record<string, unknown>>(response.body.data, 'cancelAndReplaceInnBPIBACard.cancelledCardDetails') ?? {},
    });
  }

}
