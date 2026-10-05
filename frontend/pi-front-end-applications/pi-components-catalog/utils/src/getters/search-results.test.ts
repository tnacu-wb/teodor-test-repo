import {
  SEARCH_RESULTS_PAGE_SIZE_REDIS_TTL,
  HotelBrand,
  SingleHotelAvailability,
  MULTI_HOTEL_AVAILABILITIES_QUERY_PI_V2,
  MULTI_HOTEL_AVAILABILITIES_QUERY,
} from '@whitbread-eos/api';
import { getCookie } from 'cookies-next';
import { NextRouter } from 'next/router';

import { MAX_ROOMS_SEARCH_LIMIT } from '../global-constants';
import { setCookieWithDefaultDomain } from '../helpers/cookies';
import { graphQLRequest } from '../hooks';
import {
  getPlace,
  getDefaultRooms,
  getSearchResultsPageSize,
  readPromotionsInformation,
  shouldDisplayPromoBanner,
  getBrandFromSnowDropSuggestions,
  type PromotionsInformation,
  getHotelBrandFromSearchResults,
  extractMultiHotelAvailabilities,
  getPromoId,
  extractHotelAvailabilitiesResult,
  getNewSearchResultsPI,
  getNewSearchResultsBB,
  getNewSearchResultsCCUI,
  HOTEL_AVAILABILITIES_QUERY_KEY,
  HOTEL_AVAILABILITIES_QUERY_KEY_PI,
  setPromoInfoState,
} from './search-results';

let mockMultiSearchParams;

jest.mock('../hooks', () => ({
  graphQLRequest: jest.fn(),
}));

const mockGraphQLRequest = graphQLRequest as jest.Mock;

const mockParamsForQuery: any = {
  startDate: '2023-11-25',
  endDate: '2023-11-29',
  rooms: [{ adults: 2, children: 0 }],
  place: { location: 'London', locationFormat: 'PLACEID' },
  oldWorldChannel: 'WEB',
  channel: 'PI',
  subChannel: 'WEB',
  page: 1,
  initialPageSize: 10,
  lazyLoadPageSize: 20,
  country: 'gb',
  language: 'en',
  sort: 'DISTANCE',
  filters: '',
  ratePlanCodes: ['FLEX'],
  companyId: undefined,
  sortOption: {
    rcPriceModifier: 1,
    rcDistanceModifier: 2,
    rcHubModifier: 3,
  },
};

const mockResult = {
  multiHotelAvailabilities: [{ hotelId: 'H1' }],
  total: 1,
  promotionsInformation: {
    showPromo: true,
  },
};

const hotels = [
  {
    hotelId: 'CHECRO',
    name: 'Cheltenham North West',
    hotelAvailability: {
      available: true,
      lowestRoomRate: { currencyCode: 'GBP', netTotal: 180 },
      distance: 25.47,
      unit: 'MILES',
      limitedAvailability: false,
      pmsSource: 'OPERA',
      cellCode: undefined,
      numberOfRoomsAvailable: 211,
    },
    hotelInformation: {
      brand: 'PID',
      hotelOpeningDate: '',
      coordinates: { latitude: 51.91844, longitude: -2.11272 },
      messagingFlag: { color: '', text: '' },
      thumbnailImages: [],
      hotelFacilities: [],
      links: { detailsPage: '/england/gloucestershire/cheltenham/cheltenham-north-west' },
    },
  },
];

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('cookies-next', () => ({
  getCookie: jest.fn(),
}));

jest.mock('../helpers/cookies', () => ({
  getCookie: jest.fn(),
  setCookieWithDefaultDomain: jest.fn(),
}));

jest.mock('./auth', () => ({
  ...jest.requireActual('next/router'),
  getAuthCookie: () => jest.fn(),
}));

const mockGetItem = jest.fn();
const mockSetItem = jest.fn();
const mockCleanup = jest.fn();
const mockFetchQuery = jest.fn();

// Mock the RedisStorageServer module
jest.mock('../storage/RedisStorageServer', () => ({
  ...jest.requireActual('../storage/RedisStorageServer'),
  RedisStorageServer: {
    getInstance: jest.fn(() => ({
      getItem: mockGetItem,
      setItem: mockSetItem,
      cleanup: mockCleanup,
    })),
  },
}));

