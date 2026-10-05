import { dehydrate, QueryClient } from '@tanstack/react-query';
import type { AmendConfInput } from '@whitbread-eos/api';
import {
  GET_BOOKING_CONFIRMATION_AMEND,
  GET_EMPLOYEE_STAY_RULES_QUERY,
  GET_ROOM_OCCUPANCY_LIMITATIONS_QUERY,
  GET_SEO_INFORMATION,
  GET_STATIC_CONTENT,
  GET_STAY_RULES_QUERY,
  OfferEnum,
  PageName,
  SITE_LEISURE,
} from '@whitbread-eos/api';
import {
  getFindBookingToken,
  getGQLClient,
  graphQLRequest,
  QueriesLogger,
  WB_SESSION_ID,
} from '@whitbread-eos/utils';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';

interface Props extends GetServerSidePropsContext {
  logger: any;
  queryClient: QueryClient;
  language: string;
  country: string;
}

const createAmendPiDataLoaderFn = async ({
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

  const channel = 'PI';
  let confirmationInput: AmendConfInput | null = null;
  const { prefetchQuery, logQueries } = new QueriesLogger(
    queryClient,
    req,
    res,
    query,
    'PI | Amend | Details Page'
  );

  const getStaticContent = prefetchQuery(['GetStaticContent', language, country], () =>
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

  const getStayRules = prefetchQuery(['getStayRules', channel], () =>
    graphQLRequest(
      GET_STAY_RULES_QUERY,
      {
        channel,
      },
      undefined,
      undefined,
      client
    )
  );

  const getEmployeeStayRules = prefetchQuery(['getEmployeeStayRules', channel], () =>
    graphQLRequest(
      GET_EMPLOYEE_STAY_RULES_QUERY,
      {
        channel: OfferEnum.EMPLOYEE_RATE_CODE,
      },
      undefined,
      undefined,
      client
    )
  );

  const getRoomOccupancyLimitations = prefetchQuery(['getRoomOccupancyLimitations', channel], () =>
    graphQLRequest(GET_ROOM_OCCUPANCY_LIMITATIONS_QUERY, { channel }, undefined, undefined, client)
  );

  const promiseList = [
    getStaticContent,
    getStayRules,
    getEmployeeStayRules,
    getRoomOccupancyLimitations,
    seoQuery,
  ];

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
  await Promise.allSettled(promiseList);

  logQueries(performance.now());

  return {
    dehydratedState: dehydrate(queryClient),
    confirmationInput,
    tempBookingReference,
    status,
  };
};

export default createAmendPiDataLoaderFn;
