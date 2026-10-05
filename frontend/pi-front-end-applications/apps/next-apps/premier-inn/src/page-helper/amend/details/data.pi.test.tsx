// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore-file
import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';

import createAmendPiDataLoaderFn from './data.pi';

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
      if (queryKey[0] === 'GetStaticContent') {
        return Promise.resolve({});
      }
      if (queryKey[0] === 'seoInformation') {
        return Promise.resolve({});
      }
      if (queryKey[0] === 'getStayRules') {
        return Promise.resolve({});
      }
      if (queryKey[0] === 'getEmployeeStayRules') {
        return Promise.resolve({});
      }
      if (queryKey[0] === 'getRoomOccupancyLimitations') {
        return Promise.resolve({});
      }
      if (queryKey[0] === 'getBookingConfirmationAmend') {
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
  staticData: undefined,
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
  staticData: undefined,
  tempBookingReference: null,
  status: null,
};

jest.mock('cookies', () => {
  return jest.fn().mockImplementation(() => ({
    get: () => null,
    set: () => null,
  }));
});

describe('createAmendPiDataLoaderFn', () => {
  it('pi - amend details - should render data loader with expected object without basketReference', async () => {
    const dataLoaderPi = await createAmendPiDataLoaderFn({
      ...mockedData,
      queryClient,
    });
    expect(dataLoaderPi).toEqual(expectedResult);
  });

  it('pi - amend details - should render data loader with expected object having basketReference', async () => {
    const mockedDataWithBookingReference = {
      ...mockedData,
      query: { ...mockedData.query, bookingReference: 'MAH8513478' },
    };
    const dataLoaderPi = await createAmendPiDataLoaderFn({
      ...mockedDataWithBookingReference,
      queryClient,
    });
    expect(dataLoaderPi).toEqual(expectedResultWithConfirmationInput);
  });
});
