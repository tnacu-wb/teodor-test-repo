import type { NextApiRequest, NextApiResponse } from 'next';

import { auth0 } from '../../../lib/auth0';

interface UserResponse {
  user?: {
    email?: string;
    name?: string;
    nickname?: string;
    picture?: string;
  } | null;
  error?: string;
}

export default async function handler(
  req: NextApiRequest,
  res: NextApiResponse<UserResponse>
): Promise<void> {
  res.setHeader('Cache-Control', 'no-store');
  if (req.method !== 'GET') {
    res.status(405).json({ error: 'Method not allowed' });
    return;
  }
  try {
    const session = await auth0.getSession(req);
    if (!session) {
      res.status(401).json({ user: null });
      return;
    }

    const user = session.user
      ? {
          email: session.user.email,
          name: session.user.name,
          nickname: session.user.nickname,
          picture: session.user.picture,
        }
      : null;

    res.status(200).json({ user });
  } catch {
    res.status(500).json({ error: 'Failed to retrieve user' });
  }
}
