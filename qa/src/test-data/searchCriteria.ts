import { HotelRates, type HotelRate } from './hotelRates';
import { Locations, type Location } from './locations';
import { Rooms, type Room } from './room';
import { Hotels, type HotelData } from './hotels';
import { Strings } from './strings';
import { ApiReservationCalls } from '../api/graphql/apiReservationCalls';
import { BookingChannel, HotelAvailabilityInput } from '../api/requests';
import type { HotelAvailability } from '../api/response';

type SearchCriteriaLocation = Location | HotelData;

interface GetSearchCriteriaAndHotelAvailabilityParams {
  hotelName: string;
  hotelId: string;
  hotelCity?: Location;
  daysNumberForArrivalDate?: number;
  daysNumber: number;
  rooms?: Room[];
  ratePlanCode?: string;
  pmsRoomType?: string | string[];
  loggedUser?: boolean;
  shouldHaveMeals?: boolean;
  randomStartDate?: boolean;
  shouldHaveEci?: boolean | null;
  shouldHaveLco?: boolean | null;
  eciLcoAllRooms?: boolean;
  bookingChannel?: BookingChannel;
  exactRatePlanCode?: boolean;
}

interface GetSearchCriteriaAndHotelAvailabilityV2Params {
  hotelName: string;
  hotelId: string;
  daysNumberForArrivalDate?: number;
  daysNumber: number;
  rooms?: Room[];
  ratePlanCode?: string;
}

interface GetHotelAvailabilityParams {
  daysNumberForArrivalDate?: number;
  pmsRoomType?: string;
  rooms?: Room[];
}

interface SearchCriteriaAndHotelAvailabilityResult {
  hotelAvailabilityResponse: HotelAvailability;
  searchCriteria: SearchCriteriaData;
}

/**
 * Search criteria parameters needed for hotel search flows.
 */
export interface SearchCriteria {
  arrivalDate: Date;
  departureDate: Date;
  nights: number;
  location: SearchCriteriaLocation;
  rate: HotelRate;
  rooms: Room[];
}

/** Search criteria parameters that are needed for performing hotels search */
export class SearchCriteriaData implements SearchCriteria {
  arrivalDate: Date;
  departureDate: Date;
  nights: number;
  location: SearchCriteriaLocation;
  rate: HotelRate;
  rooms: Room[];

  /**
   * SearchCriteria constructor
   * @param data.arrivalDate arrivalDate of the searchCriteria object
   * @param data.departureDate departureDate of the searchCriteria object
   * @param data.nights nights number of the searchCriteria object
   * @param data.location location of the searchCriteria object
   * @param data.rate rate type of the searchCriteria object
   * @param data.rooms rooms in the searchCriteria object
   */
  constructor({
    arrivalDate = new Date(),
    departureDate = SearchCriteriaData.addDaysDate(2),
    nights = 1,
    location = Locations.LONDON,
    rate = HotelRates.PI_FLEX,
    rooms = [Rooms.DOUBLE_1_ADULT_0_CHILDREN],
  }: Partial<SearchCriteria> = {}) {
    this.arrivalDate = arrivalDate;
    this.departureDate = departureDate;
    this.nights = nights;
    this.location = location;
    this.rate = rate;
    this.rooms = rooms;
  }

  static readonly SEARCH_CRITERIA_DEFAULT_LOCATION: SearchCriteria = new SearchCriteriaData({
    arrivalDate: new Date(),
    departureDate: SearchCriteriaData.addDaysDate(1),
    nights: 1,
    location: Locations.LONDON,
    rate: HotelRates.PI_FLEX,
    rooms: [Rooms.DOUBLE_1_ADULT_0_CHILDREN],
  });

  static readonly SEARCH_CRITERIA_DEFAULT_HOTEL: SearchCriteria = new SearchCriteriaData({
    arrivalDate: new Date(),
    departureDate: SearchCriteriaData.addDaysDate(1),
    nights: 1,
    location: Hotels.DEFAULT_HOTEL,
    rate: HotelRates.PI_FLEX,
    rooms: [Rooms.DOUBLE_1_ADULT_0_CHILDREN],
  });

