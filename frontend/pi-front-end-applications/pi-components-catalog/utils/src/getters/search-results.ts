import { QueryClient } from '@tanstack/react-query';
import type {
  SearchRoomType,
  PromotionBanner,
  SingleHotelAvailability,
  SRParamsForQuery,
  SRResponseType,
} from '@whitbread-eos/api';
import {
  LanguageEnum,
  SEARCH_INFORMATION_PAGE_SIZE_RESULTS,
  SEARCH_RESULTS_PAGE_SIZE_REDIS_TTL,
  MULTI_HOTEL_AVAILABILITIES_QUERY_PI_V2,
  MULTI_HOTEL_AVAILABILITIES_QUERY,
  PROMO_CODE_COOKIE,
  SRMultiSearchParamsType,
  HotelBrand,
  PromoKind,
  ROOM_CODES,
} from '@whitbread-eos/api';
import { getCookie } from 'cookies-next';
import { NextRouter } from 'next/router';
import { ParsedUrlQuery } from 'querystring';

import { MAX_ROOMS_SEARCH_LIMIT } from '../global-constants';
import { setCookieWithDefaultDomain } from '../helpers/cookies';
import { graphQLRequest } from '../hooks';
import { logger } from '../logger/logger';
import { RedisKeyPrefix, RedisStorageServer } from '../storage/RedisStorageServer';
import { getAuthCookie } from './auth';
import { getDistanceUnitBasedOnLocale } from './getters';

export const getPlace = (
  multiSearchParams: SRMultiSearchParamsType,
  language: string | undefined
) => {
  const place = {
    location: multiSearchParams?.placeId
      ? multiSearchParams.placeId
      : (multiSearchParams?.coordinates ?? ''),
    locationFormat: multiSearchParams?.placeId ? 'PLACEID' : 'LATLONG',
    radius: language === LanguageEnum.ENGLISH ? 30 : 50,
    radiusUnit: getDistanceUnitBasedOnLocale(language ?? LanguageEnum.ENGLISH),
  };
  return place;
};

export function getDefaultRooms(query: ParsedUrlQuery) {
  const noOfRooms = Number(query.ROOMS);

  if (!noOfRooms) {
    return [
      {
        adults: 1,
        children: 0,
        shouldIncludeCot: false,
        roomType: 'DB',
      },
    ];
  }

  const defaultRooms: SearchRoomType[] = [];
  for (let idx = 0; idx < noOfRooms && idx < MAX_ROOMS_SEARCH_LIMIT; idx++) {
    const defaultRoom: SearchRoomType = {
      adults: Number(query[`ADULT${idx + 1}`]),
      children: Number(query[`CHILD${idx + 1}`]),
      shouldIncludeCot: !!Number(query[`COT${idx + 1}`]),
      shouldBeAccessible: query[`INTTYP${idx + 1}`] === ROOM_CODES.accessible,
      roomType: query[`INTTYP${idx + 1}`] as string,
    };
    defaultRooms.push(defaultRoom);
  }

  return defaultRooms;
}

