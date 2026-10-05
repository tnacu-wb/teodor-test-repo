import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import {
  FT_CCUI_PROMO_CODE_LANDING_PAGE,
  FT_CCUI_PROMO_CODE_SITE_WIDE,
  FT_PI_BB_CCUI_BARRIER_FREE_LABEL,
  FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY,
  MULTI_HOTEL_AVAILABILITIES_QUERY,
  ROOM_TYPE,
} from '@whitbread-eos/api';
import { graphQLRequest } from '@whitbread-eos/utils';
import { ServerResponse, IncomingMessage } from 'http';

import createSearchResultsCCUIDataLoader from './data.ccui';

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
  getPromoId: jest.fn((promo) => promo),
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
  },
  language: 'en',
  country: 'GB',
  res: {} as ServerResponse,
  req: { url: undefined, cookies: {} } as IncomingMessage & {
    cookies: Partial<{ [key: string]: string }>;
  },
  proxyOptions: '',
  featureToggles: {
    [FT_PI_BB_CCUI_BARRIER_FREE_LABEL]: false,
    [FT_CCUI_PROMO_CODE_LANDING_PAGE]: true,
    [FT_CCUI_PROMO_CODE_SITE_WIDE]: true,
  },
};

describe('createSearchResultsCCUIDataLoader', () => {
  it('should create data loader with expected object', async () => {
    mockedData.language = 'en';

    const dataLoaderCCUI = await createSearchResultsCCUIDataLoader({
      ...mockedData,
      queryClient,
    });

    expect(dataLoaderCCUI).toMatchObject({
      dehydratedState: { mutations: [], queries: [] },
      user: {},
      roles: undefined,
      promoInformationQuery: expect.any(Object),
    });
  });

  it('should create data loader (with no query client data) with expected object', async () => {
    mockedData.language = 'de';

    const dataLoaderCCUI = await createSearchResultsCCUIDataLoader({
      ...mockedData,
      query: {
        reservationId: undefined,
        BRAND: undefined,
      },
      queryClient,
    });

    expect(dataLoaderCCUI).toMatchObject({
      dehydratedState: { mutations: [], queries: [] },
      user: {},
      roles: undefined,
      promoInformationQuery: expect.any(Object),
    });
  });

  it('should populate promoInformationQuery when PROMOID is provided', async () => {
    mockedData.language = 'en';

    const dataLoaderCCUI = await createSearchResultsCCUIDataLoader({
      ...mockedData,
      queryClient,
      query: {
        PROMOID: 'TEST123',
      },
    });

    // validate promo object content
    expect(dataLoaderCCUI.promoInformationQuery).toMatchObject({
      promotionCode: 'TEST123',
      isPromoEnabled: true,
      country: 'GB',
      language: 'en',
      channel: 'CCUI',
    });
  });
  it('should not populate promoInformationQuery when promotions in hotel availability is enabled', async () => {
    mockedData.language = 'en';

    const dataLoaderCCUI = await createSearchResultsCCUIDataLoader({
      ...mockedData,
      queryClient,
      query: {
        PROMOID: 'TEST123',
      },
      featureToggles: {
        ...mockedData.featureToggles,
        [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: true,
      },
    });

    expect(dataLoaderCCUI.promoInformationQuery).toBeNull();
  });

  it('should not pass promotionCode when companyId is present', async () => {
    const graphQLRequestMock = graphQLRequest as jest.Mock;

    await createSearchResultsCCUIDataLoader({
      ...mockedData,
      queryClient,
      query: {
        PROMOID: 'TEST123',
        CORPID: '15010601',
      },
      featureToggles: {
        ...mockedData.featureToggles,
        [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: true,
      },
    });
    const hotelAvailabilityCall = graphQLRequestMock.mock.calls.find(
      ([query]) => query === MULTI_HOTEL_AVAILABILITIES_QUERY
    );
    expect(hotelAvailabilityCall).toBeDefined();
    const variables = hotelAvailabilityCall![1];
    expect(variables).not.toHaveProperty('promotionCode');
  });

  it('should pass promotionCode to hotel availability when promotions in hotel availability is enabled', async () => {
    mockedData.language = 'en';

    await createSearchResultsCCUIDataLoader({
      ...mockedData,
      queryClient,
      query: {
        PROMOID: 'TEST123',
      },
      featureToggles: {
        ...mockedData.featureToggles,
        [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: true,
      },
    });

    expect(graphQLRequest).toHaveBeenCalled();
  });
});
