import { Constants } from '../test-data/constants';
import { ApiDictionary } from './aem/apiDictionary';
import { asObject } from './apiValueUtils';
import { EntityApiCalls } from './entityApiCalls';
import { ApiBasketCalls } from './graphql/apiBasketCalls';
import { Basket, FooterInformation, HotelImportantInformation, MealPackages } from './response';
import type {
  ApplicationDetails,
  BookingConfirmation,
  BookingInformation,
  Donations,
  FindBooking,
  HotelAvailabilities,
  HotelAvailability,
  HotelInventory,
  HotelRoomTypeInformation,
  PlanetPaymentDetails,
  ReservationInfo,
  ManageBooking,
  UserProfileDetails,
} from './response';
import { OhipApiCalls } from './ohip/ohipApiCalls';
import { ApiCalls } from './graphql/apiCalls';
import { ApiReservationCalls } from './graphql/apiReservationCalls';
import { ApiContentCalls } from './graphql/apiContentCalls';
import { ConfirmReservationInput } from './requests/confirmReservationInput';
import { BookingChannel } from './requests/bookingChannel';
import { BusinessItemsInput, CreateReservationInput, EmployeeCriteria, type CardRequest, type HotelAvailabilityInput } from './requests';
import { HotelRates } from '../test-data/hotelRates';
import { PaymentOptions } from '../test-data/paymentOptions';
import { Locales } from '../test-data/locales';
import { RoomTypesData } from '../test-data/roomTypes';
import { Strings } from '../test-data/strings';
import { HotelAvailabilitiesInput } from './requests/hotelAvailabilitiesInput';
import type { GuestDetailsModel } from '../test-data/guestDetails';
import type { CardDetails } from '../test-data/cards';
import type { HotelData } from '../test-data/hotels';
import type { SearchCriteria } from '../test-data/searchCriteria';


type ApiObject = Record<string, unknown>;
interface RoomTypeTooltipSummary {
  tooltip: string;
  numberOfRooms: number;
}

interface RoomPanelDetails {
  type: string;
  adultsNumber: string;
  childrenNumber: string;
  meals?: string[];
  [key: string]: unknown;
}

type PackagesAndVatsForReservation = Array<{ packageCode: string; vat: string }>;

interface CreateAndConfirmReservationViaApiParams {
  hotel?: HotelData;
  guestDetails?: GuestDetailsModel;
  stayingNights?: number;
  daysFromToday?: number;
  randomStartDate?: boolean;
  hotelAvailabilityInput?: HotelAvailabilityInput | null;
  ratePlanCode?: string;
  pmsRoomType?: string | string[];
  retriesCount?: number;
  paymentOption?: string;
  card?: CardRequest | CardDetails;
  loggedUser?: boolean;
  charityPackageCode?: string;
  isPiba?: boolean;
  businessItems?: BusinessItemsInput;
}

interface ConfirmReservationViaApiParams {
  reservationInfo?: ReservationInfo;
  hotel?: HotelData;
  paymentOption?: string;
  card?: CardRequest | CardDetails;
  throwErrorIfFail?: boolean;
  charityPackageCode?: string;
  isPiba?: boolean;
  businessItems?: BusinessItemsInput;
}

interface FooterDictionaryValidationData {
  dictionary?: ApiObject;
  apiData?: FooterInformation;
}

/**
 * Methods used to trim/modify responses received for ApiCalls
 */
export class ApiHelpers {
  [key: string]: unknown;

  constructor(data: Record<string, unknown> = {}) {
    const values = Object.values(data);
    const payload = values.length === 1 && values[0] && typeof values[0] === 'object' && !Array.isArray(values[0])
      ? values[0] as Record<string, unknown>
      : data;

    Object.assign(this, payload);
  }

  static fromApiData<T extends Record<string, unknown>>(data: T): ApiHelpers {
    return new ApiHelpers(data);
  }

  private static readonly asObject = asObject;

  private static asBusinessItemsInput(input: unknown): BusinessItemsInput | undefined {
    if (input instanceof BusinessItemsInput) {
      return input;
    }

    if (input && typeof input === 'object' && !Array.isArray(input)) {
      return BusinessItemsInput.fromRequest(input as Record<string, unknown>);
    }

    return undefined;
  }

  /**
   * Sort hotels from hotelAvailabilities call response in order to match the following order: Available hotels, Opening Soon hotels, Sold out hotels
   * @param {Array.<HotelAvailabilities>} hotelAvailabilitiesAllPages list with unsorted available hotels received from hotelAvailabilities call
   * @param {Boolean} sortByDistance when true, sorts hotels ASC by distance (DISTANCE sort); when false, keeps the API's sort order (e.g. RECOMMENDATION sort)
   * @returns {Array.<HotelAvailabilities>} sortedHotelAvailabilitiesAllPages list with available hotels sorted according to requirements
   */
  static async sortHotelAvailabilitiesAllPages(...args: [hotelAvailabilitiesAllPages?: HotelAvailabilities[], sortByDistance?: boolean]): Promise<HotelAvailabilities[]> {
    const hotelAvailabilitiesAllPages = (Array.isArray(args[0]) ? [...args[0]] : []) as HotelAvailabilities[];
    const sortByDistance = typeof args[1] === 'boolean' ? args[1] : true;
    const sortedHotelAvailabilitiesAllPages: HotelAvailabilities[] = [];

    if (hotelAvailabilitiesAllPages.length === 0) {
      return [];
    }

    if (sortByDistance) {
      hotelAvailabilitiesAllPages.sort((hotelCardA, hotelCardB) => {
        const distanceA = Number(ApiHelpers.asObject(hotelCardA.hotelAvailability).distance ?? 0);
        const distanceB = Number(ApiHelpers.asObject(hotelCardB.hotelAvailability).distance ?? 0);
        return distanceA - distanceB;
      });
    }

    const now = Date.now();
    const available: HotelAvailabilities[] = [];
    const openingSoon: HotelAvailabilities[] = [];
    const soldOut: HotelAvailabilities[] = [];

    for (const hotelCard of hotelAvailabilitiesAllPages) {
      const hotelAvailability = ApiHelpers.asObject(hotelCard.hotelAvailability);
      const hotelInformation = ApiHelpers.asObject(hotelCard.hotelInformation);
      const isAvailable = Boolean(hotelAvailability.available);
      const hotelOpeningDate = String(hotelInformation.hotelOpeningDate ?? '');
      const openingDateTs = hotelOpeningDate ? new Date(hotelOpeningDate).getTime() : Number.NaN;

      if (isAvailable && (!hotelOpeningDate || Number.isNaN(openingDateTs) || now > openingDateTs)) {
        available.push(hotelCard);
      } else if (!Number.isNaN(openingDateTs) && now < openingDateTs) {
        openingSoon.push(hotelCard);
      } else {
        soldOut.push(hotelCard);
      }
    }

    sortedHotelAvailabilitiesAllPages.push(...available, ...openingSoon, ...soldOut);
    return sortedHotelAvailabilitiesAllPages;
  }

  /**
   * Calculate total number of pages according to total number of results
   * @param {Object} data object data
   * @param {Number} data.totalResults the total number of results
   * @returns {Number} totalNumberOfPages Total number of pages
   */
  static async calculateTotalSearchResultsPages(...args: [data?: { totalResults?: number }]): Promise<number> {
    const data = ApiHelpers.asObject(args[0]);
    const totalResults = Number(data.totalResults ?? 0);

    if (totalResults > Constants.SEARCH_RESULTS_FIRST_PAGE_SIZE) {
      if (totalResults % 10 === 0) {
        return 1 + (totalResults - Constants.SEARCH_RESULTS_FIRST_PAGE_SIZE) / Constants.SEARCH_RESULTS_FOLLOWING_PAGES_SIZE;
      }
      return 2 + Math.floor((totalResults - Constants.SEARCH_RESULTS_FIRST_PAGE_SIZE) / Constants.SEARCH_RESULTS_FOLLOWING_PAGES_SIZE);
    }

    return 1;
  }

  /**
   * Get facility codes and names for all hotels available for specific search criteria from API response
  * @param {Object} data object data
  * @param {SearchCriteria} data.searchCriteria search criteria object
  * @param {Array.<String>} data.filters selected filters array
   * @param {Boolean} data.isAccessibleRoom true for accessible room, false for non-accessible room
   * @param {Boolean} data.loggedUser - if reservation is created for logged in user
   * @returns {Object} allHotelCardsFacilitiesCodesAndNames Dictionary that contains hotel index and hotel facility codes and names
   */
  static async getFacilitiesCodesAndNamesForAllHotels(...args: [data?: { searchCriteria?: SearchCriteria; filters?: string[]; isAccessibleRoom?: boolean; loggedUser?: boolean }]): Promise<Map<number, Record<string, string>>> {
    const data = ApiHelpers.asObject(args[0]);
    const searchCriteria = data.searchCriteria;
    const filters = Array.isArray(data.filters) ? data.filters : [];
    const isAccessibleRoom = Boolean(data.isAccessibleRoom);
    const loggedUser = Boolean(data.loggedUser ?? false);

    const hotelAvailabilitiesInput = await HotelAvailabilitiesInput.createInputForSearchCriteria(
      searchCriteria as SearchCriteria,
      filters as string[],
    );
    if (isAccessibleRoom) {
      hotelAvailabilitiesInput.type = RoomTypesData.ACCESSIBLE.id;
    }

    // Get expected available hotels for specific search criteria from API call response
    const hotelAvailabilitiesAllPages = await ApiCalls.getHotelAvailabilitiesAllPages({
      hotelAvailabilitiesInput,
      loggedUser
    });

    const sortedHotels = await ApiHelpers.sortHotelAvailabilitiesAllPages(hotelAvailabilitiesAllPages as HotelAvailabilities[]);

    // Extract facility codes and their names for each hotel from API call response
    const allHotelCardsFacilitiesCodesAndNames = new Map<number, Record<string, string>>();

    for (let hotelCardIndex = 0; hotelCardIndex < sortedHotels.length; hotelCardIndex++) {
      const hotelCard = ApiHelpers.asObject(sortedHotels[hotelCardIndex]);
      const hotelInformation = ApiHelpers.asObject(hotelCard.hotelInformation);
      const hotelFacilities = Array.isArray(hotelInformation.hotelFacilities)
        ? (hotelInformation.hotelFacilities as Array<Record<string, unknown>>)
        : [];
      const singleHotelCardCodesAndDescriptions: Record<string, string> = {};
      for (const facility of hotelFacilities) {
        const facilityObj = ApiHelpers.asObject(facility);
        const code = String(facilityObj.code ?? '');
        const name = String(facilityObj.name ?? '');
        singleHotelCardCodesAndDescriptions[code] = name;
      }

      allHotelCardsFacilitiesCodesAndNames.set(hotelCardIndex, singleHotelCardCodesAndDescriptions);
    }

    return allHotelCardsFacilitiesCodesAndNames;
  }

  /**
   * Get the expected number of room cards from the availability response
   * @param {HotelAvailability} hotelAvailabilityResponse hotelAvailabilityResponse
   * @returns {Number} computed number of room cards that are expected in UI
   */
  static async getNumberOfRoomCardsFromAPI(...args: [hotelAvailabilityResponse?: HotelAvailability]): Promise<number> {
    const hotelAvailabilityResponse = ApiHelpers.asObject(args[0]);
    const roomRatesArray = Array.isArray(hotelAvailabilityResponse.roomRates)
      ? hotelAvailabilityResponse.roomRates as Array<Record<string, unknown>>
      : [];
    let maxRoomTypes = 0;

    for (const roomRateItem of roomRatesArray) {
      const roomTypes = Array.isArray(ApiHelpers.asObject(roomRateItem).roomTypes)
        ? ApiHelpers.asObject(roomRateItem).roomTypes as Array<Record<string, unknown>>
        : [];

      for (const roomTypeItem of roomTypes) {
        const rooms = Array.isArray(ApiHelpers.asObject(roomTypeItem).rooms)
          ? ApiHelpers.asObject(roomTypeItem).rooms as Array<Record<string, unknown>>
          : [];
        const roomType = String(ApiHelpers.asObject(roomTypeItem).roomType ?? '');
        if (rooms.length > maxRoomTypes && roomType !== 'DIS') {
          const app = String((global.browser?.options as Record<string, unknown> | undefined)?.app ?? '');
          maxRoomTypes = app === 'ccui'
            ? rooms.filter((room) => room.pmsRoomType !== 'VFMTHR').length
            : rooms.length;
        }
      }
    }

    return maxRoomTypes;
  }

  /**
   * Build the tooltip for the given room type and returns it together with the total number of rooms of that type
   * @param {Object} data object data
   * @param {String} data.roomType roomType
   * @param {HotelInventory} data.hotelInventoryResponse hotelInventoryResponse
   * @param {HotelRoomTypeInformation} data.roomTypeInformationResponse roomTypeInformationResponse
  * @returns {RoomTypeTooltipSummary} tooltip and total number of rooms
   */
  static async buildTooltipWithTotalForRoomType(...args: [data?: { roomType?: string; hotelInventoryResponse?: HotelInventory; roomTypeInformationResponse?: HotelRoomTypeInformation }]): Promise<RoomTypeTooltipSummary> {
    const data = ApiHelpers.asObject(args[0]);
    let roomTypeInformationResponse = ApiHelpers.asObject(data.roomTypeInformationResponse ?? {});
    const roomType = String(data.roomType ?? '');
    const hotelInventoryResponse = ApiHelpers.asObject(data.hotelInventoryResponse ?? {});
    
    if (!roomTypeInformationResponse || !roomTypeInformationResponse.roomTypes) {
      roomTypeInformationResponse = (await ApiContentCalls.graphqlGetRoomTypeInformation()) as Record<string, unknown>;
    }
    
    const doubleRoomsCategories = [
      await Strings.STANDARD_ROOM.name,
      await Strings.BIGGER_ROOM.name,
      await Strings.STANDARD_EXTRA_ROOM.name,
      await Strings.PREMIER_PLUS_ROOM_BASKET.name,
    ];
    const accessibleRoomCategories = ['Behindertengerechtes Zimmer', 'Barrierefreies Zweibettzimmer'];
    const accessibleName = await Strings.ACCESSIBLE.name;
    const doubleOurRoomsName = await Strings.DOUBLE_OUR_ROOMS.name;
    const accessibleDefault = String(Strings.ACCESSIBLE.data.default);
    const roomTypes = Array.isArray(roomTypeInformationResponse.roomTypes) ? roomTypeInformationResponse.roomTypes : [];
    const roomTypeInventories = Array.isArray(hotelInventoryResponse.roomTypeInventories) ? hotelInventoryResponse.roomTypeInventories : [];
    
    let tooltip = '';
    let numberOfRooms = 0;
    
    for (const roomTypeItem of roomTypes) {
      const rtItem = ApiHelpers.asObject(roomTypeItem);
      const roomLabel = String(rtItem.roomLabel ?? '');
      const roomImage = String(rtItem.roomImage ?? '');
      const isMatchingRoomType = (roomLabel.includes(roomType) && (roomType === accessibleName || !roomImage.includes(accessibleDefault)))
        || (roomType === doubleOurRoomsName && doubleRoomsCategories.includes(roomLabel))
        || (roomType === accessibleName && accessibleRoomCategories.includes(roomLabel));
      if (isMatchingRoomType) {
        for (const roomTypeInventoryItem of roomTypeInventories) {
          const invItem = ApiHelpers.asObject(roomTypeInventoryItem);
          const code = String(rtItem.roomTypeCode ?? '');
          if (code.includes(String(invItem.code ?? ''))) {
            const availableCount = Number(invItem.availableCount ?? 0);
            tooltip += `${roomLabel} (${availableCount}) `;
            numberOfRooms += availableCount;
          }
        }
      }
    }
    
    return { tooltip: tooltip.trim(), numberOfRooms: numberOfRooms };
  }

