import { get } from '../../../../../apollo/client/rest-client';
import { endpoints } from '../../../../../apollo/subgraphs/pay-app-entity-service/services/base-service';
import { getWorldlineUserPreferences } from '../../../../../apollo/subgraphs/pay-app-entity-service/services/get-wl-user-preferences-service';

jest.mock('../../../../../apollo/client/rest-client');

const headers = { 'Content-Type': 'application/json' };
const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
const context = { headers, res };

describe('getWlUserPreferences', () => {
  it('should call the get function with correct parameters when getWlUserPreferences is called', async () => {
    await getWorldlineUserPreferences(
      { tetheredUserGuids: '891ad5c1-37b8-4cea-9d9a-96bad8776662' },
      context
    );

    expect(get).toHaveBeenCalledWith(
      endpoints.GET_WORDLINE_USER_PREFERENCES,
      expect.anything(),
      { tetheredUserGuids: '891ad5c1-37b8-4cea-9d9a-96bad8776662' },
      context
    );
  });

  it('should handle errors gracefully when getWlUserPreferences throws an error', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);
    await expect(
      getWorldlineUserPreferences(
        { tetheredUserGuids: '891ad5c1-37b8-4cea-9d9a-96bad8776662' },
        context
      )
    ).rejects.toThrow('Test error');
  });
});
