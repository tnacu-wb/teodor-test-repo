import { dehydrate, QueryClient, useQueryClient } from '@tanstack/react-query';
import { BUSINESS_BOOKER_USER_ROLES, PageName } from '@whitbread-eos/api';
import { SEO as Seo } from '@whitbread-eos/molecules';
import { BBSearchContainer as Search } from '@whitbread-eos/organisms';
import {
  axiosRequest,
  decodeIdToken,
  ID_TOKEN_COOKIE,
  useUserData,
  useUserDetails,
  getI18nLabels,
  getServerSideCustomLocale,
  WB_SESSION_ID,
} from '@whitbread-eos/utils';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';

export default function HomePage() {
  const router = useRouter();
  const client = useQueryClient();
  const variant = 'bb';
  const { searchLocation, ARRdd, ARRmm, ARRyyyy, NIGHTS, ROOMS } = router.query;
  const [stayerRole, setStayerRole] = useState(false);
  const { isLoggedIn } = useUserData();
  const userData = useUserDetails(true, isLoggedIn);
  const accessLevel = (userData as any)?.business?.accessLevel;
  useEffect(() => {
    setStayerRole([null, BUSINESS_BOOKER_USER_ROLES.STAYER, undefined].includes(accessLevel));
  }, [accessLevel]);

  return (
    <>
      <Seo page={PageName.HOME} />
      {!stayerRole && (
        <Search
          queryClient={client}
          searchLocation={searchLocation?.toString()}
          ARRdd={Number(ARRdd)}
          ARRmm={Number(ARRmm)}
          ARRyyyy={Number(ARRyyyy)}
          NIGHTS={Number(NIGHTS)}
          ROOMS={Number(ROOMS)}
          isSummaryActive={false}
          variant={variant}
        />
      )}
    </>
  );
}

export async function getServerSideProps({
  locale = 'gb',
  query,
  req,
  res,
}: GetServerSidePropsContext) {
  const { logger } = await import('@whitbread-eos/utils');

  const cookies = new Cookies(req, res);
  const sessionId = cookies.get(WB_SESSION_ID);
  const queryClient = new QueryClient();
  const { key } = query;
  let hasRegisteredSuccessfully = false;
  const idTokenCookie = cookies.get(ID_TOKEN_COOKIE);
  if (idTokenCookie) {
    const { email } = decodeIdToken(idTokenCookie) || {};
    const axiosProps = {
      method: 'GET',
      url: `${process.env.NEXT_PUBLIC_REST_API}/customers/hotels/${email}?business=true`,
      headers: {
        Authorization: `Bearer ${idTokenCookie}`,
        [WB_SESSION_ID]: sessionId || '',
      },
    };

    await queryClient.prefetchQuery({
      queryKey: ['userDetails', idTokenCookie],
      queryFn: () => axiosRequest(axiosProps),
    });
  }

  if (key) {
    const axiosActivationProps = {
      method: 'POST',
      url: `${process.env.NEXT_PUBLIC_REST_API}/companies/activation?activation-key=${key}`,
      headers: { 'Content-Type': 'application/json' },
      body: {},
    };

    const activateAccountQuery = queryClient.fetchQuery({
      queryKey: ['activateAccount'],
      queryFn: () => axiosRequest(axiosActivationProps),
    });

    await activateAccountQuery
      .then(() => {
        hasRegisteredSuccessfully = true;
      })
      .catch(() => {
        hasRegisteredSuccessfully = false;
      });
  }

  logger.info({
    label: 'BB:MainPage',
    message: 'PageLoad',
  });
  const { language } = getServerSideCustomLocale(locale);

  const labels = await getI18nLabels({
    language,
    queryClient,
  });

  return {
    props: {
      ...labels,
      dehydratedState: dehydrate(queryClient),
      hasRegisteredSuccessfully,
    },
  };
}
