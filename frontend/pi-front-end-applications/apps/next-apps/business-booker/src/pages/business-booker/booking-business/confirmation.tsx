import {
  type QueryHotelInformationArgs,
  type PackagesCriteria,
  type PromotionQueryInput,
  type StaticContentQueryInput,
  InnBusinessServerSideProps,
  PageName,
  UnleashChannel,
  FT_PI_REDIS_RQ_CACHE,
} from '@whitbread-eos/api';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import { BusinessStepType } from '@whitbread-eos/layout';
import {
  getServerSideCustomLocale,
  getI18nLabels,
  getUnleashToggles,
  GLOBALS,
  ID_TOKEN_COOKIE,
  isInnBusinessApp,
} from '@whitbread-eos/utils';
import { getPersistentQueryClient } from '@whitbread-eos/utils/server';
import Cookies from 'cookies';
import type { GetServerSidePropsContext } from 'next';
import { useRouter } from 'next/router';
import { ReactElement } from 'react';

import { ConfirmationLayout } from '~components';
import InnBusinessLayout from '~components/innBusiness/InnBusinessLayout';
import ConfirmationPageBb, { createConfirmationBbDataLoaderFn } from '~page-helper/confirmation';
import { PAGE } from '~utils/bb-all-pages-constants';
import withPiTokenRedirect from '~utils/withPiTokenRedirect';

import { innBusinessLoginRedirect } from '../search';

interface Props {
  pcksQueryInput: PackagesCriteria;
  hiQueryInput: QueryHotelInformationArgs;
  staticContentQueryInput: StaticContentQueryInput;
  basketReference: string | null;
  promotionQueryInput: PromotionQueryInput;
  featureToggles: { [key: string]: boolean };
  innBusiness?: InnBusinessServerSideProps;
  isInnBusinessAppPage?: boolean;
}

export default function ConfirmationPage({
  pcksQueryInput,
  hiQueryInput,
  staticContentQueryInput,
  basketReference,
  promotionQueryInput,
}: Readonly<Props>) {
  const router = useRouter();

  return (
    <ConfirmationPageBb
      router={router}
      pcksQueryInput={pcksQueryInput}
      hiQueryInput={hiQueryInput}
      staticContentQueryInput={staticContentQueryInput}
      basketReference={basketReference}
      promotionQueryInput={promotionQueryInput}
    />
  );
}

ConfirmationPage.getLayout = function getLayout(page: ReactElement<any>) {
  const secureUrl = process?.env?.NEXT_PUBLIC_SECURE2_URL ?? '';
  if (page.props.isInnBusinessAppPage) {
    return (
      <InnBusinessLayout
        serverSideProps={page.props.innBusiness}
        showSidebar={false}
        isBusinessBookerPage
        businessStepType={BusinessStepType.BOOKING_CONFIRMATION_PAGE}
        featureToggle={page.props.featureToggles}
        secureUrl={secureUrl}
      >
        <ErrorBoundary>{page}</ErrorBoundary>
      </InnBusinessLayout>
    );
  }

  return (
    <ConfirmationLayout>
      <ErrorBoundary>{page}</ErrorBoundary>
    </ConfirmationLayout>
  );
};

export const getServerSideProps = withPiTokenRedirect(async function getServerSideProps({
  locale = 'gb',
  ...props
}: GetServerSidePropsContext) {
  const cookies = new Cookies(props.req, props.res);
  const idTokenCookie = cookies.get(ID_TOKEN_COOKIE);
  const { language, country } = getServerSideCustomLocale(locale);

  const featureToggles = await getUnleashToggles(
    props,
    PAGE.CONFIRMATION.featureToggles.appPage,
    PAGE.CONFIRMATION.featureToggles.flagsWithFallback,
    {
      country: country || GLOBALS.locale.GB,
      channel: UnleashChannel.PIB,
      pageName: PageName.CONFIRMATION.toUpperCase(),
    }
  );

  const queryClient = getPersistentQueryClient({
    page: 'BB | PMTP | Confirmation Page',
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

  const loadedData = await createConfirmationBbDataLoaderFn({
    queryClient: queryClient,
    language: language,
    country: country,
    isInnBusinessAppPage: isInnBusinessAppPage,
    ...props,
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