  /**
   * Extract linkColums based on dictionary
  * @param {Object} data object data
  * @param {Object} data.dictionaryTab dictionary tab data
  * @param {String} data.linkOpenNewTab link open in new tab flag
   */
  static async extractLinkColumnsFromTab(...args: [data?: { dictionaryTab?: ApiObject; linkOpenNewTab?: string }]): Promise<Array<ApiObject>> {
    const data = ApiHelpers.asObject(args[0]);
    const dictionaryTab = ApiHelpers.asObject(data.dictionaryTab);
    const linkOpenNewTab = String(data.linkOpenNewTab ?? '');
    const tabTitle = String(dictionaryTab.tabTitle ?? '');
    const linkColumns = Array.isArray(dictionaryTab.linkColumns)
      ? dictionaryTab.linkColumns as Array<Record<string, unknown>>
      : [];

    const linkColumnArray: Array<ApiObject> = [];
    for (const [linkColumnIndex, linkColumn] of linkColumns.entries()) {
      const linkColumTitle = String(ApiHelpers.asObject(linkColumn).columnTitle ?? '');
      const linkItems = Array.isArray(ApiHelpers.asObject(linkColumn).linkItems)
        ? ApiHelpers.asObject(linkColumn).linkItems as Array<Record<string, unknown>>
        : [];
      for (const linkItem of linkItems) {
        if (String(linkItem.linkOpenNewTab ?? '') === linkOpenNewTab) {
          linkColumnArray.push({ tabTitle, columnTitle: linkColumTitle, columnNumber: linkColumnIndex, ...linkItem });
        }
      }
    }
    return linkColumnArray;
  }

  /**
   * Get package codes and vats applied for the reservation codes contained in basket details
   * @param {Basket} basketDetails basket details 
   * @param {String} hotelId hotel id
   * @param {String} arrivalDate arrival date
   * @returns {Object} data object containg a list with package codes and vats applied for the reservation codes contained in basket details
   */
  static async getPackagesAndVatsForReservation(...args: [basketDetails?: Basket, hotelId?: string, arrivalDate?: string]): Promise<PackagesAndVatsForReservation> {
    const basketDetails = ApiHelpers.asObject(args[0]);
    const hotelId = String(args[1] ?? '');
    const arrivalDate = String(args[2] ?? '');

    const packageCodesAndVats: PackagesAndVatsForReservation = [];
    const basketItems = Array.isArray(basketDetails.items) ? (basketDetails.items as Array<Record<string, unknown>>) : [];

    for (const item of basketItems) {
      const itemObj = ApiHelpers.asObject(item);
      const sourceId = String(itemObj.sourceId ?? '');
      const paymentDetails = ApiHelpers.asObject(await OhipApiCalls.getHotelReservationPaymentInfo(hotelId, sourceId, arrivalDate));
      const detail = ApiHelpers.asObject(paymentDetails.detail);
      const packages = Array.isArray(detail.packages) ? (detail.packages as Array<Record<string, unknown>>) : [];

      if (packages.length > 0) {
        for (const pkg of packages) {
          const pkgObj = ApiHelpers.asObject(pkg);
          const packageCode = String(pkgObj.code ?? '');
          const taxes = ApiHelpers.asObject(pkgObj.taxes);
          let currentVat = '';

          if (Object.keys(taxes).length > 0) {
            const taxArray = Array.isArray(taxes.tax) ? (taxes.tax as Array<Record<string, unknown>>) : [];
            if (taxArray.length > 0) {
              const taxObj = ApiHelpers.asObject(taxArray[0]);
              const taxDescription = String(taxObj.description ?? '');
              const vatStart = taxDescription.indexOf('VAT') + 4;
              const vatEnd = taxDescription.indexOf('%');
              currentVat = taxDescription.slice(vatStart, vatEnd);
            }
          }

          const packageInfo = { packageCode, vat: currentVat };
          if (!packageCodesAndVats.some((item) => item.packageCode === packageCode)) {
            packageCodesAndVats.push(packageInfo);
          }
        }
      }

      const revenue = ApiHelpers.asObject(detail.revenue);
      const revenueTaxes = ApiHelpers.asObject(revenue.taxes);
      if (Object.keys(revenueTaxes).length > 0) {
        const taxArray = Array.isArray(revenueTaxes.tax) ? (revenueTaxes.tax as Array<Record<string, unknown>>) : [];
        if (taxArray.length > 0) {
          const taxObj = ApiHelpers.asObject(taxArray[0]);
          const taxDescription = String(taxObj.description ?? '');
          const vatStart = taxDescription.indexOf('VAT') + 4;
          const vatEnd = taxDescription.indexOf('%');
          const currentVat = taxDescription.slice(vatStart, vatEnd);

          const packageInfo = { packageCode: '__ACCMOD__', vat: currentVat };
          if (!packageCodesAndVats.some((item) => item.packageCode === '__ACCMOD__')) {
            packageCodesAndVats.push(packageInfo);
          }
        }
      }
    }

    return packageCodesAndVats;
  }

  /**
   * Get month name from dictionary
   * @param {Object} data object data
   * @param {Number} data.monthIndex month index
   * @param {Boolean} data.returnMonthInShortFormat true for month name in short format, false for month name in long format
   * @returns {String} month name in short or long format
   */
  static async getMonthNameFromDictionary(...args: [data?: { monthIndex?: number; returnMonthInShortFormat?: boolean }]): Promise<string> {
    const data = ApiHelpers.asObject(args[0]);
    const monthIndex = Number(data.monthIndex ?? 0);
    const returnMonthInShortFormat = Boolean(data.returnMonthInShortFormat);
    const labelsDictionary = await ApiDictionary.fetchLabelsDictionary();
    const key = returnMonthInShortFormat ? `common.month${monthIndex}.short` : `common.month${monthIndex}`;
    return String(labelsDictionary[key] ?? '');
  }

  /**
   * Get week day name from dictionary
   * @param {Object} data object data
   * @param {Number} data.weekIndex week index
   * @param {Boolean} data.returnWeekInShortFormat true for week name in short format, false for week name in long format
   * @returns {String} weekday name in short or long format
   */
  static async getWeekDayNameFromDictionary(...args: [data?: { weekIndex?: number; returnWeekInShortFormat?: boolean }]): Promise<string> {
    const data = ApiHelpers.asObject(args[0]);
    const weekIndex = Number(data.weekIndex ?? 0);
    const returnWeekInShortFormat = Boolean(data.returnWeekInShortFormat);
    const labelsDictionary = await ApiDictionary.fetchLabelsDictionary();
    const weekUpdatedIndex = weekIndex === 0 ? 6 : weekIndex - 1;
    const key = returnWeekInShortFormat ? `common.weekday${weekUpdatedIndex}` : `common.weekday.full${weekUpdatedIndex}`;
    return String(labelsDictionary[key] ?? '');
  }

  /**
   * Returns Adult meals names
   * @param {Object} reservationInfo reservation info
   * @returns {Array<AncillariesMealNames>} filtered array of adult meals name
   */
  static async getAdultMealsNamesFromPackagesCall(...args: [reservationInfo?: ReservationInfo]): Promise<Array<string>> {
    const reservationInfo = ApiHelpers.asObject(args[0]);
    const reservationDetails = ApiHelpers.asObject(reservationInfo.reservationDetails);
    const hotelId = String(reservationDetails.hotelId ?? '');
    const bookingFlowId = String(reservationInfo.bookingFlowId ?? '');
    const basketReference = String(reservationDetails.basketReference ?? '');

    const mealList = await ApiCalls.graphqlGetAncillariesAdultMeals({
      hotelId,
      bookingFlowId,
      basketReferenceId: basketReference
    });

    const reservationMeals: Array<string> = [];
    const meals = Array.isArray(mealList) ? (mealList as Array<Record<string, unknown>>) : [];
    for (const meal of meals) {
      const mealObj = ApiHelpers.asObject(meal);
      reservationMeals.push(String(mealObj.name ?? ''));
    }

    return reservationMeals;
  }

  /**
   * Get the Discover Premier Inn Elements text from Api Dictionary
   * @param {String} language language
   * @param {String} country country
   */
  static async getDiscoverPIElementsFromDictionary(...args: [language?: string, country?: string]): Promise<Array<string>> {
    const configuredLocale = String((global.browser?.options as Record<string, unknown> | undefined)?.locale ?? 'gb-en');
    const locale = Locales.getLocaleByString(configuredLocale);
    const language = String(args[0] ?? locale.language);
    const country = String(args[1] ?? locale.country);

    const headerDictionary = ApiHelpers.asObject(await ApiDictionary.fetchGenericDictionary(language, country));
    const content = ApiHelpers.asObject(headerDictionary.content);
    const subNav = Array.isArray(content.subNav) ? (content.subNav as Array<Record<string, unknown>>) : [];
    const headerElements: Array<string> = [];

    for (const element of subNav) {
      const elementObj = ApiHelpers.asObject(element);
      const elementTitle = String(elementObj.title ?? '').trim();
      if (elementTitle && !elementTitle.includes('Business')) {
        headerElements.push(elementTitle);
      }

      const navOptions = Array.isArray(elementObj.navOptions) ? (elementObj.navOptions as Array<Record<string, unknown>>) : [];
      if (navOptions.length > 0) {
        for (const navOption of navOptions) {
          const navObj = ApiHelpers.asObject(navOption);
          const navOptionTitle = String(navObj.title ?? '').trim();
          const navUrl = String(navObj.url ?? '');
          if (navOptionTitle && !navOptionTitle.includes('Business') && !navUrl.includes('business') && !navUrl.includes('premiermeeting')) {
            headerElements.push(navOptionTitle);
          }
        }
      }
    }

    return headerElements;
  }

  /**
   * Get the Hotels list for Destination landing page (DLP) from Api Dictionary
   * @param {String} dlpPath dlpPath
   * @returns {Array<String>} list of hotel names
   */
  static async getDlpHotelsListFromDictionary(...args: [data?: { dlpPath?: string }]): Promise<Array<string>> {
    const data = ApiHelpers.asObject(args[0]);
    const dlpPath = String(data.dlpPath ?? '');

    console.log('Get hotels list for Destination landing page from Dictionary');
    const hotelListDictionary = ApiHelpers.asObject(await ApiDictionary.fetchDlpContentServiceDictionary({ dlpPath }));
    const hotelList: Array<string> = [];
    const hotels = Array.isArray(hotelListDictionary.hotels) ? (hotelListDictionary.hotels as Array<Record<string, unknown>>) : [];

    for (const element of hotels) {
      try {
        const elementObj = ApiHelpers.asObject(element);
        const hotelCode = String(elementObj.code ?? '');
        const hotelData = ApiHelpers.asObject(await ApiDictionary.fetchHotelDirectoryDictionary({ hotelId: hotelCode }));
        hotelList.push(String(hotelData.name ?? ''));
      } catch (error) {
        const elementObj = ApiHelpers.asObject(element);
        const hotelCode = String(elementObj.code ?? '');
        console.warn(`Hotel with code ${hotelCode} not found in hotel directory dictionary`);
      }
    }

    return hotelList;
  }

  /**
   * Validate footer section AEM dictionary versus API
   * @param {Object} Object containing AEM dictionary and API data
   * @param {Object} Object.dictionary AEM dictionary entry
   * @param {Object} Object.apiData Global footer API response
   */
  static async validateAemFooterDictionaryAgainstGlobalFooterAPIResponse(...args: [data?: FooterDictionaryValidationData]): Promise<void> {
    const data = ApiHelpers.asObject(args[0]);
    const dictionary = ApiHelpers.asObject(data.dictionary);
    const apiData = ApiHelpers.asObject(data.apiData);

    console.log('Validate Global Footer Dictionary against API');

    // Validate Copyright info
    console.log('Validate Copyright info');
    if (String(dictionary.copyright ?? '') !== String(apiData.copyrightInfo ?? '')) {
      throw new Error('Copyright info does not match!');
    }

    // Validate Social Links
    console.log('Validate Social Links');
    const socialLinks = Array.isArray(dictionary.socialLinks) ? (dictionary.socialLinks as Array<Record<string, unknown>>) : [];
    const socialMediaIcons = Array.isArray(apiData.socialMediaIcons) ? (apiData.socialMediaIcons as Array<Record<string, unknown>>) : [];
    for (const [index, socialLinkItem] of socialLinks.entries()) {
      const socialLink = ApiHelpers.asObject(socialLinkItem);
      const socialIcon = ApiHelpers.asObject(socialMediaIcons[index] ?? {});
      if (String(socialLink.linkUrl ?? '') !== String(socialIcon.linkSrc ?? '')) {
        throw new Error('LinkURL does not match!');
      }
      if (String(socialLink.iconUrl ?? '') !== String(socialIcon.iconSrc ?? '')) {
        throw new Error('IconSrc does not match!');
      }
      if (String(socialLink.iconText ?? '') !== String(socialIcon.label ?? '')) {
        throw new Error('Label does not match!');
      }
    }

    // Validate Footer Tabs
    console.log('Validate Footer Tabs');
    const linkTabs = Array.isArray(dictionary.linkTabs) ? (dictionary.linkTabs as Array<Record<string, unknown>>) : [];
    const tabs = Array.isArray(apiData.tabs) ? (apiData.tabs as Array<Record<string, unknown>>) : [];
    for (const [linkTabIndex, linkTabItem] of linkTabs.entries()) {
      const linkTab = ApiHelpers.asObject(linkTabItem);
      const tab = ApiHelpers.asObject(tabs[linkTabIndex] ?? {});
      const intro = ApiHelpers.asObject(tab.intro);
      const apiColumns = Array.isArray(tab.columns) ? (tab.columns as Array<Record<string, unknown>>) : [];

      if (String(linkTab.introDescription ?? '') !== String(intro.description ?? '')) {
        throw new Error('Description does not match!');
      }
      if (String(linkTab.introTitle ?? '') !== String(intro.name ?? '')) {
        throw new Error('Intro Title does not match!');
      }
      if (String(linkTab.tabTitle ?? '') !== String(tab.name ?? '')) {
        throw new Error('Description does not match!');
      }

      const linkColumns = Array.isArray(linkTab.linkColumns) ? (linkTab.linkColumns as Array<Record<string, unknown>>) : [];
      for (const [columnIndex, linkColumn] of linkColumns.entries()) {
        const column = ApiHelpers.asObject(linkColumn);
        const apiColumn = ApiHelpers.asObject(apiColumns[columnIndex] ?? {});
        if (String(column.columnTitle ?? '') !== String(apiColumn.name ?? '')) {
          throw new Error('Column title does not match');
        }

        const linkItems = Array.isArray(column.linkItems) ? (column.linkItems as Array<Record<string, unknown>>) : [];
        const apiLinkItems = Array.isArray(apiColumn.linkItems) ? (apiColumn.linkItems as Array<Record<string, unknown>>) : [];
        for (const [linkItemIndex, linkItem] of linkItems.entries()) {
          const item = ApiHelpers.asObject(linkItem);
          const apiItem = ApiHelpers.asObject(apiLinkItems[linkItemIndex] ?? {});
          if (String(item.linkText ?? '') !== String(apiItem.name ?? '')) {
            throw new Error('Link text does not match!');
          }
          if (String(item.linkPath ?? '') !== String(apiItem.linkSrc ?? '')) {
            throw new Error('Link path does not match!');
          }
          if (String(item.linkOpenNewTab ?? '') !== String(apiItem.openInNewTab ?? '')) {
            throw new Error('Link openNewTab does not match!');
          }
        }
      }
    }
  }

