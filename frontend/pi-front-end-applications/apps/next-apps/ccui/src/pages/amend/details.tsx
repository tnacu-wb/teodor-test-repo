import { useQueryClient } from '@tanstack/react-query';
import { FT_PI_REDIS_RQ_CACHE } from '@whitbread-eos/api';
import type { AmendConfInput } from '@whitbread-eos/api';
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

import { DefaultLayout } from '~components/index';
import { auth0 } from '~lib/auth0';
import { Page as AmendPageCCUI, createAmendCCUIDataLoaderFn } from '~page-helper/amend/details';
import { PAGE } from '~utils/ccui-all-pages-constants';
import { getProxyOptions, setProxyOptionsCookies } from '~utils/proxyOptions';

interface Props {
  confirmationInput: AmendConfInput;
  featureToggles: { [key: string]: boolean };
}
export default function AmendDetailsPage(props: Readonly<Props>) {
  const { confirmationInput, featureToggles } = props;
  useFeatureToggle(featureToggles);
  return <AmendPageCCUI confirmationInput={confirmationInput} queryClient={useQueryClient()} />;
}

AmendDetailsPage.getLayout = function getLayout(page: ReactElement) {
  return (
    <DefaultLayout
      showFooter={false}
      containerStyles={{ px: { base: 0, lg: '1.75rem', xl: '4.125rem' } }}
    >
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

  const { logger } = await import('@whitbread-eos/utils');
  const proxyOptions = getProxyOptions(props, session);
  setProxyOptionsCookies(proxyOptions, props);
  const { language, country } = getServerSideCustomLocale(locale);

  const featureToggles: { [key: string]: boolean } = await getUnleashToggles(
    props,
    PAGE.AMEND.DETAILS.featureToogles.appPage,
    PAGE.AMEND.DETAILS.featureToogles.flagsWithFallback
  );

  const queryClient = getPersistentQueryClient({
    page: PAGE.AMEND.DETAILS.featureToogles.appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });

  const labels = await getI18nLabels({
    language,
    queryClient,
  });

  const loadedData = await createAmendCCUIDataLoaderFn({
    session,
    logger,
    queryClient,
    proxyOptions,
    language,
    country,
    ...props,
  });

  return {
    props: {
      featureToggles,
      ...loadedData,
      ...labels,
    },
  };
}
