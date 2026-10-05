/* eslint-disable @typescript-eslint/no-explicit-any */
import type {
  SearchQueryURLParamsType,
  SearchRequestParamsType,
  SearchRoomType,
  SRMultiSearchParamsType,
  Room,
  StayDetailsStateLocalStorageType,
} from '@whitbread-eos/api';
import { DEFAULT_BOOKINGCHANNEL, DEFAULT_MULTI_SEARCH_ROOM } from '@whitbread-eos/api';
import { SORT_TYPES, NEW_PI_SORT_TYPES, NEW_BB_SORT_TYPES } from '@whitbread-eos/molecules';
import { isDateValid, getTodayTomorrowDate, MAX_ROOMS_SEARCH_LIMIT } from '@whitbread-eos/utils';
import type { Locale } from 'date-fns';
import { format } from 'date-fns';
import set from 'lodash/set';
import { NextRouter } from 'next/router';
import { ParsedUrlQuery } from 'querystring';

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

export function setSearchLocationInLocalStorage(
  queryParams: SearchRequestParamsType | undefined,
  stayDetailsState: StayDetailsStateLocalStorageType
) {
  const placeId = queryParams?.placeId ? queryParams.placeId : '';
  const location = queryParams?.location
    ? { longitude: queryParams.location[0], latitude: queryParams.location[1] }
    : { longitude: '', latitude: '' };
  const hotelId = queryParams?.code ? queryParams.code : '';

  set(stayDetailsState.data.info, 'suggestion.placeId', placeId);
  set(stayDetailsState.data.info, 'suggestion.location', location);
  return set(stayDetailsState.data.info, 'suggestion.hotelId', hotelId);
}

export function mapRoomsForAvailabilityQuery(
  rooms: SearchRoomType[],
  mappedRoomLabels: Record<string, string>
) {
  return rooms.map((room) => {
    const roomCode: string = mappedRoomLabels[room.roomType as any];
    return {
      adultsNumber: room.adults,
      childrenNumber: room.children,
      roomType: roomCode,
      cotRequired: room.shouldIncludeCot,
    };
  });
}

export function formatSummaryDateRange(
  dateFormat: string,
  startDate: Date | null,
  endDate: Date | null,
  translationDateLanguage: {
    locale: Locale;
  }
) {
  if (!startDate || !endDate) {
    return;
  }

  return `${format(startDate, dateFormat, translationDateLanguage)} - ${format(
    endDate,
    dateFormat,
    translationDateLanguage
  )}`;
}

export const getNotificationMarginTop = (
  searchSummaryActive: boolean,
  searchConsoleIsActive: boolean,
  breakPoint: string
) => {
  const isMobile = breakPoint === 'mobile';

  if (searchSummaryActive) {
    return 'md';
  }
  if (searchConsoleIsActive) {
    return isMobile ? '-11rem' : '-xl';
  }
  return isMobile ? '-7rem' : '-2xl';
};

export function getRedirectWithDefaultsURL(
  router: NextRouter,
  day: number,
  month: number,
  year: number,
  NIGHTS?: number,
  showMultipleRooms?: boolean,
  isPriceFinderPage?: boolean
) {
  const defaultQuery = {
    ARRdd: day,
    ARRmm: month,
    ARRyyyy: year,
    ...(!isPriceFinderPage && {
      NIGHTS: !showMultipleRooms ? 1 : NIGHTS,
      ROOMS: 1,
      ADULT1: 1,
      CHILD1: 0,
      COT1: 0,
      INTTYP1: 'DB',
    }),
  };
  const [path, searchParams] = router.asPath.split('?');

  const shouldKeepKey = (key: string) => {
    return !(
      Object.keys(defaultQuery).includes(key) ||
      key.startsWith('ADULT') ||
      key.startsWith('CHILD') ||
      key.startsWith('INTTYP') ||
      key.startsWith('COT')
    );
  };
  const url = new URLSearchParams(searchParams);

  const newQueryParams = Array.from(url.entries())
    .filter(([key]) => shouldKeepKey(key))
    .reduce((query: any, [key, value]) => {
      query[key] = value;
      return query;
    }, defaultQuery);

  const newSearchParams = Object.entries(newQueryParams)
    .map(([key, value]) => `${key}=${value}`)
    .join('&');

  return `${path}?${newSearchParams}`;
}

export function appendPromoIdToUrl(baseUrl: string, promoId?: string): string {
  if (promoId) {
    return `${baseUrl}&PROMOID=${promoId}`;
  }
  return baseUrl;
}