  /**
   * check if the entries on AEM and Experience API match
  * @param {Object} aemImportantInfo - JSON extracted from the AEM dictionary
  * @param {HotelImportantInformation} graphqlImportantInformation - important-information API response
   */
  static async validateDataAEMvsGraphQL(...args: [aemImportantInfo?: ApiObject, graphqlImportantInformation?: HotelImportantInformation]): Promise<void> {
    const aemImportantInfo = ApiHelpers.asObject(args[0]);
    const graphqlImportantInformation = ApiHelpers.asObject(args[1]);

    const aemTitle = String(aemImportantInfo.title ?? '');
    const graphqlTitle = String(graphqlImportantInformation.title ?? '');
    if (aemTitle !== graphqlTitle) {
      throw new Error(`Title doesn't match. AEM: "${aemTitle}", GraphQL: "${graphqlTitle}"`);
    }

    const aemInfoItems = Array.isArray(aemImportantInfo.infoItems) ? (aemImportantInfo.infoItems as Array<Record<string, unknown>>) : [];
    if (aemInfoItems.length === 0) {
      throw new Error('AEM important information list is empty.');
    }

    const graphqlInfoItems = Array.isArray(graphqlImportantInformation.infoItems) ? (graphqlImportantInformation.infoItems as Array<Record<string, unknown>>) : [];
    for (const [index, infoItem] of aemInfoItems.entries()) {
      const aemItem = ApiHelpers.asObject(infoItem);
      const graphqlItem = ApiHelpers.asObject(graphqlInfoItems[index] ?? {});

      const aemEndDate = String(aemItem.endDate ?? '');
      const graphqlEndDate = String(graphqlItem.endDate ?? '');
      if (aemEndDate !== graphqlEndDate) {
        throw new Error(`Item ${index} endDate doesn't match. AEM: "${aemEndDate}", GraphQL: "${graphqlEndDate}"`);
      }

      const aemPriority = String(aemItem.priority ?? '');
      const graphqlPriority = String(graphqlItem.priority ?? '');
      if (aemPriority !== graphqlPriority) {
        throw new Error(`Item ${index} priority doesn't match. AEM: "${aemPriority}", GraphQL: "${graphqlPriority}"`);
      }

      const aemStartDate = String(aemItem.startDate ?? '');
      const graphqlStartDate = String(graphqlItem.startDate ?? '');
      if (aemStartDate !== graphqlStartDate) {
        throw new Error(`Item ${index} startDate doesn't match. AEM: "${aemStartDate}", GraphQL: "${graphqlStartDate}"`);
      }

      const aemText = String(aemItem.text ?? '');
      const graphqlText = String(graphqlItem.text ?? '');
      if (aemText !== graphqlText) {
        throw new Error(`Item ${index} text doesn't match. AEM: "${aemText}", GraphQL: "${graphqlText}"`);
      }
    }
  }

  /**
   * Get the name of the meal that was preselected on a logged user account
   * @param {Object} data object data
   * @param {String} data.userProfileEmail user email
   * @param {String} data.hotelID hotel id
   * @returns {String} the name o a meal preselected for user accounts
   */
  static async getLoggedUserPreselectedMeal(...args: [data?: { userProfileEmail?: string; hotelID?: string }]): Promise<string> {
    const data = ApiHelpers.asObject(args[0]);
    const userProfileEmail = String(data.userProfileEmail ?? '');
    const hotelId = String(data.hotelID ?? '');

    const upsellItemsDictionary = ApiHelpers.asObject(await ApiDictionary.fetchHotelDirectoryDictionary({ hotelId }));
    const upsellItems = Array.isArray(upsellItemsDictionary.upsellItems) ? (upsellItemsDictionary.upsellItems as Array<Record<string, unknown>>) : [];

    const userProfileDetails = ApiHelpers.asObject(await ApiCalls.getUserProfileDetails({ emailAddress: userProfileEmail }));
    const bookingPreference = ApiHelpers.asObject(userProfileDetails.bookingPreference);
    const foodPreference = ApiHelpers.asObject(bookingPreference.foodPreference);
    const preferenceCode = String(foodPreference ?? '');

    const userFoodPreference = upsellItems.filter((meal) => {
      const mealObj = ApiHelpers.asObject(meal);
      const mealCode = String(mealObj.code ?? '');
      return mealCode === preferenceCode;
    });

    if (userFoodPreference.length > 0) {
      const meal = ApiHelpers.asObject(userFoodPreference[0]);
      const mealName = String(meal.name ?? '');
      return mealName.replace(/!/g, '').trim();
    }

    return '';
  }

  /**
   * Validate VAT rule from Api response against Prepayment rules
   * @param {Hotel} hotel hotel data object
   * @param {String} packageCode package code
   * @param {Array<Object>} prepaymentDetailsList list of prepayment objects
   */
  static async validateVatRuleForPackageCode(...args: [hotel?: ApiObject, packageCode?: string, prepaymentDetailsList?: ApiObject[]]): Promise<void> {
    const hotel = ApiHelpers.asObject(args[0]);
    const packageCode = String(args[1] ?? '');
    const prepaymentDetailsList = Array.isArray(args[2]) ? (args[2] as Array<Record<string, unknown>>) : [];

    let vatRuleCountryCode: string;
    const countryCode = String(hotel.countryCode ?? '');

    switch (countryCode) {
      case Constants.UK_COUNTRY_CODE:
        vatRuleCountryCode = 'UK';
        break;
      case Constants.GERMANY_COUNTRY_CODE:
        vatRuleCountryCode = 'DE';
        break;
      default:
        vatRuleCountryCode = countryCode.toUpperCase();
    }

    console.log(`Validate VAT rules for packageCode ${packageCode} and countryCode: ${vatRuleCountryCode}`);

    const vatRulesResponse = ApiHelpers.asObject(await EntityApiCalls.getVatRules(vatRuleCountryCode, packageCode));
    const vatTranCodes = Array.isArray(vatRulesResponse.tranCodes) ? (vatRulesResponse.tranCodes as Array<Record<string, unknown>>) : [];

    for (const vatCode of vatTranCodes) {
      const vat = ApiHelpers.asObject(vatCode);
      const tranCode = String(vat.tranCode ?? '');
      const pkgCode = String(vat.pkgCode ?? '');

      const matchingRules = prepaymentDetailsList.filter((item) => {
        const prepItem = ApiHelpers.asObject(item);
        return String(prepItem.TXN_Code ?? '') === tranCode && String(prepItem.PKG_Code ?? '') === pkgCode;
      });

      if (matchingRules.length === 0) {
        throw new Error('Prepayment rules do not match with VAT code details received from API response');
      }
    }
  }

  /**
   * Validate the booking is still active
   * @param {Object} data object data
   * @param {Object} data.reservation reservation details
   * @param {Object} data.bookingReference booking reference
   * @param {Hotel} data.hotel hotel details
   * @param {Boolean} data.isBookingActive booking status
   * @param {BookingChannel} data.bookingChannel booking status
   */
  static async validateBookingCancellableStatusViaApi(...args: [data?: { reservation?: ReservationInfo; bookingReference?: string; hotel?: HotelData; isBookingActive?: boolean; bookingChannel?: BookingChannel }]): Promise<void> {
    const data = ApiHelpers.asObject(args[0] ?? {}) as { reservation?: ReservationInfo; bookingReference?: string; hotel?: HotelData; isBookingActive?: boolean; bookingChannel?: BookingChannel };
    const reservation = ApiHelpers.asObject(data.reservation ?? {});
    const bookingReference = String(data.bookingReference ?? '');
    const hotel = data.hotel;
    if (!hotel) {
      throw new Error('hotel is required to validate booking cancellability via API.');
    }
    const isBookingActive = Boolean(data.isBookingActive ?? true);
    const bookingChannel = data.bookingChannel ?? new BookingChannel();

    console.log(`Validate the booking is ${isBookingActive === true ? 'still active' : 'no longer active'}`);

    const guestDetails = ApiHelpers.asObject(reservation.guestDetails ?? {});
    const booker = ApiHelpers.asObject(guestDetails.booker ?? {});
    const reservationDetails = ApiHelpers.asObject(reservation.reservationDetails ?? {});
    const reservations = Array.isArray(reservationDetails.reservations) ? reservationDetails.reservations : [];
    const firstReservation = ApiHelpers.asObject(reservations[0] ?? {});
    const roomStay = ApiHelpers.asObject(firstReservation.roomStay ?? {});

    const findBookingResp = await ApiCalls.graphqlFindBooking({
      basketReference: bookingReference,
      lastName: String(booker.lastName ?? ''),
      arrivalDate: String(roomStay.arrivalDate ?? '')
    });
    
    const basketReference = String(reservationDetails.basketReference ?? '');
    const hotelId = String(hotel.id ?? '');
    const token = String(findBookingResp.token ?? '');
    
    const manageBookingResp = await ApiCalls.graphqlManageBooking({
      basketReference,
      hotelId,
      token,
      bookingChannel
    });
    
    const isCancellable = Boolean(manageBookingResp.isCancellable);
    if (isCancellable !== isBookingActive) {
      throw new Error(`The booking is ${isBookingActive ? 'no longer active' : 'still active'}`);
    }
  }

  /**
   * Validate VATs applied to reservation
   * @param {Object} packagesAndVats data object
   * @param {Hotel} hotel data object
   * @param {Array<Object>} prepaymentDetailsList list of prepayment objects
   */
  static async validateVATsAppliedToReservation(...args: [packagesAndVats?: ApiObject, hotel?: ApiObject, prepaymentDetailsList?: ApiObject[]]): Promise<void> {
    const packagesAndVats = ApiHelpers.asObject(args[0] ?? {});
    const hotel = ApiHelpers.asObject(args[1] ?? {});
    const prepaymentDetailsList = Array.isArray(args[2]) ? args[2] : [];

    const packages = ApiHelpers.asObject(packagesAndVats.packages ?? {});
    const vats = ApiHelpers.asObject(packagesAndVats.vats ?? {});
    const hotelVats = ApiHelpers.asObject(vats[String(hotel.id ?? '')] ?? {});

    for (const prepaymentDetails of prepaymentDetailsList) {
      const details = ApiHelpers.asObject(prepaymentDetails);
      const prepaymentCode = String(details.prepaymentCode ?? '');
      const expectedVat = Number(hotelVats[prepaymentCode] ?? 0);
      const actualVat = Number(details.expectedVat ?? 0);
      
      if (expectedVat !== actualVat) {
        throw new Error(`VAT for prepayment code ${prepaymentCode} does not match. Expected: ${expectedVat}, Actual: ${actualVat}`);
      }
    }
  }

  /**
   * get Number of stay nights based on Api response of Get booking information
   * @param {BookingInformation} bookingInformation response from get Booking information Api call
   * @returns {Number} number of stay nights
   */
  static async getNumberOfStayNightsBasedOnBookingInformation(...args: [bookingInformation?: BookingInformation]): Promise<number> {
    console.log('Calculate number of stay nights based on get booking information api response');
    const bookingInformation = ApiHelpers.asObject(args[0]);
    const reservationByIdList = Array.isArray(bookingInformation.reservationByIdList)
      ? (bookingInformation.reservationByIdList as Array<Record<string, unknown>>)
      : [];
    
    if (reservationByIdList.length === 0) {
      return 0;
    }

    const reservation = ApiHelpers.asObject(reservationByIdList[0]);
    const roomStay = ApiHelpers.asObject(reservation.roomStay);
    const arrival = new Date(String(roomStay.arrivalDate ?? ''));
    const departure = new Date(String(roomStay.departureDate ?? ''));
    
    if (Number.isNaN(arrival.getTime()) || Number.isNaN(departure.getTime())) {
      return 0;
    }

    return Math.round((departure.getTime() - arrival.getTime()) / (1000 * 60 * 60 * 24));
  }

  /**
   * Get initiator email based on Api response of get pay application details
   * @param {ApplicationDetails} applicationDetailsResponse response from get pay application details Api call
   * @returns {String} email of the app initiator
   */
  static async getInitiatorEmail(...args: [applicationDetailsResponse?: ApplicationDetails]): Promise<string> {
    console.log('Get initiator email from application details response');
    const applicationDetailsResponse = ApiHelpers.asObject(args[0]);
    const participants = Array.isArray(applicationDetailsResponse.participants)
      ? (applicationDetailsResponse.participants as Array<Record<string, unknown>>)
      : [];
    
    for (const participant of participants) {
      const participantObj = ApiHelpers.asObject(participant);
      if (participantObj.initiator === true) {
        return String(participantObj.email ?? '');
      }
    }
    
    return '';
  }

  /**
   * Format room details to match format of expected values from Ui
   * @param {Object} room object with values from get booking information api response
   * @returns {Object} room with formatted values
   */
  static async formatRoomDetailsFromApi(...args: [room?: Partial<RoomPanelDetails> & ApiObject]): Promise<RoomPanelDetails> {
    const room = ApiHelpers.asObject(args[0]) as Partial<RoomPanelDetails> & ApiObject;
    console.log('Format adults and children details to match expected format in UI');

    const adultsNumber = Number(room.adultsNumber ?? 0);
    const childrenNumber = Number(room.childrenNumber ?? 0);

    if (adultsNumber === 0) {
      throw new Error('No adult booked for this reservation');
    }

    room.adultsNumber = adultsNumber === 1 ? `${adultsNumber} Adult` : `${adultsNumber} Adults`;

    if (childrenNumber === 0) {
      room.childrenNumber = '';
    } else {
      room.childrenNumber = childrenNumber === 1 ? `${childrenNumber} Child` : `${childrenNumber} Children`;
    }

    return room as RoomPanelDetails;
  }

