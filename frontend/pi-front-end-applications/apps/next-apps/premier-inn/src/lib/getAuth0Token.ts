import type { IncomingMessage } from 'http';

import { auth0 } from './auth0';

/**
 * Retrieves the Auth0 access token from the server-side session.
 * Returns null if no session exists or on error.
 * For use in getServerSideProps ONLY.
 */
export async function getAuth0AccessToken(req: IncomingMessage): Promise<string | null> {
  try {
    const session = await auth0.getSession(req);
    return session?.tokenSet?.accessToken ?? null;
  } catch {
    return null;
  }
}

/**
 * Retrieves the Auth0 user email from the server-side session.
 * Returns null if no session exists or on error.
 * For use in getServerSideProps ONLY.
 */
export async function getAuth0UserEmail(req: IncomingMessage): Promise<string | null> {
  try {
    const session = await auth0.getSession(req);
    return session?.user?.email ?? null;
  } catch {
    return null;
  }
}

/**
 * Retrieves both the Auth0 access token and user email in a single session call.
 * More efficient than calling getAuth0AccessToken and getAuth0UserEmail separately.
 * Returns nulls if no session exists or on error.
 * For use in getServerSideProps ONLY.
 */
export async function getAuth0TokenAndEmail(
  req: IncomingMessage
): Promise<{ accessToken: string | null; email: string | null }> {
  try {
    const session = await auth0.getSession(req);
    return {
      accessToken: session?.tokenSet?.accessToken ?? null,
      email: session?.user?.email ?? null,
    };
  } catch {
    return { accessToken: null, email: null };
  }
}
