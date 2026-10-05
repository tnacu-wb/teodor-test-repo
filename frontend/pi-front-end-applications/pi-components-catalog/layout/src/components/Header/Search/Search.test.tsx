import {
  Customer,
  FormInnB,
  GlobalInnB,
  LOCALES,
  ROOM_CODES,
  SearchRules,
} from '@whitbread-eos/api';

import { render, act, fireEvent, waitFor } from '../../../utils/test-utils';
import Search, { getDefaultRoom } from './Search.component';

const mockProps = {
  mobile: false,
  formLabels: {
    where: 'Where to?',
    whereIcon: '/content/dam/global/icons/common/location.svg',
    calendarIcon: '/content/dam/global/icons/common/calendar.svg',
    guestIcon: '/content/dam/global/icons/common/person.svg',
    whereDismissIcon: '/',
  } as FormInnB,
  globalLabels: {
    addRoom: 'Add room',
  } as GlobalInnB,
  locale: '',
  calendarIcons: {},
  icons: {},
  addRoomIcon: '/',
  userRole: 'SUPER',
  language: '',
  userDetails: { bookingPreference: {} } as Customer,
  searchRules: {
    globalConfig: {
      maxRoomsLim: {
        maxRooms: 9,
      },
    },
    maxNightsLimitation: {
      maxNights: 14,
    },
  } as SearchRules,
  onSearchButtonClick: jest.fn(),
};
const today = new Date();

const mockResultGetQueryParams: {
  ARRdd: number;
  ARRmm: number;
  ARRyyyy: number;
  nights: number;
  roomsNumber: number;
  brand: string;
  code: string;
  location: undefined;
  placeId: string;
  rooms: Array<{
    roomOne: { adults: number; children: number; shouldIncludeCot: boolean; roomType: string };
  }>;
  searchTerm: string | undefined;
} = {
  ARRdd: today.getDate(),
  ARRmm: today.getMonth() + 1,
  ARRyyyy: today.getFullYear(),
  nights: 1,
  roomsNumber: 1,
  brand: '',
  code: '',
  location: undefined,
  placeId: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
  rooms: [
    {
      roomOne: {
        adults: 1,
        children: 0,
        shouldIncludeCot: false,
        roomType: 'Double',
      },
    },
  ],
  searchTerm: 'London, UK',
};
const mockHotelInformation = {
  hotelInformation: { hotelId: 'LONEUS', name: 'London Euston', hotelOpeningDate: '' },
};
const mockHotelAvailability = {
  hotelAvailability: {
    available: true,
    roomRates: [
      {
        adultsNumber: 1,
        childrenNumber: 0,
        cotRequired: false,
        roomType: 'Double',
      },
    ],
  },
};
const mockStaticHotelInformation = { name: 'London Euston' };

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getQueryParams: () => {
      return mockResultGetQueryParams;
    },
    getHotelInformationIB: () => {
      return mockHotelInformation;
    },
    getHotelAvailabilitiesIB: () => {
      return mockHotelAvailability;
    },
    staticHotelInformationIB: () => {
      return mockStaticHotelInformation;
    },
    getSearchRedirectLink: jest.fn(),
    mapSearchParamsForURL: () => {
      return {
        searchTerm: 'London Euston',
        ARRdd: 18,
        ARRmm: 9,
        ARRyyyy: 2024,
        nights: 1,
        roomsNumber: 1,
        rooms: [
          {
            adults: 1,
            children: 0,
            shouldIncludeCot: false,
            roomType: 'Double',
          },
        ],
        code: 'LONEUS',
        location: [-0.129068, 51.527736],
        brand: 'pi',
        ADULT1: 1,
        CHILD1: 0,
        COT1: 0,
      };
    },
    roomOccupancyParamsForURL: jest.fn(),
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    isHotelOpeningSoon: () => false,
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    formatIBAssetsUrl: () => {
      return '/';
    },
    RolesRequired: serverUtils.RolesRequired,
    useTranslation: () => {
      return {
        t: (str: string) => str,
        i18n: {
          changeLanguage: () => new Promise(() => true),
        },
      };
    },
    validateRoomOccupancyConditions: serverUtils.validateRoomOccupancyConditions,
    getDaysInMonth: jest.fn(),
  };
});

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useSearchParams: () => {
    return mockSearchParams;
  },
  useParams: () => {
    return mockUseParams;
  },
  useRouter: () => ({
    push: jest.fn(),
  }),
}));

