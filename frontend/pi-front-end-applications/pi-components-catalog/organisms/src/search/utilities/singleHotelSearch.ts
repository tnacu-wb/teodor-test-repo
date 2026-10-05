import {
  StayDetailsStateLocalStorageType,
  GET_HOTEL_INFORMATION,
  SingleHotelSearchLabelsType,
  SRMultiSearchParamsType,
  Room,
  HOTEL_AVAILABILITY_QUERY,
  Area,
  PROMO_CODE_COOKIE,
} from '@whitbread-eos/api';
import type {
  SearchQueryURLParamsType,
  SearchRequestParamsType,
  SearchHotelAvailability,
} from '@whitbread-eos/api';
import { VIEW_TYPE_CONSTANTS } from '@whitbread-eos/molecules';
import { getCookie, graphQLRequest } from '@whitbread-eos/utils';
import { add, differenceInDays, format } from 'date-fns';

const DEFAULT_SORT_VALUE = '1';

export async function singleHotelSearchCCUI(
  stayDetailsState: StayDetailsStateLocalStorageType,
  paramsMappedForURL: SearchQueryURLParamsType,
  labels: SingleHotelSearchLabelsType,
  URLToRedirect: string,
  queryParams: SearchRequestParamsType,
  country: string,
  language: string,
  channel: string
) {
  const { latitude, longitude } = stayDetailsState.data.info.suggestion.location;
  const location = `${latitude},${longitude}`;
  const { listView } = VIEW_TYPE_CONSTANTS;
  const redirectToSrpLink =
    `/${country}/${language}/search.html?searchModel.searchTerm=${paramsMappedForURL.searchTerm}&LOCATION=${location}&${URLToRedirect}&BOOKINGCHANNEL=${labels.bookingChannel}&SORT=${DEFAULT_SORT_VALUE}&VIEW=${listView}` as unknown as Location;
  const hotelId = queryParams.code;

  if (!hotelId) {
    return `/${country}/${language}/search.html?searchModel.searchTerm=${paramsMappedForURL.searchTerm}&PLACEID=${queryParams.placeId}&${URLToRedirect}&BOOKINGCHANNEL=${labels.bookingChannel}&SORT=${DEFAULT_SORT_VALUE}&VIEW=${listView}` as unknown as Location;
  }
  const { isAvailable, isOpeningSoon } = await isHotelAvailable(
    {
      ARRyyyy: paramsMappedForURL.ARRyyyy,
      ARRmm: paramsMappedForURL.ARRmm,
      ARRdd: paramsMappedForURL.ARRdd,
      nights: paramsMappedForURL.nights,
      rooms: paramsMappedForURL.rooms,
      code: paramsMappedForURL.code,
    },
    country,
    language,
    labels.mappedRoomLabels,
    channel
  );

  URLToRedirect += `&BRAND=${paramsMappedForURL.brand}`;
  return await getSearchRedirectURL({
    isAvailable,
    isOpeningSoon,
    redirectToSrpLink,
    queryParams,
    country,
    language,
    URLToRedirect,
  });
}

export async function singleHotelSearchPI(
  stayDetailsState: StayDetailsStateLocalStorageType,
  paramsMappedForURL: SearchQueryURLParamsType,
  labels: SingleHotelSearchLabelsType,
  URLToRedirect: string,
  queryParams: SearchRequestParamsType,
  country: string,
  language: string,
  channel: string
) {
  const { latitude, longitude } = stayDetailsState.data.info.suggestion.location;
  const location = `${latitude},${longitude}`;
  const { listView } = VIEW_TYPE_CONSTANTS;
  const redirectToSrpLink =
    `/${country}/${language}/search.html?searchModel.searchTerm=${paramsMappedForURL.searchTerm}&LOCATION=${location}&${URLToRedirect}&BOOKINGCHANNEL=${labels.bookingChannel}&SORT=${DEFAULT_SORT_VALUE}&VIEW=${listView}` as unknown as Location;
  const hotelId = queryParams.code;

  if (!hotelId) {
    return `/${country}/${language}/search.html?searchModel.searchTerm=${paramsMappedForURL.searchTerm}&PLACEID=${queryParams.placeId}&${URLToRedirect}&BOOKINGCHANNEL=${labels.bookingChannel}&SORT=${DEFAULT_SORT_VALUE}&VIEW=${listView}` as unknown as Location;
  }

  const { isAvailable, isOpeningSoon } = await isHotelAvailable(
    {
      ARRyyyy: paramsMappedForURL.ARRyyyy,
      ARRmm: paramsMappedForURL.ARRmm,
      ARRdd: paramsMappedForURL.ARRdd,
      nights: paramsMappedForURL.nights,
      rooms: paramsMappedForURL.rooms,
      code: paramsMappedForURL.code,
    },
    country,
    language,
    labels.mappedRoomLabels,
    channel
  );

  return await getSearchRedirectURL({
    isAvailable,
    isOpeningSoon,
    redirectToSrpLink,
    queryParams,
    country,
    language,
    URLToRedirect,
  });
}

