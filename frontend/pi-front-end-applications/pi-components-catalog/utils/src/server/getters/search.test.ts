import {
  Area,
  BUSINESS_BOOKER_USER_ROLES,
  ROOM_CODES,
  StandardRoomType,
  URLParams,
} from '@whitbread-eos/api';
import { add, endOfDay, format } from 'date-fns';
import { cookies } from 'next/headers';

import { MAX_ROOMS_SEARCH_LIMIT } from '../../global-constants';
import {
  getMultiSearchParamsIB,
  getQueryParams,
  getSearchParams,
  getSearchRedirectLink,
  mapSearchParamsForURL,
  roomOccupancyParamsForURL,
  updateSearchParamsIfError,
} from './search';

const mockMaxRooms = 9;
const mockMaxNights = 14;

const mockFetchResponse = {
  data: {},
};

const mockOkStatus = { value: true };

global.fetch = jest.fn(() =>
  Promise.resolve({
    json: () => Promise.resolve(mockFetchResponse),
    ok: mockOkStatus.value,
  })
);

const mockUseRouter = jest.fn();
jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useRouter: () => mockUseRouter(),
}));

const mockSuperRole = BUSINESS_BOOKER_USER_ROLES.SUPER;
const mockToken = {
  email: 'test@test.com',
  companyId: 'test',
  business: {
    accessLevel: mockSuperRole,
    tethered: false,
  },
};
const mockCookieData = {
  value: mockToken,
};
const mockCookieStore = {
  get: () => mockCookieData,
} as unknown as ReturnType<typeof cookies>;
let mockHeadersWbUrl: string | null = 'http://test.com?a=1';

const mockParamsGetQueryParams = {
  selectedDate: {
    from: new Date(),
    to: add(new Date(), { days: 1 }),
  },
  location: {
    suggestion: 'London, UK',
    placeId: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
    code: '',
    brand: '',
    geometry: {},
  },
  rooms: [
    {
      roomOne: {
        adults: 1,
        children: 0,
        shouldIncludeCot: false,
        roomType: StandardRoomType.DB,
      },
    },
  ],
};

const today = new Date();

const mockResultgetQueryParams = {
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

const mockGetMultiSearchParamsIB = {
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
const resultGetMultiSearchParamsIB = {
  arrival: '2024-09-25',
  departure: '2024-09-26',
  numberOfUnits: 1,
  numberOfNights: 1,
  rooms: [
    {
      adults: 1,
      children: 0,
      roomType: ROOM_CODES.double,
      shouldIncludeCot: false,
    },
  ],
  cellCodes: '',
};

jest.mock('next/headers', () => ({
  cookies: () => mockCookieStore,
  headers: () => ({ get: () => mockHeadersWbUrl }),
}));

jest.mock('nanoid', () => ({
  nanoid: () => 'id',
}));

jest.mock('../../utils/decodeIdToken', () => (token: string) => token);

jest.mock('../../utils/unleash', () => ({
  getUnleashTogglesServerOrClient: jest.fn(),
}));

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useRouter: () => jest.fn(),
}));
describe('Server search getters', () => {
  const originalWindow = global.window;

  beforeEach(() => {
    // Remove window to simulate server-side environment
    // @ts-expect-error - intentionally deleting window for test
    delete global.window;
    mockHeadersWbUrl = 'http://test.com?a=1';
  });

  afterEach(() => {
    global.window = originalWindow;
  });

  it('should call getSearchParams', async () => {
    const result = await getSearchParams();

    expect(result).toBeDefined();
    expect(result.get('a')).toEqual('1');
  });

  it('should call getSearchParams with null url', async () => {
    mockHeadersWbUrl = null;
    const result = await getSearchParams();
    // Returns empty URLSearchParams when url is null
    expect(result.toString()).toEqual('');
  });

  it('should call getQueryParams with date, location and rooms', async () => {
    const resultQueryParams = getQueryParams(
      mockParamsGetQueryParams.selectedDate,
      mockParamsGetQueryParams.location,
      mockParamsGetQueryParams.rooms as any
    );
    expect(resultQueryParams).toEqual(mockResultgetQueryParams);
  });
  it('should call getQueryParams with location undefined', async () => {
    const resultQueryParams = getQueryParams(
      mockParamsGetQueryParams.selectedDate,
      undefined,
      mockParamsGetQueryParams.rooms as any
    );
    expect(resultQueryParams).toEqual(undefined);
  });
  it('should call getQueryParams without selectedDate', async () => {
    const resultQueryParams = getQueryParams(
      undefined,
      mockParamsGetQueryParams.location,
      mockParamsGetQueryParams.rooms as any
    );
    expect(resultQueryParams).toEqual(mockResultgetQueryParams);
  });
});

