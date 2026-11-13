import { ApiCalls } from './apiCalls';
import { withLocaleDefaults, getCurrentLocale } from '../../test-data/locales';
import { Hotels } from '../../test-data/hotels';
import { HotelRates } from '../../test-data/hotelRates';
import { HotelBrands, type HotelBrand } from '../../test-data/hotelBrands';
import type { PmsRoomType } from '../../test-data/pmsRoomTypes';
import {
  BookingConfirmation,
  BookingInformation,
  Companies,
  Accounts,
  AccountSpendingResponse,
  AnsweredQuestions,
  CompanyPaymentCard,
  CompanyDetails,
  Company,
  Customer,
  CustomerAccountCurrentBalancesResponse,
  CustomerInvoices,
  CurrentBalanceItem,
  DlpInformation,
  FooterInformation,
  HeaderInformation,
  HotelInformation,
  HotelImportantInformation,
  HotelRoomTypeInformation,
  RatesInformation,
  RoomClassConfig,
  RoomStay,
  TermsAndConditions,
  TotalCost,
  BookingInformationUpgradeToFlex,
  Employees,
  EmployeeDetails,
  EmployeeSpendResponse,
  CompanyBookingAlerts,
  CompanyBookingAllowances,
  CompanySpendingResponse,
  ApplicationDetails,
  PibaCardDetails,
  PibaCards,
  WorldlineUserPreferences,
  AccountTransactions,
  CostCentreDetails,
  RoomRequirements,
} from '../response';
import { AccountSpendingCriteria, BookingChannel, CustomerRequest, EmployeeCriteria, PibaCardsCriteria } from '../requests';

/** Interface for footer information parameters */
interface FooterInfoParams {
  language?: string;
  country?: string;
  site?: string;
}

/** Interface for hotel information parameters */
interface HotelInfoParams {
  hotelId: string;
  language?: string;
  country?: string;
}

/** Interface for hotel distance parameters */
interface HotelDistanceParams {
  hotelId: string;
  location?: string;
  locationFormat?: string;
  radius?: string;
  radiusUnit?: string;
}

/** Interface for rates information parameters */
interface RatesInfoParams {
  language?: string;
  country?: string;
  brand?: string;
  hotelId?: string;
}

/** Interface for header information parameters */
interface HeaderInfoParams {
  language?: string;
  country?: string;
}

/** Interface for payment method parameters */
interface PaymentMethodParams {
  basketReference: string;
  language?: string;
  country?: string;
}

/** Interface for total cost parameters */
interface TotalCostParams {
  basketReference: string;
  bookingChannel?: BookingChannel;
  language?: string;
  country?: string;
}

/** Interface for room stay list parameters */
interface RoomStayListParams {
  basketReference: string;
  bookingChannel?: BookingChannel;
  language?: string;
  country?: string;
}

/** Interface for info messages parameters */
interface InfoMessagesParams {
  basketReference: string;
  bookingChannel?: BookingChannel;
  language?: string;
  country?: string;
}

/** Interface for upgrade to flex parameters */
interface UpgradeToFlexParams {
  basketReference: string;
  bookingChannel?: BookingChannel;
  language?: string;
  country?: string;
}

/** Interface for booking information parameters */
interface BookingInfoParams {
  basketReference: string;
  bookingChannel?: BookingChannel;
  language?: string;
  country?: string;
}

/** Interface for booking confirmation parameters */
interface BookingConfirmationParams {
  basketReference: string;
  language?: string;
  country?: string;
  bookingChannel?: string;
}

/** Interface for terms and conditions parameters */
interface TermsAndConditionsParams {
  hotelId?: string;
  language?: string;
  country?: string;
  rateCode?: string;
  bookingChannel?: string;
}

/** Interface for room type information parameters */
interface RoomTypeInfoParams {
  brand?: string;
  language?: string;
  country?: string;
}

/** Interface for manage booking room type information parameters */
interface ManageBookingRoomTypeInfoParams {
  pmsRoomType?: PmsRoomType;
  hotelType?: HotelBrand;
  language?: string;
  country?: string;
}

/** Interface for countries parameters */
interface CountriesParams {
  country?: string;
  language?: string;
  site?: string;
}

/** Interface for search companies parameters */
interface SearchCompaniesParams {
  hotelId?: string;
  companyName?: string;
  limit?: number;
}

/** Interface for search companies by negotiated rates parameters */
interface SearchCompaniesByNegotiatedRatesParams {
  limit?: number;
  offset?: number;
  searchTerm?: string;
  negotiatedRateCompanies?: boolean;
}

/** Interface for room class config parameters */
interface RoomClassConfigParams {
  channel?: string;
  brand?: string;
  country?: string;
  language?: string;
}

/** Interface for employees parameters */
interface EmployeesParams {
  companyId?: string;
  searchCriteria?: string;
  bookingChannel?: string;
  awaitingApproval?: boolean;
  size?: number;
  shouldFilterEmployees?: boolean;
}

/** Interface for account list parameters */
interface AccountListParams {
  viewAll?: boolean;
}

/** Interface for company registration questions parameters */
interface CompanyRegistrationQuestionsParams {
  companyId?: string;
}

/** Interface for company payment cards parameters */
interface CompanyPaymentCardsParams {
  companyId?: string;
}

/** Interface for company details parameters */
interface CompanyDetailsParams {
  companyId?: string;
}

/** Interface for employee details parameters */
interface EmployeeDetailsParams {
  companyId?: string;
  employeeId?: string;
}

/** Interface for profile details room requirements parameters */
interface ProfileDetailsRoomRequirementsParams {
  customerId?: string;
  business?: boolean;
}

/** Interface for profile details parameters */
interface ProfileDetailsParams {
  customerId?: string;
  business?: boolean;
  loggedUser?: boolean;
  username?: string;
  password?: string;
}

/** Interface for update profile details parameters */
interface UpdateProfileDetailsParams {
  customerId?: string;
  business?: boolean;
  innBusiness?: boolean;
  payload?: CustomerRequest | Customer;
}

/** Interface for update company details parameters */
interface UpdateCompanyDetailsParams {
  companyId?: string;
  companySummary?: Record<string, unknown>;
}

/** Interface for DLP content service parameters */
interface DlpContentServiceParams {
  country?: string;
  language?: string;
  dlpPath?: string;
}

/** Interface for update employee parameters */
interface UpdateEmployeeParams {
  companyId?: string;
  employeeId?: string;
  languageCode?: string;
  updateEmployeeCriteria?: EmployeeCriteria;
  loggedUser?: boolean;
  username?: string;
  password?: string;
}

/** Interface for account current balances parameters */
interface AccountCurrentBalancesParams {
  viewAll?: boolean;
}

/** Interface for customer invoices parameters */
interface CustomerInvoicesParams {
  payload?: Record<string, unknown>;
}

/** Interface for payment info parameters */
interface PaymentInfoParams {
  accountId?: string;
}

/** Interface for customer statement value parameters */
interface CustomerStatementValueParams {
  scheme?: string;
  tetheredUserId?: string;
}

/** Interface for customer SIP account balance parameters */
interface CustomerSipAccountBalanceParams {
  scheme?: string;
  tetheredUserGuid?: string;
}

/** Interface for company spending parameters */
interface CompanySpendingParams {
  fromMonthYear?: string;
  toMonthYear?: string;
}

/** Interface for employee spend parameters */
interface EmployeeSpendParams {
  fromMonthYear?: string;
  toMonthYear?: string;
}

/** Interface for Worldline user preferences parameters */
interface WorldlineUserPreferencesParams {
  tetheredUserGuids?: string[];
}

