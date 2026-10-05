import { useQueryClient } from '@tanstack/react-query';
import { CCUI_LOCALE_COOKIE, FT_PI_REDIS_RQ_CACHE } from '@whitbread-eos/api';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import { CCUISearchContainer as Search } from '@whitbread-eos/organisms';
import { getI18nLabels, getUnleashToggles, useFeatureToggle } from '@whitbread-eos/utils';
import { getPersistentQueryClient } from '@whitbread-eos/utils/server';
import { getCookie } from 'cookies-next';
import { GetServerSidePropsContext } from 'next';
import { useRouter } from 'next/router';
import { ReactElement } from 'react';

import { DefaultLayout } from '~components';
import { auth0 } from '~lib/auth0';
import { CCUI_ROLES } from '~types/general';
import { PAGE } from '~utils/ccui-all-pages-constants';
import { getProxyOptions, setProxyOptionsCookies } from '~utils/proxyOptions';

interface Props {
  featureToggles: { [key: string]: boolean };
}
export default function HomePage(props: Readonly<Props>) {
  const router = useRouter();
  const client = useQueryClient();
  const { searchLocation, ARRdd, ARRmm, ARRyyyy, NIGHTS, ROOMS } = router.query;
  const { featureToggles } = props;
  useFeatureToggle(featureToggles);

  return (
    <Search
      queryClient={client}
      searchLocation={searchLocation?.toString()}
      ARRdd={Number(ARRdd)}
      ARRmm={Number(ARRmm)}
      ARRyyyy={Number(ARRyyyy)}
      NIGHTS={Number(NIGHTS)}
      ROOMS={Number(ROOMS)}
      isSummaryActive={false}
    />
  );
}

HomePage.getLayout = function getLayout(page: ReactElement) {
  return (
    <DefaultLayout>
      <ErrorBoundary>{page}</ErrorBoundary>
    </DefaultLayout>
  );
};

export async function getServerSideProps(context: GetServerSidePropsContext) {
  const {
    locale = 'gb',
    req,
    res,
    query: { language = 'en' },
  } = context;
  const session = await auth0.getSession(req);
  if (!session) {
    return {
      redirect: {
        destination: `/auth/login?returnTo=${encodeURIComponent(context.resolvedUrl)}`,
        permanent: false,
      },
    };
  }
  const proxyOptions = getProxyOptions({ req, res }, session);
  setProxyOptionsCookies(proxyOptions, { req, res });
  const user = session.user;
  const roles = session.user[CCUI_ROLES];

  const featureToggles: { [key: string]: boolean } = await getUnleashToggles(
    context,
    PAGE.SEARCH.featureToggles.appPage,
    PAGE.SEARCH.featureToggles.flagsWithFallback
  );

  const queryClient = getPersistentQueryClient({
    page: PAGE.SEARCH.featureToggles.appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });

  const labels = await getI18nLabels({
    language: language as string,
    queryClient,
  });

  const props = {
    ...labels,
    featureToggles,
    roles,
    user,
  };

  const auth0Locale = user?.wb_account_locale === 'en' ? 'gb' : 'de';
  const cookie = getCookie(CCUI_LOCALE_COOKIE, { req, res });
  const selectedLocale = cookie || auth0Locale || 'gb';

  if (locale !== selectedLocale || (language === 'en' && selectedLocale === 'de')) {
    return {
      redirect: {
        permanent: false,
        destination: selectedLocale === 'de' ? '/de/de' : '/gb/en',
      },
      props,
    };
  }

  return { props };
}
