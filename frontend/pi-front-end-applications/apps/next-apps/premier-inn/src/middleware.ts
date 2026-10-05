import {
  getDefaultLoginParams,
  getLanguageFromPath,
} from '@whitbread-eos/utils/auth/login-defaults';
import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

import { auth0 } from './lib/auth0';

function getRefererUrl(referer: string | null): URL | null {
  if (!referer) return null;
  try {
    return new URL(referer);
  } catch {
    return null;
  }
}

// Auth0-initiated logins (e.g. via `initiate_login_uri`) hit /auth/login with no
// query params at all, so anything the app's own login button would normally set
// (connection, ui_locales, returnTo, ext-* tracking params) is missing. Backfill
// them from the referring page before handing off to auth0.middleware, using the
// same defaults useAuth0Navigation.buildLoginUrl uses when the app builds the link.
function withDefaultLoginParams(request: NextRequest): URL | null {
  const params = new URLSearchParams(request.nextUrl.searchParams);
  const refererUrl = getRefererUrl(request.headers.get('referer'));
  // Referer is client-controlled and can be spoofed - only trust it as a source
  // for returnTo/locale when it's same-origin, otherwise it's an open-redirect vector.
  const safeRefererUrl =
    refererUrl && refererUrl.origin === request.nextUrl.origin ? refererUrl : null;

  const defaults = getDefaultLoginParams({
    currentUrl: safeRefererUrl?.href ?? request.nextUrl.origin,
    origin: request.nextUrl.origin,
    language: getLanguageFromPath(safeRefererUrl?.pathname),
    connection: process.env.NEXT_PUBLIC_AUTH0_CONNECTION,
  });

  let changed = false;
  for (const [key, value] of Object.entries(defaults)) {
    if (!params.has(key)) {
      params.set(key, value);
      changed = true;
    }
  }

  if (!changed) return null;

  const redirectUrl = new URL(request.url);
  redirectUrl.search = params.toString();
  return redirectUrl;
}

// Isolated so the /auth/login-only logic (which conditionally skips the
// standard auth0.middleware call when a valid session already exists) lives
// behind its own call boundary, rather than inline in a pathname check.
async function handleLoginRoute(request: NextRequest) {
  try {
    const session = await auth0.getSession(request);
    if (session?.tokenSet?.accessToken) {
      const locale = request.nextUrl.locale || 'gb';
      const language = locale === 'gb' ? 'en' : 'de';
      return NextResponse.redirect(new URL(`/${locale}/${language}/home.html`, request.url));
    }
  } catch {
    // Fall through to auth0.middleware
  }

  const redirectUrl = withDefaultLoginParams(request);
  if (redirectUrl) {
    return NextResponse.redirect(redirectUrl);
  }

  return await auth0.middleware(request);
}

export async function middleware(request: NextRequest) {
  if (request.nextUrl.pathname !== '/auth/login') {
    return await auth0.middleware(request);
  }

  return await handleLoginRoute(request);
}

export const config = {
  matcher: [
    /*
     * Match all request paths except for the ones starting with:
     * - _next/static (static files)
     * - _next/image (image optimization files)
     * - favicon.ico, sitemap.xml, robots.txt (metadata files)
     */
    '/((?!_next/static|_next/image|favicon.ico|sitemap.xml|robots.txt).*)',
  ],
};