  /**
   * Returns the adults count
   */
  getAdults(): number {
    let adults = 0;
    for (const room of this.rooms) {
      adults += room.adultsNumber;
    }
    return adults;
  }

  /**
   * Returns the children count
   */
  getChildren(): number {
    let children = 0;
    for (const room of this.rooms) {
      children += room.childrenNumber;
    }
    return children;
  }

  /**
   * Returns the UI formatted date
   * @param date the date for which to get the search date label
   * @returns the UI formatted date
   */
  static getFormattedDateValue(date: Date): string {
    const todayDate = new Date();
    const todayMonth = todayDate.getMonth();
    const todayDay = todayDate.getDate();
    const tomorrowDate = new Date(Date.now() + 24 * 60 * 60 * 1000);
    const tomorrowMonth = tomorrowDate.getMonth();
    const tomorrowDay = tomorrowDate.getDate();

    let datePickerValue = '';
    if (date.getMonth() === todayMonth) {
      if (date.getDate() === todayDay) {
        datePickerValue += 'Today';
      } else if (date.getMonth() === tomorrowMonth && date.getDate() === tomorrowDay) {
        datePickerValue += 'Tomorrow';
      } else {
        datePickerValue += SearchCriteriaData.formatDayDate(date);
      }
    } else if (date.getMonth() === tomorrowMonth && date.getDate() === tomorrowDay) {
      datePickerValue += 'Tomorrow';
    } else {
      datePickerValue += SearchCriteriaData.formatDayDate(date);
    }
    return datePickerValue;
  }

  /**
   * Returns the dates picker value displayed in search bar. For. e.g. 'Today  |  Tomorrow'
   */
  getDatesPickerValue(): string {
    return SearchCriteriaData.getFormattedDateValue(this.arrivalDate) + '  |  ' + SearchCriteriaData.getFormattedDateValue(this.departureDate);
  }

  /**
   * Returns the dates value displayed in search summaries. For. e.g. '10 Feb 2022  -  11 Feb 2022'
   */
  getSearchSummariesDatesValue(): string {
    const arrivalDateValue = SearchCriteriaData.formatDayDate(this.arrivalDate);
    const departureDateValue = SearchCriteriaData.formatDayDate(this.departureDate);
    return arrivalDateValue + ' – ' + departureDateValue;
  }

  /**
   * Returns the dates and rooms value displayed in search summaries. For. e.g. 'Mon 16 May 2022 – Thu 19 May 2022, 2 adults, 1 child, 1 Double room, 1 Family room'
   */
  async getSearchSummaryFullValue(): Promise<string> {
    const arrivalDateValue = SearchCriteriaData.formatDayNameDayDate(this.arrivalDate);
    const departureDateValue = SearchCriteriaData.formatDayNameDayDate(this.departureDate);
    return arrivalDateValue + ' – ' + departureDateValue + ', ' + (await this.getSearchSummariesRoomsValue());
  }

  /**
   * Returns the given date in the format: Wed 23 Mar 2022
   * @param date date
   * @returns the given date in the format: Wed 23 Mar 2022
   */
  static async getDateWithDayName(date: Date): Promise<string> {
    return SearchCriteriaData.formatDayNameDayDate(date);
  }

  /**
   * Returns the rooms picker value displayed in search bar. For. e.g. '1 adult, 1 room'
   */
  async getRoomsPickerValue(): Promise<string> {
    const adults = this.getAdults();
    const children = this.getChildren();
    let roomsPickerValue = `${adults} ${adults === 1 ? await Strings.ADULT_LOWER_CASE_GENERIC.name : await Strings.ADULTS_LOWER_CASE_GENERIC.name}`;
    roomsPickerValue += children === 0 ? '' : `, ${children} ${children === 1 ? await Strings.CHILD_LOWER_CASE.name : await Strings.CHILDREN_LOWER_CASE.name}`;
    roomsPickerValue += `, ${this.rooms.length} ${this.rooms.length === 1 ? await Strings.ROOM_LOWER_CASE.name : await Strings.ROOMS_LOWER_CASE.name}`;
    return roomsPickerValue;
  }

