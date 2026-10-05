import { ApiCalls } from './apiCalls';
import { Basket, HotelAvailability, CreateReservation, CreateReservationGuest } from '../response';
import { BookingChannel, HotelAvailabilityInput, type ConfirmReservationInput, type CreateReservationInput, type RoomPackageSelectionByIdInput } from '../requests';
import type { GuestDetailsModel } from '../../test-data/guestDetails';
import { ReservationInfoData, type ReservationInfo as CreatedReservationInfo } from '../../test-data/reservationInfo';
import { ApiHelpers } from '../apiHelpers';
import { ApiBasketCalls } from './apiBasketCalls';
import { OhipApiCalls } from '../ohip/ohipApiCalls';
import { ApiContentCalls } from './apiContentCalls';
import { HotelRates as HotelRate } from '../../test-data/hotelRates';
import { HotelBrands as HotelBrand } from '../../test-data/hotelBrands';
import { Locales as Locale } from '../../test-data/locales';
import { Constants } from '../../test-data/constants';
import { Strings as StringConstants } from '../../test-data/strings';
import { UiUtils } from '../../utils/uiUtils';

const moment = require('moment');
const _ = require('lodash');

interface HotelAvailabilityForReservation {
  hotelAvailability: HotelAvailability;
  hotelAvailabilityInput: HotelAvailabilityInput;
  ratePlanCode: string;
}

interface RoomRequirement {
  roomType: string | { id?: string };
  noOfRooms: number;
  adultsNumber: number;
  childrenNumber: number;
}

type GraphQLBody<TData = unknown> = {
  data?: TData;
  errors?: unknown[];
};

type AvailabilityErrorBody = GraphQLBody<unknown> & {
  errors: unknown[];
};

type ConfirmReservationBody = GraphQLBody<Record<string, unknown>>;

/** Interface for createReservationViaApi parameters */
interface CreateReservationViaApiParams {
  hotelId?: string | null;
  guestDetails?: GuestDetailsModel;
  stayingNights?: number;
  daysFromToday?: number;
  randomStartDate?: boolean;
  hotelAvailabilityInput?: HotelAvailabilityInput | null;
  ratePlanCode?: string;
  pmsRoomType?: string | string[];
  retriesCount?: number;
  loggedUser?: boolean;
  shouldHaveMeals?: boolean;
  shouldHaveEci?: boolean | null;
  shouldHaveLco?: boolean | null;
  eciLcoAllRooms?: boolean;
}

/** Interface for createHotelReservationWithoutGuestFromApi parameters */
interface CreateHotelReservationWithoutGuestParams {
  hotelId?: string;
  stayingNights?: number;
  daysFromToday?: number;
  hotelAvailabilityInput?: HotelAvailabilityInput | null;
  ratePlanCode?: string;
}

/** Interface for createHotelReservationWithGuestFromApi parameters */
interface CreateHotelReservationWithGuestParams {
  hotelId?: string;
  stayingNights?: number;
  daysFromToday?: number;
  hotelAvailabilityInput?: HotelAvailabilityInput | null;
  ratePlanCode?: string;
  guestDetails?: GuestDetailsModel;
}

/** Interface for getHotelAvailabilityForReservation parameters */
interface GetHotelAvailabilityForReservationParams {
  hotelId?: string;
  stayingNights?: number;
  daysFromToday?: number;
  hotelAvailabilityInput?: HotelAvailabilityInput | null;
  ratePlanCode?: string;
}

/** Interface for createReservationViaApiV2 parameters */
interface CreateReservationViaApiV2Params {
  hotelAvailability?: HotelAvailability;
  hotelAvailabilityInput?: HotelAvailabilityInput;
  ratePlanCode?: string;
  retriesCount?: number;
}

/** Interface for appendGuestToReservation parameters */
interface AppendGuestToReservationParams {
  hotelId?: string;
  basketReference?: string;
  guestDetails?: GuestDetailsModel;
}

/** Interface for confirmReservation parameters */
interface ConfirmReservationParams {
  createdReservation?: CreatedReservationInfo;
  paymentOption?: string;
}

/** Interface for confirmReservationRequest parameters */
interface ConfirmReservationRequestParams {
  confirmReservationInput?: ConfirmReservationInput;
}

/** Interface for graphqlUpdateReservationPackagesByReservation parameters */
interface UpdateReservationPackagesByReservationParams {
  basketReferenceId?: string;
  hotelId?: string;
  arrivalDate?: string;
  departureDate?: string;
  roomsSelections?: RoomPackageSelectionByIdInput[];
  previousRoomsSelections?: RoomPackageSelectionByIdInput[];
}

/** Interface for graphqlCreateReservationGuest parameters */
interface CreateReservationGuestParams {
  hotelId?: string;
  basketReference?: string;
  guestDetails?: GuestDetailsModel;
}

/** Interface for graphqlGetSingleHotelAvailability parameters */
interface GetSingleHotelAvailabilityParams {
  hotelAvailabilityInput: HotelAvailabilityInput;
  failIfError?: boolean;
  loggedUser?: boolean;
  companyId?: string;
}

/** Interface for graphqlCreateReservation parameters */
interface CreateReservationParams {
  createReservationInput?: CreateReservationInput;
  loggedUser?: boolean;
  failIfError?: boolean;
}

/**
 * Methods for accessing Premier Inn API backend resources.
 */
export class ApiReservationCalls {
  [key: string]: unknown;

  /** The reservations that are created while running the tests. */
  static createdReservations: Basket[] = [];

  private static getApp(): string {
    const app = String((global as any).browser?.options?.app ?? 'pi');
    return app;
  }

  constructor(data: Record<string, unknown> = {}) {
    const values = Object.values(data);
    const payload = values.length === 1 && values[0] && typeof values[0] === 'object' && !Array.isArray(values[0])
      ? values[0] as Record<string, unknown>
      : data;

    Object.assign(this, payload);
  }

  static fromApiData<T extends Record<string, unknown>>(data: T): ApiReservationCalls {
    return new ApiReservationCalls(data);
  }

  private static generateRandomNumber(min: number, max: number): number {
    return Math.floor(Math.random() * (max - min + 1)) + min;
  }

