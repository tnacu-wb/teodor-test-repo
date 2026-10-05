import { useQueryClient } from '@tanstack/react-query';
import { FT_PI_REDIS_RQ_CACHE } from '@whitbread-eos/api';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import {
  getUnleashToggles,
  useFeatureToggle,
  getServerSideCustomLocale,
  getI18nLabels,
  GLOBALS,
} from '@whitbread-eos/utils';
import { getPersistentQueryClient } from '@whitbread-eos/utils/server';
import type { GetServerSidePropsContext } from 'next';
import { useRouter } from 'next/router';
import type { ReactElement } from 'react';

import { PriceFinderLayout } from '~components';
import { PriceFinderPageCcui, createPriceFinderCcuiDataLoaderFn } from '~page-helper/price-finder';
import { PAGE } from '~utils/ccui-all-pages-constants';

interface DynamicObject {
  [key: string]: boolean;
}

interface Props {
  featureToggles: DynamicObject;
}

export default function PriceFinderPage({ featureToggles }: Readonly<Props>) {
  const router = useRouter();
  useFeatureToggle(featureToggles);
  return <PriceFinderPageCcui router={router} queryClient={useQueryClient()} />;
}

PriceFinderPage.getLayout = function getLayout(page: ReactElement) {
  return (
    <PriceFinderLayout>
      <ErrorBoundary>{page}</ErrorBoundary>
    </PriceFinderLayout>
  );
};

export async function getServerSideProps({ locale = 'gb', ...props }: GetServerSidePropsContext) {
  const { language, country } = getServerSideCustomLocale(locale);

  const featureToggles: DynamicObject = await getUnleashToggles(
    props,
    PAGE.PRICE_FINDER.featureToggles.appPage,
    PAGE.PRICE_FINDER.featureToggles.flagsWithFallback,
    { country: country ?? GLOBALS.locale.GB }
  );

  const queryClient = getPersistentQueryClient({
    page: PAGE.PRICE_FINDER.featureToggles.appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });

  const labels = await getI18nLabels({
    language,
    queryClient,
  });

  const loadedData = await createPriceFinderCcuiDataLoaderFn({
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
