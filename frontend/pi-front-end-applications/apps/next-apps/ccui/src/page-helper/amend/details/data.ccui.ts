import { dehydrate, QueryClient } from '@tanstack/react-query';
import type { AmendConfInput } from '@whitbread-eos/api';
import {
  GET_ROOM_OCCUPANCY_LIMITATIONS_QUERY,
  GET_STATIC_CONTENT,
  GET_STAY_RULES_QUERY,
  SITE_LEISURE,
} from '@whitbread-eos/api';
import {
  getFindBookingToken,
  graphQLRequest,
  QueriesLogger,
  WB_SESSION_ID,
  getGQLClient,
} from '@whitbread-eos/utils';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';

interface Props extends GetServerSidePropsContext {
  session: any;
  logger: any;
  queryClient: QueryClient;
  proxyOptions: any;
  language: string;
  country: string;
}

const createAmendCCUIDataLoaderFn = async ({
  session,
  queryClient,
  logger,
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
  logger.info({
    label: 'CCUI:AmendPage',
    message: 'PageLoad',
  });

  const bookingReference = query.bookingReference ? String(query.bookingReference) : '';
  const basketReferenceFromQuery = query.basketReference ? String(query.basketReference) : '';
  const basketReference = basketReferenceFromQuery || getFindBookingToken()?.basketReference || '';

  const channel = 'CCUI';

  const { prefetchQuery, logQueries } = new QueriesLogger(
    queryClient,
    req,
    res,
    query,
    'CCUI | Amend | Details Page',
    session?.user.name
  );

  let confirmationInput: AmendConfInput | null = null;
  if (bookingReference && basketReference) {
    confirmationInput = {
      basketReference,
      bookingReference,
      language,
      country,
    };
  }

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
      proxyOptions,
      client
    )
  );

  const getStayRules = prefetchQuery(['getStayRules', channel], () =>
    graphQLRequest(GET_STAY_RULES_QUERY, { channel }, undefined, proxyOptions, client)
  );

  const getRoomOccupancyLimitations = prefetchQuery(['getRoomOccupancyLimitations', channel], () =>
    graphQLRequest(
      GET_ROOM_OCCUPANCY_LIMITATIONS_QUERY,
      { channel },
      undefined,
      proxyOptions,
      client
    )
  );
  const promiseList = [getStaticContent, getStayRules, getRoomOccupancyLimitations];
  await Promise.allSettled(promiseList);

  logQueries(performance.now());

  return {
    dehydratedState: dehydrate(queryClient),
    confirmationInput,
    user: session?.user,
  };
};

export default createAmendCCUIDataLoaderFn;
