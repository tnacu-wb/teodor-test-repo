import { FT_PI_REDIS_RQ_CACHE } from '@whitbread-eos/api';
import { getServerSideCustomLocale, getI18nLabels, getUnleashToggles } from '@whitbread-eos/utils';
import { getPersistentQueryClient } from '@whitbread-eos/utils/server';
import { GetServerSidePropsContext } from 'next';

import { auth0 } from '~lib/auth0';
import {
  createDataLoaderCcuiErrors,
  PaymentCcuiProps,
  PaymentsErrorHandling,
} from '~page-helper/payment';
import { PAGE } from '~utils/ccui-all-pages-constants';
import { getProxyOptions, setProxyOptionsCookies } from '~utils/proxyOptions';

export default function PaymentPage(props: Readonly<PaymentCcuiProps>) {
  return <PaymentsErrorHandling {...props} />;
}

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

  const loadedData = await createDataLoaderCcuiErrors({
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
      ...labels,
      ...loadedData,
    },
  };
}