  /**
   * Create hotel reservation with custom startDate and endDate = startDate + 1 via API
   * @param {Object} params - object parameters
   * @param {String} params.hotelId - the hotelId for which to create reservation
  * @param {GuestDetailsModel} params.guestDetails - guest details for which to create reservation
   * @param {Number} [params.stayingNights=1] - the number of nights the guest will stay
   * @param {Number} [params.daysFromToday=2] - days from today
   * @param {Boolean} [params.randomStartDate=true] - use random start day between daysFromToday and next 20 days
  * @param {HotelAvailabilityInput} [params.hotelAvailabilityInput] - custom hotel availability input used for filtering hotels availability
   * @param {String} [params.ratePlanCode] - policy code of hotel (Pay now or Pay on arrival options)
   * @param {String} [params.pmsRoomType=''] - PMS room type that can be used to filter availabilities
   * @param {Number} [params.retriesCount=3] - the number of retries for creating the reservation
   * @param {Boolean} [params.loggedUser=false] - if there is a logged in user
   * @param {Boolean} [params.shouldHaveMeals=true] - if reservation should have restaurant and meals available
   * @param {Boolean|null} [params.shouldHaveEci] - if the reservation should have Early Check-in package available
   * @param {Boolean|null} [params.shouldHaveLco] - if the reservation should have Late Check-out package available
   * @param {Boolean} [params.eciLcoAllRooms=false] - if the reservation should have Early check-in and Late-check-out availability for all rooms
  * @returns {Promise<ReservationInfo<CreateReservation, GuestDetailsModel | undefined>>} reservation and guest details
   */
  static async createReservationViaApi({
    hotelId,
    guestDetails,
    stayingNights = 1,
    daysFromToday = 2,
    randomStartDate = true,
    hotelAvailabilityInput = null,
    ratePlanCode = HotelRate.PI_FLEX.ratePlanCode,
    pmsRoomType = '',
    retriesCount = 3,
    loggedUser = false,
    shouldHaveMeals = true,
    shouldHaveEci = null,
    shouldHaveLco = null,
    eciLcoAllRooms = false
  }: CreateReservationViaApiParams = {}): Promise<CreatedReservationInfo> {
    if (hotelId && hotelAvailabilityInput == null) {
      hotelAvailabilityInput = await HotelAvailabilityInput.createDefaultInputForHotelId(hotelId);
    } else if (hotelId && hotelAvailabilityInput != null) {
      throw new Error('HotelId could be different from hotelAvailability.hotelCode! Having both hotelId and hotelAvailabilityInput is not recommended.');
    }
    if (!hotelAvailabilityInput) {
      throw new Error('hotelAvailabilityInput is required when hotelId is not provided.');
    }
    daysFromToday = randomStartDate ? ApiReservationCalls.generateRandomNumber(daysFromToday, daysFromToday + 20) : daysFromToday;
    const startDate = moment().add(daysFromToday, 'days').format(Constants.ISO_DAY_DATE_FORMAT);
    const endDate = moment(startDate).add(stayingNights, 'days').format(Constants.ISO_DAY_DATE_FORMAT);
    const availabilityInput = hotelAvailabilityInput;
    (availabilityInput as any).arrival = startDate;
    (availabilityInput as any).departure = endDate;
    (availabilityInput as any).ratePlanCodes = [ratePlanCode];

    const hotelAvailability = await ApiReservationCalls.getHotelAvailability(availabilityInput, ratePlanCode, pmsRoomType, loggedUser, shouldHaveMeals, shouldHaveEci, shouldHaveLco, eciLcoAllRooms);
    const originalRatePlanCode = ratePlanCode ? ratePlanCode : (hotelAvailability as any).ratePlanCode;
    ratePlanCode = (hotelAvailability as any).ratePlanCode;

    const bookingFlowId = await ApiCalls.getBookingFlowIdBasedOnHotelAndRate({ hotelId: (availabilityInput as any).hotelCode, ratePlanCode: originalRatePlanCode });
    
    const createReservationInput = await ApiHelpers.createReservationInput(hotelAvailability, availabilityInput, ratePlanCode, pmsRoomType, bookingFlowId) as unknown as CreateReservationInput;
    let createReservation;
    try {
      createReservation = await ApiReservationCalls.graphqlCreateReservation(createReservationInput, loggedUser);
    } catch (err: any) {
      if (retriesCount === 0 || !err.toString().includes('"50')) {
        throw err;
      }
      console.log(err.toString());
      console.log(`${retriesCount} retries left for creating the reservation.`);
      return ApiReservationCalls.createReservationViaApi({
        hotelId: null,
        guestDetails,
        stayingNights,
        daysFromToday: (moment((availabilityInput as any).departure).toDate().getTime() - moment((availabilityInput as any).arrival).toDate().getTime()) / (24 * 3600 * 1000),
        hotelAvailabilityInput: availabilityInput,
        ratePlanCode,
        pmsRoomType,
        retriesCount: --retriesCount,
        loggedUser,
        shouldHaveMeals,
        shouldHaveEci,
        shouldHaveLco,
        eciLcoAllRooms
      });
    }

    if (guestDetails) {
      await ApiReservationCalls.graphqlCreateReservationGuest({ hotelId: (hotelAvailability as any).hotelId, basketReference: String((createReservation as any).basketReference ?? ''), guestDetails });
    }

    return ReservationInfoData.createReservationInfo({ reservationDetails: createReservation, guestDetails, bookingFlowId: String(bookingFlowId), ratePlanCode });
  }

  /**
   * Create hotel reservation without guest and custom startDate and endDate = startDate + 1 via API
   * @param {String} params.hotelId - the hotelId for which to create reservation
   * @param {Number} [params.stayingNights=1] - the number of nights the guest will stay
   * @param {Number} [params.daysFromToday=0] - days from today
  * @param {HotelAvailabilityInput} [params.hotelAvailabilityInput] - custom hotel availability input used for filtering hotels availability
   * @param {String} [params.ratePlanCode] - policy code of hotel (uses first available room rate if empty)
  * @returns {Promise<ReservationInfo<CreateReservation, undefined>>} reservation details
   */
  static async createHotelReservationWithoutGuestFromApi({
    hotelId,
    stayingNights = 1,
    daysFromToday = 0,
    hotelAvailabilityInput = null,
    ratePlanCode = HotelRate.PI_FLEX.ratePlanCode
  }: CreateHotelReservationWithoutGuestParams = {}): Promise<CreatedReservationInfo> {
    const hotelAvailabilityResponse = await this.getHotelAvailabilityForReservation({ hotelId, stayingNights, daysFromToday, hotelAvailabilityInput, ratePlanCode });
    const createReservationResponse = await this.createReservationViaApiV2({
      hotelAvailability: hotelAvailabilityResponse.hotelAvailability,
      hotelAvailabilityInput: hotelAvailabilityResponse.hotelAvailabilityInput,
      ratePlanCode: hotelAvailabilityResponse.ratePlanCode
    });
    const bookingFlowId = await ApiCalls.getBookingFlowIdBasedOnHotelAndRate({ hotelId: (hotelAvailabilityResponse.hotelAvailabilityInput as any).hotelCode, ratePlanCode: hotelAvailabilityResponse.ratePlanCode });

    return ReservationInfoData.createReservationInfo({ reservationDetails: createReservationResponse, guestDetails: undefined, bookingFlowId: String(bookingFlowId), ratePlanCode: hotelAvailabilityResponse.ratePlanCode });
  }

