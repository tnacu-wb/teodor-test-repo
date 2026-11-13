import { HotelRates } from './hotelRates';

/** Reservation information for a hotel and guest. */
export interface ReservationInfo<TReservationDetails = unknown, TGuestDetails = unknown> {
  reservationDetails: TReservationDetails;
  guestDetails: TGuestDetails;
  bookingFlowId: string;
  ratePlanCode: string;
}

export class ReservationInfoData {
  private constructor() {}

  /**
   * Create reservation information for a hotel and guest.
   * @param params.reservationDetails Reservation details returned by reservation creation.
   * @param params.guestDetails Guest details object.
   * @param params.bookingFlowId Booking flow identifier.
   * @param params.ratePlanCode Rate plan code. Defaults to PI Flex.
   */
  static createReservationInfo<TReservationDetails = unknown, TGuestDetails = unknown>(params: {
    reservationDetails: TReservationDetails;
    guestDetails: TGuestDetails;
    bookingFlowId: string;
    ratePlanCode?: string;
  }): ReservationInfo<TReservationDetails, TGuestDetails> {
    return {
      reservationDetails: params.reservationDetails,
      guestDetails: params.guestDetails,
      bookingFlowId: params.bookingFlowId,
      ratePlanCode: params.ratePlanCode ?? HotelRates.PI_FLEX.ratePlanCode,
    };
  }
}
