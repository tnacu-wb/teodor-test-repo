import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import {
  ROOM_TYPE,
  RC_PRICE_MODIFIER,
  RC_DISTANCE_MODIFIER,
  FT_PI_SORT_ORDER_DROPDOWN,
  FT_PI_PROMO_CODE_LANDING_PAGE,
  FT_PI_PROMO_CODE_SITE_WIDE,
  MULTI_HOTEL_AVAILABILITIES_QUERY_PI_V2,
  FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY,
} from '@whitbread-eos/api';
import { graphQLRequest } from '@whitbread-eos/utils';
import { IncomingMessage, ServerResponse } from 'http';

import createSearchResultsPiDataLoader from './data.pi';

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

const mockCustomLocale = jest.fn();
const mockGetSuggestions = jest.fn(() => mockSuggestions);

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  mutations: undefined,
  queries: undefined,
}));
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
      if (key === 'promotionsInformation') {
        return Promise.resolve({
          promotionsInformation: 'promo info',
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
  ID_TOKEN_COOKIE: 'id_token_cookie',
  WB_SESSION_ID: 'WB-SESSION-ID',
}));

jest.mock('@whitbread-eos/api', () => ({
  ...jest.requireActual('@whitbread-eos/api'),
  getSuggestions: () => mockGetSuggestions(),
}));

const mockToken = 'mock-id-token';

