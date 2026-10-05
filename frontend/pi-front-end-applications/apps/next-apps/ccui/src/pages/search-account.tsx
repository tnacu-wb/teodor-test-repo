import { useQueryClient } from '@tanstack/react-query';
import { FS_DISPLAY_GUEST_ACCOUNT, FT_PI_REDIS_RQ_CACHE } from '@whitbread-eos/api';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import {
  getServerSideCustomLocale,
  useFeatureSwitch as isFeatureFlagEnabled,
  getI18nLabels,
  getUnleashToggles,
} from '@whitbread-eos/utils';
import { getPersistentQueryClient } from '@whitbread-eos/utils/server';
import type { GetServerSidePropsContext } from 'next';
import { ReactElement } from 'react';

import { DefaultLayout } from '~components';
import { auth0 } from '~lib/auth0';
import {
  Page as SearchAccountPageCCUI,
  createSearchAccountCCUIDataLoaderFn,
} from '~page-helper/search-account';
import { PAGE } from '~utils/ccui-all-pages-constants';
import { getProxyOptions, setProxyOptionsCookies } from '~utils/proxyOptions';

type SearchAccountProps = {
  accessToken: string;
};

const isGuestAccountEnabled = isFeatureFlagEnabled({
  featureSwitchKey: FS_DISPLAY_GUEST_ACCOUNT,
});

export default function SearchAccountPage({ accessToken }: Readonly<SearchAccountProps>) {
  const queryClient = useQueryClient();
  return <SearchAccountPageCCUI queryClient={queryClient} accessToken={accessToken} />;
}

SearchAccountPage.getLayout = function getLayout(page: ReactElement) {
  return (
    <DefaultLayout>
      <ErrorBoundary>{page}</ErrorBoundary>
    </DefaultLayout>
  );
};

export async function getServerSideProps(context: GetServerSidePropsContext) {
  const { locale = 'gb', ...props } = context;
  const session = await auth0.getSession(props.req);

  if (!session) {
    return {
      redirect: {
        destination: `/auth/login?returnTo=${encodeURIComponent(context.resolvedUrl)}`,
        permanent: false,
      },
    };
  }

  const proxyOptions = getProxyOptions(props, session);
  setProxyOptionsCookies(proxyOptions, props);
  const { language, country } = getServerSideCustomLocale(locale);

  const featureToggles = await getUnleashToggles(
    props,
    PAGE.SEARCH_ACCOUNT.featureToggles.appPage,
    PAGE.SEARCH_ACCOUNT.featureToggles.flagsWithFallback
  );

  const queryClient = getPersistentQueryClient({
    page: PAGE.SEARCH_ACCOUNT.featureToggles.appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });

  const loadedData = await createSearchAccountCCUIDataLoaderFn({
    session,
    queryClient,
    proxyOptions,
    language,
    country,
    ...props,
  });
  const labels = await getI18nLabels({
    language,
    queryClient,
  });

  return {
    props: {
      ...loadedData,
      ...labels,
    },
    notFound: !isGuestAccountEnabled,
  };
}
