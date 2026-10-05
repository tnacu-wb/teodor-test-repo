import { dehydrate, QueryClient } from '@tanstack/react-query';
import {
  BCQueryInput,
  BCReservationListItem,
  BCResponse,
  BOOKING_CHANNEL,
  Channel,
  GET_BOOKING_CONFIRMATION,
  GET_PACKAGES,
  GET_STATIC_CONTENT,
  HIBasicBasketDetails,
  PackagesCriteria,
  QueryHotelInformationArgs,
  SITE_LEISURE,
} from '@whitbread-eos/api';
import {
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

interface Props extends GetServerSidePropsContext {
  queryClient: QueryClient;
  language: string;
  country: string;
  session: any;
  proxyOptions: any;
}

function getMaxValueFromRoomStays(
  data: BCReservationListItem[],
  field: 'adultsNumber' | 'childrenNumber'
): number {
  return data?.reduce((cur: number, val: BCReservationListItem) => {
    return cur < val.roomStay[field] ? val.roomStay[field] : cur;
  }, 0);
}

export const createConfirmationCcuiDataLoaderFn = async ({
  req,
  res,
  language,
  country,
  query,
  queryClient,
  session,
  proxyOptions,
}: Props) => {
  const cookies = new Cookies(req, res);
  const sessionId = cookies.get(WB_SESSION_ID);
  const client = getGQLClient(sessionId);

  const basketReference = query?.reservationId ? String(query.reservationId) : '';
  const user = session?.user;
  let basicBasketDetails: string | null = null;
  let redisStorage: RedisStorageServer | null = null;
  const redisKey = RedisKeyPrefix.HRS + '::' + basketReference;
  try {
    redisStorage = RedisStorageServer.getInstance();
    basicBasketDetails = await redisStorage.getItem(redisKey);
  } catch (error) {
    logger.error({ error }, 'CCUI_CONFIRMATION_PAGE_REDIS_ERROR');
  }

  const hasCachedBasketDetails = !!(basicBasketDetails && isJsonValid(basicBasketDetails));
  let basketDetails: HIBasicBasketDetails = hasCachedBasketDetails
    ? JSON.parse(basicBasketDetails || '{}')
    : null;

  const biQueryInput: BCQueryInput = {
    basketReference: basketReference,
    country,
    language,
    bookingChannel: BOOKING_CHANNEL.CCUI,
  };

  const { prefetchQuery, fetchQuery, logQueries } = new QueriesLogger(
    queryClient,
    req,
    res,
    query,
    'CCUI | Confirmation Page',
    session?.user.name
  );

  const getBookingConfirmationQuery = fetchQuery(
    ['GetBookingConfirmation', language, country, basketReference, BOOKING_CHANNEL.CCUI],
    () => graphQLRequest(GET_BOOKING_CONFIRMATION, biQueryInput, undefined, proxyOptions, client)
  );

  if (!hasCachedBasketDetails) {
    logger.info(
      `No Cache/Redis found in CCUI Confirmation ${basketReference}, Fetching from GraphQL`
    );
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
      childrenNumber: getMaxValueFromRoomStays(reservationByIdList ?? [], 'childrenNumber'),
      adultsNumber: getMaxValueFromRoomStays(reservationByIdList ?? [], 'adultsNumber'),
      reservationId: basketReference ?? '',
    };
  }

  const hiQueryInput: QueryHotelInformationArgs = {
    country,
    language,
    hotelId: basketDetails.hotelId,
  };

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
    channel: Channel.Ccui,
    basketReferenceId: basketReference,
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
    () => graphQLRequest(GET_PACKAGES, { ...pcksQueryInput }, undefined, proxyOptions, client)
  );

  const getStaticContentQuery = prefetchQuery(['GetStaticContent', language, country], () =>
    graphQLRequest(
      GET_STATIC_CONTENT,
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

  const promiseList = [getStaticContentQuery, getPackagesQuery];
  if (hasCachedBasketDetails) {
    promiseList.push(getBookingConfirmationQuery);
  }

  await Promise.all(promiseList);

  logQueries(performance.now());

  return {
    dehydratedState: dehydrate(queryClient),
    user,
    hiQueryInput,
    basketReference,
    pcksQueryInput,
  };
};

export default createConfirmationCcuiDataLoaderFn;
