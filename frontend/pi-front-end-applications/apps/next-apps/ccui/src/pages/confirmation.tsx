//<editor-fold desc="Imports" defaultstate="collapsed">
import { FT_PI_REDIS_RQ_CACHE } from '@whitbread-eos/api';
import type { Claims, PackagesCriteria, QueryHotelInformationArgs } from '@whitbread-eos/api';
import { PageName, UnleashChannel } from '@whitbread-eos/api';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import {
  getServerSideCustomLocale,
  setAnalyticsUser,
  getI18nLabels,
  getUnleashToggles,
  GLOBALS,
  useFeatureToggle,
} from '@whitbread-eos/utils';
import { getPersistentQueryClient } from '@whitbread-eos/utils/server';
import type { GetServerSidePropsContext } from 'next';
import { useRouter } from 'next/router';
import { ReactElement } from 'react';

import { ConfirmationLayout } from '~components';
import { auth0 } from '~lib/auth0';
import ConfirmationPageCcui, {
  createConfirmationCcuiDataLoaderFn,
} from '~page-helper/confirmation';
import { PAGE } from '~utils/ccui-all-pages-constants';
import { getProxyOptions, setProxyOptionsCookies } from '~utils/proxyOptions';

//</editor-fold>

interface Props {
  hiQueryInput: QueryHotelInformationArgs;
  pcksQueryInput: PackagesCriteria;
  user: Claims;
  basketReference: string | null;
  featureToggles: DynamicObject;
}
interface DynamicObject {
  [key: string]: boolean;
}

export default function ConfirmationPage({
  hiQueryInput,
  user,
  basketReference,
  pcksQueryInput,
  featureToggles,
}: Readonly<Props>) {
  const router = useRouter();
  useFeatureToggle(featureToggles);

  return (
    <ConfirmationPageCcui
      router={router}
      setAnalyticsUser={setAnalyticsUser}
      user={user}
      pcksQueryInput={pcksQueryInput}
      hiQueryInput={hiQueryInput}
      basketReference={basketReference}
    />
  );
}

//<editor-fold desc="Layout" defaultstate="collapsed">
ConfirmationPage.getLayout = function getLayout(page: ReactElement) {
  return (
    <ConfirmationLayout>
      <ErrorBoundary>{page}</ErrorBoundary>
    </ConfirmationLayout>
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
    PAGE.CONFIRMATION.featureToggles.appPage,
    PAGE.CONFIRMATION.featureToggles.flagsWithFallback,
    {
      country: country || GLOBALS.locale.GB,
      channel: UnleashChannel.CCUI,
      pageName: PageName.CONFIRMATION.toUpperCase(),
    }
  );

  const queryClient = getPersistentQueryClient({
    page: PAGE.CONFIRMATION.featureToggles.appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });

  const loadedData = await createConfirmationCcuiDataLoaderFn({
    language: language,
    queryClient: queryClient,
    country: country,
    session: session,
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
//</editor-fold>
