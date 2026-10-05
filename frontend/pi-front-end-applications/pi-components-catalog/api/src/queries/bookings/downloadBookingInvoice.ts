import { gql } from 'graphql-request';

export const downloadBookingInvoiceMutation = () => gql`
  mutation DownloadBookingInvoice($downloadBookingInvoiceRequest: DownloadBookingInvoiceRequest!) {
    downloadBookingInvoice(downloadBookingInvoiceRequest: $downloadBookingInvoiceRequest) {
      invoices {
        bookingRef
        url
        expiresAt
        fileName
        mimeType
        language
        invoiceMeta {
          invoiceNumber
          issuedDate
          hotelId
        }
      }
    }
  }
`;
