import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { IncomingMessage, ServerResponse } from 'http';

import { createPriceFinderCcuiDataLoaderFn } from './index';

const mockStaticContentData = {
  headerInformation: { config: { promotionBanner: { title: 'Test Banner' } } },
  footer: { links: [] },
  labels: { test: 'Test Label' },
};

const mockSeoData = {
  seoInformation: {
    title: 'Price Finder - CCUI',
    description: 'Find the best prices for your stay',
  },
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  graphQLRequest: jest.fn(),
  getGQLClient: jest.fn(),
  QueriesLogger: jest.fn().mockImplementation(() => ({
    fetchQuery: jest.fn().mockImplementation((queryKey, queryFn) => {
      const key = queryKey[0];
      queryFn();
      if (key === 'GetStaticContent') {
        return Promise.resolve(mockStaticContentData);
      }
      return Promise.resolve({});
    }),
    prefetchQuery: jest.fn().mockImplementation((queryKey, queryFn) => {
      const key = queryKey[0];
      queryFn();
      if (key === 'seoInformation') {
        return Promise.resolve(mockSeoData);
      }
      return Promise.resolve({});
    }),
    logQueries: jest.fn(),
  })),
}));

jest.mock('cookies', () => {
  return jest.fn().mockImplementation(() => ({
    get: () => null,
    set: () => null,
  }));
});

const queryClient = new ReactQuery.QueryClient();

const mockedData = {
  session: {
    accessToken: 'mockAccessToken123',
    user: {},
  },
  resolvedUrl: '/',
  query: {
    test: 'value',
  },
  language: 'en',
  country: 'GB',
  res: {} as ServerResponse,
  req: { url: undefined, cookies: {} } as IncomingMessage & {
    cookies: Partial<{ [key: string]: string }>;
  },
};

describe('createPriceFinderCcuiDataLoader', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should create data loader with expected object structure', async () => {
    const dataLoaderCcui = await createPriceFinderCcuiDataLoaderFn({
      ...mockedData,
      queryClient,
    });

    expect(dataLoaderCcui).toEqual({
      dehydratedState: { mutations: [], queries: [] },
      staticData: mockStaticContentData,
    });
  });

  it('should handle different language configurations', async () => {
    const germanData = {
      ...mockedData,
      language: 'de',
      country: 'DE',
    };

    const dataLoaderCcui = await createPriceFinderCcuiDataLoaderFn({
      ...germanData,
      queryClient,
    });

    expect(dataLoaderCcui).toEqual({
      dehydratedState: { mutations: [], queries: [] },
      staticData: mockStaticContentData,
    });
  });

  it('should call QueriesLogger with CCUI context', async () => {
    const mockQueriesLogger = jest.requireMock('@whitbread-eos/utils').QueriesLogger;

    await createPriceFinderCcuiDataLoaderFn({
      ...mockedData,
      queryClient,
    });

    expect(mockQueriesLogger).toHaveBeenCalledWith(
      queryClient,
      mockedData.req,
      mockedData.res,
      mockedData.query,
      'CCUI | Price Finder Page'
    );
  });

  it('should handle error scenarios gracefully', async () => {
    const mockQueriesLogger = jest.requireMock('@whitbread-eos/utils').QueriesLogger;

    // Create a new mock that will throw an error
    mockQueriesLogger.mockImplementationOnce(() => ({
      fetchQuery: jest.fn().mockRejectedValue(new Error('Query failed')),
      prefetchQuery: jest.fn().mockResolvedValue(mockSeoData),
      logQueries: jest.fn(),
    }));

    // Expect the function to reject with the error
    await expect(
      createPriceFinderCcuiDataLoaderFn({
        ...mockedData,
        queryClient,
      })
    ).rejects.toThrow('Query failed');
  });

  it('should handle missing query parameters', async () => {
    const dataWithoutQuery = {
      ...mockedData,
      query: {},
    };

    const dataLoaderCcui = await createPriceFinderCcuiDataLoaderFn({
      ...dataWithoutQuery,
      queryClient,
    });

    expect(dataLoaderCcui).toEqual({
      dehydratedState: { mutations: [], queries: [] },
      staticData: mockStaticContentData,
    });
  });
});
