import { renderHook } from '@testing-library/react';
import { useQueryRequest } from '@whitbread-eos/utils';

import { usePromoBatches, shouldRefetchBatchSummary, LIST_REFRESH_TIME } from './use-batch-summary';

jest.mock('date-fns', () => ({
  format: (date: Date) => {
    const pad = (n: number) => String(n).padStart(2, '0');
    const day = pad(date.getUTCDate());
    const monthShort = date.toLocaleString('en-GB', {
      month: 'short',
      timeZone: 'UTC',
    });
    const year = date.getUTCFullYear();
    const hours = pad(date.getUTCHours());
    const minutes = pad(date.getUTCMinutes());
    return `${day} ${monthShort} ${year} | ${hours}:${minutes}`;
  },
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useQueryRequest: jest.fn(),
}));

const mockResponse = {
  getBatchSummary: {
    page: 0,
    size: 10,
    totalPages: 1,
    totalElements: 2,
    hasNext: false,
    items: [
      {
        batchId: 'B123',
        campaignName: 'Winter Sale',
        createdAt: '2024-01-12T10:00:00Z',
        requestedBy: 'Admin',
        operaPromoCode: 'PROMO50',
        batchCount: 20,
        status: 'COMPLETED',
        isMultiple: false,
        downloaded: false,
        batchEligibilities: [
          {
            region: 'UK',
            channel: 'WEB',
            platforms: ['DESKTOP', 'MOBILE'],
          },
        ],
        maxRedemptionLimit: 5,
      },
    ],
  },
};

const mockUseQueryRequest = (response: any) => {
  (useQueryRequest as jest.Mock).mockReturnValue({
    data: response,
    isLoading: false,
    isError: false,
    isFetching: false,
    refetch: jest.fn(),
  });
};

describe('usePromoBatches', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('creates correct sort string using SORT_FIELD_MAP', () => {
    mockUseQueryRequest(mockResponse);

    renderHook(() =>
      usePromoBatches({
        pageIndex: 0,
        pageSize: 10,
        sorting: [{ id: 'created', desc: true }],
      })
    );

    expect(useQueryRequest).toHaveBeenCalledWith(
      ['getBatchSummary', 0, 10, 'createdAt,desc'],
      expect.anything(),
      { page: 0, size: 10, sort: 'createdAt,desc' },
      expect.objectContaining({
        enabled: true,
        refetchInterval: expect.any(Function),
        refetchIntervalInBackground: true,
      })
    );
  });

  it('maps API data into UI format correctly', () => {
    mockUseQueryRequest(mockResponse);

    const { result } = renderHook(() =>
      usePromoBatches({
        pageIndex: 0,
        pageSize: 10,
        sorting: [],
      })
    );

    expect(result.current.data).toEqual([
      {
        batchId: 'B123',
        campaign: 'Winter Sale',
        created: '12 Jan 2024 | 10:00',
        requestedBy: 'Admin',
        promoCode: 'PROMO50',
        amount: 20,
        status: 'Complete',
        isGeneric: 'No',
        eligibility: [
          {
            region: 'UK',
            channel: 'WEB',
            platforms: ['DESKTOP', 'MOBILE'],
          },
        ],
        downloaded: false,
        maxRedemptionLimit: 5,
      },
    ]);
    expect(result.current.total).toBe(2);
    expect(result.current.isError).toBe(false);
  });

  it('maps all statuses correctly', () => {
    const multiStatusResponse = {
      getBatchSummary: {
        ...mockResponse.getBatchSummary,
        items: [
          { ...mockResponse.getBatchSummary.items[0], status: 'FAILED', batchId: 'B1' },
          { ...mockResponse.getBatchSummary.items[0], status: 'PENDING', batchId: 'B2' },
          { ...mockResponse.getBatchSummary.items[0], status: 'RUNNING', batchId: 'B3' },
          { ...mockResponse.getBatchSummary.items[0], status: 'UNKNOWN', batchId: 'B4' },
        ],
      },
    };

    mockUseQueryRequest(multiStatusResponse);

    const { result } = renderHook(() =>
      usePromoBatches({
        pageIndex: 0,
        pageSize: 10,
        sorting: [],
      })
    );

    const statuses = result.current.data.map((d) => d.status);
    expect(statuses).toEqual(['Failed', 'Pending', 'Running', 'UNKNOWN']);
  });

  it('maps EXPIRED status correctly', () => {
    const expiredResponse = {
      getBatchSummary: {
        ...mockResponse.getBatchSummary,
        items: [{ ...mockResponse.getBatchSummary.items[0], status: 'EXPIRED', batchId: 'B5' }],
      },
    };

    mockUseQueryRequest(expiredResponse);

    const { result } = renderHook(() =>
      usePromoBatches({
        pageIndex: 0,
        pageSize: 10,
        sorting: [],
      })
    );

    expect(result.current.data[0].status).toBe('Expired');
  });

  it('handles invalid API response safely', () => {
    mockUseQueryRequest('no healthy upstream');

    const { result } = renderHook(() =>
      usePromoBatches({
        pageIndex: 0,
        pageSize: 10,
        sorting: [],
      })
    );

    expect(result.current.data).toEqual([]);
    expect(result.current.total).toBe(0);
    expect(result.current.isError).toBe(true);
  });
});

