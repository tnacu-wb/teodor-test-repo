import { dehydrate, QueryClient } from '@tanstack/react-query';
import {
  AmendConfInput,
  BCResponse,
  QueryHotelInformationArgs,
  PackagesCriteria,
  Area,
  GET_DASHBOARD_BOOKING_CONFIRMATION,
  GET_HOTEL_INFORMATION,
  GET_SEO_INFORMATION,
  GET_STATIC_CONTENT,
  PageName,
  SITE_LEISURE,
} from '@whitbread-eos/api';
import {
  getFindBookingToken,
  graphQLRequest,
  logger,
  QueriesLogger,
  WB_SESSION_ID,
  getGQLClient,
} from '@whitbread-eos/utils';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';

interface Props extends GetServerSidePropsContext {
  session: any;
  queryClient: QueryClient;
  proxyOptions: any;
  language: string;
  country: string;
}

const createBookingConfirmationCCUIDataLoaderFn = async ({
  session,
  queryClient,
  language,
  country,
  proxyOptions,
  query,
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
  let bookingConfirmationData;

  const { prefetchQuery, fetchQuery, logQueries } = new QueriesLogger(
    queryClient,
    req,
    res,
    query,
    'CCUI | Amend | Booking Confirmation Page',
    session?.user.name
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
        Area.CCUI.toUpperCase(),
      ],
      () =>
        graphQLRequest(
          GET_DASHBOARD_BOOKING_CONFIRMATION,
          { ...confirmationInput, bookingChannel: Area.CCUI.toUpperCase() },
          undefined,
          proxyOptions,
          client
        )
    );
    bookingConfirmationData = bookingConfirmation || {};

    const { hotelId } = bookingConfirmationData || {};

    logger.info({
      label: 'CCUI:AmendBookingConfirmation',
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
      () =>
        graphQLRequest(GET_HOTEL_INFORMATION, { ...hiQueryInput }, undefined, proxyOptions, client)
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
      proxyOptions,
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
      proxyOptions,
      client
    )
  );
  const results = await Promise.allSettled([getStaticContentQuery, seoQuery, ...promiseList]);
  const staticData = results[0].status === 'fulfilled' ? results[0].value : {};

  logQueries(performance.now());

  return {
    dehydratedState: dehydrate(queryClient),
    pcksQueryInput,
    hiQueryInput,
    confirmationInput,
    staticData,
    amendBookingStatus,
    user: session?.user,
    tempBookingReference,
  };
};

export default createBookingConfirmationCCUIDataLoaderFn;