const suggessions = {
  properties: [
    {
      code: 'LONSOH',
      brand: 'HUB',
      suggestion: 'hub London Soho',
      geometry: { type: 'Point', coordinates: [-0.136549, 51.513614] },
    },
    {
      code: 'LONCRE',
      brand: 'PI',
      suggestion: 'Derry / Londonderry',
      geometry: { type: 'Point', coordinates: [-7.278844, 54.992376] },
    },
  ],
  managedPlaces: [],
  places: [
    { suggestion: 'London, UK', placeId: 'ChIJdd4hrwug2EcRmSrV3Vo6llI' },
    { suggestion: 'UKVCAS, Gee Street, London, UK', placeId: 'ChIJsaiyegAbdkgRaPIGVQxyEs0' },
  ],
};

jest.mock('@whitbread-eos/api', () => ({
  ...jest.requireActual('@whitbread-eos/api'),
  getSuggestions: jest.fn().mockResolvedValue(suggessions),
}));

const mockedDefaultRooms = [
  {
    adults: 1,
    children: 0,
    roomType: 'DB',
    shouldIncludeCot: false,
    shouldBeAccessible: false,
  },
];

const mockedDefaultTwoRooms = [
  {
    adults: 1,
    children: 0,
    roomType: 'DB',
    shouldIncludeCot: false,
    shouldBeAccessible: false,
  },
  {
    adults: 1,
    children: 0,
    roomType: 'DB',
    shouldIncludeCot: false,
    shouldBeAccessible: false,
  },
];

describe('getPlace Method', () => {
  beforeEach(() => {
    mockMultiSearchParams = {
      arrivalDay: 15,
      arrivalMonth: 5,
      arrivalYear: 2023,
      location: 'Manchester',
      numberOfNights: 2,
      rooms: [],
      placeId: 'manchester_id',
      coordinates: `57'26''lat, 23'92''long`,
      bookingChannel: 'PI',
      sort: 'ASC',
      code: '',
      filters: '',
    };
  });

  it('should return place coordinates for english language and placeId has a truthy value', () => {
    const place = getPlace(mockMultiSearchParams, 'en');
    const expectedOutput = {
      location: 'manchester_id',
      locationFormat: 'PLACEID',
      radius: 30,
      radiusUnit: 'MILES',
    };
    expect(place).toEqual(expectedOutput);
  });

  it('should return place coordinates for english language and placeId has a falsy value', () => {
    mockMultiSearchParams.placeId = '';
    const place = getPlace(mockMultiSearchParams, 'en');
    const expectedOutput = {
      location: `57'26''lat, 23'92''long`,
      locationFormat: 'LATLONG',
      radius: 30,
      radiusUnit: 'MILES',
    };
    expect(place).toEqual(expectedOutput);
  });

  it('should return place coordinates for german language and placeId has a truthy value', () => {
    const place = getPlace(mockMultiSearchParams, 'de');
    const expectedOutput = {
      location: 'manchester_id',
      locationFormat: 'PLACEID',
      radius: 50,
      radiusUnit: 'KILOMETERS',
    };
    expect(place).toEqual(expectedOutput);
  });

  it('should return place coordinates for german language and placeId has a falsy value', () => {
    mockMultiSearchParams.placeId = '';
    const place = getPlace(mockMultiSearchParams, 'de');
    const expectedOutput = {
      location: `57'26''lat, 23'92''long`,
      locationFormat: 'LATLONG',
      radius: 50,
      radiusUnit: 'KILOMETERS',
    };
    expect(place).toEqual(expectedOutput);
  });

  it('should return default english place coordinates if language second parameter is undefined', () => {
    const place = getPlace(mockMultiSearchParams, undefined);
    const expectedOutput = {
      location: 'manchester_id',
      locationFormat: 'PLACEID',
      radius: 50,
      radiusUnit: 'MILES',
    };
    expect(place).toEqual(expectedOutput);
  });
});

