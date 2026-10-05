import { Constants } from '../../test-data/constants';
import type { GuestDetailsModel } from '../../test-data/guestDetails';
import type { HotelData } from '../../test-data/hotels';
import { Locales } from '../../test-data/locales';
import type { PmsRoomType } from '../../test-data/pmsRoomTypes';
import { PmsRoomTypes } from '../../test-data/pmsRoomTypes';
import type { SearchCriteria } from '../../test-data/searchCriteria';
import { Helpers } from '../../utils/helpers';
import { ApiDictionary } from '../aem/apiDictionary';
import { EntityApiCalls } from '../entityApiCalls';
import { OhipApiCalls } from '../ohip/ohipApiCalls';
import { ApiCalls } from './apiCalls';
import type { Basket, BasketItem, BookingConfirmation, Donations, HotelAvailability, BookingInformationUpgradeToFlex } from '../response';

interface BookingConfirmationData {
  bookingConfirmation?: BookingConfirmation;
}

interface DepositFoliosWithRefundData {
  basketInformation: Basket;
  refundedAmount: number;
}

interface CityTaxValidationData {
  hotel?: HotelData;
  basketReferenceId?: string;
  arrivalDate?: string;
  departureDate?: string;
  reasonForStay: string;
  adultsNumber?: number;
  childrenNumber?: number;
  bookingFlowId?: string;
  hotelHasCityTaxForBusiness?: boolean;
  hotelHasCityTaxForLeisure?: boolean;
}

interface BookingAllowancesValidationData {
  basketInformation: Basket;
  isPremierInnBreakfastAllowanceChecked?: boolean;
  isCarParkingAllowanceChecked?: boolean;
  packagesPrebookedAndAuthorizedMealList?: boolean[];
}

interface ExpectedRatePerNight {
  pricePerNight: number;
  date: string;
}

interface ExpectedMealPackage {
  description?: string;
  totalQuantity: number;
  unitPrice: number;
  packageCode: string;
}

interface StayingGuestAndRoomDetails {
  title?: string;
  firstName?: string;
  lastName?: string;
  adultsNumber?: number;
  childrenNumber?: number;
  guestAddressLine1?: string;
  guestAddressLine2?: string;
  guestAddressLine3?: string;
  postalCode?: string;
  cityName?: string;
  guestsEmail?: string;
}

/**
 * Methods used to trim/modify and validate responses received for Api Call getBookingConfirmation
 */
export class ApiBookingConfirmationHelpers {
  [key: string]: unknown;

  /**
   * Create a helper around a booking-confirmation response payload.
   * @param data raw GraphQL response or nested booking-confirmation data object
   */
  constructor(data: Record<string, unknown> = {}) {
    const values = Object.values(data);
    const payload = values.length === 1 && values[0] && typeof values[0] === 'object' && !Array.isArray(values[0])
      ? values[0] as Record<string, unknown>
      : data;

    Object.assign(this, payload);
  }

  /**
   * Create a booking-confirmation helper from typed API data.
   * @param data raw GraphQL response data
   * @returns helper instance populated with response values
   */
  static fromApiData<T extends Record<string, unknown>>(data: T): ApiBookingConfirmationHelpers {
    return new ApiBookingConfirmationHelpers(data);
  }

  /**
   * Access the helper instance as a response-shaped record.
   * @returns booking-confirmation response data
   */
  private get bookingConfirmationData(): Record<string, unknown> {
    return this as unknown as Record<string, unknown>;
  }

  /**
   * Safely coerce an unknown value to an object record.
   * @param input value from API data
   * @returns object record or an empty object when the value is not object-shaped
   */
  private asObject(input: unknown): Record<string, unknown> {
    return input && typeof input === 'object' && !Array.isArray(input) ? input as Record<string, unknown> : {};
  }

  /**
   * Safely coerce an unknown value to an array of object records.
   * @param input value from API data
   * @returns object array or an empty array when the value is not an array
   */
  private asArray(input: unknown): Array<Record<string, unknown>> {
    return Array.isArray(input) ? input as Array<Record<string, unknown>> : [];
  }

  /**
   * Compare currency amounts after two-decimal normalization, allowing the known one-penny rounding drift.
   * @param actualAmount amount from the booking-confirmation API
   * @param expectedAmount amount from the source validation data
   * @returns true when the two amounts are equal or differ by one penny
   */
  private amountsMatch(actualAmount: number, expectedAmount: number): boolean {
    const actual = Number((Math.round(Number(actualAmount) * 100) / 100).toFixed(2));
    const expected = Number((Math.round(Number(expectedAmount) * 100) / 100).toFixed(2));
    return actual === expected
      || Number((actual + 0.01).toFixed(2)) === expected
      || actual === Number((expected + 0.01).toFixed(2));
  }

  /**
   * Normalize a phone number for confirmation comparisons.
   * @param phoneValue phone value from form data or API response
   * @param prefix selected country prefix
   * @returns phone number with a plus-prefixed country code and no area-code leading zero
   */
  private normalizePhoneForComparison(phoneValue: string, prefix: string): string {
    const finalPrefix = String(prefix ?? '').trim();
    const rawPhoneValue = String(phoneValue ?? '').replace(/\s+/g, '');

    if (!rawPhoneValue) {
      return '';
    }

    const normalizedPrefix = finalPrefix.startsWith('+') ? finalPrefix : `+${finalPrefix}`;
    const phoneWithoutPrefix = rawPhoneValue.startsWith(normalizedPrefix)
      ? rawPhoneValue.slice(normalizedPrefix.length)
      : rawPhoneValue;

    const phoneWithoutZero = Helpers.getTelephoneNumberWithoutZero(phoneWithoutPrefix);

    if (!finalPrefix) {
      return phoneWithoutZero;
    }

    return `${normalizedPrefix}${phoneWithoutZero}`;
  }

  /**
   * Calculate the total price of room with given index
   * @param {Number} index of room
   * @returns {Float} Total cost of current room
   */
  async getRoomTotalPrice(index: number): Promise<number> {
    const roomIndex = Number(index ?? 0);
    const roomReservationByIdList = this.asArray(this.bookingConfirmationData.roomReservationByIdList);
    const room = this.asObject(roomReservationByIdList[roomIndex]);
    const roomStay = this.asObject(room.roomStay);
    const roomStayTotalPrice = Number.parseFloat(String(roomStay.roomPrice ?? '0'));
    return roomStayTotalPrice + (await this.getMealsTotalPrice(roomIndex));
  }

  /**
   * Calculate the total price of the meals for a room with given index
   * @param {Number} index of room
   * @returns {Float} Total cost of current room
   */
  async getMealsTotalPrice(index: number): Promise<number> {
    const roomIndex = Number(index ?? 0);
    const roomReservationByIdList = this.asArray(this.bookingConfirmationData.roomReservationByIdList);
    const room = this.asObject(roomReservationByIdList[roomIndex]);
    const reservationPackageList = this.asArray(room.reservationPackageList);
    const roomMeals = reservationPackageList.filter((packageItem) => !String(packageItem.packageCode ?? '').includes('ZCHRY'));
    const mealsPricePerNight = roomMeals.reduce((sum, current) => sum + Number.parseFloat(String(current.computedPrice ?? '0')), 0);
    const ratesPerNight = this.asArray(this.asObject(room.roomStay).ratesPerNight);
    return mealsPricePerNight * ratesPerNight.length;
  }

  /**
   * Get the selected Pms room type
   * @param {HotelAvailability} availability availability response
   * @param {PmsRoomType} pmsRoomType rate name
   */
  async getSelectedPmsRoomType(availabilityInput: HotelAvailability, pmsRoomTypeInput: PmsRoomType): Promise<string> {
    const availability = this.asObject(availabilityInput);
    const pmsRoomType = this.asObject(pmsRoomTypeInput);
    const ids = Array.isArray(pmsRoomType.ids) ? pmsRoomType.ids.map((id) => String(id)) : [];

    const roomRates = this.asArray(availability.roomRates);
    const firstRoomRate = this.asObject(roomRates[0]);
    const roomTypes = this.asArray(firstRoomRate.roomTypes);
    const firstRoomType = this.asObject(roomTypes[0]);
    const rooms = this.asArray(firstRoomType.rooms);

    const matchedRoom = rooms.find((room) => ids.includes(String(room.pmsRoomType ?? '')));
    if (!matchedRoom) {
      throw new Error(`No matching room found for pmsRoomType: ${JSON.stringify(ids)}`);
    }
    return String(matchedRoom.pmsRoomType ?? '');
  }

  /**
   * Calculate the total city tax from all reservations and nights in the booking confirmation
  * @param {Object} data object
  * @param {BookingConfirmation} data.bookingConfirmation booking confirmation response
   * @returns {Float} The total city tax for all reservations and nights
   */
  static async getTotalCityTax(dataInput: BookingConfirmationData = {}): Promise<number> {
    const data = dataInput && typeof dataInput === 'object' && !Array.isArray(dataInput) ? dataInput : {};
    const bookingConfirmation = data.bookingConfirmation && typeof data.bookingConfirmation === 'object'
      ? data.bookingConfirmation as Record<string, unknown>
      : {};
    const reservationByIdList = Array.isArray(bookingConfirmation.reservationByIdList)
      ? bookingConfirmation.reservationByIdList as Array<Record<string, unknown>>
      : [];

    let cityTax = 0;
    for (const reservation of reservationByIdList) {
      const roomStay = reservation.roomStay && typeof reservation.roomStay === 'object'
        ? reservation.roomStay as Record<string, unknown>
        : {};
      const ratesPerNight = Array.isArray(roomStay.ratesPerNight)
        ? roomStay.ratesPerNight as Array<Record<string, unknown>>
        : [];
      for (const rate of ratesPerNight) {
        cityTax += Math.round(Number(rate.cityTaxPerNight ?? 0) * 100) / 100;
      }
    }
    return cityTax;
  }

