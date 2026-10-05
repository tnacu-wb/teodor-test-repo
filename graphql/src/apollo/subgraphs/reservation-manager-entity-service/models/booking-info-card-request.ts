import { BookingChannelCriteria } from './booking-channel-criteria';

export class BookingInfoCardRequest {
  bookingReference: string;
  hotelId: string;
  arrival: string;
  surname: string;
  country?: string;
  language?: string;
  bookingChannel: BookingChannelCriteria;
  sourceSystem?: string;
  token?: string;

  constructor(data: BookingInfoCardRequest) {
    this.bookingReference = data.bookingReference;
    this.hotelId = data.hotelId;
    this.arrival = data.arrival;
    this.surname = data.surname;
    this.country = data.country;
    this.language = data.language;
    this.bookingChannel = data.bookingChannel;
    this.sourceSystem = data.sourceSystem;
    this.token = data.token;
  }
}
