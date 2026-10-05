import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import {
  FT_PI_BB_CCUI_BARRIER_FREE_LABEL,
  FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN,
  FT_BB_SHOW_PROMOTION_BOX,
  FT_PI_CCUI_BB_PROMOTION_BOX_CODE_COOKIE,
  FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY,
} from '@whitbread-eos/api';
import {
  updateHotelDisplayPageAnalytics,
  useHotelAvailabilityBB,
  useHotelRatesInformationBB,
} from '@whitbread-eos/utils';

import { mockAvailability, mockStaticData, visualDisplayContext } from '~mocks/hotel-details';

import HotelDetailsPageBB from './page.bb';

const mockCustomLocale = jest.fn();
const mockUseFeatureSwitch = jest.fn();
const queryClient = new ReactQuery.QueryClient();
const mockUseFeatureToggle = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useFeatureToggle: () => mockUseFeatureToggle(),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  useCustomLocale: () => mockCustomLocale(),
  useStaticHotelInformation: () => mockStaticData,
  invalidateQueries: () => mockAvailability,
  useQuery: () => mockAvailability,
  useQueryRequest: () => mockAvailability,
  useHotelAvailabilityBB: jest.fn(() => ({
    isLoading: false,
    isError: false,
    data: {
      hotelAvailability: {
        available: true,
        roomRates: [{ id: '1' }],
      },
    },
    error: null,
  })),

  useHotelRatesInformationBB: jest.fn(() => ({
    isLoading: false,
    isError: false,
    data: {
      rates: [{ code: 'ABC' }],
    },
    error: null,
  })),

  useRestMutationRequest: () => ({
    mutation: jest.fn(),
  }),
  useFeatureSwitch: () => mockUseFeatureSwitch(),
  getMappedRooms: jest.fn(),
  swapKeysAndValues: jest.fn(),
  getRoomTypesFromQuery: jest.fn(),
  updateHotelDisplayPageAnalytics: jest.fn(),
}));

jest.mock('@whitbread-eos/organisms', () => ({
  BBSearchContainer: () => {
    return <div data-testid="dummy-search-container">BB Search Container</div>;
  },
  Location: jest.fn(() => <div>Location</div>),
  RateSelector: jest.fn(() => <div>Rate Selector</div>),
}));

jest.mock('@whitbread-eos/molecules', () => ({
  ...jest.requireActual('@whitbread-eos/molecules'),
  getNoOfDaysInYear: jest.fn(),
  SEO: () => <div />,
}));

const mockObj = {
  visualDisplayContext,
};

const mockUseRouter = jest.fn();

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

