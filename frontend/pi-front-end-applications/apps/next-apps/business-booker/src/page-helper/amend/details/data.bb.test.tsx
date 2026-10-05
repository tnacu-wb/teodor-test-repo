// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore-file
import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';

import createAmendBbDataLoaderFn from './data.bb';

const mockStaticHotelInformation = {
  hotelInformationBySlug: {
    name: 'hub London Kings Cross',
    brand: 'HUB',
    hotelId: 'LONKIN',
  },
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

jest.mock('@whitbread-eos/utils', () => ({
  cn: (...classes: any[]) => classes.filter(Boolean).join(' '),
  QueriesLogger: jest.fn().mockImplementation(() => ({
    fetchQuery: jest.fn().mockImplementation((queryKey, queryFn) => {
      queryFn();
      if (queryKey[0] === 'getBookingConfirmationAmend') {
        return Promise.resolve({});
      } else if (queryKey[0] === 'staticHotelInformation') {
        return Promise.resolve(mockStaticHotelInformation);
      }
    }),
    prefetchQuery: jest.fn().mockImplementation((queryKey, queryFn) => {
      queryFn();
      if (queryKey[0] === 'GetStaticContent') {
        return Promise.resolve({});
      }
    }),
    logQueries: jest.fn(),
  })),
  graphQLRequest: jest.fn(),
  getGQLClient: jest.fn(),
  getFindBookingToken: jest
    .fn()
    .mockReturnValue({ basketReference: '', token: '', bookingReference: '' }),
  getDefaultSessionTracing: jest.fn(),
  getAvailabilityParamsFromUrl: jest.fn(),
  decodeIdToken: jest.fn(),
  axiosRequest: jest.fn(),
  getLoggedInUserInfo: jest.fn(),
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
  res: undefined,
  req: { url: undefined } as any,
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
  pcksQueryInput: null,
  tempBookingReference: null,
  status: null,
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
  pcksQueryInput: null,
  tempBookingReference: null,
  status: null,
};

jest.mock('cookies', () => {
  return jest.fn().mockImplementation(() => ({
    get: () => null,
    set: () => null,
  }));
});

describe('createAmendBbDataLoaderFn', () => {
  it('bb - amend details - should render data loader with expected object without basketReference', async () => {
    const dataLoaderBb = await createAmendBbDataLoaderFn({
      ...mockedData,
      queryClient,
    } as any);
    expect(dataLoaderBb).toEqual(expectedResult);
  });

  it('bb - amend details - should render data loader with expected object having basketReference', async () => {
    const mockedDataWithBookingReference = {
      ...mockedData,
      query: { ...mockedData.query, bookingReference: 'MAH8513478', basketReference: 'MAH8513478' },
    };
    const dataLoaderBb = await createAmendBbDataLoaderFn({
      ...mockedDataWithBookingReference,
      queryClient,
    } as any);
    expect(dataLoaderBb).toEqual(expectedResultWithConfirmationInput);
  });
});
