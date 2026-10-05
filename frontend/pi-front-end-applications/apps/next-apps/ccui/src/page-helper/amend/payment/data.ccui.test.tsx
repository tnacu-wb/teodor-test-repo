// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore-file
import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';

import createAmendPaymentCCUIDataLoaderFn from './data.ccui';

jest.mock('@tanstack/react-query', () => {
  const original: typeof ReactQuery = jest.requireActual('@tanstack/react-query');
  return {
    ...original,
    fetchQuery: async (queryKey: any, queryFn: any) => {
      const key = queryKey[0];
      queryFn();
      if (key === 'GetStaticContent') {
        return Promise.resolve({});
      }
    },
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    prefetchQuery: async (queryKey: any, queryFn: any) => {
      const key = queryKey[0];
      queryFn();
      if (key === 'GetHotelInformation') {
        return Promise.resolve({});
      }
    },
  };
});

jest.mock('cookies', () => {
  return jest.fn().mockImplementation(() => ({
    get: () => null,
    set: () => null,
  }));
});

jest.mock('@whitbread-eos/utils', () => ({
  QueriesLogger: jest.fn().mockImplementation(() => ({
    prefetchQuery: jest.fn().mockImplementation(async () => Promise.resolve({})),
    logQueries: jest.fn(),
  })),
  useMutationRequest: () => ({
    mutation: {
      mutate: jest.fn(),
    },
    isSuccess: false,
  }),
  graphQLRequest: jest.fn(),
  getGQLClient: jest.fn(),
  getFindBookingToken: () => ({ basketReference: 'MAH8513478' }),
  getDefaultSessionTracing: jest.fn(),
  logger: {
    info: jest.fn(),
  },
}));

const queryClient = new ReactQuery.QueryClient();

const mockedData = {
  session: {
    tokenSet: {
      accessToken: 'mockAccessToken',
    },
    user: {},
  },

  resolvedUrl: '/',
  proxyOptions: {},
  query: {
    reservationId: '12',
    BRAND: 'PI',
    bookingReference: 'bookingReference',
  },
  language: 'en',
  country: 'GB',
  res: undefined,
  req: { url: undefined },
  logger: {
    info: jest.fn(),
  },
};

const expectedResult = {
  dehydratedState: {
    mutations: [],
    queries: [],
  },
  user: {},
};

describe('createAmendPaymentCCUIDataLoaderFn', () => {
  it('should create data loader with expected object', async () => {
    const dataLoaderCcui = await createAmendPaymentCCUIDataLoaderFn({
      ...mockedData,
      queryClient,
    });
    expect(dataLoaderCcui).toEqual(expectedResult);
  });
});
