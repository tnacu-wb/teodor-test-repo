import { endpoints } from '../../../../../apollo/subgraphs/content-entity-service/services/base-service';
import { get } from '../../../../../apollo/client/rest-client';
import { getRoomClassConfig } from '../../../../../apollo/subgraphs/content-entity-service/services/room-class-config-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('getRoomClassConfig', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const channel = 'PI';
  const brand = 'pi';
  const country = 'gb';
  const language = 'en';

  it('should call the get function with correct parameters when getting room class config', async () => {
    await getRoomClassConfig({ channel, brand, country, language }, context);

    expect(get).toHaveBeenCalledWith(
      endpoints.ROOM_CLASS_CONFIG,
      getRoomClassConfig,
      {
        channelId: 'PI',
        brand: 'pi',
        country: 'gb',
        language: 'en'
      },
      context
    );
  });

  it('should return {} when response is null', async () => {
    (get as jest.Mock).mockResolvedValueOnce(null);
    const result = await getRoomClassConfig({ channel, brand, country, language }, context);

    expect(result).toEqual('{}');
  });

  it('should handle errors gracefully when getting room class config fails', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(
      getRoomClassConfig({ channel, brand, country, language }, context)
    ).rejects.toThrow('Test error');
  });
});
