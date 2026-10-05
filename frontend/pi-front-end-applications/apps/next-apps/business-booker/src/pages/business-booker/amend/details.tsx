import { useQueryClient } from '@tanstack/react-query';
import type {
  AmendConfInput,
  InnBusinessServerSideProps,
  PackagesCriteria,
} from '@whitbread-eos/api';
import { Area, BUSINESS_BOOKER_USER_ROLES, FT_PI_REDIS_RQ_CACHE } from '@whitbread-eos/api';
import { Container, ErrorBoundary } from '@whitbread-eos/atoms';
import {
  useUserData,
  useUserDetails,
  getServerSideCustomLocale,
  getI18nLabels,
  ID_TOKEN_COOKIE,
  getUnleashToggles,
  useFeatureToggle,
} from '@whitbread-eos/utils';
import {
  getInnBusinessServerSideProps,
  isInnBusinessApp,
  getPersistentQueryClient,
} from '@whitbread-eos/utils/server';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';
import { useRouter } from 'next/router';
import { ReactElement } from 'react';

import { DefaultLayout } from '~components/index';
import InnBusinessLayout from '~components/innBusiness/InnBusinessLayout';
import { Page as AmendPageBb, createAmendBbDataLoaderFn } from '~page-helper/amend/details';
import { PAGE } from '~utils/bb-all-pages-constants';
import { checkIsGuestUser } from '~utils/checkIsGuestUser';
import withPiTokenRedirect from '~utils/withPiTokenRedirect';

import { innBusinessLoginRedirect } from '../search';

interface Props {
  confirmationInput: AmendConfInput;
  pcksQueryInput: PackagesCriteria;
  isGuestUser: boolean;
  featureToggles: { [key: string]: boolean };
  innBusiness?: InnBusinessServerSideProps;
  isInnBusinessAppPage?: boolean;
  tempBookingReference?: string | null;
  status?: string | null;
}

export default function AmendPage({
  confirmationInput,
  pcksQueryInput,
  isGuestUser,
  featureToggles,
  innBusiness,
  tempBookingReference,
  status,
}: Props) {
  const router = useRouter();
  const queryClient = useQueryClient();
  useFeatureToggle(featureToggles);

  const { isLoggedIn } = useUserData();
  const userData = useUserDetails(true, isLoggedIn, innBusiness?.userDetails);

  if ((userData as any)?.business?.accessLevel === BUSINESS_BOOKER_USER_ROLES.STAYER) {
    router.push(`/${confirmationInput.country}/${confirmationInput.language}/business-booker`);
  }

  return (
    !isGuestUser && (
      <AmendPageBb
        variant={Area.BB}
        {...{ confirmationInput, pcksQueryInput, queryClient }}
        userDetails={innBusiness?.userDetails}
        tempBookingReference={tempBookingReference}
        status={status}
      />
    )
  );
}

AmendPage.getLayout = function getLayout(page: ReactElement<any>) {
  const containerStyles = { px: { base: 0, lg: '1.75rem', xl: '4.125rem' } };
  const secureUrl = process?.env?.NEXT_PUBLIC_SECURE2_URL ?? '';

  if (page.props.isInnBusinessAppPage) {
    return (
      <InnBusinessLayout
        serverSideProps={page.props.innBusiness}
        showSidebar={false}
        isBusinessBookerPage
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
  const cookies = new Cookies(props.req, props.res);
  const idTokenCookie = cookies.get(ID_TOKEN_COOKIE);
  const { logger } = await import('@whitbread-eos/utils');
  const { language, country } = getServerSideCustomLocale(locale);

  const isGuestUser = await checkIsGuestUser(props);

  if (isGuestUser) {
    return {
      redirect: {
        destination: `/${country}/${language}/business-booker`,
        permanent: false,
      },
    };
  }

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

  let innBusiness: InnBusinessServerSideProps | null = null;
  if (isInnBusinessAppPage) {
    innBusiness = await getInnBusinessServerSideProps(
      idTokenCookie,
      language,
      false,
      props.req.headers
    );
  }

  const labels = await getI18nLabels({
    language,
    queryClient,
  });

  const loadedData = await createAmendBbDataLoaderFn({
    logger: logger,
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
      isGuestUser,
      innBusiness,
      isInnBusinessAppPage,
    },
  };
});