export async function getSearchResultsPageSize(
  language: string,
  country: string,
  queryClient: QueryClient,
  redisKeyPrefix: RedisKeyPrefix = RedisKeyPrefix.COMMON
): Promise<{ initialPageSize?: number; lazyLoadPageSize?: number } | null> {
  logger.info({ country, language }, 'FETCH_SEARCH_RESULTS_INFORMATION_START');
  let cachedData;
  let redisStorage: RedisStorageServer | undefined;
  const cacheKey = `${redisKeyPrefix}::srp:page-size:${country}-${language}`;

  try {
    redisStorage = RedisStorageServer.getInstance();
    cachedData = await redisStorage.getItem(cacheKey);
  } catch (error) {
    logger.error({ error }, 'FETCH_SEARCH_RESULTS_INFORMATION_REDIS_ERROR');
  }

  try {
    if (cachedData) {
      const searchResultsPageSize = JSON.parse(cachedData);
      logger.info({ cacheKey, searchResultsPageSize }, 'FETCH_SEARCH_RESULTS_INFORMATION_END');
      return searchResultsPageSize;
    }

    const searchInformationData = await queryClient.fetchQuery({
      queryKey: ['searchInformationPageSize', country, language],
      queryFn: () => graphQLRequest(SEARCH_INFORMATION_PAGE_SIZE_RESULTS, { country, language }),
    });

    const { initialPageSize, lazyLoadPageSize } =
      searchInformationData?.searchInformation?.config?.api ?? {};

    const searchResultsPageSize = { initialPageSize, lazyLoadPageSize };

    await redisStorage?.setItem(
      cacheKey,
      JSON.stringify(searchResultsPageSize),
      SEARCH_RESULTS_PAGE_SIZE_REDIS_TTL
    );

    logger.info({ searchResultsPageSize }, 'FETCH_SEARCH_RESULTS_INFORMATION_END');
    return searchResultsPageSize;
  } catch (error) {
    logger.error({ error }, 'FETCH_SEARCH_RESULTS_INFORMATION_ERROR');
    return null;
  }
}

interface PromoBoxType {
  title: string;
  button: string;
  whenInvalid: string;
  whenMultipleRedeem: string;
  whenSuccess: string;
  whenEmpty: string;
  whenCodeAlreadyApplied?: string;
  whenUnavailable?: string;
  whenCodeExpired?: string;
  whenMinRoomsNotMet: string;
  whenMaxRoomsExceeded: string;
}

export type PromotionsInformation = {
  showPromo: boolean | null;
  isWithinPromoWindow: boolean | null;
  promotionCode: string | null;
  landingPage: string | null;
  promoBannerColour: string | null;
  promoBannerIcon: string | null;
  promoBannerTitle: string | null;
  promoBannerSubtitle: string | null;
  promoInvalidMessage: string | null;
  promoExpiredMessage: string | null;
  promoAmendMessage: string | null;
  promoBookingInfo: promoBookingInfoType;
  promoBox?: PromoBoxType;
  promoKind?: PromoKind;
  promoBoxStatus?: string | null;
  promoBoxMessageKey?: string | null;
  errorRateAndRoomMessage?: string | null;
  promoBannerVisibility?: string[];
};
interface promoBookingInfoType {
  ratePlanCode: string | null;
  promotionCode: string | null;
}

export function readPromotionsInformation(input: any): PromotionsInformation | null {
  if (!input?.dehydratedState || !Array.isArray(input.dehydratedState.queries)) {
    return null;
  }
  const queries = input.dehydratedState.queries;
  // Find any hotel availability query (hotelAvailability, hotelAvailabilityCCUI, hotelAvailabilityPI, etc.)
  const hotelAvailabilityQuery = queries.find(
    (q: any) =>
      Array.isArray(q?.queryKey) &&
      q.queryKey.some(
        (key: any) => typeof key === 'string' && key.toLowerCase().includes('hotelavailability')
      )
  );
  const nestedPromotionsInformation =
    hotelAvailabilityQuery?.state?.data?.hotelAvailability?.promotionsInformation;

  if (nestedPromotionsInformation) {
    return nestedPromotionsInformation;
  }
  // Fallback to the legacy promotionsInformation query
  const promoQuery = queries.find(
    (q: any) =>
      Array.isArray(q?.queryKey) &&
      q.queryKey.some(
        (key: any) => typeof key === 'string' && key.toLowerCase().includes('promotionsinformation')
      )
  );
  return promoQuery?.state?.data?.promotionsInformation ?? null;
}