export async function singleHotelSearchBB(
  stayDetailsState: StayDetailsStateLocalStorageType,
  paramsMappedForURL: SearchQueryURLParamsType,
  labels: SingleHotelSearchLabelsType,
  URLToRedirect: string,
  queryParams: SearchRequestParamsType,
  country: string,
  language: string,
  channel: string,
  variant?: string,
  token?: string
) {
  const { latitude, longitude } = stayDetailsState.data.info.suggestion.location;
  const location = `${latitude},${longitude}`;
  const { listView } = VIEW_TYPE_CONSTANTS;

  const redirectToSrpLink = `/${country}/${language}${
    variant === 'bb' ? '/business-booker' : ''
  }/search.html?searchModel.searchTerm=${
    paramsMappedForURL.searchTerm
  }&LOCATION=${location}&${URLToRedirect}&BOOKINGCHANNEL=${
    labels.bookingChannel
  }&SORT=${DEFAULT_SORT_VALUE}&VIEW=${listView}` as unknown as Location;

  const hotelId = queryParams.code;
  if (!hotelId) {
    return `/${country}/${language}${
      variant === 'bb' ? '/business-booker' : ''
    }/search.html?searchModel.searchTerm=${paramsMappedForURL.searchTerm}&PLACEID=${
      queryParams.placeId
    }&${URLToRedirect}&BOOKINGCHANNEL=${
      labels.bookingChannel
    }&SORT=${DEFAULT_SORT_VALUE}&VIEW=${listView}` as unknown as Location;
  }

  const { isAvailable, isOpeningSoon } = await isHotelAvailable(
    {
      ARRyyyy: paramsMappedForURL.ARRyyyy,
      ARRmm: paramsMappedForURL.ARRmm,
      ARRdd: paramsMappedForURL.ARRdd,
      nights: paramsMappedForURL.nights,
      rooms: paramsMappedForURL.rooms,
      code: paramsMappedForURL.code,
    },
    country,
    language,
    labels.mappedRoomLabels,
    channel,
    token
  );

  return await getSearchRedirectURL({
    isAvailable,
    isOpeningSoon,
    redirectToSrpLink,
    queryParams,
    country,
    language,
    URLToRedirect,
    variant,
  });
}

export async function isHotelAvailable(
  { ARRyyyy, ARRmm, ARRdd, nights, rooms, code }: SearchHotelAvailability,
  country: string,
  language: string,
  mappedRoomLabels: any,
  channel: string,
  token?: string
) {
  if (!code) {
    return { isOpeningSoon: false, isAvailable: true, hasMlos: false };
  }

  let isAvailable = false;
  let hasMlos = false;

  try {
    const hotelAvailabilityOpera = await isOperaHotelAvailable(
      { ARRyyyy, ARRmm, ARRdd, nights, rooms, code },
      language,
      country,
      mappedRoomLabels,
      channel,
      token
    );

    isAvailable = hotelAvailabilityOpera?.isHotelAvailable;
    hasMlos = hotelAvailabilityOpera?.hasHotelMlos;
  } catch (e) {
    console.log(e);
  }

  const isOpeningSoon = await isHotelOpeningSoon(
    { ARRdd, ARRmm, ARRyyyy },
    code,
    country,
    language
  );

  return { isOpeningSoon, isAvailable, hasMlos };
}

export function HDPUrlToRedirectFormatter(URLToRedirect: string): string {
  if (!URLToRedirect || URLToRedirect.indexOf('&') === -1) {
    return URLToRedirect;
  }

  const checkNoOfPairs = URLToRedirect.match(/&/g) || [];
  const checkNoOfParams = URLToRedirect.match(/=/g) || [];
  if (checkNoOfPairs?.length === 0 || checkNoOfParams?.length !== checkNoOfPairs?.length + 1) {
    return URLToRedirect;
  }

  return URLToRedirect.split('&')
    .map((kvPair: string) => {
      if (kvPair.indexOf('ARRdd') > -1 || kvPair.indexOf('ARRmm') > -1) {
        const [key, value] = kvPair.split('=');
        return `${key}=${('0' + value).slice(-2)}`;
      }
      return kvPair;
    })
    .join('&');
}

