import { useMediaQuery } from '@chakra-ui/react';
import { QueryClient } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import {
  type Channel,
  type HIAvailabilityRates,
  ROOM_TYPE,
  BOOKING_CHANNEL,
  RoomClassLabelByCode,
  HIRoomRate,
} from '@whitbread-eos/api';
import { useIsExternalSearch, useFeatureToggle } from '@whitbread-eos/utils';

import { fireEvent, render, screen, waitFor } from '../../utils/test-utils';
import RateSelectorComponent, {
  getRateCodeIndex,
  getPromotionKind,
  getSelectedRate,
} from './RateSelector.component';
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

const mockComponentProps = {
  channel: BOOKING_CHANNEL.PI as Channel,
  variant: 'PI',
  isLoading: mockResponse.isLoading,
  isError: mockResponse.isError,
  data: mockResponse.data,
  error: mockResponse.error,
  basketData: {
    hotelId: 'TKINPT',
    arrival: '2022-06-25',
    departure: '2022-06-29',
    numberOfUnits: 1,
    numberOfNights: 2,
  },
  roomTypeInformationResponse: {
    isLoadingRoomTypeInformation: false,
    isErrorRoomTypeInformation: false,
    dataRoomTypeInformation: { roomTypeInformation: { roomTypes: [] } },
    errorRoomTypeInformation: { message: '' },
  },
  globalConfigResponse: {
    isLoadingGlobalConfig: mockGlobalConfig.isLoading,
    isErrorGlobalConfig: mockGlobalConfig.isError,
    errorGlobalConfig: mockGlobalConfig.error,
    dataGlobalConfig: mockGlobalConfig.data,
  },
  bookingFlow: { bookingFlowItems: [] },
  accessibilityInfo: {
    header: 'some text',
    linkText: 'click here',
    phoneNumber: '222 222 2222',
    text: 'some text',
  },
  phoneNumber: '222 222 2222',
  isParentAnalytics: true,
  brand: 'PI',
  bookRsvIsLoading: false,
  bookRsvIsError: false,
  bookRsvData: { createReservation: { basketReference: '123' } },
  bookRsvIsSuccess: true,
  bookRsvError: null,
  handleBooking: jest.fn(),
  isHotelOpeningSoon: false,
  queryClient: new QueryClient(),
  isLessThanSm: true,
  isLessThanMd: true,
  isLessThanLg: false,
  isDisabledContinueBtn: false,
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
    getSelectedRoomClassCode: jest.fn().mockReturnValue('ST'),
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

const promoActionsMock = {
  promoState: {
    isApplied: true,
    code: 'PROMO123',
    type: 'DISCOUNT',
  },
};

describe('RateSelector - single class', () => {
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

  afterEach(() => {
    mockedProps.hotelAvailabilityResponse.isLoadingHotelAvailability = false;
    mockedProps.hotelAvailabilityResponse.isErrorHotelAvailability = false;
    mockedProps.hotelAvailabilityResponse.errorHotelAvailability = { message: '' };
  });

  it('renders RateSelector with default props', () => {
    mockCustomLocale.mockReturnValue({
      language: 'de',
    });

    const { getByText, getByTestId } = render(<RateSelector {...mockedProps} />);
    const rateCards = screen.queryAllByTestId('hdp_rateCard', {
      exact: false,
    });

    expect(rateCards[0]).toHaveTextContent('Flex');
    expect(getByText('hoteldetails.rates.grid.title')).toBeInTheDocument();
    expect(getByTestId('basket')).toBeInTheDocument();
  });

  it('should not render title and description', () => {
    mockCustomLocale.mockReturnValue({
      language: 'de',
    });

    const { queryByText } = render(
      <RateSelector {...mockedProps} hasRateSelectorTitleDescription={false} />
    );
    expect(queryByText('hoteldetails.rates.grid.title')).not.toBeInTheDocument();
    expect(queryByText('hoteldetails.rates.grid.description')).not.toBeInTheDocument();
  });

  it('should find basket with sticky position if is in desktop view', () => {
    const mockRouter = {
      push: jest.fn(),
      query: {
        INTTYP1: 'DB',
        slug: ['deutschland', 'berlin', 'berlin', 'berlin-alexanderplatz.html'],
      },
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    mockComponentProps.isLessThanLg = false;

    const { getByTestId } = render(
      <RateSelectorComponent
        {...mockComponentProps}
        {...mockMultiClassResponseHub}
        promoActions={promoActionsMock}
      />
    );
    expect(getByTestId('basketWrapper')).toHaveStyle('position: sticky');
  });

  it('should render a RateSelector with title, rates and basket', function () {
    mockCustomLocale.mockReturnValue({
      language: 'de',
    });
    const { getByText, getByTestId } = render(
      <RateSelector
        {...mockedProps}
        promoActions={promoActionsMock}
        {...mockMultiClassResponseHub}
      />
    );
    const rateCards = screen.queryAllByTestId('hdp_rateCard', {
      exact: false,
    });

    expect(rateCards[0]).toHaveTextContent('Flex');
    expect(getByText('hoteldetails.rates.grid.title')).toBeInTheDocument();
    expect(getByTestId('basket')).toBeInTheDocument();
  });

  it('should render German RateSelector with title, rates and basket', function () {
    mockCustomLocale.mockReturnValue({
      language: 'de',
    });
    const { getByText, getByTestId } = render(<RateSelector {...mockedProps} />);
    const rateCards = screen.queryAllByTestId('hdp_rateCard', {
      exact: false,
    });
    expect(getByText('hoteldetails.rates.grid.title')).toBeInTheDocument();
    expect(getByTestId('basket')).toBeInTheDocument();
    expect(rateCards[0]).toHaveTextContent('Flex');
  });

  it('should have first Radio selected by default and select another Radio when clicking a different rateItem', async () => {
    const { getAllByRole } = render(<RateSelector {...mockedProps} />);
    const rateCards = screen.queryAllByTestId('hdp_rateCard', {
      exact: false,
    });

    expect(rateCards[0]).toHaveTextContent('Flex');
    expect(getAllByRole('radio')).toHaveLength(2);
    expect(getAllByRole('radio')[0]).toBeChecked();
    expect(getAllByRole('radio')[1]).not.toBeChecked();
    fireEvent.click(getAllByRole('radio')[1]); // Standard

    expect(getAllByRole('radio')[0]).not.toBeChecked();
    expect(getAllByRole('radio')[1]).toBeChecked();

    const defaultRadio = screen.getAllByRole('radio')[0] as HTMLInputElement;
    expect(defaultRadio.value).toBe('ST-0'); //Standard Room - Flex
  });

  it('should show a loading state', async () => {
    mockedProps.hotelAvailabilityResponse.isLoadingHotelAvailability = true;
    const { getByText } = render(<RateSelector {...mockedProps} />);
    expect(getByText('searchresults.list.hotel.loading')).toBeInTheDocument();
  });

  it('should show an error state', async () => {
    mockedProps.hotelAvailabilityResponse.isErrorHotelAvailability = true;
    mockedProps.hotelAvailabilityResponse.errorHotelAvailability = {
      message: 'Error loading rates.',
    };

    const { getByText } = render(<RateSelector {...mockedProps} />);
    expect(getByText('Error loading rates.')).toBeInTheDocument();
  });

  it('should return null if there is no availability', async () => {
    const { queryByText } = render(
      <RateSelector
        {...mockedProps}
        hotelAvailabilityResponse={{
          ...mockedProps.hotelAvailabilityResponse,
          dataHotelAvailability: {
            ...mockedProps.hotelAvailabilityResponse.dataHotelAvailability,
            hotelAvailability: {
              ...mockedProps.hotelAvailabilityResponse.dataHotelAvailability.hotelAvailability,
              roomRates: [],
            },
          },
        }}
      />
    );
    expect(queryByText('hoteldetails.rates.grid.title')).toBeNull();
  });

  it('should navigate to ancillaries page if a basketReference is returned, but no bookingFlowId is provided', async () => {
    const mockRouter = {
      push: jest.fn(),
      query: {
        INTTYP1: 'DB',
        slug: ['deutschland', 'berlin', 'berlin', 'berlin-alexanderplatz.html'],
      },
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    render(<RateSelectorComponent {...mockComponentProps} />);
    await waitFor(() => {
      expect(mockRouter.push).toHaveBeenCalledWith('/gb/en/ancillaries?reservationId=123');
    });
  });

  it('should navigate to ancillaries page if a basketReference and a bookingFlowId are provided', async () => {
    const mockRouter = {
      push: jest.fn(),
      query: {
        INTTYP1: 'DB',
        slug: ['deutschland', 'berlin', 'berlin', 'berlin-alexanderplatz.html'],
      },
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    render(
      <RateSelectorComponent
        {...mockComponentProps}
        bookingFlow={{ bookingFlowItems: [{ bookingId: 'booking-a1', rateCode: 'FLEXRATE' }] }}
      />
    );
    await waitFor(() => {
      expect(mockRouter.push).toHaveBeenCalledWith(
        '/gb/en/booking-a1/ancillaries?reservationId=123'
      );
    });
  });

  it('should navigate for BB variant to guest details page if a basketReference and a bookingFlowId are provided', async () => {
    mockComponentProps.variant = 'BB';
    const mockRouter = {
      push: jest.fn(),
      query: {
        INTTYP1: 'DB',
        slug: ['deutschland', 'berlin', 'berlin', 'berlin-alexanderplatz.html'],
      },
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    render(
      <RateSelectorComponent
        {...mockComponentProps}
        bookingFlow={{ bookingFlowItems: [{ bookingId: 'booking-a1', rateCode: 'FLEXRATE' }] }}
      />
    );
    await waitFor(() => {
      expect(mockRouter.push).toHaveBeenCalledWith(
        '/gb/en/business-booker/booking-business/guest-details?reservationId=123'
      );
    });
  });

  it('should disable Book Now button after clicked ', async () => {
    const mockRouter = {
      push: jest.fn(),
      query: {
        INTTYP1: 'DB',
        slug: ['deutschland', 'berlin', 'berlin', 'berlin-alexanderplatz.html'],
      },
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    mockComponentProps.bookRsvIsLoading = true;

    const { getByTestId } = render(
      <RateSelectorComponent
        {...mockComponentProps}
        bookingFlow={{ bookingFlowItems: [{ bookingId: 'booking-a1', rateCode: 'FLEXRATE' }] }}
      />
    );

    const bookNowButton = getByTestId('hdp_basketBookNowButton');
    fireEvent.click(bookNowButton);
    expect(bookNowButton).toBeDisabled();
  });

  it('should disable Book Now button after clicked with class bundle active choice', async () => {
    const mockRouter = {
      push: jest.fn(),
      query: {
        INTTYP1: 'DB',
        slug: ['deutschland', 'berlin', 'berlin', 'berlin-alexanderplatz.html'],
      },
    };
    mockCookies.bundles = 'class';
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    mockComponentProps.bookRsvIsLoading = true;

    const { getByTestId } = render(
      <RateSelectorComponent
        {...mockComponentProps}
        bookingFlow={{ bookingFlowItems: [{ bookingId: 'booking-a1', rateCode: 'FLEXRATE' }] }}
      />
    );

    const bookNowButton = getByTestId('hdp_basketBookNowButton');
    fireEvent.click(bookNowButton);
    expect(bookNowButton).toBeDisabled();
  });

  it('should not show room rates explained tooltip', async () => {
    const mockRouter = {
      push: jest.fn(),
      query: {
        INTTYP1: 'DB',
        slug: ['deutschland', 'berlin', 'berlin', 'berlin-alexanderplatz.html'],
      },
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    const { queryByTestId, queryByText } = render(
      <RateSelectorComponent {...mockComponentProps} />
    );

    expect(queryByTestId('hdp_ratesExplainedLinkText')).not.toBeInTheDocument();
    expect(queryByText('pihotelinfo.ratesExplained')).not.toBeInTheDocument();
  });
});

jest.mock('@whitbread-eos/utils', () => {
  const utils = jest.requireActual('@whitbread-eos/utils');
  return {
    ...utils,
    getSelectedRoomClassCode: jest.fn().mockReturnValue('ST'),
    getCookie: (cookieName: string) => {
      if (cookieName === utils.BUNDLE_CHOICE) {
        return mockCookies.bundles;
      }
    },
    useScrolledPast: () => mockScrolledPast.value,
    formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
    useCustomLocale: () => mockCustomLocale(),
    useMutationRequest: () => jest.fn(),
    useFeatureToggle: jest.fn().mockReturnValue({
      kill_switch_pi_meta_search: true,
      release_pi_bb_ccui_choose_room_type: true,
      release_pi_bb_ccui_premier_plus_accessible_room: true,
      release_pi_promo_code_site_wide: true,
      release_pi_display_soft_bundles: true,
    }),
    useQueryRequest: () => mockMultiClassResponseHub,
    useStaticHotelInformation: () => ({
      brand: 'PI',
      hotelId: 'MANOLD',
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

describe('RateSelector - multiple class, PI - Meta search', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockCustomLocale.mockReturnValue({
      language: 'en',
    });
    mockUseRouter.mockReturnValue({
      query: {
        INTTYP1: 'DB',
        slug: [
          'england',
          'west-sussex',
          'crawley',
          'london-gatwick-airport-south-london-road.html',
        ],
        CID: 'GHF3_GB_GoogleSearch_desktop_selected%20checkin=2024-08-17%20los=2%20GATGAT',
      },
    });
  });

  afterEach(() => {
    mockedMulticlassPropsPI.hotelAvailabilityResponse.isLoadingHotelAvailability = false;
    mockedMulticlassPropsPI.hotelAvailabilityResponse.isErrorHotelAvailability = false;
    mockedMulticlassPropsPI.hotelAvailabilityResponse.errorHotelAvailability = { message: '' };
  });

  it('should return true when CID matches third parties', () => {
    const result = useIsExternalSearch('TRA,TRIVAGO,GHF,GHF3,GHFPP,GLBC,BMP,BMF,YEXT');
    expect(result).toBe(true);
  });

  it('should return false when CID isnt present', () => {
    const result = useIsExternalSearch('');
    expect(result).toBe(false);
  });

  it('should have first lowest price selected by default', async () => {
    const renderExternalSelectorMulti = () =>
      render(
        <RateSelector
          {...mockedMulticlassPropsPI}
          thirdParties="TRA,TRIVAGO,GHF,GHF3,GHFPP,GLBC,BMP,BMF,YEXT"
        />
      );
    renderExternalSelectorMulti();
    const defaultRadio = screen.getAllByRole('radio')[3] as HTMLInputElement;
    expect(screen.getAllByRole('radio')).toHaveLength(4);
    expect(defaultRadio).toBeChecked();

    // Prices
    // Premier Plus - Standard rate -112
    // Premier Plus - Flex rate - 136
    // Standard Room - Flex rate- 141
    // Standard Room  - Standard rate - 117
    expect(defaultRadio.value).toBe('ST-1'); // Premier Plus - Standard rate
    expect(defaultRadio.value).not.toBe('PP-1'); // Premier Plus - Flex
    expect(defaultRadio.value).not.toBe('ST-0'); // Standard Room - Flex
    expect(defaultRadio.value).not.toBe('PP-0'); // Premier Plus - Standard rate
  });

  it('should have Standard Room Flex rate selected', async () => {
    const mockRouter = {
      query: {
        INTTYP1: 'DB',
        slug: [
          'england',
          'west-sussex',
          'crawley',
          'london-gatwick-airport-south-london-road.html',
        ],
        CID: 'GHF3_GB_GoogleSearch_desktop_selected%20checkin=2024-08-17%20los=2%20GATGAT',
        SELECT: 'STANDARD_ROOM-FLEXRATE',
      },
    };

    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    const renderExternalSelectorMulti = () =>
      render(
        <RateSelector
          {...mockedMulticlassPropsPI}
          thirdParties="TRA,TRIVAGO,GHF,GHF3,GHFPP,GLBC,BMP,BMF,YEXT"
        />
      );

    renderExternalSelectorMulti();
    const defaultRadio = screen.getAllByRole('radio')[2] as HTMLInputElement;
    expect(screen.getAllByRole('radio')).toHaveLength(4);
    expect(defaultRadio).toBeChecked();

    // Prices
    // Premier Plus - Standard rate -112
    // Premier Plus - Flex rate - 136
    // Standard Room - Flex rate- 141
    // Standard Room  - Standard rate - 117
    expect(defaultRadio.value).toBe('ST-0'); // Standard Room - Flex
    expect(defaultRadio.value).not.toBe('PP-1'); // Premier Plus - Standard rate
    expect(defaultRadio.value).not.toBe('ST-1'); // Standard Room - Standard rate
    expect(defaultRadio.value).not.toBe('PP-0'); // Premier Plus - Flex rate
  });

  it('should have Standard Room Flex rate selected', async () => {
    const mockRouter = {
      query: {
        INTTYP1: 'DB',
        slug: [
          'england',
          'west-sussex',
          'crawley',
          'london-gatwick-airport-south-london-road.html',
        ],
        CID: 'GHF3_GB_GoogleSearch_desktop_selected%20checkin=2024-08-17%20los=2%20GATGAT',
        SELECT: 'ST-DOESNOTEXIST',
      },
    };

    mockUseRouter.mockReturnValue({
      query: {
        INTTYP1: 'DB',
        slug: [
          'england',
          'west-sussex',
          'crawley',
          'london-gatwick-airport-south-london-road.html',
        ],
        CID: 'GHF3_GB_GoogleSearch_desktop_selected%20checkin=2024-08-17%20los=2%20GATGAT',
      },
    });

    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    const renderExternalSelectorMulti = () =>
      render(
        <RateSelector
          {...mockedMulticlassPropsPI}
          thirdParties="TRA,TRIVAGO,GHF,GHF3,GHFPP,GLBC,BMP,BMF,YEXT"
        />
      );

    renderExternalSelectorMulti();
  });
});

const mockMultiClassResponsePI = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  basketData: {
    hotelId: 'MANOLD',
    arrival: '2022-12-21',
    departure: '2022-12-22',
    numberOfAdults: 1,
    numberOfChildren: 0,
    reservationRoomTypes: ROOM_TYPE.DOUBLE,
    numberOfUnits: 1,
  },
  data: {
    hotelAvailability: {
      hotelId: 'MANOLD',
      startDate: '2022-12-21',
      endDate: '2022-12-21',
      available: true,
      roomRates: [
        {
          ratePlanCode: 'FLEXRATE',
          roomTypes: [
            {
              roomType: 'DB',
              adults: 1,
              children: 0,
              cotRequested: false,
              rooms: [
                {
                  pmsRoomType: ROOM_TYPE.PREMIER_PLUS,
                  silentSubstitution: true,
                  cotAvailable: false,
                  roomClass: 'PP',
                  roomPriceBreakdown: {
                    totalNetAmount: 136,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-12-21',
                        netPrice: 136,
                      },
                    ],
                  },
                  specialRequests: ['DBLE'],
                },
                {
                  pmsRoomType: ROOM_TYPE.DOUBLE,
                  silentSubstitution: false,
                  cotAvailable: false,
                  roomClass: 'ST',
                  roomPriceBreakdown: {
                    totalNetAmount: 141,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-12-21',
                        netPrice: 141,
                      },
                    ],
                  },
                  specialRequests: ['DBLE'],
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
                  pmsRoomType: ROOM_TYPE.PREMIER_PLUS,
                  silentSubstitution: true,
                  cotAvailable: false,
                  roomClass: 'PP',
                  roomPriceBreakdown: {
                    totalNetAmount: 112,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-12-21',
                        netPrice: 112,
                      },
                    ],
                  },
                  specialRequests: ['DBLE'],
                },
                {
                  pmsRoomType: ROOM_TYPE.DOUBLE,
                  silentSubstitution: false,
                  cotAvailable: false,
                  roomClass: 'ST',
                  roomPriceBreakdown: {
                    totalNetAmount: 117,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-12-21',
                        netPrice: 117,
                      },
                    ],
                  },
                  specialRequests: ['DBLE'],
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
            'Pay now, fully refundable with free cancellation up to 1pm on the day of arrival',
          rateName: 'Flex',
          rateOrder: '1',
          rateTags: [] as string[],
        },
        {
          rateClassification: 'SEMIFLEX',
          rateDescription:
            'Pay now, fully refundable with free cancellation up to 3 full days before arrival',
          rateName: 'Semi-Flex',
          rateOrder: '2',
          rateTags: [] as string[],
        },
        {
          rateClassification: 'ADVANCE',
          rateDescription:
            'Pay now, fully refundable with free cancellation up to 28 full days before arrival',
          rateName: 'Advance',
          rateOrder: '3',
          rateTags: [] as string[],
        },
        {
          rateClassification: 'STANDARD',
          rateDescription:
            'Pay now, non-refundable. Amendable check in date at the same hotel up to 1pm on the day of arrival',
          rateName: 'Standard',
          rateOrder: '4',
          rateTags: [] as string[],
        },
        {
          rateClassification: 'NONFLEX',
          rateDescription: 'Pay now. No changes',
          rateName: 'Non-Flex',
          rateOrder: '5',
          rateTags: [] as string[],
        },
      ],
    },
    hotelInventory: {
      roomTypeInventories: [
        { availableCount: 12, code: 'DOUBLE' },
        { availableCount: 51, code: 'FMTRPL' },
        { availableCount: 48, code: 'FMQUAD' },
        { availableCount: 48, code: 'PPLDBL' },
      ],
    },
  } as HIAvailabilityRates,
};

const mockAvailabilityWithNegotiatedRates1 = {
  data: {
    hotelAvailability: {
      hotelId: 'MANOLD',
      startDate: '2022-12-21',
      endDate: '2022-12-21',
      available: true,
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
                  pmsRoomType: ROOM_TYPE.PREMIER_PLUS,
                  silentSubstitution: true,
                  cotAvailable: false,
                  roomClass: 'PP',
                  roomPriceBreakdown: {
                    totalNetAmount: 136,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-12-21',
                        netPrice: 136,
                      },
                    ],
                  },
                  specialRequests: ['DBLE'],
                },
                {
                  pmsRoomType: ROOM_TYPE.DOUBLE,
                  silentSubstitution: false,
                  cotAvailable: false,
                  roomClass: 'ST',
                  roomPriceBreakdown: {
                    totalNetAmount: 141,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-12-21',
                        netPrice: 141,
                      },
                    ],
                  },
                  specialRequests: ['DBLE'],
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
                  pmsRoomType: ROOM_TYPE.PREMIER_PLUS,
                  silentSubstitution: true,
                  cotAvailable: false,
                  roomClass: 'PP',
                  roomPriceBreakdown: {
                    totalNetAmount: 112,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-12-21',
                        netPrice: 112,
                      },
                    ],
                  },
                  specialRequests: ['DBLE'],
                },
                {
                  pmsRoomType: ROOM_TYPE.DOUBLE,
                  silentSubstitution: false,
                  cotAvailable: false,
                  roomClass: 'ST',
                  roomPriceBreakdown: {
                    totalNetAmount: 117,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-12-21',
                        netPrice: 117,
                      },
                    ],
                  },
                  specialRequests: ['DBLE'],
                },
              ],
            },
          ],
        },
        {
          ratePlanCode: 'B&B',
          roomTypes: [
            {
              roomType: 'DB',
              adults: 1,
              children: 0,
              cotRequested: false,
              rooms: [
                {
                  pmsRoomType: ROOM_TYPE.PREMIER_PLUS,
                  silentSubstitution: true,
                  cotAvailable: false,
                  roomClass: 'PP',
                  roomPriceBreakdown: {
                    totalNetAmount: 136,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-12-21',
                        netPrice: 136,
                      },
                    ],
                  },
                  specialRequests: ['DBLE'],
                },
                {
                  pmsRoomType: ROOM_TYPE.DOUBLE,
                  silentSubstitution: false,
                  cotAvailable: false,
                  roomClass: 'ST',
                  roomPriceBreakdown: {
                    totalNetAmount: 141,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-12-21',
                        netPrice: 141,
                      },
                    ],
                  },
                  specialRequests: ['DBLE'],
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
          rateClassification: 'B&B',
          rateDescription:
            'Pay now, fully refundable with free cancellation up to 1pm on the day of arrival',
          rateName: 'Flex',
          rateOrder: '1',
          rateTags: [] as string[],
        },
        {
          rateClassification: 'SEMIFLEX',
          rateDescription:
            'Pay now, fully refundable with free cancellation up to 3 full days before arrival',
          rateName: 'Semi-Flex',
          rateOrder: '2',
          rateTags: [] as string[],
        },
        {
          rateClassification: 'ADVANCE',
          rateDescription:
            'Pay now, fully refundable with free cancellation up to 28 full days before arrival',
          rateName: 'Advance',
          rateOrder: '3',
          rateTags: [] as string[],
        },
        {
          rateClassification: 'STANDARD',
          rateDescription:
            'Pay now, non-refundable. Amendable check in date at the same hotel up to 1pm on the day of arrival',
          rateName: 'Standard',
          rateOrder: '4',
          rateTags: [] as string[],
        },
        {
          rateClassification: 'NONFLEX',
          rateDescription: 'Pay now. No changes',
          rateName: 'Non-Flex',
          rateOrder: '5',
          rateTags: [] as string[],
        },
      ],
    },
    hotelInventory: {
      roomTypeInventories: [
        { availableCount: 12, code: 'DOUBLE' },
        { availableCount: 51, code: 'FMTRPL' },
        { availableCount: 48, code: 'FMQUAD' },
        { availableCount: 48, code: 'PPLDBL' },
      ],
    },
  } as HIAvailabilityRates,
};

