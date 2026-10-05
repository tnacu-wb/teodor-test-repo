import { NextRequest } from 'next/server';

import { auth0 } from './lib/auth0';
import { middleware, config } from './middleware';

jest.mock('next/server', () => ({
  NextResponse: {
    redirect: jest.fn((url: URL) => ({
      status: 307,
      headers: new Map([['location', url.toString()]]),
    })),
  },
  NextRequest: jest.fn(),
}));

jest.mock('./lib/auth0', () => ({
  auth0: {
    middleware: jest.fn(),
    getSession: jest.fn(),
  },
}));

const APP_INITIATED_PARAMS = {
  ui_locales: 'en',
  returnTo: 'https://example.com/gb/en/home.html',
  application: 'premier-inn',
  'ext-application': 'web',
  'ext-path': 'home',
  'ext-authEntryPoint': 'top_nav_home',
  connection: 'pi-uat',
  'ext-connection': 'pi-uat',
  'ext-origin': 'https://example.com',
};

function buildRequest({
  pathname = '/auth/login',
  locale = 'gb',
  searchParams = new URLSearchParams(),
  referer,
}: {
  pathname?: string;
  locale?: string;
  searchParams?: URLSearchParams;
  referer?: string;
} = {}): NextRequest {
  const url = `https://example.com${pathname}${searchParams.toString() ? `?${searchParams.toString()}` : ''}`;
  return {
    url,
    nextUrl: {
      pathname,
      locale,
      origin: 'https://example.com',
      searchParams,
    },
    headers: new Headers(referer ? { referer } : {}),
  } as unknown as NextRequest;
}

