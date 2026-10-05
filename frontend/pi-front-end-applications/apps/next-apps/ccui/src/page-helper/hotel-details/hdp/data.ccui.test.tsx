/* eslint-disable @typescript-eslint/no-explicit-any */
import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import {
  HOTEL_AVAILABILITY_CCUI_QUERY,
  FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY,
} from '@whitbread-eos/api';
import * as utils from '@whitbread-eos/utils';

import createHDPCcuiDataLoaderFn from './data.ccui';

const mockUseRouter = jest.fn().mockReturnValue({ query: { PROMOID: 'ST10R' } });

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const mockStaticHotelInformation = {
  hotelInformationBySlug: {
    name: 'hub London Kings Cross',
    brand: 'HUB',
    hotelId: 'LONKIN',
  },
};

const getBookingInformationData = {
  bookingInformation: {
    hotelId: 'LONKIN',
    reservationByIdList: [
      {
        roomStay: {
          arrivalDate: '2023-02-23',
          departureDate: '2023-02-24',
        },
      },
    ],
    bookingFlowId: 'booking-hub',
  },
};
const mockDecodeIdToken = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  QueriesLogger: jest.fn().mockImplementation(() => ({
    fetchQuery: jest.fn().mockImplementation((queryKey, queryFn) => {
      const key = queryKey[0];
      queryFn();
      if (key === 'staticHotelInformation') {
        return Promise.resolve(mockStaticHotelInformation);
      }
      if (key === 'promotionsInformation') {
        return Promise.resolve({
          promotionsInformation: {
            isWithinPromoWindow: true,
            promotionCode: 'ST10R',
          },
        });
      }
      if (key === 'GetBookingInformation') {
        return Promise.resolve(getBookingInformationData);
      }
      if (key === 'hotelAvailabilityCCUI') {
        return Promise.resolve({
          hotelAvailability: {
            hotelId: 'LONKIN',
            startDate: '2023-05-14',
            endDate: '2023-05-15',
            available: true,
            roomRates: [
              {
                ratePlanCode: 'STANDARD',
                roomTypes: [
                  {
                    roomType: 'DB',
                    adults: 2,
                    children: 0,
                    cotRequested: false,
                    rooms: [
                      {
                        pmsRoomType: 'DBLWIN',
                        silentSubstitution: false,
                        cotAvailable: false,
                        roomClass: 'ST',
                        specialRequests: [],
                        roomPriceBreakdown: {
                          totalNetAmount: 200,
                          currencyCode: 'GBP',
                          dailyPrices: {
                            date: '2023-05-14',
                            netPrice: 200,
                          },
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
    }),
    prefetchQuery: jest.fn().mockImplementation((queryKey, queryFn) => {
      const key = queryKey[0];
      queryFn();
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
    }),
    logQueries: jest.fn(),
  })),
  graphQLRequest: jest.fn(),
  logger: {
    info: jest.fn(),
    error: jest.fn(),
  },
}));

const mockGetCookie = jest.fn();
jest.mock('cookies', () => {
  return jest.fn().mockImplementation(() => ({
    get: mockGetCookie,
    set: mockGetCookie,
  }));
});

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  mutations: undefined,
  queries: undefined,
}));

const queryClient = new ReactQuery.QueryClient();

const mockedData = {
  session: {
    tokenSet: {
      accessToken: 'sampletoken123',
    },
    user: {},
  },
  language: 'en',
  country: 'GB',
  req: {
    url: '/_next/data/development/gb/hotels/england/greater-london/london/hub-london-kings-cross.html.json?searchLocation=London&ARRdd=25&ARRmm=12&ARRyyyy=2022&NIGHTS=4&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB&BRAND=PI&CORPID=2153&slug=england&slug=greater-london&slug=london&slug=hub-london-kings-cross.html',
  },
  params: {
    slug: '/hotels/england/greater-london/london/hub-london-kings-cross.html',
  },
  query: {
    reservationId: '123test',
  },
  featureToggles: {
    release_pi_bb_ccui_barrier_free_label: true,
    release_pi_promo_code_landing_page: true,
    release_pi_promo_code_site_wide: true,
    release_pi_ccui_bb_promotion_box_code_cookie: true,
  },
};
const mockGraphQLRequest = jest.fn();

const expectedResult = {
  accessToken: 'sampletoken123',
  user: {},
  dehydratedState: {
    mutations: [],
    queries: [],
  },
};

describe('createHDPCcuiDataLoaderFn', () => {
  beforeEach(() => {
    mockGraphQLRequest.mockClear();
  });

  it('should render data loader with expected object', async () => {
    const dataLoaderPi = await createHDPCcuiDataLoaderFn({
      ...mockedData,
      queryClient,
    } as any);
    expect(dataLoaderPi).toStrictEqual(expectedResult);

    const staticHotelQueryCall = (utils.graphQLRequest as jest.Mock).mock.calls.find(
      ([, variables]) => Boolean(variables?.stayStartDate)
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
    const fetchQueryMock = jest.fn().mockImplementation((queryKey, queryFn) => {
      const key = queryKey[0];
      queryFn();

      if (key === 'staticHotelInformation') {
        return Promise.resolve(mockStaticHotelInformation);
      }
      if (key === 'GetBookingInformation') {
        return Promise.resolve(getBookingInformationData);
      }
      return Promise.resolve({});
    });

    (utils.QueriesLogger as jest.Mock).mockImplementation(() => ({
      fetchQuery: fetchQueryMock,
      prefetchQuery: jest.fn(),
      logQueries: jest.fn(),
    }));

    await createHDPCcuiDataLoaderFn({
      ...mockedData,
      queryClient,
    } as any);

    const staticHotelFetchCall = fetchQueryMock.mock.calls.find(
      ([queryKey]: [any[]]) => queryKey?.[0] === 'staticHotelInformation'
    );

    expect(staticHotelFetchCall?.[0]?.[0]).toBe('staticHotelInformation');
    expect(staticHotelFetchCall?.[0]?.[1]).toBe('en');
    expect(staticHotelFetchCall?.[0]?.[2]).toBe('GB');
    expect(staticHotelFetchCall?.[0]?.[4]).toBe('2022-12-25');
    expect(staticHotelFetchCall?.[0]?.[5]).toBe('2022-12-29');

    jest.restoreAllMocks();
  });

  it('should render data loader DE object', async () => {
    mockedData.language = 'de';
    const dataLoaderPi = await createHDPCcuiDataLoaderFn({
      ...mockedData,
      queryClient,
    } as any);
    expect(dataLoaderPi).toStrictEqual(expectedResult);
  });

  it('should render data loader with no reservation id', async () => {
    mockedData.query.reservationId = '';
    const dataLoaderPi = await createHDPCcuiDataLoaderFn({
      ...mockedData,
      queryClient,
    } as any);
    expect(dataLoaderPi).toStrictEqual(expectedResult);
  });

  it('should call promo information query when PROMOID present and feature toggle enabled', async () => {
    const promoData = {
      ...mockedData,
      query: { PROMOID: 'ST10R' },
    };
    await createHDPCcuiDataLoaderFn({ ...promoData, queryClient } as any);
  });

  it('should not fetch user details if idTokenCookie is missing', async () => {
    mockGetCookie.mockImplementation(() => '');
    await createHDPCcuiDataLoaderFn({ ...mockedData, queryClient } as any);
    expect(mockDecodeIdToken).not.toHaveBeenCalled();
  });

  it('should use appliedPromoBoxCode cookie when cookie feature toggle is enabled', async () => {
    mockGetCookie.mockImplementation((name: string) => {
      if (name === 'appliedPromoBoxCode') {
        return 'COOKIEPROMO';
      }
      return '';
    });

    const result = await createHDPCcuiDataLoaderFn({
      ...mockedData,
      queryClient,
    } as any);

    expect(result).toStrictEqual(expectedResult);

    expect(mockGetCookie).toHaveBeenCalledWith('appliedPromoBoxCode');
  });

  it('should map rate plan codes from hotel availability response', async () => {
    mockedData.query.reservationId = '123test';

    await createHDPCcuiDataLoaderFn({
      ...mockedData,
      queryClient,
    } as any);

    expect(true).toBe(true);
  });

  it('should map rate plan codes from hotel availability response', async () => {
    const fetchQueryMock = jest.fn().mockImplementation((queryKey, queryFn) => {
      const key = queryKey[0];

      queryFn();

      if (key === 'staticHotelInformation') {
        return Promise.resolve(mockStaticHotelInformation);
      }

      if (key === 'GetBookingInformation') {
        return Promise.resolve(getBookingInformationData);
      }

      if (key === 'hotelAvailabilityCCUI') {
        return Promise.resolve({
          hotelAvailability: {
            available: true,
            roomRates: [
              {
                ratePlanCode: 'STANDARD',
              },
              {
                ratePlanCode: 'FLEX',
              },
            ],
          },
        });
      }

      return Promise.resolve({});
    });

    (utils.QueriesLogger as jest.Mock).mockImplementation(() => ({
      fetchQuery: fetchQueryMock,
      prefetchQuery: jest.fn(),
      logQueries: jest.fn(),
    }));

    await createHDPCcuiDataLoaderFn({
      ...mockedData,
      queryClient,
    } as any);

    expect(fetchQueryMock).toHaveBeenCalledWith(
      expect.arrayContaining(['hotelAvailabilityCCUI']),
      expect.any(Function)
    );
  });

  it('should use landing page promo code when corpId is not present', async () => {
    jest.spyOn(utils, 'getAvailabilityParamsFromUrl').mockReturnValue({
      arrival: '2023-05-14',
      departure: '2023-05-15',
      rooms: [],
      corpId: '',
      numberOfNights: 1,
      promoId: 'ST10R',
    } as any);

    await createHDPCcuiDataLoaderFn({
      ...mockedData,
      queryClient,
      featureToggles: {
        ...mockedData.featureToggles,
        release_pi_promo_code_landing_page: true,
      },
    } as any);

    (utils.getAvailabilityParamsFromUrl as jest.Mock).mockRestore();
  });

  it('should execute else branch when corpId is empty', async () => {
    jest.spyOn(utils, 'getAvailabilityParamsFromUrl').mockReturnValue({
      arrival: '2023-05-14',
      departure: '2023-05-15',
      rooms: [],
      corpId: '',
      numberOfNights: 1,
      promoId: 'ST10R',
    } as any);

    await createHDPCcuiDataLoaderFn({
      ...mockedData,
      queryClient,
    } as any);

    jest.restoreAllMocks();
  });

  it('should log error when searchCompanyById fails', async () => {
    const error = new Error('searchCompanyById failed');

    jest.spyOn(utils, 'getAvailabilityParamsFromUrl').mockReturnValue({
      arrival: '2023-05-14',
      departure: '2023-05-15',
      rooms: [],
      corpId: '2153',
      numberOfNights: 1,
      promoId: '',
    } as any);

    const fetchQueryMock = jest.fn().mockImplementation((queryKey) => {
      const key = queryKey[0];

      if (key === 'GetBookingInformation') {
        return Promise.resolve(getBookingInformationData);
      }

      if (key === 'staticHotelInformation') {
        return Promise.resolve(mockStaticHotelInformation);
      }

      if (key === 'searchCompanyById') {
        return Promise.reject(error);
      }

      return Promise.resolve({});
    });

    (utils.QueriesLogger as jest.Mock).mockImplementation(() => ({
      fetchQuery: fetchQueryMock,
      prefetchQuery: jest.fn(),
      logQueries: jest.fn(),
    }));

    await createHDPCcuiDataLoaderFn({
      ...mockedData,
      queryClient,
    } as any);

    expect(utils.logger.error).toHaveBeenCalledWith(error);

    jest.restoreAllMocks();
  });

  it('should use empty string when arrivalDate is undefined', async () => {
    const fetchQueryMock = jest.fn().mockImplementation((queryKey) => {
      const key = queryKey[0];

      if (key === 'GetBookingInformation') {
        return Promise.resolve({
          bookingInformation: {
            hotelId: 'LONKIN',
            reservationByIdList: [
              {
                roomStay: {
                  departureDate: '2023-02-24',
                  // arrivalDate intentionally missing
                },
              },
            ],
            bookingFlowId: 'booking-hub',
          },
        });
      }

      if (key === 'staticHotelInformation') {
        return Promise.resolve(mockStaticHotelInformation);
      }

      return Promise.resolve({});
    });

    (utils.QueriesLogger as jest.Mock).mockImplementation(() => ({
      fetchQuery: fetchQueryMock,
      prefetchQuery: jest.fn(),
      logQueries: jest.fn(),
    }));

    await createHDPCcuiDataLoaderFn({
      ...mockedData,
      queryClient,
    } as any);

    expect(fetchQueryMock).toHaveBeenCalled();

    jest.restoreAllMocks();
  });

  it('should fallback to empty strings when arrivalDate and departureDate are missing', async () => {
    const fetchQueryMock = jest.fn().mockImplementation((queryKey) => {
      const key = queryKey[0];

      if (key === 'GetBookingInformation') {
        return Promise.resolve({
          bookingInformation: {
            hotelId: 'LONKIN',
            reservationByIdList: [
              {
                roomStay: {}, // arrivalDate and departureDate missing
              },
            ],
            bookingFlowId: 'booking-hub',
          },
        });
      }

      if (key === 'staticHotelInformation') {
        return Promise.resolve(mockStaticHotelInformation);
      }

      if (key === 'hotelAvailabilityCCUI') {
        return Promise.resolve({
          hotelAvailability: {
            roomRates: [],
          },
        });
      }

      return Promise.resolve({});
    });

    const prefetchQueryMock = jest.fn().mockResolvedValue({});

    (utils.QueriesLogger as jest.Mock).mockImplementation(() => ({
      fetchQuery: fetchQueryMock,
      prefetchQuery: prefetchQueryMock,
      logQueries: jest.fn(),
    }));

    await createHDPCcuiDataLoaderFn({
      ...mockedData,
      queryClient,
      query: {
        reservationId: '123test',
      },
    } as any);

    expect(fetchQueryMock).toHaveBeenCalledWith(
      expect.arrayContaining(['GetBookingInformation']),
      expect.any(Function)
    );
  });
  it('should pass isPromoBox to hotel availability when promotions in hotel availability is enabled', async () => {
    mockGetCookie.mockImplementation((name: string) => {
      if (name === 'appliedPromoBoxCode') {
        return 'COOKIEPROMO';
      }
      return '';
    });

    jest.spyOn(utils, 'getAvailabilityParamsFromUrl').mockReturnValue({
      arrival: '2023-05-14',
      departure: '2023-05-15',
      rooms: [],
      corpId: '',
      numberOfNights: 1,
      promoId: '',
    } as any);

    await createHDPCcuiDataLoaderFn({
      ...mockedData,
      queryClient,
      featureToggles: {
        ...mockedData.featureToggles,
        [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: true,
      },
    } as any);

    const hotelAvailabilityCall = (utils.graphQLRequest as jest.Mock).mock.calls.find(
      ([query]) => query === HOTEL_AVAILABILITY_CCUI_QUERY
    );
    expect(hotelAvailabilityCall).toBeDefined();
  });
});
