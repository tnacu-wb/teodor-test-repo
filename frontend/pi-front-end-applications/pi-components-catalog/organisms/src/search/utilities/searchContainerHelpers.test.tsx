import type { SearchRequestParamsType, StayDetailsStateLocalStorageType } from '@whitbread-eos/api';
import { DEFAULT_MULTI_SEARCH_ROOM } from '@whitbread-eos/api';
import type { Locale } from 'date-fns';

import {
  appendPromoIdToUrl,
  formatSummaryDateRange,
  getNotificationMarginTop,
  getRedirectWithDefaultsURL,
  getSearchParams,
  mapRoomsForAvailabilityQuery,
  mapSearchParamsForURL,
  setSearchLocationInLocalStorage,
} from './searchContainerHelpers';

const mockQueryParams = {
  searchTerm: 'London Eye, London, UK',
  ARRdd: 27,
  ARRmm: 10,
  ARRyyyy: 2022,
  nights: 2,
  roomsNumber: 1,
  rooms: [
    {
      id: 'U6v8wmyaXH1T007qYLbiS',
      adults: 1,
      children: 0,
      shouldIncludeCot: false,
      roomType: 'Double',
    },
  ],
  placeId: 'ChIJc2nSALkEdkgRkuoJJBfzkUI',
} as SearchRequestParamsType | undefined;

const mockStayDetailsState = {
  data: {
    info: {
      suggestion: {
        searchTerm: 'London Eye, London, UK',
        location: {
          longitude: '',
          latitude: '',
        },
        placeId: 'ChIJc2nSALkEdkgRkuoJJBfzkUI',
        hotelId: '',
      },
    },
  },
} as StayDetailsStateLocalStorageType;

const setInLocalStorageFuncResult = {
  suggestion: {
    brand: undefined,
    code: undefined,
    searchTerm: 'London Eye, London, UK',
    location: {
      longitude: '',
      latitude: '',
    },
    placeId: 'ChIJc2nSALkEdkgRkuoJJBfzkUI',
    hotelId: '',
  },
};

const mockSearchParams = {
  searchTerm: 'London Bridge, London, UK',
  ARRdd: 27,
  ARRmm: 10,
  ARRyyyy: 2022,
  nights: 1,
  roomsNumber: 1,
  rooms: [
    {
      id: 'z9xpwDhAb6QwyYmoRk9b4',
      adults: 1,
      children: 0,
      shouldIncludeCot: false,
      roomType: 'Double',
    },
  ],
  placeId: 'ChIJxRO7WVEDdkgRrGM1fCYoHqY',
} as SearchRequestParamsType;

const mockRoomInputResult = {
  ADULT1: 1,
  CHILD1: 0,
  COT1: 0,
};

const mmapSearchParamsForURLResult = {
  ...mockSearchParams,
  ...mockRoomInputResult,
};

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const mockRedirectPathReturnedValue =
  '/en/search.html?ARRdd=20&ARRmm=4&ARRyyyy=2022&NIGHTS=1&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB&searchModel.searchTerm=London Eye, London, UK&PLACEID=ChIJc2nSALkEdkgRkuoJJBfzkUI&BOOKINGCHANNEL=WEB&SORT=1';

const mockTranslationDateLanguage = {
  locale: {
    code: 'en-GB',
    formatLong: {},
    localize: {},
    match: {},
    options: {
      weekStartsOn: 1,
      firstWeekContainsDate: 4,
    },
  } as Locale,
};
const dateFormat = 'dd MMM';
const startDate = new Date('2022-10-28T21:00:00.000Z');
const endDate = new Date('2022-10-29T21:00:00.000Z');

const mockRoomsInput = [
  {
    adults: 1,
    children: 0,
    shouldIncludeCot: false,
    roomType: 'Double',
    id: 'B6axJl9k6uc91lOw8CIG1',
  },
];

const mockRoomLabels = [
  {
    Accessible: 'DIS',
    Double: 'DB',
    Family: 'FAM',
    Single: 'SB',
    Twin: 'TWIN',
  },
] as { [key: string]: string }[];

const mappedRoomLabels = Object.assign({}, ...mockRoomLabels);

const mockMappedRoomsFunResult = [
  {
    adultsNumber: 1,
    childrenNumber: 0,
    roomType: 'DB',
    cotRequired: false,
  },
];