const mockRouter = {
  push: jest.fn(),
  query: {
    ARRdd: '1',
    ARRmm: '11',
    ARRyyyy: '2023',
    NIGHTS: '2',
    ROOMS: '1',
    ADULT1: '1',
    CHILD1: '0',
    COT1: '0',
    INTTYP1: 'DB',
    BRAND: 'BB',
    slug: ['england', 'greater-london', 'london', 'london-beckton.html'],
  },
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

describe('Page BB Hotel Details', () => {
  beforeAll(() => {
    mockUseRouter.mockReturnValue(mockRouter);
  });

  beforeEach(() => {
    mockCustomLocale.mockReturnValue({
      country: 'gb',
      language: 'en',
    });
    mockUseFeatureSwitch.mockReturnValue(false);
    mockUseFeatureToggle.mockReturnValue({
      [FT_PI_BB_CCUI_BARRIER_FREE_LABEL]: false,
      [FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN]: false,
      [FT_BB_SHOW_PROMOTION_BOX]: false,
      [FT_PI_CCUI_BB_PROMOTION_BOX_CODE_COOKIE]: false,
      [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: false,
    });
  });

  afterEach(() => {
    mockStaticData.isLoading = false;
    mockStaticData.isError = false;
    mockStaticData.error.message = '';
    mockStaticData.hotelId = 'LONEUS';
  });

  it('should render BB page', async () => {
    const { getByTestId } = render(
      <HotelDetailsPageBB {...mockObj} queryClient={queryClient} router={mockRouter as any} />
    );
    expect(getByTestId('HotelDetailsBBPage-Wrapper')).toBeInTheDocument();
  });
  it('should render BB page with default settings if no input is present', async () => {
    const { getByTestId } = render(
      <HotelDetailsPageBB
        {...mockObj}
        queryClient={queryClient}
        router={mockRouterWithIncompleteData as any}
      />
    );
    expect(getByTestId('HotelDetailsBBPage-Wrapper')).toBeInTheDocument();
  });

  it('should show a loading message if isLoading is true', async () => {
    mockStaticData.isLoading = true;
    const { getByText } = render(
      <HotelDetailsPageBB {...mockObj} queryClient={queryClient} router={mockRouter as any} />
    );
    expect(getByText('searchresults.list.hotel.loading')).toBeInTheDocument();
  });

  it('should render an error message if isError is true', async () => {
    mockStaticData.isError = true;
    mockStaticData.error.message = 'Error message here';
    const { getByText } = render(
      <HotelDetailsPageBB {...mockObj} queryClient={queryClient} router={mockRouter as any} />
    );
    expect(getByText('Error message here')).toBeInTheDocument();
  });

  it('should render 404 error page if no hotelId', async () => {
    mockStaticData.hotelId = '';
    render(
      <HotelDetailsPageBB {...mockObj} queryClient={queryClient} router={mockRouter as any} />
    );
    screen.getByRole('heading', { name: /404/i });
    screen.getByRole('heading', { name: /This page could not be found/i });
  });

  it('Should render Hotel FAQ', async () => {
    mockUseFeatureSwitch.mockReturnValue(true);
    const { getByTestId } = render(
      <HotelDetailsPageBB {...mockObj} queryClient={queryClient} router={mockRouter as any} />
    );
    expect(getByTestId('hdp-faq-bb')).toBeInTheDocument();
  });

  it('should use default search params when ARRdd is missing', () => {
    const routerWithMissingArrival = {
      ...mockRouter,
      query: {
        ...mockRouter.query,
        ARRdd: undefined,
      },
    };

    render(
      <HotelDetailsPageBB
        {...mockObj}
        queryClient={queryClient}
        router={routerWithMissingArrival as any}
      />
    );

    expect(screen.getByTestId('HotelDetailsBBPage-Wrapper')).toBeInTheDocument();
  });

  it('should use default search params when NIGHTS is missing', () => {
    const routerWithMissingNights = {
      ...mockRouter,
      query: {
        ...mockRouter.query,
        NIGHTS: undefined,
      },
    };

    render(
      <HotelDetailsPageBB
        {...mockObj}
        queryClient={queryClient}
        router={routerWithMissingNights as any}
      />
    );

    expect(screen.getByTestId('HotelDetailsBBPage-Wrapper')).toBeInTheDocument();
  });

  it('should use default search params when ROOMS is missing', () => {
    const routerWithMissingRooms = {
      ...mockRouter,
      query: {
        ...mockRouter.query,
        ROOMS: undefined,
      },
    };

    render(
      <HotelDetailsPageBB
        {...mockObj}
        queryClient={queryClient}
        router={routerWithMissingRooms as any}
      />
    );

    expect(screen.getByTestId('HotelDetailsBBPage-Wrapper')).toBeInTheDocument();
  });

  it('should use default search params when date is invalid', () => {
    const routerWithInvalidDate = {
      ...mockRouter,
      query: {
        ...mockRouter.query,
        ARRdd: '99',
        ARRmm: '99',
        ARRyyyy: '2023',
      },
    };

    render(
      <HotelDetailsPageBB
        {...mockObj}
        queryClient={queryClient}
        router={routerWithInvalidDate as any}
      />
    );

    expect(screen.getByTestId('HotelDetailsBBPage-Wrapper')).toBeInTheDocument();
  });

  it('should pass undefined dataHotelAvailability when hotel information data is missing', () => {
    (useHotelRatesInformationBB as jest.Mock).mockReturnValue({
      isLoading: false,
      isError: false,
      data: undefined,
      error: null,
    });

    render(
      <HotelDetailsPageBB {...mockObj} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(updateHotelDisplayPageAnalytics).toHaveBeenCalledWith(
      expect.objectContaining({
        dataHotelAvailability: undefined,
      })
    );
  });
  it('should pass promo rateName, roomClass and isPromoBox when promotions in hotel availability is enabled', () => {
    mockUseFeatureToggle.mockReturnValue({
      [FT_PI_BB_CCUI_BARRIER_FREE_LABEL]: false,
      [FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN]: false,
      [FT_BB_SHOW_PROMOTION_BOX]: false,
      [FT_PI_CCUI_BB_PROMOTION_BOX_CODE_COOKIE]: false,
      [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: true,
    });

    render(
      <HotelDetailsPageBB {...mockObj} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(useHotelAvailabilityBB).toHaveBeenCalled();
  });
  it('should not pass promo rateName, roomClass and isPromoBox when promotions in hotel availability is disabled', () => {
    mockUseFeatureToggle.mockReturnValue({
      [FT_PI_BB_CCUI_BARRIER_FREE_LABEL]: false,
      [FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN]: false,
      [FT_BB_SHOW_PROMOTION_BOX]: false,
      [FT_PI_CCUI_BB_PROMOTION_BOX_CODE_COOKIE]: false,
      [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: true,
    });

    render(
      <HotelDetailsPageBB {...mockObj} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(useHotelAvailabilityBB).toHaveBeenCalled();
  });
});

describe('Promo analytics coverage', () => {
  beforeEach(() => {
    jest.clearAllMocks();

    mockCustomLocale.mockReturnValue({
      country: 'gb',
      language: 'en',
    });

    mockUseFeatureSwitch.mockReturnValue(false);
  });

  it('should call updateHotelDisplayPageAnalytics with merged availability data', () => {
    render(
      <HotelDetailsPageBB {...mockObj} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(updateHotelDisplayPageAnalytics).toHaveBeenCalled();

    const analyticsCall = (updateHotelDisplayPageAnalytics as jest.Mock).mock.calls.at(-1)?.[0];

    expect(analyticsCall.hotelAvailability).toBe('available');
  });

  it('should not call analytics when hotelName is not available', () => {
    mockStaticData.name = '';

    render(
      <HotelDetailsPageBB {...mockObj} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(updateHotelDisplayPageAnalytics).not.toHaveBeenCalled();

    mockStaticData.name = 'Test Hotel';
  });

  it('should send unavailable hotel analytics when availability is false', () => {
    (useHotelAvailabilityBB as jest.Mock).mockReturnValue({
      isLoading: false,
      isError: false,
      data: {
        hotelAvailability: {
          available: false,
          roomRates: [],
        },
      },
      error: null,
    });

    render(
      <HotelDetailsPageBB {...mockObj} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(updateHotelDisplayPageAnalytics).toHaveBeenCalledWith(
      expect.objectContaining({
        hotelAvailability: 'unavailable',
      })
    );
  });

  it('should pass undefined dataHotelAvailability when hotel information data is missing', () => {
    (useHotelRatesInformationBB as jest.Mock).mockReturnValue({
      isLoading: false,
      isError: false,
      data: undefined,
      error: null,
    });

    render(
      <HotelDetailsPageBB {...mockObj} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(updateHotelDisplayPageAnalytics).toHaveBeenCalledWith(
      expect.objectContaining({
        dataHotelAvailability: undefined,
      })
    );
  });
});
