// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore-file
import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';

import createAmendCCUIDataLoaderFn from './data.ccui';

const mockBookingToken = {
  basketReference: 'basketReference',
};

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    router: {
      language: 'gb',
      country: 'gb',
    },
    query: {
      bookingReference: 'MAH8513478',
    },
  }),
}));

jest.mock('cookies', () => {
  return jest.fn().mockImplementation(() => ({
    get: () => null,
    set: () => null,
  }));
});

jest.mock('@whitbread-eos/utils', () => ({
  QueriesLogger: jest.fn().mockImplementation(() => ({
    prefetchQuery: jest.fn().mockImplementation((queryKey, queryFn) => {
      queryFn();
      if (queryKey[0] === 'GetStaticContent') {
        return Promise.resolve({});
      }
      if (queryKey[0] === 'getStayRules') {
        return Promise.resolve({});
      }
      if (queryKey[0] === 'getRoomOccupancyLimitations') {
        return Promise.resolve({});
      }
    }),
    logQueries: jest.fn(),
  })),
  graphQLRequest: jest.fn(),
  getGQLClient: jest.fn(),
  getFindBookingToken: () => mockBookingToken,
  getDefaultSessionTracing: jest.fn(),
  logger: {
    info: jest.fn(),
  },
}));

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  mutations: undefined,
  queries: undefined,
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
  confirmationInput: {
    basketReference: 'basketReference',
    bookingReference: 'bookingReference',
    country: 'GB',
    language: 'en',
  },
  user: {},
};

const expectedResultWithoutbookingReference = {
  dehydratedState: {
    mutations: [],
    queries: [],
  },
  confirmationInput: null,
  user: {},
};

describe('createAmendCCUIDataLoaderFn', () => {
  it('should render data loader with expected object', async () => {
    const dataLoaderCCUI = await createAmendCCUIDataLoaderFn({
      ...mockedData,
      queryClient,
    });
    expect(dataLoaderCCUI).toEqual(expectedResult);
  });

  it('should render data loader with expected object without bookingReference', async () => {
    mockedData.query.bookingReference = undefined;
    const dataLoaderCCUI = await createAmendCCUIDataLoaderFn({
      ...mockedData,
      queryClient,
    });
    expect(dataLoaderCCUI).toEqual(expectedResultWithoutbookingReference);
  });
});
