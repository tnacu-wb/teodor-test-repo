import { endpoints } from './base-service';
import { post } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import { DownloadBookingInvoiceRequest } from '../models/download-booking-invoice-request';
import { InvoiceDownloadResponse } from '../models/download-booking-invoice-response';

/**
 * This method is used to download a booking invoice.
 *
 * @param downloadBookingInvoiceRequest
 * @param context contains the headers and the client
 * @returns invoice download details.
 */
export const downloadBookingInvoice = async (
  {
    downloadBookingInvoiceRequest
  }: { downloadBookingInvoiceRequest: DownloadBookingInvoiceRequest },
  context: any
): Promise<InvoiceDownloadResponse> => {
  try {
    return await post(
      endpoints.DOWNLOAD_BOOKING_INVOICE,
      downloadBookingInvoice,
      downloadBookingInvoiceRequest,
      context
    );
  } catch (error: Error | any) {
    handleError(error, downloadBookingInvoiceRequest);
    throw error;
  }
};
