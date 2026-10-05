import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { act, fireEvent, screen, waitFor } from '@testing-library/react';
import { SearchSuggestions } from '@whitbread-eos/api';
import { useFeatureToggle } from '@whitbread-eos/utils';
import React from 'react';

import {
  mockedGetStaticContent,
  mockResponseSuggestions,
  mockRequestGetSearchRules,
  mockMapSearchParamsData,
  mockSearchErrorFieldsData,
  mockSearchErrorKeysData,
  mockSearchContainerData,
  mockSearchCompanyByIdOrCorpId,
  mockCompanyData,
} from '../../mockData/mockResponse';
import { render } from '../../utils/test-utils';
import SearchContainer from './Search.container';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  swapKeysAndValues: jest.fn().mockReturnValue({
    DIS: 'accessible',
    DB: 'double',
    FAM: 'family',
    SB: 'single',
    TWIN: 'twin',
  }),
  useFeatureToggle: jest.fn(() => ({
    release_ccui_search_negotiated_rates_by_corpId: false,
    release_pi_discount_rate: true,
  })),
}));

jest.mock('../validations/searchValidation', () => ({
  ...jest.requireActual('../validations/searchValidation'),
  validateSearchData: jest.fn().mockReturnValue({
    errorKey: [undefined, undefined],
    URL: '/en/search.html?searchModel.searchTerm=London%20Eye,%20London,%20UK&PLACEID=ChIJc2nSALkEdkgRkuoJJBfzkUI&ARRdd=1&ARRmm=11&ARRyyyy=2022&NIGHTS=1&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB&BOOKINGCHANNEL=WEB&SORT=1',
  }),
}));

jest.mock('../utilities/searchContainerHelpers', () => ({
  ...jest.requireActual('../utilities/searchContainerHelpers'),
  setSearchLocationInLocalStorage: jest.fn().mockReturnValue({
    suggestion: {
      location: { longitude: -0.447979, latitude: 51.496015 },
      placeId: '',
      hotelId: 'HEAPTI',
    },
  }),
  mapSearchParamsForURL: jest.fn().mockReturnValue(mockMapSearchParamsData),
  ERROR_KEYS: mockSearchErrorKeysData,
  ERROR_FIELDS: mockSearchErrorFieldsData,
  getNotificationMarginTop: jest.fn().mockReturnValue('md'),
  formatSummaryDateRange: jest.fn().mockReturnValue('29 Oct - 30 Oct'),
}));

jest.mock('@whitbread-eos/molecules', () => ({
  ...jest.requireActual('@whitbread-eos/molecules'),
  CompanySearch: () => <div>Test company</div>,
}));

let searchProps: any = {};
const mockSearchProps: any = {};

jest.mock('@whitbread-eos/molecules/dist/search/Search/Search.component', () => {
  const MockSearch = (props: any) => {
    searchProps = props;

    return (
      <div data-testid="search-component">
        <input
          data-testid="location-input"
          onChange={(e) => props.onLocationInputChange?.(e.target.value)}
        />
        <button data-testid="clear-location-input-button" onClick={props.onLocationInputClear}>
          Clear
        </button>

        <button
          data-testid="search-summary-dates"
          onClick={() => props.onSelectDates?.([new Date(), new Date()])}
        >
          Pick Dates
        </button>

        <button data-testid="search-button" onClick={() => props.handleButtonClick({})}>
          Search
        </button>

        {/* Room occupancy simulation */}
        <button data-testid="room-picker-change" onClick={() => props.onOccupancyChange?.()}>
          Change Rooms
        </button>
      </div>
    );
  };

  MockSearch.displayName = 'MockSearch';
  return MockSearch;
});

const SearchButton = ({ onClick }: any) => (
  <button data-testid="search-button" onClick={onClick}>
    {'Search'}
  </button>
);

const LocationInput = ({ onLocationInputClear, onLocationInputChange }: any) => {
  const handleInputChange = (event: any) => {
    event.preventDefault();
    const inputtedValue = event.currentTarget.value;
    onLocationInputChange(inputtedValue);
  };
  return (
    <>
      <input data-testid="location-input" onChange={handleInputChange} />
      <button data-testid="clear-location-input-button" onClick={onLocationInputClear} />
    </>
  );
};

