import { endpoints } from '../../../../../apollo/subgraphs/marketing-service-opera/services/base-service';
import { put } from '../../../../../apollo/client/rest-client';
import { updateEmailPreferencesWithChannelId } from '../../../../../apollo/subgraphs/marketing-service-opera/services/update-email-preferences-with-channel-id';

jest.mock('../../../../../apollo/client/rest-client');

describe('updateEmailPreferencesWithChannelId', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const contactChannelId = '12345';
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

  it('should call the put function when updateEmailPreferencesWithChannelId is called with correct parameters', async () => {
    (put as jest.Mock).mockReturnValue({ status: 204, data: '' });
    await updateEmailPreferencesWithChannelId({ contactChannelId, request }, context);

    const expectedEndpointString =
      endpoints.UPDATE_EMAIL_PREFERENCES_WITH_CHANNEL_ID.endpoint.replace(
        '{contactChannelId}',
        contactChannelId
      );

    const expectedServiceEndpoint = {
      ...endpoints.UPDATE_EMAIL_PREFERENCES_WITH_CHANNEL_ID,
      endpoint: expectedEndpointString
    };

    expect(put).toHaveBeenCalledWith(
      expectedServiceEndpoint,
      updateEmailPreferencesWithChannelId,
      request,
      context
    );
  });

  it('should handle errors gracefully when updateEmailPreferencesWithChannelId fails', async () => {
    const error = new Error('Test error');
    (put as jest.Mock).mockRejectedValueOnce(error);

    await expect(
      updateEmailPreferencesWithChannelId({ contactChannelId, request }, context)
    ).rejects.toThrow('Test error');
  });
});
