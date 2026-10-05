import { Claims, FS_SHOW_AMEND_PAYMENT_PAGE, FT_PI_REDIS_RQ_CACHE } from '@whitbread-eos/api';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import {
  useFeatureSwitch as isFeatureFlagEnabled,
  getServerSideCustomLocale,
  getI18nLabels,
  getUnleashToggles,
} from '@whitbread-eos/utils';
import { getPersistentQueryClient } from '@whitbread-eos/utils/server';
import { GetServerSidePropsContext } from 'next';
import { ReactElement } from 'react';

import { PaymentLayout } from '~components';
import { auth0 } from '~lib/auth0';
import {
  Page as PaymentPageAmend,
  createAmendPaymentCCUIDataLoaderFn,
} from '~page-helper/amend/payment';
import { PAGE } from '~utils/ccui-all-pages-constants';
import { getProxyOptions, setProxyOptionsCookies } from '~utils/proxyOptions';

export default function PaymentPage(props: Readonly<{ user: Claims }>) {
  return <PaymentPageAmend {...props} />;
}

PaymentPage.getLayout = function getLayout(page: ReactElement) {
  return (
    <PaymentLayout>
      <ErrorBoundary>{page}</ErrorBoundary>
    </PaymentLayout>
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
    PAGE.PAYMENT.featureToggles.appPage,
    PAGE.PAYMENT.featureToggles.flagsWithFallback
  );

  const queryClient = getPersistentQueryClient({
    page: PAGE.PAYMENT.featureToggles.appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });

  const isAmendPaymentPageAvailable = isFeatureFlagEnabled({
    featureSwitchKey: FS_SHOW_AMEND_PAYMENT_PAGE,
    fallbackValue: false,
  });

  const { logger } = await import('@whitbread-eos/utils');

  const labels = await getI18nLabels({
    language,
    queryClient,
  });
  const loadedData = await createAmendPaymentCCUIDataLoaderFn({
    queryClient,
    logger,
    language,
    country,
    proxyOptions,
    session,
    ...props,
  });

  return {
    props: {
      ...labels,
      ...loadedData,
    },
    notFound: !isAmendPaymentPageAvailable,
  };
}
