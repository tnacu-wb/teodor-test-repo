import { endpoints } from '../../../../../apollo/subgraphs/content-entity-service/services/base-service';
import { get } from '../../../../../apollo/client/rest-client';
import { getSearchInformation } from '../../../../../apollo/subgraphs/content-entity-service/services/search-information-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('getSearchInformation', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const country = 'gb';
  const language = 'en';

  it('should call the get function with correct parameters when getting search information', async () => {
    await getSearchInformation({ country, language }, context);

    expect(get).toHaveBeenCalledWith(
      endpoints.SEARCH_INFORMATION,
      getSearchInformation,
      {
        country: 'gb',
        language: 'en'
      },
      context
    );
  });

  it('should handle errors gracefully when getting search information', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(getSearchInformation({ country, language }, context)).rejects.toThrow(
      'Test error'
    );
  });
});
