export class BookingConfirmationAuthenticatedCriteria {
  bookingReference: string;
  country: string;
  language: string;
  bookingChannel?: string;

  constructor(data: any) {
    this.bookingReference = data.bookingReference;
    this.country = data.country;
    this.language = data.language;
    this.bookingChannel = data.booking;
  }
}
