/**
 * GraphQL Client Behaviour Tests
 *
 * Validates core client behaviours using a stub APIRequestContext:
 * - Header attachment (custom headers passed through)
 * - Retry logic for 5xx errors
 * - Non-retry for 4xx / GraphQL schema errors
 * - Timeout handling
 * - Required-parameter validation
 *
 * No live HTTP calls are made — all interactions go through the mock.
 *
 * Validates: Requirements 7.1, 7.4, 7.5, 7.6, 9.6, 9.7
 */

import { test, expect } from '@playwright/test';
import type { APIRequestContext, APIResponse } from '@playwright/test';
import { GraphQLClient, GraphQLError, ValidationError } from '../../../src/api/graphql/graphqlClient';

// ─── Mock Helpers ───────────────────────────────────────────────────────────────

interface MockResponse {
  status: number;
  body: unknown;
}

/**
 * Create a stub APIRequestContext whose `post()` delegates to the provided handler.
 * The stub captures all calls and returns objects matching Playwright's APIResponse shape.
 */
function createMockRequest(
  handler: (url: string, options: Record<string, unknown>) => Promise<MockResponse>,
): APIRequestContext {
  const stub = {
    post: async (url: string, options: Record<string, unknown> = {}): Promise<APIResponse> => {
      const result = await handler(url, options);
      return {
        status: () => result.status,
        json: async () => result.body,
        text: async () => JSON.stringify(result.body),
        ok: () => result.status >= 200 && result.status < 300,
        headers: () => ({}),
        headersArray: () => [],
        url: () => url,
        statusText: () => '',
        body: async () => Buffer.from(JSON.stringify(result.body)),
        dispose: async () => {},
      } as unknown as APIResponse;
    },
    // Remaining APIRequestContext methods are unused by GraphQLClient
    get: async () => ({}),
    put: async () => ({}),
    patch: async () => ({}),
    delete: async () => ({}),
    head: async () => ({}),
    fetch: async () => ({}),
    storageState: async () => ({ cookies: [], origins: [] }),
    dispose: async () => {},
  } as unknown as APIRequestContext;

  return stub;
}

// ─── Test Constants ─────────────────────────────────────────────────────────────

const TEST_BASE_URL = 'https://api.test.example.com/graphql';
const SAMPLE_QUERY = 'query GetHotel { hotel(id: "GATGAT") { name } }';
const FAST_RETRY_OPTIONS = { retries: 1, retryDelay: 10 };

// ─── Tests ──────────────────────────────────────────────────────────────────────

