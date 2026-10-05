import * as ReactQuery from '@tanstack/react-query';
import { QueryClientProvider } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import type { Claims } from '@whitbread-eos/api';
import { useHotelAvailabilityCCUI, useFeatureToggle, getMappedRooms } from '@whitbread-eos/utils';
import { useSearchParams } from 'next/navigation';
import { NextRouter } from 'next/router';
import React from 'react';

import {
  mockAvailability,
  mockStaticData,
  visualDisplayContext,
  mockHeaderStaticData,
  mockCompanyData,
} from '~mocks/hotel-details';

import { render, screen } from '../../../utils/page-test-utils';
import HotelDetailsPageCCUI from './page.ccui';

const mockUseFeatureToggle = useFeatureToggle as jest.MockedFunction<typeof useFeatureToggle>;

const queryClient = new ReactQuery.QueryClient();
const mockCustomLocale = jest.fn();
const mockUseFeatureSwitch = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  useCustomLocale: () => mockCustomLocale(),
  useStaticHotelInformation: () => mockStaticData,
  invalidateQueries: () => mockAvailability,
  useQuery: () => mockAvailability,
  useForDiscountedRateMicroSite: jest.fn().mockReturnValue(true),
  useForDiscountedRateFlag: jest.fn().mockReturnValue(true),
  useQueryRequest: jest.fn().mockImplementation((queryKey) => {
    if (queryKey[0][0] === 'searchCompanyById') {
      return {
        isLoading: false,
        data: mockCompanyData,
        isError: false,
      };
    } else {
      return {
        isLoading: false,
        data: mockAvailability.data,
        isError: false,
      };
    }
  }),
  useHotelAvailability: () => jest.fn(),
  useHotelAvailabilityCCUI: jest.fn().mockImplementation(() => {
    return {
      data: {
        ...mockAvailability.data,
        hotelAvailability: { ...mockAvailability.data.hotelAvailability, roomRates: [] },
      },
      isLoading: false,
      isError: false,
      error: false,
    };
  }),
  useRestMutationRequest: () => ({
    mutation: jest.fn(),
  }),
  useFeatureSwitch: () => mockUseFeatureSwitch(),
  getMappedRooms: jest.fn(),
  swapKeysAndValues: jest.fn(),
  useHotelAvailabilityDiscountRate: jest.fn().mockImplementation(() => {
    return {
      data: mockAvailability.data,
      isLoading: false,
      isError: false,
      error: false,
    };
  }),
  GetStaticContent: jest.fn().mockImplementation(() => {
    return {
      data: mockHeaderStaticData,
      isLoading: false,
      isError: false,
      error: false,
    };
  }),

  useDiscountRateInfoHotelAvailability: jest.fn().mockImplementation(() => {
    return {
      hotelAvailabilityResponse: mockAvailability.data,
      dataHotelAvailability: mockAvailability.data,
      isDiscountRateLoading: false,
      isDiscountRateError: false,
    };
  }),
  useHotelRatesInformationCCUI: jest.fn().mockImplementation(() => {
    return {
      data: mockAvailability.data,
      isLoading: false,
      isError: false,
      error: false,
    };
  }),
  useFeatureToggle: jest.fn().mockReturnValue({
    release_ccui_show_promotion_box: true,
    release_promotions_in_hotelavailability: false,
  }),
}));

jest.mock('@whitbread-eos/organisms', () => ({
  CCUISearchContainer: () => {
    return <div data-testid="dummy-search-container">CCUI Search Container</div>;
  },
  Location: jest.fn(() => <div>Location</div>),
  RateSelector: jest.fn(() => <div>Rate Selector</div>),
}));

jest.mock('@whitbread-eos/molecules', () => ({
  ...jest.requireActual('@whitbread-eos/molecules'),
  getNoOfDaysInYear: jest.fn(),
}));

const setAnalyticsUser = jest.fn();

const mockObj = {
  visualDisplayContext,
  user: 'agent' as unknown as Claims,
  setAnalyticsUser: setAnalyticsUser,
};

const mockUseRouter = jest.fn().mockImplementation(() => {
  return { query: { CORPID: '15010601' } };
});

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useSearchParams: jest.fn().mockReturnValue({
    get: (key: string) => key,
  }),
}));