  /**
   * Create hotel reservation with guest and custom startDate and endDate = startDate + 1 via API
   * @param {Object} params - object parameters
   * @param {String} params.hotelId - the hotelId for which to create reservation
   * @param {Number} [params.stayingNights=1] - the number of nights the guest will stay
   * @param {Number} [params.daysFromToday=0] - days from today
  * @param {HotelAvailabilityInput} [params.hotelAvailabilityInput] - custom hotel availability input used for filtering hotels availability
   * @param {String} [params.ratePlanCode] - policy code of hotel (uses first available room rate if empty)
  * @param {GuestDetailsModel} params.guestDetails - guest details for which to create reservation
  * @returns {Promise<ReservationInfo<CreateReservation, GuestDetailsModel | undefined>>} reservation and guest details
   */
  static async createHotelReservationWithGuestFromApi({
    hotelId,
    stayingNights = 1,
    daysFromToday = 0,
    hotelAvailabilityInput = null,
    ratePlanCode = HotelRate.PI_FLEX.ratePlanCode,
    guestDetails
  }: CreateHotelReservationWithGuestParams = {}): Promise<CreatedReservationInfo> {
    const hotelAvailabilityResponse = await this.getHotelAvailabilityForReservation({ hotelId, stayingNights, daysFromToday, hotelAvailabilityInput, ratePlanCode });
    const createReservationResponse = await this.createReservationViaApiV2({
      hotelAvailability: hotelAvailabilityResponse.hotelAvailability,
      hotelAvailabilityInput: hotelAvailabilityResponse.hotelAvailabilityInput,
      ratePlanCode: hotelAvailabilityResponse.ratePlanCode
    });
    await this.appendGuestToReservation({ hotelId: (hotelAvailabilityResponse.hotelAvailability as any).hotelId, basketReference: String((createReservationResponse as any).basketReference ?? ''), guestDetails: guestDetails });
    const bookingFlowId = await ApiCalls.getBookingFlowIdBasedOnHotelAndRate({ hotelId: (hotelAvailabilityResponse.hotelAvailabilityInput as any).hotelCode, ratePlanCode: hotelAvailabilityResponse.ratePlanCode });

    return ReservationInfoData.createReservationInfo({ reservationDetails: createReservationResponse, guestDetails, bookingFlowId: String(bookingFlowId), ratePlanCode: hotelAvailabilityResponse.ratePlanCode });
  }

  /**
   * Get hotel availability from OHIP
   * @param {Object} params - object parameters
   * @param {String} params.hotelId - the hotelId for which to create reservation
   * @param {Number} params.stayingNights - the number of nights the guest will stay
   * @param {Number} params.daysFromToday - days from today
  * @param {HotelAvailabilityInput} params.hotelAvailabilityInput - custom hotel availability input used for filtering
   * @param {String} params.ratePlanCode - policy code of hotel
  * @returns {Promise<HotelAvailabilityForReservation>} availability, request input, and rate plan code
   */
  static async getHotelAvailabilityForReservation({
    hotelId,
    stayingNights,
    daysFromToday,
    hotelAvailabilityInput = null,
    ratePlanCode
  }: GetHotelAvailabilityForReservationParams = {}): Promise<HotelAvailabilityForReservation> {
    if (hotelId && hotelAvailabilityInput === null) {
      hotelAvailabilityInput = await HotelAvailabilityInput.createDefaultInputForHotelId(hotelId);
    } else if (hotelId && hotelAvailabilityInput !== null) {
      throw new Error('HotelId could be different from hotelAvailability.hotelCode! Having both hotelId and hotelAvailabilityInput is not recommended.');
    }
    if (!hotelAvailabilityInput) {
      throw new Error('hotelAvailabilityInput is required when hotelId is not provided.');
    }
    const startDate = moment().add(daysFromToday, 'days').format(Constants.ISO_DAY_DATE_FORMAT);
    const endDate = moment(startDate).add(stayingNights, 'days').format(Constants.ISO_DAY_DATE_FORMAT);
    (hotelAvailabilityInput as any).arrival = startDate;
    (hotelAvailabilityInput as any).departure = endDate;
    const hotelAvailability = await ApiReservationCalls.getHotelAvailability2(hotelAvailabilityInput);
    ratePlanCode = ratePlanCode ? ratePlanCode : (hotelAvailability as any).roomRates[0].ratePlanCode;

    return { hotelAvailability, hotelAvailabilityInput, ratePlanCode: ratePlanCode! };
  }

  /**
   * Create reservation using API with retry logic
   * @param {Object} params - object parameters
  * @param {HotelAvailability} params.hotelAvailability - hotel availability response required for reservation
  * @param {HotelAvailabilityInput} params.hotelAvailabilityInput - hotel availability input required for reservation
   * @param {String} params.ratePlanCode - rate plan code
   * @param {Number} [params.retriesCount=3] - the number of retries for creating the reservation
  * @returns {Promise<CreateReservation>} reservation details
   */
  static async createReservationViaApiV2({
    hotelAvailability,
    hotelAvailabilityInput,
    ratePlanCode,
    retriesCount = 3
  }: CreateReservationViaApiV2Params = {}): Promise<CreateReservation> {
    const bookingFlowId = await ApiCalls.getBookingFlowIdBasedOnHotelAndRate({ hotelId: (hotelAvailabilityInput as any).hotelCode, ratePlanCode });
    const createReservationInput = await ApiHelpers.createReservationInput(hotelAvailability, hotelAvailabilityInput, ratePlanCode, '', bookingFlowId) as unknown as CreateReservationInput;
    let createReservation;
    try {
      createReservation = await ApiReservationCalls.graphqlCreateReservation(createReservationInput, false);
    } catch (err: any) {
      if (retriesCount === 0 || !err.toString().includes('"50')) {
        throw err;
      }
      console.log(err.toString());
      console.log(`${retriesCount} retries left for creating the reservation.`);
      return ApiReservationCalls.createReservationViaApiV2({
        hotelAvailability,
        hotelAvailabilityInput,
        ratePlanCode,
        retriesCount: --retriesCount
      });
    }
    return createReservation;
  }

  /**
   * Append guest to reservation
   * @param {Object} params - object parameters
   * @param {String} params.hotelId - hotel Id
   * @param {String} params.basketReference - basket reference for reservation
  * @param {GuestDetailsModel} params.guestDetails - guest details object
  * @returns {Promise<void>} after creating the reservation guest
   */
  static async appendGuestToReservation({
    hotelId,
    basketReference,
    guestDetails
  }: AppendGuestToReservationParams = {}): Promise<void> {
    if (guestDetails) {
      (guestDetails as any).booker.title = (guestDetails as any).booker.title ?? StringConstants.MR_TITLE?.name;
      await ApiReservationCalls.graphqlCreateReservationGuest({ hotelId, basketReference, guestDetails });
    }
  }

