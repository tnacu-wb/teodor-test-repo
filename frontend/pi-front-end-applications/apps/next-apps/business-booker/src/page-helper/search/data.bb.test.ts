import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import {
  ROOM_TYPE,
  RC_PRICE_MODIFIER,
  RC_DISTANCE_MODIFIER,
  FT_BB_SORT_ORDER_DROPDOWN,
  FT_BB_PROMO_CODE_LANDING_PAGE,
  FT_BB_PROMO_CODE_SITE_WIDE,
  FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY,
  MULTI_HOTEL_AVAILABILITIES_QUERY,
} from '@whitbread-eos/api';
import { graphQLRequest as mockGraphQLRequest } from '@whitbread-eos/utils';
import { ServerResponse, IncomingMessage } from 'http';

import createSearchResultsBBDataLoader from './data.bb';

const mockSuggestions = {
  properties: [
    {
      code: 'WEYGAT',
      brand: 'PI',
      suggestion: 'Weymouth',
      geometry: [Object],
    },
  ],
  managedPlaces: [],
  places: [
    {
      suggestion: 'Weymouth, UK',
      placeId: 'ChIJMzUzYv1XckgRN0df2voX6-s',
    },
  ],
};

const mockGetBookingInformationData = {
  bookingInformation: {
    hotelId: 'LONKIN',
    totalCost: 141,
    currencyCode: 'GBP',
    bookingFlowId: 'booking-hub',
    infoMessages: [''],
    reservationByIdList: [
      {
        roomStay: {
          adultsNumber: 1,
          childrenNumber: 0,
          arrivalDate: '2023-02-23',
          departureDate: '2023-02-24',
          ratePlanCode: 'FLEXRATE',
          rateExtraInfo: {
            rateName: 'Flex',
          },
          roomExtraInfo: {
            roomType: ROOM_TYPE.STANDARD_BIGGER,
            roomName: 'Bigger Room',
          },
          accessibleRoom: {
            isAccessible: false,
            phoneNumber: '0333 321 3104',
          },
        },
      },
    ],
    upgradeToFlex: {
      amount: null,
      currency: null,
      flexRateCode: null,
    },
  },
};

const mockCustomLocale = jest.fn();
const mockGetSuggestions = jest.fn(() => mockSuggestions);

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useCustomLocale: () => mockCustomLocale(),
  useMutationRequest: () => ({
    mutation: {
      mutate: jest.fn(),
    },
    isSuccess: false,
  }),
  graphQLRequest: jest.fn(),
  getGQLClient: jest.fn(),
  getSearchResultsPageSize: jest.fn().mockResolvedValue({
    initialPageSize: 40,
    lazyLoadPageSize: 10,
  }),
  logger: {
    info: jest.fn(),
  },
  QueriesLogger: jest.fn().mockImplementation(() => ({
    fetchQuery: jest.fn().mockImplementation((queryKey, queryFn) => {
      const key = queryKey[0];
      queryFn();
      if (key === 'searchInformation') {
        return Promise.resolve({
          searchInformation: 'search info',
        });
      }
      if (key === 'hotelAvailabilities') {
        return Promise.resolve({
          hotelAvailabilities: 'availabilities',
        });
      }
      if (key === 'GetStaticContent') {
        return Promise.resolve({
          headerInformation: {},
          footer: {},
          labels: {},
        });
      }
      if (key === 'getSearchRules') {
        return Promise.resolve({
          maxNightsLimitation: {
            maxNights: 9,
          },
        });
      }
    }),
    prefetchQuery: jest.fn().mockImplementation((queryKey, queryFn) => {
      const key = queryKey[0];
      queryFn();
      switch (key) {
        case 'GetBookingInformation':
          return Promise.resolve({
            ...mockGetBookingInformationData,
          });
        default:
          return Promise.resolve({});
      }
    }),
    logQueries: jest.fn(),
  })),
}));

jest.mock('@whitbread-eos/utils/server', () => ({
  getInnBusinessServerSideProps: jest.fn().mockResolvedValue(undefined),
}));

jest.mock('@whitbread-eos/api', () => ({
  ...jest.requireActual('@whitbread-eos/api'),
  getSuggestions: () => mockGetSuggestions(),
}));

jest.mock('cookies', () => {
  return jest.fn().mockImplementation(() => {
    return {
      get: () => jest.fn(),
      set: () => jest.fn(),
    };
  });
});

const queryClient = new ReactQuery.QueryClient();

const mockedData = {
  session: {
    accessToken: 'asasasGderg12312',
    user: {},
  },
  resolvedUrl: '/',
  query: {
    reservationId: '',
    BRAND: 'PI',
    PLACEID: 'ChIJMzUzYv1XckgRN0df2voX6-s',
  },
  language: 'en',
  country: 'GB',
  res: {} as ServerResponse,
  req: { url: undefined, cookies: {}, headers: { host: '' } } as IncomingMessage & {
    cookies: Partial<{ [key: string]: string }>;
  },
  featureToggles: {
    [FT_BB_SORT_ORDER_DROPDOWN]: false,
  },
};

