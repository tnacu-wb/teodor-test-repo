import { useQueryClient } from '@tanstack/react-query';
import type {
  QueryHotelInformationArgs,
  PackagesCriteria,
  QueryBookingInformationArgs,
} from '@whitbread-eos/api';
import { UnleashChannel, FT_PI_REDIS_RQ_CACHE } from '@whitbread-eos/api';
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

import { AncillariesLayout } from '~components';
import {
  createAncillariesPiDataLoaderFn,
  Page as AncillariesPagePi,
} from '~page-helper/ancillaries';
import { PAGE } from '~utils/pi-all-pages-constants';
import { clearServerQueryClient, createServerQueryClient } from '~utils/serverQueryClient';

interface Props {
  hiQueryInput: QueryHotelInformationArgs;
  pcksQueryInput: PackagesCriteria;
  biQueryInput: QueryBookingInformationArgs;
  featureToggles: DynamicObject;
}

interface DynamicObject {
  [key: string]: boolean;
}

export default function AncillariesPage({
  hiQueryInput,
  pcksQueryInput,
  biQueryInput,
  featureToggles,
}: Readonly<Props>) {
  const router = useRouter();
  useFeatureToggle(featureToggles);

  return (
    <AncillariesPagePi
      router={router}
      queryClient={useQueryClient()}
      biQueryInput={biQueryInput}
      pcksQueryInput={pcksQueryInput}
      hiQueryInput={hiQueryInput}
    />
  );
}

AncillariesPage.getLayout = function getLayout(page: ReactElement) {
  return (
    <AncillariesLayout>
      <ErrorBoundary>{page}</ErrorBoundary>
    </AncillariesLayout>
  );
};

export async function getServerSideProps({ locale = 'gb', ...props }: GetServerSidePropsContext) {
  const { language, country } = getServerSideCustomLocale(locale);

  const featureToggles: DynamicObject = await getUnleashToggles(
    props,
    PAGE.ANCILLARIES.featureToogles.appPage,
    PAGE.ANCILLARIES.featureToogles.flagsWithFallback,
    { country: country || GLOBALS.locale.GB, channel: UnleashChannel.PI, pageName: 'ANCILLARIES' }
  );

  const queryClient = createServerQueryClient({
    page: PAGE.ANCILLARIES.featureToogles.appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });
  try {
    const labels = await getI18nLabels({
      language,
      queryClient,
    });

    const loadedData = await createAncillariesPiDataLoaderFn({
      queryClient: queryClient,
      language: language,
      country: country,
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
