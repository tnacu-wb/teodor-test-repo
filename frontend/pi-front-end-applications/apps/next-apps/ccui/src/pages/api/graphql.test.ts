import '@testing-library/jest-dom';
import { NextApiRequest, NextApiResponse } from 'next';
import httpProxyMiddleware from 'next-http-proxy-middleware';
import getConfig from 'next/config';

import { auth0 } from '../../lib/auth0';
import handler from './graphql';

jest.mock('next/config');
jest.mock('next-http-proxy-middleware');
jest.mock('../../lib/auth0', () => ({
  auth0: {
    getSession: jest.fn(),
  },
}));

describe('GraphQL Proxy API Route', () => {
  const mockGetConfig = jest.mocked(getConfig);
  const mockHttpProxyMiddleware = jest.mocked(httpProxyMiddleware);
  const mockGetSession = jest.mocked(auth0.getSession);

  let mockReq: Partial<NextApiRequest>;
  let mockRes: Partial<NextApiResponse>;

  const defaultConfig = {
    publicRuntimeConfig: {
      NEXT_PUBLIC_GRAPHQL_ENDPOINT: 'https://api.example.com/graphql',
      NEXT_PUBLIC_APP_NAME: 'test-app',
      NEXT_PUBLIC_APOLLO_CLIENT_VERSION: '1.0.0',
    },
  };

  beforeEach(() => {
    jest.clearAllMocks();

    mockReq = {
      headers: {},
    } as Partial<NextApiRequest>;

    mockRes = {
      status: jest.fn().mockReturnThis(),
      json: jest.fn().mockReturnThis(),
      end: jest.fn().mockReturnThis(),
    } as Partial<NextApiResponse>;

    mockGetConfig.mockReturnValue(defaultConfig);
    mockGetSession.mockResolvedValue(null);
    mockHttpProxyMiddleware.mockResolvedValue(undefined);
  });

  describe('Basic functionality', () => {
    it('should call httpProxyMiddleware with correct parameters', async () => {
      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      expect(mockHttpProxyMiddleware).toHaveBeenCalledWith(
        mockReq,
        mockRes,
        expect.objectContaining({
          target: 'https://api.example.com/graphql',
          pathRewrite: [
            {
              patternStr: '^/api/graphql',
              replaceStr: '',
            },
          ],
          headers: expect.any(Object),
        })
      );
    });

    it('should pass through request and response objects', async () => {
      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      const callArgs = mockHttpProxyMiddleware.mock.calls[0];
      expect(callArgs[0]).toBe(mockReq);
      expect(callArgs[1]).toBe(mockRes);
    });

    it('should return the result from httpProxyMiddleware', async () => {
      const mockResult = { success: true };
      mockHttpProxyMiddleware.mockResolvedValue(mockResult as any);

      const result = await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      expect(result).toEqual(mockResult);
    });
  });

  describe('Configuration', () => {
    it('should get runtime config', async () => {
      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      expect(mockGetConfig).toHaveBeenCalled();
    });

    it('should use config values for GraphQL endpoint', async () => {
      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      expect(mockHttpProxyMiddleware).toHaveBeenCalledWith(
        expect.anything(),
        expect.anything(),
        expect.objectContaining({
          target: 'https://api.example.com/graphql',
        })
      );
    });

    it('should use config values for client name and version in headers', async () => {
      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      expect(mockHttpProxyMiddleware).toHaveBeenCalledWith(
        expect.anything(),
        expect.anything(),
        expect.objectContaining({
          headers: {
            'apollographql-client-name': 'test-app',
            'apollographql-client-version': '1.0.0',
          },
        })
      );
    });

    it('should handle missing config with empty object', async () => {
      mockGetConfig.mockReturnValue(null as any);

      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      expect(mockHttpProxyMiddleware).toHaveBeenCalledWith(
        expect.anything(),
        expect.anything(),
        expect.objectContaining({
          headers: {
            'apollographql-client-name': '',
            'apollographql-client-version': '',
          },
        })
      );
    });

    it('should handle missing publicRuntimeConfig with empty object', async () => {
      mockGetConfig.mockReturnValue({});

      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      expect(mockHttpProxyMiddleware).toHaveBeenCalledWith(
        expect.anything(),
        expect.anything(),
        expect.objectContaining({
          headers: {
            'apollographql-client-name': '',
            'apollographql-client-version': '',
          },
        })
      );
    });

    it('should use empty string when NEXT_PUBLIC_APP_NAME is missing', async () => {
      mockGetConfig.mockReturnValue({
        publicRuntimeConfig: {
          NEXT_PUBLIC_GRAPHQL_ENDPOINT: 'https://api.example.com/graphql',
          NEXT_PUBLIC_APOLLO_CLIENT_VERSION: '1.0.0',
        },
      });

      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      expect(mockHttpProxyMiddleware).toHaveBeenCalledWith(
        expect.anything(),
        expect.anything(),
        expect.objectContaining({
          headers: expect.objectContaining({
            'apollographql-client-name': '',
          }),
        })
      );
    });

    it('should use empty string when NEXT_PUBLIC_APOLLO_CLIENT_VERSION is missing', async () => {
      mockGetConfig.mockReturnValue({
        publicRuntimeConfig: {
          NEXT_PUBLIC_GRAPHQL_ENDPOINT: 'https://api.example.com/graphql',
          NEXT_PUBLIC_APP_NAME: 'test-app',
        },
      });

      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      expect(mockHttpProxyMiddleware).toHaveBeenCalledWith(
        expect.anything(),
        expect.anything(),
        expect.objectContaining({
          headers: expect.objectContaining({
            'apollographql-client-version': '',
          }),
        })
      );
    });
  });

  describe('Authentication', () => {
    it('should get auth0 session', async () => {
      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      expect(mockGetSession).toHaveBeenCalledWith(mockReq);
    });

    it('should use access token from session if available', async () => {
      mockGetSession.mockResolvedValue({
        tokenSet: {
          accessToken: 'session-access-token',
        },
      } as any);

      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      expect(mockHttpProxyMiddleware).toHaveBeenCalledWith(
        expect.anything(),
        expect.anything(),
        expect.objectContaining({
          headers: expect.objectContaining({
            Authorization: 'Bearer session-access-token',
          }),
        })
      );
    });

    it('should format session token as Bearer token', async () => {
      mockGetSession.mockResolvedValue({
        tokenSet: {
          accessToken: 'my-token-123',
        },
      } as any);

      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      const headers = (mockHttpProxyMiddleware.mock.calls[0][2] as any).headers;
      expect(headers.Authorization).toBe('Bearer my-token-123');
    });

    it('should use authorization header from request if no session token', async () => {
      mockReq.headers = {
        authorization: 'Bearer request-token',
      };

      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      expect(mockHttpProxyMiddleware).toHaveBeenCalledWith(
        expect.anything(),
        expect.anything(),
        expect.objectContaining({
          headers: expect.objectContaining({
            Authorization: 'Bearer request-token',
          }),
        })
      );
    });

    it('should prefer session token over request authorization header', async () => {
      mockGetSession.mockResolvedValue({
        tokenSet: {
          accessToken: 'session-token',
        },
      } as any);

      mockReq.headers = {
        authorization: 'Bearer request-token',
      };

      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      expect(mockHttpProxyMiddleware).toHaveBeenCalledWith(
        expect.anything(),
        expect.anything(),
        expect.objectContaining({
          headers: expect.objectContaining({
            Authorization: 'Bearer session-token',
          }),
        })
      );
    });

    it('should handle missing session', async () => {
      mockGetSession.mockResolvedValue(null);

      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      const headers = (mockHttpProxyMiddleware.mock.calls[0][2] as any).headers;
      expect(headers.Authorization).toBeUndefined();
    });

    it('should handle session without tokenSet', async () => {
      mockGetSession.mockResolvedValue({} as any);

      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      const headers = (mockHttpProxyMiddleware.mock.calls[0][2] as any).headers;
      expect(headers.Authorization).toBeUndefined();
    });

    it('should handle session with tokenSet but no accessToken', async () => {
      mockGetSession.mockResolvedValue({
        tokenSet: {},
      } as any);

      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      const headers = (mockHttpProxyMiddleware.mock.calls[0][2] as any).headers;
      expect(headers.Authorization).toBeUndefined();
    });

    it('should not include Authorization header when no token available', async () => {
      mockGetSession.mockResolvedValue(null);
      mockReq.headers = {};

      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      expect(mockHttpProxyMiddleware).toHaveBeenCalledWith(
        expect.anything(),
        expect.anything(),
        expect.objectContaining({
          headers: {
            'apollographql-client-name': 'test-app',
            'apollographql-client-version': '1.0.0',
            // No Authorization header
          },
        })
      );
    });
  });

  describe('Headers', () => {
    it('should include apollographql-client-name header', async () => {
      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      const headers = (mockHttpProxyMiddleware.mock.calls[0][2] as any).headers;
      expect(headers['apollographql-client-name']).toBe('test-app');
    });

    it('should include apollographql-client-version header', async () => {
      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      const headers = (mockHttpProxyMiddleware.mock.calls[0][2] as any).headers;
      expect(headers['apollographql-client-version']).toBe('1.0.0');
    });

    it('should include Authorization header when session token exists', async () => {
      mockGetSession.mockResolvedValue({
        tokenSet: {
          accessToken: 'test-token',
        },
      } as any);

      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      const headers = (mockHttpProxyMiddleware.mock.calls[0][2] as any).headers;
      expect(headers.Authorization).toBe('Bearer test-token');
    });

    it('should include all headers when token is available', async () => {
      mockGetSession.mockResolvedValue({
        tokenSet: {
          accessToken: 'test-token',
        },
      } as any);

      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      expect(mockHttpProxyMiddleware).toHaveBeenCalledWith(
        expect.anything(),
        expect.anything(),
        expect.objectContaining({
          headers: {
            'apollographql-client-name': 'test-app',
            'apollographql-client-version': '1.0.0',
            Authorization: 'Bearer test-token',
          },
        })
      );
    });
  });

  describe('Proxy configuration', () => {
    it('should target correct GraphQL endpoint', async () => {
      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      const config = mockHttpProxyMiddleware.mock.calls[0][2];
      expect(config?.target).toBe('https://api.example.com/graphql');
    });

    it('should configure path rewrite correctly', async () => {
      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      const config = mockHttpProxyMiddleware.mock.calls[0][2];
      expect(config?.pathRewrite).toEqual([
        {
          patternStr: '^/api/graphql',
          replaceStr: '',
        },
      ]);
    });

    it('should rewrite /api/graphql to empty string', async () => {
      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      const config = mockHttpProxyMiddleware.mock.calls[0][2] as any;
      const pathRewrite = config?.pathRewrite?.[0];
      expect(pathRewrite?.patternStr).toBe('^/api/graphql');
      expect(pathRewrite?.replaceStr).toBe('');
    });

    it('should use different GraphQL endpoint from config', async () => {
      mockGetConfig.mockReturnValue({
        publicRuntimeConfig: {
          NEXT_PUBLIC_GRAPHQL_ENDPOINT: 'https://different-api.com/graphql',
          NEXT_PUBLIC_APP_NAME: 'test-app',
          NEXT_PUBLIC_APOLLO_CLIENT_VERSION: '1.0.0',
        },
      });

      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      expect(mockHttpProxyMiddleware).toHaveBeenCalledWith(
        expect.anything(),
        expect.anything(),
        expect.objectContaining({
          target: 'https://different-api.com/graphql',
        })
      );
    });
  });

  describe('Error handling', () => {
    it('should propagate httpProxyMiddleware errors', async () => {
      const error = new Error('Proxy error');
      mockHttpProxyMiddleware.mockRejectedValue(error);

      await expect(handler(mockReq as NextApiRequest, mockRes as NextApiResponse)).rejects.toThrow(
        'Proxy error'
      );
    });

    it('should propagate auth0 session errors', async () => {
      const error = new Error('Auth0 session error');
      mockGetSession.mockRejectedValue(error);

      await expect(handler(mockReq as NextApiRequest, mockRes as NextApiResponse)).rejects.toThrow(
        'Auth0 session error'
      );
    });

    it('should handle getConfig throwing error', async () => {
      mockGetConfig.mockImplementation(() => {
        throw new Error('Config error');
      });

      await expect(handler(mockReq as NextApiRequest, mockRes as NextApiResponse)).rejects.toThrow(
        'Config error'
      );
    });
  });

  describe('Integration scenarios', () => {
    it('should handle complete authenticated request with all config', async () => {
      mockGetSession.mockResolvedValue({
        tokenSet: {
          accessToken: 'full-access-token',
        },
      } as any);

      mockGetConfig.mockReturnValue({
        publicRuntimeConfig: {
          NEXT_PUBLIC_GRAPHQL_ENDPOINT: 'https://production-api.com/graphql',
          NEXT_PUBLIC_APP_NAME: 'production-app',
          NEXT_PUBLIC_APOLLO_CLIENT_VERSION: '2.5.0',
        },
      });

      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      expect(mockHttpProxyMiddleware).toHaveBeenCalledWith(mockReq, mockRes, {
        target: 'https://production-api.com/graphql',
        pathRewrite: [
          {
            patternStr: '^/api/graphql',
            replaceStr: '',
          },
        ],
        headers: {
          'apollographql-client-name': 'production-app',
          'apollographql-client-version': '2.5.0',
          Authorization: 'Bearer full-access-token',
        },
      });
    });

    it('should handle unauthenticated request with minimal config', async () => {
      mockGetSession.mockResolvedValue(null);

      mockGetConfig.mockReturnValue({
        publicRuntimeConfig: {
          NEXT_PUBLIC_GRAPHQL_ENDPOINT: 'https://api.example.com/graphql',
        },
      });

      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      expect(mockHttpProxyMiddleware).toHaveBeenCalledWith(mockReq, mockRes, {
        target: 'https://api.example.com/graphql',
        pathRewrite: [
          {
            patternStr: '^/api/graphql',
            replaceStr: '',
          },
        ],
        headers: {
          'apollographql-client-name': '',
          'apollographql-client-version': '',
        },
      });
    });

    it('should handle request with authorization header but no session', async () => {
      mockGetSession.mockResolvedValue(null);
      mockReq.headers = {
        authorization: 'Bearer client-provided-token',
      };

      await handler(mockReq as NextApiRequest, mockRes as NextApiResponse);

      expect(mockHttpProxyMiddleware).toHaveBeenCalledWith(
        expect.anything(),
        expect.anything(),
        expect.objectContaining({
          headers: expect.objectContaining({
            Authorization: 'Bearer client-provided-token',
          }),
        })
      );
    });
  });

  describe('Type safety', () => {
    it('should handle request with proper NextApiRequest type', async () => {
      const typedReq = mockReq as NextApiRequest;
      await handler(typedReq, mockRes as NextApiResponse);

      expect(mockHttpProxyMiddleware).toHaveBeenCalled();
    });

    it('should handle response with proper NextApiResponse type', async () => {
      const typedRes = mockRes as NextApiResponse;
      await handler(mockReq as NextApiRequest, typedRes);

      expect(mockHttpProxyMiddleware).toHaveBeenCalled();
    });
  });
});
