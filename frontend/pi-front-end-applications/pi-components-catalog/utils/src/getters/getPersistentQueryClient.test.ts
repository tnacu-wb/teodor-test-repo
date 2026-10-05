import { QueryClient } from '@tanstack/react-query';
import getConfig from 'next/config';

import { logger } from '../logger/logger';
import { RedisStorageServer } from '../storage/RedisStorageServer';
import { getPersistentQueryClient } from './getPersistentQueryClient';

jest.mock('next/config', () => jest.fn());

jest.mock('@whitbread-eos/api', () => ({
  CACHED_QUERIES_LIST: ['GetStaticContent', 'seoInformation'],
  DEFAULT_REDIS_TTL: 30 * 60,
}));

jest.mock('../logger/logger', () => ({
  logger: {
    info: jest.fn(),
    error: jest.fn(),
  },
}));

jest.mock('../storage/RedisStorageServer', () => ({
  RedisStorageServer: {
    getInstance: jest.fn(),
  },
  RedisKeyPrefix: {
    PI: 'premier-inn',
    BB: 'bb',
    CCUI: 'ccui',
    COMMON: 'fe',
  },
}));

describe('getPersistentQueryClient', () => {
  const mockGetConfig = getConfig as jest.MockedFunction<typeof getConfig>;
  const mockGetInstance = RedisStorageServer.getInstance as jest.MockedFunction<
    typeof RedisStorageServer.getInstance
  >;

  const redisStorage = {
    getItem: jest.fn(),
    setItem: jest.fn(),
    removeItem: jest.fn(),
  } as unknown as RedisStorageServer;

  beforeEach(() => {
    jest.clearAllMocks();
    jest.useFakeTimers();
    jest.setSystemTime(new Date('2026-08-12T10:00:00.000Z'));
    delete process.env.NEXT_PUBLIC_REDIS_HOST;
    delete process.env.NEXT_PUBLIC_REDIS_PORT;
    mockGetConfig.mockReturnValue({
      publicRuntimeConfig: {
        NEXT_PUBLIC_REDIS_HOST: 'localhost',
        NEXT_PUBLIC_REDIS_PORT: '6379',
      },
    });
    mockGetInstance.mockReturnValue(redisStorage);
    redisStorage.getItem = jest.fn().mockResolvedValue(null);
    redisStorage.setItem = jest.fn().mockResolvedValue(undefined);
    redisStorage.removeItem = jest.fn().mockResolvedValue(undefined);
  });

  afterEach(() => {
    jest.useRealTimers();
  });

  it('should return a plain query client when cache is disabled', () => {
    const queryClient = getPersistentQueryClient({ enabled: false });

    expect(queryClient).toBeInstanceOf(QueryClient);
    expect(mockGetInstance).not.toHaveBeenCalled();
    expect(queryClient.getDefaultOptions().queries?.persister).toBeUndefined();
  });

  it('should return a plain query client when Redis env is missing', () => {
    mockGetConfig.mockReturnValue({ publicRuntimeConfig: {} });

    const queryClient = getPersistentQueryClient({ enabled: true });

    expect(queryClient).toBeInstanceOf(QueryClient);
    expect(mockGetInstance).not.toHaveBeenCalled();
    expect(queryClient.getDefaultOptions().queries?.persister).toBeUndefined();
  });

  it('should configure the query persister when cache is enabled and Redis env exists', async () => {
    const queryClient = getPersistentQueryClient({ page: 'confirmation', enabled: true });

    expect(queryClient).toBeInstanceOf(QueryClient);
    expect(mockGetInstance).toHaveBeenCalledTimes(1);

    await expect(
      queryClient.fetchQuery({ queryKey: ['GetStaticContent'], queryFn: () => 'fresh-data' })
    ).resolves.toBe('fresh-data');

    expect(redisStorage.getItem).toHaveBeenCalledWith('premier-inn::rq-["GetStaticContent"]');
    expect(redisStorage.setItem).toHaveBeenCalledWith(
      'premier-inn::rq-["GetStaticContent"]',
      JSON.stringify({ timestamp: Date.now(), data: 'fresh-data' })
    );
  });

  it('should reuse cached Redis data across separate query clients', async () => {
    const redisCache = new Map<string, string>();
    redisStorage.getItem = jest.fn((key: string) => Promise.resolve(redisCache.get(key) ?? null));
    redisStorage.setItem = jest.fn((key: string, value: string) => {
      redisCache.set(key, value);
      return Promise.resolve();
    });
    redisStorage.removeItem = jest.fn((key: string) => {
      redisCache.delete(key);
      return Promise.resolve();
    });
    const firstQueryFn = jest.fn().mockResolvedValue({ label: 'cached-static-content' });
    const secondQueryFn = jest.fn().mockResolvedValue({ label: 'fresh-should-not-run' });

    const firstClient = getPersistentQueryClient({ page: 'search', enabled: true });
    const secondClient = getPersistentQueryClient({ page: 'search', enabled: true });

    await expect(
      firstClient.fetchQuery({ queryKey: ['GetStaticContent'], queryFn: firstQueryFn })
    ).resolves.toEqual({ label: 'cached-static-content' });
    await expect(
      secondClient.fetchQuery({ queryKey: ['GetStaticContent'], queryFn: secondQueryFn })
    ).resolves.toEqual({ label: 'cached-static-content' });

    expect(firstQueryFn).toHaveBeenCalledTimes(1);
    expect(secondQueryFn).not.toHaveBeenCalled();
    expect(redisStorage.getItem).toHaveBeenCalledTimes(2);
    expect(redisStorage.setItem).toHaveBeenCalledTimes(1);
  });

  it.each([
    ['premier-inn', 'premier-inn::rq'],
    ['business-booker', 'bb::rq'],
    ['ccui', 'ccui::rq'],
    [undefined, 'premier-inn::rq'],
    ['some-unknown-app', 'premier-inn::rq'],
  ])('namespaces the cache prefix per app (APP_NAME=%s)', async (appName, expectedPrefix) => {
    mockGetConfig.mockReturnValue({
      publicRuntimeConfig: {
        NEXT_PUBLIC_REDIS_HOST: 'localhost',
        NEXT_PUBLIC_REDIS_PORT: '6379',
      },
      serverRuntimeConfig: appName ? { APP_NAME: appName } : {},
    });

    const queryClient = getPersistentQueryClient({ enabled: true });

    await queryClient.fetchQuery({ queryKey: ['GetStaticContent'], queryFn: () => 'fresh-data' });

    expect(redisStorage.getItem).toHaveBeenCalledWith(`${expectedPrefix}-["GetStaticContent"]`);
  });

  it('should only persist allow-listed query keys', async () => {
    const queryClient = getPersistentQueryClient({ enabled: true });

    await expect(
      queryClient.fetchQuery({ queryKey: ['GetStaticContentLabels'], queryFn: () => 'fresh-data' })
    ).resolves.toBe('fresh-data');

    expect(redisStorage.getItem).not.toHaveBeenCalled();
    expect(redisStorage.setItem).not.toHaveBeenCalled();
  });

  it('should log cache hits, misses, and stores through the storage wrapper', async () => {
    redisStorage.getItem = jest
      .fn()
      .mockResolvedValueOnce(JSON.stringify({ timestamp: Date.now(), data: 'cached-value' }))
      .mockResolvedValueOnce(null);
    redisStorage.setItem = jest.fn().mockResolvedValue(undefined);

    const cachedClient = getPersistentQueryClient({ page: 'search', enabled: true });
    const missedClient = getPersistentQueryClient({ page: 'search', enabled: true });

    await expect(
      cachedClient.fetchQuery({ queryKey: ['GetStaticContent'], queryFn: () => 'fresh-data' })
    ).resolves.toBe('cached-value');
    await expect(
      missedClient.fetchQuery({ queryKey: ['seoInformation'], queryFn: () => 'fresh-data' })
    ).resolves.toBe('fresh-data');

    expect(logger.info).toHaveBeenCalledWith(
      { page: 'search', key: 'premier-inn::rq-["GetStaticContent"]' },
      'RQ cache HIT'
    );
    expect(logger.info).toHaveBeenCalledWith(
      { page: 'search', key: 'premier-inn::rq-["seoInformation"]' },
      'RQ cache MISS'
    );
    expect(logger.info).toHaveBeenCalledWith(
      { page: 'search', key: 'premier-inn::rq-["seoInformation"]' },
      'RQ cache STORED'
    );
  });

  it('should refresh stale cached data when cache entry is expired', async () => {
    redisStorage.getItem = jest
      .fn()
      .mockResolvedValue(
        JSON.stringify({ timestamp: Date.now() - 30 * 60 * 1000 - 1, data: 'stale-value' })
      );
    redisStorage.removeItem = jest.fn().mockResolvedValue(undefined);

    const queryClient = getPersistentQueryClient({ enabled: true });

    await expect(
      queryClient.fetchQuery({ queryKey: ['GetStaticContent'], queryFn: () => 'fresh-data' })
    ).resolves.toBe('fresh-data');

    expect(redisStorage.removeItem).toHaveBeenCalledWith('premier-inn::rq-["GetStaticContent"]');
    expect(redisStorage.setItem).toHaveBeenCalledWith(
      'premier-inn::rq-["GetStaticContent"]',
      JSON.stringify({ timestamp: Date.now(), data: 'fresh-data' })
    );
  });

  it('should fetch fresh data when cache read fails', async () => {
    const error = new Error('Redis read failed');
    redisStorage.getItem = jest.fn().mockRejectedValue(error);

    const queryClient = getPersistentQueryClient({ enabled: true });

    await expect(
      queryClient.fetchQuery({ queryKey: ['GetStaticContent'], queryFn: () => 'fresh-data' })
    ).resolves.toBe('fresh-data');

    expect(logger.error).toHaveBeenCalledWith(
      { err: error, key: 'premier-inn::rq-["GetStaticContent"]' },
      'RQ cache READ ERROR'
    );
  });

  it('should fall back to a plain query client when Redis initialization fails', () => {
    const error = new Error('Redis unavailable');
    mockGetInstance.mockImplementation(() => {
      throw error;
    });

    const queryClient = getPersistentQueryClient({ enabled: true });

    expect(queryClient).toBeInstanceOf(QueryClient);
    expect(logger.error).toHaveBeenCalledWith(
      { err: error },
      'getPersistentQueryClient: falling back to non-persistent client'
    );
  });
});