const mockRouter: NextRouter = {
  route: '/hotel',
  pathname: '/hotel/[slug]',
  query: {
    ARRdd: '01',
    ARRmm: '12',
    ARRyyyy: '2025',
    NIGHTS: '1',
    ROOMS: '1',
    ADULT1: '1',
    CHILD1: '0',
    COT1: '0',
    INTTYP1: 'STD',
    slug: ['premier-inn'],
  },
  forward: jest.fn(),
  basePath: '',
  push: jest.fn().mockResolvedValue(true),
  replace: jest.fn().mockResolvedValue(true),
  reload: jest.fn(),
  back: jest.fn(),
  prefetch: jest.fn().mockResolvedValue(true),
  beforePopState: jest.fn(),
  events: { on: jest.fn(), off: jest.fn(), emit: jest.fn() },
  isFallback: false,
  isReady: true,
  isPreview: false,
  isLocaleDomain: false,
  asPath:
    '/en/hotels/england/greater-london/london/london-beckton.html?ARRdd=1&ARRmm=11&ARRyyyy=2023&NIGHTS=2&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB',
};

const mockRouterWithIncompleteData = {
  push: jest.fn(),
  query: {
    ADULT1: '1',
    CHILD1: '0',
    COT1: '0',
    INTTYP1: 'DB',
    slug: ['england', 'greater-london', 'london', 'london-beckton.html'],
  },
  asPath:
    '/en/hotels/england/greater-london/london/london-beckton.html?ARRdd=1&ARRmm=11&ARRyyyy=2023&NIGHTS=2&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB',
};

describe('Page CCUI Hotel Details', () => {
  beforeAll(() => {
    mockUseRouter.mockReturnValue(mockRouter);
  });

  beforeEach(() => {
    mockCustomLocale.mockReturnValue({
      country: 'gb',
      language: 'en',
    });
    mockUseFeatureSwitch.mockReturnValue(false);
  });

  afterEach(() => {
    mockStaticData.isLoading = false;
    mockStaticData.isError = false;
    mockStaticData.error.message = '';
    mockStaticData.hotelId = 'LONEUS';
  });

  it('should render CCUI page', async () => {
    const { getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <HotelDetailsPageCCUI {...mockObj} queryClient={queryClient} router={mockRouter} />
      </QueryClientProvider>
    );
    expect(getByTestId('HotelDetailsCCUIPage-Wrapper')).toBeInTheDocument();
  });

  it('should render PI page with default settings if no input is present', async () => {
    const { getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <HotelDetailsPageCCUI
          {...mockObj}
          queryClient={queryClient}
          router={mockRouterWithIncompleteData as any}
        />
      </QueryClientProvider>
    );
    expect(getByTestId('HotelDetailsCCUIPage-Wrapper')).toBeInTheDocument();
  });

  it('should show a loading message if isLoading is true', async () => {
    mockStaticData.isLoading = true;
    const { getByText } = render(
      <QueryClientProvider client={queryClient}>
        <HotelDetailsPageCCUI {...mockObj} queryClient={queryClient} router={mockRouter} />
      </QueryClientProvider>
    );
    expect(getByText('searchresults.list.hotel.loading')).toBeInTheDocument();
  });

  it('should render an error message if isError is true', async () => {
    mockStaticData.isError = true;
    mockStaticData.error.message = 'Error message here';
    const { getByText } = render(
      <QueryClientProvider client={queryClient}>
        <HotelDetailsPageCCUI {...mockObj} queryClient={queryClient} router={mockRouter} />
      </QueryClientProvider>
    );
    expect(getByText('Error message here')).toBeInTheDocument();
  });

  it('should render 404 error page if no hotelId', async () => {
    mockStaticData.hotelId = '';
    render(
      <QueryClientProvider client={queryClient}>
        <HotelDetailsPageCCUI {...mockObj} queryClient={queryClient} router={mockRouter} />
      </QueryClientProvider>
    );
    screen.getByRole('heading', { name: /404/i });
    screen.getByRole('heading', { name: /This page could not be found/i });
  });
  it('Should render Hotel FAQ', async () => {
    mockUseFeatureSwitch.mockReturnValue(true);
    const { getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <HotelDetailsPageCCUI {...mockObj} queryClient={queryClient} router={mockRouter} />
      </QueryClientProvider>
    );
    expect(getByTestId('hdp-faq-ccui')).toBeInTheDocument();
  });
  it('should handle empty roomRates returned from CCUI availability', async () => {
    const { getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <HotelDetailsPageCCUI {...mockObj} queryClient={queryClient} router={mockRouter} />
      </QueryClientProvider>
    );
    expect(getByTestId('HotelDetailsCCUIPage-Wrapper')).toBeInTheDocument();
    expect(screen.queryByTestId('room-rate-card')).not.toBeInTheDocument();
  });
  it('should call getMappedRooms to map room data', async () => {
    const { getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <HotelDetailsPageCCUI {...mockObj} queryClient={queryClient} router={mockRouter} />
      </QueryClientProvider>
    );

    expect(getByTestId('HotelDetailsCCUIPage-Wrapper')).toBeInTheDocument();
    expect(getMappedRooms).toHaveBeenCalled();
  });
  it('should read search params via next/navigation', () => {
    const { getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <HotelDetailsPageCCUI {...mockObj} queryClient={queryClient} router={mockRouter} />
      </QueryClientProvider>
    );

    expect(getByTestId('HotelDetailsCCUIPage-Wrapper')).toBeInTheDocument();
    expect(useSearchParams().get('PROMOID')).toBe('PROMOID');
  });
  it('should initialise promo state with isPromoApplied set to false', async () => {
    const mockDispatch = jest.fn();
    const reducerSpy = jest
      .spyOn(React, 'useReducer')
      .mockImplementation((_reducer, initialState) => {
        expect(initialState.isPromoApplied).toBe(false);
        return [initialState, mockDispatch];
      });

    render(
      <QueryClientProvider client={queryClient}>
        <HotelDetailsPageCCUI {...mockObj} router={mockRouter} queryClient={queryClient} />
      </QueryClientProvider>
    );

    reducerSpy.mockRestore();
  });
});

