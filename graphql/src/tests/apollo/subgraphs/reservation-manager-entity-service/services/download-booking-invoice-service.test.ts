import { endpoints } from '../../../../../apollo/subgraphs/reservation-manager-entity-service/services/base-service';
import { post } from '../../../../../apollo/client/rest-client';
import { downloadBookingInvoice } from '../../../../../apollo/subgraphs/reservation-manager-entity-service/services/download-booking-invoice-service';
import { DownloadBookingInvoiceRequest } from '../../../../../apollo/subgraphs/reservation-manager-entity-service/models/download-booking-invoice-request';
import { InvoiceDownloadResponse } from '../../../../../apollo/subgraphs/reservation-manager-entity-service/models/download-booking-invoice-response';

jest.mock('../../../../../apollo/client/rest-client');

describe('downloadBookingInvoice', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const downloadBookingInvoiceRequest: DownloadBookingInvoiceRequest = {
    bookingRef: ['GAA9674785'],
    lang: 'EN',
    channel: 'PI',
    subChannel: 'DIRECT',
    hotelBrand: 'HUB'
  };

  const downloadBookingInvoiceResponse: InvoiceDownloadResponse = {
    invoices: [
      {
        bookingRef: 'GAA9674785',
        url: 'https://example.com/invoice.pdf',
        expiresAt: '2026-04-01T12:00:00Z',
        fileName: 'invoice-GAA9674785.pdf',
        mimeType: 'application/pdf',
        language: 'EN',
        invoiceMeta: {
          invoiceNumber: 'INV-12345',
          issuedDate: '2026-04-01',
          hotelId: 'WORHIG'
        }
      }
    ]
  };

  it('should call the post function when correct parameters are provided', async () => {
    (post as jest.Mock).mockResolvedValueOnce(downloadBookingInvoiceResponse);

    const response = await downloadBookingInvoice({ downloadBookingInvoiceRequest }, context);

    expect(post).toHaveBeenCalledWith(
      endpoints.DOWNLOAD_BOOKING_INVOICE,
      downloadBookingInvoice,
      {
        bookingRef: ['GAA9674785'],
        lang: 'EN',
        channel: 'PI',
        subChannel: 'DIRECT',
        hotelBrand: 'HUB'
      },
      context
    );
    expect(response).toEqual(downloadBookingInvoiceResponse);
  });

  it('should handle errors gracefully when an error occurs', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);

    await expect(
      downloadBookingInvoice({ downloadBookingInvoiceRequest }, context)
    ).rejects.toThrow('Test error');
  });
});