  /**
   * Check for hotel availability and return the response
  * @param {HotelAvailabilityInput} hotelAvailabilityInput - hotel availability input object
   * @param {String} [ratePlanCode] - rate plan code used to filter availabilities
   * @param {String|String[]} [pmsRoomType=''] - PMS room type(s) to filter availabilities
   * @param {Boolean} [loggedUser=false] - if there is a logged in user
   * @param {Boolean} [shouldHaveMeals=true] - if reservation should have restaurant and meals available
   * @param {Boolean|null} [shouldHaveEci] - if reservation should have Early Check-in package available
   * @param {Boolean|null} [shouldHaveLco] - if reservation should have Late Check-out package available
    * @param {Boolean} [eciLcoAllRooms=false] - if reservation should have Early Check-in and Late-checkout packages for all rooms
    * @param {Boolean} [exactRatePlanCode=false] - only accept the requested rate plan code
  * @returns {Promise<HotelAvailability>} hotel availability response with the matching rate plan code
   */
  static async getHotelAvailability(
    hotelAvailabilityInput: HotelAvailabilityInput,
    ratePlanCode: string = (HotelRate as any)?.PI_FLEX?.ratePlanCode || 'FLEXRATE',
    pmsRoomType: string | string[] = '',
    loggedUser: boolean = false,
    shouldHaveMeals: boolean = true,
    shouldHaveEci: boolean | null = null,
    shouldHaveLco: boolean | null = null,
    eciLcoAllRooms: boolean = false,
    exactRatePlanCode: boolean = false
  ): Promise<HotelAvailability> {
    // number of retries to look for hotels availabilities over 3 months span (90 days)
    let retries = 90;
    let serviceErrorIndex = 1;
    let roomFound: boolean = false;
    
    // cancel the hotel reservations older than 1 hour, to have more availability
    await OhipApiCalls.cancelHotelReservationsForHotel((hotelAvailabilityInput as any).hotelCode);

    // get availability after opening soon date
    const hotelInformation = await ApiContentCalls.graphqlGetHotelInformation({ hotelId: (hotelAvailabilityInput as any).hotelCode });
    const additionalOpeningSoonDays = (hotelInformation as any).hotelOpeningDate && moment((hotelAvailabilityInput as any).arrival).toDate().getTime() < moment((hotelInformation as any).hotelOpeningDate).toDate().getTime() ?
      (moment((hotelInformation as any).hotelOpeningDate).toDate().getTime() - moment((hotelAvailabilityInput as any).arrival).toDate().getTime()) / (1000 * 3600 * 24) : 0;
    
    const bookingFlowId = (shouldHaveEci != null || shouldHaveLco != null || shouldHaveMeals) ? 
      await ApiCalls.getBookingFlowIdBasedOnHotelAndRate({ hotelId: (hotelAvailabilityInput as any).hotelCode }) : null;
    
    (hotelAvailabilityInput as any).arrival = moment((hotelAvailabilityInput as any).arrival).add(additionalOpeningSoonDays, 'days').format(Constants.ISO_DAY_DATE_FORMAT);
    (hotelAvailabilityInput as any).departure = moment((hotelAvailabilityInput as any).departure).add(additionalOpeningSoonDays, 'days').format(Constants.ISO_DAY_DATE_FORMAT);

    let hotelAvailability: any = await ApiReservationCalls.graphqlGetSingleHotelAvailability({ hotelAvailabilityInput, failIfError: false, loggedUser });
    let matchedAncillaryCloseoutItems = false;
    
    while ((hotelAvailability.errors && hotelAvailability.errors.length > 0 && (hotelAvailability.errors[0] as any).errorType === '500') || (!roomFound && retries !== 0)) {
      const ancillaryCloseoutMatchingItems = (hotelInformation as any).ancillaryCloseout ? (hotelInformation as any).ancillaryCloseout.items.filter((item: any) => {
        const ancillaryCloseoutStartDateTime = moment(item.startDate, Constants.DATE_FORMAT_SLASH).toDate().getTime();
        const ancillaryCloseoutEndDateTime = moment(item.endDate, Constants.DATE_FORMAT_SLASH).toDate().getTime();
        const hotelAvailabilityInputArrivalTime = moment((hotelAvailabilityInput as any).arrival).toDate().getTime();
        const hotelAvailabilityInputDepartureTime = moment((hotelAvailabilityInput as any).departure).toDate().getTime();
        return item.upsellCodes && item.upsellCodes !== 'No Upselling Codes Found' && (
          hotelAvailabilityInputArrivalTime >= ancillaryCloseoutStartDateTime && hotelAvailabilityInputArrivalTime <= ancillaryCloseoutEndDateTime ||
          hotelAvailabilityInputDepartureTime >= ancillaryCloseoutStartDateTime && hotelAvailabilityInputDepartureTime <= ancillaryCloseoutEndDateTime ||
          ancillaryCloseoutStartDateTime >= hotelAvailabilityInputArrivalTime && ancillaryCloseoutStartDateTime <= hotelAvailabilityInputDepartureTime &&
          ancillaryCloseoutEndDateTime >= hotelAvailabilityInputArrivalTime && ancillaryCloseoutEndDateTime <= hotelAvailabilityInputDepartureTime
        );
      }) : [];
      
      const stayingNights = Math.round((moment((hotelAvailabilityInput as any).departure, Constants.ISO_DAY_DATE_FORMAT).toDate().getTime() - moment((hotelAvailabilityInput as any).arrival).toDate().getTime()) / (1000 * 3600 * 24));
      let totalAdults = 0;
      let totalChildren = 0;

      (hotelAvailabilityInput as any).rooms.forEach((room: any) => {
        totalAdults += room.adultsNumber;
        totalChildren += room.childrenNumber;
      });
      
      const meals = shouldHaveMeals ? await ApiCalls.graphqlGetMealsPackages({
        hotelId: (hotelAvailabilityInput as any).hotelCode,
        nightsNumber: stayingNights,
        startDate: (hotelAvailabilityInput as any).arrival,
        endDate: (hotelAvailabilityInput as any).departure,
        adultsNumber: totalAdults,
        childrenNumber: totalChildren,
        bookingFlowId: bookingFlowId
      }) : null;
      
      if (shouldHaveMeals && (ancillaryCloseoutMatchingItems.length > 0 || !(
        (meals as any).adultMeals.filter((meal: any) => meal.id === Constants.PI_BREAKFAST_PACKAGE_CODE).length > 0 ||
        (meals as any).adultMeals.filter((meal: any) => meal.id === Constants.BREAKFAST_GERMAN_PACKAGE_CODE).length > 0 ||
        (meals as any).adultMeals.filter((meal: any) => meal.id === Constants.CONTINENTAL_BREAKFAST_PACKAGE_CODE).length > 0
      ))) {
        matchedAncillaryCloseoutItems = true;
        console.log(`No rooms are available for the period: ${(hotelAvailabilityInput as any).arrival} - ${(hotelAvailabilityInput as any).departure} because not all the meals are available.`);
        (hotelAvailabilityInput as any).arrival = moment((hotelAvailabilityInput as any).arrival).add(1, 'days').format(Constants.ISO_DAY_DATE_FORMAT);
        (hotelAvailabilityInput as any).departure = moment((hotelAvailabilityInput as any).departure).add(1, 'days').format(Constants.ISO_DAY_DATE_FORMAT);
        retries -= 1;
        console.log(`Retries left: ${retries}`);
        if (retries === 0) {
          await ApiReservationCalls.getHotelInventoryAndLogRooms(hotelAvailabilityInput as any);
          throw new Error(`No rooms with meals are available for the period: ${(hotelAvailabilityInput as any).arrival} - ${(hotelAvailabilityInput as any).departure}`);
        }
        continue;
      } else {
        if (matchedAncillaryCloseoutItems) {
          matchedAncillaryCloseoutItems = false;
          hotelAvailability = await ApiReservationCalls.graphqlGetSingleHotelAvailability({ hotelAvailabilityInput, failIfError: false, loggedUser });
          continue;
        }
      }
      
      await UiUtils.switchToDefaultContent(); // workaround to avoid the browser instance to close if it takes long time to find the availability.
      
      let filteredRoomRates = hotelAvailability.errors ? [] : (hotelAvailability as any).roomRates.filter((roomRate: any) => !ratePlanCode || roomRate.ratePlanCode === ratePlanCode);
      
      if (!exactRatePlanCode && (['pib'].includes(ApiReservationCalls.getApp()) || ['ccui'].includes(ApiReservationCalls.getApp()) && filteredRoomRates.length === 0) &&
        (ratePlanCode === '' || ratePlanCode === (HotelRate as any)?.PI_FLEX?.ratePlanCode) && (hotelAvailability as any).roomRates) {
        // use the Business Flex rate instead of Flex rate for the suppression case.
        const businessFlexRate = (hotelAvailability as any).roomRates.find((roomRate: any) => roomRate.ratePlanCode === (HotelRate as any)?.BUSINESS_FLEX?.ratePlanCode);
        filteredRoomRates = businessFlexRate ? [businessFlexRate] : filteredRoomRates;
      }
      
      filteredRoomRates = filteredRoomRates.filter((roomRate: any) =>
        roomRate.roomTypes.filter((roomType: any, index: number) =>
          roomType.rooms.length > 0 && roomType.rooms.filter((room: any) =>
            !pmsRoomType || (Array.isArray(pmsRoomType) && (!pmsRoomType[index] || room.pmsRoomType === pmsRoomType[index])) || room.pmsRoomType === pmsRoomType
          ).length > 0
        ).length >= (hotelAvailabilityInput as any).rooms.length
      );
      
      let eciPackageFound = false;
      let lcoPackageFound = false;
      
      if (shouldHaveEci != null || shouldHaveLco != null) {
        let eciList: any[] | undefined;
        let lcoList: any[] | undefined;
        let eciPackageAvailable;
        let lcoPackageAvailable;

        if (shouldHaveEci != null) {
          console.log(`Retrieve ECI packages for dates: ${(hotelAvailabilityInput as any).arrival} - ${(hotelAvailabilityInput as any).departure}`);
          eciList = await ApiCalls.graphqlGetAncillariesEciExtras({
            hotelId: (hotelAvailabilityInput as any).hotelCode,
            bookingFlowId: bookingFlowId,
            nightsNumber: stayingNights,
            startDate: (hotelAvailabilityInput as any).arrival,
            endDate: (hotelAvailabilityInput as any).departure,
            adultsNumber: totalAdults,
            childrenNumber: totalChildren
          }) as any[];
          eciPackageAvailable = eciList && eciList.length > 0 ? Number.parseInt((eciList[0] as any).available) : 0;
          if (eciLcoAllRooms) {
            eciPackageFound = eciPackageAvailable >= (hotelAvailabilityInput as any).rooms.length;
          } else {
            eciPackageFound = eciPackageAvailable > 0;
          }
        }
        
        if (shouldHaveLco != null) {
          console.log(`Retrieve LCO packages for dates: ${(hotelAvailabilityInput as any).arrival} - ${(hotelAvailabilityInput as any).departure}`);
          lcoList = await ApiCalls.graphqlGetAncillariesLcoExtras({
            hotelId: (hotelAvailabilityInput as any).hotelCode,
            bookingFlowId: bookingFlowId,
            nightsNumber: stayingNights,
            startDate: (hotelAvailabilityInput as any).arrival,
            endDate: (hotelAvailabilityInput as any).departure,
            adultsNumber: totalAdults,
            childrenNumber: totalChildren
          }) as any[];
          lcoPackageAvailable = lcoList && lcoList.length > 0 ? Number.parseInt((lcoList[0] as any).available) : 0;
          if (eciLcoAllRooms) {
            lcoPackageFound = lcoPackageAvailable >= (hotelAvailabilityInput as any).rooms.length;
          } else {
            lcoPackageFound = lcoPackageAvailable > 0;
          }
        }
      }
      
      const hasEnoughRooms = filteredRoomRates.length > 0;

      if (hotelAvailability.errors || (hotelAvailability as any).available === false || !hasEnoughRooms || 
        (shouldHaveEci != null && (shouldHaveEci === true && !eciPackageFound || shouldHaveEci === false && eciPackageFound)) || 
        (shouldHaveLco != null && (shouldHaveLco === true && !lcoPackageFound || shouldHaveLco === false && lcoPackageFound))) {
        
        if (hotelAvailability.errors) {
          if (serviceErrorIndex === 21) {
            await ApiReservationCalls.getHotelInventoryAndLogRooms(hotelAvailabilityInput as any);
            throw new Error('PMS Adapter error, probably the service is down or request is invalid!');
          } else {
            console.log(`Service returned 500 Errors. Service errors count: ${serviceErrorIndex} / 20`);
            // add some delay between retries
            await UiUtils.page.waitForTimeout(2000 * serviceErrorIndex);
            serviceErrorIndex += 1;
          }
        } else if (shouldHaveEci != null && shouldHaveEci === true && !eciPackageFound || shouldHaveLco != null && shouldHaveLco === true && !lcoPackageFound) {
          if (!eciPackageFound) {
            console.log(`No ECI packages found for the period ${(hotelAvailabilityInput as any).arrival} - ${(hotelAvailabilityInput as any).departure}`);
          }
          if (!lcoPackageFound) {
            console.log(`No LCO packages found for the period ${(hotelAvailabilityInput as any).arrival} - ${(hotelAvailabilityInput as any).departure}`);
          }
        } else if (shouldHaveEci != null && shouldHaveEci === false && eciPackageFound || shouldHaveLco != null && shouldHaveLco === false && lcoPackageFound) {
          if (eciPackageFound) {
            console.log(`ECI packages found for the period ${(hotelAvailabilityInput as any).arrival} - ${(hotelAvailabilityInput as any).departure}`);
          }
          if (lcoPackageFound) {
            console.log(`LCO packages found for the period ${(hotelAvailabilityInput as any).arrival} - ${(hotelAvailabilityInput as any).departure}`);
          }
        } else {
          console.log(`No rooms are available for the period: ${(hotelAvailabilityInput as any).arrival} - ${(hotelAvailabilityInput as any).departure}`);
        }
        
        (hotelAvailabilityInput as any).arrival = moment((hotelAvailabilityInput as any).arrival).add(1, 'days').format(Constants.ISO_DAY_DATE_FORMAT);
        (hotelAvailabilityInput as any).departure = moment((hotelAvailabilityInput as any).departure).add(1, 'days').format(Constants.ISO_DAY_DATE_FORMAT);
        hotelAvailability = await ApiReservationCalls.graphqlGetSingleHotelAvailability({ hotelAvailabilityInput, failIfError: false, loggedUser });
        retries -= 1;
        console.log(`Retries left: ${retries}`);
        if (retries === 0) {
          await ApiReservationCalls.getHotelInventoryAndLogRooms(hotelAvailabilityInput as any);
          throw new Error(`No rooms are available for the period: ${(hotelAvailabilityInput as any).arrival} - ${(hotelAvailabilityInput as any).departure}`);
        }
      } else {
        console.log(`Room found for the period: ${(hotelAvailabilityInput as any).arrival} - ${(hotelAvailabilityInput as any).departure}`);
        roomFound = true;
        (hotelAvailability as any).ratePlanCode = ratePlanCode && ratePlanCode !== (HotelRate as any)?.PI_FLEX?.ratePlanCode ? ratePlanCode : filteredRoomRates[0].ratePlanCode;
        break;
      }
    }
    
    // set correct reservation dates for the hotelAvailability object
    (hotelAvailability as any).startDate = (hotelAvailabilityInput as any).arrival;
    (hotelAvailability as any).endDate = (hotelAvailabilityInput as any).departure;
    return hotelAvailability;
  }

