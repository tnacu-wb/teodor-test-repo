import { dehydrate, QueryClient } from '@tanstack/react-query';
import {
  BCQueryInput,
  BCReservationListItem,
  BCResponse,
  BOOKING_CHANNEL,
  BOOKING_FLOW_PAGE,
  GET_BOOKING_CONFIRMATION,
  GET_HEADER_BOOKING_FLOW_INFORMATION,
  GET_HOTEL_INFORMATION,
  GET_PACKAGES,
  GET_PROMOTION_PANEL,
  GET_SEO_INFORMATION,
  GET_STATIC_CONTENT,
  QueryHotelInformationArgs,
  PageName,
  PackagesCriteria,
  SITE_LEISURE,
  StaticContentQueryInput,
  Channel,
  HIBasicBasketDetails,
} from '@whitbread-eos/api';
import {
  getGQLClient,
  getNightsNumber,
  graphQLRequest,
  isJsonValid,
  QueriesLogger,
  logger,
  BASKET_IDS_COOKIE,
  WB_SESSION_ID,
} from '@whitbread-eos/utils';
import { RedisKeyPrefix, RedisStorageServer as RedisStorage } from '@whitbread-eos/utils/server';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';

interface Props extends GetServerSidePropsContext {
  queryClient: QueryClient;
  language: string;
  country: string;
}

function getMaxValueFromRoomStays(
  data: BCReservationListItem[],
  field: 'adultsNumber' | 'childrenNumber'
): number {
  return data?.reduce((cur: number, val: BCReservationListItem) => {
    return cur < val.roomStay[field] ? val.roomStay[field] : cur;
  }, 0);
}

export const createConfirmationPiDataLoaderFn = async ({
  req,
  res,
  language,
  country,
  query,
  queryClient,
}: Props) => {
  const basketReference = query.reservationId ? String(query.reservationId) : '';

  // Get the basket IDs cookie to pass as authorization header
  const cookies = new Cookies(req, res);
  const basketIdsCookie = cookies.get(BASKET_IDS_COOKIE);
  const sessionId = cookies.get(WB_SESSION_ID);
  const client = getGQLClient(sessionId);

  // Parse the cookie to get array of encoded basket IDs as JSON string
  let basketIdsJsonString: string | undefined;
  if (basketIdsCookie && typeof basketIdsCookie === 'string') {
    try {
      const decodedCookie = decodeURIComponent(basketIdsCookie);
      const parsedIds = JSON.parse(decodedCookie);
      // Stringify it back to pass as header (already a valid JSON array string)
      basketIdsJsonString = Array.isArray(parsedIds) ? JSON.stringify(parsedIds) : undefined;
    } catch {
      basketIdsJsonString = undefined;
    }
  }

  let basicBasketDetails: string | null = null;
  let redisStorageInstance: RedisStorage | null = null;
  const redisKey = RedisKeyPrefix.HRS + '::' + basketReference;
  try {
    redisStorageInstance = RedisStorage.getInstance();
    basicBasketDetails = await redisStorageInstance.getItem(redisKey);
  } catch (error) {
    logger.error({ error }, 'PI_CONFIRMATION_PAGE_REDIS_ERROR');
  }

  const areBasketDetailsCached = !!(basicBasketDetails && isJsonValid(basicBasketDetails));
  let basketDetails: HIBasicBasketDetails = areBasketDetailsCached
    ? JSON.parse(basicBasketDetails || '{}')
    : null;

  const biQueryInput: BCQueryInput = {
    basketReference: basketReference,
    country,
    language,
    bookingChannel: BOOKING_CHANNEL.PI,
    flow: BOOKING_FLOW_PAGE.CONFIRMATION_PAGE,
  };

  const { prefetchQuery, fetchQuery, logQueries } = new QueriesLogger(
    queryClient,
    req,
    res,
    query,
    'PI | Confirmation Page'
  );
  const getBookingConfirmationQuery = fetchQuery(
    [
      'GetBookingConfirmation',
      language,
      country,
      basketReference,
      BOOKING_CHANNEL.PI,
      BOOKING_FLOW_PAGE.CONFIRMATION_PAGE,
    ],
    () =>
      graphQLRequest(
        GET_BOOKING_CONFIRMATION,
        biQueryInput,
        undefined,
        { basketIds: basketIdsJsonString },
        client
      )
  );

  if (!areBasketDetailsCached) {
    logger.info(
      `No Cache/Redis found in PI Confirmation ${basketReference}, Fetching from GraphQL`
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
    channel: Channel.Pi,
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
    site: SITE_LEISURE,
    businessBooker: false,
  };

  const promotionQueryInput = {
    country,
    language,
    hotelId: basketDetails.hotelId,
    rateCode: basketDetails.rateCode,
    bookingChannel: BOOKING_CHANNEL.PI,
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
      BOOKING_CHANNEL.PI,
    ],
    () =>
      graphQLRequest(GET_PROMOTION_PANEL, { ...promotionQueryInput }, undefined, undefined, client)
  );

  const getStaticContentQuery = fetchQuery(['GetStaticContent', language, country], () =>
    graphQLRequest(GET_STATIC_CONTENT, { ...staticContentQueryInput }, undefined, undefined, client)
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

  const promiseList = [
    getStaticContentQuery,
    getPromotionPanelQuery,
    getHotelInformationConfirmationQuery,
    getPackagesQuery,
    bookingFlowInfoQuery,
    seoQuery,
  ];
  if (areBasketDetailsCached) {
    promiseList.push(getBookingConfirmationQuery);
  }

  await Promise.all(promiseList);

  logQueries(performance.now());

  return {
    dehydratedState: dehydrate(queryClient),
    pcksQueryInput,
    hiQueryInput,
    staticContentQueryInput,
    promotionQueryInput,
    basketReference,
  };
};

export default createConfirmationPiDataLoaderFn;
