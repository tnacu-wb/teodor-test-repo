import { NextRequest } from 'next/server';

import { auth0 } from './lib/auth0';
import { middleware, config } from './middleware';

// Mock auth0
jest.mock('./lib/auth0', () => ({
  auth0: {
    middleware: jest.fn(),
  },
}));

describe('middleware', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should call auth0.middleware with the request', async () => {
    const mockRequest = {
      url: 'https://example.com/test',
      nextUrl: {
        pathname: '/test',
      },
    } as unknown as NextRequest;

    const mockResponse = { status: 200 };
    (auth0.middleware as jest.Mock).mockResolvedValue(mockResponse);

    const result = await middleware(mockRequest);

    expect(auth0.middleware).toHaveBeenCalledWith(mockRequest);
    expect(result).toBe(mockResponse);
  });

  it('should handle errors from auth0.middleware', async () => {
    const mockRequest = {
      url: 'https://example.com/test',
      nextUrl: {
        pathname: '/test',
      },
    } as unknown as NextRequest;

    const error = new Error('Auth0 error');
    (auth0.middleware as jest.Mock).mockRejectedValue(error);

    await expect(middleware(mockRequest)).rejects.toThrow('Auth0 error');
  });

  describe('config', () => {
    it('should have correct matcher configuration', () => {
      expect(config.matcher).toBeDefined();
      expect(Array.isArray(config.matcher)).toBe(true);
      expect(config.matcher).toHaveLength(1);
    });

    it('should exclude static files and metadata files', () => {
      const matcher = config.matcher[0];

      // Test that it excludes _next/static
      expect(matcher).toContain('_next/static');
      // Test that it excludes _next/image
      expect(matcher).toContain('_next/image');
      // Test that it excludes metadata files
      expect(matcher).toContain('favicon.ico');
      expect(matcher).toContain('sitemap.xml');
      expect(matcher).toContain('robots.txt');
    });
  });
});
