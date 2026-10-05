import { BIStorageInterface, DEFAULT_REDIS_TTL } from '@whitbread-eos/api';
import RedisClient, { Cluster } from 'ioredis';
import getConfig from 'next/config';

import { logger } from '../logger/logger';

export enum RedisKeyPrefix {
  PI = 'premier-inn',
  BB = 'bb',
  CCUI = 'ccui',
  COMMON = 'fe',
  HRS = 'Hotel-Reservation-Entity-Service::ReservationCache',
  GUEST_DETAILS_FORM_DATA_PI = 'GuestDetailsFormDataPI',
  GUEST_DETAILS_FORM_DATA_CCUI = 'GuestDetailsFormDataCCUI',
}

export class RedisStorageServer implements BIStorageInterface {
  private static instance: RedisStorageServer | null = null;
  private readonly redis: Cluster;

  static getInstance() {
    if (typeof window !== 'undefined') {
      throw new Error('RedisStorage should only be used on the server side.');
    }
    logger.info('Get Redis cluster connection instance');

    if (RedisStorageServer.instance) {
      return RedisStorageServer.instance;
    }

    const { publicRuntimeConfig = {} } = getConfig() || {};
    const { NEXT_PUBLIC_REDIS_HOST, NEXT_PUBLIC_REDIS_PORT } = publicRuntimeConfig;

    logger.info(
      {
        NEXT_PUBLIC_REDIS_HOST,
        NEXT_PUBLIC_REDIS_PORT,
      },
      'Create Redis cluster connection'
    );

    if (!NEXT_PUBLIC_REDIS_HOST || !NEXT_PUBLIC_REDIS_PORT) {
      throw new Error('Redis host or port is not available');
    }

    const cluster = new RedisClient.Cluster(
      [
        {
          port: NEXT_PUBLIC_REDIS_PORT,
          host: NEXT_PUBLIC_REDIS_HOST,
        },
      ],
      {
        scaleReads: 'all',
        dnsLookup: (address, callback) => callback(null, address),
        redisOptions: {
          tls: {},
        },
      }
    );

    RedisStorageServer.instance = new RedisStorageServer(cluster);
    return RedisStorageServer.instance;
  }

  constructor(redis: Cluster) {
    this.redis = redis;

    this.redis.on('error', (error) => {
      logger.info({ error }, 'Redis cluster error');
      this.cleanup();
    });

    this.redis.on('connect', () => {
      logger.info('Redis cluster connected');
    });
  }

  /**
   * Ensures the cache key has a valid prefix.
   * If the key already starts with any of the RedisKeyPrefix values followed by '::', it is returned as is.
   * Otherwise, 'premier-inn::' is prepended as the default prefix.
   *
   * @param key - The cache key to check.
   * @returns The cache key with the appropriate prefix.
   */
  private ensurePrefixedKey(key: string): string {
    const prefixes = Object.values(RedisKeyPrefix);
    for (const prefix of prefixes) {
      if (key.startsWith(`${prefix}::`)) {
        return key;
      }
    }
    return `${RedisKeyPrefix.PI}::${key}`;
  }

  /**
   * Sets a key-value pair in Redis with an optional time-to-live (TTL).
   *
   * @param key - The key under which the value will be stored in Redis.
   * @param value - The string value to store.
   * @param ttlSeconds - The TTL (time-to-live) for the key in seconds. Defaults to `DEFAULT_REDIS_TTL`.
   * @returns A promise that resolves when the item has been set.
   *
   * @remarks
   * Uses the Redis `SET` command with the `EX` option to set the expiration.
   */
  async setItem(key: string, value: string, ttlSeconds: number | string = DEFAULT_REDIS_TTL) {
    try {
      logger.info({ key, value, ttlSeconds }, 'Setting item in Redis');
      const cacheKey = this.ensurePrefixedKey(key);
      await this.redis.set(cacheKey, value, 'EX', ttlSeconds);
    } catch (error) {
      logger.error({ error, key }, 'Error setting item in Redis');
      return;
    }
  }

  async getItem(key: string) {
    try {
      logger.info({ key }, 'Getting item from Redis');
      const cacheKey = this.ensurePrefixedKey(key);
      return await this.redis.get(cacheKey);
    } catch (error) {
      logger.error({ error, key }, 'Error getting item from Redis');
      return null;
    }
  }

  async removeItem(key: string) {
    try {
      logger.info({ key }, 'Removing item from Redis');
      const cacheKey = this.ensurePrefixedKey(key);
      await this.redis.del(cacheKey);
    } catch (error) {
      logger.error({ error, key }, 'Error removing item from Redis');
      return;
    }
  }

  // Manual cleanup method
  async cleanup() {
    try {
      if (this.redis) {
        await this.redis.quit();
      }
    } catch (error) {
      logger.error({ error }, 'Error during cleanup:');
    } finally {
      logger.info('RedisStorageServer.instance CLEANUP');
      RedisStorageServer.instance = null;
    }
  }
}
