//<editor-fold desc="Imports" defaultstate="collapsed">
import { useQueryClient } from '@tanstack/react-query';
import type {
  QueryBookingInformationArgs,
  QueryHotelInformationArgs,
  PackagesCriteria,
  Claims,
} from '@whitbread-eos/api';
import { FT_PI_REDIS_RQ_CACHE, UnleashChannel } from '@whitbread-eos/api';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import {
  getServerSideCustomLocale,
  setAnalyticsUser,
  getI18nLabels,
  useFeatureToggle,
  getUnleashToggles,
  GLOBALS,
} from '@whitbread-eos/utils';
import { getPersistentQueryClient } from '@whitbread-eos/utils/server';
import type { GetServerSidePropsContext } from 'next';
import { useRouter } from 'next/router';
import type { ReactElement } from 'react';

import { AncillariesLayout } from '~components';
import { auth0 } from '~lib/auth0';
import {
  createAncillariesCcuiDataLoaderFn,
  Page as AncillariesPageCcui,
} from '~page-helper/ancillaries';
import { PAGE } from '~utils/ccui-all-pages-constants';
import { getProxyOptions, setProxyOptionsCookies } from '~utils/proxyOptions';

interface DynamicObject {
  [key: string]: boolean;
}
interface Props {
  hiQueryInput: QueryHotelInformationArgs;
  pcksQueryInput: PackagesCriteria;
  biQueryInput: QueryBookingInformationArgs;
  user: Claims;
  featureToggles: DynamicObject;
}

export default function AncillariesPage({
  pcksQueryInput,
  biQueryInput,
  hiQueryInput,
  user,
  featureToggles,
}: Readonly<Props>) {
  useFeatureToggle(featureToggles);
  const router = useRouter();
  const queryClient = useQueryClient();

  return (
    <AncillariesPageCcui
      queryClient={queryClient}
      pcksQueryInput={pcksQueryInput}
      hiQueryInput={hiQueryInput}
      biQueryInput={biQueryInput}
      router={router}
      user={user}
      setAnalyticsUser={setAnalyticsUser}
    />
  );
}

//<editor-fold desc="Layout" defaultstate="collapsed">
AncillariesPage.getLayout = function getLayout(page: ReactElement) {
  return (
    <AncillariesLayout>
      <ErrorBoundary>{page}</ErrorBoundary>
    </AncillariesLayout>
  );
};
//</editor-fold>

//<editor-fold desc="getServerSideProps" defaultstate="collapsed">
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
    PAGE.ANCILLARIES.featureToogles.appPage,
    PAGE.ANCILLARIES.featureToogles.flagsWithFallback,
    { country: country || GLOBALS.locale.GB, channel: UnleashChannel.CCUI, pageName: 'ANCILLARIES' }
  );

  const queryClient = getPersistentQueryClient({
    page: PAGE.ANCILLARIES.featureToogles.appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });

  const loadedData = await createAncillariesCcuiDataLoaderFn({
    language: language,
    queryClient: queryClient,
    country: country,
    session: session,
    proxyOptions: proxyOptions,
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
//</editor-fold>
