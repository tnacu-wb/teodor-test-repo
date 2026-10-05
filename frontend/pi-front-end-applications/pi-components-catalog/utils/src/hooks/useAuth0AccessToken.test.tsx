import { useQuery } from '@tanstack/react-query';

import { useAuth0AccessToken } from './useAuth0AccessToken';

// Mock useQuery from @tanstack/react-query
jest.mock('@tanstack/react-query', () => ({
  useQuery: jest.fn(),
}));

const mockUseQuery = useQuery as jest.MockedFunction<typeof useQuery>;

describe('useAuth0AccessToken', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should return null when disabled', () => {
    mockUseQuery.mockReturnValue({
      data: null,
      isLoading: false,
      error: null,
    } as any);

    const result = useAuth0AccessToken(false);

    expect(result.accessToken).toBeNull();
    expect(result.isLoading).toBe(false);
    expect(mockUseQuery).toHaveBeenCalledWith(
      expect.objectContaining({
        enabled: false,
      })
    );
  });

  it('should return access token when enabled and available', () => {
    const mockAccessValue = 'samplevalue';
    mockUseQuery.mockReturnValue({
      data: mockAccessValue,
      isLoading: false,
      error: null,
    } as any);

    const result = useAuth0AccessToken(true);

    expect(result.accessToken).toBe(mockAccessValue);
    expect(result.isLoading).toBe(false);
    expect(mockUseQuery).toHaveBeenCalledWith(
      expect.objectContaining({
        enabled: true,
        queryKey: ['auth0AccessToken'],
        staleTime: 4 * 60 * 1000,
        refetchInterval: expect.any(Function),
        retry: 1,
      })
    );
  });

  describe('refetchInterval', () => {
    let refetchInterval: (query: { state: { status: string } }) => number | false;

    beforeEach(() => {
      mockUseQuery.mockReturnValue({
        data: null,
        isLoading: false,
        error: null,
      } as any);
      useAuth0AccessToken(true);
      refetchInterval = (mockUseQuery.mock.calls[0][0] as any).refetchInterval;
    });

    it('should keep polling every 4 minutes while the query is not in an error state', () => {
      expect(refetchInterval({ state: { status: 'success' } })).toBe(4 * 60 * 1000);
      expect(refetchInterval({ state: { status: 'pending' } })).toBe(4 * 60 * 1000);
    });

    it('should stop polling once the query fails', () => {
      expect(refetchInterval({ state: { status: 'error' } })).toBe(false);
    });
  });

  it('should return null when query data is undefined', () => {
    mockUseQuery.mockReturnValue({
      data: undefined,
      isLoading: false,
      error: null,
    } as any);

    const result = useAuth0AccessToken(true);

    expect(result.accessToken).toBeNull();
  });

  it('should return isLoading true when query is loading', () => {
    mockUseQuery.mockReturnValue({
      data: null,
      isLoading: true,
      error: null,
    } as any);

    const result = useAuth0AccessToken(true);

    expect(result.isLoading).toBe(true);
    expect(result.accessToken).toBeNull();
  });

  it('should return error when query fails', () => {
    const mockError = new Error('Network error');
    mockUseQuery.mockReturnValue({
      data: null,
      isLoading: false,
      error: mockError,
    } as any);

    const result = useAuth0AccessToken(true);

    expect(result.error).toBe(mockError);
    expect(result.accessToken).toBeNull();
  });

  describe('fetchAuth0AccessToken (queryFn)', () => {
    let queryFn: () => Promise<any>;

    beforeEach(() => {
      mockUseQuery.mockReturnValue({
        data: null,
        isLoading: false,
        error: null,
      } as any);
      useAuth0AccessToken(true);
      queryFn = (mockUseQuery.mock.calls[0][0] as any).queryFn;
    });

    it('should call /api/auth/access-token endpoint', async () => {
      global.fetch = jest.fn().mockResolvedValue({ ok: false });
      await queryFn();
      expect(global.fetch).toHaveBeenCalledWith('/api/auth/access-token');
    });

    it('should return null when response is not ok', async () => {
      global.fetch = jest.fn().mockResolvedValue({ ok: false });
      const result = await queryFn();
      expect(result).toBeNull();
    });

    it('should return access token when response is ok and accessToken exists', async () => {
      global.fetch = jest.fn().mockResolvedValue({
        ok: true,
        json: () => Promise.resolve({ accessToken: 'samplevalue' }),
      });
      const result = await queryFn();
      expect(result).toBe('samplevalue');
    });

    it('should return null when response is ok but accessToken is missing', async () => {
      global.fetch = jest.fn().mockResolvedValue({
        ok: true,
        json: () => Promise.resolve({}),
      });
      const result = await queryFn();
      expect(result).toBeNull();
    });
  });
});
