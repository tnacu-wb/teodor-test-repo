import { QueryClient } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import {
  type Channel,
  type HIAvailabilityRates,
  ROOM_TYPE,
  BOOKING_CHANNEL,
} from '@whitbread-eos/api';

import { render } from '../../utils/test-utils';
import RateSelector from './RateSelector.container';
import { mockGlobalConfig } from './mocksRateSelectorData';

const mockCustomLocale = jest.fn();

jest.mock('@chakra-ui/react', () => ({
  ...jest.requireActual('@chakra-ui/react'),
  useMediaQuery: jest.fn().mockReturnValue([false, true]),
}));

const mockCookies = {
  bundles: 'false',
};

const mockResponse = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  basketData: {
    hotelId: 'TKINPT',
    arrival: '2022-06-25',
    departure: '2022-06-29',
    numberOfAdults: 2,
    numberOfChildren: 0,
    reservationRoomTypes: ROOM_TYPE.DOUBLE,
    numberOfUnits: 1,
  },
  data: {
    hotelAvailability: {
      hotelId: 'LONEUS',
      startDate: '2022-08-13',
      endDate: '2022-08-14',
      available: true,
      roomRates: [
        {
          ratePlanCode: 'FLEXRATE',
          roomTypes: [
            {
              roomType: 'DIS',
              adults: 2,
              children: 0,
              cotRequested: false,
              rooms: [
                {
                  pmsRoomType: 'BRFDBL',
                  silentSubstitution: false,
                  roomClass: 'ST',
                  cotAvailable: false,
                  roomPriceBreakdown: {
                    totalNetAmount: 115.33,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-08-13',
                        netPrice: 75.0,
                      },
                      {
                        date: '2022-08-14',
                        netPrice: 40.33,
                      },
                    ],
                  },
                  specialRequests: ['TW2S'],
                  softBundles: { isOptional: false, softBundleContent: [{ id: '123' }] },
                },
                {
                  pmsRoomType: 'FMTRPL',
                  silentSubstitution: false,
                  roomClass: 'ST',
                  cotAvailable: false,
                  roomPriceBreakdown: {
                    totalNetAmount: 116.33,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-08-13',
                        netPrice: 75.0,
                      },
                      {
                        date: '2022-08-14',
                        netPrice: 41.33,
                      },
                    ],
                  },
                  specialRequests: ['TWDS'],
                },
              ],
            },
            {
              roomType: 'FAM',
              adults: 2,
              children: 1,
              cotRequested: true,
              rooms: [
                {
                  pmsRoomType: 'FMTRPL',
                  silentSubstitution: false,
                  roomClass: 'ST',
                  cotAvailable: true,
                  roomPriceBreakdown: {
                    totalNetAmount: 117.33,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-08-13',
                        netPrice: 75.0,
                      },
                      {
                        date: '2022-08-14',
                        netPrice: 42.33,
                      },
                    ],
                  },
                  specialRequests: ['TRIP'],
                },
              ],
            },
          ],
        },
        {
          ratePlanCode: 'STANDARD',
          roomTypes: [
            {
              roomType: 'TWIN',
              adults: 2,
              children: 0,
              cotRequested: false,
              rooms: [
                {
                  pmsRoomType: 'TWINRM',
                  silentSubstitution: false,
                  roomClass: 'ST',
                  cotAvailable: false,
                  roomPriceBreakdown: {
                    totalNetAmount: 118.33,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-08-13',
                        netPrice: 75.0,
                      },
                      {
                        date: '2022-08-14',
                        netPrice: 43.33,
                      },
                    ],
                  },
                  specialRequests: ['TW2S'],
                },
                {
                  pmsRoomType: 'FMTRPL',
                  silentSubstitution: false,
                  roomClass: 'ST',
                  cotAvailable: false,
                  roomPriceBreakdown: {
                    totalNetAmount: 119.33,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-08-13',
                        netPrice: 75.0,
                      },
                      {
                        date: '2022-08-14',
                        netPrice: 44.33,
                      },
                    ],
                  },
                  specialRequests: ['TWDS'],
                },
              ],
            },
            {
              roomType: 'FAM',
              adults: 2,
              children: 1,
              cotRequested: true,
              rooms: [
                {
                  pmsRoomType: 'FMTRPL',
                  silentSubstitution: false,
                  roomClass: 'ST',
                  cotAvailable: true,
                  roomPriceBreakdown: {
                    totalNetAmount: 120.33,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-08-13',
                        netPrice: 75.0,
                      },
                      {
                        date: '2022-08-14',
                        netPrice: 45.33,
                      },
                    ],
                  },
                  specialRequests: ['TRIP'],
                },
              ],
            },
          ],
        },
      ],
    },
    ratesInformation: {
      rateClassifications: [
        {
          rateClassification: 'FLEXRATE',
          rateDescription:
            'Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival',
          rateName: 'Flex',
          rateOrder: '1',
          rateTags: [] as string[],
        },
        {
          rateClassification: 'STANDARD',
          rateDescription:
            'Pay now, non-refundable. Amendable check in date at the same hotel up to 1pm on the day of arrival',
          rateName: 'Standard',
          rateOrder: '5',
          rateTags: [] as string[],
        },
      ],
    },
    hotelInventory: {
      roomTypeInventories: [
        { availableCount: 12, code: 'TWINRM' },
        { availableCount: 2, code: 'LOWDBL' },
        { availableCount: 51, code: 'FMTRPL' },
        { availableCount: 48, code: 'FMQUAD' },
      ],
    },
  } as HIAvailabilityRates,
};