  /**
   * Returns the rooms value displayed in search summaries. For. e.g. '1 adult, 1 Double room'
   */
  async getSearchSummariesRoomsValue(): Promise<string> {
    const adults = this.getAdults();
    const children = this.getChildren();
    let roomsValue = `${adults} ${adults === 1 ? await Strings.ADULT_LOWER_CASE_GENERIC.name : await Strings.ADULTS_LOWER_CASE_GENERIC.name}`;
    roomsValue += children === 0 ? '' : `, ${children} ${children === 1 ? await Strings.CHILD_LOWER_CASE_GENERIC.name : await Strings.CHILDREN_LOWER_CASE_GENERIC.name}`;

    const aggregatedRooms: Record<string, { count: number; type: RoomType }> = {};
    for (const room of this.rooms) {
      const roomTypeName = await room.roomType.name.name;
      if (!aggregatedRooms[roomTypeName]) {
        aggregatedRooms[roomTypeName] = { count: 0, type: room.roomType };
      }
      aggregatedRooms[roomTypeName].count++;
    }

    const visitedRooms: Record<string, boolean> = {};
    for (const room of this.rooms) {
      const roomTypeName = await room.roomType.name.name;
      if (!visitedRooms[roomTypeName]) {
        const count = aggregatedRooms[roomTypeName].count;
        let displayRoomTypeName = roomTypeName;
        if (roomTypeName === await Strings.FAMILY.name) {
          displayRoomTypeName = String(Strings.FAMILY.data.default);
        }
        if (roomTypeName === await Strings.DOUBLE.name) {
          displayRoomTypeName = String(Strings.DOUBLE.data.default);
        }
        if (roomTypeName === await Strings.ACCESSIBLE.name) {
          displayRoomTypeName = String(Strings.ACCESSIBLE.data.default);
        }
        roomsValue += `, ${count} ${displayRoomTypeName} ${count === 1 ? await Strings.ROOM_LOWER_CASE.name : await Strings.ROOMS_LOWER_CASE.name}`;
        visitedRooms[roomTypeName] = true;
      }
    }
    return roomsValue;
  }

  /**
   * The function creates the array of Room objects based on the rooms from the searchCriteria object
   * @returns array with Room objects corresponding to the rooms objects from the searchCriteria
   */
  async getRoomArray(): Promise<Room[]> {
    return this.rooms.map((roomItem) => ({
      adultsNumber: roomItem.adultsNumber,
      childrenNumber: roomItem.childrenNumber,
      roomType: roomItem.roomType,
      cotRequired: roomItem.cotRequired,
    }));
  }

