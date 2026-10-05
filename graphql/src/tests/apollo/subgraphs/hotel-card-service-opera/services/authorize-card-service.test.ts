import { endpoints } from '../../../../../apollo/subgraphs/hotel-card-service-opera/services/base-service';
import { post } from '../../../../../apollo/client/rest-client';
import { initiateAuthorizeCard } from '../../../../../apollo/subgraphs/hotel-card-service-opera/services/authorize-card-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('initiateAuthorizeCard', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const initiateAuthorizeScaRequest = {
    requestId: '1234_5678_91011',
    environment: 'http://localhost',
    language: 'en',
    bookingReference: 'GAA7360661',
    country: 'de'
  };

  it('should call the post function with correct parameters when initiating authorization', async () => {
    await initiateAuthorizeCard({ initiateAuthorizeScaRequest }, context);

    expect(post).toHaveBeenCalledWith(
      endpoints.AUTHORIZE_CARD,
      initiateAuthorizeCard,
      initiateAuthorizeScaRequest,
      context
    );
  });

  it('should handle errors gracefully when initiating authorization', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);

    await expect(initiateAuthorizeCard({ initiateAuthorizeScaRequest }, context)).rejects.toThrow(
      'Test error'
    );
  });
});
