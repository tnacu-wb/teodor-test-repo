import { QueryClient } from '@tanstack/react-query';
import { fireEvent } from '@testing-library/dom';
import '@testing-library/jest-dom';
import {
  getCookie,
  getNewSearchResultsBB,
  useFeatureToggle,
  useMobileControlsDisplay,
  useScreenSize,
} from '@whitbread-eos/utils';

import {
  mockedGetStaticContent,
  mockedHotelAvailabilities,
  mockedPartialTranslations,
} from '../../mockData/mockResponse';
import { act, render, screen, waitFor } from '../../utils/test-utils';
import SearchResultsBBVariantContainer, {
  getSearchRedirectURL,
  NotificationWrapper,
} from './SearchResultsBBVariant.container';

jest.mock('react-infinite-scroll-component', () => {
  const MockInfiniteScroll = ({ next, children }: any) => (
    <div>
      <button data-testid="load-more" onClick={next}>
        Load More
      </button>
      {children}
    </div>
  );

  MockInfiniteScroll.displayName = 'MockInfiniteScroll';

  return MockInfiniteScroll;
});

jest.mock('@whitbread-eos/atoms', () => ({
  __esModule: true,
  ...jest.requireActual('@whitbread-eos/atoms'),

  PromotionsNotification: () => <span data-testid="promo" />,

  InfiniteScroller: ({ children, next }: any) => (
    <div>
      <button data-testid="load-more" onClick={next}>
        Load More
      </button>
      {children}
    </div>
  ),
}));

jest.mock('../utilities', () => ({
  ...jest.requireActual('../utilities'),
  getSearchQueryUrl: jest
    .fn()
    .mockReturnValue(
      'searchModel.searchTerm=London,%20UK&PLACEID=ChIJdd4hrwug2EcRmSrV3Vo6llI&ARRdd=1&ARRmm=2&ARRyyyy=2023&NIGHTS=1&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB'
    ),
}));

const defaultFeatureToggles = {
  release_ib_enabled: true,
  release_bb_sort_order_dropdown: false,
  release_promotions_in_hotelavailability: true,
  release_pi_promo_code_landing_page: true,
  release_pi_promo_code_site_wide: true,
};
const mockFetchPartialTranslations = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getNewSearchResultsBB: jest.fn(),
  getCookie: jest.fn(),
  useFeatureToggle: jest.fn(),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  graphQLRequest: jest.fn(() => ({
    hotelAvailabilities: mockedHotelAvailabilities.data,
  })),
  isDateValid: jest.fn(),
  useQueryRequest: jest.fn().mockImplementation((queryKey: string | any[]) => {
    let queryKeyValue = queryKey;
    if (Array.isArray(queryKey)) {
      queryKeyValue = queryKey[0];
    }
    switch (queryKeyValue) {
      case 'searchInformation':
        return mockFetchPartialTranslations();
      case 'GetStaticContent':
        return {
          ...mockedGetStaticContent,
        };
      default:
        return {};
    }
  }),
  useMobileControlsDisplay: jest.fn(() => false),
  useScreenSize: jest.fn(() => ({ isLessThanMd: false })),
}));

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const mockedDefaultParameters = {
  arrivalDay: 25,
  arrivalMonth: 11,
  arrivalYear: 2023,
  location: 'London',
  numberOfNights: 4,
  rooms: [{ adultsNumber: 2, childrenNumber: 0, type: 'Double' }],
  bookingChannel: 'WEB',
  placeId: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
  sort: 'DISTANCE',
  view: '2',
  code: '',
};

beforeEach(() => {
  (useFeatureToggle as jest.Mock).mockReturnValue({ ...defaultFeatureToggles });
});

const mockGetNewSearchResultsBB = getNewSearchResultsBB as jest.Mock;

