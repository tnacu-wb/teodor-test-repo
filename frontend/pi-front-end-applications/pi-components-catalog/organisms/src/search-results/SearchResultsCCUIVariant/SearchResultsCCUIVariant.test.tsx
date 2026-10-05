import { QueryClient } from '@tanstack/react-query';
import { fireEvent } from '@testing-library/dom';
import '@testing-library/jest-dom';
import { SingleHotelAvailability } from '@whitbread-eos/api';
import { getNewSearchResultsCCUI } from '@whitbread-eos/utils';

import {
  mockedGetStaticContent,
  mockedHotelAvailabilities,
  mockedPartialTranslations,
} from '../../mockData/mockResponse';
import { act, render, waitFor, screen } from '../../utils/test-utils';
import { getOpeningSoonHotels } from '../utilities';
import SearchResultsCCUIVariantContainer, {
  getHotelsWithAvailableRooms,
  getMlosHotels,
  getSoldOutHotels,
} from './SearchResultsCCUIVariant.container';

jest.mock('../utilities', () => ({
  ...jest.requireActual('../utilities'),
  getSearchQueryUrl: jest
    .fn()
    .mockReturnValue(
      'searchModel.searchTerm=London,%20UK&PLACEID=ChIJdd4hrwug2EcRmSrV3Vo6llI&ARRdd=1&ARRmm=2&ARRyyyy=2023&NIGHTS=1&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB&CORPID=1401'
    ),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  getNewSearchResultsCCUI: jest.fn(),
  graphQLRequest: jest.fn(() => ({
    hotelAvailabilities: mockedHotelAvailabilities.data,
  })),

  useQueryRequest: jest.fn().mockImplementation((queryKey: string | any[]) => {
    let queryKeyValue = queryKey;
    if (Array.isArray(queryKey)) {
      queryKeyValue = queryKey[0];
    }
    switch (queryKeyValue) {
      case 'searchInformation':
        return {
          ...mockedPartialTranslations,
        };
      case 'GetStaticContent':
        return {
          ...mockedGetStaticContent,
        };
      default:
        return {};
    }
  }),
  useFeatureToggle: jest.fn(() => ({
    release_srp_dynamic_filters: true,
    release_promotions_in_hotelavailability: true,
    release_pi_promo_code_landing_page: true,
    release_pi_promo_code_site_wide: true,
  })),
}));

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('date-fns', () => ({
  ...jest.requireActual('date-fns'),
  format: () => '5 September 2025',
}));

const mockedDefaultParameters = {
  arrivalDay: 25,
  arrivalMonth: 11,
  arrivalYear: 2023,
  location: 'London',
  numberOfNights: 4,
  rooms: [{ adultsNumber: 2, childrenNumber: 0, type: 'Double' }],
  bookingChannel: 'WEB',
  placeId: 'ChIJ2_UmUkxNekgRqmv-BDgUvtk',
  sort: 'DISTANCE',
  view: '2',
  code: '',
};

const unorderedHotels = [
  {
    hotelId: '1',
    hotelAvailability: {
      available: false,
    },
    hotelInformation: {
      hotelOpeningDate: '',
    },
  },
  {
    hotelId: '2',
    hotelAvailability: {
      available: true,
    },
    hotelInformation: {
      hotelOpeningDate: '',
    },
  },
  {
    hotelId: '3',
    hotelAvailability: {
      available: true,
    },
    hotelInformation: {
      hotelOpeningDate: '2024-09-05T00:00:00.000Z',
    },
  },
  {
    hotelId: '4',
    hotelAvailability: {
      available: true,
      hasMlosRestriction: true,
    },
    hotelInformation: {
      hotelOpeningDate: '',
    },
  },
];

const mockGetNewSearchResultsCCUI = getNewSearchResultsCCUI as jest.Mock;

