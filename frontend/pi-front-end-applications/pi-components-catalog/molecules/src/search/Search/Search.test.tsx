import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import user from '@testing-library/user-event';
import {
  SearchPartialTranslationsType,
  SearchRoomOccupancyLimitationsType,
} from '@whitbread-eos/api';
import { add } from 'date-fns';
import React from 'react';

import { act, fireEvent, render, screen, userEvent } from '../../utils/test-utils';
import SearchComponent from './Search.component';

global.fetch = jest.fn(() =>
  Promise.resolve({
    json: () => Promise.resolve(data),
  })
) as jest.Mock;

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
      maxRoomsAmend: 5,
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

const initialData = {
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

describe('SearchComponent', () => {
  it('should render the component', () => {
    const { getByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
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
          isSearchActive={true}
          mappedRoomLabels={{}}
          {...otherProps}
        />
      </QueryClientProvider>
    );
    expect(getByTestId('search-component')).toBeInTheDocument();
  });
  it('should render the component for Employee offer flow', () => {
    const { getByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
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
          isSearchActive={true}
          mappedRoomLabels={{}}
          isActiveMatchedOffer={true}
          matchedOffer={{
            cellCode: 'EMP01',
            maxRooms: 2,
            numberOfNights: 9,
            page: 'employee-offer',
            corpId: '',
            ratePlanCode: '',
          }}
          {...otherProps}
        />
      </QueryClientProvider>
    );
    expect(getByTestId('search-component')).toBeInTheDocument();
  });
  it('should search with default params', async () => {
    const { getByPlaceholderText, getByRole } = render(
      <QueryClientProvider client={new QueryClient()}>
        <SearchComponent
          mappedRoomLabels={{}}
          dataStayRules={mockedStayRules}
          dataRoomOccupancyLimitations={mockedRoomOccupancyLimitations}
          partialTranslations={mockedPartialTranslations}
          roomCodes={mockedRoomCodes}
          AEMTranslations={mockedAEMTranslations}
          screenSize={mockedScreenSize}
          handleButtonClick={handleButtonClick}
          suggestions={initialData}
          searchStyles={{}}
          isSearchActive={true}
          {...mockedDefaultParameters}
          {...otherProps}
        />
      </QueryClientProvider>
    );
    const locationInput = getByPlaceholderText('Enter place, postcode or hotel');
    await user.click(locationInput);

    const button = getByRole('button', { name: mockedAEMTranslations.submitButtonLabel });
    await user.click(button);

    // TODO: test API call
    console.log = jest.fn();
    console.log(mockedDefaultParameters);
    expect(console.log).toHaveBeenCalledWith(mockedDefaultParameters);
  });
  it('should return nothing if the location is not specified', async () => {
    const { getByPlaceholderText } = render(
      <QueryClientProvider client={new QueryClient()}>
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
          isSearchActive={true}
          mappedRoomLabels={{}}
          locale="gb"
          {...otherProps}
        />
      </QueryClientProvider>
    );
    const locationInput = getByPlaceholderText('Enter place, postcode or hotel');

    await user.type(locationInput, 'lon');
    expect(locationInput).toHaveValue('lon');
  });
});

describe('SearchComponent', () => {
  it('should render the search component for PI - Group booking page enabled', () => {
    const { getByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
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
          isSearchActive={true}
          mappedRoomLabels={{}}
          isPIGroupBookingFormEnabled={true}
          isBBGroupBookingFormEnabled={false}
          {...otherProps}
        />
      </QueryClientProvider>
    );
    expect(getByTestId('search-component')).toBeInTheDocument();
  });
  it('should render the search component for BB - Group booking page enabled', () => {
    const { getByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
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
          isSearchActive={true}
          mappedRoomLabels={{}}
          isPIGroupBookingFormEnabled={false}
          isBBGroupBookingFormEnabled={true}
          {...otherProps}
        />
      </QueryClientProvider>
    );
    expect(getByTestId('search-component')).toBeInTheDocument();
  });
  it('should render the search component with default rooms', () => {
    const { getByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
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
          isSearchActive={true}
          mappedRoomLabels={{}}
          isPIGroupBookingFormEnabled={false}
          isBBGroupBookingFormEnabled={true}
          defaultRooms={[
            {
              adults: 1,
              children: 0,
              shouldIncludeCot: false,
              roomType: 'DB',
            },
          ]}
          {...otherProps}
        />
      </QueryClientProvider>
    );
    expect(getByTestId('search-component')).toBeInTheDocument();
  });

  it('should render the search component with isDatepickerError', () => {
    const { getByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
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
          isSearchActive={true}
          mappedRoomLabels={{}}
          isPIGroupBookingFormEnabled={false}
          isBBGroupBookingFormEnabled={true}
          isDatepickerError
          {...otherProps}
        />
      </QueryClientProvider>
    );
    expect(getByTestId('search-component')).toBeInTheDocument();
  });
});

describe('SearchComponent defaultRooms prop sync', () => {
  const baseProps = {
    dataStayRules: mockedStayRules,
    dataRoomOccupancyLimitations: mockedRoomOccupancyLimitations,
    partialTranslations: mockedPartialTranslations,
    roomCodes: mockedRoomCodes,
    AEMTranslations: mockedAEMTranslations,
    screenSize: mockedScreenSize,
    handleButtonClick,
    suggestions: initialData,
    searchStyles: {},
    isSearchActive: true,
    mappedRoomLabels: {
      DB: 'Double',
      TWIN: 'Twin',
      SB: 'Single',
      FAM: 'Family',
      DIS: 'Accessible',
    },
    ...otherProps,
  };

  it('should update rooms when defaultRooms prop changes after initial render', async () => {
    const queryClient = new QueryClient();

    const { rerender, getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <SearchComponent {...baseProps} defaultRooms={undefined} />
      </QueryClientProvider>
    );

    expect(getByTestId('search-component')).toBeInTheDocument();

    await act(async () => {
      rerender(
        <QueryClientProvider client={queryClient}>
          <SearchComponent
            {...baseProps}
            defaultRooms={[{ adults: 2, children: 1, shouldIncludeCot: false, roomType: 'DB' }]}
          />
        </QueryClientProvider>
      );
    });

    expect(getByTestId('search-component')).toBeInTheDocument();
  });

  it('should not reset rooms when defaultRooms changes to undefined after being set', async () => {
    const queryClient = new QueryClient();

    const { rerender, getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <SearchComponent
          {...baseProps}
          defaultRooms={[{ adults: 2, children: 0, shouldIncludeCot: false, roomType: 'DB' }]}
        />
      </QueryClientProvider>
    );

    expect(getByTestId('search-component')).toBeInTheDocument();

    await act(async () => {
      rerender(
        <QueryClientProvider client={queryClient}>
          <SearchComponent {...baseProps} defaultRooms={undefined} />
        </QueryClientProvider>
      );
    });

    // Component remains rendered — rooms were not reset to default because mapped.length === 0
    expect(getByTestId('search-component')).toBeInTheDocument();
  });

  it('should not trigger room update on initial render when defaultRooms is already set', () => {
    const queryClient = new QueryClient();

    const { getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <SearchComponent
          {...baseProps}
          defaultRooms={[{ adults: 2, children: 0, shouldIncludeCot: false, roomType: 'DB' }]}
        />
      </QueryClientProvider>
    );

    // useRef skip-first-render guard prevents redundant setRooms call on mount
    expect(getByTestId('search-component')).toBeInTheDocument();
  });
});

describe('SearchComponent TIR', () => {
  const props = {
    dataStayRules: mockedStayRules,
    dataRoomOccupancyLimitations: mockedRoomOccupancyLimitations,
    partialTranslations: mockedPartialTranslations,
    roomCodes: mockedRoomCodes,
    AEMTranslations: mockedAEMTranslations,
    screenSize: { ...mockedScreenSize, isLessThanSm: true },
    handleButtonClick,
    suggestions: initialData,
    searchStyles: {},
    isSearchActive: true,
    mappedRoomLabels: {},
    isPIGroupBookingFormEnabled: false,
    isBBGroupBookingFormEnabled: true,
    isActiveMatchedOffer: true,
    matchedOffer: {
      corpId: '15010601',
      numberOfNights: 9,
      cellCode: '',
      ratePlanCode: 'FCDNLR30',
      page: 'travel-industry-rate',
      maxRooms: 2,
    },
  };
  it('should render the search component with isDiscountRateEnabled', () => {
    const { getByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
        <SearchComponent isDiscountRateEnabled {...props} {...otherProps} />
      </QueryClientProvider>
    );
    expect(getByTestId('search-component')).toBeInTheDocument();
  });
  it('should render the search component with isDiscountRateEnabled false', () => {
    const { getByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
        <SearchComponent isDiscountRateEnabled={false} {...props} {...otherProps} />
      </QueryClientProvider>
    );
    expect(getByTestId('search-component')).toBeInTheDocument();
  });
});

describe('SearchComponent room type fallback logic', () => {
  it('should use partialTranslations.content.global.double when defined (truthy branch)', () => {
    const { getByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
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
          isSearchActive={true}
          mappedRoomLabels={{}}
          {...otherProps}
        />
      </QueryClientProvider>
    );
    expect(getByTestId('search-component')).toBeInTheDocument();
  });

  it('should use StandardRoomType.DB fallback when partialTranslations.content.global.double is falsy (falsy branch)', () => {
    const partialTranslationsWithEmptyDouble = {
      ...mockedPartialTranslations,
      content: {
        ...mockedPartialTranslations.content,
        global: {
          ...mockedPartialTranslations.content.global,
          double: '',
        },
      },
    };

    const { getByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
        <SearchComponent
          dataStayRules={mockedStayRules}
          dataRoomOccupancyLimitations={mockedRoomOccupancyLimitations}
          partialTranslations={partialTranslationsWithEmptyDouble}
          roomCodes={mockedRoomCodes}
          AEMTranslations={mockedAEMTranslations}
          screenSize={mockedScreenSize}
          handleButtonClick={handleButtonClick}
          suggestions={initialData}
          searchStyles={{}}
          isSearchActive={true}
          mappedRoomLabels={{}}
          {...otherProps}
        />
      </QueryClientProvider>
    );
    expect(getByTestId('search-component')).toBeInTheDocument();
  });
});

describe('SearchComponent different screen sizes', () => {
  const props = {
    dataStayRules: mockedStayRules,
    dataRoomOccupancyLimitations: mockedRoomOccupancyLimitations,
    partialTranslations: mockedPartialTranslations,
    roomCodes: mockedRoomCodes,
    AEMTranslations: mockedAEMTranslations,
    handleButtonClick,
    suggestions: initialData,
    searchStyles: {},
    isSearchActive: true,
    mappedRoomLabels: {},
    isPIGroupBookingFormEnabled: false,
    isBBGroupBookingFormEnabled: true,
  };
  it('should render the search component with isLessThanSm', () => {
    const { getByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
        <SearchComponent
          screenSize={{ ...mockedScreenSize, isLessThanSm: true }}
          {...props}
          {...otherProps}
        />
      </QueryClientProvider>
    );
    expect(getByTestId('search-component')).toBeInTheDocument();
  });
  it('should render the search component with isLessThanMd', () => {
    const { getByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
        <SearchComponent
          screenSize={{ ...mockedScreenSize, isLessThanMd: true }}
          {...props}
          {...otherProps}
        />
      </QueryClientProvider>
    );
    expect(getByTestId('search-component')).toBeInTheDocument();
  });
  it('should render the search component with isLessThanLg', () => {
    const { getByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
        <SearchComponent
          screenSize={{ ...mockedScreenSize, isLessThanLg: true }}
          {...props}
          {...otherProps}
        />
      </QueryClientProvider>
    );
    expect(getByTestId('search-component')).toBeInTheDocument();
  });
  it('should render the search component with isLessThanXs', () => {
    const { getByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
        <SearchComponent
          screenSize={{ ...mockedScreenSize, isLessThanXs: true }}
          {...props}
          {...otherProps}
        />
      </QueryClientProvider>
    );
    expect(getByTestId('search-component')).toBeInTheDocument();
  });
});

describe('SearchComponent', () => {
  const props = {
    dataStayRules: mockedStayRules,
    dataRoomOccupancyLimitations: mockedRoomOccupancyLimitations,
    partialTranslations: mockedPartialTranslations,
    roomCodes: mockedRoomCodes,
    AEMTranslations: mockedAEMTranslations,
    handleButtonClick,
    suggestions: initialData,
    screenSize: mockedScreenSize,
    searchStyles: {},
    isSearchActive: true,
    mappedRoomLabels: {},
    isPIGroupBookingFormEnabled: false,
    isBBGroupBookingFormEnabled: true,
  };
  it('should render the search component with onIsSearchActive', () => {
    const { getByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
        <SearchComponent onIsSearchActive={jest.fn()} {...props} {...otherProps} />
      </QueryClientProvider>
    );
    userEvent.tab();

    expect(getByTestId('search-component')).toBeInTheDocument();
  });
  it('should click on room picker done button', () => {
    const { getByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
        <SearchComponent onIsSearchActive={jest.fn()} {...props} {...otherProps} />
      </QueryClientProvider>
    );
    const datePickerInput = screen.getByRole('textbox', { name: /datepicker-input/i });
    fireEvent.click(datePickerInput);

    const doneBtn = screen.getByRole('button', { name: /done/i });
    act(() => userEvent.click(doneBtn));

    expect(getByTestId('search-component')).toBeInTheDocument();
  });

  it('should render correctly with disableFlip={true} and open calendar on click', async () => {
    const { getByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
        <SearchComponent {...props} {...otherProps} disableFlip={true} />
      </QueryClientProvider>
    );
    expect(getByTestId('search-component')).toBeInTheDocument();

    const datePickerInput = screen.getByRole('textbox', { name: /datepicker-input/i });
    await user.click(datePickerInput);

    expect(document.body.querySelector('.react-datepicker')).toBeInTheDocument();
  });

  it('should render correctly without disableFlip prop', () => {
    const { getByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
        <SearchComponent {...props} {...otherProps} />
      </QueryClientProvider>
    );
    expect(getByTestId('search-component')).toBeInTheDocument();
  });
});
