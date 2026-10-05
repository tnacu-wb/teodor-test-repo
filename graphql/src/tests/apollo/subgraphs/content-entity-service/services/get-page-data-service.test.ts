import { endpoints } from '../../../../../apollo/subgraphs/content-entity-service/services/base-service';
import { get } from '../../../../../apollo/client/rest-client';
import { getPageData } from '../../../../../apollo/subgraphs/content-entity-service/services/get-page-data-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('getPageData', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };
  const args = { language: 'en', country: 'US' };

  it('should call the get function with correct parameters', async () => {
    const finalMap = { dictionaries: 'LAYOUT_DICTIONARY', language: 'en', country: 'US' };
    const serviceEndpoint = {
      ...endpoints.GET_PAGE_DATA,
      endpoint: expect.stringContaining('/v1/content/innb')
    };

    await getPageData(args, context, {
      fieldNodes: [{ selectionSet: { selections: [{ name: { value: 'layoutEndpoint' } }] } }]
    });

    expect(get).toHaveBeenCalledWith(serviceEndpoint, getPageData, finalMap, context);
  });
});

describe('getPageData', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };
  const args = { language: 'en', country: 'US' };

  it('should handle errors gracefully', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);
    await expect(
      getPageData(args, context, {
        fieldNodes: [{ selectionSet: { selections: [{ name: { value: 'layoutEndpoint' } }] } }]
      })
    ).rejects.toThrow('Test error');
  });
});