/** Interface for booking alerts parameters */
interface BookingAlertsParams {
  companyId?: string;
}

/** Interface for update booking alerts parameters */
interface UpdateBookingAlertsParams {
  companyId?: string;
  bookingAlerts?: CompanyBookingAlerts;
}

/** Interface for booking allowances parameters */
interface BookingAllowancesParams {
  companyId?: string;
}

/** Interface for update booking allowances parameters */
interface UpdateBookingAllowancesParams {
  companyId?: string;
  bookingAllowances?: CompanyBookingAllowances;
}

/** Interface for account spending parameters */
interface AccountSpendingParams {
  accountSpendingCriteria?: AccountSpendingCriteria;
}

/** Interface for PIBA cards parameters */
interface PibaCardsParams {
  tetheredUserId?: string;
  countryCode?: string;
  pibaCardsCriteria?: PibaCardsCriteria;
}

/** Interface for delete custom question parameters */
interface DeleteCustomQuestionParams {
  companyId?: string;
  questionId?: string;
}

/** Interface for pay applications parameters */
interface PayApplicationsParams {
  // No specific parameters
}

/** Interface for application details parameters */
interface ApplicationDetailsParams {
  scheme?: string;
  applicationId?: string;
  applicationGuid?: string;
}

/** Interface for company details lookup parameters */
interface CompanyDetailsLookupParams {
  companyRegistrationNumber?: string;
  scheme?: string;
}

/** Interface for PIBA card details parameters */
interface PibaCardDetailsParams {
  tetheredUserId?: string;
  countryCode?: string;
  cardId?: string;
}

/** Interface for account transactions parameters */
interface AccountTransactionsParams {
  payload?: Record<string, unknown>;
}

/** Interface for cost centre details parameters */
interface CostCentreDetailsParams {
  tetheredUserGuid?: string;
}

/**
 * Methods for accessing Premier Inn API backend resources.
 * Experience layer - Content Query and Mutations
 */
export class ApiContentCalls {
  [key: string]: unknown;

  constructor(data: Record<string, unknown> = {}) {
    const values = Object.values(data);
    const payload = values.length === 1 && values[0] && typeof values[0] === 'object' && !Array.isArray(values[0])
      ? values[0] as Record<string, unknown>
      : data;

    Object.assign(this, payload);
  }

  static fromApiData<T extends Record<string, unknown>>(data: T): ApiContentCalls {
    return new ApiContentCalls(data);
  }

  /**
   * Returns footer information using experience call
   * @param inputData used to set the GraphQL variables for the get footer information experience call.
   * @param inputData.language language
   * @param inputData.country country
   * @param inputData.site hotel brand
  * @returns {FooterInformation} detailed footer information
   */
  static async graphqlGetFooterInformation(
    { language, country, site }: FooterInfoParams = {}
  ): Promise<FooterInformation> {
    const variables = { ...withLocaleDefaults({ country, language }), site: site ?? 'leisure' } as Record<string, unknown>;
    const footer = (await ApiCalls.makeGraphqlCall<{ footer?: Record<string, unknown> }>(
      'getFooterInformation.graphql',
      variables
    )).body.data.footer ?? {};
    return new FooterInformation({ footer });
  }

  /**
   * GraphQL query for GetHotelInformation
   * @param data used to set the GraphQL variables for the get hotel information experience call.
   * @param data.hotelId hotelId
   * @param data.language language
   * @param data.country country
  * @returns {HotelInformation} hotel information response
   */
  static async graphqlGetHotelInformation(
    { hotelId, language, country }: HotelInfoParams
  ): Promise<HotelInformation> {
    const locale = getCurrentLocale();
    const variables = { country: country ?? locale.country, hotelId, language: language ?? locale.language } as Record<string, unknown>;
    const hotelInformation = (await ApiCalls.makeGraphqlCall<{ hotelInformation?: Record<string, unknown> }>(
      'getHotelInformation.graphql',
      variables
    )).body.data.hotelInformation ?? {};
    return new HotelInformation({ hotelInformation });
  }

  /**
   * GraphQL query for HotelInformationImportantInformation
   * @param inputData used to set the GraphQL variables for the get hotel booking information experience call.
   * @param inputData.hotelId hotelId
   * @param inputData.language language
   * @param inputData.country country
  * @returns {HotelImportantInformation} hotel important-information response
   */
  static async getHotelInformationImportantInformation(
    { hotelId, language, country }: HotelInfoParams
  ): Promise<HotelImportantInformation> {
    const locale = getCurrentLocale();
    const variables = { hotelId, country: country ?? locale.country, language: language ?? locale.language } as Record<string, unknown>;
    const hotelInformation = (await ApiCalls.makeGraphqlCall<{ hotelInformation?: { importantInfo?: Record<string, unknown> } }>(
      'getHotelInformationImportantInformation.graphql',
      variables
    )).body.data.hotelInformation ?? {};
    return new HotelImportantInformation({ hotelInformation });
  }

  /**
   * GraphQL call for GetHotelDistance
   * @param inputData used to set the GraphQL variables for the get hotel distance experience call.
   * @param inputData.hotelId hotelId (the id of the hotel you want the distance from)
   * @param inputData.location location (the id of the location/city in which the hotel is)
   * @param inputData.locationFormat locationFormat
   * @param inputData.radius radius
   * @param inputData.radiusUnit radiusUnit
  * @returns {number} rounded distance from the search location
   */
  static async graphqlGetHotelDistance(
    { hotelId, location, locationFormat, radius, radiusUnit }: HotelDistanceParams
  ): Promise<number> {
    const distanceFromSearch = await this.graphqlGetHotelDistanceForSearchResults({
      hotelId,
      location: location ?? 'ChIJNVch-SMbdkgRqv1PBlyKQqU',
      locationFormat: locationFormat ?? 'placeId',
      radius: radius ?? '50',
      radiusUnit: radiusUnit ?? 'mi'
    });
    const roundedDistance = Math.round(Number.parseFloat(distanceFromSearch) * 10) / 10;
    console.log(` The distance between the hotel and the searched location is: '${roundedDistance}'`);
    return roundedDistance;
  }

  /**
   * GraphQL query for GetHotelDistanceForSearchResults
   * @param inputData used to set the GraphQL variables for the get hotel distance experience call.
   * @param inputData.hotelId the id of the hotel you want the distance from
   * @param inputData.location the id of the location/city in which the hotel is
   * @param inputData.locationFormat what you are giving in the location field placeId or name
   * @param inputData.radius the radius of the search
   * @param inputData.radiusUnit the unit in which the result should be returned
  * @returns {string} distance from the search location
   */
  static async graphqlGetHotelDistanceForSearchResults(
    { hotelId, location, locationFormat, radius, radiusUnit }: HotelDistanceParams
  ): Promise<string> {
    const variables = {
      hotelId,
      location: location ?? 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
      locationFormat: locationFormat ?? 'placeId',
      radius: radius ?? '50',
      radiusUnit: radiusUnit ?? 'mi'
    } as Record<string, unknown>;
    return (await ApiCalls.makeGraphqlCall(
      'getHotelDistance.graphql',
      variables
    ) as any).body.data.hotelDistanceFromSearch.distance;
  }

