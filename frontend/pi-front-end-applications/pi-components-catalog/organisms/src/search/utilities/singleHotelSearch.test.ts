import '@testing-library/jest-dom';
import { add, format } from 'date-fns';

import {
  isOperaHotelAvailable,
  isHotelOpeningSoon,
  getHotelSlugById,
  mapMultiSearchRoomsToSearchRooms,
  mapMultiSearchToQueryParams,
  getSearchRedirectURL,
  isHotelAvailable,
  singleHotelSearchCCUI,
  singleHotelSearchPI,
} from './singleHotelSearch';

const mockOpeningDate = format(add(new Date(), { days: 5 }), 'yyyy-MM-dd');
const mockHotelSlug = '/england/greater-london/london/london-euston';
const mockCcuiBookingChannel = 'CCUI';
const mockedRooms = [
  {
    adults: 2,
    children: 0,
    roomType: 'double',
    shouldIncludeCot: false,
  },
  {
    adults: 1,
    children: 2,
    roomType: 'single',
    shouldIncludeCot: false,
  },
];
const mockMultiSearchParams = {
  arrivalDay: new Date().getDate(),
  arrivalMonth: new Date().getMonth(),
  arrivalYear: new Date().getFullYear(),
  location: 'London Euston',
  numberOfNights: 5,
  rooms: [
    {
      adultsNumber: 2,
      childrenNumber: 0,
      type: 'DB',
    },
    {
      adultsNumber: 1,
      childrenNumber: 2,
      type: 'SB',
    },
  ],
  code: 'LONEUS',
  bookingChannel: 'WEB',
  sort: 'DISTANCE',
};
const mockSearchHotelAvailabilityQueryParams = {
  code: 'LONEUS',
  ARRdd: new Date().getDate(),
  ARRmm: new Date().getMonth() + 1,
  ARRyyyy: new Date().getFullYear(),
  nights: 5,
  rooms: mockedRooms,
};
const mockSearchHotelAvailabilityQueryParamsLocation = {
  placeId: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
  ARRdd: new Date().getDate(),
  ARRmm: new Date().getMonth(),
  ARRyyyy: new Date().getFullYear(),
  nights: 5,
  rooms: mockedRooms,
  searchTerm: 'London, UK',
  roomsNumber: mockedRooms.length,
};
const mockMappedRoomLabels = {
  double: 'DB',
  single: 'SB',
};
const mockHotelAvailabilityResponse = {
  startDate: format(new Date(), 'yyyy-MM-dd'),
  endDate: format(add(new Date(), { days: 5 }), 'yyyy-MM-dd'),
  available: true,
  roomRates: ['FLEXRATE'],
  mlos: false,
};
const mockParamsMappedForURL = {
  ADULT1: 1,
  ARRdd: new Date().getDate(),
  ARRmm: new Date().getMonth(),
  ARRyyyy: new Date().getFullYear(),
  CHILD1: 0,
  COT1: 0,
  brand: 'PI',
  searchTerm: 'London, UK',
  nights: 5,
  rooms: mockedRooms,
  roomsNumber: mockedRooms.length,
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  axiosRequest: jest.fn().mockImplementation(() => ({
    available: true,
  })),
  graphQLRequest: jest.fn().mockImplementation(() => ({
    hotelInformation: {
      hotelOpeningDate: mockOpeningDate,
      links: {
        detailsPage: mockHotelSlug,
      },
    },
    hotelAvailability: mockHotelAvailabilityResponse,
  })),
}));

const mockSearchRedirectURLParams = {
  isAvailable: false,
  isOpeningSoon: false,
  redirectToSrpLink: 'search.html',
  queryParams: { code: 'LONEUS' },
  country: 'gb',
  language: 'en',
  URLToRedirect: 'URL_REDIRECT',
};
const mockLabels = {
  mappedRoomLabels: [mockMappedRoomLabels],
  bookingChannel: 'WEB',
};
const expectedHotelAvailableResponseOpera = {
  isOpeningSoon: true,
  isAvailable: true,
  hasMlos: false,
};

const defaultExpectedHotelAvailableResponse = {
  isOpeningSoon: false,
  isAvailable: true,
  hasMlos: false,
};

const mockHotelId = 'LONEUS';
const mockCountry = 'gb';
const mockLanguage = 'en';
const URLToRedirect = `ARRdd=${mockParamsMappedForURL.ARRdd}&ARRmm=${mockParamsMappedForURL.ARRmm}&ARRyyyy=${mockParamsMappedForURL.ARRyyyy}&NIGHTS=${mockParamsMappedForURL.nights}&ROOMS=${mockParamsMappedForURL.roomsNumber}`;

const mockGetHotelSlugById = jest.fn().mockResolvedValue(mockHotelSlug);

