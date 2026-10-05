import { dehydrate, QueryClient } from '@tanstack/react-query';
import {
  AmendConfInput,
  BCResponse,
  BookingSpinnerConfig,
  GET_DASHBOARD_BOOKING_CONFIRMATION,
  GET_HOTEL_INFORMATION,
  GET_SEO_INFORMATION,
  GET_STATIC_CONTENT,
  QueryHotelInformationArgs,
  PageName,
  PackagesCriteria,
  SITE_BB,
  Language,
} from '@whitbread-eos/api';
import {
  getFindBookingToken,
  graphQLRequest,
  ID_TOKEN_COOKIE,
  QueriesLogger,
  WB_SESSION_ID,
  getGQLClient,
} from '@whitbread-eos/utils';
import { getInnBusinessServerSideProps } from '@whitbread-eos/utils/server';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';

interface Props extends GetServerSidePropsContext {
  logger: any;
  queryClient: QueryClient;
  language: string;
  country: string;
  isInnBusinessAppPage?: boolean;
}

const createBookingConfirmationBbDataLoaderFn = async ({
  language,
  country,
  logger,
  query,
  queryClient,
  req,
  res,
  isInnBusinessAppPage = false,
}: Props) => {
  const cookies = new Cookies(req, res);
  const idTokenCookie = cookies.get(ID_TOKEN_COOKIE);
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
    'BB | Amend | Booking Confirmation Page'
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
      ],
      () =>
        graphQLRequest(
          GET_DASHBOARD_BOOKING_CONFIRMATION,
          { ...confirmationInput },
          undefined,
          undefined,
          client
        )
    );
    bookingConfirmationData = bookingConfirmation || {};

    const { hotelId, reservationByIdList } = bookingConfirmationData || {};
    bookingSpinnerConfig = bookingConfirmationData?.bookingSpinnerConfig;
    email = reservationByIdList?.[0]?.billing?.email;

    logger.info({
      label: 'BB:AmendBookingConfirmation',
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
        site: SITE_BB,
        businessBooker: true,
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

  const innBusinessServerSideProps = getInnBusinessServerSideProps(
    idTokenCookie as string,
    language as Language,
    false,
    { ...req.headers, ...(sessionId && { [WB_SESSION_ID]: sessionId }) }
  );

  const [staticDataResult, innBusinessResult] = await Promise.allSettled([
    getStaticContentQuery,
    isInnBusinessAppPage ? innBusinessServerSideProps : Promise.resolve(undefined),
    seoQuery,
    ...promiseList,
  ]);

  const staticData = staticDataResult.status === 'fulfilled' ? staticDataResult.value : {};
  const innBusiness =
    innBusinessResult.status === 'fulfilled' ? innBusinessResult.value : undefined;

  logQueries(performance.now());

  return {
    dehydratedState: dehydrate(queryClient),
    pcksQueryInput,
    hiQueryInput,
    confirmationInput,
    staticData,
    amendBookingStatus,
    email,
    bookingSpinnerConfig,
    tempBookingReference,
    innBusiness: innBusiness,
  };
};

export default createBookingConfirmationBbDataLoaderFn;
