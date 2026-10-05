import { QueryClient } from '@tanstack/react-query';

import { getRestaurantInitialContentDataFn } from './getRestaurantInitialContentData.data';

const queryClient = new QueryClient();
const mockedData = {
  queryClient,
  restaurantBrandNameForAemApi: 'restaurantBrandNameForAemApi',
  location: 'location',
  subLocation: 'subLocation',
  restaurantBrandName: 'restaurantBrandName',
};
jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  logger: {
    info: jest.fn(),
  },
  getDefaultSessionTracing: jest.fn(),
  instrumentQueryClient: () => ({
    fetchQuery: async (queryKey: any[]) => {
      const location = queryKey[2];

      if (location === 'junk-data') {
        return Promise.reject({
          response: { errors: [{ errorType: '404' }] },
        });
      } else if (location === 'junk-data-apollo') {
        return Promise.reject({
          response: {
            errors: [
              {
                message:
                  '{"message":"Resource Not Found","errors":[{"field":"path","message":"The requested resource is not found."}],"statusCode":404}',
              },
            ],
          },
        });
      } else {
        return Promise.resolve({});
      }
    },
    prefetchQuery: jest.fn(),
  }),
}));

jest.mock('cookies', () => {
  return jest.fn().mockImplementation(() => ({
    get: jest.fn(),
    set: jest.fn(),
  }));
});

jest.mock('@whitbread-eos/utils', () => ({
  QueriesLogger: jest.fn().mockImplementation(() => ({
    fetchQuery: async (queryKey: any[]) => {
      const location = queryKey[2];

      if (location === 'junk-data') {
        return Promise.reject({
          response: { errors: [{ errorType: '404' }] },
        });
      } else if (location === 'junk-data-apollo') {
        return Promise.reject({
          response: {
            errors: [
              {
                message:
                  '{"message":"Resource Not Found","errors":[{"field":"path","message":"The requested resource is not found."}],"statusCode":404}',
              },
            ],
          },
        });
      } else if (location === 'error-location') {
        return Promise.reject({
          response: {
            errors: [
              {
                message: 'Some other error',
              },
            ],
          },
        });
      } else if (queryKey[0] === 'getLocationDetails') {
        return Promise.resolve({
          locations: [{ id: 'location-123' }],
        });
      } else if (queryKey[0] === 'getOutletsDetails') {
        return Promise.resolve({
          outlets: {
            companies: [
              {
                sites: [{ id: 'site-456' }],
              },
            ],
          },
        });
      } else if (queryKey[0] === 'getOccasionsDetails') {
        return Promise.resolve({
          occasions: {
            occasions: [
              { name: 'test-occasion', id: 'occasion-789' },
              { name: 'other-occasion', id: 'occasion-999' },
            ],
          },
        });
      } else {
        return Promise.resolve({});
      }
    },
    prefetchQuery: jest.fn(),
  })),
  getDefaultSessionTracing: jest.fn(() => ({
    'WB-SESSION-ID': 'test-session-id',
  })),
  logger: {
    info: jest.fn(),
  },
}));

jest.mock('next/config', () => ({
  __esModule: true,
  default: jest.fn(() => ({
    publicRuntimeConfig: {
      NEXT_PUBLIC_OCCASION_NAME: 'test-occasion',
    },
  })),
}));

describe('getRestaurantInitialContentDataFn', () => {
  it('should return 404 for getBookPageData with 404 errorType', async () => {
    const result = await getRestaurantInitialContentDataFn({
      ...mockedData,
      query: { key: 'test' },
      req: {} as any,
      res: {} as any,
      resolvedUrl: 'test',
      location: 'junk-data',
    });

    expect(result).toEqual({ resourceNotFound: true });
  });

  it('should return 404 for getBookPageData with Apollo 404 error', async () => {
    const result = await getRestaurantInitialContentDataFn({
      ...mockedData,
      query: { key: 'test' },
      req: {} as any,
      res: {} as any,
      resolvedUrl: 'test',
      location: 'junk-data-apollo',
    });

    expect(result).toEqual({ resourceNotFound: true });
  });

  it('should handle non-404 errors for getBookPageData and continue fetching other data', async () => {
    const result = await getRestaurantInitialContentDataFn({
      ...mockedData,
      query: { key: 'test' },
      req: {} as any,
      res: {} as any,
      resolvedUrl: 'test',
      location: 'error-location',
    });

    // Even with error in getBookPageData, other data should still be fetched
    expect(result).toEqual({
      dehydratedState: {
        mutations: [],
        queries: [],
      },
      occasionId: 'occasion-789',
      siteId: 'site-456',
    });
  });

  it('should successfully fetch all data when valid location is provided', async () => {
    const result = await getRestaurantInitialContentDataFn({
      ...mockedData,
      query: { key: 'test' },
      req: {} as any,
      res: {} as any,
      resolvedUrl: 'test',
      location: 'valid-location',
      subLocation: 'valid-sublocation',
    });

    expect(result).toEqual({
      dehydratedState: {
        mutations: [],
        queries: [],
      },
      occasionId: 'occasion-789',
      siteId: 'site-456',
    });
  });

  it('should handle errors when fetching location details', async () => {
    const result = await getRestaurantInitialContentDataFn({
      ...mockedData,
      query: { key: 'test' },
      req: {} as any,
      res: {} as any,
      resolvedUrl: 'test',
      location: 'valid-location',
      subLocation: 'valid-sublocation',
    });

    expect(result).toHaveProperty('dehydratedState');
    expect(result).toHaveProperty('occasionId');
    expect(result).toHaveProperty('siteId');
  });

  it('should handle errors when fetching outlets details', async () => {
    const result = await getRestaurantInitialContentDataFn({
      ...mockedData,
      query: { key: 'test' },
      req: {} as any,
      res: {} as any,
      resolvedUrl: 'test',
      location: 'valid-location',
    });

    expect(result).toHaveProperty('dehydratedState');
  });

  it('should handle errors when fetching occasions details', async () => {
    const result = await getRestaurantInitialContentDataFn({
      ...mockedData,
      query: { key: 'test' },
      req: {} as any,
      res: {} as any,
      resolvedUrl: 'test',
      location: 'valid-location',
    });

    expect(result).toHaveProperty('occasionId');
  });

  it('should match occasion by name from config', async () => {
    const result = await getRestaurantInitialContentDataFn({
      ...mockedData,
      query: { key: 'test' },
      req: {} as any,
      res: {} as any,
      resolvedUrl: 'test',
      location: 'valid-location',
    });

    expect(result.occasionId).toBe('occasion-789');
  });
});