describe('singleHotelSearchCCUI', () => {
  it('should redirect to SRP if the Opera hotel is unavailable', async () => {
    const stayDetailsState = {
      data: {
        info: {
          suggestion: {
            location: {
              latitude: '22',
              longitude: '22',
            },
            placeId: '',
            hotelId: 'LONEUS',
          },
        },
      },
    };

    const mockParamsMappedForURL = {
      ADULT1: 1,
      ARRdd: new Date().getDate(),
      ARRmm: new Date().getMonth(),
      ARRyyyy: new Date().getFullYear(),
      CHILD1: 0,
      COT1: 0,
      brand: 'PI',
      searchTerm: 'London Euston',
      nights: 5,
      rooms: mockedRooms,
      roomsNumber: mockedRooms.length,
    };

    const result = await singleHotelSearchCCUI(
      stayDetailsState,
      mockParamsMappedForURL,
      mockLabels,
      URLToRedirect,
      {
        ...mockSearchHotelAvailabilityQueryParams,
        searchTerm: 'London Euston',
        roomsNumber: mockedRooms.length,
      },
      mockCountry,
      mockLanguage,
      mockCcuiBookingChannel
    );

    expect(result).toBe(
      `/gb/en/hotels/england/greater-london/london/london-euston.html?ARRdd=${String(
        mockParamsMappedForURL.ARRdd
      ).padStart(2, '0')}&ARRmm=${String(mockParamsMappedForURL.ARRmm).padStart(2, '0')}&ARRyyyy=${
        mockParamsMappedForURL.ARRyyyy
      }&NIGHTS=${mockParamsMappedForURL.nights}&ROOMS=${
        mockParamsMappedForURL.roomsNumber
      }&BRAND=PI`
    );
  });
  it('should redirect to SRP with the placeId if there is no hotelId provided', async () => {
    const stayDetailsState = {
      data: {
        info: {
          suggestion: {
            location: {
              latitude: '',
              longitude: '',
            },
            placeId: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
            hotelId: '',
          },
        },
      },
    };

    const result = await singleHotelSearchCCUI(
      stayDetailsState,
      mockParamsMappedForURL,
      mockLabels,
      URLToRedirect,
      mockSearchHotelAvailabilityQueryParamsLocation,
      mockCountry,
      mockLanguage,
      mockCcuiBookingChannel
    );

    expect(result).toBe(
      `/gb/en/search.html?searchModel.searchTerm=London, UK&PLACEID=${stayDetailsState.data.info.suggestion.placeId}&${URLToRedirect}&BOOKINGCHANNEL=WEB&SORT=1&VIEW=2`
    );
  });
});

describe('singleHotelSearchPI', () => {
  it('should redirect to HDP if the Opera hotel is available', async () => {
    const stayDetailsState = {
      data: {
        info: {
          suggestion: {
            location: {
              latitude: '22',
              longitude: '22',
            },
            placeId: '',
            hotelId: 'LONEUS',
          },
        },
      },
    };

    const mockParamsMappedForURL = {
      ADULT1: 1,
      ARRdd: new Date().getDate(),
      ARRmm: new Date().getMonth(),
      ARRyyyy: new Date().getFullYear(),
      CHILD1: 0,
      COT1: 0,
      brand: 'PI',
      searchTerm: 'London Euston',
      nights: 5,
      rooms: mockedRooms,
      roomsNumber: mockedRooms.length,
    };

    const result = await singleHotelSearchPI(
      stayDetailsState,
      mockParamsMappedForURL,
      mockLabels,
      URLToRedirect,
      {
        ...mockSearchHotelAvailabilityQueryParams,
        searchTerm: 'London Euston',
        roomsNumber: mockedRooms.length,
      },
      mockCountry,
      mockLanguage,
      mockCcuiBookingChannel
    );

    expect(result).toBe(
      `/gb/en/hotels/england/greater-london/london/london-euston.html?ARRdd=${String(
        mockParamsMappedForURL.ARRdd
      ).padStart(2, '0')}&ARRmm=${String(mockParamsMappedForURL.ARRmm).padStart(2, '0')}&ARRyyyy=${
        mockParamsMappedForURL.ARRyyyy
      }&NIGHTS=${mockParamsMappedForURL.nights}&ROOMS=${mockParamsMappedForURL.roomsNumber}`
    );
  });
  it('should redirect to SRP with the placeId if there is no hotelId provided', async () => {
    const stayDetailsState = {
      data: {
        info: {
          suggestion: {
            location: {
              latitude: '',
              longitude: '',
            },
            placeId: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
            hotelId: '',
          },
        },
      },
    };

    const result = await singleHotelSearchPI(
      stayDetailsState,
      mockParamsMappedForURL,
      mockLabels,
      URLToRedirect,
      mockSearchHotelAvailabilityQueryParamsLocation,
      mockCountry,
      mockLanguage,
      mockCcuiBookingChannel
    );

    expect(result).toBe(
      `/gb/en/search.html?searchModel.searchTerm=London, UK&PLACEID=${stayDetailsState.data.info.suggestion.placeId}&${URLToRedirect}&BOOKINGCHANNEL=WEB&SORT=1&VIEW=2`
    );
  });
});

