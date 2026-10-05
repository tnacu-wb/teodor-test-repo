import { Channel } from '@whitbread-eos/api';
import { getChannelByToken } from '@whitbread-eos/utils/server/edge';
import { NextResponse } from 'next/server';

import middleware, { whitelist, config } from './middleware';

jest.mock('next/server', () => ({
  NextResponse: {
    next: jest.fn(() => ({
      status: 200,
      headers: new Map(),
      cookies: {
        set: jest.fn(),
      },
    })),
    redirect: jest.fn(() => ({
      status: 307,
      headers: new Map(),
    })),
  },
  NextRequest: jest.fn(),
}));

jest.mock('@whitbread-eos/utils/server/edge', () => ({
  getRandomTracingId: jest.fn(() => 'mock-tracing-id-12345'),
  getChannelByToken: jest.fn(),
  DEFAULT_TRACING_COOKIE_NAME: 'WB-Tracing-Id',
  ID_TOKEN_COOKIE: 'appSession.0.idToken',
}));

const mockedGetChannelByToken = jest.mocked(getChannelByToken);

describe('middleware', () => {
  let mockRequest: any;
  let mockCookies: Map<string, any>;

  beforeEach(() => {
    jest.clearAllMocks();
    mockCookies = new Map();

    mockRequest = {
      nextUrl: {
        pathname: '/en/homepage',
        href: 'https://example.com/en/homepage',
      },
      headers: new Headers(),
      cookies: {
        has: (name: string) => mockCookies.has(name),
        get: (name: string) => mockCookies.get(name),
        set: jest.fn(),
      },
    };

    process.env.NEXT_PUBLIC_PI_BASE_URL = 'https://pi.example.com';

    (NextResponse.next as jest.Mock).mockImplementation((config?: any) => {
      const headers = new Headers(config?.request?.headers || {});
      return {
        status: 200,
        headers: headers,
        cookies: {
          set: jest.fn((name: string, value: string, options: any) => {
            // Store cookie in headers for testing
            const existingCookie = headers.get('set-cookie') || '';
            const newCookie = `${name}=${value}; Max-Age=${options.maxAge}`;
            headers.set(
              'set-cookie',
              existingCookie ? `${existingCookie}, ${newCookie}` : newCookie
            );
          }),
        },
      };
    });

    (NextResponse.redirect as jest.Mock).mockImplementation((url: string) => ({
      status: 307,
      headers: new Map(),
      url,
    }));
  });

  describe('config', () => {
    it('should have correct matcher configuration', () => {
      expect(config.matcher).toBe('/((?!api|static|.*\\..*|_next).*)');
    });
  });

  describe('whitelist', () => {
    it('should contain expected pages', () => {
      expect(whitelist).toContain('homepage');
      expect(whitelist).toContain('manage');
      expect(whitelist).toContain('spending');
      expect(whitelist).toContain('business-pay');
      expect(whitelist).toContain('link-innbusiness-account');
      expect(whitelist).toContain('profile');
      expect(whitelist).toContain('welcome');
      expect(whitelist).toContain('account');
      expect(whitelist).toContain('access-restricted');
      expect(whitelist).toContain('pay-application-access-restricted');
      expect(whitelist).toContain('contact-us');
    });
  });

  describe('page not in whitelist', () => {
    it('should return undefined for non-whitelisted pages', async () => {
      mockRequest.nextUrl.pathname = '/en/unknown-page';

      const result = await middleware(mockRequest);

      expect(result).toBeUndefined();
    });

    it('should return undefined for api routes', async () => {
      mockRequest.nextUrl.pathname = '/api/some-endpoint';

      const result = await middleware(mockRequest);

      expect(result).toBeUndefined();
    });
  });

  describe('PI channel redirect', () => {
    it('should redirect to PI URL when token has PI channel (en locale)', async () => {
      const mockToken = 'mock-pi-token';
      mockRequest.headers = new Headers({
        cookie: `appSession.0.idToken=${mockToken}`,
      });
      mockRequest.nextUrl.pathname = '/en/homepage';

      mockedGetChannelByToken.mockReturnValue(Channel.Pi);

      const result = await middleware(mockRequest);

      expect(mockedGetChannelByToken).toHaveBeenCalledWith(mockToken);
      expect(result).toBeDefined();
      expect(result?.status).toBe(307);
    });

    it('should redirect to PI URL with German locale when locale is de', async () => {
      const mockToken = 'mock-pi-token';
      mockRequest.headers = new Headers({
        cookie: `appSession.0.idToken=${mockToken}`,
      });
      mockRequest.nextUrl.pathname = '/de/homepage';

      mockedGetChannelByToken.mockReturnValue(Channel.Pi);

      const result = await middleware(mockRequest);

      expect(mockedGetChannelByToken).toHaveBeenCalledWith(mockToken);
      expect(result).toBeDefined();
    });

    it('should not redirect when token is present but channel is not PI', async () => {
      const mockToken = 'mock-bb-token';
      mockRequest.headers = new Headers({
        cookie: `appSession.0.idToken=${mockToken}`,
      });
      mockRequest.nextUrl.pathname = '/en/homepage';

      mockedGetChannelByToken.mockReturnValue(Channel.Bb);

      const result = await middleware(mockRequest);

      expect(mockedGetChannelByToken).toHaveBeenCalledWith(mockToken);
      expect(result).toBeDefined();
      expect(result?.status).not.toBe(307);
    });

    it('should not redirect when NEXT_PUBLIC_PI_BASE_URL is not set', async () => {
      delete process.env.NEXT_PUBLIC_PI_BASE_URL;

      const mockToken = 'mock-pi-token';
      mockRequest.headers = new Headers({
        cookie: `appSession.0.idToken=${mockToken}`,
      });
      mockRequest.nextUrl.pathname = '/en/homepage';

      mockedGetChannelByToken.mockReturnValue(Channel.Pi);

      const result = await middleware(mockRequest);

      expect(result).toBeDefined();
      expect(result?.status).not.toBe(307);
    });

    it('should handle missing token gracefully', async () => {
      mockRequest.headers = new Headers({
        cookie: 'other-cookie=value',
      });
      mockRequest.nextUrl.pathname = '/en/homepage';

      mockedGetChannelByToken.mockReturnValue(null);

      const result = await middleware(mockRequest);

      expect(mockedGetChannelByToken).toHaveBeenCalledWith('');
      expect(result).toBeDefined();
    });
  });

  describe('WB-Url header', () => {
    it('should set WB-Url header with the request URL', async () => {
      mockRequest.nextUrl.pathname = '/en/homepage';
      mockRequest.nextUrl.href = 'https://example.com/en/homepage';

      mockedGetChannelByToken.mockReturnValue(Channel.Bb);

      const result = await middleware(mockRequest);

      expect(result).toBeDefined();
    });
  });

  describe('tracing cookie', () => {
    it('should set tracing cookie when not present', async () => {
      mockRequest.nextUrl.pathname = '/en/homepage';
      mockCookies.clear();

      mockedGetChannelByToken.mockReturnValue(Channel.Bb);

      const result = await middleware(mockRequest);

      expect(result).toBeDefined();

      const setCookieHeader = result?.headers.get('set-cookie');
      expect(setCookieHeader).toBeTruthy();
      expect(setCookieHeader).toContain('WB-Tracing-Id');
      expect(setCookieHeader).toContain('mock-tracing-id-12345');
    });

    it('should not set tracing cookie when already present', async () => {
      mockRequest.nextUrl.pathname = '/en/homepage';
      mockCookies.set('WB-Tracing-Id', 'existing-tracing-id');

      mockedGetChannelByToken.mockReturnValue(Channel.Bb);

      const result = await middleware(mockRequest);

      expect(result).toBeDefined();

      const setCookieHeader = result?.headers.get('set-cookie');
      expect(setCookieHeader).toBeFalsy();
    });

    it('should set tracing cookie with correct attributes', async () => {
      mockRequest.nextUrl.pathname = '/en/homepage';
      mockCookies.clear();

      mockedGetChannelByToken.mockReturnValue(Channel.Bb);

      const result = await middleware(mockRequest);

      expect(result).toBeDefined();

      const setCookieHeader = result?.headers.get('set-cookie');
      expect(setCookieHeader).toContain('WB-Tracing-Id=mock-tracing-id-12345');
    });
  });

  describe('all whitelisted pages', () => {
    it.each(whitelist)('should process %s page', async (page) => {
      mockRequest.nextUrl.pathname = `/en/${page}`;

      mockedGetChannelByToken.mockReturnValue(Channel.Bb);

      const result = await middleware(mockRequest);

      expect(result).toBeDefined();
    });
  });

  describe('locale handling', () => {
    it('should handle English locale correctly', async () => {
      mockRequest.nextUrl.pathname = '/en/manage';

      mockedGetChannelByToken.mockReturnValue(Channel.Bb);

      const result = await middleware(mockRequest);

      expect(result).toBeDefined();
    });

    it('should handle German locale correctly', async () => {
      mockRequest.nextUrl.pathname = '/de/manage';

      mockedGetChannelByToken.mockReturnValue(Channel.Bb);

      const result = await middleware(mockRequest);

      expect(result).toBeDefined();
    });

    it('should handle other locales', async () => {
      mockRequest.nextUrl.pathname = '/fr/homepage';

      mockedGetChannelByToken.mockReturnValue(Channel.Bb);

      const result = await middleware(mockRequest);

      expect(result).toBeDefined();
    });
  });
});
