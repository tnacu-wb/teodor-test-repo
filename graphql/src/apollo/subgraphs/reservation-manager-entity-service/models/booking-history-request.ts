import { BookingChannelCriteria } from './booking-channel-criteria';

export class BookingHistoryRequest {
  sortOrder?: string;
  business?: boolean;
  typeOfBooking?: string;
  filterType?: string;
  filterValue?: string;
  continuationToken?: string;
  pageSize?: number;
  pageIndex?: number;
  bookingChannel: BookingChannelCriteria;

  constructor(data: BookingHistoryRequest) {
    this.sortOrder = data.sortOrder;
    this.business = data.business;
    this.typeOfBooking = data.typeOfBooking;
    this.filterType = data.filterType;
    this.filterValue = data.filterValue;
    this.continuationToken = data.continuationToken;
    this.pageSize = data.pageSize;
    this.pageIndex = data.pageIndex;
    this.bookingChannel = data.bookingChannel;
  }
}
