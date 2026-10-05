import { dehydrate, QueryClient } from '@tanstack/react-query';
import {
  QueryBookingInformationArgs,
  BIResponse,
  BOOKING_CHANNEL,
  BookingInformation,
  GET_BOOKING_INFORMATION,
  GET_HEADER_BOOKING_FLOW_INFORMATION,
  GET_HOTEL_INFORMATION,
  GET_PACKAGES,
  GET_PAYMENT_INFO_MESSAGES_QUERY,
  GET_SEO_INFORMATION,
  GET_STATIC_CONTENT,
  GET_TERMS_AND_CONDITIONS_QUERY,
  HIBasicBasketDetails,
  QueryHotelInformationArgs,
  PageName,
  PackagesCriteria,
  SITE_BB,
  Channel,
  GET_DASHBOARD_BASKET,
  Language,
} from '@whitbread-eos/api';
import {
  getMaxValueFromRoomStays,
  getNightsNumber,
  graphQLRequest,
  ID_TOKEN_COOKIE,
  isJsonValid,
  QueriesLogger,
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

const createPaymentBbDataLoaderFn = async ({
  req,
  res,
  queryClient,
  language,
  country,
  query,
  isInnBusinessAppPage = false,
}: Props) => {
  const cookies = new Cookies(req, res);
  const idTokenCookie = cookies.get(ID_TOKEN_COOKIE);
  const sessionId = cookies.get(WB_SESSION_ID);
  const client = getGQLClient(sessionId);

  const basketReference = query.reservationId ? String(query.reservationId) : '';
  let basicBasketDetails: string | null = null;
  let redisStorage: ReturnType<typeof RedisStorageServer.getInstance> | null = null;
  const redisKey = RedisKeyPrefix.HRS + '::' + basketReference;
  try {
    redisStorage = RedisStorageServer.getInstance();
    basicBasketDetails = await redisStorage.getItem(redisKey);
  } catch (error) {
    logger.error({ error }, 'PAYMENT_PAGE_REDIS_ERROR');
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
    'BB | PMTP | Payment Page'
  );

  const biQueryInput: QueryBookingInformationArgs = {
    basketReference: basketReference,
    country,
    language,
    bookingChannelCriteria: {
      channel: Channel.Bb,
      subchannel: 'WEB',
      language: language === 'en' ? 'EN' : 'DE',
    },
  };

  const getBookingInformationQuery = fetchQuery(
    [
      'GetBookingInformation',
      biQueryInput.language,
      biQueryInput.country,
      biQueryInput.basketReference,
    ],
    () => graphQLRequest(GET_BOOKING_INFORMATION, biQueryInput, undefined, undefined, client)
  );

  if (!hasCachedBasketDetails) {
    logger.info(`No Cache/Redis found in payment page ${basketReference}, Fetching from GraphQL`);
    const { bookingInformation }: BIResponse = await getBookingInformationQuery;
    const { hotelId, reservationByIdList, bookingFlowId }: BookingInformation = bookingInformation;

    const startDate = reservationByIdList?.[0]?.roomStay?.arrivalDate ?? '';
    const endDate = reservationByIdList?.[0]?.roomStay?.departureDate ?? '';

    basketDetails = {
      bookingFlowId: bookingFlowId ?? '',
      hotelId: hotelId ?? '',
      rateCode: reservationByIdList?.[0]?.roomStay?.ratePlanCode ?? '',
      nightsNumber: getNightsNumber(startDate, endDate),
      startDate: startDate,
      endDate: endDate,
      childrenNumber: getMaxValueFromRoomStays(reservationByIdList ?? [], 'childrenNumber'),
      adultsNumber: getMaxValueFromRoomStays(reservationByIdList ?? [], 'adultsNumber'),
      reservationId: basketReference ?? '',
    };

    await redisStorage?.setItem(redisKey, JSON.stringify(basketDetails));
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
    basketReferenceId: basketReference,
    channel: Channel.Bb,
  };

  const hiQueryInput: QueryHotelInformationArgs = {
    country,
    language,
    hotelId: basketDetails.hotelId,
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
      biQueryInput.basketReference,
      pcksQueryInput.channel,
    ],
    () => graphQLRequest(GET_PACKAGES, { ...pcksQueryInput }, undefined, undefined, client)
  );

  const getHotelInformationQuery = prefetchQuery(
    ['GetHotelInformation', hiQueryInput.hotelId, hiQueryInput.country, hiQueryInput.language],
    () => graphQLRequest(GET_HOTEL_INFORMATION, { ...hiQueryInput }, undefined, undefined, client)
  );

  const getTermsAndConditionsQuery = prefetchQuery(
    [
      'GetTermsAndConditions',
      basketDetails.hotelId,
      country,
      language,
      basketDetails.rateCode,
      BOOKING_CHANNEL.BB,
    ],
    () =>
      graphQLRequest(
        GET_TERMS_AND_CONDITIONS_QUERY,
        {
          hotelId: basketDetails.hotelId,
          language,
          country,
          rateCode: basketDetails.rateCode,
          bookingChannel: BOOKING_CHANNEL.BB,
        },
        undefined,
        undefined,
        client
      )
  );

  const getPaymentInfoMessagesQuery = prefetchQuery(
    [
      'GetPaymentInfoMessages',
      basketDetails.hotelId,
      country,
      language,
      basketDetails.rateCode,
      BOOKING_CHANNEL.BB,
    ],
    () =>
      graphQLRequest(
        GET_PAYMENT_INFO_MESSAGES_QUERY,
        {
          hotelId: basketDetails.hotelId,
          language,
          country,
          rateCode: basketDetails.rateCode,
          bookingChannel: BOOKING_CHANNEL.BB,
        },
        undefined,
        undefined,
        client
      )
  );

  const getStaticContentQuery = prefetchQuery(['GetStaticContent', language, country], () =>
    graphQLRequest(
      GET_STATIC_CONTENT,
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
      PageName.PAYMENT,
      basketDetails.hotelId,
      basketDetails.bookingFlowId,
    ],
    () =>
      graphQLRequest(
        GET_SEO_INFORMATION,
        {
          language,
          country,
          page: PageName.PAYMENT,
          bookingFlowId: basketDetails.bookingFlowId,
          hotelId: basketDetails.hotelId,
        },
        undefined,
        undefined,
        client
      )
  );

  const promiseList = [
    getBookingFlowInformationQuery,
    getStaticContentQuery,
    getPaymentInfoMessagesQuery,
    getTermsAndConditionsQuery,
    getHotelInformationQuery,
    getPackagesQuery,
    seoQuery,
  ];

  if (hasCachedBasketDetails) {
    promiseList.push(getBookingInformationQuery);
  }

  let getBasketStatus;
  if (query?.['secure-booking'] === 'true') {
    getBasketStatus = fetchQuery(['basket'], () =>
      graphQLRequest(
        GET_DASHBOARD_BASKET,
        {
          basketReference,
        },
        undefined,
        undefined,
        client
      )
    );
    promiseList.push(getBasketStatus);
  }

  const innBusinessServerSideProps = getInnBusinessServerSideProps(
    idTokenCookie as string,
    language as Language,
    false,
    { ...req.headers, ...(sessionId && { [WB_SESSION_ID]: sessionId }) }
  );

  const [innBusinessResult] = await Promise.allSettled([
    isInnBusinessAppPage ? innBusinessServerSideProps : Promise.resolve(undefined),
    ...promiseList,
  ]);

  const innBusiness =
    innBusinessResult.status === 'fulfilled' ? innBusinessResult.value : undefined;

  logQueries(performance.now());
  return {
    dehydratedState: dehydrate(queryClient),
    pcksQueryInput,
    hiQueryInput,
    basketReference,
    innBusiness: innBusiness,
  };
};

export default createPaymentBbDataLoaderFn;
