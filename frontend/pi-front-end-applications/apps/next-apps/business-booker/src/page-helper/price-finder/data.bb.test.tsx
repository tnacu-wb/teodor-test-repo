import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';

import { createPriceFinderBBDataLoaderFn } from './index';

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
  featureToggles: {},
  isInnBusinessAppPage: false,
  res: {
    getHeader: jest.fn(),
    setHeader: jest.fn(),
  } as any,
  req: {
    url: undefined,
    headers: {},
    socket: { encrypted: false },
  } as any,
};

describe('createPriceFinderPiDataLoader', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should create data loader with expected object structure', async () => {
    const dataLoaderPI = await createPriceFinderBBDataLoaderFn({
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

    const dataLoaderPI = await createPriceFinderBBDataLoaderFn({
      ...germanData,
      queryClient,
    });

    expect(dataLoaderPI).toEqual({
      dehydratedState: { mutations: [], queries: [] },
      staticData: mockStaticContentData,
    });
  });
});
