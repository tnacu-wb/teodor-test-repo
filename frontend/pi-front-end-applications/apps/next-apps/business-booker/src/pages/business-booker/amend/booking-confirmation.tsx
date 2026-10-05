import { useQueryClient } from '@tanstack/react-query';
import type {
  CONFIRM_AMEND_STATUS,
  AmendConfInput,
  BookingSpinnerConfig,
  InnBusinessServerSideProps,
} from '@whitbread-eos/api';
import { Area, FT_PI_REDIS_RQ_CACHE } from '@whitbread-eos/api';
import { Container, ErrorBoundary } from '@whitbread-eos/atoms';
import {
  getServerSideCustomLocale,
  getI18nLabels,
  getUnleashToggles,
  ID_TOKEN_COOKIE,
  isInnBusinessApp,
} from '@whitbread-eos/utils';
import { getPersistentQueryClient } from '@whitbread-eos/utils/server';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';
import { ReactElement } from 'react';

import { DefaultLayout } from '~components/index';
import InnBusinessLayout from '~components/innBusiness/InnBusinessLayout';
import {
  Page as AmendBookingConfirmationPageBb,
  createBookingConfirmationBbDataLoaderFn,
} from '~page-helper/amend/booking-confirmation';
import { PAGE } from '~utils/bb-all-pages-constants';
import withPiTokenRedirect from '~utils/withPiTokenRedirect';

import { innBusinessLoginRedirect } from '../search';

interface Props {
  confirmationInput: AmendConfInput;
  amendBookingStatus?: CONFIRM_AMEND_STATUS;
  bookingSpinnerConfig: BookingSpinnerConfig[];
  tempBookingReference: string;
  featureToggles: { [key: string]: boolean };
  innBusiness?: InnBusinessServerSideProps;
  isInnBusinessAppPage?: boolean;
}

export default function AmendConfirmationPage(props: Readonly<Props>) {
  const {
    confirmationInput,
    amendBookingStatus,
    bookingSpinnerConfig,
    tempBookingReference,
    innBusiness,
  } = props;

  return (
    <AmendBookingConfirmationPageBb
      variant={Area.BB}
      queryClient={useQueryClient()}
      confirmationInput={confirmationInput}
      amendBookingStatus={amendBookingStatus}
      bookingSpinnerConfig={bookingSpinnerConfig}
      tempBookingReference={tempBookingReference}
      showSearch={!innBusiness}
    />
  );
}

AmendConfirmationPage.getLayout = function getLayout(page: ReactElement<any>) {
  const containerStyles = { px: { base: '1rem', md: '1.5rem', lg: '1.75rem', xl: '4.125rem' } };
  const secureUrl = process?.env?.NEXT_PUBLIC_SECURE2_URL ?? '';

  if (page.props.isInnBusinessAppPage) {
    return (
      <InnBusinessLayout
        serverSideProps={page.props.innBusiness}
        showSidebar={false}
        isBusinessBookerPage={true}
        featureToggle={page.props.featureToggles}
        secureUrl={secureUrl}
      >
        <Container containerStyles={containerStyles}>
          <ErrorBoundary>{page}</ErrorBoundary>
        </Container>
      </InnBusinessLayout>
    );
  }

  return (
    <DefaultLayout showFooter={false} containerStyles={containerStyles}>
      <ErrorBoundary>{page}</ErrorBoundary>
    </DefaultLayout>
  );
};

export const getServerSideProps = withPiTokenRedirect(async function getServerSideProps({
  locale = 'gb',
  ...props
}: GetServerSidePropsContext) {
  const { logger } = await import('@whitbread-eos/utils');
  const cookies = new Cookies(props.req, props.res);
  const idTokenCookie = cookies.get(ID_TOKEN_COOKIE);
  const { language, country } = getServerSideCustomLocale(locale);

  const featureToggles: { [key: string]: boolean } = await getUnleashToggles(
    props,
    PAGE.AMEND.featureToggles.appPage,
    PAGE.AMEND.featureToggles.flagsWithFallback
  );

  const queryClient = getPersistentQueryClient({
    page: PAGE.AMEND.featureToggles.appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });

  const isInnBusinessAppPage = !!isInnBusinessApp(props?.req?.headers?.host ?? '');
  const shouldRedirectToInnBusinessLogin = !idTokenCookie && isInnBusinessAppPage;
  if (shouldRedirectToInnBusinessLogin) {
    return innBusinessLoginRedirect(locale);
  }

  const loadedData = await createBookingConfirmationBbDataLoaderFn({
    logger: logger,
    queryClient: queryClient,
    language: language,
    country: country,
    isInnBusinessAppPage: isInnBusinessAppPage,
    ...props,
  });

  const labels = await getI18nLabels({
    language,
    queryClient,
  });

  return {
    props: {
      ...loadedData,
      ...labels,
      featureToggles,
      isInnBusinessAppPage,
    },
  };
});
