// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore-file
import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';

import createBookingConfirmationPiDataLoaderFn from './data.pi';

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

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  mutations: undefined,
  queries: undefined,
}));

jest.mock('@whitbread-eos/utils', () => ({
  cn: (...classes: any[]) => classes.filter(Boolean).join(' '),
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
        // eslint-disable-next-line @typescript-eslint/no-require-imports
        return Promise.resolve(require('../test-utils').mockBookingConfirmationAmend.data);
      }
      if (queryKey[0] === 'GetStaticContent') {
        return Promise.resolve({});
      }
    }),
    logQueries: jest.fn(),
  })),
  getFindBookingToken: () => {
    return { basketReference: 'MAH8513478' };
  },
  graphQLRequest: jest.fn(),
  getGQLClient: jest.fn(),
}));

const queryClient = new ReactQuery.QueryClient();

const mockedData = {
  session: {
    accessToken: 'mockAccessToken',
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
  res: {},
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
  confirmationInput: null,
  amendBookingStatus: null,
  hiQueryInput: null,
  pcksQueryInput: null,
  bookingSpinnerConfig: [],
  email: '',
  tempBookingReference: null,
};

const expectedResultWithConfirmationInput = {
  dehydratedState: {
    mutations: [],
    queries: [],
  },
  amendBookingStatus: null,
  hiQueryInput: { country: 'GB', hotelId: 'MANOLD', language: 'en' },
  pcksQueryInput: null,
  tempBookingReference: null,
  bookingSpinnerConfig: undefined,
  confirmationInput: {
    basketReference: 'MAH8513478',
    bookingReference: 'MAH8513478',
    country: 'GB',
    language: 'en',
  },
  email: 'catalin.iosif@mailinator.com',
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
  bookingSpinnerConfig: [],
  email: '',
  tempBookingReference: null,
};

jest.mock('cookies', () => {
  return jest.fn().mockImplementation(() => ({
    get: () => null,
    set: () => null,
  }));
});

describe('createBookingConfirmationPiDataLoaderFn', () => {
  it('pi - booking confirmation - should render data loader with expected object without basketReference', async () => {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    const dataLoaderPi = await createBookingConfirmationPiDataLoaderFn({
      ...mockedData,
      queryClient,
    });
    expect(dataLoaderPi).toEqual(expectedResult);
  });

  it('pi - booking confirmation - should render data loader with expected object having basketReference from fallback', async () => {
    const mockedDataWithBookingReference = {
      ...mockedData,
      query: { ...mockedData.query, bookingReference: 'MAH8513478' },
    };
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    const dataLoaderPi = await createBookingConfirmationPiDataLoaderFn({
      ...mockedDataWithBookingReference,
      queryClient,
    });
    expect(dataLoaderPi).toEqual(expectedResultWithConfirmationInput);
  });

  it('pi - booking confirmation - should render data loader with expected object without basketReference', async () => {
    mockedData.query.status = '123';
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    const dataLoaderPi = await createBookingConfirmationPiDataLoaderFn({
      ...mockedData,
      queryClient,
    });
    expect(dataLoaderPi).toEqual(expectedResultWithStatus);
  });
});
