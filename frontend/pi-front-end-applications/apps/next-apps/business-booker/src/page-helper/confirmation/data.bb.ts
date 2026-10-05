import { dehydrate, QueryClient } from '@tanstack/react-query';
import {
  BCQueryInput,
  BCResponse,
  BOOKING_CHANNEL,
  GET_BASKET,
  GET_BOOKING_CONFIRMATION,
  GET_HEADER_BOOKING_FLOW_INFORMATION,
  GET_HOTEL_INFORMATION,
  GET_PACKAGES,
  GET_PROMOTION_PANEL,
  GET_SEO_INFORMATION,
  GET_STATIC_CONTENT,
  HIBasicBasketDetails,
  QueryHotelInformationArgs,
  PageName,
  PackagesCriteria,
  PromotionQueryInput,
  SITE_BB,
  StaticContentQueryInput,
  Channel,
  Language,
} from '@whitbread-eos/api';
import {
  getNightsNumber,
  getMaxValueFromRoomStaysBC,
  graphQLRequest,
  isJsonValid,
  QueriesLogger,
  ID_TOKEN_COOKIE,
  getGQLClient,
  WB_SESSION_ID,
  logger,
} from '@whitbread-eos/utils';
import {
  getInnBusinessServerSideProps,
  RedisKeyPrefix,
  RedisStorageServer,
} from '@whitbread-eos/utils/server';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';

interface Props extends GetServerSidePropsContext {
  queryClient: QueryClient;
  language: string;
  country: string;
  isInnBusinessAppPage?: boolean;
}

