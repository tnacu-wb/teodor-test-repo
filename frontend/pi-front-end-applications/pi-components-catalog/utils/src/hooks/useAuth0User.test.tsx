import { useQuery } from '@tanstack/react-query';

import { useAuth0User } from './useAuth0User';

jest.mock('@tanstack/react-query', () => ({
  useQuery: jest.fn(),
}));

const mockUseQuery = useQuery as jest.MockedFunction<typeof useQuery>;

describe('useAuth0User', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should return null user and false loading when disabled', () => {
    mockUseQuery.mockReturnValue({
      data: undefined,
      isLoading: false,
      error: null,
      refetch: jest.fn(),
    } as any);

    const result = useAuth0User(false);

    expect(result.user).toBeNull();
    expect(result.loading).toBe(false);
    expect(result.error).toBeNull();
    expect(mockUseQuery).toHaveBeenCalledWith(expect.objectContaining({ enabled: false }));
  });

  it('should return user when enabled and data is available', () => {
    const mockUser = {
      email: 'test@example.com',
      name: 'Test User',
      nickname: 'test',
      picture: 'pic.png',
    };
    mockUseQuery.mockReturnValue({
      data: mockUser,
      isLoading: false,
      error: null,
      refetch: jest.fn(),
    } as any);

    const result = useAuth0User(true);

    expect(result.user).toEqual(mockUser);
    expect(result.loading).toBe(false);
    expect(mockUseQuery).toHaveBeenCalledWith(
      expect.objectContaining({
        enabled: true,
        queryKey: ['auth0User'],
        staleTime: 5 * 60 * 1000,
        retry: 1,
      })
    );
  });

  it('should return null user when data is undefined', () => {
    mockUseQuery.mockReturnValue({
      data: undefined,
      isLoading: false,
      error: null,
      refetch: jest.fn(),
    } as any);

    const result = useAuth0User(true);

    expect(result.user).toBeNull();
  });

  it('should return loading true when query is loading', () => {
    mockUseQuery.mockReturnValue({
      data: null,
      isLoading: true,
      error: null,
      refetch: jest.fn(),
    } as any);

    const result = useAuth0User(true);

    expect(result.loading).toBe(true);
    expect(result.user).toBeNull();
  });

  it('should return error when query fails', () => {
    const mockError = new Error('Network error');
    mockUseQuery.mockReturnValue({
      data: null,
      isLoading: false,
      error: mockError,
      refetch: jest.fn(),
    } as any);

    const result = useAuth0User(true);

    expect(result.error).toBe(mockError);
    expect(result.user).toBeNull();
  });

  it('should call refetch when refetch is invoked', async () => {
    const mockRefetch = jest.fn().mockResolvedValue({});
    mockUseQuery.mockReturnValue({
      data: null,
      isLoading: false,
      error: null,
      refetch: mockRefetch,
    } as any);

    const result = useAuth0User(true);
    await result.refetch();

    expect(mockRefetch).toHaveBeenCalled();
  });

  describe('fetchAuth0User (queryFn)', () => {
    let queryFn: () => Promise<any>;

    beforeEach(() => {
      mockUseQuery.mockReturnValue({
        data: null,
        isLoading: false,
        error: null,
        refetch: jest.fn(),
      } as any);
      useAuth0User(true);
      queryFn = (mockUseQuery.mock.calls[0][0] as any).queryFn;
    });

    it('should call /api/auth/user endpoint', async () => {
      global.fetch = jest.fn().mockResolvedValue({ ok: false });
      await queryFn();
      expect(global.fetch).toHaveBeenCalledWith('/api/auth/user');
    });

    it('should return null when response is not ok', async () => {
      global.fetch = jest.fn().mockResolvedValue({ ok: false });
      const result = await queryFn();
      expect(result).toBeNull();
    });

    it('should return user when response is ok and user exists', async () => {
      const mockUser = { email: 'test@example.com' };
      global.fetch = jest.fn().mockResolvedValue({
        ok: true,
        json: () => Promise.resolve({ user: mockUser }),
      });
      const result = await queryFn();
      expect(result).toEqual(mockUser);
    });

    it('should return null when response is ok but user is null', async () => {
      global.fetch = jest.fn().mockResolvedValue({
        ok: true,
        json: () => Promise.resolve({ user: null }),
      });
      const result = await queryFn();
      expect(result).toBeNull();
    });

    it('should return null when user is missing from response body', async () => {
      global.fetch = jest.fn().mockResolvedValue({
        ok: true,
        json: () => Promise.resolve({}),
      });
      const result = await queryFn();
      expect(result).toBeNull();
    });
  });
});
