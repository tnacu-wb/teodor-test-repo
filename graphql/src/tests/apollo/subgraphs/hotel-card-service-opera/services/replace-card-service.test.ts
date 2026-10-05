import { endpoints } from '../../../../../apollo/subgraphs/hotel-card-service-opera/services/base-service';
import { put } from '../../../../../apollo/client/rest-client';
import { replaceCard } from '../../../../../apollo/subgraphs/hotel-card-service-opera/services/replace-card-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('replaceCard', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };
  const tetheredUserGuid = '123_456_789';
  const scheme = 'GB';
  const cardId = '123456';
  const replaceCardRequest = {
    shouldDespatchToCardholder: true,
    contactDetails: {
      title: 'Mr',
      foreName: 'test',
      lastName: 'test'
    },
    address: {
      addressLine1: 'asd',
      addressLine2: 'a',
      postcode: '123ABC',
      countryCode: 'GB'
    }
  };

  it('should call the put function with correct parameters when replaceing the card', async () => {
    await replaceCard({ tetheredUserGuid, cardId, scheme, replaceCardRequest }, context);
    const replaceCardEndpoint = endpoints.REPLACE_CARD.endpoint
      .replace('{tetheredUserGuid}', tetheredUserGuid)
      .replace('{cardId}', cardId);
    const serviceEndpoint = {
      ...endpoints.REPLACE_CARD,
      endpoint: `${replaceCardEndpoint}?scheme=${scheme}`
    };

    expect(put).toHaveBeenCalledWith(serviceEndpoint, replaceCard, replaceCardRequest, context);
  });

  it('should handle errors gracefully when replacing the card', async () => {
    const error = new Error('Test error');
    (put as jest.Mock).mockRejectedValueOnce(error);

    await expect(
      replaceCard({ tetheredUserGuid, cardId, scheme, replaceCardRequest }, context)
    ).rejects.toThrow('Test error');
  });
});
