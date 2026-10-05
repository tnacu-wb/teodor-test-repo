import { dehydrate, QueryClient } from '@tanstack/react-query';
import {
  AmendConfInput,
  BCResponse,
  QueryHotelInformationArgs,
  PackagesCriteria,
  Area,
  BookingSpinnerConfig,
  GET_DASHBOARD_BOOKING_CONFIRMATION,
  GET_HOTEL_INFORMATION,
  GET_SEO_INFORMATION,
  GET_STATIC_CONTENT,
  PageName,
  SITE_LEISURE,
} from '@whitbread-eos/api';
import {
  graphQLRequest,
  getGQLClient,
  QueriesLogger,
  WB_SESSION_ID,
  getFindBookingToken,
} from '@whitbread-eos/utils';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';

interface Props extends GetServerSidePropsContext {
  logger: any;
  queryClient: QueryClient;
  language: string;
  country: string;
}

const createBookingConfirmationPiDataLoaderFn = async ({
  language,
  country,
  logger,
  query,
  queryClient,
  req,
  res,
}: Props) => {
  const cookies = new Cookies(req, res);
  const sessionId = cookies.get(WB_SESSION_ID);
  const client = getGQLClient(sessionId);

  const bookingReference = query.bookingReference ? String(query.bookingReference) : '';
  const basketReferenceFromQuery = query.basketReference ? String(query.basketReference) : '';
  const basketReference = basketReferenceFromQuery || getFindBookingToken()?.basketReference || '';

  const amendBookingStatus = query.status ? String(query.status) : null;
  const tempBookingReference = query.tempBookingReference
    ? String(query.tempBookingReference)
    : null;
  let hiQueryInput: QueryHotelInformationArgs | null = null;
  const pcksQueryInput: PackagesCriteria | null = null;
  const promiseList = [];
  let confirmationInput: AmendConfInput | null = null;
  let email = '';
  let bookingSpinnerConfig: BookingSpinnerConfig[] = [];
  let bookingConfirmationData;

  const { prefetchQuery, fetchQuery, logQueries } = new QueriesLogger(
    queryClient,
    req,
    res,
    query,
    'PI | Amend | Booking Confirmation Page'
  );

  if (bookingReference && basketReference) {
    confirmationInput = {
      basketReference,
      bookingReference,
      language,
      country,
    };
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
          { ...confirmationInput, bookingChannel: Area.PI.toUpperCase() },
          undefined,
          undefined,
          client
        )
    );

    bookingConfirmationData = bookingConfirmation || {};
    const { hotelId, reservationByIdList } = bookingConfirmationData || {};

    bookingSpinnerConfig = bookingConfirmation?.bookingSpinnerConfig;
    // to resolve the hydration issue after returning
    email = reservationByIdList?.[0]?.billing?.email ?? '';

    logger.info({
      label: 'PI:AmendBookingConfirmation',
      message: {
        basketReference,
        bookingConfirmation,
      },
    });
    hiQueryInput = {
      country,
      language,
      hotelId: hotelId,
    };

    const hotelInformationQuery = prefetchQuery(
      ['GetHotelInformation', hiQueryInput.hotelId, hiQueryInput.country, hiQueryInput.language],
      () => graphQLRequest(GET_HOTEL_INFORMATION, { ...hiQueryInput }, undefined, undefined, client)
    );
    promiseList.push(hotelInformationQuery);
  }
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

  const seoQuery = prefetchQuery(['seoInformation', language, country, PageName.AMEND], () =>
    graphQLRequest(
      GET_SEO_INFORMATION,
      {
        language,
        country,
        page: PageName.AMEND,
      },
      undefined,
      undefined,
      client
    )
  );
  await Promise.allSettled([getStaticContentQuery, seoQuery, ...promiseList]);

  logQueries(performance.now());

  return {
    dehydratedState: dehydrate(queryClient),
    pcksQueryInput,
    hiQueryInput,
    confirmationInput,
    amendBookingStatus,
    email,
    bookingSpinnerConfig,
    tempBookingReference,
  };
};

export default createBookingConfirmationPiDataLoaderFn;