describe('getDefaultRooms', () => {
  it('should call function getDefaultRooms', () => {
    const mockRouter = {
      language: 'gb',
      country: 'gb',
      PLACEID: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
      ARRdd: '1',
      ARRmm: '9',
      ARRyyyy: '2023',
      NIGHTS: '1',
      ROOMS: '1',
      ADULT1: '1',
      CHILD1: '0',
      COT1: '0',
      INTTYP1: 'DB',
      BOOKINGCHANNEL: 'WEB',
      SORT: '1',
      VIEW: '2',
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    const result = getDefaultRooms(mockRouter as any);
    expect(result).toEqual(mockedDefaultRooms);
  });
  it('should call function getDefaultRooms with 2 ROOMS', () => {
    const mockRouter = {
      language: 'gb',
      country: 'gb',
      PLACEID: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
      ARRdd: '1',
      ARRmm: '9',
      ARRyyyy: '2023',
      NIGHTS: '1',
      ROOMS: '2',
      ADULT1: '1',
      CHILD1: '0',
      COT1: '0',
      INTTYP1: 'DB',
      ADULT2: '1',
      CHILD2: '0',
      COT2: '0',
      INTTYP2: 'DB',
      BOOKINGCHANNEL: 'WEB',
      SORT: '1',
      VIEW: '2',
    };
    (mockUseRouter as jest.Mock).mockReturnValue(mockRouter);
    const result = getDefaultRooms(mockRouter as any);
    expect(result).toEqual(mockedDefaultTwoRooms);
  });

  it('should cap the loop instead of hanging when ROOMS is an absurdly large crafted value', () => {
    const mockRouter = {
      ARRdd: '1',
      ARRmm: '9',
      ARRyyyy: '2023',
      NIGHTS: '1',
      ROOMS: '833406678',
      ADULT1: '3',
      CHILD1: '0',
      COT1: '0',
      INTTYP1: '712228345',
    };

    const result = getDefaultRooms(mockRouter as any);
    expect(result).toHaveLength(MAX_ROOMS_SEARCH_LIMIT);
  });
});

describe('getSearchResultsPageSize', () => {
  const language = 'en';
  const country = 'gb';
  const cacheKey = `fe::srp:page-size:${country}-${language}`;
  let queryClient;

  beforeEach(() => {
    jest.clearAllMocks();
    queryClient = { fetchQuery: mockFetchQuery };
  });

  it('returns cached data if present', async () => {
    const cachedValue = JSON.stringify({ initialPageSize: 10, lazyLoadPageSize: 5 });
    mockGetItem.mockResolvedValueOnce(cachedValue);

    const result = await getSearchResultsPageSize(language, country, queryClient);

    expect(result).toEqual({ initialPageSize: 10, lazyLoadPageSize: 5 });
    expect(mockFetchQuery).not.toHaveBeenCalled();
    expect(mockSetItem).not.toHaveBeenCalled();
  });

  it('fetches data and caches it if not present', async () => {
    mockGetItem.mockResolvedValueOnce(null);
    const searchInformationData = {
      searchInformation: {
        config: {
          api: {
            initialPageSize: 20,
            lazyLoadPageSize: 8,
          },
        },
      },
    };
    mockFetchQuery.mockResolvedValueOnce(searchInformationData);

    const result = await getSearchResultsPageSize(language, country, queryClient);

    expect(mockSetItem).toHaveBeenCalledWith(
      cacheKey,
      JSON.stringify({ initialPageSize: 20, lazyLoadPageSize: 8 }),
      SEARCH_RESULTS_PAGE_SIZE_REDIS_TTL
    );
    expect(result).toEqual({ initialPageSize: 20, lazyLoadPageSize: 8 });
  });

  it('fetches data and caches it if exception occurs when getting item', async () => {
    mockGetItem.mockRejectedValueOnce(new Error('Redis getItem error'));
    const searchInformationData = {
      searchInformation: {
        config: {
          api: {
            initialPageSize: 20,
            lazyLoadPageSize: 8,
          },
        },
      },
    };
    mockFetchQuery.mockResolvedValueOnce(searchInformationData);

    const result = await getSearchResultsPageSize(language, country, queryClient);

    expect(mockSetItem).toHaveBeenCalledWith(
      cacheKey,
      JSON.stringify({ initialPageSize: 20, lazyLoadPageSize: 8 }),
      SEARCH_RESULTS_PAGE_SIZE_REDIS_TTL
    );
    expect(result).toEqual({ initialPageSize: 20, lazyLoadPageSize: 8 });
  });

  it('returns null if fetchQuery throws', async () => {
    mockGetItem.mockResolvedValueOnce(null);
    mockFetchQuery.mockRejectedValueOnce(new Error('Query error'));

    const result = await getSearchResultsPageSize(language, country, queryClient);

    expect(result).toBeNull();
  });
});

describe('readPromotionsInformation', () => {
  const promoData: PromotionsInformation = {
    showPromo: true,
    isWithinPromoWindow: true,
    promoBannerColour: 'blue',
    promoBannerIcon: '/icon.svg',
    promoBannerTitle: 'Promo Title',
    promoBannerSubtitle: 'Promo Subtitle',
    promoInvalidMessage: null,
    promoExpiredMessage: null,
    promotionCode: 'CODE10',
    landingPage: '/promo',
  };

  it('returns null when input is undefined', () => {
    expect(readPromotionsInformation(undefined)).toBeNull();
  });

  it('returns null when dehydratedState is missing', () => {
    expect(readPromotionsInformation({})).toBeNull();
  });

  it('returns null when dehydratedState.queries is not an array', () => {
    expect(readPromotionsInformation({ dehydratedState: { queries: {} } })).toBeNull();
  });

  it('returns null when no matching query is found', () => {
    const input = {
      dehydratedState: {
        queries: [{ queryKey: ['otherQuery'], state: { data: {} } }],
      },
    };

    expect(readPromotionsInformation(input)).toBeNull();
  });

  it('returns null when promotionsInformation query has no state.data', () => {
    const input = {
      dehydratedState: {
        queries: [{ queryKey: ['promotionsInformation'], state: {} }],
      },
    };

    expect(readPromotionsInformation(input)).toBeNull();
  });

  it('returns promotionsInformation from hotelAvailability query (preferred path)', () => {
    const input = {
      dehydratedState: {
        queries: [
          {
            queryKey: ['hotelAvailability'],
            state: {
              data: {
                hotelAvailability: {
                  promotionsInformation: promoData,
                },
              },
            },
          },
        ],
      },
    };

    expect(readPromotionsInformation(input)).toEqual(promoData);
  });

  it('returns promotionsInformation from hotelAvailability when queryKey contains hotelAvailability', () => {
    const input = {
      dehydratedState: {
        queries: [
          {
            queryKey: ['some', 'hotelAvailability', 'other'],
            state: {
              data: {
                hotelAvailability: {
                  promotionsInformation: promoData,
                },
              },
            },
          },
        ],
      },
    };

    expect(readPromotionsInformation(input)).toEqual(promoData);
  });

  it('falls back to promotionsInformation query when hotelAvailability query is missing', () => {
    const input = {
      dehydratedState: {
        queries: [
          {
            queryKey: ['promotionsInformation'],
            state: {
              data: {
                promotionsInformation: promoData,
              },
            },
          },
        ],
      },
    };

    expect(readPromotionsInformation(input)).toEqual(promoData);
  });

  it('falls back to promotionsInformation query when hotelAvailability exists without promotionsInformation', () => {
    const input = {
      dehydratedState: {
        queries: [
          {
            queryKey: ['hotelAvailability'],
            state: {
              data: {
                hotelAvailability: {},
              },
            },
          },
          {
            queryKey: ['promotionsInformation'],
            state: {
              data: {
                promotionsInformation: promoData,
              },
            },
          },
        ],
      },
    };

    expect(readPromotionsInformation(input)).toEqual(promoData);
  });

  it('works when queryKey is an array containing promotionsInformation', () => {
    const input = {
      dehydratedState: {
        queries: [
          {
            queryKey: ['some', 'promotionsInformation', 'other'],
            state: {
              data: {
                promotionsInformation: promoData,
              },
            },
          },
        ],
      },
    };

    expect(readPromotionsInformation(input)).toEqual(promoData);
  });

  it('returns null when hotelAvailability query exists but promotionsInformation is missing and no fallback exists', () => {
    const input = {
      dehydratedState: {
        queries: [
          {
            queryKey: ['hotelAvailability'],
            state: {
              data: {
                hotelAvailability: {},
              },
            },
          },
        ],
      },
    };

    expect(readPromotionsInformation(input)).toBeNull();
  });
});

describe('shouldDisplayPromoBanner', () => {
  const mockRouter = (query: Record<string, any> = {}): NextRouter =>
    ({ query }) as unknown as NextRouter;

  beforeEach(() => {
    jest.clearAllMocks();
  });

  // ...same test cases as before, but using getCookie instead of cookieUtils.getCookie
  it('returns true for breakfastPromo when cookie exists and no landing page promo', () => {
    (getCookie as jest.Mock).mockReturnValue('PROMO123');

    const result = shouldDisplayPromoBanner(
      'breakfastPromo',
      { showPromo: false } as any,
      { enabled: false } as any,
      mockRouter({})
    );

    expect(result).toBe(true);
  });

  it('returns false for unknown type', () => {
    (getCookie as jest.Mock).mockReturnValue('');

    const result = shouldDisplayPromoBanner(
      'randomPromo',
      { showPromo: false } as any,
      { enabled: true } as any,
      mockRouter({})
    );

    expect(result).toBe(false);
  });

  it('returns false for summerPromo when promo disabled', () => {
    (getCookie as jest.Mock).mockReturnValue('');

    const result = shouldDisplayPromoBanner(
      'summerPromo',
      { showPromo: false } as any,
      { enabled: false } as any,
      mockRouter({})
    );

    expect(result).toBe(false);
  });

  it('returns true for summerPromo when no cookie, no landing promo, no sitewide promo, and enabled', () => {
    (getCookie as jest.Mock).mockReturnValue('');

    const result = shouldDisplayPromoBanner(
      'summerPromo',
      { showPromo: false } as any,
      { enabled: true } as any,
      mockRouter({})
    );

    expect(result).toBe(true);
  });

  it('does not clear cookie when hasDocument is false (SSR)', () => {
    // @ts-expect-error simulate SSR
    delete global.document;
    (getCookie as jest.Mock).mockReturnValue('PROMO123');

    shouldDisplayPromoBanner(
      'summerPromo',
      { showPromo: false } as any,
      { enabled: true } as any,
      mockRouter({ PROMOID: 'X' })
    );

    expect(setCookieWithDefaultDomain).not.toHaveBeenCalled();
  });

  it('clears cookie when landingPagePromo exists and hasDocument is true', () => {
    (getCookie as jest.Mock).mockReturnValue('PROMO123');

    shouldDisplayPromoBanner(
      'summerPromo',
      { showPromo: false } as any,
      { enabled: true } as any,
      mockRouter({ PROMOID: 'X' })
    );
  });

  it('returns false for breakfastPromo when cookie exists and no landing page promo', () => {
    (getCookie as jest.Mock).mockReturnValue('PROMO123');

    const result = shouldDisplayPromoBanner(
      'breakfastPromo',
      { showPromo: false } as any,
      { enabled: false } as any,
      mockRouter({})
    );

    expect(result).toBe(false);
  });
});

describe('getBrandFromSnowDropSuggestions', () => {
  it('returns initial brand from properties if present', async () => {
    const response = {
      properties: [{ brand: HotelBrand.PID }],
      managedPlaces: [],
      places: [],
    };

    const brand = await getBrandFromSnowDropSuggestions(response);
    expect(brand).toBe(HotelBrand.PID);
  });

  it('returns PID immediately if any suggestion ends with "Germany"', async () => {
    const response = {
      properties: [],
      managedPlaces: [],
      places: [
        { suggestion: 'Neuhausen-Hamberg, Germany', placeId: '1' },
        { suggestion: 'Some Other Place, UK', placeId: '2' },
      ],
    };

    const brand = await getBrandFromSnowDropSuggestions(response);
    expect(brand).toBe(HotelBrand.PID);
  });

  it('returns PI if none of the suggestions end with "Germany"', async () => {
    const response = {
      properties: [],
      managedPlaces: [],
      places: [
        { suggestion: 'Yorkshire Wildlife Park, UK', placeId: '1' },
        { suggestion: 'Doncaster, UK', placeId: '2' },
      ],
    };

    const brand = await getBrandFromSnowDropSuggestions(response);
    expect(brand).toBe(HotelBrand.PI);
  });

  it('returns null if suggestions array is empty', async () => {
    const response = { properties: [], managedPlaces: [], places: [] };
    const brand = await getBrandFromSnowDropSuggestions(response);
    expect(brand).toBeNull();
  });

  it('returns null if input is null or undefined', async () => {
    expect(await getBrandFromSnowDropSuggestions(null as any)).toBeNull();
    expect(await getBrandFromSnowDropSuggestions(undefined as any)).toBeNull();
  });

  it('skips suggestions with undefined or empty suggestion text', async () => {
    const response = {
      properties: [],
      managedPlaces: [],
      places: [
        { suggestion: '', placeId: '1' },
        { suggestion: undefined as any, placeId: '2' },
        { suggestion: 'Some Place, Germany', placeId: '3' },
      ],
    };
    const brand = await getBrandFromSnowDropSuggestions(response);
    expect(brand).toBe(HotelBrand.PID);
  });
});

describe('getHotelBrandFromSearchResults', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });

  test('returns PID when first available hotel brand is not PID', () => {
    const result = getHotelBrandFromSearchResults(hotels);

    expect(result).toBe(HotelBrand.PID);
  });

  test('returns PI when first available hotel brand is PI', () => {
    hotels[0].hotelInformation.brand = HotelBrand.PI;
    const result = getHotelBrandFromSearchResults(hotels);

    expect(result).toBe(HotelBrand.PI);
  });

  test('returns PI when first available hotel brand is HUB', () => {
    hotels[0].hotelInformation.brand = HotelBrand.HUB;
    const result = getHotelBrandFromSearchResults(hotels);

    expect(result).toBe('HUB');
  });

  test('returns null when first available hotel brand is empty', () => {
    hotels[0].hotelInformation.brand = '';
    const result = getHotelBrandFromSearchResults(hotels);

    expect(result).toBe(null);
  });

  test('returns null when hotels array is empty', () => {
    const hotels = [] as SingleHotelAvailability[];
    const result = getHotelBrandFromSearchResults(hotels);

    expect(result).toBe(null);
  });
  test('returns null when hotels is null', () => {
    const result = getHotelBrandFromSearchResults(null);

    expect(result).toBe(null);
  });

  test('returns null when hotels is undefined', () => {
    const result = getHotelBrandFromSearchResults(undefined);

    expect(result).toBe(null);
  });

  test('returns null when no hotel is available', () => {
    const hotelsWithNoAvailability = [
      {
        ...hotels[0],
        hotelAvailability: { available: false },
      },
    ] as SingleHotelAvailability[];

    const result = getHotelBrandFromSearchResults(hotelsWithNoAvailability);

    expect(result).toBe(null);
  });

  test('returns null when first available hotel has no hotelInformation', () => {
    const hotelsWithoutHotelInformation = [
      {
        ...hotels[0],
        hotelInformation: undefined,
      },
    ] as SingleHotelAvailability[];

    const result = getHotelBrandFromSearchResults(hotelsWithoutHotelInformation);

    expect(result).toBe(null);
  });

  test('returns null when first available hotel brand is undefined', () => {
    const hotelsWithUndefinedBrand = [
      {
        ...hotels[0],
        hotelInformation: { brand: undefined },
      },
    ] as SingleHotelAvailability[];

    const result = getHotelBrandFromSearchResults(hotelsWithUndefinedBrand);

    expect(result).toBe(null);
  });

  test('uses first available hotel when multiple hotels exist', () => {
    const multipleHotels = [
      {
        ...hotels[0],
        hotelAvailability: { available: false },
      },
      {
        ...hotels[0],
        hotelAvailability: { available: true },
        hotelInformation: { brand: HotelBrand.PI },
      },
    ] as SingleHotelAvailability[];

    const result = getHotelBrandFromSearchResults(multipleHotels);

    expect(result).toBe(HotelBrand.PI);
  });
});

