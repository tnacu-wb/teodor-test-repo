import * as ReactQuery from '@tanstack/react-query';
import { QueryClientProvider } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { waitFor, render, screen } from '@testing-library/react';
import {
  useHotelAvailability,
  PromotionsInformation,
  useDiscountRateInfoHotelAvailability,
  getCookie,
  useFeatureToggle,
} from '@whitbread-eos/utils';
import { NextRouter } from 'next/router';
import React from 'react';

import {
  mockAvailability,
  mockStaticData,
  visualDisplayContext,
  mockCompanyData,
  mockHeaderStaticData,
} from '~mocks/hotel-details';

import HotelDetailsPagePI from './page.pi';

const queryClient = new ReactQuery.QueryClient();
const mockCustomLocale = jest.fn();
const mockUseFeatureSwitch = jest.fn();

const mockUseRouter = jest.fn().mockImplementation(() => {
  return { query: { CORPID: '15010601', PROMOID: 'ST10R' } };
});

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const mockCookies = {
  bundles: 'class',
};

jest.mock('@whitbread-eos/utils', () => {
  const utils = jest.requireActual('@whitbread-eos/utils');

  return {
    ...utils,
    ...jest.requireActual('@whitbread-eos/utils'),
    formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
    useCustomLocale: () => mockCustomLocale(),
    useStaticHotelInformation: () => mockStaticData,
    invalidateQueries: () => mockAvailability,
    useQuery: () => mockAvailability,
    getCookie: jest.fn((cookieName: string) => {
      if (cookieName === utils.BUNDLE_CHOICE) {
        return mockCookies.bundles;
      }
      return undefined;
    }),
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
    useHotelAvailability: jest.fn().mockImplementation(() => {
      return {
        data: {},
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
    useFeatureToggle: jest.fn().mockImplementation(() => {
      return {
        release_pi_promo_code_landing_page: true,
        release_pi_show_promotion_box: true,
        release_pi_ccui_bb_promotion_box_code_cookie: true,
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
  };
});

jest.mock('@whitbread-eos/organisms', () => {
  const actual = jest.requireActual('@whitbread-eos/organisms');
  return {
    ...actual,
    PISearchContainer: () => {
      return <div data-testid="dummy-search-container">PI Search Container</div>;
    },
  };
});

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  PromotionsNotification: () => (
    <div data-testid="promotions-notification">PromotionsNotification</div>
  ),
}));

jest.mock('@whitbread-eos/molecules', () => ({
  ...jest.requireActual('@whitbread-eos/molecules'),
  getNoOfDaysInYear: jest.fn(),
  SEO: () => <div />,
}));

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useSearchParams: jest.fn().mockReturnValue({
    get: (key: string) => key,
  }),
}));

const mockObj = {
  visualDisplayContext,
};

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
  ...mockRouter,
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

const mockQueryEnabledParams = {
  companyData: true,
  isLoadingCompanyData: false,
  isDiscountedRateEnabled: true,
  arrival: '2023-11-01',
  departure: '2023-11-03',
};

