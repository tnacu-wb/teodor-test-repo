import '@testing-library/jest-dom';
import Cookies from 'cookies';
import { ParsedUrlQuery } from 'querystring';

import { getUnleashTogglesServerOrClient } from './unleash';

let mockIsEnabled: jest.Mock;

jest.mock('../logger/logger', () => ({
  logger: {
    debug: jest.fn(),
    info: jest.fn(),
    warn: jest.fn(),
    error: jest.fn(),
  },
}));

jest.mock('./getLoggedInUserInfo', () => ({
  __esModule: true,
  default: jest.fn(() => ({
    name: 'Test User',
    profile: {
      sessionId: 'test-session-id',
      isBusiness: false,
      accessLevel: 'guest',
      employeeId: 'emp-1',
      companyId: 'comp-1',
    },
    cdhCompanyId: 'cdh-comp-1',
    cdhEmployeeId: 'cdh-emp-1',
    operaCompanyId: 'opera-comp-1',
  })),
}));

describe('Unleash Integration', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockIsEnabled = jest.fn((flag) => flag === 'enabled-flag');
    (global as { unleashClient?: { isEnabled: jest.Mock } }).unleashClient = {
      isEnabled: mockIsEnabled,
    };

    process.env.NEXT_PUBLIC_UNLEASH_SERVER_API_URL = 'http://localhost:8080';
    process.env.UNLEASH_SERVER_API_TOKEN = 'test-token';
    delete process.env.NEXT_APP_UNLEASH_DEFINITIONS_CACHE_TTL;
    delete process.env.NEXT_APP_UNLEASH_DEFINITIONS_FETCH_TIMEOUT;
    delete process.env.NEXT_APP_UNLEASH_METRICS_INTERVAL;
    delete process.env.NEXT_APP_UNLEASH_METRICS_JITTER;
  });

  afterEach(() => {
    delete (global as { unleashClient?: unknown }).unleashClient;
  });

  describe('getUnleashTogglesServerOrClient', () => {
    it('evaluates flags using the SDK client', async () => {
      const mockCookies = {
        get: jest.fn((key) => {
          if (key === 'id_token') return 'token-value';
          return undefined;
        }),
      } as unknown as Cookies;

      const flagsWithFallback = { 'enabled-flag': false, 'disabled-flag': true };

      const result = await getUnleashTogglesServerOrClient(
        mockCookies,
        'test-page',
        flagsWithFallback,
        '/test',
        {} as ParsedUrlQuery,
        { 'WB-SESSION-ID': 'session-123' },
        {},
        undefined
      );

      expect(result).toEqual({
        'enabled-flag': true,
        'disabled-flag': false,
      });
    });

    it('respects ftOverride cookie for testing', async () => {
      const mockCookies = {
        get: jest.fn((key) => {
          if (key === 'id_token') return 'token-value';
          if (key === 'ftOverride') return 'override-flag=true,another-flag=false';
          return undefined;
        }),
      } as unknown as Cookies;

      const flagsWithFallback = { 'override-flag': false, 'another-flag': true };

      const result = await getUnleashTogglesServerOrClient(
        mockCookies,
        'test-page',
        flagsWithFallback,
        '/test',
        {} as ParsedUrlQuery,
        {},
        {},
        undefined
      );

      expect(result).toEqual({
        'override-flag': true,
        'another-flag': false,
      });
    });

    it('applies override cookie with highest precedence', async () => {
      const mockCookies = {
        get: jest.fn((key) => {
          if (key === 'id_token') return 'token-value';
          if (key === 'ftOverride') return 'flag1=false';
          return undefined;
        }),
      } as unknown as Cookies;

      mockIsEnabled.mockReturnValue(true);

      const flagsWithFallback = { flag1: true };

      const result = await getUnleashTogglesServerOrClient(
        mockCookies,
        'test-page',
        flagsWithFallback,
        '/test',
        {} as ParsedUrlQuery,
        {},
        {},
        undefined
      );

      expect(result).toEqual({ flag1: false });
    });
  });

  describe('getUnleashTogglesServerOrClient - uninitialized client', () => {
    it('falls back to default flag values when client is not initialized', async () => {
      jest.resetModules();
      const freshModule = await import('./unleash');
      delete (global as { unleashClient?: unknown }).unleashClient;

      const mockCookies = {
        get: jest.fn(() => undefined),
      } as unknown as Cookies;

      const flagsWithFallback = { 'some-flag': true, 'other-flag': false };

      const result = await freshModule.getUnleashTogglesServerOrClient(
        mockCookies,
        'test-page',
        flagsWithFallback,
        '/test',
        {} as ParsedUrlQuery,
        {},
        {},
        undefined
      );

      expect(result).toEqual({ 'some-flag': true, 'other-flag': false });
    });
  });
});