describe('extractMultiHotelAvailabilities', () => {
  test('returns empty array when queries is not an array', () => {
    const input: any = { dehydratedState: { queries: null } };
    expect(extractMultiHotelAvailabilities(input)).toEqual([]);
  });

  test('returns V2 multiHotelAvailabilities when present', () => {
    const mockData = [{ id: 1 }, { id: 2 }];

    const input = {
      dehydratedState: {
        queries: [
          {
            queryKey: ['hotelAvailabilitiesV2'],
            state: {
              data: {
                hotelAvailabilitiesV2: {
                  multiHotelAvailabilities: mockData,
                },
              },
            },
          },
        ],
      },
    };

    expect(extractMultiHotelAvailabilities(input)).toEqual(mockData);
  });

  test('returns V1 multiHotelAvailabilities when V2 not present', () => {
    const mockData = [{ id: 10 }];

    const input = {
      dehydratedState: {
        queries: [
          {
            queryKey: ['hotelAvailabilities'],
            state: {
              data: {
                hotelAvailabilities: {
                  multiHotelAvailabilities: mockData,
                },
              },
            },
          },
        ],
      },
    };

    expect(extractMultiHotelAvailabilities(input)).toEqual(mockData);
  });

  test('returns empty array when neither V1 nor V2 multiHotelAvailabilities exist', () => {
    const input = {
      dehydratedState: {
        queries: [
          {
            queryKey: ['hotelAvailabilities'],
            state: { data: {} },
          },
        ],
      },
    };

    expect(extractMultiHotelAvailabilities(input)).toEqual([]);
  });

  test('ignores mismatched queryKey values', () => {
    const input = {
      dehydratedState: {
        queries: [
          {
            queryKey: ['someOtherQuery'],
            state: {
              data: {
                hotelAvailabilitiesV2: { multiHotelAvailabilities: [{ id: 99 }] },
              },
            },
          },
        ],
      },
    };

    // Should not match, so returns empty
    expect(extractMultiHotelAvailabilities(input)).toEqual([]);
  });

  test('works when queryKey contains multiple values including a match', () => {
    const mockData = [{ id: 7 }];

    const input = {
      dehydratedState: {
        queries: [
          {
            queryKey: ['foo', 'bar', 'multiHotelAvailabilities'],
            state: {
              data: {
                hotelAvailabilitiesV2: {
                  multiHotelAvailabilities: mockData,
                },
              },
            },
          },
        ],
      },
    };

    expect(extractMultiHotelAvailabilities(input)).toEqual(mockData);
  });
});

