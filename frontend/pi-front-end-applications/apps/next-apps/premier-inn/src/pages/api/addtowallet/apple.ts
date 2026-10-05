import { decryptCryptString, formatReservationNumberInQuery } from '@whitbread-eos/utils';
import axios from 'axios';
import type { NextApiRequest, NextApiResponse } from 'next';

export default async function handler(req: NextApiRequest, res: NextApiResponse) {
  if (req.method === 'GET') {
    const { referrer } = req.query;

    if (!referrer || typeof referrer !== 'string') {
      return res.status(400).json({ message: 'Missing or invalid referrer parameter' });
    }
    const fixedReferrer = referrer.replaceAll(/ /g, '+');
    const decodedReferrerValue = decodeURIComponent(fixedReferrer);

    try {
      const decryptedParams = await decryptCryptString(
        decodedReferrerValue,
        process.env.NEXT_PUBLIC_ADD_TO_WALLET_ENCRYPTION
      );
      const cleanedParams = formatReservationNumberInQuery(decryptedParams);
      const url = `${process.env.NEXT_PUBLIC_REST_API}/v1/hotel-wallet?${cleanedParams}`;
      const response = await axios.get(url, {
        responseType: 'arraybuffer',
      });
      res.setHeader('Content-Type', 'application/vnd.apple.pkpass');
      res.setHeader('Content-Disposition', 'attachment; filename="hotel-wallet.pkpass"');
      res.setHeader('Cache-Control', 'no-store');
      res.status(200).end(response.data);
    } catch {
      res.status(500).json({ error: 'Failed to fetch pkpass' });
    }
    return;
  }

  res.setHeader('Allow', 'GET');
  res.status(405).end('Method Not Allowed');
}