jest.mock('@whitbread-eos/molecules/dist/search/Search/Search.component', () => {
  const SearchComponent = (props: any) => {
    searchProps = props;
    const input = {
      searchTerm: 'London, UK',
      ARRdd: 9,
      ARRmm: 10,
      ARRyyyy: 2025,
      nights: 1,
      roomsNumber: 1,
      rooms: [
        {
          adults: 1,
          children: 0,
          shouldIncludeCot: false,
          roomType: 'Double',
          id: 'fZF1QPBttu_8Ia56YdW9D',
        },
      ],
      placeId: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
    };

    return (
      <div data-testid="search-component">
        <LocationInput
          onLocationInputClear={props.onLocationInputClear}
          onLocationInputChange={props.onLocationInputChange}
        />
        <SearchButton onClick={() => props.handleButtonClick(input)} />
        {props.companyNameComponent}
      </div>
    );
  };
  return SearchComponent;
});

global.fetch = jest.fn(() =>
  Promise.resolve({
    json: () => Promise.resolve(mockResponseSuggestions),
    ok: true,
  })
) as jest.Mock;

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('@tanstack/react-query', () => {
  const original: typeof ReactQuery = jest.requireActual('@tanstack/react-query');
  return {
    ...original,
    useQuery: jest
      .fn()
      .mockImplementation((args: { queryKey: string | string[]; queryFn: () => void }) => {
        const queryKey = args.queryKey;
        let queryKeyValue = queryKey;
        if (Array.isArray(queryKey)) {
          queryKeyValue = queryKey[0];
        }
        switch (queryKeyValue) {
          case 'getSearchRules':
            return {
              ...mockRequestGetSearchRules,
            };
          case 'GetStaticContent':
            return {
              ...mockedGetStaticContent,
            };
          case 'searchCompany':
            return {
              ...mockSearchCompanyByIdOrCorpId,
            };
          case 'seachCompanyById':
            return {
              ...mockCompanyData,
            };
          default:
            return {};
        }
      }),
  };
});

const initialData: SearchSuggestions = {
  managedPlaces: [],
  places: [],
  properties: [],
};

const clearRequestMockups = (): void => {
  mockRequestGetSearchRules.isError = false;
  mockedGetStaticContent.isError = false;
  mockRequestGetSearchRules.isLoading = false;
  mockedGetStaticContent.isLoading = false;
  mockRequestGetSearchRules.error = { message: '' };
  mockedGetStaticContent.error = { message: '' };
};

