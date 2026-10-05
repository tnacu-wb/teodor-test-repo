import { dehydrate, QueryClient } from '@tanstack/react-query';
import {
  CCUI_ROLES,
  CountryCode,
  FT_CCUI_PROMO_CODE_LANDING_PAGE,
  FT_CCUI_PROMO_CODE_SITE_WIDE,
  FT_PI_BB_CCUI_BARRIER_FREE_LABEL,
  GET_SEARCH_RULES_QUERY,
  getStaticContent,
  getSuggestions,
  MULTI_HOTEL_AVAILABILITIES_QUERY,
  SEARCH_COMPANY_BY_ID,
  SEARCH_INFORMATION_RESULTS,
  FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY,
  SITE_LEISURE,
} from '@whitbread-eos/api';
import { APP_VARIANT, getSearchParams, getFallbackSearchPlace } from '@whitbread-eos/organisms';
import {
  getDistanceUnitBasedOnLocale,
  graphQLRequest,
  QueriesLogger,
  isDateValid,
  logger,
  getSearchResultsPageSize,
  getGQLClient,
  WB_SESSION_ID,
  getPromoId,
} from '@whitbread-eos/utils';
import Cookies from 'cookies';
import { add, format } from 'date-fns';
import { GetServerSidePropsContext } from 'next';
import { ParsedUrlQuery } from 'querystring';

interface Props extends GetServerSidePropsContext {
  session: any;
  queryClient: QueryClient;
  language: string;
  country: string;
  query: ParsedUrlQuery;
  proxyOptions: any;
  featureToggles: { [key: string]: boolean };
}

const createSearchResultsCCUIDataLoader = async ({
  session,
  queryClient,
  language,
  country,
  query,
  proxyOptions,
  req,
  res,
  featureToggles,
}: Props) => {
  const cookies = new Cookies(req, res);
  const sessionId = cookies.get(WB_SESSION_ID);
  const { prefetchQuery, logQueries } = new QueriesLogger(
    queryClient,
    req,
    res,
    query,
    'CCUI | SRP | Search Results Page',
    session?.user.name
  );
  const client = getGQLClient(sessionId);

  const user = session?.user;
  const roles = session?.user[CCUI_ROLES];

  const {
    [FT_PI_BB_CCUI_BARRIER_FREE_LABEL]: isBarrierFreeLabelEnabledFT,
    [FT_CCUI_PROMO_CODE_LANDING_PAGE]: isCcuiPromoCodeLandingPageEnabled,
    [FT_CCUI_PROMO_CODE_SITE_WIDE]: isCcuiPromoCodeSiteWideEnabled,
    [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: isPromotionsInHotelAvailabilityEnabled,
  } = featureToggles;
  const isBarrierFreeLabelEnabled =
    language === CountryCode.DE ? isBarrierFreeLabelEnabledFT : false;

  const multiSearchParams = getSearchParams(query);
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
    : new Date();

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

  const endDate = add(startDate, { days: Number(multiSearchParams.numberOfNights) });

  const searchResultsPageSize = await getSearchResultsPageSize(language, country, queryClient);

  const paramsForQuery = {
    startDate: format(startDate, 'yyyy-MM-dd'),
    endDate: format(endDate, 'yyyy-MM-dd'),
    rooms: multiSearchParams.rooms,
    place,
    oldWorldChannel: multiSearchParams.bookingChannel,
    channel: APP_VARIANT.CCUI,
    subChannel: multiSearchParams.bookingChannel,
    page: 1,
    initialPageSize: Number(searchResultsPageSize?.initialPageSize ?? 40),
    lazyLoadPageSize: Number(searchResultsPageSize?.lazyLoadPageSize ?? 10),
    country,
    language,
    sort: multiSearchParams.sort,
    filters: multiSearchParams.filters,
    companyId: multiSearchParams.corpId,
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
        proxyOptions,
        client
      )
  );

  const promoId = getPromoId(query?.PROMOID);
  let promoInformationQuery = null;
  if (isCcuiPromoCodeLandingPageEnabled) {
    const shouldCallPromoInfoAPI =
      Boolean(promoId || isCcuiPromoCodeSiteWideEnabled) && !isPromotionsInHotelAvailabilityEnabled;
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

  const hotelAvailablilitiesQueryKey = [
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

  const hotelAvailabilitiesCcuiQuery = prefetchQuery(
    paramsForQuery.companyId
      ? [...hotelAvailablilitiesQueryKey, paramsForQuery.companyId]
      : hotelAvailablilitiesQueryKey,
    () =>
      graphQLRequest(
        MULTI_HOTEL_AVAILABILITIES_QUERY,
        {
          ...paramsForQuery,
          ...(isPromotionsInHotelAvailabilityEnabled &&
            !paramsForQuery.companyId && {
              promotionCode: promoId,
            }),
        },
        undefined,
        proxyOptions,
        client
      )
  );

  const staticContentQuery = getStaticContent(isBarrierFreeLabelEnabled);
  const getStaticContentQuery = prefetchQuery(['GetStaticContent', language, country], () =>
    graphQLRequest(
      staticContentQuery,
      {
        language,
        country,
        site: SITE_LEISURE,
        businessBooker: false,
      },
      undefined,
      proxyOptions,
      client
    )
  );

  const searchRulesQuery = prefetchQuery(['getSearchRules', 'CCUI'], () =>
    graphQLRequest(
      GET_SEARCH_RULES_QUERY,
      {
        channel: 'CCUI',
      },
      undefined,
      undefined,
      client
    )
  );

  const { corpId } = multiSearchParams;
  if (corpId) {
    try {
      await prefetchQuery(['searchCompanyById', corpId], () =>
        graphQLRequest(
          SEARCH_COMPANY_BY_ID,
          {
            corpId,
          },
          undefined,
          undefined,
          client
        )
      );
    } catch (error) {
      logger.error(error);
    }
  }

  const promises = [
    getStaticContentQuery,
    hotelAvailabilitiesCcuiQuery,
    searchInformationQuery,
    searchRulesQuery,
  ];

  await Promise.all(promises);

  logQueries(performance.now());

  return {
    dehydratedState: dehydrate(queryClient),
    user,
    roles,
    promoInformationQuery,
  };
};

export default createSearchResultsCCUIDataLoader;
