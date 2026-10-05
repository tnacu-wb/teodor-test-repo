import { Auth0Client } from '@auth0/nextjs-auth0/server';
import { resolveAuthEntryPoint } from '@whitbread-eos/utils/auth/login-defaults';
import { NextResponse } from 'next/server';

// Explicitly configure Auth0Client with all required options
export const auth0 = new Auth0Client({
  appBaseUrl: process.env.AUTH0_BASE_URL,
  secret: process.env.AUTH0_SECRET,
  clientId: process.env.AUTH0_CLIENT_ID,
  clientSecret: process.env.AUTH0_CLIENT_SECRET,
  authorizationParameters: {
    audience: process.env.AUTH0_MGMT_AUDIENCE,
  },
  async onCallback(error, ctx, session) {
    const appBaseUrl = new URL(ctx.appBaseUrl || process.env.AUTH0_BASE_URL || 'http://localhost');
    const redirectUrl = new URL(ctx.returnTo || '/', appBaseUrl);
    if (redirectUrl.origin !== appBaseUrl.origin) {
      redirectUrl.href = appBaseUrl.origin;
    }

    if (!error && session) {
      redirectUrl.searchParams.set('authSignInSuccess', '1');
      redirectUrl.searchParams.set('authRedirectPageType', resolveAuthEntryPoint(redirectUrl.href));
    }

    return NextResponse.redirect(redirectUrl);
  },
});