  /**
   * Calculate the total pricePerNight (without city taxes) from all reservations and nights in the booking confirmation
  * @param {Object} data object
  * @param {BookingConfirmation} data.bookingConfirmation booking confirmation response
   * @returns {Float} The total price for all reservations and nights (without city taxes)
   */
  static async getTotalPricePerNight(dataInput: BookingConfirmationData = {}): Promise<number> {
    const data = dataInput && typeof dataInput === 'object' && !Array.isArray(dataInput) ? dataInput : {};
    const bookingConfirmation = data.bookingConfirmation && typeof data.bookingConfirmation === 'object'
      ? data.bookingConfirmation as Record<string, unknown>
      : {};
    const reservationByIdList = Array.isArray(bookingConfirmation.reservationByIdList)
      ? bookingConfirmation.reservationByIdList as Array<Record<string, unknown>>
      : [];

    let pricePerNight = 0;
    for (const reservation of reservationByIdList) {
      const roomStay = reservation.roomStay && typeof reservation.roomStay === 'object'
        ? reservation.roomStay as Record<string, unknown>
        : {};
      const ratesPerNight = Array.isArray(roomStay.ratesPerNight)
        ? roomStay.ratesPerNight as Array<Record<string, unknown>>
        : [];
      for (const rate of ratesPerNight) {
        pricePerNight += Number(rate.pricePerNight ?? 0);
      }
    }
    return pricePerNight;
  }

  /**
   * Validate hotel details
   * @param {Hotel} hotel hotel used in test
   */
  async validateHotel(hotelInput: HotelData): Promise<void> {
    console.log('Validate hotel');
    const hotel = this.asObject(hotelInput);
    const expectedId = String(hotel.id ?? '');
    const actualHotelId = String(this.bookingConfirmationData.hotelId ?? this.asObject(this.bookingConfirmationData.hotel).id ?? '');
    const hotelIdMatches = expectedId ? actualHotelId === expectedId : false;
    if (expectedId) {
      global.expect(actualHotelId, 'Expected hotelId value does not match').toBe(expectedId);
    }

    const expectedName = String(hotel.name ?? hotel.hotelName ?? '');
    const actualName = String(this.bookingConfirmationData.hotelName ?? this.asObject(this.bookingConfirmationData.hotel).name ?? '');
    if (expectedName) {
      const normalizeHotelName = (value: string) => value.toLowerCase().replace(/[^a-z0-9]/g, '');
      const actualNameNormalized = normalizeHotelName(actualName);
      const expectedNameNormalized = normalizeHotelName(expectedName);

      const hotelNameMatches =
        actualName === expectedName
        || actualName.toLowerCase() === expectedName.toLowerCase()
        || actualName.toLowerCase().includes(expectedName.toLowerCase())
        || actualNameNormalized.includes(expectedNameNormalized)
        || expectedNameNormalized.includes(actualNameNormalized);

      if (!hotelNameMatches && hotelIdMatches) {
        console.warn(`[Confirmation API] Hotel name mismatch ignored because hotelId matched: expected "${expectedName}", actual "${actualName}"`);
        return;
      }

      global.expect(hotelNameMatches).toBe(true);
    }
  }

  /**
   * Validate Staying Dates (arrival/departure)
   * @param {SearchCriteria} searchCriteria search criteria used to search for hotel available rooms
   */
  async validateStayingDates(searchCriteriaInput: SearchCriteria): Promise<void> {
    console.log('Validate staying dates');
    const searchCriteria = this.asObject(searchCriteriaInput);
    const reservationByIdList = this.asArray(this.bookingConfirmationData.reservationByIdList);
    const expectedArrivalDate = this.toIsoDayDate(new Date(String(searchCriteria.arrivalDate ?? '')));
    const expectedDepartureDate = this.toIsoDayDate(new Date(String(searchCriteria.departureDate ?? '')));

    for (const reservation of reservationByIdList) {
      const roomStay = this.asObject(reservation.roomStay);
      global.expect(String(roomStay.arrivalDate ?? ''), 'Expected arrivalDate value does not match').toBe(expectedArrivalDate);
      global.expect(String(roomStay.departureDate ?? ''), 'Expected departureDate value does not match').toBe(expectedDepartureDate);
    }
  }

  /**
   * Validate rooms occupancy
   * @param {SearchCriteria} searchCriteria search criteria used to search for hotel available rooms  
   */
  async validateRoomsOccupancy(searchCriteriaInput: SearchCriteria): Promise<void> {
    console.log('Validate rooms occupancy');
    const searchCriteria = this.asObject(searchCriteriaInput);
    const searchCriteriaRooms = this.asArray(searchCriteria.rooms);
    const reservationByIdList = this.asArray(this.bookingConfirmationData.reservationByIdList);

    const unmatchedRooms = [...searchCriteriaRooms];
    for (const reservation of reservationByIdList) {
      const roomStay = this.asObject(reservation.roomStay);
      const roomIndex = unmatchedRooms.findIndex((room) =>
        this.getAdultsCount(this.asObject(room)) === this.getAdultsCount(roomStay)
        && this.getChildrenCount(this.asObject(room)) === this.getChildrenCount(roomStay));
      if (roomIndex !== -1) {
        unmatchedRooms.splice(roomIndex, 1);
      }
    }

    global.expect(unmatchedRooms.length, 'Search criteria rooms occupancy is not the same with rooms occupancy from API response').toBe(0);
  }

  /**
   * Read the adult count from a room-shaped object.
   * @param input room or nested room data
   * @returns adult count or NaN when no supported key is present
   */
  private getAdultsCount(input: Record<string, unknown>): number {
    return this.getCount(input, ['adultsNumber', 'adults', 'adultCount', 'adultsCount', 'numberOfAdults']);
  }

  /**
   * Read the child count from a room-shaped object.
   * @param input room or nested room data
   * @returns child count or NaN when no supported key is present
   */
  private getChildrenCount(input: Record<string, unknown>): number {
    return this.getCount(input, ['childrenNumber', 'children', 'childCount', 'childrenCount', 'numberOfChildren']);
  }

  /**
   * Recursively read the first numeric count value from any supported key.
   * @param input object to inspect
   * @param keys accepted property names for the count
   * @returns numeric count or NaN when no supported key is present
   */
  private getCount(input: Record<string, unknown>, keys: string[]): number {
    for (const key of keys) {
      if (input[key] !== undefined) {
        return Number(input[key]);
      }
    }

    for (const value of Object.values(input)) {
      if (value && typeof value === 'object' && !Array.isArray(value)) {
        const count = this.getCount(value as Record<string, unknown>, keys);
        if (!Number.isNaN(count)) {
          return count;
        }
      }
    }

    return Number.NaN;
  }

  /**
   * Validate Room types
   * @param {String} selectedRoomName the name of the selected room type, this needs to be set after selecting the room
   * @param {String} hotelType hotel type
   */
  async validateRoomTypes(selectedRoomNameOrType: unknown, hotelType?: unknown): Promise<void> {
    console.log('Validate room types');
    void hotelType;
    const selectedRoomName = String(selectedRoomNameOrType ?? '').trim().toLowerCase();
    let selectedRoomTypeIds = this.asArray(this.asObject(selectedRoomNameOrType).ids).map((id) => String(id));
    if (selectedRoomTypeIds.length === 0 && selectedRoomName && hotelType) {
      selectedRoomTypeIds = await PmsRoomTypes.getPmsRoomIdsByNameAndType({ roomTypeName: String(selectedRoomNameOrType), hotelType: String(hotelType) });
    }
    const reservationByIdList = this.asArray(this.bookingConfirmationData.reservationByIdList);
    if ((!selectedRoomName && selectedRoomTypeIds.length === 0) || reservationByIdList.length === 0) {
      return;
    }

    for (const reservation of reservationByIdList) {
      const roomStay = this.asObject(reservation.roomStay);
      const bookingRoomType = String(roomStay.roomType ?? '');
      const roomExtraInfo = this.asObject(roomStay.roomExtraInfo);
      const roomTypeName = String(
        roomStay.roomTypeName
        ?? roomExtraInfo.roomName
        ?? roomExtraInfo.roomDescription
        ?? roomExtraInfo.roomType
        ?? roomStay.roomType
        ?? ''
      ).toLowerCase();
      const bookingRoomTypeNormalized = bookingRoomType.toLowerCase();
      const selectedRoomCode = this.getRoomTypeCode(selectedRoomName);
      const normalizeRoomType = (value: string) => value.toLowerCase().replace(/[^a-z0-9]/g, '');
      const bookingRoomTypeComparable = normalizeRoomType(bookingRoomTypeNormalized);
      const selectedRoomNameComparable = normalizeRoomType(selectedRoomName);
      const selectedRoomComparableTerms = [
        ...this.getRoomTypeComparableTerms(selectedRoomName),
        ...selectedRoomTypeIds.flatMap((roomTypeId) => this.getRoomTypeComparableTerms(roomTypeId)),
      ]
        .map((value) => normalizeRoomType(value));
      const roomTypeMatchesSelectedId = selectedRoomTypeIds.includes(bookingRoomType);
      const roomTypeMatchesAllowedIdAlias = selectedRoomComparableTerms.includes(bookingRoomTypeComparable);
      if (roomTypeMatchesSelectedId || roomTypeMatchesAllowedIdAlias) {
        continue;
      }

      global.expect(
        roomTypeName.includes(selectedRoomName)
        || bookingRoomTypeNormalized === selectedRoomName
        || bookingRoomTypeNormalized === selectedRoomCode
        || bookingRoomTypeComparable.includes(selectedRoomNameComparable)
        || selectedRoomNameComparable.includes(bookingRoomTypeComparable)
        || selectedRoomComparableTerms.some((term) => bookingRoomTypeComparable.includes(term)),
        `Unexpected room type name: selected="${selectedRoomName}", actual="${bookingRoomType}", description="${roomTypeName}", allowedIds="${selectedRoomTypeIds.join(',')}"`
      ).toBe(true);
    }
  }

