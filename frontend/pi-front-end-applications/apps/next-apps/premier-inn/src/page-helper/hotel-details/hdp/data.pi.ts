import { dehydrate, QueryClient } from '@tanstack/react-query';
import {
  BOOKING_CHANNEL,
  GET_SEO_INFORMATION,
  HOTEL_AVAILABILITY_QUERY,
  PageName,
  SITE_LEISURE,
  STATIC_HOTEL_INFORMATION_QUERY,
  PROMO_CODE_COOKIE,
  DISCOUNT_RATE_INFORMATION_QUERY,
  HIRoomRate,
  SEARCH_COMPANY_BY_ID,
  HOTEL_AVAILABILITY_DISCOUNT_RATE_QUERY,
  FT_PI_AUTH0_LOGIN,
  FT_PI_PROMO_CODE_LANDING_PAGE,
  FT_PI_PROMO_CODE_SITE_WIDE,
  GET_GLOBAL_CONFIG_QUERY,
  getStaticContent,
  FT_PI_BB_CCUI_BARRIER_FREE_LABEL,
  CountryCode,
  GET_PROMO_INFORMATION,
  FT_PI_NO_ROOM_TYPE_SEARCH,
  FT_PI_DISPLAY_SOFT_BUNDLES,
  GET_ROOM_TYPE_INFORMATION_QUERY,
  FT_PI_CCUI_BB_PROMOTION_BOX_CODE_COOKIE,
  FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY,
} from '@whitbread-eos/api';
import {
  axiosRequest,
  decodeIdToken,
  ID_TOKEN_COOKIE,
  getAvailabilityParamsFromUrl,
  getStaticHotelInformationQueryDateDataFromUrl,
  getGQLClient,
  getHotelAvailabilityQueryKey,
  graphQLRequest,
  QueriesLogger,
  logger,
  getActivePromotionCode,
  WB_SESSION_ID,
  BUNDLE_CHOICE,
  BUNDLE_CHOICE_OPTIONS,
} from '@whitbread-eos/utils';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';

import { getAuth0TokenAndEmail } from '../../../lib/getAuth0Token';

interface Props extends GetServerSidePropsContext {
  queryClient: QueryClient;
  language: string;
  country: string;
  featureToggles: { [key: string]: boolean };
}

