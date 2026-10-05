import { getTransactionsXls } from './getTransactionsXls';

const mockResolveAndDownloadBlob = jest.fn();

jest.mock('../../../formatters', () => ({
  resolveAndDownloadBlob: (...args: unknown[]) => mockResolveAndDownloadBlob(...args),
}));

describe('getTransactionsXls', () => {
  const mockToken = 'mock-token';
  const mockSchemeCustomerId = 123456;
  const mockTetheredUserGuid = '1234567890';
  const mockScheme = 'GB';

  beforeEach(() => {
    jest.clearAllMocks();
    global.fetch = jest.fn();
    global.URL.createObjectURL = jest.fn();
    global.URL.revokeObjectURL = jest.fn();
  });

  it('fetches and downloads transactions xls', async () => {
    (global.fetch as jest.Mock).mockResolvedValue({
      ok: true,
      headers: {
        get: (header: string) => {
          if (header === 'content-disposition') {
            return 'attachment; filename="transactions.xls"';
          }
          return null;
        },
      },
      blob: () => Promise.resolve(new Blob(['mock data'], { type: 'application/vnd.ms-excel' })),
    } as Response);

    await getTransactionsXls(mockToken, mockSchemeCustomerId, mockTetheredUserGuid, mockScheme);

    expect(global.fetch).toHaveBeenCalledWith(
      `${process.env.NEXT_PUBLIC_REST_API}/v2/piba/account/transactions/download/${mockSchemeCustomerId}/${mockTetheredUserGuid}?scheme=${mockScheme}`,
      {
        method: 'GET',
        headers: {
          Authorization: `Bearer ${mockToken}`,
          'WB-Authorization': `Bearer ${mockToken}`,
          accept: 'application/vnd.ms-excel',
        },
        cache: 'no-cache',
      }
    );
    expect(mockResolveAndDownloadBlob).toHaveBeenCalledWith(
      expect.anything(),
      'transactions.xls',
      '.xls'
    );
  });

  it('throws when response not ok', async () => {
    (global.fetch as jest.Mock).mockResolvedValue({
      ok: false,
      headers: { get: () => null },
      blob: () => Promise.resolve(new Blob()),
    } as any);

    await expect(
      getTransactionsXls(mockToken, mockSchemeCustomerId, mockTetheredUserGuid, mockScheme)
    ).rejects.toThrow('Failed to fetch transactions XLS');
  });

  it('returns null when parameters missing', async () => {
    const result = await getTransactionsXls(
      '',
      mockSchemeCustomerId,
      mockTetheredUserGuid,
      mockScheme
    );

    expect(result).toBeNull();
    expect(global.fetch).not.toHaveBeenCalled();
  });
});
