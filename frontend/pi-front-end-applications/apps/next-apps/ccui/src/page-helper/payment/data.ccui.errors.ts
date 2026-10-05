import { dehydrate, QueryClient } from '@tanstack/react-query';
import {
  QueryBookingInformationArgs,
  BIResponse,
  BookingInformation,
  GET_BOOKING_INFORMATION,
  GET_HOTEL_INFORMATION,
  GET_PACKAGES,
  GET_PAYMENT_TYPE_OPTS_QUERY_CCUI,
  GET_STATIC_CONTENT,
  HIBasicBasketDetails,
  QueryHotelInformationArgs,
  PackagesCriteria,
  SITE_LEISURE,
  Channel,
} from '@whitbread-eos/api';
import {
  getDefaultSessionTracing,
  getMaxValueFromRoomStays,
  getNightsNumber,
  graphQLRequest,
  instrumentQueryClient,
  isJsonValid,
  logger,
  WB_SESSION_ID,
  getGQLClient,
} from '@whitbread-eos/utils';
import { RedisKeyPrefix, RedisStorageServer } from '@whitbread-eos/utils/server';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';

type Session = Record<string, any>;

interface Props extends GetServerSidePropsContext {
  queryClient: QueryClient;
  session: Session | null | undefined;
  language: string;
  country: string;
  proxyOptions?: any;
}

export const createDataLoaderCcuiErrors = async ({
  req,
  res,
  query,
  queryClient,
  session,
  language,
  country,
  proxyOptions,
}: Props) => {
  const cookies = new Cookies(req, res);
  const sessionId = cookies.get(WB_SESSION_ID);
  const client = getGQLClient(sessionId);
  const accessToken = session?.tokenSet?.accessToken;
  const user = session?.user;
  const basketReference = query.reservationId ? String(query.reservationId) : '';
  let basicBasketDetails: string | null = null;
  let redisStorage: RedisStorageServer | null = null;
  const redisKey = RedisKeyPrefix.HRS + '::' + basketReference;
  try {
    redisStorage = RedisStorageServer.getInstance();
    basicBasketDetails = await redisStorage.getItem(redisKey);
  } catch (error) {
    logger.error({ error }, 'CCUI_PAYMENT_ERRORS_PAGE_REDIS_ERROR');
  }

  const hasCachedBasketDetails = !!(basicBasketDetails && isJsonValid(basicBasketDetails));
  let basketDetails: HIBasicBasketDetails = hasCachedBasketDetails
    ? JSON.parse(basicBasketDetails || '{}')
    : null;
  let hiQueryInput: QueryHotelInformationArgs | undefined = undefined;
  let pcksQueryInput: PackagesCriteria | undefined = undefined;
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

  const sessionTracing = getDefaultSessionTracing(cookies);
  const { fetchQuery, prefetchQuery } = instrumentQueryClient(queryClient, sessionTracing);

  logger.info({
    label: 'CCUI:PMTP-ERRORS:START_DATA_FETCHING',
    msg: {
      query,
      ...sessionTracing,
    },
  });

  const getBookingInformationQuery = fetchQuery(
    [
      'GetBookingInformation',
      biQueryInput.language,
      biQueryInput.country,
      biQueryInput.basketReference,
    ],
    () => graphQLRequest(GET_BOOKING_INFORMATION, biQueryInput, accessToken, proxyOptions, client)
  );

  if (!hasCachedBasketDetails) {
    logger.info(
      `No Cache/Redis found in CCUI PMTP-ERRORS ${basketReference}, Fetching from GraphQL`
    );
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

  pcksQueryInput = {
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
    channel: Channel.Ccui,
  };

  hiQueryInput = {
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
      pcksQueryInput.channel,
    ],
    () =>
      graphQLRequest(
        GET_PACKAGES,
        {
          ...pcksQueryInput,
        },
        undefined,
        proxyOptions,
        client
      )
  );

  const getHotelInformationQuery = prefetchQuery(
    ['GetHotelInformation', hiQueryInput.hotelId, hiQueryInput.country, hiQueryInput.language],
    () =>
      graphQLRequest(GET_HOTEL_INFORMATION, { ...hiQueryInput }, accessToken, proxyOptions, client)
  );

  const getPaymentMethodsQuery = prefetchQuery(
    ['getPaymentMethodsCCUI', language, country, query.reservationId],
    () =>
      graphQLRequest(
        GET_PAYMENT_TYPE_OPTS_QUERY_CCUI,
        {
          language,
          country,
          basketReference: basketReference,
        },
        accessToken,
        proxyOptions,
        client
      )
  );

  const getStaticContent = prefetchQuery(['GetStaticContent', language, country], () =>
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

  const promiseList = [
    getPackagesQuery,
    getHotelInformationQuery,
    getPaymentMethodsQuery,
    getStaticContent,
  ];
  if (hasCachedBasketDetails) {
    promiseList.push(getBookingInformationQuery);
  }

  await Promise.all(promiseList);

  logger.info({
    label: 'CCUI:PMTP-ERRORS:END_DATA_FETCHING',
    msg: {
      query,
      basketReference,
      basketDetails,
      ...sessionTracing,
    },
  });

  return {
    dehydratedState: dehydrate(queryClient),
    pcksQueryInput,
    hiQueryInput,
    basketReference,
    accessToken,
    user,
  };
};
