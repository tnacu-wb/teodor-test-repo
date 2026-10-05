import { createAxios } from '../../../../apollo/client/axios';
import { get, post, patch } from '../../../../apollo/client/rest-client';
import { endpoints } from '../../../../apollo/subgraphs/promo-service/services/base-service';
import {
  createPromoBatch,
  getPromoBatchById,
  getPromoBatchSummary,
  markPromoBatchAsDownloadedApi
} from '../../../../apollo/subgraphs/promo-service/services/promo-batch-service';
import * as axiosFactory from '../../../../apollo/client/axios';

jest.mock('../../../../apollo/client/rest-client');

describe('createPromoBatch', () => {
  afterEach(() => {
    jest.resetAllMocks();
  });

  const context = {};
  const promoBatchRequest = {
    hotelId: 'HEAPTI',
    operaPromoCode: 'OPERA123',
    batchCount: 100,
    codeLength: 8,
    expiryDate: '2024-06-08',
    notes: 'Sample notes',
    prefix: 'PROMO',
    requestedBy: 'tester'
  };

  const args = {
    createPromoBatchInput: promoBatchRequest
  };
  it('should call the post function when correct parameters are provided', async () => {
    await createPromoBatch(args, context);

    expect(post).toHaveBeenCalledWith(
      endpoints.GET_BATCHES,
      createPromoBatch,
      promoBatchRequest,
      context
    );
  });
});
describe('getBatchById', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const batchId = 'de07cc8b-ecf4-4491-9e26-69feb872ffa4';

  it('should call the get function with correct parameters', async () => {
    await getPromoBatchById({ batchId }, context);

    expect(get).toHaveBeenCalledWith(
      expect.objectContaining({
        endpoint: `/v1/promo/batches/${batchId}`,
        flowCode: 'DIGITAL_PR_002'
      }),
      getPromoBatchById,
      null,
      context
    );
  });
});

describe('getPromoBatchSummary', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  it('should call the get function with correct parameters', async () => {
    await getPromoBatchSummary({ page: 1, size: 10, sort: 'desc' }, context);

    expect(get).toHaveBeenCalledWith(
      expect.objectContaining({
        endpoint: `/v1/promo/batches`,
        flowCode: 'DIGITAL_PR_003'
      }),
      getPromoBatchSummary,
      {
        page: 1,
        size: 10,
        sort: 'desc'
      },
      context
    );
  });
});

jest.mock('../../../../apollo/client/rest-client', () => ({
  patch: jest.fn()
}));

describe('markPromoBatchAsDownloaded', () => {
  const headers = { 'Content-Type': 'application/json' };
  const res = { status: jest.fn(), json: jest.fn(), setHeader: jest.fn() };
  const context = { headers, res };

  const batchId = 'de07cc8b-ecf4-4491-9e26-69feb872ffa4';

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('markPromoBatchAsDownloaded should call patch with correct endpoint', async () => {
    (patch as jest.Mock).mockResolvedValue({ status: 204 });

    const result = await markPromoBatchAsDownloadedApi({ batchId }, context);

    expect(patch).toHaveBeenCalledWith(
      expect.objectContaining({
        endpoint: `/v1/promo/batches/${batchId}/mark-downloaded`
      }),
      markPromoBatchAsDownloadedApi,
      {},
      context
    );

    expect(result).toBe(true);
  });
});
