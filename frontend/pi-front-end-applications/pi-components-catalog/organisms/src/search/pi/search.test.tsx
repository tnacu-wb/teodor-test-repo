import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { act, fireEvent, screen, waitFor } from '@testing-library/react';
import type { SearchSuggestions } from '@whitbread-eos/api';
import React from 'react';

import {
  mockedGetStaticContent,
  mockSearchContainerData,
  mockResponseSuggestions,
  mockRequestGetSearchRules,
  mockMapSearchParamsData,
  mockSearchErrorFieldsData,
  mockSearchErrorKeysData,
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
  setSearchLocationInLocalStorage: jest.fn(),
  mapSearchParamsForURL: jest.fn().mockReturnValue(mockMapSearchParamsData),
  ERROR_KEYS: mockSearchErrorKeysData,
  ERROR_FIELDS: mockSearchErrorFieldsData,
  getNotificationMarginTop: jest.fn().mockReturnValue('md'),
  formatSummaryDateRange: jest.fn().mockReturnValue('29 Oct - 30 Oct'),
}));

let searchProps: any = {};

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
    return (
      <div data-testid="search-component">
        <LocationInput
          onLocationInputClear={props.onLocationInputClear}
          onLocationInputChange={props.onLocationInputChange}
        />
        <SearchButton onClick={props.handleButtonClick} />
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
          case 'searchCompanyById':
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
  });

  it('render the Search component and call handleButtonClick without queryParam', async () => {
    const props = {
      ...mockSearchContainerData,
      isSummaryActive: false,
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
      },
    });
  });
  beforeEach(() => {
    clearRequestMockups();
  });

  it('render the Search component with CORPID param', async () => {
    const props = {
      ...mockSearchContainerData,
      isSummaryActive: false,
      queryClient: new ReactQuery.QueryClient(),
    };
    const { getByTestId } = render(<SearchContainer {...props} />);
    expect(getByTestId('search-component')).toBeInTheDocument();
  });

  it('render the Search component with CORPID param and show error', async () => {
    const props = {
      ...mockSearchContainerData,
      isSummaryActive: null,
      queryClient: new ReactQuery.QueryClient(),
    };
    const { getByTestId } = render(<SearchContainer {...props} />);
    expect(getByTestId('search-summary-container')).toBeInTheDocument();
  });

  it('render the Search component and call handleButtonClick with invalid location', async () => {
    const props = {
      ...mockSearchContainerData,
      isSummaryActive: false,
      queryClient: new ReactQuery.QueryClient(),
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
      fireEvent.click(screen.getByTestId('search-button'));
    });

    await waitFor(() => {
      expect(searchProps.errorMessage).toBe('error.blank.location');
    });
    expect(getByTestId('search-component')).toBeInTheDocument();
  });

  it('Render the Search component with CORPID parameter', async () => {
    const props = {
      ...mockSearchContainerData,
      isSummaryActive: false,
      queryClient: new ReactQuery.QueryClient(),
      CORPID: '15010601',
    };
    const { getByTestId } = render(<SearchContainer {...props} />);
    expect(getByTestId('search-component')).toBeInTheDocument();
  });
});
