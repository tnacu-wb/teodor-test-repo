import { dehydrate, QueryClient } from '@tanstack/react-query';
import {
  AmendConfInput,
  GET_BOOKING_CONFIRMATION_AMEND,
  GET_ROOM_OCCUPANCY_LIMITATIONS_QUERY,
  GET_SEO_INFORMATION,
  GET_STATIC_CONTENT,
  GET_STAY_RULES_QUERY,
  PageName,
  PackagesCriteria,
  SITE_BB,
  Channel,
} from '@whitbread-eos/api';
import {
  graphQLRequest,
  QueriesLogger,
  WB_SESSION_ID,
  getGQLClient,
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

const createAmendBbDataLoaderFn = async ({
  language,
  country,
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
  const tempBookingReference = query.tempBookingReference
    ? String(query.tempBookingReference)
    : null;
  const status = query.status ? String(query.status) : null;
  const pcksQueryInput: PackagesCriteria | null = null;
  const channel = Channel.Bb;
  let confirmationInput: AmendConfInput | null = null;

  const { prefetchQuery, logQueries } = new QueriesLogger(
    queryClient,
    req,
    res,
    query,
    'BB | Amend | Details Page'
  );

  const getStaticContent = prefetchQuery(['GetStaticContent', language, country], () =>
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

  const getStayRules = prefetchQuery(['getStayRules', channel], () =>
    graphQLRequest(GET_STAY_RULES_QUERY, { channel }, undefined, undefined, client)
  );

  const getRoomOccupancyLimitations = prefetchQuery(['getRoomOccupancyLimitations', channel], () =>
    graphQLRequest(GET_ROOM_OCCUPANCY_LIMITATIONS_QUERY, { channel }, undefined, undefined, client)
  );

  const promiseList = [getStaticContent, getStayRules, getRoomOccupancyLimitations, seoQuery];

  if (bookingReference && basketReference) {
    confirmationInput = {
      basketReference,
      bookingReference,
      language,
      country,
    };

    const bookingConfirmationQuery = prefetchQuery(
      [
        'getBookingConfirmationAmend',
        confirmationInput.basketReference,
        confirmationInput.country,
        confirmationInput.language,
      ],
      () =>
        graphQLRequest(
          GET_BOOKING_CONFIRMATION_AMEND,
          confirmationInput as AmendConfInput,
          undefined,
          undefined,
          client
        )
    );
    promiseList.push(bookingConfirmationQuery);
  }

  await Promise.all(promiseList);

  logQueries(performance.now());

  return {
    dehydratedState: dehydrate(queryClient),
    confirmationInput,
    pcksQueryInput,
    tempBookingReference,
    status,
  };
};

export default createAmendBbDataLoaderFn;
