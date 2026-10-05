/* eslint-disable @typescript-eslint/no-require-imports */

/* eslint-disable @typescript-eslint/no-explicit-any */
import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import {
  FT_PI_PROMO_CODE_LANDING_PAGE,
  FT_PI_PROMO_CODE_SITE_WIDE,
  FT_PI_NO_ROOM_TYPE_SEARCH,
  FT_PI_DISPLAY_SOFT_BUNDLES,
  FT_PI_CCUI_BB_PROMOTION_BOX_CODE_COOKIE,
  GET_PROMO_INFORMATION,
  FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY,
  HOTEL_AVAILABILITY_QUERY,
  PROMO_CODE_COOKIE,
} from '@whitbread-eos/api';

import createHDPPiDataLoaderFn from './data.pi';

const mockUseRouter = jest.fn().mockReturnValue({ query: { PROMOID: 'ST10R' } });

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('../../../lib/getAuth0Token', () => ({
  getAuth0TokenAndEmail: jest.fn().mockResolvedValue({ accessToken: null, email: null }),
}));

const mockStaticHotelInformation = {
  hotelInformationBySlug: {
    name: 'hub London Kings Cross',
    brand: 'HUB',
    hotelId: 'LONKIN',
  },
};

const mockAvailabilityParams = {
  arrival: '2022-12-25',
  departure: '2022-12-29',
  rooms: [
    {
      adultsNumber: 1,
      childrenNumber: 1,
      roomType: 'DB',
      cotRequired: false,
    },
  ],
  corpId: '15010601',
  PROMOID: 'ST10R',
  numberOfNights: 2,
};

const mockLoggedInUserInfo = {
  operaCompanyId: '2569616',
};

const mockToken =
  'eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6InAzblU5b3M0RTBubGRMVF9ROHBnbSJ9.eyJodHRwczovL2NjdWkub3BlcmEud2hpdGJyZWFkLmRpZ2l0YWwvcm9sZSI6W10sIndiX2FjY291bnRfbG9jYWxlIjoiZW4iLCJodHRwczovL3ByZW1pZXJpbm4uY29tL2NvbXBhbnlBY2NvdW50SWQiOiJDT01QXzc4NDYwNjNlLWFjZDEtNDkzMS04YzA3LTFjMzNkZGMyYTQyZiIsImh0dHBzOi8vcHJlbWllcmlubi5jb20vZW1wbG95ZWVBY2NvdW50SWQiOiJFTVBMX2I0NWZkNzU1LWFhY2UtNGU5OS05MmIxLWRmN2NkZDUwOWU1MiIsImh0dHBzOi8vcHJlbWllcmlubi5jb20vZ2xvYmFsQ29tcGFueUlkIjoxMzYxLCJodHRwczovL3ByZW1pZXJpbm4uY29tL2VtYWlsIjoidHJhdmVsaW5nLmJnbEBtYWlsaW5hdG9yLmNvbSIsImh0dHBzOi8vcHJlbWllcmlubi5jb20vb3BlcmFDb21wYW55SWQiOiIyNTY5NjE2Iiwibmlja25hbWUiOiJ0cmF2ZWxpbmcuYmdsIiwicHJvZmlsZSI6eyJhY2Nlc3NMZXZlbCI6IlNVUEVSIiwiY29tcGFueUlkIjoiMzUwODYiLCJlbXBsb3llZUlkIjoiMSIsImlzQnVzaW5lc3MiOnRydWUsInNlc3Npb25JZCI6IjhSdHIxUktlaEFPb21rU2IifSwibmFtZSI6InRyYXZlbGluZy5iZ2xAbWFpbGluYXRvci5jb20iLCJwaWN0dXJlIjoiaHR0cHM6Ly9zLmdyYXZhdGFyLmNvbS9hdmF0YXIvYzYyNjU3YzE4NmFiNWIzMjNjOWFhZWJkYzJiZjExY2M_cz00ODAmcj1wZyZkPWh0dHBzJTNBJTJGJTJGY2RuLmF1dGgwLmNvbSUyRmF2YXRhcnMlMkZ0ci5wbmciLCJ1cGRhdGVkX2F0IjoiMjAyMy0wMy0wN1QxNDo0MTo0Ni4wMDFaIiwiaXNzIjoiaHR0cHM6Ly9hdXRoMC5zYW5kYm94LndoaXRicmVhZC5kaWdpdGFsLyIsImF1ZCI6IjhLT0NKa3o3MXBXRFlhamFNQUxhZWJKdVczQ0Nxb3ZzIiwiaWF0IjoxNjc4MjAwMTA3LCJleHAiOjE2NzgyMDE2MDcsInN1YiI6ImF1dGgwfDYzZWY0MzlkZDc0ZTZmOTZkYjAxZmEzYyIsImF0X2hhc2giOiJJalRkVEJhd2lHRmItRXVIUFNZVk93Iiwibm9uY2UiOiJaSGd3Sms2NVhkZ0g4QUZNV2pFeEpLZFNoZlRYMU5UeSJ9.E11UiJpwGnx5aj4WQvfdsjj5h2_h9MS-kiXMffYaPqk5x9QCegySS6kK0S_Z7rOKJG2QHtKH-wYyIyz-5moDA2u1iT4PLHtdwD4XoXtB-H8aKfvlpuMlCgeH9yoFrlk9NOJFcYiN8oNjL8hUymX4JOvZHOxwMbyJ-HNOcPtuWFd-HZqTrWlNXBu5XH459uDOhZGs1Ll425gbS8tP-Wa7F6liJG7046y4FVELwXZyoksKWFxmCxHrW8gEw3BCh4Ht4F_j2ORPbs0idt2VuNhp6NVuysCh0eks7CeyLLW951KCp40OW3P5P3KoK89T9ty0zpHXHlvCXKDDCmil962d7g';

