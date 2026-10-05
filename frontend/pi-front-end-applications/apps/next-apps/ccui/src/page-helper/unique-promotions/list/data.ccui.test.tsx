import { QueryClient, dehydrate } from '@tanstack/react-query';
import { GET_STATIC_CONTENT, SITE_LEISURE } from '@whitbread-eos/api';
import { graphQLRequest, QueriesLogger } from '@whitbread-eos/utils';

import promoBatchListCCUIDataLoaderFn from './data.ccui';

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  dehydrate: jest.fn(),
}));

jest.mock('@whitbread-eos/utils', () => ({
  graphQLRequest: jest.fn(),
  QueriesLogger: jest.fn(),
}));

describe('promoBatchListCCUIDataLoaderFn', () => {
  const mockPrefetchQuery = jest.fn();
  const mockLogQueries = jest.fn();

  const mockLogger = {
    info: jest.fn(),
  };

  const mockQueryClient = {} as QueryClient;

  const baseProps: any = {
    session: {
      user: { name: 'Test User', email: 'test@test.com' },
    },
    queryClient: mockQueryClient,
    logger: mockLogger,
    language: 'en',
    country: 'UK',
    proxyOptions: {},
    query: {},
    req: {},
    res: {},
  };

  beforeEach(() => {
    jest.clearAllMocks();

    (QueriesLogger as jest.Mock).mockImplementation(() => ({
      prefetchQuery: mockPrefetchQuery,
      logQueries: mockLogQueries,
    }));

    mockPrefetchQuery.mockResolvedValue({});
    (dehydrate as jest.Mock).mockReturnValue('DEHYDRATED_STATE');
  });
  it('should log page load event', async () => {
    await promoBatchListCCUIDataLoaderFn(baseProps);

    expect(mockLogger.info).toHaveBeenCalledWith({
      label: 'CCUI:PromoBatchList',
      message: 'PageLoad',
    });
  });
  it('should prefetch static content using graphQLRequest', async () => {
    await promoBatchListCCUIDataLoaderFn(baseProps);

    expect(mockPrefetchQuery).toHaveBeenCalledWith(
      ['GetStaticContent', 'en', 'UK'],
      expect.any(Function)
    );
    const prefetchFn = mockPrefetchQuery.mock.calls[0][1];
    await prefetchFn();

    expect(graphQLRequest).toHaveBeenCalledWith(
      GET_STATIC_CONTENT,
      {
        language: 'en',
        country: 'UK',
        site: SITE_LEISURE,
        businessBooker: false,
      },
      undefined,
      baseProps.proxyOptions
    );
  });
  it('should initialize QueriesLogger with correct arguments', async () => {
    await promoBatchListCCUIDataLoaderFn(baseProps);

    expect(QueriesLogger).toHaveBeenCalledWith(
      mockQueryClient,
      baseProps.req,
      baseProps.res,
      baseProps.query,
      'CCUI | Unique Promotions | List Page',
      'Test User'
    );
  });

  it('should log queries performance', async () => {
    await promoBatchListCCUIDataLoaderFn(baseProps);

    expect(mockLogQueries).toHaveBeenCalledWith(expect.any(Number));
  });
  it('should return dehydrated state and page props', async () => {
    const result = await promoBatchListCCUIDataLoaderFn(baseProps);

    expect(result).toEqual({
      dehydratedState: 'DEHYDRATED_STATE',
      language: 'en',
      country: 'UK',
      user: baseProps.session.user,
      varinnt: 'agent',
    });
  });
  it('should handle missing session user safely', async () => {
    const propsWithoutUser = {
      ...baseProps,
      session: {},
    };

    const result = await promoBatchListCCUIDataLoaderFn(propsWithoutUser);

    expect(result.user).toBeUndefined();
  });
});
