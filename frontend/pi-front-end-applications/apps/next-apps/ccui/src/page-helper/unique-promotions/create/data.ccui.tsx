import { dehydrate, QueryClient } from '@tanstack/react-query';
import { GET_STATIC_CONTENT, SITE_LEISURE } from '@whitbread-eos/api';
import { graphQLRequest, QueriesLogger, ProxyOptions } from '@whitbread-eos/utils';
import { GetServerSidePropsContext } from 'next';

import { Session } from '~types/general';

interface Props extends GetServerSidePropsContext {
  session?: Session | null;
  logger: { info: (_arg: any) => void };
  queryClient: QueryClient;
  proxyOptions: ProxyOptions;
  language: string;
  country: string;
}

const promoBatchCreateCCUIDataLoaderFn = async ({
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
  logger.info({
    label: 'CCUI:PromoBatchList',
    message: 'PageLoad',
  });

  const { prefetchQuery, logQueries } = new QueriesLogger(
    queryClient,
    req,
    res,
    query,
    'CCUI | PromoBatch | Lsit Page',
    session?.user?.name
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
      proxyOptions
    )
  );

  const promiseList = [getStaticContent];
  Promise.all(promiseList);

  logQueries(performance.now());

  return {
    dehydratedState: dehydrate(queryClient),
    language,
    country,
    user: session?.user,
    varinnt: 'agent',
  };
};

export default promoBatchCreateCCUIDataLoaderFn;
