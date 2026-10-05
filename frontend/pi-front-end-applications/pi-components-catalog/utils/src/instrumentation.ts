import { initialize } from 'unleash-client';
import type { Unleash } from 'unleash-client';

import { logger } from './logger/logger';

declare global {
  var unleashClient: Unleash | undefined;
}

let initializingUnleashPromise: Promise<void> | null = null;

function getRefreshIntervalFromEnv(): number | undefined {
  const ttlSecondsRaw = process.env.NEXT_APP_UNLEASH_DEFINITIONS_CACHE_TTL;
  if (ttlSecondsRaw === undefined) {
    return undefined;
  }

  const ttlSeconds = Number(ttlSecondsRaw);
  if (!Number.isFinite(ttlSeconds) || ttlSeconds <= 0) {
    logger.warn({
      label: 'UNLEASH_INVALID_CACHE_TTL',
      msg: 'NEXT_APP_UNLEASH_DEFINITIONS_CACHE_TTL must be a positive number',
    });
    return undefined;
  }

  return ttlSeconds * 1000;
}

function getTimeoutFromEnv(): number | undefined {
  const timeoutSecondsRaw = process.env.NEXT_APP_UNLEASH_DEFINITIONS_FETCH_TIMEOUT;
  if (timeoutSecondsRaw === undefined) {
    return undefined;
  }

  const timeoutSeconds = Number(timeoutSecondsRaw);
  if (!Number.isFinite(timeoutSeconds) || timeoutSeconds <= 0) {
    logger.warn({
      label: 'UNLEASH_INVALID_FETCH_TIMEOUT',
      msg: 'NEXT_APP_UNLEASH_DEFINITIONS_FETCH_TIMEOUT must be a positive number',
    });
    return undefined;
  }

  return timeoutSeconds * 1000;
}

function getMetricsIntervalFromEnv(): number | undefined {
  const metricsSecondsRaw = process.env.NEXT_APP_UNLEASH_METRICS_INTERVAL;
  if (metricsSecondsRaw === undefined) {
    return undefined;
  }

  const metricsSeconds = Number(metricsSecondsRaw);
  if (!Number.isFinite(metricsSeconds) || metricsSeconds <= 0) {
    logger.warn({
      label: 'UNLEASH_INVALID_METRICS_INTERVAL',
      msg: 'NEXT_APP_UNLEASH_METRICS_INTERVAL must be a positive number',
    });
    return undefined;
  }

  return metricsSeconds * 1000;
}

function getMetricsJitterFromEnv(): number | undefined {
  const metricsJitterSecondsRaw = process.env.NEXT_APP_UNLEASH_METRICS_JITTER;
  if (metricsJitterSecondsRaw === undefined) {
    return undefined;
  }

  const metricsJitterSeconds = Number(metricsJitterSecondsRaw);
  if (!Number.isFinite(metricsJitterSeconds) || metricsJitterSeconds <= 0) {
    logger.warn({
      label: 'UNLEASH_INVALID_METRICS_JITTER',
      msg: 'NEXT_APP_UNLEASH_METRICS_JITTER must be a positive number',
    });
    return undefined;
  }

  return metricsJitterSeconds * 1000;
}

function getHttpOptions() {
  if (process.env.NODE_ENV === 'development') {
    return {
      httpOptions: {
        rejectUnauthorized: false,
      },
    };
  }
  return {};
}

export async function initializeUnleash(appName: string): Promise<void> {
  if (global?.unleashClient) {
    logger.debug({ label: 'UNLEASH_ALREADY_INITIALIZED' });
    return;
  }

  if (initializingUnleashPromise) {
    logger.debug({ label: 'UNLEASH_INITIALIZATION_IN_PROGRESS' });
    await initializingUnleashPromise;
    return;
  }

  initializingUnleashPromise = (async () => {
    const apiUrl = process.env.NEXT_PUBLIC_UNLEASH_SERVER_API_URL;
    const apiToken = process.env.UNLEASH_SERVER_API_TOKEN;

    if (!apiUrl || !apiToken) {
      logger.warn({
        label: 'UNLEASH_MISSING_CONFIG',
        msg: 'NEXT_PUBLIC_UNLEASH_SERVER_API_URL or UNLEASH_SERVER_API_TOKEN not set',
      });
      return;
    }

    try {
      const refreshInterval = getRefreshIntervalFromEnv();
      const timeout = getTimeoutFromEnv();
      const metricsInterval = getMetricsIntervalFromEnv();
      const metricsJitter = getMetricsJitterFromEnv();
      const httpOptions = getHttpOptions();

      global.unleashClient = initialize({
        url: apiUrl,
        appName,
        customHeaders: {
          Authorization: apiToken,
        },
        ...(refreshInterval ? { refreshInterval } : {}),
        ...(timeout ? { timeout } : {}),
        ...(metricsInterval ? { metricsInterval } : {}),
        ...(metricsJitter ? { metricsJitter } : {}),
        ...httpOptions,
      });
    } catch (err) {
      logger.error({ label: 'UNLEASH_INITIALIZATION_FAILED', msg: { error: err } });
    }

    try {
      await new Promise<void>((resolve) => {
        const timeoutId = setTimeout(() => {
          logger.warn({ label: 'UNLEASH_SYNC_TIMEOUT', msg: 'Timeout waiting for sync after 5s' });
          resolve();
        }, 5000);

        global?.unleashClient!.on('synchronized', () => {
          clearTimeout(timeoutId);
          logger.info({ label: 'UNLEASH_SYNCHRONIZED' });
          resolve();
        });

        global?.unleashClient!.on('error', (err) => {
          clearTimeout(timeoutId);
          logger.error({ label: 'UNLEASH_INIT_ERROR', msg: { error: err } });
          resolve();
        });

        global?.unleashClient!.on('count', (name: string, enabled: boolean) => {
          logger.debug({ label: 'UNLEASH_COUNT', msg: { name, enabled } });
        });

        global?.unleashClient!.on('sent', (data: unknown) => {
          logger.info({ label: 'UNLEASH_METRICS_SENT', msg: data });
        });

        global?.unleashClient!.on('warn', (msg: unknown) => {
          logger.warn({ label: 'UNLEASH_WARN', msg });
        });
      });
    } catch (err) {
      logger.error({ label: 'UNLEASH_INITIALIZATION_FAILED', msg: { error: err } });
    }
  })();

  try {
    await initializingUnleashPromise;
  } finally {
    initializingUnleashPromise = null;
  }
}
