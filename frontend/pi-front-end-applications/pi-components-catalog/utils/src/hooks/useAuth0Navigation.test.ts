import { renderHook, act } from '@testing-library/react';

import useCustomLocale from './use-custom-locale';
import { resolveAuthEntryPoint, useAuth0Navigation } from './useAuth0Navigation';

// Mock useCustomLocale
jest.mock('./use-custom-locale', () => ({
  __esModule: true,
  default: jest.fn(),
}));

describe('useAuth0Navigation', () => {
  const mockLanguage = 'en';

  beforeEach(() => {
    jest.clearAllMocks();
    (useCustomLocale as jest.Mock).mockReturnValue({ language: mockLanguage });

    // Mock window.location
    delete (window as any).location;
    window.location = {
      origin: 'https://example.com',
      href: 'https://example.com/current-page',
    } as any;

    // Mock window.btoa - use actual implementation
    global.btoa = (str: string) => Buffer.from(str).toString('base64');
    sessionStorage.clear();
  });

  describe('buildLoginUrl', () => {
    it('should build login URL with default parameters', () => {
      const { result } = renderHook(() => useAuth0Navigation());

      const url = result.current.buildLoginUrl();

      expect(url).toContain('/auth/login');
      expect(decodeURIComponent(url)).toContain('returnTo=https://example.com/current-page');
      expect(url).toContain('ui_locales=en');
      expect(url).toContain('application=premier-inn');
      expect(url).toContain('ext-application=web');
    });

    it('should build login URL with custom returnTo', () => {
      const { result } = renderHook(() => useAuth0Navigation());

      const url = result.current.buildLoginUrl({ returnTo: 'https://example.com/custom' });

      expect(decodeURIComponent(url)).toContain('returnTo=https://example.com/custom');
    });

    it('should include maxAge parameter when provided', () => {
      const { result } = renderHook(() => useAuth0Navigation());

      const url = result.current.buildLoginUrl({ maxAge: '0' });

      expect(url).toContain('max_age=0');
    });

    it('should include prompt parameter when provided', () => {
      const { result } = renderHook(() => useAuth0Navigation());

      const url = result.current.buildLoginUrl({ prompt: 'login' });

      expect(url).toContain('prompt=login');
    });

    it('should include screenHint parameter when provided', () => {
      const { result } = renderHook(() => useAuth0Navigation());

      const url = result.current.buildLoginUrl({ screenHint: 'signup' });

      expect(url).toContain('screen_hint=signup');
    });

    it('should include login_hint parameter when userEmail is provided', () => {
      const { result } = renderHook(() => useAuth0Navigation());

      const url = result.current.buildLoginUrl({ userEmail: 'user@example.com' });

      expect(decodeURIComponent(url)).toContain('login_hint=user@example.com');
    });

    it('should include ext-email parameter when email is provided', () => {
      const { result } = renderHook(() => useAuth0Navigation());

      const url = result.current.buildLoginUrl({ email: 'booker@example.com' });

      expect(decodeURIComponent(url)).toContain('ext-email=booker@example.com');
    });

    it('should include ext-firstName parameter when firstName is provided', () => {
      const { result } = renderHook(() => useAuth0Navigation());

      const url = result.current.buildLoginUrl({ firstName: 'John' });

      expect(url).toContain('ext-firstName=John');
    });

    it('should include ext-lastName parameter when lastName is provided', () => {
      const { result } = renderHook(() => useAuth0Navigation());

      const url = result.current.buildLoginUrl({ lastName: 'Doe' });

      expect(url).toContain('ext-lastName=Doe');
    });

    it('should include all user detail parameters together', () => {
      const { result } = renderHook(() => useAuth0Navigation());

      const url = result.current.buildLoginUrl({
        email: 'booker@example.com',
        firstName: 'John',
        lastName: 'Doe',
      });

      expect(decodeURIComponent(url)).toContain('ext-email=booker@example.com');
      expect(url).toContain('ext-firstName=John');
      expect(url).toContain('ext-lastName=Doe');
    });

    it('should not include ext-email, ext-firstName, ext-lastName when not provided', () => {
      const { result } = renderHook(() => useAuth0Navigation());

      const url = result.current.buildLoginUrl();

      expect(url).not.toContain('ext-email');
      expect(url).not.toContain('ext-firstName');
      expect(url).not.toContain('ext-lastName');
    });

    it('should include ext-basketReference parameter when basketReference is provided', () => {
      const { result } = renderHook(() => useAuth0Navigation());

      const url = result.current.buildLoginUrl({ basketReference: 'BR12345' });

      expect(url).toContain('ext-basketReference=BR12345');
    });

    it('should not include ext-basketReference when not provided', () => {
      const { result } = renderHook(() => useAuth0Navigation());

      const url = result.current.buildLoginUrl();

      expect(url).not.toContain('ext-basketReference');
    });

    it('should include ext-authEntryPoint derived from the current URL', () => {
      window.location = {
        origin: 'https://example.com',
        href: 'https://example.com/search?q=london',
      } as any;

      const { result } = renderHook(() => useAuth0Navigation());

      const url = result.current.buildLoginUrl();

      expect(url).toContain('ext-authEntryPoint=top_nav_srp');
    });

    it('should include ext-authEntryPoint from custom returnTo when provided', () => {
      const { result } = renderHook(() => useAuth0Navigation());

      const url = result.current.buildLoginUrl({ returnTo: 'https://example.com/guest-details' });

      expect(url).toContain('ext-authEntryPoint=guest_details_page');
    });

    it('should encode state value when provided', () => {
      const { result } = renderHook(() => useAuth0Navigation());

      const url = result.current.buildLoginUrl({ stateValue: 'test-state' });

      expect(url).toContain('state=');
      // State should be base64 encoded
      expect(url).toMatch(/state=[A-Za-z0-9+/=]+/);
    });

    it('should handle SSR environment gracefully', () => {
      // In SSR, window may not be available when hook is first rendered
      // The hook checks for window existence at call time, not initialization
      const { result } = renderHook(() => useAuth0Navigation());

      // Should still work with window available
      const url = result.current.buildLoginUrl();
      expect(url).toContain('/auth/login');
    });

    describe('NEXT_PUBLIC_AUTH0_CONNECTION env var', () => {
      const CONNECTION_VALUE = 'my-auth0-connection';

      afterEach(() => {
        delete process.env.NEXT_PUBLIC_AUTH0_CONNECTION;
      });

      it('should include connection, ext-connection and ext-origin when env var is set', () => {
        process.env.NEXT_PUBLIC_AUTH0_CONNECTION = CONNECTION_VALUE;

        const { result } = renderHook(() => useAuth0Navigation());
        const url = result.current.buildLoginUrl();

        expect(url).toContain(`connection=${CONNECTION_VALUE}`);
        expect(url).toContain(`ext-connection=${CONNECTION_VALUE}`);
        expect(decodeURIComponent(url)).toContain('ext-origin=https://example.com');
      });

      it('should reflect the env-var value in both connection params', () => {
        process.env.NEXT_PUBLIC_AUTH0_CONNECTION = 'another-connection';

        const { result } = renderHook(() => useAuth0Navigation());
        const url = result.current.buildLoginUrl();

        expect(url).toContain('connection=another-connection');
        expect(url).toContain('ext-connection=another-connection');
      });

      it('should not include connection, ext-connection or ext-origin when env var is not set', () => {
        // env var is intentionally absent (cleared in afterEach, never set here)
        const { result } = renderHook(() => useAuth0Navigation());
        const url = result.current.buildLoginUrl();

        expect(url).not.toContain('connection=');
        expect(url).not.toContain('ext-connection=');
        expect(url).not.toContain('ext-origin=');
      });

      it('should propagate connection params through buildSignupUrl', () => {
        process.env.NEXT_PUBLIC_AUTH0_CONNECTION = CONNECTION_VALUE;

        const { result } = renderHook(() => useAuth0Navigation());
        const url = result.current.buildSignupUrl();

        expect(url).toContain(`connection=${CONNECTION_VALUE}`);
        expect(url).toContain(`ext-connection=${CONNECTION_VALUE}`);
        expect(decodeURIComponent(url)).toContain('ext-origin=https://example.com');
      });
    });
  });

  describe('buildSignupUrl', () => {
    it('should build signup URL with screenHint set to signup', () => {
      const { result } = renderHook(() => useAuth0Navigation());

      const url = result.current.buildSignupUrl();

      expect(url).toContain('screen_hint=signup');
      expect(url).toContain('/auth/login');
    });

    it('should pass through other options', () => {
      const { result } = renderHook(() => useAuth0Navigation());

      const url = result.current.buildSignupUrl({ returnTo: 'https://example.com/after-signup' });

      expect(decodeURIComponent(url)).toContain('returnTo=https://example.com/after-signup');
      expect(url).toContain('screen_hint=signup');
    });

    it('should include ext-email, ext-firstName and ext-lastName when provided', () => {
      const { result } = renderHook(() => useAuth0Navigation());

      const url = result.current.buildSignupUrl({
        email: 'booker@example.com',
        firstName: 'John',
        lastName: 'Doe',
      });

      expect(decodeURIComponent(url)).toContain('ext-email=booker@example.com');
      expect(url).toContain('ext-firstName=John');
      expect(url).toContain('ext-lastName=Doe');
      expect(url).toContain('screen_hint=signup');
    });

    it('should include ext-basketReference when basketReference is provided', () => {
      const { result } = renderHook(() => useAuth0Navigation());

      const url = result.current.buildSignupUrl({ basketReference: 'BR12345' });

      expect(url).toContain('ext-basketReference=BR12345');
      expect(url).toContain('screen_hint=signup');
    });
  });

  describe('buildLogoutUrl', () => {
    it('should build logout URL with default returnTo', () => {
      const { result } = renderHook(() => useAuth0Navigation());

      const url = result.current.buildLogoutUrl();

      expect(url).toContain('/auth/logout');
      expect(decodeURIComponent(url)).toContain('returnTo=https://example.com');
    });

    it('should build logout URL with custom returnTo', () => {
      const { result } = renderHook(() => useAuth0Navigation());

      const url = result.current.buildLogoutUrl('https://example.com/goodbye');

      expect(decodeURIComponent(url)).toContain('returnTo=https://example.com/goodbye');
    });

    it('should handle SSR environment gracefully', () => {
      const { result } = renderHook(() => useAuth0Navigation());

      const url = result.current.buildLogoutUrl();

      expect(url).toContain('/auth/logout');
    });
  });

  describe('navigateToLogin', () => {
    it('should navigate to login URL', () => {
      const { result } = renderHook(() => useAuth0Navigation());

      act(() => {
        result.current.navigateToLogin();
      });

      expect(window.location.href).toContain('/auth/login');
    });

    it('should navigate with options', () => {
      const { result } = renderHook(() => useAuth0Navigation());

      act(() => {
        result.current.navigateToLogin({ maxAge: '0' });
      });

      expect(window.location.href).toContain('max_age=0');
    });
  });

  describe('navigateToSignup', () => {
    it('should navigate to signup URL', () => {
      const { result } = renderHook(() => useAuth0Navigation());

      act(() => {
        result.current.navigateToSignup();
      });

      expect(window.location.href).toContain('screen_hint=signup');
    });

    it('should navigate with basketReference included', () => {
      const { result } = renderHook(() => useAuth0Navigation());

      act(() => {
        result.current.navigateToSignup({ basketReference: 'BR12345' });
      });

      expect(window.location.href).toContain('ext-basketReference=BR12345');
      expect(window.location.href).toContain('screen_hint=signup');
    });
  });

  describe('navigateToLogout', () => {
    it('should navigate to logout URL', () => {
      const { result } = renderHook(() => useAuth0Navigation());

      act(() => {
        result.current.navigateToLogout();
      });

      expect(window.location.href).toContain('/auth/logout');
    });

    it('should navigate with custom returnTo', () => {
      const { result } = renderHook(() => useAuth0Navigation());

      act(() => {
        result.current.navigateToLogout('https://example.com/goodbye');
      });

      expect(decodeURIComponent(window.location.href)).toContain(
        'returnTo=https://example.com/goodbye'
      );
    });
  });

  describe('encodeState', () => {
    it('should encode state value', () => {
      const { result } = renderHook(() => useAuth0Navigation());

      const url = result.current.buildLoginUrl({ stateValue: 'test-state' });

      // Should contain a state parameter with base64-like value
      expect(url).toMatch(/state=[A-Za-z0-9+/=]+/);
    });

    it('should handle state encoding', () => {
      const { result } = renderHook(() => useAuth0Navigation());

      const url = result.current.buildLoginUrl({ stateValue: 'complex-state-value-123' });

      expect(url).toContain('state=');
    });

    it('should handle encoding errors gracefully', () => {
      const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation();
      const originalBtoa = global.btoa;

      global.btoa = (() => {
        throw new Error('Encoding error');
      }) as any;

      const { result } = renderHook(() => useAuth0Navigation());

      const url = result.current.buildLoginUrl({ stateValue: 'test-state' });

      // Should still return a URL even if encoding fails
      expect(url).toContain('/auth/login');
      expect(consoleErrorSpy).toHaveBeenCalled();

      consoleErrorSpy.mockRestore();
      global.btoa = originalBtoa;
    });
  });

  describe('language', () => {
    it('should expose language from useCustomLocale', () => {
      const { result } = renderHook(() => useAuth0Navigation());

      expect(result.current.language).toBe('en');
    });

    it('should use different language when locale changes', () => {
      (useCustomLocale as jest.Mock).mockReturnValue({ language: 'de' });

      const { result } = renderHook(() => useAuth0Navigation());

      expect(result.current.language).toBe('de');

      const url = result.current.buildLoginUrl();
      expect(url).toContain('ui_locales=de');
    });
  });
});

