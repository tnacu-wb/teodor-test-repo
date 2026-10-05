import { GetServerSidePropsContext } from 'next';
import { ParsedUrlQuery } from 'querystring';

import { getUnleashToggles } from './fetchUnleashToggles';

jest.mock('cookies', () => {
  return jest.fn().mockImplementation((req) => ({
    get: jest.fn((name) => {
      const cookies = req.cookies || {};
      return cookies[name];
    }),
  }));
});

jest.mock('../utils/unleash', () => ({
  getUnleashTogglesServerOrClient: jest.fn(async (cookies, label, flagsWithFallback) => {
    const flags: { [key: string]: boolean } = {};
    const ftOverride = cookies.get('ftOverride');
    const ftOverrideCookie =
      typeof ftOverride === 'string' ? ftOverride : (ftOverride?.value ?? '');
    const ftCookieObj: { [key: string]: boolean } = {};

    if (ftOverrideCookie) {
      ftOverrideCookie.split(',').forEach((fs: string) => {
        const [key, value] = fs.split('=');
        if (key && value) {
          ftCookieObj[key] = value === 'true';
        }
      });
    }

    Object.keys(flagsWithFallback).forEach((flag: string) => {
      if (flag in ftCookieObj) {
        flags[flag] = ftCookieObj[flag];
      } else {
        flags[flag] = flag === 'feature1';
      }
    });

    return flags;
  }),
}));

jest.mock('../utils/tracing', () => ({
  getDefaultSessionTracing: () => ({ 'WB-SESSION-ID': 'test-session' }),
}));

describe('getUnleashToggles', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should evaluate feature toggles using the SDK', async () => {
    const mockReq = {
      url: '/test',
      headers: { host: 'localhost' },
      cookies: {},
    };
    const mockRes = {
      setHeader: jest.fn(),
    };

    const props = {
      req: mockReq,
      res: mockRes,
      query: {} as ParsedUrlQuery,
    } as any as GetServerSidePropsContext<ParsedUrlQuery, any>;

    const label = 'test:page';
    const flagsWithFallback = { feature1: false, feature2: true };

    const result = await getUnleashToggles(props, label, flagsWithFallback);

    expect(result).toEqual({ feature1: true, feature2: false });
  });

  it('should respect ftOverride cookie for testing', async () => {
    const mockReq = {
      url: '/test',
      headers: { host: 'localhost' },
      cookies: { ftOverride: 'feature1=false,feature2=true' },
    };
    const mockRes = {
      setHeader: jest.fn(),
    };

    const props = {
      req: mockReq,
      res: mockRes,
      query: {} as ParsedUrlQuery,
    } as any as GetServerSidePropsContext<ParsedUrlQuery, any>;

    const label = 'test:page';
    const flagsWithFallback = { feature1: true, feature2: false };

    const result = await getUnleashToggles(props, label, flagsWithFallback);

    expect(result).toEqual({ feature1: false, feature2: true });
  });

  it('should use SDK values when no override cookie', async () => {
    const mockReq = {
      url: '/test',
      headers: { host: 'localhost' },
      cookies: {},
    };
    const mockRes = {
      setHeader: jest.fn(),
    };

    const props = {
      req: mockReq,
      res: mockRes,
      query: {} as ParsedUrlQuery,
    } as any as GetServerSidePropsContext<ParsedUrlQuery, any>;

    const label = 'test:page';
    const flagsWithFallback = { feature1: false, feature2: true, feature3: true };

    const result = await getUnleashToggles(props, label, flagsWithFallback);

    expect(result).toEqual({ feature1: true, feature2: false, feature3: false });
  });

  it('should parse complex ftOverride cookie values', async () => {
    const mockReq = {
      url: '/test',
      headers: { host: 'localhost' },
      cookies: { ftOverride: 'flag1=true,flag2=false,flag3=true' },
    };
    const mockRes = {
      setHeader: jest.fn(),
    };

    const props = {
      req: mockReq,
      res: mockRes,
      query: {} as ParsedUrlQuery,
    } as any as GetServerSidePropsContext<ParsedUrlQuery, any>;

    const label = 'test:page';
    const flagsWithFallback = { flag1: false, flag2: true, flag3: false };

    const result = await getUnleashToggles(props, label, flagsWithFallback);

    expect(result).toEqual({ flag1: true, flag2: false, flag3: true });
  });

  it('should apply context when provided', async () => {
    const mockReq = {
      url: '/test',
      headers: { host: 'localhost' },
      cookies: {},
    };
    const mockRes = {
      setHeader: jest.fn(),
    };

    const props = {
      req: mockReq,
      res: mockRes,
      query: {} as ParsedUrlQuery,
    } as any as GetServerSidePropsContext<ParsedUrlQuery, any>;

    const label = 'test:page';
    const flagsWithFallback = { feature1: false };
    const context = { userId: 'user-123', environment: 'test' };

    const result = await getUnleashToggles(props, label, flagsWithFallback, context);

    expect(result).toBeDefined();
    expect(result.feature1).toBe(true);
  });
});
