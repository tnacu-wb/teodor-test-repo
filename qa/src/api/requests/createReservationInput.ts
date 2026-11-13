import { BookingChannel } from './bookingChannel';
import { ReservationInfoInput } from './reservationInfoInput';

/**
 * Defines the list of reservations used as input for Create reservation request
 */
export class CreateReservationInput {
  [key: string]: unknown;
  bookingChannel?: BookingChannel;
  bookingFlowId?: string;
  reservations?: ReservationInfoInput[];

  /**
   * Create Reservation Input constructor
   * @param data object data
   * @param data.reservationData Guarantee Code option
   * @param data.bookingChannel the booking channel data,
   * @param data.bookingFlowId the booking flow id
   */
  constructor(data: { reservationData?: ReservationInfoInput[]; bookingChannel?: BookingChannel; bookingFlowId?: string } = {}) {
    this.reservations = data.reservationData;
    this.bookingChannel = data.bookingChannel ?? new BookingChannel();
    this.bookingFlowId = data.bookingFlowId ?? 'booking-a1';
  }

  static fromRequest(data: { reservationData?: ReservationInfoInput[]; bookingChannel?: BookingChannel; bookingFlowId?: string }): CreateReservationInput {
    return new CreateReservationInput(data);
  }

  /**
   * Create a custom reservation input with parameters
   * @param data object data
   * @param data.hotel - hotelId
   * @param data.startDate - booking start date
   * @param data.endDate - booking end date
   * @param data.roomType - booking room type
   * @param data.rate - rate plan
   * @param data.bookingChannel the booking channel data
   */
  static async createCustomReservationInput(data: {
    hotel?: string;
    startDate?: string;
    endDate?: string;
    roomType?: string;
    rate?: string;
    bookingChannel?: BookingChannel;
  }): Promise<CreateReservationInput> {
    const { hotel, startDate, endDate, roomType, rate, bookingChannel = new BookingChannel() } = data;
    return new CreateReservationInput({
      reservationData: await ReservationInfoInput.createCustomReservationInfoInput({ hotel, startDate, endDate, roomType, rate }),
      bookingChannel,
    });
  }
}