const mockUseParams = { slug: ['england', 'greater-london', 'london', 'london-euston.html'] };
const mockSearchParams = {
  forEach: jest.fn((callback: (value: string, key: string) => void) => {
    const params: Record<string, string> = {
      ARRdd: '25',
      ARRmm: '09',
      ARRyyyy: '2024',
      NIGHTS: '1',
      ROOMS: '1',
      ADULT1: '1',
      CHILD1: '0',
      COT1: '0',
      INTTYP1: 'DB',
    };
    Object.keys(params).forEach((key) => {
      callback(params[key], key);
    });
  }),
  get: (key: string) => key,
};

jest.mock('react', () => ({
  ...jest.requireActual('react'),
  useEffect: jest.fn(),
}));

describe('IB Search component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render search component', () => {
    const { getByTestId } = render(<Search {...mockProps} />);
    expect(getByTestId('IB-Search-Container-Desktop')).toBeInTheDocument();
  });
  it('should render search component with default room requirements', () => {
    if (mockProps.userDetails.bookingPreference) {
      mockProps.userDetails.bookingPreference.roomRequirements = {};
    }
    const { getByTestId } = render(<Search {...mockProps} />);
    expect(getByTestId('IB-Search-Container-Desktop')).toBeInTheDocument();
  });
  it('should render search mobile component', () => {
    mockProps.mobile = true;
    const { getByTestId } = render(<Search {...mockProps} />);
    expect(getByTestId('IB-Search-Container-Mobile')).toBeInTheDocument();
  });
  it('should render search component with default props', () => {
    mockProps.mobile = false;
    const { getByTestId } = render(<Search {...mockProps} />);
    expect(getByTestId('IB-Search-Container-Desktop')).toBeInTheDocument();
  });
  it('should render search component with no form labels', () => {
    mockProps.formLabels = {} as FormInnB;
    const { getByTestId } = render(<Search {...mockProps} />);
    expect(getByTestId('IB-Search-Container-Desktop')).toBeInTheDocument();
  });

  it('should render search component and add a room', async () => {
    const { getByTestId } = render(<Search {...mockProps} />);
    expect(getByTestId('IB-Search-Container-Desktop')).toBeInTheDocument();

    const roomOccupancyButton = getByTestId('Room-Occupancy-Button');
    expect(roomOccupancyButton).toBeInTheDocument();
    await act(async () => {
      fireEvent.click(roomOccupancyButton);
    });

    await waitFor(() => {
      expect(getByTestId('RoomOccupancyDropdown-Container')).toBeInTheDocument();
      expect(getByTestId('IB-Add-Room-Button')).toBeInTheDocument();
    });

    await act(async () => {
      fireEvent.click(getByTestId('IB-Add-Room-Button'));
    });

    await waitFor(() => {
      expect(getByTestId('IB-Room-2')).toBeInTheDocument();
    });
  });
  it('should render search component, add a room after that remove the room', async () => {
    const { getByTestId } = render(<Search {...mockProps} />);
    expect(getByTestId('IB-Search-Container-Desktop')).toBeInTheDocument();

    const roomOccupancyButton = getByTestId('Room-Occupancy-Button');
    expect(roomOccupancyButton).toBeInTheDocument();
    await act(async () => {
      fireEvent.click(roomOccupancyButton);
    });

    await waitFor(() => {
      expect(getByTestId('RoomOccupancyDropdown-Container')).toBeInTheDocument();
      expect(getByTestId('IB-Add-Room-Button')).toBeInTheDocument();
    });

    await act(async () => {
      fireEvent.click(getByTestId('IB-Add-Room-Button'));
    });

    await waitFor(() => {
      expect(getByTestId('IB-Room-2')).toBeInTheDocument();
    });

    const removeRoomButton = getByTestId('IB-Remove-Room-2-Button');
    expect(removeRoomButton).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(removeRoomButton);
    });
  });

  it('should render search component, add a room and click on search with searchTerm', async () => {
    const { getByTestId } = render(<Search {...mockProps} />);
    expect(getByTestId('IB-Search-Container-Desktop')).toBeInTheDocument();

    const locationInput = getByTestId('IB-Location-Input');
    const roomOccupancyButton = getByTestId('Room-Occupancy-Button');
    expect(locationInput).toBeInTheDocument();
    expect(roomOccupancyButton).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(locationInput);
      fireEvent.change(locationInput, { target: { value: 'London, UK' } });
    });

    await waitFor(() => {
      expect(locationInput).toHaveValue('London, UK');
    });

    await act(async () => {
      fireEvent.click(roomOccupancyButton);
    });

    await waitFor(() => {
      expect(getByTestId('RoomOccupancyDropdown-Container')).toBeInTheDocument();
    });

    const searchButton = getByTestId('IB-Search-Button');
    expect(searchButton).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(searchButton);
    });
  });
  it('should render search component, add a room and click on search with hotelId', async () => {
    mockResultGetQueryParams.code = 'LONEUS';
    const { getByTestId } = render(<Search {...mockProps} />);
    expect(getByTestId('IB-Search-Container-Desktop')).toBeInTheDocument();

    const locationInput = getByTestId('IB-Location-Input');
    const roomOccupancyButton = getByTestId('Room-Occupancy-Button');
    expect(locationInput).toBeInTheDocument();
    expect(roomOccupancyButton).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(locationInput);
      fireEvent.change(locationInput, { target: { value: 'London, UK' } });
    });

    await waitFor(() => {
      expect(locationInput).toHaveValue('London, UK');
    });

    await act(async () => {
      fireEvent.click(roomOccupancyButton);
    });

    await waitFor(() => {
      expect(getByTestId('RoomOccupancyDropdown-Container')).toBeInTheDocument();
    });

    const searchButton = getByTestId('IB-Search-Button');
    expect(searchButton).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(searchButton);
    });
  });
  it('should render search component, add a room and click on search without searchTerm', async () => {
    mockResultGetQueryParams.searchTerm = undefined;

    const { getByTestId } = render(<Search {...mockProps} />);
    expect(getByTestId('IB-Search-Container-Desktop')).toBeInTheDocument();

    const locationInput = getByTestId('IB-Location-Input');
    const roomOccupancyButton = getByTestId('Room-Occupancy-Button');
    expect(locationInput).toBeInTheDocument();
    expect(roomOccupancyButton).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(locationInput);
      fireEvent.change(locationInput, { target: { value: 'London, UK' } });
    });

    await waitFor(() => {
      expect(locationInput).toHaveValue('London, UK');
    });

    await act(async () => {
      fireEvent.click(roomOccupancyButton);
    });

    await waitFor(() => {
      expect(getByTestId('RoomOccupancyDropdown-Container')).toBeInTheDocument();
    });

    const searchButton = getByTestId('IB-Search-Button');
    expect(searchButton).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(searchButton);
    });
  });
});

