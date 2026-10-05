import {
  formatHDPUrl,
  getDaysInMonth,
  isMoreThan364DaysInFuture,
  validateRoomOccupancyConditions,
} from '..';
import {
  BOOKING_SUBCHANNEL,
  BUSINESS_BOOKER_USER_ROLES,
  DEFAULT_NUMBER,
  DEFAULT_SORT_VALUE,
  MAX_NUMBER_OF_MONTHS,
  ROOM_CODES,
  SearchPlaceType,
  SearchPropertyType,
  SearchQueryURLParamsType,
  SearchRequestParamsType,
  SearchRoomType,
  URLParams,
  VIEW_TYPE,
} from '@whitbread-eos/api';
import { add, differenceInDays, endOfDay, format, isBefore, startOfDay } from 'date-fns';
import { DateRange } from 'react-day-picker';

import { MAX_ROOMS_SEARCH_LIMIT } from '../../global-constants';
import { isDateValid } from '../../validators';

// Dynamic import helper for next/headers to avoid bundling in client components
const getNextHeaders = async () => {
  if (typeof window !== 'undefined') return null;
  try {
    const { headers } = await import('next/headers');
    return headers;
  } catch {
    return null;
  }
};

export const getSearchParams = async (): Promise<URLSearchParams> => {
  const headersFn = await getNextHeaders();
  if (!headersFn) {
    return new URLSearchParams();
  }
  const headersInstance = await headersFn();
  const url = headersInstance.get('WB-Url');
  if (!url) {
    return new URLSearchParams();
  }
  const { searchParams } = new URL(url);

  return searchParams;
};

export const getQueryParams = (
  selectedDate: DateRange,
  location: SearchPropertyType | SearchPlaceType | Record<string, never>,
  rooms: Record<string, SearchRoomType>
) => {
  if (!location) return;

  const start = selectedDate?.from ?? new Date();
  const end = selectedDate?.to ?? add(new Date(start), { days: 1 });

  const queryParams: SearchRequestParamsType = {
    searchTerm: location.suggestion,
    ARRdd: start.getDate(),
    ARRmm: start.getMonth() + 1,
    ARRyyyy: start.getFullYear(),
    nights: differenceInDays(end, start),
    roomsNumber: Object.keys(rooms).length,
    rooms: Object.values(rooms),
  };

  if ('placeId' in location) {
    queryParams.placeId = location.placeId;
  }

  if ('code' in location) {
    queryParams.code = location.code;
  }

  if ('geometry' in location) {
    queryParams.location = location.geometry.coordinates;
  }

  if ('brand' in location) {
    queryParams.brand = location.brand;
  }

  return queryParams;
};

export const getSearchRedirectLink = (
  hotelCode: string | undefined,
  hotelHasAvailability: boolean,
  hotelIsOpeningSoon: boolean,
  hotelSlug: string | undefined,
  language: string,
  country: string,
  URLToRedirect: string,
  paramsMappedForURL: SearchQueryURLParamsType,
  queryParams: SearchRequestParamsType
) => {
  const { listView } = VIEW_TYPE;

  const [longitude = 0, latitude = 0] = queryParams.location || [];

  const location = `${latitude},${longitude}`;
  const baseUrl = `/${country}/${language}/business-booker`;
  const urlForSrp = `search.html?searchModel.searchTerm`;

  // CASE 1: Search for hotel -> Hotel not Available -> SRP
  if (!hotelHasAvailability || hotelIsOpeningSoon) {
    return `${baseUrl}/${urlForSrp}=${paramsMappedForURL.searchTerm}&LOCATION=${location}&${URLToRedirect}&BOOKINGCHANNEL=${BOOKING_SUBCHANNEL.CBT}&SORT=${DEFAULT_SORT_VALUE}&VIEW=${listView}`;
  }
  // CASE 2: Search for hotel -> Hotel Availabile -> HDP
  if (hotelSlug) {
    return `${baseUrl}/hotels${hotelSlug}.html?${formatHDPUrl(URLToRedirect)}`;
  }
  // CASE 3: Search for Location -> SRP
  if (!hotelCode) {
    return `${baseUrl}/${urlForSrp}=${paramsMappedForURL.searchTerm}&PLACEID=${queryParams.placeId}&${URLToRedirect}&BOOKINGCHANNEL=${BOOKING_SUBCHANNEL.CBT}&SORT=${DEFAULT_SORT_VALUE}&VIEW=${listView}`;
  }
};

