import { dehydrate, QueryClient } from '@tanstack/react-query';
import {
  GET_STATIC_CONTENT,
  SITE_LEISURE,
  GET_SEO_INFORMATION,
  PageName,
  GET_PRICE_FINDER_CONFIG,
  Channel,
} from '@whitbread-eos/api';
import { graphQLRequest, getGQLClient, QueriesLogger, WB_SESSION_ID } from '@whitbread-eos/utils';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';

import { extractPriceFinderPath } from './utils';

interface Props extends GetServerSidePropsContext {
  queryClient: QueryClient;
  language: string;
  country: string;
}

const createPriceFinderPiDataLoaderFn = async ({
  req,
  res,
  language,
  country,
  query,
  queryClient,
  resolvedUrl,
}: Props) => {
  const cookies = new Cookies(req, res);
  const sessionId = cookies.get(WB_SESSION_ID);
  const client = getGQLClient(sessionId);

  const { prefetchQuery, fetchQuery, logQueries } = new QueriesLogger(
    queryClient,
    req,
    res,
    query,
    'PI | Price Finder Page'
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

  // Extract path after /price-finder/ (or /calendar/ or /kalender/ in original URL)
  const extractedPath = extractPriceFinderPath(resolvedUrl || '');
  const path = extractedPath || 'default'; // Use 'default' when path is empty
  const priceFinderConfigQuery = prefetchQuery(['priceFinderConfig', language, country, path], () =>
    graphQLRequest(
      GET_PRICE_FINDER_CONFIG,
      {
        channel: Channel.Pi,
        brand: 'pi',
        language,
        country,
        path,
      },
      undefined,
      undefined,
      client
    )
  );

  const promiseList = [getStaticContentQuery, seoQuery, priceFinderConfigQuery];

  const [staticData] = await Promise.all(promiseList);

  logQueries(performance.now());

  return {
    dehydratedState: dehydrate(queryClient),
    staticData,
  };
};

export default createPriceFinderPiDataLoaderFn;
