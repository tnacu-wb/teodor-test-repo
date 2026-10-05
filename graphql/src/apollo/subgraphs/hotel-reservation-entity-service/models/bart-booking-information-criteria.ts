export class BartBookingInformationCriteria {
  language?: string;
  country?: string;
  bookingReference: string;
  arrivalDate: string;
  bookerLastName: string;

  constructor(data: BartBookingInformationCriteria) {
    this.language = data.language;
    this.country = data.country;
    this.bookingReference = data.bookingReference;
    this.arrivalDate = data.arrivalDate;
    this.bookerLastName = data.bookerLastName;
  }
}