export function mapSearchParamsForURL(params: SearchRequestParamsType): SearchQueryURLParamsType {
  const roomsInputDataMapped = params?.rooms?.map((room, idx) => {
    return {
      [`ADULT${idx + 1}`]: room.adults,
      [`CHILD${idx + 1}`]: room.children,
      [`COT${idx + 1}`]: room.shouldIncludeCot ? 1 : 0,
    };
  });

  const roomInputDataObj = roomsInputDataMapped?.reduce(
    (prev, current) => ({
      ...prev,
      ...current,
    }),
    {}
  );

  return {
    ...params,
    ...roomInputDataObj,
  };
}

export const roomOccupancyParamsForURL = (params: SearchRequestParamsType) => {
  return params?.rooms
    ?.filter((room) => room.roomType)
    .map(
      (room, idx) =>
        `ADULT${idx + 1}=${room.adults}&CHILD${idx + 1}=${room.children}&COT${idx + 1}=${
          room.shouldIncludeCot ? 1 : 0
        }&INTTYP${idx + 1}=${room.roomType}`
    )
    .join('&');
};

export function getMultiSearchParamsIB(searchParamsObj: Record<string, string>) {
  if (!searchParamsObj) return {};

  const { ARRdd, ARRmm, ARRyyyy, NIGHTS, ROOMS, CELLCODES = '' } = searchParamsObj;
  // Set arrival and departure as undefined if the page is accessed from an external source without params
  // In this case both server-side request and page request will not be consumed
  if (!ARRdd && !ARRmm && !ARRyyyy && !isDateValid(Number(ARRdd), Number(ARRmm), Number(ARRyyyy))) {
    return {
      arrival: undefined,
      departure: undefined,
      rooms: [],
    };
  }

  //Set default search parameters if one the required params is missing
  if (
    !ARRdd ||
    !ARRmm ||
    !ARRyyyy ||
    !NIGHTS ||
    !ROOMS ||
    !isDateValid(Number(ARRdd), Number(ARRmm), Number(ARRyyyy))
  )
    return {
      arrival: format(endOfDay(new Date()), 'yyyy-MM-dd'),
      departure: format(endOfDay(add(new Date(), { days: Number(NIGHTS) || 1 })), 'yyyy-MM-dd'),
      numberOfNights: Number(NIGHTS) || 1,
      rooms: [
        {
          adults: 1,
          children: 0,
          roomType: ROOM_CODES.double,
          shouldIncludeCot: false,
        },
      ],
    };

  const arrival = new Date(+ARRyyyy, +ARRmm - 1, +ARRdd);
  const departure = add(arrival, { days: +NIGHTS });
  // Bounded so a crafted `ROOMS` value cannot drive an unbounded loop (SRE-350);
  // reused for numberOfUnits so the payload stays consistent with `rooms`.
  const roomsCount = Math.min(+ROOMS || 0, MAX_ROOMS_SEARCH_LIMIT);
  const rooms = [];
  for (let idx = 0; idx < roomsCount; idx++) {
    rooms.push({
      adults: +searchParamsObj[`ADULT${idx + 1}`],
      children: +searchParamsObj[`CHILD${idx + 1}`],
      roomType: searchParamsObj[`INTTYP${idx + 1}`],
      shouldIncludeCot: searchParamsObj[`COT${idx + 1}`] === '1',
    });
  }

  return {
    arrival: format(arrival, 'yyyy-MM-dd'),
    departure: format(departure, 'yyyy-MM-dd'),
    numberOfUnits: roomsCount,
    numberOfNights: +NIGHTS,
    rooms,
    cellCodes: String(CELLCODES),
  };
}