  /**
   * Updated search criteria parameters based on hotel availability with specified reservation days.
   * @param hotelName {string} - Name of the hotel
   * @param hotelId {string} - ID of the hotel
   * @param hotelCity {Location} - Optional city location of the hotel
   * @param daysNumberForArrivalDate {number} - Number of days from today for arrival date (default: 2)
   * @param daysNumber {number} - Number of days for the reservation
   * @param rooms {Room[]} - Array of room objects (default: [DOUBLE_1_ADULT_0_CHILDREN])
   * @param ratePlanCode {string} - Rate plan code (default: '')
   * @param pmsRoomType {string | string[]} - PMS room type or types (default: '')
   * @param loggedUser {boolean} - Whether the user is logged in (default: false)
   * @param shouldHaveMeals {boolean} - Whether meals should be included (default: true)
   * @param randomStartDate {boolean} - Whether to use random start date (default: true)
   * @param shouldHaveEci {boolean | null} - Whether to include ECI (default: null)
   * @param shouldHaveLco {boolean | null} - Whether to include LCO (default: null)
   * @param eciLcoAllRooms {boolean} - Whether ECI/LCO applies to all rooms (default: false)
   * @param bookingChannel {BookingChannel} - Booking channel (default: new BookingChannel())
  * @param exactRatePlanCode {boolean} - Only accept the requested rate plan code (default: false)
   * @returns {Promise<SearchCriteriaAndHotelAvailabilityResult>} - Search criteria and hotel availability response
   */
  static async getSearchCriteriaAndHotelAvailability({
    hotelName,
    hotelId,
    hotelCity,
    daysNumberForArrivalDate = 2,
    daysNumber,
    rooms = [Rooms.DOUBLE_1_ADULT_0_CHILDREN],
    ratePlanCode = '',
    pmsRoomType = '',
    loggedUser = false,
    shouldHaveMeals = true,
    randomStartDate = true,
    shouldHaveEci = null,
    shouldHaveLco = null,
    eciLcoAllRooms = false,
    bookingChannel = new BookingChannel(),
    exactRatePlanCode = false,
  }: GetSearchCriteriaAndHotelAvailabilityParams): Promise<SearchCriteriaAndHotelAvailabilityResult> {
    daysNumberForArrivalDate = randomStartDate
      ? SearchCriteriaData.generateRandomNumber(daysNumberForArrivalDate, daysNumberForArrivalDate + 20)
      : daysNumberForArrivalDate;
    const hotelAvailabilityInput = await HotelAvailabilityInput.createHotelAvailabilityInputWithInterval({
      hotelId,
      daysNumberForArrivalDate,
      daysNumber,
      rooms,
      bookingChannel,
      ratePlanCodes: [ratePlanCode],
    });
    const hotelAvailabilityResponse = await ApiReservationCalls.getHotelAvailability(
      hotelAvailabilityInput,
      ratePlanCode,
      pmsRoomType,
      loggedUser,
      shouldHaveMeals,
      shouldHaveEci,
      shouldHaveLco,
      eciLcoAllRooms,
      exactRatePlanCode,
    );
    const responseRatePlanCode = String((hotelAvailabilityResponse as any).ratePlanCode ?? ratePlanCode);
    const roomsList = rooms.map((room, index) => Rooms.updateRoomInformation(Rooms.createRoom(), {
      adultsNumber: room.adultsNumber,
      childrenNumber: room.childrenNumber,
      cotRequired: room.cotRequired,
      roomType: room.roomType,
      roomNumber: index + 1,
    }));
    const location = hotelCity ? hotelCity.suggestion : hotelName;
    const searchCriteria = new SearchCriteriaData({
      arrivalDate: new Date(String(hotelAvailabilityInput.arrival)),
      departureDate: new Date(String(hotelAvailabilityInput.departure)),
      nights: daysNumber,
      location: SearchCriteriaData.createLocation(location),
      rate: HotelRates.getHotelRateByRatePlanCode(responseRatePlanCode),
      rooms: roomsList,
    });

    return { hotelAvailabilityResponse, searchCriteria };
  }

  /**
   * Updated search criteria parameters based on V2 hotel availability with specified reservation days.
   * @param hotelName {string} - Name of the hotel
   * @param hotelId {string} - ID of the hotel
   * @param daysNumberForArrivalDate {number} - Number of days from today for arrival date (default: 1)
   * @param daysNumber {number} - Number of days for the reservation
   * @param rooms {Room[]} - Array of room objects (default: [DOUBLE_1_ADULT_0_CHILDREN])
   * @param ratePlanCode {string} - Rate plan code (default: '')
   * @returns {Promise<SearchCriteriaAndHotelAvailabilityResult>} - Search criteria and hotel availability response
   */
  static async getSearchCriteriaAndHotelAvailabilityV2({
    hotelName,
    hotelId,
    daysNumberForArrivalDate = 1,
    daysNumber,
    rooms = [Rooms.DOUBLE_1_ADULT_0_CHILDREN],
    ratePlanCode = '',
  }: GetSearchCriteriaAndHotelAvailabilityV2Params): Promise<SearchCriteriaAndHotelAvailabilityResult> {
    const hotelAvailabilityInput = await HotelAvailabilityInput.createHotelAvailabilityInputWithInterval({ hotelId, daysNumberForArrivalDate, daysNumber, rooms });
    const hotelAvailabilityResponse = await ApiReservationCalls.getHotelAvailability2(hotelAvailabilityInput);
    const roomRates = (hotelAvailabilityResponse as any).roomRates as Array<{ ratePlanCode: string }> | undefined;
    const responseRatePlanCode = ratePlanCode || roomRates?.[0]?.ratePlanCode || '';
    const roomsList = rooms.map((room, index) => Rooms.updateRoomInformation(Rooms.createRoom(), {
      adultsNumber: room.adultsNumber,
      childrenNumber: room.childrenNumber,
      cotRequired: room.cotRequired,
      roomType: room.roomType,
      roomNumber: index + 1,
    }));
    const searchCriteria = new SearchCriteriaData({
      arrivalDate: new Date(String(hotelAvailabilityInput.arrival)),
      departureDate: new Date(String(hotelAvailabilityInput.departure)),
      nights: daysNumber,
      location: SearchCriteriaData.createLocation(hotelName),
      rate: HotelRates.getHotelRateByRatePlanCode(responseRatePlanCode),
      rooms: roomsList,
    });

    return { hotelAvailabilityResponse, searchCriteria };
  }