  /**
   * Get hotel availability from OHIP
    * @param {HotelAvailabilityInput} hotelAvailabilityInput - hotel availability input object
    * @returns {Promise<HotelAvailability>} hotel availability response
   */
  static async getHotelAvailability2(hotelAvailabilityInput: HotelAvailabilityInput, _ratePlanCode?: string, _pmsRoomType?: string | string[]): Promise<HotelAvailability> {
    // cancel the hotel reservations older than 1 hour, to have more availability
    await OhipApiCalls.cancelHotelReservationsForHotel((hotelAvailabilityInput as any).hotelCode);

    const rooms = await this.getRoomsFromAvailabilityInput(hotelAvailabilityInput);
    console.log(`Needed rooms: ${JSON.stringify(rooms)}`);

    const pmsRoomTypes = await this.getPMSRoomTypesFromRoomSubstitutionCall(rooms);
    const availableDates = await this.getAvailableDatesFromOhipBasedOnPmsRoomType(hotelAvailabilityInput, pmsRoomTypes, rooms);

    return this.getHotelAvailabilityBasedOnAvailableDates(hotelAvailabilityInput, availableDates);
  }

  /**
   * Get a map of rooms and the count for each room requested in hotelAvailabilityInput
   * @param {HotelAvailabilityInput} hotelAvailabilityInput - hotel availability input object
   * @returns {Promise<Array<RoomRequirement>>} room types and their requested counts
   */
  static async getRoomsFromAvailabilityInput(hotelAvailabilityInput: HotelAvailabilityInput): Promise<RoomRequirement[]> {
    const roomsNeeded: RoomRequirement[] = [];
    for (const room of hotelAvailabilityInput.rooms ?? []) {
      if (!roomsNeeded.some(el => (el.roomType === room.roomType && el.adultsNumber === room.adultsNumber && el.childrenNumber === room.childrenNumber))) {
        roomsNeeded.push({
          roomType: room.roomType ?? '',
          noOfRooms: 1,
          adultsNumber: room.adultsNumber ?? 0,
          childrenNumber: room.childrenNumber ?? 0,
        });
      } else {
        const existingElement = roomsNeeded.find(element => (element.roomType === room.roomType && element.adultsNumber === room.adultsNumber && element.childrenNumber === room.childrenNumber));
        if (existingElement) {
          existingElement.noOfRooms = existingElement.noOfRooms + 1;
        }
      }
    }
    return roomsNeeded;
  }