  /**
   * GraphQL query for GetRatesInformation
   * @param data object data
   * @param data.language language
   * @param data.country country
   * @param data.brand brand
   * @param data.hotelId hotelId
  * @returns {RatesInformation} rates information response
   */
  static async graphqlGetRatesInformation(
    { language, country, brand, hotelId }: RatesInfoParams = {}
  ): Promise<RatesInformation> {
    const variables = { ...withLocaleDefaults({ country, language }), brand: brand ?? 'pi', hotelId: hotelId ?? Hotels.DEFAULT_HOTEL.id } as Record<string, unknown>;
    const ratesInformation = (await ApiCalls.makeGraphqlCall(
      'ratesInformation.graphql',
      variables
    ) as any).body.data.ratesInformation;
    return new RatesInformation({ ratesInformationApiResponse: ratesInformation });
  }

  /**
   * GraphQL query for GetHeaderInformation
   * @param data object data
   * @param data.language language
   * @param data.country country
  * @returns {HeaderInformation} header information response
   */
  static async graphqlGetHeaderInformation(
    { language, country }: HeaderInfoParams = {}
  ): Promise<HeaderInformation> {
    const variables = withLocaleDefaults({ country, language }) as Record<string, unknown>;
    const headerInformation = (await ApiCalls.makeGraphqlCall(
      'getHeaderInformation.graphql',
      variables
    ) as any).body.data.headerInformation;
    return new HeaderInformation({ menu: headerInformation.content.menu });
  }

  /**
   * GraphQL query for GetPaymentMethod
   * @param inputData used to set the GraphQL variables for getPaymentMethods call.
   * @param inputData.basketReference BasketReference for which to get the data
   * @param inputData.language Language for which to get the data
   * @param inputData.country Country for which to get the data
  * @returns {Array.<Record<string, unknown>>} available payment methods
   */
  static async graphqlGetPaymentMethod(
    { basketReference, language, country }: PaymentMethodParams
  ): Promise<Array<Record<string, unknown>>> {
    const variables = { basketReference, ...withLocaleDefaults({ country, language }) } as Record<string, unknown>;
    return (await ApiCalls.makeGraphqlCall(
      'getPaymentMethods.graphql',
      variables
    ) as any).body.data.paymentMethods;
  }

  /**
   * GraphQL query for GetTotalCostFromBookingInformation
   * @param inputData used to set the GraphQL variables for the get hotel booking information experience call.
   * @param inputData.basketReference basketReference
   * @param inputData.bookingChannel bookingChannel
   * @param inputData.language language
   * @param inputData.country country
  * @returns {TotalCost} booking total cost
   */
  static async graphqlGetTotalCostFromBookingInformation(
    { basketReference, bookingChannel, language, country }: TotalCostParams
  ): Promise<TotalCost> {
    const variables = { basketReference, bookingChannelCriteria: bookingChannel ?? {}, ...withLocaleDefaults({ country, language }) } as Record<string, unknown>;
    const bookingInformation = (await ApiCalls.makeGraphqlCall<{ bookingInformation?: { totalCost?: number } }>(
      'getBookingInformation.graphql',
      variables
    )).body.data.bookingInformation;
    return new TotalCost({ bookingInfo: bookingInformation });
  }

  /**
   * GraphQL query for GetRoomStayListFromBookingInformation
   * @param inputData used to set the GraphQL variables for the get hotel booking information experience call.
   * @param inputData.basketReference basketReference
   * @param inputData.bookingChannel bookingChannel
   * @param inputData.language language
   * @param inputData.country country
  * @returns {Array.<RoomStay>} room stays from the booking information response
   */
  static async graphqlGetRoomStayListFromBookingInformation(
    { basketReference, bookingChannel, language, country }: RoomStayListParams
  ): Promise<RoomStay[]> {
    const variables = { basketReference, bookingChannelCriteria: bookingChannel ?? {}, ...withLocaleDefaults({ country, language }) } as Record<string, unknown>;
    const bookingInformation = (await ApiCalls.makeGraphqlCall<{ bookingInformation?: { reservationByIdList?: Array<{ roomStay?: Record<string, unknown> }> } }>(
      'getBookingInformation.graphql',
      variables
    )).body.data.bookingInformation;
    return (bookingInformation?.reservationByIdList ?? []).map(({ roomStay }) =>
      new RoomStay({ bookingInfo: { ...roomStay, ratesPerNight: [] } }),
    );
  }

  /**
   * GraphQL query for GetInfoMessagesFromBookingInformation
   * @param inputData used to set the GraphQL variables for the get hotel booking information experience call.
   * @param inputData.basketReference basketReference
   * @param inputData.bookingChannel bookingChannel
   * @param inputData.language language
   * @param inputData.country country
  * @returns {Array.<String>} info messages from the booking information response
   */
  static async graphqlGetInfoMessagesFromBookingInformation(
    { basketReference, bookingChannel, language, country }: InfoMessagesParams
  ): Promise<string[]> {
    const variables = { basketReference, bookingChannelCriteria: bookingChannel ?? {}, ...withLocaleDefaults({ country, language }) } as Record<string, unknown>;
    const bookingInformation = (await ApiCalls.makeGraphqlCall<{ bookingInformation?: { infoMessages?: string[] } }>(
      'getBookingInformation.graphql',
      variables
    )).body.data.bookingInformation;
    return bookingInformation?.infoMessages ?? [];
  }

  /**
   * GraphQL query for GetUpgradeToFlexBookingInformation
   * @param inputData used to set the GraphQL variables for the get hotel booking information experience call.
   * @param inputData.basketReference BasketReference for which to get the data
   * @param inputData.bookingChannel bookingChannel
   * @param inputData.language Language for which to get the data
   * @param inputData.country Country for which to get the data
  * @returns {BookingInformationUpgradeToFlex} upgrade-to-flex information
   */
  static async graphqlGetUpgradeToFlexBookingInformation(
    { basketReference, bookingChannel, language, country }: UpgradeToFlexParams
  ): Promise<BookingInformationUpgradeToFlex> {
    const variables = { basketReference, bookingChannelCriteria: bookingChannel ?? {}, ...withLocaleDefaults({ country, language }) } as Record<string, unknown>;
    const bookingInformation = (await ApiCalls.makeGraphqlCall<{ bookingInformation?: { upgradeToFlex?: Record<string, unknown>; totalCost?: number } }>(
      'getBookingInformation.graphql',
      variables
    )).body.data.bookingInformation;
    return new BookingInformationUpgradeToFlex({ bookingInfo: bookingInformation });
  }

  /**
   * GraphQL query for GetBookingInformation
   * @param inputData used to set the GraphQL variables for the get hotel booking information experience call.
   * @param inputData.basketReference basketReference
   * @param inputData.bookingChannel bookingChannel
   * @param inputData.language language
   * @param inputData.country country
  * @returns {BookingInformation} booking information response
   */
  static async graphqlGetBookingInformation(
    { basketReference, bookingChannel, language, country }: BookingInfoParams
  ): Promise<BookingInformation> {
    const variables = { basketReference, bookingChannelCriteria: bookingChannel ?? {}, ...withLocaleDefaults({ country, language }) } as Record<string, unknown>;
    const bookingInformation = (await ApiCalls.makeGraphqlCall<{ bookingInformation?: Record<string, unknown> }>(
      'getBookingInformation.graphql',
      variables
    )).body.data.bookingInformation ?? {};
    return new BookingInformation({ bookingInformation });
  }

