import { endpoints } from '../../../../../apollo/subgraphs/reservation-manager-entity-service/services/base-service';
import { post } from '../../../../../apollo/client/rest-client';
import { resendInvoiceEmail } from '../../../../../apollo/subgraphs/reservation-manager-entity-service/services/resend-invoice-email-service';
import { ResendInvoiceRequest } from '../../../../../apollo/subgraphs/reservation-manager-entity-service/models/resend-invoice-request';

jest.mock('../../../../../apollo/client/rest-client');

describe('resendInvoiceEmail', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const resendInvoiceRequest: ResendInvoiceRequest = {
    bookingChannel: {
      subchannel: 'web',
      channel: 'PI'
    },
    email: 'test@email.com',
    hotelId: 'LONEUS',
    bookingReference: '123456'
  };

  it('should call the post function when correct parameters are provided', async () => {
    await resendInvoiceEmail({ resendInvoiceRequest }, context);
    const endpointWithParams = `${endpoints.RESEND_INVOICE_EMAIL.endpoint}?channel=PI&subchannel=web`;
    const serviceEndpoint = { ...endpoints.RESEND_INVOICE_EMAIL, endpoint: endpointWithParams };

    expect(post).toHaveBeenCalledWith(
      serviceEndpoint,
      resendInvoiceEmail,
      {
        bookingChannel: {
          subchannel: 'web',
          channel: 'PI'
        },
        email: 'test@email.com',
        hotelId: 'LONEUS',
        bookingReference: '123456'
      },
      context
    );
  });

  it('should handle errors gracefully when an error occurs', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);

    await expect(resendInvoiceEmail({ resendInvoiceRequest }, context)).rejects.toThrow(
      'Test error'
    );
  });
});
