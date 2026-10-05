import { useQueryClient } from '@tanstack/react-query';
import { Claims, FT_PI_REDIS_RQ_CACHE } from '@whitbread-eos/api';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import {
  setAnalyticsUser,
  getServerSideCustomLocale,
  getI18nLabels,
  getUnleashToggles,
  useFeatureToggle,
} from '@whitbread-eos/utils';
import { getPersistentQueryClient } from '@whitbread-eos/utils/server';
import type { GetServerSidePropsContext } from 'next';
import { useRouter } from 'next/router';
import { ReactElement } from 'react';

import { BookingsLayout } from '~components';
import { auth0 } from '~lib/auth0';
import { Page as BookingsPageCcui, createBookingsCcuiDataLoaderFn } from '~page-helper/bookings';
import { PAGE } from '~utils/ccui-all-pages-constants';
import { getProxyOptions, setProxyOptionsCookies } from '~utils/proxyOptions';

interface Props {
  user: Claims;
  featureToggles: { [key: string]: boolean };
}

export default function BookingsPage({ user, featureToggles }: Readonly<Props>) {
  const router = useRouter();
  const queryClient = useQueryClient();
  useFeatureToggle(featureToggles);

  return (
    <BookingsPageCcui
      queryClient={queryClient}
      router={router}
      user={user}
      setAnalyticsUser={setAnalyticsUser}
      variant="ccui"
    />
  );
}

BookingsPage.getLayout = function getLayout(page: ReactElement) {
  return (
    <BookingsLayout>
      <ErrorBoundary>{page}</ErrorBoundary>
    </BookingsLayout>
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

  const { logger } = await import('@whitbread-eos/utils');
  const proxyOptions = getProxyOptions(props, session);
  setProxyOptionsCookies(proxyOptions, props);
  const { language, country } = getServerSideCustomLocale(locale);

  const featureToggles: { [key: string]: boolean } = await getUnleashToggles(
    props,
    PAGE.PAGE_HELPER.BOOKINGS.featureToggles.appPage,
    PAGE.PAGE_HELPER.BOOKINGS.featureToggles.flagsWithFallback
  );

  const queryClient = getPersistentQueryClient({
    page: PAGE.PAGE_HELPER.BOOKINGS.featureToggles.appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });

  const loadedData = await createBookingsCcuiDataLoaderFn({
    session,
    logger,
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
      featureToggles,
      ...loadedData,
      ...labels,
    },
  };
}
