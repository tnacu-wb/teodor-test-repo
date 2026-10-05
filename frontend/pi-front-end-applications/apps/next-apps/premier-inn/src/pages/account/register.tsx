import { useQueryClient } from '@tanstack/react-query';
import { FT_PI_REDIS_RQ_CACHE } from '@whitbread-eos/api';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import {
  getServerSideCustomLocale,
  getI18nLabels,
  getUnleashToggles,
  useFeatureToggle,
  GLOBALS,
} from '@whitbread-eos/utils';
import type { GetServerSidePropsContext } from 'next';
import { useRouter } from 'next/router';
import type { ReactElement } from 'react';

import { DefaultLayout } from '~components';
import { Page as RegisterPagePi, createRegisterPiDataLoaderFn } from '~page-helper/register';
import { PAGE } from '~utils/pi-all-pages-constants';
import { clearServerQueryClient, createServerQueryClient } from '~utils/serverQueryClient';

interface DynamicObject {
  [key: string]: boolean;
}

interface Props {
  featureToggles: DynamicObject;
}

export default function RegisterPage({ featureToggles }: Readonly<Props>) {
  const router = useRouter();

  useFeatureToggle(featureToggles);
  return <RegisterPagePi router={router} queryClient={useQueryClient()} />;
}

RegisterPage.getLayout = function getLayout(page: ReactElement) {
  return (
    <DefaultLayout showFooter={false}>
      <ErrorBoundary>{page}</ErrorBoundary>
    </DefaultLayout>
  );
};

export async function getServerSideProps({ locale = 'gb', ...props }: GetServerSidePropsContext) {
  const { language, country } = getServerSideCustomLocale(locale);

  const featureToggles: DynamicObject = await getUnleashToggles(
    props,
    PAGE.REGISTER.featureToogles.appPage,
    PAGE.REGISTER.featureToogles.flagsWithFallback,
    { country: country ?? GLOBALS.locale.GB }
  );

  const queryClient = createServerQueryClient({
    page: PAGE.REGISTER.featureToogles.appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });
  try {
    const labels = await getI18nLabels({
      language,
      queryClient,
    });

    const loadedData = await createRegisterPiDataLoaderFn({
      queryClient: queryClient,
      language: language,
      country: country,
      ...props,
    });

    return {
      props: {
        ...loadedData,
        ...labels,
        featureToggles,
      },
    };
  } finally {
    clearServerQueryClient(queryClient);
  }
}