export async function getSearchRedirectURL({
  isAvailable,
  isOpeningSoon,
  redirectToSrpLink,
  queryParams,
  country,
  language,
  URLToRedirect,
  variant,
}: any) {
  if (!isAvailable || isOpeningSoon) {
    return redirectToSrpLink;
  }
  const hotelSlug = await getHotelSlugById(queryParams.code, country, language);
  if (hotelSlug) {
    return `/${country}/${language}${
      variant === 'bb' ? '/business-booker' : ''
    }/hotels${hotelSlug}.html?${HDPUrlToRedirectFormatter(URLToRedirect)}` as unknown as Location;
  }
}

export async function isOperaHotelAvailable(
  { ARRyyyy, ARRmm, ARRdd, nights, rooms, code }: SearchHotelAvailability,
  language: string,
  country: string,
  mappedRoomLabels: Record<string, string>,
  bookingChannel: string,
  token?: string
) {
  const startDate = new Date(Number(ARRyyyy), Number(ARRmm && ARRmm - 1), Number(ARRdd));
  const endDate = add(startDate, { days: Number(nights) });
  const availabilityQueryParams = {
    hotelId: code,
    arrival: format(startDate, 'yyyy-MM-dd'),
    departure: format(endDate, 'yyyy-MM-dd'),
    brand: Area.PI,
    country: country,
    rooms: rooms?.map((room) => ({
      adultsNumber: room.adults,
      childrenNumber: room.children,
      cotRequired: room.shouldIncludeCot,
      roomType: room.roomType && mappedRoomLabels[room.roomType],
    })),
    language: language,
    bookingChannel: {
      channel: bookingChannel,
      language: language?.toUpperCase(),
      subchannel: 'WEB',
    },
    channel: bookingChannel,
    promotionCode: getCookie(PROMO_CODE_COOKIE),
  };

  const hotelAvailabilityResponse = await graphQLRequest(
    HOTEL_AVAILABILITY_QUERY,
    availabilityQueryParams,
    token
  );

  const isHotelAvailable =
    hotelAvailabilityResponse.hotelAvailability.roomRates.length > 0 &&
    hotelAvailabilityResponse.hotelAvailability.available;

  const hasHotelMlos = hotelAvailabilityResponse.hotelAvailability.mlos;

  return { isHotelAvailable, hasHotelMlos };
}

export async function isHotelOpeningSoon(
  { ARRdd, ARRmm, ARRyyyy }: { ARRdd: number | null; ARRmm: number | null; ARRyyyy: number | null },
  hotelId: string,
  country: string,
  language: string
): Promise<boolean> {
  const hotelResponse = await graphQLRequest(GET_HOTEL_INFORMATION, {
    hotelId,
    country,
    language,
  });

  const hotelOpeningDate = hotelResponse.hotelInformation.hotelOpeningDate;
  const selectedDate = new Date(
    `${ARRmm}/
      ${ARRdd}/
      ${ARRyyyy}`
  );
  const openingSoonDate =
    hotelOpeningDate !== '' && new Date(JSON.parse(JSON.stringify(hotelOpeningDate)));
  const isOpeningSoon = openingSoonDate && differenceInDays(openingSoonDate, selectedDate) > 0;

  return isOpeningSoon;
}

export async function getHotelSlugById(hotelId: string, country: string, language: string) {
  const hotelSlugByIdResponse = await graphQLRequest(GET_HOTEL_INFORMATION, {
    hotelId,
    country,
    language,
  });

  return hotelSlugByIdResponse.hotelInformation.links.detailsPage;
}

export function mapMultiSearchToQueryParams(multiSearchParams: SRMultiSearchParamsType) {
  return {
    ARRdd: multiSearchParams.arrivalDay,
    ARRmm: multiSearchParams.arrivalMonth,
    ARRyyyy: multiSearchParams.arrivalYear,
    searchTerm: multiSearchParams.location,
    nights: multiSearchParams.numberOfNights,
    rooms: mapMultiSearchRoomsToSearchRooms(multiSearchParams.rooms),
    roomsNumber: multiSearchParams.rooms.length,
    code: multiSearchParams.code,
  };
}

export function mapMultiSearchRoomsToSearchRooms(rooms: Room[]) {
  return rooms.map((room) => {
    return {
      adults: room.adultsNumber,
      children: room.childrenNumber,
      roomType: room.type,
      shouldIncludeCot: false,
    };
  });
}
