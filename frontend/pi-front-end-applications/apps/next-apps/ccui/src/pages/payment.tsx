import { FT_PI_REDIS_RQ_CACHE, PageName, UnleashChannel } from '@whitbread-eos/api';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import {
  getServerSideCustomLocale,
  getI18nLabels,
  useFeatureToggle,
  getUnleashToggles,
} from '@whitbread-eos/utils';
import { getPersistentQueryClient } from '@whitbread-eos/utils/server';
import { GetServerSidePropsContext } from 'next';
import { ReactElement } from 'react';

import { PaymentLayout } from '~components';
import { auth0 } from '~lib/auth0';
import { createDataLoaderCcui, PaymentCcuiProps, PaymentPageCcui } from '~page-helper/payment';
import { PAGE } from '~utils/ccui-all-pages-constants';
import { getProxyOptions, setProxyOptionsCookies } from '~utils/proxyOptions';

interface HiddenFeatures {
  DISCOUNT_SECTION: false;
  PAYMENT_METHOD_TYPES: Array<{
    name?: string;
    type: string;
  }>;
}

const HIDDEN_FEATURES: HiddenFeatures = {
  DISCOUNT_SECTION: false,
  PAYMENT_METHOD_TYPES: [],
};

interface DynamicObject {
  [key: string]: boolean;
}

export default function PaymentPage(props: Readonly<PaymentCcuiProps>) {
  useFeatureToggle(props.featureToggles);
  return <PaymentPageCcui {...props} hiddenFeatures={HIDDEN_FEATURES} />;
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

  const featureToggles: DynamicObject = await getUnleashToggles(
    props,
    PAGE.PAYMENT.featureToggles.appPage,
    PAGE.PAYMENT.featureToggles.flagsWithFallback,
    {
      basketReference: props.query?.reservationId as string,
      channel: UnleashChannel.CCUI,
      pageName: PageName.PAYMENT.toUpperCase(),
    }
  );

  const queryClient = getPersistentQueryClient({
    page: PAGE.PAYMENT.featureToggles.appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });

  const loadedData = await createDataLoaderCcui({
    queryClient,
    session,
    language,
    country,
    proxyOptions,
    ...props,
  });

  const labels = await getI18nLabels({
    language,
    queryClient,
  });

  return {
    props: {
      featureToggles,
      ...labels,
      ...loadedData,
    },
  };
}
