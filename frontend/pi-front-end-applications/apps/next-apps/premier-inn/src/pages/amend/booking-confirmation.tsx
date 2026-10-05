import { useQueryClient } from '@tanstack/react-query';
import {
  AmendConfInput,
  Area,
  BookingSpinnerConfig,
  CONFIRM_AMEND_STATUS,
  FT_PI_REDIS_RQ_CACHE,
} from '@whitbread-eos/api';
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
import {
  createBookingConfirmationPiDataLoaderFn,
  Page as AmendBookingConfirmationPagePi,
} from '~page-helper/amend/booking-confirmation';
import { clearServerQueryClient, createServerQueryClient } from '~utils/serverQueryClient';

import { PAGE } from '../../utils/pi-all-pages-constants';

interface Props {
  confirmationInput: AmendConfInput;
  amendBookingStatus?: CONFIRM_AMEND_STATUS;
  email?: string;
  bookingSpinnerConfig: BookingSpinnerConfig[];
  tempBookingReference: string;
  featureToggles: { [key: string]: boolean };
}

export default function AmendConfirmationPage(props: Readonly<Props>) {
  const {
    confirmationInput,
    amendBookingStatus,
    email,
    bookingSpinnerConfig,
    tempBookingReference,
    featureToggles,
  } = props;

  useFeatureToggle(featureToggles);

  return (
    <AmendBookingConfirmationPagePi
      variant={Area.PI}
      queryClient={useQueryClient()}
      confirmationInput={confirmationInput}
      amendBookingStatus={amendBookingStatus}
      email={email}
      bookingSpinnerConfig={bookingSpinnerConfig}
      tempBookingReference={tempBookingReference}
    />
  );
}

AmendConfirmationPage.getLayout = function getLayout(page: ReactElement) {
  return (
    <DefaultLayout
      showFooter={false}
      containerStyles={{
        px: { base: '1rem', md: '1.5rem', lg: '1.75rem', xl: '4.125rem' },
      }}
    >
      <ErrorBoundary>{page}</ErrorBoundary>
    </DefaultLayout>
  );
};

export async function getServerSideProps({ locale = 'gb', ...props }: GetServerSidePropsContext) {
  const { logger } = await import('@whitbread-eos/utils');
  const { appPage, flagsWithFallback } = PAGE.AMEND.DETAILS.featureToogles;
  const { language, country } = getServerSideCustomLocale(locale);

  const featureToggles: { [key: string]: boolean } = await getUnleashToggles(
    props,
    appPage,
    flagsWithFallback,
    { country: country ?? GLOBALS.locale.GB }
  );

  const queryClient = createServerQueryClient({
    page: appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });
  try {
    const labels = await getI18nLabels({
      language,
      queryClient,
    });
    const loadedData = await createBookingConfirmationPiDataLoaderFn({
      logger: logger,
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