describe('getPromoId', () => {
  it('should return the promo id when a non-trimmed string is provided', () => {
    expect(getPromoId('SAVE20')).toBe('SAVE20');
  });

  it('should return the first element when an array is provided', () => {
    expect(getPromoId(['SAVE20', 'SAVE30'])).toBe('SAVE20');
  });

  it('should return null when undefined is provided', () => {
    expect(getPromoId(undefined)).toBeNull();
  });
});

describe('extractHotelAvailabilitiesResult', () => {
  it('should map the response correctly', () => {
    expect(extractHotelAvailabilitiesResult(mockResult as any)).toEqual({
      results: mockResult.multiHotelAvailabilities,
      total: 1,
      promotionsInformation: mockResult.promotionsInformation,
    });
  });
});

describe('getNewSearchResultsPI', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should fetch PI search results', async () => {
    const fetchQuery = jest.fn().mockResolvedValue({
      hotelAvailabilitiesV2: mockResult,
    });

    mockGraphQLRequest.mockResolvedValue({
      hotelAvailabilitiesV2: mockResult,
    });

    const queryClient = {
      fetchQuery,
    } as any;

    const result = await getNewSearchResultsPI(queryClient, mockParamsForQuery, 'PROMO10', true);

    expect(fetchQuery).toHaveBeenCalled();

    const options = fetchQuery.mock.calls[0][0];

    expect(options.queryKey).toEqual([
      HOTEL_AVAILABILITIES_QUERY_KEY_PI,
      expect.any(String),
      expect.any(String),
      mockParamsForQuery.rooms,
      mockParamsForQuery.place,
      mockParamsForQuery.oldWorldChannel,
      mockParamsForQuery.channel,
      mockParamsForQuery.subChannel,
      mockParamsForQuery.page,
      mockParamsForQuery.initialPageSize,
      mockParamsForQuery.lazyLoadPageSize,
      mockParamsForQuery.country,
      mockParamsForQuery.language,
      mockParamsForQuery.sort,
      mockParamsForQuery.filters,
      mockParamsForQuery.ratePlanCodes,
      1,
      2,
      3,
    ]);

    // Execute the queryFn to cover graphQLRequest
    await options.queryFn();

    expect(mockGraphQLRequest).toHaveBeenCalledWith(
      MULTI_HOTEL_AVAILABILITIES_QUERY_PI_V2,
      expect.objectContaining({
        ...mockParamsForQuery,
        promotionCode: 'PROMO10',
      })
    );

    expect(result).toEqual({
      results: mockResult.multiHotelAvailabilities,
      total: 1,
      promotionsInformation: mockResult.promotionsInformation,
    });
  });

  it('should not include promotionCode when promo flag is disabled', async () => {
    const fetchQuery = jest.fn().mockResolvedValue({
      hotelAvailabilitiesV2: mockResult,
    });

    mockGraphQLRequest.mockResolvedValue({
      hotelAvailabilitiesV2: mockResult,
    });

    const queryClient = { fetchQuery } as any;

    await getNewSearchResultsPI(queryClient, mockParamsForQuery, 'PROMO10', false);

    const options = fetchQuery.mock.calls[0][0];

    await options.queryFn();

    expect(mockGraphQLRequest).toHaveBeenCalledWith(
      MULTI_HOTEL_AVAILABILITIES_QUERY_PI_V2,
      expect.not.objectContaining({
        promotionCode: expect.anything(),
      })
    );
  });
});

