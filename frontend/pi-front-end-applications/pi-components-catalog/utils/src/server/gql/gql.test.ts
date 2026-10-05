import { logger } from '../../logger/logger';
import { executeGraphQLMutation, executeGraphQLQuery, getHeaders } from './gql';

jest.mock('next/config', () => () => ({
  publicRuntimeConfig: {},
}));

jest.mock('../../logger/logger', () => ({
  logger: {
    info: jest.fn(),
    error: jest.fn(),
  },
}));

const mockFetchResponse = {
  data: {},
} as any;

const mockOkStatus = { value: true };

global.fetch = jest.fn(() =>
  Promise.resolve({
    json: () => Promise.resolve(mockFetchResponse),
    ok: mockOkStatus.value,
    redirected: false,
    status: 200,
    statusText: 'OK',
    type: 'basic',
    url: '',
    body: null,
    bodyUsed: false,
  } as Response)
);

const defaultFetch = global.fetch;

let mockCachedFetchResponse = true;

const mockCachedFetch = jest.fn(() => mockCachedFetchResponse);

jest.mock('./cachedFetch', () => ({
  cachedFetch: mockCachedFetch,
}));

let mockAppRouter = false;

jest.mock('next/headers', () => ({
  headers: () => {
    if (mockAppRouter) {
      return {
        get: () => '',
      };
    }

    throw new Error();
  },
  cookies: () => {
    return {
      get: (name: string) =>
        name === 'WB-SESSION-ID' ? { value: 'server-session-id' } : undefined,
    };
  },
}));

describe('executeGraphQLQuery', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockOkStatus.value = true;
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = {};
    mockAppRouter = false;
    mockCachedFetchResponse = true;
    global.fetch = defaultFetch;
  });

  it('should not log on client side', async () => {
    const originalWindow = global.window;
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    global.window = {};

    mockFetchResponse.data = {};
    mockFetchResponse.errors = [{ message: 'err' }];

    await executeGraphQLQuery(
      'query test { a }',
      {},
      (x) => x.data,
      '',
      false,
      true,
      {},
      {
        pageName: 'client-test',
      }
    );

    expect(logger.info).not.toHaveBeenCalled();
    expect(logger.error).not.toHaveBeenCalled();

    global.window = originalWindow;
  });

  it('should log query errors on server side', async () => {
    const originalWindow = global.window;
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    delete global.window;

    mockFetchResponse.data = { ok: true };
    mockFetchResponse.errors = [{ message: 'err' }];

    await executeGraphQLQuery('query test { a }', {}, (x) => x.data, '', false, true);

    expect(logger.error).toHaveBeenCalledWith(
      expect.objectContaining({ label: 'GRAPHQL_QUERY_ERROR' })
    );

    global.window = originalWindow;
  });

  it('should log request failed on server side', async () => {
    const originalWindow = global.window;
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    delete global.window;

    global.fetch = jest.fn().mockRejectedValueOnce(new Error('Network error'));

    await executeGraphQLQuery('query test { a }', {}, (x) => x.data);

    expect(logger.error).toHaveBeenCalledWith(
      expect.objectContaining({ label: 'GRAPHQL_QUERY_REQUEST_FAILED' })
    );

    global.window = originalWindow;
  });

  it('should log on server side when logContext.pageName provided', async () => {
    const originalWindow = global.window;
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    delete global.window;

    mockFetchResponse.data = {};
    mockFetchResponse.errors = undefined;

    await executeGraphQLQuery(
      'query test { a }',
      {},
      (x) => x.data,
      '',
      false,
      true,
      {},
      {
        pageName: 'server-test',
      }
    );

    expect(logger.info).toHaveBeenCalledWith(
      expect.objectContaining({ label: 'PAGE_GRAPHQL_REQUEST', pageName: 'server-test' })
    );

    global.window = originalWindow;
  });

  it('should include variables for getAccountList in page log', async () => {
    const originalWindow = global.window;
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    delete global.window;

    mockFetchResponse.data = {};
    mockFetchResponse.errors = undefined;

    await executeGraphQLQuery(
      'query getAccountList { a }',
      { viewAll: false },
      (x) => x.data,
      '',
      false,
      true,
      {},
      { pageName: 'server-test' }
    );

    expect(logger.info).toHaveBeenCalledWith(
      expect.objectContaining({
        label: 'PAGE_GRAPHQL_REQUEST',
        operationName: 'getAccountList',
        variables: { viewAll: false },
      })
    );

    global.window = originalWindow;
  });

  it('should fetch the data from cache if revalidateCache = false', async () => {
    mockFetchResponse.data = 'test';
    const result = await executeGraphQLQuery('test', {}, (x) => x.data);

    expect(result).toEqual('test');
    expect(global.fetch).toHaveBeenCalledWith(
      '',
      expect.objectContaining({ next: { revalidate: 0 } })
    );
  });

  it('should not fetch from cache if revalidateCache = true', async () => {
    mockFetchResponse.data = 'test';
    const result = await executeGraphQLQuery('test', {}, (x) => x.data, '', true);

    expect(result).toEqual('test');
    expect(global.fetch).toHaveBeenCalledWith('', expect.objectContaining({ cache: 'no-cache' }));
  });

  it('should fetch again if revalidateCache = false and the response has errors', async () => {
    mockFetchResponse.errors = [1];
    await executeGraphQLQuery('test', {}, (x) => x.data);

    expect(global.fetch).toHaveBeenCalledTimes(2);
  });

  it('should return null if status is not ok', async () => {
    mockOkStatus.value = false;
    mockFetchResponse.data = 'test';
    const result = await executeGraphQLQuery('test', {}, (x) => x.data);

    expect(result).toEqual(null);
  });

  it('should use cachedFetch if server side and app router', async () => {
    const originalWindow = global.window;
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    delete global.window;
    mockAppRouter = true;

    await executeGraphQLQuery('test', {}, (x) => x.data);
    expect(mockCachedFetch).toHaveBeenCalled();

    global.window = originalWindow;
  });

  it('should use cachedFetch if server side and app router, and return null if fetch response is null', async () => {
    const originalWindow = global.window;
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    delete global.window;
    mockAppRouter = true;
    mockCachedFetchResponse = false;

    await executeGraphQLQuery('test', {}, (x) => x.data);
    expect(mockCachedFetch).toHaveBeenCalled();

    global.window = originalWindow;
  });

  it('should not use cachedFetch if not server side', async () => {
    await executeGraphQLQuery('test', {}, (x) => x.data);
    expect(mockCachedFetch).not.toHaveBeenCalled();
  });

  it('should not use cachedFetch if not app router', async () => {
    await executeGraphQLQuery('test', {}, (x) => x.data);
    expect(mockCachedFetch).not.toHaveBeenCalled();
  });
});