  /**
   * Get array of PMS room types based on room list
    * @param {Array<RoomRequirement>} roomsList - list of rooms needed
    * @returns {Promise<Array<Array<String>>>} PMS room types for each requested room
   */
  static async getPMSRoomTypesFromRoomSubstitutionCall(roomsList: RoomRequirement[]): Promise<string[][]> {
    const rawPmsRoomsArray = await Promise.all(roomsList.map(async (room: RoomRequirement) => {
      const roomType = typeof room.roomType === 'string' ? room.roomType : room.roomType.id ?? '';
      const roomSubstitutions = await ApiCalls.graphqlGetRoomSubstitutions({
        adults: room.adultsNumber,
        children: room.childrenNumber,
        roomType
      });
      return roomSubstitutions.substitutionList ?? [];
    }));
    return Promise.all(rawPmsRoomsArray.map(
      async (pmsRoomType) => Promise.all(pmsRoomType.map(async (item) => item.type ?? ''))
    ));
  }

  /**
   * Get array of available dates based on PMS type and room list
    * @param {HotelAvailabilityInput} hotelAvailabilityInput - hotel availability input object
    * @param {Array<Array<String>>} pmsRoomTypeArray - PMS room types for each requested room
    * @param {Array<RoomRequirement>} roomsList - list of rooms needed
    * @returns {Promise<Array<String>>} available dates
   */
  static async getAvailableDatesFromOhipBasedOnPmsRoomType(
    hotelAvailabilityInput: HotelAvailabilityInput,
    pmsRoomTypeArray: string[][],
    roomsList: RoomRequirement[]
  ): Promise<string[]> {
    const rawAvailableDates = await Promise.all(pmsRoomTypeArray.map(
      async (item, index) => ApiReservationCalls.getAvailableDatesFromOhip(hotelAvailabilityInput, item, roomsList[index].noOfRooms)
    ));

    const availableDates = _.intersection(...rawAvailableDates);
    if (availableDates.length === 0) {
      throw new Error('No available dates.');
    }
    return availableDates;
  }

  /**
   * Get hotel availability based on available dates
    * @param {HotelAvailabilityInput} hotelAvailabilityInput - hotel availability input object
    * @param {Array<String>} availableDates - list of available dates
    * @returns {Promise<HotelAvailability>} hotel availability response
   */
  static async getHotelAvailabilityBasedOnAvailableDates(
    hotelAvailabilityInput: HotelAvailabilityInput,
    availableDates: string[]
  ): Promise<HotelAvailability> {
    const daysDiff = moment((hotelAvailabilityInput as any).departure).diff(moment((hotelAvailabilityInput as any).arrival), 'days');
    for (const date of availableDates) {
      (hotelAvailabilityInput as any).arrival = date;
      const datesRangeArray = [date];
      for (let i = 0; i < daysDiff; i++) {
        datesRangeArray.push(moment(datesRangeArray[i]).add(1, 'days').format(Constants.ISO_DAY_DATE_FORMAT));
      }
      // Check if interval is included in the available dates array
      if (datesRangeArray.every(element => availableDates.includes(element))) {
        (hotelAvailabilityInput as any).departure = moment((hotelAvailabilityInput as any).arrival).add(daysDiff, 'days').format(Constants.ISO_DAY_DATE_FORMAT);
        const hotelAvailability = await ApiReservationCalls.graphqlGetSingleHotelAvailability({ hotelAvailabilityInput, failIfError: false });
        (hotelAvailability as any).startDate = (hotelAvailabilityInput as any).arrival;
        (hotelAvailability as any).endDate = (hotelAvailabilityInput as any).departure;
        return hotelAvailability as HotelAvailability;
      } else {
        console.log('Date range is not included in available dates range');
      }
    }
    throw new Error('No available dates found');
  }