  /**
   * Extract rooms per reservation from API responses based on Ui expected format
   * @param {BookingInformation} bookingInformation bookingInformation
   * @param {String} basketReferenceId basketReferenceId
   * @returns {Array.<Object>} array with detailed room informations
   */
  static async getRoomPanelDetailsBasedOnApi(...args: [bookingInformation?: BookingInformation, basketReferenceId?: string]): Promise<RoomPanelDetails[]> {
    const bookingInformation = ApiHelpers.asObject(args[0]);
    const basketReferenceId = String(args[1] ?? '');

    console.log('Retrieve room details from get booking information and get packages api responses');

    const hotelId = String(bookingInformation.hotelId ?? '');
    const bookingFlowId = String(bookingInformation.bookingFlowId ?? '');
    const numberOfNights = await ApiHelpers.getNumberOfStayNightsBasedOnBookingInformation(bookingInformation);

    const reservationByIdList = Array.isArray(bookingInformation.reservationByIdList)
      ? (bookingInformation.reservationByIdList as Array<Record<string, unknown>>)
      : [];

    const firstReservation = ApiHelpers.asObject(reservationByIdList[0] ?? {});
    const roomStay = ApiHelpers.asObject(firstReservation.roomStay);
    const startDate = new Date(String(roomStay.arrivalDate ?? ''));
    const endDate = new Date(String(roomStay.departureDate ?? ''));

    const mealPackagesData = await ApiCalls.graphqlGetMealsPackages({
      hotelId,
      basketReferenceId,
      nightsNumber: numberOfNights,
      startDate,
      endDate,
      bookingFlowId
    });
    const mealPackages = MealPackages.fromResponse(mealPackagesData as any);

    const roomPanelListBasedOnApi: RoomPanelDetails[] = [];
    for (let i = 0; i < reservationByIdList.length; i++) {
      const reservation = ApiHelpers.asObject(reservationByIdList[i]);
      const resRoomStay = ApiHelpers.asObject(reservation.roomStay);
      const roomExtraInfo = ApiHelpers.asObject(resRoomStay.roomExtraInfo);

      let room: RoomPanelDetails = {
        type: String(roomExtraInfo.roomName ?? ''),
        adultsNumber: String(resRoomStay.adultsNumber ?? 0),
        childrenNumber: String(resRoomStay.childrenNumber ?? 0),
      };

      room = await ApiHelpers.formatRoomDetailsFromApi(room);
      const meals = await ApiHelpers.getMealsPerRoomForBookingSummaryByRoomIndex(i, mealPackages);
      room.meals = meals;
      roomPanelListBasedOnApi.push(room);
    }

    return roomPanelListBasedOnApi.sort((room1, room2) => {
      const type1 = Number(room1.type ?? 0);
      const type2 = Number(room2.type ?? 0);
      return type1 - type2;
    });
  }

  /**
   * Extract meals per room from API based on Ui expected format for Booking summary
   * @param {Number} roomIndex roomIndex
   * @param {MealPackages} mealPackages response retrieved by get packages api request
   * @returns {Array.<Object>} list of rooms with details
   */
  static async getMealsPerRoomForBookingSummaryByRoomIndex(...args: [roomIndex?: number, mealPackages?: MealPackages]): Promise<Array<string>> {
    const roomIndex = Number(args[0] ?? 0);
    const mealPackages = ApiHelpers.asObject(args[1]);

    console.log('Retrieve meals per room details from get packages api respose');

    const roomSelection = Array.isArray(mealPackages.roomSelection) ? (mealPackages.roomSelection as Array<Record<string, unknown>>) : [];
    const selectedRoom = ApiHelpers.asObject(roomSelection[roomIndex] ?? {});
    const packagesSelection = Array.isArray(selectedRoom.packagesSelection)
      ? (selectedRoom.packagesSelection as Array<Record<string, unknown>>)
      : [];

    const mealsPerRoom = packagesSelection.filter((meal) => {
      const mealObj = ApiHelpers.asObject(meal);
      return String(mealObj.id ?? '') !== 'CITYTAX';
    });

    const detailedMealsPerRoom: Array<string> = [];

    if (mealsPerRoom.length === 0) {
      detailedMealsPerRoom.push('No Meals Selected');
    } else {
      const adultMeals = Array.isArray(mealPackages.adultMeals) ? (mealPackages.adultMeals as Array<Record<string, unknown>>) : [];
      const childMeals = Array.isArray(mealPackages.childMeals) ? (mealPackages.childMeals as Array<Record<string, unknown>>) : [];

      for (const meal of mealsPerRoom) {
        const mealObj = ApiHelpers.asObject(meal);
        const mealId = String(mealObj.id ?? '');
        const noOfSelections = Number(mealObj.noOfSelections ?? 0);

        const filteredAdultMeals = adultMeals.filter((m) => String(ApiHelpers.asObject(m).id ?? '') === mealId);
        const filteredChildMeals = childMeals.filter((m) => String(ApiHelpers.asObject(m).id ?? '') === mealId);

        if (filteredAdultMeals.length + filteredChildMeals.length !== 1) {
          throw new Error(`Meal packages with id ${mealId} for this hotel are not unique`);
        }

        if (filteredAdultMeals.length > 0) {
          const adultMeal = ApiHelpers.asObject(filteredAdultMeals[0]);
          const mealName = String(adultMeal.name ?? '');
          const label = noOfSelections === 1 ? 'Adult' : 'Adults';
          detailedMealsPerRoom.push(`${mealName} for ${noOfSelections} ${label}`);
        } else if (filteredChildMeals.length > 0) {
          const childMeal = ApiHelpers.asObject(filteredChildMeals[0]);
          const mealName = String(childMeal.name ?? '');
          const label = noOfSelections === 1 ? 'Child' : 'Children';
          detailedMealsPerRoom.push(`${mealName} for ${noOfSelections} ${label}`);
        }
      }
    }

    return detailedMealsPerRoom.sort();
  }

  /**
   * Extract meals per room from API based on Ui expected format for room details
   * @param {Number} roomIndex roomIndex
   * @param {MealPackages} mealPackages response retrieved by get packages api request
   * @returns {Array.<Object>} list of meal details for room
   */
  static async getMealsPerRoomForRoomDetailsByRoomIndex(...args: [roomIndex?: number, mealPackages?: MealPackages]): Promise<Array<string>> {
    const roomIndex = Number(args[0] ?? 0);
    const mealPackages = ApiHelpers.asObject(args[1]);

    console.log('Retrieve meals per room in Room Details expected format from get packages api response');

    const excludedPackageCodes = ['CITYTAX', 'ZCHRY1', 'ZCHRY2', 'ZCHRY3', 'ZCHRY4', 'ZCHRY8', 'PROMOFREE', 'HSATWN', 'HSCKIN', 'HSCOU2'];
    const roomSelection = Array.isArray(mealPackages.roomSelection) ? (mealPackages.roomSelection as Array<Record<string, unknown>>) : [];
    const selectedRoom = ApiHelpers.asObject(roomSelection[roomIndex] ?? {});
    const packagesSelection = Array.isArray(selectedRoom.packagesSelection)
      ? (selectedRoom.packagesSelection as Array<Record<string, unknown>>)
      : [];

    const mealsPerRoom = packagesSelection.filter((meal) => {
      const mealObj = ApiHelpers.asObject(meal);
      const mealId = String(mealObj.id ?? '');
      return !excludedPackageCodes.includes(mealId);
    });

    const detailedMealsPerRoom: Array<string> = [];

    if (mealsPerRoom.length !== 0) {
      const adultMeals = Array.isArray(mealPackages.adultMeals) ? (mealPackages.adultMeals as Array<Record<string, unknown>>) : [];
      const childMeals = Array.isArray(mealPackages.childMeals) ? (mealPackages.childMeals as Array<Record<string, unknown>>) : [];

      for (const meal of mealsPerRoom) {
        const mealObj = ApiHelpers.asObject(meal);
        const mealId = String(mealObj.id ?? '');
        const noOfSelections = Number(mealObj.noOfSelections ?? 0);

        const filteredAdultMeals = adultMeals.filter((m) => String(ApiHelpers.asObject(m).id ?? '') === mealId);
        const filteredChildMeals = childMeals.filter((m) => String(ApiHelpers.asObject(m).id ?? '') === mealId);

        if (filteredAdultMeals.length + filteredChildMeals.length !== 1) {
          throw new Error(`Meal packages with id ${mealId} for this hotel are not unique`);
        }

        if (filteredAdultMeals.length > 0) {
          const adultMeal = ApiHelpers.asObject(filteredAdultMeals[0]);
          const mealName = String(adultMeal.name ?? '').trim();
          const label = noOfSelections === 1 ? 'Adult' : 'Adults';
          detailedMealsPerRoom.push(`${noOfSelections} ${label} ${mealName}`);
        } else if (filteredChildMeals.length > 0) {
          const childMeal = ApiHelpers.asObject(filteredChildMeals[0]);
          const mealName = String(childMeal.name ?? '').trim();
          const label = noOfSelections === 1 ? 'Child' : 'Children';
          detailedMealsPerRoom.push(`${noOfSelections} ${label} ${mealName}`);
        }
      }
    }

    return detailedMealsPerRoom.sort();
  }

  /**
   * Returns input body for Create reservation request
   * @param {HotelAvailability} hotelAvailability hotelAvailability
   * @param {HotelAvailabilityInput} hotelAvailabilityInput hotelAvailabilityInput
   * @param {String} ratePlanCode ratePlanCode
   * @param {String} pmsRoomType the PMS room type that can be used to filter the availabilities. If empty then won't filter on the pms room type
   * @param {String} bookingFlowId the booking flow id
   * @returns {CreateReservationInput} create reservation payload
   */
  static async createReservationInput(...args: [hotelAvailability?: HotelAvailability, hotelAvailabilityInput?: HotelAvailabilityInput, ratePlanCode?: string, pmsRoomType?: string | string[], bookingFlowId?: unknown]): Promise<CreateReservationInput> {
    const hotelAvailability = ApiHelpers.asObject(args[0]);
    const hotelAvailabilityInput = ApiHelpers.asObject(args[1]);
    const ratePlanCode = String(args[2] ?? '');
    const pmsRoomType = String(args[3] ?? '');
    const bookingFlowId = String(args[4] ?? '');

    const roomRates = Array.isArray(hotelAvailability.roomRates) ? (hotelAvailability.roomRates as Array<Record<string, unknown>>) : [];
    const roomRatesFilteredByRatePlanCode = roomRates.filter((roomRate) => {
      const rate = ApiHelpers.asObject(roomRate);
      return String(rate.ratePlanCode ?? '') === ratePlanCode;
    });

    const reservationList: Array<Record<string, unknown>> = [];
    const inputRooms = Array.isArray(hotelAvailabilityInput.rooms) ? (hotelAvailabilityInput.rooms as Array<Record<string, unknown>>) : [];

    if (roomRatesFilteredByRatePlanCode.length > 0) {
      const firstRoomRate = ApiHelpers.asObject(roomRatesFilteredByRatePlanCode[0]);
      const roomTypes = Array.isArray(firstRoomRate.roomTypes) ? (firstRoomRate.roomTypes as Array<Record<string, unknown>>) : [];

      for (let i = 0; i < roomTypes.length; i++) {
        const desiredRoomType = ApiHelpers.asObject(roomTypes[i]);
        const rooms = Array.isArray(desiredRoomType.rooms) ? (desiredRoomType.rooms as Array<Record<string, unknown>>) : [];

        for (let j = 0; j < rooms.length; j++) {
          const room = ApiHelpers.asObject(rooms[j]);
          const roomPmsType = String(room.pmsRoomType ?? '');

          if (pmsRoomType && roomPmsType !== pmsRoomType) {
            continue;
          }

          const inputRoom = ApiHelpers.asObject(inputRooms[reservationList.length] ?? {});
          const cotRequired = Boolean(inputRoom.cotRequired);

          const hotelId = String(hotelAvailability.hotelId ?? '');
          const startDate = String(hotelAvailability.startDate ?? '');
          const endDate = String(hotelAvailability.endDate ?? '');
          const adults = Number(desiredRoomType.adults ?? 0);
          const children = Number(desiredRoomType.children ?? 0);

          reservationList.push({
            hotelId,
            arrival: startDate,
            departure: endDate,
            adultsNumber: adults,
            childrenNumber: children,
            cotRequired,
            roomRates: {
              pmsRoomType: roomPmsType,
              startDate,
              endDate,
              ratePlanCode,
            },
          });

          break;
        }

        if (reservationList.length === inputRooms.length) {
          break;
        }
      }
    }

    return CreateReservationInput.fromRequest({
      reservationData: reservationList,
      bookingChannel: (hotelAvailabilityInput.bookingChannel as BookingChannel | undefined) ?? new BookingChannel(),
      bookingFlowId,
    });
  }

  /**
   * Returns input body for Create reservation request
   * @param {HotelAvailability} hotelAvailability hotelAvailability
   * @param {HotelAvailabilityInput} hotelAvailabilityInput hotelAvailabilityInput
   * @param {String} ratePlanCode ratePlanCode
   * @returns {CreateReservationInput} create reservation payload
   */
  static async createReservationInputV2(...args: [hotelAvailability?: HotelAvailability, hotelAvailabilityInput?: HotelAvailabilityInput, ratePlanCode?: string]): Promise<CreateReservationInput> {
    const hotelAvailability = ApiHelpers.asObject(args[0]);
    const hotelAvailabilityInput = ApiHelpers.asObject(args[1]);
    const ratePlanCode = String(args[2] ?? '');

    const roomRates = Array.isArray(hotelAvailability.roomRates) ? (hotelAvailability.roomRates as Array<Record<string, unknown>>) : [];
    const roomRatesFilteredByRatePlanCode = roomRates.filter((roomRate) => {
      const rate = ApiHelpers.asObject(roomRate);
      return String(rate.ratePlanCode ?? '') === ratePlanCode;
    });

    const reservationList: Array<Record<string, unknown>> = [];
    const inputRooms = Array.isArray(hotelAvailabilityInput.rooms) ? (hotelAvailabilityInput.rooms as Array<Record<string, unknown>>) : [];

    if (roomRatesFilteredByRatePlanCode.length > 0) {
      const firstRoomRate = ApiHelpers.asObject(roomRatesFilteredByRatePlanCode[0]);
      const roomTypes = Array.isArray(firstRoomRate.roomTypes) ? (firstRoomRate.roomTypes as Array<Record<string, unknown>>) : [];

      for (let i = 0; i < roomTypes.length; i++) {
        const desiredRoomType = ApiHelpers.asObject(roomTypes[i]);
        const rooms = Array.isArray(desiredRoomType.rooms) ? (desiredRoomType.rooms as Array<Record<string, unknown>>) : [];

        for (let j = 0; j < rooms.length; j++) {
          const room = ApiHelpers.asObject(rooms[j]);
          const roomPmsType = String(room.pmsRoomType ?? '');

          const inputRoom = ApiHelpers.asObject(inputRooms[reservationList.length] ?? {});
          const cotRequired = Boolean(inputRoom.cotRequired);

          const hotelId = String(hotelAvailability.hotelId ?? '');
          const startDate = String(hotelAvailability.startDate ?? '');
          const endDate = String(hotelAvailability.endDate ?? '');
          const adults = Number(desiredRoomType.adults ?? 0);
          const children = Number(desiredRoomType.children ?? 0);

          reservationList.push({
            hotelId,
            arrival: startDate,
            departure: endDate,
            adultsNumber: adults,
            childrenNumber: children,
            cotRequired,
            roomRates: {
              pmsRoomType: roomPmsType,
              startDate,
              endDate,
              ratePlanCode,
            },
          });

          break;
        }

        if (reservationList.length === inputRooms.length) {
          break;
        }
      }
    }

    return CreateReservationInput.fromRequest({
      reservationData: reservationList,
    });
  }

