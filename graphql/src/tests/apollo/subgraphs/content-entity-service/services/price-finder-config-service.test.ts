import { endpoints } from '../../../../../apollo/subgraphs/content-entity-service/services/base-service';
import { get } from '../../../../../apollo/client/rest-client';
import { getPriceFinderGlobalConfig } from '../../../../../apollo/subgraphs/content-entity-service/services/price-finder-global-config-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('getPriceFinderGlobalConfig', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const channel = 'PI';
  const brand = 'pi';
  const country = 'gb';
  const language = 'en';
  const path = '/some-path';

  it('should call the get function with correct parameters when getting price finder global config', async () => {
    await getPriceFinderGlobalConfig({ channel, brand, country, language, path }, context);

    expect(get).toHaveBeenCalledWith(
      endpoints.PRICE_FINDER_GLOBAL_CONFIG,
      getPriceFinderGlobalConfig,
      {
        channelId: 'PI',
        brand: 'pi',
        country: 'gb',
        language: 'en',
        path: '/some-path'
      },
      context
    );
  });

  it('should return {} when response is null', async () => {
    (get as jest.Mock).mockResolvedValueOnce(null);
    const result = await getPriceFinderGlobalConfig(
      { channel, brand, country, language, path },
      context
    );

    expect(result).toEqual('{}');
  });

  it('should handle errors gracefully when getting price finder config fails', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(
      getPriceFinderGlobalConfig({ channel, brand, country, language, path }, context)
    ).rejects.toThrow('Test error');
  });
});
