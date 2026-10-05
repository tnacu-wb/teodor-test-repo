import { dehydrate, QueryClient } from '@tanstack/react-query';
import {
  QueryBookingInformationArgs,
  BIResponse,
  BookingInformation,
  GET_PAYMENT_STATUS,
  CCUI_ROLES,
  GET_BOOKING_INFORMATION,
  GET_HOTEL_INFORMATION,
  GET_PACKAGES,
  GET_RESOURCE_ID_BY_ROLES,
  GET_STATIC_CONTENT,
  HIBasicBasketDetails,
  QueryHotelInformationArgs,
  PackagesCriteria,
  SITE_LEISURE,
  Channel,
} from '@whitbread-eos/api';
import {
  getMaxValueFromRoomStays,
  getNightsNumber,
  graphQLRequest,
  isJsonValid,
  QueriesLogger,
  WB_SESSION_ID,
  getGQLClient,
  logger,
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

export const createDataLoaderCcui = async ({
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
  const roles = session?.user[CCUI_ROLES];
  const roleIdList = (roles || []).join(',');
  const basketReference = query.reservationId ? String(query.reservationId) : '';
  let basicBasketDetails: string | null = null;
  let redisStorage: RedisStorageServer | null = null;
  const redisKey = RedisKeyPrefix.HRS + '::' + basketReference;
  try {
    redisStorage = RedisStorageServer.getInstance();
    basicBasketDetails = await redisStorage.getItem(redisKey);
  } catch (error) {
    logger.error({ error }, 'CCUI_PAYMENT_PAGE_REDIS_ERROR');
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
    'CCUI | PMTP | Payment Page',
    session?.user.name
  );

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
    logger.info(`No Cache/Redis found in CCUI PMTP ${basketReference}, Fetching from GraphQL`);
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
      basketReference,
      pcksQueryInput.channel,
    ],
    () =>
      graphQLRequest(
        GET_PACKAGES,
        {
          ...pcksQueryInput,
        },
        accessToken,
        proxyOptions,
        client
      )
  );

  const hotelInformationQuery = prefetchQuery(
    ['GetHotelInformation', basketDetails.hotelId, country, language],
    () =>
      graphQLRequest(GET_HOTEL_INFORMATION, { ...hiQueryInput }, accessToken, proxyOptions, client)
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
      accessToken,
      proxyOptions,
      client
    )
  );

  const resourceIdQuery = fetchQuery('GetResourceIdByRoles', () =>
    graphQLRequest(GET_RESOURCE_ID_BY_ROLES, { roleIdList }, accessToken, proxyOptions, client)
  );

  const getPaymentStatusQuery = fetchQuery(['GetPaymentStatus', basketReference], () =>
    graphQLRequest(GET_PAYMENT_STATUS, { basketReference }, accessToken, proxyOptions, client)
  );

  const promiseList = [
    resourceIdQuery,
    getPaymentStatusQuery,
    hotelInformationQuery,
    getStaticContent,
    getPackagesQuery,
  ];

  if (hasCachedBasketDetails) {
    promiseList.push(getBookingInformationQuery);
  }

  const [resourceId, paymentStatus] = await Promise.all(promiseList);

  logQueries(performance.now());

  return {
    dehydratedState: dehydrate(queryClient),
    pcksQueryInput,
    hiQueryInput,
    basketReference,
    accessToken,
    user,
    resourceIdByRoles: resourceId.searchResourceId,
    paymentStatus: { basket: paymentStatus.basket },
  };
};
