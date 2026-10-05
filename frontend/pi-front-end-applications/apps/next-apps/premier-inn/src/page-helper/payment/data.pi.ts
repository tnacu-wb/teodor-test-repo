import { dehydrate, QueryClient } from '@tanstack/react-query';
import {
  QueryBookingInformationArgs,
  BOOKING_CHANNEL,
  QueryHotelInformationArgs,
  PackagesCriteria,
  GET_BOOKING_INFORMATION,
  GET_HEADER_BOOKING_FLOW_INFORMATION,
  GET_HOTEL_INFORMATION,
  GET_PACKAGES,
  GET_PAYMENT_INFO_MESSAGES_QUERY,
  GET_STATIC_CONTENT,
  GET_TERMS_AND_CONDITIONS_QUERY,
  SITE_LEISURE,
  BIResponse,
  HIBasicBasketDetails,
  PageName,
  GET_SEO_INFORMATION,
  GET_PAYMENT_METHODS_QUERY,
  UserType,
  Area,
  BookingInformation,
  Channel,
  PaymentMethods,
  PaymentMethodsCriteria,
  GET_DASHBOARD_BASKET,
} from '@whitbread-eos/api';
import {
  getNightsNumber,
  getMaxValueFromRoomStays,
  graphQLRequest,
  isJsonValid,
  getGQLClient,
  QueriesLogger,
  ID_TOKEN_COOKIE,
  logger,
} from '@whitbread-eos/utils';
import { RedisKeyPrefix, RedisStorageServer } from '@whitbread-eos/utils/server';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';

interface Props extends GetServerSidePropsContext {
  queryClient: QueryClient;
  language: string;
  country: string;
}

const createPaymentPiDataLoaderFn = async ({
  req,
  res,
  queryClient,
  language,
  country,
  query,
}: Props) => {
  const basketReference = query.reservationId ? String(query.reservationId) : '';
  const cookies = new Cookies(req, res);
  const sessionId = cookies.get('WB-SESSION-ID');
  const client = getGQLClient(sessionId);

  const idTokenCookie = cookies.get(ID_TOKEN_COOKIE);

  let basicBasketDetails: string | null = null;
  let redisStorage: RedisStorageServer | null = null;
  const redisKey = RedisKeyPrefix.HRS + '::' + basketReference;
  try {
    redisStorage = RedisStorageServer.getInstance();
    basicBasketDetails = await redisStorage.getItem(redisKey);
  } catch (error) {
    logger.error({ error }, 'PI_PAYMENT_PAGE_REDIS_ERROR');
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
    'PI | PMTP | Payment Page'
  );

  const biQueryInput: QueryBookingInformationArgs = {
    basketReference: basketReference,
    country,
    language,
    bookingChannelCriteria: {
      channel: Channel.Pi,
      subchannel: 'WEB',
      language: language === 'en' ? 'EN' : 'DE',
    },
  };

  const paymentMethodsQueryInput: PaymentMethodsCriteria = {
    language,
    country,
    basketReference,
    userType: UserType.Leisure,
    clientChannel: Area.PI.toUpperCase(),
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
    logger.info(`No Cache/Redis found in PI Payment ${basketReference}, Fetching from GraphQL`);
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

  const getPaymentMethodQuery: Promise<PaymentMethods> = prefetchQuery(
    ['getPaymentMethods', language, country, basketReference],
    () =>
      graphQLRequest(
        GET_PAYMENT_METHODS_QUERY,
        {
          ...paymentMethodsQueryInput,
          idToken: idTokenCookie,
        },
        undefined,
        undefined,
        client
      )
  );

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
    channel: Channel.Pi,
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
      BOOKING_CHANNEL.PI,
    ],
    () =>
      graphQLRequest(
        GET_TERMS_AND_CONDITIONS_QUERY,
        {
          hotelId: basketDetails.hotelId,
          country,
          language,
          rateCode: basketDetails.rateCode,
          bookingChannel: BOOKING_CHANNEL.PI,
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
      BOOKING_CHANNEL.PI,
    ],
    () =>
      graphQLRequest(
        GET_PAYMENT_INFO_MESSAGES_QUERY,
        {
          hotelId: basketDetails.hotelId,
          language,
          country,
          rateCode: basketDetails.rateCode,
          bookingChannel: BOOKING_CHANNEL.PI,
        },
        undefined,
        undefined,
        client
      )
  );

  const getStaticContentQuery = fetchQuery(['GetStaticContent', language, country], () =>
    graphQLRequest(
      GET_STATIC_CONTENT,
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

  const bookingFlowInfoQuery = prefetchQuery(
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
    getStaticContentQuery,
    getPaymentInfoMessagesQuery,
    getTermsAndConditionsQuery,
    getHotelInformationQuery,
    getPackagesQuery,
    bookingFlowInfoQuery,
    seoQuery,
    getPaymentMethodQuery,
  ];
  if (hasCachedBasketDetails) {
    promiseList.push(getBookingInformationQuery);
  }

  let getBasketStatus;
  if (query?.['secure-booking'] === 'true') {
    getBasketStatus = fetchQuery(['basket'], () =>
      graphQLRequest(GET_DASHBOARD_BASKET, {
        basketReference,
      })
    );
    promiseList.push(getBasketStatus);
  }

  await Promise.all(promiseList);

  logQueries(performance.now());

  return {
    dehydratedState: dehydrate(queryClient),
    pcksQueryInput,
    hiQueryInput,
    basketReference,
  };
};

export default createPaymentPiDataLoaderFn;