const mockSearchQuery = {
  ADULT1: '1',
  ARRdd: '28',
  ARRmm: '7',
  ARRyyyy: '2023',
  BOOKINGCHANNEL: 'WEB',
  CELLCODES: 'EMP01',
  CHILD1: '0',
  COT1: '0',
  FILTERS: '',
  INTTYP1: 'DB',
  NIGHTS: '1',
  PLACEID: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
  ROOMS: '1',
  SORT: '1',
  VIEW: '2',
  'searchModel.searchTerm': 'London, UK',
};

const mockSearchQueryWithCompId = {
  ADULT1: '1',
  ARRdd: '28',
  ARRmm: '7',
  ARRyyyy: '2023',
  BOOKINGCHANNEL: 'WEB',
  CELLCODES: 'EMP01',
  CHILD1: '0',
  COT1: '0',
  FILTERS: '',
  INTTYP1: 'DB',
  NIGHTS: '1',
  PLACEID: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
  ROOMS: '1',
  SORT: '1',
  VIEW: '2',
  'searchModel.searchTerm': 'London, UK',
  CORPID: 'corpid',
  COMPID: 'compid',
};

const mockSearchQueryResult = {
  arrivalDay: 28,
  arrivalMonth: 7,
  arrivalYear: 2023,
  bookingChannel: 'WEB',
  cellCodes: ['EMP01'],
  code: undefined,
  coordinates: undefined,
  filters: '',
  location: 'London, UK',
  numberOfNights: 1,
  placeId: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
  rooms: [
    {
      adultsNumber: 1,
      childrenNumber: 0,
      type: 'DB',
    },
  ],
  sort: 'DISTANCE',
};

const mockSearchQueryWithCompIdResult = {
  arrivalDay: 28,
  arrivalMonth: 7,
  arrivalYear: 2023,
  bookingChannel: 'WEB',
  cellCodes: ['EMP01'],
  code: undefined,
  coordinates: undefined,
  filters: '',
  location: 'London, UK',
  numberOfNights: 1,
  placeId: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
  rooms: [
    {
      adultsNumber: 1,
      childrenNumber: 0,
      type: 'DB',
    },
  ],
  sort: 'DISTANCE',
  corpId: 'corpid',
  compId: 'compid',
};

jest.mock('./searchContainerHelpers', () => ({
  ...jest.requireActual('./searchContainerHelpers'),
  formatSummaryDateRange: jest.fn().mockReturnValue('29 Oct - 30 Oct'),
}));

