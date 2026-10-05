import { dehydrate, QueryClient } from '@tanstack/react-query';
import {
  GET_SEO_INFORMATION,
  GET_STATIC_CONTENT,
  PageName,
  SITE_LEISURE,
} from '@whitbread-eos/api';
import { graphQLRequest, getGQLClient, QueriesLogger, WB_SESSION_ID } from '@whitbread-eos/utils';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';

interface Props extends GetServerSidePropsContext {
  queryClient: QueryClient;
  language: string;
  country: string;
}

const createChooseTwinroomPiDataLoaderFn = async ({
  queryClient,
  query,
  req,
  res,
  country,
  language,
}: Props) => {
  const cookies = new Cookies(req, res);
  const sessionId = cookies.get(WB_SESSION_ID);
  const client = getGQLClient(sessionId);

  const { prefetchQuery, fetchQuery, logQueries } = new QueriesLogger(
    queryClient,
    req,
    res,
    query,
    'PI | HDP | Choose TwinRoom Page'
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

  const seoQuery = prefetchQuery(['seoInformation', language, country, PageName.CYT], () =>
    graphQLRequest(
      GET_SEO_INFORMATION,
      {
        language,
        country,
        page: PageName.CYT,
      },
      undefined,
      undefined,
      client
    )
  );

  await Promise.all([getStaticContentQuery, seoQuery]);

  logQueries(performance.now());

  return {
    dehydratedState: dehydrate(queryClient),
  };
};

export default createChooseTwinroomPiDataLoaderFn;
