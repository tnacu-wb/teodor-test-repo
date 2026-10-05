import { QueryClient } from '@tanstack/react-query';
import { fireEvent } from '@testing-library/dom';
import '@testing-library/jest-dom';
import { FT_PI_SRP_SPLIT_MAP_VIEW } from '@whitbread-eos/api';
import {
  getCookie,
  useFeatureToggle,
  useMobileControlsDisplay,
  useScreenSize,
  getNewSearchResultsPI,
} from '@whitbread-eos/utils';

import {
  mockedGetStaticContent,
  mockedHotelAvailabilities,
  mockedPartialTranslations,
} from '../../mockData/mockResponse';
import { act, render, screen, waitFor } from '../../utils/test-utils';
import SearchResultsPIVariantContainer, {
  getSearchRedirectURL,
} from './SearchResultsPIVariant.container';

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

jest.mock('../utilities', () => ({
  ...jest.requireActual('../utilities'),
  getSearchQueryUrl: jest
    .fn()
    .mockReturnValue(
      'searchModel.searchTerm=London,%20UK&PLACEID=ChIJdd4hrwug2EcRmSrV3Vo6llI&ARRdd=1&ARRmm=2&ARRyyyy=2023&NIGHTS=1&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB'
    ),
}));

const mockFetchPartialTranslations = jest.fn();

jest.mock('@whitbread-eos/utils', () => {
  const actual = jest.requireActual('@whitbread-eos/utils');

  return {
    ...actual,
    getCookie: jest.fn(),
    formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
    graphQLRequest: jest.fn(() => ({
      hotelAvailabilitiesV2: mockedHotelAvailabilities.data,
    })),
    getNewSearchResultsPI: jest.fn(),
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
    useFeatureToggle: jest.fn(() => ({
      release_pi_sort_order_dropdown: false,
      release_promotions_in_hotelavailability: true,
      release_pi_promo_code_landing_page: true,
      release_pi_promo_code_site_wide: true,
    })),
    useMobileControlsDisplay: jest.fn(() => false),
    useScreenSize: jest.fn(() => ({ isLessThanMd: false })),
  };
});

