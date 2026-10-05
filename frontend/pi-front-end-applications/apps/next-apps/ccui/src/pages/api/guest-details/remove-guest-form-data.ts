import { logger } from '@whitbread-eos/utils';
import { RedisKeyPrefix, RedisStorageServer } from '@whitbread-eos/utils/server';
import type { NextApiRequest, NextApiResponse } from 'next';

interface GuestFormDataResponse {
  success?: boolean;
  error?: string;
}

const BASKET_REFERENCE_REGEX =
  /^[A-Z]{3}-[a-z0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$/;

export default async function handler(
  req: NextApiRequest,
  res: NextApiResponse<GuestFormDataResponse>
): Promise<void> {
  res.setHeader('Cache-Control', 'no-store');

  if (req.method === 'PUT') {
    const { basketReferenceId } = req.body ?? {};

    if (!basketReferenceId) {
      res.status(400).json({ error: 'basketReferenceId is required' });
      return;
    }
    if (!BASKET_REFERENCE_REGEX.test(String(basketReferenceId))) {
      res.status(400).json({ error: 'Invalid basketReferenceId format' });
      return;
    }
    try {
      const redisKey = `${RedisKeyPrefix.GUEST_DETAILS_FORM_DATA_CCUI}::${basketReferenceId}`;
      const redisStorage = RedisStorageServer.getInstance();
      await redisStorage.removeItem(redisKey);
      res.status(200).json({ success: true });
    } catch (error) {
      logger.error({ error }, 'CCUI_REMOVE_GUEST_DETAILS_FORM_DATA_ERROR');
      res.status(500).json({ error: 'Failed to remove guest details form data' });
    }
    return;
  }

  res.status(405).json({ error: 'Method not allowed' });
}
