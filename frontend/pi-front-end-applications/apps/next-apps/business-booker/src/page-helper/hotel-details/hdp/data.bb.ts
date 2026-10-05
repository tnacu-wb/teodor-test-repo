import { dehydrate, QueryClient } from '@tanstack/react-query';
import type { HIRoomRate } from '@whitbread-eos/api';
import {
  BOOKING_CHANNEL,
  CountryCode,
  FT_PI_BB_CCUI_BARRIER_FREE_LABEL,
  GET_SEO_INFORMATION,
  getStaticContent,
  HOTEL_AVAILABILITY_BB_QUERY,
  PageName,
  RATE_INFORMATION_BB_QUERY,
  SITE_BB,
  STATIC_HOTEL_INFORMATION_QUERY,
  FT_BB_PROMO_CODE_LANDING_PAGE,
  FT_BB_PROMO_CODE_SITE_WIDE,
  GET_PROMO_INFORMATION,
  FT_PI_CCUI_BB_PROMOTION_BOX_CODE_COOKIE,
  FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY,
} from '@whitbread-eos/api';
import {
  axiosRequest,
  decodeIdToken,
  ID_TOKEN_COOKIE,
  getAvailabilityParamsFromUrl,
  getStaticHotelInformationQueryDateDataFromUrl,
  getLoggedInUserInfo,
  graphQLRequest,
  QueriesLogger,
  getActivePromotionCode,
  getGQLClient,
  WB_SESSION_ID,
} from '@whitbread-eos/utils';
import { getInnBusinessServerSideProps } from '@whitbread-eos/utils/server';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';

interface Props extends GetServerSidePropsContext {
  queryClient: QueryClient;
  language: string;
  country: string;
  featureToggles: {
    [key: string]: boolean;
  };
  isInnBusinessAppPage?: boolean;
}