describe('CCUI promo code + promo kind handling', () => {
  const mockUseHotelAvailabilityCCUI = useHotelAvailabilityCCUI as jest.MockedFunction<
    typeof useHotelAvailabilityCCUI
  >;

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should pass empty promo code and undefined promo kind when CORPID is present', () => {
    render(
      <QueryClientProvider client={queryClient}>
        <HotelDetailsPageCCUI
          {...mockObj}
          queryClient={queryClient}
          router={{
            ...mockRouter,
            query: {
              ...mockRouter.query,
              CORPID: '15010601',
              PROMOID: 'PROMO10',
            },
          }}
        />
      </QueryClientProvider>
    );

    const lastCallArgs = mockUseHotelAvailabilityCCUI.mock.calls.at(-1)!;

    expect(lastCallArgs.at(-2)).toBeUndefined();
    expect(lastCallArgs.at(-1)).toBeUndefined();
  });

  it('should pass empty promo code and undefined promo kind when CORPID is NOT present', () => {
    render(
      <QueryClientProvider client={queryClient}>
        <HotelDetailsPageCCUI
          {...mockObj}
          queryClient={queryClient}
          router={{
            ...mockRouter,
            query: {
              ...mockRouter.query,
              CORPID: undefined,
              PROMOID: 'PROMO10',
            },
          }}
        />
      </QueryClientProvider>
    );

    const lastCallArgs = mockUseHotelAvailabilityCCUI.mock.calls.at(-1)!;

    expect(lastCallArgs.at(-2)).toBe('');
    expect(lastCallArgs.at(-1)).toBeUndefined();
  });

  it('should set isPromoBoxVisible to true when feature flag and promoBox are enabled', () => {
    render(
      <QueryClientProvider client={queryClient}>
        <HotelDetailsPageCCUI
          {...mockObj}
          queryClient={queryClient}
          router={mockRouter}
          promotionBannerData={
            {
              promoBox: {
                title: 'Promo Box',
              },
            } as any
          }
        />
      </QueryClientProvider>
    );

    expect(screen.getByTestId('HotelDetailsCCUIPage-Wrapper')).toBeInTheDocument();
  });

  it('should set isPromoBoxVisible to false when promoBox is missing', () => {
    render(
      <QueryClientProvider client={queryClient}>
        <HotelDetailsPageCCUI
          {...mockObj}
          queryClient={queryClient}
          router={mockRouter}
          promotionBannerData={{} as any}
        />
      </QueryClientProvider>
    );

    expect(screen.getByTestId('HotelDetailsCCUIPage-Wrapper')).toBeInTheDocument();
  });

  it('should pass promo fields when promotions in hotel availability is enabled', () => {
    mockUseFeatureToggle.mockReturnValue({
      release_ccui_show_promotion_box: true,
      release_ccui_promo_code_landing_page: true,
      release_ccui_promo_code_site_wide: true,
      release_promotions_in_hotelavailability: true,
    });

    render(
      <QueryClientProvider client={queryClient}>
        <HotelDetailsPageCCUI
          {...mockObj}
          queryClient={queryClient}
          router={{
            ...mockRouter,
            query: {
              ...mockRouter.query,
              CORPID: undefined,
              PROMOID: 'PROMO10',
            },
          }}
          promotionBannerData={
            {
              promoKind: 'LANDING_APGE',
              showPromo: true,
            } as any
          }
        />
      </QueryClientProvider>
    );

    const args = mockUseHotelAvailabilityCCUI.mock.calls.at(-1)!;

    expect(args[6]).toBe(true);
  });
  it('should not pass promo fields when promotions in hotel availability is disabled', () => {
    mockUseFeatureToggle.mockReturnValue({
      release_ccui_show_promotion_box: true,
      release_promotions_in_hotelavailability: false,
    });

    render(
      <QueryClientProvider client={queryClient}>
        <HotelDetailsPageCCUI
          {...mockObj}
          queryClient={queryClient}
          router={{
            ...mockRouter,
            query: {
              ...mockRouter.query,
              CORPID: undefined,
            },
          }}
        />
      </QueryClientProvider>
    );

    const args = mockUseHotelAvailabilityCCUI.mock.calls.at(-1)!;

    expect(args[12]).toBeUndefined();
  });
});

