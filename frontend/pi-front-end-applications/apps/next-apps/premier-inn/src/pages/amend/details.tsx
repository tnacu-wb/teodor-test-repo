import { useQueryClient } from '@tanstack/react-query';
import { AmendConfInput, Area, PackagesCriteria, FT_PI_REDIS_RQ_CACHE } from '@whitbread-eos/api';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import {
  getI18nLabels,
  getServerSideCustomLocale,
  getUnleashToggles,
  useFeatureToggle,
  GLOBALS,
} from '@whitbread-eos/utils';
import { GetServerSidePropsContext } from 'next';
import { ReactElement } from 'react';

import { DefaultLayout } from '~components/index';
import { createAmendPiDataLoaderFn, Page as AmendPagePi } from '~page-helper/amend/details';
import { clearServerQueryClient, createServerQueryClient } from '~utils/serverQueryClient';

import { PAGE } from '../../utils/pi-all-pages-constants';

interface Props {
  confirmationInput: AmendConfInput;
  pcksQueryInput: PackagesCriteria;
  featureToggles: { [key: string]: boolean };
  tempBookingReference?: string | null;
  status?: string | null;
}
export default function AmendDetailsPage({
  pcksQueryInput,
  confirmationInput,
  featureToggles,
  tempBookingReference,
  status,
}: Readonly<Props>) {
  useFeatureToggle(featureToggles);
  return (
    <AmendPagePi
      confirmationInput={confirmationInput}
      pcksQueryInput={pcksQueryInput}
      queryClient={useQueryClient()}
      variant={Area.PI}
      tempBookingReference={tempBookingReference}
      status={status}
    />
  );
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

export async function getServerSideProps({
  locale = GLOBALS.locale.GB,
  ...props
}: GetServerSidePropsContext) {
  const { logger } = await import('@whitbread-eos/utils');
  const amendDetailsFeatureToggles = PAGE.AMEND.DETAILS.featureToogles;
  const { language, country } = getServerSideCustomLocale(locale);

  // Required feature toggles used by this page with fallback(safe value)
  // if the Unleash is down or this flag is not configured in unleash

  const featureToggles: { [key: string]: boolean } = await getUnleashToggles(
    props,
    amendDetailsFeatureToggles.appPage,
    amendDetailsFeatureToggles.flagsWithFallback,
    { country: country ?? GLOBALS.locale.GB }
  );
  const isRedisRqCacheEnabled = Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]);

  const queryClient = createServerQueryClient({
    page: amendDetailsFeatureToggles.appPage,
    enabled: isRedisRqCacheEnabled,
  });
  try {
    const labels = await getI18nLabels({
      language,
      queryClient,
    });

    const loadedData = await createAmendPiDataLoaderFn({
      logger,
      queryClient,
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
  } finally {
    clearServerQueryClient(queryClient);
  }
}
