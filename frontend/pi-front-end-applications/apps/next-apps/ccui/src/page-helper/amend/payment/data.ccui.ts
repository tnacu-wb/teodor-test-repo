import { dehydrate, QueryClient } from '@tanstack/react-query';
import { GET_STATIC_CONTENT, SITE_LEISURE } from '@whitbread-eos/api';
import { graphQLRequest, QueriesLogger, WB_SESSION_ID, getGQLClient } from '@whitbread-eos/utils';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';

interface Props extends GetServerSidePropsContext {
  logger: any;
  queryClient: QueryClient;
  proxyOptions: any;
  language: string;
  country: string;
  session: any;
}

const createAmendPaymentCCUIDataLoaderFn = async ({
  queryClient,
  query,
  logger,
  language,
  country,
  proxyOptions,
  req,
  res,
  session,
}: Props) => {
  const cookies = new Cookies(req, res);
  const sessionId = cookies.get(WB_SESSION_ID);
  const client = getGQLClient(sessionId);
  logger.info({
    label: 'CCUI:AmendPaymentPage',
    message: 'PageLoad',
  });

  const user = session?.user;
  const { prefetchQuery, logQueries } = new QueriesLogger(
    queryClient,
    req,
    res,
    query,
    'CCUI | Amend | Payment Page',
    user?.name ?? ''
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
      proxyOptions,
      client
    )
  );

  const promiseList = [getStaticContent];
  await Promise.allSettled(promiseList);

  logQueries(performance.now());

  return {
    dehydratedState: dehydrate(queryClient),
    user,
  };
};

export default createAmendPaymentCCUIDataLoaderFn;
