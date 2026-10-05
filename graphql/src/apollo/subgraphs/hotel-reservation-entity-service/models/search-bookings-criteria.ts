import { Channel } from '../../hotel-entity-service/models/channel';

export class SearchBookingsCriteria {
  bookingReference?: string;
  bookerLastName?: string;
  guestLastName?: string;
  bookerPostcode?: string;
  hotelId?: string;
  bookerEmail?: string;
  bookerPhone?: string;
  arrivalDate?: string;
  cancellationDate?: string;
  companyName?: string;
  thirdPartyBookingReferenceNumber?: string;
  channel?: Channel;
  offset?: number;
  limit?: number;

  constructor(data: any) {
    this.bookingReference = data.bookingReference;
    this.bookerLastName = data.bookerLastName;
    this.guestLastName = data.guestLastName;
    this.bookerPostcode = data.bookerPostcode;
    this.hotelId = data.hotelId;
    this.bookerEmail = data.bookerEmail;
    this.bookerPhone = data.bookerPhone;
    this.arrivalDate = data.arrivalDate;
    this.cancellationDate = data.cancellationDate;
    this.companyName = data.companyName;
    this.thirdPartyBookingReferenceNumber = data.thirdPartyBookingReferenceNumber;
    this.channel = data.channel;
    this.offset = data.offset;
    this.limit = data.limit;
  }
}