  /**
   * Retrieve booking confirmation info from GraphQL request
   * @param inputData used to set the GraphQL variables for the get hotel booking information experience call.
   * @param inputData.basketReference basket reference id
   * @param inputData.language language code
   * @param inputData.country country code
   * @param inputData.bookingChannel booking Channel code
  * @returns {BookingConfirmation} booking confirmation response
   */
  static async graphqlGetBookingConfirmation(
    { basketReference, language, country, bookingChannel }: BookingConfirmationParams
  ): Promise<BookingConfirmation> {
    const app = String((global.browser?.options as Record<string, unknown> | undefined)?.app ?? 'pi');
    const variables = { basketReference, bookingChannel: bookingChannel ?? (app === 'pib' ? 'BB' : app.toUpperCase()), ...withLocaleDefaults({ country, language }) } as Record<string, unknown>;
    const bookingConfirmation = (await ApiCalls.makeGraphqlCall(
      'getBookingConfirmation.graphql',
      variables
    ) as any).body.data.bookingConfirmation;
    return new BookingConfirmation({ bookingConfirmation });
  }

  /**
   * GraphQL query for GetTermsAndConditions
   * @param inputData used to set the GraphQL variables for getTermsAndConditions call.
   * @param inputData.hotelId Hotel Id for which to get the data
   * @param inputData.language Language for which to get the data
   * @param inputData.country Country for which to get the data
   * @param inputData.rateCode The rate code for which to get the data
   * @param inputData.bookingChannel booking channel code
  * @returns {TermsAndConditions} terms and conditions response
   */
  static async graphqlGetTermsAndConditions(
    { hotelId, language, country, rateCode, bookingChannel }: TermsAndConditionsParams = {}
  ): Promise<TermsAndConditions> {
    const app = String((global.browser?.options as Record<string, unknown> | undefined)?.app ?? 'pi');
    const variables = { hotelId: hotelId ?? Hotels.DEFAULT_HOTEL.id, rateCode: rateCode ?? HotelRates.PI_FLEX.ratePlanCode, bookingChannel: bookingChannel ?? app.toUpperCase(), ...withLocaleDefaults({ country, language }) } as Record<string, unknown>;
    const termsAndConditions = (await ApiCalls.makeGraphqlCall(
      'getTermsAndConditions.graphql',
      variables
    ) as any).body.data.termsAndConditions;
    return new TermsAndConditions({ termsAndConditions });
  }

  /**
   * GraphQL query used for GetRoomTypeInformation
   * @param inputData object input data
   * @param inputData.brand brand
   * @param inputData.language language
   * @param inputData.country country
  * @returns {HotelRoomTypeInformation} room type information response
   */
  static async graphqlGetRoomTypeInformation(
    { brand, language, country }: RoomTypeInfoParams = {}
  ): Promise<HotelRoomTypeInformation> {
    const variables = { brand: brand ?? 'PI', ...withLocaleDefaults({ country, language }) } as Record<string, unknown>;
    const roomTypeInformation = (await ApiCalls.makeGraphqlCall<{ roomTypeInformation?: { roomTypes?: Array<Record<string, unknown>> } }>(
      'getRoomTypeInformation.graphql',
      variables
    )).body.data.roomTypeInformation;
    const hotelRoomTypeInformation = new HotelRoomTypeInformation({ roomTypeInformation });
    hotelRoomTypeInformation.brand = String(variables.brand);
    return hotelRoomTypeInformation;
  }

  /**
   * GraphQL query used for GetManageBookingRoomTypeInformation
   * @param inputData object input data
   * @param inputData.pmsRoomType pms room type object
   * @param inputData.hotelType hotel brand object
   * @param inputData.language language
   * @param inputData.country country
  * @returns {HotelRoomTypeInformation} manage booking room type information response
   */
  static async graphqlGetManageBookingRoomTypeInformation(
    { pmsRoomType, hotelType, language, country }: ManageBookingRoomTypeInfoParams = {}
  ): Promise<HotelRoomTypeInformation> {
    const brand = hotelType ?? (pmsRoomType?.type ? { name: pmsRoomType.type, nameLowercase: pmsRoomType.type.toLowerCase() } : HotelBrands.PI);
    const variables = { brand: brand.name, ...withLocaleDefaults({ country, language }) } as Record<string, unknown>;
    const roomTypeInformation = (await ApiCalls.makeGraphqlCall<{ roomTypeInformation?: { roomTypes?: Array<Record<string, unknown>> } }>(
      'getRoomTypeInformation.graphql',
      variables
    )).body.data.roomTypeInformation;
    const hotelRoomTypeInformation = new HotelRoomTypeInformation({ roomTypeInformation });
    hotelRoomTypeInformation.brand = brand.name;
    return hotelRoomTypeInformation;
  }

  /**
   * Get all countries from getCountries GraphQL
   * @param data Object
   * @param data.country  Translate of countries name 
   * @param data.language Language of countries name
   * @param data.site Type of site
  * @returns {Array.<Record<string, unknown>>} countries with names and dialing details
   */
  static async graphqlGetCountries(
    { country, language, site }: CountriesParams = {}
  ): Promise<Array<Record<string, unknown>>> {
    const variables = { ...withLocaleDefaults({ country, language }), site: site ?? 'leisure' } as Record<string, unknown>;
    return (await ApiCalls.makeGraphqlCall(
      'getCountries.graphql',
      variables
    ) as any).body.data.countries.countries;
  }

  /**
   * Get companies from getSearchCompanies GraphQL
   * @param data Object
   * @param data.hotelId hotel ID
   * @param data.companyName company name
   * @param data.limit limit
  * @returns {Companies} companies matching the search criteria
   */
  static async graphqlGetSearchCompanies(
    { hotelId, companyName, limit }: SearchCompaniesParams = {}
  ): Promise<Companies> {
    const variables = { hotelId, companyName: companyName ?? '', limit: limit ?? 50 } as Record<string, unknown>;
    const companies = (await ApiCalls.makeGraphqlCall<{ searchCompanies?: { companies?: Array<Record<string, unknown>> } }>(
      'getSearchCompanies.graphql',
      variables
    )).body.data.searchCompanies?.companies ?? [];
    return new Companies({ companies });
  }

  /**
   * Get negotiated rates companies from getSearchCompanies GraphQL with retry logic
   * @param data Object
   * @param data.limit limit
   * @param data.offset offset
   * @param data.searchTerm search term
  * @returns {Companies} companies matching negotiated-rate criteria
   * @throws {Error} Throws an error if the company matching searchTerm with the specified negotiatedRateEnabled value is not found after retries
   */
  static async graphqlGetSearchCompaniesByNegotiatedRates(
    { limit, offset, searchTerm, negotiatedRateCompanies }: SearchCompaniesByNegotiatedRatesParams = {}
  ): Promise<Companies> {
    const variables = { limit: limit ?? 50, offset: offset ?? 1, searchTerm, negotiatedRateCompanies: negotiatedRateCompanies ?? true } as Record<string, unknown>;
    const companies = (await ApiCalls.makeGraphqlCall<{ searchCompanies?: { companies?: Array<Record<string, unknown>> } }>(
      'getSearchNegotiatedRatesCompanies.graphql',
      variables
    )).body.data.searchCompanies?.companies ?? [];
    return new Companies({ companies });
  }

  /**
   * GraphQL query for GetRoomClassConfig
   * @param data object data
   * @param data.channel booking channel code
   * @param data.brand hotel brand
   * @param data.country country
   * @param data.language language
  * @returns {RoomClassConfig} room class configuration
   */
  static async graphqlGetRoomClassConfig(
    { channel, brand, country, language }: RoomClassConfigParams = {}
  ): Promise<RoomClassConfig> {
    const project = String(global.browser?.options?.app ?? 'pi');
    const variables = { channel: channel ?? project.toUpperCase(), brand: brand ?? 'pi', ...withLocaleDefaults({ country, language }) } as Record<string, unknown>;
    const roomClassConfig = (await ApiCalls.makeGraphqlCall<{ roomClassConfig?: { roomClassConfig?: Array<Record<string, unknown>> } }>(
      'getRoomClassConfig.graphql',
      variables
    )).body.data.roomClassConfig;
    return new RoomClassConfig({ roomClassConfig });
  }

