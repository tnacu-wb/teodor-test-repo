import { BookingChannelCriteria } from './booking-channel-criteria';

export class CancelInformationCriteria {
  userDateTime: string;
  basketReference: string;
  hotelId: string;
  token?: string;
  bookingChannel: BookingChannelCriteria;

  constructor(data: any) {
    this.userDateTime = data.userDateTime;
    this.basketReference = data.basketReference;
    this.hotelId = data.hotelId;
    this.token = data.token;
    this.bookingChannel = new BookingChannelCriteria(data.bookingChannel);
  }
}
