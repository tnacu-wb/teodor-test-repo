import { NextApiRequest, NextApiResponse } from 'next';

// Global reference to access the mock in tests
let mockGetSession: jest.Mock;

// Mock auth0 from lib/auth0
jest.mock('../../../lib/auth0', () => {
  const getSession = jest.fn();
  mockGetSession = getSession;
  return {
    auth0: {
      getSession,
    },
  };
});

// Import handler after mock is set up
const handlerPromise = import('./access-token');

describe('/api/auth/access-token', () => {
  let handler: any;
  let req: Partial<NextApiRequest>;
  let res: Partial<NextApiResponse>;

  beforeAll(async () => {
    const handlerModule = await handlerPromise;
    handler = handlerModule.default;
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

  it('should return 200 with accessToken when session exists', async () => {
    const mockAccessToken = 'samplevalue';
    const mockSession = {
      tokenSet: {
        accessToken: mockAccessToken,
      },
    };

    mockGetSession.mockResolvedValue(mockSession);

    await handler(req as NextApiRequest, res as NextApiResponse);

    expect(mockGetSession).toHaveBeenCalledWith(req);
    expect(res.status).toHaveBeenCalledWith(200);
    expect(res.json).toHaveBeenCalledWith({ accessToken: mockAccessToken });
  });

  it('should return 401 when session does not exist', async () => {
    mockGetSession.mockResolvedValue(null);

    await handler(req as NextApiRequest, res as NextApiResponse);

    expect(mockGetSession).toHaveBeenCalledWith(req);
    expect(res.status).toHaveBeenCalledWith(401);
    expect(res.json).toHaveBeenCalledWith({ accessToken: null });
  });

  it('should return 200 with null accessToken when tokenSet is missing', async () => {
    const mockSession = {};

    mockGetSession.mockResolvedValue(mockSession);

    await handler(req as NextApiRequest, res as NextApiResponse);

    expect(res.status).toHaveBeenCalledWith(200);
    expect(res.json).toHaveBeenCalledWith({ accessToken: null });
  });

  it('should return 200 with null accessToken when tokenSet.accessToken is undefined', async () => {
    const mockSession = {
      tokenSet: {},
    };

    mockGetSession.mockResolvedValue(mockSession);

    await handler(req as NextApiRequest, res as NextApiResponse);

    expect(res.status).toHaveBeenCalledWith(200);
    expect(res.json).toHaveBeenCalledWith({ accessToken: null });
  });

  it('should return 500 on error', async () => {
    mockGetSession.mockRejectedValue(new Error('Database error'));

    await handler(req as NextApiRequest, res as NextApiResponse);

    expect(res.status).toHaveBeenCalledWith(500);
    expect(res.json).toHaveBeenCalledWith({ error: 'Failed to retrieve access token' });
  });

  it('should handle network errors gracefully', async () => {
    mockGetSession.mockRejectedValue(new Error('Network timeout'));

    await handler(req as NextApiRequest, res as NextApiResponse);

    expect(res.status).toHaveBeenCalledWith(500);
    expect(res.json).toHaveBeenCalledWith({ error: 'Failed to retrieve access token' });
  });
});
