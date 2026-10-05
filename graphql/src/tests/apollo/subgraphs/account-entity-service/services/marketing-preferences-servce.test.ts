import { endpoints } from '../../../../../apollo/subgraphs/account-entity-service/services/base-service';
import { updateMarketingPreferences } from '../../../../../apollo/subgraphs/account-entity-service/services/marketing-preferences-service';
import { put } from '../../../../../apollo/client/rest-client';

jest.mock('../../../../../apollo/client/rest-client');

describe('updateMarketingPreferences', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  const context = {};
  const updateMarketingPreferencesRequest = {
    brandCodes: ['PINN'],
    optIn: true,
    doubleOptIn: true,
    customer: {
      title: 'Ms',
      firstName: 'Test',
      lastName: 'Pod',
      countryOfResidence: 'DE',
      nationality: 'DE',
      language: 'de',
      customerId: 'testdevoptin11@mailinator.com'
    },
    sourceDetails: {
      channel: 'WEB',
      journey: 'SIGNUP',
      locale: 'DE'
    }
  };

  it('should call the put function with correct parameters when updateMarketingPreferences is called', async () => {
    await updateMarketingPreferences(
      { updateMarketingPreferencesRequest: updateMarketingPreferencesRequest },
      context
    );
    expect(put).toHaveBeenCalledWith(
      endpoints.UPDATE_MARKETING_PREFERENCES,
      updateMarketingPreferences,
      updateMarketingPreferencesRequest,
      context
    );
  });

  it('should handle errors gracefully when put function throws an error', async () => {
    const error = new Error('Test error');
    (put as jest.Mock).mockRejectedValueOnce(error);
    await expect(
      updateMarketingPreferences(
        { updateMarketingPreferencesRequest: updateMarketingPreferencesRequest },
        context
      )
    ).rejects.toThrow('Test error');
  });

  it('should return response when status is 204 when updateMarketingPreferences is called', async () => {
    const response = { status: 204, data: 'success' };
    (put as jest.Mock).mockResolvedValueOnce(response);
    const result = await updateMarketingPreferences(
      { updateMarketingPreferencesRequest: updateMarketingPreferencesRequest },
      context
    );
    expect(result).toEqual(response.data);
  });
});