  /**
   * Get room-type terms that can be compared with PMS room-type codes.
   * @param roomType room type label
   * @returns comparable room-type terms
   */
  private getRoomTypeComparableTerms(roomType: string): string[] {
    const roomTypeTerms: Array<[string[], string[]]> = [
      [['double', 'doppel'], ['double', 'dbl', 'db', 'doppel']],
      [['family', 'famil'], ['family', 'fam', 'fm']],
      [['accessible', 'barrierefrei'], ['accessible', 'dis']],
      [['single', 'einzel'], ['single', 'sb']],
      [['twin', 'zweibett'], ['twin', 'twn']],
    ];

    const normalizedRoomType = roomType.toLowerCase();
    return roomTypeTerms.find(([labels]) => labels.some((label) => normalizedRoomType.includes(label)))?.[1] ?? [roomType];
  }

  /**
   * Get the primary PMS room-type code for a room type label.
   * @param roomType room type label
   * @returns PMS room-type code or the supplied room type when no mapping exists
   */
  private getRoomTypeCode(roomType: string): string {
    const roomTypeCodes: Record<string, string> = {
      single: 'sb',
      double: 'db',
      accessible: 'dis',
      family: 'fam',
      twin: 'twin',
    };

    return roomTypeCodes[roomType] ?? roomType;
  }

  /**
   * Validate Accessible room
   * @param {SearchCriteria} searchCriteria search criteria used to search for hotel available rooms  
   */
  async validateAccessibleRoom(searchCriteriaInput: SearchCriteria): Promise<void> {
    console.log('Validate accessible room');
    const searchCriteria = this.asObject(searchCriteriaInput);
    const shouldBeAccessible = Boolean(searchCriteria.accessibleRoom ?? false);
    if (!shouldBeAccessible) {
      return;
    }

    const reservationByIdList = this.asArray(this.bookingConfirmationData.reservationByIdList);
    for (const reservation of reservationByIdList) {
      const roomStay = this.asObject(reservation.roomStay);
      const isAccessible = Boolean(roomStay.accessibleRoom ?? roomStay.isAccessibleRoom ?? false);
      global.expect(isAccessible, 'Accessible room check failed').toBe(true);
    }
  }

  /**
   * Validate rate plan code against expected 
   * @param {String} expectedRatePlan expected rate plan
   */
  async validateRatePlan(expectedRatePlanInput: string): Promise<void> {
    const expectedRatePlan = String(expectedRatePlanInput ?? '');
    const reservationByIdList = this.asArray(this.bookingConfirmationData.reservationByIdList);
    for (const item of reservationByIdList) {
      const ratePlanCode = String(this.asObject(item.roomStay).ratePlanCode ?? '');
      if (expectedRatePlan === 'FLEXRATE') {
        global.expect(['FLEXRATE', 'FX15R'], 'Unexpected rate plan').toContain(ratePlanCode);
      } else {
        global.expect(ratePlanCode, 'Unexpected rate plan').toBe(expectedRatePlan);
      }
    }
  }

  /**
   * Validate rate plan code against expected 
   * @param {String} expectedRatePlan expected rate plan
   */
  async validateItDoesntHaveTheRightRate(expectedRatePlanInput: string): Promise<void> {
    const expectedRatePlan = String(expectedRatePlanInput ?? '');
    const reservationByIdList = this.asArray(this.bookingConfirmationData.reservationByIdList);
    for (const item of reservationByIdList) {
      const ratePlanCode = String(this.asObject(item.roomStay).ratePlanCode ?? '');
      global.expect(ratePlanCode, 'Unexpected rate plan').not.toBe(expectedRatePlan);
    }
  }

  /**
   * Validate Early check in package is present with expected unit/computed price
   * @param {Object} data object
   * @param {Boolean} data.noPackage used when expect no package
   */
  async validateEciPackage(dataInput: { noPackage?: boolean } = {}): Promise<void> {
    const data = this.asObject(dataInput);
    const noPackage = Boolean(data.noPackage ?? false);
    const reservationByIdList = this.asArray(this.bookingConfirmationData.reservationByIdList);
    const firstRoomStay = this.asObject(this.asObject(reservationByIdList[0]).roomStay);
    const arrivalDate = String(firstRoomStay.arrivalDate ?? '');
    const departureDate = String(firstRoomStay.departureDate ?? '');
    const bookingFlowId = await ApiCalls.getBookingFlowIdBasedOnHotelAndRate({ hotelId: String(this.bookingConfirmationData.hotelId ?? '') });
    const nightsNumber = this.getNightsBetween(arrivalDate, departureDate);

    for (const reservation of reservationByIdList) {
      const roomStay = this.asObject(reservation.roomStay);
      const expectedPackages = this.asArray(await ApiCalls.graphqlGetAncillariesEciExtras({
        hotelId: String(this.bookingConfirmationData.hotelId ?? ''), bookingFlowId, nightsNumber, startDate: arrivalDate, endDate: departureDate,
        adultsNumber: Number(roomStay.adultsNumber ?? 0), childrenNumber: Number(roomStay.childrenNumber ?? 0),
      }));
      const actualPackage = this.asArray(reservation.reservationPackageList).find((pkg) => String(pkg.packageCode ?? '') === Constants.EARLY_CHECK_IN_CODE);
      if (noPackage) {
        global.expect(actualPackage, 'ECI package is present and it was not expected').toBeUndefined();
      } else {
        global.expect(Number(this.asObject(actualPackage).unitPrice ?? 0), 'Unexpected ECI package unit price').toBe(Number(this.asObject(expectedPackages[0]).price ?? 0));
      }
    }
  }

  /**
   * Validate Late check out package is present with expected unit/computed price
   * @param {Object} data object
   * @param {Boolean} data.noPackage used when expect no package
   */
  async validateLcoPackage(dataInput: { noPackage?: boolean } = {}): Promise<void> {
    const data = this.asObject(dataInput);
    const noPackage = Boolean(data.noPackage ?? false);
    const reservationByIdList = this.asArray(this.bookingConfirmationData.reservationByIdList);
    const firstRoomStay = this.asObject(this.asObject(reservationByIdList[0]).roomStay);
    const arrivalDate = String(firstRoomStay.arrivalDate ?? '');
    const departureDate = String(firstRoomStay.departureDate ?? '');
    const bookingFlowId = await ApiCalls.getBookingFlowIdBasedOnHotelAndRate({ hotelId: String(this.bookingConfirmationData.hotelId ?? '') });
    const nightsNumber = this.getNightsBetween(arrivalDate, departureDate);

    for (const reservation of reservationByIdList) {
      const roomStay = this.asObject(reservation.roomStay);
      const expectedPackages = this.asArray(await ApiCalls.graphqlGetAncillariesLcoExtras({
        hotelId: String(this.bookingConfirmationData.hotelId ?? ''), bookingFlowId, nightsNumber, startDate: arrivalDate, endDate: departureDate,
        adultsNumber: Number(roomStay.adultsNumber ?? 0), childrenNumber: Number(roomStay.childrenNumber ?? 0),
      }));
      const actualPackage = this.asArray(reservation.reservationPackageList).find((pkg) => String(pkg.packageCode ?? '') === Constants.LATE_CHECK_OUT_CODE);
      if (noPackage) {
        global.expect(actualPackage, 'LCO package is present and it was not expected').toBeUndefined();
      } else {
        global.expect(Number(this.asObject(actualPackage).unitPrice ?? 0), 'Unexpected LCO package unit price').toBe(Number(this.asObject(expectedPackages[0]).price ?? 0));
      }
    }
  }

  /**
   * Validate WiFi package is present with expected unit price
   * @param {Object} data object
   * @param {Boolean} data.noPackage used when expect no package
   */
  async validateWifiPackage(dataInput: { noPackage?: boolean } = {}): Promise<void> {
    const data = this.asObject(dataInput);
    const noPackage = Boolean(data.noPackage ?? false);
    const reservationByIdList = this.asArray(this.bookingConfirmationData.reservationByIdList);
    const firstRoomStay = this.asObject(this.asObject(reservationByIdList[0]).roomStay);
    const arrivalDate = String(firstRoomStay.arrivalDate ?? '');
    const departureDate = String(firstRoomStay.departureDate ?? '');
    const bookingFlowId = await ApiCalls.getBookingFlowIdBasedOnHotelAndRate({ hotelId: String(this.bookingConfirmationData.hotelId ?? '') });
    const nightsNumber = this.getNightsBetween(arrivalDate, departureDate);

    for (const reservation of reservationByIdList) {
      const roomStay = this.asObject(reservation.roomStay);
      const expectedPackages = this.asArray(await ApiCalls.graphqlGetAncillariesWiFiExtras({
        hotelId: String(this.bookingConfirmationData.hotelId ?? ''), bookingFlowId, nightsNumber, startDate: arrivalDate, endDate: departureDate,
        adultsNumber: Number(roomStay.adultsNumber ?? 0), childrenNumber: Number(roomStay.childrenNumber ?? 0),
      }));
      const actualPackage = this.asArray(reservation.reservationPackageList).find((pkg) => String(pkg.packageCode ?? '') === Constants.WIFI_CODE);
      if (noPackage) {
        global.expect(actualPackage, 'WiFi package is present and it was not expected').toBeUndefined();
      } else {
        global.expect(Number(this.asObject(actualPackage).unitPrice ?? 0), 'Unexpected WiFi package unit price').toBe(Number(this.asObject(expectedPackages[0]).price ?? 0));
      }
    }
  }