  /**
   * GraphQL query for GetEmployees
   * @param data object data
   * @param data.companyId company id
   * @param data.searchCriteria search criteria (search by name or email)
   * @param data.bookingChannel booking channel
   * @param data.awaitingApproval awaitingApproval
   * @param data.size size
  * @returns {Array.<Employees>} employees matching the search criteria
   */
  static async graphqlGetEmployees(
    { companyId, searchCriteria, bookingChannel, awaitingApproval, size }: EmployeesParams = {}
  ): Promise<Employees[]> {
    const variables = { companyId, searchCriteria: searchCriteria ?? '', bookingChannel: bookingChannel ?? 'CBT', awaitingApproval: awaitingApproval ?? false, size: size ?? 15 } as Record<string, unknown>;
    const employees = (await ApiCalls.makeGraphqlCall<{ getEmployeesV2?: { employees?: Array<Record<string, unknown>> } }>(
      'getEmployees.graphql',
      variables, true, true
    )).body.data.getEmployeesV2?.employees ?? [];
    return employees.map((employee) => new Employees({ employee }));
  }

  /**
   * GraphQL query for GetEmployees with filtering options
   * @param data object data
   * @param data.companyId company id
   * @param data.searchCriteria search criteria (search by name or email)
   * @param data.bookingChannel booking channel
   * @param data.awaitingApproval awaitingApproval
   * @param data.size size
   * @param data.shouldFilterEmployees should filter employees
  * @returns {Array.<Employees>} employees matching the selected filters
   */
  static async graphqlGetEmployeesWithFilteringOptions(
    { companyId, searchCriteria, bookingChannel, awaitingApproval, size, shouldFilterEmployees }: EmployeesParams = {}
  ): Promise<Employees[]> {
    const variables = { companyId, searchCriteria: searchCriteria ?? '', bookingChannel: bookingChannel ?? 'CBT', awaitingApproval: awaitingApproval ?? false, size: size ?? 15, shouldFilterEmployees: shouldFilterEmployees ?? false } as Record<string, unknown>;
    const employees = (await ApiCalls.makeGraphqlCall<{ getEmployeesWithFilteringOptionsV2?: { employees?: Array<Record<string, unknown>> } }>(
      'getEmployeesWithFilteringOptions.graphql',
      variables, true, true
    )).body.data.getEmployeesWithFilteringOptionsV2?.employees ?? [];
    return employees.map((employee) => new Employees({ employee }));
  }

  /**
   * GraphQL query for getAccountList
   * @param data object data
   * @param data.viewAll view all accounts
  * @returns {Array.<Accounts>} accounts for the current user
   */
  static async graphqlGetAccountList(
    { viewAll }: AccountListParams = {}
  ): Promise<Accounts[]> {
    const variables = { viewAll: viewAll ?? false } as Record<string, unknown>;
    const accounts = (await ApiCalls.makeGraphqlCall<{ getAccountList?: { accounts?: Array<Record<string, unknown>> } }>(
      'getAccountList.graphql',
      variables, true, true
    )).body.data.getAccountList?.accounts ?? [];
    return accounts.map((account) => new Accounts({ account }));
  }

  /**
   * GraphQL query for GetCompanyRegistrationQuestionsAndAnswers
   * @param data object data
   * @param data.companyId companyId
  * @returns {AnsweredQuestions} answered company-registration questions
   */
  static async graphqlGetCompanyRegistrationQuestionsAndAnswers(
    { companyId }: CompanyRegistrationQuestionsParams = {}
  ): Promise<AnsweredQuestions> {
    const variables = { companyId } as Record<string, unknown>;
    const answeredQuestions = (await ApiCalls.makeGraphqlCall<{ getCompanyRegistrationQuestionsAndAnswers?: Record<string, unknown> }>(
      'getCompanyRegistrationQuestionsAndAnswers.graphql',
      variables, true, true
    )).body.data.getCompanyRegistrationQuestionsAndAnswers ?? {};
    return new AnsweredQuestions({ answeredQuestions });
  }

  /**
   * GraphQL query to get payment cards from getCompanyDetails
   * @param data object data
   * @param data.companyId company Id
  * @returns {Array.<CompanyPaymentCard>} company payment cards
   */
  static async graphqlGetCompanyPaymentCards(
    { companyId }: CompanyPaymentCardsParams = {}
  ): Promise<CompanyPaymentCard[]> {
    const variables = { companyId } as Record<string, unknown>;
    const paymentCards = (await ApiCalls.makeGraphqlCall<{ companyDetailsV2?: { requestedCompany?: { paymentDetails?: { paymentCards?: Array<Record<string, unknown>> } } } }>(
      'getCompanyDetails.graphql',
      variables, true, true
    )).body.data.companyDetailsV2?.requestedCompany?.paymentDetails?.paymentCards ?? [];
    return paymentCards.map((companyPaymentCard) => new CompanyPaymentCard({ companyPaymentCard }));
  }

  /**
   * GraphQL query to get company details from getCompanyDetails
   * @param data object data
   * @param data.companyId company Id
  * @returns {CompanyDetails} company details
   */
  static async graphqlGetCompanyDetails(
    { companyId }: CompanyDetailsParams = {}
  ): Promise<CompanyDetails> {
    const variables = { companyId } as Record<string, unknown>;
    const companyDetails = (await ApiCalls.makeGraphqlCall<{ companyDetailsV2?: { requestedCompany?: { companyDetails?: Record<string, unknown> } } }>(
      'getCompanyDetails.graphql',
      variables, true, true
    )).body.data.companyDetailsV2?.requestedCompany?.companyDetails ?? {};
    return new CompanyDetails({ companyDetails });
  }

  /**
   * GraphQL query to get company employee details from getEmployeeDetails
   * @param data object data
   * @param data.companyId company Id
   * @param data.employeeId employee Id
  * @returns {EmployeeDetails} employee details
   */
  static async graphqlGetEmployeeDetails(
    { companyId, employeeId }: EmployeeDetailsParams = {}
  ): Promise<EmployeeDetails> {
    const variables = { companyId, employeeId } as Record<string, unknown>;
    const employeeDetails = (await ApiCalls.makeGraphqlCall<{ getEmployeeDetailsV2?: Record<string, unknown> }>(
      'getEmployeeDetails.graphql',
      variables, true, true
    )).body.data.getEmployeeDetailsV2 ?? {};
    return new EmployeeDetails({ employeeDetails });
  }

  /**
   * GraphQL query to get room requirements from getProfileDetails
   * @param data object data
   * @param data.customerId customer Id
   * @param data.business business
  * @returns {RoomRequirements | null} customer room requirements
   */
  static async graphqlGetProfileDetailsRoomRequirements(
    { customerId, business }: ProfileDetailsRoomRequirementsParams = {}
  ): Promise<RoomRequirements | null> {
    const variables = { customerId, business } as Record<string, unknown>;
    const roomRequirements = (await ApiCalls.makeGraphqlCall<{ getProfileDetailsV3?: { bookingPreference?: { roomRequirements?: Record<string, unknown> } } }>(
      'getProfileDetailsRoomRequirements.graphql',
      variables, true, true
    )).body.data.getProfileDetailsV3?.bookingPreference?.roomRequirements;
    return roomRequirements ? new RoomRequirements(roomRequirements) : null;
  }

