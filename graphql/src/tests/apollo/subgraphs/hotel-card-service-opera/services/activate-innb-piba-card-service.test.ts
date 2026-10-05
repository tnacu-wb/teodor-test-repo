import { endpoints } from '../../../../../apollo/subgraphs/hotel-card-service-opera/services/base-service';
import { post } from '../../../../../apollo/client/rest-client';
import { activateInnBPIBACard } from '../../../../../apollo/subgraphs/hotel-card-service-opera/services/activate-innb-piba-card-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('activateInnBPIBACard', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };
  const tetheredUserId = '123_456_789';
  const countryCode = 'GB';
  const cardId = '123456';

  it('should call the post function with correct parameters when activating the card', async () => {
    await activateInnBPIBACard({ tetheredUserId, cardId, countryCode }, context);
    const activateCardEndpoint = endpoints.ACTIVATE_INNB_PIBA_CARD.endpoint
      .replace('{tetheredUserId}', tetheredUserId)
      .replace('{cardId}', cardId);
    const serviceEndpoint = {
      ...endpoints.ACTIVATE_INNB_PIBA_CARD,
      endpoint: `${activateCardEndpoint}?countryCode=GB`
    };

    expect(post).toHaveBeenCalledWith(serviceEndpoint, activateInnBPIBACard, {}, context);
  });

  it('should handle errors gracefully when activating the card', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);

    await expect(
      activateInnBPIBACard({ tetheredUserId, cardId, countryCode }, context)
    ).rejects.toThrow('Test error');
  });
});
