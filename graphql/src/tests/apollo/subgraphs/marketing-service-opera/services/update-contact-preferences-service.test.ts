import { endpoints } from '../../../../../apollo/subgraphs/marketing-service-opera/services/base-service';
import { put } from '../../../../../apollo/client/rest-client';
import { updateContactPreferences } from '../../../../../apollo/subgraphs/marketing-service-opera/services/update-contact-preferences-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('updateContactPreferences', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const contactType = 'email';
  const contactValue = 'test@email.com';
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

  it('should call the put function when updateContactPreferences is called with correct parameters', async () => {
    (put as jest.Mock).mockReturnValue({ status: 204, data: '' });
    await updateContactPreferences({ contactType, contactValue, request }, context);
    const contactPreferencesEndpoint = endpoints.UPDATE_CONTACT_PREFERENCES.endpoint
      .replace('{contactType}', contactType)
      .replace('{contactValue}', contactValue);
    const serviceEndpoint = {
      ...endpoints.UPDATE_CONTACT_PREFERENCES,
      endpoint: contactPreferencesEndpoint
    };

    expect(put).toHaveBeenCalledWith(serviceEndpoint, updateContactPreferences, request, context);
  });

  it('should handle errors gracefully when updateContactPreferences fails', async () => {
    const error = new Error('Test error');
    (put as jest.Mock).mockRejectedValueOnce(error);

    await expect(
      updateContactPreferences({ contactType, contactValue, request }, context)
    ).rejects.toThrow('Test error');
  });
});