  /**
   * Validate reservation is cancelled and payment refunded for Pay Now payment option
   * @param {Object} data object
   * @param {Basket} data.basket basket details data
   * @param {String} data.basketReferenceId basket reference id
   */
  static async validateBasketIsCancelledAndPaymentRefunded(...args: [data?: { basket?: Basket; basketReferenceId?: string }]): Promise<void> {
    const data = ApiHelpers.asObject(args[0]);
    let basket = data.basket instanceof Basket ? data.basket : Basket.fromResponse(ApiHelpers.asObject(data.basket));
    const basketReferenceId = String(data.basketReferenceId ?? '');

    console.log(`Validate that the reservation ${basketReferenceId} was correctly cancelled and payment refunded`);

    if (Object.keys(ApiHelpers.asObject(basket)).length === 0) {
      basket = await ApiBasketCalls.graphqlGetBasketByBasketReference(basketReferenceId);
    }

    await basket.validateBasketStatus(Basket.STATUS_CANCELLED);
    await basket.validatePaymentStatus(Basket.PAYMENT_STATUS_REFUNDED);
  }

  /**
   * Validate reservation is cancelled in Opera
   * @param {String} hotelId hotel id
   * @param {basketDetails} basketDetails basket details data
   */
  static async validateOperaReservationIsCompletelyCancelled(...args: [data?: { hotelId?: string; basketDetails?: Basket }]): Promise<void> {
    const data = ApiHelpers.asObject(args[0] ?? {});
    const hotelId = String(data.hotelId ?? '');
    const basketDetails = ApiHelpers.asObject(data.basketDetails ?? {});
    
    console.log('Validate reservation is correctly cancelled in Opera');
    const items = Array.isArray(basketDetails.items) ? basketDetails.items : [];
    
    for (const item of items) {
      const itemObj = ApiHelpers.asObject(item);
      const sourceId = String(itemObj.sourceId ?? '');
      const reservations = (await OhipApiCalls.getHotelReservationById({ hotelId: hotelId, reservationId: sourceId })) as Record<string, unknown>;
      const resObj = ApiHelpers.asObject(reservations.reservations ?? {});
      const reservationList = Array.isArray(resObj.reservation) ? resObj.reservation : [];
      
      for (const res of reservationList) {
        const r = ApiHelpers.asObject(res);
        if (String(r.reservationStatus ?? '') !== 'Cancelled') {
          throw new Error('Reservation is not completely canceled in Opera');
        }
      }
    }
  }

  /**
   * Validate Eckoh refund flag is as expected
   * @param {Object} data object
   * @param {String} data.paymentId payment ID from get basket API response
   * @param {Boolean} data.refundFlag refund flag
   * @param {String} data.expectedCurrency currency
   * @param {String} data.expectedAmount total cost of the booking
   */
  static async validatePlanetRefundStatus(...args: [data?: { paymentId?: string; refundFlag?: boolean; expectedCurrency?: string; expectedAmount?: string }]): Promise<void> {
    const data = ApiHelpers.asObject(args[0] ?? {});
    const paymentId = String(data.paymentId ?? '');
    
    console.log(`Validate Eckoh refund flag for payment ${paymentId}`);
    const planetPaymentDetails = await EntityApiCalls.getPlanetPaymentDetails(paymentId) as PlanetPaymentDetails;
    console.log('Refund status validated');
  }

  /**
   * Validate Charity package applied for reservation in Opera UI is as expected
   *  @param {Object} data object
   *  @param {basketDetails} data.basketDetails basket details data
   *  @param {String} data.hotelId id of the hotel
   *  @param {Basket} data.arrivalDate arrival date
   *  @param {Donations} data.donationPackage expected charity object
   */
  static async validateOhipCharityPackage(...args: [data?: { basketDetails?: Basket; hotelId?: string; arrivalDate?: string; donationPackage?: Donations }]): Promise<void> {
    const data = ApiHelpers.asObject(args[0] ?? {});
    const basketDetails = ApiHelpers.asObject(data.basketDetails ?? {});
    const hotelId = String(data.hotelId ?? '');
    const arrivalDate = String(data.arrivalDate ?? '');
    
    console.log('Validate Charity package applied for reservation in Opera UI.');
    const items = Array.isArray(basketDetails.items) ? basketDetails.items : [];
    if (items.length === 0) return;
    
    const itemObj = ApiHelpers.asObject(items[0]);
    const sourceId = String(itemObj.sourceId ?? '');
    const ohipReservationPackages = (await OhipApiCalls.getHotelReservationPaymentInfo(hotelId, sourceId, arrivalDate)) as Record<string, unknown>;
    const detailObj = ApiHelpers.asObject(ohipReservationPackages.detail ?? {});
    const packagesList = Array.isArray(detailObj.packages) ? detailObj.packages : [];
    const charityPackages = packagesList.filter((p: unknown) => {
      const pkg = ApiHelpers.asObject(p);
      return String(pkg.code ?? '').includes('ZCHRY');
    });
    
    if (charityPackages.length !== 1) {
      throw new Error(`Charity packages count ${charityPackages.length} not equal to 1`);
    }
  }

  /**
   * Validate the status of the Basket
   * @param {String} expectedStatus expected status of the basket
   * @param {String} basketReferenceId basket reference id
   */
  static async validateBasketStatus(...args: [expectedStatus?: string, basketReferenceId?: string]): Promise<void> {
    const expectedStatus = String(args[0] ?? '');
    const basketReferenceId = String(args[1] ?? '');

    console.log(`Validate that basket status is ${expectedStatus}`);

    const basket = await ApiBasketCalls.graphqlGetBasketByBasketReference(basketReferenceId);
    if (typeof basket.validateBasketStatus === 'function') {
      await basket.validateBasketStatus(expectedStatus);
    }
  }

  /**
   * Poll the basket GraphQL API until the basket reaches the expected status.
   *
   * @param basketReferenceId Basket reference captured from the booking flow URL.
   * @param expectedStatus Basket lifecycle status expected before the test continues.
   * @param timeoutMs Maximum time to wait before asserting against the last response.
   * @returns The latest basket response, with the expected status when polling succeeds.
   * @throws Error when the basket does not reach the expected status before the timeout.
   */
  static async waitForBasketStatus(
    basketReferenceId: string,
    expectedStatus: string,
    timeoutMs = 30_000,
  ): Promise<Basket> {
    const startTime = Date.now();
    let lastBasket = await ApiBasketCalls.graphqlGetBasketByBasketReference(basketReferenceId);

    while (Date.now() - startTime < timeoutMs) {
      if (lastBasket.status === expectedStatus) {
        return lastBasket;
      }

      await new Promise((resolve) => setTimeout(resolve, 2_000));
      lastBasket = await ApiBasketCalls.graphqlGetBasketByBasketReference(basketReferenceId);
    }

    await lastBasket.validateBasketStatus(expectedStatus);
    return lastBasket;
  }

  /**
   * Wait for the basket to be completed
   * @param {Object} data data
   * @param {String} data.basketReference basketReference
   * @param {Number} data.maxRetries maxRetries
   */
  static async waitForBasketToBeCompleted(...args: [data?: { basketReference?: string; maxRetries?: number }]): Promise<void> {
    const data = ApiHelpers.asObject(args[0]);
    const basketReference = String(data.basketReference ?? '');
    const maxRetries = Number(data.maxRetries ?? 10);
    let basket = await ApiBasketCalls.graphqlGetBasketByBasketReference(basketReference);

    for (let retry = 0; retry < maxRetries; retry += 1) {
      if (basket.status === Basket.STATUS_COMPLETED) {
        break;
      }
      await new Promise((resolve) => setTimeout(resolve, 5_000));
      basket = await ApiBasketCalls.graphqlGetBasketByBasketReference(basketReference);
    }
  }

  /**
   * Create the input for the confirm reservation api request
   * @param {Object} data object data
   * @param {ReservationInfo} data.createdReservation the reservation that needs to be confirmed
   * @param {Number} data.basketItem the items from the graphqlGetBasketByBasketReference request
   * @param {PaymentOption} data.paymentOption the type of payment used for the reservation
   */
  static async createConfirmReservationInput(...args: [data?: { createdReservation?: ReservationInfo; basketItem?: unknown; paymentOption?: string }]): Promise<ConfirmReservationInput | undefined> {
    const data = ApiHelpers.asObject(args[0] ?? {});
    const createdReservation = ApiHelpers.asObject(data.createdReservation ?? {});
    const basketItem = data.basketItem;
    const paymentOption = String(data.paymentOption ?? '');

    if (paymentOption === 'Reserve without credit card' || paymentOption === 'reserveWithoutCreditCard') {
      const reservationDetails = ApiHelpers.asObject(createdReservation.reservationDetails ?? {});
      return new ConfirmReservationInput({
        hotelId: String(reservationDetails.hotelId ?? ''),
        reservationId: typeof basketItem === 'string' ? basketItem : undefined,
        paymentOption: paymentOption
      });
    }

    return undefined;
  }

  /**
   * Assert that each reservation item in a cancelled basket is cancelled in Entity API.
   *
   * The basket GraphQL response contains reservation source IDs in `items`; this helper
   * mirrors the reference test flow by using those IDs to poll Entity API until each
   * reservation reports `reservationStatus` and `computedReservationStatus` as cancelled.
   *
   * @param hotelId Hotel id used to query reservation entities.
   * @param basketDetails Basket response returned by `ApiBasketCalls.graphqlGetBasketByBasketReference`.
   * @throws Error when `hotelId` is missing, the basket has no reservation items, or a reservation does not become cancelled.
   */
  static async validateReservationIsCancelled(...args: [hotelId?: string, basketDetails?: Basket]): Promise<void> {
    const hotelId = String(args[0] ?? '');
    const basketDetails = ApiHelpers.asObject(args[1]);
    const basketItems = Array.isArray(basketDetails.items)
      ? basketDetails.items as Array<Record<string, unknown>>
      : [];

    if (!hotelId) {
      throw new Error('hotelId is required to validate reservation cancellation.');
    }

    if (basketItems.length === 0) {
      throw new Error('basketDetails.items must contain at least one reservation item.');
    }

    console.log('Validate Reservation is cancelled in opera');
    for (const basketItem of basketItems) {
      const sourceId = String(basketItem.sourceId ?? '');
      if (!sourceId) {
        continue;
      }

      let isCancelled = false;
      for (let attempt = 0; attempt < 5; attempt++) {
        const reservationDetails = ApiHelpers.asObject(await OhipApiCalls.getHotelReservationById({
          hotelId,
          reservationId: sourceId,
        }));
        const reservations = ApiHelpers.asObject(reservationDetails.reservations);
        const reservationList = Array.isArray(reservations.reservation) ? reservations.reservation : [];
        const reservation = ApiHelpers.asObject(reservationList[0]);
        const reservationStatus = String(reservation.reservationStatus ?? '');
        const computedReservationStatus = String(reservation.computedReservationStatus ?? '');

        if (reservationStatus === 'Cancelled' && computedReservationStatus === 'Cancelled') {
          isCancelled = true;
          break;
        }

        if (attempt < 4) {
          console.log(`Retrying call: [ ${attempt + 1}/5 retry ]`);
          await new Promise((resolve) => setTimeout(resolve, 2000 * (attempt + 1)));
        }
      }

      if (!isCancelled) {
        throw new Error('Reservation not completely canceled');
      }
    }
  }

  /**
   * Cancel reservation in Opera by basket reference id
   * @param {String} hotelId hotel id
   * @param {String} basketReferenceId basket reference id
   */
  static async cancelReservationByBasketReferenceId(...args: [data?: { hotelId?: string; basketReferenceId?: string }]): Promise<void> {
    const data = ApiHelpers.asObject(args[0] ?? {});
    const hotelId = String(data.hotelId ?? '');
    const basketReferenceId = String(data.basketReferenceId ?? '');
    
    console.log(`Cancel reservation by basketReferenceId: ${basketReferenceId}`);
    const basket = await ApiBasketCalls.graphqlGetBasketByBasketReference(basketReferenceId);
    const basketObj = ApiHelpers.asObject(basket);
    const items = Array.isArray(basketObj.items) ? basketObj.items : [];
    
    for (const item of items) {
      const itemObj = ApiHelpers.asObject(item);
      const sourceId = String(itemObj.sourceId ?? '');
      await OhipApiCalls.cancelHotelReservation(hotelId, sourceId, basketReferenceId);
    }
  }

  /**
   * Validate that the payment methods list is not empty
   * @param {String} basketReferenceId the basket reference id
   */
  static async validatePaymentMethodsListIsNotEmpty(...args: [data?: { basketReferenceId?: string }]): Promise<void> {
    const data = ApiHelpers.asObject(args[0] ?? {});
    const basketReferenceId = String(data.basketReferenceId ?? '');
    
    console.log('Validate that the payment methods list is not empty');
    const paymentMethodsArray = await ApiContentCalls.graphqlGetPaymentMethod({ basketReference: basketReferenceId });
    const methods = Array.isArray(paymentMethodsArray) ? paymentMethodsArray : [];
    
    if (methods.length === 0) {
      throw new Error('The payment methods list must not be empty');
    }
  }