describe('executeGraphQLMutation', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockOkStatus.value = true;
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = {};
    global.fetch = defaultFetch;
  });

  it('should execute the mutation', async () => {
    mockFetchResponse.data = 'test';
    const result = await executeGraphQLMutation('test', {}, (x) => x.data);

    expect(result).toEqual('test');
  });

  it('should log mutation errors on server side', async () => {
    const originalWindow = global.window;
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    delete global.window;

    mockFetchResponse.data = { ok: true };
    mockFetchResponse.errors = [{ message: 'err' }];

    await executeGraphQLMutation('mutation test { a }', {}, (x) => x.data);

    expect(logger.error).toHaveBeenCalledWith(
      expect.objectContaining({ label: 'GRAPHQL_MUTATION_ERROR' })
    );

    global.window = originalWindow;
  });

  it('should log mutation request failed on server side', async () => {
    const originalWindow = global.window;
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    delete global.window;

    global.fetch = jest.fn().mockRejectedValueOnce(new Error('Network error'));

    await executeGraphQLMutation('mutation test { a }', {}, (x) => x.data);

    expect(logger.error).toHaveBeenCalledWith(
      expect.objectContaining({ label: 'GRAPHQL_MUTATION_REQUEST_FAILED' })
    );

    global.window = originalWindow;
  });

  it('should return null if status is not ok', async () => {
    mockOkStatus.value = false;
    mockFetchResponse.data = 'test';
    const result = await executeGraphQLMutation('test', {}, (x) => x.data);

    expect(result).toEqual(null);
  });
});

describe('getHeaders', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should include Authorization header if token is available and contentRequest is not defined', async () => {
    const headers = await getHeaders('test-token', {});
    expect(headers).toHaveProperty('Authorization', 'Bearer test-token');
  });

  it('should not include Authorization header if contentRequest is true', async () => {
    const headers = await getHeaders('test-token', {}, true);
    expect(headers).not.toHaveProperty('Authorization');
  });

  it('should include WB-SESSION-ID header if sessionId is available and contentRequest is false', async () => {
    Object.defineProperty(global, 'window', {
      value: undefined,
      writable: true,
      configurable: true,
    });
    mockAppRouter = true;

    const headers = await getHeaders('test-token', {}, false);
    expect(headers).toHaveProperty('WB-SESSION-ID', 'server-session-id');
  });

  it('should not include WB-SESSION-ID header if contentRequest is true', async () => {
    Object.defineProperty(global, 'window', {
      value: undefined,
      writable: true,
      configurable: true,
    });
    mockAppRouter = true;

    const headers = await getHeaders('test-token', {}, true);
    expect(headers).not.toHaveProperty('WB-SESSION-ID');
  });
});
