import { endpoints } from '../../../../../apollo/subgraphs/marketing-service-opera/services/base-service';
import { get } from '../../../../../apollo/client/rest-client';
import { retrieveContactPreferences } from '../../../../../apollo/subgraphs/marketing-service-opera/services/get-contact-preferences-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('retrieveContactPreferences', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const request = {
    contactType: 'email',
    contactValue: 'test@email.com',
    brandCodes: 'PINN',
    business: false,
    contactChannelId: '',
    countryOfResidence: 'gb',
    language: 'en'
  };

  it('should call the get function when retrieveContactPreferences is called with correct parameters', async () => {
    await retrieveContactPreferences({ request }, context);
    const contactPreferencesEndpoint = endpoints.GET_CONTACT_PREFERENCES.endpoint
      .replace('{contactType}', request.contactType)
      .replace('{contactValue}', request.contactValue);
    const serviceEndpoint = {
      ...endpoints.GET_CONTACT_PREFERENCES,
      endpoint: contactPreferencesEndpoint
    };

    expect(get).toHaveBeenCalledWith(
      serviceEndpoint,
      retrieveContactPreferences,
      {
        brandCodes: 'PINN',
        business: false,
        countryOfResidence: 'gb',
        language: 'en'
      },
      context
    );
  });

  it('should handle errors gracefully when retrieveContactPreferences fails', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(retrieveContactPreferences({ request }, context)).rejects.toThrow('Test error');
  });
});
