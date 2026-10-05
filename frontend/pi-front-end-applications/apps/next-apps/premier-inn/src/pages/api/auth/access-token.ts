import type { NextApiRequest, NextApiResponse } from 'next';

import { auth0 } from '../../../lib/auth0';

interface AccessTokenResponse {
  accessToken?: string | null;
  error?: string;
}

export default async function handler(
  req: NextApiRequest,
  res: NextApiResponse<AccessTokenResponse>
): Promise<void> {
  res.setHeader('Cache-Control', 'no-store');
  if (req.method !== 'GET') {
    res.status(405).json({ error: 'Method not allowed' });
    return;
  }
  try {
    const session = await auth0.getSession(req);
    if (!session) {
      res.status(401).json({ accessToken: null });
      return;
    }
    const accessToken = session.tokenSet?.accessToken ?? null;

    res.status(200).json({ accessToken });
  } catch {
    res.status(500).json({ error: 'Failed to retrieve access token' });
  }
}
