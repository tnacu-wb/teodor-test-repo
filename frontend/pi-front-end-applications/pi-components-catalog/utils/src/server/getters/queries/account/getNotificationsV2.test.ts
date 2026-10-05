import { cookies } from 'next/headers';

import { executeGraphQLQuery } from '../../..';
import getNotificationsV2 from './getNotificationsV2';

jest.mock('../../..', () => ({
  executeGraphQLQuery: jest.fn(),
  ID_TOKEN_COOKIE: 'id_token_cookie',
}));
jest.mock('@whitbread-eos/api', () => ({
  getNotificationsV2Query: jest.fn(() => 'mocked-query'),
}));
jest.mock('next/headers', () => ({
  cookies: jest.fn(),
}));

describe('getNotificationsV2', () => {
  const mockToken = 'mocked-token';
  const mockNotifications = [{ id: 1, message: 'Test notification' }];
  const mockResponse = { data: { getNotificationsV2: mockNotifications } };
  const originalWindow = global.window;

  beforeEach(() => {
    jest.clearAllMocks();
    // Remove window to simulate server-side environment
    // @ts-expect-error - intentionally deleting window for test
    delete global.window;
    (cookies as jest.Mock).mockReturnValue({
      get: jest.fn(() => ({ value: mockToken })),
    });
    (executeGraphQLQuery as jest.Mock).mockResolvedValue(mockNotifications);
  });

  afterEach(() => {
    global.window = originalWindow;
  });

  it('should call executeGraphQLQuery with correct parameters', async () => {
    await getNotificationsV2();
    expect(executeGraphQLQuery).toHaveBeenCalledWith(
      'mocked-query',
      {},
      expect.any(Function),
      mockToken,
      true,
      true
    );
  });

  it('should use provided token when token argument is supplied', async () => {
    await getNotificationsV2('provided-token');

    expect(cookies).not.toHaveBeenCalled();
    expect(executeGraphQLQuery).toHaveBeenCalledWith(
      'mocked-query',
      {},
      expect.any(Function),
      'provided-token',
      true,
      true
    );
  });

  it('should return notifications from the response', async () => {
    (executeGraphQLQuery as jest.Mock).mockImplementation(async (_query, _vars, cb) =>
      cb(mockResponse)
    );
    const result = await getNotificationsV2();
    expect(result).toEqual(mockNotifications);
  });

  it('should not call executeGraphQLQuery when cookie token is missing', async () => {
    (cookies as jest.Mock).mockReturnValue({
      get: jest.fn(() => undefined),
    });

    const result = await getNotificationsV2();

    expect(result).toBeNull();
    expect(executeGraphQLQuery).not.toHaveBeenCalled();
  });

  it('should not call executeGraphQLQuery when token argument is blank', async () => {
    const result = await getNotificationsV2('   ');

    expect(result).toBeNull();
    expect(cookies).not.toHaveBeenCalled();
    expect(executeGraphQLQuery).not.toHaveBeenCalled();
  });

  it('should return undefined if response does not contain notifications', async () => {
    (executeGraphQLQuery as jest.Mock).mockImplementation(async (_query, _vars, cb) =>
      cb({ data: {} })
    );
    const result = await getNotificationsV2();
    expect(result).toBeUndefined();
  });
});