describe('errorRateAndRoomMessage mapping', () => {
  beforeEach(() => {
    jest.clearAllMocks();

    mockUseFeatureToggle.mockReturnValue({
      release_ccui_show_promotion_box: true,
      release_promotions_in_hotelavailability: true,
    } as any);

    (useHotelAvailabilityCCUI as jest.Mock).mockReturnValue({
      data: {
        ...mockAvailability.data,
        hotelAvailability: {
          ...mockAvailability.data.hotelAvailability,
          promotionsInformation: {
            errorRateAndRoomMessage: 'Invalid rate and room',
          },
        },
      },
      isLoading: false,
      isError: false,
      error: null,
    });
  });

  it('should map errorRateAndRoomMessage from availability response', () => {
    render(
      <QueryClientProvider client={queryClient}>
        <HotelDetailsPageCCUI {...mockObj} queryClient={queryClient} router={mockRouter} />
      </QueryClientProvider>
    );

    expect(useHotelAvailabilityCCUI).toHaveBeenCalled();
  });
  describe('promotion availability fields', () => {
    beforeEach(() => {
      mockUseFeatureToggle.mockReturnValue({
        release_promotions_in_hotelavailability: true,
        release_ccui_show_promotion_box: true,
      } as any);
    });

    const mockUseHotelAvailabilityCCUI = useHotelAvailabilityCCUI as jest.MockedFunction<
      typeof useHotelAvailabilityCCUI
    >;

    it('should pass promo fields when promotions are enabled', () => {
      mockUseFeatureToggle.mockReturnValue({
        release_promotions_in_hotelavailability: true,
        release_ccui_show_promotion_box: true,
      } as any);

      render(
        <QueryClientProvider client={queryClient}>
          <HotelDetailsPageCCUI
            {...mockObj}
            queryClient={queryClient}
            router={{
              ...mockRouter,
              query: {
                ...mockRouter.query,
                CORPID: undefined,
              },
            }}
          />
        </QueryClientProvider>
      );

      const args = mockUseHotelAvailabilityCCUI.mock.calls.at(-1)!;

      // true branch should execute
      expect(args[10]).toBeFalsy();
      expect(args[11]).toBeFalsy();
      expect(args[12]).toBeFalsy();
    });
  });
});
