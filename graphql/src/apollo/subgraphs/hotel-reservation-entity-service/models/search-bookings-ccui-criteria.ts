export class SearchBookingsCcuiCriteria {
  bookingReference?: string;
  bookerLastName?: string;
  guestLastName?: string;
  bookerPostcode?: string;
  hotelId?: string;
  bookerEmail?: string;
  bookerPhone?: string;
  arrivalDateFrom?: string;
  arrivalDateTo?: string;
  cancellationDate?: string;
  companyName?: string;
  thirdPartyBookingReferenceNumber?: string;
  bookingsDatabaseSearch?: boolean;
  pageSize?: number;
  pageNumber?: number;
  continuationToken?: string;

  constructor(data: any) {
    this.bookingReference = data.bookingReference;
    this.bookerLastName = data.bookerLastName;
    this.guestLastName = data.guestLastName;
    this.bookerPostcode = data.bookerPostcode;
    this.hotelId = data.hotelId;
    this.bookerEmail = data.bookerEmail;
    this.bookerPhone = data.bookerPhone;
    this.arrivalDateFrom = data.arrivalDateFrom;
    this.arrivalDateTo = data.arrivalDateTo;
    this.cancellationDate = data.cancellationDate;
    this.companyName = data.companyName;
    this.thirdPartyBookingReferenceNumber = data.thirdPartyBookingReferenceNumber;
    this.bookingsDatabaseSearch = data.bookingsDatabaseSearch;
    this.pageSize = data.pageSize;
    this.pageNumber = data.pageNumber;
    this.continuationToken = data.continuationToken;
  }
}
