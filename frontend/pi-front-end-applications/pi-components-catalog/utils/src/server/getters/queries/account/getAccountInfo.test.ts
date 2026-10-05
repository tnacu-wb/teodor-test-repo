import { getAccountInfoQuery, Scheme } from '@whitbread-eos/api';
import { cookies } from 'next/headers';

import { executeGraphQLQuery } from '../../../gql';
import getAccountInfo from './getAccountInfo';

jest.mock('../../../gql', () => ({
  executeGraphQLQuery: jest.fn(),
}));
jest.mock('next/headers', () => ({
  cookies: jest.fn(),
}));
jest.mock('@whitbread-eos/api', () => ({
  ...jest.requireActual('@whitbread-eos/api'),
  getAccountInfoQuery: jest.fn(() => 'MOCK_QUERY'),
  Scheme: { TEST: 'test' },
}));

describe('getAccountInfo', () => {
  const mockCookies = {
    get: jest.fn(),
  };
  const originalWindow = global.window;

  beforeEach(() => {
    jest.clearAllMocks();
    // Remove window to simulate server-side environment
    // @ts-expect-error - intentionally deleting window for test
    delete global.window;
    (cookies as jest.Mock).mockReturnValue(mockCookies);
  });

  afterEach(() => {
    global.window = originalWindow;
  });

  it('should call executeGraphQLQuery with correct params and return response', async () => {
    mockCookies.get.mockReturnValue({ value: 'mock-token' });
    (executeGraphQLQuery as jest.Mock).mockResolvedValue('mock-response');

    const scheme = 'DE' as Scheme;
    const tetheredUserGuid = 'user-guid';
    const result = await getAccountInfo(scheme, tetheredUserGuid);

    expect(getAccountInfoQuery).toHaveBeenCalled();
    expect(executeGraphQLQuery).toHaveBeenCalledWith(
      'MOCK_QUERY',
      { scheme, tetheredUserId: tetheredUserGuid },
      expect.any(Function),
      'mock-token',
      true,
      false
    );
    expect(result).toBe('mock-response');
  });

  it('should use empty string as token if cookie is not set', async () => {
    mockCookies.get.mockReturnValue(undefined);
    (executeGraphQLQuery as jest.Mock).mockResolvedValue('no-cookie-response');

    const scheme = 'GB' as Scheme;
    const result = await getAccountInfo(scheme, '');

    expect(executeGraphQLQuery).toHaveBeenCalledWith(
      'MOCK_QUERY',
      { scheme, tetheredUserId: '' },
      expect.any(Function),
      '',
      true,
      false
    );
    expect(result).toBe('no-cookie-response');
  });

  it('should pass undefined tetheredUserGuid if not provided', async () => {
    mockCookies.get.mockReturnValue({ value: 'mock-token' });
    (executeGraphQLQuery as jest.Mock).mockResolvedValue('response');

    const scheme = 'GB' as Scheme;
    await getAccountInfo(scheme, '');

    expect(executeGraphQLQuery).toHaveBeenCalledWith(
      'MOCK_QUERY',
      { scheme, tetheredUserId: '' },
      expect.any(Function),
      'mock-token',
      true,
      false
    );
  });

  it('should extract getAccountInfo from GqlResponse', async () => {
    mockCookies.get.mockReturnValue({ value: 'mock-token' });
    const gqlResponse = { data: { getAccountInfo: { id: '123' } } };
    (executeGraphQLQuery as jest.Mock).mockImplementation((_query, _vars, selector) =>
      selector(gqlResponse)
    );

    const scheme = 'GB' as Scheme;
    const result = await getAccountInfo(scheme, '');

    expect(result).toEqual({ id: '123' });
  });
});