function validateRooms(
  searchParams: URLSearchParams,
  newSearchParams: URLSearchParams,
  rooms: number,
  maxRooms: number
) {
  let shouldUpdate = false;

  // Bounded by the product limit so a crafted `ROOMS` value cannot drive an
  // unbounded loop (SRE-350).
  for (let roomIndex = 1; roomIndex <= rooms && roomIndex <= maxRooms; roomIndex++) {
    const adultKey = `${URLParams.adult}${roomIndex}`;
    const childKey = `${URLParams.child}${roomIndex}`;
    const cotKey = `${URLParams.cot}${roomIndex}`;
    const roomTypeKey = `${URLParams.roomType}${roomIndex}`;

    const { adult, child, cot, roomType } = getSearchDetails(roomIndex, searchParams);

    const allConditions = validateRoomOccupancyConditions(adult, child, roomType, cot);

    if (adult && child && cot && roomType && allConditions) {
      const resetRoomOccupancyParams = {
        [adultKey]: '1',
        [childKey]: '0',
        [cotKey]: '0',
        [roomTypeKey]: ROOM_CODES.double,
      };

      setSearchParams(newSearchParams, resetRoomOccupancyParams);
      for (let roomIndex = 2; roomIndex < 5; roomIndex++) {
        newSearchParams.delete(`${URLParams.adult}${roomIndex}`);
        newSearchParams.delete(`${URLParams.child}${roomIndex}`);
        newSearchParams.delete(`${URLParams.cot}${roomIndex}`);
        newSearchParams.delete(`${URLParams.roomType}${roomIndex}`);
      }
      newSearchParams.set(URLParams.rooms, '1');
      shouldUpdate = true;
    }
  }

  return shouldUpdate;
}

export function updateSearchParamsIfError(
  searchParams: URLSearchParams,
  userRole: string,
  maxRooms: number,
  maxNights: number
) {
  const nights = searchParams.get(URLParams.nights);
  const day = searchParams.get(URLParams.day);
  const month = searchParams.get(URLParams.month);
  const year = searchParams.get(URLParams.year);

  const searchParamsObj: Record<string, string> = {};
  searchParams.forEach((value, key) => {
    searchParamsObj[key] = value;
  });

  const startDate = new Date(Number(year), Number(month) && Number(month) - 1, Number(day));
  const today = startOfDay(new Date());
  let shouldUpdate = false;
  const newSearchParams = new URLSearchParams(searchParams.toString());

  const daysInMonth = getDaysInMonth(Number(month), Number(year));

  // Raw parsed value so the `rooms > maxRooms` check still detects an over-limit
  // request; the loops it feeds are bounded by maxRooms so a crafted `ROOMS`
  // value cannot drive an unbounded loop (SRE-350).
  const rooms = Number(searchParamsObj['ROOMS']);

  if (resetRoomsParams(rooms, userRole, searchParams, searchParamsObj, newSearchParams, maxRooms)) {
    shouldUpdate = true;
  }

  if (validateRooms(searchParams, newSearchParams, rooms, maxRooms)) {
    shouldUpdate = true;
  }

  if (resetNightsParams(nights, newSearchParams, maxNights)) {
    shouldUpdate = true;
  }

  if (resetDateParams(year, month, day, startDate, today, daysInMonth, newSearchParams)) {
    shouldUpdate = true;
  }

  return {
    shouldUpdate,
    url: shouldUpdate ? `${window.location.pathname}?${newSearchParams.toString()}` : undefined,
  };
}