  /**
   * Get open soon date from hotel information call
    * @param {HotelAvailabilityInput} hotelAvailabilityInput - hotel availability input object
    * @returns {Promise<Number>} days until the hotel opens
   */
  static async getOpenSoonDays(hotelAvailabilityInput: HotelAvailabilityInput): Promise<number> {
    const hotelInformation = await ApiContentCalls.graphqlGetHotelInformation({ hotelId: (hotelAvailabilityInput as any).hotelCode });
    const additionalOpeningSoonDays = (hotelInformation as any).hotelOpeningDate && moment((hotelAvailabilityInput as any).arrival).toDate().getTime() < moment((hotelInformation as any).hotelOpeningDate).toDate().getTime() ?
      (moment((hotelInformation as any).hotelOpeningDate).toDate().getTime() - moment((hotelAvailabilityInput as any).arrival).toDate().getTime()) / (1000 * 3600 * 24) : 0;
    return additionalOpeningSoonDays;
  }

  /**
   * Get available dates to try reservations on a specific hotel and desired rooms
    * @param {HotelAvailabilityInput} hotelAvailabilityInput - hotel availability input object
    * @param {String|Array<String>} pmsRoomType - PMS room type to filter availabilities
    * @param {Number} numberOfRooms - number of rooms needed for this type
    * @returns {Promise<Array<String>>} available dates
   */
  static async getAvailableDatesFromOhip(
    hotelAvailabilityInput: HotelAvailabilityInput,
    pmsRoomType: string | string[],
    numberOfRooms: number
  ): Promise<string[]> {
    const dateRangeStart = moment((hotelAvailabilityInput as any).arrival).format(Constants.ISO_DAY_DATE_FORMAT);
    const dateRangeEnd = moment((hotelAvailabilityInput as any).arrival).add(89, 'days').format(Constants.ISO_DAY_DATE_FORMAT);

    const hotelInventory = await OhipApiCalls.getHotelInventorySingleRoomType((hotelAvailabilityInput as any).hotelCode, dateRangeStart, dateRangeEnd, Array.isArray(pmsRoomType) ? pmsRoomType : [pmsRoomType], numberOfRooms);
    const availableDates = new Set<string>();
    for (const roomType of (hotelInventory as any).hotelInventories[0].roomTypeInventories) {
      const filteredDays = roomType.inventoryCounts.filter((day: any) => day.availableCount >= numberOfRooms);
      for (const day of filteredDays) {
        availableDates.add(day.startDate);
      }
    }
    return Array.from(availableDates);
  }

  /**
   * GraphQL query for GetSingleHotelAvailability
   * @param {Object} params - object parameters
  * @param {HotelAvailabilityInput} params.hotelAvailabilityInput - hotel availability input to set GraphQL variables
   * @param {Boolean} [params.failIfError=true] - if it should fail the run on response error
   * @param {Boolean} [params.loggedUser] - if user is logged in
   * @param {String} [params.companyId] - company id
  * @returns {Promise<HotelAvailability | AvailabilityErrorBody>} hotel availability response or GraphQL errors
   */
  static async graphqlGetSingleHotelAvailability({
    hotelAvailabilityInput,
    failIfError = true,
    loggedUser = false,
    companyId
  }: GetSingleHotelAvailabilityParams): Promise<HotelAvailability | AvailabilityErrorBody> {
    const app = ApiReservationCalls.getApp();
    const queryFile = ['bb'].includes(app) ? 'hotelAvailabilityBB.graphql' :
      ['pib'].includes(app) ? 'hotelAvailabilityIB.graphql' :
        'hotelAvailability.graphql';
    
    const arrival = moment(hotelAvailabilityInput.arrival).format(Constants.ISO_DAY_DATE_FORMAT);
    const departure = moment(hotelAvailabilityInput.departure).format(Constants.ISO_DAY_DATE_FORMAT);
    const requestRooms: Record<string, unknown>[] = [];
    
    for (const room of hotelAvailabilityInput.rooms ?? []) {
      const roomData = room as unknown as Record<string, unknown>;
      const roomType = roomData.roomType as { id?: string } | string | undefined;
      const newRoom = { ...roomData };
      if (typeof roomType === 'object' && roomType?.id !== undefined) {
        newRoom.roomType = roomType.id;
      }
      requestRooms.push(newRoom);
    }
    
    const bookingChannel = hotelAvailabilityInput.bookingChannel ?? new BookingChannel();
    const bookingChannelLanguage = bookingChannel.language
      ?? Locale.getLocaleByString(String((global as any).browser?.options?.locale ?? 'gb-en')).language;

    let hotelIdVariable = ['pib', 'pi', 'distr', 'ccui'].includes(app) ? 'hotelId' : 'hotelCode';
    hotelIdVariable = ['bb'].includes(app) ? 'hotel' : hotelIdVariable;
    
    let variables: any = {
      arrival,
      departure,
      rooms: requestRooms,
      bookingChannel,
      companyId
    };
    
    if (['pi'].includes(app)) {
      variables.promotionCode = null;
    }
    
    variables[hotelIdVariable] = ['bb'].includes(app) 
      ? { identifier: (hotelAvailabilityInput as any).hotelCode } 
      : hotelAvailabilityInput.hotelCode;
    
    if (['bb'].includes(app)) {
      variables = { searchCriteria: variables };
    }

    if (['pib', 'pi', 'ccui', 'distr'].includes(app)) {
      variables.bookingChannel.language = bookingChannelLanguage.toUpperCase();
      variables.brand = HotelBrand.PI?.nameLowercase || 'pi';
      variables.channel = bookingChannel.channel;
      variables.country = Locale.getLocaleByString((global as any).browser?.options?.locale ?? 'gb-en')?.country || '';
      variables.language = bookingChannelLanguage.toLowerCase();
    }

    const response = await ApiCalls.makeGraphqlCall<Record<string, unknown>>(queryFile, variables, failIfError, loggedUser);
    if (response.errors) {
      return { errors: response.errors };
    }
    return new HotelAvailability({ hotelAvailabilityApiResponse: response.body.data.hotelAvailability as Record<string, unknown> ?? {} });
  }