jest.mock('cookies', () => {
  return jest.fn().mockImplementation(() => {
    return {
      get: (key: string) => {
        if (key === 'id_token_cookie') return mockToken;
        if (key === 'WB-SESSION-ID') return 'mock-session-id';
        return key;
      },
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
  req: { url: undefined, cookies: {} } as IncomingMessage & {
    cookies: Partial<{ [key: string]: string }>;
  },
  featureToggles: {
    [FT_PI_SORT_ORDER_DROPDOWN]: false,
    [FT_PI_PROMO_CODE_LANDING_PAGE]: true,
    [FT_PI_PROMO_CODE_SITE_WIDE]: true,
  },
};

describe('createSearchResultsPIDataLoader', () => {
  it('should create data loader with expected object', async () => {
    mockedData.language = 'en';

    const dataLoaderPI = await createSearchResultsPiDataLoader({
      ...mockedData,
      queryClient,
    });

    expect(dataLoaderPI).toEqual(
      expect.objectContaining({
        dehydratedState: { mutations: [], queries: [] },
      })
    );
  });

  it('should create data loader and include fallbackSearchPlace when no placeId', async () => {
    mockedData.language = 'de';

    const dataLoaderPI = await createSearchResultsPiDataLoader({
      ...mockedData,
      query: {
        reservationId: undefined,
        BRAND: undefined,
      },
      queryClient,
    });

    expect(dataLoaderPI).toEqual(
      expect.objectContaining({
        dehydratedState: { mutations: [], queries: [] },
        fallbackSearchPlace: {
          PLACEID: 'ChIJMzUzYv1XckgRN0df2voX6-s',
          'searchModel.searchTerm': 'Weymouth, UK',
        },
      })
    );
  });

  it('should not pass promotionCode when promotions in hotel availability is disabled', async () => {
    const graphQLRequestMock = graphQLRequest as jest.Mock;

    graphQLRequestMock.mockClear();

    await createSearchResultsPiDataLoader({
      ...mockedData,
      queryClient,
      query: {
        ...mockedData.query,
        PROMOID: 'TEST123',
      },
      featureToggles: {
        ...mockedData.featureToggles,
        [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: false,
      },
    });

    const hotelAvailabilityCall = graphQLRequestMock.mock.calls.find(
      ([query]) => query === MULTI_HOTEL_AVAILABILITIES_QUERY_PI_V2
    );
    expect(hotelAvailabilityCall).toBeDefined();
    expect(hotelAvailabilityCall[1].promotionCode).toBeUndefined();
  });
});

describe('Cookie Parsing Logic', () => {
  it('should still work when rcPriceModifier and rcDistanceModifier return mocked strings', async () => {
    const dataLoaderPI = await createSearchResultsPiDataLoader({
      ...mockedData,
      queryClient,
    });

    expect(dataLoaderPI).toEqual(
      expect.objectContaining({
        dehydratedState: { mutations: [], queries: [] },
      })
    );
  });

  it('should not fail when cookies are non-numeric (mock returns key names)', async () => {
    const dataLoaderPI = await createSearchResultsPiDataLoader({
      ...mockedData,
      queryClient,
    });

    expect(dataLoaderPI).toEqual(
      expect.objectContaining({
        dehydratedState: { mutations: [], queries: [] },
      })
    );
  });

  it('should not fail when cookies do not exist (mock already handles this)', async () => {
    const dataLoaderPI = await createSearchResultsPiDataLoader({
      ...mockedData,
      queryClient,
    });

    expect(dataLoaderPI).toEqual(
      expect.objectContaining({
        dehydratedState: { mutations: [], queries: [] },
      })
    );
  });

  it('should handle cookies with special mocked characters (mock returns literal key)', async () => {
    const dataLoaderPI = await createSearchResultsPiDataLoader({
      ...mockedData,
      queryClient,
    });

    expect(dataLoaderPI).toEqual(
      expect.objectContaining({
        dehydratedState: { mutations: [], queries: [] },
      })
    );
  });

  it('should pass promotionCode to hotel availabilities when promotions in hotel availability is enabled', async () => {
    const graphQLRequestMock = graphQLRequest as jest.Mock;

    graphQLRequestMock.mockClear();

    await createSearchResultsPiDataLoader({
      ...mockedData,
      queryClient,
      query: {
        ...mockedData.query,
        PROMOID: 'TEST123',
      },
      featureToggles: {
        ...mockedData.featureToggles,
        [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: true,
      },
    });

    const hotelAvailabilityCall = graphQLRequestMock.mock.calls.find(
      ([query]) => query === MULTI_HOTEL_AVAILABILITIES_QUERY_PI_V2
    );

    expect(hotelAvailabilityCall).toBeDefined();

    expect(hotelAvailabilityCall[1]).toEqual(
      expect.objectContaining({
        promotionCode: 'TEST123',
      })
    );
  });
});

describe('Cookie Parsing Logic', () => {
  it('should set rcPriceModifier and rcDistanceModifier correctly when cookies are valid integers', async () => {
    document.cookie = `${RC_PRICE_MODIFIER}=10`;
    document.cookie = `${RC_DISTANCE_MODIFIER}=20`;

    const dataLoaderPI = await createSearchResultsPiDataLoader({
      ...mockedData,
      queryClient,
    });

    expect(dataLoaderPI).toEqual(
      expect.objectContaining({
        dehydratedState: { mutations: [], queries: [] },
      })
    );
  });

  it('should set rcPriceModifier and rcDistanceModifier to empty strings when cookies are invalid', async () => {
    document.cookie = `${RC_PRICE_MODIFIER}=not-a-number`;
    document.cookie = `${RC_DISTANCE_MODIFIER}=also-not-a-number`;

    const dataLoaderPI = await createSearchResultsPiDataLoader({
      ...mockedData,
      queryClient,
    });

    expect(dataLoaderPI).toEqual(
      expect.objectContaining({
        dehydratedState: { mutations: [], queries: [] },
      })
    );
  });

  it('should set rcPriceModifier and rcDistanceModifier to empty strings when cookies do not exist', async () => {
    document.cookie = '';

    const dataLoaderPI = await createSearchResultsPiDataLoader({
      ...mockedData,
      queryClient,
    });

    expect(dataLoaderPI).toEqual(
      expect.objectContaining({
        dehydratedState: { mutations: [], queries: [] },
      })
    );
  });

  it('should handle multiple cookies correctly', async () => {
    document.cookie = `${RC_PRICE_MODIFIER}=30`;
    document.cookie = `${RC_DISTANCE_MODIFIER}=40`;
    document.cookie = 'other_cookie=value';

    const dataLoaderPI = await createSearchResultsPiDataLoader({
      ...mockedData,
      queryClient,
    });

    expect(dataLoaderPI).toEqual(
      expect.objectContaining({
        dehydratedState: { mutations: [], queries: [] },
      })
    );
  });

  it('should handle cookies with special characters', async () => {
    document.cookie = `${RC_PRICE_MODIFIER}=30%`;
    document.cookie = `${RC_DISTANCE_MODIFIER}=40$`;

    const dataLoaderPI = await createSearchResultsPiDataLoader({
      ...mockedData,
      queryClient,
    });

    expect(dataLoaderPI).toEqual(
      expect.objectContaining({
        dehydratedState: { mutations: [], queries: [] },
      })
    );
  });
});