describe('getSearchRedirectLink function', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  it('should call getSearchRedirectLink Function', () => {
    const {
      hotelCode,
      hotelHasAvailability,
      hotelIsOpeningSoon,
      hotelSlug,
      language,
      country,
      URLToRedirect,
      paramsMappedForURL,
      queryParams,
    } = {
      hotelCode: 'LONEUS',
      hotelHasAvailability: true,
      hotelIsOpeningSoon: false,
      hotelSlug: '',
      language: 'en',
      country: 'gb',
      URLToRedirect: '',
      paramsMappedForURL: {
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
        brand: Area.PI,
        ADULT1: 1,
        CHILD1: 0,
        COT1: 0,
      },
      queryParams: mockResultgetQueryParams,
    };
    expect(
      getSearchRedirectLink(
        hotelCode,
        hotelHasAvailability,
        hotelIsOpeningSoon,
        hotelSlug,
        language,
        country,
        URLToRedirect,
        paramsMappedForURL,
        queryParams as any
      )
    ).toEqual(undefined);
  });
});

describe('mapSearchParamsForUrl function', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  it('should map room data correctly', () => {
    const mockedRooms = [{ adults: 2, children: 1, shouldIncludeCot: true, roomType: 'Double' }];
    const params = {
      searchTerm: '',
      ARRdd: undefined,
      ARRmm: undefined,
      ARRyyyy: undefined,
      nights: 1,
      roomsNumber: 1,
      rooms: mockedRooms,
    };
    expect(mapSearchParamsForURL(params)).toEqual({
      rooms: mockedRooms,
      searchTerm: '',
      ARRdd: undefined,
      ARRmm: undefined,
      ARRyyyy: undefined,
      nights: 1,
      roomsNumber: 1,
      ADULT1: 2,
      CHILD1: 1,
      COT1: 1,
    });
  });
});

describe('roomOccupancyParamsForURL function', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  it('should return false an empty string if params.rooms is empty', () => {
    const params = {
      searchTerm: '',
      ARRdd: undefined,
      ARRmm: undefined,
      ARRyyyy: undefined,
      nights: 1,
      roomsNumber: 1,
      rooms: [],
    };
    expect(roomOccupancyParamsForURL(params)).toBe('');
  });
  it('should generate correct URL params', () => {
    const params = {
      searchTerm: '',
      ARRdd: undefined,
      ARRmm: undefined,
      ARRyyyy: undefined,
      nights: 1,
      roomsNumber: 1,
      rooms: [{ roomType: ROOM_CODES.double, adults: 2, children: 0, shouldIncludeCot: true }],
    };
    const expected = 'ADULT1=2&CHILD1=0&COT1=1&INTTYP1=DB';
    expect(roomOccupancyParamsForURL(params)).toBe(expected);
  });
});

