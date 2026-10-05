import { FT_PI_REDIS_RQ_CACHE } from '@whitbread-eos/api';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import {
  getServerSideCustomLocale,
  getI18nLabels,
  getUnleashToggles,
  useFeatureToggle,
} from '@whitbread-eos/utils';
import { getPersistentQueryClient } from '@whitbread-eos/utils/server';
import { GetServerSidePropsContext } from 'next';
import { ReactElement } from 'react';

import { DefaultLayout } from '~components';
import { auth0 } from '~lib/auth0';
import { PAGE } from '~utils/ccui-all-pages-constants';
import { getProxyOptions, setProxyOptionsCookies } from '~utils/proxyOptions';

import { RepeatBookingPage, createRepeatPageCcuiDataLoaderFn } from '../page-helper/repeat-booking';

export default function RepeatPage({
  featureToggles,
}: Readonly<{ featureToggles: { [key: string]: boolean } }>) {
  useFeatureToggle(featureToggles);
  return <RepeatBookingPage />;
}
RepeatPage.getLayout = function getLayout(page: ReactElement) {
  return (
    <DefaultLayout showFooter={true}>
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

  const featureToggles: { [key: string]: boolean } = await getUnleashToggles(
    props,
    PAGE.PAGE_HELPER.REPEAT_BOOKING.featureToggles.appPage,
    PAGE.PAGE_HELPER.REPEAT_BOOKING.featureToggles.flagsWithFallback
  );

  const queryClient = getPersistentQueryClient({
    page: PAGE.PAGE_HELPER.REPEAT_BOOKING.featureToggles.appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });

  const loadedData = await createRepeatPageCcuiDataLoaderFn({
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
      featureToggles,
      ...loadedData,
      ...labels,
    },
  };
}
