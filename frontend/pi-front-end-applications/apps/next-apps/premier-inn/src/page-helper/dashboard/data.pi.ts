import { dehydrate, QueryClient } from '@tanstack/react-query';
import {
  BCAuthResponse,
  BCResponse,
  GET_DASHBOARD_BOOKING_CONFIRMATION,
  GET_DASHBOARD_BOOKING_CONFIRMATION_AUTHENTICATED,
  GET_HOTEL_INFORMATION,
  GET_PACKAGES,
  GET_SEO_INFORMATION,
  GET_STATIC_CONTENT,
  QueryHotelInformationArgs,
  PageName,
  PackagesCriteria,
  SITE_LEISURE,
  Area,
  Channel,
} from '@whitbread-eos/api';
import {
  ID_TOKEN_COOKIE,
  getDefaultSessionTracing,
  getFindBookingToken,
  getGQLClient,
  getMaxValueFromRoomStaysBC,
  getNightsNumber,
  graphQLRequest,
  logger,
  QueriesLogger,
  WB_SESSION_ID,
} from '@whitbread-eos/utils';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';

interface Props extends GetServerSidePropsContext {
  queryClient: QueryClient;
  language: string;
  country: string;
  featureToggles?: { [key: string]: boolean };
  authToken?: string;
}

interface ConfInput {
  basketReference: string;
  bookingReference: string;
  language: string;
  country: string;
}

async function getBookingConfirmationData(
  queryClient: QueryClient,
  confirmationInput: ConfInput,
  req: any,
  res: any,
  cookies: any,
  sessionTracing: any,
  fetchQuery: any,
  client: any,
  authToken?: string
) {
  // Use provided authToken (Auth0) or fall back to legacy cookie
  const token = authToken || cookies.get(ID_TOKEN_COOKIE);
  let bookingConfirmationData;

  if ((token as any) instanceof Error || !token) {
    const { bookingConfirmation }: BCResponse = await fetchQuery(
      [
        'getBookingConfirmation',
        confirmationInput.basketReference,
        confirmationInput.language,
        confirmationInput.country,
        Area.PI.toUpperCase(),
      ],
      () =>
        graphQLRequest(
          GET_DASHBOARD_BOOKING_CONFIRMATION,
          {
            ...confirmationInput,
            bookingChannel: Area.PI.toUpperCase(),
          },
          undefined,
          undefined,
          client
        )
    );
    bookingConfirmationData = bookingConfirmation;
  } else {
    const { bookingConfirmationAuthenticated }: BCAuthResponse = await fetchQuery(
      [
        'getBookingConfirmationAuthenticated',
        confirmationInput.bookingReference,
        confirmationInput.language,
        confirmationInput.country,
        Area.PI.toUpperCase(),
      ],
      () =>
        graphQLRequest(
          GET_DASHBOARD_BOOKING_CONFIRMATION_AUTHENTICATED,
          { ...confirmationInput, bookingChannel: Area.PI.toUpperCase() },
          token,
          undefined,
          client
        )
    );
    bookingConfirmationData = bookingConfirmationAuthenticated;
  }
  return bookingConfirmationData;
}

const createDashboardPiDataLoaderFn = async ({
  language,
  country,
  query,
  queryClient,
  req,
  res,
  authToken,
}: Props) => {
  const bookingReference = query.bookingReference ? String(query.bookingReference) : '';
  const { basketReference } = getFindBookingToken();
  let hiQueryInput: QueryHotelInformationArgs | null = null;
  let pcksQueryInput: PackagesCriteria | null = null;
  const promiseList = [];
  let confirmationInput: ConfInput | null = null;

  const cookies = new Cookies(req, res);
  const sessionId = cookies.get(WB_SESSION_ID);
  const client = getGQLClient(sessionId);
  const sessionTracing = getDefaultSessionTracing(cookies);

  const { prefetchQuery, fetchQuery, logQueries } = new QueriesLogger(
    queryClient,
    req,
    res,
    query,
    'PI | Dashboard | Single Booking'
  );

  if (bookingReference && basketReference) {
    confirmationInput = {
      basketReference,
      bookingReference,
      language,
      country,
    };
    const bookingData = await getBookingConfirmationData(
      queryClient,
      confirmationInput,
      req,
      res,
      cookies,
      sessionTracing,
      fetchQuery,
      client,
      authToken
    );
    const { hotelId, reservationByIdList, bookingFlowId } = bookingData;

    pcksQueryInput = {
      country,
      language,
      hotelId: hotelId,
      adultsNumber: getMaxValueFromRoomStaysBC(reservationByIdList, 'adultsNumber'),
      childrenNumber: getMaxValueFromRoomStaysBC(reservationByIdList, 'childrenNumber'),
      startDate: reservationByIdList[0].roomStay.arrivalDate,
      endDate: reservationByIdList[0].roomStay.departureDate,
      bookingFlowId: bookingFlowId || '',
      nightsNumber: getNightsNumber(
        reservationByIdList[0].roomStay.arrivalDate,
        reservationByIdList[0].roomStay.departureDate
      ),
      basketReferenceId: basketReference,
      channel: Channel.Bb,
    };

    hiQueryInput = {
      country,
      language,
      hotelId: hotelId,
    };

    const packagesQuery = prefetchQuery(
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
        basketReference,
      ],
      () => graphQLRequest(GET_PACKAGES, { ...pcksQueryInput }, undefined, undefined, client)
    );

    const hotelInformationQuery = prefetchQuery(
      ['GetHotelInformation', hiQueryInput.hotelId, hiQueryInput.country, hiQueryInput.language],
      () => graphQLRequest(GET_HOTEL_INFORMATION, { ...hiQueryInput }, undefined, undefined, client)
    );
    promiseList.push(packagesQuery, hotelInformationQuery);
  } else {
    logger.info({
      label: 'PI:DASHBOARD',
      message: 'PageLoaded',
    });
  }

  const seoQuery = prefetchQuery(['seoInformation', language, country, PageName.DASHBOARD], () =>
    graphQLRequest(
      GET_SEO_INFORMATION,
      {
        language,
        country,
        page: PageName.DASHBOARD,
        hotelId: pcksQueryInput?.hotelId,
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

  await Promise.all([getStaticContentQuery, seoQuery, ...promiseList]);

  logQueries(performance.now());

  return {
    dehydratedState: dehydrate(queryClient),
    pcksQueryInput,
    hiQueryInput,
    confirmationInput,
  };
};

export default createDashboardPiDataLoaderFn;
