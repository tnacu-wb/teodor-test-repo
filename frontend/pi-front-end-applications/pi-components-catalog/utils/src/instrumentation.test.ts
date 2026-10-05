import { initialize } from 'unleash-client';

import { initializeUnleash } from './instrumentation';
import { logger } from './logger/logger';

let mockOn: jest.Mock;
let mockAutoSyncEvent = true;
let mockSynchronizedCallbacks: Array<() => void> = [];

jest.mock('unleash-client', () => {
  mockOn = jest.fn((event, callback) => {
    if (event === 'synchronized') {
      if (mockAutoSyncEvent) {
        callback();
      } else {
        mockSynchronizedCallbacks.push(callback);
      }
    }
  });

  return {
    initialize: jest.fn(() => ({
      on: mockOn,
    })),
  };
});

jest.mock('./logger/logger', () => ({
  logger: {
    debug: jest.fn(),
    info: jest.fn(),
    warn: jest.fn(),
    error: jest.fn(),
  },
}));

const mockedInitialize = initialize as jest.MockedFunction<typeof initialize>;

describe('initializeUnleash (instrumentation)', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    delete (global as { unleashClient?: unknown }).unleashClient;
    mockAutoSyncEvent = true;
    mockSynchronizedCallbacks = [];

    process.env.NEXT_PUBLIC_UNLEASH_SERVER_API_URL = 'http://localhost:4242/api/frontend';
    process.env.UNLEASH_SERVER_API_TOKEN = 'test-token';
    process.env.NODE_ENV = 'test';

    delete process.env.NEXT_APP_UNLEASH_DEFINITIONS_CACHE_TTL;
    delete process.env.NEXT_APP_UNLEASH_DEFINITIONS_FETCH_TIMEOUT;
    delete process.env.NEXT_APP_UNLEASH_METRICS_INTERVAL;
    delete process.env.NEXT_APP_UNLEASH_METRICS_JITTER;
  });

  it('initializes unleash with required config', async () => {
    await initializeUnleash('ccui');

    expect(mockedInitialize).toHaveBeenCalledWith({
      url: 'http://localhost:4242/api/frontend',
      appName: 'ccui',
      customHeaders: {
        Authorization: 'test-token',
      },
    });
    expect(logger.info).toHaveBeenCalledWith({ label: 'UNLEASH_SYNCHRONIZED' });
  });

  it('returns early when already initialized', async () => {
    (global as { unleashClient?: unknown }).unleashClient = { on: jest.fn() };

    await initializeUnleash('ccui');

    expect(mockedInitialize).not.toHaveBeenCalled();
    expect(logger.debug).toHaveBeenCalledWith({ label: 'UNLEASH_ALREADY_INITIALIZED' });
  });

  it('warns and skips initialization when config is missing', async () => {
    delete process.env.UNLEASH_SERVER_API_TOKEN;

    await initializeUnleash('ccui');

    expect(mockedInitialize).not.toHaveBeenCalled();
    expect(logger.warn).toHaveBeenCalledWith({
      label: 'UNLEASH_MISSING_CONFIG',
      msg: 'NEXT_PUBLIC_UNLEASH_SERVER_API_URL or UNLEASH_SERVER_API_TOKEN not set',
    });
  });

  it('maps valid env timings to milliseconds', async () => {
    process.env.NEXT_APP_UNLEASH_DEFINITIONS_CACHE_TTL = '60';
    process.env.NEXT_APP_UNLEASH_DEFINITIONS_FETCH_TIMEOUT = '3';
    process.env.NEXT_APP_UNLEASH_METRICS_INTERVAL = '120';
    process.env.NEXT_APP_UNLEASH_METRICS_JITTER = '7';

    await initializeUnleash('ccui');

    expect(mockedInitialize).toHaveBeenCalledWith({
      url: 'http://localhost:4242/api/frontend',
      appName: 'ccui',
      customHeaders: {
        Authorization: 'test-token',
      },
      refreshInterval: 60000,
      timeout: 3000,
      metricsInterval: 120000,
      metricsJitter: 7000,
    });
  });

  it('logs warnings for invalid timing env values', async () => {
    process.env.NEXT_APP_UNLEASH_DEFINITIONS_CACHE_TTL = 'bad';
    process.env.NEXT_APP_UNLEASH_DEFINITIONS_FETCH_TIMEOUT = '0';
    process.env.NEXT_APP_UNLEASH_METRICS_INTERVAL = '-1';
    process.env.NEXT_APP_UNLEASH_METRICS_JITTER = 'NaN';

    await initializeUnleash('ccui');

    expect(logger.warn).toHaveBeenCalledWith({
      label: 'UNLEASH_INVALID_CACHE_TTL',
      msg: 'NEXT_APP_UNLEASH_DEFINITIONS_CACHE_TTL must be a positive number',
    });
    expect(logger.warn).toHaveBeenCalledWith({
      label: 'UNLEASH_INVALID_FETCH_TIMEOUT',
      msg: 'NEXT_APP_UNLEASH_DEFINITIONS_FETCH_TIMEOUT must be a positive number',
    });
    expect(logger.warn).toHaveBeenCalledWith({
      label: 'UNLEASH_INVALID_METRICS_INTERVAL',
      msg: 'NEXT_APP_UNLEASH_METRICS_INTERVAL must be a positive number',
    });
    expect(logger.warn).toHaveBeenCalledWith({
      label: 'UNLEASH_INVALID_METRICS_JITTER',
      msg: 'NEXT_APP_UNLEASH_METRICS_JITTER must be a positive number',
    });
  });

  it('sets development-only http options', async () => {
    process.env.NODE_ENV = 'development';

    await initializeUnleash('ccui');

    expect(mockedInitialize).toHaveBeenCalledWith(
      expect.objectContaining({
        httpOptions: {
          rejectUnauthorized: false,
        },
      })
    );
  });

  it('logs initialization errors when initialize throws', async () => {
    mockedInitialize.mockImplementationOnce(() => {
      throw new Error('boom');
    });

    await initializeUnleash('ccui');

    expect(logger.error).toHaveBeenCalledWith(
      expect.objectContaining({
        label: 'UNLEASH_INITIALIZATION_FAILED',
      })
    );
    expect(logger.error).toHaveBeenCalledWith(
      expect.objectContaining({
        label: 'UNLEASH_INITIALIZATION_FAILED',
      })
    );
  });

  it('does not initialize twice when called concurrently', async () => {
    mockAutoSyncEvent = false;

    const first = initializeUnleash('ccui');
    const second = initializeUnleash('ccui');

    expect(mockedInitialize).toHaveBeenCalledTimes(1);

    mockSynchronizedCallbacks.forEach((callback) => callback());
    await Promise.all([first, second]);
  });
});