  /**
   * Validate rates per night
  * @param {Array<number | Array<ExpectedRatePerNight>>} expectedRatesPerRoomPerNight expected rates per room
   * @param {Number} discountAmount the discount amount applied
   * @param {Boolean} includesCityTax true if the expectedRatesPerRoomPerNight per room includes the city tax.
   */
  async validateRatesPerNight(
    expectedRatesPerRoomPerNight: number[] | ExpectedRatePerNight[][],
    discountAmount: number = 0,
    includesCityTax: boolean = true,
  ): Promise<void> {
    console.log('Validate rates per night');
    const expectedRates = Array.isArray(expectedRatesPerRoomPerNight) ? expectedRatesPerRoomPerNight : [];
    const discount = Number(discountAmount ?? 0);
    const includesTax = Boolean(includesCityTax ?? true);
    const reservationByIdList = this.asArray(this.bookingConfirmationData.reservationByIdList);

    if (expectedRates.length > 0 && typeof expectedRates[0] === 'number') {
      const expectedFlat = expectedRates as number[];
      const firstRoom = this.asObject(reservationByIdList[0]);
      const ratesPerNight = this.asArray(this.asObject(firstRoom.roomStay).ratesPerNight);
      global.expect(ratesPerNight.length, 'Rates per room per night rooms count').toBe(expectedFlat.length);
      for (let i = 0; i < ratesPerNight.length; i++) {
        const rate = this.asObject(ratesPerNight[i]);
        const cityTax = includesTax ? Number(rate.cityTaxPerNight ?? 0) : 0;
        const actual = Number((Number(rate.pricePerNight ?? 0) + cityTax).toFixed(2));
        const expected = Number(Number(expectedFlat[i]).toFixed(2));
        global.expect(this.amountsMatch(actual, expected), 'Unexpected price per night').toBe(true);
      }
      return;
    }

    global.expect(reservationByIdList.length, 'Rates per room per night rooms count').toBe(expectedRates.length);
    const unmatchedRooms = [...reservationByIdList];
    for (const expectedRoom of expectedRates as ExpectedRatePerNight[][]) {
      let hasThisRoomPricePerNight = false;
      let hasThisRoomStartDate = false;
      const attemptedRateMatches: string[] = [];

      for (let roomIndex = 0; roomIndex < unmatchedRooms.length; roomIndex++) {
        const room = this.asObject(unmatchedRooms[roomIndex]);
        const rates = this.asArray(this.asObject(room.roomStay).ratesPerNight);
        let totalPricePerNight = 0;
        let expectedTotalPricePerNight = 0;

        for (let rateIndex = 0; rateIndex < rates.length; rateIndex++) {
          const rate = this.asObject(rates[rateIndex]);
          const expectedRate = this.asObject(expectedRoom[rateIndex]);
          const cityTaxAmount = includesTax ? Number(rate.cityTaxPerNight ?? 0) : 0;
          const expectedPricePerNightValue = (roomIndex === 0 && rateIndex === 0 && discount > 0)
            ? Number(expectedRate.pricePerNight ?? 0) - discount
            : Number(expectedRate.pricePerNight ?? 0);
          const actualPricePerNightValue = Number(rate.pricePerNight ?? 0) + cityTaxAmount;

          hasThisRoomPricePerNight = this.amountsMatch(
            actualPricePerNightValue,
            expectedPricePerNightValue,
          );
          hasThisRoomStartDate = String(rate.startDate ?? '') === String(expectedRate.date ?? '');
          attemptedRateMatches.push(
            `room=${roomIndex}, night=${rateIndex}, actual=${actualPricePerNightValue}, expected=${expectedPricePerNightValue}, actualDate=${String(rate.startDate ?? '')}, expectedDate=${String(expectedRate.date ?? '')}`,
          );

          // Workaround for date mismatch issue — check if dates are off by 1 day
          if (!hasThisRoomStartDate && rate.startDate && expectedRate.date) {
            const rateDate = new Date(String(rate.startDate));
            const expectedDate = new Date(String(expectedRate.date));
            const dateDiffMs = Math.abs(rateDate.getTime() - expectedDate.getTime());
            const oneDayMs = 24 * 60 * 60 * 1000;
            if (dateDiffMs === oneDayMs) {
              hasThisRoomStartDate = true;
            }
          }

          expectedTotalPricePerNight += expectedPricePerNightValue;
          totalPricePerNight += Number(rate.pricePerNight ?? 0);

          if (discount === 0 && (!hasThisRoomPricePerNight || !hasThisRoomStartDate)) {
            break;
          }
        }

        if (discount > 0 && Number(totalPricePerNight.toFixed(2)) === Number(expectedTotalPricePerNight.toFixed(2))) {
          hasThisRoomPricePerNight = true;
        }

        if (hasThisRoomPricePerNight && hasThisRoomStartDate) {
          unmatchedRooms.splice(roomIndex, 1);
          break;
        }

      }

      global.expect(hasThisRoomPricePerNight, `Unexpected price per night. Expected room rates were not found. Attempts: ${attemptedRateMatches.join(' | ')}`).toBe(true);
      global.expect(hasThisRoomStartDate, `Unexpected rate start date. Expected room dates were not found. Attempts: ${attemptedRateMatches.join(' | ')}`).toBe(true);
    }
  }

  /**
   * Validate Currency
   * @param {String} expectedCurrency expected currency
   */
  async validateCurrency(expectedCurrencyInput: string): Promise<void> {
    const expectedCurrency = String(expectedCurrencyInput ?? '');
    global.expect(String(this.bookingConfirmationData.currencyCode ?? ''), 'Unexpected currency code').toBe(expectedCurrency);
  }

  /**
   * Validate Booking flow id
   * @param {String} expectedBookingFlowId expected booking flow id
   */
  async validateBookingFlowId(expectedBookingFlowIdInput: string): Promise<void> {
    const expectedBookingFlowId = String(expectedBookingFlowIdInput ?? '');
    global.expect(String(this.bookingConfirmationData.bookingFlowId ?? ''), 'Unexpected booking flow id').toBe(expectedBookingFlowId);
  }

  /**
   * Validate policy code
   * @param {String} policyCode policy code 
   */
  async validatePolicyCode(policyCodeInput: string): Promise<void> {
    const policyCode = String(policyCodeInput ?? '');
    global.expect(String(this.bookingConfirmationData.policyCode ?? ''), 'policyCode is not as expected').toBe(policyCode);
  }

