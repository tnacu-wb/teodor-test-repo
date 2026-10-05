import '@testing-library/jest-dom';

import { Auth0Client } from './auth0-mock';

describe('Auth0Client Mock', () => {
  let consoleWarnSpy: jest.SpyInstance;

  beforeEach(() => {
    jest.clearAllMocks();
    consoleWarnSpy = jest.spyOn(console, 'warn').mockImplementation(() => {
      console.info('Mocked console.warn called');
    });
  });

  afterEach(() => {
    consoleWarnSpy.mockRestore();
  });

  describe('Constructor', () => {
    it('should create an instance of Auth0Client', () => {
      const client = new Auth0Client();

      expect(client).toBeInstanceOf(Auth0Client);
    });

    it('should log warning message on construction', () => {
      new Auth0Client();

      expect(consoleWarnSpy).toHaveBeenCalledWith(
        'Auth0Client is mocked. Please use auth0.getSession.mockReturnValue to set the session value for your tests.'
      );
    });

    it('should log warning exactly once per instance', () => {
      new Auth0Client();

      expect(consoleWarnSpy).toHaveBeenCalledTimes(1);
    });

    it('should log warning for multiple instances', () => {
      new Auth0Client();
      new Auth0Client();

      expect(consoleWarnSpy).toHaveBeenCalledTimes(2);
    });
  });

  describe('getSession method', () => {
    it('should have getSession method', () => {
      const client = new Auth0Client();

      expect(client.getSession).toBeDefined();
    });

    it('should be a jest mock function', () => {
      const client = new Auth0Client();

      expect(jest.isMockFunction(client.getSession)).toBe(true);
    });

    it('should be callable', () => {
      const client = new Auth0Client();

      expect(() => client.getSession()).not.toThrow();
    });

    it('should return undefined by default', () => {
      const client = new Auth0Client();

      const result = client.getSession();

      expect(result).toBeUndefined();
    });

    it('should allow mockReturnValue', () => {
      const client = new Auth0Client();
      const mockSession = { user: { id: '123' } };

      client.getSession.mockReturnValue(mockSession);

      expect(client.getSession()).toEqual(mockSession);
    });

    it('should allow mockResolvedValue for async', async () => {
      const client = new Auth0Client();
      const mockSession = { user: { id: '456' } };

      client.getSession.mockResolvedValue(mockSession);

      const result = await client.getSession();

      expect(result).toEqual(mockSession);
    });

    it('should track calls', () => {
      const client = new Auth0Client();
      const mockReq = { headers: {} };

      client.getSession(mockReq);

      expect(client.getSession).toHaveBeenCalledWith(mockReq);
      expect(client.getSession).toHaveBeenCalledTimes(1);
    });

    it('should allow mockImplementation', () => {
      const client = new Auth0Client();
      const customImplementation = jest.fn(() => ({ customSession: true }));

      client.getSession.mockImplementation(customImplementation);

      const result = client.getSession();

      expect(result).toEqual({ customSession: true });
      expect(customImplementation).toHaveBeenCalled();
    });

    it('should support multiple calls with different return values', () => {
      const client = new Auth0Client();

      client.getSession
        .mockReturnValueOnce({ session: 1 })
        .mockReturnValueOnce({ session: 2 })
        .mockReturnValueOnce({ session: 3 });

      expect(client.getSession()).toEqual({ session: 1 });
      expect(client.getSession()).toEqual({ session: 2 });
      expect(client.getSession()).toEqual({ session: 3 });
    });

    it('should allow mockRejectedValue for error scenarios', async () => {
      const client = new Auth0Client();
      const error = new Error('Session error');

      client.getSession.mockRejectedValue(error);

      await expect(client.getSession()).rejects.toThrow('Session error');
    });

    it('should be resetable', () => {
      const client = new Auth0Client();

      client.getSession();
      client.getSession();

      expect(client.getSession).toHaveBeenCalledTimes(2);

      client.getSession.mockReset();

      expect(client.getSession).toHaveBeenCalledTimes(0);
    });
  });

  describe('middleware method', () => {
    it('should have middleware method', () => {
      const client = new Auth0Client();

      expect(client.middleware).toBeDefined();
    });

    it('should be a jest mock function', () => {
      const client = new Auth0Client();

      expect(jest.isMockFunction(client.middleware)).toBe(true);
    });

    it('should be callable', () => {
      const client = new Auth0Client();

      expect(() => client.middleware()).not.toThrow();
    });

    it('should return undefined by default', () => {
      const client = new Auth0Client();

      const result = client.middleware();

      expect(result).toBeUndefined();
    });

    it('should allow mockReturnValue', () => {
      const client = new Auth0Client();
      const mockMiddleware = jest.fn();

      client.middleware.mockReturnValue(mockMiddleware);

      expect(client.middleware()).toEqual(mockMiddleware);
    });

    it('should track calls', () => {
      const client = new Auth0Client();
      const mockReq = {};
      const mockRes = {};

      client.middleware(mockReq, mockRes);

      expect(client.middleware).toHaveBeenCalledWith(mockReq, mockRes);
      expect(client.middleware).toHaveBeenCalledTimes(1);
    });

    it('should allow mockImplementation', () => {
      const client = new Auth0Client();
      const customMiddleware = jest.fn((req, res, next) => next());

      client.middleware.mockImplementation(customMiddleware);

      const mockNext = jest.fn();
      client.middleware({}, {}, mockNext);

      expect(customMiddleware).toHaveBeenCalled();
      expect(mockNext).toHaveBeenCalled();
    });

    it('should support async middleware', async () => {
      const client = new Auth0Client();
      const asyncMiddleware = jest.fn(async () => {
        return 'async result';
      });

      client.middleware.mockImplementation(asyncMiddleware);

      const result = await client.middleware();

      expect(result).toBe('async result');
      expect(asyncMiddleware).toHaveBeenCalled();
    });

    it('should be resetable', () => {
      const client = new Auth0Client();

      client.middleware();
      client.middleware();

      expect(client.middleware).toHaveBeenCalledTimes(2);

      client.middleware.mockReset();

      expect(client.middleware).toHaveBeenCalledTimes(0);
    });
  });

  describe('Multiple instances', () => {
    it('should create independent mock instances', () => {
      const client1 = new Auth0Client();
      const client2 = new Auth0Client();

      client1.getSession.mockReturnValue({ client: 1 });
      client2.getSession.mockReturnValue({ client: 2 });

      expect(client1.getSession()).toEqual({ client: 1 });
      expect(client2.getSession()).toEqual({ client: 2 });
    });

    it('should track calls independently', () => {
      const client1 = new Auth0Client();
      const client2 = new Auth0Client();

      client1.getSession();
      client1.getSession();
      client2.getSession();

      expect(client1.getSession).toHaveBeenCalledTimes(2);
      expect(client2.getSession).toHaveBeenCalledTimes(1);
    });

    it('should not share middleware implementations', () => {
      const client1 = new Auth0Client();
      const client2 = new Auth0Client();

      const middleware1 = jest.fn();
      const middleware2 = jest.fn();

      client1.middleware.mockImplementation(middleware1);
      client2.middleware.mockImplementation(middleware2);

      client1.middleware();
      client2.middleware();

      expect(middleware1).toHaveBeenCalledTimes(1);
      expect(middleware2).toHaveBeenCalledTimes(1);
    });
  });

  describe('Usage scenarios', () => {
    it('should support authenticated session scenario', () => {
      const client = new Auth0Client();
      const mockSession = {
        user: {
          id: 'user-123',
          name: 'John Doe',
          email: 'john@example.com',
        },
        tokenSet: {
          accessToken: 'access-token-123',
          idToken: 'id-token-123',
        },
      };

      client.getSession.mockReturnValue(mockSession);

      const session = client.getSession();

      expect(session).toEqual(mockSession);
      expect(session.user.id).toBe('user-123');
      expect(session.tokenSet.accessToken).toBe('access-token-123');
    });

    it('should support unauthenticated session scenario', () => {
      const client = new Auth0Client();

      client.getSession.mockReturnValue(null);

      const session = client.getSession();

      expect(session).toBeNull();
    });

    it('should support async session retrieval', async () => {
      const client = new Auth0Client();
      const mockSession = {
        user: { id: 'async-user' },
      };

      client.getSession.mockResolvedValue(mockSession);

      const session = await client.getSession({ headers: {} });

      expect(session).toEqual(mockSession);
    });

    it('should support session retrieval with request parameter', () => {
      const client = new Auth0Client();
      const mockReq = {
        headers: {
          cookie: 'auth-session=xyz',
        },
      };
      const mockSession = { user: { id: '123' } };

      client.getSession.mockImplementation((req) => {
        if (req.headers.cookie?.includes('auth-session')) {
          return mockSession;
        }
        return null;
      });

      const session = client.getSession(mockReq);

      expect(session).toEqual(mockSession);
    });

    it('should support middleware authentication flow', () => {
      const client = new Auth0Client();
      const mockReq = { url: '/protected' };
      const mockRes = {
        status: jest.fn().mockReturnThis(),
        json: jest.fn(),
      };
      const mockNext = jest.fn();

      client.middleware.mockImplementation((req, res, next) => {
        if (req.url === '/protected') {
          return res.status(401).json({ error: 'Unauthorized' });
        }
        next();
      });

      client.middleware(mockReq, mockRes, mockNext);

      expect(mockRes.status).toHaveBeenCalledWith(401);
      expect(mockNext).not.toHaveBeenCalled();
    });

    it('should support testing session errors', async () => {
      const client = new Auth0Client();
      const error = new Error('Failed to retrieve session');

      client.getSession.mockRejectedValue(error);

      await expect(client.getSession()).rejects.toThrow('Failed to retrieve session');
    });

    it('should verify session method was called with correct arguments', () => {
      const client = new Auth0Client();
      const mockReq = { headers: { authorization: 'Bearer token' } };

      client.getSession(mockReq);

      expect(client.getSession).toHaveBeenCalledWith(mockReq);
      expect(client.getSession.mock.calls[0][0]).toBe(mockReq);
    });
  });

  describe('Mock function properties', () => {
    it('should have mock property on getSession', () => {
      const client = new Auth0Client();

      expect(client.getSession.mock).toBeDefined();
      expect(client.getSession.mock.calls).toEqual([]);
    });

    it('should have mock property on middleware', () => {
      const client = new Auth0Client();

      expect(client.middleware.mock).toBeDefined();
      expect(client.middleware.mock.calls).toEqual([]);
    });

    it('should track call history in mock.calls', () => {
      const client = new Auth0Client();

      client.getSession('arg1');
      client.getSession('arg2');

      expect(client.getSession.mock.calls).toEqual([['arg1'], ['arg2']]);
    });

    it('should track return values in mock.results', () => {
      const client = new Auth0Client();

      client.getSession.mockReturnValue('result1');
      client.getSession();

      expect(client.getSession.mock.results[0].value).toBe('result1');
    });
  });

  describe('Type safety', () => {
    it('should allow TypeScript-style usage', () => {
      const client: Auth0Client = new Auth0Client();

      expect(client).toBeInstanceOf(Auth0Client);
      expect(client.getSession).toBeDefined();
      expect(client.middleware).toBeDefined();
    });

    it('should support type-safe session mocking', () => {
      const client = new Auth0Client();

      interface Session {
        user: {
          id: string;
          email: string;
        };
      }

      const mockSession: Session = {
        user: {
          id: '123',
          email: 'test@example.com',
        },
      };

      client.getSession.mockReturnValue(mockSession);

      const result = client.getSession();

      expect(result).toEqual(mockSession);
    });
  });

  describe('Documentation example verification', () => {
    it('should work with example from warning message', () => {
      const client = new Auth0Client();
      const sessionValue = { user: { id: 'example-user' } };

      client.getSession.mockReturnValue(sessionValue);

      expect(client.getSession()).toEqual(sessionValue);
    });
  });
});