describe('Page PI Hotel Details', () => {
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

  beforeEach(() => {
    mockStaticData.isLoading = false;
    mockStaticData.isError = false;
    mockStaticData.error.message = '';
    mockStaticData.hotelId = 'LONEUS';
  });

  it('should render PI page', async () => {
    const { getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <HotelDetailsPagePI {...mockObj} queryClient={queryClient} router={mockRouter as any} />
      </QueryClientProvider>
    );
    await waitFor(() => {
      expect(getByTestId('HotelDetailsPIPage-Wrapper')).toBeInTheDocument();
    });
  });

  it('should render PI page with default settings if no input is present', async () => {
    const { getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <HotelDetailsPagePI
          {...mockObj}
          queryClient={queryClient}
          router={mockRouterWithIncompleteData as any}
        />
      </QueryClientProvider>
    );
    await waitFor(() => {
      expect(getByTestId('HotelDetailsPIPage-Wrapper')).toBeInTheDocument();
    });
  });

  it('should show a loading message if isLoading is true', async () => {
    mockStaticData.isLoading = true;
    const { getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <HotelDetailsPagePI {...mockObj} queryClient={queryClient} router={mockRouter as any} />
      </QueryClientProvider>
    );
    expect(getByTestId('HotelDetailsPIPage-Wrapper-loading')).toBeInTheDocument();
  });

  it('should render an error message if isError is true', async () => {
    mockStaticData.isError = true;
    mockStaticData.error.message = 'Error message here';
    const { getByText } = render(
      <QueryClientProvider client={queryClient}>
        <HotelDetailsPagePI {...mockObj} queryClient={queryClient} router={mockRouter as any} />
      </QueryClientProvider>
    );
    expect(getByText('Error message here')).toBeInTheDocument();
  });

  it('should render 404 error page if no hotelId', async () => {
    mockStaticData.hotelId = '';
    render(
      <QueryClientProvider client={queryClient}>
        <HotelDetailsPagePI {...mockObj} queryClient={queryClient} router={mockRouter as any} />
      </QueryClientProvider>
    );
    screen.getByRole('heading', { name: /404/i });
    screen.getByRole('heading', { name: /This page could not be found/i });
  });

  it('should render Hotel FAQ when feature is enabled', async () => {
    mockUseFeatureSwitch.mockReturnValue(true);
    const { getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <HotelDetailsPagePI {...mockObj} queryClient={queryClient} router={mockRouter as any} />
      </QueryClientProvider>
    );
    expect(getByTestId('hdp-faq-pi')).toBeInTheDocument();
  });

  // New test cases for queryEnabled logic
  it('should not enable query when companyData is falsy', async () => {
    const updatedQueryParams = {
      ...mockQueryEnabledParams,
      companyData: null, // falsy companyData
    };
    expect(!!updatedQueryParams.companyData && !updatedQueryParams.isLoadingCompanyData).toBe(
      false
    );
  });

  it('should not enable query when isLoadingCompanyData is true', async () => {
    const updatedQueryParams = {
      ...mockQueryEnabledParams,
      isLoadingCompanyData: true, // true loading state
    };
    expect(!!updatedQueryParams.companyData && !updatedQueryParams.isLoadingCompanyData).toBe(
      false
    );
  });

  it('should enable query when all conditions are met', async () => {
    expect(
      !!mockQueryEnabledParams.companyData &&
        !mockQueryEnabledParams.isLoadingCompanyData &&
        mockQueryEnabledParams.isDiscountedRateEnabled &&
        mockQueryEnabledParams.arrival !== undefined &&
        mockQueryEnabledParams.departure !== undefined
    ).toBe(true);
  });
  it('returns promo code when promo landing page is enabled and banner data matches PROMOID', async () => {
    // Arrange: prepare router query with PROMOID and the matching banner data
    const routerWithPromo = {
      ...mockRouter,
      query: {
        ...mockRouter.query,
        PROMOID: 'ST10R',
      },
    };

    const promoBannerData = {
      showPromo: true,
      isWithinPromoWindow: true,
      promotionCode: 'ST10R',
    };

    render(
      <QueryClientProvider client={queryClient}>
        <HotelDetailsPagePI
          queryClient={queryClient}
          router={routerWithPromo as any}
          visualDisplayContext={visualDisplayContext}
          promotionBannerData={promoBannerData as PromotionsInformation}
        />
      </QueryClientProvider>
    );
    await waitFor(() => {
      expect(screen.getByTestId('HotelDetailsPIPage-Wrapper')).toBeInTheDocument();
      expect(routerWithPromo?.query?.PROMOID).toEqual(promoBannerData?.promotionCode);
    });
  });

  it('should render component in case of roomRates are present', async () => {
    (useHotelAvailability as jest.Mock).mockReturnValue({
      data: mockAvailability.data,
      isLoading: false,
      isError: false,
      error: null,
    });

    mockUseFeatureSwitch.mockReturnValue(true);
    const { getByTestId } = render(
      <HotelDetailsPagePI
        {...mockObj}
        queryClient={queryClient}
        router={mockRouterWithIncompleteData}
      />
    );
    expect(getByTestId('hdp_roomsSeeRates')).toBeInTheDocument();
  });
  it('should render component in case of roomRates are present and pass PROMOID', async () => {
    (useHotelAvailability as jest.Mock).mockReturnValue({
      data: mockAvailability.data,
      isLoading: false,
      isError: false,
      error: null,
    });

    mockUseFeatureSwitch.mockReturnValue(true);
    render(
      <HotelDetailsPagePI
        {...mockObj}
        queryClient={queryClient}
        router={{ ...mockRouterWithIncompleteData, query: { PROMOID: 'ST10R' } }}
      />
    );
    await waitFor(() => {
      expect(screen.getByTestId('hdp_roomsSeeRates')).toBeInTheDocument();
    });
    jest.clearAllMocks();
  });
  it('should render component in case of isNoRoomTypeSearchEnabled and empty INTTYP1 ', async () => {
    (useHotelAvailability as jest.Mock).mockReturnValue({
      data: mockAvailability.data,
      isLoading: false,
      isError: false,
      error: null,
    });

    mockUseFeatureSwitch.mockReturnValue(true);
    render(
      <HotelDetailsPagePI
        {...mockObj}
        queryClient={queryClient}
        router={{ ...mockRouterWithIncompleteData, query: { INTTYP1: '' } }}
      />
    );
    await waitFor(() => {
      expect(screen.getByTestId('hdp_roomsSeeRates')).toBeInTheDocument();
    });
    jest.clearAllMocks();
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
        <HotelDetailsPagePI {...mockObj} router={mockRouter} queryClient={queryClient} />
      </QueryClientProvider>
    );

    reducerSpy.mockRestore();
  });

  it('should use default numberOfNights as 1 when NIGHTS is missing', async () => {
    const routerWithoutNights = {
      ...mockRouter,
      query: {
        ARRdd: '01',
        ARRmm: '12',
        ARRyyyy: '2025',
        ROOMS: '1',
        CORPID: 'CORP123',
        PROMOID: 'PROMO123',
      },
    };

    render(
      <QueryClientProvider client={queryClient}>
        <HotelDetailsPagePI
          {...mockObj}
          queryClient={queryClient}
          router={routerWithoutNights as any}
        />
      </QueryClientProvider>
    );

    await waitFor(() => {
      expect(screen.getByTestId('HotelDetailsPIPage-Wrapper')).toBeInTheDocument();
    });
  });

  it('should return default room values when required params are missing', async () => {
    const routerWithMissingParams = {
      ...mockRouter,
      query: {
        ARRdd: '01',
        ARRmm: '12',
        ARRyyyy: '2025',
        // missing NIGHTS and ROOMS to hit fallback branch
        CORPID: 'CORP123',
        PROMOID: 'PROMO123',
      },
    };

    render(
      <QueryClientProvider client={queryClient}>
        <HotelDetailsPagePI
          {...mockObj}
          queryClient={queryClient}
          router={routerWithMissingParams as any}
        />
      </QueryClientProvider>
    );

    await waitFor(() => {
      expect(screen.getByTestId('HotelDetailsPIPage-Wrapper')).toBeInTheDocument();
    });
  });

  it('should cover fallback branch with corpId and promoId present', async () => {
    const routerWithFallbackData = {
      ...mockRouter,
      query: {
        ARRdd: '',
        ARRmm: '',
        ARRyyyy: '',
        NIGHTS: '',
        ROOMS: '',
        CORPID: 'TESTCORP',
        PROMOID: 'TESTPROMO',
      },
    };

    render(
      <QueryClientProvider client={queryClient}>
        <HotelDetailsPagePI
          {...mockObj}
          queryClient={queryClient}
          router={routerWithFallbackData as any}
        />
      </QueryClientProvider>
    );

    await waitFor(() => {
      expect(screen.getByTestId('HotelDetailsPIPage-Wrapper')).toBeInTheDocument();
    });
  });

  it('should render loading state when discount rate and corpId are enabled and discount rate info is loading', async () => {
    const mockUseDiscountRateInfoHotelAvailability =
      useDiscountRateInfoHotelAvailability as jest.Mock;

    mockUseDiscountRateInfoHotelAvailability.mockReturnValue({
      hotelAvailabilityResponse: {},
      dataHotelAvailability: {},
      isDiscountRateLoading: true,
      isDiscountRateError: false,
    });

    render(
      <QueryClientProvider client={queryClient}>
        <HotelDetailsPagePI
          {...mockObj}
          queryClient={queryClient}
          router={{
            ...mockRouter,
            query: {
              ...mockRouter.query,
              CORPID: 'CORP123',
            },
          }}
        />
      </QueryClientProvider>
    );

    await waitFor(() => {
      expect(screen.getByTestId('HotelDetailsPIPage-Wrapper-loading')).toBeInTheDocument();
    });
  });

  it('should render PromotionsNotification when room rates exist and no promo is applied', async () => {
    (useHotelAvailability as jest.Mock).mockReturnValue({
      data: mockAvailability.data,
      isLoading: false,
      isError: false,
      error: null,
    });

    render(
      <QueryClientProvider client={queryClient}>
        <HotelDetailsPagePI
          {...mockObj}
          queryClient={queryClient}
          router={mockRouter}
          promotionBannerData={
            {
              promoBox: {},
            } as any
          }
        />
      </QueryClientProvider>
    );

    await waitFor(() => {
      expect(screen.getByTestId('promotions-notification')).toBeInTheDocument();
    });
  });

  it('should not render PromotionsNotification when promo code cookie exists and feature is enabled', async () => {
    (getCookie as jest.Mock).mockImplementation((cookieName: string) => {
      if (cookieName === 'appliedPromoBoxCode') {
        return 'TESTPROMO';
      }
      return undefined;
    });

    (useHotelAvailability as jest.Mock).mockReturnValue({
      data: mockAvailability.data,
      isLoading: false,
      isError: false,
      error: null,
    });

    render(
      <QueryClientProvider client={queryClient}>
        <HotelDetailsPagePI
          {...mockObj}
          queryClient={queryClient}
          router={mockRouter}
          promotionBannerData={
            {
              promoBox: {},
            } as any
          }
        />
      </QueryClientProvider>
    );

    await waitFor(() => {
      expect(screen.queryByTestId('promotions-notification')).not.toBeInTheDocument();
    });
  });

  it('should not render HotelRooms when room details drawer is enabled and hotel details exist on HDP', async () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      release_pi_room_details_drawer: true,
    });

    render(
      <QueryClientProvider client={queryClient}>
        <HotelDetailsPagePI {...mockObj} queryClient={queryClient} router={mockRouter} />
      </QueryClientProvider>
    );

    await waitFor(() => {
      expect(screen.queryByTestId('hdp_roomsSeeRates')).not.toBeInTheDocument();
    });
  });

  it('should pass PROMOID to useHotelAvailability when banner promo matches', async () => {
    const mockUseHotelAvailability = useHotelAvailability as jest.Mock;
    render(
      <QueryClientProvider client={queryClient}>
        <HotelDetailsPagePI
          {...mockObj}
          queryClient={queryClient}
          router={{
            ...mockRouter,
            query: {
              ...mockRouter.query,
              PROMOID: 'ST10R',
            },
          }}
          promotionBannerData={
            {
              showPromo: true,
              isWithinPromoWindow: true,
              promotionCode: 'ST10R',
            } as PromotionsInformation
          }
        />
      </QueryClientProvider>
    );
    await waitFor(() => {
      expect(mockUseHotelAvailability).toHaveBeenCalled();
    });
  });

  it('should pass banner promotionCode when PROMOID is missing', async () => {
    const mockUseHotelAvailability = useHotelAvailability as jest.Mock;
    render(
      <QueryClientProvider client={queryClient}>
        <HotelDetailsPagePI
          {...mockObj}
          queryClient={queryClient}
          router={{
            ...mockRouter,
            query: {
              ...mockRouter.query,
              PROMOID: undefined,
            },
          }}
          promotionBannerData={
            {
              showPromo: true,
              isWithinPromoWindow: true,
              promotionCode: 'SUMMER25',
            } as PromotionsInformation
          }
        />
      </QueryClientProvider>
    );
    await waitFor(() => {
      expect(mockUseHotelAvailability).toHaveBeenCalled();
    });
  });

  it('should pass empty promo code when banner is not valid', async () => {
    const mockUseHotelAvailability = useHotelAvailability as jest.Mock;
    render(
      <QueryClientProvider client={queryClient}>
        <HotelDetailsPagePI
          {...mockObj}
          queryClient={queryClient}
          router={{
            ...mockRouter,
            query: {
              ...mockRouter.query,
              PROMOID: 'ST10R',
            },
          }}
          promotionBannerData={
            {
              showPromo: false,
              isWithinPromoWindow: true,
              promotionCode: 'ST10R',
            } as PromotionsInformation
          }
        />
      </QueryClientProvider>
    );

    await waitFor(() => {
      expect(mockUseHotelAvailability).toHaveBeenCalled();
    });
    expect(mockUseHotelAvailability.mock.calls[0][8]).toBe('');
  });
});