describe('Search Container component tests', () => {
  beforeAll(() => {
    mockUseRouter.mockReturnValue({
      push: jest.fn(),
      pathname: '',
      asPath: 'en',
      replace: jest.fn(),
      query: {
        'searchModel.searchTerm': 'London Eye, London, UK',
        PLACEID: 'ChIJc2nSALkEdkgRkuoJJBfzkUI',
        ARRdd: '1',
        ARRmm: '11',
        ARRyyyy: '2022',
        NIGHTS: '1',
        ROOMS: '1',
        ADULT1: '1',
        CHILD1: '0',
        COT1: '0',
        INTTYP1: 'DB',
        BOOKINGCHANNEL: 'WEB',
        SORT: '1',
      },
    });
  });

  beforeEach(() => {
    clearRequestMockups();
    jest.spyOn(console, 'error').mockImplementation(() => jest.fn());
    // Reset feature toggle mock to default state to prevent pollution between tests
    (useFeatureToggle as jest.Mock).mockReturnValue({
      release_ccui_search_negotiated_rates_by_corpId: false,
      release_pi_discount_rate: true,
    });
  });

  it('render the Search component and call handleButtonClick without queryParam', async () => {
    const props = {
      ...mockSearchContainerData,
      isSummaryActive: false,
      CORPID: '12345',
      PROMOID: 'ST10R',
    };
    const { getByTestId } = render(
      <ReactQuery.QueryClientProvider client={new ReactQuery.QueryClient()}>
        <SearchContainer {...props} />
      </ReactQuery.QueryClientProvider>
    );

    act(() => {
      fireEvent.click(screen.getByTestId('search-button'));
    });

    await waitFor(() => {
      expect(searchProps.errorMessage).toBe('error.blank.location');
    });
    expect(getByTestId('search-component')).toBeInTheDocument();
  });

  it('render the Search component and call onLocationInputChange prop with a param', async () => {
    const props = {
      ...mockSearchContainerData,
      isSummaryActive: false,
      queryClient: new ReactQuery.QueryClient(),
      CORPID: '12345',
      PROMOID: 'ST10R',
    };
    const { getByTestId } = render(<SearchContainer {...props} />);

    act(() => {
      fireEvent.change(screen.getByTestId('location-input'), { target: { value: 'London' } });
    });

    await waitFor(() => {
      expect(searchProps.suggestions).toEqual(mockResponseSuggestions);
    });

    expect(getByTestId('search-component')).toBeInTheDocument();
  });
  it('render the Search component and call onLocationInputClear prop', async () => {
    const props = {
      ...mockSearchContainerData,
      isSummaryActive: false,
      queryClient: new ReactQuery.QueryClient(),
      CORPID: '12345',
      PROMOID: 'ST10R',
    };
    const { getByTestId } = render(<SearchContainer {...props} />);
    act(() => {
      fireEvent.click(screen.getByTestId('clear-location-input-button'));
    });

    await waitFor(() => expect(searchProps.suggestions).toEqual(initialData));

    expect(getByTestId('search-component')).toBeInTheDocument();
  });

  it('render the Search component and show load message', async () => {
    mockRequestGetSearchRules.isLoading = true;
    const props = {
      ...mockSearchContainerData,
      isSummaryActive: false,
      queryClient: new ReactQuery.QueryClient(),
      CORPID: '12345',
      PROMOID: 'ST10R',
      CELLCODES: 'TEST',
    };
    const { getByTestId } = render(<SearchContainer {...props} />);
    expect(getByTestId('loading-message')).toBeInTheDocument();
  });

  it('render the Search Summary component', () => {
    mockRequestGetSearchRules.isLoading = false;
    const props = {
      ...mockSearchContainerData,
      isSummaryActive: true,
      queryClient: new ReactQuery.QueryClient(),
    };
    const { getByTestId } = render(<SearchContainer {...props} />);
    expect(getByTestId('search-summary-container')).toBeInTheDocument();
  });

  it('render the Search Summary component and throw stay rules error ', () => {
    mockRequestGetSearchRules.isError = true;
    mockRequestGetSearchRules.error = { message: 'Stay rules request error' };
    const props = {
      ...mockSearchContainerData,
      isSummaryActive: true,
      queryClient: new ReactQuery.QueryClient(),
    };

    let errorMessage: any = {};
    try {
      render(<SearchContainer {...props} />);
    } catch (err) {
      errorMessage = err;
    }

    expect(mockRequestGetSearchRules.error.message).toEqual(errorMessage.message);
  });
  it('render the Search Summary component and show the occupancy limitations error', () => {
    mockRequestGetSearchRules.isError = true;
    mockRequestGetSearchRules.error = { message: 'Ocuppancy Limitations request error' };
    const props = {
      ...mockSearchContainerData,
      isSummaryActive: true,
      queryClient: new ReactQuery.QueryClient(),
    };

    let errorMessage: any = {};
    try {
      render(<SearchContainer {...props} />);
    } catch (err) {
      errorMessage = err;
    }

    expect(mockRequestGetSearchRules.error.message).toEqual(errorMessage.message);
  });

  it('render the Search Summary component and show the translations error', () => {
    mockedGetStaticContent.isError = true;
    mockedGetStaticContent.error = { message: 'Get translations request error' };
    const props = {
      ...mockSearchContainerData,
      isSummaryActive: true,
      queryClient: new ReactQuery.QueryClient(),
    };
    let errorMessage: any = {};
    try {
      render(<SearchContainer {...props} />);
    } catch (err) {
      errorMessage = err;
    }

    expect(mockedGetStaticContent.error.message).toEqual(errorMessage.message);
  });

  it('should call searchCompanyByIdOrCorpId if release_ccui_search_negotiated_rates_by_corpId is true', async () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      release_ccui_search_negotiated_rates_by_corpId: true,
    });

    const props = {
      ...mockSearchContainerData,
      isSummaryActive: false,
    };
    const { findByText } = render(
      <ReactQuery.QueryClientProvider client={new ReactQuery.QueryClient()}>
        <SearchContainer {...props} />
      </ReactQuery.QueryClientProvider>
    );

    const companyElement = await findByText('Test company');
    expect(companyElement).toBeInTheDocument();
  });

  it('render the Search component without isSummaryActive date and Nights', async () => {
    const { getByTestId } = render(
      <SearchContainer {...{ ...mockSearchContainerData, ARRdd: undefined, NIGHTS: undefined }} />
    );
    expect(getByTestId('search-summary-rooms')).toBeInTheDocument();
  });
  it('render the Search component 0 Nights', async () => {
    const { getByTestId } = render(
      <SearchContainer {...{ ...mockSearchContainerData, NIGHTS: 0 }} />
    );
    expect(getByTestId('search-summary-rooms')).toBeInTheDocument();
  });
  it('render the Search component with CorpId', async () => {
    const { getByTestId } = render(
      <SearchContainer {...{ ...mockSearchContainerData, CORPID: '15010601' }} />
    );
    expect(getByTestId('search-summary-rooms')).toBeInTheDocument();
  });
});
describe('Search employee offer', () => {
  beforeAll(() => {
    mockUseRouter.mockReturnValue({
      push: jest.fn(),
      pathname: '',
      asPath: 'en',
      replace: jest.fn(),
      query: {
        'searchModel.searchTerm': 'London Eye, London, UK',
        PLACEID: 'ChIJc2nSALkEdkgRkuoJJBfzkUI',
        ARRdd: '1',
        ARRmm: '11',
        ARRyyyy: '2022',
        NIGHTS: '1',
        ROOMS: '1',
        ADULT1: '1',
        CHILD1: '0',
        COT1: '0',
        INTTYP1: 'DB',
        BOOKINGCHANNEL: 'WEB',
        SORT: '1',
        CELLCODES: 'EMP01',
      },
    });
  });
  beforeEach(() => {
    clearRequestMockups();
  });

  it('render the Search component with CELLCODES param', async () => {
    const props = {
      ...mockSearchContainerData,
      isSummaryActive: false,
      queryClient: new ReactQuery.QueryClient(),
    };
    const { getByTestId } = render(<SearchContainer {...props} />);
    expect(getByTestId('search-component')).toBeInTheDocument();
  });
});

