import { BookingChannelCriteria } from './booking-channel-criteria';

export class FindBookingCriteria {
  resNo: string;
  lastName: string;
  arrivalDate: string;
  country?: string;
  language?: string;
  bookingChannel?: BookingChannelCriteria;

  constructor(data: any) {
    this.resNo = data.resNo;
    this.lastName = data.lastName;
    this.arrivalDate = data.arrivalDate;
    this.country = data.country;
    this.language = data.language;
    this.bookingChannel = data.bookingChannel;
  }
}