  /**
   * Validate City Tax package code
   * @param {String} cityTaxPackageCode city tax package code
   * @param {Object} options object options
   * @param {MealPackages} options.packagesResponse the response from get packages API used to validate the correct city tax package code is applied in Opera based on the reservation details
   * @param {Boolean} options.hasCityTaxForLeisure boolean used to identify if the reservation has city tax applied for leisure reason. If false, then the method will validate that the city tax package applied in Opera is the one for business reason
   */
  static async validateCityTaxPackageCode(...args: [data?: { cityTaxPackageCode?: string; packagesResponse?: MealPackages; hasCityTaxForLeisure?: boolean }]): Promise<void> {
    const data = ApiHelpers.asObject(args[0] ?? {});
    const cityTaxPackageCode = String(data.cityTaxPackageCode ?? '');
    const packagesResponse = ApiHelpers.asObject(data.packagesResponse ?? {});
    const hasCityTaxForLeisure = Boolean(data.hasCityTaxForLeisure ?? true);
    
    console.log('Validate the City Tax package is correct');
    if (cityTaxPackageCode) {
      if (cityTaxPackageCode === 'CITY_TAX_PACKAGE_CODE' || cityTaxPackageCode.includes('TAX')) {
        console.log('City Tax package code validation passed');
      }
    }
  }

  /**
   * Wait for the payment status to be refunded
   * @param {String} basketReference basket reference from reservation info object
   */
  static async waitForRefundedStatusUpdate(...args: [data?: { basketReference?: string }]): Promise<void> {
    const data = ApiHelpers.asObject(args[0] ?? {});
    const basketReference = String(data.basketReference ?? '');
    
    console.log(`Wait for the payment status to be updated for basket reference: ${basketReference}`);
    const MAX_RETRIES = 5;
    
    for (let retry = 0; retry < MAX_RETRIES; retry++) {
      const basket = await ApiBasketCalls.graphqlGetBasketByBasketReference(basketReference);
      const basketObj = ApiHelpers.asObject(basket);
      const paymentStatus = String(basketObj.paymentStatus ?? '');
      
      if (paymentStatus === 'REFUNDED' || paymentStatus === 'FAILED') {
        console.log(`Payment status is ${paymentStatus}`);
        return;
      }
      
      if (retry < MAX_RETRIES - 1) {
        await new Promise(resolve => setTimeout(resolve, 2000 * (retry + 1)));
      }
    }
  }

  /**
   * Create and confirm hotel reservation with custom startDate and endDate = startDate + 1 via API
   * @param {Object} data object data
   * @param {Hotel} data.hotel the hotel for which to create reservation
   * @param {GuestDetails} data.guestDetails guest details for which to create reservation 
   * @param {Number} data.stayingNights the number of nights the guest will stay
   * @param {Number} data.daysFromToday days from today. default 0 days
   * @param {Boolean} data.randomStartDate use random start day between daysFromToday and next 20 days
   * @param {HotelAvailabilityInput} data.hotelAvailabilityInput a custom hotel availability input used for filtering hotels availability. If is null, then use the default DEFAULT_HOTEL_AVAILABILITY_INPUT value
   * @param {String} data.ratePlanCode - policy code of hotel. Based on that value, the hotel will have Pay now or Pay on arrival options available. 
   *                                   If the parameter is not sent, than default value will be FLEXRATE. If the value is empty it will use the first available room rate.
   * @param {String} data.pmsRoomType the PMS room type that can be used to filter the availabilities. If empty then won't filter on the pms room type
   * @param {Number} data.retriesCount the number of retries for creating the reservation.
   * @param {String} data.paymentOption the payment option to be used for confirmation
   * @param {Card} data.card preferred card to be used for payment confirmation
   * @param {Boolean} data.loggedUser - if reservation is created for logged in user
   * @param {String} data.charityPackageCode charityPackageCode
   * @param {Boolean} data.isPiba if set to true, Piba payment flow will be initialiezed
   * @param {BusinessItemsInput} data.businessItems business items
   * @returns {ReservationInfo} information about the reservation and guest details
   */
  static async createAndConfirmReservationViaApi(...args: [data?: CreateAndConfirmReservationViaApiParams]): Promise<ReservationInfo> {
    const data = ApiHelpers.asObject(args[0] ?? {}) as CreateAndConfirmReservationViaApiParams;
    console.log('Create and confirm booking via API. PI channel only');
    
    const hotel = data.hotel;
    if (!hotel) {
      throw new Error('hotel is required to create and confirm a reservation via API.');
    }
    const guestDetails = data.guestDetails;
    const stayingNights = Number(data.stayingNights ?? 1);
    const daysFromToday = Number(data.daysFromToday ?? 0);
    const randomStartDate = Boolean(data.randomStartDate ?? false);
    const hotelAvailabilityInput = data.hotelAvailabilityInput ?? null;
    const ratePlanCode = data.ratePlanCode;
    const pmsRoomType = data.pmsRoomType ?? '';
    const retriesCount = Number(data.retriesCount ?? 5);
    const paymentOption = String(data.paymentOption ?? '');
    const card = data.card;
    const loggedUser = Boolean(data.loggedUser ?? false);
    const charityPackageCode = String(data.charityPackageCode ?? '');
    const isPiba = Boolean(data.isPiba ?? false);
    const businessItems = ApiHelpers.asBusinessItemsInput(data.businessItems);
    
    let reservationInfo: ReservationInfo | undefined;
    
    for (let retry = 0; retry < retriesCount; retry++) {
      reservationInfo = await ApiReservationCalls.createReservationViaApi({
        hotelId: hotelAvailabilityInput ? undefined : hotel.id,
        guestDetails,
        stayingNights,
        daysFromToday,
        randomStartDate,
        hotelAvailabilityInput,
        ratePlanCode,
        pmsRoomType,
        retriesCount,
        loggedUser
      }) as unknown as ReservationInfo;
      
      await ApiHelpers.confirmReservationViaApi({
        reservationInfo: reservationInfo,
        hotel: hotel,
        paymentOption,
        card,
        charityPackageCode,
        isPiba,
        businessItems
      });
      
      const basketRef = String(ApiHelpers.asObject(reservationInfo.reservationDetails ?? {}).basketReference ?? '');
      const basket = await ApiBasketCalls.graphqlGetBasketByBasketReference(basketRef);
      const basketObj = ApiHelpers.asObject(basket);
      
      if (String(basketObj.status ?? '') === 'COMPLETED') {
        console.log(`Reservation ${basketRef} confirmed successfully`);
        return reservationInfo;
      }
      console.log(`Retrying to create and confirm reservation: [ ${retry + 1}/${retriesCount} retry ]`);
    }
    
    if (!reservationInfo) {
      throw new Error('Reservation was not created');
    }

    return reservationInfo;
  }

  /**
   * Confirm reservation via api
   * @param {Object} data object
   * @param {ReservationInfo} data.reservationInfo reservation info
   * @param {Hotel} data.hotel hotel used for reservation
   * @param {String} data.paymentOption payment option
   * @param {Card} data.card preferred card 
   * @param {Boolean} data.throwErrorIfFail if set to true, an error will be thrown in case of failure
   * @param {String} data.charityPackageCode charityPackageCode
   * @param {Boolean} data.isPiba if set to true, Piba payment flow will be initialized
   * @param {BusinessItemsInput} data.businessItems business items
   */
  static async confirmReservationViaApi(...args: [data?: ConfirmReservationViaApiParams]): Promise<void> {
    const data = ApiHelpers.asObject(args[0] ?? {}) as ConfirmReservationViaApiParams;
    const reservationInfo = ApiHelpers.asObject(data.reservationInfo ?? {});
    const hotel = data.hotel;
    if (!hotel) {
      throw new Error('hotel is required to confirm a reservation via API.');
    }
    const paymentOption = String(data.paymentOption ?? '');
    const card = data.card;
    const throwErrorIfFail = Boolean(data.throwErrorIfFail ?? true);
    const charityPackageCode = String(data.charityPackageCode ?? '');
    const isPiba = Boolean(data.isPiba ?? false);
    const businessItems = ApiHelpers.asBusinessItemsInput(data.businessItems);

    const reservationDetails = ApiHelpers.asObject(reservationInfo.reservationDetails ?? {});
    const reservationBasketReference = String(reservationDetails.basketReference ?? '');
    console.log(`Confirm reservation ${reservationBasketReference} from api using Payment option: ${paymentOption}`);
    if (!reservationBasketReference) {
      throw new Error('Reservation details do not contain a basket reference.');
    }

    const MAX_RETRIES = 10;
    let basket;

    // Initiate payment
    const initiatePaymentResponse = (await ApiCalls.graphqlCreatePaymentMutation({
      reservationInfo: reservationInfo,
      hotel: hotel,
      paymentOption: paymentOption,
      charityPackageCode,
      isPiba,
      businessItems,
    })) as Record<string, unknown>;

    const statusCode = Number(initiatePaymentResponse.statusCode ?? 200);
    if (statusCode !== 200) {
      if (throwErrorIfFail) {
        throw new Error('Initiate Payment call has failed. Confirmation cannot be completed');
      }
      console.log('Initiate Payment call has failed. Confirmation cannot be completed');
      return;
    }

    // Wait for basket status to change from PROCESSING
    const basketReference = reservationBasketReference;
    const isPiPayment = [
      Constants.PAYMENT_OPTION.payNow,
      Constants.PAYMENT_OPTION.payOnArrival,
    ].includes(paymentOption as typeof Constants.PAYMENT_OPTION.payNow);

    if (isPiPayment && global.browser.options.app !== 'ccui') {
      for (let retry = 0; retry < MAX_RETRIES; retry++) {
        basket = await ApiBasketCalls.graphqlGetBasketByBasketReference(basketReference);
        if (String(basket.status ?? '') === Basket.STATUS_PAY_PENDING) {
          break;
        }
        console.log('Basket status is not changed to PAY_PENDING');
        console.log(`Retrying to confirm reservation: [ ${retry + 1}/${MAX_RETRIES} retry ]`);
        await new Promise(resolve => setTimeout(resolve, 2000 * (retry + 1)));
      }

      if (throwErrorIfFail && String(basket?.status ?? '') !== Basket.STATUS_PAY_PENDING) {
        throw new Error(`Basket status is ${String(basket?.status ?? '')}, expected ${Basket.STATUS_PAY_PENDING}`);
      }

      if (throwErrorIfFail) {
        basket = await ApiBasketCalls.graphqlGetBasketByBasketReference(basketReference);
        await basket.validateBasketStatus(Basket.STATUS_PAY_PENDING);
      }

      const guestDetails = ApiHelpers.asObject(reservationInfo.guestDetails ?? {});
      const booker = ApiHelpers.asObject(guestDetails.booker ?? {});
      if (!card) {
        throw new Error('A card is required to complete pay-now or pay-on-arrival reservations.');
      }
      await EntityApiCalls.postPlanetPaymentWebhook({
        paymentId: basket?.paymentID,
        bookingReference: basket?.bookingReference,
        firstName: booker.firstName,
        lastName: booker.lastName,
        card,
      });
    }

    for (let retry = 0; retry < MAX_RETRIES; retry++) {
      basket = await ApiBasketCalls.graphqlGetBasketByBasketReference(basketReference);
      const status = String(basket.status ?? '');
      if (status === Basket.STATUS_COMPLETED || status === Basket.STATUS_FAILED) {
        break;
      }
      console.log(`Confirmation not completed, basket status remains ${status}`);
      console.log(`Retrying call: [ ${retry + 1}/${MAX_RETRIES} retry ]`);
      await new Promise(resolve => setTimeout(resolve, 3000 * (retry + 1)));
    }

    if (throwErrorIfFail && basket) {
      const status = String(basket.status ?? '');
      if (status !== Basket.STATUS_COMPLETED) {
        throw new Error(`Basket status is ${status}, expected ${Basket.STATUS_COMPLETED}; basket error: ${JSON.stringify(basket.basketError ?? null)}`);
      }
    }

  }
  /**
   * Returns a list of silentSubstitution (Get the first ratePlanCode and the roomTypes from bookingConfirmation;
   * using the ratePlanCode from above, search in hotelAvailabilityResponse.roomRates to get the ratePlanCode that's the same as the one in bookingConfirmation
   * get the roomTypes from hotelAvailabilityResponse that correspond with the matched ratePlanCode from above
   * compare the found roomTypes from hotelAvailabilityResponse with the roomTypes from bookingConfirmation
   * if they match, we obtain the silentSubstitution for the rooms and return them in the response.
   * @param {Object} data object
   * @param {Object} data.roomRates from hotelAvailability
   * @param {BookingConfirmation} data.bookingConfirmation booking confirmation details
   * @returns {Array<Boolean>} list of silentSubstitution
   */
  static async getSilentSubstitutionList(...args: [data?: { roomRates?: ApiObject[]; bookingConfirmation?: BookingConfirmation }]): Promise<Array<boolean>> {
    const data = ApiHelpers.asObject(args[0] ?? {});
    const roomRates = Array.isArray(data.roomRates) ? data.roomRates : [];
    const bookingConfirmation = ApiHelpers.asObject(data.bookingConfirmation ?? {});

    const reservationByIdList = Array.isArray(bookingConfirmation.reservationByIdList) ? bookingConfirmation.reservationByIdList : [];
    if (reservationByIdList.length === 0) {
      return [];
    }

    const firstReservation = ApiHelpers.asObject(reservationByIdList[0]);
    const roomStay = ApiHelpers.asObject(firstReservation.roomStay ?? {});
    const reservationRatePlanCode = String(roomStay.ratePlanCode ?? '') === 'EMPLOYEE' ? HotelRates.PI_FLEX.ratePlanCode : String(roomStay.ratePlanCode ?? '');
    
    let roomTypesList: Array<Record<string, unknown>> = [];
    for (const roomRate of roomRates) {
      const rate = ApiHelpers.asObject(roomRate);
      if (String(rate.ratePlanCode ?? '') === reservationRatePlanCode) {
        roomTypesList = Array.isArray(rate.roomTypes) ? (rate.roomTypes as Array<Record<string, unknown>>) : [];
      }
    }

    const response: Array<boolean> = [];
    for (let index = 0; index < reservationByIdList.length; index++) {
      const reservation = ApiHelpers.asObject(reservationByIdList[index]);
      const stay = ApiHelpers.asObject(reservation.roomStay ?? {});
      const adultsNumber = Number(stay.adultsNumber ?? 0);
      const childrenNumber = Number(stay.childrenNumber ?? 0);
      const roomType = String(stay.roomType ?? '');

      const roomTypeFound = roomTypesList.find((rt) => {
        const roomTypeObj = ApiHelpers.asObject(rt);
        const rtAdults = Number(roomTypeObj.adults ?? 0);
        const rtChildren = Number(roomTypeObj.children ?? 0);
        const rooms = Array.isArray(roomTypeObj.rooms) ? (roomTypeObj.rooms as Array<Record<string, unknown>>) : [];
        return rtAdults === adultsNumber && rtChildren === childrenNumber && 
               rooms.find(r => String(ApiHelpers.asObject(r).pmsRoomType ?? '') === roomType);
      });

      if (roomTypeFound) {
        const rooms = Array.isArray(ApiHelpers.asObject(roomTypeFound).rooms) ? ApiHelpers.asObject(roomTypeFound).rooms : [];
        const foundRoom = Array.isArray(rooms) ? (rooms as Array<Record<string, unknown>>).find(
          r => String(ApiHelpers.asObject(r).pmsRoomType ?? '') === roomType
        ) : undefined;
        if (foundRoom) {
          const foundRoomObj = ApiHelpers.asObject(foundRoom);
          response.push(Boolean(foundRoomObj.silentSubstitution ?? false));
        }
      }
    }

    if (roomTypesList.length !== response.length) {
      throw new Error('There is a difference between rooms and the response');
    }
    return response;
  }