describe('shouldRefetchBatchSummary', () => {
  const makeQuery = (data: any) => ({
    state: { data },
  });

  const validBase = {
    getBatchSummary: {
      items: [],
      page: 0,
      size: 10,
      totalPages: 1,
      totalElements: 0,
      hasNext: false,
    },
  };

  beforeEach(() => {
    jest.restoreAllMocks();
  });

  it('returns false when query is undefined', () => {
    expect(shouldRefetchBatchSummary(undefined)).toBe(false);
  });

  it('returns false when query.state is missing', () => {
    expect(shouldRefetchBatchSummary({})).toBe(false);
  });

  it('returns false when query.state.data is undefined', () => {
    expect(shouldRefetchBatchSummary({ state: {} })).toBe(false);
  });

  it('returns false when response shape is invalid', () => {
    expect(shouldRefetchBatchSummary(makeQuery({ foo: 'bar' }))).toBe(false);
  });

  it('returns false when getBatchSummary is null', () => {
    expect(shouldRefetchBatchSummary(makeQuery({ getBatchSummary: null }))).toBe(false);
  });

  it('returns false when items is undefined (?? fallback)', () => {
    const response = {
      getBatchSummary: {},
    };

    expect(shouldRefetchBatchSummary(makeQuery(response))).toBe(false);
  });

  it('returns false when items array is empty', () => {
    expect(shouldRefetchBatchSummary(makeQuery(validBase))).toBe(false);
  });

  it('returns false when items contain no status field', () => {
    const response = {
      ...validBase,
      getBatchSummary: {
        ...validBase.getBatchSummary,
        items: [{ foo: 'bar' }],
      },
    };

    expect(shouldRefetchBatchSummary(makeQuery(response))).toBe(false);
  });

  it('returns false when status is falsy', () => {
    const response = {
      ...validBase,
      getBatchSummary: {
        ...validBase.getBatchSummary,
        items: [{ status: '' }],
      },
    };

    expect(shouldRefetchBatchSummary(makeQuery(response))).toBe(false);
  });

  it('returns false when status not in ACTIVE_STATUS_SET', () => {
    const response = {
      ...validBase,
      getBatchSummary: {
        ...validBase.getBatchSummary,
        items: [{ status: 'COMPLETED' }, { status: 'FAILED' }],
      },
    };

    expect(shouldRefetchBatchSummary(makeQuery(response))).toBe(false);
  });

  it('returns LIST_REFRESH_TIME when RUNNING exists', () => {
    const response = {
      ...validBase,
      getBatchSummary: {
        ...validBase.getBatchSummary,
        items: [{ status: 'COMPLETED' }, { status: 'RUNNING' }],
      },
    };

    expect(shouldRefetchBatchSummary(makeQuery(response))).toBe(LIST_REFRESH_TIME);
  });

  it('returns LIST_REFRESH_TIME when PENDING exists', () => {
    const response = {
      ...validBase,
      getBatchSummary: {
        ...validBase.getBatchSummary,
        items: [{ status: 'PENDING' }],
      },
    };

    expect(shouldRefetchBatchSummary(makeQuery(response))).toBe(LIST_REFRESH_TIME);
  });

  it('returns LIST_REFRESH_TIME when mixed statuses include active one', () => {
    const response = {
      ...validBase,
      getBatchSummary: {
        ...validBase.getBatchSummary,
        items: [{ status: 'FAILED' }, { status: 'PENDING' }, { status: 'COMPLETED' }],
      },
    };

    expect(shouldRefetchBatchSummary(makeQuery(response))).toBe(LIST_REFRESH_TIME);
  });
});
