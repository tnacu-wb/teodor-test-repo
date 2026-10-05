// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore-file
import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { axiosRequest } from '@whitbread-eos/utils';

import createChooseBathroomDataLoaderFn from './data.pi';

const mockStaticHotelInformation = {
  hotelInformationBySlug: {
    name: 'hub London Kings Cross',
    brand: 'HUB',
    hotelId: 'LONKIN',
  },
};

jest.mock('../../../lib/getAuth0Token', () => ({
  getAuth0TokenAndEmail: jest.fn().mockResolvedValue({ accessToken: null, email: null }),
}));

const mockToken =
  'eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6InAzblU5b3M0RTBubGRMVF9ROHBnbSJ9.eyJodHRwczovL2NjdWkub3BlcmEud2hpdGJyZWFkLmRpZ2l0YWwvcm9sZSI6W10sIndiX2FjY291bnRfbG9jYWxlIjoiZW4iLCJodHRwczovL3ByZW1pZXJpbm4uY29tL2NvbXBhbnlBY2NvdW50SWQiOiJDT01QXzc4NDYwNjNlLWFjZDEtNDkzMS04YzA3LTFjMzNkZGMyYTQyZiIsImh0dHBzOi8vcHJlbWllcmlubi5jb20vZW1wbG95ZWVBY2NvdW50SWQiOiJFTVBMX2I0NWZkNzU1LWFhY2UtNGU5OS05MmIxLWRmN2NkZDUwOWU1MiIsImh0dHBzOi8vcHJlbWllcmlubi5jb20vZ2xvYmFsQ29tcGFueUlkIjoxMzYxLCJodHRwczovL3ByZW1pZXJpbm4uY29tL2VtYWlsIjoidHJhdmVsaW5nLmJnbEBtYWlsaW5hdG9yLmNvbSIsImh0dHBzOi8vcHJlbWllcmlubi5jb20vb3BlcmFDb21wYW55SWQiOiIyNTY5NjE2Iiwibmlja25hbWUiOiJ0cmF2ZWxpbmcuYmdsIiwicHJvZmlsZSI6eyJhY2Nlc3NMZXZlbCI6IlNVUEVSIiwiY29tcGFueUlkIjoiMzUwODYiLCJlbXBsb3llZUlkIjoiMSIsImlzQnVzaW5lc3MiOnRydWUsInNlc3Npb25JZCI6IjhSdHIxUktlaEFPb21rU2IifSwibmFtZSI6InRyYXZlbGluZy5iZ2xAbWFpbGluYXRvci5jb20iLCJwaWN0dXJlIjoiaHR0cHM6Ly9zLmdyYXZhdGFyLmNvbS9hdmF0YXIvYzYyNjU3YzE4NmFiNWIzMjNjOWFhZWJkYzJiZjExY2M_cz00ODAmcj1wZyZkPWh0dHBzJTNBJTJGJTJGY2RuLmF1dGgwLmNvbSUyRmF2YXRhcnMlMkZ0ci5wbmciLCJ1cGRhdGVkX2F0IjoiMjAyMy0wMy0wN1QxNDo0MTo0Ni4wMDFaIiwiaXNzIjoiaHR0cHM6Ly9hdXRoMC5zYW5kYm94LndoaXRicmVhZC5kaWdpdGFsLyIsImF1ZCI6IjhLT0NKa3o3MXBXRFlhamFNQUxhZWJKdVczQ0Nxb3ZzIiwiaWF0IjoxNjc4MjAwMTA3LCJleHAiOjE2NzgyMDE2MDcsInN1YiI6ImF1dGgwfDYzZWY0MzlkZDc0ZTZmOTZkYjAxZmEzYyIsImF0X2hhc2giOiJJalRkVEJhd2lHRmItRXVIUFNZVk93Iiwibm9uY2UiOiJaSGd3Sms2NVhkZ0g4QUZNV2pFeEpLZFNoZlRYMU5UeSJ9.E11UiJpwGnx5aj4WQvfdsjj5h2_h9MS-kiXMffYaPqk5x9QCegySS6kK0S_Z7rOKJG2QHtKH-wYyIyz-5moDA2u1iT4PLHtdwD4XoXtB-H8aKfvlpuMlCgeH9yoFrlk9NOJFcYiN8oNjL8hUymX4JOvZHOxwMbyJ-HNOcPtuWFd-HZqTrWlNXBu5XH459uDOhZGs1Ll425gbS8tP-Wa7F6liJG7046y4FVELwXZyoksKWFxmCxHrW8gEw3BCh4Ht4F_j2ORPbs0idt2VuNhp6NVuysCh0eks7CeyLLW951KCp40OW3P5P3KoK89T9ty0zpHXHlvCXKDDCmil962d7g';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
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
  axiosRequest: jest.fn().mockReturnValue(Promise.resolve({ data: {} })),
  graphQLRequest: jest.fn(),
  getGQLClient: jest.fn(),
  logger: {
    info: jest.fn(),
  },
  ID_TOKEN_COOKIE: 'id_token_cookie',
  WB_SESSION_ID: 'WB-SESSION-ID',
}));

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  mutations: undefined,
  queries: undefined,
}));

const queryClient = new ReactQuery.QueryClient();

jest.mock('cookies', () => {
  return function () {
    return {
      get: (key: string) => {
        if (key === 'id_token_cookie') return mockToken;
        if (key === 'WB-SESSION-ID') return 'mock-session-id';
        return key;
      },
    };
  };
});

const mockDecodeIdToken = jest.fn();

const mockedData = {
  language: 'en',
  country: 'GB',
  req: {
    url: '/_next/data/development/gb/hotels/england/greater-london/london/hub-london-kings-cross.html.json?searchLocation=London&ARRdd=25&ARRmm=12&ARRyyyy=2022&NIGHTS=4&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB&BRAND=PI&slug=england&slug=greater-london&slug=london&slug=hub-london-kings-cross.html',
  },
  logger: {
    info: jest.fn(),
  },
  params: {
    slug: '/hotels/england/greater-london/london/hub-london-kings-cross.html',
  },
  featureToggles: {},
};

const expectedResult = {
  dehydratedState: {
    mutations: [],
    queries: [],
  },
};

describe('createChooseBathroomDataLoaderFn', () => {
  afterEach(() => {
    mockDecodeIdToken.mockReset();
  });
  it('should render data loader with expected object', async () => {
    process.env.NEXT_PUBLIC_ACCOUNT_SERVICE = 'http://hotel-account-service-opera.opera-be';
    const dataLoaderPI = await createChooseBathroomDataLoaderFn({
      ...mockedData,
      queryClient,
    } as any);

    expect(axiosRequest).toHaveBeenCalledWith({
      method: 'GET',
      url: `http://hotel-account-service-opera.opera-be/customers/hotels/traveling.bgl@mailinator.com?business=false`,
      headers: {
        Authorization: `Bearer ${mockToken}`,
        'WB-SESSION-ID': 'mock-session-id',
      },
    });
    expect(dataLoaderPI).toStrictEqual(expectedResult);
  });
});
