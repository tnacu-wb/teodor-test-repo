jest.mock('@auth0/nextjs-auth0/server', () => ({
  Auth0Client: jest.fn().mockImplementation(() => ({
    middleware: jest.fn(),
    getSession: jest.fn(),
    getAccessToken: jest.fn(),
    updateSession: jest.fn(),
    withMiddlewareAuthRequired: jest.fn(),
    withPageAuthRequired: jest.fn(),
  })),
}));

jest.mock('next/server', () => ({
  NextResponse: { redirect: jest.fn() },
}));

describe('auth0 client', () => {
  it('should export auth0 client instance', async () => {
    const { auth0 } = await import('./auth0');
    expect(auth0).toBeDefined();
  });

  it('should have getSession method', async () => {
    const { auth0 } = await import('./auth0');
    expect(typeof auth0.getSession).toBe('function');
  });

  it('should have getAccessToken method', async () => {
    const { auth0 } = await import('./auth0');
    expect(typeof auth0.getAccessToken).toBe('function');
  });

  it('should have middleware method', async () => {
    const { auth0 } = await import('./auth0');
    expect(typeof auth0.middleware).toBe('function');
  });
});

describe('Auth0Client constructor configuration', () => {
  const originalEnv = process.env;

  beforeEach(() => {
    jest.resetModules();
    process.env = {
      ...originalEnv,
      AUTH0_BASE_URL: 'https://example.com',
      AUTH0_SECRET: 'test-secret',
      AUTH0_CLIENT_ID: 'test-client-id',
      AUTH0_CLIENT_SECRET: 'test-client-secret',
      AUTH0_MGMT_AUDIENCE: 'https://api.example.com',
    };
  });

  afterEach(() => {
    process.env = originalEnv;
    jest.dontMock('@auth0/nextjs-auth0/server');
  });

  it('should instantiate Auth0Client with all required config options', () => {
    const mockConstructor = jest.fn().mockReturnValue({ getSession: jest.fn() });
    jest.doMock('@auth0/nextjs-auth0/server', () => ({ Auth0Client: mockConstructor }));
    // eslint-disable-next-line @typescript-eslint/no-require-imports
    require('./auth0');

    expect(mockConstructor).toHaveBeenCalledWith({
      appBaseUrl: 'https://example.com',
      secret: 'test-secret',
      clientId: 'test-client-id',
      clientSecret: 'test-client-secret',
      authorizationParameters: {
        audience: 'https://api.example.com',
      },
      onCallback: expect.any(Function),
    });
  });

  it('should pass AUTH0_BASE_URL as appBaseUrl', () => {
    process.env.AUTH0_BASE_URL = 'https://custom-base.com';
    const mockConstructor = jest.fn().mockReturnValue({ getSession: jest.fn() });
    jest.doMock('@auth0/nextjs-auth0/server', () => ({ Auth0Client: mockConstructor }));
    // eslint-disable-next-line @typescript-eslint/no-require-imports
    require('./auth0');

    expect(mockConstructor).toHaveBeenCalledWith(
      expect.objectContaining({ appBaseUrl: 'https://custom-base.com' })
    );
  });

  it('should pass AUTH0_MGMT_AUDIENCE as authorizationParameters.audience', () => {
    process.env.AUTH0_MGMT_AUDIENCE = 'https://mgmt.api.example.com';
    const mockConstructor = jest.fn().mockReturnValue({ getSession: jest.fn() });
    jest.doMock('@auth0/nextjs-auth0/server', () => ({ Auth0Client: mockConstructor }));
    // eslint-disable-next-line @typescript-eslint/no-require-imports
    require('./auth0');

    expect(mockConstructor).toHaveBeenCalledWith(
      expect.objectContaining({
        authorizationParameters: { audience: 'https://mgmt.api.example.com' },
      })
    );
  });

  it('should pass undefined values when env vars are not set', () => {
    delete process.env.AUTH0_BASE_URL;
    delete process.env.AUTH0_SECRET;
    delete process.env.AUTH0_MGMT_AUDIENCE;
    const mockConstructor = jest.fn().mockReturnValue({ getSession: jest.fn() });
    jest.doMock('@auth0/nextjs-auth0/server', () => ({ Auth0Client: mockConstructor }));

    // eslint-disable-next-line @typescript-eslint/no-require-imports
    require('./auth0');

    expect(mockConstructor).toHaveBeenCalledWith(
      expect.objectContaining({
        appBaseUrl: undefined,
        secret: undefined,
        authorizationParameters: { audience: undefined },
      })
    );
  });
});