export function shouldDisplayPromoBanner(
  type: string,
  promotionBannerData: PromotionsInformation,
  promotionBanner: PromotionBanner,
  router: NextRouter
): boolean {
  const hasDocument = typeof document !== 'undefined';
  const promoCode = hasDocument ? getCookie(PROMO_CODE_COOKIE) : '';
  const landingPagePromo = router?.query?.PROMOID;
  const summerPromoEnabled = promotionBanner?.enabled;
  const siteWidePromo = promotionBannerData?.showPromo;

  // Clear promo cookie if on landing page promo + sitewide promo
  if (landingPagePromo && hasDocument) {
    setCookieWithDefaultDomain(PROMO_CODE_COOKIE, null, -1);
  }

  if (type === 'breakfastPromo') {
    return Boolean(promoCode && !landingPagePromo);
  }

  if (type === 'summerPromo') {
    return Boolean(!promoCode && !landingPagePromo && !siteWidePromo && summerPromoEnabled);
  }

  return false;
}

export async function getBrandFromSnowDropSuggestions(snowdropSuggestionsResponse: {
  properties?: { brand?: string | null }[];
  managedPlaces?: unknown[];
  places?: {
    suggestion: string;
    placeId: string;
  }[];
}): Promise<string | null> {
  try {
    if (!snowdropSuggestionsResponse) return null;

    const initialBrand = getBrand(snowdropSuggestionsResponse?.properties?.[0]?.brand as string);
    if (initialBrand) {
      return initialBrand;
    }

    const suggestions = snowdropSuggestionsResponse?.places ?? [];
    if (!Array.isArray(suggestions) || suggestions.length === 0) {
      return null;
    }

    for (const place of suggestions) {
      const suggestionText = place?.suggestion?.trim();
      if (!suggestionText) continue;

      const lastWord = suggestionText.split(/\s+/).pop()?.toLowerCase();

      if (lastWord === 'germany') {
        return HotelBrand.PID;
      }
    }
    return HotelBrand.PI;
  } catch {
    return null;
  }
}

export function getPromoId(rawPromoId: string | string[] | undefined): string | null {
  if (Array.isArray(rawPromoId)) {
    return rawPromoId[0] ?? null;
  }
  return rawPromoId?.trim() || null;
}

export function getBrand(brand: string | null) {
  if (!brand) return null;
  return brand;
}

export function getHotelBrandFromSearchResults(
  hotelAvailabilities: SingleHotelAvailability[] | null | undefined
): string | null {
  if (!Array.isArray(hotelAvailabilities) || hotelAvailabilities?.length === 0) {
    return null;
  }

  const firstAvailableHotel = hotelAvailabilities.find(
    (h) => h?.hotelAvailability?.available === true
  );

  const brand = firstAvailableHotel?.hotelInformation?.brand;

  if (!brand) {
    return null;
  }

  return getBrand(brand);
}

export function extractMultiHotelAvailabilities(input: { dehydratedState: { queries: any } }) {
  const queries = input?.dehydratedState?.queries;
  if (!Array.isArray(queries)) return [];

  const hotelQuery = queries?.find((q: { queryKey: any }) =>
    q?.queryKey?.some?.(
      (k: string) =>
        k?.includes?.('multiHotelAvailabilities') || k?.includes?.('hotelAvailabilities')
    )
  );

  return (
    hotelQuery?.state?.data?.hotelAvailabilitiesV2?.multiHotelAvailabilities ??
    hotelQuery?.state?.data?.hotelAvailabilities?.multiHotelAvailabilities ??
    []
  );
}

export const HOTEL_AVAILABILITIES_QUERY_KEY = 'hotelAvailabilities';
export const HOTEL_AVAILABILITIES_QUERY_KEY_PI = 'hotelAvailabilitiesV2';

export function buildBaseHotelAvailabilitiesQueryKey(paramsForQuery: SRParamsForQuery) {
  return [
    paramsForQuery.startDate,
    paramsForQuery.endDate,
    paramsForQuery.rooms,
    paramsForQuery.place,
    paramsForQuery.oldWorldChannel,
    paramsForQuery.channel,
    paramsForQuery.subChannel,
    paramsForQuery.page,
    paramsForQuery.initialPageSize,
    paramsForQuery.lazyLoadPageSize,
    paramsForQuery.country,
    paramsForQuery.language,
    paramsForQuery.sort,
    paramsForQuery.filters,
  ];
}

