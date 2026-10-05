import { QueryClient } from '@tanstack/react-query';
import type { Query, QueryPersister } from '@tanstack/react-query';
import { CACHED_QUERIES_LIST, DEFAULT_REDIS_TTL } from '@whitbread-eos/api';
import getConfig from 'next/config';

import { logger } from '../logger/logger';
import { RedisKeyPrefix, RedisStorageServer } from '../storage/RedisStorageServer';

interface PersistentQueryClientOptions {
  page?: string;
  enabled?: boolean;
}

interface QueryStorage {
  getItem: (key: string) => Promise<string | null>;
  setItem: (key: string, value: string) => Promise<unknown>;
  removeItem: (key: string) => Promise<void>;
}

interface CachedQuery {
  timestamp: number;
  data: Awaited<ReturnType<Parameters<QueryPersister>[0]>>;
}

const createRedisQueryPersister = ({
  storage,
  maxAge,
  prefix,
  shouldPersistQuery,
}: {
  storage: QueryStorage;
  maxAge: number;
  prefix: string;
  shouldPersistQuery: (query: Query) => boolean;
}): QueryPersister => {
  return async (queryFn, context, query) => {
    if (!shouldPersistQuery(query)) {
      return queryFn(context);
    }

    const key = `${prefix}-${query.queryHash}`;

    try {
      const cachedValue = await storage.getItem(key);

      if (cachedValue != null) {
        const cachedQuery = JSON.parse(cachedValue) as CachedQuery;
        const isFresh = Date.now() - cachedQuery.timestamp <= maxAge;

        if (isFresh) {
          return cachedQuery.data;
        }

        await storage.removeItem(key);
      }
    } catch (error) {
      logger.error({ err: error, key }, 'RQ cache READ ERROR');
    }

    const data = await queryFn(context);

    try {
      await storage.setItem(key, JSON.stringify({ timestamp: Date.now(), data }));
    } catch (error) {
      logger.error({ err: error, key }, 'RQ cache WRITE ERROR');
    }

    return data;
  };
};

const APP_NAME_TO_REDIS_PREFIX: Record<string, RedisKeyPrefix> = {
  'premier-inn': RedisKeyPrefix.PI,
  'business-booker': RedisKeyPrefix.BB,
  ccui: RedisKeyPrefix.CCUI,
};

export const getPersistentQueryClient = ({
  page,
  enabled = true,
}: PersistentQueryClientOptions = {}): QueryClient => {
  const { publicRuntimeConfig = {}, serverRuntimeConfig = {} } = getConfig() || {};
  const { NEXT_PUBLIC_REDIS_HOST: host, NEXT_PUBLIC_REDIS_PORT: port } = publicRuntimeConfig;

  if (!enabled || !(host && port)) {
    return new QueryClient();
  }

  const appName = serverRuntimeConfig.APP_NAME ?? process.env.APP_NAME ?? '';
  const appPrefix = APP_NAME_TO_REDIS_PREFIX[appName] ?? RedisKeyPrefix.PI;

  try {
    const redis = RedisStorageServer.getInstance();
    const storage = {
      getItem: async (key: string) => {
        const value = await redis.getItem(key);
        logger.info({ page, key }, value != null ? 'RQ cache HIT' : 'RQ cache MISS');
        return value;
      },
      setItem: (key: string, value: string) => {
        logger.info({ page, key }, 'RQ cache STORED');
        return redis.setItem(key, value);
      },
      removeItem: (key: string) => redis.removeItem(key),
    };
    const persister = createRedisQueryPersister({
      storage,
      maxAge: DEFAULT_REDIS_TTL * 1000,
      prefix: `${appPrefix}::rq`,
      shouldPersistQuery: (query: Query) =>
        CACHED_QUERIES_LIST.includes(query.queryKey[0] as string),
    });

    return new QueryClient({
      defaultOptions: {
        queries: {
          persister,
        },
      },
    });
  } catch (error) {
    logger.error({ err: error }, 'getPersistentQueryClient: falling back to non-persistent client');
    return new QueryClient();
  }
};
