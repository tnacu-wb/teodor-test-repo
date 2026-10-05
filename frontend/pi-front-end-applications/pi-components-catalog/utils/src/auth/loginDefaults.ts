// Pure, dependency-free login param helpers shared by useAuth0Navigation (browser)
// and the premier-inn app's middleware (edge runtime). Keep this file free of any
// imports beyond built-ins so it stays safe to import from both contexts.

export const DEFAULT_LOGIN_APPLICATION = 'premier-inn';
export const DEFAULT_LOGIN_EXT_APPLICATION = 'web';
export const DEFAULT_LOGIN_EXT_PATH = 'home';

export function resolveAuthEntryPoint(url: string): string {
  let pathname: string;
  try {
    pathname = new URL(url).pathname.toLowerCase();
  } catch {
    pathname = url.toLowerCase();
  }
  if (pathname.includes('/guest-details')) return 'guest_details_page';
  if (pathname.includes('/confirmation')) return 'confirmation';
  if (pathname.includes('/account/settings')) return 'settings';
  if (pathname.includes('/account/dashboard')) return 'booking_dashboard';
  if (pathname.includes('/home')) return 'top_nav_home';
  if (pathname.includes('/search')) return 'top_nav_srp';
  if (pathname.includes('/hotels/')) return 'top_nav_hdp';
  return 'top_nav_other';
}

// Infers the PI language segment (en/de) from a `/{country}/{language}/...` path,
// e.g. for reading the language off a Referer URL when no other context is known.
export function getLanguageFromPath(pathname?: string | null): string | null {
  if (!pathname) return null;
  const match = pathname.match(/^\/(gb|de)\/(en|de)(?:\/|$)/);
  return match ? match[2] : null;
}

export interface LoginDefaultsInput {
  /** The page the user is/was on, used for `returnTo` and to derive `ext-authEntryPoint`. */
  currentUrl: string;
  /** This app's own origin, used for `ext-origin` when a connection is configured. */
  origin: string;
  language?: string | null;
  /** Value of NEXT_PUBLIC_AUTH0_CONNECTION, if configured. */
  connection?: string | null;
}

/**
 * Builds the standard set of Auth0 `/auth/login` query params this app always
 * wants set: returnTo, application/ext-* tracking params, and (when a connection
 * is configured) connection/ext-connection/ext-origin.
 */
export function getDefaultLoginParams({
  currentUrl,
  origin,
  language,
  connection,
}: LoginDefaultsInput): Record<string, string> {
  const params: Record<string, string> = {
    returnTo: currentUrl,
    application: DEFAULT_LOGIN_APPLICATION,
    'ext-application': DEFAULT_LOGIN_EXT_APPLICATION,
    'ext-path': DEFAULT_LOGIN_EXT_PATH,
    'ext-authEntryPoint': resolveAuthEntryPoint(currentUrl),
  };

  if (language) {
    params.ui_locales = language;
  }

  if (connection) {
    params.connection = connection;
    params['ext-connection'] = connection;
    params['ext-origin'] = origin;
  }

  return params;
}