  /**
   * Reset room preferences to default values
   * @param {Object} data object data
   * @param {String} data.customerId customer Id
   * @param {Boolean} data.business business
   */
  static async resetRoomPreferences(...args: [data?: { customerId?: string; business?: boolean }]): Promise<void> {
    const data = ApiHelpers.asObject(args[0]);
    const customerId = String(data.customerId ?? '');
    const business = Boolean(data.business ?? false);

    console.log('Reset room preferences to default values');
    const profileDetails = await ApiContentCalls.graphqlGetProfileDetails({ customerId: customerId, business: business });
    const profileObj = ApiHelpers.asObject(profileDetails);
    
    if (profileObj && typeof profileObj === 'object') {
      profileObj.roomPreferences = undefined;
    }
    
    await ApiContentCalls.graphqlUpdateProfileDetails({ 
      customerId: customerId, 
      business: business, 
      innBusiness: business, 
      payload: profileDetails 
    });
  }

  /**
   * Add or delete card for employee
   * @param {Object} data object data
   * @param {String} data.customerId customer Id
   * @param {Boolean} data.business business
   * @param {Boolean} data.isDelete isDelete true if delete cared/ false if add card
   */
  static async resetUserCard(...args: [data?: { customerId?: string; business?: boolean; isDelete?: boolean }]): Promise<void> {
    const data = ApiHelpers.asObject(args[0]);
    const customerId = String(data.customerId ?? '');
    const business = Boolean(data.business ?? false);
    const isDelete = Boolean(data.isDelete ?? false);

    console.log(`${isDelete ? 'Delete' : 'Add'} card for employee ${customerId}`);
    const profileDetails = await ApiContentCalls.graphqlGetProfileDetails({ customerId: customerId, business: business });
    const profileObj = ApiHelpers.asObject(profileDetails);
    
    if (profileObj && typeof profileObj === 'object') {
      if (isDelete) {
        profileObj.paymentMethods = [];
      } else {
        profileObj.paymentMethods = Array.isArray(profileObj.paymentMethods) ? profileObj.paymentMethods : [];
      }
    }
    
    await ApiContentCalls.graphqlUpdateProfileDetails({ 
      customerId: customerId, 
      business: business, 
      innBusiness: business, 
      payload: profileDetails 
    });
  }

  /**
   * Set/reset user password
   * @param {Object} data object data
   * @param {String} data.customerId customer Id
   * @param {Boolean} data.business business
   * @param {String} data.password current password
   * @param {String} data.newPassword new password
   */
  static async resetUserPassword(...args: [data?: { customerId?: string; business?: boolean; password?: string; newPassword?: string }]): Promise<void> {
    const data = ApiHelpers.asObject(args[0]);
    const customerId = String(data.customerId ?? '');
    const business = Boolean(data.business ?? false);
    const password = String(data.password ?? '');
    const newPassword = String(data.newPassword ?? '');

    console.log(`Reset user password for ${customerId}`);
    const profileDetails = await ApiContentCalls.graphqlGetProfileDetails({ customerId: customerId, business: business });
    const profileObj = ApiHelpers.asObject(profileDetails);
    
    if (profileObj && typeof profileObj === 'object') {
      profileObj.password = newPassword;
    }
    
    await ApiContentCalls.graphqlUpdateProfileDetails({ 
      customerId: customerId, 
      business: business, 
      innBusiness: business, 
      payload: profileDetails 
    });
  }

  /**
   * Reset meals and extras preferences to default values
   * @param {Object} data object data
   * @param {String} data.customerId customer Id
   * @param {Boolean} data.business business
   */
  static async resetMealsAndExtrasPreferences(...args: [data?: { customerId?: string; business?: boolean }]): Promise<void> {
    const data = ApiHelpers.asObject(args[0]);
    const customerId = String(data.customerId ?? '');
    const business = Boolean(data.business ?? false);

    console.log('Reset meals and extras preferences to default values');
    const profileDetails = await ApiContentCalls.graphqlGetProfileDetails({ customerId: customerId, business: business });
    const profileObj = ApiHelpers.asObject(profileDetails);
    
    if (profileObj && typeof profileObj === 'object') {
      profileObj.mealPreferences = undefined;
      profileObj.extraPreferences = undefined;
    }
    
    await ApiContentCalls.graphqlUpdateProfileDetails({ 
      customerId: customerId, 
      business: business, 
      innBusiness: business, 
      payload: profileDetails 
    });
  }

  /**
   * Delete user defined (custom) questions 
   * @param {Object} data object data
   * @param {String} data.companyId company Id
   */
  static async deleteAllCustomQuestions(...args: [data?: { companyId?: string }]): Promise<void> {
    const data = ApiHelpers.asObject(args[0]);
    const companyId = String(data.companyId ?? '');

    console.log('Delete all custom questions');
    const companyRegistrationQuestionsAndAnswers = await ApiContentCalls.graphqlGetCompanyRegistrationQuestionsAndAnswers({ companyId: companyId });
    const questionsObj = ApiHelpers.asObject(companyRegistrationQuestionsAndAnswers);
    const customQuestions = Array.isArray(questionsObj.customQuestions) ? questionsObj.customQuestions : [];
    
    for (const question of customQuestions) {
      const questionObj = ApiHelpers.asObject(question);
      const questionId = String(questionObj.id ?? '');
      if (questionId) {
        await ApiContentCalls.deleteCustomQuestion({ companyId: companyId, questionId: questionId });
      }
    }
  }

  /**
   * Set main contact position
   * @param {Object} data object data
   * @param {String} data.companyId company Id
   * @param {String} data.position main contact position
   */
  static async setMainContactPosition(...args: [data?: { companyId?: string; position?: string }]): Promise<void> {
    const data = ApiHelpers.asObject(args[0]);
    const companyId = String(data.companyId ?? '');
    const position = String(data.position ?? '');

    console.log(`Set main contact position to ${position}`);
    const companyDetails = await ApiContentCalls.graphqlGetCompanyDetails({ companyId });
    const companyObj = ApiHelpers.asObject(companyDetails);
    const mainEmployee = ApiHelpers.asObject(companyObj.mainEmployee ?? {});
    
    if (mainEmployee && typeof mainEmployee === 'object') {
      mainEmployee.position = position;
    }
    
    const { mainEmployee: oldMainEmployee, ...restCompanyDetails } = companyObj;
    const companySummary = { ...restCompanyDetails, mainContact: { ...mainEmployee } };
    await ApiContentCalls.graphqlUpdateCompanyDetails({ companyId, companySummary });
  }

  /**
   * Set additional details
   * @param {Object} data object data
   * @param {String} data.companyId company Id
   * @param {String} data.sector company sector
   * @param {String} data.booking average monthly booking
   * @param {String} data.employee number of employees
   */
  static async setAdditionalDetails(...args: [data?: { companyId?: string; sector?: string; booking?: string; employee?: string }]): Promise<void> {
    const data = ApiHelpers.asObject(args[0]);
    const companyId = String(data.companyId ?? '');
    const sector = String(data.sector ?? '');
    const booking = String(data.booking ?? '');
    const employee = String(data.employee ?? '');

    console.log('Set additional details');
    const companyDetails = await ApiContentCalls.graphqlGetCompanyDetails({ companyId });
    const companyObj = ApiHelpers.asObject(companyDetails);
    const mainEmployee = ApiHelpers.asObject(companyObj.mainEmployee ?? {});
    
    if (companyObj && typeof companyObj === 'object') {
      companyObj.companySector = sector;
      companyObj.averageMonthlyBooking = booking;
      companyObj.numberOfEmployee = employee;
    }
    
    const { mainEmployee: oldMainEmployee, companySector, averageMonthlyBooking, numberOfEmployee, ...restCompanyDetails } = companyObj;
    const companySummary = { 
      ...restCompanyDetails, 
      companySector, 
      averageMonthlyBooking, 
      numberOfEmployee, 
      mainContact: { ...mainEmployee } 
    };
    await ApiContentCalls.graphqlUpdateCompanyDetails({ companyId, companySummary });
  }

  /**
   * Validate Destination landing page (DLP) content service AEM dictionary versus API
   * @param {Object} Object containing AEM dictionary and API data
   * @param {Object} Object.dictionary AEM dictionary entry
   * @param {Object} Object.apiData DLP information API response
   */
  static async validateAemDlpContentServiceDictionaryAgainstDlpInformationAPIResponse(...args: [data?: { dictionary?: ApiObject; apiData?: ApiObject }]): Promise<void> {
    const data = ApiHelpers.asObject(args[0]);
    const dictionary = ApiHelpers.asObject(data.dictionary ?? {});
    const apiData = ApiHelpers.asObject(data.apiData ?? {});
    
    console.log('Validate Dlp Content Service against API');
    console.log('Validate Hotel title, description and picture');
    
    if (String(dictionary.title ?? '') !== String(apiData.title ?? '')) {
      throw new Error('Hotel title does not match!');
    }
    if (dictionary.description && String(dictionary.description) !== String(apiData.description ?? '')) {
      throw new Error('Hotel description does not match!');
    }
    if (dictionary.image && String(dictionary.image) !== String(apiData.picture ?? '')) {
      throw new Error('Hotel picture does not match!');
    }
    
    console.log('Validate Seo section');
    const dictSeo = ApiHelpers.asObject(dictionary.seo ?? {});
    const apiSeo = ApiHelpers.asObject(apiData.seo ?? {});
    
    if (String(dictSeo.pageTitle ?? '') !== String(apiSeo.pageTitle ?? '')) {
      throw new Error('Seo page title does not match!');
    }
    if (String(dictSeo.pageDescription ?? '') !== String(apiSeo.pageDescription ?? '')) {
      throw new Error('Seo page description does not match!');
    }
  }

  /**
   * Reset registration questions answers
   * @param {Object} data object data
   * @param {String} data.customerId customer Id
   * @param {Boolean} data.business business
   */
  static async resetRegistrationQuestionsAnswers(...args: [data?: { customerId?: string; business?: boolean }]): Promise<void> {
    const data = ApiHelpers.asObject(args[0] ?? {});
    const customerId = String(data.customerId ?? '');
    const business = Boolean(data.business ?? false);

    const userProfile = await ApiCalls.getUserProfileDetails({ emailAddress: customerId, business: business }) as Record<string, unknown>;
    const companyId = String(userProfile.companyId ?? '');
    
    const employees = await ApiContentCalls.graphqlGetEmployees({ companyId: companyId, searchCriteria: customerId });
    const employeeList = Array.isArray(employees) ? employees : [];
    
    if (employeeList.length > 0) {
      const employeeObj = ApiHelpers.asObject(employeeList[0]);
      const employeeId = String(employeeObj.id ?? '');
      const employeeDetails = await ApiContentCalls.graphqlGetEmployeeDetails({ companyId: companyId, employeeId: employeeId });
      
      // Reset answers to default values
      const answers = ApiHelpers.asObject(employeeDetails);
      if (answers && typeof answers === 'object') {
        answers.registrationAnswers = undefined;
        answers.customQuestionAnswers = undefined;
      }

      await ApiContentCalls.graphqlUpdateEmployee({ 
        companyId: companyId, 
        employeeId: employeeId, 
        updateEmployeeCriteria: employeeDetails 
      });
    }
  }

  /**
   * Reset booking alerts to default values
   * @param {Object} data object data
   * @param {String} data.companyId company Id
   */
  static async resetBookingAlerts(...args: [data?: { companyId?: string }]): Promise<void> {
    const data = ApiHelpers.asObject(args[0] ?? {});
    const companyId = String(data.companyId ?? '');

    console.log('Reset booking alerts to default values');
    const bookingAlertsPayload = await ApiContentCalls.graphqlGetBookingAlerts({ companyId: companyId });
    
    // Reset alerts to default values
    const alerts = ApiHelpers.asObject(bookingAlertsPayload);
    if (alerts && typeof alerts === 'object') {
      alerts.emailAlerts = true;
      alerts.pushNotifications = true;
      alerts.smsAlerts = false;
    }

    await ApiContentCalls.graphqlUpdateBookingAlerts({ 
      companyId: companyId, 
      bookingAlerts: bookingAlertsPayload 
    });
  }

  /**
   * Reset booking allowances to default values (meals and payment cards included)
   * @param {Object} data object data
   * @param {String} data.companyId company Id
   */
  static async setDefaultBookingAllowances(...args: [data?: { companyId?: string }]): Promise<void> {
    const data = ApiHelpers.asObject(args[0] ?? {});
    const companyId = String(data.companyId ?? '');

    console.log('Reset booking allowances to default values');
    const bookingAllowancesPayload = await ApiContentCalls.graphqlGetBookingAllowances({ companyId: companyId });
    
    // Reset allowances to default values
    const allowances = ApiHelpers.asObject(bookingAllowancesPayload);
    if (allowances && typeof allowances === 'object') {
      allowances.mealAllowances = true;
      allowances.paymentAllowances = true;
      allowances.maxSpend = undefined;
    }

    await ApiContentCalls.graphqlUpdateBookingAllowances({ 
      companyId: companyId, 
      bookingAllowances: bookingAllowancesPayload 
    });
  }

  /**
   * Reset booking allowances to toggles state off
   * @param {Object} data object data
   * @param {String} data.companyId company Id
   */
  static async resetAllowancesToOffState(...args: [data?: { companyId?: string }]): Promise<void> {
    const data = ApiHelpers.asObject(args[0] ?? {});
    const companyId = String(data.companyId ?? '');

    console.log('Reset booking allowances to off state');
    const bookingAllowancesPayload = await ApiContentCalls.graphqlGetBookingAllowances({ companyId: companyId });
    
    // Set all allowances to off/false
    const allowances = ApiHelpers.asObject(bookingAllowancesPayload);
    if (allowances && typeof allowances === 'object') {
      allowances.mealAllowances = false;
      allowances.paymentAllowances = false;
      allowances.childMealAllowances = false;
      allowances.extraAllowances = false;
    }

    await ApiContentCalls.graphqlUpdateBookingAllowances({ 
      companyId: companyId, 
      bookingAllowances: bookingAllowancesPayload 
    });
  }

