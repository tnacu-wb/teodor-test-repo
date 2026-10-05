import { BookingChannelCriteria } from './booking-channel-criteria';

export class CancelBookingRequest {
  bookingReference?: string;
  basketReference?: string;
  hotelId: string;
  sourceSystem?: string;
  arrivalDate?: string;
  paymentOption?: string;
  token?: string;
  country?: string;
  language?: string;
  bookingChannel?: BookingChannelCriteria;

  constructor(data: any) {
    this.bookingReference = data.bookingReference;
    this.basketReference = data.basketReference;
    this.hotelId = data.hotelId;
    this.sourceSystem = data.sourceSystem;
    this.arrivalDate = data.arrivalDate;
    this.paymentOption = data.paymentOption;
    this.token = data.token;
    this.country = data.country;
    this.language = data.language;
    this.bookingChannel = new BookingChannelCriteria(data.bookingChannel);
  }
}
