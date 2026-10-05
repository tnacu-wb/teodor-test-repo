import { InputGroup } from '@chakra-ui/react';
import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import '@testing-library/jest-dom';
import { act, fireEvent, screen, waitFor } from '@testing-library/react';
import user from '@testing-library/user-event';
import type {
  SearchPartialTranslationsType,
  SearchRoomOccupancyLimitationsType,
} from '@whitbread-eos/api';
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
import SearchComponent from './Search.component';
import SearchContainer from './Search.container';

global.fetch = jest.fn(() =>
  Promise.resolve({
    json: () => Promise.resolve(data),
  })
) as jest.Mock;

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useFeatureSwitch: () => true,
  swapKeysAndValues: jest.fn().mockReturnValue({
    DIS: 'accessible',
    DB: 'double',
    FAM: 'family',
    SB: 'single',
    TWIN: 'twin',
  }),
  useFeatureToggle: jest.fn(() => ({})),
}));

const data = {
  properties: [
    {
      code: 'LONSOH',
      brand: 'HUB',
      suggestion: 'hub London Soho',
      geometry: {
        type: 'Point',
        coordinates: [-0.136549, 51.513614],
      },
    },
    {
      code: 'LONCRE',
      brand: 'PI',
      suggestion: 'Derry / Londonderry',
      geometry: {
        type: 'Point',
        coordinates: [-7.278844, 54.992376],
      },
    },
    {
      code: 'LONRIC',
      brand: 'ZIP',
      suggestion: 'London Richmond',
      geometry: {
        type: 'Point',
        coordinates: [-0.291975, 51.466948],
      },
    },
    {
      code: 'LONARC',
      brand: 'PI',
      suggestion: 'London Archway',
      geometry: {
        type: 'Point',
        coordinates: [-0.135969, 51.565884],
      },
    },
    {
      code: 'BARPTI',
      brand: 'PI',
      suggestion: 'London Barking',
      geometry: {
        type: 'Point',
        coordinates: [0.0715156, 51.535072],
      },
    },
  ],
  managedPlaces: [],
  places: [
    {
      suggestion: 'London, UK',
      placeId: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
    },
    {
      suggestion: 'London Bridge, London, UK',
      placeId: 'ChIJxRO7WVEDdkgRrGM1fCYoHqY',
    },
    {
      suggestion: 'London Eye, London, UK',
      placeId: 'ChIJc2nSALkEdkgRkuoJJBfzkUI',
    },
    {
      suggestion: 'London Bridge Station, London, UK',
      placeId: 'ChIJ___OyFADdkgRkjYaWF6n5h0',
    },
    {
      suggestion: 'London Stansted Airport (STN), Bassingbourn Road, Stansted, UK',
      placeId: 'ChIJtxsqpbgEdkgRSCY1a5fQpDA',
    },
  ],
};

const mockedStayRules = {
  globalConfig: {
    maxRoomsLim: {
      maxRooms: 4,
    },
  },
  maxNightsLimitation: {
    maxNights: 9,
  },
  maxArrivalDateLimitation: {
    maxArrivalDate: 364,
  },
};

const mockedRoomOccupancyLimitations = {
  roomOccupancyLimitations: {
    roomOccupancies: [
      {
        adultsNumber: 1,
        childrenNumber: 0,
        acceptedRoomTypes: ['SB', 'DB', 'DIS'],
      },
      {
        adultsNumber: 1,
        childrenNumber: 1,
        acceptedRoomTypes: ['FAM'],
      },
      {
        adultsNumber: 1,
        childrenNumber: 2,
        acceptedRoomTypes: ['FAM'],
      },
      {
        adultsNumber: 2,
        childrenNumber: 0,
        acceptedRoomTypes: ['DB', 'TWIN', 'DIS'],
      },
      {
        adultsNumber: 2,
        childrenNumber: 1,
        acceptedRoomTypes: ['FAM'],
      },
      {
        adultsNumber: 2,
        childrenNumber: 2,
        acceptedRoomTypes: ['FAM'],
      },
    ],
  },
} as SearchRoomOccupancyLimitationsType;

const mockedPartialTranslations = {
  content: {
    global: {
      addRoom: 'Add another room',
      adult: 'adult',
      adults: 'adults',
      adultsLabel: 'Adults',
      child: 'child',
      children: 'children',
      childrenLabel: 'Children',
      done: 'Done',
      night: 'night',
      room: 'room',
      rooms: 'rooms',
      today: 'Today',
      tomorrow: 'Tomorrow',
      single: 'Single',
      double: 'Double',
      accessible: 'Accessible',
      twin: 'Twin',
      family: 'Family',
    },
  },
  datePicker: {
    reset: 'Reset',
  },
  form: {
    where: 'Enter place, postcode or hotel',
    adultsHelperText: 'Max 2 per room',
    checkout: 'Check out:',
    childrenHelperText: '2-15 years',
    includeCot: 'Include a cot?',
    removeRoom: 'Remove room',
    roomType: 'Room type',
  },
  results: {
    notifications: {
      groupBookingHeader: 'Unable to add more rooms',
      groupBookingMessage:
        'If you’d like to book five rooms or more, please call us and we’ll be happy to help.',
    },
  },
  config: {
    roomCodes: {
      accessible: 'DIS',
      double: 'DB',
      family: 'FAM',
      single: 'SB',
      twin: 'TWIN',
    },
  },
} as SearchPartialTranslationsType;