const mockMutationResponse = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  isSuccess: true,
  data: { createReservation: { basketReference: '123' } },
};

const mockedProps = {
  channel: BOOKING_CHANNEL.PI as Channel,
  variant: 'PI',
  hotelAvailabilityResponse: {
    isLoadingHotelAvailability: mockResponse.isLoading,
    isErrorHotelAvailability: mockResponse.isError,
    errorHotelAvailability: mockResponse.error,
    dataHotelAvailability: mockResponse.data,
  },
  globalConfigResponse: {
    isLoadingGlobalConfig: mockGlobalConfig.isLoading,
    isErrorGlobalConfig: mockGlobalConfig.isError,
    errorGlobalConfig: mockGlobalConfig.error,
    dataGlobalConfig: mockGlobalConfig.data,
  },
  queryClient: new QueryClient(),
  isHotelOpeningSoon: false,
  isParentAnalytics: true,
  arrival: '2023-04-14',
  departure: '2023-04-15',
  numberOfUnits: 1,
  numberOfNights: 1,
  isLessThanSm: true,
  isLessThanMd: true,
  isLessThanLg: false,
  isSilentSubstitution: true,
  mappedRoomLabels: {
    DIS: 'Accessible',
    DB: 'Double',
    FAM: 'Family',
    SB: 'Single',
    TWIN: 'Twin',
  },
  globalTranslationForRooms: {
    DIS: 'Accessible',
    DB: 'Double',
    FAM: 'Family',
    SB: 'Single',
    TWIN: 'Twin',
  },
  isSilentFeatureFlagEnabled: true,
  hasRateSelectorTitleDescription: true,
};

const mockUseFeatureSwitch = jest.fn().mockReturnValue(true);
const mockgetBasketLabel = jest.fn();
const mockScrolledPast = {
  value: false,
};
jest.mock('@whitbread-eos/utils', () => {
  const utils = jest.requireActual('@whitbread-eos/utils');

  return {
    ...utils,
    getCookie: (cookieName: string) => {
      if (cookieName === utils.BUNDLE_CHOICE) {
        return mockCookies.bundles;
      }
    },
    useScrolledPast: () => mockScrolledPast.value,
    formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
    useCustomLocale: () => mockCustomLocale(),
    useMutationRequest: () => mockMutationResponse,
    useQueryRequest: () => mockResponse,
    useFeatureToggle: jest.fn().mockReturnValue({
      kill_switch_pi_meta_search: true,
      release_pi_bb_ccui_choose_room_type: true,
      release_pi_bb_ccui_premier_plus_accessible_room: true,
      release_pi_promo_code_landing_page: false,
      release_pi_promo_code_site_wide: true,
      release_bb_promo_code_landing_page: true,
      release_bb_promo_code_site_wide: true,
    }),
    useFeatureSwitch: () => mockUseFeatureSwitch(),
    getBasketLabel: () => mockgetBasketLabel(),
    useStaticHotelInformation: () => ({
      brand: 'PI',
      hotelId: 'MANOLD',
      bookingFlow: {
        bookingFlowItems: [],
      },
      contactDetails: {
        email: 'manold@premier-inn.com',
      },
      accessibilityInfo: {
        header: 'Accessibility header',
      },
    }),
    CacheStorage: {
      create: jest.fn().mockImplementation(() => {
        console.warn('Mock CacheStorage.create called');
        return {
          setItem: jest.fn(),
          getItem: jest.fn(() => null), // Mock getItem to return null
          removeItem: jest.fn(),
        };
      }),
    },
  };
});