describe('onCallback', () => {
  const originalEnv = process.env;

  const getOnCallback = () => {
    const mockConstructor = jest.fn().mockReturnValue({ getSession: jest.fn() });
    jest.doMock('@auth0/nextjs-auth0/server', () => ({ Auth0Client: mockConstructor }));
    // eslint-disable-next-line @typescript-eslint/no-require-imports
    require('./auth0');
    return mockConstructor.mock.calls[0][0].onCallback as (
      error: unknown,
      ctx: { appBaseUrl: string; returnTo?: string },
      session: unknown
    ) => Promise<unknown>;
  };

  beforeEach(() => {
    jest.resetModules();
    jest.clearAllMocks();
    process.env = {
      ...originalEnv,
      AUTH0_BASE_URL: 'https://example.com',
      AUTH0_SECRET: 'test-secret',
      AUTH0_CLIENT_ID: 'test-client-id',
      AUTH0_CLIENT_SECRET: 'test-client-secret',
      AUTH0_MGMT_AUDIENCE: 'https://api.example.com',
    };
  });

  afterEach(() => {
    process.env = originalEnv;
    jest.dontMock('@auth0/nextjs-auth0/server');
  });

  it('redirects to a same-origin returnTo on successful sign-in with the success markers set', async () => {
    const onCallback = getOnCallback();
    const { NextResponse } = jest.requireMock('next/server');

    await onCallback(
      null,
      { appBaseUrl: 'https://example.com', returnTo: '/gb/en/guest-details.html' },
      { user: {} }
    );

    const redirectUrl = NextResponse.redirect.mock.calls[0][0] as URL;
    expect(redirectUrl.origin).toBe('https://example.com');
    expect(redirectUrl.pathname).toBe('/gb/en/guest-details.html');
    expect(redirectUrl.searchParams.get('authSignInSuccess')).toBe('1');
    expect(redirectUrl.searchParams.get('authRedirectPageType')).toBeTruthy();
  });

  it('falls back to the app base origin for a cross-origin returnTo instead of redirecting to it', async () => {
    const onCallback = getOnCallback();
    const { NextResponse } = jest.requireMock('next/server');

    await onCallback(
      null,
      { appBaseUrl: 'https://example.com', returnTo: 'https://evil-attacker.com/phishing' },
      { user: {} }
    );

    const redirectUrl = NextResponse.redirect.mock.calls[0][0] as URL;
    expect(redirectUrl.origin).toBe('https://example.com');
    expect(redirectUrl.href).not.toContain('evil-attacker.com');
  });

  it('does not set the success markers on error', async () => {
    const onCallback = getOnCallback();
    const { NextResponse } = jest.requireMock('next/server');

    await onCallback(new Error('callback failed'), { appBaseUrl: 'https://example.com' }, null);

    const redirectUrl = NextResponse.redirect.mock.calls[0][0] as URL;
    expect(redirectUrl.searchParams.has('authSignInSuccess')).toBe(false);
  });

  it('does not set the success markers when there is no session', async () => {
    const onCallback = getOnCallback();
    const { NextResponse } = jest.requireMock('next/server');

    await onCallback(null, { appBaseUrl: 'https://example.com' }, null);

    const redirectUrl = NextResponse.redirect.mock.calls[0][0] as URL;
    expect(redirectUrl.searchParams.has('authSignInSuccess')).toBe(false);
  });
});