describe('getNewSearchResultsBB', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should fetch BB search results', async () => {
    const fetchQuery = jest.fn().mockResolvedValue({
      hotelAvailabilities: mockResult,
    });

    mockGraphQLRequest.mockResolvedValue({
      hotelAvailabilities: mockResult,
    });

    const queryClient = {
      fetchQuery,
    } as any;

    const result = await getNewSearchResultsBB(queryClient, mockParamsForQuery, 'PROMO10', true);

    expect(fetchQuery).toHaveBeenCalled();

    const options = fetchQuery.mock.calls[0][0];

    expect(options.queryKey).toEqual([
      HOTEL_AVAILABILITIES_QUERY_KEY,
      expect.any(String),
      expect.any(String),
      mockParamsForQuery.rooms,
      mockParamsForQuery.place,
      mockParamsForQuery.oldWorldChannel,
      mockParamsForQuery.channel,
      mockParamsForQuery.subChannel,
      mockParamsForQuery.page,
      mockParamsForQuery.initialPageSize,
      mockParamsForQuery.lazyLoadPageSize,
      mockParamsForQuery.country,
      mockParamsForQuery.language,
      mockParamsForQuery.sort,
      mockParamsForQuery.filters,
      1,
      2,
    ]);

    await options.queryFn();

    expect(mockGraphQLRequest).toHaveBeenCalledWith(
      MULTI_HOTEL_AVAILABILITIES_QUERY,
      expect.objectContaining({
        ...mockParamsForQuery,
        idToken: expect.anything(),
        promotionCode: 'PROMO10',
      })
    );

    expect(result).toEqual({
      results: mockResult.multiHotelAvailabilities,
      total: 1,
      promotionsInformation: mockResult.promotionsInformation,
    });
  });

  it('should not include promotionCode when promo flag is disabled', async () => {
    const fetchQuery = jest.fn().mockResolvedValue({
      hotelAvailabilities: mockResult,
    });

    mockGraphQLRequest.mockResolvedValue({
      hotelAvailabilities: mockResult,
    });

    const queryClient = { fetchQuery } as any;

    await getNewSearchResultsBB(queryClient, mockParamsForQuery, 'PROMO10', false);

    const options = fetchQuery.mock.calls[0][0];

    await options.queryFn();

    expect(mockGraphQLRequest).toHaveBeenCalledWith(
      MULTI_HOTEL_AVAILABILITIES_QUERY,
      expect.not.objectContaining({
        promotionCode: expect.anything(),
      })
    );
  });
});