const createHDPPiDataLoaderFn = async ({
  queryClient,
  language,
  country,
  featureToggles,
  params,
  req,
  res,
  query,
}: Props) => {
  const cookies = new Cookies(req, res);
  const sessionId = cookies.get(WB_SESSION_ID);
  const client = getGQLClient(sessionId);

  const isAuth0Enabled = featureToggles[FT_PI_AUTH0_LOGIN] ?? false;
  let authToken: string | undefined;
  let userEmail: string | undefined;

  if (isAuth0Enabled) {
    const { accessToken, email } = await getAuth0TokenAndEmail(req);
    authToken = accessToken ?? undefined;
    userEmail = email ?? undefined;
  } else {
    const idTokenCookie = cookies.get(ID_TOKEN_COOKIE);
    authToken = idTokenCookie;
    userEmail = idTokenCookie ? decodeIdToken(idTokenCookie).email : undefined;
  }

  const { prefetchQuery, fetchQuery, logQueries } = new QueriesLogger(
    queryClient,
    req,
    res,
    query,
    'PI | HDP | Hotel Details Page'
  );

  const {
    [FT_PI_PROMO_CODE_LANDING_PAGE]: isPromoCodeLandingPageEnabled,
    [FT_PI_PROMO_CODE_SITE_WIDE]: isPromoCodeSiteWideEnabled,
    [FT_PI_BB_CCUI_BARRIER_FREE_LABEL]: isBarrierFreeLabelEnabledFT,
    [FT_PI_NO_ROOM_TYPE_SEARCH]: isNoRoomTypeSearchEnabled,
    [FT_PI_DISPLAY_SOFT_BUNDLES]: isSoftBundlesEnabled,
    [FT_PI_CCUI_BB_PROMOTION_BOX_CODE_COOKIE]: isPromoBoxAppliedCodeCookieEnabled,
    [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: isPromotionsInHotelAvailabilityEnabled,
  } = featureToggles;

  const targetVariant = cookies.get(BUNDLE_CHOICE);
  const noRoomTypeSearch =
    isNoRoomTypeSearchEnabled && targetVariant === BUNDLE_CHOICE_OPTIONS.noRoomTypeSearch;
  const { arrival, departure, rooms, empOfferCodes, numberOfNights, corpId, promoId } =
    getAvailabilityParamsFromUrl(req.url!.split('?')[1], noRoomTypeSearch);
  const { staticHotelInformationQueryKeyDates, staticHotelInformationQueryPayloadDates } =
    getStaticHotelInformationQueryDateDataFromUrl(req.url!.split('?')[1]);
  const softBundle =
    isSoftBundlesEnabled &&
    (empOfferCodes ?? []).length === 0 &&
    (targetVariant === BUNDLE_CHOICE_OPTIONS.class ||
      targetVariant === BUNDLE_CHOICE_OPTIONS.rate ||
      targetVariant === BUNDLE_CHOICE_OPTIONS.roomOnly)
      ? targetVariant
      : null;
  const isBarrierFreeLabelEnabled =
    language === CountryCode.DE ? isBarrierFreeLabelEnabledFT : false;

  const slugItems = params?.slug as string[];
  const slug = ['/hotels', ...slugItems].join('/');

  const staticHotelData = await fetchQuery(
    ['staticHotelInformation', language, country, slug, ...staticHotelInformationQueryKeyDates],
    () =>
      graphQLRequest(
        STATIC_HOTEL_INFORMATION_QUERY,
        {
          slug,
          language,
          country,
          ...staticHotelInformationQueryPayloadDates,
        },
        undefined,
        undefined,
        client
      )
  );

  /* get necessary data to prefetch availability */
  const { hotelId, brand } = staticHotelData.hotelInformationBySlug;

  const cookieBasedPromoCode = cookies.get(PROMO_CODE_COOKIE);

  const cookieBasedPromoBoxAppliedCode =
    isPromoBoxAppliedCodeCookieEnabled && cookies.get('appliedPromoBoxCode')
      ? cookies.get('appliedPromoBoxCode')
      : '';

  let landingPagePromoCode = isPromoCodeLandingPageEnabled && promoId ? promoId : '';

  const PromoId = landingPagePromoCode?.trim() || cookieBasedPromoBoxAppliedCode;
  if (
    (isPromoCodeLandingPageEnabled || isPromoBoxAppliedCodeCookieEnabled) &&
    !cookieBasedPromoCode
  ) {
    const shouldCallPromoInfoAPI =
      promoId || isPromoCodeSiteWideEnabled || cookieBasedPromoBoxAppliedCode;

    if (shouldCallPromoInfoAPI && !isPromotionsInHotelAvailabilityEnabled) {
      const brandName = brand?.toUpperCase() as string;

      const promoInformationQuery = await fetchQuery(
        [
          'promotionsInformation',
          country,
          language,
          BOOKING_CHANNEL.PI,
          brandName as string,
          PromoId as string,
          '',
          arrival,
          departure,
          !!cookieBasedPromoBoxAppliedCode,
        ],
        () =>
          graphQLRequest(
            GET_PROMO_INFORMATION,
            {
              country,
              language: language,
              channel: BOOKING_CHANNEL.PI,
              brand: brandName as string,
              promotionCode: PromoId as string,
              bookingDate: '',
              stayStartDate: arrival,
              stayEndDate: departure,
              isPromoBox: !!cookieBasedPromoBoxAppliedCode,
            },
            undefined,
            undefined,
            client
          )
      );
      landingPagePromoCode = getActivePromotionCode(
        promoInformationQuery?.promotionsInformation,
        promoId as string
      );
    }
  }
  let hotelAvailabilityQuery;
  if (numberOfNights !== 0 && !corpId) {
    const availabilityPromoCode = isPromotionsInHotelAvailabilityEnabled
      ? PromoId
      : landingPagePromoCode;
    const isPromoBoxValue =
      !isPromotionsInHotelAvailabilityEnabled || cookieBasedPromoCode ? undefined : false;

    const promotionCode = availabilityPromoCode || (cookies.get(PROMO_CODE_COOKIE) as string);

    hotelAvailabilityQuery = prefetchQuery(
      getHotelAvailabilityQueryKey(
        language,
        country,
        brand,
        hotelId,
        arrival,
        departure,
        rooms,
        empOfferCodes,
        promotionCode,
        undefined,
        softBundle ?? undefined
      ),
      () =>
        graphQLRequest(
          HOTEL_AVAILABILITY_QUERY,
          {
            hotelId,
            arrival,
            departure,
            rooms,
            brand: brand.toLowerCase(),
            language,
            country,
            bookingChannel: {
              channel: BOOKING_CHANNEL.PI,
              language: language?.toUpperCase(),
              subchannel: 'WEB',
            },
            channel: BOOKING_CHANNEL.PI,
            ratePlanCodes: empOfferCodes,
            promotionCode: promotionCode,
            isPromoBox: isPromoBoxValue,
            softBundle:
              softBundle === BUNDLE_CHOICE_OPTIONS.roomOnly
                ? BUNDLE_CHOICE_OPTIONS.rate
                : softBundle,
          },
          undefined,
          undefined,
          client
        )
    );
  }

  let ratesInformationQuery;
  if (numberOfNights !== 0 && corpId) {
    let searchCompanyByIdResult;
    try {
      searchCompanyByIdResult = await fetchQuery(['searchCompanyById', corpId], () =>
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
    const { companyId } = searchCompanyByIdResult?.companyProfile ?? {};

    const hotelAvailabilityResult = await fetchQuery(
      [
        'hotelAvailabilityDiscountRate',
        language,
        country,
        brand.toLowerCase(),
        hotelId,
        companyId,
        arrival,
        departure,
        JSON.stringify(rooms),
        BOOKING_CHANNEL.PI,
      ].filter(Boolean),
      () =>
        graphQLRequest(
          HOTEL_AVAILABILITY_DISCOUNT_RATE_QUERY,
          {
            hotelId,
            arrival,
            departure,
            rooms,
            brand: brand.toLowerCase(),
            language,
            country,
            companyId,
            bookingChannel: {
              channel: BOOKING_CHANNEL.PI,
              language: language?.toUpperCase(),
              subchannel: 'WEB',
            },
            channel: BOOKING_CHANNEL.PI,
          },
          undefined,
          undefined,
          client
        )
    );

    const ratePlanCodes = hotelAvailabilityResult?.hotelAvailability?.roomRates.map(
      (roomRate: HIRoomRate) => roomRate.ratePlanCode
    );

    ratesInformationQuery = prefetchQuery(
      [
        'ratesInformationDiscountRate',
        language,
        country,
        brand.toLowerCase(),
        hotelId,
        BOOKING_CHANNEL.PI,
        ratePlanCodes,
      ],
      () =>
        graphQLRequest(
          DISCOUNT_RATE_INFORMATION_QUERY,
          {
            hotelId,
            brand: brand.toLowerCase(),
            language,
            country,
            channel: BOOKING_CHANNEL.PI,
            ratePlans: ratePlanCodes?.toString(),
          },
          undefined,
          undefined,
          client
        )
    );
  }

  const getGlobalConfigQuery = prefetchQuery(
    ['getGlobalConfig', brand, BOOKING_CHANNEL.PI, country, language],
    () =>
      graphQLRequest(
        GET_GLOBAL_CONFIG_QUERY,
        {
          brand,
          channel: BOOKING_CHANNEL.PI,
          country,
          language,
        },
        undefined,
        undefined,
        client
      ),
    { enabled: true }
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

  const seoQuery = prefetchQuery(['seoInformation', language, country, PageName.HDP, hotelId], () =>
    graphQLRequest(
      GET_SEO_INFORMATION,
      {
        language,
        country,
        page: PageName.HDP,
        hotelId,
      },
      undefined,
      undefined,
      client
    )
  );

  const roomTypeInformationQuery = prefetchQuery(
    ['getRoomTypeInformation', language, country, brand, hotelId],
    () =>
      graphQLRequest(
        GET_ROOM_TYPE_INFORMATION_QUERY,
        {
          language,
          country,
          brand,
          hotelId,
        },
        undefined,
        undefined,
        client
      )
  );

  const promises = [
    getStaticContentQuery,
    seoQuery,
    hotelAvailabilityQuery,
    roomTypeInformationQuery,
  ];
  if (corpId) {
    promises.push(ratesInformationQuery);
  }

  promises.push(getGlobalConfigQuery);

  if (authToken && userEmail) {
    const axiosProps = {
      method: 'GET',
      url: `${process.env.NEXT_PUBLIC_ACCOUNT_SERVICE}/customers/hotels/${userEmail}?business=false`,
      headers: {
        Authorization: `Bearer ${authToken}`,
        [WB_SESSION_ID]: sessionId,
      },
    };

    const userDetailsQuery = prefetchQuery(['userDetails', authToken], () =>
      axiosRequest(axiosProps)
    );
    promises.push(userDetailsQuery);
  }

  await Promise.all(promises);

  logQueries(performance.now());

  return {
    dehydratedState: dehydrate(queryClient),
  };
};

export default createHDPPiDataLoaderFn;