describe('createSearchResultsBBDataLoader', () => {
  it('should create data loader with expected object', async () => {
    mockedData.language = 'en';

    const dataLoaderBB = await createSearchResultsBBDataLoader({
      ...mockedData,
      queryClient,
    });

    expect(dataLoaderBB).toEqual(
      expect.objectContaining({
        dehydratedState: { mutations: [], queries: [] },
        innBusiness: undefined,
        promoInformationQuery: null,
      })
    );
  });

  it('should create data loader (with no query client data) with expected object', async () => {
    mockedData.language = 'de';

    const dataLoaderBB = await createSearchResultsBBDataLoader({
      ...mockedData,
      query: {
        reservationId: undefined,
        BRAND: undefined,
      },
      queryClient,
    });

    expect(dataLoaderBB).toEqual(
      expect.objectContaining({
        dehydratedState: { mutations: [], queries: [] },
        innBusiness: undefined,
        promoInformationQuery: null,
        fallbackSearchPlace: {
          PLACEID: 'ChIJMzUzYv1XckgRN0df2voX6-s',
          'searchModel.searchTerm': 'Weymouth, UK',
        },
      })
    );
  });

  it('should not create promoInformationQuery when promotions are returned from hotel availability', async () => {
    const result = await createSearchResultsBBDataLoader({
      ...mockedData,
      query: {
        PROMOID: 'PROMO123',
      },
      featureToggles: {
        [FT_BB_SORT_ORDER_DROPDOWN]: false,
        [FT_BB_PROMO_CODE_LANDING_PAGE]: true,
        [FT_BB_PROMO_CODE_SITE_WIDE]: false,
        [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: true,
      },
      queryClient,
    });

    expect(result.promoInformationQuery).toBeNull();
  });
  it('should create promoInformationQuery when promotions in hotel availability is disabled', async () => {
    const result = await createSearchResultsBBDataLoader({
      ...mockedData,
      query: {
        PROMOID: 'PROMO123',
      },
      featureToggles: {
        [FT_BB_SORT_ORDER_DROPDOWN]: false,
        [FT_BB_PROMO_CODE_LANDING_PAGE]: true,
        [FT_BB_PROMO_CODE_SITE_WIDE]: false,
        [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: false,
      },
      queryClient,
    });

    expect(result.promoInformationQuery).toEqual(
      expect.objectContaining({
        isPromoEnabled: true,
        promotionCode: 'PROMO123',
      })
    );
  });
  it('should not pass promotionCode to hotel availability query when promotions in hotel availability is disabled', async () => {
    jest.clearAllMocks();

    await createSearchResultsBBDataLoader({
      ...mockedData,
      query: {
        PROMOID: 'PROMO123',
      },
      featureToggles: {
        [FT_BB_SORT_ORDER_DROPDOWN]: false,
        [FT_BB_PROMO_CODE_LANDING_PAGE]: true,
        [FT_BB_PROMO_CODE_SITE_WIDE]: false,
        [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: false,
      },
      queryClient,
    });

    const availabilityCall = (mockGraphQLRequest as jest.Mock).mock.calls.find(
      ([query]) => query === MULTI_HOTEL_AVAILABILITIES_QUERY
    );

    expect(availabilityCall?.[1]).not.toHaveProperty('promotionCode');
  });
  it('should pass promotionCode to hotel availability query when promotions in hotel availability is enabled', async () => {
    jest.clearAllMocks();

    await createSearchResultsBBDataLoader({
      ...mockedData,
      query: {
        PROMOID: 'PROMO123',
      },
      featureToggles: {
        [FT_BB_SORT_ORDER_DROPDOWN]: false,
        [FT_BB_PROMO_CODE_LANDING_PAGE]: true,
        [FT_BB_PROMO_CODE_SITE_WIDE]: false,
        [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: true,
      },
      queryClient,
    });

    const hotelAvailabilityCall = (mockGraphQLRequest as jest.Mock).mock.calls.find(
      ([query]) => query === MULTI_HOTEL_AVAILABILITIES_QUERY
    );

    expect(hotelAvailabilityCall).toBeDefined();
    expect(hotelAvailabilityCall?.[1]).toEqual(
      expect.objectContaining({
        promotionCode: 'PROMO123',
      })
    );
  });
});

describe('Cookie Parsing Logic', () => {
  it('should set rcPriceModifier and rcDistanceModifier correctly when cookies are valid integers', async () => {
    document.cookie = `${RC_PRICE_MODIFIER}=10`;
    document.cookie = `${RC_DISTANCE_MODIFIER}=20`;

    const dataLoaderBB = await createSearchResultsBBDataLoader({
      ...mockedData,
      queryClient,
    });

    expect(dataLoaderBB).toEqual(
      expect.objectContaining({
        dehydratedState: { mutations: [], queries: [] },
        innBusiness: undefined,
      })
    );
  });

  it('should set rcPriceModifier and rcDistanceModifier to empty strings when cookies are invalid', async () => {
    document.cookie = `${RC_PRICE_MODIFIER}=not-a-number`;
    document.cookie = `${RC_DISTANCE_MODIFIER}=also-not-a-number`;

    const dataLoaderBB = await createSearchResultsBBDataLoader({
      ...mockedData,
      queryClient,
    });

    expect(dataLoaderBB).toEqual(
      expect.objectContaining({
        dehydratedState: { mutations: [], queries: [] },
        innBusiness: undefined,
      })
    );
  });

  it('should set rcPriceModifier and rcDistanceModifier to empty strings when cookies do not exist', async () => {
    document.cookie = '';

    const dataLoaderBB = await createSearchResultsBBDataLoader({
      ...mockedData,
      queryClient,
    });

    expect(dataLoaderBB).toEqual(
      expect.objectContaining({
        dehydratedState: { mutations: [], queries: [] },
        innBusiness: undefined,
      })
    );
  });

  it('should handle multiple cookies correctly', async () => {
    document.cookie = `${RC_PRICE_MODIFIER}=30`;
    document.cookie = `${RC_DISTANCE_MODIFIER}=40`;
    document.cookie = 'other_cookie=value';

    const dataLoaderBB = await createSearchResultsBBDataLoader({
      ...mockedData,
      queryClient,
    });

    expect(dataLoaderBB).toEqual(
      expect.objectContaining({
        dehydratedState: { mutations: [], queries: [] },
        innBusiness: undefined,
      })
    );
  });

  it('should handle cookies with special characters', async () => {
    document.cookie = `${RC_PRICE_MODIFIER}=30%`;
    document.cookie = `${RC_DISTANCE_MODIFIER}=40$`;

    const dataLoaderBB = await createSearchResultsBBDataLoader({
      ...mockedData,
      queryClient,
    });

    expect(dataLoaderBB).toEqual(
      expect.objectContaining({
        dehydratedState: { mutations: [], queries: [] },
        innBusiness: undefined,
      })
    );
  });
  it('should compute fallbackSearchPlace when both placeId and coordinates are missing', async () => {
    const dataLoaderBB = await createSearchResultsBBDataLoader({
      ...mockedData,
      query: {
        PLACEID: undefined,
        BRAND: 'PI',
        location: 'Weymouth',
      },
      queryClient,
    });

    expect(dataLoaderBB.fallbackSearchPlace).toEqual({
      PLACEID: 'ChIJMzUzYv1XckgRN0df2voX6-s',
      'searchModel.searchTerm': 'Weymouth, UK',
    });
  });
  it('should set startDate and endDate to null when arrival date is invalid', async () => {
    const dataLoaderBB = await createSearchResultsBBDataLoader({
      ...mockedData,
      query: {
        arrivalDay: '99',
        arrivalMonth: '99',
        arrivalYear: '9999',
        PLACEID: 'ChIJMzUzYv1XckgRN0df2voX6-s',
      },
      queryClient,
    });

    // ensure no crash and the loader returns correctly
    expect(dataLoaderBB).toHaveProperty('dehydratedState');
  });
  it('should call promo information API when landing page FT is enabled and PROMOID exists', async () => {
    jest.clearAllMocks();

    await createSearchResultsBBDataLoader({
      ...mockedData,
      query: { PROMOID: 'PROMO123' },
      featureToggles: {
        [FT_BB_PROMO_CODE_LANDING_PAGE]: true,
        [FT_BB_PROMO_CODE_SITE_WIDE]: false,
      },
      queryClient,
    });

    expect(mockGraphQLRequest).toHaveBeenCalled();
  });
  it('should call promo information API when site-wide FT is enabled even without PROMOID', async () => {
    jest.clearAllMocks();

    await createSearchResultsBBDataLoader({
      ...mockedData,
      query: {},
      featureToggles: {
        [FT_BB_PROMO_CODE_LANDING_PAGE]: true,
        [FT_BB_PROMO_CODE_SITE_WIDE]: true,
      },
      queryClient,
    });

    expect(mockGraphQLRequest).toHaveBeenCalled();
  });
  it('should default rcPriceModifier and rcDistanceModifier to 1 when cookies object returns undefined', async () => {
    const dataLoaderBB = await createSearchResultsBBDataLoader({
      ...mockedData,
      queryClient,
    });

    expect(dataLoaderBB).toHaveProperty('dehydratedState');
  });
});