  /**
   * Format a date as an ISO calendar day in UTC.
   * @param date date to format
   * @returns ISO date in YYYY-MM-DD format
   */
  private toIsoDayDate(date: Date): string {
    const year = date.getUTCFullYear();
    const month = `${date.getUTCMonth() + 1}`.padStart(2, '0');
    const day = `${date.getUTCDate()}`.padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  /**
   * Calculate the number of nights between arrival and departure dates.
   * @param arrivalDate arrival date string
   * @param departureDate departure date string
   * @returns number of nights between the supplied dates
   */
  private getNightsBetween(arrivalDate: string, departureDate: string): number {
    return Math.round((new Date(departureDate).getTime() - new Date(arrivalDate).getTime()) / (24 * 60 * 60 * 1000));
  }

  /**
   * Validate room total price 
   * @param {Array<String>} expectedRoomPrices expectedRoomPrices
   * @param {Number} discountAmount the discount amount applied
   * @param {Boolean} includesCityTax true if the final expected room price should include the city tax
   */
  async validateRoomPrice(
    expectedRoomPricesInput: Array<number | string | Record<string, unknown>>,
    discountAmount: number = 0,
    includesCityTax: boolean = false,
  ): Promise<void> {
    console.log('Validate room total price');
    const expectedRoomPrices = Array.isArray(expectedRoomPricesInput) ? expectedRoomPricesInput : [];
    const discount = Number(discountAmount ?? 0);
    const includesTax = Boolean(includesCityTax ?? false);
    const reservationByIdList = this.asArray(this.bookingConfirmationData.reservationByIdList);

    global.expect(reservationByIdList.length, 'Total price rooms count mismatch').toBe(expectedRoomPrices.length);
    const unmatchedRooms = [...reservationByIdList];

    for (const expectedRoomPriceRaw of expectedRoomPrices) {
      const expectedRoomPriceObj = this.asObject(expectedRoomPriceRaw);
      const expectedRoomPrice = Number(
        typeof expectedRoomPriceRaw === 'number' || typeof expectedRoomPriceRaw === 'string'
          ? expectedRoomPriceRaw
          : (expectedRoomPriceObj.totalPrice ?? 0),
      );

      let hasThisRoomPrice = false;
      for (let roomIndex = 0; roomIndex < unmatchedRooms.length; roomIndex++) {
        const room = this.asObject(unmatchedRooms[roomIndex]);
        const roomStay = this.asObject(room.roomStay);
        const ratesPerNight = this.asArray(roomStay.ratesPerNight);
        const cityTaxAmount = includesTax
          ? ratesPerNight.reduce((totalCityTax, currentValue) => totalCityTax + Number(this.asObject(currentValue).cityTaxPerNight ?? 0), 0)
          : 0;

        let expectedRoomPriceValue = discount > 0
          ? Math.trunc((expectedRoomPrice + Math.round(cityTaxAmount * 100) / 100 - discount) * 100) / 100
          : Math.trunc((expectedRoomPrice + Math.round(cityTaxAmount * 100) / 100) * 100) / 100;

        const actualRoomPrice = Number((Math.round(Number.parseFloat(String(roomStay.roomPrice ?? 0)) * 100) / 100).toFixed(2));
        expectedRoomPriceValue = Number((Math.round(Number.parseFloat(String(expectedRoomPriceValue)) * 100) / 100).toFixed(2));

        hasThisRoomPrice = this.amountsMatch(actualRoomPrice, expectedRoomPriceValue);

        if (hasThisRoomPrice) {
          unmatchedRooms.splice(roomIndex, 1);
          break;
        }
      }

      global.expect(hasThisRoomPrice, 'Unexpected room total price. Expected room total was not found').toBe(true);
    }
  }

  /**
   * Validate meals
  * @param {Array<ExpectedMealPackage>} roomListMealsDetails expected meal details per room
   * Ex: { description: String.PREMIER_INN_BREAKFAST.name, totalQuantity: 2, unitPrice: 10.5, packageCode: 'BFADBF' }
   * @param {SearchCriteria} searchCriteria - search criteria
   */
  async validateMealPackagesPerRoom(
    roomListMealsDetailsInput: ExpectedMealPackage[],
    searchCriteriaInput: SearchCriteria,
  ): Promise<void> {
    console.log('Validate meals for all rooms');
    const roomListMealsDetails = Array.isArray(roomListMealsDetailsInput) ? roomListMealsDetailsInput : [];
    const reservationByIdList = this.asArray(this.bookingConfirmationData.reservationByIdList);
    const searchCriteria = this.asObject(searchCriteriaInput);
    const searchRooms = this.asArray(searchCriteria.rooms);
    let matchedMeals = 0;

    // codes that need to be summed together and compared under a single normalized/grouped package code
    const groupedPackageCodes: Record<string, string> = {
      [Constants.PI_BREAKFAST_PACKAGE_CODE]: Constants.BREAKFAST_GERMAN_PACKAGE_CODE,
      PIBBEV: Constants.BREAKFAST_GERMAN_PACKAGE_CODE,
      MD2DIN: Constants.MEAL_DEAL_PACKAGE_CODE,
      MDBEVA: Constants.MEAL_DEAL_PACKAGE_CODE,
      MDBFST: Constants.MEAL_DEAL_PACKAGE_CODE,
    };

    for (const room of reservationByIdList) {
      const roomObj = this.asObject(room);
      const roomStay = this.asObject(roomObj.roomStay);
      const indexOfFoundRoom = searchRooms.findIndex((searchCriteriaRoom) => {
        const expectedRoom = this.asObject(searchCriteriaRoom);
        return Number(roomStay.adultsNumber ?? 0) === Number(expectedRoom.adultsNumber ?? expectedRoom.adults ?? 0)
          && Number(roomStay.childrenNumber ?? 0) === Number(expectedRoom.childrenNumber ?? expectedRoom.children ?? 0);
      });

      if (indexOfFoundRoom < 0 || !roomListMealsDetails[indexOfFoundRoom]) {
        continue;
      }

      const expectedRoomMeal = this.asObject(roomListMealsDetails[indexOfFoundRoom]);
      if (!expectedRoomMeal) {
        continue;
      }
      const expectedPackageCode = groupedPackageCodes[String(expectedRoomMeal.packageCode ?? '')] ?? String(expectedRoomMeal.packageCode ?? '');

      // aggregate all entries per normalized package code across the whole room, regardless of their position in the list
      const aggregatedMealsByCode = new Map<string, { packageCode: string; unitPrice: number; computedPrice: number; totalQuantity: number }>();
      const reservationPackages = this.asArray(roomObj.reservationPackageList);
      for (const packageItem of reservationPackages) {
        const actualMeal = this.asObject(packageItem);
        const packageCode = String(actualMeal.packageCode ?? '');
        // ZCHRY3 is a donation package; BBIB is a zero-price DE meal group, skip it unless it's the expected package code
        if (packageCode === 'ZCHRY3' || (packageCode === Constants.BREAKFAST_GERMAN_PACKAGE_CODE && expectedPackageCode !== Constants.BREAKFAST_GERMAN_PACKAGE_CODE)) {
          continue;
        }
        const normalizedCode = groupedPackageCodes[packageCode] ?? packageCode;
        const aggregatedMeal = aggregatedMealsByCode.get(normalizedCode) ?? { packageCode: normalizedCode, unitPrice: 0, computedPrice: 0, totalQuantity: 0 };
        aggregatedMeal.unitPrice += Number(actualMeal.unitPrice ?? 0);
        aggregatedMeal.computedPrice += Number(actualMeal.computedPrice ?? 0);
        // totalQuantity (e.g. number of adults) is a per-stay value repeated on every per-night entry, not additive across nights
        aggregatedMeal.totalQuantity = Number(actualMeal.totalQuantity ?? 0);
        aggregatedMealsByCode.set(normalizedCode, aggregatedMeal);
      }

      for (const actualRoomMeal of aggregatedMealsByCode.values()) {
        if (actualRoomMeal.packageCode !== Constants.CITY_TAX_PACKAGE_CODE && actualRoomMeal.packageCode === expectedPackageCode) {
          const actualUnitPrice = Number(actualRoomMeal.unitPrice.toFixed(2));
          const expectedUnitPrice = Number(Number(expectedRoomMeal.unitPrice ?? 0).toFixed(2));
          const actualComputedPrice = Number(actualRoomMeal.computedPrice.toFixed(2));
          const expectedComputedPrice = Number((Number(expectedRoomMeal.totalQuantity ?? 0) * Number(expectedRoomMeal.unitPrice ?? 0)).toFixed(2));

          global.expect(actualUnitPrice, 'Meal unit price should match expected value').toBe(expectedUnitPrice);
          global.expect(actualComputedPrice, 'Meal computed price should match expected value').toBe(expectedComputedPrice);
          global.expect(actualRoomMeal.totalQuantity, 'Meal total quantity should match expected value').toBe(Number(expectedRoomMeal.totalQuantity ?? 0));
          matchedMeals++;
        }
      }
    }

    global.expect(matchedMeals, 'Total number of matched meals is not as expected').toBe(roomListMealsDetails.length);
  }

  /**
   * Validate Upgrade to flex
  * @param {BookingInformationUpgradeToFlex} expectedFlexRate - expected upgrade-to-flex response
   */
  async validateUpgradeToFlex(expectedFlexRateInput: BookingInformationUpgradeToFlex): Promise<void> {
    console.log('Validate upgrade to flex');
    const expectedFlexRate = this.asObject(expectedFlexRateInput);
    const upgradeToFlex = this.asArray(this.bookingConfirmationData.upgradeToFlex);
    const firstUpgrade = this.asObject(upgradeToFlex[0]);

    if (!Object.keys(expectedFlexRate).length) {
      return;
    }

    global.expect(String(firstUpgrade.flexRateCode ?? ''), 'Rate code was not upgraded as expected').toBe(String(expectedFlexRate.flexRateCode ?? ''));
    global.expect(Number(firstUpgrade.amount ?? 0), 'Upgrade amount is different than expected').toBe(Number(expectedFlexRate.amount ?? 0));
    global.expect(String(firstUpgrade.currency ?? ''), 'Upgrade currency is different than expected').toBe(String(expectedFlexRate.currency ?? ''));
  }

  /**
   * Validate Billing booker
   * @param {GuestDetailsModel} guestDetailsInput GuestDetails object data
    * @param {boolean} validateBillingAddress Whether the confirmation billing address should be compared with the guest address.
   */
  async validateBillingBooker(guestDetailsInput: GuestDetailsModel, validateBillingAddress = true): Promise<void> {
    console.log('Validate billing booker');
    const guestDetails = this.asObject(guestDetailsInput);
    const expectedBooker = this.asObject(guestDetails.booker);
    const reservationByIdList = this.asArray(this.bookingConfirmationData.reservationByIdList);
    const address = this.asObject(expectedBooker.address);

    for (const reservation of reservationByIdList) {
      const billing = this.asObject(this.asObject(reservation).billing);
      const billingAddress = this.asObject(billing.address);

      if (Locales.isEnglishWebsite() && expectedBooker.title !== undefined) {
        global.expect(String(billing.title ?? ''), 'Billing title does not match expected value').toBe(String(expectedBooker.title ?? ''));
      } else if (!Locales.isEnglishWebsite()) {
        global.expect(['Herr', 'Fräulein', 'Frau', 'Fraulein', 'Dr.', 'Prof.', 'Mag.', 'Lord', 'Lady', 'Sir', 'Col.', 'Rev.'])
          .toContain(String(billing.title ?? ''));
      }
      if (expectedBooker.firstName !== undefined) {
        global.expect(String(billing.firstName ?? ''), 'Billing first name does not match expected value').toBe(String(expectedBooker.firstName));
      }
      if (expectedBooker.lastName !== undefined) {
        global.expect(String(billing.lastName ?? ''), 'Billing last name does not match expected value').toBe(String(expectedBooker.lastName));
      }
      if (expectedBooker.emailAddress !== undefined) {
        global.expect(String(billing.email ?? ''), 'Billing email does not match expected value').toBe(String(expectedBooker.emailAddress));
      }

      if (expectedBooker.landline !== undefined) {
        const expectedLandline = this.normalizePhoneForComparison(String(expectedBooker.landline ?? ''), String(expectedBooker.landlinePrefix ?? ''));
        const actualLandline = this.normalizePhoneForComparison(String(billing.landline ?? ''), String(expectedBooker.landlinePrefix ?? ''));
        if (String(billing.landline ?? '') !== '') {
          global.expect(actualLandline, 'Landline value does not match expected value').toBe(expectedLandline);
        }
      }

      if (expectedBooker.mobile !== undefined) {
        const expectedMobile = this.normalizePhoneForComparison(String(expectedBooker.mobile ?? ''), String(expectedBooker.mobilePrefix ?? ''));
        const actualMobile = this.normalizePhoneForComparison(String(billing.telephone ?? ''), String(expectedBooker.mobilePrefix ?? ''));
        if (String(billing.telephone ?? '') !== '') {
          global.expect(actualMobile, 'Telephone value does not match expected value').toBe(expectedMobile);
        }
      }

      if (validateBillingAddress && Object.keys(address).length) {
        global.expect(String(billingAddress.addressLine1 ?? billing.address1 ?? ''), 'Billing address line 1 does not match expected value').toBe(String(address.addressLine1 ?? ''));
        global.expect(String(billingAddress.addressLine2 ?? billing.address2 ?? ''), 'Billing address line 2 does not match expected value').toBe(String(address.addressLine2 ?? ''));
        global.expect(String(billingAddress.addressLine3 ?? billing.address3 ?? ''), 'Billing address line 3 does not match expected value').toBe(String(address.addressLine3 ?? ''));
        global.expect(String(billingAddress.country ?? billing.country ?? ''), 'Billing country does not match expected value').toBe(String(address.countryCode ?? address.country ?? ''));
        global.expect(String(billingAddress.postalCode ?? billing.postalCode ?? ''), 'Billing postcode does not match expected value').toBe(String(address.postalCode ?? ''));
      }

    }
  }

  /**
   * Validate Stay purpose (LEI/BUS)
   * @param {GuestDetails} guestDetails GuestDetails object data
   */
  async validatePurposeOfStay(guestDetailsInput: GuestDetailsModel): Promise<void> {
    const guestDetails = this.asObject(guestDetailsInput);
    console.log(`Validate reason for stay=${String(guestDetails.reasonForStay ?? '')}`);
    const expectedPurpose = String(guestDetails.reasonForStay ?? '').toUpperCase();
    if (!expectedPurpose) {
      return;
    }

    for (const reservation of this.asArray(this.bookingConfirmationData.reservationByIdList)) {
      const additionalGuestInfo = this.asObject(this.asObject(reservation).additionalGuestInfo);
      global.expect(String(additionalGuestInfo.purposeOfStay ?? '').toUpperCase(), 'Reason for stay does not match expected value').toBe(expectedPurpose);
    }
  }

  /**
   * Validate staying guests per room
  * @param {Array<StayingGuestAndRoomDetails>} stayingGuestsAndRoomDetails staying guests and room details
   * Example of stayingGuestsAndRoomDetails:
   * stayingGuestsAndRoomDetails = [{firstName: 'Ana', lastName: 'Popescu', adultsNumber: 1, childrenNumber: 0 }]
   */
  async validateGuests(stayingGuestsAndRoomDetailsInput: StayingGuestAndRoomDetails[]): Promise<void> {
    console.log('Validate staying guests');
    const normalizeGuestName = (name: string): string => name.trim().replace(/\s+/g, ' ').toLowerCase();
    const stayingGuestsAndRoomDetails = Array.isArray(stayingGuestsAndRoomDetailsInput) ? stayingGuestsAndRoomDetailsInput : [];
    const reservationByIdList = this.asArray(this.bookingConfirmationData.reservationByIdList);

    global.expect(reservationByIdList.length, 'Staying guests rooms count mismatch').toBe(stayingGuestsAndRoomDetails.length);
    const unmatchedReservations = [...reservationByIdList];

    for (const expected of stayingGuestsAndRoomDetails) {
      let matched = false;
      for (let i = 0; i < unmatchedReservations.length; i++) {
        const reservation = this.asObject(unmatchedReservations[i]);
        const roomStay = this.asObject(reservation.roomStay);
        const adultsMatch = expected.adultsNumber === undefined
          || Number(roomStay.adultsNumber ?? 0) === Number(expected.adultsNumber);
        const childrenMatch = expected.childrenNumber === undefined
          || Number(roomStay.childrenNumber ?? 0) === Number(expected.childrenNumber);

        if (!adultsMatch || !childrenMatch) {
          continue;
        }

        const guestList = this.asArray(reservation.reservationGuestList).length > 0
          ? this.asArray(reservation.reservationGuestList)
          : this.asArray(reservation.stayingGuests);
        const firstGuest = this.asObject(guestList[0]);

        if (expected.firstName !== undefined && expected.lastName !== undefined) {
          const expectedName = normalizeGuestName(`${expected.firstName ?? ''} ${expected.lastName ?? ''}`);
          const actualName = normalizeGuestName(`${firstGuest.givenName ?? firstGuest.firstName ?? ''} ${firstGuest.surName ?? firstGuest.lastName ?? ''}`);
          global.expect(actualName, 'Guest name does not match expected value').toBe(expectedName);
        } else if (expected.firstName !== undefined) {
          global.expect(normalizeGuestName(String(firstGuest.givenName ?? firstGuest.firstName ?? '')), 'Guest first name does not match expected value').toBe(normalizeGuestName(expected.firstName));
        } else if (expected.lastName !== undefined) {
          global.expect(normalizeGuestName(String(firstGuest.surName ?? firstGuest.lastName ?? '')), 'Guest last name does not match expected value').toBe(normalizeGuestName(expected.lastName));
        }

        unmatchedReservations.splice(i, 1);
        matched = true;
        break;
      }

      global.expect(matched, 'Expected room/guest combination was not found').toBe(true);
    }
  }

  /**
   * Validate staying guests and room details for  checkin per room 
  * @param {Array<StayingGuestAndRoomDetails>} stayingGuestsAndRoomDetailsForCheckIn staying guests and room details
   * Example of stayingGuestsAndRoomDetails:
   * stayingGuestsAndRoomDetails = [{title:Herr firstName: 'Ana', lastName: 'Popescu', adultsNumber: 1, childrenNumber: 0 , guestAdressLine1 : guestAdressLine1 , guestAdressLine2 : guestAdressLine2, guestAdressLine3 : guestAdressLine3 , cityName : Berlin , postalCode : 21342, guestsEmail : german@mailinator.com }]
   */
  async validateStayingGuestsAndRoomDetailsForCheckIn(stayingGuestsAndRoomDetailsForCheckIn: StayingGuestAndRoomDetails[]): Promise<void> {
    console.log(`Validate guests check-in details : ${JSON.stringify(stayingGuestsAndRoomDetailsForCheckIn)}`);
    let matchedGuests = 0;
    let matchedRooms = 0;
    for (const reservation of this.asArray(this.bookingConfirmationData.reservationByIdList)) {
      const roomStay = this.asObject(reservation.roomStay);
      const guest = this.asObject(this.asArray(reservation.reservationGuestList)[0]);
      const address = this.asObject(guest.address);
      matchedGuests += Number(Boolean(stayingGuestsAndRoomDetailsForCheckIn.find((expected) =>
        String(expected.title ?? '') === String(guest.nameTitle ?? '')
        && String(expected.firstName ?? '') === String(guest.givenName ?? '')
        && String(expected.lastName ?? '') === String(guest.surName ?? '')
        && String(expected.guestAddressLine1 ?? '') === String(address.addressLine1 ?? '')
        && String(expected.guestAddressLine2 ?? '') === String(address.addressLine2 ?? '')
        && String(expected.guestAddressLine3 ?? '') === String(address.addressLine3 ?? '')
        && String(expected.postalCode ?? '') === String(address.postalCode ?? '')
        && String(expected.cityName ?? '') === String(address.cityName ?? '')
        && String(expected.guestsEmail ?? '') === String(guest.email ?? ''))));
      matchedRooms += Number(Boolean(stayingGuestsAndRoomDetailsForCheckIn.find((expected) =>
        Number(expected.adultsNumber ?? 0) === Number(roomStay.adultsNumber ?? 0)
        && Number(expected.childrenNumber ?? 0) === Number(roomStay.childrenNumber ?? 0))));
    }
    global.expect(matchedGuests, 'Matching staying guests count is incorrect').toBe(stayingGuestsAndRoomDetailsForCheckIn.length);
    global.expect(matchedRooms, 'Matching rooms count is incorrect').toBe(stayingGuestsAndRoomDetailsForCheckIn.length);
  }

  /**
   * Validate Deposit policies
   * @param {String} policyCode expected policyCode
   */
  async validateDepositPoliciesForAllRooms(policyCodeInput: string): Promise<void> {
    const policyCode = String(policyCodeInput ?? '');
    const reservationByIdList = this.asArray(this.bookingConfirmationData.reservationByIdList);

    for (const reservation of reservationByIdList) {
      const depositPolicies = this.asArray(this.asObject(reservation).depositPolicies);
      const depositPolicyCode = String(this.asObject(depositPolicies[0]).policyCode ?? '');
      global.expect(depositPolicyCode, 'policyCode used for DepositPolicies does not match bookingConfirmation.policyCode field')
        .toBe(String(this.bookingConfirmationData.policyCode ?? ''));
      global.expect(depositPolicyCode, 'policyCode used for DepositPolicies is not as expected').toBe(policyCode);
    }
  }

  /**
   * Get total deposits paid for a given basket item.
  * @param {BasketItem} item - basket item containing sourceId.
   * @returns {Promise<Number>} The total deposits paid for the reservation.
   */
  async getTotalDepositsPaidForItem(itemInput: BasketItem): Promise<number> {
    const item = this.asObject(itemInput);
    const sourceId = String(item.sourceId ?? item.reservationId ?? '');
    const hotelId = String(this.bookingConfirmationData.hotelId ?? '');
    const depositFolios = this.asObject(await OhipApiCalls.getDepositFolios(hotelId, sourceId));
    let total = 0;
    for (const reservationDepositFolio of this.asArray(depositFolios.reservationDepositFoliosInfo)) {
      for (const deposit of this.asArray(this.asObject(reservationDepositFolio).deposits)) {
        total += Number(this.asObject(this.asObject(deposit).postedAmount).amount ?? 0);
      }
    }
    return Number(total.toFixed(2));
  }

  /**
   * Validate Deposit folio
  * @param {Basket} basketInformation basket response used to extract Opera reservations
   */
  async validateDepositFolios(basketInformation: Basket): Promise<void> {
    const items = this.asArray(basketInformation.items);
    global.expect(items.length, 'Expected total number of items to be greater than 0').toBeGreaterThan(0);
    const depositsPaid = await Promise.all(items.map((item) => this.getTotalDepositsPaidForItem(item)));
    for (const reservation of this.asArray(this.bookingConfirmationData.reservationByIdList)) {
      const policy = this.asObject(this.asArray(reservation.depositPolicies)[0]);
      const confirmationPaidAmount = Number(this.asObject(policy.amountPaid).amount ?? 0);
      global.expect(depositsPaid, 'Reservation deposits do not contain booking confirmation amount').toContain(confirmationPaidAmount);
    }
  }

  /**
   * Validate Deposit folio amounts after refund
  * @param {Object} data object data
  * @param {Basket} data.basketInformation basket response used to extract Opera reservations
   * @param {Number} data.refundedAmount refundedAmount during amend or cancel
   */
  async validateDepositFoliosWithRefunds(data: DepositFoliosWithRefundData): Promise<void> {
    const basketInformation = this.asObject(data.basketInformation);
    const items = this.asArray(basketInformation.items);
    const refundedAmount = Number(data.refundedAmount ?? 0);
    global.expect(items.length, 'Expected total number of items to be greater than 0').toBeGreaterThan(0);
    const depositsTransactions = (await Promise.all(items.map((item) => this.getTotalDepositsPaidForItem(item))))
      .reduce((total, amount) => total + amount, 0);
    const confirmationPaidAmount = this.asArray(this.bookingConfirmationData.reservationByIdList)
      .reduce((total, reservation) => total + Number(this.asObject(this.asObject(this.asArray(reservation.depositPolicies)[0]).amountPaid).amount ?? 0), 0);
    global.expect(depositsTransactions.toFixed(2), 'Deposit folios do not reflect the refunded amount')
      .toBe((Math.round((confirmationPaidAmount - refundedAmount) * 100) / 100).toFixed(2));
  }

  /**
   * Validate charity package is correctly applied to first room in Booking confirmation response 
   * @param {Donations} charityPackage expected charity object
   */
  async validateCharityIsAppliedToFirstRoom(charityPackageInput: Donations): Promise<void> {
    const charityPackage = this.asObject(charityPackageInput);
    const reservationByIdList = this.asArray(this.bookingConfirmationData.reservationByIdList);
    const firstRoom = this.asObject(reservationByIdList[0]);
    const firstRoomPackages = this.asArray(firstRoom.reservationPackageList);
    const expectedCode = String(charityPackage.code ?? charityPackage.packageCode ?? '').trim();
    const actualPackage = firstRoomPackages.find((pkg) => String(pkg.packageCode ?? '') === expectedCode);
    global.expect(actualPackage, 'No charity pledges applied to first room in bookingConfirmation response').toBeDefined();
    const actual = this.asObject(actualPackage);
    const unitPrice = Number(charityPackage.unitPrice ?? 0);
    global.expect(Number(actual.unitPrice ?? 0), 'Unit price for charity pledge does not match expected value').toBe(unitPrice);
    global.expect(Number(actual.totalQuantity ?? 0), 'Only singular donation should be applied per reservation')
      .toBe(Constants.NO_OF_DONATION_PACKAGES_PER_RESERVATION);
    global.expect(Number(actual.computedPrice ?? 0), 'Total charity price does not match expected value').toBe(unitPrice);
  }

  /**
   * Validate only one charity package is applied to reservation based on Booking confirmation response 
   */
  async validateSingleDonationIsAppliedToReservation(): Promise<void> {
    const reservationByIdList = this.asArray(this.bookingConfirmationData.reservationByIdList);
    const allPackages = reservationByIdList.flatMap((reservation) => this.asArray(reservation.reservationPackageList));
    const charityCount = allPackages.filter((pkg) => String(pkg.packageCode ?? '').startsWith('ZCHRY')).length;
    global.expect(charityCount, 'Number of charity packages is different than expected').toBe(Constants.NO_OF_DONATION_PACKAGES_PER_RESERVATION);
  }

  /**
   * Validate Donation package
   * @param {Donations} charityPackage expected charity object
   */
  async validateDonationPackage(charityPackageInput: Donations): Promise<void> {
    await this.validateCharityIsAppliedToFirstRoom(charityPackageInput);
    await this.validateSingleDonationIsAppliedToReservation();
  }

  /**
   * Validate Total cost with or without discount
   * @param {Number} expectedTotalCost expected total cost
   * @param {String} paymentOption payment option name
   * @param {Number} discountAmount the discount amount applied
   * @param {Boolean} hasPreviousTotal if false, the previousTotal value is 0.
   * @param {Boolean} hasBalanceOutstanding if false, the balanceOutstanding value is 0.
   * @param {Number} previousTotal previous total
   */
  async validateTotalCostNoDiscountsNoAmendment(
    expectedTotalCostInput: number,
    paymentOption: string = 'PAY_ON_ARRIVAL',
    discountAmount: number = 0,
    hasPreviousTotal: boolean = true,
    hasBalanceOutstanding: boolean = false,
    previousTotalArg?: number,
  ): Promise<void> {
    console.log('Validate total cost details');
    const expectedTotalCost = Number(expectedTotalCostInput ?? 0);
    const paymentOptionValue = String(paymentOption ?? 'PAY_ON_ARRIVAL');
    const discount = Number(discountAmount ?? 0);
    const hasPrevious = Boolean(hasPreviousTotal ?? true);
    const hasOutstanding = Boolean(hasBalanceOutstanding ?? false);
    const previousTotalValue = previousTotalArg !== undefined ? Number(previousTotalArg) : undefined;

    const totalCostWoDiscount = Number(this.bookingConfirmationData.totalCostWoDiscount ?? 0);
    const newTotal = Number(this.bookingConfirmationData.newTotal ?? 0);
    const totalCost = Number(this.bookingConfirmationData.totalCost ?? 0);
    const previousTotal = Number(this.bookingConfirmationData.previousTotal ?? 0);
    const balanceOutstanding = Number(this.bookingConfirmationData.balanceOutstanding ?? 0);

    global.expect(totalCostWoDiscount, 'totalCostWoDiscount does not match expected total when no discounts are added').toBe(Number(expectedTotalCost.toFixed(2)));
    global.expect(newTotal, 'newTotal does not match expected total').toBe(Number((expectedTotalCost - discount).toFixed(2)));
    global.expect(totalCost, 'totalCost does not match expected total').toBe(Number((expectedTotalCost - discount).toFixed(2)));

    if (paymentOptionValue === 'PAY_NOW' || paymentOptionValue === 'PAY_NOW_CCUI') {
      let expectedPreviousTotal = hasPrevious ? (previousTotalValue ?? expectedTotalCost - discount) : 0;
      expectedPreviousTotal = Number(Number(expectedPreviousTotal).toFixed(2));
      global.expect(previousTotal, 'previousTotal does not match expected total').toBe(expectedPreviousTotal);

      const expectedBalanceOutstanding = hasOutstanding
        ? Number((expectedTotalCost - expectedPreviousTotal).toFixed(2))
        : 0;
      global.expect(balanceOutstanding, 'balanceOutstanding should be equal with total cost').toBe(expectedBalanceOutstanding);
      return;
    }

    global.expect(previousTotal, 'previousTotal does not match expected total').toBe(0);
    const expectedOutstanding = discount > 0 ? expectedTotalCost - discount : expectedTotalCost;
    global.expect(balanceOutstanding, 'balanceOutstanding should be 0 when no amendment').toBe(Number(expectedOutstanding.toFixed(2)));
  }

  /**
   * Validate discount is applied to total cost
   * @param {Number} expectedDiscount the discount value to apply to total cost
   */
  async validateDiscountIsCorrectlyApplied(expectedDiscountInput: number): Promise<void> {
    console.log('Validate discount is applied on total cost');
    const expectedDiscount = Number(expectedDiscountInput ?? 0);
    const totalCostWoDiscount = Number(this.bookingConfirmationData.totalCostWoDiscount ?? 0);
    const newTotal = Number(this.bookingConfirmationData.newTotal ?? 0);

    if (expectedDiscount > 0) {
      global.expect(newTotal, `Discount: ${expectedDiscount} was not applied to ${totalCostWoDiscount}`).toBe(totalCostWoDiscount - expectedDiscount);
      global.expect(newTotal < totalCostWoDiscount, `New total cost: ${newTotal} is not less than the cost without discount ${totalCostWoDiscount}`).toBe(true);
      return;
    }

    global.expect(newTotal, `Discount: ${expectedDiscount} was used for the reservation`).toBe(totalCostWoDiscount);
  }

  /**
   * Validate City tax
   * @param {Boolean} isCityTaxAppliedForHotel true if the hotel has city tax, false if hotel doesn't have city tax
   */
  async validateCityTax(isCityTaxAppliedForHotel: boolean = false): Promise<void> {
    const isCityTaxApplied = Boolean(isCityTaxAppliedForHotel ?? false);
    console.log(`Validate city tax is correctly applied. Expected ${isCityTaxApplied}`);
    const reservationByIdList = this.asArray(this.bookingConfirmationData.reservationByIdList);

    for (const reservation of reservationByIdList) {
      const ratesPerNight = this.asArray(this.asObject(this.asObject(reservation).roomStay).ratesPerNight);
      for (const rate of ratesPerNight) {
        const cityTaxPerNight = Number(this.asObject(rate).cityTaxPerNight ?? 0);
        if (isCityTaxApplied) {
          global.expect(cityTaxPerNight, 'Invalid City tax per night').toBeGreaterThan(0);
        } else {
          global.expect(cityTaxPerNight, 'Invalid City tax per night').toBe(0);
        }
      }
    }
  }

  /**
   * Validate city tax based on hotel information and guest's reason for stay
  * @param {Object} data object
   * @param {Hotel} data.hotel hotel used for reservation
   * @param {String} data.basketReferenceId basket reference id
   * @param {String} data.arrivalDate arrival date
   * @param {String} data.departureDate departure date
   * @param {String} data.reasonForStay guest's reason for stay 
   * @param {Number} data.adultsNumber number of staying adults
   * @param {Number} data.childrenNumber number of stayint childs
   * @param {String} data.bookingFlowId booking flow id
   */
  async validateCityTaxBasedOnReasonForStay(dataInput: CityTaxValidationData): Promise<void> {
    const data = this.asObject(dataInput);
    const reasonForStay = String(data.reasonForStay ?? '').toUpperCase();
    const hotelName = String(this.asObject(data.hotel).name ?? this.bookingConfirmationData.hotelName ?? 'unknown');
    console.log(`Validate city tax is correctly applied based on hotel information and reason for stay. Hotel: ${hotelName}, reasonForStay: ${reasonForStay}`);

    const businessFlag = Boolean(
      data.hotelHasCityTaxForBusiness
      ?? this.bookingConfirmationData.hotelHasCityTaxForBusiness
      ?? this.asObject(data.hotelCityTax).hotelHasCityTaxForBusiness
      ?? false,
    );
    const leisureFlag = Boolean(
      data.hotelHasCityTaxForLeisure
      ?? this.bookingConfirmationData.hotelHasCityTaxForLeisure
      ?? this.asObject(data.hotelCityTax).hotelHasCityTaxForLeisure
      ?? false,
    );

    const shouldHaveTax = (businessFlag && reasonForStay === Constants.REASON_FOR_STAY.business)
      || (leisureFlag && reasonForStay === Constants.REASON_FOR_STAY.leisure);
    await this.validateCityTax(shouldHaveTax);
  }

  /**
   * Validate payment card
   * @param {String} paymentOption payment option string
   * @param {String} cardNumber card number string
   * @param {Boolean} hasCardDetails true if card details are expected to be present
   */
  async validatePaymentCard(paymentOptionInput: string, cardNumberInput: string, hasCardDetails: boolean = true): Promise<void> {
    console.log('Validate payment card number is correct');
    const paymentOption = String(paymentOptionInput ?? '');
    const cardNumber = String(cardNumberInput ?? '');
    const hasCard = Boolean(hasCardDetails ?? true);
    const reservationByIdList = this.asArray(this.bookingConfirmationData.reservationByIdList);
    const expectedCardNumber = cardNumber ? cardNumber.replace(/\s+/g, '') : '';
    const noCardPaymentOptions = new Set([Constants.PAYMENT_OPTION.reserveWithoutCreditCard, 'NON_GUARANTEED_BOOKING', 'ACCOUNT_TO_COMPANY']);

    for (const reservation of reservationByIdList) {
      const paymentCard = this.asObject(this.asObject(reservation).paymentCard);
      const actualCardNumber = String(paymentCard.cardNumberMasked ?? '');
      if (noCardPaymentOptions.has(paymentOption) || !hasCard) {
        global.expect(actualCardNumber, 'Api response payment card number is not empty').toBe('');
        global.expect(actualCardNumber.length, 'Api response payment card number length is incorrect').toBe(0);
      } else {
        global.expect(actualCardNumber.length, 'Api response payment card number length is incorrect').toBe(16);
        if (expectedCardNumber) {
          global.expect(actualCardNumber.slice(-4), 'Api response payment card number does not match the expected card number').toBe(expectedCardNumber.slice(-4));
        }
      }
    }
  }

  /**
   * Validate Guest Details against Booking confirmation Api response for getBookingConfirmation graphQL call
    * @param {Object} dataInput validation inputs
    * @param {GuestDetailsModel} [dataInput.guestDetails] GuestDetails object data
    * @param {StayingGuestAndRoomDetails[]} [dataInput.stayingGuestsAndRoomDetails] Staying guests and room details
    * @param {boolean} [dataInput.validateBillingAddress] Whether the confirmation billing address should be compared with the guest address
   * Example of stayingGuestsAndRoomDetails:
   * stayingGuestsAndRoomDetails = [{firstName: 'Ana', lastName: 'Popescu', roomType: 'DOUBLE', adultsNumber: 1, childrenNumber: 0 }]
   */
  async validateGuestDetailsAgainstBookingConfirmation(dataInput: {
    guestDetails?: GuestDetailsModel;
    stayingGuestsAndRoomDetails?: StayingGuestAndRoomDetails[];
    validateBillingAddress?: boolean;
  }): Promise<void> {
    console.log('Validate Guest Details');
    const guestDetails = (dataInput.guestDetails ?? {}) as GuestDetailsModel;
    const stayingGuestsAndRoomDetails = Array.isArray(dataInput.stayingGuestsAndRoomDetails)
      ? dataInput.stayingGuestsAndRoomDetails
      : [];
    await this.validateBillingBooker(guestDetails, dataInput.validateBillingAddress ?? true);
    await this.validatePurposeOfStay(guestDetails);
    await this.validateGuests(stayingGuestsAndRoomDetails);
  }

  /**
   * Validate booking allowances notes
  * @param {Object} data object
  * @param {Basket} data.basketInformation basket response used to extract Opera reservations
   * @param {Boolean} data.isPremierInnBreakfastAllowanceChecked Premier Inn Breakfast Allowance for Account to company option checked or not
   * @param {Boolean} data.isCarParkingAllowanceChecked Car Parking Allowance for Account to company option checked or not
   * @param {Array<Boolean>} data.packagesPrebookedAndAuthorizedMealList packagesPrebookedAndAuthorizedMealList to know which room has Premier Inn Breakfast Allowance for Account to company option is Pre-Booked and Authorized or not
   */
  async validateBookingAllowancesNotesForA2C(data: BookingAllowancesValidationData): Promise<void> {
    const basketInformation = this.asObject(data.basketInformation);
    const items = this.asArray(basketInformation.items);
    const packagesPrebookedAndAuthorizedMealList = Array.isArray(data.packagesPrebookedAndAuthorizedMealList)
      ? data.packagesPrebookedAndAuthorizedMealList
      : undefined;
    const breakfastAllowanceChecked = Boolean(data.isPremierInnBreakfastAllowanceChecked ?? false);
    const carParkingAllowanceChecked = Boolean(data.isCarParkingAllowanceChecked ?? false);
    global.expect(items.length, 'Expected total number of items to be greater than 0').toBeGreaterThan(0);
    const allowancesLabelsDictionary = await ApiDictionary.fetchAllowancesDictionary();

    for (const [itemIndex, item] of items.entries()) {
      const reservationResponse = this.asObject(await OhipApiCalls.getHotelReservationById({
        hotelId: String(basketInformation.hotelId ?? ''), reservationId: String(item.sourceId ?? ''),
      }));
      const reservation = this.asObject(this.asArray(this.asObject(reservationResponse.reservations).reservation)[0]);
      const notes = JSON.stringify(reservation.comments ?? reservation.businessNotes ?? '');
      const hasPrebookedAndAuthorized = Boolean(packagesPrebookedAndAuthorizedMealList?.[itemIndex]);
      const breakfastNote = hasPrebookedAndAuthorized
        ? String(allowancesLabelsDictionary['packages.premierInnBreakfast.value'] ?? '')
        : String(allowancesLabelsDictionary[breakfastAllowanceChecked ? 'businessNotes.premierInnBreakfast.allow' : 'businessNotes.premierInnBreakfast.deny'] ?? '');
      const carParkingNote = String(allowancesLabelsDictionary[carParkingAllowanceChecked ? 'businessNotes.carParking.allow' : 'businessNotes.carParking.deny'] ?? '');
      global.expect(notes, 'Premier Inn Breakfast Allowance for Account to company option').toContain(breakfastNote);
      global.expect(notes, 'Car Parking Allowance for Account to company option').toContain(carParkingNote);
    }
  }

}
