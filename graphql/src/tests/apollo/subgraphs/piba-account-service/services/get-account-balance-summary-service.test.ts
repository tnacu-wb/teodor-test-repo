import { get } from '../../../../../apollo/client/rest-client';
import {
  getAccountBalanceSummary,
  getAccountBalanceSummaryV2
} from '../../../../../apollo/subgraphs/piba-account-service-opera/services/get-account-balance-summary-service';
import { endpoints } from '../../../../../apollo/subgraphs/piba-account-service-opera/services/base-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('globalConfig', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  it('should call the get function with correct parameters', async () => {
    const args = {
      viewAll: false
    };

    const serviceEndpoint = {
      ...endpoints.GET_ACCOUNT_BALANCE_SUMMARY,
      endpoint: expect.stringContaining('/piba/account/balance/summary')
    };

    await getAccountBalanceSummary(args, context);

    expect(get).toHaveBeenCalledWith(serviceEndpoint, getAccountBalanceSummary, null, context);
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

    await expect(getAccountBalanceSummary({ viewAll: true }, context)).rejects.toThrow(
      'Test error'
    );
  });
});

describe('getAccountBalanceSummaryV2', () => {
  const context = {};
  const input = {
    tetheredUserGuid: 'user-guid-123',
    scheme: 'GB'
  };

  it('should call the get function with correct parameters', async () => {
    const expectedEndpoint = endpoints.GET_ACCOUNT_BALANCE_SUMMARY_V2.endpoint
      .replace('{tetheredUserGuid}', input.tetheredUserGuid)
      .replace('{scheme}', input.scheme);

    const serviceEndpoint = {
      ...endpoints.GET_ACCOUNT_BALANCE_SUMMARY_V2,
      endpoint: expectedEndpoint
    };

    await getAccountBalanceSummaryV2(input, context);

    expect(get).toHaveBeenCalledWith(serviceEndpoint, getAccountBalanceSummaryV2, {}, context);
  });

  it('should handle errors gracefully', async () => {
    const error = new Error('Test error');
    (get as jest.Mock).mockRejectedValueOnce(error);

    await expect(getAccountBalanceSummaryV2(input, context)).rejects.toThrow('Test error');
  });
});
