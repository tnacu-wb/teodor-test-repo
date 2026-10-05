import { endpoints } from '../../../../../apollo/subgraphs/content-entity-service/services/base-service';
import { get } from '../../../../../apollo/client/rest-client';
import { homepageAppsContent } from '../../../../../apollo/subgraphs/content-entity-service/services/homepage-apps-content-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('globalConfig', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  it('should call the get function with correct parameters', async () => {
    const args = {
      channel: 'PI',
      subchannel: 'apps',
      language: 'en',
      country: 'gb'
    };

    const finalMap = {
      channel: 'PI',
      subchannel: 'apps',
      language: 'en',
      country: 'gb'
    };

    (get as jest.Mock).mockResolvedValueOnce({
      logo: { imagePath: '/content/dam/global/icons/brand/logo-pi-rest-easy.svg' },
      heading: 'Welcome!',
      destinationCards: [],
      contentCards: [],
      promoCards: [],
      notification: null
    });

    const serviceEndpoint = {
      ...endpoints.HOMEPAGE_APPS_CONTENT,
      endpoint: expect.stringContaining('/v1/content/homepage')
    };

    await homepageAppsContent(args, context);

    expect(get).toHaveBeenCalledWith(serviceEndpoint, homepageAppsContent, finalMap, context);
  });

  it('should handle errors gracefully', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    const args = {
      channel: 'PI',
      subchannel: 'apps',
      language: 'en',
      country: 'gb'
    };

    await expect(homepageAppsContent(args, context)).rejects.toThrow('Test error');
  });
});