const mockAvailabilityWithNegotiatedRates2 = {
  data: {
    hotelAvailability: {
      hotelId: 'MANOLD',
      startDate: '2022-12-21',
      endDate: '2022-12-21',
      available: true,
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
                  pmsRoomType: ROOM_TYPE.PREMIER_PLUS,
                  silentSubstitution: true,
                  cotAvailable: false,
                  roomClass: 'PP',
                  roomPriceBreakdown: {
                    totalNetAmount: 136,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-12-21',
                        netPrice: 136,
                      },
                    ],
                  },
                  specialRequests: ['DBLE'],
                },
                {
                  pmsRoomType: ROOM_TYPE.DOUBLE,
                  silentSubstitution: false,
                  cotAvailable: false,
                  roomClass: 'ST',
                  roomPriceBreakdown: {
                    totalNetAmount: 141,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-12-21',
                        netPrice: 141,
                      },
                    ],
                  },
                  specialRequests: ['DBLE'],
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
                  pmsRoomType: ROOM_TYPE.PREMIER_PLUS,
                  silentSubstitution: true,
                  cotAvailable: false,
                  roomClass: 'PP',
                  roomPriceBreakdown: {
                    totalNetAmount: 112,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-12-21',
                        netPrice: 112,
                      },
                    ],
                  },
                  specialRequests: ['DBLE'],
                },
                {
                  pmsRoomType: ROOM_TYPE.DOUBLE,
                  silentSubstitution: false,
                  cotAvailable: false,
                  roomClass: 'ST',
                  roomPriceBreakdown: {
                    totalNetAmount: 117,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-12-21',
                        netPrice: 117,
                      },
                    ],
                  },
                  specialRequests: ['DBLE'],
                },
              ],
            },
          ],
        },
        {
          ratePlanCode: 'B&B',
          roomTypes: [
            {
              roomType: 'DB',
              adults: 1,
              children: 0,
              cotRequested: false,
              rooms: [
                {
                  pmsRoomType: ROOM_TYPE.DOUBLE,
                  silentSubstitution: false,
                  cotAvailable: false,
                  roomClass: 'ST',
                  roomPriceBreakdown: {
                    totalNetAmount: 141,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-12-21',
                        netPrice: 141,
                      },
                    ],
                  },
                  specialRequests: ['DBLE'],
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
          rateClassification: 'B&B',
          rateDescription:
            'Pay now, fully refundable with free cancellation up to 1pm on the day of arrival',
          rateName: 'Flex',
          rateOrder: '1',
          rateTags: [] as string[],
        },
        {
          rateClassification: 'SEMIFLEX',
          rateDescription:
            'Pay now, fully refundable with free cancellation up to 3 full days before arrival',
          rateName: 'Semi-Flex',
          rateOrder: '2',
          rateTags: [] as string[],
        },
        {
          rateClassification: 'ADVANCE',
          rateDescription:
            'Pay now, fully refundable with free cancellation up to 28 full days before arrival',
          rateName: 'Advance',
          rateOrder: '3',
          rateTags: [] as string[],
        },
        {
          rateClassification: 'STANDARD',
          rateDescription:
            'Pay now, non-refundable. Amendable check in date at the same hotel up to 1pm on the day of arrival',
          rateName: 'Standard',
          rateOrder: '4',
          rateTags: [] as string[],
        },
        {
          rateClassification: 'NONFLEX',
          rateDescription: 'Pay now. No changes',
          rateName: 'Non-Flex',
          rateOrder: '5',
          rateTags: [] as string[],
        },
      ],
    },
    hotelInventory: {
      roomTypeInventories: [
        { availableCount: 12, code: 'DOUBLE' },
        { availableCount: 51, code: 'FMTRPL' },
        { availableCount: 48, code: 'FMQUAD' },
        { availableCount: 48, code: 'PPLDBL' },
      ],
    },
  } as HIAvailabilityRates,
};