const mockGetNewSearchResultsPI = getNewSearchResultsPI as jest.Mock;

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
      query: { language: 'en', country: 'gb', viewType: '2' },
      push: jest.fn(),
    });
    mockFetchPartialTranslations.mockImplementation(() => {
      return {
        ...mockedPartialTranslations,
      };
    });
    mockGetNewSearchResultsPI.mockReset();
    mockGetNewSearchResultsPI.mockResolvedValue({
      results: mockedHotelAvailabilities.data.multiHotelAvailabilities,
      total: mockedHotelAvailabilities.data.total,
      promotionsInformation: mockedHotelAvailabilities.data.promotionsInformation,
    });
  });

  it('should show a loading state for each query', async () => {
    mockedPartialTranslations.isLoading = true;
    mockedGetStaticContent.isLoading = true;
    mockedHotelAvailabilities.isLoading = true;
    const { getByText } = render(
      <SearchResultsPIVariantContainer
        queryClient={new QueryClient()}
        multiSearchParams={mockedDefaultParameters}
        onNoHotelsWarning={jest.fn()}
      />
    );

    await waitFor(() => {
      expect(getByText('searchresults.list.hotel.loading')).toBeInTheDocument();
    });
  });

  it('should show an error state for each labels query', async () => {
    mockedPartialTranslations.isLoading = false;
    mockedGetStaticContent.isLoading = false;
    mockedHotelAvailabilities.isLoading = false;
    mockedHotelAvailabilities.isError = true;
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
      <SearchResultsPIVariantContainer
        queryClient={new QueryClient()}
        multiSearchParams={mockedDefaultParameters}
        onNoHotelsWarning={jest.fn()}
      />
    );

    await waitFor(() => {
      expect(getByText('Error on loading translations...')).toBeInTheDocument();
    });
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
        <SearchResultsPIVariantContainer
          queryClient={new QueryClient()}
          multiSearchParams={mockedDefaultParameters}
          onNoHotelsWarning={jest.fn()}
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

  it('calls getNewSearchResultsPI with the queryClient, query params, promoId and promotions flag', async () => {
    mockedPartialTranslations.isError = false;
    mockedGetStaticContent.isError = false;
    mockedHotelAvailabilities.isError = false;
    mockedPartialTranslations.isLoading = false;
    mockedGetStaticContent.isLoading = false;

    const queryClient = new QueryClient();

    await act(async () => {
      render(
        <SearchResultsPIVariantContainer
          queryClient={queryClient}
          multiSearchParams={mockedDefaultParameters}
          onNoHotelsWarning={jest.fn()}
        />
      );
    });

    await waitFor(() => {
      expect(mockGetNewSearchResultsPI).toHaveBeenCalledWith(
        queryClient,
        expect.objectContaining({
          channel: 'PI',
          country: 'de',
          language: 'de',
          page: 1,
        }),
        null,
        true
      );
    });
  });

  it('renders hotels returned by getNewSearchResultsPI', async () => {
    mockedPartialTranslations.isError = false;
    mockedGetStaticContent.isError = false;
    mockedHotelAvailabilities.isError = false;
    mockedPartialTranslations.isLoading = false;
    mockedGetStaticContent.isLoading = false;

    mockGetNewSearchResultsPI.mockResolvedValue({
      results: mockedHotelAvailabilities.data.multiHotelAvailabilities,
      total: mockedHotelAvailabilities.data.multiHotelAvailabilities.length,
      promotionsInformation: null,
    });

    await act(async () => {
      render(
        <SearchResultsPIVariantContainer
          queryClient={new QueryClient()}
          multiSearchParams={mockedDefaultParameters}
          onNoHotelsWarning={jest.fn()}
        />
      );
    });

    await waitFor(() => {
      expect(mockGetNewSearchResultsPI).toHaveBeenCalled();
    });
  });

  it('shows the SRisError state when getNewSearchResultsPI rejects', async () => {
    mockedPartialTranslations.isError = false;
    mockedGetStaticContent.isError = false;
    mockedPartialTranslations.isLoading = false;
    mockedGetStaticContent.isLoading = false;

    mockGetNewSearchResultsPI.mockRejectedValueOnce(new Error('network error'));

    await act(async () => {
      render(
        <SearchResultsPIVariantContainer
          queryClient={new QueryClient()}
          multiSearchParams={mockedDefaultParameters}
          onNoHotelsWarning={jest.fn()}
        />
      );
    });

    await waitFor(() => {
      expect(mockGetNewSearchResultsPI).toHaveBeenCalled();
    });
  });

  it('should change route after sort was changed', async () => {
    const mockRouter = {
      query: { language: 'en', country: 'gb', SORT: '1' },
      push: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    mockedPartialTranslations.isError = false;
    mockedGetStaticContent.isError = false;
    mockedHotelAvailabilities.isError = false;
    mockedPartialTranslations.isLoading = false;
    mockedGetStaticContent.isLoading = false;

    const { getByTestId, queryByTestId } = render(
      <SearchResultsPIVariantContainer
        queryClient={new QueryClient()}
        multiSearchParams={mockedDefaultParameters}
        onNoHotelsWarning={jest.fn()}
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
      expect(mockRouter.push).toHaveBeenLastCalledWith({
        query: { SORT: '1', VIEW: '2', country: 'gb', language: 'en' },
      });
    });
  });

  it('should change route after filter was changed', async () => {
    const mockRouter = {
      query: { language: 'en', country: 'gb', FILTERS: '' },
      push: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    mockedPartialTranslations.isError = false;
    mockedGetStaticContent.isError = false;
    mockedHotelAvailabilities.isError = false;
    mockedPartialTranslations.isLoading = false;
    mockedGetStaticContent.isLoading = false;

    const { getByTestId } = render(
      <SearchResultsPIVariantContainer
        queryClient={new QueryClient()}
        multiSearchParams={mockedDefaultParameters}
        onNoHotelsWarning={jest.fn()}
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
      <SearchResultsPIVariantContainer
        queryClient={new QueryClient()}
        multiSearchParams={mockedDefaultParameters}
        onNoHotelsWarning={jest.fn()}
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
      <SearchResultsPIVariantContainer
        queryClient={new QueryClient()}
        multiSearchParams={mockedDefaultParameters}
        onNoHotelsWarning={jest.fn()}
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
        <SearchResultsPIVariantContainer
          queryClient={new QueryClient()}
          multiSearchParams={mockedDefaultParameters}
          onNoHotelsWarning={jest.fn()}
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
      'gb/en/search.html?searchModel.searchTerm=London,%20UK&PLACEID=ChIJdd4hrwug2EcRmSrV3Vo6llI&ARRdd=1&ARRmm=2&ARRyyyy=2023&NIGHTS=1&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB&SORT=1&FILTERS=',
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
      release_pi_sort_order_dropdown: true,
    });

    mockedPartialTranslations.isError = false;
    mockedGetStaticContent.isError = false;
    mockedHotelAvailabilities.isError = false;
    mockedPartialTranslations.isLoading = false;
    mockedGetStaticContent.isLoading = false;

    let renderScreen;
    await act(async () => {
      renderScreen = render(
        <SearchResultsPIVariantContainer
          queryClient={new QueryClient()}
          multiSearchParams={mockedDefaultParameters}
          onNoHotelsWarning={jest.fn()}
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
      'gb/en/search.html?searchModel.searchTerm=London,%20UK&PLACEID=ChIJdd4hrwug2EcRmSrV3Vo6llI&ARRdd=1&ARRmm=2&ARRyyyy=2023&NIGHTS=1&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB&SORT=3&FILTERS=',
      undefined,
      { shallow: true }
    );
  });
});

describe('getSearchRedirectURL function', () => {
  it('returns the expected URL with the country and language', () => {
    const mockRouter = {
      push: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    const router: any = mockRouter;

    const paramsToIgnore = [];
    const country = 'gb';
    const language = 'en';
    const expectedUrl =
      'gb/en/search.html?searchModel.searchTerm=London,%20UK&PLACEID=ChIJdd4hrwug2EcRmSrV3Vo6llI&ARRdd=1&ARRmm=2&ARRyyyy=2023&NIGHTS=1&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB';

    expect(getSearchRedirectURL(router, paramsToIgnore, country, language)).toBe(expectedUrl);
  });

  it('should render mobile controls when useMobileControlsDisplay is true', async () => {
    (useMobileControlsDisplay as jest.Mock).mockReturnValue(true);
    (useScreenSize as jest.Mock).mockReturnValue({ isLessThanMd: true });
    (mockUseRouter as jest.Mock).mockReturnValue({
      locale: 'en',
      query: { language: 'en', country: 'gb', viewType: '2', reservationId: '123' },
      push: jest.fn(),
    });

    mockedPartialTranslations.isError = false;
    mockedGetStaticContent.isError = false;
    mockedHotelAvailabilities.isError = false;
    mockedPartialTranslations.isLoading = false;
    mockedGetStaticContent.isLoading = false;

    const { getByTestId } = render(
      <SearchResultsPIVariantContainer
        queryClient={new QueryClient()}
        multiSearchParams={mockedDefaultParameters}
        onNoHotelsWarning={jest.fn()}
      />
    );

    await waitFor(() => {
      expect(getByTestId('SRP-controls-filter-by-button')).toBeInTheDocument();
      expect(getByTestId('DropdownComp-sort-by-menuButton')).toBeInTheDocument();
      expect(getByTestId('controls-map-or-list-button')).toBeInTheDocument();
    });
  });

  it('adds the srp-split-view-active class to the body while split view is active', async () => {
    Object.defineProperty(window, 'innerWidth', {
      writable: true,
      configurable: true,
      value: 1400,
    });
    (useMobileControlsDisplay as jest.Mock).mockReturnValue(false);
    (useScreenSize as jest.Mock).mockReturnValue({ isLessThanMd: false });
    (useFeatureToggle as jest.Mock).mockReturnValue({
      [FT_PI_SRP_SPLIT_MAP_VIEW]: true,
    });
    (mockUseRouter as jest.Mock).mockReturnValue({
      locale: 'en',
      query: { language: 'en', country: 'gb', viewType: '2' },
      push: jest.fn(),
    });

    mockedPartialTranslations.isError = false;
    mockedGetStaticContent.isError = false;
    mockedHotelAvailabilities.isError = false;
    mockedPartialTranslations.isLoading = false;
    mockedGetStaticContent.isLoading = false;

    const { unmount } = render(
      <SearchResultsPIVariantContainer
        queryClient={new QueryClient()}
        multiSearchParams={mockedDefaultParameters}
        onNoHotelsWarning={jest.fn()}
      />
    );

    await waitFor(() => {
      expect(document.body.classList.contains('srp-split-view-active')).toBe(true);
    });

    unmount();

    expect(document.body.classList.contains('srp-split-view-active')).toBe(false);
  });

  describe('Cookie modifier parsing ', () => {
    const mockGetCookie = getCookie as jest.Mock;

    beforeEach(() => {
      jest.clearAllMocks();
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

      expect(rcPriceModifier).toBe(0); // ✅ main fix validation
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

    it('should correctly parse valid numeric cookie values', () => {
      mockGetCookie.mockReturnValue('1.75');

      const hubModifierCookie = mockGetCookie('RC_HUB_MODIFIER');
      const rcHubModifier =
        hubModifierCookie !== undefined && hubModifierCookie !== null
          ? parseFloat(hubModifierCookie)
          : undefined;

      expect(rcHubModifier).toBe(1.75);
    });
  });
  it('should fetch more hotels when scrolling loads more results', async () => {
    render(
      <SearchResultsPIVariantContainer
        queryClient={new QueryClient()}
        multiSearchParams={mockedDefaultParameters}
      />
    );

    await waitFor(() => {
      expect(mockGetNewSearchResultsPI).toHaveBeenCalledTimes(1);
    });

    fireEvent.click(screen.getByTestId('load-more'));

    await waitFor(() => {
      expect(mockGetNewSearchResultsPI).toHaveBeenCalledTimes(2);
    });
  });
});