const mockDecodeIdToken = jest.fn();

const mockCookies = {
  bundles: 'class',
};

jest.mock('@whitbread-eos/utils', () => {
  const utils = jest.requireActual('@whitbread-eos/utils');

  return {
    ...utils,
    ...jest.requireActual('@whitbread-eos/utils'),
    axiosRequest: jest.fn().mockImplementation(() => ({})),
    QueriesLogger: jest.fn().mockImplementation(() => ({
      fetchQuery: jest.fn().mockImplementation((queryKey, queryFn) => {
        const key = queryKey[0];
        queryFn();
        if (key === 'staticHotelInformation') {
          return Promise.resolve(mockStaticHotelInformation);
        }
      }),
      prefetchQuery: jest.fn().mockImplementation((queryKey, queryFn) => {
        const key = queryKey[0];
        queryFn();
        if (key === 'hotelAvailability' || key === 'hotelAvailabilityDiscountRate') {
          return Promise.resolve({
            hotelAvailability: {
              hotelId: 'LONKIN',
              arrival: '2023-07-01',
              departure: '2023-07-03',
              rooms: [
                {
                  adultsNumber: 1,
                  childrenNumber: 0,
                  cotRequired: false,
                  roomType: 'DB',
                },
              ],
              brand: 'hub',
              country: 'gb',
              language: 'en',
              roomRates: [
                {
                  ratePlanCode: 'SEMIFLEX',
                  roomTypes: [
                    {
                      roomType: 'DB',
                      adults: 1,
                      children: 0,
                      cotRequested: false,
                      rooms: [
                        {
                          pmsRoomType: 'DOUBLE',
                          silentSubstitution: true,
                          cotAvailable: false,
                          roomClass: 'ST',
                          specialRequests: ['SING'],
                          roomPriceBreakdown: {
                            totalNetAmount: 53.9,
                            currencyCode: 'EUR',
                            packageCode: null,
                            packageAmount: null,
                            dailyPrices: [
                              {
                                date: '2024-11-30',
                                netPrice: 53.9,
                              },
                            ],
                          },
                        },
                      ],
                    },
                  ],
                },
                {
                  ratePlanCode: 'ADVANCE',
                  roomTypes: [
                    {
                      roomType: 'DB',
                      adults: 1,
                      children: 0,
                      cotRequested: false,
                      rooms: [
                        {
                          pmsRoomType: 'DOUBLE',
                          silentSubstitution: true,
                          cotAvailable: false,
                          roomClass: 'ST',
                          specialRequests: ['SING'],
                          roomPriceBreakdown: {
                            totalNetAmount: 44,
                            currencyCode: 'EUR',
                            packageCode: null,
                            packageAmount: null,
                            dailyPrices: [
                              {
                                date: '2024-11-30',
                                netPrice: 44,
                              },
                            ],
                          },
                        },
                      ],
                    },
                  ],
                },
                {
                  ratePlanCode: 'STANDARD',
                  roomTypes: [
                    {
                      roomType: 'DB',
                      adults: 1,
                      children: 0,
                      cotRequested: false,
                      rooms: [
                        {
                          pmsRoomType: 'DOUBLE',
                          silentSubstitution: true,
                          cotAvailable: false,
                          roomClass: 'ST',
                          specialRequests: ['SING'],
                          roomPriceBreakdown: {
                            totalNetAmount: 40,
                            currencyCode: 'EUR',
                            packageCode: null,
                            packageAmount: null,
                            dailyPrices: [
                              {
                                date: '2024-11-30',
                                netPrice: 40,
                              },
                            ],
                          },
                        },
                      ],
                    },
                  ],
                },
                {
                  ratePlanCode: 'FCDNLR30',
                  roomTypes: [
                    {
                      roomType: 'DB',
                      adults: 1,
                      children: 0,
                      cotRequested: false,
                      rooms: [
                        {
                          pmsRoomType: 'DOUBLE',
                          silentSubstitution: true,
                          cotAvailable: false,
                          roomClass: 'ST',
                          specialRequests: ['SING'],
                          roomPriceBreakdown: {
                            totalNetAmount: 38.5,
                            currencyCode: 'EUR',
                            packageCode: null,
                            packageAmount: null,
                            dailyPrices: [
                              {
                                date: '2024-11-30',
                                netPrice: 38.5,
                              },
                            ],
                          },
                        },
                      ],
                    },
                  ],
                },
              ],
            },
          });
        }
        if (key === 'ratesInformation' || key === 'ratesInformationDiscountRate') {
          return Promise.resolve({
            ratesInformation: {
              rateClassifications: [
                {
                  rateClassification: 'FLEX',
                  rateDescription: 'Flex rate',
                  rateName: 'Flex',
                  rateOrder: 1,
                  rateTags: [],
                },
              ],
            },
          });
        }
        if (key === 'userDetails') {
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
        }
        if (key === 'searchCompanyById') {
          return Promise.resolve({
            companyProfile: {
              name: 'Zaraa Ltd',
              address: {
                addressLine1: '804 Compass Building',
                addressLine2: 'Station Approach',
                addressLine3: '',
                addressLine4: 'HAYES',
                country: 'GB',
                postalCode: 'UB3 4FH',
              },
              telephoneNumber: '7895674563',
              corpId: '3824',
              PROMOID: 'ST10R',
              companyId: '331323',
              profileType: 'Company',
              language: 'en',
              active: true,
              negotiatedRateEnabled: true,
            },
          });
        }
      }),
      logQueries: jest.fn(),
    })),
    getCookie: (cookieName: string) => {
      if (cookieName === utils.BUNDLE_CHOICE) {
        return mockCookies.bundles;
      }
    },
    graphQLRequest: jest.fn((...args) => mockGraphQLRequest(...args)),
    getGQLClient: jest.fn(),
    decodeIdToken: () => mockDecodeIdToken(),
    getAvailabilityParamsFromUrl: jest.fn().mockImplementation(() => mockAvailabilityParams),
    getLoggedInUserInfo: jest.fn().mockImplementation(() => mockLoggedInUserInfo),
    getHotelAvailabilityQueryKey: () => jest.fn(),
    logger: {
      info: jest.fn(),
    },
  };
});

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  mutations: undefined,
  queries: undefined,
}));

