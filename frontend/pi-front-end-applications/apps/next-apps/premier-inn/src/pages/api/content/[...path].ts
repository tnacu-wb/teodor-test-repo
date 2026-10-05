import type { NextApiRequest, NextApiResponse } from 'next';

const UPSTREAM_BASE_URL = 'https://www.premierinn.com';

/**
 * GET /api/content/**
 *
 * Proxies requests to https://www.premierinn.com/content/** preserving the
 * full path and query string. Only GET is allowed.
 */
export default async function handler(req: NextApiRequest, res: NextApiResponse) {
  if (req.method !== 'GET') {
    res.setHeader('Allow', 'GET');
    return res.status(405).json({ error: 'Method not allowed' });
  }

  const { path } = req.query;

  if (!path || !Array.isArray(path)) {
    return res.status(400).json({ error: 'Missing content path' });
  }

  // Next.js has already decoded route segments. Reject traversal, embedded
  // separators and residual encoding that an upstream could decode again.
  if (
    path.length === 0 ||
    path.some(
      (segment) =>
        !segment || segment === '.' || segment === '..' || /[\\/%\u0000-\u001f\u007f]/.test(segment)
    )
  ) {
    return res.status(400).json({ error: 'Invalid content path' });
  }

  const contentPath = path.map((segment) => encodeURIComponent(segment)).join('/');

  // Preserve original query params (excluding the catch-all `path` param)
  const queryParams = new URLSearchParams();
  for (const [key, value] of Object.entries(req.query)) {
    if (key === 'path') continue;
    if (Array.isArray(value)) {
      value.forEach((v) => queryParams.append(key, v));
    } else if (value) {
      queryParams.append(key, value);
    }
  }

  const upstreamUrl = new URL(`/content/${contentPath}`, UPSTREAM_BASE_URL);
  upstreamUrl.search = queryParams.toString();

  if (upstreamUrl.origin !== UPSTREAM_BASE_URL || !upstreamUrl.pathname.startsWith('/content/')) {
    return res.status(400).json({ error: 'Invalid content path' });
  }

  try {
    const response = await fetch(upstreamUrl, {
      method: 'GET',
      // A redirect must not move the server request to another destination.
      redirect: 'error',
      headers: {
        Accept: req.headers.accept || '*/*',
        'User-Agent': 'PremierInn-NextJS-Proxy',
      },
    });

    const contentType = response.headers.get('content-type') || 'application/octet-stream';
    const cacheControl = response.headers.get('cache-control');

    res.setHeader('Content-Type', contentType);
    if (cacheControl) {
      res.setHeader('Cache-Control', cacheControl);
    }
    res.status(response.status);

    // Use arrayBuffer to handle binary responses (images, PDFs, etc.)
    const buffer = Buffer.from(await response.arrayBuffer());
    return res.send(buffer);
  } catch {
    return res.status(502).json({
      error: 'Failed to fetch upstream content',
    });
  }
}
