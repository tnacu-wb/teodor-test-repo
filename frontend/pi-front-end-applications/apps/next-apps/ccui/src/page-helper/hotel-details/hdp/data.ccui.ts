import { dehydrate, QueryClient } from '@tanstack/react-query';
import {
  QueryBookingInformationArgs,
  BIResponse,
  BOOKING_CHANNEL,
  BookingInformation,
  GET_BOOKING_INFORMATION,
  GET_PACKAGES,
  HIRoomRate,
  HOTEL_AVAILABILITY_CCUI_QUERY,
  PackagesCriteria,
  RATE_INFORMATION_CCUI_QUERY,
  SEARCH_COMPANY_BY_ID,
  SITE_LEISURE,
  STATIC_HOTEL_INFORMATION_QUERY,
  Channel,
  FT_PI_BB_CCUI_BARRIER_FREE_LABEL,
  getStaticContent,
  CountryCode,
  FT_CCUI_PROMO_CODE_LANDING_PAGE,
  FT_CCUI_PROMO_CODE_SITE_WIDE,
  GET_PROMO_INFORMATION,
  FT_PI_CCUI_BB_PROMOTION_BOX_CODE_COOKIE,
  FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY,
} from '@whitbread-eos/api';
import {
  getAvailabilityParamsFromUrl,
  getStaticHotelInformationQueryDateDataFromUrl,
  getGQLClient,
  getMaxValueFromRoomStays,
  getNightsNumber,
  graphQLRequest,
  logger,
  QueriesLogger,
  getActivePromotionCode,
  WB_SESSION_ID,
} from '@whitbread-eos/utils';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';

interface Props extends GetServerSidePropsContext {
  queryClient: QueryClient;
  session: any;
  language: string;
  country: string;
  proxyOptions: any;
  featureToggles: { [key: string]: boolean };
}