describe('getNewSearchResultsCCUI', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should include companyId in query key', async () => {
    const fetchQuery = jest.fn().mockResolvedValue({
      hotelAvailabilities: mockResult,
    });

    mockGraphQLRequest.mockResolvedValue({
      hotelAvailabilities: mockResult,
    });

    const queryClient = {
      fetchQuery,
    } as any;

    await getNewSearchResultsCCUI(
      queryClient,
      {
        ...mockParamsForQuery,
        companyId: 'COMPANY123',
      },
      'PROMO10',
      true
    );

    const options = fetchQuery.mock.calls[0][0];

    expect(options.queryKey).toContain('COMPANY123');

    await options.queryFn();

    expect(mockGraphQLRequest).toHaveBeenCalledWith(
      MULTI_HOTEL_AVAILABILITIES_QUERY,
      expect.not.objectContaining({
        promotionCode: expect.anything(),
      })
    );
  });

  it('should not include companyId when undefined', async () => {
    const fetchQuery = jest.fn().mockResolvedValue({
      hotelAvailabilities: mockResult,
    });

    mockGraphQLRequest.mockResolvedValue({
      hotelAvailabilities: mockResult,
    });

    const queryClient = {
      fetchQuery,
    } as any;

    await getNewSearchResultsCCUI(queryClient, mockParamsForQuery, 'PROMO10', true);

    const options = fetchQuery.mock.calls[0][0];

    expect(options.queryKey).not.toContain('COMPANY123');

    await options.queryFn();

    expect(mockGraphQLRequest).toHaveBeenCalledWith(
      MULTI_HOTEL_AVAILABILITIES_QUERY,
      expect.objectContaining({
        ...mockParamsForQuery,
        promotionCode: 'PROMO10',
      })
    );
  });

  it('should return mapped hotel availability response', async () => {
    const fetchQuery = jest.fn().mockResolvedValue({
      hotelAvailabilities: mockResult,
    });

    const queryClient = {
      fetchQuery,
    } as any;

    const result = await getNewSearchResultsCCUI(queryClient, mockParamsForQuery, 'PROMO10', true);

    expect(result).toEqual({
      results: mockResult.multiHotelAvailabilities,
      total: 1,
      promotionsInformation: mockResult.promotionsInformation,
    });
  });
});