const mockGetCookie = jest.fn();
jest.mock('cookies', () => {
  return jest.fn().mockImplementation(() => ({
    get: mockGetCookie,
    set: mockGetCookie,
  }));
});

const queryClient = new ReactQuery.QueryClient();

const mockedData = {
  language: 'en',
  country: 'GB',
  req: {
    url: '/_next/data/development/gb/hotels/england/greater-london/london/hub-london-kings-cross.html.json?searchLocation=London&ARRdd=25&ARRmm=12&ARRyyyy=2022&NIGHTS=4&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB&BRAND=PI&slug=england&slug=greater-london&slug=london&slug=hub-london-kings-cross.html',
  },
  params: {
    slug: '/hotels/england/greater-london/london/hub-london-kings-cross.html',
  },
  featureToggles: {
    [FT_PI_PROMO_CODE_LANDING_PAGE]: true,
    [FT_PI_PROMO_CODE_SITE_WIDE]: true,
    [FT_PI_NO_ROOM_TYPE_SEARCH]: true,
    [FT_PI_CCUI_BB_PROMOTION_BOX_CODE_COOKIE]: true,
  },
};

const expectedResult = {
  dehydratedState: {
    mutations: [],
    queries: [],
  },
};

const mockGraphQLRequest = jest.fn();