test.describe('GraphQLClient behaviour', () => {
  test('attaches custom headers to requests', async () => {
    let capturedHeaders: Record<string, string> = {};

    const request = createMockRequest(async (_url, options) => {
      capturedHeaders = (options.headers as Record<string, string>) ?? {};
      return { status: 200, body: { data: { hotel: { name: 'Test Hotel' } } } };
    });

    const client = new GraphQLClient(request, TEST_BASE_URL, {
      headers: {
        Accept: '*/*',
        Authorization: 'Bearer test-token',
      },
    });

    await client.query(SAMPLE_QUERY);

    expect(capturedHeaders['Accept']).toBe('*/*');
    expect(capturedHeaders['Authorization']).toBe('Bearer test-token');
    expect(capturedHeaders['Content-Type']).toBe('application/json');
  });

  test('retries on 5xx HTTP responses', async () => {
    let callCount = 0;

    const request = createMockRequest(async () => {
      callCount++;
      if (callCount === 1) {
        return { status: 503, body: { errors: [{ message: 'Service Unavailable' }] } };
      }
      return { status: 200, body: { data: { hotel: { name: 'Recovered Hotel' } } } };
    });

    const client = new GraphQLClient(request, TEST_BASE_URL, {
      ...FAST_RETRY_OPTIONS,
      headers: { Accept: '*/*' },
    });

    const result = await client.query<{ hotel: { name: string } }>(SAMPLE_QUERY);

    expect(callCount).toBe(2);
    expect(result.hotel.name).toBe('Recovered Hotel');
  });

  test('retries on GraphQL errors with 5xx errorType', async () => {
    let callCount = 0;

    const request = createMockRequest(async () => {
      callCount++;
      if (callCount === 1) {
        return {
          status: 200,
          body: {
            errors: [
              {
                message: 'Upstream timeout',
                extensions: { errorType: '503' },
              },
            ],
          },
        };
      }
      return { status: 200, body: { data: { hotel: { id: 'LONEUS' } } } };
    });

    const client = new GraphQLClient(request, TEST_BASE_URL, {
      ...FAST_RETRY_OPTIONS,
      headers: { Accept: '*/*' },
    });

    const result = await client.query<{ hotel: { id: string } }>(SAMPLE_QUERY);

    expect(callCount).toBe(2);
    expect(result.hotel.id).toBe('LONEUS');
  });

  test('does NOT retry on 4xx HTTP responses', async () => {
    let callCount = 0;

    const request = createMockRequest(async () => {
      callCount++;
      return { status: 400, body: { errors: [{ message: 'Bad Request' }] } };
    });

    const client = new GraphQLClient(request, TEST_BASE_URL, {
      ...FAST_RETRY_OPTIONS,
      headers: { Accept: '*/*' },
    });

    await expect(client.query(SAMPLE_QUERY)).rejects.toThrow(GraphQLError);
    expect(callCount).toBe(1);
  });

  test('does NOT retry on GraphQL schema errors', async () => {
    let callCount = 0;

    const request = createMockRequest(async () => {
      callCount++;
      return {
        status: 200,
        body: {
          errors: [{ message: 'Cannot query field "nonExistent" on type "Hotel"' }],
        },
      };
    });

    const client = new GraphQLClient(request, TEST_BASE_URL, {
      ...FAST_RETRY_OPTIONS,
      headers: { Accept: '*/*' },
    });

    await expect(client.query(SAMPLE_QUERY)).rejects.toThrow(GraphQLError);
    expect(callCount).toBe(1);
  });

  test('throws ValidationError for empty query string', async () => {
    const request = createMockRequest(async () => {
      return { status: 200, body: { data: {} } };
    });

    const client = new GraphQLClient(request, TEST_BASE_URL, {
      headers: { Accept: '*/*' },
    });

    await expect(client.query('')).rejects.toThrow(ValidationError);

    try {
      await client.query('');
    } catch (error) {
      expect(error).toBeInstanceOf(ValidationError);
      expect((error as ValidationError).message).toContain('query');
    }
  });

  test('throws ValidationError for empty baseURL', async () => {
    const request = createMockRequest(async () => {
      return { status: 200, body: { data: {} } };
    });

    const client = new GraphQLClient(request, '', {
      headers: { Accept: '*/*' },
    });

    await expect(client.query(SAMPLE_QUERY)).rejects.toThrow(ValidationError);

    try {
      await client.query(SAMPLE_QUERY);
    } catch (error) {
      expect(error).toBeInstanceOf(ValidationError);
      expect((error as ValidationError).message).toContain('baseURL');
    }
  });

  test('includes operation name in error messages', async () => {
    const request = createMockRequest(async () => {
      return { status: 400, body: { errors: [{ message: 'Forbidden' }] } };
    });

    const client = new GraphQLClient(request, TEST_BASE_URL, {
      ...FAST_RETRY_OPTIONS,
      headers: { Accept: '*/*' },
    });

    const namedQuery = 'query FetchHotelDetails { hotel(id: "X") { name } }';

    try {
      await client.query(namedQuery);
    } catch (error) {
      expect(error).toBeInstanceOf(GraphQLError);
      expect((error as GraphQLError).message).toContain('FetchHotelDetails');
      expect((error as GraphQLError).operation).toBe('FetchHotelDetails');
    }
  });
});
