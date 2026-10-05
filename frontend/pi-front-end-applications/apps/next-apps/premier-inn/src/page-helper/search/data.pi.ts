import { dehydrate, QueryClient } from '@tanstack/react-query';
import {
  GET_SEARCH_RULES_QUERY,
  GET_SEO_INFORMATION,
  MULTI_HOTEL_AVAILABILITIES_QUERY_PI_V2,
  PageName,
  SEARCH_INFORMATION_RESULTS,
  SITE_LEISURE,
  RC_PRICE_MODIFIER,
  RC_DISTANCE_MODIFIER,
  RC_HUB_MODIFIER,
  FT_PI_SORT_ORDER_DROPDOWN,
  getStaticContent,
  FT_PI_BB_CCUI_BARRIER_FREE_LABEL,
  getSuggestions,
  CountryCode,
  FT_PI_PROMO_CODE_LANDING_PAGE,
  FT_PI_PROMO_CODE_SITE_WIDE,
  PROMO_CODE_COOKIE,
  FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY,
} from '@whitbread-eos/api';
import { APP_VARIANT, getFallbackSearchPlace, getSearchParams } from '@whitbread-eos/organisms';
import {
  getDistanceUnitBasedOnLocale,
  getGQLClient,
  graphQLRequest,
  QueriesLogger,
  isDateValid,
  getSearchResultsPageSize,
  WB_SESSION_ID,
  getPromoId,
} from '@whitbread-eos/utils';
import Cookies from 'cookies';
import { add, format } from 'date-fns';
import { GetServerSidePropsContext } from 'next';
import { ParsedUrlQuery } from 'querystring';

interface Props extends GetServerSidePropsContext {
  queryClient: QueryClient;
  language: string;
  country: string;
  query: ParsedUrlQuery;
  featureToggles: { [key: string]: boolean };
}