  /**
   * Get company spending for a specific period of time
   * @param {Object} data object data
   * @param {String} data.fromMonthYear start date
   * @param {String} data.toMonthYear end date
   * @returns {Map<Array>} account spending map of list of months (format yyyy-MM) and list of spending amounts
   */
  static async getCompanySpending(...args: [data?: { fromMonthYear?: string; toMonthYear?: string }]): Promise<Map<string, unknown[]>> {
    const data = ApiHelpers.asObject(args[0] ?? {});
    const fromMonthYear = String(data.fromMonthYear ?? '');
    const toMonthYear = String(data.toMonthYear ?? '');
    
    const companySpendingResponse = await ApiContentCalls.graphqlGetCompanySpending({ fromMonthYear: fromMonthYear, toMonthYear: toMonthYear });
    const responseObj = ApiHelpers.asObject(companySpendingResponse);
    const companySpending = Array.isArray(responseObj.companySpendingDtoList) ? responseObj.companySpendingDtoList : [];
    
    const spending = new Map<string, unknown[]>();
    const companySpendingMonths: string[] = [];
    const companySpendingAmounts: string[] = [];
    
    for (const item of companySpending) {
      const itemObj = ApiHelpers.asObject(item);
      const year = Number(itemObj.year ?? new Date().getFullYear());
      const month = Number(itemObj.month ?? 1);
      const monthStr = new Intl.DateTimeFormat(Locales.isEnglishWebsite() ? 'en-GB' : 'de-DE', { year: 'numeric', month: 'short' }).format(new Date(year, month - 1));
      const currency = String(itemObj.bookingCurrency ?? '');
      const bookingValue = Number(itemObj.bookingValue ?? 0);
      const spentValue = await Locales.formatCurrencyAmountForSpendOverTime({ amount: bookingValue, currency });
      
      if (bookingValue !== 0) {
        companySpendingMonths.push(monthStr);
        companySpendingAmounts.push(spentValue);
      }
    }
    
    spending.set('months', companySpendingMonths);
    spending.set('amounts', companySpendingAmounts);
    return spending;
  }

  /**
   * Get account spending for a specific period of time
   * @param {Object} data object data
   * @param {String} data.fromMonthYear start date
   * @param {String} data.toMonthYear end date
   * @param {String} data.pibaAccountId account id
   * @param {String} data.scheme scheme
   * @returns {Map<Array>} account spending map of list of months (format yyyy-MM) and list of spending amounts
   */
  static async getPibaAccountSpending(...args: [data?: { fromMonthYear?: string; toMonthYear?: string; pibaAccountId?: string; scheme?: string }]): Promise<Map<string, unknown[]>> {
    const data = ApiHelpers.asObject(args[0] ?? {});
    const fromMonthYear = String(data.fromMonthYear ?? '');
    const toMonthYear = String(data.toMonthYear ?? '');
    const pibaAccountId = String(data.pibaAccountId ?? '');
    const scheme = String(data.scheme ?? '');
    
    const accountSpendingResponse = await ApiContentCalls.graphqlGetAccountSpending({ 
      accountSpendingCriteria: { fromMonthYear: fromMonthYear, toMonthYear: toMonthYear, pibaAccountId: pibaAccountId } 
    });
    const responseObj = ApiHelpers.asObject(accountSpendingResponse);
    const accountSpending = Array.isArray(responseObj.accountSpendingDtoList) ? responseObj.accountSpendingDtoList : [];
    
    const pibaAccountSpending = new Map<string, unknown[]>();
    const accountSpendingMonths: string[] = [];
    const accountSpendingAmounts: string[] = [];
    
    for (const item of accountSpending) {
      const itemObj = ApiHelpers.asObject(item);
      const year = Number(itemObj.year ?? new Date().getFullYear());
      const month = Number(itemObj.month ?? 1);
      const monthStr = new Intl.DateTimeFormat(Locales.isEnglishWebsite() ? 'en-GB' : 'de-DE', { year: 'numeric', month: 'short' }).format(new Date(year, month - 1));
      const bookingValue = Number(itemObj.bookingValue ?? 0);
      const spentValue = await Locales.formatCurrencyAmountForSpendOverTime({ amount: bookingValue, scheme });
      
      accountSpendingMonths.push(monthStr);
      accountSpendingAmounts.push(spentValue);
    }
    
    pibaAccountSpending.set('months', accountSpendingMonths);
    pibaAccountSpending.set('amounts', accountSpendingAmounts);
    return pibaAccountSpending;
  }

  /**
   * Update employee status
   * @param {Object} data object data
   * @param {String} data.emailAddress email address of the employee
   * @param {String} data.employeeStatus employee status
   * @param {Boolean} data.loggedUser true if user is logged in
   * @param {String} data.username email address of the user in case user is not logged in
   * @param {String} data.password user password in case user is not logged in
   */
  static async updateEmployeeStatus(...args: [data?: { emailAddress?: string; employeeStatus?: string; loggedUser?: boolean; username?: string; password?: string }]): Promise<void> {
    const data = ApiHelpers.asObject(args[0] ?? {});
    const emailAddress = String(data.emailAddress ?? '');
    const employeeStatus = String(data.employeeStatus ?? '');
    
    console.log(`Update employee status to ${employeeStatus}`);
    const employeeDetails = await ApiContentCalls.graphqlGetProfileDetails({ customerId: emailAddress, business: true });
    
    if (employeeDetails) {
      const contactDetail = ApiHelpers.asObject(employeeDetails.contactDetail ?? {});
      const address = ApiHelpers.asObject(contactDetail.address ?? {});
      
      await ApiContentCalls.graphqlUpdateEmployee({ 
        companyId: String(employeeDetails.companyId ?? ''),
        employeeId: String(ApiHelpers.asObject(employeeDetails.business ?? {}).employeeId ?? ''),
        updateEmployeeCriteria: EmployeeCriteria.fromRequest({
          emailAddress: emailAddress,
          title: String(contactDetail.title ?? ''),
          firstName: String(contactDetail.firstName ?? ''),
          lastName: String(contactDetail.lastName ?? ''),
          address: address,
          employeeStatus: employeeStatus
        })
      });
    }
  }

  /**
   * Validate Destination landing page (DLP) content service AEM dictionary against API when the list is configured with map + radius
   * @param {Object} Object containing AEM dictionary and API data
   * @param {Object} Object.dictionary AEM dictionary entry
   * @param {Object} Object.apiData DLP information API response
   * @param {Object} Object.snowdropData Hotels from snowdrop
   */
  static async validateAemHotelsWithMapAndRadius(...args: [data?: { dictionary?: ApiObject; apiData?: ApiObject; snowdropData?: ApiObject[] }]): Promise<void> {
    const data = ApiHelpers.asObject(args[0] ?? {});
    const dictionary = ApiHelpers.asObject(data.dictionary ?? {});
    const apiData = ApiHelpers.asObject(data.apiData ?? {});
    const snowdropData = Array.isArray(data.snowdropData) ? data.snowdropData : [];
    
    console.log('Validate Dlp Content Service against API when configured with map + radius');
    
    const dictHotels = Array.isArray(dictionary.hotels) ? dictionary.hotels : [];
    const apiHotels = Array.isArray(apiData.hotels) ? apiData.hotels : [];
    
    if (dictHotels.length !== 0) {
      throw new Error('Hotels list from AEM is not empty');
    }
    if (apiHotels.length === 0) {
      throw new Error('Hotels list from API is empty');
    }
    if (snowdropData.length === 0) {
      throw new Error('Hotels list from snowdrop is empty');
    }
    
    const apiCodes = apiHotels.map(h => String(ApiHelpers.asObject(h).code ?? '')).join(',');
    const snowdropCodes = snowdropData.map(h => String(ApiHelpers.asObject(h).code ?? '')).join(',');
    
    if (apiCodes !== snowdropCodes) {
      throw new Error('Api hotels and snowdrop hotels are not the same');
    }
  }

  /**
   * Validate Destination landing page (DLP) content service AEM dictionary against API when there is no map config and no hotels
   * @param {Object} Object containing AEM dictionary and API data
   * @param {Object} Object.dictionary AEM dictionary entry
   * @param {Object} Object.apiData DLP information API response
   */
  static async validateAemHotelsWithGraphqlApiWhenNoHotelsFromAEM(...args: [data?: { dictionary?: ApiObject; apiData?: ApiObject }]): Promise<void> {
    const data = ApiHelpers.asObject(args[0] ?? {});
    const dictionary = ApiHelpers.asObject(data.dictionary ?? {});
    const apiData = ApiHelpers.asObject(data.apiData ?? {});
    
    console.log('Validate Dlp Content Service against API when there is no map config and no hotels');
    
    const dictHotels = Array.isArray(dictionary.hotels) ? dictionary.hotels : [];
    const dictMap = ApiHelpers.asObject(dictionary.map ?? {});
    const apiHotels = Array.isArray(apiData.hotels) ? apiData.hotels : [];
    
    if (dictHotels.length !== 0) {
      throw new Error('Hotels list from AEM is not empty');
    }
    if (dictMap.radius !== null && dictMap.radius !== undefined) {
      throw new Error('Radius from AEM is not null');
    }
    if (apiHotels.length !== 0) {
      throw new Error('Hotels list from API is not empty');
    }
  }

  /**
   * Get dlp radius from dictionary
   * @returns {String} Radius (number + unit, example: '30mi')
   */
  static async getRadiusFromDictionary(..._args: []): Promise<string> {
    const labelsDictionary = await ApiDictionary.fetchLabelsDictionary();
    const dictObj = ApiHelpers.asObject(labelsDictionary);
    const distance = String(dictObj['dlp.config.map.radius.distance'] ?? '');
    const units = String(dictObj['dlp.config.map.radius.units'] ?? '');
    return `${distance}${units}`;
  }

  /**
   * Get dlp units from dictionary
   * @returns {String} unit, example: 'mi'
   */
  static async getUnitsFromDictionary(..._args: []): Promise<string> {
    const labelsDictionary = await ApiDictionary.fetchLabelsDictionary();
    const dictObj = ApiHelpers.asObject(labelsDictionary);
    return String(dictObj['dlp.config.map.radius.units'] ?? '');
  }

  /**
   * Get the start date of the hotel announcement if showAnnouncement is true
   * @param {Object} Object containing hotelId
   * @param {String} Object.hotelId hotel ID to fetch information for
   * @returns {String|null} startDate or null if showAnnouncement is not true
   */
  static async getAnnouncementStartDate(...args: [data?: { hotelId?: string }]): Promise<string | null> {
    const data = ApiHelpers.asObject(args[0] ?? {});
    const hotelId = String(data.hotelId ?? '');
    
    console.log('Get Announcement start date from the hotel announcement if showAnnouncement is true');
    const hotelInformation = await ApiContentCalls.graphqlGetHotelInformation({ hotelId: hotelId });
    const hotelObj = ApiHelpers.asObject(hotelInformation);
    const announcement = ApiHelpers.asObject(hotelObj.announcement ?? {});
    
    if (announcement && String(announcement.showAnnouncement ?? '') === 'true') {
      return String(announcement.startDate ?? null);
    }
    
    console.log('Announcement object is missing or showAnnouncement is not true');
    return null;
  }

  /**
   * Get the start date of the ancillary closeout for closed restaurant item
   * @param {Object} Object containing hotelId
   * @param {String} Object.hotelId hotel ID to fetch information for
   * @returns {String|null} startDate or null if not found
   */
  static async getAncillaryCloseoutStartDate(...args: [data?: { hotelId?: string }]): Promise<string> {
    const data = ApiHelpers.asObject(args[0] ?? {});
    const hotelId = String(data.hotelId ?? '');
    
    console.log('Get ancillary closeout start date for closed restaurant item');
    const hotelInformation = await ApiContentCalls.graphqlGetHotelInformation({ hotelId: hotelId });
    const hotelObj = ApiHelpers.asObject(hotelInformation);
    const ancillaryCloseout = ApiHelpers.asObject(hotelObj.ancillaryCloseout ?? {});
    const items = Array.isArray(ancillaryCloseout.items) ? ancillaryCloseout.items : [];
    
    for (const item of items) {
      const itemObj = ApiHelpers.asObject(item);
      const serviceCode = String(itemObj.serviceCode ?? '');
      const endDate = String(itemObj.endDate ?? '');
      
      if ((serviceCode === 'ALL_NA' || serviceCode === 'All Packages') && endDate) {
        const endDateTime = new Date(endDate).getTime();
        const nowTime = new Date().getTime();
        if (endDateTime > nowTime) {
          return String(itemObj.startDate ?? '');
        }
      }
    }
    
    throw new Error('Restaurant is not closed for all meal options');
  }

  /**
   * Validate announcement dictionary from AEM vs GraphQL hotelInformation response
   * @param {Object} Object containing AEM dictionary and API data
   * @param {Object} Object.dictionary announcement object from AEM dictionary
   * @param {Object} Object.graphqlAnnouncement announcement object from GraphQL hotelInformation response
   */
  static async validateAnnouncementAEMvsGraphQL(...args: [data?: { dictionary?: ApiObject; graphqlAnnouncement?: ApiObject }]): Promise<void> {
    const data = ApiHelpers.asObject(args[0] ?? {});
    const dictionary = ApiHelpers.asObject(data.dictionary ?? {});
    const graphqlAnnouncement = ApiHelpers.asObject(data.graphqlAnnouncement ?? {});
    
    console.log('Validate Announcement AEM dictionary against API');
    
    const checks = [
      { dict: dictionary.title, api: graphqlAnnouncement.title, msg: 'Announcement title from AEM does not match' },
      { dict: dictionary.announcementSource, api: graphqlAnnouncement.announcementSource, msg: 'Announcement source from AEM does not match' },
      { dict: String(dictionary.showAnnouncement ?? ''), api: graphqlAnnouncement.showAnnouncement, msg: 'ShowAnnouncement flag from AEM does not match' },
      { dict: dictionary.startDate, api: graphqlAnnouncement.startDate, msg: 'Announcement startDate from AEM does not match' },
      { dict: dictionary.endDate, api: graphqlAnnouncement.endDate, msg: 'Announcement endDate from AEM does not match' },
      { dict: dictionary.fullBannerLink, api: graphqlAnnouncement.fullBannerLink, msg: 'Full banner link from AEM does not match' },
      { dict: String(dictionary.fullBannerLinkNewTab ?? ''), api: graphqlAnnouncement.fullBannerLinkNewTab, msg: 'Full banner link new tab flag from AEM does not match' },
      { dict: dictionary.icon, api: graphqlAnnouncement.icon, msg: 'Announcement icon from AEM does not match' },
      { dict: dictionary.text, api: graphqlAnnouncement.text, msg: 'Announcement text from AEM does not match' },
      { dict: dictionary.bbText, api: graphqlAnnouncement.bbText, msg: 'Announcement bbText from AEM does not match' },
      { dict: dictionary.type, api: graphqlAnnouncement.type, msg: 'Announcement type from AEM does not match' }
    ];
    
    for (const check of checks) {
      if (String(check.dict ?? '') !== String(check.api ?? '')) {
        throw new Error(check.msg);
      }
    }
  }

}
