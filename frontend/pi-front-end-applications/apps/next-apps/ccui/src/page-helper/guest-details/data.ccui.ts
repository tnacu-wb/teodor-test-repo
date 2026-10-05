import { dehydrate, QueryClient } from '@tanstack/react-query';
import {
  QueryBookingInformationArgs,
  BIResponse,
  BookingInformation,
  GET_ANCILLARIES_BOOKING_INFO,
  GET_COUNTRIES,
  GET_HOTEL_INFORMATION,
  GET_PACKAGES,
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
import { RedisKeyPrefix, RedisStorageServer as RedisServer } from '@whitbread-eos/utils/server';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';

interface Props extends GetServerSidePropsContext {
  queryClient: QueryClient;
  language: string;
  country: string;
  proxyOptions: any;
  session: any;
}

const createGuestDetailsCcuiDataLoaderFn = async ({
  req,
  res,
  language,
  country,
  query,
  queryClient,
  proxyOptions,
  session,
}: Props) => {
  const cookies = new Cookies(req, res);
  const sessionId = cookies.get(WB_SESSION_ID);
  const client = getGQLClient(sessionId);

  const { prefetchQuery, fetchQuery, logQueries } = new QueriesLogger(
    queryClient,
    req,
    res,
    query,
    'CCUI | GDP | Guest Details Page',
    session?.user.name
  );

  const basketReference = query.reservationId ? String(query.reservationId) : '';
  let basicBasketDetails: string | null = null;
  let redisStorageInstance: RedisServer | null = null;
  const redisKey = RedisKeyPrefix.HRS + '::' + basketReference;
  try {
    redisStorageInstance = RedisServer.getInstance();
    basicBasketDetails = await redisStorageInstance.getItem(redisKey);
  } catch (error) {
    logger.error({ error }, 'CCUI_GUEST_DETAILS_REDIS_ERROR');
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
    () =>
      graphQLRequest(GET_ANCILLARIES_BOOKING_INFO, biQueryInput, undefined, proxyOptions, client)
  );

  if (!hasCachedBasketDetails) {
    logger.info(`No Cache/Redis found in CCUI GDP ${basketReference}, Fetching from GraphQL`);
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

    await redisStorageInstance?.setItem(redisKey, JSON.stringify(basketDetails));
  }

  if (!basketDetails) {
    logger.error({ basketReference }, 'CCUI_GUEST_DETAILS_NO_BASKET_DETAILS');
    throw new Error('No basket details found');
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
    channel: Channel.Ccui,
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
    () => graphQLRequest(GET_PACKAGES, { ...pcksQueryInput }, undefined, proxyOptions, client)
  );

  const getHotelInformationQuery = prefetchQuery(
    ['GetHotelInformation', hiQueryInput.hotelId, hiQueryInput.country, hiQueryInput.language],
    () =>
      graphQLRequest(GET_HOTEL_INFORMATION, { ...hiQueryInput }, undefined, proxyOptions, client)
  );

  const getCountriesQuery = prefetchQuery(['GetCountries', country, language, 'leisure'], () =>
    graphQLRequest(
      GET_COUNTRIES,
      {
        country,
        language,
        site: 'leisure',
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
        site: SITE_LEISURE,
        businessBooker: false,
      },
      undefined,
      proxyOptions,
      client
    )
  );
  const promiseList = [
    getPackagesQuery,
    getHotelInformationQuery,
    getCountriesQuery,
    getStaticContentQuery,
  ];
  if (hasCachedBasketDetails) {
    promiseList.push(getBookingInformationQuery);
  }

  await Promise.all(promiseList);

  logQueries(performance.now());

  return {
    dehydratedState: dehydrate(queryClient),
    pcksQueryInput,
    biQueryInput,
    hiQueryInput,
  };
};

export default createGuestDetailsCcuiDataLoaderFn;
