import { useQueryClient } from '@tanstack/react-query';
import { FT_PI_REDIS_RQ_CACHE } from '@whitbread-eos/api';
import type {
  CONFIRM_AMEND_STATUS,
  AmendConfInput,
  BookingSpinnerConfig,
} from '@whitbread-eos/api';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import {
  getServerSideCustomLocale,
  getI18nLabels,
  getUnleashToggles,
  useFeatureToggle,
} from '@whitbread-eos/utils';
import { getPersistentQueryClient } from '@whitbread-eos/utils/server';
import { GetServerSidePropsContext } from 'next';
import { ReactElement } from 'react';

import { DefaultLayout } from '~components/index';
import { auth0 } from '~lib/auth0';
import {
  Page as AmendBookingConfirmationPageCCUI,
  createBookingConfirmationCCUIDataLoaderFn,
} from '~page-helper/amend/booking-confirmation';
import { PAGE } from '~utils/ccui-all-pages-constants';
import { getProxyOptions, setProxyOptionsCookies } from '~utils/proxyOptions';

interface Props {
  confirmationInput: AmendConfInput;
  amendBookingStatus?: CONFIRM_AMEND_STATUS;
  bookingSpinnerConfig: BookingSpinnerConfig[];
  tempBookingReference: string;
  featureToggles: { [key: string]: boolean };
}
export default function AmendConfirmationPage(props: Readonly<Props>) {
  const {
    confirmationInput,
    amendBookingStatus,
    bookingSpinnerConfig,
    tempBookingReference,
    featureToggles,
  } = props;

  useFeatureToggle(featureToggles);

  return (
    <AmendBookingConfirmationPageCCUI
      queryClient={useQueryClient()}
      confirmationInput={confirmationInput}
      amendBookingStatus={amendBookingStatus}
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

  const featureToggles: { [key: string]: boolean } = await getUnleashToggles(
    props,
    PAGE.AMEND.DETAILS.featureToogles.appPage,
    PAGE.AMEND.DETAILS.featureToogles.flagsWithFallback
  );

  const queryClient = getPersistentQueryClient({
    page: PAGE.AMEND.DETAILS.featureToogles.appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });

  const labels = await getI18nLabels({
    language,
    queryClient,
  });

  const loadedData = await createBookingConfirmationCCUIDataLoaderFn({
    session,
    queryClient,
    proxyOptions,
    language,
    country,
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
