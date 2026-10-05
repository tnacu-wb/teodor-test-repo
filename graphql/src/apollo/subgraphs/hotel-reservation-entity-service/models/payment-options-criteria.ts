import { BookingChannelCriteria } from './booking-channel-criteria';

export class PaymentOptionsCriteria {
  bookingChannel: BookingChannelCriteria;
  originalBookingRef: string;
  tempBookingRef: string;
  token: string;
  country: string;

  constructor(data: any) {
    this.bookingChannel = new BookingChannelCriteria(data.bookingChannel);
    this.originalBookingRef = data.originalBookingRef;
    this.tempBookingRef = data.tempBookingRef;
    this.token = data.token;
    this.country = data.country;
  }
}
