import RedisClient, { Cluster } from 'ioredis';

import { RedisKeyPrefix, RedisStorageServer } from './RedisStorageServer';

const mockSet = jest.fn();
const mockGet = jest.fn();
const mockDel = jest.fn();
const mockOn = jest.fn();
const mockQuit = jest.fn();

jest.mock('ioredis', () => {
  return {
    Cluster: jest.fn(() => ({
      set: mockSet,
      get: mockGet,
      del: mockDel,
      on: mockOn,
      quit: mockQuit,
    })),
  };
});

jest.mock('next/config', () => () => ({
  publicRuntimeConfig: {
    NEXT_PUBLIC_REDIS_HOST: 'localhost',
    NEXT_PUBLIC_REDIS_PORT: 6379,
  },
}));

describe('RedisStorageServer', () => {
  let redisClusterMock: Cluster;
  let redisStorage: RedisStorageServer;

  beforeEach(() => {
    jest.clearAllMocks();
    redisClusterMock = new RedisClient.Cluster([{ port: 6379, host: 'localhost' }], {
      scaleReads: 'all',
    });
    redisStorage = new RedisStorageServer(redisClusterMock);
  });

  describe('getInstance', () => {
    it('should initialize Redis cluster and return an instance', () => {
      Object.defineProperty(global, 'window', {
        value: undefined,
        writable: true,
      });
      const instance = RedisStorageServer.getInstance();
      expect(instance).toBeInstanceOf(RedisStorageServer);
    });

    it('should return exisitng instance and not create a new redis connection', () => {
      Object.defineProperty(global, 'window', {
        value: undefined,
        writable: true,
      });
      const instance1 = RedisStorageServer.getInstance();
      const instance2 = RedisStorageServer.getInstance();

      expect(instance1).toBeInstanceOf(RedisStorageServer);
      expect(instance2).toBeInstanceOf(RedisStorageServer);
    });

    it('should throw error if called on client side', () => {
      Object.defineProperty(global, 'window', {
        value: {},
        writable: true,
      });
      expect(() => RedisStorageServer.getInstance()).toThrow(
        'RedisStorage should only be used on the server side.'
      );
    });
  });

  describe('event listeners', () => {
    it('should set up event listeners for redis events', () => {
      expect(mockOn).toHaveBeenCalledWith('connect', expect.any(Function));
      expect(mockOn).toHaveBeenCalledWith('error', expect.any(Function));
    });

    it('should not quit cluster when connected', () => {
      const errorhandler = mockOn.mock.calls.find((call) => call[0] === 'connect')[1];
      errorhandler();

      expect(mockQuit).not.toHaveBeenCalled();
    });

    it('should quit cluster when error', () => {
      const errorhandler = mockOn.mock.calls.find((call) => call[0] === 'error')[1];
      errorhandler();

      expect(mockQuit).toHaveBeenCalled();
    });
  });

  describe('setItem', () => {
    it('should set an item in Redis with default TTL', async () => {
      await redisStorage.setItem(`${RedisKeyPrefix.COMMON}::key`, 'value');
      expect(mockSet).toHaveBeenCalledWith(
        `${RedisKeyPrefix.COMMON}::key`,
        'value',
        'EX',
        expect.anything()
      );
    });

    it('should set an item with prefix added when not present cache key in Redis with default TTL', async () => {
      await redisStorage.setItem('key', 'value');
      expect(mockSet).toHaveBeenCalledWith(
        `${RedisKeyPrefix.PI}::key`,
        'value',
        'EX',
        expect.anything()
      );
    });

    it('should handle error during setItem', async () => {
      mockSet.mockRejectedValueOnce(new Error('setItem error'));
      expect(async () => await redisStorage.setItem('key', 'value')).not.toThrow();
    });
  });

  describe('getItem', () => {
    it('should get an item from Redis', async () => {
      mockGet.mockResolvedValue('value');
      const result = await redisStorage.getItem('key');
      expect(mockGet).toHaveBeenCalledWith(`${RedisKeyPrefix.PI}::key`);
      expect(result).toBe('value');
    });

    it('should handle error during getItem', async () => {
      mockGet.mockRejectedValueOnce(new Error('getItem error'));
      expect(async () => await redisStorage.getItem('key')).not.toThrow();
    });
  });

  describe('removeItem', () => {
    it('should remove an item from Redis', async () => {
      await redisStorage.removeItem(`${RedisKeyPrefix.COMMON}::key`);
      expect(mockDel).toHaveBeenCalledWith(`${RedisKeyPrefix.COMMON}::key`);
    });

    it('should handle error during removeItem', async () => {
      mockDel.mockRejectedValueOnce(new Error('removeItem error'));
      expect(async () => await redisStorage.removeItem('key')).not.toThrow();
    });
  });

  describe('cleanup', () => {
    it('should call quit and set isConnected to false', async () => {
      await redisStorage.cleanup();
      expect(mockQuit).toHaveBeenCalled();
    });

    it('should handle error during quit', async () => {
      mockQuit.mockRejectedValueOnce(new Error('quit error'));
      expect(async () => await redisStorage.cleanup()).not.toThrow();
    });
  });
});
