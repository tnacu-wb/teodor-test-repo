import { BookingChannelCriteria } from './booking-channel-criteria';

export class ResendInvoiceRequest {
  email?: string;
  hotelId?: string;
  bookingReference?: string;
  invoiceRecordNumber?: string;
  bookingChannel: BookingChannelCriteria;
  sourceSystem?: string;

  constructor(data: ResendInvoiceRequest) {
    this.email = data.email;
    this.hotelId = data.hotelId;
    this.bookingReference = data.bookingReference;
    this.invoiceRecordNumber = data.invoiceRecordNumber;
    this.bookingChannel = data.bookingChannel;
    this.sourceSystem = data.sourceSystem;
  }
}
