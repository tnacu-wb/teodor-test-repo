import { dehydrate, QueryClient } from '@tanstack/react-query';
import {
  GET_STATIC_CONTENT,
  SITE_LEISURE,
  GET_SEO_INFORMATION,
  PageName,
} from '@whitbread-eos/api';
import { graphQLRequest, getGQLClient, QueriesLogger, WB_SESSION_ID } from '@whitbread-eos/utils';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';

interface Props extends GetServerSidePropsContext {
  queryClient: QueryClient;
  language: string;
  country: string;
}

const createPriceFinderCcuiDataLoaderFn = async ({
  req,
  res,
  language,
  country,
  query,
  queryClient,
}: Props) => {
  const cookies = new Cookies(req, res);
  const sessionId = cookies.get(WB_SESSION_ID);
  const client = getGQLClient(sessionId);

  const { prefetchQuery, fetchQuery, logQueries } = new QueriesLogger(
    queryClient,
    req,
    res,
    query,
    'CCUI | Price Finder Page'
  );

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

  const seoQuery = prefetchQuery(['seoInformation', language, country, PageName.PRICE_FINDER], () =>
    graphQLRequest(
      GET_SEO_INFORMATION,
      {
        language,
        country,
        page: PageName.PRICE_FINDER,
      },
      undefined,
      undefined,
      client
    )
  );

  const promiseList = [getStaticContentQuery, seoQuery];

  const [staticData] = await Promise.all(promiseList);

  logQueries(performance.now());

  return {
    dehydratedState: dehydrate(queryClient),
    staticData,
  };
};

export default createPriceFinderCcuiDataLoaderFn;