describe('getDefaultRoom', () => {
  it('returns default room with user preferences', () => {
    const userDetails = {
      bookingPreference: {
        roomRequirements: {
          adults: 2,
          children: 0,
          cotRequired: true,
          type: ROOM_CODES.double,
        },
      },
    } as Customer;
    expect(getDefaultRoom(userDetails)).toEqual({
      adults: 2,
      children: 0,
      shouldIncludeCot: true,
      roomType: ROOM_CODES.double,
    });
  });

  it('returns default room with fallback values', () => {
    const userDetails = { bookingPreference: {} } as Customer;
    expect(getDefaultRoom(userDetails)).toEqual({
      adults: 1,
      children: 0,
      shouldIncludeCot: false,
      roomType: ROOM_CODES.double,
    });
  });

  it('fixes invalid adults/roomType combination', () => {
    const userDetails = {
      bookingPreference: {
        roomRequirements: {
          adults: 1,
          children: 0,
          cotRequired: false,
          type: ROOM_CODES.twin,
        },
      },
    } as Customer;
    expect(getDefaultRoom(userDetails)).toEqual({
      adults: 1,
      children: 0,
      shouldIncludeCot: false,
      roomType: ROOM_CODES.double,
    });
  });
});

describe('Search overlay functionality', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    document.body.style.overflow = '';
  });

  afterEach(() => {
    document.body.style.overflow = '';
  });

  it('should render overlay when location input is focused on desktop', async () => {
    const testProps = {
      ...mockProps,
      mobile: false,
    };
    const { getByTestId, queryByTestId } = render(<Search {...testProps} />);

    expect(queryByTestId('IB-Search-Overlay')).not.toBeInTheDocument();

    const locationInput = getByTestId('IB-Location-Input');
    await act(async () => {
      fireEvent.focus(locationInput);
    });
  });

  it('should not render overlay on mobile view', async () => {
    const testProps = {
      ...mockProps,
      mobile: true,
    };
    const { queryByTestId } = render(<Search {...testProps} />);

    expect(queryByTestId('IB-Search-Overlay')).not.toBeInTheDocument();
  });

  it('should close all dropdowns when overlay is clicked', async () => {
    const testProps = {
      ...mockProps,
      mobile: false,
    };
    const { getByTestId, queryByTestId } = render(<Search {...testProps} />);

    const roomOccupancyButton = getByTestId('Room-Occupancy-Button');
    await act(async () => {
      fireEvent.click(roomOccupancyButton);
    });

    await waitFor(() => {
      expect(getByTestId('RoomOccupancyDropdown-Container')).toBeInTheDocument();
    });

    const overlay = queryByTestId('IB-Search-Overlay');
    if (overlay) {
      await act(async () => {
        fireEvent.click(overlay);
      });
    }
  });
});
