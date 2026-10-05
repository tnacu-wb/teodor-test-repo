import { endpoints } from '../../../../../apollo/subgraphs/hotel-card-service-opera/services/base-service';
import { get } from '../../../../../apollo/client/rest-client';
import { retrievePIBACardDetails } from '../../../../../apollo/subgraphs/hotel-card-service-opera/services/get-piba-card-details-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('retrievePIBACardDetails', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };
  const tetheredUserId = '123_456_789';
  const countryCode = 'GB';
  const cardId = '123456';

  it('should call the get function with correct parameters when retrieving PIBA card details', async () => {
    await retrievePIBACardDetails({ tetheredUserId, cardId, countryCode }, context);
    const pibaCardDetailsEndpoint = endpoints.GET_PIBA_CARD_DETAILS.endpoint
      .replace('{tetheredUserId}', tetheredUserId)
      .replace('{cardId}', cardId);
    const serviceEndpoint = {
      ...endpoints.GET_PIBA_CARD_DETAILS,
      endpoint: `${pibaCardDetailsEndpoint}?countryCode=GB`
    };

    expect(get).toHaveBeenCalledWith(serviceEndpoint, retrievePIBACardDetails, {}, context);
  });

  it('should handle errors gracefully when retrieving PIBA card details', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(
      retrievePIBACardDetails({ tetheredUserId, cardId, countryCode }, context)
    ).rejects.toThrow('Test error');
  });
});
