import { dehydrate, QueryClient } from '@tanstack/react-query';
import {
  QueryBookingInformationArgs,
  BIResponse,
  BookingInformation,
  GET_ANCILLARIES_BOOKING_INFO,
  GET_COUNTRIES,
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
  FT_PI_BB_ACCOUNT_SERV_TO_SERV,
} from '@whitbread-eos/api';
import {
  axiosRequest,
  decodeIdToken,
  ID_TOKEN_COOKIE,
  getGQLClient,
  getMaxValueFromRoomStays,
  getNightsNumber,
  graphQLRequest,
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
  featureToggles: { [key: string]: boolean };

  authToken?: string;
  userEmail?: string;
  passedBasketDetails?: HIBasicBasketDetails;
  hasCachedBasketDetails?: boolean;
}

const createGuestDetailsPiDataLoaderFn = async ({
  req,
  res,
  language,
  country,
  query,
  queryClient,
  featureToggles,
  passedBasketDetails,
  authToken: providedAuthToken,
  userEmail: providedUserEmail,
  hasCachedBasketDetails,
}: Props) => {
  const cookies = new Cookies(req, res);
  const sessionId = cookies.get(WB_SESSION_ID);
  const client = getGQLClient(sessionId);

  // Use provided tokens (Auth0) or fall back to legacy cookie
  const idTokenCookie = cookies.get(ID_TOKEN_COOKIE);
  const authToken = providedAuthToken || idTokenCookie;
  const userEmail =
    providedUserEmail || (idTokenCookie ? decodeIdToken(idTokenCookie).email : undefined);

  const { prefetchQuery, fetchQuery, logQueries } = new QueriesLogger(
    queryClient,
    req,
    res,
    query,
    'PI | GDP | Guest Details Page'
  );

  const basketReference = query.reservationId ? String(query.reservationId) : '';
  let basketDetails = passedBasketDetails;
  const redisKey = RedisKeyPrefix.HRS + '::' + basketReference;
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
  await prefetchQuery(['GetBookingInformation', language, country, basketReference], () =>
    graphQLRequest(GET_ANCILLARIES_BOOKING_INFO, biQueryInput, undefined, undefined, client)
  );

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
    logger.info(`No Cache/Redis found in PI GDP ${basketReference}, Fetching from GraphQL`);
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

    try {
      const redisStorage = RedisStorageServer.getInstance();
      await redisStorage.setItem(redisKey, JSON.stringify(basketDetails));
    } catch (error) {
      logger.error({ error }, 'PI_GUEST_DETAILS_REDIS_SET_ERROR');
    }
  }

  if (!basketDetails) {
    logger.error({ basketReference }, 'PI_GUEST_DETAILS_NO_BASKET_DETAILS');
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
      undefined,
      client
    )
  );

  const getBookingFlowInformationQuery = fetchQuery(
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

  const promiseList = [
    getStaticContentQuery,
    getBookingFlowInformationQuery,
    getCountriesQuery,
    getHotelInformationQuery,
    getPackagesQuery,
    seoQuery,
  ];

  if (authToken && userEmail) {
    const { [FT_PI_BB_ACCOUNT_SERV_TO_SERV]: isServ2ServEnabled } = featureToggles;

    const axiosProps = {
      method: 'GET',
      url: `${
        isServ2ServEnabled
          ? process.env.NEXT_PUBLIC_ACCOUNT_SERVICE
          : process.env.NEXT_PUBLIC_REST_API
      }/customers/hotels/${userEmail}?business=false`,
      headers: {
        Authorization: `Bearer ${authToken}`,
        [WB_SESSION_ID]: sessionId,
      },
    };

    const userDetailsQuery = prefetchQuery(['userDetails', authToken], () =>
      axiosRequest(axiosProps)
    );
    promiseList.push(userDetailsQuery);
  }
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

export default createGuestDetailsPiDataLoaderFn;