describe('getMultiSearchParamsIB', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  it('should call getMultiSearchParamsIB with params ', () => {
    expect(getMultiSearchParamsIB(mockGetMultiSearchParamsIB)).toEqual(
      resultGetMultiSearchParamsIB
    );
  });

  it('should cap rooms and numberOfUnits when ROOMS is an absurdly large crafted value', () => {
    const crafted = {
      ARRdd: '25',
      ARRmm: '09',
      ARRyyyy: '2024',
      NIGHTS: '1',
      ROOMS: '833406678',
      ADULT1: '1',
      CHILD1: '0',
      COT1: '0',
      INTTYP1: 'DB',
    };

    const result = getMultiSearchParamsIB(crafted) as {
      numberOfUnits: number;
      rooms: unknown[];
    };

    expect(result.rooms).toHaveLength(MAX_ROOMS_SEARCH_LIMIT);
    expect(result.numberOfUnits).toBe(MAX_ROOMS_SEARCH_LIMIT);
  });
  it('should call getMultiSearchParamsIB without params ', () => {
    expect(getMultiSearchParamsIB(undefined)).toEqual({});
  });
  it('should call getMultiSearchParamsIB and return default parameters ', () => {
    mockGetMultiSearchParamsIB.ARRdd = '';
    expect(getMultiSearchParamsIB(mockGetMultiSearchParamsIB)).toEqual({
      arrival: format(endOfDay(new Date()), 'yyyy-MM-dd'),
      departure: format(
        endOfDay(add(new Date(), { days: Number(mockGetMultiSearchParamsIB.NIGHTS) || 1 })),
        'yyyy-MM-dd'
      ),
      numberOfNights: 1,
      rooms: [
        {
          adults: 1,
          children: 0,
          roomType: ROOM_CODES.double,
          shouldIncludeCot: false,
        },
      ],
    });
  });

  it('should call getMultiSearchParamsIB without some of params ', () => {
    mockGetMultiSearchParamsIB.ARRdd = '';
    mockGetMultiSearchParamsIB.ARRyyyy = '';
    mockGetMultiSearchParamsIB.ARRmm = '';
    expect(getMultiSearchParamsIB(mockGetMultiSearchParamsIB)).toEqual({
      arrival: undefined,
      departure: undefined,
      rooms: [],
    });
  });
});