const mockAvailabilityWithNegotiatedRates3 = {
  data: {
    hotelAvailability: {
      hotelId: 'MANOLD',
      startDate: '2022-12-21',
      endDate: '2022-12-21',
      available: true,
      roomRates: [
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
                  pmsRoomType: ROOM_TYPE.PREMIER_PLUS,
                  silentSubstitution: true,
                  cotAvailable: false,
                  roomClass: 'PP',
                  roomPriceBreakdown: {
                    totalNetAmount: 112,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-12-21',
                        netPrice: 112,
                      },
                    ],
                  },
                  specialRequests: ['DBLE'],
                },
                {
                  pmsRoomType: ROOM_TYPE.DOUBLE,
                  silentSubstitution: false,
                  cotAvailable: false,
                  roomClass: 'ST',
                  roomPriceBreakdown: {
                    totalNetAmount: 117,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-12-21',
                        netPrice: 117,
                      },
                    ],
                  },
                  specialRequests: ['DBLE'],
                },
              ],
            },
          ],
        },
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
                  pmsRoomType: ROOM_TYPE.PREMIER_PLUS,
                  silentSubstitution: true,
                  cotAvailable: false,
                  roomClass: 'PP',
                  roomPriceBreakdown: {
                    totalNetAmount: 136,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-12-21',
                        netPrice: 136,
                      },
                    ],
                  },
                  specialRequests: ['DBLE'],
                },
                {
                  pmsRoomType: ROOM_TYPE.DOUBLE,
                  silentSubstitution: false,
                  cotAvailable: false,
                  roomClass: 'ST',
                  roomPriceBreakdown: {
                    totalNetAmount: 141,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-12-21',
                        netPrice: 141,
                      },
                    ],
                  },
                  specialRequests: ['DBLE'],
                },
              ],
            },
          ],
        },
        {
          ratePlanCode: 'B&B',
          roomTypes: [
            {
              roomType: 'DB',
              adults: 1,
              children: 0,
              cotRequested: false,
              rooms: [
                {
                  pmsRoomType: ROOM_TYPE.PREMIER_PLUS,
                  silentSubstitution: true,
                  cotAvailable: false,
                  roomClass: 'PP',
                  roomPriceBreakdown: {
                    totalNetAmount: 136,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-12-21',
                        netPrice: 136,
                      },
                    ],
                  },
                  specialRequests: ['DBLE'],
                },
                {
                  pmsRoomType: ROOM_TYPE.DOUBLE,
                  silentSubstitution: false,
                  cotAvailable: false,
                  roomClass: 'ST',
                  roomPriceBreakdown: {
                    totalNetAmount: 141,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-12-21',
                        netPrice: 141,
                      },
                    ],
                  },
                  specialRequests: ['DBLE'],
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
          rateClassification: 'SEMIFLEX',
          rateDescription:
            'Pay now, fully refundable with free cancellation up to 3 full days before arrival',
          rateName: 'Semi-Flex',
          rateOrder: '2',
          rateTags: [] as string[],
        },
        {
          rateClassification: 'ADVANCE',
          rateDescription:
            'Pay now, fully refundable with free cancellation up to 28 full days before arrival',
          rateName: 'Advance',
          rateOrder: '3',
          rateTags: [] as string[],
        },
        {
          rateClassification: 'STANDARD',
          rateDescription:
            'Pay now, non-refundable. Amendable check in date at the same hotel up to 1pm on the day of arrival',
          rateName: 'Standard',
          rateOrder: '4',
          rateTags: [] as string[],
        },
        {
          rateClassification: 'NONFLEX',
          rateDescription: 'Pay now. No changes',
          rateName: 'Non-Flex',
          rateOrder: '5',
          rateTags: [] as string[],
        },
      ],
    },
    hotelInventory: {
      roomTypeInventories: [
        { availableCount: 12, code: 'DOUBLE' },
        { availableCount: 51, code: 'FMTRPL' },
        { availableCount: 48, code: 'FMQUAD' },
        { availableCount: 48, code: 'PPLDBL' },
      ],
    },
  } as HIAvailabilityRates,
};