  /**
   * Update this search criteria instance dates based on hotel availability.
   * @param daysNumberForArrivalDate {number} - Number of days from today for arrival date (default: 0)
   * @param pmsRoomType {string} - PMS room type (default: '')
   * @param rooms {Room[]} - Array of room objects (default: [DOUBLE_1_ADULT_0_CHILDREN])
   * @returns {Promise<void>}
   */
  async getHotelAvailability({ daysNumberForArrivalDate = 0, pmsRoomType = '', rooms = [Rooms.DOUBLE_1_ADULT_0_CHILDREN] }: GetHotelAvailabilityParams = {}): Promise<void> {
    const hotelAvailabilityInput = await HotelAvailabilityInput.createHotelAvailabilityInputWithInterval({
      hotelId: this.location.id,
      daysNumberForArrivalDate,
      daysNumber: this.nights,
      rooms,
    });
    await ApiReservationCalls.getHotelAvailability2(hotelAvailabilityInput, this.rate.ratePlanCode, pmsRoomType);

    this.arrivalDate = new Date(String(hotelAvailabilityInput.arrival));
    this.departureDate = new Date(String(hotelAvailabilityInput.departure));
  }

  private static generateRandomNumber(min: number, max: number): number {
    return Math.floor(Math.random() * (max - min + 1)) + min;
  }

  private static addDaysDate(days: number): Date {
    const date = new Date();
    date.setDate(date.getDate() + days);
    return date;
  }

  private static formatDayDate(date: Date): string {
    return date.toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' }).replace(',', '');
  }

  private static formatDayNameDayDate(date: Date): string {
    return date.toLocaleDateString('en-GB', { weekday: 'short', day: '2-digit', month: 'short', year: 'numeric' }).replace(',', '');
  }

  private static createLocation(name: string): Location {
    return { name, suggestion: name, id: '', countryCode: '' };
  }

  /**
   * Method used to update search criteria information
   * @param arrivalDate {Date} - Optional arrival date
   * @param departureDate {Date} - Optional departure date
   * @param nights {number} - Optional number of nights
   * @param location {SearchCriteriaLocation} - Optional location (Location | HotelData)
   * @param rooms {Room[]} - Optional array of rooms
   * @returns {Promise<void>}
   */
  async updateSearchCriteriaInformation({ arrivalDate, departureDate, nights, location, rooms }: Partial<SearchCriteria>): Promise<void> {
    if (arrivalDate !== undefined) {
      this.arrivalDate = arrivalDate;
    }
    if (departureDate !== undefined) {
      this.departureDate = departureDate;
    }
    if (nights !== undefined) {
      this.nights = nights;
    }
    if (location !== undefined) {
      this.location = location;
    }
    if (rooms !== undefined) {
      this.rooms = rooms;
    }
  }
}

// Type import for Room
type RoomType = Room['roomType'];
