import { endpoints } from '../../../../../apollo/subgraphs/hotel-card-service-opera/services/base-service';
import { post } from '../../../../../apollo/client/rest-client';
import { retrieveAllPIBACards } from '../../../../../apollo/subgraphs/hotel-card-service-opera/services/get-all-piba-cards-service';
import { PIBACardsCriteria } from '../../../../../apollo/subgraphs/hotel-card-service-opera/models/piba-cards-criteria';

jest.mock('../../../../../apollo/client/rest-client');

describe('retrieveAllPIBACards', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const tetheredUserId = '123_456_789';
  const countryCode = 'GB';
  const pibaCardsCriteria: PIBACardsCriteria = {
    userId: '123_456_789',
    includeCancelledCards: true,
    showMyCards: true,
    pageNumber: 1,
    maxRows: 1
  };

  it('should call the post function with correct parameters when retrieving all PIBA cards', async () => {
    await retrieveAllPIBACards({ tetheredUserId, countryCode, pibaCardsCriteria }, context);
    const allPibaCardsEndpoint = endpoints.GET_ALL_PIBA_CARDS.endpoint.replace(
      '{tetheredUserId}',
      tetheredUserId
    );
    const serviceEndpoint = {
      ...endpoints.GET_ALL_PIBA_CARDS,
      endpoint: `${allPibaCardsEndpoint}?countryCode=GB`
    };

    expect(post).toHaveBeenCalledWith(
      serviceEndpoint,
      retrieveAllPIBACards,
      pibaCardsCriteria,
      context
    );
  });

  it('should handle errors gracefully when retrieving all PIBA cards', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);

    await expect(
      retrieveAllPIBACards({ tetheredUserId, countryCode, pibaCardsCriteria }, context)
    ).rejects.toThrow('Test error');
  });
});
