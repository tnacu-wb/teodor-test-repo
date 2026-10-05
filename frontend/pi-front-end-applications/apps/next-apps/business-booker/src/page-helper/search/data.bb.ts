import { dehydrate, QueryClient } from '@tanstack/react-query';
import {
  GET_SEARCH_RULES_QUERY,
  GET_SEO_INFORMATION,
  MULTI_HOTEL_AVAILABILITIES_QUERY,
  PageName,
  SEARCH_INFORMATION_RESULTS,
  SITE_BB,
  RC_PRICE_MODIFIER,
  RC_DISTANCE_MODIFIER,
  FT_BB_SORT_ORDER_DROPDOWN,
  FT_PI_BB_CCUI_BARRIER_FREE_LABEL,
  getStaticContent,
  getSuggestions,
  CountryCode,
  FT_BB_PROMO_CODE_LANDING_PAGE,
  FT_BB_PROMO_CODE_SITE_WIDE,
  FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY,
} from '@whitbread-eos/api';
import { APP_VARIANT, getFallbackSearchPlace, getSearchParams } from '@whitbread-eos/organisms';
import {
  getDistanceUnitBasedOnLocale,
  getSearchResultsPageSize,
  graphQLRequest,
  ID_TOKEN_COOKIE,
  isDateValid,
  QueriesLogger,
  getGQLClient,
  WB_SESSION_ID,
  getPromoId,
} from '@whitbread-eos/utils';
import { getInnBusinessServerSideProps } from '@whitbread-eos/utils/server';
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
  isInnBusinessAppPage?: boolean;
}

const createSearchResultsBBDataLoader = async ({
  queryClient,
  language,
  country,
  query,
  featureToggles,
  req,
  res,
  isInnBusinessAppPage = false,
}: Props) => {
  const cookies = new Cookies(req, res);
  const idTokenCookie = cookies.get(ID_TOKEN_COOKIE);
  const sessionId = cookies.get(WB_SESSION_ID);

  const { fetchQuery, prefetchQuery, logQueries } = new QueriesLogger(
    queryClient,
    req,
    res,
    query,
    'BB | SRP | Search Results Page'
  );

  const {
    [FT_BB_SORT_ORDER_DROPDOWN]: isBbSortOrderDropdownEnabled,
    [FT_PI_BB_CCUI_BARRIER_FREE_LABEL]: isBarrierFreeLabelEnabledFT,
    [FT_BB_PROMO_CODE_LANDING_PAGE]: isBBPromoCodeLandingPageEnabled,
    [FT_BB_PROMO_CODE_SITE_WIDE]: isBBPromoCodeSiteWideEnabled,
    [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: isPromotionsInHotelAvailabilityEnabled,
  } = featureToggles;
  const isBarrierFreeLabelEnabled =
    language === CountryCode.DE ? isBarrierFreeLabelEnabledFT : false;
  const enabledNewSortOrderDropdown = {
    isBbSortOrderDropdownEnabled: isBbSortOrderDropdownEnabled,
  };

  const multiSearchParams = getSearchParams(query, false, enabledNewSortOrderDropdown);
  const client = getGQLClient(sessionId);

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
  const rcPriceModifier = rcPriceModifierCookie ? parseFloat(rcPriceModifierCookie) : 1;
  const rcDistanceModifier = rcDistanceModifierCookie ? parseFloat(rcDistanceModifierCookie) : 1;

  const sortOption = {
    rcPriceModifier: rcPriceModifier,
    rcDistanceModifier: rcDistanceModifier,
  };

  const searchResultsPageSize = await getSearchResultsPageSize(language, country, queryClient);

  const paramsForQuery = {
    startDate: startDate && format(startDate, 'yyyy-MM-dd'),
    endDate: endDate && format(endDate, 'yyyy-MM-dd'),
    rooms: multiSearchParams.rooms,
    place,
    oldWorldChannel: multiSearchParams.bookingChannel,
    channel: APP_VARIANT.BB,
    subChannel: multiSearchParams.bookingChannel,
    page: 1,
    initialPageSize: Number(searchResultsPageSize?.initialPageSize ?? 40),
    lazyLoadPageSize: Number(searchResultsPageSize?.lazyLoadPageSize ?? 10),
    country,
    language,
    sort: multiSearchParams.sort,
    sortOption,
    filters: multiSearchParams.filters,
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
  if (isBBPromoCodeLandingPageEnabled) {
    const shouldCallPromoInfoAPI =
      Boolean(promoId || isBBPromoCodeSiteWideEnabled) && !isPromotionsInHotelAvailabilityEnabled;

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
    'hotelAvailabilities',
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

  if (rcPriceModifier && rcDistanceModifier) {
    hotelAvailabilitiesQueryKey.push(rcPriceModifier, rcDistanceModifier);
  }

  const hotelAvailabilitiesQuery = prefetchQuery(hotelAvailabilitiesQueryKey, () =>
    graphQLRequest(
      MULTI_HOTEL_AVAILABILITIES_QUERY,
      {
        ...paramsForQuery,
        idToken: idTokenCookie,
        ...(isPromotionsInHotelAvailabilityEnabled && {
          promotionCode: promoId,
        }),
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
        site: SITE_BB,
        businessBooker: true,
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

  const searchRulesQuery = prefetchQuery(['getSearchRules', 'BB'], () =>
    graphQLRequest(
      GET_SEARCH_RULES_QUERY,
      {
        channel: 'BB',
      },
      undefined,
      undefined,
      client
    )
  );

  const innBusinessServerSideProps = getInnBusinessServerSideProps(idTokenCookie, language, false, {
    ...req.headers,
    ...(sessionId && { [WB_SESSION_ID]: sessionId }),
  });

  const [innBusinessResult] = await Promise.allSettled([
    isInnBusinessAppPage ? innBusinessServerSideProps : Promise.resolve(undefined),
    getStaticContentQuery,
    hotelAvailabilitiesQuery,
    searchInformationQuery,
    searchRulesQuery,
    seoQuery,
  ]);

  const innBusiness =
    innBusinessResult.status === 'fulfilled' ? innBusinessResult.value : undefined;

  logQueries(performance.now());

  return {
    dehydratedState: dehydrate(queryClient),
    innBusiness: innBusiness,
    ...(fallbackSearchPlace && { fallbackSearchPlace }),
    promoInformationQuery,
  };
};

export default createSearchResultsBBDataLoader;
