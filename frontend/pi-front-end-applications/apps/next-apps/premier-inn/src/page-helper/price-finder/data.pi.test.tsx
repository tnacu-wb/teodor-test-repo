import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { IncomingMessage, ServerResponse } from 'http';

import { createPriceFinderPiDataLoaderFn } from './index';

const mockStaticContentData = {
  headerInformation: { config: { promotionBanner: { title: 'Test Banner' } } },
  footer: { links: [] },
  labels: { test: 'Test Label' },
};

const mockSeoData = {
  seoInformation: {
    title: 'Price Finder - Premier Inn',
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
  return function () {
    return {
      get: (key: string) => {
        if (key === 'id_token_cookie') return 'mockToken';
        if (key === 'WB-SESSION-ID') return 'mock-session-id';
        return key;
      },
    };
  };
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

describe('createPriceFinderPiDataLoader', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should create data loader with expected object structure', async () => {
    const dataLoaderPI = await createPriceFinderPiDataLoaderFn({
      ...mockedData,
      queryClient,
    });

    expect(dataLoaderPI).toEqual({
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

    const dataLoaderPI = await createPriceFinderPiDataLoaderFn({
      ...germanData,
      queryClient,
    });

    expect(dataLoaderPI).toEqual({
      dehydratedState: { mutations: [], queries: [] },
      staticData: mockStaticContentData,
    });
  });
});
