//<editor-fold desc="Imports" defaultstate="collapsed">
import { dehydrate, QueryClient } from '@tanstack/react-query';
import { GET_STATIC_CONTENT, SITE_LEISURE } from '@whitbread-eos/api';
import { graphQLRequest, QueriesLogger, WB_SESSION_ID, getGQLClient } from '@whitbread-eos/utils';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';

//</editor-fold>

interface Props extends GetServerSidePropsContext {
  session: any;
  queryClient: QueryClient;
  proxyOptions: any;
  language: string;
  country: string;
}
const createSearchAccountCCUIDataLoaderFn = async ({
  req,
  res,
  session,
  query,
  queryClient,
  language,
  country,
  proxyOptions,
}: Props) => {
  const cookies = new Cookies(req, res);
  const sessionId = cookies.get(WB_SESSION_ID);
  const client = getGQLClient(sessionId);
  const accessToken = session?.tokenSet?.accessToken;

  const { prefetchQuery, logQueries } = new QueriesLogger(
    queryClient,
    req,
    res,
    query,
    'CCUI | Search Account Page',
    session?.user.name
  );

  await prefetchQuery(['GetStaticContent', language, country], () =>
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

  logQueries(performance.now());

  return {
    dehydratedState: dehydrate(queryClient),
    user: session?.user,
    accessToken,
  };
};

export default createSearchAccountCCUIDataLoaderFn;