describe('SearchResults container', () => {
  window.scrollTo = jest.fn();

  beforeAll(() => {
    window.localStorage.setItem(
      'StayDetailsState',
      '{"timestamp":1675236414754,"data":{"arrival":"2023-07-07T07:26:47.474Z","info":{"suggestion":{"location":{"longitude":null,"latitude":null},"name":"London, UK","text":"London, UK"},"text":"London, UK"},"nights":1,"rooms":[{"adults":1,"children":0,"cot":false,"number":1,"type":{"code":"DB","name":"double"}}],"bookingChannel":"WEB"}}'
    );
  });

  beforeEach(() => {
    mockUseRouter.mockReturnValue({
      locale: 'en',
      query: { language: 'en', country: 'gb', VIEW: '2' },
      push: jest.fn(),
      localStorage: {
        getItem: jest.fn(),
      },
    });
    mockGetNewSearchResultsCCUI.mockReset();
    mockGetNewSearchResultsCCUI.mockResolvedValue({
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
      <SearchResultsCCUIVariantContainer
        queryClient={new QueryClient()}
        multiSearchParams={mockedDefaultParameters}
        onNoHotelsWarning={jest.fn()}
        isSearchError={true}
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
    mockedPartialTranslations.error = { message: 'error' } as any;
    mockedGetStaticContent.error = { message: 'error' } as any;

    const { getByText } = render(
      <SearchResultsCCUIVariantContainer
        queryClient={new QueryClient()}
        multiSearchParams={mockedDefaultParameters}
        onNoHotelsWarning={jest.fn()}
        isSearchError={true}
      />
    );

    await waitFor(() => {
      expect(getByText('Error on loading translations...')).toBeInTheDocument();
    });

    mockedPartialTranslations.isError = false;
    mockedGetStaticContent.isError = false;
  });

  it('should render the component by displaying the Map View button', async () => {
    mockedPartialTranslations.isError = false;
    mockedGetStaticContent.isError = false;
    mockedHotelAvailabilities.isError = false;
    mockedPartialTranslations.isLoading = false;
    mockedGetStaticContent.isLoading = false;

    let renderScreen;
    await act(async () => {
      renderScreen = render(
        <SearchResultsCCUIVariantContainer
          queryClient={new QueryClient()}
          multiSearchParams={mockedDefaultParameters}
          onNoHotelsWarning={jest.fn()}
          isSearchError={true}
        />
      );
    });

    const { getByTestId } = renderScreen;

    await waitFor(() => {
      expect(getByTestId('controls-map-or-list-button')).toBeInTheDocument();
      fireEvent.scroll(window, { target: { scrollY: 1000 } });
    });
  });

  it('should change route on click on map button', async () => {
    const mockRouter = {
      push: jest.fn(),
      query: { VIEW: '2', FILTERS: '' },
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    mockedPartialTranslations.isError = false;
    mockedGetStaticContent.isError = false;
    mockedHotelAvailabilities.isError = false;
    mockedPartialTranslations.isLoading = false;
    mockedGetStaticContent.isLoading = false;

    const { getByTestId } = render(
      <SearchResultsCCUIVariantContainer
        queryClient={new QueryClient()}
        multiSearchParams={mockedDefaultParameters}
        onNoHotelsWarning={jest.fn()}
        isSearchError={true}
      />
    );

    await waitFor(() => {
      expect(getByTestId('controls-map-or-list-button')).toBeTruthy();
    });

    await act(async () => {
      fireEvent.click(getByTestId('controls-map-or-list-button'));
    });

    await waitFor(() => {
      expect(mockRouter.push).toHaveBeenLastCalledWith({
        query: { ...mockRouter.query, VIEW: '1' },
      });
    });
  });

  it('should change route on click on list button', async () => {
    const mockRouter = {
      push: jest.fn(),
      query: { VIEW: '1', FILTERS: '' },
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    mockedPartialTranslations.isError = false;
    mockedGetStaticContent.isError = false;
    mockedHotelAvailabilities.isError = false;
    mockedPartialTranslations.isLoading = false;
    mockedGetStaticContent.isLoading = false;

    const { getByTestId } = render(
      <SearchResultsCCUIVariantContainer
        queryClient={new QueryClient()}
        multiSearchParams={mockedDefaultParameters}
        onNoHotelsWarning={jest.fn()}
        isSearchError={true}
      />
    );

    await waitFor(() => {
      expect(getByTestId('controls-map-or-list-button')).toBeTruthy();
    });

    await act(async () => {
      fireEvent.click(getByTestId('controls-map-or-list-button'));
    });

    await waitFor(() => {
      expect(mockRouter.push).toHaveBeenLastCalledWith({
        query: { ...mockRouter.query, VIEW: '2' },
      });
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
      <SearchResultsCCUIVariantContainer
        queryClient={new QueryClient()}
        multiSearchParams={mockedDefaultParameters}
        onNoHotelsWarning={jest.fn()}
        isSearchError={true}
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
        expect.stringMatching(/SORT=2/),
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

    render(
      <SearchResultsCCUIVariantContainer
        queryClient={new QueryClient()}
        multiSearchParams={mockedDefaultParameters}
        onNoHotelsWarning={jest.fn()}
        isSearchError={true}
      />
    );

    fireEvent.click(screen.getByTestId('DLP-Filters-Open-Button'));

    await waitFor(() => {
      expect(screen.getByTestId('DLP-Filters-Filters-checkbox-CPF')).toBeInTheDocument();
    });

    fireEvent.click(screen.getByTestId('DLP-Filters-Filters-checkbox-CPF'));

    await waitFor(() => {
      expect(mockRouter.push).toHaveBeenLastCalledWith(
        expect.stringMatching('FILTERS=CPF'),
        undefined,
        expect.anything()
      );
    });
  });

  it('should display no hotels warning when no hotels found after filtering and resultsMeta.total is 0', async () => {
    mockedHotelAvailabilities.data.total = 0;
    mockGetNewSearchResultsCCUI.mockResolvedValue({
      results: mockedHotelAvailabilities.data.multiHotelAvailabilities,
      total: mockedHotelAvailabilities.data.total,
      promotionsInformation: mockedHotelAvailabilities.data.promotionsInformation,
    });

    const mockRouter = {
      query: { language: 'en', country: 'gb', VIEW: '1', FILTERS: 'CPF' },
      push: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    mockedPartialTranslations.isError = false;
    mockedGetStaticContent.isError = false;
    mockedHotelAvailabilities.isError = false;
    mockedPartialTranslations.isLoading = false;
    mockedGetStaticContent.isLoading = false;

    render(
      <SearchResultsCCUIVariantContainer
        queryClient={new QueryClient()}
        multiSearchParams={mockedDefaultParameters}
        onNoHotelsWarning={jest.fn()}
        isSearchError={true}
      />
    );

    fireEvent.click(screen.getByTestId('DLP-Filters-Open-Button'));

    await waitFor(() => {
      expect(screen.getByTestId('DLP-Filters-Filters-checkbox-CPF')).toBeInTheDocument();
    });

    fireEvent.click(screen.getByTestId('DLP-Filters-Filters-checkbox-CPF'));

    await waitFor(() => {
      expect(screen.getByTestId('AlertDescription')).toHaveTextContent(
        "We couldn't find any hotels that matched your criteria."
      );
    });
  });
});

describe('getHotelsWithAvailableRooms', () => {
  it('should return two objects in array', async () => {
    expect(getHotelsWithAvailableRooms(unorderedHotels as SingleHotelAvailability[]).length).toBe(
      3
    );
  });
});
describe('getSoldOutHotels', () => {
  it('should return one objects in array', async () => {
    expect(
      getSoldOutHotels(unorderedHotels as SingleHotelAvailability[], mockedDefaultParameters, [])
        .length
    ).toBe(1);
  });
  it('should return one objects in array, ignoring mlos', async () => {
    expect(
      getSoldOutHotels(
        unorderedHotels as SingleHotelAvailability[],
        mockedDefaultParameters,
        unorderedHotels.filter(
          (hotel) => hotel.hotelAvailability.hasMlosRestriction
        ) as SingleHotelAvailability[]
      ).length
    ).toBe(1);
  });
});
describe('getOpeningSoonHotels', () => {
  it('should return one objects in array', async () => {
    expect(
      getOpeningSoonHotels(unorderedHotels as SingleHotelAvailability[], mockedDefaultParameters)
        .length
    ).toBe(1);
  });
});
describe('getMlosHotels', () => {
  it('should return one objects in array', async () => {
    expect(getMlosHotels(unorderedHotels as SingleHotelAvailability[]).length).toBe(1);
  });
});
