import { NextApiRequest, NextApiResponse } from 'next';

let mockGetSession: jest.Mock;

jest.mock('../../../lib/auth0', () => {
  const getSession = jest.fn();
  mockGetSession = getSession;
  return { auth0: { getSession } };
});

const handlerPromise = import('./user');

describe('/api/auth/user', () => {
  let handler: any;
  let req: Partial<NextApiRequest>;
  let res: Partial<NextApiResponse>;

  beforeAll(async () => {
    const mod = await handlerPromise;
    handler = mod.default;
  });

  beforeEach(() => {
    jest.clearAllMocks();
    req = { method: 'GET' };
    res = {
      status: jest.fn().mockReturnThis(),
      json: jest.fn().mockReturnThis(),
      setHeader: jest.fn().mockReturnThis(),
    };
  });

  it('should set Cache-Control: no-store on all responses', async () => {
    mockGetSession.mockResolvedValue(null);

    await handler(req as NextApiRequest, res as NextApiResponse);

    expect(res.setHeader).toHaveBeenCalledWith('Cache-Control', 'no-store');
  });

  it('should return 405 for non-GET methods', async () => {
    req = { method: 'POST' };

    await handler(req as NextApiRequest, res as NextApiResponse);

    expect(res.status).toHaveBeenCalledWith(405);
    expect(res.json).toHaveBeenCalledWith({ error: 'Method not allowed' });
    expect(mockGetSession).not.toHaveBeenCalled();
  });

  it('should return 405 for DELETE method', async () => {
    req = { method: 'DELETE' };

    await handler(req as NextApiRequest, res as NextApiResponse);

    expect(res.status).toHaveBeenCalledWith(405);
    expect(res.json).toHaveBeenCalledWith({ error: 'Method not allowed' });
  });

  it('should return 401 when session does not exist', async () => {
    mockGetSession.mockResolvedValue(null);

    await handler(req as NextApiRequest, res as NextApiResponse);

    expect(mockGetSession).toHaveBeenCalledWith(req);
    expect(res.status).toHaveBeenCalledWith(401);
    expect(res.json).toHaveBeenCalledWith({ user: null });
  });

  it('should return 200 with user profile when session exists', async () => {
    mockGetSession.mockResolvedValue({
      user: {
        email: 'test@example.com',
        name: 'Test User',
        nickname: 'testuser',
        picture: 'https://example.com/avatar.png',
      },
    });

    await handler(req as NextApiRequest, res as NextApiResponse);

    expect(mockGetSession).toHaveBeenCalledWith(req);
    expect(res.status).toHaveBeenCalledWith(200);
    expect(res.json).toHaveBeenCalledWith({
      user: {
        email: 'test@example.com',
        name: 'Test User',
        nickname: 'testuser',
        picture: 'https://example.com/avatar.png',
      },
    });
  });

  it('should return 200 with null user when session.user is falsy', async () => {
    mockGetSession.mockResolvedValue({ user: null });

    await handler(req as NextApiRequest, res as NextApiResponse);

    expect(res.status).toHaveBeenCalledWith(200);
    expect(res.json).toHaveBeenCalledWith({ user: null });
  });

  it('should return 200 with partial user when only email is present', async () => {
    mockGetSession.mockResolvedValue({
      user: { email: 'test@example.com' },
    });

    await handler(req as NextApiRequest, res as NextApiResponse);

    expect(res.status).toHaveBeenCalledWith(200);
    expect(res.json).toHaveBeenCalledWith({
      user: {
        email: 'test@example.com',
        name: undefined,
        nickname: undefined,
        picture: undefined,
      },
    });
  });

  it('should return 500 when getSession throws', async () => {
    mockGetSession.mockRejectedValue(new Error('Session error'));

    await handler(req as NextApiRequest, res as NextApiResponse);

    expect(res.status).toHaveBeenCalledWith(500);
    expect(res.json).toHaveBeenCalledWith({ error: 'Failed to retrieve user' });
  });

  it('should return 500 on network errors', async () => {
    mockGetSession.mockRejectedValue(new Error('Network timeout'));

    await handler(req as NextApiRequest, res as NextApiResponse);

    expect(res.status).toHaveBeenCalledWith(500);
    expect(res.json).toHaveBeenCalledWith({ error: 'Failed to retrieve user' });
  });
});
