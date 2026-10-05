import { BookingChannelCriteria } from './booking-channel-criteria';

export interface RemoveRoomCriteria {
  tempBookingRef: string;
  reservationId: string;
  bookingChannel: BookingChannelCriteria;
  token?: string;
}
