import { Scheme } from '@whitbread-eos/api';

import { getSpendingSummaryV2 } from './getSpendingSummaryV2';

const mockOkStatus = { value: true };

describe('getSpendingSummaryV2', () => {
  const mockToken = 'mock-token';

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should fetch spending summary successfully', async () => {
    const mockResponse = {
      data: {
        getAccountBalanceSummaryV2: {
          accountName: 'Test Account',
          tetheredGuid: 'tether-123',
          available: { amount: 1000, currencyCode: 'GBP', currencySymbol: '£' },
          currentBalance: { amount: 800, currencyCode: 'GBP', currencySymbol: '£' },
        },
      },
    };

    mockOkStatus.value = true;
    global.fetch = jest.fn(() =>
      Promise.resolve({
        json: () => Promise.resolve(mockResponse),
        ok: mockOkStatus.value,
        headers: new Headers(),
        redirected: false,
        status: 200,
        statusText: 'OK',
        type: 'basic',
        url: '',
        body: null,
        bodyUsed: false,
      } as Response)
    );

    const result = await getSpendingSummaryV2(mockToken, 'GB' as Scheme, 'tether-123');

    expect(global.fetch).toHaveBeenCalledWith(
      expect.any(String),
      expect.objectContaining({
        method: 'POST',
        headers: expect.objectContaining({
          Authorization: 'Bearer mock-token',
          'Content-Type': 'application/json',
        }),
      })
    );
    expect(result).toEqual(mockResponse);
  });

  it('should handle network errors by returning null', async () => {
    global.fetch = jest.fn(() => Promise.reject(new Error('Network error')));

    const result = await getSpendingSummaryV2(mockToken, 'GB' as Scheme, 'tether-123');
    expect(result).toBeNull();
  });

  it('should throw error if no token', async () => {
    await expect(getSpendingSummaryV2('', 'GB' as Scheme, 'tether-123')).rejects.toThrowError(
      'Authentication token is required'
    );
  });
});
