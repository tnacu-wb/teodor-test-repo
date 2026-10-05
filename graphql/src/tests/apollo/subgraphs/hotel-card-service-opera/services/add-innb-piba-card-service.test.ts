import { endpoints } from '../../../../../apollo/subgraphs/hotel-card-service-opera/services/base-service';
import { post } from '../../../../../apollo/client/rest-client';
import { addInnBPIBACard } from '../../../../../apollo/subgraphs/hotel-card-service-opera/services/add-innb-piba-card-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('addInnBPIBACard', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const tetheredUserGuid = '123_456_789';
  const countryCode = 'GB';
  const addInnBPIBACardCriteria = {
    displayName: 'Test Card',
    cardLimit: 16,
    restrictCardUsage: true,
    restrictionStart: '2025-10-23',
    restrictionEnd: '2026-10-23',
    primarySchemeCustomerId: 12345,
    schemeCustomerId: 12345,
    cardDeliveryAddressType: 'COMPANY_REGISTERED_ADDRESS'
  };

  it('should call the post function with correct parameters when adding the card', async () => {
    await addInnBPIBACard({ tetheredUserGuid, countryCode, addInnBPIBACardCriteria }, context);
    const addPibaCardEndpoint = endpoints.ADD_INNB_PIBA_CARD.endpoint.replace(
      '{tetheredUserGuid}',
      tetheredUserGuid
    );
    const serviceEndpoint = {
      ...endpoints.ADD_INNB_PIBA_CARD,
      endpoint: `${addPibaCardEndpoint}?countryCode=GB`
    };

    expect(post).toHaveBeenCalledWith(
      serviceEndpoint,
      addInnBPIBACard,
      addInnBPIBACardCriteria,
      context
    );
  });

  it('should handle errors gracefully when adding the card', async () => {
    const error = new Error('Test error');
    (post as jest.Mock).mockRejectedValueOnce(error);

    await expect(
      addInnBPIBACard({ tetheredUserGuid, countryCode, addInnBPIBACardCriteria }, context)
    ).rejects.toThrow('Test error');
  });
});