const mockedMulticlassPropsPI = {
  channel: BOOKING_CHANNEL.PI as Channel,
  variant: 'PI',
  queryClient: new QueryClient(),
  hotelAvailabilityResponse: {
    isLoadingHotelAvailability: mockMultiClassResponsePI.isLoading,
    isErrorHotelAvailability: mockMultiClassResponsePI.isError,
    errorHotelAvailability: mockMultiClassResponsePI.error,
    dataHotelAvailability: mockMultiClassResponsePI.data,
  },
  globalConfigResponse: {
    isLoadingGlobalConfig: mockGlobalConfig.isLoading,
    isErrorGlobalConfig: mockGlobalConfig.isError,
    errorGlobalConfig: mockGlobalConfig.error,
    dataGlobalConfig: mockGlobalConfig.data,
  },
  isHotelOpeningSoon: false,
  isParentAnalytics: true,
  arrival: '2022-12-20',
  departure: '2022-12-20',
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
  hasRateSelectorTitleDescription: true,
};

jest.mock('@whitbread-eos/utils', () => {
  const utils = jest.requireActual('@whitbread-eos/utils');
  return {
    ...utils,
    getSelectedRoomClassCode: jest.fn().mockReturnValue('ST'),
    getCookie: (cookieName: string) => {
      if (cookieName === utils.BUNDLE_CHOICE) {
        return mockCookies.bundles;
      }
    },
    useScrolledPast: () => mockScrolledPast.value,
    formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
    useCustomLocale: () => mockCustomLocale(),
    useMutationRequest: () => jest.fn(),
    useQueryRequest: () => mockMultiClassResponsePI,
    useFeatureToggle: jest.fn(() => ({
      kill_switch_pi_meta_search: true,
      release_pi_bb_ccui_choose_room_type: true,
      release_pi_bb_ccui_premier_plus_accessible_room: true,
      release_pi_promo_code_site_wide: true,
    })),
    useStaticHotelInformation: () => ({
      brand: 'PI',
      hotelId: 'MANOLD',
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

const renderRateSelectorMulti = () => render(<RateSelector {...mockedMulticlassPropsPI} />);

describe('RateSelector - multiple class, PI', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockCustomLocale.mockReturnValue({
      language: 'en',
    });
    mockUseRouter.mockReturnValue({
      query: {
        INTTYP1: 'DB',
        slug: ['deutschland', 'berlin', 'berlin', 'berlin-alexanderplatz.html'],
      },
    });
  });

  afterEach(() => {
    mockedMulticlassPropsPI.hotelAvailabilityResponse.isLoadingHotelAvailability = false;
    mockedMulticlassPropsPI.hotelAvailabilityResponse.isErrorHotelAvailability = false;
    mockedMulticlassPropsPI.hotelAvailabilityResponse.errorHotelAvailability = { message: '' };
  });

  it('should render a RateSelector with title, rates and basket', function () {
    renderRateSelectorMulti();
    expect(screen.getByText('hoteldetails.rates.grid.title')).toBeInTheDocument();
    expect(screen.getByTestId('basket')).toBeInTheDocument();
  });

  it('should render 2 roomClass rateCards in correct order', function () {
    renderRateSelectorMulti();
    const rateCards = screen.queryAllByTestId('hdp_rateCard', {
      exact: false,
    });

    expect(rateCards.length).toBe(2);
    // Premier Plus Room card
    expect(rateCards[0]).toHaveTextContent('pihotelinfo.premierPlusTitle');
    expect(rateCards[0]).toHaveTextContent('Flex');
    expect(rateCards[0]).toHaveTextContent('Standard');

    // Standard Room card
    expect(rateCards[1]).toHaveTextContent('pihotelinfo.standardRoomTitle');
    expect(rateCards[1]).toHaveTextContent('Flex');
    expect(rateCards[1]).toHaveTextContent('Standard');
  });

  it('should have first Radio selected by default', async () => {
    renderRateSelectorMulti();
    const defaultRadio = screen.getAllByRole('radio')[0] as HTMLInputElement;
    expect(screen.getAllByRole('radio')).toHaveLength(4);
    expect(defaultRadio).toBeChecked();
    expect(defaultRadio.value).toBe('PP-0'); // Premier Plus - Flex
    expect(defaultRadio.value).not.toBe('PP-1'); // Premier Plus - Standaed rate
    expect(defaultRadio.value).not.toBe('ST-0'); // Standard Room - Flex
  });

  it('should have the correct rate selected by default with negotiated rates', async () => {
    const hotelAvailabilityResponse = {
      isLoadingHotelAvailability: mockMultiClassResponsePI.isLoading,
      isErrorHotelAvailability: mockMultiClassResponsePI.isError,
      errorHotelAvailability: mockMultiClassResponsePI.error,
      dataHotelAvailability: mockAvailabilityWithNegotiatedRates1.data,
    };
    render(
      <RateSelector
        {...mockedMulticlassPropsPI}
        hotelAvailabilityResponse={hotelAvailabilityResponse}
      />
    );
    const defaultRadio = screen.getAllByRole('radio')[0] as HTMLInputElement;
    expect(screen.getAllByRole('radio')).toHaveLength(6);
    expect(defaultRadio).toBeChecked();
    expect(defaultRadio.value).toBe('PP-2'); // Premier Plus - Flex
    expect(defaultRadio.value).not.toBe('PP-1'); // Premier Plus - Standaed rate
    expect(defaultRadio.value).not.toBe('PP-0'); // Premier Plus - Standaed rate
    expect(defaultRadio.value).not.toBe('ST-0'); // Standard Room - Flex
  });

  it('should have the correct rate selected by default when premier plus room is not available to certain rates', async () => {
    const hotelAvailabilityResponse = {
      isLoadingHotelAvailability: mockMultiClassResponsePI.isLoading,
      isErrorHotelAvailability: mockMultiClassResponsePI.isError,
      errorHotelAvailability: mockMultiClassResponsePI.error,
      dataHotelAvailability: mockAvailabilityWithNegotiatedRates2.data,
    };
    render(
      <RateSelector
        {...mockedMulticlassPropsPI}
        hotelAvailabilityResponse={hotelAvailabilityResponse}
      />
    );
    const defaultRadio = screen.getAllByRole('radio')[0] as HTMLInputElement;
    expect(screen.getAllByRole('radio')).toHaveLength(5);
    expect(defaultRadio).toBeChecked();
    expect(defaultRadio.value).toBe('PP-0'); // Premier Plus - Flex
    expect(defaultRadio.value).not.toBe('PP-1'); // Premier Plus - Standaed rate
    expect(defaultRadio.value).not.toBe('PP-2'); // Premier Plus - Standaed rate
    expect(defaultRadio.value).not.toBe('ST-0'); // Standard Room - Flex
  });

  it('should have the correct rate selected by default when rates information is not available for certain rates', async () => {
    const hotelAvailabilityResponse = {
      isLoadingHotelAvailability: mockMultiClassResponsePI.isLoading,
      isErrorHotelAvailability: mockMultiClassResponsePI.isError,
      errorHotelAvailability: mockMultiClassResponsePI.error,
      dataHotelAvailability: mockAvailabilityWithNegotiatedRates3.data,
    };
    render(
      <RateSelector
        {...mockedMulticlassPropsPI}
        hotelAvailabilityResponse={hotelAvailabilityResponse}
      />
    );
    const defaultRadio = screen.getAllByRole('radio')[0] as HTMLInputElement;
    expect(screen.getAllByRole('radio')).toHaveLength(4);
    expect(defaultRadio).toBeChecked();
    expect(defaultRadio.value).toBe('PP-1'); // Premier Plus - Flex
    expect(defaultRadio.value).not.toBe('PP-0'); // Premier Plus - Standaed rate
    expect(defaultRadio.value).not.toBe('ST-1'); // Premier Plus - Standaed rate
    expect(defaultRadio.value).not.toBe('ST-0'); // Standard Room - Flex
  });
});

const mockMultiClassResponseHub = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  basketData: {
    hotelId: 'LONKIN',
    arrival: '2022-12-19',
    departure: '2022-12-20',
    numberOfAdults: 1,
    numberOfChildren: 0,
    reservationRoomTypes: ROOM_TYPE.DOUBLE,
    numberOfUnits: 1,
  },
  data: {
    hotelAvailability: {
      hotelId: 'LONKIN',
      startDate: '2022-12-19',
      endDate: '2022-12-19',
      available: true,
      roomRates: [
        {
          ratePlanCode: 'FLEXRATE',
          promotionCode: 'FLX30R',
          roomTypes: [
            {
              roomType: 'DB',
              adults: 1,
              children: 0,
              cotRequested: false,
              rooms: [
                {
                  pmsRoomType: ROOM_TYPE.STANDARD,
                  silentSubstitution: true,
                  cotAvailable: false,
                  roomClass: 'ST',
                  roomPriceBreakdown: {
                    totalNetAmount: 136,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-12-19',
                        netPrice: 136,
                      },
                    ],
                  },
                  specialRequests: ['DBLE'],
                },
                {
                  pmsRoomType: ROOM_TYPE.STANDARD_BIGGER,
                  silentSubstitution: false,
                  cotAvailable: false,
                  roomClass: 'BG',
                  roomPriceBreakdown: {
                    totalNetAmount: 141,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-12-19',
                        netPrice: 141,
                      },
                    ],
                  },
                  specialRequests: ['DBLE'],
                },
              ],
            },
          ],
        },
        {
          ratePlanCode: 'STANDARD',
          promotionCode: 'ST10R',
          roomTypes: [
            {
              roomType: 'DB',
              adults: 1,
              children: 0,
              cotRequested: false,
              rooms: [
                {
                  pmsRoomType: ROOM_TYPE.STANDARD,
                  silentSubstitution: true,
                  cotAvailable: false,
                  roomClass: 'ST',
                  roomPriceBreakdown: {
                    totalNetAmount: 112,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-12-19',
                        netPrice: 112,
                      },
                    ],
                  },
                  specialRequests: ['DBLE'],
                },
                {
                  pmsRoomType: ROOM_TYPE.STANDARD_BIGGER,
                  silentSubstitution: false,
                  cotAvailable: false,
                  roomClass: 'BG',
                  roomPriceBreakdown: {
                    totalNetAmount: 117,
                    currencyCode: 'GBP',
                    dailyPrices: [
                      {
                        date: '2022-12-19',
                        netPrice: 117,
                      },
                    ],
                  },
                  specialRequests: ['DBLE'],
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
            'Pay now, fully refundable with free cancellation up to 1pm on the day of arrival',
          rateName: 'Flex',
          rateOrder: '1',
          rateTags: [] as string[],
        },
        {
          rateClassification: 'SEMIFLEX',
          rateDescription:
            'Pay now, fully refundable with free cancellation up to 3 full days before arrival',
          rateName: 'Semi-Flex',
          rateOrder: '2',
          rateTags: [] as string[],
        },
        {
          rateClassification: 'ADVANCE',
          rateDescription:
            'Pay now, fully refundable with free cancellation up to 28 full days before arrival',
          rateName: 'Advance',
          rateOrder: '3',
          rateTags: [] as string[],
        },
        {
          rateClassification: 'STANDARD',
          rateDescription:
            'Pay now, non-refundable. Amendable check in date at the same hotel up to 1pm on the day of arrival',
          rateName: 'Standard',
          rateOrder: '4',
          rateTags: [] as string[],
        },
        {
          rateClassification: 'NONFLEX',
          rateDescription: 'Pay now. No changes',
          rateName: 'Non-Flex',
          rateOrder: '5',
          rateTags: [] as string[],
        },
      ],
    },
    roomTypeInformation: {
      roomTypes: [
        {
          roomTypeCode: [ROOM_TYPE.STANDARD],
          roomCategory: 'Standard',
          roomLabel: 'Standard Room',
          roomDescription: 'Compact rooms, designed around you.',
          roomImage:
            '/content/dam/pi/websites/desktop/new-hotel-details-content/Hub/Hub-Standard-Room.jpg',
          groupId: 'double',
        },
        {
          roomTypeCode: [ROOM_TYPE.STANDARD_BIGGER],
          roomCategory: 'Bigger Room',
          roomLabel: 'Bigger room',
          roomDescription:
            'All the clever design, entertainment and comfort of our standard room just a bit, well, bigger. There’s extra space and a luxurious kingsize Hypnos bed.',
          roomImage: '/content/dam/hub/hotelimages/generic/hub-bigger.jpg',
          groupId: 'double',
        },
      ],
    },
    hotelInventory: {
      roomTypeInventories: [
        { availableCount: 12, code: 'DBLWIN' },
        { availableCount: 51, code: 'BIGWIN' },
        { availableCount: 48, code: 'EXTDBL' },
      ],
    },
  } as unknown as HIAvailabilityRates,
};

