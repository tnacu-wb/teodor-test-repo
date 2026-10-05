import { dehydrate, QueryClient } from '@tanstack/react-query';
import {
  QueryBookingInformationArgs,
  BIResponse,
  BookingInformation,
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
  SITE_BB,
  Channel,
  Language,
  GET_CONTACT_PREFERENCES,
  BRANDCODES,
} from '@whitbread-eos/api';
import {
  getMaxValueFromRoomStays,
  getNightsNumber,
  graphQLRequest,
  ID_TOKEN_COOKIE,
  isJsonValid,
  QueriesLogger,
  decodeIdToken,
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
const createGuestDetailsBBDataLoaderFn = async ({
  req,
  res,
  language,
  country,
  query,
  queryClient,
  isInnBusinessAppPage = false,
}: Props) => {
  const cookies = new Cookies(req, res);
  const sessionId = cookies.get(WB_SESSION_ID);
  const idTokenCookie = cookies.get(ID_TOKEN_COOKIE);
  const client = getGQLClient(sessionId);

  const { prefetchQuery, fetchQuery, logQueries } = new QueriesLogger(
    queryClient,
    req,
    res,
    query,
    'BB | GDP | Guest Details Page'
  );

  const basketReference = query?.reservationId ? String(query.reservationId) : '';
  let basicBasketDetails: string | null = null;
  let redisStorage: ReturnType<typeof RedisStorageServer.getInstance> | null = null;
  const redisKey = RedisKeyPrefix.HRS + '::' + basketReference;
  try {
    redisStorage = RedisStorageServer.getInstance();
    basicBasketDetails = await redisStorage.getItem(redisKey);
  } catch (error) {
    logger.error({ error }, 'GUEST_DETAILS_REDIS_ERROR');
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
    () => graphQLRequest(GET_ANCILLARIES_BOOKING_INFO, biQueryInput, undefined, undefined, client)
  );

  if (!hasCachedBasketDetails) {
    logger.info(`No Cache/Redis found in GDP ${basketReference}, Fetching from GraphQL`);
    const { bookingInformation }: BIResponse = await getBookingInformationQuery;
    const { hotelId, reservationByIdList, bookingFlowId }: BookingInformation = bookingInformation;
    if (bookingFlowId && reservationByIdList?.[0]) {
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
      PageName.GUEST_DETAILS,
      basketDetails.hotelId,
      basketDetails.bookingFlowId,
    ],
    () =>
      graphQLRequest(
        GET_SEO_INFORMATION,
        {
          language,
          country,
          page: PageName.GUEST_DETAILS,
          bookingFlowId: basketDetails.bookingFlowId,
          hotelId: basketDetails.hotelId,
        },
        undefined,
        undefined,
        client
      )
  );
  const { email } = decodeIdToken(idTokenCookie as string);
  const variables = {
    request: {
      contactType: 'email' as const,
      contactValue: email,
      brandCodes: BRANDCODES[0],
      business: true,
      contactChannelId: null,
    },
  };
  const contactPreferencesQuery = prefetchQuery(['ContactPreferences', idTokenCookie], () =>
    graphQLRequest(GET_CONTACT_PREFERENCES, variables, idTokenCookie, undefined, client)
  );

  const promiseList = [
    getPackagesQuery,
    getHotelInformationQuery,
    getStaticContentQuery,
    getBookingFlowInformationQuery,
    seoQuery,
    contactPreferencesQuery,
  ];

  if (hasCachedBasketDetails) {
    promiseList.push(getBookingInformationQuery);
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
    dehydratedState: queryClient && dehydrate(queryClient),
    pcksQueryInput,
    hiQueryInput,
    biQueryInput,
    innBusiness: innBusiness,
  };
};

export default createGuestDetailsBBDataLoaderFn;
