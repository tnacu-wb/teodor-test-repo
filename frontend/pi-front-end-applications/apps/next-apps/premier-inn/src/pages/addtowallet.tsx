import { useQueryClient } from '@tanstack/react-query';
import { FT_PI_REDIS_RQ_CACHE } from '@whitbread-eos/api';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import {
  useFeatureToggle,
  getServerSideCustomLocale,
  getI18nLabels,
  getUnleashToggles,
  GLOBALS,
} from '@whitbread-eos/utils';
import { getPersistentQueryClient } from '@whitbread-eos/utils/server';
import type { GetServerSidePropsContext } from 'next';
import { useRouter } from 'next/router';
import type { ReactElement } from 'react';

import { AddToWalletLayout } from '~components';
import { AddToWalletPagePi, createAddToWalletPiDataLoaderFn } from '~page-helper/add-to-wallet';
import { PAGE } from '~utils/pi-all-pages-constants';

interface DynamicObject {
  [key: string]: boolean;
}

interface Props {
  featureToggles: DynamicObject;
}

export default function AddToWalletPage({ featureToggles }: Readonly<Props>) {
  const router = useRouter();
  useFeatureToggle(featureToggles);
  return <AddToWalletPagePi router={router} queryClient={useQueryClient()} />;
}

AddToWalletPage.getLayout = function getLayout(page: ReactElement) {
  return (
    <AddToWalletLayout>
      <ErrorBoundary>{page}</ErrorBoundary>
    </AddToWalletLayout>
  );
};

export async function getServerSideProps({ locale = 'gb', ...props }: GetServerSidePropsContext) {
  const { language, country } = getServerSideCustomLocale(locale);

  const featureToggles: DynamicObject = await getUnleashToggles(
    props,
    PAGE.ADD_TO_WALLET.featureToggles.appPage,
    PAGE.ADD_TO_WALLET.featureToggles.flagsWithFallback,
    { country: country ?? GLOBALS.locale.GB }
  );

  const queryClient = getPersistentQueryClient({
    page: PAGE.ADD_TO_WALLET.featureToggles.appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });

  const labels = await getI18nLabels({
    language,
    queryClient,
  });

  const loadedData = await createAddToWalletPiDataLoaderFn({
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
}