  /**
   * GraphQL mutation for CreateReservation
   * @param {Object} params - object parameters
  * @param {CreateReservationInput} createReservationInput - create reservation input to set GraphQL variables
   * @param {Boolean} [params.loggedUser=false] - if reservation is created for logged in user
   * @param {Boolean} [params.failIfError=true] - if it should fail the run on response error
  * @returns {Promise<CreateReservation>} created reservation response
   */
  static async graphqlCreateReservation(
    createReservationInput?: CreateReservationInput,
    loggedUser = false,
    failIfError = true
  ): Promise<CreateReservation> {
    const queryFile = 'createReservation.graphql';
    const variables = {
      reservations: createReservationInput?.reservations,
      bookingChannel: createReservationInput?.bookingChannel,
      bookingFlowId: createReservationInput?.bookingFlowId
    };
    const response = await ApiCalls.makeGraphqlCall<{ createReservation?: Record<string, unknown> }>(queryFile, variables, failIfError, loggedUser);
    const createReservation = response.body.data.createReservation ?? {};
    const createdReservation = new CreateReservation({ createReservation });
    const basket = await ApiBasketCalls.graphqlGetBasketByBasketReference(createdReservation.basketReference ?? '');
    ApiReservationCalls.createdReservations.push(basket); // cache the reservations so we can cancel those after running the tests
    console.log(`Created reservation with "${basket.reference}" basket reference for "${basket.hotelId}" hotel id.`);
    return createdReservation;
  }

  /**
   * GraphQL mutation for CreateReservationGuest
   * @param {Object} params - object parameters
   * @param {String} [params.hotelId=''] - hotel Id for which to add the guest
   * @param {String} [params.basketReference=''] - basket reference from createReservation request
  * @param {GuestDetailsModel} [params.guestDetails] - guest details object
  * @returns {Promise<CreateReservationGuest>} reservation guest response
   */
  static async graphqlCreateReservationGuest({
    hotelId = '',
    basketReference = '',
    guestDetails
  }: CreateReservationGuestParams = {}): Promise<CreateReservationGuest> {
    const queryFile = 'createReservationGuest.graphql';
    if (guestDetails) {
      delete (guestDetails as any).booker?.mobilePrefix;
      delete (guestDetails as any).booker?.landlinePrefix;
    }
    const variables = {
      hotelId,
      basketReference,
      reasonForStay: (guestDetails as any)?.reasonForStay,
      booker: (guestDetails as any)?.booker,
      stayingGuests: (guestDetails as any)?.stayingGuests
    };

    const response = await ApiCalls.makeGraphqlCall<{ createReservationGuest?: Record<string, unknown> }>(queryFile, variables);
    const createReservationGuest = response.body.data.createReservationGuest ?? {};
    return new CreateReservationGuest({ createReservationGuest });
  }

  /**
   * Get hotel inventory and log desired rooms for the request
   * @param {HotelAvailabilityInput} hotelAvailabilityInput - input for which the request is made
   * @returns {Promise<void>} after logging requested and available rooms
   */
  static async getHotelInventoryAndLogRooms(hotelAvailabilityInput: HotelAvailabilityInput): Promise<void> {
    for (const room of hotelAvailabilityInput.rooms ?? []) {
      if ((room as any).roomType?.id !== undefined) {
        (room as any).roomType = (room as any).roomType.id;
      }
    }
    const hotelInventory = await ApiCalls.graphqlGetHotelInventory({
      hotelId: hotelAvailabilityInput.hotelCode,
      dateRangeStart: hotelAvailabilityInput.arrival,
      dateRangeEnd: hotelAvailabilityInput.departure
    });
    console.log(`Requested rooms: ${JSON.stringify(hotelAvailabilityInput.rooms)}`);
    console.log(`Available rooms: ${JSON.stringify((hotelInventory as any).roomTypeInventories)}`);
  }

  /**
   * Confirm a created reservation
   * @param {Object} params - object parameters
    * @param {ReservationInfo<CreateReservation, GuestDetailsModel | undefined>} [params.createdReservation] - initiated reservation information
   * @param {String} [params.paymentOption='RESERVE_WITHOUT_CARD'] - payment type (PAY_ON_ARRIVAL, RESERVE_WITHOUT_CARD, PAY_NOW)
    * @returns {Promise<Array<ConfirmReservationBody>>} confirmation responses
   */
  static async confirmReservation({
    createdReservation,
    paymentOption = 'RESERVE_WITHOUT_CARD'
  }: ConfirmReservationParams = {}): Promise<ConfirmReservationBody[]> {
    let basketInformation;
    if ((global as any).browser?.options?.environment === 'prod') {
      throw new Error('The prod tests shouldn\'t confirm booking!');
    }
    if ((createdReservation as any)?.reservationDetails?.basketReference) {
      basketInformation = await ApiBasketCalls.graphqlGetBasketByBasketReference((createdReservation as any).reservationDetails.basketReference);
    } else {
      throw new Error('Created reservation does not have a basket reference');
    }

    const confirmedReservations: ConfirmReservationBody[] = [];
    const basketItems = (basketInformation as any).items;
    for (const basketItem of basketItems) {
      const confirmReservationInput = await ApiHelpers.createConfirmReservationInput({ createdReservation: createdReservation as unknown as import('../response').ReservationInfo, basketItem: basketItem.sourceId, paymentOption: paymentOption }) as unknown as ConfirmReservationInput;
      confirmedReservations.push(await ApiReservationCalls.confirmReservationRequest({ confirmReservationInput }));
    }
    return confirmedReservations;
  }

  /**
   * Confirm reservation via API request
   * @param {Object} params - object parameters
    * @param {ConfirmReservationInput} [params.confirmReservationInput] - confirm reservation payload
    * @returns {Promise<ConfirmReservationBody>} confirmation response from API
   */
  static async confirmReservationRequest({
    confirmReservationInput
  }: ConfirmReservationRequestParams = {}): Promise<ConfirmReservationBody> {
    const url = (global as any).browser?.options?.entityApiBaseUrl;
    const path = '/ohip/v1/reservations/confirm';
    const requestContext = ApiCalls.getRequestContext();
    const response = await requestContext.fetch(url + path, {
      method: 'POST',
      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json',
      },
      data: JSON.stringify(confirmReservationInput)
    }).catch((err: any) => {
      console.log(JSON.stringify(err));
      throw err;
    });
    const body = await response.json().catch(() => ({}));
    if (!response.ok()) {
      throw new Error(JSON.stringify(body));
    }
    return body as ConfirmReservationBody;
  }

  /**
   * GraphQL mutation for updateReservationPackagesByReservation
   * @param {Object} params - object parameters
   * @param {String} [params.basketReferenceId] - basket reference id
   * @param {String} [params.hotelId] - hotel id
   * @param {String} [params.arrivalDate] - arrival date
   * @param {String} [params.departureDate] - departure date
  * @param {Array<RoomPackageSelectionByIdInput>} [params.roomsSelections] - room selections
  * @param {Array<RoomPackageSelectionByIdInput>} [params.previousRoomsSelections] - previous room selections
  * @returns {Promise<void>} after updating reservation packages
   */
  static async graphqlUpdateReservationPackagesByReservation({
    basketReferenceId,
    hotelId,
    arrivalDate,
    departureDate,
    roomsSelections,
    previousRoomsSelections
  }: UpdateReservationPackagesByReservationParams = {}): Promise<void> {
    const variables = { basketReferenceId, hotelId, arrivalDate, departureDate, roomsSelections, previousRoomsSelections } as Record<string, unknown>;
    const queryFile = 'updateReservationPackagesByReservation.graphql';
    await ApiCalls.makeGraphqlCall(queryFile, variables);
  }

}