type ResultType = {
  multiHotelAvailabilities: SingleHotelAvailability[];
  total: number;
  promotionsInformation: PromotionsInformation;
};

export function extractHotelAvailabilitiesResult(root: ResultType) {
  return {
    results: root.multiHotelAvailabilities,
    total: root.total,
    promotionsInformation: root?.promotionsInformation,
  };
}

export async function getNewSearchResultsPI(
  queryClient: QueryClient,
  paramsForQuery: SRParamsForQuery,
  promoId: string | null,
  isPromotionsInHotelAvailabilityEnabled: boolean
) {
  const queryResult = await queryClient.fetchQuery({
    queryKey: [
      HOTEL_AVAILABILITIES_QUERY_KEY_PI,
      ...buildBaseHotelAvailabilitiesQueryKey(paramsForQuery),
      paramsForQuery.ratePlanCodes,
      paramsForQuery.sortOption?.rcPriceModifier,
      paramsForQuery.sortOption?.rcDistanceModifier,
      paramsForQuery.sortOption?.rcHubModifier ?? null,
    ],
    queryFn: () =>
      graphQLRequest(MULTI_HOTEL_AVAILABILITIES_QUERY_PI_V2, {
        ...paramsForQuery,
        ...(isPromotionsInHotelAvailabilityEnabled && {
          promotionCode: promoId,
        }),
      }),
  });

  return extractHotelAvailabilitiesResult(queryResult.hotelAvailabilitiesV2);
}

export async function getNewSearchResultsBB(
  queryClient: QueryClient,
  paramsForQuery: SRParamsForQuery,
  promoId: string | null,
  isPromotionsInHotelAvailabilityEnabled: boolean
) {
  const queryResult = await queryClient.fetchQuery({
    queryKey: [
      HOTEL_AVAILABILITIES_QUERY_KEY,
      ...buildBaseHotelAvailabilitiesQueryKey(paramsForQuery),
      paramsForQuery.sortOption?.rcPriceModifier,
      paramsForQuery.sortOption?.rcDistanceModifier,
    ],
    queryFn: () =>
      graphQLRequest(MULTI_HOTEL_AVAILABILITIES_QUERY, {
        ...paramsForQuery,
        idToken: getAuthCookie(),
        ...(isPromotionsInHotelAvailabilityEnabled && {
          promotionCode: promoId,
        }),
      }) as Promise<SRResponseType>,
  });

  return extractHotelAvailabilitiesResult(queryResult.hotelAvailabilities as ResultType);
}

export async function getNewSearchResultsCCUI(
  queryClient: QueryClient,
  paramsForQuery: SRParamsForQuery,
  promoId: string | null,
  isPromotionsInHotelAvailabilityEnabled: boolean
) {
  const hotelAvailablilitiesQueryKey = [
    'hotelAvailabilities',
    ...buildBaseHotelAvailabilitiesQueryKey(paramsForQuery),
  ];
  const queryResult = await queryClient.fetchQuery({
    queryKey: paramsForQuery.companyId
      ? [...hotelAvailablilitiesQueryKey, paramsForQuery.companyId]
      : hotelAvailablilitiesQueryKey,
    queryFn: () =>
      graphQLRequest(MULTI_HOTEL_AVAILABILITIES_QUERY, {
        ...paramsForQuery,
        ...(isPromotionsInHotelAvailabilityEnabled &&
          !paramsForQuery.companyId && {
            promotionCode: promoId,
          }),
      }) as Promise<SRResponseType>,
  });

  return extractHotelAvailabilitiesResult(queryResult.hotelAvailabilities as ResultType);
}

export function setPromoInfoState(
  promotionsInformation: PromotionsInformation,
  setPromotionBannerData: any,
  flag: boolean
) {
  if (flag && promotionsInformation) {
    setPromotionBannerData(promotionsInformation);
  }
}