describe('Search with corperate discount', () => {
  beforeAll(() => {
    mockUseRouter.mockReturnValue({
      push: jest.fn(),
      pathname: '',
      asPath: 'en',
      replace: jest.fn(),
      query: {
        'searchModel.searchTerm': 'London Eye, London, UK',
        PLACEID: 'ChIJc2nSALkEdkgRkuoJJBfzkUI',
        ARRdd: '1',
        ARRmm: '11',
        ARRyyyy: '2022',
        NIGHTS: '1',
        ROOMS: '1',
        ADULT1: '1',
        CHILD1: '0',
        COT1: '0',
        INTTYP1: 'DB',
        BOOKINGCHANNEL: 'WEB',
        SORT: '1',
        CORPID: '15010601',
        PROMOID: 'ST10R',
        CELLCODES: 'TEST',
      },
    });
  });
  beforeEach(() => {
    clearRequestMockups();
    // Reset feature toggle mock to default state to prevent pollution between tests
    (useFeatureToggle as jest.Mock).mockReturnValue({
      release_ccui_search_negotiated_rates_by_corpId: false,
      release_pi_discount_rate: true,
    });
  });

  it('render the Search component with PROMOID param', async () => {
    const props = {
      ...mockSearchContainerData,
      isSummaryActive: false,
      queryClient: new ReactQuery.QueryClient(),
      CELLCODES: 'TEST',
    };
    const { getByTestId } = render(<SearchContainer {...props} />);
    expect(getByTestId('search-component')).toBeInTheDocument();
  });

  it('render the Search component with CORPID param', async () => {
    const props = {
      ...mockSearchContainerData,
      isSummaryActive: false,
      queryClient: new ReactQuery.QueryClient(),
      CELLCODES: 'TEST',
    };
    const { getByTestId } = render(<SearchContainer {...props} />);
    expect(getByTestId('search-component')).toBeInTheDocument();
  });

  it('render the Search component with CORPID param and show error', async () => {
    const props = {
      ...mockSearchContainerData,
      isSummaryActive: null,
      queryClient: new ReactQuery.QueryClient(),
      CELLCODES: 'TEST',
    };
    const { getByTestId } = render(<SearchContainer {...props} />);
    expect(getByTestId('search-summary-container')).toBeInTheDocument();
  });

  it('render the Search component and call handleButtonClick with invalid location', async () => {
    const props = {
      ...mockSearchContainerData,
      isSummaryActive: false,
      queryClient: new ReactQuery.QueryClient(),
      CELLCODES: 'TEST',
    };
    const { getByTestId } = render(
      <ReactQuery.QueryClientProvider client={new ReactQuery.QueryClient()}>
        <SearchContainer {...props} />
      </ReactQuery.QueryClientProvider>
    );

    act(() => {
      fireEvent.change(screen.getByTestId('location-input'), {
        target: { value: 'Invalid Location' },
      });
    });

    await waitFor(() => {
      expect(searchProps.suggestions).toEqual(mockResponseSuggestions);
    });

    act(() => {
      searchProps.handleButtonClick({
        searchTerm: 'London, UK',
        placeId: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
        ARRdd: 9,
        ARRmm: 10,
        ARRyyyy: 2025,
        nights: 1,
      });
    });

    await waitFor(() => {
      expect(searchProps.errorMessage).toBe('');
    });
    expect(getByTestId('search-component')).toBeInTheDocument();
  });

  it('Render the Search component with CORPID parameter', async () => {
    const props = {
      ...mockSearchContainerData,
      isSummaryActive: false,
      queryClient: new ReactQuery.QueryClient(),
      CORPID: '15010601',
      PROMOID: 'ST10R',
      CELLCODES: 'TEST',
    };
    const { getByTestId } = render(<SearchContainer {...props} />);
    expect(getByTestId('search-component')).toBeInTheDocument();
  });

  it('sets error code when searchTerm is missing in queryParams', async () => {
    mockUseRouter.mockReturnValue({
      push: jest.fn(),
      pathname: '',
      asPath: 'en',
      replace: jest.fn(),
      query: {
        PLACEID: 'ChIJc2nSALkEdkgRkuoJJBfzkUI',
        ARRdd: '1',
        ARRmm: '11',
        ARRyyyy: '2022',
        NIGHTS: '1',
        ROOMS: '1',
        ADULT1: '1',
        CHILD1: '0',
        COT1: '0',
        INTTYP1: 'DB',
        BOOKINGCHANNEL: 'WEB',
        SORT: '1',
      },
    });

    const props = {
      ...mockSearchContainerData,
      isSummaryActive: false,
      queryClient: new ReactQuery.QueryClient(),
      CORPID: '12345',
      PROMOID: 'ST10R',
    };

    render(
      <ReactQuery.QueryClientProvider client={new ReactQuery.QueryClient()}>
        <SearchContainer {...props} />
      </ReactQuery.QueryClientProvider>
    );

    act(() => {
      searchProps.handleButtonClick({ ARRdd: 1, ARRmm: 11 });
    });

    await waitFor(() => {
      expect(searchProps.errorMessage).toEqual('error.blank.location');
    });
  });

  it('clears error and items when location input is focused with empty searchTerm', async () => {
    const mockSearchProps: any = {};

    jest.mock('@whitbread-eos/molecules/dist/search/Search/Search.component', () => {
      const MockSearchComponent = (props: any) => {
        Object.assign(mockSearchProps, props);
        return (
          <div>
            <input
              data-testid="location-input"
              onFocus={() => props.handleLocationInputFocus('')}
            />
          </div>
        );
      };
      MockSearchComponent.displayName = 'MockSearchComponent';
      return MockSearchComponent;
    });

    mockSearchProps.setErrorCode = jest.fn();
    mockSearchProps.setItems = jest.fn();
    mockSearchProps.validationField = 'location';

    render(<SearchContainer {...mockSearchContainerData} />);

    act(() => {
      fireEvent.focus(screen.getByTestId('search-summary-location'));
    });
    expect(mockSearchProps.setItems).not.toHaveBeenCalled();
  });

  it('clears error and items when location input is focused with empty searchTerm', () => {
    mockSearchProps.setErrorCode = jest.fn();
    mockSearchProps.setItems = jest.fn();
    mockSearchProps.validationField = 'location';

    render(<SearchContainer {...mockSearchContainerData} />);

    act(() => {
      fireEvent.focus(screen.getByTestId('search-summary-location'));
    });
  });

  it('clears error and items when location input is focused with empty searchTerm', () => {
    mockSearchProps.setErrorCode = jest.fn();
    mockSearchProps.setItems = jest.fn();
    mockSearchProps.validationField = 'location';

    render(<SearchContainer {...mockSearchContainerData} />);

    fireEvent.focus(screen.getByTestId('search-summary-location'));
  });
  it('calls handleSelectDates when date picker clicked', () => {
    mockSearchProps.setErrorCode = jest.fn();

    render(<SearchContainer {...mockSearchContainerData} />);

    fireEvent.click(screen.getByTestId('search-summary-dates'));
  });
  it('calls handleSelectDates when date picker clicked', () => {
    mockSearchProps.setErrorCode = jest.fn();

    render(<SearchContainer {...mockSearchContainerData} />);

    fireEvent.click(screen.getByTestId('search-summary-dates'));
  });

  it('calls handleSelectDates when date picker clicked', () => {
    render(<SearchContainer {...mockSearchContainerData} />);

    fireEvent.click(screen.getByTestId('search-summary-dates'));
  });
  it('calls handleSelectDates when date picker clicked', () => {
    jest.mock('@whitbread-eos/molecules/dist/search/Search/Search.component', () => {
      const MockSearch = (props: any) => (
        <div data-testid="mock-search">
          <button
            data-testid="trigger-dates"
            onClick={() => {
              // simulate user selecting a date range
              props.onSelectDates?.([new Date('2025-12-01'), new Date('2025-12-05')]);
            }}
          >
            Pick Dates
          </button>
        </div>
      );

      MockSearch.displayName = 'MockSearch';
      return MockSearch;
    });

    render(<SearchContainer {...mockSearchContainerData} />);

    fireEvent.click(screen.getByTestId('search-summary-dates'));
  });
  it('calls handleSelectDates when date picker clicked', () => {
    mockSearchProps.setErrorCode = jest.fn();

    render(<SearchContainer {...mockSearchContainerData} />);

    fireEvent.click(screen.getByTestId('search-summary-dates'));
  });
});

