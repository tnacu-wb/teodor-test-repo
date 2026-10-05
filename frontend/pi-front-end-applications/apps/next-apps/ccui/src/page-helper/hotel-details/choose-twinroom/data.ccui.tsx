import { dehydrate, QueryClient } from '@tanstack/react-query';
import { GET_STATIC_CONTENT, SITE_LEISURE } from '@whitbread-eos/api';
import { graphQLRequest, QueriesLogger, WB_SESSION_ID, getGQLClient } from '@whitbread-eos/utils';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';

interface Props extends GetServerSidePropsContext {
  queryClient: QueryClient;
  language: string;
  country: string;
  session: any;
  proxyOptions: any;
}

const createChooseTwinroomCcuiDataLoaderFn = async ({
  queryClient,
  session,
  query,
  req,
  res,
  country,
  language,
  proxyOptions,
}: Props) => {
  const cookies = new Cookies(req, res);
  const sessionId = cookies.get(WB_SESSION_ID);
  const client = getGQLClient(sessionId);
  const { prefetchQuery, logQueries } = new QueriesLogger(
    queryClient,
    req,
    res,
    query,
    'CCUI | HDP | Choose TwinRoom Page',
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
  };
};

export default createChooseTwinroomCcuiDataLoaderFn;