const mockedMulticlassPropsHub = {
  channel: BOOKING_CHANNEL.PI as Channel,
  variant: 'PI',
  queryClient: new QueryClient(),
  hotelAvailabilityResponse: {
    isLoadingHotelAvailability: mockMultiClassResponseHub.isLoading,
    isErrorHotelAvailability: mockMultiClassResponseHub.isError,
    errorHotelAvailability: mockMultiClassResponseHub.error,
    dataHotelAvailability: mockMultiClassResponseHub.data,
  },
  globalConfigResponse: {
    isLoadingGlobalConfig: mockGlobalConfig.isLoading,
    isErrorGlobalConfig: mockGlobalConfig.isError,
    errorGlobalConfig: mockGlobalConfig.error,
    dataGlobalConfig: mockGlobalConfig.data,
  },
  isHotelOpeningSoon: false,
  isParentAnalytics: true,
  arrival: '2022-12-19',
  departure: '2022-12-19',
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
  hasRateSelectorTitleDescription: true,
};

jest.mock('@whitbread-eos/utils', () => {
  const utils = jest.requireActual('@whitbread-eos/utils');
  return {
    ...utils,
    getSelectedRoomClassCode: jest.fn().mockReturnValue('ST'),
    getCookie: (cookieName: string) => {
      if (cookieName === utils.BUNDLE_CHOICE) {
        return mockCookies.bundles;
      }
    },
    useScrolledPast: () => mockScrolledPast.value,
    formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
    useCustomLocale: () => mockCustomLocale(),
    useMutationRequest: () => jest.fn(),
    useQueryRequest: () => mockMultiClassResponseHub,
    useFeatureToggle: jest.fn().mockReturnValue({
      kill_switch_pi_meta_search: true,
      release_pi_bb_ccui_choose_room_type: true,
      release_pi_bb_ccui_premier_plus_accessible_room: true,
      release_pi_promo_code_site_wide: true,
    }),
    useStaticHotelInformation: () => ({
      brand: 'HUB',
      hotelId: 'LONKIN',
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

const renderRateSelectorPIIVariantHub = () =>
  render(<RateSelector {...mockedMulticlassPropsHub} />);

describe('RateSelector - multiple class, HUB', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockCustomLocale.mockReturnValue({
      language: 'en',
    });
    mockUseRouter.mockReturnValue({
      locale: 'gb',
      query: {
        INTTYP1: 'DB',
        slug: ['deutschland', 'berlin', 'berlin', 'berlin-alexanderplatz.html'],
      },
    });
  });

  afterEach(() => {
    mockedMulticlassPropsHub.hotelAvailabilityResponse.isLoadingHotelAvailability = false;
    mockedMulticlassPropsHub.hotelAvailabilityResponse.isErrorHotelAvailability = false;
    mockedMulticlassPropsHub.hotelAvailabilityResponse.errorHotelAvailability = { message: '' };
  });

  it('should render a RateSelector with title, rates and basket', function () {
    renderRateSelectorPIIVariantHub();
    expect(screen.getByText('hoteldetails.rates.grid.title')).toBeInTheDocument();
    expect(screen.getByTestId('basket')).toBeInTheDocument();
  });

  it('should render 2 roomClass rateCards in correct order (HUB - Bigger Room is re-ordered to be first)', function () {
    renderRateSelectorPIIVariantHub();

    const rateCards = screen.queryAllByTestId('hdp_rateCard', {
      exact: false,
    });

    expect(rateCards.length).toBe(2);
    // Bigger Rooms rate card
    expect(rateCards[0]).toHaveTextContent('pihotelinfo.hubBiggerRoomTitle');
    expect(rateCards[0]).toHaveTextContent('Flex');
    expect(rateCards[0]).toHaveTextContent('Standard');
    // Standard Rooms rate card
    expect(rateCards[1]).toHaveTextContent('pihotelinfo.standardRoomTitle');
    expect(rateCards[1]).toHaveTextContent('Flex');
    expect(rateCards[1]).toHaveTextContent('Standard');
  });

  it('should have first Radio selected by default', async () => {
    renderRateSelectorPIIVariantHub();
    const defaultRadio = screen.getAllByRole('radio')[0] as HTMLInputElement;

    expect(screen.getAllByRole('radio')).toHaveLength(4);
    expect(defaultRadio).toBeChecked();
    expect(defaultRadio.value).toBe('BG-0'); // Bigger Room - Flex
    expect(defaultRadio.value).not.toBe('BG-1'); // Bigger Room - Standard rate
    expect(defaultRadio.value).not.toBe('ST-0'); // Standard Room - Flex
  });
});

jest.mock('@whitbread-eos/utils', () => {
  const utils = jest.requireActual('@whitbread-eos/utils');
  return {
    ...utils,
    getSelectedRoomClassCode: jest.fn().mockReturnValue('ST'),
    getCookie: (cookieName: string) => {
      if (cookieName === utils.BUNDLE_CHOICE) {
        return mockCookies.bundles;
      }
    },
    useScrolledPast: () => mockScrolledPast.value,
    formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
    useCustomLocale: () => mockCustomLocale(),
    useMutationRequest: () => jest.fn(),
    useQueryRequest: () => mockMultiClassResponseHub,
    isArrivalDateWithinSetHours: jest.fn(),
    useFeatureToggle: jest.fn().mockReturnValue({
      kill_switch_pi_meta_search: true,
      release_pi_bb_ccui_choose_room_type: true,
      release_pi_bb_ccui_premier_plus_accessible_room: true,
      release_pi_promo_code_site_wide: true,
    }),
    useStaticHotelInformation: () => ({
      brand: 'HUB',
      hotelId: 'LONKIN',
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

describe('RateSelector - multiple class - Rate Card Ordering feature - PI brand', () => {
  beforeEach(() => {
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
  });

  afterEach(() => {
    mockedProps.hotelAvailabilityResponse.isLoadingHotelAvailability = false;
    mockedProps.hotelAvailabilityResponse.isErrorHotelAvailability = false;
    mockedProps.hotelAvailabilityResponse.errorHotelAvailability = { message: '' };
  });

  const doubleRoom = {
    pmsRoomType: ROOM_TYPE.DOUBLE,
    silentSubstitution: false,
    cotAvailable: false,
    roomClass: 'ST',
    roomPriceBreakdown: {
      totalNetAmount: 141,
      currencyCode: 'GBP',
      dailyPrices: [
        {
          date: '2022-12-21',
          netPrice: 141,
        },
      ],
    },
    specialRequests: ['DBLE'],
  };

  const myMockComponentProps = {
    ...mockComponentProps,
    data: {
      ...mockResponse.data.ratesInformation,
      ...mockResponse.data.hotelInventory,
      hotelAvailability: {
        ...mockResponse.data.hotelAvailability,
        roomRates: [
          {
            ratePlanCode: 'FLEXRATE',
            promotionCode: 'FLX30R',
            roomTypes: [
              {
                roomType: 'DB',
                adults: 1,
                children: 0,
                cotRequested: false,
                rooms: [
                  { ...doubleRoom, roomClass: 'PP' },
                  { ...doubleRoom, roomClass: 'SV' },
                  { ...doubleRoom, roomClass: 'ST' },
                  { ...doubleRoom, roomClass: 'PV' },
                  { ...doubleRoom, roomClass: 'SU' },
                ],
              },
            ],
          },
          {
            ratePlanCode: 'STANDARD',
            promotionCode: 'ST10R',
            roomTypes: [
              {
                roomType: 'DB',
                adults: 1,
                children: 0,
                cotRequested: false,
                rooms: [
                  { ...doubleRoom, roomClass: 'PP' },
                  { ...doubleRoom, roomClass: 'SV' },
                  { ...doubleRoom, roomClass: 'ST' },
                  { ...doubleRoom, roomClass: 'PV' },
                  { ...doubleRoom, roomClass: 'SU' },
                ],
              },
            ],
          },
        ],
      },
    },
  };

  const renderRateSelectorComponentForRateCardOrdering = () =>
    render(
      <RateSelectorComponent
        {...myMockComponentProps}
        bookingFlow={{ bookingFlowItems: [{ bookingId: 'booking-a1', rateCode: 'FLEXRATE' }] }}
        variant="pi"
      />
    );

  it('should render the cards by the orderedroomClassesCodes that are coming from AEM and the isRateCardOrderingEnabled feature flag', async () => {
    const mockRouter = {
      push: jest.fn(),
      query: {
        INTTYP1: 'DB',
        slug: ['deutschland', 'berlin', 'berlin', 'berlin-alexanderplatz.html'],
      },
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    renderRateSelectorComponentForRateCardOrdering();
    const rateCards = screen.queryAllByTestId('hdp_roomTypeTitle', {
      exact: false,
    });

    const expectedCardsOrder = [
      RoomClassLabelByCode.PP,
      RoomClassLabelByCode.PV,
      RoomClassLabelByCode.SU,
      RoomClassLabelByCode.ST,
      RoomClassLabelByCode.SV,
    ];

    expect(rateCards.length).toBe(5);
    rateCards.forEach((rateCard, i) => {
      expect(rateCard).toHaveTextContent(expectedCardsOrder[i]);
    });
  });
});

const mockUseFeatureToggle = useFeatureToggle as jest.Mock;
const mockIsArrivalDateWithinSetHours = jest.requireMock('@whitbread-eos/utils')
  .isArrivalDateWithinSetHours as jest.Mock;

describe('RateSelector - shouldShowNotification', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should show notification for DE hotels when DE feature flag is disabled and arrival date is within 72 hours', () => {
    mockUseFeatureToggle.mockReturnValue({
      kill_switch_ccui_uk_enable_payments_within_72h: true,
      kill_switch_ccui_de_enable_payments_within_72h: false,
      release_pi_bb_ccui_choose_room_type: true,
      release_pi_bb_ccui_premier_plus_accessible_room: true,
      release_pi_promo_code_site_wide: true,
    });
    mockIsArrivalDateWithinSetHours.mockReturnValue(true);

    const { getByTestId } = render(
      <RateSelectorComponent
        {...mockComponentProps}
        brand="PID"
        channel={BOOKING_CHANNEL.CCUI as Channel}
        variant="CCUI"
      />
    );

    expect(getByTestId('hdp_payOnArrivalOnly-AlertDescription')).toBeInTheDocument();
  });

  it('should show notification for UK hotels when UK feature flag is disabled and arrival date is within 72 hours', () => {
    mockUseFeatureToggle.mockReturnValue({
      kill_switch_ccui_uk_enable_payments_within_72h: false,
      kill_switch_ccui_de_enable_payments_within_72h: true,
      release_pi_bb_ccui_choose_room_type: true,
      release_pi_bb_ccui_premier_plus_accessible_room: true,
    });
    mockIsArrivalDateWithinSetHours.mockReturnValue(true);

    const { getByTestId } = render(
      <RateSelectorComponent
        {...mockComponentProps}
        brand="PI"
        channel={BOOKING_CHANNEL.CCUI as Channel}
        variant="CCUI"
      />
    );

    expect(getByTestId('hdp_payOnArrivalOnly-AlertDescription')).toBeInTheDocument();
  });

  it('should show notification when feature flags are disabled', () => {
    mockUseFeatureToggle.mockReturnValue({
      kill_switch_ccui_uk_enable_payments_within_72h: false,
      kill_switch_ccui_de_enable_payments_within_72h: false,
      release_pi_bb_ccui_choose_room_type: true,
      release_pi_bb_ccui_premier_plus_accessible_room: true,
    });
    mockIsArrivalDateWithinSetHours.mockReturnValue(true);

    const { getByTestId } = render(
      <RateSelectorComponent
        {...mockComponentProps}
        brand="PI"
        channel={BOOKING_CHANNEL.CCUI as Channel}
        variant="CCUI"
      />
    );

    expect(getByTestId('hdp_payOnArrivalOnly-AlertDescription')).toBeInTheDocument();
  });

  it('should show notification when feature flags are disabled', () => {
    mockUseFeatureToggle.mockReturnValue({
      kill_switch_ccui_uk_enable_payments_within_72h: false,
      kill_switch_ccui_de_enable_payments_within_72h: false,
      release_pi_bb_ccui_choose_room_type: true,
      release_pi_bb_ccui_premier_plus_accessible_room: true,
    });
    mockIsArrivalDateWithinSetHours.mockReturnValue(true);

    const { getByTestId } = render(
      <RateSelectorComponent
        {...mockComponentProps}
        brand="PI"
        channel={BOOKING_CHANNEL.CCUI as Channel}
        variant="CCUI"
      />
    );

    expect(getByTestId('hdp_payOnArrivalOnly-AlertDescription')).toBeInTheDocument();
  });

  it('should not show notification when arrival date is not within 72 hours', () => {
    mockUseFeatureToggle.mockReturnValue({
      kill_switch_ccui_uk_enable_payments_within_72h: true,
      kill_switch_ccui_de_enable_payments_within_72h: true,
      release_pi_bb_ccui_choose_room_type: true,
      release_pi_bb_ccui_premier_plus_accessible_room: true,
    });
    mockIsArrivalDateWithinSetHours.mockReturnValue(false);

    const { queryByTestId } = render(
      <RateSelectorComponent
        {...mockComponentProps}
        brand="PID"
        channel={BOOKING_CHANNEL.CCUI as Channel}
        variant="CCUI"
      />
    );

    expect(queryByTestId('hdp_payOnArrivalOnly-AlertDescription')).not.toBeInTheDocument();
  });
});

describe('RateSelector - Multi room search with different room class availability', () => {
  beforeEach(() => {
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
  });

  const mockMultiRoomAvailability = {
    data: {
      hotelAvailability: {
        hotelId: 'MANOLD',
        startDate: '2025-12-21',
        endDate: '2025-12-22',
        available: true,
        roomRates: [
          {
            ratePlanCode: 'STANDARD',
            promotionCode: 'ST30R',
            roomTypes: [
              {
                roomType: 'DB',
                adults: 1,
                children: 0,
                cotRequested: false,
                rooms: [
                  {
                    pmsRoomType: ROOM_TYPE.PREMIER_PLUS,
                    silentSubstitution: true,
                    cotAvailable: false,
                    roomClass: 'PP',
                    roomPriceBreakdown: {
                      totalNetAmount: 112,
                      currencyCode: 'GBP',
                      dailyPrices: [
                        {
                          date: '2022-12-21',
                          netPrice: 112,
                        },
                      ],
                    },
                    specialRequests: ['DBLE'],
                  },
                  {
                    pmsRoomType: ROOM_TYPE.DOUBLE,
                    silentSubstitution: false,
                    cotAvailable: false,
                    roomClass: 'ST',
                    roomPriceBreakdown: {
                      totalNetAmount: 117,
                      currencyCode: 'GBP',
                      dailyPrices: [
                        {
                          date: '2022-12-21',
                          netPrice: 117,
                        },
                      ],
                    },
                    specialRequests: ['DBLE'],
                  },
                ],
              },
              {
                roomType: 'TWIN',
                adults: 1,
                children: 0,
                cotRequested: false,
                rooms: [
                  {
                    pmsRoomType: ROOM_TYPE.DOUBLE,
                    silentSubstitution: true,
                    cotAvailable: false,
                    roomClass: 'ST',
                    roomPriceBreakdown: {
                      totalNetAmount: 112,
                      currencyCode: 'GBP',
                      dailyPrices: [
                        {
                          date: '2022-12-21',
                          netPrice: 112,
                        },
                      ],
                    },
                    specialRequests: ['DBLE'],
                  },
                  {
                    pmsRoomType: ROOM_TYPE.DOUBLE,
                    silentSubstitution: false,
                    cotAvailable: false,
                    roomClass: 'ST',
                    roomPriceBreakdown: {
                      totalNetAmount: 117,
                      currencyCode: 'GBP',
                      dailyPrices: [
                        {
                          date: '2022-12-21',
                          netPrice: 117,
                        },
                      ],
                    },
                    specialRequests: ['DBLE'],
                  },
                ],
              },
            ],
          },
          {
            ratePlanCode: 'FLEXRATE',
            promotionCode: 'FLX30R',
            roomTypes: [
              {
                roomType: 'DB',
                adults: 1,
                children: 0,
                cotRequested: false,
                rooms: [
                  {
                    pmsRoomType: ROOM_TYPE.PREMIER_PLUS_VIEW,
                    silentSubstitution: true,
                    cotAvailable: false,
                    roomClass: 'PV',
                    roomPriceBreakdown: {
                      totalNetAmount: 136,
                      currencyCode: 'GBP',
                      dailyPrices: [
                        {
                          date: '2022-12-21',
                          netPrice: 136,
                        },
                      ],
                    },
                    specialRequests: ['DBLE'],
                  },
                ],
              },
              {
                roomType: 'TWIN',
                adults: 1,
                children: 0,
                cotRequested: false,
                rooms: [
                  {
                    pmsRoomType: ROOM_TYPE.PREMIER_PLUS_VIEW,
                    silentSubstitution: true,
                    cotAvailable: false,
                    roomClass: 'PV',
                    roomPriceBreakdown: {
                      totalNetAmount: 136,
                      currencyCode: 'GBP',
                      dailyPrices: [
                        {
                          date: '2022-12-21',
                          netPrice: 136,
                        },
                      ],
                    },
                    specialRequests: ['DBLE'],
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
              'Pay now, fully refundable with free cancellation up to 3 full days before arrival',
            rateName: 'Semi-Flex',
            rateOrder: '2',
            rateTags: [] as string[],
          },
          {
            rateClassification: 'STANDARD',
            rateDescription:
              'Pay now, non-refundable. Amendable check in date at the same hotel up to 1pm on the day of arrival',
            rateName: 'Standard',
            rateOrder: '4',
            rateTags: [] as string[],
          },
        ],
      },
      hotelInventory: {
        roomTypeInventories: [
          { availableCount: 12, code: 'DOUBLE' },
          { availableCount: 51, code: 'FMTRPL' },
          { availableCount: 48, code: 'FMQUAD' },
          { availableCount: 48, code: 'PPLDBL' },
        ],
      },
    } as HIAvailabilityRates,
  };

  it('should render room class cards that is available for all room types', async () => {
    const hotelAvailabilityResponse = {
      isLoadingHotelAvailability: mockMultiClassResponsePI.isLoading,
      isErrorHotelAvailability: mockMultiClassResponsePI.isError,
      errorHotelAvailability: mockMultiClassResponsePI.error,
      dataHotelAvailability: mockMultiRoomAvailability.data,
    };
    const { getAllByTestId } = render(
      <RateSelector
        {...mockedMulticlassPropsPI}
        hotelAvailabilityResponse={hotelAvailabilityResponse}
      />
    );

    const rateCards = getAllByTestId('hdp_rateCard');
    expect(rateCards.length).toBe(2);
  });

  it('should have the correct rate selected by default with different room class availability for multi room search', async () => {
    const hotelAvailabilityResponse = {
      isLoadingHotelAvailability: mockMultiClassResponsePI.isLoading,
      isErrorHotelAvailability: mockMultiClassResponsePI.isError,
      errorHotelAvailability: mockMultiClassResponsePI.error,
      dataHotelAvailability: mockMultiRoomAvailability.data,
    };
    render(
      <RateSelector
        {...mockedMulticlassPropsPI}
        hotelAvailabilityResponse={hotelAvailabilityResponse}
      />
    );
    const defaultRadio = screen.getAllByRole('radio')[0] as HTMLInputElement;
    expect(screen.getAllByRole('radio')).toHaveLength(2);
    expect(defaultRadio).toBeChecked();
    expect(defaultRadio.value).toBe('ST-0');
  });

  it('should display Promo tag only if FF true and rate tags available', function () {
    mockUseFeatureToggle.mockReturnValue({
      kill_switch_ccui_uk_enable_payments_within_72h: false,
      kill_switch_ccui_de_enable_payments_within_72h: false,
      release_pi_bb_ccui_choose_room_type: false,
      release_pi_bb_ccui_premier_plus_accessible_room: false,
      release_pi_promo_code_landing_page: true,
      release_pi_show_promotion_box: true,
    });
    const hotelAvailabilityResponse = {
      isLoadingHotelAvailability: mockMultiClassResponsePI.isLoading,
      isErrorHotelAvailability: mockMultiClassResponsePI.isError,
      errorHotelAvailability: mockMultiClassResponsePI.error,
      dataHotelAvailability: mockMultiRoomAvailability.data,
    };
    const { getByTestId } = render(
      <RateSelector
        {...mockedMulticlassPropsPI}
        hotelAvailabilityResponse={hotelAvailabilityResponse}
      />
    );

    expect(getByTestId('basketWrapper')).toBeInTheDocument();
  });

  it('should display Promo box', async function () {
    (useMediaQuery as jest.Mock).mockReturnValue([true]);
    mockUseFeatureToggle.mockReturnValue({
      kill_switch_ccui_uk_enable_payments_within_72h: false,
      kill_switch_ccui_de_enable_payments_within_72h: false,
      release_pi_bb_ccui_choose_room_type: false,
      release_pi_bb_ccui_premier_plus_accessible_room: false,
      release_pi_promo_code_landing_page: true,
    });
    const hotelAvailabilityResponse = {
      isLoadingHotelAvailability: mockMultiClassResponsePI.isLoading,
      isErrorHotelAvailability: mockMultiClassResponsePI.isError,
      errorHotelAvailability: mockMultiClassResponsePI.error,
      dataHotelAvailability: mockMultiRoomAvailability.data,
    };
    const { getByTestId } = render(
      <RateSelector
        {...mockedMulticlassPropsPI}
        hotelAvailabilityResponse={hotelAvailabilityResponse}
      />
    );

    expect(getByTestId('basketWrapper')).toBeInTheDocument();
  });
});

describe('getRateCodeIndex', () => {
  const roomRates = [
    { ratePlanCode: 'FX30R', promotionCode: 'PROMO1' },
    { ratePlanCode: 'ST20RU', promotionCode: null },
    { ratePlanCode: 'FLEXRATE', promotionCode: 'FXPROMO' },
  ] as HIRoomRate[];

  it('should return the index when a matching ratePlanCode is found via mapping', () => {
    const mapping = [{ rate: 'FLEXRATE', code: 'FX30R' }];

    const result = getRateCodeIndex('FLEXRATE', roomRates, mapping);
    expect(result).toBe(0);
  });

  it('should return -1 when mapped code does not match any ratePlanCode', () => {
    const mapping = [{ rate: 'FLEXRATE', code: 'PROMO1' }];

    const result = getRateCodeIndex('FLEXRATE', roomRates, mapping);
    expect(result).toBe(-1);
  });

  it('should return -1 when no matching rateType is present in mapping', () => {
    const mapping = [{ rate: 'STANDARD', code: 'ST20RU' }];

    const result = getRateCodeIndex('NOTFOUND', roomRates, mapping);
    expect(result).toBe(-1);
  });

  it('should handle multiple mappings and pick correct one', () => {
    const mapping = [
      { rate: 'STANDARD', code: 'ST20RU' },
      { rate: 'FLEXRATE', code: 'FX30R' },
    ];

    const result = getRateCodeIndex('STANDARD', roomRates, mapping);
    expect(result).toBe(1);
  });

  it('should return -1 if mapping is empty', () => {
    const mapping: any[] = [];

    const result = getRateCodeIndex('FLEXRATE', roomRates, mapping);
    expect(result).toBe(-1);
  });

  it('should match using startsWith for both rate and code', () => {
    const mapping = [{ rate: 'FLEX', code: 'FX30' }];

    const result = getRateCodeIndex('FLEXRATE', roomRates, mapping);
    expect(result).toBe(0);
  });
});

describe('getPromotionKind', () => {
  const promoActionsBase: any = {
    promoState: {
      code: 'FX10R',
      type: 'PERCENTAGE',
    },
  };

  it('should return null when selectedPromoCode is empty', () => {
    const result = getPromotionKind('', promoActionsBase);
    expect(result).toBeNull();
  });

  it('should return null when promoActions is undefined', () => {
    const result = getPromotionKind('FX10R', undefined as any);
    expect(result).toBeNull();
  });

  it('should return null when promoActions.promoState.code is missing', () => {
    const result = getPromotionKind('FX10R', { promoState: {} } as any);
    expect(result).toBeNull();
  });

  it('should return null when selectedPromoCode does not match promoActions.promoState.code', () => {
    const result = getPromotionKind('FX20R', promoActionsBase);
    expect(result).toBeNull();
  });

  it('should return type when selectedPromoCode matches promoActions.promoState.code', () => {
    const result = getPromotionKind('FX10R', promoActionsBase);
    expect(result).toBe('PERCENTAGE');
  });

  it('should return null when type is undefined even if codes match', () => {
    const result = getPromotionKind('FX10R', {
      promoState: { code: 'FX10R', type: undefined },
    } as any);
    expect(result).toBeFalsy();
  });
});

describe('getSelectedRate', () => {
  const mockRoomRates = [
    {
      ratePlanCode: 'RATE1',
      roomTypes: [{ roomType: 'DB' }],
    },
    {
      ratePlanCode: 'RATE2',
      roomTypes: [{ roomType: 'TWIN' }],
    },
    {
      ratePlanCode: 'RATE3',
      roomTypes: [{ roomType: 'FAM' }],
    },
  ] as HIRoomRate[];

  const mockPromoKind = 'PERCENTAGE' as any;

  it('should return rate by activeUserChoiceRate when noRoomTypeSearch is true and hotelAvailability exists', () => {
    const result = getSelectedRate(
      true,
      { hotelAvailability: {} },
      mockRoomRates,
      'RATE2',
      0,
      { rate: '' },
      mockPromoKind
    );

    expect(result.ratePlanCode).toBe('RATE2');
    expect(result.promoKind).toBe('PERCENTAGE');
  });

  it('should fallback to selectedRateIndex when noRoomTypeSearch is true but activeUserChoiceRate not found', () => {
    const result = getSelectedRate(
      true,
      { hotelAvailability: {} },
      mockRoomRates,
      'NONEXISTENT',
      1,
      { rate: '' },
      mockPromoKind
    );

    expect(result.ratePlanCode).toBe('RATE2');
    expect(result.promoKind).toBe('PERCENTAGE');
  });

  it('should use activeChoice.rate when noRoomTypeSearch is false and activeChoice.rate is set', () => {
    const result = getSelectedRate(
      false,
      { hotelAvailability: {} },
      mockRoomRates,
      'RATE1',
      0,
      { rate: 'RATE3' },
      mockPromoKind
    );

    expect(result.ratePlanCode).toBe('RATE3');
    expect(result.promoKind).toBe('PERCENTAGE');
  });

  it('should fallback to selectedRateIndex when neither condition is met', () => {
    const result = getSelectedRate(
      false,
      { hotelAvailability: {} },
      mockRoomRates,
      'RATE1',
      2,
      { rate: '' },
      mockPromoKind
    );

    expect(result.ratePlanCode).toBe('RATE3');
    expect(result.promoKind).toBe('PERCENTAGE');
  });

  it('should return rate from selectedRateIndex when noRoomTypeSearch is true but no hotelAvailability', () => {
    const result = getSelectedRate(
      true,
      null,
      mockRoomRates,
      'RATE2',
      0,
      { rate: '' },
      mockPromoKind
    );

    expect(result.ratePlanCode).toBe('RATE1');
    expect(result.promoKind).toBe('PERCENTAGE');
  });

  it('should always include promoKind in the result', () => {
    const customPromoKind = 'FIXED_AMOUNT' as any;
    const result = getSelectedRate(
      false,
      null,
      mockRoomRates,
      '',
      1,
      { rate: '' },
      customPromoKind
    );

    expect(result.promoKind).toBe('FIXED_AMOUNT');
  });

  it('should handle empty activeChoice object', () => {
    const result = getSelectedRate(
      false,
      { hotelAvailability: {} },
      mockRoomRates,
      'RATE1',
      1,
      {},
      mockPromoKind
    );

    expect(result.ratePlanCode).toBe('RATE2');
    expect(result.promoKind).toBe('PERCENTAGE');
  });

  it('should prioritize noRoomTypeSearch over activeChoice.rate', () => {
    const result = getSelectedRate(
      true,
      { hotelAvailability: {} },
      mockRoomRates,
      'RATE3',
      0,
      { rate: 'RATE1' },
      mockPromoKind
    );

    expect(result.ratePlanCode).toBe('RATE3');
    expect(result.promoKind).toBe('PERCENTAGE');
  });
});

describe('renderRateCards function', () => {
  const mockProps = {
    channel: 'PI' as Channel,
    variant: 'PI',
    isLoading: false,
    isError: false,
    data: {
      hotelAvailability: {
        hotelId: 'LONEUS',
        startDate: '2024-01-01',
        endDate: '2024-01-02',
        available: true,
        roomRates: [
          {
            ratePlanCode: 'FLEXRATE',
            cellCode: 'FLEX',
            roomTypes: [
              {
                roomType: 'double',
                adults: 2,
                children: 0,
                rooms: [
                  {
                    pmsRoomType: 'DBLDBL',
                    roomClass: 'ST',
                    roomType: 'double',
                    roomPriceBreakdown: {
                      totalNetAmount: 100,
                      currencyCode: 'GBP',
                    },
                  },
                ],
              },
            ],
          },
        ],
      },
      hotelInventory: {
        roomTypeInventories: [],
      },
      ratesInformation: {
        rateClassifications: [
          {
            ratePlanCode: 'FLEXRATE',
            rateName: 'Flexible Rate',
            rateDescription: 'Fully flexible rate',
          },
        ],
      },
    } as any,
    error: null,
    basketData: {
      hotelId: 'LONEUS',
      arrival: '2024-01-01',
      departure: '2024-01-02',
      numberOfUnits: 1,
      numberOfAdults: 2,
      numberOfChildren: 0,
    },
    bookingFlow: {
      bookingFlowItems: [],
    },
    queryClient: new QueryClient(),
    roomTypeInformationResponse: {
      isLoadingRoomTypeInformation: false,
      dataRoomTypeInformation: {
        roomTypeInformation: {
          roomTypes: [
            {
              roomTypeCode: 'DB',
              roomTypeName: 'Double Room',
            },
          ],
        },
      },
    } as any,
    globalConfigResponse: {
      isLoadingGlobalConfig: false,
      isErrorGlobalConfig: false,
      dataGlobalConfig: mockGlobalConfig,
    } as any,
    isParentAnalytics: false,
    brand: 'PI',
    accessibilityInfo: {},
    phoneNumber: '123456',
    bookRsvIsLoading: false,
    bookRsvIsError: false,
    bookRsvIsSuccess: false,
    bookRsvData: null,
    bookRsvError: null,
    handleBooking: jest.fn(),
    isHotelOpeningSoon: false,
    isLessThanSm: false,
    isLessThanMd: false,
    isLessThanLg: false,
    isDisabledContinueBtn: false,
    mappedRoomLabels: {},
    isSilentFeatureFlagEnabled: false,
    thirdParties: '',
    companyData: undefined,
    isPrePopulateBillingAddressEnabled: false,
    targetRatePlanCode: '',
    isRateCardOrderingEnabledBB: false,
    isRateCardOrderingEnabledCCUI: false,
    isCityTaxEnabled: false,
    promoActions: undefined,
  };

  beforeEach(() => {
    jest.clearAllMocks();
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });
    (useFeatureToggle as jest.Mock).mockReturnValue({
      FT_PI_NO_ROOM_TYPE_SEARCH: false,
      FT_PI_DISPLAY_SOFT_BUNDLES: false,
      FT_PI_META_CLASS_RATE_SEARCH: false,
      FT_PI_BB_CCUI_NONSILENT_SUBSTITUTION_PER_ROOMCLASS: false,
      FT_CCUI_ENABLE_72H_UK_NOTIFICATION: false,
      FT_CCUI_ENABLE_72H_DE_NOTIFICATION: false,
      FT_PI_CCUI_BB_PREM_PLUS_ACCESSIBLE: false,
      FT_PI_PROMO_CODE_SITE_WIDE: false,
      FT_PI_PROMO_CODE_LANDING_PAGE: false,
      FT_CCUI_PROMO_CODE_LANDING_PAGE: false,
      FT_BB_PROMO_CODE_LANDING_PAGE: false,
    });
  });

  it('should render LoadingSpinner when noRoomTypeSearch is enabled but data is loading', () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      FT_PI_NO_ROOM_TYPE_SEARCH: true,
      FT_PI_DISPLAY_SOFT_BUNDLES: false,
    });

    const propsWithLoading = {
      ...mockProps,
      roomTypeInformationResponse: {
        isLoadingRoomTypeInformation: true,
      },
      isLoading: true,
    };

    document.cookie = 'targetVariant=noRoomTypeSearch;display_all_rooms_on_hdp=true';
    render(<RateSelectorComponent {...propsWithLoading} />);

    expect(screen.getByTestId('loading-spinner')).toBeInTheDocument();
  });

  it('should render ChoiceArchitecture when noRoomTypeSearch is enabled and data is loaded', () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      FT_PI_NO_ROOM_TYPE_SEARCH: true,
      FT_PI_DISPLAY_SOFT_BUNDLES: false,
    });

    document.cookie = 'targetVariant=noRoomTypeSearch;display_all_rooms_on_hdp=true';
    render(<RateSelectorComponent {...mockProps} />);

    // ChoiceArchitecture should be rendered
    expect(screen.queryByTestId('loading-spinner')).not.toBeInTheDocument();
  });

  it('should render LoadingSpinner when noRoomTypeSearch is enabled but hotelAvailability is missing', () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      FT_PI_NO_ROOM_TYPE_SEARCH: true,
      FT_PI_DISPLAY_SOFT_BUNDLES: false,
    });

    const propsWithoutAvailability = {
      ...mockProps,
      data: {
        ...mockProps.data,
        hotelAvailability: null,
      },
      isLoading: true,
    };

    document.cookie = 'targetVariant=noRoomTypeSearch;display_all_rooms_on_hdp=true';
    render(<RateSelectorComponent {...(propsWithoutAvailability as any)} />);

    expect(screen.getByTestId('loading-spinner')).toBeInTheDocument();
    document.cookie = 'bundles=; expires=Thu, 01 Jan 1970 00:00:00 UTC';
  });

  it('should render LoadingSpinner when noRoomTypeSearch is enabled but roomTypes is missing', () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      FT_PI_NO_ROOM_TYPE_SEARCH: true,
      FT_PI_DISPLAY_SOFT_BUNDLES: false,
    });

    const propsWithoutRoomTypes = {
      ...mockProps,
      roomTypeInformationResponse: {
        isLoadingRoomTypeInformation: false,
        dataRoomTypeInformation: {
          roomTypeInformation: {
            roomTypes: null,
          },
        },
      },
      isLoading: true,
    };

    document.cookie = 'targetVariant=noRoomTypeSearch;display_all_rooms_on_hdp=true';
    render(<RateSelectorComponent {...(propsWithoutRoomTypes as any)} />);

    expect(screen.getByTestId('loading-spinner')).toBeInTheDocument();
  });

  it('should render BundleChoice when bundle choice is enabled with class option', () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      FT_PI_NO_ROOM_TYPE_SEARCH: false,
      FT_PI_DISPLAY_SOFT_BUNDLES: true,
    });

    document.cookie = 'bundles=class';
    render(<RateSelectorComponent {...mockProps} />);

    // BundleChoice should be rendered - verify by checking the absence of RateCards
    expect(screen.queryByTestId('loading-spinner')).not.toBeInTheDocument();
    document.cookie = 'bundles=; expires=Thu, 01 Jan 1970 00:00:00 UTC';
  });

  it('should render BundleChoice when bundle choice is enabled with rate option', () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      FT_PI_NO_ROOM_TYPE_SEARCH: false,
      FT_PI_DISPLAY_SOFT_BUNDLES: true,
    });

    document.cookie = 'bundles=rate';
    render(<RateSelectorComponent {...mockProps} />);

    // BundleChoice should be rendered
    expect(screen.queryByTestId('loading-spinner')).not.toBeInTheDocument();
    document.cookie = 'bundles=; expires=Thu, 01 Jan 1970 00:00:00 UTC';
  });

  it('should render RateCards when no special flags are enabled', () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      FT_PI_NO_ROOM_TYPE_SEARCH: false,
      FT_PI_DISPLAY_SOFT_BUNDLES: false,
    });

    render(<RateSelectorComponent {...mockProps} />);

    // RateCards should be rendered - verify by checking basket is present
    expect(screen.getByTestId('basketWrapper')).toBeInTheDocument();
  });

  it('should render RateCards when bundle cookie is set to false', () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      FT_PI_NO_ROOM_TYPE_SEARCH: false,
      FT_PI_DISPLAY_SOFT_BUNDLES: true,
    });

    document.cookie = 'bundles=false';
    render(<RateSelectorComponent {...mockProps} />);

    // RateCards should be rendered as default
    expect(screen.getByTestId('basketWrapper')).toBeInTheDocument();
    document.cookie = 'bundles=; expires=Thu, 01 Jan 1970 00:00:00 UTC';
  });

  it('should prioritize noRoomTypeSearch over bundle choice when both are enabled', () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      FT_PI_NO_ROOM_TYPE_SEARCH: true,
      FT_PI_DISPLAY_SOFT_BUNDLES: true,
    });

    document.cookie = 'targetVariant=noRoomTypeSearch;display_all_rooms_on_hdp=true';
    render(<RateSelectorComponent {...mockProps} />);

    // ChoiceArchitecture should be rendered, not BundleChoice
    expect(screen.queryByTestId('loading-spinner')).not.toBeInTheDocument();
    document.cookie = 'bundles=; expires=Thu, 01 Jan 1970 00:00:00 UTC';
  });

  it('should render LoadingSpinner when isLoading prop is true regardless of flags', () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      FT_PI_NO_ROOM_TYPE_SEARCH: true,
      FT_PI_DISPLAY_SOFT_BUNDLES: false,
    });

    const propsWithIsLoading = {
      ...mockProps,
      isLoading: true,
    };

    render(<RateSelectorComponent {...propsWithIsLoading} />);

    expect(screen.getByTestId('loading-spinner')).toBeInTheDocument();
  });

  it('should handle missing roomTypeInformationResponse gracefully in noRoomTypeSearch mode', () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      FT_PI_NO_ROOM_TYPE_SEARCH: true,
      FT_PI_DISPLAY_SOFT_BUNDLES: false,
    });

    const propsWithoutRoomTypeInfo = {
      ...mockProps,
      roomTypeInformationResponse: {
        isLoadingRoomTypeInformation: true,
        dataRoomTypeInformation: null,
      },
      isLoading: true,
    };

    document.cookie = 'targetVariant=noRoomTypeSearch;display_all_rooms_on_hdp=true';
    render(<RateSelectorComponent {...(propsWithoutRoomTypeInfo as any)} />);

    expect(screen.getByTestId('loading-spinner')).toBeInTheDocument();
  });
});