describe('Helper functions tests', () => {
  beforeAll(() => {
    jest.clearAllMocks();
  });
  it('call setSearchLocationInLocalStorage and match the result', () => {
    const result = setSearchLocationInLocalStorage(mockQueryParams, mockStayDetailsState);
    expect(result).toEqual(setInLocalStorageFuncResult);
  });
  it('call mapSearchParamsForURL and match the result', () => {
    const result = mapSearchParamsForURL(mockSearchParams);
    expect(result).toEqual(mmapSearchParamsForURLResult);
  });
  it('call mapRoomsForAvailabilityQuery and match the result', () => {
    const result = mapRoomsForAvailabilityQuery(mockRoomsInput, mappedRoomLabels);
    expect(result).toEqual(mockMappedRoomsFunResult);
  });
  it('call getRedirectWithDefaultsURL and match the result', () => {
    const mockRouter = {
      pathname: '',
      asPath:
        '/en/search.html?searchModel.searchTerm=London%20Eye,%20London,%20UK&PLACEID=ChIJc2nSALkEdkgRkuoJJBfzkUI&ARRdd=27&ARRmm=10&ARRyyyy=2022&NIGHTS=14&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB&BOOKINGCHANNEL=WEB&SORT=1',
      replace: jest.fn(),
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);

    const router: any = mockRouter;
    const result = getRedirectWithDefaultsURL(router, 20, 4, 2022);
    expect(result).toBe(mockRedirectPathReturnedValue);
  });
  it('call getNotificationMarginTop and match the result', () => {
    let result = '';
    result = getNotificationMarginTop(true, true, 'mobile');
    expect(result).toBe('md');

    result = getNotificationMarginTop(false, true, 'mobile');
    expect(result).toBe('-11rem');

    result = getNotificationMarginTop(false, false, 'mobile');
    expect(result).toBe('-7rem');
  });
  it('call formatSummaryDateRange and match the results', () => {
    const result = formatSummaryDateRange(
      dateFormat,
      startDate,
      endDate,
      mockTranslationDateLanguage
    );
    expect(formatSummaryDateRange).toBeCalledTimes(1);
    expect(result).toBe('29 Oct - 30 Oct');
  });

  it('call getSearchParams and match the result', () => {
    const result = getSearchParams(mockSearchQuery);
    expect(result).toEqual(mockSearchQueryResult);
  });

  it('call getSearchParams with no SORT set, should return default', () => {
    mockSearchQuery.SORT = '';
    const result = getSearchParams(mockSearchQuery);
    expect(result.sort).toEqual('DISTANCE');
  });

  it('call getSearchParams with flag enabled, should return default', () => {
    mockSearchQuery.SORT = '1';
    const result = getSearchParams(mockSearchQuery, false, {
      isPiSortOrderDropdownEnabled: true,
      isBbSortOrderDropdownEnabled: false,
    });
    expect(result.sort).toEqual('RECOMMENDATION');
  });

  it('call getSearchParams with flag enabled, should return default', () => {
    mockSearchQuery.SORT = '1';
    const result = getSearchParams(mockSearchQuery, false, {
      isPiSortOrderDropdownEnabled: false,
      isBbSortOrderDropdownEnabled: true,
    });
    expect(result.sort).toEqual('DISTANCE');
  });

  it('call getSearchParams with no ROOMS set, should return default rooms', () => {
    mockSearchQuery.ROOMS = '';
    const result = getSearchParams(mockSearchQuery);
    expect(result.rooms).toEqual([DEFAULT_MULTI_SEARCH_ROOM]);
  });

  it('call getSearchParams with companyId and match the result', () => {
    const result = getSearchParams(mockSearchQueryWithCompId);
    expect(result).toEqual(mockSearchQueryWithCompIdResult);
  });

  it('should update arrival date when isMetaArrivalDayKeywordFlagEnabled is true', () => {
    const query = {
      ARRdd: 'tomorrow',
      ARRmm: '',
      ARRyyyy: '',
      NIGHTS: '3',
      ROOMS: '1',
      ADULT1: '2',
      CHILD1: '1',
      INTTYP1: 'DB',
    };

    const mockCurrentDate = new Date('2023-10-01T00:00:00.000Z');
    jest.useFakeTimers().setSystemTime(mockCurrentDate);
    const result = getSearchParams(query, true);

    expect(result.arrivalDay).toBe(2);
    expect(result.arrivalMonth).toBe(10);
    expect(result.arrivalYear).toBe(2023);

    jest.useRealTimers();
  });

  it('should not update arrival date when isMetaArrivalDayKeywordFlagEnabled is false', () => {
    const query = {
      ARRdd: 'tomorrow',
      ARRmm: '',
      ARRyyyy: '',
      NIGHTS: '3',
      ROOMS: '1',
      ADULT1: '2',
      CHILD1: '1',
      INTTYP1: 'DB',
    };

    const mockCurrentDate = new Date('2023-10-01T00:00:00.000Z');
    jest.useFakeTimers().setSystemTime(mockCurrentDate);
    const result = getSearchParams(query, false);

    expect(result.arrivalDay).toBe(null);
    expect(result.arrivalMonth).toBe(null);
    expect(result.arrivalYear).toBe(null);

    jest.useRealTimers();
  });
});

describe('appendPromoIdToUrl', () => {
  it('should append PROMOID to the URL when promoId is provided', () => {
    const baseUrl = '/en/search.html?searchModel.searchTerm=London';
    const promoId = 'ABC123';
    const result = appendPromoIdToUrl(baseUrl, promoId);
    expect(result).toBe('/en/search.html?searchModel.searchTerm=London&PROMOID=ABC123');
  });

  it('should return the same URL when promoId is undefined', () => {
    const baseUrl = '/en/search.html?searchModel.searchTerm=London';
    const result = appendPromoIdToUrl(baseUrl);
    expect(result).toBe(baseUrl);
  });

  it('should return the same URL when promoId is an empty string', () => {
    const baseUrl = '/en/search.html?searchModel.searchTerm=London';
    const promoId = '';
    const result = appendPromoIdToUrl(baseUrl, promoId);
    expect(result).toBe(baseUrl);
  });

  it('should correctly append PROMOID when baseUrl has existing query parameters', () => {
    const baseUrl = '/en/search.html?searchModel.searchTerm=London&ROOMS=1';
    const promoId = 'XYZ789';
    const result = appendPromoIdToUrl(baseUrl, promoId);
    expect(result).toBe('/en/search.html?searchModel.searchTerm=London&ROOMS=1&PROMOID=XYZ789');
  });

  it('should handle baseUrl without existing query params gracefully', () => {
    const baseUrl = '/en/search.html';
    const promoId = 'PROMO2025';
    const result = appendPromoIdToUrl(baseUrl, promoId);
    expect(result).toBe('/en/search.html&PROMOID=PROMO2025');
  });
});
