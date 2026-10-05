import { getDdSepaFormStatusQuery, Scheme } from '@whitbread-eos/api';
import { cookies } from 'next/headers';

import { executeGraphQLQuery } from '../../../gql';
import getDdSepaFormStatus from './getDdSepaFormStatus';

jest.mock('../../../gql', () => ({
  executeGraphQLQuery: jest.fn(),
}));
jest.mock('next/headers', () => ({
  cookies: jest.fn(),
}));
jest.mock('@whitbread-eos/api', () => ({
  ...jest.requireActual('@whitbread-eos/api'),
  getDdSepaFormStatusQuery: jest.fn(() => 'MOCK_QUERY'),
  Scheme: { TEST: 'test' },
}));

describe('getDdSepaFormStatus', () => {
  const mockCookies = {
    get: jest.fn(),
  };

  beforeEach(() => {
    jest.clearAllMocks();
    (cookies as jest.Mock).mockReturnValue(mockCookies);
  });

  it('should call executeGraphQLQuery with correct params and return response', async () => {
    mockCookies.get.mockReturnValue({ value: 'mock-token' });
    (executeGraphQLQuery as jest.Mock).mockResolvedValue('mock-response');

    const scheme = 'DE' as Scheme;
    const mockHostedPageGuid = 'mock-hosted-page-guid';
    const result = await getDdSepaFormStatus('mock-token', mockHostedPageGuid, scheme);

    expect(getDdSepaFormStatusQuery).toHaveBeenCalled();
    expect(executeGraphQLQuery).toHaveBeenCalledWith(
      'MOCK_QUERY',
      { scheme, hostedPageGuid: mockHostedPageGuid },
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
    const result = await getDdSepaFormStatus('', '', scheme);

    expect(executeGraphQLQuery).toHaveBeenCalledWith(
      'MOCK_QUERY',
      { scheme, hostedPageGuid: '' },
      expect.any(Function),
      '',
      true,
      false
    );
    expect(result).toBe('no-cookie-response');
  });

  it('should pass undefined hostedPageGuid if not provided', async () => {
    mockCookies.get.mockReturnValue({ value: 'mock-token' });
    (executeGraphQLQuery as jest.Mock).mockResolvedValue('response');

    const scheme = 'GB' as Scheme;
    await getDdSepaFormStatus('mock-token', '', scheme);

    expect(executeGraphQLQuery).toHaveBeenCalledWith(
      'MOCK_QUERY',
      { scheme, hostedPageGuid: '' },
      expect.any(Function),
      'mock-token',
      true,
      false
    );
  });

  it('should extract getDdSepaFormStatus from GqlResponse', async () => {
    mockCookies.get.mockReturnValue({ value: 'mock-token' });
    const gqlResponse = { data: { getDdSepaFormStatus: { id: '123' } } };
    (executeGraphQLQuery as jest.Mock).mockImplementation((_query, _vars, selector) =>
      selector(gqlResponse)
    );

    const scheme = 'GB' as Scheme;
    const result = await getDdSepaFormStatus('mock-token', '', scheme);

    expect(result).toEqual({ id: '123' });
  });
});
