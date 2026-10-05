import { getLocalhostHeadersAsync } from './localhostHeaders';

const mockGet = jest.fn();
jest.mock('next/headers', () => ({
  headers: jest.fn(() => ({
    get: mockGet,
  })),
}));

describe('getLocalhostHeadersAsync', () => {
  const originalWindow = global.window;

  beforeEach(() => {
    jest.clearAllMocks();
    // Remove window to simulate server-side environment for async tests
    // @ts-expect-error - intentionally deleting window for test
    delete global.window;
  });

  afterEach(() => {
    global.window = originalWindow;
  });

  it('returns empty object if host is not set', async () => {
    mockGet.mockReturnValue(undefined);
    const result = await getLocalhostHeadersAsync(true);
    expect(result).toEqual({});
  });

  it('returns empty object if host does not include localhost', async () => {
    mockGet.mockReturnValue('example.com');
    const result = await getLocalhostHeadersAsync(true);
    expect(result).toEqual({});
  });

  it('returns X-Dev-Nonce and Referer headers if host includes localhost and isServerSide is true', async () => {
    mockGet.mockReturnValue('localhost:3000');
    const result = await getLocalhostHeadersAsync(true);
    expect(result).toHaveProperty('X-Dev-Nonce');
    expect(typeof result['X-Dev-Nonce']).toBe('string');
    expect(result).toHaveProperty('Referer', 'http://localhost:3000/');
  });

  it('generates a different X-Dev-Nonce each call', async () => {
    mockGet.mockReturnValue('localhost:3000');
    const nonce1 = (await getLocalhostHeadersAsync(true))['X-Dev-Nonce'];
    const nonce2 = (await getLocalhostHeadersAsync(true))['X-Dev-Nonce'];
    expect(nonce1).not.toEqual(nonce2);
  });

  it('returns X-Dev-Nonce header on client side when host includes localhost', async () => {
    global.window = { location: { host: 'localhost:3000' } } as Window & typeof globalThis;

    const result = await getLocalhostHeadersAsync(false);
    expect(result).toHaveProperty('X-Dev-Nonce');
    expect(typeof result['X-Dev-Nonce']).toBe('string');
    expect(result).not.toHaveProperty('Referer');

    global.window = originalWindow;
  });
});
