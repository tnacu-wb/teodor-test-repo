import { dehydrate, QueryClient } from '@tanstack/react-query';
import { SITE_BB, GET_SEO_INFORMATION, PageName, getStaticContent } from '@whitbread-eos/api';
import {
  graphQLRequest,
  QueriesLogger,
  ID_TOKEN_COOKIE,
  WB_SESSION_ID,
  getGQLClient,
} from '@whitbread-eos/utils';
import { getInnBusinessServerSideProps } from '@whitbread-eos/utils/server';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';
import { ParsedUrlQuery } from 'querystring';

interface Props extends GetServerSidePropsContext {
  queryClient: QueryClient;
  language: string;
  country: string;
  query: ParsedUrlQuery;
  featureToggles: { [key: string]: boolean };
  isInnBusinessAppPage?: boolean;
}

const createPriceFinderBBDataLoaderFn = async ({
  req,
  res,
  language,
  country,
  query,
  queryClient,
  isInnBusinessAppPage = false,
}: Props) => {
  const cookies = new Cookies(req, res);
  const idTokenCookie = cookies.get(ID_TOKEN_COOKIE);
  const sessionId = cookies.get(WB_SESSION_ID);
  const client = getGQLClient(sessionId);

  const { prefetchQuery, fetchQuery, logQueries } = new QueriesLogger(
    queryClient,
    req,
    res,
    query,
    'BB | Price Finder Page'
  );

  const staticContentQuery = getStaticContent(false); // No barrier free label needed for price finder
  const getStaticContentQuery = fetchQuery(['GetStaticContent', language, country], () =>
    graphQLRequest(
      staticContentQuery,
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

  const innBusinessServerSideProps = getInnBusinessServerSideProps(idTokenCookie, language, true, {
    ...req.headers,
    ...(sessionId && { [WB_SESSION_ID]: sessionId }),
  });

  const [innBusinessResult, staticDataResult] = await Promise.allSettled([
    isInnBusinessAppPage ? innBusinessServerSideProps : Promise.resolve(undefined),
    getStaticContentQuery,
    seoQuery,
  ]);

  const innBusiness =
    innBusinessResult.status === 'fulfilled' ? innBusinessResult.value : undefined;
  const staticData = staticDataResult.status === 'fulfilled' ? staticDataResult.value : undefined;

  logQueries(performance.now());

  return {
    dehydratedState: dehydrate(queryClient),
    staticData,
    ...(innBusiness && { innBusiness }),
  };
};

export default createPriceFinderBBDataLoaderFn;