export const createConfirmationBbDataLoaderFn = async ({
  req,
  res,
  language,
  country,
  query,
  queryClient,
  isInnBusinessAppPage = false,
}: Props) => {
  const cookies = new Cookies(req, res);
  const idTokenCookie = cookies.get(ID_TOKEN_COOKIE);
  const sessionId = cookies.get(WB_SESSION_ID);
  const client = getGQLClient(sessionId);

  const basketReference = query?.reservationId ? String(query.reservationId) : '';
  let basicBasketDetails: string | null = null;
  let redisStorage = null;
  const redisKey = RedisKeyPrefix.HRS + '::' + basketReference;
  try {
    redisStorage = RedisStorageServer.getInstance();
    basicBasketDetails = await redisStorage.getItem(redisKey);
  } catch (error) {
    logger.error({ error }, 'CONFIRMATION_PAGE_REDIS_ERROR');
  }

  const hasCachedBasketDetails = !!(basicBasketDetails && isJsonValid(basicBasketDetails));
  let basketDetails: HIBasicBasketDetails = hasCachedBasketDetails
    ? JSON.parse(basicBasketDetails || '{}')
    : null;

  const { prefetchQuery, fetchQuery, logQueries } = new QueriesLogger(
    queryClient,
    req,
    res,
    query,
    'BB | Confirmation Page'
  );

  const biQueryInput: BCQueryInput = {
    basketReference: basketReference,
    country,
    language,
    bookingChannel: BOOKING_CHANNEL.BB,
  };

  const getBookingConfirmationQuery = fetchQuery(
    ['GetBookingConfirmation', language, country, basketReference, BOOKING_CHANNEL.BB],
    () => graphQLRequest(GET_BOOKING_CONFIRMATION, biQueryInput, undefined, undefined, client)
  );

  if (!hasCachedBasketDetails) {
    logger.info(`No Cache/Redis found in Confirmation ${basketReference}, Fetching from GraphQL`);
    const { bookingConfirmation }: BCResponse = await getBookingConfirmationQuery;
    const { hotelId, reservationByIdList, bookingFlowId } = bookingConfirmation;

    const startDate = reservationByIdList?.[0]?.roomStay?.arrivalDate ?? '';
    const endDate = reservationByIdList?.[0]?.roomStay?.departureDate ?? '';

    basketDetails = {
      bookingFlowId: bookingFlowId ?? '',
      hotelId: hotelId ?? '',
      rateCode: reservationByIdList?.[0]?.roomStay?.ratePlanCode ?? '',
      nightsNumber: getNightsNumber(startDate, endDate),
      startDate: startDate,
      endDate: endDate,
      childrenNumber: getMaxValueFromRoomStaysBC(reservationByIdList ?? [], 'childrenNumber'),
      adultsNumber: getMaxValueFromRoomStaysBC(reservationByIdList ?? [], 'adultsNumber'),
      reservationId: basketReference ?? '',
    };
  }

  const pcksQueryInput: PackagesCriteria = {
    country,
    language,
    hotelId: basketDetails.hotelId,
    adultsNumber: basketDetails.adultsNumber,
    childrenNumber: basketDetails.childrenNumber,
    startDate: basketDetails.startDate,
    endDate: basketDetails.endDate,
    bookingFlowId: basketDetails.bookingFlowId,
    nightsNumber: basketDetails.nightsNumber,
    channel: Channel.Bb,
    basketReferenceId: basketReference,
  };

  const hiQueryInput: QueryHotelInformationArgs = {
    country,
    language,
    hotelId: basketDetails.hotelId,
  };

  const staticContentQueryInput: StaticContentQueryInput = {
    country,
    language,
    site: SITE_BB,
    businessBooker: true,
  };

  const promotionQueryInput: PromotionQueryInput = {
    country,
    language,
    hotelId: basketDetails.hotelId,
    rateCode: basketDetails.rateCode,
    bookingChannel: BOOKING_CHANNEL.BB,
  };

  const getPackagesQuery = prefetchQuery(
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
      pcksQueryInput.basketReferenceId,
      pcksQueryInput.channel,
    ],
    () => graphQLRequest(GET_PACKAGES, { ...pcksQueryInput }, undefined, undefined, client)
  );

  const getHotelInformationConfirmationQuery = prefetchQuery(
    ['GetHotelInformation', hiQueryInput.hotelId, hiQueryInput.country, hiQueryInput.language],
    () => graphQLRequest(GET_HOTEL_INFORMATION, { ...hiQueryInput }, undefined, undefined, client)
  );

  const getPromotionPanelQuery = prefetchQuery(
    [
      'GetPromotionPanel',
      promotionQueryInput.country,
      promotionQueryInput.language,
      promotionQueryInput.hotelId,
      promotionQueryInput.rateCode,
      BOOKING_CHANNEL.BB,
    ],
    () =>
      graphQLRequest(GET_PROMOTION_PANEL, { ...promotionQueryInput }, undefined, undefined, client)
  );

  const getBasketQuery = prefetchQuery(['GetBasket', basketReference], () =>
    graphQLRequest(
      GET_BASKET,
      {
        basketReference,
      },
      undefined,
      undefined,
      client
    )
  );

  const getStaticContentQuery = prefetchQuery(['GetStaticContent', language, country], () =>
    graphQLRequest(GET_STATIC_CONTENT, { ...staticContentQueryInput }, undefined, undefined, client)
  );

  const getBookingFlowInformationQuery = prefetchQuery(
    [
      'GetBookingFlowInformation',
      basketDetails.bookingFlowId,
      basketDetails.hotelId,
      language,
      country,
    ],
    () =>
      graphQLRequest(
        GET_HEADER_BOOKING_FLOW_INFORMATION,
        {
          bookingFlowId: basketDetails.bookingFlowId,
          hotelId: basketDetails.hotelId,
          language,
          country,
        },
        undefined,
        undefined,
        client
      )
  );

  const seoQuery = prefetchQuery(
    [
      'seoInformation',
      language,
      country,
      PageName.CONFIRMATION,
      basketDetails.hotelId,
      basketDetails.bookingFlowId,
    ],
    () =>
      graphQLRequest(
        GET_SEO_INFORMATION,
        {
          language,
          country,
          page: PageName.CONFIRMATION,
          bookingFlowId: basketDetails.bookingFlowId,
          hotelId: basketDetails.hotelId,
        },
        undefined,
        undefined,
        client
      )
  );

  const innBusinessServerSideProps = getInnBusinessServerSideProps(
    idTokenCookie as string,
    language as Language,
    false,
    { ...req.headers, ...(sessionId && { [WB_SESSION_ID]: sessionId }) }
  );

  const promiseList = [
    isInnBusinessAppPage ? innBusinessServerSideProps : Promise.resolve(undefined),
    getBasketQuery,
    getStaticContentQuery,
    getBookingFlowInformationQuery,
    getHotelInformationConfirmationQuery,
    getPromotionPanelQuery,
    getPackagesQuery,
    seoQuery,
  ];
  if (hasCachedBasketDetails) {
    promiseList.push(getBookingConfirmationQuery);
  }

  const [innBusinessResult] = await Promise.allSettled(promiseList);

  const innBusiness =
    innBusinessResult.status === 'fulfilled' ? innBusinessResult.value : undefined;

  logQueries(performance.now());

  return {
    dehydratedState: dehydrate(queryClient),
    pcksQueryInput,
    hiQueryInput,
    staticContentQueryInput,
    promotionQueryInput,
    basketReference,
    innBusiness: innBusiness,
  };
};

export default createConfirmationBbDataLoaderFn;