  /**
   * GraphQL query for getProfileDetails
   * @param data object data
   * @param data.customerId customer Id
   * @param data.business business
   * @param data.loggedUser true if user is logged in
   * @param data.username email address of the user in case user is not logged in
   * @param data.password user password in case user is not logged in
  * @returns {Customer} customer profile details
   */
  static async graphqlGetProfileDetails(
    { customerId, business, loggedUser, username, password }: ProfileDetailsParams = {}
  ): Promise<Customer> {
    const variables = { customerId, business } as Record<string, unknown>;
    const customer = (await ApiCalls.makeGraphqlCall<{ getProfileDetailsV3?: Record<string, unknown> }>(
      'getProfileDetails.graphql',
      variables, true, loggedUser ?? true, username, password
    )).body.data.getProfileDetailsV3 ?? {};
    return new Customer({ customer });
  }

  /**
   * GraphQL query for updateProfileDetails
   * @param inputData used to set the GraphQL variables
   * @param inputData.customerId customer Id
   * @param inputData.business business
   * @param inputData.innBusiness innBusiness
  * @param {CustomerRequest} inputData.payload customer profile update request
  * @returns {Promise<void>} after updating the customer profile
   */
  static async graphqlUpdateProfileDetails(
    { customerId, business, innBusiness, payload }: UpdateProfileDetailsParams = {}
  ): Promise<void> {
    const variables = { customerId, business, innBusiness, payload } as Record<string, unknown>;
    await ApiCalls.makeGraphqlCall(
      'updateProfileDetails.graphql',
      variables, true, true
    );
  }

  /**
   * GraphQL query for updateCompanyDetails
   * @param inputData used to set the GraphQL variables
   * @param inputData.companyId company Id
  * @param inputData.companySummary companySummary payload object
  * @returns {Promise<void>} after updating company details
   */
  static async graphqlUpdateCompanyDetails(
    { companyId, companySummary }: UpdateCompanyDetailsParams = {}
  ): Promise<void> {
    const variables = { companyId, companySummary } as Record<string, unknown>;
    await ApiCalls.makeGraphqlCall(
      'updateCompanyDetails.graphql',
      variables, true, true
    );
  }

  /**
   * GraphQL query for GetDlpInformation
   * @param data object data
   * @param data.country country
   * @param data.language language
   * @param data.dlpPath dlpPath
  * @returns {DlpInformation} destination landing page information
   */
  static async graphqlGetDlpContentService(
    { country, language, dlpPath }: DlpContentServiceParams = {}
  ): Promise<DlpInformation> {
    const locale = getCurrentLocale();
    const variables = { country: country ?? locale.country, language: language ?? locale.language, dlpPath } as Record<string, unknown>;
    const response = await ApiCalls.makeGraphqlCall<{ dlpInformation?: Record<string, unknown> }>(
      'getDlpInformation.graphql',
      variables
    );
    const dlpInformation = response.body.data.dlpInformation ?? {};
    return new DlpInformation({ dlpInformation });
  }

  /**
   * GraphQL query for updateEmployee
   * @param inputData used to set the GraphQL variables
   * @param inputData.companyId customer Id
   * @param inputData.employeeId employee Id
   * @param inputData.languageCode language code
  * @param {EmployeeCriteria} inputData.updateEmployeeCriteria employee update request
   * @param inputData.loggedUser true if user is logged in
   * @param inputData.username email address of the user in case user is not logged in
   * @param inputData.password user password in case user is not logged in
   */
  static async graphqlUpdateEmployee(
    { companyId, employeeId, languageCode, updateEmployeeCriteria, loggedUser, username, password }: UpdateEmployeeParams = {}
  ): Promise<void> {
    const variables = { companyId, employeeId, languageCode, updateEmployeeCriteria, loggedUser, username, password } as Record<string, unknown>;
    await ApiCalls.makeGraphqlCall(
      'updateEmployee.graphql',
      variables, true, loggedUser ?? true, username, password
    );
  }

  /**
   * GraphQL query to get account balance summary from getAccountBalanceSummary
   * @param data object data
   * @param data.viewAll view all balances
  * @returns {CustomerAccountCurrentBalancesResponse} account balance summary
   */
  static async graphqlCustomerAccountCurrentBalances(
    { viewAll }: AccountCurrentBalancesParams = {}
  ): Promise<CustomerAccountCurrentBalancesResponse> {
    const variables = { viewAll: viewAll ?? true } as Record<string, unknown>;
    const customerAccountCurrentBalancesResponse = (await ApiCalls.makeGraphqlCall<{ getAccountBalanceSummary?: Record<string, unknown> }>(
      'getAccountBalanceSummary.graphql',
      variables, true, true
    )).body.data.getAccountBalanceSummary ?? {};
    return new CustomerAccountCurrentBalancesResponse({ customerAccountCurrentBalancesResponse });
  }

  /**
   * GraphQL query to get customer invoices
   * @param data object data
   * @param data.payload payload object
  * @returns {CustomerInvoices} customer invoices
   */
  static async graphqlCustomerInvoices(
    { payload }: CustomerInvoicesParams = {}
  ): Promise<CustomerInvoices> {
    const invoicePayload = payload ?? {};
    const searchCriteria = invoicePayload.searchCriteria as Record<string, unknown> | undefined;
    const pagingRequest = invoicePayload.pagingRequest as Record<string, unknown> | undefined;
    const variables = {
      payload: {
        tetheredUserGuid: invoicePayload.tetheredUserGuid,
        schemeCustomerId: invoicePayload.schemeCustomerId,
        scheme: invoicePayload.scheme,
        searchCriteria: { dateFrom: searchCriteria?.dateFrom, dateTo: searchCriteria?.dateTo },
        pagingRequest: { page: pagingRequest?.page ?? 1, maximumDisplayRows: pagingRequest?.maximumDisplayRows ?? 15 }
      }
    } as Record<string, unknown>;
    const customerCurrentInvoice = (await ApiCalls.makeGraphqlCall<{ viewCustomerInvoicesV2?: Record<string, unknown> }>(
      'viewCustomerInvoices.graphql',
      variables, true, true
    )).body.data.viewCustomerInvoicesV2 ?? {};
    return new CustomerInvoices({ customerCurrentInvoice });
  }

  /**
   * GraphQL query to get payment information
   * @param data object data
   * @param data.accountId user WL account Id
  * @returns {Array.<Record<string, unknown>>} payment information rows for the interim-payments SIP table
   */
  static async graphqlGetPaymentInfo(
    { accountId }: PaymentInfoParams = {}
  ): Promise<Array<Record<string, unknown>>> {
    const variables = { paymentInfoCriteria: { accountId, page: 1, size: 15, nonInvoiceOnly: false } } as Record<string, unknown>;
    return (await ApiCalls.makeGraphqlCall<{ getPaymentInfo?: { payments?: Array<Record<string, unknown>> } }>(
      'getPaymentInformation.graphql',
      variables, true, true
    )).body.data.getPaymentInfo?.payments ?? [];
  }

  /**
   * GraphQL query to get customer statement value on SIP
   * @param data object data
   * @param data.scheme scheme
   * @param data.tetheredUserId tetheredUserId
  * @returns {number} customer statement value
   */
  static async graphqlCustomerStatementValue(
    { scheme, tetheredUserId }: CustomerStatementValueParams = {}
  ): Promise<number> {
    const variables = { scheme, tetheredUserId } as Record<string, unknown>;
    return Number((await ApiCalls.makeGraphqlCall<{ getAccountInfo?: { statementValue?: number } }>(
      'getAccountInfo.graphql',
      variables, true, true
    )).body.data.getAccountInfo?.statementValue ?? 0);
  }

