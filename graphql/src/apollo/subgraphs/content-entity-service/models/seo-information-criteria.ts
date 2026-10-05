export class SeoInformationCriteria {
  page: string;
  hotelId?: string;
  language: string;
  country: string;
  bookingFlowId?: string;

  constructor(data: any) {
    this.page = data.page;
    this.hotelId = data.hotelId;
    this.language = data.language;
    this.country = data.country;
    this.bookingFlowId = data.booking;
  }
}
