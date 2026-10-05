// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore-file
import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';

import createDashboardPiDataLoaderFn from './data.pi';

const bookingConfirmationAuthenticatedMock = {
  bookingConfirmationAuthenticated: {
    reservationByIdList: [
      {
        billing: {
          address: {
            addressLine1: 'Dental Surgery',
            postalCode: 'GU16 7HF',
          },
          title: 'Mr',
          telephone: '+440123123123',
          firstName: 'Catalin',
          lastName: 'Iosif',
          email: 'mailto:catalin.iosif@mailinator.com',
        },
        reservationGuestList: [
          {
            givenName: 'Catalin',
            surName: 'Iosif',
          },
        ],
        gdsReferenceNumber: null,
        roomStay: {
          checkInTime: '15:00',
          checkOutTime: '12:00',
          ratePlanCode: 'FLEXRATE',
          arrivalDate: '2023-06-15',
          departureDate: '2023-06-17',
          bookingChannel: 'PI.com',
          roomPrice: 1998,
          cot: false,
          adultsNumber: 1,
          roomExtraInfo: {
            roomName: 'Premier Plus Room',
          },
          childrenNumber: 0,
        },
        paymentCard: {
          cardNumberMasked: 'XXXXXXXXXXXX0017',
        },
        reservationOverrideReasons: {
          reasonCode: 'DTH',
          callerName: 'test',
          managerName: '',
          reasonName: 'Death',
        },
        reservationOverridden: true,
        guaranteeCode: 'CC',
        reservationStatus: 'Reserved',
        additionalGuestInfo: {
          purposeOfStay: 'LEI',
        },
      },
    ],
    balanceOutstanding: 1998,
    currencyCode: 'GBP',
    newTotal: 1998,
    policyCode: 'D1A',
    previousTotal: 0,
    totalCost: 1998,
    hotelId: 'MANOLD',
    hotelName: 'Manchester Old Trafford',
    bookingFlowId: 'booking-a1',
    rateMessage: '<p>Amend or cancel up to 1pm on arrival day</p>\n',
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

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  mutations: [],
  queries: [],
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  QueriesLogger: jest.fn().mockImplementation(() => ({
    fetchQuery: jest.fn().mockImplementation((queryKey, queryFn) => {
      const key = queryKey[0];
      queryFn();
      if (key === 'getBookingConfirmation') {
        return Promise.resolve({ bookingConfirmation: { hotelId: '123' } });
      }
      if (key === 'GetStaticContent') {
        return Promise.resolve({});
      }
      if (key === 'getBookingConfirmationAuthenticated') {
        return Promise.resolve(bookingConfirmationAuthenticatedMock);
      }
    }),
    prefetchQuery: jest.fn().mockImplementation((queryKey, queryFn) => {
      const key = queryKey[0];
      queryFn();
      switch (key) {
        case 'GetHotelInformation':
          return Promise.resolve({});
        default:
          return Promise.resolve({});
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
  logger: {
    info: jest.fn(),
  },
}));

const queryClient = new ReactQuery.QueryClient();

const mockedData = {
  session: {
    accessToken: 'asasasGderg12312',
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
  req: {},
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
  hiQueryInput: null,
  pcksQueryInput: null,
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
  hiQueryInput: { country: 'GB', hotelId: 'MANOLD', language: 'en' },
  pcksQueryInput: {
    adultsNumber: 1,
    basketReferenceId: 'MAH8513478',
    bookingFlowId: 'booking-a1',
    channel: 'BB',
    childrenNumber: 0,
    country: 'GB',
    endDate: '2023-06-17',
    hotelId: 'MANOLD',
    language: 'en',
    nightsNumber: 2,
    startDate: '2023-06-15',
  },
};

jest.mock('cookies', () => {
  return jest.fn().mockImplementation(() => ({
    get: () => null,
    set: () => null,
  }));
});

describe('createDashboardPiDataLoaderFn', () => {
  it('pi - dashboard - should render data loader with expected object without basketReference', async () => {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    const dataLoaderPi = await createDashboardPiDataLoaderFn({
      ...mockedData,
      queryClient,
    });
    expect(dataLoaderPi).toEqual(expectedResult);
  });

  it('pi - dashboard - should render data loader with expected object having basketReference', async () => {
    const mockedDataWithBookingReference = {
      ...mockedData,
      query: { ...mockedData.query, bookingReference: 'MAH8513478' },
      authToken: mockedData.session.accessToken,
    };
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    const dataLoaderPi = await createDashboardPiDataLoaderFn({
      ...mockedDataWithBookingReference,
      queryClient,
    });
    expect(dataLoaderPi).toEqual(expectedResultWithConfirmationInput);
  });
});