describe('setPromoInfoState', () => {
  const promotionsInformation = {
    promotionCode: 'PROMO10',
  } as PromotionsInformation;

  it('should call setPromotionBannerData when flag is true and promotionsInformation exists', () => {
    const setPromotionBannerData = jest.fn();

    setPromoInfoState(promotionsInformation, setPromotionBannerData, true);

    expect(setPromotionBannerData).toHaveBeenCalledTimes(1);
    expect(setPromotionBannerData).toHaveBeenCalledWith(promotionsInformation);
  });

  it('should not call setPromotionBannerData when flag is false', () => {
    const setPromotionBannerData = jest.fn();

    setPromoInfoState(promotionsInformation, setPromotionBannerData, false);

    expect(setPromotionBannerData).not.toHaveBeenCalled();
  });

  it('should not call setPromotionBannerData when promotionsInformation is undefined', () => {
    const setPromotionBannerData = jest.fn();

    setPromoInfoState(undefined as unknown as PromotionsInformation, setPromotionBannerData, true);

    expect(setPromotionBannerData).not.toHaveBeenCalled();
  });

  it('should not call setPromotionBannerData when flag is true but promotionsInformation is null', () => {
    const setPromotionBannerData = jest.fn();

    setPromoInfoState(null as unknown as PromotionsInformation, setPromotionBannerData, true);

    expect(setPromotionBannerData).not.toHaveBeenCalled();
  });
});
