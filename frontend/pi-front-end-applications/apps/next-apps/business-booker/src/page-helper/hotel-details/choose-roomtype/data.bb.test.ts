import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import utils from '@whitbread-eos/utils';

import { createChooseRoomTypeBbDataLoaderFn } from './';

const mockStaticHotelInformation = {
  hotelInformationBySlug: {
    name: 'hub London Kings Cross',
    brand: 'HUB',
    hotelId: 'LONKIN',
  },
};
const mockToken = 'mock-id-token';
jest.mock('cookies', () => {
  return function () {
    return {
      get: (key: string) => {
        if (key === 'id_token_cookie') return mockToken;
        if (key === 'WB-SESSION-ID') return 'mock-session-id';
        return undefined;
      },
    };
  };
});

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  mutations: undefined,
  queries: undefined,
}));

const queryClient = new ReactQuery.QueryClient();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  decodeIdToken: () => jest.fn(),
  QueriesLogger: jest.fn().mockImplementation(() => ({
    fetchQuery: jest.fn().mockImplementation((queryKey, queryFn) => {
      queryFn();
      if (queryKey[0] === 'staticHotelInformation') {
        return Promise.resolve(mockStaticHotelInformation);
      }
    }),
    prefetchQuery: jest.fn().mockImplementation((queryKey, queryFn) => {
      queryFn();
      switch (queryKey[0]) {
        case 'hotelAvailability':
          return Promise.resolve({
            hotelAvailability: {
              hotelId: 'LONKIN',
              startDate: '2023-02-22',
              endDate: '2023-02-22',
              available: true,
              roomRates: [],
            },
          });
        case 'userDetails':
          return Promise.resolve({
            contactDetail: {
              title: 'Mr',
              firstName: 'firstname',
              lastName: 'lastname',
              email: 'email',
              mobile: 'mobile',
              address: {
                companyName: 'whitbread',
                countryCode: 'GB',
                line1: 'Street',
                line2: 'Street 2',
                line3: 'Street 3',
                line4: 'Street 4',
                postCode: '123',
                type: '',
              },
            },
          });
        default:
          return Promise.resolve({});
      }
    }),
    logQueries: jest.fn(),
  })),
  graphQLRequest: jest.fn(),
  logger: {
    info: jest.fn(),
  },
  axiosRequest: jest.fn().mockReturnValue(Promise.resolve({ data: {} })),
  ID_TOKEN_COOKIE: 'id_token_cookie',
  WB_SESSION_ID: 'WB-SESSION-ID',
}));

const mockedData = {
  language: 'en',
  country: 'GB',
  req: {
    url: '/_next/data/development/gb/hotels/england/greater-london/london/hub-london-kings-cross.html.json?searchLocation=London&ARRdd=25&ARRmm=12&ARRyyyy=2022&NIGHTS=4&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB&BRAND=PI&slug=england&slug=greater-london&slug=london&slug=hub-london-kings-cross.html',
    headers: {
      host: '',
    },
  },
  logger: {
    info: jest.fn(),
  },
  params: {
    slug: '/hotels/england/greater-london/london/hub-london-kings-cross.html',
  },
  featureToggles: {
    release_pi_bb_account_serv_2_serv: true,
  },
};

const expectedResult = {
  dehydratedState: {
    mutations: [],
    queries: [],
  },
  innBusiness: undefined,
};

describe('createChooseRoomTypeBbDataLoaderFn', () => {
  it('should render data loader with no idTokenCookie', async () => {
    const dataLoaderBb = await createChooseRoomTypeBbDataLoaderFn({
      ...mockedData,
      queryClient,
    } as any);
    expect(dataLoaderBb).toStrictEqual(expectedResult);
  });

  it('should render data loader with expected object with featureToggle on', async () => {
    process.env.NEXT_PUBLIC_ACCOUNT_SERVICE = 'http://hotel-account-service-opera.opera-be';
    jest.spyOn(utils, 'decodeIdToken').mockReturnValue({
      email: 'test@test.com',
      name: 'Test User',
      exp: null,
      profile: { employeeId: 'test-employee-id' },
    });

    const dataLoaderBb = await createChooseRoomTypeBbDataLoaderFn({
      ...mockedData,
      queryClient,
    } as any);

    expect(utils.axiosRequest).toHaveBeenCalledWith({
      method: 'GET',
      url: 'http://hotel-account-service-opera.opera-be/customers/hotels/test@test.com?business=true',
      headers: {
        Authorization: `Bearer ${mockToken}`,
        'WB-SESSION-ID': 'mock-session-id',
      },
    });
    expect(dataLoaderBb).toStrictEqual(expectedResult);
  });

  it('should render data loader with expected object with featureToggle off', async () => {
    process.env.NEXT_PUBLIC_REST_API = 'https://restapi.dit.premierinn.digital';
    const tmpMockData = { ...mockedData };
    tmpMockData.featureToggles.release_pi_bb_account_serv_2_serv = false;

    const dataLoaderBb = await createChooseRoomTypeBbDataLoaderFn({
      ...tmpMockData,
      queryClient,
    } as any);

    expect(utils.axiosRequest).toHaveBeenCalledWith({
      method: 'GET',
      url: `https://restapi.dit.premierinn.digital/customers/hotels/test@test.com?business=true`,
      headers: {
        Authorization: `Bearer ${mockToken}`,
        'WB-SESSION-ID': 'mock-session-id',
      },
    });
    expect(dataLoaderBb).toStrictEqual(expectedResult);
  });
});
