import { endpoints } from '../../../../../apollo/subgraphs/rules-agent-entity-service/services/base-service';
import { get } from '../../../../../apollo/client/rest-client';
import { retrieveResourceId } from '../../../../../apollo/subgraphs/rules-agent-entity-service/services/search-resource-id-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('retrieveResourceId', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const roleIdList = 'ROLE_TEST';

  it('should call the get function with correct parameters when roleIdList is provided', async () => {
    await retrieveResourceId({ roleIdList }, context);

    expect(get).toHaveBeenCalledWith(
      endpoints.SEARCH_RESOURCE_ID,
      retrieveResourceId,
      { roleIdList: 'ROLE_TEST' },
      context
    );
  });

  it('should handle errors gracefully when get function fails', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(retrieveResourceId({ roleIdList }, context)).rejects.toThrow('Test error');
  });

  it('should return correct values when get function succeeds', async () => {
    const response = { data: { searchResourceId: ['Role1', 'Role2', 'Role3'] } };
    (get as jest.Mock).mockResolvedValueOnce(response);

    const result = await retrieveResourceId({ roleIdList }, context);

    expect(result).toEqual(response);
  });
});