describe('isHotelAvailable', () => {
  it('should return the fields for an Opera hotel', async () => {
    const result = await isHotelAvailable(
      mockSearchHotelAvailabilityQueryParams,
      mockCountry,
      mockLanguage,
      mockMappedRoomLabels,
      mockCcuiBookingChannel
    );

    expect(result).toEqual(expectedHotelAvailableResponseOpera);
  });

  it('should return the default values fields if no hotelId', async () => {
    const result = await isHotelAvailable(
      {
        ARRdd: new Date().getDate(),
        ARRmm: new Date().getMonth(),
        ARRyyyy: new Date().getFullYear(),
        nights: 5,
        rooms: mockedRooms,
      },
      mockCountry,
      mockLanguage,
      mockMappedRoomLabels,
      'PI'
    );
    expect(result).toEqual(defaultExpectedHotelAvailableResponse);
  });
});

describe('getSearchRedirectURL', () => {
  it('should return the redirectToSrpLink when isAvailable is false', async () => {
    const result = await getSearchRedirectURL(mockSearchRedirectURLParams);

    expect(result).toEqual('search.html');
    expect(mockGetHotelSlugById).not.toHaveBeenCalled();
  });

  it('should return the redirectToSrpLink when isOpeningSoon is true', async () => {
    const result = await getSearchRedirectURL(mockSearchRedirectURLParams);

    expect(result).toEqual('search.html');
    expect(mockGetHotelSlugById).not.toHaveBeenCalled();
  });

  it('should return the correct URL when hotelSlug is found', async () => {
    const result = await getSearchRedirectURL({
      ...mockSearchRedirectURLParams,
      isAvailable: true,
    });

    expect(result).toEqual(`/gb/en/hotels${mockHotelSlug}.html?URL_REDIRECT`);
  });
});

describe('isOperaHotelAvailable', () => {
  it('fetches hotel availability and maps response correctly', async () => {
    const result = await isOperaHotelAvailable(
      mockSearchHotelAvailabilityQueryParams,
      mockLanguage,
      mockCountry,
      mockMappedRoomLabels,
      'PI'
    );

    expect(result?.isHotelAvailable).toEqual(true);
  });
});

describe('isHotelOpeningSoon', () => {
  it('should return the hotel opening date', async () => {
    const mockArrivalDate = {
      ARRdd: new Date().getDate(),
      ARRmm: new Date().getMonth() + 1,
      ARRyyyy: new Date().getFullYear(),
    };

    const result = await isHotelOpeningSoon(
      { ...mockArrivalDate },
      mockHotelId,
      mockCountry,
      mockLanguage
    );

    expect(result).toEqual(true);
  });
});

describe('getHotelSlugById', () => {
  it('should return the hotel slug for a hotel', async () => {
    const result = await getHotelSlugById(mockHotelId, mockCountry, mockLanguage);

    expect(result).toEqual(mockHotelSlug);
  });
});

describe('mapMultiSearchToQueryParams', () => {
  it('maps SRMultiSearchParamsType to query params', () => {
    const expectedResult = {
      ARRdd: new Date().getDate(),
      ARRmm: new Date().getMonth(),
      ARRyyyy: new Date().getFullYear(),
      searchTerm: 'London Euston',
      nights: 5,
      rooms: [
        {
          adults: 2,
          children: 0,
          roomType: 'DB',
          shouldIncludeCot: false,
        },
        {
          adults: 1,
          children: 2,
          roomType: 'SB',
          shouldIncludeCot: false,
        },
      ],
      roomsNumber: 2,
      code: 'LONEUS',
    };
    expect(mapMultiSearchToQueryParams(mockMultiSearchParams)).toEqual(expectedResult);
  });
});

describe('mapMultiSearchRoomsToSearchRooms', () => {
  it('should map Room to SearchRooms', () => {
    const rooms = [
      {
        adultsNumber: 2,
        childrenNumber: 0,
        type: 'DB',
      },
      {
        adultsNumber: 1,
        childrenNumber: 2,
        type: 'FAM',
      },
    ];
    const expectedResult = [
      {
        adults: 2,
        children: 0,
        roomType: 'DB',
        shouldIncludeCot: false,
      },
      {
        adults: 1,
        children: 2,
        roomType: 'FAM',
        shouldIncludeCot: false,
      },
    ];
    expect(mapMultiSearchRoomsToSearchRooms(rooms)).toEqual(expectedResult);
  });
});
