// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore-file
import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';

import createBookingConfirmationBbDataLoaderFn from './data.bb';

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
        return Promise.resolve({
          headerInformation: {},
          footer: {},
          labels: {},
        });
      }
    }),
    logQueries: jest.fn(),
  })),
  getFindBookingToken: () => {
    return { basketReference: 'MAH8513478' };
  },
  formatFindBookingToken: () => 'token',
  graphQLRequest: jest.fn(),
  getGQLClient: jest.fn(),
  getDefaultSessionTracing: jest.fn(),
  getAvailabilityParamsFromUrl: jest.fn(),
  decodeIdToken: jest.fn(),
  axiosRequest: jest.fn(),
  getLoggedInUserInfo: jest.fn(),
  logger: {
    info: jest.fn(),
  },
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
  },
  language: 'en',
  country: 'GB',
  res: {},
  req: { url: undefined, headers: { host: '' } },
  logger: {
    info: jest.fn(),
  },
  featureToggles: {
    FT_IB_ENABLED: false,
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
  staticData: {
    footer: {},
    headerInformation: {},
    labels: {},
  },
  bookingSpinnerConfig: [],
  email: '',
  tempBookingReference: null,
  innBusiness: undefined,
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
  staticData: {
    footer: {},
    headerInformation: {},
    labels: {},
  },
  tempBookingReference: null,
  bookingSpinnerConfig: undefined,
  email: 'catalin.iosif@mailinator.com',
  innBusiness: undefined,
};

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  mutations: undefined,
  queries: undefined,
}));

jest.mock('cookies', () => {
  return jest.fn().mockImplementation(() => ({
    get: () => null,
    set: () => null,
  }));
});

describe('createBookingConfirmationBbDataLoaderFn', () => {
  it('bb - booking confirmation - should render data loader with expected object without basketReference', async () => {
    const dataLoaderPi = await createBookingConfirmationBbDataLoaderFn({
      ...mockedData,
      queryClient,
    });
    expect(dataLoaderPi).toEqual(expectedResult);
  });

  it('bb - booking confirmation - should render data loader with expected object having basketReference', async () => {
    const mockedDataWithBookingReference = {
      ...mockedData,
      query: { ...mockedData.query, bookingReference: 'MAH8513478' },
    };
    const dataLoaderPi = await createBookingConfirmationBbDataLoaderFn({
      ...mockedDataWithBookingReference,
      queryClient,
    });
    expect(dataLoaderPi).toEqual(expectedResultWithConfirmationInput);
  });
});
