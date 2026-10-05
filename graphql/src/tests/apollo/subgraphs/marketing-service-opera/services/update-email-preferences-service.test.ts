import { endpoints } from '../../../../../apollo/subgraphs/marketing-service-opera/services/base-service';
import { put } from '../../../../../apollo/client/rest-client';
import { updateEmailPreferences } from '../../../../../apollo/subgraphs/marketing-service-opera/services/update-email-preferences-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('updateEmailPreferences', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const request = {
    brandCodes: ['PINN'],
    doubleOptIn: false,
    customer: {
      language: 'en',
      countryOfResidence: 'GB',
      firstName: 'Test',
      lastName: 'Test',
      title: 'Mr'
    },
    optIn: true,
    secondPartyOptIn: false,
    thirdPartyVendorsOptIn: false,
    sourceDetails: {
      channel: 'BB'
    }
  };

  it('should call the put function when updateEmailPreferences is called with correct parameters', async () => {
    (put as jest.Mock).mockReturnValue({ status: 204, data: '' });
    await updateEmailPreferences({ request }, context);
    const serviceEndpoint = endpoints.UPDATE_EMAIL_PREFERENCES;

    expect(put).toHaveBeenCalledWith(serviceEndpoint, updateEmailPreferences, request, context);
  });

  it('should handle errors gracefully when updateEmailPreferences fails', async () => {
    const error = new Error('Test error');
    (put as jest.Mock).mockRejectedValueOnce(error);

    await expect(updateEmailPreferences({ request }, context)).rejects.toThrow('Test error');
  });
});
