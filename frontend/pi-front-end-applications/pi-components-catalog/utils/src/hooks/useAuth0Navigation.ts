import { useCallback } from 'react';

import { getDefaultLoginParams, resolveAuthEntryPoint } from '../auth/loginDefaults';
import useCustomLocale from './use-custom-locale';

export { resolveAuthEntryPoint };

interface Auth0UrlOptions {
  returnTo?: string;
  stateValue?: string;
  maxAge?: string;
  prompt?: 'login' | 'none';
  screenHint?: 'signup' | 'login';
  userEmail?: string; // Pass user email if needed for login_hint
  email?: string;
  firstName?: string;
  lastName?: string;
  basketReference?: string;
}

/**
 * Custom hook for Auth0 navigation
 * Provides functions to build Auth0 authentication URLs
 * Note: User state should be managed by the consuming component
 */
export function useAuth0Navigation() {
  const { language } = useCustomLocale();

  /**
   * Encodes state value for Auth0
   */
  const encodeState = useCallback((value: string): string => {
    if (!value || typeof window === 'undefined') {
      return value;
    }

    try {
      const binary = new TextEncoder()
        .encode(value)
        .reduce((acc, byte) => acc + String.fromCharCode(byte), '');
      return window.btoa(binary);
    } catch (error) {
      console.error('Failed to encode state', error);
      return value;
    }
  }, []);

  /**
   * Builds Auth0 login URL with query parameters
   */
  const buildLoginUrl = useCallback(
    (options: Auth0UrlOptions = {}): string => {
      if (typeof window === 'undefined') return '';

      const currentUrl = options.returnTo || window.location.href;
      const loginUrl = new URL('/auth/login', window.location.origin);

      const defaultParams = getDefaultLoginParams({
        currentUrl,
        origin: window.location.origin,
        language,
        connection: process.env.NEXT_PUBLIC_AUTH0_CONNECTION,
      });
      Object.entries(defaultParams).forEach(([key, value]) => {
        loginUrl.searchParams.set(key, value);
      });

      if (options.maxAge) {
        loginUrl.searchParams.set('max_age', options.maxAge);
      }

      if (options.prompt) {
        loginUrl.searchParams.set('prompt', options.prompt);
      }

      if (options.screenHint) {
        loginUrl.searchParams.set('screen_hint', options.screenHint);
      }

      if (options.userEmail) {
        loginUrl.searchParams.set('login_hint', options.userEmail);
      }

      if (options.stateValue) {
        loginUrl.searchParams.set('state', encodeState(options.stateValue));
      }

      if (options.email) {
        loginUrl.searchParams.set('ext-email', options.email);
      }

      if (options.firstName) {
        loginUrl.searchParams.set('ext-firstName', options.firstName);
      }

      if (options.lastName) {
        loginUrl.searchParams.set('ext-lastName', options.lastName);
      }

      if (options.basketReference) {
        loginUrl.searchParams.set('ext-basketReference', options.basketReference);
      }

      return loginUrl.toString();
    },
    [language, encodeState]
  );

  /**
   * Builds Auth0 signup URL with query parameters
   */
  const buildSignupUrl = useCallback(
    (options: Auth0UrlOptions = {}): string => {
      return buildLoginUrl({
        ...options,
        screenHint: 'signup',
      });
    },
    [buildLoginUrl]
  );

  /**
   * Builds Auth0 logout URL with query parameters
   */
  const buildLogoutUrl = useCallback((returnTo?: string): string => {
    if (typeof window === 'undefined') return '';

    const logoutUrl = new URL('/auth/logout', window.location.origin);
    const destination = returnTo || window.location.origin;
    logoutUrl.searchParams.set('returnTo', destination);

    return logoutUrl.toString();
  }, []);

  /**
   * Navigate to Auth0 login page
   */
  const navigateToLogin = useCallback(
    (options?: Auth0UrlOptions) => {
      const url = buildLoginUrl(options);
      window.location.href = url;
    },
    [buildLoginUrl]
  );

  /**
   * Navigate to Auth0 signup page
   */
  const navigateToSignup = useCallback(
    (options?: Auth0UrlOptions) => {
      const url = buildSignupUrl(options);
      window.location.href = url;
    },
    [buildSignupUrl]
  );

  /**
   * Navigate to Auth0 logout page
   */
  const navigateToLogout = useCallback(
    (returnTo?: string) => {
      const url = buildLogoutUrl(returnTo);
      window.location.href = url;
    },
    [buildLogoutUrl]
  );

  return {
    // URL builders
    buildLoginUrl,
    buildSignupUrl,
    buildLogoutUrl,
    // Navigation functions
    navigateToLogin,
    navigateToSignup,
    navigateToLogout,
    // Locale
    language,
  };
}
