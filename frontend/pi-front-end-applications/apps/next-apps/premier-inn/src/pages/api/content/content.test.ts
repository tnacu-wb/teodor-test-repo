/** @jest-environment node */
import type { NextApiRequest, NextApiResponse } from 'next';

import handler from './[...path]';

describe('content proxy destination restrictions', () => {
  const originalFetch = global.fetch;
  const fetchMock = jest.fn();
  let res: NextApiResponse;

  const request = (path: string[] | string | undefined, extra = {}) =>
    ({ method: 'GET', headers: {}, query: { path, ...extra } }) as NextApiRequest;

  beforeEach(() => {
    fetchMock.mockReset();
    global.fetch = fetchMock;
    res = {
      setHeader: jest.fn(),
      status: jest.fn().mockReturnThis(),
      json: jest.fn().mockReturnThis(),
      send: jest.fn().mockReturnThis(),
    } as unknown as NextApiResponse;
  });

  afterAll(() => {
    global.fetch = originalFetch;
  });

  it('preserves content paths, repeated query parameters, binary data and cache headers', async () => {
    const body = Buffer.from([0, 128, 255]);
    fetchMock.mockResolvedValue({
      status: 200,
      headers: new Headers({ 'content-type': 'image/png', 'cache-control': 'max-age=60' }),
      arrayBuffer: async () => body,
    });

    await handler(request(['dam', 'hotel image.png'], { size: ['small', 'large'] }), res);

    const [url, options] = fetchMock.mock.calls[0];
    expect(url.toString()).toBe(
      'https://www.premierinn.com/content/dam/hotel%20image.png?size=small&size=large'
    );
    expect(options.redirect).toBe('error');
    expect(res.send).toHaveBeenCalledWith(body);
    expect(res.setHeader).toHaveBeenCalledWith('Cache-Control', 'max-age=60');
  });

  it.each([
    undefined,
    'dam/image.png',
    [],
    [''],
    ['..', 'admin'],
    ['.'],
    ['../admin'],
    ['..\\admin'],
    ['%2e%2e', 'admin'],
    ['%252e%252e', 'admin'],
    ['https://attacker.example'],
    ['//169.254.169.254/latest/meta-data'],
    ['image\n.png'],
  ])('rejects invalid path %j before fetching', async (path) => {
    await handler(request(path), res);
    expect(res.status).toHaveBeenCalledWith(400);
    expect(fetchMock).not.toHaveBeenCalled();
  });

  it('encodes query and fragment delimiters inside filenames as path data', async () => {
    fetchMock.mockRejectedValue(new Error('upstream unavailable'));
    await handler(request(['image?redirect=https:#fragment']), res);
    const [url] = fetchMock.mock.calls[0];
    expect(url.origin).toBe('https://www.premierinn.com');
    expect(url.pathname).toBe('/content/image%3Fredirect%3Dhttps%3A%23fragment');
    expect(url.search).toBe('');
    expect(url.hash).toBe('');
  });

  it('returns a generic 502 when fetch rejects a redirect or network error', async () => {
    fetchMock.mockRejectedValue(new Error('sensitive upstream details'));
    await handler(request(['dam', 'redirect']), res);
    expect(fetchMock.mock.calls[0][1].redirect).toBe('error');
    expect(res.status).toHaveBeenCalledWith(502);
    expect(res.json).toHaveBeenCalledWith({ error: 'Failed to fetch upstream content' });
  });

  it('rejects non-GET requests without contacting upstream', async () => {
    await handler({ ...request(['dam']), method: 'POST' }, res);
    expect(res.status).toHaveBeenCalledWith(405);
    expect(res.setHeader).toHaveBeenCalledWith('Allow', 'GET');
    expect(fetchMock).not.toHaveBeenCalled();
  });
});
