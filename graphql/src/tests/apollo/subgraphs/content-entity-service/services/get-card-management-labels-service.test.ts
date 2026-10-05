import { endpoints } from '../../../../../apollo/subgraphs/content-entity-service/services/base-service';
import { get } from '../../../../../apollo/client/rest-client';
import { getCardManagementLabels } from '../../../../../apollo/subgraphs/content-entity-service/services/get-card-management-labels-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('getCardManagementLabels', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };
  const language = 'en';

  it('should call the get function with correct parameters', async () => {
    const finalMap = { language: 'en' };
    const serviceEndpoint = {
      ...endpoints.GET_CARD_MANAGEMENT_LABELS,
      endpoint: expect.stringContaining(`/v1/content/innb/cardmgmt`)
    };

    await getCardManagementLabels({ language }, context);

    expect(get).toHaveBeenCalledWith(serviceEndpoint, getCardManagementLabels, finalMap, context);
  });
});

describe('getCardManagementLabels', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  it('should handle errors gracefully', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);
    const language = 'en';
    await expect(getCardManagementLabels({ language }, context)).rejects.toThrow('Test error');
  });
});
