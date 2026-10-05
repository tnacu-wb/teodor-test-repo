import { useQueryClient } from '@tanstack/react-query';
import { Channel, FT_PI_REDIS_RQ_CACHE } from '@whitbread-eos/api';
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

import { SecondaryHDPLayout } from '~components';
import { useScreenSize } from '~hooks/use-screensize';
import {
  ChooseRoomTypePagePI,
  createChooseRoomTypePiDataLoaderFn,
} from '~page-helper/hotel-details/choose-roomtype/index';
import { PAGE } from '~utils/pi-all-pages-constants';
import { clearServerQueryClient, createServerQueryClient } from '~utils/serverQueryClient';

interface Props {
  featureToggles: { [key: string]: boolean };
}

export default function ChooseRoomTypePage({ featureToggles }: Props) {
  const queryClient = useQueryClient();
  const { isLessThanXs, isLessThanSm, isLessThanMd, isLessThanLg } = useScreenSize();
  useFeatureToggle(featureToggles);
  return (
    <ChooseRoomTypePagePI
      {...{
        queryClient,
        visualDisplayContext: {
          isLessThanXs,
          isLessThanSm,
          isLessThanMd,
          isLessThanLg,
        },
      }}
      channel={Channel.Pi}
    />
  );
}

ChooseRoomTypePage.getLayout = function getLayout(page: ReactElement) {
  return (
    <SecondaryHDPLayout>
      <ErrorBoundary>{page}</ErrorBoundary>
    </SecondaryHDPLayout>
  );
};

export async function getServerSideProps({ locale = 'gb', ...props }: GetServerSidePropsContext) {
  const { language, country } = getServerSideCustomLocale(locale);

  const featureToggles: { [key: string]: boolean } = await getUnleashToggles(
    props,
    PAGE.CHOOSE_ROOMTYPE.featureToggles.appPage,
    PAGE.CHOOSE_ROOMTYPE.featureToggles.flagsWithFallback,
    { country: country ?? GLOBALS.locale.GB }
  );

  const queryClient = createServerQueryClient({
    page: PAGE.CHOOSE_ROOMTYPE.featureToggles.appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });
  try {
    const labels = await getI18nLabels({
      language,
      queryClient,
    });
    const loadedData = await createChooseRoomTypePiDataLoaderFn({
      queryClient,
      language,
      country,
      featureToggles,
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