function resetDateParams(
  year: string | null,
  month: string | null,
  day: string | null,
  startDate: Date,
  today: Date,
  daysInMonth: number,
  newSearchParams: URLSearchParams
) {
  let shouldUpdate = false;

  if (
    year &&
    month &&
    day &&
    (isBefore(startDate, today) ||
      isMoreThan364DaysInFuture(startDate, today) ||
      isNaN(Number(year)) ||
      isNaN(Number(month)) ||
      isNaN(Number(day)) ||
      Number(month) < DEFAULT_NUMBER ||
      Number(month) > MAX_NUMBER_OF_MONTHS ||
      Number(day) < DEFAULT_NUMBER ||
      Number(day) > daysInMonth)
  ) {
    const resetDate = {
      [URLParams.nights]: '1',
      [URLParams.day]: today.getDate().toString(),
      [URLParams.month]: (today.getMonth() + 1).toString(),
      [URLParams.year]: today.getFullYear().toString(),
    };

    setSearchParams(newSearchParams, resetDate);
    shouldUpdate = true;
  }
  return shouldUpdate;
}

function resetRoomsParams(
  rooms: number,
  userRole: string,
  searchParams: URLSearchParams,
  searchParamsObj: Record<string, string>,
  newSearchParams: URLSearchParams,
  maxRooms: number
) {
  let shouldUpdate = false;

  const resetRoomToDefault = () => {
    const resetRoomParamsToDefault = {
      [URLParams.rooms]: '1',
      [`${URLParams.adult}1`]: '1',
      [`${URLParams.child}1`]: '0',
      [`${URLParams.cot}1`]: '0',
      [`${URLParams.roomType}1`]: ROOM_CODES.double,
    };
    setSearchParams(newSearchParams, resetRoomParamsToDefault);

    resetExtraRooms(newSearchParams, 2, maxRooms);

    shouldUpdate = true;
  };

  if (
    searchParamsObj['ROOMS'] &&
    (Number.isNaN(rooms) ||
      rooms < DEFAULT_NUMBER ||
      rooms > maxRooms ||
      (userRole === BUSINESS_BOOKER_USER_ROLES.SELF && rooms > DEFAULT_NUMBER))
  ) {
    resetRoomToDefault();
  } else if (searchParamsObj['ROOMS']) {
    // Bounded by the product limit so a crafted `ROOMS` value cannot drive an
    // unbounded loop (SRE-350).
    for (let roomIndex = 1; roomIndex <= rooms && roomIndex <= maxRooms; roomIndex++) {
      const { adult, child, cot, roomType } = getSearchDetails(roomIndex, searchParams);

      if (!adult || !child || !cot || !roomType) {
        resetRoomToDefault();
        break;
      }
    }
  }
  return shouldUpdate;
}

function resetNightsParams(
  nights: string | null,
  newSearchParams: URLSearchParams,
  maxNights: number
) {
  let shouldUpdate = false;

  if (
    nights &&
    (Number.isNaN(Number(nights)) || Number(nights) > maxNights || Number(nights) < DEFAULT_NUMBER)
  ) {
    newSearchParams.set(URLParams.nights, '1');
    shouldUpdate = true;
  }
  return shouldUpdate;
}

const resetExtraRooms = (
  searchParams: URLSearchParams,
  fromRoomIndex: number,
  maxRooms: number
) => {
  for (let roomIndex = fromRoomIndex; roomIndex <= maxRooms; roomIndex++) {
    searchParams.delete(`${URLParams.adult}${roomIndex}`);
    searchParams.delete(`${URLParams.child}${roomIndex}`);
    searchParams.delete(`${URLParams.cot}${roomIndex}`);
    searchParams.delete(`${URLParams.roomType}${roomIndex}`);
  }
};

const getSearchDetails = (roomIndex: number, searchParams: URLSearchParams) => {
  const getParam = (key: string): string | null => searchParams.get(key);

  const adultKey = `${URLParams.adult}${roomIndex}`;
  const childKey = `${URLParams.child}${roomIndex}`;
  const cotKey = `${URLParams.cot}${roomIndex}`;
  const roomTypeKey = `${URLParams.roomType}${roomIndex}`;

  return {
    adult: getParam(adultKey),
    child: getParam(childKey),
    cot: getParam(cotKey),
    roomType: getParam(roomTypeKey),
  };
};

const setSearchParams = (newSearchParams: URLSearchParams, paramValues: Record<string, string>) => {
  Object.keys(paramValues).forEach((key) => {
    const value = paramValues[key];

    newSearchParams.set(key, value);
  });
};
