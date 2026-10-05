import { dehydrate, QueryClient } from '@tanstack/react-query';
import {
  QueryBookingInformationArgs,
  BIResponse,
  GET_ANCILLARIES_BOOKING_INFO,
  GET_HEADER_BOOKING_FLOW_INFORMATION,
  GET_HOTEL_INFORMATION,
  GET_PACKAGES,
  GET_SEO_INFORMATION,
  GET_STATIC_CONTENT,
  HIBasicBasketDetails,
  QueryHotelInformationArgs,
  PageName,
  PackagesCriteria,
  SITE_LEISURE,
  Channel,
} from '@whitbread-eos/api';
import {
  getGQLClient,
  getNightsNumber,
  getMaxValueFromRoomStays,
  graphQLRequest,
  isJsonValid,
  QueriesLogger,
  logger,
  WB_SESSION_ID,
} from '@whitbread-eos/utils';
import { RedisKeyPrefix, RedisStorageServer } from '@whitbread-eos/utils/server';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';

interface Props extends GetServerSidePropsContext {
  queryClient: QueryClient;
  language: string;
  country: string;
}
const createAncillariesPiDataLoaderFn = async ({
  req,
  res,
  language,
  country,
  query,
  queryClient,
}: Props) => {
  const cookies = new Cookies(req, res);
  const sessionId = cookies.get(WB_SESSION_ID);
  const client = getGQLClient(sessionId);

  const { prefetchQuery, fetchQuery, logQueries } = new QueriesLogger(
    queryClient,
    req,
    res,
    query,
    'PI | Ancillaries Page'
  );

  const basketReference = query.reservationId ? String(query.reservationId) : '';

  let basicBasketDetails: string | null = null;
  let redisStorage: RedisStorageServer | null = null;
  const redisKey = RedisKeyPrefix.HRS + '::' + basketReference;
  try {
    redisStorage = RedisStorageServer.getInstance();
    basicBasketDetails = await redisStorage.getItem(redisKey);
  } catch (error) {
    logger.error({ error }, 'PI_ANCILLARIES_REDIS_ERROR');
  }

  const hasCachedBasketDetails = !!(basicBasketDetails && isJsonValid(basicBasketDetails));
  let basketDetails: HIBasicBasketDetails = hasCachedBasketDetails
    ? JSON.parse(basicBasketDetails || '{}')
    : null;

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

  const getBookingInformationQuery = fetchQuery(
    [
      'GetBookingInformation',
      biQueryInput.language,
      biQueryInput.country,
      biQueryInput.basketReference,
    ],
    () => graphQLRequest(GET_ANCILLARIES_BOOKING_INFO, biQueryInput, undefined, undefined, client)
  );

  if (!hasCachedBasketDetails) {
    logger.info(`No Cache/Redis found in PI ancillaries ${basketReference}, Fetching from GraphQL`);
    const { bookingInformation }: BIResponse = await getBookingInformationQuery;
    const { hotelId, reservationByIdList, bookingFlowId } = bookingInformation;

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
    hotelId: basketDetails?.hotelId,
    adultsNumber: basketDetails?.adultsNumber,
    childrenNumber: basketDetails?.childrenNumber,
    startDate: basketDetails?.startDate,
    endDate: basketDetails?.endDate,
    bookingFlowId: basketDetails?.bookingFlowId,
    nightsNumber: basketDetails?.nightsNumber,
    basketReferenceId: basketReference,
    channel: Channel.Pi,
  };

  const hiQueryInput: QueryHotelInformationArgs = {
    country,
    language,
    hotelId: basketDetails?.hotelId,
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

  const getBookingFlowInformationQuery = prefetchQuery(
    [
      'GetBookingFlowInformation',
      basketDetails?.bookingFlowId,
      basketDetails?.hotelId,
      language,
      country,
    ],
    () =>
      graphQLRequest(
        GET_HEADER_BOOKING_FLOW_INFORMATION,
        {
          bookingFlowId: basketDetails?.bookingFlowId,
          hotelId: basketDetails?.hotelId,
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
      PageName.ANCILLARIES,
      basketDetails?.hotelId,
      basketDetails?.bookingFlowId,
    ],
    () =>
      graphQLRequest(
        GET_SEO_INFORMATION,
        {
          language,
          country,
          page: PageName.ANCILLARIES,
          bookingFlowId: basketDetails?.bookingFlowId,
          hotelId: basketDetails?.hotelId,
        },
        undefined,
        undefined,
        client
      )
  );

  const promiseList = [
    getStaticContentQuery,
    getBookingFlowInformationQuery,
    getHotelInformationQuery,
    getPackagesQuery,
    seoQuery,
  ];

  if (hasCachedBasketDetails) {
    promiseList.push(getBookingInformationQuery);
  }

  await Promise.all(promiseList);

  logQueries(performance.now());

  return {
    dehydratedState: dehydrate(queryClient),
    pcksQueryInput,
    hiQueryInput,
    biQueryInput,
  };
};

export default createAncillariesPiDataLoaderFn;