describe('updateSearchParamsIfError', () => {
  const currentDay = today.getDate().toString();
  const currentMonth = (today.getMonth() + 1).toString();
  const currentYear = today.getFullYear().toString();

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should update URL with default nights if nights are not valid', () => {
    const searchParams = new URLSearchParams();
    searchParams.set(URLParams.nights, 'asdzxc');
    const result = updateSearchParamsIfError(
      searchParams,
      BUSINESS_BOOKER_USER_ROLES.SUPER,
      mockMaxRooms,
      mockMaxNights
    );

    expect(result.shouldUpdate).toBe(true);
    expect(result.url).toBe('/?NIGHTS=1');
  });

  it('should update URL with default nights if nights are more than 14', () => {
    const searchParams = new URLSearchParams();
    searchParams.set(URLParams.nights, '15');
    const result = updateSearchParamsIfError(
      searchParams,
      BUSINESS_BOOKER_USER_ROLES.SUPER,
      mockMaxRooms,
      mockMaxNights
    );

    expect(result.shouldUpdate).toBe(true);
    expect(result.url).toBe('/?NIGHTS=1');
  });

  it('should update URL with default nights if nights are < 1', () => {
    const searchParams = new URLSearchParams();
    searchParams.set(URLParams.nights, '-2');
    const result = updateSearchParamsIfError(
      searchParams,
      BUSINESS_BOOKER_USER_ROLES.SUPER,
      mockMaxRooms,
      mockMaxNights
    );

    expect(result.shouldUpdate).toBe(true);
    expect(result.url).toBe('/?NIGHTS=1');
  });

  it('should update URL with default day, month, year if day is not valid', () => {
    const searchParams = new URLSearchParams();
    searchParams.set(URLParams.day, 'asdzxc');
    searchParams.set(URLParams.month, 'asdzxc');
    searchParams.set(URLParams.year, 'asdzxc');
    const result = updateSearchParamsIfError(
      searchParams,
      BUSINESS_BOOKER_USER_ROLES.SUPER,
      mockMaxRooms,
      mockMaxNights
    );

    expect(result.shouldUpdate).toBe(true);
    expect(result.url).toBe(
      `/?ARRdd=${currentDay}&ARRmm=${currentMonth}&ARRyyyy=${currentYear}&NIGHTS=1`
    );
  });

  it('should update URL with default day, month, year if date is not valid', () => {
    const searchParams = new URLSearchParams();
    searchParams.set(URLParams.day, 'asdzxc');
    searchParams.set(URLParams.month, currentMonth);
    searchParams.set(URLParams.year, currentYear);
    const result = updateSearchParamsIfError(
      searchParams,
      BUSINESS_BOOKER_USER_ROLES.SUPER,
      mockMaxRooms,
      mockMaxNights
    );

    expect(result.shouldUpdate).toBe(true);
    expect(result.url).toBe(
      `/?ARRdd=${currentDay}&ARRmm=${currentMonth}&ARRyyyy=${currentYear}&NIGHTS=1`
    );
  });

  it('should update URL with default if month is not valid', () => {
    const searchParams = new URLSearchParams();
    searchParams.set(URLParams.day, currentDay);
    searchParams.set(URLParams.month, '14');
    searchParams.set(URLParams.year, currentYear);
    const result = updateSearchParamsIfError(
      searchParams,
      BUSINESS_BOOKER_USER_ROLES.SUPER,
      mockMaxRooms,
      mockMaxNights
    );

    expect(result.shouldUpdate).toBe(true);
    expect(result.url).toBe(
      `/?ARRdd=${currentDay}&ARRmm=${currentMonth}&ARRyyyy=${currentYear}&NIGHTS=1`
    );
  });
  it('should update URL with default if day is not valid', () => {
    const searchParams = new URLSearchParams();
    searchParams.set(URLParams.day, '33');
    searchParams.set(URLParams.month, currentMonth);
    searchParams.set(URLParams.year, currentYear);
    const result = updateSearchParamsIfError(
      searchParams,
      BUSINESS_BOOKER_USER_ROLES.SUPER,
      mockMaxRooms,
      mockMaxNights
    );

    expect(result.shouldUpdate).toBe(true);
    expect(result.url).toBe(
      `/?ARRdd=${currentDay}&ARRmm=${currentMonth}&ARRyyyy=${currentYear}&NIGHTS=1`
    );
  });
  it('should update URL with default rooms if rooms are not valid', () => {
    const searchParams = new URLSearchParams();
    searchParams.set(URLParams.rooms, 'asdzxc');
    const result = updateSearchParamsIfError(
      searchParams,
      BUSINESS_BOOKER_USER_ROLES.SUPER,
      mockMaxRooms,
      mockMaxNights
    );

    expect(result.shouldUpdate).toBe(true);
    expect(result.url).toBe('/?ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB');
  });
  it('should update URL with default rooms if rooms are < 1 ', () => {
    const searchParams = new URLSearchParams();
    searchParams.set(URLParams.rooms, '0');
    const result = updateSearchParamsIfError(
      searchParams,
      BUSINESS_BOOKER_USER_ROLES.SUPER,
      mockMaxRooms,
      mockMaxNights
    );

    expect(result.shouldUpdate).toBe(true);
    expect(result.url).toBe('/?ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB');
  });
  it('should update URL with default rooms if rooms are > 5 ', () => {
    const searchParams = new URLSearchParams();
    searchParams.set(URLParams.rooms, '5');
    const result = updateSearchParamsIfError(
      searchParams,
      BUSINESS_BOOKER_USER_ROLES.SUPER,
      mockMaxRooms,
      mockMaxNights
    );

    expect(result.shouldUpdate).toBe(true);
    expect(result.url).toBe('/?ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB');
  });
  it('should update URL with default rooms if rooms are > 1 and role is SELF', () => {
    const searchParams = new URLSearchParams();
    searchParams.set(URLParams.rooms, '2');
    const result = updateSearchParamsIfError(
      searchParams,
      BUSINESS_BOOKER_USER_ROLES.SELF,
      mockMaxRooms,
      mockMaxNights
    );

    expect(result.shouldUpdate).toBe(true);
    expect(result.url).toBe('/?ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB');
  });
  it('should reset to default when missing parameters for additional rooms', () => {
    const searchParams = new URLSearchParams(
      'ROOMS=3&ADULT1=2&CHILD1=1&COT1=0&INTTYP1=DB&ADULT2=1&CHILD2=0&COT2=0&INTTYP2=DB'
    );
    const result = updateSearchParamsIfError(
      searchParams,
      BUSINESS_BOOKER_USER_ROLES.SUPER,
      mockMaxRooms,
      mockMaxNights
    );

    expect(result.shouldUpdate).toBe(true);
    expect(result.url).toBe('/?ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB');
  });
});
