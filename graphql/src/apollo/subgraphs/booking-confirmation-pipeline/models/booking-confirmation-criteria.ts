export class BookingConfirmationCriteria {
  basketReference: string;
  country: string;
  language: string;
  bookingChannel?: string;
  flow?: String;

  constructor(data: any) {
    this.basketReference = data.basketReference;
    this.country = data.country;
    this.language = data.language;
    this.bookingChannel = data.booking;
    this.flow = data.flow;
  }
}
