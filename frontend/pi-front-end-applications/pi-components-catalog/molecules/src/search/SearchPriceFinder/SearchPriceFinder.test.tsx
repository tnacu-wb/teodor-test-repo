import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { SearchPartialTranslationsType } from '@whitbread-eos/api';
import { add } from 'date-fns';
import type { NextRouter } from 'next/router';

import { fireEvent, render, screen } from '../../utils/test-utils';
import { updateQueryParams } from './SearchPriceFinder.component';
import SearchComponent from './SearchPriceFinder.component';

global.fetch = jest.fn(() =>
  Promise.resolve({
    json: () => Promise.resolve(data),
  })
) as jest.Mock;

const mockRouter: Partial<NextRouter> = {
  push: jest.fn(),
  query: {
    ARRdd: '1',
    ARRmm: '11',
    ARRyyyy: '2025',
    PLACEID: '2ChIJLeE-dKZ4cUgRCZpt1tAnixM',
    searchTerm: 'London, UK',
  },
  locale: 'en-GB',
};

const mockRouter2: Partial<NextRouter> = {
  push: jest.fn(),
  query: {},
  locale: 'en-GB',
};

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
      offers: [
        {
          cellCode: 'EMP01',
          maxRooms: 2,
          numberOfNights: 9,
          page: 'employee-offer',
        },
      ],
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
    cotLimit: '0-2 years',
    removeRoom: 'Remove room',
    roomType: 'Room type',
  },
  results: {
    notifications: {
      groupBookingHeader: 'Unable to add more rooms',
      groupBookingMessage:
        'If you’d like to book five rooms or more, please call us and we’ll be happy to help.',
      ccuiGroupBookingMessage: 'Error message for CCUI',
      groupBookingFormPageMessage:
        'To make a group booking of 5 to 9 rooms, contact us via Live Chat for guidance. To book 10 rooms or more, please complete the <a href="/why/groups/form{groupBookingLink}">group booking form</a> and we will be in contact to discuss your enquiry.',
      emp01groupBookingMessage: 'You can book a maximum of 2 rooms using the employee rate',
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

const initialData = {
  managedPlaces: [],
  places: [],
  properties: [],
};

const mockedSuggestions = {
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

const mockedScreenSize = {
  isLessThanLg: false,
  isLessThanMd: false,
  isLessThanMobile: false,
  isLessThanSm: false,
  isLessThanXl: true,
  isLessThanXs: false,
};

jest.setTimeout(10000);

const today = new Date();
const startDate = add(today, { days: 5 });
const endDate = add(today, { days: 8 });

const otherProps = {
  onSelectDates: () => undefined,
  startDate,
  endDate,
};
const handleButtonClick = jest.fn();
const onIsSearchActive = jest.fn();
const setLocationName = jest.fn();
const satelliteTrack = jest.fn();
const satelliteLoaded = jest.fn();

// Track QueryClient instances for cleanup
const queryClients: QueryClient[] = [];

describe('SearchComponent', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (window as any)._satellite = { track: satelliteTrack };
    (window as any).__satelliteLoaded = { track: satelliteLoaded };
  });

  afterEach(() => {
    // Clean up all QueryClient instances
    queryClients.forEach((client) => client.clear());
    queryClients.length = 0;
  });

  it('should render the component with Search Icon button', () => {
    const { getByTestId } = render(
      <QueryClientProvider
        client={(() => {
          const client = new QueryClient({
            defaultOptions: { queries: { retry: false }, mutations: { retry: false } },
          });
          queryClients.push(client);
          return client;
        })()}
      >
        <SearchComponent
          router={mockRouter as NextRouter}
          partialTranslations={mockedPartialTranslations}
          AEMTranslations={mockedAEMTranslations}
          screenSize={{ ...mockedScreenSize, isLessThanMd: true, isLessThanXl: false }}
          handleLocationSearch={handleButtonClick}
          suggestions={data}
          searchPriceFinderStyles={{}}
          isSearchActive={true}
          {...otherProps}
        />
      </QueryClientProvider>
    );
    expect(getByTestId('search-component')).toBeInTheDocument();
  });
  it('should render Search text button', () => {
    render(
      <QueryClientProvider
        client={(() => {
          const client = new QueryClient({
            defaultOptions: { queries: { retry: false }, mutations: { retry: false } },
          });
          queryClients.push(client);
          return client;
        })()}
      >
        <SearchComponent
          router={mockRouter as NextRouter}
          partialTranslations={mockedPartialTranslations}
          AEMTranslations={mockedAEMTranslations}
          screenSize={{
            ...mockedScreenSize,
            isLessThanLg: true,
            isLessThanSm: false,
          }}
          handleLocationSearch={handleButtonClick}
          suggestions={data}
          searchPriceFinderStyles={{}}
          isSearchActive={true}
          {...otherProps}
        />
      </QueryClientProvider>
    );
    expect(screen.getByText('Search')).toBeInTheDocument();
  });

  it('should render AutoComplete component after typing in a Location', async () => {
    const { getByTestId } = render(
      <QueryClientProvider
        client={(() => {
          const client = new QueryClient({
            defaultOptions: { queries: { retry: false }, mutations: { retry: false } },
          });
          queryClients.push(client);
          return client;
        })()}
      >
        <SearchComponent
          router={mockRouter as NextRouter}
          partialTranslations={mockedPartialTranslations}
          AEMTranslations={mockedAEMTranslations}
          screenSize={mockedScreenSize}
          handleLocationSearch={handleButtonClick}
          suggestions={data}
          searchPriceFinderStyles={{}}
          isSearchActive={false}
          onIsSearchActive={onIsSearchActive}
          {...otherProps}
        />
      </QueryClientProvider>
    );

    const searchComponent = getByTestId('search-component');
    fireEvent.focus(searchComponent);
    const input = getByTestId('locationPicker-locationPlaceholder');
    expect(input).toBeInTheDocument();
    fireEvent.change(input, { target: { value: 'London' } });
    const autocomplete = await screen.findByTestId('locationPicker-autocompleteList');
    expect(autocomplete).toBeInTheDocument();
    expect(onIsSearchActive).toHaveBeenCalledWith(true);
  });

  it('should not call onIsSearchActive', () => {
    const { getByTestId } = render(
      <QueryClientProvider
        client={(() => {
          const client = new QueryClient({
            defaultOptions: { queries: { retry: false }, mutations: { retry: false } },
          });
          queryClients.push(client);
          return client;
        })()}
      >
        <SearchComponent
          router={mockRouter as NextRouter}
          partialTranslations={mockedPartialTranslations}
          AEMTranslations={mockedAEMTranslations}
          screenSize={{
            ...mockedScreenSize,
            isLessThanMd: false,
          }}
          handleLocationSearch={handleButtonClick}
          suggestions={initialData}
          searchPriceFinderStyles={{}}
          isSearchActive={false}
          {...otherProps}
        />
      </QueryClientProvider>
    );

    const searchComponent = getByTestId('search-component');
    fireEvent.focus(searchComponent);
    expect(onIsSearchActive).toHaveBeenCalledTimes(0);
  });

  it('should be able to click search button', () => {
    const { getByTestId } = render(
      <QueryClientProvider
        client={(() => {
          const client = new QueryClient({
            defaultOptions: { queries: { retry: false }, mutations: { retry: false } },
          });
          queryClients.push(client);
          return client;
        })()}
      >
        <SearchComponent
          router={mockRouter as NextRouter}
          partialTranslations={mockedPartialTranslations}
          AEMTranslations={mockedAEMTranslations}
          screenSize={{
            ...mockedScreenSize,
            isLessThanMd: false,
          }}
          handleLocationSearch={handleButtonClick}
          suggestions={mockedSuggestions}
          searchPriceFinderStyles={{}}
          isSearchActive={false}
          setLocationName={setLocationName}
          {...otherProps}
        />
      </QueryClientProvider>
    );

    const searchComponentButton = getByTestId('search-component-button');
    expect(searchComponentButton).toBeInTheDocument();
    fireEvent.click(searchComponentButton);
    expect(handleButtonClick).toHaveBeenCalled();
  });

  it('should set location name', () => {
    const { getByTestId } = render(
      <QueryClientProvider
        client={(() => {
          const client = new QueryClient({
            defaultOptions: { queries: { retry: false }, mutations: { retry: false } },
          });
          queryClients.push(client);
          return client;
        })()}
      >
        <SearchComponent
          router={mockRouter as NextRouter}
          partialTranslations={mockedPartialTranslations}
          AEMTranslations={mockedAEMTranslations}
          screenSize={{
            ...mockedScreenSize,
            isLessThanMd: false,
          }}
          suggestions={mockedSuggestions}
          searchPriceFinderStyles={{}}
          isSearchActive={false}
          {...otherProps}
        />
      </QueryClientProvider>
    );

    const searchComponentButton = getByTestId('search-component-button');
    expect(searchComponentButton).toBeInTheDocument();
    fireEvent.click(searchComponentButton);
    expect(setLocationName).toHaveBeenCalledTimes(0);
  });

  it('should NOT call updateQueryParams when PLACEID is missing', () => {
    const queryClient = new QueryClient();

    const { getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <SearchComponent
          router={mockRouter2 as NextRouter}
          partialTranslations={{ form: { where: 'Enter' } } as any}
          AEMTranslations={{ submitButtonLabel: 'Search' } as any}
          screenSize={{
            ...mockedScreenSize,
            isLessThanMd: false,
          }}
          suggestions={{
            properties: [],
            places: [{ suggestion: 'London', placeId: 'dKZ4cUgRCZpt1tAnixM' }],
            managedPlaces: [],
          }}
          handleLocationSearch={handleButtonClick}
          setLocationName={setLocationName}
          searchPriceFinderStyles={{}}
          isSearchActive={true}
        />
      </QueryClientProvider>
    );

    const button = getByTestId('search-component-button');

    fireEvent.click(button);

    expect(handleButtonClick).toHaveBeenCalled();
    expect(setLocationName).toHaveBeenCalled();
    expect(mockRouter.push).not.toHaveBeenCalled();
  });
});

describe('updateQueryParams deletes keys when value is empty', () => {
  let router: { push: jest.Mock };
  let searchParams: URLSearchParams;

  beforeEach(() => {
    router = { push: jest.fn() };
    searchParams = new URLSearchParams('');
    Object.defineProperty(window, 'location', {
      value: { pathname: '/price-finder' },
    });
  });

  it('should deletes key when value is null', () => {
    updateQueryParams({ PLACEID: null }, searchParams, router);
    expect(router.push).toHaveBeenCalledWith('/price-finder?');
  });

  it('should deletes key when value is undefined', () => {
    updateQueryParams({ PLACEID: undefined }, searchParams, router);
    expect(router.push).toHaveBeenCalledWith('/price-finder?');
  });

  it('should deletes key when value is empty string', () => {
    updateQueryParams({ PLACEID: '' }, searchParams, router);
    expect(router.push).toHaveBeenCalledWith('/price-finder?');
  });
});