describe('middleware', () => {
  const originalConnection = process.env.NEXT_PUBLIC_AUTH0_CONNECTION;

  beforeEach(() => {
    jest.clearAllMocks();
  });

  afterEach(() => {
    if (originalConnection === undefined) {
      delete process.env.NEXT_PUBLIC_AUTH0_CONNECTION;
    } else {
      process.env.NEXT_PUBLIC_AUTH0_CONNECTION = originalConnection;
    }
  });

  it('should call auth0.middleware with the request', async () => {
    const mockRequest = buildRequest({ pathname: '/test' });

    const mockResponse = { status: 200 };
    (auth0.middleware as jest.Mock).mockResolvedValue(mockResponse);

    const result = await middleware(mockRequest);

    expect(auth0.middleware).toHaveBeenCalledWith(mockRequest);
    expect(result).toBe(mockResponse);
  });

  it('should handle errors from auth0.middleware', async () => {
    const mockRequest = buildRequest({ pathname: '/test' });

    const error = new Error('Auth0 error');
    (auth0.middleware as jest.Mock).mockRejectedValue(error);

    await expect(middleware(mockRequest)).rejects.toThrow('Auth0 error');
  });

  describe('/auth/login', () => {
    it('redirects to the home page when a session with an access token already exists', async () => {
      const mockRequest = buildRequest({ locale: 'gb' });

      (auth0.getSession as jest.Mock).mockResolvedValue({
        tokenSet: { accessToken: 'existing-token' },
      });

      const { NextResponse } = jest.requireMock('next/server');
      const result = await middleware(mockRequest);

      expect(auth0.getSession).toHaveBeenCalledWith(mockRequest);
      expect(NextResponse.redirect).toHaveBeenCalledWith(
        new URL('/gb/en/home.html', mockRequest.url)
      );
      expect(auth0.middleware).not.toHaveBeenCalled();
      expect(result).toEqual({ status: 307, headers: expect.any(Map) });
    });

    it('redirects to the German home page for the de locale', async () => {
      const mockRequest = buildRequest({ locale: 'de' });

      (auth0.getSession as jest.Mock).mockResolvedValue({
        tokenSet: { accessToken: 'existing-token' },
      });

      const { NextResponse } = jest.requireMock('next/server');
      await middleware(mockRequest);

      expect(NextResponse.redirect).toHaveBeenCalledWith(
        new URL('/de/de/home.html', mockRequest.url)
      );
    });

    it('falls through to auth0.middleware when getSession throws (app-initiated params already present)', async () => {
      const mockRequest = buildRequest({
        searchParams: new URLSearchParams(APP_INITIATED_PARAMS),
      });

      (auth0.getSession as jest.Mock).mockRejectedValue(new Error('Failed to decrypt session'));
      const mockResponse = { status: 200 };
      (auth0.middleware as jest.Mock).mockResolvedValue(mockResponse);

      const result = await middleware(mockRequest);

      expect(auth0.middleware).toHaveBeenCalledWith(mockRequest);
      expect(result).toBe(mockResponse);
    });

    it('falls through to auth0.middleware when there is no existing session (app-initiated params already present)', async () => {
      const mockRequest = buildRequest({
        searchParams: new URLSearchParams(APP_INITIATED_PARAMS),
      });

      (auth0.getSession as jest.Mock).mockResolvedValue(null);
      const mockResponse = { status: 200 };
      (auth0.middleware as jest.Mock).mockResolvedValue(mockResponse);

      const result = await middleware(mockRequest);

      expect(auth0.middleware).toHaveBeenCalledWith(mockRequest);
      expect(result).toBe(mockResponse);
    });

    it('falls through to auth0.middleware when the session has no access token (app-initiated params already present)', async () => {
      const mockRequest = buildRequest({
        searchParams: new URLSearchParams(APP_INITIATED_PARAMS),
      });

      (auth0.getSession as jest.Mock).mockResolvedValue({ tokenSet: {} });
      const mockResponse = { status: 200 };
      (auth0.middleware as jest.Mock).mockResolvedValue(mockResponse);

      const result = await middleware(mockRequest);

      expect(auth0.middleware).toHaveBeenCalledWith(mockRequest);
      expect(result).toBe(mockResponse);
    });

    describe('default login params for Auth0-initiated logins', () => {
      it('backfills ui_locales, returnTo, connection and tracking params from the referer', async () => {
        process.env.NEXT_PUBLIC_AUTH0_CONNECTION = 'pi-uat';

        const mockRequest = buildRequest({
          referer: 'https://example.com/gb/en/hotels/london-euston.html',
        });

        (auth0.getSession as jest.Mock).mockResolvedValue(null);

        const { NextResponse } = jest.requireMock('next/server');
        await middleware(mockRequest);

        expect(NextResponse.redirect).toHaveBeenCalledTimes(1);
        const redirectUrl = NextResponse.redirect.mock.calls[0][0] as URL;
        expect(Object.fromEntries(redirectUrl.searchParams)).toEqual({
          ui_locales: 'en',
          returnTo: 'https://example.com/gb/en/hotels/london-euston.html',
          application: 'premier-inn',
          'ext-application': 'web',
          'ext-path': 'home',
          'ext-authEntryPoint': 'top_nav_hdp',
          connection: 'pi-uat',
          'ext-connection': 'pi-uat',
          'ext-origin': 'https://example.com',
        });
        expect(auth0.middleware).not.toHaveBeenCalled();
      });

      it('ignores a cross-origin referer instead of using it as the returnTo/locale source', async () => {
        delete process.env.NEXT_PUBLIC_AUTH0_CONNECTION;

        const mockRequest = buildRequest({
          referer: 'https://evil-attacker.com/gb/de/phishing.html',
        });

        (auth0.getSession as jest.Mock).mockResolvedValue(null);

        const { NextResponse } = jest.requireMock('next/server');
        await middleware(mockRequest);

        expect(NextResponse.redirect).toHaveBeenCalledTimes(1);
        const redirectUrl = NextResponse.redirect.mock.calls[0][0] as URL;
        expect(Object.fromEntries(redirectUrl.searchParams)).toEqual({
          returnTo: 'https://example.com',
          application: 'premier-inn',
          'ext-application': 'web',
          'ext-path': 'home',
          'ext-authEntryPoint': 'top_nav_other',
        });
      });

      it('backfills only the referer-independent params when there is no referer or configured connection', async () => {
        delete process.env.NEXT_PUBLIC_AUTH0_CONNECTION;

        const mockRequest = buildRequest();

        (auth0.getSession as jest.Mock).mockResolvedValue(null);

        const { NextResponse } = jest.requireMock('next/server');
        await middleware(mockRequest);

        expect(NextResponse.redirect).toHaveBeenCalledTimes(1);
        const redirectUrl = NextResponse.redirect.mock.calls[0][0] as URL;
        expect(Object.fromEntries(redirectUrl.searchParams)).toEqual({
          returnTo: 'https://example.com',
          application: 'premier-inn',
          'ext-application': 'web',
          'ext-path': 'home',
          'ext-authEntryPoint': 'top_nav_other',
        });
      });

      it('preserves params already present and only fills in the gaps', async () => {
        delete process.env.NEXT_PUBLIC_AUTH0_CONNECTION;

        const mockRequest = buildRequest({
          searchParams: new URLSearchParams({ ui_locales: 'de' }),
          referer: 'https://example.com/gb/en/home.html',
        });

        (auth0.getSession as jest.Mock).mockResolvedValue(null);

        const { NextResponse } = jest.requireMock('next/server');
        await middleware(mockRequest);

        const redirectUrl = NextResponse.redirect.mock.calls[0][0] as URL;
        expect(redirectUrl.searchParams.get('ui_locales')).toBe('de');
        expect(redirectUrl.searchParams.get('ext-authEntryPoint')).toBe('top_nav_home');
      });

      it('falls through to auth0.middleware when every default param is already present', async () => {
        process.env.NEXT_PUBLIC_AUTH0_CONNECTION = 'pi-uat';

        const mockRequest = buildRequest({
          searchParams: new URLSearchParams(APP_INITIATED_PARAMS),
        });

        (auth0.getSession as jest.Mock).mockResolvedValue(null);
        const mockResponse = { status: 200 };
        (auth0.middleware as jest.Mock).mockResolvedValue(mockResponse);

        const result = await middleware(mockRequest);

        expect(auth0.middleware).toHaveBeenCalledWith(mockRequest);
        expect(result).toBe(mockResponse);
      });
    });
  });

  describe('config', () => {
    it('should have correct matcher configuration', () => {
      expect(config.matcher).toBeDefined();
      expect(Array.isArray(config.matcher)).toBe(true);
      expect(config.matcher).toHaveLength(1);
    });

    it('should exclude static files and metadata files', () => {
      const matcher = config.matcher[0];

      // Test that it excludes _next/static
      expect(matcher).toContain('_next/static');
      // Test that it excludes _next/image
      expect(matcher).toContain('_next/image');
      // Test that it excludes metadata files
      expect(matcher).toContain('favicon.ico');
      expect(matcher).toContain('sitemap.xml');
      expect(matcher).toContain('robots.txt');
    });
  });
});
