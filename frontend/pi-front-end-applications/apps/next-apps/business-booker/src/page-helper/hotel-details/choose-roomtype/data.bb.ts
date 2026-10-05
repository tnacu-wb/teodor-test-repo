import { dehydrate, QueryClient } from '@tanstack/react-query';
import {
  GET_SEO_INFORMATION,
  GET_STATIC_CONTENT,
  PageName,
  SITE_BB,
  FT_PI_BB_ACCOUNT_SERV_TO_SERV,
} from '@whitbread-eos/api';
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
  featureToggles: { [key: string]: boolean };
  isInnBusinessAppPage?: boolean;
}

const createChooseRoomTypeBbDataLoaderFn = async ({
  queryClient,
  req,
  res,
  query,
  country,
  language,
  featureToggles,
  isInnBusinessAppPage = false,
}: Props) => {
  const cookies = new Cookies(req, res);
  const sessionId = cookies.get(WB_SESSION_ID);
  const tokenCookie = cookies.get(ID_TOKEN_COOKIE);
  const promiseList = [];
  const client = getGQLClient(sessionId);

  const { prefetchQuery, logQueries } = new QueriesLogger(
    queryClient,
    req,
    res,
    query,
    'BB | HDP | Choose Room Type Page'
  );

  if (tokenCookie) {
    const { email } = decodeIdToken(tokenCookie) || {};
    const { [FT_PI_BB_ACCOUNT_SERV_TO_SERV]: isServ2ServEnabled } = featureToggles;

    const axiosProps = {
      method: 'GET',
      url: `${
        isServ2ServEnabled
          ? process.env.NEXT_PUBLIC_ACCOUNT_SERVICE
          : process.env.NEXT_PUBLIC_REST_API
      }/customers/hotels/${email}?business=true`,
      headers: {
        Authorization: `Bearer ${tokenCookie}`,
        [WB_SESSION_ID]: sessionId,
      },
    };

    const userDetailsQuery = prefetchQuery(['userDetails', tokenCookie], () =>
      axiosRequest(axiosProps)
    );
    promiseList.push(userDetailsQuery);
  }

  const getStaticContent = prefetchQuery(['GetStaticContent', language, country], () =>
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

  promiseList.push(getStaticContent, seoQuery);

  const innBusinessServerSideProps = getInnBusinessServerSideProps(tokenCookie, language, false, {
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

export default createChooseRoomTypeBbDataLoaderFn;