describe('SearchResults container', () => {
  beforeAll(() => {
    window.localStorage.setItem(
      'StayDetailsState',
      '{"timestamp":1675236414754,"data":{"arrival":"2023-02-01T07:26:47.474Z","info":{"suggestion":{"location":{"longitude":null,"latitude":null},"name":"London, UK","text":"London, UK"},"text":"London, UK"},"nights":1,"rooms":[{"adults":1,"children":0,"cot":false,"number":1,"type":{"code":"DB","name":"double"}}],"bookingChannel":"WEB"}}'
    );
  });

  beforeEach(() => {
    mockUseRouter.mockReturnValue({
      locale: 'en',
      query: { language: 'en', country: 'gb', VIEW: '2', SORT: '1' },
      push: jest.fn(),
    });
    mockFetchPartialTranslations.mockImplementation(() => {
      return {
        ...mockedPartialTranslations,
      };
    });
    mockGetNewSearchResultsBB.mockReset();
    mockGetNewSearchResultsBB.mockResolvedValue({
      results: mockedHotelAvailabilities.data.multiHotelAvailabilities,
      total: mockedHotelAvailabilities.data.total,
      promotionsInformation: mockedHotelAvailabilities.data.promotionsInformation,
    });
  });

  it('should show a loading state for each query', async () => {
    mockedPartialTranslations.isLoading = true;
    mockedGetStaticContent.isLoading = true;
    mockedHotelAvailabilities.isLoading = true;
    const { getByTestId } = render(
      <SearchResultsBBVariantContainer
        queryClient={new QueryClient()}
        multiSearchParams={mockedDefaultParameters}
        onNoHotelsWarning={jest.fn()}
        variant={'bb'}
      />
    );

    await waitFor(() => {
      expect(getByTestId('loading-spinner')).toBeInTheDocument();
    });
  });

  it('should show an error state for each labels query', async () => {
    mockedPartialTranslations.isLoading = false;
    mockedGetStaticContent.isLoading = false;
    mockedHotelAvailabilities.isLoading = false;
    mockedPartialTranslations.isError = true;
    mockedGetStaticContent.isError = true;

    mockFetchPartialTranslations.mockImplementation(() => {
      return {
        isLoading: false,
        isError: true,
        error: { message: 'error' },
        data: null,
      };
    });

    const { getByText } = render(
      <SearchResultsBBVariantContainer
        queryClient={new QueryClient()}
        multiSearchParams={mockedDefaultParameters}
        onNoHotelsWarning={jest.fn()}
        variant={'bb'}
      />
    );

    await waitFor(() => {
      expect(getByText('Error on loading translations...')).toBeInTheDocument();
    });

    mockedPartialTranslations.isError = false;
    mockedGetStaticContent.isError = false;
  });

  it('should show controls after hotels loaded', async () => {
    mockedPartialTranslations.isError = false;
    mockedGetStaticContent.isError = false;
    mockedHotelAvailabilities.isError = false;
    mockedPartialTranslations.isLoading = false;
    mockedGetStaticContent.isLoading = false;

    let renderScreen;
    await act(async () => {
      renderScreen = render(
        <SearchResultsBBVariantContainer
          queryClient={new QueryClient()}
          multiSearchParams={mockedDefaultParameters}
          onNoHotelsWarning={jest.fn()}
          variant={'bb'}
        />
      );
    });

    const { getByTestId } = renderScreen;

    await waitFor(() => {
      expect(getByTestId('SRP-controls-filter-by-button')).toBeInTheDocument();
      expect(getByTestId('DropdownComp-sort-by-menuButton')).toBeInTheDocument();
      expect(getByTestId('controls-map-or-list-button')).toBeInTheDocument();
    });
  });

  it('should render page when IB is false', async () => {
    mockedPartialTranslations.isError = false;
    mockedGetStaticContent.isError = false;
    mockedHotelAvailabilities.isError = false;
    mockedPartialTranslations.isLoading = false;
    mockedGetStaticContent.isLoading = false;

    let renderScreen;
    await act(async () => {
      renderScreen = render(
        <SearchResultsBBVariantContainer
          queryClient={new QueryClient()}
          multiSearchParams={mockedDefaultParameters}
          onNoHotelsWarning={jest.fn()}
          variant={'bb'}
          innBusiness={false}
        />
      );
    });

    const { getByTestId } = renderScreen;

    await waitFor(() => {
      expect(getByTestId('SRP-controls-filter-by-button')).toBeInTheDocument();
      expect(getByTestId('DropdownComp-sort-by-menuButton')).toBeInTheDocument();
      expect(getByTestId('controls-map-or-list-button')).toBeInTheDocument();
    });
  });

  it('should render page when IB is true', async () => {
    mockedPartialTranslations.isError = false;
    mockedGetStaticContent.isError = false;
    mockedHotelAvailabilities.isError = false;
    mockedPartialTranslations.isLoading = false;
    mockedGetStaticContent.isLoading = false;

    const longStayParams = { ...mockedDefaultParameters, numberOfNights: 16 };

    let renderScreen;
    await act(async () => {
      renderScreen = render(
        <SearchResultsBBVariantContainer
          queryClient={new QueryClient()}
          multiSearchParams={longStayParams}
          onNoHotelsWarning={jest.fn()}
          variant={'bb'}
          innBusiness={true}
        />
      );
    });

    const { getByTestId } = renderScreen;

    await waitFor(() => {
      expect(getByTestId('SRP-controls-filter-by-button')).toBeInTheDocument();
      expect(getByTestId('DropdownComp-sort-by-menuButton')).toBeInTheDocument();
      expect(getByTestId('controls-map-or-list-button')).toBeInTheDocument();
    });
  });

  it('should change route after sort was changed', async () => {
    const mockRouter = {
      query: { language: 'en', country: 'gb', VIEW: '2', SORT: '1' },
      push: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    mockedPartialTranslations.isError = false;
    mockedGetStaticContent.isError = false;
    mockedHotelAvailabilities.isError = false;
    mockedPartialTranslations.isLoading = false;
    mockedGetStaticContent.isLoading = false;

    const { getByTestId, queryByTestId } = render(
      <SearchResultsBBVariantContainer
        queryClient={new QueryClient()}
        multiSearchParams={mockedDefaultParameters}
        onNoHotelsWarning={jest.fn()}
        variant={'bb'}
      />
    );

    await waitFor(() => {
      expect(getByTestId('DropdownComp-sort-by-menuButton')).toBeTruthy();
    });

    await act(async () => {
      fireEvent.click(getByTestId('DropdownComp-sort-by-menuButton'));
    });

    await waitFor(() => {
      expect(queryByTestId('DropdownComp-sort-by-1')).toBeTruthy();
    });

    await act(async () => {
      fireEvent.click(getByTestId('DropdownComp-sort-by-1'));
    });

    await waitFor(() => {
      expect(mockRouter.push).toHaveBeenLastCalledWith(
        expect.stringMatching('SORT=2'),
        undefined,
        expect.anything()
      );
    });
  });

  it('should change route after filter was changed', async () => {
    const mockRouter = {
      query: { language: 'en', country: 'gb', VIEW: '2', FILTERS: '' },
      push: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    mockedPartialTranslations.isError = false;
    mockedGetStaticContent.isError = false;
    mockedHotelAvailabilities.isError = false;
    mockedPartialTranslations.isLoading = false;
    mockedGetStaticContent.isLoading = false;

    const { getByTestId } = render(
      <SearchResultsBBVariantContainer
        queryClient={new QueryClient()}
        multiSearchParams={mockedDefaultParameters}
        onNoHotelsWarning={jest.fn()}
        variant={'bb'}
      />
    );

    await waitFor(() => {
      expect(getByTestId('SRP-controls-filter-by-button')).toBeTruthy();
    });

    await act(async () => {
      fireEvent.click(getByTestId('SRP-controls-filter-by-button'));
    });

    await waitFor(() => {
      expect(screen.queryByTestId('SRP-Filters-checkbox-CPF')).toBeTruthy();
    });

    await act(async () => {
      fireEvent.click(screen.getByTestId('SRP-Filters-checkbox-CPF'));
    });

    await waitFor(() => {
      expect(mockRouter.push).toHaveBeenLastCalledWith(
        expect.stringMatching('FILTERS=CPF'),
        undefined,
        expect.anything()
      );
    });
  });

  it('should change route on click on map button', async () => {
    const mockRouter = {
      query: { language: 'en', country: 'gb', VIEW: '2' },
      push: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    mockedPartialTranslations.isError = false;
    mockedGetStaticContent.isError = false;
    mockedHotelAvailabilities.isError = false;
    mockedPartialTranslations.isLoading = false;
    mockedGetStaticContent.isLoading = false;

    const { getByTestId } = render(
      <SearchResultsBBVariantContainer
        queryClient={new QueryClient()}
        multiSearchParams={mockedDefaultParameters}
        onNoHotelsWarning={jest.fn()}
        variant={'bb'}
      />
    );

    await waitFor(() => {
      expect(getByTestId('controls-map-or-list-button')).toBeTruthy();
    });

    await act(async () => {
      fireEvent.click(getByTestId('controls-map-or-list-button'));
    });

    await waitFor(() => {
      expect(mockRouter.push).toHaveBeenLastCalledWith(
        expect.stringMatching('VIEW=1'),
        undefined,
        expect.anything()
      );
    });
  });

  it('should change route on click on list button', async () => {
    const mockRouter = {
      query: { language: 'en', country: 'gb', VIEW: '1' },
      push: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    mockedPartialTranslations.isError = false;
    mockedGetStaticContent.isError = false;
    mockedHotelAvailabilities.isError = false;
    mockedPartialTranslations.isLoading = false;
    mockedGetStaticContent.isLoading = false;

    const { getByTestId } = render(
      <SearchResultsBBVariantContainer
        queryClient={new QueryClient()}
        multiSearchParams={mockedDefaultParameters}
        onNoHotelsWarning={jest.fn()}
        variant={'bb'}
      />
    );

    await waitFor(() => {
      expect(getByTestId('controls-map-or-list-button')).toBeTruthy();
    });

    await act(async () => {
      fireEvent.click(getByTestId('controls-map-or-list-button'));
    });

    await waitFor(() => {
      expect(mockRouter.push).toHaveBeenLastCalledWith(
        expect.stringMatching('VIEW=2'),
        undefined,
        expect.anything()
      );
    });
  });

  it('should call router with the following url', async () => {
    const mockRouter = {
      query: { language: 'en', country: 'gb', VIEW: '2' },
      push: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    mockedPartialTranslations.isError = false;
    mockedGetStaticContent.isError = false;
    mockedHotelAvailabilities.isError = false;
    mockedPartialTranslations.isLoading = false;
    mockedGetStaticContent.isLoading = false;

    let renderScreen;
    await act(async () => {
      renderScreen = render(
        <SearchResultsBBVariantContainer
          queryClient={new QueryClient()}
          multiSearchParams={mockedDefaultParameters}
          onNoHotelsWarning={jest.fn()}
          variant={'bb'}
        />
      );
    });

    const { getByTestId } = renderScreen;
    const sortDropdown = getByTestId('DropdownComp-sort-by-menuButton');
    sortDropdown.click();
    const sortOption = getByTestId('DropdownComp-sort-by-1');
    sortOption.click();

    expect(getByTestId('DropdownComp-sort-by-menuButton')).toBeInTheDocument();
    expect(sortOption).toBeInTheDocument();
    expect(mockRouter.push).toHaveBeenCalledWith(
      '/gb/en/business-booker/search.html?searchModel.searchTerm=London,%20UK&PLACEID=ChIJdd4hrwug2EcRmSrV3Vo6llI&ARRdd=1&ARRmm=2&ARRyyyy=2023&NIGHTS=1&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB&SORT=1&FILTERS=',
      undefined,
      { shallow: true }
    );
  });

  it('should call router with the following url when feature flag enabled', async () => {
    const mockRouter = {
      query: { language: 'en', country: 'gb', VIEW: '2' },
      push: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    (useFeatureToggle as jest.Mock).mockReturnValue({
      ...defaultFeatureToggles,
      release_bb_sort_order_dropdown: true,
    });

    mockedPartialTranslations.isError = false;
    mockedGetStaticContent.isError = false;
    mockedHotelAvailabilities.isError = false;
    mockedPartialTranslations.isLoading = false;
    mockedGetStaticContent.isLoading = false;

    let renderScreen;
    await act(async () => {
      renderScreen = render(
        <SearchResultsBBVariantContainer
          queryClient={new QueryClient()}
          multiSearchParams={mockedDefaultParameters}
          onNoHotelsWarning={jest.fn()}
          variant={'bb'}
        />
      );
    });

    const { getByTestId } = renderScreen;
    const sortDropdown = getByTestId('DropdownComp-sort-by-menuButton');
    sortDropdown.click();
    const sortOption = getByTestId('DropdownComp-sort-by-2');
    sortOption.click();

    expect(getByTestId('DropdownComp-sort-by-menuButton')).toBeInTheDocument();
    expect(sortOption).toBeInTheDocument();
    expect(mockRouter.push).toHaveBeenCalledWith(
      '/gb/en/business-booker/search.html?searchModel.searchTerm=London,%20UK&PLACEID=ChIJdd4hrwug2EcRmSrV3Vo6llI&ARRdd=1&ARRmm=2&ARRyyyy=2023&NIGHTS=1&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB&SORT=3&FILTERS=',
      undefined,
      { shallow: true }
    );
  });
});

describe('getSearchRedirectURL function', () => {
  it('returns the expected URL with the country and language', async () => {
    const mockRouter = {
      push: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    const router: any = mockRouter;

    const paramsToIgnore = [];
    const country = 'gb';
    const language = 'en';
    const expectedUrl =
      'gb/en/business-booker/search.html?searchModel.searchTerm=London,%20UK&PLACEID=ChIJdd4hrwug2EcRmSrV3Vo6llI&ARRdd=1&ARRmm=2&ARRyyyy=2023&NIGHTS=1&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB';

    await waitFor(() => {
      expect(getSearchRedirectURL(router, paramsToIgnore, country, language)).toBe(expectedUrl);
    });
  });
});

describe('NotificationWrapper', () => {
  it('should return null when promotionBannerData is not provided', () => {
    const { container } = render(<NotificationWrapper promotionBannerData={null} />);
    expect(container.firstChild).toBeNull();
  });

  it('should render mocked PromotionsNotification component when promotionBannerData exists', () => {
    render(
      <NotificationWrapper
        promotionBannerData={{
          showPromo: true,
          isWithinPromoWindow: true,
          promotionCode: 'ST10R',
          landingPage: '',
          promoBannerColour: '#511E62',
          promoBannerIcon: '/content/dam/global/icons/common/price-tag-orange-16.svg',
          promoBannerTitle: 'Summer Sale: 10% off',
          promoBannerSubtitle: 'Select one of our hotels to see your discount.',
          promoInvalidMessage: null,
          promoExpiredMessage: null,
          promoAmendMessage: 'Your booking includes a promotion.',
        }}
      />
    );
    expect(screen.getByTestId('promo')).toBeInTheDocument();
  });

  it('should render mobile controls when useMobileControlsDisplay is true', async () => {
    (useMobileControlsDisplay as jest.Mock).mockReturnValue(true);
    (useScreenSize as jest.Mock).mockReturnValue({ isLessThanMd: true });
    (mockUseRouter as jest.Mock).mockReturnValue({
      locale: 'en',
      query: { language: 'en', country: 'gb', VIEW: '2', reservationId: '123' },
      push: jest.fn(),
    });

    mockedPartialTranslations.isError = false;
    mockedGetStaticContent.isError = false;
    mockedHotelAvailabilities.isError = false;
    mockedPartialTranslations.isLoading = false;
    mockedGetStaticContent.isLoading = false;

    const { getByTestId } = render(
      <SearchResultsBBVariantContainer
        queryClient={new QueryClient()}
        multiSearchParams={mockedDefaultParameters}
        onNoHotelsWarning={jest.fn()}
        variant={'bb'}
      />
    );

    await waitFor(() => {
      expect(getByTestId('SRP-controls-filter-by-button')).toBeInTheDocument();
      expect(getByTestId('DropdownComp-sort-by-menuButton')).toBeInTheDocument();
      expect(getByTestId('controls-map-or-list-button')).toBeInTheDocument();
    });
  });

  it('should set a scrollable target only for PIB mobile results', async () => {
    (useScreenSize as jest.Mock).mockReturnValue({
      isLessThanLg: true,
      isLessThanMd: false,
      isLessThanSm: false,
    });
    mockedPartialTranslations.isError = false;
    mockedGetStaticContent.isError = false;
    mockedHotelAvailabilities.isError = false;
    mockedPartialTranslations.isLoading = false;
    mockedGetStaticContent.isLoading = false;

    render(
      <SearchResultsBBVariantContainer
        queryClient={new QueryClient()}
        multiSearchParams={mockedDefaultParameters}
        onNoHotelsWarning={jest.fn()}
        variant={'bb'}
        innBusiness={true}
      />
    );

    await waitFor(() => {
      expect(screen.getByText('searchresults.list.hotel.loading')).not.toHaveAttribute(
        'data-scrollable-target'
      );
    });
  });

  it('should not set a scrollable target only for PIB desktop results', async () => {
    (useScreenSize as jest.Mock).mockReturnValue({
      isLessThanLg: false,
      isLessThanMd: false,
      isLessThanSm: false,
    });
    mockedPartialTranslations.isError = false;
    mockedGetStaticContent.isError = false;
    mockedHotelAvailabilities.isError = false;
    mockedPartialTranslations.isLoading = false;
    mockedGetStaticContent.isLoading = false;

    render(
      <SearchResultsBBVariantContainer
        queryClient={new QueryClient()}
        multiSearchParams={mockedDefaultParameters}
        onNoHotelsWarning={jest.fn()}
        variant={'bb'}
        innBusiness={true}
      />
    );

    await waitFor(() => {
      expect(screen.getByText('searchresults.list.hotel.loading')).not.toHaveAttribute(
        'data-scrollable-target'
      );
    });
  });

  it('should not set a scrollable target for non-PIB results', async () => {
    (useScreenSize as jest.Mock).mockReturnValue({ isLessThanMd: true, isLessThanSm: false });
    mockedPartialTranslations.isError = false;
    mockedGetStaticContent.isError = false;
    mockedHotelAvailabilities.isError = false;
    mockedPartialTranslations.isLoading = false;
    mockedGetStaticContent.isLoading = false;

    render(
      <SearchResultsBBVariantContainer
        queryClient={new QueryClient()}
        multiSearchParams={mockedDefaultParameters}
        onNoHotelsWarning={jest.fn()}
        variant={'bb'}
      />
    );

    await waitFor(() => {
      expect(screen.getByText('searchresults.list.hotel.loading')).not.toHaveAttribute(
        'data-scrollable-target'
      );
    });
  });

  describe('Cookie modifier parsing (new logic)', () => {
    const mockGetCookie = getCookie as jest.Mock;

    beforeEach(() => {
      jest.clearAllMocks();
      (useFeatureToggle as jest.Mock).mockReturnValue({ ...defaultFeatureToggles });
    });

    it('should correctly parse rcPriceModifier when cookie is "0"', () => {
      mockGetCookie.mockImplementation((key: string) => {
        if (key === 'RC_PRICE_MODIFIER') return '0';
        if (key === 'RC_DISTANCE_MODIFIER') return '1';
        if (key === 'RC_HUB_MODIFIER') return '2';
        return null;
      });

      const priceCookie = mockGetCookie('RC_PRICE_MODIFIER');
      const rcPriceModifier =
        priceCookie !== undefined && priceCookie !== null ? parseFloat(priceCookie) : 1;

      expect(rcPriceModifier).toBe(0);
    });

    it('should fallback to 1 when cookie is null', () => {
      mockGetCookie.mockReturnValue(null);

      const priceCookie = mockGetCookie('RC_PRICE_MODIFIER');
      const rcPriceModifier =
        priceCookie !== undefined && priceCookie !== null ? parseFloat(priceCookie) : 1;

      expect(rcPriceModifier).toBe(1);
    });

    it('should fallback to 1 when cookie is undefined', () => {
      mockGetCookie.mockReturnValue(undefined);

      const distanceCookie = mockGetCookie('RC_DISTANCE_MODIFIER');
      const rcDistanceModifier =
        distanceCookie !== undefined && distanceCookie !== null ? parseFloat(distanceCookie) : 1;

      expect(rcDistanceModifier).toBe(1);
    });
  });

  it('should fetch more hotels when scrolling loads more results', async () => {
    render(
      <SearchResultsBBVariantContainer
        queryClient={new QueryClient()}
        multiSearchParams={mockedDefaultParameters}
        onNoHotelsWarning={jest.fn()}
        variant={'bb'}
      />
    );

    await waitFor(() => {
      expect(mockGetNewSearchResultsBB).toHaveBeenCalledTimes(1);
    });

    fireEvent.click(screen.getByTestId('load-more'));

    await waitFor(() => {
      expect(mockGetNewSearchResultsBB).toHaveBeenCalledTimes(2);
    });
  });
});
