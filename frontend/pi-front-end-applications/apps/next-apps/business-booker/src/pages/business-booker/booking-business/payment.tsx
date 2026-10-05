import {
  Area,
  FT_PI_BB_NON_GUARANTEED_REMINDER,
  FT_PI_REDIS_RQ_CACHE,
  PageName,
  UnleashChannel,
} from '@whitbread-eos/api';
import type {
  QueryHotelInformationArgs,
  PackagesCriteria,
  InnBusinessServerSideProps,
} from '@whitbread-eos/api';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import { BusinessStepType } from '@whitbread-eos/layout';
import {
  getServerSideCustomLocale,
  getI18nLabels,
  getUnleashToggles,
  useFeatureToggle,
  isSecureBookingPage,
  type secureBookingType,
  isValidSecureBooking,
  ID_TOKEN_COOKIE,
  isInnBusinessApp,
} from '@whitbread-eos/utils';
import { getPersistentQueryClient } from '@whitbread-eos/utils/server';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';
import { ReactElement } from 'react';

import { PaymentLayout } from '~components';
import InnBusinessLayout from '~components/innBusiness/InnBusinessLayout';
import { createPaymentBbDataLoaderFn, PaymentPageBb } from '~page-helper/payment';
import { PAGE } from '~utils/bb-all-pages-constants';
import withPiTokenRedirect from '~utils/withPiTokenRedirect';

import { innBusinessLoginRedirect } from '../search';

interface Props {
  pcksQueryInput: PackagesCriteria;
  hiQueryInput: QueryHotelInformationArgs;
  basketReference: string | null;
  featureToggles: DynamicObject;
  innBusiness?: InnBusinessServerSideProps;
  isInnBusinessAppPage?: boolean;
}

interface DynamicObject {
  [key: string]: boolean;
}

export default function PaymentPage(props: Readonly<Props>) {
  useFeatureToggle(props.featureToggles);

  return (
    <PaymentPageBb
      {...props}
      userDetails={props?.innBusiness?.userDetails}
      companyDetails={props?.innBusiness?.companyDetails}
    />
  );
}

PaymentPage.getLayout = function getLayout(page: ReactElement<any>) {
  const secureUrl = process?.env?.NEXT_PUBLIC_SECURE2_URL ?? '';
  if (page.props.isInnBusinessAppPage) {
    return (
      <InnBusinessLayout
        serverSideProps={page.props.innBusiness}
        showSidebar={false}
        isBusinessBookerPage
        businessStepType={BusinessStepType.PAYMENT_PAGE}
        featureToggle={page.props.featureToggles}
        secureUrl={secureUrl}
      >
        <ErrorBoundary>{page}</ErrorBoundary>
      </InnBusinessLayout>
    );
  }

  return (
    <PaymentLayout>
      <ErrorBoundary>{page}</ErrorBoundary>
    </PaymentLayout>
  );
};

export const getServerSideProps = withPiTokenRedirect(async function getServerSideProps({
  locale = 'gb',
  ...props
}: GetServerSidePropsContext) {
  const cookies = new Cookies(props.req, props.res);
  const idTokenCookie = cookies.get(ID_TOKEN_COOKIE);
  const { language, country } = getServerSideCustomLocale(locale);

  const featureToggles: DynamicObject = await getUnleashToggles(
    props,
    PAGE.PAYMENT.featureToggles.appPage,
    PAGE.PAYMENT.featureToggles.flagsWithFallback,
    {
      basketReference: props.query?.reservationId as string,
      channel: UnleashChannel.PIB,
      pageName: PageName.PAYMENT.toUpperCase(),
    }
  );

  const queryClient = getPersistentQueryClient({
    page: PAGE.PAYMENT.featureToggles.appPage,
    enabled: Boolean(featureToggles[FT_PI_REDIS_RQ_CACHE]),
  });

  const isInnBusinessAppPage = !!isInnBusinessApp(props?.req?.headers?.host ?? '');
  const shouldRedirectToInnBusinessLogin = !idTokenCookie && isInnBusinessAppPage;
  if (shouldRedirectToInnBusinessLogin) {
    return innBusinessLoginRedirect(locale);
  }

  const labels = await getI18nLabels({
    language,
    queryClient,
  });

  const loadedData = await createPaymentBbDataLoaderFn({
    queryClient: queryClient,
    language: language,
    country: country,
    isInnBusinessAppPage: isInnBusinessAppPage,
    ...props,
  });

  const isSecureBookingFeatureEnabled = featureToggles[FT_PI_BB_NON_GUARANTEED_REMINDER];

  // check if the reservation is valid or not in case of secure booking flow. if not valid, show error message
  if (isSecureBookingPage(props?.query as secureBookingType, isSecureBookingFeatureEnabled)) {
    const isValidReservation = await isValidSecureBooking(featureToggles, loadedData, Area.BB);

    // if reservation is not valid, show error message
    if (isValidReservation?.error) {
      return { notFound: true };
    }
  }

  return {
    props: {
      ...loadedData,
      ...labels,
      featureToggles,
      isInnBusinessAppPage,
    },
  };
});
