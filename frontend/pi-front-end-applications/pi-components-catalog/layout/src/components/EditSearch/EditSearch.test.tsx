import { LOCALES } from '@whitbread-eos/api';

import { render, mockUseTranslation, userEvent, act } from '../../utils/test-utils';
import EditSearch from './index';

const mockStaticHotelInformation = { name: 'London Euston' };

const mockSearchParams = {
  forEach: jest.fn((callback) => {
    const params = {
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
  get: (key) => key,
};

const mockGetMultiSearchParamsIB = {
  arrival: '2024-09-25',
  departure: '2024-09-26',
  numberOfUnits: 1,
  numberOfNights: 1,
  rooms: [
    {
      adults: 1,
      children: 0,
      roomType: 'Double',
      shouldIncludeCot: false,
    },
  ],
  cellCodes: '',
};

const mockUseParams = { slug: ['england', 'greater-london', 'london', 'london-euston.html'] };

jest.mock('next/navigation', () => ({
  useRouter: jest.fn(() => ({
    push: jest.fn(),
    refresh: jest.fn(),
  })),
  usePathname: jest.fn(),
  useSearchParams: () => {
    return mockSearchParams;
  },
  useParams: () => {
    return mockUseParams;
  },
}));

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    getInitials: serverUtils.getInitials,
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    formatIBAssetsUrl: () => {
      return '/';
    },
    RolesRequired: serverUtils.RolesRequired,
    cn: jest.fn(),
    getQueryParams: jest.fn(),
    getMultiSearchParamsIB: () => {
      return mockGetMultiSearchParamsIB;
    },
    staticHotelInformationIB: () => {
      return mockStaticHotelInformation;
    },
    useTranslation: mockUseTranslation,
    updateSearchParamsIfError: () => ({ shouldUpdate: false }),
    validateRoomOccupancyConditions: () => true,
  };
});

const mockProps = {
  searchIcon: '/',
  handleEditClick: () => {
    return;
  },
  language: 'en',
};

describe('Edit Search component', () => {
  beforeEach(() => {
    mockProps.handleEditClick = () => {
      return;
    };
  });
  it('should render Edit search component', async () => {
    mockProps.handleEditClick = undefined;
    const { getByTestId } = await render(<EditSearch {...mockProps} />);
    expect(getByTestId('Edit-Search-IB')).toBeInTheDocument();
  });

  it('should click Edit button', async () => {
    const { getByTestId } = await render(<EditSearch {...mockProps} />);
    expect(getByTestId('Edit-Search-IB')).toBeInTheDocument();

    const editButton = getByTestId('Edit-Search-Button');

    await act(() => {
      userEvent.click(editButton);
    });
  });

  it('should click Edit button with default value', async () => {
    mockProps.handleEditClick = undefined;
    const { getByTestId } = await render(<EditSearch {...mockProps} />);
    expect(getByTestId('Edit-Search-IB')).toBeInTheDocument();

    const editButton = getByTestId('Edit-Search-Button');

    await act(() => {
      userEvent.click(editButton);
    });
  });
});
describe('parseRooms logic', () => {
  function testParseRooms(rooms) {
    let totalAdults = 0;
    let totalChildren = 0;
    const roomsCount = {} as Record<string, number>;

    rooms?.map((room) => {
      totalAdults += room.adults;
      totalChildren += room.children;

      if (room.roomType !== undefined) {
        if (roomsCount[room.roomType]) {
          roomsCount[room.roomType] += 1;
        } else {
          roomsCount[room.roomType] = 1;
        }
      }
    });

    return { totalAdults, totalChildren, roomsCount };
  }

  it('should count adults, children and room types correctly for mixed roomTypes', () => {
    const rooms = [
      { adults: 2, children: 1, roomType: 'DB' },
      { adults: 1, children: 0, roomType: 'FAM' },
      { adults: 1, children: 2, roomType: 'DB' }, // triggers the if branch
      { adults: 1, children: 0, roomType: undefined }, // should be ignored
    ];

    const result = testParseRooms(rooms);

    expect(result.totalAdults).toBe(5); // 2 + 1 + 1 + 1
    expect(result.totalChildren).toBe(3); // 1 + 0 + 2
    expect(result.roomsCount).toEqual({ DB: 2, FAM: 1 });
  });

  it('should handle only one roomType', () => {
    const rooms = [{ adults: 1, children: 0, roomType: 'SINGLE' }];

    const result = testParseRooms(rooms);

    expect(result.totalAdults).toBe(1);
    expect(result.totalChildren).toBe(0);
    expect(result.roomsCount).toEqual({ SINGLE: 1 });
  });

  it('should ignore rooms with undefined roomType', () => {
    const rooms = [
      { adults: 1, children: 0, roomType: undefined },
      { adults: 2, children: 1, roomType: undefined },
    ];

    const result = testParseRooms(rooms);

    expect(result.totalAdults).toBe(3);
    expect(result.totalChildren).toBe(1);
    expect(result.roomsCount).toEqual({});
  });
  it('should increment roomType count for duplicate roomType', () => {
    const rooms = [
      { adults: 1, children: 0, roomType: 'DB' },
      { adults: 2, children: 1, roomType: 'DB' }, // triggers increment
    ];

    const result = testParseRooms(rooms);

    expect(result.roomsCount['DB']).toBe(2); // checks increment
  });
});
