import { endpoints } from '../../../../../apollo/subgraphs/hotel-card-service-opera/services/base-service';
import { put } from '../../../../../apollo/client/rest-client';
import { updateInnBPIBACard } from '../../../../../apollo/subgraphs/hotel-card-service-opera/services/update-innb-piba-card-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('updateInnBPIBACard', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const tetheredUserGuid = 'COMP_123-456';
  const cardId = '1';
  const countryCode = 'GB';
  const updateInnBPIBACardRequest = {
    displayName: 'Appsync PIBA',
    cardLimit: 16,
    restrictCardUsage: true,
    restrictionStart: '2025-11-23',
    restrictionEnd: '2025-12-23'
  };

  it('should call the put function with correct parameters when updating InnB PIBA card', async () => {
    (put as jest.Mock).mockReturnValue({ data: '' });
    await updateInnBPIBACard(
      { tetheredUserGuid, cardId, countryCode, updateInnBPIBACardRequest },
      context
    );
    const updateInnBPIBACardEndpoint = endpoints.UPDATE_INNB_PIBA_CARD.endpoint
      .replace('{tetheredUserGuid}', tetheredUserGuid)
      .replace('{cardId}', cardId);
    const serviceEndpoint = {
      ...endpoints.UPDATE_INNB_PIBA_CARD,
      endpoint: `${updateInnBPIBACardEndpoint}?countryCode=GB`
    };

    expect(put).toHaveBeenCalledWith(
      serviceEndpoint,
      updateInnBPIBACard,
      updateInnBPIBACardRequest,
      context
    );
  });

  it('should handle errors gracefully when updating InnB PIBA card', async () => {
    const error = new Error('Test error');
    (put as jest.Mock).mockRejectedValueOnce(error);

    await expect(
      updateInnBPIBACard(
        { tetheredUserGuid, cardId, countryCode, updateInnBPIBACardRequest },
        context
      )
    ).rejects.toThrow('Test error');
  });
});
