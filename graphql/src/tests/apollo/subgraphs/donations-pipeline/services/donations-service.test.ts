import { getDonations } from '../../../../../apollo/subgraphs/donations-pipeline/services/donations-service';
import { get } from '../../../../../apollo/client/rest-client';

jest.mock('../../../../../apollo/client/rest-client');

describe('getDonations', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };
  const bookingFlowCriteria = {
    country: 'gb',
    language: 'en',
    hotelId: 'HOTEL_1',
    rateCode: 'RATE_1',
    bookingChannel: 'BB'
  };

  it('should handle errors gracefully when fetching donations', async () => {
    const expectedMessage = 'Hotel information response is empty';
    const expectedJsonError = {
      message: expectedMessage,
      errors: [
        {
          field: 'Error',
          message: expectedMessage
        }
      ],
      errorType: 404
    };
    const expectedError = new Error(JSON.stringify(expectedJsonError));
    (get as jest.Mock).mockReturnValueOnce(Promise.resolve(null));

    await expect(getDonations({ bookingFlowCriteria }, context)).rejects.toThrow(expectedMessage);
  });
});