  /**
   * GraphQL query to get customer account balance on SIP
   * @param data object data
   * @param data.scheme scheme
   * @param data.tetheredUserGuid tetheredUserGuid
  * @returns {CurrentBalanceItem} customer SIP account balance
   */
  static async graphqlCustomerSipAccountBalance(
    { scheme, tetheredUserGuid }: CustomerSipAccountBalanceParams = {}
  ): Promise<CurrentBalanceItem> {
    const variables = { scheme, tetheredUserGuid } as Record<string, unknown>;
    const currentBalanceItem = (await ApiCalls.makeGraphqlCall<{ getAccountBalanceSummaryV2?: Record<string, unknown> }>(
      'getAccountBalanceSummaryV2.graphql',
      variables, true, true
    )).body.data.getAccountBalanceSummaryV2 ?? {};
    return new CurrentBalanceItem({ currentBalanceItem });
  }

  /**
   * GraphQL query to get company spending from getCompanySpending response
   * @param data object data
   * @param data.fromMonthYear "MM-YYYY" month and year from which the query will be performed
   * @param data.toMonthYear "MM-YYYY" month and year to which the query will be performed
  * @returns {CompanySpendingResponse} company spending response
   */
  static async graphqlGetCompanySpending(
    { fromMonthYear, toMonthYear }: CompanySpendingParams = {}
  ): Promise<CompanySpendingResponse> {
    const variables = { fromMonthYear, toMonthYear } as Record<string, unknown>;
    const companySpending = (await ApiCalls.makeGraphqlCall<{ getCompanySpending?: { companySpendingDtoList?: Array<Record<string, unknown>> } }>(
      'getCompanySpending.graphql',
      variables, true, true
    )).body.data.getCompanySpending;
    return new CompanySpendingResponse({ companySpendingDtoList: companySpending?.companySpendingDtoList });
  }

  /**
   * GraphQL query to get employee spend from EmployeeSpendQuery response
   * @param data object data
   * @param data.fromMonthYear "MM-YYYY" month and year from which the query will be performed
   * @param data.toMonthYear "MM-YYYY" month and year to which the query will be performed
  * @returns {EmployeeSpendResponse} employee spending response
   */
  static async graphqlGetEmployeeSpend(
    { fromMonthYear, toMonthYear }: EmployeeSpendParams = {}
  ): Promise<EmployeeSpendResponse> {
    const variables = { fromMonthYear, toMonthYear } as Record<string, unknown>;
    const employeeSpend = (await ApiCalls.makeGraphqlCall<{ getEmployeeSpend?: { employeeSpendDtoList?: Array<Record<string, unknown>> } }>(
      'getEmployeeSpend.graphql',
      variables, true, true
    )).body.data.getEmployeeSpend;
    return new EmployeeSpendResponse({ employeeSpendDtoList: employeeSpend?.employeeSpendDtoList });
  }

  /**
  * GraphQL query to get Worldline customer preferences
   * @param data object data
   * @param data.tetheredUserGuids tetheredUserGuids
  * @returns {Array.<WorldlineUserPreferences>} Worldline user preferences
   */
  static async graphqlGetWorldlineUserPreferences(
    { tetheredUserGuids }: WorldlineUserPreferencesParams = {}
  ): Promise<WorldlineUserPreferences[]> {
    const variables = { tetheredUserGuids: [...(tetheredUserGuids ?? [])] } as Record<string, unknown>;
    const preferences = (await ApiCalls.makeGraphqlCall<{ getWorldlineUserPreferences?: Array<Record<string, unknown>> }>(
      'getWorldlineUserPreferences.graphql',
      variables, true, true
    )).body.data.getWorldlineUserPreferences ?? [];
    return preferences.map((userPreferences) => new WorldlineUserPreferences({ userPreferences }));
  }

  /**
   * GraphQL query for getBookingAlerts
   * @param data object data
   * @param data.companyId companyId
  * @returns {CompanyBookingAlerts} company booking alerts
   */
  static async graphqlGetBookingAlerts(
    { companyId }: BookingAlertsParams = {}
  ): Promise<CompanyBookingAlerts> {
    const variables = { companyId } as Record<string, unknown>;
    const bookingAlerts = (await ApiCalls.makeGraphqlCall<{ companyDetailsV2?: { requestedCompany?: { bookingAlerts?: Record<string, unknown> } } }>(
      'getCompanyDetails.graphql',
      variables, true, true
    )).body.data.companyDetailsV2?.requestedCompany?.bookingAlerts ?? {};
    return new CompanyBookingAlerts({ bookingAlerts });
  }

  /**
   * GraphQL query for updateBookingAlerts
   * @param inputData used to set the GraphQL variables
   * @param inputData.companyId company Id
   * @param inputData.bookingAlerts booking alerts object
   */
  static async graphqlUpdateBookingAlerts(
    { companyId, bookingAlerts }: UpdateBookingAlertsParams = {}
  ): Promise<void> {
    const variables = { companyId, bookingAlerts } as Record<string, unknown>;
    await ApiCalls.makeGraphqlCall(
      'updateBookingAlerts.graphql',
      variables, true, true
    );
  }

  /**
   * GraphQL query for getBookingAllowances
   * @param data object data
   * @param data.companyId companyId
  * @returns {CompanyBookingAllowances} company booking allowances
   */
  static async graphqlGetBookingAllowances(
    { companyId }: BookingAllowancesParams = {}
  ): Promise<CompanyBookingAllowances> {
    const variables = { companyId } as Record<string, unknown>;
    const bookingAllowances = (await ApiCalls.makeGraphqlCall<{ companyDetailsV2?: { requestedCompany?: { bookingAllowances?: Record<string, unknown> } } }>(
      'getCompanyDetails.graphql',
      variables, true, true
    )).body.data.companyDetailsV2?.requestedCompany?.bookingAllowances ?? {};
    return new CompanyBookingAllowances({ bookingAllowances });
  }

  /**
   * GraphQL query for updateBookingAllowances
   * @param inputData used to set the GraphQL variables
   * @param inputData.companyId company Id
   * @param inputData.bookingAllowances booking allowances object
   */
  static async graphqlUpdateBookingAllowances(
    { companyId, bookingAllowances }: UpdateBookingAllowancesParams = {}
  ): Promise<void> {
    const variables = { companyId, bookingAllowances } as Record<string, unknown>;
    await ApiCalls.makeGraphqlCall(
      'updateBookingAllowances.graphql',
      variables, true, true
    );
  }

  /**
   * GraphQL query to get get account spending
   * @param data object data
   * @param data.accountSpendingCriteria account spending criteria object
  * @returns {AccountSpendingResponse} account spending response
   */
  static async graphqlGetAccountSpending(
    { accountSpendingCriteria }: AccountSpendingParams = {}
  ): Promise<AccountSpendingResponse> {
    const variables = { accountSpendingCriteria } as Record<string, unknown>;
    const accountSpending = (await ApiCalls.makeGraphqlCall<{ getAccountSpending?: { accountSpendingDtoList?: Array<Record<string, unknown>> } }>(
      'getAccountSpending.graphql',
      variables, true, true
    )).body.data.getAccountSpending;
    return new AccountSpendingResponse({ accountSpendingDtoList: accountSpending?.accountSpendingDtoList });
  }

