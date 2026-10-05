import { endpoints } from '../../../../../apollo/subgraphs/reservation-manager-entity-service/services/base-service';
import { post } from '../../../../../apollo/client/rest-client';
import { resendConfirmationEmail } from '../../../../../apollo/subgraphs/reservation-manager-entity-service/services/resend-confirmation-email-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('resendConfirmationEmail', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const resendConfirmationRequest = {
    email: 'test@email.com',
    hotelId: 'LONEUS',
    bookingReference: '123456'
  };

  it('should call the post function when correct parameters are provided', async () => {
    await resendConfirmationEmail({ resendConfirmationRequest }, context);

    expect(post).toHaveBeenCalledWith(
      endpoints.RESEND_CONFIRMATION_EMAIL,
      resendConfirmationEmail,
      {
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

    await expect(resendConfirmationEmail({ resendConfirmationRequest }, context)).rejects.toThrow(
      'Test error'
    );
  });
});
