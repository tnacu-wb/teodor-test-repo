import { Auth0Client } from '@auth0/nextjs-auth0/server';
import { CCUI_ROLES } from '@whitbread-eos/api';

export const auth0 = new Auth0Client({
  domain: process.env.AUTH0_DOMAIN!,
  clientId: process.env.AUTH0_CLIENT_ID!,
  clientSecret: process.env.AUTH0_CLIENT_SECRET!,
  secret: process.env.AUTH0_SECRET!,
  appBaseUrl: process.env.AUTH0_BASE_URL!,
  session: {
    rolling: process.env.AUTH0_SESSION_ROLLING === 'true',
  },
  async beforeSessionSaved(session) {
    // Preserve CCUI_ROLES custom claim in session from ID token
    const idToken = session.idToken as any;
    if (idToken && idToken[CCUI_ROLES]) {
      session.user[CCUI_ROLES] = idToken[CCUI_ROLES];
    }
    // Reject sessions without CCUI_ROLES (prevents session creation)
    if (!session.user[CCUI_ROLES] || session.user[CCUI_ROLES].length === 0) {
      throw new Error('User lacks required CCUI_ROLES');
    }
    return session;
  },
});