const createHDPCcuiDataLoaderFn = async ({
  queryClient,
  session,
  language,
  country,
  params,
  req,
  res,
  proxyOptions,
  query,
  featureToggles,
}: Props) => {
  const cookies = new Cookies(req, res);
  const sessionId = cookies.get(WB_SESSION_ID);
  const client = getGQLClient(sessionId);
  const { reservationId } = query;

  const { prefetchQuery, fetchQuery, logQueries } = new QueriesLogger(
    queryClient,
    req,
    res,
    query,
    'CCUI | HDP | Hotel Details Page',
    session?.user.name
  );
  const accessToken = session?.tokenSet?.accessToken;
  const user = session?.user;
  const slugItems = params?.slug as string[];
  const slug = ['/hotels', ...slugItems].join('/');
  const channel = BOOKING_CHANNEL.CCUI;
  const {
    [FT_PI_BB_CCUI_BARRIER_FREE_LABEL]: isBarrierFreeLabelEnabledFT,
    [FT_CCUI_PROMO_CODE_LANDING_PAGE]: isCcuiPromoCodeLandingPageEnabled,
    [FT_CCUI_PROMO_CODE_SITE_WIDE]: isCcuiPromoCodeSiteWideEnabled,
    [FT_PI_CCUI_BB_PROMOTION_BOX_CODE_COOKIE]: isPromoBoxAppliedCodeCookieEnabled,
    [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: isPromotionsInHotelAvailabilityEnabled,
  } = featureToggles;
  const isBarrierFreeLabelEnabled =
    language === CountryCode.DE ? isBarrierFreeLabelEnabledFT : false;

  let pcksQuery = null;
  if (reservationId) {
    const basketReference = String(reservationId);
    const biQueryInput: QueryBookingInformationArgs = {
      basketReference: basketReference,
      country,
      language,
      bookingChannelCriteria: {
        channel: Channel.Ccui,
        subchannel: 'WEB',
        language: language === 'en' ? 'EN' : 'DE',
      },
    };
    const { bookingInformation }: BIResponse = await fetchQuery(
      ['GetBookingInformation', language, country, basketReference],
      () => graphQLRequest(GET_BOOKING_INFORMATION, biQueryInput, undefined, proxyOptions, client)
    );
    const { hotelId, reservationByIdList, bookingFlowId }: BookingInformation = bookingInformation;

    const startDate = reservationByIdList?.[0]?.roomStay?.arrivalDate ?? '';
    const endDate = reservationByIdList?.[0]?.roomStay?.departureDate ?? '';

    const pcksQueryInput: PackagesCriteria = {
      country,
      language,
      hotelId: hotelId ?? '',
      adultsNumber: getMaxValueFromRoomStays(reservationByIdList ?? [], 'adultsNumber'),
      childrenNumber: getMaxValueFromRoomStays(reservationByIdList ?? [], 'childrenNumber'),
      startDate: startDate,
      endDate: reservationByIdList?.[0]?.roomStay?.departureDate ?? '',
      bookingFlowId: bookingFlowId ?? '',
      nightsNumber: getNightsNumber(startDate, endDate),
      basketReferenceId: basketReference,
    };
    pcksQuery = await prefetchQuery(
      [
        'GetPackages',
        pcksQueryInput.language,
        pcksQueryInput.country,
        pcksQueryInput.hotelId,
        pcksQueryInput.bookingFlowId,
        pcksQueryInput.startDate,
        pcksQueryInput.endDate,
        pcksQueryInput.nightsNumber,
        pcksQueryInput.adultsNumber,
        pcksQueryInput.childrenNumber,
        basketReference,
      ],
      () => graphQLRequest(GET_PACKAGES, { ...pcksQueryInput }, undefined, proxyOptions, client)
    );
  }

  const { arrival, departure, rooms, corpId, numberOfNights, promoId } =
    getAvailabilityParamsFromUrl(req.url!.split('?')[1]);
  const { staticHotelInformationQueryKeyDates, staticHotelInformationQueryPayloadDates } =
    getStaticHotelInformationQueryDateDataFromUrl(req.url!.split('?')[1]);
  const staticData = await fetchQuery(
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
        accessToken,
        proxyOptions,
        client
      )
  );

  /* get necessary data to prefetch availability */
  const { hotelId, brand } = staticData.hotelInformationBySlug;
  let ccuiLandingPagePromoCode = isCcuiPromoCodeLandingPageEnabled && promoId ? promoId : '';
  const cookieBasedPromoBoxAppliedCode =
    isPromoBoxAppliedCodeCookieEnabled && cookies.get('appliedPromoBoxCode')
      ? cookies.get('appliedPromoBoxCode')
      : '';

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

  const promises = [getStaticContentQuery, pcksQuery];

  let searchCompanyByIdResult;
  if (corpId) {
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
  }
  const PromoId = ccuiLandingPagePromoCode?.trim() || cookieBasedPromoBoxAppliedCode;
  if (isCcuiPromoCodeLandingPageEnabled || isPromoBoxAppliedCodeCookieEnabled) {
    const shouldCallPromoInfoAPI =
      PromoId || isCcuiPromoCodeSiteWideEnabled || cookieBasedPromoBoxAppliedCode;
    if (shouldCallPromoInfoAPI && !isPromotionsInHotelAvailabilityEnabled) {
      const brandName = brand?.toUpperCase() as string;
      const promoInformationQuery = await fetchQuery(
        [
          'promotionsInformation',
          country,
          language,
          BOOKING_CHANNEL.CCUI,
          brandName,
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
              channel: BOOKING_CHANNEL.CCUI,
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
      ccuiLandingPagePromoCode = getActivePromotionCode(
        promoInformationQuery?.promotionsInformation,
        PromoId as string
      );
    }
  }

  if (numberOfNights !== 0) {
    const { companyId } = searchCompanyByIdResult?.companyProfile ?? {};
    const availabilityPromoCode =
      isPromotionsInHotelAvailabilityEnabled && !corpId ? PromoId : ccuiLandingPagePromoCode;
    const isPromoBoxValue = !isPromotionsInHotelAvailabilityEnabled || corpId ? undefined : false;

    const hotelAvailabilityResult = await fetchQuery(
      [
        'hotelAvailabilityCCUI',
        language,
        country,
        brand.toLowerCase(),
        hotelId,
        companyId,
        arrival,
        departure,
        JSON.stringify(rooms),
        channel,
        corpId ? '' : availabilityPromoCode,
        isPromoBoxValue,
      ].filter(Boolean),
      () =>
        graphQLRequest(
          HOTEL_AVAILABILITY_CCUI_QUERY,
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
              channel,
              language: language?.toUpperCase(),
              subchannel: 'WEB',
            },
            channel,
            promotionCode: corpId ? '' : availabilityPromoCode,
            isPromoBox: isPromoBoxValue,
          },
          undefined,
          proxyOptions,
          client
        )
    );

    const ratePlanCodes = hotelAvailabilityResult?.hotelAvailability?.roomRates.map(
      (roomRate: HIRoomRate) => roomRate.ratePlanCode
    );

    //ratesInformationQuery
    prefetchQuery(
      [
        'ratesInformationCCUI',
        language,
        country,
        brand.toLowerCase(),
        hotelId,
        channel,
        ratePlanCodes,
      ],
      () =>
        graphQLRequest(
          RATE_INFORMATION_CCUI_QUERY,
          {
            hotelId,
            brand: brand.toLowerCase(),
            language,
            country,
            channel,
            ratePlans: ratePlanCodes?.toString(),
          },
          undefined,
          undefined,
          client
        )
    );
  }
  await Promise.all(promises);

  logQueries(performance.now());

  return {
    dehydratedState: dehydrate(queryClient),
    accessToken,
    user,
  };
};

export default createHDPCcuiDataLoaderFn;
