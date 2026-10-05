import { useQueryClient } from '@tanstack/react-query';
import { Claims, FT_CCUI_UNIQUE_PROMO_CODE, FT_PI_REDIS_RQ_CACHE } from '@whitbread-eos/api';
import {
  getServerSideCustomLocale,
  getI18nLabels,
  getUnleashToggles,
  useFeatureToggle,
  logger,
  isPromoAdmin,
} from '@whitbread-eos/utils';
import { getPersistentQueryClient } from '@whitbread-eos/utils/server';
import { GetServerSidePropsContext } from 'next';
import React, { ReactElement } from 'react';

import { DefaultLayout } from '~components';
import { auth0 } from '~lib/auth0';
import { Page, promoBatchListCCUIDataLoaderFn } from '~page-helper/unique-promotions/list';
import { PAGE } from '~utils/ccui-all-pages-constants';
import { getProxyOptions, setProxyOptionsCookies } from '~utils/proxyOptions';

interface Props {
  featureToggles: { [key: string]: boolean };
  user: Claims;
  variant: string;
}
export default function UniquePromoPage(props: Readonly<Props>) {
  const { featureToggles, user } = props;
  useFeatureToggle(featureToggles);
  return <Page queryClient={useQueryClient()} user={user} />;
}

UniquePromoPage.getLayout = function getLayout(page: ReactElement) {
  return (
    <DefaultLayout showFooter={false}>
      <UniquePromoPage {...page?.props}></UniquePromoPage>
    </DefaultLayout>
  );
};

export async function getServerSideProps(context: GetServerSidePropsContext) {
  const { locale = 'gb', ...props } = context;
  const { req } = context;
  const session = await auth0.getSession(req);

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
    PAGE.UNIQUE_PROMOTIONS.featureToggles.appPage,
    PAGE.UNIQUE_PROMOTIONS.featureToggles.flagsWithFallback
  );

  const queryClient = getPersistentQueryClient({
    page: PAGE.UNIQUE_PROMOTIONS.featureToggles.appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });

  const labels = await getI18nLabels({
    language,
    queryClient,
  });

  const loadedData = await promoBatchListCCUIDataLoaderFn({
    session,
    logger,
    queryClient,
    proxyOptions,
    language,
    country,
    ...props,
  });

  const isPromoAdminUser = isPromoAdmin(loadedData?.user);
  const isUniquePromoCodeEnabled = featureToggles[FT_CCUI_UNIQUE_PROMO_CODE];
  const isPromoCodeEnabled = isUniquePromoCodeEnabled && isPromoAdminUser;

  if (!isPromoCodeEnabled) {
    return { notFound: true };
  }

  return {
    props: {
      featureToggles,
      ...loadedData,
      ...labels,
    },
  };
}
