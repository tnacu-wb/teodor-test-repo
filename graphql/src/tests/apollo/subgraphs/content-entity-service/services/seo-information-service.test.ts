import { endpoints } from '../../../../../apollo/subgraphs/content-entity-service/services/base-service';
import { get } from '../../../../../apollo/client/rest-client';
import { getSeoInformation } from '../../../../../apollo/subgraphs/content-entity-service/services/seo-information-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('getSeoInformation', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const hotelId = 'HOTEL';
  const page = 'HDP';
  const country = 'gb';
  const language = 'en';

  it('should call the get function with correct parameters when getting SEO information', async () => {
    await getSeoInformation({ hotelId, page, country, language }, context);

    expect(get).toHaveBeenCalledWith(
      endpoints.SEO_INFORMATION,
      getSeoInformation,
      {
        hotelId: 'HOTEL',
        page: 'HDP',
        country: 'gb',
        language: 'en'
      },
      context
    );
  });

  it('should handle errors gracefully when getting SEO information fails', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(getSeoInformation({ hotelId, page, country, language }, context)).rejects.toThrow(
      'Test error'
    );
  });
});