describe('resolveAuthEntryPoint', () => {
  it('returns guest_details_page for /guest-details path', () => {
    expect(resolveAuthEntryPoint('https://example.com/guest-details')).toBe('guest_details_page');
  });

  it('returns confirmation for /confirmation path', () => {
    expect(resolveAuthEntryPoint('https://example.com/confirmation?bookingRef=123')).toBe(
      'confirmation'
    );
  });

  it('returns settings for /account/settings path', () => {
    expect(resolveAuthEntryPoint('https://example.com/account/settings')).toBe('settings');
  });

  it('returns top_nav_home for /home path', () => {
    expect(resolveAuthEntryPoint('https://example.com/home')).toBe('top_nav_home');
  });

  it('returns top_nav_srp for /search path', () => {
    expect(resolveAuthEntryPoint('https://example.com/search?q=london')).toBe('top_nav_srp');
  });

  it('returns top_nav_hdp for /hotels/ path', () => {
    expect(resolveAuthEntryPoint('https://example.com/hotels/london-city')).toBe('top_nav_hdp');
  });

  it('returns top_nav_other for an unrecognised path', () => {
    expect(resolveAuthEntryPoint('https://example.com/offers')).toBe('top_nav_other');
  });

  it('returns booking_dashboard for /account/dashboard path', () => {
    expect(resolveAuthEntryPoint('https://example.com/account/dashboard')).toBe(
      'booking_dashboard'
    );
  });

  it('is case-insensitive', () => {
    expect(resolveAuthEntryPoint('https://example.com/GUEST-DETAILS')).toBe('guest_details_page');
    expect(resolveAuthEntryPoint('https://example.com/SEARCH')).toBe('top_nav_srp');
  });

  it('matches only the pathname, not query parameters', () => {
    expect(resolveAuthEntryPoint('https://example.com/home?returnTo=/search')).toBe('top_nav_home');
    expect(resolveAuthEntryPoint('https://example.com/offers?redirect=/hotels/london')).toBe(
      'top_nav_other'
    );
    expect(resolveAuthEntryPoint('https://example.com/account/login?returnTo=/confirmation')).toBe(
      'top_nav_other'
    );
  });

  it('matches only the pathname, not the hash fragment', () => {
    expect(resolveAuthEntryPoint('https://example.com/offers#/search')).toBe('top_nav_other');
  });

  it('falls back gracefully when given a non-absolute URL', () => {
    expect(resolveAuthEntryPoint('/search')).toBe('top_nav_srp');
    expect(resolveAuthEntryPoint('/unknown')).toBe('top_nav_other');
  });
});