const createSearchResultsPiDataLoader = async ({
  queryClient,
  language,
  country,
  query,
  featureToggles,
  req,
  res,
}: Props) => {
  const cookies = new Cookies(req, res);
  const sessionId = cookies.get(WB_SESSION_ID);
  const client = getGQLClient(sessionId);

  const { fetchQuery, prefetchQuery, logQueries } = new QueriesLogger(
    queryClient,
    req,
    res,
    query,
    'PI | SRP | Search Results Page'
  );

  const {
    [FT_PI_SORT_ORDER_DROPDOWN]: isPiSortOrderDropdownEnabled,
    [FT_PI_BB_CCUI_BARRIER_FREE_LABEL]: isBarrierFreeLabelEnabledFT,
    [FT_PI_PROMO_CODE_LANDING_PAGE]: isPromoCodeLandingPageEnabled,
    [FT_PI_PROMO_CODE_SITE_WIDE]: isPromoCodeSiteWideEnabled,
    [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: isPromotionsInHotelAvailabilityEnabled,
  } = featureToggles;
  const isBarrierFreeLabelEnabled =
    language === CountryCode.DE ? isBarrierFreeLabelEnabledFT : false;
  const enabledNewSortOrderDropdown = {
    isPiSortOrderDropdownEnabled: isPiSortOrderDropdownEnabled,
  };

  const multiSearchParams = getSearchParams(query, false, enabledNewSortOrderDropdown);

  const isValidDate = isDateValid(
    Number(multiSearchParams.arrivalDay),
    Number(multiSearchParams.arrivalMonth),
    Number(multiSearchParams.arrivalYear)
  );
  const startDate = isValidDate
    ? new Date(
        Number(multiSearchParams.arrivalYear),
        Number(multiSearchParams.arrivalMonth && multiSearchParams.arrivalMonth - 1),
        Number(multiSearchParams.arrivalDay)
      )
    : null;

  let fallbackSearchPlace;
  if (!multiSearchParams.placeId && !multiSearchParams.coordinates) {
    const snuwdropSuggestions = await getSuggestions(multiSearchParams.location);
    fallbackSearchPlace = getFallbackSearchPlace(multiSearchParams.location, snuwdropSuggestions);
    multiSearchParams.placeId = fallbackSearchPlace.PLACEID ?? '';
  }

  const place = {
    location: multiSearchParams.placeId ?? multiSearchParams.coordinates,
    locationFormat: multiSearchParams.placeId ? 'PLACEID' : 'LATLONG',
    radius: language === 'en' ? 30 : 50, // should retrieve radius from BE - new labels query
    radiusUnit: getDistanceUnitBasedOnLocale(language),
  };

  const endDate =
    (startDate && add(startDate, { days: Number(multiSearchParams.numberOfNights) })) || null;

  const rcPriceModifierCookie = cookies.get(RC_PRICE_MODIFIER);
  const rcDistanceModifierCookie = cookies.get(RC_DISTANCE_MODIFIER);
  const rcHubModifierCookie = cookies.get(RC_HUB_MODIFIER);
  const rcPriceModifier = rcPriceModifierCookie ? parseFloat(rcPriceModifierCookie) : 1;
  const rcDistanceModifier = rcDistanceModifierCookie ? parseFloat(rcDistanceModifierCookie) : 1;
  const rcHubModifier = rcHubModifierCookie ? parseFloat(rcHubModifierCookie) : undefined;

  const sortOption = {
    rcPriceModifier: rcPriceModifier,
    rcDistanceModifier: rcDistanceModifier,
    rcHubModifier: rcHubModifier,
  };

  const searchResultsPageSize = await getSearchResultsPageSize(language, country, queryClient);

  const paramsForQuery = {
    startDate: startDate && format(startDate, 'yyyy-MM-dd'),
    endDate: endDate && format(endDate, 'yyyy-MM-dd'),
    rooms: multiSearchParams.rooms,
    place,
    oldWorldChannel: multiSearchParams.bookingChannel,
    channel: APP_VARIANT.PI,
    subChannel: multiSearchParams.bookingChannel,
    page: 1,
    initialPageSize: Number(searchResultsPageSize?.initialPageSize ?? 40),
    lazyLoadPageSize: Number(searchResultsPageSize?.lazyLoadPageSize ?? 10),
    country,
    language,
    sort: multiSearchParams.sort,
    sortOption,
    filters: multiSearchParams.filters,
    ratePlanCodes: multiSearchParams?.cellCodes ?? [],
  };

  const searchInformationQuery = prefetchQuery(
    ['searchInformation', paramsForQuery.country, paramsForQuery.language],
    () =>
      graphQLRequest(
        SEARCH_INFORMATION_RESULTS,
        {
          country: paramsForQuery.country,
          language: paramsForQuery.language,
        },
        undefined,
        undefined,
        client
      )
  );

  const promoId = getPromoId(query?.PROMOID);

  let promoInformationQuery = null;
  const cookieBasedPromoCode = cookies.get(PROMO_CODE_COOKIE);
  if (isPromoCodeLandingPageEnabled && !cookieBasedPromoCode) {
    const shouldCallPromoInfoAPI =
      Boolean(promoId || isPromoCodeSiteWideEnabled) && !isPromotionsInHotelAvailabilityEnabled;

    if (shouldCallPromoInfoAPI) {
      promoInformationQuery = {
        isPromoEnabled: shouldCallPromoInfoAPI,
        country: paramsForQuery.country,
        language: paramsForQuery.language,
        channel: paramsForQuery.channel,
        promotionCode: promoId as string,
        arrival: paramsForQuery.startDate,
        departure: paramsForQuery.endDate,
      };
    }
  }

  const hotelAvailabilitiesQueryKey = [
    'hotelAvailabilitiesV2',
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
    paramsForQuery.ratePlanCodes,
    paramsForQuery.sortOption?.rcPriceModifier,
    paramsForQuery.sortOption?.rcDistanceModifier,
    paramsForQuery.sortOption?.rcHubModifier ?? null,
  ];

  const hotelAvailabilitiesQuery = prefetchQuery(hotelAvailabilitiesQueryKey, () =>
    graphQLRequest(
      MULTI_HOTEL_AVAILABILITIES_QUERY_PI_V2,
      {
        ...paramsForQuery,
        ...(isPromotionsInHotelAvailabilityEnabled && {
          promotionCode: promoId,
        }),
      },
      undefined,
      undefined,
      client
    )
  );

  const seoQuery = prefetchQuery(
    ['seoInformation', paramsForQuery.language, paramsForQuery.country, PageName.SRP],
    () =>
      graphQLRequest(
        GET_SEO_INFORMATION,
        {
          language: paramsForQuery.language,
          country: paramsForQuery.country,
          page: PageName.SRP,
        },
        undefined,
        undefined,
        client
      )
  );
  const staticContentQuery = getStaticContent(isBarrierFreeLabelEnabled);

  const getStaticContentQuery = fetchQuery(['GetStaticContent', language, country], () =>
    graphQLRequest(
      staticContentQuery,
      {
        language,
        country,
        site: SITE_LEISURE,
        businessBooker: false,
      },
      undefined,
      undefined,
      client
    )
  );

  const searchRulesQuery = prefetchQuery(['getSearchRules', 'PI'], () =>
    graphQLRequest(
      GET_SEARCH_RULES_QUERY,
      {
        channel: 'PI',
      },
      undefined,
      undefined,
      client
    )
  );

  const promises = [
    getStaticContentQuery,
    hotelAvailabilitiesQuery,
    searchInformationQuery,
    searchRulesQuery,
    seoQuery,
  ];

  await Promise.all(promises);

  logQueries(performance.now());

  return {
    dehydratedState: dehydrate(queryClient),
    ...(fallbackSearchPlace && { fallbackSearchPlace }),
    promoInformationQuery,
  };
};

export default createSearchResultsPiDataLoader;
