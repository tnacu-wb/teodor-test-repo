import { Auth0Client } from '@auth0/nextjs-auth0/server';
import type { NextApiRequest, NextApiResponse } from 'next';

interface TokenResponse {
  idToken?: string | null;
  error?: string;
  accessToken?: string | null;
}

const auth0Client = new Auth0Client();

export default async function handler(
  req: NextApiRequest,
  res: NextApiResponse<TokenResponse>
): Promise<void> {
  if (req.method !== 'GET') {
    res.setHeader('Cache-Control', 'no-store');
    res.status(405).json({ error: 'Method not allowed' });
    return;
  }
  try {
    res.setHeader('Cache-Control', 'no-store');
    const session = await auth0Client.getSession(req);
    if (!session) {
      res.status(401).json({ accessToken: null, idToken: null });
      return;
    }

    const idToken = session.tokenSet?.idToken ?? null;
    const accessToken = session.tokenSet?.accessToken ?? null;
    res.status(200).json({ idToken, accessToken });
  } catch {
    res.status(500).json({ error: 'Failed to retrieve tokens' });
  }
}
