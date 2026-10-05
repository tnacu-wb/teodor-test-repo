import { Channel } from '@whitbread-eos/api';
import type { NextApiRequest, NextApiResponse } from 'next';

export default async function handler(req: NextApiRequest, res: NextApiResponse): Promise<void> {
  if (req.method !== 'POST') {
    return res.status(405).json({ error: 'Method not allowed' });
  }

  const { basketId, country, language } = req.body;

  if (!basketId) {
    return res.status(400).json({ error: 'basketId is required' });
  }

  const orchestratorBaseUrl = process.env.NEXT_PUBLIC_PAYMENT_ORCHESTRATION_API;

  if (!orchestratorBaseUrl) {
    return res.status(500).json({ error: 'Payment orchestrator URL not configured' });
  }

  // Construct returnUrl from referer, stripping any stale 3DS-related params before
  // appending source=datatrans. This prevents params accumulating across retries —
  // e.g. a retry after a failed 3DS would otherwise produce a returnUrl that already
  // contains 3ds_failed=true, causing both params to appear together on the next return.
  const referer = req.headers.referer || '';
  const refererUrl = new URL(referer);
  refererUrl.searchParams.delete('source');
  refererUrl.searchParams.delete('3ds_failed');
  refererUrl.searchParams.set('source', 'datatrans');
  const returnUrl = refererUrl.toString();

  try {
    const response = await fetch(`${orchestratorBaseUrl}/api/payments/init`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        paymentMethod: 'NEW_CARD_WEB',
        basketId,
        returnUrl,
        country,
        language,
        userType: 'LEISURE',
        clientChannel: Channel.Pi,
      }),
    });

    if (!response.ok) {
      throw new Error(`Backend API error: ${response.status}`);
    }

    const data = await response.json();
    res.status(201).json(data);
  } catch (error) {
    console.error('Secure Fields API error:', error);
    res.status(502).json({ error: 'Failed to initialize payment session' });
  }
}