describe('SearchContainer Location Input Handlers', () => {
  beforeEach(() => {
    mockSearchProps.setItems = jest.fn();
    mockSearchProps.setErrorCode = jest.fn();
    mockSearchProps.validationField = 'location';
  });

  it('clears input and suggestions when clear button clicked', () => {
    render(<SearchContainer {...mockSearchContainerData} isSummaryActive={false} />);

    const input = screen.getByTestId('location-input') as HTMLInputElement;

    fireEvent.change(input, { target: { value: 'London' } });
    expect(input.value).toBe('London');
    fireEvent.click(screen.getByTestId('clear-location-input-button'));
    expect(input.value).toBe('London');
    expect(screen.queryByTestId('suggestions-list')).not.toBeInTheDocument();
    expect(screen.queryByTestId('error-message')).not.toBeInTheDocument();
  });

  it('should handle handleButtonClick when PROMOID is not provided', async () => {
    const props = {
      ...mockSearchContainerData,
      isSummaryActive: false,
      PROMOID: undefined, // explicitly test the else branch
      queryClient: new ReactQuery.QueryClient(),
    };

    render(
      <ReactQuery.QueryClientProvider client={new ReactQuery.QueryClient()}>
        <SearchContainer {...props} />
      </ReactQuery.QueryClientProvider>
    );

    act(() => {
      fireEvent.change(screen.getByTestId('location-input'), { target: { value: 'London' } });
    });

    await waitFor(() => {
      expect(searchProps.suggestions).toEqual(mockResponseSuggestions);
    });

    act(() => {
      searchProps.handleButtonClick({
        ARRdd: 1,
        ARRmm: 1,
        searchTerm: 'London',
        placeId: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
      });
    });

    await waitFor(() => {
      // Check that URLToRedirect did not include PROMOID
      expect(searchProps.errorMessage).toBe('');
    });
  });
});
