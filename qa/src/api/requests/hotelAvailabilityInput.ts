import { BookingChannel } from './bookingChannel';
import { Rooms, type Room } from '../../test-data/room';
import type { RoomData } from './room';

/**
 Example of input object for Hotel Availability request
{
  hotelCode: "MANOLD",
  arrival: "2022-09-27",
  departure: "2022-09-28",
  rooms: [
    {
      adultsNumber: 1,
      childrenNumber: 0,
      roomType: "DB",
      cotRequired: false,
    },
  ],
}
 */
export interface HotelAvailabilityInputData {
  hotelCode?: string;
  arrival?: string;
  departure?: string;
  rooms?: Array<Room | RoomData>;
  bookingChannel?: BookingChannel;
  ratePlanCodes?: string[];
}

export class HotelAvailabilityInput {
  [key: string]: unknown;
  hotelCode?: string;
  arrival?: string;
  departure?: string;
  rooms?: Array<Room | RoomData>;
  bookingChannel?: BookingChannel;
  ratePlanCodes?: string[];

  constructor(data: HotelAvailabilityInputData = {}) {
    Object.assign(this, data);
  }

  static fromRequest(data: HotelAvailabilityInputData): HotelAvailabilityInput {
    return new HotelAvailabilityInput(data);
  }

  private static formatIsoDayDate(date: Date): string {
    return date.toISOString().slice(0, 10);
  }

  private static addDays(days: number): string {
    const date = new Date();
    date.setDate(date.getDate() + days);
    return HotelAvailabilityInput.formatIsoDayDate(date);
  }

  /**
   * Create a default hotel availability input
   * @param {String} hotelId Hotel Id for which to get the data
   * @returns {HotelAvailabilityInput} HotelAvailabilityInput object that keeps the data for the request
   */
  static async createDefaultInputForHotelId(hotelId: string = ''): Promise<HotelAvailabilityInput> {
    return new HotelAvailabilityInput({
      hotelCode: hotelId,
      arrival: HotelAvailabilityInput.addDays(1),
      departure: HotelAvailabilityInput.addDays(2),
      rooms: [Rooms.DOUBLE_1_ADULT_0_CHILDREN],
      bookingChannel: new BookingChannel(),
    });
  }

  /**
   * Set a hotel availability for a specified time interval
   * @param {Object} data object data
   * @param {String} data.hotelId Hotel Id for which to get the data
   * @param {String} data.daysNumberForArrivalDate days number for arrival date
   * @param {String} data.daysNumber number of days for hotel reservation
   * @param {Array.<Room>} data.rooms type of rooms
   * @param {BookingChannel} data.bookingChannel bookingChannel
   * @param {Array<String>} data.ratePlanCodes the rate plan codes
   * @returns {HotelAvailabilityInput} HotelAvailabilityInput object that keeps the data for the request
   */
  static async createHotelAvailabilityInputWithInterval({
    hotelId,
    daysNumberForArrivalDate = 1,
    daysNumber,
    rooms = [Rooms.DOUBLE_1_ADULT_0_CHILDREN],
    bookingChannel = new BookingChannel(),
    ratePlanCodes,
  }: {
    hotelId?: string;
    daysNumberForArrivalDate?: number;
    daysNumber?: number;
    rooms?: Room[];
    bookingChannel?: BookingChannel;
    ratePlanCodes?: string[];
  } = {}): Promise<HotelAvailabilityInput> {
    const nights = daysNumber ?? 1;
    return new HotelAvailabilityInput({
      hotelCode: hotelId,
      arrival: HotelAvailabilityInput.addDays(daysNumberForArrivalDate),
      departure: HotelAvailabilityInput.addDays(daysNumberForArrivalDate + nights),
      rooms,
      bookingChannel,
      ratePlanCodes,
    });
  }

  /** 
   * Create a custom hotel availability input with arrival and departure configurable
   * @param {Object} data object data
   * @param {String} data.hotelId Hotel Id for which to get the data
   * @param {String} data.arrival - arrival date for which to get the data
   * @param {String} data.departure - departure date for which to get the data
   * @returns {HotelAvailabilityInput} HotelAvailabilityInput object that keeps the data for the request
   */
  static async createCustomDatesInputForHotelId({ hotelId, arrival, departure }: { hotelId?: string; arrival?: string; departure?: string }): Promise<HotelAvailabilityInput> {
    return new HotelAvailabilityInput({
      hotelCode: hotelId,
      arrival,
      departure,
      rooms: [Rooms.DOUBLE_1_ADULT_0_CHILDREN],
      bookingChannel: new BookingChannel(),
    });
  }

  /**
   * Creates a custom hotel availability input with the possibility to set rooms configurations
   * @param {Object} data object data
   * @param {String} data.hotelId - Hotel code for which to create the input 
   * @param {Array<Room>} data.rooms - Array of room configurations for input
   * @returns {HotelAvailabilityInput} HotelAvailabilityInput object
   */
  static async createInputForHotelCodeAndRooms({ hotelId, rooms }: { hotelId?: string; rooms?: Room[] }): Promise<HotelAvailabilityInput> {
    return new HotelAvailabilityInput({
      hotelCode: hotelId,
      arrival: HotelAvailabilityInput.addDays(1),
      departure: HotelAvailabilityInput.addDays(2),
      rooms,
      bookingChannel: new BookingChannel(),
    });
  }

}