export function getSearchParams(
  query: ParsedUrlQuery,
  isMetaArrivalDayKeywordFlagEnabled?: boolean,
  enabledNewSortOrderDropdown?: {
    isPiSortOrderDropdownEnabled?: boolean;
    isBbSortOrderDropdownEnabled?: boolean;
  }
) {
  const { NIGHTS, ROOMS, LOCATION, PLACEID, BOOKINGCHANNEL, FILTERS, CODE, PROMOID } = query;
  let { ARRdd, ARRmm, ARRyyyy } = query;

  if (isMetaArrivalDayKeywordFlagEnabled) {
    const updatedArrivalDate = getTodayTomorrowDate(ARRdd, ARRmm, ARRyyyy);
    if (updatedArrivalDate) {
      ARRdd = updatedArrivalDate.ARRdd;
      ARRmm = updatedArrivalDate.ARRmm;
      ARRyyyy = updatedArrivalDate.ARRyyyy;
    }
  }

  const location = query['searchModel.searchTerm'] as string;
  const SORT = query?.SORT as string;

  const CELLCODES = query?.CELLCODES as string;
  const cellCodesArr = CELLCODES?.split(',') as string[] | [];
  let sort = SORT_TYPES[Number(SORT)] ?? SORT_TYPES[1];

  if (enabledNewSortOrderDropdown?.isPiSortOrderDropdownEnabled) {
    sort = NEW_PI_SORT_TYPES[Number(SORT)] ?? NEW_PI_SORT_TYPES[1];
  } else if (enabledNewSortOrderDropdown?.isBbSortOrderDropdownEnabled) {
    sort = NEW_BB_SORT_TYPES[Number(SORT)] ?? NEW_BB_SORT_TYPES[1];
  }

  const corpId = query.CORPID && (query.CORPID as string);
  const compId = query.COMPID && (query.COMPID as string);

  const SRRooms: Room[] = [];
  for (let idx = 0; idx < Number(ROOMS) && idx < MAX_ROOMS_SEARCH_LIMIT; idx++) {
    const room: Room = {
      adultsNumber: Number(query[`ADULT${idx + 1}`]),
      childrenNumber: Number(query[`CHILD${idx + 1}`]),
      type: query[`INTTYP${idx + 1}`] as string,
    };
    SRRooms.push(room);
  }
  if (SRRooms.length === 0) {
    SRRooms.push(DEFAULT_MULTI_SEARCH_ROOM);
  }

  const isArrivalValid = isDateValid(Number(ARRdd), Number(ARRmm), Number(ARRyyyy));

  const multiSearchParams: SRMultiSearchParamsType = {
    arrivalDay: isArrivalValid ? Number(ARRdd) : null,
    arrivalMonth: isArrivalValid ? Number(ARRmm) : null,
    arrivalYear: isArrivalValid ? Number(ARRyyyy) : null,
    location: location,
    numberOfNights: isArrivalValid ? Number(NIGHTS) : null,
    rooms: SRRooms,
    code: CODE as string,
    placeId: PLACEID as string,
    coordinates: LOCATION as string,
    bookingChannel: BOOKINGCHANNEL ? (BOOKINGCHANNEL as string) : DEFAULT_BOOKINGCHANNEL,
    sort: sort,
    filters: (FILTERS as string) || '',
    cellCodes: cellCodesArr,
    corpId,
    compId,
    promoId: PROMOID,
  };

  return multiSearchParams;
}

export const FIELDS = {
  datepicker: 'datepicker',
  location: 'location',
  occupancy: 'occupancy',
  numberOfNights: 'numberOfNights',
};

export type ERROR_VALUES_TYPE =
  | 'invalidLocation'
  | 'invalidDate'
  | 'arrivalDateInThePast'
  | 'arrivalDateInTheFuture'
  | 'invalidNights'
  | 'invalidOccupancy';
export type ERRORS_TYPE = { [key in ERROR_VALUES_TYPE]: ERROR_VALUES_TYPE };

export const ERROR_KEYS: ERRORS_TYPE = {
  invalidLocation: 'invalidLocation',
  invalidDate: 'invalidDate',
  arrivalDateInThePast: 'arrivalDateInThePast',
  arrivalDateInTheFuture: 'arrivalDateInTheFuture',
  invalidNights: 'invalidNights',
  invalidOccupancy: 'invalidOccupancy',
};

export const ERROR_FIELDS = {
  invalidLocation: 'location',
  invalidDate: 'datepicker',
  arrivalDateInThePast: 'datepicker',
  arrivalDateInTheFuture: 'datepicker',
  invalidNights: 'numberOfNights',
  invalidOccupancy: 'occupancy',
};