const createHDPBbDataLoaderFn = async ({
  queryClient,
  language,
  country,
  query,
  params,
  req,
  res,
  featureToggles,
  isInnBusinessAppPage = false,
}: Props) => {
  const slugItems = params?.slug as string[];
  const slug = ['/hotels', ...slugItems].join('/');

  const cookies = new Cookies(req, res);
  const idTokenCookie = cookies.get(ID_TOKEN_COOKIE);
  const sessionId = cookies.get(WB_SESSION_ID);

  const { prefetchQuery, fetchQuery, logQueries } = new QueriesLogger(
    queryClient,
    req,
    res,
    query,
    'BB | HDP | Hotel Details Page'
  );
  const {
    [FT_PI_BB_CCUI_BARRIER_FREE_LABEL]: isBarrierFreeLabelEnabledFT,
    [FT_BB_PROMO_CODE_LANDING_PAGE]: isPromoCodeLandingPageEnabled,
    [FT_BB_PROMO_CODE_SITE_WIDE]: isPromoCodeSiteWideEnabled,
    [FT_PI_CCUI_BB_PROMOTION_BOX_CODE_COOKIE]: isPromoBoxAppliedCodeCookieEnabled,
    [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: isPromotionsInHotelAvailabilityEnabled,
  } = featureToggles;
  const client = getGQLClient(sessionId);
  const { arrival, departure, rooms, numberOfNights, promoId } = getAvailabilityParamsFromUrl(
    req.url!.split('?')[1]
  );
  const { staticHotelInformationQueryKeyDates, staticHotelInformationQueryPayloadDates } =
    getStaticHotelInformationQueryDateDataFromUrl(req.url!.split('?')[1]);
  const isBarrierFreeLabelEnabled =
    language === CountryCode.DE ? isBarrierFreeLabelEnabledFT : false;
  const staticHotelQuery = fetchQuery(
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
  const innBusinessServerSideProps = getInnBusinessServerSideProps(idTokenCookie, language, true, {
    ...req.headers,
    ...(sessionId && { [WB_SESSION_ID]: sessionId }),
  });
  const staticContentQuery = prefetchQuery(['GetStaticContent', language, country], () =>
    graphQLRequest(
      getStaticContent(isBarrierFreeLabelEnabled),
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

  const [staticDataResult, innBusinessResult] = await Promise.allSettled([
    staticHotelQuery,
    isInnBusinessAppPage ? innBusinessServerSideProps : Promise.resolve(undefined),
    staticContentQuery,
  ]);

  const staticData = staticDataResult.status === 'fulfilled' ? staticDataResult.value : {};
  const innBusiness =
    innBusinessResult.status === 'fulfilled' ? innBusinessResult.value : undefined;

  const { hotelId, brand } = staticData?.hotelInformationBySlug ?? { hotelId: '', brand: '' };

  let landingPagePromoCode = isPromoCodeLandingPageEnabled && promoId ? promoId : '';
  const cookieBasedPromoBoxAppliedCode =
    isPromoBoxAppliedCodeCookieEnabled && cookies.get('appliedPromoBoxCode')
      ? cookies.get('appliedPromoBoxCode')
      : '';
  const PromoId = landingPagePromoCode?.trim() || cookieBasedPromoBoxAppliedCode;

  if (isPromoCodeLandingPageEnabled || isPromoBoxAppliedCodeCookieEnabled) {
    const shouldCallPromoInfoAPI =
      promoId || isPromoCodeSiteWideEnabled || cookieBasedPromoBoxAppliedCode;

    if (shouldCallPromoInfoAPI && !isPromotionsInHotelAvailabilityEnabled) {
      const brandName = brand?.toUpperCase() as string;

      const promoInformationQuery = await fetchQuery(
        [
          'promotionsInformation',
          country,
          language,
          BOOKING_CHANNEL.BB,
          brandName,
          PromoId,
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
              channel: BOOKING_CHANNEL.BB,
              brand: brandName,
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
        PromoId as string
      );
    }
  }

  if (idTokenCookie) {
    const { email } = decodeIdToken(idTokenCookie) || {};

    const axiosProps = {
      method: 'GET',
      url: `${process.env.NEXT_PUBLIC_ACCOUNT_SERVICE}/customers/hotels/${email}?business=true`,
      headers: {
        Authorization: `Bearer ${idTokenCookie}`,
        [WB_SESSION_ID]: sessionId,
      },
    };

    await prefetchQuery(['userDetails', idTokenCookie], () => axiosRequest(axiosProps));

    const userData = getLoggedInUserInfo(idTokenCookie);
    const companyId = userData.operaCompanyId || '';

    if (numberOfNights !== 0) {
      const availabilityPromoCode =
        isPromotionsInHotelAvailabilityEnabled && !companyId ? PromoId : landingPagePromoCode;
      const isPromoBoxValue =
        !isPromotionsInHotelAvailabilityEnabled || companyId ? undefined : false;

      const queryResult = await fetchQuery(
        [
          'hotelAvailabilityBB',
          language,
          country,
          brand.toLowerCase(),
          hotelId,
          companyId,
          arrival,
          departure,
          JSON.stringify(rooms),
          BOOKING_CHANNEL.BB,
          availabilityPromoCode,
          isPromoBoxValue ?? null,
        ],
        () =>
          graphQLRequest(
            HOTEL_AVAILABILITY_BB_QUERY,
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
                channel: BOOKING_CHANNEL.BB,
                language: language?.toUpperCase(),
                subchannel: 'WEB',
              },
              promotionCode: availabilityPromoCode,
              isPromoBox: isPromoBoxValue,
            },
            idTokenCookie, // replace with access token once change is required
            undefined,
            client
          )
      );
      const ratePlanCodes = queryResult?.hotelAvailability?.roomRates.map(
        (roomRate: HIRoomRate) => roomRate.ratePlanCode
      );

      await prefetchQuery(
        ['ratesInformationBB', language, country, brand.toLowerCase(), hotelId, ratePlanCodes],
        () =>
          graphQLRequest(
            RATE_INFORMATION_BB_QUERY,
            {
              hotelId,
              brand: brand.toLowerCase(),
              language,
              country,
              channel: BOOKING_CHANNEL.BB,
              ratePlans: ratePlanCodes.toString(),
            },
            idTokenCookie, // replace with access token once change is required
            undefined,
            client
          )
      );
    }

    await prefetchQuery(['seoInformation', language, country, PageName.HDP], () =>
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
  }

  logQueries(performance.now());

  return {
    dehydratedState: dehydrate(queryClient),
    innBusiness: innBusiness,
  };
};

export default createHDPBbDataLoaderFn;
