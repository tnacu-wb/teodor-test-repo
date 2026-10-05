import { PROD_ENVS } from '@whitbread-eos/utils';
import { randomUUID } from 'node:crypto';

export default function handler(req, res) {
  const NEXT_ENV = process.env.NEXT_ENV;
  const app = req.query?.app || '';
  let allowedOrigin = 'https://www.premierinn.com';
  const isProdEnv = PROD_ENVS.includes(NEXT_ENV);

  if (isProdEnv && app === 'business-booker') {
    allowedOrigin = 'https://business.premierinn.com';
  } else if (!isProdEnv) {
    allowedOrigin =
      app === 'business-booker'
        ? `https://business.${NEXT_ENV}.premierinn.digital`
        : `https://www.${NEXT_ENV}.premierinn.digital`;
  }

  res.setHeader('Access-Control-Allow-Origin', allowedOrigin);
  res.setHeader('Access-Control-Allow-Credentials', 'true');

  if (req.method !== 'GET') {
    res.status(405).json({ error: 'Method not allowed' });
    return;
  }
  let uuid;
  if (req.cookies?.fpid) {
    uuid = req.cookies.fpid;
  } else {
    uuid = randomUUID();
  }
  const expires = new Date(Date.now() + 365 * 24 * 60 * 60 * 1000).toUTCString();
  const cookieDomain = allowedOrigin.includes('digital')
    ? '.premierinn.digital'
    : '.premierinn.com';
  res.setHeader(
    'Set-Cookie',
    `fpid=${uuid}; Path=/; Domain=${cookieDomain}; Secure; SameSite=Lax; Expires=${expires}`
  );
  res.status(200).json({ fpid: uuid });
}