describe('createHDPPiDataLoaderFn', () => {
  beforeEach(() => {
    mockGetCookie.mockImplementation(() => mockToken);
    mockDecodeIdToken.mockReturnValue({ email: 'test@example.com' });
    mockGraphQLRequest.mockClear();
  });

  afterEach(() => {
    mockDecodeIdToken.mockReset();
    mockGetCookie.mockReset();
  });
  it('should render data loader with expected object', async () => {
    const dataLoaderPi = await createHDPPiDataLoaderFn({
      ...mockedData,
      queryClient,
    } as any);
    expect(dataLoaderPi).toStrictEqual(expectedResult);

    const staticHotelQueryCall = mockGraphQLRequest.mock.calls.find(([, variables]) =>
      Boolean(variables?.stayStartDate)
    );

    expect(staticHotelQueryCall?.[1]).toEqual(
      expect.objectContaining({
        language: 'en',
        country: 'GB',
        stayStartDate: '2022-12-25',
        stayEndDate: '2022-12-29',
      })
    );
  });

  it('should include arrival and departure in static hotel cache key', async () => {
    await createHDPPiDataLoaderFn({
      ...mockedData,
      queryClient,
    } as any);

    const staticHotelFetchQueryCall = (
      require('@whitbread-eos/utils').QueriesLogger as jest.Mock
    ).mock.results[0].value.fetchQuery.mock.calls.find(
      ([queryKey]: [any[]]) => queryKey?.[0] === 'staticHotelInformation'
    );

    expect(staticHotelFetchQueryCall?.[0]?.[0]).toBe('staticHotelInformation');
    expect(staticHotelFetchQueryCall?.[0]?.[1]).toBe('en');
    expect(staticHotelFetchQueryCall?.[0]?.[2]).toBe('GB');
    expect(staticHotelFetchQueryCall?.[0]?.[4]).toBe('2022-12-25');
    expect(staticHotelFetchQueryCall?.[0]?.[5]).toBe('2022-12-29');
  });

  it('should not fetch any data if idTokenCookie is not present', async () => {
    mockGetCookie.mockImplementation(() => '');
    await createHDPPiDataLoaderFn({ ...mockedData, queryClient } as any);

    expect(mockDecodeIdToken).not.toHaveBeenCalled();
  });

  it('should call promo information query when PROMOID present and feature toggle enabled', async () => {
    const promoData = {
      ...mockedData,
      featureToggles: {
        ...mockedData.featureToggles,
        [FT_PI_PROMO_CODE_LANDING_PAGE]: true,
        [FT_PI_PROMO_CODE_SITE_WIDE]: true,
        [FT_PI_NO_ROOM_TYPE_SEARCH]: true,
      },
      query: { PROMOID: 'ST10R' },
    };

    await createHDPPiDataLoaderFn({ ...promoData, queryClient } as any);
  });

  it('should call softBundle hotel Availability query when softBundles are enabled', async () => {
    const softBundleData = {
      ...mockedData,
      featureToggles: {
        ...mockedData.featureToggles,
        [FT_PI_PROMO_CODE_LANDING_PAGE]: true,
        [FT_PI_PROMO_CODE_SITE_WIDE]: true,
        [FT_PI_NO_ROOM_TYPE_SEARCH]: true,
        [FT_PI_DISPLAY_SOFT_BUNDLES]: true,
      },
    };

    await createHDPPiDataLoaderFn({ ...softBundleData, queryClient } as any);
  });

  it('should not fetch user details if idTokenCookie is missing', async () => {
    mockGetCookie.mockImplementation(() => '');
    await createHDPPiDataLoaderFn({ ...mockedData, queryClient } as any);
    expect(mockDecodeIdToken).not.toHaveBeenCalled();
  });

  it('should call hotel availability if number of nights > 0 and corpId does not exist', async () => {
    mockAvailabilityParams.corpId = '';
    await createHDPPiDataLoaderFn({ ...mockedData, queryClient } as any);
    expect(mockGraphQLRequest).toHaveBeenCalled();
  });

  it('should use appliedPromoBoxCode cookie when promo box cookie feature flag is enabled', async () => {
    mockGetCookie.mockImplementation((name) => {
      if (name === 'appliedPromoBoxCode') {
        return 'PROMO123';
      }
      return mockToken;
    });
    await createHDPPiDataLoaderFn({
      ...mockedData,
      queryClient,
    } as any);
    expect(mockGetCookie).toHaveBeenCalled();
  });

  it('should handle missing appliedPromoBoxCode cookie when feature flag is enabled', async () => {
    mockGetCookie.mockImplementation((name) => {
      if (name === 'appliedPromoBoxCode') {
        return undefined;
      }
      return mockToken;
    });
    await createHDPPiDataLoaderFn({
      ...mockedData,
      queryClient,
    } as any);
    expect(mockGetCookie).toHaveBeenCalled();
  });

  it('should not use appliedPromoBoxCode cookie when promo box cookie feature flag is disabled', async () => {
    mockGetCookie.mockClear();
    const data = {
      ...mockedData,
      featureToggles: {
        ...mockedData.featureToggles,
        [FT_PI_CCUI_BB_PROMOTION_BOX_CODE_COOKIE]: false,
      },
    };
    await createHDPPiDataLoaderFn({
      ...data,
      queryClient,
    } as any);
    expect(true).toBe(true);
  });

  it('should call promo information query when landing page flag is enabled and promo cookie does not exist', async () => {
    mockGetCookie.mockImplementation(() => undefined);

    const data = {
      ...mockedData,
      query: {
        ...mockedData,
        PROMOID: 'SUMMER25',
      },
      featureToggles: {
        ...mockedData.featureToggles,
        [FT_PI_CCUI_BB_PROMOTION_BOX_CODE_COOKIE]: false,
      },
    };

    await createHDPPiDataLoaderFn({
      ...data,
      queryClient,
    } as any);

    expect(mockGraphQLRequest).toHaveBeenCalled();
  });

  it('should enter promo flow when promo box cookie flag is enabled and promo cookie does not exist', async () => {
    mockGetCookie.mockImplementation((name) => {
      if (name === 'appliedPromoBoxCode') {
        return 'PROMO123';
      }

      return undefined;
    });

    const data = {
      ...mockedData,
      featureToggles: {
        ...mockedData.featureToggles,
        [FT_PI_PROMO_CODE_LANDING_PAGE]: false,
      },
    };

    await createHDPPiDataLoaderFn({
      ...data,
      queryClient,
    } as any);

    expect(mockGetCookie).toHaveBeenCalledWith('appliedPromoBoxCode');
  });

  it('should enter promo flow when both feature flags are enabled and promo cookie does not exist', async () => {
    mockGetCookie.mockImplementation((name) => {
      if (name === 'appliedPromoBoxCode') {
        return 'PROMO123';
      }

      return undefined;
    });

    await createHDPPiDataLoaderFn({
      ...mockedData,
      queryClient,
    } as any);

    expect(mockGetCookie).toHaveBeenCalledWith('appliedPromoBoxCode');
  });

  it('should skip promo information flow when promo cookie already exists', async () => {
    mockGetCookie.mockImplementation((name) => {
      if (name === 'PROMO_CODE_COOKIE') {
        return 'FREEBREAKFAST';
      }

      return mockToken;
    });

    await createHDPPiDataLoaderFn({
      ...mockedData,
      queryClient,
    } as any);

    expect(mockGraphQLRequest).toHaveBeenCalled();
  });

  it('should not enter promo flow when both feature flags are disabled', async () => {
    mockGetCookie.mockImplementation(() => undefined);

    const data = {
      ...mockedData,
      featureToggles: {
        ...mockedData.featureToggles,
        [FT_PI_PROMO_CODE_LANDING_PAGE]: false,
        [FT_PI_CCUI_BB_PROMOTION_BOX_CODE_COOKIE]: false,
      },
    };

    await createHDPPiDataLoaderFn({
      ...data,
      queryClient,
    } as any);

    expect(mockGraphQLRequest).toHaveBeenCalled();
  });

  it('should not call promo information API when promoId, sitewide flag and promo box code are all falsy', async () => {
    mockGetCookie.mockImplementation(() => undefined);

    const originalPromoId = mockAvailabilityParams.PROMOID;
    mockAvailabilityParams.PROMOID = undefined as any;

    const data = {
      ...mockedData,
      featureToggles: {
        ...mockedData.featureToggles,
        [FT_PI_PROMO_CODE_SITE_WIDE]: false,
        [FT_PI_CCUI_BB_PROMOTION_BOX_CODE_COOKIE]: false,
      },
    };

    await createHDPPiDataLoaderFn({
      ...data,
      queryClient,
    } as any);

    expect(mockGraphQLRequest.mock.calls.some(([query]) => query === GET_PROMO_INFORMATION)).toBe(
      false
    );

    mockAvailabilityParams.PROMOID = originalPromoId;
  });

  it('should call promo information API when site wide flag is enabled', async () => {
    mockGetCookie.mockImplementation(() => undefined);

    const originalPromoId = mockAvailabilityParams.PROMOID;
    mockAvailabilityParams.PROMOID = undefined as any;

    const data = {
      ...mockedData,
      featureToggles: {
        ...mockedData.featureToggles,
        [FT_PI_CCUI_BB_PROMOTION_BOX_CODE_COOKIE]: false,
      },
    };

    await createHDPPiDataLoaderFn({
      ...data,
      queryClient,
    } as any);

    expect(mockGraphQLRequest).toHaveBeenCalled();

    mockAvailabilityParams.PROMOID = originalPromoId;
  });

  it('should call promo information API when appliedPromoBoxCode exists', async () => {
    mockGetCookie.mockImplementation((name) => {
      if (name === 'appliedPromoBoxCode') {
        return 'PROMO123';
      }

      return undefined;
    });

    const originalPromoId = mockAvailabilityParams.PROMOID;
    mockAvailabilityParams.PROMOID = undefined as any;

    const data = {
      ...mockedData,
      featureToggles: {
        ...mockedData.featureToggles,
        [FT_PI_PROMO_CODE_LANDING_PAGE]: false,
        [FT_PI_PROMO_CODE_SITE_WIDE]: false,
      },
    };

    await createHDPPiDataLoaderFn({
      ...data,
      queryClient,
    } as any);

    expect(mockGetCookie).toHaveBeenCalledWith('appliedPromoBoxCode');
    mockAvailabilityParams.PROMOID = originalPromoId;
  });

  it('should not call promo information API when promotions in hotel availability is enabled', async () => {
    mockGraphQLRequest.mockClear();
    await createHDPPiDataLoaderFn({
      ...mockedData,
      queryClient,
      featureToggles: {
        ...mockedData.featureToggles,
        [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: true,
      },
    } as any);
    expect(mockGraphQLRequest.mock.calls.some(([query]) => query === GET_PROMO_INFORMATION)).toBe(
      false
    );
  });

  it('should pass promotionCode to hotel availability when promotions in hotel availability is enabled', async () => {
    mockAvailabilityParams.corpId = '';

    mockGetCookie.mockImplementation((name) => {
      if (name === 'appliedPromoBoxCode') {
        return 'TEST123';
      }
      return undefined;
    });

    await createHDPPiDataLoaderFn({
      ...mockedData,
      queryClient,
      featureToggles: {
        ...mockedData.featureToggles,
        [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: true,
      },
    } as any);

    const hotelAvailabilityCall = mockGraphQLRequest.mock.calls.find(
      ([query]) => query === HOTEL_AVAILABILITY_QUERY
    );
    expect(hotelAvailabilityCall).toBeDefined();
    const variables = hotelAvailabilityCall![1];
    expect(variables.isPromoBox).toBe(false);
  });

  it('should pass isPromoBox as false to hotel availability when promotions in hotel availability is enabled', async () => {
    mockGraphQLRequest.mockClear();

    mockGetCookie.mockImplementation((name) => {
      if (name === 'appliedPromoBoxCode') {
        return 'PROMO123';
      }
      return undefined;
    });

    await createHDPPiDataLoaderFn({
      ...mockedData,
      queryClient,
      featureToggles: {
        ...mockedData.featureToggles,
        [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: true,
      },
    } as any);

    const hotelAvailabilityCall = mockGraphQLRequest.mock.calls.find(
      ([query]) => query === HOTEL_AVAILABILITY_QUERY
    );

    expect(hotelAvailabilityCall).toBeDefined();

    const variables = hotelAvailabilityCall![1];

    expect(variables).toHaveProperty('promotionCode');
  });

  it('should not pass isPromoBox to hotel availability when the promo cookie is already set, even if promotions in hotel availability is enabled', async () => {
    mockGraphQLRequest.mockClear();

    mockGetCookie.mockImplementation((name) => {
      if (name === PROMO_CODE_COOKIE) {
        return 'FREEBREAKFAST';
      }
      return undefined;
    });

    await createHDPPiDataLoaderFn({
      ...mockedData,
      queryClient,
      featureToggles: {
        ...mockedData.featureToggles,
        [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: true,
      },
    } as any);

    const hotelAvailabilityCall = mockGraphQLRequest.mock.calls.find(
      ([query]) => query === HOTEL_AVAILABILITY_QUERY
    );

    expect(hotelAvailabilityCall).toBeDefined();

    const variables = hotelAvailabilityCall![1];

    expect(variables.isPromoBox).toBeUndefined();
    expect(variables.promotionCode).toBe('FREEBREAKFAST');
  });
});
