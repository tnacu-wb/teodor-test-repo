import { get } from '../../../../../apollo/client/rest-client';
import { getAccountList } from '../../../../../apollo/subgraphs/piba-account-service-opera/services/get-account-list-service';
import { endpoints } from '../../../../../apollo/subgraphs/piba-account-service-opera/services/base-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('getAccountList Resolver', () => {
  const args = {
    viewAll: true
  };
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const mockResponse = {
    accounts: [{ schemeCustomerId: 1, accountName: 'Account 1' }],
    totalRecordCount: 1
  };

  it('should return account list data when valid parameters are provided', async () => {
    (get as jest.Mock).mockResolvedValueOnce(mockResponse);

    const response = await getAccountList(args, context);

    expect(get).toHaveBeenCalledWith(endpoints.GET_ACCOUNT_LIST, getAccountList, null, context);
    expect(response).toEqual(mockResponse);
  });

  it('should handle null response when fetching account list', async () => {
    (get as jest.Mock).mockResolvedValueOnce(null);

    const response = await getAccountList(args, context);

    expect(response).toEqual(null);
  });

  it('should handle errors gracefully when fetching account list fails', async () => {
    const error = new Error('Failed to fetch account list');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(getAccountList(args, context)).rejects.toThrow('Failed to fetch account list');
  });
});
