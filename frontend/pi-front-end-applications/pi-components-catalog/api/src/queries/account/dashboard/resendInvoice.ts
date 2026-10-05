import { gql } from 'graphql-request';

export const RESEND_INVOICE_BOOKING_INFORMATION_CARD = gql`
  mutation ResendInvoice(
    $hotelId: String!
    $email: String!
    $bookingReference: String!
    $invoiceRecordNumber: String!
    $bookingChannel: BookingChannelCriteria!
  ) {
    resendInvoiceEmail(
      resendInvoiceRequest: {
        hotelId: $hotelId
        email: $email
        bookingReference: $bookingReference
        invoiceRecordNumber: $invoiceRecordNumber
        bookingChannel: $bookingChannel
      }
    )
  }
`;