const mockedRoomCodes = {
  DIS: 'accessible',
  DB: 'double',
  FAM: 'family',
  SB: 'single',
  TWIN: 'twin',
};

const mockedAEMTranslations = {
  roomsWarningTitle: 'More rooms for more people',
  removeRoomButtonLabel: 'Remove',
  adultsLabel: 'Adults',
  childrenLabel: 'Children',
  roomTypeLabel: 'Room type',
  locationPlaceholder: 'Location',
  submitButtonLabel: 'Search',
  datepickerCheckinLabel: 'Check In',
  datepickerCheckoutLabel: 'Check Out',
  locationErrorLabel: 'Please enter a location or a hotel',
  numberOfNightsPlaceholder: 'Nights',
};

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
    if (onLocationInputChange) {
      onLocationInputChange(inputtedValue);
    }
  };
  return (
    <>
      <input
        data-testid="location-input"
        placeholder="Enter place, postcode or hotel"
        onChange={handleInputChange}
      />
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
const initialData: SearchSuggestions = {
  managedPlaces: [],
  places: [],
  properties: [],
};

const mockedDefaultParameters = {
  searchLocation: 'London',
  ARRdd: Number(23),
  ARRmm: Number(10),
  ARRyyyy: Number(2024),
  NIGHTS: Number(5),
  ROOMS: Number(1),
  ADULT1: Number(1),
  CHILD1: Number(0),
  locale: 'en',
  noOfNightsError: 'Error',
};

const mockedScreenSize = {
  isLessThanLg: false,
  isLessThanMd: false,
  isLessThanMobile: false,
  isLessThanSm: false,
  isLessThanXl: true,
  isLessThanXs: false,
};

jest.setTimeout(15000);

const handleButtonClick = jest.fn();
const setContractRateCompanyMock = jest.fn();

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

const clearRequestMockups = (): void => {
  mockRequestGetSearchRules.isError = false;
  mockedGetStaticContent.isError = false;
  mockRequestGetSearchRules.isLoading = false;
  mockedGetStaticContent.isLoading = false;
  mockRequestGetSearchRules.error = { message: '' };
  mockedGetStaticContent.error = { message: '' };
};

describe('SearchComponent', () => {
  it('should render the component', () => {
    const { getByTestId } = render(
      <InputGroup>
        <SearchComponent
          dataStayRules={mockedStayRules}
          dataRoomOccupancyLimitations={mockedRoomOccupancyLimitations}
          partialTranslations={mockedPartialTranslations}
          roomCodes={mockedRoomCodes}
          AEMTranslations={mockedAEMTranslations}
          screenSize={mockedScreenSize}
          handleButtonClick={handleButtonClick}
          suggestions={initialData}
          noOfNightsError={'Error'}
          searchStyles={{}}
          contractRateCompanyState={[null, setContractRateCompanyMock]}
          isSearchActive={true}
          mappedRoomLabels={{}}
        />
      </InputGroup>
    );
    expect(getByTestId('search-component')).toBeInTheDocument();
  });

  it('should search with default params', async () => {
    const { getByPlaceholderText, getByRole } = render(
      <InputGroup>
        <SearchComponent
          dataStayRules={mockedStayRules}
          dataRoomOccupancyLimitations={mockedRoomOccupancyLimitations}
          partialTranslations={mockedPartialTranslations}
          roomCodes={mockedRoomCodes}
          AEMTranslations={mockedAEMTranslations}
          screenSize={mockedScreenSize}
          handleButtonClick={handleButtonClick}
          suggestions={initialData}
          searchStyles={{}}
          contractRateCompanyState={[null, setContractRateCompanyMock]}
          isSearchActive={true}
          mappedRoomLabels={{}}
          {...mockedDefaultParameters}
        />
      </InputGroup>
    );
    const locationInput = getByPlaceholderText('Enter place, postcode or hotel');
    await user.click(locationInput);

    const button = getByRole('button', { name: mockedAEMTranslations.submitButtonLabel });
    await user.click(button);

    expect(handleButtonClick).toHaveBeenCalled();
  });

  it('should return nothing if the location is not specified', async () => {
    const { getByPlaceholderText, findByText } = render(
      <InputGroup>
        <SearchComponent
          dataStayRules={mockedStayRules}
          dataRoomOccupancyLimitations={mockedRoomOccupancyLimitations}
          partialTranslations={mockedPartialTranslations}
          roomCodes={mockedRoomCodes}
          AEMTranslations={mockedAEMTranslations}
          screenSize={mockedScreenSize}
          handleButtonClick={handleButtonClick}
          suggestions={initialData}
          noOfNightsError={'Error'}
          savedNights={5}
          searchStyles={{}}
          contractRateCompanyState={[null, setContractRateCompanyMock]}
          isSearchActive={true}
          mappedRoomLabels={{}}
        />
      </InputGroup>
    );
    const locationInput = getByPlaceholderText('Enter place, postcode or hotel');

    user.type(locationInput, 'lon');
    setTimeout(async () => {
      const item = await findByText(data.places[0].suggestion);
      user.click(item);

      expect(findByText('London, UK')).toBeInTheDocument;
      user.type(locationInput, '');
      expect(findByText('London, UK')).toBeInTheDocument;
    }, 300);
  });
});

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

describe('Search with corporate discount', () => {
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
