import type {
  SearchPlaceType,
  SearchPropertyType,
  SearchSuggestions,
  SRErrorResponseHotelAvailabilities,
  SRErrorResponseHotelAvailabilitiesV2,
  SingleHotelAvailability,
  SRMultiSearchParamsType,
} from '@whitbread-eos/api';
import { DAY_TODAY, DAY_TOMORROW } from '@whitbread-eos/api';
import { differenceInDays } from 'date-fns';
import { NextRouter } from 'next/router';

export const getFallbackSearchPlace = (
  searchTerm: string,
  searchSuggestions: SearchSuggestions
): { PLACEID?: string; 'searchModel.searchTerm'?: string } => {
  const { properties, places } = searchSuggestions;
  const items = [...properties, ...places];
  const searchedItem = items.find((item) => item.suggestion === searchTerm);
  const placeId = (searchedItem as SearchPlaceType)?.placeId;
  const coordinates = (searchedItem as SearchPropertyType)?.geometry?.coordinates;

  if (placeId) {
    return {
      PLACEID: placeId,
    };
  } else if (coordinates?.length || !placeId) {
    // if the searched location is a hotel or if we didn't find a match in snowdrop response
    // then we will go further with the first suggestion from snowdrop
    // https://whitbreadis.atlassian.net/browse/DNRQ-43970?focusedCommentId=287687
    const item = places[0];
    return {
      PLACEID: item.placeId,
      'searchModel.searchTerm': item.suggestion,
    };
  }
  return {};
};

type SearchValueType = string | string[] | undefined;

export const formatDateSearchQueryUrl = (
  key: string,
  value: SearchValueType,
  isMetaArrivalDayKeywordFlagEnabled?: boolean
): SearchValueType => {
  if (!value) {
    return value;
  }

  switch (key) {
    case 'ARRdd':
      // don't truncate date string if the value is 'today' or 'tomorrow' and feature enabled
      if (
        isMetaArrivalDayKeywordFlagEnabled &&
        typeof value === 'string' &&
        (value.toLowerCase() === DAY_TODAY || value.toLowerCase() === DAY_TOMORROW)
      ) {
        return value;
      }
      return ('0' + value).slice(-2);
    case 'ARRmm':
      return ('0' + value).slice(-2);
    default:
      return value;
  }
};

export const getSearchQueryUrl = (
  router: NextRouter,
  ignoreKeys?: string[],
  formatter?: (key: string, value: SearchValueType) => SearchValueType
): string => {
  const queryParams = router.query;
  const pathObject: { [key: string]: any } = {};
  for (const key in queryParams) {
    const foundInIgnoredKeys = ignoreKeys?.find((el) => el === key);
    if (!foundInIgnoredKeys) {
      if (formatter) {
        pathObject[key] = formatter(key, queryParams[key]);
      } else {
        pathObject[key] = queryParams[key];
      }
    }
  }
  const searchQueryUrl = Object.entries(pathObject)
    .map(([key, value]) => `${key}=${value}`)
    .join('&');
  return searchQueryUrl;
};

export function hotelOpensSoon(
  hotelOpeningDate: string,
  multiSearchParams: SRMultiSearchParamsType
) {
  if (!hotelOpeningDate) return false;
  try {
    return (
      differenceInDays(
        new Date(hotelOpeningDate),
        new Date(
          `${multiSearchParams?.arrivalMonth}/${multiSearchParams?.arrivalDay}/${multiSearchParams?.arrivalYear}`
        )
      ) > 0
    );
  } catch (e) {
    return false;
  }
}

export const getOpeningSoonHotels = (
  hotels: SingleHotelAvailability[],
  multiSearchParams: SRMultiSearchParamsType
) =>
  hotels?.filter((hotel: SingleHotelAvailability) => {
    if (hotel?.hotelInformation?.hotelOpeningDate) {
      try {
        return hotelOpensSoon(hotel?.hotelInformation?.hotelOpeningDate, multiSearchParams);
      } catch (e) {
        return false;
      }
    }
    return false;
  });

export function firstError(
  errors: SRErrorResponseHotelAvailabilities | SRErrorResponseHotelAvailabilitiesV2
) {
  return errors?.response?.errors[0];
}

export const getCurrentResultsNumber = (
  results: any[],
  items: any[],
  total: number,
  lazyLoadPageSize = 10
) => {
  if (
    (results.length < lazyLoadPageSize && items.length > total - results.length) ||
    total - results.length === items.length
  ) {
    return 0;
  }
  return results.length;
};

export const getNewUrlSortValue = (
  currentValue: any,
  URLSortValue: string | string[] | undefined,
  enabledNewSortOrderDropdown?: {
    isPiSortOrderDropdownEnabled?: boolean;
    isBbSortOrderDropdownEnabled?: boolean;
  }
) => {
  const SORT_BY = {
    DISTANCE: '1',
    PRICE: '2',
  };

  const piSortByIds = {
    RECOMMENDATION: '1',
    DISTANCE: '2',
    PRICE: '3',
  };

  const bbSortByIds = {
    DISTANCE: '1',
    RECOMMENDATION: '2',
    PRICE: '3',
  };

  if (enabledNewSortOrderDropdown?.isPiSortOrderDropdownEnabled) {
    return piSortByIds[currentValue?.id as keyof typeof piSortByIds];
  }

  if (enabledNewSortOrderDropdown?.isBbSortOrderDropdownEnabled) {
    return bbSortByIds[currentValue?.id as keyof typeof bbSortByIds];
  }

  return URLSortValue === SORT_BY.DISTANCE ? SORT_BY.PRICE : SORT_BY.DISTANCE;
};

export function getSearchRedirectURL(
  router: NextRouter,
  paramsToIgnore: string[],
  country: string,
  language: string
) {
  const queryParamsUrl = getSearchQueryUrl(router, paramsToIgnore);
  return `${country}/${language}/search.html?${queryParamsUrl}`;
}