  /**
   * GraphQL query for GetAllPibaCards
   * @param data object data
   * @param data.tetheredUserId tethered user id
   * @param data.countryCode country code
   * @param data.pibaCardsCriteria piba cards criteria object
  * @returns {Array.<PibaCards>} PIBA cards
   */
  static async graphqlGetPibaCards(
    { tetheredUserId, countryCode, pibaCardsCriteria }: PibaCardsParams = {}
  ): Promise<PibaCards[]> {
    const variables = { tetheredUserId, countryCode: countryCode ?? 'GB', pibaCardsCriteria: pibaCardsCriteria ?? {} } as Record<string, unknown>;
    const pibaCards = (await ApiCalls.makeGraphqlCall<{ getAllPIBACards?: { innBusinessPayCardList?: Array<Record<string, unknown>> } }>(
      'getAllPibaCards.graphql',
      variables, true, true
    )).body.data.getAllPIBACards?.innBusinessPayCardList ?? [];
    return pibaCards.map((pibaCard) => new PibaCards({ pibaCards: pibaCard }));
  }

  /**
   * Delete user defined (custom) questions 
   * @param data object data
   * @param data.companyId company Id
   * @param data.questionId custom questionId
   */
  static async deleteCustomQuestion(
    { companyId, questionId }: DeleteCustomQuestionParams = {}
  ): Promise<void> {
    const variables = { companyId, questionId } as Record<string, unknown>;
    await ApiCalls.makeGraphqlCall(
      'deleteCustomQuestion.graphql',
      variables, true, true
    );
  }

  /**
   * GraphQL query to get pay applications
  * @returns {Array.<Record<string, unknown>>} pay-app applications
   */
  static async graphqlGetPayApplications(
    {}: PayApplicationsParams = {}
  ): Promise<Array<Record<string, unknown>>> {
    const variables = {} as Record<string, unknown>;
    return (await ApiCalls.makeGraphqlCall<{ getPayApplications?: { applications?: Array<Record<string, unknown>> } }>(
      'getPayApplications.graphql',
      variables, true, true
    )).body.data.getPayApplications?.applications ?? [];
  }

  /**
   * GraphQL query to get pay application details
   * @param data data object
   * @param data.scheme application scheme 
   * @param data.applicationId application id
   * @param data.applicationGuid application guid
  * @returns {ApplicationDetails} application details
   */
  static async graphqlGetApplicationDetails(
    { scheme, applicationId, applicationGuid }: ApplicationDetailsParams = {}
  ): Promise<ApplicationDetails> {
    const variables = { scheme, applicationId, applicationGuid } as Record<string, unknown>;
    const applicationDetails = (await ApiCalls.makeGraphqlCall<{ getApplicationDetails?: Record<string, unknown> }>(
      'getApplicationDetails.graphql',
      variables, true, true
    )).body.data.getApplicationDetails ?? {};
    return new ApplicationDetails({ applicationDetails });
  }

  /**
   * GraphQL query to get company details lookup
   * @param data object data
   * @param data.companyRegistrationNumber company registration number too lookup for
   * @param data.scheme pay app scheme (GB/DE)
   * @returns {Record<string, unknown>} company details lookup result
   */
  static async graphqlGetCompanyDetailsLookup(
    { companyRegistrationNumber, scheme }: CompanyDetailsLookupParams = {}
  ): Promise<Record<string, unknown>> {
    const variables = { companyRegistrationNumber, scheme } as Record<string, unknown>;
    return (await ApiCalls.makeGraphqlCall<{ companyDetailsLookup?: { data?: Record<string, unknown> } }>(
      'companyDetailsLookup.graphql',
      variables, true, true
    )).body.data.companyDetailsLookup?.data ?? {};
  }

  /**
   * GraphQL query to get piba card details
   * @param data object data
   * @param data.tetheredUserId tetheredUserId
   * @param data.countryCode country code from scheme (GB/DE)
   * @param data.cardId PIBA card ID
  * @returns {PibaCardDetails} PIBA card details
   */
  static async graphqlGetPibaCardDetails(
    { tetheredUserId, countryCode, cardId }: PibaCardDetailsParams = {}
  ): Promise<PibaCardDetails> {
    const variables = { tetheredUserId, countryCode, cardId } as Record<string, unknown>;
    const pibaCard = (await ApiCalls.makeGraphqlCall<{ getPIBACardDetails?: Record<string, unknown> }>(
      'getPibaCardDetails.graphql',
      variables, true, true
    )).body.data.getPIBACardDetails ?? {};
    return new PibaCardDetails({ pibaCard });
  }

  /**
   * GraphQL query to get account transactions
   * @param data object data
   * @param data.payload payload object
  * @returns {Array.<AccountTransactions>} account transactions
   */
  static async graphqlGetAccountTransactions(
    { payload }: AccountTransactionsParams = {}
  ): Promise<AccountTransactions[]> {
    const accountPayload = payload ?? {};
    const searchCriteria = accountPayload.searchCriteria as Record<string, unknown> | undefined;
    const dateSearch = searchCriteria?.dateSearch as Record<string, unknown> | undefined;
    const pagingRequest = accountPayload.pagingRequest as Record<string, unknown> | undefined;
    const variables = { payload: { pagingRequest: { page: pagingRequest?.page ?? 1, maximumDisplayRows: pagingRequest?.maximumDisplayRows ?? 15 }, scheme: accountPayload.scheme, schemeCustomerId: accountPayload.schemeCustomerId, searchCriteria: { dateSearch: { dateFrom: dateSearch?.dateFrom, dateTo: dateSearch?.dateTo, transactionTypes: 'Both' } }, tetheredUserGuid: accountPayload.tetheredUserGuid } } as Record<string, unknown>;
    const transactions = (await ApiCalls.makeGraphqlCall<{ viewAccountTransactions?: { response?: { transactions?: Array<Record<string, unknown>> } } }>(
      'getAccountTransactions.graphql',
      variables, true, true
    )).body.data.viewAccountTransactions?.response?.transactions ?? [];
    return transactions.map((accountTransactions) => new AccountTransactions({ accountTransactions }));
  }

  /**
   * GraphQL query to get cost centre details
   * @param data object data
   * @param data.tetheredUserGuid tetheredUserGuid
   * @returns {Array.<CostCentreDetails>} cost centre details
   */
  static async graphqlGetCostCentreDetails(
    { tetheredUserGuid }: CostCentreDetailsParams = {}
  ): Promise<CostCentreDetails[]> {
    const variables = { tetheredUserGuid } as Record<string, unknown>;
    const costCentreDetails = (await ApiCalls.makeGraphqlCall<{ getCostCentreDetails?: Array<Record<string, unknown>> }>(
      'getCostCentreDetails.graphql',
      variables, true, true
    )).body.data.getCostCentreDetails ?? [];
    return costCentreDetails.map((costCentreDetail) => new CostCentreDetails({ costCentreDetail }));
  }

  /**
   * Get and validate a company with negotiated rates enabled by name
   * @param data Object
   * @param data.companyName company name to search for
  * @returns {Company} validated company with a non-empty corpId
   * @throws {Error} if company not found or has invalid corpId
   */
  static async graphqlGetValidatedNegotiatedRatesCompanyByName(
    { companyName }: SearchCompaniesParams = {}
  ): Promise<Company> {
    const companies = await this.graphqlGetSearchCompaniesByNegotiatedRates({ searchTerm: companyName });
    const company = companies.companies.find((item) => item.name === companyName && item.negotiatedRateEnabled === true);
    if (!company) {
      throw new Error(`No negotiated-rate-enabled company found with name "${companyName}"`);
    }
    if (!company.corpId) {
      throw new Error(`Company "${companyName}" has invalid corpId (empty or missing)`);
    }
    return company;
  }

}
