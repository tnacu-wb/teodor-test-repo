import { dehydrate, QueryClient } from '@tanstack/react-query';
import {
  GET_SEO_INFORMATION,
  GET_STATIC_CONTENT,
  FT_PI_AUTH0_LOGIN,
  PageName,
  SITE_LEISURE,
} from '@whitbread-eos/api';
import {
  axiosRequest,
  decodeIdToken,
  ID_TOKEN_COOKIE,
  getGQLClient,
  graphQLRequest,
  QueriesLogger,
  WB_SESSION_ID,
} from '@whitbread-eos/utils';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';

import { getAuth0TokenAndEmail } from '../../../lib/getAuth0Token';

interface Props extends GetServerSidePropsContext {
  queryClient: QueryClient;
  language: string;
  country: string;
  featureToggles?: { [key: string]: boolean };
}

const createChooseBathroomPiDataLoaderFn = async ({
  queryClient,
  query,
  req,
  res,
  country,
  language,
  featureToggles = {},
}: Props) => {
  const cookies = new Cookies(req, res);
  const sessionId = cookies.get(WB_SESSION_ID);
  const client = getGQLClient(sessionId);
  const promiseList = [];

  const { prefetchQuery, fetchQuery, logQueries } = new QueriesLogger(
    queryClient,
    req,
    res,
    query,
    'PI | HDP | Choose Bathroom Page'
  );

  const isAuth0Enabled = featureToggles[FT_PI_AUTH0_LOGIN] ?? false;
  let authToken: string | undefined;
  let userEmail: string | undefined;

  if (isAuth0Enabled) {
    const { accessToken, email } = await getAuth0TokenAndEmail(req);
    authToken = accessToken ?? undefined;
    userEmail = email ?? undefined;
  } else {
    const idTokenCookie = cookies.get(ID_TOKEN_COOKIE);
    authToken = idTokenCookie;
    userEmail = idTokenCookie ? decodeIdToken(idTokenCookie).email : undefined;
  }

  if (authToken && userEmail) {
    const axiosProps = {
      method: 'GET',
      url: `${process.env.NEXT_PUBLIC_ACCOUNT_SERVICE}/customers/hotels/${userEmail}?business=false`,
      headers: {
        Authorization: `Bearer ${authToken}`,
        [WB_SESSION_ID]: sessionId,
      },
    };

    const userDetailsQuery = prefetchQuery(['userDetails', authToken], () =>
      axiosRequest(axiosProps)
    );
    promiseList.push(userDetailsQuery);
  }

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

  await Promise.all([getStaticContentQuery, seoQuery, ...promiseList]);

  logQueries(performance.now());

  return {
    dehydratedState: dehydrate(queryClient),
  };
};

export default createChooseBathroomPiDataLoaderFn;
