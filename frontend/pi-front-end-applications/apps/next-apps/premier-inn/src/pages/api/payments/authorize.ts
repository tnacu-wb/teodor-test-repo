import type { NextApiRequest, NextApiResponse } from 'next';

export default async function handler(req: NextApiRequest, res: NextApiResponse): Promise<void> {
  if (req.method !== 'POST') {
    return res.status(405).json({ error: 'Method not allowed' });
  }

  const { basketId } = req.body;

  if (!basketId) {
    return res.status(400).json({ error: 'basketId is required' });
  }

  const orchestratorBaseUrl = process?.env?.NEXT_PUBLIC_PAYMENT_ORCHESTRATION_API;

  if (!orchestratorBaseUrl) {
    return res.status(500).json({ error: 'Payment orchestrator URL not configured' });
  }

  try {
    const response = await fetch(`${orchestratorBaseUrl}/api/payments/authorize`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ basketId }),
    });

    if (!response.ok) {
      const status = response.status;
      if (status === 401) return res.status(401).json({ error: 'AUTHORIZATION_DECLINED' });
      if (status === 422) return res.status(422).json({ error: '3DS_AUTHENTICATION_FAILED' });
      if (status === 404) return res.status(404).json({ error: 'TRANSACTION_NOT_FOUND' });
      throw new Error(`Authorization failed: ${status}`);
    }

    res.status(204).end();
  } catch (error) {
    console.error('Payment authorization error:', error);
    res.status(502).json({ error: 'Payment authorization failed' });
  }
}
