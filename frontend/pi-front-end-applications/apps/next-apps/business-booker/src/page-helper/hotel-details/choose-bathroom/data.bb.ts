import { dehydrate, QueryClient } from '@tanstack/react-query';
import { GET_SEO_INFORMATION, GET_STATIC_CONTENT, PageName, SITE_BB } from '@whitbread-eos/api';
import {
  axiosRequest,
  decodeIdToken,
  ID_TOKEN_COOKIE,
  graphQLRequest,
  QueriesLogger,
  getGQLClient,
  WB_SESSION_ID,
} from '@whitbread-eos/utils';
import { getInnBusinessServerSideProps } from '@whitbread-eos/utils/server';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';

interface Props extends GetServerSidePropsContext {
  queryClient: QueryClient;
  language: string;
  country: string;
  isInnBusinessAppPage?: boolean;
}

const createChooseBathroomBbDataLoaderFn = async ({
  queryClient,
  req,
  res,
  query,
  country,
  isInnBusinessAppPage = false,
  language,
}: Props) => {
  const cookies = new Cookies(req, res);
  const sessionId = cookies.get(WB_SESSION_ID);
  const idTokenCookie = cookies.get(ID_TOKEN_COOKIE);
  const promiseList = [];
  const client = getGQLClient(sessionId);

  const { prefetchQuery, logQueries } = new QueriesLogger(
    queryClient,
    req,
    res,
    query,
    'BB | HDP | Choose Bathroom Page'
  );

  if (idTokenCookie) {
    const { email } = decodeIdToken(idTokenCookie) || {};

    const axiosProps = {
      method: 'GET',
      url: `${process.env.NEXT_PUBLIC_ACCOUNT_SERVICE}/customers/hotels/${email}?business=true`,
      headers: {
        Authorization: `Bearer ${idTokenCookie}`,
        [WB_SESSION_ID]: sessionId,
      },
    };

    const userDetailsQuery = prefetchQuery(['userDetails', idTokenCookie], () =>
      axiosRequest(axiosProps)
    );
    promiseList.push(userDetailsQuery);
  }

  const getStaticContentQuery = prefetchQuery(['GetStaticContent', language, country], () =>
    graphQLRequest(
      GET_STATIC_CONTENT,
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

  const seoQuery = prefetchQuery(['seoInformation', language, country, PageName.CYB], () =>
    graphQLRequest(
      GET_SEO_INFORMATION,
      {
        language,
        country,
        page: PageName.CYB,
      },
      undefined,
      undefined,
      client
    )
  );

  promiseList.push(getStaticContentQuery, seoQuery);

  const innBusinessServerSideProps = getInnBusinessServerSideProps(idTokenCookie, language, false, {
    ...req.headers,
    ...(sessionId && { [WB_SESSION_ID]: sessionId }),
  });

  const [innBusinessResult] = await Promise.allSettled([
    isInnBusinessAppPage ? innBusinessServerSideProps : Promise.resolve(undefined),
    ...promiseList,
  ]);

  const innBusiness =
    innBusinessResult.status === 'fulfilled' ? innBusinessResult.value : undefined;

  logQueries(performance.now());

  return {
    dehydratedState: dehydrate(queryClient),
    innBusiness: innBusiness,
  };
};

export default createChooseBathroomBbDataLoaderFn;
