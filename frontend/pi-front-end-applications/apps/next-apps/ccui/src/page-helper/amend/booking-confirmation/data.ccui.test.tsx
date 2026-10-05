// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore-file
import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';

import { mockBookingConfirmationAmend } from '../test-utils';
import createBookingConfirmationCCUIDataLoaderFn from './data.ccui';

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
      if (queryKey[0] === 'GetHotelInformation') {
        return Promise.resolve({});
      }
    }),
    fetchQuery: jest.fn().mockImplementation((queryKey, queryFn) => {
      queryFn();
      if (queryKey[0] === 'getBookingConfirmation') {
        return Promise.resolve(mockBookingConfirmationAmend.data);
      }
      if (queryKey[0] === 'GetStaticContent') {
        return Promise.resolve({});
      }
    }),
    logQueries: jest.fn(),
  })),
  graphQLRequest: jest.fn(),
  getGQLClient: jest.fn(),
  getFindBookingToken: () => {
    return { basketReference: 'MAH8513478' };
  },
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
  query: {
    reservationId: '12',
    BRAND: 'PI',
    status: '',
  },
  language: 'en',
  country: 'GB',
  res: undefined,
  req: { url: undefined },
  proxyOptions: {},
};

const expectedResult = {
  dehydratedState: {
    mutations: [],
    queries: [],
  },
  confirmationInput: null,
  amendBookingStatus: null,
  hiQueryInput: null,
  tempBookingReference: null,
  pcksQueryInput: null,
  staticData: {},
  user: {},
};

const expectedResultWithConfirmationInput = {
  dehydratedState: {
    mutations: [],
    queries: [],
  },
  confirmationInput: {
    basketReference: 'MAH8513478',
    bookingReference: 'MAH8513478',
    country: 'GB',
    language: 'en',
  },
  amendBookingStatus: null,
  hiQueryInput: { country: 'GB', hotelId: 'MANOLD', language: 'en' },
  pcksQueryInput: null,
  staticData: {},
  tempBookingReference: null,
  user: {},
};

const expectedResultWithStatus = {
  dehydratedState: {
    mutations: [],
    queries: [],
  },
  confirmationInput: null,
  amendBookingStatus: '123',
  hiQueryInput: null,
  pcksQueryInput: null,
  tempBookingReference: null,
  staticData: {},
  user: {},
};

jest.mock('cookies', () => {
  return jest.fn().mockImplementation(() => ({
    get: () => null,
    set: () => null,
  }));
});

describe('createBookingConfirmationCCUIDataLoaderFn', () => {
  it('ccui - booking confirmation - should render data loader with expected object without basketReference', async () => {
    const dataLoaderCCUI = await createBookingConfirmationCCUIDataLoaderFn({
      ...mockedData,
      queryClient,
    });
    expect(dataLoaderCCUI).toEqual(expectedResult);
  });

  it('ccui - booking confirmation - should render data loader with expected object having basketReference', async () => {
    const mockedDataWithBookingReference = {
      ...mockedData,
      query: { ...mockedData.query, bookingReference: 'MAH8513478' },
    };
    const dataLoaderCCUI = await createBookingConfirmationCCUIDataLoaderFn({
      ...mockedDataWithBookingReference,
      queryClient,
    });
    expect(dataLoaderCCUI).toEqual(expectedResultWithConfirmationInput);
  });

  it('ccui - booking confirmation - should render data loader with expected object without query status', async () => {
    mockedData.query.status = '123';
    const dataLoaderCCUI = await createBookingConfirmationCCUIDataLoaderFn({
      ...mockedData,
      queryClient,
    });
    expect(dataLoaderCCUI).toEqual(expectedResultWithStatus);
  });
});