const mockUseRouter = jest.fn();

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('@whitbread-eos/molecules', () => ({
  ...jest.requireActual('@whitbread-eos/molecules'),
  Basket: () => <div data-testid="basket">Basket Component</div>,
}));

describe('soft bundles rates', () => {
  jest.mock('@whitbread-eos/utils', () => {
    const utils = jest.requireActual('@whitbread-eos/utils');

    return {
      ...utils,
      getCookie: (cookieName: string) => {
        if (cookieName === utils.BUNDLE_CHOICE) {
          return mockCookies.bundles;
        }
      },
      useScrolledPast: () => mockScrolledPast.value,
      formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
      useCustomLocale: () => mockCustomLocale(),
      useMutationRequest: () => mockMutationResponse,
      useQueryRequest: () => mockResponse,
      useFeatureToggle: jest.fn().mockReturnValue({
        kill_switch_pi_meta_search: true,
        release_pi_bb_ccui_choose_room_type: true,
        release_pi_bb_ccui_premier_plus_accessible_room: true,
        release_pi_promo_code_landing_page: false,
        release_pi_promo_code_site_wide: true,
        release_bb_promo_code_landing_page: true,
        release_bb_promo_code_site_wide: true,
      }),
      useFeatureSwitch: () => mockUseFeatureSwitch(),
      getBasketLabel: () => mockgetBasketLabel(),
      useStaticHotelInformation: () => ({
        brand: 'PI',
        hotelId: 'MANOLD',
        bookingFlow: {
          bookingFlowItems: [],
        },
        contactDetails: {
          email: 'manold@premier-inn.com',
        },
        accessibilityInfo: {
          header: 'Accessibility header',
        },
      }),
      CacheStorage: {
        create: jest.fn().mockImplementation(() => {
          console.warn('Mock CacheStorage.create called');
          return {
            setItem: jest.fn(),
            getItem: jest.fn(() => null), // Mock getItem to return null
            removeItem: jest.fn(),
          };
        }),
      },
    };
  });

  beforeEach(() => {
    jest.clearAllMocks;
    mockScrolledPast.value = false;
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });
    mockUseRouter.mockReturnValue({
      locale: 'gb',
      query: {
        INTTYP1: 'DB',
        slug: ['deutschland', 'berlin', 'berlin', 'berlin-alexanderplatz.html'],
      },
    });
    mockCookies.bundles = 'false';
  });

  it('should render a RateSelector with title, rates and basket with soft bundles', function () {
    const { getByTestId } = render(<RateSelector {...mockedProps} isSoftBundlesVisible={true} />);

    expect(getByTestId('basket')).toBeInTheDocument();
  });

  it('should render a RateSelector with soft bundles with lessThanLg', function () {
    mockScrolledPast.value = true;
    const { getByText } = render(
      <RateSelector {...mockedProps} isSoftBundlesVisible={true} isLessThanLg={true} />
    );

    expect(getByText('hoteldetails.rates.grid.title.roomAndRate')).toBeInTheDocument();
  });

  it('should render RateSelector promoBox', function () {
    mockScrolledPast.value = true;
    const { getByText } = render(<RateSelector {...mockedProps} isLessThanLg={true} />);

    expect(getByText('hoteldetails.rates.grid.title')).toBeInTheDocument();
  });

  it('should render RateSelector with lessThanLg', function () {
    const { getByText } = render(<RateSelector {...mockedProps} isLessThanLg={true} />);

    expect(getByText('hoteldetails.rates.grid.title')).toBeInTheDocument();
  });
});
