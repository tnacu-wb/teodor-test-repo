import { QueryClient, useQueryClient } from '@tanstack/react-query';
import { FT_PI_PRE_CHECK_IN } from '@whitbread-eos/api';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import {
  getI18nLabels,
  getServerSideCustomLocale,
  getUnleashToggles,
  useFeatureToggle,
  GLOBALS,
} from '@whitbread-eos/utils';
import type { GetServerSidePropsContext } from 'next';
import { useRouter } from 'next/router';
import type { ReactElement } from 'react';

import { DefaultLayout, SecondaryHDPLayout } from '~components';
import { Page as PreCheckIn } from '~page-helper/pre-check-in';
import { PAGE } from '~utils/pi-all-pages-constants';

interface DynamicObject {
  [key: string]: boolean;
}

interface Props {
  featureToggles: DynamicObject;
}

export default function PreCheckInPage(props: Readonly<Props>) {
  const queryClient = useQueryClient();
  useFeatureToggle(props.featureToggles);

  return <PreCheckIn queryClient={queryClient} />;
}

PreCheckInPage.getLayout = function GetLayout(page: ReactElement) {
  const { query } = useRouter();
  const Layout = query && Object.entries(query)?.length ? SecondaryHDPLayout : DefaultLayout;
  return (
    <Layout>
      <ErrorBoundary>{page}</ErrorBoundary>
    </Layout>
  );
};

export async function getServerSideProps({ locale = 'gb', ...props }: GetServerSidePropsContext) {
  const queryClient = new QueryClient();
  const { language, country } = getServerSideCustomLocale(locale);

  const featureToggles: DynamicObject = await getUnleashToggles(
    props,
    PAGE.PRE_CHECK_IN.featureToogles.appPage,
    PAGE.PRE_CHECK_IN.featureToogles.flagsWithFallback,
    { country: country || GLOBALS.locale.GB }
  );

  const { [FT_PI_PRE_CHECK_IN]: preCheckIn } = featureToggles;

  const labels = await getI18nLabels({
    language,
    queryClient,
    fetchPreCheckInLabel: true,
    forceRefetch: true,
  });

  return preCheckIn ? { props: { ...labels, featureToggles } } : { notFound: true };
}
