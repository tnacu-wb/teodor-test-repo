//<editor-fold desc="Imports" defaultstate="collapsed">
import { dehydrate } from '@tanstack/react-query';
import { GET_STATIC_CONTENT, SITE_LEISURE } from '@whitbread-eos/api';
import { graphQLRequest, QueriesLogger, WB_SESSION_ID, getGQLClient } from '@whitbread-eos/utils';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';

//</editor-fold>

interface Props extends GetServerSidePropsContext {
  session: any;
  queryClient: any;
  proxyOptions: any;
  language: string;
  country: string;
}
const createRepeatPageCcuiDataLoaderFn = async ({
  req,
  res,
  query,
  session,
  queryClient,
  language,
  country,
  proxyOptions,
}: Props) => {
  const cookies = new Cookies(req, res);
  const sessionId = cookies.get(WB_SESSION_ID);
  const client = getGQLClient(sessionId);
  const { fetchQuery, logQueries } = new QueriesLogger(
    queryClient,
    req,
    res,
    query,
    'CCUI | RBP | Repeat Booking Page',
    session?.user.name
  );

  await fetchQuery(['GetStaticContent', language, country], () =>
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

export default createRepeatPageCcuiDataLoaderFn;
